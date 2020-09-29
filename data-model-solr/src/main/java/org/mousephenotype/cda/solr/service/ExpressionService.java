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

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.solr.bean.ExpressionImagesBean;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


@Service
public class ExpressionService extends BasicService {

	private static final Logger log = LoggerFactory.getLogger(ExpressionService.class);

	public static final String SECTION_PARAM_NAME="LacZ images Section";
	public static final String WHOLEMOUNT_PARAM_NAME="LacZ images wholemount";
	
	public static final String SECTION_EMBRYO_LACZ="IMPC_ELZ_063_001";
	public static final String WHOLEOMOUNT_EMBRYO_LACZ="IMPC_ELZ_064_001";

	public final static String SECTION_PARAMETER_STABLE_ID = "IMPC_ALZ_075_001";
	public final static String WHOLEMOUNT_PARAMETER_STABLE_ID = "IMPC_ALZ_076_001";


	private AnatomyService anatomyService;
	private SolrClient     experimentCore;
	private SolrClient     impcImagesCore;
	private ImpressService impressService;
	private ExpressionServiceLacz expressionServiceLacz;

	private Map<String, OntologyBean> abnormalEmapaFromImpress;
	private Map<String, OntologyBean> abnormalMaFromImpress;

	@Inject
	public ExpressionService(
			SolrClient experimentCore,
			SolrClient impcImagesCore,
			AnatomyService anatomyService,
			ExpressionServiceLacz expressionServiceLacz,
			ImpressService impressService)
	{
	    super();
		this.experimentCore = experimentCore;
		this.impcImagesCore = impcImagesCore;
		this.anatomyService = anatomyService;
		this.expressionServiceLacz = expressionServiceLacz;
		this.impressService = impressService;

		initialiseAbnormalOntologyMaps();
	}

	public ExpressionService() {
		super();
	}

	public QueryResponse getExpressionImagesForGeneByAnatomy(String mgiAccession, String anatomy,
			String experimentOrControl, int numberOfImagesToRetrieve, SexType sex, String metadataGroup, String strain)
					throws SolrServerException, IOException  {

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
		QueryResponse response = impcImagesCore.query(solrQuery);
		return response;
	}

	/**
	 *
	 * @param mgiAccession
	 *            if mgi accesion null assume a request for control data
	 * @param fields
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public QueryResponse getExpressionTableDataImages(String mgiAccession, boolean embryo, String... fields)
			throws SolrServerException, IOException  {
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
		solrQuery.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);

		QueryResponse response = experimentCore.query(solrQuery);
		return response;
	}


	private QueryResponse getAdultLaczImageFacetsForGene(String mgiAccession, String parameterStableId, String... fields) throws SolrServerException, IOException  {
		
		//solrQuery.addFilterQuery(ImageDTO.PARAMETER_NAME + ":\"LacZ Images Section\" OR " + ImageDTO.PARAMETER_NAME
		//		+ ":\"LacZ Images Wholemount\"");// reduce the number to image
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:1920455%22&facet=true&facet.field=selected_top_level_ma_term&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		// for embryo data the fields would be like this
		// "parameter_name": "LacZ images section",
		// "procedure_name": "Embryo LacZ",

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
		if(!StringUtils.isEmpty(parameterStableId)){
		solrQuery.addFilterQuery(ImageDTO.PARAMETER_STABLE_ID + ":" +parameterStableId);// reduce the number to image
													// parameters only as we are
													// talking about images not
													// expression data here
		}
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		solrQuery.addFacetField(ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM);
		solrQuery.setRows(Integer.MAX_VALUE);
		QueryResponse response = impcImagesCore.query(solrQuery);
		return response;
	}

	public List<Count> getLaczCategoricalParametersForGene(String mgiAccession, String... fields)
			throws SolrServerException, IOException  {
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:1920455%22&facet=true&facet.field=selected_top_level_ma_term&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		// for embryo data the fields would be like this
		// "parameter_name": "LacZ images section",
		// "procedure_name": "Embryo LacZ",

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");

		solrQuery.addFilterQuery(ImageDTO.PROCEDURE_NAME + ":\"Adult LacZ\"");
		solrQuery.addFilterQuery("-" + ImageDTO.PARAMETER_NAME + ":\"LacZ Images Section\"");
		solrQuery.addFilterQuery("-" + ImageDTO.PARAMETER_NAME + ":\"LacZ Images Wholemount\"");
		solrQuery.addFilterQuery(ObservationDTO.CATEGORY + ":\"expression\"");
		// only look for expresssion at the moment as that is all the anatamogram can  display
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		solrQuery.addFacetField(ImageDTO.PARAMETER_STABLE_ID);
		solrQuery.setRows(0);
		QueryResponse response = experimentCore.query(solrQuery);
		List<FacetField> categoryParameterFields = response.getFacetFields();

		return categoryParameterFields.get(0).getValues();
	}

	private QueryResponse getEmbryoLaczImageFacetsForGene(String mgiAccession, String parameterStableId, String... fields)
			throws SolrServerException, IOException  {
		//solrQuery.addFilterQuery(ImageDTO.PARAMETER_STABLE_ID + ":IMPC_ELZ_064_001" + " OR "
				//+ ImageDTO.PARAMETER_STABLE_ID + ":IMPC_ELZ_063_001");
		// e.g.
		// http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/select?q=gene_accession_id:%22MGI:1920455%22&facet=true&facet.field=selected_top_level_ma_term&fq=(parameter_name:%22LacZ%20Images%20Section%22%20OR%20parameter_name:%22LacZ%20Images%20Wholemount%22)
		// for embryo data the fields would be like this
		// "parameter_name": "LacZ images section",
		// "procedure_name": "Embryo LacZ",

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(ImageDTO.GENE_ACCESSION_ID + ":\"" + mgiAccession + "\"");
		solrQuery.addFilterQuery(ImageDTO.PARAMETER_STABLE_ID + ":"+parameterStableId);// reduce
		// the number to image parameters only as we are talking about images not expression data here
		solrQuery.setFacetMinCount(1);
		solrQuery.setFacet(true);
		solrQuery.setFields(fields);
		solrQuery.addFacetField(ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM);
		solrQuery.setRows(Integer.MAX_VALUE);
		solrQuery.setSort(ObservationDTO.ID, SolrQuery.ORDER.asc);
		QueryResponse response = impcImagesCore.query(solrQuery);
		return response;
	}

	/**
	 *
	 * @param acc
	 *            mgi_accession for gene
	 * @param topMaNameFilter
	 *            Only include images under the top level ma term specified here
	 * @param parameterStableId TODO
	 * @param imagesOverview
	 *            If imagesOverview true then restrict response to only certain
	 *            fields as we are only displaying annotations for a dataset not
	 *            a specific thumbnail
	 * @param imagesOverview
	 *            If true we want some images data/stats added to the model for
	 *            display in the tabbed pane on the gene page.
	 * @throws SolrServerException, IOException
	 * @throws SQLException
	 */
	public ExpressionImagesBean  getLacImageDataForGene(String acc, String topMaNameFilter, String parameterStableId, boolean imagesOverview) throws SolrServerException, IOException  {

		QueryResponse laczResponse = null;
		String noTopTermId = "";
		String topLevelField = "";// type ma or emap imageDTO field for top
									// level terms
		String termIdField = "";
		if (parameterStableId!=null && parameterStableId.contains("ELZ")) { // use EMAP terms and top level terms
			noTopTermId = "TS20 embryo or Unassigned";// currently if unassigned they either have embryo TS20 as there EMAP id but our system doesn't find any selected_top_level emap or nothing is assigned but we know they are embryo so assign this id to unassigned
			topLevelField = ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM;
			termIdField = ImageDTO.ANATOMY_ID;
			if (imagesOverview) {
				laczResponse = getEmbryoLaczImageFacetsForGene(acc, parameterStableId, ImageDTO.OMERO_ID, ImageDTO.JPEG_URL, ImageDTO.THUMBNAIL_URL,
						topLevelField, ImageDTO.PARAMETER_ASSOCIATION_NAME, ImageDTO.PARAMETER_ASSOCIATION_VALUE,
						ImageDTO.ANATOMY_ID, ImageDTO.ANATOMY_TERM);
			} else {
				laczResponse = getEmbryoLaczImageFacetsForGene(acc, parameterStableId, ImageDTO.OMERO_ID, ImageDTO.JPEG_URL, ImageDTO.THUMBNAIL_URL,
						topLevelField, ImageDTO.PARAMETER_ASSOCIATION_NAME, ImageDTO.PARAMETER_ASSOCIATION_VALUE,
						ImageDTO.ZYGOSITY, ImageDTO.SEX, ImageDTO.ALLELE_SYMBOL, ImageDTO.DOWNLOAD_URL,
						ImageDTO.IMAGE_LINK, ImageDTO.ANATOMY_ID, ImageDTO.ANATOMY_TERM);
			}

		} else {
			noTopTermId = "Unassigned Top Level MA";
			topLevelField = ImageDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM;
			termIdField = ImageDTO.ANATOMY_ID;
			if (imagesOverview) {
				laczResponse = getAdultLaczImageFacetsForGene(acc,parameterStableId, ImageDTO.OMERO_ID, ImageDTO.JPEG_URL, ImageDTO.THUMBNAIL_URL, topLevelField,
						ImageDTO.PARAMETER_ASSOCIATION_NAME, ImageDTO.PARAMETER_ASSOCIATION_VALUE, ImageDTO.ANATOMY_ID,
						ImageDTO.UBERON_ID, ImageDTO.EFO_ID);
			} else {
				laczResponse = getAdultLaczImageFacetsForGene(acc, parameterStableId,   ImageDTO.OMERO_ID, ImageDTO.JPEG_URL,  ImageDTO.THUMBNAIL_URL, topLevelField,
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
		Map<String, Boolean> haveImpcImages = new HashMap<>();
		expFacetToDocs.put(noTopTermId, new SolrDocumentList());


		for (SolrDocument doc : imagesResponse) {
			List<String> tops = getListFromCollection(doc.getFieldValues(topLevelField));

			// work out list of uberon/efo ids with/without expressions
			// noTopLevelCount.setCount(c);
			if (tops.isEmpty()) {// if no top level found this image then add it to the "No top level" term docs so we can display orphaned terms and images
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
		
		for(Count count: filteredTopLevelAnatomyTerms){
			Boolean hasImages=false;
			if(count.getCount()>0){
				hasImages=true;
			}
			haveImpcImages.put(count.getName(),hasImages );
			
		}

		ImageServiceUtil.sortHigherLevelTermCountsAlphabetically(filteredTopLevelAnatomyTerms);
		ImageServiceUtil.sortDocsByExpressionAlphabetically(expFacetToDocs);

		ExpressionImagesBean bean=null;
//		if (parameterStableId.contains("EZ")) {//embryo
			
//			model.addAttribute("impcEmbryoExpressionImageFacets", filteredTopLevelAnatomyTerms);
//			model.addAttribute("haveImpcEmbryoImages", haveImpcImages);
//			model.addAttribute("impcEmbryoExpressionFacetToDocs", expFacetToDocs);
			bean=new ExpressionImagesBean(filteredTopLevelAnatomyTerms, haveImpcImages, expFacetToDocs );
//		} else {
//			model.addAttribute("impcAdultExpressionImageFacets", filteredTopLevelAnatomyTerms);
//			model.addAttribute("haveImpcAdultImages", haveImpcImages);
//			model.addAttribute("impcAdultExpressionFacetToDocs", expFacetToDocs);
//		}
		// }
			return bean;
	}

	/**
	 * 
	 * @param anatomogramDataBeans
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public Map<String, Long> getLacSelectedTopLevelMaCountsForAnatomogram(
			List<AnatomogramDataBean> anatomogramDataBeans) throws SolrServerException, IOException  {
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
	 * @throws SolrServerException, IOException
	 */
	public Set<String> getLacSelectedTopLevelMaIdsForAnatomogram(
			List<AnatomogramDataBean> anatomogramDataBeans) throws SolrServerException, IOException  {
		Set<String> topLevelMaToCountMap = new HashSet<>();
		for (AnatomogramDataBean bean : anatomogramDataBeans) {
			for (String topMaId : bean.getTopLevelMaIds()) {
					topLevelMaToCountMap.add(topMaId);
			}
		}

		return topLevelMaToCountMap;
	}

	public List<AnatomogramDataBean> getAnatomogramDataBeans(List<Count> parameterCounts) throws SolrServerException, IOException  {
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

	/**
	 *
	 * @param acc
	 *            mgi_accession for gene
	 * @param model
	 *            Spring MVC model
	 * @throws SolrServerException, IOException
	 */
	public Model getExpressionDataForGene(String acc, Model model, boolean embryo) throws SolrServerException, IOException  {

		QueryResponse laczDataResponse = expressionServiceLacz.getCategoricalAdultLacZData(acc, embryo, ImageDTO.ZYGOSITY,
				ImageDTO.EXTERNAL_SAMPLE_ID, ObservationDTO.OBSERVATION_TYPE, ObservationDTO.PARAMETER_STABLE_ID,
				ObservationDTO.PARAMETER_NAME, ObservationDTO.CATEGORY, ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList mutantCategoricalAdultLacZData = laczDataResponse.getResults();
		Map<String, SolrDocumentList> expressionAnatomyToDocs = getAnatomyToDocsForCategorical(
				mutantCategoricalAdultLacZData);
		Map<String, ExpressionRowBean> expressionAnatomyToRow = new TreeMap<>();
		Map<String, ExpressionRowBean> wtAnatomyToRow = new TreeMap<>();

		QueryResponse wtLaczDataResponse = expressionServiceLacz.getCategoricalAdultLacZData(null, embryo, ImageDTO.ZYGOSITY,
				ImageDTO.EXTERNAL_SAMPLE_ID, ObservationDTO.OBSERVATION_TYPE, ObservationDTO.PARAMETER_NAME,
				ObservationDTO.CATEGORY, ObservationDTO.BIOLOGICAL_SAMPLE_GROUP);
		SolrDocumentList wtCategoricalAdultLacZData = wtLaczDataResponse.getResults();
		Map<String, SolrDocumentList> wtAnatomyToDocs = getAnatomyToDocsForCategorical(wtCategoricalAdultLacZData);

		QueryResponse laczImagesResponse = null;

		laczImagesResponse = getExpressionTableDataImages(acc, embryo, ImageDTO.ZYGOSITY,
				ImageDTO.PARAMETER_ASSOCIATION_NAME, ImageDTO.PARAMETER_NAME, ObservationDTO.OBSERVATION_TYPE,
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


	/**
	 * @author ilinca
	 * @since 2016/07/08
	 * @param anatomyId
	 * @return List of gene ids with positive expression in given anatomy term. 
	 * @throws SolrServerException, IOException
	 */
	public List<String> getGenesWithExpression(String anatomyId) throws SolrServerException, IOException {
		
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
		
		for (Count value: experimentCore.query(q).getFacetFields().get(0).getValues()){
			geneIds.add(value.getName());
		}
		return geneIds;
		
	}

	private void initialiseAbnormalOntologyMaps() {
		abnormalMaFromImpress = impressService.getParameterStableIdToAbnormalMaMap();
		abnormalEmapaFromImpress = impressService.getParameterStableIdToAbnormalEmapaMap();
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
							log.debug("no anatomy id for param id: " + parameterStableId);
						}
					}
					row = getExpressionCountForAnatomyTerm(anatomy, row, doc);
				} else if (doc.get(ObservationDTO.OBSERVATION_TYPE).equals("image_record")
						&& doc.get(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP).equals("experimental")) {// assume
																									// image
																									// with
																									// parameterAssociation
					
					//seperate images based on wholemount or section
					//System.out.println("parameter_name="+doc);
					row = homImages(row, doc);
					String paramName=(String)doc.get(ObservationDTO.PARAMETER_NAME);
					if(paramName.equalsIgnoreCase(WHOLEMOUNT_PARAM_NAME)){
						row.setWholemountImagesAvailable(true);
					}
					if(paramName.equalsIgnoreCase(SECTION_PARAM_NAME)){
						row.setSectionImagesAvailable(true);
					}
					//for embryo data
					if(paramName.equalsIgnoreCase(WHOLEOMOUNT_EMBRYO_LACZ)){
						row.setWholemountImagesAvailable(true);
					}
					if(paramName.equalsIgnoreCase(SECTION_EMBRYO_LACZ)){
						row.setSectionImagesAvailable(true);
					}
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
			if (row.getSpecimenImageOnly().keySet().size() > 0) {
				row.setImageOnly(true);
			}
			if (row.getSpecimenAmbiguous().keySet().size() > 0) {
				row.setAmbiguous(true);
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
				else if (paramAssValue.equalsIgnoreCase("imageOnly")) {
					row.addImageOnly(sampleId, zyg);

				}
				else if (paramAssValue.equalsIgnoreCase("ambiguous")) {
					row.addSpecimenAmbiguous(sampleId, zyg);

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
					anatomy = anatomy.toLowerCase();

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
				anatomy = anatomy.toLowerCase();
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

		

		public void setAmbiguous(boolean b) {
			this.ambiguous=b;
		}



		public Map<String, Specimen> getSpecimenAmbiguous() {
			return this.specimenAmbiguous;
			
		}
		
		public void addSpecimenAmbiguous(String specimenId, String zyg) {
			if (!this.getSpecimenAmbiguous().containsKey(specimenId)) {
				this.getSpecimenAmbiguous().put(specimenId, new Specimen());
			}
			Specimen specimen = this.getSpecimenAmbiguous().get(specimenId);
			specimen.setZyg(zyg);
			this.specimenAmbiguous.put(specimenId, specimen);
			
		}

		public void addImageOnly(String specimenId, String zyg) {
			if (!this.getSpecimenImageOnly().containsKey(specimenId)) {
				this.getSpecimenImageOnly().put(specimenId, new Specimen());
			}
			Specimen specimen = this.getSpecimenImageOnly().get(specimenId);
			specimen.setZyg(zyg);
			this.specimenImageOnly.put(specimenId, specimen);
			
		}

		private Map<String, Specimen> getSpecimenImageOnly() {
			return this.specimenImageOnly;
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
		private boolean imageOnly=false;
		private boolean ambiguous=false;

		public boolean isAmbiguous() {
			return ambiguous;
		}



		public boolean isImageOnly() {
			return imageOnly;
		}

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
		
		public void setImageOnly(boolean imageOnly) {
			this.imageOnly = imageOnly;
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
		
		private boolean sectionImagesAvailable=false;
		public boolean isSectionImagesAvailable() {
			return sectionImagesAvailable;
		}

		public void setSectionImagesAvailable(boolean sectionImagesAvailable) {
			this.sectionImagesAvailable = sectionImagesAvailable;
		}

		private boolean wholemountImagesAvailable=false;
		
		

		public boolean isWholemountImagesAvailable() {
			return wholemountImagesAvailable;
		}

		public void setWholemountImagesAvailable(boolean wholemountImagesAvailable) {
			this.wholemountImagesAvailable = wholemountImagesAvailable;
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
		private Map<String, Specimen> specimenNotExpressed = new HashMap<>();
		private Map<String, Specimen> specimenNoTissueAvailable = new HashMap<>();
		private Map<String, Specimen> specimenAmbiguous = new HashMap<>();
		private Map<String, Specimen> specimenImageOnly = new HashMap<>();

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
			throws SolrServerException, IOException  {
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

				QueryResponse response = experimentCore.query(query);
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
	 * @throws SolrServerException, IOException
	 */
	public boolean expressionDataAvailable(String anatomyId) throws SolrServerException, IOException {
		boolean expressionData=false;
		SolrQuery query = getBasicExpressionQuery(anatomyId);
		query.setRows(0);
		QueryResponse response = experimentCore.query(query);
		if(response.getResults().getNumFound() >0){
			expressionData= true;
		}
		
		//check the impc images core
		if(this.impcImagesHasExpression(anatomyId)){
			expressionData=true;
		}
		return expressionData;
	}

	private boolean impcImagesHasExpression(String anatomyId) throws SolrServerException, IOException  {
		SolrQuery solrQuery=new SolrQuery();
		solrQuery.setQuery(ObservationDTO.PROCEDURE_NAME + ":*LacZ");
		solrQuery.addFilterQuery("(" + ObservationDTO.ANATOMY_ID + ":\"" + anatomyId + "\" OR " + ObservationDTO.INTERMEDIATE_ANATOMY_ID + ":\"" + anatomyId + "\" OR "
				+ ObservationDTO.SELECTED_TOP_LEVEL_ANATOMY_ID + ":\"" + anatomyId + "\")");
		if(impcImagesCore.query(solrQuery).getResults().getNumFound()>0){
			return true;
		}
		
		return false;
	}

	/**
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public List<ObservationDTO> getCategoricalAdultLacZDataForReport() throws SolrServerException, IOException  {
		SolrQuery solrQuery = new SolrQuery()
		.setQuery(ObservationDTO.BIOLOGICAL_SAMPLE_GROUP + ":\"" + "experimental" + "\"")
		.addFilterQuery(ImageDTO.PROCEDURE_NAME + ":\"Adult LacZ\"")
		.addFilterQuery(ObservationDTO.OBSERVATION_TYPE + ":\"categorical\"")
		.setFields(
			ObservationDTO.GENE_SYMBOL,
			ObservationDTO.GENE_ACCESSION_ID,
			ObservationDTO.ALLELE_SYMBOL,
			ObservationDTO.ALLELE_ACCESSION_ID,
			ObservationDTO.STRAIN_NAME,
			ObservationDTO.STRAIN_ACCESSION_ID,
			ObservationDTO.CATEGORY,
			ObservationDTO.COLONY_ID,
			ObservationDTO.EXTERNAL_SAMPLE_ID,
			ObservationDTO.ZYGOSITY,
			ObservationDTO.SEX,
			ObservationDTO.PARAMETER_NAME,
			ObservationDTO.PARAMETER_STABLE_ID,
			ObservationDTO.GENE_ACCESSION_ID,
			ObservationDTO.PHENOTYPING_CENTER)
		.setRows(Integer.MAX_VALUE);

		return experimentCore.query(solrQuery).getBeans(ObservationDTO.class);
	}
}