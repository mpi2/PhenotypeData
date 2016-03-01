/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.sanitycheck;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrelac on 09/02/2016.
 * <p/>
 * This class encapsulates the code and data necessary to load the specified target database with the source dcc
 * specimen files currently found at /nfs/komp2/web/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the source file, target database name and authentication, and spring context file.
 */
public class SpecimenLoader {
    /**
     * Load specimen data that was encoded using the IMPC XML format
     */

    // Required by the Harwell DCC export utilities
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
    private static final Logger logger = LoggerFactory.getLogger(SpecimenLoader.class);

    private String filename;
    private String username = "";
    private String password = "";
    private String dbName;

    private Connection connection;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, XMLloadingException, KeyManagementException, SQLException, JAXBException, DccLoaderException {

        SpecimenLoader main = new SpecimenLoader();
        main.initialize(args);
        main.run();

        logger.debug("Process finished.  Exiting.");
    }

    private void initialize(String[] args)
            throws IOException, SQLException, KeyManagementException, NoSuchAlgorithmException {

        OptionParser parser = new OptionParser();

        // parameter to indicate the database name
        parser.accepts("dbname").withRequiredArg().ofType(String.class);

        // parameter to indicate the name of the file to process
        parser.accepts("filename").withRequiredArg().ofType(String.class);

        // parameter to indicate the database username
        parser.accepts("username").withRequiredArg().ofType(String.class);

        // parameter to indicate the database password
        parser.accepts("password").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        /* Uncomment if you want to use spring. */
//        // Wire up spring support for this application
//        ApplicationContext applicationContext;
//        String context = (String) options.valuesOf("context").get(0);
//        logger.debug("Using application context file {}", context);
//        applicationContext = loadApplicationContext((String)options.valuesOf("context").get(0));
//        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(this, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);

        dbName = (String) options.valuesOf("dbname").get(0);
        String dbUrl = "jdbc:mysql://mysql-mi-dev:4356/"
                + dbName
                + "?autoReconnect=true&amp;useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterEncoding=utf8&amp;characterSetResults=utf8&amp;zeroDateTimeBehavior=convertToNull";

        username = (String) options.valuesOf("username").get(0);
        password = (String) options.valuesOf("password").get(0);
        connection = DriverManager.getConnection(dbUrl, username, password);

        filename = (String) options.valuesOf("filename").get(0);
        logger.info("Loading specimen file {}", filename);
    }

    private void run() throws JAXBException, XMLloadingException, IOException, SQLException, KeyManagementException, NoSuchAlgorithmException, DccLoaderException {
        List<CentreSpecimen> centerSpecimens = XMLUtils.unmarshal(SpecimenLoader.CONTEXT_PATH, CentreSpecimenSet.class, filename).getCentre();

        if (centerSpecimens.size() == 0) {
            logger.error("{} failed to unmarshall", filename);
            throw new XMLloadingException(filename + " failed to unmarshall.");
        }

        logger.debug("There are {} center specimen sets in specimen file {}", centerSpecimens.size(), filename);

        PreparedStatement ps;
        String query;
        for (CentreSpecimen centerSpecimen : centerSpecimens) {
            logger.debug("Parsing specimens for center {}", centerSpecimen.getCentreID());

            Long centerPk = 0L;
            for (Specimen specimen : centerSpecimen.getMouseOrEmbryo()) {

                connection.setAutoCommit(false);    // BEGIN TRANSACTION

                Long statuscodePk, specimenPk;
                ResultSet rs;

                // center
                query = "";
                ps = connection.prepareStatement("SELECT * FROM center WHERE centerId = ? AND pipeline = ? AND project = ?;");
                ps.setString(1, centerSpecimen.getCentreID().value());
                ps.setString(2, specimen.getPipeline());
                ps.setString(3, specimen.getProject());
                rs = ps.executeQuery();
                if (rs.next()) {
                    centerPk = rs.getLong("pk");
                } else {
                    query = "INSERT INTO center (centerId, pipeline, project) VALUES (?, ?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setString(1, centerSpecimen.getCentreID().value());
                    ps.setString(2, specimen.getPipeline());
                    ps.setString(3, specimen.getProject());
                    ps.execute();
                    rs = ps.executeQuery("SELECT LAST_INSERT_ID();");
                    rs.next();
                    centerPk = rs.getLong(1);
                }

                // statuscode
                if (specimen.getStatusCode() != null) {
                    query = "INSERT INTO statuscode (dateOfStatuscode, value) VALUES ( ?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setDate(1, new java.sql.Date(specimen.getStatusCode().getDate().getTime().getTime()));
                    ps.setString(2, specimen.getStatusCode().getValue());
                    ps.execute();
                    rs = ps.executeQuery("SELECT LAST_INSERT_ID();");
                    rs.next();
                    statuscodePk = rs.getLong(1);
                } else {
                    statuscodePk = null;
                }

                // specimen
                query =
                          "SELECT *\n"
                        + "FROM specimen s\n"
                        + "JOIN center_specimen cs ON cs.specimen_fk =  s.pk\n"
                        + "JOIN center           c ON  c.pk          = cs.center_fk\n"
                        + "WHERE s.specimenId = ? AND c.centerId = ? AND c.pipeline = ? AND c.project = ?;";

                ps = connection.prepareStatement(query);
                ps.setString(1, specimen.getSpecimenID());
                ps.setString(2, centerSpecimen.getCentreID().value());
                ps.setString(3, specimen.getPipeline());
                ps.setString(4, specimen.getProject());
                rs = ps.executeQuery();
                if (rs.next()) {
                    specimenPk = rs.getLong("s.pk");
                    // Validate that this specimen's info matches the existing one in the database.
                    if ( ! specimen.getGender().value().equals(rs.getString("gender"))) {
                        throw new DccLoaderException("gender mismatch (pk " + specimenPk + "). Existing gender: " + specimen.getGender().value() + ". This gender: '" + rs.getString("gender") + "'.");
                    }
                    if ( ! specimen.isIsBaseline() == (rs.getInt("isBaseline") == 1)) {
                        throw new DccLoaderException("isBaseline mismatch (pk " + specimenPk + "). Existing isBaseline: " + (specimen.isIsBaseline() ? 1 : 0) + ". This isBaseline: '" + rs.getInt("isBaseline") + "'.");
                    }
                    if ( ! specimen.getLitterId().equals(rs.getString("litterId"))) {
                        throw new DccLoaderException("litterId mismatch. (pk " + specimenPk + "). Existing gender: " + specimen.getLitterId() + ". This litterId: '" + rs.getString("litterId") + "'.");
                    }
                    if ( ! specimen.getPhenotypingCentre().value().equals(rs.getString("phenotypingCenter"))) {
                        throw new DccLoaderException("phenotypingCenter mismatch. (pk " + specimenPk + "). Existing phenotypingCenter: " + specimen.getPhenotypingCentre().value()
                                + ". This phenotypingCenter: '" + rs.getString("phenotypingCenter") + "'.");
                    }
                    if ( ! specimen.getPipeline().equals(rs.getString("pipeline"))) {
                        throw new DccLoaderException("pipeline mismatch. (pk " + specimenPk + "). Existing pipeline: " + specimen.getPipeline()  + ". This pipeline: '" + rs.getString("pipeline") + "'.");
                    }
                    if (specimen.getProductionCentre() == null) {
                        if (rs.getString("productionCenter") != null) {
                            throw new DccLoaderException("productionCenter mismatch. (pk " + specimenPk + "). Existing productionCenter is null. this productionCenter: '" + rs.getString("productionCenter)"));
                        }
                    } else {
                        if ( ! specimen.getProductionCentre().value().equals(rs.getString("productionCenter"))) {
                            throw new DccLoaderException("productionCenter mismatch. (pk " + specimenPk + "). Existing productionCenter: " + specimen.getProductionCentre().value()
                                    + ". This productionCenter: '" + rs.getString("productionCenter") + "'.");
                        }
                    }
                    if ( ! specimen.getProject().equals(rs.getString("project"))) {
                        throw new DccLoaderException("project mismatch. (pk " + specimenPk + "). Existing project: " + specimen.getProject() + ". This project: '" + rs.getString("project") + "'.");
                    }
                    if ( ! specimen.getStrainID().equals(rs.getString("strainId"))) {
                        throw new DccLoaderException("strainId mismatch. (pk " + specimenPk + "). Existing strainId: " + specimen.getStrainID() + ". This strainId: '" + rs.getString("strainId") + "'.");
                    }
                    if ( ! specimen.getZygosity().value().equals(rs.getString("zygosity"))) {
                        throw new DccLoaderException("zygosity mismatch. (pk " + specimenPk + "). Existing zygosity: " + specimen.getZygosity().value() + ". This zygosity: '" + rs.getString("zygosity") + "'.");
                    }
                } else {
                    query = "INSERT INTO specimen (" +
                                "colonyId, gender, isBaseline, litterId, phenotypingCenter, pipeline, productionCenter, project," +
                                " specimenId, strainId, zygosity, statuscode_fk)" +
                            " VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                    ps = connection.prepareStatement(query);
                    if (specimen.getColonyID() == null) {
                        ps.setNull(1, Types.VARCHAR);
                    } else {
                        ps.setString(1, specimen.getColonyID());
                    }
                    ps.setString(2, specimen.getGender().value());
                    ps.setInt(3, specimen.isIsBaseline() ? 1 : 0);
                    ps.setString(4, specimen.getLitterId());
                    ps.setString(5, specimen.getPhenotypingCentre().value());
                    ps.setString(6, specimen.getPipeline());
                    if (specimen.getProductionCentre() == null) {
                        ps.setNull(7, Types.VARCHAR);
                    } else {
                        ps.setString(7, specimen.getProductionCentre().value());
                    }
                    ps.setString(8, specimen.getProject());
                    ps.setString(9, specimen.getSpecimenID());
                    if (specimen.getStrainID() == null) {
                        ps.setNull(10, Types.VARCHAR);
                    } else {
                        ps.setString(10, specimen.getStrainID());
                    }
                    ps.setString(11, specimen.getZygosity().value());
                    if (statuscodePk == null) {
                        ps.setNull(12, Types.BIGINT);
                    } else {
                        ps.setLong(12, statuscodePk);
                    }
                    ps.execute();
                    rs = ps.executeQuery("SELECT LAST_INSERT_ID();");
                    rs.next();
                    specimenPk = rs.getLong(1);
                }

                // embryo or mouse
                if (specimen instanceof Embryo) {
                    Embryo embryo = (Embryo) specimen;
                    query = "INSERT INTO embryo (stage, stageUnit, specimen_fk) VALUES (?, ?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setString(1, embryo.getStage());
                    ps.setString(2, embryo.getStageUnit().value());
                    ps.setLong(3, specimenPk);
                } else  if (specimen instanceof Mouse) {
                    Mouse mouse = (Mouse) specimen;
                    query = "INSERT INTO mouse (DOB, specimen_fk) VALUES (?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setDate(1, new java.sql.Date(mouse.getDOB().getTime().getTime()));
                    ps.setLong(2, specimenPk);
                } else {
                    throw new DccLoaderException("Unknown specimen type '" + specimen.getClass().getSimpleName());
                }
                ps.execute();

                // genotype
                for (Genotype genotype : specimen.getGenotype()) {
                    query = "INSERT INTO genotype (geneSymbol, mgiAlleleId, mgiGeneId, fatherZygosity, motherZygosity, specimen_fk)"
                          + " VALUES (?, ?, ?, ?, ?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setString(1, genotype.getGeneSymbol());
                    ps.setString(2, genotype.getMGIAlleleId());
                    ps.setString(3, genotype.getMGIGeneId());
                    if (genotype.getFatherZygosity() != null) {
                        ps.setString(4, genotype.getFatherZygosity().value());
                    } else {
                        ps.setNull(4, Types.VARCHAR);
                    }
                    if (genotype.getMotherZygosity() != null) {
                        ps.setString(5, genotype.getMotherZygosity().value());
                    } else {
                        ps.setNull(5, Types.VARCHAR);
                    }

                    ps.setLong(6, specimenPk);
                    ps.execute();
                }

                // parentalStrain
                for (ParentalStrain parentalStrain : specimen.getParentalStrain()) {
                    query = "INSERT INTO parentalStrain (percentage, mgiStrainId, gender, level, specimen_fk) VALUES (?, ?, ?, ?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setDouble(1, parentalStrain.getPercentage());
                    ps.setString(2, parentalStrain.getMGIStrainID());
                    ps.setString(3, parentalStrain.getGender().value());
                    ps.setInt(4, parentalStrain.getLevel());
                    ps.setLong(5, specimenPk);
                    ps.execute();
                }

                // chromosomalAlteration
                if ( ! specimen.getChromosomalAlteration().isEmpty()) {
                    throw new DccLoaderException("chromosomalAlteration is not yet supported. Records found!");
                }

                // center_specimen
                query = "INSERT INTO center_specimen (center_fk, specimen_fk) VALUES ( ?, ?);";
                ps = connection.prepareStatement(query);
                ps.setLong(1, centerPk);
                ps.setLong(2, specimenPk);
                try {
                    ps.execute();
                } catch (SQLException e) {
                    // Duplicate specimen
                    System.out.println("DUPLICATE SPECIMEN: " + dumpSpecimen(centerPk, specimenPk));
                    connection.rollback();
                    continue;
                }

                // relatedSpecimen NOTE: 'specimen_mine_fk cannot be loaded until ALL of the specimen files have been loaded,
                // as the related specimens are not guaranteed to be defined in the same specimen file (and, in fact, are not).
                for (RelatedSpecimen relatedSpecimen : specimen.getRelatedSpecimen()) {
                    query = "INSERT INTO relatedSpecimen (relationship, specimenIdMine, specimen_theirs_fk) VALUES ( ?, ?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setString(1, relatedSpecimen.getRelationship().value());
                    ps.setString(2, relatedSpecimen.getSpecimenID());
                    ps.setLong(3, specimenPk);
                    ps.execute();
                }

                connection.commit();
            }
        }

        connection.close();
    }

    protected ApplicationContext loadApplicationContext(String context) {
        ApplicationContext appContext;

        // Try context as a file resource.
        File file = new File(context);
        if (file.exists()) {
            // Try context as a file resource
            appContext = new FileSystemXmlApplicationContext("file:" + context);
        } else {
            // Try context as a class path resource
            appContext = new ClassPathXmlApplicationContext(context);
        }

        if (appContext == null) {
            logger.error("Unable to load context '" + context  + "' from file or classpath. Exiting...");
        }

        return appContext;
    }

//    private void truncateTables() throws SQLException {
//        String query;
//        PreparedStatement ps;
//
//        String[] tables = new String[] {
//                  "center"
//                , "center_specimen"
//                , "embryo"
//                , "genotype"
//                , "mouse"
//                , "parentalStrain"
//                , "relatedSpecimen"
//                , "specimen"
//                , "statuscode"
//        };
//
//        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
//        ps.execute();
//        for (String tableName : tables) {
//            query = "TRUNCATE " + tableName + ";";
//
//            try {
//                ps = connection.prepareStatement(query);
//                ps.execute();
//            } catch (SQLException e) {
//                logger.error("Unable to truncate table " + tableName);
//                throw e;
//            }
//        }
//        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1");
//        ps.execute();
//    }

    private String dumpSpecimen(long centerPk, long specimenPk) {
        String retVal = "";

        String query =
                "SELECT\n"
                    + "  cs.pk AS cs_pk\n"
                    + ", c.pk AS c_pk\n"
                    + ", s.pk AS s_pk\n"
                    + ", s.statuscode_fk AS s_statuscode_fk\n"
                    + ", c.centerId\n"
                    + ", c.pipeline\n"
                    + ", c.project\n"
                    + ", s.colonyId\n"
                    + ", s.gender\n"
                    + ", s.isBaseline\n"
                    + ", s.litterId\n"
                    + ", s.phenotypingCenter\n"
                    + ", s.pipeline\n"
                    + ", s.productionCenter\n"
                    + ", s.specimenId\n"
                    + ", s.strainId\n"
                    + ", s.zygosity\n"
                    + ", sc.dateOfStatuscode\n"
                    + ", sc.value\n"
                    + ", m.DOB\n"
                    + ", e.stage\n"
                    + ", e.stageUnit\n"
                    + "FROM center c\n"
                    + "JOIN center_specimen cs ON cs.center_fk = c.pk\n"
                    + "JOIN specimen s ON cs.specimen_fk = s.pk\n"
                    + "LEFT OUTER JOIN mouse m ON m.specimen_fk = cs.specimen_fk\n"
                    + "LEFT OUTER JOIN embryo e ON e.specimen_fk = cs.specimen_fk\n"
                    + "LEFT OUTER JOIN statuscode sc ON sc.pk = s.statuscode_fk\n"
                    + "WHERE c.pk = ? AND s.pk = ?;";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setLong(1, centerPk);
            ps.setLong(2, specimenPk);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                retVal += "{"
                        + "cs.pk=" + rs.getLong("cs_pk")
                        + ",c.pk=" + rs.getLong("c_pk")
                        + ",s.pk=" + rs.getLong("s_pk")
                        + ",s.statuscode_fk=" + (rs.getLong("s_statuscode_fk") == 0 ? "<null>" : rs.getLong("s_statuscode_fk"))
                        + ",centerId=" + rs.getString("c.centerId")
                        + ",pipeline=" + rs.getString("c.pipeline")
                        + ",project=" + rs.getString("c.project")
                        + ",colonyId=" + rs.getString("s.colonyId")
                        + ",gender=" + rs.getString("s.gender")
                        + ",isBaseline=" + rs.getInt("s.isBaseline")
                        + ",litterId=" + rs.getString("s.litterId")
                        + ",phenotypingCenter=" + rs.getString("s.phenotypingCenter")
                        + ",productionCenter=" + (rs.getString("s.productionCenter") == null ? "<null>" : rs.getString("s.productionCenter"))
                        + ",specimenId=" + rs.getString("s.specimenId")
                        + ",strainId=" + rs.getString("s.strainId")
                        + ",zygosity=" + rs.getString("s.zygosity");
                if (rs.getLong("s_statuscode_fk") != 0) {
                    retVal += ",sc.dateOfStatuscode=" + (rs.getDate("sc.dateOfStatuscode") == null ? "<null>" : rs.getDate("sc.dateOfStatuscode"))
                    + ",sc.value=" + rs.getString("sc.value");
                }
                retVal += (rs.getDate("m.DOB") == null ? " (EMBRYO)" : " (MOUSE)");
            }

        } catch (SQLException e) {
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
        }

        return retVal;
    }
}