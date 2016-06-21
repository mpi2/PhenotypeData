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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

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

    public static final String ACTIVE_STATUS = "active";

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * Return the <code>GenomicFeature</code> matching the given {@code mgiAccessionId}
     *
     * @param alleleAccessionId the desired allele accession id (exact match)
     *
     * @return {@code Allele} matching the given {@code alleleAccessionId}, if
     *         found; null otherwise
     */
    public Allele getAllele(String alleleAccessionId) {
        Allele retVal = null;

        List<Allele> alleleList = jdbcTemplate.query("SELECT * FROM allele WHERE acc = ?", new AlleleRowMapper(), alleleAccessionId);
        if ( ! alleleList.isEmpty()) {
            retVal = alleleList.get(0);
        }

        return retVal;
    }

    /**
     * If {@link Allele} doesn't exist, insert it into the database.
     *
     * @param allele the {@link Allele} to be inserted
     *
     * @return the number of {@code allele}s inserted
     */
    public int updateAllele(Allele allele, PreparedStatementSetter pss) throws CdaLoaderException {
        int count = 0;

        // Insert Allele if it does not exist.
        if (getAllele(allele.getId().getAccession()) == null) {
            try {
                count = jdbcTemplate.update("INSERT INTO allele (acc, db_id, gf_acc, gf_db_id, biotype_acc, biotype_db_id, symbol, name) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    pss);
            } catch (DuplicateKeyException e) {
                logger.warn("Duplicate allele entry. Accession id: " + allele.getId().getAccession() + ". Name: " + allele.getName() + ". Allele not added.");
            }
        }

        return count;
    }



    /**
     * Return the list of consider ids matching the given {@code ontologyTermAcc}
     *
     * @param ontologyTermAcc the desired ontology term's accession id
     *
     * @return the list of consider ids matching the given {@code ontologyTermAcc}, if found; an empty list otherwise
     */
    public List<ConsiderId> getConsiderIds(String ontologyTermAcc) {

        List<ConsiderId> termList = jdbcTemplate.query("SELECT * FROM consider_id WHERE ontology_term_acc = ?", new ConsiderIdRowMapper(), ontologyTermAcc);

        return termList;
    }



    /**
     * Return the <code>GenomicFeature</code> matching the given {@code mgiAccessionId}
     *
     * @param mgiAccessionId the desired mgiAccessionId (exact match)
     *
     * @return {@code GenomicFeature} matching the given {@code mgiAccesionId}, if
     *         found; null otherwise
     */
    public GenomicFeature getGenomicFeature(String mgiAccessionId) {
        GenomicFeature retVal = null;

        List<GenomicFeature> termList = jdbcTemplate.query("SELECT * FROM genomic_feature WHERE acc = ?", new GenomicFeatureRowMapper(), mgiAccessionId);
        if ( ! termList.isEmpty()) {
            retVal = termList.get(0);
        }

        return retVal;
    }

    /**
     * Return the list of <code>GenomicFeature</code>s
     *
     * @return the list of <code>GenomicFeature</code>s
     */
    public Map<String, GenomicFeature> getGenomicFeatures() {
        Map<String, GenomicFeature> retVal = new HashMap<>();

        List<GenomicFeature> genes = jdbcTemplate.query("SELECT * FROM genomic_feature", new GenomicFeatureRowMapper());

        for (GenomicFeature gene : genes) {
            retVal.put(gene.getId().getAccession(), gene);
        }

        return retVal;
    }

    /**
     * If {@link GenomicFeature} doesn't exist, insert it and any of its {@link Synonym}s and {@link Xref}s that don't
     * yet exist, into the database.
     *
     * @param genomicFeature the {@link GenomicFeature} to be inserted
     *
     * @return the number of {@code genomicFeature}s inserted
     */
    public int updateGenomicFeature(GenomicFeature genomicFeature, PreparedStatementSetter pss) throws CdaLoaderException {
        int count = 0;

        // Insert GenomicFeature term if it does not exist.
        if (getGenomicFeature(genomicFeature.getId().getAccession()) == null) {
            try {
                count = jdbcTemplate.update("INSERT INTO genomic_feature (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        pss);
            } catch (DuplicateKeyException e) {
                logger.warn("Duplicate genomic_feature entry. Accession id: " + genomicFeature.getId().getAccession() + ". GenomicFeature: " + genomicFeature.getName() + ". GenomicFeature not added.");
            }
        }

        // Insert synonyms if they do not already exist.
        List<Synonym> synonyms = (genomicFeature.getSynonyms() == null ? new ArrayList<>() : genomicFeature.getSynonyms());
        for (Synonym synonym : synonyms) {

            if (getSynonym(genomicFeature.getId().getAccession(), synonym.getSymbol()) == null) {
                jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                        genomicFeature.getId().getAccession(), genomicFeature.getId().getDatabaseId(), synonym.getSymbol());
            }
        }

        // Insert xrefs if they do not already exist.
        List<Xref> xrefs = genomicFeature.getXrefs();
        for (Xref xref : xrefs) {
            if (getXref(xref.getAccession(), xref.getXrefAccession()) == null) {
                jdbcTemplate.update("INSERT INTO xref (acc, db_id, xref_acc, xref_db_id) VALUES(?, ?, ?, ?)",
                        xref.getAccession(), xref.getDatabaseId(), xref.getXrefAccession(), xref.getXrefDatabaseId());
            }
        }

        return count;
    }



    /**
     * Return the <code>OntologyTerm</code> matching the given {@code accesionId}
     *
     * @param accessionId the desired term's accession id
     *
     * @return {@code OntologyTerm} matching the given {@code accesionId} if
     *         found; null otherwise
     */
    public OntologyTerm getOntologyTerm(String accessionId) {
        OntologyTerm retVal = null;

        List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term WHERE acc = ?", new OntologyTermRowMapper(), accessionId);
        if ( ! termList.isEmpty()) {
            retVal = termList.get(0);

            retVal.setConsiderIds(getConsiderIds(retVal.getId().getAccession()));
            retVal.setSynonyms(getSynonyms(retVal.getId().getAccession()));
        }

        return retVal;
    }

    /**
     * Return the <code>OntologyTerm</code> matching the given {@code accesionId} and {@code term}
     *
     * @param accessionId the desired term's accession id
     * @param term the desired term
     *
     * @return {@code OntologyTerm} matching the given {@code accesionId} and {@code term}, if
     *         found; null otherwise
     */
    public OntologyTerm getOntologyTerm(String accessionId, String term) {
        OntologyTerm retVal = null;

        List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term WHERE acc = ? AND name = ?", new OntologyTermRowMapper(), accessionId, term);
        if ( ! termList.isEmpty()) {
            retVal = termList.get(0);

            retVal.setConsiderIds(getConsiderIds(retVal.getId().getAccession()));
            retVal.setSynonyms(getSynonyms(retVal.getId().getAccession()));
        }

        return retVal;
    }

   /** Return the <code>OntologyTerm</code> matching the given {@code dbId} and {@code term} after first looking up
    * and possibly transforming the given term to a standardised term.
    *
    * @param dbId the desired term's database id
    * @param term the term to be matched
    *
    * @return {@code OntologyTerm} matching the given {@code dbId} and {@code term}, if
    *         found; null if not found. If more than one result is found, CdaLoaderException is thrown.
    *
    * @throws CdaLoaderException
    */
    public OntologyTerm getMappedBiotype(int dbId, String term) throws CdaLoaderException {
        String mappedTerm = getMappedTerm(term);

        return getOntologyTerm(dbId, mappedTerm);
    }

    /**
     * Return the <code>OntologyTerm</code> matching the given {@code dbId} and {@code term}
     *
     * @param dbId the desired term's database id
     * @param term the term to be matched
     *
     * @return {@code OntologyTerm} matching the given {@code dbId} and {@code term}, if
     *         found; null if not found. If more than one result is found, CdaLoaderException is thrown.
     *
     * @throws CdaLoaderException
     */

    public OntologyTerm getOntologyTerm(int dbId, String term) throws CdaLoaderException {

        OntologyTerm retVal = null;

        List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term WHERE db_id = ? AND name = ?", new OntologyTermRowMapper(), dbId, term);
        if ((termList == null) || (termList.isEmpty())) {
            return null;
        } else if (termList.size() > 1) {
            throw new CdaLoaderException("There is more than one ontology term for the given dbId '" + dbId + "' for term name '" + term + "'.");
        } else {
            retVal = termList.get(0);

            retVal.setConsiderIds(getConsiderIds(retVal.getId().getAccession()));
            retVal.setSynonyms(getSynonyms(retVal.getId().getAccession()));
        }

        return retVal;
    }

    /**
     * Return a map of <code>OntologyTerm</code>s matching the given {@code dbId}, indexed by ontology term
     *
     * @param dbId the dbId of the desired terms
     *
     * @return a map of <code>OntologyTerm</code>s matching the given {@code dbId}, indexed by ontology name
     */
    public Map<String, OntologyTerm> getOntologyTerms(int dbId) {
        Map<String, OntologyTerm> retVal = new HashMap<>();

        List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term WHERE db_id = ?", new OntologyTermRowMapper(), dbId);

        for (OntologyTerm term : termList) {
            term.setConsiderIds(getConsiderIds(term.getId().getAccession()));
            term.setSynonyms(getSynonyms(term.getId().getAccession()));
            retVal.put(term.getName(), term);
        }

        return retVal;
    }

    /**
     * If {@link OntologyTerm} doesn't exist, insert it and any of its {@link Synonym}s that don't yet exist, into the database.
     *
     * @param term the {@link OntologyTerm} to be inserted
     *
     * @return the number of {@code term}s inserted
     */
    public int updateOntologyTerm(OntologyTerm term) {
        int count = 0;

        if (getOntologyTerm(term.getId().getAccession(), term.getName()) == null) {
            // Write ontology terms.
            count = jdbcTemplate.update("INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete, replacement_acc) VALUES (?, ?, ?, ?, ?, ?)",
                    term.getId().getAccession(), term.getId().getDatabaseId(), term.getName(), term.getDescription(), term.getIsObsolete(), term.getReplacementAcc());
        }

        // Write synonym items.
        for (Synonym synonym : term.getSynonyms()) {
            if (getSynonym(term.getId().getAccession(), synonym.getSymbol()) == null) {
                jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                        term.getId().getAccession(), term.getId().getDatabaseId(), synonym.getSymbol());
            }
        }

        // Write consider_id items.
        for (ConsiderId considerId : term.getConsiderIds()) {
            jdbcTemplate.update("INSERT INTO consider_id (ontology_term_acc, acc) VALUES (?, ?)",
                    term.getId().getAccession(), considerId.getAcc());
        }

        return count;
    }



    /**
     * Return the {@code SequenceRegion} mapped by {@b id}, if found; null otherwise
     *
     * @param id the {@code SequenceRegion} primary key
     *
     * @return the {@code SequenceRegion} mapped by {@b id}, if found; null otherwise
     */
    public SequenceRegion getSequenceRegion(int id) {
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
                  "WHERE s.id = ?;";

        List<SequenceRegion> sequenceRegionList = jdbcTemplate.query(query, new SequenceRegionRowMapper(), id);
        if ( ! sequenceRegionList.isEmpty()) {
            retVal = sequenceRegionList.get(0);
        }

        return retVal;
    }

    /**
     * Return a map of <code>SequenceRegion</code>s , indexed by region name
     *
     * @return a map of <code>SequenceRegion</code>s, indexed by region name
     */
    public Map<String, SequenceRegion> getSequenceRegions() {
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
     * Returns the {@code Strain} instance matching {@code accessionId}, if found; null otherwise
     *
     * @param accessionId the desired accessino id
     *
     * @return the {@code Strain} instance matching {@code accessionId}, if found; null otherwise
     *
     * @throws CdaLoaderException
     */
    public Strain getStrain(String accessionId) throws CdaLoaderException {

        List<Strain> strainList = jdbcTemplate.query("SELECT * FROM strain WHERE acc = ?", new StrainRowMapper(), accessionId);

        return (strainList.isEmpty() ? null : strainList.get(0));
    }

    public List<Strain> getStrains() throws CdaLoaderException {

        return jdbcTemplate.query("SELECT * FROM strain", new StrainRowMapper());
    }

    /**
     * If {@link Strain} doesn't exist, insert it and any of its {@link Synonym}s that don't yet exist, into the database.
     *
     * @param strain the strain instance to be inserted
     *
     * @return a map with the number of {@link Strain} and {@link Synonym} objects inserted (keyed by class)
     */
    public Map<Class, Integer> updateStrain(Strain strain) throws CdaLoaderException {
        Map<Class, Integer> retVal = new HashMap<>();
        int strainCount = 0;
        int synonymCount = 0;

        // Insert Strain term if it does not exist.
        if (getStrain(strain.getId().getAccession()) == null) {

            try {
                strainCount = jdbcTemplate.update("INSERT INTO strain (acc, db_id, biotype_acc, biotype_db_id, name) VALUES (?, ?, ?, ?, ?)",
                        strain.getId().getAccession(), strain.getId().getDatabaseId(), strain.getBiotype().getId().getAccession(), strain.getBiotype().getId().getDatabaseId(), strain.getName());
            } catch (DuplicateKeyException e) {
                logger.warn("Duplicate strain entry. Accession id: " + strain.getId().getAccession() + ". Strain: " + strain.getName()  + ". Strain not added.");
            }
        }

        // Insert synonyms if they do not already exist.
        List<Synonym> synonyms = strain.getSynonyms();
        for (Synonym synonym : synonyms) {
            if (getSynonym(strain.getId().getAccession(), synonym.getSymbol()) == null) {
                synonymCount += jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                        strain.getId().getAccession(), strain.getId().getDatabaseId(), synonym.getSymbol());
            }
        }

        retVal.put(Strain.class, strainCount);
        retVal.put (Synonym.class, synonymCount);

        return retVal;
    }



    /**
     * Return the matching synonym if found; null otherwise
     *
     * @param accessionId the desired term's accession id
     * @param symbol the desired synonym term
     *
     * @return the matching synonym if found; null otherwise
     */
    public Synonym getSynonym(String accessionId, String symbol) {
        Synonym synonym = null;

        List<Synonym> synonyms = jdbcTemplate.query("SELECT * FROM synonym WHERE acc = ? AND symbol = ?", new SynonymRowMapper(), accessionId, symbol);

        if ( ! synonyms.isEmpty()) {
            synonym = synonyms.get(0);
        }

        return synonym;
    }

    /**
     * Return the list of synonyms matching the given {@code accesionId}
     *
     * @param accessionId the desired term's accession id
     *
     * @return the list of synonyms matching the given {@code accesionId} and {@code dbId}, if
     *         found; an empty list otherwise
     */
    public List<Synonym> getSynonyms(String accessionId) {

        List<Synonym> termList = jdbcTemplate.query("SELECT * FROM synonym WHERE acc = ?", new SynonymRowMapper(), accessionId);

        return termList;
    }



    /**
     * Return the list of xrefs matching the given {@code accesionId}
     *
     * @param accessionId the desired term's accession id
     *
     * @return the list of synonyms matching the given {@code accesionId} and {@code dbId}, if
     *         found; an empty list otherwise
     */
    public Xref getXref(String accessionId, String xrefAccessionId) {

        List<Xref> xrefList = jdbcTemplate.query("SELECT * FROM xref WHERE acc = ? AND xref_acc = ?", new XrefRowMapper(), accessionId, xrefAccessionId);

        return (xrefList.isEmpty() ? null : xrefList.get(0));
    }

    /**
     * Return the list of xrefs matching the given {@code accesionId}
     *
     * @param accessionId the desired term's accession id
     *
     * @return the list of synonyms matching the given {@code accesionId} and {@code dbId}, if
     *         found; an empty list otherwise
     */
    public List<Xref> getXrefs(String accessionId) {

        List<Xref> xrefList = jdbcTemplate.query("SELECT * FROM xref WHERE acc = ?", new XrefRowMapper(), accessionId);

        return xrefList;
    }


    // MISCELLANEOUS FUNCTIONS



    private final String[][] mappedTerms = new String[][] {
              { "congenic strain",   "congenic" }
            , { "coisogenic strain", "coisogenic" }
    };
    public String getMappedTerm(String term) {
        String retVal = term;
        for (int i = 0; i < mappedTerms.length; i++) {
            if (term.equals(mappedTerms[i][0])) {
                retVal = mappedTerms[i][1];
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


    // ROW MAPPERS


    public class AlleleRowMapper implements RowMapper<Allele> {

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
        public Allele mapRow(ResultSet rs, int rowNum) throws SQLException {
            Allele allele = new Allele();

            allele.setId(new DatasourceEntityId(rs.getString("acc"), rs.getInt("db_id")));
            allele.setBiotype(getOntologyTerm(rs.getString("biotype_acc")));
            allele.setName(rs.getString("name"));
            allele.setSymbol(rs.getString("symbol"));

            allele.setGene(getGenomicFeature(rs.getString("acc")));
            allele.setSynonyms(getSynonyms(rs.getString("acc")));

            return allele;
        }
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
            feature.setBiotype(getOntologyTerm(rs.getString("biotype_acc")));
            feature.setcMposition(rs.getString("cm_position"));
            feature.setName(rs.getString("name"));

            SequenceRegion sequenceRegion = new SequenceRegion();
            feature.setSequenceRegion(getSequenceRegion(rs.getInt("seq_region_id")));
            feature.setStatus(ACTIVE_STATUS);
            feature.setSubtype(getOntologyTerm(rs.getString("subtype_acc")));
            feature.setSymbol(rs.getString("symbol"));

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
                strain.setBiotype(getOntologyTerm(rs.getString("biotype_acc")));
                strain.setId(new DatasourceEntityId(rs.getString("acc"), rs.getInt("db_id")));
                strain.setName(rs.getString("name"));

                strain.setSynonyms(getSynonyms(rs.getString("acc")));

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
}