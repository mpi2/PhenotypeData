package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.util.NamedList;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;

@Configuration
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"})
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@ComponentScan("org.mousephenotype.cda.db")
public class ObservationIndexerTestConfig {

    @Value("${owlpath}")
    protected String owlpath;

    @Value("${internal_solr_url}")
    private String internalSolrUrl;

    @Autowired
    public DataSource komp2DataSource;

    @Autowired
    public OntologyTermRepository ontologyTermRepository;


    /////////////////////////
    // Read only solr servers
    /////////////////////////

    // allele
    @Bean(name = "alleleCore")
    public HttpSolrClient alleleCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/allele").build();
    }

    // allele2
    @Bean(name = "allele2Core")
    public HttpSolrClient allele2Core() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/allele2").build();
    }

    // anatomy
    @Bean(name = "anatomyCore")
    HttpSolrClient anatomyCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/anatomy").build();
    }

    // autosuggest
    @Bean(name = "autosuggestCore")
    HttpSolrClient autosuggestCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/autosuggest").build();
    }

    // experiment
    @Bean(name = "experimentCore")
    HttpSolrClient experimentCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/experiment").build();
    }

    // gene
    @Bean(name = "geneCore")
    HttpSolrClient geneCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/gene").build();
    }

    // genotype-phenotype
    @Bean(name = "genotypePhenotypeCore")
    HttpSolrClient genotypePhenotypeCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/genotype-phenotype").build();
    }

    // images
    @Bean(name = "sangerImagesCore")
    HttpSolrClient imagesCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/images").build();
    }

    // impc_images
    @Bean(name = "impcImagesCore")
    HttpSolrClient impcImagesCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/impc_images").build();
    }

    // mgi-phenotype
    @Bean(name = "mgiPhenotypeCore")
    HttpSolrClient mgiPhenotypeCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/mgi-phenotype").build();
    }

    // mp
    @Bean(name = "mpCore")
    HttpSolrClient mpCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/mp").build(); }

    // phenodigm
    @Bean(name = "phenodigmCore")
    public HttpSolrClient phenodigmCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/phenodigm").build();
    }

    // pipeline
    @Bean(name = "pipelineCore")
    HttpSolrClient pipelineCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/pipeline").build();
    }

    // product
    @Bean(name = "productCore")
    HttpSolrClient productCore() { return new HttpSolrClient.Builder(internalSolrUrl + "/product").build(); }

    // statistical-result
    @Bean(name = "statisticalResultCore")
    HttpSolrClient statisticalResultCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/statistical-result").build();
    }


    ///////////
    // SERVICES
    ///////////

    @Bean
    public ImpressService impressService() {
        return new ImpressService(pipelineCore());
    }


    ////////////////
    // MISCELLANEOUS
    ////////////////

    @Bean
    public Connection connection() throws Exception {
        return komp2DataSource.getConnection();
    }


//    @Bean
//    public ObservationIndexer observationIndexer() {
//        return new ObservationIndexer(komp2DataSource, ontologyTermRepository, experimentCore());
//    }

    @Bean
    public SolrClient solrClient() {
        return new SolrClient() {
            @Override
            public NamedList<Object> request(SolrRequest solrRequest, String s) throws SolrServerException, IOException {
                return null;
            }

            @Override
            public void close() throws IOException {

            }
        };
    }
}