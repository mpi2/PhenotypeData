/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package uk.ac.ebi.phenotype.generic.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RegisterInterestUtils {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private HttpEntity<String> httpEntityHeaders;
	private String             riBaseUrl;


	@Inject
	public RegisterInterestUtils(
			String riBaseUrl
	) {
		this.riBaseUrl = riBaseUrl;
	}


    /**
     *
     * @return a list of the currently logged in user's gene interest mgi accession ids
     */
    public Map<String, List<String>> getGeneAccessionIds() {

        ResponseEntity<Map<String, List<String>>> response = new RestTemplate().exchange(riBaseUrl + "/api/summary/list", HttpMethod.GET, httpEntityHeaders,
                                                                                         new ParameterizedTypeReference<Map<String, List<String>>>() { }, Collections.emptyMap());
        return response.getBody();
    }

	public boolean isLoggedIn(HttpServletRequest request) {

        // Use the web service to check if we're logged in.

        httpEntityHeaders = new HttpEntity<>(buildHeadersFromRiToken(request));

        ResponseEntity<List<String>> response = new RestTemplate().exchange(riBaseUrl + "/api/roles", HttpMethod.GET, httpEntityHeaders,
                                                                            new ParameterizedTypeReference<List<String>>() {
                                                                            }, Collections.emptyMap());
        List<String> roles = response.getBody();

        boolean loggedIn = (roles.contains("ROLE_USER")) || (roles.contains("ROLE_ADMIN"));

        return loggedIn;
	}

    /**
     * Register currently logged in user for interest in {@code geneAccessionId}
     *
     * @param geneAccessionId
     * @return String returned by Register Interest web service 'register' action. An empty string indicates success.
     */
    public String registerGene(HttpServletRequest request, HttpServletResponse response, String geneAccessionId) {

        if ( ! isLoggedIn(request)) {
            return "User not logged in.";
        }

        // Use the web service to register interest in gene.
        httpEntityHeaders = new HttpEntity<>(buildHeadersFromRiToken(request));

        ResponseEntity<String> restResponse = new RestTemplate().exchange(riBaseUrl + "/api/registration/gene?geneAccessionId=" + geneAccessionId,
                                                                          HttpMethod.POST, httpEntityHeaders, String.class);

        return restResponse.getBody() == null ? "" : restResponse.getBody();
    }

    public static HttpHeaders buildHeadersFromRiToken(HttpServletRequest request) {

        String riToken = (String) request.getSession().getAttribute("riToken");
        String      cookieNameValuePair  = "JSESSIONID=" + riToken;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", cookieNameValuePair);

        return headers;
    }

	/**
	 * Unregister currently logged in user for interest in {@code geneAccessionId}
	 *
	 * @param geneAccessionId
	 * @return String returned by Register Interest web service 'unregister' action. An empty string indicates success.
	 */
	public  String unregisterGene(HttpServletRequest request, String geneAccessionId) {

        if ( ! isLoggedIn(request)) {
            return "User not logged in.";
        }

        // Use the web service to unregister.
        httpEntityHeaders = new HttpEntity<>(buildHeadersFromRiToken(request));

        ResponseEntity<String> restResponse = new RestTemplate().exchange(riBaseUrl + "/api/unregistration/gene?geneAccessionId=" + geneAccessionId,
                                                                          HttpMethod.DELETE, httpEntityHeaders, String.class);

        return restResponse.getBody() == null ? "" : restResponse.getBody();
	}
}