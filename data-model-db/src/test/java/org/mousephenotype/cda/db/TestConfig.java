/*******************************************************************************
 *  Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.db;

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * <p>
 * This will also import the data specified in the sql/test-data.sql file.
 */

@Configuration
@ComponentScan("org.mousephenotype.cda.db")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.mousephenotype.cda.db.dao", entityManagerFactoryRef = "internalEntityManagerFactory", transactionManagerRef = "internalTransactionManager")
public class TestConfig {

	public static final String INTERNAL = "internal";


	@Bean(name = "komp2DataSource")
	@Primary
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("komp2test").build();
	}


	@Bean(name = "internalEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean internalEntityManagerFactory(EntityManagerFactoryBuilder builder) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "create");
		properties.put("hibernate.hbm2ddl.import_files", "sql/test-data.sql");

		return builder.dataSource(dataSource()).packages("org.mousephenotype.cda.db.pojo", "org.mousephenotype.cda.db.dao").persistenceUnit(INTERNAL).properties(properties).build();
	}


	@Bean(name = "internalTransactionManager")
	@Primary
	public PlatformTransactionManager internalTransactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setDataSource(dataSource());
		jpaTransactionManager.setPersistenceUnitName(INTERNAL);
		return jpaTransactionManager;
	}


	@Bean(name = "sessionFactory")
	@Primary
	public SessionFactory getSessionFactory(DataSource dataSource) {

		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);

		sessionBuilder.scanPackages("org.mousephenotype.cda.db.dao");
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

		return sessionBuilder.buildSessionFactory();
	}


	@Bean(name = "admintoolsDataSource")
	public DataSource admintoolsDataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName("admintoolstest").build();
	}

}
