package uk.ac.ebi;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.solr.service.ImpressService;
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

//@Configuration
//@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"}, multicoreSupport = true)
//@ComponentScan(
//	basePackages = {"org.mousephenotype.cda"},
//	useDefaultFilters = false,
//	includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ImpressService.class})
//	})
public class SolrServerConfig {
//
//	public static final int QUEUE_SIZE = 10000;
//	public static final int THREAD_COUNT = 3;
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
//
//
//	// Read only solr servers
//
//    //Phenodigm2 server
//	@Bean(name = "phenodigmCore")
//	public HttpSolrClient getPhenodigmCore() {
//        //new core keeps old url address
//		return new HttpSolrClient(solrBaseUrl + "/phenodigm");
//	}
//
//	//Configuration
//	@Bean(name = "configurationCore")
//	public HttpSolrClient getConfigurationCore() {
//		return new HttpSolrClient(solrBaseUrl + "/configuration");
//	}
//
//
//
//	//Allele
//	@Bean(name = "alleleCore")
//	public HttpSolrClient getAlleleCore() {
//		return new HttpSolrClient(solrBaseUrl + "/allele");
//	}
//
//
//	//Autosuggest
//	@Bean(name = "autosuggestCore")
//	HttpSolrClient getAutosuggestCore() {
//		return new HttpSolrClient(solrBaseUrl + "/autosuggest");
//	}
//
//	//Gene
//	@Bean(name = "geneCore")
//	HttpSolrClient getGeneCore() {
//		return new HttpSolrClient(solrBaseUrl + "/gene");
//	}
//
//	//GenotypePhenotype
//    // TK: this core seems to be used only in the test packages - remove?
//	@Bean(name = "genotypePhenotypeCore")
//	HttpSolrClient getGenotypePhenotypeCore() {
//		return new HttpSolrClient(solrBaseUrl + "/genotype-phenotype");
//	}
//
//	//DELETEME
////	//GenotypePhenotype
////	@Bean(name = "genotypePhenotypeCore")
////	HttpSolrClient getGenotypePhenotypeCore() {
////		return new HttpSolrClient("http://ves-hx-d1:8090/mi/impc/beta/solr/genotype-phenotype");
////	}
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
}
