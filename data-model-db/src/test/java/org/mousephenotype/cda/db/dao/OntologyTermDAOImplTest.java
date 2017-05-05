package org.mousephenotype.cda.db.dao;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.config.TestConfig;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.Arrays;
import java.util.List;


/**
 * Created by jmason on 21/08/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})
@ContextConfiguration(classes = {TestConfig.class})
public class OntologyTermDAOImplTest extends TestCase {

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	OntologyTermDAO ontologyTermDAO;


	@Test
	public void testGetAllOntologyTerms()
	throws Exception {

		List<OntologyTerm> terms = ontologyTermDAO.getAllOntologyTerms();
		logger.debug("Terms count is: " + terms.size());
		assertTrue(terms.size() >= 10);

	}


	@Test
	public void testGetOntologyTermByName()
	throws Exception {

		List<String> testTerms = Arrays.asList("Gene", "increased brown adipose tissue amount");

		for (String termTestString : testTerms) {
			OntologyTerm term = ontologyTermDAO.getOntologyTermByName(termTestString);
			logger.debug(String.format("Term test string is '%s', Term is: %s", termTestString, term));
			String testedValue = term.getName();
			assertTrue(testedValue.equals(termTestString));
		}

	}


	@Test
	public void testGetOntologyTermByAccession()
	throws Exception {

		List<String> testTerms = Arrays.asList("MP:0000002", "MP:0000008");

		for (String termTestString : testTerms) {
			OntologyTerm term = ontologyTermDAO.getOntologyTermByAccession(termTestString);
			logger.debug(String.format("Term test string is '%s', Term is: %s", termTestString, term));
			String testedValue = term.getId().getAccession();
			assertTrue(testedValue.equals(termTestString));
		}

	}


	@Test
	public void testGetOntologyTermByAccessionAndDatabaseId()
	throws Exception {

		List<String> testTerms = Arrays.asList("MP:0000003", "MP:0000005");

		for (String termTestString : testTerms) {
			OntologyTerm term = ontologyTermDAO.getOntologyTermByAccessionAndDatabaseId(termTestString, 5);
			logger.debug(String.format("Term test string is '%s', Term is: %s", termTestString, term));
			String testedValue = term.getId().getAccession();
			assertTrue(testedValue.equals(termTestString));
		}
	}
}