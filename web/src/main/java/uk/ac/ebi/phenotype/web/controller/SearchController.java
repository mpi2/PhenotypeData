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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.mousephenotype.cda.solr.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONObject;
import uk.ac.ebi.phenotype.util.SearchConfig;


@Controller
public class SearchController {

	//LinkedList<FileMeta> files = new LinkedList<FileMeta>();
    //FileMeta fileMeta = null;

	/**
	 * redirect calls to the base url or /search path to the search page with the version 2 URL path
	 *
	 * @return
	 */
	@RequestMapping("/index.html")
	public String rootForward() {
		return "redirect:/search";
	}

	@Autowired
	private SolrIndex solrIndex;

	@Autowired
	private SearchConfig searchConfig;

	@Autowired
	private DataTableController dataTableController;

	@Autowired
	private QueryBrokerController queryBrokerController;

    @Autowired
    @Qualifier("geneCore")
    private SolrClient geneCore;

    @Autowired
    @Qualifier("mpCore")
    private SolrClient mpCore;

    @Autowired
    @Qualifier("diseaseCore")
    private SolrClient diseaseCore;

    @Autowired
    @Qualifier("anatomyCore")
    private SolrClient anatomyCore;

    @Autowired
    @Qualifier("impcImagesCore")
    private SolrClient impcImagesCore;

    @Autowired
    @Qualifier("allele2Core")
    private SolrClient allele2Core;


    /**
     * searchOverview page
     *
     */

    @RequestMapping("/search")
    public String searchResult(
            @RequestParam(value = "kw", required = false, defaultValue = "*") String query,
            HttpServletRequest request,
            Model model) throws IOException, URISyntaxException {

			String fqStr = null;
		return processSearchOverview("gene", query, fqStr, request, model);
	}

	private String processSearchOverview(String dataType, String query, String fqStr, HttpServletRequest request, Model model) throws IOException, URISyntaxException {

		System.out.println("query: " + query);
		if ( query.equals("*") ){
			query = "*:*";
		}


		String paramString = request.getQueryString();
		System.out.println("paramString " + paramString);
//        JSONObject facetCountJsonResponse = fetchAllFacetCounts(dataType, query, fqStr, request, model);
//        System.out.println(facetCountJsonResponse.toString());
//        model.addAttribute("facetCount", facetCountJsonResponse);

//		iDisplayStart = 0;
//		iDisplayLength = 1;
		Boolean showImgView = false;

		JSONObject coreResult = new JSONObject();
		Boolean doFacet = false;

		Map<String, SolrClient> solrCoreMap = new HashMap<>();
		solrCoreMap.put("gene", geneCore);
		solrCoreMap.put("mp", mpCore);
		solrCoreMap.put("disease", diseaseCore);
		solrCoreMap.put("anatomy", anatomyCore);
		solrCoreMap.put("impc_images", impcImagesCore);
		solrCoreMap.put("allele2", allele2Core);

		Map<String, Integer> coreCount = new HashMap<>();
		Map<String, SolrDocument> coreData = new HashMap<>();

		for ( String thisCore : solrCoreMap.keySet() ){

			SolrQuery solrParams = composeSolrJParamStr(query, fqStr, thisCore, doFacet);
			//System.out.println("param: " + solrParams.toString());

			QueryResponse response = null;
			try {
				response = solrCoreMap.get(thisCore).query(solrParams);
				Integer docCount = (int) (long) response.getResults().getNumFound();
				coreCount.put(thisCore, docCount);
				//coreData.put(thisCore, response.getResults());
				SolrDocument doc = null;
				if ( docCount != 0 ) {
					doc = response.getResults().get(0);
                    doc = highlightMatches(doc, query, thisCore);
                    //System.out.println(thisCore + " - doc: " + doc.toString());
				}
				coreData.put(thisCore, doc);

			} catch (SolrServerException e) {
				System.out.println(e.getStackTrace());
			}
		}

		model.addAttribute("coreCount", coreCount);
		model.addAttribute("coreData", coreData);
        model.addAttribute("params", paramString);

		return "search";
	}

	@RequestMapping("/search/{dataType}")
	public String searchOverviewResult(
			@PathVariable ()String dataType,
			@RequestParam(value = "kw", required = false, defaultValue = "*") String query,
			@RequestParam(value = "fq", required = false) String fqStr,
			@RequestParam(value = "iDisplayStart", required = false) Integer iDisplayStart,
			@RequestParam(value = "iDisplayLength", required = false) Integer iDisplayLength,
			@RequestParam(value = "showImgView", required = false) boolean showImgView,
			HttpServletRequest request,
			Model model) throws IOException, URISyntaxException {

		return processDataTypeSearch(dataType, query, fqStr, iDisplayStart, iDisplayLength, showImgView, request, model);
	}

	private String processDataTypeSearch(String dataType, String query, String fqStr, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView, HttpServletRequest request, Model model) throws IOException, URISyntaxException {
		iDisplayStart =  iDisplayStart == null ? 0 : iDisplayStart;
		request.setAttribute("iDisplayStart", iDisplayStart);
		iDisplayLength = iDisplayLength == null ? 10 : iDisplayLength;
		request.setAttribute("iDisplayLength", iDisplayLength);

		String debug = request.getParameter("debug");

		String paramString = request.getQueryString();
		//System.out.println("paramString " + paramString);
		JSONObject facetCountJsonResponse = fetchAllFacetCounts(dataType, query, fqStr, request, model);

		model.addAttribute("facetCount", facetCountJsonResponse);
		model.addAttribute("searchQuery", query.replaceAll("\\\\",""));
		model.addAttribute("dataType", dataType); // lowercase: core name
		model.addAttribute("dataTypeParams", paramString);

		JSONObject json = fetchSearchResultJson(query, dataType, iDisplayStart, iDisplayLength, showImgView, fqStr, model, request);
		model.addAttribute("jsonStr", convert2DataTableJson(request, json, query, fqStr, iDisplayStart, iDisplayLength, showImgView, dataType));

		return "searchDatatype";
	}


    private SolrDocument highlightMatches(SolrDocument doc, String query, String coreName){
        String lcQuery = query.toLowerCase().replaceAll("\"", "");

        if (coreName.equals("gene")) {
            doc = getMatchStr(doc, GeneDTO.MARKER_NAME, lcQuery, false);

            List<String> fields = Arrays.asList(GeneDTO.HUMAN_GENE_SYMBOL, GeneDTO.MARKER_SYNONYM);
            for(String field : fields){
                doc = getMatchStr(doc, field, lcQuery, true);
            }
        }
        else if (coreName.equals("mp")) {
            List<String> fields = Arrays.asList(MpDTO.MP_TERM_SYNONYM, MpDTO.MP_NARROW_SYNONYM);
            for(String field : fields){
                doc = getMatchStr(doc, field, lcQuery, true);
            }

            String def = doc.getFieldValue(MpDTO.MP_DEFINITION).toString();
            if (def.toLowerCase().contains(lcQuery)) {

                String defMatch = "<div class='fullDef'>" + Tools.highlightMatchedStrIfFound(lcQuery, def, "span", "subMatch") + "</div>";

                int defaultLen = 30;

                if (def.length() > defaultLen) {

                    String trimmedDef = def.substring(0, defaultLen);
                    // retrim if in the middle of a word
                    trimmedDef = trimmedDef.substring(0, Math.min(trimmedDef.length(), trimmedDef.lastIndexOf(" ")));

                    String partMpDef = "<div class='partDef'>" + Tools.highlightMatchedStrIfFound(lcQuery, trimmedDef, "span", "subMatch") + " ...</div>";
                    doc.setField(MpDTO.MP_DEFINITION, partMpDef + defMatch + "<div class='moreLess'>Show more</div>");
                }
                else {
                    doc.setField(MpDTO.MP_DEFINITION, def);
                }
            }

        }
        else if (coreName.equals("anatomy")){
            doc = getMatchStr(doc, AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM, lcQuery, false);
        }
        else if (coreName.equals("impc_images")) {
            String thumb = doc.getFieldValue(ImageDTO.JPEG_URL).toString().replace("render_image", "render_birds_eye_view");
            doc.setField(ImageDTO.JPEG_URL, thumb);

            List<String> fields = Arrays.asList(ImageDTO.PROCEDURE_NAME, ImageDTO.GENE_SYMBOL);
            for(String field : fields){
                doc = getMatchStr(doc, field, lcQuery, false);
            }
            List<String> fields2 = Arrays.asList(ImageDTO.ANATOMY_TERM, ImageDTO.MARKER_SYNONYM, ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM, ImageDTO.INTERMEDIATE_ANATOMY_TERM);
            for(String field : fields2){
                doc = getMatchStr(doc, field, lcQuery, true);
            }
        }
        else if (coreName.equals("allele2")) {
            doc = getMatchStr(doc, AlleleDTO.MARKER_SYMBOL, lcQuery, false);
            String sup = "<sup>" + doc.getFieldValue(Allele2DTO.ALLELE_NAME).toString() + "</sup>";
            doc.setField(Allele2DTO.ALLELE_NAME, sup);

        }

        return doc;
    }

    private SolrDocument getMatchStr(SolrDocument doc, String fieldName, String lcQuery, Boolean isList){
        try {
            String fieldVal = null;

            if (isList){
                //System.out.println(fieldName + " : " + doc.getFieldValue(fieldName));
                for (String fv : (List<String>) doc.getFieldValue(fieldName)) {
                    if (fv.toLowerCase().contains(lcQuery)) {
                        fieldVal = fv;
                        break;
                    }
                }
            }
            else {
                if (doc.getFieldValue(fieldName).toString().toLowerCase().contains(lcQuery)) {
                    fieldVal = doc.getFieldValue(fieldName).toString();
                }
            }

            if (fieldVal != null) {
                //System.out.println(fieldName + " GOT VAL: " + fieldVal);
                String fieldValMatch = Tools.highlightMatchedStrIfFound(lcQuery, fieldVal, "span", "subMatch");
                if (isList) {
                    List<String> matches = new ArrayList<>();
                    matches.add(fieldValMatch);
                    doc.setField(fieldName, matches);
                } else {
                    //System.out.println("MATCH: " + fieldValMatch);
                    doc.setField(fieldName, fieldValMatch);
                }
            }
        }
        catch (Exception e){
            System.out.println(fieldName + ": Error: " + e.getStackTrace());
        }

        return doc;
    }

    /**
	 * search page
	 *
	 */

//	@RequestMapping("/search")
//	public String searchResult2(
//			HttpServletRequest request,
//			Model model) throws IOException, URISyntaxException {
//
//	//	System.out.println("path: /search");
//
//		return processSearch("gene", "*", null, null, null, false, request, model);
//	}
//
//	@RequestMapping("/search/{dataType}")
//	public String searchResult(
//			@PathVariable ()String dataType,
//			@RequestParam(value = "kw", required = false, defaultValue = "*") String query,
//			@RequestParam(value = "fq", required = false) String fqStr,
//			@RequestParam(value = "iDisplayStart", required = false) Integer iDisplayStart,
//			@RequestParam(value = "iDisplayLength", required = false) Integer iDisplayLength,
//			@RequestParam(value = "showImgView", required = false) boolean showImgView,
//			HttpServletRequest request,
//			Model model) throws IOException, URISyntaxException {
//
////		System.out.println("path: /search/" + dataType);
//		if ( query.equals("*") ){
//			query = "*:*";
//		}
//
//		return processSearch(dataType, query, fqStr, iDisplayStart, iDisplayLength, showImgView, request, model);
//	}

	private String processSearch(String dataType, String query, String fqStr, Integer iDisplayStart, Integer iDisplayLength, boolean showImgView, HttpServletRequest request, Model model) throws IOException, URISyntaxException {
		iDisplayStart =  iDisplayStart == null ? 0 : iDisplayStart;
		request.setAttribute("iDisplayStart", iDisplayStart);
		iDisplayLength = iDisplayLength == null ? 10 : iDisplayLength;
		request.setAttribute("iDisplayLength", iDisplayLength);

		String debug = request.getParameter("debug");

		String paramString = request.getQueryString();
//		System.out.println("paramString " + paramString);
		JSONObject facetCountJsonResponse = fetchAllFacetCounts(dataType, query, fqStr, request, model);

		model.addAttribute("facetCount", facetCountJsonResponse);
		model.addAttribute("searchQuery", query.replaceAll("\\\\",""));
		model.addAttribute("dataType", dataType); // lowercase: core name
		model.addAttribute("dataTypeParams", paramString);

		JSONObject json = fetchSearchResultJson(query, dataType, iDisplayStart, iDisplayLength, showImgView, fqStr, model, request);
		model.addAttribute("jsonStr", convert2DataTableJson(request, json, query, fqStr, iDisplayStart, iDisplayLength, showImgView, dataType));

		return "search";
	}


	public String convert2DataTableJson(HttpServletRequest request, JSONObject json, String query, String fqStr, Integer iDisplayStart, Integer iDisplayLength, Boolean showImgView, String dataType) throws IOException, URISyntaxException {

		String mode = dataType + "Grid";
		String solrCoreName = dataType;
		Boolean legacyOnly = false;
		String evidRank = "";
        Boolean doFacet = true;
		String solrParamStr = composeSolrParamStr(query, fqStr, dataType, doFacet);
//		System.out.println("SearchController solrParamStr: "+ solrParamStr);
		String content = dataTableController.fetchDataTableJson(request, json, mode, query, fqStr, iDisplayStart, iDisplayLength, solrParamStr, showImgView, solrCoreName, legacyOnly, evidRank);
//		System.out.println("CONTENT: " + content);

		return content;
	}

	public JSONObject fetchSearchResultJson(String query, String dataType, Integer iDisplayStart, Integer iDisplayLength, Boolean showImgView, String fqStr, Model model, HttpServletRequest request) throws IOException, URISyntaxException {

		// facet filter on the left panel of search page

		String breadcrumLabel = searchConfig.getBreadcrumLabel(dataType);
		model.addAttribute("dataTypeLabel", breadcrumLabel);
		model.addAttribute("gridHeaderListStr", StringUtils.join(searchConfig.getGridHeaders(dataType), ","));

		// results on the right panel of search page
        Boolean doFacet = true;
		String solrParamStr = composeSolrParamStr(query, fqStr, dataType, doFacet);
		//System.out.println("SearchController solrParamStr: " + solrParamStr);
		String mode = dataType + "Grid";
		JSONObject json = solrIndex.getQueryJson(query, dataType, solrParamStr, mode, iDisplayStart, iDisplayLength, showImgView);
		System.out.println("SearchController JSON: " + json.toString());
		return json;
	}

    public SolrQuery composeSolrJParamStr(String query, String fqStr, String dataType, Boolean doFacet){

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		solrQuery.set("qf", searchConfig.getQf(dataType));
		solrQuery.set("defType", searchConfig.getDefType());
		solrQuery.set("fl", StringUtils.join(searchConfig.getFieldList(dataType), ","));
		solrQuery.set("bq", searchConfig.getBqStr(dataType, query).replace("&bq=", ""));
		solrQuery.set("wt", "json");
		solrQuery.setRows(1);

        if (fqStr != null) {
            solrQuery.setFilterQueries(fqStr);
            if ( dataType.equals("impc_images")){
				solrQuery.setFilterQueries(fqStr + " AND (biological_sample_group:experimental)");
            }
        }
        else {
			solrQuery.setFilterQueries(searchConfig.getFqStr(dataType));
        }


        return solrQuery;
    }

	public String composeSolrParamStr(String query, String fqStr, String dataType, Boolean doFacet){

		String qfStr = searchConfig.getQfSolrStr(dataType);
		String defTypeStr = searchConfig.getDefTypeSolrStr();
		String facetStr = searchConfig.getFacetFieldsSolrStr(dataType);
		String flStr = searchConfig.getFieldListSolrStr(dataType);
		String bqStr = searchConfig.getBqStr(dataType, query);
		String sortStr = searchConfig.getSortingStr(dataType);

		//String solrParamStr = "wt=json&q=" + query + qfStr + defTypeStr + flStr + facetStr + bqStr + sortStr;
        String solrParamStr = null;
        if ( doFacet ) {
            solrParamStr = "wt=json&q=" + query + qfStr + defTypeStr + flStr + bqStr + facetStr;
        }
        else {
            solrParamStr = "wt=json&q=" + query + qfStr + defTypeStr + flStr + bqStr;
        }

		if (fqStr != null) {
			solrParamStr += "&fq=" + fqStr;
            if ( dataType.equals("impc_images")){
                solrParamStr += "AND (biological_sample_group:experimental)";
            }
		}
		else {
			solrParamStr += "&fq=" + searchConfig.getFqStr(dataType);
		}


		return solrParamStr;
	}

	public JSONObject fetchAllFacetCounts(String dataType, String query, String fqStr, HttpServletRequest request, Model model) throws IOException, URISyntaxException {

		JSONObject qryBrokerJson = new JSONObject();

		if ( query.equals("*") ){
			query = "*:*";
		}
		String qStr = "q=" + query;

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

			qryBrokerJson.put(thisCore, qStr + "&fq="+thisFqStr + qfDefTypeWt);
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

	@RequestMapping(value="/batchQuery", method=RequestMethod.GET)
	public String loadBatchQueryPage(
			@RequestParam(value = "core", required = false) String core,
			HttpServletRequest request,
			Model model) {

		String outputFieldsHtml = Tools.fetchOutputFieldsCheckBoxesHtml(core);
		model.addAttribute("outputFields", outputFieldsHtml);

		return "batchQuery";
	}


}
