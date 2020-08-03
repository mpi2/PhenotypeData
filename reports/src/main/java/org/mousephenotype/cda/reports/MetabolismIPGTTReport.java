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
 * Metabolism (glucose tolerance procedure) report.
 *
 * Created by mrelac on 28/07/2015.
 */
@Component
public class MetabolismIPGTTReport extends AbstractReport {

    @Autowired
    ExperimentService experimentService;

    @Autowired
    ObservationService observationService;

    private String[] header = new String[] {
             "Mouse Id", "Sample Type", "Gene Symbol", "MGI Gene Id", "Allele Symbol", "Zygosity"
            ,"Sex", "Colony Id", "Phenotyping Center", "Metadata Group"

            ,"Body Weight IMPC_IPG_001_001"

            ,"Blood glucose concentration - time point 0 IMPC_IPG_002_001"
            ,"Blood glucose concentration - time point 15 IMPC_IPG_002_001"
            ,"Blood glucose concentration - time point 30 IMPC_IPG_002_001"
            ,"Blood glucose concentration - time point 60 IMPC_IPG_002_001"
            ,"Blood glucose concentration - time point 120 IMPC_IPG_002_001"

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

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            log.error("MetabolismIPGTTReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        csvWriter.writeNext(header);

        int count = 0;

        try {
            Collection<String> biologicalSampleIds = observationService.getMetabolismReportBiologicalSampleIds("IMPC_IPG_*");
            for (String biologicalSampleId : biologicalSampleIds) {
//if (count >= 1000) break;
                Integer lBiologicalSampleId = commonUtils.tryParseInt(biologicalSampleId);
                if (lBiologicalSampleId != null) {
                    List<ObservationDTO> mouseInfoDTOs = observationService.getMetabolismReportBiologicalSampleId("IMPC_IPG_*", lBiologicalSampleId);
                    csvWriter.writeRow(createReportRow(mouseInfoDTOs));
                    if (++count % 1000 == 0)
                        log.debug(new Date().toString() + ": " + count + " records written.");
                }
            }

            csvWriter.close();

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception in MetabolismIPGTTReport. Reason: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. " + count + " records written in " + commonUtils.msToHms(System.currentTimeMillis() - start)));
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

	    boolean[] hasWarnings = {false, false, false};  // Parameters IMPC_IPG_001_001, 002_001, and 010_001

	    List<String> retVal = new ArrayList<>();

        Map<String, Float[]> mouseInfoMap = new HashMap<>();    // key = parameterStableId
                                                                // value = dataPoint.discretePoint[0], [15], [30], [60], [120] for IMPC_IPG_002,
                                                                //         datapoint[0] for the rest.

        mouseInfoMap.put("IMPC_IPG_001_001", new Float[] {null, null, null, null, null});
        mouseInfoMap.put("IMPC_IPG_002_001", new Float[] {null, null, null, null, null});
        mouseInfoMap.put("IMPC_IPG_010_001", new Float[] {null, null, null, null, null});

        for (ObservationDTO mouseInfoDTO : mouseInfoDTOs) {
            String parameterStableId = mouseInfoDTO.getParameterStableId();
            Float dataPoint = mouseInfoDTO.getDataPoint();
            Float[] data = mouseInfoMap.get(parameterStableId);
            String externalSampleId = mouseInfoDTO.getExternalSampleId();
            String[] warnings = new String[]{"", "", ""};
            switch (parameterStableId) {
                case "IMPC_IPG_001_001":
                    if (data[0] != null) {
                        warnings[0] = "Multiple values found for simple parameter IMPC_IPG_001_001 dataPoint for externalSampleId " + externalSampleId;
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
                                localWarn.add("Multiple values found for simple parameter IMPC_IPG_002_001 dataPoint for discretePoint 0 for externalSampleId " + externalSampleId);
                            }
                            data[0] = dataPoint;
                            break;

                        case 15:
                            if (data[1] != null) {
                                localWarn.add("Multiple values found for simple parameter IMPC_IPG_002_001 dataPoint for discretePoint 15 for externalSampleId " + externalSampleId);
                            }
                            data[1] = dataPoint;
                            break;

                        case 30:
                            if (data[2] != null) {
                                localWarn.add("Multiple values found for simple parameter IMPC_IPG_002_001 dataPoint for discretePoint 30 for externalSampleId " + externalSampleId);
                            }
                            data[2] = dataPoint;
                            break;

                        case 60:
                            if (data[3] != null) {
                                localWarn.add("Multiple values found for simple parameter IMPC_IPG_002_001 dataPoint for discretePoint 60 for externalSampleId " + externalSampleId);
                            }
                            data[3] = dataPoint;
                            break;

                        case 120:
                            if (data[4] != null) {
                                localWarn.add("Multiple values found for simple parameter IMPC_IPG_002_001 dataPoint for discretePoint 120 for externalSampleId " + externalSampleId);
                            }
                            data[4] = dataPoint;
                            break;

                        default:
                            localWarn.add("Unexpected discretePoint '" + dataPoint + "' for externalSampleId " + externalSampleId);
                    }
                    if (!localWarn.isEmpty()) {
	                    for (String aLocalWarn : localWarn) {
		                    if (!warnings[1].isEmpty()) warnings[1] += "\n";
		                    warnings[1] += aLocalWarn;
		                    hasWarnings[1] = true;
	                    }
                    }
                    break;

                case "IMPC_IPG_010_001":
                    if (data[0] != null) {
                        warnings[2] = "Multiple values found for simple parameter IMPC_IPG_010_001 dataPoint for discretePoint 15 for externalSampleId " + externalSampleId;
                        hasWarnings[2] = true;
                    }

                    data[0] = dataPoint;
                    break;

            }

            for (String warning : warnings) {
                if ( ! warning.isEmpty())
                    log.debug(warning);
            }
        }

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

        Float[] data = mouseInfoMap.get("IMPC_IPG_001_001");
        if (data != null) {
            if (hasWarnings[0]) {
                retVal.add(DATA_ERROR);
            } else if (data[0] == null) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data[0]));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_IPG_002_001");
        if (data != null) {
            if (hasWarnings[1]) {
	            for (int i=0; i<data.length; i++) {
		            retVal.add(DATA_ERROR);
	            }
            } else {
	            for (Float aData : data) {
		            if (aData == null) {
			            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
		            } else {
			            retVal.add(Float.toString(aData));
		            }
	            }
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_IPG_010_001");
        if (data != null) {
            if (hasWarnings[2]) {
                retVal.add(DATA_ERROR);
            } else if (data[0] == null) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data[0]));
            }
        } else {
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