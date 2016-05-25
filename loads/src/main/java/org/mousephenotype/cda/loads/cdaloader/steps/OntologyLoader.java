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

package org.mousephenotype.cda.loads.cdaloader.steps;

import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.loads.cdaloader.OntologyParser;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Loads a single ontology file into the ontology_term table of the target database.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class OntologyLoader implements Step, ItemReader<OntologyTerm>, InitializingBean {

    CommonUtils commonUtils = new CommonUtils();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // Required for ItemReader
    private int dbId;
    private String sourceFilename;
    private String prefix;

    // Required for ItemReader read() implementation
    private int readIndex = 0;
    private List<OntologyTerm> terms = null;


    public OntologyLoader(String sourceFilename, int dbId, String prefix) throws CdaLoaderException {
        this.sourceFilename = sourceFilename;
        this.dbId = dbId;
        this.prefix = prefix;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
  	    Assert.notNull(sourceFilename, "sourceFilename must be set");
        Assert.notNull(dbId, "dbId must be set");
        Assert.notNull(prefix, "prefix must be set");
    }

    public Step getStep(StepBuilderFactory stepBuilderFactory, ItemWriter ontologyWriter) {
        return stepBuilderFactory.get("loadOntologyStep")
                .chunk(1000)
                .reader(this)
                .writer(ontologyWriter)
                .build();
    }

    // STEP IMPLEMENTATION
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
        return 1;
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

    }


    // ITEMREADER IMPLEMENTATION

    /**
     * Reads a piece of input data and advance to the next one. Implementations
     * <strong>must</strong> return <code>null</code> at the end of the input
     * data set. In a transactional setting, caller might get the same item
     * twice from successive calls (or otherwise), if the first call was in a
     * transaction that rolled back.
     *
     * @return T the item to be processed
     * @throws ParseException                if there is a problem parsing the current record
     *                                       (but the next one may still be valid)
     * @throws NonTransientResourceException if there is a fatal exception in
     *                                       the underlying resource. After throwing this exception implementations
     *                                       should endeavour to return null from subsequent calls to read.
     * @throws UnexpectedInputException      if there is an uncategorised problem
     *                                       with the input data. Assume potentially transient, so subsequent calls to
     *                                       read might succeed.
     * @throws Exception                     if an there is a non-specific error.
     */
    @Override
    public OntologyTerm read() throws CdaLoaderException {
        OntologyTerm term = null;

        if (terms == null) {
            try {
                terms = new OntologyParser(sourceFilename, prefix).getTerms();
                logger.info("FILENAME: " + sourceFilename);
                logger.info("PREFIX: " + prefix);
                logger.info("TERMS COUNT: " + terms.size());
                logger.info("");

            } catch (OWLOntologyCreationException e) {
                throw new CdaLoaderException(e);
            }
        }

        if (readIndex < terms.size()) {
            term = terms.get(readIndex);
            term.setId(new DatasourceEntityId(term.getId().getAccession(), dbId));
        }

        readIndex++;

        return term;
    }
}