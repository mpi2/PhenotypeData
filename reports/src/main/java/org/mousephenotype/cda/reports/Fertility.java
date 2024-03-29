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
import org.mousephenotype.cda.common.Constants;
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
import java.util.stream.Collectors;

/**
 * Fertility report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class Fertility extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObservationService observationService;

    public static final String MALE_FERTILITY_PARAMETER = "IMPC_FER_001_001";
    public static final String FEMALE_FERTILITY_PARAMETER = "IMPC_FER_019_001";

    public Fertility() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if (!errors.isEmpty()) {
            logger.error("FertilityReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        int count = 0;
        long start = System.currentTimeMillis();

        try {
            QueryResponse response = observationService.getData(resources, Arrays.asList( new String[] { FEMALE_FERTILITY_PARAMETER, MALE_FERTILITY_PARAMETER } ), null);
            List<Map<String, String>> fertilityResourceList = observationService.getCategories(resources, Arrays.asList(new String[] { FEMALE_FERTILITY_PARAMETER, MALE_FERTILITY_PARAMETER }), "sex,category,gene_symbol");

            // Gather collections for summary.
            Set<String> bothFertile;
            Set<String> bothInfertile;
            Set<String> conflictingFemales;
            Set<String> conflictingMales;
            Set<String> femalesFertile = new TreeSet<>();
            Set<String> femalesInfertile = new TreeSet<>();
            Set<String> malesFertile = new TreeSet<>();
            Set<String> malesInfertile = new TreeSet<>();
            for (Map<String, String> fertilityResourceMap : fertilityResourceList) {
                String geneSymbol = "";
                String sex = "";
                String category = "";
                for (Map.Entry<String, String> entry : fertilityResourceMap.entrySet()) {
                    switch (entry.getKey()) {
                        case "gene_symbol": geneSymbol = entry.getValue();                break;
                        case "sex":         sex = entry.getValue().toLowerCase();         break;
                        case "category":    category = entry.getValue().toLowerCase();    break;
                        default: throw new ReportException("Unexpected facet '" + entry.getKey() + "'");
                    }
                }

                if (sex.equals("female") ) {
                    if (category.equals("fertile")) {
                        femalesFertile.add(geneSymbol);
                    } else if (category.equals("infertile")){
                        femalesInfertile.add(geneSymbol);
                    } else {
                        throw new ReportException("Invalid category' " + category + "'");
                    }
                } else if (sex.equals("male")) {
                    if (category.equals("fertile")) {
                        malesFertile.add(geneSymbol);
                    } else if (category.equals("infertile")){
                        malesInfertile.add(geneSymbol);
                    } else {
                        throw new ReportException("Invalid category' " + category + "'");
                    }
                } else {
                    throw new ReportException("Invalid sex' " + sex + "'");
                }
            }
            bothFertile = new TreeSet<>(femalesFertile);
            bothFertile.retainAll(malesFertile);
            bothInfertile = new TreeSet<>(femalesInfertile);
            bothInfertile.retainAll(malesInfertile);
            conflictingFemales = new TreeSet<>(femalesFertile);
            conflictingFemales.retainAll(femalesInfertile);
            conflictingMales = new TreeSet<>(malesFertile);
            conflictingMales.retainAll(malesInfertile);

            // Write summary section.
            csvWriter.write(Arrays.asList("Phenotype", "# Genes*", "Gene Symbols"));
            csvWriter.write(buildList("Both fertile", bothFertile));
            csvWriter.write(buildList("Both infertile", bothInfertile));
            csvWriter.write(buildList("Females fertile", femalesFertile));
            csvWriter.write(buildList("Females infertile", femalesInfertile));
            csvWriter.write(buildList("Males fertile", malesFertile));
            csvWriter.write(buildList("Males infertile", malesInfertile));
            csvWriter.write(Constants.EMPTY_ROW);

             // Write conflicting section.
            csvWriter.write(buildList("Conflicting females", conflictingFemales));
            csvWriter.write(buildList("Conflicting males", conflictingMales));
            csvWriter.write(Arrays.asList(new String[] { "* includes conflicting data. Conflicting data are genes that appear in more than one fertility category." } ));
            csvWriter.write(Constants.EMPTY_ROW);

            // Write detail section.
            String[] detailHeading = {
                "Gene Symbol",
                "Gene Accession Id",
                "Allele Symbol",
                "Allele Accession Id",
                "Background Strain Name",
                "Background Strain Accession Id",
                "Colony Id",
                "Phenotyping Center",
                "Zygosity",
                "Sex",
                "Phenotype",
                "Comment"
            };
            csvWriter.write(Arrays.asList(detailHeading));

            count += 13;    // Sum of rows written above

            List<List<String>> matrix = new ArrayList<>();
            for ( SolrDocument doc : response.getResults()) {
                String category = doc.getFieldValue(ObservationDTO.CATEGORY).toString();
                String geneSymbol = doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString();

                List<String> row = new ArrayList<>();
                row.add(doc.getFieldValue(ObservationDTO.GENE_SYMBOL) != null ? doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString() : "");
                row.add(doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID) != null ? doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID).toString() : "");
                row.add(doc.getFieldValue(ObservationDTO.ALLELE_SYMBOL) != null ? doc.getFieldValue(ObservationDTO.ALLELE_SYMBOL).toString() : "");
                row.add(doc.getFieldValue(ObservationDTO.ALLELE_ACCESSION_ID) != null ? doc.getFieldValue(ObservationDTO.ALLELE_ACCESSION_ID).toString() : "");
                row.add(doc.getFieldValue(ObservationDTO.STRAIN_NAME) != null ? doc.getFieldValue(ObservationDTO.STRAIN_NAME).toString() : "");
                row.add(doc.getFieldValue(ObservationDTO.STRAIN_ACCESSION_ID) != null ? doc.getFieldValue(ObservationDTO.STRAIN_ACCESSION_ID).toString() : "");
                row.add(doc.getFieldValue(ObservationDTO.COLONY_ID) != null ? doc.getFieldValue(ObservationDTO.COLONY_ID).toString() : "");
                row.add(doc.getFieldValue(ObservationDTO.PHENOTYPING_CENTER) != null ? doc.getFieldValue(ObservationDTO.PHENOTYPING_CENTER).toString() : "");
                row.add(doc.getFieldValue(ObservationDTO.ZYGOSITY) != null ? doc.getFieldValue(ObservationDTO.ZYGOSITY).toString() : "");
                row.add(doc.getFieldValue(ObservationDTO.SEX) != null ? doc.getFieldValue(ObservationDTO.SEX).toString() : "");

                row.add(category);

                if ((conflictingFemales.contains(geneSymbol)) || (conflictingMales.contains(geneSymbol))) {
                    row.add("Conflicting Data");
                }

                matrix.add(row);
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
            count += matrix.size();

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format(
            "Finished. %s rows written in %s",
            count, commonUtils.msToHms(System.currentTimeMillis() - start)));
    }

    private List<String> buildList(String label, Set<String> genes) {
        List<String> retVal = new ArrayList<>();
        retVal.add(label);
        retVal.add(Integer.toString(genes.size()));
        retVal.addAll(genes);

        return retVal;
    }
}