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

import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.OntologyTermAnomaly;
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
import org.springframework.util.Assert;

import java.util.*;

/**
 * Updates the impress ontology terms with the latest terms.
 *
 * Created by mrelac on 31/08/2016.
 *
 */
public class ImpressUpdater implements Step, Tasklet, InitializingBean {

    private CdaSqlUtils                 cdaSqlUtils;
    private CommonUtils                 commonUtils = new CommonUtils();
    private NamedParameterJdbcTemplate  jdbcImpress;
    private final Logger                logger = LoggerFactory.getLogger(this.getClass());
    private StepBuilderFactory          stepBuilderFactory;


    public ImpressUpdater(NamedParameterJdbcTemplate jdbcImpress, StepBuilderFactory stepBuilderFactory,
                          CdaSqlUtils cdaSqlUtils) {
        this.jdbcImpress = jdbcImpress;
        this.stepBuilderFactory = stepBuilderFactory;
        this.cdaSqlUtils = cdaSqlUtils;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(jdbcImpress, "jdbcImpress must be set");
        Assert.notNull(stepBuilderFactory, "stepBuilderFactory must be set");
        Assert.notNull(cdaSqlUtils, "cdaSqlUtils must be set");
    }

    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "impressUpdaterStep";
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
        stepBuilderFactory.get("impressUpdaterStep")
                .tasklet(this)
                .build()
                .execute(stepExecution);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        long startStep = new Date().getTime();
        String query = "SELECT ontology_acc FROM phenotype_parameter_ontology_annotation";

        List<String> ontologyAccessionIds = jdbcImpress.queryForList(query, new HashMap<>(), String.class);
        Set<OntologyTermAnomaly> anomalies = cdaSqlUtils.checkAndUpdateOntologyTerms(jdbcImpress, ontologyAccessionIds, "phenotype_parameter_ontology_association", "ontology_acc");

        if ( ! anomalies.isEmpty()) {
            System.out.println("\nanomalies:");
            List<String> anomalyReasons = new ArrayList<>();
            for (OntologyTermAnomaly anomaly : anomalies) {
                anomalyReasons.add(anomaly.getReason());
            }
            Collections.sort(anomalyReasons);
            for (String anomalyReason : anomalyReasons) {
                System.out.println("\t" + anomalyReason);
            }
        }

        logger.debug("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));
        contribution.setExitStatus(ExitStatus.COMPLETED);
        chunkContext.setComplete();

        return RepeatStatus.FINISHED;
    }
}