package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@SpringBootTest(classes = TestConfigSolr.class)
public class ExpressionServiceTest {

	private final Logger logger = LoggerFactory.getLogger(ObservationServiceTest.class);


	@Autowired
	private ExpressionService expressionService;


	@Test
	public void getLacDataForAnatomogram() throws IOException, SolrServerException {

		expressionService.initialiseAbnormalOntologyMaps();
		String geneAccession = "MGI:1922730";

		List<Count> parameterCounts = expressionService.getLaczCategoricalParametersForGene(geneAccession);
		List<AnatomogramDataBean> beans = expressionService.getAnatomogramDataBeans(parameterCounts);
		for (AnatomogramDataBean bean : beans) {
			logger.debug("AnatomogramDataBean" + bean);
		}


		Map<String, Long> anatomogramDataBeans = expressionService.getLacSelectedTopLevelMaCountsForAnatomogram(beans);
		for (String topMa : anatomogramDataBeans.keySet()) {
			logger.debug("topMa=" + topMa + " total count " + anatomogramDataBeans.get(topMa));
		}

	}

	@Test
	public void getDataForAnatomyPage() throws SolrServerException, IOException {
		expressionService.getFacets("MA:0000004");
	}


}
