package org.mousephenotype.cda.db.repositories;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.db.repositories.OntologyTermRepository;
import org.mousephenotype.cda.db.repositories.TestConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


/**
 * Created by jmason on 21/08/2015.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
public class OntologyTermDAOImplTest extends TestCase {

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

		List<String> testTerms = Arrays.asList("Gene", "increased brown adipose tissue amount");

		for (String termTestString : testTerms) {
			OntologyTerm term = ontologyTermRepository.getByName(termTestString);
			String testedValue = term.getName();
			assertTrue(testedValue.equals(termTestString));
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