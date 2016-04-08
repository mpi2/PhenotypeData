package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigSolr.class} )
@TestPropertySource(locations = {"classpath:test.properties", "file:${user.home}/configfiles/${profile}/test.properties"})
public class ObservationServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());



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
	public void testGetDistinctPipelineAlleleCenterListByGeneAccession() throws SolrServerException {

		// Arsk
		String acc = "MGI:1924291";
		List<Map<String, String>> dataMapList = observationService.getDistinctPipelineAlleleCenterListByGeneAccession(acc);

		logger.info("datamaplist: " + dataMapList);


	}


}
