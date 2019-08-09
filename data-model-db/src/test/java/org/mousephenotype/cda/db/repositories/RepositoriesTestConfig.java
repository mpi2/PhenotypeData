/*******************************************************************************
 * Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 ******************************************************************************/

package org.mousephenotype.cda.db.repositories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * <p>
 * This will also populate the test database with the test data in the sql/test-data.sql file.
 */

@Deprecated
@Configuration
@ComponentScan(value = {"org.mousephenotype.cda.db"})
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@EnableTransactionManagement
public class RepositoriesTestConfig {

	// UtilitiesTest requires two separate database connections containing the same table (in structure and name).
	// When using only H2 or only HSQL, separate database connections actually seem to point to the same database;
	// thus the testQueryDiffTwoDiffs() test fails because it is expecting the result count to be different and it is not.
	// Using two separate database vendors insures the databases are different.
	EmbeddedDatabaseType dcc1Type = EmbeddedDatabaseType.H2;
	EmbeddedDatabaseType dcc2Type = EmbeddedDatabaseType.HSQL;


	@Primary
	@Bean(name = "komp2DataSource")
	public DataSource komp2DataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
			.ignoreFailedDrops(true)
			.setName("komp2test")
			.addScript("sql/h2/H2ReplaceDateDiff.sql")
			.build();
	}


	// dcc1 and dcc2 databases for testing SqlUtils.queryDiff()
	// dcc1
	@Bean(name = "dcc1DataSource")
	public DataSource dcc1DataSource() {
		return new EmbeddedDatabaseBuilder().setType(dcc1Type)
			.ignoreFailedDrops(true)
			.setName("dcc1")
			.addScript("sql/dcc1-test-data.sql")
			.build();
	}

	@Bean
	public NamedParameterJdbcTemplate jdbc1() {
		return new NamedParameterJdbcTemplate(dcc1DataSource());
	}



	// dcc2
	@Bean(name = "dcc2DataSource")
	public DataSource dcc2DataSource() {
		return new EmbeddedDatabaseBuilder().setType(dcc2Type)
			.ignoreFailedDrops(true)
			.setName("dcc2")
			.addScript("sql/dcc2-test-data.sql")
			.build();
	}

	@Bean
	public NamedParameterJdbcTemplate jdbc2() {
		return new NamedParameterJdbcTemplate(dcc2DataSource());
	}
}