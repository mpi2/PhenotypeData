package org.mousephenotype.cda.loads.europubmed;

import org.mousephenotype.cda.annotations.ComponentScanNonParticipant;
import org.mousephenotype.cda.db.dao.GwasDAO;
import org.mousephenotype.cda.db.dao.ReferenceDAO;
import org.mousephenotype.cda.reports.support.ReportsConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Created by ckchen on 20/10/2017.
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"org.mousephenotype.cda.loads.europubmed"},
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ReportsConfig.class})})

//@PropertySource("file:${user.home}/configfiles/${profile:jenkins}/application.properties")
@PropertySource(value="file:${user.home}/configfiles/${profile:dev}/application.properties")
public class PhenotypeArchiveDatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
    public DataSource komp2DataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.admintools")
    public DataSource admintoolsDataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }


//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//        emf.setDataSource(komp2DataSource());
//        emf.setPackagesToScan(new String[]{"org.mousephenotype.cda.db.pojo", "org.mousephenotype.cda.db.entity"});
//
//        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        emf.setJpaVendorAdapter(vendorAdapter);
//        emf.setJpaProperties(buildHibernateProperties());
//
//        return emf;
//    }
//
//    protected Properties buildHibernateProperties() {
//        Properties hibernateProperties = new Properties();
//
//        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//        hibernateProperties.setProperty("hibernate.show_sql", "false");
//        hibernateProperties.setProperty("hibernate.use_sql_comments", "false");
//        hibernateProperties.setProperty("hibernate.format_sql", "false");
//        hibernateProperties.setProperty("hibernate.generate_statistics", "false");
//        hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");
//
//        return hibernateProperties;
//    }
//
//    @Bean
//    @Primary
//    @PersistenceContext(name = "komp2Context")
//    public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(komp2DataSource())
//                .packages("org.mousephenotype.cda.db")
//                .persistenceUnit("komp2")
//                .build();
//    }
//
//    @Bean(name = "sessionFactoryHibernate")
//    public LocalSessionFactoryBean sessionFactoryHibernate() {
//        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//        sessionFactory.setDataSource(komp2DataSource());
//        sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
//        return sessionFactory;
//    }



}
