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
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.Introspector;
import java.io.IOException;
import java.util.*;

/**
 * Fertility report.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class FertilityReport extends AbstractReport {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObservationService observationService;

    public static final String MALE_FERTILITY_PARAMETER = "IMPC_FER_001_001";
    public static final String FEMALE_FERTILITY_PARAMETER = "IMPC_FER_019_001";

    public FertilityReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("FertilityReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<String[]> result = new ArrayList<>();

        try {
            List<ObservationDTO> results;
            Map<String, Set<String>> maleColonies = new HashMap<>();
            Map<String, Set<String>> femaleColonies = new HashMap<>();
            Map<String, Set<String>> bothColonies = new HashMap<>();

            Map<String, Set<String>> maleGenes = new HashMap<>();
            Map<String, Set<String>> femaleGenes = new HashMap<>();
            Map<String, Set<String>> bothGenes = new HashMap<>();

            maleColonies.put("Fertile", new HashSet<>());
            maleColonies.put("Infertile", new HashSet<>());
            femaleColonies.put("Fertile", new HashSet<>());
            femaleColonies.put("Infertile", new HashSet<>());
            bothColonies.put("Fertile", new HashSet<>());
            bothColonies.put("Infertile", new HashSet<>());

            maleGenes.put("Fertile", new HashSet<>());
            maleGenes.put("Infertile", new HashSet<>());
            femaleGenes.put("Fertile", new HashSet<>());
            femaleGenes.put("Infertile", new HashSet<>());
            bothGenes.put("Fertile", new HashSet<>());
            bothGenes.put("Infertile", new HashSet<>());


            List<ObservationDTO> observationResults = observationService.getObservationsByParameterStableId(MALE_FERTILITY_PARAMETER);
            for (ObservationDTO observationResult : observationResults) {
                if (resources.contains(observationResult.getDataSourceName())) {
                    String key = observationResult.getCategory();
                    maleColonies.get(key).add(observationResult.getColonyId());
                    maleGenes.get(key).add(observationResult.getGeneSymbol() + " (" + observationResult.getGeneAccession() + ")");
                }
            }

            observationResults = observationService.getObservationsByParameterStableId(FEMALE_FERTILITY_PARAMETER);
            for (ObservationDTO observationResult : observationResults) {
                if (resources.contains(observationResult.getDataSourceName())) {
                    String key = observationResult.getCategory();
                    femaleColonies.get(key).add(observationResult.getColonyId());
                    femaleGenes.get(key).add(observationResult.getGeneSymbol() + " (" + observationResult.getGeneAccession() + ")");
                }
            }

            bothColonies.put("Fertile", new HashSet<>(femaleColonies.get("Fertile")));
            bothColonies.put("Infertile", new HashSet<>(femaleColonies.get("Infertile")));
            bothGenes.put("Infertile", new HashSet<>(femaleGenes.get("Infertile")));
            bothGenes.put("Infertile", new HashSet<>(femaleGenes.get("Infertile")));

            bothColonies.get("Fertile").retainAll(maleColonies.get("Fertile"));
            bothColonies.get("Infertile").retainAll(maleColonies.get("Infertile"));
            bothGenes.get("Fertile").retainAll(maleGenes.get("Fertile"));
            bothGenes.get("Infertile").retainAll(maleGenes.get("Infertile"));

            result.add(Arrays.asList("Sex", "IMPC/3i Line count", "IMPC/3i Gene count", "IMPC/3i Gene Symbols", "IMPC/3i Gene Accession Ids").toArray(new String[4]));
            result.add(Arrays.asList("Both infertile", Integer.toString(bothColonies.get("Infertile").size()), Integer.toString(bothGenes.get("Infertile").size()), StringUtils.join(bothGenes.get("Infertile"), ";")).toArray(new String[4]));
            result.add(Arrays.asList("Both fertile", Integer.toString(bothColonies.get("Fertile").size()), Integer.toString(bothGenes.get("Fertile").size()), "").toArray(new String[4]));
            result.add(Arrays.asList("Males infertile", Integer.toString(maleColonies.get("Infertile").size()), Integer.toString(maleGenes.get("Infertile").size()), StringUtils.join(maleGenes.get("Infertile"), ";")).toArray(new String[4]));
            result.add(Arrays.asList("Males fertile", Integer.toString(maleColonies.get("Fertile").size()), Integer.toString(bothGenes.get("Fertile").size()), "").toArray(new String[4]));
            result.add(Arrays.asList("Females infertile", Integer.toString(femaleColonies.get("Infertile").size()), Integer.toString(femaleGenes.get("Infertile").size()), StringUtils.join(femaleGenes.get("Infertile"), ";")).toArray(new String[4]));
            result.add(Arrays.asList("Females fertile", Integer.toString(femaleColonies.get("Fertile").size()), Integer.toString(bothGenes.get("Fertile").size()), "").toArray(new String[4]));

            csvWriter.writeAll(result);

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