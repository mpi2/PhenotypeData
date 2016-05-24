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

package org.mousephenotype.cda.loads.cdaloader.configs;

import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.steps.tasklets.RecreateAndLoadDbTables;
import org.mousephenotype.cda.loads.cdaloader.support.ResourceFile;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mrelac on 12/04/2016.
 */
@Configuration
@EnableBatchProcessing
public class ConfigBatch {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public JobRepository jobRepository;

    private CommonUtils commonUtils = new CommonUtils();

//    @Autowired
//    public JobExecution execution;

//    @Autowired
//    public DataSource komp2Loads;
//
//    @Autowired
//    public SystemCommandTasklet downloadReports;

//    @Autowired
//    @Qualifier("oboReader")
//    public FlatFileItemReader<OntologyTerm> ontologyReader;

//    @Autowired
//    public ItemWriter<OntologyTerm> ontologyWriter;


//    @Autowired
//    @Qualifier("downloadResourceFiles")
//    public DownloadResourceFiles downloadResourceFiles;

//    @Autowired
//    @Qualifier("recreateAndLoadDbTables")
//    public SystemCommandTasklet recreateAndLoadDbTables;

//    @Autowired
//    @Qualifier("dbItemWriter")
//    public ResourceFileDbItemWriter dbItemWriter;








    @Autowired
    @Qualifier("ontologyMa")
    public ResourceFile ontologyMa;

    @Autowired
    @Qualifier("ontologyMp")
    public ResourceFile ontologyMp;

    @Autowired
    @Qualifier("recreateAndLoadDbTables")
    public RecreateAndLoadDbTables recreateAndLoadDbTables;

    @Autowired
    public Step doNothingStep;

//    @Autowired
//    public Step loadMaStep;
//
//    @Autowired
//    public Step loadMpStep;







    // tag::readerwriterprocessor[]
//    @Bean
//    public FlatFileItemReader<Person> reader() {
//        FlatFileItemReader<Person> reader = new FlatFileItemReader<Person>();
//        reader.setResource(new ClassPathResource("sample-data.csv"));
//        reader.setLineMapper(new DefaultLineMapper<Person>() {{
//            setLineTokenizer(new DelimitedLineTokenizer() {{
//                setNames(new String[] { "firstName", "lastName" });
//            }});
//            setFieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {{
//                setTargetType(Person.class);
//            }});
//        }});
//        return reader;
//    }


    @Bean
    public Job[] runLoadJobs() throws CdaLoaderException {
        Job[] jobs = new Job[] { cdaDownloadJob2(), cdaDownloadJob()};
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String now = dateFormat.format(new Date());


        for (int i = 0; i < jobs.length; i++) {
            Job job = jobs[i];
            try {
                JobInstance instance = jobRepository.createJobInstance("flow_" + now + "_" + i, new JobParameters());
                JobExecution execution = jobRepository.createJobExecution(instance, new JobParameters(), "xxx_" + now + "_" + i);
                job.execute(execution);
            } catch (Exception e) {

                throw new CdaLoaderException(e);
            }
        }

        return jobs;
    }


    public Job cdaDownloadJob2() throws CdaLoaderException {
System.out.println("cdaDownloadJob2");
        return jobBuilderFactory.get("cdaDownloadJob")
                .incrementer(new RunIdIncrementer())
                .flow(recreateAndLoadDbTables.getStep())
                .next(doNothingStep)
                .end()
                .build();
    }


    /**
     *
     * NOTES:
     *  recreateAndLoadDbTables works fine.
     *  ontologyMa.getDownloadStep() works fine on its own.
     *  ontologyMp.getDownloadStep() works fine on its own.
     *    the two together cause an infinite loop downloading the Ma file.
     */

    public Job cdaDownloadJob() throws CdaLoaderException {
System.out.println("cdaDownloadJob");


        return jobBuilderFactory.get("cdaDownloadJob")
                .incrementer(new RunIdIncrementer())
                .flow(recreateAndLoadDbTables.getStep())
                .next(doNothingStep)
//                .end()
//                .start(downloadFlow())




//                .next(downloadJob(loadJob(doNothingStep)))
//                .flow(recreateAndLoadDbTables.getStep())
//.end()
//                .next(downloadJob(loadJob(doNothingStep)))
//                .next(loadJob(doNothingStep))
//                .on("*").to(ontologyMa.getDownloadStep())
////                .end()
//                .start(ontologyMp.getDownloadStep())
////                .end()
//
//                .start(ontologyMa.getLoadStep())
////                .end()
//                .start(ontologyMp.getLoadStep())
//                .end()



//                .chunk(10)
//                .reader(new ResourceFileOntology())
//                .writer(ontologyWriter)
//                .build();
//
//
//
//
                // Download the ontology owl files.

                // THESE TWO STATEMENTS ENABLED LOAD THE MA TERMS, BUT NEVER LOAD THE MP TERMS. THE PROCESS JUST HANGS.
//                .next(ontologyMa.getLoadStep())
//                .next(ontologyMp.getLoadStep())


                // THIS SET OF STATEMENTS IS INTENDED TO PARALLELIZE. MP GETS LOADED, BUT MA DOESN'T. THE PROCESS JUST HANGS.
//                .split(new ConcurrentTaskExecutor())
//                .add()
//                .next(ontologyMa.getLoadStep())
//                .split(new ConcurrentTaskExecutor())
//                .add()
//                .next(ontologyMp.getLoadStep())


                // THESE TWO STATEMENTS ENABLED LOAD MA AND MP TERMS AS EXPECTED.
//                .flow(loadMaStep)
//                .next(loadMpStep)



                // THIS SET OF STATEMENTS IS INTENDED TO PARALLELIZE. MA GETS LOADED, BUT MP DOESN'T. THE PROCESS JUST HANGS. SIMILAR TO ABOVE, EXCEPT IT'S MA THAT GETS LOADED.
//                .split(new ConcurrentTaskExecutor())
//                .add()
//                .next(loadMaStep)
//                .split(new ConcurrentTaskExecutor())
//                .add()
//                .next(loadMpStep)





                .end()
                .build();
    }


//    @Bean
//    public Job cdaRecreateAndLoadDbTablesJob() throws CdaLoaderException {
//        return jobBuilderFactory.get("cdaDownloadJob")
//                .incrementer(new RunIdIncrementer())
//                .flow(recreateAndLoadDbTables.getStep())
//
//                .end()
//                .build();
//    }
//
//
//    @Bean
//    public Job cdaLoadJob() throws CdaLoaderException {
//        return jobBuilderFactory.get("cdaLoadJob")
//                .incrementer(new RunIdIncrementer())
//                .flow(ontologyMa.getLoadStep())
//                .end()
//                .start(ontologyMp.getLoadStep())
//                .end()
//                .build();
//    }

//    @Bean
//    public Job cdaMasterJob() throws CdaLoaderException {
//        return jobBuilderFactory.get("cdaMasterJob")
//                .incrementer(new RunIdIncrementer())
//                .flow(cdaRecreateAndLoadDbTablesJob())
//
//
//                .flow(recreateAndLoadDbTables.getStep())
//
//
//                .next(ontologyMa.getDownloadStep())
//                .end()
//                .start(ontologyMp.getDownloadStep())
//                .end()
//                .start(ontologyMa.getLoadStep())
//                .end()
//                .start(ontologyMp.getLoadStep())
//                .end()
//                .build();
//    }

    public Step downloadJob(Step nextStep) throws CdaLoaderException {
        Flow flow = new FlowBuilder<Flow>("downloadSubflow")
                .from(ontologyMa.getDownloadStep())
                .from(ontologyMp.getDownloadStep())
                .end();

        SimpleJobBuilder builder = new JobBuilder("downloadFlow").repository(jobRepository).start(ontologyMa.getDownloadStep());
        builder.incrementer(new RunIdIncrementer());
        builder.split(new SimpleAsyncTaskExecutor()).add(flow).end();

        try {
            builder.preventRestart().build().execute(jobRepository.createJobExecution("flow", new JobParameters()));
        } catch (Exception e) {

            throw new CdaLoaderException(e);
        }

        return nextStep;
    }

//    public Flow downloadFlow() {
//        Flow flow = null;
//
//        return flow;
//    }

//    public Step loadJob(Step nextStep) throws CdaLoaderException {
//
//        Flow flow = new FlowBuilder<Flow>("loadSubflow")
//                .from(ontologyMa.getLoadStep())
//                .from(ontologyMp.getLoadStep())
//                .end();
//
//        SimpleJobBuilder builder = new JobBuilder("loadFlow").repository(jobRepository).start(ontologyMa.getLoadStep());
//        builder.incrementer(new RunIdIncrementer());
//        builder.split(new SimpleAsyncTaskExecutor()).add(flow).end();
//
//        builder.preventRestart().build().execute(execution);
//
//        return nextStep;
//    }
}