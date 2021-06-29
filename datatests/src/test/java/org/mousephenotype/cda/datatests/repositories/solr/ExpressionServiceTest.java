package org.mousephenotype.cda.datatests.repositories.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mousephenotype.cda.solr.service.AnatomogramDataBean;
import org.mousephenotype.cda.solr.service.ExpressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class ExpressionServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Autowired
	private ExpressionService expressionService;


	@Test
	public void getLacDataForAnatomogram() throws IOException, SolrServerException {

		String geneAccession = "MGI:1922730";

		List<Count>               parameterCounts = expressionService.getLaczCategoricalParametersForGene(geneAccession);
		List<AnatomogramDataBean> beans           = expressionService.getAnatomogramDataBeans(parameterCounts);
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