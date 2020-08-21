/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
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
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.PhenotypeCenterAllService;
import org.mousephenotype.cda.solr.service.PhenotypeCenterAllServiceBean;
import org.mousephenotype.cda.solr.service.ProcedureCompletenessAllService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.beans.Introspector;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Procedure Completeness report.
 * <p>
 * Created by mrelac on 28/02/2019.
 */

@Component
public class ProcedureCompletenessAll extends AbstractReport {

    protected Logger                          logger    = LoggerFactory.getLogger(this.getClass());
    private   ProcedureCompletenessAllService procedureCompletenessAllService;
    private   PhenotypeCenterAllService       phenotypeCenterAllService;
    private   Set<String>                     nullParts = new HashSet<>();

    @Inject
    public ProcedureCompletenessAll(
        ProcedureCompletenessAllService procedureCompletenessAllService,
        PhenotypeCenterAllService phenotypeCenterAllService
    ) {
        super();
        this.procedureCompletenessAllService = procedureCompletenessAllService;
        this.phenotypeCenterAllService = phenotypeCenterAllService;
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ProcedureCompletenessReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        int count = 0;
        long start = System.currentTimeMillis();

        // Report rows are unique amongst the following fields:
        String[] heading = {
            "Gene Symbol",
            "Gene Accession Id",
            "Allele Symbol",
            "Allele Accession Id",
            "Background Strain Name",
            "Background Strain Accession Id",
            "Colony Id",
            "Phenotyping Center",
            "Zygosity",
            "Life Stage",

            // All remaining fields are either collapsed, comma-separated collections or a single count.
            "Procedure Ids with Parameter Status Success",
            "Procedure Names with Parameter Status Success",
            "Parameter Ids with Status Success",
            "Parameter Names with Status Success",
            "Top Level MP Ids with Parameter Status Success",
            "Top Level MP Names with Parameter Status Success",
            "MP Ids with Parameter Status Success",
            "MP Names with Parameter Status Success",
            "Procedure Ids with Parameter Status Success Count",
            "Parameter Ids with Status Success Count",
            "Top Level MP Ids with Status Success Count",
            "MP Ids with Status Success Count",

            "Procedure Ids with Parameter Status Fail",
            "Procedure Ids with Parameter Status Fail Count",
            "Parameter Ids with Status Fail Count",

            "Procedure Ids with Parameter Status Other",
            "Procedure Ids with Parameter Status Other Count",
            "Parameter Ids with Status Other Count"};

        csvWriter.write(heading);
        count++;

        // Sort by center, then colonyId
        List<String> centers = getCenters()
            .stream()
            .sorted()
            .collect(Collectors.toList());

        for (String center : centers) {
            final Set<ProcedureCompletenessAllService.ProcedureCompletenessDTO> dtos = getCenterData(center)
                .stream()
                .sorted(Comparator.comparing(ProcedureCompletenessAllService.ProcedureCompletenessDTO::getColonyId))
                .collect(Collectors.toSet());
            final Set<PhenotypeCenterAllServiceBean> dataByCenter = procedureCompletenessAllService.getCenterData(center);
            int                                      centerRowCount = 0;

            for (ProcedureCompletenessAllService.ProcedureCompletenessDTO dto : dtos) {
                // Capture and log beans with required but null components.
                Set<String> filtered = dataByCenter
                    .stream()
                    .filter(b -> (b.getColonyId() == null) || (b.getZygosity() == null) || (b.getLifeStageName() == null))
                    .map(b -> b.getColonyId() + "::" + b.getAlleleSymbol() + "::" + b.getParameterStableId())
                    .collect(Collectors.toSet());

                if ( ! filtered.isEmpty()) {
                    nullParts.addAll(filtered);
                    continue;
                }

                csvWriter.write(procedureCompletenessAllService.buildOutputRow(center, dataByCenter, dto));
                count++;
                if (count % 1000 == 0)
                    csvWriter.flushQuietly();
            }
        }

        csvWriter.closeQuietly();

        if ( ! procedureCompletenessAllService.missingMpTermNames.isEmpty()) {
            logger.error("{} missing required mp term names", procedureCompletenessAllService.missingMpTermNames.size());
            System.out.println("phenotypingCenter::mpAccessionId");
            procedureCompletenessAllService.missingMpTermNames
                .stream()
                .forEach(x -> System.out.println(x));
        }

        if ( ! nullParts.isEmpty()) {
            logger.error("{} missing required fields from ProcedureCompletenessAllService:", nullParts.size());
            System.out.println("colonyId::geneSymbol::alleleSymbol::parameterStableId");
            nullParts
                .stream()
                .forEach(x -> System.out.println(x));
        }

        log.info(String.format(
            "Finished. %s rows written in %s",
            count, commonUtils.msToHms(System.currentTimeMillis() - start)));
    }

    private Set<ProcedureCompletenessAllService.ProcedureCompletenessDTO> getCenterData(String center) throws ReportException {
        try {
            return procedureCompletenessAllService.getProcedureCompletenessDTOs(center);
        } catch (Exception e) {
            throw new ReportException(e);
        }
    }

    private List<String> getCenters() throws ReportException {
        try {
            return phenotypeCenterAllService.getPhenotypeCenters();
        } catch (Exception e) {
            throw new ReportException(e);
        }
    }
}