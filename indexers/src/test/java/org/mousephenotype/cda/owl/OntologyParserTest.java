package org.mousephenotype.cda.owl;

/**
 * Created by ilinca on 10/08/2016.
 * Refactored by mrelac on 07/12/2018.
 * Refactored by mrelac on 15/04/2019.
 */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {OntologyParserTestConfig.class})

public class OntologyParserTest {

    private final Logger         logger = LoggerFactory.getLogger(this.getClass());
    private       OntologyParser ontologyParser;


    @Autowired
    private String owlpath;

    @Autowired
    private OntologyParserFactory ontologyParserFactory;


	@Test
	public void findSpecificMaTermMA_0002405() throws Exception {

	    ontologyParser = ontologyParserFactory.getMaParser();

		List<OntologyTermDTO> termList = ontologyParser.getTerms();
		Map<String, OntologyTermDTO> terms = termList.stream()
				.filter(term -> term.getAccessionId().equals("MA:0002406"))
				.collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));
		assertTrue(terms.containsKey("MA:0002406"));
	}


    // Because it had that IRI used twice, once with ObjectProperty and once with AnnotationProperty RO_0002200
    // last run: 16 seconds
    @Test
    public void testUberon()  throws Exception {

		ontologyParser = ontologyParserFactory.getUberonParser();
        assertNotNull(ontologyParser);
    }

    @Test
    public void testReplacementOptions() throws Exception {

        ontologyParser = ontologyParserFactory.getMpParser();

        List<OntologyTermDTO> termList = ontologyParser.getTerms();
        Map<String, OntologyTermDTO> terms =
                termList.stream()
                .filter(term -> term.getAccessionId().equals("MP:0006374") || term.getAccessionId().equals("MP:0002977") || term.getAccessionId().equals("MP:0000003"))
                .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));

        /* Test alternative ids are found for MP_0000003 (should be MP:0000011). */

        OntologyTermDTO withAltIds = terms.get("MP:0000003");
        assertTrue("Expected MP:0000003 has MP:0000011 as alt id. ", (withAltIds.getAlternateIds() != null && withAltIds.getAlternateIds().contains("MP:0000011")));

        /*
         * Test for term MP:0006374 with replacement ID MP:0008996
         */
        OntologyTermDTO withReplacementIds = terms.get("MP:0006374");
        assertNotNull("Expected term MP:0006374, a term with replacement ids. Not found.", withReplacementIds);
        assertTrue("Expected MP:0006374 to be marked obsolete but it was not.", withReplacementIds.isObsolete());
        assertNotNull("Expected MP:0006374 to have a replacement term, but the replacement term was null", withReplacementIds.getReplacementAccessionId());
        assertFalse("Expected MP:0006374 to have a replacement term, but the replacement term list was empty.", withReplacementIds.getReplacementAccessionId().isEmpty());
        assertTrue("Expected replacement accession id MP:0008996. Not found.", withReplacementIds.getReplacementAccessionId().contains("MP:0008996"));

        /*
         * Test for term MP:0002977 with consider IDs MP:0010241 and MP:0010464
         */
        OntologyTermDTO withConsiderIds = terms.get("MP:0002977");
        assertNotNull("Expected term MP:0002977, a term with consider ids. Not found.", withConsiderIds);
        assertTrue("Expected at least two consider id terms: MP:0010241 and MP:0010464, but found " + withConsiderIds.getConsiderIds().size() + ".'", withConsiderIds.getConsiderIds().size() >= 2);
        assertTrue("Expected consider id MP:0010241. Not found.", withConsiderIds.getConsiderIds().contains("MP:0010241"));
        assertTrue("Expected consider id MP:0010464. Not found.", withConsiderIds.getConsiderIds().contains("MP:0010464"));
    }


    @Test
    public void findSpecificEmapaTermEMAPA_18025() throws Exception {

        ontologyParser = ontologyParserFactory.getEmapaParser();
        List<OntologyTermDTO> termList = ontologyParser.getTerms();
        Map<String, OntologyTermDTO> terms =
                termList.stream()
                        .filter(term -> term.getAccessionId().equals("EMAPA:18025"))
                        .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));

        assertTrue(terms.containsKey("EMAPA:18025") );
    }


    @Test
    public void findMaTermByReferenceFromMpTerm() throws Exception {

        ontologyParser = ontologyParserFactory.getMpMaParser();

        OntologyParser maParser = ontologyParserFactory.getMaParser();

        Set<String> referencedClasses = ontologyParser.getReferencedClasses("MP:0001926",
                OntologyParserFactory.VIA_PROPERTIES, "MA");
        if (referencedClasses != null && referencedClasses.size() > 0) {
            for (String id : referencedClasses) {
                OntologyTermDTO maTerm = maParser.getOntologyTerm(id);

                logger.debug("MA term "+id+" is "+maTerm+" for MP term MP:0001926");
                assertFalse(maTerm == null);
            }
        }
    }


    // last run: 41 seconds
    @Test
    public void findSpecificMpTermMP_0020422() throws Exception {

        ontologyParser = new OntologyParser(owlpath + "/mp.owl", "MP", null, null);

        List<OntologyTermDTO> termList = ontologyParser.getTerms();
        Map<String, OntologyTermDTO> terms =
                termList.stream()
                        .filter(term -> term.getAccessionId().equals("MP:0020422"))
                        .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));

        assertTrue(terms.containsKey("MP:0020422"));
    }


    // last run: 17 seconds
    @Test
    public void testRootTermAndTopTermsInOntologyParserMap() throws Exception {

        ontologyParser = new OntologyParser(owlpath + "/mp.owl", "MP", null, null);
        List<OntologyTermDTO> termList = ontologyParser.getTerms();
        Map<String, OntologyTermDTO> terms =
                termList.stream()
                        .filter(term -> term.getAccessionId().equals("MP:0000001") || OntologyParserFactory.TOP_LEVEL_MP_TERMS.contains(term.getAccessionId()))
                        .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));

        assertTrue(terms.containsKey("MP:0000001") );
        assertTrue(terms.containsKey("MP:0010771"));
        assertFalse(terms.containsKey("MP:0010734571"));
    }


    @Test
    public void testTermsInSlim() throws Exception{

        OntologyParserExtended ontologyParserExtended = new OntologyParserExtended(owlpath + "/mp.owl", "MP", null, null);
        Set<String> wantedIds = new HashSet<>();
        wantedIds.add("MP:0008901");
        wantedIds.add("MP:0005395"); // "other phenotype" -  obsolete and should not be in the sim
        Set<String> termsInSlim = ontologyParserExtended.getTermsInSlim(wantedIds, null);
        assertTrue("Expected at least 7 terms in slim but found " + termsInSlim.size(), termsInSlim.size() >= 7);
        assertTrue( "Expected to find MP:0005395 but didn't", ! termsInSlim.contains("MP:0005395"));
    }


    @Test
    public void testParentInfo() throws Exception{

        ontologyParser = new OntologyParser(owlpath + "/mp.owl", "MP", null, null);
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        assertTrue(term.getParentIds().contains("MP:0000003"));
        assertTrue(term.getParentIds().size() == 1);
        assertTrue(term.getParentNames().size() == 1);

    }


    @Test
    public void testChildInfo() throws Exception{

        ontologyParser = new OntologyParser(owlpath + "/mp.owl", "MP", null, null);
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        assertTrue(term.getChildIds().contains("MP:0010024"));
        int size = term.getChildIds().size();
        assertTrue("Expected at least 4 child ids but found " + size, size >= 4);
        size = term.getChildNames().size();
        assertTrue("Expected at least 4 child names but found " + size, size >= 4);

        term =ontologyParser.getOntologyTerm("MP:0000003");
        size = term.getChildIds().size();
        assertTrue("Expected at least 12 child ids but found " + size, size >= 12);
        size = term.getChildNames().size();
        assertTrue("Expected at least 12 child names but found " + size, size >= 12);
    }


    @Test
    public void testTopLevels() throws Exception{

        Set<String> topLevels = new HashSet<>(OntologyParserFactory.TOP_LEVEL_MP_TERMS);

        ontologyParser = new OntologyParser(owlpath + "/mp.owl", "MP", topLevels, null);

        // 1 term top level
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        assertTrue(term.getTopLevelIds().contains("MP:0005375"));
        assertTrue(term.getTopLevelIds().size() == 1);
        assertTrue(term.getTopLevelNames().size() == 1);

        assertTrue(ontologyParser.getOntologyTerm("MP:0005385") != null);

        // multiple top levels
        term = ontologyParser.getOntologyTerm("MP:0000017"); // big ears
        assertTrue(term.getTopLevelIds().contains("MP:0005382"));
        assertTrue(term.getTopLevelIds().contains("MP:0005378"));
        assertTrue(term.getTopLevelIds().contains("MP:0005377"));
        assertTrue(term.getTopLevelIds().size() == 3);
        assertTrue(term.getTopLevelNames().size() == 3);

        // term is top level itself
        term = ontologyParser.getOntologyTerm("MP:0005378");
        assertTrue(term.getTopLevelIds() == null || term.getTopLevelIds().size() == 0);
    }


    @Test
    public void testMpMaMapping() throws OWLOntologyCreationException, OWLOntologyStorageException, SQLException, IOException {

        ontologyParser = ontologyParserFactory.getMpMaParser();

        Set<OWLObjectPropertyImpl> viaProperties = new HashSet<>();
        viaProperties.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000052")));
        viaProperties.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000070")));
        viaProperties.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/mp/mp-logical-definitions#inheres_in_part_of")));

        // Should have only MA_0000009 = adipose tissue; MP:0000003 = abnormal adipose tissue morphology
        Set<String> ma = ontologyParser.getReferencedClasses("MP:0000003", viaProperties, "MA");
        assertTrue(ma.size() == 1);
        assertTrue(ma.contains("MA:0000009"));

        Set<String> maBrain = ontologyParser.getReferencedClasses("MP:0002152", viaProperties, "MA");
        assertTrue(maBrain.contains("MA:0000168"));
    }


    @Test
    public void testPrefixCheck() throws OWLOntologyCreationException, OWLOntologyStorageException, SQLException, IOException {

        Set<String> wantedIds = new HashSet<>();
        wantedIds.add("HP:0001892");

        ontologyParser = new OntologyParser(owlpath + "/hp.owl", "HP", null, wantedIds);

        assertTrue( ! ontologyParser.getTermsInSlim().contains("UPHENO:0001002"));
    }

    @Test
    public void testTopLevelsForHp() throws OWLOntologyCreationException, OWLOntologyStorageException, SQLException, IOException {

        Set<String> wantedIds = new HashSet<>();
        wantedIds.add("HP:0001892");
        wantedIds.add("HP:0001477");
        wantedIds.add("HP:0000164");
        wantedIds.add("HP:0006202"); // child of HP:0001495

        ontologyParser = new OntologyParser(owlpath + "/hp.owl", "HP", OntologyParserFactory.TOP_LEVEL_HP_TERMS, wantedIds);

        assertTrue(ontologyParser.getOntologyTerm("HP:0001892").getTopLevelIds().size() > 0);
        assertTrue(ontologyParser.getOntologyTerm("HP:0001495").getTopLevelIds().size() > 0);
        assertTrue(ontologyParser.getOntologyTerm("HP:0000164").getTopLevelIds().size() > 0);
        assertTrue(ontologyParser.getOntologyTerm("HP:0001477").getTopLevelIds().size() > 0);
        assertTrue(ontologyParser.getOntologyTerm("HP:0001477").getTopLevelIds().contains("HP:0000478"));
    }

    public class OntologyParserExtended extends OntologyParser {

        public OntologyParserExtended(String pathToOwlFile, String prefix, Collection<String> topLevelIds, Set<String> wantedIds)
                throws OWLOntologyCreationException, IOException, OWLOntologyStorageException {
            super(pathToOwlFile, prefix, topLevelIds, wantedIds);
        }

        public Set<String> getTermsInSlim(Set<String> wantedIDs, String prefix) throws IOException, OWLOntologyStorageException {
            return super.getTermsInSlim(wantedIDs, prefix);
        }
    }
}
