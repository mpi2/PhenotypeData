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
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.support.CdaLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mrelac on 26/04/16.
 */
public class MarkerWriter implements ItemWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private CdaLoaderUtils cdaLoaderUtils;

    private MarkerPSSetter       pss     = new MarkerPSSetter();
    private Map<String, Integer> written = new HashMap<>();

    public MarkerWriter() {

        written.put("genes", 0);
        written.put("synonyms", 0);
        written.put("xrefs", 0);
    }

    /**
     * Process the supplied data element. Will not be called with any null items
     * in normal operation.
     *
     * @param items items to be written
     * @throws Exception if there are errors. The framework will catch the
     *                   exception and convert or rethrow it as appropriate.
     */
    @Override
    public void write(List items) throws Exception {
        for (Object genomicFeature1 : items) {
            GenomicFeature gene = (GenomicFeature) genomicFeature1;
            pss.setFeature(gene);

            try {
                Map<String, Integer> counts = cdaLoaderUtils.insertGene(gene, pss);
                written.put("genes", written.get("genes") + counts.get("genes"));
                written.put("synonyms", written.get("synonyms") + counts.get("synonyms"));
                written.put("xrefs", written.get("xrefs") + counts.get("xrefs"));

            } catch (Exception e) {
                throw new CdaLoaderException(e.getLocalizedMessage() + "\n\t gene: " + gene);
            }
        }
    }

    public class MarkerPSSetter implements PreparedStatementSetter {
        private GenomicFeature feature;

        public void setFeature(GenomicFeature feature) {
            this.feature = feature;
        }

        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            Integer biotypeDbId = (feature.getBiotype() == null ? null : feature.getBiotype().getId().getDatabaseId());
            String biotypeAcc = (feature.getBiotype() == null ? null : feature.getBiotype().getId().getAccession());
            Integer subtypeDbId = (feature.getSubtype() == null ? null : feature.getSubtype().getId().getDatabaseId());
            String subtypeAcc = (feature.getSubtype() == null ? null : feature.getSubtype().getId().getAccession());

//          INSERT INTO genomic_feature (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status)
//          VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

            ps.setString(1, feature.getId().getAccession());                // acc
            ps.setInt(2, feature.getId().getDatabaseId());                  // db_id
            ps.setString(3, feature.getSymbol());                           // symbol
            ps.setString(4, feature.getName());                             // name
            if (biotypeAcc == null) {
                ps.setNull(5, Types.VARCHAR);                               // biotype_acc
                ps.setNull(6, Types.INTEGER);                               // biotype_db_id
            } else {
                ps.setString(5, biotypeAcc);
                ps.setInt(6, biotypeDbId);
            }
            if (subtypeAcc == null) {
                ps.setNull(7, Types.VARCHAR);                               // subtype_acc
                ps.setNull(8, Types.INTEGER);                               // subtype_db_id
            } else {
                ps.setString(7, subtypeAcc);                                // subtype_acc
                ps.setInt(8, subtypeDbId);                                  // subtype_db_id
            }
            if (feature.getSequenceRegion() == null) {
                ps.setNull(9, Types.INTEGER);                               // seq_region_id
            } else {
                ps.setInt(9, feature.getSequenceRegion().getId());          // seq_region_id
            }
            ps.setInt(10, feature.getStart());                              // seq_region_start
            ps.setInt(11, feature.getEnd());                                // seq_region_end
            ps.setInt(12, feature.getStrand());                             // seq_region_strand
            if ((feature.getcMposition() == null) || (feature.getcMposition().trim().isEmpty())) {
                ps.setNull(13, Types.VARCHAR);
            } else {
                ps.setString(13, feature.getcMposition());                  // cm_position
            }
            ps.setString(14, feature.getStatus());                          // status
        }
    }

    public int getWrittenGenes() {
        return written.get("genes");
    }

    public int getWrittenSynonyms() {
        return written.get("synonyms");
    }

    public int getWrittenXrefs() {
        return written.get("xrefs");
    }
}