/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.datatests.repositories.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrJ;
import org.mousephenotype.cda.solr.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

@Configuration
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"})
public class RepositorySolrTestConfig {

    @Value("${internal_solr_url}")
    private String internalSolrUrl;

    private GenotypePhenotypeService genotypePhenotypeService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /////////////////////////
    // READ-ONLY SOLR SERVERS
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
    public AlleleService alleleService() {
        return new AlleleService(alleleCore());

    }
    @Bean
    public AnatomyService anatomyService() {
        return new AnatomyService(anatomyCore());
    }

    @Bean
    public ExpressionService expressionService() {
        return new ExpressionService(experimentCore(), impcImagesCore(), anatomyService(), expressionServiceLacz(), impressService());
    }

    @Bean
    public ExpressionServiceLacz expressionServiceLacz() {
        return new ExpressionServiceLacz(experimentCore());
    }
    @Bean
    public GeneService geneService() {
        return new GeneService(geneCore(), impressService());
    }

    @Bean
    public GrossPathService grossPathService() {
        return new GrossPathService(observationService(), imageService());
    }

    @Bean
    public ImageService imageService() {
        return new ImageService(impcImagesCore(), statisticalResultCore());
    }

    @Bean
    public ImpressService impressService() {
        return new ImpressService(pipelineCore());
    }

    @Bean
    public MpService mpService() {
        return new MpService(mpCore());
    }

    @Bean
    public ObservationService observationService() {
        return new ObservationService(experimentCore());
    }

    @Bean
    public OrderService orderService() {
        return new OrderService(allele2Core(), productCore());
    }

    @Bean
    public PhenodigmService phenodigmService() {
        return new PhenodigmService(phenodigmCore());
    }


    ////////////////
    // Miscellaneous
    ////////////////

    @Bean
    public ImagesSolrJ imagesSolrJ(SolrClient sangerImagesCore) {
        return new ImagesSolrJ(sangerImagesCore);
    }


    /////////////////////////////////////////////
    // Required for spring-data-solr repositories
    /////////////////////////////////////////////

    @Bean
    public SolrClient solrClient() { return new HttpSolrClient.Builder(internalSolrUrl).build(); }

    @Bean
    public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }
}
