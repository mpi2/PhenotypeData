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
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Phenotype Overview Per Gene report.
 *
 * Created by mrelac on 24/07/2015.
 */
@SpringBootApplication
@Component
public class PhenotypeOverviewPerGeneReport extends AbstractReport {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("postqcService")
    PostQcService genotypePhenotypeService;

    @Autowired
    ObservationService observationService;

    public PhenotypeOverviewPerGeneReport() {
        super();
    }

    public static void main(String args[]) {
        SpringApplication.run(PhenotypeOverviewPerGeneReport.class, args);
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass().getSuperclass()));
    }

    @Override
    public void run(String[] args) throws ReportException {
        initialise(args);

        long start = System.currentTimeMillis();

        List<String[]> result = new ArrayList<>();
        String[] headerParams  ={"Marker symbol", "# phenotype hits", "phenotype hits"};
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
                    geneToPhenotypes.put(gp.getMarkerSymbol(), new HashSet<String>());
                }

                geneToPhenotypes.get(gp.getMarkerSymbol()).add(gp.getMpTermName());
            }

            Set<String> allGenes = new HashSet<>(observationService.getGenesWithMoreProcedures(1, resources));
            allGenes.removeAll(geneToPhenotypes.keySet());

            for (String geneSymbol : geneToPhenotypes.keySet()) {
                String[] row = {geneSymbol, Integer.toString(geneToPhenotypes.get(geneSymbol).size()), StringUtils.join(geneToPhenotypes.get(geneSymbol), ": ")};
                result.add(row);
            }

            for (String geneSymbol : allGenes) {
                String[] row = {geneSymbol, "0", ""};
                result.add(row);
            }
            csvWriter.writeAll(result);

        } catch (SolrServerException | InterruptedException | ExecutionException e) {
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