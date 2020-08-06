/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this targetFile except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.reports;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.ExperimentService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Base class for Bone Mineral Density (Bmd) reports.
 *
 * Created by mrelac on 28/07/2015.
 */
public abstract class BoneMineralAbstractReport extends AbstractReport {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private Set<String> missingData      = new HashSet<>();

    @Autowired
    ExperimentService experimentService;

    @Autowired
    ObservationService observationService;

     protected String[] header = {
         "Colony Id",
         "Phenotyping Center",
         "Strain",
         "Gene Accession Id",
         "Gene Symbol",
         "Allele Accession Id",
         "Allele Symbol",
         "First Date",
         "Last Date",
            "Mean WT Male", "Median WT Male", "SD WT Male", "N WT Male",
            "Mean HOM Male", "Median HOM Male", "SD HOM Male", "N HOM Male",
            "Mean HET Male", "Median HET Male", "SD HET Male", "N HET Male",
            "Mean HEM Male", "Median HEM Male", "SD HEM Male", "N HEM Male",
            "Mean WT Female", "Median WT Female", "SD WT Female", "N WT Female",
            "Mean HOM Female", "Median HOM Female", "SD HOM Female", "N HOM Female",
            "Mean HET Female", "Median HET Female", "SD HET Female", "N HET Female"
    };

    public BoneMineralAbstractReport() {
        super();
    }

    public void run(String parameter) throws ReportException {
        List<String[]> report = new ArrayList<>();

        report.add(header);

        try {
            // 1. Gather the data without grouping
            Map<String, Set<ObservationDTO>> dtosByColony =
                    observationService.getDatapointsByColony(resources, parameter, "experimental");

            // 2. Validate all colony data, logging and filtering out required missing fields.
            Map<String, Set<ObservationDTO>> validatedDtosByColony = getValidatedColonyData(dtosByColony);

            // 3. For each set of validated colony data:
            //    b. Compute values
            //    c. Write row
            for (Map.Entry<String, Set<ObservationDTO>> entrySet : validatedDtosByColony.entrySet()) {
                String colonyKey = entrySet.getKey();
                Set<ObservationDTO> colonyData = entrySet.getValue();
                BmdStatisticsComputer statisticsComputer = new BmdStatisticsComputer(colonyData);
                List<String> row = buildColonyOutputRow(colonyData, statisticsComputer.compute());
                csvWriter.write(row);
            }

            // 4. Log any errors
            if ( ! missingData.isEmpty()) {
                log.warn("Missing required pipeline/parameter data:");
                System.out.println("  colony::center::strain::pipeline::parameter::geneAcc::gene::allele");
                missingData
                        .stream()
                        .forEach(message -> System.out.println("  " + message));
            }

            csvWriter.close();

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Report creation failed. Reason: " + e.getLocalizedMessage());
        }
    }

    /**
     * Validates colony data, logs missing data
     * @param allColonyObservations colony data map for all colonies, keyed by colonyId
     * @return a map of all validated colony data (with invalid colony entries removed)
     */
    private Map<String, Set<ObservationDTO>> getValidatedColonyData(Map<String, Set<ObservationDTO>> allColonyObservations) {
        Map<String, Set<ObservationDTO>> validatedColonyData = new HashMap<>();

        for (Map.Entry<String, Set<ObservationDTO>> colonyObservations : allColonyObservations.entrySet()) {
            String colonyKey = colonyObservations.getKey();
            Set<ObservationDTO> colonyObservationValues = colonyObservations.getValue();
            if (observationsAreValid(colonyObservationValues)) {
                validatedColonyData.put(colonyKey, colonyObservationValues);
            }
        }

        return validatedColonyData;
    }
    //  Required: colonyId, phenotypingCenter, strain, pipelineStableId, parameterStableId,
    //            geneAccessionId, geneSymbol, alleleSymbol
    private boolean observationsAreValid(Set<ObservationDTO> dtos) {
        boolean isValid = true;
        for (ObservationDTO dto : dtos) {
            if ((dto.getColonyId() == null)
             || (dto.getPhenotypingCenter() == null)
             || (dto.getStrain() == null)
             || (dto.getPipelineStableId() == null)
             || (dto.getParameterStableId() == null)
             || (dto.getGeneAccession() == null)
             || (dto.getGeneSymbol() == null)
             || (dto.getAlleleSymbol() == null))
            {
                String n = "<null>";
                String message =
                    dto.getPhenotypingCenter() == null  ? n : dto.getPhenotypingCenter() + "::" +
                    dto.getColonyId() == null           ? n : dto.getColonyId() + "::" +
                    dto.getStrain() == null             ? n : dto.getStrain() + "::" +
                    dto.getPipelineStableId() == null   ? n : dto.getPipelineStableId() + "::" +
                    dto.getParameterStableId() == null  ? n : dto.getParameterStableId() + "::" +
                    dto.getGeneAccession() == null      ? n : dto.getGeneAccession() + "::" +
                    dto.getGeneSymbol() == null         ? n : dto.getGeneSymbol() + "::" +
                    dto.getAlleleSymbol() == null       ? n : dto.getAlleleSymbol();
                missingData.add(message);
                isValid = false;
            }
        }

        return isValid;
    }


    // PRIVATE METHODS


    public List<String> buildColonyOutputRow(Set<ObservationDTO> colonyDataSet, BmdStatisticsComputer stats){
        List<String> row = new ArrayList<>();
        for (ObservationDTO data : colonyDataSet) {
            row.add(data.getColonyId());
            row.add(data.getPhenotypingCenter());
            row.add(data.getStrain());
            row.add(data.getGeneAccession());
            row.add(data.getGeneSymbol());
            row.add(data.getAlleleAccession() == null ? "" : data.getAlleleAccession());
            row.add(data.getAlleleSymbol());
            row.add(data.getDateOfExperimentString());

            row.add("FIXME FIXME FIXME");

            row.add("" + stats.getMean(SexType.male, null));
            row.add("" + stats.getMedian(SexType.male, null));
            row.add("" + stats.getSD(SexType.male, null));
            row.add("" + stats.getN(SexType.male, null));
            row.add("" + stats.getMean(SexType.male, ZygosityType.homozygote));
            row.add("" + stats.getMedian(SexType.male, ZygosityType.homozygote));
            row.add("" + stats.getSD(SexType.male, ZygosityType.homozygote));
            row.add("" + stats.getN(SexType.male, ZygosityType.homozygote));
            row.add("" + stats.getMean(SexType.male, ZygosityType.heterozygote));
            row.add("" + stats.getMedian(SexType.male, ZygosityType.heterozygote));
            row.add("" + stats.getSD(SexType.male, ZygosityType.heterozygote));
            row.add("" + stats.getN(SexType.male, ZygosityType.heterozygote));
            row.add("" + stats.getMean(SexType.male, ZygosityType.hemizygote));
            row.add("" + stats.getMedian(SexType.male, ZygosityType.hemizygote));
            row.add("" + stats.getSD(SexType.male, ZygosityType.hemizygote));
            row.add("" + stats.getN(SexType.male, ZygosityType.hemizygote));
            row.add("" + stats.getMean(SexType.female, null));
            row.add("" + stats.getMedian(SexType.female, null));
            row.add("" + stats.getSD(SexType.female, null));
            row.add("" + stats.getN(SexType.female, null));
            row.add("" + stats.getMean(SexType.female, ZygosityType.homozygote));
            row.add("" + stats.getMedian(SexType.female, ZygosityType.homozygote));
            row.add("" + stats.getSD(SexType.female, ZygosityType.homozygote));
            row.add("" + stats.getN(SexType.female, ZygosityType.homozygote));
            row.add("" + stats.getMean(SexType.female, ZygosityType.heterozygote));
            row.add("" + stats.getMedian(SexType.female, ZygosityType.heterozygote));
            row.add("" + stats.getSD(SexType.female, ZygosityType.heterozygote));
            row.add("" + stats.getN(SexType.female, ZygosityType.heterozygote));
        }

        return row;
    }


    // PRIVATE CLASSES


    /**
     * Computes the BMD statistics for a given colony
     */
    private class BmdStatisticsComputer {
        public final HashMap<String, HashMap<String, List<Float>>>           dataPoints = new HashMap<>();
        public final HashMap<String, HashMap<String, DescriptiveStatistics>> statistics = new HashMap<>();

        public final Set<ObservationDTO> colonyObservations;
        public final List<String> sexes = new ArrayList<>();
        public final List<String> zygosities = new ArrayList<>();

        /**
         * @param colonyObservations a set of observations for a single colony on which statistical computations are
         *                           performed
         */
        public BmdStatisticsComputer(Set<ObservationDTO> colonyObservations) {
            this.colonyObservations = colonyObservations;
        }

        /**
         * Compute statistics for a single colony's set of observations
         *
         * @return the statistical results for the colony provided to the constructor
         */
        public BmdStatisticsComputer compute() throws SolrServerException, IOException {
            for (ObservationDTO dto : colonyObservations) {
                String sex = dto.getSex();
                String zygosity = dto.getZygosity();
                if ( ! dataPoints.containsKey(sex)) {
                    dataPoints.put(sex, new HashMap<>());
                    statistics.put(sex, new HashMap<>());
                }
                if ( ! dataPoints.get(sex).containsKey(zygosity)){
                    dataPoints.get(sex).put(zygosity, new ArrayList<>());
                    statistics.get(sex).put(zygosity, new DescriptiveStatistics());
                }
                dataPoints.get(sex).get(zygosity).add((Float)dto.getDataPoint());
                statistics.get(sex).get(zygosity).addValue(Double.parseDouble("" + dto.getDataPoint()));

                if ( ! zygosities.contains(zygosity)){
                    zygosities.add(zygosity);
                }
                if ( ! sexes.contains(sex)){
                    sexes.add(sex);
                    dataPoints.get(sex).put("WT", new ArrayList<>());
                    statistics.get(sex).put("WT", new DescriptiveStatistics());
                }
            }

            for (String sex : sexes) {
                ObservationDTO commonDtoData = colonyObservations.iterator().next();
                String center = commonDtoData.getPhenotypingCenter();
                String pipeline = commonDtoData.getPipelineStableId();
                String parameter = commonDtoData.getParameterStableId();
                String geneAcc = commonDtoData.getGeneAccession();
                String alleleAcc = commonDtoData.getAlleleAccession();
                String strainAcc = commonDtoData.getStrainAccessionId();

                List<ExperimentDTO> experiments = experimentService.getExperimentDTO(
                        parameter,
                        pipeline,
                        geneAcc,
                        SexType.valueOf(sex),
                        center,
                        zygosities,
                        strainAcc,
                        null,
                        Boolean.FALSE,
                        alleleAcc);
                for (ExperimentDTO exp: experiments){
                    for (ObservationDTO obs: exp.getControls()){
                        dataPoints.get(sex).get("WT").add((Float)obs.getDataPoint());
                        statistics.get(sex).get("WT").addValue(Double.parseDouble("" + obs.getDataPoint()));
                    }
                }
            }

            return this;
        }


        public Double getMean(SexType sex, ZygosityType zygosityType){

            String zygosity = (zygosityType != null) ? zygosityType.getName() : "WT";
            if (statistics.containsKey(sex.getName()) && statistics.get(sex.getName()).containsKey(zygosity)){
                return statistics.get(sex.getName()).get(zygosity).getMean();
            }
            return null;
        }

        public Double getSD(SexType sex, ZygosityType zygosityType){

            String zygosity = (zygosityType != null) ? zygosityType.getName() : "WT";
            if (statistics.containsKey(sex.getName()) && statistics.get(sex.getName()).containsKey(zygosity)){
                return statistics.get(sex.getName()).get(zygosity).getStandardDeviation();
            }
            return null;
        }

        public Integer getN(SexType sex, ZygosityType zygosityType){

            String zygosity = (zygosityType != null) ? zygosityType.getName() : "WT";
            if (dataPoints.containsKey(sex.getName()) && dataPoints.get(sex.getName()).containsKey(zygosity)){
                return dataPoints.get(sex.getName()).get(zygosity).size();
            }
            return null;
        }

        public Float getMedian(SexType sex, ZygosityType zygosityType){

            String zygosity = (zygosityType != null) ? zygosityType.getName() : "WT";
            if (dataPoints.containsKey(sex.getName()) && dataPoints.get(sex.getName()).containsKey(zygosity)){
                return getMedian(dataPoints.get(sex.getName()).get(zygosity));
            }
            return null;
        }

        private Float getMedian(List<Float> list){

            Float median = (float)0.0;
            int middle = list.size()/2;
            Collections.sort(list);

            if ( list.size() == 0){
                return null;
            }
            if (list.size() % 2 == 0){
                median = (list.get(middle - 1) + list.get(middle)) /2;
            }else {
                median = list.get(middle);
            }

            return median;
        }
    }

    // PROTECTED CLASSES



    protected class IpGTTStats {

//        String geneSymbol;
//        String geneAccessionId;
//        String alleleSymbol;
//        String colony;
//        String firstDate;
//        String lastDate;
//        String phenotypingCenter;
        // < sex, <zygosity, <datapoints>>>
        HashMap<String, HashMap<String, List<Float>>> datapoints;
        HashMap<String, HashMap<String, DescriptiveStatistics>> stats;


        private IpGTTStats(Set<ObservationDTO> colonyDTOs, Set<String> missingGroupData, Set<String> missingParamData) throws NumberFormatException, SolrServerException, IOException, URISyntaxException {
//            SolrDocumentList docList = group.getResult();
//            colony = group.getGroupValue();
//            SolrDocument doc = docList.get(0);
//            phenotypingCenter = SolrUtils.getFieldValue(doc, ObservationDTO.PHENOTYPING_CENTER);
//            alleleSymbol = SolrUtils.getFieldValue(doc, ObservationDTO.ALLELE_SYMBOL);
//            geneAccessionId = SolrUtils.getFieldValue(doc, ObservationDTO.GENE_ACCESSION_ID);
//            geneSymbol = SolrUtils.getFieldValue(doc, ObservationDTO.GENE_SYMBOL);
//            firstDate = SolrUtils.getFieldValue(doc, ObservationDTO.DATE_OF_EXPERIMENT);
//            lastDate = SolrUtils.getFieldValue(docList.get(docList.size()-1), ObservationDTO.DATE_OF_EXPERIMENT);


//            SolrDocumentList docList = group.getResult();
//            colony = colonyData.getco;
//            SolrDocument doc = docList.get(0);
//            phenotypingCenter = SolrUtils.getFieldValue(doc, ObservationDTO.PHENOTYPING_CENTER);
//            alleleSymbol = SolrUtils.getFieldValue(doc, ObservationDTO.ALLELE_SYMBOL);
//            geneAccessionId = SolrUtils.getFieldValue(doc, ObservationDTO.GENE_ACCESSION_ID);
//            geneSymbol = SolrUtils.getFieldValue(doc, ObservationDTO.GENE_SYMBOL);
//            firstDate = SolrUtils.getFieldValue(doc, ObservationDTO.DATE_OF_EXPERIMENT);
//            lastDate = SolrUtils.getFieldValue(docList.get(docList.size()-1), ObservationDTO.DATE_OF_EXPERIMENT);


            datapoints = new HashMap<>();
            stats = new HashMap<>();



            List<String> zygosities = new ArrayList<>();
            List<String> sexes = new ArrayList<>();

            for (ObservationDTO dto : colonyDTOs) {
                String sex = dto.getSex();
                String zygosity = dto.getZygosity();
                if ( ! datapoints.containsKey(sex)) {
                    datapoints.put(sex, new HashMap<>());
                    stats.put(sex, new HashMap<>());
                }
                if ( ! datapoints.get(sex).containsKey(zygosity)){
                    datapoints.get(sex).put(zygosity, new ArrayList<>());
                    stats.get(sex).put(zygosity, new DescriptiveStatistics());
                }
                datapoints.get(sex).get(zygosity).add((Float)dto.getDataPoint());
                stats.get(sex).get(zygosity).addValue(Double.parseDouble("" + dto.getDataPoint()));

                if ( ! zygosities.contains(zygosity)){
                    zygosities.add(zygosity);
                }
                if ( ! sexes.contains(sex)){
                    sexes.add(sex);
                    datapoints.get(sex).put("WT", new ArrayList<>());
                    stats.get(sex).put("WT", new DescriptiveStatistics());
                }
            }

            for (String sex : sexes) {
                ObservationDTO commonDtoData = colonyDTOs.iterator().next();
                String center = commonDtoData.getPhenotypingCenter();
                String pipeline = commonDtoData.getPipelineStableId();
                String parameter = commonDtoData.getParameterStableId();
                String geneAcc = commonDtoData.getGeneAccession();
                String alleleAcc = commonDtoData.getAlleleAccession();
                String strainAcc = commonDtoData.getStrainAccessionId();
                if ((center == null) || (pipeline == null) || (parameter == null) || (geneAcc == null)
                   || (alleleAcc == null) || (strainAcc == null)) {
                    String message = center + "::" + pipeline + "::" + parameter + "::" + geneAcc + "::" + alleleAcc + "::" + strainAcc;
                    missingParamData.add(message);
                    continue;
                }

                List<ExperimentDTO> experiments = experimentService.getExperimentDTO(
                        parameter,
                        pipeline,
                        geneAcc,
                        SexType.valueOf(sex),
                        center,
                        zygosities,
                        strainAcc,
                        null,
                        Boolean.FALSE,
                        alleleAcc);
                for (ExperimentDTO exp: experiments){
                    for (ObservationDTO obs: exp.getControls()){
                        datapoints.get(sex).get("WT").add((Float)obs.getDataPoint());
                        stats.get(sex).get("WT").addValue(Double.parseDouble("" + obs.getDataPoint()));
                    }
                }
            }
        }

        public Double getMean(SexType sex, ZygosityType zygosityType){

            String zygosity = (zygosityType != null) ? zygosityType.getName() : "WT";
            if (stats.containsKey(sex.getName()) && stats.get(sex.getName()).containsKey(zygosity)){
                return stats.get(sex.getName()).get(zygosity).getMean();
            }
            return null;
        }

        public Double getSD(SexType sex, ZygosityType zygosityType){

            String zygosity = (zygosityType != null) ? zygosityType.getName() : "WT";
            if (stats.containsKey(sex.getName()) && stats.get(sex.getName()).containsKey(zygosity)){
                return stats.get(sex.getName()).get(zygosity).getStandardDeviation();
            }
            return null;
        }

        public Integer getN(SexType sex, ZygosityType zygosityType){

            String zygosity = (zygosityType != null) ? zygosityType.getName() : "WT";
            if (datapoints.containsKey(sex.getName()) && datapoints.get(sex.getName()).containsKey(zygosity)){
                return datapoints.get(sex.getName()).get(zygosity).size();
            }
            return null;
        }

        public Float getMedian(SexType sex, ZygosityType zygosityType){

            String zygosity = (zygosityType != null) ? zygosityType.getName() : "WT";
            if (datapoints.containsKey(sex.getName()) && datapoints.get(sex.getName()).containsKey(zygosity)){
                return getMedian(datapoints.get(sex.getName()).get(zygosity));
            }
            return null;
        }

        private Float getMedian(List<Float> list){

            Float median = (float)0.0;
            int middle = list.size()/2;
            Collections.sort(list);

            if ( list.size() == 0){
                return null;
            }
            if (list.size() % 2 == 0){
                median = (list.get(middle - 1) + list.get(middle)) /2;
            }else {
                median = list.get(middle);
            }

            return median;
        }
    }

}
