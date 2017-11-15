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

import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.PhenotypedColony;
import org.mousephenotype.cda.loads.common.config.DataSourcesConfigApp;
import org.mousephenotype.cda.loads.create.extract.dcc.ExtractDccExperiments;
import org.mousephenotype.cda.loads.create.extract.dcc.ExtractDccSpecimens;
import org.mousephenotype.cda.loads.create.load.LoadFromDcc;
import org.mousephenotype.cda.loads.create.load.steps.SampleLoader;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JndiConnectionFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mrelac on 02/05/2017.
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration(exclude = {
        JndiConnectionFactoryAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        Neo4jDataAutoConfiguration.class
})
public class TestConfig extends DataSourcesConfigApp {

    private JobBuilderFactory jobBuilderFactory;
    private JobRepository jobRepository;
    private StepBuilderFactory stepBuilderFactory;
    private Map<String, Allele> allelesBySymbolMap = new HashMap<>();
    private Map<String, Integer> cdaOrganisation_idMap = new HashMap<>();
    private Map<String, PhenotypedColony> phenotypedColonyMap = new HashMap<>();

    @Lazy
    @Inject
    public TestConfig(
            JobBuilderFactory jobBuilderFactory,
            StepBuilderFactory stepBuilderFactory,
            JobRepository jobRepository

    ) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobRepository = jobRepository;
    }

    // cda_base database
    @Bean
    public DataSource cdabaseDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("cda_base_test")
                .addScripts("sql/h2/cdabase/schema.sql")
                .build();
    }


    // cda database
    @Bean
    public DataSource cdaDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("cda_test")
                .addScripts("sql/h2/cdabase/schema.sql", "sql/h2/dataIntegrationTest-data.sql")
                .build();
    }


    // dcc database
    @Bean
    public DataSource dccDataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("dcc_test")
                .addScripts("sql/h2/dcc/createSpecimen.sql", "sql/h2/dcc/createExperiment.sql")
                .build();
    }

    @Bean
    public ExtractDccSpecimens extractDccSpecimens() {
        return new ExtractDccSpecimens(dccDataSource(), dccSqlUtils());
    }

    @Bean
    public ExtractDccExperiments extractDccExperiments() {
        return new ExtractDccExperiments(dccDataSource(), dccSqlUtils());
    }

    @Bean
    public SampleLoader sampleLoaader() {

        SampleLoader sampleLoader = new SampleLoader(
                jdbcCda(),
                stepBuilderFactory,
                cdaSqlUtils(),
                dccSqlUtils(),
                allelesBySymbolMap,
                cdaOrganisation_idMap,
                phenotypedColonyMap);

        return sampleLoader;
    }

//    @Bean
//    public LoadFromDcc loadFromDcc() {
//        LoadFromDcc loadFromDcc = new LoadFromDcc(
//                jobBuilderFactory,
//                stepBuilderFactory,
//                jobRepository,
//                cdaDataSource(),
//                sampleLoaader(),
//                null /*experimentLoader*/,
//                null,
//                null
//        );
//
//        return loadFromDcc;
//    }
}