package org.mousephenotype.cda.indexers.configuration;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
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

// If same key is found in both files, the second file's value will "win".
@PropertySources({
        @PropertySource(value = "file:${user.home}/configfiles/${profile:dev}/datarelease.properties", ignoreResourceNotFound=true),
        @PropertySource(value = "file:${user.home}/configfiles/${profile:dev}/application.properties", ignoreResourceNotFound=true)
})

public class IndexerConfig {

    public static final int QUEUE_SIZE = 10000;
    public static final int THREAD_COUNT = 3;

    @NotNull
    @Value("${buildIndexesSolrUrl}")
    private String writeSolrBaseUrl;

    @NotNull
    @Value("${solr.host}")
    private String solrBaseUrl;

    // Indexers for writing
    @Bean
    SolrClient experimentCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/experiment").build();
    }
    @Bean
    SolrClient genotypePhenotypeCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/genotype-phenotype").build();
    }
    @Bean
    SolrClient statisticalResultCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/statistical-result").build();
    }
    @Bean
    SolrClient preqcCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/preqc").build();
    }
    @Bean
    SolrClient alleleCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/allele").build();
    }
    @Bean
    SolrClient sangerImagesCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/images").build();
    }
    @Bean
    SolrClient impcImagesCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/impc_images").build();
    }
    @Bean
    SolrClient mpCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/mp").build();
    }
    @Bean
    SolrClient anatomyCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/anatomy").build();
    }

    @Bean
    SolrClient pipelineCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/pipeline").build();
    }

    @Bean
    SolrClient geneCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/gene").build();
    }

    @Bean
    SolrClient allele2Core() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/allele2").build();
    }

    @Bean
    SolrClient productCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/product").build();
    }

    // IMPC disease core retired and now points to externall phenodigm
    @Bean
    SolrClient phenodigmCore() {
        // readonly
        return new HttpSolrClient.Builder(solrBaseUrl + "/phenodigm").build();
    }

    @Bean
    SolrClient autosuggestCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/autosuggest").build();
    }

    @Bean
    SolrClient mgiPhenotypeCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/mgi-phenotype").build();
    }

    @Bean
    SolrClient gwasCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/gwas").build();
    }

	// database connections
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
