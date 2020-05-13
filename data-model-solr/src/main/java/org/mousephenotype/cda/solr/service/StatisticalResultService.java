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

import com.google.common.collect.Iterators;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.EmpiricalDistribution;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.*;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.mousephenotype.cda.constants.OverviewChartsConstants;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.db.repositories.*;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.generic.util.GeneRowForHeatMap3IComparator;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.solr.web.dto.*;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@Named("statistical-result-service")
public class StatisticalResultService extends GenotypePhenotypeService implements WebStatus {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	protected BiologicalModelRepository biologicalModelRepository;
	protected DatasourceRepository      datasourceRepository;
	protected OrganisationRepository    organisationRepository;
	protected ParameterRepository       parameterRepository;
	protected PipelineRepository        pipelineRepository;
	protected SolrClient                statisticalResultCore;


	@Inject
	public StatisticalResultService(
			@NotNull ImpressService impressService,
			@NotNull SolrClient genotypePhenotypeCore,
			@NotNull GenesSecondaryProjectRepository genesSecondaryProjectRepository,
			@NotNull BiologicalModelRepository biologicalModelRepository,
			@NotNull DatasourceRepository datasourceRepository,
			@NotNull OrganisationRepository organisationRepository,
			@NotNull ParameterRepository parameterRepository,
			@NotNull PipelineRepository pipelineRepository,
			@NotNull SolrClient statisticalResultCore)
	{
		super(impressService, genotypePhenotypeCore, genesSecondaryProjectRepository);
		this.biologicalModelRepository = biologicalModelRepository;
		this.datasourceRepository = datasourceRepository;
		this.organisationRepository = organisationRepository;
		this.parameterRepository= parameterRepository;
		this.pipelineRepository = pipelineRepository;
		this.statisticalResultCore = statisticalResultCore;
	}


	public static Double getFemalePercentageChange(String token) {
		Double retVal = null;

		List<String> sexes = Arrays.asList(token.split(","));
		for (String sex : sexes) {
			if (sex.contains("Female")) {
				try {
					String[] pieces = sex.split(":");
					retVal = Double.parseDouble(pieces[1].replaceAll("%", ""));
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					// null statement;
				}
				break;
			}
		}

		return retVal;
	}

	public Set<String> getChartPivots(String baseUrl, String acc, String parameterStableId, List<String> pipelineStableIds, List<String> zyList, List<String> phenotypingCentersList,
						  List<String> strainsParams, List<String> metaDataGroup, List<String> alleleAccession) throws IOException, SolrServerException, URISyntaxException {

		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		if (acc != null){
			query.addFilterQuery(StatisticalResultDTO.MARKER_ACCESSION_ID + ":\"" + acc + "\"");
		}
		if ((parameterStableId != null) && ( ! parameterStableId.trim().isEmpty())) {
			query.addFilterQuery(StatisticalResultDTO.PARAMETER_STABLE_ID + ":" + parameterStableId);
		}
		if (pipelineStableIds != null & pipelineStableIds.size() > 0){
			query.addFilterQuery(pipelineStableIds.stream().collect(Collectors.joining(" OR ",  StatisticalResultDTO.PIPELINE_STABLE_ID + ":(", ")")));
		}
		if (zyList != null && zyList.size() > 0){
			query.addFilterQuery(zyList.stream().collect(Collectors.joining(" OR ",  StatisticalResultDTO.ZYGOSITY + ":(", ")")));
		}
		if (phenotypingCentersList != null && phenotypingCentersList.size() > 0){
			query.addFilterQuery(phenotypingCentersList.stream().collect(Collectors.joining("\" OR \"",  StatisticalResultDTO.PHENOTYPING_CENTER + ":(\"", "\")")));
		}
		if (strainsParams != null && strainsParams.size() > 0){
			query.addFilterQuery(strainsParams.stream().collect(Collectors.joining("\" OR \"", StatisticalResultDTO.STRAIN_ACCESSION_ID + ":(\"", "\")")));
		}
		if (metaDataGroup != null && metaDataGroup.size() > 0){
			query.addFilterQuery(metaDataGroup.stream().collect(Collectors.joining("\" OR \"",  StatisticalResultDTO.METADATA_GROUP + ":(\"", "\")")));
		}
		if (alleleAccession != null && alleleAccession.size() > 0){
			query.addFilterQuery(alleleAccession.stream().collect(Collectors.joining("\" OR \"", StatisticalResultDTO.ALLELE_ACCESSION_ID + ":(\"", "\")")));
		}
		query.setFacet(true);

		// If you add/change order of pivots, make sure you do the same in the for loops below
		String pivotFacet = StatisticalResultDTO.PIPELINE_STABLE_ID + "," +
				StatisticalResultDTO.ZYGOSITY + "," +
				StatisticalResultDTO.PHENOTYPING_CENTER + "," +
				StatisticalResultDTO.STRAIN_ACCESSION_ID + "," +
				StatisticalResultDTO.ALLELE_ACCESSION_ID;
		//pivot needs to have metadata_group irrespective of if it's included in filter or not as we want seperate experiments based on the metadata
			pivotFacet += "," + StatisticalResultDTO.METADATA_GROUP;

		//}
		query.add("facet.pivot", pivotFacet );

		query.setFacetLimit(-1);

		Set<String> resultParametersForCharts = new HashSet<>();
		System.out.println("SR facet pivot query="+query);
		NamedList<List<PivotField>> facetPivot = statisticalResultCore.query(query).getFacetPivot();
		for( PivotField pivot : facetPivot.get(pivotFacet)){
			getParametersForChartFromPivot(pivot, baseUrl, resultParametersForCharts);
		}

		return resultParametersForCharts;
	}


	private Set<String> getParametersForChartFromPivot(PivotField pivot, String urlParams, Set<String> set){

		if ( pivot != null){
			if(pivot.getValue().toString().length()>0) {//if the value is not set don't add an empty string parameter as this will then fail with the metadata_group being set to empty string!!
			urlParams += pivot.getField() + "=" + pivot.getValue().toString() + "&";
			}
			if (pivot.getPivot() != null) {
				for (PivotField p : pivot.getPivot()) {
					getParametersForChartFromPivot(p, urlParams, set);
				}
			} else {
				set.add(urlParams);
			}
		}

		return set;

	}

	public static Double getMalePercentageChange(String token) {
		Double retVal = null;

		List<String> sexes = Arrays.asList(token.split(","));
		for (String sex : sexes) {
			if (sex.contains("Male")) {
				try {
					String[] pieces = sex.split(":");
					retVal = Double.parseDouble(pieces[1].replaceAll("%", ""));
				} catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
					// null statement;
				}
				break;
			}
		}

		return retVal;
	}

	/**
	 * @author ilinca
	 * @since 2016/07/05
	 * @param anatomyId
	 * @return Number of genes in s-r core for anatomy term given. 
	 * @throws SolrServerException, IOException
	 */
	public Integer getGenesByAnatomy(String anatomyId)
			throws SolrServerException, IOException, JSONException {

		SolrQuery query = new SolrQuery();
		query.setQuery("(" + StatisticalResultDTO.ANATOMY_TERM_ID + ":\"" + anatomyId + "\" OR " +
				StatisticalResultDTO.INTERMEDIATE_ANATOMY_TERM_ID + ":\"" + anatomyId + "\" OR " +
				StatisticalResultDTO.TOP_LEVEL_ANATOMY_TERM_ID + ":\"" + anatomyId + "\")")
				.setRows(0)
				.add("group", "true")
				.add("group.field", StatisticalResultDTO.MARKER_ACCESSION_ID)
				.add("group.ngroups", "true")
				.add("wt","json");
		JSONObject groups = new JSONObject(statisticalResultCore.query(query).getResponse().get("grouped").toString().replaceAll("=", ":"));

		return groups.getJSONObject(StatisticalResultDTO.MARKER_ACCESSION_ID).getInt("ngroups");

	}


	public Map<String, Long> getColoniesNoMPHit(List<String> resourceName, ZygosityType zygosity)
			throws SolrServerException, IOException {

		Map<String, Long>  res = new HashMap<>();
		SolrQuery q = new SolrQuery();

		if (resourceName != null){
			q.setQuery(GenotypePhenotypeDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + GenotypePhenotypeDTO.RESOURCE_NAME + ":"));
		}else {
			q.setQuery("*:*");
		}

		if (zygosity != null){
			q.addFilterQuery(GenotypePhenotypeDTO.ZYGOSITY + ":" + zygosity.name());
		}

		q.addFilterQuery(GenotypePhenotypeDTO.P_VALUE+ ":[" + StatisticalResultService.P_VALUE_THRESHOLD + " TO 1]");

		q.addFacetField(StatisticalResultDTO.COLONY_ID);
		q.setFacetMinCount(1);
		q.setFacet(true);
		q.setRows(1);
		q.set("facet.limit", -1);

		logger.info("Solr url for getColoniesNoMPHit " + SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + q);
		QueryResponse response = statisticalResultCore.query(q);

		for( Count facet : response.getFacetField(StatisticalResultDTO.COLONY_ID).getValues()){
			res.put(facet.getName(), facet.getCount());
		}

		return res;
	}


	/**
	 *
	 * @param geneAccessionId
	 * @return Basic information for allele pages in an AllelePageDTO
	 */
	public AllelePageDTO getAllelesInfo(String geneAccessionId, List<String> alleleSymbol, List<String> phenotypingCenter,
										List<String> pipelineName, List<String> procedureStableId, List<String> procedureName, List<String> mpTermIds, ArrayList<String> resource){

		AllelePageDTO dto = new AllelePageDTO();
		SolrQuery q = buildQuery(geneAccessionId, procedureName, alleleSymbol, phenotypingCenter, pipelineName, procedureStableId, resource, mpTermIds, null, null, null, null, null, null, null, null);

		q.addField(StatisticalResultDTO.MARKER_SYMBOL);
		q.addField(StatisticalResultDTO.MARKER_ACCESSION_ID);
		q.addFilterQuery(StatisticalResultDTO.P_VALUE + ":[* TO *]");
		q.setFacet(true);
		q.setFacetLimit(-1);
		q.setFacetMinCount(1);
		q.addFacetField(StatisticalResultDTO.PHENOTYPING_CENTER);
		q.addFacetField(StatisticalResultDTO.PIPELINE_NAME);
		q.addFacetField(StatisticalResultDTO.ALLELE_SYMBOL);
		q.setRows(1);

		String pivotFacet =  StatisticalResultDTO.PROCEDURE_NAME  + "," + StatisticalResultDTO.PARAMETER_STABLE_ID;
		q.set("facet.pivot", pivotFacet);
		q.set("facet.pivot.mincount", 1);

		try {
			QueryResponse res = statisticalResultCore.query(q);

			FacetField phenotypingCenters = res.getFacetField(StatisticalResultDTO.PHENOTYPING_CENTER);

			for (Count facet : phenotypingCenters.getValues()){
				dto.addPhenotypingCenter(facet.getName());
			}

			FacetField alleles = statisticalResultCore.query(q).getFacetField(StatisticalResultDTO.ALLELE_SYMBOL);
			for (Count facet : alleles.getValues()){
				dto.addAlleleSymbol(facet.getName());
			}

			FacetField pipelines = statisticalResultCore.query(q).getFacetField(StatisticalResultDTO.PIPELINE_NAME);
			for (Count facet : pipelines.getValues()){
				dto.addPipelineName(facet.getName());
			}

			for( PivotField pivot : res.getFacetPivot().get(pivotFacet)){
				if (pivot.getPivot() != null){
					List<String> lst = new ArrayList<>();
					for (PivotField gene : pivot.getPivot()){
						lst.add(gene.getValue().toString());
					}
					dto.addParametersByProcedure(pivot.getValue().toString(), new ArrayList<>(lst));
					dto.addProcedureNames(pivot.getValue().toString());
				}
			}

			if (res.getResults().size() > 0) {
				SolrDocument doc = res.getResults().get(0);
				dto.setGeneSymbol(doc.getFieldValue(StatisticalResultDTO.MARKER_SYMBOL).toString());
				dto.setGeneAccession(geneAccessionId);
			} else {
				logger.info("StatisticalResultService.getAllelesInfo(...) - No results from solr query " + ((HttpSolrClient)statisticalResultCore).getBaseURL() + "/" + q);
			}

		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}

		return dto;

	}

	public List<String> getCenters(String pipelineStableId, String observationType, String resource, String status)
			throws SolrServerException, IOException {

		List<String> res = new ArrayList<>();

		SolrQuery query = new SolrQuery().setQuery("*:*");
		query.set("facet", true);
		query.set("facet.field", StatisticalResultDTO.PHENOTYPING_CENTER);
		query.setRows(0);
		query.set("facet.limit", -1);
		query.set("facet.mincount", 1);

		if (pipelineStableId != null){
			query.addFilterQuery(StatisticalResultDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId);
		}
		if (observationType != null){
			query.addFilterQuery(StatisticalResultDTO.DATA_TYPE + ":" + observationType);
		}
		if (resource != null){
			query.addFilterQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + resource);
		}
		if (status != null){
			query.addFilterQuery(StatisticalResultDTO.STATUS + ":" + status);
		}

		QueryResponse response = statisticalResultCore.query(query);

		for (  Count facet: response.getFacetField(StatisticalResultDTO.PHENOTYPING_CENTER).getValues()){
			res.add(facet.getName());
		}

		return res;
	}


	/**
	 * @author tudose
	 * @since 2015/07/21
	 * @return List<ProcedureBean>
	 */
	public List<ImpressBaseDTO> getProcedures(String pipelineStableId, String observationType, String resource, Integer minParameterNumber, List<String> proceduresToSkip, String status, boolean includeWilcoxon){

		List<ImpressBaseDTO> procedures = new ArrayList<>();

		try {
			SolrQuery query = new SolrQuery()
					.setQuery("*:*")
					.addField(StatisticalResultDTO.PROCEDURE_ID)
					.addField(StatisticalResultDTO.PROCEDURE_NAME)
					.addField(StatisticalResultDTO.PROCEDURE_STABLE_ID);
			query.set("group", true);
			query.set("group.field", StatisticalResultDTO.PROCEDURE_NAME);
			query.setRows(10000);
			query.setSort(StatisticalResultDTO.DOCUMENT_ID, SolrQuery.ORDER.asc);
			query.set("group.limit", 1);
			if (!includeWilcoxon){
				query.addFilterQuery("-" + StatisticalResultDTO.STATISTICAL_METHOD + ":Wilcoxon*");
			}
			if (pipelineStableId != null){
				query.addFilterQuery(StatisticalResultDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId);
			}
			if (observationType != null){
				query.addFilterQuery(StatisticalResultDTO.DATA_TYPE + ":" + observationType);
			}
			if (resource != null){
				query.addFilterQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + resource);
			}
			if (status != null){
				query.addFilterQuery(StatisticalResultDTO.STATUS + ":" + status);
			}
			String pivotField = StatisticalResultDTO.PROCEDURE_NAME + "," + StatisticalResultDTO.PARAMETER_NAME;

			if (minParameterNumber != null && minParameterNumber > 0){

				query.setFacet(true);
				query.setFacetMinCount(1);
				query.set("facet.pivot.mincount", minParameterNumber);
				query.set("facet.pivot", pivotField);

			}

			QueryResponse response = statisticalResultCore.query(query);

			for ( Group group: response.getGroupResponse().getValues().get(0).getValues()){

				ImpressBaseDTO procedure = new ImpressBaseDTO(Long.getLong(group.getResult().get(0).getFirstValue(StatisticalResultDTO.PROCEDURE_ID).toString()),
						null,
						group.getResult().get(0).getFirstValue(StatisticalResultDTO.PROCEDURE_STABLE_ID ).toString(),
						group.getResult().get(0).getFirstValue(StatisticalResultDTO.PROCEDURE_NAME).toString());
				procedures.add(procedure);
			}

			if (minParameterNumber != null && minParameterNumber > 0){
				// get procedureList with more than minParameterNumber
				// remove from procedures the ones not in procedureList
				List<Map<String, String>> res = getFacetPivotResults(response, false);
				HashSet<String> proceduresWithMinCount = new HashSet<>();

				for (Map<String, String> pivot : res ){
					proceduresWithMinCount.addAll(pivot.values());
				}

				List<ImpressBaseDTO> proceduresToReturn = new ArrayList<>();

				for (ImpressBaseDTO proc : procedures){
					if (proceduresWithMinCount.contains(proc.getName())){
						if (proceduresToSkip != null && !proceduresToSkip.contains(proc.getStableId()) || proceduresToSkip == null ){
							proceduresToReturn.add(proc);
						}
					}
				}
				procedures = proceduresToReturn;

			}

		} catch (SolrServerException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}

		return procedures;
	}


	public TreeMap<String, ParallelCoordinatesDTO> getGenotypeEffectFor(List<String> procedureStableId,
																		List<String> phenotypingCenters, Boolean requiredParamsOnly,
																		String baseUrl, List<String> genes, String topLevelMpId)
			throws SolrServerException, IOException, URISyntaxException{

		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		if (procedureStableId != null) {
			query.addFilterQuery(StatisticalResultDTO.PROCEDURE_STABLE_ID + ":" + StringUtils.join(procedureStableId, "* OR " + StatisticalResultDTO.PROCEDURE_STABLE_ID + ":") + "*");
		}
		query.addFilterQuery(StatisticalResultDTO.DATA_TYPE + ":unidimensional");
		query.setFacet(true);
		query.setFacetMinCount(1);
		query.setFacetLimit(-1);
		query.addFacetField(StatisticalResultDTO.PARAMETER_STABLE_ID);
		query.addFacetField(StatisticalResultDTO.PARAMETER_NAME);

		if (phenotypingCenters != null && phenotypingCenters.size() > 0){
			query.addFilterQuery(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(phenotypingCenters, "\" OR " + StatisticalResultDTO.PHENOTYPING_CENTER + ":\"") + "\"");
		}

		List<String> parameterStableIds = new ArrayList<>(getFacets(statisticalResultCore.query(query)).get(StatisticalResultDTO.PARAMETER_STABLE_ID).keySet());
		TreeSet<ParameterDTO> parameterUniqueByStableId = new TreeSet<>(ParameterDTO.getComparatorByName());

		for (ParameterDTO param : impressService.getParameters(procedureStableId, "unidimensional", topLevelMpId)){
			if (parameterStableIds.contains(param.getStableId()) && (param.isRequired() || !requiredParamsOnly) && !parameterUniqueByStableId.contains(param)){
				parameterUniqueByStableId.add(param);
			}
		}

		List<ParameterDTO> parameters = new ArrayList<>(parameterUniqueByStableId);
		Map<String, ParameterDTO> parameterMap = new HashMap<>();
		for (ParameterDTO p: parameterUniqueByStableId){
			parameterMap.put(p.getStableId(), p);
		}


		query = new SolrQuery();
		query.setQuery("-" + StatisticalResultDTO.STATISTICAL_METHOD + ":Wilcoxon*"); // Decided to omit Wilcoxon because it does not adjust for batch or center effect and the value for genotyope effect does not have the same meaning as for the other values.
		query.addFilterQuery(StatisticalResultDTO.PARAMETER_STABLE_ID + ":\"" +
				StringUtils.join(parameters.stream().map(ParameterDTO::getStableId).collect(Collectors.toList()), "\" OR " + StatisticalResultDTO.PARAMETER_STABLE_ID + ":\"") + "\"");
		query.addFilterQuery(StatisticalResultDTO.STATUS + ":Success");

		query.addField(StatisticalResultDTO.GENOTYPE_EFFECT_PARAMETER_ESTIMATE);
		query.addField(StatisticalResultDTO.MARKER_ACCESSION_ID);
		query.addField(StatisticalResultDTO.PARAMETER_STABLE_ID);
		query.addField(StatisticalResultDTO.FEMALE_KO_PARAMETER_ESTIMATE);
		query.addField(StatisticalResultDTO.MALE_KO_PARAMETER_ESTIMATE);
		query.addField(StatisticalResultDTO.PHENOTYPING_CENTER);
		query.addField(StatisticalResultDTO.PROCEDURE_NAME);
		query.addField(StatisticalResultDTO.MARKER_SYMBOL);
		query.addField(StatisticalResultDTO.SIGNIFICANT);
		query.setRows(Integer.MAX_VALUE);
		query.setSort(StatisticalResultDTO.DOCUMENT_ID, SolrQuery.ORDER.asc);

		if (phenotypingCenters != null && phenotypingCenters.size() > 0){
			query.addFilterQuery(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(phenotypingCenters, "\" OR " + StatisticalResultDTO.PHENOTYPING_CENTER + ":\"") + "\"");
		}

		if (genes != null){
			query.addFilterQuery(StatisticalResultDTO.MARKER_SYMBOL + ":(\"" + genes.stream().collect(Collectors.joining("\" OR \"")) + "\")");
		}

		List<StatisticalResultDTO> result = statisticalResultCore.query(query, SolrRequest.METHOD.POST).getBeans(StatisticalResultDTO.class);
		TreeMap<String, ParallelCoordinatesDTO> row = addMaxGenotypeEffects(result, parameterMap, baseUrl);
		row = addMeanValues(row, parameters);
		row = addDefaultValues(row, parameters); // add normal/no effect values after mean so that they're not used in the computation

		return row;

	}

	private Comparator<String> getParallelCoordsComparator(){
		return (o1, o2) -> {
			if ((o1.equals(ParallelCoordinatesDTO.DEFAULT) || o1.equals(ParallelCoordinatesDTO.MEAN)) && !o2.equals(ParallelCoordinatesDTO.DEFAULT) && !o2.equals(ParallelCoordinatesDTO.MEAN)){
				return 1;
			} else  if ((o2.equals(ParallelCoordinatesDTO.DEFAULT) || o2.equals(ParallelCoordinatesDTO.MEAN)) && !o1.equals(ParallelCoordinatesDTO.DEFAULT) && !o1.equals(ParallelCoordinatesDTO.MEAN)){
				return -1;
			} else {
				return o1.compareTo(o2);
			}
		};
	}


	private TreeMap<String, ParallelCoordinatesDTO> addDefaultValues(TreeMap<String, ParallelCoordinatesDTO> beans, List<ParameterDTO> allParameterNames) {

		ParallelCoordinatesDTO currentBean = new ParallelCoordinatesDTO(ParallelCoordinatesDTO.DEFAULT, null, "No effect", allParameterNames);

		for (ParameterDTO param : allParameterNames){
			currentBean.addValue(param, 0.0, false);
		}

		beans.put(ParallelCoordinatesDTO.DEFAULT, currentBean);

		return beans;

	}

	private TreeMap<String, ParallelCoordinatesDTO> addMeanValues(TreeMap<String, ParallelCoordinatesDTO> beans, List<ParameterDTO> allParameterNames) {

		ParallelCoordinatesDTO currentBean = new ParallelCoordinatesDTO(ParallelCoordinatesDTO.MEAN, null, "Mean", allParameterNames);

		Map<String, Double> sum = new HashMap<>();
		Map<String, Integer> n = new HashMap<>();

		for (ParameterDTO param : allParameterNames){
			sum.put(param.getName(), new Double(0.0));
			n.put(param.getName(), 0);
		}

		for (ParallelCoordinatesDTO pc : beans.values()){
			pc.getValues().values().stream().filter(val -> val.getGenotypeEffect() != null).forEach(val -> {
				sum.put(val.getParameterName(), (sum.get(val.getParameterName()) + val.getGenotypeEffect()));
				n.put(val.getParameterName(), (n.get(val.getParameterName()) + 1));
			});
		}

		for (ParameterDTO param : allParameterNames){
			Double mean = (n.get(param.getName()) > 0) ?sum.get(param.getName()) / n.get(param.getName()) : 0;
			currentBean.addValue(param, mean, false);
		}

		beans.put(ParallelCoordinatesDTO.MEAN, currentBean);

		return beans;

	}

	private TreeMap<String, ParallelCoordinatesDTO> addMaxGenotypeEffects(List<StatisticalResultDTO> docs, Map<String, ParameterDTO> parameters, String baseUrl) {

		TreeMap<String, ParallelCoordinatesDTO> beans = new TreeMap<>(getParallelCoordsComparator());

		for (StatisticalResultDTO doc: docs) {

			String geneAccession = null;
			Double val = 0.0;

			if (doc.getGenotypeEffectParameterEstimate() != null){
				val = doc.getGenotypeEffectParameterEstimate();
			}
			if (doc.getFemaleKoParameterEstimate() != null && Math.abs(val) < Math.abs(doc.getFemaleKoParameterEstimate())){
				val = doc.getFemaleKoParameterEstimate();
			}
			if (doc.getMaleKoParameterEstimate() != null && Math.abs(val) < Math.abs(doc.getMaleKoParameterEstimate())){
				val = doc.getMaleKoParameterEstimate();
			}
			if (geneAccession == null){
				geneAccession = doc.getMarkerAccessionId();
			}

			String gene = doc.getMarkerSymbol();
			String group = (gene == null) ? "WT " : "Mutant";
			ParameterDTO p = parameters.get(doc.getParameterStableId());
			ParallelCoordinatesDTO currentBean = beans.containsKey(gene + " " + group) ? beans.get(gene + " " + group) : new ParallelCoordinatesDTO(gene,  geneAccession, group, parameters.values());
			currentBean.addValue(p, val, doc.getSignificant());
			beans.put(gene + " " + group, currentBean);
		}

		return beans;
	}


	public StackedBarsData getUnidimensionalData(Parameter p, List<String> genes, List<String> strains, String biologicalSample, String[] center, String[] sex)
			throws SolrServerException, IOException  {

		String urlParams = "";
		SolrQuery query = new SolrQuery().addFilterQuery(StatisticalResultDTO.PARAMETER_STABLE_ID + ":" + p.getStableId());
		String q = "*:*";
		query.addFilterQuery((strains.size() > 1) ? "(" + StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(strains.toArray(), "\" OR " + StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"") + "\")" : StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" + strains.get(0) + "\"");
		if (strains.size() > 0) {
			urlParams += "&strain=" + StringUtils.join(strains.toArray(), "&strain=");
		}

		if (center != null && center.length > 0) {
			query.addFilterQuery( "(" + ((center.length > 1) ? StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + StringUtils.join(center, "\" OR " + StatisticalResultDTO.PHENOTYPING_CENTER + ":\"") + "\"" : StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + center[0] + "\"") + ")");
			urlParams += "&phenotyping_center=" + StringUtils.join(center, "&phenotyping_center=");
		}

		if (sex != null && sex.length == 1) {
			if (sex[0].equalsIgnoreCase("male")){
				query.addFilterQuery( StatisticalResultDTO.MALE_CONTROL_COUNT + ":[4 TO 100000]");
				query.addFilterQuery( StatisticalResultDTO.MALE_MUTANT_COUNT + ":[4 TO 100000]");
			} else {
				query.addFilterQuery( StatisticalResultDTO.FEMALE_CONTROL_COUNT + ":[4 TO 100000]");
				query.addFilterQuery( StatisticalResultDTO.FEMALE_MUTANT_COUNT + ":[4 TO 100000]");
			}
		}

		query.setQuery(q);
		query.addFilterQuery("(" + StatisticalResultDTO.FEMALE_CONTROL_COUNT + ":[4 TO 100000] OR " + StatisticalResultDTO.MALE_CONTROL_COUNT + ":[4 TO 100000])");
		query.setRows(10000000);
		query.setSort(StatisticalResultDTO.DOCUMENT_ID, SolrQuery.ORDER.asc);
		query.setFields(StatisticalResultDTO.MARKER_ACCESSION_ID, StatisticalResultDTO.FEMALE_CONTROL_MEAN, StatisticalResultDTO.MARKER_SYMBOL,
				StatisticalResultDTO.FEMALE_MUTANT_MEAN, StatisticalResultDTO.MALE_CONTROL_MEAN, StatisticalResultDTO.MALE_MUTANT_MEAN,
				StatisticalResultDTO.FEMALE_CONTROL_COUNT, StatisticalResultDTO.FEMALE_MUTANT_COUNT, StatisticalResultDTO.MALE_CONTROL_COUNT, StatisticalResultDTO.MALE_MUTANT_COUNT);
		query.set("group", true);
		query.set("group.field", StatisticalResultDTO.COLONY_ID);
		query.set("group.limit", 1);

		List<Group> groups = statisticalResultCore.query(query).getGroupResponse().getValues().get(0).getValues();
		double[] meansArray = new double[groups.size()];
		String[] genesArray = new String[groups.size()];
		String[] geneSymbolArray = new String[groups.size()];
		int size = 0;

		for (Group gr : groups) {

			SolrDocumentList resDocs = gr.getResult();
			String sexToDisplay = null;
			OverviewRatio overviewRatio = new OverviewRatio();

			for (SolrDocument doc : resDocs){
				sexToDisplay = getSexToDisplay(sex, sexToDisplay, doc);
				overviewRatio.add(doc);
			}

			if (sexToDisplay != null){
				Double ratio = overviewRatio.getPlotRatio(sexToDisplay);
				if (ratio != null){
					genesArray[size] = (String) resDocs.get(0).get(StatisticalResultDTO.MARKER_ACCESSION_ID);
					geneSymbolArray[size] = (String) resDocs.get(0).get(StatisticalResultDTO.MARKER_SYMBOL);
					meansArray[size] = ratio;
					size++;
				}
			}
		}

		// we do the binning for all the data but fill the bins after that to
		// keep tract of phenotype associations
		int binCount = Math.min((int) Math.floor((double) groups.size() / 2), 20);
		List<String> mutantGenes = new ArrayList<>();
		List<String> controlGenes = new ArrayList<>();
		List<String> mutantGeneAcc = new ArrayList<>();
		List<String> controlGeneAcc = new ArrayList<>();
		List<Double> upperBounds = new ArrayList<>();
		EmpiricalDistribution distribution = new EmpiricalDistribution(binCount);
		if (size > 0) {
			distribution.load(ArrayUtils.subarray(meansArray, 0, size-1));
			for (double bound : distribution.getUpperBounds()) {
				upperBounds.add(bound);
			}
			// we we need to distribute the control mutants and the
			// phenotype-mutants in the bins
			List<Double> controlM = new ArrayList<>();
			List<Double> phenMutants = new ArrayList<>();

			for (int j = 0; j < upperBounds.size(); j++) {
				controlM.add((double) 0);
				phenMutants.add((double) 0);
				controlGenes.add("");
				mutantGenes.add("");
				controlGeneAcc.add("");
				mutantGeneAcc.add("");
			}

			for (int j = 0; j < size; j++) {
				// find out the proper bin
				int binIndex = getBin(upperBounds, meansArray[j]);
				if (genes.contains(genesArray[j])) {
					phenMutants.set(binIndex, 1 + phenMutants.get(binIndex));
					String genesString = mutantGenes.get(binIndex);
					if (!genesString.contains(geneSymbolArray[j])) {
						if (genesString.equals("")) {
							mutantGenes.set(binIndex, geneSymbolArray[j]);
							mutantGeneAcc.set(binIndex, "accession=" + genesArray[j]);
						} else {
							mutantGenes.set(binIndex, genesString + ", " + geneSymbolArray[j]);
							mutantGeneAcc.set(binIndex, mutantGeneAcc.get(binIndex) + "&accession=" + genesArray[j]);
						}
					}
				} else { // treat as control because they don't have this phenotype association
					String genesString = controlGenes.get(binIndex);
					if (!genesString.contains(geneSymbolArray[j])) {
						if (genesString.equalsIgnoreCase("")) {
							controlGenes.set(binIndex, geneSymbolArray[j]);
							controlGeneAcc.set(binIndex, "accession=" + genesArray[j]);
						} else {
							controlGenes.set(binIndex, genesString + ", " + geneSymbolArray[j]);
							controlGeneAcc.set(binIndex, controlGeneAcc.get(binIndex) + "&accession=" + genesArray[j]);
						}
					}
					controlM.set(binIndex, 1 + controlM.get(binIndex));
				}
			}

			// add the rest of parameters to the graph urls
			for (int t = 0; t < controlGeneAcc.size(); t++) {
				controlGeneAcc.set(t, controlGeneAcc.get(t) + urlParams);
				mutantGeneAcc.set(t, mutantGeneAcc.get(t) + urlParams);
			}

			StackedBarsData data = new StackedBarsData();
			data.setUpperBounds(upperBounds);
			data.setControlGenes(controlGenes);
			data.setControlMutatns(controlM);
			data.setMutantGenes(mutantGenes);
			data.setPhenMutants(phenMutants);
			data.setControlGeneAccesionIds(controlGeneAcc);
			data.setMutantGeneAccesionIds(mutantGeneAcc);
			return data;
		}

		return null;
	}

	private String getSexToDisplay(String[] sex, String oldSexToDisplay, SolrDocument doc){

		String sexToDisplay = null;

		if (sex != null && sex.length == 1 && oldSexToDisplay == null){
			if (sex[0].equalsIgnoreCase(SexType.male.getName())) {
				sexToDisplay = 	SexType.male.getName();
			}
			if (sex[0].equalsIgnoreCase(SexType.female.getName())) {
				sexToDisplay = 	SexType.female.getName();
			}
		}

		if (sex == null || sex.length == 0 || sex.length == 2) {
			if ( doc.containsKey(StatisticalResultDTO.FEMALE_CONTROL_MEAN) && Double.parseDouble(doc.get(StatisticalResultDTO.FEMALE_CONTROL_COUNT).toString()) > 3 &&
					doc.containsKey(StatisticalResultDTO.FEMALE_MUTANT_MEAN) && Double.parseDouble(doc.get(StatisticalResultDTO.FEMALE_MUTANT_COUNT).toString()) > 3 &&
					doc.containsKey(StatisticalResultDTO.MALE_CONTROL_MEAN) && Double.parseDouble(doc.get(StatisticalResultDTO.MALE_CONTROL_COUNT).toString()) > 3 &&
					doc.containsKey(StatisticalResultDTO.MALE_MUTANT_MEAN) && Double.parseDouble(doc.get(StatisticalResultDTO.MALE_MUTANT_COUNT).toString()) > 3 ){
				sexToDisplay = "both";
			}
			else if (doc.containsKey(StatisticalResultDTO.FEMALE_CONTROL_MEAN) &&  Double.parseDouble(doc.get(StatisticalResultDTO.FEMALE_CONTROL_COUNT).toString()) > 3 &&
					doc.containsKey(StatisticalResultDTO.FEMALE_MUTANT_MEAN) &&  Double.parseDouble(doc.get(StatisticalResultDTO.FEMALE_MUTANT_COUNT).toString()) > 3
					){
				if (oldSexToDisplay != null && (oldSexToDisplay.equalsIgnoreCase(SexType.male.getName()) || oldSexToDisplay.equalsIgnoreCase(SexType.both.getName()) )){
					sexToDisplay = "both";
				}
				else {
					sexToDisplay = SexType.female.getName();
				}
			}
			else if (doc.containsKey(StatisticalResultDTO.MALE_CONTROL_MEAN) &&  Double.parseDouble(doc.get(StatisticalResultDTO.MALE_CONTROL_COUNT).toString()) > 3 &&
					doc.containsKey(StatisticalResultDTO.MALE_MUTANT_MEAN) &&  Double.parseDouble(doc.get(StatisticalResultDTO.MALE_MUTANT_COUNT).toString()) > 3 ){
				if (oldSexToDisplay != null && (oldSexToDisplay.equalsIgnoreCase(SexType.female.getName()) || oldSexToDisplay.equalsIgnoreCase(SexType.both.getName()) )){
					sexToDisplay = "both";
				}
				else {
					sexToDisplay = SexType.male.getName();
				}
			}
		}

		return sexToDisplay;
	}


	private int getBin(List<Double> bins, Double valueToBin) {

		for (Double upperBound : bins) {
			if (valueToBin < upperBound) { return bins.indexOf(upperBound); }
		}
		return bins.size() - 1;
	}


	public Map<String, List<String>> getDistributionOfLinesByMPTopLevel(List<String> resourceName, Float pValueThreshold)
			throws SolrServerException, IOException , InterruptedException, ExecutionException {

		Map<String, List<String>> res = new ConcurrentHashMap<>(); //<parameter, <genes>>
		String pivotFacet =  StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME + "," + StatisticalResultDTO.COLONY_ID;
		SolrQuery q = new SolrQuery();

		if (resourceName != null){
			q.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + StatisticalResultDTO.RESOURCE_NAME + ":"));
		}else {
			q.setQuery("*:*");
		}

		if (pValueThreshold != null){
			q.setFilterQueries(StatisticalResultDTO.P_VALUE + ":[0 TO " + pValueThreshold + "]");
		}

		q.set("facet.pivot", pivotFacet);
		q.setFacet(true);
		q.setFacetMinCount(1);
		q.setRows(1);
		q.set("facet.limit", -1);

		logger.info("Solr url for getDistributionOfLinesByMPTopLevel " + SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + q);
		QueryResponse response = statisticalResultCore.query(q);

		for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
			if (pivot.getPivot() != null) {
				List<String> colonies = new ArrayList<>();
				for (PivotField colony : pivot.getPivot()){
					colonies.add(colony.getValue().toString());
				}
				res.put(pivot.getValue().toString(), new ArrayList<String>(colonies));
			}
		}

		return res;
	}


	public Map<String, List<String>> getDistributionOfGenesByMPTopLevel(List<String> resourceName, Float pValueThreshold)
			throws SolrServerException, IOException , InterruptedException, ExecutionException {

		Map<String, List<String>> res = new ConcurrentHashMap<>(); //<parameter, <genes>>
		String pivotFacet =  StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME + "," + StatisticalResultDTO.MARKER_ACCESSION_ID;
		SolrQuery q = new SolrQuery();

		if (resourceName != null){
			q.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + StatisticalResultDTO.RESOURCE_NAME + ":"));
		}else {
			q.setQuery("*:*");
		}

		if (pValueThreshold != null){
			q.setFilterQueries(StatisticalResultDTO.P_VALUE + ":[0 TO " + pValueThreshold + "]");
		}

		q.set("facet.pivot", pivotFacet);
		q.setFacet(true);
		q.setRows(1);
		q.set("facet.limit", -1);

		logger.info("Solr url for getDistributionOfGenesByMPTopLevel " + SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + q);
		QueryResponse response = statisticalResultCore.query(q);

		for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
			if (pivot.getPivot() != null) {
				List<String> genes = new ArrayList<>();
				for (PivotField gene : pivot.getPivot()){
					genes.add(gene.getValue().toString());
				}
				res.put(pivot.getValue().toString(), new ArrayList<String>(genes));
			}
		}

		return res;
	}

	/**
	 * @return Map <String, Long> : <top_level_mp_name, number_of_annotations>
	 * @author tudose
	 */
	public TreeMap<String, Long> getDistributionOfAnnotationsByMPTopLevel(List<String> resourceName, Float pValueThreshold) {

		SolrQuery query = new SolrQuery();

		if (resourceName != null){
			query.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + StringUtils.join(resourceName, " OR " + StatisticalResultDTO.RESOURCE_NAME + ":"));
		}else {
			query.setQuery("*:*");
		}

		if (pValueThreshold != null){
			query.setFilterQueries(StatisticalResultDTO.P_VALUE + ":[0 TO " + pValueThreshold + "]");
		}

		query.setFacet(true);
		query.setFacetLimit(-1);
		query.setFacetMinCount(1);
		query.setRows(0);
		query.addFacetField(StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME);

		try {
			QueryResponse response = statisticalResultCore.query(query);
			TreeMap<String, Long> res = new TreeMap<>();
			res.putAll(getFacets(response).get(StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME));
			return res;
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * Get the result for a set of
	 *  allele strain phenotypeCenter, pipeline, parameter, metadata, zygosity, sex
	 * @param statisticalType
	 * @throws SolrServerException, IOException
	 */
	public List<? extends StatisticalResult> getStatisticalResult(
			String alleleAccession,
			String strain,
			String phenotypingCenter,
			String pipelineStableId,
			String parameterStableId,
			String metadataGroup,
			ZygosityType zygosity,
			SexType sex,
			ObservationType statisticalType) throws SolrServerException, IOException  {

		List<StatisticalResult> results = new ArrayList<>();

		QueryResponse response = new QueryResponse();
		
		SolrQuery query = new SolrQuery()
				.setQuery("*:*")
				.addFilterQuery(StatisticalResultDTO.ALLELE_ACCESSION_ID + ":\"" + alleleAccession + "\"")
				.addFilterQuery(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"")
				.addFilterQuery(StatisticalResultDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId)
				.addFilterQuery(StatisticalResultDTO.PARAMETER_STABLE_ID + ":" + parameterStableId)
				.addFilterQuery(StatisticalResultDTO.ZYGOSITY + ":" + zygosity.name())
				.setStart(0)
				.setRows(10)
				;

		if(strain != null) {
			query.addFilterQuery(StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" + strain + "\"");
		}

		if(sex != null) {
			query.addFilterQuery(StatisticalResultDTO.SEX + ":" + sex);
		}

		if(metadataGroup==null) {
			// don't add a metadata group filter
		} else if (metadataGroup.isEmpty()) {
			query.addFilterQuery(StatisticalResultDTO.METADATA_GROUP + ":\"\"");
		} else {
			query.addFilterQuery(StatisticalResultDTO.METADATA_GROUP + ":" + metadataGroup);
		}

		System.out.println("statistical-result query========: "+query);

		response = statisticalResultCore.query(query);
		List<StatisticalResultDTO> solrResults = response.getBeans(StatisticalResultDTO.class);

		if (statisticalType == ObservationType.unidimensional) {

			for (StatisticalResultDTO solrResult : solrResults) {
				results.add(translateStatisticalResultToUnidimensionalResult(solrResult));
			}

		} else if (statisticalType == ObservationType.categorical) {

			for (StatisticalResultDTO solrResult : solrResults) {
				results.add(translateStatisticalResultToCategoricalResult(solrResult));
			}

		}

		return results;
	}


	public Map<String, Set<String>> getAccessionProceduresMap(String resourceName){

		SolrQuery query = new SolrQuery();
		Map<String, Set<String>> res =  new HashMap<>();
		NamedList<List<PivotField>> response;

		if (resourceName == null){
			query.setQuery("*:*");
		}else {
			query.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + resourceName);
		}
		query.setFacet(true);
		query.addFacetPivotField(StatisticalResultDTO.MARKER_ACCESSION_ID + "," + StatisticalResultDTO.PROCEDURE_STABLE_ID);
		query.setFacetLimit(-1);
		query.setFacetMinCount(1);
		query.setRows(0);

		try {
			response = statisticalResultCore.query(query).getFacetPivot();
			for (PivotField genePivot : response.get(StatisticalResultDTO.MARKER_ACCESSION_ID + "," + StatisticalResultDTO.PROCEDURE_STABLE_ID)){
				if (genePivot.getPivot() != null) {
					String geneName = genePivot.getValue().toString();
					Set<String> procedures = new HashSet<>();
					for (PivotField f : genePivot.getPivot()){
						procedures.add(f.getValue().toString());
					}
					res.put(geneName, procedures);
				}
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return res;
	}


	public Map<String, List<ExperimentsDataTableRow>> getPvaluesByAlleleAndPhenotypingCenterAndPipeline(String geneAccession, List<String> procedureName ,List<String> alleleSymbol, List<String> phenotypingCenter, List<String> pipelineName, List<String> procedureStableIds, List<String> resource, List<String> mpTermId, String graphBaseUrl)
			throws NumberFormatException, SolrServerException, IOException, UnsupportedEncodingException {

		Map<String, List<ExperimentsDataTableRow>> results = new HashMap<>();

		SolrQuery query = buildQuery(geneAccession, procedureName,alleleSymbol, phenotypingCenter, pipelineName, procedureStableIds, resource, mpTermId, null, null, null, null, null, null, null, null);
		List<StatisticalResultDTO> solrResults = statisticalResultCore.query(query).getBeans(StatisticalResultDTO.class);

		for (StatisticalResultDTO statResult : solrResults) {

			if (!results.containsKey(statResult.getParameterStableId())) {
				results.put(statResult.getParameterStableId(), new ArrayList<ExperimentsDataTableRow>());
			}

			results.get(statResult.getParameterStableId()).add(getRowFromDto(statResult, graphBaseUrl));
		}

		return results;

	}

	public Map<CombinedObservationKey, ExperimentsDataTableRow> getAllDataRecords(String geneAccession, List<String> procedureName ,List<String> alleleSymbol, List<String> phenotypingCenter, List<String> pipelineName, List<String> procedureStableIds, List<String> resource, List<String> mpTermId, String graphBaseUrl)
			throws NumberFormatException, SolrServerException, IOException {

		Map<CombinedObservationKey, ExperimentsDataTableRow> results = new HashMap<>();

		SolrQuery query = buildQuery(geneAccession, procedureName,alleleSymbol, phenotypingCenter, pipelineName, procedureStableIds, resource, mpTermId, null, null, null, null, null, null, null, null);
		List<StatisticalResultDTO> solrResults = statisticalResultCore.query(query).getBeans(StatisticalResultDTO.class);

		for (StatisticalResultDTO statResult : solrResults) {
			ExperimentsDataTableRow row = getRowFromDto(statResult, graphBaseUrl);
			results.put(row.getCombinedKey(), row);
		}

		return results;

	}

	private ExperimentsDataTableRow getRowFromDto(StatisticalResultDTO dto, String graphBaseUrl)
			throws UnsupportedEncodingException{

		MarkerBean allele = new MarkerBean();
		allele.setAccessionId(dto.getAlleleAccessionId());
		allele.setSymbol(dto.getAlleleSymbol());

		MarkerBean gene = new MarkerBean();
		gene.setAccessionId(dto.getMarkerAccessionId());
		gene.setSymbol(dto.getMarkerSymbol());

		ImpressBaseDTO procedure  = new ImpressBaseDTO(null, dto.getProcedureStableKey(), dto.getProcedureStableId(), dto.getProcedureName());
		ImpressBaseDTO parameter = new ImpressBaseDTO(null, dto.getParameterStableKey(), dto.getParameterStableId(), dto.getParameterName());
		ImpressBaseDTO pipeline = new ImpressBaseDTO(null, dto.getPipelineStableKey(), dto.getPipelineStableId(), dto.getPipelineName());
		ZygosityType zygosity = dto.getZygosity() != null ? ZygosityType.valueOf(dto.getZygosity()) : ZygosityType.not_applicable;
		ExperimentsDataTableRow row = new ExperimentsDataTableRow(dto.getPhenotypingCenter(), dto.getStatisticalMethod(),
				dto.getStatus(), allele, gene, zygosity,
				pipeline, procedure, parameter, graphBaseUrl, dto.getpValue(), dto.getFemaleMutantCount(),
				dto.getMaleMutantCount(), dto.getEffectSize(), dto.getMetadataGroup());
		row.setLifeStageName(dto.getLifeStageName());
		row.setLifeStageAcc(dto.getLifeStageAcc());
		row.setSignificant(dto.getSignificant());
		return row;
	}


	/**
	 *
	 * @param gene
	 * @param zygosity
	 * @return SolrDocumentList grouped by top level MP term
	 * @throws SolrServerException, IOException
	 * @author ilinca
	 */
	public HashMap<String, List<StatisticalResultDTO>> getPhenotypesForTopLevelTerm(String gene, ZygosityType zygosity)
			throws SolrServerException, IOException  {

		HashMap<String, List<StatisticalResultDTO>> res = new HashMap<>();
		String query = "*:*";

		if (gene.equalsIgnoreCase("*")) {
			query = StatisticalResultDTO.MARKER_ACCESSION_ID + ":" + gene;
		} else {
			query = StatisticalResultDTO.MARKER_ACCESSION_ID + ":\"" + gene + "\"";
		}

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query);
		solrQuery.setRows(Integer.MAX_VALUE);
		solrQuery.setSort(StatisticalResultDTO.P_VALUE, ORDER.asc);
		solrQuery.addFilterQuery(StringUtils.join(Arrays.asList(StatisticalResultDTO.MP_TERM_ID + ":*", StatisticalResultDTO.FEMALE_MP_TERM_ID+":*", StatisticalResultDTO.MALE_MP_TERM_ID+":*"), " OR "));
		solrQuery.addFilterQuery(StatisticalResultDTO.STATUS + ":Success");
		solrQuery.setFields(StatisticalResultDTO.P_VALUE, StatisticalResultDTO.SEX, StatisticalResultDTO.ZYGOSITY,
				StatisticalResultDTO.MARKER_ACCESSION_ID, StatisticalResultDTO.MARKER_SYMBOL,
				StatisticalResultDTO.MP_TERM_ID, StatisticalResultDTO.MP_TERM_NAME,
				StatisticalResultDTO.TOP_LEVEL_MP_TERM_ID, StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME,
				StatisticalResultDTO.FEMALE_TOP_LEVEL_MP_TERM_ID, StatisticalResultDTO.FEMALE_TOP_LEVEL_MP_TERM_NAME,
				StatisticalResultDTO.MALE_TOP_LEVEL_MP_TERM_ID, StatisticalResultDTO.MALE_TOP_LEVEL_MP_TERM_NAME,
				StatisticalResultDTO.PHENOTYPE_SEX, StatisticalResultDTO.RESOURCE_NAME,
				StatisticalResultDTO.PROCEDURE_STABLE_ID, StatisticalResultDTO.SIGNIFICANT);

		if (zygosity != null) {
			solrQuery.addFilterQuery(StatisticalResultDTO.ZYGOSITY + ":" + zygosity.getName());
		}
		List<StatisticalResultDTO> dtos = statisticalResultCore.query(solrQuery).getBeans(StatisticalResultDTO.class);

		for (StatisticalResultDTO dto : dtos) {
			if (dto.getTopLevelMpTermId() != null || dto.getFemaleTopLevelMpTermId() != null || dto.getMaleTopLevelMpTermId() != null) {

				// Collect all the top level terms into a single list
				List<String> topLevelTermIds = Stream.of(dto.getTopLevelMpTermId(), dto.getFemaleTopLevelMpTermId(), dto.getMaleTopLevelMpTermId())
						.filter(Objects::nonNull)
						.flatMap(Collection::stream)
						.collect(Collectors.toList());
				
				for (String id : topLevelTermIds ) {
					if (!res.containsKey(id)) {
						res.put(id, new ArrayList<>());
					}
					
					res.get(id).add(dto);
				}

			} 
			else if (dto.getMpTermId()!=null) {
				
				String id = dto.getMpTermId();
				if (!res.containsKey(id)) {
					res.put(id, new ArrayList<>());
				}
				res.get(id).add(dto);

			}
		}
		return res;
	}



	public PhenotypeFacetResult getPhenotypeFacetResultByPhenotypingCenterAndPipeline(String phenotypingCenter, String pipelineStableId)
			throws IOException, URISyntaxException, JSONException {

		SolrQuery query = new SolrQuery();
		query.setQuery(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter);
		query.addFilterQuery(StatisticalResultDTO.PIPELINE_STABLE_ID + ":" + pipelineStableId);
		query.setFacet(true);
		query.addFacetField(StatisticalResultDTO.RESOURCE_FULLNAME);
		query.addFacetField(StatisticalResultDTO.PROCEDURE_NAME );
		query.addFacetField(StatisticalResultDTO.MARKER_SYMBOL);
		query.addFacetField(StatisticalResultDTO.MP_TERM_NAME );
		query.setSort(StatisticalResultDTO.P_VALUE, SolrQuery.ORDER.asc);
		query.setRows(10000000);
		query.set("wt", "json");
		query.set("version", "2.2");

		String solrUrl = SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query;
		return createPhenotypeResultFromSolrResponse(solrUrl);
	}


	public Set<String> getAccessionsByResourceName(String resourceName){

		Set<String> res = new HashSet<>();
		SolrQuery query = new SolrQuery()
				.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":" + resourceName);
		query.setFacet(true);
		query.addFacetField(StatisticalResultDTO.MARKER_ACCESSION_ID);
		query.setFacetLimit(10000000);
		query.setFacetMinCount(1);
		query.setRows(0);

		QueryResponse response;
		try {
			response = statisticalResultCore.query(query);
			for (Count id: response.getFacetField(StatisticalResultDTO.MARKER_ACCESSION_ID).getValues()){
				res.add(id.getName());
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	protected UnidimensionalResult translateStatisticalResultToUnidimensionalResult(StatisticalResultDTO result) {

		UnidimensionalResult r = new UnidimensionalResult();

		if(result.getBatchSignificant()!=null) r.setBatchSignificance(result.getBatchSignificant());
		if(result.getBlupsTest()!= null) r.setBlupsTest(new Double(result.getBlupsTest()));
		r.setColonyId(result.getColonyId());
		//       if(result.getControlBiologicalModelId()!= null) r.setControlBiologicalModel(biologicalModelDAO.getBiologicalModelById(result.getControlBiologicalModelId()));
		r.setControlSelectionStrategy(result.getControlSelectionMethod());
		if(result.getResourceId()!= null) r.setDatasource(datasourceRepository.findById(result.getResourceId()).get());
		r.setDependentVariable(result.getDependentVariable());
		if(result.getEffectSize()!= null) r.setEffectSize(new Double(result.getEffectSize()));
		if(result.getMutantBiologicalModelId()!= null) r.setExperimentalBiologicalModel(biologicalModelRepository.findById(result.getMutantBiologicalModelId()).get());
		if(result.getZygosity()!= null) r.setExperimentalZygosity(ZygosityType.valueOf(result.getZygosity()));
		r.setFemaleControls(result.getFemaleControlCount());
		r.setFemaleMutants(result.getFemaleMutantCount());
		if(result.getFemaleKoParameterEstimate()!= null) r.setGenderFemaleKoEstimate(result.getFemaleKoParameterEstimate());
		if(result.getFemaleKoEffectPValue()!= null) r.setGenderFemaleKoPValue(new Double(result.getFemaleKoEffectPValue()));
		if(result.getFemaleKoEffectStderrEstimate()!= null) r.setGenderFemaleKoStandardErrorEstimate(new Double(result.getFemaleKoEffectStderrEstimate()));
		if(result.getMaleKoParameterEstimate()!= null) r.setGenderMaleKoEstimate(new Double(result.getMaleKoParameterEstimate()));
		if(result.getMaleKoEffectPValue()!= null) r.setGenderMaleKoPValue(new Double(result.getMaleKoEffectPValue()));
		if(result.getMaleKoEffectStderrEstimate()!= null) r.setGenderMaleKoStandardErrorEstimate(new Double(result.getMaleKoEffectStderrEstimate()));
		if(result.getSexEffectPValue()!= null) r.setGenderEffectPValue(new Double(result.getSexEffectPValue()));
		if(result.getSexEffectParameterEstimate()!= null) r.setGenderParameterEstimate(new Double(result.getSexEffectParameterEstimate()));
		if(result.getSexEffectStderrEstimate()!= null) r.setGenderStandardErrorEstimate(new Double(result.getSexEffectStderrEstimate()));
		if(result.getGenotypeEffectPValue()!= null) r.setGenotypeEffectPValue(new Double(result.getGenotypeEffectPValue()));
		if(result.getGenotypeEffectParameterEstimate()!= null) r.setGenotypeParameterEstimate(new Double(result.getGenotypeEffectParameterEstimate()));
		if(result.getGenotypeEffectStderrEstimate()!= null) r.setGenotypeStandardErrorEstimate(new Double(result.getGenotypeEffectStderrEstimate()));
		r.setGp1Genotype(result.getGroup1Genotype());
		if(result.getGroup1ResidualsNormalityTest()!= null) r.setGp1ResidualsNormalityTest(new Double(result.getGroup1ResidualsNormalityTest()));
		r.setGp2Genotype(result.getGroup2Genotype());
		if(result.getGroup2ResidualsNormalityTest()!= null) r.setGp2ResidualsNormalityTest(new Double(result.getGroup2ResidualsNormalityTest()));
		r.setId(result.getDbId());
		if(result.getInteractionEffectPValue()!= null) r.setInteractionEffectPValue(new Double(result.getInteractionEffectPValue()));
		r.setInteractionSignificance(result.getInteractionSignificant());
		if(result.getInterceptEstimate()!= null) r.setInterceptEstimate(new Double(result.getInterceptEstimate()));
		if(result.getInterceptEstimateStderrEstimate()!= null) r.setInterceptEstimateStandardError(new Double(result.getInterceptEstimateStderrEstimate()));
		r.setMaleControls(result.getMaleControlCount());
		r.setMaleMutants(result.getMaleMutantCount());
		r.setMetadataGroup(result.getMetadataGroup());
		if(result.getNullTestPValue()!= null) r.setNullTestSignificance(new Double(result.getNullTestPValue()));
		if(result.getPhenotypingCenter()!= null) r.setOrganisation(organisationRepository.getByName(result.getPhenotypingCenter()));
		if(result.getParameterStableId()!= null) r.setParameter(parameterRepository.getFirstByStableId(result.getParameterStableId()));
		if(result.getPipelineStableId()!= null) r.setPipeline(pipelineRepository.getByStableId(result.getPipelineStableId()));
		//    if(result.getProjectName()!= null) r.setProject(projectDAO.getProjectByName(result.getProjectName()));
		if(result.getpValue()!= null) r.setpValue(new Double(result.getpValue()));
		r.setRawOutput(result.getRawOutput());
		if(result.getRotatedResidualsTest()!= null) r.setRotatedResidualsNormalityTest(new Double(result.getRotatedResidualsTest()));
		if(result.getSex()!= null) r.setSexType(SexType.valueOf(result.getSex()));
		r.setStatisticalMethod(result.getStatisticalMethod());
		r.setVarianceSignificance(result.getVarianceSignificant());
		if(result.getWeightEffectPValue()!= null) r.setWeightEffectPValue(new Double(result.getWeightEffectPValue()));
		if(result.getWeightEffectParameterEstimate()!= null) r.setWeightParameterEstimate(new Double(result.getWeightEffectParameterEstimate()));
		if(result.getWeightEffectStderrEstimate()!= null) r.setWeightStandardErrorEstimate(new Double(result.getWeightEffectStderrEstimate()));
		if(result.getZygosity()!= null) r.setZygosityType(ZygosityType.valueOf(result.getZygosity()));
		if(result.getMpTermId()!= null){
			r.setMpTermId(result.getMpTermId());
		}
		if(result.getMpTermName()!= null) r.setMpTermName(result.getMpTermName());
		if(result.getStatus()!=null)r.setStatus(result.getStatus());

		// Reference range plus results
		if (result.getGenotypePvalueLowVsNormalHigh() != null) r.setGenotypePvalueLowVsNormalHigh(result.getGenotypePvalueLowVsNormalHigh());
		if (result.getGenotypePvalueLowNormalVsHigh() != null) r.setGenotypePvalueLowNormalVsHigh(result.getGenotypePvalueLowNormalVsHigh());
		if (result.getGenotypeEffectSizeLowVsNormalHigh() != null) r.setGenotypeEffectSizeLowVsNormalHigh(result.getGenotypeEffectSizeLowVsNormalHigh());
		if (result.getGenotypeEffectSizeLowNormalVsHigh() != null) r.setGenotypeEffectSizeLowNormalVsHigh(result.getGenotypeEffectSizeLowNormalVsHigh());
		if (result.getFemalePvalueLowVsNormalHigh() != null) r.setFemalePvalueLowVsNormalHigh(result.getFemalePvalueLowVsNormalHigh());
		if (result.getFemalePvalueLowNormalVsHigh() != null) r.setFemalePvalueLowNormalVsHigh(result.getFemalePvalueLowNormalVsHigh());
		if (result.getFemaleEffectSizeLowVsNormalHigh() != null) r.setFemaleEffectSizeLowVsNormalHigh(result.getFemaleEffectSizeLowVsNormalHigh());
		if (result.getFemaleEffectSizeLowNormalVsHigh() != null) r.setFemaleEffectSizeLowNormalVsHigh(result.getFemaleEffectSizeLowNormalVsHigh());
		if (result.getMalePvalueLowVsNormalHigh() != null) r.setMalePvalueLowVsNormalHigh(result.getMalePvalueLowVsNormalHigh());
		if (result.getMalePvalueLowNormalVsHigh() != null) r.setMalePvalueLowNormalVsHigh(result.getMalePvalueLowNormalVsHigh());
		if (result.getMaleEffectSizeLowVsNormalHigh() != null) r.setMaleEffectSizeLowVsNormalHigh(result.getMaleEffectSizeLowVsNormalHigh());
		if (result.getMaleEffectSizeLowNormalVsHigh() != null) r.setMaleEffectSizeLowNormalVsHigh(result.getMaleEffectSizeLowNormalVsHigh());

		if (result.getClassificationTag()!= null) r.setClassification(result.getClassificationTag());


		return r;
	}

	protected CategoricalResult translateStatisticalResultToCategoricalResult(StatisticalResultDTO result) {

		CategoricalResult r = new CategoricalResult();
		r.setColonyId(result.getColonyId());
		if(result.getControlBiologicalModelId()!= null) r.setControlBiologicalModel(biologicalModelRepository.findById(result.getControlBiologicalModelId()).get());
		r.setControlSelectionStrategy(result.getControlSelectionMethod());
//        if(result.getResourceId()!= null) r.setDatasource(datasourceDAO.getDatasourceById(result.getResourceId()));
		r.setDependentVariable(result.getDependentVariable());
		if(result.getEffectSize()!= null) r.setEffectSize(new Double(result.getEffectSize()));
		if (result.getMutantBiologicalModelId() != null) r.setExperimentalBiologicalModel(biologicalModelRepository.findById(result.getMutantBiologicalModelId()).get());
		if(result.getZygosity()!= null) r.setExperimentalZygosity(ZygosityType.valueOf(result.getZygosity()));
		r.setFemaleControls(result.getFemaleControlCount());
		r.setFemaleMutants(result.getFemaleMutantCount());
		r.setMaleControls(result.getMaleControlCount());
		r.setMaleMutants(result.getMaleMutantCount());
		r.setMetadataGroup(result.getMetadataGroup());
		if(result.getPhenotypingCenter()!= null) r.setOrganisation(organisationRepository.getByName(result.getPhenotypingCenter()));
		if(result.getParameterStableId()!= null) r.setParameter(parameterRepository.getFirstByStableId(result.getParameterStableId()));
		if(result.getPipelineStableId()!= null) r.setPipeline(pipelineRepository.getByStableId(result.getPipelineStableId()));
		//       if(result.getProjectName()!= null) r.setProject(projectDAO.getProjectByName(result.getProjectName()));
		if(result.getpValue()!= null) r.setpValue(new Double(result.getpValue()));
		r.setRawOutput(result.getRawOutput());
		if(result.getSex()!= null) r.setSexType(SexType.valueOf(result.getSex()));
		r.setStatisticalMethod(result.getStatisticalMethod());
		if(result.getZygosity()!= null) r.setZygosityType(ZygosityType.valueOf(result.getZygosity()));
		r.setCategoryA(StringUtils.join(result.getCategories(), "|"));
		if(result.getSex()!= null) r.setControlSex(SexType.valueOf(result.getSex()));
		r.setEffectSize(result.getEffectSize());
		if(result.getSex()!= null) r.setExperimentalSex(SexType.valueOf(result.getSex()));
		if(result.getMpTermId()!= null) r.setMpTermId(result.getMpTermId());
		if(result.getMpTermName()!= null) r.setMpTermName(result.getMpTermName());
		if(result.getStatus()!=null)r.setStatus(result.getStatus());
		return r;
	}

	@Cacheable("geneRowCache")
	public HashMap<String, GeneRowForHeatMap> getSecondaryProjectMapForGeneList(Set<String> geneAccessions1, List<BasicBean> topLevelMps) throws IOException, SolrServerException {

		HashMap<String, GeneRowForHeatMap> geneRowMap = new HashMap<>(); // <geneAcc, row>
		Iterators.partition(geneAccessions1.iterator(), 50).forEachRemaining(geneAccessions ->
		{

			SolrQuery q = new SolrQuery()
					.setQuery(geneAccessions.stream().collect(Collectors.joining("\" OR \"", StatisticalResultDTO.MARKER_ACCESSION_ID + ":(\"", "\")")))
					.addField(StatisticalResultDTO.PROCEDURE_STABLE_ID)
					.addField(StatisticalResultDTO.MARKER_ACCESSION_ID)
					.addField(StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME)
					.addField(StatisticalResultDTO.MP_TERM_NAME)
					.addField(StatisticalResultDTO.MARKER_SYMBOL)
					.addField(StatisticalResultDTO.STATUS)
					.addField(StatisticalResultDTO.SIGNIFICANT)
					.setRows(Integer.MAX_VALUE)
					.setSort(StatisticalResultDTO.DOCUMENT_ID, SolrQuery.ORDER.asc);

			q.addFilterQuery(StatisticalResultDTO.MP_TERM_ID + ":*"); // Ignore MPATH or other types of associations
			q.add("group", "true");
			q.add("group.field", StatisticalResultDTO.MARKER_ACCESSION_ID);
			q.set("group.limit", Integer.MAX_VALUE);

			logger.info("getSecondaryProjectMapForGeneList Statistical result query: " + ((HttpSolrClient) statisticalResultCore).getBaseURL() + "/select?" + q.getQuery());
			GroupCommand groups = null;
			try {
				groups = statisticalResultCore.query(q, SolrRequest.METHOD.POST).getGroupResponse().getValues().get(0);
			} catch (SolrServerException | IOException e) {
				e.printStackTrace();
				return;
			}

			for (Group group : groups.getValues()) {
				// Each group contains data for a different gene
				String geneAcc = group.getGroupValue();
				SolrDocumentList docs = group.getResult();
				GeneRowForHeatMap row = new GeneRowForHeatMap(geneAcc, docs.get(0).getFieldValue(StatisticalResultDTO.MARKER_SYMBOL).toString(), topLevelMps); // Fill row with default values for all mp top levels

				for (SolrDocument doc : docs) {
					List<String> currentTopLevelMps = (ArrayList<String>) doc.get(StatisticalResultDTO.TOP_LEVEL_MP_TERM_NAME);

					// The current top level might be null, because the actual term is already a top level,
					// check the associated mp term to see, and add it if it's already top-level
					if (currentTopLevelMps == null) {
						if (topLevelMps.stream().anyMatch(x -> x.getName().equals(doc.getFieldValue(StatisticalResultDTO.MP_TERM_NAME).toString()))) {
							currentTopLevelMps = new ArrayList<>();
							currentTopLevelMps.add(doc.getFieldValue(StatisticalResultDTO.MP_TERM_NAME).toString());
						}
					}

					// The term might have been annotated to "mammalian phenotype" which doesn't have an icon in the grid.  Skip it
					if (currentTopLevelMps != null) {
						for (String mp : currentTopLevelMps) {
							HeatMapCell cell = row.getXAxisToCellMap().containsKey(mp) ? row.getXAxisToCellMap().get(mp) : new HeatMapCell(mp, HeatMapCell.THREE_I_NO_DATA);
							if (doc.getFieldValue(StatisticalResultDTO.SIGNIFICANT) != null && doc.getFieldValue(StatisticalResultDTO.SIGNIFICANT).toString().equalsIgnoreCase("true")) {
								cell.addStatus(HeatMapCell.THREE_I_DEVIANCE_SIGNIFICANT);
							} else if (doc.getFieldValue(StatisticalResultDTO.STATUS).toString().equals("Success")) {
								cell.addStatus(HeatMapCell.THREE_I_DATA_ANALYSED_NOT_SIGNIFICANT);
							} else {
								cell.addStatus(HeatMapCell.THREE_I_COULD_NOT_ANALYSE);
							}

							row.add(cell);
						}
					}
				}
				geneRowMap.put(geneAcc, row);
			}
		});

		return geneRowMap;
	}


	public List<GeneRowForHeatMap> getSecondaryProjectMapForResource(String resourceName) {

		List<GeneRowForHeatMap> res = new ArrayList<>();
		HashMap<String, GeneRowForHeatMap> geneRowMap = new HashMap<>(); // <geneAcc, row>
		List<BasicBean> procedures = getProceduresForDataSource(resourceName);

		for (BasicBean procedure : procedures){
			SolrQuery q = new SolrQuery()
					.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":\"" + resourceName + "\"")
					.addFilterQuery(StatisticalResultDTO.PROCEDURE_STABLE_ID + ":" + procedure.getId())
					.setSort(StatisticalResultDTO.P_VALUE, SolrQuery.ORDER.asc)
					.addField(StatisticalResultDTO.PROCEDURE_STABLE_ID)
					.addField(StatisticalResultDTO.MARKER_ACCESSION_ID)
					.addField(StatisticalResultDTO.MARKER_SYMBOL)
					.addField(StatisticalResultDTO.STATUS)
					.addField(StatisticalResultDTO.P_VALUE)
					.setRows(10000000)
					.setSort(StatisticalResultDTO.DOCUMENT_ID, SolrQuery.ORDER.asc);
			q.add("group", "true");
			q.add("group.field", StatisticalResultDTO.MARKER_ACCESSION_ID);
			q.add("group.sort", StatisticalResultDTO.P_VALUE + " asc");

			try {
				GroupCommand groups = statisticalResultCore.query(q).getGroupResponse().getValues().get(0);

				for (Group group:  groups.getValues()){
					GeneRowForHeatMap row;
					HeatMapCell cell = new HeatMapCell();
					SolrDocument doc = group.getResult().get(0);
					String geneAcc = doc.get(StatisticalResultDTO.MARKER_ACCESSION_ID).toString();
					Map<String, HeatMapCell> xAxisToCellMap = new HashMap<>();

					if (geneRowMap.containsKey(geneAcc)){
						row = geneRowMap.get(geneAcc);
						xAxisToCellMap = row.getXAxisToCellMap();
					} else {
						row = new GeneRowForHeatMap(geneAcc);
						row.setSymbol(doc.get(StatisticalResultDTO.MARKER_SYMBOL).toString());
						xAxisToCellMap.put(procedure.getId(), null);
					}
					cell.setxAxisKey(doc.get(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString());
					if(doc.getFieldValue(StatisticalResultDTO.P_VALUE)!=null && Double.valueOf(doc.getFieldValue(StatisticalResultDTO.P_VALUE).toString()) < 0.0001){
						cell.addStatus(HeatMapCell.THREE_I_DEVIANCE_SIGNIFICANT);
					} else if (doc.getFieldValue(StatisticalResultDTO.STATUS).toString().equals("Success")){
						cell.addStatus(HeatMapCell.THREE_I_DATA_ANALYSED_NOT_SIGNIFICANT);
					} else {
						cell.addStatus(HeatMapCell.THREE_I_COULD_NOT_ANALYSE);
					}
					xAxisToCellMap.put(doc.getFieldValue(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString(), cell);
					row.setXAxisToCellMap(xAxisToCellMap);
					geneRowMap.put(geneAcc, row);
				}
			} catch (SolrServerException | IOException ex) {
				logger.error(ex.getMessage());
			}
		}

		res = new ArrayList<>(geneRowMap.values());
		Collections.sort(res, new GeneRowForHeatMap3IComparator());

		return res;
	}


	public Map<String, List<String>> getParametersToProcedureMap(String alleleAccession, String geneAccession, String resourceName, String phenotypingCenter, String pipelineSrableId, String procedure)
			throws SolrServerException, IOException {

		Map<String, List<String>> res = new ConcurrentHashMap<>(); //<parameter, <genes>>

		SolrQuery q = new SolrQuery().setQuery("*:*");
		q.add("fl", StatisticalResultDTO.PARAMETER_NAME + "," + StatisticalResultDTO.PARAMETER_STABLE_ID);

		if (resourceName != null){
			q.addFilterQuery(StatisticalResultDTO.RESOURCE_NAME + ":\"" + resourceName + "\"");
		}
		if (phenotypingCenter != null){
			q.addFilterQuery(StatisticalResultDTO.PHENOTYPING_CENTER + ":\"" + phenotypingCenter + "\"");
		}
		if (pipelineSrableId != null){
			q.addFilterQuery(StatisticalResultDTO.PIPELINE_STABLE_ID + ":\"" + pipelineSrableId + "\"");
		}
		String pivotFacet =  StatisticalResultDTO.PROCEDURE_NAME  + "," + StatisticalResultDTO.PARAMETER_STABLE_ID;
		q.set("facet.pivot", pivotFacet);
		q.setFacet(true);
		q.setRows(1);
		q.set("facet.limit", -1);

		logger.info("Solr url for getParametersToProcedureMap " + SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + q);
		QueryResponse response = statisticalResultCore.query(q);

		for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
			if (pivot.getPivot() != null){
				List<String> lst = new ArrayList<>();
				for (PivotField gene : pivot.getPivot()){
					lst.add(gene.getValue().toString());
				}
				res.put(pivot.getValue().toString(), new ArrayList<String>(lst));
			}
		}

		return res;
	}


	public List<BasicBean> getProceduresForDataSource(String resourceName){

		List<BasicBean> res = new ArrayList<BasicBean>();
		SolrQuery q = new SolrQuery()
				.setQuery(StatisticalResultDTO.RESOURCE_NAME + ":\"" + resourceName + "\"")
				.setRows(10000);

		q.add("group", "true");
		q.add("group.field", StatisticalResultDTO.PROCEDURE_NAME);
		q.add("group.rows", "1");
		q.add("fl", StatisticalResultDTO.PROCEDURE_NAME + "," + StatisticalResultDTO.PROCEDURE_STABLE_ID);

		logger.info("Procedure query " + SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + q);

		try {
			GroupCommand groups = statisticalResultCore.query(q).getGroupResponse().getValues().get(0);
			for (Group group: groups.getValues()){
				BasicBean bb = new BasicBean();
				SolrDocument doc = group.getResult().get(0);
				bb.setName(doc.getFieldValue(StatisticalResultDTO.PROCEDURE_NAME).toString());
				bb.setId(doc.getFieldValue(StatisticalResultDTO.PROCEDURE_STABLE_ID).toString());
				res.add(bb);
			}
		} catch (SolrServerException | IOException ex) {
			logger.error(ex.getMessage());
		}
		return res;
	}


	/*
     * End of method for PhenotypeCallSummarySolrImpl
     */
	public GeneRowForHeatMap getResultsForGeneHeatMap(String accession, GenomicFeature gene, List<BasicBean> xAxisBeans, Map<String, List<String>> geneToTopLevelMpMap) {

		GeneRowForHeatMap row = new GeneRowForHeatMap(accession);
		if (gene != null) {
			row.setSymbol(gene.getSymbol());
		} else {
			System.err.println("error no symbol for gene " + accession);
		}

		Map<String, HeatMapCell> xAxisToCellMap = new HashMap<>();
		for (BasicBean xAxisBean : xAxisBeans) {
			HeatMapCell cell = new HeatMapCell();
			if (geneToTopLevelMpMap.containsKey(accession)) {

				List<String> mps = geneToTopLevelMpMap.get(accession);
				// cell.setLabel("No Phenotype Detected");
				if (mps != null && !mps.isEmpty()) {
					if (mps.contains(xAxisBean.getId())) {
						cell.setxAxisKey(xAxisBean.getId());
						cell.setLabel("Data Available");
						cell.addStatus("Data Available");
					} else {
						cell.addStatus("No MP");
					}
				} else {
					// System.err.println("mps are null or empty");
					cell.addStatus("No MP");
				}
			} else {
				// if no doc found for the gene then no data available
				cell.addStatus("No Data Available");
			}
			xAxisToCellMap.put(xAxisBean.getId(), cell);
		}
		row.setXAxisToCellMap(xAxisToCellMap);

		return row;
	}


	/**
	 * This map is needed for the summary on phenotype pages (the percentages &
	 * pie chart). It takes a long time to load so it does it asynchronously.
	 *
	 * @param sex
	 * @return Map < String parameterStableId , List<String
	 *         geneMgiIdWithParameterXMeasured>>
	 * @throws SolrServerException, IOException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @author tudose
	 */
	public Map<String, List<String>> getParameterToGeneMap(SexType sex)
			throws SolrServerException, IOException , InterruptedException, ExecutionException {

		Map<String, List<String>> res = new ConcurrentHashMap<>(); //<parameter, <genes>>
		String pivotFacet =  StatisticalResultDTO.PARAMETER_STABLE_ID + "," + StatisticalResultDTO.MARKER_ACCESSION_ID;
		SolrQuery q = new SolrQuery().setQuery(ObservationDTO.SEX + ":" + sex.name());
		q.setFilterQueries( StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" +
				StringUtils.join(OverviewChartsConstants.B6N_STRAINS, "\" OR " + ObservationDTO.STRAIN_ACCESSION_ID + ":\"") + "\"");
		q.set("facet.pivot", pivotFacet);
		q.setFacet(true);
		q.setRows(1);
		q.set("facet.limit", -1);

		logger.info("Solr url for getParameterToGeneMap " + SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + q);
		QueryResponse response = statisticalResultCore.query(q);

		for( PivotField pivot : response.getFacetPivot().get(pivotFacet)){
			if (pivot.getPivot() != null) {
				List<String> genes = new ArrayList<>();
				for (PivotField gene : pivot.getPivot()){
					genes.add(gene.getValue().toString());
				}
				res.put(pivot.getValue().toString(), new ArrayList<String>(genes));
			}
		}

		return res;
	}

	public List<Group> getGenesBy(String mpId, SexType sex)
			throws SolrServerException, IOException  {

		SolrQuery q = new SolrQuery().setQuery("(" + StatisticalResultDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " +
				StatisticalResultDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\" OR " +
				StatisticalResultDTO.MP_TERM_ID_OPTIONS + ":\"" + mpId + "\" OR " +
				StatisticalResultDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + mpId + "\")")
				.setRows(10000)
				.setSort(StatisticalResultDTO.DOCUMENT_ID, SolrQuery.ORDER.asc);
		q.set("group.field", "" + StatisticalResultDTO.MARKER_SYMBOL);
		q.set("group", true);
		q.set("group.limit", 0);

		if (sex != null) {
			q.addFilterQuery("(" + StatisticalResultDTO.PHENOTYPE_SEX + ":" + sex.getName() + " OR " + StatisticalResultDTO.SEX + ":" + sex.getName() + ")");
		}
		
		System.out.println("Query: " + q);

		QueryResponse results = statisticalResultCore.query(q);
		
		System.out.println("Results: " + results.getGroupResponse().getValues().get(0).getValues().size());
		
		return results.getGroupResponse().getValues().get(0).getValues();
	}

	public List<StatisticalResultDTO> getImpcPvalues() throws SolrServerException, IOException  {
		SolrQuery q = new SolrQuery("*:*")
				.addFilterQuery(StatisticalResultDTO.STATUS + ":Success")
				.addFilterQuery(StatisticalResultDTO.RESOURCE_NAME + ":(IMPC OR 3i)")
				.addField(StatisticalResultDTO.ALLELE_SYMBOL)
				.addField(StatisticalResultDTO.COLONY_ID)
				.addField(StatisticalResultDTO.MARKER_SYMBOL)
				.addField(StatisticalResultDTO.MARKER_ACCESSION_ID)
				.addField(StatisticalResultDTO.ZYGOSITY)
				.addField(StatisticalResultDTO.PHENOTYPING_CENTER)
				.addField(StatisticalResultDTO.PARAMETER_STABLE_ID)
				.addField(StatisticalResultDTO.PARAMETER_NAME)
				.addField(StatisticalResultDTO.P_VALUE)
				.setRows(Integer.MAX_VALUE)
				.setSort(StatisticalResultDTO.DOCUMENT_ID, SolrQuery.ORDER.asc);

		List<StatisticalResultDTO> result = statisticalResultCore.query(q).getBeans(StatisticalResultDTO.class);

		return result;
	}

	public List<StatisticalResultDTO> getImpcPvaluesAndMpTerms() throws SolrServerException, IOException  {
		SolrQuery q = new SolrQuery("*:*")
				.addFilterQuery(StatisticalResultDTO.STATUS + ":Success")
				.addFilterQuery(StatisticalResultDTO.RESOURCE_NAME + ":(IMPC OR 3i)")
				.addField(StatisticalResultDTO.ALLELE_SYMBOL)
				.addField(StatisticalResultDTO.COLONY_ID)
				.addField(StatisticalResultDTO.MARKER_SYMBOL)
				.addField(StatisticalResultDTO.MARKER_ACCESSION_ID)
				.addField(StatisticalResultDTO.ZYGOSITY)
				.addField(StatisticalResultDTO.MP_TERM_NAME)
				.addField(StatisticalResultDTO.PHENOTYPING_CENTER)
				.addField(StatisticalResultDTO.PARAMETER_STABLE_ID)
				.addField(StatisticalResultDTO.PROCEDURE_STABLE_ID)
				.addField(StatisticalResultDTO.PARAMETER_NAME)
				.addField(StatisticalResultDTO.P_VALUE)
				.setRows(Integer.MAX_VALUE)
				.setSort(StatisticalResultDTO.DOCUMENT_ID, SolrQuery.ORDER.asc);

		List<StatisticalResultDTO> result = statisticalResultCore.query(q).getBeans(StatisticalResultDTO.class);

		return result;
	}

	public Integer getParameterCountByGene(String acc) throws IOException, SolrServerException {
		SolrQuery query = new SolrQuery("p_value:[* TO *] AND marker_accession_id:\"" + acc + "\"");
		query.add("group", "true");
		query.add("group.ngroups", "true");
		query.add("group.field", StatisticalResultDTO.PARAMETER_NAME);
		QueryResponse result = statisticalResultCore.query(query);
		return result.getGroupResponse().getValues().get(0).getNGroups();
	}

	class OverviewRatio {
		Double meanFControl;
		Double meanFMutant;
		Double meanMControl;
		Double meanMMutant;

		Double nFControl;
		Double nFMutant;
		Double nMControl;
		Double nMMutant;

		public OverviewRatio(){
			meanFControl = (double) 0;
			meanFMutant = (double) 0;
			meanMMutant = (double) 0;
			meanMControl = (double) 0;

			nFControl = (double) 0;
			nFMutant = (double) 0;
			nMControl = (double) 0;
			nMMutant = (double) 0;
		}

		public void add (	Double meanFControl, Double meanFMutant, Double meanMControl, Double meanMMutant, Double nFControl, Double nFMutant, Double nMControl, Double nMMutant){

			this.meanFControl += meanFControl;
			this.meanFMutant += meanFMutant;
			this.meanMMutant += meanMMutant;
			this.meanMControl += meanMControl;

			this.nFControl += nFControl;
			this.nFMutant += nFMutant;
			this.nMControl += nMControl;
			this.nMMutant += nMMutant;
		}

		public void add (SolrDocument doc){

			if (doc.containsKey(StatisticalResultDTO.FEMALE_CONTROL_MEAN)){
				this.meanFControl += Double.parseDouble(doc.getFieldValue(StatisticalResultDTO.FEMALE_CONTROL_MEAN).toString());
			}
			if (doc.containsKey(StatisticalResultDTO.FEMALE_MUTANT_MEAN)){
				this.meanFMutant += Double.parseDouble(doc.getFieldValue(StatisticalResultDTO.FEMALE_MUTANT_MEAN).toString());
			}
			if (doc.containsKey(StatisticalResultDTO.MALE_MUTANT_MEAN)){
				this.meanMMutant += Double.parseDouble(doc.get(StatisticalResultDTO.MALE_MUTANT_MEAN).toString());
			}
			if (doc.containsKey(StatisticalResultDTO.MALE_CONTROL_MEAN)){
				this.meanMControl += Double.parseDouble(doc.get(StatisticalResultDTO.MALE_CONTROL_MEAN).toString());
			}
			if (doc.containsKey(StatisticalResultDTO.FEMALE_CONTROL_COUNT)){
				this.nFControl += Double.parseDouble(doc.get(StatisticalResultDTO.FEMALE_CONTROL_COUNT).toString());
			}
			if (doc.containsKey(StatisticalResultDTO.FEMALE_MUTANT_COUNT)){
				this.nFMutant += Double.parseDouble(doc.get(StatisticalResultDTO.FEMALE_MUTANT_COUNT).toString());
			}
			if (doc.containsKey(StatisticalResultDTO.MALE_CONTROL_COUNT)){
				this.nMControl += Double.parseDouble(doc.get(StatisticalResultDTO.MALE_CONTROL_COUNT).toString());
			}
			if (doc.containsKey(StatisticalResultDTO.MALE_MUTANT_COUNT)){
				this.nMMutant += Double.parseDouble(doc.get(StatisticalResultDTO.MALE_MUTANT_COUNT).toString());
			}
		}


		public Double getPlotRatio(String sexToDisplay){

			Double ratio = null;

			if (sexToDisplay.equalsIgnoreCase("both") ) {
				if (nMMutant > 0 && nFMutant > 0){
					Double totalMutant = nFMutant + nMMutant;
					Double ratioMale = nMMutant / totalMutant;
					Double ratioFemale = nFMutant / totalMutant;
					ratio =  meanFMutant * ratioFemale + meanMMutant * ratioMale;

					totalMutant = nFControl	+ nMControl;
					ratioMale = nMControl / totalMutant;
					ratioFemale = nFControl / totalMutant;
					ratio = ratio/(meanFControl * ratioFemale + meanMControl * ratioMale);
				}
			} else if (sexToDisplay.equalsIgnoreCase(SexType.male.getName())){
				if (nMMutant > 0){
					ratio = meanMMutant / meanMControl;
				}
			} else if (sexToDisplay.equalsIgnoreCase(SexType.female.getName())) {
				if (nFMutant > 0){
					ratio = meanFMutant / meanFControl;
				}
			}

			return ratio;
		}
	}


	/**
	 *
	 * @param mpId
	 * @return List of stable ids for parameters that led to at least one association to the
	 * given parameter or some class in its subtree
	 * @throws SolrServerException, IOException
	 * @author tudose
	 */
	public List<String> getParametersForPhenotype(String mpId)
			throws SolrServerException, IOException  {

		List<String> res = new ArrayList<>();
		SolrQuery q = new SolrQuery().setQuery("(" + StatisticalResultDTO.MP_TERM_ID + ":\"" + mpId + "\" OR " +
				StatisticalResultDTO.TOP_LEVEL_MP_TERM_ID + ":\"" + mpId + "\" OR " +
				StatisticalResultDTO.INTERMEDIATE_MP_TERM_ID + ":\"" + mpId + "\") AND (" +
				StatisticalResultDTO.STRAIN_ACCESSION_ID + ":\"" + StringUtils.join(OverviewChartsConstants.B6N_STRAINS, "\" OR " +
				GenotypePhenotypeDTO.STRAIN_ACCESSION_ID + ":\"") + "\")").setRows(0);
		q.set("facet.field", "" + StatisticalResultDTO.PARAMETER_STABLE_ID);
		q.set("facet", true);
		q.set("facet.limit", -1);
		q.set("facet.mincount", 1);

		QueryResponse response = statisticalResultCore.query(q);

		for (Count parameter : response.getFacetField(StatisticalResultDTO.PARAMETER_STABLE_ID).getValues()) {
			res.add(parameter.getName());
		}

		return res;
	}

	@Override
	public long getWebStatus() throws SolrServerException, IOException  {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(statisticalResultCore) + "/select?" + query);

		QueryResponse response = statisticalResultCore.query(query);
		return response.getResults().getNumFound();
	}

	@Override
	public String getServiceName() {
		return "statistical result service";
	}
}
