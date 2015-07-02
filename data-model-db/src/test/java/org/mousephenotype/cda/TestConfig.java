package org.mousephenotype.cda;

import org.hibernate.SessionFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
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
 * Created by jmason on 29/06/2015.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan("org.mousephenotype.cda")
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "org.mousephenotype.cda.dao",
	entityManagerFactoryRef = "internalEntityManagerFactory",
	transactionManagerRef = "internalTransactionManager")
public class TestConfig {

	public static final String INTERNAL = "internal";

	@Bean(name = "komp2DataSource")
	@Primary
	public DataSource dataSource() {

		DataSource dataSource = new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
			.setName("komp2")
			.build();
//		DatabasePopulatorUtils.execute(createDatabasePopulator(), dataSource);

		return dataSource;
	}

//	private DatabasePopulator createDatabasePopulator() {
//		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
//		databasePopulator.setContinueOnError(true);
//		databasePopulator.addScript(new ClassPathResource("sql/test-data.sql"));
//		return databasePopulator;
//	}

	@Bean(name = "internalEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean internalEntityManagerFactory(EntityManagerFactoryBuilder builder) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "create");
		properties.put("hibernate.hbm2ddl.import_files", "sql/test-data.sql");

		return builder.dataSource(dataSource())
		              .packages("org.mousephenotype.cda.pojo", "org.mousephenotype.cda.dao")
		              .persistenceUnit(INTERNAL)
		              .properties(properties)
		              .build();
	}

	@Bean(name = "internalTransactionManager")
	@Primary
	public PlatformTransactionManager internalTransactionManager() {
		JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
		jpaTransactionManager.setDataSource(dataSource());
		jpaTransactionManager.setPersistenceUnitName(INTERNAL);
		return jpaTransactionManager;
	}

	@Bean(name = "admintoolsDataSource")
	public DataSource admintoolsDataSource() {
		return new EmbeddedDatabaseBuilder()
			.setType(EmbeddedDatabaseType.H2)
				.setName("admin_tools")
				//			.addScript("classpath:sql/schema.sql")
				//			.addScript("classpath:sql/test-data.sql")
			.build();
	}

	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource) {

		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);

		sessionBuilder.scanPackages("org.mousephenotype.cda.dao");
		sessionBuilder.scanPackages("org.mousephenotype.cda.pojo");

		return sessionBuilder.buildSessionFactory();
	}
}
