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

package org.mousephenotype.cda.loads.create.load.steps;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.db.pojo.Experiment;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.loads.common.*;
import org.mousephenotype.cda.loads.create.load.support.StrainMapper;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Loads the experiments from a database with a dcc schema into the cda database.
 *
 * Created by mrelac on 12/10/2016.
 *
 */
public class ExperimentLoader implements Step, Tasklet, InitializingBean {

    // How many threads used to process experiments
    private static final int N_THREADS = 40;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private CommonUtils                commonUtils = new CommonUtils();
    private StrainMapper               strainMapper;

    private final CdaSqlUtils                cdaSqlUtils;
    private final DccSqlUtils                dccSqlUtils;
    private final NamedParameterJdbcTemplate jdbcCda;
    private final StepBuilderFactory         stepBuilderFactory;

    private Set<String> badDates                     = new HashSet<>();
    private Set<String> experimentsMissingSamples    = new HashSet<>();        // value = specimenId + "_" + cda phenotypingCenterPk
    private Set<String> ignoredExperimentsInfo       = new HashSet<>();
    private Set<String> missingBackgroundStrains     = new HashSet<>();
    private Set<String> missingColonyIds             = new HashSet<>();
    private Set<String> missingColonyIdInfo          = new HashSet<>();
    private Set<String> missingProjects              = new HashSet<>();
    private Set<String> experimentsMissingProjects   = new HashSet<>();
    private Set<String> missingCenters               = new HashSet<>();
    private Set<String> experimentsMissingCenters    = new HashSet<>();
    private Set<String> missingPipelines             = new HashSet<>();
    private Set<String> experimentsMissingPipelines  = new HashSet<>();
    private Set<String> missingProcedures            = new HashSet<>();
    private Set<String> experimentsMissingProcedures = new HashSet<>();

    private Set<String> skippedExperiments           = new HashSet<>();         // A set of all experiments that were skipped (exclusive of the experiments in ingoredExperiments)
    private Set<String> unsupportedParametersMap = new HashSet<>();

    private static Set<UniqueExperimentId> ignoredExperiments;                  // Experments purposefully ignored.

    static {
        Set<UniqueExperimentId> ignoredExperimentSet = new HashSet<>();
        ignoredExperimentSet.add(new UniqueExperimentId("Ucd", "GRS_2013-10-09_4326"));
        ignoredExperimentSet.add(new UniqueExperimentId("Ucd", "GRS_2014-07-16_8800"));

        ignoredExperiments = new HashSet<>(ignoredExperimentSet);
    }

    private int lineLevelProcedureCount   = 0;
    private int sampleLevelProcedureCount = 0;

    // lookup maps returning specified parameter type list given cda procedure primary key
    final private Map<String, Allele>   allelesBySymbolMap;
    private Map<String, GenomicFeature> genesByAccMap;
    private Map<String, Strain>         strainsByNameMap;
    
    private final boolean INCLUDE_DERIVED_PARAMETERS = false;

    public ExperimentLoader(NamedParameterJdbcTemplate jdbcCda,
                            StepBuilderFactory stepBuilderFactory,
                            CdaSqlUtils cdaSqlUtils,
                            DccSqlUtils dccSqlUtils,
                            Map<String, GenomicFeature> genesByAccMap,
                            Map<String, Allele> allelesBySymbolMap,
                            Map<String, Strain> strainsByNameMap,
                            Map<String, PhenotypedColony> phenotypedColonyMap,
                            Map<String, Integer> cdaOrganisation_idMap

                            ) {
        this.jdbcCda = jdbcCda;
        this.stepBuilderFactory = stepBuilderFactory;
        this.cdaSqlUtils = cdaSqlUtils;
        this.dccSqlUtils = dccSqlUtils;
        this.genesByAccMap = genesByAccMap;
        this.allelesBySymbolMap = allelesBySymbolMap;
        this.strainsByNameMap = strainsByNameMap;
        this.phenotypedColonyMap = phenotypedColonyMap;
        this.cdaOrganisation_idMap = cdaOrganisation_idMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(jdbcCda, "jdbcCda must be set");
        Assert.notNull(stepBuilderFactory, "stepBuilderFactory must be set");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must be set");
        Assert.notNull(dccSqlUtils, "dccSqlUtils must be set");
        Assert.notNull(genesByAccMap, "genesByAccMap must be set");
        Assert.notNull(allelesBySymbolMap, "allelesBySymbolMap must be set");
        Assert.notNull(strainsByNameMap, "strainsByNameMap must be set");
        Assert.notNull(phenotypedColonyMap, "phenotypedColonyMap must be set");
        Assert.notNull(cdaOrganisation_idMap, "cdaOrganisation_idMap must be set");
    }

    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "experimentLoaderStep";
    }

    /**
     * @return true if a step that is already marked as complete can be started again.
     */
    @Override
    public boolean isAllowStartIfComplete() {
        return false;
    }

    /**
     * @return the number of times a job can be started with the same identifier.
     */
    @Override
    public int getStartLimit() {
        return 10;
    }

    /**
     * Process the step and assign progress and status meta information to the {@link StepExecution} provided. The
     * {@link Step} is responsible for setting the meta information and also saving it if required by the
     * implementation.<br>
     * <p/>
     * It is not safe to re-use an instance of {@link Step} to process multiple concurrent executions.
     *
     * @param stepExecution an entity representing the step to be executed
     * @throws JobInterruptedException if the step is interrupted externally
     */
    @Override
    public void execute(StepExecution stepExecution) throws JobInterruptedException {
        stepBuilderFactory.get("experimentLoaderStep")
                .tasklet(this)
                .build()
                .execute(stepExecution);
    }


    // lookup maps returning cda table primary key given dca unique string
    // Initialise them here, as this code gets called multiple times for different dcc data sources
    // and these maps must be cleared before their second and subsequent uses.
    private Map<String, Integer>                cdaDb_idMap = new ConcurrentHashMap<>();
    final private Map<String, Integer>          cdaOrganisation_idMap;
    final private Map<String, PhenotypedColony> phenotypedColonyMap;
    private Map<String, Integer>                cdaProject_idMap                  = new ConcurrentHashMap<>();
    private Map<String, Integer>                cdaPipeline_idMap                 = new ConcurrentHashMap<>();
    private Map<String, Integer>                cdaProcedure_idMap                = new ConcurrentHashMap<>();
    private Map<String, Integer>                cdaParameter_idMap                = new ConcurrentHashMap<>();
    private Map<String, String>                 cdaParameterNameMap               = new ConcurrentHashMap<>();              // map of impress parameter names keyed by stable_parameter_id
    private Set<String>                         derivedImpressParameters          = new HashSet<>();
    private Set<String>                         metadataAndDataAnalysisParameters = new HashSet<>();
    private Map<String, BiologicalSample>       samplesMap                        = new ConcurrentHashMap<>();              // keyed by external_id and short_name (e.g. "mouseXXX_IMPC", "mouseYYY_3i", etc)

    // DCC parameter lookup maps, keyed by procedure_pk
    private Map<Long, List<MediaParameter>>       mediaParameterMap       = new ConcurrentHashMap<>();
    private Map<Long, List<OntologyParameter>>    ontologyParameterMap    = new ConcurrentHashMap<>();
    private Map<Long, List<SeriesParameter>>      seriesParameterMap      = new ConcurrentHashMap<>();
    private Map<Long, List<SeriesMediaParameter>> seriesMediaParameterMap = new ConcurrentHashMap<>();
    private Map<Long, List<MediaSampleParameter>> mediaSampleParameterMap = new ConcurrentHashMap<>();

    // BiologicalModel map
    private Map<BioModelKey, Integer> bioModelMap = new ConcurrentHashMap<>();           // key is composite key. Value is biological model primary key.


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        String message = "**** LOADING " + dccSqlUtils.getDbName() + " EXPERIMENTS ****";
        logger.info(org.apache.commons.lang3.StringUtils.repeat("*", message.length()));
        logger.info(message);
        logger.info(org.apache.commons.lang3.StringUtils.repeat("*", message.length()));

        long startStep = new Date().getTime();

        CommonUtils.printJvmMemoryConfiguration();

        List<DccExperimentDTO> dccExperiments = dccSqlUtils.getExperiments();
        Map<String, Integer> counts;

        strainMapper = new StrainMapper(cdaSqlUtils);

        CommonUtils.printJvmMemoryConfiguration();

        // Initialise maps. If they are not null, clear them first, as this method gets called multiple times to
        // load data from different dcc databases.
        logger.info("Loading lookup maps started");

        cdaDb_idMap.clear();
        cdaDb_idMap = cdaSqlUtils.getCdaDb_idsByDccDatasourceShortName();
        logger.info("loaded {} db_id rows", cdaDb_idMap.size());

        cdaProject_idMap.clear();
        cdaProject_idMap = cdaSqlUtils.getCdaProject_idsByDccProject();
        logger.info("loaded {} project rows", cdaProject_idMap.size());

        cdaPipeline_idMap.clear();
        cdaPipeline_idMap = cdaSqlUtils.getCdaPipeline_idsByDccPipeline();
        logger.info("loaded {} pipeline rows", cdaPipeline_idMap.size());

        cdaProcedure_idMap.clear();
        cdaProcedure_idMap = cdaSqlUtils.getCdaProcedure_idsByDccProcedureId();
        logger.info("loaded {} procedure rows", cdaProcedure_idMap.size());

        cdaParameter_idMap.clear();
        cdaParameter_idMap = cdaSqlUtils.getCdaParameter_idsByDccParameterId();
        logger.info("loaded {} parameter rows", cdaParameter_idMap.size());

        cdaParameterNameMap.clear();
        cdaParameterNameMap = cdaSqlUtils.getCdaParameterNames();
        logger.info("loaded {} parameterName rows", cdaParameterNameMap.size());

        derivedImpressParameters.clear();
        derivedImpressParameters = cdaSqlUtils.getImpressDerivedParameters();
        logger.info("loaded {} derivedImpressParameter rows", derivedImpressParameters.size());

        metadataAndDataAnalysisParameters.clear();
        metadataAndDataAnalysisParameters = cdaSqlUtils.getImpressMetadataAndDataAnalysisParameters();
        logger.info("loaded {} requiredImpressParameter rows", metadataAndDataAnalysisParameters.size());

        samplesMap.clear();
        samplesMap = cdaSqlUtils.getBiologicalSamples();
        logger.info("loaded {} sample rows", samplesMap.size());


        // Load DCC parameter maps.
        mediaParameterMap.clear();
        mediaParameterMap = dccSqlUtils.getMediaParameters();
        logger.info("loaded {} mediaParameter rows", mediaParameterMap.size());

        ontologyParameterMap.clear();
        ontologyParameterMap = dccSqlUtils.getOntologyParameters();
        logger.info("loaded {} ontologyParameter rows", ontologyParameterMap.size());

        seriesParameterMap.clear();
        seriesParameterMap = dccSqlUtils.getSeriesParameters();
        logger.info("loaded {} seriesParameter rows", seriesParameterMap.size());

        seriesMediaParameterMap.clear();
        seriesMediaParameterMap = dccSqlUtils.getSeriesMediaParameters();
        logger.info("loaded {} seriesMediaParameter rows", seriesMediaParameterMap.size());

        mediaSampleParameterMap.clear();
        mediaSampleParameterMap = dccSqlUtils.getMediaSampleParameters();
        logger.info("loaded {} mediaSampleParameter rows", mediaSampleParameterMap.size());

        logger.info("Loading lookup maps finished");

        CommonUtils.printJvmMemoryConfiguration();

//        cdaSqlUtils.manageIndexes("experiment", CdaSqlUtils.IndexAction.DISABLE);
//        cdaSqlUtils.manageIndexes("observation", CdaSqlUtils.IndexAction.DISABLE);
//        cdaSqlUtils.manageIndexes("procedure_meta_data", CdaSqlUtils.IndexAction.DISABLE);
//        cdaSqlUtils.manageIndexes("categorical_observation", CdaSqlUtils.IndexAction.DISABLE);
//        cdaSqlUtils.manageIndexes("datetime_observation", CdaSqlUtils.IndexAction.DISABLE);
//        cdaSqlUtils.manageIndexes("image_record_observation", CdaSqlUtils.IndexAction.DISABLE);
//        cdaSqlUtils.manageIndexes("text_observation", CdaSqlUtils.IndexAction.DISABLE);
//        cdaSqlUtils.manageIndexes("time_series_observation", CdaSqlUtils.IndexAction.DISABLE);
//        cdaSqlUtils.manageIndexes("unidimensional_observation", CdaSqlUtils.IndexAction.DISABLE);

        int experimentCount = 0;
        int skippedExperimentsCount = 0;

        ExecutorService executor = Executors.newFixedThreadPool(N_THREADS);

        List<Future<Experiment>> tasks = new ArrayList<>();

        for (DccExperimentDTO dccExperiment : dccExperiments) {

            // Skip purposefully ignored experiments.
            UniqueExperimentId uniqueExperiment = new UniqueExperimentId(dccExperiment.getPhenotypingCenter(), dccExperiment.getExperimentId());

            if (ignoredExperiments.contains(uniqueExperiment)) {
                ignoredExperimentsInfo.add("Ignoring center::experiment " + ignoredExperiments.toString());
                continue;
            }

            // Skip any experiments with known bad colony ids.
            if (DccSqlUtils.knownBadColonyIds.contains(dccExperiment.getColonyId())) {
                skippedExperiments.add(dccExperiment.getDatasourceShortName() + " experiment " + dccExperiment.getExperimentId());
                skippedExperimentsCount++;
                continue;
            }

            Callable<Experiment> task = () -> insertExperiment(dccExperiment);
            tasks.add(executor.submit(task));

            experimentCount++;
            if (experimentCount % 100000 == 0) {
                logger.info("Submitted {} experiments", experimentCount);

                // Drain the queue so we don't run out of memory
                while (true) {
                    Integer done = 0;
                    Integer left = 0;
                    for (Future<Experiment> future : tasks) {
                        if (future.isDone()) {
                            done += 1;

                        } else {
                            left += 1;
                        }
                    }

                    if (left == 0) {
                        tasks = new ArrayList<>();
                        break;
                    }
                    Thread.sleep(5000);

                }
            }
        }

        // Drain the final set
        logger.info("Processing final queue of " + tasks.size());
        executor.shutdown();

        logger.info("Loading complete.");

        CommonUtils.printJvmMemoryConfiguration();

        // Print out the counts.
        List<List<String>> loadCounts = null;
        try {
            loadCounts = cdaSqlUtils.getLoadCounts();   // This threw an uncategorized SQLException for SQL [SELECT COUNT(*) FROM experiment]; SQL state [null]; error code [0]; Connection has already been closed.
        } catch (Exception e) {
            logger.warn(e.getLocalizedMessage());
            e.printStackTrace();
        }

        if (loadCounts == null) {
            System.out.println("Unable to get load counts.");
        } else {
            List<String> headingList = Arrays.asList("experiment", "biological_sample", "live_sample", "procedure_meta_data", "observation", "categorical", "date_time", "image_record", "text", "time_series", "unidimensional");
            String       borderRow   = StringUtils.repeat("*", StringUtils.join(headingList, "    ").length() + 10);
            String       countsRow   = "";
            for (int i = 0; i < headingList.size(); i++) {
                if (i > 0)
                    countsRow += "    ";
                countsRow += String.format("%" + headingList.get(i).length() + "." + headingList.get(i).length() + "s", loadCounts.get(1).get(i));
            }

            System.out.println(borderRow);
            System.out.println("**** COUNTS for " + cdaSqlUtils.getDbName() + " data loaded from " + dccSqlUtils.getDbName());
            System.out.println("**** " + StringUtils.join(headingList, "    "));
            System.out.println("**** " + countsRow);
            System.out.println(borderRow);
        }


        // Log info sets

        Iterator<String> missingColonyIdInfoIt = missingColonyIdInfo.iterator();
        while (missingColonyIdInfoIt.hasNext()) {
            logger.info(missingColonyIdInfoIt.next());
        }

        Iterator<String> ignoredExperimentsInfoIt = ignoredExperimentsInfo.iterator();
        while (ignoredExperimentsInfoIt.hasNext()) {
            logger.info(ignoredExperimentsInfoIt.next());
        }

        Iterator<String> badDatesIt = badDates.iterator();
        while (badDatesIt.hasNext()) {
            logger.info(badDatesIt.next());
        }


        // Log warning sets

        Iterator<String> missingColonyIdsIt = missingColonyIds.iterator();
        while (missingColonyIdsIt.hasNext()) {
            logger.warn(missingColonyIdsIt.next());
        }

        Iterator<String> missingBackgroundStrainsIt = missingBackgroundStrains.iterator();
        while (missingBackgroundStrainsIt.hasNext()) {
            logger.info(missingBackgroundStrainsIt.next());
        }

        Iterator<String> experimentsMissingSamplesIt = experimentsMissingSamples.iterator();
        while (experimentsMissingSamplesIt.hasNext()) {
            logger.warn(experimentsMissingSamplesIt.next());
        }
        
        Iterator<String> missingProjectsIt = missingProjects.iterator();
        while (missingProjectsIt.hasNext()) {
            logger.warn(missingProjectsIt.next());
        }
        
        Iterator<String> experimentsMissingProjectsIt = experimentsMissingProjects.iterator();
        while (experimentsMissingProjectsIt.hasNext()) {
            logger.warn(experimentsMissingProjectsIt.next());
        }
        
        Iterator<String> missingCentersIt = missingCenters.iterator();
        while (missingCentersIt.hasNext()) {
            logger.warn(missingCentersIt.next());
        }
        
        Iterator<String> experimentsMissingCentersIt = experimentsMissingCenters.iterator();
        while (experimentsMissingCentersIt.hasNext()) {
            logger.warn(experimentsMissingCentersIt.next());
        }
        
        Iterator<String> missingPipelinesIt = missingPipelines.iterator();
        while (missingPipelinesIt.hasNext()) {
            logger.warn(missingPipelinesIt.next());
        }
        
        Iterator<String> experimentsMissingPipelinesIt = experimentsMissingPipelines.iterator();
        while (experimentsMissingPipelinesIt.hasNext()) {
            logger.warn(experimentsMissingPipelinesIt.next());
        }
        
        Iterator<String> missingProceduresIt = missingProcedures.iterator();
        while (missingProceduresIt.hasNext()) {
            logger.warn(missingProceduresIt.next());
        }
        
        Iterator<String> experimentsMissingProceduresIt = experimentsMissingProcedures.iterator();
        while (experimentsMissingProceduresIt.hasNext()) {
            logger.warn(experimentsMissingProceduresIt.next());
        }


//        logger.info("Skipped {} experiments because of known bad colony id", skippedExperimentsCount);

        logger.info("Wrote {} sample-Level procedures", sampleLevelProcedureCount);
        logger.info("Wrote {} line-Level procedures", lineLevelProcedureCount);


//        // ENABLE INDEXES
//        logger.info("Enabling indexes for experiment");
//        cdaSqlUtils.manageIndexes("experiment", CdaSqlUtils.IndexAction.ENABLE);
//
//        logger.info("Enabling indexes for observation");
//        cdaSqlUtils.manageIndexes("observation", CdaSqlUtils.IndexAction.ENABLE);
//
//        logger.info("Enabling indexes for procedure_meta_data");
//        cdaSqlUtils.manageIndexes("procedure_meta_data", CdaSqlUtils.IndexAction.ENABLE);
//
//        logger.info("Enabling indexes for categorical_observation");
//        cdaSqlUtils.manageIndexes("categorical_observation", CdaSqlUtils.IndexAction.ENABLE);
//
//        logger.info("Enabling indexes for datetime_observation");
//        cdaSqlUtils.manageIndexes("datetime_observation", CdaSqlUtils.IndexAction.ENABLE);
//
//        logger.info("Enabling indexes for image_record_observation");
//        cdaSqlUtils.manageIndexes("image_record_observation", CdaSqlUtils.IndexAction.ENABLE);
//
//        logger.info("Enabling indexes for text_observation");
//        cdaSqlUtils.manageIndexes("text_observation", CdaSqlUtils.IndexAction.ENABLE);
//
//        logger.info("Enabling indexes for time_series_observation");
//        cdaSqlUtils.manageIndexes("time_series_observation", CdaSqlUtils.IndexAction.ENABLE);
//
//        logger.info("Enabling indexes for unidimensional_observation");
//        cdaSqlUtils.manageIndexes("unidimensional_observation", CdaSqlUtils.IndexAction.ENABLE);


        logger.debug("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));
        contribution.setExitStatus(ExitStatus.COMPLETED);
        chunkContext.setComplete();

        return RepeatStatus.FINISHED;
    }


    public Experiment insertExperiment(DccExperimentDTO dccExperiment) throws DataLoadException {

        Experiment experiment = createExperiment(dccExperiment);

        return experiment;
    }

    private String buildSamplesMapKey(DccExperimentDTO dccExperiment) {
        return dccExperiment.getSpecimenId() + "_" + dccExperiment.getDatasourceShortName();
    }

    private Experiment createExperiment(DccExperimentDTO dccExperiment) throws DataLoadException {
        Experiment experiment = new Experiment();
        int dbId;
        Integer phenotypingCenterPk;
        String phenotypingCenter;
        Integer projectPk;
        Integer pipelinePk;
        String pipelineStableId;
        Integer procedurePk;
        String procedureStableId;
        String externalId;
        String procedureStatus;
        String procedureStatusMessage;

        String colonyId;
        Date dateOfExperiment;
        String sequenceId;

        Integer biologicalModelPk;
        Integer biologicalSamplePk;
        String metadataCombined;
        String metadataGroup;

        dbId = cdaDb_idMap.get(dccExperiment.getDatasourceShortName());
        phenotypingCenterPk = cdaOrganisation_idMap.get(dccExperiment.getPhenotypingCenter());
        phenotypingCenter = LoadUtils.mappedExternalCenterNames.get(dccExperiment.getPhenotypingCenter());
        if (phenotypingCenterPk == null) {
            missingCenters.add("Missing phenotyping center '" + dccExperiment.getPhenotypingCenter() + "'");
            if (dccExperiment.isLineLevel()) {
                experimentsMissingCenters.add("Null/invalid phenotyping center '" + dccExperiment.getPhenotypingCenter() + "'\tproject::line\t" + dccExperiment.getProject() + "::" + dccExperiment.getExperimentId());
            } else {
                experimentsMissingCenters.add("Null/invalid phenotyping center '" + dccExperiment.getPhenotypingCenter() + "'\tproject::experiment\t" + dccExperiment.getProject() + "::" + dccExperiment.getExperimentId());
            }
            return null;
        }

        /**
         * Some dcc europhenome colonies were incorrectly associated with EuroPhenome. Imits has the authoritative mapping
         * between colonyId and project and, for these incorrect colonies, overrides the dbId to reflect the real owner
         * of the data, MGP.
         *
         * Ignore mice with colonyId starting with 'baseline', as these specimens are not in imits.
         */
        if ((dccExperiment.getDatasourceShortName().equals(CdaSqlUtils.EUROPHENOME) &&
                ( ! dccExperiment.getColonyId().toLowerCase().startsWith("baseline")))) {

            PhenotypedColony phenotypedColony = phenotypedColonyMap.get(dccExperiment.getColonyId());
            if ((phenotypedColony == null) || (phenotypedColony.getColonyName() == null)) {
                String errMsg = "Unable to get phenotypedColony for experiment samples for colonyId {} to apply special MGP" +
                                " remap rule for EuroPhenome. Rule NOT applied." + dccExperiment.getColonyId();
                missingColonyIds.add(errMsg);

                return null;
            }

            if (phenotypedColony.getPhenotypingConsortium().getName().equals(CdaSqlUtils.MGP)) {
                dccExperiment.setProject(CdaSqlUtils.MGP);
                dccExperiment.setDatasourceShortName(CdaSqlUtils.MGP);
                dbId = cdaDb_idMap.get(dccExperiment.getDatasourceShortName());
            }

            // Make sure to remap the phenotypingCenterPk and phenotypingCenter to the iMits value, which takes precedence.
            phenotypingCenterPk = phenotypedColony.getPhenotypingCentre().getId();
            phenotypingCenter = LoadUtils.mappedExternalCenterNames.get(phenotypedColony.getPhenotypingCentre().getName());
        }

        projectPk = cdaProject_idMap.get(dccExperiment.getProject());
        if (projectPk == null) {
            missingProjects.add("Missing project '" + dccExperiment.getProject() + "'");
            if (dccExperiment.isLineLevel()) {
                experimentsMissingProjects.add("Null/invalid project '" + dccExperiment.getProject() + "'\tcenter::line\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getExperimentId());
            } else {
                experimentsMissingProjects.add("Null/invalid project '" + dccExperiment.getProject() + "'\tcenter::experiment\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getExperimentId());
            }
            return null;
        }
        pipelinePk = cdaPipeline_idMap.get(dccExperiment.getPipeline());
        if (pipelinePk == null) {
            missingPipelines.add("Missing pipeline '" + dccExperiment.getPipeline() + "'");
            if (dccExperiment.isLineLevel()) {
                experimentsMissingPipelines.add("Null/invalid pipeline '" + dccExperiment.getPipeline() + "'\tcenter::line\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getExperimentId());
            } else {
                experimentsMissingPipelines.add("Null/invalid pipeline '" + dccExperiment.getPipeline() + "'\tcenter::experiment\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getExperimentId());
            }
            return null;
        }
        pipelineStableId = dccExperiment.getPipeline();
        procedurePk = cdaProcedure_idMap.get(dccExperiment.getProcedureId());
        if (procedurePk == null) {
            missingProcedures.add("Missing procedure '" + dccExperiment.getProcedureId() + "'");
            if (dccExperiment.isLineLevel()) {
                experimentsMissingProcedures.add("Null/invalid procedure '" + dccExperiment.getProcedureId() + "'\tcenter::line\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getExperimentId());
            } else {
                experimentsMissingProcedures.add("Null/invalid procedure '" + dccExperiment.getProcedureId() + "'\tcenter::experiment\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getExperimentId());
            }
            return null;
        }
        procedureStableId = dccExperiment.getProcedureId();
        externalId = dccExperiment.getExperimentId();

        String[] rawProcedureStatus;

        try {
            rawProcedureStatus = commonUtils.parseImpressStatus(dccExperiment.getRawProcedureStatus());
        } catch (Exception e) {
            logger.warn("Invalid procedureStatus {} for phenotyping Center {}, experimentId {}, procedure. Skipping... ",
                         dccExperiment.getPhenotypingCenter(), dccExperiment.getExperimentId(), dccExperiment.getProcedureId());
            return null;
        }
        procedureStatus = rawProcedureStatus[0];
        procedureStatusMessage = rawProcedureStatus[1];
        int missing = ((procedureStatus != null) && ( ! procedureStatus.trim().isEmpty()) ? 1 : 0);


        // Check the bioModelMap for the biological model. If it is not found, create it. NOTE: line-level experiments do not have control data.
        if (dccExperiment.isControl()) {

            // CONTROLS
            BioModelKeyControl controlKey = createBioModelControlKey(dbId, phenotypingCenterPk, dccExperiment.getSpecimenStrainId());

            synchronized (controlKey) {
                biologicalModelPk = bioModelMap.get(controlKey);
                if (biologicalModelPk == null) {
                    biologicalModelPk = createBiologicalModelControl(controlKey);
                    if (biologicalModelPk == null) {
                        throw new DataLoadException();
                    } else {
                        bioModelMap.put(controlKey, biologicalModelPk);
                    }
                }
            }
        } else {

            // MUTANTS. Get and use iMits colony info.
            BioModelKeyMutant mutantKey = createBioModelMutantKey(dbId, phenotypingCenterPk, dccExperiment);

            synchronized (mutantKey) {
                biologicalModelPk = bioModelMap.get(mutantKey);
                if (biologicalModelPk == null) {
                    biologicalModelPk = createBiologicalModelMutant(mutantKey);
                    if (biologicalModelPk == null) {
                        throw new DataLoadException();
                    } else {
                        bioModelMap.put(mutantKey, biologicalModelPk);
                    }
                }
            }
        }


        /**
         * Set colonyId, dateOfExperiment, and sequenceId. The source is different for line-level vs specimen-level:
         *
         * -------------------------------------------------------------------------------------------------
         * | Experiment Type      |  Line-level source   | Specimen-level source                           |
         * |   colonyId           |    PhenotypedColony  |   NULL                                          |
         * |   dateOfExperiment   |    NULL              |   dccExperiment (Skip experiment if null)       |
         * |   sequenceId         |    NULL              |   dccExperiment (may be null)                   |
         * -------------------------------------------------------------------------------------------------
         */

        if (dccExperiment.isLineLevel()) {

            PhenotypedColony phenotypedColony = phenotypedColonyMap.get(dccExperiment.getColonyId());
            if ((phenotypedColony == null) || (phenotypedColony.getColonyName() == null)) {
                missingColonyIds.add("Null/invalid colony '" + dccExperiment.getColonyId() + "'\tcenter\t" + dccExperiment.getPhenotypingCenter());
                return null;
            }

            colonyId = phenotypedColony.getColonyName();
            dateOfExperiment = null;
            sequenceId = null;
            biologicalSamplePk = null;

        } else {

            colonyId = null;
            dateOfExperiment = getDateOfExperiment(dccExperiment);
            if (dateOfExperiment == null) {
                return null;
            }
            sequenceId = dccExperiment.getSequenceId();
            String samplesMapKey = buildSamplesMapKey(dccExperiment);
            biologicalSamplePk = samplesMap.get(samplesMapKey).getId();

            // Insert into the biological_model_sample table.
            cdaSqlUtils.insertBiologicalModelSample(biologicalModelPk, biologicalSamplePk);
        }

       /** Save procedure metadata into metadataCombined and metadataGroup:
        *
        * metadataCombined - All of a procedure's metadata parameters, in parameterDescription = value format. Each
        * metadata parameter is separated by a pair of colons. Each metadata lvalue is separated from its rvalue by " = ";
        * for example, for cda.experiment.external_id '8852_1943':
        *     "Equipment name = Rotarod apparatus::Equipment manufacturer = Bioseb::Equipment model = LE 8200::Surface of the rod = Foam rubber::Diameter of the rod = 4.5::Acceleration mode = 4 to 40 rpm in 5 min::Number of mice on the rod per run = 3::First inter-trial interval = 15::Second inter-trial interval = 15"
        *
        * metadataGroup - An md5 hash of only the required parameters. The hash source is the required metadata
        * parameters in the same format as <i>metadataCombined</i> above.</ul>
        */
        List<ProcedureMetadata> dccMetadataList = dccSqlUtils.getProcedureMetadata(dccExperiment.getDcc_procedure_pk());
        if (dccMetadataList == null)
            dccMetadataList = new ArrayList<>();
        ObservableList<String> metadataCombinedList = FXCollections.observableArrayList();
        ObservableList<String> metadataGroupList = FXCollections.observableArrayList();
        for (ProcedureMetadata metadata : dccMetadataList) {
            String parameterName = cdaParameterNameMap.get(metadata.getParameterID());
            metadataCombinedList.add(parameterName + " = " + metadata.getValue());
            if (metadataAndDataAnalysisParameters.contains(metadata.getParameterID())) {
                metadataGroupList.add(parameterName + " = " + metadata.getValue());
            }
        }

        // If the production center is specified and does not equal the phenotyping center, add the production center to both lists.
        if ((dccExperiment.getProductionCenter() != null) && ( ! dccExperiment.getProductionCenter().equals(dccExperiment.getPhenotypingCenter()))) {
            metadataCombinedList.add("ProductionCenter = " + dccExperiment.getProductionCenter());
            metadataGroupList.add("ProductionCenter = " + dccExperiment.getProductionCenter());
        }

        metadataCombined = StringUtils.join(metadataCombinedList, "::");
        metadataGroup = StringUtils.join(metadataGroupList, "::");
        metadataGroup = DigestUtils.md5Hex(metadataGroup);

        int experimentPk = cdaSqlUtils.insertExperiment(
                dbId,
                externalId,
                sequenceId,
                dateOfExperiment,
                phenotypingCenterPk,
                projectPk,
                pipelinePk,
                pipelineStableId,
                procedurePk,
                procedureStableId,
                colonyId,
                procedureStatus,
                procedureStatusMessage,
                biologicalModelPk,
                metadataCombined,
                metadataGroup
        );

        if (dccExperiment.isLineLevel()) {
            if (experimentPk > 0)
                lineLevelProcedureCount += 1;
        } else {
            if (experimentPk > 0) {
                sampleLevelProcedureCount += 1;
            }
        }

        // Procedure-level metadata
        cdaSqlUtils.insertProcedureMetadata(dccMetadataList, dccExperiment.getProcedureId(), experimentPk, 0);

        // Observations (including observation-level metadata)
        createObservations(dccExperiment, dbId, experimentPk, phenotypingCenter, phenotypingCenterPk, biologicalSamplePk, missing);

        return experiment;
    }

    private synchronized BioModelKeyControl createBioModelControlKey(int dbId, int phenotypingCenterPk, String specimenStrainId) throws DataLoadException {
        String zygosity = ZygosityType.homozygote.getName();
        String allelicComposition = "";
        Strain backgroundStrain = cdaSqlUtils.getBackgroundStrain(specimenStrainId);
        String geneticBackground = strainMapper.parseMultipleBackgroundStrainNames(backgroundStrain.getName());

        return new BioModelKeyControl(dbId, allelicComposition, geneticBackground, zygosity, phenotypingCenterPk, backgroundStrain);
    }

    private synchronized Integer createBiologicalModelControl(BioModelKeyControl controlKey) throws DataLoadException {

        String message;
        Integer biologicalModelPk;

        DatasourceEntityId dsId = controlKey.getBackgroundStrain().getId();
        AccDbId strain = new AccDbId(dsId.getAccession(), dsId.getDatabaseId());
        BioModelInsertDTOControl controlDto = new BioModelInsertDTOControl(controlKey.getDbId(), controlKey.getAllelicComposition(), controlKey.getGeneticBackground(), controlKey.getZygosity(), strain);

        biologicalModelPk = cdaSqlUtils.insertBiologicalModelImpc(controlDto);
        if (biologicalModelPk == null) {
            message = "BiologicalModel creation failed. BioModelInsertDTOControl: " + controlDto;
            logger.error(message);
            throw new DataLoadException(message);
        }

        return biologicalModelPk;
    }

    private synchronized BioModelKeyMutant createBioModelMutantKey(int dbId, int phenotypingCenterPk, DccExperimentDTO dccExperiment) throws DataLoadException {
        String  message;
        String  zygosity;
        String  allelicComposition;
        String  geneticBackground;

        // MUTANTS. line-level zygosity is computed differently from specimen-level zygosity.
        if (dccExperiment.isLineLevel()) {
            zygosity = getLineLevelZygosity(dccExperiment);
        } else {
            zygosity = cdaSqlUtils.getSpecimenLevelMutantZygosity(dccExperiment.getZygosity());
        }

        // Get and use iMits colony info.
        PhenotypedColony colony = phenotypedColonyMap.get(dccExperiment.getColonyId());
        if ((colony == null) || (colony.getColonyName() == null)) {
            message = "Missing colonyId " + dccExperiment.getColonyId();
            missingColonyIds.add(message);
            return null;
        }
        allelicComposition = strainMapper.createAllelicComposition(zygosity, colony.getAlleleSymbol(), colony.getGene().getSymbol(), LoadUtils.SampleGroup.EXPERIMENTAL.value());
        geneticBackground = colony.getBackgroundStrain();

        return new BioModelKeyMutant(dbId, allelicComposition, geneticBackground, zygosity, phenotypingCenterPk, colony);
    }

    private synchronized Integer createBiologicalModelMutant(BioModelKeyMutant mutantKey) throws DataLoadException {
        String  message;
        Integer biologicalModelPk;
        PhenotypedColony colony = mutantKey.getColony();

        GenomicFeature gf = genesByAccMap.get(colony.getGene().getId().getAccession());
        if (gf == null) {
            message = "Unknown gene '" + colony.getGene() + "'";
            logger.error(message);
            return null;
        }
        AccDbId gene = new AccDbId(gf.getId().getAccession(), gf.getId().getDatabaseId());

        Allele a = allelesBySymbolMap.get(colony.getAlleleSymbol());
        if (a == null) {
            a = cdaSqlUtils.createAndInsertAllele(colony.getAlleleSymbol(), colony.getGene());
            if (a == null) {
                message = "Couldn't create allele '" + colony.getAlleleSymbol() + "'";
                logger.error(message);
                throw new DataLoadException(message);
            } else {
                allelesBySymbolMap.put(a.getSymbol(), a);
            }
        }
        AccDbId allele = new AccDbId(a.getId().getAccession(), a.getId().getDatabaseId());

        Strain s = strainsByNameMap.get(colony.getBackgroundStrain());
        if (s == null) {
            s = strainMapper.createBackgroundStrain(colony.getBackgroundStrain());
            if (s == null) {
                message = "Couldn't create strain '" + colony.getBackgroundStrain() + "'";
                logger.error(message);
                throw new DataLoadException(message);
            } else {
                strainsByNameMap.put(s.getName(), s);
            }
        }
        AccDbId strain = new AccDbId(s.getId().getAccession(), s.getId().getDatabaseId());

        BioModelInsertDTOMutant mutantDto = new BioModelInsertDTOMutant(mutantKey.getDbId(), mutantKey.getAllelicComposition(), mutantKey.getGeneticBackground(), mutantKey.getZygosity(), gene, allele, strain);

        biologicalModelPk = cdaSqlUtils.insertBiologicalModelImpc(mutantDto);
        if (biologicalModelPk == null) {
            message = "BiologicalModel creation failed. BioModelInsertDTOMutant: " + mutantDto;
            logger.error(message);
            return null;
        }

        return biologicalModelPk;
    }

    private void createObservations( DccExperimentDTO dccExperiment, int dbId, int experimentPk, String phenotypingCenter, int phenotypingCenterPk, int biologicalSamplePk, int missing) throws DataLoadException {

        // simpleParameters
        List<SimpleParameter> simpleParameterList = dccSqlUtils.getSimpleParameters(dccExperiment.getDcc_procedure_pk());
        if (simpleParameterList == null)
            simpleParameterList = new ArrayList<>();
        for (SimpleParameter simpleParameter : simpleParameterList) {
            if (INCLUDE_DERIVED_PARAMETERS) {
                insertSimpleParameter(dccExperiment, simpleParameter, experimentPk, dbId, biologicalSamplePk, missing);
            } else {
                if ( ! derivedImpressParameters.contains(simpleParameter.getParameterID())) {
                    insertSimpleParameter(dccExperiment, simpleParameter, experimentPk, dbId, biologicalSamplePk, missing);
                }
            }
        }


        // mediaParameters
        List<MediaParameter> mediaParameterList = mediaParameterMap.get(dccExperiment.getDcc_procedure_pk());
        if (mediaParameterList == null)
            mediaParameterList = new ArrayList<>();
        if ((dccExperiment.isLineLevel()) && ( ! mediaParameterList.isEmpty())) {
            String errMsg = String.format("We don't currently support processing of line level MediaParameters: %s. Skipping ...", dccExperiment.getProcedureId());
            logger.warn(errMsg);
            return;
        }
        for (MediaParameter mediaParameter : mediaParameterList) {
            insertMediaParameter(dccExperiment, mediaParameter, experimentPk, dbId, biologicalSamplePk, phenotypingCenter, phenotypingCenterPk, missing);
        }


        // ontologyParameters
        List<OntologyParameter> ontologyParameterList = ontologyParameterMap.get(dccExperiment.getDcc_procedure_pk());
        if (ontologyParameterList == null)
            ontologyParameterList = new ArrayList<>();
        if ((dccExperiment.isLineLevel()) && ( ! ontologyParameterList.isEmpty())) {
            String errMsg = String.format("We don't currently support processing of line level OntologyParameters: %s. Skipping ...", dccExperiment.getProcedureId());
            logger.warn(errMsg);
            return;
        }
        for (OntologyParameter ontologyParameter : ontologyParameterList) {
            insertOntologyParameters(dccExperiment, ontologyParameter, experimentPk, dbId, biologicalSamplePk, missing);
        }


        // seriesParameters
        List<SeriesParameter> seriesParameterList = seriesParameterMap.get(dccExperiment.getDcc_procedure_pk());
        if (seriesParameterList == null)
            seriesParameterList = new ArrayList<>();
        if ((dccExperiment.isLineLevel()) && ( ! seriesParameterList.isEmpty())) {
            String errMsg = String.format("We don't currently support processing of line level SeriesParameters: %s. Skipping ...", dccExperiment.getProcedureId());
            logger.warn(errMsg);
            return;
        }
        for (SeriesParameter seriesParameter : seriesParameterList) {
            List<SeriesParameterValue> values = dccSqlUtils.getSeriesParameterValues(seriesParameter.getHjid());
            seriesParameter.setValue(values);

            if (INCLUDE_DERIVED_PARAMETERS) {
                insertSeriesParameter(dccExperiment, seriesParameter, experimentPk, dbId, biologicalSamplePk, missing);
            } else {
                if ( ! derivedImpressParameters.contains(seriesParameter.getParameterID())) {
                    insertSeriesParameter(dccExperiment, seriesParameter, experimentPk, dbId, biologicalSamplePk, missing);
                }
            }
        }


        // seriesMediaParameters
        List<SeriesMediaParameter> seriesMediaParameterList = seriesMediaParameterMap.get(dccExperiment.getDcc_procedure_pk());
        if (seriesMediaParameterList == null)
            seriesMediaParameterList = new ArrayList<>();
        if ((dccExperiment.isLineLevel()) && ( ! seriesMediaParameterList.isEmpty())) {
            String errMsg = String.format("We don't currently support processing of line level SeriesMediaParameters: %s. Skipping ...", dccExperiment.getProcedureId());
            logger.warn(errMsg);
            return;
        }
        for (SeriesMediaParameter seriesMediaParameter : seriesMediaParameterList) {
            List<SeriesMediaParameterValue> values = dccSqlUtils.getSeriesMediaParameterValues(seriesMediaParameter.getHjid());

            for (SeriesMediaParameterValue value : values) {

                // Add in parameterAssociation associations
                List<ParameterAssociation> parms = dccSqlUtils.getSeriesMediaParameterValueParameterAssociations(value.getHjid());
                value.setParameterAssociation(parms);

                // Wire in procedureMetadata associations
                List<ProcedureMetadata> pms = dccSqlUtils.getSeriesMediaParameterValueProcedureMetadataAssociations(value.getHjid());
            }

            seriesMediaParameter.setValue(values);
            insertSeriesMediaParameter(dccExperiment, seriesMediaParameter, experimentPk, dbId, biologicalSamplePk,
                                       phenotypingCenter, phenotypingCenterPk, simpleParameterList, ontologyParameterList, missing);
        }


        // mediaSampleParameters
        List<MediaSampleParameter> mediaSampleParameterList = mediaSampleParameterMap.get(dccExperiment.getDcc_procedure_pk());
        if (mediaSampleParameterList == null)
            mediaSampleParameterList = new ArrayList<>();
        if ((dccExperiment.isLineLevel()) && ( ! mediaSampleParameterList.isEmpty())) {
            String errMsg = String.format("We don't currently support processing of line level MediaSampleParameters: %s. Skipping ...", dccExperiment.getProcedureId());
            logger.warn(errMsg);
            return;
        }
        for (MediaSampleParameter mediaSampleParameter : mediaSampleParameterList) {

            insertMediaSampleParameter(dccExperiment, mediaSampleParameter, experimentPk, dbId, biologicalSamplePk,
                                       phenotypingCenter, phenotypingCenterPk, simpleParameterList, ontologyParameterList, missing);
        }
    }

    /**
     * This method is meant to be called when a non-null, non-zero date is expected. The date is validated to be:
     * not null, not zero, and between MIN_DATE and MAX_DATE, inclusive. If it is, the date is returned; otherwise,
     * null is returned.
     */
    private Date getDateOfExperiment(DccExperimentDTO dccExperiment) {
        Date dateOfExperiment;
        SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd");

        Date dccDate = dccExperiment.getDateOfExperiment();
        String message = "Invalid experiment date '" + dccDate + "' for center " + dccExperiment.getPhenotypingCenter();

        try {

            Date maxDate = new Date();
            Date minDate = dateFormat.parse("1975-01-01");

            if ( ! commonUtils.isDateValid(dccDate, minDate, maxDate)) {
                badDates.add(message);
                return null;
            }

            dateOfExperiment = dccDate;

        } catch (Exception e) {
            badDates.add(message);
            return null;
        }

        return dateOfExperiment;
    }

    /**
     * Compute the line-level [mutant] zygosity (line-level controls have no specimens and, thus, no zygosity)
     * @param dccExperiment
     * @return The zygosity string, suitable for insertion into the database
     */
    private String getLineLevelZygosity(DccExperimentDTO dccExperiment) {

        List<SimpleParameter> simpleParameters = dccSqlUtils.getSimpleParameters(dccExperiment.getDcc_procedure_pk());

        // Default zygosity is homozygous since most of the time this will be the case
        ZygosityType zygosity = ZygosityType.homozygote;

        // Check if Hemizygote
        for (SimpleParameter param : simpleParameters) {

            // Find the associated "Outcome" parameter
            if (param.getParameterID()
                     .equals("IMPC_VIA_001_001")) {

                // Found the outcome parameter, check zygosity
                String category = param.getValue();

                if (category != null && category.contains("Hemizygous")) {
                    zygosity = ZygosityType.hemizygote;
                }

                break;
            }
        }

        return zygosity.getName();
    }

    private void insertSimpleParameter(DccExperimentDTO dccExperiment, SimpleParameter simpleParameter, int experimentPk,
                                       int dbId, Integer biologicalSamplePk, int missing) throws DataLoadException {
        String parameterStableId = simpleParameter.getParameterID();
        Integer parameterPk = cdaParameter_idMap.get(parameterStableId);
        if (parameterPk == null) {
            logger.warn("Experiment {}: unknown parameterStableId {} for simpleParameter {}. Skipping...",
                    dccExperiment, parameterStableId, simpleParameter.getParameterID());
            return;
        }

        String sequenceId = (simpleParameter.getSequenceID() == null ? null : simpleParameter.getSequenceID().toString());

        ObservationType observationType = cdaSqlUtils.computeObservationType(parameterStableId, simpleParameter.getValue());

        String[] rawParameterStatus = commonUtils.parseImpressStatus(simpleParameter.getParameterStatus());
        String parameterStatus = ((rawParameterStatus != null) && (rawParameterStatus.length > 0) ? rawParameterStatus[0] : null);
        String parameterStatusMessage = ((rawParameterStatus != null) && (rawParameterStatus.length > 1) ? rawParameterStatus[1] : null);

        if (parameterStatus != null)
            missing = 1;

        int populationId = 0;


        // Special rules. May cause observation to be skipped.
        // Skip loading EuroPhenome - ICS - vagina presence - "present" male data
        // Per Mohammed SELLOUM <selloum@igbmc.fr> 5 June 2015 12:57:28 BST
        if (dccExperiment.getDatasourceShortName().equals("EuroPhenome") &&
            dccExperiment.getPhenotypingCenter().equalsIgnoreCase("ICS") &&
            parameterStableId.equals("ESLIM_001_001_125") &&
            dccExperiment.getSpecimenId() != null &&
            dccExperiment.getSex().equals(SexType.male) &&
            simpleParameter.getValue().equals("present")
            ) {

            logger.info("Special rule: skipping specimen {}, experiment {}, parameter {}, sex {} ",
                        dccExperiment.getSpecimenId(), dccExperiment.getExperimentId(),
                        parameterStableId, dccExperiment.getSex());
            return;
        }

        // If the parameter is not already marked as missing, check for null/empty values. Values are not required - sometimes
        // there is a parameterStatus instead, and sometimes an optional, empty or null value is provided. Ignore in all such cases.
        String value = simpleParameter.getValue();
        if (missing == 0) {
            if ((value == null) || value.trim().isEmpty()) {
                if ((simpleParameter.getParameterStatus() == null) || (simpleParameter.getParameterStatus().trim().isEmpty())) {
                    if (metadataAndDataAnalysisParameters.contains(simpleParameter.getParameterID())) {
                        logger.warn("Experiment {} has null/empty value and status for required simpleParameter {}",
                                    dccExperiment, simpleParameter.getParameterID());
                    }
                }
                return;
            }
        }

        int observationPk;
        try {
            observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                                                              sequenceId, populationId, observationType, missing,
                                                              parameterStatus, parameterStatusMessage,
                                                              simpleParameter);
        } catch (Exception e) {
            logger.warn("Insert of simple parameter observation for phenotyping center {} failed. Skipping... " +
                        " biologicalSamplePk {}. parameterStableId {}." +
                        " parameterPk {}. observationType {}. missing {}. parameterStatus {}. parameterStatusMessage {}." +
                        " Reason: {}",
                        dccExperiment.getPhenotypingCenter(), biologicalSamplePk, parameterStableId, parameterPk,
                        observationType, missing, parameterStatus, parameterStatusMessage, e.getLocalizedMessage());
            return;
        }

        // Insert experiment_observation
        cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
    }

    private void insertMediaParameter(DccExperimentDTO dccExperiment, MediaParameter mediaParameter,
                                      int experimentPk, int dbId, Integer biologicalSamplePk, String phenotypingCenter,
                                      int phenotypingCenterPk, int missing) throws DataLoadException
    {
        if (dccExperiment.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperiment.getExperimentId() + " contains MediaParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String parameterStableId = mediaParameter.getParameterID();
        int parameterPk = cdaParameter_idMap.get(parameterStableId);
        String sequenceId = null;
        ObservationType observationType = ObservationType.image_record;
        String URI = mediaParameter.getURI();

        String[] rawParameterStatus = commonUtils.parseImpressStatus(mediaParameter.getParameterStatus());

        String parameterStatus = rawParameterStatus[0];
        String parameterStatusMessage = rawParameterStatus[1];

        if ((parameterStatus != null) || (URI == null || URI.isEmpty() || URI.endsWith("/")))
            missing = 1;

        int populationId = 0;

        int observationPk;
        try {
            observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                                                              sequenceId, populationId, observationType, missing,
                                                              parameterStatus, parameterStatusMessage,
                                                              mediaParameter, dccExperiment, phenotypingCenter, phenotypingCenterPk);
        } catch (Exception e) {
            logger.warn("Insert of media parameter observation for phenotyping center {} failed. Skipping... " +
                                " biologicalSamplePk {}. parameterStableId {}." +
                                " parameterPk {}. observationType {}. missing {}. parameterStatus {}. parameterStatusMessage {}." +
                                " Reason: {}",
                        phenotypingCenter, biologicalSamplePk, parameterStableId, parameterPk,
                        observationType, missing, parameterStatus, parameterStatusMessage, e.getLocalizedMessage());
            return;
        }

        // Insert experiment_observation
        cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
    }

    public void insertMediaSampleParameter(DccExperimentDTO dccExperiment, MediaSampleParameter mediaSampleParameter,
                                           int experimentPk, int dbId, Integer biologicalSamplePk, String phenotypingCenter,
                                           int phenotypingCenterPk, List<SimpleParameter> simpleParameterList,
                                           List<OntologyParameter> ontologyParameterList, int missing) throws DataLoadException
    {
        if (dccExperiment.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperiment.getExperimentId() + " contains MediaSampleParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String          parameterStableId      = mediaSampleParameter.getParameterID();
        int             parameterPk            = cdaParameter_idMap.get(parameterStableId);
        int             populationId           = 0;
        String          sequenceId             = null;
        ObservationType observationType        = ObservationType.image_record;

        String[] rawParameterStatus = commonUtils.parseImpressStatus(mediaSampleParameter.getParameterStatus());
        String parameterStatus = rawParameterStatus[0];
        String parameterStatusMessage = rawParameterStatus[1];

        if (parameterStatus != null)
            missing = 1;

        String info = mediaSampleParameter.getParameterID() + mediaSampleParameter.getParameterStatus();
        String mediaSampleString = "";
        for (MediaSample mediaSample : mediaSampleParameter.getMediaSample()) {
            mediaSampleString += mediaSample.getLocalId();
            for (MediaSection mediaSection : mediaSample.getMediaSection()) {
                mediaSampleString += mediaSection.getLocalId();
                for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                    mediaSampleString += mediaFile.getFileType();
                    mediaSampleString += mediaFile.getLocalId();
                    mediaSampleString += mediaFile.getURI();
                    mediaSampleString += mediaFile.getParameterAssociation().get(0).getParameterID();
                }
            }
        }

        logger.debug("mediaSampleParam = " + info);
        logger.debug("mediaSampleString = " + mediaSampleString);

        int observationPk = 0;

        for (MediaSample mediaSample : mediaSampleParameter.getMediaSample()) {
            for (MediaSection mediaSection : mediaSample.getMediaSection()) {

                for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                    String URI = mediaFile.getURI();
                    missing = (missing == 1 || (URI == null || URI.isEmpty() || URI.endsWith("/")) ? 1 : 0);

                    try {
                        observationPk = cdaSqlUtils.insertObservation(
                                dbId, biologicalSamplePk, parameterStableId, parameterPk, sequenceId, populationId,
                                observationType, missing, parameterStatus, parameterStatusMessage, mediaSampleParameter,
                                mediaFile, dccExperiment, phenotypingCenter, phenotypingCenterPk, experimentPk,
                                simpleParameterList, ontologyParameterList);
                    } catch (Exception e) {
                        logger.warn("Insert of media sample parameter observation for phenotyping center {} failed. Skipping... " +
                                            " biologicalSamplePk {}. parameterStableId {}." +
                                            " parameterPk {}. observationType {}. missing {}. parameterStatus {}. parameterStatusMessage {}." +
                                            " experimentPk {}. Reason: {}",
                                    phenotypingCenter, biologicalSamplePk, parameterStableId, parameterPk,
                                    observationType, missing, parameterStatus, parameterStatusMessage,
                                    experimentPk, e.getLocalizedMessage());
                        continue;
                    }
                }
            }
        }

        // Insert experiment_observation
        cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
    }

    private void insertSeriesMediaParameter(DccExperimentDTO dccExperiment, SeriesMediaParameter seriesMediaParameter,
                                            int experimentPk, int dbId, Integer biologicalSamplePk, String phenotypingCenter,
                                            int phenotypingCenterPk, List<SimpleParameter> simpleParameterList,
                                            List<OntologyParameter> ontologyParameterList, int missing) throws DataLoadException
    {
        if (dccExperiment.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperiment.getExperimentId() + " contains SeriesMediaParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String parameterStableId = seriesMediaParameter.getParameterID();
        int parameterPk = cdaParameter_idMap.get(parameterStableId);
        String sequenceId = null;
        ObservationType observationType = ObservationType.image_record;

        String[] rawParameterStatus = commonUtils.parseImpressStatus(seriesMediaParameter.getParameterStatus());
        String parameterStatus = rawParameterStatus[0];
        String parameterStatusMessage = rawParameterStatus[1];

        if (parameterStatus != null)
            missing = 1;

        int populationId = 0;

        for (SeriesMediaParameterValue value : seriesMediaParameter.getValue()) {

            String URI = value.getURI();
            missing = (observationType == ObservationType.image_record && (URI == null || URI.isEmpty() || URI.endsWith("/")) ? 1 : missing);

            int observationPk;
            try {
                observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                                                              sequenceId, populationId, observationType, missing,
                                                              parameterStatus, parameterStatusMessage,
                                                              value, dccExperiment, biologicalSamplePk, phenotypingCenter,
                                                              phenotypingCenterPk, experimentPk, simpleParameterList,
                                                              ontologyParameterList);
            } catch (Exception e) {
                logger.warn("Insert of series media parameter observation for phenotyping center {} failed. Skipping... " +
                            " biologicalSamplePk {}. parameterStableId {}." +
                            " parameterPk {}. observationType {}. missing {}. parameterStatus {}. parameterStatusMessage {}." +
                            " URI {}. Reason: {}",
                            phenotypingCenter, biologicalSamplePk, parameterStableId, parameterPk,
                            observationType, missing, parameterStatus, parameterStatusMessage, value.getURI(),
                            e.getLocalizedMessage());
                e.printStackTrace();
                continue;
            }

            // Insert experiment_observation
            cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
        }
    }


    private void insertSeriesParameter(DccExperimentDTO dccExperiment, SeriesParameter seriesParameter, int experimentPk,
                                       int dbId, Integer biologicalSamplePk, int missing) throws DataLoadException {

        if (dccExperiment.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperiment.getExperimentId() + " contains SeriesParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        List<ProcedureMetadata> dccMetadataList = dccSqlUtils.getProcedureMetadata(dccExperiment.getDcc_procedure_pk());
        String parameterStableId = seriesParameter.getParameterID();

        String[] rawParameterStatus = commonUtils.parseImpressStatus(seriesParameter.getParameterStatus());
        String parameterStatus = rawParameterStatus[0];
        String parameterStatusMessage = rawParameterStatus[1];

        if (parameterStatus != null)
            missing = 1;


        for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {

            // Get the parameter data type.
            String          incrementValue  = seriesParameterValue.getIncrementValue();
            String          simpleValue     = seriesParameterValue.getValue();
            int             observationPk   = 0;
            ObservationType observationType = cdaSqlUtils.computeObservationType(parameterStableId, simpleValue);
            int             parameterPk     = cdaParameter_idMap.get(parameterStableId);
            String          sequenceId      = null;
            int             populationId    = 0;
            int             valueMissing    = missing;

            // time_series_observation variables
            Float dataPoint     = null;
            Date  timePoint     = dccExperiment.getDateOfExperiment();                                                  // timePoint for all cases. Default is dateOfExperiment.
            Float discretePoint = null;

            if (valueMissing == 0) {
                if ((simpleValue != null) && ( ! simpleValue.equals("null")) && ( ! simpleValue.trim().isEmpty())) {
                    try {
                        dataPoint = Float.parseFloat(simpleValue);                                                      // dataPoint for all cases.
                        valueMissing = 0;
                    } catch (NumberFormatException e) {
                        valueMissing = 1;
                    }
                } else {
                    valueMissing = 1;
                }
            }

            // Test increment value to see if it represents a date.
            if (incrementValue.contains("-") && (incrementValue.contains(" ") || incrementValue.contains("T"))) {

                // Time series (increment is a datetime or time) - e.g. IMPC_CAL_003_001
                SeriesParameterObservationUtils utils = new SeriesParameterObservationUtils();

                discretePoint = utils.convertTimepoint(incrementValue, dccExperiment, dccMetadataList);                 // discretePoint if increment value represents a date.

                // Parse value into correct format
                String parsedIncrementValue = utils.getParsedIncrementValue(incrementValue);
                if (parsedIncrementValue.contains("-")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    try {
                        timePoint = simpleDateFormat.parse(parsedIncrementValue);                                       // timePoint (overridden if increment value represents a date.
                        SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Date maxDate = new Date();
                        Date minDate = ymdFormat.parse("1975-01-01");
                        String message = "Invalid timepoint date '" + ymdFormat.format(timePoint) + "' for center " + dccExperiment.getPhenotypingCenter();
                        if ( ! commonUtils.isDateValid(timePoint, minDate, maxDate)) {
                            valueMissing = 1;
                            badDates.add(message);
                        }

                    } catch (ParseException e) { }
                }

            } else {

                // Not time series (increment is not a timestamp) - e.g. IMPC_GRS_004_001

                try {
                    discretePoint = Float.parseFloat(incrementValue);                                                   // discretePoint if increment value does not represent a date.
                } catch (NumberFormatException e) {
                    valueMissing = 1;
                }
            }

            try {
                observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                                                              sequenceId, populationId, observationType, valueMissing,
                                                              parameterStatus, parameterStatusMessage,
                                                              seriesParameter, dataPoint, timePoint, discretePoint);
            } catch (Exception e) {
                logger.warn("Insert of series parameter observation for phenotyping center {} failed. Skipping... " +
                                    " biologicalSamplePk {}. parameterStableId {}." +
                                    " parameterPk {}. observationType {}. missing {}. parameterStatus {}. parameterStatusMessage {}." +
                                    " dataPoint {}. timePoint {}. discretePoint {}. Reason: {}",
                            dccExperiment.getPhenotypingCenter(), biologicalSamplePk, parameterStableId, parameterPk,
                            observationType, valueMissing, parameterStatus, parameterStatusMessage, dataPoint, timePoint,
                            discretePoint, e.getLocalizedMessage());
                return;
            }

            // Insert experiment_observation
            cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
        }
    }

    private void insertOntologyParameters(DccExperimentDTO dccExperiment, OntologyParameter ontologyParameter,
                                          int experimentPk, int dbId, Integer biologicalSamplePk, int missing) throws DataLoadException
    {
        if (dccExperiment.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperiment.getExperimentId() + " contains OntologyParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String[] rawParameterStatus = commonUtils.parseImpressStatus(ontologyParameter.getParameterStatus());
        String parameterStatus = rawParameterStatus[0];
        String parameterStatusMessage = rawParameterStatus[1];

        if (parameterStatus != null)
            missing = 1;

        String parameterStableId = ontologyParameter.getParameterID();
        int parameterPk = cdaParameter_idMap.get(parameterStableId);

        Integer sequenceId = null;
        BigInteger bi = ontologyParameter.getSequenceID();
        if (bi != null) {
            try {
                sequenceId = Integer.valueOf(bi.intValue());
            } catch (Exception e) {

            }
        }

        ObservationType observationType = ObservationType.ontological;
        int populationId = 0;

        int observationPk;
        try {
            observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                                                          sequenceId, populationId, observationType, missing,
                                                          parameterStatus, parameterStatusMessage,
                                                          ontologyParameter, dccExperiment.getExperimentId(), experimentPk);
        } catch (Exception e) {
            logger.warn("Insert of ontology parameter observation for phenotyping center {} failed. Skipping... " +
                                " biologicalSamplePk {}. parameterStableId {}." +
                                " parameterPk {}. observationType {}. missing {}. parameterStatus {}. parameterStatusMessage {}." +
                                " parameterId {}. Reason: {}",
                        dccExperiment.getPhenotypingCenter(), biologicalSamplePk, parameterStableId, parameterPk,
                        observationType, missing, parameterStatus, parameterStatusMessage, ontologyParameter.getParameterID(),
                        e.getLocalizedMessage());
            return;
        }

        // Insert experiment_observation
        cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
    }


    public static class UniqueExperimentId {
        private String dccCenterName;
        private String dccExperimentId;

        public UniqueExperimentId(String dccCenterName, String dccExperimentId) {
            this.dccCenterName = dccCenterName;
            this.dccExperimentId = dccExperimentId;
        }

        public String getDccCenterName() {
            return dccCenterName;
        }

        public void setDccCenterName(String dccCenterName) {
            this.dccCenterName = dccCenterName;
        }

        public String getDccExperimentId() {
            return dccExperimentId;
        }

        public void setDccExperimentId(String dccExperimentId) {
            this.dccExperimentId = dccExperimentId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            UniqueExperimentId that = (UniqueExperimentId) o;

            if (!dccCenterName.equals(that.dccCenterName)) return false;
            return dccExperimentId.equals(that.dccExperimentId);
        }

        @Override
        public int hashCode() {
            int result = dccCenterName.hashCode();
            result = 31 * result + dccExperimentId.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return dccCenterName + "::" + dccExperimentId;
        }
    }
}