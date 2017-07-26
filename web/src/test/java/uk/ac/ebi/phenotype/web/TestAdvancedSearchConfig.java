package uk.ac.ebi.phenotype.web;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.solr.service.GrossPathService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.PhenotypeCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
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
 * TestConfig sets up the in memory database for supporting the database tests.
 * <p>
 * This will also import the data specified in the sql/test-data.sql file.
 */

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan(value = "org.mousephenotype.cda",
	excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = {"org.mousephenotype.cda.db.entity.*OntologyDAO"})
)
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"}, multicoreSupport = true)
public class TestAdvancedSearchConfig {



	@NotNull
	@Value("${solr.host}")
	private String solrBaseUrl;

	@Autowired
	ImpressService impressService;

	@NotNull
	@Value("${imits.solr.host}")
	private String imitsSolrBaseUrl;


	// Required for spring-data-solr repositories
	@Bean
	public SolrClient solrClient() { return new HttpSolrClient(solrBaseUrl); }

	@Bean
	public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }
	// Required for spring-data-solr repositories


//	@Bean(name = "allele2Core")
//	HttpSolrClient getAllele2Core() {
//
//		//return new HttpSolrClient("http://localhost:8086/solr-example/allele");
//		return new HttpSolrClient(solrBaseUrl + "/allele2");
//
//	}
//
//	@Bean(name = "productCore")
//	HttpSolrClient getProductCore() {
//
//		return new HttpSolrClient(imitsSolrBaseUrl + "/product");
//		//return new HttpSolrClient("http://localhost:8086/solr-example/product");
//
//	}



	// Read only solr servers

	//Phenodigm server for our Web Status currently only
	@Bean(name = "phenodigmCore")
	public HttpSolrClient getPhenodigmCore() {
		return new HttpSolrClient(solrBaseUrl + "/phenodigm");
	}
//
//	//Configuration
//	@Bean(name = "configurationCore")
//	public HttpSolrClient getConfigurationCore() {
//		return new HttpSolrClient(solrBaseUrl + "/configuration");
//	}
//
//	//Allele
//	@Bean(name = "alleleCore")
//	public HttpSolrClient getAlleleCore() {
//		return new HttpSolrClient(solrBaseUrl + "/allele");
//	}
//
	//Autosuggest
	@Bean(name = "autosuggestCore")
	HttpSolrClient getAutosuggestCore() {
		return new HttpSolrClient(solrBaseUrl + "/autosuggest");
	}
//
//	//Disease
//	@Bean(name = "diseaseCore")
//	HttpSolrClient getDiseaseCore() {
//		return new HttpSolrClient(solrBaseUrl + "/disease");
//	}
//
//	//Gene
//	@Bean(name = "geneCore")
//	HttpSolrClient getGeneCore() {
//		return new HttpSolrClient(solrBaseUrl + "/gene");
//	}
//
	//GenotypePhenotype
	@Bean(name = "genotypePhenotypeCore")
	HttpSolrClient getGenotypePhenotypeCore() {
		return new HttpSolrClient(solrBaseUrl + "/genotype-phenotype");
	}

	// Impc images core
//	@Bean(name = "impcImagesCore")
//	HttpSolrClient getImpcImagesCore() {
//		return new HttpSolrClient(solrBaseUrl + "/impc_images");
//	}
//
//	//SangerImages
//	@Bean(name = "sangerImagesCore")
//	HttpSolrClient getImagesCore() {
//		return new HttpSolrClient(solrBaseUrl + "/images");
//	}
//
//	//ANATOMY
//	@Bean(name = "anatomyCore")
//	HttpSolrClient getAnatomyCore() { return new HttpSolrClient(solrBaseUrl + "/anatomy");	}
//
//	//MP
//	@Bean(name = "mpCore")
//	HttpSolrClient getMpCore() { return new HttpSolrClient(solrBaseUrl + "/mp"); }
//
//	//EMAP
//	@Bean(name = "emapCore")
//	HttpSolrClient getEmapCore() {
//		return new HttpSolrClient(solrBaseUrl + "/emap");
//	}
//
//	@Bean(name = "experimentCore")
//	HttpSolrClient getExperimentCore() {
//		return new HttpSolrClient(solrBaseUrl + "/experiment");
//	}

	//Pipeline
	@Bean(name = "pipelineCore")
	HttpSolrClient getPipelineCore() {
		return new HttpSolrClient(solrBaseUrl + "/pipeline");
	}

	//Preqc
	@Bean(name = "preqcCore")
	HttpSolrClient getPreqcCore() {
		return new HttpSolrClient(solrBaseUrl + "/preqc");
	}

//	//StatisticalResult
//	@Bean(name = "statisticalResultCore")
//	HttpSolrClient getStatisticalResultCore() {
//		return new HttpSolrClient(solrBaseUrl + "/statistical-result");
//	}
//
//	@Bean(name = "phenotypeCenterService")
//	PhenotypeCenterService phenotypeCenterService() {
//		return new PhenotypeCenterService(solrBaseUrl + "/experiment", impressService);
//	}

	@Bean(name = "preQcPhenotypeCenterService")
	PhenotypeCenterService preQcPhenotypeCenterService() {
		return new PhenotypeCenterService(solrBaseUrl + "/preqc", impressService);
	}
	


//	@Bean
//	@Primary
//	@ConfigurationProperties(prefix = "datasource.komp2")
//	public DataSource komp2DataSource() {
//		return DataSourceBuilder.create().build();
//	}


//	@Bean
//	@ConfigurationProperties(prefix = "datasource.ontodb")
//	public DataSource ontodbDataSource() {
//		return DataSourceBuilder.create().build();
//	}
//
//	@Bean
//	public GrossPathService grossPathService() {
//		return new GrossPathService();
//	}
//
//	@Bean
//	public ObservationService observationService() {
//		return new ObservationService();
//	}


	// PRIVATE AND PROTECTED METHODS


//	@Bean
//	protected LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
//		emf.setDataSource(komp2DataSource());
//		emf.setPackagesToScan(new String[]{"org.mousephenotype.cda.db.pojo", "org.mousephenotype.cda.db.entity"});
//
//		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//		emf.setJpaVendorAdapter(vendorAdapter);
//		emf.setJpaProperties(buildHibernateProperties());
//
//		return emf;
//	}
//
//	private Properties buildHibernateProperties() {
//		Properties hibernateProperties = new Properties();
//
//		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
//		hibernateProperties.setProperty("hibernate.show_sql", "true");
//		hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
//		hibernateProperties.setProperty("hibernate.format_sql", "true");
//		hibernateProperties.setProperty("hibernate.generate_statistics", "false");
//		hibernateProperties.setProperty("hibernate.current_session_context_class","thread");
//
//		return hibernateProperties;
//	}
//
//	@Bean(name = "sessionFactoryHibernate")
//	protected LocalSessionFactoryBean sessionFactory() {
//		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
//		sessionFactory.setDataSource(komp2DataSource());
//		sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
//		return sessionFactory;
//	}
//
//	@Bean(name = "komp2TxManager")
//	@Primary
//	protected PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
//		JpaTransactionManager tm = new JpaTransactionManager();
//		tm.setEntityManagerFactory(emf);
//		tm.setDataSource(komp2DataSource());
//		return tm;
//	}
}