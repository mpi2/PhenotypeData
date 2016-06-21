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
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by mrelac on 09/06/16.
 */
public abstract class AlleleProcessorAbstract implements ItemProcessor<Allele, Allele> {

    private      int                        addedAllelesCount = 0;
    public final Set<String>                errMessages       = new HashSet<>();
    private      Map<String, OntologyTerm>  featureTypes;
    private     Map<String, GenomicFeature> genomicFeatures;
    protected   int                         lineNumber        = 0;
    private final Logger                    logger            = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    public AlleleProcessorAbstract(Map<String, GenomicFeature> genomicFeatures, Map<String, OntologyTerm> featureTypes) {
        this.genomicFeatures = genomicFeatures;
        this.featureTypes = featureTypes;
    }

    @Override
    public Allele process(Allele allele) throws Exception {

        lineNumber++;

        // Validate the file using the content of the gene and allele accession ids.
        if (lineNumber == 1) {
            if ( ! allele.getId().getAccession().toLowerCase().startsWith("mgi:") ||
               ( ! allele.getGene().getId().getAccession().toLowerCase().startsWith("mgi:"))) {
                throw new CdaLoaderException("Parsing error on line " + lineNumber
                        + ": Expected allele and gene accession ids to begin with 'MGI:'. allele: "
                        + allele.getId().getAccession() + ". Gene: "
                        + allele.getGene().getId().getAccession());
            }

            return null;
        }

        if (allele.getBiotype().getName().equals("GeneModel")) {
            return null;
        }

        OntologyTerm biotype = featureTypes.get(allele.getBiotype().getName());
        if (biotype == null) {
            logger.warn("Line {} : NO biotype FOR allele {}. Skipped...", lineNumber, allele.toString());
            return null;
        }

        if (allele.getGene().getId().getAccession().trim().isEmpty()) {
            return null;                                                // Ignore empty genes.
        }

        GenomicFeature gene = (allele.getGene() == null ? null : genomicFeatures.get(allele.getGene().getId().getAccession()));

        // Fill in the missing fields in preparation for writing to the database.
        allele.getId().setDatabaseId(DbIdType.MGI.intValue());
        allele.setGene(gene);
        allele.setBiotype(biotype);

        addedAllelesCount++;

        return allele;
    }

    public Set<String> getErrMessages() {
        return errMessages;
    }

    public Map<String, GenomicFeature> getGenomicFeatures() {
        return genomicFeatures;
    }

    public int getAddedAllelesCount() {
        return addedAllelesCount;
    }
}