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

import javafx.util.Pair;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.enumerations.DbIdType;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.loads.create.load.support.StrainMapper;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

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
    private Map<String, SequenceRegion>   sequenceRegions;  // keyed by strains id (int)
    private SqlUtils                      sqlUtils = new SqlUtils();
    private Map<String, Strain>           strainsByNameOrMgiAccessionId = new HashMap<>();    // keyed by strains name or mgi accession id
    private Map<String, Strain>           strainsBySynonym = new HashMap<>();    // keyed by strains synonym
    private Map<String, List<Synonym>>    synonyms;         // keyed by accession id

    private final LoadUtils   loadUtils   = new LoadUtils();
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());

    public static final String FEATURES_UNKNOWN    = "unknown";

    public static final String STATUS_ACTIVE       = "active";
    public static final String STATUS_WITHDRAWN    = "withdrawn";

    public static final String NAME_NOT_SPECIFIED  = "Not Specified";

    public static final String BIOTYPE_GENE_STRING = "Gene";
    public static final String BIOTYPE_TM1A_STRING = "Targeted (Floxed/Frt)";
    public static final String BIOTYPE_TM1E_STRING = "Targeted (Reporter)";

    public static final String EUROPHENOME = "EuroPhenome";         // The datasourceShortName for dcc_europhenome_final loads
    public static final String MGP         = "MGP";                 // The MGP project name
    public static final String THREEI      = "3i";                  // The 3i project name


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
                logger.error("Error inserting allele {}: {}. Record skipped...", allele, e.getLocalizedMessage());
            }
        }

        return count;
    }


    /**
     *
     * @return a map of {@link BiologicalSample}, keyed by external_id and short_name (e.g. "mouseXXX_IMPC", "mouseYYY_3i", etc)
     *         NOTE: The external_id in {@link BiologicalSample} is called stableId.
     */
    public Map<String, BiologicalSample> getBiologicalSamples() {

        Map<String, BiologicalSample> map = new HashMap<>();
        String query = "SELECT edb.short_name, bs.* FROM biological_sample bs JOIN external_db edb ON edb.id = bs.db_id";

        List<BiologicalSample> samples = jdbcCda.query(query, new BiologicalSampleRowMapper());
        for (BiologicalSample sample : samples) {
            map.put(sample.getStableId() + "_" + sample.getOrganisation().getId() + "_" + sample.getDatasource().getShortName(), sample);
        }

        return map;
    }


    public Strain getBackgroundStrain(String specimenStrainId) throws DataLoadException {
        Strain backgroundStrain;
        String backgroundStrainName;
        String message;
        StrainMapper strainMapper = new StrainMapper(this);

        String lookedupStrainName = (strainMapper.lookupBackgroundStrain(specimenStrainId)!=null)
                ? strainMapper.lookupBackgroundStrain(specimenStrainId).getName()
                : specimenStrainId;

        // specimen.strainId can contain an MGI strain accession id in the form "MGI:", or a strain name like C57BL/6N.
        if (specimenStrainId.toLowerCase().startsWith("mgi:")) {
            backgroundStrain = getStrainByNameOrMgiAccessionIdOrSynonym(lookedupStrainName);
            if (backgroundStrain == null) {
                throw new DataLoadException("No strain table entry found for strain accession id '" + specimenStrainId + "' ("+lookedupStrainName+")");
            }
            backgroundStrainName = lookedupStrainName;

        } else {
            backgroundStrainName = lookedupStrainName;
        }

        try {

            backgroundStrain = getStrainByNameOrMgiAccessionIdOrSynonym(backgroundStrainName);

            if (backgroundStrain == null) {
                backgroundStrain = strainMapper.createBackgroundStrain(backgroundStrainName);
                insertStrain(backgroundStrain);
            }

        } catch (DataLoadException e) {

            message = "Insert strain " + specimenStrainId + " failed. Skipping...";
            logger.error(message);
            throw new DataLoadException(message, e);
        }

        return backgroundStrain;
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

    @Transactional
    public int insertBiologicalModelImpc(BioModelInsertDTOMutant mutant) throws DataLoadException {

        // Check to see if model exists before creating, return PK if found
        Integer biologicalModelId = findBiologicalModel(DbIdType.IMPC.intValue(), mutant.getAllelicComposition(), mutant.getGeneticBackground(), mutant.getZygosity());

        if (biologicalModelId==null) {

            biologicalModelId = insertBiologicalModel(DbIdType.IMPC.intValue(), mutant.getAllelicComposition(), mutant.getGeneticBackground(), mutant.getZygosity());
            insertBiologicalModelGenes(biologicalModelId, mutant.getGenes());
            insertBiologicalModelAlleles(biologicalModelId, mutant.getAlleles());
            insertBiologicalModelStrains(biologicalModelId, mutant.getStrains());
        }

        return biologicalModelId;
    }

    @Transactional
    public int insertBiologicalModelImpc(BioModelInsertDTOControl control) throws DataLoadException {

        // Check to see if model exists before creating, return PK if found
        Integer biologicalModelId = findBiologicalModel(control.getDbId(), control.getAllelicComposition(), control.getGeneticBackground(), control.getZygosity());

        if (biologicalModelId==null) {

            biologicalModelId = insertBiologicalModel(control.getDbId(), control.getAllelicComposition(), control.getGeneticBackground(), control.getZygosity());
            insertBiologicalModelStrains(biologicalModelId, control.getStrains());
        }
        return biologicalModelId;
    }

    /**
     * Insert into the biological_model_sample table
     * @param biologicalModelId
     * @param biologicalSampleId
     * @throws DataLoadException
     */
    public void insertBiologicalModelSample(int biologicalModelId, int biologicalSampleId) throws DataLoadException {

        final String insert = "INSERT INTO biological_model_sample (" +
                "biological_model_id,   biological_sample_id) VALUES (" +
                ":biological_model_id, :biological_sample_id)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("biological_model_id", biologicalModelId);
        parameterMap.put("biological_sample_id", biologicalSampleId);

        KeyHolder keyholder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        String message = "INSERT INTO biological_model_sample failed for biological_model_id " + biologicalModelId + ", biological_sample_id " + biologicalSampleId + ". Skipping...";

        try {

            jdbcCda.update(insert, parameterSource, keyholder);

        } catch (DuplicateKeyException e) {

            throw new DataLoadException(message, DataLoadException.DETAIL.DUPLICATE_KEY);
        }
    }

    public int insertBiologicalModelsMGI(List<BioModelInsertDTOMGI> models) throws DataLoadException
    {
        int count = 0;

        for (BioModelInsertDTOMGI model : models) {
            insertBiologicalModelMGI(model);
            count++;
        }

        return count;
    }
    @Transactional
    protected int insertBiologicalModelMGI(BioModelInsertDTOMGI model) throws DataLoadException {
        int biologicalModelId = 0;

        biologicalModelId = insertBiologicalModel(model.getDbId(), model.getAllelicComposition(), model.getGeneticBackground(), model.getZygosity());
        insertBiologicalModelGenes(biologicalModelId, model.getGenes());
        insertBiologicalModelAlleles(biologicalModelId, model.getAlleles());
        insertBiologicalModelPhenotypes(biologicalModelId, model.getPhenotypes());

        return biologicalModelId;
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

        if (originalAcc == null) {
            status.addError("Term " + originalAcc + " is obsolete and has no replacement/consider id term.");
            return null;
        }

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
     *
     * @return a 2-row list of table counts. The first row contains the table names. The second row contains the counts.
     */
    public List<List<String>> getLoadCounts() {
        List<List<String>> results = new ArrayList<>();

        List<String> tableNames = Arrays.asList(
                "experiment", "biological_sample", "live_sample", "procedure_meta_data", "observation",
                "categorical_observation", "datetime_observation", "image_record_observation", "text_observation",
                "time_series_observation", "unidimensional_observation"
        );
        List<String> counts = new ArrayList<>();

        results.add(tableNames);

        for (String tableName : tableNames) {
            String query = "SELECT COUNT(*) FROM " + tableName;
            Map<String, Object> parameterMap = new HashMap<>();

            Long result = jdbcCda.queryForObject(query, parameterMap, Long.class);
            counts.add(Long.toString(result));
        }

        results.add(counts);

        return results;
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
        String mappedTerm = loadUtils.translateTerm(term);

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
        String query =
                "-- PhenotypedColonyRowMapper.sql\n" +
                "\n" +
                "SELECT\n" +
                "  pc.id,\n" +
                "  pc.colony_name,\n" +
                "  pc.es_cell_name,\n" +
                "  pc.gf_acc,\n" +
                "  pc.gf_db_id,\n" +
                "  pc.allele_symbol,\n" +
                "  pc.background_strain_name,\n" +
                "  pc.phenotyping_centre_organisation_id,\n" +
                "  pcphorg.name                              AS pc_phenotyping_centre_name,\n" +
                "  pc.phenotyping_consortium_project_id,\n" +
                "  pcphprj.name                              AS pc_phenotyping_project_name,\n" +
                "  pc.production_centre_organisation_id,\n" +
                "  pcprorg.name                              AS pc_production_centre_name,\n" +
                "  pc.production_consortium_project_id,\n" +
                "  pcprprj.name                              AS pc_production_project_name,\n" +
                "  gf.*\n" +
                "FROM phenotyped_colony  pc\n" +
                "            JOIN genomic_feature    gf          ON gf       .db_id  = gf_db_id AND gf.acc = pc.gf_acc\n" +
                "            JOIN organisation       pcphorg     ON pcphorg  .id     = pc.phenotyping_centre_organisation_id\n" +
                "            JOIN project            pcphprj     ON pcphprj  .id     = pc.phenotyping_consortium_project_id\n" +
                "LEFT OUTER  JOIN organisation       pcprorg     ON pcprorg  .id     = pc.phenotyping_centre_organisation_id\n" +
                "LEFT OUTER  JOIN project            pcprprj     ON pcprprj  .id     = pc.phenotyping_consortium_project_id";

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
            String phenotypingCenter,
            int phenotypingCenterPk,
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

            String filePathWithoutName = createNfsPathWithoutName(dccExperimentDTO, phenotypingCenter, parameterStableId);
            String fullResolutionFilePath = getFullResolutionFilePath(filePathWithoutName, URI);

            parameterMap.clear();
            parameterMap.put("observationPk", observationPk);
            parameterMap.put("samplePk", biologicalSamplePk);
            parameterMap.put("downloadFilePath", URI.toLowerCase());
            parameterMap.put("imageLink", mediaFile.getLink());
            parameterMap.put("incrementValue", null);
            parameterMap.put("fileType", mediaFile.getFileType());
            parameterMap.put("mediaSampleLocalId", null);
            parameterMap.put("mediaSectionId", null);
            parameterMap.put("organisationPk", phenotypingCenterPk);
            parameterMap.put("fullResolutionFilePath", fullResolutionFilePath);

            try {
                count = jdbcCda.update(insert, parameterMap);
            } catch (Exception e) {
                logger.error("INSERT to image_record_observation table for MediaSampleParameter failed for" +
                             " center {}, parameterStableId {}, observationType {}, observationPk {}, biologicalSamplePk {}," +
                             " downloadFilePath {}, imageLink {}, fileType {}, organisationPk {}, fullResolutionFilePath {}." +
                             " Reason:\n\t{}",
                             phenotypingCenter, parameterStableId, observationType.toString(), observationPk, biologicalSamplePk, URI,
                             mediaFile.getLink(), mediaFile.getFileType(), phenotypingCenterPk, fullResolutionFilePath,
                             e.getLocalizedMessage());
            }
            if (count == 0) {
                logger.warn("Insert MediaSampleParameter failed for parameterSource {}. Marking it as missing ...", parameterSource);
                updateObservationMissingFlag(observationPk, true);
            } else {
                // Save parameter associations
                for (ParameterAssociation parameterAssociation : mediaFile.getParameterAssociation()) {
                    int parameterAssociationPk = insertParameterAssociation(observationPk, parameterAssociation, simpleParameterList, ontologyParameterList);

                    // Save Dimensions
                    for (Dimension dimension : parameterAssociation.getDim()) {
                        insertDimension(parameterAssociationPk, dimension);
                    }
                }

                // Save procedure metadata
                insertProcedureMetadata(mediaFile.getProcedureMetadata(), dccExperimentDTO.getProcedureId(), experimentPk, observationPk);
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
            String phenotypingCenter,
            int phenotypingCenterPk
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

            String filePathWithoutName    = createNfsPathWithoutName(dccExperimentDTO, phenotypingCenter, parameterStableId);
            String fullResolutionFilePath = getFullResolutionFilePath(filePathWithoutName, mediaParameter.getURI());

            parameterMap.clear();
            parameterMap.put("observationPk", observationPk);
            parameterMap.put("samplePk", biologicalSamplePk);
            parameterMap.put("downloadFilePath", mediaParameter.getURI().toLowerCase());
            parameterMap.put("imageLink", mediaParameter.getLink());
            parameterMap.put("incrementValue", null);
            parameterMap.put("fileType", mediaParameter.getFileType());
            parameterMap.put("mediaSampleLocalId", null);
            parameterMap.put("mediaSectionId", null);
            parameterMap.put("organisationPk", phenotypingCenterPk);
            parameterMap.put("fullResolutionFilePath", fullResolutionFilePath);

            try {
                count = jdbcCda.update(insert, parameterMap);
            } catch (Exception e) {
                logger.error("INSERT to image_record_observation table for MediaParameter failed for parameterStableId {}," +
                             " observationType {}, observationPk {}, imnageLink {}, fileType {}, phenotypingCenterPk {}," +
                             " fullResolutionFilePath {}. Reason:\n\t{}",
                             parameterStableId, observationType.toString(), observationPk, mediaParameter.getLink(),
                             mediaParameter.getFileType(), phenotypingCenterPk, fullResolutionFilePath, e.getLocalizedMessage());
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
            Integer sequenceId,
            int populationId,
            ObservationType observationType,
            int missing,
            String parameterStatus,
            String parameterStatusMessage,
            OntologyParameter ontologyParameter,
            String experimentId,                     // Used for logging/debugging purposes only.
            int experimentPk                         // Used for logging/debugging purposes only.
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

            final String insertOntologyEntity =
                    "INSERT INTO ontology_entity (ontology_observation_id, term, term_value)" +
                            " VALUES (:observationPk, :term, :termValue)";

            parameterMap.clear();
            parameterMap.put("observationPk", observationPk);
            parameterMap.put("parameterId", ontologyParameter.getParameterID());
            parameterMap.put("sequenceId", ontologyParameter.getSequenceID());

            try {
                count = jdbcCda.update(insert, parameterMap);

                for (String term : ontologyParameter.getTerm()) {

                    OntologyTerm t = null;
                    try {
                        t = getOntologyTerm(term);

                        parameterMap.clear();
                        parameterMap.put("observationPk", observationPk);
                        parameterMap.put("term", term);
                        parameterMap.put("termValue", t.getName());

                        count += jdbcCda.update(insertOntologyEntity, parameterMap);

                    } catch (Exception e) {

                        logger.warn("INSERT to ontology_entity table failed for experimentId {}, experimentPk, observationPk {}, observationType {}, parameterStableId {}, parameterId {}, sequenceId {}, term {}, termValue {}, biologicalSampleId {}. Reason: \n\t{}",
                                experimentId, experimentPk, observationPk, observationType.toString(), parameterStableId, parameterPk, sequenceId, term, t.getName(), biologicalSamplePk, e.getLocalizedMessage());
                    }

                }

            } catch (Exception e) {
                logger.error("INSERT to ontology_observation table failed for experimentId {}, experimentPk {}, parameterStableId {}, parameterId {}, observationType {}, observationPk {}, sequenceId {}. Reason:\n\t{}",
                             experimentId, experimentPk, parameterStableId, ontologyParameter.getParameterID(), observationType.toString(), observationPk, ontologyParameter.getSequenceID(), e.getLocalizedMessage());
            }
            if (count == 0) {
                logger.warn("Insert OntologyParameter failed for experimentPk {}, observationPk {}, parameterSource {}. Marking it as missing ...", experimentPk, observationPk, parameterSource);
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
            String phenotypingCenter,
            int phenotypingCenterPk,
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

            String filePathWithoutName = createNfsPathWithoutName(dccExperimentDTO, phenotypingCenter, parameterStableId);
            String fullResolutionFilePath = getFullResolutionFilePath(filePathWithoutName, seriesMediaParameterValue.getURI());

            parameterMap.clear();
            parameterMap.put("observationPk", observationPk);
            parameterMap.put("samplePk", samplePk);
            parameterMap.put("downloadFilePath", seriesMediaParameterValue.getURI().toLowerCase());
            parameterMap.put("imageLink", seriesMediaParameterValue.getLink());
            parameterMap.put("incrementValue", seriesMediaParameterValue.getIncrementValue());
            parameterMap.put("fileType", seriesMediaParameterValue.getFileType());
            parameterMap.put("mediaSampleLocalId", null);
            parameterMap.put("mediaSectionId", null);
            parameterMap.put("organisationPk", phenotypingCenterPk);
            parameterMap.put("fullResolutionFilePath", fullResolutionFilePath);

            try {
                count = jdbcCda.update(insert, parameterMap);
            } catch (Exception e) {
                logger.error("INSERT to image_record_observation table for SeriesMediaParameterValue failed for parameterStableId {}, observationType {}, observationPk {}, samplePk {}, downloadFilePath {}, imageLink {}, fileType {}, organisationPk {}, fullResolutionFilePath {}. Reason:\n\t{}",
                             parameterStableId, observationType.toString(), observationPk, samplePk, seriesMediaParameterValue.getURI(), seriesMediaParameterValue.getLink(),
                             seriesMediaParameterValue.getFileType(), phenotypingCenterPk, fullResolutionFilePath, e.getLocalizedMessage());
            }
            if (count == 0) {
                logger.warn("Insert SeriesMediaParameter failed for parameterSource {}. Marking it as missing ...", parameterSource);
                updateObservationMissingFlag(observationPk, true);
            } else {

                try {
                    // Save parameter associations
                    if (seriesMediaParameterValue.getParameterAssociation() != null && seriesMediaParameterValue.getParameterAssociation().size() > 0) {

                        for (ParameterAssociation parameterAssociation : seriesMediaParameterValue.getParameterAssociation()) {
                            Integer parameterAssociationPk = null;
                            try {
                                parameterAssociationPk = insertParameterAssociation(observationPk, parameterAssociation, simpleParameterList, ontologyParameterList);
                            } catch (DataIntegrityViolationException e) {
                                String value = "";
                                String associatedParameter = "";
                                for (SimpleParameter s : simpleParameterList) {
                                    if (s.getParameterID().equals(parameterAssociation.getParameterID())) {
                                        value = s.getValue();
                                        associatedParameter=s.getParameterID();
                                        break;
                                    }
                                }
                                logger.debug("Duplicate parameter association for specimen ID: {}, center: {}, parameterAssociation: {}->{}, value: {}",
                                        dccExperimentDTO.getSpecimenId(),
                                        dccExperimentDTO.getPhenotypingCenter(),
                                        parameterStableId,
                                        associatedParameter,
                                        value);
                            }

                            if (parameterAssociationPk != null) {

                                // Save Dimensions
                                if (parameterAssociation.getDim() != null && parameterAssociation.getDim().size() > 0) {
                                    for (Dimension dimension : parameterAssociation.getDim()) {
                                        insertDimension(parameterAssociationPk, dimension);
                                    }
                                }

                            }
                        }
                    }
                } catch (NullPointerException e) {
                    logger.error("Issue saving parameter association for parameterStableId {}, observationType {}, observationPk {}, samplePk {}, downloadFilePath {}, imageLink {}, fileType {}, organisationPk {}, fullResolutionFilePath {}. Reason:\n\t{}",
                            parameterStableId, observationType.toString(), observationPk, samplePk, seriesMediaParameterValue.getURI(), seriesMediaParameterValue.getLink(),
                            seriesMediaParameterValue.getFileType(), phenotypingCenterPk, fullResolutionFilePath, e.getLocalizedMessage());
                }

                // Save any procedure metadata
                insertProcedureMetadata(seriesMediaParameterValue.getProcedureMetadata(), dccExperimentDTO.getProcedureId(), experimentPk, observationPk);
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

        // Set the parameter association value. It is always a
        // SimpleParameter and/or multiple OntologyParameters where multiple values are allowed.
        // Loop through parameters to get the values for this parameterAssociation and if
        // multiple ontology values, combine into a comma separated list.
        String value = null;
        for (SimpleParameter sp : simpleParameterList) {
            String paramStableId = sp.getParameterID();
            if (paramStableId.equals(parameterAssociation.getParameterID())) {
                value = sp.getValue();
                break;
            }
        }
        for (OntologyParameter sp : ontologyParameterList) {
            String paramStableId = sp.getParameterID();
            if (paramStableId.equals(parameterAssociation.getParameterID())) {
                if (value != null) {
                    value +=","+StringUtils.join(sp.getTerm(), ",");
                } else {
                    value = StringUtils.join(sp.getTerm(), ",");
                }
                break;
            }
        }

        parameterMap.put("parameterAssociationValue", value);
        SqlParameterSource  parameterSource = new MapSqlParameterSource(parameterMap);

        int count = jdbcCda.update(insert, parameterSource, keyholder);
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

        String query =
                "INSERT INTO phenotyped_colony (" +
                        " colony_name," +
                        " es_cell_name," +
                        " gf_acc," +
                        " gf_db_id," +
                        " allele_symbol," +
                        " background_strain_name," +
                        " phenotyping_centre_organisation_id," +
                        " phenotyping_consortium_project_id," +
                        " production_centre_organisation_id," +
                        " production_consortium_project_id)" +
                        " VALUES (" +
                        " :colony_name," +
                        " :es_cell_name," +
                        " :gf_acc," +
                        " :gf_db_id," +
                        " :allele_symbol," +
                        " :background_strain_name," +
                        " :phenotyping_centre_organisation_id," +
                        " :phenotyping_consortium_project_id," +
                        " :production_centre_organisation_id," +
                        " :production_consortium_project_id)";

        // Insert PhenotypedColonies if they do not exist. Ignore any duplicates.
        for (PhenotypedColony phenotypedColony : phenotypedColonies) {
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("colony_name", phenotypedColony.getColonyName());
                parameterMap.put("es_cell_name", phenotypedColony.getEs_cell_name() == null ? null : phenotypedColony.getEs_cell_name());
                parameterMap.put("gf_acc", phenotypedColony.getGene().getId().getAccession());
                parameterMap.put("gf_db_id", DbIdType.MGI.intValue());
                parameterMap.put("allele_symbol", phenotypedColony.getAlleleSymbol());
                parameterMap.put("background_strain_name", phenotypedColony.getBackgroundStrain());
                parameterMap.put("phenotyping_centre_organisation_id", phenotypedColony.getPhenotypingCentre().getId());
                parameterMap.put("phenotyping_consortium_project_id", phenotypedColony.getPhenotypingConsortium().getId());
                parameterMap.put("production_centre_organisation_id", phenotypedColony.getProductionCentre().getId());
                parameterMap.put("production_consortium_project_id", phenotypedColony.getProductionConsortium().getId());

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
     * @return a strain, keyed by strain name or mgi accession id
     *
     */
    public Strain getStrainByNameOrMgiAccessionIdOrSynonym(String strainName) {

        Strain strain = strainsByNameOrMgiAccessionId.get(strainName);

        if (strain == null) {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("name", strainName);
            List<Strain> strainList = jdbcCda.query("SELECT * FROM strain WHERE (name=:name OR acc=:name)", parameterMap, new StrainRowMapper());

            if (strainList.size() == 1) {

                strain = strainList.get(0);
                strainsByNameOrMgiAccessionId.put(strainName, strain);

            } else if (strainList.size() > 1) {

                logger.warn("Expected 1, found "
                        + strainList.size()
                        + " strains for strain ID "
                        + strainName
                        + ". Using first strain"
                        + strainList.get(0)
                        + "\n List of strains found: " + StringUtils.join(strainList, ", "));

                strain = strainList.get(0);
                strainsByNameOrMgiAccessionId.put(strainName, strain);

            } else {

                strain = strainsBySynonym.get(strainName);

                if (strain == null) {

                    SqlRowSet srs = jdbcCda.queryForRowSet("SELECT strain.acc, synonym.symbol FROM strain INNER JOIN synonym ON strain.acc=synonym.acc WHERE (synonym.symbol=:name)", parameterMap);

                    Set<Pair<String, String>> strains = new HashSet<>();

                    while (srs.next()) {

                        String syn = srs.getString("synonym");
                        String acc = srs.getString("acc");
                        strains.add(new Pair<>(acc, syn));

                    }

                    if (strains.size() == 1) {
                        Pair<String, String> pair = new ArrayList<>(strains).get(0);
                        String acc = pair.getKey();
                        String syn = pair.getValue();

                        strain = strainsByNameOrMgiAccessionId.get(acc);

                        if (strain != null) {
                            strainsBySynonym.put(syn, strain);  // syn -> strain
                        }

                    } else if (strains.size() > 1) {

                        // 2017-09-18
                        // Checked in MGI and imits.  There are 2 synonyms with multiple strains associated at this
                        // time: HAR:3280, EM:03573 have synonyms "EPD0001_3_G07", " 129S/SvEvBrd-Tpm1<tm1a(EUCOMM)Wtsi>/WtsiH"

                        logger.warn("Expected 1, found "+ strains.size() + " strains for synonym of strain name " + strainName + ". Skipped.");
                    }
                }
            }
        }

        return strain;
    }


    /**
     * @return The full list of {@link Strain}, indexed by strain name
     */
    public Map<String, Strain> getStrainsByName() {
        Map<String, Strain> strains = new ConcurrentHashMap<>();

        Map<String, Object> parameterMap = new HashMap<>();
        List<Strain> strainList = jdbcCda.query("SELECT * FROM strain", parameterMap, new StrainRowMapper());
        for (Strain strain : strainList) {
            strains.put(strain.getName(), strain);
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

        Map<String, Integer> countsMap = new HashMap<>();
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
            datasource.setShortName(rs.getString("short_name"));
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
     * @return The set of derived parameters
     */
    public HashSet<String> getImpressDerivedParameters() {

        String query = "SELECT stable_id FROM phenotype_parameter WHERE derived = 1";
        List<String> results = jdbcCda.queryForList(query, new HashMap(), String.class);
        return new HashSet<>(results);
    }

    /**
     *
     * @return The set of metadata and data_analysis parameters
     */
    public HashSet<String> getImpressMetadataAndDataAnalysisParameters() {

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
     * Maps dcc zygosity string to cda zygosity string suitable for insertion into the cda database.
     *
     * @param dccZygosity The dcc zygosity string
     *
     * @return the cda zygosity string, or null if the dccZygosity is unknown.
     */
    public String getSpecimenLevelMutantZygosity(String dccZygosity) {

        String zygosity;
        switch (dccZygosity) {
            case "wild type":
            case "homozygous":
                zygosity = ZygosityType.homozygote.getName();
                break;
            case "heterozygous":
                zygosity = ZygosityType.heterozygote.getName();
                break;
            case "hemizygous":
                zygosity = ZygosityType.hemizygote.getName();
                break;

            default:
                String message = "Unknown dcc zygosity '" + dccZygosity + "'";
                logger.error(message);
                zygosity = null;
        }

        return zygosity;
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

            phenotypedColony.setBackgroundStrain(rs.getString("background_strain_name"));

            Organisation phenotypingCenter = new Organisation();
            phenotypingCenter.setId(rs.getInt("phenotyping_centre_organisation_id"));
            phenotypingCenter.setName(rs.getString("pc_phenotyping_centre_name"));
            phenotypedColony.setPhenotypingCentre(phenotypingCenter);

            Project phenotypingConsortium = new Project();
            phenotypingConsortium.setId(rs.getInt("phenotyping_consortium_project_id"));
            phenotypingConsortium.setName(rs.getString("pc_phenotyping_project_name"));
            phenotypedColony.setPhenotypingConsortium(phenotypingConsortium);

            Organisation productionCenter = new Organisation();
            productionCenter.setId(rs.getInt("production_centre_organisation_id"));
            productionCenter.setName(rs.getString("pc_production_centre_name"));
            phenotypedColony.setProductionCentre(productionCenter);

            Project productionConsortium = new Project();
            productionConsortium.setId(rs.getInt("production_consortium_project_id"));
            productionConsortium.setName(rs.getString("pc_production_project_name"));
            phenotypedColony.setProductionConsortium(productionConsortium);

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


    private String createNfsPathWithoutName(DccExperimentDTO dccExperimentDTO, String phenotypingCenter, String parameterStableId) {
        return phenotypingCenter + "/" + dccExperimentDTO.getPipeline() + "/" + dccExperimentDTO.getProcedureId() + "/" + parameterStableId;
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

    private Integer findBiologicalModel(int dbId, String allelicComposition, String geneticBackground, String zygosity) throws DataLoadException {

        final String find = "SELECT id FROM biological_model WHERE " +
                "db_id=:db_id AND allelic_composition=:allelic_composition AND genetic_background=:genetic_background AND zygosity=:zygosity";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("db_id", dbId);
        parameterMap.put("allelic_composition", allelicComposition);
        parameterMap.put("genetic_background", geneticBackground);
        parameterMap.put("zygosity", zygosity);

        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        Integer pk;
        try {
            pk = jdbcCda.queryForObject(find, parameterSource, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            // model not found
            return null;
        }

        if (pk != null && pk > 0) {
            return pk;
        }

        return null;
    }

   	private int insertBiologicalModel(int dbId, String allelicComposition, String geneticBackground, String zygosity) throws DataLoadException {

        final String insert = "INSERT INTO biological_model (" +
                "db_id,   allelic_composition,  genetic_background,  zygosity) VALUES (" +
                ":db_id, :allelic_composition, :genetic_background, :zygosity)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("db_id", dbId);
        parameterMap.put("allelic_composition", allelicComposition);
        parameterMap.put("genetic_background", geneticBackground);
        parameterMap.put("zygosity", zygosity);

        KeyHolder keyholder = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        int                      count;
        DataLoadException.DETAIL detail = DataLoadException.DETAIL.GENERAL_ERROR;

        try {

            count = jdbcCda.update(insert, parameterSource, keyholder);
            if (count > 0) {
                return keyholder.getKey().intValue();
            }

        } catch (DuplicateKeyException e) {
            detail = DataLoadException.DETAIL.DUPLICATE_KEY;
        }

        String message = "INSERT INTO biological_model failed for db_id " + dbId + ", allelic_composition " + allelicComposition + ", genetic_background " + geneticBackground + ", zygosity " + zygosity + "'. Skipping...";

        throw new DataLoadException(message, detail);
    }

    /**
     * Inserts a {@link List} of genes associated with {@code biologicalModelId} into the biological_model_genomic_feature table.
     * @param biologicalModelId biological model primary key
     * @param genes list of gene accession ids and db_ids to be inserted
     *              
     * @return The number of genes inserted
     * 
     * @throws DataLoadException If the gene(s) are already associated with this biological model
     */
    private int insertBiologicalModelGenes(int biologicalModelId, Set<AccDbId> genes) throws DataLoadException {

        int count = 0;

        final String insert = "INSERT INTO biological_model_genomic_feature (" +
                "biological_model_id,   gf_acc,  gf_db_id) VALUES (" +
                ":biological_model_id, :gf_acc, :gf_db_id)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("biological_model_id", biologicalModelId);

        for (AccDbId gene : genes) {
            parameterMap.put("gf_acc", gene.getAcc());
            parameterMap.put("gf_db_id", gene.getDbId());

            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            String message = "INSERT INTO biological_model_genomic_feature failed for biological_model_id " + biologicalModelId + ", gf_acc " + gene.getAcc() + ", gf_db_id " + gene.getDbId() + ". Skipping...";

            try {

                count += jdbcCda.update(insert, parameterSource, keyholder);

            } catch (DuplicateKeyException e) {

                throw new DataLoadException(message, DataLoadException.DETAIL.DUPLICATE_KEY);
            }
        }
        
        return count;
    }


    /**
     * Inserts a {@link List} of alleles associated with {@code biologicalModelId} into the biological_model_allele table.
     * @param biologicalModelId biological model primary key
     * @param alleles list of allele accession ids and db_ids to be inserted
     *
     * @return The number of alleles inserted
     *
     * @throws DataLoadException If the allele(s) are already associated with this biological model
     */
    private int insertBiologicalModelAlleles(int biologicalModelId, Set<AccDbId> alleles) throws DataLoadException {

        int count = 0;
        
        final String insert = "INSERT INTO biological_model_allele (" +
                "biological_model_id,   allele_acc,  allele_db_id) VALUES (" +
                ":biological_model_id, :allele_acc, :allele_db_id)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("biological_model_id", biologicalModelId);

        for (AccDbId allele : alleles) {
            parameterMap.put("allele_acc", allele.getAcc());
            parameterMap.put("allele_db_id", allele.getDbId());

            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            String message = "INSERT INTO biological_model_allele failed for biological_model_id " + biologicalModelId + ", allele_acc " + allele.getAcc() + ", allele_db_id " + allele.getDbId() + ". Skipping...";

            try {

                count += jdbcCda.update(insert, parameterSource, keyholder);

            } catch (DuplicateKeyException e) {

                throw new DataLoadException(message, DataLoadException.DETAIL.DUPLICATE_KEY);
            }
        }

        return count;
    }

    private int insertBiologicalModelStrains(int biologicalModelId, Set<AccDbId> strains) throws DataLoadException {

        int count = 0;
        
        final String insert = "INSERT INTO biological_model_strain (" +
                "biological_model_id,   strain_acc,  strain_db_id) VALUES (" +
                ":biological_model_id, :strain_acc, :strain_db_id)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("biological_model_id", biologicalModelId);

        for (AccDbId strain : strains) {
            parameterMap.put("strain_acc", strain.getAcc());
            parameterMap.put("strain_db_id", strain.getDbId());

            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            String message = "INSERT INTO biological_model_strain failed for biological_model_id " + biologicalModelId + ", strain_acc " + strain.getAcc() + ", strain_db_id " + strain.getDbId() + ". Skipping...";

            try {

                count += jdbcCda.update(insert, parameterSource, keyholder);

            } catch (DuplicateKeyException e) {

                throw new DataLoadException(message, DataLoadException.DETAIL.DUPLICATE_KEY);
            }
        }

        return count;
    }

    private int insertBiologicalModelPhenotypes(int biologicalModelId, Set<AccDbId> phenotypes) throws DataLoadException {

        int count = 0;
        
        final String insert = "INSERT INTO biological_model_phenotype (" +
                "biological_model_id,   phenotype_acc,  phenotype_db_id) VALUES (" +
                ":biological_model_id, :phenotype_acc, :phenotype_db_id)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("biological_model_id", biologicalModelId);

        for (AccDbId phenotype : phenotypes) {
            parameterMap.put("phenotype_acc", phenotype.getAcc());
            parameterMap.put("phenotype_db_id", phenotype.getDbId());

            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            String message = "INSERT INTO biological_model_phenotype failed for biological_model_id " + biologicalModelId + ", phenotype_acc " + phenotype.getAcc() + ", phenotype_db_id " + phenotype.getDbId() + ". Skipping...";

            try {

                count += jdbcCda.update(insert, parameterSource, keyholder);

            } catch (DuplicateKeyException e) {

                throw new DataLoadException(message, DataLoadException.DETAIL.DUPLICATE_KEY);
            }
        }

        return count;
    }
}