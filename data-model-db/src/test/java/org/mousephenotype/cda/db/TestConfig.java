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

package org.mousephenotype.cda.db;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.*;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;


/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * <p>
 * This will also populate the test database with the test data in the sql/test-data.sql file.
 */

@Configuration
@ComponentScan(value = {"org.mousephenotype.cda.db","org.mousephenotype.cda.neo4j"},
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = {"org.mousephenotype.cda.db.entity.*OntologyDAO"})
)
@EnableJpaRepositories
@EnableTransactionManagement
public class TestConfig {

	public static final String INTERNAL = "internal";


	@Bean(name = "komp2DataSource")
	@Primary
	public DataSource h2DataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
			.ignoreFailedDrops(true)
			.setName("komp2test")
			.build();
	}

	@Bean(name = "admintoolsDataSource")
	public DataSource admintoolsDataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
			.setName("admintoolstest")
			.build();
	}


	protected Properties buildHibernateProperties() {
		Properties hibernateProperties = new Properties();

		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		hibernateProperties.put("hibernate.hbm2ddl.import_files", "sql/test-data.sql");
		hibernateProperties.setProperty("hibernate.show_sql", "false");
		hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
		hibernateProperties.setProperty("hibernate.format_sql", "true");
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		hibernateProperties.setProperty("hibernate.generate_statistics", "false");
		hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");

		return hibernateProperties;
	}


	@Bean(name = "sessionFactoryHibernate")
	@Primary
	public SessionFactory getSessionFactory() {

		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(h2DataSource());
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

		return sessionBuilder.buildSessionFactory();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(h2DataSource());
		em.setPackagesToScan("org.mousephenotype.cda.db.entity", "org.mousephenotype.cda.db.pojo");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(buildHibernateProperties());

		return em;
	}

	@Bean
	public HibernateTransactionManager transactionManager(SessionFactory s) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(s);
		return txManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}


	// dcc1 and dcc2 databases for testing SqlUtils.queryDiff()


	// dcc1
	@Bean(name = "dcc1DataSource")
	public DataSource dcc1DataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
			.ignoreFailedDrops(true)
			.setName("dcc1test")
			.build();
	}

	protected Properties buildHibernatePropertiesDcc1() {
		Properties hibernateProperties = new Properties();

		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		hibernateProperties.put("hibernate.hbm2ddl.import_files", "sql/dcc1-test-data.sql");
		hibernateProperties.setProperty("hibernate.show_sql", "false");
		hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
		hibernateProperties.setProperty("hibernate.format_sql", "true");
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		hibernateProperties.setProperty("hibernate.generate_statistics", "false");
		hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");
		hibernateProperties.setProperty("hibernate.ejb.entitymanager_factory_name", "emfDcc1");

		return hibernateProperties;
	}


	@Bean(name = "sessionFactorydcc1")
	public SessionFactory getSessionFactoryDcc1() {

		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dcc1DataSource());
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

		return sessionBuilder.buildSessionFactory();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactoryDcc1() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dcc1DataSource());
		em.setPackagesToScan("org.mousephenotype.cda.db.entity", "org.mousephenotype.cda.db.pojo");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(buildHibernatePropertiesDcc1());

		return em;
	}

	@Bean
	public JdbcTemplate jdbc1() {
		return new JdbcTemplate(dcc1DataSource());
	}


	// dcc2
	@Bean(name = "DataSourceDcc2")
	public DataSource dcc2DataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2)
			.ignoreFailedDrops(true)
			.setName("dcc2test")
			.build();
	}

	protected Properties buildHibernatePropertiesdcc2() {
		Properties hibernateProperties = new Properties();

		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
		hibernateProperties.put("hibernate.hbm2ddl.import_files", "sql/dcc2-test-data.sql");
		hibernateProperties.setProperty("hibernate.show_sql", "false");
		hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
		hibernateProperties.setProperty("hibernate.format_sql", "true");
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		hibernateProperties.setProperty("hibernate.generate_statistics", "false");
		hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");
		hibernateProperties.setProperty("hibernate.ejb.entitymanager_factory_name", "emfDcc2");

		return hibernateProperties;
	}

	@Bean(name = "sessionFactorydcc2")
	public SessionFactory getSessionFactorydcc2() {

		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dcc2DataSource());
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

		return sessionBuilder.buildSessionFactory();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactorydcc2() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(dcc2DataSource());
		em.setPackagesToScan("org.mousephenotype.cda.db.entity", "org.mousephenotype.cda.db.pojo");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(buildHibernatePropertiesdcc2());

		return em;
	}

	@Bean
	public JdbcTemplate jdbc2() {
		return new JdbcTemplate(dcc2DataSource());
	}
}