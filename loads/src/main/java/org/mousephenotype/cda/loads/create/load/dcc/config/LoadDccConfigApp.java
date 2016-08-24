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

package org.mousephenotype.cda.loads.create.load.dcc.config;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile}/datarelease.properties")
@EnableAutoConfiguration
/**
 * This configuration class holds configuration information shared by the data load create process.
 *
 * Created by mrelac on 18/08/2016.
 */
public class LoadDccConfigApp {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean(name = "cda")
    @ConfigurationProperties(prefix = "cda")
    public DataSource cda() {
        DataSource ds = DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();

        return ds;
    }

    @Bean(name = "jdbcCda")
    public NamedParameterJdbcTemplate jdbcCda() {
        return new NamedParameterJdbcTemplate(cda());
    }


    @Bean(name = "dcc")
 //   @ConfigurationProperties(prefix = "dcc")
    public DataSource dcc() {
        DataSource ds = DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();

        return ds;
    }

    @Bean(name = "jdbcDcc")
    public NamedParameterJdbcTemplate jdbcDcc() {
        return new NamedParameterJdbcTemplate(dcc());
    }


    @Bean(name = "komp2")
    @Primary
    @ConfigurationProperties(prefix = "komp2")
    public DataSource komp2DataSource() {
        DataSource ds = DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();

        return ds;
    }
}