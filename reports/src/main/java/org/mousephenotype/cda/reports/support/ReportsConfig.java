package org.mousephenotype.cda.reports.support;

import org.mousephenotype.cda.annotations.ComponentScanNonParticipant;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

/**
 * ReportType bean configuration
 */

@Configuration
@EnableAutoConfiguration
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = ComponentScanNonParticipant.class),
        basePackages = {
        "org.mousephenotype.cda.reports",
        "org.mousephenotype.cda.db",
        "org.mousephenotype.cda.solr",
        "org.mousephenotype.cda.utilities" })
@PropertySource("file:${user.home}/configfiles/${profile:dev}/application.properties")
public class ReportsConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
    public DataSource komp2DataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.ontodb")
    public DataSource ontodbDataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    @Primary
    @PersistenceContext(name = "komp2Context")
    public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(komp2DataSource())
                .packages("org.mousephenotype.cda.db")
                .persistenceUnit("komp2")
                .build();
    }

    @Bean(name = "sessionFactory")
    @Primary
    public HibernateJpaSessionFactoryBean sessionFactory(EntityManagerFactory emf) {
        HibernateJpaSessionFactoryBean factory = new HibernateJpaSessionFactoryBean();
        factory.setEntityManagerFactory(emf);
        return factory;
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.admintools")
    public DataSource admintoolsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public SexualDimorphismDAO sexualDimorphismDAO() {
        return new SexualDimorphismDAOImpl();
    }

    // Needed for ImportDccMissingReport

    @Bean(name = "dcc1")
       @ConfigurationProperties(prefix = "dcc1")
   	public DataSource dcc1() {
           DataSource ds = DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
   		return ds;
   	}

   	@Bean(name = "dcc2")
       @ConfigurationProperties(prefix = "dcc2")
   	public DataSource dcc2() {
           DataSource ds = DataSourceBuilder.create().build();
   		return ds;
   	}

   	@Bean(name = "jdbctemplate1")
   	public JdbcTemplate jdbctemplate1() {
   		return new JdbcTemplate(dcc1());
   	}

   	@Bean(name = "jdbctemplate2")
   	public JdbcTemplate jdbctemplate2() {
   		return new JdbcTemplate(dcc2());
   	}
}
