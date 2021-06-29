package org.mousephenotype.cda.datatests.repositories.solr;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mousephenotype.cda.solr.service.AlleleService;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.test.util.AssertionErrors.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class AlleleServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AlleleService alleleService;

	@Test
	public void testGetStatusCount() throws Exception {

		logger.debug("Testing the getStatusCount method");

		// 2015-07-06 Data comes from the current allele core
		Set<String> genes = new HashSet<>();
		genes.add("MGI:1328357");
		genes.add("MGI:2442115");
		genes.add("MGI:105090");
		final int expectedStatusCount = genes.size();

		final HashMap<String, Long> actualStatusCount = alleleService.getStatusCount(genes, AlleleDTO.LATEST_PHENOTYPE_STATUS);
		logger.debug("status count is: " + actualStatusCount);

		assertTrue("Expected at least " + expectedStatusCount + " 'Phenotyping Complete' statuses but found " + actualStatusCount, actualStatusCount.get("Phenotyping Complete") >= expectedStatusCount);
	}
}