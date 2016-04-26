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

package org.mousephenotype.cda.loads.cdaloader;

import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

//    @Autowired
//    public DataSource komp2Loads;

    @Autowired
    public SystemCommandTasklet downloadReports;

    @Autowired
    public FlatFileItemReader ontologyReader;

    @Autowired
    public FlatFileItemWriter ontologyWriter;

    @Autowired
    public SystemCommandTasklet recreateAndLoadTables;

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
    public PersonItemProcessor processor() {
        return new PersonItemProcessor();
    }

//    @Bean
//    public JdbcBatchItemWriter<Person> writer() {
//        JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<Person>();
//        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Person>());
//        writer.setSql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)");
//        writer.setDataSource(komp2Loads);
//        return writer;
//    }
    // end::readerwriterprocessor[]

    // tag::listener[]

//    @Bean
//    public JobExecutionListener listener() {
//        return new JobCompletionNotificationListener(new JdbcTemplate(komp2Loads));
//    }

    // end::listener[]

    // tag::jobstep[]
//    @Bean
//    public Job cdaLoadJob() {
//        return jobBuilderFactory.get("cdaLoadJob")
//                .incrementer(new RunIdIncrementer())
////                .listener(listener())
//                .flow(step1())
//                .end()
//                .build();
//    }

    @Bean
    public Job cdaLoadJob() {
        return jobBuilderFactory.get("cdaLoadJob")
                .incrementer(new RunIdIncrementer())
//                .listener(listener())
                .flow(step1())
                .next(step2())
                .next(step3())
                .end()
                .build();
    }

//    @Bean
//    public Step step1() {
//        return stepBuilderFactory.get("step1")
//                .<Person, Person> chunk(10)
//                .reader(reader())
//                .processor(processor())
//                .writer(writer())
//                .build();
//    }
    // end::jobstep[]

//    @Bean
//    public Job anotherJob() {
//        return jobBuilderFactory.get("recreateDbJob")
//                .incrementer(new RunIdIncrementer())
//                .flow(step2())
//                .end()
//                .build();
//    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(downloadReports)
                .build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(recreateAndLoadTables)
                .build();
    }

        @Bean
        public Step step3() {
            return stepBuilderFactory.get("step3")
                    .chunk(10)
                    .reader(ontologyReader)
                    .writer(ontologyWriter)
                    .build();
        }
}