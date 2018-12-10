package org.mousephenotype.cda.db.owl;

/**
 * Created by ilinca on 10/08/2016.
 * Refactored by mrelac on 07/12/2018.
 */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.owl.OntologyParser;
import org.mousephenotype.cda.owl.OntologyParserFactory;
import org.mousephenotype.cda.owl.OntologyTermDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)

@ContextConfiguration(classes = {OntologyParserTestConfig.class})
public class OntologyParserTest {

//    public static boolean               downloadFiles = false;
//    private       Map<String, Download> downloads     = new HashMap<>();  // key = map name. value = download info.
//    public        boolean               doDownload    = true;
//    private final Logger                logger        = LoggerFactory.getLogger(this.getClass());
    private   OntologyParser ontologyParser;
//
//
    @NotNull
    @Value("${owlpath}")
    protected String         owlpath;

    @Autowired
    DataSource komp2DataSource;
//
//    @Before
//    public void setUp() throws Exception {
//
//        String mpExtMerged = owlpath + "/mp-ext-merged.owl";
//
//        downloads.put("efo", new Download("EFO", "http://www.ebi.ac.uk/efo/efo.owl", owlpath + "/efo.owl"));
////        downloads.put("mphp", new Download("MP", "http://build-artifacts.berkeleybop.org/build-mp-hp-view/latest/mp-hp-view.owl", owlpath + "/mp-hp.owl"));
//        downloads.put("mp", new Download("MP", "http://purl.obolibrary.org/obo/mp.owl", owlpath + "/mp.owl"));
//        downloads.put("hp", new Download("HP", "http://purl.obolibrary.org/obo/hp.owl", owlpath + "/hp.owl"));
//        downloads.put("ma", new Download("MA", "http://purl.obolibrary.org/obo/ma.owl", owlpath + "/ma.owl"));
//        downloads.put("mpma", new Download("MP", "http://purl.obolibrary.org/obo/mp-ext-merged.owl", owlpath + "/mp-ext-merged.owl"));
//        downloads.put("emapa", new Download("EMAPA", "http://purl.obolibrary.org/obo/emapa.owl", owlpath + "/emapa.owl"));
//        downloads.put("uberon", new Download("UBERON", "http://purl.obolibrary.org/obo/uberon.owl", owlpath + "/uberon.owl"));
//
//        if ( ! downloadFiles) {
//            downloadFiles();
//            downloadFiles = true;
//        }
//
//        // Copy static owl file for testMpMaMapping()
//        Resource resource = new ClassPathResource("mp-ext-merged.owl");
//        Files.copy(resource.getInputStream(), Paths.get(mpExtMerged), StandardCopyOption.REPLACE_EXISTING);
//    }

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
    @Test
    public void testUberon()  throws Exception {

		OntologyParserFactory f = new OntologyParserFactory(komp2DataSource, owlpath);

		ontologyParser = f.getUberonParser();
		assertNotNull(ontologyParser);
    }

//    // Because it had that IRI used twice, once with ObjectProperty and once with AnnotationProperty RO_0002200
//    @Test
//    public void testEFO()  throws Exception {
//
//        ontologyParser = new OntologyParser(downloads.get("efo").target, downloads.get("efo").name, null, null);
//        List<OntologyTermDTO> terms = ontologyParser.getTerms();
//        Assert.assertFalse("Expected at least one term.", terms.isEmpty());
//
//    }
//
//
//    @Test
//    public void testNarrowSynonyms() throws Exception {
//
//        logger.debug("target: " + downloads.get("mphp").target);
//        logger.debug("name:   " + downloads.get("mphp").name);
//        ontologyParser = new OntologyParser(downloads.get("mphp").target, downloads.get("mphp").name, null, null);
//        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0006325");
//
//        Set<String> narrowSynonyms = ontologyParser.getNarrowSynonyms(term, 1);
//
//        Assert.assertFalse("Narrow synonyms list is empty!", narrowSynonyms.isEmpty());
//        Assert.assertTrue("Narrow synonyms list does not contain a label!", narrowSynonyms.contains("conductive hearing impairment"));
//        Assert.assertTrue("Narrow synonyms list does not contain an exact synonym!", narrowSynonyms.contains("complete hearing loss"));
//
//        // Test both HP and MP terms are considered.
//        // Abnormal glucose homeostasis MP:0002078 is equivalent to HP:0011014
//        term = ontologyParser.getOntologyTerm("MP:0002078");
//
//        Assert.assertTrue("HP synonym not found, was looking for Abnormal C-peptide level ." , ontologyParser.getNarrowSynonyms(term,2).contains("Abnormal C-peptide level"));
//
//    }
//
//
//    // mp-hp.owl is now generated - not downloaded (mrelac - 15-Feb-2018)
////    @Test
////    public void testEquivalent() throws Exception {
////
////        ontologyParser = new OntologyParser(downloads.get("mphp").target, downloads.get("mphp").name, null, null);
////        List<OntologyTermDTO> terms = ontologyParser.getTerms();
////        Assert.assertFalse("Term list is empty!", terms.isEmpty());
////
////        OntologyTermDTO mp0000572 = ontologyParser.getOntologyTerm("MP:0000572");
////        Assert.assertNotNull("Could not find MP:0000572 in mp-hp.owl", mp0000572);
////
////        Assert.assertFalse("Could not find equivalent class for MP:0000572 in mp-hp.owl. Equivalent class should be HP:0005922.", mp0000572.getEquivalentClasses().isEmpty());
////        Set<OntologyTermDTO> termSet = mp0000572.getEquivalentClasses();
////        List<OntologyTermDTO> eqTerms =
////                termSet.stream()
////                .filter(term -> term.getAccessionId().equals("HP:0005922"))
////                .collect(Collectors.toList());
////        Assert.assertFalse("Expected equivalent class HP:0005922 but list is empty.", eqTerms.isEmpty());
////        Assert.assertTrue("Expected equivalent class HP:0005922. Not found.", eqTerms.get(0).getAccessionId().equals("HP:0005922"));
////    }
//
//
//    @Test
//    public void testReplacementOptions() throws Exception {
//
//        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
//
//        List<OntologyTermDTO> termList = ontologyParser.getTerms();
//        Map<String, OntologyTermDTO> terms =
//                termList.stream()
//                .filter(term -> term.getAccessionId().equals("MP:0006374") || term.getAccessionId().equals("MP:0002977") || term.getAccessionId().equals("MP:0000003"))
//                .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));
//
//        /* Test alternative ids are found for MP_0000003 (should be MP:0000011). */
//
//        OntologyTermDTO withAltIds = terms.get("MP:0000003");
//        Assert.assertTrue("Expected MP:0000003 has MP:0000011 as alt id. ", (withAltIds.getAlternateIds() != null && withAltIds.getAlternateIds().contains("MP:0000011")));
//
//        /*
//         * Test for term MP:0006374 with replacement ID MP:0008996
//         */
//        OntologyTermDTO withReplacementIds = terms.get("MP:0006374");
//        Assert.assertNotNull("Expected term MP:0006374, a term with replacement ids. Not found.", withReplacementIds);
//        Assert.assertTrue("Expected MP:0006374 to be marked obsolete but it was not.", withReplacementIds.isObsolete());
//        Assert.assertNotNull("Expected MP:0006374 to have a replacement term, but the replacement term was null", withReplacementIds.getReplacementAccessionId());
//        Assert.assertFalse("Expected MP:0006374 to have a replacement term, but the replacement term list was empty.", withReplacementIds.getReplacementAccessionId().isEmpty());
//        Assert.assertTrue("Expected replacement accession id MP:0008996. Not found.", withReplacementIds.getReplacementAccessionId().contains("MP:0008996"));
//
//        /*
//         * Test for term MP:0002977 with consider IDs MP:0010241 and MP:0010464
//         */
//        OntologyTermDTO withConsiderIds = terms.get("MP:0002977");
//        Assert.assertNotNull("Expected term MP:0002977, a term with consider ids. Not found.", withConsiderIds);
//        Assert.assertTrue("Expected at least two consider id terms: MP:0010241 and MP:0010464, but found " + withConsiderIds.getConsiderIds().size() + ".'", withConsiderIds.getConsiderIds().size() >= 2);
//        Assert.assertTrue("Expected consider id MP:0010241. Not found.", withConsiderIds.getConsiderIds().contains("MP:0010241"));
//        Assert.assertTrue("Expected consider id MP:0010464. Not found.", withConsiderIds.getConsiderIds().contains("MP:0010464"));
//
//
//
//    }
//
//    @Test
//    public void findSpecificEmapaTermEMAPA_18025() throws Exception {
//        ontologyParser = new OntologyParser(downloads.get("emapa").target, downloads.get("emapa").name, OntologyParserFactory.TOP_LEVEL_EMAPA_TERMS, null);
//        List<OntologyTermDTO> termList = ontologyParser.getTerms();
//        Map<String, OntologyTermDTO> terms =
//                termList.stream()
//                        .filter(term -> term.getAccessionId().equals("EMAPA:18025"))
//                        .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));
//
//        Assert.assertTrue(terms.containsKey("EMAPA:18025") );
//
//    }
//
//    @Test
//    public void findMaTermByReferenceFromMpTerm() throws Exception {
//        ontologyParser = new OntologyParser(downloads.get("mpma").target, downloads.get("mpma").name, null, null);
//
//        OntologyParser maParser = new OntologyParser(downloads.get("ma").target, downloads.get("ma").name, OntologyParserFactory.TOP_LEVEL_MA_TERMS, null);
//
//        Set<String> referencedClasses = ontologyParser.getReferencedClasses("MP:0001926",
//                OntologyParserFactory.VIA_PROPERTIES, "MA");
//        if (referencedClasses != null && referencedClasses.size() > 0) {
//            for (String id : referencedClasses) {
//                OntologyTermDTO maTerm = maParser.getOntologyTerm(id);
//
//                System.out.println("MA term "+id+" is "+maTerm+" for MP term MP:0001926");
//                Assert.assertFalse(maTerm == null);
//            }
//        }
//    }
//
//
//    @Test
//    public void findSpecificMpTermMP_0020422() throws Exception {
//        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
//        List<OntologyTermDTO> termList = ontologyParser.getTerms();
//        Map<String, OntologyTermDTO> terms =
//                termList.stream()
//                        .filter(term -> term.getAccessionId().equals("MP:0020422"))
//                        .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));
//
//        Assert.assertTrue(terms.containsKey("MP:0020422"));
//
//    }
//
//    @Test
//    public void testRootTermAndTopTermsInOntologyParserMap() throws Exception {
//
//        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
//        List<OntologyTermDTO> termList = ontologyParser.getTerms();
//        Map<String, OntologyTermDTO> terms =
//                termList.stream()
//                        .filter(term -> term.getAccessionId().equals("MP:0000001") || OntologyParserFactory.TOP_LEVEL_MP_TERMS.contains(term.getAccessionId()))
//                        .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));
//
//        Assert.assertTrue(terms.containsKey("MP:0000001") );
//        Assert.assertTrue(terms.containsKey("MP:0010771"));
//        Assert.assertFalse(terms.containsKey("MP:0010734571"));
//    }
//
//    @Test
//    public void testTermsInSlim() throws Exception{
//
//        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
//        Set<String> wantedIds = new HashSet<>();
//        wantedIds.add("MP:0008901");
//        wantedIds.add("MP:0005395"); // "other phenotype" -  obsolete and should not be in the sim
//        Set<String> termsInSlim = ontologyParser.getTermsInSlim(wantedIds, null);
//        Assert.assertTrue(termsInSlim.size() == 7);
//        Assert.assertTrue(!termsInSlim.contains("MP:0005395"));
//    }
//
//    @Test
//    public void testParentInfo() throws Exception{
//
//        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
//        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
//        Assert.assertTrue(term.getParentIds().contains("MP:0000003"));
//        Assert.assertTrue(term.getParentIds().size() == 1);
//        Assert.assertTrue(term.getParentNames().size() == 1);
//
//    }
//
//    @Test
//    public void testChildInfo() throws Exception{
//
//        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
//        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
//        Assert.assertTrue(term.getChildIds().contains("MP:0010024"));
//        logger.debug("term.getChildIds().size() " + term.getChildIds().size() + term.getChildIds());
//        Assert.assertTrue(term.getChildIds().size() == 4); // 4 child terms in the ontology without reasoning
//        Assert.assertTrue(term.getChildNames().size() == 4);
//
//        term =ontologyParser.getOntologyTerm("MP:0000003");
//        logger.debug("term.getChildIds().size() " + term.getChildIds().size() + term.getChildIds());
//        Assert.assertTrue(term.getChildIds().size() >= 12); // 11 child terms in the ontology without reasoning
//        Assert.assertTrue(term.getChildNames().size() >= 12);
//
//    }
//
//    @Test
//    public void testTopLevels() throws Exception{
//
//        Set<String> topLevels = new HashSet<>(OntologyParserFactory.TOP_LEVEL_MP_TERMS);
//
//        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, topLevels, null);
//
//        // 1 term top level
//        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
//        Assert.assertTrue(term.getTopLevelIds().contains("MP:0005375"));
//        Assert.assertTrue(term.getTopLevelIds().size() == 1);
//        Assert.assertTrue(term.getTopLevelNames().size() == 1);
//
//        Assert.assertTrue(ontologyParser.getOntologyTerm("MP:0005385") != null);
//
//        // multiple top levels
//        term = ontologyParser.getOntologyTerm("MP:0000017"); // big ears
//        Assert.assertTrue(term.getTopLevelIds().contains("MP:0005382"));
//        Assert.assertTrue(term.getTopLevelIds().contains("MP:0005378"));
//        Assert.assertTrue(term.getTopLevelIds().contains("MP:0005377"));
//        Assert.assertTrue(term.getTopLevelIds().size() == 3);
//        Assert.assertTrue(term.getTopLevelNames().size() == 3);
//
//        // term is top level itself
//        term = ontologyParser.getOntologyTerm("MP:0005378");
//        Assert.assertTrue(term.getTopLevelIds() == null || term.getTopLevelIds().size() == 0);
//    }
//
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