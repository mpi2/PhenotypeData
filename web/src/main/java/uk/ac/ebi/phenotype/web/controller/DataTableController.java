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
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jackson.map.ObjectMapper;
import org.mousephenotype.cda.db.dao.GenomicFeatureDAO;
import org.mousephenotype.cda.db.dao.ReferenceDAO;
import org.mousephenotype.cda.solr.generic.util.JSONImageUtils;
import org.mousephenotype.cda.solr.generic.util.Tools;
import org.mousephenotype.cda.solr.service.ExpressionService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.SolrIndex;
import org.mousephenotype.cda.solr.service.SolrIndex.AnnotNameValCount;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import uk.ac.ebi.phenodigm.dao.PhenoDigmWebDao;
import uk.ac.ebi.phenodigm.model.GeneIdentifier;
import uk.ac.ebi.phenodigm.web.AssociationSummary;
import uk.ac.ebi.phenodigm.web.DiseaseAssociationSummary;
import uk.ac.ebi.phenotype.generic.util.RegisterInterestDrupalSolr;

@Controller
public class DataTableController {

	private static final int ArrayList = 0;

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@Autowired
	private SolrIndex solrIndex;

	@Autowired
	private GeneService geneService;

	@Autowired
	private MpService mpService;

	@Autowired
	ExpressionService expressionService;

	@Resource(name = "globalConfiguration")
	private Map<String, String> config;

	@Autowired
	@Qualifier("admintoolsDataSource")
	private DataSource admintoolsDataSource;

	@Autowired
	@Qualifier("komp2DataSource")
	private DataSource komp2DataSource;

	@Autowired
	private PaperController paperController;

	private String IMG_NOT_FOUND = "Image coming soon<br>";
	private String NO_INFO_MSG = "No information available";

	@Autowired
	private ReferenceDAO referenceDAO;

	@Autowired
	private GenomicFeatureDAO genesDao;

	@Autowired
	private PhenoDigmWebDao phenoDigmDao;
	private final double rawScoreCutoff = 1.97;

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
			Model model) throws IOException, URISyntaxException, SolrServerException {

		String content = null;

		String oriDataTypeName = dataTypeName;
		List<String> queryIds = Arrays.asList(idlist.split(","));
		Long time = System.currentTimeMillis();

		List<String> mgiIds = new ArrayList<>();
		List<org.mousephenotype.cda.solr.service.dto.GeneDTO> genes = new ArrayList<>();
		List<QueryResponse> solrResponses = new ArrayList<>();

		List<String> batchIdList = new ArrayList<>();
		String batchIdListStr = null;

		int counter = 0;

		//System.out.println("id length: "+ queryIds.size());
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
		/*
		if ( genes.size() == 0 ){
			mgiIds = queryIds;
		}*/

		//System.out.println("Get " + mgiIds.size() + " out of " + queryIds.size() + " mgi genes by ensembl id/marker_symbol took: " + (System.currentTimeMillis() - time));
		//System.out.println("mgi id: " + mgiIds);

		content = fetchBatchQueryDataTableJson(request, solrResponses, fllist, oriDataTypeName, queryIds);
		return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
	}

	private JSONObject prepareHpMpMapping(QueryResponse solrResponse) {

		JSONObject j = new JSONObject();
		//Map<String, List<HpTermMpId>> hp2mp = new HashMap<>();
		Map<String, List<String>> hp2mp = new HashMap<>();
		Set<String> mpids = new HashSet<>();

		SolrDocumentList results = solrResponse.getResults();
		for (int i = 0; i < results.size(); ++i) {
			SolrDocument doc = results.get(i);

			Map<String, Object> docMap = doc.getFieldValueMap();
			//String hp_id = (String) docMap.get("hp_id");
			//String hp_term = (String) docMap.get("hp_term");
			String hpidTerm = (String) docMap.get("hp_id") + "_" + (String) docMap.get("hp_term");
			String mp_id = (String) docMap.get("mp_id");
			mpids.add("\"" + mp_id + "\"");

			if ( ! hp2mp.containsKey(hpidTerm) ){
				hp2mp.put(hpidTerm, new ArrayList<String>());
			}
			hp2mp.get(hpidTerm).add(mp_id);
		}

		j.put("map", hp2mp);

		List<String> ids = new ArrayList<>();
		ids.addAll(mpids);
		String idlist = StringUtils.join(ids, ",");
		j.put("idlist",  idlist);

		return j;
	}

	public String fetchBatchQueryDataTableJson(HttpServletRequest request, List<QueryResponse> solrResponses, String fllist, String dataTypeName, List<String> queryIds ) {

		String oriDataTypeName = dataTypeName;
		if ( dataTypeName.contains("marker_symbol")){
			dataTypeName = "marker_symbol";
		}

		String hostName = request.getAttribute("mappedHostname").toString().replace("https:", "http:");
		String baseUrl = request.getAttribute("baseUrl").toString();

		String NA = "Info not available";

		List<String> flList = Arrays.asList(StringUtils.split(fllist, ","));
		//System.out.println("flist: " + flList);
		Set<String> foundIds = new HashSet<>();
		//List<String> foundIds = new ArrayList<>();

		//System.out.println("responses: " + solrResponses.size());

		SolrDocumentList results = new SolrDocumentList();

		for ( QueryResponse solrResponse : solrResponses ){
			results.addAll(solrResponse.getResults());
		}
		int totalDocs = results.size();

		Map<String, String> dataTypeId = new HashMap<>();
		dataTypeId.put("gene", GeneDTO.MGI_ACCESSION_ID);
		dataTypeId.put("marker_symbol", GeneDTO.MGI_ACCESSION_ID);
		dataTypeId.put("ensembl", GeneDTO.MGI_ACCESSION_ID);

		dataTypeId.put("mp", MpDTO.MP_ID);
		dataTypeId.put("anatomy", "anatomy_id");
		dataTypeId.put("hp", "hp_id");
		dataTypeId.put("disease", "disease_id");

		Map<String, String> dataTypePath = new HashMap<>();
		dataTypePath.put("gene", "genes");
		dataTypePath.put("marker_symbol", "genes");
		dataTypePath.put("mp", "phenotypes");
		dataTypePath.put("anatomy", "anatomy");
		dataTypePath.put("disease", "disease");

		// for sorting by user query list
		Map<String, Integer> sortCol = new HashMap<>();
		sortCol.put("gene", 0);
		sortCol.put("mouse_marker_symbol", 1);
		sortCol.put("human_marker_symbol", 2);
		sortCol.put("ensembl", 1);
		sortCol.put("hp", 0);
		sortCol.put("mp", 0);
		sortCol.put("anatomy", 0);
		sortCol.put("disease", 0);

		Map<String, List<String>> idRow = new HashMap<>();

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);

		int fieldCount = 0;

//		System.out.println("totaldocs:" + totalDocs);
		for (int i = 0; i < results.size(); ++i) {
			SolrDocument doc = results.get(i);

			List<String> rowData = new ArrayList<String>();

			Map<String, Collection<Object>> docMap = doc.getFieldValuesMap();  // Note getFieldValueMap() returns only String

			List<String> orthologousDiseaseIdAssociations = new ArrayList<>();
			List<String> orthologousDiseaseTermAssociations = new ArrayList<>();
			List<String> phenotypicDiseaseIdAssociations = new ArrayList<>();
			List<String> phenotypicDiseaseTermAssociations = new ArrayList<>();

			if ( docMap.get("mgi_accession_id") != null && !( oriDataTypeName.equals("anatomy") || oriDataTypeName.equals("disease") ) ) {
				Collection<Object> mgiGeneAccs = docMap.get("mgi_accession_id");

				for( Object acc : mgiGeneAccs ){
					String mgi_gene_id = (String) acc;
					//System.out.println("mgi_gene_id: "+ mgi_gene_id);
					GeneIdentifier geneIdentifier = new GeneIdentifier(mgi_gene_id, mgi_gene_id);
					List<DiseaseAssociationSummary> diseaseAssociationSummarys = new ArrayList<>();
					try {
						//log.info("{} - getting disease-gene associations using cutoff {}", geneIdentifier, rawScoreCutoff);
						diseaseAssociationSummarys = phenoDigmDao.getGeneToDiseaseAssociationSummaries(geneIdentifier, rawScoreCutoff);
						//log.info("{} - received {} disease-gene associations", geneIdentifier, diseaseAssociationSummarys.size());
					} catch (RuntimeException e) {
						log.error(ExceptionUtils.getFullStackTrace(e));
						//log.error("Error retrieving disease data for {}", geneIdentifier);
					}

					// add the known association summaries to a dedicated list for the top
					// panel
					for (DiseaseAssociationSummary diseaseAssociationSummary : diseaseAssociationSummarys) {
						AssociationSummary associationSummary = diseaseAssociationSummary.getAssociationSummary();
						if (associationSummary.isAssociatedInHuman()) {
							//System.out.println("DISEASE ID: " + diseaseAssociationSummary.getDiseaseIdentifier().toString());
							//System.out.println("DISEASE ID: " + diseaseAssociationSummary.getDiseaseIdentifier().getDatabaseAcc());
							//System.out.println("DISEASE TERM: " + diseaseAssociationSummary.getDiseaseTerm());
							orthologousDiseaseIdAssociations.add(diseaseAssociationSummary.getDiseaseIdentifier().toString());
							orthologousDiseaseTermAssociations.add(diseaseAssociationSummary.getDiseaseTerm());
						} else {
							phenotypicDiseaseIdAssociations.add(diseaseAssociationSummary.getDiseaseIdentifier().toString());
							phenotypicDiseaseTermAssociations.add(diseaseAssociationSummary.getDiseaseTerm());
						}
					}
				}
			}
			fieldCount = 0; // reset

			//for (String fieldName : doc.getFieldNames()) {
			//for ( int k=0; k<flList.length; k++ ){
			for (String fieldName : flList){
				//String fieldName = flList[k];
				//System.out.println("DataTableController: "+ fieldName + " - value: " + docMap.get(fieldName));

				if ( fieldName.equals("images_link") ){

					String impcImgBaseUrl = baseUrl + "/impcImages/images?";

					String qryField = null;
					String imgQryField = null;
					if ( oriDataTypeName.equals("gene") || oriDataTypeName.equals("ensembl") ){
						qryField = "mgi_accession_id";
						imgQryField = "gene_accession_id";
					}
					else if (oriDataTypeName.equals("anatomy") ){
						qryField = "anatomy_id";
						imgQryField = "ma_id";
					}

					Collection<Object> accs = docMap.get(qryField);
					String accStr = null;
					String imgLink = null;

					//System.out.println("qryfield: " + qryField);
					//System.out.println("imgQryField: " + imgQryField);
					if ( accs != null ){
						for( Object acc : accs ){
							accStr = imgQryField + ":\"" + (String) acc + "\"";
						}
						//imgLink = "<a target='_blank' href='" + hostName + impcImgBaseUrl + "q="  + accStr + " AND observation_type:image_record&fq=biological_sample_group:experimental" + "'>image url</a>";
						imgLink = "<a target='_blank' href='" + hostName + impcImgBaseUrl + "q="  + accStr + " AND observation_type:image_record" + "'>image url</a>";

					}
					else {
						imgLink = NA;
					}

					fieldCount++;
					rowData.add(imgLink);
				}
				else if ( docMap.get(fieldName) == null ){
					fieldCount++;

					String vals = NA;
					if ( fieldName.equals("disease_id_by_gene_orthology") ){
						vals = orthologousDiseaseIdAssociations.size() == 0 ? NA : StringUtils.join(orthologousDiseaseIdAssociations, ", ");
					}
					else if ( fieldName.equals("disease_term_by_gene_orthology") ){
						vals = orthologousDiseaseTermAssociations.size() == 0 ? NA : StringUtils.join(orthologousDiseaseTermAssociations, ", ");
					}
					else if ( fieldName.equals("disease_id_by_phenotypic_similarity") ){
						vals = phenotypicDiseaseIdAssociations.size() == 0 ? NA : StringUtils.join(phenotypicDiseaseIdAssociations, ", ");
					}
					else if ( fieldName.equals("disease_term_by_phenotypic_similarity") ){
						vals = phenotypicDiseaseTermAssociations.size() == 0 ? NA : StringUtils.join(phenotypicDiseaseTermAssociations, ", ");
					}

					rowData.add(vals);

				}
				else {
					try {
						String value = null;
						//System.out.println("TEST CLASS: "+ docMap.get(fieldName).getClass());
						//System.out.println("****dataTypeName: " + dataTypeName + " --- " + fieldName);
						try {
							Collection<Object> vals =  docMap.get(fieldName);
							Set<Object> valSet = new HashSet<>(vals);
							value = StringUtils.join(valSet, ", ");

							if ( !oriDataTypeName.equals("hp") && dataTypeId.get(dataTypeName).equals(fieldName) ){
								//String coreName = dataTypeName.equals("marker_symbol") || dataTypeName.equals("ensembl") ? "gene" : dataTypeName;
								String coreName = null;
								if ( oriDataTypeName.equals("mouse_marker_symbol") ){
									coreName = "gene";
									Collection<Object> mvals = docMap.get("marker_symbol");
									Set<Object> mvalSet = new HashSet<>(mvals);
									for (Object mval : mvalSet) {

										// so that we can compare
										String valstr = "\"" + mval.toString().toLowerCase() + "\"";

										for(String qid : queryIds){
											if ( qid.toLowerCase().equals(valstr)){
												foundIds.add(qid);
											}
										}
									}
								}
								else if (oriDataTypeName.equals("human_marker_symbol") ){
									coreName = "gene";
									Collection<Object> mvals = docMap.get("human_gene_symbol");
									Set<Object> mvalSet = new HashSet<>(mvals);
									for (Object mval : mvalSet) {
										//for (Object mval : mvals) {
										// so that we can compare
										String valstr = "\"" + mval.toString() + "\"";

										//foundIds.add("\"" + mval.toString().toUpperCase() + "\"");
										if ( queryIds.contains(valstr)) {
											foundIds.add(valstr);
										}
									}

								}
								else if (oriDataTypeName.equals("ensembl") ){
									coreName = "gene";
									Collection<Object> gvals = docMap.get("ensembl_gene_id");
									Set<Object> gvalSet = new HashSet<>(gvals);
									for (Object gval : gvalSet) {
										if (gval.toString().startsWith("ENSMUSG")) {
											foundIds.add("\"" + gval + "\"");
										}
									}
								}
								else {
									coreName = dataTypeName;
									foundIds.add("\"" + value + "\"");
								}

								value = "<a target='_blank' href='" + hostName + baseUrl + "/" + dataTypePath.get(coreName) + "/" + value + "'>" + value + "</a>";
							}
							else if ( oriDataTypeName.equals("hp") && dataTypeId.get(dataTypeName).equals(fieldName) ){
								foundIds.add("\"" + value + "\"");
							}
						} catch ( ClassCastException c) {
							value = docMap.get(fieldName).toString();
						}

						//System.out.println("row " + i + ": field: " + k + " -- " + fieldName + " - " + value);
						fieldCount++;
						rowData.add(value);
					} catch(Exception e){
						//e.printStackTrace();
						if ( e.getMessage().equals("java.lang.Integer cannot be cast to java.lang.String") ){
							Collection<Object> vals = docMap.get(fieldName);
							if ( vals.size() > 0 ){
								Iterator it = vals.iterator();
								String value = (String) it.next();
								//String value = Integer.toString(val);
								fieldCount++;
								rowData.add(value);
							}
						}
					}
				}
			}
			// figure out the column users search againt
			//System.out.println(flList.get(sortCol.get(oriDataTypeName)));
			for (Object s : docMap.get(flList.get(sortCol.get(oriDataTypeName)))){
				String qStr = s.toString();
				//System.out.println("qStr: "+ qStr);
				qStr = oriDataTypeName.equals("mouse_marker_symbol") ? qStr.toLowerCase() : qStr;
				if ( !idRow.containsKey("\"" + qStr + "\"")) {
					idRow.put("\"" + qStr + "\"", rowData);
				}
				else {
					idRow.get("\"" + qStr + "\"").addAll(rowData);
				}
			}
		}

		// find the ids that are not found and displays them to users
		ArrayList nonFoundIds = (java.util.ArrayList) CollectionUtils.disjunction(queryIds, new ArrayList(foundIds));

		//System.out.println("non Found ids: "+ new ArrayList(nonFoundIds));
		//System.out.println("Found ids: "+ new ArrayList(foundIds));
		int fieldIndex = 0;
		if ( nonFoundIds.size() > 0){

			String fname = oriDataTypeName.equals("human_marker_symbol") ? "human_gene_symbol" : dataTypeName;
			int fc = 0;
			for(String fn : flList){
				if (fname.equals(fn)){
					fieldIndex = fc;
					break;
				}
				fc++;
			}
		}

		int resultsCount = 0;
		for ( int i=0; i<nonFoundIds.size(); i++ ){
			String thisVal = oriDataTypeName.equals("mouse_marker_symbol") ? nonFoundIds.get(i).toString().toLowerCase() : nonFoundIds.get(i).toString();
			String displayVal = nonFoundIds.get(i).toString().replaceAll("\"", "");
			List<String> rowData = new ArrayList<String>();
			for ( int l=0; l<fieldCount; l++ ){
				if (oriDataTypeName.equals("mouse_marker_symbol")){
					if ( StringUtils.containsIgnoreCase(StringUtils.join(queryIds,","), thisVal)) {
						rowData.add(l == fieldIndex ? displayVal : NA);
					}
				}
				else {
					if (queryIds.contains(thisVal)) {
						rowData.add(l == fieldIndex ? displayVal : NA);
					}
				}
			}
			if (rowData.size() != 0) {
				//j.getJSONArray("aaData").add(rowData);
				idRow.put(thisVal, rowData);
			}
			resultsCount += rowData.size();
		}


		if ( resultsCount == 0 && nonFoundIds.size() != 0 && foundIds.size() == 0){
			// cases where id is not found in our database
			return "";
		}

		// output result as the order of users query list
		for(String q : queryIds){
			if ( oriDataTypeName.equals("mouse_marker_symbol")){
				List<String> data = idRow.get(q.toLowerCase());
				for ( int i=0; i<data.size()/flList.size(); i++ ) {
					j.getJSONArray("aaData").add(data.subList(i * flList.size(), i * flList.size() + flList.size()));
				}
			}
			else {
				List<String> data = idRow.get(q);
				for ( int i=0; i<data.size()/flList.size(); i++ ) {
					j.getJSONArray("aaData").add(data.subList(i*flList.size(), i*flList.size() + flList.size()));
				}
			}
		}


		return j.toString();
	}
	public String fetchBatchQueryDataTableJson2(HttpServletRequest request, List<QueryResponse> solrResponses, String fllist, String dataTypeName, List<String> queryIds ) {

		String oriDataTypeName = dataTypeName;
		if ( dataTypeName.contains("marker_symbol")){
			dataTypeName = "marker_symbol";
		}

		String hostName = request.getAttribute("mappedHostname").toString().replace("https:", "http:");
		String baseUrl = request.getAttribute("baseUrl").toString();

		String NA = "Info not available";

		List<String> flList = Arrays.asList(StringUtils.split(fllist, ","));
		System.out.println("flist: " + flList);
		Set<String> foundIds = new HashSet<>();
		//List<String> foundIds = new ArrayList<>();

		//System.out.println("responses: " + solrResponses.size());

		SolrDocumentList results = new SolrDocumentList();

		for ( QueryResponse solrResponse : solrResponses ){
			results.addAll(solrResponse.getResults());
		}
		int totalDocs = results.size();

		Map<String, String> dataTypeId = new HashMap<>();
		dataTypeId.put("gene", GeneDTO.MGI_ACCESSION_ID);
		dataTypeId.put("marker_symbol", GeneDTO.MGI_ACCESSION_ID);
		dataTypeId.put("ensembl", GeneDTO.MGI_ACCESSION_ID);

		dataTypeId.put("mp", MpDTO.MP_ID);
		dataTypeId.put("anatomy", "anatomy_id");
		dataTypeId.put("hp", "hp_id");
		dataTypeId.put("disease", "disease_id");

		Map<String, String> dataTypePath = new HashMap<>();
		dataTypePath.put("gene", "genes");
		dataTypePath.put("marker_symbol", "genes");
		dataTypePath.put("mp", "phenotypes");
		dataTypePath.put("anatomy", "anatomy");
		dataTypePath.put("disease", "disease");

		// for sorting by user query list
		Map<String, Integer> sortCol = new HashMap<>();
		sortCol.put("gene", 0);
		sortCol.put("mouse_marker_symbol", 1);
		sortCol.put("human_marker_symbol", 2);
		sortCol.put("ensembl", 1);
		sortCol.put("hp", 0);
		sortCol.put("mp", 0);
		sortCol.put("anatomy", 0);
		sortCol.put("disease", 0);

		Map<String, String> searchCol = new HashMap<>();
		searchCol.put("gene", GeneDTO.MGI_ACCESSION_ID);
		searchCol.put("mouse_marker_symbol", GeneDTO.MARKER_SYMBOL);
		searchCol.put("human_marker_symbol", GeneDTO.HUMAN_GENE_SYMBOL);
		searchCol.put("ensemble", GeneDTO.ENSEMBL_GENE_ID);
		searchCol.put("mpId", MpDTO.MP_ID);
		searchCol.put("mpTerm", MpDTO.MP_ID);
		searchCol.put("anatomy", AnatomyDTO.ANATOMY_ID);
		//searchCol.put("")

		Map<String, List<String>> idRow = new HashMap<>();

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);

		int fieldCount = 0;

//		System.out.println("totaldocs:" + totalDocs);
		for (int i = 0; i < results.size(); ++i) {
			SolrDocument doc = results.get(i);

			List<String> rowData = new ArrayList<String>();

			Map<String, Collection<Object>> docMap = doc.getFieldValuesMap();  // Note getFieldValueMap() returns only String

			List<String> orthologousDiseaseIdAssociations = new ArrayList<>();
			List<String> orthologousDiseaseTermAssociations = new ArrayList<>();
			List<String> phenotypicDiseaseIdAssociations = new ArrayList<>();
			List<String> phenotypicDiseaseTermAssociations = new ArrayList<>();

			if ( docMap.get("mgi_accession_id") != null && !( oriDataTypeName.equals("anatomy") || oriDataTypeName.equals("disease") ) ) {
				Collection<Object> mgiGeneAccs = docMap.get("mgi_accession_id");

				for( Object acc : mgiGeneAccs ){
					String mgi_gene_id = (String) acc;
					//System.out.println("mgi_gene_id: "+ mgi_gene_id);
					GeneIdentifier geneIdentifier = new GeneIdentifier(mgi_gene_id, mgi_gene_id);
					List<DiseaseAssociationSummary> diseaseAssociationSummarys = new ArrayList<>();
					try {
						//log.info("{} - getting disease-gene associations using cutoff {}", geneIdentifier, rawScoreCutoff);
						diseaseAssociationSummarys = phenoDigmDao.getGeneToDiseaseAssociationSummaries(geneIdentifier, rawScoreCutoff);
						//log.info("{} - received {} disease-gene associations", geneIdentifier, diseaseAssociationSummarys.size());
					} catch (RuntimeException e) {
						log.error(ExceptionUtils.getFullStackTrace(e));
						//log.error("Error retrieving disease data for {}", geneIdentifier);
					}

					// add the known association summaries to a dedicated list for the top
					// panel
					for (DiseaseAssociationSummary diseaseAssociationSummary : diseaseAssociationSummarys) {
						AssociationSummary associationSummary = diseaseAssociationSummary.getAssociationSummary();
						if (associationSummary.isAssociatedInHuman()) {
							//System.out.println("DISEASE ID: " + diseaseAssociationSummary.getDiseaseIdentifier().toString());
							//System.out.println("DISEASE ID: " + diseaseAssociationSummary.getDiseaseIdentifier().getDatabaseAcc());
							//System.out.println("DISEASE TERM: " + diseaseAssociationSummary.getDiseaseTerm());
							orthologousDiseaseIdAssociations.add(diseaseAssociationSummary.getDiseaseIdentifier().toString());
							orthologousDiseaseTermAssociations.add(diseaseAssociationSummary.getDiseaseTerm());
						} else {
							phenotypicDiseaseIdAssociations.add(diseaseAssociationSummary.getDiseaseIdentifier().toString());
							phenotypicDiseaseTermAssociations.add(diseaseAssociationSummary.getDiseaseTerm());
						}
					}
				}
			}
			fieldCount = 0; // reset

			//for (String fieldName : doc.getFieldNames()) {
			//for ( int k=0; k<flList.length; k++ ){
			for (String fieldName : flList){
				//String fieldName = flList[k];
				//System.out.println("DataTableController: "+ fieldName + " - value: " + docMap.get(fieldName));

				if ( fieldName.equals("images_link") ){

					String impcImgBaseUrl = baseUrl + "/impcImages/images?";

					String qryField = null;
					String imgQryField = null;
					if ( oriDataTypeName.equals("gene") || oriDataTypeName.equals("ensembl") ){
						qryField = "mgi_accession_id";
						imgQryField = "gene_accession_id";
					}
					else if (oriDataTypeName.equals("anatomy") ){
						qryField = "anatomy_id";
						imgQryField = "ma_id";
					}

					Collection<Object> accs = docMap.get(qryField);
					String accStr = null;
					String imgLink = null;

					//System.out.println("qryfield: " + qryField);
					//System.out.println("imgQryField: " + imgQryField);
					if ( accs != null ){
						for( Object acc : accs ){
							accStr = imgQryField + ":\"" + (String) acc + "\"";
						}
						//imgLink = "<a target='_blank' href='" + hostName + impcImgBaseUrl + "q="  + accStr + " AND observation_type:image_record&fq=biological_sample_group:experimental" + "'>image url</a>";
						imgLink = "<a target='_blank' href='" + hostName + impcImgBaseUrl + "q="  + accStr + " AND observation_type:image_record" + "'>image url</a>";

					}
					else {
						imgLink = NA;
					}

					fieldCount++;
					rowData.add(imgLink);
				}
				else if ( docMap.get(fieldName) == null ){
					fieldCount++;

					String vals = NA;
					if ( fieldName.equals("disease_id_by_gene_orthology") ){
						vals = orthologousDiseaseIdAssociations.size() == 0 ? NA : StringUtils.join(orthologousDiseaseIdAssociations, ", ");
					}
					else if ( fieldName.equals("disease_term_by_gene_orthology") ){
						vals = orthologousDiseaseTermAssociations.size() == 0 ? NA : StringUtils.join(orthologousDiseaseTermAssociations, ", ");
					}
					else if ( fieldName.equals("disease_id_by_phenotypic_similarity") ){
						vals = phenotypicDiseaseIdAssociations.size() == 0 ? NA : StringUtils.join(phenotypicDiseaseIdAssociations, ", ");
					}
					else if ( fieldName.equals("disease_term_by_phenotypic_similarity") ){
						vals = phenotypicDiseaseTermAssociations.size() == 0 ? NA : StringUtils.join(phenotypicDiseaseTermAssociations, ", ");
					}

					rowData.add(vals);

				}
				else {
					try {
						String value = null;
						//System.out.println("TEST CLASS: "+ docMap.get(fieldName).getClass());
						//System.out.println("****dataTypeName: " + dataTypeName + " --- " + fieldName);
						try {
							Collection<Object> vals =  docMap.get(fieldName);
							Set<Object> valSet = new HashSet<>(vals);

							value = StringUtils.join(valSet, ", ");

							if ( !oriDataTypeName.equals("hp") && dataTypeId.get(dataTypeName).equals(fieldName) ){
								//String coreName = dataTypeName.equals("marker_symbol") || dataTypeName.equals("ensembl") ? "gene" : dataTypeName;
								String coreName = null;
								if ( oriDataTypeName.equals("mouse_marker_symbol") ){
									coreName = "gene";
									Collection<Object> mvals = docMap.get("marker_symbol");
									Set<Object> mvalSet = new HashSet<>(mvals);
									for (Object mval : mvalSet) {

										// so that we can compare
										String valstr = "\"" + mval.toString().toLowerCase() + "\"";

										for(String qid : queryIds){
											if ( qid.toLowerCase().equals(valstr)){
												foundIds.add(qid);
											}
										}
									}
								}
								else if (oriDataTypeName.equals("human_marker_symbol") ){
									coreName = "gene";
									Collection<Object> mvals = docMap.get("human_gene_symbol");
									Set<Object> mvalSet = new HashSet<>(mvals);
									for (Object mval : mvalSet) {
										//for (Object mval : mvals) {
										// so that we can compare
										String valstr = "\"" + mval.toString() + "\"";

										//foundIds.add("\"" + mval.toString().toUpperCase() + "\"");
										if ( queryIds.contains(valstr)) {
											foundIds.add(valstr);
										}
									}

								}
								else if (oriDataTypeName.equals("ensembl") ){
									coreName = "gene";
									Collection<Object> gvals = docMap.get("ensembl_gene_id");
									Set<Object> gvalSet = new HashSet<>(gvals);
									for (Object gval : gvalSet) {
										if (gval.toString().startsWith("ENSMUSG")) {
											foundIds.add("\"" + gval + "\"");
										}
									}
								}
								else {
									coreName = dataTypeName;
									foundIds.add("\"" + value + "\"");
								}

								value = "<a target='_blank' href='" + hostName + baseUrl + "/" + dataTypePath.get(coreName) + "/" + value + "'>" + value + "</a>";
							}
							else if ( oriDataTypeName.equals("hp") && dataTypeId.get(dataTypeName).equals(fieldName) ){
								foundIds.add("\"" + value + "\"");
							}
						} catch ( ClassCastException c) {
							value = docMap.get(fieldName).toString();
						}

						//System.out.println("row " + i + ": field: " + k + " -- " + fieldName + " - " + value);
						fieldCount++;
						rowData.add(value);
					} catch(Exception e){
						//e.printStackTrace();
						if ( e.getMessage().equals("java.lang.Integer cannot be cast to java.lang.String") ){
							Collection<Object> vals = docMap.get(fieldName);
							if ( vals.size() > 0 ){
								Iterator it = vals.iterator();
								String value = (String) it.next();
								//String value = Integer.toString(val);
								fieldCount++;
								rowData.add(value);
							}
						}
					}
				}
			}
			// figure out the column users search againt
			System.out.println(flList.get(sortCol.get(oriDataTypeName)));
			for (Object s : docMap.get(flList.get(sortCol.get(oriDataTypeName)))){
				String qStr = s.toString();
				System.out.println("qStr: "+ qStr);
				qStr = oriDataTypeName.equals("mouse_marker_symbol") ? qStr.toLowerCase() : qStr;
				if ( !idRow.containsKey("\"" + qStr + "\"")) {
					idRow.put("\"" + qStr + "\"", rowData);
				}
				else {
					idRow.get("\"" + qStr + "\"").addAll(rowData);
				}
			}
		}

		// find the ids that are not found and displays them to users
		ArrayList nonFoundIds = (java.util.ArrayList) CollectionUtils.disjunction(queryIds, new ArrayList(foundIds));

		//System.out.println("non Found ids: "+ new ArrayList(nonFoundIds));
		//System.out.println("Found ids: "+ new ArrayList(foundIds));
		int fieldIndex = 0;
		if ( nonFoundIds.size() > 0){

			String fname = oriDataTypeName.equals("human_marker_symbol") ? "human_gene_symbol" : dataTypeName;
			int fc = 0;
			for(String fn : flList){
				if (fname.equals(fn)){
					fieldIndex = fc;
					break;
				}
				fc++;
			}
		}

		int resultsCount = 0;
		for ( int i=0; i<nonFoundIds.size(); i++ ){
			String thisVal = oriDataTypeName.equals("mouse_marker_symbol") ? nonFoundIds.get(i).toString().toLowerCase() : nonFoundIds.get(i).toString();
			String displayVal = nonFoundIds.get(i).toString().replaceAll("\"", "");
			List<String> rowData = new ArrayList<String>();
			for ( int l=0; l<fieldCount; l++ ){
				if (oriDataTypeName.equals("mouse_marker_symbol")){
					if ( StringUtils.containsIgnoreCase(StringUtils.join(queryIds,","), thisVal)) {
						rowData.add(l == fieldIndex ? displayVal : NA);
					}
				}
				else {
					if (queryIds.contains(thisVal)) {
						rowData.add(l == fieldIndex ? displayVal : NA);
					}
				}
			}
			if (rowData.size() != 0) {
				//j.getJSONArray("aaData").add(rowData);
				idRow.put(thisVal, rowData);
			}
			resultsCount += rowData.size();
		}


		if ( resultsCount == 0 && nonFoundIds.size() != 0 && foundIds.size() == 0){
			// cases where id is not found in our database
			return "";
		}

		// output result as the order of users query list
		for(String q : queryIds){
			if ( oriDataTypeName.equals("mouse_marker_symbol")){
				List<String> data = idRow.get(q.toLowerCase());
				for ( int i=0; i<data.size()/flList.size(); i++ ) {
					j.getJSONArray("aaData").add(data.subList(i * flList.size(), i * flList.size() + flList.size()));
				}
			}
			else {
				List<String> data = idRow.get(q);
				for ( int i=0; i<data.size()/flList.size(); i++ ) {
					j.getJSONArray("aaData").add(data.subList(i*flList.size(), i*flList.size() + flList.size()));
				}
			}
		}


		return j.toString();
	}

	/**
	 * <p>
	 * Return jQuery dataTable from server-side for lazy-loading.
	 * </p>
	 *
	 * @param bRegex =false bRegex_0=false bRegex_1=false bRegex_2=false
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
			Model model) throws IOException, URISyntaxException {

		JSONObject jParams = (JSONObject) JSONSerializer.toJSON(solrParams);

		String solrCoreName = jParams.containsKey("solrCoreName") ? jParams.getString("solrCoreName") : jParams.getString("facetName");
		// use this for pattern matching later, instead of the modified complexphrase q string
		String queryOri = jParams.getString("qOri");

		String query = "";
		String fqOri = "";
		String mode = jParams.getString("mode");
		String solrParamStr = jParams.getString("params");

		boolean legacyOnly = jParams.getBoolean("legacyOnly");
		String evidRank = jParams.containsKey("evidRank") ? jParams.getString("evidRank") : "";

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
		if (jParams.containsKey("showImgView")) {
			showImgView = jParams.getBoolean("showImgView");
		}

		//System.out.println("DataTableController SOLRPARAMS: " + solrParamStr);
		JSONObject json = solrIndex.getQueryJson(query, solrCoreName, solrParamStr, mode, iDisplayStart, iDisplayLength, showImgView);

		String content = fetchDataTableJson(request, json, mode, queryOri, fqOri, iDisplayStart, iDisplayLength, solrParamStr, showImgView, solrCoreName, legacyOnly, evidRank);

		return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);
	}

	@ExceptionHandler(Exception.class)
	private ResponseEntity<String> getSolrErrorResponse(Exception e) {
		e.printStackTrace();
		String bootstrap = "<div class=\"alert\"><strong>Warning!</strong>  Error: Search functionality is currently unavailable</div>";
		String errorJSON = "{'aaData':[[' " + bootstrap + "','  ', ' ']], 'iTotalRecords':1,'iTotalDisplayRecords':1}";
		JSONObject errorJson = (JSONObject) JSONSerializer.toJSON(errorJSON);
		return new ResponseEntity<String>(errorJson.toString(), createResponseHeaders(), HttpStatus.CREATED);
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
									 boolean legacyOnly, String evidRank) throws IOException, URISyntaxException {

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
			jsonStr = parseJsonforDiseaseDataTable(json, request, solrCoreName);
		} else if (mode.equals("gene2go")) {
			jsonStr = parseJsonforGoDataTable(json, request, solrCoreName, evidRank);
		} else if (mode.equals("allele2Grid")) {
			jsonStr = parseJsonforProductDataTable(json, request, solrCoreName);
		}

		return jsonStr;
	}

	public String parseJsonforProductDataTable(JSONObject json, HttpServletRequest request, String solrCoreName) throws UnsupportedEncodingException {

		String baseUrl = request.getAttribute("baseUrl").toString();

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		j.put("iDisplayStart", request.getAttribute("displayStart"));
		j.put("iDisplayLength", request.getAttribute("displayLength"));

		for (int i = 0; i < docs.size(); i ++) {
			List<String> rowData = new ArrayList<String>();

			// array element is an alternate of facetField and facetCount
			JSONObject doc = docs.getJSONObject(i);

			//String alleleName = "<span class='allelename'>"+ URLEncoder.encode(doc.getString("allele_name"), "UTF-8")+"</span>";

			if ( !doc.containsKey(Allele2DTO.ALLELE_NAME)){
				// no point to show an allele that has no name
				continue;
			}

			String markerAcc = doc.getString(Allele2DTO.MGI_ACCESSION_ID);
			String alleleName = doc.getString(Allele2DTO.ALLELE_NAME);
			String alleleUrl = request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString() + "/alleles/" + markerAcc + "/" + alleleName;
			String markerSymbol = doc.getString(Allele2DTO.MARKER_SYMBOL);
			String alleleLink = "<a href='" + alleleUrl + "'>" + markerSymbol + "<sup>" + alleleName + "</sup></a>";
			String mutationType = "";
			String mt = doc.containsKey(Allele2DTO.MUTATION_TYPE) ? doc.getString(Allele2DTO.MUTATION_TYPE) : "";
			String desc = doc.containsKey(Allele2DTO.ALLELE_DESCRIPTION) ? doc.getString(Allele2DTO.ALLELE_DESCRIPTION) : "";
			List<String> mtDesc = new ArrayList<>(); mtDesc.add(mt); mtDesc.add(desc);

			if (! mt.isEmpty() && ! desc.isEmpty()) {
				mutationType = StringUtils.join(mtDesc, "; ");
			}

			List<String> order = new ArrayList<>();
			String dataUrl = baseUrl + "/order?acc=" + markerAcc + "&allele=" + alleleName +"&bare=true";

			if ( doc.containsKey(Allele2DTO.TARGETING_VECTOR_AVAILABLE) && doc.getBoolean(Allele2DTO.TARGETING_VECTOR_AVAILABLE) ) {
				order.add("<tr>");
				order.add("<td><a class='iFrameFancy' data-url='" + dataUrl + "&type=targeting_vector'><i class='fa fa-shopping-cart'><span class='orderFont'> Targeting vector</span></i></a></td>");

				if (doc.containsKey(Allele2DTO.VECTOR_ALLELE_IMAGE)) {
					order.add("<td><a class='iFrameVector' data-url='" + doc.getString(Allele2DTO.VECTOR_ALLELE_IMAGE) + "' title='map for vector'><i class='fa fa-th-list'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}

				if (doc.containsKey(Allele2DTO.VECTOR_GENBANK_LINK)) {
					order.add("<td><a class='genbank' href='" + doc.getString(Allele2DTO.VECTOR_GENBANK_LINK) + "' title='genbank file for vector'><i class='fa fa-file-text'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}
				order.add("</tr>");
			}
			if ( doc.containsKey(Allele2DTO.ES_CELL_AVAILABLE) && doc.getBoolean(Allele2DTO.ES_CELL_AVAILABLE)){
				order.add("<tr>");
				order.add("<td><a class='iFrameFancy' data-url='" + dataUrl + "&type=es_cell'><i class='fa fa-shopping-cart'><span class='orderFont'> ES cell</span></i></a></td>");

				if (doc.containsKey(Allele2DTO.ALLELE_SIMPLE_IMAGE)) {
					order.add("<td><a class='iFrameVector' data-url='" + doc.getString(Allele2DTO.ALLELE_SIMPLE_IMAGE) + "' title='map for allele'><i class='fa fa-th-list'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}

				if (doc.containsKey(Allele2DTO.GENBANK_FILE)) {
					order.add("<td><a class='genbank' href='" + doc.getString(Allele2DTO.GENBANK_FILE) + "' title='genbank file for allele'><i class='fa fa-file-text'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}
				order.add("</tr>");
			}

			if ( doc.containsKey(Allele2DTO.MOUSE_AVAILABLE) && doc.getBoolean(Allele2DTO.MOUSE_AVAILABLE)){
				order.add("<tr>");
				order.add("<td><a class='iFrameFancy' data-url='" + dataUrl + "&type=mouse'><i class='fa fa-shopping-cart'><span class='orderFont'> Mouse</span></i></a></td>");

				if (doc.containsKey(Allele2DTO.ALLELE_SIMPLE_IMAGE)) {
					order.add("<td><a class='iFrameVector' data-url='" + doc.getString(Allele2DTO.ALLELE_SIMPLE_IMAGE) + "' title='map for allele'><i class='fa fa-th-list'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}

				if (doc.containsKey(Allele2DTO.GENBANK_FILE)) {
					order.add("<td><a class='genbank' href='" + doc.getString(Allele2DTO.GENBANK_FILE) + "' title='genbank file for allele'><i class='fa fa-file-text'></i></a></td>");
				}
				else {
					order.add("<td></td>");
				}
				order.add("</tr>");
			}
			rowData.add(alleleLink);
			rowData.add(mutationType);
			rowData.add("<table><tbody>" + StringUtils.join(order, "") + "</tbody></table>");

			j.getJSONArray("aaData").add(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		j.put("facet_fields", facetFields);

		return j.toString();
	}

	public String parseJsonforGoDataTable(JSONObject json, HttpServletRequest request, String solrCoreName, String evidRank) {

		String hostName = request.getAttribute("mappedHostname").toString();
		String baseUrl = request.getAttribute("baseUrl").toString();

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		log.debug("TOTAL GENE2GO: " + totalDocs);

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		j.put("iDisplayStart", request.getAttribute("displayStart"));
		j.put("iDisplayLength", request.getAttribute("displayLength"));

		//GO evidence code ranking mapping

		for (int i = 0; i < docs.size(); i ++) {

			JSONObject doc = docs.getJSONObject(i);
			//System.out.println("DOC: "+ doc.toString());
			String marker_symbol = doc.getString("marker_symbol");
			String gId = doc.getString("mgi_accession_id");
			String glink = "<a href='" + hostName + baseUrl + "/genes/" + gId + "'>" + marker_symbol + "</a>";

			String phenoStatus = doc.getString("latest_phenotype_status");

			String NOINFO = "no info available";

			// has GO
			if (doc.containsKey("go_count")) {
				// System.out.println("GO COUNT: "+ doc.getInt("go_count"));
				List<String> rowData = new ArrayList<String>();
				rowData.add(glink);
				rowData.add(phenoStatus);
				rowData.add(Integer.toString(doc.getInt("go_count")));
				rowData.add("<i class='fa fa-plus-square'></i>");
				j.getJSONArray("aaData").add(rowData);
			} else {
				// No GO
				List<String> rowData = new ArrayList<String>();

				rowData.add(glink);
				rowData.add(phenoStatus);
				rowData.add(NOINFO);
				rowData.add("");

				j.getJSONArray("aaData").add(rowData);
			}
		}
		return j.toString();
	}

	public String parseJsonforGeneDataTable(JSONObject json, HttpServletRequest request, String qryStr, String fqOri, String solrCoreName, boolean legacyOnly) {

		RegisterInterestDrupalSolr registerInterest = new RegisterInterestDrupalSolr(config.get("drupalBaseUrl"), request);

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

		for (int i = 0; i < docs.size(); i ++) {

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


			String statusField = (doc.containsKey(GeneDTO.LATEST_PHENOTYPE_STATUS)) ? doc.getString(GeneDTO.LATEST_PHENOTYPE_STATUS) : null;

			// made this as null by default: don't want to show this for now
			//Integer legacyPhenotypeStatus = null;
			Integer legacyPhenotypeStatus = (doc.containsKey(GeneDTO.LEGACY_PHENOTYPE_STATUS)) ? doc.getInt(GeneDTO.LEGACY_PHENOTYPE_STATUS) : null;

			Integer hasQc = (doc.containsKey(GeneDTO.HAS_QC)) ? doc.getInt(GeneDTO.HAS_QC) : null;
			String phenotypeStatusHTMLRepresentation = geneService.getPhenotypingStatus(statusField, hasQc, legacyPhenotypeStatus, geneLink, toExport, legacyOnly);
			rowData.add(phenotypeStatusHTMLRepresentation);

			// register of interest
			if (registerInterest.loggedIn()) {
				if (registerInterest.alreadyInterested(mgiId)) {
					String uinterest = "<div class='registerforinterest' oldtitle='Unregister interest' title=''>"
							+ "<i class='fa fa-sign-out'></i>"
							+ "<a id='" + doc.getString("mgi_accession_id") + "' class='regInterest primary interest' href=''>&nbsp;Unregister interest</a>"
							+ "</div>";

					rowData.add(uinterest);
				} else {
					String rinterest = "<div class='registerforinterest' oldtitle='Register interest' title=''>"
							+ "<i class='fa fa-sign-in'></i>"
							+ "<a id='" + doc.getString("mgi_accession_id") + "' class='regInterest primary interest' href=''>&nbsp;Register interest</a>"
							+ "</div>";

					rowData.add(rinterest);
				}
			} else {
				// use the login link instead of register link to avoid user clicking on tab which
				// will strip out destination link that we don't want to see happened
				String interest = "<div class='registerforinterest' oldtitle='Login to register interest' title=''>"
						+ "<i class='fa fa-sign-in'></i>"
						// + "<a class='regInterest' href='/user/login?destination=data/search#fq=*:*&facet=gene'>&nbsp;Interest</a>"
						+ "<a class='regInterest' href='/user/login?destination=data/search/gene?kw=*&fq=*:*'>&nbsp;Interest</a>"
						+ "</div>";

				rowData.add(interest);
			}

			j.getJSONArray("aaData").add(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");

		//facetFields.
		j.put("facet_fields", facetFields);

		return j.toString();
	}

	public String parseJsonforProtocolDataTable(JSONObject json, HttpServletRequest request, String solrCoreName) {

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		j.put("iDisplayStart", request.getAttribute("displayStart"));
		j.put("iDisplayLength", request.getAttribute("displayLength"));

		String impressBaseUrl = request.getAttribute("drupalBaseUrl") + "/impress/impress/displaySOP/";

		for (int i = 0; i < docs.size(); i ++) {
			List<String> rowData = new ArrayList<String>();

			JSONObject doc = docs.getJSONObject(i);

			String parameter = doc.getString("parameter_name");
			rowData.add(parameter);

			// a parameter can belong to multiple procedures
			JSONArray procedures = doc.getJSONArray("procedure_name");
			JSONArray procedure_stable_keys = doc.getJSONArray("procedure_stable_key");

			List<String> procedureLinks = new ArrayList<String>();
			for (int p = 0; p < procedures.size(); p ++) {
				String procedure = procedures.get(p).toString();
				String procedure_stable_key = procedure_stable_keys.get(p).toString();
				procedureLinks.add("<a href='" + impressBaseUrl + procedure_stable_key + "'>" + procedure + "</a>");
			}

			rowData.add(StringUtils.join(procedureLinks, "<br>"));

			String pipeline = doc.getString("pipeline_name");
			rowData.add(pipeline);

			j.getJSONArray("aaData").add(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		j.put("facet_fields", facetFields);

		return j.toString();
	}

	public String parseJsonforMpDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreNamet) {

		RegisterInterestDrupalSolr registerInterest = new RegisterInterestDrupalSolr(config.get("drupalBaseUrl"), request);
		String baseUrl = request.getAttribute("baseUrl").toString();

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		j.put("iDisplayStart", request.getAttribute("displayStart"));
		j.put("iDisplayLength", request.getAttribute("displayLength"));

		for (int i = 0; i < docs.size(); i ++) {
			List<String> rowData = new ArrayList<String>();

			// array element is an alternate of facetField and facetCount
			JSONObject doc = docs.getJSONObject(i);

			String mpId = doc.getString("mp_id");
			String mpTerm = doc.getString("mp_term");
			String mpLink = "<a href='" + baseUrl + "/phenotypes/" + mpId + "'>" + mpTerm + "</a>";
			String mpCol = null;

			if (doc.containsKey("mixSynQf")) {

				mpCol = "<div class='title'>" + mpLink + "</div>";

				JSONArray data = doc.getJSONArray("mixSynQf");
				int counter = 0;
				String synMatch = null;
				String syn = null;

				for (Object d : data) {

					if ( d.toString().startsWith("MP:") ){
						continue;
					}
					String targetStr = qryStr.toLowerCase().replaceAll("\"", "");
					if (d.toString().toLowerCase().contains(targetStr)) {
						if (synMatch == null) {
							synMatch = Tools.highlightMatchedStrIfFound(targetStr, d.toString(), "span", "subMatch");
						}
					}
					else {
						counter++;
						if ( counter == 1 ) {
							syn = d.toString();
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
			String mpDef = doc.containsKey(MpDTO.MP_DEFINITION) ? doc.getString(MpDTO.MP_DEFINITION): "No definition data available";

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



			// number of postqc phenotyping calls of this MP
//            int numCalls = doc.containsKey("pheno_calls") ? doc.getInt("pheno_calls") : 0;
//
//			if (numCalls > 0){
//				rowData.add("<a href='" + baseUrl + "/phenotypes/" + mpId + "#hasGeneVariants'>" + numCalls + "</a>");
//			}
//			else {
//				rowData.add(Integer.toString(numCalls));
//			}

			// link out to ontology browser page
			rowData.add("<a href='" + baseUrl + "/ontologyBrowser?" + "termId=" + mpId + "'><i class=\"fa fa-share-alt-square\"></i> Browse</a>");

			// register of interest
			if (registerInterest.loggedIn()) {
				if (registerInterest.alreadyInterested(mpId)) {
					String uinterest = "<div class='registerforinterest' oldtitle='Unregister interest' title=''>"
							+ "<i class='fa fa-sign-out'></i>"
							+ "<a id='" + mpId + "' class='regInterest primary interest' href=''>&nbsp;Unregister interest</a>"
							+ "</div>";

					rowData.add(uinterest);
				} else {
					String rinterest = "<div class='registerforinterest' oldtitle='Register interest' title=''>"
							+ "<i class='fa fa-sign-in'></i>"
							+ "<a id='" + mpId + "' class='regInterest primary interest' href=''>&nbsp;Register interest</a>"
							+ "</div>";

					rowData.add(rinterest);
				}
			} else {
				// use the login link instead of register link to avoid user clicking on tab which
				// will strip out destination link that we don't want to see happened
				String interest = "<div class='registerforinterest' oldtitle='Login to register interest' title=''>"
						+ "<i class='fa fa-sign-in'></i>"
						// + "<a class='regInterest' href='/user/login?destination=data/search#fq=*:*&facet=mp'>&nbsp;Interest</a>"
						+ "<a class='regInterest' href='/user/login?destination=data/search/mp?kw=*&fq=top_level_mp_term:*'>&nbsp;Interest</a>"
						+ "</div>";

				rowData.add(interest);
			}

			j.getJSONArray("aaData").add(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		j.put("facet_fields", facetFields);

		return j.toString();
	}

	public String parseJsonforAnatomyDataTable(JSONObject json, HttpServletRequest request, String qryStr, String solrCoreName) throws IOException, URISyntaxException {

		//String baseUrl = request.getAttribute("baseUrl") + "/anatomy/";
		String baseUrl = request.getAttribute("baseUrl").toString();

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		j.put("iDisplayStart", request.getAttribute("displayStart"));
		j.put("iDisplayLength", request.getAttribute("displayLength"));

		for (int i = 0; i < docs.size(); i ++) {
			List<String> rowData = new ArrayList<String>();

			// array element is an alternate of facetField and facetCount
			JSONObject doc = docs.getJSONObject(i);
			String anatomyId = doc.getString(AnatomyDTO.ANATOMY_ID);
			String anatomyTerm = doc.getString(AnatomyDTO.ANATOMY_TERM);
			String anatomylink = "<a href='" + baseUrl + "/anatomy/" + anatomyId + "'>" + anatomyTerm + "</a>";

			// check has expression data
			//Anatomy ma = JSONMAUtils.getMA(anatomyId, config);

			String anatomyCol = "<div class='title'>" + anatomylink + "</div>";

			if (doc.containsKey(AnatomyDTO.ANATOMY_TERM_SYNONYM)) {

				JSONArray data = doc.getJSONArray(AnatomyDTO.ANATOMY_TERM_SYNONYM);
				int counter = 0;
				String synMatch = null;
				String syn = null;

				for (Object d : data) {

					String targetStr = qryStr.toLowerCase().replaceAll("\"", "");
					if (d.toString().toLowerCase().contains(targetStr)) {
						if (synMatch == null) {
							synMatch = Tools.highlightMatchedStrIfFound(targetStr, d.toString(), "span", "subMatch");
						}
					}
					else {
						counter++;
						if ( counter == 1 ) {
							syn = d.toString();
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

//            if (doc.containsKey(AnatomyDTO.ANATOMY_TERM_SYNONYM)) {
//                List<String> anatomySynonyms = doc.getJSONArray(AnatomyDTO.ANATOMY_TERM_SYNONYM);
//                List<String> prefixSyns = new ArrayList();
//
//                for (String sn : anatomySynonyms) {
//                    prefixSyns.add(Tools.highlightMatchedStrIfFound(qryStr, sn, "span", "subMatch"));
//                }
//
//                String syns = null;
//                if (prefixSyns.size() > 1) {
//                    syns = "<ul class='synonym'><li>" + StringUtils.join(prefixSyns, "</li><li>") + "</li></ul>";
//                } else {
//                    syns = prefixSyns.get(0);
//                }
//
//                String anatomyCol = "<div class='anatomyCol'><div class='title'>"
//                        + anatomylink
//                        + "</div>"
//                        + "<div class='subinfo'>"
//                        + "<span class='label'>synonym: </span>" + syns
//                        + "</div>";
//                rowData.add(anatomyCol);
//            } else {
//                rowData.add(anatomylink);
//            }

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
			j.getJSONArray("aaData").add(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		j.put("facet_fields", facetFields);

		return j.toString();
	}

	private boolean hasExpressionData(String anatomyId) throws IOException, URISyntaxException {
		boolean expressionDataAvailable=false;
		//check legacy Sanger images for any images
		JSONObject maAssociatedExpressionImagesResponse = JSONImageUtils.getAnatomyAssociatedExpressionImages(anatomyId, config, 1);
		JSONArray expressionImageDocs = maAssociatedExpressionImagesResponse.getJSONObject("response").getJSONArray("docs");
		if(expressionImageDocs.size()>0){
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

	public String parseJsonforImpcImageDataTable(JSONObject json, String solrParams, boolean showImgView, HttpServletRequest request, String query, String fqOri, String solrCoreName) throws IOException, URISyntaxException {

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

			JSONObject j = new JSONObject();
			j.put("aaData", new Object[0]);
			j.put("imgHref", mediaBaseUrl + URLEncoder.encode(solrParams, "UTF-8"));
			j.put("imgCount", totalDocs);
			j.put("iTotalRecords", totalDocs);
			j.put("iTotalDisplayRecords", totalDocs);
			j.put("iDisplayStart", request.getAttribute("displayStart"));
			j.put("iDisplayLength", request.getAttribute("displayLength"));

			//String imgBaseUrl = mediaBaseUrl + "/";
			for (int i = 0; i < docs.size(); i ++) {

				List<String> rowData = new ArrayList<String>();
				JSONObject doc = docs.getJSONObject(i);
				String annots = "";

				String imgLink = null;

				if (doc.containsKey("jpeg_url")) {

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
					imgLink = IMG_NOT_FOUND;
				}

				try {
					//ArrayList<String> mp = new ArrayList<String>();
					ArrayList<String> ma = new ArrayList<String>();
					ArrayList<String> procedures = new ArrayList<String>();

					int counter = 0;

					if (doc.has("anatomy_id")) {
						JSONArray termIds   = doc.getJSONArray("anatomy_id");
						JSONArray termNames = doc.getJSONArray("anatomy_term");
						for( Object s : termIds ){
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

//					if ( mp.size() == 1 ){
//						annots += "<span class='imgAnnots'><span class='annotType'>MP</span>: " + StringUtils.join(mp, ", ") + "</span>";
//					}
//					else if ( mp.size() > 1 ){
//						String list = "<ul class='imgMp'><li>" + StringUtils.join(mp, "</li><li>") + "</li></ul>";
//						annots += "<span class='imgAnnots'><span class='annotType'>MP</span>: " + list + "</span>";
//					}
//
//
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

					j.getJSONArray("aaData").add(rowData);
				} catch (Exception e) {
					// some images have no annotations
					rowData.add("No information available");
					rowData.add(imgLink);
					j.getJSONArray("aaData").add(rowData);
				}
			}

			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
			j.put("facet_fields", facetFields);

			return j.toString();
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

				AnnotNameValCount annot = annots.get(i);
				String displayAnnotName = annot.getName();
				String annotVal = annot.getVal();

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

				List pathAndImgCount = solrIndex.fetchImpcImagePathByAnnotName(query, thisFqStr);

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
					j.getJSONArray("aaData").add(rowData);
				}

			}

			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
			j.put("facet_fields", facetFields);
			//System.out.println("JSON STR: " + j.toString());
			return j.toString();
		}
	}

	public String parseJsonforImageDataTable(JSONObject json, String solrParams, boolean showImgView, HttpServletRequest request, String query, String fqOri, String solrCoreName) throws IOException, URISyntaxException {

		String mediaBaseUrl = config.get("mediaBaseUrl");

		int start = (int) request.getAttribute("displayStart");
		int length = (int) request.getAttribute("displayLength");

		String imgUrl = request.getAttribute("baseUrl") + "/imagesb?" + solrParams;

		if (showImgView) {
			// image view: one image per row
			JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
			int totalDocs = json.getJSONObject("response").getInt("numFound");

			JSONObject j = new JSONObject();
			j.put("aaData", new Object[0]);
			j.put("imgHref", imgUrl);
			j.put("imgCount", totalDocs);
			j.put("iTotalRecords", totalDocs);
			j.put("iTotalDisplayRecords", totalDocs);
			j.put("iDisplayStart", request.getAttribute("displayStart"));
			j.put("iDisplayLength", request.getAttribute("displayLength"));

			String imgBaseUrl = mediaBaseUrl + "/";

			for (int i = 0; i < docs.size(); i ++) {

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
						for (Object s : termIds) {
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
						for (Object s : expNames) {
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
					j.getJSONArray("aaData").add(rowData);
				} catch (Exception e) {
					// some images have no annotations
					rowData.add("No information available");
					rowData.add(imgLink);
					j.getJSONArray("aaData").add(rowData);
				}
			}

			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
			j.put("facet_fields", facetFields);

			return j.toString();
		} else {
			// annotation view: images group by annotationTerm per row

			String fqStr = fqOri;
			if ( fqStr == null ){
				solrParams += "&fq=*:*";
			}


			JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");

			JSONArray facets = solrIndex.mergeFacets(facetFields);

			int numFacets = facets.size();

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

				if (facets.size() <= i) {
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

					j.getJSONArray("aaData").add(rowData);
				}

			}

			j.put("facet_fields", facetFields);

			return j.toString();
		}
	}

	public String parseJsonforDiseaseDataTable(JSONObject json, HttpServletRequest request, String solrCoreName) {

		String baseUrl = request.getAttribute("baseUrl") + "/disease/";

		JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
		int totalDocs = json.getJSONObject("response").getInt("numFound");

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);

		j.put("iTotalRecords", totalDocs);
		j.put("iTotalDisplayRecords", totalDocs);
		j.put("iDisplayStart", request.getAttribute("displayStart"));
		j.put("iDisplayLength", request.getAttribute("displayLength"));

		Map<String, String> srcBaseUrlMap = new HashMap<>();
		srcBaseUrlMap.put("OMIM", "http://omim.org/entry/");
		srcBaseUrlMap.put("ORPHANET", "http://www.orpha.net/consor/cgi-bin/OC_Exp.php?Lng=GB&Expert=");
		srcBaseUrlMap.put("DECIPHER", "http://decipher.sanger.ac.uk/syndrome/");

		for (int i = 0; i < docs.size(); i ++) {
			List<String> rowData = new ArrayList<String>();

			// disease link
			JSONObject doc = docs.getJSONObject(i);
			//System.out.println(" === JSON DOC IN DISEASE === : " + doc.toString());
			String diseaseId = doc.getString("disease_id");
			String diseaseTerm = doc.getString("disease_term");
			String diseaseLink = "<a href='" + baseUrl + diseaseId + "'>" + diseaseTerm + "</a>";
			rowData.add(diseaseLink);

			// disease source
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


			// Curated genes, candidate genes by phenotype
			// commented out for now
           /* try {
                //String isHumanCurated = doc.getString("human_curated").equals("true") ? human : "";
                String isHumanCurated = doc.getString("human_curated").equals("true")
                        || doc.getString("impc_predicted_known_gene").equals("true")
                        || doc.getString("mgi_predicted_known_gene").equals("true") ? human : "";

                String isMouseCurated = doc.getString("mouse_curated").equals("true") ? mice : "";
                rowData.add(isHumanCurated + isMouseCurated);

				//rowData.add("test1" + "test2");
                //String isImpcPredicted = (doc.getString("impc_predicted").equals("true") || doc.getString("impc_predicted_in_locus").equals("true")) ? impc : "";
                //String isMgiPredicted = (doc.getString("mgi_predicted").equals("true") || doc.getString("mgi_predicted_in_locus").equals("true")) ? mgi : "";
                String isImpcPredicted = (doc.getString("impc_predicted").equals("true") || doc.getString("impc_novel_predicted_in_locus").equals("true")) ? impc : "";
                String isMgiPredicted = (doc.getString("mgi_predicted").equals("true") || doc.getString("mgi_novel_predicted_in_locus").equals("true")) ? mgi : "";

                rowData.add(isImpcPredicted + isMgiPredicted);
				//rowData.add("test3" + "test4");
                //System.out.println("DOCS: " + rowData.toString());
               // j.getJSONArray("aaData").add(rowData);
            } catch (Exception e) {
                log.error("Error getting disease curation values");
                log.error(e.getLocalizedMessage());
            }*/

			j.getJSONArray("aaData").add(rowData);
		}

		JSONObject facetFields = json.getJSONObject("facet_counts").getJSONObject("facet_fields");
		j.put("facet_fields", facetFields);

		return j.toString();
	}

	private ArrayList<String> fetchImgGeneAnnotations(JSONObject doc, HttpServletRequest request) {

		ArrayList<String> gene = new ArrayList<String>();

		try {
			if (doc.has("symbol_gene")) {
				JSONArray geneSymbols = doc.getJSONArray("symbol_gene");
				for (Object s : geneSymbols) {
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

	public String fetchImagePathByAnnotName(String query, String fqStr) throws IOException, URISyntaxException {

		String mediaBaseUrl = config.get("mediaBaseUrl");

		final int maxNum = 4; // max num of images to display in grid column

		String queryUrl = config.get("internalSolrUrl")
				+ "/images/select?qf=auto_suggest&defType=edismax&wt=json&q=" + query
				+ "&" + fqStr
				+ "&rows=" + maxNum;

		List<String> imgPath = new ArrayList<String>();

		JSONObject thumbnailJson = solrIndex.getResults(queryUrl);
		JSONArray docs = thumbnailJson.getJSONObject("response").getJSONArray("docs");

		int dataLen = docs.size() < 5 ? docs.size() : maxNum;

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

	private String concateGeneInfo(JSONObject doc, JSONObject json, String qryStr, HttpServletRequest request) {

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

				//System.out.println(qryStr);
				String field = fields[i];
				List<String> info = new ArrayList<String>();

				if (field.equals("marker_name")) {
					info.add(Tools.highlightMatchedStrIfFound(qryStr, doc.getString(field), "span", "subMatch"));
				} else if (field.equals("human_gene_symbol")) {
					JSONArray data = doc.getJSONArray(field);
					for (Object h : data) {
						info.add(Tools.highlightMatchedStrIfFound(qryStr, h.toString(), "span", "subMatch"));
					}
				} else if (field.equals("marker_synonym")) {
					JSONArray data = doc.getJSONArray(field);
					int counter = 0;
					String synMatch = null;
					String syn = null;

					for (Object d : data) {
						counter++;
						String targetStr = qryStr.toLowerCase().replaceAll("\"", "");
						if ( d.toString().toLowerCase().contains(targetStr) ) {
							if ( synMatch == null ) {
								synMatch = Tools.highlightMatchedStrIfFound(targetStr, d.toString(), "span", "subMatch");
							}
						}
						else {
							if  (counter == 1) {
								syn = d.toString();
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

	// allele reference stuff
	@RequestMapping(value = "/dataTableAlleleRefCount", method = RequestMethod.GET)
	public @ResponseBody
	int updateReviewed(
			@RequestParam(value = "filterStr", required = true) String sSearch,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException {

		return fetchAlleleRefCount(sSearch);
	}

	public int fetchAlleleRefCount(String sSearch) throws SQLException {

		Connection conn = admintoolsDataSource.getConnection();

		String like = "%" + sSearch + "%";
		String query = null;

		if (sSearch != "") {
			query = "select count(*) as count from allele_ref where "
					+ " reviewed='no' and"
					+ " acc like ?"
					+ " or symbol like ?"
					+ " or pmid like ?"
					+ " or date_of_publication like ?"
					+ " or grant_id like ?"
					+ " or agency like ?"
					+ " or acronym like ?";
		} else {
			query = "select count(*) as count from allele_ref where reviewed='no'";
		}
		//System.out.println("DataTableController: query: "+query);
		int rowCount = 0;
		try (PreparedStatement p1 = conn.prepareStatement(query)) {
			if (sSearch != "") {
				for (int i = 1; i < 8; i ++) {
					p1.setString(i, like);
				}
			}
			ResultSet resultSet = p1.executeQuery();

			while (resultSet.next()) {
				rowCount = Integer.parseInt(resultSet.getString("count"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rowCount;
	}

	@RequestMapping(value = "/dataTableAlleleRefPost", method = RequestMethod.POST)
	public @ResponseBody
	String updateReviewed(
			@RequestParam(value = "symbol", required = true) String alleleSymbol,
			@RequestParam(value = "id", required = true) String dbidStr,
			@RequestParam(value = "pmid", required = true) String pmidStr,
			@RequestParam(value = "falsepositive", required = true) String falsepositive,
			@RequestParam(value = "reviewed", required = true) String reviewed,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException {

		// store new value to database
		alleleSymbol = alleleSymbol.trim();
		Integer dbid = Integer.parseInt(dbidStr);
		Integer pmid = Integer.parseInt(pmidStr);

		return setAlleleSymbol(dbid, pmid, alleleSymbol, falsepositive, reviewed);
	}

	@RequestMapping(value = "/fetchAlleleRefPmidData", method = RequestMethod.POST)
	public @ResponseBody
	String fetchAlleleRefPmidData(
			@RequestParam(value = "pmid", required = true) String pmidStr,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException {

		Integer pmid = Integer.parseInt(pmidStr);
		return new ObjectMapper().writeValueAsString(fetchPmidData(pmid));
	}

	private String fetchMeshTermsByPmid(Integer pmid) {

		List<String> pmidQryStr = new ArrayList<>();
		pmidQryStr.add("ext_id:" + pmid);
		Map<Integer, PaperController.Pubmed> pd = paperController.fetchEuropePubmedData(pmidQryStr);
		PaperController.Pubmed pub = pd.get(pmid);

		// fetch mesh terms: heading pulus mesh heading+mesh qualifier
		List<String> mterms = new ArrayList<>();
		for ( int k=0; k<pub.meshTerms.size(); k++ ) {
			PaperController.MeshTerm mt = pub.meshTerms.get(k);
			mterms.add(mt.meshHeading);
			for (String mq : mt.meshQualifiers) {
				mterms.add(mt.meshHeading + " " + mq);
			}
		}
		return mterms.size() > 0 ? StringUtils.join(mterms, "|||") : "";

	}
	public String setAlleleSymbol(Integer dbid, Integer pmid, String alleleSymbol, String falsepositive, String reviewed) throws SQLException {

		String meshTerms = fetchMeshTermsByPmid(pmid);

		final String NA = "Not available";
		// convert "<sup>" to "<" and "</sup>" to ">"
		alleleSymbol = alleleSymbol.replaceAll("<sup>","<");
		alleleSymbol = alleleSymbol.replaceAll("</sup>",">");

		Connection connKomp2 = komp2DataSource.getConnection();
		Connection conn = admintoolsDataSource.getConnection();
		final String delimiter = "|||";

		List<String> alleleSymbols = new ArrayList<>();
		JSONObject j = new JSONObject();

		String sqla = "SELECT acc, gf_acc FROM allele WHERE symbol=?";
		//String updateSql = "UPDATE allele_ref SET acc=?, gacc=?, symbol=?, reviewed=?, timestamp=?, falsepositive=?, mesh=? WHERE dbid=?";
		String updateSql = "UPDATE allele_ref SET acc=?, gacc=?, symbol=?, reviewed=?, datasource=?, falsepositive=?, mesh=? WHERE dbid=?";

		// when symbol is set to be empty, reviewed should have been set to "yes" by curator
		if (reviewed.equals("yes") && alleleSymbol.isEmpty()) {
			alleleSymbol = NA;
			updatePaper(conn, updateSql, "", "", alleleSymbol, falsepositive, dbid, meshTerms);
			j.put("reviewed", "yes");
			j.put("falsepositive", falsepositive.equals("yes") ? "yes" : "no");
			j.put("symbol", NA);
		}

		else if (! alleleSymbol.contains(",")) { // single symbol

			// single allele symbols
			String alleleAcc = null;
			String geneAcc = null;

			// find matching allele symbol from komp2 database and use its allele acc to populate allele_ref table
			try (PreparedStatement p = connKomp2.prepareStatement(sqla)) {
				p.setString(1, alleleSymbol);
				ResultSet resultSet = p.executeQuery();

				while (resultSet.next()) {
					alleleAcc = resultSet.getString("acc");
					geneAcc = resultSet.getString("gf_acc");
					System.out.println(alleleSymbol + ": " + alleleAcc + " --- " + geneAcc);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (alleleAcc != null && geneAcc != null) {
					updatePaper(conn, updateSql, alleleAcc, geneAcc, alleleSymbol, falsepositive, dbid, meshTerms);

					j.put("reviewed", "yes");
					j.put("falsepositive", "no");
					j.put("symbol", alleleSymbol);
				}
				else {
					// revert info in the db
					Map<String, String> info = fetchPmidData(pmid);
					j.put("reviewed", info.get("reviewed"));
					j.put("falsepositive", info.get("falsepositive"));
					j.put("error", "ERROR: setting symbol failed: could not find matching accession id");
					j.put("allAllelesNotFound", true);
					j.put("symbol", info.get("symbol"));
				}

			} catch (SQLException se) {
				//Handle errors for JDBC
				se.printStackTrace();

				// revert info in the db
				Map<String, String> info = fetchPmidData(pmid);
				j.put("reviewed", info.get("reviewed"));
				j.put("falsepositive", info.get("falsepositive"));
				j.put("error", "ERROR: setting symbol failed");
				j.put("symbol", info.get("symbol"));
			}

		}
		else if (alleleSymbol.contains(",")) {
			// if there are multiple allele symbols, it should have been separated by comma
			alleleSymbols = Arrays.asList(alleleSymbol.split(","));

			List<String> nonMatchedAlleleSymbols = new ArrayList<>();
			List<String> matchedAlleleSymbols = new ArrayList<>();
			List<String> alleleAccs = new ArrayList<>();
			List<String> geneAccs = new ArrayList<>();

			for (String thisAlleleSymbol : alleleSymbols) {

				thisAlleleSymbol = thisAlleleSymbol.trim();

				// fetch allele id, gene id of this allele symbol
				// and update acc and gacc fields of allele_ref table
				//System.out.println("set allele: " + sqla);

				String alleleAcc = null;
				String geneAcc = null;

				// find matching allele symbol from komp2 database and use its allele acc to populate allele_ref table
				try (PreparedStatement p = connKomp2.prepareStatement(sqla)) {
					p.setString(1, thisAlleleSymbol);
					ResultSet resultSet = p.executeQuery();

					while (resultSet.next()) {
						alleleAcc = resultSet.getString("acc");
						geneAcc = resultSet.getString("gf_acc");
						//System.out.println(alleleSymbol + ": " + alleleAcc + " --- " + geneAcc);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				//System.out.println("setting acc and gacc -> " + alleleAcc + " --- " + geneAcc);

				if (alleleAcc != null && geneAcc != null) {
					alleleAccs.add(alleleAcc);
					geneAccs.add(geneAcc);
					matchedAlleleSymbols.add(thisAlleleSymbol);
					System.out.println("matched symbol: "+thisAlleleSymbol);
				}
				else if ( alleleAcc == null ){
					nonMatchedAlleleSymbols.add(thisAlleleSymbol);
					System.out.println("NON matched symbol: "+thisAlleleSymbol);
				}
			}

			String alleleAccsStr = StringUtils.join(alleleAccs, delimiter);
			String geneAccsStr = StringUtils.join(geneAccs, delimiter);

			try{
				updatePaper(conn, updateSql, alleleAccsStr, geneAccsStr, StringUtils.join(matchedAlleleSymbols, delimiter), falsepositive, dbid, meshTerms);
			}
			catch (SQLException se) {
				//Handle errors for JDBC
				se.printStackTrace();

				// revert info in the db
				Map<String, String> info = fetchPmidData(pmid);
				j.put("reviewed", info.get("reviewed"));
				j.put("falsepositive", info.get("falsepositive"));
				j.put("error", "ERROR: setting symbol failed");
				j.put("symbol", info.get("symbol"));
			}

			if ( nonMatchedAlleleSymbols.size() == alleleSymbols.size() ) {
				// all symbols not found in KOMP2
//				j.put("reviewed", "no");
//				j.put("symbol", alleleSymbol);
//				j.put("allAllelesNotFound", true);

				Map<String, String> info = fetchPmidData(pmid);
				j.put("reviewed", info.get("reviewed"));
				j.put("falsepositive", info.get("falsepositive"));
				j.put("error", "ERROR: setting symbol failed");
				j.put("allAllelesNotFound", true);
				j.put("symbol", info.get("symbol"));
			}
			else {
				if ( matchedAlleleSymbols.size() == alleleSymbols.size() ){
					// all matched
					j.put("reviewed", "yes");
					j.put("falsepositive", "no");
					j.put("symbol", alleleSymbol);
				}
				else {
					// displays only the matched ones
					j.put("reviewed", "yes");
					j.put("falsepositive", "no");
					j.put("symbol", StringUtils.join(matchedAlleleSymbols, ","));
					j.put("someAllelesNotFound", StringUtils.join(nonMatchedAlleleSymbols, ","));
				}
			}
		}

		conn.close();
		connKomp2.close();

		return j.toString();
	}

	public Map<String, String> fetchPmidData(Integer pmid) throws SQLException {
		Connection conn = admintoolsDataSource.getConnection();

		Map<String, String> info = new HashMap<>();

		String query = "SELECT * FROM allele_ref WHERE pmid=?";
		try (PreparedStatement p = conn.prepareStatement(query)) {
			p.setInt(1, pmid);
			ResultSet resultSet = p.executeQuery();
			while (resultSet.next()) {
				info.put("symbol", resultSet.getString("symbol"));
				info.put("reviewed", resultSet.getString("reviewed"));
				info.put("falsepositive", resultSet.getString("falsepositive"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return info;
	}

	private void updatePaper(
			Connection conn, String updateSql,
			String alleleAccsStr, String geneAccsStr,
			String alleleSymbol, String falsepositive, Integer dbid,
			String meshTerms) throws SQLException {
		//System.out.println(updateSql + " 1: " + alleleAccsStr + " 2: " + geneAccsStr + " 3: " + alleleSymbol + " 6: "  + falsepositive + " 7: " + meshTerms  + " 8: " +  dbid);
		//String updateSql = "UPDATE allele_ref SET 1. acc=?, 2. gacc=?, 3. symbol=?, 4. reviewed=?, 5. datasource=?, 6. falsepositive=?, 7. mesh=? WHERE 8. dbid=?";

		PreparedStatement stmt = conn.prepareStatement(updateSql);
		stmt.setString(1, alleleAccsStr);
		stmt.setString(2, geneAccsStr);
		stmt.setString(3, alleleSymbol);
		stmt.setString(4, "yes");
		//stmt.setString(5, String.valueOf(new Timestamp(System.currentTimeMillis()))); // leave time as it is for stats purpose
		stmt.setString(5, "manual");
		stmt.setString(6, falsepositive);
		stmt.setString(7, meshTerms);
		stmt.setInt(8, dbid);

		stmt.executeUpdate();
	}

	// allele reference stuff
	@RequestMapping(value = "/dataTableAlleleRefEdit", method = RequestMethod.GET)
	public ResponseEntity<String> dataTableAlleleRefEditJson(
			@RequestParam(value = "iDisplayStart", required = false) Integer iDisplayStart,
			@RequestParam(value = "iDisplayLength", required = false) Integer iDisplayLength,
			@RequestParam(value = "sSearch", required = false) String sSearch,
			@RequestParam(value = "doAlleleRefEdit", required = false) String editParams,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException {

		JSONObject jParams = (JSONObject) JSONSerializer.toJSON(editParams);
		Boolean editMode = jParams.getString("editMode").equals("true") ? true : false;

		String content = fetch_allele_ref_edit(iDisplayLength, iDisplayStart, sSearch, editMode);
		//System.out.println("ALLELE REF EDIT: " + content);
		return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);

	}

	@RequestMapping(value = "/dataTableAlleleRef", method = RequestMethod.GET)
	public ResponseEntity<String> dataTableAlleleRefJson(
			//@RequestParam(value = "iDisplayStart", required = false, defaultValue = "0") int iDisplayStart,
			//@RequestParam(value = "iDisplayLength", required = false, defaultValue = "-1") int iDisplayLength,
			//@RequestParam(value = "sSearch", required = false) String sSearch,
			@RequestParam(value = "doAlleleRef", required = false) String params,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException {

		JSONObject jParams = (JSONObject) JSONSerializer.toJSON(params);

		int iDisplayLength = jParams.getInt("iDisplayLength");
		int iDisplayStart = jParams.getInt("iDisplayStart");
		String searchKw = jParams.getString("kw");

		String content = fetch_allele_ref(iDisplayLength, iDisplayStart, searchKw);
		return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);

	}
	@RequestMapping(value = "/dataTableAlleleRef2", method = RequestMethod.GET)
	public ResponseEntity<String> dataTableAlleleRefJson2(
			//@RequestParam(value = "iDisplayStart", required = false, defaultValue = "0") int iDisplayStart,
			//@RequestParam(value = "iDisplayLength", required = false, defaultValue = "-1") int iDisplayLength,
			//@RequestParam(value = "sSearch", required = false) String sSearch,
			@RequestParam(value = "doAlleleRef", required = false) String params,
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, URISyntaxException, SQLException {
		//System.out.println("params: " + params);

		JSONObject jParams = (JSONObject) JSONSerializer.toJSON(params);

		int iDisplayLength = jParams.getInt("iDisplayLength");
		int iDisplayStart = jParams.getInt("iDisplayStart");
		String searchKw = jParams.getString("kw");
		Boolean rowFormat = jParams.getBoolean("rowFormat");
		String orderByStr = jParams.getString("orderBy");

		String content = fetch_allele_ref2(iDisplayLength, iDisplayStart, searchKw, rowFormat, orderByStr);
		//System.out.println("content: " + content);
		return new ResponseEntity<String>(content, createResponseHeaders(), HttpStatus.CREATED);

	}

	public String fetch_allele_ref_edit(int iDisplayLength, int iDisplayStart, String sSearch, Boolean editMode) throws SQLException {

		Connection conn = admintoolsDataSource.getConnection();

		//String likeClause = " like '%" + sSearch + "%'";
		String like = "%" + sSearch + "%";
		String query = null;

		if (sSearch != "") {
			query = "SELECT count(*) AS count FROM allele_ref WHERE "
					+ "falsepositive='no' "
					+ "AND (acc like ? "
					+ "OR symbol like ? "
					+ "OR pmid like ? "
					+ "OR date_of_publication like ? "
					+ "OR grant_id like ? "
					+ "OR agency like ?)";
			//  + " OR acronym like ?";
		} else {
			query = "SELECT count(*) AS count FROM allele_ref WHERE falsepositive='no'";
		}
		//System.out.println("count query: "+query);
		int rowCount = 0;
		try (PreparedStatement p1 = conn.prepareStatement(query)) {

			if (sSearch != "") {
				for (int i = 1; i < 7; i++) {
					p1.setString(i, like);
				}
			}
			ResultSet resultSet = p1.executeQuery();
			while (resultSet.next()) {
				rowCount = Integer.parseInt(resultSet.getString("count"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		//System.out.println("Got " + rowCount + " rows");
		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		j.put("iTotalRecords", rowCount);
		j.put("iTotalDisplayRecords", rowCount);

		String query2 = null;

		if (sSearch != "") {
			query2 = "SELECT dbid AS dbid,"
					+ "reviewed,"
					+ "gacc AS gacc,"
					+ "symbol AS symbol,"
					+ "pmid,"
					+ "date_of_publication,"
					+ "grant_id AS grant_id,"
					+ "agency AS agency,"
					+ "acronym AS acronym,"
					+ "paper_url "
					+ "FROM allele_ref "
					+ "WHERE falsepositive='no' "
					+ "AND (symbol LIKE ? "
					+ "OR pmid LIKE ? "
					+ "OR date_of_publication LIKE ? "
					+ "OR grant_id LIKE ? "
					+ "OR agency LIKE ? ) "
					+ "ORDER BY reviewed DESC "
					+ "LIMIT ?, ?";
		} else {
			query2 = "SELECT dbid AS dbid,"
					+ "reviewed,"
					+ "gacc AS gacc,"
					+ "symbol AS symbol,"
					+ "pmid,"
					+ "date_of_publication,"
					+ "grant_id AS grant_id,"
					+ "agency AS agency,"
					+ "paper_url "
					+ "FROM allele_ref "
					+ "WHERE falsepositive='no' "
					+ "ORDER BY reviewed DESC limit ?,?";
		}

//		System.out.println("query: "+ query);
//        System.out.println("query2: "+ query2);
//		System.out.println("start: " + iDisplayStart + " end: " + iDisplayLength);
		String impcGeneBaseUrl = "http://www.mousephenotype.org/data/genes/";

		try (PreparedStatement p2 = conn.prepareStatement(query2)) {
			if (sSearch != "") {
				for (int i = 1; i < 8; i ++) {
					p2.setString(i, like);
					if (i == 6) {
						p2.setInt(i, iDisplayStart);
					} else if (i == 7) {
						p2.setInt(i, iDisplayLength);
					}
				}
			} else {
				p2.setInt(1, iDisplayStart);
				p2.setInt(2, iDisplayLength);
			}

			ResultSet resultSet = p2.executeQuery();
			final String delimeter = "\\|\\|\\|";

			while (resultSet.next()) {

				List<String> rowData = new ArrayList<String>();

				// dbid has been concatanated so becomes a string
				String dbidStr = resultSet.getString("dbid");
				//int dbid = resultSet.getInt("dbid");
				///String gacc = resultSet.getString("gacc");
				String alleleSymbol = resultSet.getString("symbol").isEmpty() ? "Symbol needs hand curation" : Tools.superscriptify(resultSet.getString("symbol")).replaceAll(delimeter, ", ");

				if (editMode){
					String falsePositive = "<input type='checkbox' name='falsepositive'>False positive<br>";
					String reviewed = resultSet.getString("reviewed").equals("yes") ? "<input type='checkbox' checked name='reviewed'>Reviewed<br><br>" : "<input type='checkbox' name='reviewed'>Reviewed<br><br>";

					alleleSymbol = "<form class='alleleSub'>"
							+ falsePositive
							+ reviewed
							+ "<textarea name='asymbolForm'>" + alleleSymbol + "</textarea><br><br>"
							+ "<input class='update' type='button' value='Update'>"
							+ "<a><i class='fa fa-question-circle fa-1x howto'></i></a>"
							+ "</form>";
				}
				//String alLink = alleleSymbol.equals("") ? "" : "<a target='_blank' href='" + impcGeneBaseUrl + resultSet.getString("gacc") + "'>" + alleleSymbol + "</a>";
				rowData.add(alleleSymbol);

				rowData.add(resultSet.getString("date_of_publication"));
				String pmid = "<span class='pmid' id=" + dbidStr + ">" + resultSet.getInt("pmid") + "</span>";
				rowData.add(pmid);

				String[] grantIds = resultSet.getString("grant_id").split(delimeter);
				String[] grantAgencies = resultSet.getString("agency").split(delimeter);
				Set<String> gIdsAgencies = new HashSet<>();

				for( int i=0; i<grantIds.length; i++ ) {
					if (!grantIds[i].equals("")){
						gIdsAgencies.add(grantIds[i] + " (" + grantAgencies[i] + ")");
					}
				}
				rowData.add(gIdsAgencies.size()>0 ? StringUtils.join(gIdsAgencies, ", ") : "No information available");


				String[] urls = resultSet.getString("paper_url").split(delimeter);
				List<String> links = new ArrayList<>();

				// show multiple papers as the sources may not have the same format for the same paper
				int pCount = 0;
				for (int p=0; p<urls.length; p++) {
					if (pCount<3) {
						String linkName = urls.length == 1 ? "Link" : "Link"+ (p+1);
						if ( ! urls[p].isEmpty()) {
							links.add("<a target='_blank' href='" + urls[p] + "'>" + linkName + "</a>");
							pCount++;
						}
					}
				}
				rowData.add(StringUtils.join(links, "<br>"));

				j.getJSONArray("aaData").add(rowData);
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			conn.close();
		}
		return j.toString();
	}

	public String fetch_allele_ref2(int iDisplayLength, int iDisplayStart, String sSearch, Boolean rowFormat, String orderByStr) throws SQLException, UnsupportedEncodingException {
		final int DISPLAY_THRESHOLD = 4;
		List<org.mousephenotype.cda.db.pojo.ReferenceDTO> references = referenceDAO.getReferenceRows(sSearch, orderByStr);

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		j.put("iTotalRecords", references.size());
		j.put("iTotalDisplayRecords", references.size());

		for (org.mousephenotype.cda.db.pojo.ReferenceDTO reference : references) {

			List<String> rowData = new ArrayList<>();
			Map<String,String> alleleSymbolinks = new LinkedHashMap<String,String>();
			List<String> alLinks = new ArrayList<>();

			if (reference.getAlleleAccessionIds() != null) {

				// show max of 50 alleles for a paper
				int alleleAccessionIdCount = reference.getAlleleAccessionIds().size() > 50 ? 50 : reference.getAlleleAccessionIds().size();

				for (int i = 0; i < alleleAccessionIdCount; i++) {
					String symbol = Tools.superscriptify(reference.getAlleleSymbols().get(i));

					if (symbol.equals("N/A")){
						continue;
					}

					String alleleLink;
					//String cssClass = "class='" +  (alleleSymbolinks.size() < DISPLAY_THRESHOLD ? "showMe" : "hideMe") + "'";
					String cssClass = "class='" + (i < DISPLAY_THRESHOLD ? "showMe" : "hideMe") + "'";

					if (reference.getImpcGeneLinks() != null && reference.getImpcGeneLinks().size() != 0) {

						if (i < reference.getImpcGeneLinks().size()) {
							alleleLink = "<div " + cssClass + "><a target='_blank' href='" + reference.getImpcGeneLinks().get(i) + "'>" + symbol + "</a></div>";
						} else {
							if (i > 0) {
								alleleLink = "<div " + cssClass + "><a target='_blank' href='" + reference.getImpcGeneLinks().get(0) + "'>" + symbol + "</a></div>";
							} else {
								alleleLink = alleleLink = "<div " + cssClass + ">" + symbol + "</div>";
							}
						}

						alleleSymbolinks.put(symbol, alleleLink);
					}
					else {
						// some allele id does not associate with a gene id in database yet
						alleleSymbolinks.put(symbol, symbol);
					}
				}

				if (alleleSymbolinks.size() > 5) {
					int num = alleleSymbolinks.size();
					int totalNum = reference.getAlleleAccessionIds().size();
					if (totalNum > num) {
						alleleSymbolinks.put("toggle", "<div class='alleleToggle' rel='" + num + "'>Show " + num + " of " + totalNum + " alleles...</div>");
					} else {
						alleleSymbolinks.put("toggle", "<div class='alleleToggle' rel='" + num + "'>Show all " + num + " alleles ...</div>");
					}
				}

				Iterator it = alleleSymbolinks.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					alLinks.add(pair.getValue().toString());
					it.remove(); // avoids a ConcurrentModificationException
				}
			}
//			else {
//				alLinks.add("Not available");
//			}

			int pmid = reference.getPmid();
			List<String> paperLinks = new ArrayList<>();
			List<String> paperLinksOther = new ArrayList<>();
			List<String> paperLinksPubmed = new ArrayList<>();
			List<String> paperLinksEuroPubmed = new ArrayList<>();
			String[] urlList = (reference.getPaperUrls() != null) ? reference.getPaperUrls().toArray(new String[0]) : new String[0];

			for (int i = 0; i < urlList.length; i ++) {
				String[] urls = urlList[i].split(",");

				int pubmedSeen = 0;
				int eupubmedSeen = 0;
				int otherSeen = 0;

				for (int k = 0; k < urls.length; k ++) {
					String url = urls[k];

					if (pubmedSeen != 1) {
						if (url.startsWith("http://www.pubmedcentral.nih.gov") && url.endsWith("pdf")) {
							paperLinksPubmed.add(url);

							pubmedSeen ++;
						} else if (url.startsWith("http://www.pubmedcentral.nih.gov") && url.endsWith(Integer.toString(pmid))) {
							paperLinksPubmed.add(url);
							pubmedSeen ++;
						}
					}
					if (eupubmedSeen != 1) {
						if (url.startsWith("http://europepmc.org/") && url.endsWith("pdf=render")) {
							paperLinksEuroPubmed.add(url);
							eupubmedSeen ++;
						} else if (url.startsWith("http://europepmc.org/")) {
							paperLinksEuroPubmed.add(url);
							eupubmedSeen ++;
						}
					}
					if (otherSeen != 1 &&  ! url.startsWith("http://www.pubmedcentral.nih.gov") &&  ! url.startsWith("http://europepmc.org/")) {
						paperLinksOther.add(url);
						otherSeen ++;
					}
				}
			}


			// ordered
//            paperLinks.addAll(paperLinksEuroPubmed);
//            paperLinks.addAll(paperLinksPubmed);
//            paperLinks.addAll(paperLinksOther);
//            rowData.add(StringUtils.join(paperLinks, ""));
			// for now show only one
			String paperLink = null;
			if (paperLinksEuroPubmed.size()>0){
				paperLink = paperLinksEuroPubmed.get(0);
			}
			else if (paperLinksPubmed.size()>0){
				paperLink = paperLinksPubmed.get(0);
			}
			else {
				paperLink = paperLinksOther.get(0);
			}

			rowData.add("<p><a href='" + paperLink + "'>" + reference.getTitle() + "</a></p>");
			rowData.add("<p class='author'>" + reference.getAuthor() + "</p>");
			rowData.add("<p><i>" + reference.getJournal() + "</i>, " + reference.getDateOfPublication() + "</p>");
			rowData.add("<p>PMID: " + Integer.toString(reference.getPmid()) + "</p>");
			if ( alLinks.size() > 0) {
				rowData.add("<div class='alleles'>IMPC allele: " + StringUtils.join(alLinks, ", ") + "</div>");
			}

			List<String> agencyList = new ArrayList();
			int agencyCount = reference.getGrantAgencies().size();

			// unique agency
			for (int i = 0; i < agencyCount; i++) {
				String grantAgency = reference.getGrantAgencies().get(i);
				if ( ! grantAgency.isEmpty()) {
					if ( ! agencyList.contains(grantAgency)) {
						agencyList.add(grantAgency);
					}
				}
			}


			if (agencyList.size() >0) {
				rowData.add("<p>Grant agency: " + StringUtils.join(agencyList, ", ") + "</p>");
			}
			// hidden in datatable: mesh terms
			String meshTerms = StringUtils.join(reference.getMeshTerms(), ", ");
			rowData.add("<p class='meshTerms'>" + meshTerms + "</p>");

			if (rowFormat){
				// single column
				String inOneRow = StringUtils.join(rowData, "");
				List<String> rowData2 = new ArrayList<>();
				rowData2.add("<div class='innerData'>" + inOneRow + "</div>");
				rowData = rowData2;
			}
			j.getJSONArray("aaData").add(rowData);

		}

		return j.toString();
	}

	public String fetch_allele_ref(int iDisplayLength, int iDisplayStart, String sSearch) throws SQLException, UnsupportedEncodingException {
		final int DISPLAY_THRESHOLD = 4;
		List<org.mousephenotype.cda.db.pojo.ReferenceDTO> references = referenceDAO.getReferenceRows(sSearch);

		JSONObject j = new JSONObject();
		j.put("aaData", new Object[0]);
		j.put("iTotalRecords", references.size());
		j.put("iTotalDisplayRecords", references.size());

		for (org.mousephenotype.cda.db.pojo.ReferenceDTO reference : references) {

			List<String> rowData = new ArrayList<>();
			Map<String,String> alleleSymbolinks = new LinkedHashMap<String,String>();
			List<String> alLinks = new ArrayList<>();

			if (reference.getAlleleAccessionIds() != null) {

				// show max of 50 alleles for a paper
				int alleleAccessionIdCount = reference.getAlleleAccessionIds().size() > 50 ? 50 : reference.getAlleleAccessionIds().size();

				for (int i = 0; i < alleleAccessionIdCount; i++) {
					String symbol = Tools.superscriptify(reference.getAlleleSymbols().get(i));
					String alleleLink;
					//String cssClass = "class='" +  (alleleSymbolinks.size() < DISPLAY_THRESHOLD ? "showMe" : "hideMe") + "'";
					String cssClass = "class='" + (i < DISPLAY_THRESHOLD ? "showMe" : "hideMe") + "'";

					if (reference.getImpcGeneLinks() != null && reference.getImpcGeneLinks().size() != 0) {

						if (i < reference.getImpcGeneLinks().size()) {
							alleleLink = "<div " + cssClass + "><a target='_blank' href='" + reference.getImpcGeneLinks().get(i) + "'>" + symbol + "</a></div>";
						} else {
							if (i > 0) {
								alleleLink = "<div " + cssClass + "><a target='_blank' href='" + reference.getImpcGeneLinks().get(0) + "'>" + symbol + "</a></div>";
							} else {
								alleleLink = alleleLink = "<div " + cssClass + ">" + symbol + "</div>";
							}
						}

						alleleSymbolinks.put(symbol, alleleLink);
					}
					else {
						// some allele id does not associate with a gene id in database yet
						alleleSymbolinks.put(symbol, symbol);
					}
				}

				if (alleleSymbolinks.size() > 5) {
					int num = alleleSymbolinks.size();
					int totalNum = reference.getAlleleAccessionIds().size();
					if (totalNum > num) {
						alleleSymbolinks.put("toggle", "<div class='alleleToggle' rel='" + num + "'>Show " + num + " of " + totalNum + " alleles...</div>");
					} else {
						alleleSymbolinks.put("toggle", "<div class='alleleToggle' rel='" + num + "'>Show all " + num + " alleles ...</div>");
					}
				}

				Iterator it = alleleSymbolinks.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					alLinks.add(pair.getValue().toString());
					it.remove(); // avoids a ConcurrentModificationException
				}
			}
			else {
				alLinks.add("Not available");
			}

			rowData.add(reference.getTitle());
			rowData.add(StringUtils.join(alLinks, ""));
			rowData.add(reference.getJournal());

			rowData.add("<span>" + reference.getDateOfPublication() + "</span>");


			List<String> agencyList = new ArrayList();
			int agencyCount = reference.getGrantAgencies().size();

			// unique agency
			for (int i = 0; i < agencyCount; i++) {
				String cssClass = "class='" +  (i < DISPLAY_THRESHOLD ? "showMe" : "hideMe") + "'";
				String grantAgency = reference.getGrantAgencies().get(i);
				if ( ! grantAgency.isEmpty()) {
					String thisAgency = "<li " + cssClass + ">" + grantAgency + "</li>";
					if ( ! agencyList.contains(thisAgency)) {
						agencyList.add(thisAgency);
					}
				}
			}

			rowData.add("<ul>" + StringUtils.join(agencyList, "") + "</ul>");

			int pmid = reference.getPmid();
			List<String> paperLinks = new ArrayList<>();
			List<String> paperLinksOther = new ArrayList<>();
			List<String> paperLinksPubmed = new ArrayList<>();
			List<String> paperLinksEuroPubmed = new ArrayList<>();
			String[] urlList = (reference.getPaperUrls() != null) ? reference.getPaperUrls().toArray(new String[0]) : new String[0];

			for (int i = 0; i < urlList.length; i ++) {
				String[] urls = urlList[i].split(",");

				int pubmedSeen = 0;
				int eupubmedSeen = 0;
				int otherSeen = 0;

				for (int k = 0; k < urls.length; k ++) {
					String url = urls[k];

					if (pubmedSeen != 1) {
						if (url.startsWith("http://www.pubmedcentral.nih.gov") && url.endsWith("pdf")) {
							paperLinksPubmed.add("<li><a target='_blank' href='" + url + "'>Pubmed Central</a></li>");
							pubmedSeen ++;
						} else if (url.startsWith("http://www.pubmedcentral.nih.gov") && url.endsWith(Integer.toString(pmid))) {
							paperLinksPubmed.add("<li><a target='_blank' href='" + url + "'>Pubmed Central</a></li>");
							pubmedSeen ++;
						}
					}
					if (eupubmedSeen != 1) {
						if (url.startsWith("http://europepmc.org/") && url.endsWith("pdf=render")) {
							paperLinksEuroPubmed.add("<li><a target='_blank' href='" + url + "'>Europe Pubmed Central</a></li>");
							eupubmedSeen ++;
						} else if (url.startsWith("http://europepmc.org/")) {
							paperLinksEuroPubmed.add("<li><a target='_blank' href='" + url + "'>Europe Pubmed Central</a></li>");
							eupubmedSeen ++;
						}
					}
					if (otherSeen != 1 &&  ! url.startsWith("http://www.pubmedcentral.nih.gov") &&  ! url.startsWith("http://europepmc.org/")) {
						paperLinksOther.add("<li><a target='_blank' href='" + url + "'>Non-pubmed source</a></li>");
						otherSeen ++;
					}
				}
			}

			// hidden
			rowData.add(Integer.toString(reference.getPmid()));

			// ordered
//            paperLinks.addAll(paperLinksEuroPubmed);
//            paperLinks.addAll(paperLinksPubmed);
//            paperLinks.addAll(paperLinksOther);
//            rowData.add(StringUtils.join(paperLinks, ""));
			// for now show only one
			if (paperLinksEuroPubmed.size()>0){
				rowData.add(paperLinksEuroPubmed.get(0));
			}
			else if (paperLinksPubmed.size()>0){
				rowData.add(paperLinksPubmed.get(0));
			}
			else {
				rowData.add(paperLinksOther.get(0));
			}

			// hidden in datatable: mesh terms
			String meshTerms = StringUtils.join(reference.getMeshTerms(), ", ");
			rowData.add(meshTerms);

			j.getJSONArray("aaData").add(rowData);

		}

		return j.toString();
	}

	public class MpAnnotations {
		public String mp_id;
		public String mp_term;
		public String mp_definition;
		public String top_level_mp_id;
		public String top_level_mp_term;
		public String mgi_accession_id;
		public String marker_symbol;
		public String human_gene_symbol;
		public String disease_id;
		public String disease_term;

	}
}
