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

package org.mousephenotype.cda.loads.cdaloader.support;

import org.mousephenotype.cda.db.pojo.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by mrelac on 27/05/16.
 */
public class SqlLoaderUtils {

    public static void loadOntologyTerm(JdbcTemplate jdbcTemplate, OntologyTerm term) {
        // Write ontology terms.
        jdbcTemplate.update("INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete, replacement_acc) VALUES (?, ?, ?, ?, ?, ?)",
                term.getId().getAccession(), term.getId().getDatabaseId(), term.getName(), term.getDescription(), term.getIsObsolete(), term.getReplacementAcc());

        // Write synonym items.
        for (Synonym synonym : term.getSynonyms()) {
            jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                    term.getId().getAccession(), term.getId().getDatabaseId(), synonym.getSymbol());
        }

        // Write consider_id items.
        for (ConsiderId considerId : term.getConsiderIds()) {
            jdbcTemplate.update("INSERT INTO consider_id (acc, db_id, term) VALUES (?, ?, ?)",
                    term.getId().getAccession(), term.getId().getDatabaseId(), considerId.getTerm());
        }
    }

    /**
     * Return the <code>OntologyTerm</code> matching the given {@code accesionId} and {@code dbId}
     *
     * @param jdbcTemplate a valid <code>JdbcTemplate</code> instance
     * @param accessionId the desired term's accession id
     * @param dbId the desired term's db id
     *
     * @return {@code OntologyTerm} matching the given {@code accesionId} and {@code dbId}, if
     *         found; null otherwise
     */
    public static OntologyTerm getOntologyTerm(JdbcTemplate jdbcTemplate, String accessionId, int dbId) {
        OntologyTerm retVal = null;

        List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term WHERE acc = ? AND db_id = ?", new OntologyTermRowMapper(), accessionId, dbId);
        if ( ! termList.isEmpty()) {
            retVal = termList.get(0);

            retVal.setConsiderIds(getConsiderIs(jdbcTemplate, retVal.getId().getAccession(), retVal.getId().getDatabaseId()));
            retVal.setSynonyms(getOntologyTermSynonyms(jdbcTemplate, retVal.getId().getAccession(), retVal.getId().getDatabaseId()));
        }

        return retVal;
    }

    /**
     * Return the <code>OntologyTerm</code> matching the given {@code name}
     *
     * @param jdbcTemplate a valid <code>JdbcTemplate</code> instance
     * @param termName the desired term name (exact match)
     *
     * @return {@code OntologyTerm} matching the given {@code accesionId} and {@code dbId}, if
     *         found; null otherwise
     */
    public static OntologyTerm getOntologyTerm(JdbcTemplate jdbcTemplate, String termName) {
        OntologyTerm retVal = null;

        List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term WHERE name = ?", new OntologyTermRowMapper(), termName);
        if ( ! termList.isEmpty()) {
            retVal = termList.get(0);

            retVal.setConsiderIds(getConsiderIs(jdbcTemplate, retVal.getId().getAccession(), retVal.getId().getDatabaseId()));
            retVal.setSynonyms(getOntologyTermSynonyms(jdbcTemplate, retVal.getId().getAccession(), retVal.getId().getDatabaseId()));
        }

        return retVal;
    }

    public static class OntologyTermRowMapper implements RowMapper<OntologyTerm> {

        /**
         * Implementations must implement this method to map each row of data
         * in the ResultSet. This method should not call {@code next()} on
         * the ResultSet; it is only supposed to map values of the current row.
         *
         * @param rs     the ResultSet to map (pre-initialized for the current row)
         * @param rowNum the number of the current row
         * @return the result object for the current row
         * @throws SQLException if a SQLException is encountered getting
         *                      column values (that is, there's no need to catch SQLException)
         */
        @Override
        public OntologyTerm mapRow(ResultSet rs, int rowNum) throws SQLException {
            OntologyTerm term = new OntologyTerm();

            term.setId(new DatasourceEntityId(rs.getString("acc"), rs.getInt("db_id")));
            term.setName(rs.getString("name"));
            term.setDescription(rs.getString("description"));
            Integer isObsolete = rs.getInt("is_obsolete");
            term.setIsObsolete((isObsolete != null) && (isObsolete == 1) ? true : false);
            term.setReplacementAcc(rs.getString("replacement_acc"));

            return term;
        }
    }

    /**
     * Return the list of synonyms matching the given {@code accesionId} and {@code dbId}
     *
     * @param jdbcTemplate a valid <code>JdbcTemplate</code> instance
     * @param accessionId the desired term's accession id
     * @param dbId the desired term's db id
     *
     * @return the list of synonyms matching the given {@code accesionId} and {@code dbId}, if
     *         found; an empty list otherwise
     */
    public static List<Synonym> getOntologyTermSynonyms(JdbcTemplate jdbcTemplate, String accessionId, int dbId) {

        List<Synonym> termList = jdbcTemplate.query("SELECT * FROM synonym WHERE acc = ? AND db_id = ?", new SynonymRowMapper(), accessionId, dbId);

        return termList;
    }

    public static class SynonymRowMapper implements RowMapper<Synonym> {

        /**
         * Implementations must implement this method to map each row of data
         * in the ResultSet. This method should not call {@code next()} on
         * the ResultSet; it is only supposed to map values of the current row.
         *
         * @param rs     the ResultSet to map (pre-initialized for the current row)
         * @param rowNum the number of the current row
         * @return the result object for the current row
         * @throws SQLException if a SQLException is encountered getting
         *                      column values (that is, there's no need to catch SQLException)
         */
        @Override
        public Synonym mapRow(ResultSet rs, int rowNum) throws SQLException {
            Synonym term = new Synonym();

            term.setId(rs.getInt("id"));
            term.setSymbol(rs.getString("symbol"));

            return term;
        }
    }

    /**
     * Return the list of consider ids matching the given {@code accesionId} and {@code dbId}
     *
     * @param jdbcTemplate a valid {@code JdbcTemplate} instance
     * @param accessionId the desired term's accession id
     * @param dbId the desired term's db id
     *
     * @return the list of consider ids matching the given {@code accesionId} and {@code dbId}, if
     *         found; an empty list otherwise
     */
    public static List<ConsiderId> getConsiderIs(JdbcTemplate jdbcTemplate, String accessionId, int dbId) {

        List<ConsiderId> termList = jdbcTemplate.query("SELECT * FROM consider_id WHERE acc = ? AND db_id = ?", new ConsiderIdRowMapper(), accessionId, dbId);

        return termList;
    }

    public static class ConsiderIdRowMapper implements RowMapper<ConsiderId> {

        /**
         * Implementations must implement this method to map each row of data
         * in the ResultSet. This method should not call {@code next()} on
         * the ResultSet; it is only supposed to map values of the current row.
         *
         * @param rs     the ResultSet to map (pre-initialized for the current row)
         * @param rowNum the number of the current row
         * @return the result object for the current row
         * @throws SQLException if a SQLException is encountered getting
         *                      column values (that is, there's no need to catch SQLException)
         */
        @Override
        public ConsiderId mapRow(ResultSet rs, int rowNum) throws SQLException {
            ConsiderId considerId = new ConsiderId();

            considerId.setId(rs.getInt("id"));
            considerId.setTerm(rs.getString("term"));

            return considerId;
        }
    }


    public static void putStrain(JdbcTemplate jdbcTemplate, Strain strain) {
        // Write Strain terms.
        jdbcTemplate.update("INSERT INTO strain (acc, db_id, biotype_acc, biotype_db_id, name) VALUES (?, ?, ?, ?, ?)",
                strain.getId().getAccession(), strain.getId().getDatabaseId(), strain.getBiotype().getId().getAccession(), strain.getBiotype().getId().getDatabaseId(), strain.getName());
        // Write synonym items.
//        for (Synonym synonym : term.getSynonyms()) {
//            jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
//                    term.getId().getAccession(), term.getId().getDatabaseId(), synonym.getSymbol());
//        }
    }
}