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
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.ExperimentService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Base class for Bone Mineral Density (Bmd) reports.
 *
 * Created by mrelac on 28/07/2015.
 */
public abstract class BoneMineralAbstractReport extends AbstractReport {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ExperimentService experimentService;

    @Autowired
    ObservationService observationService;


     protected String[] header = {
            "Gene Symbol", "MGI Gene Id", "Allele Symbol", "Colony Id", "Phenotyping Center", "First Date", "Last Date",
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
            report.addAll(getBmpIpgttStats(observationService.getDatapointsByColony(resources, parameter, "experimental")));
        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception in observationService.getDatapointsByColony. Reason: " + e.getLocalizedMessage());
        }

        csvWriter.writeRowsOfArray(report);

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }
    }


    // PRIVATE METHODS


    public List<String[]> getBmpIpgttStats(List<Group> groups){

        List<String[]> rows = new ArrayList<>();

        try {
            for (Group group: groups) {
                IpGTTStats stats;
                stats = new IpGTTStats(group);

                String[] row = { stats.geneSymbol, stats.geneAccessionId, stats.alleleSymbol, stats.colony,  stats.phenotypingCenter, stats.firstDate, stats.lastDate,
                        "" + stats.getMean(SexType.male, null), "" + stats.getMedian(SexType.male, null), "" + stats.getSD(SexType.male, null), "" + stats.getN(SexType.male, null),
                        "" + stats.getMean(SexType.male, ZygosityType.homozygote), "" + stats.getMedian(SexType.male, ZygosityType.homozygote), "" + stats.getSD(SexType.male, ZygosityType.homozygote), "" + stats.getN(SexType.male, ZygosityType.homozygote),
                        "" + stats.getMean(SexType.male, ZygosityType.heterozygote), "" + stats.getMedian(SexType.male, ZygosityType.heterozygote), "" + stats.getSD(SexType.male, ZygosityType.heterozygote), "" + stats.getN(SexType.male, ZygosityType.heterozygote),
                        "" + stats.getMean(SexType.male, ZygosityType.hemizygote), "" + stats.getMedian(SexType.male, ZygosityType.hemizygote), "" + stats.getSD(SexType.male, ZygosityType.hemizygote), "" + stats.getN(SexType.male, ZygosityType.hemizygote),
                        "" + stats.getMean(SexType.female, null), "" + stats.getMedian(SexType.female, null), "" + stats.getSD(SexType.female, null), "" + stats.getN(SexType.female, null),
                        "" + stats.getMean(SexType.female, ZygosityType.homozygote), "" + stats.getMedian(SexType.female, ZygosityType.homozygote), "" + stats.getSD(SexType.female, ZygosityType.homozygote), "" + stats.getN(SexType.female, ZygosityType.homozygote),
                        "" + stats.getMean(SexType.female, ZygosityType.heterozygote), "" + stats.getMedian(SexType.female, ZygosityType.heterozygote), "" + stats.getSD(SexType.female, ZygosityType.heterozygote), "" + stats.getN(SexType.female, ZygosityType.heterozygote),
                };
                rows.add(row);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return rows;
    }


    // PROTECTED CLASSES



    protected class IpGTTStats {

        String geneSymbol;
        String geneAccessionId;
        String alleleSymbol;
        String colony;
        String firstDate;
        String lastDate;
        String phenotypingCenter;
        // < sex, <zygosity, <datapoints>>>
        HashMap<String, HashMap<String, List<Float>>> datapoints;
        HashMap<String, HashMap<String, DescriptiveStatistics>> stats;


        public IpGTTStats(Group group) throws NumberFormatException, SolrServerException, IOException, URISyntaxException {

            SolrDocumentList docList = group.getResult();
            colony = group.getGroupValue();
            SolrDocument doc = docList.get(0);
            phenotypingCenter = doc.getFieldValue(ObservationDTO.PHENOTYPING_CENTER).toString();
            alleleSymbol = doc.getFieldValue(ObservationDTO.ALLELE_SYMBOL).toString();
            geneSymbol = doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString();
            geneAccessionId = doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID).toString();
            firstDate = doc.getFieldValue(ObservationDTO.DATE_OF_EXPERIMENT).toString();
            lastDate = docList.get(docList.size()-1).getFieldValue(ObservationDTO.DATE_OF_EXPERIMENT).toString();
            datapoints = new HashMap<>();
            stats = new HashMap<>();


            List<String> zygosities = new ArrayList<>();
            List<String> sexes = new ArrayList<>();

            for (SolrDocument d : docList){
                String sex = d.getFieldValue(ObservationDTO.SEX).toString();
                String zyg = d.getFieldValue(ObservationDTO.ZYGOSITY).toString();
                if (!datapoints.containsKey(sex)) {
                    datapoints.put(sex, new HashMap<>());
                    stats.put(sex, new HashMap<>());
                }
                if (!datapoints.get(sex).containsKey(zyg)){
                    datapoints.get(sex).put(zyg, new ArrayList<>());
                    stats.get(sex).put(zyg, new DescriptiveStatistics());
                }
                datapoints.get(sex).get(zyg).add((Float)d.getFieldValue(ObservationDTO.DATA_POINT));
                stats.get(sex).get(zyg).addValue(Double.parseDouble("" + d.getFieldValue(ObservationDTO.DATA_POINT)));

                if (!zygosities.contains(zyg)){
                    zygosities.add(zyg);
                }
                if (!sexes.contains(sex)){
                    sexes.add(sex);
                    datapoints.get(sex).put("WT", new ArrayList<>());
                    stats.get(sex).put("WT", new DescriptiveStatistics());
                }

            }

            for (String sex : sexes){
                String center = SolrUtils.getFieldValue(doc, ObservationDTO.PHENOTYPING_CENTER);
                String pipeline = SolrUtils.getFieldValue(doc, ObservationDTO.PIPELINE_STABLE_ID);
                String parameter = SolrUtils.getFieldValue(doc, ObservationDTO.PARAMETER_STABLE_ID);
                String geneAcc = SolrUtils.getFieldValue(doc, ObservationDTO.GENE_ACCESSION_ID);
                String alleleAcc = SolrUtils.getFieldValue(doc, ObservationDTO.ALLELE_ACCESSION_ID);
                String strainAcc = SolrUtils.getFieldValue(doc, ObservationDTO.STRAIN_ACCESSION_ID);
                if ((center == null) || (pipeline == null) || (parameter == null) || (geneAcc == null)
                   || (alleleAcc == null) || (strainAcc == null)) {
                    log.warn("Missing required data for center::pipeline::parameter::gene::allele::strain \"" +
                                     "{}::{}::{}::{}::{}::{}", center, pipeline, parameter, geneAcc,
                             alleleAcc, strainAcc);
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

        public Double getMean(SexType sex, ZygosityType zyg){

            String zygosity = (zyg != null) ? zyg.getName() : "WT";
            if (stats.containsKey(sex.getName()) && stats.get(sex.getName()).containsKey(zygosity)){
                return stats.get(sex.getName()).get(zygosity).getMean();
            }
            return null;
        }

        public Double getSD(SexType sex, ZygosityType zyg){

            String zygosity = (zyg != null) ? zyg.getName() : "WT";
            if (stats.containsKey(sex.getName()) && stats.get(sex.getName()).containsKey(zygosity)){
                return stats.get(sex.getName()).get(zygosity).getStandardDeviation();
            }
            return null;
        }

        public Integer getN(SexType sex, ZygosityType zyg){

            String zygosity = (zyg != null) ? zyg.getName() : "WT";
            if (datapoints.containsKey(sex.getName()) && datapoints.get(sex.getName()).containsKey(zygosity)){
                return datapoints.get(sex.getName()).get(zygosity).size();
            }
            return null;
        }

        public Float getMedian(SexType sex, ZygosityType zyg){

            String zygosity = (zyg != null) ? zyg.getName() : "WT";
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
