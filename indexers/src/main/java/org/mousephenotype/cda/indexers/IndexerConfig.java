package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.util.NamedList;
import org.mousephenotype.cda.db.repositories.GenesSecondaryProjectRepository;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Configuration
@ComponentScan(basePackages = "org.mousephenotype.cda.db")
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@EnableTransactionManagement
public class IndexerConfig {

    @Value("${buildIndexesSolrUrl}")
    private String writeSolrBaseUrl;

    @Value("${internal_solr_url}")
    private String internalSolrUrl;

    private GenesSecondaryProjectRepository genesSecondaryProjectRepository;

    public static final int QUEUE_SIZE = 10000;
    public static final int THREAD_COUNT = 3;


    @Inject
    public IndexerConfig(@NotNull OntologyTermRepository ontologyTermRepository,
                         @NotNull GenesSecondaryProjectRepository genesSecondaryProjectRepository)
    {
        this.genesSecondaryProjectRepository = genesSecondaryProjectRepository;
    }


    /////////////////////
    // read-only indexers
    /////////////////////

    // Creation of the IMPC disease core has been replaced by phenodigm core provided by QMUL
    @Bean
    public SolrClient phenodigmCore() {
        // readonly
        return new HttpSolrClient.Builder(internalSolrUrl + "/phenodigm").build();
    }


    //////////////////////
    // read-write indexers
    //////////////////////
    @Bean
    public SolrClient experimentCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/experiment")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient genotypePhenotypeCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/genotype-phenotype")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient statisticalResultCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/statistical-result")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient alleleCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/allele")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient sangerImagesCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/images")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient impcImagesCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/impc_images")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient mpCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/mp")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient anatomyCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/anatomy")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient pipelineCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/pipeline")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient geneCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/gene")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient allele2Core() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/allele2")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient productCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/product")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient autosuggestCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/autosuggest")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }

    @Bean
    public SolrClient mgiPhenotypeCore() {
        return new ConcurrentUpdateSolrClient.Builder(writeSolrBaseUrl + "/mgi-phenotype")
                .withQueueSize(QUEUE_SIZE)
                .withThreadCount(THREAD_COUNT).build();
    }


    ///////////
    // SERVICES
    ///////////

    @Bean
    public GenotypePhenotypeService genotypePhenotypeService() {
        return new GenotypePhenotypeService(impressService(), genotypePhenotypeCore(), genesSecondaryProjectRepository);
    }

    @Bean
    public ImpressService impressService() {
        return new ImpressService(pipelineCore());
    }


    ////////////////
    // MISCELLANEOUS
    ////////////////

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