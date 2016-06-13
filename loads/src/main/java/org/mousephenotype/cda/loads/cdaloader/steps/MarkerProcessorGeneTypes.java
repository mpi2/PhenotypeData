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

import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.SequenceRegion;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by mrelac on 09/06/16.
 */
class MarkerProcessorGeneTypes implements ItemProcessor<FieldSet, GenomicFeature> {

    private      int                             addedGenomicFeaturesCount = 0;
    public final Set<String>                     errMessages               = new HashSet<>();
//    private      HashMap<String, OntologyTerm>   featureTypes              = null;
    private      int                             lineNumber                = 0;
    private      HashMap<String, SequenceRegion> sequenceRegions           = null;

    // The following ints define the column offset of the given column in the GENE_TYPES file.
    public final static int OFFSET_SEQ_REGION = 0;
    public final static int OFFSET_BIO_TYPE   = 2;
    public final static int OFFSET_START      = 3;
    public final static int OFFSET_END        = 4;
    public final static int OFFSET_STRAND     = 6;
    public final static int OFFSET_COMMENTS   = 8;


    @Override
    public GenomicFeature process(FieldSet item) throws Exception {

        lineNumber++;

        HashMap<String, GenomicFeature> genomicFeatures = new HashMap<>();

        String         accessionId;
        GenomicFeature feature   = null;
        String         comments  = item.getValues()[OFFSET_COMMENTS];
        String         seqRegion = item.getValues()[OFFSET_SEQ_REGION];
        String         biotype   = item.getValues()[OFFSET_BIO_TYPE];
        String         start     = item.getValues()[OFFSET_START];
        String         end       = item.getValues()[OFFSET_END];
        int            strand    = (item.getValues()[OFFSET_STRAND].equals("+") ? 1 : -1);

        ParsedComment parsedComment = new ParsedComment(item.getValues()[OFFSET_COMMENTS]);

        accessionId = parsedComment.getId();

        if ( ! biotype.equals("GeneModel")) {

            feature = new GenomicFeature();









//            DatasourceEntityId dsId = new DatasourceEntityId();
//            dsId.setAccession(accessionId);
//            dsId.setDatabaseId(DbIdType.MGI.intValue());
//            feature.setId(dsId);
//
//            OntologyTerm biotypeTerm = featureTypes.get(biotype); //ontologyTermDAO.getOntologyTermByName(biotype);
//            OntologyTerm subtypeTerm = featureTypes.get(properties.get("Note")); //ontologyTermDAO.getOntologyTermByName(properties.get("Note"));
//
//            if (subtypeTerm == null) {
//                subtypeTerm = featureTypes.get("unknown");
//            }
//
//            feature.setBiotype(biotypeTerm);
//            feature.setSubtype(subtypeTerm);
//
//            feature.setSymbol(properties.get("Name"));
//
//            SequenceRegion r = sequenceRegions.get(seqRegion);
//            feature.setSequenceRegion(r);
//
//            feature.setStart(Integer.parseInt(start));
//            feature.setEnd(Integer.parseInt(end));
//
//            feature.setStrand(strand);
//
//            genomicFeatures.put(accessionId, feature);
            addedGenomicFeaturesCount++;

        }

        return feature;
    }


    // PRIVATE CLASSES


    private class ParsedComment {
        private String id = null;
        private String name = null;
        private String note = null;
        
        public ParsedComment(String comment) throws CdaLoaderException {
            if ((comment == null) || (comment.isEmpty()))
                return;

            String[] parts = comment.split(";");
            for (String part : parts) {
                String[] innerParts = part.split("=");

                switch (innerParts[0]) {
                    case "ID":
                        id = innerParts[1];
                        break;

                    case "Name":
                        name = innerParts[1];
                        break;

                    case "Note":
                        note = innerParts[1];
                        break;

                    default:
                        throw new CdaLoaderException("Unexpected comment '" + comment + "'.  token '" + innerParts[0] + "' with value '" + innerParts[1] + "' in MGI_GTUP.gff");
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