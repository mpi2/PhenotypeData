package org.mousephenotype.cda.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.db.dao.SecondaryProjectDAO;
import org.mousephenotype.cda.db.dao.SecondaryProjectDAOImpl;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrJ;
import org.mousephenotype.cda.solr.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
import javax.validation.constraints.NotNull;


@Configuration
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"})
public class TestConfigSolr {


	@NotNull
	@Value("${solr.host}")
	private String solrBaseUrl;


	//////////////
	// datasources
	//////////////

	@Value("${datasource.komp2.jdbc-url}")
	private String komp2Url;

	@NotNull
	@Value("${datasource.komp2.username}")
	private String username;

	@NotNull
	@Value("${datasource.komp2.password}")
	private String password;

	@Bean
	@Primary
	@ConfigurationProperties("datasource.komp2")
	public DataSource dataSource() {

		DataSource komp2DataSource = SqlUtils.getConfiguredDatasource(komp2Url, username, password);

		return komp2DataSource;
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
	public SecondaryProjectDAO secondaryProjectDAO() {
		return new SecondaryProjectDAOImpl();
	}


	///////////
	// SERVICES
	///////////

	@Bean
	public AdvancedSearchService advancedSearchService() {
		return new AdvancedSearchService(postQcService());
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
	public PostQcService postQcService() {
		return new PostQcService(genotypePhenotypeCore(), secondaryProjectDAO());
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
	public SolrClient solrClient() { return new HttpSolrClient.Builder(solrBaseUrl).build(); }

	@Bean
	public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }

	@Bean(name = "sessionFactoryHibernate")
	protected LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan("org.mousephenotype.cda.db");
		return sessionFactory;
	}

	@Bean(name = "komp2TxManager")
	@Primary
	protected PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager tm = new JpaTransactionManager();
		tm.setEntityManagerFactory(emf);
		tm.setDataSource(dataSource());
		return tm;
	}
}