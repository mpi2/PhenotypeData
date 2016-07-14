package org.mousephenotype.cda.solr.service;

import javax.validation.constraints.NotNull;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
public class OrderServiceTest {

	@Autowired
	private OrderService orderService;
	
	@Configuration
	@ComponentScan(
		basePackages = {"org.mousephenotype.cda"},
		useDefaultFilters = false,
		includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {OrderService.class})
		})
	static class ContextConfiguration {

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
		
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}
	
	@Test
	public void getAlleleDocsTest(){
		String geneAcc="MGI:1859328";
		try {
			orderService.getAlleleDocs(geneAcc);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
