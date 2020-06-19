package org.mousephenotype.cda.datatests.repositories.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.repositories.image.ImagesSolrJ;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class ImagesSolrjTest  {

	@Autowired
	ImagesSolrJ imagesSolrJ;

	@Test
	public void testGetIdsForKeywordsSearch() throws SolrServerException, IOException {
		List<String> result = imagesSolrJ.getIdsForKeywordsSearch("accession:MGI\\:1933365", 0, 10);
		assertTrue(result.size() > 0);
	}

	@Test
	public void testGetExperimentalFacetForGeneAccession() throws SolrServerException, IOException {
		String geneId = "MGI:1933365";
		QueryResponse solrR = imagesSolrJ.getExperimentalFacetForGeneAccession(geneId);
		assertTrue(solrR.getFacetFields().size() > 0);

	}

	@Test
	public void testGetDocsForGeneWithFacetField() throws SolrServerException, IOException {

		String geneId = "MGI:4433191";
		geneId = "MGI:97549";
		QueryResponse response = imagesSolrJ.getDocsForGeneWithFacetField(geneId, "expName", "Xray", "", 0, 5);
		assertTrue(response.getResults().size() > 0);

		for (SolrDocument doc : response.getResults()) {
			assertTrue("Image ID is null for a SOLR result", doc.getFieldValues("id") != null);
		}

		//no Histology Slide expName anymore?? what happened
		response = imagesSolrJ.getDocsForGeneWithFacetField(geneId, "expName", "Wholemount Expression", "", 0, 10);
		assertTrue(response.getResults().size() > 0);

		for (SolrDocument doc : response.getResults()) {
			assertTrue("Image ID is null for a SOLR result", doc.getFieldValues("id") != null);
		}
	}


	@Test
	public void testGetExpressionFacetForGeneAccession() throws SolrServerException, IOException {
		QueryResponse solrR = null;
		solrR = imagesSolrJ.getExpressionFacetForGeneAccession("MGI:104874");
		assertTrue(solrR.getFacetFields().size() > 0);
	}


	@Test
	public void testgetFilteredDocsForQuery() throws SolrServerException, IOException {
		String filter="expName:Wholemount Expression";
		List<String> filters=new ArrayList<String>();
		filters.add(filter);

		QueryResponse solrR= imagesSolrJ.getFilteredDocsForQuery("accession:MGI\\:1933365", filters, "", "", 0, 10);
		assertTrue(solrR.getResults().size()>0);

		QueryResponse solrR3= imagesSolrJ.getFilteredDocsForQuery("accession:MGI\\:1933365", filters, "auto_suggest", "", 0, 10);
		assertTrue(solrR3.getResults().size()>0);
	}


	@Test
	public void testProcessSpacesForSolr() throws MalformedURLException {

		Map<String,String> testcases = new HashMap<String,String>();
		testcases.put("test \"1", "\"test \\\"1\"");
		testcases.put("test 1", "\"test 1\"");
		testcases.put("*", "*");
		testcases.put("asdf *asdf", "\"asdf *asdf\"");
		testcases.put("test: test", "\"test: test\"");
		testcases.put("\"te st", "\"\\\"te st\"");
		testcases.put("\"te st\"", "\"te st\"");

        for(Map.Entry<String, String> testcase : testcases.entrySet()) {
            String key = testcase.getKey();
            String expected = testcase.getValue();
            String processed = imagesSolrJ.processValueForSolr(key);
            assertEquals(expected, processed);
        }
	}
}