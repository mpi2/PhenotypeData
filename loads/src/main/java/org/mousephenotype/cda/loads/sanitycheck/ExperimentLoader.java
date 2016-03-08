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
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.mousephenotype.dcc.exportlibrary.xmlserialization.exceptions.XMLloadingException;
import org.mousephenotype.dcc.utils.xml.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.List;

/**
 * Created by mrelac on 09/02/2016.
 * <p/>
 * This class encapsulates the code and data necessary to load the specified target database with the source dcc
 * experiment files currently found at /nfs/komp2/web/phenotype_data/impc. This class is meant to be an executable jar
  * whose arguments describe the source file, target database name and authentication, and spring context file.
 */
public class ExperimentLoader {
    /**
     * Load specimen data that was encoded using the IMPC XML format
     */

    // Required by the Harwell DCC export utilities
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
    private static final Logger logger = LoggerFactory.getLogger(ExperimentLoader.class);

    private String filename;
    private String username = "";
    private String password = "";
    private String dbName;
    private boolean truncate = false;

    private Connection connection;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, XMLloadingException, KeyManagementException, SQLException, JAXBException, DccLoaderException {

        ExperimentLoader main = new ExperimentLoader();
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

        // parameter to indicate the database password
        parser.accepts("truncate").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        dbName = (String) options.valuesOf("dbname").get(0);
        String dbUrl = "jdbc:mysql://mysql-mi-dev:4356/"
                + dbName
                + "?autoReconnect=true&amp;useUnicode=true&amp;connectionCollation=utf8_general_ci&amp;characterEncoding=utf8&amp;characterSetResults=utf8&amp;zeroDateTimeBehavior=convertToNull";

        username = (String) options.valuesOf("username").get(0);
        password = (String) options.valuesOf("password").get(0);
        connection = DriverManager.getConnection(dbUrl, username, password);

        filename = (String) options.valuesOf("filename").get(0);
        truncate = false;
        if ( ! options.valuesOf("truncate").isEmpty()) {
            truncate = (options.valuesOf("truncate").get(0).toString().toLowerCase().equals("true") ? true : false);
        }
        logger.info("Loading experiment file {}", filename);
    }

    private void run() throws JAXBException, XMLloadingException, IOException, SQLException, KeyManagementException, NoSuchAlgorithmException, DccLoaderException {
        List<CentreProcedure> centerProcedures = XMLUtils.unmarshal(ExperimentLoader.CONTEXT_PATH, CentreProcedureSet.class, filename).getCentre();

        if (centerProcedures.size() == 0) {
            logger.error("{} failed to unmarshall", filename);
            throw new XMLloadingException(filename + " failed to unmarshall.");
        }

        logger.debug("There are {} center procedure sets in experiment file {}", centerProcedures.size(), filename);

        long centerPk, centerProcedurePk, procedurePk, specimenPk, statuscodePk;
        PreparedStatement ps;
        ResultSet rs;
        String query;
        for (CentreProcedure centerProcedure : centerProcedures) {
            logger.debug("Parsing experiments for center {}", centerProcedure.getCentreID());

            if (truncate) {
                LoaderUtils.truncateExperimentTables(connection);
            }

            connection.setAutoCommit(false);    // BEGIN TRANSACTION

            // Get centerPk
            centerPk = LoaderUtils.getCenterPk(connection, centerProcedure.getCentreID().value(), centerProcedure.getPipeline(), centerProcedure.getProject());
            if (centerPk < 1) {
                System.out.println("UNKNOWN CENTER,PIPELINE,PROJECT: '" + centerProcedure.getCentreID().value() + ","
                        + centerProcedure.getPipeline() + "," + centerProcedure.getProject() + "'. INSERTING...");
                centerPk = LoaderUtils.insertIntoCenter(connection, centerProcedure.getCentreID().value(), centerProcedure.getPipeline(), centerProcedure.getProject());
            }

            for (Experiment experiment : centerProcedure.getExperiment()) {
                for (String specimenId : experiment.getSpecimenID()) {
                    // get specimenPk
                    Specimen specimen = LoaderUtils.getSpecimen(connection, specimenId, centerProcedure.getCentreID().value());
                    if (specimen == null) {
                        System.out.println("UNKNOWN SPECIMEN,CENTER: '" + specimenId + "," + centerProcedure.getCentreID().value() + "'. INSERTING...");
                        connection.rollback();
                        continue;
                    }
                    specimenPk = specimen.getHjid();

                    // procedure
                    ps = connection.prepareStatement("SELECT * FROM procedure_ WHERE procedureId = ?;");
                    ps.setString(1, experiment.getProcedure().getProcedureID());
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        procedurePk = rs.getLong("pk");
                    } else {
                        query = "INSERT INTO procedure_ (procedureId) VALUES (?);";
                        ps = connection.prepareStatement(query);
                        ps.setString(1, experiment.getProcedure().getProcedureID());
                        ps.execute();
                        rs = ps.executeQuery("SELECT LAST_INSERT_ID();");
                        rs.next();
                        procedurePk = rs.getLong(1);
                    }

                    // center_procedure
                    ps = connection.prepareStatement("SELECT * FROM center_procedure WHERE center_fk = ? and procedure_fk = ?");
                    ps.setLong(1, centerPk);
                    ps.setLong(2, procedurePk);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        centerProcedurePk = rs.getLong("pk");
                    } else {
                        query = "INSERT INTO center_procedure (center_fk, procedure_fk) VALUES (?, ?);";
                        ps = connection.prepareStatement(query);
                        ps.setLong(1, centerPk);
                        ps.setLong(2, procedurePk);
                        ps.execute();
                        rs = ps.executeQuery("SELECT LAST_INSERT_ID();");
                        rs.next();
                        centerProcedurePk = rs.getLong(1);
                    }

                    // housing
                    if ((centerProcedure.getHousing() != null) && ( ! centerProcedure.getHousing().isEmpty())) {
                        query = "INSERT INTO housing (fromLims, lastUpdated, center_procedure_fk) VALUES (?, ?, ?);";
                        ps = connection.prepareStatement(query);
                        for (Housing housing : centerProcedure.getHousing()) {
                            ps.setInt(1, (housing.isFromLIMS() ? 1 : 0));
                            ps.setDate(2, housing.getLastUpdated() == null ? null : new Date(housing.getLastUpdated().getTime().getTime()));
                            ps.setLong(3, centerProcedurePk);
                            ps.execute();
                        }
                    }

                    // line
                    if ((centerProcedure.getLine() != null) && ( ! centerProcedure.getLine().isEmpty())) {
                        for (Line line : centerProcedure.getLine()) {
                            ps = connection.prepareStatement("SELECT * FROM line WHERE colonyId = ? AND center_procedure_fk = ?");
                            ps.setString(1, line.getColonyID());
                            ps.setLong(2, centerProcedurePk);
                            rs = ps.executeQuery();
                            long linePk;
                            if (rs.next()) {
                                linePk = rs.getLong("pk");
                            } else {
                                query = "INSERT INTO line (colonyId, center_procedure_fk) VALUES (?, ?);";
                                ps = connection.prepareStatement(query);


                                ps.setString(1, line.getColonyID());
                                ps.setLong(2, centerProcedurePk);
                                ps.execute();
                                rs = ps.executeQuery("SELECT LAST_INSERT_ID();");
                                rs.next();
                                linePk = rs.getLong(1);
                            }

                            if ((line.getStatusCode() != null) && ( ! line.getStatusCode().isEmpty())) {
                                // line_statuscode
                                query = "INSERT INTO line_statuscode (line_fk, statuscode_fk) VALUES (?, ?);";
                                ps = connection.prepareStatement(query);
                                for (StatusCode statuscode : line.getStatusCode()) {
                                    StatusCode existingStatuscode = LoaderUtils.selectOrInsertStatuscode(connection, statuscode);
                                    statuscodePk = existingStatuscode.getHjid();
                                    ps.setLong(1, linePk);
                                    ps.setLong(2, statuscodePk);
                                    ps.execute();
                                }
                            }
                        }
                    }

                    // experiment


                    // experiment_statuscode



                    // experiment_specimen


                    // simpleParameter
                    // ontologyParameter
                    // seriesParameter
                    // mediaParameter
                    // ontologyParameterTerm
                    // seriesParameterValue
                    // mediaParameter_parameterAssociation
                    // mediaParameter_procedureMetadata
                    // parameterAssociation
                    // procedureMetadata
                    // dimension
                    // mediaSampleParameter
                    // mediaSample
                    // mediaSection
                    // mediaFile
                    // mediaFile_parameterAssociation
                    // mediaFile_procedureMetadata
                    // seriesMediaParameter
                    // procedure_procedureMetadata
                    // seriesParameterValue
                    // seriesMediaParameterValue_parameterAssociation
                    // seriesMediaParameterValue_procedureMetadata


                    connection.commit();

                }





//                // center
//                query = "";
//                ps = connection.prepareStatement("SELECT * FROM center WHERE centerId = ? AND pipeline = ? AND project = ?;");
//                ps.setString(1, centerProcedure.getCentreID().value());
//                ps.setString(2, experiment.getPipeline());
//                ps.setString(3, experiment.getProject());
//                rs = ps.executeQuery();
//                if (rs.next()) {
//                    centerPk = rs.getLong("pk");
//                } else {
//                    query = "INSERT INTO center (centerId, pipeline, project) VALUES (?, ?, ?);";
//                    ps = connection.prepareStatement(query);
//                    ps.setString(1, centerProcedure.getCentreID().value());
//                    ps.setString(2, experiment.getPipeline());
//                    ps.setString(3, experiment.getProject());
//                    ps.execute();
//                    rs = ps.executeQuery("SELECT LAST_INSERT_ID();");
//                    rs.next();
//                    centerPk = rs.getLong(1);
//                }
//
//                // statuscode
//                if (experiment.getStatusCode() != null) {
//                    query = "INSERT INTO statuscode (dateOfStatuscode, value) VALUES ( ?, ?);";
//                    ps = connection.prepareStatement(query);
//                    ps.setDate(1, new Date(experiment.getStatusCode().getDate().getTime().getTime()));
//                    ps.setString(2, experiment.getStatusCode().getValue());
//                    ps.execute();
//                    rs = ps.executeQuery("SELECT LAST_INSERT_ID();");
//                    rs.next();
//                    statuscodePk = rs.getLong(1);
//                } else {
//                    statuscodePk = null;
//                }
//
//                // specimen
//                query =
//                          "SELECT *\n"
//                        + "FROM specimen s\n"
//                        + "JOIN center_specimen cs ON cs.specimen_fk =  s.pk\n"
//                        + "JOIN center           c ON  c.pk          = cs.center_fk\n"
//                        + "WHERE s.specimenId = ? AND c.centerId = ? AND c.pipeline = ? AND c.project = ?;";
//
//                ps = connection.prepareStatement(query);
//                ps.setString(1, experiment.getSpecimenID());
//                ps.setString(2, centerProcedure.getCentreID().value());
//                ps.setString(3, experiment.getPipeline());
//                ps.setString(4, experiment.getProject());
//                rs = ps.executeQuery();
//                if (rs.next()) {
//                    specimenPk = rs.getLong("s.pk");
//                    // Validate that this specimen's info matches the existing one in the database.
//                    if ( ! experiment.getGender().value().equals(rs.getString("gender"))) {
//                        throw new DccLoaderException("gender mismatch (pk " + specimenPk + "). Existing gender: " + experiment.getGender().value() + ". This gender: '" + rs.getString("gender") + "'.");
//                    }
//                    if ( ! experiment.isIsBaseline() == (rs.getInt("isBaseline") == 1)) {
//                        throw new DccLoaderException("isBaseline mismatch (pk " + specimenPk + "). Existing isBaseline: " + (experiment.isIsBaseline() ? 1 : 0) + ". This isBaseline: '" + rs.getInt("isBaseline") + "'.");
//                    }
//                    if ( ! experiment.getLitterId().equals(rs.getString("litterId"))) {
//                        throw new DccLoaderException("litterId mismatch. (pk " + specimenPk + "). Existing gender: " + experiment.getLitterId() + ". This litterId: '" + rs.getString("litterId") + "'.");
//                    }
//                    if ( ! experiment.getPhenotypingCentre().value().equals(rs.getString("phenotypingCenter"))) {
//                        throw new DccLoaderException("phenotypingCenter mismatch. (pk " + specimenPk + "). Existing phenotypingCenter: " + experiment.getPhenotypingCentre().value()
//                                + ". This phenotypingCenter: '" + rs.getString("phenotypingCenter") + "'.");
//                    }
//                    if ( ! experiment.getPipeline().equals(rs.getString("pipeline"))) {
//                        throw new DccLoaderException("pipeline mismatch. (pk " + specimenPk + "). Existing pipeline: " + experiment.getPipeline()  + ". This pipeline: '" + rs.getString("pipeline") + "'.");
//                    }
//                    if (experiment.getProductionCentre() == null) {
//                        if (rs.getString("productionCenter") != null) {
//                            throw new DccLoaderException("productionCenter mismatch. (pk " + specimenPk + "). Existing productionCenter is null. this productionCenter: '" + rs.getString("productionCenter)"));
//                        }
//                    } else {
//                        if ( ! experiment.getProductionCentre().value().equals(rs.getString("productionCenter"))) {
//                            throw new DccLoaderException("productionCenter mismatch. (pk " + specimenPk + "). Existing productionCenter: " + experiment.getProductionCentre().value()
//                                    + ". This productionCenter: '" + rs.getString("productionCenter") + "'.");
//                        }
//                    }
//                    if ( ! experiment.getProject().equals(rs.getString("project"))) {
//                        throw new DccLoaderException("project mismatch. (pk " + specimenPk + "). Existing project: " + experiment.getProject() + ". This project: '" + rs.getString("project") + "'.");
//                    }
//                    if ( ! experiment.getStrainID().equals(rs.getString("strainId"))) {
//                        throw new DccLoaderException("strainId mismatch. (pk " + specimenPk + "). Existing strainId: " + experiment.getStrainID() + ". This strainId: '" + rs.getString("strainId") + "'.");
//                    }
//                    if ( ! experiment.getZygosity().value().equals(rs.getString("zygosity"))) {
//                        throw new DccLoaderException("zygosity mismatch. (pk " + specimenPk + "). Existing zygosity: " + experiment.getZygosity().value() + ". This zygosity: '" + rs.getString("zygosity") + "'.");
//                    }
//                } else {
//                    query = "INSERT INTO specimen (" +
//                                "colonyId, gender, isBaseline, litterId, phenotypingCenter, pipeline, productionCenter, project," +
//                                " specimenId, strainId, zygosity, statuscode_fk)" +
//                            " VALUES( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
//                    ps = connection.prepareStatement(query);
//                    if (experiment.getColonyID() == null) {
//                        ps.setNull(1, Types.VARCHAR);
//                    } else {
//                        ps.setString(1, experiment.getColonyID());
//                    }
//                    ps.setString(2, experiment.getGender().value());
//                    ps.setInt(3, experiment.isIsBaseline() ? 1 : 0);
//                    ps.setString(4, experiment.getLitterId());
//                    ps.setString(5, experiment.getPhenotypingCentre().value());
//                    ps.setString(6, experiment.getPipeline());
//                    if (experiment.getProductionCentre() == null) {
//                        ps.setNull(7, Types.VARCHAR);
//                    } else {
//                        ps.setString(7, experiment.getProductionCentre().value());
//                    }
//                    ps.setString(8, experiment.getProject());
//                    ps.setString(9, experiment.getSpecimenID());
//                    if (experiment.getStrainID() == null) {
//                        ps.setNull(10, Types.VARCHAR);
//                    } else {
//                        ps.setString(10, experiment.getStrainID());
//                    }
//                    ps.setString(11, experiment.getZygosity().value());
//                    if (statuscodePk == null) {
//                        ps.setNull(12, Types.BIGINT);
//                    } else {
//                        ps.setLong(12, statuscodePk);
//                    }
//                    ps.execute();
//                    rs = ps.executeQuery("SELECT LAST_INSERT_ID();");
//                    rs.next();
//                    specimenPk = rs.getLong(1);
//                }
//
//                // embryo or mouse
//                if (experiment instanceof Embryo) {
//                    Embryo embryo = (Embryo) experiment;
//                    query = "INSERT INTO embryo (stage, stageUnit, specimen_fk) VALUES (?, ?, ?);";
//                    ps = connection.prepareStatement(query);
//                    ps.setString(1, embryo.getStage());
//                    ps.setString(2, embryo.getStageUnit().value());
//                    ps.setLong(3, specimenPk);
//                } else  if (experiment instanceof Mouse) {
//                    Mouse mouse = (Mouse) experiment;
//                    query = "INSERT INTO mouse (DOB, specimen_fk) VALUES (?, ?);";
//                    ps = connection.prepareStatement(query);
//                    ps.setDate(1, new Date(mouse.getDOB().getTime().getTime()));
//                    ps.setLong(2, specimenPk);
//                } else {
//                    throw new DccLoaderException("Unknown specimen type '" + experiment.getClass().getSimpleName());
//                }
//                ps.execute();
//
//                // genotype
//                for (Genotype genotype : experiment.getGenotype()) {
//                    query = "INSERT INTO genotype (geneSymbol, mgiAlleleId, mgiGeneId, fatherZygosity, motherZygosity, specimen_fk)"
//                          + " VALUES (?, ?, ?, ?, ?, ?);";
//                    ps = connection.prepareStatement(query);
//                    ps.setString(1, genotype.getGeneSymbol());
//                    ps.setString(2, genotype.getMGIAlleleId());
//                    ps.setString(3, genotype.getMGIGeneId());
//                    if (genotype.getFatherZygosity() != null) {
//                        ps.setString(4, genotype.getFatherZygosity().value());
//                    } else {
//                        ps.setNull(4, Types.VARCHAR);
//                    }
//                    if (genotype.getMotherZygosity() != null) {
//                        ps.setString(5, genotype.getMotherZygosity().value());
//                    } else {
//                        ps.setNull(5, Types.VARCHAR);
//                    }
//
//                    ps.setLong(6, specimenPk);
//                    ps.execute();
//                }
//
//                // parentalStrain
//                for (ParentalStrain parentalStrain : experiment.getParentalStrain()) {
//                    query = "INSERT INTO parentalStrain (percentage, mgiStrainId, gender, level, specimen_fk) VALUES (?, ?, ?, ?, ?);";
//                    ps = connection.prepareStatement(query);
//                    ps.setDouble(1, parentalStrain.getPercentage());
//                    ps.setString(2, parentalStrain.getMGIStrainID());
//                    ps.setString(3, parentalStrain.getGender().value());
//                    ps.setInt(4, parentalStrain.getLevel());
//                    ps.setLong(5, specimenPk);
//                    ps.execute();
//                }
//
//                // chromosomalAlteration
//                if ( ! experiment.getChromosomalAlteration().isEmpty()) {
//                    throw new DccLoaderException("chromosomalAlteration is not yet supported. Records found!");
//                }
//
//                // center_specimen
//                query = "INSERT INTO center_specimen (center_fk, specimen_fk) VALUES ( ?, ?);";
//                ps = connection.prepareStatement(query);
//                ps.setLong(1, centerPk);
//                ps.setLong(2, specimenPk);
//                try {
//                    ps.execute();
//                } catch (SQLException e) {
//                    // Duplicate specimen
//                    System.out.println("DUPLICATE SPECIMEN: " + dumpSpecimen(centerPk, specimenPk));
//                    connection.rollback();
//                    continue;
//                }
//
//                // relatedSpecimen NOTE: 'specimen_mine_fk cannot be loaded until ALL of the specimen files have been loaded,
//                // as the related specimens are not guaranteed to be defined in the same specimen file (and, in fact, are not).
//                for (RelatedSpecimen relatedSpecimen : experiment.getRelatedSpecimen()) {
//                    query = "INSERT INTO relatedSpecimen (relationship, specimenIdMine, specimen_theirs_fk) VALUES ( ?, ?, ?);";
//                    ps = connection.prepareStatement(query);
//                    ps.setString(1, relatedSpecimen.getRelationship().value());
//                    ps.setString(2, relatedSpecimen.getSpecimenID());
//                    ps.setLong(3, specimenPk);
//                    ps.execute();
//                }
            }
        }

        connection.close();
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