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

package org.mousephenotype.cda.loads.common;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile}/application.properties")
@PropertySource(value="file:${user.home}/configfiles/${profile}/dcc.properties",
                ignoreResourceNotFound=true)
@EnableAutoConfiguration
/**
 * Created by mrelac on 12/04/2016.
 */
public class CommonConfigApp {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Bean(name = "dcc1")
	@Primary
    @ConfigurationProperties(prefix = "dcc1")
	public DataSource dcc1() {
        DataSource ds = DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
		return ds;
	}

	@Bean(name = "dcc2")
    @ConfigurationProperties(prefix = "dcc2")
	public DataSource dcc2() {
        DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}

	@Bean(name = "jdbctemplate1")
	public JdbcTemplate jdbctemplate1() {
		return new JdbcTemplate(dcc1());
	}

	@Bean(name = "jdbctemplate2")
	public JdbcTemplate jdbctemplate2() {
		return new JdbcTemplate(dcc2());
	}
}