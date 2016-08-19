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

package org.mousephenotype.cda.loads.create.extract.cdabase.steps;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.util.Date;

/**
 * Created by mrelac on 13/04/2016.
 */
public class CdabaseDbInitialiser implements Tasklet, InitializingBean {

    private final CommonUtils commonUtils = new CommonUtils();
    private       String      dbname;
    private final Logger      logger      = LoggerFactory.getLogger(this.getClass());

    @Autowired
    DataSource cdabase;


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cdabase, "cdabase must be set");
        dbname = cdabase.getConnection().getCatalog();
        Assert.notNull(dbname, "dbname must be set");
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {

        long startStep = new Date().getTime();

        logger.info("Database '{}': Create cdabase tables - start", dbname);
        org.springframework.core.io.Resource r = new ClassPathResource("scripts/cdabase/schema.sql");
//        ResourceDatabasePopulator            p = new ResourceDatabasePopulator(false, false, "iso-8859-15", r);
        ResourceDatabasePopulator            p = new ResourceDatabasePopulator(r);
        p.execute(cdabase);

        logger.info("Database '{}': Create cda_base tables - end. Total elapsed time: {}", dbname, commonUtils.msToHms(new Date().getTime() - startStep));

        return RepeatStatus.FINISHED;
    }

    @StepScope
    public Step getStep(StepBuilderFactory stepBuilderFactory) {
        return stepBuilderFactory.get("databaseInitialiserStep")
                                 .tasklet(this)
                                 .build();
    }
}