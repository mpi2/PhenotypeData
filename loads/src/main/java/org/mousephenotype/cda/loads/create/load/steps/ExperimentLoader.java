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
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.DccSqlUtils;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.utilities.CommonUtils;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads the experiments from a database with a dcc schema into the cda database.
 *
 * Created by mrelac on 12/10/2016.
 *
 */
public class ExperimentLoader implements Step, Tasklet, InitializingBean {

    private CommonUtils             commonUtils             = new CommonUtils();

    private CdaSqlUtils                cdaSqlUtils;
    private DccSqlUtils                dccSqlUtils;
    private NamedParameterJdbcTemplate jdbcCda;
    private NamedParameterJdbcTemplate jdbcDcc;
    private NamedParameterJdbcTemplate jdbcDccEurophenomeFinal;

    private final Logger         logger      = LoggerFactory.getLogger(this.getClass());
    private StepBuilderFactory   stepBuilderFactory;
    private Map<String, Integer> written     = new HashMap<>();

    private String externalDbShortName;
    private int    externalDbId;


    public ExperimentLoader(
            NamedParameterJdbcTemplate jdbcCda,
            NamedParameterJdbcTemplate jdbcDcc,
            NamedParameterJdbcTemplate jdbcDccEurophenomeFinal,
            StepBuilderFactory stepBuilderFactory,
            CdaSqlUtils cdaSqlUtils,
            DccSqlUtils dccSqlUtils,
            String externalDbShortName) {
        this.jdbcCda = jdbcCda;
        this.jdbcDcc = jdbcDcc;
        this.jdbcDccEurophenomeFinal = jdbcDccEurophenomeFinal;
        this.stepBuilderFactory = stepBuilderFactory;
        this.cdaSqlUtils = cdaSqlUtils;
        this.dccSqlUtils = dccSqlUtils;
        this.externalDbShortName = externalDbShortName;


        // FIXME FIXME FIXME
        written.put("biologicalModel", 0);
        written.put("biologicalSample", 0);
        written.put("liveSample", 0);
        written.put("controlSample", 0);
        written.put("experimentalSample", 0);
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        setExternalDb(externalDbShortName);

        Assert.notNull(jdbcCda, "jdbcCda must be set");
        Assert.notNull(jdbcDcc, "jdbcDcc must be set");
        Assert.notNull(jdbcDccEurophenomeFinal, "jdbcDccEurophenomeFinal must be set");
        Assert.notNull(stepBuilderFactory, "stepBuilderFactory must be set");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must be set");
        Assert.notNull(dccSqlUtils, "dccSqlUtils must be set");
        Assert.notNull(externalDbId, "externalDb short_name (e.g. IMPC, Ensembl, etc.) must be set");
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

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        long startStep = new Date().getTime();

        List<org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment> dccExperiments = dccSqlUtils.getExperiments();
        Map<String, Integer> counts;

        for (org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment dccExperiment : dccExperiments) {
            counts = insertExperiment(dccExperiment);





//            String sampleGroup = (experiment.isIsBaseline()) ? "control" : "experimental";
//            boolean isControl = (sampleGroup.equals("control"));
//
//            if (isControl) {
//                counts = insertSampleControlSpecimen(experiment);
//                written.put("controlSample", written.get("controlSample") + 1);
//            } else {
//                counts = insertSampleExperimentalSpecimen(experiment);
//                written.put("experimentalSample", written.get("experimentalSample") + 1);
//            }
//
//            written.put("biologicalModel", written.get("biologicalModel") + counts.get("biologicalModel"));
//            written.put("biologicalSample", written.get("biologicalSample") + counts.get("biologicalSample"));
//            written.put("liveSample", written.get("liveSample") + counts.get("liveSample"));
        }

//        Iterator<String> missingColonyIdsIt = missingColonyIds.iterator();
//        while (missingColonyIdsIt.hasNext()) {
//            String colonyId = missingColonyIdsIt.next();
//            logger.error("Missing phenotyped_colony information for dcc-supplied colony " + colonyId + ". Skipping...");
//        }

//        Iterator<String> unexpectedStageIt = unexpectedStage.iterator();
//        while (unexpectedStageIt.hasNext()) {
//            String stage = unexpectedStageIt.next();
//            logger.info("Unexpected value for embryonic DCP stage: " + stage);
//        }
//
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
    private Map<String, Integer> insertExperiment(org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment dccExperiment) throws DataLoadException {
        Map<String, Integer> results = new HashMap<>();

        Experiment experiment = createExperiment(dccExperiment);

        return results;
    }

    private Experiment createExperiment(org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment dccExperiment) throws DataLoadException {
        Experiment experiment = new Experiment();

        String colonyId;                        // FIXME
//        PhenotypedColony phenotypedColony = cdaSqlUtils.getPhenotypedColony(dccExperiment.getProcedure().)

        Datasource datasource = new Datasource();
        datasource.setId(externalDbId);
        Date dateOfExperiment = getDateOfExperiment(dccExperiment);
        if (dateOfExperiment == null) {
            return null;
        }

        String metadataCombined;                // FIXME

        String metadataGroup;                   // FIXME

        BiologicalModel biologicalModel;        // FIXME

        List<Observation> observations;;        // FIXME

        Organisation organisation;              // FIXME

        Pipeline pipeline;                      // FIXME

        Procedure procedure;                    // FIXME

        String procedureStableId;               // FIXME

        String procedureStatus;                 // FIXME

        String procedureStatusMessage;          // FIXME

        Project project;                        // FIXME

        String sequenceId;                      // FIXME

//        experiment.setColonyId(colonyId);
//        experiment.setDatasource(datasource);
//        experiment.setDateOfExperiment(dateOfExperiment);
//        experiment.setExternalId(dccExperiment.getExperimentID());
////        experiment.setId();
//        experiment.setMetadataCombined(metadataCombined);
//        experiment.setMetadataGroup(metadataGroup);
//        experiment.setModel(biologicalModel);
//        experiment.setObservations(observations);
//        experiment.setOrganisation(organisation);
//        experiment.setPipeline(pipeline);
//        experiment.setProcedure(procedure);
//        experiment.setProcedureStableId(procedureStableId);
//        experiment.setProcedureStatus(procedureStatus);
//        experiment.setProcedureStatusMessage(procedureStatusMessage);
//        experiment.setProject(project);
//        experiment.setSequenceId(sequenceId);

        cdaSqlUtils.insertExperiment(experiment);

        return experiment;
    }

    public String getExternalDb() {
        return externalDbShortName;
    }

    public void setExternalDb(String externalDbShortName) {
        this.externalDbShortName = externalDbShortName;
        this.externalDbId = cdaSqlUtils.getExternalDbId(externalDbShortName);
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

    /**
     * Validates and returns date of experiment, if valid; null otherwise. Logs error message if invalid.
     * @param dccExperiment
     * @return the date of experiment, if valid; null otherwise.
     */
    public Date getDateOfExperiment(org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Experiment dccExperiment) {
        Date date;
        Date dccDate = dccExperiment.getDateOfExperiment().getTime();
        String experimentId = dccExperiment.getExperimentID();

        try {
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse("1975-01-01");

            if (dccDate.before(date)) {

                logger.warn("Skipping experiment '{}' due to invalid date {}", experimentId, dccDate);
                return null;
            }

        } catch (Exception e) {

            logger.warn("Skipping experiment '{}' due to invalid parsed date {}", experimentId, dccDate);
            return null;
        }

        return date;
    }
}