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
package org.mousephenotype.cda.solr.service;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.AnatomyPageTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Pulled in 2015/07/09 by @author tudose
 *
 */

@Service
public class ExpressionService extends BasicService {

	@Autowired
	@Qualifier("experimentCore")
	private HttpSolrClient experimentSolr;

	@Autowired
	@Qualifier("impcImagesCore")
	private HttpSolrClient imagesSolr;

	@Autowired
	ExperimentService experimentService;

	@Autowired
	ImpressService impressService;

	@Autowired
	private AnatomyService anatomyService;

	// @Autowired
	// MaService maService;

	Map<String, OntologyBean> abnormalEmapaFromImpress = null;
	Map<String, OntologyBean> abnormalMaFromImpress = null;

	public ExpressionService() {
	}

	public ExpressionService(String experimentSolrUrl, String imagesSolrUrl, String impressServiceUrl,
			String anatomyServiceUrl) {

		experimentSolr = new HttpSolrClient(experimentSolrUrl);
		imagesSolr = new HttpSolrClient(imagesSolrUrl);
		impressService = new ImpressService(impressServiceUrl);
		anatomyService = new AnatomyService(anatomyServiceUrl);
	}

	@PostConstruct
	public void initialiseAbnormalOntologyMaps() {
		abnormalMaFromImpress = impressService.getParameterStableIdToAbnormalMaMap();
		abnormalEmapaFromImpress = impressService.getParameterStableIdToAbnormalEmapaMap();

	}

	public QueryResponse getExpressionImagesForGeneByAnatomy(String mgiAccession, String anatomy,
			String experimentOrControl, int numberOfImagesToRetrieve, SexType sex, String metadataGroup, String strain)
					throws SolrServerException, IOException, IOException {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ImageDTO.BIOLOGICAL_SAMPLE_GROUP + ":" + experimentOrControl);
		if (StringUtils.isNotEmpty(metadataGroup)) {
			solrQuery.addFilterQuery(ImageDTO.METADATA_GROUP + ":" + metadataGroup);
		}
		if (StringUtils.isNotEmpty(strain)) {
			solrQuery.addFilterQuery(ImageDTO.STRAIN_NAME + ":" + strain);
		}
		if (sex != null) {
			solrQuery.addFilterQuery(ImageDTO.SEX + ":" + sex.name());
		}
		if (StringUtils.isNotEmpty(anatomy)) {
			solrQuery.addFilterQuery(ImageDTO.PARAMETER_ASSOCIATION_NAME + ":\"" + anatomy + "\"");
		}
		// solrQuery.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":\"" +
		// procedure_name + "\"");
		solrQuery.setRows(numberOfImagesToRetrieve);
		QueryResponse response = imagesSolr.query(solrQuery);
		return response;
	}

	/**
	 *
	 * @param mgiAccession
	 *            if mgi accesion null assume a request for control data
	 * @param fields
	 * @return
	 * @throws SolrServerException, IOException, IOException
	 */
	public QueryResponse getExpressionTableDataImages(String mgiAccession, boolean embryo, String... fields)
			throws SolrServerException, IOException, IOException {
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:106209%22&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		SolrQuery solrQuery = new SolrQuery();
		if (mgiAccession != null) {
			solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
		} else {
			// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=biological_sample_group:control&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)&rows=100000
			solrQuery.setQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\"" + "control" + "\"");
		}
		if (embryo) {
			solrQuery.addFilterQuery(ImageDTO.PARAMETER_NAME + ":\"LacZ images Section\" OR " + ImageDTO.PARAMETER_NAME
					+ ":\"LacZ images wholemount\"");
		} else {
			solrQuery.addFilterQuery(ImageDTO.PARAMETER_NAME + ":\"LacZ Images Section\" OR " + ImageDTO.PARAMETER_NAME
					+ ":\"LacZ Images Wholemount\"");
		}

		solrQuery.setFields(fields);
		solrQuery.setRows(100000);
		QueryResponse response = imagesSolr.query(solrQuery);
		return response;
	}

	/**
	 *
	 * @param mgiAccession
	 *            if mgi accesion null assume a request for control data
	 * @param fields
	 * @return
	 * @throws SolrServerException, IOException, IOException
	 */
	private QueryResponse getCategoricalAdultLacZData(String mgiAccession, boolean embryo, String... fields)
			throws SolrServerException, IOException, IOException {
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=gene_accession_id:%22MGI:1351668%22&facet=true&facet.field=parameter_name&facet.mincount=1&fq=(procedure_name:%22Adult%20LacZ%22)&rows=10000
		SolrQuery solrQuery = new SolrQuery();
		if (mgiAccession != null) {
			solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
		} else {
			// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=biological_sample_group:control&facet=true&facet.field=ma_term&facet.mincount=1&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)&rows=100000
			solrQuery.setQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\"" + "control" + "\"");
		}
		if (embryo) {
			solrQuery.addFilterQuery(ImageDTO.PROCEDURE_NAME + ":\"Embryo LacZ\"");
			solrQuery.addFilterQuery("!" + ImageDTO.PARAMETER_NAME + ":\"LacZ images section\"");
			solrQuery.addFilterQuery("!" + ImageDTO.PARAMETER_NAME + ":\"LacZ images wholemount\"");
			solrQuery.addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":\"categorical\"");
		} else {
			solrQuery.addFilterQuery(ImageDTO.PROCEDURE_NAME + ":\"Adult LacZ\"");
			solrQuery.addFilterQuery("!" + ImageDTO.PARAMETER_NAME + ":\"LacZ Images Section\"");
			solrQuery.addFilterQuery("!" + ImageDTO.PARAMETER_NAME + ":\"LacZ Images Wholemount\"");
			solrQuery.addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":\"categorical\"");
		}

		// solrQuery.setFacetMinCount(1);
		// solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		// solrQuery.addFacetField("ma_term");
		solrQuery.setRows(100000);
		QueryResponse response = experimentSolr.query(solrQuery);
		return response;
	}

	private QueryResponse getAdultLaczImageFacetsForGene(String mgiAccession, String... fields) throws SolrServerException, IOException, IOException {
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:1920455%22&facet=true&facet.field=selected_top_level_ma_term&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		// for embryo data the fields would be like this
		// "parameter_name": "LacZ images section",
		// "procedure_name": "Embryo LacZ",

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ImageDTO.PARAMETER_NAME + ":\"LacZ Images Section\" OR " + ImageDTO.PARAMETER_NAME
				+ ":\"LacZ Images Wholemount\"");// reduce the number to image
													// parameters only as we are
													// talking about images not
													// expression data here
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		solrQuery.addFacetField(ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM);
		solrQuery.setRows(100000);
		QueryResponse response = imagesSolr.query(solrQuery);
		return response;
	}

	public List<Count> getLaczCategoricalParametersForGene(String mgiAccession, String... fields)
			throws SolrServerException, IOException, IOException {
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:1920455%22&facet=true&facet.field=selected_top_level_ma_term&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		// for embryo data the fields would be like this
		// "parameter_name": "LacZ images section",
		// "procedure_name": "Embryo LacZ",

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");

		solrQuery.addFilterQuery(ImageDTO.PROCEDURE_NAME + ":\"Adult LacZ\"");
		solrQuery.addFilterQuery("!" + ImageDTO.PARAMETER_NAME + ":\"LacZ Images Section\"");
		solrQuery.addFilterQuery("!" + ImageDTO.PARAMETER_NAME + ":\"LacZ Images Wholemount\"");
		solrQuery.addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":\"categorical\"");
		solrQuery.addFilterQuery(ObservationDTO.CATEGORY + ":\"expression\"");// only
																				// look
																				// for
																				// expresssion
																				// at
																				// the
																				// moment
																				// as
																				// that
																				// is
																				// all
																				// the
																				// anatamogram
																				// can
																				// display
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		solrQuery.addFacetField(ImageDTO.PARAMETER_STABLE_ID);
		solrQuery.setRows(0);
		QueryResponse response = experimentSolr.query(solrQuery);
		List<FacetField> categoryParameterFields = response.getFacetFields();
		return categoryParameterFields.get(0).getValues();
	}

	private QueryResponse getEmbryoLaczImageFacetsForGene(String mgiAccession, String... fields)
			throws SolrServerException, IOException, IOException {
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:1920455%22&facet=true&facet.field=selected_top_level_ma_term&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		// for embryo data the fields would be like this
		// "parameter_name": "LacZ images section",
		// "procedure_name": "Embryo LacZ",

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ImageDTO.PARAMETER_STABLE_ID + ":IMPC_ELZ_064_001" + " OR "
				+ ImageDTO.PARAMETER_STABLE_ID + ":IMPC_ELZ_063_001");// reduce
																		// the
																		// number
																		// to
																		// image
																		// parameters
																		// only
																		// as we
																		// are
																		// talking
																		// about
																		// images
																		// not
																		// expression
																		// data
																		// here
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		solrQuery.addFacetField(ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM);
		solrQuery.setRows(100000);
		QueryResponse response = imagesSolr.query(solrQuery);
		return response;
	}

	/**
	 *
	 * @param acc
	 *            mgi_accession for gene
	 * @param topMaNameFilter
	 *            Only include images under the top level ma term specified here
	 * @param imagesOverview
	 *            If imagesOverview true then restrict response to only certain
	 *            fields as we are only displaying annotations for a dataset not
	 *            a specific thumbnail
	 * @param imagesOverview
	 *            If true we want some images data/stats added to the model for
	 *            display in the tabbed pane on the gene page.
	 * @param model
	 *            Spring MVC model
	 * @throws SolrServerException, IOException, IOException
	 * @throws SQLException
	 */
	public void getLacImageDataForGene(String acc, String topMaNameFilter, boolean imagesOverview, boolean embryoOnly,
			Model model) throws SolrServerException, IOException, IOException {
		QueryResponse laczResponse = null;
		String noTopTermId = "";
		String topLevelField = "";// type ma or emap imageDTO field for top
									// level terms
		String termIdField = "";
		if (embryoOnly) { // use EMAP terms and top level terms
			noTopTermId = "TS20 embryo or Unassigned";// currently if unassigned
														// they either have
														// embryo TS20 as there
														// EMAP id but our
														// system doesn't find
														// any
														// selected_top_level
														// emap or nothing is
														// assigned but we know
														// they are embryo so
														// assign this id to
														// unassigned
			topLevelField = ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM;
			termIdField = ImageDTO.ANATOMY_ID;
			if (imagesOverview) {
				laczResponse = getEmbryoLaczImageFacetsForGene(acc, ImageDTO.OMERO_ID, ImageDTO.JPEG_URL, topLevelField,
						ImageDTO.PARAMETER_ASSOCIATION_NAME, ImageDTO.PARAMETER_ASSOCIATION_VALUE, ImageDTO.ANATOMY_ID,
						ImageDTO.ANATOMY_TERM);
			} else {
				laczResponse = getEmbryoLaczImageFacetsForGene(acc, ImageDTO.OMERO_ID, ImageDTO.JPEG_URL, topLevelField,
						ImageDTO.PARAMETER_ASSOCIATION_NAME, ImageDTO.PARAMETER_ASSOCIATION_VALUE, ImageDTO.ZYGOSITY,
						ImageDTO.SEX, ImageDTO.ALLELE_SYMBOL, ImageDTO.DOWNLOAD_URL, ImageDTO.IMAGE_LINK,
						ImageDTO.ANATOMY_ID, ImageDTO.ANATOMY_TERM);
			}

		} else {
			noTopTermId = "Unassigned Top Level MA";
			topLevelField = ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM;
			termIdField = ImageDTO.ANATOMY_ID;
			if (imagesOverview) {
				laczResponse = getAdultLaczImageFacetsForGene(acc, ImageDTO.OMERO_ID, ImageDTO.JPEG_URL, topLevelField,
						ImageDTO.PARAMETER_ASSOCIATION_NAME, ImageDTO.PARAMETER_ASSOCIATION_VALUE, ImageDTO.ANATOMY_ID,
						ImageDTO.UBERON_ID, ImageDTO.EFO_ID);
			} else {
				laczResponse = getAdultLaczImageFacetsForGene(acc, ImageDTO.OMERO_ID, ImageDTO.JPEG_URL, topLevelField,
						ImageDTO.PARAMETER_ASSOCIATION_NAME, ImageDTO.PARAMETER_ASSOCIATION_VALUE, ImageDTO.ZYGOSITY,
						ImageDTO.SEX, ImageDTO.ALLELE_SYMBOL, ImageDTO.DOWNLOAD_URL, ImageDTO.IMAGE_LINK,
						ImageDTO.ANATOMY_ID, ImageDTO.UBERON_ID, ImageDTO.EFO_ID);
			}

		}
		SolrDocumentList imagesResponse = laczResponse.getResults();
		List<FacetField> fields = laczResponse.getFacetFields();
		// we have the unique ma top level terms associated and all the images
		// now we need lists of images with these top level ma terms in their
		// annotation
		Map<String, SolrDocumentList> expFacetToDocs = new HashMap<>();
		expFacetToDocs.put(noTopTermId, new SolrDocumentList());


		for (SolrDocument doc : imagesResponse) {
			List<String> tops = getListFromCollection(doc.getFieldValues(topLevelField));

			// work out list of uberon/efo ids with/without expressions
			

			// noTopLevelCount.setCount(c);
			if (tops.isEmpty()) {// if no top level found this image then add it
									// to the "No top level" term docs so we can
									// display orphaned terms and images
				expFacetToDocs.get(noTopTermId).add(doc);
			} else {

				for (String top : tops) {
					SolrDocumentList list = null;
					if (!expFacetToDocs.containsKey(top)) {
						expFacetToDocs.put(top, new SolrDocumentList());
					}
					list = expFacetToDocs.get(top);
					list.add(doc);
				}
			}
		}

		
		List<Count> topLevelAnatomyTerms = new ArrayList<>();
		List<Count> filteredTopLevelAnatomyTerms = new ArrayList<>();
		// if (fields.get(0).getValues().size() > 0) {

		topLevelAnatomyTerms.addAll(fields.get(0).getValues());
		if (expFacetToDocs.get(noTopTermId).size() > 0) {// only add this
															// facet for no
															// top levels
															// found if
															// there are any
			Count dummyCountForImagesWithNoHigherLevelAnatomy = new Count(new FacetField(noTopTermId), noTopTermId,
					expFacetToDocs.get(noTopTermId).size());
			topLevelAnatomyTerms.add(dummyCountForImagesWithNoHigherLevelAnatomy);
		}

		if (topMaNameFilter != null) {
			for (Count topLevel : topLevelAnatomyTerms) {
				if (topLevel.getName().equals(topMaNameFilter)) {
					filteredTopLevelAnatomyTerms.add(topLevel);
				}
			}
		} else {
			filteredTopLevelAnatomyTerms = topLevelAnatomyTerms;
		}

		ImageServiceUtil.sortHigherLevelTermCountsAlphabetically(filteredTopLevelAnatomyTerms);
		ImageServiceUtil.sortDocsByExpressionAlphabetically(expFacetToDocs);

		System.out.println("Check Top level anatomy terms: " + filteredTopLevelAnatomyTerms);
		if (embryoOnly) {
			model.addAttribute("impcEmbryoExpressionImageFacets", filteredTopLevelAnatomyTerms);
			model.addAttribute("impcEmbryoExpressionFacetToDocs", expFacetToDocs);

		} else {
			model.addAttribute("impcAdultExpressionImageFacets", filteredTopLevelAnatomyTerms);
			model.addAttribute("impcAdultExpressionFacetToDocs", expFacetToDocs);
		}
		// }
	}

	/**
	 * 
	 * @param anatomogramDataBeans
	 * @return
	 * @throws SolrServerException, IOException, IOException
	 */
	public Map<String, Long> getLacSelectedTopLevelMaCountsForAnatomogram(
			List<AnatomogramDataBean> anatomogramDataBeans) throws SolrServerException, IOException, IOException {
		Map<String, Long> topLevelMaToCountMap = new HashMap<>();
		for (AnatomogramDataBean bean : anatomogramDataBeans) {
			for (String topMaId : bean.getTopLevelMaNames()) {
				if (!topLevelMaToCountMap.containsKey(topMaId)) {
					topLevelMaToCountMap.put(topMaId, new Long(0));
				}
				Long count = topLevelMaToCountMap.get(topMaId);
				count = count + bean.getCount();
				topLevelMaToCountMap.put(topMaId, count);

			}
		}

		return topLevelMaToCountMap;
	}
	
	/**
	 * 
	 * @param anatomogramDataBeans
	 * @return
	 * @throws SolrServerException, IOException, IOException
	 */
	public Set<String> getLacSelectedTopLevelMaIdsForAnatomogram(
			List<AnatomogramDataBean> anatomogramDataBeans) throws SolrServerException, IOException, IOException {
		Set<String> topLevelMaToCountMap = new HashSet<>();
		for (AnatomogramDataBean bean : anatomogramDataBeans) {
			for (String topMaId : bean.getTopLevelMaIds()) {
					topLevelMaToCountMap.add(topMaId);
			}
		}

		return topLevelMaToCountMap;
	}

	public List<AnatomogramDataBean> getAnatomogramDataBeans(List<Count> parameterCounts) throws SolrServerException, IOException, IOException {
		List<AnatomogramDataBean> anatomogramDataBeans = new ArrayList<>();

		//if the solr core wasn't up before the webapp starts then these maps maybe empty - of so we need to populate them before continuing
		if(abnormalMaFromImpress ==null || abnormalMaFromImpress.size()==0){
			this.initialiseAbnormalOntologyMaps();
		}
		
		for (Count count : parameterCounts) {
			AnatomogramDataBean bean = new AnatomogramDataBean();
			bean.setParameterId(count.getName());
			bean.setPatameterName(count.getName());
			bean.setCount(count.getCount());
			
			if (abnormalMaFromImpress.containsKey(count.getName())) {
				OntologyBean ontologyBean = abnormalMaFromImpress.get(count.getName());
				bean.setMaId(ontologyBean.getId());
				bean.setMaTerm(ontologyBean.getName());
				// this method for getting uberon ids needs to be changed so
				// we get the associated intermediate terms so we include
				// all possible uberon ids
				// higher up the tree to display on the anatomogram
				bean = anatomyService.getUberonIdAndTopLevelMaTerm(bean);

				anatomogramDataBeans.add(bean);
			}
		}
		return anatomogramDataBeans;

	}

	public JSONObject getAnatomogramJson(List<AnatomogramDataBean> anatomogramDataBeans) {
		JSONObject anatomogram = new JSONObject();

		JSONArray expList = new JSONArray();
		JSONArray noExpList = new JSONArray();
		JSONArray allPaths = new JSONArray();

		Map<String, Set<String>>maId2Uberon = new HashMap<>();
		Map<String, Set<String>>uberon2MaId = new HashMap<>();
		Map<String, Set<String>>topLevelName2maId = new HashMap<>();
		Map<String, List<String>>maId2topLevelName = new HashMap<>();

		for (AnatomogramDataBean dataBean : anatomogramDataBeans) {
			if (dataBean.getMaId() != null && dataBean.getUberonIds() != null) {

				List<String> uberonIds = dataBean.getUberonIds();

				addAnatomogramSpecialIds(dataBean, expList, allPaths, uberonIds, maId2Uberon, uberon2MaId, maId2topLevelName, topLevelName2maId);
				if (dataBean.getEfoIds() != null) {
					List<String> efoIds = dataBean.getEfoIds();
					addAnatomogramSpecialIds(dataBean, expList, allPaths, efoIds, maId2Uberon, uberon2MaId, maId2topLevelName, topLevelName2maId);
				}
			}
		}

		anatomogram.put("expression", expList);
		anatomogram.put("noExpression", noExpList);
		anatomogram.put("allPaths", allPaths);

		anatomogram.put("topLevelName2maIdMap", topLevelName2maId);
		anatomogram.put("maId2UberonMap", maId2Uberon);

		anatomogram.put("uberon2MaIdMap", uberon2MaId);
		anatomogram.put("maId2topLevelNameMap", maId2topLevelName);


		//System.out.println("ANATOMOGRAM: " + anatomogram);
		return anatomogram;
	}

	private void addAnatomogramSpecialIds(AnatomogramDataBean dataBean, JSONArray expList, JSONArray allPaths, List<String> uberonOrEfoIds,
				  Map<String, Set<String>> maId2Uberon,
				  Map<String, Set<String>> uberon2MaId,
				  Map<String, List<String>> maId2topLevelName,
				  Map<String, Set<String>> topLevelName2maId ) {

		String maTermId = dataBean.getMaId();
		List<String> maTopLevelNames = dataBean.getTopLevelMaNames();

		maId2topLevelName.put(maTermId, maTopLevelNames);

		for (String toplevelname : maTopLevelNames ){
			if (!topLevelName2maId.containsKey(toplevelname)) {
				topLevelName2maId.put(toplevelname, new HashSet<>());
			}
			topLevelName2maId.get(toplevelname).add(maTermId);
		}


		for (String id : uberonOrEfoIds) {

			if (!uberon2MaId.containsKey(id)) {
				uberon2MaId.put(id, new HashSet<>());
			}
			uberon2MaId.get(id).add(maTermId);

			if (!maId2Uberon.containsKey(maTermId)) {
				maId2Uberon.put(maTermId, new HashSet<>());
			}
			maId2Uberon.get(maTermId).add(id);

			JSONObject exp = new JSONObject();
			exp.put("factorName", maTermId); // used as a note to say what this id is, blank if unknown
			exp.put("value", "1");
			exp.put("svgPathId", id);
			if (!expList.contains(exp)) {
				expList.add(exp);
			}
			if (!allPaths.contains(id)) {
				allPaths.add(id);
			}
		}
	}

	/**
	 *
	 * @param acc
	 *            mgi_accession for gene
	 * @param model
	 *            Spring MVC model
	 * @throws SolrServerException, IOException, IOException
	 */
	public Model getExpressionDataForGene(String acc, Model model, boolean embryo) throws SolrServerException, IOException, IOException {

		QueryResponse laczDataResponse = getCategoricalAdultLacZData(acc, embryo, ImageDTO.ZYGOSITY,
				ImageDTO.EXTERNAL_SAMPLE_ID, ObservationDTO.OBSERVATION_TYPE, ObservationDTO.PARAMETER_STABLE_ID,
				ObservationDTO.PARAMETER_NAME, ObservationDTO.CATEGORY, ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList mutantCategoricalAdultLacZData = laczDataResponse.getResults();
		Map<String, SolrDocumentList> expressionAnatomyToDocs = getAnatomyToDocsForCategorical(
				mutantCategoricalAdultLacZData);
		Map<String, ExpressionRowBean> expressionAnatomyToRow = new TreeMap<>();
		Map<String, ExpressionRowBean> wtAnatomyToRow = new TreeMap<>();

		QueryResponse wtLaczDataResponse = getCategoricalAdultLacZData(null, embryo, ImageDTO.ZYGOSITY,
				ImageDTO.EXTERNAL_SAMPLE_ID, ObservationDTO.OBSERVATION_TYPE, ObservationDTO.PARAMETER_NAME,
				ObservationDTO.CATEGORY, ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList wtCategoricalAdultLacZData = wtLaczDataResponse.getResults();
		Map<String, SolrDocumentList> wtAnatomyToDocs = getAnatomyToDocsForCategorical(wtCategoricalAdultLacZData);

		QueryResponse laczImagesResponse = null;

		laczImagesResponse = getExpressionTableDataImages(acc, embryo, ImageDTO.ZYGOSITY,
				ImageDTO.PARAMETER_ASSOCIATION_NAME, ObservationDTO.OBSERVATION_TYPE,
				ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList imagesMutantResponse = laczImagesResponse.getResults();
		Map<String, ExpressionRowBean> mutantImagesAnatomyToRow = new TreeMap<>();
		Map<String, SolrDocumentList> mutantImagesAnatomyToDocs = getAnatomyToDocs(imagesMutantResponse);

		for (String anatomy : expressionAnatomyToDocs.keySet()) {

			ExpressionRowBean expressionRow = getAnatomyRow(anatomy, expressionAnatomyToDocs, embryo);
			int hetSpecimens = 0;
			for (String key : expressionRow.getSpecimen().keySet()) {

				if (expressionRow.getSpecimen().get(key).getZyg().equalsIgnoreCase("heterozygote")) {
					hetSpecimens++;
				}
			}
			expressionRow.setNumberOfHetSpecimens(hetSpecimens);
			expressionAnatomyToRow.put(anatomy, expressionRow);

			ExpressionRowBean wtRow = getAnatomyRow(anatomy, wtAnatomyToDocs, embryo);

			if (wtRow.getSpecimenExpressed().keySet().size() > 0) {
				wtRow.setWildTypeExpression(true);
			}
			wtAnatomyToRow.put(anatomy, wtRow);

			ExpressionRowBean mutantImagesRow = getAnatomyRow(anatomy, mutantImagesAnatomyToDocs, embryo);
			mutantImagesRow.setNumberOfHetSpecimens(hetSpecimens);
			mutantImagesAnatomyToRow.put(anatomy, mutantImagesRow);

		}

		if (embryo) {
			model.addAttribute("embryoExpressionAnatomyToRow", expressionAnatomyToRow);
			model.addAttribute("embryoMutantImagesAnatomyToRow", mutantImagesAnatomyToRow);
			model.addAttribute("embryoWtAnatomyToRow", wtAnatomyToRow);
		} else {
			model.addAttribute("expressionAnatomyToRow", expressionAnatomyToRow);
			model.addAttribute("mutantImagesAnatomyToRow", mutantImagesAnatomyToRow);
			model.addAttribute("wtAnatomyToRow", wtAnatomyToRow);
		}
		return model;

	}

	
	public List<AnatomyPageTableRow> getLacZDataForAnatomy(String anatomyId,List<String> anatomyTerms, List<String> phenotypingCenter,
		List<String> procedure, List<String> paramAssoc, String baseUrl)
					throws SolrServerException, IOException, IOException {
		Map<String, AnatomyPageTableRow> res = new HashMap<>();
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/select?q=*:*&fq=(anatomy_id:%22MA:0000031%22%20OR%20intermediate_anatomy_id:%22MA:0000031%22%20OR%20selected_top_level_anatomy_id:%22MA0000031%22)
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		if (anatomyId != null) {
			query.addFilterQuery("(" + ObservationDTO.ANATOMY_ID + ":\"" + anatomyId + "\" OR "
					+ ObservationDTO.INTERMEDIATE_ANATOMY_ID + ":\"" + anatomyId + "\" OR "
					+ ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_ID + ":\"" + anatomyId + "\")");
		}

		query.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":*LacZ")
				.addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":\"categorical\"")
				.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\"experimental\"")
				.addFilterQuery("(" + ObservationDTO.CATEGORY + ":\"no expression\" OR " + ObservationDTO.CATEGORY
						+ ":\"expression\"" + ")") // only have expressed and
													// not expressed ingnore
													// ambiguous and no tissue
				.setRows(Integer.MAX_VALUE).setFields(ObservationDTO.SEX, ObservationDTO.ALLELE_SYMBOL,
						ObservationDTO.ALLELE_ACCESSION_ID, ObservationDTO.ZYGOSITY, ObservationDTO.ANATOMY_ID,
						ObservationDTO.ANATOMY_TERM, ObservationDTO.PROCEDURE_STABLE_ID, ObservationDTO.DATASOURCE_NAME,
						// ObservationDTO.PARAMETER_ASSOCIATION_VALUE,
						ObservationDTO.GENE_SYMBOL, ObservationDTO.GENE_ACCESSION_ID, ObservationDTO.PARAMETER_NAME,
						ObservationDTO.PARAMETER_STABLE_ID, ObservationDTO.PROCEDURE_NAME,
						ObservationDTO.PHENOTYPING_CENTER, ObservationDTO.INTERMEDIATE_ANATOMY_ID,
						ObservationDTO.INTERMEDIATE_ANATOMY_TERM, ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_ID,
						ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM, ObservationDTO.CATEGORY);

		if (anatomyTerms != null) {
			query.addFilterQuery(ObservationDTO.ANATOMY_TERM + ":\""
					+ StringUtils.join(anatomyTerms, "\" OR " + ObservationDTO.ANATOMY_TERM + ":\"") + "\"");
		}
		if (phenotypingCenter != null) {
			query.addFilterQuery(ObservationDTO.PHENOTYPING_CENTER + ":\""
					+ StringUtils.join(phenotypingCenter, "\" OR " + ObservationDTO.PHENOTYPING_CENTER + ":\"") + "\"");
		}
		if (procedure != null) {
			query.addFilterQuery(ObservationDTO.PROCEDURE_NAME + ":\""
					+ StringUtils.join(procedure, "\" OR " + ObservationDTO.PROCEDURE_NAME + ":\"") + "\"");
		}
		 if (paramAssoc != null) {
		 query.addFilterQuery(ObservationDTO.CATEGORY
		 + ":\""
		 + StringUtils.join(paramAssoc, "\" OR "
		 + ObservationDTO.CATEGORY + ":\"")
		 + "\"");
		 }

		System.out.println("SOLR URL WAS " + experimentSolr.getBaseURL() + "/select?" + query);
		List<ObservationDTO> response = experimentSolr.query(query).getBeans(ObservationDTO.class);
		for (ObservationDTO observation : response) {

			// for (String expressionValue :
			// observation.getDistinctParameterAssociationsValue()) {
			String expressionValue = observation.getCategory();

			AnatomyPageTableRow row = new AnatomyPageTableRow(observation, anatomyId, baseUrl, expressionValue);
			if (res.containsKey(row.getKey())) {
				row = res.get(row.getKey());
				row.addSex(observation.getSex());
				// row.addImage();
			}
			res.put(row.getKey(), row);

			// }
		}

		return new ArrayList<>(res.values());

	}

	/**
	 * @author ilinca
	 * @since 2016/07/08
	 * @param anatomyId
	 * @return List of gene ids with positive expression in given anatomy term. 
	 * @throws SolrServerException, IOException, IOException
	 */
	public List<String> getGenesWithExpression(String anatomyId) throws SolrServerException, IOException, IOException{
		
		List<String> geneIds = new ArrayList<>();
		SolrQuery q = new SolrQuery();
		q.setQuery(ObservationDTO.CATEGORY + ":\"expression\"" )
			.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\"experimental\"");
		if (anatomyId != null){
			q.setFilterQueries(ObservationDTO.ANATOMY_ID + ":\"" + anatomyId + "\" OR " + ObservationDTO.TOP_LEVEL_ANATOMY_ID + ":\"" + anatomyId + "\" OR " +ObservationDTO.INTERMEDIATE_ANATOMY_ID + ":\"" + anatomyId + "\"" );
		}
		q.setFacet(true)
			.setFacetMinCount(1)
			.setFacetLimit(-1)
			.addFacetField(ObservationDTO.GENE_ACCESSION_ID);
		
		for (Count value: experimentSolr.query(q).getFacetFields().get(0).getValues()){
			geneIds.add(value.getName());
		}
		return geneIds;
		
	}
	
	
	private ExpressionRowBean getAnatomyRow(String anatomy, Map<String, SolrDocumentList> anatomyToDocs,
			boolean embryo) {

		ExpressionRowBean row = new ExpressionRowBean();
		if (anatomyToDocs.containsKey(anatomy)) {

			for (SolrDocument doc : anatomyToDocs.get(anatomy)) {

				if (doc.containsKey(ObservationDTO.OBSERVATION_TYPE)
						&& doc.get(ObservationDTO.OBSERVATION_TYPE).equals("categorical")) {

					if (doc.containsKey(ImageDTO.PARAMETER_STABLE_ID) && row.getParameterStableId() == null) {
						String parameterStableId = (String) doc.get(ImageDTO.PARAMETER_STABLE_ID);
						row.setParameterStableId(parameterStableId);
						OntologyBean ontologyBean = null;
						if (embryo) {
							ontologyBean = abnormalEmapaFromImpress.get(parameterStableId);
						} else {
							ontologyBean = abnormalMaFromImpress.get(parameterStableId);
						}

						if (ontologyBean != null) {
							row.setAbnormalAnatomyId(ontologyBean.getId());
							row.setAbnormalAnatomyName(StringUtils.capitalize(ontologyBean.getName()));
						} else {
							System.out.println("no anatomy id for param id: " + parameterStableId);
						}
					}
					row = getExpressionCountForAnatomyTerm(anatomy, row, doc);
				} else if (doc.get(ObservationDTO.OBSERVATION_TYPE).equals("image_record")
						&& doc.get(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP).equals("experimental")) {// assume
																									// image
																									// with
																									// parameterAssociation
					row = homImages(row, doc);
					row.setImagesAvailable(true);
					row.setNumberOfImages(row.getNumberOfImages() + 1);
				}
			}
			if (row.getSpecimenExpressed().keySet().size() > 0) {
				row.setExpression(true);
			}
			if (row.getSpecimenNotExpressed().keySet().size() > 0) {
				row.setNotExpressed(true);
			}
			if (row.getSpecimenNoTissueAvailable().keySet().size() > 0) {
				row.setNoTissueAvailable(true);
			}

		}
		row.anatomy = anatomy;
		// System.out.println(row);
		return row;
	}

	/**
	 * Are there hom images in this set (needed for the expression table on gene
	 * page
	 *
	 * @param row
	 * @param doc
	 * @return
	 */
	private ExpressionRowBean homImages(ExpressionRowBean row, SolrDocument doc) {

		if (doc.containsKey(ImageDTO.ZYGOSITY)) {
			if (doc.get(ImageDTO.ZYGOSITY).equals("homozygote")) {
				row.setHomImages(true);
			}
		}
		return row;
	}

	private ExpressionRowBean getExpressionCountForAnatomyTerm(String anatomy, ExpressionRowBean row,
			SolrDocument doc) {

		if (doc.containsKey(ImageDTO.PARAMETER_NAME)) {
			String paramAssName = (String) doc.get(ObservationDTO.PARAMETER_NAME);
			String paramAssValue = (String) doc.get(ObservationDTO.CATEGORY);

			String sampleId = (String) doc.get(ImageDTO.EXTERNAL_SAMPLE_ID);
			String zyg = (String) doc.get(ImageDTO.ZYGOSITY);
			if (paramAssName.equalsIgnoreCase(anatomy)) {
				row.addSpecimen(sampleId, zyg);
				if (paramAssValue.equalsIgnoreCase("expression")) {
					row.addSpecimenExpressed(sampleId, zyg);

				} else if (paramAssValue.equalsIgnoreCase("tissue not available")) {
					row.addNoTissueAvailable(sampleId, zyg);

				} else if (paramAssValue.equalsIgnoreCase("no expression")) {
					row.addNotExpressed(sampleId, zyg);

				}
			}

		}

		return row;
	}

	private Map<String, SolrDocumentList> getAnatomyToDocs(SolrDocumentList controlResponse) {

		Map<String, SolrDocumentList> anatomyToDocs = new HashMap<>();

		for (SolrDocument doc : controlResponse) {

			List<String> anatomies = getListFromCollection(doc.getFieldValues(ImageDTO.PARAMETER_ASSOCIATION_NAME));
			if (anatomies != null) {

				SolrDocumentList anatomyList = null;
				for (String anatomy : anatomies) {

					if (!anatomyToDocs.containsKey(anatomy)) {
						anatomyToDocs.put(anatomy, new SolrDocumentList());
					}
					anatomyList = anatomyToDocs.get(anatomy);

					anatomyList.add(doc);
				}
			}

		}
		return anatomyToDocs;
	}

	private Map<String, SolrDocumentList> getAnatomyToDocsForCategorical(SolrDocumentList response) {
		Map<String, SolrDocumentList> anatomyToDocs = new HashMap<>();
		for (SolrDocument doc : response) {
			if (doc.containsKey(ObservationDTO.OBSERVATION_TYPE)
					&& doc.get(ObservationDTO.OBSERVATION_TYPE).equals("categorical")) {
				String anatomy = (String) doc.get(ImageDTO.PARAMETER_NAME);
				SolrDocumentList anatomyList = null;
				if (!anatomyToDocs.containsKey(anatomy)) {
					anatomyToDocs.put(anatomy, new SolrDocumentList());
				}

				anatomyList = anatomyToDocs.get(anatomy);
				anatomyList.add(doc);
			}
		}
		return anatomyToDocs;
	}

	/**
	 * class for storing just the data needed for one row of the expression
	 * table on the gene page
	 *
	 * @author jwarren
	 *
	 */
	public class ExpressionRowBean {
		@Override
		public String toString() {
			return "ExpressionRowBean [anatomy=" + anatomy + ", abnormalAnatomyId=" + abnormalAnatomyId + ", numberOfImages="
					+ numberOfImages + ", parameterStableId=" + parameterStableId + ", abnormalAnatomyName=" + abnormalAnatomyName
					+ ", homImages=" + homImages + ", wildTypeExpression=" + wildTypeExpression + ", expression="
					+ expression + ", notExpressed=" + notExpressed + ", noTissueAvailable=" + noTissueAvailable
					+ ", imagesAvailable=" + imagesAvailable + ", specimenExpressed=" + specimenExpressed
					+ ", specimen=" + specimen + ", numberOfHetSpecimens=" + numberOfHetSpecimens
					+ ", specimenNotExpressed=" + specimenNotExpressed + ", specimenNoTissueAvailable="
					+ specimenNoTissueAvailable + "]";
		}

		String anatomy;
		String abnormalAnatomyId;
		private int numberOfImages;

		public int getNumberOfImages() {
			return numberOfImages;
		}

		public String getAbnormalAnatomyId() {
			return abnormalAnatomyId;
		}

		public void setNumberOfImages(int numberOfImages) {
			this.numberOfImages = numberOfImages;

		}

		public void setAbnormalAnatomyId(String abnormalAnatomyId) {
			this.abnormalAnatomyId = abnormalAnatomyId;
		}

		private String parameterStableId;
		private String abnormalAnatomyName;

		public String getAbnormalAnatomyName() {
			return abnormalAnatomyName;
		}

		public void setAbnormalAnatomyName(String abnormalAnatomyName) {
			this.abnormalAnatomyName = abnormalAnatomyName;
		}

		public String getParameterStableId() {
			return parameterStableId;
		}

		public void setAnatomyName(String anatomyNameName) {
			this.abnormalAnatomyName = anatomyNameName;

		}

		public void setParameterStableId(String parameterStableId) {
			this.parameterStableId = parameterStableId;
		}

		boolean homImages = false;
		boolean wildTypeExpression = false;
		boolean expression = false;

		public boolean isExpression() {
			return expression;
		}

		public void setExpression(boolean expression) {
			this.expression = expression;
		}

		public boolean isNotExpressed() {
			return notExpressed;
		}

		public void setNotExpressed(boolean notExpressed) {
			this.notExpressed = notExpressed;
		}

		public boolean isNoTissueAvailable() {
			return noTissueAvailable;
		}

		public void setNoTissueAvailable(boolean noTissueAvailable) {
			this.noTissueAvailable = noTissueAvailable;
		}

		boolean notExpressed = false;
		boolean noTissueAvailable = false;

		private boolean imagesAvailable;

		public boolean isImagesAvailable() {
			return imagesAvailable;
		}

		public void setImagesAvailable(boolean b) {
			this.imagesAvailable = b;
		}

		Map<String, Specimen> specimenExpressed = new HashMap<>();
		Map<String, Specimen> specimen = new HashMap<>();

		public boolean isHomImages() {
			return homImages;
		}

		public void setHomImages(boolean homImages) {
			this.homImages = homImages;
		}

		public boolean isWildTypeExpression() {
			return wildTypeExpression;
		}

		public void setWildTypeExpression(boolean wildTypeExpression) {
			this.wildTypeExpression = wildTypeExpression;
		}

		int numberOfHetSpecimens;
		private Map<String, Specimen> specimenNotExpressed = new HashMap<>();;
		private Map<String, Specimen> specimenNoTissueAvailable = new HashMap<>();;

		public int getNumberOfHetSpecimens() {
			return numberOfHetSpecimens;
		}

		public void setNumberOfHetSpecimens(int numberOfHetSpecimens) {
			this.numberOfHetSpecimens = numberOfHetSpecimens;
		}

		public Map<String, Specimen> getSpecimenExpressed() {
			return specimenExpressed;
		}

		public Map<String, Specimen> getSpecimenNotExpressed() {
			return specimenNotExpressed;
		}

		public Map<String, Specimen> getSpecimenNoTissueAvailable() {
			return specimenNoTissueAvailable;
		}

		public void addSpecimenExpressed(String specimenId, String zygosity) {
			if (!this.getSpecimenExpressed().containsKey(specimenId)) {
				this.getSpecimenExpressed().put(specimenId, new Specimen());
			}
			Specimen specimen = this.getSpecimenExpressed().get(specimenId);
			specimen.setZyg(zygosity);
			this.specimenExpressed.put(specimenId, specimen);
		}

		public void addNotExpressed(String specimenId, String zygosity) {
			if (!this.getSpecimenNotExpressed().containsKey(specimenId)) {
				this.getSpecimenNotExpressed().put(specimenId, new Specimen());
			}
			Specimen specimen = this.getSpecimenNotExpressed().get(specimenId);
			specimen.setZyg(zygosity);
			this.specimenNotExpressed.put(specimenId, specimen);
		}

		public void addNoTissueAvailable(String specimenId, String zygosity) {
			if (!this.getSpecimenNoTissueAvailable().containsKey(specimenId)) {
				this.getSpecimenNoTissueAvailable().put(specimenId, new Specimen());
			}
			Specimen specimen = this.getSpecimenNoTissueAvailable().get(specimenId);
			specimen.setZyg(zygosity);
			this.specimenNoTissueAvailable.put(specimenId, specimen);
		}

		public void addSpecimen(String specimenId, String zygosity) {
			if (!this.getSpecimen().containsKey(specimenId)) {
				this.getSpecimen().put(specimenId, new Specimen());
			}
			Specimen specimen = this.getSpecimen().get(specimenId);
			specimen.setZyg(zygosity);
			this.specimen.put(specimenId, specimen);
		}

		public Map<String, Specimen> getSpecimen() {
			return this.specimen;
		}

	}

	public class Specimen {

		@Override
		public String toString() {
			return "Specimen [zyg=" + zyg + "]";
		}

		private String zyg;

		public String getZyg() {
			return zyg;
		}

		public void setZyg(String zyg) {
			this.zyg = zyg;
		}

	}

	public Map<String, Set<String>> getFacets(String anatomyId)
			throws SolrServerException, IOException, IOException {
				Map<String, Set<String>> res = new HashMap<>();
				SolrQuery query = getBasicExpressionQuery(anatomyId); // only have expressed and
													// not expressed ingnore
													// ambiguous and no tissue
				//query.addFilterQuery("(" + ImageDTO.PARAMETER_ASSOCIATION_VALUE + ":\"no expression\" OR " + ImageDTO.PARAMETER_ASSOCIATION_VALUE
				//		+ ":\"expression\"" + ")");
				query.setFacet(true);
				query.setFacetLimit(-1);
				query.setFacetMinCount(1);
				query.addFacetField(ObservationDTO.ANATOMY_TERM);
				query.addFacetField(ObservationDTO.PHENOTYPING_CENTER);
				query.addFacetField(ObservationDTO.PROCEDURE_NAME);
				query.addFacetField(ObservationDTO.CATEGORY);

				QueryResponse response = experimentSolr.query(query);
				for (FacetField facetField : response.getFacetFields()) {
					Set<String> filter = new TreeSet<>();
					for (Count facet : facetField.getValues()) {
							filter.add(facet.getName());
						
					}
					
					res.put(facetField.getName(), filter);
				}

				return res;
		}

	private SolrQuery getBasicExpressionQuery(String anatomyId) {
		SolrQuery query = new SolrQuery();
		query.setQuery(ObservationDTO.PROCEDURE_NAME + ":*LacZ*");

		if (anatomyId != null) {
			query.addFilterQuery("(" + ObservationDTO.ANATOMY_ID + ":\"" + anatomyId + "\" OR " + ObservationDTO.INTERMEDIATE_ANATOMY_ID + ":\"" + anatomyId + "\" OR "
					+ ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_ID + ":\"" + anatomyId + "\")");
		}
		query.addFilterQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\"experimental\"")
		.addFilterQuery("(" + ObservationDTO.CATEGORY + ":\"no expression\" OR " + ObservationDTO.CATEGORY
				+ ":\"expression\"" + ")");
		return query;
	}
	
	/**
	 * Method that checks the impc images and categorical obeservations for expression data which is either expressed or not expressed.
	 * @param anatomyId
	 * @return
	 * @throws SolrServerException, IOException, IOException
	 */
	public boolean expressionDataAvailable(String anatomyId) throws SolrServerException, IOException, IOException{
		boolean expressionData=false;
		SolrQuery query = getBasicExpressionQuery(anatomyId);
		query.setRows(0);
		QueryResponse response = experimentSolr.query(query);
		if(response.getResults().getNumFound() >0){
			expressionData= true;
		}
		
		//check the impc images core
		if(this.impcImagesHasExpression(anatomyId)){
			expressionData=true;
		}
		return expressionData;
	}

	private boolean impcImagesHasExpression(String anatomyId) throws SolrServerException, IOException, IOException {
		SolrQuery solrQuery=new SolrQuery();
		solrQuery.setQuery(ObservationDTO.PROCEDURE_NAME + ":*LacZ");
		solrQuery.addFilterQuery("(" + ObservationDTO.ANATOMY_ID + ":\"" + anatomyId + "\" OR " + ObservationDTO.INTERMEDIATE_ANATOMY_ID + ":\"" + anatomyId + "\" OR "
				+ ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_ID + ":\"" + anatomyId + "\")");
		if(imagesSolr.query(solrQuery).getResults().getNumFound()>0){
			return true;
		}
		
		return false;
	}
	
	
	
}
