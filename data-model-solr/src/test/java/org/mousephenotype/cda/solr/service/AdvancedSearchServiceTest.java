package org.mousephenotype.cda.solr.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfigSolr.class})
public class AdvancedSearchServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private       AdvancedSearchService advancedSearchService;

	@Test
    public void testGetGenesForMpId(){

		//Test "abnormal glucose homeostasis" only

    	String phenotypeId="MP:0002078";
    	List<String> geneSymbols=null;
    	try {
			geneSymbols = advancedSearchService.getGenesForPhenotype(phenotypeId);

		} catch (IOException | URISyntaxException | SolrServerException e) {

			e.printStackTrace();
		}
    	//currently 4056 genes in the gp core are associated to abnormal glucose Homeostasis
    	logger.debug("geneSymbols size for phenotype is "+geneSymbols.size());
    	assertTrue(geneSymbols.size()>405);


    }

	@Test
	public void testGetGenesForPhenotypeAndPhenotype(){
		String phenotypeId="MP:0002078";//"abnormal glucose homeostasis
		String phenotypeId2="MP:0003956";//abnormal body size
		List<String> resultsOfAndQuery=null;
		try {

			resultsOfAndQuery= advancedSearchService.getGenesForPhenotypeAndPhenotype(phenotypeId, phenotypeId2);

		} catch (IOException | URISyntaxException | SolrServerException e) {

			e.printStackTrace();
		}
		logger.debug("resultsOfAndQuery size for phenotype is "+resultsOfAndQuery.size());
		assertTrue(resultsOfAndQuery.size()>0);
//		for(String geneSymbol: resultsOfAndQuery){
//			logger.debug("intersection symbol is "+geneSymbol);
//		}
		//Ncald is a gene contained on both phenotype pages. 57 currently in common
	}

	@Test
	public void testGetGenesForPhenotypeORPhenotype(){
		String phenotypeId="MP:0002078";//"abnormal glucose homeostasis
		String phenotypeId2="MP:0003956";//abnormal body size
		Collection<String> resultsOfAndQuery=null;
		try {

			resultsOfAndQuery= advancedSearchService.getGenesForPhenotypeORPhenotype(phenotypeId, phenotypeId2);

		} catch (IOException | URISyntaxException | SolrServerException e) {

			e.printStackTrace();
		}
		logger.debug("resultsOfORQuery size for phenotype is "+resultsOfAndQuery.size());
		assertTrue(resultsOfAndQuery.size()>=680);
//		for(String geneSymbol: resultsOfAndQuery){
//			logger.debug("union symbol is "+geneSymbol);
		//}
		//Ncald is a gene contained on both phenotype pages. 57 currently in common 331 + 406 -(57)=680
	}
}