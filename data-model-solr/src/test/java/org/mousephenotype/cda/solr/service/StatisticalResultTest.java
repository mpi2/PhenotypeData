package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.repositories.GenotypePhenotypeRepository;
import org.mousephenotype.cda.solr.repositories.StatisticalResultRepository;
import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Test the statistical result core
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigSolr.class} )
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile}/test.properties"})
@Transactional
public class StatisticalResultTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	PhenotypePipelineDAO ppDAo;

	@Autowired
	StatisticalResultService statisticalResultService;

	@Autowired
	PostQcService genotypePhenotypeService;

	@Autowired
	GenotypePhenotypeRepository genotypePhenotypeRepository;

	@Autowired
	StatisticalResultRepository statisticalResultRepository;

	@Test
	public void verifyDatabaseWiring() {
		System.out.println(ppDAo.getParameterByStableId("IMPC_DXA_006_001"));
		System.out.println(ppDAo.getParameterByStableId("IMPC_DXA_006_002"));
	}

	@Test
	public void verifyAllGpEntriesAppearInSrCore(){


		List<GenotypePhenotypeDTO> list = genotypePhenotypeRepository.findByProcedureStableId("IMPC_ABR_001");
		list.stream().limit(5).forEach(x -> logger.info("Marker {}, Phenotype {}, Sex {}", x.getMarkerAccessionId(), x.getMpTermName(), x.getSex()) );
		assertTrue(list.size()>0);

		list = genotypePhenotypeRepository.findByParameterStableId("IMPC_DXA_006_001");
		list.stream().limit(5).forEach(x -> logger.info("Marker {}, Phenotype {}, Sex {}", x.getMarkerAccessionId(), x.getMpTermName(), x.getSex()) );
		assertTrue(list.size()>0);

		list.forEach( x -> {
			List<StatisticalResultDTO> list1 = statisticalResultRepository.findByMarkerAccessionIdAndParameterStableIdAndProcedureStableIdAndPhenotypeSexAndMpTermId(x.getMarkerAccessionId(), x.getParameterStableId(), x.getProcedureStableId(), x.getSex(), x.getMpTermId());
			logger.info("Size of statistical-results list for {}: \n{}", x, list1.size());
			//assertTrue(list1.size()>0);
			list1.stream().forEach(e->System.out.println(e));
		});

	}

	@Test
	public void testGetDistinctPipelineAlleleCenterListByGeneAccession() throws SolrServerException {

		statisticalResultRepository.findByParameterStableId(null);


	}


}
