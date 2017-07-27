package uk.ac.ebi.phenotype.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import org.neo4j.ogm.model.Result;
import uk.ac.ebi.phenotype.web.Neo4jConfig;
import uk.ac.ebi.phenotype.web.TestAdvancedSearchConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestAdvancedSearchConfig.class, Neo4jConfig.class})
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
        advancedSearchService = new AdvancedSearchService(gpService, autosuggestCore, neo4jSession);
    }

    @Test
    public void testGenesByMpTerm() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, SolrServerException, IOException, InterruptedException {

        AdvancedSearchPhenotypeForm mpForm = new AdvancedSearchPhenotypeForm();

        //AdvancedSearchMpRow mpRow = new AdvancedSearchMpRow("abnormal glucose homeostasis", 0.00001, 0.0001);
        AdvancedSearchMpRow mpRow = new AdvancedSearchMpRow("abnormal retina morphology", null, null);
        mpForm.addPhenotypeRows(mpRow);
//        mpForm.setHasOutputColumn(true);
//        mpForm.setShowMpTerm(true);
        System.out.println("MP FORM: " + mpForm.toString());

        AdvancedSearchGeneForm geneForm = new AdvancedSearchGeneForm();
        //geneForm.setChrIds(Arrays.asList("1"));
        System.out.println("GENE FORM: " + geneForm.toString());

        AdvancedSearchDiseaseForm diseaseForm = new AdvancedSearchDiseaseForm();
        System.out.println("DISEASE FORM: " + diseaseForm.toString());

        String fileType = null; // only needed for export

        List<Object> objects = advancedSearchService.fetchGraphDataAdvSrchResult(mpForm, geneForm, diseaseForm, fileType);
        Result result = (Result) objects.get(0);

        // do something with the result
        for (Map<String,Object> row : result) {
        	//System.out.println("row="+row);
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getValue() != null && ! entry.getValue().toString().startsWith("[Ljava.lang.Object")) {
                    List<Object> objs = (List<Object>) entry.getValue();

                    System.out.println(entry.getKey() + " -- " + objs.size());
                    //for(Object obj: objs){
                    //System.out.println(obj);
                    //}
                }
            }
        }


    }


//	@Test
//	public void getResult(){
//
//		AdvancedSearchPhenotypeForm advancedSearchPhenotypeForm =new AdvancedSearchPhenotypeForm();
//		AdvancedSearchMpRow row=new AdvancedSearchMpRow("abnormal circulating glucose level");//default cuttoff
//
//		advancedSearchPhenotypeForm.addPhenotypeRows(row);
//		List<String> dataTypes=new ArrayList<>();
//		dataTypes.add("g");
//		String hostname="http://localhost";
//		String baseUrl="/";
//		JSONObject jParams = null;//@TODO need to set a dummy object here still
//
//		Boolean significantPValue=true;
//		SexType sexType=SexType.both;
//		String impressParameter="";
//
//		Double lowerPvalue = null;
//		Double upperPvalue = null;
//		List<String> chrs = new ArrayList<>();
//		Integer regionStart = null;
//		Integer regionEnd = null;
//		boolean isMouseGenes = false;
//		List<String> geneList = new ArrayList<>();
//		List<String> genotypeList =  new ArrayList<>();
//		List<String> alleleTypes =  new ArrayList<>();
//		List<String> mutationTypes =  new ArrayList<>();
//		int phenodigmScoreLow = 0;
//		int phenodigmScoreHigh = 0;
//		String diseaseTerm="";
//		boolean humanCurated=false;
//		String fileType = null;
//		JSONObject jcontent=null;
//		String humanDiseaseTerm="";//not sure what diseaseTerm is if we have a humand one and disease one?
//		String mpStr="abnormal circulating glucose level";//what is this?
//
//		try {
//			jcontent = advancedSearchService.fetchGraphDataAdvSrchResult(advancedSearchPhenotypeForm, dataTypes, hostname, baseUrl,jParams, significantPValue, sexType, impressParameter, lowerPvalue, upperPvalue, chrs, regionStart, regionEnd, isMouseGenes, geneList, genotypeList, alleleTypes, mutationTypes, phenodigmScoreLow, phenodigmScoreHigh, diseaseTerm, humanCurated, humanDiseaseTerm, mpStr, fileType);
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SolrServerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("jcontent from test is "+jcontent);
//	}
//
//	@Test
//    public void testGetGenesForMpId(){
//
//		//Test "abnormal glucose homeostasis" only
//
//		AdvancedSearchService searchSolrDao=new AdvancedSearchService(gpService, autosuggestCore, neo4jSession);
//    	String phenotypeId="MP:0002078";
//    	List<String> geneSymbols=null;
//    	try {
//			geneSymbols=searchSolrDao.getGenesForPhenotype(phenotypeId);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SolrServerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	//currently 4056 genes in the gp core are associated to abnormal glucose Homeostasis
//    	System.out.println("geneSymbols size for phenotype is "+geneSymbols.size());
//    	assertTrue(geneSymbols.size()>405);
//
//
//    }
//
//	@Test
//	public void testGetGenesForPhenotypeAndPhenotype(){
//		String phenotypeId="MP:0002078";//"abnormal glucose homeostasis
//		String phenotypeId2="MP:0003956";//abnormal body size
//		List<String> resultsOfAndQuery=null;
//		try {
//			resultsOfAndQuery=advancedSearchService.getGenesForPhenotypeAndPhenotype(phenotypeId, phenotypeId2);
//		} catch (IOException | URISyntaxException | SolrServerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("resultsOfAndQuery size for phenotype is "+resultsOfAndQuery.size());
//		assertTrue(resultsOfAndQuery.size()>0);
////		for(String geneSymbol: resultsOfAndQuery){
////			System.out.println("intersection symbol is "+geneSymbol);
////		}
//		//Ncald is a gene contained on both phenotype pages. 57 currently in common
//	}
//
//	@Test
//	public void testGetGenesForPhenotypeORPhenotype(){
//		String phenotypeId="MP:0002078";//"abnormal glucose homeostasis
//		String phenotypeId2="MP:0003956";//abnormal body size
//		Collection<String> resultsOfAndQuery=null;
//		try {
//			resultsOfAndQuery=advancedSearchService.getGenesForPhenotypeORPhenotype(phenotypeId, phenotypeId2);
//		} catch (IOException | URISyntaxException | SolrServerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("resultsOfORQuery size for phenotype is "+resultsOfAndQuery.size());
//		assertTrue(resultsOfAndQuery.size()>=680);
////		for(String geneSymbol: resultsOfAndQuery){
////			System.out.println("union symbol is "+geneSymbol);
//		//}
//		//Ncald is a gene contained on both phenotype pages. 57 currently in common 331 + 406 -(57)=680
//	}

}

