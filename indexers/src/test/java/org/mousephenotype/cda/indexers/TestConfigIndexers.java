package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.mousephenotype.cda.db.dao.EmapOntologyDAO;
import org.mousephenotype.cda.db.dao.MaOntologyDAO;
import org.mousephenotype.cda.db.dao.MpOntologyDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.support.HttpSolrServerFactoryBean;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * @author jmason
 */

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan({"org.mousephenotype.cda", "org.mousephenotype.cda.indexers", "org.mousephenotype.cda.solr.service"})
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"}, multicoreSupport = true)
public class TestConfigIndexers {

	@Value("http:${solrUrl}")
	String solrBaseUrl;

	@Bean
	public SolrServer solrServer() throws Exception {
		System.out.println("SOLR SERVER: " + solrBaseUrl);
		HttpSolrServerFactoryBean f = new HttpSolrServerFactoryBean();
		f.setUrl(solrBaseUrl);
		f.afterPropertiesSet();
		return f.getSolrServer();
	}

	@Bean
	public SolrTemplate solrTemplate(SolrServer solrServer) throws Exception {
		return new SolrTemplate(solrServer());
	}

	@Bean
	@Primary
	@ConfigurationProperties(prefix = "datasource.komp2")
	public DataSource komp2DataSource() {
		DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}

	@Bean
	@ConfigurationProperties(prefix = "datasource.admintools")
	public DataSource admintoolsDataSource() {
		DataSource ds = DataSourceBuilder.create().build();
		return ds;
	}


	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(komp2DataSource());
		emf.setPackagesToScan(new String[]{"org.mousephenotype.cda.db.pojo", "org.mousephenotype.cda.db.dao"});

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
		hibernateProperties.setProperty("hibernate.current_session_context_class", "thread");

		return hibernateProperties;
	}

	@Bean
	@Primary
	@PersistenceContext(name = "komp2Context")
	public LocalContainerEntityManagerFactoryBean emf(EntityManagerFactoryBuilder builder) {
		return builder
			.dataSource(komp2DataSource())
			.packages("org.mousephenotype.cda.db")
			.persistenceUnit("komp2")
			.build();
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
	//	public PlatformTransactionManager txManager() {
	//		return new DataSourceTransactionManager(komp2DataSource());
	//	}
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager tm = new JpaTransactionManager();
		tm.setEntityManagerFactory(emf);
		tm.setDataSource(komp2DataSource());
		return tm;
	}


	@Bean(name = "alleleIndexing")
	SolrServer getalleleIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/allele", 10000, 5);
	}

	@Bean(name = "preqcIndexing")
	SolrServer getpreqcIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/preqc", 10000, 5);
	}

	@Bean(name = "observationIndexing")
	SolrServer getobservationIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/experiment", 10000, 5);
	}

	@Bean(name = "maIndexing")
	SolrServer getmaIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/ma", 10000, 5);
	}

	@Bean(name = "diseaseIndexing")
	SolrServer getdiseaseIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/disease", 10000, 5);
	}

	@Bean(name = "geneIndexing")
	SolrServer getgeneIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/gene", 10000, 5);
	}

	@Bean(name = "genotypePhenotypeIndexing")
	SolrServer getgenotypePhenotypeIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/genotype-phenotype", 10000, 5);
	}

	@Bean(name = "mgiPhenotypeIndexing")
	SolrServer getmgiPhenotypeIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/mgi-phenotype", 10000, 5);
	}

	@Bean(name = "pipelineIndexing")
	SolrServer getpipelineIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/pipeline", 10000, 5);
	}

	@Bean(name = "sangerImagesIndexing")
	SolrServer getsangerImagesIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/images", 10000, 5);
	}

	@Bean(name = "mpIndexing")
	SolrServer getmpIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/mp", 10000, 5);
	}

	@Bean(name = "emapIndexing")
	SolrServer getemapIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/emap", 10000, 5);
	}

	@Bean(name = "autosuggestIndexing")
	SolrServer getautosuggestIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/autosuggest", 10000, 5);
	}

	@Bean(name = "impcImagesIndexing")
	SolrServer getimpcImagesIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/impc_images", 10000, 5);
	}

	@Bean(name = "statisticalResultsIndexing")
	SolrServer getstatisticalResultsIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/statistical-result", 10000, 5);
	}

	@Bean(name = "gwasIndexing")
	SolrServer getgwasIndexing() {
		return new ConcurrentUpdateSolrServer(solrBaseUrl + "/gwas", 10000, 5);
	}


	@Bean(name = "mpOntologyService")
	MpOntologyDAO getMpOntologyDAO() throws SQLException {
		return new MpOntologyDAO();
	}

	@Bean(name = "maOntologyDAO")
	MaOntologyDAO getMaOntologyDAO() throws SQLException {
		return new MaOntologyDAO();
	}

	@Bean(name = "emapOntologyDAO")
	EmapOntologyDAO getapOntologyDAO() throws SQLException {
		return new EmapOntologyDAO();
	}


	@Bean(name = "globalConfiguration")
	public Map<String, String> globalConfiguration() {

		Map<String, String> gc = new HashMap<>();
		gc.put("baseUrl", "${baseUrl}");
		gc.put("drupalBaseUrl", "${drupalBaseUrl}");
		gc.put("solrUrl", "${solrUrl}");
		gc.put("internalSolrUrl", "${internalSolrUrl}");
		gc.put("mediaBaseUrl", "${mediaBaseUrl}");
		gc.put("impcMediaBaseUrl", "${impcMediaBaseUrl}");
		gc.put("pdfThumbnailUrl", "${pdfThumbnailUrl}");
		gc.put("googleAnalytics", "${googleAnalytics}");
		gc.put("liveSite", "${liveSite}");

		return gc;
	}

}
