/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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
package org.mousephenotype.cda.releasenotes;


import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;

import javax.validation.constraints.NotNull;

@Configuration
@EnableJpaRepositories(basePackages = {"org.mousephenotype.cda.db.repositories"})
@EntityScan("org.mousephenotype.cda.db.pojo")
@ComponentScan(basePackages = {
        "org.mousephenotype.cda.releasenotes",
        "org.mousephenotype.cda.db",
        "org.mousephenotype.cda.solr"})
public class ReleaseAnalyticsManagerConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${internal_solr_url}")
    private String internalSolrUrl;

    // Required for spring-data-solr repositories
    @Bean
    public SolrClient solrClient() { return new HttpSolrClient.Builder(internalSolrUrl).build(); }

    @Bean
    public SolrOperations solrTemplate() { return new SolrTemplate(solrClient()); }
    // Required for spring-data-solr repositories

    @Bean(name = "experimentCore")
    HttpSolrClient getExperimentCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/experiment").build();
    }

    @Bean(name = "pipelineCore")
    HttpSolrClient getPipelineCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/pipeline").build();
    }

    @Bean(name = "geneCore")
    HttpSolrClient getGeneCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/gene").build();
    }

    @Bean(name = "genotypePhenotypeCore")
    HttpSolrClient getGenotypePhenotypeCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/genotype-phenotype").build();
    }

    @Bean(name = "statisticalResultCore")
    HttpSolrClient getStatisticalResultCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/statistical-result").build();
    }

    @Bean(name = "sangerImagesCore")
    HttpSolrClient getSangerImagesCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/images").build();
    }

    @Bean(name = "impcImagesCore")
    HttpSolrClient getImpcImagesCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/impc_images").build();
    }

    @Bean(name = "allele2Core")
    HttpSolrClient getAllele2Core() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/allele2").build();
    }

    @Bean(name = "alleleCore")
    HttpSolrClient getAlleleCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/allele").build();
    }

    @Bean(name = "anatomyCore")
    HttpSolrClient getAnatomyCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/anatomy").build();
    }

    @Bean(name = "mpCore")
    HttpSolrClient getMpCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/mp").build();
    }

    @Bean(name = "productCore")
    HttpSolrClient getProductCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/product").build();
    }

    @Bean(name = "phenodigmCore")
    HttpSolrClient getPhenodigmCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/phenodigm").build();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propsConfig
                = new PropertySourcesPlaceholderConfigurer();
        propsConfig.setLocation(new ClassPathResource("git.properties"));
        propsConfig.setIgnoreResourceNotFound(true);
        propsConfig.setIgnoreUnresolvablePlaceholders(true);
        return propsConfig;
    }

}