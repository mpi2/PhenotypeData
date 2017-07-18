package uk.ac.ebi.phenotype.service;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.neo4j.cypher.internal.frontend.v2_3.perty.recipe.Pretty.nestWith;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import net.sf.json.JSONObject;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestAdvancedSearchConfig.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class AdvancedSearchServiceTest {
	
	@Autowired
	Session neo4jSession;
	
	@Autowired
	@Qualifier("postqcService")
    private PostQcService gpService;
	
	@Autowired
	 @Qualifier("autosuggestCore")
	 private SolrClient autosuggestCore;
	
	private AdvancedSearchService advancedSearchService;
	@Before
	public void setUp(){
		advancedSearchService=new AdvancedSearchService(gpService, autosuggestCore, neo4jSession);
	}
	
	@Test
	public void getResult(){
		
		PhenotypeFormObject phenotypeFormObject=new PhenotypeFormObject();
		PhenotypeFormEntryRow row=new PhenotypeFormEntryRow("abnormal circulating glucose level");//default cuttoff
		
		phenotypeFormObject.addPhenotypeFormRows(row);
		List<String> dataTypes=new ArrayList<>();
		dataTypes.add("g");
		String hostname="http://localhost";
		String baseUrl="/";
		JSONObject jParams = null;//@TODO need to set a dummy object here still
		
		Boolean significantPValue=true;
		SexType sexType=SexType.both;
		String impressParameter="";
		
		Double lowerPvalue = null;
		Double upperPvalue = null;
		List<String> chrs = new ArrayList<>();
		Integer regionStart = null;
		Integer regionEnd = null;
		boolean isMouseGenes = false;
		List<String> geneList = new ArrayList<>();
		List<String> genotypeList =  new ArrayList<>();
		List<String> alleleTypes =  new ArrayList<>();
		List<String> mutationTypes =  new ArrayList<>();
		int phenodigmScoreLow = 0;
		int phenodigmScoreHigh = 0;
		String diseaseTerm="";
		boolean humanCurated=false;
		String fileType = null;
		JSONObject jcontent=null;
		String humanDiseaseTerm="";//not sure what diseaseTerm is if we have a humand one and disease one?
		String mpStr="abnormal circulating glucose level";//what is this?
		
		try {
			jcontent = advancedSearchService.fetchGraphDataAdvSrch(phenotypeFormObject, dataTypes, hostname, baseUrl,jParams, significantPValue, sexType, impressParameter, lowerPvalue, upperPvalue, chrs, regionStart, regionEnd, isMouseGenes, geneList, genotypeList, alleleTypes, mutationTypes, phenodigmScoreLow, phenodigmScoreHigh, diseaseTerm, humanCurated, humanDiseaseTerm, mpStr, fileType);
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
		System.out.println("jcontent from test is "+jcontent);
	}
	
	@Test
    public void testGetGenesForMpId(){
		
		//Test "abnormal glucose homeostasis" only

		AdvancedSearchService searchSolrDao=new AdvancedSearchService(gpService, autosuggestCore, neo4jSession);
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
