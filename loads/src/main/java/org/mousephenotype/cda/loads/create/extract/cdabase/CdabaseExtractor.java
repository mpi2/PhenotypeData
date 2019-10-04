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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.common.CommandLineUtils;
import org.mousephenotype.cda.loads.create.extract.cdabase.steps.*;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.slf4j.Logger;
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
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Lazy;
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
 * Created by mrelac on 12/04/2016.
 */
@SpringBootApplication
public class CdabaseExtractor implements CommandLineRunner {

    private SqlUtils               sqlUtils     = new SqlUtils();
    private JobBuilderFactory      jobBuilderFactory;
    private StepBuilderFactory     stepBuilderFactory;
    private JobRepository          jobRepository;
    private List<Downloader>       downloaderList;
    private List<OntologyLoader>   ontologyLoaderList;
    private AlleleLoader           alleleLoader;
    private BiologicalModelLoader  bioModelLoader;
    private MarkerLoader           markerLoader;
    private StrainLoader           strainLoader;
    private PhenotypedColonyLoader phenotypedColonyLoader;
    private DataSource             cdabaseDataSource;
    private Logger                 logger       = LoggerFactory.getLogger(this.getClass());
    private boolean                skipDownload = false;


    @Inject
    @Lazy
    public CdabaseExtractor(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            JobRepository jobRepository,
            List<Downloader> downloaderList,
            List<OntologyLoader> ontologyLoaderList,
            AlleleLoader alleleLoader,
            BiologicalModelLoader bioModelLoader,
            MarkerLoader markerLoader,
            StrainLoader strainLoader,
            PhenotypedColonyLoader phenotypedColonyLoader,
            DataSource cdabaseDataSource

    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobRepository = jobRepository;
        this.downloaderList = downloaderList;
        this.ontologyLoaderList = ontologyLoaderList;
        this.alleleLoader = alleleLoader;
        this.bioModelLoader = bioModelLoader;
        this.markerLoader = markerLoader;
        this.strainLoader = strainLoader;
        this.phenotypedColonyLoader = phenotypedColonyLoader;
        this.cdabaseDataSource = cdabaseDataSource;
    }


    /**
     * This class is intended to be a command-line callable java main program that creates (or truncates the tables in)
     * the intermediate cda database 'cda_base' and initialises it with ontologies, strains, genes, alleles, synonyms,
     * biological models, and phenotyped colony information read from reports.
     */
    public static void main(String[] args) {

        new SpringApplicationBuilder(CdabaseExtractor.class)
                .web(WebApplicationType.NONE)
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .run(args);
    }


    @Override
    public void run(String... args) throws Exception {

        initialise(args);
        runJobs();
    }

    private void initialise(String[] args) throws DataLoadException {

        OptionParser parser = CommandLineUtils.getOptionParser();

        // parameter to indicate that the download step should be skipped.
        parser.accepts("skipDownload");

        OptionSet options = parser.parse(args);

        if (options.has("skipDownload")) {
            String message = "Skipping download step as requested.";
            logger.info(message);
            skipDownload = true;
        }
    }


    public Job[] runJobs() throws DataLoadException {

        // Populate Spring Batch tables if necessary.
        try {
            boolean exists = sqlUtils.columnInSchemaMysql(cdabaseDataSource.getConnection(), "BATCH_JOB_INSTANCE", "JOB_INSTANCE_ID");
            if ( ! exists) {
                sqlUtils.createSpringBatchTables(cdabaseDataSource);
            }

        } catch (Exception e) {
            throw new DataLoadException("Unable to create Spring Batch tables.");
        }

        ArrayList<Job> jobs = new ArrayList<>();
        if ( ! skipDownload) {
            jobs.add(downloaderJob());
        }

        jobs.add(dbLoaderJob());

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = dateFormat.format(new Date());

        int i = 0;
        for (Job job : jobs) {
            try {
                JobInstance instance = jobRepository.createJobInstance("flow_" + now + "_" + job.getName(), new JobParameters());
                JobExecution execution = jobRepository.createJobExecution(instance, new JobParameters(), "jobExec_" + now + "_" + job.getName());
                job.execute(execution);

            } catch (Exception e) {

                throw new DataLoadException(e);
            }
        }

        return jobs.toArray(new Job[0]);
    }

    public Job downloaderJob() throws DataLoadException {

        List<Flow> flows = new ArrayList<>();
        for (int i = 0; i < downloaderList.size(); i++) {
            Downloader downloader = this.downloaderList.get(i);
            flows.add(new FlowBuilder<Flow>("subflow_" + i).from(downloader.getStep(stepBuilderFactory)).end());
        }

        FlowBuilder<Flow> flowBuilder = new FlowBuilder<Flow>("splitflow").start(flows.get(0));

        for (int i = 1; i < downloaderList.size(); i++) {
            SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor(Executors.defaultThreadFactory());
            flowBuilder.split(executor).add(flows.get(i));
        }

        return jobBuilderFactory.get("downloaderJob")
                .incrementer(new RunIdIncrementer())
                .start(flowBuilder.build())
                .end()
                .build();
    }

    public Job dbLoaderJob() throws DataLoadException {
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