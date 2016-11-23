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

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.db.pojo.Experiment;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccExperimentDTO;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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

    private final Logger         logger      = LoggerFactory.getLogger(this.getClass());
    private StepBuilderFactory   stepBuilderFactory;
    private Map<String, Integer> written     = new HashMap<>();


    public ExperimentLoader(NamedParameterJdbcTemplate jdbcCda, StepBuilderFactory stepBuilderFactory,
                            CdaSqlUtils cdaSqlUtils, DccSqlUtils dccSqlUtils) {
            this.jdbcCda = jdbcCda;
            this.stepBuilderFactory = stepBuilderFactory;
            this.cdaSqlUtils = cdaSqlUtils;
            this.dccSqlUtils = dccSqlUtils;


        // FIXME FIXME FIXME
        written.put("biologicalModel", 0);
        written.put("biologicalSample", 0);
        written.put("liveSample", 0);
        written.put("controlSample", 0);
        written.put("experimentalSample", 0);
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
    private Map<String, Integer> cdaDb_idMap;
    private Map<String, Integer> cdaOrganisation_idMap;
    private Map<String, Integer> cdaProject_idMap;
    private Map<String, Integer> cdaPipeline_idMap;
    private Map<String, Integer> cdaProcedure_idMap;
    private Map<String, Integer> cdaParameter_idMap;
    private Map<String, BiologicalSample> samplesMap;   // keyed by external_id

    // lookup maps returning specified parameter type list given cda procedure primary key
    private ConcurrentHashMap<String, Allele> allelesBySymbolMap;



    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        long startStep = new Date().getTime();
        Experiment experiment;
        Observation observation;
        List<DccExperimentDTO> dccExperiments = dccSqlUtils.getExperiments();
        Map<String, Integer>   counts;

        // Initialise maps.
        euroPhenomeStrainMapper = new EuroPhenomeStrainMapper(cdaSqlUtils);
        allelesBySymbolMap = new ConcurrentHashMap<>(cdaSqlUtils.getAllelesBySymbol());

        List<String> dccCenterIds = dccSqlUtils.getCenterIds();
        cdaDb_idMap = cdaSqlUtils.getCdaDb_idsByDccDatasourceShortName();
        cdaOrganisation_idMap = cdaSqlUtils.getCdaOrganisation_idsByDccCenterId(dccCenterIds);
        cdaProject_idMap = cdaSqlUtils.getCdaProject_idsByDccProject();
        cdaPipeline_idMap = cdaSqlUtils.getCdaPipeline_idsByDccPipeline();
        cdaProcedure_idMap = cdaSqlUtils.getCdaProcedure_idsByDccProcedureId();
        cdaParameter_idMap = cdaSqlUtils.getCdaParameter_idsByDccParameterId();
        samplesMap = cdaSqlUtils.getBiologicalSamples();

        for (DccExperimentDTO dccExperiment : dccExperiments) {

            experiment = insertExperiment(dccExperiment);
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

//        logger.info("Wrote {} new biological models", written.get("biologicalModel"));
//        logger.info("Wrote {} new biological samples", written.get("biologicalSample"));
//        logger.info("Wrote {} new live samples", written.get("liveSample"));
//        logger.info("Processed {} experimental samples", written.get("experimentalSample"));
//        logger.info("Processed {} control samples", written.get("controlSample"));
//        logger.info("Processed {} total samples", written.get("experimentalSample") + written.get("controlSample"));

        logger.debug("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));
        contribution.setExitStatus(ExitStatus.COMPLETED);
        chunkContext.setComplete();

        return RepeatStatus.FINISHED;
    }

    @Transactional
    private Experiment insertExperiment(DccExperimentDTO dccExperiment) throws DataLoadException {

        Experiment experiment = createExperiment(dccExperiment);

        return experiment;
    }

    private Set<String> missingCenters    = new HashSet<>();
    private Set<String> missingProjects   = new HashSet<>();
    private Set<String> missingPipelines  = new HashSet<>();
    private Set<String> missingProcedures = new HashSet<>();

    private Experiment createExperiment(DccExperimentDTO dccExperiment) throws DataLoadException {
        Experiment experiment = new Experiment();

        int dbId;
        Integer organisationId;
        Integer projectId;
        Integer pipelineId;
        String pipelineStableId;
        Integer procedureId;
        String procedureStableId;
        String externalId;
        String procedureStatus;
        String procedureStatusMessage;

        String colonyId;
        Date dateOfExperiment;
        String sequenceId;

        Integer biologicalModelId;
        String metadataCombined;
        String metadataGroup;

        dbId = cdaDb_idMap.get(dccExperiment.getDatasourceShortName());
        organisationId = cdaOrganisation_idMap.get(dccExperiment.getCenterId());
        if (organisationId == null) {
            missingCenters.add(dccExperiment.getCenterId());
            return null;
        }
        projectId = cdaProject_idMap.get(dccExperiment.getProject());
        if (projectId == null) {
            missingProjects.add(dccExperiment.getProject());
            return null;
        }
        pipelineId = cdaPipeline_idMap.get(dccExperiment.getPipeline());
        if (pipelineId == null) {
            missingPipelines.add(dccExperiment.getPipeline());
            return null;
        }
        pipelineStableId = dccExperiment.getPipeline();
        procedureId = cdaProcedure_idMap.get(dccExperiment.getProcedureId());
        if (procedureId == null) {
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
            PhenotypedColony phenotypedColony = cdaSqlUtils.getPhenotypedColony(dccExperiment.getColonyId());
            if ((phenotypedColony == null) || (phenotypedColony.getColonyName() == null)) {
                logger.error("Experiment {} has null/invalid colonyId '{}'. Skipping ...", dccExperiment.getExperimentId(), dccExperiment.getColonyId());
                return null;
            }
            colonyId = phenotypedColony.getColonyName();
            dateOfExperiment = null;
            sequenceId = null;
            List<SimpleParameter> simpleParameters = dccSqlUtils.getSimpleParameters(dccExperiment.getDcc_procedure_pk());
            biologicalModelId = getBiologicalModelId(colonyId, simpleParameters);

        } else {
            colonyId = null;
            dateOfExperiment = getDateOfExperiment(dccExperiment);
            if (dateOfExperiment == null) {
                return null;
            }
            sequenceId = dccExperiment.getSequenceId();
            biologicalModelId = null;
        }

metadataCombined = null;
metadataGroup = null;

        int experimentPk = cdaSqlUtils.insertExperiment(
                dbId,
                externalId,
                sequenceId,
                dateOfExperiment,
                organisationId,
                projectId,
                pipelineId,
                pipelineStableId,
                procedureId,
                procedureStableId,
                colonyId,
                procedureStatus,
                procedureStatusMessage,
                biologicalModelId,
                metadataCombined,
                metadataGroup
        );

        createObservations(dccExperiment, dbId, experimentPk);

        return experiment;
    }


    // PRIVATE METHODS


    // NO: THE ORIGINAL CODE JUST SKIPS THE SIMPLEPARAMETER, NOT THE EXPERIMENT!! FIXME
//    /**
//     * Applies special rules for skipping selected experiments
//     * @param dccExperiment
//     * @return true if experiment should be skipped; false otherwise.
//     */
//    public boolean shouldSkip(org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment dccExperiment, Experiment experiment) {
//        // Skip loading EuroPhenome - ICS - vagina presence - "present" male data
//        // Per Mohammed SELLOUM <selloum@igbmc.fr> 5 June 2015 12:57:28 BST
//        if (experiment.getDatasource().getName().equals("EuroPhenome") &&
//                experiment.getOrganisation().getName().equals("ICS") &&
//                experiment.getProcedure()..getStableId().equals("ESLIM_001_001_125") &&
//                specimen.getSex().equals(SexType.male) &&
//                simpleValue.equals("present")
//                ) {
//
//            logger.info("Manually skipping specimen {}, experiment {}, parameter {}, sex {} ", specimenId, experimentID, parameterID, specimen.getSex());
//            continue;
//        }
//    }


    private void createObservations( DccExperimentDTO dccExperimentDTO, int dbId, int experimentPk) throws DataLoadException {

        Integer         biologicalSampleId;
        int             parameterId;
        String          parameterStableId;
        String          sequenceId;
        int             populationId = 0;          // Not used. Always 0.
        ObservationType observationType;
        int             missing;
        String          parameterStatus;
        String          parameterStatusMessage;

        // For all parameter types:
        if (dccExperimentDTO.isLineLevel()) {
            biologicalSampleId = null;
        } else {
            BiologicalSample bs = samplesMap.get(dccExperimentDTO.getSpecimenId());
            if (bs == null) {
                logger.warn("Missing sample external id {} for experiment id {}. Skipping sample ...",
                            dccExperimentDTO.getSpecimenId(), dccExperimentDTO.getExperimentId());
                return;
            }
            biologicalSampleId = bs.getId();
        }


        // simpleParameters
        for (SimpleParameter simpleParameter : dccSqlUtils.getSimpleParameters(dccExperimentDTO.getDcc_procedure_pk())) {
            parameterStableId = simpleParameter.getParameterID();
            parameterId = cdaParameter_idMap.get(parameterStableId);
            sequenceId = (simpleParameter.getSequenceID() == null ? null : simpleParameter.getSequenceID().toString());
            observationType = cdaSqlUtils.computeObservationType(parameterStableId, simpleParameter.getValue());
            String[] rawParameterStatus = commonUtils.parseImpressStatus(simpleParameter.getParameterStatus());
            parameterStatus = rawParameterStatus[0];
            parameterStatusMessage = rawParameterStatus[1];
            missing = (parameterStatus != null ? 1 : 0);


            // Special rules. May cause observation to be skipped.
            // Skip loading EuroPhenome - ICS - vagina presence - "present" male data
            // Per Mohammed SELLOUM <selloum@igbmc.fr> 5 June 2015 12:57:28 BST
            if (dccExperimentDTO.getDatasourceShortName().equals("EuroPhenome") &&
                dccExperimentDTO.getCenterId().equalsIgnoreCase("ICS") &&
                parameterStableId.equals("ESLIM_001_001_125") &&
                dccExperimentDTO.getSpecimenId() != null &&
                dccExperimentDTO.getSex().equals(SexType.male) &&
                simpleParameter.getValue().equals("present")
                ) {

                logger.info("Special rule: skipping specimen {}, experiment {}, parameter {}, sex {} ",
                            dccExperimentDTO.getSpecimenId(), dccExperimentDTO.getExperimentId(),
                            parameterStableId, dccExperimentDTO.getSex());
                continue;
            }

            // Check for null/empty values.
            String value = simpleParameter.getValue();
            if ((value == null) || value.trim().isEmpty()) {
                logger.warn("Null/empty value found for simple parameter {}, dcc experiment {}. Skipping parameter ...",
                            simpleParameter.getParameterID(), dccExperimentDTO);
                continue;
            }

            int observationPk = cdaSqlUtils.insertObservation(dbId, biologicalSampleId, parameterStableId, parameterId,
                                                              sequenceId, populationId, observationType, missing,
                                                              parameterStatus, parameterStatusMessage,
                                                              simpleParameter.getValue());

            // Insert experiment_observation
            cdaSqlUtils.insertExperiment_observation(experimentPk, observationPk);


        }

        // mediaParameters
        for (MediaParameter mp : dccSqlUtils.getMediaParameters(dccExperimentDTO.getDcc_procedure_pk())) {
//            observation.setMissingFlag(mp.getParameterStatus() == null ? true : false);
        }

        // ontologyParameters
        for (OntologyParameter op : dccSqlUtils.getOntologyParameters(dccExperimentDTO.getDcc_procedure_pk())) {
//            observation.setMissingFlag(op.getParameterStatus() == null ? true : false);
        }

        // seriesParameters
        for (SeriesParameter seriesParameter : dccSqlUtils.getSeriesParameters(dccExperimentDTO.getDcc_procedure_pk())) {
//            observation.setMissingFlag(seriesParameter.getParameterStatus() == null ? true : false);
        }

        // seriesMediaParameters
        for (SeriesMediaParameter smp : dccSqlUtils.getSeriesMediaParameters(dccExperimentDTO.getDcc_procedure_pk())) {
//            observation.setMissingFlag(smp.getParameterStatus() == null ? true : false);
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
            String colony_id,
            List<SimpleParameter> simpleParameters) throws DataLoadException {
        int biological_model_id = 0;

        String zygosity = getZygosity(simpleParameters);
        String sampleGroup = "experimental";
        biological_model_id = cdaSqlUtils.selectOrInsertBiologicalModel(colony_id, euroPhenomeStrainMapper, zygosity, sampleGroup, allelesBySymbolMap).getId();

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
}