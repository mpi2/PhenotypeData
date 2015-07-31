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

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObservationService observationService;

    public static final String[] EMPTY_ROW = new String[]{""};

    public ViabilityReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {
        initialise(args);

        long start = System.currentTimeMillis();

        List<List<String[]>> result = new ArrayList<>();
        List<String[]> allTable = new ArrayList<>();
        List<String[]> countsTable = new ArrayList<>();
        List<String[]> genesTable = new ArrayList<>();
        HashMap<String, Integer> countsByCategory = new HashMap<>();
        HashMap<String, HashSet<String>> genesByVia = new HashMap<>();

        try {
            QueryResponse response = observationService.getViabilityData(resources);
            String[] header = {"Gene", "Colony", "Category"};
            allTable.add(header);
            for ( SolrDocument doc : response.getResults()){
                String category = doc.getFieldValue(ObservationDTO.CATEGORY).toString();
                HashSet genes = new HashSet<>();
                String[] row = {(doc.getFieldValue(ObservationDTO.GENE_SYMBOL) != null) ? doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString() : "",
                        doc.getFieldValue(ObservationDTO.COLONY_ID).toString(), category};
                allTable.add(row);
                if (countsByCategory.containsKey(category)){
                    countsByCategory.put(category, countsByCategory.get(category) + 1);
                }else {
                    countsByCategory.put(category, 1);
                }
                if (genesByVia.containsKey(category)){
                    genes = genesByVia.get(category);
                }

                if (doc.getFieldValue(ObservationDTO.GENE_SYMBOL) != null) {
                    genes.add(doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString());
                } else {
                    System.out.println("  ERROR: Could not get solr document field gene_symbol for document: " + doc);
                }
                genesByVia.put(category, genes);
            }

            for (String cat: countsByCategory.keySet()){
                String[] row = {cat, countsByCategory.get(cat).toString()};
                countsTable.add(row);
            }

            String[] genesHeader = {"Category", "# genes", "Genes"};
            genesTable.add(genesHeader);
            for (String cat : genesByVia.keySet()){
                String[] row = {cat, "" + genesByVia.get(cat).size(), StringUtils.join(genesByVia.get(cat), ", ")};
                genesTable.add(row);
            }

            HashSet<String> conflicts = new HashSet<>();
            for (String cat : genesByVia.keySet()){
                for (String otherCat : genesByVia.keySet()){
                    if (!otherCat.equalsIgnoreCase(cat)){
                        Set<String> conflictingGenes = genesByVia.get(otherCat);
                        conflictingGenes.retainAll(genesByVia.get(cat));
                        conflicts.addAll(conflictingGenes);
                    }
                }
            }

            genesTable.add(EMPTY_ROW);
            String[] row = {"Conflicting", "" + conflicts.size(), StringUtils.join(conflicts, ", ")};
            genesTable.add(row);
            String[] note = {"NOTE: Symbols in the conflicting list represent genes that are included in more than one viability category."};
            genesTable.add(note);

            result.add(countsTable);
            result.add(genesTable);
            result.add(allTable);

            csvWriter.writeAllMulti(result);

        } catch (SolrServerException e) {
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