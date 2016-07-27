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

package org.mousephenotype.cda.loads.dataimporter;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.StatusCode;
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
 * specimen files currently found at /nfs/komp2/web/phenotype_data/impc. This class is meant to be an executable jar
 * whose arguments describe the source file, target database name and authentication, and spring context file.
 */
public class ImportSpecimens {
    /**
     * Load specimen data that was encoded using the IMPC XML format
     */

    // Required by the Harwell DCC export utilities
    public static final String CONTEXT_PATH = "org.mousephenotype.dcc.exportlibrary.datastructure.core.common:org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure:org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.submission:org.mousephenotype.dcc.exportlibrary.datastructure.tracker.validation";
    private static final Logger logger = LoggerFactory.getLogger(ImportSpecimens.class);

    private String filename;
    private String username = "";
    private String password = "";
    private String dbName;
    private boolean truncate = false;

    private Connection connection;

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, XMLloadingException, KeyManagementException, SQLException, JAXBException, DataImporterException {

        ImportSpecimens main = new ImportSpecimens();
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

        // parameter to indicate the whether or not to truncate the tables first.
        parser.accepts("truncate").withRequiredArg().ofType(String.class);

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
        truncate = false;
        if ( ! options.valuesOf("truncate").isEmpty()) {
            truncate = (options.valuesOf("truncate").get(0).toString().toLowerCase().equals("true") ? true : false);
        }
        logger.info("Loading specimen file {}", filename);
    }

    private void run() throws JAXBException, XMLloadingException, IOException, SQLException, KeyManagementException, NoSuchAlgorithmException, DataImporterException {
        List<CentreSpecimen> centerSpecimens = XMLUtils.unmarshal(ImportSpecimens.CONTEXT_PATH, CentreSpecimenSet.class, filename).getCentre();

        if (centerSpecimens.size() == 0) {
            logger.error("{} failed to unmarshall", filename);
            throw new XMLloadingException(filename + " failed to unmarshall.");
        }

        logger.debug("There are {} center specimen sets in specimen file {}", centerSpecimens.size(), filename);

        PreparedStatement ps;
        String query;
        for (CentreSpecimen centerSpecimen : centerSpecimens) {
            logger.debug("Parsing specimens for center {}", centerSpecimen.getCentreID());

            long centerPk = 0L;
            for (Specimen specimen : centerSpecimen.getMouseOrEmbryo()) {

                connection.setAutoCommit(false);    // BEGIN TRANSACTION

                Long statuscodePk, specimenPk;
                ResultSet rs;

                // center
                centerPk = DataImporterUtils.getCenterPk(connection, centerSpecimen.getCentreID().value(), specimen.getPipeline(), specimen.getProject());
                if (centerPk < 1) {
                    centerPk = DataImporterUtils.insertIntoCenter(connection, centerSpecimen.getCentreID().value(), specimen.getPipeline(), specimen.getProject());
                }

                // statuscode
                if (specimen.getStatusCode() != null) {
                    StatusCode existingStatuscode = DataImporterUtils.selectOrInsertStatuscode(connection,
                                                                                               specimen.getStatusCode().getValue(), specimen.getStatusCode().getDate());
                    statuscodePk = existingStatuscode.getHjid();
                } else {
                    statuscodePk = null;
                }

                // specimen
                Specimen existingSpecimen = DataImporterUtils.getSpecimen(connection, specimen.getSpecimenID(), centerSpecimen.getCentreID().value());
                if (existingSpecimen != null) {
                    specimenPk = existingSpecimen.getHjid();
                    // Validate that this specimen's info matches the existing one in the database.
                    if ( ! specimen.getPipeline().equals(existingSpecimen.getPipeline())) {
                        throw new DataImporterException("pipeline mismatch (pk " + specimenPk + "). Existing pipeline: '" + specimen.getPipeline() + "'. This pipeline: '" + existingSpecimen.getPipeline() + "'.");
                    }
                    if ( ! specimen.getGender().value().equals(existingSpecimen.getGender().value())) {
                        throw new DataImporterException("project mismatch (pk " + specimenPk + "). Existing project: '" + specimen.getProject() + "'. This project: '" + existingSpecimen.getProject() + "'.");
                    }
                    if ( ! specimen.getGender().value().equals(existingSpecimen.getGender().value())) {
                        throw new DataImporterException("gender mismatch (pk " + specimenPk + "). Existing gender: '" + specimen.getGender().value() + "'. This gender: '" + existingSpecimen.getGender().value() + "'.");
                    }
                    if ( specimen.isIsBaseline() != existingSpecimen.isIsBaseline()) {
                        throw new DataImporterException("isBaseline mismatch (pk " + specimenPk + "). Existing isBaseline: '" + (specimen.isIsBaseline() ? 1 : 0) + "'. This isBaseline: '" + existingSpecimen.isIsBaseline() + "'.");
                    }
                    if ( ! specimen.getLitterId().equals(existingSpecimen.getLitterId())) {
                        throw new DataImporterException("litterId mismatch. (pk " + specimenPk + "). Existing litterId: '" + specimen.getLitterId() + "'. This litterId: '" + existingSpecimen.getLitterId() + "'.");
                    }
                    if ( ! specimen.getPhenotypingCentre().value().equals(existingSpecimen.getPhenotypingCentre().value())) {
                        throw new DataImporterException("phenotypingCenter mismatch. (pk " + specimenPk + "). Existing phenotypingCenter: '" + specimen.getPhenotypingCentre().value()
                                + "'. This phenotypingCenter: '" + existingSpecimen.getPhenotypingCentre() + "'.");
                    }
                    if (specimen.getProductionCentre() == null) {
                        if (existingSpecimen.getProductionCentre() != null) {
                            throw new DataImporterException("productionCenter mismatch. (pk " + specimenPk + "). Existing productionCenter is null. this productionCenter: '" + existingSpecimen.getProductionCentre());
                        }
                    } else {
                        if ( ! specimen.getProductionCentre().value().equals(existingSpecimen.getProductionCentre().value())) {
                            throw new DataImporterException("productionCenter mismatch. (pk " + specimenPk + "). Existing productionCenter: '" + specimen.getProductionCentre().value()
                                    + "'. This productionCenter: '" + existingSpecimen.getProductionCentre().value() + "'.");
                        }
                    }
                    if ( ! specimen.getStrainID().equals(existingSpecimen.getStrainID())) {
                        throw new DataImporterException("strainId mismatch. (pk " + specimenPk + "). Existing strainId: '" + specimen.getStrainID() + "'. This strainId: '" + existingSpecimen.getStrainID() + "'.");
                    }
                    if ( ! specimen.getZygosity().value().equals(existingSpecimen.getZygosity().value())) {
                        throw new DataImporterException("zygosity mismatch. (pk " + specimenPk + "). Existing zygosity: '" + specimen.getZygosity().value() + "'. This zygosity: '" + existingSpecimen.getZygosity().value() + "'.");
                    }
                } else {
                    query = "INSERT INTO specimen (" +
                                "colonyId, gender, isBaseline, litterId, phenotypingCenter, pipeline, productionCenter, project," +
                                " specimenId, strainId, zygosity, statuscode_pk)" +
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
                    query = "INSERT INTO embryo (stage, stageUnit, specimen_pk) VALUES (?, ?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setString(1, embryo.getStage());
                    ps.setString(2, embryo.getStageUnit().value());
                    ps.setLong(3, specimenPk);
                } else  if (specimen instanceof Mouse) {
                    Mouse mouse = (Mouse) specimen;
                    query = "INSERT INTO mouse (DOB, specimen_pk) VALUES (?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setDate(1, new java.sql.Date(mouse.getDOB().getTime().getTime()));
                    ps.setLong(2, specimenPk);
                } else {
                    throw new DataImporterException("Unknown specimen type '" + specimen.getClass().getSimpleName());
                }
                ps.execute();

                // genotype
                for (Genotype genotype : specimen.getGenotype()) {
                    query = "INSERT INTO genotype (geneSymbol, mgiAlleleId, mgiGeneId, fatherZygosity, motherZygosity, specimen_pk)"
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
                    query = "INSERT INTO parentalStrain (percentage, mgiStrainId, gender, level, specimen_pk) VALUES (?, ?, ?, ?, ?);";
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
                    throw new DataImporterException("chromosomalAlteration is not yet supported. Records found!");
                }

                // center_specimen
                query = "INSERT INTO center_specimen (center_pk, specimen_pk) VALUES ( ?, ?);";
                ps = connection.prepareStatement(query);
                ps.setLong(1, centerPk);
                ps.setLong(2, specimenPk);
                try {
                    ps.execute();
                } catch (SQLException e) {
                    // Duplicate specimen
                    System.out.println("DUPLICATE SPECIMEN: " + DataImporterUtils.dumpSpecimen(connection, centerPk, specimenPk));
                    connection.rollback();
                    continue;
                }

                // relatedSpecimen NOTE: 'specimen_mine_pk cannot be loaded until ALL of the specimen files have been loaded,
                // as the related specimens are not guaranteed to be defined in the same specimen file (and, in fact, are not).
                for (RelatedSpecimen relatedSpecimen : specimen.getRelatedSpecimen()) {
                    query = "INSERT INTO relatedSpecimen (relationship, specimenIdMine, specimen_theirs_pk) VALUES ( ?, ?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setString(1, relatedSpecimen.getRelationship().value());
                    ps.setString(2, relatedSpecimen.getSpecimenID());
                    ps.setLong(3, specimenPk);
                    ps.execute();
                }

                connection.commit();
            }
        }

        // Update the relatedSpecimen.specimen_mine_pk column.
        query = "UPDATE relatedSpecimen SET specimen_mine_pk = (SELECT pk FROM specimen WHERE relatedSpecimen.specimenIdMine = specimen.specimenId)";
        ps = connection.prepareStatement(query);
        ps.execute();
        connection.commit();

        connection.close();
    }
}