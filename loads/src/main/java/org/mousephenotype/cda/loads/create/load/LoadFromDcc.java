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
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Loads the cda database from the dcc database.
 * Created by mrelac on 31/08/2016.
 */
// FIXME
@ComponentScan
@Deprecated
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
    @Lazy
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
        initialise(args);
        runJobs();
    }


    private void initialise(String[] args) throws DataLoadException {

        OptionParser parser = new OptionParser();

        // parameter to indicate the profile to use
        parser.accepts("profile").withRequiredArg().ofType(String.class);

        // parameter to indicate the components to build (e.g. specimens,experiments)
        parser.accepts("component").withRequiredArg().ofType(String.class);

        // parameter to indicate the source of the dcc files to build (e.g. europhenome,dcc)
        parser.accepts("source").withRequiredArg().ofType(String.class);

        OptionSet options = parser.parse(args);

        if ( ! options.has("component")) {
            String message = "Missing required command-line parameter 'component (e.g. --component specimens --component experiments)'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        boolean loadSpecimens = false;
        boolean loadExperiments = false;
        List<String> components = (List<String>) options.valuesOf("component");
        for (String component : components) {
            if (component.equals("specimens")) {
                loadSpecimens = true;
            } else if (component.equals("experiments")) {
                loadExperiments = true;
            } else {
                String message = "Invalid component '" + component + "'. Valid components are 'specimens' and 'experiments'.";
                logger.error(message);
                throw new DataLoadException(message);
            }
        }

        if ( ! options.has("source")) {
            String message = "Missing required source parameter (e.g. europhenome,dcc)'";
            logger.error(message);
            throw new DataLoadException(message);
        }
        boolean loadEurophenome = false;
        boolean loadDcc = false;
        List<String> sources = (List<String>) options.valuesOf("source");
//        for (String source : sources) {
//            if (source.equals("europhenome")) {
//                loadEurophenome = true;
//            } else if (source.equals("dcc")) {
//                loadDcc = true;
//            } else {
//                String message = "Invalid source '" + source + "'. Valid source values are 'europhenome' and 'dcc'.";
//                logger.error(message);
//                throw new DataLoadException(message);
//            }
//        }
//
//        // Process in this order: europhenome_specimens, europhenome_experiments, dcc_specimens, dcc_experiments
//        if (loadEurophenome && loadSpecimens)
//            jobs.add(processEurophenomeSpecimens());
//
//        if (loadEurophenome && loadExperiments)
//            jobs.add(processEurophenomeExperiments());
//
//        if (loadDcc && loadSpecimens)
//            jobs.add(processDccSpecimens());
//
//        if (loadDcc && loadExperiments)
//            jobs.add(processDccExperiments());
//
//        List<String> parts = new ArrayList<>();
//        if (loadEurophenome && loadSpecimens)
//            parts.add("europhenome specimens");
//        if (loadDcc && loadSpecimens)
//            parts.add("dcc specimens");
//        if (loadEurophenome && loadExperiments)
//            parts.add("europhenome experiments");
//        if (loadDcc && loadExperiments)
//            parts.add("dcc experiments");
//
//        logger.info("processing {}", StringUtils.join(parts, ", "));
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

//    public Job processEurophenomeSpecimens() throws DataLoadException {
//
//        // Specimens to Samples
//        Flow samplesFlow = new FlowBuilder<Flow>("processEurophenomeSpecimensFlow").from(sampleDccEurophenomeLoader).end();
//
//        return jobBuilderFactory.get("processEurophenomeSpecimensJob")
//                .incrementer(new RunIdIncrementer())
//                .start(samplesFlow)
//                .end()
//                .build();
//    }
//
//    public Job processEurophenomeExperiments() throws DataLoadException {
//
//        // Dcc Experiments to Cda Experiments
//        Flow experimentsFlow = new FlowBuilder<Flow>("processEurophenomeExperimentsFlow").from(experimentDccEurophenomeLoader).end();
//
//        return jobBuilderFactory.get("processEurophenomeExperimentsJob")
//                .incrementer(new RunIdIncrementer())
//                .start(experimentsFlow)
//                .end()
//                .build();
//    }
//
//    public Job processDccSpecimens() throws DataLoadException {
//
//        // Specimens to Samples
//        Flow samplesFlow = new FlowBuilder<Flow>("processDccSpecimensFlow").from(sampleDccLoader).end();
//
//        return jobBuilderFactory.get("processDccSpecimensJob")
//                .incrementer(new RunIdIncrementer())
//                .start(samplesFlow)
//                .end()
//                .build();
//    }
//
//    public Job processDccExperiments() throws DataLoadException {
//
//        // Dcc Experiments to Cda Experiments
//        Flow experimentsFlow = new FlowBuilder<Flow>("processDccExperimentsFlow").from(experimentDccLoader).end();
//
//        return jobBuilderFactory.get("processDccExperimentsJob")
//                .incrementer(new RunIdIncrementer())
//                .start(experimentsFlow)
//                .end()
//                .build();
//    }
}