/** *****************************************************************************
 * Copyright 2017 QMUL - Queen Mary University of London
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
 ****************************************************************************** */
package uk.ac.ebi.phenotype.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import net.sf.json.JSONObject;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * A service for executing solr queries, using caching
 *
 */
@Service
public class QueryBrokerService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Autowired
    private SolrIndex solrIndex;

    // Use cache to manage queries for minimizing network traffic    
    final int CACHE_SIZE = 500; 

    /**
     * A map implementing a cache of associations: url->result
     *
     * Implementation copied from QueryBrokerController.
     *
     */
    @SuppressWarnings("unchecked")
    Map<String, Object> cache = (Map<String, Object>) Collections.synchronizedMap(new LinkedHashMap<String, Object>(CACHE_SIZE + 1, .75F, true) {
        private static final long serialVersionUID = 1L;

        // This method is called just after a new entry has been added
        public boolean removeEldestEntry(Map.Entry eldest) {
            return size() > CACHE_SIZE;
        }
    });

    /**
     * Create a Json object by running multiple solr queries and recording their
     * results.
     *
     * @param subfacet
     * @param queries
     *
     * a simple map with solr queries. Keys will appear in the output. Strings
     * associated to those keys should hold entire solr queries (containing url
     * and full select string)
     *
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject runQueries(String subfacet, JSONObject queries) throws IOException, URISyntaxException {

        JSONObject result = new JSONObject();

        Iterator keys = queries.keys();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String url = queries.getString(key);
            
            if (!cache.containsKey(url)) {
                //LOGGER.info("running de novo: " + key + ": " + url);                
                JSONObject json = solrIndex.getResults(url);
                if (subfacet == null) {
                    // Main facet counts only
                    int numFound = json.getJSONObject("response").getInt("numFound");
                    result.put(key, numFound);
                    cache.put(url, numFound);
                } else {
                    JSONObject j = new JSONObject();
                    j.put("response", json.getJSONObject("response"));
                    j.put("facet_counts", json.getJSONObject("facet_counts"));
                    result.put(key, j);
                    cache.put(url, j);
                }
            } else {
                LOGGER.info("Using cache for key: " + url);
                result.put(key, cache.get(url));                
            }
        }
        return result;
    }

    /**
     * Run a single query, cache and return the result
     *
     * @param url
     *
     * a single url with a solr query, must include http:// etc.
     *
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject runQuery(String url) throws IOException, URISyntaxException {

        // Perhaps use cached result
        if (cache.containsKey(url)) {
            LOGGER.info("Using cache for key: " + url);
            return (JSONObject) cache.get(url);
        }

        LOGGER.info("running de novo: " + url);

        // look up request 
        JSONObject json = solrIndex.getResults(url);

        // extract just the relevant parts of the result 
        // (this omits items like the header)
        JSONObject result = new JSONObject();
        result.put("response", json.getJSONObject("response"));
        result.put("facet_counts", json.getJSONObject("facet_counts"));
        cache.put(url, result);

        return result;
    }

}
