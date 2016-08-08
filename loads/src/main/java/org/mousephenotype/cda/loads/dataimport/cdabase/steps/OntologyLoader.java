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

package org.mousephenotype.cda.loads.dataimport.cdabase.steps;

import org.mousephenotype.cda.loads.exceptions.DataImportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Loads a single ontology file into the ontology_term table of the target database.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class OntologyLoader implements Step, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Required for ItemReader
    private int dbId;
    private String sourceFilename;
    private String prefix;

    private OntologyReader ontologyReader;
    private OntologyWriter ontologyWriter;
    private StepBuilderFactory stepBuilderFactory;


    public OntologyLoader(String sourceFilename, int dbId, String prefix, StepBuilderFactory stepBuilderFactory, OntologyWriter ontologyWriter) throws DataImportException {
        this.sourceFilename = sourceFilename;
        this.dbId = dbId;
        this.prefix = prefix;
        this.stepBuilderFactory = stepBuilderFactory;
        this.ontologyWriter = ontologyWriter;

        ontologyReader = new OntologyReader(sourceFilename, dbId, prefix);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
  	    Assert.notNull(sourceFilename, "sourceFilename must be set");
        Assert.notNull(dbId, "dbId must be set");
        Assert.notNull(prefix, "prefix must be set");

    }

    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "ontologyLoaderStep";
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
        stepBuilderFactory.get("ontologyLoaderStep")
                .chunk(100000)
                .reader(ontologyReader)
                .writer(ontologyWriter)
                .build()
                .execute(stepExecution);
    }
}