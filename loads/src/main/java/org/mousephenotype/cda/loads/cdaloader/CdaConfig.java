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
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.SystemCommandTasklet;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

@Configuration
@ComponentScan({"org.mousephenotype.cda"})
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
@EnableAutoConfiguration
/**
 * Created by mrelac on 12/04/2016.
 */
public class CdaConfig {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    public final long DOWNLOAD_TASKLET_TIMEOUT = 10000;                                  // Timeout in milliseconds

	@Bean(name = "komp2Loads")
	@Primary
    @ConfigurationProperties(prefix = "datasource.komp2Loads")
	public DataSource komp2LoadsDataSource() {
        DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}

	@Bean
	@Primary
	@PersistenceContext(name="komp2Context")
	public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder){
		return builder
			.dataSource(komp2LoadsDataSource())
			.packages("org.mousephenotype.cda.db")
			.persistenceUnit("komp2")
			.build();
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

    @Bean(name = "downloadReportsTasklet")
    @StepScope
    public SystemCommandTasklet downloadReportsTasklet() {
        SystemCommandTasklet downloadReportsTasklet = new SystemCommandTasklet();
        downloadReportsTasklet.setCommand("touch xxx");
        downloadReportsTasklet.setTimeout(DOWNLOAD_TASKLET_TIMEOUT);
        downloadReportsTasklet.setWorkingDirectory("/Users/mrelac");

        return downloadReportsTasklet;
    }
}