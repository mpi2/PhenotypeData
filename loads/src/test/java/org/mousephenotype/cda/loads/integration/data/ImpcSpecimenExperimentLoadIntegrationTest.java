/*******************************************************************************
 * Copyright © 2017 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.integration.data;

import org.h2.tools.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.WeightMap;
import org.mousephenotype.cda.loads.create.extract.dcc.DccExperimentExtractor;
import org.mousephenotype.cda.loads.create.extract.dcc.DccSpecimenExtractor;
import org.mousephenotype.cda.loads.create.load.ExperimentLoader;
import org.mousephenotype.cda.loads.create.load.SampleLoader;
import org.mousephenotype.cda.loads.integration.data.config.TestConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

/**
 * This is an end-to-end integration data test class that uses an in-memory database to populate a small dcc, cda_base,
 * and cda set of databases.
 * The specimen and experiment tested here was missing from dcc_6_0 and dcc_6_1 but both were present in the live komp2 database.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan
@ContextConfiguration(classes = TestConfig.class)
public class ImpcSpecimenExperimentLoadIntegrationTest {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource cdaDataSource;
    
    @Autowired
    private DataSource dccDataSource;

    @Autowired
    private NamedParameterJdbcTemplate jdbcCda;



    @Autowired
    private DccSpecimenExtractor dccSpecimenExtractor;

    @Autowired
    private DccExperimentExtractor dccExperimentExtractor;

    @Autowired
    private SampleLoader sampleLoader;

    @Autowired
    private ExperimentLoader experimentLoader;



    // Set startServer to true to produce an in-memory h2 database browser.
    private static boolean startServer = false;
    private static Server server;

    private Thread thread;
    @Before
    public void before() throws SQLException {


        // Show browser if startServer is true.
        if (startServer) {
            startServer = false;
            Runnable runnable = () -> {

                try {
                    Server.startWebServer(dccDataSource.getConnection());

                    server = Server.createWebServer("-web");  // .start();
                    server.start();
                    System.out.println("URL: " + server.getURL());
                    System.out.println("Port: " + server.getPort());
                    Server.openBrowser(server.getURL());

                } catch (Exception e) {
                    System.out.println("Embedded h2 server failed to start: " + e.getLocalizedMessage());
                    System.exit(1);
                }
            };

            thread = new Thread(runnable);
            thread.start();
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
        }


        // Reload databases.
        String[] cdaSchemas = new String[] {
                "sql/h2/cda/schema.sql",
                "sql/h2/impress/impressSchema.sql"
        };
        String[] dccSchemas = new String[] {
                "sql/h2/dcc/createSpecimen.sql",
                "sql/h2/dcc/createExperiment.sql"
        };

        for (String schema : cdaSchemas) {
            logger.info("cda schema: " + schema);
            Resource r = context.getResource(schema);
            ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), r);
        }
        for (String schema : dccSchemas) {
            logger.info("dcc schema: " + schema);
            Resource r = context.getResource(schema);
            String s = dccDataSource.getConnection().getSchema();
            System.out.println("dcc schema = " + s);
            ScriptUtils.executeSqlScript(dccDataSource.getConnection(), r);
        }
    }




    @Test
    public void testLoadMissingSpecimenAndExperiment() throws Exception {

        Resource cdaResource        = context.getResource("classpath:sql/h2/LoadImpcSpecimenExperiment-data.sql");
        Resource specimenResource   = context.getResource("classpath:xml/ImpcSpecimenExperiment-specimens.xml");
        Resource experimentResource = context.getResource("classpath:xml/ImpcSpecimenExperiment-experiments.xml");

        ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), cdaResource);

        String[] extractSpecimenArgs = new String[]{
                "--datasourceShortName=IMPC",
                "--filename=" + specimenResource.getFile().getAbsolutePath()
        };

        String[] extractExperimentArgs = new String[]{
                "--datasourceShortName=IMPC",
                "--filename=" + experimentResource.getFile().getAbsolutePath()
        };

        String[] loadArgs = new String[] {
        };

        dccSpecimenExtractor.run(extractSpecimenArgs);
        dccExperimentExtractor.run(extractExperimentArgs);

        String specimenQuery = "SELECT COUNT(*) AS cnt FROM specimen";
        Integer specimenCount = 0;

        String experimentQuery = "SELECT COUNT(*) AS cnt FROM experiment";
        Integer experimentCount = 0;

        try (Connection connection = dccDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(specimenQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                specimenCount = resultSet.getInt("cnt");
            }
        }

        try (Connection connection = dccDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(experimentQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                experimentCount = resultSet.getInt("cnt");
            }
        }

        final int EXPECTED_SPECIMEN_COUNT = 1;
        final int EXPECTED_EXPERIMENT_COUNT = 1;
        assertTrue( "Expected " + EXPECTED_SPECIMEN_COUNT + " specimen(s). Found " + specimenCount, specimenCount == EXPECTED_SPECIMEN_COUNT);
        assertTrue( "Expected " + EXPECTED_EXPERIMENT_COUNT + " experiment(s). Found " + experimentCount, experimentCount == EXPECTED_EXPERIMENT_COUNT);

        sampleLoader.run(loadArgs);

        String bsQuery = "SELECT COUNT(*) AS cnt FROM biological_sample";
        Integer bsCount = 0;

        String bmsQuery = "SELECT COUNT(*) AS cnt FROM biological_model_sample";
        Integer bmsCount = 0;

        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(bsQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                bsCount = resultSet.getInt("cnt");
            }
        }

        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(bmsQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                bmsCount = resultSet.getInt("cnt");
            }
        }

        assertTrue(bsCount == bmsCount);

        // Check that the model has a gene, allele and strain

        String modelQuery = "SELECT * FROM biological_model bm " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bm.id " +
                "INNER JOIN biological_model_sample bmsamp ON bmsamp.biological_model_id=bm.id " ;
        Integer modelCount = 0;
        Set<Integer> modelIds = new HashSet<>();
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(modelQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                modelCount++;
                modelIds.add(resultSet.getInt("id"));
            }

        }

        Assert.assertEquals(1, modelCount.intValue());
        Assert.assertEquals(1, modelIds.size());


        // Load the experiment
        experimentLoader.run(loadArgs);

        experimentQuery = "SELECT COUNT(*) AS cnt FROM experiment";
        experimentCount = 0;

        String observationQuery = "SELECT COUNT(*) AS cnt FROM observation";
        Integer observationCount = 0;

        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(experimentQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                experimentCount = resultSet.getInt("cnt");
            }
        }

        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(observationQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                observationCount = resultSet.getInt("cnt");
            }
        }

        Assert.assertEquals(1, experimentCount.intValue());
        Assert.assertEquals(1, observationCount.intValue());
    }




    @Test
    public void testWeightMapIsDeterministicRegardlessOfOrderOfLoadedValues() throws Exception {

        Integer MAX_REPEATED_LOAD_ATTEMPTS = 10;
        ZonedDateTime dateOfExperiment = ZonedDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd").parse("2009-10-16").toInstant(), ZoneId.of("UTC"));
        Set<WeightMap.BodyWeight> nearestWeights = new HashSet<>();

        for (int i = 0; i<MAX_REPEATED_LOAD_ATTEMPTS; i++) {

            Resource cdaResource = context.getResource("classpath:sql/h2/LoadImpcSpecimenExperiment-data.sql");
            Resource specimenResource = context.getResource("classpath:xml/ImpcSpecimenExperiment-specimens.xml");
            Resource experimentResourceWeightsFirstOrder = context.getResource("classpath:xml/ImpcSpecimenExperiment-experiments-weight1.xml");
            Resource experimentResourceWeightsSecondOrder = context.getResource("classpath:xml/ImpcSpecimenExperiment-experiments-weight2.xml");

            String[] extractSpecimenArgs = new String[]{
                    "--datasourceShortName=IMPC",
                    "--filename=" + specimenResource.getFile().getAbsolutePath()
            };

            String[] extractExperimentArgsFirstOrder = new String[]{
                    "--datasourceShortName=IMPC",
                    "--filename=" + experimentResourceWeightsFirstOrder.getFile().getAbsolutePath()
            };

            String[] extractExperimentArgsSecondOrder = new String[]{
                    "--datasourceShortName=IMPC",
                    "--filename=" + experimentResourceWeightsSecondOrder.getFile().getAbsolutePath()
            };

            String[] loadArgs = new String[]{
            };

            resetDatabases(cdaResource);

            // First load of the weight data
            dccSpecimenExtractor.run(extractSpecimenArgs);
            dccExperimentExtractor.run(extractExperimentArgsFirstOrder);
            sampleLoader.run(loadArgs);
            experimentLoader.setSHUFFLE(Boolean.TRUE);
            experimentLoader.run(loadArgs);

            nearestWeights.add(getNearestWeightFromDatabase(dateOfExperiment));

            System.out.println("\n********************************************************************");
            System.out.println("NEAREST WEIGHTS SET AFTER LOAD USING FIRST ORDERING: " + nearestWeights);
            System.out.println("********************************************************************\n");
            resetDatabases(cdaResource);


            // Second load of the weight data
            dccSpecimenExtractor.run(extractSpecimenArgs);
            dccExperimentExtractor.run(extractExperimentArgsSecondOrder);
            sampleLoader.run(loadArgs);
            experimentLoader.setSHUFFLE(Boolean.TRUE);
            experimentLoader.run(loadArgs);

            nearestWeights.add(getNearestWeightFromDatabase(dateOfExperiment));

            System.out.println("\n*********************************************************************");
            System.out.println("NEAREST WEIGHTS SET AFTER LOAD USING SECOND ORDERING: " + nearestWeights);
            System.out.println("*********************************************************************\n");

            // Found a case where the nearest weight algorithm is non-deterministic,
            // Break out of the loop as the test will now fail
            if (nearestWeights.size() > 1) {
                break;
            }
        }

        Assert.assertEquals(1, nearestWeights.size());
    }


    @Test
    public void testWeightMapReturnsWeightFromSameProcedure() throws Exception {

        Integer MAX_REPEATED_LOAD_ATTEMPTS = 5;
        ZonedDateTime dateOfExperiment = ZonedDateTime.ofInstant(new SimpleDateFormat("yyyy-MM-dd").parse("2009-10-16").toInstant(), ZoneId.of("UTC"));
        Set<WeightMap.BodyWeight> nearestWeights = new HashSet<>();

        for (int i = 0; i<MAX_REPEATED_LOAD_ATTEMPTS; i++) {

            Resource cdaResource = context.getResource("classpath:sql/h2/LoadImpcSpecimenExperiment-data.sql");
            Resource specimenResource = context.getResource("classpath:xml/ImpcSpecimenExperiment-specimens.xml");
            Resource experimentResourceWeights = context.getResource("classpath:xml/ImpcSpecimenExperiment-experiments-weight3.xml");

            String[] extractSpecimenArgs = new String[]{
                    "--datasourceShortName=IMPC",
                    "--filename=" + specimenResource.getFile().getAbsolutePath()
            };

            String[] extractExperimentArgs = new String[]{
                    "--datasourceShortName=IMPC",
                    "--filename=" + experimentResourceWeights.getFile().getAbsolutePath()
            };

            String[] loadArgs = new String[]{
            };

            resetDatabases(cdaResource);

            // First load of the weight data
            dccSpecimenExtractor.run(extractSpecimenArgs);
            dccExperimentExtractor.run(extractExperimentArgs);
            sampleLoader.run(loadArgs);
            experimentLoader.setSHUFFLE(Boolean.TRUE);
            experimentLoader.run(loadArgs);

            nearestWeights.add(getNearestWeightFromDatabase(dateOfExperiment));

            System.out.println("\n********************************************************************");
            System.out.println("Iteration " + i + " NEAREST WEIGHTS SET AFTER LOAD USING FIRST ORDERING: " + nearestWeights);
            System.out.println("********************************************************************\n");

            // Found a case where the nearest weight algorithm is non-deterministic,
            // Break out of the loop as the test will now fail
            Assert.assertEquals(1, nearestWeights.size());
            Assert.assertTrue(new ArrayList<>(nearestWeights).get(0).getWeight() - 35.31 < 0.0001);
            Assert.assertTrue(new ArrayList<>(nearestWeights).get(0).getParameterStableId().contains("DXA"));
        }

    }



    //
    // BEGIN PRIVATE METHODS
    //


    private void resetDatabases(Resource cdaResource) throws SQLException {
        //
        // DROP AND RECREATE THE DATABASE
        //

        String[] cdaSchemas = new String[]{
                "sql/h2/cda/schema.sql",
                "sql/h2/impress/impressSchema.sql"
        };
        String[] dccSchemas = new String[]{
                "sql/h2/dcc/createSpecimen.sql",
                "sql/h2/dcc/createExperiment.sql"
        };

        for (String schema : cdaSchemas) {
            Resource r = context.getResource(schema);
            ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), r);
        }
        for (String schema : dccSchemas) {
            Resource r = context.getResource(schema);
            ScriptUtils.executeSqlScript(dccDataSource.getConnection(), r);

        }

        ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), cdaResource);
        replaceH2DateDiffMethodWithMysqlCompatibleMethod();
    }


    private WeightMap.BodyWeight getNearestWeightFromDatabase(ZonedDateTime dateOfExperiment) throws SQLException {

        WeightMap weightMap = new WeightMap(cdaDataSource);
        weightMap.initialize();
        logger.debug("Weight Map is :" + weightMap.get());

        // Get the specimen ID for checking weight
        Integer testDbId = null;
        try (Connection c = cdaDataSource.getConnection(); PreparedStatement s = c.prepareStatement("SELECT * FROM biological_sample where external_id = 'C10837'")) {
            ResultSet rs = s.executeQuery();
            while (rs.next()) {
                testDbId = rs.getInt("id");
            }
        }

        logger.debug("Weight map for specimen 'C10837' (DB ID is " + testDbId + ") is : " + weightMap.get(testDbId));
        logger.debug("Nearest weight to " + dateOfExperiment + " is " + weightMap.getNearestWeight(testDbId, "IMPC_DXA_002_001", dateOfExperiment));

        return weightMap.getNearestWeight(testDbId, dateOfExperiment);
    }

    private void replaceH2DateDiffMethodWithMysqlCompatibleMethod() throws SQLException {
        // Replace the H2 default DATEDIFF function to be compatible with MySQL DATEDIFF syntax
        String replaceDateDiff = "CREATE ALIAS IF NOT EXISTS REMOVE_DATE_DIFF FOR \"org.mousephenotype.cda.loads.integration.data.utilities.H2Function.removeDateDifference\"; " +
                "CALL REMOVE_DATE_DIFF(); " +
                "DROP ALIAS IF EXISTS DATEDIFF; " +
                "CREATE ALIAS DATEDIFF FOR \"org.mousephenotype.cda.loads.integration.data.utilities.H2Function.dateDifference\"; ";
        Resource d = new ByteArrayResource(replaceDateDiff.getBytes());
        ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), d);
    }
}