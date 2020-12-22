package uk.ac.ebi.phenotype;

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


    @Value("${internal_solr_url}")
    private String internalSolrUrl;

    @NotNull
    @Autowired
    ImpressService impressService;


    // Required for spring-data-solr repositories
    @Bean
    public SolrClient solrClient() { return new HttpSolrClient.Builder(internalSolrUrl).build(); }

    @Bean
    public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }
    // Required for spring-data-solr repositories


    @Bean(name = "allele2Core")
    HttpSolrClient getAllele2Core() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/allele2").build();
    }

    @Bean(name = "productCore")
    HttpSolrClient getProductCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/product").build();
    }


    /////////////////////////
    // Read only solr servers
    /////////////////////////

    // allele
    @Bean(name = "alleleCore")
    public HttpSolrClient getAlleleCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/allele").build();
    }

    // anatomy
    @Bean(name = "anatomyCore")
    HttpSolrClient getAnatomyCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/anatomy").build();	}

    // autosuggest
    @Bean(name = "autosuggestCore")
    HttpSolrClient getAutosuggestCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/autosuggest").build();
    }

    // experiment
    @Bean(name = "experimentCore")
    HttpSolrClient getExperimentCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/experiment").build();
    }

    // gene
    @Bean(name = "geneCore")
    HttpSolrClient getGeneCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/gene").build();
    }

    // essential gene
    @Bean(name = "essentialGeneCore")
    HttpSolrClient getEssentialGeneCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/essentialgenes").build();
    }

    // genotype-phenotype
    // TK: this core seems to be used only in the test packages - remove?
    @Bean(name = "genotypePhenotypeCore")
    HttpSolrClient getGenotypePhenotypeCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/genotype-phenotype").build();
    }

    // images
    @Bean(name = "sangerImagesCore")
    HttpSolrClient getImagesCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/images").build();
    }

    // impc_images
    @Bean(name = "impcImagesCore")
    HttpSolrClient getImpcImagesCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/impc_images").build();
    }

    // mp
    @Bean(name = "mpCore")
    HttpSolrClient getMpCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/mp").build(); }

    // phenodigm
    @Bean(name = "phenodigmCore")
    public HttpSolrClient getPhenodigmCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/phenodigm").build();
    }

    // pipeline
    @Bean(name = "pipelineCore")
    HttpSolrClient getPipelineCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/pipeline").build();
    }

    // statistical-result
    @Bean(name = "statisticalResultCore")
    HttpSolrClient getStatisticalResultCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/statistical-result").build();
    }
}
