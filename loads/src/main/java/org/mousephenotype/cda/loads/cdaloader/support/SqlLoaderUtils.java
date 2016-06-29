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

import org.apache.commons.lang3.StringUtils;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 27/05/16.
 */
public class SqlLoaderUtils {

    private Map<String, Allele>           alleles;          // keyed by allele accession id
    private Map<String, List<ConsiderId>> considerIds;      // keyed by ontology term accession id
    private Map<String, GenomicFeature>   genes;            // keyed by marker accession id
    private Map<String, OntologyTerm>     ontologyTerms;    // keyed by ontology term accession id
    private Map<String, SequenceRegion>   sequenceRegions;  // keyed by strain id (int)
    private Map<String, Strain>           strains;          // keyed by strain accession id
    private Map<String, List<Synonym>>    synonyms;         // keyed by accession id (of the object it is embedded in)
    private Map<String, List<Xref>>       xrefs;            // keyed by accession id (of the object it is embedded in)

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String FEATURES_UNKNOWN    = "unknown";

    public static final String STATUS_ACTIVE       = "active";
    public static final String STATUS_WITHDRAWN    = "withdrawn";

    public static final String NAME_NOT_SPECIFIED  = "Not Specified";

    public static final String BIOTYPE_GENE_STRING = "Gene";
    public static final String BIOTYPE_TM1A_STRING = "Targeted (Floxed/Frt)";
    public static final String BIOTYPE_TM1E_STRING = "Targeted (Reporter)";

    @Autowired
    private JdbcTemplate jdbcTemplate;



    /**
     * Return a list of all alleles, keyed by allele accession id
     * @return a list of all alleles, keyed by allele accession id
     */
    public Map<String, Allele> getAlleles() {
        if (alleles == null) {
            alleles = new ConcurrentHashMap<>(120000);

            List<Allele> allelesList = jdbcTemplate.query("SELECT * FROM allele", new AlleleRowMapper());

            for (Allele allele : allelesList) {
                alleles.put(allele.getId().getAccession(), allele);
            }
        }

        return alleles;
    }
    /**
     * Return the {@link Allele} matching the given {@code alleleAccessionId}
     *
     * @param alleleAccessionId the desired allele accession id (exact match)
     *
     * @return {@link Allele} matching the given {@code alleleAccessionId}, if
     *         found; null otherwise
     */
    public Allele getAllele(String alleleAccessionId) {
        return getAlleles().get(alleleAccessionId);
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
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", pss);

                alleles.put(allele.getId().getAccession(), allele);
            } catch (DuplicateKeyException e) {
                logger.warn("Duplicate allele entry. Accession id: " + allele.getId().getAccession() + ". Name: " + allele.getName() + ". Allele not added.");
            }
        }

        return count;
    }

    /**
     * If {@link BiologicalModelAggregator} doesn't exist, insert it into the database.
     *
     * @param bioModel the {@link BiologicalModelAggregator} instance to be inserted
     * @param bioModelPss - the biological_model prepared statement setter
     * @param bioModelAllelePss - the biological_model_allele prepared statement setter
     * @param bioModelGenomicFeaturePss - the biological_model_genomic_feature prepared statement setter
     * @param bioModelPhenotypePss - the biological_model_phenotype prepared statement setter
     *
     * @return the number of {@code bioModel}s inserted
     */
    public int updateBioModel(BiologicalModelAggregator bioModel,
                              PreparedStatementSetter bioModelPss,
                              PreparedStatementSetter bioModelAllelePss,
                              PreparedStatementSetter bioModelGenomicFeaturePss,
                              PreparedStatementSetter bioModelPhenotypePss) throws CdaLoaderException
    {
        int count = 0;

        try {

            count = jdbcTemplate.update("INSERT INTO biological_model (db_id, allelic_composition, genetic_background) " +
                                "VALUES (?, ?, ?)", bioModelPss);

            int bioModelId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()",Integer.class);
            bioModel.setBiologicalModelId(bioModelId);
        } catch (DuplicateKeyException e) {
            logger.warn("Duplicate biological_model entry: {}. biological model not added.", bioModel);
            return count;
        }

        try {

            count = jdbcTemplate.update("INSERT INTO biological_model_allele (biological_model_id, allele_acc, allele_db_id) " +
                                "VALUES (?, ?, ?)", bioModelAllelePss);

        } catch (DuplicateKeyException e) {

            logger.warn("Duplicate biological_model_allele entry: {}. biological model not added.", bioModel);
            return count;
        }

        try {

            count = jdbcTemplate.update("INSERT INTO biological_model_genomic_feature (biological_model_id, genomic_feature_acc, genomic_feature_db_id) " +
                                "VALUES (?, ?, ?)", bioModelGenomicFeaturePss);

        } catch (DuplicateKeyException e) {

            logger.warn("Duplicate biological_model_genomic_feature entry: {}. biological model not added.", bioModel);
            return count;
        }
        
        try {

            count = jdbcTemplate.update("INSERT INTO biological_model_phenotype (biological_model_id, phenotype_acc, phenotype_db_id) " +
                                "VALUES (?, ?, ?)", bioModelPhenotypePss);

        } catch (DuplicateKeyException e) {

            logger.warn("Duplicate biological_model_phenotype entry: {}. biological model not added.", bioModel);
            return count;
        }

        return count;
    }



    /**
     * Returns the list of all consider accession ids, keyed by ontology term accession id
     * @return the list of all consider accession ids, keyed by ontology term accession id
     */
    public Map<String, List<ConsiderId>> getConsiderIds() {
        if (considerIds == null) {
            considerIds = new ConcurrentHashMap<>(100);

            List<ConsiderId> considerIdList = jdbcTemplate.query("SELECT * FROM consider_id", new ConsiderIdRowMapper());

            for (ConsiderId considerId : considerIdList) {
                if ( ! considerIds.containsKey(considerId.getOntologyTermAccessionId())) {
                    considerIds.put(considerId.getOntologyTermAccessionId(), new ArrayList<>());
                }
                considerIds.get(considerId.getOntologyTermAccessionId()).add(considerId);
            }
        }

        return considerIds;
    }

    /**
     * Return the list of consider accession ids matching the given {@code ontologyTermAccessionId}
     *
     * @param ontologyTermAccessionId the desired ontology term's accession id
     *
     * @return the list of consider ids matching the given {@code ontologyTermAccessionId}, if found; an empty list otherwise
     */
    public List<ConsiderId> getConsiderIds(String ontologyTermAccessionId) {
        return getConsiderIds().get(ontologyTermAccessionId);
    }



    /**
     * Return the <code>GenomicFeature</code> matching the given {@code mgiAccessionId}
     *
     * @param mgiAccessionId the desired mgiAccessionId (exact match)
     *
     * @return {@code GenomicFeature} matching the given {@code mgiAccesionId}, if
     *         found; null otherwise
     */
    public GenomicFeature getGene(String mgiAccessionId) {
        return getGenes().get(mgiAccessionId);
    }

    /**
     * Return the list of <code>GenomicFeature</code>s
     *
     * @return the list of <code>GenomicFeature</code>s
     */
    public Map<String, GenomicFeature> getGenes() {
        if (genes == null) {
            genes = new ConcurrentHashMap<>(150000);

            List<GenomicFeature> geneList = jdbcTemplate.query("SELECT * FROM genomic_feature", new GenomicFeatureRowMapper());

            for (GenomicFeature gene : geneList) {
                genes.put(gene.getId().getAccession(), gene);
            }
        }

        return genes;
    }

    /**
     * If {@link GenomicFeature} doesn't exist, insert it and any of its {@link Synonym}s and {@link Xref}s that don't
     * yet exist, into the database.
     *
     * @param gene the {@link GenomicFeature} to be inserted
     *
     * @return the number of {@code genomicFeature}s inserted
     */
    public int updateGenomicFeature(GenomicFeature gene, PreparedStatementSetter pss) throws CdaLoaderException {
        int count = 0;

        // Insert gene if it does not exist.
        if (getGene(gene.getId().getAccession()) == null) {
            try {
                if (pss != null) {

                    count = jdbcTemplate.update("INSERT INTO genomic_feature (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", pss);

                } else {

                    jdbcTemplate.update("INSERT INTO genomic_feature (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            gene.getId().getAccession(),
                            gene.getId().getDatabaseId(),
                            gene.getSymbol(),
                            gene.getName(),
                            gene.getBiotype().getId().getAccession(),
                            gene.getBiotype().getId().getDatabaseId(),
                            gene.getSubtype() == null ? null : gene.getSubtype().getId().getAccession(),
                            gene.getSubtype() == null ? null : gene.getSubtype().getId().getDatabaseId(),
                            gene.getSequenceRegion() == null ? null : gene.getSequenceRegion().getId(),
                            gene.getStart(),
                            gene.getEnd(),
                            gene.getStrand(),
                            gene.getcMposition(),
                            gene.getStatus());
                }
                genes.put(gene.getId().getAccession(), gene);
            } catch (DuplicateKeyException e) {
                logger.warn("Duplicate genomic_feature entry. Accession id: " + gene.getId().getAccession() + ". GenomicFeature: " + gene.getName() + ". GenomicFeature not added.");
            }
        }

        // Insert synonyms if they do not already exist.
        List<Synonym> synonymList = (gene.getSynonyms() == null ? new ArrayList<>() : gene.getSynonyms());
        for (Synonym synonym : synonymList) {
            if (getSynonym(gene.getId().getAccession(), synonym.getSymbol()) == null) {

                jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                        gene.getId().getAccession(), gene.getId().getDatabaseId(), synonym.getSymbol());

                List<Synonym> tmpSynonymList = new ArrayList<>();
                tmpSynonymList.add(synonym);
                synonyms.put(gene.getId().getAccession(), tmpSynonymList);
            }
        }

        // Insert xrefs if they do not already exist.
        List<Xref> xrefList = (gene.getXrefs() == null ? new ArrayList<>() : gene.getXrefs());
        for (Xref xref : xrefList) {
            if (getXref(xref.getAccession(), xref.getXrefAccession()) == null) {

                jdbcTemplate.update("INSERT INTO xref (acc, db_id, xref_acc, xref_db_id) VALUES(?, ?, ?, ?)",
                        xref.getAccession(), xref.getDatabaseId(), xref.getXrefAccession(), xref.getXrefDatabaseId());

                List<Xref> tmpXrefList = new ArrayList();
                tmpXrefList.add(xref);
                xrefs.put(xref.getAccession(), tmpXrefList);
            }
        }

        return count;
    }



    public Map<String, OntologyTerm> getOntologyTerms() {
        if (ontologyTerms == null) {
            ontologyTerms = new ConcurrentHashMap<>(75000);

            List<OntologyTerm> termList = jdbcTemplate.query("SELECT * FROM ontology_term", new OntologyTermRowMapper());

            for (OntologyTerm term : termList) {
                ontologyTerms.put(term.getId().getAccession(), term);
            }
        }

        return ontologyTerms;
    }

    /**
     * Return the <code>OntologyTerm</code> matching the given {@code accesionId}.
     * <i>NOTE: Ontology terms are unique by accession id.</i>
     *
     * @param accessionId the desired term's accession id
     *
     * @return {@code OntologyTerm} matching the given {@code accesionId} if
     *         found; null otherwise
     */
    public OntologyTerm getOntologyTerm(String accessionId) {
        return (accessionId == null ? null : getOntologyTerms().get(accessionId));
    }

   /** Return the <code>OntologyTerm</code> matching the given {@code dbId} and {@code term} after first looking up
    * and possibly transforming the given term to a standardised term.
    *
    * <b><i>WARNING: BECAUSE OF THE DATA, THIS QUERY CAN POTENTIALLY RETURN MORE THAN ONE ROW. WE ARE LUCKY THAT
    * IT DOES NOT SO FAR.</i></b>
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
     * <b><i>WARNING: BECAUSE OF THE DATA, THIS QUERY CAN POTENTIALLY RETURN MORE THAN ONE ROW. WE ARE LUCKY THAT
     * IT DOES NOT SO FAR.</i></b>
     * @param dbId the desired term's database id
     * @param term the term to be matched
     *
     * @return {@code OntologyTerm} matching the given {@code dbId} and {@code term}, if
     *         found; null if not found. If more than one result is found, CdaLoaderException is thrown.
     *
     * @throws CdaLoaderException if more than one term would be returned
     */

    public OntologyTerm getOntologyTerm(int dbId, String term) throws CdaLoaderException {
        List<OntologyTerm> retVal = new ArrayList<>();

        List<OntologyTerm> ontologyTerms = new ArrayList<>(getOntologyTerms(dbId).values());
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            if (ontologyTerm.getName().equals(term)) {
                retVal.add(ontologyTerm);
            }
        }

        if (retVal.isEmpty()) {
            return null;
        } else if (retVal.size() == 1) {
            return retVal.get(0);
        } else {
            throw new CdaLoaderException("There is more than one ontology term for the given dbId '" + dbId + "' for term name '" + term + "': " + StringUtils.join(retVal, ", "));
        }
    }

private Map<Integer, Map<String, OntologyTerm>> ontologyTermMaps = new ConcurrentHashMap<>();       // keyed by dbId
    /**
     * Return a map of <code>OntologyTerm</code>s matching the given {@code dbId}, indexed by ontology term
     * NOTE: maps are cached by dbId.
     *
     * @param dbId the dbId of the desired terms
     *
     * @return a map of <code>OntologyTerm</code>s matching the given {@code dbId}, indexed by ontology name
     */
    public Map<String, OntologyTerm> getOntologyTerms(int dbId) {
        Map<String, OntologyTerm> retVal = ontologyTermMaps.get(dbId);
        if (retVal == null) {
            retVal = new ConcurrentHashMap<>();
            for (Map.Entry<String, OntologyTerm> entrySet : getOntologyTerms().entrySet()) {
                OntologyTerm term = entrySet.getValue();
                if (term.getId().getDatabaseId() == dbId) {
                    term.setConsiderIds(getConsiderIds(term.getId().getAccession()));
                    term.setSynonyms(getSynonyms(term.getId().getAccession()));
                    retVal.put(term.getName(), term);
                }
            }
            ontologyTermMaps.put(dbId, retVal);
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

        if (getOntologyTerm(term.getId().getAccession()) == null) {
            // Write ontology terms.

            count = jdbcTemplate.update("INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete, replacement_acc) VALUES (?, ?, ?, ?, ?, ?)",
                    term.getId().getAccession(), term.getId().getDatabaseId(), term.getName(), term.getDescription(), term.getIsObsolete(), term.getReplacementAcc());

            ontologyTerms.put(term.getId().getAccession(), term);
        }

        // Write synonym items.
        for (Synonym synonym : term.getSynonyms()) {
            if (getSynonym(term.getId().getAccession(), synonym.getSymbol()) == null) {

                jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                        term.getId().getAccession(), term.getId().getDatabaseId(), synonym.getSymbol());

                List<Synonym> tmpSynonymList = new ArrayList<>();
                tmpSynonymList.add(synonym);
                synonyms.put(term.getId().getAccession(), tmpSynonymList);
            }
        }

        // Write consider_id items.
        for (ConsiderId considerId : term.getConsiderIds()) {

            jdbcTemplate.update("INSERT INTO consider_id (ontology_term_acc, acc) VALUES (?, ?)",
                    term.getId().getAccession(), considerId.getConsiderAccessionId());

            List<ConsiderId> tmpConsiderIdList = new ArrayList<>();
            tmpConsiderIdList.add(considerId);
            considerIds.put(term.getId().getAccession(), tmpConsiderIdList);
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
        List<SequenceRegion> sequenceRegions = new ArrayList<>(getSequenceRegions().values());

        for (SequenceRegion sequenceRegion : sequenceRegions) {
            if (sequenceRegion.getId() == id) {
                return sequenceRegion;
            }
        }

        return null;
    }

    /**
     * Return a map of <code>SequenceRegion</code>s , indexed by region name
     *
     * @return a map of <code>SequenceRegion</code>s, indexed by region name
     */
    public Map<String, SequenceRegion> getSequenceRegions() {
        if (sequenceRegions == null) {
            sequenceRegions = new ConcurrentHashMap<>();
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
                sequenceRegions.put(sequenceRegion.getName(), sequenceRegion);
            }
        }

        return sequenceRegions;
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
        return getStrains().get(accessionId);
    }

    public Map<String, Strain> getStrains() throws CdaLoaderException {

        if (strains == null) {
            strains = new ConcurrentHashMap<>();

            List<Strain> strainList = jdbcTemplate.query("SELECT * FROM strain", new StrainRowMapper());

            for (Strain strain : strainList) {
                strains.put(strain.getId().getAccession(), strain);
            }
        }

        return strains;
    }

    /**
     * If {@link Strain} doesn't exist, insert it and any of its {@link Synonym}s that don't yet exist, into the database.
     *
     * @param strain the strain instance to be inserted
     *
     * @return a map with the number of {@link Strain} and {@link Synonym} objects inserted (keyed by class)
     */
    public Map<Class, Integer> updateStrain(Strain strain) throws CdaLoaderException {
        Map<Class, Integer> retVal = new ConcurrentHashMap<>(50000);
        int strainCount = 0;
        int synonymCount = 0;
        retVal.put(Strain.class, strainCount);
        retVal.put (Synonym.class, synonymCount);

        // Insert Strain term if it does not exist.
        if (getStrain(strain.getId().getAccession()) == null) {

            try {

                strainCount = jdbcTemplate.update("INSERT INTO strain (acc, db_id, biotype_acc, biotype_db_id, name) VALUES (?, ?, ?, ?, ?)",
                        strain.getId().getAccession(), strain.getId().getDatabaseId(), strain.getBiotype().getId().getAccession(), strain.getBiotype().getId().getDatabaseId(), strain.getName());

                strains.put(strain.getId().getAccession(), strain);
            } catch (DuplicateKeyException e) {
                logger.warn("Duplicate strain entry. Accession id: " + strain.getId().getAccession() + ". Strain: " + strain.getName()  + ". Strain not added.");
            } catch (Exception e) {
                logger.error("INSERT FAILED FOR Strain {}. Reason: {}. Skipping...", strain, e.getLocalizedMessage());
            }
        }

        // Insert synonyms if they do not already exist.
        for (Synonym synonym : strain.getSynonyms()) {
            if (getSynonym(strain.getId().getAccession(), synonym.getSymbol()) == null) {
                synonymCount += insertStrainSynonym(strain, synonym);
            }
        }

        return retVal;
    }

    /**
     * Inserts the given synonym using {@code strain}'s id
     *
     * @param strain the strain instance whose synonym is to be inserted
     * @param synonym the synonym to be inserted
     *
     * @return an int indicating the number of synonyms added
     *
     * @throws CdaLoaderException if synonym already exists
     */
    public int insertStrainSynonym(Strain strain, Synonym synonym) throws CdaLoaderException {
        int synonymCount;

        synonymCount = jdbcTemplate.update("INSERT INTO synonym (acc, db_id, symbol) VALUES (?, ?, ?)",
                                   strain.getId().getAccession(), strain.getId().getDatabaseId(), synonym.getSymbol());

        List<Synonym> tmpSynonymList = new ArrayList<>();
        tmpSynonymList.add(synonym);
        synonyms.put(strain.getId().getAccession(), tmpSynonymList);

        return synonymCount;
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
        for (Synonym synonym : getSynonyms(accessionId)) {
            if (synonym.getSymbol().equals(symbol)) {
                return synonym;
            }
        }

        return null;
    }

    /**
     * Return the list of synonyms matching the given {@code accessionId}
     *
     * @param accessionId the desired term's accession id
     *
     * @return the list of synonyms matching the given {@code accesionId} and {@code dbId}, if
     *         found; an empty list otherwise
     */
    public List<Synonym> getSynonyms(String accessionId) {
        List<Synonym> retVal = getSynonyms().get(accessionId);

        return (retVal == null ? new ArrayList<>() : retVal);
    }

    private Map<String, List<Synonym>> getSynonyms() {
        if (synonyms == null) {
            synonyms = new ConcurrentHashMap<>(100000);

            List<Synonym> synonymList = jdbcTemplate.query("SELECT * FROM synonym", new SynonymRowMapper());

            for (Synonym synonym : synonymList) {
                if ( ! synonyms.containsKey(synonym.getAccessionId())) {
                    synonyms.put(synonym.getAccessionId(), new ArrayList<>());
                }
                synonyms.get(synonym.getAccessionId()).add(synonym);
            }
        }

        return synonyms;
    }



    /**
     * Return the xref matching the given {@code accesionId} and {@code xrefAccessionId}, if found; null otherwise
     *
     * @param accessionId the desired term's accession id
     * @param xrefAccessionId the desired term's xref accession id
     *
     * @return  the xref matching the given {@code accesionId} and {@code xrefAccessionId}, if found; null otherwise
     */
    public Xref getXref(String accessionId, String xrefAccessionId) {
        for (Xref xref : getXrefs(accessionId)) {
            if (xref.getXrefAccession().equals(xrefAccessionId)) {
                return xref;
            }
        }

        return null;
    }

    /**
     * Return the list of xrefs matching the given {@code accesionId}, if found; an empty list otherwise
     *
     * @param accessionId the desired xref's accession id
     *
     * @return  the list of xrefs matching the given {@code accesionId}, if found; an empty list otherwise
     */
    public List<Xref> getXrefs(String accessionId) {
        List<Xref> xrefsByAccessionId = getXrefs().get(accessionId);
        return (xrefsByAccessionId == null ? new ArrayList<>() : xrefsByAccessionId);
    }

    public Map<String, List<Xref>> getXrefs() {
        if (xrefs == null) {
            xrefs = new ConcurrentHashMap<>(150000);

            List<Xref> xrefList = jdbcTemplate.query("SELECT * FROM xref", new XrefRowMapper());

            for (Xref xref : xrefList) {
                if ( ! xrefs.containsKey(xref.getAccession())) {
                    xrefs.put(xref.getAccession(), new ArrayList<>());
                }
                xrefs.get(xref.getAccession()).add(xref);
            }
        }

        return xrefs;
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

    @Deprecated
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

            allele.setGene(getGene(rs.getString("acc")));
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

            considerId.setOntologyTermAccessionId(rs.getString("ontology_term_acc"));
            considerId.setConsiderAccessionId(rs.getString("acc"));

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
            feature.setSequenceRegion(getSequenceRegion(rs.getInt("seq_region_id")));
            feature.setStatus(STATUS_ACTIVE);
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
                String coordSystemStrainAcc = rs.getString("coord_system_strain_acc");      // This, and coord_system_strain_db_id, can be null.
                if (coordSystemStrainAcc != null) {
                    strain = getStrain(rs.getString("coord_system_strain_acc"));
                }
            } catch (Exception e) {
                logger.error("EXCEPTION: " + e.getLocalizedMessage());
                e.printStackTrace();
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
            Synonym synonym = new Synonym();

            synonym.setAccessionId(rs.getString("acc"));
            synonym.setDbId(rs.getInt("db_id"));
            synonym.setSymbol(rs.getString("symbol"));

            return synonym;
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