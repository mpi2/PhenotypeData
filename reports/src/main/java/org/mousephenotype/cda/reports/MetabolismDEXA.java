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
 * Metabolism (DEXA) report.
 *
 * Created by mrelac on 28/07/2015.
 */
@Component
public class MetabolismDEXA extends AbstractReport {

    @Autowired
    ExperimentService experimentService;

    @Autowired
    ObservationService observationService;

    private String[] header = new String[] {
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

        "Body weight IMPC_DXA_001_001",
        "Fat mass IMPC_DXA_002_001",
        "Lean mass IMPC_DXA_003_001",

        "Metadata"
    };

    public MetabolismDEXA() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            log.error("MetabolismDEXAReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        csvWriter.write(header);

        int count = 0;

        try {
            Collection<String> sampleIds = observationService.getMetabolismReportBiologicalSampleIds("IMPC_DXA_*");
            for (String sampleId : sampleIds) {
                List<ObservationDTO> mouseInfoDTOs = observationService.getMetabolismReportBiologicalSampleId("IMPC_DXA_*", sampleId);
                csvWriter.write(createReportRow(mouseInfoDTOs));
                if (++count % 10000 == 0)
                    log.debug(new Date().toString() + ": " + count + " records written.");
            }

            csvWriter.close();

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception in MetabolismDEXAReport. Reason: " + e.getLocalizedMessage());
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
        List<String> retVal = new ArrayList<>();

        Map<String, List<Float>> mouseInfoMap = new HashMap<>();    // key = parameterStableId
                                                                // value = dataPoint

        mouseInfoMap.put("IMPC_DXA_001_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_DXA_002_001", new ArrayList<>());
        mouseInfoMap.put("IMPC_DXA_003_001", new ArrayList<>());

        for (ObservationDTO mouseInfoDTO : mouseInfoDTOs) {
            if (mouseInfoMap.containsKey(mouseInfoDTO.getParameterStableId())) {
                mouseInfoMap.get(mouseInfoDTO.getParameterStableId()).add(mouseInfoDTO.getDataPoint());
            }
        }

        String externalSampleId = mouseInfoDTOs.get(0).getExternalSampleId();

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

        List<Float> data = mouseInfoMap.get("IMPC_DXA_001_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_DXA_001_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_DXA_002_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_DXA_002_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
            }
        } else {
            retVal.add(Constants.NO_INFORMATION_AVAILABLE);
        }

        data = mouseInfoMap.get("IMPC_DXA_003_001");
        if (data != null) {
            if (data.size() > 1) {
                log.debug("Multiple values found for simple parameter IMPC_DXA_003_001 for externalSampleId " + externalSampleId);
                retVal.add(DATA_ERROR);
            } else if (data.isEmpty()) {
                retVal.add(Constants.NO_INFORMATION_AVAILABLE);
            } else {
                retVal.add(Float.toString(data.get(0)));
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