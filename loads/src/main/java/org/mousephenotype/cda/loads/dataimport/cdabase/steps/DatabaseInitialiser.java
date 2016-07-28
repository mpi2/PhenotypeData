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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

/**
 * Created by mrelac on 13/04/2016.
 */
public class DatabaseInitialiser implements Tasklet, InitializingBean, ApplicationContextAware {

    private final CommonUtils commonUtils = new CommonUtils();
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());
    private final String      mysql       = commonUtils.getMysqlFullpath();

    private String dbhostname;
    private String dbport;


    @Value("${cdabase.url}")
    private String cdaUrl;

    @Value("${cdabase.dbname}")
    private String dbname;

    @Value("${cdabase.username}")
    private String dbusername;

    @Value("${cdabase.password}")
    private String dbpassword;

    private ApplicationContext applicationContext;


    @Override
    public void afterPropertiesSet() throws Exception {
        URL url = new URL(cdaUrl.replace("jdbc:mysql:", "http:"));     // Replace the jdbc:mysql: protocol with http. URL barks at jdbc:mysql:.
        dbhostname = url.getHost();
        dbport = (url.getPort() == -1 ? "3306" : Integer.toString(url.getPort()));

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









//        String filename = applicationContext.getResource("scripts/schema.sql").getFile().getAbsolutePath();
        File f = applicationContext.getResource("classpath:scripts/schema.sql").getFile();
        System.out.println("absolutePath: " + f.getAbsolutePath());
        System.out.println("canonicalPath: " + f.getCanonicalPath());
        System.out.println("name: " + f.getName());
        System.out.println("path: " + f.getPath());

        String filename = f.getCanonicalPath();











        String[] commands = new String[] { "/bin/sh", "-c", mysql + " --host=" + dbhostname + " --port=" + dbport + " --user=" + dbusername + " --password=" + dbpassword + " " + dbname + " < " + filename };

        try {
            System.out.println("cmd = " + StringUtils.join(commands, " "));
            Process process = Runtime.getRuntime().exec(commands);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                 InputStreamReader(process.getErrorStream()));

            int exitVal = process.waitFor();
            System.out.println("exitVal = " + exitVal);
            if (exitVal > 0) {
                String s = null;
                while ((s = stdInput.readLine()) != null) {
                    System.out.println(s);
                }
                while ((s = stdError.readLine()) != null) {
                    System.err.println(s);
                }
            }
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}