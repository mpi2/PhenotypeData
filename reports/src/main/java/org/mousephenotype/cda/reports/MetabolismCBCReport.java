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

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.ExperimentService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.*;

/**
 * Metabolism (CBC) report.
 *
 * Created by mrelac on 28/07/2015.
 */
@Component
public class MetabolismCBCReport extends AbstractReport {

    @Autowired
    ExperimentService experimentService;

    @Autowired
    ObservationService observationService;

    private String[] header = new String[] {
             "Mouse Id", "Sample Type", "Gene Symbol", "MGI Gene Id", "Allele Symbol", "Zygosity"
            ,"Sex", "Colony Id", "Phenotyping Center", "Metadata Group"

            ,"Total cholesterol IMPC_CBC_015_001"
            ,"HDL-cholesterol IMPC_CBC_016_001"
            ,"Triglycerides IMPC_CBC_017_001"
            ,"Glucose IMPC_CBC_018_001"
            ,"Fructosamine IMPC_CBC_020_001"
            ,"LDL-cholesterol IMPC_CBC_025_001"
            ,"Free fatty acids IMPC_CBC_026_001"
            ,"Glycerol IMPC_CBC_027_001"
            ,"C-reactive protein IMPC_CBC_032_001"
            ,"Glycosilated hemoglobin A1c (HbA1c) IMPC_CBC_052_001"
            ,"Thyroxine IMPC_CBC_053_001"

            ,"Insulin IMPC_INS_001_001"

            // metadata
            ,"CBC Metadata"
            ,"INS Metadata"
    };

    public MetabolismCBCReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            log.error("MetabolismCBCReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        csvWriter.write(header);

        int count = 0;

        try {
            Collection<String> biologicalSampleIds = observationService.getMetabolismReportBiologicalSampleIds("IMPC_CBC_*");
            for (String biologicalSampleId : biologicalSampleIds) {
//if (count >= 1000) break;
                Integer iBiologicalSampleId = commonUtils.tryParseInt(biologicalSampleId);
                if (iBiologicalSampleId != null) {
                    List<ObservationDTO> mouseInfoDTOs = observationService.getMetabolismReportBiologicalSampleId("IMPC_CBC_*", iBiologicalSampleId);
                    List<ObservationDTO> mouseInfoInsulinDTOs = observationService.getMetabolismReportBiologicalSampleId("IMPC_INS_*", iBiologicalSampleId);
                    csvWriter.write(createReportRow(mouseInfoDTOs, mouseInfoInsulinDTOs));
                    if (++count % 10000 == 0)
                        log.debug(new Date().toString() + ": " + count + " records written.");
                }
            }

            csvWriter.close();

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception in MetabolismCBCReport. Reason: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. " + count + " records written in " + commonUtils.msToHms(System.currentTimeMillis() - start)));
    }

    /**
     * Return a list of strings for a single biologicalSampleId suitable for writing to an output file
     *
     * @param mouseInfoDTOs the DTOs for single biologicalSampleId
     * @param mouseInfoInsulinDTOs the DTOs for single biologicalSampleId containing any insulin parameters
     *
     * @return a list of strings for a single biologicalSampleId suitable for writing to an output file
     *
     * @throws ReportException
     */
    private List<String> createReportRow(List<ObservationDTO> mouseInfoDTOs, List<ObservationDTO> mouseInfoInsulinDTOs) throws ReportException {
        List<String> retVal = new ArrayList<>();

        Map<String, List<Float>> mouseInfoMap = new HashMap<>();    // key = parameterStableId
                                                                    // value = dataPoint
        mouseInfoMap.put("IMPC_CBC_015_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_CBC_016_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_CBC_017_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_CBC_018_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_CBC_020_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_CBC_025_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_CBC_026_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_CBC_027_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_CBC_032_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_CBC_052_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_CBC_053_001", new ArrayList<>());

        mouseInfoMap.put("IMPC_INS_001_001", new ArrayList<>());

        for (ObservationDTO mouseInfoDTO : mouseInfoDTOs) {
            if (mouseInfoMap.containsKey(mouseInfoDTO.getParameterStableId())) {
                mouseInfoMap.get(mouseInfoDTO.getParameterStableId()).add(mouseInfoDTO.getDataPoint());

                // Add any INS parameters.
                List<Float> insDataPoints = new ArrayList<>();
                for (ObservationDTO observationDTO : mouseInfoInsulinDTOs) {
                    insDataPoints.add(observationDTO.getDataPoint());
                }
                if ( ! insDataPoints.isEmpty()) {
                    mouseInfoMap.put("IMPC_INS_001_001", insDataPoints);
                }
            }
        }

        String externalSampleId = mouseInfoDTOs.get(0).getExternalSampleId().toString();
        // Build the output row.
        retVal.add(mouseInfoDTOs.get(0).getExternalSampleId());
        retVal.add(mouseInfoDTOs.get(0).getGroup());
        retVal.add(mouseInfoDTOs.get(0).getGeneSymbol());
        retVal.add(mouseInfoDTOs.get(0).getGeneAccession());
        retVal.add(mouseInfoDTOs.get(0).getAlleleSymbol());
        retVal.add(mouseInfoDTOs.get(0).getZygosity());
        retVal.add(mouseInfoDTOs.get(0).getSex());
        retVal.add(mouseInfoDTOs.get(0).getColonyId());
        retVal.add(mouseInfoDTOs.get(0).getPhenotypingCenter());
        retVal.add(mouseInfoDTOs.get(0).getMetadataGroup());

        List<Float> data = mouseInfoMap.get("IMPC_CBC_015_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_015_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_CBC_016_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_016_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_CBC_017_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_017_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_CBC_018_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_018_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_CBC_020_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_020_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_CBC_025_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_025_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_CBC_026_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_026_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_CBC_027_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_027_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_CBC_032_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_032_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_CBC_052_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_052_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_CBC_053_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CBC_053_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_INS_001_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_INS_001_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        // CBC Metadata

        List<String> metadataCBCList = mouseInfoDTOs.get(0).getMetadata();
        if (metadataCBCList != null) {
            retVal.add(StringUtils.join(metadataCBCList, "::"));
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        // INS Metadata

        if ( ! mouseInfoInsulinDTOs.isEmpty()) {
            List<String> metadataINSList = mouseInfoInsulinDTOs.get(0).getMetadata();
            if (metadataINSList != null) {
                retVal.add(StringUtils.join(metadataINSList, "::"));
            } else {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            }
        }

        return retVal;
    }
}