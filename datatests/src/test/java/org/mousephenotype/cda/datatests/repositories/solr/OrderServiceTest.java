package org.mousephenotype.cda.datatests.repositories.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mousephenotype.cda.enumerations.OrderType;
import org.mousephenotype.cda.solr.service.OrderService;
import org.mousephenotype.cda.solr.service.dto.ProductDTO;
import org.mousephenotype.cda.solr.web.dto.OrderTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class OrderServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OrderService orderService;


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
		} catch (SolrServerException | IOException e) {

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
        } catch (SolrServerException | IOException e) {

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
        } catch (SolrServerException | IOException e) {

            e.printStackTrace();
        }
		assertTrue(orderRows.size()>1);
	}
}