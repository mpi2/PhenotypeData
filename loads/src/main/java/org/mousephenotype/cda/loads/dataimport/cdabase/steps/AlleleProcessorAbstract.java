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
import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.dataimport.cdabase.support.CdabaseSqlUtils;
import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 09/06/16.
 */
public abstract class AlleleProcessorAbstract implements ItemProcessor<Allele, Allele> {

    private      int                         addedAllelesCount   = 0;
    private      Map<String, Allele>         alleles             = new ConcurrentHashMap<>();         // Alleles mapped by allele accession id
    private      int                         allelesWithoutGenesCount = 0;
    private      OntologyTerm                biotypeTm1a;
    private      OntologyTerm                biotypeTm1e;
    public final Set<String>                 errMessages         = ConcurrentHashMap.newKeySet();     // This is the java 8 way to create a concurrent hash set.
    private      Map<String, OntologyTerm>   mgiFeatureTypes;
    private      Map<String, GenomicFeature> genes;
    protected    int                         lineNumber          = 0;
    private final Logger                     logger              = LoggerFactory.getLogger(this.getClass());
    private      int                         withdrawnGenesCount = 0;

    @Autowired
    @Qualifier("cdabaseLoaderUtils")
    private CdabaseSqlUtils cdabaseSqlUtils;

    public abstract Allele setBiotype(Allele allele) throws DataImportException;
    public abstract Allele setGene(Allele allele) throws DataImportException;


    public AlleleProcessorAbstract(Map<String, GenomicFeature> genes) {
        this.genes = genes;
    }

    @Override
    public Allele process(Allele allele) throws Exception {

        lineNumber++;

        // Validate the file using the content of the gene and allele accession ids.
        if (lineNumber == 1) {
            if ( ! allele.getId().getAccession().toLowerCase().startsWith("mgi:") ||
               ( ! allele.getGene().getId().getAccession().toLowerCase().startsWith("mgi:"))) {
                throw new DataImportException("Parsing error on line " + lineNumber
                        + ": Expected allele and gene accession ids to begin with 'MGI:'. allele: '"
                        + allele.getId().getAccession() + "'. Gene: '"
                        + allele.getGene().getId().getAccession() + "'");
            }
        }

        // Initialise collections.
        if (mgiFeatureTypes == null) {
            mgiFeatureTypes = cdabaseSqlUtils.getOntologyTerms(DbIdType.MGI.intValue());
        }

        if (alleles.containsKey(allele.getId().getAccession())) {
            return null;
        }

        // Fill in the missing common fields in preparation for writing to the database.
        allele.getId().setDatabaseId(DbIdType.MGI.intValue());

        // Call the remaining methods to finish setting the allele instance.
        allele = setBiotype(allele);
        if (allele == null) {
            return null;
        }

        allele = setGene(allele);
        if (allele == null) {
            return null;
        }

        alleles.put(allele.getId().getAccession(), allele);

        addedAllelesCount++;

        return allele;
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

    public int getAddedAllelesCount() {
        return addedAllelesCount;
    }

    public int getAllelesWithoutGenesCount() {
        return allelesWithoutGenesCount;
    }

    public int getWithdrawnGenesCount() {
        return withdrawnGenesCount;
    }

    // PROTECTED METHODS


    /**
     * If the biotype component cannot be found from the information supplied in the load file, a warning is issued and
     * a null allele is returned; otherwise, the allele is returned, with the biotype set.
     *
     * @param allele the allele being processed
     *
     * @return If the biotype was found, returns the allele being processed; otherwise, returns null.
     */
    protected Allele setBiotypeSkipAlleleIfNoBiotypeFound(Allele allele) {
        OntologyTerm biotype = mgiFeatureTypes.get(allele.getBiotype().getName());
        if (biotype == null) {
            logger.warn("Line {} : NO biotype FOR allele {}. Skipped...", lineNumber, allele.toString());
            return null;
        }

        allele.setBiotype(biotype);

        return allele;
    }

    /**
     * Sets the biotype component to the biotype matching tm1a if the alleleSymbol contains tm1a; otherwise, sets it
     * to the biotype component matching tm1e. Then adds the biotype component to the allele and returns the allele.
     *
     * @param allele the allele being processed
     *
     * @return the allele being processed, with the biotype component set.
     *
     * @throws DataImportException
     */
    protected Allele setBiotypeMouseMutants(Allele allele) throws DataImportException {
        if (biotypeTm1a == null) {
            biotypeTm1a = cdabaseSqlUtils.getOntologyTerm(DbIdType.MGI.intValue(), CdabaseSqlUtils.BIOTYPE_TM1A_STRING);
            biotypeTm1e = cdabaseSqlUtils.getOntologyTerm(DbIdType.MGI.intValue(), CdabaseSqlUtils.BIOTYPE_TM1E_STRING);
        }

        allele.setBiotype(allele.getSymbol().contains("tm1a") ? biotypeTm1a : biotypeTm1e);

        return allele;
    }

    /**
     * Adds the gene component to the specified allele. If the gene cannot be found by the marker accession id
     * specified in the allele.getGene().getId().getAccession, a null gene is added to the allele.
     *
     * @param allele the allele being processed
     *
     * @return the allele being processed, with the gene component set.
     */
    protected Allele setGeneNullIsOk(Allele allele) {
        GenomicFeature gene;
        if ((allele.getGene() == null)
             || (allele.getGene().getId().getAccession().trim().isEmpty())
             || (genes.get(allele.getGene().getId().getAccession())) == null)
        {
            allelesWithoutGenesCount++;
            gene = null;
        } else {
            gene = genes.get(allele.getGene().getId().getAccession());
        }

        allele.setGene(gene);

        return allele;
    }

    /**
     * A gene that does not exist signifies that MGI's report files are out of sync. There should never be any such
     * genes. Log a warning if there are, and add the missing gene to the genomic_features table with status 'withdrawn'.
     *
     * <p>If the marker accession id is null or empty, a null gene is added to the allele and the allele is returned.</p>
     * <p>If the gene is found by the marker accession id, it is added to the allele and the allele is returned.</p>
     * <p>If the gene cannot be found by the marker accession id, the gene is added to the genomic_feature table with the
     * status 'withdrawn' and the biotype set to the ontology term matching "Gene", the newly added gene is added to
     * the allele, and the allele is returned. This case indicates an out-of-sync case with MGI's report files.</p>
     *
     * @param allele the allele being processed
     *
     * @return the allele being processed, with the gene component set.
     *
     * @throws DataImportException
     */
    protected Allele setGeneNullMeansAddWithdraw(Allele allele) throws DataImportException {
        GenomicFeature gene;
        if ((allele.getGene() == null) || (allele.getGene().getId().getAccession().trim().isEmpty())) {
            gene = null;
        } else {
            gene = genes.get(allele.getGene().getId().getAccession());
            if (gene == null) {
                withdrawnGenesCount++;

                OntologyTerm biotype = cdabaseSqlUtils.getOntologyTerm(DbIdType.Genome_Feature_Type.intValue(), CdabaseSqlUtils.BIOTYPE_GENE_STRING);
                gene = new GenomicFeature();
                gene.setId(new DatasourceEntityId(allele.getGene().getId().getAccession(), DbIdType.MGI.intValue()));
                gene.setBiotype(biotype);
                gene.setSymbol(allele.getGene().getSymbol());
                gene.setName(allele.getGene().getSymbol());
                gene.setStatus(CdabaseSqlUtils.STATUS_WITHDRAWN);
                logger.warn("MGI IMSR_report file is out-of-sync. Adding withdrawn gene {} to allele {}.",
                        gene.toString(), allele.toString());

                cdabaseSqlUtils.insertGene(gene);
            }
        }

        allele.setGene(gene);

        return allele;
    }
}