/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.ri.extract;

import org.mousephenotype.cda.ri.core.exceptions.InterestException;
import org.mousephenotype.cda.ri.core.utils.RiSqlUtils;
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
import org.springframework.boot.WebApplicationType;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;


/**
 * Created by mrelac on 22/05/2017.
 *
 * This class is intended to be a command-line callable java main program that loads the ri database with the lastest
 * iMits data.
 */
@EnableBatchProcessing
@ComponentScan({"org.mousephenotype.ri.extract"})
public class ApplicationExtract implements CommandLineRunner {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private JobBuilderFactory  jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    private JobRepository      jobRepository;
    private List<Downloader>   downloader;
    private GeneLoader         imitsLoader;
    private String             pathToGeneStatusChangeReport;
    private DataSource         riDataSource;
    private RiSqlUtils         riSqlUtils;


    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ApplicationExtract.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }


    @Inject
    public ApplicationExtract(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            JobRepository jobRepository,
            List<Downloader> downloader,
            GeneLoader imitsLoader,
            DataSource riDataSource,
            RiSqlUtils riSqlUtils
    ) {

        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobRepository = jobRepository;
        this.downloader = downloader;
        this.imitsLoader = imitsLoader;
        this.riDataSource = riDataSource;
        this.riSqlUtils = riSqlUtils;
    }

    /**
     * Callers may opt to pass in the fully-qualified path of the gene status report (default is GeneStatusChange.tsv)
     * by using the --report=xxx argument. If supplied, no download is done; the supplied report file is used instead.
     *
     * @param args
     * @throws Exception
     */

    @Override
    public void run(String... args) throws Exception {

        PropertySource ps = new SimpleCommandLinePropertySource(args);
        if (ps.containsProperty("report")) {
            pathToGeneStatusChangeReport = ps.getProperty("report").toString();
            imitsLoader.setPathToGeneStatusChangeReport(pathToGeneStatusChangeReport);
        }

        runJobs();
    }

    public Job[] runJobs() throws InterestException {

        // Populate Spring Batch tables if necessary.
        try {
            boolean exists = riSqlUtils.columnInSchemaMysql(riDataSource.getConnection(), "BATCH_JOB_INSTANCE", "JOB_INSTANCE_ID");
            if ( ! exists) {
                riSqlUtils.createSpringBatchTables(riDataSource);
            }

        } catch (Exception e) {
            throw new InterestException("Unable to create Spring Batch tables.");
        }

        List<Job> jobList = new ArrayList<>();
        if (pathToGeneStatusChangeReport == null) {
            jobList.add(downloaderJob());
        }
        jobList.add(imitsLoaderJob());

        Job[] jobs = jobList.toArray(new Job[0]);

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = dateFormat.format(new Date());

        for (int i = 0; i < jobs.length; i++) {
            Job job = jobs[i];
            try {
                JobInstance instance = jobRepository.createJobInstance("flow_" + now + "_" + i, new JobParameters());
                JobExecution execution = jobRepository.createJobExecution(instance, new JobParameters(), "jobExec_" + now + "_" + i);
                job.execute(execution);

            } catch (Exception e) {

                throw new InterestException(e);
            }
        }

        return jobs;
    }

    public Job downloaderJob() throws InterestException {

        List<Flow> flows = new ArrayList<>();
        for (int i = 0; i < downloader.size(); i++) {
            Downloader downloader = this.downloader.get(i);
            flows.add(new FlowBuilder<Flow>("subflow_" + i).from(downloader.getStep(stepBuilderFactory)).end());
        }

        FlowBuilder<Flow> flowBuilder = new FlowBuilder<Flow>("splitflow").start(flows.get(0));

        for (int i = 1; i < downloader.size(); i++) {
            SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor(Executors.defaultThreadFactory());
            flowBuilder.split(executor).add(flows.get(i));
        }

        Job j = jobBuilderFactory.get("downloaderJob")
                .incrementer(new RunIdIncrementer())
                .start(flowBuilder.build())
                .end()
                .build();

        imitsLoader.setPathToGeneStatusChangeReport(downloader.get(0).getTargetFilename());

        return j;
    }

    public Job imitsLoaderJob() throws InterestException {

        // imits
        Flow geneFlow = new FlowBuilder<Flow>("geneFlow").from(imitsLoader).end();

        return jobBuilderFactory.get("geneLoaderJob")
                .incrementer(new RunIdIncrementer())
                .start(geneFlow)
                .end()
                .build();
    }
}