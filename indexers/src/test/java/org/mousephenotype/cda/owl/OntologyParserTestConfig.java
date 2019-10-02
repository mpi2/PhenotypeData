/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.owl;


import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class OntologyParserTestConfig {


    @Value("${owlpath}")
    protected String owlpath;

    @Value("${internal_solr_url}")
    private String internalSolrUrl;

    @Value("${datasource.komp2.jdbc-url}")
    private String komp2Url;

    @Value("${datasource.komp2.username}")
    private String username;

    @Value("${datasource.komp2.password}")
    private String password;


    @Bean
    public DataSource dataSource() {

        DataSource dataSource = null;

        // Spring Boot 2.1.4 invokes this method at startup without the benefit of the configServer and thus does not
        // resolve the @Value placeholders. Returning null in this case seems to have no ill downstream side effects.
        if ( ! komp2Url.contains("datasource.komp2")) {
            try {
                dataSource = SqlUtils.getConfiguredDatasource(komp2Url, username, password);
            } catch (Exception e) {
                System.out.println();
            }
        }

        return dataSource;
    }

    @Bean
    public OntologyParserFactory ontologyParserFactory() {
        return new OntologyParserFactory(dataSource(), owlpath);
    }


    /////////////////////////
    // READ-ONLY SOLR SERVERS
    /////////////////////////

    // Needed for OntologyParserFactory bean creation.

    // phenodigm
    @Bean
    public HttpSolrClient phenodigmCore() {
        return new HttpSolrClient.Builder(internalSolrUrl + "/phenodigm").build();
    }


    ////////////////
    // MISCELLANEOUS
    ////////////////

    @Bean
    public String owlPath() {
        return owlpath;
    }
}