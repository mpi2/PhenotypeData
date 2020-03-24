/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package org.mousephenotype.cda.indexers;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.constants.Constants;
import org.mousephenotype.cda.db.WeightMap;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.enumerations.BiologicalSampleType;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.indexers.exceptions.IndexerException;
import org.mousephenotype.cda.indexers.utils.IndexerMap;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.mousephenotype.cda.solr.service.OntologyBean;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTOWrite;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Populate the experiment core
 */
@EnableAutoConfiguration
public class ObservationIndexer extends AbstractIndexer implements CommandLineRunner {

    @Value("${experimenterIdMap}")
    String experimenterIdMap;


	private final Logger logger = LoggerFactory.getLogger(ObservationIndexer.class);

	private final List<String> MALE_FERTILITY_PARAMETERS   = Arrays.asList("IMPC_FER_001_001", "IMPC_FER_006_001",
                                                                             "IMPC_FER_007_001", "IMPC_FER_008_001", "IMPC_FER_009_001");
	private final List<String> FEMALE_FERTILITY_PARAMETERS = Arrays.asList("IMPC_FER_019_001", "IMPC_FER_010_001",
                                                                               "IMPC_FER_011_001", "IMPC_FER_012_001", "IMPC_FER_013_001");


	private Map<Long, String>                         anatomyMap              = new HashMap<>();
	private Map<String, BiologicalDataBean>           biologicalData          = new HashMap<>();
	private Map<Long, DatasourceBean>                 datasourceMap           = new HashMap<>();
	private Map<String, String>                       emap2emapaIdMap         = new HashMap<>();
	private Map<Long, List<OntologyBean>>             ontologyEntityMap;
	private Map<Long, List<ParameterAssociationBean>> parameterAssociationMap = new HashMap<>();
	private Map<Long, ParameterDTO>                   parameterMap            = new HashMap<>();
	private Map<Long, ImpressBaseDTO>                 pipelineMap             = new HashMap<>();
	private Map<Long, ImpressBaseDTO>                 procedureMap            = new HashMap<>();
	private Map<Long, DatasourceBean>                 projectMap              = new HashMap<>();
	private Map<String, Map<String, String>>          translateCategoryNames  = new HashMap<>();


    // For debugging, set this variable TRUE to skip loading the slow-loading maps below. For normal indexing operation, set it to FALSE.
    private final Boolean SKIP_SLOW_LOADING_MAPS = Boolean.FALSE;
    // Slow-loading maps. These maps take a long time to load.
	private Map<Long, List<String>>                   experimenterData        = new HashMap<>();
	private Map<String, BiologicalDataBean>           lineBiologicalData      = new HashMap<>();
	private WeightMap                                 weightMap;                                   // NOTE: weightMap takes upwards of 8 minutes to load.

	private OntologyParser        emapaParser;
	private OntologyParser        maParser;
	private OntologyParserFactory ontologyParserFactory;

	private SolrClient experimentCore;


    private final long    DISPLAY_INTERVAL_IN_SECONDS                       = 300;
    private final int     MAX_MISSING_BIOLOGICAL_DATA_ERROR_COUNT_DISPLAYED = 100;
    private final Boolean USE_PARALLEL_STREAM                               = Boolean.FALSE;
    private       int     missingBiologicalDataErrorCount                   = 0;

    private long       startTimestamp;
    private AtomicLong lastTimestamp         = new AtomicLong(0L);
    private AtomicLong expectedDocumentCount = new AtomicLong(0L);      // Override inherited variable with AtomicLong to avoid concurrency issues.

	protected ObservationIndexer() {

	}

	@Inject
	public ObservationIndexer(
			@NotNull DataSource komp2DataSource,
			@NotNull OntologyTermRepository ontologyTermRepository,
			@NotNull SolrClient experimentCore)
	{
		super(komp2DataSource, ontologyTermRepository);
		this.experimentCore = experimentCore;
    }

    @Override
    public RunStatus validateBuild() throws IndexerException {
		return super.validateBuild(experimentCore);
    }

	public static void main(String[] args) {

        ConfigurableApplicationContext context = new SpringApplicationBuilder(ObservationIndexer.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);

        context.close();
    }


    @Override
	public RunStatus run() throws IndexerException, SQLException {
	    if ( ! SKIP_SLOW_LOADING_MAPS) {
            weightMap = new WeightMap(komp2DataSource);
        }
        long count;
        RunStatus runStatus = new RunStatus();
        long start = System.currentTimeMillis();

		try (Connection connection = komp2DataSource.getConnection()) {

            ontologyParserFactory = new OntologyParserFactory(komp2DataSource, owlpath);
            emapaParser = ontologyParserFactory.getEmapaParser();
            maParser = ontologyParserFactory.getMaParser();
            logger.info("  populating supporting maps");
            pipelineMap = IndexerMap.getImpressPipelines(connection);
            procedureMap = IndexerMap.getImpressProcedures(connection);
            parameterMap = IndexerMap.getImpressParameters(connection);
            logger.info("  IMPReSS maps:  Pipeline: {}, Procedure: {}, Parameter: {} ", pipelineMap.size(), procedureMap.size(), parameterMap.size());

            logger.info("  populating ontology entity map");
            ontologyEntityMap = IndexerMap.getOntologyParameterSubTerms(connection);
            logger.info("  ontology entity map size: " + ontologyEntityMap.size());

            logger.info("  populating datasource map");
			populateDatasourceDataMap(connection);

			if ( ! SKIP_SLOW_LOADING_MAPS) {
                logger.info("  populating experimenter map");
                populateExperimenterDataMap(connection);
                logger.info("  map size: " + experimenterData.size());
            }

            logger.info("  populating categorynames map");
			populateCategoryNamesDataMap(connection);
            logger.info("  map size: " + translateCategoryNames.size());

            logger.info("  populating biological data map");
			populateBiologicalDataMap(connection);
            logger.info("  map size: " + biologicalData.size());

            if ( ! SKIP_SLOW_LOADING_MAPS) {
                logger.info("  populating line data map");
                populateLineBiologicalDataMap(connection);
                logger.info("  map size: " + lineBiologicalData.size());
            }

            logger.info("  populating parameter association map");
			populateParameterAssociationMap(connection);
            logger.info("  map size: " + parameterAssociationMap.size());

            logger.info("  populating emap to emapa map");
            populateEmap2EmapaMap();
            logger.info("  map size: " + emap2emapaIdMap.size());

            logger.info("  populating anatomy map");
			populateAnatomyMap(connection);
            logger.info("  map size: " + anatomyMap.size());

            logger.info("  maps populated");

			count = populateObservationSolrCore(runStatus);
			super.expectedDocumentCount = expectedDocumentCount.get();      // Save atomic document count in parent class.


        } catch (SolrServerException | SQLException | IOException | OWLOntologyCreationException | OWLOntologyStorageException e) {
            e.printStackTrace();
            throw new IndexerException(e);
        }

		logger.info(" Added {} total beans in {}", count, commonUtils.msToHms(System.currentTimeMillis() - start));
        return runStatus;
    }

    private static class NamedQuery {
	    public final String name;
	    public final String query;

        public NamedQuery(String name, String query) {
            this.name = name;
            this.query = query;
        }
    }
	public long populateObservationSolrCore(RunStatus runStatus) throws IOException, SolrServerException, IndexerException {

		experimentCore.deleteByQuery("*:*");

		List<NamedQuery> observationQueries = Arrays.asList(
                new NamedQuery("Categorical", "SELECT o.id as id, o.db_id as datasource_id, o.parameter_id as parameter_id, o.parameter_stable_id, o.observation_type, o.missing, o.parameter_status, o.parameter_status_message, o.biological_sample_id, o.sequence_id as sequence_id ,e.project_id as project_id, e.pipeline_id as pipeline_id, e.procedure_id as procedure_id, e.date_of_experiment, e.external_id, e.id as experiment_id, e.metadata_combined as metadata_combined, e.metadata_group as metadata_group, bs.project_id AS specimen_project_id, co.category as raw_category FROM observation o INNER JOIN categorical_observation co ON o.id=co.id INNER JOIN experiment_observation eo ON eo.observation_id=o.id INNER JOIN experiment e on eo.experiment_id=e.id  LEFT OUTER JOIN biological_sample bs ON bs.id = o.biological_sample_id WHERE o.missing=0"),
                new NamedQuery("Unidimensional", "SELECT o.id as id, o.db_id as datasource_id, o.parameter_id as parameter_id, o.parameter_stable_id, o.observation_type, o.missing, o.parameter_status, o.parameter_status_message, o.biological_sample_id, o.sequence_id as sequence_id ,e.project_id as project_id, e.pipeline_id as pipeline_id, e.procedure_id as procedure_id, e.date_of_experiment, e.external_id, e.id as experiment_id, e.metadata_combined as metadata_combined, e.metadata_group as metadata_group, bs.project_id AS specimen_project_id, uo.data_point as unidimensional_data_point FROM observation o INNER JOIN unidimensional_observation uo ON o.id=uo.id INNER JOIN experiment_observation eo ON eo.observation_id=o.id INNER JOIN experiment e on eo.experiment_id=e.id LEFT OUTER JOIN biological_sample bs ON bs.id = o.biological_sample_id WHERE o.missing=0"),
                new NamedQuery("Multidimensional", "SELECT o.id as id, o.db_id as datasource_id, o.parameter_id as parameter_id, o.parameter_stable_id, o.observation_type, o.missing, o.parameter_status, o.parameter_status_message, o.biological_sample_id, o.sequence_id as sequence_id ,e.project_id as project_id, e.pipeline_id as pipeline_id, e.procedure_id as procedure_id, e.date_of_experiment, e.external_id, e.id as experiment_id, e.metadata_combined as metadata_combined, e.metadata_group as metadata_group, bs.project_id AS specimen_project_id, mo.data_point as multidimensional_data_point, mo.order_index, mo.dimension FROM observation o INNER JOIN multidimensional_observation mo ON o.id=mo.id INNER JOIN experiment_observation eo ON eo.observation_id=o.id INNER JOIN experiment e on eo.experiment_id=e.id LEFT OUTER JOIN biological_sample bs ON bs.id = o.biological_sample_id WHERE o.missing=0"),
                new NamedQuery("TimeSeries", "SELECT o.id as id, o.db_id as datasource_id, o.parameter_id as parameter_id, o.parameter_stable_id, o.observation_type, o.missing, o.parameter_status, o.parameter_status_message, o.biological_sample_id, o.sequence_id as sequence_id ,e.project_id as project_id, e.pipeline_id as pipeline_id, e.procedure_id as procedure_id, e.date_of_experiment, e.external_id, e.id as experiment_id, e.metadata_combined as metadata_combined, e.metadata_group as metadata_group, bs.project_id AS specimen_project_id, tso.data_point as time_series_data_point, tso.time_point, tso.discrete_point FROM observation o INNER JOIN time_series_observation tso ON o.id=tso.id INNER JOIN experiment_observation eo ON eo.observation_id=o.id INNER JOIN experiment e on eo.experiment_id=e.id LEFT OUTER JOIN biological_sample bs ON bs.id = o.biological_sample_id WHERE o.missing=0"),
                new NamedQuery("Text", "SELECT o.id as id, o.db_id as datasource_id, o.parameter_id as parameter_id, o.parameter_stable_id, o.observation_type, o.missing, o.parameter_status, o.parameter_status_message, o.biological_sample_id, o.sequence_id as sequence_id ,e.project_id as project_id, e.pipeline_id as pipeline_id, e.procedure_id as procedure_id, e.date_of_experiment, e.external_id, e.id as experiment_id, e.metadata_combined as metadata_combined, e.metadata_group as metadata_group, bs.project_id AS specimen_project_id, tro.text_value as text_value FROM observation o INNER JOIN text_observation tro ON o.id=tro.id INNER JOIN experiment_observation eo ON eo.observation_id=o.id INNER JOIN experiment e on eo.experiment_id=e.id LEFT OUTER JOIN biological_sample bs ON bs.id = o.biological_sample_id WHERE o.missing=0"),
                new NamedQuery("Image", "SELECT o.id as id, o.db_id as datasource_id, o.parameter_id as parameter_id, o.parameter_stable_id, o.observation_type, o.missing, o.parameter_status, o.parameter_status_message, o.biological_sample_id, o.sequence_id as sequence_id ,e.project_id as project_id, e.pipeline_id as pipeline_id, e.procedure_id as procedure_id, e.date_of_experiment, e.external_id, e.id as experiment_id, e.metadata_combined as metadata_combined, e.metadata_group as metadata_group, bs.project_id AS specimen_project_id, iro.file_type, iro.download_file_path FROM observation o INNER JOIN image_record_observation iro ON o.id=iro.id INNER JOIN experiment_observation eo ON eo.observation_id=o.id INNER JOIN experiment e on eo.experiment_id=e.id LEFT OUTER JOIN biological_sample bs ON bs.id = o.biological_sample_id WHERE o.missing=0"),
                new NamedQuery("OntologyTerm", "SELECT o.id as id, o.db_id as datasource_id, o.parameter_id as parameter_id, o.parameter_stable_id, o.observation_type, o.missing, o.parameter_status, o.parameter_status_message, o.biological_sample_id, o.sequence_id as sequence_id ,e.project_id as project_id, e.pipeline_id as pipeline_id, e.procedure_id as procedure_id, e.date_of_experiment, e.external_id, e.id as experiment_id, e.metadata_combined as metadata_combined, e.metadata_group as metadata_group, bs.project_id AS specimen_project_id, onto.term AS ontology_id, onto.term_value AS ontology_term FROM observation o INNER JOIN ontology_entity onto ON o.id=onto.ontology_observation_id INNER JOIN experiment_observation eo ON eo.observation_id=o.id INNER JOIN experiment e on eo.experiment_id=e.id LEFT OUTER JOIN biological_sample bs ON bs.id = o.biological_sample_id WHERE o.missing=0")
         );

        startTimestamp = System.currentTimeMillis();
        lastTimestamp.getAndSet(startTimestamp);

        logger.info("  BEGIN processing experiments");

        if (USE_PARALLEL_STREAM) {

            observationQueries
                .parallelStream()
                .forEach(query -> {

                    long documentCountForQuery = 0L;
                    try (Connection connection = komp2DataSource.getConnection()) {

                        logger.info("STARTING QUERY '" + query.name + "'");
                        documentCountForQuery = executeQueryAndWriteObservations(connection, query, runStatus);
                        logger.info("FINISHED QUERY '" + query.name + "'. Wrote {} documents.", documentCountForQuery);

                    } catch (Exception e) {

                        logger.error("EXCEPTION in query {}. Wrote {} documents before error. Query aborted.", query.name, documentCountForQuery);
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }

                }
            );

        } else {

            for (NamedQuery query : observationQueries) {
                long documentCountForQuery = 0L;
                try (Connection connection = komp2DataSource.getConnection()) {

                    logger.info("  STARTING QUERY {}", query.name);
                    documentCountForQuery = executeQueryAndWriteObservations(connection, query, runStatus);
                    logger.info("  FINISHED QUERY {}. Wrote {} documents.", query.name, documentCountForQuery);

                } catch (Exception e) {

                    logger.error("EXCEPTION in query {}. Wrote {} documents before error. Query aborted.", query.name, documentCountForQuery);
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        logger.info("  FINISHED processing experiments.");

        if (missingBiologicalDataErrorCount > 0) {
            logger.error("'Cannot find biological data for specimen id...' occurred " + missingBiologicalDataErrorCount + " times.");
        }

        return expectedDocumentCount.get();
    }

    private long executeQueryAndWriteObservations(Connection connection, NamedQuery query, RunStatus runStatus) {

	    long documentCountForQuery = 0L;
        try (PreparedStatement p = connection.prepareStatement(query.query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {

            setFetchSize(p);

            logger.debug("  QUERY START");		// 2019-08-16 16:57:05.782  INFO 32731 --- [           main] o.m.cda.indexers.ObservationIndexer      :   QUERY START
            ResultSet r = p.executeQuery();
            logger.debug("  QUERY END");		// 2019-08-16 16:57:05.791  INFO 32731 --- [           main] o.m.cda.indexers.ObservationIndexer      :   QUERY END

            documentCountForQuery = writeObservations(query, r, runStatus);
            checkAndLogProgress(query.name);
            experimentCore.commit();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" Big error :" + e.getMessage());
        }

        return documentCountForQuery;
    }

    private void setFetchSize(PreparedStatement p) throws Exception {
	    try {

            p.setFetchSize(Integer.MIN_VALUE);

        } catch (Exception e) {

            // NOTE: This message is generated by the H2 database engine when trying to set fetch size
            // to Integer.MIN_VALUE (mysql).  It's harmless to ignore these exceptions.
	        if ( ! e.getLocalizedMessage().contains("Invalid value \"-2147483648\" for parameter \"rows\" [90008-199]")) {
	            throw e;
            }
        }
    }

    private Set<String> uniqueObservationKeys = new ConcurrentSkipListSet<>();
    private long writeObservations(NamedQuery query, ResultSet r, RunStatus runStatus) throws Exception {

	    long documentCountForQuery = 0L;
        while (r.next()) {

            // Skip observation if it has already been added
            if (uniqueObservationKeys.contains(r.getString("id"))) {
                continue;
            }
            uniqueObservationKeys.add(r.getString("id"));

            if (writeObservation(query, r, runStatus))
                continue;

            documentCountForQuery++;
        }

        return documentCountForQuery;
    }

    private boolean writeObservation(NamedQuery query, ResultSet r, RunStatus runStatus) throws Exception {
        ObservationDTOWrite o = new ObservationDTOWrite();

        o.setId(r.getString("id"));
        o.setParameterId(r.getLong("parameter_id"));
        o.setExperimentId(r.getLong("experiment_id"));
        o.setExperimentSourceId(r.getString("external_id"));
        addSequenceIdIfApplicable(r, o);
        addDateOfExperiment(r, o);
        addParameter(r, o);
        addProcedure(r, o);
        addPipeline(r, o);
        addAnatomyTermIfApplicable(r, o);
        addDatasource(r, o);
        addProject(r, o);
        addSpecimenProject(r, o);
        addMetadata(r, o);
        addBiologicalData(r, o, runStatus);
        addLifeStageIfApplicable(o);
        o.setObservationType(r.getString("observation_type"));
        addCorrectDataPointForType(r, o);
        addParameterAssociationsIfApplicable(r, o);
        addWeightParametersIfApplicable(o);

        try {

            experimentCore.addBean(o, 60000);

        } catch (Exception e) {

            logger.error("Failed to add experimentId: {}, dateOfBirth: {},  dateOfExperiment: {}, weightDate: {}. Reason: {}\nquery: {}",
                         o.getExperimentId(),
                         o.getDateOfBirthAsZonedDateTime() == null ? "null" : o.getDateOfBirthAsZonedDateTime().toString(),
                         o.getDateOfExperimentAsZonedDateTime() == null ? "null" : o.getDateOfExperimentAsZonedDateTime().toString(),
                         o.getWeightDateAsZonedDateTime() == null ? "null" : o.getWeightDateAsZonedDateTime().toString(),
                         e.getLocalizedMessage(),
                         query);
            return true;
        }

        checkAndLogProgress(query.name);
        expectedDocumentCount.getAndIncrement();
        return false;
    }

    private void addSequenceIdIfApplicable(ResultSet r, ObservationDTOWrite o) throws SQLException {
        if (StringUtils.isNotEmpty(r.getString("sequence_id"))) {
            if (isInteger(r.getString("sequence_id"))) {
                Integer seqId = Integer.parseInt(r.getString("sequence_id"));
                o.setSequenceId(seqId);
            }
        }
    }

    private void addPipeline(ResultSet r, ObservationDTOWrite o) throws SQLException {
        o.setPipelineId(pipelineMap.get(r.getLong("pipeline_id")).getId());
        o.setPipelineName(pipelineMap.get(r.getLong("pipeline_id")).getName());
        o.setPipelineStableId(pipelineMap.get(r.getLong("pipeline_id")).getStableId());
    }

    private void addProcedure(ResultSet r, ObservationDTOWrite o) throws SQLException {
        o.setProcedureId(procedureMap.get(r.getLong("procedure_id")).getId());
        o.setProcedureName(procedureMap.get(r.getLong("procedure_id")).getName());
        String procedureStableId = procedureMap.get(r.getLong("procedure_id")).getStableId();
        o.setProcedureStableId(procedureStableId);
        o.setProcedureGroup(procedureStableId.substring(0, procedureStableId.lastIndexOf("_")));
    }

    private void addParameter(ResultSet r, ObservationDTOWrite o) throws SQLException {
        o.setParameterId(parameterMap.get(r.getLong("parameter_id")).getId());
        o.setParameterName(parameterMap.get(r.getLong("parameter_id")).getName());
        o.setParameterStableId(parameterMap.get(r.getLong("parameter_id")).getStableId());
        o.setDataType(parameterMap.get(r.getLong("parameter_id")).getDatatype());
    }

    private void addDateOfExperiment(ResultSet r, ObservationDTOWrite o) throws SQLException {
        try {
            ZonedDateTime dateOfExperiment = ZonedDateTime.parse(r.getString("date_of_experiment"), DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_OPTIONAL_MILLISECONDS).withZone(ZoneId.of("UTC")));
            o.setDateOfExperiment(dateOfExperiment);
        } catch (NullPointerException e) {
            logger.debug("  No date of experiment set for experiment external ID: {}", r.getString("external_id"));
            o.setDateOfExperiment((Date) null);
        }
    }

    private void addAnatomyTermIfApplicable(ResultSet r, ObservationDTOWrite o) throws SQLException {
        if (anatomyMap.containsKey(r.getLong("parameter_id"))) {

            String anatomyTermId = anatomyMap.get(r.getLong("parameter_id"));

            if (anatomyTermId != null) {

                if (o.getAnatomyId() == null) {
                    // Initialize all the collections of anatomy terms
                    o.setAnatomyId(new ArrayList<>());
                    o.setAnatomyTerm(new ArrayList<>());
                    o.setAnatomyTermSynonym(new ArrayList<>());
                    o.setIntermediateAnatomyId(new ArrayList<>());
                    o.setIntermediateAnatomyTerm(new ArrayList<>());
                    o.setIntermediateAnatomyTermSynonym(new ArrayList<>());
                    o.setSelectedTopLevelAnatomyId(new ArrayList<>());
                    o.setSelectedTopLevelAnatomyTerm(new ArrayList<>());
                    o.setSelectedTopLevelAnatomyTermSynonym(new ArrayList<>());
                }

                if (anatomyTermId.startsWith("MA:")) {
                    addAnatomyInfo(maParser.getOntologyTerm(anatomyTermId), o);
                } else if (anatomyTermId.startsWith("EMAPA:")) {
                    addAnatomyInfo(emapaParser.getOntologyTerm(anatomyTermId), o);
                }
            }
        }
    }

    private void addSpecimenProject(ResultSet r, ObservationDTOWrite o) throws SQLException {
	    long specimenProjectId = r.getLong("specimen_project_id");
        o.setSpecimenProjectId((r.wasNull()) && (specimenProjectId > 0L) ? specimenProjectId : null);
        o.setSpecimenProjectName((r.wasNull()) && (specimenProjectId > 0L) ? projectMap.get(specimenProjectId).name : null);
    }

    private void addDatasource(ResultSet r, ObservationDTOWrite o) throws SQLException {
        o.setDataSourceId(datasourceMap.get(r.getLong("datasource_id")).id);
        o.setDataSourceName(datasourceMap.get(r.getLong("datasource_id")).name);
    }

    private void addProject(ResultSet r, ObservationDTOWrite o) throws SQLException {
        o.setProjectId(projectMap.get(r.getLong("project_id")).id);
        o.setProjectName(projectMap.get(r.getLong("project_id")).name);
    }

    private void addMetadata(ResultSet r, ObservationDTOWrite o) throws SQLException {
        o.setMetadataGroup(r.getString("metadata_group"));
        if (r.wasNull()) {
            o.setMetadataGroup("");
            o.setMetadata(new ArrayList<>());
        }

        String metadataCombined = r.getString("metadata_combined");
        if (!r.wasNull()) {
            o.setMetadata(new ArrayList<>(Arrays.asList(metadataCombined.split("::"))));
        }

        if ( ! SKIP_SLOW_LOADING_MAPS) {
            // Add experimenter ID(s) to the metadata
            if (experimenterData.containsKey(o.getExperimentId())) {
                if (o.getMetadata() == null) {
                    o.setMetadata(new ArrayList<>(experimenterData.get(o.getExperimentId())));
                } else {
                    o.getMetadata().addAll(experimenterData.get(o.getExperimentId()));
                }
            }
        }
    }

    private void addBiologicalData(ResultSet r, ObservationDTOWrite o, RunStatus runStatus) throws Exception {
        String bioSampleId = r.getString("biological_sample_id");
        if (r.wasNull()) {
            addBiologicalDataForLines(runStatus, r, o);
        } else {
            if (addBiologicalDataForSamples(o, bioSampleId)) {

                if (missingBiologicalDataErrorCount++ < MAX_MISSING_BIOLOGICAL_DATA_ERROR_COUNT_DISPLAYED) {
                    runStatus.addError(" Cannot find biological data for specimen id: " + r.getString("biological_sample_id") + ", experiment id: " + r.getString("experiment_id"));
                }

            }
        }

    }

    private boolean addBiologicalDataForSamples(ObservationDTOWrite o, String bioSampleId) throws Exception {
        BiologicalDataBean b           = biologicalData.get(bioSampleId);

        if (b == null) {
            return true;
        }

        addBiologicalInformation(o, b);

        o.setZygosity(b.zygosity);
        o.setDateOfBirth(b.dateOfBirth);
        if (b.dateOfBirth != null && o.getDateOfExperimentAsDate() != null) {

            Instant dob        = b.dateOfBirth.toInstant();
            Instant expDate    = o.getDateOfExperimentAsZonedDateTime().toInstant();
            int     ageInDays  = (int) Duration.between(dob, expDate).toDays();
            int     daysInWeek = 7;
            int     ageInWeeks = ageInDays / daysInWeek;
            o.setAgeInDays(ageInDays);
            o.setAgeInWeeks(ageInWeeks);
        }
        o.setSex(b.sex);
        o.setGroup(b.sampleGroup);
        o.setBiologicalSampleId(b.biologicalSampleId);
        o.setExternalSampleId(b.externalSampleId);

        if (b.productionCenterName != null) {
            o.setProductionCenter(b.productionCenterName);
        }
        if (b.productionCenterId != null) {
            o.setProductionCenterId(b.productionCenterId);
        }
        if (b.litterId != null) {
            o.setLitterId(b.litterId);
        }

        return false;
    }

    private void addBiologicalDataForLines(RunStatus runStatus, ResultSet r, ObservationDTOWrite o) throws SQLException {
        if ( ! SKIP_SLOW_LOADING_MAPS) {
            BiologicalDataBean b = lineBiologicalData.get(r.getString("experiment_id"));
            if (b == null) {
                runStatus.addError(
                        " Cannot find biological data for line level experiment " + r.getString("experiment_id"));
                return;
            }

            addBiologicalInformation(o, b);

            // Viability applies to both sexes
            if (o.getParameterStableId().contains("_VIA_")) {
                o.setSex(SexType.both.getName());
            } else {
                // Fertility applies to the sex tested, separate
                // parameters per male//female
                if (MALE_FERTILITY_PARAMETERS.contains(o.getParameterStableId())) {
                    o.setSex(SexType.male.getName());
                } else if (FEMALE_FERTILITY_PARAMETERS.contains(o.getParameterStableId())) {
                    o.setSex(SexType.female.getName());
                }
                if (o.getSex() == null) {
                    o.setSex(SexType.both.getName());
                }
            }

            if (b.zygosity != null) {
                o.setZygosity(b.zygosity);
            } else {
                // Default to hom
                o.setZygosity(ZygosityType.homozygote.getName());
            }
        }

        // All line level parameters are sample group "experimental"
        // due to the nature of the
        // procedures (i.e. no control mice will go through VIA or
        // FER procedures.)
        o.setGroup(BiologicalSampleType.experimental.getName());
    }

    private void addLifeStageIfApplicable(ObservationDTOWrite o) {
        final OntologyTerm lifeStage = getLifeStage(o.getParameterStableId());
        if (lifeStage != null) {
            o.setDevelopmentStageAcc(lifeStage.getId().getAccession());
            o.setDevelopmentStageName(lifeStage.getName());
        }
    }

    private void addCorrectDataPointForType(ResultSet r, ObservationDTOWrite o) throws SQLException {
        // Add the correct "data point" for the type
        switch (ObservationType.valueOf(r.getString("observation_type"))) {
            case unidimensional:
                o.setDataPoint(r.getFloat("unidimensional_data_point"));
                break;

            case multidimensional:
                o.setDataPoint(r.getFloat("multidimensional_data_point"));

                String dimension = r.getString("dimension");
                if (!r.wasNull()) {
                    o.setDimension(dimension);
                }

                Integer order_index = r.getInt("order_index");
                if (!r.wasNull()) {
                    o.setOrderIndex(order_index);
                }

                break;

            case time_series:
                o.setDataPoint(r.getFloat("time_series_data_point"));

                String time_point = r.getString("time_point");
                if (!r.wasNull()) {
                    o.setTimePoint(time_point);
                }

                Float discrete_point = r.getFloat("discrete_point");
                if (!r.wasNull()) {
                    o.setDiscretePoint(discrete_point);
                }

                break;

            case categorical:

                String cat = r.getString("raw_category");
                if (!r.wasNull()) {

                    String param = r.getString("parameter_stable_id");
                    if (translateCategoryNames.containsKey(param)) {

                        String transCat = translateCategoryNames.get(param).get(cat);
                        if (transCat != null && !transCat.equals("")) {
                            o.setCategory(transCat);
                        } else {
                            o.setCategory(cat);
                        }

                    } else {
                        o.setCategory(cat);
                    }
                }

                break;

            case image_record:

                String file_type = r.getString("file_type");
                if (!r.wasNull()) {
                    o.setFileType(file_type);
                }

                String download_file_path = r.getString("download_file_path");
                if (!r.wasNull()) {
                    o.setDownloadFilePath(download_file_path);
                }

                break;

            case ontological:

                if (ontologyEntityMap.containsKey(Long.parseLong(o.getId()))) {

                    List<OntologyBean> subOntBeans = ontologyEntityMap.get(Long.parseLong(o.getId()));
                    for (OntologyBean bean : subOntBeans) {
                        o.addSubTermId(bean.getId());
                        o.addSubTermName(bean.getName());
                        o.addSubTermDescription(bean.getDescription());
                    }
                }

                break;

            case text:

                String text_value = r.getString("text_value");
                if (!r.wasNull()) {
                    o.setTextValue(text_value);
                }

                break;

            case text_series:

                String text_series_value = r.getString("text_value");
                if (!r.wasNull()) {
                    o.setTextValue(text_series_value);
                }

                String text_series_increment = r.getString("increment");
                if (!r.wasNull()) {
                    o.setDimension(text_series_increment);
                }

                break;

            default:
                logger.warn("Unknown observation type {}", r.getString("observation_type"));
                break;
        } // end switch
    }

    private void addParameterAssociationsIfApplicable(ResultSet r, ObservationDTOWrite o) throws SQLException {
        if (parameterAssociationMap.containsKey(r.getLong("id"))) {
            for (ParameterAssociationBean pb : parameterAssociationMap.get(r.getLong("id"))) {

                // Will never be null, we hope
                o.addParameterAssociationStableId(pb.parameterStableId);
                o.addParameterAssociationName(pb.parameterAssociationName);
                if (StringUtils.isNotEmpty(pb.parameterAssociationValue)) {
                    o.addParameterAssociationValue(pb.parameterAssociationValue);
                }
                if (StringUtils.isNotEmpty(pb.sequenceId)) {
                    o.addParameterAssociationSequenceId(pb.sequenceId);
                }

                if (StringUtils.isNotEmpty(pb.dimId)) {
                    o.addParameterAssociationDimId(pb.dimId);
                }
            }
        }
    }

    private void addWeightParametersIfApplicable(ObservationDTOWrite o) {
        // Add weight parameters only if this observation isn't itself a
        // weight parameter
        if ( ! SKIP_SLOW_LOADING_MAPS) {
            if (!WeightMap.isWeightParameter(o.getParameterStableId())) {
                WeightMap.BodyWeight b = weightMap.getNearestWeight(o.getBiologicalSampleId(), o.getParameterStableId(), o.getDateOfExperimentAsZonedDateTime());

                if (o.getProcedureGroup().contains("_IPG")) {
                    b = weightMap.getNearestIpgttWeight(o.getBiologicalSampleId());
                }

                if (b != null) {
                    o.setWeight(b.getWeight());
                    o.setWeightDate(b.getDate());
                    o.setWeightDaysOld(b.getDaysOld());
                    o.setWeightParameterStableId(b.getParameterStableId());
                }
            }
        }
    }

    private void addBiologicalInformation(ObservationDTOWrite o, BiologicalDataBean b) {
        o.setBiologicalModelId(b.biologicalModelId);
        o.setGeneAccession(b.geneAcc);
        o.setGeneSymbol(b.geneSymbol);
        o.setAlleleAccession(b.alleleAccession);
        o.setAlleleSymbol(b.alleleSymbol);
        o.setStrainAccessionId(b.strainAcc);
        o.setStrainName(b.strainName);
        o.setGeneticBackground(b.geneticBackground);
        o.setAllelicComposition(b.allelicComposition);
        o.setPhenotypingCenter(b.phenotypingCenterName);
        o.setPhenotypingCenterId(b.phenotypingCenterId);
        o.setColonyId(b.colonyId);
    }

    private void addAnatomyInfo(OntologyTermDTO term, ObservationDTOWrite doc) {

        if (term != null) {
            doc.getAnatomyId().add(term.getAccessionId());
            doc.getAnatomyTerm().add(term.getName());
            doc.getAnatomyTermSynonym().addAll(term.getSynonyms());
            if (term.getTopLevelIds() != null) {
                doc.setSelectedTopLevelAnatomyId(new ArrayList<>(term.getTopLevelIds()));
                doc.setSelectedTopLevelAnatomyTerm(new ArrayList<>(term.getTopLevelNames()));
            }
            if (term.getIntermediateIds() != null) {
                doc.setIntermediateAnatomyId(new ArrayList<>(term.getIntermediateIds()));
                doc.setIntermediateAnatomyTerm(new ArrayList<>(term.getIntermediateNames()));
            }
        }
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NullPointerException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    /**
     * Add all the relevant data required quickly looking up biological data
     * associated to a biological sample
     *
     * @throws SQLException when a database exception occurs
     */
	void populateBiologicalDataMap(Connection connection) throws SQLException {

        String query = "SELECT CAST(bs.id AS CHAR) as biological_sample_id, bs.organisation_id as phenotyping_center_id, "
                + "org.name as phenotyping_center_name, bs.sample_group, bs.external_id as external_sample_id, "
                + "bs.project_id, project.name AS project_name, "
                + "ls.date_of_birth, ls.colony_id, ls.sex as sex, ls.zygosity, ls.developmental_stage_acc, ot.name AS developmental_stage_name, ot.acc as developmental_stage_acc,"
                + "bms.biological_model_id, "
                + "strain.acc as strain_acc, strain.name as strain_name, bm.genetic_background, bm.allelic_composition, "
                + "bs.production_center_id, prod_org.name as production_center_name, ls.litter_id, "
                + "(select distinct allele_acc from biological_model_allele bma WHERE bma.biological_model_id=bms.biological_model_id) as allele_accession, "
                + "(select distinct a.symbol from biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bms.biological_model_id)  as allele_symbol, "
                + "(select distinct gf_acc from biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=bms.biological_model_id) as acc, "
                + "(select distinct gf.symbol from biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf on gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bms.biological_model_id)  as symbol "
                + "FROM biological_sample bs "
                + "INNER JOIN organisation org ON bs.organisation_id=org.id "
                + "INNER JOIN live_sample ls ON bs.id=ls.id "
                + "INNER JOIN biological_model_sample bms ON bs.id=bms.biological_sample_id "
                + "INNER JOIN biological_model_strain bmstrain ON bmstrain.biological_model_id=bms.biological_model_id "
                + "INNER JOIN strain ON strain.acc=bmstrain.strain_acc "
                + "INNER JOIN biological_model bm ON bm.id = bms.biological_model_id "
                + "INNER JOIN ontology_term ot ON ot.acc=ls.developmental_stage_acc "
                + "INNER JOIN organisation prod_org ON bs.organisation_id=prod_org.id "
                + "LEFT OUTER JOIN project ON project.id = bs.project_id";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                BiologicalDataBean b = new BiologicalDataBean();

                b.alleleAccession = resultSet.getString("allele_accession");
                b.alleleSymbol = resultSet.getString("allele_symbol");
                b.biologicalModelId = resultSet.getLong("biological_model_id");
                b.biologicalSampleId = resultSet.getLong("biological_sample_id");
                b.colonyId = resultSet.getString("colony_id");

				String rawDOB = null;

                try {
					rawDOB = resultSet.getString("date_of_birth");

					DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_OPTIONAL_MILLISECONDS);
					b.dateOfBirth = ZonedDateTime.parse(rawDOB, formatter.withZone(ZoneId.of("UTC")));

                } catch (DateTimeParseException e) {

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_OPTIONAL_MILLISECONDS);
                    if (rawDOB != null) {
                        b.dateOfBirth = ZonedDateTime.parse(rawDOB, formatter.withZone(ZoneId.of("UTC")));
                    }

                } catch (NullPointerException e) {

                    b.dateOfBirth = null;
                    logger.debug("  No date of birth set for specimen external ID: {}", resultSet.getString("external_sample_id"));

                }

                b.externalSampleId = resultSet.getString("external_sample_id");
                b.geneAcc = resultSet.getString("acc");
                b.geneSymbol = resultSet.getString("symbol");
				b.phenotypingCenterId = resultSet.getLong("phenotyping_center_id");
                b.phenotypingCenterName = resultSet.getString("phenotyping_center_name");
                b.sampleGroup = resultSet.getString("sample_group");
                b.sex = resultSet.getString("sex");
                b.strainAcc = resultSet.getString("strain_acc");
                b.strainName = resultSet.getString("strain_name");
                b.geneticBackground = resultSet.getString("genetic_background");
                b.allelicComposition = resultSet.getString("allelic_composition");
                b.zygosity = resultSet.getString("zygosity");
				b.productionCenterId = resultSet.getLong("production_center_id");
                b.productionCenterName = resultSet.getString("production_center_name");
                b.litterId = resultSet.getString("litter_id");
                b.projectId = resultSet.getLong("project_id");
                b.projectName = resultSet.getString("project_name");

                biologicalData.put(resultSet.getString("biological_sample_id"), b);
            }
        }
    }

    /**
     * Add all the relevant data required quickly looking up biological data
     * associated to a biological model (really an experiment)
     *
     * @throws SQLException when a database exception occurs
     */
	void populateLineBiologicalDataMap(Connection connection) throws SQLException {

        String query = "SELECT e.id as experiment_id, e.colony_id, e.biological_model_id, "
                + "e.organisation_id as phenotyping_center_id, org.name as phenotyping_center_name, "
                + "strain.acc as strain_acc, strain.name as strain_name, bm.genetic_background, bm.allelic_composition, "
                + "(select distinct allele_acc from biological_model_allele bma WHERE bma.biological_model_id=e.biological_model_id) as allele_accession, "
                + "(select distinct a.symbol from biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=e.biological_model_id)  as allele_symbol, "
                + "(select distinct gf_acc from biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=e.biological_model_id) as acc, "
                + "(select distinct gf.symbol from biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf on gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=e.biological_model_id)  as symbol "
                + "FROM experiment e "
                + "INNER JOIN organisation org ON e.organisation_id=org.id "
                + "INNER JOIN biological_model_strain bm_strain ON bm_strain.biological_model_id=e.biological_model_id "
                + "INNER JOIN strain ON strain.acc=bm_strain.strain_acc "
                + "INNER JOIN biological_model bm ON bm.id = e.biological_model_id";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                BiologicalDataBean b = new BiologicalDataBean();

                b.colonyId = resultSet.getString("colony_id");
				b.phenotypingCenterId = resultSet.getLong("phenotyping_center_id");
                b.phenotypingCenterName = resultSet.getString("phenotyping_center_name");
                b.strainAcc = resultSet.getString("strain_acc");
                b.strainName = resultSet.getString("strain_name");
                b.geneticBackground = resultSet.getString("genetic_background");
                b.allelicComposition = resultSet.getString("allelic_composition");
                b.alleleAccession = resultSet.getString("allele_accession");
                b.alleleSymbol = resultSet.getString("allele_symbol");
				b.biologicalModelId = resultSet.getLong("biological_model_id");
                b.geneAcc = resultSet.getString("acc");
                b.geneSymbol = resultSet.getString("symbol");

                if (b.alleleAccession == null && b.colonyId != null) {
                    // Override the biological model with one that has the
                    // correct gene/allele/strain
                    String query2 = "SELECT DISTINCT bm.id as biological_model_id, "
                            + " (select distinct allele_acc from biological_model_allele bma WHERE bma.biological_model_id=bm.id) as allele_accession, "
                            + " (select distinct a.symbol from biological_model_allele bma INNER JOIN allele a on (a.acc=bma.allele_acc AND a.db_id=bma.allele_db_id) WHERE bma.biological_model_id=bm.id) as allele_symbol, "
                            + " (select distinct gf_acc from biological_model_genomic_feature bmgf WHERE bmgf.biological_model_id=bm.id) as acc, "
                            + " (select distinct gf.symbol from biological_model_genomic_feature bmgf INNER JOIN genomic_feature gf on gf.acc=bmgf.gf_acc WHERE bmgf.biological_model_id=bm.id)  as symbol, "
                            + " strain.acc as strain_acc, strain.name as strain_name, bm.genetic_background, bm.allelic_composition "
                            + " FROM live_sample ls "
                            + " INNER JOIN biological_model_sample bms ON bms.biological_sample_id=ls.id "
                            + " INNER JOIN biological_model bm ON bm.id=bms.biological_model_id "
                            + " INNER JOIN biological_model_strain bm_strain ON bm_strain.biological_model_id=bm.id "
                            + " INNER JOIN strain ON strain.acc=bm_strain.strain_acc "
                            + " WHERE bm.allelic_composition !='' AND ls.colony_id = ? LIMIT 1 ";
                    try (PreparedStatement p2 = connection.prepareStatement(query2)) {
                        p2.setString(1, resultSet.getString("colony_id"));
                        ResultSet resultSet2 = p2.executeQuery();
                        resultSet2.next();
                        b.strainAcc = resultSet2.getString("strain_acc");
                        b.strainName = resultSet2.getString("strain_name");
                        b.geneticBackground = resultSet2.getString("genetic_background");
                        b.allelicComposition = resultSet.getString("allelic_composition");
                        b.alleleAccession = resultSet2.getString("allele_accession");
                        b.alleleSymbol = resultSet2.getString("allele_symbol");
						b.biologicalModelId = resultSet2.getLong("biological_model_id");
                        b.geneAcc = resultSet2.getString("acc");
                        b.geneSymbol = resultSet2.getString("symbol");
                    }
                }

                if ( ! SKIP_SLOW_LOADING_MAPS) {
                    lineBiologicalData.put(resultSet.getString("experiment_id"), b);
                }
            }
        }
    }

    /**
     * Add all the relevant data required for translating the category names in
     * the cases where the category names are numerals, but the actual name is
     * in the description field
     *
     * @throws SQLException when a database exception occurs
     */
	void populateCategoryNamesDataMap(Connection connection) throws SQLException {

        String query = "SELECT pp.stable_id, ppo.name, ppo.description FROM phenotype_parameter pp "
                + "INNER JOIN phenotype_parameter_lnk_option pplo ON pp.id=pplo.parameter_id "
                + "INNER JOIN phenotype_parameter_option ppo ON ppo.id=pplo.option_id "
                + "WHERE ppo.name NOT REGEXP '^[a-zA-Z]' AND ppo.description!='' ";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                String stableId = resultSet.getString("stable_id");
                logger.debug("  parameter_stable_id for numeric category: {}", stableId);

                if (!translateCategoryNames.containsKey(stableId)) {
                    translateCategoryNames.put(stableId, new HashMap<>());
                }

                String name = resultSet.getString("name");
                String description = resultSet.getString("description");
                if (name.matches("[0-9]+")) {

                    translateCategoryNames.get(stableId).put(name, description);

                    // also add .0 onto string as sometimes this is what our numerical
                    // categories look like in the database!!!!
                    name += ".0";
                    translateCategoryNames.get(stableId).put(name, description);
                } else {
                    logger.debug("  Not translating non alphabetical category for parameter: " + stableId + ", name: "
                                         + name + ", desc:" + description);
                }
            }
        }
    }

	void populateParameterAssociationMap(Connection connection) throws SQLException {

		Map<String, String> stableIdToNameMap = this.getAllParameters(connection);
        String query = "SELECT id, observation_id, parameter_id, sequence_id, dim_id, parameter_association_value FROM parameter_association  where parameter_association_value is not  null";

        try (PreparedStatement p = connection.prepareStatement(query)) {

            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

				Long observationId = resultSet.getLong("observation_id");

                ParameterAssociationBean pb = new ParameterAssociationBean();
				pb.observationId = observationId;
                pb.parameterStableId = resultSet.getString("parameter_id");
                pb.parameterAssociationValue = resultSet.getString("parameter_association_value");
                if (stableIdToNameMap.get(pb.parameterStableId) != null) {
                    pb.parameterAssociationName = stableIdToNameMap.get(pb.parameterStableId);
                }
                pb.sequenceId = resultSet.getString("sequence_id");
                pb.dimId = resultSet.getString("dim_id");

				if (!parameterAssociationMap.containsKey(observationId)) {
					parameterAssociationMap.put(observationId, new ArrayList<>());
                }

				parameterAssociationMap.get(observationId).add(pb);
            }
        }
    }

    /**
     * Return all parameter stable ids and names
     *
     * @throws SQLException When a database error occurrs
     */
	Map<String, String> getAllParameters(Connection connection) throws SQLException {
        Map<String, String> parameters = new HashMap<>();

        String query = "SELECT stable_id, name FROM phenotype_parameter";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                parameters.put(resultSet.getString("stable_id"), resultSet.getString("name"));
            }
        }

        return parameters;
    }

	void populateExperimenterDataMap(Connection connection) throws SQLException, IOException {

        Map<String, String> nameMap = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(experimenterIdMap));
        for (String line : lines) {
            String[] fields = line.split("\t");
            nameMap.put(fields[0], fields[1]);
        }

        String query = "SELECT DISTINCT experiment_id, value, parameter_id, p.name " +
                "FROM procedure_meta_data m " +
                "INNER JOIN phenotype_parameter p ON p.stable_id=m.parameter_id " +
                "WHERE name LIKE '%experimenter%' AND value IS NOT NULL ";

        try (PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {

				if ( ! experimenterData.containsKey(resultSet.getLong("experiment_id"))) {
					experimenterData.put(resultSet.getLong("experiment_id"), new ArrayList<>());
                }

                String ids = resultSet.getString("value");
                String parameterName = resultSet.getString("name");

                for (String id : ids.split(",")) {

                    String loadId = id;

                    //Translate the experimenter ID if needed
                    if (nameMap.containsKey(id)) {
                        loadId = nameMap.get(id);
                    }

                    //Hash the ID
                    loadId = DigestUtils.md5Hex(loadId).substring(0, 5).toUpperCase();

					experimenterData.get(resultSet.getLong("experiment_id")).add(parameterName + " = " + loadId);
                }
            }
        }
    }

	void populateDatasourceDataMap(Connection connection) throws SQLException {

        List<String> queries = new ArrayList<>();
        queries.add("SELECT id, short_name as name, 'DATASOURCE' as datasource_type FROM external_db");
        queries.add("SELECT id, name, 'PROJECT' as datasource_type FROM project");

        for (String query : queries) {

            try (PreparedStatement p = connection.prepareStatement(query)) {

                ResultSet resultSet = p.executeQuery();

                while (resultSet.next()) {

                    DatasourceBean b = new DatasourceBean();

					b.id = resultSet.getLong("id");
                    b.name = resultSet.getString("name");

                    switch (resultSet.getString("datasource_type")) {
                        case "DATASOURCE":
						datasourceMap.put(resultSet.getLong("id"), b);
                            break;
                        case "PROJECT":
						projectMap.put(resultSet.getLong("id"), b);
                            break;
                    }
                }
            }
        }
    }


    /**
     * Return map of EMAP => EMAPA
     *
     * @throws IOException When a database error occurrs
     */
    void populateEmap2EmapaMap() throws IOException {
        emap2emapaIdMap = ontologyParserFactory.getEmapToEmapaMap();
    }

    /**
     * Return map of specimen ID => weight for
     *
     * @throws SQLException When a database error occurrs
     */
	void populateAnatomyMap(Connection connection) throws SQLException {

        String query = "SELECT DISTINCT p.id, p.stable_id, o.ontology_acc " +
                "FROM phenotype_parameter p " +
                "INNER JOIN phenotype_parameter_lnk_ontology_annotation l ON l.parameter_id=p.id " +
                "INNER JOIN phenotype_parameter_ontology_annotation o ON o.id=l.annotation_id " +
                "WHERE p.stable_id like '%_ALZ_%' OR p.stable_id like '%_ELZ_%' ";

		try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String ontoAcc = resultSet.getString("ontology_acc");
                if (ontoAcc != null) {
					anatomyMap.put(resultSet.getLong("id"),
                                   ontoAcc.startsWith("EMAP:") ? emap2emapaIdMap.get(ontoAcc) : ontoAcc);
                } else {
                    logger.warn(" Parameter {} missing ontology association.", resultSet.getString("stable_id"));
                }
            }
        }
    }

    private synchronized void checkAndLogProgress(String queryName) {
        long currentTimestamp = System.currentTimeMillis();
        if (currentTimestamp - lastTimestamp.get() >= (DISPLAY_INTERVAL_IN_SECONDS * 1000)) {
            logCurrentProgress(queryName, expectedDocumentCount.get(), startTimestamp);
            lastTimestamp.getAndSet(currentTimestamp);
        }
    }

    private synchronized void logCurrentProgress(String queryName, long count, long startTimestamp) {
        long now                = new Date().getTime();
        long totalTimeInMinutes = (now - startTimestamp) / 60000L;
        if (totalTimeInMinutes > 0) {
            logger.info("   Query '{}': Added {} experiments ({} experiments per minute).", queryName, count, count / totalTimeInMinutes);
        }
    }

    Map<String, Map<String, String>> getTranslateCategoryNames() {
        return translateCategoryNames;
    }

    Map<String, BiologicalDataBean> getLineBiologicalData() {
        return lineBiologicalData;
    }

    Map<String, BiologicalDataBean> getBiologicalData() {
        return biologicalData;
    }

	Map<Long, DatasourceBean> getDatasourceMap() {
        return datasourceMap;
    }

	Map<Long, DatasourceBean> getProjectMap() {
        return projectMap;
    }

    /**
     * Internal class to act as Map value DTO for biological data
     */
    static class BiologicalDataBean {

        public String        alleleAccession;
        public String        alleleSymbol;
        public Long          biologicalModelId;
        public Long          biologicalSampleId;
        public String        colonyId;
        public ZonedDateTime dateOfBirth;
        public String        externalSampleId;
        public String        geneAcc;
        public String        geneSymbol;
        public String        phenotypingCenterName;
        public Long          phenotypingCenterId;
        public String        sampleGroup;
        public String        sex;
        public String        strainAcc;
        public String        strainName;
        public String        geneticBackground;
        public String        allelicComposition;
        public String        zygosity;
        public String        productionCenterName;
        public Long          productionCenterId;
        public String        litterId;
        public Long          projectId;
        public String        projectName;
    }


    /**
     * Internal class to act as Map value DTO for datasource data
     */
    static class DatasourceBean {

		public Long   id;
        public String name;
    }

    /**
     * Internal class to act as Map value DTO for datasource data
     */
    static class ParameterAssociationBean {

        public String parameterAssociationName;
        public String parameterAssociationValue;
		public Long   id;
		public Long   observationId;
        public String parameterStableId;
        public String sequenceId;
        public String dimId;
    }
}