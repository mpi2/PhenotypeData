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

package org.mousephenotype.cda.loads;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan("org.mousephenotype.cda.loads")
@PropertySource(value="file:${user.home}/configfiles/${profile}/application.properties")
@PropertySource(value="file:${user.home}/configfiles/${profile}/cdaload.properties",
                ignoreResourceNotFound=true)
@EnableAutoConfiguration
/**
 * Created by mrelac on 26/07/2016.
 *
 * This configuration class is meant to encapsulate the hibernate configuration requirements for the loads module.
 * It gives spring batch the hibernate it so desparately desires, without cluttering the loads code, which does not use
 * hibernate.
 */
public class ConfigAppHibernate {

	// REQUIRED FOR HIBERNATE

	@Bean(name = "komp2Datasource")
	@Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
	public DataSource cdaload() {
        DataSource ds = DataSourceBuilder.create().build();

		return ds;
	}

	@Bean(name = "komp2TxManager")
    @Primary
	public PlatformTransactionManager txManager() {
		return new DataSourceTransactionManager(cdaload());
	}

    protected Properties buildHibernateProperties() {
   		Properties hibernateProperties = new Properties();

   		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
   		hibernateProperties.setProperty("hibernate.show_sql", "false");
   		hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
   		hibernateProperties.setProperty("hibernate.format_sql", "true");
   		hibernateProperties.setProperty("hibernate.generate_statistics", "false");

   		return hibernateProperties;
   	}

   	@Bean(name = "sessionFactory")
    @Qualifier("komp2Datasource")
   	@Primary
   	public SessionFactory sessionFactory(DataSource dataSource) {

   		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.scanPackages("org.mousephenotype.cda.loads.cdaloader");

   		return sessionBuilder.buildSessionFactory();
   	}

    @Bean
   	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
   		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
   		emf.setDataSource(cdaload());
//   		emf.setPackagesToScan(new String[]{"org.mousephenotype.cda.loads.cdaloader"});

   		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
   		emf.setJpaVendorAdapter(vendorAdapter);
   		emf.setJpaProperties(buildHibernateProperties());

   		return emf;
   	}
}