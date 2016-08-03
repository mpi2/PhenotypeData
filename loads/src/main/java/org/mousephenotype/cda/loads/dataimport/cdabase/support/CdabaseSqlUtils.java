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

package org.mousephenotype.cda.loads.dataimport.cdabase.support;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.mousephenotype.cda.loads.legacy.LoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 27/05/16.
 */
public class CdabaseSqlUtils {

    private Map<String, List<ConsiderId>> considerIds;      // keyed by ontology term accession id
    private Map<String, OntologyTerm>     ontologyTerms;    // keyed by ontology term accession id
    private Map<String, SequenceRegion>   sequenceRegions;  // keyed by strain id (int)
    private Map<String, Strain>           strains;          // keyed by accession id
    private Map<String, List<Synonym>>    synonyms;         // keyed by accession id

    private       LoaderUtils loaderUtils = new LoaderUtils();
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());

    public static final String FEATURES_UNKNOWN    = "unknown";

    public static final String STATUS_ACTIVE       = "active";
    public static final String STATUS_WITHDRAWN    = "withdrawn";

    public static final String NAME_NOT_SPECIFIED  = "Not Specified";

    public static final String BIOTYPE_GENE_STRING = "Gene";
    public static final String BIOTYPE_TM1A_STRING = "Targeted (Floxed/Frt)";
    public static final String BIOTYPE_TM1E_STRING = "Targeted (Reporter)";

    @Autowired
    private NamedParameterJdbcTemplate npJdbcTemplate;


    /**
     * Return a list of all alleles, keyed by allele accession id
     * @return a list of all alleles, keyed by allele accession id
     */
    public Map<String, Allele> getAlleles() {
        Map<String, Allele> alleles = new ConcurrentHashMap<>();

        logger.info("Loading alleles.");
        List<Allele> allelesList = npJdbcTemplate.query("SELECT * FROM allele", new AlleleRowMapper());

        for (Allele allele : allelesList) {
            alleles.put(allele.getId().getAccession(), allele);
        }

        logger.info("Loading alleles complete.");

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


    /**
     * Try to insert the alleles. Return the count of inserted alleles.
     *
     * @param alleles A {@link List} of {@link Allele} to be inserted
     *
     * @return the count of inserted alleles.
     */
    public int insertAlleles(List<Allele> alleles) throws DataImportException {
        int count = 0;
        final String query = "INSERT INTO allele (acc, db_id, gf_acc, gf_db_id, biotype_acc, biotype_db_id, symbol, name) " +
                             "VALUES (:acc, :db_id, :gf_acc, :gf_db_id, :biotype_acc, :biotype_db_id, :symbol, :name)";

        // Insert alleles. Ignore any duplicates.
        for (Allele allele : alleles) {
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("acc", allele.getId().getAccession());
                parameterMap.put("db_id", allele.getId().getDatabaseId());
                parameterMap.put("gf_acc", (allele.getGene() == null ? null : allele.getGene().getId().getAccession()));
                parameterMap.put("gf_db_id", (allele.getGene() == null ? null : allele.getGene().getId().getDatabaseId()));
                parameterMap.put("biotype_acc", allele.getBiotype().getId().getAccession());
                parameterMap.put("biotype_db_id", allele.getBiotype().getId().getDatabaseId());
                parameterMap.put("symbol", allele.getSymbol());
                parameterMap.put("name", allele.getName());

                count += npJdbcTemplate.update(query, parameterMap);

            } catch (DuplicateKeyException e) {

            } catch (Exception e) {
                logger.error("Error inserting allele {}: {}. Record skipped...", alleles, e.getLocalizedMessage());
            }
        }

        return count;
    }



    /**
     * Returns the list of all consider accession ids, keyed by ontology term accession id
     * @return the list of all consider accession ids, keyed by ontology term accession id
     */
    public Map<String, List<ConsiderId>> getConsiderIds() {
        if (considerIds == null) {
            considerIds = new ConcurrentHashMap<>();

            List<ConsiderId> considerIdList = npJdbcTemplate.query("SELECT * FROM consider_id", new ConsiderIdRowMapper());

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
        Map<String, GenomicFeature> genes = new ConcurrentHashMap<>();

        logger.info("Loading genes");
        List<GenomicFeature> geneList = npJdbcTemplate.query("SELECT * FROM genomic_feature", new GenomicFeatureRowMapper());

        for (GenomicFeature gene : geneList) {
            genes.put(gene.getId().getAccession(), gene);
        }
        logger.info("Loading genes complete.");

        return genes;
    }

    /**
     * If {@link BiologicalModelAggregator} doesn't exist, insert it into the database.
     *
     * @param bioModels the {@link BiologicalModelAggregator} instance to be inserted
     *
     * @return the number of {@code bioModel}s inserted
     */
    public Map<String, Integer> insertBioModel(List<BiologicalModelAggregator> bioModels) throws DataImportException {
        int count = 0;

        Map<String, Integer> countsMap = new HashMap<>();
        countsMap.put("bioModels", 0);
        countsMap.put("bioModelAlleles", 0);
        countsMap.put("bioModelGenomicFeatures", 0);
        countsMap.put("bioModelPhenotypes", 0);

        final String bioModelInsert = "INSERT INTO biological_model (db_id, allelic_composition, genetic_background) " +
                                      "VALUES (:db_id, :allelic_composition, :genetic_background)";

        final String bioModelAlleleInsert = "INSERT INTO biological_model_allele (biological_model_id, allele_acc, allele_db_id) " +
                                            "VALUES (:biological_model_id, :allele_acc, :allele_db_id)";

        final String bioModelGFInsert = "INSERT INTO biological_model_genomic_feature (biological_model_id, gf_acc, gf_db_id) " +
                                        "VALUES (:biological_model_id, :gf_acc, :gf_db_id)";


        final String bioModelPhenotypeInsert = "INSERT INTO biological_model_phenotype (biological_model_id, phenotype_acc, phenotype_db_id) " +
                                               "VALUES (:biological_model_id, :phenotype_acc, :phenotype_db_id)";

        for (BiologicalModelAggregator bioModel : bioModels) {

            // biological_model
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("db_id", DbIdType.MGI.intValue());
                parameterMap.put("allelic_composition", bioModel.getAllelicComposition());
                parameterMap.put("genetic_background", bioModel.getGeneticBackground());

                count = npJdbcTemplate.update(bioModelInsert, parameterMap);
                countsMap.put("bioModels", countsMap.get("bioModels") + count);

                int bioModelId = npJdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", new HashMap<>(), Integer.class);
                bioModel.setBiologicalModelId(bioModelId);

            } catch (DuplicateKeyException e) {

                logger.warn("Duplicate biological_model entry: {}. biological model not added.", bioModels);
                return countsMap;

            } catch (Exception e) {

                logger.warn("Skipping bioModel {}: {}", bioModels, e.getLocalizedMessage());
            }

            // biological_model_allele
            for (String alleleAccessionId : bioModel.getAlleleAccessionIds()) {
                try {
                    Map<String, Object> parameterMap = new HashMap<>();
                    parameterMap.put("biological_model_id", bioModel.getBiologicalModelId());
                    parameterMap.put("allele_acc", alleleAccessionId);
                    parameterMap.put("allele_db_id", DbIdType.MGI.intValue());

                    count = npJdbcTemplate.update(bioModelAlleleInsert, parameterMap);
                    countsMap.put("bioModelAlleles", countsMap.get("bioModelAlleles") + count);

                } catch (DuplicateKeyException e) {

                    logger.warn("Duplicate biological_model_allele entry: {}. biological model not added.", bioModels);
                    return countsMap;

                } catch (Exception e) {
                    logger.warn("Skipping bioModel {}: {}", bioModels, e.getLocalizedMessage());
                }
            }

            // biological_model_genomic_feature
            for (String markerAccessionId : bioModel.getMarkerAccessionIds()) {
                try {
                    Map<String, Object> parameterMap = new HashMap<>();
                    parameterMap.put("biological_model_id", bioModel.getBiologicalModelId());
                    parameterMap.put("gf_acc", markerAccessionId);
                    parameterMap.put("gf_db_id", DbIdType.MGI.intValue());

                    count = npJdbcTemplate.update(bioModelGFInsert, parameterMap);
                    countsMap.put("bioModelGenomicFeatures", countsMap.get("bioModelGenomicFeatures") + count);

                } catch (DuplicateKeyException e) {

                    logger.warn("Duplicate biological_model_genomic_feature entry: {}. biological model not added.", bioModels);
                    return countsMap;

                } catch (Exception e) {

                    logger.warn("Skipping bioModel {}: {}", bioModels, e.getLocalizedMessage());
                }
            }

            // biological_model_phenotype
            for (String phenotypeAccessionId : bioModel.getMpAccessionIds()) {
                try {
                    Map<String, Object> parameterMap = new HashMap<>();
                    parameterMap.put("biological_model_id", bioModel.getBiologicalModelId());
                    parameterMap.put("phenotype_acc", phenotypeAccessionId);
                    parameterMap.put("phenotype_db_id", DbIdType.MGI.intValue());

                    count = npJdbcTemplate.update(bioModelPhenotypeInsert, parameterMap);
                    countsMap.put("bioModelPhenotypes", countsMap.get("bioModelPhenotypes") + count);

                } catch (DuplicateKeyException e) {

                    logger.warn("Duplicate biological_model_phenotype entry: {}. biological model not added.", bioModels);
                    return countsMap;

                } catch (Exception e) {

                    logger.warn("Skipping bioModel {}: {}", bioModels, e.getLocalizedMessage());
                }
            }
        }

        return countsMap;
    }



    /**
     * Try to insert the gene. If it succeeds, insert any synonyms and xrefs.
     *
     * @param gene the {@link GenomicFeature} to be inserted
     *
     * @return a map, keyed by type (genes, synonyms, xrefs) of the number of {@code gene} components inserted
     */
    public Map<String, Integer> insertGene(GenomicFeature gene) throws DataImportException {
        List<GenomicFeature> geneList = new ArrayList<>();
        geneList.add(gene);

        return insertGenes(geneList);
    }

    /**
     * Try to insert the genes. If an insert fails, skip it and its synonyms and xrefs; if it succeeds, insert any synonyms and xrefs.
     *
     * @param genes the {@link List} of {@link GenomicFeature} instances to be inserted
     *
     * @return a map, keyed by type (genes, synonyms, xrefs) of the number of {@code gene} components inserted
     */
    public Map<String, Integer> insertGenes(List<GenomicFeature> genes) throws DataImportException {
        int count = 0;

        Map<String, Integer> countsMap = new HashMap<>();
        countsMap.put("genes", 0);
        countsMap.put("synonyms", 0);
        countsMap.put("xrefs", 0);

        final String gfInsert = "INSERT INTO genomic_feature (acc, db_id, symbol, name, biotype_acc, biotype_db_id, subtype_acc, subtype_db_id, seq_region_id, seq_region_start, seq_region_end, seq_region_strand, cm_position, status) " +
                                "VALUES (:acc, :db_id, :symbol, :name, :biotype_acc, :biotype_db_id, :subtype_acc, :subtype_db_id, :seq_region_id, :seq_region_start, :seq_region_end, :seq_region_strand, :cm_position, :status)";

        final String synoymInsert = "INSERT INTO synonym (acc, db_id, symbol) " +
                                    "VALUES (:acc, :db_id, :symbol)";

        final String xrefInsert = "INSERT INTO xref (acc, db_id, xref_acc, xref_db_id) " +
                                  "VALUES(:acc, :db_id, :xref_acc, :xref_db_id)";


        // Insert genes. Ignore any duplicates.
        for (GenomicFeature gene : genes) {
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("acc", gene.getId().getAccession());
                parameterMap.put("db_id", gene.getId().getDatabaseId());
                parameterMap.put("symbol", gene.getSymbol());
                parameterMap.put("name", gene.getName());
                parameterMap.put("biotype_acc", gene.getBiotype().getId().getAccession());
                parameterMap.put("biotype_db_id", gene.getBiotype().getId().getDatabaseId());
                parameterMap.put("subtype_acc", gene.getSubtype() == null ? null : gene.getSubtype().getId().getAccession());
                parameterMap.put("subtype_db_id", gene.getSubtype() == null ? null : gene.getSubtype().getId().getDatabaseId());
                parameterMap.put("seq_region_id", gene.getSequenceRegion() == null ? null : gene.getSequenceRegion().getId());
                parameterMap.put("seq_region_start", gene.getStart());
                parameterMap.put("seq_region_end", gene.getEnd());
                parameterMap.put("seq_region_strand", gene.getStrand());
                parameterMap.put("cm_position", gene.getcMposition());
                parameterMap.put("status", gene.getStatus());

                count = npJdbcTemplate.update(gfInsert, parameterMap);
                countsMap.put("genes", countsMap.get("genes") + count);

            } catch (DuplicateKeyException dke) {
                logger.warn("Duplicate genomic_feature entry. Accession id: " + gene.getId().getAccession() + ". GenomicFeature: " + gene.getName() + ". GenomicFeature not added.");
                continue;
            }


            // Insert synonyms. Ignore any duplicates.
            if (gene.getSynonyms() != null) {
                for (Synonym synonym : gene.getSynonyms()) {
                    try {
                        Map<String, Object> parameterMap = new HashMap<>();
                        parameterMap.put("acc", synonym.getAccessionId());
                        parameterMap.put("db_id", synonym.getDbId());
                        parameterMap.put("symbol", synonym.getSymbol());

                        count = npJdbcTemplate.update(synoymInsert, parameterMap);
                        countsMap.put("synonyms", countsMap.get("synonyms") + count);

                        if (count > 0) {
                            updateSynonymMap(synonym.getAccessionId(), synonym);
                        }

                    } catch (DuplicateKeyException dke) {

                    }
                }
            }


            // Insert xrefs. Ignore any duplicates.
            for (Xref xref : gene.getXrefs()) {
                try {
                    Map<String, Object> parameterMap = new HashMap<>();
                    parameterMap.put("acc", gene.getId().getAccession());
                    parameterMap.put("db_id", gene.getId().getDatabaseId());
                    parameterMap.put("xref_acc", xref.getXrefAccession());
                    parameterMap.put("xref_db_id", xref.getXrefDatabaseId());

                    count = npJdbcTemplate.update(xrefInsert, parameterMap);
                    countsMap.put("xrefs", countsMap.get("xrefs") + count);

                } catch (DuplicateKeyException dke) {

                } catch (Exception e) {
                    throw new DataImportException(e + "\n\txref: " + xref.toString());
                }
            }
        }

        return countsMap;
    }


    public Map<String, OntologyTerm> getOntologyTerms() {
        if (ontologyTerms == null) {
            ontologyTerms = new ConcurrentHashMap();

            List<OntologyTerm> termList = npJdbcTemplate.query("SELECT * FROM ontology_term", new OntologyTermRowMapper());

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
    *         found; null if not found. If more than one result is found, DataImportException is thrown.
    *
    * @throws DataImportException
    */
    public OntologyTerm getMappedBiotype(int dbId, String term) throws DataImportException {
        String mappedTerm = loaderUtils.translateTerm(term);

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
     *         found; null if not found. If more than one result is found, DataImportException is thrown.
     *
     * @throws DataImportException if more than one term would be returned
     */

    public OntologyTerm getOntologyTerm(int dbId, String term) throws DataImportException {
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
            throw new DataImportException("There is more than one ontology term for the given dbId '" + dbId + "' for term name '" + term + "': " + StringUtils.join(retVal, ", "));
        }
    }

private Map<Integer, Map<String, OntologyTerm>> ontologyTermMaps = new ConcurrentHashMap<>();       // keyed by dbId
    /**
     * Return a CASE-INSENSITIVE {@link TreeMap} of <code>OntologyTerm</code>s matching the given {@code dbId}, indexed by ontology term
     * NOTE: maps are cached by dbId.
     *
     * @param dbId the dbId of the desired terms
     *
     * @return a map of <code>OntologyTerm</code>s matching the given {@code dbId}, indexed by ontology name
     */
    public Map<String, OntologyTerm> getOntologyTerms(int dbId) {
        Map<String, OntologyTerm> ontologyTerms = ontologyTermMaps.get(dbId);
        if (ontologyTerms == null) {
            ontologyTerms = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            for (Map.Entry<String, OntologyTerm> entrySet : getOntologyTerms().entrySet()) {
                OntologyTerm term = entrySet.getValue();
                if (term.getId().getDatabaseId() == dbId) {
                    term.setConsiderIds(getConsiderIds(term.getId().getAccession()));
                    term.setSynonyms(getSynonyms(term.getId().getAccession()));
                    ontologyTerms.put(term.getName(), term);
                }
            }
            ontologyTermMaps.put(dbId, ontologyTerms);
        }

        Map<String, OntologyTerm> retVal = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        retVal.putAll(ontologyTerms);

        return ontologyTerms;
    }

    public Map<String, Organisation> getOrganisations() {
        Map<String, Organisation> organisations = new ConcurrentHashMap<>();

        List<Organisation> organisationList = npJdbcTemplate.query("SELECT * FROM organisation", new OrganisationRowMapper());

        for (Organisation organisation : organisationList) {
            organisations.put(organisation.getName(), organisation);
        }

        return organisations;
    }

    public Map<String, Project> getProjects() {
        Map<String, Project> projects = new ConcurrentHashMap<>();

        List<Project> projectList = npJdbcTemplate.query("SELECT * FROM project", new ProjectRowMapper());

        for (Project project : projectList) {
            projects.put(project.getName(), project);
        }

        return projects;
    }

    /**
     * Try to insert the ontology terms. If any insert fails, continue to the next term; otherwise, try to insert the synonyms and consider ids.
     *
     * @param terms the {@link List} of {@link OntologyTerm} instances to be inserted
     *
     * @return a map, keyed by type (terms, synonyms, considerIds) of the number of {@code OntologyTerm} components inserted
     */
    public Map<String, Integer> insertOntologyTerm(List<OntologyTerm> terms) {
        int count;

        Map<String, Integer> countsMap = new HashMap<String, Integer>();
        countsMap.put("terms", 0);
        countsMap.put("synonyms", 0);
        countsMap.put("considerIds", 0);

        final String ontologyTermInsert = "INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete, replacement_acc) " +
                                          "VALUES (:acc, :db_id, :name, :description, :is_obsolete, :replacement_acc)";

        final String synonymInsert = "INSERT INTO synonym (acc, db_id, symbol) " +
                                     "VALUES (:acc, :db_id, :symbol)";

        final String considerIdInsert = "INSERT INTO consider_id (ontology_term_acc, acc) " +
                                        "VALUES (:ontology_term_acc, :acc)";

        for (OntologyTerm term : terms) {
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("acc", term.getId().getAccession());
                parameterMap.put("db_id", term.getId().getDatabaseId());
                parameterMap.put("name", term.getName());
                parameterMap.put("description", term.getDescription());
                parameterMap.put("is_obsolete", term.getIsObsolete());
                parameterMap.put("replacement_acc", term.getReplacementAcc());

                count = npJdbcTemplate.update(ontologyTermInsert, parameterMap);
                countsMap.put("terms", countsMap.get("terms") + count);

            } catch (DuplicateKeyException dke) {

                continue;

            } catch (Exception e) {

                logger.error("ontologyTermInsert failed {}. term: {}.\n{}", term, e.getLocalizedMessage());
            }

            for (Synonym synonym : term.getSynonyms()) {
                try {
                    Map<String, Object> parameterMap = new HashMap<>();
                    parameterMap.put("acc", synonym.getAccessionId());
                    parameterMap.put("db_id", synonym.getDbId());
                    parameterMap.put("symbol", synonym.getSymbol());

                    count = npJdbcTemplate.update(synonymInsert, parameterMap);
                    countsMap.put("synonyms", countsMap.get("synonyms") + count);

                    if (count > 0) {
                        updateSynonymMap(synonym.getAccessionId(), synonym);
                    }

                } catch (DuplicateKeyException dke) {

                }
            }

            // Try to insert considerIds. Ignore DuplicateKeyExceptions.

            for (ConsiderId considerId : term.getConsiderIds()) {
                try {
                    Map<String, Object> parameterMap = new HashMap<>();
                    parameterMap.put("ontology_term_acc", term.getId().getAccession());
                    parameterMap.put("acc", considerId.getConsiderAccessionId());

                    count = npJdbcTemplate.update(considerIdInsert, parameterMap);
                    countsMap.put("considerIds", countsMap.get("considerIds") + count);

                } catch (DuplicateKeyException dke) {

                }
            }
        }

        return countsMap;
    }



    /**
     * Try to insert the phenotyped colony. Return the count of inserted phenotype colonies.
     *
     * @param phenotypedColonies the {@link List} of {@link PhenotypedColony} to be inserted
     *
     * @return the count of inserted phenotype colonies.
     */
    public int insertPhenotypedColonies(List<PhenotypedColony> phenotypedColonies) throws DataImportException {
        int count = 0;

        String query = "INSERT INTO phenotyped_colony (" +
                       " colony_name," +
                       " es_cell_name," +
                       " gf_acc," +
                       " gf_db_id," +
                       " allele_acc," +
                       " allele_db_id," +
                       " strain_acc," +
                       " strain_db_id," +
                       " production_centre_organisation_id," +
                       " production_consortium_project_id," +
                       " phenotyping_centre_organisation_id," +
                       " phenotyping_consortium_project_id," +
                       " cohort_production_centre_organisation_id)" +
                       " VALUES (" +
                       " :colony_name," +
                       " :es_cell_name," +
                       " :gf_acc," +
                       " :gf_db_id," +
                       " :allele_acc," +
                       " :allele_db_id," +
                       " :strain_acc," +
                       " :strain_db_id," +
                       " :production_centre_organisation_id," +
                       " :production_consortium_project_id," +
                       " :phenotyping_centre_organisation_id," +
                       " :phenotyping_consortium_project_id," +
                       " :cohort_production_centre_organisation_id)";

        // Insert PhenotypedColonies if they do not exist. Ignore any duplicates.
        for (PhenotypedColony phenotypedColony : phenotypedColonies) {
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("colony_name", phenotypedColony.getColonyName());
                parameterMap.put("es_cell_name", phenotypedColony.getEs_cell_name() == null ? null : phenotypedColony.getEs_cell_name());
                parameterMap.put("gf_acc", phenotypedColony.getGene().getId().getAccession());
                parameterMap.put("gf_db_id", DbIdType.MGI.intValue());
                parameterMap.put("allele_acc", phenotypedColony.getAllele().getId().getAccession());
                parameterMap.put("allele_db_id", DbIdType.MGI.intValue());
                parameterMap.put("strain_acc", phenotypedColony.getStrain().getId().getAccession());
                parameterMap.put("strain_db_id", DbIdType.MGI.intValue());
                parameterMap.put("production_centre_organisation_id", phenotypedColony.getProductionCentre().getId());
                parameterMap.put("production_consortium_project_id", phenotypedColony.getProductionConsortium().getId());
                parameterMap.put("phenotyping_centre_organisation_id", phenotypedColony.getPhenotypingCentre().getId());
                parameterMap.put("phenotyping_consortium_project_id", phenotypedColony.getPhenotypingConsortium().getId());
                parameterMap.put("cohort_production_centre_organisation_id", phenotypedColony.getCohortProductionCentre().getId());

                count = npJdbcTemplate.update(query, parameterMap);

            } catch (DuplicateKeyException e) {

            }
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

            List<SequenceRegion> sequenceRegionList = npJdbcTemplate.query(query, new SequenceRegionRowMapper());

            for (SequenceRegion sequenceRegion : sequenceRegionList) {
                sequenceRegions.put(sequenceRegion.getName(), sequenceRegion);
            }
        }

        return sequenceRegions;
    }



    /**
     * Returns the {@code Strain} instance matching {@code accessionId}, if found; null otherwise
     *
     * @param accessionId the desired strain accession id
     *
     * @return the {@code Strain} instance matching {@code accessionId}, if found; null otherwise
     *
     * @throws DataImportException
     */
    public Strain getStrain(String accessionId) throws DataImportException {
        return getStrains().get(accessionId);
    }

    public Map<String, Strain> getStrains() throws DataImportException {

        if ((strains == null) || strains.isEmpty()) {
            strains = new ConcurrentHashMap<>();

            logger.info("Loading strains.");

            List<Strain> strainList = npJdbcTemplate.query("SELECT * FROM strain", new StrainRowMapper());

            for (Strain strain : strainList) {
                strains.put(strain.getId().getAccession(), strain);
            }

            logger.info("Loading strains complete.");
        }

        return strains;
    }

    /**
     * Try to insert the strain and, if successful, any synonyms.
     *
     * @param strain the {@link Strain} to be inserted
     *
     * @return a map, keyed by type (strains, synonyms) of the number of {@code strain} components inserted
     */
    public Map<String, Integer> insertStrain(Strain strain) throws DataImportException {
        List<Strain> strainList = new ArrayList<>();
        strainList.add(strain);
        return insertStrains(strainList);
    }

    /**
     * Try to insert the strains and, if successful, any synonyms.
     *
     * @param strains the {@link List} of {@link Strain} instances to be inserted
     *
     * @return a map, keyed by type (strains, synonyms) of the number of {@code strain} components inserted
     */
    public Map<String, Integer> insertStrains(List<Strain> strains) throws DataImportException {
        int count;

        Map<String, Integer> countsMap = new HashMap<String, Integer>();
        countsMap.put("strains", 0);
        countsMap.put("synonyms", 0);

        final String strainInsert = "INSERT INTO strain (acc, db_id, biotype_acc, biotype_db_id, name) " +
                                   "VALUES (:acc, :db_id, :biotype_acc, :biotype_db_id, :name)";

        // Insert strains. Ignore any duplicates.
        for (Strain strain : strains) {
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("acc", strain.getId().getAccession());
                parameterMap.put("db_id", strain.getId().getDatabaseId());
                parameterMap.put("biotype_acc", (strain.getBiotype() == null ? null : strain.getBiotype().getId().getAccession()));
                parameterMap.put("biotype_db_id", (strain.getBiotype() == null ? null : strain.getBiotype().getId().getDatabaseId()));
                parameterMap.put("name", strain.getName());

                count = npJdbcTemplate.update(strainInsert, parameterMap);
                countsMap.put("strains", countsMap.get("strains") + count);

            } catch (DuplicateKeyException e) {
                continue;
            }

            // Insert synonyms. Ignore any duplicates.
            if (strain.getSynonyms() != null) {
                for (Synonym synonym : strain.getSynonyms()) {
                    try {
                        Map<String, Object> parameterMap = new HashMap<>();
                        parameterMap.put("acc", synonym.getAccessionId());
                        parameterMap.put("db_id", synonym.getDbId());
                        parameterMap.put("symbol", synonym.getSymbol());

                        count = insertStrainSynonym(synonym);
                        countsMap.put("synonyms", countsMap.get("synonyms") + count);

                        if (count > 0) {
                            updateSynonymMap(synonym.getAccessionId(), synonym);
                        }

                    } catch (DuplicateKeyException dke) {

                    }
                }
            }
        }

        return countsMap;
    }

    /**
     * Inserts the given synonym
     *
     * @param synonym the synonym to be inserted
     *
     * @return an int indicating the number of synonyms added
     *
     */
    public int insertStrainSynonym(Synonym synonym)  {
        int count = 0;

        final String query = "INSERT INTO synonym (acc, db_id, symbol) " +
                             "VALUES (:acc, :db_id, :symbol)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("acc", synonym.getAccessionId());
            parameterMap.put("db_id", synonym.getDbId());
            parameterMap.put("symbol", synonym.getSymbol());

            count = npJdbcTemplate.update(query, parameterMap);

            if (count > 0) {
                updateSynonymMap(synonym.getAccessionId(), synonym);
            }

        } catch (DuplicateKeyException dke) {

        }

        return count;
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
            if (synonym.getSymbol().equalsIgnoreCase(symbol)) {
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
            synonyms = new ConcurrentHashMap<>();
            String lastAcc = "";
            List<Synonym> synonymList = npJdbcTemplate.query("SELECT * FROM synonym ORDER BY acc", new SynonymRowMapper());
            List<Synonym> accSynonyms = new ArrayList<>();
            for (Synonym synonym : synonymList) {
                if ( ! lastAcc.equals(synonym.getAccessionId())) {
                    if ( ! accSynonyms.isEmpty()) {
                        synonyms.put(lastAcc, accSynonyms);
                        accSynonyms = new ArrayList<>();
                    }
                }

                lastAcc = synonym.getAccessionId();
                accSynonyms.add(synonym);
            }

            if ( ! accSynonyms.isEmpty()) {
                synonyms.put(lastAcc, accSynonyms);
            }
        }

        return synonyms;
    }

    /**
     * Add the given synonym to the synonyms map, keyed by accessionId. If there is no entry yet for that accessionId,
     * create one first.
     *
     * @param accessionId the synonyms map key
     * @param synonym the synonym to add
     */
    private void updateSynonymMap(String accessionId, Synonym synonym) {
        List<Synonym> accSynonyms = getSynonyms().get(accessionId);
        if (accSynonyms == null) {
            accSynonyms = new ArrayList<>();
            getSynonyms().put(accessionId, accSynonyms);
        }

        accSynonyms.add(synonym);
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
        Map<String, List<Xref>> xrefs = new ConcurrentHashMap<>();

        List<Xref> xrefList = npJdbcTemplate.query("SELECT * FROM xref", new XrefRowMapper());

        for (Xref xref : xrefList) {
            if ( ! xrefs.containsKey(xref.getAccession())) {
                xrefs.put(xref.getAccession(), new ArrayList<>());
            }
            xrefs.get(xref.getAccession()).add(xref);
        }

        return xrefs;
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

//            allele.setGene(getGene(rs.getString("acc")));
//            allele.setSynonyms(getSynonyms(rs.getString("acc")));

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
            GenomicFeature gene = new GenomicFeature();

            gene.setId(new DatasourceEntityId(rs.getString("acc"), rs.getInt("db_id")));
            gene.setBiotype(getOntologyTerm(rs.getString("biotype_acc")));
            gene.setcMposition(rs.getString("cm_position"));
            gene.setName(rs.getString("name"));
            gene.setSequenceRegion(getSequenceRegion(rs.getInt("seq_region_id")));
            gene.setStatus(STATUS_ACTIVE);
            gene.setSubtype(getOntologyTerm(rs.getString("subtype_acc")));
            gene.setSymbol(rs.getString("symbol"));

            return gene;
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

    public class OrganisationRowMapper implements RowMapper<Organisation> {

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
        public Organisation mapRow(ResultSet rs, int rowNum) throws SQLException {
            Organisation organisation = new Organisation();

            organisation.setId(rs.getInt("id"));
            organisation.setName(rs.getString("name"));
            organisation.setFullname(rs.getString("fullname"));
            organisation.setCountry(rs.getString("country"));

            return organisation;
        }
    }

    public class ProjectRowMapper implements RowMapper<Project> {

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
        public Project mapRow(ResultSet rs, int rowNum) throws SQLException {
            Project project = new Project();

            project.setId(rs.getInt("id"));
            project.setName(rs.getString("name"));
            project.setFullname(rs.getString("fullname"));
            project.setDescription(rs.getString("description"));

            return project;
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

            // NOTE: LOADING THE STRAIN COMPONENT ADDS A TREMENDOUS AMOUNT OF TIME, SO IT'S DISABLED.
//            Strain strain = null;
//            try {
//                String coordSystemStrainAcc = rs.getString("coord_system_strain_acc");      // This, and coord_system_strain_db_id, can be null.
//                if (coordSystemStrainAcc != null) {
//                    strain = getStrain(rs.getString("coord_system_strain_acc"));
//                }
//            } catch (Exception e) {
//                logger.error("EXCEPTION: " + e.getLocalizedMessage());
//                e.printStackTrace();
//            }

            CoordinateSystem coordinateSystem = new CoordinateSystem();
            coordinateSystem.setDatasource(datasource);
            coordinateSystem.setName(rs.getString("coord_system_name"));
            coordinateSystem.setId(rs.getInt("coord_system_id"));
//            coordinateSystem.setStrain(strain);

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