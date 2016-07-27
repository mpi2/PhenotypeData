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

import org.mousephenotype.cda.db.pojo.GenomicFeature;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.SequenceRegion;
import org.mousephenotype.cda.db.pojo.Synonym;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.dataimport.cdabase.support.CdabaseLoaderUtils;
import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by mrelac on 09/06/16.
 *
 *
 */
public class MarkerProcessorGenes implements ItemProcessor<GenomicFeature, GenomicFeature> {

    private int                         addedGenesCount = 0;
//    private Map<String, GenomicFeature> coordinates;                    // key = marker accession id
    private Map<String, OntologyTerm>   featureTypes;
    private Map<String, GenomicFeature> genes;                          // key = marker accession id
    private Map<String, GenomicFeature> genomicFeatures;
    private int                         lineNumber = 0;
    private Map<String, SequenceRegion> sequenceRegions;
    private List<String>                unknownList    = new ArrayList<>();

    public final        Set<String> errMessages            = new HashSet<>();
    private final       Logger      logger                 = LoggerFactory.getLogger(this.getClass());
    public static final int         START_COLUMN_SENTINAL  = -2;        // Indicates the column is the 'genome coordinate start' column.
    public static final int         END_COLUMN_SENTINAL    = -3;        // Indicates the column is the 'genome coordinate end' column.
    public static final int         STRAND_COLUMN_SENTINAL = -4;        // Indicates the column is the 'strand' column.

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private CdabaseLoaderUtils cdabaseLoaderUtils;

    private final String[] expectedHeadings = new String[] {
              "MGI Accession ID"
            , "Chr"
            , "cM Position"
            , "genome coordinate start"
            , "genome coordinate end"
            , "strand"
            , "Marker Symbol"
            , "Status"
            , "Marker Name"
            , "Marker Type"     // (i.e. biotype)
            , "Feature Type"    // (i.e. subtype)
            , "Marker Synonyms (pipe-separated)"
    };


    private void initialise() throws Exception {
        featureTypes = cdabaseLoaderUtils.getOntologyTerms(DbIdType.Genome_Feature_Type.intValue());
        sequenceRegions = cdabaseLoaderUtils.getSequenceRegions();
    }


    public MarkerProcessorGenes(Map<String, GenomicFeature> genes) {
        this.genes = genes;
    }

    /**
     * RULES:
     * - The first line of the file contains the headings, so GenomicFeature components that are not strings must do something sensible.
     * - The headings are validated in the processor.
     * - Exclude rows of status 'W'.
     * - If strand is "+" in the file, set the GenomicFeature strand field to 1; otherwise, set it to -1.
     */
    @Override
    public GenomicFeature process(GenomicFeature gene) throws Exception {

        lineNumber++;

        // Validate the file using the heading names and initialize any collections.
        if (lineNumber == 1) {
            String[] actualHeadings = new String[] {
                  gene.getId().getAccession()
                , gene.getSequenceRegion().getName()
                , gene.getcMposition()
                , gene.getStart() == START_COLUMN_SENTINAL ? "genome coordinate start" : ""
                , gene.getEnd() == END_COLUMN_SENTINAL ? "genome coordinate end" : ""
                , gene.getStrand() == STRAND_COLUMN_SENTINAL ? "strand" : ""
                , gene.getSymbol()
                , gene.getStatus()
                , gene.getName()
                , gene.getBiotype().getName()
                , gene.getSubtype().getName()
                , gene.getSynonyms().get(0).getSymbol()
            };

            for (int i = 0; i < expectedHeadings.length; i++) {
                if ( ! expectedHeadings[i].equals(actualHeadings[i])) {
                    throw new DataImportException("Expected heading '" + expectedHeadings[i] + "' but found '" + actualHeadings[i] + "'.");
                }
            }

            initialise();

            return null;
        }

        if (gene.getStatus().equals("W")) {
            return null;
        }

        // Fill in the missing common fields in preparation for writing to the database.
        OntologyTerm subtype = null;
        if ( ! gene.getSubtype().getName().isEmpty()) {
            try {
                subtype = featureTypes.get(gene.getSubtype().getName());
                if (subtype == null) {
                    throw new DataImportException("Unknown subtype '" + gene.getSubtype().getName());
                }
            } catch (Exception e) {
                if ( ! unknownList.contains(gene.getSubtype().getName())) {
                    logger.warn("Line {}, Gene {}, subtype '{}' is not in the ontology_term (db_id = 2) table. Setting to 'unknown'.",
                                lineNumber, gene.getId().getAccession(), gene.getSubtype().getName());
                    unknownList.add(gene.getSubtype().getName());
                }
                subtype = featureTypes.get("unknown");
            }
        }

        gene.setBiotype(featureTypes.get(gene.getBiotype().getName()));
        gene.getId().setDatabaseId(DbIdType.MGI.intValue());
        gene.setSequenceRegion(sequenceRegions.get(gene.getSequenceRegion().getName()));
        gene.setStatus(CdabaseLoaderUtils.STATUS_ACTIVE);
        gene.setSubtype(subtype);
        genes.put(gene.getId().getAccession(), gene);
        if (gene.getSynonyms() != null) {
            for (Synonym synonym : gene.getSynonyms()) {
                synonym.setAccessionId(gene.getId().getAccession());
                synonym.setDbId(gene.getId().getDatabaseId());
            }
        }
        addedGenesCount++;

        return gene;
    }

    public Set<String> getErrMessages() {
        return errMessages;
    }

    public Map<String, GenomicFeature> getGenomicFeatures() {
        return genomicFeatures;
    }

    public void setGenomicFeatures(Map<String, GenomicFeature> genomicFeatures) {
        this.genomicFeatures = genomicFeatures;
    }

    public int getAddedGenesCount() {
        return addedGenesCount;
    }
}