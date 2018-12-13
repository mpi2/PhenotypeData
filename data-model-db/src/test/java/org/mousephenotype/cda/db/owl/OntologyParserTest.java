package org.mousephenotype.cda.db.owl;

/**
 * Created by ilinca on 10/08/2016.
 * Refactored by mrelac on 07/12/2018.
 */

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)

@ContextConfiguration(classes = {OntologyParserTestConfig.class})
public class OntologyParserTest {

    private final Logger         logger = LoggerFactory.getLogger(this.getClass());
    private       OntologyParser ontologyParser;


    @NotNull
    @Value("${owlpath}")
    protected     String         owlpath;

    @Autowired
    DataSource komp2DataSource;


@Ignore
	@Test
	public void findSpecificMaTermMA_0002405() throws Exception {

	    OntologyParserFactory f = new OntologyParserFactory(komp2DataSource, owlpath);

	    ontologyParser = f.getMaParser();

		List<OntologyTermDTO> termList = ontologyParser.getTerms();
		Map<String, OntologyTermDTO> terms = termList.stream()
				.filter(term -> term.getAccessionId().equals("MA:0002406"))
				.collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));
		assertTrue(terms.containsKey("MA:0002406"));
	}


    // Because it had that IRI used twice, once with ObjectProperty and once with AnnotationProperty RO_0002200
@Ignore
    @Test
    public void testUberon()  throws Exception {

		OntologyParserFactory f = new OntologyParserFactory(komp2DataSource, owlpath);

		ontologyParser = f.getUberonParser();
		assertNotNull(ontologyParser);
    }

@Ignore
    @Test
    public void testNarrowSynonyms() throws Exception {

		OntologyParserFactory f = new OntologyParserFactory(komp2DataSource, owlpath);

		ontologyParser = f.getMpHpParser();

        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0006325");

        Set<String> narrowSynonyms = ontologyParser.getNarrowSynonyms(term, 1);

        assertFalse("Narrow synonyms list is empty!", narrowSynonyms.isEmpty());
        assertTrue("Narrow synonyms list does not contain a label!", narrowSynonyms.contains("conductive hearing impairment"));
        assertTrue("Narrow synonyms list does not contain an exact synonym!", narrowSynonyms.contains("complete hearing loss"));

        // Test both HP and MP terms are considered.
        // Abnormal glucose homeostasis MP:0002078 is equivalent to HP:0011014
        term = ontologyParser.getOntologyTerm("MP:0002078");

        assertTrue("HP synonym not found, was looking for Abnormal C-peptide level ." , ontologyParser.getNarrowSynonyms(term,2).contains("Abnormal C-peptide level"));
    }

@Ignore
    @Test
    public void testEquivalent() throws Exception {

        OntologyParserFactory f = new OntologyParserFactory(komp2DataSource, owlpath);

        ontologyParser = f.getMpHpParser();

        List<OntologyTermDTO> terms = ontologyParser.getTerms();
        assertFalse("Term list is empty!", terms.isEmpty());

        OntologyTermDTO mp0000572 = ontologyParser.getOntologyTerm("MP:0000572");
        assertNotNull("Could not find MP:0000572 in mp-hp.owl", mp0000572);

        assertFalse("Could not find equivalent class for MP:0000572 in mp-hp.owl. Equivalent class should be HP:0005922.", mp0000572.getEquivalentClasses().isEmpty());
        Set<OntologyTermDTO> termSet = mp0000572.getEquivalentClasses();
        List<OntologyTermDTO> eqTerms =
                termSet.stream()
                .filter(term -> term.getAccessionId().equals("HP:0005922"))
                .collect(Collectors.toList());
        assertFalse("Expected equivalent class HP:0005922 but list is empty.", eqTerms.isEmpty());
        assertTrue("Expected equivalent class HP:0005922. Not found.", eqTerms.get(0).getAccessionId().equals("HP:0005922"));
    }

@Ignore
    @Test
    public void testReplacementOptions() throws Exception {

        OntologyParserFactory f = new OntologyParserFactory(komp2DataSource, owlpath);

        ontologyParser = f.getMpParser();

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

@Ignore
    @Test
    public void findSpecificEmapaTermEMAPA_18025() throws Exception {

        OntologyParserFactory f = new OntologyParserFactory(komp2DataSource, owlpath);

        ontologyParser = f.getEmapaParser();
        List<OntologyTermDTO> termList = ontologyParser.getTerms();
        Map<String, OntologyTermDTO> terms =
                termList.stream()
                        .filter(term -> term.getAccessionId().equals("EMAPA:18025"))
                        .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));

        assertTrue(terms.containsKey("EMAPA:18025") );
    }

@Ignore
    @Test
    public void findMaTermByReferenceFromMpTerm() throws Exception {

        OntologyParserFactory f = new OntologyParserFactory(komp2DataSource, owlpath);

        ontologyParser = f.getMpMaParser();

        OntologyParser maParser = f.getMaParser();

        Set<String> referencedClasses = ontologyParser.getReferencedClasses("MP:0001926",
                OntologyParserFactory.VIA_PROPERTIES, "MA");
        if (referencedClasses != null && referencedClasses.size() > 0) {
            for (String id : referencedClasses) {
                OntologyTermDTO maTerm = maParser.getOntologyTerm(id);

                System.out.println("MA term "+id+" is "+maTerm+" for MP term MP:0001926");
                assertFalse(maTerm == null);
            }
        }
    }

@Ignore
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

@Ignore
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

//    @Test
//    public void testTermsInSlim() throws Exception{
//
//        ontologyParser = new OntologyParser(owlpath + "/mp.owl", "MP", null, null);
//        Set<String> wantedIds = new HashSet<>();
//        wantedIds.add("MP:0008901");
//        wantedIds.add("MP:0005395"); // "other phenotype" -  obsolete and should not be in the sim
//        Set<String> termsInSlim = ontologyParser.getTermsInSlim(wantedIds, null);
//        assertTrue(termsInSlim.size() == 7);
//        assertTrue(!termsInSlim.contains("MP:0005395"));
//    }

@Ignore
    @Test
    public void testParentInfo() throws Exception{

        ontologyParser = new OntologyParser(owlpath + "/mp.owl", "MP", null, null);
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        assertTrue(term.getParentIds().contains("MP:0000003"));
        assertTrue(term.getParentIds().size() == 1);
        assertTrue(term.getParentNames().size() == 1);

    }

@Ignore
    @Test
    public void testChildInfo() throws Exception{

        ontologyParser = new OntologyParser(owlpath + "/mp.owl", "MP", null, null);
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        assertTrue(term.getChildIds().contains("MP:0010024"));
        logger.debug("term.getChildIds().size() " + term.getChildIds().size() + term.getChildIds());
        assertTrue(term.getChildIds().size() == 4); // 4 child terms in the ontology without reasoning
        assertTrue(term.getChildNames().size() == 4);

        term =ontologyParser.getOntologyTerm("MP:0000003");
        logger.debug("term.getChildIds().size() " + term.getChildIds().size() + term.getChildIds());
        assertTrue(term.getChildIds().size() >= 12); // 11 child terms in the ontology without reasoning
        assertTrue(term.getChildNames().size() >= 12);

    }

@Ignore
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

//    @Test
//    public void testMpMaMapping() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
//
//        Set<OWLObjectPropertyImpl> viaProperties = new HashSet<>();
//        viaProperties.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000052")));
//        viaProperties.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000070")));
//        viaProperties.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/mp/mp-logical-definitions#inheres_in_part_of")));
//
//        OntologyParser mpMaParser = new OntologyParser(Paths.get(owlpath)+ "/mp-ext-merged.owl", null, null, null);
//        // Should have only MA_0000009 = adipose tissue; MP:0000003 = abnormal adipose tissue morphology
//        Set<String> ma = mpMaParser.getReferencedClasses("MP:0000003", viaProperties, "MA");
//        Assert.assertTrue(ma.size() == 1);
//        Assert.assertTrue(ma.contains("MA:0000009"));
//
//        Set<String> maBrain = mpMaParser.getReferencedClasses("MP:0002152", viaProperties, "MA");
//        Assert.assertTrue(maBrain.contains("MA:0000168"));
//
//    }
//
//    @Test
//    public void testPrefixCheck() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
//
//        Set<String> wantedIds = new HashSet<>();
//        wantedIds.add("HP:0001892");
//        OntologyParser hpParser = new OntologyParser(downloads.get("hp").target, "HP", null, wantedIds);
//        Assert.assertTrue(!hpParser.getTermsInSlim().contains("UPHENO:0001002"));
//
//    }
//
//    @Test
//    public void testTopLevelsForHp() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
//        Set<String> wantedIds = new HashSet<>();
//        wantedIds.add("HP:0001892");
//        wantedIds.add("HP:0001477");
//        wantedIds.add("HP:0000164");
//        wantedIds.add("HP:0006202"); // child of HP:0001495
//        OntologyParser hpParser = new OntologyParser(downloads.get("hp").target, "HP", OntologyParserFactory.TOP_LEVEL_HP_TERMS, wantedIds);
//        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001892").getTopLevelIds().size() > 0);
//        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001495").getTopLevelIds().size() > 0);
//        Assert.assertTrue(hpParser.getOntologyTerm("HP:0000164").getTopLevelIds().size() > 0);
//        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001477").getTopLevelIds().size() > 0);
//        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001477").getTopLevelIds().contains("HP:0000478"));
//    }


}