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

import org.apache.commons.lang3.StringUtils;
import org.h2.tools.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.loads.create.extract.dcc.DccExperimentExtractor;
import org.mousephenotype.cda.loads.create.extract.dcc.DccSpecimenExtractor;
import org.mousephenotype.cda.loads.create.load.ExperimentLoader;
import org.mousephenotype.cda.loads.create.load.SampleLoader;
import org.mousephenotype.cda.loads.integration.data.config.TestConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;

/**
 * This is an end-to-end integration data test class that uses an in-memory database to populate a small dcc, cda_base,
 * and cda set of databases.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ImpcTcpExperimentLoadIntegrationTest {
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



    // Set startServer to true to produce an in-memory h2 database browser.
    private static boolean startServer = false;
    private static Server server;

    private Thread thread;
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
    public void testLoadSpecimenAndExperiment() throws Exception {

        Resource dataResource   = context.getResource("classpath:sql/h2/LoadTcpSpecimenAndExperiment-data.sql");
        Resource specimenResource   = context.getResource("classpath:xml/LoadTcpSpecimenAndExperiment-Specimens.xml");
        Resource experimentResource = context.getResource("classpath:xml/LoadTcpSpecimenAndExperiment-Experiment.xml");

        ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), dataResource);

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

        sampleLoader.run(loadArgs);
        experimentLoader.run(loadArgs);

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

        // Check that the model has a strain

        String modelQuery = "SELECT * FROM biological_model bm " +
                "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bm.id " +
                "INNER JOIN biological_model_sample bmsamp ON bmsamp.biological_model_id=bm.id " ;
        Set<Integer> modelIds = new HashSet<>();
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(modelQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                modelIds.add(resultSet.getInt("id"));
            }

        }

        Assert.assertEquals(2, modelIds.size());


        String expQuery = "SELECT * FROM experiment e ";
        Set<Integer> expIds = new HashSet<>();
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(expQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                expIds.add(resultSet.getInt("id"));
            }

        }

        Assert.assertEquals(3, expIds.size());



        String q = "SELECT * FROM experiment e INNER JOIN experiment_observation eo ON eo.experiment_id=e.id " +
                "INNER JOIN observation o ON o.id=eo.observation_id " +
                "INNER JOIN unidimensional_observation uo ON uo.id=o.id " +
                "WHERE e.external_id='Biochemistry_1938'";
        Set<String> obs = new HashSet<>();
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(q)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                String key = resultSet.getString("experiment_id") + "::";
                key += resultSet.getString("external_id") + "::";
                key += resultSet.getString("parameter_stable_id") + "::";
                key += resultSet.getString("data_point");
                obs.add(key);
            }
        }

        System.out.println(obs);

        Assert.assertTrue(obs.size() > 10);


        //
        // VALIDATE THE IMAGES LOADED CORRECTLY
        //


        String imgQuery = "SELECT * FROM experiment e INNER JOIN experiment_observation eo ON eo.experiment_id=e.id " +
                "INNER JOIN observation o ON o.id=eo.observation_id " +
                "INNER JOIN image_record_observation iro ON iro.id=o.id " +
                "WHERE e.external_id='Xray_893'";
        Set<ImageRecordDTO> images = new HashSet<>();
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(imgQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                String experimentId = resultSet.getString("experiment_id");
                String downloadFilePath = resultSet.getString("download_file_path");
                String fullResolutionFilepath = resultSet.getString("full_resolution_file_path");
                images.add(new ImageRecordDTO(experimentId, downloadFilePath, fullResolutionFilepath));
            }
        }

        System.out.println(StringUtils.join(images, "\n"));

        Assert.assertTrue(images.size() == 4);

        Assert.assertTrue(images.stream().map(ImageRecordDTO::getFullResolutionFilepath).filter(Objects::nonNull).count() == 4);



        //
        // VALIDATE THE METADATA GROUPS AND COMBINED ARE CALCULATED PROPERLY
        //

        String metaGroupsQuery = "SELECT DISTINCT e.id, metadata_group, metadata_combined FROM experiment e " +
                "INNER JOIN experiment_observation eo ON eo.experiment_id=e.id " +
                "INNER JOIN observation o ON o.id=eo.observation_id " +
                "WHERE e.external_id in ('ClinicalBloodChemistry_4807', 'Biochemistry_1938')";
        Set<String> metadataGroups = new HashSet<>();
        Set<String> metadataCombinedGroups = new HashSet<>();
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(metaGroupsQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                logger.info("Adding metadata group {} for experiment {}", resultSet.getString("metadata_group"), resultSet.getString("id"));
                metadataGroups.add(resultSet.getString("metadata_group"));
                metadataCombinedGroups.add(resultSet.getString("metadata_combined"));
            }
        }

        System.out.println("Metadata groups: " + StringUtils.join(metadataGroups, ", "));
        System.out.println("Metadata combined groups: " + StringUtils.join(metadataCombinedGroups, ", "));

        Assert.assertTrue(metadataGroups.size() == 1);

        for (String metadataGroup : metadataCombinedGroups) {
            Assert.assertTrue( ! metadataGroup.toLowerCase().contains("experimenter"));
        }
    }

    class ImageRecordDTO {
        private String experimentId;
        private String downloadFilePath;
        private String fullResolutionFilepath;

        public ImageRecordDTO(String experimentId, String downloadFilePath, String fullResolutionFilepath) {
            this.experimentId = experimentId;
            this.downloadFilePath = downloadFilePath;
            this.fullResolutionFilepath = fullResolutionFilepath;
        }

        @Override
        public String toString() {
            return "ImageRecordDTO{" +
                    "experimentId='" + experimentId + '\'' +
                    ", downloadFilePath='" + downloadFilePath + '\'' +
                    ", fullResolutionFilepath='" + fullResolutionFilepath + '\'' +
                    '}';
        }

        public String getExperimentId() {
            return experimentId;
        }

        public void setExperimentId(String experimentId) {
            this.experimentId = experimentId;
        }

        public String getDownloadFilePath() {
            return downloadFilePath;
        }

        public void setDownloadFilePath(String downloadFilePath) {
            this.downloadFilePath = downloadFilePath;
        }

        public String getFullResolutionFilepath() {
            return fullResolutionFilepath;
        }

        public void setFullResolutionFilepath(String fullResolutionFilepath) {
            this.fullResolutionFilepath = fullResolutionFilepath;
        }
    }
}