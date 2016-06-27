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
import org.mousephenotype.cda.loads.cdaloader.support.SqlLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

/**
 * Created by mrelac on 26/04/16.
 */
public class AlleleWriter implements ItemWriter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("sqlLoaderUtils")
    private SqlLoaderUtils sqlLoaderUtils;

    private AllelePSSetter pss = new AllelePSSetter();


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

        for (Object allele1 : items) {
            Allele allele = (Allele) allele1;
            pss.setAllele(allele);
            sqlLoaderUtils.updateAllele(allele, pss);
        }
    }

    public class AllelePSSetter implements PreparedStatementSetter {
        private Allele allele;

        public void setAllele(Allele allele) {
            this.allele = allele;
        }
private int count = 0;
        @Override
        public void setValues(PreparedStatement ps) throws SQLException {
            String geneAcc = (allele.getGene() == null ? null : allele.getGene().getId().getAccession());
            Integer geneDbId = (allele.getGene() == null ? null : allele.getGene().getId().getDatabaseId());

//          INSERT INTO allele (acc, db_id, gf_acc, gf_db_id, biotype_acc, biotype_db_id, symbol, name) 
//          VALUES (?, ?, ?, ?, ?, ?, ?, ?)

            ps.setString(1, allele.getId().getAccession());                 // acc
            ps.setInt(2, allele.getId().getDatabaseId());                   // db_id
            if (geneAcc == null) {
                ps.setNull(3, Types.VARCHAR);                               // gf_acc
                ps.setNull(4, Types.INTEGER);                               // gf_db_id
            } else {
                ps.setString(3, geneAcc);                                   // gf_acc
                ps.setInt(4, geneDbId);                                     // gf_db_id
            }
            ps.setString(5, allele.getBiotype().getId().getAccession());    // biotype_acc
            ps.setInt(6, allele.getBiotype().getId().getDatabaseId());      // biotype_db_id
            ps.setString(7, allele.getSymbol());                            // symbol
            ps.setString(8, allele.getName());                              // name
        }
    }
}