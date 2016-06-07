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

import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.transform.DefaultFieldSet;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.regex.Pattern;

/**
 * Loads the report.txt IMSR strain file into the strain and synonym tables of the target database.
 * As of 31-May-2016, the report.txt file's first line is a heading that must be skipped.
 *
 * Created by mrelac on 13/04/2016.
 *
 */
public class StrainLoaderImsr implements Step, InitializingBean {

    private       CommonUtils commonUtils = new CommonUtils();
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());
    private FlatFileItemReader<FieldSet> imsrReader;
    private String                       sourceFilename;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private ItemProcessor strainProcessorImsr;

    @Autowired
    private StrainWriter writer;


    public StrainLoaderImsr(String sourceFilename) throws CdaLoaderException {
        this.sourceFilename = sourceFilename;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

  	    Assert.notNull(sourceFilename, "sourceFilename must be set");

        imsrReader = new FlatFileItemReader<>();
        imsrReader.setResource(new FileSystemResource(sourceFilename));
        imsrReader.setLineMapper((line, lineNumber) -> {
            FieldSet fieldset = new DefaultFieldSet(line.split(Pattern.quote("\t")));
            return fieldset;
        });
    }


    // Step IMPLEMENTATION


    /**
     * @return the name of this step.
     */
    @Override
    public String getName() {
        return "strainLoaderImsrStep";
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

        stepBuilderFactory.get("strainLoaderStep")
                .listener(new StrainLoaderStepListener())
                .chunk(100)
                .reader(imsrReader)
                .processor(strainProcessorImsr)
                .writer(writer)
                .build()
                .execute(stepExecution);
    }

    public class StrainLoaderStepListener implements StepExecutionListener {
        private Date start;
        private Date stop;

        /**
         * Initialize the state of the listener with the {@link StepExecution} from
         * the current scope.
         *
         * @param stepExecution
         */
        @Override
        public void beforeStep(StepExecution stepExecution) {
            start = new Date();
        }

        /**
         * Give a listener a chance to modify the exit status from a step. The value
         * returned will be combined with the normal exit status using
         * {@link ExitStatus#and(ExitStatus)}.
         * <p/>
         * Called after execution of step's processing logic (both successful or
         * failed). Throwing exception in this method has no effect, it will only be
         * logged.
         *
         * @param stepExecution
         * @return an {@link ExitStatus} to combine with the normal value. Return
         * null to leave the old value unchanged.
         */
        @Override
        public ExitStatus afterStep(StepExecution stepExecution) {
            int addedStrainCount = ((StrainProcessorImsr)strainProcessorImsr).getAddedEucommStrainCount();
            int addedSynonymCount = ((StrainProcessorImsr)strainProcessorImsr).getAddedSynonymCount();
            stop = new Date();

            logger.info("Added {} strains and {} synonyms in {}", addedStrainCount, addedSynonymCount, commonUtils.formatDateDifference(start, stop));

            return ExitStatus.COMPLETED;
        }
    }
}