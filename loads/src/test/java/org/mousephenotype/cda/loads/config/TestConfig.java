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

package org.mousephenotype.cda.loads.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;


/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * <p>
 * This will also populate the test database with the test data in the sql/test-data.sql file.
 */

@Configuration
public class TestConfig {

	// dcc1 and dcc2 databases for testing SqlUtils.queryDiff()


	// dcc1
//	@Bean(name = "dcc1DataSource")
//	public DataSource dcc1DataSource() {
//		return new EmbeddedDatabaseBuilder()
//			.setType(EmbeddedDatabaseType.H2)
//			.ignoreFailedDrops(true)
//			.addScript("sql/dcc1-test-data.sql")
//			.build();
//	}


	@Bean(name = "dcc1DataSource")
	public DataSource dcc1DataSource() {
//		EmbeddedDatabase e = new EmbeddedDatabaseBuilder()
//					.setType(EmbeddedDatabaseType.HSQL)
//					.ignoreFailedDrops(true)
//					.addScript("sql/dcc1-test-data.sql")
//					.build();


//		BasicDataSource s = new BasicDataSource();
//		s.setDriverClassName("org.h2.Driver");
//		s.setUsername("sa");
//		s.setPassword("");
//		s.setUrl("jdbc:h2:testdb/~/dcc1");


		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.HSQL)
			.ignoreFailedDrops(true)
			.addScript("sql/dcc1-test-data.sql")
			.build();
	}



	@Bean
	public JdbcTemplate jdbc1() {
		return new JdbcTemplate(dcc1DataSource());
	}


	// dcc2
	@Bean(name = "DataSourceDcc2")
	public DataSource dcc2DataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL)
			.ignoreFailedDrops(true)
			.addScript("sql/dcc2-test-data.sql")
			.build();
	}


	@Bean
	public JdbcTemplate jdbc2() {
		return new JdbcTemplate(dcc2DataSource());
	}
}