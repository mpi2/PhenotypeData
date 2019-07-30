package org.mousephenotype.cda.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.HibernateConfig;
import org.mousephenotype.cda.db.repositories.GenesSecondaryProjectRepository;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrJ;
import org.mousephenotype.cda.solr.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.Properties;


@Configuration
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"})
//@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@ComponentScan(basePackageClasses = {HibernateConfig.class})
public class TestConfigSolr {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private GenesSecondaryProjectRepository genesSecondaryProjectRepository;

	public TestConfigSolr(@NotNull GenesSecondaryProjectRepository genesSecondaryProjectRepository) {
		this.genesSecondaryProjectRepository = genesSecondaryProjectRepository;
	}

	@Value("${internal_solr_url}")
	private String internalSolrUrl;


	//////////////
	// DATASOURCES
	//////////////

//	@Value("${datasource.komp2.jdbc-url}")
//	private String komp2Url;
//
//	@Value("${datasource.komp2.username}")
//	private String username;
//
//	@Value("${datasource.komp2.password}")
//	private String password;

//	@Bean
//	@Primary
//	public DataSource komp2DataSource() {
//
//		DataSource komp2DataSource = SqlUtils.getConfiguredDatasource(komp2Url, username, password);
//
//		return komp2DataSource;
//	}


	/////////////////////////
	// READ-ONLY SOLR SERVERS
	/////////////////////////

	// allele
	@Bean(name = "alleleCore")
	public HttpSolrClient alleleCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/allele").build();
	}

	// allele2
	@Bean(name = "allele2Core")
	public HttpSolrClient allele2Core() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/allele2").build();
	}

	// anatomy
	@Bean(name = "anatomyCore")
	HttpSolrClient anatomyCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/anatomy").build();
	}

	// autosuggest
	@Bean(name = "autosuggestCore")
	HttpSolrClient autosuggestCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/autosuggest").build();
	}

	// experiment
	@Bean(name = "experimentCore")
	HttpSolrClient experimentCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/experiment").build();
	}

	// gene
	@Bean(name = "geneCore")
	HttpSolrClient geneCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/gene").build();
	}

	// genotype-phenotype
	@Bean(name = "genotypePhenotypeCore")
	HttpSolrClient genotypePhenotypeCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/genotype-phenotype").build();
	}

	// images
	@Bean(name = "sangerImagesCore")
	HttpSolrClient imagesCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/images").build();
	}

	// impc_images
	@Bean(name = "impcImagesCore")
	HttpSolrClient impcImagesCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/impc_images").build();
	}

	// mgi-phenotype
	@Bean(name = "mgiPhenotypeCore")
	HttpSolrClient mgiPhenotypeCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/mgi-phenotype").build();
	}

	// mp
	@Bean(name = "mpCore")
	HttpSolrClient mpCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/mp").build(); }

	// phenodigm
	@Bean(name = "phenodigmCore")
	public HttpSolrClient phenodigmCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/phenodigm").build();
	}

	// pipeline
	@Bean(name = "pipelineCore")
	HttpSolrClient pipelineCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/pipeline").build();
	}

	// product
	@Bean(name = "productCore")
	HttpSolrClient productCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/product").build(); }

	// statistical-result
	@Bean(name = "statisticalResultCore")
	HttpSolrClient statisticalResultCore() {
		return new HttpSolrClient.Builder(internalSolrUrl + "/statistical-result").build();
	}


	///////////
	// SERVICES
	///////////

	@Bean
	public AdvancedSearchService advancedSearchService() {
		return new AdvancedSearchService(genotypePhenotypeService());
	}

	@Bean
	public AlleleService alleleService() {
		return new AlleleService(alleleCore());

	}
	@Bean
	public AnatomyService anatomyService() {
		return new AnatomyService(anatomyCore());
	}

	@Bean
	public ExpressionService expressionService() {
		return new ExpressionService(experimentCore(), impcImagesCore(), anatomyService(), impressService());
	}

	@Bean
	public GeneService geneService() {
		return new GeneService(geneCore());
	}

	@Bean
	public GrossPathService grossPathService() {
		return new GrossPathService(observationService(), imageService());
	}

	@Bean
	public ImageService imageService() {
		return new ImageService(impcImagesCore());
	}

	@Bean
	public ImpressService impressService() {
		return new ImpressService(pipelineCore());
	}

	@Bean
	public MpService mpService() {
		return new MpService(mpCore());
	}

	@Bean
	public ObservationService observationService() {
		return new ObservationService(experimentCore());
	}

	@Bean
	public OrderService orderService() {
		return new OrderService(allele2Core(), productCore());
	}

	@Bean
	public PhenodigmService phenodigmService() {
		return new PhenodigmService(phenodigmCore());
	}

	@Bean
	public GenotypePhenotypeService genotypePhenotypeService() {
		return new GenotypePhenotypeService(impressService(), genotypePhenotypeCore(), genesSecondaryProjectRepository);
	}


	////////////////
	// Miscellaneous
	////////////////

	@Bean
	public ImagesSolrJ imagesSolrJ() {
		return new ImagesSolrJ();
	}


	/////////////////////////////////////////////
	// Required for spring-data-solr repositories
	/////////////////////////////////////////////

	@Bean
	public SolrClient solrClient() { return new HttpSolrClient.Builder(internalSolrUrl).build(); }

	@Bean
	public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }


	// HIBERNATE


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
//	public HibernateTransactionManager transactionManager() {
//		HibernateTransactionManager txManager = new HibernateTransactionManager();
//		txManager.setSessionFactory(getSessionFactory());
//		return txManager;
//	}

//	@Bean
//	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
//		return new PersistenceExceptionTranslationPostProcessor();
//	}


	// PRIVATE METHODS


//	private Properties buildHibernateProperties() {
//		Properties hibernateProperties = new Properties();
//
//		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//		hibernateProperties.put("hibernate.hbm2ddl.import_files", "sql/test-data.sql");
//		hibernateProperties.setProperty("hibernate.show_sql", "false");
//		hibernateProperties.setProperty("hibernate.use_sql_comments", "true");
//		hibernateProperties.setProperty("hibernate.format_sql", "true");
//		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
//		hibernateProperties.setProperty("hibernate.generate_statistics", "false");
//		hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");
//
//		return hibernateProperties;
//	}
//
//	private SessionFactory getSessionFactory() {
//		LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(komp2DataSource());
//		sessionBuilder.scanPackages("org.mousephenotype.cda.db.entity");
//		sessionBuilder.scanPackages("org.mousephenotype.cda.db.pojo");
//
//		return sessionBuilder.buildSessionFactory();
//	}
}