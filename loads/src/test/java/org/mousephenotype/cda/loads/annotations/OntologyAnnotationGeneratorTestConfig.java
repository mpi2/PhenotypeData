/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.annotations;

import org.mousephenotype.cda.db.PrimaryDataSource;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@ComponentScan(basePackages = {"org.mousephenotype.cda.loads.annotations", "org.mousephenotype.cda.db"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
                        OntologyAnnotationGeneratorConfig.class,
                        PrimaryDataSource.class
                })})
@EnableAutoConfiguration
@EnableTransactionManagement
public class OntologyAnnotationGeneratorTestConfig {

    @Value("${datasource.komp2.jdbc-url}")
    protected String cdabaseUrl;

    @Value("${datasource.komp2.username}")
    protected String cdabaseUsername;

    @Value("${datasource.komp2.password}")
    protected String cdabasePassword;

    @Bean
    public DataSource komp2DataSource() {
        return SqlUtils.getConfiguredDatasource(cdabaseUrl, cdabaseUsername, cdabasePassword);
    }

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcCda());
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcCda() {
        return new NamedParameterJdbcTemplate(komp2DataSource());
    }
}