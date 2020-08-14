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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
            logger.error("LaczExpressionReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);


        PropertySource ps = new SimpleCommandLinePropertySource(args);
        if (ps.containsProperty(IMAGE_COLLECTION_LINK_BASE_KEY)) {
            imageCollectionLinkBase = ps.getProperty(IMAGE_COLLECTION_LINK_BASE_KEY).toString();

            throw new ReportException("Required reports_hostname parameter is missing. Format is like http://www.mousephenotype.org.");
        }

        long start = System.currentTimeMillis();

//        List<List<String>> results = imageService.getLaczExpressionSpreadsheet(imageCollectionLinkBase);

        final List<ObservationDTO> data = expressionService.getCategoricalAdultLacZDataForReport();
        final Map<String, Set<String>> imageAvailable = imageService.getLaczImagesAvailable();

        final List<List<String>> results = getLaczExpressionSpreadsheet(data, imageAvailable);
        csvWriter.writeRows(results);

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
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

        List<String> header = new ArrayList<>();
        header.add("Gene Symbol");
        header.add("MGI Gene Id");
        header.add("Allele Symbol");
        header.add("Colony Id");
        header.add("Biological Sample Id");
        header.add("Zygosity");
        header.add("Sex");
        header.add("Phenotyping Centre");
        header.addAll(allParameters);
        header.add("LacZ Images Wholemount");
        header.add("LacZ Images Section");

        result.add(header);

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
                        row.add("");
                    } else {
                        row.add(expression);
                    }
                } else {
                    row.add("");
                }
            }

            String gene = specimenData.getGeneSymbol();

            String wholemountUrl = ""; // default is empty
            if (imageAvailable.containsKey(gene) && imageAvailable.get(gene).contains(ExpressionService.WHOLEMOUNT_PARAMETER_STABLE_ID)) {
                wholemountUrl = String.format("%s/imageComparator?acc=%s&parameter_stable_id=%s",
                        imageCollectionLinkBase,
                        specimenData.getGeneAccession(),
                        ExpressionService.WHOLEMOUNT_PARAMETER_STABLE_ID);
            }
            row.add(wholemountUrl);

            String sectionUrl = ""; // default is empty
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