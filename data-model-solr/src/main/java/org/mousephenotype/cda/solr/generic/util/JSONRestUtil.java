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
package org.mousephenotype.cda.solr.generic.util;

import org.mousephenotype.cda.utilities.HttpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

@Component
public class JSONRestUtil {


	private final static Logger log = LoggerFactory.getLogger(JSONRestUtil.class);

	/**
	 * Get the results of a query from the provided url. make sure the url requests JSON!!!
	 *
	 * @param url
	 *            the URL from which to get the content
	 * @return a JSONObject representing the result of the query
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static JSONObject getResults(String url) throws IOException, URISyntaxException, JSONException {

		log.debug("GETTING CONTENT FROM: " + url);

		HttpProxy proxy = new HttpProxy();
		String content = proxy.getContent(new URL(url));

		return new JSONObject(content);
	}
	
	public static JSONArray getResultsArray(String url) throws IOException, URISyntaxException, JSONException {

		log.debug("GETTING CONTENT FROM: " + url);

		HttpProxy proxy = new HttpProxy();
		String content = proxy.getContent(new URL(url));

		return new JSONArray(content);
	}

	public static int getNumberFoundFromJsonResponse(JSONObject response) throws JSONException {
		int numberFound = (int) response.getJSONObject("response").getInt("numFound");
		return numberFound;
	}

	public static JSONArray getDocArray(JSONObject jsonResponse) throws JSONException {
		JSONArray docs = jsonResponse.getJSONObject(
				"response").getJSONArray("docs");
		return docs;
	}
}