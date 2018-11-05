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

package org.mousephenotype.cda.loads.integration.data.config;

import javafx.application.Application;
import org.mousephenotype.cda.loads.create.extract.cdabase.steps.PhenotypedColonyLoader;
import org.mousephenotype.cda.loads.create.extract.cdabase.steps.PhenotypedColonyProcessor;
import org.mousephenotype.cda.loads.create.extract.cdabase.steps.PhenotypedColonyWriter;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.test.context.ContextConfiguration;

import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * Configuration to test phenotyped colonies batch processor
 */
@Configuration
@ContextConfiguration(classes = {BatchLoaderTestConfig.class})
public class BatchPhenotypedColonyLoaderTestConfig {

    @Autowired
    ApplicationContext context;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;


    @Bean
    public PhenotypedColonyLoader phenotypedcolonyLoader() throws DataLoadException, MalformedURLException {

        return new PhenotypedColonyLoader(context.getResource("classpath:data/imits_phenotyped_colonies.csv"));
    }

    @Bean
    public Job getJob() throws DataLoadException, MalformedURLException {
        Flow phenotypedColoniesFlow = new FlowBuilder<Flow>("phenotypedColoniesFlow").from(phenotypedcolonyLoader()).end();

        final Job dbLoaderJob = jobBuilderFactory.get("dbLoaderJob")
                .incrementer(new RunIdIncrementer())
                .start(phenotypedColoniesFlow)
                .end()
                .build();

        return dbLoaderJob;
    }


    @Bean
    public PhenotypedColonyProcessor phenotypedColonyProcessor() throws DataLoadException {
        return new PhenotypedColonyProcessor(new HashMap<>());
    }

    @Bean
    public PhenotypedColonyWriter phenotypedColonyWriter() {
        return new PhenotypedColonyWriter();
    }

}