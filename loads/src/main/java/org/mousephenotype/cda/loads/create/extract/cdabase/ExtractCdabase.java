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

package org.mousephenotype.cda.loads.create.extract.cdabase;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.create.extract.cdabase.config.ExtractCdabaseConfigBeans;
import org.mousephenotype.cda.loads.create.extract.cdabase.steps.*;
import org.mousephenotype.cda.loads.exceptions.DataImportException;
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
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Created by mrelac on 12/04/2016.
 */
@EnableBatchProcessing
@Import( {ExtractCdabaseConfigBeans.class })
public class ExtractCdabase implements CommandLineRunner {

    /**
     * This class is intended to be a command-line callable java main program that creates (or truncates the tables in)
     * the intermediate cda database 'cda_base' and initialises it with ontologies, strains, genes, alleles, synonyms,
     * biological models, and phenotyped colony information read from reports.
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ExtractCdabase.class, args);
    }

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    @JobScope
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobRepository jobRepository;

    @Resource(name = "downloader")
    public List<Downloader> downloader;

    @Resource(name = "ontologyLoaderList")
    public List<OntologyLoader> ontologyLoaderList;

    @Autowired
    public AlleleLoader alleleLoader;

    @Autowired
    public BiologicalModelLoader bioModelLoader;

    @Autowired
    public MarkerLoader markerLoader;

    @Autowired
    public StrainLoader strainLoader;

    @Autowired
    public PhenotypedColonyLoader phenotypedColonyLoader;

    @Autowired
    private DataSource cdabase;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils();

    @Override
    public void run(String... args) throws Exception {
        runJobs();
    }

    public Job[] runJobs() throws DataImportException {

        // Populate Spring Batch tables if necessary.
        try {
            boolean exists = sqlUtils.columnInSchemaMysql(cdabase.getConnection(), "BATCH_JOB_INSTANCE", "JOB_INSTANCE_ID");
            if ( ! exists) {
                logger.info("Creating SPRING BATCH tables");
                sqlUtils.createSpringBatchTables(cdabase);
            }

        } catch (Exception e) {
            throw new DataImportException("Unable to create Spring Batch tables.");
        }

        Job[] jobs = new Job[] {
                  downloaderJob()
                , dbLoaderJob()
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

                throw new DataImportException(e);
            }
        }

        return jobs;
    }

    public Job downloaderJob() throws DataImportException {

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

        return jobBuilderFactory.get("downloaderJob")
                .incrementer(new RunIdIncrementer())
                .start(flowBuilder.build())
                .end()
                .build();
    }

    public Job dbLoaderJob() throws DataImportException {
        List<Flow> ontologyFlows = new ArrayList<>();

//        // Ontologies - synchronous flows.
//        List<Flow> synchronousFlows = new ArrayList<>();
//        for (int i = 0; i < ontologyLoaderList.size(); i++) {
//            OntologyLoader ontologyLoader = ontologyLoaderList.get(i);
//            synchronousFlows.add(new FlowBuilder<Flow>("ontology_" + ontologyLoader.getName() + "_synchronousFlow").from(ontologyLoader).end());
//        }
//        FlowBuilder<Flow> synchronousFlowBuilder = new FlowBuilder<Flow>("ontologyLoaderFlow").start(synchronousFlows.get(0));
//        for (int i = 1; i < synchronousFlows.size(); i++) {
//            synchronousFlowBuilder.next(synchronousFlows.get(i));
//        }
//        Flow ontologyFlow = synchronousFlowBuilder.build();

        // Ontologies - parallel flows.
        List<Flow> parallelFlows = new ArrayList<>();
        for (int i = 0; i < ontologyLoaderList.size(); i++) {
            OntologyLoader ontologyLoader = ontologyLoaderList.get(i);
            parallelFlows.add(new FlowBuilder<Flow>("ontology_" + ontologyLoader.getName() + "_parallelFlow").from(ontologyLoader).end());
        }
        FlowBuilder<Flow> parallelFlowBuilder = new FlowBuilder<Flow>("ontologyLoaderParallelFlows").start(parallelFlows.get(0));
        for (int i = 1; i < parallelFlows.size(); i++) {
            SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor(Executors.defaultThreadFactory());
            parallelFlowBuilder.split(executor).add(parallelFlows.get(i));
        }
        Flow ontologyFlow = parallelFlowBuilder.build();

        // Markers - Gene types and subtypes, marker lists, VEGA, Ensembl, EntrezGene, and cCDS models
        Flow markersFlow = new FlowBuilder<Flow>("markersFlow").from(markerLoader).end();

        // Alleles
        Flow allelesFlow = new FlowBuilder<Flow>("allelesFlow").from(alleleLoader).end();

        // Strains - mgi, imsr (the order is important)
        Flow strainsFlow = new FlowBuilder<Flow>("strainsFlow").from(strainLoader).end();

        // Biological Models
        Flow bioModelsFlow = new FlowBuilder<Flow>("bioModelsFlow").from(bioModelLoader).end();

        // phenotyped colonies
        Flow phenotypedColoniesFlow = new FlowBuilder<Flow>("phenotypedColoniesFlow").from(phenotypedColonyLoader).end();

        return jobBuilderFactory.get("dbLoaderJob")
                .incrementer(new RunIdIncrementer())
                .start(ontologyFlow)
                .next(markersFlow)
                .next(allelesFlow)
                .next(strainsFlow)
                .next(bioModelsFlow)
                .next(phenotypedColoniesFlow)
                .end()
                .build();
    }
}