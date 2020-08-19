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
import org.mousephenotype.cda.common.Constants;
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
public class Viability extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObservationService observationService;

    @Autowired
    GeneService geneService;

    public Viability() {
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

        int count = 0;
        long start = System.currentTimeMillis();

        try {

            ObservationService.ViabilityData data = observationService.getViabilityData(resources, true);

            //System.out.println("keys: "+ data.getViabilityCategories().keySet());
            TreeMap<String, Set<String>> categoryData = new TreeMap<>(data.getViabilityCategories());                   // Using a TreeMap causes the categories to be sorted for better presentation.

            // Build summary section.
            List<List<String>> summaryGrid = new ArrayList<>();

            for (Map.Entry<String, Set<String>> category : categoryData.entrySet()) {
                List<String> row = new ArrayList<>();
                row.add(category.getKey());
                Set<String> symbols = category.getValue();
                List<String> symbolsList = new ArrayList<>(symbols);
                row.add(Integer.toString(symbolsList.size()));
                Collections.sort(symbolsList);
                for (String symbol : symbolsList) {
                    row.add(symbol);
                }
                summaryGrid.add(row);
            }

            // Build conflicting data section.
            Set<String> conflictingSet = new TreeSet<>();
            Map<String, List<ObservationDTO>> genesMap = new HashMap<>();
            List<ObservationDTO> details = data.getData();
            for (ObservationDTO detail : details) {
                List<ObservationDTO> oList = genesMap.get(detail.getGeneSymbol());
                if (oList == null) {
                    oList = new ArrayList<>();
                    genesMap.put(detail.getGeneSymbol(), oList);
                }

                oList.add(detail);
            }
            for (List<ObservationDTO> genes : genesMap.values()) {
                if (genes.size() > 1) {
                    ObservationDTO expectedGene = genes.get(0);
                    for (int i = 1; i < genes.size(); i++) {
                        ObservationDTO actualGene = genes.get(i);
                        if (actualGene.getZygosity().equals(expectedGene.getZygosity()) &&
                           (actualGene.getCategory().equals(expectedGene.getCategory()))) {
                            // Do nothing
                        } else {
                            conflictingSet.add(expectedGene.getGeneSymbol());
                            break;
                        }
                    }
                }
            }

            List<String> conflictingList = new ArrayList<>(conflictingSet);
            Collections.sort(conflictingList);
            List<String> conflictingRow = new ArrayList<>();
            conflictingRow.add("Conflicting data");
            conflictingRow.add(Integer.toString(conflictingSet.size()));
            conflictingRow.addAll(conflictingSet);

            // Write summary section.
            csvWriter.write(Arrays.asList(new String[] {  "Phenotype", "# Genes*", "Gene Symbols"  }));
            csvWriter.writeRows(summaryGrid);

            csvWriter.write(Constants.EMPTY_ROW);

            // Write conflicting section.
            csvWriter.write(Arrays.asList(new String[] { "* includes conflicting data. Conflicting data are genes that appear in more than one viability category." }));
            csvWriter.write(conflictingRow);

            csvWriter.write(Constants.EMPTY_ROW);
            csvWriter.write(Constants.EMPTY_ROW);
            csvWriter.write(Constants.EMPTY_ROW);

            // Build and write detail section.
            csvWriter.write(Arrays.asList(new String[] { "List of genes that result in a lethal, subviable or viable phenotype. A gene may appear more than once if there are results for different colonies or for different alleles." }));
            csvWriter.write(Arrays.asList(new String[] { "Gene Symbol", "Gene Accession Id", "Allele Symbol", "Allele Accession Id", "Phenotyping Centre", "Colony Id", "Sex", "Zygosity", "Phenotype", "Comment" } ));
            details = data.getData();
            details.sort(Comparator
                    .comparing(ObservationDTO::getGeneSymbol)
                    .thenComparing(ObservationDTO::getAlleleSymbol)
                    .thenComparing(ObservationDTO::getZygosity)
                    .thenComparing(ObservationDTO::getCategory));

            for (ObservationDTO detail : details) {
                List<String> row = new ArrayList<>();
                row.add(detail.getGeneSymbol());
                row.add(detail.getGeneAccession());
                row.add(detail.getAlleleSymbol());
                row.add(detail.getAlleleAccession());
                row.add(detail.getPhenotypingCenter());
                row.add(detail.getColonyId());
                row.add(detail.getSex());
                row.add(detail.getZygosity());
                row.add(detail.getCategory());
                String comment = "";
                if ((detail.getGeneSymbol() != null) && ( ! detail.getGeneSymbol().trim().isEmpty())) {
                    if (conflictingSet.contains(detail.getGeneSymbol().trim())) {
                        comment = "Conflicting data";
                    }
                }
                row.add(comment);

                csvWriter.write(row);
                count++;
            }

        } catch (Exception e) {

            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format(
            "Finished. %s detail rows written in %s",
            count, commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}