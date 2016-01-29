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
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
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

    @Autowired
    ObservationService observationService;

    @Autowired
    GeneService geneService;

    public static final String[] EMPTY_ROW = new String[]{""};

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

        List<List<String[]>> result = new ArrayList<>();
        List<String[]> allTable = new ArrayList<>();
        List<String[]> countsTable = new ArrayList<>();
        List<String[]> genesTable = new ArrayList<>();

        try {
        	
            QueryResponse response = observationService.getViabilityData(resources, null);
            String[] header = {"Gene Symbol", "MGI Gene Id", "Colony Id", "Zygosity", "Category"};
            allTable.add(header);
            for ( SolrDocument doc : response.getResults()){
                String category = doc.getFieldValue(ObservationDTO.CATEGORY).toString();
                String[] row = {(doc.getFieldValue(ObservationDTO.GENE_SYMBOL) != null) ? doc.getFieldValue(ObservationDTO.GENE_SYMBOL).toString() : "",
                		(doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID) != null) ? doc.getFieldValue(ObservationDTO.GENE_ACCESSION_ID).toString() : "",
                        doc.getFieldValue(ObservationDTO.COLONY_ID).toString(), category.split(" - ")[0], category.split(" - ")[1]};
                allTable.add(row);
            }
            
            Map<String, Set<String>> viabilityRes = observationService.getViabilityCategories(resources);
    		
    		for (String cat: viabilityRes.keySet()){
                String[] row = {cat, ""+viabilityRes.get(cat).size()};
                countsTable.add(row);
            }

            
            String[] genesHeader = {"Category", "# genes", "Genes"};
            genesTable.add(genesHeader);
            
            for (String cat : viabilityRes.keySet()){
                String[] row = {cat, "" + viabilityRes.get(cat).size(), StringUtils.join(viabilityRes.get(cat), ", ")};
                genesTable.add(row);
            }

            Set<String> conflicts = new HashSet<>();
            for (String cat : viabilityRes.keySet()){
                for (String otherCat : viabilityRes.keySet()){
                    if (!otherCat.equalsIgnoreCase(cat)){
                        Set<String> conflictingGenes = new HashSet(viabilityRes.get(otherCat));
                        conflictingGenes.retainAll(viabilityRes.get(cat));
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