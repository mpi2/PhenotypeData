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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.create.extract.dcc.DccExperimentExtractor;
import org.mousephenotype.cda.loads.create.extract.dcc.DccSpecimenExtractor;
import org.mousephenotype.cda.loads.create.load.ExperimentLoader;
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
 *
 * This test validates that a sample and an experiment with a valid background strain in IMITS but no background strain
 * specified in the XML file is correctly loaded and that the biological model background strain matches the one in
 * IMITS.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan
@ContextConfiguration(classes = TestConfig.class)
public class ImpcSpecimenExperimentLoadIntegrationTest4 {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource cdaDataSource;
    
    @Autowired
    private DataSource dccDataSource;

    @Autowired
    private CdaSqlUtils cdaSqlUtils;

    @Autowired
    private DccSpecimenExtractor dccSpecimenExtractor;

    @Autowired
    private DccExperimentExtractor dccExperimentExtractor;

    @Autowired
    private SampleLoader sampleLoader;

    @Autowired
    private ExperimentLoader experimentLoader;


    @Before
    public void before() throws SQLException {

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
    public void testXmlStrainMissing() throws Exception {

        Resource cdaResource        = context.getResource("classpath:sql/h2/LoadImpcSpecimenExperiment-data4.sql");
        Resource specimenResource   = context.getResource("classpath:xml/ImpcSpecimenExperiment-specimens4.xml");
        Resource experimentResource = context.getResource("classpath:xml/ImpcSpecimenExperiment-experiments4.xml");

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
        Assert.assertEquals(2, observationCount.intValue());
        Assert.assertEquals("strain_4", cdaSqlUtils.getExperimentBackgroundStrain("PAT_2015-06-29 2:14 PM_ET8295-113").getName());
    }
}