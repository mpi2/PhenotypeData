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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Loads the cda database from the dcc database.
 * Created by mrelac on 31/08/2016.
 */
@EnableBatchProcessing
@Import( {LoadConfigBeans.class })
public class LoadFromDcc implements CommandLineRunner {
    private List<Job>           jobs = new ArrayList<>();

    private DataSource         cdaDataSource;
    public  ExperimentLoader   experimentDccEurophenomeLoader;
    public  ExperimentLoader   experimentDccLoader;
    public  JobBuilderFactory  jobBuilderFactory;
    public  JobRepository      jobRepository;
    public  SampleLoader       sampleDccEurophenomeLoader;
    public  SampleLoader       sampleDccLoader;
    public  StepBuilderFactory stepBuilderFactory;

    private final org.slf4j.Logger logger   = LoggerFactory.getLogger(this.getClass());
    private       SqlUtils         sqlUtils = new SqlUtils();

    @Inject
    public LoadFromDcc(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            JobRepository jobRepository,
            DataSource cdaDataSource,
            SampleLoader sampleDccEurophenomeLoader,
            ExperimentLoader experimentDccEurophenomeLoader,
            SampleLoader sampleDccLoader,
            ExperimentLoader experimentDccLoader
    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobRepository = jobRepository;
        this.cdaDataSource = cdaDataSource;
        this.sampleDccEurophenomeLoader = sampleDccEurophenomeLoader;
        this.experimentDccEurophenomeLoader = experimentDccEurophenomeLoader;
        this.sampleDccLoader = sampleDccLoader;
        this.experimentDccLoader = experimentDccLoader;
    }


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


    @Override
    public void run(String... args) throws Exception {
        initialize(args);
        runJobs();
    }


    private void initialize(String[] args) throws DataLoadException {

        OptionParser parser = new OptionParser();

        // parameter to indicate the profile to use
        parser.accepts("profile").withRequiredArg().ofType(String.class);

        // parameter to indicate the components to build (e.g. specimens,experiments)
        parser.accepts("component").withRequiredArg().ofType(String.class);

        // parameter to indicate the dcc files to build (e.g. europhenome,dcc)
        parser.accepts("dcc").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        if ( ! options.has("component")) {
            String message = "Missing required command-line parameter 'component (e.g. --component specimens --component experiments)'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        boolean processSpecimens = false;
        boolean processExperiments = false;
        List<String> components = (List<String>) options.valuesOf("component");
        for (String component : components) {
            if (component.equals("specimens")) {
                processSpecimens = true;
            } else if (component.equals("experiments")) {
                processExperiments = true;
            } else {
                String message = "Invalid component '" + component + "'. Valid components are 'specimens' and 'experiments'.";
                logger.error(message);
                throw new DataLoadException(message);
            }
        }

        if ( ! options.has("dcc")) {
            String message = "Missing required command-line parameter 'dcc (e.g. europhenome,dcc)'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        boolean processEurophenome = false;
        boolean processDcc = false;
        List<String> dccs = (List<String>) options.valuesOf("dcc");
        for (String dcc : dccs) {
            if (dcc.equals("europhenome")) {
                processEurophenome = true;
            } else if (dcc.equals("dcc")) {
                processDcc = true;
            } else {
                String message = "Invalid dcc '" + dcc + "'. Valid dcc values are 'europhenome' and 'dcc'.";
                logger.error(message);
                throw new DataLoadException(message);
            }
        }

        if (processSpecimens) {
            if (processEurophenome) {
                jobs.add(specimensFromDccEurophenome());
            }
            if (processDcc) {
                jobs.add(specimensFromDcc());
            }
        }

        if (processExperiments) {
            if (processEurophenome) {
                jobs.add(experimentsFromDccEurophenome());
            }
            if (processDcc) {
                jobs.add(experimentsFromDcc());
            }
        }

        List<String> parts = new ArrayList<>();
        if (processEurophenome && processSpecimens)
            parts.add("europhenome specimens");
        if (processDcc && processSpecimens)
            parts.add("dcc specimens");
        if (processEurophenome && processExperiments)
            parts.add("europhenome experiments");
        if (processDcc && processExperiments)
            parts.add("dcc experiments");

        logger.info("processing {}", StringUtils.join(parts, ", "));
    }


    public List<Job> runJobs() throws DataLoadException {

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

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = dateFormat.format(new Date());

        for (Job job : jobs) {
            try {
                JobInstance instance = jobRepository.createJobInstance("flow_" + now + "_" + job.getName(), new JobParameters());
                JobExecution execution = jobRepository.createJobExecution(instance, new JobParameters(), "jobExec_" + now + "_" + job.getName());
                job.execute(execution);
            } catch (Exception e) {

                throw new DataLoadException(e);
            }
        }

        return jobs;
    }

    public Job specimensFromDcc() throws DataLoadException {

        // Specimens to Samples
        Flow samplesFlow = new FlowBuilder<Flow>("samplesDccFlow").from(sampleDccLoader).end();

        return jobBuilderFactory.get("samplesDccJobJob")
                .incrementer(new RunIdIncrementer())
                .start(samplesFlow)
                .end()
                .build();
    }

    public Job experimentsFromDcc() throws DataLoadException {

        // Dcc Experiments to Cda Experiments
        Flow experimentsFlow = new FlowBuilder<Flow>("experimentsDccFlow").from(experimentDccLoader).end();

        return jobBuilderFactory.get("experimentsDccJob")
                .incrementer(new RunIdIncrementer())
                .start(experimentsFlow)
                .end()
                .build();
    }

    public Job specimensFromDccEurophenome() throws DataLoadException {

        // Specimens to Samples
        Flow samplesFlow = new FlowBuilder<Flow>("samplesDccEurophenomeFlow").from(sampleDccEurophenomeLoader).end();

        return jobBuilderFactory.get("samplesDccEurophenomeJob")
                .incrementer(new RunIdIncrementer())
                .start(samplesFlow)
                .end()
                .build();
    }

    public Job experimentsFromDccEurophenome() throws DataLoadException {

        // Dcc Experiments to Cda Experiments
        Flow experimentsFlow = new FlowBuilder<Flow>("experimentsDccEurophenomeFlow").from(experimentDccEurophenomeLoader).end();

        return jobBuilderFactory.get("experimentsDccEurophenomeJob")
                .incrementer(new RunIdIncrementer())
                .start(experimentsFlow)
                .end()
                .build();
    }
}