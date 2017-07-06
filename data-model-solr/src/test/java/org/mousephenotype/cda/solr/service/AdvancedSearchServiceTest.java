package org.mousephenotype.cda.solr.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfigSolr.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class AdvancedSearchServiceTest {
	@Autowired
	@Qualifier("postqcService")
    private PostQcService gpService;
	
	private AdvancedSearchService searchSolrDao;
	@Before
	public void setUp(){
		searchSolrDao=new AdvancedSearchService(gpService);
	}
	
	@Test
    public void testGetGenesForMpId(){
		
		//Test "abnormal glucose homeostasis" only

		AdvancedSearchService searchSolrDao=new AdvancedSearchService(gpService);
    	String phenotypeId="MP:0002078";
    	List<String> geneSymbols=null;
    	try {
			geneSymbols=searchSolrDao.getGenesForPhenotype(phenotypeId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//currently 4056 genes in the gp core are associated to abnormal glucose Homeostasis
    	System.out.println("geneSymbols size for phenotype is "+geneSymbols.size());
    	assertTrue(geneSymbols.size()>405);
    	
    	
    }
	
	@Test
	public void testGetGenesForPhenotypeAndPhenotype(){
		String phenotypeId="MP:0002078";//"abnormal glucose homeostasis
		String phenotypeId2="MP:0003956";//abnormal body size
		List<String> resultsOfAndQuery=null;
		try {
			resultsOfAndQuery=searchSolrDao.getGenesForPhenotypeAndPhenotype(phenotypeId, phenotypeId2);
		} catch (IOException | URISyntaxException | SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("resultsOfAndQuery size for phenotype is "+resultsOfAndQuery.size());
		assertTrue(resultsOfAndQuery.size()>0);
		for(String geneSymbol: resultsOfAndQuery){
			System.out.println("intersection symbol is "+geneSymbol);
		}
		//Ncald is a gene contained on both phenotype pages. 57 currently in common
	}
	
	@Test
	public void testGetGenesForPhenotypeORPhenotype(){
		String phenotypeId="MP:0002078";//"abnormal glucose homeostasis
		String phenotypeId2="MP:0003956";//abnormal body size
		List<String> resultsOfAndQuery=null;
		try {
			resultsOfAndQuery=searchSolrDao.getGenesForPhenotypeORPhenotype(phenotypeId, phenotypeId2);
		} catch (IOException | URISyntaxException | SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("resultsOfAndQuery size for phenotype is "+resultsOfAndQuery.size());
		assertTrue(resultsOfAndQuery.size()>736);
		for(String geneSymbol: resultsOfAndQuery){
			System.out.println("intersection symbol is "+geneSymbol);
		}
		//Ncald is a gene contained on both phenotype pages. 57 currently in common 331 + 406 -(57*2)=355
	}

}
