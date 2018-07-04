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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Created by mrelac on 27/09/16.
 */
@Configuration
public class CdaSqlUtilsTestConfig {

	@Bean(name = "cdaSqlUtils")
 	public CdaSqlUtils cdaSqlUtils() {
     return new CdaSqlUtils(jdbcCdaBase());
 }

	@Bean(name = "cdaBaseDataSourceH2")
	@Primary
	public DataSource cdaBaseDataSourceH2() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
                                            .ignoreFailedDrops(true)
                                            .setName("cda_base_test")
                                            .build();
	}

	@Bean
	public NamedParameterJdbcTemplate jdbcCdaBase() {
		return new NamedParameterJdbcTemplate(cdaBaseDataSourceH2());
	}


	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryDcc1() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(cdaBaseDataSourceH2());
		em.setPackagesToScan("org.mousephenotype.cda.db.entity", "org.mousephenotype.cda.db.pojo");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
//		em.setJpaProperties(buildHibernatePropertiesDcc1());

		return em;
	}

//	protected Properties buildHibernatePropertiesDcc1() {
//		Properties hibernateProperties = new Properties();
//
//		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//		hibernateProperties.put("hibernate.hbm2ddl.import_files", "sql/CdaSqlUtilsTest.sql");
//		hibernateProperties.setProperty("hibernate.show_sql", "true");
//		hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
//		hibernateProperties.setProperty("hibernate.format_sql", "true");
//		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
//		hibernateProperties.setProperty("hibernate.generate_statistics", "false");
//		hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");
//
//		// This lets you split the ddl over multiple lines. H2 interprets newline as a statement terminator.
//		hibernateProperties.setProperty("hibernate.hbm2ddl.import_files_sql_extractor", "org.hibernate.tool.hbm2ddl.MultipleLinesSqlCommandExtractor");
//
//		return hibernateProperties;
//	}
}