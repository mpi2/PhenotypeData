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

package org.mousephenotype.cda.loads.create.load.config;

import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.common.config.DataSourcesConfigApp;
import org.mousephenotype.cda.loads.create.load.ImpressLoader;
import org.mousephenotype.cda.loads.create.load.steps.ImpressUpdater;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;

import javax.inject.Inject;
import javax.sql.DataSource;

/**
 * Created by mrelac on 03/09/16.
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
public class LoadConfigBeans extends DataSourcesConfigApp implements InitializingBean {

    private NamedParameterJdbcTemplate    jdbcCda;
    private NamedParameterJdbcTemplate    jdbcDcc;
    private NamedParameterJdbcTemplate    jdbcDccEurophenome;
    private StepBuilderFactory            stepBuilderFactory;


    @Value("${datasource.impress.url}")
    private String impressUrl;

    @Value("${datasource.impress.username}")
    private String impressUsername;

    @Value("${datasource.impress.password}")
    private String impressPassword;


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private JobRepository jobRepository;



    @Inject
    @Lazy
    public LoadConfigBeans(
            NamedParameterJdbcTemplate jdbcCda,
            NamedParameterJdbcTemplate jdbcDcc,
            NamedParameterJdbcTemplate jdbcDccEurophenome,
            StepBuilderFactory stepBuilderFactory
            
    ) {
        this.jdbcCda = jdbcCda;
        this.jdbcDcc = jdbcDcc;
        this.jdbcDccEurophenome = jdbcDccEurophenome;
        this.stepBuilderFactory = stepBuilderFactory;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.notNull(jdbcCda, "jdbcCda must not be null");
        Assert.notNull(jdbcDcc, "jdbcDcc must not be null");
        Assert.notNull(jdbcDccEurophenome, "jdbcDccEurophenome must not be null");
        Assert.notNull(stepBuilderFactory, "stepBuilderFactory must not be null");
    }

    // impress database
    @Bean
    public DataSource impressDataSource() {
        return getConfiguredDatasource(impressUrl, impressUsername, impressPassword);
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcImpress() {
        return new NamedParameterJdbcTemplate(impressDataSource());
    }

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcCda());
    }
    
    
    @Bean
    @Lazy
    public ImpressUpdater impressUpdater() {
        return new ImpressUpdater(jdbcImpress(), stepBuilderFactory, cdaSqlUtils());
    }
    @Bean
    @Lazy
    public ImpressLoader impressLoader() {
        return new ImpressLoader(jobBuilderFactory, jobRepository, impressUpdater(), cdaDataSource());
    }
}