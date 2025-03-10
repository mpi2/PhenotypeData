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
import org.mousephenotype.cda.solr.service.ExpressionService;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.beans.Introspector;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Lac-Z Expression report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class LaczExpression extends AbstractReport {

    protected Logger       logger = LoggerFactory.getLogger(this.getClass());
    protected String       reportsHostname;
    protected ImageService imageService;
    protected ExpressionService expressionService;

    public final String IMAGE_COLLECTION_LINK_BASE_KEY = "image_collection_link_base";
    public String imageCollectionLinkBase = "https://www.mousephenotype.org/data";

    @Inject
    public LaczExpression(@NotNull ImageService imageService,
                          @NotNull ExpressionService expressionService) {
        super();
        this.imageService = imageService;
        this.expressionService = expressionService;
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException, IOException, SolrServerException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("LaczExpression parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);
        PropertySource ps = new SimpleCommandLinePropertySource(args);
        if (ps.containsProperty(IMAGE_COLLECTION_LINK_BASE_KEY)) {
            imageCollectionLinkBase = ps.getProperty(IMAGE_COLLECTION_LINK_BASE_KEY).toString();
            throw new ReportException("Required reports_hostname parameter is missing. Format is like http://www.mousephenotype.org.");
        }

        long start = System.currentTimeMillis();
        // Sort by: geneSymbol (0), alleleSymbol (2), strainName (4), zygosity (8), sex (9)
        final List<ObservationDTO> data = expressionService.getCategoricalAdultLacZDataForReport();
        final Map<String, Set<String>> imageAvailable = imageService.getLaczImagesAvailable();
        final List<List<String>> results = getLaczExpressionSpreadsheet(data, imageAvailable)

            .stream()
            .sorted(Comparator.comparing((List<String> l) -> l.get(0))
                        .thenComparing((l) -> l.get(2))
                        .thenComparing((l) -> l.get(4))
                        .thenComparing((l) -> l.get(8))
                        .thenComparing((l) -> l.get(9)))
            .collect(Collectors.toList());

        csvWriter.writeRows(results);

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format(
            "Finished. %s rows written in %s",
            1 + results.size(), commonUtils.msToHms(System.currentTimeMillis() - start)));
    }

    protected void initialise(String[] args) throws ReportException {
        super.initialise(args);
    }

    public List<List<String>> getLaczExpressionSpreadsheet(List<ObservationDTO> data, Map<String, Set<String>> imageAvailable) {
        List<List<String>> result = new ArrayList<>();
        final List<String> allParameters = data
                .stream()
                .map(ObservationDTO::getParameterName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<String> heading = new ArrayList<>();
        heading.add("Gene Symbol");
        heading.add("Gene Accession Id");
        heading.add("Allele Symbol");
        heading.add("Allele Accession Id");
        heading.add("Background Strain Name");
        heading.add("Background Strain Accession Id");
        heading.add("Colony Id");
        heading.add("External Sample Id");
        heading.add("Zygosity");
        heading.add("Sex");
        heading.add("Phenotyping Center");
        heading.addAll(allParameters);
        heading.add("LacZ Images Wholemount");
        heading.add("LacZ Images Section");

        csvWriter.write(heading);

        // Create map of specimen ID -> [List of observation DTOs] to facilitate generating the report
        final Map<String, List<ObservationDTO>> specimens = data
                .stream()
                .collect(Collectors.groupingBy(ObservationDTO::getExternalSampleId));

        for (String specimen : specimens.keySet()) {

            List<ObservationDTO> expressionData = specimens.get(specimen);
            ObservationDTO specimenData = expressionData.get(0);

            List<String> row = new ArrayList<>();
            row.add(specimenData.getGeneSymbol());
            row.add(specimenData.getGeneAccession());
            row.add(specimenData.getAlleleSymbol());
            row.add(specimenData.getAlleleAccession());
            row.add(specimenData.getStrainName());
            row.add(specimenData.getStrainAccessionId());
            row.add(specimenData.getColonyId());
            row.add(specimenData.getExternalSampleId());
            row.add(specimenData.getZygosity());
            row.add(specimenData.getSex());
            row.add(specimenData.getPhenotypingCenter());

            for (String parameter : allParameters) {
                final List<ObservationDTO> dtos = expressionData
                        .stream()
                        .filter(x -> x.getParameterName().equals(parameter))
                        .collect(Collectors.toList());
                if (dtos.size() > 0) {
                    // Image DTO(s) exist for this parameter
                    final String expression = dtos
                            .stream()
                            .map(ObservationDTO::getCategory)
                            .distinct()
                            .collect(Collectors.joining(", "));
                    if (expression.isEmpty()) {
                        row.add(Constants.NO_DATA);
                    } else {
                        row.add(expression);
                    }
                } else {
                    row.add(Constants.NO_DATA);
                }
            }

            String gene = specimenData.getGeneSymbol();

            String wholemountUrl = Constants.NO_DATA; // default is empty
            if (imageAvailable.containsKey(gene) && imageAvailable.get(gene).contains(ExpressionService.WHOLEMOUNT_PARAMETER_STABLE_ID)) {
                wholemountUrl = String.format("%s/imageComparator?acc=%s&parameter_stable_id=%s",
                        imageCollectionLinkBase,
                        specimenData.getGeneAccession(),
                        ExpressionService.WHOLEMOUNT_PARAMETER_STABLE_ID);
            }
            row.add(wholemountUrl);

            String sectionUrl = Constants.NO_DATA; // default is empty
            if (imageAvailable.containsKey(gene) && imageAvailable.get(gene).contains(ExpressionService.SECTION_PARAMETER_STABLE_ID)) {
                sectionUrl = String.format("%s/imageComparator?acc=%s&parameter_stable_id=%s",
                        imageCollectionLinkBase,
                        specimenData.getGeneAccession(),
                        ExpressionService.SECTION_PARAMETER_STABLE_ID);
            }
            row.add(sectionUrl);

            result.add(row);
        }

        return result;
    }

}