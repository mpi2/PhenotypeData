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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            if (getSynonym(jdbcTemplate, term.getId().getAccession(), synonym.getSymbol()) == null) {
                jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                        term.getId().getAccession(), term.getId().getDatabaseId(), synonym.getSymbol());
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
     * Return a map of <code>OntologyTerm</code>s matching the given {@code dbId}, indexed by ontology name
     *
     * @param jdbcTemplate a valid <code>JdbcTemplate</code> instance
     * @param dbId the dbId of the desired terms
     *
     * @return a map of <code>OntologyTerm</code>s matching the given {@code dbId}, indexed by ontology name
     */
    public Map<String, OntologyTerm> getOntologyTerms(JdbcTemplate jdbcTemplate, int dbId) {
        Map<String, OntologyTerm> retVal = new HashMap<>();

        List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term WHERE db_id = ?", new OntologyTermRowMapper(), dbId);

        for (OntologyTerm term : termList) {
            term.setConsiderIds(getConsiderIs(jdbcTemplate, term.getId().getAccession()));
            term.setSynonyms(getSynonyms(jdbcTemplate, term.getId().getAccession()));
            retVal.put(term.getName(), term);
        }

        return retVal;
    }

    /**
     * Return the {@code SequenceRegion} mapped by {@b id}, if found; null otherwise
     *
     * @param jdbcTemplate a valid <code>JdbcTemplate</code> instance
     * @param id the {@code SequenceRegion} primary key
     *
     * @return the {@code SequenceRegion} mapped by {@b id}, if found; null otherwise
     */
    public SequenceRegion getSequenceRegion(JdbcTemplate jdbcTemplate, int id) {
        SequenceRegion retVal = null;
        String query =
          "SELECT\n" +
                  "  s.id AS seq_id\n" +
                  ", s.name AS seq_name\n" +
                  ", s.coord_system_id AS seq_coord_system_id\n" +
                  ", s.length AS seq_length\n" +
                  ", c.id AS coord_system_id\n" +
                  ", c.name AS coord_system_name\n" +
                  ", c.strain_acc AS coord_system_strain_acc\n" +
                  ", c.strain_db_id as coord_system_strain_db_id\n" +
                  ", c.db_id AS coord_system_db_id\n" +
                  "FROM seq_region s\n" +
                  "JOIN coord_system c ON c.id = s.coord_system_id\n" +
                  "WHERE id = ?;";

        List<SequenceRegion> sequenceRegionList = jdbcTemplate.query(query, new SequenceRegionRowMapper(), id);
        if ( ! sequenceRegionList.isEmpty()) {
            retVal = sequenceRegionList.get(0);
        }

        return retVal;
    }

    /**
     * Return a map of <code>SequenceRegion</code>s , indexed by region name
     *
     * @param jdbcTemplate a valid <code>JdbcTemplate</code> instance
     *
     * @return a map of <code>SequenceRegion</code>s, indexed by region name
     */
    public Map<String, SequenceRegion> getSequenceRegions(JdbcTemplate jdbcTemplate) {
        Map<String, SequenceRegion> retVal = new HashMap<>();
        String query =
          "SELECT\n" +
                  "  s.id AS seq_id\n" +
                  ", s.name AS seq_name\n" +
                  ", s.coord_system_id AS seq_coord_system_id\n" +
                  ", s.length AS seq_length\n" +
                  ", c.id AS coord_system_id\n" +
                  ", c.name AS coord_system_name\n" +
                  ", c.strain_acc AS coord_system_strain_acc\n" +
                  ", c.strain_db_id as coord_system_strain_db_id\n" +
                  ", c.db_id AS coord_system_db_id\n" +
                  "FROM seq_region s\n" +
                  "JOIN coord_system c ON c.id = s.coord_system_id;";

        List<SequenceRegion> sequenceRegionList = jdbcTemplate.query(query, new SequenceRegionRowMapper());

        for (SequenceRegion sequenceRegion : sequenceRegionList) {
            retVal.put(sequenceRegion.getName(), sequenceRegion);
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
    public OntologyTerm getOntologyTermByName(JdbcTemplate jdbcTemplate, String termName) {
        OntologyTerm retVal = null;

        List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term WHERE name = ?", new OntologyTermRowMapper(), termName);
        if ( ! termList.isEmpty()) {
            retVal = termList.get(0);

            retVal.setConsiderIds(getConsiderIs(jdbcTemplate, retVal.getId().getAccession()));
            retVal.setSynonyms(getSynonyms(jdbcTemplate, retVal.getId().getAccession()));
        }

        return retVal;
    }

    public class GenomicFeatureRowMapper implements RowMapper<GenomicFeature> {

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
        public GenomicFeature mapRow(ResultSet rs, int rowNum) throws SQLException {
            GenomicFeature feature = new GenomicFeature();

            feature.setId(new DatasourceEntityId(rs.getString("acc"), rs.getInt("db_id")));
            try {
                feature.setBiotype(getOntologyTerm(getJdbcTemplate(), rs.getString("biotype_acc"), rs.getInt("biotype_db_id")));
                feature.setcMposition(rs.getString("cm_position"));
                feature.setName(rs.getString("name"));
                feature.setSequenceRegion(getSequenceRegion(getJdbcTemplate(), rs.getInt("seq_region_id")));
                feature.setStatus("active");
                feature.setSubtype(getOntologyTerm(getJdbcTemplate(), rs.getString("subtype_acc"), rs.getInt("subtype_db_id")));
                feature.setSymbol(rs.getString("symbol"));

            } catch (CdaLoaderException e) {
                throw new SQLException(e);
            }

            return feature;
        }
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

    public class SequenceRegionRowMapper implements RowMapper<SequenceRegion> {

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
        public SequenceRegion mapRow(ResultSet rs, int rowNum) throws SQLException {
            SequenceRegion region = new SequenceRegion();

            Datasource datasource = new Datasource();
            datasource.setId(rs.getInt("coord_system_db_id"));

            Strain strain = null;
            try {
                strain = getStrain(rs.getString("coord_system_strain_acc"));
            } catch (Exception e) {
                logger.error("EXCEPTION: " + e.getLocalizedMessage());
            }

            CoordinateSystem coordinateSystem = new CoordinateSystem();
            coordinateSystem.setDatasource(datasource);
            coordinateSystem.setName(rs.getString("coord_system_name"));
            coordinateSystem.setId(rs.getInt("coord_system_id"));
            coordinateSystem.setStrain(strain);

            region.setCoordinateSystem(coordinateSystem);
            region.setName(rs.getString("seq_name"));
            region.setId(rs.getInt("seq_id"));

            return region;
        }
    }

    /**
     * Return the list of synonyms matching the given {@code accesionId}
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
     * Return the list of xrefs matching the given {@code accesionId}
     *
     * @param jdbcTemplate a valid <code>JdbcTemplate</code> instance
     * @param accessionId the desired term's accession id
     *
     * @return the list of synonyms matching the given {@code accesionId} and {@code dbId}, if
     *         found; an empty list otherwise
     */
    public Xref getXref(JdbcTemplate jdbcTemplate, String accessionId, String xrefAccessionId) {

        List<Xref> xrefList = jdbcTemplate.query("SELECT * FROM xref WHERE acc = ? AND xref_acc = ?", new XrefRowMapper(), accessionId, xrefAccessionId);

        return (xrefList.isEmpty() ? null : xrefList.get(0));
    }

    /**
     * Return the list of xrefs matching the given {@code accesionId}
     *
     * @param jdbcTemplate a valid <code>JdbcTemplate</code> instance
     * @param accessionId the desired term's accession id
     *
     * @return the list of synonyms matching the given {@code accesionId} and {@code dbId}, if
     *         found; an empty list otherwise
     */
    public List<Xref> getXrefs(JdbcTemplate jdbcTemplate, String accessionId) {

        List<Xref> xrefList = jdbcTemplate.query("SELECT * FROM xref WHERE acc = ?", new XrefRowMapper(), accessionId);

        return xrefList;
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

    public class XrefRowMapper implements RowMapper<Xref> {

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
        public Xref mapRow(ResultSet rs, int rowNum) throws SQLException {
            Xref xref = new Xref();

            xref.setId(rs.getInt("id"));
            xref.setAccession(rs.getString("acc"));
            xref.setDatabaseId(rs.getInt("db_id"));
            xref.setXrefAccession(rs.getString("xref_acc"));
            xref.setXrefDatabaseId(rs.getInt("xref_db_id"));

            return xref;
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
     * Insert { @code GenomicFeature} into the {@code genomic_feature} table.
     *
     * @param jdbcTemplate a valid {@code JdbcTemplate} instance
     * @param genomicFeature the {@code GenomicFeature} instance to be inserted
     */
    public void insertOrUpdateGenomicFeature(JdbcTemplate jdbcTemplate, GenomicFeature genomicFeature) throws CdaLoaderException {
        // Insert GenomicFeature term if it does not exist.
        if (getGenomicFeature(getJdbcTemplate(), genomicFeature.getId().getAccession()) == null) {

            try {
                jdbcTemplate.update("INSERT INTO genomic_feature (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        genomicFeature.getId().getAccession(),
                        genomicFeature.getId().getDatabaseId(),
                        genomicFeature.getSymbol(),
                        genomicFeature.getName(),
                        genomicFeature.getBiotype().getId().getAccession(),
                        genomicFeature.getBiotype().getId().getDatabaseId(),
                        genomicFeature.getSubtype() == null ? null : genomicFeature.getSubtype().getId().getAccession(),
                        genomicFeature.getSubtype() == null ? null : genomicFeature.getSubtype().getId().getDatabaseId(),
                        genomicFeature.getSequenceRegion() == null ? null : genomicFeature.getSequenceRegion().getId(),
                        genomicFeature.getStart(),
                        genomicFeature.getEnd(),
                        genomicFeature.getStrand(),
                        genomicFeature.getcMposition(),
                        genomicFeature.getStatus());

            } catch (DuplicateKeyException e) {
                logger.warn("Duplicate genomic_feature entry. Accession id: " + genomicFeature.getId().getAccession() + ". GenomicFeature: " + genomicFeature.getName()  + ". GenomicFeature not added.");
            }
        }

        // Insert synonyms if they do not already exist.
        List<Synonym> synonyms = (genomicFeature.getSynonyms() == null ? new ArrayList<>() : genomicFeature.getSynonyms());
        for (Synonym synonym : synonyms) {

            if (getSynonym(jdbcTemplate, genomicFeature.getId().getAccession(), synonym.getSymbol()) == null) {
                jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                        genomicFeature.getId().getAccession(), genomicFeature.getId().getDatabaseId(), synonym.getSymbol());
            }
        }

        // Insert xrefs if they do not already exist.
        List<Xref> xrefs = genomicFeature.getXrefs();
        for (Xref xref : xrefs) {
            if (getXref(getJdbcTemplate(), xref.getAccession(), xref.getXrefAccession()) == null) {
                jdbcTemplate.update("INSERT INTO xref (acc, db_id, xref_acc, xref_db_id) VALUES(?, ?, ?, ?)",
                        xref.getAccession(), xref.getDatabaseId(), xref.getXrefAccession(), xref.getXrefDatabaseId());
            }
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
        OntologyTerm term = getOntologyTermByName(getJdbcTemplate(), strainType);

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

    /**
     * Return the <code>GenomicFeature</code> matching the given {@code mgiAccessionId}
     *
     * @param jdbcTemplate a valid <code>JdbcTemplate</code> instance
     * @param mgiAccessionId the desired mgiAccessionId (exact match)
     *
     * @return {@code GenomicFeature} matching the given {@code mgiAccesionId}, if
     *         found; null otherwise
     */
    public GenomicFeature getGenomicFeature(JdbcTemplate jdbcTemplate, String mgiAccessionId) {
        GenomicFeature retVal = null;

        List<GenomicFeature> termList = jdbcTemplate.query("SELECT * FROM genomic_feature WHERE acc = ?", new GenomicFeatureRowMapper(), mgiAccessionId);
        if ( ! termList.isEmpty()) {
            retVal = termList.get(0);
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