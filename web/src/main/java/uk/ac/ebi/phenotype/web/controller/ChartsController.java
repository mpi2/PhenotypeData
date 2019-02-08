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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;
import org.mousephenotype.cda.enumerations.EmbryoViability;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.ExperimentService;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.exception.SpecificExperimentException;
import org.mousephenotype.cda.solr.web.dto.ViabilityDTO;
import org.mousephenotype.cda.solr.web.dto.EmbryoViability_DTO;
import org.mousephenotype.cda.web.ChartType;
import org.mousephenotype.cda.web.TimeSeriesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import uk.ac.ebi.phenotype.chart.*;
import uk.ac.ebi.phenotype.error.GenomicFeatureNotFoundException;
import uk.ac.ebi.phenotype.error.ParameterNotFoundException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.ValueConstants.DEFAULT_NONE;


@Controller
public class ChartsController {

    private final Logger log = LoggerFactory.getLogger(ChartsController.class);

    @Autowired
    private CategoricalChartAndTableProvider categoricalChartAndTableProvider;

    @Autowired
    private TimeSeriesChartAndTableProvider timeSeriesChartAndTableProvider;

    @Autowired
    private UnidimensionalChartAndTableProvider continousChartAndTableProvider;

    @Autowired
    private ScatterChartAndTableProvider scatterChartAndTableProvider;

    @Autowired
    private AbrChartAndTableProvider abrChartAndTableProvider;

    @Autowired
    private ViabilityChartAndDataProvider viabilityChartAndDataProvider;

    @Autowired
    private ExperimentService experimentService;

    @Autowired
    private StatisticalResultService srService;

    @Autowired
    private GeneService geneService;
    
    @Autowired
    private ImpressService is;
    
    @Resource(name = "globalConfiguration")
    private Map<String, String> config;


    @Value("${solr_url}")
    public String SOLR_URL;


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
//            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
//            response.setHeader("Access-Control-Max-Age", "3600");
//            response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
            return createCharts(accessionsParams, pipelineStableIds, parameterIds, gender, phenotypingCenter, strains, metadataGroup, zygosity, model, chartType, alleleAccession);
        } catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Will only ever return one chart!
     *
     * @param accession
     * @param strain
     * @param metadataGroup
     * @param parameterStableId
     * @param gender
     * @param zygosity
     * @param phenotypingCenter
     * @param strategies
     * @param model
     * @return
     * @throws GenomicFeatureNotFoundException
     * @throws ParameterNotFoundException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SolrServerException, IOException
     */
    
    
    @RequestMapping("/chart")
    public String chart(@RequestParam(required = true, value = "experimentNumber") String experimentNumber,
                        @RequestParam(required = false, value = "accession") String[] accession,
                        @RequestParam(required = false, value = "strain_accession_id") String strain,
                        @RequestParam(required = false, value = "allele_accession_id") String alleleAccession,
                        @RequestParam(required = false, value = "metadata_group", defaultValue = DEFAULT_NONE) String metadataGroup,
                        @RequestParam(required = false, value = "parameter_stable_id") String parameterStableId,
                        @RequestParam(required = false, value = "gender") String[] gender,
                        @RequestParam(required = false, value = "zygosity") String[] zygosity,
                        @RequestParam(required = false, value = "phenotyping_center") String phenotypingCenter,
                        @RequestParam(required = false, value = "strategy") String[] strategies,
                        @RequestParam(required = false, value = "pipeline_stable_id") String pipelineStableId,
                        @RequestParam(required = false, value = "chart_type") ChartType chartType,
                        @RequestParam(required = false, value = "chart_only", defaultValue = "false") boolean chartOnly,
                        @RequestParam(required = false, value = "standAlone") boolean standAlone, Model model)
            throws GenomicFeatureNotFoundException, ParameterNotFoundException, IOException, URISyntaxException, SolrServerException, SpecificExperimentException {
    	if(parameterStableId!=null && !parameterStableId.equals("")){
    		boolean isDerivedBodyWeight=TimeSeriesConstants.DERIVED_BODY_WEIGHT_PARAMETERS.contains(parameterStableId);
    		model.addAttribute("isDerivedBodyWeight", isDerivedBodyWeight);
    	}
        UnidimensionalDataSet unidimensionalChartDataSet = null;
        ChartData timeSeriesForParam = null;
        CategoricalResultAndCharts categoricalResultAndChart = null;

        boolean statsError = false;

        if (parameterStableId.startsWith("IMPC_FER_")) {
            String url = config.get("baseUrl") + "/genes/" + accession[0];
            return "redirect:" + url;
        }

		// TODO need to check we don't have more than one accession and one
        // parameter throw and exception if we do
        // get the parameter object from the stable id
        ParameterDTO parameter = is.getParameterByStableId(parameterStableId);     
        
        if (parameter == null) {
        	System.out.println("throwing parameter not found exception");
            throw new ParameterNotFoundException("Parameter " + parameterStableId + " can't be found.", parameterStableId);
        }

        String metadata = null;
        String xUnits = parameter.getUnitX();
        ObservationType observationTypeForParam = parameter.getObservationType();
        List<String> genderList = getParamsAsList(gender);

		// Use the first phenotyping center passed in (ignore the others?)
        // should only now be one center at this stage for one graph/experiment
        // TODO put length check and exception here
        // List<String> phenotypingCenters = getParamsAsList(phenotypingCenter);
       
        String metaDataGroupString = null;
        if (metadataGroup != null && ! metadataGroup.equals(DEFAULT_NONE)) {
            metaDataGroupString = metadataGroup;
        }

        List<String> zyList = getParamsAsList(zygosity);

        Integer pipelineId = null;
        ImpressBaseDTO pipeline = null;

        if (pipelineStableId != null &&  ! pipelineStableId.equals("")) {
            log.debug("pipe stable id=" + pipelineStableId);
            pipeline = is.getPipeline(pipelineStableId);
            pipelineId = pipeline.getId();
            model.addAttribute("pipeline", pipeline);
            model.addAttribute("pipelineUrl", is.getPipelineUrlByStableId(pipeline.getStableId()));
        }

        model.addAttribute("phenotypingCenter", phenotypingCenter);

        ExperimentDTO experiment = null;
        

        // 29-Apr-2015 (mrelac) The team has determined that we don't display fertility graphs because Impress does not require all the supporting
        // data to be uploaded, and some centers don't upload it, so we don't know if the data is valid or not.

//        if (parameterStableId.startsWith("IMPC_FER_")) {
//			// Its a fertility outcome param which means its a line level query
//            // so we don't use the normal experiment query in experiment service
//            //http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/query?q=parameter_stable_id:IMPC_FER_*&facet=true&facet.field=parameter_stable_id&rows=300&fq=gene_accession_id:%22MGI:1918788%22
//            //http://localhost:8080/phenotype-archive/charts?accession=MGI:1918788&parameter_stable_id=IMPC_FER_001_001
//            FertilityDTO fertility = experimentService.getSpecificFertilityExperimentDTO(parameter.getId(), pipelineId, accession[0], phenotypingCenterId, strain, metaDataGroupString, alleleAccession);
//            FertilityDTO fertilityDTO = fertilityChartAndDataProvider.doFertilityData(parameter, fertility);
//            if (fertilityDTO != null) {
//                model.addAttribute("fertilityDTO", fertilityDTO);
//                BiologicalModel expBiologicalModel = bmDAO.getBiologicalModelById(fertilityDTO.getParamStableIdToObservation().entrySet().iterator().next().getValue().getBiologicalModelId());
//                setTitlesForGraph(model, expBiologicalModel);
//            }
//            return "chart";
//        }

        experiment = experimentService.getSpecificExperimentDTO(parameterStableId, pipelineStableId, accession[0], genderList, zyList, phenotypingCenter, strain, metaDataGroupString, alleleAccession, SOLR_URL);

        if (parameterStableId.startsWith("IMPC_VIA_")) {
            // Its a viability outcome param which means its a line level query
            // so we don't use the normal experiment query in experiment service
            ViabilityDTO viability = experimentService.getSpecificViabilityExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession);
            ViabilityDTO viabilityDTO = viabilityChartAndDataProvider.doViabilityData(parameter, viability);
            model.addAttribute("viabilityDTO", viabilityDTO);
            setTitlesForGraph(model, experiment.getGeneticBackgtround(), experiment.getAlleleSymobl());
        }

        if (parameterStableId.startsWith("IMPC_EVL_")) {
            // Its an E9.5 embryonic viability outcome param which means its a line level query
            // so we don't use the normal experiment query in experiment service
            // Note:  EmbryoViability.E9_5 specifies the set of related parameters passed to getSpecificEmbryoViability_ExperimentDTO
            EmbryoViability_DTO embryoViability = experimentService.getSpecificEmbryoViability_ExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession, EmbryoViability.E9_5);
            EmbryoViability_DTO embryoViability_DTO = viabilityChartAndDataProvider.doEmbryo_ViabilityData(parameter, embryoViability);
            model.addAttribute("embryoViabilityDTO", embryoViability_DTO);
            setTitlesForGraph(model, experiment.getGeneticBackgtround(), experiment.getAlleleSymobl());
        }

        if (parameterStableId.startsWith("IMPC_EVM_")) {
            // Its an E12.5 embryonic viability outcome param which means its a line level query
            // so we don't use the normal experiment query in experiment service
            // Note:  EmbryoViability.E12_5 specifies the set of related parameters passed to getSpecificEmbryoViability_ExperimentDTO
            EmbryoViability_DTO embryoViability = experimentService.getSpecificEmbryoViability_ExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession, EmbryoViability.E12_5);
            EmbryoViability_DTO embryoViability_DTO = viabilityChartAndDataProvider.doEmbryo_ViabilityData(parameter, embryoViability);
            model.addAttribute("embryoViabilityDTO", embryoViability_DTO);
            setTitlesForGraph(model, experiment.getGeneticBackgtround(), experiment.getAlleleSymobl());
        }

        if (parameterStableId.startsWith("IMPC_EVO_")) {
            // Its an E14.5 embryonic viability outcome param which means its a line level query
            // so we don't use the normal experiment query in experiment service
            // Note:  EmbryoViability.E14_5 specifies the set of related parameters passed to getSpecificEmbryoViability_ExperimentDTO
            EmbryoViability_DTO embryoViability = experimentService.getSpecificEmbryoViability_ExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession, EmbryoViability.E14_5);
            EmbryoViability_DTO embryoViability_DTO = viabilityChartAndDataProvider.doEmbryo_ViabilityData(parameter, embryoViability);
            model.addAttribute("embryoViabilityDTO", embryoViability_DTO);
            setTitlesForGraph(model, experiment.getGeneticBackgtround(), experiment.getAlleleSymobl());
        }

        if (parameterStableId.startsWith("IMPC_EVP_")) {
            // Its an E18.5 embryonic viability outcome param which means its a line level query
            // so we don't use the normal experiment query in experiment service
            // Note:  EmbryoViability.E18_5 specifies the set of related parameters passed to getSpecificEmbryoViability_ExperimentDTO
            EmbryoViability_DTO embryoViability = experimentService.getSpecificEmbryoViability_ExperimentDTO(parameterStableId, pipelineStableId, accession[0], phenotypingCenter, strain, metaDataGroupString, alleleAccession, EmbryoViability.E18_5);
            EmbryoViability_DTO embryoViability_DTO = viabilityChartAndDataProvider.doEmbryo_ViabilityData(parameter, embryoViability);
            model.addAttribute("embryoViabilityDTO", embryoViability_DTO);
            setTitlesForGraph(model, experiment.getGeneticBackgtround(), experiment.getAlleleSymobl());
        }

        
        
        if (experiment != null) {
            if (pipeline == null) {
                // if we don't already have the pipeline from the url params get it via the experiment returned
                pipeline = is.getPipeline(experiment.getPipelineStableId());
            }

            if (experiment.getMetadataGroup() != null){
            	metadata = experiment.getMetadataHtml();
            }

            String xAxisTitle = xUnits;
            setTitlesForGraph(model, experiment.getGeneticBackgtround(), experiment.getAlleleSymobl());

            try {
				// if (chartType == null){
                // chartType = GraphUtils.getDefaultChartType(parameter);
                // // chartType might still be null after this
                // if(chartType==ChartType.PIE){
                // viabilityDTO =
                // viabilityChartAndDataProvider.doViabilityData(null, null);
                // model.addAttribute("viabilityDTO", viabilityDTO);
                // //model.addAttribute("tableData", viabilityDTO);
                // return "chart";
                // }
                // }
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

                            // get experiments for other parameters too
                            model.addAttribute("abrChart", abrChartAndTableProvider.getChart(pipelineStableId, accession[0], genderList, zyList, phenotypingCenter, strain, metaDataGroupString, alleleAccession, "abrChart" + experimentNumber, SOLR_URL));
                            break;

	                    case UNIDIMENSIONAL_BOX_PLOT:

                            try {
                                unidimensionalChartDataSet = continousChartAndTableProvider.doUnidimensionalData(experiment, experimentNumber, parameter, ChartType.UNIDIMENSIONAL_BOX_PLOT, false, xAxisTitle);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            model.addAttribute("unidimensionalChartDataSet", unidimensionalChartDataSet);

		                    scatterChartAndData = scatterChartAndTableProvider.doScatterData(experiment, unidimensionalChartDataSet.getMin(), unidimensionalChartDataSet.getMax(), parameter, experimentNumber);
		                    model.addAttribute("scatterChartAndData", scatterChartAndData);

		                    break;

	                    case CATEGORICAL_STACKED_COLUMN:

                            categoricalResultAndChart = categoricalChartAndTableProvider.doCategoricalData(experiment, parameter, accession[0], experimentNumber);
                            model.addAttribute("categoricalResultAndChart", categoricalResultAndChart);
                            break;

                        case TIME_SERIES_LINE:

                            timeSeriesForParam = timeSeriesChartAndTableProvider.doTimeSeriesData(experiment, parameter, experimentNumber);
                            model.addAttribute("timeSeriesChartsAndTable", timeSeriesForParam);
                            break;

                        default:

                            log.error("Unknown how to display graph for observation type: " + observationTypeForParam);
                            break;
                    }
                }else{
                	log.error("chart type is null");
                }

            } catch (SQLException e) {
                log.error(ExceptionUtils.getFullStackTrace(e));
                statsError = true;
            }

            model.addAttribute("pipeline", pipeline);
            model.addAttribute("metadata", metadata);
            model.addAttribute("phenotypingCenter", phenotypingCenter);
            model.addAttribute("experimentNumber", experimentNumber);
            model.addAttribute("statsError", statsError);
            model.addAttribute("gpUrl", experiment.getGenotypePhenotypeUrl());
            model.addAttribute("srUrl", experiment.getStatisticalResultUrl());
            model.addAttribute("phenStatDataUrl", experiment.getDataPhenStatFormatUrl());
            model.addAttribute("chartOnly", chartOnly);

        } else {
            System.out.println("empty experiment");
            model.addAttribute("emptyExperiment", true);
        }

        return "chart";
    }
    

    private void setTitlesForGraph(Model model, String geneticBackground, String alleleSymbol) {

        model.addAttribute("symbol", (alleleSymbol != null) ? alleleSymbol : "unknown");
        model.addAttribute("geneticBackgroundString",  (geneticBackground != null) ? geneticBackground : "unknown");
    
    }

    
    private String createCharts(String[] accessionsParams, String[] pipelineStableIdsArray, String[] parameterIds, String[] gender, String[] phenotypingCenter,
    			String[] strains, String[] metadataGroup, String[] zygosity, Model model, ChartType chartType, String[] alleleAccession)
            throws SolrServerException, IOException, GenomicFeatureNotFoundException, ParameterNotFoundException, URISyntaxException {

        Long time = System.currentTimeMillis();
        GraphUtils graphUtils = new GraphUtils(experimentService, srService);
        List<String> geneIds = getParamsAsList(accessionsParams);
        List<String> paramIds = getParamsAsList(parameterIds);
        List<String> genderList = getParamsAsList(gender);
        List<String> phenotypingCentersList = getParamsAsList(phenotypingCenter);
        List<String> strainsList = getParamsAsList(strains);
        List<String> metadataGroups = getParamsAsList(metadataGroup);
        List<String> pipelineStableIds = getParamsAsList(pipelineStableIdsArray);
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
        List<String> abrParameters =  new ArrayList<>();
        abrParameters.addAll(paramIds);
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

                ParameterDTO parameter = is.getParameterByStableId(parameterId);
                if(parameter==null) {
                	System.err.println("no parameter returned skipping for parameterId="+parameterId);
                	continue;
                }
                pNames.add(StringUtils.capitalize(parameter.getName()) + " (" + parameter.getStableId() + ")");
				// instead of an experiment list here we need just the outline
                // of the experiments - how many, observation types
                Set<String> graphUrlsForParam = graphUtils.getGraphUrls(geneId, parameter, pipelineStableIds, zyList, phenotypingCentersList,
                								strainsList, metadataGroups, chartType, alleleAccessions);
                allGraphUrlSet.addAll(graphUrlsForParam);

            }// end of parameterId iterations

            allParameters = StringUtils.join(pNames, ", ");

        }// end of gene iterations
        log.info(allGraphUrlSet.size() + " chart links.");
        model.addAttribute("allGraphUrlSet", allGraphUrlSet);
        model.addAttribute("allParameters", allParameters);
        return "stats";
    }

    /**
     * Exception handler for gene not found
     *
     * @param exception of proper type to indicate gene not found
     * @return model and view for error page
     */
    @ExceptionHandler(GenomicFeatureNotFoundException.class)
    public ModelAndView handleGenomicFeatureNotFoundException(GenomicFeatureNotFoundException exception) {

        log.error(ExceptionUtils.getFullStackTrace(exception));

        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("acc", exception.getAcc());
        mv.addObject("type", "MGI gene");
        mv.addObject("exampleURI", "/charts?accession=MGI:104874");

        return mv;
    }

    /**
     * Exception handler for parameter not found
     *
     * @param exception of proper type to indicate parameter not found
     * @return model and view for error page
     */
    @ExceptionHandler(ParameterNotFoundException.class)
    public ModelAndView handleParameterNotFoundException(ParameterNotFoundException exception) {

	    log.error(ExceptionUtils.getFullStackTrace(exception));

        ModelAndView mv = new ModelAndView("identifierError");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("acc", exception.getAcc());
        mv.addObject("type", "Parameter");
        mv.addObject("exampleURI", "/charts?accession=MGI:98373&parameterId=M-G-P_014_001_001&gender=male&zygosity=homozygote&phenotypingCenter=WTSI");

        return mv;
    }

    /**
     * Exception handler for experiment not found
     *
     * @param exception of proper type to indicate experiment not found
     * @return model and view for error page
     */
    @ExceptionHandler(SpecificExperimentException.class)
    public ModelAndView handleSpecificExperimentException(SpecificExperimentException exception) {

	    log.error(ExceptionUtils.getFullStackTrace(exception));

        ModelAndView mv = new ModelAndView("Specific Experiment Not Found Error");
        mv.addObject("errorMessage", exception.getMessage());
        mv.addObject("type", "Experiment");

        return mv;
    }

    /**
     * Convenience method that just changes an array [] to a more modern LIst (I
     * hate arrays! :) )
     *
     * @param parameterIds
     * @return
     */
    private List<String> getParamsAsList(String[] parameterIds) {

        List<String> paramIds = new ArrayList<String>();
        if (parameterIds != null) {
            paramIds.addAll(Arrays.stream(parameterIds).collect(Collectors.toSet()));
        }
        return paramIds;
    }

    @RequestMapping("/colors")
    public String colors(Model model) {
    	System.out.println("calling colors page");
    	model.addAttribute("maleColors", ChartColors.maleRgb);
    	model.addAttribute("femaleColors", ChartColors.femaleRgb);
    	model.addAttribute("highDifferenceColors",ChartColors.highDifferenceColors);
        return "colors";
    }

}
