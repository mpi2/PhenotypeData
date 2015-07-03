package org.mousephenotype.cda.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Solr beans
 */
@Configuration
public class SolrServerConfig {

	@Value("${solr.host")
	private String solrBaseUrl;

	@Bean(name = "experimentCore")
	SolrServer getExperimentSolr() {
		return new HttpSolrServer(solrBaseUrl + "/experiment");
	}

	@Bean(name = "imagesCore")
	SolrServer getimagesSolr() {
		return new HttpSolrServer(solrBaseUrl + "/images");
	}

}
