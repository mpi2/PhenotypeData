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
import org.mousephenotype.cda.loads.common.BioModelResults;
import org.mousephenotype.cda.loads.create.extract.dcc.DccExperimentExtractor;
import org.mousephenotype.cda.loads.create.extract.dcc.DccSpecimenExtractor;
import org.mousephenotype.cda.loads.create.load.ExperimentLoader;
import org.mousephenotype.cda.loads.create.load.SampleLoader;
import org.mousephenotype.cda.loads.integration.data.config.TestConfig;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan
@ContextConfiguration(classes = TestConfig.class)
public class DataIntegrationTest {
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
    public void testLoadSpecimenAndExperiment() throws Exception {

        Resource dataResource   = context.getResource("classpath:sql/h2/LoadSpecimenAndExperiment-data.sql");
        Resource specimenResource   = context.getResource("classpath:xml/LoadSpecimenAndExperiment-Specimens.xml");
        Resource experimentResource = context.getResource("classpath:xml/LoadSpecimenAndExperiment-Experiment.xml");

        ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), dataResource);

        String[] extractSpecimenArgs = new String[]{
                "--datasourceShortName=EuroPhenome",
                "--filename=" + specimenResource.getFile().getAbsolutePath()
        };

        String[] extractExperimentArgs = new String[]{
                "--datasourceShortName=EuroPhenome",
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

        // Check that the model has a gene, allele and strain

        String modelQuery = "SELECT * FROM biological_model bm " +
                "INNER JOIN biological_model_allele bma ON bma.biological_model_id=bm.id " +
                "INNER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id=bm.id " +
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

        Assert.assertEquals(modelCount.intValue(), 31);
        Assert.assertEquals(modelIds.size(), 1);

    }




    /**
     * The intention of this test is to verify that the background strain is the same for control specimens as it is for
     * mutant specimens. This test should be made for both line-level and specimen-level experiments.
     *
     * So we need a control specimen and a mutant specimen for the specimen-level experiment part of the test, and a
     * line-level experiment for the line-level part of the test.
     *
     * specimen-level experiment using Akt2:
     *   productionCenter: Wtsi
     *
     *   SPECIMEN                         EXPERIMENT
     *   control specimenId:  14819
     *   mutant specimenId:   19603       WTSI.2013-10-31.14.experiment.impc.xml   line 38783
     */
 //@Ignore
    @Test
    public void testBackgroundStrainIsEqual() throws Exception {

        Resource dataResource   = context.getResource("classpath:sql/h2/BackgroundStrainIsEqual-data.sql");
        Resource specimenResource   = context.getResource("classpath:xml/BackgroundStrainIsEqual-Specimens.xml");
        Resource experimentResource = context.getResource("classpath:xml/BackgroundStrainIsEqual-Experiment.xml");

        ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), dataResource);

        String[] extractSpecimenArgs = new String[]{
                "--datasourceShortName=EuroPhenome",
                "--filename=" + specimenResource.getFile().getAbsolutePath()
        };

        String[] extractExperimentArgs = new String[]{
                "--datasourceShortName=EuroPhenome",
                "--filename=" + experimentResource.getFile().getAbsolutePath()
        };

        String[] loadArgs = new String[] {
                };

        dccSpecimenExtractor.run(extractSpecimenArgs);
        dccExperimentExtractor.run(extractExperimentArgs);

        sampleLoader.run(loadArgs);
        experimentLoader.run(loadArgs);

        List<List<String>> results     = getSpecimenStrainAccs();
        List<String>       controlList = results.get(0);
        List<String>       mutantList  = results.get(1);

        if ((controlList.get(0) != mutantList.get(0)) || (controlList.get(1) != mutantList.get(1))) {
            Assert.fail("control and mutant strains differ:\n\tcontrol: " + controlList.get(0) + "::" + controlList.get(1) +
                                "\n\tmutant:  " + mutantList.get(0) + "::" + mutantList.get(1));
        }


        // TODO - Check that experiment.colony_id and experiment.biological_model_id are null
    }

    /*
     * Test the special MGP EuroPhenome remapping rule
     */
    // FIXME - Implement me.
//@Ignore
//    @Test
//    public void testEuroPhenomeRemapper() {
//
//    }

    /*
     * Test that line-level experiments get the correct biological model and friends.
     * ColonyId:                'MCCU'
     * alleleSymbol:            'Rcn3<tm1b(EUCOMM)Hmgu>'
     * background_strain_name:  'Gsk3a<tm1a(EUCOMM)Wtsi>'
     * phenotypingCenterPk:     3  (WTSI)
     * phenotypingConsortiumPk: 8  (MGP)
     * filename:                WTSI/WTSI.2017-09-11.126.experiment.xml
     *
     * EXPECTED:
     *  experiment:
     *      experimentId:           'IMPC_FER_001-MCCU'
     *      db_id:                  22 (IMPC)
     *      phenotypingCenter:      'Wtsi'
     *      pipeline:               'MGP_001'
     *      project:                'MGP'
     *      procedureId:            'IMPC_VIA_001'
     *      dcc_procedure_pk:
     *      colonyId:               'MCCU'
     *      isLineLevel:            1
     *
     *  biological_model:
     *      id:                     43258
     *      db_id:                  22 (IMPC)
     *      allelic_composition:    'Gsk3a<tm1a(EUCOMM)Wtsi>/Gsk3a<tm1a(EUCOMM)Wtsi>'
     *      genetic_background:     'involves: C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac'
     *      zygosity:               'homozygote'
     *
     *   strain:
     *      acc:                    'IMPC-CURATE-C44BE'
     *      db_id:                  22 (IMPC)
     *      biotype_acc:            'CV:00000051'
     *      biotype_db_id:          3 (MGI)
     *      name:                   'C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac'
     *
     *   b_m_gene:
     *      gf_acc:                 'MGI:2152453'
     *      gf_db_id:               3 (MGI)
     *
     *   b_m_allele:
     *      allele_acc:             'MGI:4434136'
     *      allele_db_id:           3 (MGI)
     *
     *   b_m_strain:
     *      strain_acc:             'IMPC-CURATE-C44BE'
     *      strain_db_id:           22 (IMPC)
     *
     *   biological_sample:         <all fields are null>
     *
     * NOTE: This test generates a DccExperimentExtractor WARNING that can be ignored. It generates this warning:
     *               "UNKNOWN CENTER,PIPELINE,PROJECT: 'Wtsi,MGP_001,MGP Legacy'. INSERTING..."
     *       because it expects the center, pipeline, and project to be inserted by the SampleLoader, which is not run
     *       for this test because there are no samples.
     */
//@Ignore
    @Test
    public void testLineLevelExperiment() throws Exception {

        Resource dataResource   = context.getResource("classpath:sql/h2/LineLevelExperiment-data.sql");
        Resource experimentResource = context.getResource("classpath:xml/LineLevelExperiment-Experiment.xml");

        ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), dataResource);

        String[] extractExperimentArgs = new String[]{
                "--datasourceShortName=IMPC",
                "--filename=" + experimentResource.getFile().getAbsolutePath()
        };

        String[] loadArgs = new String[] {
                };

        dccExperimentExtractor.run(extractExperimentArgs);

        experimentLoader.run(loadArgs);

        BioModelResults expected = getLineLevelExpectedResults(jdbcCda);
        BioModelResults actual = BioModelResults.query(22,
                                                "Gsk3a<tm1a(EUCOMM)Wtsi>/Gsk3a<tm1a(EUCOMM)Wtsi>",
                                                "involves: C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac",
                                                "homozygote");

        RunStatus status = expected.diff(actual);
        if (status.hasWarnings()) {
            for (String s : status.getWarningMessages()) {
                logger.error(s);
            }

            Assert.fail();
        }
    }

//    @Test
//    public void testLineExperimentThatCreatesNewBiologicalModel() {
//
//    }
//
//    @Test
//    public void testExperimentWithSimpleParameters_INCLUDE_DERIVED_PARAMETERS_false() {
//
//    }
//
//    @Test
//    public void testExperimentWithSimpleParameters_INCLUDE_DERIVED_PARAMETERS_true() {
//
//    }
//
//    @Test
//    public void testExperimentWithMediaParametersWithoutParameterAssociations() {
//
//    }
//
//    @Test
//    public void testExperimentWithMediaParametersWithParameterAssociations() {
//
//    }
//
//    @Test
//    public void testExperimentWithMediaParametersWithLinks() {
//
//    }
//
//    @Test
//    public void testExperimentWithOntologyParameters() {
//
//    }
//
//    @Test
//    public void testExperimentWithSeriesParameters_INCLUDE_DERIVED_PARAMETERS_true() {
//
//    }
//
//    @Test
//    public void testExperimentWithSeriesParameters_INCLUDE_DERIVED_PARAMETERS_false() {
//
//    }
//
//    @Test
//    public void testExperimentWithSeriesMediaParametersWithParameterAssociations() {
//
//    }
//
//    @Test
//    public void testExperimentWithSeriesMediaParametersWithProcedureMetadataAssociations() {
//
//    }
//
//    @Test
//    public void testExperimentWithSeriesMediaParametersWithLink() {
//
//    }
//
//    @Test
//    public void testExperimentWithMediaSampleParameterWithLink() {
//
//    }
//
//    @Test
//    public void testExperimentWithSeriesParameterValue() {
//
//    }
//
//    @Test
//    public void testExperimentWithProcedureLevelMetadata() {
//
//    }
//
//    @Test
//    public void testExperimentWithObservationLevelMetadata() {
//
//    }
//
//    /*
//     * Test the special rule for 3i projects wiith valid identifiers
//     */
//    @Test
//    public void test3iExperimentWithValidProjectIdentifier() {
//
//    }
//
//
//    /*
//     * Test the special rule for 3i projects wiith invalid identifiers
//     */
//    @Test
//    public void test3iExperimentWithInvalidProjectIdentifier() {
//
//    }


    // PRIVATE METHODS


    /**
    Returns:
    <ul>
        <li>control strain acc, control strain name</li>
        <li>mutant strain acc, mutant strain name</li>
     </ul>
     */
    private List<List<String>> getSpecimenStrainAccs() {

        List<List<String>> results = new ArrayList<>();

        String query = "SELECT bs.*, s.* FROM biological_sample bs " +
                "LEFT OUTER JOIN biological_model_sample bms ON bms.biological_sample_id = bs.id " +
                "LEFT OUTER JOIN biological_model bm ON bm.id = bms.biological_model_id " +
                "LEFT OUTER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id = bm.id " +
                "LEFT OUTER JOIN strain s ON s.acc = bmstrain.strain_acc " +
                "WHERE bs.external_id IN ('19603', '14819')";

        Map<String, Object> parameterMap = new HashMap<>();

        List<Map<String, Object>> listMap     = jdbcCda.queryForList(query, parameterMap);
        List<String>              controlList = new ArrayList<>();
        List<String>              mutantList  = new ArrayList<>();
        for (Map<String, Object> map : listMap) {
            Object sampleGroup = map.get("sample_group");
            Object acc         = map.get("acc");
            Object name        = map.get("name");

            if (sampleGroup != null) {
                switch (sampleGroup.toString()) {
                    case "control":
                        controlList.add(acc == null ? "" : acc.toString());
                        controlList.add(name == null ? "" : name.toString());
                        break;

                    default:
                        mutantList.add(acc == null ? "" : acc.toString());
                        mutantList.add(name == null ? "" : name.toString());
                }
            }

            results.add(controlList);
            results.add(mutantList);
        }

        return results;
    }

    private BioModelResults getLineLevelExpectedResults(NamedParameterJdbcTemplate jdbcCda) {
        return new BioModelResults(jdbcCda,
                                   22,
                                   "Gsk3a<tm1a(EUCOMM)Wtsi>/Gsk3a<tm1a(EUCOMM)Wtsi>",
                                   "involves: C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac",
                                   "homozygote",
                                   "MGI:2152453", 3,
                                   "MGI:4434136", 3,
                                   "IMPC-CURATE-C44BE", 22,
                                   "IMPC-CURATE-C44BE", 22, "CV:00000051", 3, "C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac",
                                   null, 0, null, 0, null, 0, 0);
    }
}