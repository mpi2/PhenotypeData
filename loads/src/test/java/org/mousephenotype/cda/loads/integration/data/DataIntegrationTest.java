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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.create.extract.dcc.ExtractDccExperiments;
import org.mousephenotype.cda.loads.create.extract.dcc.ExtractDccSpecimens;
import org.mousephenotype.cda.loads.create.load.LoadExperiments;
import org.mousephenotype.cda.loads.create.load.LoadSpecimens;
import org.mousephenotype.cda.loads.integration.data.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is an end-to-end integration data test class that uses an in-memory database to populate a small dcc, cda_base,
 * and cda set of databases.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan
@ContextConfiguration(classes = TestConfig.class)
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
    private DataSource cdaDataSource;

    @Autowired
    private CdaSqlUtils cdaSqlUtils;

    @Autowired
    private NamedParameterJdbcTemplate jdbcCda;



    @Autowired
    private ExtractDccSpecimens extractDccSpecimens;

    @Autowired
    private ExtractDccExperiments extractDccExperiments;

    @Autowired
    private LoadSpecimens loadSpecimens;

    @Autowired
    private LoadExperiments loadExperiments;




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
//    @Ignore
    @Test
    public void testBackgroundStrainIsEqual() throws Exception {

        Resource specimenResource = context.getResource("classpath:xml/akt2Specimens.xml");
        Resource experimentResource = context.getResource("classpath:xml/akt2Experiment.xml");

        String[] extractSpecimenArgs = new String[] {
                "--datasourceShortName=EuroPhenome",
                "--filename=" + specimenResource.getFile().getAbsolutePath()
                };

        String[] extractExperimentArgs = new String[] {
                "--datasourceShortName=EuroPhenome",
                "--filename=" + experimentResource.getFile().getAbsolutePath()
        };

        String[] loadArgs = new String[] {
                "--profile=dev",
        };

        System.out.println("extractDccSpecimens");
        extractDccSpecimens.run(extractSpecimenArgs);

        System.out.println("extractDccExperiments");
        extractDccExperiments.run(extractExperimentArgs);

        System.out.println("loadSpecimens");
        loadSpecimens.run(loadArgs);

        System.out.println("loadExperiments");
        loadExperiments.run(loadArgs);

        List<List<String>> results = getSpecimenStrainAccs();
        List<String> controlList = results.get(0);
        List<String> mutantList = results.get(1);

        if ((controlList.get(0) != mutantList.get(0)) || (controlList.get(1) != mutantList.get(1))) {
            Assert.fail("control and mutant strains differ:\n\tcontrol: " + controlList.get(0) + "::" + controlList.get(1) +
                                                          "\n\tmutant:  " + mutantList.get(0) + "::" + mutantList.get(1));
        }





//        List<SpecimenExtended> specimens = dccSqlUtils.getSpecimens();
//        System.out.println("Specimens:");
//        for (SpecimenExtended specimen : specimens) {
//            System.out.println("\tdatasourceShortName: " + specimen.getDatasourceShortName() + ", " + specimen.getSpecimen());
//        }
//
//
//        List<DccExperimentDTO> experiments = dccSqlUtils.getExperiments();
//        System.out.println("Experiments:");
//        for (DccExperimentDTO experiment : experiments) {
//
//            System.out.println("\t" + experiment);
//
//            List<SimpleParameter> simpleParameters = dccSqlUtils.getSimpleParameters(experiment.getDcc_procedure_pk());
//            for (SimpleParameter simpleParameter: simpleParameters) {
//                System.out.println("\t" + simpleParameter.getParameterID() + " :: " + simpleParameter.getValue());
//            }
//        }
    }


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
}