package uk.ac.ebi.phenotype.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.neo4j.entity.Allele;
import org.mousephenotype.cda.neo4j.entity.Gene;
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

import static org.junit.Assert.assertTrue;

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

    @Autowired
    @Qualifier("allele2Core")
    private SolrClient allele2Core;

    @Autowired
    @Qualifier("statisticalResultCore")
    private SolrClient statisticalResultCore;

    @Autowired
    @Qualifier("genotypePhenotypeCore")
    private SolrClient genotypePhenotypeCore;

    private AdvancedSearchService advancedSearchService;

    @Before
    public void setUp() {
        advancedSearchService = new AdvancedSearchService(gpService, autosuggestCore, neo4jSession);
    }


    @Test
    public void testGenesByMpTermFromNeo4jAndSolr() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException, SolrServerException, IOException, InterruptedException {

        AdvancedSearchPhenotypeForm mpForm = new AdvancedSearchPhenotypeForm();

        String mpTerm = "cardiovascular system phenotype";
        //String mpTerm = "abnormal glucose homeostasis";
        //String mpTerm = "abnormal retina morphology";

        //AdvancedSearchMpRow mpRow = new AdvancedSearchMpRow("abnormal glucose homeostasis", 0.00001, 0.0001);
        AdvancedSearchMpRow mpRow = new AdvancedSearchMpRow(mpTerm, null, null);
        mpForm.addPhenotypeRows(mpRow);
        mpForm.setSignificantPvaluesOnly(true);
//        mpForm.setHasOutputColumn(true);
//        mpForm.setShowMpTerm(true);
        //System.out.println("MP FORM: " + mpForm.toString());

        AdvancedSearchGeneForm geneForm = new AdvancedSearchGeneForm();
        //geneForm.setChrIds(Arrays.asList("1"));
        //System.out.println("GENE FORM: " + geneForm.toString());

        AdvancedSearchDiseaseForm diseaseForm = new AdvancedSearchDiseaseForm();
        //System.out.println("DISEASE FORM: " + diseaseForm.toString());

        String fileType = null; // only needed for export

        List<Object> objects = advancedSearchService.fetchGraphDataAdvSrchResult(mpForm, geneForm, diseaseForm, fileType);
        Result result = (Result) objects.get(0);

        Set<String> n4jGeneSymbols = new HashSet<>();
        Set<String> n4jAlleleSymbols = new HashSet<>();

        int genesFoundNeo4j = 0;
        // do something with the result
        for (Map<String, Object> row : result) {
            //System.out.println("row="+row);
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().toString().startsWith("[Ljava.lang.Object")) {
                    List<Object> objs = (List<Object>) entry.getValue();

                    //System.out.println(entry.getKey() + " -- " + objs.size());
                    if (entry.getKey().equals("collect(distinct g)")) {
                        genesFoundNeo4j = objs.size();

                        for (Object obj : objs) {
                            //System.out.println(obj);
                            Gene g = (Gene) obj;
                            //System.out.println("check: "+ g.getMarkerSymbol());
                            n4jGeneSymbols.add(g.getMarkerSymbol());
                        }
                    }
                    else if (entry.getKey().equals("collect(distinct a)")) {
                        genesFoundNeo4j = objs.size();

                        for (Object obj : objs) {
                            //System.out.println(obj);
                            Allele a = (Allele) obj;
                            n4jAlleleSymbols.add(a.getAlleleSymbol());
                        }
                    }
                }
            }
        }

        // get result from SOLR
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addFilterQuery("mp_term_name:\"" + mpTerm + "\" OR intermediate_mp_term_name:\"" + mpTerm + "\" OR top_level_mp_term_name:\"" + mpTerm + "\"");
        query.setStart(0);
        query.setRows(99999);
        query.setParam("fl", "marker_symbol, allele_symbol", "mp_term_name", "intermediate_mp_term_name", "top_level_mp_term_name");

        System.out.println("SOLR QUERY: " + query);
        QueryResponse response = genotypePhenotypeCore.query(query);
        //System.out.println("response: " + response);

        Set<String> geneSymbolsGP = new HashSet<>();
        Set<String> alleleSymbolsGP = new HashSet<>();
        Map<String, String> alleleMp = new HashMap<>();

        //long genesFoundSolr = response.getResults().getNumFound();
        for (SolrDocument doc : response.getResults()) {
            String makerSymbol = doc.getFieldValue("marker_symbol").toString();
            String alleleSymbol = doc.getFieldValue("allele_symbol").toString();
            geneSymbolsGP.add(makerSymbol);
            alleleSymbolsGP.add(alleleSymbol);
            //alleleMp.put(alleleSymbol, )
        }

        int genesFoundSolr = geneSymbolsGP.size();

        System.out.println("\nCompare number of unique genes associated with phenotype '" + mpTerm + "'\nfrom Neo4j and SOLR genotype-phenotype core:\n");
        String comp = genesFoundNeo4j == genesFoundSolr ? "SUCCESS: " : "FAILED: ";
        comp += "Neo4j [" + genesFoundNeo4j + "] vs SOLR: [" + genesFoundSolr + "]";
        System.out.println(comp + "\n\n");
        assertTrue(genesFoundNeo4j==genesFoundSolr);

        Set<String> missingGeneSymbolN4j = new HashSet<>();
        Set<String> missingAlleleSymbolN4j = new HashSet<>();

        for (String gs : geneSymbolsGP){
            if ( ! n4jGeneSymbols.contains(gs)){
                missingGeneSymbolN4j.add(gs);
            }
        }

        for (String as : alleleSymbolsGP){
            if ( ! n4jAlleleSymbols.contains(as)){
                missingAlleleSymbolN4j.add(as);
            }
        }

        Set intersect = new TreeSet(n4jGeneSymbols);
        intersect.retainAll(geneSymbolsGP);

        System.out.println(intersect.size() + " common gene symbols: ");
        System.out.println(intersect);
        System.out.println();

        System.out.println(missingGeneSymbolN4j.size() + " Neo4j gene symbols not in GP");
        System.out.println(missingGeneSymbolN4j);
        System.out.println();
        System.out.println(missingAlleleSymbolN4j.size() + " Neo4j allele symbols not in GP");
        System.out.println(missingAlleleSymbolN4j);

    }

    @Test
    public void testMarkerSymbolsInGPAndStatisticalResultCoreWithMp() throws IOException, SolrServerException {

        String mpTerm = "cardiovascular system phenotype";
        //String mpTerm = "abnormal glucose homeostasis";
        //String mpTerm = "abnormal retina morphology";
        //String mpTerm = "rib fusion";

        // get result from SOLR
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.addFilterQuery("mp_term_name:\"" + mpTerm + "\" OR intermediate_mp_term_name:\"" + mpTerm + "\" OR top_level_mp_term_name:\"" + mpTerm + "\"");
        query.setStart(0);
        query.setRows(99999);
        query.setParam("fl", "marker_symbol");

        System.out.println("SOLR QUERY: " + query);
        QueryResponse response = genotypePhenotypeCore.query(query);
        //System.out.println("response: " + response);

        Set<String> geneSymbolsGP = new HashSet<>();

        for (SolrDocument doc : response.getResults()){
            geneSymbolsGP.add(doc.getFieldValue("marker_symbol").toString());
        }

        // ignores significant:true

        SolrQuery query2 = new SolrQuery();
        query2.setQuery("*:*");
//        query2.addFilterQuery("(mp_term_name:\"" + mpTerm + "\" OR intermediate_mp_term_name:\"" + mpTerm + "\" OR top_level_mp_term_name:\"" + mpTerm
//                + "\" OR female_mp_term_name:\"" + mpTerm + "\" OR female_intermediate_mp_term_name:\"" + mpTerm + "\" OR female_top_level_mp_term_name:\"" + mpTerm
//                + "\" OR male_mp_term_name:\"" + mpTerm + "\" OR male_intermediate_mp_term_name:\"" + mpTerm + "\" OR male_top_level_mp_term_name:\"" + mpTerm + "\")");
        query2.addFilterQuery("significant:true AND (mp_term_name:\"" + mpTerm + "\" OR intermediate_mp_term_name:\"" + mpTerm + "\" OR top_level_mp_term_name:\"" + mpTerm
                + "\" OR female_mp_term_name:\"" + mpTerm + "\" OR female_intermediate_mp_term_name:\"" + mpTerm + "\" OR female_top_level_mp_term_name:\"" + mpTerm
                + "\" OR male_mp_term_name:\"" + mpTerm + "\" OR male_intermediate_mp_term_name:\"" + mpTerm + "\" OR male_top_level_mp_term_name:\"" + mpTerm + "\")");
        query2.setRows(99999);
        query2.setParam("fl", "marker_symbol");

        System.out.println("SOLR QUERY: " + query2);
        QueryResponse response2 = statisticalResultCore.query(query2);
        //System.out.println("response: " + response);

        Set<String> geneSymbolsSR= new HashSet<>();

        for (SolrDocument doc : response2.getResults()){
            geneSymbolsSR.add(doc.getFieldValue("marker_symbol").toString());
        }

        System.out.println("Test consistency of MP to gene association of GP and SR cores:\n");
        System.out.println("Unique gene symbols in GP core: " + geneSymbolsGP.size());
        System.out.println("Unique gene symbols in SR core: " + geneSymbolsSR.size());

        int missing = 0;
        for (String gs : geneSymbolsGP){
            if (! geneSymbolsSR.contains(gs) ){
                System.out.println(gs + " not in SR core");
                missing++;
            }
        }

        System.out.println();
        if (missing == 0){
            System.out.println("SUCCESS: '" + mpTerm + "' associated with genes consistent in GP core and SR core ");
        }
        else {
            System.out.println("FAILED: '" + mpTerm + "' associated with genes INconsistent in GP core and SR core ");
        }
        assertTrue(missing==0);
        


    }

    @Test
    public void testMarkerSymbolsInGPCoreMissingInAllele2Core() throws IOException, SolrServerException {

        // get result from SOLR
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setStart(0);
        query.setRows(99999);
        query.setParam("fl", "marker_symbol");
        query.setParam("fq", "type:Allele");

        System.out.println("SOLR QUERY: " + query);
        QueryResponse response = allele2Core.query(query);
        //System.out.println("response: " + response);

        Set<String> geneSymbolsAllele2 = new HashSet<>();

        for (SolrDocument doc : response.getResults()){
            geneSymbolsAllele2.add(doc.getFieldValue("marker_symbol").toString());
        }

        SolrQuery query2 = new SolrQuery();
        query2.setQuery("marker_symbol:*");
        query2.setStart(0);
        query2.setRows(99999);
        query2.setParam("fl", "marker_symbol, allele_symbol");

        System.out.println("SOLR QUERY: " + query2);
        QueryResponse response2 = genotypePhenotypeCore.query(query2);


        Set<String> geneSymbolsGP = new HashSet<>();
        Map<String, String> geneAllele = new HashMap<>();

        for (SolrDocument doc : response2.getResults()){
            String gs = doc.getFieldValue("marker_symbol").toString();
            geneSymbolsGP.add(gs);

            geneAllele.put(gs, doc.getFieldValue("allele_symbol").toString());
        }

        Set<String> missingGeneSymbolsAllele2 = new HashSet<>();

        // gene symbols in GP BUT NOT in Allele2
        for(String gs : geneSymbolsGP){
            if (! geneSymbolsAllele2.contains(gs)){
                missingGeneSymbolsAllele2.add(gs);
            }
        }


        if (missingGeneSymbolsAllele2.size() > 0) {
            System.out.println("FAILED: " + missingGeneSymbolsAllele2.size() + " GP core gene symbols NOT in Allele2 core:");
            System.out.println(missingGeneSymbolsAllele2 + "\n");
        }
        else {
            System.out.println("SUCCESS");
        }
        assertTrue(missingGeneSymbolsAllele2.size()==0);

    }

    @Test
    public void testAllelesInSRCoreMissingInAllele2Core() throws IOException, SolrServerException {

        // get result from SOLR
        SolrQuery query = new SolrQuery();
        query.setQuery("*:*");
        query.setStart(0);
        query.setRows(99999);
        query.setParam("fl", "allele_symbol");
        query.setParam("fq", "type:Allele");

        System.out.println("SOLR QUERY: " + query);
        QueryResponse response = allele2Core.query(query);
        //System.out.println("response: " + response);

        Set<String> alleleSymbolsAllele2 = new HashSet<>();

        for (SolrDocument doc : response.getResults()){
            alleleSymbolsAllele2.add(doc.getFieldValue("allele_symbol").toString());
        }

        SolrQuery query2 = new SolrQuery();
        query2.setQuery("marker_symbol:*");
        query2.setStart(0);
        query2.setRows(99999);
        query2.setParam("fl", "allele_symbol");

        System.out.println("SOLR QUERY: " + query2);
        QueryResponse response2 = statisticalResultCore.query(query2);


        Set<String> alleleSymbolsSR = new HashSet<>();

        for (SolrDocument doc : response2.getResults()){
            alleleSymbolsSR.add(doc.getFieldValue("allele_symbol").toString());
        }

        Set<String> missingAlleleSymbolsAllele2 = new HashSet<>();

        // gene symbols in Allele2 BUT NOT in GP
        for(String gs : alleleSymbolsSR){
            if (! alleleSymbolsAllele2.contains(gs)){
                missingAlleleSymbolsAllele2.add(gs);
            }
        }

        if (missingAlleleSymbolsAllele2.size() > 0) {
            System.out.println("FAILED: " + missingAlleleSymbolsAllele2.size() + " SR core allele symbols NOT in Allele2 core:");
            System.out.println(missingAlleleSymbolsAllele2 + "\n");
        }
        else {
            System.out.println("SUCCESS");
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

