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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.common.config.DataSourcesConfigApp;
import org.mousephenotype.cda.loads.create.extract.dcc.ExtractDccSpecimens;
import org.mousephenotype.cda.loads.integration.data.config.TestConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.sql.DataSource;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * This is an end-to-end integration data test class that uses an in-memory database to populate a small dcc, cda_base,
 * and cda set of databases.
 */

@RunWith(SpringRunner.class)
@ComponentScan
@ContextConfiguration(classes = TestConfig.class)

//@ComponentScan(value = {"org.mousephenotype.cda.loads.integration.data"} , excludeFilters = {
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.mousephenotype.cda.loads.common.config.DataSourcesConfigApp.class),
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.mousephenotype.cda.loads.create.extract.cdabase.config.ExtractCdabaseConfigBeans.class),
//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = org.mousephenotype.cda.loads.create.extract.dcc.config.ExtractDccConfigBeans.class)
//})



public class DataIntegrationTest {

//    private String authStringEncoded ;
//    String baseUrl = "https://wwwdev.ebi.ac.uk/mi/impc/dev/interest/contacts";
//    private ClientResponse response;
//    private String username = "ri-admin";
//
//
//    @Autowired
//    private NamedParameterJdbcTemplate jdbc;
//
//    @Autowired
//    private ApplicationGenerate applicationGenerate;
//
//    @Autowired
//    private ApplicationSend applicationSend;
//
//    @Autowired
//    private String riPassword;

//    @Autowired
//    private ExtractDccSpecimens extractDccSpecimens;

//    @Autowired
//    private NamedParameterJdbcTemplate jdbcMike;

    @Autowired
    private DataSource dccDataSource;

    @Autowired
    private DccSqlUtils dccSqlUtils;


@Before
public void initialise() {
//    DataSourcesConfigApp a = new MockitoJUnit();


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
//    @Ignore
    @Test
    public void testBackgroundStrainIsEqual() throws Exception {

        Map<String, Object> parameterMap = new HashMap<>();

//        SqlRowSet rs = jdbcMike.queryForRowSet("SELECT * FROM mike", parameterMap);
//        while (rs.next()) {
//            System.out.println("c1 = " + rs.getInt("c1"));
//        }

        dccSqlUtils.getDbName();


//
//        String[] emails           = new String[]{"mrelac@ebi.ac.uk", "mike@foxhill.com"};
//        String[] geneAccessionIds = new String[]{"MGI:1919199", "MGI:102851"};
//
//        initialise();
//
//        extractGenes();
//        for (int i = 0; i < emails.length; i++) {
//            register(emails[i], geneAccessionIds[i]);
//            unregister(emails[i], geneAccessionIds[i]);
//        }
//        String[] args = new String[0];
//        applicationGenerate.run(args);
//        applicationSend.run(args);
    }


    // PRIVATE METHODS


//    private void initialise() {
//
//        System.out.println("INITIALISING WEB SERVICE");
//        if ((username == null) || (username.isEmpty()) || (riPassword ==  null) || riPassword.isEmpty()) {
//            System.err.println("Please provide --username=xxx --password=yyy");
//            System.exit(1);
//        }
//
//        String authString = username + ":" + riPassword;
//        authStringEncoded = Base64.getEncoder().encodeToString(authString.getBytes());
////        System.out.println("Base64 encoded auth string: " + authStringEncoded);
//
//        cleanDatabase();
//    }
//
//    private void cleanDatabase() {
//        ParameterMap<String, Object> parameterMap = new ParameterMap<>();
//
//        jdbc.update("DELETE FROM log;", parameterMap);
//        jdbc.update("DELETE FROM gene_sent;", parameterMap);
//        jdbc.update("DELETE FROM gene_contact;", parameterMap);
//        jdbc.update("DELETE FROM contact;", parameterMap);
//    }
//
//    private void extractGenes() {
//
//    }
//
//
//    // Web Service support routines
//
//
//
//    private void register(String email, String geneAccessionId) {
//
//        System.out.println("REGISTERING " + email + ", " + geneAccessionId);
//
//        // Perform POST.
//        String url = baseUrl + "?email=" + email + "&gene=" + geneAccessionId + "&type=gene";
//        response = performPost(url);
//        if (response.getStatus() != 200) {
//            System.err.println("Server status: " + response.getStatus() + ". " + response.getEntity(String.class));
//            System.err.println("URL: " + url);
//        } else {
//            String output = response.getEntity(String.class);
//            System.out.println("response: " + output);
//        }
//    }
//
//    private void unregister(String email, String geneAccessionId) {
//
//        System.out.println("UNREGISTERING " + email + ", " + geneAccessionId);
//
//        // Perform DELETE.
//        String url = baseUrl + "?email=" + email + "&gene=" + geneAccessionId + "&type=gene";
//        response = performDelete(url);
//        if (response.getStatus() != 200) {
//            System.err.println("Server status: " + response.getStatus() + ". " + response.getEntity(String.class));
//            System.err.println("URL: " + url);
//        } else {
//            String output = response.getEntity(String.class);
//            System.out.println("response: " + output);
//        }
//    }
//
//    private ClientResponse performPost(String url) {
//
//        Client      restClient  = Client.create();
//        WebResource webResource = restClient.resource(url);
//        ClientResponse response = webResource
//                .accept("application/json")
//                .header("Authorization", "Basic " + authStringEncoded)
//                .post(ClientResponse.class);
//
//        return response;
//    }
//
//    private ClientResponse performDelete(String url) {
//
//        Client         restClient = Client.create();
//        WebResource    webResource = restClient.resource(url);
//        ClientResponse response = webResource
//                .accept("application/json")
//                .header("Authorization", "Basic " + authStringEncoded)
//                .delete(ClientResponse.class);
//
//        return response;
//    }
}