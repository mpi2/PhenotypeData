/*******************************************************************************
 * Copyright © 2015-2017 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.create.load;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.pojo.Experiment;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.loads.common.*;
import org.mousephenotype.cda.loads.create.load.support.StrainMapper;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Loads the experiments from a database with a dcc schema into the cda database.
 *
 * Created by mrelac on 12/10/201.
 *
 */
@ComponentScan
public class ExperimentLoader implements CommandLineRunner {

    // How many threads used to process experiments
    private static final int N_THREADS = 75;
    private static final Boolean ONE_AT_A_TIME = Boolean.FALSE;
    private static Boolean SHUFFLE = Boolean.FALSE;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private CommonUtils                commonUtils = new CommonUtils();

    private final CdaSqlUtils                cdaSqlUtils;
    private final DccSqlUtils                dccSqlUtils;
    private final NamedParameterJdbcTemplate jdbcCda;

    private Set<String> badDates                     = new ConcurrentSkipListSet<>();
    private Set<String> invalidXmlStrainValues       = new ConcurrentSkipListSet<>();
    private Set<String> missingColonyIds             = new ConcurrentSkipListSet<>();
    private Set<String> missingProjects              = new ConcurrentSkipListSet<>();
    private Set<String> experimentsMissingProjects   = new ConcurrentSkipListSet<>();
    private Set<String> missingCenters               = new ConcurrentSkipListSet<>();
    private Set<String> missingDatasourceShortNames  = new ConcurrentSkipListSet<>();
    private Set<String> experimentsMissingCenters    = new ConcurrentSkipListSet<>();
    private Set<String> missingPipelines             = new ConcurrentSkipListSet<>();
    private Set<String> experimentsMissingPipelines  = new ConcurrentSkipListSet<>();
    private Set<String> missingProcedures            = new ConcurrentSkipListSet<>();
    private Set<String> experimentsMissingProcedures = new ConcurrentSkipListSet<>();

    private Set<String> skippedExperiments       = new ConcurrentSkipListSet<>();         // A set of all experiments that were skipped (exclusive of the experiments in ingoredExperiments)
    private Set<String> unsupportedParametersMap = new ConcurrentSkipListSet<>();

    private static Set<UniqueExperimentId> ignoredExperiments;                  // Experments purposefully ignored.

    private int lineLevelProcedureCount   = 0;
    private int sampleLevelProcedureCount = 0;

    private final boolean INCLUDE_DERIVED_PARAMETERS = true;
    private final String  MISSING_COLONY_ID_REASON   = "ExperimentLoader: specimen was not found in phenotyped_colony table";


    // lookup maps returning cda table primary key given dca unique string
    // Initialise them here, as this code gets called multiple times for different dcc data sources
    // and these maps must be cleared before their second and subsequent uses.
    private static Map<String, Long>                   cdaDb_idMap                       = new ConcurrentHashMapAllowNull<>();
    private static Map<String, Long>                   cdaProject_idMap                  = new ConcurrentHashMapAllowNull<>();
    private static Map<String, Long>                   cdaPipeline_idMap                 = new ConcurrentHashMapAllowNull<>();
    private static Map<String, Long>                   cdaProcedure_idMap                = new ConcurrentHashMapAllowNull<>();
    private static Map<String, Long>                   cdaParameter_idMap                = new ConcurrentHashMapAllowNull<>();
    private static Map<String, String>                 cdaParameterNameMap               = new ConcurrentHashMapAllowNull<>();          // map of impress parameter names keyed by stable_parameter_id
    private static Set<String>                         derivedImpressParameters          = new ConcurrentSkipListSet<>();
    private static Set<String>                         metadataAndDataAnalysisParameters = new ConcurrentSkipListSet<>();
    private static Map<BioSampleKey, BiologicalSample> samplesMap                        = new ConcurrentHashMapAllowNull<>();
    private static StrainMapper                        strainMapper;
    private static Map<String, Strain>                 strainsByNameOrMgiAccessionIdMap;


    // DCC parameter lookup maps, keyed by procedure_pk
    private static Map<Long, List<MediaParameter>>       mediaParameterMap       = new ConcurrentHashMapAllowNull<>();
    private static Map<Long, List<OntologyParameter>>    ontologyParameterMap    = new ConcurrentHashMapAllowNull<>();
    private static Map<Long, List<SeriesParameter>>      seriesParameterMap      = new ConcurrentHashMapAllowNull<>();
    private static Map<Long, List<SeriesMediaParameter>> seriesMediaParameterMap = new ConcurrentHashMapAllowNull<>();
    private static Map<Long, List<MediaSampleParameter>> mediaSampleParameterMap = new ConcurrentHashMapAllowNull<>();

    private static BioModelManager               bioModelManager;
    private static Map<String, Long>             cdaOrganisation_idMap;
    private static Map<String, PhenotypedColony> phenotypedColonyMap;
    private static Map<String, MissingColonyId>  missingColonyMap;
    private static Map<String, OntologyTerm>     ontologyTermMap;
    private static Set<String>                   imitsBackgroundStrains;

    private int bioModelsAddedCount = 0;

    static {
        Set<UniqueExperimentId> ignoredExperimentSet = new ConcurrentSkipListSet<>();
        ignoredExperimentSet.add(new UniqueExperimentId("Ucd", "GRS_2013-10-09_4326"));
        ignoredExperimentSet.add(new UniqueExperimentId("Ucd", "GRS_2014-07-16_8800"));

        ignoredExperiments = new ConcurrentSkipListSet<>(ignoredExperimentSet);
    }


    public ExperimentLoader(NamedParameterJdbcTemplate jdbcCda,
                           CdaSqlUtils cdaSqlUtils,
                           DccSqlUtils dccSqlUtils) {
        this.jdbcCda = jdbcCda;
        this.cdaSqlUtils = cdaSqlUtils;
        this.dccSqlUtils = dccSqlUtils;
    }


    public static void main(String[] args) {

        new SpringApplicationBuilder(ExperimentLoader.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);
    }


    @Override
    public void run(String... strings) throws DataLoadException {

        Assert.notNull(jdbcCda, "jdbcCda must not be null");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must not be null");
        Assert.notNull(dccSqlUtils, "dccSqlUtils must not be null");

        bioModelManager = new BioModelManager(cdaSqlUtils, dccSqlUtils);
        cdaOrganisation_idMap = new ConcurrentHashMapAllowNull<>(cdaSqlUtils.getCdaOrganisation_idsByDccCenterId());
        phenotypedColonyMap = bioModelManager.getPhenotypedColonyMap();
        missingColonyMap = new ConcurrentHashMapAllowNull<>(cdaSqlUtils.getMissingColonyIdsMap());
        ontologyTermMap = new ConcurrentHashMapAllowNull<>(bioModelManager.getOntologyTermMap());
        imitsBackgroundStrains = cdaSqlUtils.getImitsBackgroundStrains();
        OntologyTerm impcUncharacterizedBackgroundStrain = ontologyTermMap.get(CdaSqlUtils.IMPC_UNCHARACTERIZED_BACKGROUND_STRAIN);
        strainMapper = new StrainMapper(cdaSqlUtils, bioModelManager.getStrainsByNameOrMgiAccessionIdMap(), impcUncharacterizedBackgroundStrain);
        strainsByNameOrMgiAccessionIdMap = bioModelManager.getStrainsByNameOrMgiAccessionIdMap();

        Assert.notNull(bioModelManager, "bioModelManager must not be null");
        Assert.notNull(cdaOrganisation_idMap, "cdaOrganisation_idMap must not be null");
        Assert.notNull(phenotypedColonyMap, "phenotypedColonyMap must not be null");
        Assert.notNull(missingColonyMap, "missingColonyMap must not be null");
        Assert.notNull(ontologyTermMap, "ontologyTermMap must not be null");
        Assert.notNull(imitsBackgroundStrains, "imitsBackgroundStrains must not be null");
        Assert.notNull(strainMapper, "strainMapper must not be null");
        Assert.notNull(strainsByNameOrMgiAccessionIdMap, "strainsByNameOrMgiAccessionIdMap must not be null");

        long startStep = new Date().getTime();


        logger.info("Getting experiments");
        List<DccExperimentDTO> dccExperiments = dccSqlUtils.getExperiments();

        // Sometimes helpful to load the experiments in other-than-file order (for testing, etc.)
        if (SHUFFLE) {
            Collections.shuffle(dccExperiments);
        }

        logger.info("Getting experiments complete. Loading {} experiments from DCC.", dccExperiments.size());

        CommonUtils.printJvmMemoryConfiguration();


        String banner = "**** LOADING " + dccSqlUtils.getDbName() + " EXPERIMENTS ****";
        logger.info(org.apache.commons.lang3.StringUtils.repeat("*", banner.length()));
        logger.info(banner);
        logger.info(org.apache.commons.lang3.StringUtils.repeat("*", banner.length()));


        CommonUtils.printJvmMemoryConfiguration();

        logger.info("Loading lookup maps started");

        cdaDb_idMap = cdaSqlUtils.getCdaDb_idsByDccDatasourceShortName();
        logger.info("loaded {} db_id rows", cdaDb_idMap.size());

        cdaProject_idMap = cdaSqlUtils.getCdaProject_idsByDccProject();
        logger.info("loaded {} project rows", cdaProject_idMap.size());

        cdaPipeline_idMap = cdaSqlUtils.getCdaPipeline_idsByDccPipeline();
        logger.info("loaded {} pipeline rows", cdaPipeline_idMap.size());

        cdaProcedure_idMap = cdaSqlUtils.getCdaProcedure_idsByDccProcedureId();
        logger.info("loaded {} procedure rows", cdaProcedure_idMap.size());

        cdaParameter_idMap = cdaSqlUtils.getCdaParameter_idsByDccParameterId();
        logger.info("loaded {} parameter rows", cdaParameter_idMap.size());

        cdaParameterNameMap = cdaSqlUtils.getCdaParameterNames();
        logger.info("loaded {} parameterName rows", cdaParameterNameMap.size());

        derivedImpressParameters = cdaSqlUtils.getImpressDerivedParameters();
        logger.info("loaded {} derivedImpressParameter rows", derivedImpressParameters.size());

        metadataAndDataAnalysisParameters = cdaSqlUtils.getImpressMetadataAndIsImportantParameters();
        logger.info("loaded {} requiredImpressParameter rows", metadataAndDataAnalysisParameters.size());

        samplesMap = cdaSqlUtils.getBiologicalSamplesMapBySampleKey();
        logger.info("loaded {} sample rows", samplesMap.size());


        // Load DCC parameter maps.
        mediaParameterMap = dccSqlUtils.getMediaParameters();
        logger.info("loaded {} mediaParameter rows", mediaParameterMap.size());

        ontologyParameterMap = dccSqlUtils.getOntologyParameters();
        logger.info("loaded {} ontologyParameter rows", ontologyParameterMap.size());

        seriesParameterMap = dccSqlUtils.getSeriesParameters();
        logger.info("loaded {} seriesParameter rows", seriesParameterMap.size());

        seriesMediaParameterMap = dccSqlUtils.getSeriesMediaParameters();
        logger.info("loaded {} seriesMediaParameter rows", seriesMediaParameterMap.size());

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
                continue;
            }

            experimentCount++;

            if (ONE_AT_A_TIME) {

                insertExperiment(dccExperiment);

            } else {

                Callable<Experiment> task = () -> insertExperiment(dccExperiment);
                tasks.add(executor.submit(task));

                if (experimentCount % 100000 == 0) {
                    tasks = drainQueue(tasks);
                }
            }
        }

        // Drain the final set
        if ( ! ONE_AT_A_TIME) {
            logger.info("Processing final queue of " + tasks.size());
            if ( ! tasks.isEmpty()) {
                drainQueue(tasks);
            }
        }
        executor.shutdown();

        logger.info("Loading complete.");

        CommonUtils.printJvmMemoryConfiguration();

        // Print out the counts.
        List<List<String>> loadCounts = null;
        try {
            loadCounts = cdaSqlUtils.getLoadCounts();
        } catch (Exception e) {
            logger.warn(e.getLocalizedMessage());
            e.printStackTrace();
        }

        if (loadCounts == null) {
            System.out.println("Unable to get load counts.");
        } else {
            List<String> headingList = Arrays.asList("experiment", "procedure_meta_data", "observation", "categorical", "date_time", "image_record", "text", "time_series", "unidimensional");
            String       borderRow   = StringUtils.repeat("*", StringUtils.join(headingList, "    ").length() + 10);
            StringBuilder countsRow  = new StringBuilder();
            for (int i = 0; i < headingList.size(); i++) {
                if (i > 0) {
                    countsRow.append("    ");
                }
                countsRow.append(String.format("%" + headingList.get(i).length() + "." + headingList.get(i).length() + "s", loadCounts.get(1).get(i)));
            }

            System.out.println(borderRow);
            System.out.println("**** COUNTS for " + cdaSqlUtils.getDbName() + " data loaded from " + dccSqlUtils.getDbName());
            System.out.println("**** " + StringUtils.join(headingList, "    "));
            System.out.println("**** " + countsRow);
            System.out.println(borderRow);
        }

        System.out.println("New biological models for line-level experiments: " + bioModelsAddedCount);


        // Log infos


        if ( ! invalidXmlStrainValues.isEmpty()) {
            logger.info("Found {} invalid XML background strain values (remapped to IMITS background strain)." +
                        " See SampleLoader job output for values.", invalidXmlStrainValues.size());
//            invalidXmlStrainValues.stream().sorted().forEach(System.out::println);
        }

        long missingColonyMapFilteredCount = missingColonyMap.values()
                .stream()
                .filter(missing -> missing.getLogLevel() >= 0)      // Known missing have log level -1. Ignore them.
                .count();

        if (missingColonyMapFilteredCount > 0) {
            logger.info("Missing colonyIds::reason");
            missingColonyMap.values()
                    .stream()
                    .sorted(Comparator.comparing(MissingColonyId::getColonyId))
                    .filter(missing -> missing.getLogLevel() >= 0)      // Known missing have log level -1. Ignore them.
                    .forEach(missing -> System.out.println(missing.getColonyId() + "::" + missing.getReason()));
        }

        if ( ! badDates.isEmpty()) {
            logger.info("Bad/invalid dates:");
            badDates.stream().sorted().forEach(System.out::println);
        }


        // Log warnings

        // Remove any colonyIds that are already known to be missing.
        missingColonyIds = missingColonyIds
                .stream()
                .filter(colonyId -> ! missingColonyMap.containsKey(colonyId))
                .collect(Collectors.toSet());

        // Log any remaining missing colonyIds and add them to the missing_colony_id table.
        if ( ! missingColonyIds.isEmpty()) {
            logger.warn("{} missing colony ids:", missingColonyIds.size());
            missingColonyIds
                    .stream()
                    .sorted()
                    .map(colonyId -> {
                        System.out.println(colonyId);
                        cdaSqlUtils.insertMissingColonyId(colonyId, 0, "Missing from ExperimentLoader");
                        return colonyId;
                    })
                    .collect(Collectors.toList());
        }

        if ( ! missingDatasourceShortNames.isEmpty()) {
            logger.warn("Missing datasourceShortNames:");
            missingDatasourceShortNames.stream().sorted().forEach(System.out::println);
        }

        if ( ! missingProjects.isEmpty()) {
            logger.warn("Missing projects:");
            missingProjects.stream().sorted().forEach(System.out::println);
//            experimentsMissingProjects.stream().sorted().forEach(System.out::println);
        }

        if ( ! missingCenters.isEmpty()) {
            logger.warn("Missing centers: ");
            missingCenters.stream().sorted().forEach(System.out::println);
//            experimentsMissingCenters.stream().sorted().forEach(System.out::println);
        }

        if ( ! missingPipelines.isEmpty()) {
            logger.warn("Missing pipelines: ");
            missingPipelines.stream().sorted().forEach(System.out::println);
//            experimentsMissingPipelines.stream().sorted().forEach(System.out::println);
        }

        if ( ! missingProcedures.isEmpty()) {
            logger.warn("Missing procedures: ");
            missingProcedures.stream().sorted().forEach(System.out::println);
        }

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
    }

    private List<Future<Experiment>> drainQueue(List<Future<Experiment>> tasks) {
        while (true) {
            Integer left = 0;
            for (Future<Experiment> future : tasks) {
                if (!future.isDone()) {
                    left += 1;
                }
            }

            if (left == 0) {
                tasks = new ArrayList<>();
                break;
            }

            try {
                Thread.sleep(5000);

            } catch (InterruptedException e) {
            }
        }

        return tasks;
    }

    private Experiment insertExperiment(DccExperimentDTO dccExperiment) throws DataLoadException {
        Experiment experiment          = new Experiment();
        Long       dbId;
        Long       phenotypingCenterPk = null;
        String     phenotypingCenter;
        Long       projectPk;
        Long       pipelinePk;
        String     pipelineStableId;
        Long       procedurePk;
        String     procedureStableId;
        String     externalId;
        String     procedureStatus;
        String     procedureStatusMessage;

        String colonyId;
        Date   dateOfExperiment;
        String sequenceId;

        Long   biologicalModelPk;
        Long   biologicalSamplePk;
        String metadataCombined;
        String metadataGroup;


        /*
         * Some dcc europhenome colonies were incorrectly associated with EuroPhenome. Imits has the authoritative mapping
         * between colonyId and project and, for these incorrect colonies, overrides the dbId and phenotyping center to
         * reflect the real owner of the data, MGP. Remapping changes the experiment's project and datasourceShortName.
         */
        EuroPhenomeRemapper remapper = new EuroPhenomeRemapper(dccExperiment, phenotypedColonyMap);
        if (remapper.needsRemapping()) {
            remapper.remap();
        }

        // Override the supplied 3i project with iMits version, if it's not a valid project identifier
        if (dccExperiment.getDatasourceShortName().equals(CdaSqlUtils.THREEI) &&
                ! cdaProject_idMap.containsKey(dccExperiment.getProject()))
        {

            // Set default project to MGP
            // For now, also default the control mice to the MGP project
            dccExperiment.setProject(CdaSqlUtils.MGP);

            PhenotypedColony phenotypedColony = phenotypedColonyMap.get(dccExperiment.getColonyId());
            if ((phenotypedColony == null) || (phenotypedColony.getColonyName() == null)) {
                missingColonyIds.add(dccExperiment.getColonyId());

            } else {

                // Override the project with that from the iMits record
                if (phenotypedColony.getPhenotypingConsortium() != null && phenotypedColony.getPhenotypingConsortium().getName() != null) {
                    dccExperiment.setProject(phenotypedColony.getPhenotypingConsortium().getName());
                }
            }
        }


        /*
         * Some legacy strain names use a semicolon to separate multiple strain names contained in a single
         * field. Load processing code expects the separator for multiple strains to be an asterisk. Remap any
         * such strain names here.
         *
         * NOTE: Only do this for legacy EuroPhenome sources. Overriding the colony backgroundStrain is a big deal
         *       and breaks the loader if run against IMPC (e.g. the correct colony strain 'C57BL/6N' gets overwritten
         *       with the IMPC specimen's 'C57Bl6n', which causes a new strain by that name to be created, then later
         *       causes the database to complain about duplicate biological models.
         */
        if ((dccExperiment.getSpecimenStrainId() != null) && ( ! dccExperiment.getSpecimenStrainId().isEmpty()) && (dccExperiment.getDatasourceShortName().equalsIgnoreCase(CdaSqlUtils.EUROPHENOME) || dccExperiment.getDatasourceShortName().equalsIgnoreCase(CdaSqlUtils.MGP))) {
            String remappedStrainName = strainMapper.parseMultipleBackgroundStrainNames(dccExperiment.getSpecimenStrainId());
            dccExperiment.setSpecimenStrainId(remappedStrainName);
            PhenotypedColony colony = phenotypedColonyMap.get(dccExperiment.getColonyId());
            if (colony != null) {
                colony.setBackgroundStrain(remappedStrainName);
            }
        }


        /*
         * Some centers submitting EuroPhenome legacy data used obsolete strain names that had to be hand-curated
         * to reflect the current name. The {@link StrainMapper} takes care of this remapping.
         * For example, the Akt2 specimen strains from the dcc are 129/SvEv, but must be remapped to 129S/SvEv. The
         * {@link StrainMapper} class takes care of remapping.
         *
         * NOTE: If the strain does not yet exist, it is created and inserted into the strain table.
         *
         * NOTE: This remapping takes precedence over the iMits background strain value, so if there is a
         * {@link PhenotypedColony} entry for this strain, it should be updated with the remapped strain. This
         * makes it safe to use later on.
         */


        // Get strain (remapped if necessary), phenotypingCenter, and phenotypingCenterPk. The
        // extra block was added to make sure colony, which may be null, isn't dereferenced outside this block.
        {
            PhenotypedColony colony = phenotypedColonyMap.get(dccExperiment.getColonyId());

            // For mutants, select the correct background strain.
            if ( ! dccExperiment.isControl()) {
                dccExperiment.setSpecimenStrainId(cdaSqlUtils.getMutantBackgroundStrain(dccExperiment.getSpecimenStrainId(), colony.getBackgroundStrain(), imitsBackgroundStrains, invalidXmlStrainValues, strainsByNameOrMgiAccessionIdMap));
            }

            // Run the strain name through the StrainMapper to remap incorrect legacy strain names.
            Strain remappedStrain;
            try {
                if ((dccExperiment.getSpecimenStrainId() != null) && (!dccExperiment.getSpecimenStrainId().isEmpty()) && (dccExperiment.getDatasourceShortName().equalsIgnoreCase(CdaSqlUtils.EUROPHENOME) || dccExperiment.getDatasourceShortName().equalsIgnoreCase(CdaSqlUtils.MGP))) {

                    synchronized (strainMapper) {

                        String backgroundStrainName = dccExperiment.getSpecimenStrainId();

                        remappedStrain = strainMapper.lookupBackgroundStrain(backgroundStrainName);
                        if (backgroundStrainName != null && remappedStrain == null) {
                            remappedStrain = StrainMapper.createBackgroundStrain(backgroundStrainName);
                            cdaSqlUtils.insertStrain(remappedStrain);
                            strainsByNameOrMgiAccessionIdMap.put(remappedStrain.getName(), remappedStrain);
                            strainsByNameOrMgiAccessionIdMap.put(remappedStrain.getId().getAccession(), remappedStrain);
                        }
                        if (colony != null) {
                            colony.setBackgroundStrain(remappedStrain.getName());
                        }
                    }
                }
            } catch (Exception e ) {
                e.printStackTrace();
            }

            // Get phenotypingCenter and phenotypingCenterPk.
            phenotypingCenter = LoadUtils.mappedExternalCenterNames.get(dccExperiment.getPhenotypingCenter());
            if (colony != null) {

                phenotypingCenterPk = colony.getPhenotypingCentre().getId();

            } else {

                // Ignore any missing colony ids with log_level < 1. We know they are missing. It's OK to skip them.
                MissingColonyId missing = missingColonyMap.get(dccExperiment.getColonyId());
                if ((missing != null) && (missing.getLogLevel() < 1)) {
                    return null;
                }

                if (dccExperiment.isControl()) {
                    phenotypingCenterPk = cdaOrganisation_idMap.get(dccExperiment.getPhenotypingCenter());
                } else {
                    // It is an error if a MUTANT is not found in the iMits report (i.e. its colony is null)
                    missingColonyIds.add(dccExperiment.getColonyId());
                    return null;
                }
            }
        }

        dbId = cdaDb_idMap.get(dccExperiment.getDatasourceShortName());

        if (phenotypingCenterPk == null) {
            if ( ! missingCenters.contains(dccExperiment.getPhenotypingCenter())) {
                missingCenters.add(dccExperiment.getPhenotypingCenter());
                if (dccExperiment.isLineLevel()) {
                    experimentsMissingCenters.add("Null/invalid phenotyping center '" + dccExperiment.getPhenotypingCenter() + "'\tproject::colony::line\t" + dccExperiment.getProject() + "::" + dccExperiment.getColonyId() + "::" + dccExperiment.getExperimentId());
                } else {
                    experimentsMissingCenters.add("Null/invalid phenotyping center '" + dccExperiment.getPhenotypingCenter() + "'\tproject::colony::experiment\t" + dccExperiment.getProject() + "::" + dccExperiment.getColonyId() + "::" + dccExperiment.getExperimentId());
                }
            }

            return null;
        }

        if (dbId == null) {
            missingDatasourceShortNames.add(dccExperiment.getDatasourceShortName());

            return null;
        }


        projectPk = cdaProject_idMap.get(dccExperiment.getProject());
        if (projectPk == null) {
            if ( ! missingProjects.contains(dccExperiment.getProject())) {
                missingProjects.add(dccExperiment.getProject());
                if (dccExperiment.isLineLevel()) {
                    experimentsMissingProjects.add("Null/invalid project '" + dccExperiment.getProject() + "'\tcenter::colony::line\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getColonyId() + "::" + dccExperiment.getExperimentId());
                } else {
                    experimentsMissingProjects.add("Null/invalid project '" + dccExperiment.getProject() + "'\tcenter::colony::experiment\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getColonyId() + "::" + dccExperiment.getExperimentId());
                }
            }
            return null;
        }
        pipelinePk = cdaPipeline_idMap.get(dccExperiment.getPipeline());
        if (pipelinePk == null) {
            if ( ! missingPipelines.contains(dccExperiment.getPipeline())) {
                missingPipelines.add(dccExperiment.getPipeline());
                if (dccExperiment.isLineLevel()) {
                    experimentsMissingPipelines.add("Null/invalid pipeline '" + dccExperiment.getPipeline() + "'\tcenter::colony::line\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getColonyId() + "::" + dccExperiment.getExperimentId());
                } else {
                    experimentsMissingPipelines.add("Null/invalid pipeline '" + dccExperiment.getPipeline() + "'\tcenter::colony::experiment\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getColonyId() + "::" + dccExperiment.getExperimentId());
                }
            }
            return null;
        }
        pipelineStableId = dccExperiment.getPipeline();
        procedurePk = cdaProcedure_idMap.get(dccExperiment.getProcedureId());
        if (procedurePk == null) {
            if ( ! missingProcedures.contains(dccExperiment.getProcedureId())) {
                missingProcedures.add(dccExperiment.getProcedureId());
                if (dccExperiment.isLineLevel()) {
                    experimentsMissingProcedures.add("Null/invalid procedure '" + dccExperiment.getProcedureId() + "'\tcenter::colony::line\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getColonyId() + "::" + dccExperiment.getExperimentId());
                } else {
                    experimentsMissingProcedures.add("Null/invalid procedure '" + dccExperiment.getProcedureId() + "'\tcenter::colony::experiment\t" + dccExperiment.getPhenotypingCenter() + "::" + dccExperiment.getColonyId() + "::" + dccExperiment.getExperimentId());
                }
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

        // Get the biological model primary key.
        String zygosity;
        BioModelKey key;
        BioModelKey impcDsKey;

        if (dccExperiment.isLineLevel()) {
            List<SimpleParameter> simpleParameterList = dccSqlUtils.getSimpleParameters(dccExperiment.getDcc_procedure_pk());
            zygosity = LoadUtils.getLineLevelZygosity(simpleParameterList);
            key = bioModelManager.createMutantKey(dccExperiment.getDatasourceShortName(), dccExperiment.getSpecimenStrainId(), dccExperiment.getColonyId(), zygosity);
            impcDsKey = bioModelManager.createMutantKey(CdaSqlUtils.IMPC, dccExperiment.getSpecimenStrainId(), dccExperiment.getColonyId(), zygosity);
        } else {
            if (dccExperiment.isControl()) {
                zygosity = LoadUtils.getControlZygosity();
                key = bioModelManager.createControlKey(dccExperiment.getDatasourceShortName(), dccExperiment.getSpecimenStrainId());
                impcDsKey = bioModelManager.createControlKey(CdaSqlUtils.IMPC, dccExperiment.getSpecimenStrainId());
            } else {
                zygosity = LoadUtils.getSpecimenLevelMutantZygosity(dccExperiment.getZygosity());
                key = bioModelManager.createMutantKey(dccExperiment.getDatasourceShortName(), dccExperiment.getSpecimenStrainId(), dccExperiment.getColonyId(), zygosity);
                impcDsKey = bioModelManager.createMutantKey(CdaSqlUtils.IMPC, dccExperiment.getSpecimenStrainId(), dccExperiment.getColonyId(), zygosity);
            }
        }
        biologicalModelPk = bioModelManager.getBiologicalModelPk(key);
        if (biologicalModelPk == null) {

            if (dccExperiment.isLineLevel()) {

                // This line-level experiment's biological model may not have been created yet.
                biologicalModelPk = bioModelManager.insertLineIfMissing(zygosity, dbId, phenotypingCenterPk, dccExperiment);

            } else {

                if (dccExperiment.getDatasourceShortName().equals(CdaSqlUtils.THREEI)) {                                // 3i reuses IMPC samples, so try using the same key, but with IMPC instead of 3i.
                    biologicalModelPk = bioModelManager.getBiologicalModelPk(impcDsKey);
                }

                if (biologicalModelPk == null) {

                    MissingColonyId mid = missingColonyMap.get(dccExperiment.getColonyId());                            // Skip any known missing colony ids. We already know about them.
                    if (mid == null) {
                        PhenotypedColony phenotypedColony = phenotypedColonyMap.get(dccExperiment.getColonyId());
                        if (phenotypedColony != null) {                                                                 // Skip any colony ids missing from phenotyped_colony. Add them to the missingColonyIds set below.

                            // Specimen-level experiment models should already be loaded. Log a warning and skip them if they are not.
                            String message = "Unknown sample '" + dccExperiment.getSpecimenId() + "' for experiment '" + dccExperiment.getExperimentId() + "', colonyId "
                                    + dccExperiment.getColonyId() + ". isControl = " + dccExperiment.isControl() + ". key = " + key + ". Skipping.";
                            logger.warn(message);
                        }
                    } else {
                        missingColonyIds.add(dccExperiment.getColonyId());
                    }

                    return null;
                }
            }
        }


        /*
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
                missingColonyIds.add(dccExperiment.getColonyId());
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
            BioSampleKey bioSampleKey = BioSampleKey.make(dccExperiment.getSpecimenId(), phenotypingCenterPk);
            biologicalSamplePk = samplesMap.get(bioSampleKey).getId();
        }

       /* Save procedure metadata into metadataCombined and metadataGroup:
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
        if (dccMetadataList == null) {
            dccMetadataList = new ArrayList<>();
        }

        List<String> metadataCombinedList = new ArrayList<>();
        List<String> metadataGroupList = new ArrayList<>();

        for (ProcedureMetadata metadata : dccMetadataList) {
            String parameterName = cdaParameterNameMap.get(metadata.getParameterID());

            // Do not cache the experimenter ID
            if ( ! parameterName.toLowerCase().contains("experimenter")) {
                metadataCombinedList.add(parameterName + " = " + metadata.getValue());
            }

            if (metadataAndDataAnalysisParameters.contains(metadata.getParameterID())) {
                metadataGroupList.add(parameterName + " = " + metadata.getValue());
            }
        }

        // If the production center is specified and does not equal the phenotyping center, add the production center to both lists.
        if ((dccExperiment.getProductionCenter() != null) && ( ! dccExperiment.getProductionCenter().equals(dccExperiment.getPhenotypingCenter()))) {
            metadataCombinedList.add("ProductionCenter = " + dccExperiment.getProductionCenter());
            metadataGroupList.add("ProductionCenter = " + dccExperiment.getProductionCenter());
        }

        // Put the required metadata group components in a sorted order for producing
        // The same hash for the same values irrespective of the order of the input
        Collections.sort(metadataGroupList);

        metadataCombined = StringUtils.join(metadataCombinedList, "::");
        metadataGroup = StringUtils.join(metadataGroupList, "::");
        metadataGroup = DigestUtils.md5Hex(metadataGroup);

        long experimentPk = cdaSqlUtils.insertExperiment(
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


    private void createObservations( DccExperimentDTO dccExperiment, long dbId, long experimentPk, String phenotypingCenter, long phenotypingCenterPk, Long biologicalSamplePk, int missing) throws DataLoadException {

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
            List<ProcedureMetadata> pms = dccSqlUtils.getMediaParameterProcedureMetadataAssociations(mediaParameter.getHjid());
            mediaParameter.setProcedureMetadata(pms);

            List<ParameterAssociation> pma = dccSqlUtils.getMediaParameterParameterAssociations(mediaParameter.getHjid());
            mediaParameter.setParameterAssociation(pma);

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

                try {
                    // Add in parameterAssociation associations
                    List<ParameterAssociation> parms = dccSqlUtils.getSeriesMediaParameterValueParameterAssociations(value.getHjid());
                    value.setParameterAssociation(parms);
                } catch (NullPointerException e) {
                    logger.info("Could not add parameter associations for param HJID {}", value.getHjid());
                }

                // Wire in procedureMetadata associations
                List<ProcedureMetadata> pms = dccSqlUtils.getSeriesMediaParameterValueProcedureMetadataAssociations(value.getHjid());
                value.setProcedureMetadata(pms);
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
        String message = "Invalid experiment date '" + dccDate + "'" +
                " for datasource " + dccExperiment.getDatasourceShortName() +
                ", center " + dccExperiment.getPhenotypingCenter();

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

    private void insertSimpleParameter(DccExperimentDTO dccExperiment, SimpleParameter simpleParameter, long experimentPk,
                                       long dbId, Long biologicalSamplePk, int missing) throws DataLoadException {

        if (dccExperiment.getSpecimenId() != null && dccExperiment.getSpecimenId().equals("B6NC_46853_163447") && dccExperiment.getProcedureId().startsWith("IMPC_CBC")) {
            logger.debug("CANARY -- specimen B6NC_46853_163447\n{}, \nParameter: {}", dccExperiment, simpleParameter.getParameterID());
        }

        String parameterStableId = simpleParameter.getParameterID();
        Long parameterPk = cdaParameter_idMap.get(parameterStableId);
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
            dccExperiment.getSex().equals(SexType.male.getName()) &&
            simpleParameter.getValue().equals("present"))
        {

            logger.debug("Special rule: skipping specimen {}, experiment {}, parameter {}, sex {} ",
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

        long observationPk;
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

        if (dccExperiment.getSpecimenId() != null && dccExperiment.getSpecimenId().equals("B6NC_46853_163447") && dccExperiment.getProcedureId().startsWith("IMPC_CBC")) {
            logger.debug("END CANARY -- Successfully inserted specimen B6NC_46853_163447, experimentPk {}, parameter {}", experimentPk, simpleParameter.getParameterID());
        }



    }

    private void insertMediaParameter(DccExperimentDTO dccExperiment, MediaParameter mediaParameter,
                                      long experimentPk, long dbId, Long biologicalSamplePk, String phenotypingCenter,
                                      long phenotypingCenterPk, int missing) throws DataLoadException
    {
        if (dccExperiment.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperiment.getExperimentId() + " contains MediaParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String parameterStableId = mediaParameter.getParameterID();
        long parameterPk = cdaParameter_idMap.get(parameterStableId);
        String sequenceId = null;
        ObservationType observationType = ObservationType.image_record;
        String URI = mediaParameter.getURI();

        String[] rawParameterStatus = commonUtils.parseImpressStatus(mediaParameter.getParameterStatus());

        String parameterStatus = rawParameterStatus[0];
        String parameterStatusMessage = rawParameterStatus[1];

        if ((parameterStatus != null) || (URI == null || URI.isEmpty() || URI.endsWith("/")))
            missing = 1;

        int populationId = 0;

        long observationPk;
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
                                           long experimentPk, long dbId, Long biologicalSamplePk, String phenotypingCenter,
                                           long phenotypingCenterPk, List<SimpleParameter> simpleParameterList,
                                           List<OntologyParameter> ontologyParameterList, int missing) throws DataLoadException
    {
        if (dccExperiment.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperiment.getExperimentId() + " contains MediaSampleParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String          parameterStableId = mediaSampleParameter.getParameterID();
        long            parameterPk       = cdaParameter_idMap.get(parameterStableId);
        int             populationId      = 0;
        String          sequenceId        = null;
        ObservationType observationType   = ObservationType.image_record;

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

        long observationPk = 0;

        for (MediaSample mediaSample : mediaSampleParameter.getMediaSample()) {
            for (MediaSection mediaSection : mediaSample.getMediaSection()) {

                for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                    String URI = mediaFile.getURI();
                    missing = (missing == 1 || (URI == null || URI.isEmpty() || URI.endsWith("/")) ? 1 : 0);

                    List<ProcedureMetadata> pms = dccSqlUtils.getMediaFileProcedureMetadataAssociations(mediaFile.getHjid());
                    mediaFile.setProcedureMetadata(pms);

                    List<ParameterAssociation> pma = dccSqlUtils.getMediaFileParameterAssociations(mediaFile.getHjid());
                    mediaFile.setParameterAssociation(pma);

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
                                            long experimentPk, long dbId, Long biologicalSamplePk, String phenotypingCenter,
                                            long phenotypingCenterPk, List<SimpleParameter> simpleParameterList,
                                            List<OntologyParameter> ontologyParameterList, int missing) throws DataLoadException
    {
        if (dccExperiment.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperiment.getExperimentId() + " contains SeriesMediaParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String parameterStableId = seriesMediaParameter.getParameterID();
        long parameterPk = cdaParameter_idMap.get(parameterStableId);
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
            missing = (URI == null || URI.isEmpty() || URI.endsWith("/") ? 1 : missing);

            long observationPk;
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
                continue;
            }

            // Insert experiment_observation
            cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
        }
    }


    private void insertSeriesParameter(DccExperimentDTO dccExperiment, SeriesParameter seriesParameter, long experimentPk,
                                       long dbId, Long biologicalSamplePk, int missing) throws DataLoadException {

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
            Long            observationPk   = null;
            ObservationType observationType = cdaSqlUtils.computeObservationType(parameterStableId, simpleValue);
            Long            parameterPk     = cdaParameter_idMap.get(parameterStableId);
            String          sequenceId      = null;
            int             populationId    = 0;

            switch (observationType) {
                case time_series:
                    observationPk = insertTimeSeries(dccExperiment, simpleValue, incrementValue, dccMetadataList,
                            dbId, biologicalSamplePk, parameterStableId, parameterPk,
                            sequenceId, populationId, observationType, missing,
                            parameterStatus, parameterStatusMessage,
                            seriesParameter);
                    break;
                case text_series:
                    observationPk = insertTextSeries(dccExperiment, simpleValue, incrementValue, dccMetadataList,
                            dbId, biologicalSamplePk, parameterStableId, parameterPk,
                            sequenceId, populationId, observationType, missing,
                            parameterStatus, parameterStatusMessage,
                            seriesParameter);
                    break;
                default:
                    observationPk = null;
                    break;
            }

            if (observationPk != null) {
                // Insert experiment_observation
                cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
            }
        }
    }

    private Long insertTimeSeries(
            DccExperimentDTO dccExperiment,
            String simpleValue,
            String incrementValue,
            List<ProcedureMetadata> dccMetadataList,
            long dbId,
            Long biologicalSamplePk,
            String parameterStableId,
            long parameterPk,
            String sequenceId,
            int populationId,
            ObservationType observationType,
            int valueMissing,
            String parameterStatus,
            String parameterStatusMessage,
            SeriesParameter seriesParameter) {

        Long observationPk = null;

        // dataPoint for all cases.
        Float dataPoint     = null;

        // timePoint for all cases. Default is dateOfExperiment.
        Date  timePoint     = dccExperiment.getDateOfExperiment();

        // discrete point for all cases. Default shows elapsed time / sequence
        Float discretePoint = null;

        if (valueMissing == 0) {
            if ((simpleValue != null) && ( ! simpleValue.equals("null")) && ( ! simpleValue.trim().isEmpty())) {
                try {
                    dataPoint = Float.parseFloat(simpleValue);
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

            // discretePoint if increment value represents a date.
            discretePoint = utils.convertTimepoint(incrementValue, dccExperiment, dccMetadataList);
            if (discretePoint == null) {
                valueMissing = 1;
            }

            // Parse value into correct format
            String parsedIncrementValue = utils.getParsedIncrementValue(incrementValue);
            if (parsedIncrementValue.contains("-")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                try {
                    // timePoint (overridden if increment value represents a date.
                    timePoint = simpleDateFormat.parse(parsedIncrementValue);
                    SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy-MM-dd");

                    Date maxDate = new Date();
                    Date minDate = ymdFormat.parse("1975-01-01");
                    String message = "Invalid timepoint date '" + ymdFormat.format(timePoint) + "'" +
                            " for datasource " + dccExperiment.getDatasourceShortName() +
                            ", center " + dccExperiment.getPhenotypingCenter() +
                            ", experimentId '" + dccExperiment.getExperimentId() + "'";


                    if ( ! commonUtils.isDateValid(timePoint, minDate, maxDate)) {
                        valueMissing = 1;
                        badDates.add(message);
                    }

                } catch (ParseException e) { }
            }

        } else {

            // Not time series (increment is not a timestamp) - e.g. IMPC_GRS_004_001

            try {
                // discretePoint if increment value does not represent a date.
                discretePoint = Float.parseFloat(incrementValue);
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
            logger.warn("Insert of time series parameter observation for phenotyping center {} failed. Skipping... " +
                            " biologicalSamplePk {}. parameterStableId {}." +
                            " parameterPk {}. observationType {}. missing {}. parameterStatus {}. parameterStatusMessage {}." +
                            " dataPoint {}. timePoint {}. discretePoint {}. Reason: {}",
                    dccExperiment.getPhenotypingCenter(), biologicalSamplePk, parameterStableId, parameterPk,
                    observationType, valueMissing, parameterStatus, parameterStatusMessage, dataPoint, timePoint,
                    discretePoint, e.getLocalizedMessage());
        }

        return observationPk;
    }

    // text series parameter
    private Long insertTextSeries(
            DccExperimentDTO dccExperiment,
            String simpleValue,
            String incrementValue,
            List<ProcedureMetadata> dccMetadataList,
            long dbId,
            Long biologicalSamplePk,
            String parameterStableId,
            long parameterPk,
            String sequenceId,
            int populationId,
            ObservationType observationType,
            int valueMissing,
            String parameterStatus,
            String parameterStatusMessage,
            SeriesParameter seriesParameter) {

        Long observationPk = null;

        try {
            observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                    sequenceId, populationId, observationType, valueMissing,
                    parameterStatus, parameterStatusMessage,
                    seriesParameter, simpleValue, incrementValue);
        } catch (Exception e) {
            logger.warn("Insert of text series parameter observation for phenotyping center {} failed. Skipping... " +
                            " biologicalSamplePk {}. parameterStableId {}." +
                            " parameterPk {}. observationType {}. missing {}. parameterStatus {}. parameterStatusMessage {}." +
                            " textValue {}. increment {}. Reason: {}",
                    dccExperiment.getPhenotypingCenter(), biologicalSamplePk, parameterStableId, parameterPk,
                    observationType, valueMissing, parameterStatus, parameterStatusMessage, simpleValue, incrementValue,
                    e.getLocalizedMessage());
        }

        return observationPk;
    }


    private void insertOntologyParameters(DccExperimentDTO dccExperiment, OntologyParameter ontologyParameter,
                                          long experimentPk, long dbId, Long biologicalSamplePk, int missing) throws DataLoadException
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
        long parameterPk = cdaParameter_idMap.get(parameterStableId);

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

        long observationPk;
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


    public static class UniqueExperimentId implements Comparable<UniqueExperimentId> {
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

        @Override
        public int compareTo(UniqueExperimentId o) {
            int retVal = dccCenterName.compareTo(o.dccCenterName);
            if (retVal == 0) {
                retVal = dccExperimentId.compareTo(o.dccExperimentId);
            }

            return retVal;
        }
    }

    public static Boolean getSHUFFLE() {
        return SHUFFLE;
    }

    public static void setSHUFFLE(Boolean SHUFFLE) {
        ExperimentLoader.SHUFFLE = SHUFFLE;
    }
}