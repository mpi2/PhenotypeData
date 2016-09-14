package org.mousephenotype.cda.solr.service;

import static org.junit.Assert.*;

import javax.validation.constraints.NotNull;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.enumerations.OrderType;
import org.mousephenotype.cda.solr.service.dto.ProductDTO;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
			includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {OrderService.class})
			})
	static class ContextConfiguration {

		@NotNull
		@Value("${solr.host}")
		private String solrBaseUrl;

		@Bean(name = "allele2Core")
		HttpSolrClient getAllele2Core() {
			return new HttpSolrClient(solrBaseUrl + "/allele2");
		}

		@Bean(name = "eucommCreProductsCore")
		HttpSolrClient getEucomCreToolsProduct() {
			return new HttpSolrClient(solrBaseUrl + "/eucommtoolscre_product");
		}

		@Bean(name = "productCore")
		HttpSolrClient getProductCore() {
			return new HttpSolrClient(solrBaseUrl + "/product");
		}

		@Bean(name = "eucommToolsCreAllele2Core")
		HttpSolrClient getEucommToolsCreAllele2() {
			return new HttpSolrClient(solrBaseUrl + "/eucommtoolscre_allele2");
		}


		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}
	
	@Test
	public void getAlleleDocsTest() throws IOException, SolrServerException {
		String geneAcc="MGI:1859328";
			assertTrue(orderService.getAllele2DTOs(geneAcc, Integer.MAX_VALUE).size()>=2);
	}
	
	@Test
	public void getProductDocsTest()  {
		String geneAcc="MGI:1859328";
			try {
				assertTrue(orderService.getProductsForGene(geneAcc).size()>2);
			} catch (SolrServerException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	@Test
	public void testGetStoreNameToProductsMap() {
		String acc = "MGI:1859328";
		String allele = "tm1a(EUCOMM)Wtsi";
		OrderType orderType = OrderType.mouse;

		try {
			Map<String, List<ProductDTO>> storeToMap = orderService.getStoreNameToProductsMap(acc, allele, orderType);
			for(String store: storeToMap.keySet()){
				System.out.println("store="+store);
				System.out.println("products="+storeToMap.get(store));
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
