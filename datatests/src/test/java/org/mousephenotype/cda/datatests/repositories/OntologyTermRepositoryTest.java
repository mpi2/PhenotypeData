package org.mousephenotype.cda.datatests.repositories;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositoryTestConfig.class})
public class OntologyTermRepositoryTest {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OntologyTermRepository ontologyTermRepository;


	@Test
	public void testGetAllOntologyTerms() {

		long size = ontologyTermRepository.count();
		assertTrue(size >= 10);
	}


	@Test
	public void testGetOntologyTermByName() {

		List<String> expectedValues = Arrays.asList("gene", "increased brown adipose tissue amount");

		for (String expectedValue : expectedValues) {
			OntologyTerm term = ontologyTermRepository.getByName(expectedValue);
			String actualValue = term.getName();
			assertEquals(expectedValue, actualValue);
		}
	}


	@Test
	public void testGetOntologyTermByAccession() {

		List<String> testTerms = Arrays.asList("MP:0000002", "MP:0000008");

		for (String termTestString : testTerms) {
			OntologyTerm term = ontologyTermRepository.getById_Accession(termTestString);
			String testedValue = term.getId().getAccession();
			assertTrue(testedValue.equals(termTestString));
		}
	}


	@Test
	public void testGetOntologyTermByAccessionAndDatabaseId() {

		List<String> testTerms = Arrays.asList("MP:0000003", "MP:0000005");

		for (String termTestString : testTerms) {
			OntologyTerm term = ontologyTermRepository.getById_AccessionAndId_DatabaseId(termTestString, 5L);
			String testedValue = term.getId().getAccession();
			assertTrue(testedValue.equals(termTestString));
		}
	}
}