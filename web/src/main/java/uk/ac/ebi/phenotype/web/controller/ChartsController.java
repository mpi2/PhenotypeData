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
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.dto.LifeStage;
import org.mousephenotype.cda.enumerations.EmbryoViability;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.service.dto.*;
import org.mousephenotype.cda.solr.service.exception.SpecificExperimentException;
import org.mousephenotype.cda.solr.web.dto.EmbryoViability_DTO;
import org.mousephenotype.cda.solr.web.dto.ViabilityDTO;
import org.mousephenotype.cda.utilities.LifeStageMapper;
import org.mousephenotype.cda.web.ChartType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.phenotype.chart.*;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.error.ParameterNotFoundException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.web.bind.annotation.ValueConstants.DEFAULT_NONE;


@Controller
public class ChartsController {

	private final Logger                              log = LoggerFactory.getLogger(this.getClass());
	private final CategoricalChartAndTableProvider    categoricalChartAndTableProvider;
	private final TimeSeriesChartAndTableProvider     timeSeriesChartAndTableProvider;
	private final UnidimensionalChartAndTableProvider continousChartAndTableProvider;
	private final ScatterChartAndTableProvider        scatterChartAndTableProvider;
	private final AbrChartAndTableProvider            abrChartAndTableProvider;
	private final ViabilityChartAndDataProvider       viabilityChartAndDataProvider;
	private final ExperimentService                   experimentService;
	private final StatisticalResultService            srService;
	private final GeneService                         geneService;
	private final ImageService                        imageService;
	private final ImpressService                      impressService;
	private final GenotypePhenotypeService            gpService;
    
    @Resource(name = "globalConfiguration")
    private Map<String, String> config;


    @Value("${solr_url}")
    public String SOLR_URL;


    @Inject
    public ChartsController(
			@NotNull CategoricalChartAndTableProvider categoricalChartAndTableProvider,
			@NotNull TimeSeriesChartAndTableProvider timeSeriesChartAndTableProvider,
			@NotNull UnidimensionalChartAndTableProvider continousChartAndTableProvider,
			@NotNull ScatterChartAndTableProvider scatterChartAndTableProvider,
			@NotNull AbrChartAndTableProvider abrChartAndTableProvider,
			@NotNull ViabilityChartAndDataProvider viabilityChartAndDataProvider,
			@NotNull ExperimentService experimentService,
			@NotNull GeneService geneService,
			@NotNull ImpressService impressService,
			@NotNull ImageService imageService,
			@NotNull @Named("statistical-result-service") StatisticalResultService srService,
			@NotNull @Named("genotype-phenotype-service") GenotypePhenotypeService gpService
			) {
        this.categoricalChartAndTableProvider = categoricalChartAndTableProvider;
        this.timeSeriesChartAndTableProvider = timeSeriesChartAndTableProvider;
        this.continousChartAndTableProvider = continousChartAndTableProvider;
        this.scatterChartAndTableProvider = scatterChartAndTableProvider;
        this.abrChartAndTableProvider = abrChartAndTableProvider;
        this.viabilityChartAndDataProvider = viabilityChartAndDataProvider;
        this.experimentService = experimentService;
        this.srService = srService;
        this.geneService = geneService;
        this.impressService = impressService;
        this.imageService=imageService;
        this.gpService = gpService;
	}

    /**
     * Runs when the request missing an accession ID. This redirects to the
     * search page which defaults to showing all genes in the list
     *
     * @return string to instruct spring to redirect to the search page
     */
    @RequestMapping("/stats")
    public String rootForward() {

        return "redirect:/search";
    }

    /**
     * This method should take in the parameters and then generate a skeleton
     * jsp page with urls that can be called by a jquery ajax requests for each
     * graph div and table div
     *
     * @param parameterIds
     * @param gender
     * @param zygosity
     * @param phenotypingCenter
     * @param strategies
     * @param accessionsParams
     * @param model
     * @return
     * @throws GenomicFeatureNotFoundException
     * @throws ParameterNotFoundException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException, IOException
     */

    @RequestMapping("/charts")
    public String charts(@RequestParam(required = false, value = "accession") String[] accessionsParams,
                         @RequestParam(required = false, value = "parameter_stable_id") String[] parameterIds,
                         @RequestParam(required = false, value = "gender") String[] gender,
                         @RequestParam(required = false, value = "zygosity") String[] zygosity,
                         @RequestParam(required = false, value = "phenotyping_center") String[] phenotypingCenter,
                         @RequestParam(required = false, value = "strategy") String[] strategies,
                         @RequestParam(required = false, value = "strain") String[] strains,
                         @RequestParam(required = false, value = "metadata_group") String[] metadataGroup,
                         @RequestParam(required = false, value = "chart_type") ChartType chartType,
                         @RequestParam(required = false, value = "pipeline_stable_id") String[] pipelineStableIds,
                         @RequestParam(required = false, value = "procedure_stable_id") String[] procedureStableIds,
                         @RequestParam(required = false, value = "allele_accession_id") String[] alleleAccession,
                         @RequestParam(required = false, value = "pageTitle") String pageTitle,
                         @RequestParam(required = false, value = "pageLinkBack") String pageLinkBack,
                         HttpServletRequest request, HttpServletResponse response,
                         Model model) {
        try {
            if ((accessionsParams != null) && (accessionsParams.length > 0) && (parameterIds != null) && (parameterIds.length > 0)) {
                for (String parameterStableId : parameterIds) {
                    if (parameterStableId.contains("_FER_")) {
                    	System.err.println("We don't have data for fertility so we can't display charts");
                        String url =  "http:" + request.getAttribute("mappedHostname").toString() + request.getAttribute("baseUrl").toString() + "/genes/" + accessionsParams[0];
                        return "redirect:" + url;
                    }
                }
            }
            response.addHeader("Access-Control-Allow-Origin", "*");//allow javascript requests from other domain - note spring way of doing this does not work!!!! as usual!!!

            model.addAttribute("pageTitle", pageTitle);

            return createCharts(accessionsParams, pipelineStableIds, procedureStableIds, parameterIds, gender, phenotypingCenter, strains, metadataGroup, zygosity, model, chartType, alleleAccession);
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }


    @RequestMapping("/chart")
    public String chart(@RequestParam(required = true, value = "experimentNumber", defaultValue = "1") String experimentNumber,
                        @RequestParam(required = false, value = "accession") String[] accession,
                        @RequestParam(required = false, value = "strain_accession_id") String strain,
                        @RequestParam(required = false, value = "allele_accession_id") String alleleAccession,
                        @RequestParam(required = false, value = "metadata_group", defaultValue = DEFAULT_NONE) String metadataGroup,
						@RequestParam(required = false, value = "pipeline_stable_id") String pipelineStableId,
						@RequestParam(required = false, value = "procedure_stable_id") String procedureStableId,
                        @RequestParam(required = false, value = "parameter_stable_id") String parameterStableId,
                        @RequestParam(required = false, value = "gender") String[] gender,
                        @RequestParam(required = false, value = "zygosity") String[] zygosity,
                        @RequestParam(required = false, value = "phenotyping_center") String phenotypingCenter,
                        @RequestParam(required = false, value = "strategy") String[] strategies,
                        @RequestParam(required = false, value = "chart_type") ChartType chartType,
                        @RequestParam(required = false, value = "chart_only", defaultValue = "false") boolean chartOnly,
                        @RequestParam(required = false, value = "standAlone") boolean standAlone,
                        @RequestParam(required = false, value = "fromFile") boolean fromFile,
                        Model model)
            throws IOException, URISyntaxException, SolrServerException, SpecificExperimentException, ParameterNotFoundException {

		if (StringUtils.isEmpty(parameterStableId)) {
			System.out.println("throwing parameter not found exception");
			throw new ParameterNotFoundException("Parameter " + parameterStableId + " can't be found.", parameterStableId);
		}

		if (!parameterStableId.equals("")) {
			boolean isDerivedBodyWeight = Constants.DERIVED_BODY_WEIGHT_PARAMETERS.contains(parameterStableId);
			model.addAttribute("isDerivedBodyWeight", isDerivedBodyWeight);
		}

		UnidimensionalDataSet      unidimensionalChartDataSet = null;
		ChartData                  seriesParameterChartData   = null;
		CategoricalResultAndCharts categoricalResultAndChart  = null;

		boolean statsError = false;

		if (parameterStableId.startsWith("IMPC_FER_")) {
			String url = config.get("baseUrl") + "/genes/" + accession[0];
			return "redirect:" + url;
		}

		// Use the first phenotyping center passed in (ignore the others?)
		// should only now be one center at this stage for one graph/experiment
		// TODO put length check and exception here
		// List<String> phenotypingCenters = getParamsAsList(phenotypingCenter);

		String       metadata     = null;
		List<String> metadataList = null;
		String metaDataGroupString = null;
		if (metadataGroup != null && !metadataGroup.equals(DEFAULT_NONE)) {
			metaDataGroupString = metadataGroup;
		}

		List<String> zyList = getParamsAsList(zygosity);

		ImpressBaseDTO pipeline = null;

		if (pipelineStableId != null && !pipelineStableId.equals("")) {
			log.debug("pipe stable id=" + pipelineStableId);
			pipeline = impressService.getPipeline(pipelineStableId);
			model.addAttribute("pipeline", pipeline);
			model.addAttribute("pipelineUrl", impressService.getPipelineUrlByStableKey(pipeline.getStableKey()));
		}

		model.addAttribute("phenotypingCenter", phenotypingCenter);

		ExperimentDTO experiment;

		GeneDTO gene = geneService.getGeneById(accession[0]);
		model.addAttribute("gene", gene);

		long startTimeSolr = System.currentTimeMillis();

		if (zygosity.length != 1) {
			log.warn("More than one zygosity specified", String.join(", ", zygosity));
		}

		experiment = experimentService.getSpecificExperimentDTO(
				pipelineStableId,
				procedureStableId,
				parameterStableId,
				alleleAccession,
				phenotypingCenter,
				zygosity[0],
				strain,
				metaDataGroupString
		);
		experiment = experimentService.setUrls(experiment, parameterStableId, pipelineStableId, gene.getMgiAccessionId(), Arrays.asList(zygosity), phenotypingCenter, strain, metadataGroup, alleleAccession, SOLR_URL);
		experiment.setMarkerAccession(gene.getMgiAccessionId());

		Set<SexType> sexes = new HashSet<>(Arrays.asList(SexType.male, SexType.female));
		if (experiment.getSexes() != null && ! experiment.getSexes().contains(SexType.not_considered)) {
			sexes = experiment.getSexes();
		}
		experiment.setSexes(sexes);

		if (experiment.getSexes().isEmpty()) {
			experiment.setSexes(Collections.singleton(SexType.not_considered));
		}

		long endTimeSolr   = System.currentTimeMillis();
		long timeTakenSolr = endTimeSolr - startTimeSolr;
		System.out.println("solr time taken to get experiment=" + timeTakenSolr);

		ProcedureDTO proc;
		ParameterDTO parameter = null;

		if (experiment != null) {
			String pipe = (experiment.getPipelineStableId()!= null) ? experiment.getPipelineStableId() : pipelineStableId;
			String procStableId = (experiment.getProcedureStableId()!= null) ? experiment.getProcedureStableId() : procedureStableId;
			proc = impressService.getProcedureByStableId(pipe, procStableId);

			String procedureUrl = "";
			String parameterUrl = "";
			if (proc != null) {
				procedureUrl = impressService.getProcedureUrlByStableKeyAndPipelineStableKey(proc.getStableKey(), pipeline.getStableKey());
				model.addAttribute("procedureUrl", procedureUrl);
			}

			parameter = impressService.getParameterByPipelineProcedureParameterStableKey(pipeline.getStableKey(), proc.getStableKey(), parameterStableId);
			model.addAttribute("parameter", parameter);

			//3i procedures with at least some headline images associated
			if (parameter.getStableId().startsWith("MGP_BMI") || parameter.getStableId().startsWith("MGP_MLN") || parameter.getStableId().startsWith("MGP_IMM")) {
				addFlowCytometryImages(accession, model, parameter);
			}

			String          xUnits                  = parameter.getUnitX();
			ObservationType observationTypeForParam = parameter.getObservationType();

			if (parameter.getStableKey() != null) {
				parameterUrl = impressService.getParameterUrlByProcedureAndParameterKey(proc.getStableKey(), parameter.getStableKey());
				model.addAttribute("parameterUrl", parameterUrl);
			}
			model.addAttribute("alleleSymbol", experiment.getAlleleSymbol());
			setTitlesForGraph(model, experiment.getGeneticBackgtround(), experiment.getAlleleSymbol());

			if (experiment.getMetadataGroup() != null) {
				metadata = experiment.getMetadataHtml();
				metadataList = experiment.getMetadata();
			}

			try {

				if (chartType != null) {

					ScatterChartAndData scatterChartAndData;

					switch (chartType) {

						case UNIDIMENSIONAL_SCATTER_PLOT:

							scatterChartAndData = scatterChartAndTableProvider.doScatterData(experiment, null, null, parameter, experimentNumber);
							model.addAttribute("scatterChartAndData", scatterChartAndData);

							if (observationTypeForParam.equals(ObservationType.unidimensional)) {
								List<UnidimensionalStatsObject> unidimenStatsObjects = scatterChartAndData.getUnidimensionalStatsObjects();
								unidimensionalChartDataSet = new UnidimensionalDataSet();
								unidimensionalChartDataSet.setStatsObjects(unidimenStatsObjects);
								model.addAttribute("unidimensionalChartDataSet", unidimensionalChartDataSet);
							}
							break;

						case UNIDIMENSIONAL_ABR_PLOT:

							seriesParameterChartData = abrChartAndTableProvider.getAbrChartAndData(experiment, parameter, "abrChart" + experimentNumber, SOLR_URL);
							model.addAttribute("abrChart", seriesParameterChartData.getChart());
							break;

						case UNIDIMENSIONAL_BOX_PLOT:

							try {
								unidimensionalChartDataSet = continousChartAndTableProvider.doUnidimensionalData(experiment, experimentNumber, parameter, xUnits);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							model.addAttribute("unidimensionalChartDataSet", unidimensionalChartDataSet);

							scatterChartAndData = scatterChartAndTableProvider.doScatterData(experiment, unidimensionalChartDataSet.getMin(), unidimensionalChartDataSet.getMax(), parameter, experimentNumber);
							model.addAttribute("scatterChartAndData", scatterChartAndData);

							break;

						case CATEGORICAL_STACKED_COLUMN:

							categoricalResultAndChart = categoricalChartAndTableProvider.doCategoricalData(experiment, parameter, experimentNumber);
							model.addAttribute("categoricalResultAndChart", categoricalResultAndChart);
							break;

						case TIME_SERIES_LINE:

							seriesParameterChartData = timeSeriesChartAndTableProvider.doTimeSeriesData(experiment, parameter, experimentNumber);
							model.addAttribute("timeSeriesChartsAndTable", seriesParameterChartData);
							break;

						default:

							log.error("Unknown how to display graph for observation type: " + observationTypeForParam);
							break;
					}
				} else {
					log.error("chart type is null");
				}

			} catch (SQLException e) {
				log.error(ExceptionUtils.getStackTrace(e));
				statsError = true;
			}
		} else {
			System.out.println("empty experiment");
			model.addAttribute("emptyExperiment", true);
		}

		if (procedureStableId.equals("IMPC_VIA_001")) {
			if (parameterStableId.startsWith("IMPC_VIA_")) {

				// IMPC VIA 001

				// Its a viability outcome param which means its a line level query
				// so we don't use the normal experiment query in experiment service
				ViabilityDTO viability = experimentService.getSpecificViabilityVersion1ExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession);
				ViabilityDTO viabilityDTO = viabilityChartAndDataProvider.doViabilityData(viability, parameterStableId);
				model.addAttribute("viabilityDTO", viabilityDTO);
			}
		} else if (procedureStableId.equals("IMPC_VIA_002")) {

			// IMPC VIA 002

			// Its a viability outcome param which means its a line level query
			// so we don't use the normal experiment query in experiment service
			ViabilityDTO viability = experimentService.getSpecificViabilityVersion2ExperimentDTO(parameterStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession);
			ViabilityDTO viabilityDTO = viabilityChartAndDataProvider.doViabilityData(viability, parameterStableId);
			model.addAttribute("viabilityDTO", viabilityDTO);
		}


		if (parameterStableId.startsWith("IMPC_EVL_")) {
			// Its an E9.5 embryonic viability outcome param which means its a line level query
			// so we don't use the normal experiment query in experiment service
			// Note:  EmbryoViability.E9_5 specifies the set of related parameters passed to getSpecificEmbryoViability_ExperimentDTO
			EmbryoViability_DTO embryoViability     = experimentService.getSpecificEmbryoViability_ExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession, EmbryoViability.E9_5);
			EmbryoViability_DTO embryoViability_DTO = viabilityChartAndDataProvider.doEmbryo_ViabilityData(parameter, embryoViability);
			model.addAttribute("embryoViabilityDTO", embryoViability_DTO);
		}

		if (parameterStableId.startsWith("IMPC_EVM_")) {
			// Its an E12.5 embryonic viability outcome param which means its a line level query
			// so we don't use the normal experiment query in experiment service
			// Note:  EmbryoViability.E12_5 specifies the set of related parameters passed to getSpecificEmbryoViability_ExperimentDTO
			EmbryoViability_DTO embryoViability     = experimentService.getSpecificEmbryoViability_ExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession, EmbryoViability.E12_5);
			EmbryoViability_DTO embryoViability_DTO = viabilityChartAndDataProvider.doEmbryo_ViabilityData(parameter, embryoViability);
			model.addAttribute("embryoViabilityDTO", embryoViability_DTO);
		}

		if (parameterStableId.startsWith("IMPC_EVO_")) {
			// Its an E14.5 embryonic viability outcome param which means its a line level query
			// so we don't use the normal experiment query in experiment service
			// Note:  EmbryoViability.E14_5 specifies the set of related parameters passed to getSpecificEmbryoViability_ExperimentDTO
			EmbryoViability_DTO embryoViability     = experimentService.getSpecificEmbryoViability_ExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession, EmbryoViability.E14_5);
			EmbryoViability_DTO embryoViability_DTO = viabilityChartAndDataProvider.doEmbryo_ViabilityData(parameter, embryoViability);
			model.addAttribute("embryoViabilityDTO", embryoViability_DTO);
		}

		if (parameterStableId.startsWith("IMPC_EVP_")) {
			// Its an E18.5 embryonic viability outcome param which means its a line level query
			// so we don't use the normal experiment query in experiment service
			// Note:  EmbryoViability.E18_5 specifies the set of related parameters passed to getSpecificEmbryoViability_ExperimentDTO
			EmbryoViability_DTO embryoViability     = experimentService.getSpecificEmbryoViability_ExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession, EmbryoViability.E18_5);
			EmbryoViability_DTO embryoViability_DTO = viabilityChartAndDataProvider.doEmbryo_ViabilityData(parameter, embryoViability);
			model.addAttribute("embryoViabilityDTO", embryoViability_DTO);
		}


		model.addAttribute("pipeline", pipeline);
		model.addAttribute("phenotypingCenter", phenotypingCenter);
		model.addAttribute("experimentNumber", experimentNumber);
		model.addAttribute("statsError", statsError);
		if (experiment != null) {
			model.addAttribute("gpUrl", experiment.getGenotypePhenotypeUrl());
			model.addAttribute("srUrl", experiment.getStatisticalResultUrl());
			model.addAttribute("phenStatDataUrl", experiment.getDataPhenStatFormatUrl());
		}
		model.addAttribute("chartOnly", chartOnly);

		// Metadata
		Map<String, String> metadataMap = null;
		if (metadataList != null) {
			metadataMap = metadataList
					.stream()
					.map(x -> Arrays.asList((x.split("="))))
					.filter(x -> x.size() == 2)
					.collect(Collectors.toMap(
							k -> k.get(0),
							v -> v.get(1),
							(v1, v2) -> v1.concat(", ".concat(v2)),
							TreeMap::new
					));
		}
		model.addAttribute("metadata", metadata);
		model.addAttribute("metadataMap", metadataMap);


		Integer numberFemaleMutantMice  = 0;
		Integer numberMaleMutantMice    = 0;
		Integer numberFemaleControlMice = 0;
		Integer numberMaleControlMice   = 0;

		if (unidimensionalChartDataSet != null) {

			// Count each specimen only once, no matter how many time's it's been measured
			final Set<ObservationDTO> mutants = unidimensionalChartDataSet.getExperiment().getMutants();
			final Set<ObservationDTO> controls = unidimensionalChartDataSet.getExperiment().getControls();
			numberFemaleMutantMice = (int) mutants.stream().filter(x -> x.getSex().equals(SexType.female.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();
			numberMaleMutantMice = (int) mutants.stream().filter(x -> x.getSex().equals(SexType.male.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();
			numberFemaleControlMice = (int) controls.stream().filter(x -> x.getSex().equals(SexType.female.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();
			numberMaleControlMice = (int) controls.stream().filter(x -> x.getSex().equals(SexType.male.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();

		}

		if (categoricalResultAndChart != null) {
/*			final List<CategoricalResult> statsResults = categoricalResultAndChart.getStatsResults();
			for (CategoricalResult cr : statsResults) {
				numberFemaleControlMice = cr.getFemaleControls();
				numberFemaleMutantMice = cr.getFemaleMutants();
				numberMaleControlMice = cr.getMaleControls();
				numberMaleMutantMice = cr.getMaleMutants();
			}*/

			final Set<ObservationDTO> mutants = categoricalResultAndChart.getExperiment().getMutants();
			final Set<ObservationDTO> controls = categoricalResultAndChart.getExperiment().getControls();
			if (mutants != null) {
				numberFemaleMutantMice = (int) mutants.stream().filter(x -> x.getSex().equals(SexType.female.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();
				numberMaleMutantMice = (int) mutants.stream().filter(x -> x.getSex().equals(SexType.male.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();
			} else {
				numberFemaleMutantMice = 0;
				numberMaleMutantMice = 0;
			}
			if (controls != null) {
				numberFemaleControlMice = (int) controls.stream().filter(x -> x.getSex().equals(SexType.female.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();
				numberMaleControlMice = (int) controls.stream().filter(x -> x.getSex().equals(SexType.male.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();
			} else {
				numberFemaleControlMice = 0;
				numberMaleControlMice = 0;
			}
		}

		if (seriesParameterChartData != null) {

			// Count each specimen only once, no matter how many time's it's been measured
			final Set<ObservationDTO> controls = seriesParameterChartData.getExperiment().getControls();
			final Set<ObservationDTO> mutants  = seriesParameterChartData.getExperiment().getMutants();
			numberFemaleMutantMice = (int) mutants.stream().filter(x -> x.getSex().equals(SexType.female.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();
			numberMaleMutantMice = (int) mutants.stream().filter(x -> x.getSex().equals(SexType.male.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();
			numberFemaleControlMice = (int) controls.stream().filter(x -> x.getSex().equals(SexType.female.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();
			numberMaleControlMice = (int) controls.stream().filter(x -> x.getSex().equals(SexType.male.getName())).map(ObservationDTOBase::getExternalSampleId).distinct().count();

		}
		
		model.addAttribute("numberFemaleMutantMice", numberFemaleMutantMice);
		model.addAttribute("numberMaleMutantMice", numberMaleMutantMice);
		model.addAttribute("numberFemaleControlMice", numberFemaleControlMice);
		model.addAttribute("numberMaleControlMice", numberMaleControlMice);

		final int totalSamples = Stream.of(numberFemaleMutantMice, numberMaleMutantMice, numberFemaleControlMice, numberMaleControlMice).filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
		model.addAttribute("numberMice", totalSamples);

		if (experiment != null) {
			List<GenotypePhenotypeDTO> gpList = gpService.getGenotypePhenotypeFor(
					gene.getMgiAccessionId(),
					experiment.getParameterStableId(),
					experiment.getStrain(),
					experiment.getAlleleAccession(),
					experiment.getZygosities(),
					experiment.getOrganisation(),
					experiment.getSexes());

			// If we are displaying a chart for IPGTT, check all possible derived terms associated to IPG procedure
			// and add any significant results to the MP terms that are associated to this data
			if (parameterStableId.equalsIgnoreCase("IMPC_IPG_002_001")) {
				for (String param : Constants.IMPC_IPG_002_001) {
					List<GenotypePhenotypeDTO> addGpList = gpService.getGenotypePhenotypeFor(
							gene.getMgiAccessionId(),
							param,
							experiment.getStrain(),
							experiment.getAlleleAccession(),
							experiment.getZygosities(),
							experiment.getOrganisation(),
							experiment.getSexes());
					gpList.addAll(addGpList);
				}
			}
			//for line level parameters such as viability
			if (org.mousephenotype.cda.common.Constants.viabilityParameters.contains(parameterStableId)) {
				for (String param : org.mousephenotype.cda.common.Constants.viabilityParameters) {
					List<GenotypePhenotypeDTO> addGpList = gpService.getGenotypePhenotypeFor(
							gene.getMgiAccessionId(),
							param,
							experiment.getStrain(),
							experiment.getAlleleAccession(),
							experiment.getZygosities(),
							experiment.getOrganisation(),
							null);//dont' filter out sex based as line level parameters this causes issues with associated phenotype dipslay on chart
					gpList.addAll(addGpList);
				}
			}
			List<String> phenotypeTerms = gpList.stream().map(GenotypePhenotypeDTO::getMpTermName).distinct().collect(Collectors.toList());
			//for links to phenotype pages we need the MP_ID
			List<String> phenotypeIds = gpList.stream().map(GenotypePhenotypeDTO::getMpTermId).distinct().collect(Collectors.toList());
			model.addAttribute("phenotypes", phenotypeTerms);
			model.addAttribute("phenotypeIds", phenotypeIds);
		}



		LifeStage parameterLifeStage = LifeStageMapper.getLifeStage(parameterStableId);

		List<LifeStage> postnatalLifeStages = Arrays.asList(LifeStage.EARLY_ADULT, LifeStage.MIDDLE_AGED_ADULT, LifeStage.LATE_ADULT);

		Boolean isPostnatal = postnatalLifeStages.contains(parameterLifeStage);

		model.addAttribute("isPostnatal", isPostnatal);
		model.addAttribute("lifeStage", LifeStageMapper.getLifeStage(parameterStableId).getName());

		return "chart";
	}


	private void addFlowCytometryImages(String[] accession, Model model, ParameterDTO parameter)
			throws SolrServerException, IOException {
		log.debug("flow cytomerty for 3i detected get headline images");
		//lets get the 3i headline images
		//example query http://ves-hx-d8.ebi.ac.uk:8986/solr/impc_images/select?q=parameter_stable_id:MGP_IMM_233_001
		//or maybe we need to filter by parameter association first based no the initial parameter
		//spleen Immunophenotyping e.g. Sik3 has many
		//chart example= http://localhost:8090/phenotype-archive/charts?phenotyping_center=WTSI&accession=MGI:2446296&parameter_stable_id=MGP_IMM_086_001
		//bone marrow chart example=http://localhost:8090/phenotype-archive/charts?phenotyping_center=WTSI&accession=MGI:1353467&parameter_stable_id=MGP_BMI_018_001
		//http://localhost:8090/phenotype-archive/charts?phenotyping_center=WTSI&accession=MGI:1353467&parameter_stable_id=MGP_BMI_018_001
		//http://ves-hx-d8.ebi.ac.uk:8986/solr/impc_images/select?q=parameter_stable_id:MGP_IMM_233_001&fq=parameter_association_stable_id:MGP_IMM_086_001&fq=gene_symbol:Sik3
		//http://localhost:8090/phenotype-archive/charts?phenotyping_center=WTSI&accession=MGI:1915276&parameter_stable_id=MGP_MLN_114_001
		//accession[0]
		QueryResponse imagesResponse = imageService.getHeadlineImages(accession[0], null,1000, null, null, parameter.getStableId());
		log.debug("number of images found="+imagesResponse.getResults().getNumFound());
		List<ImageDTO> wtAndMutantImages = imagesResponse.getBeans(ImageDTO.class);
		List<ImageDTO> controlImages=new ArrayList<>();
		List<ImageDTO> mutantImages=new ArrayList<>();
		for(ImageDTO image: wtAndMutantImages) {
			if(image.isControl())
			{
                log.debug("control found");
				controlImages.add(image);
			}
			if(image.isMutant()) {
                log.debug("mutant found");
				mutantImages.add(image);
			}
		}


		int imageCountMax=controlImages.size();
		if(mutantImages.size()>imageCountMax) {
			imageCountMax=mutantImages.size();
		}
				model.addAttribute("controlImages", controlImages);
				model.addAttribute("mutantImages", mutantImages);
        log.debug("imageCountMax="+imageCountMax);
				model.addAttribute("imageCountMax",imageCountMax);
	}
    

    private void setTitlesForGraph(Model model, String geneticBackground, String alleleSymbol) {

        model.addAttribute("symbol", (alleleSymbol != null) ? alleleSymbol : "unknown");
        model.addAttribute("geneticBackgroundString",  (geneticBackground != null) ? geneticBackground : "unknown");
    
    }

    
    private String createCharts(String[] accessionsParams, String[] pipelineStableIdsArray, String[] procedureStableIdsArray, String[] parameterIds, String[] gender, String[] phenotypingCenter,
    			String[] strains, String[] metadataGroup, String[] zygosity, Model model, ChartType chartType, String[] alleleAccession)
            throws SolrServerException, IOException, GenomicFeatureNotFoundException, ParameterNotFoundException, URISyntaxException {

        Long time = System.currentTimeMillis();
        GraphUtils graphUtils = new GraphUtils(experimentService, srService, impressService);
        List<String> geneIds = getParamsAsList(accessionsParams);
        List<String> paramIds = getParamsAsList(parameterIds);
        List<String> genderList = getParamsAsList(gender);
        List<String> phenotypingCentersList = getParamsAsList(phenotypingCenter);
        List<String> strainsList = getParamsAsList(strains);
        List<String> metadataGroups = getParamsAsList(metadataGroup);
        List<String> pipelineStableIds = getParamsAsList(pipelineStableIdsArray);
        List<String> procedureStableIds = getParamsAsList(procedureStableIdsArray);
        List<String> alleleAccessions = getParamsAsList(alleleAccession);

        // add sexes explicitly here so graphs urls are created separately
        if (genderList.isEmpty()) {
            genderList.add(SexType.male.name());
            genderList.add(SexType.female.name());
        }

        List<String> zyList = getParamsAsList(zygosity);
        if (zyList.isEmpty()) {
            zyList.add(ZygosityType.homozygote.name());
            zyList.add(ZygosityType.heterozygote.name());
            zyList.add(ZygosityType.hemizygote.name());
        }

        Set<String> allGraphUrlSet = new LinkedHashSet<>();
        String allParameters = "";

        // All ABR parameters are displayed on the same chart so we don't want to duplicate an identical chart for every ABR parameter
		List<String> abrParameters = new ArrayList<>(paramIds);
        abrParameters.retainAll(Constants.ABR_PARAMETERS);
        if (abrParameters.size() > 1){
            for (int i = 1; i < abrParameters.size(); i++) { // remove all ABR params but the first one
                paramIds.remove(abrParameters.get(i));
            }
        }
        if(geneIds.size()==0) {
        	System.err.println("There are no geneIds for this request....probably and error");
        }

        for (String geneId : geneIds) {

            GeneDTO gene = geneService.getGeneById(geneId);

            if (gene == null) {
                throw new GenomicFeatureNotFoundException("Gene " + geneId + " can't be found.", geneId);
            }

            log.debug(gene.toString());

            model.addAttribute("gene", gene);

            List<String> pNames = new ArrayList<>();

            for (String parameterId : paramIds) {

                ParameterDTO parameter = impressService.getParameterByStableId(parameterId);
                if(parameter==null) {
                	System.err.println("no parameter returned skipping for parameterId="+parameterId);
                	continue;
                }
                pNames.add(StringUtils.capitalize(parameter.getName()) + " (" + parameter.getStableId() + ")");

                Set<String> graphUrlsForParam = graphUtils.getGraphUrls(
						pipelineStableIds,
						procedureStableIds,
						parameter.getStableId(),
						geneId,
						alleleAccessions,
						zyList,
						strainsList,
						phenotypingCentersList,
						metadataGroups
						);

                allGraphUrlSet.addAll(graphUrlsForParam);

            }// end of parameterId iterations

            allParameters = StringUtils.join(pNames, ", ");

        }// end of gene iterations
        log.debug(allGraphUrlSet.size() + " chart links.");
        List allUrls=putEarlyAdultViabilityFirst(allGraphUrlSet);//we want early adult viability first if  present rather than embryo viability data
        model.addAttribute("allGraphUrlSet", allUrls);
        model.addAttribute("allParameters", allParameters);
        return "stats";
    }



	private List<String> putEarlyAdultViabilityFirst(Set<String> urlsSet) {
		//if we have the main early adult viability chart we want to show that top
		//so reorder here
		List<String> urlsList=new ArrayList<>();
		urlsList.addAll(urlsSet);
		Iterator urlsIt=urlsList.iterator();
		String viaUrl="";
		while(urlsIt.hasNext()){
			String tempUrl=(String)urlsIt.next();
			if(tempUrl.contains("_VIA_001_001")){
				viaUrl=new String(tempUrl);
				urlsIt.remove();
			}
		}
		if(!viaUrl.isEmpty()) {
			urlsList.add(0, viaUrl);
		}
		return urlsList;
	}
    /**
     * Convenience method that just changes an array [] to a more modern LIst (I
     * hate arrays! :) )
     *
     * @param parameterIds
     * @return
     */
    private List<String> getParamsAsList(String[] parameterIds) {

        List<String> paramIds = new ArrayList<>();
        if (parameterIds != null) {
            paramIds.addAll(Arrays.stream(parameterIds).collect(Collectors.toSet()));
        }
        return paramIds;
    }

    @RequestMapping("/colors")
    public String colors(Model model) {
    	model.addAttribute("maleColors", ChartColors.maleRgb);
    	model.addAttribute("femaleColors", ChartColors.femaleRgb);
    	model.addAttribute("highDifferenceColors",ChartColors.highDifferenceColors);
        return "colors";
    }

}
