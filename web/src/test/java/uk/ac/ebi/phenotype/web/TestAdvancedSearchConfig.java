package uk.ac.ebi.phenotype.web;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * TestConfig sets up the in memory database for supporting the database tests.
 * <p>
 * This will also import the data specified in the sql/test-data.sql file.
 */

@Configuration
@EnableTransactionManagement
@EnableAutoConfiguration
@ComponentScan(value = "org.mousephenotype.cda",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ, pattern = {"org.mousephenotype.cda.db.entity.*OntologyDAO"})
)
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"})
public class TestAdvancedSearchConfig {

    @Value("${imits.solr.host}")
    private String imitsSolrBaseUrl;

    @Value("${solr.host}")
    private String solrBaseUrl;


    @Autowired
    ImpressService impressService;


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


    // Read only solr servers

    // phenodigm
    @Bean(name = "phenodigmCore")
    public HttpSolrClient getPhenodigmCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/phenodigm").build();
    }

    // autosuggest
    @Bean(name = "autosuggestCore")
    HttpSolrClient getAutosuggestCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/autosuggest").build();
    }

    // genotype-phenotype
    @Bean(name = "genotypePhenotypeCore")
    HttpSolrClient getGenotypePhenotypeCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/genotype-phenotype").build();
    }

    //Pipeline
    @Bean(name = "pipelineCore")
    HttpSolrClient getPipelineCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/pipeline").build();
    }

	// statistical-result
	@Bean(name = "statisticalResultCore")
	HttpSolrClient getStatisticalResultCore() {
		return new HttpSolrClient.Builder(solrBaseUrl + "/statistical-result").build();
	}
}