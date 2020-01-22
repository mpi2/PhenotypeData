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
import java.util.*;

import static junit.framework.TestCase.assertTrue;

/**
 * This is an end-to-end integration data test class that uses an in-memory database to populate a small dcc, cda_base,
 * and cda set of databases.
 *
 * This test validates the Ccpcz series parameter values that threw exceptions in DR11.0.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan
@ContextConfiguration(classes = TestConfig.class)
public class ImpcSeriesParameterExperimentLoadIntegrationTest {
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
    private DccExperimentExtractor dccExperimentExtractor;

    @Autowired
    private SampleLoader sampleLoader;

    @Autowired
    private ExperimentLoader experimentLoader;

    @Autowired
    private CdaSqlUtils cdaSqlUtils;


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
    public void validateCcpczSeriesParameters() throws Exception {

        Resource cdaResource        = context.getResource("classpath:sql/h2/LoadImpcSeriesParameterExperiment-data.sql");
        Resource specimenResource   = context.getResource("classpath:xml/ImpcSeriesParameterExperiment-specimens.xml");
        Resource experimentResource = context.getResource("classpath:xml/ImpcSeriesParameterExperiment-experiments.xml");

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

        // Looad the experiment
        experimentLoader.run(loadArgs);

        experimentQuery = "SELECT COUNT(*) AS cnt FROM experiment";
        experimentCount = 0;

        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(experimentQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                experimentCount = resultSet.getInt("cnt");
            }
        }

        Assert.assertEquals(1, experimentCount.intValue());

        // There should be 5 valid observations (2 simple, 3 time-series) and 1 missing observation

        String observationNotMissingQuery = "SELECT COUNT(*) AS cnt FROM observation WHERE missing = 0";
        Integer observationNotMissingCount = 0;

        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(observationNotMissingQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                observationNotMissingCount = resultSet.getInt("cnt");
            }
        }

        Assert.assertEquals("Expected 5 observations but found " + observationNotMissingCount, 5, observationNotMissingCount.intValue());

        String observationIsMissingQuery = "SELECT COUNT(*) AS cnt FROM observation WHERE missing = 1";
        Integer observationIsMissingCount = 0;

        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(observationIsMissingQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                observationIsMissingCount = resultSet.getInt("cnt");
            }
        }

        Assert.assertEquals("Expected 1 missing observation but found " + observationIsMissingCount, 1, observationIsMissingCount.intValue());


        // Check valid time series observation value count

        String timeSeriesObservationQuery = "SELECT COUNT(*) AS cnt FROM time_series_observation";
        Integer timeSeriesObservationCount = 0;

        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(timeSeriesObservationQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                timeSeriesObservationCount = resultSet.getInt("cnt");
            }
        }

        Assert.assertEquals("Expected 3 time series observations but found " + timeSeriesObservationCount, 3, timeSeriesObservationCount.intValue());


        String       observationValuesQuery = "SELECT discrete_point from time_series_observation";
        List<String> observationDiscretePoints = new ArrayList<>();
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(observationValuesQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                observationDiscretePoints.add(resultSet.getString("discrete_point"));
            }
        }

        Collections.sort(observationDiscretePoints);
        observationDiscretePoints.stream().forEach(System.out::println);

        Assert.assertTrue("Didn't find expected time series observation discrete_value '11.666", observationDiscretePoints.get(0).startsWith("11.666"));
        Assert.assertTrue("Didn't find expected time series observation discrete_value '11.916", observationDiscretePoints.get(1).startsWith("11.916"));
        Assert.assertTrue("Didn't find expected time series observation discrete_value '11.666", observationDiscretePoints.get(2).startsWith("12.166"));
    }
}