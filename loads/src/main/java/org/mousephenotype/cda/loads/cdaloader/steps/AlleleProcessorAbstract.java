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

import org.mousephenotype.cda.db.pojo.*;
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

    private       int         addedAllelesCount = 0;
    public final  Set<String> errMessages       = new HashSet<>();
//    private       Map<String, OntologyTerm>   featureTypes;
    private       Map<String, GenomicFeature> genomicFeatures;
    protected       int                         lineNumber          = 0;
    private final Logger                      logger              = LoggerFactory.getLogger(this.getClass());
//    private       Map<String, SequenceRegion> sequenceRegions;

    // The following ints define the column offset of the given column in the GENE_TYPES file.
    public final static int OFFSET_MGI_ACCESSION_ID = 0;
    public final static int OFFSET_SEQ_REGION = 0;
    public final static int OFFSET_BIO_TYPE   = 2;
    public final static int OFFSET_START      = 3;
    public final static int OFFSET_END        = 4;
    public final static int OFFSET_STRAND     = 6;
    public final static int OFFSET_COMMENTS   = 8;

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


//    private void initialise() throws Exception {
//        featureTypes.putAll(sqlLoaderUtils.getOntologyTerms(sqlLoaderUtils.getJdbcTemplate(), DbIdType.Genome_Feature_Type.intValue()));
//        sequenceRegions.putAll(sqlLoaderUtils.getSequenceRegions(sqlLoaderUtils.getJdbcTemplate()));
//    }

    public AlleleProcessorAbstract(Map<String, GenomicFeature> genomicFeatures, Map<String, OntologyTerm> featureTypes, Map<String, SequenceRegion> sequenceRegions) {
        this.genomicFeatures = genomicFeatures;
//        this.featureTypes = featureTypes;
//        this.sequenceRegions = sequenceRegions;
    }

    @Override
    public Allele process(Allele allele) throws Exception {

        lineNumber++;

//        // Validate the file using the content of the "mgi marker accession id" column.
//        if (lineNumber == 1) {
//            if ( ! item.getValues()[OFFSET_MGI_ACCESSION_ID].toLowerCase().startsWith("mgi:")) {
//                throw new CdaLoaderException("Parsing error on line " + lineNumber + ": Expected mgi accession id to begin with 'MGI:. Line: " + StringUtils.join(item, ", "));
//            }
//
//            return null;
//        }


        // Initialise maps on first call to process().
//        if (featureTypes.isEmpty()) {
//            initialise();
//        }






//        String         accessionId    = parsedComment.getId();
//        String         biotype        = item.getValues()[OFFSET_BIO_TYPE];
//        String         end            = item.getValues()[OFFSET_END];
//        SequenceRegion sequenceRegion = sequenceRegions.get(item.getValues()[OFFSET_SEQ_REGION].substring(3));
//        String         start          = item.getValues()[OFFSET_START];
//        int            strand         = (item.getValues()[OFFSET_STRAND].equals("+") ? 1 : -1);
//        OntologyTerm   subtypeTerm    = featureTypes.get(parsedComment.getNote() != null ? parsedComment.getNote() : "unknown");
//        String         symbol         = parsedComment.getName();
//
//        if ( ! biotype.equals("GeneModel")) {
//
//            allele = new GenomicFeature();
//
//            DatasourceEntityId dsId = new DatasourceEntityId();
//            dsId.setAccession(accessionId);
//            dsId.setDatabaseId(DbIdType.MGI.intValue());
//
//            allele.setId(dsId);
//            allele.setBiotype(featureTypes.get(biotype));
//            allele.setEnd(Integer.parseInt(end));
//            allele.setSequenceRegion(sequenceRegion);
//            allele.setStart(Integer.parseInt(start));
//            allele.setStatus(MarkerLoader.ACTIVE_STATUS);
//            allele.setStrand(strand);
//            allele.setSubtype(subtypeTerm);
//            allele.setSymbol(symbol);
//
//            genomicFeatures.put(accessionId, allele);
            addedAllelesCount++;
//        }

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