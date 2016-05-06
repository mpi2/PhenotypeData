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

import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.loads.cdaloader.exceptions.CdaLoaderException;
import org.mousephenotype.cda.loads.cdaloader.steps.tasklets.RecreateAndLoadDbTables;
import org.mousephenotype.cda.loads.cdaloader.support.ResourceFile;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by mrelac on 12/04/2016.
 */
@Configuration
@EnableBatchProcessing
public class ConfigBatch {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

//    @Autowired
//    public StepBuilderFactory stepBuilderFactory;

//    @Autowired
//    public DataSource komp2Loads;
//
//    @Autowired
//    public SystemCommandTasklet downloadReports;

//    @Autowired
//    @Qualifier("oboReader")
//    public FlatFileItemReader<OntologyTerm> ontologyReader;

    @Autowired
    public ItemWriter<OntologyTerm> ontologyWriter;


//    @Autowired
//    @Qualifier("downloadResourceFiles")
//    public DownloadResourceFiles downloadResourceFiles;

//    @Autowired
//    @Qualifier("recreateAndLoadDbTables")
//    public SystemCommandTasklet recreateAndLoadDbTables;

    @Autowired
    @Qualifier("resourceFileOntologyMa")
    public ResourceFile resourceFileOntologyMa;

    @Autowired
    @Qualifier("resourceFileOntologyMp")
    public ResourceFile resourceFileOntologyMp;

    @Autowired
    @Qualifier("recreateAndLoadDbTables")
    public RecreateAndLoadDbTables recreateAndLoadDbTables;



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


//    @Bean
//    public Job cdaLoadJob() throws CdaLoaderException {
//        return jobBuilderFactory.get("cdaLoadJob")
//                .incrementer(new RunIdIncrementer())
////                .listener(listener())
//
//                .flow(resourceFileOntologyMa.getDownloadStep())
//                .next(resourceFileOntologyMa.getLoadStep())
////                .next(recreateAndLoadDbTablesStep())
////                .next(loadOntologyMaStep())
////                .flow(loadOntologyMaStep())
//                .end()
//                .build();
//    }

    @Bean
    public Job cdaLoadJob() throws CdaLoaderException {
        return jobBuilderFactory.get("cdaLoadJob")
                .incrementer(new RunIdIncrementer())
                .flow(resourceFileOntologyMp.getDownloadStep())
                .next(resourceFileOntologyMp.getLoadStep())
                .next(recreateAndLoadDbTables.getStep())
                .end()
                .build();
    }

//    public Step downloadResourceFilesStep() {
//        return stepBuilderFactory.get("downloadResourceFiles")
//                .tasklet(downloadResourceFiles)
//                .build();
//    }

//    public Step recreateAndLoadDbTablesStep() {
//        return stepBuilderFactory.get("recreateAndLoadDbTablesStep")
//                .tasklet(recreateAndLoadDbTables)
//                .build();
//    }

//    public Step loadOntologyMaStep() throws CdaLoaderException{
//
//        return stepBuilderFactory.get("loadOntologyMaStep")
//                .chunk(10)
//                .reader(resourceFileOntologyMa.getItemReader())
//                .writer(ontologyWriter)
//                .build();
//    }
}