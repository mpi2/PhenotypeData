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

package org.mousephenotype.cda.loads.create.load;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.create.load.steps.ImpressUpdater;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Lazy;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Loads the cda database from the impress database.
 * Created by mrelac on 30/09/2016.
 */
@ComponentScan
@EnableBatchProcessing
public class ImpressLoader implements CommandLineRunner {

    private final org.slf4j.Logger logger   = LoggerFactory.getLogger(this.getClass());
    private       SqlUtils         sqlUtils = new SqlUtils();

    private JobBuilderFactory jobBuilderFactory;
    private JobRepository     jobRepository;
    private ImpressUpdater    impressUpdater;
    private DataSource        cdaDataSource;



    @Inject
    @Lazy
    public ImpressLoader(
            JobBuilderFactory jobBuilderFactory,
            JobRepository jobRepository,
            ImpressUpdater impressUpdater,
            DataSource cdaDataSource
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.jobRepository = jobRepository;
        this.impressUpdater = impressUpdater;
        this.cdaDataSource = cdaDataSource;
    }


    /**
     * This class is intended to be a command-line callable java main program that loads the cda database.
     */
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ImpressLoader.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }



    @Override
    public void run(String... args) throws Exception {

        runJobs();
    }

    public Job[] runJobs() throws DataLoadException {

        // Populate Spring Batch tables if necessary.
        try {
            boolean exists = sqlUtils.columnInSchemaMysql(cdaDataSource.getConnection(), "BATCH_JOB_INSTANCE", "JOB_INSTANCE_ID");
            if ( ! exists) {
                logger.info("Creating SPRING BATCH tables");
                sqlUtils.createSpringBatchTables(cdaDataSource);
            }

        } catch (Exception e) {
            throw new DataLoadException("Unable to create Spring Batch tables.");
        }

        Job[] jobs = new Job[] {
                updateImpress()
        };
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = dateFormat.format(new Date());

        for (int i = 0; i < jobs.length; i++) {
            Job job = jobs[i];
            try {
                JobInstance instance = jobRepository.createJobInstance("flow_" + now + "_" + i, new JobParameters());
                JobExecution execution = jobRepository.createJobExecution(instance, new JobParameters(), "jobExec_" + now + "_" + i);
                job.execute(execution);
            } catch (Exception e) {

                throw new DataLoadException(e);
            }
        }

        return jobs;
    }

    public Job updateImpress() throws DataLoadException {

        Flow samplesFlow = new FlowBuilder<Flow>("impressFlow").from(impressUpdater).end();

        return jobBuilderFactory.get("impressJob")
                .incrementer(new RunIdIncrementer())
                .start(samplesFlow)
                .end()
                .build();
    }
}