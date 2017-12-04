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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.create.extract.dcc.DccExperimentExtractor;
import org.mousephenotype.cda.loads.create.extract.dcc.DccSpecimenExtractor;
import org.mousephenotype.cda.loads.create.load.ExperimentLoader;
import org.mousephenotype.cda.loads.create.load.SampleLoader;
import org.mousephenotype.cda.loads.integration.data.config.TestConfig;
import org.mousephenotype.cda.utilities.RunStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an end-to-end integration data test class that uses an in-memory database to populate a small dcc, cda_base,
 * and cda set of databases.
 */
// FIXME
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")

public class DataIntegrationTest {

    @Autowired
    private ApplicationContext context;

    
    
    @Autowired
    private DataSource dccDataSource;

    @Autowired
    private DccSqlUtils dccSqlUtils;



    @Autowired
    private DataSource cdabaseDataSource;

    @Autowired
    private CdaSqlUtils cdabaseSqlUtils;

    


    @Autowired
    private CdaSqlUtils cdaSqlUtils;

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

    @Autowired
    private DataSource cdaDataSource;



    // Set startServer to true to produce an in-memory h2 database browser.
    private static boolean startServer = true;
    private static Server server;

    private Thread thread;
    @Before
    public void before() throws SQLException {


        // Show browser if startServer is true.
        if (startServer) {
            startServer = false;
            Runnable runnable = () -> {

                try {
                    Server.startWebServer(cdaDataSource.getConnection());

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
            Resource r = context.getResource(schema);
            ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), r);
        }
        for (String schema : dccSchemas) {
            Resource r = context.getResource(schema);
            ScriptUtils.executeSqlScript(dccDataSource.getConnection(), r);
        }
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
 @Ignore
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

        String[] loadArgs = new String[]{
                "--profile=dev",
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
@Ignore
    @Test
    public void testEuroPhenomeRemapper() {

    }

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

        String[] loadArgs = new String[]{
                "--profile=dev",
                };

        dccExperimentExtractor.run(extractExperimentArgs);

        experimentLoader.run(loadArgs);

        BioModelResults expected = getLineLevelExpectedResults();
        BioModelResults actual = expected.query(22,
                                                "Gsk3a<tm1a(EUCOMM)Wtsi>/Gsk3a<tm1a(EUCOMM)Wtsi>",
                                                "involves: C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac",
                                                "homozygote");

        RunStatus status = expected.diff(actual);
        System.out.println(status.getWarningMessages());

//        List<List<String>> results = getSpecimenStrainAccs();
//        List<String> controlList = results.get(0);
//        List<String> mutantList = results.get(1);
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

    public class BioModelResults {

        int    bm_db_id;
        String bm_allelicComposition;
        String bm_geneticBackground;
        String bm_zygosity;

        String bm_strain_acc;
        int    bm_strain_db_id;

        String  bm_gf_acc;
        Integer bm_gf_db_id;

        String  bm_allele_acc;
        Integer bm_allele_db_id;

        String strain_acc;
        int    strain_db_id;

        String strain_biotype_acc;
        int    strain_biotype_db_id;
        String strain_name;

        String  bs_external_id;
        Integer bs_db_id;
        String  bs_sample_type_acc;
        Integer bs_sample_type_db_id;
        String  bs_sample_group;
        Integer bs_organisation_id;
        Integer bs_production_center_id;

        public BioModelResults(int bm_db_id, BiologicalModel bm, Strain strain) {
            this.bm_db_id = bm_db_id;
            this.bm_allelicComposition = bm.getAllelicComposition();
            this.bm_geneticBackground = bm.getGeneticBackground();
            this.bm_zygosity = bm.getZygosity();

            if ((bm.getStrains() != null) && (bm.getStrains().size() > 0)) {
                this.bm_strain_acc = bm.getStrains().get(0).getId().getAccession();
                this.bm_strain_db_id = bm.getStrains().get(0).getId().getDatabaseId();
            }
            
            if ((bm.getGenomicFeatures() != null) && (bm.getGenomicFeatures().size() > 0)) {
                this.bm_gf_acc = bm.getGenomicFeatures().get(0).getId().getAccession();
                this.bm_gf_db_id = bm.getGenomicFeatures().get(0).getId().getDatabaseId();
            }

            if ((bm.getAlleles() != null) && (bm.getAlleles().size() > 0)) {
                this.bm_allele_acc = bm.getAlleles().get(0).getId().getAccession();
                this.bm_allele_db_id = bm.getAlleles().get(0).getId().getDatabaseId();
            }

            this.strain_acc = strain.getId().getAccession();
            this.strain_db_id = strain.getId().getDatabaseId();
            this.strain_biotype_acc = strain.getBiotype().getId().getAccession();
            this.strain_biotype_db_id = strain.getBiotype().getId().getDatabaseId();
            this.strain_name = strain.getName();

            if ((bm.getBiologicalSamples() != null) && (bm.getBiologicalSamples().size() > 0)) {
                this.bs_db_id = bm.getBiologicalSamples().get(0).getDatasource().getId();
                this.bs_external_id = bm.getBiologicalSamples().get(0).getStableId();
                this.bs_sample_type_acc = bm.getBiologicalSamples().get(0).getType().getId().getAccession();
                this.bs_sample_type_db_id = bm.getBiologicalSamples().get(0).getType().getId().getDatabaseId();

                this.bs_sample_group = bm.getBiologicalSamples().get(0).getGroup();
                this.bs_organisation_id = bm.getBiologicalSamples().get(0).getOrganisation().getId();
                this.bs_production_center_id = bm.getBiologicalSamples().get(0).getProductionCenter().getId();
            }
        }

        public BioModelResults(
                int bm_db_id,
                String bm_allelicComposition,
                String bm_geneticBackground,
                String bm_zygosity,

                String bm_gf_acc,
                int bm_gf_db_id,

                String bm_allele_acc,
                int bm_allele_db_id,

                String bm_strain_acc,
                int bm_strain_db_id,

                String strain_acc,
                int strain_db_id,
                String strain_biotype_acc,
                int strain_biotype_db_id,
                String strain_name,

                String bs_external_id,
                Integer bs_db_id,
                String bs_sample_type_acc,
                Integer bs_sample_type_db_id,
                String bs_sample_group,
                Integer bs_organisation_id,
                Integer bs_production_center_id)
        {
            this.bm_db_id = bm_db_id;
            this.bm_allelicComposition = bm_allelicComposition;
            this.bm_geneticBackground = bm_geneticBackground;
            this.bm_zygosity = bm_zygosity;

            this.bm_strain_acc = bm_strain_acc;
            this.bm_strain_db_id = bm_strain_db_id;
            
            this.bm_gf_acc = bm_gf_acc;
            this.bm_gf_db_id = bm_gf_db_id;
            
            this.bm_allele_acc = bm_allele_acc;
            this.bm_allele_db_id = bm_allele_db_id;

            this.strain_acc = strain_acc;
            this.strain_db_id = strain_db_id;
            this.strain_biotype_acc = strain_biotype_acc;
            this.strain_biotype_db_id = strain_biotype_db_id;
            this.strain_name = strain_name;
            
            this.bs_external_id = bs_external_id;
            this.bs_db_id = bs_db_id;
            this.bs_sample_type_acc = bs_sample_type_acc;
            this.bs_sample_type_db_id = bs_sample_type_db_id;
            this.bs_sample_group = bs_sample_group;
            this.bs_organisation_id = bs_organisation_id;
            this.bs_production_center_id = bs_production_center_id;
        }

        public RunStatus diff(BioModelResults other) {
            RunStatus status = new RunStatus();
            if (bm_db_id != other.bm_db_id) { status.addWarning("bm_db_id mismatch: " + bm_db_id + "::" + other.bm_db_id); }
            if (bm_allelicComposition != other.bm_allelicComposition) { status.addWarning("bm_allelicComposition mismatch: " + bm_allelicComposition + "::" + other.bm_allelicComposition); }
            if (bm_geneticBackground != other.bm_geneticBackground) { status.addWarning("bm_geneticBackground mismatch: " + bm_geneticBackground + "::" + other.bm_geneticBackground); }
            if (bm_zygosity != other.bm_zygosity) { status.addWarning("bm_zygosity mismatch: " + bm_zygosity + "::" + other.bm_zygosity); }
            if (strain_acc != other.strain_acc) { status.addWarning("strain_acc mismatch: " + strain_acc + "::" + other.strain_acc); }
            if (strain_db_id != other.strain_db_id) { status.addWarning("strain_db_id mismatch: " + strain_db_id + "::" + other.strain_db_id); }
            if (strain_biotype_acc != other.strain_biotype_acc) { status.addWarning("strain_biotype_acc mismatch: " + strain_biotype_acc + "::" + other.strain_biotype_acc); }
            if (strain_biotype_db_id != other.strain_biotype_db_id) { status.addWarning("strain_biotype_db_id mismatch: " + strain_biotype_db_id + "::" + other.strain_biotype_db_id); }
            if (strain_name != other.strain_name) { status.addWarning("strain_name mismatch: " + strain_name + "::" + other.strain_name); }
            if (bm_gf_acc != other.bm_gf_acc) { status.addWarning("gf_acc mismatch: " + bm_gf_acc + "::" + other.bm_gf_acc); }
            if (bm_gf_db_id != other.bm_gf_db_id) { status.addWarning("gf_db_id mismatch: " + bm_gf_db_id + "::" + other.bm_gf_db_id); }
            if (bm_allele_acc != other.bm_allele_acc) { status.addWarning("allele_acc mismatch: " + bm_allele_acc + "::" + other.bm_allele_acc); }
            if (bm_allele_db_id != other.bm_allele_db_id) { status.addWarning("allele_db_id mismatch: " + bm_allele_db_id + "::" + other.bm_allele_db_id); }
            if (bs_external_id != other.bs_external_id) { status.addWarning("biosample_external_id mismatch: " + bs_external_id + "::" + other.bs_external_id); }
            if (bs_db_id != other.bs_db_id) { status.addWarning("biosample_db_id mismatch: " + bs_db_id + "::" + other.bs_db_id); }
            if (bs_sample_type_acc != other.bs_sample_type_acc) { status.addWarning("biosample_sample_type_acc mismatch: " + bs_sample_type_acc + "::" + other.bs_sample_type_acc); }
            if (bs_sample_type_db_id != other.bs_sample_type_db_id) { status.addWarning("biosample_sample_type_db_id mismatch: " + bs_sample_type_db_id + "::" + other.bs_sample_type_db_id); }
            if (bs_sample_group != other.bs_sample_group) { status.addWarning("biosample_sample_group mismatch: " + bs_sample_group + "::" + other.bs_sample_group); }
            if (bs_organisation_id != other.bs_organisation_id) { status.addWarning("biosample_organisation_id mismatch: " + bs_organisation_id + "::" + other.bs_organisation_id); }
            if (bs_production_center_id != other.bs_production_center_id) { status.addWarning("biosample_production_center_id mismatch: " + bs_production_center_id + "::" + other.bs_production_center_id); }

            return status;
        }

        public BioModelResults query(int db_id, String allelicComposition, String geneticBackground, String zygosity) {
            String query = "SELECT\n" +
                    "  bm.db_id                  AS bm_db_id,\n" +
                    "  bm.allelic_composition    AS bm_allelic_composition,\n" +
                    "  bm.genetic_background     AS bm_genetic_background,\n" +
                    "  bm.zygosity               AS bm_zygosity,\n" +
                    "  \n" +
                    "  bmgf.gf_acc               AS bm_gf_acc,\n" +
                    "  bmgf.gf_db_id             AS bm_gf_db_id,\n" +
                    "  \n" +
                    "  bma.allele_acc            AS bm_allele_acc,\n" +
                    "  bma.allele_db_id          AS bm_allele_db_id,\n" +
                    "  \n" +
                    "  bmstr.strain_acc          AS bm_strain_acc,\n" +
                    "  bmstr.strain_db_id        AS bm_strain_db_id,\n" +
                    "  \n" +
                    "  s.acc                     AS strain_acc,\n" +
                    "  s.db_id                   AS strain_db_id,\n" +
                    "  s.biotype_acc             AS strain_biotype_acc,\n" +
                    "  s.biotype_db_id           AS strain_biotype_db_id,\n" +
                    "  s.name                    AS strain_name,\n" +
                    "  \n" +
                    "  bs.external_id            AS bs_external_id,\n" +
                    "  bs.db_id                  AS bs_db_id,\n" +
                    "  bs.sample_type_acc        AS bs_sample_type_acc,\n" +
                    "  bs.sample_type_db_id      AS bs_sample_type_db_id,\n" +
                    "  bs.sample_group           AS bs_sample_group,\n" +
                    "  bs.organisation_id        AS bs_organisation_id,\n" +
                    "  bs.production_center_id   AS bs_production_center_id\n" +
                    "FROM biological_model bm\n" +
                    "LEFT OUTER JOIN biological_model_strain bmstr ON bmstr.biological_model_id = bm.id\n" +
                    "LEFT OUTER JOIN strain s ON s.acc = bmstr.strain_acc AND s.db_id = bmstr.strain_db_id\n" +
                    "LEFT OUTER JOIN biological_model_sample bms ON bms.biological_model_id = bm.id\n" +
                    "LEFT OUTER JOIN biological_sample bs ON bs.id = bms.biological_sample_id\n" +
                    "LEFT OUTER JOIN biological_model_allele bma ON bma.biological_model_id = bm.id\n" +
                    "LEFT OUTER JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id = bm.id\n" +
                    "WHERE\n" +
                    "    bm.db_id               = :bm_db_id               AND\n" +
                    "    bm.allelic_composition = :bm_allelic_composition AND\n" +
                    "    bm.genetic_background  = :bm_genetic_background  AND\n" +
                    "    bm.zygosity            = :bm_zygosity\n";

            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("bm_db_id", db_id);
            parameterMap.put("bm_allelic_composition", allelicComposition);
            parameterMap.put("bm_genetic_background", geneticBackground);
            parameterMap.put("bm_zygosity", zygosity);

            BiologicalModel bm = new BiologicalModel();
            Strain strain = new Strain();

            List<Map<String, Object>> listMap = jdbcCda.queryForList(query, parameterMap);
            for (Map<String, Object> map : listMap) {
                Object o;
                Datasource ds;
                DatasourceEntityId dsId;
                Allele bmAllele = new Allele();
                List<Allele> bmAlleleList = new ArrayList<>();
                GenomicFeature bmGf = new GenomicFeature();
                List<GenomicFeature> bmGfList = new ArrayList<>();
                Strain bmStrain;
                List<Strain> bmStrainList = new ArrayList<>();
                BiologicalSample bs;

                ds = new Datasource();
                o = map.get("bm_db_id");
                ds.setId(o == null ? 0 : Integer.parseInt(o.toString()));
                bm.setDatasource(ds);
                o = map.get("bm_allelic_composition");
                bm.setAllelicComposition(o == null ? "" : o.toString());
                o = map.get("bm_genetic_background");
                bm.setGeneticBackground(o == null ? "" : o.toString());
                o = map.get("bm_zygosity");
                bm.setZygosity(o == null ? "" : o.toString());

                bmStrain = new Strain();
                dsId = new DatasourceEntityId();
                o = map.get("bm_strain_acc");
                dsId.setAccession(o == null ? null : o.toString());
                o = map.get("bm_strain_db_id");
                dsId.setDatabaseId(o == null ? 0 : Integer.parseInt(o.toString()));
                bmStrain.setId(dsId);
                bmStrainList.add(bmStrain);
                bm.setStrains(bmStrainList);

                dsId = new DatasourceEntityId();
                o = map.get("bm_gf_acc");
                dsId.setAccession(o == null ? null : o.toString());
                o = map.get("bm_gf_db_id");
                dsId.setDatabaseId(o == null ? 0 : Integer.parseInt(o.toString()));
                bmGf.setId(dsId);
                bmGfList.add(bmGf);
                bm.setGenomicFeatures(bmGfList);

                dsId = new DatasourceEntityId();
                o = map.get("bm_allele_acc");
                dsId.setAccession(o == null ? null : o.toString());
                o = map.get("bm_allele_db_id");
                dsId.setDatabaseId(o == null ? 0 : Integer.parseInt(o.toString()));
                bmAllele.setId(dsId);
                bmAlleleList.add(bmAllele);
                bm.setAlleles(bmAlleleList);

                strain = new Strain();
                dsId = new DatasourceEntityId();
                o = map.get("strain_acc");
                dsId.setAccession(o == null ? null : o.toString());
                o = map.get("strain_db_id");
                dsId.setDatabaseId(o == null ? null : Integer.parseInt(o.toString()));
                strain.setId(dsId);
                OntologyTerm term = new OntologyTerm();
                dsId = new DatasourceEntityId();
                o = map.get("strain_biotype_acc");
                dsId.setAccession(o == null ? null : o.toString());
                o = map.get("strain_biotype_db_id");
                dsId.setDatabaseId(o == null ? 0 : Integer.parseInt(o.toString()));
                term.setId(dsId);
                strain.setBiotype(term);
                o = map.get("strain_name");
                strain.setName(o == null ? null : o.toString());

                bs = new BiologicalSample();
                o = map.get("bs.external_id");
                bs.setStableId(o == null ? null :o.toString());
                ds = new Datasource();
                o = map.get("bs_db_id");
                ds.setId(o == null ? 0 : Integer.parseInt(o.toString()));
                bs.setDatasource(ds);



                term = new OntologyTerm();
                dsId = new DatasourceEntityId();
                o = map.get("bs_sample_type_acc");
                dsId.setAccession(o == null ? null : o.toString());
                o = map.get("bs_sample_type_db_id");
                dsId.setDatabaseId(o == null ? 0 : Integer.parseInt(o.toString()));
                term.setId(dsId);
                bs.setType(term);




                o = map.get("bs_sample_group");
                bs.setGroup(o == null ? null : o.toString());
                o = map.get("bs_organisation_id");
                Organisation org = new Organisation();
                org.setId(o == null ? 0 : Integer.parseInt(o.toString()));
                bs.setOrganisation(org);
                org.setId(o == null ? 0 : Integer.parseInt(o.toString()));
                bs.setProductionCenter(org);
                List<BiologicalSample> samplesList = new ArrayList<>();
                samplesList.add(bs);
                bm.setBiologicalSamples(samplesList);
            }

            return new BioModelResults(bm_db_id, bm, strain);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BioModelResults that = (BioModelResults) o;

            if (bm_db_id != that.bm_db_id) return false;
            if (strain_db_id != that.strain_db_id) return false;
            if (strain_biotype_db_id != that.strain_biotype_db_id) return false;
            if (!bm_allelicComposition.equals(that.bm_allelicComposition)) return false;
            if (!bm_geneticBackground.equals(that.bm_geneticBackground)) return false;
            if (!bm_zygosity.equals(that.bm_zygosity)) return false;
            if (!strain_acc.equals(that.strain_acc)) return false;
            if (!strain_biotype_acc.equals(that.strain_biotype_acc)) return false;
            if (!strain_name.equals(that.strain_name)) return false;
            if (bm_gf_acc != null ? !bm_gf_acc.equals(that.bm_gf_acc) : that.bm_gf_acc != null) return false;
            if (bm_gf_db_id != null ? !bm_gf_db_id.equals(that.bm_gf_db_id) : that.bm_gf_db_id != null) return false;
            if (bm_allele_acc != null ? !bm_allele_acc.equals(that.bm_allele_acc) : that.bm_allele_acc != null) return false;
            if (bm_allele_db_id != null ? !bm_allele_db_id.equals(that.bm_allele_db_id) : that.bm_allele_db_id != null)
                return false;
            if (bs_external_id != null ? !bs_external_id.equals(that.bs_external_id) : that.bs_external_id != null)
                return false;
            if (bs_db_id != null ? !bs_db_id.equals(that.bs_db_id) : that.bs_db_id != null)
                return false;
            if (bs_sample_type_acc != null ? !bs_sample_type_acc.equals(that.bs_sample_type_acc) : that.bs_sample_type_acc != null)
                return false;
            if (bs_sample_type_db_id != null ? !bs_sample_type_db_id.equals(that.bs_sample_type_db_id) : that.bs_sample_type_db_id != null)
                return false;
            if (bs_sample_group != null ? !bs_sample_group.equals(that.bs_sample_group) : that.bs_sample_group != null)
                return false;
            if (bs_organisation_id != null ? !bs_organisation_id.equals(that.bs_organisation_id) : that.bs_organisation_id != null)
                return false;
            return bs_production_center_id != null ? bs_production_center_id.equals(that.bs_production_center_id) : that.bs_production_center_id == null;
        }

        @Override
        public int hashCode() {
            int result = bm_db_id;
            result = 31 * result + bm_allelicComposition.hashCode();
            result = 31 * result + bm_geneticBackground.hashCode();
            result = 31 * result + bm_zygosity.hashCode();
            result = 31 * result + strain_acc.hashCode();
            result = 31 * result + strain_db_id;
            result = 31 * result + strain_biotype_acc.hashCode();
            result = 31 * result + strain_biotype_db_id;
            result = 31 * result + strain_name.hashCode();
            result = 31 * result + (bm_gf_acc != null ? bm_gf_acc.hashCode() : 0);
            result = 31 * result + (bm_gf_db_id != null ? bm_gf_db_id.hashCode() : 0);
            result = 31 * result + (bm_allele_acc != null ? bm_allele_acc.hashCode() : 0);
            result = 31 * result + (bm_allele_db_id != null ? bm_allele_db_id.hashCode() : 0);
            result = 31 * result + (bs_external_id != null ? bs_external_id.hashCode() : 0);
            result = 31 * result + (bs_db_id != null ? bs_db_id.hashCode() : 0);
            result = 31 * result + (bs_sample_type_acc != null ? bs_sample_type_acc.hashCode() : 0);
            result = 31 * result + (bs_sample_type_db_id != null ? bs_sample_type_db_id.hashCode() : 0);
            result = 31 * result + (bs_sample_group != null ? bs_sample_group.hashCode() : 0);
            result = 31 * result + (bs_organisation_id != null ? bs_organisation_id.hashCode() : 0);
            result = 31 * result + (bs_production_center_id != null ? bs_production_center_id.hashCode() : 0);
            return result;
        }
    }

    private BioModelResults getLineLevelExpectedResults() {
        return new BioModelResults(22,
                                   "Gsk3a<tm1a(EUCOMM)Wtsi>/Gsk3a<tm1a(EUCOMM)Wtsi>",
                                   "involves: C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac",
                                   "homozygote",
                                   "MGI:2152453", 3,
                                   "MGI:4434136", 3,
                                   "IMPC-CURATE-C44BE", 22,
                                   "IMPC-CURATE-C44BE", 22, "CV:00000051", 3, "C57BL/6Brd-Tyr<c-Brd>;C57BL/6N;C57BL/6NTac",
                                   null, null, null, null, null, null, null);
    }
}