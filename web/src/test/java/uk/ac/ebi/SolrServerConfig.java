package uk.ac.ebi;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.MpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Read only Solr server bean configuration The writable Solr servers are configured in IndexerConfig.java of the
 * indexer module
 */

@Configuration
//@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"})
//@ComponentScan(
//	basePackages = {"org.mousephenotype.cda"},
//	useDefaultFilters = false,
//	includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ImpressService.class})
//	})
public class SolrServerConfig {

	public static final int QUEUE_SIZE = 10000;
	public static final int THREAD_COUNT = 3;

	@Value("${solr.host}")
	private String solrBaseUrl;


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

//	// statistical-result
//	@Bean(name = "statisticalResultCore")
//	HttpSolrClient statisticalResultCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/statistical-result").build();
//	}



	///////////
	// SERVICES
	///////////

	@Bean
	public GeneService geneService() {
		return new GeneService(geneCore());
	}

	@Bean
	public ImpressService impressService() {
		return new ImpressService(pipelineCore());
	}

	@Bean
	public MpService mpService() {
		return new MpService(mpCore());
	}







//	@NotNull
//	@Autowired
//	ImpressService impressService;

//	@Value("${imits.solr.host}")
//	private String imitsSolrBaseUrl;
//
//
//	// Required for spring-data-solr repositories
//	@Bean
//	public SolrClient solrClient() { return new HttpSolrClient.Builder(solrBaseUrl).build(); }
//
//	@Bean
//	public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }
//	// Required for spring-data-solr repositories


//	@Bean(name = "allele2Core")
//	HttpSolrClient getAllele2Core() {
//
//		//return new HttpSolrClient("http://localhost:8086/solr-example/allele");
//		return new HttpSolrClient.Builder(solrBaseUrl + "/allele2").build();
//
//	}


//	@Bean(name = "productCore")
//	HttpSolrClient getProductCore() {
//
//		return new HttpSolrClient.Builder(imitsSolrBaseUrl + "/product").build();
//		//return new HttpSolrClient("http://localhost:8086/solr-example/product");
//
//	}





	// Read only solr servers

//    //Phenodigm2 server
//	@Bean(name = "phenodigmCore")
//	public HttpSolrClient getPhenodigmCore() {
//        //new core keeps old url address
//		return new HttpSolrClient.Builder(solrBaseUrl + "/phenodigm").build();
//	}
        
//	//Configuration
//	@Bean(name = "configurationCore")
//	public HttpSolrClient getConfigurationCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/configuration").build();
//	}



//	//Allele
//	@Bean(name = "alleleCore")
//	public HttpSolrClient getAlleleCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/allele").build();
//	}


//	//Autosuggest
//	@Bean(name = "autosuggestCore")
//	HttpSolrClient getAutosuggestCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/autosuggest").build();
//	}

//	//Gene
//	@Bean(name = "geneCore")
//	HttpSolrClient getGeneCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/gene").build();
//	}

//	//GenotypePhenotype
//    // TK: this core seems to be used only in the test packages - remove?
//	@Bean(name = "genotypePhenotypeCore")
//	HttpSolrClient getGenotypePhenotypeCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/genotype-phenotype").build();
//	}

	//DELETEME
//	//GenotypePhenotype
//	@Bean(name = "genotypePhenotypeCore")
//	HttpSolrClient getGenotypePhenotypeCore() {
//		return new HttpSolrClient("http://ves-hx-d1:8090/mi/impc/beta/solr/genotype-phenotype");
//	}

//	// Impc images core
//	@Bean(name = "impcImagesCore")
//	HttpSolrClient getImpcImagesCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/impc_images").build();
//	}
//
//	//SangerImages
//	@Bean(name = "sangerImagesCore")
//	HttpSolrClient getImagesCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/images").build();
//	}

//	//ANATOMY
//	@Bean(name = "anatomyCore")
//	HttpSolrClient getAnatomyCore() { return new HttpSolrClient.Builder(solrBaseUrl + "/anatomy").build();	}

//	//MP
//	@Bean(name = "mpCore")
//	HttpSolrClient getMpCore() { return new HttpSolrClient.Builder(solrBaseUrl + "/mp").build(); }

//	//EMAP
//	@Bean(name = "emapCore")
//	HttpSolrClient getEmapCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/emap").build();
//	}

//	@Bean(name = "experimentCore")
//	HttpSolrClient getExperimentCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/experiment").build();
//	}

//	//Pipeline
//	@Bean(name = "pipelineCore")
//	HttpSolrClient getPipelineCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/pipeline").build();
//	}

//	//Preqc
//	@Bean(name = "preqcCore")
//	HttpSolrClient getPreqcCore() {
//		return new HttpSolrClient.Builder(solrBaseUrl + "/preqc").build();
//	}
}