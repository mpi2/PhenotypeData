package org.mousephenotype.cda.indexers.configuration;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.Properties;


/**
 * Created by ckc on 07/06/16.
 * Indexers bean configuration
 */

@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan(
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = {"org.mousephenotype.cda.solr.generic.util.*"}),
	basePackages = {
	"org.mousephenotype.cda.indexers",
	"org.mousephenotype.cda.db",
	"org.mousephenotype.cda.solr",
	"org.mousephenotype.cda.utilities"}
)
@PropertySource("file:${user.home}/configfiles/${profile:dev}/application.properties")
public class IndexerConfig {

    public static final int QUEUE_SIZE = 10000;
    public static final int THREAD_COUNT = 3;

    @NotNull
    @Value("${buildIndexesSolrUrl}")
    private String writeSolrBaseUrl;


    @Value("${datasource.komp2.url}")
    String riUrl;

    @Value("${datasource.komp2.username}")
    String username;

    @Value("${datasource.komp2.password}")
    String password;


    // Indexers for writing
    @Bean
    SolrClient experimentCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/experiment", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrClient genotypePhenotypeCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/genotype-phenotype", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrClient statisticalResultCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/statistical-result", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrClient preqcCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/preqc", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrClient alleleCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/allele", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrClient sangerImagesCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/images", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrClient impcImagesCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/impc_images", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrClient mpCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/mp", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrClient anatomyCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/anatomy", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrClient pipelineCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/pipeline", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrClient geneCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/gene", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrClient allele2Core() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/allele2", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrClient productCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/product", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrClient diseaseCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/disease", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrClient autosuggestCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/autosuggest", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrClient mgiPhenotypeCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/mgi-phenotype", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrClient gwasCore() {
        return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/gwas", QUEUE_SIZE, THREAD_COUNT);
    }

	@Bean
	SolrClient phenodigmCore() {
		return new ConcurrentUpdateSolrClient(writeSolrBaseUrl + "/phenodigm", QUEUE_SIZE, THREAD_COUNT);
	}

	// database connections
    @Bean
    @Primary
    public DataSource komp2DataSource() {

        DataSource ds = DataSourceBuilder
                .create()
                .url(riUrl)
                .username(username)
                .password(password)
                .type(BasicDataSource.class)
                .driverClassName("com.mysql.jdbc.Driver").build();

        ((BasicDataSource) ds).setInitialSize(4);
        ((BasicDataSource) ds).setTestOnBorrow(true);
        ((BasicDataSource) ds).setValidationQuery("SELECT 1");

        return ds;
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.admintools")
    public DataSource admintoolsDataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.goapro")
    public DataSource goaproDataSource() {
        return DataSourceBuilder.create().driverClassName("oracle.jdbc.driver.OracleDriver").build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.ontodb")
    public DataSource ontodbDataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.pfam")
    public DataSource pfamDataSource() {
        return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.uniprot")
    public DataSource uniprotDataSource() {
        return DataSourceBuilder.create().driverClassName("oracle.jdbc.driver.OracleDriver").build();
    }

	@Bean
	@ConfigurationProperties(prefix = "datasource.phenodigm")
	public DataSource phenodigmDataSource() {
		return DataSourceBuilder.create().driverClassName("com.mysql.jdbc.Driver").build();
	}



	// support beans for hibernate wiring
    protected Properties buildHibernateProperties() {
	    Properties hibernateProperties = new Properties();

	    hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
	    hibernateProperties.setProperty("hibernate.show_sql", "false");
	    hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
	    hibernateProperties.setProperty("hibernate.format_sql", "true");
	    hibernateProperties.setProperty("hibernate.generate_statistics", "false");
	    hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");

	    return hibernateProperties;
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
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(komp2DataSource());
		em.setPackagesToScan("org.mousephenotype.cda.db.entity", "org.mousephenotype.cda.db.pojo");

		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(buildHibernateProperties());

		return em;
	}

	@Bean
	public HibernateTransactionManager transactionManager(SessionFactory s) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(s);
		return txManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}


}
