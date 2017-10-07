/** *****************************************************************************
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
 ****************************************************************************** */
package uk.ac.ebi.phenotype.web.controller;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * TK: Description of class?
 *
 *
 */
@Controller
public class QueryBrokerController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Autowired
    private SolrIndex solrIndex;

    // Use cache to manage queries for minimizing network traffic
    // final int MAX_ENTRIES = 600; 
    final int MAX_ENTRIES = 0; // temporarily disable cache for testing

    @SuppressWarnings("unchecked")
    Map<String, Object> cache = (Map<String, Object>) Collections.synchronizedMap(new LinkedHashMap<String, Object>(MAX_ENTRIES + 1, .75F, true) {
        private static final long serialVersionUID = 1L;

        // This method is called just after a new entry has been added
        public boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_ENTRIES;
        }
    });

    @RequestMapping(value = "/fetchDefaultCore", method = RequestMethod.GET)
    @ResponseBody
    public String fetchDefaultCore(
            @RequestParam(value = "q", required = true) String query,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException {

        String solrParams = "&rows=0&wt=json&&defType=edismax&qf=auto_suggest&facet.field=docType&facet=on&facet.limit=-1&facet.mincount=1";

        String solrurl = SolrUtils.getBaseURL(solrIndex.getSolrServer("autosuggest")) + "/select?q=" + query + solrParams; // not working, points to omero image baseurl

        LOGGER.info("fetchDefaultCore: url: " + solrurl);
        JSONObject json = solrIndex.getResults(solrurl);

        JSONArray docCount = json.getJSONObject("facet_counts").getJSONObject("facet_fields").getJSONArray("docType");
        Map<String, Integer> dc = new HashMap<>();
        for (int i = 0; i < docCount.size(); i = i + 2) {
            dc.put(docCount.get(i).toString(), (Integer) docCount.get(i + 1));
        }

        // priority order of facet to be opened based on search result
        String defaultCore = "gene";
        if (dc.containsKey("gene")) {
            defaultCore = "gene";
        } else if (dc.containsKey("mp")) {
            defaultCore = "mp";
        } else if (dc.containsKey("disease")) {
            defaultCore = "disease";
        } else if (dc.containsKey("anatomy")) {
            defaultCore = "anatomy";
        } else if (dc.containsKey("impc_images")) {
            defaultCore = "impc_images";
        }

        LOGGER.info("fetchDefaultCore: default core: " + defaultCore);
        return defaultCore;
    }

    /**
     * Examine or clear cached SOLR queries
     *
     * @param clearCache true to clear the cache, false to examine the cached
     * keys
     */
    @RequestMapping(value = "/querybroker", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> clearCache(
            @RequestParam(value = "clearCache", required = false) Boolean clearCache) {

        JSONObject jsonResponse = new JSONObject();

        if (clearCache != null && clearCache == true) {
            jsonResponse.put("Details", cache.keySet().size() + " cleared from cache");
            cache.clear();
        } else {
            jsonResponse.put("Details", cache.keySet().size() + " entries in cache");
            jsonResponse.put("Cached Keys", cache.keySet());
        }

        return new ResponseEntity<>(jsonResponse, createResponseHeaders(), HttpStatus.CREATED);
    }

    /**
     * Return multiple solr json responses from server to avoid multiple calls
     * from client Using cache to further reduce queries to the SOLR server
     *
     * @param solrParams
     * @param subfacet
     * @param response
     * @param model     
     * @param request
     * @return @throws URISyntaxException
     * @throws IOException
     */
    @RequestMapping(value = "/querybroker", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> jsons(
            @RequestParam(value = "q", required = true) String solrParams,
            @RequestParam(value = "subfacet", required = false) String subfacet,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) throws IOException, URISyntaxException {

        JSONObject jParams = (JSONObject) JSONSerializer.toJSON(solrParams);
        JSONObject jsonResponse = createJsonResponse(subfacet, jParams);

        return new ResponseEntity<>(jsonResponse, createResponseHeaders(), HttpStatus.CREATED);
    }

    private HttpHeaders createResponseHeaders() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    /**
     * Create a Json object by running multiple solr queries and recording their results.
     * 
     * Consider replacing this function by runCoreQueries with a similar signature.
     * This function looks up solr urls, the other function assumes all the query
     * urls are given.
     * 
     * @param subfacet
     * @param jParams
     * @return
     * @throws IOException
     * @throws URISyntaxException 
     */
    public JSONObject createJsonResponse(String subfacet, JSONObject jParams) throws IOException, URISyntaxException {

        JSONObject jsonResponse = new JSONObject();

        Iterator cores = jParams.keys();

        while (cores.hasNext()) {
            String core = (String) cores.next();
            String param = jParams.getString(core);            
            
            String url = SolrUtils.getBaseURL(solrIndex.getSolrServer(core)) + "/select?" + param;
            LOGGER.info("createJsonResponse url: " + url);
            String key = core + param;
            Object o = cache.get(key);

            if (o == null && !cache.containsKey(key)) {
                // Object not in cache. If null is not a possible value in the cache,
                // the call to cache.contains(key) is not needed
                JSONObject json = solrIndex.getResults(url);
                if (subfacet == null) {
                    // Main facet counts only
                    int numFound = json.getJSONObject("response").getInt("numFound");
                    jsonResponse.put(core, numFound);
                    cache.put(key, numFound);
                } else {
                    JSONObject j = new JSONObject();
                    j.put("response", json.getJSONObject("response"));
                    j.put("facet_counts", json.getJSONObject("facet_counts"));
                    jsonResponse.put(core, j);
                    cache.put(key, j);
                }
            } else {
                jsonResponse.put(core, o);
                LOGGER.info("createJsonResponse: Using cache for key: " + key);
            }
        }
        return jsonResponse;
    }

    
    /**
     * Create a Json object by running multiple solr queries and recording their results.
     * 
     * @param subfacet
     * @param queries
     * 
     * a simple map with solr queries. Keys will appear in the output. 
     * Strings associated to those keys should hold entire solr queries (containing
     * url and full select string)
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
            LOGGER.info("runCoreQueries url: " + url);
                        
            if (!cache.containsKey(url)) {
                // Object not in cache. 
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
                result.put(key, cache.get(url));
                LOGGER.info("createJsonResponse: Using cache for key: " + url);
            }
        }
        return result;
    }
    
}
