package org.mousephenotype.cda.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.solr.service.PhenotypeCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import javax.validation.constraints.NotNull;


/**
 * Solr server bean configuration
 */

@Configuration
@ComponentScan("org.mousephenotype.cda.solr")
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"}, multicoreSupport = true)
public class SolrServerConfig {

	@NotNull
	@Value("${phenodigm.solrserver}")
	private String phenodigmSolrUrl;
	
	//Phenodigm server for our Web Status currently only
	@Bean(name = "phenodigmCore")
	public HttpSolrServer getPhenodigmCore() {
		return new HttpSolrServer(phenodigmSolrUrl);
	}

	@NotNull
	@Value("${solr.host}")
	private String solrBaseUrl;

	@Autowired
	PhenotypePipelineDAO ppDao;

	// PhenoDigm solr server configuration
	@Bean(name = "solrServer")
	HttpSolrServer getSolServerPhenodigm() {
		return new HttpSolrServer(phenodigmSolrUrl);
	}


	//Allele
	@Bean(name = "alleleCore")
	public HttpSolrServer getAlleleCore() {
		return new HttpSolrServer(solrBaseUrl + "/allele");
	}


	//Autosuggest
	@Bean(name = "autosuggestCore")
//	HttpSolrServer getAutosuggestCore() {
//		return new HttpSolrServer(solrBaseUrl + "/autosuggest");
//	}
	HttpSolrServer getAutosuggestCore() {
		return new HttpSolrServer("http:// localhost:8090/solr" + "/autosuggest");
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


	//ImpcImages
	@Bean(name = "impcImagesCore")
	HttpSolrServer getImpcImagesCore() {
		return new HttpSolrServer(solrBaseUrl + "/impc_images");
	}


	//MA
	@Bean(name = "maCore")
//	HttpSolrServer getMaCore() {
//		return new HttpSolrServer(solrBaseUrl + "/ma");
//	}
	HttpSolrServer getMaCore() {
		return new HttpSolrServer("http://localhost:8090/solr" + "/ma");
	}

	//ANATOMY
	@Bean(name = "anatomyCore")
//	HttpSolrServer getMaCore() {
//		return new HttpSolrServer(solrBaseUrl + "/anatomy");
//	}
	HttpSolrServer getAnatomyCore() {
		return new HttpSolrServer("http://localhost:8090/solr" + "/anatomy");
	}

	//MP
	@Bean(name = "mpCore")
	HttpSolrServer getMpCore() {
		return new HttpSolrServer(solrBaseUrl + "/mp");
	}

	//EMAP
	@Bean(name = "emapCore")
	HttpSolrServer getEmapCore() {
		return new HttpSolrServer(solrBaseUrl + "/emap");
	}
	

	//Observation
	@Bean(name = "experimentCore")
	HttpSolrServer getExperimentCore() {
		return new HttpSolrServer(solrBaseUrl + "/experiment");
	}


	//Pipeline
	@Bean(name = "pipelineCore")
	HttpSolrServer getPipelineCore() {
		return new HttpSolrServer(solrBaseUrl + "/pipeline");
	}


	//Preqc
	@Bean(name = "preQcCore")
	HttpSolrServer getPreQcCore() {
		return new HttpSolrServer(solrBaseUrl + "/preqc");
	}


	//SangerImages
	@Bean(name = "sangerImagesCore")
	HttpSolrServer getImagesCore() {
		return new HttpSolrServer(solrBaseUrl + "/images");
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
