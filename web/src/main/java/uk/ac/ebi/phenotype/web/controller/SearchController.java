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
import uk.ac.ebi.phenotype.util.SearchConfig;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private SearchConfig searchConfig;

    @Autowired
    private DataTableController dataTableController;

    @Autowired
    private QueryBrokerController queryBrokerController;

    /**
     * redirect calls to the base url or /search/ path to the search page
     *
     * @return
     */
    @RequestMapping(value = {"/index.html", "/search/"})     
    public String searchForward(HttpServletRequest request) {        
        String redirectUrl = request.getScheme() + ":" + request.getAttribute("mappedHostname") + request.getAttribute("baseUrl") + "/search";
        LOGGER.info("redirecting to "+redirectUrl);
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

        LOGGER.info("searchAll");
        return processSearch("gene", "*", null, null, null, false, request, model, null, null);
    }

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

        LOGGER.info("searchResult: dataType: " + dataType);
        if (query.equals("*")) {
            query = "*:*";
        }

        String oriQuery = query;
        String oriFqStr = fqStr;
        String chrQuery = null;

        if (StringUtils.isEmpty(dataType)) {
            dataType = "gene";
        }

        // for queries that ask for genomic coordinates, parse the query string 
        // into chromosome, start-end positions        
        String pattern = "(?i:^\"(chr)(.*)\\\\:(\\d+)\\\\-(\\d+)\"$)";
        if (query.matches(pattern)) {
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(query);
            if (m.find()) {
                String chrName = m.group(2).toUpperCase();
                String range = "[" + m.group(3).toUpperCase() + " TO " + m.group(4) + "]";
                String rangeQry = "(chr_name:" + chrName + ") AND (seq_region_start:" + range + ") AND (seq_region_end:" + range + ")";
                chrQuery = fqStr == null ? rangeQry : fqStr + " AND " + rangeQry;
            }           
            if (dataType.equals("gene")) {
                query = "*:*";
            } else {
                fqStr = oriFqStr;
            }
        }

        return processSearch(dataType, query, fqStr, iDisplayStart, iDisplayLength, showImgView, request, model, oriQuery, chrQuery);
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
     * @param iDisplayStart
     * @param iDisplayLength
     * @param showImgView
     * @param request
     * @param model
     * @param oriQuery
     * @param chrQuery
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private String processSearch(String dataType, String query, String fqStr, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView, HttpServletRequest request, Model model, String oriQuery, String chrQuery) throws IOException, URISyntaxException {

        iDisplayStart = iDisplayStart == null ? 0 : iDisplayStart;
        request.setAttribute("iDisplayStart", iDisplayStart);
        iDisplayLength = iDisplayLength == null ? 10 : iDisplayLength;
        request.setAttribute("iDisplayLength", iDisplayLength);

        String paramString = request.getQueryString();
        LOGGER.info("processSearch: paramString: " + paramString);

        // fetch counts of hits in broad categories (used in webpage in tab headings)
        JSONObject facetCountJsonResponse = fetchAllFacetCounts(dataType, query, fqStr, request, model, oriQuery, chrQuery);
        LOGGER.info("facetCountJsonResponse: " + facetCountJsonResponse.toString(1));
        model.addAttribute("facetCount", facetCountJsonResponse);
        model.addAttribute("searchQuery", query.replaceAll("\\\\", ""));
        model.addAttribute("dataType", dataType);
        model.addAttribute("dataTypeParams", paramString);

        Boolean export = false;
        JSONObject json = fetchSearchResult(export, query, dataType, iDisplayStart, iDisplayLength, showImgView, fqStr, model, request);
        //LOGGER.info("fetchSearchResult gave result:\n"+json.toString(2));
        model.addAttribute("jsonStr", convert2DataTableJson(export, request, json, query, fqStr, iDisplayStart, iDisplayLength, showImgView, dataType));

        return "search";
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
     * Fetch information from solr to satisfy the search query.
     *
     * (This is also used for FileExport, hence public)
     *
     * @param export
     * @param query
     * @param dataType
     * @param iDisplayStart
     * @param iDisplayLength
     * @param showImgView
     * @param fqStr
     * @param model
     * @param request
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public JSONObject fetchSearchResult(Boolean export, String query, String dataType, Integer iDisplayStart, Integer iDisplayLength, Boolean showImgView, String fqStr, Model model, HttpServletRequest request) throws IOException, URISyntaxException {

        // facet filter on the left panel of search page
        String breadcrumLabel = searchConfig.getBreadcrumLabel(dataType);
        model.addAttribute("dataTypeLabel", breadcrumLabel);
        model.addAttribute("gridHeaderListStr", StringUtils.join(searchConfig.getGridHeaders(dataType), ","));

        // results on the right panel of search page
        String solrParamStr = composeSolrParamStr(export, query, fqStr, dataType);
        LOGGER.info("fetchSearchResultJson solrParamStr: " + solrParamStr);

        String mode = dataType + "Grid";
        JSONObject json = solrIndex.getQueryJson(query, dataType, solrParamStr, mode, iDisplayStart, iDisplayLength, showImgView);
        //LOGGER.info("fetchSearchResult: " + json.toString(2));
        return json;
    }

    public String composeSolrParamStr(Boolean export, String query, String fqStr, String dataType) {

        String qfStr = searchConfig.getQfSolrStr(dataType);
        String defTypeStr = searchConfig.getDefTypeSolrStr();
        String facetStr = searchConfig.getFacetFieldsSolrStr(dataType);
        String flStr = searchConfig.getFieldListSolrStr(dataType);
        String bqStr = searchConfig.getBqStr(dataType, query);

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
            solrParamStr += "&fq=" + searchConfig.getFqStr(dataType);
        }

        return solrParamStr;
    }

    public JSONObject fetchAllFacetCounts(String dataType, String query, String fqStr, HttpServletRequest request, Model model, String oriQuery, String chrQuery) throws IOException, URISyntaxException {

        JSONObject qryBrokerJson = new JSONObject();

        if (query.equals("*")) {
            query = "*:*";
        }

        List<String> cores = Arrays.asList(new String[]{"gene", "mp", "disease", "anatomy", "impc_images", "allele2"});
        for (int i = 0; i < cores.size(); i++) {
            String thisCore = cores.get(i);            
            String qfDefTypeWt = "&qf=" + searchConfig.getQf(thisCore) + "&defType=edismax&wt=json";
            String thisFqStr;
            if (thisCore.equals(dataType)) {
                if (thisCore.equals("gene")) {
                    thisFqStr = fqStr == null ? "" : fqStr;
                } else {
                    thisFqStr = fqStr == null ? searchConfig.getFqStr(thisCore) : fqStr;
                }
            } else {
                if (thisCore.equals("gene")) {
                    thisFqStr = "";
                } else {
                    thisFqStr = searchConfig.getFqStr(thisCore);
                }
            }

            if (chrQuery != null && thisCore.equals("gene")) {
                query = "*:*";
                thisFqStr = chrQuery;
            } else if (chrQuery != null && !thisCore.equals("gene")) {
                query = oriQuery;
            }

            qryBrokerJson.put(thisCore, "q=" + query + "&fq=" + thisFqStr + qfDefTypeWt);
        }

        String subfacet = null;
        return queryBrokerController.createJsonResponse(subfacet, qryBrokerJson, request);
    }

}
