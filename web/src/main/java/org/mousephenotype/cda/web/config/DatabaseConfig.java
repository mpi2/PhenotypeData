package org.mousephenotype.cda.web.config;

import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernatePersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.cloud.service.relational.TomcatDbcpPooledDataSourceCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


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
public class DatabaseConfig {

	public static final String INTERNAL = "internal";

	@Value("${spring.datasource.driverClassName}")
	String databaseDriverClass;
	@Value("${spring.datasource.password}")
    String komp2Pass;
	@Value("${spring.datasource.url}")
    String komp2Url;
	@Value("${spring.datasource.username}")
    String komp2Username;
	@Value("${spring.jpa.database-platform}")
    String hibernateDialect;
	@Value("${spring.jpa.show-sql")
	String hibernateShowSql;
	private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "spring.jpa.database-platform";
    private static final String PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN = "entitymanager.packages.to.scan";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "spring.jpa.show-sql";
    @Resource
    private Environment env;
    
	@Bean(name = "komp2DataSource")
	@Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
 
        dataSource.setDriverClassName(databaseDriverClass);
        
        dataSource.setUrl(komp2Url);
        dataSource.setUsername(komp2Username);
        dataSource.setPassword(komp2Pass);
 
        return dataSource;
    }

//	private Properties hibProperties() {
//        Properties properties = new Properties();
//        properties.put(PROPERTY_NAME_HIBERNATE_DIALECT, hibernateDialect);
////        properties.put(PROPERTY_NAME_HIBERNATE_SHOW_SQL,hibernateShowSql);
//        return properties;
//    }
	
//	 @Bean
//	    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//	        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
//	        entityManagerFactoryBean.setDataSource(dataSource());
//	        entityManagerFactoryBean.persistenceUnit(HibernatePersistence.class);
//	        entityManagerFactoryBean.setPackagesToScan(env.getRequiredProperty(PROPERTY_NAME_ENTITYMANAGER_PACKAGES_TO_SCAN));
//	         
//	        entityManagerFactoryBean.setJpaProperties(hibProperties());
//	         
//	        return entityManagerFactoryBean;
//	    }
	@Bean(name = "internalEntityManagerFactory")
	@Primary
	public LocalContainerEntityManagerFactoryBean internalEntityManagerFactory(EntityManagerFactoryBuilder builder) {

		Map<String, Object> properties = new HashMap<>();
		properties.put("hibernate.hbm2ddl.auto", "read");

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

//	@Bean(name = "admintoolsDataSource")
//	public DataSource admintoolsDataSource() {
//		return new EmbeddedDatabaseBuilder()
//			.setType(EmbeddedDatabaseType.H2)
//				.setName("admin_tools")
//				//			.addScript("classpath:sql/schema.sql")
//				//			.addScript("classpath:sql/test-data.sql")
//			.build();
//	}

	@Bean(name = "sessionFactory")
	public SessionFactory getSessionFactory(DataSource dataSource) {

		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);

		sessionBuilder.scanPackages("org.mousephenotype.cda.dao");
		sessionBuilder.scanPackages("org.mousephenotype.cda.pojo");

		return sessionBuilder.buildSessionFactory();
	}
}
