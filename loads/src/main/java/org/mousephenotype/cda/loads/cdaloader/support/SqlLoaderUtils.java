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
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by mrelac on 27/05/16.
 */
public class SqlLoaderUtils {

    @Autowired
    @Qualifier("komp2Loads")
    private DataSource komp2Loads;


    public void insertOntologyTerm(JdbcTemplate jdbcTemplate, OntologyTerm term) {
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
            jdbcTemplate.update("INSERT INTO consider_id (ontology_term_acc, acc) VALUES (?, ?)",
                    term.getId().getAccession(), considerId.getAcc());
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
    public OntologyTerm getOntologyTerm(JdbcTemplate jdbcTemplate, String accessionId, int dbId) {
        OntologyTerm retVal = null;

        List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term WHERE acc = ? AND db_id = ?", new OntologyTermRowMapper(), accessionId, dbId);
        if ( ! termList.isEmpty()) {
            retVal = termList.get(0);

            retVal.setConsiderIds(getConsiderIs(jdbcTemplate, retVal.getId().getAccession()));
            retVal.setSynonyms(getSynonyms(jdbcTemplate, retVal.getId().getAccession(), retVal.getId().getDatabaseId()));
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
    public OntologyTerm getOntologyTerm(JdbcTemplate jdbcTemplate, String termName) {
        OntologyTerm retVal = null;

        List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term WHERE name = ?", new OntologyTermRowMapper(), termName);
        if ( ! termList.isEmpty()) {
            retVal = termList.get(0);

            retVal.setConsiderIds(getConsiderIs(jdbcTemplate, retVal.getId().getAccession()));
            retVal.setSynonyms(getSynonyms(jdbcTemplate, retVal.getId().getAccession(), retVal.getId().getDatabaseId()));
        }

        return retVal;
    }

    public class OntologyTermRowMapper implements RowMapper<OntologyTerm> {

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
    public List<Synonym> getSynonyms(JdbcTemplate jdbcTemplate, String accessionId, int dbId) {

        List<Synonym> termList = jdbcTemplate.query("SELECT * FROM synonym WHERE acc = ? AND db_id = ?", new SynonymRowMapper(), accessionId, dbId);

        return termList;
    }

    public class StrainRowMapper implements RowMapper<Strain> {

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
        public Strain mapRow(ResultSet rs, int rowNum) throws SQLException {
            Strain strain = new Strain();

            try {
                strain.setBiotype(getOntologyTerm(getJdbcTemplate(), rs.getString("biotype_acc"), rs.getInt("biotype_db_id")));
                strain.setId(new DatasourceEntityId(rs.getString("acc"), rs.getInt("db_id")));
                strain.setName(rs.getString("name"));

                strain.setSynonyms(getSynonyms(getJdbcTemplate(), rs.getString("acc"), rs.getInt("db_id")));

            } catch (Exception e) {

                throw new RuntimeException(e);
            }

            return strain;
        }
    }

    public class SynonymRowMapper implements RowMapper<Synonym> {

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
     * Return the list of consider ids matching the given {@code ontologyTermAcc}
     *
     * @param jdbcTemplate a valid {@code JdbcTemplate} instance
     * @param ontologyTermAcc the desired ontology term's accession id
     *
     * @return the list of consider ids matching the given {@code ontologyTermAcc}, if found; an empty list otherwise
     */
    public List<ConsiderId> getConsiderIs(JdbcTemplate jdbcTemplate, String ontologyTermAcc) {

        List<ConsiderId> termList = jdbcTemplate.query("SELECT * FROM consider_id WHERE ontology_term_acc = ?", new ConsiderIdRowMapper(), ontologyTermAcc);

        return termList;
    }

    public class ConsiderIdRowMapper implements RowMapper<ConsiderId> {

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

            considerId.setAcc(rs.getString("acc"));
            considerId.setOntologyTermAcc(rs.getString("ontology_term_acc"));

            return considerId;
        }
    }

    /**
     * Insert { @code Strain} into the {@code strain} table. Synonyms are not loaded.
     *
     * @param jdbcTemplate a valid {@code JdbcTemplate} instance
     * @param strain the {@code Strain} instance to be inserted
     */
    public void insertStrain(JdbcTemplate jdbcTemplate, Strain strain) {
        // Insert Strain terms.
        jdbcTemplate.update("INSERT INTO strain (acc, db_id, biotype_acc, biotype_db_id, name) VALUES (?, ?, ?, ?, ?)",
                strain.getId().getAccession(), strain.getId().getDatabaseId(), strain.getBiotype().getId().getAccession(), strain.getBiotype().getId().getDatabaseId(), strain.getName());
    }

    /**
     * Insert the list of {@code List&ld;Synonym&gt;} into the {@code synonym} table. The strain must already exist.
     *
     * @param jdbcTemplate a valid {@code JdbcTemplate} instance
     * @param strain the {@code Strain} instance containing the list of synonyms to be inserted. The strain is not changed.
     */
    public void insertStrainSynonym(JdbcTemplate jdbcTemplate, Strain strain) {

        for (Synonym synonym : strain.getSynonyms()) {
            jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                    strain.getId().getAccession(), strain.getId().getDatabaseId(), synonym.getSymbol());
        }
    }

    public List<Strain> getStrains() throws CdaLoaderException {

        List<Strain> strains = getJdbcTemplate().query("SELECT * FROM strain", new StrainRowMapper());


        return strains;
    }

    /**
     * Returns the {@code OntologyTerm} matching the strain type (e.g. "inbred strain", "coisogenic", "Not Applicable",
     * etc.)
     *
     * @param strainType the strain type (e.g. "inbred strain", "coisogenic", "Not Applicable"
     * @return the {@code OntologyTerm} matching the strain type
     *
     * @throws CdaLoaderException if the strain type is not found
     */
    public OntologyTerm getBiotype(String strainType) throws CdaLoaderException {

        OntologyTerm term = getOntologyTerm(getJdbcTemplate(), strainType);

        if (term == null) {
            throw new CdaLoaderException("No CV term found for strain type " + strainType);
        }

        return term;
    }

    /**
     * Returns a new {@code JdbcTemplate}
     * @return a new {@code JdbcTemplate}
     * @throws CdaLoaderException
     */
    public JdbcTemplate getJdbcTemplate() throws CdaLoaderException {
        JdbcTemplate jdbcTemplate;
        try {
            jdbcTemplate = new JdbcTemplate(komp2Loads);
        } catch (Exception e) {
            throw new CdaLoaderException(e);
        }

        return jdbcTemplate;
    }
}