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
 * Metabolism (calorimetry procedure) report.
 *
 * Created by mrelac on 28/07/2015.
 */
@Component
public class MetabolismCalorimetry extends AbstractReport {

    @Autowired
    ExperimentService experimentService;

    @Autowired
    ObservationService observationService;

    private String[] header = {
        "Gene Symbol",
        "Gene Accession Id",
        "Allele Symbol",
        "Allele Accession Id",
        "Background Strain Name",
        "Background Strain Accession Id",
        "Colony Id",
        "Phenotyping Center",
        "Mouse Id",
        "Sample Type",
        "Zygosity",
        "Sex",
        "Metadata Group",

        "Body weight before experiment IMPC_CAL_001_001",
        "Body weight after experiment IMPC_CAL_002_001",

        "Oxygen consumption min IMPC_CAL_003_001",
        "Oxygen consumption max IMPC_CAL_003_001",
        "Oxygen consumption mean IMPC_CAL_003_001",
        "Oxygen consumption count IMPC_CAL_003_001",

        "Carbon dioxide production min IMPC_CAL_004_001",
        "Carbon dioxide production max IMPC_CAL_004_001",
        "Carbon dioxide production mean IMPC_CAL_004_001",
        "Carbon dioxide production count IMPC_CAL_004_001",

        "Heat production (metabolic rate) min IMPC_CAL_005_001",
        "Heat production (metabolic rate) max IMPC_CAL_005_001",
        "Heat production (metabolic rate) mean IMPC_CAL_005_001",
        "Heat production (metabolic rate) count IMPC_CAL_005_001",

        "Ambulatory activity (no. of beam cuts) min IMPC_CAL_006_001",
        "Ambulatory activity (no. of beam cuts) max IMPC_CAL_006_001",
        "Ambulatory activity (no. of beam cuts) mean IMPC_CAL_006_001",
        "Ambulatory activity (no. of beam cuts) count IMPC_CAL_006_001",

        "Total activity (no. of fine movement + no. of beam cuts) min IMPC_CAL_007_001",
        "Total activity (no. of fine movement + no. of beam cuts) max IMPC_CAL_007_001",
        "Total activity (no. of fine movement + no. of beam cuts) mean IMPC_CAL_007_001",
        "Total activity (no. of fine movement + no. of beam cuts) count IMPC_CAL_007_001",

        "Total food intake IMPC_CAL_008_001",

        "Cumulative food intake min IMPC_CAL_009_001",
        "Cumulative food intake max IMPC_CAL_009_001",
        "Cumulative food intake mean IMPC_CAL_009_001",
        "Cumulative food intake count IMPC_CAL_009_001",

        "Respiratory Exchange Ratio IMPC_CAL_017_001",

        "Total water intake IMPC_CAL_021_001",

        "Cumulative water intake min IMPC_CAL_022_001",
        "Cumulative water intake max IMPC_CAL_022_001",
        "Cumulative water intake mean IMPC_CAL_022_001",
        "Cumulative water intake count IMPC_CAL_022_001",

        "Respiratory Exchange Ratio series min IMPC_CAL_042_001",
        "Respiratory Exchange Ratio series max IMPC_CAL_042_001",
        "Respiratory Exchange Ratio series mean IMPC_CAL_042_001",
        "Respiratory Exchange Ratio series count IMPC_CAL_042_001",

        "Respiratory Exchange Ratio Derived min",
        "Respiratory Exchange Ratio Derived max",
        "Respiratory Exchange Ratio Derived mean",
        "Respiratory Exchange Ratio Derived count",

        "Metadata"
    };

    public static final String RER_DATA_PARAMETER = "rerDataParameter";

    public MetabolismCalorimetry() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            log.error("MetabolismCalorimetryReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        csvWriter.write(header);
        int count = 0;

        try {
            Collection<String> sampleIds = observationService.getMetabolismReportBiologicalSampleIds("IMPC_CAL_*");
            for (String sampleId : sampleIds) {
                List<ObservationDTO> dtos = observationService.getMetabolismReportBiologicalSampleId("IMPC_CAL_*", sampleId);
                csvWriter.write(createReportRow(dtos));
                if (++count % 10000 == 0)
                    log.debug("  " + count + " records written.");
            }

            csvWriter.close();

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception in MetabolismCalorimetryReport. Reason: " + e.getLocalizedMessage());
        }

        log.info(String.format(
            "Finished. %s rows written in %s",
            count, commonUtils.msToHms(System.currentTimeMillis() - start)));
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
    private void

    accumulateSeriesParameterValues(Map<String, CalorimetryData> mouseInfoMap, String mouseInfoMapKey, float currentDataPoint) {
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

	    boolean hasRerWarnings = false;

	    List<String> retVal = new ArrayList<>();
        Map<String, CalorimetryData> mouseInfoMap = new HashMap<>();  // key = parameterStableId or equivalent parameter identifier (e.g. RER_DATA_PARAMETER)
                                                                      // value = CalorimetryData instance

        // Compute RER values using IMPC_CAL_004_001 mean of increments / IMPC_CAL_003_001 mean of increments.
        String[] warnings = new String[] { "", "" };                 // [0] = too many 003 parameters. [1] = too many 004 parameters.
        Map<Float, Float[]> rerMap = new HashMap<>();                // key: discretePoint. value[0]: IMPC_CAL_003_001 value. value[1]: IMPC_CAL_004_001. value[2]: 004 / 003.

        for (ObservationDTO mouseInfoDTO : mouseInfoDTOs) {
            String parameterStableId = mouseInfoDTO.getParameterStableId();
            // Skip parameter_stable_ids except for 003 and 004.
            if (( ! parameterStableId.equals("IMPC_CAL_003_001")) &&
                ( ! parameterStableId.equals("IMPC_CAL_004_001"))) {
                continue;
            }

            Float discretePoint = mouseInfoDTO.getDiscretePoint();
            Float dataPoint = mouseInfoDTO.getDataPoint();
            String externalSampleId = mouseInfoDTO.getExternalSampleId();
            if ( ! rerMap.containsKey(discretePoint)) {
                rerMap.put(discretePoint, new Float[]{null, null, null});
            }
            Float[] rer = rerMap.get(discretePoint);

            switch (parameterStableId) {
                case "IMPC_CAL_003_001":
                    if (rer[0] != null) {
                        warnings[0] = "Multiple values found for series parameter IMPC_CAL_003_001, discretePoint '" + discretePoint + "' for externalSampleId " + externalSampleId;
                        hasRerWarnings = true;
                    }

                    rer[0] = dataPoint;
                    break;

                case "IMPC_CAL_004_001":
                    if (rer[1] != null) {
                        warnings[1] = "Multiple values found for series parameter IMPC_CAL_004_001, discretePoint '" + discretePoint + "' for externalSampleId " + externalSampleId;
                        hasRerWarnings = true;
                    }

                    rer[1] = dataPoint;
                    break;
            }

            if ((rer[0] != null) && (rer[1] != null)) {
                rer[2] = rer[1] / rer[0];
                accumulateSeriesParameterValues(mouseInfoMap, RER_DATA_PARAMETER, rer[2] );
            }
        }

        for (String warning : warnings) {
            if ( ! warning.isEmpty())
                log.debug(warning);              // MPII-1728 - Treat these as debug/info statements.
        }

        // Accumulate the min, max, sum, and count for all parameters - even simple ones. A simple parameter is expected
        // to be a single value. For such parameters, a check is done after all values are accumulated to validate that
        // there is no more than one such value. If there is more than one, a warning is logged.
        for (ObservationDTO mouseInfoDTO : mouseInfoDTOs) {
            String mouseInfoMapKey = mouseInfoDTO.getParameterStableId();
            switch (mouseInfoMapKey) {
                case "IMPC_CAL_001_001":
                case "IMPC_CAL_002_001":
                case "IMPC_CAL_008_001":
                case "IMPC_CAL_017_001":
                case "IMPC_CAL_021_001":
                case "IMPC_CAL_003_001":
                case "IMPC_CAL_004_001":
                case "IMPC_CAL_005_001":
                case "IMPC_CAL_006_001":
                case "IMPC_CAL_007_001":
                case "IMPC_CAL_009_001":
                case "IMPC_CAL_022_001":
                case "IMPC_CAL_042_001":
                    accumulateSeriesParameterValues(mouseInfoMap, mouseInfoMapKey, mouseInfoDTO.getDataPoint());
                    break;
            }

            // Compute the RER the same way as above, fetching the current data point from the rerMap.
            Float[] rerData = rerMap.get(mouseInfoDTO.getDiscretePoint());
            if ((rerData != null) && (rerData[2] != null)) {
                accumulateSeriesParameterValues(mouseInfoMap, RER_DATA_PARAMETER, rerData[2]);
            }
        }

        // Build the output row.
        retVal.add(mouseInfoDTOs.get(0).getGeneSymbol());
        retVal.add(mouseInfoDTOs.get(0).getGeneAccession());
        retVal.add(mouseInfoDTOs.get(0).getAlleleSymbol());
        retVal.add(mouseInfoDTOs.get(0).getAlleleAccession());
        retVal.add(mouseInfoDTOs.get(0).getStrainName());
        retVal.add(mouseInfoDTOs.get(0).getStrainAccessionId());
        retVal.add(mouseInfoDTOs.get(0).getColonyId());
        retVal.add(mouseInfoDTOs.get(0).getPhenotypingCenter());
        retVal.add(mouseInfoDTOs.get(0).getExternalSampleId());
        retVal.add(mouseInfoDTOs.get(0).getGroup());
        retVal.add(mouseInfoDTOs.get(0).getZygosity());
        retVal.add(mouseInfoDTOs.get(0).getSex());
        retVal.add(mouseInfoDTOs.get(0).getMetadataGroup());

        String externalSampleId = mouseInfoDTOs.get(0).getExternalSampleId();
        CalorimetryData calorimetryData = mouseInfoMap.get("IMPC_CAL_001_001");
        if (calorimetryData != null) {
            if (calorimetryData.count > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CAL_001_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else {
                retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_001_001").min));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_002_001");
        if (calorimetryData != null) {
            if (calorimetryData.count > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CAL_002_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else {
                retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_002_001").min));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_003_001");
        if (calorimetryData != null) {
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_003_001").min));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_003_001").max));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_003_001").sum / mouseInfoMap.get("IMPC_CAL_003_001").count));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_003_001").count));
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_004_001");
        if (calorimetryData != null) {
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_004_001").min));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_004_001").max));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_004_001").sum / mouseInfoMap.get("IMPC_CAL_004_001").count));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_004_001").count));
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_005_001");
        if (calorimetryData != null) {
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_005_001").min));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_005_001").max));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_005_001").sum / mouseInfoMap.get("IMPC_CAL_005_001").count));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_005_001").count));
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_006_001");
        if (calorimetryData != null) {
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_006_001").min));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_006_001").max));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_006_001").sum / mouseInfoMap.get("IMPC_CAL_006_001").count));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_006_001").count));
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_007_001");
        if (calorimetryData != null) {
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_007_001").min));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_007_001").max));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_007_001").sum / mouseInfoMap.get("IMPC_CAL_007_001").count));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_007_001").count));
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_008_001");
        if (calorimetryData != null) {
            if (calorimetryData.count > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CAL_008_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else {
                retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_008_001").min));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_009_001");
        if (calorimetryData != null) {
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_009_001").min));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_009_001").max));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_009_001").sum / mouseInfoMap.get("IMPC_CAL_009_001").count));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_009_001").count));
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_017_001");
        if (calorimetryData != null) {
            if (calorimetryData.count > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CAL_017_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else {
                retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_017_001").min));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_021_001");
        if (calorimetryData != null) {
            if (calorimetryData.count > 1) {
                log.debug("Multiple values found for simple parameter IMPC_CAL_021_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else {
                retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_021_001").min));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_022_001");
        if (calorimetryData != null) {
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_022_001").min));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_022_001").max));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_022_001").sum / mouseInfoMap.get("IMPC_CAL_022_001").count));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_022_001").count));
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get("IMPC_CAL_042_001");
        if (calorimetryData != null) {
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_042_001").min));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_042_001").max));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_042_001").sum / mouseInfoMap.get("IMPC_CAL_042_001").count));
            retVal.add(Float.toString(mouseInfoMap.get("IMPC_CAL_042_001").count));
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        calorimetryData = mouseInfoMap.get(RER_DATA_PARAMETER);
        if (calorimetryData != null) {
            if (hasRerWarnings) {
                retVal.add(DATA_ERROR);
                retVal.add(DATA_ERROR);
                retVal.add(DATA_ERROR);
                retVal.add(DATA_ERROR);
            } else {
                retVal.add(Float.toString(mouseInfoMap.get(RER_DATA_PARAMETER).min));
                retVal.add(Float.toString(mouseInfoMap.get(RER_DATA_PARAMETER).max));
                retVal.add(Float.toString(mouseInfoMap.get(RER_DATA_PARAMETER).sum / mouseInfoMap.get(RER_DATA_PARAMETER).count));
                retVal.add(Float.toString(mouseInfoMap.get(RER_DATA_PARAMETER).count));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        // Metadata

        List<String> metadataList = mouseInfoDTOs.get(0).getMetadata();
        if (metadataList != null) {
            retVal.add(StringUtils.join(metadataList, "::"));
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        return retVal;
    }
}