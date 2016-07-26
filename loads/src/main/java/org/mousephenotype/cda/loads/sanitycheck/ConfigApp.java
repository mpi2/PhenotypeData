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

package org.mousephenotype.cda.loads.sanitycheck;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@ComponentScan("org.mousephenotype.cda.loads.sanitycheck")
@PropertySource(value="file:${user.home}/configfiles/${profile}/application.properties")
@PropertySource(value="file:${user.home}/configfiles/${profile}/cdaload.properties",
                ignoreResourceNotFound=true)
/**
 * Created by mrelac on 12/04/2016.
 */
public class ConfigApp {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Bean(name = "dccload1")
	@Primary
    @ConfigurationProperties(prefix = "datasource.dccloader1")
	public DataSource dccload1() {
        DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}

	@Bean(name = "dccload2")
    @ConfigurationProperties(prefix = "datasource.dccloader2")
	public DataSource dccload2() {
        DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}

	@Bean(name = "jdbctemplate1")
	public JdbcTemplate jdbctemplate1() {
		return new JdbcTemplate(dccload1());
	}

	@Bean(name = "jdbctemplate2")
	public JdbcTemplate jdbctemplate2() {
		return new JdbcTemplate(dccload2());
	}

	@Bean
	public DccLoaderValidator dccLoaderValidator() {
        return new DccLoaderValidator();
	}
}