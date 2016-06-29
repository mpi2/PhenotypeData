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

package org.mousephenotype.cda.loads.cdaloader.steps;

import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.loads.cdaloader.support.BiologicalModelAggregator;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
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
    private Map<String, String> alleleSymbolToAccessionIdMap = new HashMap<>(); // key = allele symbol. Value = allele accession id
    private Map<String, Allele>                    alleles;                     // Alleles mapped by allele accession id
    private Map<String, BiologicalModelAggregator> bioModels = new HashMap<>(); // bioModel mapped by allele symbol
    private Map<String, GenomicFeature>            genomicFeatures;             // Genes mapped by marker accession id
    protected int                                  lineNumber = 0;

    private int multipleAllelesPerRowCount = 0;
    private int multipleGenesPerRowCount   = 0;

    private final Logger     logger      = LoggerFactory.getLogger(this.getClass());
    public final Set<String> errMessages = ConcurrentHashMap.newKeySet();       // This is the java 8 way to create a concurrent hash set.

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    public BiologicalModelProcessor(Map<String, Allele> alleles, Map<String, GenomicFeature> genomicFeatures) {
        this.alleles = alleles;
        this.genomicFeatures = genomicFeatures;
    }

    @Override
    public BiologicalModelAggregator process(BiologicalModelAggregator bioModel) throws Exception {

        lineNumber++;

        // Skip rows with multiple alleles. Our model doesn't yet handle them. Multiple alleleSymbol values are
        // separated by "|". Multiple mgiMarkerAccessionId values are separated by ",".
        if (bioModel.getAlleleSymbol().contains("|")) {
            logger.debug("Line {}: Skipping because of multiple alleles: {}", lineNumber, bioModel.getAllelicComposition());
            multipleAllelesPerRowCount++;
            return null;
        }
        if (bioModel.getMpAccessionId().contains(",")) {
            logger.debug("Line {}: Skipping because of multiple genes: {}", lineNumber, bioModel.getMpAccessionId());
            multipleGenesPerRowCount++;
            return null;
        }

        if (alleleSymbolToAccessionIdMap.isEmpty()) {
            for (Allele allele : alleles.values()) {
                alleleSymbolToAccessionIdMap.put(allele.getSymbol(), allele.getId().getAccession());
            }
        }

        String alleleAccessionId = alleleSymbolToAccessionIdMap.get(bioModel.getAlleleSymbol());
        if (alleleAccessionId == null) {
            logger.warn("No allele accession id found for allele symbol '" + bioModel.getAlleleSymbol() + "'. Skipping...");
            return null;
        }

        bioModel.setAlleleAccessionId(alleleAccessionId);

        addedBioModelsCount++;

        return bioModel;
    }

    public Map<String, Allele> getAlleles() {
        return alleles;
    }

    public Set<String> getErrMessages() {
        return errMessages;
    }

    public Map<String, GenomicFeature> getGenomicFeatures() {
        return genomicFeatures;
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