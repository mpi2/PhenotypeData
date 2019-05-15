package uk.ac.ebi.phenotype.service;

import javax.validation.constraints.NotNull;

import org.aspectj.lang.annotation.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;


import uk.ac.ebi.phenotype.web.dao.Statistics;
import uk.ac.ebi.phenotype.web.dao.StatisticsService;
import uk.ac.ebi.phenotype.web.dao.StatsClient;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
//@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
//@SpringBootTest(classes = {TestConfigIndexers.class})
public class StatsServiceTest {

	
	@Autowired
	private StatisticsService statsService;

	RestTemplate restTemplate;

	@Configuration
	@ComponentScan(
		basePackages = {"uk.ac.ebi.phenotype.web.dao"},
		useDefaultFilters = false,
		includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {StatisticsService.class})
		})
	static class ContextConfiguration {


	}


//	@Before
//	public void setUp() throws Exception {
//
//		RestConfiguration restConfiguration=new RestConfiguration();
//		RestTemplateBuilder builder=new RestTemplateBuilder();
//		restTemplate = restConfiguration.restTemplate(builder);
//		StatsClient client=new StatsClient(restTemplate);
//		//statsService=new StatsService(client);
//	}

	

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

	@Test
	public void testGetDataForGeneAccession() {
		ResponseEntity<PagedResources<Statistics>> statsResponse=null;
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
//
//
//	@Test
//	public void testGetDataForGeneSybmol() {
//		ResponseEntity<PagedResources<Statistics>> statsResponse=null;
//		try {
//			statsResponse = statsService.getStatsDataForGeneSymbol("Arel1");
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
//		ResponseEntity<PagedResources<Statistics>> statsResponse=null;
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
//			statsResponse = statsService.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId, pipelineStableId, zygosity, phenotypingCenter, metaDataGroup);
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
//
//	@Test
//	public void testGetSpecificExperimentDTO(){
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
//		ExperimentDTO experimentDTO = statsService.getSpecificExperimentDTOFromRest(parameterStableId, pipelineStableId, geneAccession, genderList, zyList, phenotypingCenter, strain, metadataGroup, alleleAccession, ebiMappedSolrUrl);
//		assert(experimentDTO.getAlleleAccession().equals(alleleAccession));
//		assert(experimentDTO.getMetadataGroup().equals(metadataGroup));
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
