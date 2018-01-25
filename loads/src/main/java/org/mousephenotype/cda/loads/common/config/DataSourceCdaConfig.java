/*******************************************************************************
 * Copyright © 2016 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.common.config;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.slf4j.LoggerFactory;
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
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;


@PropertySource(value="file:${user.home}/configfiles/${profile}/datarelease.properties")
@Configuration
@EnableAutoConfiguration(exclude = {
        JndiConnectionFactoryAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        JpaRepositoriesAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        Neo4jDataAutoConfiguration.class
        })
/**
 * This configuration class defines the Cda {@link DataSource}, {@link NamedParameterJdbcTemplate}, and
 * {@link CdaSqlUtils} for the cda database.
 *
 * Created by mrelac on 18/01/2018.
 */
public class DataSourceCdaConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${datasource.cda.url}")
    protected String cdaUrl;

    @Value("${datasource.cda.username}")
    protected String cdaUsername;

    @Value("${datasource.cda.password}")
    protected String cdaPassword;


    // cda database
    @Primary
    @Bean
    public DataSource cdaDataSource() {
        return SqlUtils.getConfiguredDatasource(cdaUrl, cdaUsername, cdaPassword);
    }

    @Bean
    public NamedParameterJdbcTemplate jdbcCda() {
        return new NamedParameterJdbcTemplate(cdaDataSource());
    }

    @Bean
    public CdaSqlUtils cdaSqlUtils() {
        return new CdaSqlUtils(jdbcCda());
    }
}