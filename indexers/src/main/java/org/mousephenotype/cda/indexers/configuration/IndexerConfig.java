package org.mousephenotype.cda.indexers.configuration;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
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

@Configuration
@ComponentScan(basePackages = "org.mousephenotype.cda.db")
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@EnableTransactionManagement
public class IndexerConfig {

    @Value("${buildIndexesSolrUrl}")
    private String writeSolrBaseUrl;

    @Value("${internal_solr_url}")
    private String internalSolrUrl;

    private DataSource             komp2DataSource;
    private OntologyTermRepository ontologyTermRepository;
    private SolrClient             pipelineCore;

    @Inject
    public IndexerConfig(DataSource komp2DataSource, OntologyTermRepository ontologyTermRepository) {
        this.komp2DataSource = komp2DataSource;
        this.ontologyTermRepository = ontologyTermRepository;
    }


    /////////////////////
    // read-only indexers
    /////////////////////

    // Creation of the IMPC disease core has been replaced by phenodigm core provided by QMUL
    @Bean
    SolrClient phenodigmCore() {
        // readonly
        return new HttpSolrClient.Builder(internalSolrUrl + "/phenodigm").build();
    }


    //////////////////////
    // read-write indexers
    //////////////////////
    @Bean
    SolrClient experimentCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/experiment").build();
    }

    @Bean
    SolrClient genotypePhenotypeCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/genotype-phenotype").build();
    }

    @Bean
    SolrClient statisticalResultCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/statistical-result").build();
    }

    @Bean
    SolrClient alleleCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/allele").build();
    }

    @Bean
    SolrClient sangerImagesCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/images").build();
    }

    @Bean
    SolrClient impcImagesCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/impc_images").build();
    }

    @Bean
    SolrClient mpCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/mp").build();
    }

    @Bean
    SolrClient anatomyCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/anatomy").build();
    }

    @Bean
    SolrClient pipelineCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/pipeline").build();
    }

    @Bean
    SolrClient geneCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/gene").build();
    }

    @Bean
    SolrClient allele2Core() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/allele2").build();
    }

    @Bean
    SolrClient productCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/product").build();
    }

    @Bean
    SolrClient autosuggestCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/autosuggest").build();
    }

    @Bean
    SolrClient mgiPhenotypeCore() {
        return new HttpSolrClient.Builder(writeSolrBaseUrl + "/mgi-phenotype").build();
    }


    //////////////
    // datasources
    //////////////

    @Bean
    @ConfigurationProperties(prefix = "datasource.uniprot")
    public DataSource uniprotDataSource() {
        return DataSourceBuilder.create().driverClassName("oracle.jdbc.driver.OracleDriver").build();
    }
}