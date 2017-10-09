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

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenotype.service.QueryBrokerService;
import uk.ac.ebi.phenotype.service.datatable.DataTableService;
import uk.ac.ebi.phenotype.service.datatable.DataTableServiceFactory;
import uk.ac.ebi.phenotype.service.search.SearchUrlService;
import uk.ac.ebi.phenotype.service.search.SearchUrlServiceFactory;
import uk.ac.ebi.phenotype.util.SearchSettings;

/**
 * Controller for search queries.
 *
 * Processed urls containing /search and looks up results from solr cores
 *
 */
@Controller
public class SearchController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    @Autowired
    private SearchUrlServiceFactory urlFactory;

    @Autowired
    private DataTableServiceFactory tableFactory;

    @Autowired
    private QueryBrokerService queryBrokerService;

    /**
     * redirect calls to the base url or /search/ path to the search page
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/index.html", "/search/"})
    public String searchForward(HttpServletRequest request) {
        String redirectUrl = request.getScheme() + ":" + request.getAttribute("mappedHostname") + request.getAttribute("baseUrl") + "/search";
        return "redirect:" + redirectUrl;
    }

    /**
     * Process a generic entry onto the search page. As default behavior, this
     * looks up all genes.
     *
     * @param request
     * @param model
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    @RequestMapping("/search")
    public String searchAll(
            HttpServletRequest request,
            Model model) throws IOException, URISyntaxException {

        SearchSettings settings = new SearchSettings("gene", "*", null, request);
        processSearch(settings, model);
        return "search";
    }

    /**
     * Primary gateway to searching on the web portal.
     *
     *
     * @param dataType
     *
     * The primary type of objects of interest. Use one of gene, disease, mp,
     * etc. that correspond to the main-level tabs on the website
     *
     * @param query
     *
     * The search query
     *
     * @param fqStr
     *
     * Optional filtering of the query results. This is activated when user
     * chooses to filter a result, e.g. filter diseases by disease source
     *
     * @param iDisplayStart
     *
     * Determines whether to return the best hits, or hits starting from a
     * lower-down rank
     *
     * @param iDisplayLength
     *
     * Determines the number of hits to return
     *
     * @param showImgView
     *
     * a boolean that matters for image search only
     *
     * @param request
     *
     * object carries all the url parameters. The function will look up (when
     * necessary iDisplayStart (starting hit to return) and iDisplayLength
     * (number of hits to display). The iDisplay(Start/Length) parameters are
     * used when fetching non-top hits.
     *
     * @param model
     *
     * object that is passed on to the web-page generating scripts. The function
     * fills this object.
     *
     * @return
     *
     * name of view used to generate output web page
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    @RequestMapping("/search/{dataType}")
    public String searchResult(
            @PathVariable() String dataType,
            @RequestParam(value = "kw", required = false, defaultValue = "*") String query,
            @RequestParam(value = "fq", required = false) String fqStr,
            @RequestParam(value = "iDisplayStart", required = false) Integer iDisplayStart,
            @RequestParam(value = "iDisplayLength", required = false) Integer iDisplayLength,
            @RequestParam(value = "showImgView", required = false) boolean showImgView,
            HttpServletRequest request,
            Model model) throws IOException, URISyntaxException {

        // encode the parsed search settings into an object
        SearchSettings settings = new SearchSettings(dataType, query, fqStr, request);
        settings.setImgView(showImgView);
        settings.setDisplay(iDisplayStart, iDisplayLength);
        System.out.println("\n\n\n");
        LOGGER.info(settings.toString());

        processSearch(settings, model);

        return "search";
    }

    /**
     * Perform two-step processing of a search. The first step is to count hits
     * in various facets/categories. The second step is to retrieve detailed
     * hits in one of the categories.
     *
     * @param settings
     * @param model
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private void processSearch(SearchSettings settings, Model model) throws IOException, URISyntaxException {

        // fetch counts of hits in broad categories (used in webpage in tab headings)        
        JSONObject facetCounts = getMainFacetCounts(settings);
        model.addAttribute("facetCount", facetCounts);
        model.addAttribute("searchQuery", settings.getQuery().replaceAll("\\\\", ""));
        model.addAttribute("dataType", settings.getDataType());
        //LOGGER.info("facetCountJsonResponse: new: " + facetCounts.toString(1));

        // create an object that will create query urls
        SearchUrlService urlservice = urlFactory.getService(settings.getDataType());
        model.addAttribute("dataTypeLabel", urlservice.breadcrumb());
        model.addAttribute("gridHeaderListStr", urlservice.gridHeadersStr());
        // perform the query, i.e. gather the hits from solr
        JSONObject searchHits = fetchSearchResult(urlservice, settings, true);
        //LOGGER.info("fetchSearchResultNew gave result:\n" + searchHits.toString(2));

        // transform the results from solr into another json/string.
        // The transformed object will be sent to the web-page generating scripts
        DataTableService tableService = tableFactory.getService(settings.getDataType());
        if (tableService != null) {            
            String newstring = tableService.toDataTable(searchHits, settings);
            model.addAttribute("searchResult", newstring);
        } else {
            LOGGER.info("table service not availabe for " + settings.getDataType());
	}
	}

    /**
     * Create and execute a solr query; retrieve search result
     *
     * @param urlService
     * @param settings
     * @param facet
     *
     * determine if result should include faceting
     *
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject fetchSearchResult(SearchUrlService urlService, SearchSettings settings, Boolean facet) throws IOException, URISyntaxException {

        // create and execute the main solr query 
        String queryUrl = urlService.getGridQueryUrl(settings.getQuery(),
                settings.getFqStr(),
                settings.getiDisplayStart(), settings.getiDisplayLength(),
                facet);
        JSONObject result = queryBrokerService.runQuery(queryUrl);
        if ("disease".equals(settings.getDataType())) {
            LOGGER.info("fetchSearchResult: " + result.toString(2));
        } else {
            LOGGER.info("fetchSearchResult: other");
        }

        return result;
    }

    /**
     * Run quick search queries and count the number of hits.
     *
     * @param settings
     *
     * An object specifying what to search for
     *
     * @return
     *
     * A map providing a count summary of search hits in each category, e.g.
     * hits in gene category, mp, disease, etc.
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    private JSONObject getMainFacetCounts(SearchSettings settings) throws IOException, URISyntaxException {

        // construct a map of queries, each query asking for number of hits in a core
        JSONObject queries = new JSONObject();
        String dataType = settings.getDataType();

        List<String> cores = Arrays.asList(new String[]{"gene", "mp", "disease", "phenodigm2disease", "anatomy", "impc_images", "allele2"});
        for (int i = 0; i < cores.size(); i++) {
            String thisCore = cores.get(i);
            SearchUrlService searchService = urlFactory.getService(thisCore);

            // apply custom filter on its intended core type
            String customFqStr = "";
            if (thisCore.equals(dataType)) {
                customFqStr = settings.getFqStr();
            }
            //LOGGER.info("core: " + thisCore + "\ncustomFqStr: " + customFqStr);

            // original code had logic handling genomic coordinate queries here.
            // TODO - reinstate the logic below into the current framework?
            //if (chrQuery != null && thisCore.equals("gene")) {
            //    query = "*:*";
            //    thisFqStr = chrQuery;
            //} else if (chrQuery != null && !thisCore.equals("gene")) {
            //    query = oriQuery;
            //}
            //
            // record a complete query url in the map
            String thisQueryUrl = searchService.getCountQuerySolrUrl(settings.getQuery(), customFqStr);
            queries.put(thisCore, thisQueryUrl);
        }

        return queryBrokerService.runQueries(null, queries);
    }

}
