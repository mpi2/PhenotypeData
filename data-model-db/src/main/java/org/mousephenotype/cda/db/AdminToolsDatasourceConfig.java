package org.mousephenotype.cda.db;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * AdminToolsDatasourceConfig holds the configuration for the admintools datasource
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan("org.mousephenotype.cda.db")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.mousephenotype.cda.db")
public class AdminToolsDatasourceConfig {

	@Bean
	@ConfigurationProperties(prefix = "datasource.admintools")
	public DataSource admintoolsDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean emf2(EntityManagerFactoryBuilder builder){
		return builder
			.dataSource(admintoolsDataSource())
			.packages("org.mousephenotype.cda.db")
			.persistenceUnit("admintools")
			.build();
	}

	@Bean(name = "admintoolsSessionFactory")
	public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
		HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
		factory.setEntityManagerFactory(emf);
		return factory;
	}
}
