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
import org.mousephenotype.cda.loads.cdaloader.steps.DoNothingStep;
import org.mousephenotype.cda.loads.cdaloader.steps.itemreaders.OntologyItemReader;
import org.mousephenotype.cda.loads.cdaloader.steps.itemwriters.ResourceFileDbItemWriter;
import org.mousephenotype.cda.loads.cdaloader.steps.tasklets.RecreateAndLoadDbTables;
import org.mousephenotype.cda.loads.cdaloader.support.ResourceFileOntology;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

/**
 * Created by mrelac on 03/05/16.
 */
@Configuration
public class ConfigBeans {

    @NotNull
    @Value("${owlpath}")
    protected String owlpath;


//@Autowired
//public JobRepository jobRepository;
// THIS CAUSES NPE.
//    @Bean
//    public JobRepository jobRepository() throws CdaLoaderException {
//        try {
//
//            // Using MapJobRepositoryFactoryBean automatically rebuilds the BATCH tables if necessary.
//            JobRepository jobRepository = new MapJobRepositoryFactoryBean().getObject();
//
//
//            // Add a JobExecution to JobRepository.
//            jobExecution = jobRepository.createJobExecution("flow", new JobParameters());
//
//
//            return jobRepository;
//        } catch (Exception e) {
//
//            throw new CdaLoaderException(e);
//        }
//    }





     // THIS CAUSES "A job execution for this job is already running: JobInstance: id=2, version=0, Job=[flow]"
//    private JobExecution jobExecution;
//    @Bean
//    public JobExecution jobExecution() throws CdaLoaderException {
//        try {
////            if (jobExecution == null) {
//                jobExecution = jobRepository.createJobExecution("flow", new JobParameters());
////            }
//        } catch (Exception e) {
//
//            throw new CdaLoaderException(e);
//        }
//
//        return jobExecution;
//    }
//    private JobExecution jobExecution;
//    @Bean
//    public JobExecution jobExecution() throws CdaLoaderException {
////        JobExecution jobExecution;
//        try {
//            if (jobExecution == null) {
//                jobExecution = jobRepository().createJobExecution("flow", new JobParameters());
//            }
//        } catch (Exception e) {
//
//            throw new CdaLoaderException(e);
//        }
//
//        return jobExecution;
//    }



//    private JobRepository jobRepository;
//    private JobExecution jobExecution;
//
//    @PostConstruct
//    public void init() throws CdaLoaderException {
//        try {
//            jobRepository = new MapJobRepositoryFactoryBean().getObject();
//            jobExecution = jobRepository.createJobExecution("flow", new JobParameters());
//        } catch (Exception e) {//
//            throw new CdaLoaderException(e);
//        }
//    }






        @Autowired
        public StepBuilderFactory stepBuilderFactory;
    // THIS CAUSES java.lang.IllegalStateException: Already value [org.springframework.jdbc.datasource.ConnectionHolder@586737ff] for key [org.apache.commons.dbcp.BasicDataSource@14292d71] bound to thread [main]
//    @Autowired
//    public PlatformTransactionManager komp2TxManager;
//
//    @Bean
//    public StepBuilderFactory stepBuilderFactory() throws CdaLoaderException {
//        return new StepBuilderFactory(jobRepository, komp2TxManager);
//    }




//    @Bean
//    public JobBuilderFactory jobBuilderFactory() throws CdaLoaderException {
//        JobBuilderFactory jobBuilderFactory = new JobBuilderFactory(jobRepository);
//        jobBuilderFactory.get("cdaDownloadJob").incrementer(new RunIdIncrementer());
//        return jobBuilderFactory;
//    }

    @Bean(name = "recreateAndLoadDbTables")
    public RecreateAndLoadDbTables recreateAndLoadDbTables() {
        return new RecreateAndLoadDbTables();
    }

    @Bean(name = "ontologyMa")
//    @StepScope
    public ResourceFileOntology ontologyMa() throws CdaLoaderException {
        ResourceFileOntology resourceFileOntology = new ResourceFileOntology();
        String sourceUrl = "http://purl.obolibrary.org/obo/ma.owl";
        String filename = owlpath + "/ma.owl";
        int dbId = 8;
        String prefix = "MA";
        resourceFileOntology.initialise(sourceUrl, filename, dbId, prefix);

System.out.println("ontologyMa bean invocation");
        return resourceFileOntology;
    }

    @Bean(name = "ontologyMp")
//    @StepScope
    public ResourceFileOntology ontologyMp() throws CdaLoaderException {
        ResourceFileOntology resourceFileOntology = new ResourceFileOntology();
        String sourceUrl = "ftp://ftp.informatics.jax.org/pub/reports/mp.owl";
        String filename = owlpath + "/mp.owl";
        int dbId = 5;
        String prefix = "MP";
        resourceFileOntology.initialise(sourceUrl, filename, dbId, prefix);

System.out.println("ontologyMp bean invocation");
        return resourceFileOntology;
    }

    @Bean(name = "dbItemWriter")
    @StepScope
    public ResourceFileDbItemWriter dbItemWriter() {
        ResourceFileDbItemWriter writer = new ResourceFileDbItemWriter();

        return writer;
    }

    @Bean
    @StepScope
    public Step loadMaStep() throws CdaLoaderException {

        String filename = owlpath + "/ma.owl";
        int dbId = 8;
        String prefix = "MA";

        OntologyItemReader ontologyReader = new OntologyItemReader();
        ontologyReader.initialise(filename, dbId, prefix);

System.out.println("loadMaStep()");
        return stepBuilderFactory.get("loadMaStep")
                .chunk(1000)
                .reader(ontologyReader)
                .writer(dbItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public Step loadMpStep () throws CdaLoaderException {

        String filename = owlpath + "/mp.owl";
        int dbId = 5;
        String prefix = "MP";

        OntologyItemReader ontologyReader = new OntologyItemReader();
        ontologyReader.initialise(filename, dbId, prefix);

System.out.println("loadMpStep()");
        return stepBuilderFactory.get("loadMpStep")
                .chunk(1000)
                .reader(ontologyReader)
                .writer(dbItemWriter())
                .build();
    }

    @Bean
    public Step doNothingStep() {
        return new DoNothingStep();
    }
}