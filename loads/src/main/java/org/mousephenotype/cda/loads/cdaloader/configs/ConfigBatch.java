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
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.support.ConcurrentExecutorAdapter;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;

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
//    @Qualifier("resourceFileDbItemWriter")
//    public ResourceFileDbItemWriter resourceFileDbItemWriter;

    @Autowired
    @Qualifier("resourceFileOntologyMa")
    public ResourceFile resourceFileOntologyMa;

    @Autowired
    @Qualifier("resourceFileOntologyMp")
    public ResourceFile resourceFileOntologyMp;

    @Autowired
    @Qualifier("recreateAndLoadDbTables")
    public RecreateAndLoadDbTables recreateAndLoadDbTables;

    @Autowired
    public Step loadMaStep;

    @Autowired
    public Step loadMpStep;



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


    /**
     *
     * NOTES:
     *  recreateAndLoadDbTables works fine.
     *  resourceFileOntologyMa.getDownloadStep() works fine on its own.
     *  resourceFileOntologyMp.getDownloadStep() works fine on its own.
     *    the two together cause an infinite loop downloading the Ma file.
     */
    @Bean
    public Job cdaDownloadJob() throws CdaLoaderException {
        return jobBuilderFactory.get("cdaDownloadJob")
                .incrementer(new RunIdIncrementer())
                .flow(recreateAndLoadDbTables.getStep())


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
//                .next(resourceFileOntologyMa.getLoadStep())
//                .next(resourceFileOntologyMp.getLoadStep())


                // THIS SET OF STATEMENTS IS INTENDED TO PARALLELIZE. MP GETS LOADED, BUT MA DOESN'T. THE PROCESS JUST HANGS.
//                .split(new ConcurrentTaskExecutor())
//                .add()
//                .next(resourceFileOntologyMa.getLoadStep())
//                .split(new ConcurrentTaskExecutor())
//                .add()
//                .next(resourceFileOntologyMp.getLoadStep())


                // THESE TWO STATEMENTS ENABLED LOAD MA AND MP TERMS AS EXPECTED.
                .next(loadMaStep)
                .next(loadMpStep)



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




}