/*******************************************************************************
 *  Copyright (c) 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package uk.ac.ebi.phenotype.web;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.phenotype.service.UniprotService;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mrelac on 29/06/2015.
 */

@Configuration
@EnableAutoConfiguration
public class TestConfig {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${solr.host}")
    private String solrHost;

    @Value("${cms_base_url}")
    private String cmsBaseUrl;


	@Value("${base_url}")
	private String baseUrl;

    @Value("${internal_solr_url}")
    private String internalSolrUrl;

    @PostConstruct
    public void initialise() {
        logger.info("dataSource.komp2.url: " + komp2DataSource());
        logger.info("solr.host:            " + solrHost);
        logger.info("baseUrl:              " + baseUrl);
        logger.info("internalSolrUrl:      " + internalSolrUrl);
    }


    @Bean (name="globalConfiguration")
    public Map<String, String> globalConfiguration(){

    	Map <String, String> gc = new HashMap<>();
    	gc.put("baseUrl", "${baseUrl}");
    	gc.put("cmsBaseUrl", "${cmsBaseUrl}");
    	gc.put("solrUrl", "${solrUrl}");
    	gc.put("internalSolrUrl", "${internalSolrUrl}");
    	gc.put("mediaBaseUrl", "${mediaBaseUrl}");
    	gc.put("impcMediaBaseUrl", "${impcMediaBaseUrl}");
    	gc.put("pdfThumbnailUrl", "${pdfThumbnailUrl}");
    	gc.put("googleAnalytics", "${googleAnalytics}");
    	gc.put("liveSite", "${liveSite}");

		return gc;
	}


	//////////////
	// DATASOURCES
	//////////////

	@Value("${datasource.komp2.jdbc-url}")
	private String komp2Url;

	@Value("${datasource.komp2.username}")
	private String username;

	@Value("${datasource.komp2.password}")
	private String password;

	@Bean
	public DataSource komp2DataSource() {

		DataSource komp2DataSource = SqlUtils.getConfiguredDatasource(komp2Url, username, password);

		return komp2DataSource;
	}


	@Bean
	public UniprotService uniprotService() {
		return new UniprotService();
	}
}