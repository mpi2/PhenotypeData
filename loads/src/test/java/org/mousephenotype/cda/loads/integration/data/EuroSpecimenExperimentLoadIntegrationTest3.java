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
import org.mousephenotype.cda.loads.create.extract.dcc.DccSpecimenExtractor;
import org.mousephenotype.cda.loads.create.load.SampleLoader;
import org.mousephenotype.cda.loads.integration.data.config.TestConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
public class EuroSpecimenExperimentLoadIntegrationTest3 {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource cdaDataSource;
    
    @Autowired
    private DataSource dccDataSource;




    @Autowired
    private DccSpecimenExtractor dccSpecimenExtractor;

    @Autowired
    private SampleLoader sampleLoader;



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

        Resource cdaResource        = context.getResource("classpath:sql/h2/LoadEuroSpecimenExperiment-data3.sql");
        Resource specimenResource   = context.getResource("classpath:xml/EuroSpecimenExperiment-specimens3.xml");

        ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), cdaResource);

        String[] extractSpecimenArgs = new String[]{
                "--datasourceShortName=EuroPhenome",
                "--filename=" + specimenResource.getFile().getAbsolutePath()
        };


        String[] loadArgs = new String[] {
        };

        dccSpecimenExtractor.run(extractSpecimenArgs);

        String specimenQuery = "SELECT COUNT(*) AS cnt FROM specimen";
        Integer specimenCount = 0;


        try (Connection connection = dccDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(specimenQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                specimenCount = resultSet.getInt("cnt");
            }
        }


        final int EXPECTED_SPECIMEN_COUNT = 3;
        assertTrue( "Expected " + EXPECTED_SPECIMEN_COUNT + " specimen(s). Found " + specimenCount, specimenCount == EXPECTED_SPECIMEN_COUNT);

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

        String modelQuery = "SELECT bm.id, bmstrain.strain_acc FROM biological_model bm " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bm.id " ;
        Integer modelCount = 0;
        Set<String> strains = new HashSet<>();
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(modelQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                modelCount++;
                strains.add(resultSet.getString("strain_acc"));
            }

        }

        Assert.assertEquals(3, modelCount.intValue());
        Assert.assertEquals(1, strains.size());

    }
}