package org.mousephenotype.cda.indexers.configuration;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;
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
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = {"org.mousephenotype.cda.solr.service.*", "org.mousephenotype.cda.solr.generic.util.*"}),
	basePackages = {
	"org.mousephenotype.cda.indexers",
	"org.mousephenotype.cda.db",
	"org.mousephenotype.cda.solr",
	"org.mousephenotype.cda.utilities"}
)
@PropertySource("file:${user.home}/configfiles/${profile}/application.properties")
public class IndexerConfig {

    public static final int QUEUE_SIZE = 10000;
    public static final int THREAD_COUNT = 3;

    @NotNull
    @Value("${buildIndexesSolrUrl}")
    private String writeSolrBaseUrl;


    // Indexers for writing
    @Bean
    SolrServer observationIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/experiment", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrServer genotypePhenotypeIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/genotype-phenotype", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrServer statisticalResultsIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/statistical-result", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrServer preqcIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/preqc", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrServer alleleIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/allele", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrServer sangerImagesIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/images", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrServer impcImagesIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/impc_images", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrServer mpIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/mp", QUEUE_SIZE, THREAD_COUNT);
    }
    @Bean
    SolrServer anatomyIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/anatomy", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrServer pipelineIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/pipeline", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrServer geneIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/gene", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrServer diseaseIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/disease", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrServer autosuggestIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/autosuggest", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrServer mgiPhenotypeIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/mgi-phenotype", QUEUE_SIZE, THREAD_COUNT);
    }

    @Bean
    SolrServer gwasIndexing() {
        return new ConcurrentUpdateSolrServer(writeSolrBaseUrl + "/gwas", QUEUE_SIZE, THREAD_COUNT);
    }

    // database connections
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
    public DataSource komp2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.admintools")
    public DataSource admintoolsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.goapro")
    public DataSource goaproDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.ontodb")
    public DataSource ontodbDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.pfam")
    public DataSource pfamDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.uniprot")
    public DataSource uniprotDataSource() {
        return DataSourceBuilder.create().build();
    }



    // support beans for hibernate wiring
    protected Properties buildHibernateProperties() {
	    Properties hibernateProperties = new Properties();

	    hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
	    hibernateProperties.put("hibernate.hbm2ddl.import_files", "sql/test-data.sql");
	    hibernateProperties.setProperty("hibernate.show_sql", "false");
	    hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
	    hibernateProperties.setProperty("hibernate.format_sql", "true");
	    hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
	    hibernateProperties.setProperty("hibernate.generate_statistics", "false");
	    hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");

	    return hibernateProperties;
    }


	@Bean(name = "sessionFactory")
	@Primary
	public SessionFactory getSessionFactory() {

		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.dao");
		sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");

		return sessionBuilder.buildSessionFactory();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(komp2DataSource());
		em.setPackagesToScan("org.mousephenotype.cda.db.dao", "org.mousephenotype.cda.db.pojo");

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
