package uk.ac.ebi.phenotype.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.mousephenotype.cda.config.TestConfigIndexers;
import org.mousephenotype.cda.file.stats.Stats;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
//@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
//@SpringBootTest(classes = {TestConfigIndexers.class})
public class StatsServiceTest {

	
	
	private StatsService statsService;
	
	RestTemplate restTemplate;

	@Before
	public void setUp() throws Exception {
		
		RestConfiguration restConfiguration=new RestConfiguration();
		RestTemplateBuilder builder=new RestTemplateBuilder();
		restTemplate = restConfiguration.restTemplate(builder);
		StatsClient client=new StatsClient(restTemplate);
		statsService=new StatsService(client);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTestStatsData() {
		ResponseEntity<PagedResources<Stats>> statsResponse=null;
		try {
			statsResponse = statsService.getStatsData(0, 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("test response ="+statsResponse);
		System.out.println("stats list="+statsResponse.getBody().getContent());
		assert(statsResponse.getBody().getContent().size()==2);
	}
	
	@Test
	public void testGetDataForGeneAccession() {
		ResponseEntity<PagedResources<Stats>> statsResponse=null;
		try {
			statsResponse = statsService.getStatsDataForGeneAccesssion("MGI:2443170");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("test response ="+statsResponse);
		System.out.println("stats list="+statsResponse.getBody().getContent());
		assert(statsResponse.getBody().getContent().size()==1);
	}
	
	
	@Test
	public void testGetDataForGeneSybmol() {
		ResponseEntity<PagedResources<Stats>> statsResponse=null;
		try {
			statsResponse = statsService.getStatsDataForGeneSymbol("Arel1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("test response ="+statsResponse);
		System.out.println("stats list="+statsResponse.getBody().getContent());
		assert(statsResponse.getBody().getContent().size()==1);
	}
	
	@Test
	public void testGetIndividualStatsData() {
		ResponseEntity<PagedResources<Stats>> statsResponse=null;
		String geneAccession="MGI:2443170";
		String alleleAccession="MGI:2159965";
		String parameterStableId="IMPC_HEM_038_001";
		String pipelineStableId="IMPC_001";
		String zygosity="homozygote";
		String phenotypingCenter="MARC";
		String metaDataGroup= "08aa37a898ab923b9ffdbd01c0077040";
		
		
		try {
			statsResponse = statsService.findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup(geneAccession, alleleAccession, parameterStableId, pipelineStableId, zygosity, phenotypingCenter, metaDataGroup);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("test response ="+statsResponse);
		System.out.println("stats list="+statsResponse.getBody().getContent());
		assert(statsResponse.getBody().getContent().size()==1);
	}
	
	
//	@Test
//	public void testGetIndividualStatsDataHalfQuery() {
//		ResponseEntity<PagedResources<Stats>> statsResponse=null;
//		String geneAccession="MGI:2443170";
//		String alleleAccession="MGI:2159965";
//		String parameterStableId="IMPC_HEM_038_001";
//		String pipelineStableId="IMPC_001";
//		String zygosity="homozygote";
//		String phenotypingCenter="MARC";
//		String metaDataGroup= "08aa37a898ab923b9ffdbd01c0077040";
//		
//		
//		try {
//			statsResponse = statsService.findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosity(geneAccession, alleleAccession, parameterStableId, pipelineStableId, zygosity, phenotypingCenter, metaDataGroup);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("test response ="+statsResponse);
//		System.out.println("stats list="+statsResponse.getBody().getContent());
//		assert(statsResponse.getBody().getContent().size()==1);
//	}

}
