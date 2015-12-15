package org.mousephenotype.cda.solr;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.solr.service.PhenotypeCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;

import javax.validation.constraints.NotNull;


/**
 * Solr server bean configuration
 */

@Configuration
@ComponentScan("org.mousephenotype.cda.solr")
@EnableSolrRepositories(basePackages = {"org.mousephenotype.cda.solr.repositories"}, multicoreSupport = true)
public class ImitsSolrServerConfig {

	//private static final String IMITS_SOLR_CORE_URL = "http://ikmc.vm.bytemark.co.uk:8983/solr";
	
	@NotNull
	@Value("${imits.solr.host}")
	private String imitsSolrBaseUrl;

		@Bean(name = "allele2Core")
		HttpSolrServer getAllele2Core() {
			return new HttpSolrServer(imitsSolrBaseUrl + "/allele2");
		}
		
		@Bean(name = "eucommCreProductsCore")
		HttpSolrServer getEucomCreToolsProduct() {
			return new HttpSolrServer(imitsSolrBaseUrl + "/eucommtoolscre_product");
		}
		
		@Bean(name = "eucommToolsProductCore")
		HttpSolrServer getEucommToolsProductCore() {
			return new HttpSolrServer(imitsSolrBaseUrl + "/product");
		}
		
		@Bean(name = "eucommToolsCreAllele2Core")
		HttpSolrServer getEucommToolsCreAllele2() {
			return new HttpSolrServer(imitsSolrBaseUrl + "/eucommtoolscre_allele2");
		}
		

}
