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
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.ExperimentService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Metabolism (glucose tolerance procedure) report.
 *
 * Created by mrelac on 28/07/2015.
 */
@Component
public class MetabolismIPGTTReport extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(ObservationService.class);

    @Autowired
    ExperimentService experimentService;

    @Autowired
    ObservationService observationService;

    private boolean[] hasWarnings = {false, false, false};  // Parameters IMPC_IPG_001_001, 002_001, and 003_001

    private String[] header = new String[] {
             "Mouse id", "Sample Type", "Gene", "Allele", "Zygosity"
            ,"Sex", "Colony id", "Phenotyping center", "Metadata group"

            ,"Body Weight IMPC_IPG_001_001"
            ,"Blood glucose concentration IMPC_IPG_002_001"
            ,"Fasted blood glucose concentration IMPC_IPG_010_001"

            // metadata
            ,"Metadata"
    };

    public MetabolismIPGTTReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {
        File file = null;

        List<String> errors = parser.validate(parser.parse(args));
        initialise(args);

        long start = System.currentTimeMillis();

        csvWriter.writeNext(header);

        // This is a tremendous amount of data, so we'll do a write after every biological sample id found.
        try {
            Collection<String> biologicalSampleIds = observationService.getMetabolismReportBiologicalSampleIds("IMPC_IPG_*");
            int count = 0;
            for (String biologicalSampleId : biologicalSampleIds) {
if (count >= 100) break;
                if (count == 71) {
                    int m = 17;
                    System.out.println(m);
                }
                Integer lBiologicalSampleId = commonUtils.tryParseInt(biologicalSampleId);
                if (lBiologicalSampleId != null) {
                    List<ObservationDTO> mouseInfoDTOs = observationService.getMetabolismReportBiologicalSampleId("IMPC_IPG_*", lBiologicalSampleId);
                    csvWriter.writeNext(createReportRow(mouseInfoDTOs));
                    if (++count % 1000 == 0)
                        logger.info(new Date().toString() + ": " + count + " records written.");
                }
            }

            csvWriter.close();

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception in observationService.getObservationsByProcedureStableId. Reason: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }


    // PRIVATE CLASSES


    private class CalorimetryData {
        private float min;
        private float max;
        private float sum;
        private int count;

        public CalorimetryData(float min, float max, float sum, int count) {
            this.min = min;
            this.max = max;
            this.sum = sum;
            this.count = count;
        }
    }


    // PRIVATE METHODS


    /**
     * Given a mouseInfoMap, key, and a current data point, computes the min, max, sum, and increments the count for
     * the single data point.
     *
     * @param mouseInfoMap a valid mouseInfoMap instance
     * @param mouseInfoMapKey the parameter stable id used as a key to the mouseInfoMap for the currentDataPoint
     * @param currentDataPoint the current data point to be accumulated
     */
    private void accumulateSeriesParameterValues(Map<String, CalorimetryData> mouseInfoMap, String mouseInfoMapKey, float currentDataPoint) {
        if ( ! mouseInfoMap.containsKey(mouseInfoMapKey)) {
            mouseInfoMap.put(mouseInfoMapKey, new CalorimetryData(currentDataPoint, currentDataPoint, currentDataPoint, 1));
        } else {
            CalorimetryData data = mouseInfoMap.get(mouseInfoMapKey);
            if (currentDataPoint < data.min)
                data.min = currentDataPoint;
            if (currentDataPoint > data.max)
                data.max = currentDataPoint;
            data.sum += currentDataPoint;
            data.count++;
        }
    }

    /**
     * Return a list of strings for a single biologicalSampleId suitable for writing to an output file
     *
     * @param mouseInfoDTOs the DTOs for single biologicalSampleId
     *
     * @return a list of strings for a single biologicalSampleId suitable for writing to an output file
     *
     * @throws ReportException
     */
    private List<String> createReportRow(List<ObservationDTO> mouseInfoDTOs) throws ReportException {
        List<String> retVal = new ArrayList<>();

        Map<String, Float[]> mouseInfoMap = new HashMap<>();// key = parameterStableId
                                                                // value = dataPoint.discretePoint[0], [15], [30], [60], [120] for IMPC_IPG_002,
                                                                //         datapoint[0] for the rest.

        mouseInfoMap.put("IMPC_IPG_001_001", new Float[] {null, null, null, null, null});
        mouseInfoMap.put("IMPC_IPG_002_001", new Float[] {null, null, null, null, null});
        mouseInfoMap.put("IMPC_IPG_003_001", new Float[] {null, null, null, null, null});

        for (ObservationDTO mouseInfoDTO : mouseInfoDTOs) {
            String parameterStableId = mouseInfoDTO.getParameterStableId();
            Float dataPoint = mouseInfoDTO.getDataPoint();
            Float[] data = mouseInfoMap.get(parameterStableId);
            String externalSampleId = mouseInfoDTO.getExternalSampleId();
            String[] warnings = new String[]{"", "", ""};
            switch (parameterStableId) {
                case "IMPC_IPG_001_001":
                    if (data[0] != null) {
                        warnings[0] = "Expected only 1 IMPC_IPG_001_001 dataPoint for this mouse but found more.";
                        hasWarnings[0] = true;
                    }

                    data[0] = dataPoint;
                    break;

                case "IMPC_IPG_002_001":
                    List<String> localWarn = new ArrayList<>();
                    Integer discretePoint = mouseInfoDTO.getDiscretePoint().intValue();
                    switch (discretePoint) {
                        case 0:
                            if (data[0] != null) {
                                localWarn.add("Expected only 1 IMPC_IPG_002_001 dataPoint for discretePoint 0 for externalSampleId '" + externalSampleId + "' but found more.");
                            }
                            data[0] = dataPoint;
                            break;

                        case 15:
                            if (data[1] != null) {
                                localWarn.add("Expected only 1 IMPC_IPG_002_001 dataPoint for discretePoint 15 for externalSampleId '" + externalSampleId + "' but found more.");
                            }
                            data[1] = dataPoint;
                            break;

                        case 30:
                            if (data[2] != null) {
                                localWarn.add("Expected only 1 IMPC_IPG_002_001 dataPoint for discretePoint 30 for externalSampleId '" + externalSampleId + "' but found more.");
                            }
                            data[2] = dataPoint;
                            break;

                        case 60:
                            if (data[3] != null) {
                                localWarn.add("Expected only 1 IMPC_IPG_002_001 dataPoint for discretePoint 60 for externalSampleId '" + externalSampleId + "' but found more.");
                            }
                            data[3] = dataPoint;
                            break;

                        case 120:
                            if (data[4] != null) {
                                localWarn.add("Expected only 1 IMPC_IPG_002_001 dataPoint for discretePoint 120 for externalSampleId '" + externalSampleId + "' but found more.");
                            }
                            data[4] = dataPoint;
                            break;

                        default:
                            localWarn.add("Unexpected discretePoint '" + dataPoint + "' for external_sample_id '" + externalSampleId + "'");
                    }
                    if (!localWarn.isEmpty()) {
                        for (int i = 0; i < localWarn.size(); i++) {
                            if (!warnings[1].isEmpty())
                                warnings[1] += "\n";
                            warnings[1] += localWarn.get(i);
                            hasWarnings[1] = true;
                        }
                    }
                    break;

                case "IMPC_IPG_003_001":
                    if (data[0] != null) {
                        warnings[2] = "Expected only 1 IMPC_IPG_003_001 dataPoint for this mouse but found more.";
                        hasWarnings[2] = true;
                    }

                    data[0] = dataPoint;
                    break;

            }

            for (String warning : warnings) {
                if ( ! warning.isEmpty())
                    logger.warn(warning);
            }
        }

        // Build the output row.
System.out.println("mouseInfoDTOs size: " + mouseInfoDTOs.size());
System.out.println("biologicalSampleId = " + mouseInfoDTOs.get(0).getBiologicalSampleId());
        retVal.add(mouseInfoDTOs.get(0).getExternalSampleId());
        retVal.add(mouseInfoDTOs.get(0).getGroup());
        retVal.add(mouseInfoDTOs.get(0).getGeneSymbol());
        retVal.add(mouseInfoDTOs.get(0).getAlleleSymbol());
        retVal.add(mouseInfoDTOs.get(0).getZygosity());
        retVal.add(mouseInfoDTOs.get(0).getSex());
        retVal.add(mouseInfoDTOs.get(0).getColonyId());
        retVal.add(mouseInfoDTOs.get(0).getPhenotypingCenter());
        retVal.add(mouseInfoDTOs.get(0).getMetadataGroup());

        Float[] data = mouseInfoMap.get("IMPC_IPG_001_001");
        if (data != null) {
            if (hasWarnings[2]) {
                retVal.add(DATA_ERROR);
            } else if (data[0] == null) {
                retVal.add(NO_INFO_AVAILABLE);
            } else {
                retVal.add(Float.toString(data[0]));
            }
        } else {
            retVal.add(NO_INFO_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_IPG_002_001 (time point)");
        if (data != null) {
            if (hasWarnings[1]) {
                retVal.add(DATA_ERROR);
            } else {
                retVal.add(StringUtils.join(data, "::"));

//                for (int i = 0; i < data.length; i++) {
//                    if (data[i] == null) {
//                        retVal.add(NO_INFO_AVAILABLE);
//                    } else {
//                        retVal.add(Float.toString(data[i]));
//                    }
//                }
            }
        } else {
            retVal.add(NO_INFO_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_IPG_003_001");
        if (data != null) {
            if (hasWarnings[2]) {
                retVal.add(DATA_ERROR);
            } else if (data[0] == null) {
                retVal.add(NO_INFO_AVAILABLE);
            } else {
                retVal.add(Float.toString(data[0]));
            }
        } else {
            retVal.add(NO_INFO_AVAILABLE);
        }

        // Metadata

        List<String> metadataList = mouseInfoDTOs.get(0).getMetadata();
        if (metadataList != null) {
            retVal.add(StringUtils.join(metadataList, "::"));
        } else {
            retVal.add(NO_INFO_AVAILABLE);
        }

        return retVal;
    }
}