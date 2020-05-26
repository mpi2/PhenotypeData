/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *******************************************************************************/
package org.mousephenotype.cda.indexers;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.constants.ParameterConstants;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.pojo.PhenotypeAnnotationType;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.db.statistics.ResultDTO;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.utilities.PercentChangeStringParser;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Load documents into the statistical-results SOLR core
 */
@EnableAutoConfiguration
public class StatisticalResultsIndexer extends AbstractIndexer implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(StatisticalResultsIndexer.class);
    private Boolean SAVE = Boolean.TRUE;
    private Map<String, List<String>> impressAbnormals = new HashMap<>();

    private Double SIGNIFICANCE_THRESHOLD = Constants.P_VALUE_THRESHOLD;
    private final double REPORT_INTERVAL = 100000;

    static final String RESOURCE_3I = "3i";

    private final List<String> EMBRYO_PROCEDURES_NO_VIA = Arrays.asList("IMPC_GPL", "IMPC_GEL", "IMPC_GPM", "IMPC_GEM", "IMPC_GPO", "IMPC_GEO", "IMPC_GPP", "IMPC_GEP");
    private final List<String> EMBRYO_PROCEDURES_VIA = Arrays.asList("IMPC_EVL_001_001", "IMPC_EVM_001_001", "IMPC_EVO_001_001", "IMPC_EVP_001_001");

    private Map<Long, ImpressBaseDTO>     pipelineMap              = new HashMap<>();
    private Map<Long, ImpressBaseDTO>     procedureMap             = new HashMap<>();
    private Map<Long, ParameterDTO>       parameterMap             = new HashMap<>();
    private Map<String, ResourceBean>     resourceMap              = new HashMap<>();
    private Map<String, List<String>>     sexesMap                 = new HashMap<>();
    private Set<String>                   alreadyReported          = new HashSet<>();
    private Map<Long, BiologicalDataBean> biologicalDataMap        = new HashMap<>();
    private Map<String, Set<String>>      parameterMpTermMap       = new HashMap<>();
    private Map<String, String>           embryoSignificantResults = new HashMap<>();
    private Set<String>                   VIA_SIGNIFICANT          = new HashSet<>();
    private Set<String>                   MALE_FER_SIGNIFICANT     = new HashSet<>();
    private Set<String>                   FEMALE_FER_SIGNIFICANT   = new HashSet<>();
    private List<String>                  shouldHaveAdded          = new ArrayList<>();

    private Set<String> uniqueSRKeys = new ConcurrentSkipListSet<>();

    public void setPipelineMap(Map<Long, ImpressBaseDTO> pipelineMap) {
        this.pipelineMap = pipelineMap;
    }
    public void setProcedureMap(Map<Long, ImpressBaseDTO> procedureMap) {
        this.procedureMap = procedureMap;
    }
    public void setParameterMap(Map<Long, ParameterDTO> parameterMap) {
        this.parameterMap = parameterMap;
    }

    private OntologyParser        mpParser;
    private OntologyParser        mpMaParser;
    private OntologyParser        maParser;
    private OntologyParserFactory ontologyParserFactory;

    private MpTermService       mpTermService;
    private ParameterRepository parameterRepository;
    private SolrClient          statisticalResultCore;

    protected StatisticalResultsIndexer() {

    }

    @Inject
    public StatisticalResultsIndexer(
            @NotNull DataSource komp2DataSource,
            @NotNull OntologyTermRepository ontologyTermRepository,
            @NotNull MpTermService mpTermService,
            @NotNull ParameterRepository parameterRepository,
            @NotNull SolrClient statisticalResultCore)
    {
        super(komp2DataSource, ontologyTermRepository);
        this.mpTermService = mpTermService;
        this.parameterRepository = parameterRepository;
        this.statisticalResultCore = statisticalResultCore;
    }

    public void setMpParser(OntologyParser mpParser) {
        this.mpParser = mpParser;
    }

    public void setMpMaParser(OntologyParser mpMaParser) {
        this.mpMaParser = mpMaParser;
    }

    public void setMaParser(OntologyParser maParser) {
        this.maParser = maParser;
    }

    public OntologyParserFactory getOntologyParserFactory() {
        return ontologyParserFactory;
    }

    public void setOntologyParserFactory(OntologyParserFactory ontologyParserFactory) {
        this.ontologyParserFactory = ontologyParserFactory;
    }


    @Override
    public RunStatus validateBuild() throws IndexerException {
        return super.validateBuild(statisticalResultCore);
    }



    @Override
    public RunStatus run() throws IndexerException, IOException {

        long start = System.currentTimeMillis();
        RunStatus runStatus = new RunStatus();

        try {
            Connection connection = komp2DataSource.getConnection();

            synchronized(this) {

                ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);

                mpParser = ontologyParserFactory.getMpParser();
                mpMaParser = ontologyParserFactory.getMpMaParser();
                maParser = ontologyParserFactory.getMaParser();

                pipelineMap = IndexerMap.getImpressPipelines(connection);
                procedureMap = IndexerMap.getImpressProcedures(connection);
                parameterMap = IndexerMap.getImpressParameters(connection);
            }

            populateBiologicalDataMap();
            populateResourceDataMap();
            populateSexesMap();
            populateParameterMpTermMap();
            populateEmbryoSignificanceMap();
            populateAdultLineLevelSignificanceMap();

        } catch (SQLException | OWLOntologyCreationException | OWLOntologyStorageException e) {
            throw new IndexerException(e);
        }

        expectedDocumentCount = populateStatisticalResultsSolrCore();

        logger.info((SAVE?"":"Would have") + " Added {} total beans in {}", expectedDocumentCount, commonUtils.msToHms(System.currentTimeMillis() - start));
        return runStatus;
    }

    private int populateStatisticalResultsSolrCore() throws IndexerException {

        int count = 0;

        try {

            if (SAVE) statisticalResultCore.deleteByQuery("*:*");
            if (SAVE) statisticalResultCore.commit();

            List<Callable<List<StatisticalResultDTO>>> resultGenerators = Arrays.asList(
                    getViabilityResults()
                    , getFertilityResults()
                    , getReferenceRangePlusResults()
                    , getEmbryoViabilityResults()
                    , getEmbryoResults()
                    , getGrossPathologyResults()
                    , getUnidimensionalResults()
                    , getCategoricalResults()
            );

            ExecutorService pool = Executors.newFixedThreadPool(4);
            List<Future<List<StatisticalResultDTO>>> producers = new ArrayList<>();

            for (Callable<List<StatisticalResultDTO>> r : resultGenerators) {

                Future<List<StatisticalResultDTO>> future = pool.submit(r);
                producers.add(future);

            }

            AtomicInteger atomicInt = new AtomicInteger(0);
            for (Future<List<StatisticalResultDTO>> future : producers) {

                try {
                    atomicInt.addAndGet(future.get().size());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Stop threadpool
            pool.shutdown();

            count = atomicInt.get();

            if (SAVE) statisticalResultCore.commit();
            checkSolrCount(count);

            logger.info((SAVE?"":"Would have") + " Added {} statistical result documents", count);

        } catch (IOException | SolrServerException e) {
            throw new IndexerException(e);
        }

        return count;
    }


    public ViabilityResults getViabilityResults() {return new ViabilityResults(); }
    public FertilityResults getFertilityResults() {return new FertilityResults(); }
    public ReferenceRangePlusResults getReferenceRangePlusResults() {return new ReferenceRangePlusResults(); }
    public UnidimensionalResults getUnidimensionalResults() {return new UnidimensionalResults(); }
    public CategoricalResults getCategoricalResults() {return new CategoricalResults(); }
    public EmbryoViabilityResults getEmbryoViabilityResults() {return new EmbryoViabilityResults(); }
    public EmbryoResults getEmbryoResults() {return new EmbryoResults(); }
    public GrossPathologyResults getGrossPathologyResults() {return new GrossPathologyResults(); }


    /**
     * Check to see if the count of documents we think have been added actually matches
     * the number of documents in solr
     *
     * @param documentsAddedCount The number of documents added
     */
    private void checkSolrCount(Integer documentsAddedCount) throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();
        query.setQuery("*:*").setRows(0);
        QueryResponse response = statisticalResultCore.query(query);
        Long solrDocumentCount = response.getResults().getNumFound();

        if (documentsAddedCount - solrDocumentCount != 0) {
            logger.warn("  Count of documents in solr: {}, count added by indexer: {}, Difference: {}", solrDocumentCount, documentsAddedCount, documentsAddedCount - solrDocumentCount);
        }

        if (documentsAddedCount - solrDocumentCount > 0) {

            // The java Set.add() method returns false when attempting to add an element that already exists in
            // the set so the filter will remove all non-duplicate elements leaving only those document IDs that
            // have been added twice
            Set<String> uniques = new HashSet<>();
            Set<String> diff = shouldHaveAdded
                    .stream()
                    .filter(e -> ! uniques.add(e))
                    .collect(Collectors.toSet());

            logger.warn(" Should have added these {} doc IDs, but missing from solr {}", diff.size(), StringUtils.join(diff, ", "));
        }
    }

    private Double nullCheckResult(ResultSet r,  String field) throws SQLException {
        double v = r.getDouble(field);
        return r.wasNull() ? null : v;
    }

    private StatisticalResultDTO parseResultCommonFields(ResultSet r) throws SQLException {

        StatisticalResultDTO doc = new StatisticalResultDTO();

        doc.setDocId(r.getString("doc_id"));
        doc.setDataType(r.getString("data_type"));

        // Experiment details

        // Use the procedure prefix to associated with the result to find the procedure prefix
        String procedurePrefix = StringUtils.join(Arrays.asList(procedureMap.get(r.getLong("procedure_id")).getStableId().split("_")).subList(0, 2), "_");

        if (ParameterConstants.source3iProcedurePrefixes.contains(procedurePrefix)) {
            // Override the resource for the 3i procedures
            doc.setResourceId(resourceMap.get(RESOURCE_3I).id);
            doc.setResourceName(resourceMap.get(RESOURCE_3I).shortName);
            doc.setResourceFullname(resourceMap.get(RESOURCE_3I).name);
        } else {
            doc.setResourceId(r.getLong("resource_id"));
            doc.setResourceName(r.getString("resource_name"));
            doc.setResourceFullname(r.getString("resource_fullname"));
        }

        doc.setProjectId(r.getLong("project_id"));
        doc.setProjectName(r.getString("project_name"));
        doc.setPhenotypingCenter(r.getString("phenotyping_center"));
        doc.setControlBiologicalModelId(r.getLong("control_id"));
        doc.setMutantBiologicalModelId(r.getLong("experimental_id"));
        doc.setZygosity(r.getString("experimental_zygosity"));
        doc.setDependentVariable(r.getString("dependent_variable"));
        doc.setExternalDbId(r.getLong("external_db_id"));
        doc.setDbId(r.getLong("db_id"));
        doc.setOrganisationId(r.getLong("organisation_id"));
        doc.setPhenotypingCenterId(r.getLong("phenotyping_center_id"));

        doc.setControlSelectionMethod(r.getString("control_selection_strategy"));
        doc.setStatisticalMethod(r.getString("statistical_method"));
        doc.setWorkflow(r.getString("workflow"));
        doc.setMaleControlCount(r.getInt("male_controls"));
        doc.setFemaleControlCount(r.getInt("female_controls"));
        doc.setMaleMutantCount(r.getInt("male_mutants"));
        doc.setFemaleMutantCount(r.getInt("female_mutants"));
        doc.setColonyId(r.getString("colony_id"));
        doc.setStatus(r.getString("status"));

        if (doc.getPhenotypeSex() == null) {
            doc.setPhenotypeSex(new ArrayList<>());
        }

        if (doc.getMaleMutantCount()>0) {
            doc.getPhenotypeSex().add(SexType.male.getName());
        }

        if (doc.getFemaleMutantCount()>0) {
            doc.getPhenotypeSex().add(SexType.female.getName());
        }

        // Always set a metadata group here to allow for simpler searching for
        // unique results and to maintain parity with the observation index
        // where "empty string" metadata group means no required metadata.
        if (StringUtils.isNotEmpty(r.getString("metadata_group"))) {
            doc.setMetadataGroup(r.getString("metadata_group"));
        } else {
            doc.setMetadataGroup("");
        }

        addImpressData(r, doc);

        // Biological details
        addBiologicalData(doc, doc.getMutantBiologicalModelId());

        final OntologyTerm lifeStage = getLifeStage(doc.getParameterStableId());

        if (lifeStage != null) {
            doc.setLifeStageAcc(lifeStage.getId().getAccession());
            doc.setLifeStageName(lifeStage.getName());
        } else {
            logger.info("  Life stage is NULL for doc id " + doc.getDocId());
        }

        // MP Terms must come after setting the stage as it's used for selecting MA or EMAPA
        addMpTermData(r, doc);

        return doc;
    }


    /**
     * parseLineResult changes a database result set for a line into a solr document
     *
     * @param r the result set
     * @return a solr document
     */
    private StatisticalResultDTO parseLineResult(ResultSet r) throws SQLException {

        StatisticalResultDTO doc = new StatisticalResultDTO();

        String docId = r.getString("doc_id");
        if (docId == null) {
            docId = String.valueOf(Math.random());
        }

        doc.setDocId(docId);
        doc.setDataType(r.getString("data_type"));
        doc.setResourceId(r.getLong("resource_id"));
        doc.setResourceName(r.getString("resource_name"));
        doc.setResourceFullname(r.getString("resource_fullname"));
        doc.setProjectId(r.getLong("project_id"));
        doc.setProjectName(r.getString("project_name"));
        doc.setPhenotypingCenter(r.getString("phenotyping_center"));
        doc.setMutantBiologicalModelId(r.getLong("biological_model_id"));
        doc.setZygosity(r.getString("experimental_zygosity"));
        doc.setDependentVariable(r.getString("dependent_variable"));
        doc.setExternalDbId(r.getLong("external_db_id"));
        doc.setDbId(r.getLong("db_id"));
        doc.setPhenotypingCenterId(r.getLong("phenotyping_center_id"));

        doc.setStatisticalMethod("Supplied as data");
        doc.setColonyId(r.getString("colony_id"));
        doc.setStatus("Success");

        // Need to set sgnificance if a phenotype association has been made for this data set
        doc.setSignificant(false);


        // Always set a metadata group here to allow for simpler searching for
        // unique results and to maintain parity with the observation index
        // where "empty string" metadata group means no required metadata.
        if (StringUtils.isNotEmpty(r.getString("metadata_group"))) {
            doc.setMetadataGroup(r.getString("metadata_group"));
        } else {
            doc.setMetadataGroup("");
        }

        // Fertility results DO NOT contain the counts of controls/mutants
        switch (r.getString("dependent_variable")) {
            case "IMPC_VIA_001_001":
                doc.setMaleMutantCount(r.getInt("male_mutants"));
                doc.setFemaleMutantCount(r.getInt("female_mutants"));

                // Viability parameter significant for both sexes
                doc.setPhenotypeSex(Arrays.asList("female", "male"));

                if (VIA_SIGNIFICANT.contains(doc.getColonyId())) {
                    doc.setSignificant(true);
                }

                break;
            case "IMPC_FER_001_001":
                // Fertility significant for Males
                doc.setPhenotypeSex(Collections.singletonList("male"));

                if (MALE_FER_SIGNIFICANT.contains(doc.getColonyId())) {
                    doc.setSignificant(true);
                }

                break;
            case "IMPC_FER_019_001":
                // Fertility significant for females
                doc.setPhenotypeSex(Collections.singletonList("female"));

                if (FEMALE_FER_SIGNIFICANT.contains(doc.getColonyId())) {
                    doc.setSignificant(true);
                }

                break;
        }

        // Impress pipeline data details
        addImpressData(r, doc);

        // Biological details
        addBiologicalData(doc, doc.getMutantBiologicalModelId());

        OntologyTerm lifeStage = getLifeStage(doc.getParameterStableId());
        if (lifeStage != null) {
            doc.setLifeStageAcc(lifeStage.getId().getAccession());
            doc.setLifeStageName(lifeStage.getName());
        } else {
            logger.info("  Line result stage is NULL for doc id " + doc.getDocId());
        }


        // MP Term details
        addMpTermData(r, doc);

        try {
            String category = r.getString("category");
            if (!r.wasNull() && category.equals("Insufficient numbers to make a call")) {
                doc.setStatus("Failed - " + category);
            }
        } catch (java.sql.SQLException e) {
            // do nothing. Result set did not have "category" in it
        }

        try {
            r.getString("experimental_zygosity");
            if (r.wasNull()) {
                String category = r.getString("category");
                if (!r.wasNull()) {
                    String[] fields = category.split("-");

                    ZygosityType zygosity;
                    switch (fields[0].trim().toLowerCase()) {
                        case "heterozygous":
                            zygosity = ZygosityType.heterozygote;
                            break;
                        case "hemizygous":
                            zygosity = ZygosityType.hemizygote;
                            break;
                        case "homozygous":
                        default:
                            zygosity = ZygosityType.homozygote;
                            break;
                    }

                    doc.setZygosity(zygosity.getName());
                }
            }
        } catch (java.sql.SQLException e) {
            // do nothing. Result set did not have "category" in it
        }

        String sex = r.getString("sex");
        if (!r.wasNull()) {

            doc.setSex(sex);

            // Do not attempt to add to the phenotye_sex fuield if it has been manually set as it is for
            // the viability and fertility parameters listed here
            if ( ! Arrays.asList("IMPC_VIA_001_001", "IMPC_FER_001_001", "IMPC_FER_019_001").contains(doc.getDependentVariable())) {
                // Add the sex to the phenotype_sexes field
                if (doc.getPhenotypeSex() == null) {
                    doc.setPhenotypeSex(new ArrayList<>());
                }

                if (!doc.getPhenotypeSex().contains(sex)) {
                    doc.getPhenotypeSex().add(sex);
                }
            }

        }

        Double p_value = r.getDouble("p_value");
        if (!r.wasNull() && doc.getMpTermId()!=null) {
            doc.setpValue(p_value);
        }

        Double effect_size = r.getDouble("effect_size");
        if (!r.wasNull() && doc.getMpTermId()!=null) {
            doc.setEffectSize(effect_size);
        }

        return doc;
    }


    /**
     * Add the appropriate MP term associations to the document
     * This is only used for the embryo data for the moment (2016-04-07)
     *
     * @param mpId the mp term accession id
     * @param doc the solr document to update
     */
    private void addMpTermData(String mpId, StatisticalResultDTO doc) {

        // Add the appropriate fields for the global MP term
        if (mpId != null) {

            OntologyTermDTO mpTerm = mpParser.getOntologyTerm(mpId);
            if (mpTerm != null) {
                doc.setMpTermId(mpTerm.getAccessionId());
                doc.setMpTermName(mpTerm.getName());

                // if the mpId itself is a top level, add itself as a top level
                if (mpTerm.getTopLevelIds() == null ){
                    // if the mpId itself is a top level, add itself as a top level
                    doc.addTopLevelMpTermId(mpTerm.getAccessionId());
                    doc.addTopLevelMpTermName(mpTerm.getName());
                }
                else {
                    doc.addTopLevelMpTermId(mpTerm.getTopLevelIds());
                    doc.addTopLevelMpTermName(mpTerm.getTopLevelNames());
                }

                doc.addIntermediateMpTermId(mpTerm.getIntermediateIds());
                doc.addIntermediateMpTermName(mpTerm.getIntermediateNames());

                addAnatomyMapping(doc, mpTerm);
            }
        }
    }

    private void addAnatomyMapping(StatisticalResultDTO doc, OntologyTermDTO mpTerm){

        // mp-anatomy mappings (all MA at the moment)
        // For all non-embryo life stages indicated by not containing a digit
		if (doc.getLifeStageAcc() != null && ! doc.getLifeStageAcc().matches("[0-9]")) {
			Set<String> referencedClasses = mpMaParser.getReferencedClasses(doc.getMpTermId(),
                    OntologyParserFactory.VIA_PROPERTIES, "MA");
			if (referencedClasses != null && referencedClasses.size() > 0) {
				for (String id : referencedClasses) {
					OntologyTermDTO maTerm = maParser.getOntologyTerm(id);

					if (maTerm != null) {
						doc.addAnatomyTermId(id);
						doc.addAnatomyTermName(maTerm.getName());
						if (maTerm.getIntermediateIds() != null) {
							doc.addIntermediateAnatomyTermId(maTerm.getIntermediateIds());
							doc.addIntermediateAnatomyTermName(maTerm.getIntermediateNames());
						}
						if (maTerm.getTopLevelIds() != null) {
							doc.addTopLevelAnatomyTermId(maTerm.getTopLevelIds());
							doc.addTopLevelAnatomyTermName(maTerm.getTopLevelNames());
						}
					}else{
						logger.info("MA term is null for id:"+doc.getMpTermId());
					}
				}
			}
			// Also check mappings up the tree, as a leaf term might not have a
			// mapping, but the parents might.
			Set<String> anatomyIdsForAncestors = new HashSet<>();
			for (String mpAncestorId : mpTerm.getIntermediateIds()) {
				if (mpMaParser.getReferencedClasses(mpAncestorId, OntologyParserFactory.VIA_PROPERTIES, "MA") != null) {
					anatomyIdsForAncestors.addAll(
							mpMaParser.getReferencedClasses(mpAncestorId, OntologyParserFactory.VIA_PROPERTIES, "MA"));
				}
			}

			for (String id : anatomyIdsForAncestors) {
				OntologyTermDTO maTerm = maParser.getOntologyTerm(id);
				if (maTerm != null) {
					doc.addIntermediateAnatomyTermId(id);
					doc.addIntermediateAnatomyTermName(maTerm.getName());

					if (maTerm.getIntermediateIds() != null) {
						doc.addIntermediateAnatomyTermId(maTerm.getIntermediateIds());
						doc.addIntermediateAnatomyTermName(maTerm.getIntermediateNames());
					}
					if (maTerm.getTopLevelIds() != null) {
						doc.addTopLevelAnatomyTermId(maTerm.getTopLevelIds());
						doc.addTopLevelAnatomyTermName(maTerm.getTopLevelNames());
					}
				}else{
					logger.info("maTerm is null when looking for anatomyIdsForAncestors id:"+id);
				}
			}
		}

    }


    String getResult(StatisticalResultDTO doc, ResultSet resultSet) {

        String mpTerm = null;

        ResultDTO result = new ResultDTO();
        try {
            result.setPipelineId(resultSet.getLong("pipeline_id"));
            result.setProcedureId(resultSet.getLong("procedure_id"));
            result.setParameterId(resultSet.getLong("parameter_id"));
            result.setParameterStableId(resultSet.getString("dependent_variable"));

            result.setNullTestPvalue(resultSet.getDouble("categorical_p_value"));
            result.setGenotypeEffectSize(resultSet.getDouble("categorical_effect_size"));


            try {
                result.setSex(SexType.valueOf(resultSet.getString("sex")));
            } catch (Exception e) {
                result.setSex(null);
            }

            SqlUtils sqlUtils = new SqlUtils();
            Boolean additionalColumns;
            try (Connection conn = komp2DataSource.getConnection()) {
                additionalColumns = sqlUtils.columnInSchemaMysql(conn, "stats_categorical_result", "male_p_value");
            }

            if (additionalColumns) {
                result.setMalePvalue(resultSet.getDouble("male_p_value"));
                result.setMaleEffectSize(resultSet.getDouble("male_effect_size"));

                result.setFemalePvalue(resultSet.getDouble("female_p_value"));
                result.setFemaleEffectSize(resultSet.getDouble("female_effect_size"));
            }

            result.setCategoryA(resultSet.getString("category_a"));
            result.setCategoryB(resultSet.getString("category_b"));


            if (result.getCategoryA() == null) {
                result.setCategoryA("abnormal");
                result.setCategoryB("normal");
            }

            try (Connection connection = komp2DataSource.getConnection()) {
                OntologyTerm term = mpTermService.getMPTerm(
                        doc.getParameterStableId(),
                        result,
                        doc.getSex() == null ? null : SexType.valueOf(doc.getSex()),
                        connection,
                        0.0001f,
                        Boolean.TRUE);

                if (term != null) {
                    mpTerm = term.getId().getAccession();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return mpTerm;
    }

    /**
     * Add the appropriate MP term associations to the document
     *
     * @param r   the result set to pull the relevant fields from
     * @param doc the solr document to update
     * @throws SQLException if the query fields do not exist
     */
    private void addMpTermData(ResultSet r, StatisticalResultDTO doc) throws SQLException {


        String mpTerm = r.getString("mp_acc");

        // For reference range plus results only, test that the MP term has been set, if not, try to set the abnormal termif (doc.getStatisticalMethod() != null && doc.getStatisticalMethod().equals("Reference Ranges Plus framework")) {

        // Sometimes, the stats result generator doesn't set the MP term (also not for either sex), in that case,
        // try to set the abnormal term for the parameter

        if (r.wasNull()) {

            // If there is a not male MP term set
            r.getString("male_mp_acc");
            if (r.wasNull()) {

                // And, If there is a not female MP term set
                r.getString("female_mp_acc");
                if (r.wasNull()) {

                    // Lookup and cache the impress object corresponding to the parameter in question
                    if (!impressAbnormals.containsKey(doc.getParameterStableId())) {

                        Parameter parameter = parameterRepository.getFirstByStableId(doc.getParameterStableId());

                        List<String> abnormalMpIds = parameter.getAnnotations()
                                .stream()
                                .filter(x -> x.getType().equals(PhenotypeAnnotationType.abnormal))
                                .map(x -> x.getOntologyTerm().getId().getAccession())
                                .collect(Collectors.toList());

                        impressAbnormals.put(doc.getParameterStableId(), abnormalMpIds);
                    }

                    // Get the first abnormal term ID as that is likely the real "abnormal" term
                    if (impressAbnormals.containsKey(doc.getParameterStableId())) {
                        List<String> abnormals = impressAbnormals.get(doc.getParameterStableId());
                        if (CollectionUtils.isNotEmpty(abnormals)) {
                            mpTerm = abnormals.get(0);
                        }
                    }
                }
            }
        }

        // If after all that the mp_term is still null, this is probably a poorly loaded statistical result
        // Try to determine the correct MP term by parsing the result again
        if (mpTerm == null && doc.getDataType().contains("categorical")) {
            mpTerm = getResult(doc, r);
        }



            // Add the appropriate fields for the global MP term
        if (mpTerm != null) {

            addMpTermData(mpTerm, doc);

        }

        // Process the male MP term
        mpTerm = r.getString("male_mp_acc");
        if (!r.wasNull()) {

            OntologyTermDTO term = mpParser.getOntologyTerm(mpTerm);
            if (term != null) {
                doc.setMaleMpTermId(term.getAccessionId());
                doc.setMaleMpTermName(term.getName());

                doc.addMaleTopLevelMpTermId(term.getTopLevelIds());
                doc.addMaleTopLevelMpTermName(term.getTopLevelNames());

                doc.addMaleIntermediateMpTermId(term.getIntermediateIds());
                doc.addMaleIntermediateMpTermName(term.getIntermediateNames());
            }
        }

        // Process the female MP term
        mpTerm = r.getString("female_mp_acc");
        if (!r.wasNull()) {

            OntologyTermDTO term = mpParser.getOntologyTerm(mpTerm);
            if (term != null) {
                doc.setFemaleMpTermId(term.getAccessionId());
                doc.setFemaleMpTermName(term.getName());

                doc.addFemaleTopLevelMpTermId(term.getTopLevelIds());
                doc.addFemaleTopLevelMpTermName(term.getTopLevelNames());

                doc.addFemaleIntermediateMpTermId(term.getIntermediateIds());
                doc.addFemaleIntermediateMpTermName(term.getIntermediateNames());
            }
        }

    }


    private void addImpressData(ResultSet r, StatisticalResultDTO doc)
            throws SQLException {

        doc.setPipelineId(pipelineMap.get(r.getLong("pipeline_id")).getId());
        doc.setPipelineStableKey(pipelineMap.get(r.getLong("pipeline_id")).getStableKey());
        doc.setPipelineName(pipelineMap.get(r.getLong("pipeline_id")).getName());
        doc.setPipelineStableId(pipelineMap.get(r.getLong("pipeline_id")).getStableId());
        doc.setProcedureId(procedureMap.get(r.getLong("procedure_id")).getId());
        doc.setProcedureStableKey(procedureMap.get(r.getLong("procedure_id")).getStableKey());
        doc.setProcedureName(procedureMap.get(r.getLong("procedure_id")).getName());
        doc.setProcedureStableId(procedureMap.get(r.getLong("procedure_id")).getStableId());
        doc.setParameterId(parameterMap.get(r.getLong("parameter_id")).getId());
        doc.setParameterStableKey(parameterMap.get(r.getLong("parameter_id")).getStableKey());
        doc.setParameterName(parameterMap.get(r.getLong("parameter_id")).getName());
        doc.setParameterStableId(parameterMap.get(r.getLong("parameter_id")).getStableId());

        // Create field that contains all possible MP terms (including intermediate and top level terms)
        // that this parameter can produce
        Set<String> mpIds = parameterMpTermMap.get(doc.getParameterStableId());

        if (mpIds != null) {

            mpIds.forEach(mpId -> {

                OntologyTermDTO term = mpParser.getOntologyTerm(mpId);
                if (term !=null && term.getAccessionId() != null){
                    doc.addMpTermIdOptions(term.getAccessionId());
                    doc.addMpTermNameOptions(term.getName());
                    doc.addMpTermIdOptions(term.getIntermediateIds());
                    doc.addMpTermNameOptions(term.getIntermediateNames());
                }else{
                	logger.debug("term is null in indexer for mpId"+mpId);
                }

            });

        } else {

            String p = doc.getParameterStableId();
            if ( ! alreadyReported.contains(p)) {
                alreadyReported.add(p);
                logger.debug(" Cannot find MP terms for parameter {}", p);
            }

        }

    }


    private void addBiologicalData(StatisticalResultDTO doc, Long biologicalModelId) {

        BiologicalDataBean b = biologicalDataMap.get(biologicalModelId);

        if (b == null) {
            logger.error(" Cannot find genomic information for biological_model_id {}", biologicalModelId);
            return;
        }

        doc.setMarkerAccessionId(b.geneAcc);
        doc.setMarkerSymbol(b.geneSymbol);
        doc.setAlleleAccessionId(b.alleleAccession);
        doc.setAlleleName(b.alleleName);
        doc.setAlleleSymbol(b.alleleSymbol);
        doc.setStrainAccessionId(b.strainAcc);
        doc.setStrainName(b.strainName);
        doc.setGeneticBackground(b.geneticBackground);

    }


    /**
     * Add all the relevant data required quickly looking up biological data
     * associated to a biological sample
     *
     * @throws SQLException when a database exception occurs
     */
    public void populateBiologicalDataMap() throws SQLException {

        String query = "SELECT bm.id, "
                + "strain.acc AS strain_acc, strain.name AS strain_name, bm.genetic_background, "
                + "(SELECT DISTINCT allele_acc FROM biological_model_allele bma WHERE bma.biological_model_id=bm.id) AS allele_accession, "
                + "(SELECT DISTINCT a.symbol FROM biological_model_allele bma INNER JOIN allele a ON (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bm.id) AS allele_symbol, "
                + "(SELECT DISTINCT a.name FROM biological_model_allele bma INNER JOIN allele a ON (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bm.id) AS allele_name, "
                + "(SELECT DISTINCT gf_acc FROM biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=bm.id) AS acc, "
                + "(SELECT DISTINCT gf.symbol FROM biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf ON gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bm.id) AS symbol "
                + "FROM biological_model bm "
                + "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bm.id "
                + "INNER JOIN strain ON strain.acc=bmstrain.strain_acc "
                + "WHERE exists(SELECT DISTINCT gf.symbol FROM biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf ON gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bm.id)";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

            p.setFetchSize(Integer.MIN_VALUE);
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                BiologicalDataBean b = new BiologicalDataBean();

                b.alleleAccession = resultSet.getString("allele_accession");
                b.alleleSymbol = resultSet.getString("allele_symbol");
                b.alleleName = resultSet.getString("allele_name");
                b.geneAcc = resultSet.getString("acc");
                b.geneSymbol = resultSet.getString("symbol");
                b.strainAcc = resultSet.getString("strain_acc");
                b.strainName = resultSet.getString("strain_name");
                b.geneticBackground = resultSet.getString("genetic_background");

                biologicalDataMap.put(resultSet.getLong("id"), b);
            }
        }
        logger.info(" Mapped {} biological data entries", biologicalDataMap.size());

    }


    /**
     * Add all the relevant data required quickly looking up biological data
     * associated to a biological sample
     *
     * @throws SQLException when a database exception occurs
     */
    public void populateResourceDataMap() throws SQLException {

        String query = "SELECT id, name, short_name FROM external_db";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

            p.setFetchSize(Integer.MIN_VALUE);
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                ResourceBean b = new ResourceBean();
                b.id = resultSet.getLong("id");
                b.name = resultSet.getString("name");
                b.shortName = resultSet.getString("short_name");
                resourceMap.put(resultSet.getString("short_name"), b);
            }
        }
        logger.info(" Mapped {} resource data entries", resourceMap.size());
    }

    /**
     * Add all the relevant data required quickly looking up biological data
     * associated to a biological sample
     *
     * @throws SQLException when a database exception occurs
     */
    public void populateSexesMap() throws SQLException {

        List<String> queries = Arrays.asList(
                "SELECT CONCAT('unidimensional-', s.id) AS id, GROUP_CONCAT(distinct p.sex) as sexes FROM stats_unidimensional_results s INNER JOIN stat_result_phenotype_call_summary r ON r.unidimensional_result_id=s.id INNER JOIN phenotype_call_summary p ON p.id=r.phenotype_call_summary_id GROUP BY s.id",
                "SELECT CONCAT('categorical-', s.id) AS id, GROUP_CONCAT(distinct p.sex) as sexes FROM stats_categorical_results s INNER JOIN stat_result_phenotype_call_summary r ON r.categorical_result_id=s.id INNER JOIN phenotype_call_summary p ON p.id=r.phenotype_call_summary_id GROUP BY s.id",
                "SELECT CONCAT('rrplus-', s.id) AS id, GROUP_CONCAT(distinct p.sex) as sexes FROM stats_rrplus_results s INNER JOIN stat_result_phenotype_call_summary r ON r.rrplus_result_id=s.id INNER JOIN phenotype_call_summary p ON p.id=r.phenotype_call_summary_id GROUP BY s.id"
        );

        for (String query : queries) {
            try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

                p.setFetchSize(Integer.MIN_VALUE);
                ResultSet resultSet = p.executeQuery();

                while (resultSet.next()) {
                    List<String> sexes = new ArrayList<>(
                            Arrays.asList(
                                    resultSet.getString("sexes")
                                            .replaceAll(" ", "")
                                            .split(",")));

                    sexesMap.put(resultSet.getString("id"), sexes);
                }
            }
        }
        logger.info(" Mapped {} sexes data entries", sexesMap.size());
    }

    /**
     * The embryo significance map keys are document IDs that should match the embryo documents and the key is the MP
     * acc
     */
    public void populateEmbryoSignificanceMap() throws SQLException {

        // Generate MySQL REGEX string to include all embryo parameters
        Set<String> allEmbryoProcedures = new HashSet<>();
        allEmbryoProcedures.addAll(EMBRYO_PROCEDURES_NO_VIA);
        allEmbryoProcedures.addAll(EMBRYO_PROCEDURES_VIA);
        String embryoProcedures = StringUtils.join(allEmbryoProcedures, "|");

        // Populate the significant results map with this query
        String sigResultsQuery = "SELECT CONCAT(parameter.stable_id, '_', pcs.colony_id, pcs.organisation_id) AS doc_id, mp_acc " +
                "FROM phenotype_call_summary pcs " +
                "INNER JOIN phenotype_parameter parameter ON parameter.id = pcs.parameter_id " +
                "WHERE parameter.stable_id REGEXP '" + embryoProcedures + "' AND pcs.mp_acc IS NOT NULL";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(sigResultsQuery)) {
            ResultSet r = p.executeQuery();
            while (r.next()) {

                String docId = r.getString("doc_id");
                String mpAcc = r.getString("mp_acc");

                embryoSignificantResults.put(docId, mpAcc);
            }
        }

        logger.info(" Mapped {} embryo significant data entries", embryoSignificantResults.size());

    }

    void populateAdultLineLevelSignificanceMap() throws SQLException {

        class SignificantQuery {
            private String label;
            private String query;
            private Set<String> set;

            private SignificantQuery(String label, String query, Set<String> set) {
                this.label = label;
                this.query = query;
                this.set = set;
            }
        }

        List<SignificantQuery> sigQueries = new ArrayList<>();

        String query = "SELECT colony_id " +
                "FROM phenotype_call_summary pcs " +
                "INNER JOIN phenotype_parameter parameter ON parameter.id = pcs.parameter_id " +
                "WHERE parameter.stable_id = 'IMPC_VIA_001_001' ";
        sigQueries.add(new SignificantQuery("Viability", query, VIA_SIGNIFICANT));

        query = "SELECT colony_id " +
                "FROM phenotype_call_summary pcs " +
                "INNER JOIN phenotype_parameter parameter ON parameter.id = pcs.parameter_id " +
                "WHERE parameter.stable_id = 'IMPC_FER_001_001' ";
        sigQueries.add(new SignificantQuery("Male fertility", query, MALE_FER_SIGNIFICANT));

        query = "SELECT colony_id " +
                "FROM phenotype_call_summary pcs " +
                "INNER JOIN phenotype_parameter parameter ON parameter.id = pcs.parameter_id " +
                "WHERE parameter.stable_id = 'IMPC_FER_019_001' ";
        sigQueries.add(new SignificantQuery("Female fertility", query, FEMALE_FER_SIGNIFICANT));

        for (SignificantQuery sq : sigQueries) {

            try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(sq.query)) {
                ResultSet r = p.executeQuery();
                while (r.next()) {
                    sq.set.add(r.getString("colony_id"));
                }
            }

            logger.info(" Mapped {} {} significant data entries", sq.set.size(), sq.label);
        }
    }

    public void populateParameterMpTermMap() throws SQLException {

        String query = "SELECT stable_id, ontology_acc FROM phenotype_parameter p " +
                "INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON l.parameter_id=p.id " +
                "INNER JOIN phenotype_parameter_ontology_annotation o ON o.id=l.annotation_id WHERE ontology_acc like 'MP:%'" ;

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

            p.setFetchSize(Integer.MIN_VALUE);
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                String parameter = resultSet.getString("stable_id");
                String ontologyTerm = resultSet.getString("ontology_acc");

                if( ! parameterMpTermMap.containsKey(parameter)) {
                    parameterMpTermMap.put(parameter, new HashSet<>());
                }
                parameterMpTermMap.get(parameter).add(ontologyTerm);

            }
        }
        logger.info(" Mapped {} parameterMpTerm data entries", parameterMpTermMap.size());

    }

    static class ResourceBean {
        Long id;
        String name;
        String shortName;


        @Override
        public String toString() {

            return "ResourceBean{" + "id=" + id +
                    ", name='" + name + '\'' +
                    ", shortName='" + shortName + '\'' +
                    '}';
        }
    }

    /**
     * Internal class to act as Map value DTO for biological data
     */
    private static class BiologicalDataBean {
        private String alleleAccession;
        private String alleleSymbol;
        private String alleleName;
        private String geneAcc;
        private String geneSymbol;
        private String strainAcc;
        private String strainName;
        private String geneticBackground;
    }


    public class CategoricalResults implements  Callable<List<StatisticalResultDTO>> {
        String query = "SELECT CONCAT(dependent_variable, '_CAT_', sr.id) AS doc_id, "
                + "  'categorical' AS data_type, sr.id AS db_id, control_id, "
                + "  experimental_id, experimental_sex AS sex, experimental_zygosity, "
                + "  external_db_id, organisation_id, "
                + "  pipeline_id, procedure_id, parameter_id, colony_id, "
                + "  dependent_variable, control_selection_strategy, male_controls, "
                + "  male_mutants, female_controls, female_mutants, "
                + "  metadata_group, statistical_method, workflow, status, "
                + "  category_a, category_b, "
                + "  male_p_value, male_effect_size, female_p_value, female_effect_size, classification_tag, "
                + "  p_value AS categorical_p_value, effect_size AS categorical_effect_size, "
                + "  mp_acc, NULL AS male_mp_acc, NULL AS female_mp_acc, "
                + "  db.short_name AS resource_name, db.name AS resource_fullname, db.id AS resource_id, "
                + "  proj.name AS project_name, proj.id AS project_id, "
                + "  org.name AS phenotyping_center, org.id AS phenotyping_center_id "
                + "FROM stats_categorical_results sr "
                + "INNER JOIN external_db db ON db.id=sr.external_db_id "
                + "INNER JOIN project proj ON proj.id=sr.project_id "
                + "INNER JOIN organisation org ON org.id=sr.organisation_id "
                + "WHERE dependent_variable NOT LIKE '%FER%' AND dependent_variable NOT LIKE '%VIA%'";

        @Override
        public List<StatisticalResultDTO> call() {

            List<StatisticalResultDTO> docs = new ArrayList<>();

            try (Connection connection = komp2DataSource.getConnection()) {
                try (PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

                    p.setFetchSize(Integer.MIN_VALUE);
                    ResultSet r = p.executeQuery();
                    while (r.next()) {
                        StatisticalResultDTO doc = parseCategoricalResult(r);

                        // Skip document if it has already been added
                        if (uniqueSRKeys.contains(doc.getDocId())) {
                            continue;
                        }
                        uniqueSRKeys.add(doc.getDocId());

                        docs.add(doc);
                        if (SAVE) statisticalResultCore.addBean(doc, 30000);
                        shouldHaveAdded.add(doc.getDocId());
                        if (docs.size() % REPORT_INTERVAL == 0) {
                            logger.info((SAVE?"":"Would have") + " Added {} categorical documents", docs.size());
                        }
                    }

                } catch (Exception e) {
                    logger.warn(" Error occurred getting categorical results", e);
                }
            } catch (Exception e) {
                logger.warn(" Error occurred getting categorical results", e);
            }

            logger.info((SAVE?"":"Would have") + " Added {} categorical documents", docs.size());
            return docs;
        }

        private StatisticalResultDTO parseCategoricalResult(ResultSet r) throws SQLException {

            StatisticalResultDTO doc = parseResultCommonFields(r);
            if (sexesMap.containsKey("categorical-" + doc.getDbId())) {
                doc.setPhenotypeSex(sexesMap.get("categorical-" + doc.getDbId()));
            }

            doc.setSex(r.getString("sex"));
            doc.setpValue(r.getDouble("categorical_p_value"));

            doc.setEffectSize(r.getDouble("categorical_effect_size"));

            doc.setMaleKoEffectPValue(r.getDouble("male_p_value"));
            doc.setMaleKoParameterEstimate(r.getDouble("male_effect_size"));
            doc.setFemaleKoEffectPValue(r.getDouble("female_p_value"));
            doc.setFemaleKoParameterEstimate(r.getDouble("female_effect_size"));
            doc.setClassificationTag(r.getString("classification_tag"));

            setSignificantFlag(SIGNIFICANCE_THRESHOLD, doc);

            Set<String> categories = new HashSet<>();
            if (StringUtils.isNotEmpty(r.getString("category_a"))) {
                categories.addAll(Arrays.asList(r.getString("category_a").split("\\|")));
            }
            if (StringUtils.isNotEmpty(r.getString("category_b"))) {
                categories.addAll(Arrays.asList(r.getString("category_b")
                        .split("\\|")));
            }

            doc.setCategories(new ArrayList<>(categories));

            if (! doc.getStatus().equals("Success")) {
                doc.setpValue(1.0);
                doc.setEffectSize(0.0);
            }

            return doc;

        }

    }

    /**
     * Populate unidimensional statistic results
     */
    public class UnidimensionalResults implements Callable<List<StatisticalResultDTO>> {

        String query = "SELECT CONCAT(dependent_variable, '_CONT_', sr.id) as doc_id, "
                + "  'unidimensional' AS data_type, "
                + "  sr.id AS db_id, control_id, experimental_id, experimental_zygosity, "
                + "  external_db_id, organisation_id, "
                + "  pipeline_id, procedure_id, parameter_id, colony_id, "
                + "  dependent_variable, control_selection_strategy, "
                + "  male_controls, male_mutants, female_controls, female_mutants, "
                + "  male_control_mean, male_experimental_mean, female_control_mean, female_experimental_mean, "
                + "  metadata_group, statistical_method, workflow, status, "
                + "  batch_significance, "
                + "  variance_significance, null_test_significance, genotype_parameter_estimate, "
                + "  genotype_percentage_change, "
                + "  genotype_stderr_estimate, genotype_effect_pvalue, gender_parameter_estimate, "
                + "  gender_stderr_estimate, gender_effect_pvalue, weight_parameter_estimate, "
                + "  weight_stderr_estimate, weight_effect_pvalue, gp1_genotype, "
                + "  gp1_residuals_normality_test, gp2_genotype, gp2_residuals_normality_test, "
                + "  blups_test, rotated_residuals_normality_test, intercept_estimate, "
                + "  intercept_stderr_estimate, interaction_significance, interaction_effect_pvalue, "
                + "  gender_female_ko_estimate, gender_female_ko_stderr_estimate, gender_female_ko_pvalue, "
                + "  gender_male_ko_estimate, gender_male_ko_stderr_estimate, gender_male_ko_pvalue, "
                + "  classification_tag, additional_information, "
                + "  mp_acc, male_mp_acc, female_mp_acc, "
                + "  db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, "
                + "  proj.name as project_name, proj.id as project_id, "
                + "  org.name as phenotyping_center, org.id as phenotyping_center_id "
                + "FROM stats_unidimensional_results sr "
                + "INNER JOIN external_db db on db.id=sr.external_db_id "
                + "INNER JOIN project proj on proj.id=sr.project_id "
                + "INNER JOIN organisation org on org.id=sr.organisation_id "
                + "WHERE dependent_variable NOT LIKE '%FER%' AND dependent_variable NOT LIKE '%VIA%'";

        @Override
        public List<StatisticalResultDTO> call() {

            logger.info("  Starting unidimensional documents generation");

            List<StatisticalResultDTO> docs = new ArrayList<>();
            try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

                p.setFetchSize(Integer.MIN_VALUE);
                ResultSet r = p.executeQuery();
                while (r.next()) {
                    StatisticalResultDTO doc = parseUnidimensionalResult(r);

                    // Skip document if it has already been added
                    if (uniqueSRKeys.contains(doc.getDocId())) {
                        continue;
                    }
                    uniqueSRKeys.add(doc.getDocId());

                    docs.add(doc);
                    if (SAVE) statisticalResultCore.addBean(doc, 30000);
                    shouldHaveAdded.add(doc.getDocId());
                    if (docs.size()% REPORT_INTERVAL ==0) {
                        logger.info((SAVE?"":"Would have") + " Added {} unidimensional documents", docs.size());
                    }
                }
            } catch (Exception e) {
                logger.warn(" Error occurred getting unidimensional results", e);
            }
            logger.info((SAVE?"":"Would have") + " Added {} unidimensional documents", docs.size());
            return docs;
        }

        private StatisticalResultDTO parseUnidimensionalResult(ResultSet r) throws SQLException {

            StatisticalResultDTO doc = parseResultCommonFields(r);
            if (sexesMap.containsKey("unidimensional-" + doc.getDbId())) {
                doc.setPhenotypeSex(sexesMap.get("unidimensional-" + doc.getDbId()));
            }

            // Index the mean fields
            doc.setMaleControlMean(r.getDouble("male_control_mean"));
            doc.setMaleMutantMean(r.getDouble("male_experimental_mean"));
            doc.setFemaleControlMean(r.getDouble("female_control_mean"));
            doc.setFemaleMutantMean(r.getDouble("female_experimental_mean"));

            doc.setNullTestPValue(nullCheckResult(r, "null_test_significance"));

            // If PhenStat did not run, then the result will have a NULL for the null_test_significance field
            // In that case, fall back to Wilcoxon test
            Double pv = doc.getNullTestPValue();
            if (pv==null && doc.getStatus().equals("Success") && doc.getStatisticalMethod() != null && doc.getStatisticalMethod().startsWith("Wilcoxon")) {

                // Wilcoxon test.  Choose the most significant pvalue from the sexes
                pv = 1.0;
                double fPv = r.getDouble("gender_female_ko_pvalue");
                if (!r.wasNull() && fPv < pv) {
                    pv = fPv;
                }

                double mPv = r.getDouble("gender_male_ko_pvalue");
                if (!r.wasNull() && mPv < pv) {
                    pv = mPv;
                }

            }

            if ( ! doc.getStatus().equals("Success")) {
                pv = 1.0;
            }

            doc.setpValue(pv);

            setSignificantFlag(SIGNIFICANCE_THRESHOLD, doc);

            doc.setGroup1Genotype(r.getString("gp1_genotype"));
            doc.setGroup1ResidualsNormalityTest(nullCheckResult(r, "gp1_residuals_normality_test"));
            doc.setGroup2Genotype(r.getString("gp2_genotype"));
            doc.setGroup2ResidualsNormalityTest(nullCheckResult(r, "gp2_residuals_normality_test"));

            doc.setBatchSignificant(r.getBoolean("batch_significance"));
            doc.setVarianceSignificant(r.getBoolean("variance_significance"));
            doc.setInteractionSignificant(r.getBoolean("interaction_significance"));

            doc.setGenotypeEffectParameterEstimate(nullCheckResult(r, "genotype_parameter_estimate"));

            String percentageChange = r.getString("genotype_percentage_change");
            if (!r.wasNull()) {
                Double femalePercentageChange = PercentChangeStringParser.getFemalePercentageChange(percentageChange);
                if (femalePercentageChange != null) {
                    doc.setFemalePercentageChange(femalePercentageChange.toString() + "%");
                }

                Double malePercentageChange = PercentChangeStringParser.getMalePercentageChange(percentageChange);
                if (malePercentageChange != null) {
                    doc.setMalePercentageChange(malePercentageChange.toString() + "%");
                }
            }

            doc.setGenotypeEffectStderrEstimate(nullCheckResult(r, "genotype_stderr_estimate"));
            doc.setGenotypeEffectPValue(nullCheckResult(r, "genotype_effect_pvalue"));

            doc.setSexEffectParameterEstimate(nullCheckResult(r, "gender_parameter_estimate"));
            doc.setSexEffectStderrEstimate(nullCheckResult(r, "gender_stderr_estimate"));
            doc.setSexEffectPValue(nullCheckResult(r, "gender_effect_pvalue"));

            doc.setWeightEffectParameterEstimate(nullCheckResult(r, "weight_parameter_estimate"));
            doc.setWeightEffectStderrEstimate(nullCheckResult(r, "weight_stderr_estimate"));
            doc.setWeightEffectPValue(nullCheckResult(r, "weight_effect_pvalue"));

            doc.setInterceptEstimate(nullCheckResult(r, "intercept_estimate"));
            doc.setInterceptEstimateStderrEstimate(nullCheckResult(r, "intercept_stderr_estimate"));
            doc.setInteractionEffectPValue(nullCheckResult(r, "interaction_effect_pvalue"));

            doc.setFemaleKoParameterEstimate(nullCheckResult(r, "gender_female_ko_estimate"));
            doc.setFemaleKoEffectStderrEstimate(nullCheckResult(r, "gender_female_ko_stderr_estimate"));
            doc.setFemaleKoEffectPValue(nullCheckResult(r, "gender_female_ko_pvalue"));

            doc.setMaleKoParameterEstimate(nullCheckResult(r, "gender_male_ko_estimate"));
            doc.setMaleKoEffectStderrEstimate(nullCheckResult(r, "gender_male_ko_stderr_estimate"));
            doc.setMaleKoEffectPValue(nullCheckResult(r, "gender_male_ko_pvalue"));

            doc.setBlupsTest(nullCheckResult(r, "blups_test"));
            doc.setRotatedResidualsTest(nullCheckResult(r, "rotated_residuals_normality_test"));
            doc.setClassificationTag(r.getString("classification_tag"));
            doc.setAdditionalInformation(r.getString("additional_information"));
            return doc;

        }
    }

    /**
     * 	Generate reference range plus statistic result DTOs
     */
    public class ReferenceRangePlusResults implements  Callable<List<StatisticalResultDTO>> {

        String query = "SELECT CONCAT(dependent_variable, '_RR_', sr.id) as doc_id, "
                + "  'unidimensional-ReferenceRange' AS data_type, "
                + "  sr.id AS db_id, control_id, experimental_id, experimental_zygosity, "
                + "  external_db_id, organisation_id, "
                + "  pipeline_id, procedure_id, parameter_id, colony_id, "
                + "  dependent_variable, control_selection_strategy, "
                + "  male_controls, male_mutants, female_controls, female_mutants, "
                + "  male_control_mean, male_experimental_mean, female_control_mean, female_experimental_mean, "
                + "  metadata_group, statistical_method, workflow, status, "
                + "  genotype_parameter_estimate, genotype_effect_pvalue, "
                + "  gp1_genotype, gp2_genotype, "
                + "  gender_female_ko_estimate, gender_female_ko_pvalue, "
                + "  gender_male_ko_estimate, gender_male_ko_pvalue, "
                + "  classification_tag, additional_information, "
                + "  mp_acc, male_mp_acc, female_mp_acc, "
                + "  db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, "
                + "  proj.name as project_name, proj.id as project_id, "
                + "  org.name as phenotyping_center, org.id as phenotyping_center_id "
                + "FROM stats_rrplus_results sr "
                + "INNER JOIN external_db db on db.id=sr.external_db_id "
                + "INNER JOIN project proj on proj.id=sr.project_id "
                + "INNER JOIN organisation org on org.id=sr.organisation_id ";

        @Override
        public List<StatisticalResultDTO> call() {

            List<StatisticalResultDTO> docs = new ArrayList<>();
            try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

                p.setFetchSize(Integer.MIN_VALUE);
                ResultSet r = p.executeQuery();
                while (r.next()) {
                    StatisticalResultDTO doc = parseReferenceRangeResult(r);

                    // Skip document if it has already been added
                    if (uniqueSRKeys.contains(doc.getDocId())) {
                        continue;
                    }
                    uniqueSRKeys.add(doc.getDocId());

                    docs.add(doc);
                    if (SAVE) statisticalResultCore.addBean(doc, 30000);
                    shouldHaveAdded.add(doc.getDocId());
                }
            } catch (Exception e) {
                logger.warn(" Error occurred getting RR plus results", e);
            }
            logger.info((SAVE?"":"Would have") + " Added {} RR plus documents", docs.size());
            return docs;
        }

        private StatisticalResultDTO parseReferenceRangeResult(ResultSet r) throws SQLException {

            List<Double> mins = new ArrayList<>();

            StatisticalResultDTO doc = parseResultCommonFields(r);
            if (sexesMap.containsKey("rrplus-" + doc.getDbId())) {
                doc.setPhenotypeSex(sexesMap.get("rrplus-" + doc.getDbId()));
            }

            // Index the mean fields
            doc.setMaleControlMean(r.getDouble("male_control_mean"));
            doc.setMaleMutantMean(r.getDouble("male_experimental_mean"));
            doc.setFemaleControlMean(r.getDouble("female_control_mean"));
            doc.setFemaleMutantMean(r.getDouble("female_experimental_mean"));

            doc.setGroup1Genotype(r.getString("gp1_genotype"));
            doc.setGroup2Genotype(r.getString("gp2_genotype"));


            // Set the overall genotype effect fields
            String genotypePvalue = r.getString("genotype_effect_pvalue");
            if (! r.wasNull()) {
                String [] fields = genotypePvalue.split(",");

                // Low vs normal&high genotype pvalue
                Double pvalue = Double.parseDouble(fields[0]);
                doc.setGenotypePvalueLowVsNormalHigh(pvalue);

                // High vs low&normal genotype pvalue
                pvalue = Double.parseDouble(fields[1]);
                doc.setGenotypePvalueLowNormalVsHigh(pvalue);

                doc.setNullTestPValue(Math.min(doc.getGenotypePvalueLowNormalVsHigh(), doc.getGenotypePvalueLowVsNormalHigh()));
                doc.setpValue(doc.getNullTestPValue());
                mins.add(pvalue);

                String genotypeEffectSize = r.getString("genotype_parameter_estimate");
                if (! r.wasNull()) {
                    fields = genotypeEffectSize.replaceAll("%", "").split(",");

                    // Low vs normal&high genotype effect size
                    double es = Double.parseDouble(fields[0]);
                    doc.setGenotypeEffectSizeLowVsNormalHigh(es);

                    // High vs low&normal genotype effect size
                    es = Double.parseDouble(fields[1]);
                    doc.setGenotypeEffectSizeLowNormalVsHigh(es);
                }

            }

            // Set the female female effect fields
            genotypePvalue = r.getString("gender_female_ko_pvalue");
            if (! r.wasNull() && ! genotypePvalue.equals("NA")) {
                String [] fields = genotypePvalue.split(",");

                // Low vs normal&high female pvalue
                Double pvalue = Double.parseDouble(fields[0]);
                doc.setFemalePvalueLowVsNormalHigh(pvalue);
                mins.add(pvalue);

                // High vs low&normal female pvalue
                pvalue = Double.parseDouble(fields[1]);
                doc.setFemalePvalueLowNormalVsHigh(pvalue);
                mins.add(pvalue);

                String genotypeEffectSize = r.getString("gender_female_ko_estimate");
                if (! r.wasNull()) {
                    fields = genotypeEffectSize.replaceAll("%", "").split(",");

                    // Low vs normal&high female effect size
                    double es = Double.parseDouble(fields[0]);
                    doc.setFemaleEffectSizeLowVsNormalHigh(es);

                    // High vs low&normal female effect size
                    es = Double.parseDouble(fields[1]);
                    doc.setFemaleEffectSizeLowNormalVsHigh(es);
                }

            }

            // Set the male effect fields
            genotypePvalue = r.getString("gender_male_ko_pvalue");
            if (! r.wasNull() && ! genotypePvalue.equals("NA")) {
                String [] fields = genotypePvalue.split(",");

                // Low vs normal&high male pvalue
                Double pvalue = Double.parseDouble(fields[0]);
                doc.setMalePvalueLowVsNormalHigh(pvalue);
                mins.add(pvalue);

                // High vs low&normal male pvalue
                pvalue = Double.parseDouble(fields[1]);
                doc.setMalePvalueLowNormalVsHigh(pvalue);
                mins.add(pvalue);

                String genotypeEffectSize = r.getString("gender_male_ko_estimate");
                if (! r.wasNull()) {
                    fields = genotypeEffectSize.replaceAll("%", "").split(",");

                    // Low vs normal&high male effect size
                    double es = Double.parseDouble(fields[0]);
                    doc.setMaleEffectSizeLowVsNormalHigh(es);

                    // High vs low&normal male effect size
                    es = Double.parseDouble(fields[1]);
                    doc.setMaleEffectSizeLowNormalVsHigh(es);
                }


            }

            Double minimumPvalue = Collections.min(mins);
            doc.setpValue(minimumPvalue);
            setSignificantFlag(SIGNIFICANCE_THRESHOLD, doc);

            // If not already set, ensure that the document has all possible top level MP terms defined
            if (doc.getTopLevelMpTermId() == null && mpParser.getOntologyTerm(doc.getMpTermId()) != null) {
                OntologyTermDTO term = mpParser.getOntologyTerm(doc.getMpTermId());
                doc.addTopLevelMpTermIds(term.getTopLevelIds());
                doc.addTopLevelMpTermNames(term.getTopLevelNames());
            }


            if (! doc.getStatus().equals("Success")) {
                doc.setpValue(1.0);
                doc.setEffectSize(0.0);
            }

            doc.setClassificationTag(r.getString("classification_tag"));
            doc.setAdditionalInformation(r.getString("additional_information"));
            return doc;

        }

    }

    /**
     * If the result is significant (indicated by having a more significant p_value than pValueThreshold)
     * then if there has not been a previous result (sex specific or genotype effect) which is significant
     * then mark this as significant, otherwise, not.
     *
     * @param pValueThreshold The p value to indicate significance threshould
     * @param doc the solr document to update
     */
    private void setSignificantFlag(Double pValueThreshold, StatisticalResultDTO doc) {

        doc.setSignificant(false);

        // do not override significant == true
        if (doc.getSignificant()!=null && doc.getSignificant()) {
            return;
        }

        if (doc.getNullTestPValue() != null) {
            // PhenStat result

            if (doc.getNullTestPValue() <= pValueThreshold) {
                doc.setSignificant(true);
            } else if (doc.getStatus().equals("Success") && doc.getSignificant() == null) {
                doc.setSignificant(false);
            }

        } else if (doc.getStatus().equals("Success") && doc.getStatisticalMethod() != null && doc.getStatisticalMethod().startsWith("Wilcoxon")) {
            // Wilcoxon test.  Choose the most significant pvalue from the sexes, already tcalculated and stored
            // in the Pvalue field of the doc

            if (doc.getpValue() <= pValueThreshold) {
                doc.setSignificant(true);
            } else {
                doc.setSignificant(false);
            }

        } else if (doc.getNullTestPValue() == null && doc.getStatus().equals("Success") && doc.getStatisticalMethod() != null && doc.getStatisticalMethod().startsWith("Fisher")) {
            // Fisher's exact test.  Choose the most significant pvalue from the sexes, already tcalculated and stored
            // in the Pvalue field of the doc

            if (doc.getpValue() <= pValueThreshold) {
                doc.setSignificant(true);
            } else {
                doc.setSignificant(false);
            }
        }
    }

    class FertilityResults implements Callable<List<StatisticalResultDTO>> {

        String query = "SELECT CONCAT(parameter.stable_id, '_', exp.id, '_', IF(sex IS NULL,'both',sex)) as doc_id, co.category, " +
                "'line' AS data_type, db.id AS db_id, " +
                "zygosity as experimental_zygosity, db.id AS external_db_id, exp.pipeline_id, exp.procedure_id, obs.parameter_id, exp.colony_id, sex, " +
                "parameter.stable_id as dependent_variable, " +
                "'Success' as status, exp.biological_model_id, " +
                "p_value as p_value, effect_size AS effect_size, " +
                "mp_acc, null as male_mp_acc, null as female_mp_acc, exp.metadata_group, " +
                "db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, " +
                "proj.name as project_name, proj.id as project_id, " +
                "org.name as phenotyping_center, org.id as phenotyping_center_id " +
                "FROM phenotype_parameter parameter " +
                "INNER JOIN phenotype_procedure_parameter pproc ON pproc.parameter_id=parameter.id " +
                "INNER JOIN phenotype_procedure proc ON proc.id=pproc.procedure_id " +
                "INNER JOIN observation obs ON obs.parameter_stable_id=parameter.stable_id AND obs.parameter_stable_id IN ('IMPC_FER_001_001', 'IMPC_FER_019_001') " +
                "INNER JOIN categorical_observation co ON co.id=obs.id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id=obs.id " +
                "INNER JOIN experiment exp ON eo.experiment_id=exp.id " +
                "INNER JOIN external_db db ON db.id=obs.db_id " +
                "INNER JOIN project proj ON proj.id=exp.project_id " +
                "INNER JOIN organisation org ON org.id=exp.organisation_id " +
                "LEFT OUTER JOIN phenotype_call_summary sr ON (exp.colony_id=sr.colony_id AND sr.parameter_id=parameter.id) " +
                "WHERE  parameter.stable_id IN ('IMPC_FER_001_001', 'IMPC_FER_019_001') AND exp.procedure_id=proc.id";

        @Override
        public List<StatisticalResultDTO> call() {

            List<StatisticalResultDTO> docs = new ArrayList<>();

            try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

                p.setFetchSize(Integer.MIN_VALUE);
                ResultSet r = p.executeQuery();
                while (r.next()) {

                    // Skip processing females for male infertility parameter
                    if (r.getString("dependent_variable") != null && r.getString("dependent_variable").equals("IMPC_FER_001_001") && r.getString("sex") != null && r.getString("sex").equals("female")) {
                        continue;
                    }
                    // Skip processing males for female infertility parameter
                    if (r.getString("dependent_variable") != null && r.getString("dependent_variable").equals("IMPC_FER_019_001") && r.getString("sex") != null && r.getString("sex").equals("male")) {
                        continue;
                    }

                    StatisticalResultDTO doc = parseLineResult(r);

                    // Skip document if it has already been added
                    if (uniqueSRKeys.contains(doc.getDocId())) {
                        continue;
                    }
                    uniqueSRKeys.add(doc.getDocId());

                    doc.setCategories(Collections.singletonList(r.getString("category")));
                    r.getString("p_value");
                    if (r.wasNull()) {
                        doc.setpValue(1.0);
                        doc.setEffectSize(0.0);
                    }
                    docs.add(doc);
                    if (SAVE) statisticalResultCore.addBean(doc, 30000);
                    shouldHaveAdded.add(doc.getDocId());
                }

            } catch (Exception e) {
                logger.warn(" Error occurred getting fertility results", e);
            }

            logger.info((SAVE?"":"Would have") + " Added {} fertility parameter documents", docs.size());
            return docs;
        }

    }
    class ViabilityResults implements Callable<List<StatisticalResultDTO>> {

        // Populate viability results
        String query = "SELECT CONCAT(parameter.stable_id, '_', exp.id, '_', CASE WHEN sex IS NULL THEN 'na' ELSE sex END) as doc_id, co.category, " +
                "'line' AS data_type, db.id AS db_id, " +
                "zygosity as experimental_zygosity, db.id AS external_db_id, exp.pipeline_id, exp.procedure_id, obs.parameter_id, exp.colony_id, sex, " +
                "parameter.stable_id as dependent_variable, " +
                "'Success' as status, exp.biological_model_id, " +
                "p_value as p_value, effect_size AS effect_size, " +
                "mp_acc, null as male_mp_acc, null as female_mp_acc, exp.metadata_group, " +
                "db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, " +
                "proj.name as project_name, proj.id as project_id, " +
                "org.name as phenotyping_center, org.id as phenotyping_center_id, " +
                "0 AS male_controls, " +
                "(SELECT uobs2.data_point " +
                "  FROM observation obs2 " +
                "  INNER JOIN unidimensional_observation uobs2 ON obs2.id=uobs2.id " +
                "  INNER JOIN experiment_observation eo2 ON eo2.observation_id=obs2.id " +
                "  INNER JOIN experiment exp2 ON eo2.experiment_id=exp2.id " +
                "  WHERE exp2.colony_id=exp.colony_id AND obs2.parameter_stable_id='IMPC_VIA_010_001' limit 1) AS male_mutants, " +
                "0 AS female_controls, " +
                "(SELECT uobs2.data_point " +
                "  FROM observation obs2 " +
                "  INNER JOIN unidimensional_observation uobs2 ON obs2.id=uobs2.id " +
                "  INNER JOIN experiment_observation eo2 ON eo2.observation_id=obs2.id " +
                "  INNER JOIN experiment exp2 ON eo2.experiment_id=exp2.id " +
                "  WHERE exp2.colony_id=exp.colony_id AND obs2.parameter_stable_id='IMPC_VIA_014_001' limit 1) AS  female_mutants " +
                "FROM phenotype_parameter parameter " +
                "INNER JOIN phenotype_procedure_parameter pproc ON pproc.parameter_id=parameter.id " +
                "INNER JOIN phenotype_procedure proc ON proc.id=pproc.procedure_id " +
                "INNER JOIN observation obs ON obs.parameter_stable_id=parameter.stable_id AND obs.parameter_stable_id = 'IMPC_VIA_001_001' " +
                "INNER JOIN categorical_observation co ON co.id=obs.id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id=obs.id " +
                "INNER JOIN experiment exp ON eo.experiment_id=exp.id " +
                "INNER JOIN external_db db ON db.id=obs.db_id " +
                "INNER JOIN project proj ON proj.id=exp.project_id " +
                "INNER JOIN organisation org ON org.id=exp.organisation_id " +
                "LEFT OUTER JOIN phenotype_call_summary sr ON (exp.colony_id=sr.colony_id AND sr.parameter_id=parameter.id) " +
                "WHERE  parameter.stable_id = 'IMPC_VIA_001_001' AND exp.procedure_id=proc.id" ;

        @Override
        public List<StatisticalResultDTO> call() {

            List<StatisticalResultDTO> docs = new ArrayList<>();

            try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

                p.setFetchSize(Integer.MIN_VALUE);
                ResultSet r = p.executeQuery();
                while (r.next()) {

                    StatisticalResultDTO doc = parseLineResult(r);

                    // Skip document if it has already been added
                    if (uniqueSRKeys.contains(doc.getDocId())) {
                        continue;
                    }
                    uniqueSRKeys.add(doc.getDocId());

                    doc.setCategories(Collections.singletonList(r.getString("category")));
                    docs.add(doc);
                    if (SAVE) statisticalResultCore.addBean(doc, 30000);
                    shouldHaveAdded.add(doc.getDocId());
                }

            } catch (Exception e) {
                logger.warn(" Error occurred getting viability results", e);
            }

            logger.info((SAVE?"":"Would have") + " Added {} viability parameter documents", docs.size());
            return docs;
        }

    }

    public class EmbryoResults implements Callable<List<StatisticalResultDTO>> {

        String query = "SELECT DISTINCT " +
                "  CONCAT_WS('-', exp.procedure_stable_id, parameter.stable_id, ls.colony_id, bm.zygosity, sex, exp.organisation_id, exp.metadata_group) AS doc_id,  " +
                "  CONCAT(parameter.stable_id, '_', ls.colony_id, exp.organisation_id) AS significant_id, " +
                "  'embryo' AS data_type, 'Success' AS status, " +
                "  exp.metadata_group, exp.pipeline_id, exp.procedure_id, obs.parameter_id, parameter.stable_id AS dependent_variable, " +
                "  bm.id AS biological_model_id, bm.zygosity AS experimental_zygosity, ls.colony_id, sex, " +
                "  NULL AS p_value, NULL AS effect_size, NULL AS mp_acc, NULL AS male_mp_acc, NULL AS female_mp_acc, " +
                "  db.short_name AS resource_name, db.name AS resource_fullname, db.id AS db_id, db.id AS resource_id, db.id AS external_db_id,  " +
                "  proj.name AS project_name, proj.id AS project_id,  " +
                "  org.name AS phenotyping_center, org.id AS phenotyping_center_id  " +
                "FROM observation obs INNER JOIN phenotype_parameter parameter ON parameter.id = obs.parameter_id " +
                "  INNER JOIN live_sample ls ON ls.id = obs.biological_sample_id " +
                "  INNER JOIN biological_sample bs ON bs.id = obs.biological_sample_id " +
                "  INNER JOIN biological_model_sample bms ON bms.biological_sample_id = obs.biological_sample_id " +
                "  INNER JOIN biological_model bm ON bm.id = bms.biological_model_id " +
                "  INNER JOIN experiment_observation eo ON eo.observation_id = obs.id " +
                "  INNER JOIN experiment exp ON exp.id = eo.experiment_id " +
                "  INNER JOIN (SELECT id FROM phenotype_procedure WHERE stable_id REGEXP '" + StringUtils.join(EMBRYO_PROCEDURES_NO_VIA, "|") + "') B ON B.id = exp.procedure_id " +
                "  INNER JOIN external_db db ON db.id = obs.db_id " +
                "  INNER JOIN project proj ON proj.id = exp.project_id " +
                "  INNER JOIN organisation org ON org.id = exp.organisation_id " +
                "WHERE bs.sample_group = 'experimental' ";


        @Override
        public List<StatisticalResultDTO> call() {

            List<StatisticalResultDTO> docs = new ArrayList<>();

            try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {


                p.setFetchSize(Integer.MIN_VALUE);
                ResultSet r = p.executeQuery();
                int i = 0;

                while (r.next()) {

                    StatisticalResultDTO doc = parseLineResult(r);
                    doc.setDocId(doc.getDocId() + "-" + (i++));

                    // Skip document if it has already been added
                    if (uniqueSRKeys.contains(doc.getDocId())) {
                        continue;
                    }
                    uniqueSRKeys.add(doc.getDocId());

                    if (embryoSignificantResults.containsKey(r.getString("significant_id"))) {
                        addMpTermData(embryoSignificantResults.get(r.getString("significant_id")), doc);
                        doc.setSignificant(true);
                    } else {
                        doc.setSignificant(false);
                    }

                    docs.add(doc);
                    if (SAVE) statisticalResultCore.addBean(doc, 30000);
                    shouldHaveAdded.add(doc.getDocId());

                }

            } catch (Exception e) {
            	e.printStackTrace();
                logger.warn(" Error occurred getting embryo results", e);
            }

            logger.info("  Generated {} embryo parameter documents", docs.size());
            return docs;
        }
    }

    public class EmbryoViabilityResults implements Callable<List<StatisticalResultDTO>> {

        String query = "SELECT co.category, " +
                "  CONCAT(parameter.stable_id, '_', exp.id, '_embryo') as doc_id, " +
                "  CONCAT(parameter.stable_id, '_', exp.colony_id, org.id) as significant_id, " +
                "'embryo' AS data_type, db.id AS db_id, " +
                "zygosity as experimental_zygosity, db.id AS external_db_id, exp.pipeline_id, exp.procedure_id, " +
                "parameter.id as parameter_id, exp.colony_id, null as sex, " +
                "parameter.stable_id as dependent_variable, " +
                "'Success' as status, exp.biological_model_id, " +
                "0.0 as p_value, 1.0 AS effect_size, " +
                "ontology_acc AS mp_acc, null as male_mp_acc, null as female_mp_acc, exp.metadata_group, " +
                "db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, " +
                "proj.name as project_name, proj.id as project_id, " +
                "org.name as phenotyping_center, org.id as phenotyping_center_id " +
                "FROM phenotype_parameter parameter " +
                "  INNER JOIN observation o ON o.parameter_stable_id=parameter.stable_id " +
                "  INNER JOIN categorical_observation co ON co.id=o.id " +
                "  INNER JOIN experiment_observation eo ON eo.observation_id=o.id " +
                "  INNER JOIN experiment exp ON eo.experiment_id=exp.id " +
                "  INNER JOIN biological_model bm ON bm.id=exp.biological_model_id " +
                "  INNER JOIN external_db db ON db.id=o.db_id " +
                "  INNER JOIN project proj ON proj.id=exp.project_id " +
                "  INNER JOIN organisation org ON org.id=exp.organisation_id " +
                "  LEFT OUTER JOIN ( " +
                "    SELECT parameter_id, name, ontology_acc FROM phenotype_parameter_lnk_option lnkopt " +
                "    INNER JOIN phenotype_parameter_option opt ON opt.id=lnkopt.option_id " +
                "    INNER JOIN phenotype_parameter_ontology_annotation oa ON oa.option_id=opt.id " +
                "  ) b ON b.parameter_id=parameter.id AND b.name=co.category " +
                "WHERE parameter.stable_id in ('" + StringUtils.join(EMBRYO_PROCEDURES_VIA, "','") + "') ";

        @Override
        public List<StatisticalResultDTO> call() {

            List<StatisticalResultDTO> docs = new ArrayList<>();
            try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

                p.setFetchSize(Integer.MIN_VALUE);
                ResultSet r = p.executeQuery();
                while (r.next()) {
                    StatisticalResultDTO doc = parseLineResult(r);

                    // Skip document if it has already been added
                    if (uniqueSRKeys.contains(doc.getDocId())) {
                        continue;
                    }
                    uniqueSRKeys.add(doc.getDocId());

                    if (embryoSignificantResults.containsKey(r.getString("significant_id"))) {
                        addMpTermData(embryoSignificantResults.get(r.getString("significant_id")), doc);
                        doc.setSignificant(true);
                    } else {
                        doc.setSignificant(false);
                    }

                    docs.add(doc);
                    if (SAVE) statisticalResultCore.addBean(doc, 30000);
                    shouldHaveAdded.add(doc.getDocId());
                }
            } catch (Exception e) {
                logger.warn(" Error occurred getting embryo results", e);
            }

            logger.info( (SAVE?"":"Would have") + " Added {} embryo viability parameter documents", docs.size());
            return docs;
        }


    }

    public class GrossPathologyResults implements Callable<List<StatisticalResultDTO>> {

        String query = "SELECT DISTINCT CONCAT(parameter.stable_id, '_', o.id, '_', term, '_', ls.sex, '_grosspath') as doc_id, " +
                "'adult-gross-path' AS data_type, db.id AS db_id, " +
                "ls.zygosity as experimental_zygosity, ls.id, bs.sample_group, db.id AS external_db_id, exp.pipeline_id, exp.procedure_id, " +
                "parameter.id as parameter_id, ls.colony_id, ls.sex as sex, " +
                "parameter.stable_id as dependent_variable, " +
                "'Success' as status, bm.id AS biological_model_id, " +
                "null as p_value, null AS effect_size, " +
                "oe.term as mp_acc , null as male_mp_acc, null as female_mp_acc, exp.metadata_group, " +
                "db.short_name as resource_name, db.name as resource_fullname, db.id as resource_id, " +
                "proj.name as project_name, proj.id as project_id, " +
                "org.name as phenotyping_center, org.id as phenotyping_center_id " +
                "FROM observation o " +
                "INNER JOIN ontology_entity oe on oe.ontology_observation_id=o.id " +
                "INNER JOIN biological_model_sample bms ON bms.biological_sample_id = o.biological_sample_id " +
                "INNER JOIN biological_model bm ON bms.biological_model_id = bm.id " +
                "INNER JOIN biological_sample bs ON bs.id = bms.biological_sample_id " +
                "INNER JOIN live_sample ls ON bms.biological_sample_id = ls.id " +
                "INNER JOIN experiment_observation eo ON eo.observation_id = o.id " +
                "INNER JOIN experiment exp ON exp.id = eo.experiment_id " +
                "INNER JOIN external_db db ON db.id=o.db_id " +
                "INNER JOIN project proj ON proj.id=exp.project_id " +
                "INNER JOIN organisation org ON org.id=exp.organisation_id " +
                "INNER JOIN phenotype_parameter parameter ON parameter.id = o.parameter_id " +
                "WHERE o.parameter_stable_id like '%PAT%' and term_value != 'normal' and term like 'MP%'  AND bs.sample_group!='control'  " ;

        @Override
        public List<StatisticalResultDTO> call() {

            List<StatisticalResultDTO> docs = new ArrayList<>();

            try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(query, java.sql.ResultSet.TYPE_FORWARD_ONLY,  java.sql.ResultSet.CONCUR_READ_ONLY)) {

                p.setFetchSize(Integer.MIN_VALUE);
                ResultSet r = p.executeQuery();
                int i = 0;
                while (r.next()) {

                    StatisticalResultDTO doc = parseLineResult(r);
                    doc.setDocId(doc.getDocId()+"-"+(i++));

                    // Skip document if it has already been added
                    if (uniqueSRKeys.contains(doc.getDocId())) {
                        continue;
                    }
                    uniqueSRKeys.add(doc.getDocId());

                    doc.setSignificant(true);
                    docs.add(doc);

                    if (SAVE) statisticalResultCore.addBean(doc, 30000);
                    shouldHaveAdded.add(doc.getDocId());
                }

            } catch (Exception e) {
                logger.warn(" Error occurred getting gross pathology results", e);
            }

            logger.info((SAVE?"":"Would have") + " Added {} gross pathology parameter documents", docs.size());
            return docs;
        }

    }

    public void setSAVE(Boolean SAVE) {
        this.SAVE = SAVE;
    }

    public Map<String, String> getEmbryoSignificantResults() {
        return embryoSignificantResults;
    }


    public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(StatisticalResultsIndexer.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }
}