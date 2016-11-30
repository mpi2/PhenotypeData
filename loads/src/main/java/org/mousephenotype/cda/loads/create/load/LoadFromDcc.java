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
import org.mousephenotype.cda.loads.create.load.config.LoadConfigBeans;
import org.mousephenotype.cda.loads.create.load.steps.ExperimentLoader;
import org.mousephenotype.cda.loads.create.load.steps.SampleLoader;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Loads the cda database from the dcc database.
 * Created by mrelac on 31/08/2016.
 */
@EnableBatchProcessing
@Import( {LoadConfigBeans.class })
public class LoadFromDcc implements CommandLineRunner {

    /**
     * This class is intended to be a command-line callable java main program that loads the cda database with all
     * data conforming to the dcc schema. This input data may be in different databases.
     */
    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(LoadFromDcc.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.run(args);
    }

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    @JobScope
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobRepository jobRepository;

    @Autowired
    public ExperimentLoader experimentDccLoader;

    @Autowired
    public SampleLoader sampleDccLoader;

    @Autowired
    public ExperimentLoader experimentDccEurophenomeLoader;

    @Autowired
    public SampleLoader sampleDccEurophenomeLoader;

    @Autowired
    @Qualifier("cdaDataSource")
    private DataSource cdaDataSource;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils();

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
                  fromDccEurophenome(),
                  fromDcc()
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

    public Job fromDcc() throws DataLoadException {

        // Specimens to Samples
        Flow samplesFlow = new FlowBuilder<Flow>("samplesDccFlow").from(sampleDccLoader).end();

        // Dcc Experiments to Cda Experiments
        Flow experimentsFlow = new FlowBuilder<Flow>("experimentsDccFlow").from(experimentDccLoader).end();

        return jobBuilderFactory.get("samplesJob")
                .incrementer(new RunIdIncrementer())
                .start(samplesFlow)
                .next(experimentsFlow)
                .end()
                .build();
    }

    public Job fromDccEurophenome() throws DataLoadException {

        // Specimens to Samples
        Flow samplesFlow = new FlowBuilder<Flow>("samplesDccEurophenomeFlow").from(sampleDccEurophenomeLoader).end();

        // Dcc Experiments to Cda Experiments
        Flow experimentsFlow = new FlowBuilder<Flow>("experimentsDccEurophenomeFlow").from(experimentDccEurophenomeLoader).end();

        return jobBuilderFactory.get("samplesJob")
                .incrementer(new RunIdIncrementer())
                .start(samplesFlow)
                .next(experimentsFlow)
//                .start(experimentsFlow)
                .end()
                .build();
    }
}