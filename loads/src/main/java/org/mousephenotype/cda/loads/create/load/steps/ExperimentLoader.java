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
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccExperimentDTO;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.common.SeriesParameterObservationUtils;
import org.mousephenotype.cda.loads.create.load.support.EuroPhenomeStrainMapper;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads the experiments from a database with a dcc schema into the cda database.
 *
 * Created by mrelac on 12/10/2016.
 *
 */
public class ExperimentLoader implements Step, Tasklet, InitializingBean {

    private CommonUtils                commonUtils = new CommonUtils();
    private CdaSqlUtils                cdaSqlUtils;
    private DccSqlUtils                dccSqlUtils;
    private EuroPhenomeStrainMapper    euroPhenomeStrainMapper;
    private NamedParameterJdbcTemplate jdbcCda;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private StepBuilderFactory            stepBuilderFactory;

    private Set<String> missingColonyIds         = new HashSet<>();
    private Set<String> noGeneForAllele          = new HashSet<>();
    private Set<String> unsupportedParametersMap = new HashSet<>();

    private int lineLevelProcedureCount   = 0;
    private int sampleLevelProcedureCount = 0;


    public ExperimentLoader(NamedParameterJdbcTemplate jdbcCda, StepBuilderFactory stepBuilderFactory,
                            CdaSqlUtils cdaSqlUtils, DccSqlUtils dccSqlUtils) {
            this.jdbcCda = jdbcCda;
            this.stepBuilderFactory = stepBuilderFactory;
            this.cdaSqlUtils = cdaSqlUtils;
            this.dccSqlUtils = dccSqlUtils;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(jdbcCda, "jdbcCda must be set");
        Assert.notNull(stepBuilderFactory, "stepBuilderFactory must be set");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must be set");
        Assert.notNull(dccSqlUtils, "dccSqlUtils must be set");
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
    private Map<String, Integer>                  cdaDb_idMap;
    private Map<String, Integer>                  cdaOrganisation_idMap;
    private Map<String, Integer>                  cdaProject_idMap;
    private Map<String, Integer>                  cdaPipeline_idMap;
    private Map<String, Integer>                  cdaProcedure_idMap;
    private Map<String, Integer>                  cdaParameter_idMap;
    private Set<String>                           requiredImpressParameters;
    private Map<String, BiologicalSample>         samplesMap;                       // keyed by external_id
    private Map<String, PhenotypedColony>         phenotypedColonyMap;

    // DCC parameter lookup maps, keyed by procedure_pk
    private Map<Long, List<ProcedureMetadata>>    procedureMetadataMap;
    private Map<Long, List<SimpleParameter>>      simpleParameterMap;
    private Map<Long, List<MediaParameter>>       mediaParameterMap;
    private Map<Long, List<OntologyParameter>>    ontologyParameterMap;
    private Map<Long, List<SeriesParameter>>      seriesParameterMap;
    private Map<Long, List<SeriesMediaParameter>> seriesMediaParameterMap;
    private Map<Long, List<MediaSampleParameter>> mediaSampleParameterMap;


    // lookup maps returning specified parameter type list given cda procedure primary key
    private ConcurrentHashMap<String, Allele> allelesBySymbolMap;


    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        logger.info("Loading dcc experiments from {}", dccSqlUtils.getDbName());

        long startStep = new Date().getTime();
        Experiment experiment;
        Observation observation;
        List<DccExperimentDTO> dccExperiments = dccSqlUtils.getExperiments();
        Map<String, Integer>   counts;

        // Initialise maps.
        logger.info("Loading lookup maps started");
        euroPhenomeStrainMapper = new EuroPhenomeStrainMapper(cdaSqlUtils);
        allelesBySymbolMap = new ConcurrentHashMap<>(cdaSqlUtils.getAllelesBySymbol());

        List<String> dccCenterIds = dccSqlUtils.getCenterIds();
        cdaDb_idMap = cdaSqlUtils.getCdaDb_idsByDccDatasourceShortName();
        cdaOrganisation_idMap = cdaSqlUtils.getCdaOrganisation_idsByDccCenterId(dccCenterIds);
        cdaProject_idMap = cdaSqlUtils.getCdaProject_idsByDccProject();
        cdaPipeline_idMap = cdaSqlUtils.getCdaPipeline_idsByDccPipeline();
        cdaProcedure_idMap = cdaSqlUtils.getCdaProcedure_idsByDccProcedureId();
        cdaParameter_idMap = cdaSqlUtils.getCdaParameter_idsByDccParameterId();
        phenotypedColonyMap = cdaSqlUtils.getPhenotypedColonies();
        requiredImpressParameters = cdaSqlUtils.getRequiredImpressParameters();
        samplesMap = cdaSqlUtils.getBiologicalSamples();

        // Load DCC parameter maps
        procedureMetadataMap = dccSqlUtils.getProcedureMetadata();
        simpleParameterMap = dccSqlUtils.getSimpleParameters();
        mediaParameterMap = dccSqlUtils.getMediaParameters();
        ontologyParameterMap = dccSqlUtils.getOntologyParameters();
        seriesParameterMap = dccSqlUtils.getSeriesParameters();
        seriesMediaParameterMap = dccSqlUtils.getSeriesMediaParameters();
        mediaSampleParameterMap = dccSqlUtils.getMediaSampleParameters();
        logger.info("Loading lookup maps finished");


        int experimentCount = 0;
        for (DccExperimentDTO dccExperiment : dccExperiments) {
            insertExperiment(dccExperiment);
            experimentCount++;
            if (experimentCount % 100000 == 0) {
                logger.info("Processed {} experiments", experimentCount);
            }
        }

        Iterator<String> missingColonyIdsIt = missingColonyIds.iterator();
        while (missingColonyIdsIt.hasNext()) {
            String colonyId = missingColonyIdsIt.next();
            logger.error("Missing phenotyped_colony information for dcc-supplied colony '" + colonyId + "'. Skipping...");
        }

        Iterator<String> missingCentersIt = missingCenters.iterator();
        while (missingCentersIt.hasNext()) {
            String centerId = missingCentersIt.next();
            logger.error("Missing center '" + centerId + "'. Skipping...");
        }

        Iterator<String> missingPipelinesIt = missingPipelines.iterator();
        while (missingPipelinesIt.hasNext()) {
            String pipelineId = missingPipelinesIt.next();
            logger.error("Missing pipeline '" + pipelineId + "'. Skipping...");
        }

        Iterator<String> missingProceduresIt = missingProcedures.iterator();
        while (missingProceduresIt.hasNext()) {
            String procedureId = missingProceduresIt.next();
            logger.error("Missing procedure '" + procedureId + "'. Skipping...");
        }

        Iterator<String> missingProjectsIt = missingProjects.iterator();
        while (missingProjectsIt.hasNext()) {
            String projectId = missingProjectsIt.next();
            logger.error("Missing project '" + projectId + "'. Skipping...");
        }

        Iterator<String> missingSamplesIt = missingSamples.iterator();
        while (missingSamplesIt.hasNext()) {
            String parameterStableId = missingSamplesIt.next();
            logger.error("Missing samples for parameter stable id '" + parameterStableId + "'. Skipping...");
        }

        logger.info("Wrote {} sample-Level procedures", sampleLevelProcedureCount);
        logger.info("Wrote {} line-Level procedures", lineLevelProcedureCount);

        logger.debug("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));
        contribution.setExitStatus(ExitStatus.COMPLETED);
        chunkContext.setComplete();

        return RepeatStatus.FINISHED;
    }

//    @Transactional
    public Experiment insertExperiment(DccExperimentDTO dccExperiment) throws DataLoadException {



//        if (dccExperiment.getExperimentId().equals("8852_1943")) {
//            int mm = 17;
//            System.out.println();
//        } else {
//            return new Experiment();
//        }
        Experiment experiment = createExperiment(dccExperiment);

        return experiment;
    }

    private Set<String> missingCenters    = new HashSet<>();
    private Set<String> missingProjects   = new HashSet<>();
    private Set<String> missingPipelines  = new HashSet<>();
    private Set<String> missingProcedures = new HashSet<>();
    private Set<String> missingSamples    = new HashSet<>();        // value = parameterStableId

    private Experiment createExperiment(DccExperimentDTO dccExperiment) throws DataLoadException {
        Experiment experiment = new Experiment();
        int dbId;
        Integer organisationPk;
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
        String metadataCombined;
        String metadataGroup;

        dbId = cdaDb_idMap.get(dccExperiment.getDatasourceShortName());
        organisationPk = cdaOrganisation_idMap.get(dccExperiment.getPhenotypingCenter());
        if (organisationPk == null) {
            missingCenters.add(dccExperiment.getPhenotypingCenter());
            return null;
        }
        projectPk = cdaProject_idMap.get(dccExperiment.getProject());
        if (projectPk == null) {
            missingProjects.add(dccExperiment.getProject());
            return null;
        }
        pipelinePk = cdaPipeline_idMap.get(dccExperiment.getPipeline());
        if (pipelinePk == null) {
            missingPipelines.add(dccExperiment.getPipeline());
            return null;
        }
        pipelineStableId = dccExperiment.getPipeline();
        procedurePk = cdaProcedure_idMap.get(dccExperiment.getProcedureId());
        if (procedurePk == null) {
            missingProcedures.add(dccExperiment.getProcedureId());
            return null;
        }
        procedureStableId = dccExperiment.getProcedureId();
        externalId = dccExperiment.getExperimentId();
        String[] rawProcedureStatus = commonUtils.parseImpressStatus(dccExperiment.getRawProcedureStatus());
        procedureStatus = rawProcedureStatus[0];
        procedureStatusMessage = rawProcedureStatus[1];

        // Within the scope of the cda experiment, line-level procedures have:
        //   - no dcc experiment info (e.g. no date_of_experimentor or sequence_id). The external_id is computed from
        //     a concatenation of Dcc procedureId and colonyId.
        //   - non-null colony_id
        //   - non-null biological_model_id
        //   Create an external_id from the dcc procedureId and colonyId.
        // sample-level procedures have:
        //  - date_of_experiment (skip if it is null), external_id, sequence_id (though it may be null)
        //  - null colony_id
        //  - null biological_model_id
        if (dccExperiment.isLineLevel()) {

            PhenotypedColony phenotypedColony = phenotypedColonyMap.get(dccExperiment.getColonyId());
            if ((phenotypedColony == null) || (phenotypedColony.getColonyName() == null)) {
                logger.error("Experiment {} has null/invalid colonyId '{}'. Skipping ...", dccExperiment.getExperimentId(), dccExperiment.getColonyId());
                return null;
            }
            colonyId = phenotypedColony.getColonyName();
            dateOfExperiment = null;
            sequenceId = null;
            List<SimpleParameter> simpleParameters = simpleParameterMap.get(dccExperiment.getDcc_procedure_pk());
            biologicalModelPk = getBiologicalModelId(phenotypedColony, simpleParameters);

        } else {
            colonyId = null;
            dateOfExperiment = getDateOfExperiment(dccExperiment);
            if (dateOfExperiment == null) {
                return null;
            }
            sequenceId = dccExperiment.getSequenceId();
            biologicalModelPk = null;
        }

       /** Save procedure metadata into metadataCombined and metadataGroup:
        *
        * metadataCombined - All of a procedure's metadata parameters, in token = value format. Each metadata parameter
        * is separated by a pair of colons. Each metadata lvalue is separated from its rvalue by " = ";
        * for example: "Parm1 = 123::Parm2 = 567"
        *
        * metadataGroup - An md5 hash of only the required parameters. The hash source is the required metadata
        * parameters in the same format as <i>metadataCombined</i> above.</ul>
        */
        List<ProcedureMetadata> dccMetadataList = procedureMetadataMap.get(dccExperiment.getDcc_procedure_pk());
        if (dccMetadataList == null)
            dccMetadataList = new ArrayList<>();
        ObservableList<String> metadataCombinedList = FXCollections.observableArrayList();
        ObservableList<String> metadataGroupList = FXCollections.observableArrayList();
        for (ProcedureMetadata metadata : dccMetadataList) {
            metadataCombinedList.add(metadata.getParameterID() + " = " + metadata.getValue());
            if (requiredImpressParameters.contains(metadata.getParameterID())) {
                metadataGroupList.add(metadata.getParameterID() + " = " + metadata.getValue());
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
                organisationPk,
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

        createObservations(dccExperiment, dbId, experimentPk);

        cdaSqlUtils.insertProcedureMetadata(dccMetadataList, dccExperiment.getProcedureId(), experimentPk, 0);

        return experiment;
    }


    // PRIVATE METHODS

    private void createObservations( DccExperimentDTO dccExperimentDTO, int dbId, int experimentPk) throws DataLoadException {

        Integer biologicalSamplePk;

        // For all parameter types:
        if (dccExperimentDTO.isLineLevel()) {
            biologicalSamplePk = null;
        } else {
            BiologicalSample bs = samplesMap.get(dccExperimentDTO.getSpecimenId());
            if (bs == null) {
                if ( ! DccSqlUtils.knownBadColonyIds.contains(dccExperimentDTO.getColonyId())) {
                    missingColonyIds.add(dccExperimentDTO.getColonyId());
                }

                return;
            }
            biologicalSamplePk = bs.getId();
        }


        // simpleParameters
        List<SimpleParameter> simpleParameterList = simpleParameterMap.get(dccExperimentDTO.getDcc_procedure_pk());
        for (SimpleParameter simpleParameter : simpleParameterList) {
            insertSimpleParameter(dccExperimentDTO, simpleParameter, experimentPk, dbId, biologicalSamplePk);
        }


        // mediaParameters
        List<MediaParameter> mediaParameterList = mediaParameterMap.get(dccExperimentDTO.getDcc_procedure_pk());
        if (mediaParameterList == null)
            mediaParameterList = new ArrayList<>();
        if ((dccExperimentDTO.isLineLevel()) && ( ! mediaParameterList.isEmpty())) {
            String errMsg = String.format("We don't currently support processing of line level MediaParameters: %s. Skipping ...", dccExperimentDTO.getProcedureId());
            logger.warn(errMsg);
            return;
        }
        for (MediaParameter mediaParameter : mediaParameterList) {
            insertMediaParameter(dccExperimentDTO, mediaParameter, experimentPk, dbId, biologicalSamplePk);
        }


        // ontologyParameters
        List<OntologyParameter> ontologyParameterList = ontologyParameterMap.get(dccExperimentDTO.getDcc_procedure_pk());
        if (ontologyParameterList == null)
            ontologyParameterList = new ArrayList<>();
        if ((dccExperimentDTO.isLineLevel()) && ( ! ontologyParameterList.isEmpty())) {
            String errMsg = String.format("We don't currently support processing of line level OntologyParameters: %s. Skipping ...", dccExperimentDTO.getProcedureId());
            logger.warn(errMsg);
            return;
        }
        for (OntologyParameter ontologyParameter : ontologyParameterList) {
            insertOntologyParameters(dccExperimentDTO, ontologyParameter, experimentPk, dbId, biologicalSamplePk);
        }


        // seriesParameters
        List<SeriesParameter> seriesParameterList = seriesParameterMap.get(dccExperimentDTO.getDcc_procedure_pk());
        if (seriesParameterList == null)
            seriesParameterList = new ArrayList<>();
        if ((dccExperimentDTO.isLineLevel()) && ( ! seriesParameterList.isEmpty())) {
            String errMsg = String.format("We don't currently support processing of line level SeriesParameters: %s. Skipping ...", dccExperimentDTO.getProcedureId());
            logger.warn(errMsg);
            return;
        }
        for (SeriesParameter seriesParameter : seriesParameterList) {
            insertSeriesParameter(dccExperimentDTO, seriesParameter, experimentPk, dbId, biologicalSamplePk);
        }


        // seriesMediaParameters
        List<SeriesMediaParameter> seriesMediaParameterList = seriesMediaParameterMap.get(dccExperimentDTO.getDcc_procedure_pk());
        if (seriesMediaParameterList == null)
            seriesMediaParameterList = new ArrayList<>();
        if ((dccExperimentDTO.isLineLevel()) && ( ! seriesMediaParameterList.isEmpty())) {
            String errMsg = String.format("We don't currently support processing of line level SeriesMediaParameters: %s. Skipping ...", dccExperimentDTO.getProcedureId());
            logger.warn(errMsg);
            return;
        }
        for (SeriesMediaParameter seriesMediaParameter : seriesMediaParameterList) {
            insertSeriesMediaParameter(dccExperimentDTO, seriesMediaParameter, experimentPk, dbId, biologicalSamplePk,
                                       simpleParameterList, ontologyParameterList);
        }


        // mediaSampleParameters
        List<MediaSampleParameter> mediaSampleParameterList = mediaSampleParameterMap.get(dccExperimentDTO.getDcc_procedure_pk());
        if (mediaSampleParameterList == null)
            mediaSampleParameterList = new ArrayList<>();
        if ((dccExperimentDTO.isLineLevel()) && ( ! mediaSampleParameterList.isEmpty())) {
            String errMsg = String.format("We don't currently support processing of line level MediaSampleParameters: %s. Skipping ...", dccExperimentDTO.getProcedureId());
            logger.warn(errMsg);
            return;
        }
        for (MediaSampleParameter mediaSampleParameter : mediaSampleParameterList) {

            insertMediaSampleParameter(dccExperimentDTO, mediaSampleParameter, experimentPk, dbId, biologicalSamplePk,
                                       simpleParameterList, ontologyParameterList);
        }
    }

    /**
     * Validates and returns date of experiment, if valid; null otherwise. Logs null/invalid dates.
     * @param dccExperiment
     * @return the date of experiment, if valid; null otherwise.
     */
    private Date getDateOfExperiment(DccExperimentDTO dccExperiment) {
        Date dateOfExperiment = null;
        SimpleDateFormat dateFormat  = new SimpleDateFormat("yyyy-MM-dd");

        String experimentId = dccExperiment.getExperimentId();

        try {
            Date dccDate = dccExperiment.getDateOfExperiment();
            Date maxDate = new Date();
            Date minDate = dateFormat.parse("1975-01-01");

            if (dccDate.before(minDate)) {

                logger.warn("Experiment {} has date before 01-January-1975. Skipping ...", experimentId, dccDate);
            } else if (dccDate.after(maxDate)) {

                logger.warn("Experiment {} has date after today's date. Skipping ...", experimentId, dccDate);
            }

            dateOfExperiment = dccDate;

        } catch (Exception e) {

            logger.warn("Experiment {} has invalid date. Skipping ...", experimentId, dccExperiment.getDateOfExperiment());
        }

        return dateOfExperiment;
    }

    private int getBiologicalModelId(
            PhenotypedColony phenotypedColony,
            List<SimpleParameter> simpleParameters) throws DataLoadException {
        int biological_model_id = 0;

        String zygosity = getZygosity(simpleParameters);
        String sampleGroup = "experimental";
        biological_model_id = cdaSqlUtils.selectOrInsertBiologicalModel(phenotypedColony, euroPhenomeStrainMapper, zygosity, sampleGroup, allelesBySymbolMap).getId();

        return biological_model_id;
    }

    private String getZygosity(List<SimpleParameter> simpleParameters) {

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

    private void insertSimpleParameter(DccExperimentDTO dccExperimentDTO, SimpleParameter simpleParameter, int experimentPk,
                                       int dbId, int biologicalSamplePk) throws DataLoadException {
        String parameterStableId = simpleParameter.getParameterID();
        int parameterPk = cdaParameter_idMap.get(parameterStableId);
        String sequenceId = (simpleParameter.getSequenceID() == null ? null : simpleParameter.getSequenceID().toString());
        ObservationType observationType = cdaSqlUtils.computeObservationType(parameterStableId, simpleParameter.getValue());
        String[] rawProcedureStatus = commonUtils.parseImpressStatus(dccExperimentDTO.getRawProcedureStatus());
        String procedureStatus = rawProcedureStatus[0];
        String[] rawParameterStatus = commonUtils.parseImpressStatus(simpleParameter.getParameterStatus());
        String parameterStatus = rawParameterStatus[0];
        String parameterStatusMessage = rawParameterStatus[1];
        int missing = ((procedureStatus != null) || parameterStatus != null ? 1 : 0);
        int populationId = 0;


        // Special rules. May cause observation to be skipped.
        // Skip loading EuroPhenome - ICS - vagina presence - "present" male data
        // Per Mohammed SELLOUM <selloum@igbmc.fr> 5 June 2015 12:57:28 BST
        if (dccExperimentDTO.getDatasourceShortName().equals("EuroPhenome") &&
            dccExperimentDTO.getPhenotypingCenter().equalsIgnoreCase("ICS") &&
            parameterStableId.equals("ESLIM_001_001_125") &&
            dccExperimentDTO.getSpecimenId() != null &&
            dccExperimentDTO.getSex().equals(SexType.male) &&
            simpleParameter.getValue().equals("present")
            ) {

            logger.info("Special rule: skipping specimen {}, experiment {}, parameter {}, sex {} ",
                        dccExperimentDTO.getSpecimenId(), dccExperimentDTO.getExperimentId(),
                        parameterStableId, dccExperimentDTO.getSex());
            return;
        }

        // Check for null/empty values. Values are not required - sometimes there is a parameterStatus instead.
        String value = simpleParameter.getValue();
        if ((value == null) || value.trim().isEmpty()) {
            if ((simpleParameter.getParameterStatus() == null) || (simpleParameter.getParameterStatus().trim().isEmpty())) {
                logger.warn("Null/empty value and status found for simple parameter {}, dcc experiment {}. Skipping parameter ...",
                            simpleParameter.getParameterID(), dccExperimentDTO);
            }
            return;
        }

        int observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                                                          sequenceId, populationId, observationType, missing,
                                                          parameterStatus, parameterStatusMessage,
                                                          simpleParameter);

        // Insert experiment_observation
        cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
    }

    private void insertMediaParameter(DccExperimentDTO dccExperimentDTO, MediaParameter mediaParameter,
                                      int experimentPk, int dbId, int biologicalSamplePk) throws DataLoadException
    {
        if (dccExperimentDTO.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperimentDTO.getExperimentId() + " contains MediaParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String parameterStableId = mediaParameter.getParameterID();
        int parameterPk = cdaParameter_idMap.get(parameterStableId);
        String sequenceId = null;
        ObservationType observationType = ObservationType.image_record;
        String[] rawProcedureStatus = commonUtils.parseImpressStatus(dccExperimentDTO.getRawProcedureStatus());
        String procedureStatus = rawProcedureStatus[0];
        String[] rawParameterStatus = commonUtils.parseImpressStatus(mediaParameter.getParameterStatus());
        String parameterStatus = rawParameterStatus[0];
        String parameterStatusMessage = rawParameterStatus[1];
        String URI = mediaParameter.getURI();
        int missing = ((procedureStatus != null) || parameterStatus != null ||
                (URI == null || URI.isEmpty() || URI.endsWith("/")) ? 1 : 0);
        int populationId = 0;
        BiologicalSample sample = samplesMap.get(parameterPk);
        if (sample == null) {
            missingSamples.add(parameterStableId);
            return;
        }

        int samplePk = sample.getId();

        int organisationPk = cdaOrganisation_idMap.get(parameterPk);

        int observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                                                          sequenceId, populationId, observationType, missing,
                                                          parameterStatus, parameterStatusMessage,
                                                          mediaParameter, dccExperimentDTO, samplePk, organisationPk);

        // Insert experiment_observation
        cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
    }

    public void insertMediaSampleParameter(DccExperimentDTO dccExperimentDTO, MediaSampleParameter mediaSampleParameter,
                                           int experimentPk, int dbId, int biologicalSamplePk,
                                           List<SimpleParameter> simpleParameterList,
                                           List<OntologyParameter> ontologyParameterList) throws DataLoadException
    {
        if (dccExperimentDTO.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperimentDTO.getExperimentId() + " contains MediaSampleParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String          parameterStableId      = mediaSampleParameter.getParameterID();
        int             parameterPk            = cdaParameter_idMap.get(parameterStableId);
        int             populationId           = 0;
        String          sequenceId             = null;
        ObservationType observationType        = ObservationType.image_record;
        String[]        rawProcedureStatus     = commonUtils.parseImpressStatus(dccExperimentDTO.getRawProcedureStatus());
        String          procedureStatus        = rawProcedureStatus[0];
        String[]        rawParameterStatus     = commonUtils.parseImpressStatus(mediaSampleParameter.getParameterStatus());
        String          parameterStatus        = rawParameterStatus[0];
        String          parameterStatusMessage = rawParameterStatus[1];
        int             missing                = ((procedureStatus != null) || parameterStatus != null ? 1 : 0);
        int             samplePk               = samplesMap.get(parameterPk).getId();
        int             organisationPk         = cdaOrganisation_idMap.get(parameterPk);

        String info              = mediaSampleParameter.getParameterID() + mediaSampleParameter.getParameterStatus();
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
            String mediaSampleLocalId = mediaSample.getLocalId();
            for (MediaSection mediaSection : mediaSample.getMediaSection()) {

                for (MediaFile mediaFile : mediaSection.getMediaFile()) {
                    String URI = mediaFile.getURI();
                    missing = (missing == 1 || (URI == null || URI.isEmpty() || URI.endsWith("/")) ? 1 : 0);

                    observationPk = cdaSqlUtils.insertObservation(
                            dbId, biologicalSamplePk, parameterStableId, parameterPk, sequenceId, populationId,
                            observationType, missing, parameterStatus, parameterStatusMessage, mediaSampleParameter,
                            mediaFile, dccExperimentDTO, samplePk, organisationPk, experimentPk,
                            simpleParameterList, ontologyParameterList);
                }
            }
        }

        // Insert experiment_observation
        cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
    }

    private void insertSeriesMediaParameter(DccExperimentDTO dccExperimentDTO, SeriesMediaParameter seriesMediaParameter,
                                            int experimentPk, int dbId, int biologicalSamplePk,
                                            List<SimpleParameter> simpleParameterList,
                                            List<OntologyParameter> ontologyParameterList) throws DataLoadException
    {
        if (dccExperimentDTO.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperimentDTO.getExperimentId() + " contains SeriesMediaParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String parameterStableId = seriesMediaParameter.getParameterID();
        int parameterPk = cdaParameter_idMap.get(parameterStableId);
        String sequenceId = null;
        ObservationType observationType = ObservationType.image_record;
        String[] rawProcedureStatus = commonUtils.parseImpressStatus(dccExperimentDTO.getRawProcedureStatus());
        String procedureStatus = rawProcedureStatus[0];
        String[] rawParameterStatus = commonUtils.parseImpressStatus(seriesMediaParameter.getParameterStatus());
        String parameterStatus = null;
        String parameterStatusMessage = null;
        try {
            parameterStatus        = rawParameterStatus[0];
            parameterStatusMessage = rawParameterStatus[1];
        } catch (Exception e) {
            logger.error("Error extracting parameter. rawParameterStatus = {}. Ingored.", rawParameterStatus);
        }
        int missing = ((procedureStatus != null) || parameterStatus != null ? 1 : 0);
        int populationId = 0;
        BiologicalSample sample = samplesMap.get(parameterPk);
        if (sample == null) {
            missingSamples.add(parameterStableId);
            return;
        }

        int samplePk = sample.getId();
        int organisationPk = cdaOrganisation_idMap.get(parameterPk);

        for (SeriesMediaParameterValue value : seriesMediaParameter.getValue()) {

            String URI = value.getURI();
            missing = (observationType == ObservationType.image_record && (URI == null || URI.isEmpty() || URI.endsWith("/")) ? 1 : missing);

            int observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                                                              sequenceId, populationId, observationType, missing,
                                                              parameterStatus, parameterStatusMessage,
                                                              value, dccExperimentDTO, samplePk, organisationPk,
                                                              experimentPk, simpleParameterList, ontologyParameterList);

            // Insert experiment_observation
            cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
        }
    }


    private void insertSeriesParameter(DccExperimentDTO dccExperimentDTO, SeriesParameter seriesParameter, int experimentPk,
                                       int dbId, int biologicalSamplePk) throws DataLoadException {

        if (dccExperimentDTO.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperimentDTO.getExperimentId() + " contains SeriesParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        List<ProcedureMetadata> dccMetadataList = procedureMetadataMap.get(dccExperimentDTO.getDcc_procedure_pk());
        String parameterStableId = seriesParameter.getParameterID();

        for (SeriesParameterValue seriesParameterValue : seriesParameter.getValue()) {

            // Get the parameter data type.
            String          incrementValue         = seriesParameterValue.getIncrementValue();
            String          simpleValue            = seriesParameterValue.getValue();
            int             observationPk          = 0;
            ObservationType observationType        = cdaSqlUtils.computeObservationType(parameterStableId, simpleValue);
            String[]        rawProcedureStatus     = commonUtils.parseImpressStatus(dccExperimentDTO.getRawProcedureStatus());
            String          procedureStatus        = rawProcedureStatus[0];
            String[]        rawParameterStatus     = commonUtils.parseImpressStatus(seriesParameter.getParameterStatus());
            String          parameterStatus        = rawParameterStatus[0];
            String          parameterStatusMessage = rawParameterStatus[1];
            int             missing                = ((procedureStatus != null) || parameterStatus != null ? 1 : 0);
            int             parameterPk            = cdaParameter_idMap.get(parameterStableId);
            String          sequenceId             = null;
            int             populationId           = 0;

            // time_series_observation variables
            Float dataPoint     = null;
            Date  timePoint     = dccExperimentDTO.getDateOfExperiment();                                               // timePoint for all cases. Default is dateOfExperiment.
            Float discretePoint = null;

            if ((simpleValue != null) && ( ! simpleValue.equals("null")) && ( ! simpleValue.equals(""))) {
                try {
                    dataPoint = Float.parseFloat(simpleValue);                                                          // dataPoint for all cases.
                } catch (NumberFormatException e) {
                    missing = 1;
                }
            }

            // Test increment value to see if it represents a date.
            if (incrementValue.contains("-") && (incrementValue.contains(" ") || incrementValue.contains("T"))) {

                // Time series (increment is a datetime or time) - e.g. IMPC_CAL_003_001
                SeriesParameterObservationUtils utils = new SeriesParameterObservationUtils();

                discretePoint = utils.convertTimepoint(incrementValue, dccExperimentDTO, dccMetadataList);              // discretePoint if increment value represents a date.

                // Parse value into correct format
                String parsedIncrementValue = utils.getParsedIncrementValue(incrementValue);
                if (parsedIncrementValue.contains("-")) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    try {
                        timePoint = simpleDateFormat.parse(parsedIncrementValue);                                       // timePoint (overridden if increment value represents a date.

                    } catch (ParseException e) { }
                }

            } else {

                // Not time series (increment is not a timestamp) - e.g. IMPC_GRS_004_001

                try {
                    discretePoint = Float.parseFloat(incrementValue);                                                   // discretePoint if increment value does not represent a date.
                } catch (NumberFormatException e) {
                    missing = 1;
                }
            }

            observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                                                          sequenceId, populationId, observationType, missing,
                                                          parameterStatus, parameterStatusMessage,
                                                          seriesParameter, dataPoint, timePoint, discretePoint);

            // Insert experiment_observation
            cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
        }
    }

    private void insertOntologyParameters(DccExperimentDTO dccExperimentDTO, OntologyParameter ontologyParameter,
                                          int experimentPk, int dbId, int biologicalSamplePk) throws DataLoadException
    {
        if (dccExperimentDTO.isLineLevel()) {
            unsupportedParametersMap.add("Line-level procedure " + dccExperimentDTO.getExperimentId() + " contains OntologyParameters, which is currently unsupported. Skipping parameters.");
            return;
        }

        String parameterStableId = ontologyParameter.getParameterID();
        int parameterPk = cdaParameter_idMap.get(parameterStableId);
        String sequenceId = null;
        ObservationType observationType = ObservationType.image_record;
        String[] rawProcedureStatus = commonUtils.parseImpressStatus(dccExperimentDTO.getRawProcedureStatus());
        String procedureStatus = rawProcedureStatus[0];
        String[] rawParameterStatus = commonUtils.parseImpressStatus(ontologyParameter.getParameterStatus());
        String parameterStatus = rawParameterStatus[0];
        String parameterStatusMessage = rawParameterStatus[1];
        int missing = ((procedureStatus != null) || parameterStatus != null ? 1 : 0);
        int populationId = 0;

        int observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSamplePk, parameterStableId, parameterPk,
                                                          sequenceId, populationId, observationType, missing,
                                                          parameterStatus, parameterStatusMessage,
                                                          ontologyParameter);

        // Insert experiment_observation
        cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);
    }
}