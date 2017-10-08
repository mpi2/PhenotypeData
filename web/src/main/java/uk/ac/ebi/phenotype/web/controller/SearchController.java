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
import org.apache.commons.lang.StringUtils;
import org.mousephenotype.cda.solr.service.SolrIndex;
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
    private SolrIndex solrIndex;

    @Autowired
    private SearchUrlServiceFactory urlFactory;

    @Autowired
    private DataTableServiceFactory tableFactory;

    @Autowired
    private DataTableController dataTableController;

    @Autowired
    private QueryBrokerController queryBrokerController;

    /**
     * redirect calls to the base url or /search/ path to the search page
     *
     * @param request
     * @return
     */
    @RequestMapping(value = {"/index.html", "/search/"})
    public String searchForward(HttpServletRequest request) {
        String redirectUrl = request.getScheme() + ":" + request.getAttribute("mappedHostname") + request.getAttribute("baseUrl") + "/search";
        LOGGER.info("redirecting to " + redirectUrl);
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

        LOGGER.info("*** searchAll ***");
        SearchSettings settings = new SearchSettings("gene", "*", null, request);
        processSearch(settings, request, model);
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

        LOGGER.info("\n\n\n\n");
        LOGGER.info("searchResult: dataType: " + dataType);
        LOGGER.info("request is " + request.toString());

        // encode the parsed search settings into an object
        SearchSettings settings = new SearchSettings(dataType, query, fqStr, request);
        settings.setImgView(showImgView);
        settings.setDisplay(iDisplayStart, iDisplayLength);
        LOGGER.info(settings.toString());

        processSearch(settings, request, model);

        return "search";
    }

    /**
     * An intermediate function that uses parsed out argument values from the
     * url and fills in the model object with search result.
     *
     * The actual result fetching is performed elsewhere.
     *
     * @param dataType
     * @param query
     * @param fqStr
     * @param showImgView
     * @param request
     * @param model
     * @param oriQuery
     * @param chrQuery
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    /*
    private String processSearchOld(String dataType, String query, String fq, boolean showImgView, HttpServletRequest request, Model model, String oriQuery, String chrQuery) throws IOException, URISyntaxException {

        String paramString = request.getQueryString();
        LOGGER.info("processSearch: request querystring: " + paramString);

        // ensure request object holds attributes for search start and length
        Integer iDisplayStart = 0, iDisplayLength = 10;
        if (request.getParameter("iDisplayStart") != null) {
            iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
        }
        if (request.getParameter("iDisplayLength") != null) {
            iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
        }
        LOGGER.info("processSearch with " + iDisplayStart + "," + iDisplayLength);
        LOGGER.info("fq: " + fq);

        // fetch counts of hits in broad categories (used in webpage in tab headings)
        JSONObject facetCountJsonResponse = fetchAllFacetCounts(dataType, query, fq, request, model, oriQuery, chrQuery);
        LOGGER.info("facetCountJsonResponse: " + facetCountJsonResponse.toString(1));

        model.addAttribute("facetCount", facetCountJsonResponse);
        model.addAttribute("searchQuery", query.replaceAll("\\\\", ""));
        model.addAttribute("dataType", dataType);
        model.addAttribute("dataTypeParams", paramString);

        Boolean export = false;
        JSONObject json = fetchSearchResultOld(export, query, dataType, iDisplayStart, iDisplayLength, showImgView, fq, model);
        //LOGGER.info("fetchSearchResult gave result:\n"+json.toString(2));
        model.addAttribute("jsonStr", convert2DataTableJson(export, request, json, query, fq, iDisplayStart, iDisplayLength, showImgView, dataType));

        return "search";
    }
     */
    /**
     *
     * @param settings
     * @param request
     *
     * Try to remove this from the argument list?
     *
     * @param model
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private void processSearch(SearchSettings settings, HttpServletRequest request, Model model) throws IOException, URISyntaxException {

        // remove paramSring? only used in model.addAttribute below
        //String paramString = request.getQueryString();
        //LOGGER.info("processSearch: request querystring: " + paramString);
        LOGGER.info("processSearch: settings query: " + settings.getQuery());

        // calculate facets using old code
        //JSONObject facetCountJsonResponse = fetchAllFacetCounts(settings.getDataType(),
        //        settings.getQuery(), settings.getFqStr(), request, model, settings.getOriFqStr(), settings.getChrQuery());
        //LOGGER.info("facetCountJsonResponse: old: " + facetCountJsonResponse.toString(1));
        // fetch counts of hits in broad categories (used in webpage in tab headings)        
        JSONObject facetCounts = getMainFacetCounts(settings);
        LOGGER.info("facetCountJsonResponse: new: " + facetCounts.toString(1));

        model.addAttribute("facetCount", facetCounts);
        model.addAttribute("searchQuery", settings.getQuery().replaceAll("\\\\", ""));
        model.addAttribute("dataType", settings.getDataType());
        //model.addAttribute("dataTypeParams", paramString);

        // extract the hits using old code
        //Boolean export = false;
        //JSONObject json = fetchSearchResultOld(export, settings.getQuery(), settings.getDataType(),
        //        settings.getiDisplayStart(), settings.getiDisplayLength(), settings.isImgView(),
        //        settings.getFqStr(), model);
        //LOGGER.info("fetchSearchResultOld gave result:\n" + json.toString(2));
        // record summary of the search into the model
        SearchUrlService urlservice = urlFactory.getService(settings.getDataType());
        model.addAttribute("dataTypeLabel", urlservice.breadcrumbLabel());
        LOGGER.info("using breadcrumbLabel: " + urlservice.breadcrumbLabel());
        model.addAttribute("gridHeaderListStr", urlservice.gridHeadersStr());
        // perform the query, i.e. gather the hits from the solr
        JSONObject searchHits = fetchSearchResult(urlservice, settings, true);
        //LOGGER.info("fetchSearchResultNew gave result:\n" + searchHits.toString(2));

        String oldstring = convert2DataTableJson(false, request, searchHits,
                settings.getQuery(), settings.getFqStr(), 
                settings.getiDisplayStart(), settings.getiDisplayLength(), 
                settings.isImgView(), settings.getDataType());
        //LOGGER.info("old datatable string:\n"+oldstring);
        
        DataTableService tableService = tableFactory.getService(settings.getDataType());
        if (tableService != null) {
            LOGGER.info("using table service for " + settings.getDataType());
            String newstring = tableService.toDataTable(searchHits, settings);
            //LOGGER.info("new datatable string:\n"+newstring); 
            model.addAttribute("searchResult", newstring);
            LOGGER.info("after searchResult");
        } else {
            LOGGER.info("table service not availabel for " + settings.getDataType());
            model.addAttribute("searchResult", oldstring);
            LOGGER.info("oldResult is: "+oldstring);
        }

        LOGGER.info("exiting processSearch");
    }

    /**
     * Changes from one json object to another json string?
     *
     * (This is also used for FileExport, hence public)
     *
     * @param export
     * @param request
     * @param json
     * @param query
     * @param fqStr
     * @param iDisplayStart
     * @param iDisplayLength
     * @param showImgView
     * @param dataType
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public String convert2DataTableJson(Boolean export, HttpServletRequest request, JSONObject json, String query, String fqStr, Integer iDisplayStart, Integer iDisplayLength, Boolean showImgView, String dataType) throws IOException, URISyntaxException {

        String mode = dataType + "Grid";
        String solrCoreName = dataType;
        Boolean legacyOnly = false;
        String evidRank = "";
        String solrParamStr = composeSolrParamStr(export, query, fqStr, dataType);
        //LOGGER.info("convert2DataTableJsons solrParamStr: " + dataType + " -- " + solrParamStr);
        String content = dataTableController.fetchDataTableJson(request, json, mode, query, fqStr, iDisplayStart, iDisplayLength, solrParamStr, showImgView, solrCoreName, legacyOnly, evidRank);
        //LOGGER.info("convert2DataTableJsons result: " + content);

        return content;
    }
   
    /**
     * Fetch information from solr. This function should be deprecated, but it
     * still used in FileExportController.
     *
     *
     * @param export
     * @param query
     * @param dataType
     * @param iDisplayStart
     * @param iDisplayLength
     * @param showImgView
     * @param fqStr
     * @param model
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject fetchSearchResultOld(Boolean export, String query, String dataType, Integer iDisplayStart, Integer iDisplayLength, Boolean showImgView, String fqStr, Model model) throws IOException, URISyntaxException {

        // facet filter on the left panel of search page         
        SearchUrlService config = urlFactory.getService(dataType);
        model.addAttribute("dataTypeLabel", config.breadcrumbLabel());
        model.addAttribute("gridHeaderListStr", config.gridHeadersStr());

        // results on the right panel of search page
        String solrParamStr = composeSolrParamStr(export, query, fqStr, dataType);

        String mode = dataType + "Grid";
        JSONObject json = solrIndex.getQueryJson(query, dataType, solrParamStr, mode, iDisplayStart, iDisplayLength, showImgView);
        return json;
    }

    /**
     *
     * @param searchservice
     * @param settings
     * @param facet
     *
     * determine if result should include faceting
     *
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject fetchSearchResult(SearchUrlService searchservice, SearchSettings settings, Boolean facet) throws IOException, URISyntaxException {

        // results on the right panel of search page
        //String solrParamStrOld = composeSolrParamStr(false, settings.getQuery(), settings.getFqStr(), settings.getDataType());
        //LOGGER.info("fetchSearchResult (new):\n - oldQueryStr: " + solrParamStrOld + "\n");
        //String mode = dataType + "Grid";
        //JSONObject json = solrIndex.getQueryJson(settings.getQuery(), dataType, solrParamStrOld, mode,
        //        settings.getiDisplayStart(), settings.getiDisplayLength(), settings.isImgView());
        //LOGGER.info("\n\n +++ output was "+json.toString(2));
        // create and execute a solr query to fetch results
        String queryUrl = searchservice.getGridQueryUrl(settings.getQuery(),
                settings.getFqStr(),
                settings.getiDisplayStart(), settings.getiDisplayLength(),
                facet);
        LOGGER.info("fetchSearchResult (new):\n - newQueryStr: " + queryUrl + "\n");
        JSONObject result = queryBrokerController.runQuery(queryUrl);
        //LOGGER.info("\n\n +++ output new was: "+result.toString(2));

        return result;
    }

    public String composeSolrParamStr(Boolean export, String query, String fqStr, String dataType) {

        SearchUrlService config = urlFactory.getService(dataType);
        String qfStr = "&qf=" + config.qf();
        String defTypeStr = "&defType=" + config.defType();
        String facetStr = config.facetFieldsSolrStr();
        String flStr = "&fl=" + StringUtils.join(config.fieldList(), ",");
        String bqStr = "&bq=" + config.bq(query);

        // extra bq for anatomy and mp with facet filter
        if (dataType.equals("mp") || dataType.equals("anatomy")) {
            if (fqStr != null && !fqStr.contains("AND")) {
                String[] parts = fqStr.split(":");
                String fqTerm = " " + parts[1].replaceAll("\\)", "");
                String field = dataType.equals("mp") ? "mp_term" : "anatomy_term";
                bqStr += " " + field + ":" + fqTerm + " ^200";
            }
        }

        String solrParamStr = "wt=json&q=" + query + qfStr + defTypeStr + flStr + bqStr;
        if (!export) {
            solrParamStr += facetStr;
        }

        if (fqStr != null) {
            solrParamStr += "&fq=" + fqStr;
        } else {
            solrParamStr += "&fq=" + config.fq();
        }

        return solrParamStr;
    }

    /**
     * Original implementation of gene/phenotypes/disease/etc counts
     */
    public JSONObject fetchAllFacetCounts(String dataType, String query, String fqStr, HttpServletRequest request, Model model, String oriQuery, String chrQuery) throws IOException, URISyntaxException {

        JSONObject qryBrokerJson = new JSONObject();

        if (query.equals("*")) {
            query = "*:*";
        }

        LOGGER.info("fetchAllFacetCounts dataType: " + dataType);
        LOGGER.info("fetchAllFacetCounts query: " + query);
        LOGGER.info("fetchAllFacetCounts fqStr: " + fqStr);

        List<String> cores = Arrays.asList(new String[]{"gene", "mp", "disease", "anatomy", "impc_images", "allele2"});
        for (int i = 0; i < cores.size(); i++) {
            String thisCore = cores.get(i);
            SearchUrlService config = urlFactory.getService(thisCore);
            String qfDefTypeWt = "&qf=" + config.qf() + "&defType=" + config.defType() + "&wt=json";
            String thisFqStr;
            if (thisCore.equals(dataType)) {
                if (thisCore.equals("gene")) {
                    thisFqStr = fqStr == null ? "" : fqStr;
                } else {
                    thisFqStr = fqStr == null ? config.fq() : fqStr;
                }
            } else {
                if (thisCore.equals("gene")) {
                    thisFqStr = "";
                } else {
                    thisFqStr = config.fq();
                }
            }
            LOGGER.info("core: " + thisCore + "\nthisFqStr: " + thisFqStr);

            if (chrQuery != null && thisCore.equals("gene")) {
                query = "*:*";
                thisFqStr = chrQuery;
            } else if (chrQuery != null && !thisCore.equals("gene")) {
                query = oriQuery;
            }

            qryBrokerJson.put(thisCore, "q=" + query + "&fq=" + thisFqStr + qfDefTypeWt);
        }

        String subfacet = null;
        return queryBrokerController.createJsonResponse(subfacet, qryBrokerJson);
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
            LOGGER.info("core: " + thisCore + "\ncustomFqStr: " + customFqStr);

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

        return queryBrokerController.runQueries(null, queries);
    }

}
