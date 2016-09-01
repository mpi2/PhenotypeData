package org.mousephenotype.cda.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.PhenotypeCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
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
@ComponentScan(
	basePackages = {"org.mousephenotype.cda"},
	useDefaultFilters = false,
	includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ImpressService.class})
	})
public class SolrServerConfig {

	public static final int QUEUE_SIZE = 10000;
	public static final int THREAD_COUNT = 3;


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



	@Bean(name = "allele2Core")
	HttpSolrClient getAllele2Core() {
		return new HttpSolrClient(imitsSolrBaseUrl + "/allele2");
	}

	@Bean(name = "eucommCreProductsCore")
	HttpSolrClient getEucomCreToolsProduct() {
		return new HttpSolrClient(solrBaseUrl + "/eucommtoolscre_product");
	}

	@Bean(name = "productCore")
	HttpSolrClient getProductCore() {
		return new HttpSolrClient(solrBaseUrl + "/product");
	}

	@Bean(name = "eucommToolsCreAllele2Core")
	HttpSolrClient getEucommToolsCreAllele2() {
		return new HttpSolrClient(solrBaseUrl + "/eucommtoolscre_allele2");
	}



	// Read only solr servers

	//Phenodigm server for our Web Status currently only
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

	//Disease
	@Bean(name = "diseaseCore")
	HttpSolrClient getDiseaseCore() {
		return new HttpSolrClient(solrBaseUrl + "/disease");
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

	//MA
	@Bean(name = "maCore")
	HttpSolrClient getMaCore() {
		return new HttpSolrClient(solrBaseUrl + "/ma");
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

	@Bean(name = "phenotypeCenterService")
	PhenotypeCenterService phenotypeCenterService() {
		return new PhenotypeCenterService(solrBaseUrl + "/experiment", impressService);
	}

	@Bean(name = "preQcPhenotypeCenterService")
	PhenotypeCenterService preQcPhenotypeCenterService() {
		return new PhenotypeCenterService(solrBaseUrl + "/preqc", impressService);
	}


}
