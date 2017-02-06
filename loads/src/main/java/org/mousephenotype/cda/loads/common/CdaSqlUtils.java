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

package org.mousephenotype.cda.loads.common;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.loads.create.extract.cdabase.support.BiologicalModelAggregator;
import org.mousephenotype.cda.loads.create.load.support.EuroPhenomeStrainMapper;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.loads.legacy.LoaderUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by mrelac on 27/05/16.
 */
public class CdaSqlUtils {

    private Map<String, Set<AlternateId>> alternateIds;     // keyed by ontology term accession id
    private Map<String, Set<ConsiderId>>  considerIds;      // keyed by ontology term accession id
    private Map<String, OntologyTerm>     ontologyTerms;    // keyed by ontology term accession id
    private Map<String, SequenceRegion>   sequenceRegions;  // keyed by strain id (int)
    private SqlUtils                      sqlUtils = new SqlUtils();
    private Map<String, Strain>           strains;          // keyed by accession id
    private Map<String, Strain>           strainsByName;    // keyed by strain name
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


    public static final String OBSERVATION_INSERT = "INSERT INTO observation (" +
            "db_id, biological_sample_id, parameter_id, parameter_stable_id, sequence_id, population_id," +
            "observation_type, missing, parameter_status, parameter_status_message) " +
            "VALUES (:dbId, :biologicalSampleId, :parameterId, :parameterStableId, :sequenceId, :populationId, " +
            ":observationType, :missing, :parameterStatus, :parameterStatusMessage)";

    @NotNull
    private NamedParameterJdbcTemplate jdbcCda;


    @Inject
    public CdaSqlUtils(NamedParameterJdbcTemplate jdbcCda) {
        this.jdbcCda = jdbcCda;
    }

    /**
     * Return a list of all alleles, keyed by allele accession id
     * @return a list of all alleles, keyed by allele accession id
     */
    public Map<String, Allele> getAlleles() {
        Map<String, Allele> alleles = new ConcurrentHashMap<>();

        logger.info("Loading alleles.");
        List<Allele> allelesList = jdbcCda.query("SELECT * FROM allele", new AlleleRowMapper());

        for (Allele allele : allelesList) {
            alleles.put(allele.getId().getAccession(), allele);
        }

        logger.info("Loading alleles complete.");

        return alleles;
    }

    /**
     * Return a list of all alleles, keyed by allele symbol
     * @return a list of all alleles, keyed by allele symbol
     */
    public Map<String, Allele> getAllelesBySymbol() {
        Map<String, Allele> alleles = new ConcurrentHashMap<>();

        logger.info("Loading alleles by symbol.");
        List<Allele> allelesList = jdbcCda.query("SELECT * FROM allele", new AlleleRowMapper());

        for (Allele allele : allelesList) {
            alleles.put(allele.getSymbol(), allele);
        }

        logger.info("Loading alleles by symbol complete.");

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
    public int insertAlleles(List<Allele> alleles) throws DataLoadException {
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

                count += jdbcCda.update(query, parameterMap);

            } catch (DuplicateKeyException e) {

            } catch (Exception e) {
                logger.error("Error inserting allele {}: {}. Record skipped...", alleles, e.getLocalizedMessage());
            }
        }

        return count;
    }


    /**
     * Return the {@link BiologicalModel} for the given input parameters
     *
     * @param geneAccessionId gene accession id
     * @param allele_symbol allele symbol
     * @param background_strain_name background strain name
     *
     * @return the {@link BiologicalModel} for the given input parameters
     */
    public BiologicalModel getBiologicalModelByJoins(String geneAccessionId, String allele_symbol, String background_strain_name) {
        BiologicalModel bm = null;
        String query =
                "SELECT DISTINCT bm.*\n" +
                "FROM biological_model bm\n" +
                "JOIN biological_model_allele bma ON bma.biological_model_id=bm.id\n" +
                "JOIN allele a ON a.acc=bma.allele_acc\n" +
                "JOIN biological_model_genomic_feature bmgf ON bmgf.biological_model_id=bm.id\n" +
                "JOIN genomic_feature gf ON gf.acc=bmgf.gf_acc\n" +
                "JOIN biological_model_strain bms ON bms.biological_model_id = bm.id\n" +
                "JOIN strain s ON s.acc = bms.strain_acc\n" +
                "JOIN phenotyped_colony pc ON pc.gf_acc = gf.acc AND pc.allele_symbol = a.symbol AND pc.background_strain_name = s.name\n" +
                "WHERE pc.gf_acc = :gf_acc AND pc.allele_symbol = :allele_symbol AND pc.background_strain_name = :background_strain_name\n";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("gf_acc", geneAccessionId);
        parameterMap.put("allele_symbol", allele_symbol);
        parameterMap.put("background_strain_name", background_strain_name);

        List<BiologicalModel> bmAggregatorList = jdbcCda.query(query, parameterMap, new BiologicalModelRowMapper());

        if ( ! bmAggregatorList.isEmpty()) {
            bm = bmAggregatorList.get(0);
        }

        return bm;
    }


    /**
     *
     * @return a map of {@link BiologicalSample}, keyed by specimen external_id (AKA stableId).
     */
    public Map<String, BiologicalSample> getBiologicalSamples() {

        Map<String, BiologicalSample> map = new HashMap<>();
        String query = "SELECT * FROM biological_sample";

        List<BiologicalSample> samples = jdbcCda.query(query, new BiologicalSampleRowMapper());
        for (BiologicalSample sample : samples) {
            map.put(sample.getStableId(), sample);
        }

        return map;
    }


    /**
     * Return the {@link BiologicalModel} for the given input parameters
     *
     * @param allelicComposition allelic composition
     * @param backgroundStrainName background strain name
     *
     * @return the {@link BiologicalModel} for the given input parameters
     */
    public BiologicalModel getBiologicalModel(String allelicComposition, String backgroundStrainName) {
        BiologicalModel bm = null;
        String query =
                "SELECT * FROM biological_model\n" +
                "WHERE allelic_composition = :allelic_composition AND genetic_background = :genetic_background\n";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("allelic_composition", allelicComposition);
        parameterMap.put("genetic_background", backgroundStrainName);

        List<BiologicalModel> bmAggregatorList = jdbcCda.query(query, parameterMap, new BiologicalModelRowMapper());

        if ( ! bmAggregatorList.isEmpty()) {
            bm = bmAggregatorList.get(0);
        }

        return bm;
    }



    /**
     * @return the set of all alternate accession ids in a {@link Map} keyed by alternate accession id
     */
    public Map<String, Set<AlternateId>> getAlternateIds() {
        if (alternateIds == null) {
            alternateIds = new ConcurrentHashMap<>();

            List<AlternateId> alternateIdList = jdbcCda.query("SELECT * FROM alternate_id", new AlternateIdRowMapper());

            for (AlternateId alternateId : alternateIdList) {
                if ( ! alternateIds.containsKey(alternateId.getAlternateAccessionId())) {
                    alternateIds.put(alternateId.getAlternateAccessionId(), new HashSet<>());
                }

                alternateIds.get(alternateId.getAlternateAccessionId()).add(alternateId);
            }
        }

        return alternateIds;
    }

    /**
     * Return the {@link Set} of ontology accession ids matching the given {@code alternateAccessionId}, if found;
     * an empty set otherwise
     *
     * @param alternateAccessionId the accession id to check
     *
     * @return the ontology term accession id associated with the given alternate accession id, if found; an empty
     * set otherwise
     */
    public Set<AlternateId> getAlternateIds(String alternateAccessionId) {
        Set<AlternateId> alternateIds = getAlternateIds().get(alternateAccessionId);

        return (alternateIds == null ? new HashSet<>() : alternateIds);
    }



    /**
     *
     * @return A complete map of cda db_id, keyed by datasourceShortName
     */
    public Map<String, Integer> getCdaDb_idsByDccDatasourceShortName() {
        Map<String, Integer> map = new ConcurrentHashMap<>();

        List<Datasource> results = jdbcCda.query("SELECT * FROM external_db", new BeanPropertyRowMapper(Datasource.class));
        for (Datasource result : results) {
            map.put(result.getShortName(), result.getId());
        }

        return map;
    }

    /**
     *
     * @return A complete map of cda organisation_id primary keys, keyed by dcc center.centerId
     */
    public Map<String, Integer> getCdaOrganisation_idsByDccCenterId() {
        Map<String, Integer> map = new ConcurrentHashMap<>();

        Map<String, Organisation> organisations = getOrganisations();

        Iterator<Map.Entry<String, String>> entrySetIt = LoadUtils.mappedExternalCenterNames.entrySet().iterator();
        while (entrySetIt.hasNext()) {
            Map.Entry<String, String> entry = entrySetIt.next();
            // key = external (e.g. dcc) name.  value = cda organisation.name.
            String dccName = entry.getKey();
            String cdaName = entry.getValue();

            map.put(dccName, organisations.get(cdaName).getId());
        }

        return map;
    }

    /**
     *
     * @return A complete map of cda project_id primary keys, keyed by dcc center.project
     */
    public Map<String, Integer> getCdaProject_idsByDccProject() {
        Map<String, Integer> map = new ConcurrentHashMap<>();

        Map<String, Project> projects = getProjects();

        Iterator<Map.Entry<String, String>> entrySetIt = LoadUtils.mappedExternalProjectNames.entrySet().iterator();
        while (entrySetIt.hasNext()) {
            Map.Entry<String, String> entry = entrySetIt.next();
            // key = external (e.g. dcc) name.  value = cda project.name.
            String dccName = entry.getKey();
            String cdaName = entry.getValue();

            map.put(dccName, projects.get(cdaName).getId());
        }

        return map;
    }

    /**
     *
     * @return A complete map of cda pipeline primary keys, keyed by dcc center.pipeline
     */
    public Map<String, Integer> getCdaPipeline_idsByDccPipeline() {
        Map<String, Integer> map = new ConcurrentHashMap<>();

        List<Map<String, Object>> results = jdbcCda.queryForList("SELECT id, stable_id FROM phenotype_pipeline", new HashMap<>());

        for (Map<String, Object> result : results) {
            String  stableId = result.get("stable_id").toString();
            Integer id       = Integer.valueOf(result.get("id").toString());
            map.put(stableId, id);
        }

        return map;
    }

    /**
     *
     * @return A complete map of cda procedure primary keys, keyed by dcc procedure_.procedureId
     */
    public Map<String, Integer> getCdaProcedure_idsByDccProcedureId() {
        Map<String, Integer> map = new ConcurrentHashMap<>();

        List<Map<String, Object>> results = jdbcCda.queryForList("SELECT id, stable_id FROM phenotype_procedure", new HashMap<>());

        for (Map<String, Object> result : results) {
            String  stableId = result.get("stable_id").toString();
            Integer id       = Integer.valueOf(result.get("id").toString());
            map.put(stableId, id);
        }

        return map;
    }

    /**
     *
     * @return A complete map of cda parameter primary keys, keyed by dcc procedure_.parameterId
     */
    public Map<String, Integer> getCdaParameter_idsByDccParameterId() {
        Map<String, Integer> map = new ConcurrentHashMap<>();

        List<Map<String, Object>> results = jdbcCda.queryForList("SELECT id, stable_id FROM phenotype_parameter", new HashMap<>());

        for (Map<String, Object> result : results) {
            String  stableId = result.get("stable_id").toString();
            Integer id       = Integer.valueOf(result.get("id").toString());
            map.put(stableId, id);
        }

        return map;
    }

    public Map<String, String> getCdaParameterNames() {
        Map<String, String> map = new ConcurrentHashMap<>();

        List<Map<String, Object>> results = jdbcCda.queryForList("SELECT stable_id, name FROM phenotype_parameter", new HashMap<>());

        for (Map<String, Object> result : results) {
            String  stableId = result.get("stable_id").toString();
            String name      = result.get("name").toString();
            map.put(stableId, name);
        }

        return map;
    }

    
    /**
     * @return the set of all consider accession ids
     */
    public Map<String, Set<ConsiderId>> getConsiderIds() {
        if (considerIds == null) {
            considerIds = new ConcurrentHashMap<>();

            List<ConsiderId> considerIdList = jdbcCda.query("SELECT * FROM consider_id", new ConsiderIdRowMapper());

            for (ConsiderId considerId : considerIdList) {
                if ( ! considerIds.containsKey(considerId.getOntologyTermAccessionId())) {
                    considerIds.put(considerId.getOntologyTermAccessionId(), new HashSet<>());
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
    public Set<ConsiderId> getConsiderIds(String ontologyTermAccessionId) {
        Set<ConsiderId> considerIds = getConsiderIds().get(ontologyTermAccessionId);
        return (considerIds == null ? new HashSet<>() : considerIds);
    }

    public String getDbName() {
        String query = "SELECT DATABASE()";

        String dbname = jdbcCda.queryForObject(query, new HashMap<>(), String.class);

        return dbname;
    }

    /**
     *
     * @param externalDbShortName a name matching the external_db.short_name field
     * @return the db_id matching short_name
     */
    public int getExternalDbId(String externalDbShortName) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("short_name", externalDbShortName);

        return jdbcCda.queryForObject("SELECT id FROM external_db WHERE short_name = :short_name", parameterMap, Integer.class);
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
     * Return the <code>GenomicFeature</code> matching the given {@code geneSymbol}
     *
     * @param geneSymbol the desired gene symbol (exact match)
     *
     * @return {@code GenomicFeature} matching the given {@code geneSymbol}, if
     *         found; null otherwise
     */
    public GenomicFeature getGeneBySymbol(String geneSymbol) {
        GenomicFeature gene = null;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("symbol", geneSymbol);

        List<GenomicFeature> geneList = jdbcCda.query("SELECT * FROM genomic_feature WHERE symbol = :symbol", parameterMap, new GenomicFeatureRowMapper());

        if ( ! geneList.isEmpty()) {
            gene = geneList.get(0);
        }

        return gene;
    }

    /**
     * Return the list of <code>GenomicFeature</code>s
     *
     * @return the list of <code>GenomicFeature</code>s
     */
    public Map<String, GenomicFeature> getGenes() {
        Map<String, GenomicFeature> genes = new ConcurrentHashMap<>();

        logger.info("Loading genes");
        List<GenomicFeature> geneList = jdbcCda.query("SELECT * FROM genomic_feature", new GenomicFeatureRowMapper());

        for (GenomicFeature gene : geneList) {
            genes.put(gene.getId().getAccession(), gene);
        }
        logger.info("Loading genes complete.");

        return genes;
    }

    /**
     * Return the list of <code>GenomicFeature</code>s
     *
     * @return the list of <code>GenomicFeature</code>s, mapped by gene symbol
     */
    public Map<String, GenomicFeature> getGenesBySymbol() {
        Map<String, GenomicFeature> genes = new ConcurrentHashMap<>();

        logger.info("Loading genes mapped by symbol");
        List<GenomicFeature> geneList = jdbcCda.query("SELECT * FROM genomic_feature", new GenomicFeatureRowMapper());

        for (GenomicFeature gene : geneList) {
            genes.put(gene.getSymbol(), gene);
        }
        logger.info("Loading genes by symbol complete.");

        return genes;
    }

    /**
     * Insert {@link BiologicalModelAggregator} doesn't exist, insert it into the database.
     *
     * @param bioModels the {@link BiologicalModelAggregator} instance to be inserted
     *
     * @return the number of {@code bioModel}s inserted
     */
    public Map<String, Integer> insertBiologicalModel(List<BiologicalModelAggregator> bioModels) throws DataLoadException {
        int count = 0;

        Map<String, Integer> countsMap = new HashMap<>();
        countsMap.put("bioModelsInserted", 0);
        countsMap.put("bioModelsUpdated", 0);
        countsMap.put("bioModelAlleles", 0);
        countsMap.put("bioModelGenomicFeatures", 0);
        countsMap.put("bioModelStrains", 0);
        countsMap.put("bioModelPhenotypes", 0);

        final String bioModelInsert = "INSERT INTO biological_model (db_id, allelic_composition, genetic_background, zygosity) " +
                                      "VALUES (:db_id, :allelic_composition, :genetic_background, :zygosity)";

        final String bioModelUpdate = "UPDATE biological_model " +
                                      "SET zygosity = :zygosity WHERE allelic_composition = :allelic_composition AND genetic_background = :genetic_background";

        final String bioModelAlleleInsert = "INSERT INTO biological_model_allele (biological_model_id, allele_acc, allele_db_id) " +
                                            "VALUES (:biological_model_id, :allele_acc, :allele_db_id)";

        final String bioModelGFInsert = "INSERT INTO biological_model_genomic_feature (biological_model_id, gf_acc, gf_db_id) " +
                                        "VALUES (:biological_model_id, :gf_acc, :gf_db_id)";

        final String bioModelStrainInsert = "INSERT INTO biological_model_strain (biological_model_id, strain_acc, strain_db_id) " +
                                        "VALUES (:biological_model_id, :strain_acc, :strain_db_id)";

        final String bioModelPhenotypeInsert = "INSERT INTO biological_model_phenotype (biological_model_id, phenotype_acc, phenotype_db_id) " +
                                               "VALUES (:biological_model_id, :phenotype_acc, :phenotype_db_id)";

        for (BiologicalModelAggregator bioModel : bioModels) {

            // biological_model
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("db_id", DbIdType.MGI.intValue());
            parameterMap.put("allelic_composition", bioModel.getAllelicComposition());
            parameterMap.put("genetic_background", bioModel.getGeneticBackground());
            parameterMap.put("zygosity", bioModel.getZygosity());

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            try {
                count = jdbcCda.update(bioModelInsert, parameterSource, keyholder);
                if (count > 0) {
                    countsMap.put("bioModelsInserted", countsMap.get("bioModelsInserted") + count);
                    bioModel.setBiologicalModelId(keyholder.getKey().intValue());
                }

            } catch (DuplicateKeyException e) {
                count = jdbcCda.update(bioModelUpdate, parameterMap);
                if (count > 0) {
                    countsMap.put("bioModelsUpdated", countsMap.get("bioModelsUpdated") + count);
                } else {
                    throw new DataLoadException("Insert and Update failed for bioModel " + bioModel.toString() + "'. Skipping...");
                }

                return countsMap;
            }

            // biological_model_allele. Controls don't have allele or gene associations so will be null.
            if (bioModel.getAlleleAccessionIds() != null) {
                for (String alleleAccessionId : bioModel.getAlleleAccessionIds()) {
                    try {
                        parameterMap = new HashMap<>();
                        parameterMap.put("biological_model_id", bioModel.getBiologicalModelId());
                        parameterMap.put("allele_acc", alleleAccessionId);
                        parameterMap.put("allele_db_id", DbIdType.MGI.intValue());

                        count = jdbcCda.update(bioModelAlleleInsert, parameterMap);
                        countsMap.put("bioModelAlleles", countsMap.get("bioModelAlleles") + count);

                    } catch (DuplicateKeyException e) {

                        logger.warn("Duplicate biological_model_allele entry: {}. biological model not added.", bioModels);
                        return countsMap;

                    } catch (Exception e) {
                        logger.warn("Skipping bioModel {}: {}", bioModels, e.getLocalizedMessage());
                    }
                }
            }

            // biological_model_genomic_feature. Controls don't have allele or gene associations so will be null.
            if (bioModel.getMarkerAccessionIds() != null) {
                for (String markerAccessionId : bioModel.getMarkerAccessionIds()) {
                    try {
                        parameterMap = new HashMap<>();
                        parameterMap.put("biological_model_id", bioModel.getBiologicalModelId());
                        parameterMap.put("gf_acc", markerAccessionId);
                        parameterMap.put("gf_db_id", DbIdType.MGI.intValue());

                        count = jdbcCda.update(bioModelGFInsert, parameterMap);
                        countsMap.put("bioModelGenomicFeatures", countsMap.get("bioModelGenomicFeatures") + count);

                    } catch (DuplicateKeyException e) {

                        logger.warn("Duplicate biological_model_genomic_feature entry: {}. biological model not added.", bioModels);
                        return countsMap;

                    } catch (Exception e) {

                        logger.warn("Skipping bioModel {}: {}", bioModels, e.getLocalizedMessage());
                    }
                }
            }

            // biological_model_strain
            for (String strainAccessionId : bioModel.getStrainAccessionIds()) {
                try {
                    parameterMap = new HashMap<>();
                    parameterMap.put("biological_model_id", bioModel.getBiologicalModelId());
                    parameterMap.put("strain_acc", strainAccessionId);
                    parameterMap.put("strain_db_id", DbIdType.MGI.intValue());

                    count = jdbcCda.update(bioModelStrainInsert, parameterMap);
                    countsMap.put("bioModelStrains", countsMap.get("bioModelStrains") + count);

                } catch (DuplicateKeyException e) {

                    logger.warn("Duplicate biological_model_strain entry: {}. biological model not added.", bioModels);
                    return countsMap;

                } catch (Exception e) {

                    logger.warn("Skipping bioModel {}: {}", bioModels, e.getLocalizedMessage());
                }
            }

            // biological_model_phenotype
            for (String phenotypeAccessionId : bioModel.getMpAccessionIds()) {
                try {
                    parameterMap = new HashMap<>();
                    parameterMap.put("biological_model_id", bioModel.getBiologicalModelId());
                    parameterMap.put("phenotype_acc", phenotypeAccessionId);
                    parameterMap.put("phenotype_db_id", DbIdType.MGI.intValue());

                    count = jdbcCda.update(bioModelPhenotypeInsert, parameterMap);
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
     * Insert biological_model_sample record. Ignore duplicates.
     * @param biologicalModelId
     * @param biologicalSampleId
     * @return the nuber of rows inserted
     * @throws DataLoadException
     */
    public int insertBiologicalModelSample(int biologicalModelId, int biologicalSampleId) throws DataLoadException {
        int count = 0;

        final String insert = "INSERT INTO biological_model_sample (biological_model_id, biological_sample_id) " +
                                   "VALUES (:biological_model_id, :biological_sample_id)";

        // Insert biological_model_sample. Ignore any duplicates.
        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("biological_model_id", biologicalModelId);
            parameterMap.put("biological_sample_id", biologicalSampleId);

            count = jdbcCda.update(insert, parameterMap);

        } catch (DuplicateKeyException e) {

        }

        return count;
    }

    /**
     * Insert biological_sample record. Ignore duplicates.
     * @param externalId
     * @param dbId
     * @param sampleType
     * @param sampleGroup
     * @param phenotypingCenterId
     * @param productionCenterId (May be null)
     * @return a map with the number of rows inserted ("count") and the biologicalSampleId ("biologicalSampleId")
     * @throws DataLoadException
     */
    public Map<String, Integer> insertBiologicalSample(String externalId, int dbId, OntologyTerm sampleType, String sampleGroup, int phenotypingCenterId, Integer productionCenterId) throws DataLoadException {
        Map<String, Integer> results = new HashMap<>();

        final String insert = "INSERT INTO biological_sample (external_id, db_id, sample_type_acc, sample_type_db_id, sample_group, organisation_id, production_center_id) " +
                                   "VALUES (:external_id, :db_id, :sample_type_acc, :sample_type_db_id, :sample_group, :organisation_id, :production_center_id)";

        // Insert biological sample. Ignore any duplicates.
        int count = 0;
        int id = 0;
        Map<String, Object> parameterMap = new HashMap<>();
        try {
            parameterMap.put("external_id", externalId);
            parameterMap.put("db_id", dbId);
            parameterMap.put("sample_type_acc", sampleType.getId().getAccession());
            parameterMap.put("sample_type_db_id", sampleType.getId().getDatabaseId());
            parameterMap.put("sample_group", sampleGroup);
            parameterMap.put("organisation_id", phenotypingCenterId);
            parameterMap.put("production_center_id", productionCenterId);
            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            count = jdbcCda.update(insert, parameterSource, keyholder);
            if (count > 0) {
                id = keyholder.getKey().intValue();
            }

        } catch (DuplicateKeyException e) {
            id = jdbcCda.queryForObject("SELECT id FROM biological_sample WHERE external_id = :external_id AND organisation_id = :organisation_id", parameterMap, Integer.class);
        }

        results.put("count", count);
        results.put("biologicalSampleId", id);

        return results;
    }

    // Returns the newly-inserted primary key if successful; 0 otherwise.
    public int insertExperiment(
            int db_id,
            String external_id,
            String sequence_id,
            Date date_of_experiment,
            int organisation_id,
            int project_id,
            int pipeline_id,
            String pipeline_stable_id,
            int procedure_id,
            String procedure_stable_id,
            String colony_id,
            String procedure_status,
            String procedure_status_message,
            Integer biological_model_id,
            String metadataCombined,
            String metadataGroup
    ) throws DataLoadException {

        int pk = 0;

        final String insert = "INSERT INTO experiment (" +
                "db_id, external_id, sequence_id, date_of_experiment, organisation_id, project_id," +
                " pipeline_id, pipeline_stable_id, procedure_id, procedure_stable_id, biological_model_id," +
                " colony_id, metadata_combined, metadata_group, procedure_status, procedure_status_message) " +
                "VALUES (:db_id, :external_id, :sequence_id, :date_of_experiment, :organisation_id, :project_id," +
                " :pipeline_id, :pipeline_stable_id, :procedure_id, :procedure_stable_id, :biological_model_id," +
                " :colony_id, :metadata_combined, :metadata_group, :procedure_status, :procedure_status_message)";

        // Insert experiment. Ignore any duplicates.
        Map<String, Object> parameterMap = new HashMap<>();
        try {
            parameterMap.put("db_id", db_id);
            parameterMap.put("external_id", external_id);
            parameterMap.put("sequence_id", sequence_id);
            parameterMap.put("date_of_experiment", date_of_experiment);
            parameterMap.put("external_id", external_id);
            parameterMap.put("organisation_id", organisation_id);
            parameterMap.put("project_id", project_id);
            parameterMap.put("pipeline_id", pipeline_id);
            parameterMap.put("pipeline_stable_id", pipeline_stable_id);
            parameterMap.put("procedure_id", procedure_id);
            parameterMap.put("procedure_stable_id", procedure_stable_id);
            parameterMap.put("biological_model_id", biological_model_id);
            parameterMap.put("colony_id", colony_id);
            parameterMap.put("metadata_combined", metadataCombined);
            parameterMap.put("metadata_group", metadataGroup);
            parameterMap.put("procedure_status", procedure_status);
            parameterMap.put("procedure_status_message", procedure_status_message);

            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            int                count           = jdbcCda.update(insert, parameterSource, keyholder);
            if (count == 1) {
                pk = keyholder.getKey().intValue();
            }

        } catch (DuplicateKeyException e) {

        }

        return pk;
    }

    public void insertExperiment_observation(int experimentPk, int observationPk) throws DataLoadException {

        final String insert = "INSERT INTO experiment_observation (" +
                "experiment_id, observation_id) " +
                "VALUES (:experimentPk, :observationPk)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("experimentPk", experimentPk);
        parameterMap.put("observationPk", observationPk);

        KeyHolder          keyholder       = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        jdbcCda.update(insert, parameterSource, keyholder);
    }



    /**
     * Insert live_sample record. Ignore duplicates.
     *
     * @param biologicalSampleId
     * @param colonyId
     * @param dateOfBirth
     * @param developmentalStage
     * @param litterId
     * @param sex
     * @param zygosity
     * @return the number of rows inserted
     * @throws DataLoadException
     */
    public int insertLiveSample(int biologicalSampleId, String colonyId, Date dateOfBirth, OntologyTerm developmentalStage, String litterId, String sex, String zygosity) throws DataLoadException {
        int count = 0;

        final String insert = "INSERT INTO live_sample (id, colony_id, date_of_birth, developmental_stage_acc, developmental_stage_db_id, litter_id, sex, zygosity) " +
                                   "VALUES (:id, :colony_id, :date_of_birth, :developmental_stage_acc, :developmental_stage_db_id, :litter_id, :sex, :zygosity)";

        // Insert live sample. Ignore any duplicates.
        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("id", biologicalSampleId);
            parameterMap.put("colony_id", (colonyId == null ? "unknown" : colonyId));
            parameterMap.put("date_of_birth", dateOfBirth);
            parameterMap.put("developmental_stage_acc", developmentalStage.getId().getAccession());
            parameterMap.put("developmental_stage_db_id", developmentalStage.getId().getDatabaseId());
            parameterMap.put("litter_id", litterId);
            parameterMap.put("sex", sex);
            parameterMap.put("zygosity", zygosity);

            count = jdbcCda.update(insert, parameterMap);

        } catch (DuplicateKeyException e) {

        }

        return count;
    }

    /**
     * Try to insert the gene. If it succeeds, insert any synonyms and xrefs.
     *
     * @param gene the {@link GenomicFeature} to be inserted
     *
     * @return a map, keyed by type (genes, synonyms, xrefs) of the number of {@code gene} components inserted
     */
    public Map<String, Integer> insertGene(GenomicFeature gene) throws DataLoadException {
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
    public Map<String, Integer> insertGenes(List<GenomicFeature> genes) throws DataLoadException {
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

                count = jdbcCda.update(gfInsert, parameterMap);
                countsMap.put("genes", countsMap.get("genes") + count);

            } catch (DuplicateKeyException dke) {
//                logger.info("Duplicate genomic_feature entry. Accession id: " + gene.getId().getAccession() + ". GenomicFeature: " + gene.getName() + ". GenomicFeature not added.");
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

                        count = jdbcCda.update(synoymInsert, parameterMap);
                        countsMap.put("synonyms", countsMap.get("synonyms") + count);

                        if (count > 0) {
                            updateSynonymMap(synonym.getAccessionId(), synonym);
                        }

                    } catch (DuplicateKeyException dke) {

                    }
                }
            }


            // Insert xrefs. Ignore any duplicates.
            if (gene.getXrefs() != null) {
                for (Xref xref : gene.getXrefs()) {
                    try {
                        Map<String, Object> parameterMap = new HashMap<>();
                        parameterMap.put("acc", gene.getId().getAccession());
                        parameterMap.put("db_id", gene.getId().getDatabaseId());
                        parameterMap.put("xref_acc", xref.getXrefAccession());
                        parameterMap.put("xref_db_id", xref.getXrefDatabaseId());

                        count = jdbcCda.update(xrefInsert, parameterMap);
                        countsMap.put("xrefs", countsMap.get("xrefs") + count);

                    } catch (DuplicateKeyException dke) {

                    } catch (Exception e) {
                        throw new DataLoadException(e + "\n\txref: " + xref.toString());
                    }
                }
            }
        }

        return countsMap;
    }


    public Map<String, OntologyTerm> getOntologyTerms() {
        if (ontologyTerms == null) {
            ontologyTerms = new ConcurrentHashMap();

            List<OntologyTerm> termList = jdbcCda.query("SELECT * FROM ontology_term", new OntologyTermRowMapper());

            for (OntologyTerm term : termList) {
                ontologyTerms.put(term.getId().getAccession(), term);
            }
        }

        return ontologyTerms;
    }

    /**
     * This method generically replaces any obsolete/missing ontology terms, identified by {@code ontologyAccessionIds},
     * walking {@code ontologyAccessionIds}, calling {@code }getLatestOntologyTerm()} to replace any obsolete/missing
     * ontology terms.
     *
     * @param ontologyAccessionIds
     * @param jdbc a {@link NamedParameterJdbcTemplate} instance pointing to the database to be updated
     * @param ontologyAccessionIds the list of ontology accession ids to be checked and, if obsolete or missing, replaced
     * @param tableName the name of the table to be updated
     * @param ontologyAccColumnName the name of the ontology accesion id column whose value will be replaced if obsolete
     *                              or missing
     *
     * @return a {@link Set<OntologyTermAnomaly>} a list of the anomalies
     */
    public Set<OntologyTermAnomaly> checkAndUpdateOntologyTerms(NamedParameterJdbcTemplate jdbc, List<String> ontologyAccessionIds, String tableName, String ontologyAccColumnName) {

        String dbName = sqlUtils.getDatabaseName(jdbc);
        String update = "UPDATE " + tableName + "\n" +
                        "SET " + ontologyAccColumnName + " = :newOntologyAcc WHERE " + ontologyAccColumnName + " = :originalOntologyAcc;";

        Set<OntologyTermAnomaly> anomalies = new HashSet<>();
        for (String originalAcc : ontologyAccessionIds) {
            OntologyTerm originalTerm = getOntologyTerm(originalAcc);
            if ((originalTerm != null) && ( ! originalTerm.getIsObsolete())) {
                continue;
            }

            RunStatus status = new RunStatus();
            OntologyTerm replacementOntologyTerm = getLatestOntologyTerm(originalAcc, status);
            String replacementAcc = null;

            if (replacementOntologyTerm != null) {
                replacementAcc = replacementOntologyTerm.getId().getAccession();
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("originalOntologyAcc", originalAcc);
                parameterMap.put("newOntologyAcc", replacementOntologyTerm.getId().getAccession());
                jdbc.update(update, parameterMap);
            }

            // Log the anomalies
            for (String reason : status.getErrorMessages()) {
                anomalies.add(new OntologyTermAnomaly(dbName, tableName, ontologyAccColumnName, originalAcc, replacementAcc, reason));
            }
            for (String reason : status.getWarningMessages()) {
                anomalies.add(new OntologyTermAnomaly(dbName, tableName, ontologyAccColumnName, originalAcc, replacementAcc, reason));
            }
        }

        for (OntologyTermAnomaly anomaly : anomalies) {

            // Log the anomalies
            anomaly.setDbName(dbName);
            anomaly.setTableName(tableName);
            anomaly.setOntologyAccColumnName(ontologyAccColumnName);

            insertOntologyTermAnomaly(anomaly);

        }

        return anomalies;
    }


    /**
     * Returns the observation type based on the parameterId and a sample
     * parameter value.
     *
     * @param parameterId The parameter Id
     * @param value     a string representing parameter sample data (e.g. a floating
     *                  point number or anything else).
     * @return The observation type based on the parameter and a sample
     * parameter value. If <code>value</code> is a floating point number and
     * <code>parameter</code> does not have a valid data type,
     * <code>value</code> is used to disambiguate the graph type: the
     * observation type will be either <i>time_series</i> or
     * <i>unidimensional</i>; otherwise, it will be interpreted as
     * <i>categorical</i>.
     */
    public ObservationType computeObservationType(String parameterId, String value) {

        Parameter parameter = getParameterByStableId(parameterId);

        Map<String, String> MAPPING = new HashMap<>();
        MAPPING.put("M-G-P_022_001_001_001", "FLOAT");
        MAPPING.put("M-G-P_022_001_001", "FLOAT");
        MAPPING.put("ESLIM_006_001_035", "FLOAT");
        MAPPING = Collections.unmodifiableMap(MAPPING);

        ObservationType observationType = null;

        Float valueToInsert = 0.0f;

        String datatype = parameter.getDatatype();
        if (MAPPING.containsKey(parameter.getStableId())) {
            datatype = MAPPING.get(parameter.getStableId());
        }

        if (parameter.isMetaDataFlag()) {

            observationType = ObservationType.metadata;

        } else {

            if (parameter.isOptionsFlag()) {

                observationType = ObservationType.categorical;

            } else {

                if (datatype.equals("TEXT")) {

                    observationType = ObservationType.text;

                } else if (datatype.equals("DATETIME")) {

                    observationType = ObservationType.datetime;

                } else if (datatype.equals("BOOL")) {

                    observationType = ObservationType.categorical;

                } else if (datatype.equals("FLOAT") || datatype.equals("INT")) {

                    if (parameter.isIncrementFlag()) {

                        observationType = ObservationType.time_series;

                    } else {

                        observationType = ObservationType.unidimensional;

                    }

                    try {
                        if (value != null) {
                            valueToInsert = Float.valueOf(value);
                        }
                    } catch (NumberFormatException ex) {
                        logger.debug("Invalid float value: " + value);
                    }

                } else if (datatype.equals("IMAGE") || (datatype.equals("") && parameter.getName().contains("images"))) {

                    observationType = ObservationType.image_record;

                } else if (datatype.equals("") && !parameter.isOptionsFlag() && !parameter.getName().contains("images")) {

                    // is that a number or a category?
                    try {
                        // check whether it's null
                        if (value != null && !value.equals("null") && !value.trim().isEmpty()) {
                            valueToInsert = Float.valueOf(value);
                        }
                        if (parameter.isIncrementFlag()) {
                            observationType = ObservationType.time_series;
                        } else {
                            observationType = ObservationType.unidimensional;
                        }

                    } catch (NumberFormatException ex) {
                        observationType = ObservationType.categorical;
                    }
                }
            }
        }

        return observationType;
    }

    public String[] computeParameterUnits(String parameterId) {

        Parameter parameter = getParameterByStableId(parameterId);
        String[] units = null;

        if (parameter.isIncrementFlag()) {
            units = new String[2];
            Iterator i$ = parameter.getIncrement().iterator();

            label22: {
                ParameterIncrement increment;
                do {
                    if( ! i$.hasNext()) {
                        break label22;
                    }

                    increment = (ParameterIncrement)i$.next();
                } while(increment.getValue().length() <= 0 && parameter.getIncrement().size() != 1);

                units[0] = increment.getUnit();
            }

            units[1] = parameter.getUnit();
        } else {
            units = new String[] { parameter.getUnit() };
        }

        return units;
    }

    private Map<String, Parameter> parametersByStableIdMap;
    public Parameter getParameterByStableId(String stableId) {

        if (parametersByStableIdMap == null) {
            String query = "SELECT * FROM phenotype_parameter";
            parametersByStableIdMap = new HashMap<>();
            List<Parameter> parameters = jdbcCda.query(query, new HashMap<>(), new ParameterRowMapper());
            for (Parameter parameter : parameters) {
                parametersByStableIdMap.put(parameter.getStableId(), parameter);
            }
        }

        return parametersByStableIdMap.get(stableId);
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

    /**
     * This method checks the original ontology tern accession id against the database of loaded terms and adjusts it
     * if necessary according to the following rules:
     * <pre>
     *     Look up the term in the ontology_term table using the original accession id. If the term exists
     *          if it is marked obsolete
     *              if there is a replacement_acc, return it instead
     *              else
     *                  if there is a consider id term, return it instead
     *                  else this term is obsolete and there is no replacement/alternative. Return null.
     *          else
     *              return the original ontology term
     *      else
     *          if there is an alternate id, use it instead
     *          else
     *              this is an unknown ontolgy term accession id. Return null.
     * </pre>
     * @param originalAcc the original ontology term accession id
     * @param status the status of the call. Successfully replaced terms are described by status warnings. Terms that
     *               failed to be replaced are described by status errors. Terms that are found and are not obsolete
     *               are not added to the status object.
     * @return the ontology term, if found; null otherwise
     */
    public OntologyTerm getLatestOntologyTerm(String originalAcc, RunStatus status) {

        OntologyTerm term;

        Map<String, OntologyTerm> terms = getOntologyTerms();
        term = terms.get(originalAcc);
        if (term != null) {
            term.setConsiderIds(getConsiderIds(term.getId().getAccession()));
            if (term.getIsObsolete()) {
                if (term.getReplacementAcc() != null) {
                    String replacementAcc = term.getReplacementAcc();
                    term = terms.get(term.getReplacementAcc());
                    if ((term == null) || (term.getIsObsolete())) {
                        status.addError("Term " + originalAcc + " has invalid replacement term " + replacementAcc + ".");
                        return null;
                    }

                    status.addWarning("Term " + originalAcc + " is obsolete and was replaced by replacement id " + replacementAcc + ".");
                    return term;

                } else if ((term.getConsiderIds() != null) && (!term.getConsiderIds().isEmpty())) {
                    if (term.getConsiderIds().size() > 1) {
                        status.addError("Term " + originalAcc + " is obsolete and has multiple consider ids.");
                        return null;
                    }

                    String considerAcc = term.getConsiderIds().iterator().next().getConsiderAccessionId();
                    term = terms.get(considerAcc);
                    if ((term == null) || (term.getIsObsolete())) {
                        status.addError("Term " + originalAcc + " is obsolete and has invalid consider id " + considerAcc + ".");
                        return null;
                    }

                    status.addWarning("Term " + originalAcc + " is obsolete and was replaced by consider id " + considerAcc + ".");
                    return term;

                } else {
                    status.addError("Term " + originalAcc + " is obsolete and has no replacement/consider id term.");
                    return null;
                }
            } else {

                return term;
            }
        } else {
            // Load any alternative ids. If there are none, an empty set is returned.
            Set<AlternateId> alternateIds = getAlternateIds(originalAcc);
            if ( ! alternateIds.isEmpty()) {
                if (alternateIds.size() > 1) {
                    status.addError("Term " + originalAcc + " is missing and has multiple alternate ids.");
                    return null;
                }

                String ontologyAcc = alternateIds.iterator().next().getOntologyTermAccessionId();
                term = terms.get(ontologyAcc);
                if ((term == null) || (term.getIsObsolete())) {
                    status.addError("Term " + originalAcc + " is missing and has invalid alternate id " + ontologyAcc + ".");
                    return null;
                }

                status.addWarning("Term " + originalAcc + " is missing but is an alternate id for " + ontologyAcc + ".");
                return term;
            }
        }

        return term;
    }

    /**
     * @return the contents of the ontology_term_anomaly table, or an empty list.
     */
    public Set<OntologyTermAnomaly> getOntologyTermAnomalies() {
        List<OntologyTermAnomaly> anomalyList = jdbcCda.query("SELECT * FROM ontology_term_anomaly", new OntologyTermAnomalyRowMapper());

        Set<OntologyTermAnomaly> anomalySet = new HashSet<>();
        anomalySet.addAll(anomalyList);

        return anomalySet;
    }

    public int insertOntologyTermAnomaly(OntologyTermAnomaly anomaly) {
        Date now = new Date();

        final String ontologyTermInsert = "INSERT INTO ontology_term_anomaly (db_name, table_name, column_name, original_acc, replacement_acc, reason, last_modified) " +
                                          "VALUES (:db_name, :table_name, :column_name, :original_acc, :replacement_acc, :reason, :last_modified)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("db_name", anomaly.getDbName());
        parameterMap.put("table_name", anomaly.getTableName());
        parameterMap.put("column_name", anomaly.getOntologyAccColumnName());
        parameterMap.put("original_acc", anomaly.getOriginalAcc());
        parameterMap.put("replacement_acc", anomaly.getReplacementAcc());
        parameterMap.put("reason", anomaly.getReason());
        parameterMap.put("last_modified", now);

        KeyHolder keyholder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        int count = jdbcCda.update(ontologyTermInsert, parameterSource, keyholder);
        anomaly.setId(keyholder.getKey().intValue());
        anomaly.setLast_modified(now);

        return count;
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
    *         found; null if not found. If more than one result is found, DataLoadException is thrown.
    *
    * @throws DataLoadException
    */
    public OntologyTerm getMappedBiotype(int dbId, String term) throws DataLoadException {
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
     *         found; null if not found. If more than one result is found, DataLoadException is thrown.
     *
     * @throws DataLoadException if more than one term would be returned
     */

    public OntologyTerm getOntologyTerm(int dbId, String term) throws DataLoadException {
        List<OntologyTerm> terms = new ArrayList<>();

        List<OntologyTerm> ontologyTerms = new ArrayList<>(getOntologyTerms(dbId).values());
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            if (ontologyTerm.getName().equalsIgnoreCase(term)) {
                terms.add(ontologyTerm);
            }
        }

        if (terms.isEmpty()) {
            return null;
        } else if (terms.size() == 1) {
            return terms.get(0);
        } else {
            throw new DataLoadException("There is more than one ontology term for the given dbId '" + dbId + "' for term name '" + term + "': " + StringUtils.join(terms, ", "));
        }
    }

    /**
     * Returns the ontology term matching {@code name}, if found, null otherwise
     * @param name ontology term name
     *
     * @return the ontology term matching {@code name}, if found, null otherwise
     *
     * @throws DataLoadException if more than one term was found. Use {@code getOntologyTermsByName()} for a list.
     */
    public OntologyTerm getOntologyTermByName(String name) throws DataLoadException {
        List<OntologyTerm> terms = getOntologyTermsByName(name);

        if (terms.size() > 1) {
            throw new DataLoadException(terms.size() + " terms were found for ontology term name '" + name + "'.");
        }

        return (terms.isEmpty() ? null : terms.get(0));
    }

    public List<OntologyTerm> getOntologyTermsByName(String name) {
        String query = "SELECT * FROM ontology_term WHERE name = :name";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("name", name);

        List<OntologyTerm> terms = jdbcCda.query(query, parameterMap, new OntologyTermRowMapper());

        return terms;
    }

private Map<Integer, Map<String, OntologyTerm>> ontologyTermMaps = new ConcurrentHashMap<>();       // keyed by dbId
    /**
     * Return a CASE-INSENSITIVE {@link TreeMap} of <code>OntologyTerm</code>s matching the given {@code dbId}, indexed by dbId
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
                    term.setAlternateIds(getAlternateIds(term.getId().getAccession()));
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

        List<Organisation> organisationList = jdbcCda.query("SELECT * FROM organisation", new OrganisationRowMapper());

        for (Organisation organisation : organisationList) {
            organisations.put(organisation.getName(), organisation);
        }

        return organisations;
    }

    /**
     * @return the full list of {@link }PhenotypedColony}, indexed by colonyId
     */
    public Map<String, PhenotypedColony> getPhenotypedColonies() {

        Map<String, PhenotypedColony> list = new HashMap<>();
        String query = "SELECT\n" +
                "  pc.*,\n" +
                "  gf.*\n" +
                "FROM phenotyped_colony pc\n" +
                "JOIN genomic_feature gf ON gf.db_id = gf_db_id AND gf.acc = pc.gf_acc";

        List<PhenotypedColony> phenotypedColonies = jdbcCda.query(query, new HashMap<>(), new PhenotypedColonyRowMapper());
        for (PhenotypedColony phenotypedColony : phenotypedColonies) {
            list.put(phenotypedColony.getColonyName(), phenotypedColony);
        }

        return list;
    }

    public Map<String, Project> getProjects() {
        Map<String, Project> projects = new ConcurrentHashMap<>();

        List<Project> projectList = jdbcCda.query("SELECT * FROM project", new ProjectRowMapper());

        for (Project project : projectList) {
            projects.put(project.getName(), project);
        }

        return projects;
    }

    // SimpleParameter version. Returns the newly-inserted primary key if successful; 0 otherwise.
    public int insertObservation(
            int dbId,
            Integer biologicalSamplePk,
            String parameterStableId,
            int parameterId,
            String sequenceId,
            int populationId,
            ObservationType observationType,
            int missing,
            String parameterStatus,
            String parameterStatusMessage,
            SimpleParameter simpleParameter
    ) throws DataLoadException {

        String detailInsert = null;
        String rawValue = simpleParameter.getValue();
        int observationPk = 0;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("dbId", dbId);
        parameterMap.put("biologicalSampleId", biologicalSamplePk);
        parameterMap.put("observationType", observationType.name());
        parameterMap.put("parameterId", parameterId);
        parameterMap.put("parameterStableId", parameterStableId);
        parameterMap.put("sequenceId", sequenceId);
        parameterMap.put("populationId", populationId);
        parameterMap.put("missing", missing);
        parameterMap.put("parameterStatus", parameterStatus);
        parameterMap.put("parameterStatusMessage", parameterStatusMessage);

        // Do observationType validation and build the observation detail parameter map.
        Map<String, Object> detailParameterMap = new HashMap<>();
        switch (observationType) {
            case metadata:
                // Do not load metadata parameters like this
                // See the loadProcedureMetaData(...) method
                break;

            case datetime:
                // Validate the date. If it is missing or it fails validation, mark the observation as missing.
                try {
                    if (rawValue == null) {
                        missing = 1;
                    }

                    Date date = javax.xml.bind.DatatypeConverter.parseDateTime(rawValue).getTime();
                    detailParameterMap.put("date", date);

                } catch (Exception e) {
                    missing = 1;
                }

                detailInsert = "INSERT INTO datetime_observation (id, datetime_point) VALUES (:observationPk, :date)";
                break;

            case text:
                // Validate the text. If it is missing, mark the observation as missing.
                if (rawValue == null) {
                    missing = 1;
                }

                detailParameterMap.put("text", rawValue);

                detailInsert = "INSERT INTO text_observation (id, text) VALUES (:observationPk, :text)";
                break;

            case categorical:
                // Validate the category. If it is missing, mark the observation as missing.
                try {
                    if (rawValue == null) {
                        missing = 1;
                    }

                    detailParameterMap.put("category", rawValue);

                } catch (Exception e) {
                    missing = 1;
                }

                detailInsert = "INSERT INTO categorical_observation (id, category) VALUES (:observationPk, :category)";
                break;

            case unidimensional:
                // Validate the data point. If it is missing or it fails validation, mark the observation as missing.
                try {
                    if (rawValue == null) {
                        missing = 1;
                    }

                    Float dataPoint = Float.parseFloat(rawValue);
                    detailParameterMap.put("dataPoint", dataPoint);

                } catch (Exception e) {
                    missing = 1;
                }

                detailInsert = "INSERT INTO unidimensional_observation (id, data_point) VALUES (:observationPk, :dataPoint)";
                break;

            default:
                throw new DataLoadException("Unknown observationType '" + observationType.toString() + "'");
        }

        parameterMap.put("missing", missing);

        KeyHolder          keyholder       = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
        int count = jdbcCda.update(OBSERVATION_INSERT, parameterSource, keyholder);
        if (count > 0) {
            observationPk = keyholder.getKey().intValue();
            detailParameterMap.put("observationPk", observationPk);
        } else {
            logger.warn("Insert to observation table failed for parameterSource {}", parameterSource);
            return 0;
        }

        if ((missing == 0) && (detailInsert != null)) {
            count = jdbcCda.update(detailInsert, detailParameterMap);
            if (count == 0) {
                logger.warn("Insert failed for parameterSource {}. Marking it as missing ...", parameterSource);
                updateObservationMissingFlag(observationPk, true);
            }
        }

        return observationPk;
    }


    // MediaSampleParameter version. Returns the newly-inserted primary key if successful; 0 otherwise.
    public int insertObservation(
            int dbId,
            Integer biologicalSamplePk,
            String parameterStableId,
            int parameterPk,
            String sequenceId,
            int populationId,
            ObservationType observationType,
            int missing,
            String parameterStatus,
            String parameterStatusMessage,
            MediaSampleParameter mediaSampleParameter,
            MediaFile mediaFile,
            DccExperimentDTO dccExperimentDTO,
            int samplePk,
            int organisationPk,
            int experimentPk,
            List<SimpleParameter> simpleParameterList,
            List<OntologyParameter> ontologyParameterList
    ) throws DataLoadException {

        String URI = mediaFile.getURI();
        KeyHolder keyholder     = new GeneratedKeyHolder();
        int       observationPk;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("dbId", dbId);
        parameterMap.put("biologicalSampleId", biologicalSamplePk);
        parameterMap.put("observationType", observationType.name());
        parameterMap.put("parameterId", parameterPk);
        parameterMap.put("parameterStableId", parameterStableId);
        parameterMap.put("sequenceId", sequenceId);
        parameterMap.put("populationId", populationId);
        parameterMap.put("missing", missing);
        parameterMap.put("parameterStatus", parameterStatus);
        parameterMap.put("parameterStatusMessage", parameterStatusMessage);

        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        int count = jdbcCda.update(OBSERVATION_INSERT, parameterSource, keyholder);
        if (count > 0) {
            observationPk = keyholder.getKey().intValue();
        } else {
            logger.warn("Insert MediaSampleParameter to observation table failed for parameterSource {}", parameterSource);
            return 0;
        }

        if (missing == 0) {
            final String insert =
                    "INSERT INTO image_record_observation (" +
                            "id, sample_id, download_file_path, image_link, increment_value, file_type, media_sample_local_id, media_section_id, organisation_id, full_resolution_file_path" +
                            "" +
                            ") VALUES (" +
                            ":observationPk, :samplePk, :downloadFilePath, :imageLink, :incrementValue, :fileType, :mediaSampleLocalId, :mediaSectionId, :organisationPk, :fullResolutionFilePath" +
                            ")";

            String filePathWithoutName = createNfsPathWithoutName(dccExperimentDTO, parameterStableId);
            String fullResolutionFilePath = getFullResolutionFilePath(filePathWithoutName, URI);

            parameterMap.clear();
            parameterMap.put("observationPk", observationPk);
            parameterMap.put("samplePk", samplePk);
            parameterMap.put("downloadFilePath", URI.toLowerCase());
            parameterMap.put("imageLink", mediaFile.getLink());
            parameterMap.put("incrementValue", null);
            parameterMap.put("fileType", mediaFile.getFileType());
            parameterMap.put("mediaSampleLocalId", null);
            parameterMap.put("mediaSectionId", null);
            parameterMap.put("organisationPk", organisationPk);
            parameterMap.put("fullResolutionPath", fullResolutionFilePath);

            try {
                count = jdbcCda.update(insert, parameterMap);
            } catch (Exception e) {
                logger.error("INSERT to image_record_observation table for MediaSampleParameter failed for parameterStableId {}, observationType {}, observationPk {}, samplePk {}, downloadFilePath {}, imageLink {}, fileType {}, organisationPk {}, fullResolutionPath {}. Reason:\n\t{}",
                             parameterStableId, observationType.toString(), observationPk, samplePk, URI, mediaFile.getLink(), mediaFile.getFileType(), organisationPk, fullResolutionFilePath, e.getLocalizedMessage());
            }
            if (count == 0) {
                logger.warn("Insert MediaSampleParameter failed for parameterSource {}. Marking it as missing ...", parameterSource);
                updateObservationMissingFlag(observationPk, true);
            } else {
                // Save any parameter associations.
                for (ParameterAssociation parameterAssociation : mediaFile.getParameterAssociation()) {
                    int parameterAssociationPk = insertParameterAssociation(observationPk, parameterAssociation, simpleParameterList, ontologyParameterList);

                    // Save any Dimensions.
                    for (Dimension dimension : parameterAssociation.getDim()) {
                        insertDimension(parameterAssociationPk, dimension);
                    }
                }

                // Save any procedure metadata.
                for (ProcedureMetadata procedureMetadata : mediaFile.getProcedureMetadata()) {
                    insertProcedureMetadata(mediaFile.getProcedureMetadata(), dccExperimentDTO.getProcedureId(),
                                            experimentPk, observationPk);
                }
            }
        } else {
            logger.debug("Image record not loaded (missing = 1). parameterStableId {}, URI {}" + parameterStableId,  URI);
        }

        return observationPk;
    }


    // MediaParameter version. Returns the newly-inserted primary key if successful; 0 otherwise.
    public int insertObservation(
            int dbId,
            Integer biologicalSamplePk,
            String parameterStableId,
            int parameterPk,
            String sequenceId,
            int populationId,
            ObservationType observationType,
            int missing,
            String parameterStatus,
            String parameterStatusMessage,
            MediaParameter mediaParameter,
            DccExperimentDTO dccExperimentDTO,
            int samplePk,
            int organisationPk
    ) throws DataLoadException {

        KeyHolder keyholder     = new GeneratedKeyHolder();
        int       observationPk;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("dbId", dbId);
        parameterMap.put("biologicalSampleId", biologicalSamplePk);
        parameterMap.put("observationType", observationType.name());
        parameterMap.put("parameterId", parameterPk);
        parameterMap.put("parameterStableId", parameterStableId);
        parameterMap.put("sequenceId", sequenceId);
        parameterMap.put("populationId", populationId);
        parameterMap.put("missing", missing);
        parameterMap.put("parameterStatus", parameterStatus);
        parameterMap.put("parameterStatusMessage", parameterStatusMessage);

        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        int count = jdbcCda.update(OBSERVATION_INSERT, parameterSource, keyholder);
        if (count > 0) {
            observationPk = keyholder.getKey().intValue();
        } else {
            logger.warn("Insert MediaParameter to observation table failed for parameterSource {}", parameterSource);
            return 0;
        }

        if (missing == 0) {
            final String insert =
                    "INSERT INTO image_record_observation (" +
                            "id, sample_id, download_file_path, image_link, increment_value, file_type, media_sample_local_id, media_section_id, organisation_id, full_resolution_file_path" +
                            "" +
                            ") VALUES (" +
                            ":observationPk, :samplePk, :downloadFilePath, :imageLink, :incrementValue, :fileType, :mediaSampleLocalId, :mediaSectionId, :organisationPk, :fullResolutionFilePath" +
                            ")";

            String filePathWithoutName    = createNfsPathWithoutName(dccExperimentDTO, parameterStableId);
            String fullResolutionFilePath = getFullResolutionFilePath(filePathWithoutName, mediaParameter.getURI());

            parameterMap.clear();
            parameterMap.put("observationPk", observationPk);
            parameterMap.put("samplePk", samplePk);
            parameterMap.put("downloadFilePath", mediaParameter.getURI().toLowerCase());
            parameterMap.put("imageLink", mediaParameter.getLink());
            parameterMap.put("incrementValue", null);
            parameterMap.put("fileType", mediaParameter.getFileType());
            parameterMap.put("mediaSampleLocalId", null);
            parameterMap.put("mediaSectionId", null);
            parameterMap.put("organisationPk", organisationPk);
            parameterMap.put("fullResolutionPath", fullResolutionFilePath);

            try {
                count = jdbcCda.update(insert, parameterMap);
            } catch (Exception e) {
                logger.error("INSERT to image_record_observation table for MediaParameter failed for parameterStableId {}, observationType {}, observationPk {}, imnageLink {}, fileType {}, organisationPk {}, fullResolutionPath {}. Reason:\n\t{}",
                             parameterStableId, observationType.toString(), observationPk, mediaParameter.getLink(), mediaParameter.getFileType(), organisationPk, fullResolutionFilePath, e.getLocalizedMessage());
            }
            if (count == 0) {
                logger.warn("Insert MediaParameter failed for parameterSource {}. Marking it as missing ...", parameterSource);
                updateObservationMissingFlag(observationPk, true);
            }
        } else {
            logger.debug("Image record not loaded (missing = 1). parameterStableId {} " + parameterStableId);
        }

        return observationPk;
    }


    // OntologyParameter version. Returns the newly-inserted primary key if successful; 0 otherwise.
    public int insertObservation(
            int dbId,
            Integer biologicalSamplePk,
            String parameterStableId,
            int parameterPk,
            String sequenceId,
            int populationId,
            ObservationType observationType,
            int missing,
            String parameterStatus,
            String parameterStatusMessage,
            OntologyParameter ontologyParameter
    ) throws DataLoadException {

        KeyHolder keyholder     = new GeneratedKeyHolder();
        int       observationPk;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("dbId", dbId);
        parameterMap.put("biologicalSampleId", biologicalSamplePk);
        parameterMap.put("observationType", observationType.name());
        parameterMap.put("parameterId", parameterPk);
        parameterMap.put("parameterStableId", parameterStableId);
        parameterMap.put("sequenceId", sequenceId);
        parameterMap.put("populationId", populationId);
        parameterMap.put("missing", missing);
        parameterMap.put("parameterStatus", parameterStatus);
        parameterMap.put("parameterStatusMessage", parameterStatusMessage);

        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        int count = jdbcCda.update(OBSERVATION_INSERT, parameterSource, keyholder);
        if (count > 0) {
            observationPk = keyholder.getKey().intValue();
        } else {
            logger.warn("Insert OntologyParameter to observation table failed for parameterSource {}", parameterSource);
            return 0;
        }

        if (missing == 0) {
            final String insert =
                    "INSERT INTO ontology_observation (id, parameter_id, sequence_id)" +
                            " VALUES (:observationPk, :parameterId, :sequenceId)";

            parameterMap.clear();
            parameterMap.put("observationPk", observationPk);
            parameterMap.put("parameterId", ontologyParameter.getParameterID());
            parameterMap.put("sequenceId", ontologyParameter.getSequenceID());

            try {
                count = jdbcCda.update(insert, parameterMap);
            } catch (Exception e) {
                logger.error("INSERT to ontology_observation table failed for parameterStableId {}, parameterId {}, observationType {}, observationPk {}, sequenceId {}. Reason:\n\t{}",
                             parameterStableId, ontologyParameter.getParameterID(), observationType.toString(), observationPk, ontologyParameter.getSequenceID(), e.getLocalizedMessage());
            }
            if (count == 0) {
                logger.warn("Insert OntologyParameter failed for parameterSource {}. Marking it as missing ...", parameterSource);
                updateObservationMissingFlag(observationPk, true);
            }
        }

        return observationPk;
    }


    // SeriesMediaParameterValue version. Returns the newly-inserted primary key if successful; 0 otherwise.
    public int insertObservation(
            int dbId,
            Integer biologicalSamplePk,
            String parameterStableId,
            int parameterPk,
            String sequenceId,
            int populationId,
            ObservationType observationType,
            int missing,
            String parameterStatus,
            String parameterStatusMessage,
            SeriesMediaParameterValue seriesMediaParameterValue,
            DccExperimentDTO dccExperimentDTO,
            int samplePk,
            int organisationPk,
            int experimentPk,
            List<SimpleParameter> simpleParameterList,
            List<OntologyParameter> ontologyParameterList
    ) throws DataLoadException {

        KeyHolder keyholder     = new GeneratedKeyHolder();
        int       observationPk;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("dbId", dbId);
        parameterMap.put("biologicalSampleId", biologicalSamplePk);
        parameterMap.put("observationType", observationType.name());
        parameterMap.put("parameterId", parameterPk);
        parameterMap.put("parameterStableId", parameterStableId);
        parameterMap.put("sequenceId", sequenceId);
        parameterMap.put("populationId", populationId);
        parameterMap.put("missing", missing);
        parameterMap.put("parameterStatus", parameterStatus);
        parameterMap.put("parameterStatusMessage", parameterStatusMessage);

        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        int count = jdbcCda.update(OBSERVATION_INSERT, parameterSource, keyholder);
        if (count > 0) {
            observationPk = keyholder.getKey().intValue();
        } else {
            logger.warn("Insert SeriesMediaParameter to observation table failed for parameterSource {}", parameterSource);
            return 0;
        }

        if (missing == 0) {
            final String insert =
                    "INSERT INTO image_record_observation (" +
                            "id, sample_id, download_file_path, image_link, increment_value, file_type, media_sample_local_id, media_section_id, organisation_id, full_resolution_file_path" +
                            "" +
                            ") VALUES (" +
                            ":observationPk, :samplePk, :downloadFilePath, :imageLink, :incrementValue, :fileType, :mediaSampleLocalId, :mediaSectionId, :organisationPk, :fullResolutionFilePath" +
                            ")";

            String filePathWithoutName = createNfsPathWithoutName(dccExperimentDTO, parameterStableId);
            String fullResolutionFilePath = getFullResolutionFilePath(filePathWithoutName, seriesMediaParameterValue.getURI());

            parameterMap.clear();
            parameterMap.put("observationPk", observationPk);
            parameterMap.put("samplePk", samplePk);
            parameterMap.put("downloadFilePath", seriesMediaParameterValue.getURI().toLowerCase());
            parameterMap.put("imageLink", seriesMediaParameterValue.getLink());
            parameterMap.put("incrementValue", null);
            parameterMap.put("fileType", seriesMediaParameterValue.getFileType());
            parameterMap.put("mediaSampleLocalId", null);
            parameterMap.put("mediaSectionId", null);
            parameterMap.put("organisationPk", organisationPk);
            parameterMap.put("fullResolutionPath", fullResolutionFilePath);

            try {
                count = jdbcCda.update(insert, parameterMap);
            } catch (Exception e) {
                logger.error("INSERT to image_record_observation table for SeriesMediaParameterValue failed for parameterStableId {}, observationType {}, observationPk {}, samplePk {}, downloadFilePath {}, imageLink {}, fileType {}, organisationPk, fullResolutionPath {}. Reason:\n\t{}",
                             parameterStableId, observationType.toString(), observationPk, samplePk, seriesMediaParameterValue.getURI(), seriesMediaParameterValue.getLink(),
                             seriesMediaParameterValue.getFileType(), organisationPk, fullResolutionFilePath, e.getLocalizedMessage());
            }
            if (count == 0) {
                logger.warn("Insert MediaParameter failed for parameterSource {}. Marking it as missing ...", parameterSource);
                updateObservationMissingFlag(observationPk, true);
            } else {
                // Save any parameter associations.
                for (ParameterAssociation parameterAssociation : seriesMediaParameterValue.getParameterAssociation()) {
                    int parameterAssociationPk = insertParameterAssociation(observationPk, parameterAssociation, simpleParameterList, ontologyParameterList);

                    // Save any Dimensions.
                    for (Dimension dimension : parameterAssociation.getDim()) {
                        insertDimension(parameterAssociationPk, dimension);
                    }
                }

                // Save any procedure metadata.
                for (ProcedureMetadata procedureMetadata : seriesMediaParameterValue.getProcedureMetadata()) {
                    insertProcedureMetadata(seriesMediaParameterValue.getProcedureMetadata(), dccExperimentDTO.getProcedureId(),
                                            experimentPk, observationPk);
                }
            }
        } else {
            logger.debug("Image record not loaded: " + seriesMediaParameterValue.getURI());
        }

        return observationPk;
    }


    // SeriesParameter version. Returns the newly-inserted primary key if successful; 0 otherwise.
    public int insertObservation(
            int dbId,
            Integer biologicalSamplePk,
            String parameterStableId,
            int parameterPk,
            String sequenceId,
            int populationId,
            ObservationType observationType,
            int missing,
            String parameterStatus,
            String parameterStatusMessage,
            SeriesParameter seriesParameter,
            Float dataPoint,
            Date timePoint,
            Float discretePoint
    ) throws DataLoadException {

        KeyHolder keyholder     = new GeneratedKeyHolder();
        int       observationPk;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("dbId", dbId);
        parameterMap.put("biologicalSampleId", biologicalSamplePk);
        parameterMap.put("observationType", observationType.name());
        parameterMap.put("parameterId", parameterPk);
        parameterMap.put("parameterStableId", parameterStableId);
        parameterMap.put("sequenceId", sequenceId);
        parameterMap.put("populationId", populationId);
        parameterMap.put("missing", missing);
        parameterMap.put("parameterStatus", parameterStatus);
        parameterMap.put("parameterStatusMessage", parameterStatusMessage);

        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        int count = jdbcCda.update(OBSERVATION_INSERT, parameterSource, keyholder);
        if (count > 0) {
            observationPk = keyholder.getKey().intValue();
        } else {
            logger.warn("Insert SeriesParameter to observation table failed for parameterSource {}", parameterSource);
            return 0;
        }

        if (missing == 0) {
            final String insert = "INSERT INTO time_series_observation (id, data_point, time_point, discrete_point)" +
                    "VALUES (:observationPk, :dataPoint, :timePoint, :discretePoint)";

            parameterMap.clear();
            parameterMap.put("observationPk", observationPk);
            parameterMap.put("dataPoint", dataPoint);
            parameterMap.put("timePoint", timePoint);
            parameterMap.put("discretePoint", discretePoint);

            try {
                count = jdbcCda.update(insert, parameterMap);
            } catch (Exception e) {
                logger.error("INSERT to time_series_observation table failed for parameterStableId {}, observationType {}, observationPk {}, dataPoint {}, timePoint {}, discretePoint {}. Reason:\n\t{}",
                             parameterStableId, observationType.toString(), observationPk, dataPoint, timePoint, discretePoint, e.getLocalizedMessage());
            }
            if (count == 0) {
                logger.warn("Insert failed for parameterSource {}. Marking it as missing ...", parameterSource);
                updateObservationMissingFlag(observationPk, true);
            }
        }

        return observationPk;
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

        Map<String, Integer> countsMap = new HashMap<>();
        countsMap.put("terms", 0);
        countsMap.put("synonyms", 0);
        countsMap.put("alternateIds", 0);
        countsMap.put("considerIds", 0);

        final String ontologyTermInsert = "INSERT INTO ontology_term (acc, db_id, name, description, is_obsolete, replacement_acc) " +
                                          "VALUES (:acc, :db_id, :name, :description, :is_obsolete, :replacement_acc)";

        final String synonymInsert = "INSERT INTO synonym (acc, db_id, symbol) " +
                                     "VALUES (:acc, :db_id, :symbol)";

        final String alternateIdInsert = "INSERT INTO alternate_id (ontology_term_acc, alternate_id_acc) " +
                                         "VALUES (:ontology_term_acc, :alternate_id_acc)";

        final String considerIdInsert = "INSERT INTO consider_id (ontology_term_acc, consider_id_acc) " +
                                        "VALUES (:ontology_term_acc, :consider_id_acc)";

        for (OntologyTerm term : terms) {
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("acc", term.getId().getAccession());
                parameterMap.put("db_id", term.getId().getDatabaseId());
                parameterMap.put("name", term.getName());
                parameterMap.put("description", term.getDescription());
                parameterMap.put("is_obsolete", term.getIsObsolete());
                parameterMap.put("replacement_acc", term.getReplacementAcc());

                count = jdbcCda.update(ontologyTermInsert, parameterMap);
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

                    count = jdbcCda.update(synonymInsert, parameterMap);
                    countsMap.put("synonyms", countsMap.get("synonyms") + count);

                    if (count > 0) {
                        updateSynonymMap(synonym.getAccessionId(), synonym);
                    }

                } catch (DuplicateKeyException dke) {

                }
            }

            // Try to insert alternateIds. Ignore DuplicateKeyExceptions.

            for (AlternateId alternateId : term.getAlternateIds()) {
                try {
                    Map<String, Object> parameterMap = new HashMap<>();
                    parameterMap.put("ontology_term_acc", term.getId().getAccession());
                    parameterMap.put("alternate_id_acc", alternateId.getAlternateAccessionId());

                    count = jdbcCda.update(alternateIdInsert, parameterMap);
                    countsMap.put("alternateIds", countsMap.get("alternateIds") + count);

                } catch (DuplicateKeyException dke) {

                }
            }

            // Try to insert considerIds. Ignore DuplicateKeyExceptions.

            for (ConsiderId considerId : term.getConsiderIds()) {
                try {
                    Map<String, Object> parameterMap = new HashMap<>();
                    parameterMap.put("ontology_term_acc", term.getId().getAccession());
                    parameterMap.put("consider_id_acc", considerId.getConsiderAccessionId());

                    count = jdbcCda.update(considerIdInsert, parameterMap);
                    countsMap.put("considerIds", countsMap.get("considerIds") + count);

                } catch (DuplicateKeyException dke) {

                }
            }
        }

        return countsMap;
    }


    // Returns the parameter association primary key.
    public int insertParameterAssociation(int observationPk, ParameterAssociation parameterAssociation,
                                           List<SimpleParameter> simpleParameterList,
                                           List<OntologyParameter> ontologyParameterList)
    {
        int pk = 0;
        String insert = "INSERT INTO parameter_association (observation_id,  parameter_id, sequence_id, parameter_association_value)" +
                "VALUES (:observationPk, :parameterId, :sequenceId, :parameterAssociationValue)";

        KeyHolder           keyholder       = new GeneratedKeyHolder();

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("observationPk", observationPk);
        parameterMap.put("parameterId", parameterAssociation.getParameterID());
        parameterMap.put("sequenceId", parameterAssociation.getSequenceID());

        // set the parameter association value here. It is always a
        // seriesParameter or Ontology so not multiple values are allowed.
        // Loop through simple parameters (but not ontology parameters as they
        // don't have values) to get the value for this parameterAssociation and get
        // the value for it.
        String value = null;
        for (SimpleParameter sp : simpleParameterList) {
            String paramStableId = sp.getParameterID();                             // parameter stable id
            if (paramStableId.equals(parameterAssociation.getParameterID())) {
                value = sp.getValue();
            }
        }
        for (OntologyParameter sp : ontologyParameterList) {
            String paramStableId = sp.getParameterID();                             // parameter stable id
            if (paramStableId.equals(parameterAssociation.getParameterID())) {
                for (String term : sp.getTerm()) {
                    System.err.println("ontology parameter in parameterAssociation not storing these yet but if they are here we should! term has values in them term=" + term);
                }
                value = org.apache.commons.lang.StringUtils.join(sp.getTerm(), ",");
            }
        }

        if (value != null) {
            if (value.length() > 45) {
                String trimmedValue = StringUtils.left(value, 44);
                logger.info("Trimming parameterAssociationValue '{}' to 44 characters ('{}')", value, trimmedValue);
                value = trimmedValue;
            }
        }

        parameterMap.put("parameterAssociationValue", value);
        SqlParameterSource  parameterSource = new MapSqlParameterSource(parameterMap);

        int count = jdbcCda.update(insert, parameterMap);
        if (count > 0) {
            pk = keyholder.getKey().intValue();
        }

        return pk;
    }



    /**
     * Try to insert the phenotyped colony. Return the count of inserted phenotype colonies.
     *
     * @param phenotypedColonies the {@link List} of {@link PhenotypedColony} to be inserted
     *
     * @return the count of inserted phenotype colonies.
     */
    public int insertPhenotypedColonies(List<PhenotypedColony> phenotypedColonies) throws DataLoadException {
        int count = 0;

        String query = "INSERT INTO phenotyped_colony (" +
                       " colony_name," +
                       " es_cell_name," +
                       " gf_acc," +
                       " gf_db_id," +
                       " allele_symbol," +
                       " background_strain_name," +
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
                       " :allele_symbol," +
                       " :background_strain_name," +
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
                parameterMap.put("allele_symbol", phenotypedColony.getAlleleSymbol());
                parameterMap.put("background_strain_name", phenotypedColony.getBackgroundStrainName());
                parameterMap.put("production_centre_organisation_id", phenotypedColony.getProductionCentre().getId());
                parameterMap.put("production_consortium_project_id", phenotypedColony.getProductionConsortium().getId());
                parameterMap.put("phenotyping_centre_organisation_id", phenotypedColony.getPhenotypingCentre().getId());
                parameterMap.put("phenotyping_consortium_project_id", phenotypedColony.getPhenotypingConsortium().getId());
                parameterMap.put("cohort_production_centre_organisation_id", phenotypedColony.getCohortProductionCentre().getId());

                count = jdbcCda.update(query, parameterMap);

            } catch (DuplicateKeyException e) {

            }
        }

        return count;
    }

    public int insertProcedureMetadata(List<ProcedureMetadata> metadataList, String procedureId, int experimentPk, int observationPk) {
        int pk = 0;
        final String insert = "INSERT INTO procedure_meta_data (procedure_id, experiment_id, parameter_id, sequence_id, parameter_status, value, observation_id)" +
                             " VALUES (:procedureId, :experimentPk, :parameterId, :sequenceId, :parameterStatus, :value, :observationPk)";

        KeyHolder           keyholder       = new GeneratedKeyHolder();
        Map<String, Object> parameterMap    = new HashMap<>();

        for (ProcedureMetadata metadata : metadataList) {

            String metadataValue = metadata.getValue();
            if (metadataValue != null) {
                metadataValue = metadataValue.trim().replace("\n", "");         // remove terminating newlines from europhenome ICS data.
            }

            parameterMap.put("procedureId", procedureId);
            parameterMap.put("experimentPk", experimentPk);
            parameterMap.put("parameterId", metadata.getParameterID());
            parameterMap.put("sequenceId", metadata.getSequenceID());
            parameterMap.put("parameterStatus", metadata.getParameterStatus());
            parameterMap.put("value", metadataValue);
            parameterMap.put("observationPk", observationPk);
            SqlParameterSource  parameterSource = new MapSqlParameterSource(parameterMap);

            int count = jdbcCda.update(insert, parameterSource, keyholder);

            if (count > 0) {
                pk = keyholder.getKey().intValue();
            }
        }

        return pk;
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

            List<SequenceRegion> sequenceRegionList = jdbcCda.query(query, new SequenceRegionRowMapper());

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
     * @throws DataLoadException
     */
    public Strain getStrain(String accessionId) throws DataLoadException {
        return getStrains().get(accessionId);
    }

    /**
     * @return a list of all strains, keyed by accession id
     *
     * @throws DataLoadException
     */
    public Map<String, Strain> getStrains() throws DataLoadException {

        if ((strains == null) || strains.isEmpty()) {
            strains = new ConcurrentHashMap<>();

            logger.info("Loading strains.");

            List<Strain> strainList = jdbcCda.query("SELECT * FROM strain", new StrainRowMapper());

            for (Strain strain : strainList) {
                strains.put(strain.getId().getAccession(), strain);
            }

            logger.info("Loading strains complete.");
        }

        return strains;
    }


    /**
     * @return a list of all strains, keyed by strain name
     *
     * @throws DataLoadException
     */
    public Map<String, Strain> getStrainsByName() throws DataLoadException {

        if ((strainsByName == null) || strainsByName.isEmpty()) {
            strainsByName = new ConcurrentHashMap<>();

            logger.info("Loading strains by name.");

            List<Strain> strainList = new ArrayList<>(getStrains().values());

            for (Strain strain : strainList) {
                strainsByName.put(strain.getName(), strain);
            }

            logger.info("Loading strains by name complete.");
        }

        return strains;
    }
    /**
     * Returns the {@code Strain} instance matching {@code strainName}, if found; null otherwise
     *
     * @param strainName the strain name
     *
     * @return the {@code Strain} instance matching {@code strainName}, if found; null otherwise
     *
     * @throws DataLoadException
     */
    public Strain getStrainByName(String strainName) throws DataLoadException {
        return getStrainsByName().get(strainName);
    }

    /**
     * Try to insert the strain and, if successful, any synonyms.
     *
     * @param strain the {@link Strain} to be inserted
     *
     * @return a map, keyed by type (strains, synonyms) of the number of {@code strain} components inserted
     */
    public Map<String, Integer> insertStrain(Strain strain) throws DataLoadException {
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
    public Map<String, Integer> insertStrains(List<Strain> strains) throws DataLoadException {
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

                count = jdbcCda.update(strainInsert, parameterMap);
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

            count = jdbcCda.update(query, parameterMap);

            if (count > 0) {
                updateSynonymMap(synonym.getAccessionId(), synonym);
            }

        } catch (DuplicateKeyException dke) {

        }

        return count;
    }

    public void updateObservationMissingFlag(int observationPk, boolean missing) {
        int iMissing = (missing ? 1 : 0);
        final String update = "UPDATE observation SET missing = :missing WHERE id = :observationPk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("observationPk", observationPk);
        parameterMap.put("missing", iMissing);

        jdbcCda.update(update, parameterMap);
    }

    public void insertDimension(int parameterAssociationPk, Dimension dimension) {
        final String insert = "INSERT INTO dimension (parameter_association_id, id,  origin, unit, value)" +
                                            " VALUES (:parameterAssociationPk, :id, :origin, :unit, :value)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("parameterAssociationPk", parameterAssociationPk);
        parameterMap.put("id", dimension.getId());
        parameterMap.put("origin", dimension.getOrigin());
        parameterMap.put("unit", dimension.getUnit());
        parameterMap.put("value", dimension.getValue());

        jdbcCda.update(insert, parameterMap);
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
            List<Synonym> synonymList = jdbcCda.query("SELECT * FROM synonym ORDER BY acc", new SynonymRowMapper());
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

        List<Xref> xrefList = jdbcCda.query("SELECT * FROM xref", new XrefRowMapper());

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
            GenomicFeature gene = new GenomicFeature();
            gene.setId(new DatasourceEntityId(rs.getString("gf_acc"), rs.getInt("gf_db_id")));
            allele.setGene(gene);
//            allele.setGene(getGene(rs.getString("acc")));
//            allele.setSynonyms(getSynonyms(rs.getString("acc")));

            return allele;
        }
    }

    public class BiologicalModelRowMapper implements RowMapper<BiologicalModel> {

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
        public BiologicalModel mapRow(ResultSet rs, int rowNum) throws SQLException {
            BiologicalModel bm = new BiologicalModel();

            bm.setId(rs.getInt("id"));
            bm.setAllelicComposition(rs.getString("allelic_composition"));
            bm.setGeneticBackground(rs.getString("genetic_background"));
            bm.setZygosity(rs.getString("zygosity"));

            return bm;
        }
    }

    public class BiologicalSampleRowMapper implements RowMapper<BiologicalSample> {

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
        public BiologicalSample mapRow(ResultSet rs, int rowNum) throws SQLException {
            BiologicalSample biologicalSample = new BiologicalSample();

            biologicalSample.setId(rs.getInt("id"));
            biologicalSample.setStableId(rs.getString("external_id"));
            Datasource datasource = new Datasource();
            datasource.setId(rs.getInt("db_id"));
            biologicalSample.setDatasource(datasource);
            biologicalSample.setType(new OntologyTerm(rs.getString("sample_type_acc"), rs.getInt("sample_type_db_id")));
            biologicalSample.setGroup(rs.getString("sample_group"));
            Organisation organisation = new Organisation();
            organisation.setId(rs.getInt("organisation_id"));
            biologicalSample.setOrganisation(organisation);
            Organisation productionCenter = new Organisation();
            productionCenter.setId(rs.getInt("production_center_id"));
            biologicalSample.setProductionCenter(productionCenter);
            // litter_id was moved to LiveSample.

            return biologicalSample;
        }
    }

    public class AlternateIdRowMapper implements RowMapper<AlternateId> {

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
        public AlternateId mapRow(ResultSet rs, int rowNum) throws SQLException {
            AlternateId alternateId = new AlternateId();

            alternateId.setOntologyTermAccessionId(rs.getString("ontology_term_acc"));
            alternateId.setAlternateAccessionId(rs.getString("alternate_id_acc"));

            return alternateId;
        }
    }

    /**
     *
     * @return A set of required parameters
     */
    public HashSet<String> getRequiredImpressParameters() {

        String query = "SELECT stable_id FROM phenotype_parameter WHERE metadata = 1 AND data_analysis = 1";
        List<String> results = jdbcCda.queryForList(query, new HashMap(), String.class);
        return new HashSet<>(results);
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
            considerId.setConsiderAccessionId(rs.getString("consider_id_acc"));

            return considerId;
        }
    }

    private OntologyTerm targetedTerm = null;
    public OntologyTerm getTargetedTerm() {
        if (targetedTerm == null) {
            try {
                targetedTerm = getOntologyTermByName("targeted");
            } catch (Exception e) {

            }
        }

        return targetedTerm;
    }

    /**
   	 * Create an allele record in the database with the supplied allele symbol
   	 *
   	 *
   	 * @param alleleSymbol the allele symbol
     * @param gene the gene instance
   	 * @return an allele DAO object representing the newly created allele, or null if the allele symbol is bad.
   	 */
   	public Allele createAndInsertAllele(String alleleSymbol, GenomicFeature gene) throws DataLoadException {

        if (alleleSymbol == null || alleleSymbol.isEmpty()) {
            logger.warn("Allele symbol is null");
      		throw new DataLoadException("Allele symbol is null");
        }

   		// Create the allele based on the symbol
   		// e.g. allele symbol Lama4<tm1.1(KOMP)Vlcg>
        // Alleles are not required to have "<" and ">". If missing, just use the name for both the gene and the allele.

   		// Create the gene symbol
        int index = alleleSymbol.indexOf('<');
   		String alleleGeneSymbol = (index >= 0 ? alleleSymbol.substring(0, index)  : alleleSymbol);

   		// Create the allele acc
   		String alleleAccession = "NULL-" + DigestUtils.md5Hex(alleleSymbol).substring(0, 9).toUpperCase();

        // Create the allele
        Allele allele = new Allele();
        allele.setBiotype(getTargetedTerm());
        allele.setGene(gene);
        allele.setId(new DatasourceEntityId(alleleAccession, DbIdType.IMPC.intValue()));
        allele.setName(alleleSymbol);
        allele.setSymbol(alleleSymbol);
        allele.setSynonyms(new ArrayList<>());
        List<Allele> alleles = new ArrayList<>();
        alleles.add(allele);

   		// Insert the allele into the database
        int count = insertAlleles(alleles);
        if (count > 0) {
            logger.info("Created allele '{}', '{}', '{}'", allele.getId().getAccession(), allele.getSymbol(), (gene == null ? "null" : gene.getSymbol()));
        }

        return allele;
   	}


    /**
   	 * Create a strain record in the database with the supplied strain name
   	 *
   	 * @param strainName the strain name
   	 * @return the {@link Strain} instance representing the newly created strain, or null if the strain name is bad.
   	 */
   	public Strain createAndInsertStrain(String strainName) throws DataLoadException {

   		if (strainName == null || strainName.isEmpty()) {
   			return null;
   		}

   		// Create the strain acc
   		String strainAccession = "NULL-" + DigestUtils.md5Hex(strainName).substring(0, 9).toUpperCase();

        // Create the strain based on the strain name
        Strain strain = new Strain();
        strain.setId(new DatasourceEntityId(strainAccession, DbIdType.IMPC.intValue()));
        strain.setName(strainName);

        // insert the strain into the database
   		Map<String, Integer> count = insertStrain(strain);
        if (count.get("strains") > 0) {
            logger.info("Created strain '{}', '{}'", strain.getId().getAccession(), strain.getName());
        }

   		return strain;
   	}

    /**
     * Enables/disables mysql table indexes.
     */
    public enum IndexAction {
        DISABLE,
        ENABLE
    }
    public void manageIndexes(String tableName, IndexAction action) {
        String query = "ALTER TABLE " + tableName + " " + action.toString() + " KEYS";

        jdbcCda.getJdbcOperations().execute(query);
    }

    /**
     * Returns a biological model. First queries for an existing one matching input parameters. If not found, creates
     * one and returns it.
     *
     * @param colony
     * @param strainMapper
     * @param zygosity
     * @param sampleGroup
     * @param allelesBySymbol
     * @return {@link BiologicalModel} matching the input parameters. Creates it if necessary.
     * @throws DataLoadException
     */
    public BiologicalModel selectOrInsertBiologicalModel (
            PhenotypedColony colony,
            EuroPhenomeStrainMapper strainMapper,
            String zygosity,
            String sampleGroup,
            Map<String, Allele> allelesBySymbol) throws DataLoadException
    {

        String message;

        GenomicFeature gene;
        String backgroundStrainName;
        Strain backgroundStrain;

        if (colony == null) {
            logger.warn("colony is null for sample group {}", sampleGroup);
            throw new DataLoadException("colonyId is null");
        }

        // Get the gene. Mark as error and skip if no gene.
        gene = colony.getGene();
        if (gene == null) {
            message = "Missing gene information for dcc-supplied colony " + colony.getColonyName() + " for allele symbol '" + colony.getAlleleSymbol() + "'. Skipping...";
            logger.error(message);
            throw new DataLoadException(message);
        }

        // Get the allele by symbol.
        Allele allele = allelesBySymbol.get(colony.getAlleleSymbol());
        if (allele == null) {
            allele = createAndInsertAllele(colony.getAlleleSymbol(), gene);
        }

        // Get the background strain from iMits. EuroPhenome background strains require manual curation/remapping and
        // may be comprised of multiple strains separated by semicolons. Treat any background strains with semicolons
        // as a single strain; do not split them into separate strains.
        // Recap:
        //  - Get background strain from iMits
        //  - Filter the iMits background strain name through the EuroPhenomeStrainMapper
        //  - If the filtered background strain does not exist, create it and add it to the strain table.
        try {
            backgroundStrainName = strainMapper.filterEuroPhenomeGeneticBackground(colony.getBackgroundStrainName());
            backgroundStrain = getStrainByName(backgroundStrainName);
            if (backgroundStrain == null) {
                backgroundStrain = createAndInsertStrain(backgroundStrainName);
            }

        } catch (DataLoadException e) {

            message = "Insert strain " + colony.getBackgroundStrainName() + " for dcc-supplied colony '" + colony.getColonyName() + "' failed. Reason: " + e.getLocalizedMessage() + ". Skipping...";
            logger.error(message);
            throw new DataLoadException(message, e);
        }

        // Get the biological model. Create one if it is not found.
        BiologicalModel biologicalModel = getBiologicalModelByJoins(colony.getGene().getId().getAccession(), allele.getSymbol(), backgroundStrainName);
        if (biologicalModel == null) {
            String allelicComposition = strainMapper.createAllelicComposition(zygosity, allele.getSymbol(), gene.getSymbol(), sampleGroup);
            BiologicalModelAggregator biologicalModelAggregator = new BiologicalModelAggregator(
                    allelicComposition,
                    allele.getSymbol(),
                    backgroundStrainName,
                    zygosity,
                    allele.getId().getAccession(),
                    colony.getGene().getId().getAccession(),
                    backgroundStrain.getId().getAccession());
            List<BiologicalModelAggregator> biologicalModelAggregators = new ArrayList<>();
            biologicalModelAggregators.add(biologicalModelAggregator);

            insertBiologicalModel(biologicalModelAggregators);

            biologicalModel = getBiologicalModel(allelicComposition, backgroundStrainName);
            if (biologicalModel == null) {
                throw new DataLoadException("Attempt to create biological model for colony '" + colony.getColonyName() + "' failed.");
            }
        }

        return biologicalModel;
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

    public class OntologyTermAnomalyRowMapper implements RowMapper<OntologyTermAnomaly> {

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
        public OntologyTermAnomaly mapRow(ResultSet rs, int rowNum) throws SQLException {
            OntologyTermAnomaly term = new OntologyTermAnomaly(
                rs.getString("db_name"),
                rs.getString("table_name"),
                rs.getString("column_name"),
                rs.getString("original_acc"),
                rs.getString("replacement_acc"),
                rs.getString("reason"));

            term.setId(rs.getInt("id"));
            term.setLast_modified(new Date(rs.getTimestamp("last_modified").getTime()));

            return term;
        }
    }

    public class ParameterRowMapper implements RowMapper<Parameter> {

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
        public Parameter mapRow(ResultSet rs, int rowNum) throws SQLException {
            Parameter parameter = new Parameter();

            parameter.setId(rs.getInt("id"));
            parameter.setStableId(rs.getString("stable_id"));

            Datasource datasource = new Datasource();
            datasource.setId(rs.getInt("db_id"));
            parameter.setDatasource(datasource);
            parameter.setName(rs.getString("name"));

            parameter.setDescription(rs.getString("description"));
            parameter.setMajorVersion(rs.getInt("major_version"));
            parameter.setMinorVersion(rs.getInt("minor_version"));
            parameter.setUnit(rs.getString("unit"));
            parameter.setDatatype(rs.getString("datatype"));
            parameter.setType(rs.getString("parameter_type"));
            parameter.setFormula(rs.getString("formula"));
            Integer flag = rs.getInt("required");
            parameter.setRequiredFlag((flag != null) && (flag == 1 ? true : false));
            
            flag = rs.getInt("metadata");
            parameter.setMetaDataFlag((flag != null) && (flag == 1 ? true : false));
            
            flag = rs.getInt("important");
            parameter.setImportantFlag((flag != null) && (flag == 1 ? true : false));
            
            flag = rs.getInt("derived");
            parameter.setDerivedFlag((flag != null) && (flag == 1 ? true : false));
            
            flag = rs.getInt("annotate");
            parameter.setAnnotateFlag((flag != null) && (flag == 1 ? true : false));
            
            flag = rs.getInt("increment");
            parameter.setIncrementFlag((flag != null) && (flag == 1 ? true : false));
            
            flag = rs.getInt("options");
            parameter.setOptionsFlag((flag != null) && (flag == 1 ? true : false));

            parameter.setSequence(rs.getInt("sequence"));
            
            flag = rs.getInt("media");
            parameter.setMediaFlag((flag != null) && (flag == 1 ? true : false));
            
            flag = rs.getInt("data_analysis");
            parameter.setRequiredForDataAnalysisFlag((flag != null) && (flag == 1 ? true : false));

            parameter.setDataAnalysisNotes(rs.getString("data_analysis_notes"));
            parameter.setStableKey(rs.getInt("stable_key"));

            return parameter;
        }
    }

    public class PhenotypedColonyRowMapper implements RowMapper<PhenotypedColony> {

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
        public PhenotypedColony mapRow(ResultSet rs, int rowNum) throws SQLException {
            PhenotypedColony phenotypedColony = new PhenotypedColony();

            phenotypedColony.setId(rs.getInt("id"));
            phenotypedColony.setColonyName(rs.getString("colony_name"));
            String esCellName = rs.getString("es_cell_name");
            phenotypedColony.setEs_cell_name(rs.wasNull() ? null : esCellName);

            GenomicFeatureRowMapper geneRowMapper = new GenomicFeatureRowMapper();
            GenomicFeature gene = geneRowMapper.mapRow(rs, rowNum);
            phenotypedColony.setGene(gene);

            phenotypedColony.setAlleleSymbol(rs.getString("allele_symbol"));

            phenotypedColony.setBackgroundStrainName(rs.getString("background_strain_name"));

            Organisation productionCenter = new Organisation();
            productionCenter.setId(rs.getInt("production_centre_organisation_id"));
            phenotypedColony.setProductionCentre(productionCenter);

            Project productionConsortium = new Project();
            productionConsortium.setId(rs.getInt("production_consortium_project_id"));
            phenotypedColony.setProductionConsortium(productionConsortium);

            Organisation phenotypingCenter = new Organisation();
            phenotypingCenter.setId(rs.getInt("phenotyping_centre_organisation_id"));
            phenotypedColony.setPhenotypingCentre(phenotypingCenter);

            Project phenotypingConsortium = new Project();
            phenotypingConsortium.setId(rs.getInt("phenotyping_consortium_project_id"));
            phenotypedColony.setPhenotypingConsortium(phenotypingConsortium);

            Organisation cohortProduction = new Organisation();
            cohortProduction.setId(rs.getInt("cohort_production_centre_organisation_id"));
            phenotypedColony.setCohortProductionCentre(cohortProduction);

            return phenotypedColony;
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

    // PRIVATE METHODS


    private String createNfsPathWithoutName(DccExperimentDTO dccExperimentDTO, String parameterStableId) {
        return dccExperimentDTO.getPhenotypingCenter() + "/" + dccExperimentDTO.getPipeline() + "/" + dccExperimentDTO.getProcedureId() + "/" + parameterStableId;
    }


    private String getFullResolutionFilePath(String filePathWithoutName, String uri) {

   		String fullResolutionFilePath = null;
   		//dont do this if it's not a mousephenotype.org URL. ie. it's not been provided by the phenoDCC
   		if (uri.contains("www.mousephenotype.org")) {
   			fullResolutionFilePath = filePathWithoutName + "/" + uri.substring(uri.lastIndexOf("/") + 1, uri.length());
   		}

   		logger.debug("fullresfilepath = " + fullResolutionFilePath);

   		return fullResolutionFilePath;
   	}
}