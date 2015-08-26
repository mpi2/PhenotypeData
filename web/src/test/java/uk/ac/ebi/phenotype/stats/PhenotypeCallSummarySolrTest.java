package uk.ac.ebi.phenotype.stats;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.PhenotypeCallSummary;
import org.mousephenotype.cda.solr.generic.util.PhenotypeCallSummarySolr;
import org.mousephenotype.cda.solr.generic.util.PhenotypeFacetResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.TestConfig;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/applicationTest.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class PhenotypeCallSummarySolrTest {

	@Autowired
	PhenotypeCallSummarySolr dao;

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetPhenotypeCallByAccession() throws IOException, URISyntaxException {

		String markerAccession="MGI:104874";
		PhenotypeFacetResult phenotypesResult = dao.getPhenotypeCallByGeneAccession(markerAccession);
		List<PhenotypeCallSummary> phenotypes = phenotypesResult.getPhenotypeCallSummaries();
		System.out.println(phenotypes.size());
		System.out.println(phenotypes.get(0).getPhenotypeTerm().getDescription());


	}

}
