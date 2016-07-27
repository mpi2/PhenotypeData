/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.dataimport.cdabase.steps;

import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.loads.dataimport.cdabase.support.BiologicalModelAggregator;
import org.mousephenotype.cda.loads.dataimport.cdabase.support.CdabaseLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 09/06/16.
 */
public class BiologicalModelProcessor implements ItemProcessor<BiologicalModelAggregator, BiologicalModelAggregator> {

    private int                                    addedBioModelsCount = 0;
    private Map<String, String>                    alleleSymbolToAccessionIdMap;        // key = allele symbol. Value = allele accession id
    private Map<String, Allele>                    alleles;                             // Alleles mapped by allele accession id
    private Map<String, BiologicalModelAggregator> bioModels = new HashMap<>();         // bioModel mapped by allelic_composition + genetic_background
    private Map<String, GenomicFeature>            genes;                               // Genes mapped by marker accession id
    protected int                                  lineNumber = 0;

    private int multipleAllelesPerRowCount = 0;
    private int multipleGenesPerRowCount   = 0;

    private final Logger     logger      = LoggerFactory.getLogger(this.getClass());
    public final Set<String> errMessages = ConcurrentHashMap.newKeySet();       // This is the java 8 way to create a concurrent hash set.

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private CdabaseLoaderUtils cdabaseLoaderUtils;


    public BiologicalModelProcessor(Map<String, Allele> alleles, Map<String, GenomicFeature> genes) {
        this.alleles = alleles;
        this.genes = genes;
    }

    @Override
    public BiologicalModelAggregator process(BiologicalModelAggregator newBioModel) throws Exception {

        lineNumber++;

        // Skip rows with multiple alleles. Our model doesn't yet handle them. Multiple alleleSymbol values are
        // separated by "|". Multiple mgiMarkerAccessionId values are separated by ",".
        if (newBioModel.getAlleleSymbol().contains("|")) {
            logger.debug("Line {}: Skipping because of multiple alleles: {}", lineNumber, newBioModel.getAllelicComposition());
            multipleAllelesPerRowCount++;
            return null;
        }
        if (newBioModel.getMarkerAccessionIds().toArray(new String[0])[0].contains(",")) {
            logger.debug("Line {}, bioModel {}: Skipping because of multiple marker accession ids: {}", lineNumber, newBioModel.toString(), newBioModel.getMarkerAccessionIds().toArray(new String[0])[0]);
            multipleGenesPerRowCount++;
            return null;
        }

        // Populate the necessary collections.
        if ((genes == null) || (genes.isEmpty())) {
            genes = cdabaseLoaderUtils.getGenes();
        }
        if ((alleles == null) || (alleles.isEmpty())) {
            alleles = cdabaseLoaderUtils.getAlleles();
        }
        if (alleleSymbolToAccessionIdMap == null) {
            alleleSymbolToAccessionIdMap = new HashMap<>();
            for (Allele allele : alleles.values()) {
                alleleSymbolToAccessionIdMap.put(allele.getSymbol(), allele.getId().getAccession());
            }
        }

        // Look up the allele accession id and put it in the newBioModel.
        String alleleAccessionId = alleleSymbolToAccessionIdMap.get(newBioModel.getAlleleSymbol());
        if (alleleAccessionId == null) {
            logger.warn("No allele accession id found for allele symbol '" + newBioModel.getAlleleSymbol() + "'. Skipping...");
            return null;
        }
        newBioModel.getAlleleAccessionIds().add(alleleAccessionId);

        // Try to fetch the bioModel from the hash. If it doesn't exist, create it, add it, and return it.
        String key = newBioModel.getAllelicComposition() + "_" + newBioModel.getGeneticBackground();
        BiologicalModelAggregator existingBioModel = bioModels.get(key);
        if (existingBioModel == null) {
            addedBioModelsCount++;
            BiologicalModelAggregator bioModel = new BiologicalModelAggregator(newBioModel);
            bioModels.put(key, bioModel);
            return bioModel;
        }

        existingBioModel.getAlleleAccessionIds().add(alleleAccessionId);
        existingBioModel.getMarkerAccessionIds().add(newBioModel.getMarkerAccessionIds().toArray(new String[0])[0]);
        existingBioModel.getMpAccessionIds().add(newBioModel.getMpAccessionIds().toArray(new String[0])[0]);

        return existingBioModel;
    }

    public Map<String, Allele> getAlleles() {
        return alleles;
    }

    public Set<String> getErrMessages() {
        return errMessages;
    }

    public Map<String, GenomicFeature> getGenes() {
        return genes;
    }

    public int getAddedBioModelsCount() {
        return addedBioModelsCount;
    }

    public int getMultipleAllelesPerRowCount() {
        return multipleAllelesPerRowCount;
    }

    public int getMultipleGenesPerRowCount() {
        return multipleGenesPerRowCount;
    }

    public Map<String, BiologicalModelAggregator> getBioModels() {
        return bioModels;
    }
}