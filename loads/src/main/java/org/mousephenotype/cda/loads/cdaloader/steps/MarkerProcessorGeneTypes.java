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
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by mrelac on 09/06/16.
 */
public class MarkerProcessorGeneTypes implements ItemProcessor<FieldSet, GenomicFeature> {

    private       int                         addedGeneTypesCount = 0;
    public final  Set<String>                 errMessages         = new HashSet<>();
    private       Map<String, OntologyTerm>   featureTypes;
    private       Map<String, GenomicFeature> genomicFeatures;
    private       int                         lineNumber          = 0;
    private final Logger                      logger              = LoggerFactory.getLogger(this.getClass());
    private       Map<String, SequenceRegion> sequenceRegions;

    // The following ints define the column offset of the given column in the GENE_TYPES file.
    public final static int OFFSET_SEQ_REGION = 0;
    public final static int OFFSET_BIO_TYPE   = 2;
    public final static int OFFSET_START      = 3;
    public final static int OFFSET_END        = 4;
    public final static int OFFSET_STRAND     = 6;
    public final static int OFFSET_COMMENTS   = 8;

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    private void initialise() throws Exception {
        featureTypes.putAll(sqlLoaderUtils.getOntologyTerms(DbIdType.Genome_Feature_Type.intValue()));
        sequenceRegions.putAll(sqlLoaderUtils.getSequenceRegions());
    }

    public MarkerProcessorGeneTypes(Map<String, GenomicFeature> genomicFeatures, Map<String, OntologyTerm> featureTypes, Map<String, SequenceRegion> sequenceRegions) {
        this.genomicFeatures = genomicFeatures;
        this.featureTypes = featureTypes;
        this.sequenceRegions = sequenceRegions;
    }

    @Override
    public GenomicFeature process(FieldSet item) throws Exception {

        lineNumber++;


        // Make sure maps are initialised.
        if (featureTypes.isEmpty()) {
            initialise();
        }

        GenomicFeature feature  = null;

        /*
        * Fields within MGI_GTGUP.gff:
        *   [0] - sequenceRegion
        *   [1] - source (not used)
        *   [2] - biotype
        *   [3] - start
        *   [4] - end
        *   [5] - score (not used)
        *   [6] - strand
        *   [7] - phase (not used)
        *   [8] - comments
         */

        ParsedComment parsedComment = null;
        try {
            parsedComment = new ParsedComment(item.getValues()[OFFSET_COMMENTS]);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage() + ".\t Line number " + lineNumber);
            return null;
        }

        String         accessionId    = parsedComment.getId();
        String         biotype        = item.getValues()[OFFSET_BIO_TYPE];
        String         end            = item.getValues()[OFFSET_END];
        SequenceRegion sequenceRegion = sequenceRegions.get(item.getValues()[OFFSET_SEQ_REGION].substring(3));
        String         start          = item.getValues()[OFFSET_START];
        int            strand         = (item.getValues()[OFFSET_STRAND].equals("+") ? 1 : -1);
        OntologyTerm   subtypeTerm    = featureTypes.get(parsedComment.getNote() != null ? parsedComment.getNote() : "unknown");
        String         symbol         = parsedComment.getName();

        if ( ! biotype.equals("GeneModel")) {

            feature = new GenomicFeature();

            DatasourceEntityId dsId = new DatasourceEntityId();
            dsId.setAccession(accessionId);
            dsId.setDatabaseId(DbIdType.MGI.intValue());

            feature.setId(dsId);
            feature.setBiotype(featureTypes.get(biotype));
            feature.setEnd(Integer.parseInt(end));
            feature.setSequenceRegion(sequenceRegion);
            feature.setStart(Integer.parseInt(start));
            feature.setStatus(SqlLoaderUtils.ACTIVE_STATUS);
            feature.setStrand(strand);
            feature.setSubtype(subtypeTerm);
            feature.setSymbol(symbol);
            feature.setXrefs(new ArrayList<>());

            genomicFeatures.put(accessionId, feature);
            addedGeneTypesCount++;
        }

        return feature;
    }

    public Set<String> getErrMessages() {
        return errMessages;
    }

    public Map<String, GenomicFeature> getGenomicFeatures() {
        return genomicFeatures;
    }

    public int getAddedGeneTypesCount() {
        return addedGeneTypesCount;
    }

    // PRIVATE CLASSES


    private class ParsedComment {
        private String id = null;
        private String name = null;
        private String note = null;

        /*
        * Lines come in the form "ID=MGIxxxxxxx;Name=yyyyy;Note=zzzzz" where ID is the markerAccessionId, Name is the markerSymbol, and Note is the markerFeature (not used).
        * ID is on every line in the file. Name and Note are optional. The three tokens always seem to appear in the
        * same order: ID (always), then Name (if it exists), then Note (if it exists). You can't split on ";" because
        * some names contain ";", and a Note might as well (though I haven't seen one).
        *
        * Extract the ID=MGI:xxxxxxx and remove it from the string.
         */
        public ParsedComment(String comment) throws CdaLoaderException {
            if ((comment == null) || (comment.isEmpty()))
                return;

            String tmp = comment;
            int endIndex = tmp.indexOf(";");

            if (endIndex == -1) {
                id = tmp.substring(3);
                return;
            }
            id = tmp.substring(3, endIndex);

            tmp = tmp.replace("ID=" + id, "");
            if (tmp.isEmpty()) {
                return;
            }

            if (tmp.startsWith(";Name=")) {
                tmp = tmp.replace(";Name=", "");
                endIndex = tmp.indexOf(";Note=");
                if (endIndex == -1) {
                    name = tmp;
                    return;
                } else {
                    name = tmp.substring(0, endIndex);
                    tmp = tmp.replace(name + ";Note=", "");
                    note = tmp;
                }
            }
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getNote() {
            return note;
        }
    }
}