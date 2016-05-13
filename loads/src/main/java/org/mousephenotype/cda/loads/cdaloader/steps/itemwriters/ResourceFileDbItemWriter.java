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

package org.mousephenotype.cda.loads.cdaloader.steps.itemwriters;

import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Synonym;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by mrelac on 26/04/16.
 */
public class ResourceFileDbItemWriter implements ItemWriter {
    @Autowired
    @Qualifier("komp2Loads")
    private DataSource komp2Loads;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier("komp2TxManager")
    private PlatformTransactionManager tx;

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
        for (Object term1 : items) {
            OntologyTerm term = (OntologyTerm) term1;
            jdbcTemplate.update("INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete, replacement_acc) VALUES (?, ?, ?, ?, ?, ?)",
                    term.getId().getAccession(), term.getId().getDatabaseId(), term.getName(), term.getDescription(), term.getIsObsolete(), term.getReplacementAcc());

            // Write synonym items.
            for (Synonym synonym : term.getSynonyms()) {
                jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                        term.getId().getAccession(), term.getId().getDatabaseId(), synonym.getSymbol());
            }

            // Write consider_id items.
            for (String considerId : term.getConsiderIds()) {
                jdbcTemplate.update("INSERT INTO consider_id (acc, db_id, term) VALUES (?, ?, ?)",
                        term.getId().getAccession(), term.getId().getDatabaseId(), considerId);
            }
        }
    }
}