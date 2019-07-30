package org.mousephenotype.cda.indexers.configuration;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


@Configuration
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@EnableTransactionManagement
public class IndexerConfig {

    @Value("${buildIndexesSolrUrl}")
    private String writeSolrBaseUrl;

    @Value("${internal_solr_url}")
    private String internalSolrUrl;


    /////////////////////
    // read-only indexers
    /////////////////////

    // Creation of the IMPC disease core has been replaced by phenodigm core provided by QMUL
    @Bean
    SolrClient phenodigmCore() {
        // readonly
        return new HttpSolrClient.Builder(internalSolrUrl + "/phenodigm").build();
    }


    //////////////////////
    // read-write indexers
    //////////////////////
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

    @Bean
    SolrClient autosuggestCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/autosuggest").build();
    }

    @Bean
    SolrClient mgiPhenotypeCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/mgi-phenotype").build();
    }


    //////////////
    // datasources
    //////////////

    @Value("${datasource.komp2.jdbc-url}")
    private String komp2Url;

    @Value("${datasource.komp2.username}")
    private String username;

    @Value("${datasource.komp2.password}")
    private String password;


    //////////////
    // datasources
    //////////////

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "datasource.komp2")
    public DataSource komp2DataSource() {
        DataSource komp2DataSource = SqlUtils.getConfiguredDatasource(komp2Url, username, password);

        return komp2DataSource;
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.uniprot")
    public DataSource uniprotDataSource() {
        return DataSourceBuilder.create().driverClassName("oracle.jdbc.driver.OracleDriver").build();
    }


    /////////////////////////////////////
	// support beans for hibernate wiring
    /////////////////////////////////////

//    protected Properties buildHibernateProperties() {
//	    Properties hibernateProperties = new Properties();
//
//	    hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//	    hibernateProperties.setProperty("hibernate.show_sql", "false");
//	    hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
//	    hibernateProperties.setProperty("hibernate.format_sql", "true");
//	    hibernateProperties.setProperty("hibernate.generate_statistics", "false");
//	    hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");
//
//	    return hibernateProperties;
//    }
//
//    @Primary
//	@Bean(name = "sessionFactoryHibernate")
//	public SessionFactory getSessionFactory() {
//
//		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
//		sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
//		sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");
//
//		return sessionBuilder.buildSessionFactory();
//	}
//
//	@Bean
//	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//		em.setDataSource(komp2DataSource());
//		em.setPackagesToScan("org.mousephenotype.cda.db.entity", "org.mousephenotype.cda.db.pojo");
//
//		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//		em.setJpaVendorAdapter(vendorAdapter);
//		em.setJpaProperties(buildHibernateProperties());
//
//		return em;
//	}
//
//	@Bean
//	public HibernateTransactionManager transactionManager(SessionFactory s) {
//		HibernateTransactionManager txManager = new HibernateTransactionManager();
//		txManager.setSessionFactory(s);
//		return txManager;
//	}
//
//	@Bean
//	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
//		return new PersistenceExceptionTranslationPostProcessor();
//	}
}