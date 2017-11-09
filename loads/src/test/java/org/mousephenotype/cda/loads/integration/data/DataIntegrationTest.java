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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Base64;

@RunWith(SpringRunner.class)
@WebAppConfiguration
//@ContextConfiguration(classes = AppDevConfig.class)
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


    /**
     * This is an end-to-end integration data test clas that uses an in-memory database to populate a small dcc, cda_base,
     * and cda set of databases. It tests the Atk2 gene from dcc import to cda load, insuring that the background strain
     * is the same for both controls and mutants:
     *      Load Akt2.xml file
     *
     *     register interest (using DEV web service)
     *     unregister interest (using DEV web service)
     *     generate
     *     send
     * @throws Exception
     */
//    @Ignore
    @Test
    public void testAkt2() throws Exception {
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