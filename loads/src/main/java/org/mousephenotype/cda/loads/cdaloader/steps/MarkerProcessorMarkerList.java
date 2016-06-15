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
import org.mousephenotype.cda.loads.cdaloader.support.FileHeading;
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by mrelac on 09/06/16.
 *
 *
 */
public class MarkerProcessorMarkerList implements ItemProcessor<FieldSet, GenomicFeature> {

    private       int                         addedMarkerListCount = 0;
    public final  Set<String>                 errMessages     = new HashSet<>();
    private       Map<String, OntologyTerm>   featureTypes;
    private       Map<String, GenomicFeature> genomicFeatures;
    private       int                         lineNumber      = 0;
    private final Logger                      logger          = LoggerFactory.getLogger(this.getClass());
    private       Map<String, SequenceRegion> sequenceRegions;
    private       int                         updatedMarkerListCount = 0;

    // The following ints define the column offset of the given column in the MARKER_LIST file.
    public final static int OFFSET_MGI_ACCESSION_ID = 0;
    public final static int OFFSET_CHROMOSOME       = 1;
    public final static int OFFSET_CM_POSITION      = 2;
    public final static int OFFSET_SYMBOL           = 6;
    public final static int OFFSET_NAME             = 8;
    public final static int OFFSET_MARKER_TYPE      = 9;
    public final static int OFFSET_SYNONYMS         = 11;

    // The following strings define the column names in the MARKER_LIST file.
    public final static String HEADING_MGI_ACCESSION_ID = "MGI Accession ID";
    public final static String HEADING_CHROMOSOME       = "Chr";
    public final static String HEADING_CM_POSITION      = "cM Position";
    public final static String HEADING_SYMBOL           = "Marker Symbol";
    public final static String HEADING_NAME             = "Marker Name";
    public final static String HEADING_MARKER_TYPE      = "Marker Type";
    public final static String HEADING_SYNONYMS         = "Marker Synonyms (pipe-separated)";

    public FileHeading[] fileHeadings = new FileHeading[] {
              new FileHeading(OFFSET_MGI_ACCESSION_ID, HEADING_MGI_ACCESSION_ID)
            , new FileHeading(OFFSET_CHROMOSOME, HEADING_CHROMOSOME)
            , new FileHeading(OFFSET_CM_POSITION, HEADING_CM_POSITION)
            , new FileHeading(OFFSET_SYMBOL, HEADING_SYMBOL)
            , new FileHeading(OFFSET_NAME, HEADING_NAME)
            , new FileHeading(OFFSET_MARKER_TYPE, HEADING_MARKER_TYPE)
            , new FileHeading(OFFSET_SYNONYMS, HEADING_SYNONYMS)
    };

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;


    public MarkerProcessorMarkerList(Map<String, GenomicFeature> genomicFeatures, Map<String, OntologyTerm> featureTypes, Map<String, SequenceRegion> sequenceRegions) {
        this.genomicFeatures = genomicFeatures;
        this.featureTypes = featureTypes;
        this.sequenceRegions = sequenceRegions;
    }

    @Override
    public GenomicFeature process(FieldSet item) throws Exception {

        lineNumber++;

        // Validate the columns using the heading names.
        if (lineNumber == 1) {
            RunStatus status = sqlLoaderUtils.validateHeadings(item.getValues(),fileHeadings);
            if (status.hasErrors()) {
                throw new CdaLoaderException(status.toStringErrorMessages());
            }

            return null;
        }

        /*
        * Fields within MRK_List1.rpt:
        *   [0] - MGI Accession ID
        *   [1] - Chr
        *   [2] - cM Position
        *   [6] - genome coordinate start
        *   [4] - genome coordinate end
        *   [5] - strand
        *   [6] - Marker Symbol
        *   [7] - Status
        *   [8] - Marker Name
         */

        String   mgiAccessionId = item.getValues()[OFFSET_MGI_ACCESSION_ID];
        String   biotype        = item.getValues()[OFFSET_MARKER_TYPE];
        String   chromosome     = item.getValues()[OFFSET_CHROMOSOME];
        String   cMposition     = item.getValues()[OFFSET_CM_POSITION];
        String   name           = item.getValues()[OFFSET_NAME];
        String   symbol         = item.getValues()[OFFSET_SYMBOL];
        String[] synonyms       = new String[0];
        // If there are no synonyms, the column does not exist in the item and will throw an IndexOutOfBoundsException if you try to access it.
        if ((item.getFieldCount() >= OFFSET_SYNONYMS + 1) && (item.getValues()[OFFSET_SYNONYMS] != null)) {
            synonyms = item.getValues()[OFFSET_SYNONYMS].split(",");
        }

        GenomicFeature feature = null;
        if ( ! mgiAccessionId.equals("NULL")) {

            if ( ! genomicFeatures.containsKey(mgiAccessionId)) {

                feature = new GenomicFeature();

                DatasourceEntityId dsId = new DatasourceEntityId();
                dsId.setAccession(mgiAccessionId);
                dsId.setDatabaseId(DbIdType.MGI.intValue());

                feature.setId(dsId);
                feature.setBiotype(featureTypes.get(biotype));
                feature.setSequenceRegion(sequenceRegions.get(chromosome));
                feature.setcMposition(cMposition);
                feature.setStatus(MarkerLoader.ACTIVE_STATUS);
                feature.setSymbol(symbol);
                feature.setSubtype(featureTypes.get("unknown"));
                addedMarkerListCount++;

            } else {

                feature = genomicFeatures.get(mgiAccessionId);
                updatedMarkerListCount++;
            }

            feature.setName(name);

            for(String synonymName : synonyms) {
                Synonym synonym = new Synonym();
                synonym.setSymbol(synonymName);
                feature.addSynonym(synonym);
            }

            genomicFeatures.put(mgiAccessionId, feature);
        }

        return feature;
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

    public int getAddedMarkerListCount() {
        return addedMarkerListCount;
    }

    public int getUpdatedMarkerListCount() {
        return updatedMarkerListCount;
    }
}