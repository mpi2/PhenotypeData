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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

public class RegisterInterestUtils {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private HttpEntity<String> httpEntityHeaders;
	private Boolean            loggedIn = false;
	private String             riBaseUrl;


	@Inject
	public RegisterInterestUtils(
			String riBaseUrl
	) {
		this.riBaseUrl = riBaseUrl;
	}



    public Cookie getCookie(HttpServletRequest request, String cookieName) {

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie;
                }
            }
        }

        return null;
    }

    /**
     *
     * @return a list of the currently logged in user's gene interest mgi accession ids
     */
    public List<String> getGeneAccessionIds() {

        ResponseEntity<List<String>> response = new RestTemplate().exchange(riBaseUrl + "/api/summary/list", HttpMethod.GET, httpEntityHeaders,
                                                                            new ParameterizedTypeReference<List<String>>() { }, Collections.emptyMap());
        return response.getBody();
    }

	public  boolean loggedIn(HttpServletRequest request) {

        Cookie cookie = getCookie(request, "RISESSIONID");

        if (cookie != null) {

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", "JSESSIONID=" + cookie.getValue());
            httpEntityHeaders = new HttpEntity<>(headers);

            ResponseEntity<List<String>> response = new RestTemplate().exchange(riBaseUrl + "/api/roles", HttpMethod.GET, httpEntityHeaders,
                                                                                new ParameterizedTypeReference<List<String>>() {
                                                                                }, Collections.emptyMap());
            List<String> roles = response.getBody();

            loggedIn = (roles.contains("ROLE_USER")) || (roles.contains("ROLE_ADMIN"));
        }

		return loggedIn;
	}

	/**
	 * Register currently logged in user for interest in {@code geneAccessionId}
	 *
	 * @param geneAccessionId
	 * @return String returned by Register Interest web service 'register' action. An empty string indicates success.
	 */
	public  String registerGene(HttpServletRequest request, String geneAccessionId) {
		String retVal = "User not logged in.";

		if (loggedIn(request)) {
			ResponseEntity<String> response = new RestTemplate().exchange(riBaseUrl + "/api/registration/gene?geneAccessionId=" + geneAccessionId,
																		  HttpMethod.POST, httpEntityHeaders, String.class);
			retVal = response.getBody() == null ? "" : response.getBody();
		}

		return retVal;
	}

	/**
	 * Unregister currently logged in user for interest in {@code geneAccessionId}
	 *
	 * @param geneAccessionId
	 * @return String returned by Register Interest web service 'unregister' action. An empty string indicates success.
	 */
	public  String unregisterGene(HttpServletRequest request, String geneAccessionId) {
		String retVal = "User not logged in.";

		if (loggedIn(request)) {
			ResponseEntity<String> response = new RestTemplate().exchange(riBaseUrl + "/api/unregistration/gene?geneAccessionId=" + geneAccessionId,
																		  HttpMethod.DELETE, httpEntityHeaders, String.class);
			retVal = response.getBody() == null ? "" : response.getBody();
		}

		return retVal;
	}
}