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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by mrelac on 09/06/16.
 */
public class MarkerProcessorGeneCoordinates implements ItemProcessor<GenomicFeature, GenomicFeature> {

    private int                         addedCoordinatesCount = 0;
    private Map<String, OntologyTerm>   featureTypes;
    private Map<String, GenomicFeature> coordinates;                      // key = marker accession id
    private int                         lineNumber      = 0;
    private Map<String, SequenceRegion> sequenceRegions;
    private List<String>                unknownList     = new ArrayList<>();

    public final  Set<String> errMessages = new HashSet<>();
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    private void initialise() throws Exception {
        featureTypes = sqlLoaderUtils.getOntologyTerms(DbIdType.Genome_Feature_Type.intValue());
        sequenceRegions = sqlLoaderUtils.getSequenceRegions();
    }

    public MarkerProcessorGeneCoordinates(Map<String, GenomicFeature> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * RULES:
     * - If strand is "+" in the file, set the GenomicFeature strand field to 1; otherwise, set it to -1.
     */
    @Override
    public GenomicFeature process(GenomicFeature gene) throws Exception {

        lineNumber++;

        // Make sure maps are initialised.
        if (featureTypes == null) {
            initialise();
        }

        if (!((gene.getBiotype().getName().toLowerCase().equals("gene")) || (gene.getBiotype().getName().toLowerCase().equals("pseudogene")))) {
            return null;
        }
        if ((gene.getStart() < 0) || (gene.getEnd() < 0)) {
            return null;
        }

        // Fill in the missing common fields in preparation for writing to the database.
        OntologyTerm subtype = null;
        if (!gene.getSubtype().getName().isEmpty()) {
            try {
                subtype = featureTypes.get(gene.getSubtype().getName());
                if (subtype == null) {
                    throw new CdaLoaderException("Unknown subtype '" + gene.getSubtype().getName());
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
        gene.setStatus(SqlLoaderUtils.STATUS_ACTIVE);
        gene.setSubtype(subtype);

        coordinates.put(gene.getId().getAccession(), gene);
        addedCoordinatesCount++;

        return gene;
    }

    public int getAddedCoordinatesCount() {
        return addedCoordinatesCount;
    }

    public Set<String> getErrMessages() {
        return errMessages;
    }

    public Map<String, GenomicFeature> getCoordinates() {
        return coordinates;
    }
}