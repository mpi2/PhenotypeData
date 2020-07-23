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

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.*;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.solr.service.exception.SpecificExperimentException;
import org.mousephenotype.cda.solr.stats.strategy.AllControlsStrategy;
import org.mousephenotype.cda.solr.stats.strategy.ControlSelectionStrategy;
import org.mousephenotype.cda.solr.web.dto.EmbryoViability_DTO;
import org.mousephenotype.cda.solr.web.dto.FertilityDTO;
import org.mousephenotype.cda.solr.web.dto.ViabilityDTO;
import org.mousephenotype.cda.web.TimeSeriesConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Service class to collect interactions with the experiment datasource.
 * The experiment datasource holds information about the experimental observations
 *
 */

@Service
public class ExperimentService{

    private final Logger  LOG          = LoggerFactory.getLogger(this.getClass());
    private final Integer MIN_CONTROLS = 6;

    private ObservationService       observationService;
    private StatisticalResultService statisticalResultService;
    private GenotypePhenotypeService genotypePhenotypeService;

    @Inject
    public ExperimentService(
            ObservationService observationService,
            @Named("statistical-result-service") StatisticalResultService statisticalResultService,
            @Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService)
    {
        this.observationService = observationService;
        this.statisticalResultService = statisticalResultService;
        this.genotypePhenotypeService = genotypePhenotypeService;
    }

    public ExperimentService() {

    }
    
    /*
     * Bringing this method back so we don't need stats results to show charts
     */
    public Map<String, List<String>> getExperimentKeys(String mgiAccession, String parameterStableIds, List<String> pipelineStableIds, List<String> phenotypingCenter, List<String> strain, List<String> metaDataGroup, List<String> alleleAccession) 
    	    throws SolrServerException, IOException  {
    	        return observationService.getExperimentKeys(mgiAccession, parameterStableIds, pipelineStableIds, phenotypingCenter, strain, metaDataGroup, alleleAccession);
    	    }

    /**
     * Get the experiment DTO for the supplied data
     *
     * @param pipelineStableId the pipeline to get the experiment for
     * @param parameterStableId the parameter to get the experiment for
     * @param geneAccession the gene to get experiment for
     * @param sex null for both sexes
     * @param zygosities null for any zygosity
     * @param strain null for any strain
     * @return list of experiment objects
     */

    public List<ExperimentDTO> getExperimentDTO(String parameterStableId, String pipelineStableId, String geneAccession,
    SexType sex, String phenotypingCenter, List<String> zygosities, String strain, String metaDataGroup,
    Boolean includeResults, String alleleAccession)
    throws SolrServerException, IOException {

        List<ObservationDTO> observations = observationService.getExperimentObservationsBy(parameterStableId, pipelineStableId, geneAccession, zygosities, phenotypingCenter, strain, sex, metaDataGroup, alleleAccession);
        Map<String, ExperimentDTO> experimentsMap = new HashMap<>();

        for (ObservationDTO observation : observations) {

            // collect all the strains, organisations, sexes, and zygosities
            // combinations of the mutants to get the controls later

            // Experiment KEY is a combination of
            // - organisation
            // - strain
            // - parameter
            // - pipeline
            // - allele
            // - meatdata group
            ExperimentDTO experiment;

            String experimentKey = observation.getKey();

            if (experimentsMap.containsKey(experimentKey)) {
                experiment = experimentsMap.get(experimentKey);
            } else {
                experiment = new ExperimentDTO();
                experiment.setExperimentId(experimentKey);
                experiment.setObservationType(ObservationType.valueOf(observation.getObservationType()));
                experiment.setHomozygoteMutants(new HashSet<ObservationDTO>());
                experiment.setHeterozygoteMutants(new HashSet<ObservationDTO>());
                experiment.setHemizygoteMutants(new HashSet<ObservationDTO>());

                // Tree sets to keep "female" before "male" and "hetero" before
                // "hom"
                experiment.setSexes(new TreeSet<SexType>());
                experiment.setZygosities(new TreeSet<ZygosityType>());
            }

            if (experiment.getMetadata() == null) {
                experiment.setMetadata(observation.getMetadata());
            }

            if (observation.getAlleleSymbol() != null){ 
            	experiment.setAlleleSymobl(observation.getAlleleSymbol());
            }
            if (observation.getGeneticBackground()  != null){
            	experiment.setGeneticBackgtround(observation.getGeneticBackground());
            }

            if (experiment.getMetadataGroup() == null) {
                // LOG.debug("metaDataGroup in observation="+observation.getMetadataGroup());
                experiment.setMetadataGroup(observation.getMetadataGroup());
            }

            if (experiment.getGeneMarker() == null) {
                experiment.setGeneMarker(observation.getGeneSymbol());
            }

            if (experiment.getParameterStableId() == null) {
                experiment.setParameterStableId(observation.getParameterStableId());
            }

            if (experiment.getPipelineStableId() == null) {
                LOG.debug("setting pipelinestabl=" + observation.getPipelineStableId());
                experiment.setPipelineStableId(observation.getPipelineStableId());
            }

            if (experiment.getOrganisation() == null) {
                experiment.setOrganisation(observation.getPhenotypingCenter());
            }

            if (experiment.getStrain() == null) {
                experiment.setStrain(observation.getStrain());
            }
            if (experiment.getExperimentalBiologicalModelId() == null) {
                experiment.setExperimentalBiologicalModelId(observation.getSpecimenId());
            }
             if (experiment.getAlleleAccession() == null) {
                experiment.setAlleleAccession(observation.getAlleleAccession());
            }


            experiment.getZygosities().add(ZygosityType.valueOf(observation.getZygosity()));
            experiment.getSexes().add(SexType.valueOf(observation.getSex()));

            // includeResults variable skips the results when gathering
            // experiments for calculating the results (performance)
            if (experiment.getResults() == null && experiment.getExperimentalBiologicalModelId() != null && includeResults) {

                String phenCenter = observation.getPhenotypingCenter();
                ObservationType statisticalType = experiment.getObservationType();
                ZygosityType zygosity = ZygosityType.valueOf(observation.getZygosity());

                List<StatisticalResultDTO> results = statisticalResultService.getStatisticalResult(alleleAccession, strain, phenCenter, pipelineStableId, parameterStableId, metaDataGroup, zygosity, sex, statisticalType);
                experiment.setResults(results);
            }

            if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.heterozygote)) {
                experiment.getHeterozygoteMutants().add(observation);
            } else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.homozygote)) {
                experiment.getHomozygoteMutants().add(observation);
            } else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.hemizygote)) {
                experiment.getHemizygoteMutants().add(observation);
            }

            experiment.setProcedureStableId(observation.getProcedureStableId());
            experiment.setProcedureName(observation.getProcedureName());

            experimentsMap.put(experimentKey, experiment);

        }

        // Set to record the experiments that don't have control data
        Set<String> noControls = new HashSet<>();

        // TODO: Update control selection strategy based on recommendation of
        // stats working group

        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        // CONTROL SELECTION
        // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
        // Loop over all the experiments for which we found mutant data
        // to gather the control data
        
        for (String key : experimentsMap.keySet()) {

            // If the requester filtered based on organisation, then the
            // organisationId parameter will not be null and we can use that,
            // otherwise we need to determine the organisation for this
            // experiment and use that

            ExperimentDTO experiment = experimentsMap.get(key);

            if (experiment.getControls() == null) {

                experiment.setControls(new HashSet<ObservationDTO>());

                // ======================================
                // CONTROL GROUP SELECTION STRATEGY
                //
                // Per meeting 2014-01-21
                // - Categorical data
                // Use all control data (broken up by metadata splits)
                // - Unidimensional data
                // Use concurrent controls when appropriate
                // Concurrent means all collected on the same day (ALL
                // male/female controls/mutants)
                //
                // Per discussion with Terry 2014-03-24
                // - Categorical data
                // No change
                // - Unidimensional data
                // Use concurrent controls when appropriate
                // Concurrent means all collected on the same day (ALL
                // male/female controls/mutants)
                // Else, use all appropriate data for controls
                // ======================================

                List<ObservationDTO> controls = new ArrayList<ObservationDTO>();

                if (experiment.getObservationType().equals(ObservationType.categorical)) {
                    // ======================================
                    // CATEGORICAL CONTROL SELECTION
                    // ======================================

                    // Use all appropriate controls for categorical data
                    experiment.setControlSelectionStrategy(ControlStrategy.baseline_all);

                    if (experiment.getSexes() != null) {

                        for (SexType s : SexType.values()) {
                            if (!experiment.getSexes().contains(s)) {
                                continue;
                            }

                            controls.addAll(observationService.getAllControlsBySex(parameterStableId, experiment.getStrain(), phenotypingCenter, null, s.toString(), experiment.getMetadataGroup()));

                        }

                    } else {

                        controls.addAll(observationService.getAllControlsBySex(parameterStableId, experiment.getStrain(), phenotypingCenter, null, null, experiment.getMetadataGroup()));

                    }

                } else {
                    // ======================================
                    // UNIDIMENSIONAL/TIMESERIES CONTROL SELECTION
                    // ======================================

                    // Default to using all controls
                    experiment.setControlSelectionStrategy(ControlStrategy.baseline_all);

                    // Find the dates of the experiments
                    Set<String> allBatches = new HashSet<String>();
                    Date experimentDate = new Date(0L);
                    for (ObservationDTO o : experiment.getMutants()) {

                        //put a null pointer check here as for viability data has none and chart fails
                        if(o.getDateOfExperiment()!=null) {
                            allBatches.add(o.getDateOfExperiment().getYear() + "-" + o.getDateOfExperiment().getMonth() + "-" + o.getDateOfExperiment().getDate());
                            if (o.getDateOfExperiment().after(experimentDate)) {
                                experimentDate = o.getDateOfExperiment();
                            }
                        }
                        if (phenotypingCenter == null) {
                        	phenotypingCenter = o.getPhenotypingCenter();
                        }



                    }
                    LOG.debug("Number of batches: " + allBatches.size());

                    // If there is only 1 batch, the selection strategy is to
                    // try to use concurrent controls. If there is more than one
                    // batch, we default to all controls
                    experiment.setControlSelectionStrategy(ControlStrategy.baseline_all);

                    //
                    // If one sex specified
                    //
                    if (experiment.getSexes() != null && experiment.getSexes().size() < 2) {

                        for (SexType s : SexType.values()) {

                            if (!experiment.getSexes().contains(s)) {
                                continue;
                            }

                            if (allBatches.size() == 1) {

                                // If we have enough control data in the same
                                // batch (same day) for this sex, then use
                                // concurrent controls

                                List<ObservationDTO> potentialControls = observationService.getConcurrentControlsBySex(parameterStableId, experiment.getStrain(), phenotypingCenter, experimentDate, s.name(), experiment.getMetadataGroup());
                                LOG.debug("Number of potential controls for sex: " + s.name() + ": " + potentialControls.size());

                                if (potentialControls.size() >= MIN_CONTROLS) {

                                    controls = potentialControls;
                                    experiment.setControlSelectionStrategy(ControlStrategy.concurrent);
                                    LOG.debug("Setting concurrent controls for sex: " + s.name());

                                } else {

                                    controls = observationService.getAllControlsBySex(parameterStableId, experiment.getStrain(), phenotypingCenter, null, s.name(), experiment.getMetadataGroup());
                                    LOG.debug("Using baseline controls for sex: " + s.name() + ", num controls: " + controls.size());

                                }

                            } else {

                                controls = observationService.getAllControlsBySex(parameterStableId, experiment.getStrain(), phenotypingCenter, null, s.name(), experiment.getMetadataGroup());
                                LOG.debug("Using baseline controls for sex: " + s.name() + ", num controls: " + controls.size());
                            }
                        }

                    } else {

                        //
                        // Processing both sexes
                        //

                        if (allBatches.size() == 1) {

                            List<ObservationDTO> potentialMaleControls = observationService.getConcurrentControlsBySex(parameterStableId, experiment.getStrain(), phenotypingCenter, experimentDate, SexType.male.name(), experiment.getMetadataGroup());
                            List<ObservationDTO> potentialFemaleControls = observationService.getConcurrentControlsBySex(parameterStableId, experiment.getStrain(), phenotypingCenter, experimentDate, SexType.female.name(), experiment.getMetadataGroup());

                            LOG.debug("Number of potential controls for males: " + potentialMaleControls.size());
                            LOG.debug("Number of potential controls for females: " + potentialFemaleControls.size());

                            // Only if BOTH counts of male and
                            // female controls are equal or more than
                            // MIN_CONTROLS
                            // do we do concurrent controls

                            if (potentialMaleControls.size() >= MIN_CONTROLS && potentialFemaleControls.size() >= MIN_CONTROLS) {

                                controls = potentialMaleControls;
                                controls.addAll(potentialFemaleControls);
                                experiment.setControlSelectionStrategy(ControlStrategy.concurrent);
                                LOG.debug("Setting concurrent controls");

                            } else {

                                controls = observationService.getAllControlsBySex(parameterStableId, experiment.getStrain(), phenotypingCenter, null, null, experiment.getMetadataGroup());
                                LOG.debug("Using baseline controls, num controls: " + controls.size());

                            }

                        } else {

                            controls = observationService.getAllControlsBySex(parameterStableId, experiment.getStrain(), phenotypingCenter, null, null, experiment.getMetadataGroup());
                            LOG.debug("Using baseline controls, num controls: " + controls.size());
                        }

                    }

                } // End control selection

                experiment.getControls().addAll(controls);

                if (experiment.getControlBiologicalModelId() == null && controls.size() > 0) {
                    experiment.setControlBiologicalModelId(controls.get(0).getSpecimenId());
                }

                // Flag all the experiments that don't have control data
                if (controls.size() < 1) {
                    noControls.add(key);
                }
            }
        }

        // had to comment this out as we have phenotype calls from the pheno
        // summary table that are for graphs with no control data
        // so if we take those out then there appears to be no reason to have a
        // graph link.
        // for(String key : noControls) {
        // experimentsMap.remove(key);
        // }

        return new ArrayList<ExperimentDTO>(experimentsMap.values());
    }


    
    /**
     * Should only return 1 experimentDTO - returns null if none and exception
     * if more than 1 - used by ajax charts
     *
     * @param acc
     * @param strain
     * @param metadataGroup
     * @return
     * @throws SolrServerException, IOException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SpecificExperimentException
     */
    public ViabilityDTO getSpecificViabilityExperimentDTO(String parameterStableId, String pipelineStableId, String acc, String phenotypingCenter, String strain, String metadataGroup, String alleleAccession) throws SolrServerException, IOException , URISyntaxException, SpecificExperimentException {
        ViabilityDTO viabilityDTO=new ViabilityDTO();
        Map<String, ObservationDTO> paramStableIdToObservation = new HashMap<>();
        //for viability we don't need to filter on Sex or Zygosity
        List<ObservationDTO> observations = observationService.getExperimentObservationsBy(parameterStableId, pipelineStableId, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
        ObservationDTO outcomeObservation = observations.get(0);
        viabilityDTO.setCategory(observations.get(0).getCategory());
        for(int i=3;i<15; i++){
            String formatted = String.format("%02d",i);
            String param="IMPC_VIA_0"+formatted+"_001";
            List<ObservationDTO> observationsForCounts = observationService.getViabilityData(param, pipelineStableId, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
            if(observationsForCounts.size()>1){
                System.err.println("More than one observation found for a viability request!!!");
            }
            paramStableIdToObservation.put(param,observationsForCounts.get(0));
        }
        viabilityDTO.setParamStableIdToObservation(paramStableIdToObservation);
        return viabilityDTO;
    }

    public EmbryoViability_DTO getSpecificEmbryoViability_ExperimentDTO(String parameterStableId, String pipelineStableId, String acc, String phenotypingCenter, String strain, String metadataGroup, String alleleAccession, EmbryoViability embryoViability) throws SolrServerException, IOException , URISyntaxException, SpecificExperimentException {
        EmbryoViability_DTO embryoViability_DTO=new EmbryoViability_DTO(embryoViability);
        Map<String, ObservationDTO> paramStableIdToObservation = new HashMap<>();
        //for viability we don't need to filter on Sex or Zygosity
        List<ObservationDTO> observations = observationService.getExperimentObservationsBy(parameterStableId, pipelineStableId, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
        ObservationDTO outcomeObservation = observations.get(0);
        embryoViability_DTO.setCategory(observations.get(0).getCategory());
        embryoViability_DTO.setProceedureName(observations.get(0).getProcedureName());
        for(String param : embryoViability_DTO.parameters.parameterList){
            List<ObservationDTO> observationsForCounts = observationService.getViabilityData(param, pipelineStableId, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
            if(observationsForCounts != null){
                if(observationsForCounts.size()>1){
                    System.err.println("More than one observation found for a viability request!!!");
                }
                paramStableIdToObservation.put(param,observationsForCounts.get(0));
            }
        }
        embryoViability_DTO.setParamStableIdToObservation(paramStableIdToObservation);
        return embryoViability_DTO;
    }

    public FertilityDTO getSpecificFertilityExperimentDTO(String parameterStableId, String pipelineStableId, String acc, String phenotypingCenter, String strain, String metadataGroup, String alleleAccession) throws SolrServerException, IOException , URISyntaxException, SpecificExperimentException {
    	FertilityDTO fertilityDTO=new FertilityDTO();
        Map<String, ObservationDTO> paramStableIdToObservation = new HashMap<>();
            //for viability we don't need to filter on Sex or Zygosity
        List<ObservationDTO> observations = observationService.getExperimentObservationsBy(parameterStableId, pipelineStableId, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
        ObservationDTO outcomeObservation = observations.get(0);
        System.out.println("specific outcome="+observations);
           System.out.println("category of observation="+outcomeObservation.getCategory());
           fertilityDTO.setCategory(observations.get(0).getCategory());
       for(int i=1;i<14; i++){
    	   String formatted = String.format("%02d",i);
    	   String param="IMPC_FER_0"+formatted+"_001";
    	   System.out.println("fert param="+param);
           List<ObservationDTO> observationsForCounts = observationService.getViabilityData(param, pipelineStableId, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
           if(observationsForCounts.size()>1){
        	   System.err.println("More than one observation found for a viability request!!!");
           }
           if(observationsForCounts.size()>0){
           System.out.println("vai param name="+observationsForCounts.get(0).getParameterName());
           System.out.println("via data_point="+observationsForCounts.get(0).getDataPoint());
           paramStableIdToObservation.put(param,observationsForCounts.get(0));
           }

       }
       //do for "IMPC_FER_019_001" Gross findings female
       List<ObservationDTO> observationsForCounts = observationService.getViabilityData("IMPC_FER_019_001", pipelineStableId, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
       if(observationsForCounts.size()>0){
    	   System.out.println("vai param name="+observationsForCounts.get(0).getParameterName());
           System.out.println("via data_point="+observationsForCounts.get(0).getDataPoint());
    	   paramStableIdToObservation.put("IMPC_FER_019_001",observationsForCounts.get(0));
       }

       fertilityDTO.setParamStableIdToObservation(paramStableIdToObservation);
        return fertilityDTO;
    }

   	/**
     * Should only return 1 experimentDTO - returns null if none and exception
     * if more than 1 - used by ajax charts
     *
     * @param parameterStableId
     * @param acc
     * @param genderList
     * @param zyList
     * @param strain
     * @param metadataGroup
     * @return
     * @throws SolrServerException, IOException
     * @throws IOException
     * @throws URISyntaxException
     * @throws SpecificExperimentException
     */
    public ExperimentDTO getSpecificExperimentDTO(String parameterStableId, String pipelineStableId, String acc, List<String> genderList, List<String> zyList, String phenotypingCenter, String strain, String metadataGroup, String alleleAccession, String ebiMappedSolrUrl)
    throws SolrServerException, IOException, SpecificExperimentException {

    	List<ExperimentDTO> experimentList;
        boolean includeResults = true;

        // if gender list is size 2 assume both sexes so no filter needed
        if (genderList.isEmpty() || genderList.size() == 2) {

            // if zygosity list is size 3 then no filter needed either
            if (zyList.isEmpty() || zyList.size() == 3) {
                experimentList = this.getExperimentDTO(parameterStableId, pipelineStableId, acc, null, phenotypingCenter, null, strain, metadataGroup, includeResults, alleleAccession);
            } else {
                experimentList = this.getExperimentDTO(parameterStableId, pipelineStableId, acc, null, phenotypingCenter, zyList, strain, metadataGroup, includeResults, alleleAccession);
            }

        } else {
            String gender = genderList.get(0);
            if (zyList.isEmpty() || zyList.size() == 3) {
                experimentList = this.getExperimentDTO(parameterStableId, pipelineStableId, acc, SexType.valueOf(gender), phenotypingCenter, null, strain, metadataGroup, includeResults, alleleAccession);
            } else {
                experimentList = this.getExperimentDTO(parameterStableId, pipelineStableId, acc, SexType.valueOf(gender), phenotypingCenter, zyList, strain, metadataGroup, includeResults, alleleAccession);
            }

        }
        if (experimentList.isEmpty()) {
            return null;// return null if no experiments
        }
        if (experimentList.size() > 1 && !TimeSeriesConstants.DERIVED_BODY_WEIGHT_PARAMETERS.contains(parameterStableId)) {//need the BWT exemption as we get multiple experiments for that- so we just need to pass them back and let the chart and table code handle them...?
        	System.out.println("experiment list size="+experimentList.size());
        	for(ExperimentDTO experiment : experimentList){
        		System.out.println("aaahhhh experimentId="+ experiment.getExperimentId()+ " metadata_group="+experiment.getMetadataGroup());
        	}
        	//return experimentList.get(0);
            throw new SpecificExperimentException("Too many experiments returned - should only be one from this method call");
        }
        ExperimentDTO experiment=null;
        //if parameter is the bodyweight parameter we can merge the results as currently it's the only one that we don't want to have a meta data split
        if(TimeSeriesConstants.DERIVED_BODY_WEIGHT_PARAMETERS.contains(parameterStableId)){
        	 experiment = experimentList.get(0);
        	for(ExperimentDTO exp: experimentList){
        		experiment.getControls().addAll(exp.getControls());
        		experiment.getHomozygoteMutants().addAll(exp.getHomozygoteMutants());
        		experiment.getHeterozygoteMutants().addAll(exp.getHeterozygoteMutants());
        		experiment.getHemizygoteMutants().addAll(exp.getHemizygoteMutants());

        		experiment.getSexes().addAll(exp.getSexes());
        		
        	}
        }else{
	        experiment = experimentList.get(0);
	        experiment = setUrls(experiment, parameterStableId, pipelineStableId, acc, zyList, phenotypingCenter, strain, metadataGroup, alleleAccession, ebiMappedSolrUrl);
        }

        return experiment;

    }

    private ExperimentDTO setUrls(ExperimentDTO experiment, String parameterStableId, String pipelineStableId, String acc, List<String> zyList, String phenotypingCenter, String strain, String metadataGroup, String alleleAccession, String ebiMappedSolrUrl){

        List<String> phenotypingCenters = new ArrayList<>();
        phenotypingCenters.add(phenotypingCenter);

        experiment.setStatisticalResultUrl(ebiMappedSolrUrl + "/statistical-result/select?" + statisticalResultService.buildQuery(acc, null, null, phenotypingCenters, null, null, null, null, null,
            null, zyList, strain, parameterStableId, pipelineStableId, metadataGroup, alleleAccession));

        experiment.setGenotypePhenotypeUrl(ebiMappedSolrUrl  + "/genotype-phenotype/select?" + genotypePhenotypeService.buildQuery(acc, null, null, phenotypingCenters, null, null, null, null, null,
                                                                                                                                   null, zyList, strain, parameterStableId, pipelineStableId, null, alleleAccession));

        String experimentRawDataUrl = "/exportraw?";
        if (phenotypingCenter != null ) { experimentRawDataUrl += "phenotyping_center=" + phenotypingCenter + "&";}
        if (parameterStableId != null) {experimentRawDataUrl += "parameter_stable_id=" + parameterStableId + "&";}
        if (alleleAccession != null) {experimentRawDataUrl += "allele_accession_id=" + alleleAccession + "&";}
        if (strain != null) {experimentRawDataUrl += "strain=" + strain + "&";}
        if (pipelineStableId != null) {experimentRawDataUrl += "pipeline_stable_id=" + pipelineStableId + "&";}
        if (zyList != null) {
            for (String zyg : zyList){
                experimentRawDataUrl += "&zygosity=" + zyg + "&";
            }
        }

        experiment.setDataPhenStatFormatUrl(experimentRawDataUrl);
        //System.out.println("experiment srUrl="+experiment.getStatisticalResultUrl());
        return experiment;


    }

    /**
     * Control strategy selection based on phenotyping center and user supplied
     * strategy.
     *
     * @param phenotypingCenter
     *            center at which the mutants were phenotyped
     * @param strategies
     *            which control selection strategy to use
     *
     * @return an instance of a control selection strategy
     */
    public ControlSelectionStrategy getControlSelectionStrategy(String[] phenotypingCenter, String[] strategies) {
        // TODO: implement logic to get appropriate control selection strategy
        // object
        return new AllControlsStrategy();
    }



	public Collection<? extends String> getChartPivots(String accessionAndParam, String acc, ParameterDTO parameter,
			List<String> pipelineStableIds, List<String> zyList, List<String> phenotypingCentersList,
			List<String> strainsParams, List<String> metaDataGroup, List<String> alleleAccession) throws IOException, SolrServerException, URISyntaxException {
		return observationService.getChartPivots(accessionAndParam, acc, parameter, pipelineStableIds, zyList, phenotypingCentersList, strainsParams, metaDataGroup, alleleAccession);
	}
}
