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
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.*;

/**
 * Viability report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class ViabilityReport extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String VIABILITY_PARAMETER_STABLE_ID = "IMPC_VIA_001_001";

    @Autowired
    ObservationService observationService;

    @Autowired
    GeneService geneService;

    public ViabilityReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {
        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ViabilityReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        try {
            QueryResponse response = observationService.getData(resources, Arrays.asList( new String[] { VIABILITY_PARAMETER_STABLE_ID } ), null);
            List<Map<String, String>> viabilityResourceList = observationService.getCategories(resources, Arrays.asList(new String[] { VIABILITY_PARAMETER_STABLE_ID }), "category,gene_symbol");

            // Gather collections for summary.
            Map<String, List<String>> categoriesMap = new TreeMap<>();
            Set<String> conflictingViability = new TreeSet<>();
            for (Map<String, String> viabilityResourceMap : viabilityResourceList) {
                String category = "";
                String geneSymbol = "";
                for (Map.Entry<String, String> entry : viabilityResourceMap.entrySet()) {
                    switch (entry.getKey()) {
                        case "gene_symbol":
                            geneSymbol = entry.getValue();
                            break;
                        case "category":
                            category = entry.getValue().toLowerCase();
                            break;
                        default:
                            throw new ReportException("Unexpected facet '" + entry.getKey() + "'");
                    }
                }

                if ( ! categoriesMap.containsKey(category)) {
                    categoriesMap.put(category, new ArrayList<>());
                }

                categoriesMap.get(category).add(geneSymbol);
            }
            // Conflicts summary
            for (String category : categoriesMap.keySet()){
                for (String categoryCopy : categoriesMap.keySet()){
                    if ( ! categoryCopy.equalsIgnoreCase(category)){
                        Set<String> conflictingGenes = new HashSet(categoriesMap.get(categoryCopy));
                        conflictingGenes.retainAll(categoriesMap.get(category));
                        conflictingViability.addAll(conflictingGenes);
                    }
                }
            }

            // Write summary section.
            csvWriter.writeRow(Arrays.asList(new String[] {  "Phenotype", "# Genes", "Gene Symbols"  }));
            for (String category : categoriesMap.keySet()) {
                csvWriter.writeRow(Arrays.asList(new String[] { category, "" + categoriesMap.get(category).size(), StringUtils.join(categoriesMap.get(category), ", ") }));
            }

            csvWriter.writeNext(EMPTY_ROW);

            // Write conflicting section.
            csvWriter.writeRow(Arrays.asList(new String[] { "Conflicting", "" + conflictingViability.size(), StringUtils.join(conflictingViability, ", ") } ));
            csvWriter.writeRow(Arrays.asList(new String[] { "NOTE: Symbols in the conflicting list represent genes that are included in more than one viability category." } ));

            csvWriter.writeNext(EMPTY_ROW);

            // Write detail section.
            csvWriter.writeRow(Arrays.asList(new String[] { "Gene Symbol", "MGI Gene Id", "Colony Id", "Sex", "Zygosity", "Phenotype", "Comment" } ));//            allTable.add(header);
            for ( SolrDocument doc : response.getResults()) {
                String category = doc.getFieldValue(ObservationDTO.CATEGORY).toString();
                String geneSymbol = doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString();
                List<String> row = new ArrayList<>();
                row.add(doc.getFieldValue(ObservationDTO.GENE_SYMBOL) != null ? geneSymbol : "");
                row.add(doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID) != null ? doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID).toString() : "");
                row.add(doc.getFieldValue(ObservationDTO.COLONY_ID).toString());
                row.add(doc.getFieldValue(ObservationDTO.SEX).toString());
                row.add(doc.getFieldValue(ObservationDTO.ZYGOSITY).toString());
                row.add(category.split(" - ")[1]);                          // Phenotype (category)
                if (conflictingViability.contains(geneSymbol)) {
                    row.add("Conflicting Data");
                }

                csvWriter.writeRow(row);
            }

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}