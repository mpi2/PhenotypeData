package org.mousephenotype.cda.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.solr.service.PhenotypeCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.support.HttpSolrServerFactoryBean;

import javax.validation.constraints.NotNull;


/**
 * Read only Solr server bean configuration
 * The writable Solr servers are configured in IndexerConfig.java of the indexer module
 */

@Configuration
@ComponentScan("org.mousephenotype.cda.solr")
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"}, multicoreSupport = true)
public class SolrServerConfig {

	public static final int QUEUE_SIZE = 10000;
	public static final int THREAD_COUNT = 3;



	@NotNull
	@Value("${solr.host}")
	private String solrBaseUrl;

	@Autowired
	PhenotypePipelineDAO ppDao;

	@NotNull
	@Value("${phenodigm.solrserver}")
	private String phenodigmSolrUrl;


	// Required for SolrCrudRepositories to work
	@Bean
	public SolrServer solrServer() throws Exception
	{
		HttpSolrServerFactoryBean f = new HttpSolrServerFactoryBean();
		f.setUrl(solrBaseUrl);
		f.afterPropertiesSet();
		return f.getSolrServer();
	}

	@Bean
	public SolrTemplate solrTemplate(SolrServer solrServer) throws Exception
	{
		return new SolrTemplate(solrServer());
	}


	// Read only solr servers

	//Phenodigm server for our Web Status currently only
	@Bean(name = "phenodigmCore")
	public HttpSolrServer getPhenodigmCore() { return new HttpSolrServer(phenodigmSolrUrl);	}

	//Allele
	@Bean(name = "alleleCore")
	public HttpSolrServer getAlleleCore() {
		return new HttpSolrServer(solrBaseUrl + "/allele");
	}


	//Autosuggest
	@Bean(name = "autosuggestCore")
	HttpSolrServer getAutosuggestCore() {
		return new HttpSolrServer(solrBaseUrl + "/autosuggest");
	}

	//Disease
	@Bean(name = "diseaseCore")
	HttpSolrServer getDiseaseCore() {
		return new HttpSolrServer(solrBaseUrl + "/disease");
	}

	//Gene
	@Bean(name = "geneCore")
	HttpSolrServer getGeneCore() {
		return new HttpSolrServer(solrBaseUrl + "/gene");
	}

	//GenotypePhenotype
	@Bean(name = "genotypePhenotypeCore")
	HttpSolrServer getGenotypePhenotypeCore() {
		return new HttpSolrServer(solrBaseUrl + "/genotype-phenotype");
	}

	// Impc images core
	@Bean(name = "impcImagesCore")
	//HttpSolrServer getImpcImagesCore() { return new HttpSolrServer(solrBaseUrl + "/impc_images"); }
	HttpSolrServer getImpcImagesCore() { return new HttpSolrServer("http://localhost:8090/solr"+ "/impc_images"); }

	//SangerImages
	@Bean(name = "sangerImagesCore")
	HttpSolrServer getImagesCore() {
		return new HttpSolrServer(solrBaseUrl + "/images");
	}

	//MA
	@Bean(name = "maCore")
	HttpSolrServer getMaCore() { return new HttpSolrServer(solrBaseUrl + "/ma"); }

	//ANATOMY
	@Bean(name = "anatomyCore")
	HttpSolrServer getAnatomyCore() { return new HttpSolrServer(solrBaseUrl + "/anatomy"); }

	//MP
	@Bean(name = "mpCore")
	HttpSolrServer getMpCore() {
		return new HttpSolrServer(solrBaseUrl + "/mp");
	}

	//EMAP
	@Bean(name = "emapCore")
//	HttpSolrServer getEmapCore() {
//		return new HttpSolrServer(solrBaseUrl + "/emap");
//	}
	HttpSolrServer getEmapCore() {
		return new HttpSolrServer("http://localhost:8090/solr" + "/emap");
	}

	@Bean(name = "experimentCore")
	HttpSolrServer getExperimentCore() {
		return new HttpSolrServer(solrBaseUrl + "/experiment");
	}

	//Pipeline
	@Bean(name = "pipelineCore")
	//HttpSolrServer getPipelineCore() {
	//	return new HttpSolrServer(solrBaseUrl + "/pipeline");
	//}
	HttpSolrServer getPipelineCore() {
		return new HttpSolrServer("http://localhost:8090/solr" + "/pipeline");
	}

	//Preqc
	@Bean(name = "preQcCore")
	HttpSolrServer getPreQcCore() {
		return new HttpSolrServer(solrBaseUrl + "/preqc");
	}

	//StatisticalResult
	@Bean(name = "statisticalResultCore")
	HttpSolrServer getStatisticalResultCore() {
		return new HttpSolrServer(solrBaseUrl + "/statistical-result");
	}

	@Bean(name = "phenotypeCenterService")
	PhenotypeCenterService phenotypeCenterService() {
		return new PhenotypeCenterService(solrBaseUrl + "/experiment", ppDao);
	}

	@Bean(name = "preQcPhenotypeCenterService")
	PhenotypeCenterService preQcPhenotypeCenterService() {
		return new PhenotypeCenterService(solrBaseUrl + "/preqc", ppDao);
	}


}
