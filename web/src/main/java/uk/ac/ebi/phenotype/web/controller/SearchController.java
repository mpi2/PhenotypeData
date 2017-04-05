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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.phenotype.util.SearchConfig;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class SearchController {

	//LinkedList<FileMeta> files = new LinkedList<FileMeta>();
    //FileMeta fileMeta = null;


	@Autowired
	private SolrIndex solrIndex;

	@Autowired
	private SearchConfig searchConfig;

	@Autowired
	private DataTableController dataTableController;

	@Autowired
	private QueryBrokerController queryBrokerController;

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

		return "redirect:" + request.getAttribute("baseUrl") + "/search";
	}


	/**
	 * search page
	 *
	 */

	@RequestMapping("/search")
	public String searchResult2(
			HttpServletRequest request,
			Model model) throws IOException, URISyntaxException {

		return processSearch("gene", "*", null, null, null, false, request, model, null, null);
	}

	@RequestMapping("/search/{dataType}")
	public String searchResult(
			@PathVariable ()String dataType,
			@RequestParam(value = "kw", required = false, defaultValue = "*") String query,
			@RequestParam(value = "fq", required = false) String fqStr,
			@RequestParam(value = "iDisplayStart", required = false) Integer iDisplayStart,
			@RequestParam(value = "iDisplayLength", required = false) Integer iDisplayLength,
			@RequestParam(value = "showImgView", required = false) boolean showImgView,
			HttpServletRequest request,
			Model model) throws IOException, URISyntaxException {

//		System.out.println("path: /search/" + dataType);
		if ( query.equals("*") ){
			query = "*:*";
		}

		String oriQuery = query;
		String oriFqStr = fqStr;
		String chrQuery = null;

		if (StringUtils.isEmpty(dataType)) {
			dataType = "gene";
		}

		String pattern = "(?i:^\"(chr)(.*)\\\\:(\\d+)\\\\-(\\d+)\"$)";

		if (query.matches(pattern)) {

			// Create a Pattern object
			Pattern r = Pattern.compile(pattern);

			// Now create matcher object.
			Matcher m = r.matcher(query);
			if (m.find()) {
				String chrName = m.group(2).toUpperCase();
				String range = "[" + m.group(3).toUpperCase() + " TO " + m.group(4) + "]";
				String rangeQry = "(chr_name:" + chrName + ") AND (seq_region_start:"+range +") AND (seq_region_end:" + range + ")";

				chrQuery = fqStr == null ? rangeQry : fqStr + " AND " + rangeQry;
			}

			if (dataType.equals("gene")){
				query = "*:*";
			}
			else {
				fqStr = oriFqStr;
			}

		}

		return processSearch(dataType, query, fqStr, iDisplayStart, iDisplayLength, showImgView, request, model, oriQuery, chrQuery);
	}

	private String processSearch(String dataType, String query, String fqStr, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView, HttpServletRequest request, Model model, String oriQuery, String chrQuery) throws IOException, URISyntaxException {
		iDisplayStart =  iDisplayStart == null ? 0 : iDisplayStart;
		request.setAttribute("iDisplayStart", iDisplayStart);
		iDisplayLength = iDisplayLength == null ? 10 : iDisplayLength;
		request.setAttribute("iDisplayLength", iDisplayLength);

		String debug = request.getParameter("debug");

		String paramString = request.getQueryString();
		//System.out.println("paramString " + paramString);
		JSONObject facetCountJsonResponse = fetchAllFacetCounts(dataType, query, fqStr, request, model, oriQuery, chrQuery);

		model.addAttribute("facetCount", facetCountJsonResponse);
		model.addAttribute("searchQuery", query.replaceAll("\\\\",""));
		model.addAttribute("dataType", dataType); // lowercase: core name
		model.addAttribute("dataTypeParams", paramString);

        Boolean export = false;
		JSONObject json = fetchSearchResultJson(export, query, dataType, iDisplayStart, iDisplayLength, showImgView, fqStr, model, request);
		//System.out.println(json.toString());
		model.addAttribute("jsonStr", convert2DataTableJson(export, request, json, query, fqStr, iDisplayStart, iDisplayLength, showImgView, dataType));

		return "search";
	}


	public String convert2DataTableJson(Boolean export, HttpServletRequest request, JSONObject json, String query, String fqStr, Integer iDisplayStart, Integer iDisplayLength, Boolean showImgView, String dataType) throws IOException, URISyntaxException {

		String mode = dataType + "Grid";
		String solrCoreName = dataType;
		Boolean legacyOnly = false;
		String evidRank = "";
		String solrParamStr = composeSolrParamStr(export, query, fqStr, dataType);
		//System.out.println("SearchController solrParamStr: "+ dataType + " -- " + solrParamStr);
		String content = dataTableController.fetchDataTableJson(request, json, mode, query, fqStr, iDisplayStart, iDisplayLength, solrParamStr, showImgView, solrCoreName, legacyOnly, evidRank);
		//System.out.println("CONTENT: " + content);

		return content;
	}

	public JSONObject fetchSearchResultJson(Boolean export, String query, String dataType, Integer iDisplayStart, Integer iDisplayLength, Boolean showImgView, String fqStr, Model model, HttpServletRequest request) throws IOException, URISyntaxException {

		// facet filter on the left panel of search page

		String breadcrumLabel = searchConfig.getBreadcrumLabel(dataType);
		model.addAttribute("dataTypeLabel", breadcrumLabel);
		model.addAttribute("gridHeaderListStr", StringUtils.join(searchConfig.getGridHeaders(dataType), ","));

		// results on the right panel of search page

		String solrParamStr = composeSolrParamStr(export, query, fqStr, dataType);
		//System.out.println("SearchController solrParamStr: " + solrParamStr);
		String mode = dataType + "Grid";
		JSONObject json = solrIndex.getQueryJson(query, dataType, solrParamStr, mode, iDisplayStart, iDisplayLength, showImgView);
//		System.out.println("SearchController JSON: " + json.toString());
		return json;
	}

	public String composeSolrParamStr(Boolean export, String query, String fqStr, String dataType){

		String qfStr = searchConfig.getQfSolrStr(dataType);
		String defTypeStr = searchConfig.getDefTypeSolrStr();
		String facetStr = searchConfig.getFacetFieldsSolrStr(dataType);
		String flStr = searchConfig.getFieldListSolrStr(dataType);
		String bqStr = searchConfig.getBqStr(dataType, query);

        // extra bq for anatomy and mp with facet filter
        if ( dataType.equals("mp") || dataType.equals("anatomy")) {
            if (fqStr != null && !fqStr.contains("AND")) {
                String[] parts = fqStr.split(":");
                String fqTerm = " " + parts[1].replaceAll("\\)", "");
                String field = dataType.equals("mp") ? "mp_term" : "anatomy_term";
                bqStr += " " + field +  ":" + fqTerm + " ^200";
            }
        }

		String sortStr = searchConfig.getSortingStr(dataType);

		//String solrParamStr = "wt=json&q=" + query + qfStr + defTypeStr + flStr + facetStr + bqStr + sortStr;
		String solrParamStr = "wt=json&q=" + query + qfStr + defTypeStr + flStr + bqStr;
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
			solrParamStr += "&fq=" + searchConfig.getFqStr(dataType);
		}


		return solrParamStr;
	}

	public JSONObject fetchAllFacetCounts(String dataType, String query, String fqStr, HttpServletRequest request, Model model, String oriQuery, String chrQuery) throws IOException, URISyntaxException {

		JSONObject qryBrokerJson = new JSONObject();

		if ( query.equals("*") ){
			query = "*:*";
		}

		String qfDefTypeWt = null;

		List<String> cores = Arrays.asList(new String[]{"gene", "mp", "disease", "anatomy", "impc_images", "allele2"});
		for( int i=0; i<cores.size(); i++ ){
			String thisCore = cores.get(i);
			String thisFqStr = null;

			qfDefTypeWt = "&qf=" + searchConfig.getQf(thisCore) + "&defType=edismax&wt=json";

			if ( thisCore.equals(dataType) ) {
				if ( thisCore.equals("gene") ){
					thisFqStr = fqStr == null ? "" : fqStr;
				}
				else {
					thisFqStr = fqStr == null ? searchConfig.getFqStr(thisCore) : fqStr;
				}
			}
			else {
				if ( thisCore.equals("gene") ){
					thisFqStr = "";
				}
				else {
					thisFqStr = searchConfig.getFqStr(thisCore);
				}
			}

			if (chrQuery != null && thisCore.equals("gene")){
				query = "*:*";
				thisFqStr = chrQuery;
			}
			else if (chrQuery != null && ! thisCore.equals("gene")){
				query = oriQuery;
			}


			qryBrokerJson.put(thisCore, "q=" + query + "&fq="+thisFqStr + qfDefTypeWt);
		}

		// test
//		for ( String core : cores ){
//			System.out.println("SearchController facetcount - " + core + " : " + qryBrokerJson.get(core));
//		}

		String subfacet = null;
		return queryBrokerController.createJsonResponse(subfacet, qryBrokerJson, request);

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

	@RequestMapping(value="/batchQuery2", method=RequestMethod.GET)
	public String loadBatchQueryPage2(
			@RequestParam(value = "core", required = false) String core,
			@RequestParam(value = "fllist", required = false) String fllist,
			@RequestParam(value = "idlist", required = false) String idlist,
			HttpServletRequest request,
			Model model) {

		String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml2(core);
		model.addAttribute("outputFields", outputFieldsHtml);

		if ( idlist != null) {
			model.addAttribute("core", core);
			model.addAttribute("fllist", fllist);
			model.addAttribute("idlist", idlist);
		}

		return "batchQuery2";
	}

	@RequestMapping(value="/mesh", method=RequestMethod.GET)
	public String mesh(
			HttpServletRequest request,
			Model model) {

		return "mesh";
	}

}
