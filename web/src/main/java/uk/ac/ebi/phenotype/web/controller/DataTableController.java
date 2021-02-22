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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.solr.generic.util.JSONImageUtils;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.SolrIndex.AnnotNameValCount;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.phenotype.service.BatchQueryForm;
import uk.ac.ebi.phenotype.util.SolrUtilsWeb;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;


@Controller
public class DataTableController {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@NotNull @Autowired
	private SolrIndex solrIndex;

	@NotNull @Autowired
	private GeneService geneService;

	@NotNull @Autowired
	ExpressionService expressionService;

	@NotNull @Autowired
	private MpService mpService;

	@NotNull @Autowired
	private PhenodigmService phenodigmService;

	@NotNull @Resource(name = "globalConfiguration")
	private Map<String, String> config;

	@NotNull @Autowired
	private SolrUtilsWeb solrUtilsWeb;


	/**
	 <p>
	 * deals with batchQuery
	 * Return jQuery dataTable from server-side for lazy-loading.
	 * </p>
	 * @throws SolrServerException, IOException
	 *
	 */

	@RequestMapping(value = "/dataTable_bq", method = RequestMethod.POST)
	public ResponseEntity<String> bqDataTableJson(
			@RequestParam(value = "idlist", required = true) String idlist,
			@RequestParam(value = "fllist", required = true) String fllist,
			@RequestParam(value = "corename", required = true) String dataTypeName,

			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, SolrServerException, JSONException {

		String content = null;
		fllist += ",datasets_raw_data";

		String oriDataTypeName = dataTypeName;
		List<String> queryIds = Arrays.asList(idlist.split(","));
		Long time = System.currentTimeMillis();

		List<String> mgiIds = new ArrayList<>();
		List<org.mousephenotype.cda.solr.service.dto.GeneDTO> genes = new ArrayList<>();
		List<QueryResponse> solrResponses = new ArrayList<>();

		List<String> batchIdList = new ArrayList<>();
		String batchIdListStr = null;

		int counter = 0;

		//System.out.println("id length: "+ queryIds.length());
		// will show only 10 records to the users to show how the data look like
		for ( String id : queryIds ) {
			counter++;

			// limit the batch size
			//if ( counter < 11 ){
			batchIdList.add(id);
			//}
		}
		queryIds = batchIdList;

		// batch solr query
		batchIdListStr = StringUtils.join(batchIdList, ",");
		if ( !batchIdListStr.startsWith("\"") && !batchIdListStr.endsWith("\"")){
			batchIdListStr = "\"" + batchIdListStr + "\"";
		}
		//System.out.println("idstr: "+ batchIdListStr);
		solrResponses.add(solrIndex.getBatchQueryJson(batchIdListStr, fllist, dataTypeName));
		content = fetchBatchQueryDataTableJson(request, solrResponses, fllist, oriDataTypeName, queryIds);
		return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
	}

	public String fetchBatchQueryDataTableJson(HttpServletRequest request, List<QueryResponse> solrResponses, String fllist, String dataTypeName, List<String> queryIds ) throws JSONException, IOException, SolrServerException {

		SolrDocumentList results = new SolrDocumentList();

		for (QueryResponse solrResponse : solrResponses) {
			results.addAll(solrResponse.getResults());
		}

		String         mode = "onPage";
		BatchQueryForm form = new BatchQueryForm(mode, request, results, fllist, dataTypeName, queryIds, mpService, phenodigmService);
		//System.out.println(form.j.toString());

		return form.j.toString();
	}

	/**
	 * <p>
	 * Return jQuery dataTable from server-side for lazy-loading.
	 * </p>
	 *
	 * bSearchable_0=true bSearchable_1=true bSearchable_2=true bSortable_0=true
	 * bSortable_1=true bSortable_2=true iColumns=3 for paging:
	 * iDisplayLength=10 iDisplayStart=0 for sorting: iSortCol_0=0
	 * iSortingCols=1 for filtering: sSearch= sSearch_0= sSearch_1= sSearch_2=
	 * mDataProp_0=0 mDataProp_1=1 mDataProp_2=2 sColumns= sEcho=1
	 * sSortDir_0=asc
	 *
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@RequestMapping(value = "/dataTable", method = RequestMethod.GET)
	public ResponseEntity<String> dataTableJson(
			@RequestParam(value = "iDisplayStart", required = false) int iDisplayStart,
			@RequestParam(value = "iDisplayLength", required = false) int iDisplayLength,
			@RequestParam(value = "solrParams", required = false) String solrParams,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, JSONException {

		JSONObject jParams = new JSONObject (solrParams);

		String solrCoreName = jParams.has("solrCoreName") ? jParams.getString("solrCoreName") : jParams.getString("facetName");
		// use this for pattern matching later, instead of the modified complexphrase q string
		String queryOri = jParams.getString("qOri");

		String query = "";
		String fqOri = "";
		String mode = jParams.getString("mode");
		String solrParamStr = jParams.getString("params");

		boolean legacyOnly = jParams.getBoolean("legacyOnly");
		String evidRank = jParams.has("evidRank") ? jParams.getString("evidRank") : "";

		// Get the query string
		String[] pairs = solrParamStr.split("&");
		for (String pair : pairs) {
			try {
				String[] parts = pair.split("=");
				if (parts[0].equals("q")) {
					query = parts[1];
				}
				if (parts[0].equals("fq")) {
					fqOri = "&fq=" + parts[1];
				}
			} catch (Exception e) {
				log.error("Error getting value of key");
			}
		}

		boolean showImgView = false;
		if (jParams.has("showImgView")) {
			showImgView = jParams.getBoolean("showImgView");
		}

		System.out.println("DataTableController SOLRPARAMS: " + solrParamStr);
		JSONObject json = solrIndex.getQueryJson(query, solrCoreName, solrParamStr, mode, iDisplayStart, iDisplayLength, showImgView);

		String content = fetchDataTableJson(request, json, mode, queryOri, fqOri, iDisplayStart, iDisplayLength, solrParamStr, showImgView, solrCoreName, legacyOnly, evidRank);

		return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
	}

	private HttpHeaders createResponseHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();

		// this returns json, but utf encoding failed
		//responseHeaders.setContentType(MediaType.APPLICATION_JSON);

		// this returns html string, not json, and is utf encoded
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");


		return responseHeaders;
	}

	public String fetchDataTableJson(HttpServletRequest request,
									 JSONObject json,
									 String mode,
									 String query,
									 String fqOri,
									 int start,
									 int length,
									 String solrParams,
									 boolean showImgView,
									 String solrCoreName,
									 boolean legacyOnly, String evidRank) throws IOException, URISyntaxException, JSONException {

		request.setAttribute("displayStart", start);
		request.setAttribute("displayLength", length);
		String jsonStr = null;
		if (mode.equals("geneGrid")) {
			jsonStr = parseJsonforGeneDataTable(json, request, query, fqOri, solrCoreName, legacyOnly);
		} else if (mode.equals("pipelineGrid")) {
			jsonStr = parseJsonforProtocolDataTable(json, request, solrCoreName);
		} else if (mode.equals("impc_imagesGrid")) {
			jsonStr = parseJsonforImpcImageDataTable(json, solrParams, showImgView, request, query, fqOri, solrCoreName);
		} else if (mode.equals("imagesGrid")) {
			jsonStr = parseJsonforImageDataTable(json, solrParams, showImgView, request, query, fqOri, solrCoreName);
		} else if (mode.equals("mpGrid")) {
			jsonStr = parseJsonforMpDataTable(json, request, query, solrCoreName);
		} else if (mode.equals("anatomyGrid")) {
			jsonStr = parseJsonforAnatomyDataTable(json, request, query, solrCoreName);
		} else if (mode.equals("diseaseGrid")) {
			jsonStr = parseJsonforDiseaseDataTable(json, request, query, solrCoreName);
		} else if (mode.equals("allele2Grid")) {
			jsonStr = parseJsonforProductDataTable(json, request, query, solrCoreName);
		}

		return jsonStr;
	}

	public String parseJsonforProductDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreName) throws UnsupportedEncodingException, JSONException {

		String baseUrl = request.getAttribute("baseUrl").toString();

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		j.put("iDisplayStart", request.getAttribute("displayStart"));
		j.put("iDisplayLength", request.getAttribute("displayLength"));


		for (int i = 0; i < docs.length(); i ++) {
			List<String> rowData = new ArrayList<String>();

			// array element is an alternate of facetField and facetCount
			JSONObject doc = docs.getJSONObject(i);

			//String alleleName = "<span class='allelename'>"+ URLEncoder.encode(doc.getString("allele_name"), "UTF-8")+"</span>";

			if ( !doc.has(Allele2DTO.ALLELE_NAME)){
				// no point to show an allele that has no name
				continue;
			}

			String markerAcc = doc.getString(Allele2DTO.MGI_ACCESSION_ID);
			String alleleName = doc.getString(Allele2DTO.ALLELE_NAME);
//			String alleleUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString() + "/alleles/" + markerAcc + "/" + alleleName;
//			String markerSymbol = doc.getString(Allele2DTO.MARKER_SYMBOL);
			String alleleLink = "";//"<a href='" + alleleUrl + "'>" + markerSymbol + "<sup>" + alleleName + "</sup></a>";

			String mutationType = "";
			String mt = doc.has(Allele2DTO.MUTATION_TYPE) ? doc.getString(Allele2DTO.MUTATION_TYPE) : "";
			String desc = doc.has(Allele2DTO.ALLELE_DESCRIPTION) ? doc.getString(Allele2DTO.ALLELE_DESCRIPTION) : "";
			List<String> mtDesc = new ArrayList<>(); mtDesc.add(mt); mtDesc.add(desc);

			if (! mt.isEmpty() && ! desc.isEmpty()) {
				mutationType = StringUtils.join(mtDesc, "; ");
			}

			List<String> order = new ArrayList<>();
			String dataUrl = baseUrl + "/order?acc=" + markerAcc + "&allele=" + alleleName +"&bare=true";

			if ( doc.has(Allele2DTO.TARGETING_VECTOR_AVAILABLE) && doc.getBoolean(Allele2DTO.TARGETING_VECTOR_AVAILABLE) ) {
				order.add("<tr>");
				order.add("<td><a target='_blank' href='" + dataUrl + "&type=targeting_vector'><i class='fa fa-shopping-cart'><span class='orderFont'> Targeting vector</span></i></a></td>");

				if (doc.has(Allele2DTO.VECTOR_ALLELE_IMAGE)) {
					order.add("<td><a target='_blank' href='" + doc.getString(Allele2DTO.VECTOR_ALLELE_IMAGE) + "' title='map for vector'><i class='fa fa-th-list'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}

				if (doc.has(Allele2DTO.VECTOR_GENBANK_LINK)) {
					order.add("<td><a target='_blank' href='" + doc.getString(Allele2DTO.VECTOR_GENBANK_LINK) + "' title='genbank file for vector'><i class='fa fa-file-text'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}
				order.add("</tr>");
			}
			if ( doc.has(Allele2DTO.ES_CELL_AVAILABLE) && doc.getBoolean(Allele2DTO.ES_CELL_AVAILABLE)){
				order.add("<tr>");
				order.add("<td><a target='_blank' href='" + dataUrl + "&type=es_cell'><i class='fa fa-shopping-cart'><span class='orderFont'> ES cell</span></i></a></td>");

				if (doc.has(Allele2DTO.ALLELE_SIMPLE_IMAGE)) {
					order.add("<td><a target='_blank' href='" + doc.getString(Allele2DTO.ALLELE_SIMPLE_IMAGE) + "' title='map for allele'><i class='fa fa-th-list'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}

				if (doc.has(Allele2DTO.GENBANK_FILE)) {
					order.add("<td><a class='genbank' href='" + doc.getString(Allele2DTO.GENBANK_FILE) + "' title='genbank file for allele'><i class='fa fa-file-text'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}
				order.add("</tr>");
			}

			if ( doc.has(Allele2DTO.MOUSE_AVAILABLE) && doc.getBoolean(Allele2DTO.MOUSE_AVAILABLE)){
				order.add("<tr>");
				order.add("<td><a target='_blank' href='" + dataUrl + "&type=mouse'><i class='fa fa-shopping-cart'><span class='orderFont'> Mouse</span></i></a></td>");

				if (doc.has(Allele2DTO.ALLELE_SIMPLE_IMAGE)) {
					order.add("<td><a target='_blank' href='" + doc.getString(Allele2DTO.ALLELE_SIMPLE_IMAGE) + "' title='map for allele'><i class='fa fa-th-list'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}

				if (doc.has(Allele2DTO.GENBANK_FILE)) {
					order.add("<td><a class='genbank' href='" + doc.getString(Allele2DTO.GENBANK_FILE) + "' title='genbank file for allele'><i class='fa fa-file-text'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}

				order.add("</tr>");
			}

			if (doc.has(Allele2DTO.TISSUES_AVAILABLE) && doc.has(Allele2DTO.TISSUE_TYPES) && doc.has(Allele2DTO.TISSUE_ENQUIRY_LINKS)){
				List<String> tissuesAvail = new ArrayList<>();

				JSONArray tissueTypes = doc.getJSONArray(Allele2DTO.TISSUE_TYPES);
				for (int t=0; t<tissueTypes.length(); t++){
					String href = "<a href='" + doc.getJSONArray(Allele2DTO.TISSUE_ENQUIRY_LINKS).get(t).toString() + "'>" + "<span><i class='fa fa-envelope'></i></span> " + tissueTypes.get(t).toString() + "</a>";
					tissuesAvail.add("<li>" + href + "</li>");
				}

//				String enquiry = "<span><i class='fa fa-question'></i> Tissue enquiry:</span><br>";
				String enquiry = "<span style='color: #0978a1; fill: #0978a1;'></i> Tissue Enquiry:</span><br>";

				order.add("<tr><td colspan=3 class='tissue'>" + enquiry + "<ul>" + StringUtils.join(tissuesAvail, "") + "</ul></td></tr>");
			}

			// populate the cells
			rowData.add(concateAlleleNameInfo(doc, request, qryStr));
			rowData.add(mutationType);
			rowData.add("<table><tbody>" + StringUtils.join(order, "") + "</tbody></table>");

			j.getJSONArray("aaData").put(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		j.put("facet_fields", facetFields);

		return j.toString();
	}

	public String parseJsonforGeneDataTable(JSONObject json, HttpServletRequest request, String qryStr, String fqOri, String solrCoreName, boolean legacyOnly) throws UnsupportedEncodingException, JSONException {

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		log.debug("TOTAL GENEs: " + totalDocs);

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		if (fqOri == null ){
			// display total GENE facet count as protein coding gene count
			j.put("useProteinCodingGeneCount", true);
		}
		else {
			j.put("useProteinCodingGeneCount", false);
		}

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		j.put("iDisplayStart", request.getAttribute("displayStart"));
		j.put("iDisplayLength", request.getAttribute("displayLength"));

		String baseUrl = request.getAttribute("baseUrl").toString();

		for (int i = 0; i < docs.length(); i ++) {

			List<String> rowData = new ArrayList<String>();

			JSONObject doc = docs.getJSONObject(i);
			String geneInfo = concateGeneInfo(doc, json, qryStr, request);
			rowData.add(geneInfo);

			// phenotyping status
			String mgiId = doc.getString(GeneDTO.MGI_ACCESSION_ID);
			String geneSymbol = doc.getString(GeneDTO.MARKER_SYMBOL);
			String productLink = request.getAttribute("mappedHostname").toString() + baseUrl + "/search/allele2?kw=\"" + geneSymbol + "\"";
			String geneLink = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString() + "/genes/" + mgiId;

			// ES cell/mice production status
			boolean toExport = false;

			String prodStatus = geneService.getLatestProductionStatuses(doc, toExport, productLink);
			rowData.add(prodStatus);


			String statusField = (doc.has(GeneDTO.PHENOTYPE_STATUS)) ? doc.getString(GeneDTO.PHENOTYPE_STATUS) : null;

			// made this as null by default: don't want to show this for now
			//Integer legacyPhenotypeStatus = null;
//			Integer legacyPhenotypeStatus = (doc.has(GeneDTO.LEGACY_PHENOTYPE_STATUS)) ? doc.getInt(GeneDTO.LEGACY_PHENOTYPE_STATUS) : null;
//
//			Integer hasQc = (doc.has(GeneDTO.HAS_QC)) ? doc.getInt(GeneDTO.HAS_QC) : null;
//			String phenotypeStatusHTMLRepresentation = geneService.getPhenotypingStatus(statusField, hasQc, legacyPhenotypeStatus, geneLink, toExport, legacyOnly);
//			rowData.add(phenotypeStatusHTMLRepresentation);

			// register of interest

//			boolean loggedIn = false;
//			try {
//
//				loggedIn = riUtils.isLoggedIn();
//
//			} catch (Exception e) {
//				// Nothing to do. If register interest service isn't working, a 500 is thrown. Handle as unauthenticated.
//			}
//
//			String target = paBaseUrl + "/search/gene?" + request.getQueryString();
//			String formAction;
//			String formMethod;
//			String registerIconClass;
//			String registerButtonText;
//
//			CsrfToken csrf = (CsrfToken) request.getAttribute("_csrf");
//
//			if (loggedIn) {
//
//				List<String> geneAccessionIds = riUtils.getGeneAccessionIds();
//
//				formMethod = "POST";
//
//				if (geneAccessionIds.contains(mgiId)) {
//
//					formAction = paBaseUrl + "/unregistration/gene/" + doc.getString("mgi_accession_id");
//					registerIconClass = "fa fa-sign-out";
//					registerButtonText = "Unregister interest";
//
//					String unregisterTag = buildRiFormTag(target, formAction, formMethod, registerIconClass, registerButtonText, csrf);
//
//					rowData.add(unregisterTag);
//
//				} else {
//
//					formAction = paBaseUrl + "/registration/gene/" + doc.getString("mgi_accession_id");
//					registerIconClass = "fa fa-sign-in";
//					registerButtonText = "Register interest";
//
//					String registerTag = buildRiFormTag(target, formAction, formMethod, registerIconClass, registerButtonText, csrf);
//
//					rowData.add(registerTag);
//				}
//
//			} else {
//
//				// Use Register Interest authenticated endpoint
//
//				formAction = paBaseUrl + "/authenticated";
//				formMethod = "GET";
//				registerIconClass = "fa fa-sign-in";
//				registerButtonText = "Login to register interest";
//
//				String interestTag = buildRiFormTag(target, formAction, formMethod, registerIconClass, registerButtonText, csrf);
//
//				rowData.add(interestTag);
//			}

			j.getJSONArray("aaData").put(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");

		//facetFields.
		j.put("facet_fields", facetFields);

		return j.toString();
	}

	private String buildRiFormTag(String target, String formAction, String formMethod, String registerIconClass, String registerButtonText, CsrfToken csrf) {
		// If csrf is disabled, the CsrfToken will be null. Account for that here to avoid NPE.
		String csrfLine = (csrf == null ? "" : "    <input type=\"hidden\" name=\"" + csrf.getParameterName() + "\" value=\"" + csrf.getToken() + "\" />\n");
		String riFormTag =
				" <span>\n" +
				"  <form style=\"border: 0;\">\n" +
						csrfLine +
						"    <input type=\"hidden\" name=\"target\" value=\"" + target + "\" />\n" +
						"      <button type=\"submit\" class=\"btn btn-block btn-primary btn-default\" formaction=\"" + formAction + "\" formmethod=\"" + formMethod + "\" style=\"vertical-align: top\">\n" +
						"        <i class=\"" + registerIconClass + "\"></i>\n" +
						"        " + registerButtonText + "\n" +
						"      </button>\n" +
						"    </a>\n" +
						"  </form>" +
		        " </span>\n";

		return riFormTag;
	}


	public String parseJsonforProtocolDataTable(JSONObject json, HttpServletRequest request, String solrCoreName) throws JSONException {

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		j.put("iDisplayStart", request.getAttribute("displayStart"));
		j.put("iDisplayLength", request.getAttribute("displayLength"));

		String impressBaseUrl = request.getAttribute("cmsBaseUrl") + "/impress/protocol/";

		for (int i = 0; i < docs.length(); i ++) {
			List<String> rowData = new ArrayList<String>();

			JSONObject doc = docs.getJSONObject(i);

			String parameter = doc.getString("parameter_name");
			rowData.add(parameter);

			// a parameter can belong to multiple procedures
			JSONArray procedures = doc.getJSONArray("procedure_name");
			JSONArray procedure_stable_keys = doc.getJSONArray("procedure_stable_key");

			List<String> procedureLinks = new ArrayList<String>();
			for (int p = 0; p < procedures.length(); p ++) {
				String procedure = procedures.get(p).toString();
				String procedure_stable_key = procedure_stable_keys.get(p).toString();
				procedureLinks.add("<a href='" + impressBaseUrl + procedure_stable_key + "'>" + procedure + "</a>");
			}

			rowData.add(StringUtils.join(procedureLinks, "<br>"));

			String pipeline = doc.getString("pipeline_name");
			rowData.add(pipeline);



			j.getJSONArray("aaData").put(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		j.put("facet_fields", facetFields);

		return j.toString();
	}

	public String parseJsonforMpDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreNamet) throws UnsupportedEncodingException, JSONException {

		String baseUrl = request.getAttribute("baseUrl").toString();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", new Object[0]);

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		jsonObject.put("iTotalRecords", totalDocs);
		jsonObject.put("iTotalDisplayRecords", totalDocs);
		jsonObject.put("iDisplayStart", request.getAttribute("displayStart"));
		jsonObject.put("iDisplayLength", request.getAttribute("displayLength"));

		qryStr = URLDecoder.decode(qryStr, "UTF-8");
		//System.out.println("kw decoded: "+ qryStr);

		// removes quotes or wildcard and highlight matched string
		qryStr = qryStr.toLowerCase().replaceAll("\"", "").replaceAll("\\*", "");

		for (int i = 0; i < docs.length(); i ++) {
			List<String> rowData = new ArrayList<String>();

			// array element is an alternate of facetField and facet
			//
			// Count
			JSONObject doc = docs.getJSONObject(i);

			String mpId = doc.getString("mp_id");
			String mpTerm = doc.getString("mp_term");
			String mpLink = "<a href='" + baseUrl + "/phenotypes/" + mpId + "'>" + mpTerm + "</a>";
			String mpCol = null;

			//System.out.println("DOC: "+ doc.toString());
			if (doc.has("mixSynQf")) {

				mpCol = "<div class='title'>" + mpLink + "</div>";

				JSONArray data = doc.getJSONArray("mixSynQf");
				int counter = 0;
				String synMatch = null;
				String syn = null;

				for (int j = 0; j < data.length(); j++)  {
				    Object d = data.get(j);
					counter++;

					if ( d.toString().startsWith("MP:") ){
						continue;
					}

					syn = d.toString();

					if ( d.toString().toLowerCase().contains(qryStr) ) {
						if (synMatch == null) {
							synMatch = Tools.highlightMatchedStrIfFound(qryStr, d.toString(), "span", "subMatch");
						}
					}
				}

				if (synMatch != null) {
					syn = synMatch;
				}

				if (counter > 1) {
					syn = syn + "<a href='" + baseUrl + "/phenotypes/" + mpId + "'> <span class='moreLess'>(Show more)</span></a>";
				}

				mpCol += "<div class='subinfo'>"
						+ "<span class='label'>synonym</span>: "
						+ syn
						+ "</div>";

				mpCol = "<div class='mpCol'>" + mpCol + "</div>";
				rowData.add(mpCol);
			} else {
				rowData.add(mpLink);
			}

			// some MP do not have definition
			String mpDef = doc.has(MpDTO.MP_DEFINITION) ? doc.getString(MpDTO.MP_DEFINITION): "No definition data available";

			int defaultLen = 30;
			if (mpDef != null && mpDef.length() > defaultLen) {
				String trimmedDef = mpDef.substring(0, defaultLen);
				// retrim if in the middle of a word
				trimmedDef = trimmedDef.substring(0, Math.min(trimmedDef.length(), trimmedDef.lastIndexOf(" ")));
				String partMpDef = "<div class='partDef'>" + Tools.highlightMatchedStrIfFound(qryStr, trimmedDef, "span", "subMatch") + " ...</div>";
				mpDef = "<div class='fullDef'>" + Tools.highlightMatchedStrIfFound(qryStr, mpDef, "span", "subMatch") + "</div>";
				rowData.add(partMpDef + mpDef + "<div class='moreLess'>Show more</div>");
			} else {
				rowData.add(mpDef);
			}

			// link out to ontology browser page
			rowData.add("<a href='" + baseUrl + "/ontologyBrowser?" + "termId=" + mpId + "'><i class=\"fa fa-share-alt-square\"></i> Browse</a>");

			jsonObject.getJSONArray("aaData").put(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		jsonObject.put("facet_fields", facetFields);

		return jsonObject.toString();
	}

	public String parseJsonforAnatomyDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreName) throws IOException, URISyntaxException, JSONException {

		//String baseUrl = request.getAttribute("baseUrl") + "/anatomy/";
		String baseUrl = request.getAttribute("baseUrl").toString();

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", new Object[0]);

		jsonObject.put("iTotalRecords", totalDocs);
		jsonObject.put("iTotalDisplayRecords", totalDocs);
		jsonObject.put("iDisplayStart", request.getAttribute("displayStart"));
		jsonObject.put("iDisplayLength", request.getAttribute("displayLength"));

		qryStr = URLDecoder.decode(qryStr, "UTF-8");
		//System.out.println("kw decoded: "+ qryStr);

		// removes quotes or wildcard and highlight matched string
		qryStr = qryStr.toLowerCase().replaceAll("\"", "").replaceAll("\\*", "");

		for (int i = 0; i < docs.length(); i ++) {
			List<String> rowData = new ArrayList<String>();

			// array element is an alternate of facetField and facetCount
			JSONObject doc = docs.getJSONObject(i);
			String anatomyId = doc.getString(AnatomyDTO.ANATOMY_ID);
			String anatomyTerm = doc.getString(AnatomyDTO.ANATOMY_TERM);
			String anatomylink = "<a href='" + baseUrl + "/anatomy/" + anatomyId + "'>" + anatomyTerm + "</a>";

			// check has expression data
			//Anatomy ma = JSONMAUtils.getMA(anatomyId, config);

			String anatomyCol = "<div class='title'>" + anatomylink + "</div>";

			if (doc.has(AnatomyDTO.ANATOMY_TERM_SYNONYM)) {

				JSONArray data = doc.getJSONArray(AnatomyDTO.ANATOMY_TERM_SYNONYM);
				int counter = 0;
				String synMatch = null;
				String syn = null;

				for (int j = 0; j < data.length(); j++) {
					Object d = data.get(j);
					counter++;

					syn = d.toString();
					if ( d.toString().toLowerCase().contains(qryStr) ) {
						if (synMatch == null) {
							synMatch = Tools.highlightMatchedStrIfFound(qryStr, d.toString(), "span", "subMatch");
						}
					}
				}

				if (synMatch != null) {
					syn = synMatch;
				}

				if (counter > 1) {
					syn = syn + "<a href='" + baseUrl + "/anatomy/" + anatomyId + "'> <span class='moreLess'>(Show more)</span></a>";
				}

				anatomyCol += "<div class='subinfo'>"
						+ "<span class='label'>synonym</span>: "
						+ syn
						+ "</div>";

				anatomyCol = "<div class='mpCol'>" + anatomyCol + "</div>";
				rowData.add(anatomyCol);
			} else {
				rowData.add(anatomyCol);
			}

			// developmental stage
			rowData.add(doc.getString("stage"));
			//display yes or no in anatomy search results in the LacZ Expression Data column 
			boolean expressionDataAvailable = hasExpressionData(anatomyId);


			rowData.add(expressionDataAvailable ? "<a href='" + baseUrl + "/anatomy/" + anatomyId + "#maHasExp" + "'>Yes</a>" :  "No");

			// link out to ontology browser page
			rowData.add("<a href='" + baseUrl + "/ontologyBrowser?" + "termId=" + anatomyId + "'><i class=\"fa fa-share-alt-square\"></i> Browse</a>");
			// some MP do not have definition
                /*String mpDef = "not applicable";
             try {
             maDef = doc.getString("ma_definition");
             }
             catch (Exception e) {
             //e.printStackTrace();
             }
             rowData.add(mpDef);*/
			jsonObject.getJSONArray("aaData").put(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		jsonObject.put("facet_fields", facetFields);

		return jsonObject.toString();
	}

	private boolean hasExpressionData(String anatomyId) throws IOException, URISyntaxException, JSONException {
		boolean expressionDataAvailable=false;
		//check legacy Sanger images for any images
		JSONObject maAssociatedExpressionImagesResponse = JSONImageUtils.getAnatomyAssociatedExpressionImages(anatomyId, config, 1);
		JSONArray expressionImageDocs = maAssociatedExpressionImagesResponse.getJSONObject("response").getJSONArray("docs");
		if(expressionImageDocs.length()>0){
			expressionDataAvailable=true;
		}
		//check experiment core for expression categorical data and impc images for parameter associated expression data
		try {
			if(expressionService.expressionDataAvailable(anatomyId)){
				expressionDataAvailable=true;
			}
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return expressionDataAvailable;
	}

	public String parseJsonforImpcImageDataTable(JSONObject json, String solrParams, boolean showImgView, HttpServletRequest request, String query, String fqOri, String solrCoreName) throws IOException, URISyntaxException, JSONException {

		int start = (int) request.getAttribute("displayStart");
		int length = (int) request.getAttribute("displayLength");

		fqOri = fqOri == null ? "*:*" : fqOri;

		String baseUrl = (String) request.getAttribute("baseUrl");
		//String mediaBaseUrl = config.get("mediaBaseUrl");
		String mediaBaseUrl = baseUrl + "/impcImages/images?";
		//https://dev.mousephenotype.org/data/impcImages/images?q=observation_type:image_record&fq=%28biological_sample_group:experimental%29%20AND%20%28procedure_name:%22Combined%20SHIRPA%20and%20Dysmorphology%22%29%20AND%20%28gene_symbol:Cox19%29

		if (showImgView) {

			// image view: one image per row
			JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
			int totalDocs = json.getJSONObject("response").getInt("numFound");

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("aaData", new Object[0]);
			jsonObject.put("imgHref", mediaBaseUrl + URLEncoder.encode(solrParams, "UTF-8"));
			jsonObject.put("imgCount", totalDocs);
			jsonObject.put("iTotalRecords", totalDocs);
			jsonObject.put("iTotalDisplayRecords", totalDocs);
			jsonObject.put("iDisplayStart", request.getAttribute("displayStart"));
			jsonObject.put("iDisplayLength", request.getAttribute("displayLength"));

			//String imgBaseUrl = mediaBaseUrl + "/";
			for (int i = 0; i < docs.length(); i ++) {

				List<String> rowData = new ArrayList<String>();
				JSONObject doc = docs.getJSONObject(i);
				String annots = "";

				String imgLink = null;

				if (doc.has("jpeg_url")) {

					String fullSizePath = doc.getString("jpeg_url"); //http://wwwdev.ebi.ac.uk/mi/media/omero/webgateway/render_image/7257/
					String thumbnailPath = doc.getString("thumbnail_url");
					String largeThumbNailPath = doc.getString("jpeg_url");
					String img = "<img class='thumbnailStyle' src='" + thumbnailPath + "'/>";
					if(doc.getString("download_url").contains("annotation")){
						imgLink = "<a rel='nofollow' href='" + doc.getString("download_url") + "'>" + img + "</a>";
					}else{
						imgLink = "<a rel='nofollow' class='fancybox' fullres='" + fullSizePath +  "' href='" + largeThumbNailPath + "'>" + img + "</a>";
					}
				} else {
					imgLink = Constants.IMAGE_COMING_SOON;
				}

				try {
					//ArrayList<String> mp = new ArrayList<String>();
					ArrayList<String> ma = new ArrayList<String>();
					ArrayList<String> procedures = new ArrayList<String>();

					int counter = 0;

					if (doc.has("anatomy_id")) {
						JSONArray termIds   = doc.getJSONArray("anatomy_id");
						JSONArray termNames = doc.getJSONArray("anatomy_term");
						for (int j = 0; j < termIds.length(); j++) {
							Object s = termIds.get(j);
							log.info(i + " - anatomy: " + termNames.get(counter).toString());
							log.debug(i + " - anatomy: " + termNames.get(counter).toString());
							String name = termNames.get(counter).toString();
							String maid = termIds.get(counter).toString();
							String url = request.getAttribute("baseUrl") + "/anatomy/" + maid;
							ma.add("<a href='" + url + "'>" + name + "</a>");

							counter++;
						}
					}

					if (doc.has("procedure_name")) {
						String procedureName = doc.getString("procedure_name");
						procedures.add(procedureName);
					}

					if ( ma.size() == 1 ){
						annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + StringUtils.join(ma, ", ") + "</span>";
					}
					else if ( ma.size() > 1 ){
						String list = "<ul class='imgMa'><li>" + StringUtils.join(ma, "</li><li>") + "</li></ul>";
						annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + list + "</span>";
					}
					if (procedures.size() == 1) {
						annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + StringUtils.join(procedures, ", ") + "</span>";
					} else if (procedures.size() > 1) {
						String list = "<ul class='imgProcedure'><li>" + StringUtils.join(procedures, "</li><li>") + "</li></ul>";
						annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + list + "</span>";
					}

					// gene link
					if (doc.has("gene_symbol")) {
						String geneSymbol = doc.getString("gene_symbol");
						String geneAccessionId = doc.getString("gene_accession_id");
						String url = baseUrl + "/genes/" + geneAccessionId;
						String geneLink = "<a href='" + url + "'>" + geneSymbol + "</a>";

						annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + geneLink + "</span>";
					}

					rowData.add(annots);
					rowData.add(imgLink);

					jsonObject.getJSONArray("aaData").put(rowData);
				} catch (Exception e) {
					// some images have no annotations
					rowData.add(Constants.NO_INFORMATION_AVAILABLE);
					rowData.add(imgLink);
					jsonObject.getJSONArray("aaData").put(rowData);
				}
			}

			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
			jsonObject.put("facet_fields", facetFields);

			return jsonObject.toString();
		} else {

			// annotation view: images group by annotationTerm per row
			String defaultQStr = "observation_type:image_record&qf=imgQf&defType=edismax";

			if (query != "") {
				defaultQStr = "q=" + query + " AND " + defaultQStr;
			} else {
				defaultQStr = "q=" + defaultQStr;
			}

			List<AnnotNameValCount> annots = solrIndex.mergeImpcFacets(query, json, baseUrl);

			int numAnnots = annots.size();

			JSONObject j = new JSONObject();
			j.put("aaData", new Object[0]);
			j.put("imgHref", mediaBaseUrl + URLEncoder.encode(solrParams, "UTF-8"));
			j.put("imgCount", json.getJSONObject("response").getInt("numFound"));
			j.put("iTotalRecords", numAnnots);
			j.put("iTotalDisplayRecords", numAnnots);
			j.put("iDisplayStart", request.getAttribute("displayStart"));
			j.put("iDisplayLength", request.getAttribute("displayLength"));

			int end = start + length > numAnnots ? numAnnots : start + length;
			for (int i = start; i < end; i = i + 1) {

				List<String> rowData = new ArrayList<String>();

				AnnotNameValCount annot            = annots.get(i);
				String            displayAnnotName = annot.getName();
				String            annotVal         = annot.getVal();

				String qryStr = query.replaceAll("\"", "");
				String displayLabel = annotVal;
				if ( annot.getRelatedSynonym() != null ){
					displayLabel = annotVal + " (Related synonym: " + Tools.highlightMatchedStrIfFound(qryStr, annot.getRelatedSynonym(), "span", "subMatch") + ")";
				} else if (annot.getMarkerSynonym() != null){
					displayLabel = annotVal + " (Synonym: " + Tools.highlightMatchedStrIfFound(qryStr, annot.getMarkerSynonym(), "span", "subMatch") + ")";
				} else if (annot.getParamAssociationName() != null) {
					displayLabel = annotVal + " (Parameter association: " + Tools.highlightMatchedStrIfFound(qryStr, annot.getParamAssociationName(), "span", "subMatch") + ")";
				}

				String annotId = annot.getId();
				String valLink = annot.getLink() != null ? "<a href='" + annot.getLink()  + "'>" + displayLabel + "</a>" : displayLabel;
				//String valLink = "<a href='" + link + "'>" + annotVal + "</a>";

				String fqVal = annot.id != null ? annot.getId() : annot.getVal();
				String thisFqStr = null;
				String annotFilter = annot.getFq() + ":\"" + fqVal + "\"";
				String fqOri2 = fqOri.replaceAll("\\(|\\)", "");
				if ( fqOri2.equals(annotFilter)){
					thisFqStr = fqOri;
				}
				else {
					thisFqStr = fqOri + " AND " + annotFilter;
				}
				//System.out.println("this fq str: "+thisFqStr);
				//https://dev.mousephenotype.org/data/impcImages/images?q=observation_type:image_record&fq=biological_sample_group:experimental"
				String imgSubSetLink = null;
				String thisImgUrl = null;

				List pathAndImgCount = solrUtilsWeb.fetchImpcImagePathByAnnotName(query, thisFqStr);

				int imgCount = (int) pathAndImgCount.get(1);

				String unit = imgCount > 1 ? "images" : "image";

				if (imgCount > 0) {
					String currFqStr = null;
					String subFqStr = null; //fqOri.equals("*:*") ? null : fqOri;

					if (displayAnnotName.equals("Gene")) {
						subFqStr = "gene_symbol:\"" + annotVal + "\"";
					} else if (displayAnnotName.equals("Procedure")) {
						subFqStr = "procedure_name:\"" + annotVal + "\"";
					} else if (displayAnnotName.equals("Anatomy")) {
						subFqStr = "anatomy_id:\"" + annotId + "\"";
					}

					if ( fqOri.equals("*:*")) {
						currFqStr = subFqStr;
					}
					else {
						if (!fqOri.equals("("+ subFqStr+")")) {
							currFqStr = fqOri + " AND " + subFqStr;
						}
						else {
							currFqStr = fqOri;
						}
					}

					//String thisImgUrl = mediaBaseUrl + defaultQStr + " AND (" + query + ")&" + defaultFqStr;
					thisImgUrl = mediaBaseUrl + defaultQStr + "&fq=" + currFqStr;

					imgSubSetLink = "<a rel='nofollow' href='" + thisImgUrl + "'>" + imgCount + " " + unit + "</a>";

					rowData.add("<span class='annotType'>" + displayAnnotName + "</span>: " + valLink + " (" + imgSubSetLink + ")");

					rowData.add("<a rel='nofollow' href='" + thisImgUrl + "'>" + pathAndImgCount.get(0) + "</a>");
					//rowData.add(pathAndImgCount.get(0).toString());  // link to this image only
					j.getJSONArray("aaData").put(rowData);
				}

			}

			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
			j.put("facet_fields", facetFields);
			//System.out.println("JSON STR: " + j.toString());
			return j.toString();
		}
	}

	public String parseJsonforImageDataTable(JSONObject json, String solrParams, boolean showImgView, HttpServletRequest request, String query, String fqOri, String solrCoreName) throws IOException, URISyntaxException, JSONException {

		String mediaBaseUrl = config.get("mediaBaseUrl");

		int start = (int) request.getAttribute("displayStart");
		int length = (int) request.getAttribute("displayLength");

		String imgUrl = request.getAttribute("baseUrl") + "/imagesb?" + solrParams;

		if (showImgView) {
			// image view: one image per row
			JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
			int totalDocs = json.getJSONObject("response").getInt("numFound");

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("aaData", new Object[0]);
			jsonObject.put("imgHref", imgUrl);
			jsonObject.put("imgCount", totalDocs);
			jsonObject.put("iTotalRecords", totalDocs);
			jsonObject.put("iTotalDisplayRecords", totalDocs);
			jsonObject.put("iDisplayStart", request.getAttribute("displayStart"));
			jsonObject.put("iDisplayLength", request.getAttribute("displayLength"));

			String imgBaseUrl = mediaBaseUrl + "/";

			for (int i = 0; i < docs.length(); i ++) {

				List<String> rowData = new ArrayList<String>();
				JSONObject doc = docs.getJSONObject(i);

				String annots = "";

				String largeThumbNailPath = imgBaseUrl + doc.getString("largeThumbnailFilePath");
				String img = "<img src='" + imgBaseUrl + doc.getString("smallThumbnailFilePath") + "'/>";
				String fullSizePath = largeThumbNailPath.replace("tn_large", "full");
				String imgLink = "<a rel='nofollow' class='fancybox' fullres='" + fullSizePath + "' href='" + largeThumbNailPath + "'>" + img + "</a>";

				try {
					ArrayList<String> mp = new ArrayList<String>();
					ArrayList<String> ma = new ArrayList<String>();
					ArrayList<String> exp = new ArrayList<String>();
					ArrayList<String> emap = new ArrayList<String>();

					int counter = 0;

					if (doc.has("annotationTermId")) {
						JSONArray termIds = doc.getJSONArray("annotationTermId");

						JSONArray termNames = new JSONArray();
						if ( doc.has("annotationTermName") ) {
							termNames = doc.getJSONArray("annotationTermName");
						}
						else {
							termNames = termIds; // temporary solution for those term ids that do not have term name
						}
						for (int j = 0; j < termIds.length(); j++) {
							Object s = termIds.get(j);
							if (s.toString().startsWith("MA:")) {
								log.debug(i + " - MA: " + termNames.get(counter).toString());
								String name = termNames.get(counter).toString();
								String maid = termIds.get(counter).toString();
								String url = request.getAttribute("baseUrl") + "/anatomy/" + maid;
								ma.add("<a href='" + url + "'>" + name + "</a>");
							}
							else if (s.toString().startsWith("MP:")) {
								log.debug(i + " - MP: " + termNames.get(counter).toString());
								log.debug(i + " - MP: " + termIds.get(counter).toString());
								String mpid = termIds.get(counter).toString();
								String name = termNames.get(counter).toString();
								String url = request.getAttribute("baseUrl") + "/phenotypes/" + mpid;
								mp.add("<a href='" + url + "'>" + name + "</a>");
							}
							else if (s.toString().startsWith("EMAP:")) {
								String emapid = termIds.get(counter).toString();
								String name = termNames.get(counter).toString();
								//String url = request.getAttribute("baseUrl") + "/phenotypes/" + mpid;
								//emap.add("<a href='" + url + "'>" + name + "</a>");
								emap.add(name);  // we do not have page for emap yet
							}

							counter ++;
						}
					}

					if (doc.has("expName")) {
						JSONArray expNames = doc.getJSONArray("expName");
						for (int j = 0; j < expNames.length(); j++) {
							Object s = expNames.get(j);
							log.debug(i + " - expTERM: " + s.toString());
							exp.add(s.toString());
						}
					}

					if (mp.size() == 1) {
						annots += "<span class='imgAnnots'><span class='annotType'>MP</span>: " + StringUtils.join(mp, ", ") + "</span>";
					} else if (mp.size() > 1) {
						String list = "<ul class='imgMp'><li>" + StringUtils.join(mp, "</li><li>") + "</li></ul>";
						annots += "<span class='imgAnnots'><span class='annotType'>MP</span>: " + list + "</span>";
					}

					if (ma.size() == 1) {
						annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + StringUtils.join(ma, ", ") + "</span>";
					} else if (ma.size() > 1) {
						String list = "<ul class='imgMa'><li>" + StringUtils.join(ma, "</li><li>") + "</li></ul>";
						annots += "<span class='imgAnnots'><span class='annotType'>MA</span>: " + list + "</span>";
					}

					if (exp.size() == 1) {
						annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + StringUtils.join(exp, ", ") + "</span>";
					} else if (exp.size() > 1) {
						String list = "<ul class='imgProcedure'><li>" + StringUtils.join(exp, "</li><li>") + "</li></ul>";
						annots += "<span class='imgAnnots'><span class='annotType'>Procedure</span>: " + list + "</span>";
					}

					if (emap.size() == 1) {
						annots += "<span class='imgAnnots'><span class='annotType'>EMAP</span>: " + StringUtils.join(emap, ", ") + "</span>";
					} else if (exp.size() > 1) {
						String list = "<ul class='imgEmap'><li>" + StringUtils.join(emap, "</li><li>") + "</li></ul>";
						annots += "<span class='imgAnnots'><span class='annotType'>EMAP</span>: " + list + "</span>";
					}

					ArrayList<String> gene = fetchImgGeneAnnotations(doc, request);

					if (gene.size() == 1) {
						annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + StringUtils.join(gene, ",") + "</span>";
					} else if (gene.size() > 1) {
						String list = "<ul class='imgGene'><li>" + StringUtils.join(gene, "</li><li>") + "</li></ul>";
						annots += "<span class='imgAnnots'><span class='annotType'>Gene</span>: " + list + "</span>";
					}

					rowData.add(annots);
					rowData.add(imgLink);
					jsonObject.getJSONArray("aaData").put(rowData);
				} catch (Exception e) {
					// some images have no annotations
					rowData.add(Constants.NO_INFORMATION_AVAILABLE);
					rowData.add(imgLink);
					jsonObject.getJSONArray("aaData").put(rowData);
				}
			}

			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
			jsonObject.put("facet_fields", facetFields);

			return jsonObject.toString();
		} else {
			// annotation view: images group by annotationTerm per row

			String fqStr = fqOri;
			if ( fqStr == null ){
				solrParams += "&fq=*:*";
			}


			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");

			JSONArray facets = solrIndex.mergeFacets(facetFields);

			int numFacets = facets.length();

			//System.out.println("Number of facets: " + numFacets);
			JSONObject j = new JSONObject();
			j.put("aaData", new Object[0]);
			j.put("imgHref", imgUrl);
			j.put("imgCount", json.getJSONObject("response").getInt("numFound"));
			j.put("iTotalRecords", numFacets / 2);
			j.put("iTotalDisplayRecords", numFacets / 2);
			j.put("iDisplayStart", request.getAttribute("displayStart"));
			j.put("iDisplayLength", request.getAttribute("displayLength"));

			int end = start + length;
			//System.out.println("Start: "+start*2+", End: "+end*2);

			// The facets array looks like:
			//   [0] = facet name
			//   [1] = facet count for [0]
			//   [n] = facet name
			//   [n+1] = facet count for [n]
			// So we start at 2 times the start to skip over all the n+1
			// and increase the end similarly.
			for (int i = start * 2; i < end * 2; i = i + 2) {

				if (facets.length() <= i) {
					break;
				}//stop when we hit the end

				String[] names = facets.get(i).toString().split("_");

				if (names.length == 2) {  // only want facet value of xxx_yyy

					List<String> rowData = new ArrayList<>();


					Map<String, String> hm = solrIndex.renderFacetField(names, request.getAttribute("mappedHostname").toString(), request.getAttribute("baseUrl").toString()); //MA:xxx, MP:xxx, MGI:xxx, exp

					String displayAnnotName = "<span class='annotType'>" + hm.get("label").toString() + "</span>: " + hm.get("link").toString();
					String facetField = hm.get("field").toString();

					String imgCount = facets.get(i + 1).toString();
					String unit = Integer.parseInt(imgCount) > 1 ? "images" : "image";

					imgUrl = imgUrl.replaceAll("&q=.+&", "&defType=edismax&q=" + query + " AND " + facetField + ":\"" + names[0] + "\"&");

					String imgSubSetLink = "<a rel='nofollow' href='" + imgUrl + "'>" + imgCount + " " + unit + "</a>";

					rowData.add(displayAnnotName + " (" + imgSubSetLink + ")");

					// messy here, as ontodb (the latest term name info) may not have the terms in ann_annotation table
					// so we just use the name from ann_annotation table
					String thisFqStr = "";
					String fq = "";


					if (facetField == "annotationTermName") {
						fq = "(" + facetField + ":\"" + names[0] + "\" OR annotationTermName:\"" + names[0] + "\")";
					} else {
						fq = facetField + ":\"" + names[0] + "\"";
					}

					thisFqStr = "fq=" + (fqStr == null ? fq : fqStr + " AND " + fq);


					rowData.add(fetchImagePathByAnnotName(query, thisFqStr));

					j.getJSONArray("aaData").put(rowData);
				}

			}

			j.put("facet_fields", facetFields);

			return j.toString();
		}
	}

	public String parseJsonforDiseaseDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreName) throws JSONException {

		String baseUrl = request.getAttribute("baseUrl") + "/disease/";

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("aaData", new Object[0]);

		jsonObject.put("iTotalRecords", totalDocs);
		jsonObject.put("iTotalDisplayRecords", totalDocs);
		jsonObject.put("iDisplayStart", request.getAttribute("displayStart"));
		jsonObject.put("iDisplayLength", request.getAttribute("displayLength"));

		Map<String, String> srcBaseUrlMap = new HashMap<>();
		srcBaseUrlMap.put("OMIM", "http://omim.org/entry/");
		srcBaseUrlMap.put("ORPHANET", "http://www.orpha.net/consor/cgi-bin/OC_Exp.php?Lng=GB&Expert=");
		srcBaseUrlMap.put("DECIPHER", "http://decipher.sanger.ac.uk/syndrome/");

		qryStr = qryStr.replaceAll("\"", "");

		for (int i = 0; i < docs.length(); i ++) {
			List<String> rowData = new ArrayList<String>();

			// disease link
			JSONObject doc = docs.getJSONObject(i);
			//System.out.println(" === JSON DOC IN DISEASE === : " + doc.toString());
			String diseaseId = doc.getString("disease_id");
			String diseaseTerm = doc.getString("disease_term");
			String diseaseCol = "<a href='" + baseUrl + diseaseId + "'>" + diseaseTerm + "</a>";

			// disease column
			if (doc.has("disease_alts")) {

				JSONArray data = doc.getJSONArray("disease_alts");
				int counter = 0;
				String synMatch = null;
				String syn = null;

				for (int j = 0; j < data.length(); j++) {
					Object d = data.get(j);
					counter++;
					syn = d.toString();

					if ( syn.toLowerCase().contains(qryStr.toLowerCase()) ) {
						if (synMatch == null) {
							synMatch = Tools.highlightMatchedStrIfFound(qryStr, syn, "span", "subMatch");
						}
					}
				}

				if (synMatch != null) {
					syn = synMatch;
				}

				if (counter > 1) {
					syn = syn + "<a href='" + baseUrl + "/disease/" + diseaseId + "'> <span class='moreLess'>(Show more)</span></a>";
				}

				diseaseCol += "<div class='subinfo'>"
						+ "<span class='label'>synonym</span>: "
						+ syn
						+ "</div>";

				diseaseCol = "<div class='diseaseCol'>" + diseaseCol + "</div>";
				rowData.add(diseaseCol);
			} else {
				rowData.add(diseaseCol);
			}

			// source column
			String src = doc.getString("disease_source");
			String[] IdParts =  diseaseId.split(":");
			String digits = IdParts[1];
			String srcId = src + ":" + digits;
			rowData.add("<a target='_blank' href='" + srcBaseUrlMap.get(src) + digits + "'>" + srcId + "</a>");

			// curated data: human/mouse
			String human = "<span class='status done curatedHuman'>human</span>";
			String mice = "<span class='status done curatedMice'>mice</span>";

			// predicted data: impc/mgi
			String impc = "<span class='status done candidateImpc'>IMPC</span>";
			String mgi = "<span class='status done candidateMgi'>MGI</span>";

			jsonObject.getJSONArray("aaData").put(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		jsonObject.put("facet_fields", facetFields);

		return jsonObject.toString();
	}

	private ArrayList<String> fetchImgGeneAnnotations(JSONObject doc, HttpServletRequest request) {

		ArrayList<String> gene = new ArrayList<String>();

		try {
			if (doc.has("symbol_gene")) {
				JSONArray geneSymbols = doc.getJSONArray("symbol_gene");
				for (int i = 0; i < geneSymbols.length(); i++) {
					Object s = geneSymbols.get(i);
					String[] names = s.toString().split("_");
					String url = request.getAttribute("baseUrl") + "/genes/" + names[1];
					gene.add("<a href='" + url + "'>" + names[0] + "</a>");
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return gene;
	}

	public String fetchImagePathByAnnotName(String query, String fqStr) throws IOException, URISyntaxException, JSONException {

		String mediaBaseUrl = config.get("mediaBaseUrl");

		final int maxNum = 4; // max num of images to display in grid column

		String queryUrl = config.get("internalSolrUrl")
				+ "/images/select?qf=auto_suggest&defType=edismax&wt=json&q=" + query
				+ "&" + fqStr
				+ "&rows=" + maxNum;

		List<String> imgPath = new ArrayList<String>();

		JSONObject thumbnailJson = solrIndex.getResults(queryUrl);
		JSONArray docs = thumbnailJson.getJSONObject("response").getJSONArray("docs");

		int dataLen = docs.length() < 5 ? docs.length() : maxNum;

		for (int i = 0; i < dataLen; i ++) {
			JSONObject doc = docs.getJSONObject(i);
			String largeThumbNailPath = mediaBaseUrl + "/" + doc.getString("largeThumbnailFilePath");
			String fullSizePath = largeThumbNailPath.replace("tn_large", "full");
			String img = "<img src='" + mediaBaseUrl + "/" + doc.getString("smallThumbnailFilePath") + "'/>";
			String link = "<a rel='nofollow' class='fancybox' fullres='" + fullSizePath + "' href='" + largeThumbNailPath + "'>" + img + "</a>";

			imgPath.add(link);
		}

		return StringUtils.join(imgPath, "");

	}
	private String concateAlleleNameInfo(JSONObject doc, HttpServletRequest request, String qryStr) throws UnsupportedEncodingException, JSONException{

		List<String> alleleNameInfo = new ArrayList<String>();

		String markerAcc = doc.getString(Allele2DTO.MGI_ACCESSION_ID);
		String alleleName = doc.getString(Allele2DTO.ALLELE_NAME);
		String alleleUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString() + "/alleles/" + markerAcc + "/" + alleleName;
		String markerSymbol = doc.getString(Allele2DTO.MARKER_SYMBOL);
		String alleleLink = "<a href='" + alleleUrl + "'>" + markerSymbol + "<sup>" + alleleName + "</sup></a>";
		String geneUrl = request.getAttribute("baseUrl") + "/genes/" + markerAcc;

		String[] fields = {"marker_name", "marker_synonym"};

		qryStr = URLDecoder.decode(qryStr, "UTF-8");
		//System.out.println("kw decoded: "+ qryStr);

		// removes quotes or wildcard and highlight matched string
		qryStr = qryStr.toLowerCase().replaceAll("\"", "").replaceAll("\\*", "");

		for (int i = 0; i < fields.length; i ++) {
			try {
				//"highlighting":{"MGI:97489":{"marker_symbol":["<em>Pax</em>5"],"synonym":["<em>Pax</em>-5"]},

				String field = fields[i];
				List<String> info = new ArrayList<String>();

				if (field.equals("marker_name")) {
					info.add(Tools.highlightMatchedStrIfFound(qryStr, doc.getString(field), "span", "subMatch"));
				}
				else if (field.equals("marker_synonym")) {
					JSONArray data = doc.getJSONArray(field);
					int counter = 0;

					String synMatch = null;
					String syn = null;

					for (int j = 0; j < data.length(); j++) {
						Object d = data.get(j);
						counter++;

						syn = d.toString();
						if ( d.toString().toLowerCase().contains(qryStr) ) {
							if ( synMatch == null ) {
								synMatch = Tools.highlightMatchedStrIfFound(qryStr, d.toString(), "span", "subMatch");
							}
						}
					}

					if ( synMatch != null ){
						syn = synMatch;
					}

					if ( counter == 1 ){
						info.add(syn);
					}
					else if ( counter > 1 ){
						info.add(syn + "<a href='" + geneUrl + "'> <span class='moreLess'>(see more)</span></a>");
					}
				}

				// field string shown to the users
				if ( field.equals("marker_name" ) ){
					field = "gene name";
				}
				else if ( field.equals("marker_synonym") ){
					field = "gene synonym";
				}
				String ulClass = "synonym";

				//geneInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
				if (info.size() > 1) {
					String fieldDisplay = "<ul class='" + ulClass + "'><li>" + StringUtils.join(info, "</li><li>") + "</li></ul>";
					alleleNameInfo.add("<span class='label'>" + field + "</span>: " + fieldDisplay);
				} else {
					alleleNameInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		//return "<div class='geneCol'>" + markerSymbolLink + StringUtils.join(geneInfo, "<br>") + "</div>";
		return "<div class='alleleNameCol'><div class='title'>"
				+ alleleLink
				+ "</div>"
				+ "<div class='subinfo'>"
				+ StringUtils.join(alleleNameInfo, "<br>")
				+ "</div>";

	}

	private String concateGeneInfo(JSONObject doc, JSONObject json, String qryStr, HttpServletRequest request) throws JSONException {

		List<String> geneInfo = new ArrayList<String>();

		String markerSymbol = "<span class='gSymbol'>" + doc.getString("marker_symbol") + "</span>";
		String mgiId = doc.getString("mgi_accession_id");
		//System.out.println(request.getAttribute("baseUrl"));
		String geneUrl = request.getAttribute("baseUrl") + "/genes/" + mgiId;
		//String markerSymbolLink = "<a href='" + geneUrl + "' target='_blank'>" + markerSymbol + "</a>";
		String markerSymbolLink = "<a href='" + geneUrl + "'>" + markerSymbol + "</a>";

		String[] fields = {"marker_name", "human_gene_symbol", "marker_synonym"};
		for (int i = 0; i < fields.length; i ++) {
			try {
				//"highlighting":{"MGI:97489":{"marker_symbol":["<em>Pax</em>5"],"synonym":["<em>Pax</em>-5"]},

				qryStr = URLDecoder.decode(qryStr, "UTF-8");
				//System.out.println("kw decoded: "+ qryStr);

				// removes quotes or wildcard and highlight matched string
				qryStr = qryStr.toLowerCase().replaceAll("\"", "").replaceAll("\\*", "");

				//System.out.println(qryStr);
				String field = fields[i];
				List<String> info = new ArrayList<String>();

				if (field.equals("marker_name")) {
					info.add(Tools.highlightMatchedStrIfFound(qryStr, doc.getString(field), "span", "subMatch"));
				} else if (field.equals("human_gene_symbol")) {
					JSONArray data = doc.getJSONArray(field);

					for (int j = 0; j < data.length(); j++) {
						Object h = data.get(j);
						info.add(Tools.highlightMatchedStrIfFound(qryStr, h.toString(), "span", "subMatch"));
					}
				} else if (field.equals("marker_synonym")) {
					JSONArray data = doc.getJSONArray(field);
					int counter = 0;
					String synMatch = null;
					String syn = null;

					for (int j = 0; j < data.length(); j++) {
						Object d = data.get(j);
						counter++;

						syn = d.toString();

						if ( d.toString().toLowerCase().contains(qryStr) ) {
							if ( synMatch == null ) {
								synMatch = Tools.highlightMatchedStrIfFound(qryStr, d.toString(), "span", "subMatch");
							}
						}
					}

					if ( synMatch != null ){
						syn = synMatch;
					}

					if ( counter == 1 ){
						info.add(syn);
					}
					else if ( counter > 1 ){
						info.add(syn + "<a href='" + geneUrl + "'> <span class='moreLess'>(see more)</span></a>");
					}
				}


				// field string shown to the users
				if ( field.equals("human_gene_symbol") ){
					field = "human ortholog";
				}
				else if ( field.equals("marker_name" ) ){
					field = "name";
				}
				else if ( field.equals("marker_synonym") ){
					field = "synonym";
				}
				String ulClass = field == "human ortholog" ? "ortholog" : "synonym";

				//geneInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
				if (info.size() > 1) {
					String fieldDisplay = "<ul class='" + ulClass + "'><li>" + StringUtils.join(info, "</li><li>") + "</li></ul>";
					geneInfo.add("<span class='label'>" + field + "</span>: " + fieldDisplay);
				} else {
					geneInfo.add("<span class='label'>" + field + "</span>: " + StringUtils.join(info, ", "));
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		//return "<div class='geneCol'>" + markerSymbolLink + StringUtils.join(geneInfo, "<br>") + "</div>";
		return "<div class='geneCol'><div class='title'>"
				+ markerSymbolLink
				+ "</div>"
				+ "<div class='subinfo'>"
				+ StringUtils.join(geneInfo, "<br>")
				+ "</div>";
	}
}