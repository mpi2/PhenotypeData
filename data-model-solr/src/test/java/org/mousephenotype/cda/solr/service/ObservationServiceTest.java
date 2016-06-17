package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/test.properties")
public class ObservationServiceTest {

	private final Logger logger = LoggerFactory.getLogger(ObservationServiceTest.class);

	// Sring Configuration class
	// Only wire up the observation service for this test suite
	@Configuration
	@ComponentScan(
		basePackages = {"org.mousephenotype.cda"},
		useDefaultFilters = false,
		includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ObservationService.class})
		})
	static class ContextConfiguration {

		@NotNull
		@Value("${solr.host}")
		private String solrBaseUrl;

		@Bean(name = "experimentCore")
		HttpSolrServer getExperimentCore() {
			return new HttpSolrServer(solrBaseUrl + "/experiment");
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}

	@Autowired
	ObservationService observationService;


	@Test
	public void getObservationByProcedureNameAndGene(){

		String procedureName="Histopathology";
		String geneAccession="MGI:2449119";
		try {
			List<ObservationDTO> result = observationService.getObservationsByProcedureNameAndGene(procedureName, geneAccession);
			for(ObservationDTO obs:result){
				System.out.println("observations="+obs);
			}

		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void getGrossPathObservationByProcedureNameAndGene(){

		String procedureName="Gross Pathology and Tissue Collection";
		String geneAccession="MGI:2449119";
		try {
			List<ObservationDTO> result = observationService.getObservationsByProcedureNameAndGene(procedureName, geneAccession);
			assertTrue(result.size()>0);
			for(ObservationDTO obs:result){
				System.out.println("observations="+obs);
			}

		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetDistinctPipelineAlleleCenterListByGeneAccession() throws SolrServerException {

		// Arsk
		String acc = "MGI:1924291";
		List<Map<String, String>> dataMapList = observationService.getDistinctPipelineAlleleCenterListByGeneAccession(acc);

		logger.info("datamaplist: " + dataMapList);


	}


}
