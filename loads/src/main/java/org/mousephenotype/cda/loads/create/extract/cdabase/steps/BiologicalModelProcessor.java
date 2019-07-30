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

package org.mousephenotype.cda.loads.create.extract.cdabase.steps;

import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.common.AccDbId;
import org.mousephenotype.cda.loads.common.BioModelInsertDTOMGI;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.ConcurrentHashMapAllowNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by mrelac on 09/06/16.
 */
public class BiologicalModelProcessor implements ItemProcessor<BioModelInsertDTOMGI, BioModelInsertDTOMGI> {

    private int                 addedBioModelsCount = 0;
    private Map<String, Allele> alleleSymbolToAllele;                       // key = allele symbol. Value = allele instance
    private CdaSqlUtils         cdabaseSqlUtils;
    protected int               lineNumber = 0;

    private ConcurrentHashMapAllowNull<BioModelInsertDTOMGI, BioModelInsertDTOMGI> bioModels = new ConcurrentHashMapAllowNull<>();         // Accumulation of inserted bioModels.

    private int multipleAllelesPerRowCount         = 0;
    private int multipleGenesPerRowCount           = 0;
    private int multipleAllelesAndGenesPerRowCount = 0;

    private final Logger     logger      = LoggerFactory.getLogger(this.getClass());
    public final Set<String> errMessages = ConcurrentHashMapAllowNull.newKeySet();       // This is the java 8 way to create a concurrent hash set.

    public BiologicalModelProcessor(CdaSqlUtils cdabaseSqlutils) {
        this.cdabaseSqlUtils = cdabaseSqlutils;
    }

    @Override
    public BioModelInsertDTOMGI process(BioModelInsertDTOMGI bioModelIn) throws Exception {

        lineNumber++;

        Set<AccDbId> alleles    = new HashSet<>();
        Set<AccDbId> genes      = new HashSet<>();
        Set<AccDbId> phenotypes = new HashSet<>();

        if (alleleSymbolToAllele == null) {
            alleleSymbolToAllele = cdabaseSqlUtils.getAllelesBySymbol();
        }

        // Extract Allele(s). Translate allele symbol(s) into accession IDs.
        // Exactly 1 allele is required.
        if ( ! bioModelIn.getAlleles().isEmpty()) {
            // The BiologicalModelLoader puts the alleleSymbol into the acc field. Skip input rows with multiple alleles (separated by '|').
            String alleleSymbol = bioModelIn.getAlleles().iterator().next().getAcc();
            if (alleleSymbol.contains("|")) {
                multipleAllelesPerRowCount++;

                if (( ! bioModelIn.getGenes().isEmpty()) && (bioModelIn.getGenes().iterator().next().getAcc().contains(","))) {
                    multipleAllelesAndGenesPerRowCount++;
                }
                return null;
            }

            Allele allele  = alleleSymbolToAllele.get(alleleSymbol);

            if (allele == null) {
                logger.info("No allele accession id found for allele symbol '" + alleleSymbol + "'. Skipping...");
                return null;
            } else {
                alleles.add(new AccDbId(allele.getId().getAccession(), DbIdType.MGI.longValue()));
            }
        } else {
            logger.info("No alleleSymbol was found for bioModel " + bioModelIn + ". Skipping...");
            return null;
        }


        // Extract gene(s). There must be at least one gene.
        if (bioModelIn.getGenes().isEmpty()) {
            logger.error("No gene found for bioModel " + bioModelIn + ". Skipping...");
            return null;
        }
        // Inflate any rows with multiple genes that are separated by ",".
        String[]      geneAccessionIds = bioModelIn.getGenes().iterator().next().getAcc().split(Pattern.quote(","));
        for (String geneAccessionId : geneAccessionIds) {
            genes.add(new AccDbId(geneAccessionId, DbIdType.MGI.longValue()));
        }
        if (genes.size() > 1) {
            logger.info("MULTI-GENE: " + bioModelIn);       // As of 2017-10-06, there were only five of these.
            multipleGenesPerRowCount++;
        }


        // As of 2017-10-05, phenotypes don't appear more than once on an given input line. Just load them.
        phenotypes.add(new AccDbId(bioModelIn.getPhenotypes().iterator().next().getAcc(), DbIdType.MGI.longValue()));


        // If the biological model already exists, use it; otherwise, create a new one with empty collections and add it to the map.
        // BioModelIn always contains exactly one gene, allele, and phenotype.
        BioModelInsertDTOMGI bioModelOut = bioModels.get(bioModelIn);
        if (bioModelOut == null) {
            bioModelOut = new BioModelInsertDTOMGI(bioModelIn.getDbId(), bioModelIn.getAllelicComposition(), bioModelIn.getGeneticBackground(), bioModelIn.getZygosity());
            bioModels.put(bioModelOut, bioModelOut);
            addedBioModelsCount++;
        }


        bioModelOut.getAlleles().addAll(alleles);
        bioModelOut.getGenes().addAll(genes);
        bioModelOut.getPhenotypes().addAll(phenotypes);


        return bioModelOut;
    }

    public Set<String> getErrMessages() {
        return errMessages;
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

    public int getMultipleAllelesAndGenesPerRowCount() {
        return multipleAllelesAndGenesPerRowCount;
    }

    public Map<BioModelInsertDTOMGI, BioModelInsertDTOMGI> getBioModels() {
        return bioModels;
    }
}