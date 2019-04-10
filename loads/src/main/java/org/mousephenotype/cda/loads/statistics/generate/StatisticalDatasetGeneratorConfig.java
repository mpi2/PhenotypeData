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

package org.mousephenotype.cda.loads.statistics.generate;

import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.validation.constraints.NotNull;

@Configuration
@PropertySource(value="file:${user.home}/configfiles/${profile:dev}/datarelease.properties")
public class StatisticalDatasetGeneratorConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${solr.host}")
    private String solrBaseUrl;

    @Bean(name = "experimentCore")
    HttpSolrClient getExperimentCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/experiment").build();
    }

    //Pipeline
    @Bean(name = "pipelineCore")
    HttpSolrClient getPipelineCore() {
        return new HttpSolrClient.Builder(solrBaseUrl + "/pipeline").build();
    }
}