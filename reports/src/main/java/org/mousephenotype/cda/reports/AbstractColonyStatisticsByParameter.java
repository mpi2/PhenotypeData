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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.ExperimentService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Base class for Bone Mineral Density (Bmd) reports.
 * <p>
 * Created by mrelac on 28/07/2015.
 */
@Deprecated
public abstract class AbstractColonyStatisticsByParameter extends AbstractReport {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    private final Set<String> missingData    = new HashSet<>();
    private final Set<String> mismatchedData = new HashSet<>();
    protected int count = 0;

    @Autowired
    ExperimentService experimentService;

    @Autowired
    ObservationService observationService;

    // The prefixed spaces have a side effect that the heading comes first when sorting.
    protected String[] header = {
        " Gene Symbol",
        " Gene Accession Id",
        " Allele Symbol",
        " Allele Accession Id",
        " Background Strain Name",
        " Background Strain Accession Id",
        " Colony Id",
        " Phenotyping Center",
        " Parameter Name",
        " Parameter Id",
        " First Experiment Date",
        " Last Experiment Date",
        " Mean WT Male", " Median WT Male", " SD WT Male", " N WT Male",
        " Mean HOM Male", " Median HOM Male", " SD HOM Male", " N HOM Male",
        " Mean HET Male", " Median HET Male", " SD HET Male", " N HET Male",
        " Mean HEM Male", " Median HEM Male", " SD HEM Male", " N HEM Male",
        " Mean WT Female", " Median WT Female", " SD WT Female", " N WT Female",
        " Mean HOM Female", " Median HOM Female", " SD HOM Female", " N HOM Female",
        " Mean HET Female", " Median HET Female", " SD HET Female", " N HET Female"
    };


    public AbstractColonyStatisticsByParameter() {
        super();
    }

    public void run(String parameter) throws ReportException {

        csvWriter.write(header);

        /*
         * Steps:
         * 1. Gather the data without grouping
         * 2. Validate all colony data, logging and filtering out required missing fields
         * 3. Order by gene symbol
         * 4. Compute values for each set of validated colony data
         * 5. Sort by: geneSymbol, alleleSymbol, StrainName, phenotypingCenter
         * 6. Write out the rows
         * 7. Log any errors
         */
        try {
            Map<String, Map<String, Set<ObservationDTO>>> dtosByColony =
                observationService.getDataPointsByColony(resources, parameter);

            Map<String, Map<String, Set<ObservationDTO>>> validatedDtosByColony =
                getValidatedColonyData(dtosByColony);
            List<List<String>> matrix = new ArrayList<>();
            for (Map<String, Set<ObservationDTO>> colonyStrainData : validatedDtosByColony.values()) {
                matrix.add(buildColonyOutputRow(colonyStrainData));
            }

            // Sort by: geneSymbol (0), alleleSymbol (2), strainName (4), center (7)
            matrix = matrix
                .stream()
                .sorted(Comparator.comparing((List<String> l) -> l.get(0))
                .thenComparing((l) -> l.get(2))
                .thenComparing((l) -> l.get(4))
                .thenComparing((l) -> l.get(7)))
                .collect(Collectors.toList());

            csvWriter.writeRows(matrix);
            count = matrix.size();
            if ( ! missingData.isEmpty()) {
                logErrors(missingData, missingData.size() + " rows missing required report data:");
            }

            if ( ! mismatchedData.isEmpty()) {
                logErrors(mismatchedData,
                          mismatchedData.size() +
                              " rows of mismatched data. Any rows here indicate the data is split and a new row should be output:");
            }

            csvWriter.close();

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Report creation failed. Reason: " + e.getLocalizedMessage());
        }
    }

    void logErrors(Set<String> errors, String errorExplanation) {
        System.out.println();
        log.warn(errorExplanation);
        System.out.println();
        System.out.println("geneSymbol::geneAccessionId::alleleSymbol::alleleAccessionId::strainSymbol::" +
                               "strainAccessionId::colony::center::pipeline::parameter");
        errors
            .stream()
            .forEach(message -> {
                System.out.println("  " + message);
            });
    }

    /**
     * Validates colony data
     *
     * @param allColonyObservations colony data map for all colonies, keyed by colonyId
     * @return a map of all validated colony data (with invalid colony entries removed)
     */
    private Map<String, Map<String, Set<ObservationDTO>>> getValidatedColonyData(
        Map<String, Map<String, Set<ObservationDTO>>> allColonyObservations) {
        Map<String, Map<String, Set<ObservationDTO>>> validatedColonyData = new HashMap<>();

        for (Map.Entry<String, Map<String, Set<ObservationDTO>>> colonyObservations : allColonyObservations.entrySet()) {
            String              colonyKey                           = colonyObservations.getKey();
            Set<ObservationDTO> colonyObservationExperimentalValues = colonyObservations.getValue().get("experimental");
            if (colonyObservationExperimentalValues != null) {
                if (colonyFieldsNotNull(colonyObservationExperimentalValues)) {
                    validatedColonyData.put(colonyKey, colonyObservations.getValue());
                    colonyFieldsMatch(colonyObservationExperimentalValues);
                }
            }
        }

        return validatedColonyData;
    }

    //  Required: colonyId, phenotypingCenter, strainAccessionId, strainName, pipelineStableId, parameterStableId,
    //            geneAccessionId, geneSymbol, alleleSymbol
    private boolean colonyFieldsNotNull(Set<ObservationDTO> dtos) {
        boolean notNull = true;
        for (ObservationDTO dto : dtos) {
            if ((dto.getGeneSymbol() == null)
                || (dto.getGeneAccession() == null)
                || (dto.getAlleleSymbol() == null)
                || (dto.getAlleleAccession() == null)
                || (dto.getStrainName() == null)
                || (dto.getStrainAccessionId() == null)
                || (dto.getColonyId() == null)
                || (dto.getPhenotypingCenter() == null)
                || (dto.getPipelineStableId() == null)
                || (dto.getParameterName() == null)
                || (dto.getParameterStableId() == null)) {
                notNull = false;
                String message = buildMessage(dto);
                missingData.add(message);
            }
        }

        return notNull;
    }

    // Return true if colony fields for the given dtos match; false otherwise
    private boolean colonyFieldsMatch(Set<ObservationDTO> dtos) {
        boolean colonyFieldsMatch;

        ObservationDTO dto = dtos.iterator().next();
        if ((dtos.stream().allMatch(d -> d.getGeneSymbol().equals(dto.getGeneSymbol())))
            && (dtos.stream().allMatch(d -> d.getGeneAccession().equals(dto.getGeneAccession())))
            && (dtos.stream().allMatch(d -> d.getAlleleSymbol().equals(dto.getAlleleSymbol())))
            && (dtos.stream().allMatch(d -> d.getAlleleAccession().equals(dto.getAlleleAccession())))
            && (dtos.stream().allMatch(d -> d.getStrainName().equals(dto.getStrainName())))
            && (dtos.stream().allMatch(d -> d.getStrainAccessionId().equals(dto.getStrainAccessionId())))
            && (dtos.stream().allMatch(d -> d.getColonyId().equals(dto.getColonyId())))
            && (dtos.stream().allMatch(d -> d.getPhenotypingCenter().equals(dto.getPhenotypingCenter())))
            && (dtos.stream().allMatch(d -> d.getPipelineStableId().equals(dto.getPipelineStableId())))
            && (dtos.stream().allMatch(d -> d.getParameterName().equals(dto.getParameterName())))
            && (dtos.stream().allMatch(d -> d.getParameterStableId().equals(dto.getParameterStableId())))) {
            colonyFieldsMatch = true;
        } else {
            colonyFieldsMatch = false;
            String message = buildMessage(dto);
            mismatchedData.add(message);
        }

        return colonyFieldsMatch;
    }

    private String buildMessage(ObservationDTO dto) {
        String n = "<null>";
        String result =
            (dto.getGeneSymbol() == null ? n : dto.getGeneSymbol()) + "::" +
                (dto.getGeneAccession() == null ? n : dto.getGeneAccession()) + "::" +
                (dto.getAlleleSymbol() == null ? n : dto.getAlleleSymbol()) + "::" +
                (dto.getAlleleAccession() == null ? n : dto.getAlleleAccession()) + "::" +
                (dto.getStrainName() == null ? n : dto.getStrainName()) + "::" +
                (dto.getStrainAccessionId() == null ? n : dto.getStrainAccessionId()) + "::" +
                (dto.getColonyId() == null ? n : dto.getColonyId()) + "::" +
                (dto.getPhenotypingCenter() == null ? n : dto.getPhenotypingCenter()) + "::" +
                (dto.getPipelineStableId() == null ? n : dto.getPipelineStableId()) + "::" +
                (dto.getParameterStableId() == null ? n : dto.getParameterStableId());

        return result;
    }


    // PRIVATE METHODS


    private List<String> buildColonyOutputRow(Map<String, Set<ObservationDTO>> colonyDataAllGroups) {
        List<String>        row                    = new ArrayList<>();
        Set<ObservationDTO> colonyDataControl      = colonyDataAllGroups.get("control");
        Set<ObservationDTO> colonyDataExperimental = colonyDataAllGroups.get("experimental");
        Date firstExperimentDate = colonyDataExperimental
            .stream()
            .map(ObservationDTO::getDateOfExperiment).min(Date::compareTo).get();
        Date lastExperimentDate = colonyDataExperimental
            .stream()
            .map(ObservationDTO::getDateOfExperiment).max(Date::compareTo).get();

        ObservationDTO data = colonyDataExperimental.iterator().next();
        row.add(data.getGeneSymbol());
        row.add(data.getGeneAccession());
        row.add(data.getAlleleSymbol());
        row.add(data.getAlleleAccession());
        row.add(data.getStrainName());
        row.add(data.getStrainAccessionId());
        row.add(data.getColonyId());
        row.add(data.getPhenotypingCenter());
        row.add(data.getParameterName());
        row.add(data.getParameterStableId());
        row.add(super.formatDate(firstExperimentDate));
        row.add(super.formatDate(lastExperimentDate));

        // Remap null dataPoint values to -1, then show NO_DATA for those values in the report.
        for (String sex : new String[]{"male", "female"}) {
            for (String zygosity : new String[]{"WT", "homozygote", "heterozygote", "hemizygote"}) {

                if (sex.equalsIgnoreCase("female") && zygosity.equalsIgnoreCase("hemizygote")) {
                    // Skip female hemizygote
                } else {
                    Set<ObservationDTO> colonyData =
                        (zygosity.equalsIgnoreCase("WT") ? colonyDataControl : colonyDataExperimental);
                    if (colonyData == null)
                        colonyData = new HashSet<>();

                    List<Double> values = colonyData
                        .stream()
                        .filter(ds -> ds.getZygosity().equalsIgnoreCase(zygosity.equalsIgnoreCase("WT") ? ds.getZygosity() : zygosity))
                        .filter(ds -> ds.getSex().equalsIgnoreCase(sex))
                        .filter(ds -> ds.getDataPoint() != null)
                        .map(o -> o.getDataPoint().doubleValue())
                        .collect(Collectors.toList());

                    if (values.isEmpty()) {
                        row.add(Constants.NO_INFORMATION_AVAILABLE);
                        row.add(Constants.NO_INFORMATION_AVAILABLE);
                        row.add(Constants.NO_INFORMATION_AVAILABLE);
                        row.add(Constants.NO_INFORMATION_AVAILABLE);
                    } else {
                        DescriptiveStatistics ds = new DescriptiveStatistics(
                            ArrayUtils.toPrimitive(values.toArray(new Double[0])));

                        Double d = ds.getMean();
                        row.add(d.isInfinite() || d.isNaN() ? Constants.NO_INFORMATION_AVAILABLE : d.toString());

                        d = getMedian(values);
                        row.add(d.isInfinite() || d.isNaN() ? Constants.NO_INFORMATION_AVAILABLE : d.toString());

                        d = ds.getStandardDeviation();
                        row.add(d.isInfinite() || d.isNaN() ? Constants.NO_INFORMATION_AVAILABLE : d.toString());

                        row.add(new Double(ds.getN()).toString());
                    }
                }
            }
        }

        return row;
    }

    private Double getMedian(List<Double> doubles) {

        Double       median;
        int          middle = doubles.size() / 2;
        Collections.sort(doubles);

        if (doubles.isEmpty()) {
            return Double.NaN;
        }
        if (doubles.size() % 2 == 0) {
            median = (doubles.get(middle - 1) + doubles.get(middle)) / 2;
        } else {
            median = doubles.get(middle);
        }

        return median;
    }
}
