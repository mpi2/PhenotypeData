package uk.ac.ebi.phenotype;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.PhenotypeCenterProcedureCompletenessService;
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
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"})
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
    public SolrClient solrClient() { return new HttpSolrClient.Builder(solrBaseUrl).build(); }

    @Bean
    public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }
    // Required for spring-data-solr repositories


    @Bean(name = "allele2Core")
    HttpSolrClient getAllele2Core() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/allele2").build();
    }

    @Bean(name = "productCore")
    HttpSolrClient getProductCore() {
        return new HttpSolrClient.Builder(imitsSolrBaseUrl + "/product").build();
    }


    // Read only solr servers

    //Phenodigm2 server
    @Bean(name = "phenodigmCore")
    public HttpSolrClient getPhenodigmCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/phenodigm").build();
    }

    //Configuration
    @Bean(name = "configurationCore")
    public HttpSolrClient getConfigurationCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/configuration").build();
    }



    //Allele
    @Bean(name = "alleleCore")
    public HttpSolrClient getAlleleCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/allele").build();
    }


    //Autosuggest
    @Bean(name = "autosuggestCore")
    HttpSolrClient getAutosuggestCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/autosuggest").build();
    }

    //Gene
    @Bean(name = "geneCore")
    HttpSolrClient getGeneCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/gene").build();
    }

    //GenotypePhenotype
    // TK: this core seems to be used only in the test packages - remove?
    @Bean(name = "genotypePhenotypeCore")
    HttpSolrClient getGenotypePhenotypeCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/genotype-phenotype").build();
    }

    //DELETEME
//	//GenotypePhenotype
//	@Bean(name = "genotypePhenotypeCore")
//	HttpSolrClient getGenotypePhenotypeCore() {
//		return new HttpSolrClient("http://ves-hx-d1:8090/mi/impc/beta/solr/genotype-phenotype");
//	}

    // Impc images core
    @Bean(name = "impcImagesCore")
    HttpSolrClient getImpcImagesCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/impc_images").build();
    }

    //SangerImages
    @Bean(name = "sangerImagesCore")
    HttpSolrClient getImagesCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/images").build();
    }

    //ANATOMY
    @Bean(name = "anatomyCore")
    HttpSolrClient getAnatomyCore() { return new HttpSolrClient.Builder(solrBaseUrl + "/anatomy").build();	}

    //MP
    @Bean(name = "mpCore")
    HttpSolrClient getMpCore() { return new HttpSolrClient.Builder(solrBaseUrl + "/mp").build(); }

    //EMAP
    @Bean(name = "emapCore")
    HttpSolrClient getEmapCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/emap").build();
    }

    @Bean(name = "experimentCore")
    HttpSolrClient getExperimentCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/experiment").build();
    }

    //Pipeline
    @Bean(name = "pipelineCore")
    HttpSolrClient getPipelineCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/pipeline").build();
    }

    //Preqc
    @Bean(name = "preqcCore")
    HttpSolrClient getPreqcCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/preqc").build();
    }

    //StatisticalResult
    @Bean(name = "statisticalResultCore")
    HttpSolrClient getStatisticalResultCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/statistical-result").build();
    }


    // Service Beans


    @Bean
    PhenotypeCenterService phenotypeCenterService() {
        return new PhenotypeCenterService(getExperimentCore());
    }

    @Bean
    PhenotypeCenterProcedureCompletenessService phenotypeCenterProcedureCompletenessService() {
        return new PhenotypeCenterProcedureCompletenessService(phenotypeCenterService(), impressService);
    }
}