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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.integration.data.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

/**
 * This is an end-to-end integration data test class that uses an in-memory database to populate a small dcc, cda_base,
 * and cda set of databases.
 */

@RunWith(SpringRunner.class)
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

//        Resource r = context.getResource("classpath:sql/h2/dataIntegrationTest-data.sql");
//        ScriptUtils.executeSqlScript(ds.getConnection(), r);






    }
}