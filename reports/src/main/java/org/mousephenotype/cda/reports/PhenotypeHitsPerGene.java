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
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.*;

/**
 * Phenotype Overview Per Gene report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class PhenotypeHitsPerGene extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private List<GenotypePhenotypeDTO> missingGPTerms = new ArrayList<>();

    @Autowired
    @Qualifier("genotype-phenotype-service")
    GenotypePhenotypeService genotypePhenotypeService;

    @Autowired
    ObservationService observationService;

    @Autowired
    GeneService geneService;

    public PhenotypeHitsPerGene() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        boolean hasErrors = false;
        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("PhenotypeOverviewPerGeneReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<String[]> result = new ArrayList<>();
        // According to Terry, there is a 1 to 1 relationship between Gene Symbol and MGI Gene Id, so we can just use a map, keyed by gene name, to accumulate the MGI Gene Ids.
        Map<String, String> geneSymbolToId = new HashMap<>();
        String[] headerParams  ={"Gene Symbol", "MGI Gene Id", "# Phenotype Hits", "Phenotype Hits"};
        result.add(headerParams);

        try {

            List<GenotypePhenotypeDTO> gps = genotypePhenotypeService.getAllGenotypePhenotypes(resources);

            Map<String, Set<String>> geneToPhenotypes = new HashMap<>();

            for (GenotypePhenotypeDTO gp : gps) {

                // Exclude LacZ calls
                if(gp.getParameterStableId().contains("ALZ")) {
                    continue;
                }

                if( ! geneToPhenotypes.containsKey(gp.getMarkerSymbol())) {
                    geneToPhenotypes.put(gp.getMarkerSymbol(), new HashSet<>());
                }

                // Term can be EMAP, MPATH, MP, etc.
                if ((gp.getAnatomyTermName() != null) && ( ! gp.getAnatomyTermName().isEmpty())) {
                    geneToPhenotypes.get(gp.getMarkerSymbol()).add(gp.getAnatomyTermName() + "/" + gp.getParameterName());
                } else if ((gp.getMpathTermName() != null)) {
                    if ( ! gp.getMpathTermName().isEmpty()) {
                        geneToPhenotypes.get(gp.getMarkerSymbol()).add(gp.getMpathTermName() + "/" + gp.getParameterName());
                    } else {
                        missingGPTerms.add(gp);
                        continue;
                    }
                } else if ((gp.getMpTermName() != null) && ( ! gp.getMpTermName().isEmpty())) {
                    geneToPhenotypes.get(gp.getMarkerSymbol()).add(gp.getMpTermName());
                } else {
                    hasErrors = true;
                    logger.error("GenotypePhenotypeDTO term is null or empty for g-p core doc_id {}. Not added.", gp.getId());
                    continue;
                }

                geneSymbolToId.put(gp.getMarkerSymbol(), gp.getMarkerAccessionId());
            }

            Set<String> allGenes = new HashSet<>(observationService.getGenesWithMoreProcedures(1, resources));
            allGenes.removeAll(geneToPhenotypes.keySet());

            for (String geneSymbol : geneToPhenotypes.keySet()) {
                String mgiGeneId = (geneSymbolToId.containsKey(geneSymbol) ? geneSymbolToId.get(geneSymbol) : "");
                String[] row = {geneSymbol, mgiGeneId, Integer.toString(geneToPhenotypes.get(geneSymbol).size()), StringUtils.join(geneToPhenotypes.get(geneSymbol), "::")};
                result.add(row);
            }

            String mgiGeneId = "";
            for (String geneSymbol : allGenes) {
                if (geneSymbolToId.containsKey(geneSymbol)) {
                    mgiGeneId = geneSymbolToId.get(geneSymbol);
                } else {
                    GeneDTO geneDTO = geneService.getGeneByGeneSymbolWithLimitedFields(geneSymbol);
                    if (geneDTO != null) {
                        mgiGeneId = geneDTO.getMgiAccessionId();
                    }
                }

                String[] row = {geneSymbol, mgiGeneId, "0", ""};
                result.add(row);
            }

        } catch (SolrServerException | IOException e) {
            throw new ReportException("Exception creating " + this.getClass().getCanonicalName() + ". Reason: " + e.getLocalizedMessage());
        }

        if ( ! missingGPTerms.isEmpty()) {
            result.add( new String[] { " "});
            result.add( new String[] {"Gene Symbol", "MGI Gene Id", "Missing/obsolete MPATH term"});

            missingGPTerms
                    .stream()
                    .forEach(gp -> {
                        result.add(new String[] { gp.getMarkerSymbol(), gp.getMarkerAccessionId(), gp.getMpathTermId(), });
                    });
        }

        csvWriter.writeRowsOfArray(result);
        csvWriter.closeQuietly();

        log.info(String.format(
            "Finished. %s rows written in %s",
            result.size(), commonUtils.msToHms(System.currentTimeMillis() - start)));

        if (hasErrors) {
            throw new ReportException();
        }
    }
}