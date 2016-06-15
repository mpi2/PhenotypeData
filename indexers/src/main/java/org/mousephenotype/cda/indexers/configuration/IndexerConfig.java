package org.mousephenotype.cda.indexers.configuration;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
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
@ComponentScan(basePackages = {
	"org.mousephenotype.cda.indexers",
	"org.mousephenotype.cda.db",
	"org.mousephenotype.cda.solr",
	"org.mousephenotype.cda.utilities"},
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = "org.mousephenotype.cda.solr.service.*"))
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
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(komp2DataSource());
        emf.setPackagesToScan("org.mousephenotype.cda.db.pojo");

        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        emf.setJpaVendorAdapter(vendorAdapter);
        emf.setJpaProperties(buildHibernateProperties());

        return emf;
    }

    protected Properties buildHibernateProperties() {
        Properties hibernateProperties = new Properties();

        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
        hibernateProperties.setProperty("hibernate.format_sql", "true");
        hibernateProperties.setProperty("hibernate.generate_statistics", "false");
        hibernateProperties.setProperty("hibernate.current_session_context_class","thread");

        return hibernateProperties;
    }


    @Bean(name = "sessionFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(komp2DataSource());
        sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
        return sessionFactory;
    }

    @Bean(name = "komp2TxManager")
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        tm.setDataSource(komp2DataSource());
        return tm;
    }



}
