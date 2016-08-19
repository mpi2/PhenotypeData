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

package org.mousephenotype.cda.loads.create.extract.cdabase.support;

import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.util.Date;
import java.util.Set;

/**
 * Created by mrelac on 14/06/16.
 */
public abstract class LogStatusStepListener implements StepExecutionListener {
    protected CommonUtils commonUtils = new CommonUtils();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected Date        start;
    protected Date        stop;

    protected abstract Set<String> logStatus();


    @Override
    public void beforeStep(StepExecution stepExecution) {
        start = new Date();
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stop = new Date();

        Set<String> errorMessages = logStatus();
        logErrors(errorMessages);

        return ExitStatus.COMPLETED;
    }

    private void logErrors(Set<String> errorMessages) {
        if ( ! errorMessages.isEmpty()) {
            logger.warn("WARNINGS:");
            for (String s : errorMessages) {
                logger.warn("\t" + s);
            }
        }
    }
}
