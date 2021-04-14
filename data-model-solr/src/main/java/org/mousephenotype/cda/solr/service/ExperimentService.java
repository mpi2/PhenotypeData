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

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.enumerations.*;
import org.mousephenotype.cda.solr.repositories.StatisticalRawDataRepository;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTOBase;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.solr.service.exception.SpecificExperimentException;
import org.mousephenotype.cda.solr.web.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Service class to collect interactions with the experiment datasource.
 * The experiment datasource holds information about the experimental observations
 *
 */

@Service
public class ExperimentService {

    private final Logger  LOG          = LoggerFactory.getLogger(this.getClass());
    private final Integer MIN_CONTROLS = 6;
    public static final int PARTITION_SIZE = 50;

    private ObservationService       observationService;
    private StatisticalResultService statisticalResultService;
    private GenotypePhenotypeService genotypePhenotypeService;
    private StatisticalRawDataRepository statisticalRawDataRepository;

    @Inject
    public ExperimentService(
            ObservationService observationService,
            @Named("statistical-result-service") StatisticalResultService statisticalResultService,
            @Named("genotype-phenotype-service") GenotypePhenotypeService genotypePhenotypeService,
            StatisticalRawDataRepository statisticalRawDataRepository)

    {
        this.observationService = observationService;
        this.statisticalResultService = statisticalResultService;
        this.genotypePhenotypeService = genotypePhenotypeService;
        this.statisticalRawDataRepository = statisticalRawDataRepository;
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
     * @param zygosities null for any zygosity
     * @param strain null for any strain
     * @return list of experiment objects
     */

    public List<ExperimentDTO> getExperimentDTO(String parameterStableId, String pipelineStableId,
                                                String phenotypingCenter, List<String> zygosities, String strain, String metaDataGroup,
                                                String alleleAccession)
            throws SolrServerException, IOException {

        Map<String, ExperimentDTO> experimentsMap = new HashMap<>();

        // Find all S-R results for this set
        List<StatisticalResultDTO> statisticalResults = statisticalResultService.getStatisticalResult(pipelineStableId, null, parameterStableId, alleleAccession, phenotypingCenter, metaDataGroup, strain, zygosities);

        Set<ObservationDTO> allObservations = new HashSet<>();

        for (StatisticalResultDTO result : statisticalResults) {

            // For each S-R result, get experiment
            ExperimentDTO dto = getSpecificExperimentDTO(pipelineStableId, result.getProcedureStableId().get(0), parameterStableId, alleleAccession, phenotypingCenter, result.getZygosity(), result.getStrainAccessionId(), result.getMetadataGroup());

            // Fully populate the observation DTOs for those experiments from the Minimal Observations in the SR core
            Set<String> foundSamples = allObservations.stream().map(ObservationDTOBase::getExternalSampleId).collect(Collectors.toSet());

            allObservations.addAll(
                    observationService.inflateObservations(
                        Stream.of(dto.getMutants(), dto.getControls())
                                .flatMap(Collection::stream)
                                .filter(x -> !foundSamples.contains(x.getExternalSampleId()))
                                .map(ObservationDTOBase::getExternalSampleId)
                                .collect(Collectors.toList()),
                        parameterStableId,
                        phenotypingCenter
            ));

            Map<String, ObservationDTO> observationByExternalSampleId = allObservations.stream().collect(Collectors.toMap(ObservationDTOBase::getExternalSampleId, Function.identity()));
            dto.setControls(dto.getControls().stream().map(x -> observationByExternalSampleId.get(x.getExternalSampleId())).collect(Collectors.toSet()));
            dto.setHemizygoteMutants(dto.getHemizygoteMutants().stream().map(x -> observationByExternalSampleId.get(x.getExternalSampleId())).collect(Collectors.toSet()));
            dto.setHomozygoteMutants(dto.getHomozygoteMutants().stream().map(x -> observationByExternalSampleId.get(x.getExternalSampleId())).collect(Collectors.toSet()));
            dto.setHeterozygoteMutants(dto.getHeterozygoteMutants().stream().map(x -> observationByExternalSampleId.get(x.getExternalSampleId())).collect(Collectors.toSet()));

            String key = Stream.of(dto.getControls(), dto.getMutants()).flatMap(Collection::stream).map(ObservationDTO::getKey).collect(Collectors.toList()).get(0);
            experimentsMap.put(key, dto);

        }
        // Sort by p-value


        return new ArrayList<>(experimentsMap.values());
    }


    /**
     * Get the experiment DTO for the supplied data
     *
     * @param pipelineStableId the pipeline to get the experiment for
     * @param parameterStableId the parameter to get the experiment for
     * @param zygosity null for any zygosity
     * @param strain null for any strain
     * @return list of experiment objects
     */

    public ExperimentDTO getSpecificExperimentDTO(
            String pipelineStableId,
            String procedureStableId,
            String parameterStableId,
            String alleleAccession,
            String phenotypingCenter,
            String zygosity,
            String strain,
            String metadataGroup
            )
    throws SolrServerException, IOException {

        ExperimentDTO experiment = new ExperimentDTO();


        List<StatisticalResultDTO> results = new ArrayList<>();
        results.addAll(statisticalResultService.getStatisticalResult(pipelineStableId, procedureStableId, parameterStableId, alleleAccession, phenotypingCenter, metadataGroup, strain, Collections.singletonList(zygosity)));

        for (StatisticalResultDTO result : results) {

            if (result.getProcedureStableId().size() != 1) {
                LOG.error("There should only be one procedure per statistical result", result);
                continue;
            }

            experiment.setExperimentId(String.valueOf(experiment.hashCode()));
            experiment.setObservationType(ObservationType.valueOf(result.getDataType()));
            experiment.setHomozygoteMutants(new HashSet<>());
            experiment.setHeterozygoteMutants(new HashSet<>());
            experiment.setHemizygoteMutants(new HashSet<>());

            // Tree sets to keep "female" before "male" and "hetero" before
            // "hom"
            experiment.setSexes(new TreeSet<>());
            if (result.getPhenotypeSex() != null) {
                experiment.getSexes().addAll(result.getPhenotypeSex().stream().map(SexType::getByDisplayName).collect(Collectors.toList()));
            } else {
                if (result.getFemaleMutantCount() != null && result.getFemaleMutantCount() > 0) {
                    experiment.getSexes().add(SexType.female);
                }
                if (result.getMaleMutantCount() != null && result.getMaleMutantCount() > 0) {
                    experiment.getSexes().add(SexType.male);
                }
            }
            experiment.setZygosities(new TreeSet<>());
            experiment.getZygosities().add(ZygosityType.valueOf(result.getZygosity()));

            // The metadata for a result is the combination of all metadata for each specimen included in
            // the data used to produce the result.  That potentially yields several unique sets of metadata
            // without producing a split, if, for e.g., a non-critical piece of metadata changed, such as
            // experimenter ID.  Since all critical metadata is the same (otherwise there would have been
            // a split), we can pick an arbitrary specimen's metadata as an exemplar to display.
            if (result.getMetadata() != null) {
                List<String> metadata = Arrays.asList(result.getMetadata().get(0).split("\\|"));
                experiment.setMetadata(metadata);
            }

            experiment.setGeneMarker(result.getMarkerSymbol());
            experiment.setAlleleSymbol(result.getAlleleSymbol());
            experiment.setAlleleAccession(result.getAlleleAccessionId());
            experiment.setGeneticBackgtround(result.getGeneticBackground());
            experiment.setMetadataGroup(result.getMetadataGroup());

            experiment.setPipelineStableId(result.getPipelineStableId());
            experiment.setProcedureStableId(result.getProcedureStableId().get(0));
            experiment.setParameterStableId(result.getParameterStableId());

            experiment.setOrganisation(result.getPhenotypingCenter());
            experiment.setStrain(result.getStrainAccessionId());

            experiment.setResults(Collections.singletonList(result));

            // Get the raw data from the result
            String output;
            String b64 = this.statisticalRawDataRepository.findStatisticalRawDataDTOByDocId(result.getDocId()).getRawData();

            if (b64 != null) {
                try (GZIPInputStream gzis = new GZIPInputStream(new ByteArrayInputStream(Base64.decodeBase64(b64)))) {
                    output = IOUtils.toString(gzis, "UTF-8");
                } catch (IOException e) {
                    LOG.error("Failed to unzip content for result", result, e);
                    continue;
                }

                // Each element has the structure:
                // {
                //    "biological_sample_group": <String> [BiologicalSampleType enum]
                //    "date_of_experiment": <String> [ISO8601 date formatted string]
                //    "external_sample_id": <String>
                //    "specimen_sex": <String> [SexType enum]
                //    "body_weight": <Double>
                //    "data_point": <Double> [null when categorical]
                //    "category": <String> [null when not categorical]
                //    "time_point": <String> [null when not time_series, iso8601 otherwise]
                //    "discrete_point": <String> [null when not time_series, float otherwise]
                //  }
                List<MinimalObservationDto> minObservations = Arrays.asList(
                        new Gson().fromJson(output, MinimalObservationDto[].class));

                List<ObservationDTO> observations = minObservations.stream()
                    .filter(x -> (x.getDataPoint()!=null || x.getCategory()!=null) )
                    .map(x -> {
                        ObservationDTO o = new ObservationDTO();
                        o.setExternalSampleId(x.getExternalSampleId());
                        o.setSex(x.getSex());
                        o.setGroup(x.getBiologicalSampleGroup());
                        o.setDateOfExperiment(x.getDateOfExperiment());
                        o.setCategory(x.getCategory());
                        o.setDataPoint(x.getDataPoint() != null ? x.getDataPoint().floatValue() : null);
                        o.setDiscretePoint(x.getDiscreteTimePoint() != null ? x.getDiscreteTimePoint().floatValue() : null);
                        o.setWeight(x.getBodyWeight() != null ? x.getBodyWeight().floatValue() : null);
                        o.setAlleleAccession(result.getAlleleAccessionId());
                        o.setStrainAccessionId(result.getStrainAccessionId());
                        o.setPhenotypingCenter(result.getPhenotypingCenter());
                        o.setParameterStableId(result.getParameterStableId());
                        o.setPipelineStableId(result.getPipelineStableId());
                        o.setZygosity(result.getZygosity());
                        o.setMetadataGroup(result.getMetadataGroup());
                        o.setProductionCenter(result.getProductionCenter());
                        return o;
                    })
                    .collect(Collectors.toList());

                final Set<ObservationDTO> controls = observations.stream().filter(x -> x.getGroup().equals(BiologicalSampleType.control.toString())).collect(Collectors.toSet());
                final Set<ObservationDTO> mutants = observations.stream().filter(x -> x.getGroup().equals(BiologicalSampleType.experimental.toString())).collect(Collectors.toSet());

                experiment.setControls(controls);
                switch (ZygosityType.valueOf(result.getZygosity())) {
                    case homozygote:
                        experiment.setHomozygoteMutants(mutants);
                        break;
                    case heterozygote:
                        experiment.setHeterozygoteMutants(mutants);
                        break;
                    case hemizygote:
                        experiment.setHemizygoteMutants(mutants);
                        break;
                    default:
                        LOG.error("No zygosity type found for result", result);
                        continue;
                }
            }
        }

        return experiment;
    }

    @Data
    private class MinimalObservationDto {

        //[
        // {
        //   "biological_sample_group": "control",
        //   "date_of_experiment": "2013-03-12T00:00:00Z",
        //   "external_sample_id": "AAQD_381_001903",
        //   "specimen_sex": "female",
        //   "body_weight": 21.0,
        //   "data_point": 187.39,
        //   "category": null
        //   "time_point: null
        //   "discrete_point: null
        // },
        // { ... },
        // ]

        @SerializedName("biological_sample_group") String biologicalSampleGroup;
        @SerializedName("external_sample_id") String externalSampleId;
        @SerializedName("date_of_experiment") Date dateOfExperiment;
        @SerializedName("specimen_sex") String sex;
        @SerializedName("category") String category;
        @SerializedName("data_point") Double dataPoint;
        @SerializedName("time_point") String timePoint;
        @SerializedName("discrete_point") Double discreteTimePoint;
        @SerializedName("body_weight") Double bodyWeight;
    }


    public List<ObservationDTO> getViabilityForGene(String accession) throws IOException, SolrServerException {
//http://wwwdev.ebi.ac.uk/mi/impc/dev/solr/experiment/select?q=gene_accession_id:%22MGI:2136171%22&fq=parameter_stable_id:IMPC_VIA_*
        List<ObservationDTO> observations = observationService.getViabilityObservationsByGene(accession);
        return observations;

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
    public ViabilityDTO getSpecificViabilityVersion1ExperimentDTO(String parameterStableId, String pipelineStableId, String acc, String phenotypingCenter, String strain, String metadataGroup, String alleleAccession) throws SolrServerException, IOException , URISyntaxException, SpecificExperimentException {
        ViabilityDTO viabilityDTO=new ViabilityDTOVersion1();
        Map<String, ObservationDTO> paramStableIdToObservation = new HashMap<>();

        //for viability we don't need to filter on Sex or Zygosity
        List<ObservationDTO> observations = observationService.getExperimentObservationsBy(parameterStableId, pipelineStableId, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
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


    public ViabilityDTO getViabilityForGeneVersion1ExperimentDTO(String parameterStableId, String pipelineStableId, String acc, String phenotypingCenter, String strain, String metadataGroup, String alleleAccession) throws SolrServerException, IOException , URISyntaxException, SpecificExperimentException {
        ViabilityDTO viabilityDTO=new ViabilityDTOVersion1();
        Map<String, ObservationDTO> paramStableIdToObservation = new HashMap<>();

        //for viability we don't need to filter on Sex or Zygosity
        List<ObservationDTO> observations = observationService.getExperimentObservationsBy(parameterStableId, pipelineStableId, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
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

    /**
     * Should only return 1 experimentDTO - returns null if none and exception
     * if more than 1 - used by ajax charts
     *
     * @param acc
     * @param strain
     * @param metadataGroup
     * @return
     */
    public ViabilityDTO getSpecificViabilityVersion2ExperimentDTO(String parameterStableId, String acc, String phenotypingCenter, String strain, String metadataGroup, String alleleAccession) throws SolrServerException, IOException {
        ViabilityDTO viabilityDTO=new ViabilityDTOVersion2();
        Map<String, ObservationDTO> paramStableIdToObservation = new HashMap<>();

        //for viability we don't need to filter on Sex or Zygosity
        List<ObservationDTO> observations = observationService.getExperimentObservationsBy(parameterStableId, null, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
        Map<String, String> textValue = new Gson().fromJson(observations.get(0).getTextValue(), Map.class);
        String category = textValue.get("outcome");

        viabilityDTO.setCategory(category);

        // The supporting viability parameters for IMPRESS version 2 range from 49 - 62 (inclusive)
        for(int i=49;i<63; i++){

//            if (i==55 || i==56) {
                // Skip for now
                // IMPC_VIA_055_001 = hemi males, IMPC_VIA_056_001 anygous females
 //               continue;
//            }

            String formatted = String.format("%03d",i);
            String param="IMPC_VIA_"+formatted+"_001";
            List<ObservationDTO> observationsForCounts = observationService.getViabilityData(param, null, acc, null, phenotypingCenter, strain, null, metadataGroup, alleleAccession);
            if(observationsForCounts != null) {
                if (observationsForCounts.size() > 1) {
                    System.err.println("More than one observation found for a viability request: " + param);
                }
                paramStableIdToObservation.put(param, observationsForCounts.get(0));
            }
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
   	@Deprecated
    public ExperimentDTO getSpecificExperimentDTO(String parameterStableId, String pipelineStableId, String acc, List<String> genderList, List<String> zyList, String phenotypingCenter, String strain, String metadataGroup, String alleleAccession, String ebiMappedSolrUrl)
    throws SolrServerException, IOException, SpecificExperimentException {

    	List<ExperimentDTO> experimentList;

        // if zygosity list is size 3 then no filter needed either
        if (zyList.isEmpty() || zyList.size() == 3) {
            experimentList = this.getExperimentDTO(parameterStableId, pipelineStableId, phenotypingCenter, null, strain, metadataGroup, alleleAccession);
        } else {
            experimentList = this.getExperimentDTO(parameterStableId, pipelineStableId, phenotypingCenter, zyList, strain, metadataGroup, alleleAccession);
        }

        if (experimentList.isEmpty()) {
            return null;// return null if no experiments
        }
        if (experimentList.size() > 1 && !Constants.DERIVED_BODY_WEIGHT_PARAMETERS.contains(parameterStableId)) {//need the BWT exemption as we get multiple experiments for that- so we just need to pass them back and let the chart and table code handle them...?
        	System.out.println("experiment list size="+experimentList.size());
        	for(ExperimentDTO experiment : experimentList){
        		System.out.println("aaahhhh experimentId="+ experiment.getExperimentId()+ " metadata_group="+experiment.getMetadataGroup());
        	}
        	//return experimentList.get(0);
            throw new SpecificExperimentException("Too many experiments returned - should only be one from this method call");
        }
        ExperimentDTO experiment=null;
        //if parameter is the bodyweight parameter we can merge the results as currently it's the only one that we don't want to have a meta data split
        if(Constants.DERIVED_BODY_WEIGHT_PARAMETERS.contains(parameterStableId)){
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

    public ExperimentDTO setUrls(ExperimentDTO experiment, String parameterStableId, String pipelineStableId, String acc, List<String> zyList, String phenotypingCenter, String strain, String metadataGroup, String alleleAccession, String ebiMappedSolrUrl){

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


	public Collection<? extends String> getChartPivots(String accessionAndParam, String acc, String parameter,
			List<String> pipelineStableIds, List<String> zyList, List<String> phenotypingCentersList,
			List<String> strainsParams, List<String> metaDataGroup, List<String> alleleAccession, List<String>procedureStableIds) throws IOException, SolrServerException {
		return observationService.getChartPivots(accessionAndParam, acc, parameter, pipelineStableIds, zyList, phenotypingCentersList, strainsParams, metaDataGroup, alleleAccession, procedureStableIds);
	}


    public Set<String> getChartPivotsForGeneViability(String geneAccession) throws IOException, SolrServerException {
   	    return observationService.getChartPivotsForGeneViability(geneAccession);
    }
}
