package org.mousephenotype.cda.reports.support;

import org.hibernate.SessionFactory;
import org.mousephenotype.cda.annotations.ComponentScanNonParticipant;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
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
@PropertySource("file:${user.home}/configfiles/${profile:jenkins}/application.properties")
public class ReportsConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
    public DataSource komp2DataSource() {
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

    @Bean(name = "sessionFactoryHibernate")
	@Primary
	public SessionFactory getSessionFactory() {

		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

		return sessionBuilder.buildSessionFactory();
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

    
    
    // Needed for extract/load validation reports


    @Bean(name = "cdabasePrevious")
    @ConfigurationProperties(prefix = "datasource.cdabase.compare.previous")
   	public DataSource cdabasePrevious() {
           DataSource ds = DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
   		return ds;
   	}

   	@Bean(name = "cdabaseCurrent")
    @ConfigurationProperties(prefix = "datasource.cdabase.compare.current")
   	public DataSource cdabaseCurrent() {
           DataSource ds = DataSourceBuilder.create().build();
   		return ds;
   	}

   	@Bean(name = "jdbcCdabasePrevious")
   	public JdbcTemplate jdbcCdabasePrevious() {
   		return new JdbcTemplate(cdabasePrevious());
   	}

   	@Bean(name = "jdbcCdabaseCurrent")
   	public JdbcTemplate jdbcCdabaseCurrent() {
   		return new JdbcTemplate(cdabaseCurrent());
   	}
    


    @Bean(name = "cdaPrevious")
    @ConfigurationProperties(prefix = "datasource.cda.compare.previous")
   	public DataSource cdaPrevious() {
           DataSource ds = DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
   		return ds;
   	}

   	@Bean(name = "cdaCurrent")
    @ConfigurationProperties(prefix = "datasource.cda.compare.current")
   	public DataSource cdaCurrent() {
           DataSource ds = DataSourceBuilder.create().build();
   		return ds;
   	}

   	@Bean(name = "jdbcCdaPrevious")
   	public JdbcTemplate jdbcCdaPrevious() {
   		return new JdbcTemplate(cdaPrevious());
   	}

   	@Bean(name = "jdbcCdaCurrent")
   	public JdbcTemplate jdbcCdaCurrent() {
   		return new JdbcTemplate(cdaCurrent());
   	}

   	@Bean(name = "jdbcCda1")
	public NamedParameterJdbcTemplate jdbcCda1() {
    	return new NamedParameterJdbcTemplate(cdaPrevious());
	}

	@Bean(name = "jdbcCda2")
	public NamedParameterJdbcTemplate jdbcCda2() {
		return new NamedParameterJdbcTemplate(cdaCurrent());
}




    @Bean(name = "dccPrevious")
    @ConfigurationProperties(prefix = "datasource.dcc.compare.previous")
   	public DataSource dccPrevious() {
           DataSource ds = DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
   		return ds;
   	}

   	@Bean(name = "dccCurrent")
    @ConfigurationProperties(prefix = "datasource.dcc.compare.current")
   	public DataSource dccCurrent() {
           DataSource ds = DataSourceBuilder.create().build();
   		return ds;
   	}

   	@Bean(name = "jdbcDccPrevious")
   	public JdbcTemplate jdbcDccPrevious() {
   		return new JdbcTemplate(dccPrevious());
   	}

   	@Bean(name = "jdbcDccCurrent")
   	public JdbcTemplate jdbcDccCurrent() {
   		return new JdbcTemplate(dccCurrent());
   	}
}