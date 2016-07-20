package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigSolr.class})
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/application.properties")
public class ExpressionServiceTest {


	@Autowired
	private ExpressionService expressionService;

	@Autowired
	ExperimentService experimentService;


	@Test
	public void getLacDataForAnatomogram() {

		expressionService.initialiseAbnormalOntologyMaps();
		String geneAccession = "MGI:1922730";
		try {
			List<Count> parameterCounts = expressionService.getLaczCategoricalParametersForGene(geneAccession);
			List<AnatomogramDataBean> beans = expressionService.getAnatomogramDataBeans(parameterCounts);
			for (AnatomogramDataBean bean : beans) {
				System.out.println("AnatomogramDataBean" + bean);
			}


			Map<String, Long> anatomogramDataBeans = expressionService.getLacSelectedTopLevelMaCountsForAnatomogram(beans);
			for (String topMa : anatomogramDataBeans.keySet()) {
				System.out.println("topMa=" + topMa + " total count " + anatomogramDataBeans.get(topMa));
			}

		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getDataForAnatomyPage() throws SolrServerException {
		expressionService.getFacets("MA:0000004");
	}


}
