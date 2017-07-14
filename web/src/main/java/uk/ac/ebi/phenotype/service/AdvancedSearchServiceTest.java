package uk.ac.ebi.phenotype.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import net.sf.json.JSONObject;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfigSolr.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class AdvancedSearchServiceTest {
	@Autowired
	@Qualifier("postqcService")
    private PostQcService gpService;
	
	private AdvancedSearchService advancedSearchService;
	@Before
	public void setUp(){
		advancedSearchService=new AdvancedSearchService(gpService);
	}
	
	@Test
	public void getResult(){
		String hostname="http://localhost";
		String baseUrl="/";
		JSONObject jParams = null;//@TODO need to set a dummy object here still
		
		Boolean significantPValue=true;
		SexType sexType=SexType.both;
		String impressParameter="";
		
		Double lowerPvalue = null;
		Double upperPvalue = null;
		List<String> chrs = null;
		Integer regionStart = null;
		Integer regionEnd = null;
		boolean isMouseGenes = false;
		List<String> geneList = null;
		List<String> genotypeList = null;
		List<String> alleleTypes = null;
		List<String> mutationTypes = null;
		int phenodigmScoreLow = 0;
		int phenodigmScoreHigh = 0;
		String fileType = null;
		JSONObject jcontent=null;
		
		try {
			jcontent = advancedSearchService.fetchGraphDataAdvSrch(hostname, baseUrl,jParams, significantPValue, sexType, impressParameter, lowerPvalue, upperPvalue, chrs, regionStart, regionEnd, isMouseGenes, geneList, genotypeList, alleleTypes, mutationTypes, phenodigmScoreLow, phenodigmScoreHigh, fileType);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			resultsOfAndQuery=advancedSearchService.getGenesForPhenotypeAndPhenotype(phenotypeId, phenotypeId2);
		} catch (IOException | URISyntaxException | SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("resultsOfAndQuery size for phenotype is "+resultsOfAndQuery.size());
		assertTrue(resultsOfAndQuery.size()>0);
//		for(String geneSymbol: resultsOfAndQuery){
//			System.out.println("intersection symbol is "+geneSymbol);
//		}
		//Ncald is a gene contained on both phenotype pages. 57 currently in common
	}
	
	@Test
	public void testGetGenesForPhenotypeORPhenotype(){
		String phenotypeId="MP:0002078";//"abnormal glucose homeostasis
		String phenotypeId2="MP:0003956";//abnormal body size
		Collection<String> resultsOfAndQuery=null;
		try {
			resultsOfAndQuery=advancedSearchService.getGenesForPhenotypeORPhenotype(phenotypeId, phenotypeId2);
		} catch (IOException | URISyntaxException | SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("resultsOfORQuery size for phenotype is "+resultsOfAndQuery.size());
		assertTrue(resultsOfAndQuery.size()>=680);
//		for(String geneSymbol: resultsOfAndQuery){
//			System.out.println("union symbol is "+geneSymbol);
		//}
		//Ncald is a gene contained on both phenotype pages. 57 currently in common 331 + 406 -(57)=680
	}

}
