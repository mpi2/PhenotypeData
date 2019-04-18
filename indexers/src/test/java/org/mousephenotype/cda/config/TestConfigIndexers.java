package org.mousephenotype.cda.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.db.dao.*;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * @author jmason
 */
@Configuration
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"})
public class TestConfigIndexers {


    @Value("${owlpath}")
    protected String owlpath;

    @Value("${solr.host}")
    private String solrBaseUrl;


    //////////////
    // datasources
    //////////////

    @Value("${datasource.komp2.jdbc-url}")
    private String komp2Url;

    @Value("${datasource.komp2.username}")
    private String username;

    @Value("${datasource.komp2.password}")
    private String password;

    @Bean
    @Primary
    @ConfigurationProperties("datasource.komp2")
    public DataSource komp2DataSource() {

        DataSource komp2DataSource = SqlUtils.getConfiguredDatasource(komp2Url, username, password);

        return komp2DataSource;
    }

    @Bean
    @ConfigurationProperties(prefix = "datasource.uniprot")
    public DataSource uniprotDataSource() {
        return DataSourceBuilder.create().driverClassName("oracle.jdbc.driver.OracleDriver").build();
    }


    /////////////////////////
    // Read only solr servers
    /////////////////////////

    // allele
    @Bean(name = "alleleCore")
    public HttpSolrClient alleleCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/allele").build();
    }

    // allele2
    @Bean(name = "allele2Core")
    public HttpSolrClient allele2Core() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/allele2").build();
    }

    // anatomy
    @Bean(name = "anatomyCore")
    HttpSolrClient anatomyCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/anatomy").build();
    }

    // autosuggest
    @Bean(name = "autosuggestCore")
    HttpSolrClient autosuggestCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/autosuggest").build();
    }

    // experiment
    @Bean(name = "experimentCore")
    HttpSolrClient experimentCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/experiment").build();
    }

    // gene
    @Bean(name = "geneCore")
    HttpSolrClient geneCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/gene").build();
    }

    // genotype-phenotype
    @Bean(name = "genotypePhenotypeCore")
    HttpSolrClient genotypePhenotypeCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/genotype-phenotype").build();
    }

    // images
    @Bean(name = "sangerImagesCore")
    HttpSolrClient imagesCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/images").build();
    }

    // impc_images
    @Bean(name = "impcImagesCore")
    HttpSolrClient impcImagesCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/impc_images").build();
    }

    // mgi-phenotype
    @Bean(name = "mgiPhenotypeCore")
    HttpSolrClient mgiPhenotypeCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/mgi-phenotype").build();
    }

    // mp
    @Bean(name = "mpCore")
    HttpSolrClient mpCore() { return new HttpSolrClient.Builder(solrBaseUrl + "/mp").build(); }

    // phenodigm
    @Bean(name = "phenodigmCore")
    public HttpSolrClient phenodigmCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/phenodigm").build();
    }

    // pipeline
    @Bean(name = "pipelineCore")
    HttpSolrClient pipelineCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/pipeline").build();
    }

    // product
    @Bean(name = "productCore")
    HttpSolrClient productCore() { return new HttpSolrClient.Builder(solrBaseUrl + "/product").build(); }

    // statistical-result
    @Bean(name = "statisticalResultCore")
    HttpSolrClient statisticalResultCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/statistical-result").build();
    }


    ///////
    // DAOs
    ///////

    @Bean
    public DatasourceDAO datasourceDAO() {
        return new DatasourceDAOImpl();
    }

    @Bean
    public OntologyTermDAO ontologyTermDAO() {
        return new OntologyTermDAOImpl();
    }

    @Bean
    public PhenotypePipelineDAO pipelineDAO() {
        return new PhenotypePipelineDAOImpl();
    }

    @Bean
    public SecondaryProjectDAO secondaryProjectDAO() {
        return new SecondaryProjectDAOImpl();
    }


    ///////////
    // SERVICES
    ///////////

    @Bean
    public ImpressService impressService() {
        return new ImpressService(pipelineCore());
    }

    @Bean
    public MpTermService mpTermService() {
        return new MpTermService(ontologyTermDAO(), pipelineDAO());
    }

    @Bean
    public PostQcService postqcService() {
        return new PostQcService(genotypePhenotypeCore(), secondaryProjectDAO());
    }


    /////////////////////////////
    // Required for indexer tests
    /////////////////////////////

    @Bean
    public SolrClient solrClient() { return new HttpSolrClient.Builder(solrBaseUrl).build(); }

    @Bean
    public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }

    @Bean(name = "sessionFactoryHibernate")
    protected LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(komp2DataSource());
        sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
        return sessionFactory;
    }

    @Bean(name = "komp2TxManager")
    @Primary
    protected PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        tm.setDataSource(komp2DataSource());
        return tm;
    }


    ////////////////
    // Miscellaneous
    ////////////////

    @Bean
    public OntologyParserFactory ontologyParserFactory() {
        return new OntologyParserFactory(komp2DataSource(), owlpath);
    }
}