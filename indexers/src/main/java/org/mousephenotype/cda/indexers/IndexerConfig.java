package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.util.NamedList;
import org.mousephenotype.cda.db.repositories.GenesSecondaryProjectRepository;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import javax.sql.DataSource;
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
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/experiment").build();
    }

    @Bean
    public SolrClient genotypePhenotypeCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/genotype-phenotype").build();
    }

    @Bean
    public SolrClient statisticalResultCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/statistical-result").build();
    }

    @Bean
    public SolrClient alleleCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/allele").build();
    }

    @Bean
    public SolrClient sangerImagesCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/images").build();
    }

    @Bean
    public SolrClient impcImagesCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/impc_images").build();
    }

    @Bean
    public SolrClient mpCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/mp").build();
    }

    @Bean
    public SolrClient anatomyCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/anatomy").build();
    }

    @Bean
    public SolrClient pipelineCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/pipeline").build();
    }

    @Bean
    public SolrClient geneCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/gene").build();
    }

    @Bean
    public SolrClient allele2Core() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/allele2").build();
    }

    @Bean
    public SolrClient productCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/product").build();
    }

    @Bean
    public SolrClient autosuggestCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/autosuggest").build();
    }

    @Bean
    public SolrClient mgiPhenotypeCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/mgi-phenotype").build();
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



    //////////////
    // datasources
    //////////////


    @Value("${datasource.uniprot.jdbc-url}")
    String uniprotUrl;

    @Value("${datasource.uniprot.username}")
    String uniprotUsername;

    @Value("${datasource.uniprot.password}")
    String uniprotPassword;

    @Bean
    @ConfigurationProperties(prefix = "datasource.uniprot")
    public DataSource uniprotDataSource() {
        return DataSourceBuilder.create().driverClassName("oracle.jdbc.driver.OracleDriver").build();
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