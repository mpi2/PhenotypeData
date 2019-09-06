/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.phenotype.service;

import org.mousephenotype.cda.db.HibernateConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import uk.ac.ebi.phenotype.web.dao.StatisticsService;
import uk.ac.ebi.phenotype.web.dao.StatsClient;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@Import(HibernateConfig.class)
public class ServiceTestConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${statistics_url}")
    private String statisticsUrl;


    @Bean
    public DataSource komp2DataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .ignoreFailedDrops(true)
                .setName("test")
                .build();
    }

    @Bean
    public StatsClient statsClient() {
        return new StatsClient();
    }

    @Bean
    public StatisticsService statisticsService() {
        return new StatisticsService(statsClient());
    }
}