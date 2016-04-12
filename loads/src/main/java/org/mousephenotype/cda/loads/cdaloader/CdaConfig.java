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

package org.mousephenotype.cda.loads.cdaloader;

import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan({"org.mousephenotype.cda"})
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
@EnableAutoConfiguration
/**
 * Created by mrelac on 12/04/2016.
 */
public class CdaConfig {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Value("${datasource.komp2.url}")
//    private String datasourceKomp2Url;
//
//    @Value("${phenodigm.solrserver}")
//    private String phenodigmSolrserver;
//
//    @Value("${solr.host}")
//    private String solrHost;
//
//    @Value("${baseUrl}")
//    private String baseUrl;
//
//    @Value("${internalSolrUrl}")
//    private String internalSolrUrl;

	@Bean
	@Primary
    @ConfigurationProperties(prefix = "datasource.komp2Loads")
	public DataSource komp2LoadsDataSource() {
        DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}

    @Bean
   	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
   		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
   		emf.setDataSource(komp2LoadsDataSource());
   		emf.setPackagesToScan(new String[]{"org.mousephenotype.cda.db.pojo", "org.mousephenotype.cda.db.dao"});

   		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
   		emf.setJpaVendorAdapter(vendorAdapter);
   		emf.setJpaProperties(buildHibernateProperties());

   		return emf;
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
	public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
		HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
		factory.setEntityManagerFactory(emf);
		return factory;
	}

	@Bean(name = "komp2TxManager")
    @Primary
	public PlatformTransactionManager txManager() {
		return new DataSourceTransactionManager(komp2LoadsDataSource());
	}

	@Bean
	@ConfigurationProperties(prefix = "datasource.admintools")
	public DataSource admintoolsDataSource() {
		return DataSourceBuilder.create().build();
	}









	@Bean
	public String impcXmlFormatSpecimenLoader() { return ""; }
}