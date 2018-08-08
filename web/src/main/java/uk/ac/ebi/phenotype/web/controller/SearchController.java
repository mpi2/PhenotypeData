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
package uk.ac.ebi.phenotype.web.controller;

import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.phenotype.service.QueryBrokerService;
import uk.ac.ebi.phenotype.service.search.SearchUrlService;
import uk.ac.ebi.phenotype.service.search.SearchUrlServiceFactory;
import uk.ac.ebi.phenotype.util.SearchSettings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLDecoder;

@Controller
public class SearchController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SolrIndex solrIndex;

	@Autowired
	private DataTableController dataTableController;

	@Autowired
	private SearchUrlServiceFactory urlFactory;

	@Autowired
	private QueryBrokerService queryBrokerService;

	@Autowired
	@Qualifier("komp2DataSource")
	private DataSource komp2DataSource;


	/**
	 * redirect calls to the base url or /search path to the search page with the version 2 URL path
	 *
	 * @return
	 */
	@RequestMapping("/index.html")
	public String rootForward(HttpServletRequest request) {
		String redirectUrl = request.getScheme() + ":" + request.getAttribute("mappedHostname") + request.getAttribute("baseUrl") + "/search";
		return "redirect:" + redirectUrl;
	}


	/**
	 * search page
	 *
	 */

	/**
	 * redirect calls to the base url or /search path to the search page with the version 2 URL path
	 *
	 * @return
	 */
	@RequestMapping("/search/")  // appended slash
	public String searchForward(HttpServletRequest request) {
		String redirectUrl = request.getScheme() + ":" + request.getAttribute("mappedHostname") + request.getAttribute("baseUrl") + "/search/gene?kw=*";
		return "redirect:" + redirectUrl;
	}

	@RequestMapping("/search")
	public String searchResult2(
			HttpServletRequest request,
			Model model) throws IOException, URISyntaxException {

		String paramString = request.getQueryString();

		logger.debug("paramStr: {}", paramString);

		if ((paramString == null) || (paramString.equals("kw=*"))) {
			return searchForward(request);
		}

		SearchSettings settings = new SearchSettings("gene", "*", null, request);
		return processSearch(settings, model);
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

		if (StringUtils.isEmpty(dataType)) {
			dataType = "gene";
		}

		String paramString = request.getQueryString();

		// encode the parsed search settings into an object
		SearchSettings settings = new SearchSettings(dataType, query, fqStr, request);
		settings.setImgView(showImgView);
		settings.setDisplay(iDisplayStart, iDisplayLength);
		//logger.info(settings.toString());

		return processSearch(settings, model);
	}

	/**
	 	 * Figure out the default datatype and check the corresponding tab on search page when returing search result
	 	 *
	 	 * @param query string
	 	 * @return dataType as string
	 	 */
	@RequestMapping(value = "/fetchDefaultCore", method = RequestMethod.GET)
	@ResponseBody public String fetchDefaultCore(
			@RequestParam(value = "q", required = true) String query,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException  {

		String fqStr = "";
		SearchSettings settings = new SearchSettings("gene", query, fqStr, request);

		JSONObject facetCounts = getMainFacetCounts(settings);

		String dataType = "gene";

		if (facetCounts.getInt("gene") > 0){
			dataType = "gene";
		}
		else if (facetCounts.getInt("mp") > 0){
			dataType =  "mp";
		}
		else if (facetCounts.getInt("disease") > 0){
			dataType =  "disease";
		}
		else if (facetCounts.getInt("anatomy") > 0){
			dataType =  "anatomy";
		}
		else if (facetCounts.getInt("allele2") > 0){
			dataType = "allele2";
		}
		else if (facetCounts.getInt("impc_images") > 0){
			dataType =  "impc_images";
		}

		return dataType;

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
	private String processSearch(SearchSettings settings, Model model) throws IOException, URISyntaxException {

		// fetch counts of hits in broad categories (used in webpage in tab headings)
		JSONObject facetCounts = getMainFacetCounts(settings);

		// get default datatype based on facet count in the order of
		// gene, mp, disease, mp, images, products
		//settings.setDataType(setDefaultDataTypeByFacetCount(facetCounts));
		//System.out.println("type: "+ settings.getDataType());

		model.addAttribute("facetCount", facetCounts);
		model.addAttribute("searchQuery", URLDecoder.decode(settings.getQuery().replaceAll("\\\\", ""), "UTF-8"));
		model.addAttribute("dataType", settings.getDataType());
		//logger.info("facetCounts: " + facetCounts.toString(1));

		// create an object that will create query urls
		SearchUrlService urlservice = urlFactory.getService(settings.getDataType());
		model.addAttribute("dataTypeLabel", urlservice.breadcrumb());
		model.addAttribute("gridHeaderListStr", urlservice.gridHeadersStr());

			// perform the query, i.e. gather the hits from solr
		JSONObject searchHits = fetchSearchResult(urlservice, settings, true);
		//logger.info("fetchSearchResult result:\n" + searchHits.toString(2));

        Boolean export = false;
		model.addAttribute("jsonStr", convert2DataTableJson(urlservice, export, searchHits, settings));

		return "search";
	}

	/**
	 * Create and execute a solr query; retrieve search result
	 *
	 * (This is used outside of this controller, by FileExportController;
	 * consider moving into a separate SearchService)
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

		System.out.println("search result url: " + queryUrl);
		JSONObject result = queryBrokerService.runQuery(queryUrl);

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

		String[] cores = new String[]{"gene", "mp", "disease", "anatomy", "impc_images", "allele2"};
		for (String thisCore : cores) {
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

	public String convert2DataTableJson(SearchUrlService urlservice , Boolean export, JSONObject searchHits, SearchSettings settings) throws IOException, URISyntaxException {

		String mode = settings.getDataType() + "Grid";
		String solrCoreName = settings.getDataType() ;
		Boolean legacyOnly = false;
		String evidRank = "";
		String solrParamStr = composeSolrParamStr(urlservice, settings, export, settings.getQuery(), settings.getFqStr(), solrCoreName);
		//System.out.println("SearchController solrParamStr: "+ solrCoreName + " -- " + solrParamStr);

		String content = dataTableController.fetchDataTableJson(settings.getRequest(), searchHits, mode, settings.getQuery(), settings.getFqStr(),
			settings.getiDisplayStart(), settings.getiDisplayLength(), solrParamStr, settings.isImgView(), settings.getDataType(), legacyOnly, evidRank);

		return content;
	}

	public String composeSolrParamStr(SearchUrlService urlservice, SearchSettings settings, Boolean export, String query, String fqStr, String dataType){

		String qfStr = urlservice.qf();
		String defTypeStr = urlservice.defType();
		String facetStr = urlservice.facetFieldsSolrStr();
		String flStr = urlservice.fieldListSolrStr();
		String bqStr = urlservice.bq(settings.getQuery());

        // extra bq for anatomy and mp with facet filter
        if ( dataType.equals("mp") || dataType.equals("anatomy")) {
            if (fqStr != null && !fqStr.contains("AND")) {
                String[] parts = fqStr.split(":");
                String fqTerm = " " + parts[1].replaceAll("\\)", "");
                String field = dataType.equals("mp") ? "mp_term" : "anatomy_term";
                bqStr += " " + field +  ":" + fqTerm + " ^200";
            }
        }

		//String sortStr = searchConfig.getSortingStr(dataType);

		//String solrParamStr = "wt=json&q=" + query + qfStr + defTypeStr + flStr + facetStr + bqStr + sortStr;
		String solrParamStr = "wt=json&q=" + query
				+ "&qf=" + qfStr
				+ "&defType=" + defTypeStr
				+ "&fl=" + flStr
				+ "&bq=" + bqStr;// + sortStr;

        if (! export) {
            solrParamStr += facetStr;
        }

        if (fqStr != null) {
			solrParamStr += "&fq=" + fqStr;
//            if ( dataType.equals("impc_images")){
//                solrParamStr += "AND (biological_sample_group:experimental)";
//            }
		}
		else {
			//solrParamStr += "&fq=" + searchConfig.getFqStr(dataType);
			solrParamStr += "&fq=" + urlservice.fq();
		}


		return solrParamStr;
	}

	@RequestMapping(value="/batchquery2", method=RequestMethod.GET)
	public @ResponseBody String fetchDataFields(
			@RequestParam(value = "core", required = false) String core,
			HttpServletRequest request,
			Model model) {

		return Tools.fetchOutputFieldsCheckBoxesHtml(core);

	}

	@RequestMapping(value="/batchquery2-1", method=RequestMethod.GET)
	public @ResponseBody String fetchDataFields2(
			@RequestParam(value = "core", required = false) String core,
			HttpServletRequest request,
			Model model) {

		System.out.println("here.2-1....");
		return Tools.fetchOutputFieldsCheckBoxesHtml2(core);

	}

	@RequestMapping(value="/batchQuery", method=RequestMethod.GET)
	public String loadBatchQueryPage(
			@RequestParam(value = "core", required = false) String core,
			@RequestParam(value = "fllist", required = false) String fllist,
			@RequestParam(value = "idlist", required = false) String idlist,
			HttpServletRequest request,
			Model model) {

		String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml(core);
		model.addAttribute("outputFields", outputFieldsHtml);

		if ( idlist != null) {
			model.addAttribute("core", core);
			model.addAttribute("fllist", fllist);
			model.addAttribute("idlist", idlist);
		}

		return "batchQuery";
	}

	@RequestMapping(value="/mesh", method=RequestMethod.GET)
	public String mesh(
			HttpServletRequest request,
			Model model) {

		return "mesh";
	}

}
