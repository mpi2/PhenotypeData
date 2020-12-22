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

import org.mousephenotype.cda.db.pojo.AnalyticsSignificantCallsProcedures;
import org.mousephenotype.cda.db.pojo.MetaInfo;
import org.mousephenotype.cda.db.repositories.AnalyticsSignificantCallsProceduresRepository;
import org.mousephenotype.cda.db.repositories.MetaHistoryRepository;
import org.mousephenotype.cda.db.repositories.MetaInfoRepository;
import org.mousephenotype.cda.dto.AggregateCountXY;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.Allele2Service;
import org.mousephenotype.cda.solr.service.PhenodigmService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.LifeStageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONObject;
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
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
	private AnalyticsSignificantCallsProceduresRepository analyticsSignificantCallsProceduresRepository;
	private UnidimensionalChartAndTableProvider           chartProvider;
	private MetaHistoryRepository                         metaHisoryRepository;
	private MetaInfoRepository                            metaInfoRepository;
	private PhenodigmService                              phenodigmService;
	private StatisticalResultService                      statisticalResultService;


	@Inject
	public ReleaseController(
			@NotNull Allele2Service allele2Service,
			@NotNull AnalyticsSignificantCallsProceduresRepository analyticsSignificantCallsProceduresRepository,
			@NotNull UnidimensionalChartAndTableProvider chartProvider,
			@NotNull MetaHistoryRepository metaHisoryRepository,
			@NotNull MetaInfoRepository metaInfoRepository,
			@NotNull PhenodigmService phenodigmService,
			@NotNull StatisticalResultService statisticalResultService)
	{
		this.allele2Service = allele2Service;
		this.analyticsSignificantCallsProceduresRepository = analyticsSignificantCallsProceduresRepository;
		this.chartProvider = chartProvider;
		this.metaHisoryRepository = metaHisoryRepository;
		this.metaInfoRepository = metaInfoRepository;
		this.phenodigmService = phenodigmService;
		this.statisticalResultService = statisticalResultService;
	}


	/**
	 * Force update meta info cache every fifteen minutes
	 */
	@Scheduled(cron = "0 0/15 * * * *")
	void updateCacheTimer() {
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
	 */
	private Map<String, String> getMetaInfo() {
		Map<String, String> metaInfo = cachedMetaInfo;

		if (metaInfo == null || Math.random() < CACHE_REFRESH_PERCENT) {
			metaInfo =
					StreamSupport
							.stream(metaInfoRepository.findAll().spliterator(), false)
							.collect(Collectors.toMap(MetaInfo::getPropertyKey, MetaInfo::getPropertyValue));

			// The front end will check for the key
			// "unique_mouse_model_disease_associations" in the map,
			// If not there, do not display the count
//			final Integer diseaseAssociationCount = phenodigmService.getDiseaseAssociationCount();
//			if (diseaseAssociationCount != null) {
//				metaInfo.put("unique_mouse_model_disease_associations", diseaseAssociationCount.toString());
//			}

			synchronized (this) {
				cachedMetaInfo = metaInfo;
			}
			logger.info("Refreshing metadata cache");
		}

		return metaInfo;
	}

	@RequestMapping(value = "/release.json", method = RequestMethod.GET)
	public ResponseEntity<String> getJsonReleaseInformation() {

		Map<String, String> metaInfo = getMetaInfo();
		JSONObject          json     = new JSONObject(metaInfo);
		return new ResponseEntity<>(json.toString(), createResponseHeaders(), HttpStatus.OK);
	}

	private HttpHeaders createResponseHeaders() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);
		return responseHeaders;
	}

	@RequestMapping(value = "/release/web")
	public String getWebRelease() {
		return "webRelease";
	}

	@RequestMapping(value = "/release", method = RequestMethod.GET)
	public String getReleaseInformation(Model model) {

		// 10% of the time refresh the cached metadata info
		Map<String, String> metaInfo = getMetaInfo();

		/*
		 * Phenotyping centers?
		 */
		String sCenters = metaInfo.get("phenotyped_lines_centers");
		List<String> phenotypingCenters = Stream.of(sCenters.split(","))
				.map(String::trim)
				.collect(Collectors.toList());

		/*
		 * Data types
		 */
		String sDataTypes = metaInfo.get("datapoint_types");
		List<String> dataTypes = Stream.of(sDataTypes.split(","))
				.map(String::trim)
				.collect(Collectors.toList());

		/*
		 * QC types
		 */
		List<String> qcTypes = Arrays.asList("QC_passed");

		/*
		 * Targeted allele types
		 */
		String sAlleleTypes = metaInfo.get("targeted_allele_types");
		List<String> alleleTypes = Stream.of(sAlleleTypes.split(","))
				.map(String::trim)
				.collect(Collectors.toList());

		/*
		 * Helps to generate graphs
		 */
		AnalyticsChartProvider chartsProvider = new AnalyticsChartProvider();

		List<AggregateCountXY> callBeans = getAllProcedurePhenotypeCalls();

		String callProcedureChart = chartsProvider.generateAggregateCountByProcedureChart(callBeans,
				"Number of Phenotype Calls by Procedure",
				"Further categorized by Embryo, Late Adult, and Early Adult",
				"Number of phenotype calls", "calls",
				"callProcedureChart", "checkAllPhenCalls", "uncheckAllPhenCalls");

		/*
		 * Get Historical trends release by release
		 */
		List<String> allDataReleaseVersions = metaHisoryRepository.getAllDataReleaseVersionsCastAsc();

		List<String> trendsVariables = Arrays.asList("statistically_significant_calls", "phenotyped_genes", "phenotyped_lines" );
		Map<String, List<AggregateCountXY>> trendsMap = new HashMap<>();
		for (String trendsVariable : trendsVariables) {
			trendsMap.put(trendsVariable, getHistoricalData(trendsVariable));
		}

		String trendsChart = chartsProvider.generateHistoryTrendsChart(trendsMap, allDataReleaseVersions,
				"Genes/Mutant Lines/MP Calls", "By Data Release", "Genes/Mutant Lines", "Phenotype Calls", true,
				"trendsChart", null, null);

		Map<String, List<AggregateCountXY>> datapointsTrendsMap = new HashMap<>();
		List<String> status = Arrays.asList("QC_passed");

		for (String dataType : dataTypes) {
			for (String s : status) {
				String propertyKey = dataType + "_datapoints_" + s;
				List<AggregateCountXY> dataPoints = getHistoricalData(propertyKey);
				datapointsTrendsMap.put(propertyKey, dataPoints);
			}
		}

		String datapointsTrendsChart = chartsProvider.generateHistoryTrendsChart(datapointsTrendsMap, allDataReleaseVersions,
				"Data Points by Data Release", "", "Data Points", null, false, "datapointsTrendsChart", "checkAllDataPoints",
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
				"Distribution of Phenotype Associations by Top-level MP Term", "", "Number of genotype-phenotype associations",
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

		/*
		 * Get all former releases: releases but the current one
		 */
		List<String> releases = metaHisoryRepository.getAllDataReleaseVersionsBeforeSpecified(metaInfo.get("data_release_version"));

		model.addAttribute("metaInfo", metaInfo);
		model.addAttribute("releases", releases);
		model.addAttribute("phenotypingCenters", phenotypingCenters);
		model.addAttribute("dataTypes", dataTypes);
		model.addAttribute("qcTypes", qcTypes);
		model.addAttribute("alleleTypes", alleleTypes);
		model.addAttribute("statisticalMethodsShortName", statisticalMethodsShortName);
		model.addAttribute("callProcedureChart", callProcedureChart);
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

		return null;
	}

	@RequestMapping(value = "/release_notes/{releaseVersion}.html", method = RequestMethod.GET)
	public String remapLegacyPastReleasesInformation(@PathVariable String releaseVersion,
													 HttpServletRequest request) {
		// Remap legacy request to new format
		String tmp = releaseVersion.replace("IMPC_Release_Notes_", "");
		tmp = tmp.replace(".html", "");
		Double d = new CommonUtils().tryParseDouble(tmp);

		String url =  "http:" + request.getAttribute("mappedHostname").toString()
				+ request.getAttribute("baseUrl").toString()
				+ "/previous-releases/" + d;
		return "redirect:" + url;
	}

	@RequestMapping(value = "/previous-releases/{releaseVersion}", method = RequestMethod.GET)
	public String getPastReleasesInformation(Model model, @PathVariable String releaseVersion) {

		/*
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
				.map(aggregate -> new AggregateCountXY(
						CommonUtils.tryParseInt(aggregate.getPropertyValue()),
						aggregate.getPropertyKey(),
						aggregate.getPropertyKey(),
						null,
						aggregate.getDataReleaseVersion(),
						aggregate.getDataReleaseVersion(),
						null))
		.collect(Collectors.toList());
	}

	private List<AggregateCountXY> getAllProcedurePhenotypeCalls() {
		Iterable<AnalyticsSignificantCallsProcedures> all = analyticsSignificantCallsProceduresRepository.findAll();
		List<AggregateCountXY> data =
			StreamSupport
				.stream(all.spliterator(), false)
				.map(ascp -> new AggregateCountXY(
					Math.toIntExact(ascp.getSignificantCalls()),
					ascp.getProcedureName(),
					"procedure",
					ascp.getProcedureStableId(),
					getAllProcedurePhenotypeCallsYvalue(ascp),
					"nb of calls",
					null))
				.collect(Collectors.toList());

		// Sort by Embryo (life stage, then procedure alphabetic)
		List<AggregateCountXY> embryoData = data
		    .stream()
			.filter((ac -> ac.getyValue().equalsIgnoreCase("Embryo")))
			.sorted(
				Comparator.comparing((AggregateCountXY axy) -> LifeStageMapper.getLifeStage(axy.getxAttribute()).ordinal())
				.thenComparing(AggregateCountXY::getxValue)
			)
			.collect(Collectors.toList());

		// Sort by everything NOT Embryo (alphabetic)
		List<AggregateCountXY> nonEmbryoData = data
			.stream()
			.filter((ac -> ! ac.getyValue().equalsIgnoreCase("Embryo")))
			.sorted(Comparator.comparing(AggregateCountXY::getxValue))
			.collect(Collectors.toList());
		embryoData.addAll(nonEmbryoData);
		return embryoData;
	}

	private String getAllProcedurePhenotypeCallsYvalue(AnalyticsSignificantCallsProcedures ascp) {
		if  ((ascp.getProcedureName().contains("E9.5"))
			|| (ascp.getProcedureName().contains("E12.5"))
			|| (ascp.getProcedureName().contains("E15.5"))
			|| (ascp.getProcedureName().contains("E18.5"))) {
			return "Embryo";
		} else if (ascp.getProcedureStableId().contains("LA_")) {
			return "Late Adult";
		} else {
			return "Early Adult";
		}
	}
}
