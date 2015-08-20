package org.mousephenotype.cda.db;

import org.mousephenotype.cda.annotations.ComponentScanNonParticipant;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Komp2DatasourceConfig holds the configuration for the komp2 datasource
 */

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.mousephenotype.cda.db", entityManagerFactoryRef = "emf")
@ComponentScanNonParticipant
public class Komp2DatasourceConfig {

//	@Bean
//	@Primary
//	@ConfigurationProperties(prefix = "datasource.komp2")
//	public DataSource komp2DataSource() {
//		return DataSourceBuilder.create().build();
//	}
//
//	@Bean
//	@Primary
//	@PersistenceContext(name="komp2Context")
//	public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder){
//		return builder
//			.dataSource(komp2DataSource())
//			.packages("org.mousephenotype.cda.db")
//			.persistenceUnit("komp2")
//			.build();
//	}
//
//	@Bean(name = "sessionFactory")
//	public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
//		HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
//		factory.setEntityManagerFactory(emf);
//		return factory;
//	}
//
//	@Bean(name = "komp2TxManager")
//	public PlatformTransactionManager txManager() {
//		return new DataSourceTransactionManager(komp2DataSource());
//	}
}
