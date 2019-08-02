package org.mousephenotype.cda.indexers;

import org.mousephenotype.cda.db.HibernateConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * @author jmason
 */
@Configuration
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"})
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@Import(HibernateConfig.class)
public class IndexersTestConfig {

// FIXME FIXME FIXME Clean this file up.



//    @Value("${owlpath}")
//    protected String owlpath;
//
//    @Value("${internal_solr_url}")
//    private String internalSolrUrl;
//
//
//    private GenesSecondaryProjectRepository genesSecondaryProjectRepository;
//    private OntologyTermRepository          ontologyTermRepository;
//    private ParameterRepository             parameterRepository;
    private ApplicationContext              applicationContext;
//
//
    @Inject
    public IndexersTestConfig(
//            @NotNull GenesSecondaryProjectRepository genesSecondaryProjectRepository,
//            @NotNull OntologyTermRepository ontologyTermRepository,
//            @NotNull ParameterRepository parameterRepository,
            @NotNull ApplicationContext applicationContext)
    {
//        this.genesSecondaryProjectRepository = genesSecondaryProjectRepository;
//        this.ontologyTermRepository = ontologyTermRepository;
//        this.parameterRepository = parameterRepository;
        this.applicationContext = applicationContext;
    }
//
//
//    //////////////
//    // datasources
//    //////////////
//
//    @Value("${datasource.komp2.jdbc-url}")
//    private String komp2Url;
//
//    @Value("${datasource.komp2.username}")
//    private String username;
//
//    @Value("${datasource.komp2.password}")
//    private String password;
//
//    @Bean
//    @Primary
//    public DataSource komp2DataSource() {
//
//        DataSource komp2DataSource = SqlUtils.getConfiguredDatasource(komp2Url, username, password);
//
//        return komp2DataSource;
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "datasource.uniprot")
//    public DataSource uniprotDataSource() {
//        return DataSourceBuilder.create().driverClassName("oracle.jdbc.driver.OracleDriver").build();
//    }
//
//
//    /////////////////////////
//    // Read only solr servers
//    /////////////////////////
//
//    // allele
//    @Bean(name = "alleleCore")
//    public HttpSolrClient alleleCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/allele").build();
//    }
//
//    // allele2
//    @Bean(name = "allele2Core")
//    public HttpSolrClient allele2Core() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/allele2").build();
//    }
//
//    // anatomy
//    @Bean(name = "anatomyCore")
//    HttpSolrClient anatomyCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/anatomy").build();
//    }
//
//    // autosuggest
//    @Bean(name = "autosuggestCore")
//    HttpSolrClient autosuggestCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/autosuggest").build();
//    }
//
//    // experiment
//    @Bean(name = "experimentCore")
//    HttpSolrClient experimentCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/experiment").build();
//    }
//
//    // gene
//    @Bean(name = "geneCore")
//    HttpSolrClient geneCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/gene").build();
//    }
//
//    // genotype-phenotype
//    @Bean(name = "genotypePhenotypeCore")
//    HttpSolrClient genotypePhenotypeCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/genotype-phenotype").build();
//    }
//
//    // images
//    @Bean(name = "sangerImagesCore")
//    HttpSolrClient imagesCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/images").build();
//    }
//
//    // impc_images
//    @Bean(name = "impcImagesCore")
//    HttpSolrClient impcImagesCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/impc_images").build();
//    }
//
//    // mgi-phenotype
//    @Bean(name = "mgiPhenotypeCore")
//    HttpSolrClient mgiPhenotypeCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/mgi-phenotype").build();
//    }
//
//    // mp
//    @Bean(name = "mpCore")
//    HttpSolrClient mpCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/mp").build(); }
//
//    // phenodigm
//    @Bean(name = "phenodigmCore")
//    public HttpSolrClient phenodigmCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/phenodigm").build();
//    }
//
//    // pipeline
//    @Bean(name = "pipelineCore")
//    HttpSolrClient pipelineCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/pipeline").build();
//    }
//
//    // product
//    @Bean(name = "productCore")
//    HttpSolrClient productCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/product").build(); }
//
//    // statistical-result
//    @Bean(name = "statisticalResultCore")
//    HttpSolrClient statisticalResultCore() {
//        return new HttpSolrClient.Builder(internalSolrUrl + "/statistical-result").build();
//    }
//
//
//    ///////////
//    // SERVICES
//    ///////////
//
//    @Bean
//    public ImpressService impressService() {
//        return new ImpressService(pipelineCore());
//    }
//
//    @Bean
//    public MpTermService mpTermService() {
//        return new MpTermService(ontologyTermRepository, parameterRepository);
//    }
//
//    @Bean
//    public GenotypePhenotypeService genotypePhenotypeService() {
//        return new GenotypePhenotypeService(impressService(), genotypePhenotypeCore(), genesSecondaryProjectRepository);
//    }
//
//
//    /////////////////////////////
//    // Required for indexer tests
//    /////////////////////////////
//
//    @Bean
//    public SolrClient solrClient() { return new HttpSolrClient.Builder(internalSolrUrl).build(); }
//
//    @Bean
//    public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }
//
////    @Bean(name = "sessionFactoryHibernate")
////    protected LocalSessionFactoryBean sessionFactory() {
////        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
////        sessionFactory.setDataSource(komp2DataSource());
////        sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
////        return sessionFactory;
////    }
////
////    @Bean(name = "komp2TxManager")
////    @Primary
////    protected PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
////        JpaTransactionManager tm = new JpaTransactionManager();
////        tm.setEntityManagerFactory(emf);
////        tm.setDataSource(komp2DataSource());
////        return tm;
////    }

    @Bean
    public IndexerManager indexerManager() {
        return new IndexerManager(applicationContext);
    }
//
//
//    ////////////////
//    // Miscellaneous
//    ////////////////
//
//    @Bean
//    public OntologyParserFactory ontologyParserFactory() {
//        return new OntologyParserFactory(komp2DataSource(), owlpath);
//    }
}