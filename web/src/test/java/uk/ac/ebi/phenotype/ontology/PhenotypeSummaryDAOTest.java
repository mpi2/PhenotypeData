/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This test class is intended to run healthchecks against the observation table.
 */

package uk.ac.ebi.phenotype.ontology;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.ebi.phenotype.TestConfig;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/applicationTest.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class PhenotypeSummaryDAOTest {

	@Autowired
	private PhenotypeSummaryDAO phenotypeSummary;

	@Autowired
	PostQcService gpService;

	String testGene = "MGI:104874";

	@Test
	public void testPhenotypeSummaryForAllGenes(){
		System.out.println( ">> testPhenotypeSummaryForAllGenes");
		try {
			System.out.println(phenotypeSummary.getSummaryObjects("*").getFemalePhenotypes().size());
		} catch (SolrServerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
		System.out.println(">> done.");
	}

	@Test
	public void testGetSexesRepresentationForPhenotypesSet() throws MalformedURLException, SolrServerException{
		HashMap<String, String> summary;
		summary = gpService.getTopLevelMPTerms(testGene, null);
		for (String id: summary.keySet()){

			SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(testGene, id, null);
			String sex = phenotypeSummary.getSexesRepresentationForPhenotypesSet(resp);
			assertTrue(sex != null);
			assertTrue(sex.equalsIgnoreCase("male") || sex.equalsIgnoreCase("female") || sex.equalsIgnoreCase("both sexes"));
			}

	}

	@Test
	public void testGetDataSourcesForPhenotypesSet() throws MalformedURLException, SolrServerException{
		HashMap<String, String> summary;
		summary = gpService.getTopLevelMPTerms(testGene, null);
		for (String id: summary.keySet()){
			SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(testGene, id, null);
			HashSet<String> dataSources = phenotypeSummary.getDataSourcesForPhenotypesSet(resp);
			assertTrue(dataSources != null);
		}
	}

	@Test
	public void testNonExistingGeneName() throws SolrServerException, MalformedURLException{
		System.out.println("Testing inexisting gene name...");
		String gene = "ilincasMadeUpGene";
		phenotypeSummary = new PhenotypeSummaryDAOImpl();
		try {
			assertTrue(phenotypeSummary.getSummaryObjects(gene)==null);
//			assertFalse(phenotypeSummary.getSummaryObjects(gene).getBothPhenotypes().size() > 0 ||
//					phenotypeSummary.getSummaryObjects(gene).getMalePhenotypes().size() > 0 ||
//					phenotypeSummary.getSummaryObjects(gene).getFemalePhenotypes().size() > 0);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}


	//removed this test until the mammalian phenotype top level issue is resolved
//	@Test
//	public void testGetSummaryObjectsForAllGenesInSolr() throws MalformedURLException, SolrServerException{
//
//		String topLevelsMP = "MP:0001186$MP:0002006$MP:0002873$MP:0003012$MP:0003631$MP:0005367$MP:0005369$MP:0005370$MP:0005371$MP:0005375$MP:0005376$MP:0005"
//				+ "377$MP:0005378$MP:0005379$MP:0005380$MP:0005381$MP:0005382$MP:0005384$MP:0005385$MP:0005386$MP:0005387$MP:0005388$MP:0005389$MP:000"
//				+ "5390$MP:0005391$MP:0005394$MP:0005395$MP:0005397$MP:0010768$MP:0010771$";
//
//		int noSexDocs_temp = 0;
//		long allDocs = 0;
//		// put the genes in a hashSet to get rid of duplicates
//
//		HashMap<String, String> summary;
//		for (String gene: gpService.getAllGenesWithPhenotypeAssociations()){
//			System.out.println("Test gene: " + gene);
//			//test getTopLevelMPTerms
//			summary = gpService.getTopLevelMPTerms(gene);
//			assertTrue(summary.size() > 0);	// we're sure there are entries for gene Akt2
//			for (String id : summary.keySet()) {
//				assertTrue("MP top level id must start with \'MP\'", id.startsWith("MP"));	// these should be only MP ids, not something else
//				// check it is indeed a top level term
//				assertTrue(gene+" MP id returned as top level seems it is actually not top level: " + id , topLevelsMP.contains(id));
//			}
//			// test getPhenotypesForTopLevelTerm
//			for (String id: summary.keySet()){
//				SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id);
//				assertTrue (resp != null);
//				assertTrue (resp.size() > 0);
//			}
//			// test getSexesRepresentationForPhenotypesSet
//			for (String id: summary.keySet()){
//				SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id);
//				String sex = phenotypeSummary.getSexesRepresentationForPhenotypesSet(resp);
//				if (sex == null){
//					System.out.println("Sex field missing: " + gene + " " + id);
//					noSexDocs_temp += resp.getNumFound();
//				assertTrue(sex != null);
//				assertTrue(sex.equalsIgnoreCase("male") || sex.equalsIgnoreCase("female") || sex.equalsIgnoreCase("both sexes"));
//					System.out.println("+++" + noSexDocs_temp);
//				}
//			}
//			// test getDataSourcesForPhenotypesSet
//			for (String id: summary.keySet()){
//				SolrDocumentList resp = gpService.getPhenotypesForTopLevelTerm(gene, id);
//				HashSet<String> dataSources = phenotypeSummary.getDataSourcesForPhenotypesSet(resp);
//				assertTrue(dataSources != null);
//			}
//
//			// test getSummaryObjects for all
//			try {
//				phenotypeSummary.getSummaryObjects(gene);
//			} catch (Exception e) {
//				e.printStackTrace();
//				fail();
//			}
//		}
//
//	}

}