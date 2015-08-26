package uk.ac.ebi.phenotype.api;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.TestConfig;

import static org.junit.Assert.assertTrue;
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/applicationTest.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class GeneServiceTest {

	@Autowired
	GeneService geneService;



	@Test
	public void testGetGeneById() throws SolrServerException {
		String mgiId = "MGI:1929293";
		GeneDTO gene = geneService.getGeneById(mgiId);
		assertTrue(gene!=null);
		System.out.println("Gene symbol is: " + gene.getMarkerSymbol());
		System.out.println("Didn't retreive human gene symbol. Proof: " + gene.getHumanGeneSymbol());
	}

}
