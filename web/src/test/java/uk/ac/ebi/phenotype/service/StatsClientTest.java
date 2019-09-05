package uk.ac.ebi.phenotype.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import uk.ac.ebi.phenotype.stats.model.Statistics;
import uk.ac.ebi.phenotype.web.dao.StatsClient;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
public class StatsClientTest {
	
	StatsClient statsClient;
	private static String statisticsUrl="http://localhost:8080/";
	//private static String statisticsUrl="http://ves-ebi-d1.ebi.ac.uk:8091/";
	@Configuration
	static class ContextConfiguration {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}
	}

	@Before
	public void setUp() throws Exception {

		
		statsClient=new StatsClient(statisticsUrl);
	}

//	@Test
//	public void testGetTestStatsData() {
//		ResponseEntity<PagedResources<Statistics>> statsResponse=null;
//		try {
//			statsResponse = statsService.getStatsData(0, 2);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("test response ="+statsResponse);
//		System.out.println("stats list="+statsResponse.getBody().getContent());
//		assert(statsResponse.getBody().getContent().size()==2);
//	}

//	@Test
//	public void testGetDataForGeneAccession() {
//		ResponseEntity<PagedResources<Statistics>> statsResponse=null;
//		try {
//			statsResponse = statsClient.getStatsDataForGeneAccession("MGI:2443170");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("test response ="+statsResponse);
//		System.out.println("stats list="+statsResponse.getBody().getContent());
//		assert(statsResponse.getBody().getContent().size()==1);
//	}
//
//
//	@Test
//	public void testGetDataForGeneSybmol() {
//		
//		// this currently works http://localhost:8080/statisticses/search/findByGeneSymbol?geneSymbol=Arel1
//		ResponseEntity<PagedResources<Statistics>> statsResponse=null;
//		try {
//			statsResponse = statsClient.getStatsDataForGeneSymbol("Ces2e");//Ces2e, Arel1
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("test response ="+statsResponse);
//		System.out.println("stats list="+statsResponse.getBody().getContent());
//		assert(statsResponse.getBody().getContent().size()==1);
//	}
//
//	@Test
//	public void testGetIndividualStatsData() {
//		ResponseEntity<List<Statistics>> statsResponse=null;
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
//			statsResponse = statsClient.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId, pipelineStableId, zygosity, phenotypingCenter, metaDataGroup);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println("test response ="+statsResponse);
//		assertTrue(statsResponse.getBody().get(0).getGeneAccession()!=null);
//	}

//	@Test
//	public void testGetSpecificExperimentDTO(){
//
//		 //[id=null, parameterStableId=IMPC_HEM_038_001, parameterStableName=Basophil differential count, pipelineStableId=IMPC_001, allele=Ces2e<em1(IMPC)Marc>, geneSymbol=Ces2e, phenotypingCenter=MARC, geneAccession=MGI:2443170, alleleAccession=MGI:2159965, metaDataGroup=08aa37a898ab923b9ffdbd01c0077040, zygosity=homozygote, colonyId=Ces2e_cas9_del68, impressParameterKey=0, impressProtocolKey=0, result=Result [vectoroutput=null, details=Details [responseType=data_point_of_Type_unidimensional, rawDataSummaryStatistics=RawSummaryStatistics [femaleControl=BasicStats [count=472, mean=0.039978813, sd=0.0869695], maleControl=BasicStats [count=444, mean=0.048581082, sd=0.077982545], femaleExperimental=BasicStats [count=7, mean=0.06285714, sd=0.12065299], maleExpreimental=BasicStats [count=7, mean=0.7228571, sd=0.81624573]], originalSex=null, originalBiologicalSampleGroup=null, originalResponse=null, originalDateOfExperiment=null, originalBodyWeight=null]], headerInfo=null]]
//
//		String pipelineStableId="IMPC_001";
//		String phenotypingCenter="MARC";
//		List<String> zyList=new ArrayList<>();//do we ignore these now as the stats contains data for both
//		List<String> genderList=new ArrayList<>();//do we ignore these now as the stats contains data for both
//		String parameterStableId="IMPC_HEM_038_001";;
//		String geneAccession="MGI:2443170";
//		String alleleAccession="MGI:2159965";
//		String metadataGroup= "08aa37a898ab923b9ffdbd01c0077040";
//		String ebiMappedSolrUrl="//ves-ebi-d0.ebi.ac.uk:8986/solr";
//		String strain="";//we hve colonyId now so what do we do with this?
//		String zygosity="homozygote";
//		ResponseEntity<List<Statistics>> responseEntity = statsClient.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId, pipelineStableId, zygosity, phenotypingCenter, metadataGroup);
//		
//		System.out.println("stats size="+responseEntity.getBody().size());
//		assert(responseEntity.getBody().get(0).getGeneAccession()!=null);
//		//assert(experimentDTO.getMetadataGroup().equals(metadataGroup));
//
//
////		List<Stats> stats = repo.findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup(geneAccession, alleleAccession, parameterStableId, pipelineStableId, "homozygote", phenotypingCenter,  metadataGroup);
//////		@Param("geneAccession") String geneAccession, @Param("alleleAccession") String alleleAccession, @Param("parameterStableId") String parameterStableId,
//////		@Param("pipelineStableId") String pipelineStableId,  @Param("zygosity") String zygosity, @Param("phenotypingCenter") String phenotypingCenter, @Param("metaDataGroup") String metaDataGroup);
////		assert(stats.get(0).getAlleleAccession().equals(alleleAccession));
////		assert((stats.get(0).getMetaDataGroup().equals(metadataGroup)));
//
//	}
}
