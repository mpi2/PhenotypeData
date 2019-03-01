package org.mousephenotype.cda.reports.support;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.solr.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import javax.validation.constraints.NotNull;


/**
 * Read only Solr server bean configuration The writable Solr servers are configured in IndexerConfig.java of the
 * indexer module
 */

@Configuration
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"}, multicoreSupport = true)
public class SolrServerConfig {

	@NotNull
	@Value("${solr.host}")
	private String solrBaseUrl;

	@NotNull
	@Value("${imits.solr.host}")
	private String imitsSolrBaseUrl;

	@Bean
	public ImpressService impressService() {
		return new ImpressService();
	}

	// Required for spring-data-solr repositories
	@Bean
	public SolrClient solrClient() { return new HttpSolrClient(solrBaseUrl); }

	@Bean
	public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }
	// Required for spring-data-solr repositories


	@Bean(name = "allele2Core")
	HttpSolrClient getAllele2Core() {

		//return new HttpSolrClient("http://localhost:8086/solr-example/allele");
		return new HttpSolrClient(solrBaseUrl + "/allele2");

	}

	@Bean(name = "productCore")
	HttpSolrClient getProductCore() {

		return new HttpSolrClient(imitsSolrBaseUrl + "/product");
	}

	// Read only solr servers

	// IMPC disease core retired and now points to Phenodigm server
	@Bean(name = "phenodigmCore")
	public HttpSolrClient getPhenodigmCore() {
		return new HttpSolrClient(solrBaseUrl + "/phenodigm");
	}

	//Configuration
	@Bean(name = "configurationCore")
	public HttpSolrClient getConfigurationCore() {
		return new HttpSolrClient(solrBaseUrl + "/configuration");
	}


	//Allele
	@Bean(name = "alleleCore")
	public HttpSolrClient getAlleleCore() {
		return new HttpSolrClient(solrBaseUrl + "/allele");
	}


	//Autosuggest
	@Bean(name = "autosuggestCore")
	HttpSolrClient getAutosuggestCore() {
		return new HttpSolrClient(solrBaseUrl + "/autosuggest");
	}

	//Gene
	@Bean(name = "geneCore")
	HttpSolrClient getGeneCore() {
		return new HttpSolrClient(solrBaseUrl + "/gene");
	}

	//GenotypePhenotype
	@Bean(name = "genotypePhenotypeCore")
	HttpSolrClient getGenotypePhenotypeCore() {
		return new HttpSolrClient(solrBaseUrl + "/genotype-phenotype");
	}

	// Impc images core
	@Bean(name = "impcImagesCore")
	HttpSolrClient getImpcImagesCore() {
		return new HttpSolrClient(solrBaseUrl + "/impc_images");
	}

	//SangerImages
	@Bean(name = "sangerImagesCore")
	HttpSolrClient getImagesCore() {
		return new HttpSolrClient(solrBaseUrl + "/images");
	}

	//ANATOMY
	@Bean(name = "anatomyCore")
	HttpSolrClient getAnatomyCore() { return new HttpSolrClient(solrBaseUrl + "/anatomy");	}

	//MP
	@Bean(name = "mpCore")
	HttpSolrClient getMpCore() { return new HttpSolrClient(solrBaseUrl + "/mp"); }

	//EMAP
	@Bean(name = "emapCore")
	HttpSolrClient getEmapCore() {
		return new HttpSolrClient(solrBaseUrl + "/emap");
	}

	@Bean(name = "experimentCore")
	HttpSolrClient getExperimentCore() {
		return new HttpSolrClient(solrBaseUrl + "/experiment");
	}

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

	//StatisticalResult
	@Bean(name = "statisticalResultCore")
	HttpSolrClient getStatisticalResultCore() {
		return new HttpSolrClient(solrBaseUrl + "/statistical-result");
	}


	// Service Beans


	@Bean
	PhenotypeCenterService phenotypeCenterService() {
		return new PhenotypeCenterService(getExperimentCore());
	}

	@Bean
	PhenotypeCenterProcedureCompletenessService phenotypeCenterProcedureCompletenessService() {
		return new PhenotypeCenterProcedureCompletenessService(phenotypeCenterService(), impressService());
	}

	@Bean
	PhenotypeCenterAllService phenotypeCenterAllService() {
		return new PhenotypeCenterAllService(getStatisticalResultCore());
	}

	@Bean
	PhenotypeCenterProcedureCompletenessAllService phenotypeCenterProcedureCompletenessAllService() {
		return new PhenotypeCenterProcedureCompletenessAllService(phenotypeCenterAllService(), getStatisticalResultCore());
	}
}