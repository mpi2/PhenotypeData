package org.mousephenotype.cda.loads.statistics.generate;

import org.springframework.context.annotation.Configuration;


/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * <p>
 * This will also import the data specified in the sql/test-data.sql file.
 */

@Configuration
//@EnableTransactionManagement
//@EnableAutoConfiguration
//@ComponentScan(value = "org.mousephenotype.cda",
//	excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = {"org.mousephenotype.cda.db.entity.*OntologyDAO"})
//)
//@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"}, multicoreSupport = true)
//@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class TestConfigGenerateDatasets {

//
//
//	@NotNull
//	@Value("${solr.host}")
//	private String solrBaseUrl;
//
//	@Autowired
//	ImpressService impressService;
//
//	@NotNull
//	@Value("${imits.solr.host}")
//	private String imitsSolrBaseUrl;
//
//
//	// Required for spring-data-solr repositories
//	@Bean
//	public SolrClient solrClient() { return new HttpSolrClient(solrBaseUrl); }
//
//	@Bean
//	public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }
//	// Required for spring-data-solr repositories
//
//
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
//
//
//
//	// Read only solr servers
//
//	//Phenodigm server for our Web Status currently only
//	@Bean(name = "phenodigmCore")
//	public HttpSolrClient getPhenodigmCore() {
//		return new HttpSolrClient(solrBaseUrl + "/phenodigm");
//	}
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
//	//Autosuggest
//	@Bean(name = "autosuggestCore")
//	HttpSolrClient getAutosuggestCore() {
//		return new HttpSolrClient(solrBaseUrl + "/autosuggest");
//	}
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
//	//GenotypePhenotype
//	@Bean(name = "genotypePhenotypeCore")
//	HttpSolrClient getGenotypePhenotypeCore() {
//		return new HttpSolrClient(solrBaseUrl + "/genotype-phenotype");
//	}
//
//	// Impc images core
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
//
//	//Pipeline
//	@Bean(name = "pipelineCore")
//	HttpSolrClient getPipelineCore() {
//		return new HttpSolrClient(solrBaseUrl + "/pipeline");
//	}
//
//	//Preqc
//	@Bean(name = "preqcCore")
//	HttpSolrClient getPreqcCore() {
//		return new HttpSolrClient(solrBaseUrl + "/preqc");
//	}
//
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
//
//	@Bean(name = "preQcPhenotypeCenterService")
//	PhenotypeCenterService preQcPhenotypeCenterService() {
//		return new PhenotypeCenterService(solrBaseUrl + "/preqc", impressService);
//	}
//
//
//	@Value("${datasource.komp2.url}")
//	String komp2Url;
//
//	@Value("${datasource.komp2.username}")
//	String komp2Username;
//
//	@Value("${datasource.komp2.password}")
//	String komp2Password;
//
//
//	@Bean
//	@Primary
//	public DataSource komp2DataSource() {
//		return SqlUtils.getConfiguredDatasource(komp2Url, komp2Username, komp2Password);
//	}
//
//	@Bean
//	@ConfigurationProperties(prefix = "datasource.admintools")
//	public DataSource admintoolsDataSource() {
//		return DataSourceBuilder.create().build();
//	}
//
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
//
//
//	// PRIVATE AND PROTECTED METHODS
//
//
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