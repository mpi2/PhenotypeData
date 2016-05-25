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

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Date;

/**
 * Created by mrelac on 13/04/2016.
 */
public class DatabaseInitialiser implements Tasklet, InitializingBean {

    CommonUtils commonUtils = new CommonUtils();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cdaload.dbhostname}")
    private String dbhostname;

    @Value("${cdaload.dbport}")
    private String dbport;

    @Value("${cdaload.dbname}")
    private String dbname;

    @Value("${cdaload.username}")
    private String dbusername;

    @Value("${cdaload.password}")
    private String dbpassword;

    @Value("${cdaload.mysql}")
    private String mysql;


    @Override
    public void afterPropertiesSet() throws Exception {
  	    Assert.notNull(mysql, "mysql executable must be set");
        Assert.notNull(dbhostname, "dbhostname must be set");
        Assert.notNull(dbport, "dbport must be set");
        Assert.notNull(dbusername, "dbusername must be set");
        Assert.notNull(dbpassword, "dbpassword must be set");
        Assert.notNull(dbname, "dbname must be set");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        long startStep = new Date().getTime();

        ClassLoader classloader = getClass().getClassLoader();
        String filename = classloader.getResource("scripts/schema.sql").getPath();

        String[] commands = new String[] { "/bin/sh", "-c", mysql + " --host=" + dbhostname + " --port=" + dbport + " --user=" + dbusername + " --password=" + dbpassword + " " + dbname + " < " + filename };

        try {
            System.out.println("cmd = " + StringUtils.join(commands, " "));
            Process p = Runtime.getRuntime().exec(commands);
            int exitVal = p.waitFor();
            System.out.println("exitVal = " + exitVal);
        }

        catch(IOException | InterruptedException e) {
            System.out.println("FAIL: " + e.getLocalizedMessage());
        }

        logger.info("Total steps elapsed time: " + commonUtils.msToHms(new Date().getTime() - startStep));

        return RepeatStatus.FINISHED;
    }

    @StepScope
    public Step getStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("databaseInitialiserStep")
                .tasklet(this)
                .build();
    }
}