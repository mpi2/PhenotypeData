/*******************************************************************************
 *  Copyright © 2018 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.ri.core.services;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.ri.BaseTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by mrelac on 16/08/2018.
 */

// FIXME FIXME FIXME


@RunWith(SpringRunner.class)
public class GenerateServiceTest extends BaseTest {

    @Value("${username:}")
    private String username;

    @Value("${password:}")
    private String password;


//    private String authStringEncoded ;
//    String baseUrl = "https://wwwdev.ebi.ac.uk/mi/impc/dev/interest/contacts";
//    private ClientResponse response;

//    public static void main(String[] args) throws Exception {
//        SpringApplication app = new SpringApplication(GenerateServiceTest.class);
//        app.setBannerMode(Banner.Mode.OFF);
//        app.setLogStartupInfo(false);
//        app.setWebEnvironment(false);
//        app.run(args);
//    }

@Ignore
    @Test
    public void myTest() {


//    @Override
//    public void run(String... args) throws Exception {

        String url;

//        initialise();
//
//        // Perform get.
//        url = baseUrl + "?email=mrelac@ebi.ac.uk";
//        response = performGet(url);
//        if (response.getStatus() != 200) {
//            System.err.println("Server status: " + response.getStatus() + ". " + response.getEntity(String.class));
//            System.err.println("URL: " + url);
//            System.exit(1);
//        } else {
//            String output = response.getEntity(String.class);
//            System.out.println("response: " + output);
//        }
//
//
//        // Perform POST.
//        url = baseUrl + "?email=mrelac@ebi.ac.uk&gene=MGI:104874&type=gene";
//        response = performPost(url);
//        if (response.getStatus() != 200) {
//            System.err.println("Server status: " + response.getStatus() + ". " + response.getEntity(String.class));
//            System.err.println("URL: " + url);
//            System.exit(1);
//        } else {
//            String output = response.getEntity(String.class);
//            System.out.println("response: " + output);
//        }
//
//
//        // Perform DELETE.
//        url = baseUrl + "?email=mrelac@ebi.ac.uk&gene=MGI:104874&type=gene";
//        response = performDelete(url);
//        if (response.getStatus() != 200) {
//            System.err.println("Server status: " + response.getStatus() + ". " + response.getEntity(String.class));
//            System.err.println("URL: " + url);
//            System.exit(1);
//        } else {
//            String output = response.getEntity(String.class);
//            System.out.println("response: " + output);
//        }

    }


    // PRIVATE METHODS


//    private void initialise() {
//
//        if ((username == null) || (username.isEmpty()) || (password ==  null) || password.isEmpty()) {
//            System.err.println("Please provide --username=xxx --password=yyy");
//            System.exit(1);
//        }
//
//        String authString = username + ":" + password;
//        authStringEncoded = Base64.getEncoder().encodeToString(authString.getBytes());
////        System.out.println("Base64 encoded auth string: " + authStringEncoded);
//    }
//
//    private ClientResponse performGet(String url) {
//
//        Client         restClient = Client.create();
//        WebResource    webResource = restClient.resource(url);
//        ClientResponse response = webResource
//                .accept("application/json")
//                .header("Authorization", "Basic " + authStringEncoded)
//                .get(ClientResponse.class);
//
//        return response;
//    }
//
//    private ClientResponse performPost(String url) {
//
//        Client         restClient = Client.create();
//        WebResource    webResource = restClient.resource(url);
//        ClientResponse response = webResource
//                .accept("application/json")
//                .header("Authorization", "Basic " + authStringEncoded)
//                .post(ClientResponse.class);
//
//        return response;
//    }
//
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