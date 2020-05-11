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

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONObject;
import org.mousephenotype.cda.db.pojo.AggregateCountXY;
import org.mousephenotype.cda.db.pojo.MetaInfo;
import org.mousephenotype.cda.db.pojo.UniqueDatatypeAndStatisticalMethod;
import org.mousephenotype.cda.db.repositories.AnalyticsPvalueDistributionRepository;
import org.mousephenotype.cda.db.repositories.AnalyticsSignificantCallsProceduresRepository;
import org.mousephenotype.cda.db.repositories.MetaHistoryRepository;
import org.mousephenotype.cda.db.repositories.MetaInfoRepository;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.Allele2Service;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.PhenodigmService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.ac.ebi.phenotype.chart.AnalyticsChartProvider;
import uk.ac.ebi.phenotype.chart.UnidimensionalChartAndTableProvider;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
public class ReleaseController {

	private final Logger logger = LoggerFactory.getLogger(ReleaseController.class);

	private final  Double              CACHE_REFRESH_PERCENT       = 0.05; // 5%
	private        Map<String, String> cachedMetaInfo              = null;
	private static Map<String, String> statisticalMethodsShortName = new HashMap<>();
	static {
		statisticalMethodsShortName.put("Fisher's exact test", "Fisher");
		statisticalMethodsShortName.put("Wilcoxon rank sum test with continuity correction", "Wilcoxon");
		statisticalMethodsShortName.put("Mixed Model framework, generalized least squares, equation withoutWeight",
										"MMgls");
		statisticalMethodsShortName.put("Mixed Model framework, linear mixed-effects model, equation withoutWeight",
										"MMlme");
	}


	private Allele2Service                                allele2Service;
	private AnalyticsPvalueDistributionRepository         analyticsPvalueDistributionRepository;
	private AnalyticsSignificantCallsProceduresRepository analyticsSignificantCallsProceduresRepository;
	private UnidimensionalChartAndTableProvider           chartProvider;
	private MetaHistoryRepository                         metaHisoryRepository;
	private MetaInfoRepository                            metaInfoRepository;
	private ObservationService                            observationService;
	private PhenodigmService                              phenodigmService;
	private StatisticalResultService                      statisticalResultService;


	@Inject
	public ReleaseController(
			@NotNull Allele2Service allele2Service,
			@NotNull AnalyticsPvalueDistributionRepository analyticsPvalueDistributionRepository,
			@NotNull AnalyticsSignificantCallsProceduresRepository analyticsSignificantCallsProceduresRepository,
			@NotNull UnidimensionalChartAndTableProvider chartProvider,
			@NotNull MetaHistoryRepository metaHisoryRepository,
			@NotNull MetaInfoRepository metaInfoRepository,
			@NotNull ObservationService observationService,
			@NotNull PhenodigmService phenodigmService,
			@NotNull StatisticalResultService statisticalResultService)
	{
		this.allele2Service = allele2Service;
		this.analyticsPvalueDistributionRepository = analyticsPvalueDistributionRepository;
		this.analyticsSignificantCallsProceduresRepository = analyticsSignificantCallsProceduresRepository;
		this.chartProvider = chartProvider;
		this.metaHisoryRepository = metaHisoryRepository;
		this.metaInfoRepository = metaInfoRepository;
		this.observationService = observationService;
		this.phenodigmService = phenodigmService;
		this.statisticalResultService = statisticalResultService;
	}


	/**
	 * Force update meta info cache every fifteen minutes
	 * @throws SQLException when the database is not available
	 */
	@Scheduled(cron = "0 0/15 * * * *")
	private void updateCacheTimer() throws SQLException {
		logger.info("Cache timeout expired. Clearing metadata cache");
		cachedMetaInfo = null;
		getMetaInfo();
	}

	/**
	 * Return the meta information about the data release
	 *
	 * If the data is cached, return the cached data
	 *
	 * Sometimes (defined by CACHE_REFRESH_PERCENT), refresh the cached data
	 *
	 * @return map of the meta data
	 * @throws SQLException
	 */
	private Map<String, String> getMetaInfo() throws SQLException {
		Map<String, String> metaInfo = cachedMetaInfo;

		if (metaInfo == null || Math.random() < CACHE_REFRESH_PERCENT) {
			metaInfo =
					StreamSupport
							.stream(metaInfoRepository.findAll().spliterator(), false)
							.collect(Collectors.toMap(MetaInfo::getPropertyKey, MetaInfo::getPropertyValue));

			// The front end will check for the key
			// "unique_mouse_model_disease_associations" in the map,
			// If not there, do not display the count
			final Integer diseaseAssociationCount = phenodigmService.getDiseaseAssociationCount();
			if (diseaseAssociationCount != null) {
				metaInfo.put("unique_mouse_model_disease_associations", diseaseAssociationCount.toString());
			}

			synchronized (this) {
				cachedMetaInfo = metaInfo;
			}
			logger.info("Refreshing metadata cache");
		}

		return metaInfo;
	}

	@RequestMapping(value = "/release.json", method = RequestMethod.GET)
	public ResponseEntity<String> getJsonReleaseInformation() {

		try {

			// 10% of the time refresh the cached metadata info
			Map<String, String> metaInfo = getMetaInfo();

			JSONObject json = new JSONObject(metaInfo);

			return new ResponseEntity<>(json.toString(), createResponseHeaders(), HttpStatus.OK);
		} catch (SQLException e) {
			e.printStackTrace();
			return new ResponseEntity<>("Error retreiving release information", createResponseHeaders(),
					HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

	private HttpHeaders createResponseHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}

	@RequestMapping(value = "/release/web")
	public String getWebRelease(Model model) {
		return "webRelease";
	}

	@RequestMapping(value = "/release", method = RequestMethod.GET)
	public String getReleaseInformation(Model model)
			throws SolrServerException, IOException, URISyntaxException, SQLException {

		// 10% of the time refresh the cached metadata info
		Map<String, String> metaInfo = getMetaInfo();

		/*
		 * What are the different Phenotyping centers?
		 */

		String sCenters = metaInfo.get("phenotyped_lines_centers");
		String[] phenotypingCenters = sCenters.split(",");

		/*
		 * Data types
		 */

		String sDataTypes = metaInfo.get("datapoint_types");
		String[] dataTypes = sDataTypes.split(",");

		/*
		 * QC types
		 */
		String[] qcTypes = new String[] { "QC_passed", "QC_failed", "issues" };

		/*
		 * Targeted allele types
		 */

		String sAlleleTypes = metaInfo.get("targeted_allele_types");
		String[] alleleTypes = sAlleleTypes.split(",");

		/*
		 * Helps to generate graphs
		 */
		AnalyticsChartProvider chartsProvider = new AnalyticsChartProvider();

		List<AggregateCountXY> callBeans = getAllProcedurePhenotypeCalls();

		String callProcedureChart = chartsProvider.generateAggregateCountByProcedureChart(callBeans,
				"Phenotype calls per procedure", "Center by center", "Number of phenotype calls", "calls",
				"callProcedureChart", "checkAllPhenCalls", "uncheckAllPhenCalls");

		Map<String, List<String>> statisticalMethods =
				analyticsPvalueDistributionRepository.getAllStatisticalMethods(UniqueDatatypeAndStatisticalMethod.class)
						.stream()
						.collect(
								Collectors.groupingBy(
										UniqueDatatypeAndStatisticalMethod::getDatatype,
										Collectors.mapping(
												UniqueDatatypeAndStatisticalMethod::getStatisticalMethod,
												Collectors.toList())));

		/**
		 * Generate pValue distribution graph for all methods
		 */
		Map<String, String> distributionCharts = new HashMap<>();

		for (String dataType : statisticalMethods.keySet()) {
			for (String statisticalMethod : statisticalMethods.get(dataType)) {
				List<AggregateCountXY> distribution = getAggregates(dataType, statisticalMethod);
				String chart = chartsProvider.generateAggregateCountByProcedureChart(distribution,
																					 "P-value distribution", statisticalMethod, "Frequency", "",
																					 statisticalMethodsShortName.get(statisticalMethod) + "Chart", "xxx", "xxx");
				distributionCharts.put(statisticalMethodsShortName.get(statisticalMethod) + "Chart", chart);
			}
		}

		/**
		 * Get Historical trends release by release
		 */
		List<String> allDataReleaseVersions = metaHisoryRepository.getAllDataReleaseVersionsCastAsc();

		String[] trendsVariables = new String[] { "statistically_significant_calls", "phenotyped_genes",
				"phenotyped_lines" };
		Map<String, List<AggregateCountXY>> trendsMap = new HashMap<>();
		for (int i = 0; i < trendsVariables.length; i++) {
			trendsMap.put(trendsVariables[i], getHistoricalData(trendsVariables[i]));
		}

		String trendsChart = chartsProvider.generateHistoryTrendsChart(trendsMap, allDataReleaseVersions,
				"Genes/Mutant Lines/MP Calls", "Release by Release", "Genes/Mutant Lines", "Phenotype Calls", true,
				"trendsChart", null, null);

		Map<String, List<AggregateCountXY>> datapointsTrendsMap = new HashMap<>();
		String[]                            status              = new String[] { "QC_passed", "QC_failed", "issues" };

		for (int i = 0; i < dataTypes.length; i++) {
			for (int j = 0; j < status.length; j++) {
				String                 propertyKey = dataTypes[i] + "_datapoints_" + status[j];
				List<AggregateCountXY> dataPoints  = getHistoricalData(propertyKey);
				// if (beans.size() > 0) {
				datapointsTrendsMap.put(propertyKey, dataPoints);
				// }
			}
		}

		String datapointsTrendsChart = chartsProvider.generateHistoryTrendsChart(datapointsTrendsMap, allDataReleaseVersions,
				"Data points", "", "Data points", null, false, "datapointsTrendsChart", "checkAllDataPoints",
				"uncheckAllDataPoints");

		TreeMap<String, TreeMap<String, Long>> annotationDistribution = new TreeMap<>();
		annotationDistribution.put(ZygosityType.heterozygote.getName(),
								   statisticalResultService.getDistributionOfAnnotationsByMPTopLevel(ZygosityType.heterozygote, null));
		annotationDistribution.put(ZygosityType.homozygote.getName(),
								   statisticalResultService.getDistributionOfAnnotationsByMPTopLevel(ZygosityType.homozygote, null));
		annotationDistribution.put(ZygosityType.hemizygote.getName(),
								   statisticalResultService.getDistributionOfAnnotationsByMPTopLevel(ZygosityType.hemizygote, null));
		String annotationDistributionChart = chartsProvider.generateAggregateCountByProcedureChart(
				statisticalResultService.getAggregateCountXYBean(annotationDistribution),
				"Distribution of Phenotype Associations in IMPC", "", "Number of genotype-phenotype associations",
				" lines", "distribution", null, null);

		Set<String> allPhenotypingCenters = allele2Service.getFacets(Allele2DTO.LATEST_PHENOTYPING_CENTRE);
		TreeMap<String, TreeMap<String, Long>> phenotypingDistribution = new TreeMap<>();
		for (String center : allPhenotypingCenters) {
			if (!center.equals("")) {
				phenotypingDistribution.put(center,
											allele2Service.getStatusCountByPhenotypingCenter(center, Allele2DTO.LATEST_PHENOTYPE_STATUS));
			}
		}
		String phenotypingDistributionChart = chartsProvider.generateAggregateCountByProcedureChart(
				statisticalResultService.getAggregateCountXYBean(phenotypingDistribution), "Phenotyping Status by Center", "",
				"Number of Genes", " genes", "phenotypeStatusByCenterChart", "checkAllPhenByCenter",
				"uncheckAllPhenByCenter");

		Set<String> allGenotypingCenters = allele2Service.getFacets(Allele2DTO.LATEST_PRODUCTION_CENTRE);
		TreeMap<String, TreeMap<String, Long>> genotypingDistribution = new TreeMap<>();
		for (String center : allGenotypingCenters) {
			if (!center.equals("")) {
				genotypingDistribution.put(center,
										   allele2Service.getStatusCountByProductionCenter(center, Allele2DTO.LATEST_MOUSE_STATUS));
			}
		}
		String genotypingDistributionChart = chartsProvider.generateAggregateCountByProcedureChart(
				statisticalResultService.getAggregateCountXYBean(genotypingDistribution), "Genotyping Status by Center", "",
				"Number of Genes", " genes", "genotypeStatusByCenterChart", "checkAllGenByCenter",
				"uncheckAllGenByCenter");

		HashMap<String, Integer> fertilityDistrib = getFertilityMap();

		/**
		 * Get all former releases: releases but the current one
		 */
		List<String> releases = metaHisoryRepository.getAllDataReleaseVersionsBeforeSpecified(metaInfo.get("data_release_version"));

		model.addAttribute("metaInfo", metaInfo);
		model.addAttribute("releases", releases);
		model.addAttribute("phenotypingCenters", phenotypingCenters);
		model.addAttribute("dataTypes", dataTypes);
		model.addAttribute("qcTypes", qcTypes);
		model.addAttribute("alleleTypes", alleleTypes);
		model.addAttribute("statisticalMethods", statisticalMethods);
		model.addAttribute("statisticalMethodsShortName", statisticalMethodsShortName);
		model.addAttribute("callProcedureChart", callProcedureChart);
		model.addAttribute("distributionCharts", distributionCharts);
		model.addAttribute("trendsChart", trendsChart);
		model.addAttribute("datapointsTrendsChart", datapointsTrendsChart);
		model.addAttribute("annotationDistributionChart", annotationDistributionChart);
		model.addAttribute("genotypeStatusChart",
				chartProvider.getStatusColumnChart(allele2Service.getStatusCount(null, Allele2DTO.LATEST_MOUSE_STATUS),
												   "Genotyping Status", "genotypeStatusChart", null));
		model.addAttribute("phenotypeStatusChart",
				chartProvider.getStatusColumnChart(allele2Service.getStatusCount(null, Allele2DTO.LATEST_PHENOTYPE_STATUS),
												   "Phenotyping Status", "phenotypeStatusChart", null));
		model.addAttribute("phenotypingDistributionChart", phenotypingDistributionChart);
		model.addAttribute("genotypingDistributionChart", genotypingDistributionChart);
		model.addAttribute("fertilityMap", fertilityDistrib);

		return null;
	}

	@RequestMapping(value = "/release_notes/{releaseVersion}.html", method = RequestMethod.GET)
	public String remapLegacyPastReleasesInformation(Model model, @PathVariable String releaseVersion) throws SQLException {
		// Remap legacy request to new format
		String tmp = releaseVersion.replace("IMPC_Release_Notes_", "");
		tmp = tmp.replace(".html", "");
		Double d = new CommonUtils().tryParseDouble(tmp);
		return "redirect:/previous-releases/" + Double.toString(d);
	}

	@RequestMapping(value = "/previous-releases/{releaseVersion}", method = RequestMethod.GET)
	public String getPastReleasesInformation(Model model, @PathVariable String releaseVersion) throws SQLException {

		/**
		 * Get all previous releases
		 */
		List<String> previousReleases = metaHisoryRepository.getAllDataReleaseVersionsBeforeSpecified(releaseVersion);
		List<String> allReleases = metaHisoryRepository.getAllDataReleaseVersionsCastDesc();
		String currentRelease = allReleases.get(0);
		if (( ! releaseVersion.equals(currentRelease)) && (allReleases.contains(releaseVersion))) {
			model.addAttribute(releaseVersion);
			model.addAttribute("releases", previousReleases);

			return "previous_releases";
		}

		throw new RuntimeException("Page not found");
	}

	@RequestMapping(value = "/page-retired", method = RequestMethod.GET)
	public String pageRetired() {

		return "page_retired";
	}

	private List<AggregateCountXY> getHistoricalData(String propertyKey) {

		return metaHisoryRepository.getAllByPropertyKeyCastAsc(propertyKey)
				.stream()
				.map(aggregate -> {
					return new AggregateCountXY(
							CommonUtils.tryParseInt(aggregate.getPropertyValue()),
							aggregate.getPropertyKey(),
							aggregate.getPropertyKey(),
							null,
							aggregate.getDataReleaseVersion(),
							aggregate.getDataReleaseVersion(),
							null);
				})
		.collect(Collectors.toList());
	}


	private List<AggregateCountXY> getAggregates(String dataType, String statisticalMethod) {

		return analyticsPvalueDistributionRepository.getAllByDatatypeAndStatisticalMethodOrderByPvalueBinAsc(dataType, statisticalMethod)
				.stream()
				.map(dist -> {
					return new AggregateCountXY(
							dist.getPvalueCount(),
							Double.toString(dist.getPvalueBin()),
							"p-value",
							null,
							dist.getStatisticalMethod(),
							dist.getStatisticalMethod(),
							null);
				})
		.collect(Collectors.toList());
	}


	public HashMap<String, Integer> getFertilityMap() throws IOException, SolrServerException {

		List<String> resource = new ArrayList<>();
		resource.add("IMPC");
		Set<String> fertileColonies = observationService.getAllColonyIdsByResource(resource, true);
		Set<String> maleInfertileColonies = new HashSet<>();
		Set<String> femaleInfertileColonies = new HashSet<>();
		Set<String> bothSexesInfertileColonies;

		maleInfertileColonies = statisticalResultService.getAssociationsDistribution("male infertility", "IMPC").keySet();
		femaleInfertileColonies = statisticalResultService.getAssociationsDistribution("female infertility", "IMPC").keySet();

		bothSexesInfertileColonies = new HashSet<>(maleInfertileColonies);
		bothSexesInfertileColonies.retainAll(femaleInfertileColonies);
		fertileColonies.removeAll(maleInfertileColonies);
		fertileColonies.removeAll(femaleInfertileColonies);
		maleInfertileColonies.removeAll(bothSexesInfertileColonies);
		femaleInfertileColonies.removeAll(bothSexesInfertileColonies);

		HashMap<String, Integer> res = new HashMap<>();
		res.put("female infertile", femaleInfertileColonies.size());
		res.put("male infertile", maleInfertileColonies.size());
		res.put("both sexes infertile", bothSexesInfertileColonies.size());
		res.put("fertile", fertileColonies.size());

		return res;
	}

	private List<AggregateCountXY> getAllProcedurePhenotypeCalls() {

		return
				StreamSupport
						.stream(analyticsSignificantCallsProceduresRepository.findAll().spliterator(), false)
						.map(ascp -> new AggregateCountXY(
								Math.toIntExact(ascp.getSignificantCalls()),
								ascp.getProcedureStableId(),
								"procedure",
								null,
								ascp.getPhenotypingCenter(),
								"nb of calls",
								null))
						.collect(Collectors.toList());
	}
}
