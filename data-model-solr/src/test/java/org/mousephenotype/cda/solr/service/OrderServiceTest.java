package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.enumerations.OrderType;
import org.mousephenotype.cda.solr.service.dto.ProductDTO;
import org.mousephenotype.cda.solr.web.dto.OrderTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
public class OrderServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

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
			return new HttpSolrClient.Builder(solrBaseUrl + "/allele2").build();
		}

		

		@Bean(name = "productCore")
		HttpSolrClient getProductCore() {
			return new HttpSolrClient.Builder(solrBaseUrl + "/product").build();
		}

		

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}
	
	@Test
	public void getAlleleDocsTest() throws IOException, SolrServerException {
		String geneAcc="MGI:1859328";
			assertTrue(orderService.getAllele2DTOs(geneAcc, Integer.MAX_VALUE, false).size()>=2);
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
			Map<String, List<ProductDTO>> storeToMap = orderService.getStoreNameToProductsMap(acc, allele, orderType, false);
			for(String store: storeToMap.keySet()){
				logger.info("store="+store);
				logger.info("products="+storeToMap.get(store));
			}
			assertTrue(storeToMap.size()>0);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetStoreNameToProductsMapWithCreLine() {
		String acc = "MGI:95771";
		String allele = "tm2(EGFP/cre/ERT2)Wtsi";
		OrderType orderType = OrderType.mouse;
		boolean creLine=true;

		try {
			Map<String, List<ProductDTO>> storeToMap = orderService.getStoreNameToProductsMap(acc, allele, orderType, creLine);
			for(String store: storeToMap.keySet()){
				logger.info("store="+store);
				logger.info("products="+storeToMap.get(store));
			}
			assertTrue(storeToMap.size()>0);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetOrderTableRowsForCreLine(){
		List<OrderTableRow> orderRows=new ArrayList<>();
		try {
			String acc=null;
			
			boolean creLine=true;
			orderRows = orderService.getOrderTableRows(acc, null, creLine);
			logger.info("order rows size in test="+orderRows.size());
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(orderRows.size()>1);
	}
}