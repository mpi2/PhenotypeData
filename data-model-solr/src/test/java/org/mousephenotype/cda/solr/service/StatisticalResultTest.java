package org.mousephenotype.cda.solr.service;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test the statistical result core
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigSolr.class} )
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
@Transactional
public class StatisticalResultTest {

	public static final Integer NUM_TO_TEST = 10;

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
	public void allDocumentsHaveATopLevelMpTermId() throws IOException, SolrServerException {

		//q=-top_level_mp_term_id:*&fq=status:Success&fq=-procedure_stable_id:IMPC_VIA_001&fq=-procedure_stable_id:IMPC_FER*&fq=+statistical_method:"Reference%20Ranges%20Plus%20framework"&fq=mp_term_id_options:*

		SolrQuery query = new SolrQuery("-top_level_mp_term_id:* AND -female_top_level_mp_term_id:* AND -male_top_level_mp_term_id:*")
				.addFilterQuery("status:Success")
				.addFilterQuery("-procedure_stable_id:IMPC_VIA_001")
				.addFilterQuery("-procedure_stable_id:IMPC_FER*")
				.addFilterQuery("mp_term_id_options:*")
				.addFilterQuery("data_type:unidimensional-ReferenceRange")
				.setRows(1);

		QueryResponse response = statisticalResultService.getSolrServer().query(query);
		System.out.println("query in allDocumentsHaveTopLevelmp=" + query);

		long numberFound = response.getResults().getNumFound();
		System.out.println("number of documents in SR core without a top level mp=" + numberFound);
		if(numberFound > 0) {
			System.out.println(response.getBeans(StatisticalResultDTO.class).get(0));

		}

		assertFalse(numberFound > 0);

	}

	@Test
	public void verifyDatabaseWiring() {
		System.out.println(ppDAo.getParameterByStableId("IMPC_DXA_006_001"));
		System.out.println(ppDAo.getParameterByStableId("IMPC_DXA_006_002"));
	}

	@Test
	public void verifyAllGpEntriesAppearInSrCore(){


		List<GenotypePhenotypeDTO> list = genotypePhenotypeRepository.findByProcedureStableId("IMPC_ABR_001");
		list.stream().limit(NUM_TO_TEST).forEach(x -> logger.info("Marker {}, Phenotype {}, Sex {}", x.getMarkerAccessionId(), x.getMpTermName(), x.getSex()) );
		assertTrue(list.size()>0);

		list = new ArrayList<>(genotypePhenotypeRepository.findByParameterStableId("IMPC_DXA_006_001"));

		// Randomize the list so we check a different NUM_TO_TEST for each test run
		Collections.shuffle(list);

		list.stream().limit(NUM_TO_TEST).forEach(x -> logger.info("Marker {}, Phenotype {}, Sex {}", x.getMarkerAccessionId(), x.getMpTermName(), x.getSex()) );
		assertTrue(list.size()>0);

		list.stream().limit(NUM_TO_TEST).forEach(x -> {
			List<StatisticalResultDTO> list1 = statisticalResultRepository.findByMarkerAccessionIdAndParameterStableIdAndProcedureStableIdAndPhenotypeSexAndMpTermId(x.getMarkerAccessionId(), x.getParameterStableId(), x.getProcedureStableId(), x.getSex(), x.getMpTermId());
			if (list1.size() == 0) {
				logger.warn("Genotype phenotype result for {}, which has no statistical-results entry", StringUtils.join(Arrays.asList(x.getMarkerAccessionId(), x.getParameterStableId(), x.getProcedureStableId(), x.getSex(), x.getMpTermId()), ", "));
			}
		});

	}

	@Test
	public void testGetDistinctPipelineAlleleCenterListByGeneAccession() throws SolrServerException, IOException {

		statisticalResultRepository.findByParameterStableId(null);


	}


}
