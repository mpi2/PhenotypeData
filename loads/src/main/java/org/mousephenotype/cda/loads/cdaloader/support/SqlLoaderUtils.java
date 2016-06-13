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
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("komp2Loads")
    private DataSource komp2Loads;


    public void insertOntologyTerm(JdbcTemplate jdbcTemplate, OntologyTerm term) {
        // Write ontology terms.
        jdbcTemplate.update("INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete, replacement_acc) VALUES (?, ?, ?, ?, ?, ?)",
                term.getId().getAccession(), term.getId().getDatabaseId(), term.getName(), term.getDescription(), term.getIsObsolete(), term.getReplacementAcc());

        // Write synonym items.
        for (Synonym synonym : term.getSynonyms()) {


            try {


                jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                        term.getId().getAccession(), term.getId().getDatabaseId(), synonym.getSymbol());


            } catch (DuplicateKeyException e) {
                logger.warn("Duplicate synonym: " + "acc = '" + term.getId().getAccession() + "'. db_id = '" + term.getId().getDatabaseId() + "'. symbol = '" + synonym.getSymbol() + "'. Skipped...");
            }

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
            retVal.setSynonyms(getSynonyms(jdbcTemplate, retVal.getId().getAccession()));
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
            retVal.setSynonyms(getSynonyms(jdbcTemplate, retVal.getId().getAccession()));
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
     *
     * @return the list of synonyms matching the given {@code accesionId} and {@code dbId}, if
     *         found; an empty list otherwise
     */
    public List<Synonym> getSynonyms(JdbcTemplate jdbcTemplate, String accessionId) {

        List<Synonym> termList = jdbcTemplate.query("SELECT * FROM synonym WHERE acc = ?", new SynonymRowMapper(), accessionId);

        return termList;
    }

    /**
     * Return the matching synonym if found; null otherwise
     *
     * @param jdbcTemplate a valid <code>JdbcTemplate</code> instance
     * @param accessionId the desired term's accession id
     * @param symbol the desired synonym term
     *
     * @return the matching synonym if found; null otherwise
     */
    public Synonym getSynonym(JdbcTemplate jdbcTemplate, String accessionId, String symbol) {
        Synonym synonym = null;

        List<Synonym> synonyms = jdbcTemplate.query("SELECT * FROM synonym WHERE acc = ? AND symbol = ?", new SynonymRowMapper(), accessionId, symbol);

        if ( ! synonyms.isEmpty()) {
            synonym = synonyms.get(0);
        }

        return synonym;
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

                strain.setSynonyms(getSynonyms(getJdbcTemplate(), rs.getString("acc")));

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
     * Insert { @code Strain} into the {@code strain} table.
     *
     * @param jdbcTemplate a valid {@code JdbcTemplate} instance
     * @param strain the {@code Strain} instance to be inserted
     */
    public void insertOrUpdateStrain(JdbcTemplate jdbcTemplate, Strain strain) throws CdaLoaderException {
        // Insert Strain term if it does not exist.
        if (getStrain(strain.getId().getAccession()) == null) {

            try {
                jdbcTemplate.update("INSERT INTO strain (acc, db_id, biotype_acc, biotype_db_id, name) VALUES (?, ?, ?, ?, ?)",
                        strain.getId().getAccession(), strain.getId().getDatabaseId(), strain.getBiotype().getId().getAccession(), strain.getBiotype().getId().getDatabaseId(), strain.getName());

            } catch (DuplicateKeyException e) {
                logger.warn("Duplicate strain entry. Accession id: " + strain.getId().getAccession() + ". Strain: " + strain.getName()  + ". Strain not added.");
            }
        }

        // Insert synonyms if they do not already exist.
        List<Synonym> synonyms = strain.getSynonyms();
        for (Synonym synonym : synonyms) {
            if (getSynonym(jdbcTemplate, strain.getId().getAccession(), synonym.getSymbol()) == null) {
                jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                        strain.getId().getAccession(), strain.getId().getDatabaseId(), synonym.getSymbol());
            }
        }
    }

    /**
     * Returns the {@code Strain} instance matching {@code accessionId}, if found; null otherwise
     *
     * @return the {@code Strain} instance matching {@code accessionId}, if found; null otherwise
     *
     * @throws CdaLoaderException
     */

    /**
     * Returns the {@code Strain} instance matching {@code accessionId}, if found; null otherwise
     *
     * @param accessionId the desired accessino id
     *
     * @return the {@code Strain} instance matching {@code accessionId}, if found; null otherwise
     *
     * @throws CdaLoaderException
     */
    public Strain getStrain(String accessionId) throws CdaLoaderException {

        List<Strain> strainList = getJdbcTemplate().query("SELECT * FROM strain WHERE acc = ?", new StrainRowMapper(), accessionId);

        return (strainList.isEmpty() ? null : strainList.get(0));
    }

    public List<Strain> getStrainList() throws CdaLoaderException {

        return getJdbcTemplate().query("SELECT * FROM strain", new StrainRowMapper());
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

        strainType = getMappedStrainType(strainType);
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


    // PRIVATE METHODS


    private final String[][] mappedStrainTypes = new String[][] {
              { "congenic strain",   "congenic" }
            , { "coisogenic strain", "coisogenic" }
    };
    public String getMappedStrainType(String strainType) {
        String retVal = strainType;
        for (int i = 0; i < mappedStrainTypes.length; i++) {
            if (strainType.equals(mappedStrainTypes[i][0])) {
                retVal = mappedStrainTypes[i][1];
                break;
            }
        }

        return retVal;
    }

    public RunStatus validateHeadings(String[] headingRow, FileHeading[] headings) {
        String    actualValue   = "";
        String    expectedValue = "";
        int       offset        = 0;
        RunStatus status        = new RunStatus();

        try {
            for (int i = 0; i < headings.length; i++) {
                offset = headings[i].offset;
                expectedValue = headings[i].heading;
                actualValue = headingRow[offset];
                if ( ! expectedValue.equals(actualValue)) {
                    status.addError("Expected column " + offset
                            + " to equal " + expectedValue + " but was " + actualValue);
                }
            }
        } catch (IndexOutOfBoundsException e) {
            status.addError("Index out of bounds. Expected column " + expectedValue + " at offset " + offset + ". Heading row = " + headingRow);
        }

        return status;
    }
}