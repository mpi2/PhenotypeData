package org.mousephenotype.cda.owl;

/**
 * Created by ilinca on 10/08/2016.
 */

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.owl.config.TestConfig;
import org.mousephenotype.cda.utilities.UrlUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@ComponentScan("org.mousephenotype.cda.owl")
//@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
@SpringBootTest(classes = TestConfig.class)
@Deprecated
public class OntologyParserTest {
    public static boolean                     areFilesDownloaded = false;

    private       Map<String, Download>       downloads          = new HashMap<>();  // key = map name. value = download info.
    private final Logger                      logger             = LoggerFactory.getLogger(this.getClass());
    private       Map<String, OntologyParser> ontologyParsers    = new HashMap<>();                                     // Key is downloads.name field (e.g. "emapa", "hp", "mphp", etc)
    private       String                      owlpath            = "impc_ontologies";


    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource testDataSource;

    @Before
    public void setUp() throws Exception {

        // These urls were taken from the jenkins job that downloads the ontologies: http://ves-ebi-d9.ebi.ac.uk:8080/jenkins/job/IMPC_Download_ontology_reports/configure
        downloads.put("emapa",  new Download("emapa",  "http://purl.obolibrary.org/obo/emapa.owl",                                                                     owlpath + "/emapa.owl"));
        downloads.put("ma",     new Download("ma",     "http://purl.obolibrary.org/obo/ma.owl",                                                                        owlpath + "/ma.owl"));
        downloads.put("mp",     new Download("mp",     "http://purl.obolibrary.org/obo/mp.owl",                                                                        owlpath + "/mp.owl"));
        downloads.put("mpma",   new Download("mp",     "http://purl.obolibrary.org/obo/mp-ext-merged.owl",                                                             owlpath + "/mp-ext-merged.owl"));
        // This file is not downloaded; it is kept in the resources directory, as it is hand-built whenever we want the latest mp-hp mapping from monarch.
        downloads.put("mphp",   new Download("mphp",   "",                                                                                                             owlpath + "/mp-hp.owl"));

        if ( ! areFilesDownloaded) {
            downloadFiles();
            areFilesDownloaded = true;
        }

        // Load impress schema
        Resource r = context.getResource("sql/h2/impress/impressSchema.sql");
        ScriptUtils.executeSqlScript(testDataSource.getConnection(), r);

        // Load ontology parsers
        OntologyParserFactory factory = new OntologyParserFactory(testDataSource, owlpath);
        ontologyParsers.put("emapa", factory.getEmapaParser());
        ontologyParsers.put("ma", factory.getMaParser());
        ontologyParsers.put("mp", factory.getMpParser());
        ontologyParsers.put("mpma", factory.getMpMaParser());
        ontologyParsers.put("mphp", factory.getMpHpParser());
    }

    private class Download {
        public final String name;
        public final String url;
        public final String target;

        public Download(String name, String url, String target) {
            this.name = name;
            this.url = url;
            this.target = target;
        }
    }

    private void downloadFiles() throws Exception{

        try {
            Files.createDirectories(Paths.get(owlpath));
        } catch (IOException e) {
            System.err.println("Create owlpath directory '" + owlpath + "' failed. Reason: " + e.getLocalizedMessage());
        }

        for (Download download : downloads.values()) {

            // Download required EMAP-EMAPA.txt
            Resource r   = context.getResource("classpath:EMAP-EMAPA.txt");
            String emapaTarget = owlpath + "/EMAP-EMAPA.txt";
            logger.info("DOWNLOADING EMAP-EMAPA.txt to " + emapaTarget);
            Files.copy(r.getInputStream(), Paths.get(emapaTarget), StandardCopyOption.REPLACE_EXISTING);

            if (download.url.isEmpty()) {
                switch (download.name) {
                    case "mphp":

                        Resource mpHpOwl   = context.getResource("classpath:mp-hp.owl");
                        logger.info("DOWNLOADING mp-hp.owl to " + download.target);
                        Files.copy(mpHpOwl.getInputStream(), Paths.get(download.target), StandardCopyOption.REPLACE_EXISTING);
                        break;

                    default:

                        break;
                }
            } else {

                // Download the files with non-empty urls
                FileOutputStream    fos;
                ReadableByteChannel rbc;
                String              target;
                URL                 url;

                target = download.target;

                try {
                    url = new URL(UrlUtils.getRedirectedUrl(download.url));
                    if (download.url.equals(url.toString())) {
                        logger.info("DOWNLOADING " + url.toString() + " to " + download.target);
                    } else {
                        logger.info("DOWNLOADING " + download.url + " (remapped to " + url.toString() + ") to " + download.target);
                    }
                    rbc = Channels.newChannel(url.openStream());
                    fos = new FileOutputStream(target);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

                } catch (IOException e) {
                    logger.error(download.url + " -> " + target + " download failed. Reason: " + e.getLocalizedMessage());
                }
            }
        }
    }

//@Ignore
//    @Test
//    public void testOwlOntologyDownloads() throws Exception {
//        String message;
//        List<Exception> exception = new ArrayList();
//        File owlpathFile = new File(owlpath);
//        File[] owlFiles = owlpathFile.listFiles(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return name.toLowerCase().endsWith(".owl");
//            }
//        });
//
//        String prefix;
//        for (File file : owlFiles) {
//            prefix = file.getName().replace(".owl", "").toUpperCase();
//            try {
//                ontologyParser = new OntologyParser(file.getPath(), prefix, null, null);
//            } catch (Exception e) {
//                message = "[FAIL - " + prefix + "] Exception in " + file.getPath() + "(" + prefix + "): " + e.getLocalizedMessage();
//                exception.add(e);
//                System.out.println(message + "\n");
//                continue;
//            }
//            List<OntologyTermDTO> terms = ontologyParser.getTerms();
//            if (terms.size() > 700) {
//                logger.debug("[PASS - " + prefix + "] - " + file.getPath() + ". Size: " + terms.size());
//            } else {
//                logger.debug("[FAIL - " + prefix + "] - " + file.getPath() + ". Size: " + terms.size());
//            }
//        }
//
//        if ( ! exception.isEmpty()) {
//            throw exception.get(0);            // Just throw the first one.
//        }
//    }


//@Ignore
	@Test
	public void findSpecificMaTermMA_0002405() throws Exception {
		List<OntologyTermDTO> termList = ontologyParsers.get("ma").getTerms();
		Map<String, OntologyTermDTO> terms = termList.stream()
				.filter(term -> term.getAccessionId().equals("MA:0002406"))
				.collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));
		Assert.assertTrue(terms.containsKey("MA:0002406"));
	}


@Ignore
    @Test
    public void testNarrowSynonyms() throws Exception {
OntologyParser ontologyParser;
        logger.debug("target: " + downloads.get("mphp").target);
        logger.debug("name:   " + downloads.get("mphp").name);
        ontologyParser = new OntologyParser(downloads.get("mphp").target, downloads.get("mphp").name, null, null);
        OntologyTermDTO term = ontologyParsers.get("mphp").getOntologyTerm("MP:0006325");

        OntologyParserFactory f = new OntologyParserFactory(testDataSource, owlpath);
        OntologyParser p = f.getMpHpParser();

//        f.getMpHpParser()
        Set<String> narrowSynonyms2 = ontologyParser.getNarrowSynonyms(term, 1);


        Set<String> narrowSynonyms = ontologyParser.getNarrowSynonyms(term, 1);

        Assert.assertFalse("Narrow synonyms list is empty!", narrowSynonyms.isEmpty());
        Assert.assertTrue("Narrow synonyms list does not contain a label!", narrowSynonyms.contains("conductive hearing impairment"));
        Assert.assertTrue("Narrow synonyms list does not contain an exact synonym!", narrowSynonyms.contains("complete hearing loss"));

        // Test both HP and MP terms are considered.
        // Abnormal glucose homeostasis MP:0002078 is equivalent to HP:0011014
        term = ontologyParser.getOntologyTerm("MP:0002078");

        Assert.assertTrue("HP synonym not found, was looking for Abnormal C-peptide level ." , ontologyParser.getNarrowSynonyms(term,2).contains("Abnormal C-peptide level"));

    }


    // mp-hp.owl is now generated - not downloaded (mrelac - 15-Feb-2018)
@Ignore
    @Test
    public void testEquivalent() throws Exception {
OntologyParser ontologyParser;
        ontologyParser = new OntologyParser(downloads.get("mphp").target, downloads.get("mphp").name, null, null);
        List<OntologyTermDTO> terms = ontologyParser.getTerms();
        Assert.assertFalse("Term list is empty!", terms.isEmpty());

        OntologyTermDTO mp0000572 = ontologyParser.getOntologyTerm("MP:0000572");
        Assert.assertNotNull("Could not find MP:0000572 in mp-hp.owl", mp0000572);

        Assert.assertFalse("Could not find equivalent class for MP:0000572 in mp-hp.owl. Equivalent class should be HP:0005922.", mp0000572.getEquivalentClasses().isEmpty());
        Set<OntologyTermDTO> termSet = mp0000572.getEquivalentClasses();
        List<OntologyTermDTO> eqTerms =
                termSet.stream()
                .filter(term -> term.getAccessionId().equals("HP:0005922"))
                .collect(Collectors.toList());
        Assert.assertFalse("Expected equivalent class HP:0005922 but list is empty.", eqTerms.isEmpty());
        Assert.assertTrue("Expected equivalent class HP:0005922. Not found.", eqTerms.get(0).getAccessionId().equals("HP:0005922"));
    }


@Ignore
    @Test
    public void testReplacementOptions() throws Exception {
OntologyParser ontologyParser;
        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);

        List<OntologyTermDTO> termList = ontologyParser.getTerms();
        Map<String, OntologyTermDTO> terms =
                termList.stream()
                .filter(term -> term.getAccessionId().equals("MP:0006374") || term.getAccessionId().equals("MP:0002977") || term.getAccessionId().equals("MP:0000003"))
                .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));

        /* Test alternative ids are found for MP_0000003 (should be MP:0000011). */

        OntologyTermDTO withAltIds = terms.get("MP:0000003");
        Assert.assertTrue("Expected MP:0000003 has MP:0000011 as alt id. ", (withAltIds.getAlternateIds() != null && withAltIds.getAlternateIds().contains("MP:0000011")));

        /*
         * Test for term MP:0006374 with replacement ID MP:0008996
         */
        OntologyTermDTO withReplacementIds = terms.get("MP:0006374");
        Assert.assertNotNull("Expected term MP:0006374, a term with replacement ids. Not found.", withReplacementIds);
        Assert.assertTrue("Expected MP:0006374 to be marked obsolete but it was not.", withReplacementIds.isObsolete());
        Assert.assertNotNull("Expected MP:0006374 to have a replacement term, but the replacement term was null", withReplacementIds.getReplacementAccessionId());
        Assert.assertFalse("Expected MP:0006374 to have a replacement term, but the replacement term list was empty.", withReplacementIds.getReplacementAccessionId().isEmpty());
        Assert.assertTrue("Expected replacement accession id MP:0008996. Not found.", withReplacementIds.getReplacementAccessionId().contains("MP:0008996"));

        /*
         * Test for term MP:0002977 with consider IDs MP:0010241 and MP:0010464
         */
        OntologyTermDTO withConsiderIds = terms.get("MP:0002977");
        Assert.assertNotNull("Expected term MP:0002977, a term with consider ids. Not found.", withConsiderIds);
        Assert.assertTrue("Expected at least two consider id terms: MP:0010241 and MP:0010464, but found " + withConsiderIds.getConsiderIds().size() + ".'", withConsiderIds.getConsiderIds().size() >= 2);
        Assert.assertTrue("Expected consider id MP:0010241. Not found.", withConsiderIds.getConsiderIds().contains("MP:0010241"));
        Assert.assertTrue("Expected consider id MP:0010464. Not found.", withConsiderIds.getConsiderIds().contains("MP:0010464"));



    }

@Ignore
    @Test
    public void findSpecificEmapaTermEMAPA_18025() throws Exception {
OntologyParser ontologyParser;
        ontologyParser = new OntologyParser(downloads.get("emapa").target, downloads.get("emapa").name, OntologyParserFactory.TOP_LEVEL_EMAPA_TERMS, null);
        List<OntologyTermDTO> termList = ontologyParser.getTerms();
        Map<String, OntologyTermDTO> terms =
                termList.stream()
                        .filter(term -> term.getAccessionId().equals("EMAPA:18025"))
                        .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));

        Assert.assertTrue(terms.containsKey("EMAPA:18025") );

    }

@Ignore
    @Test
    public void findMaTermByReferenceFromMpTerm() throws Exception {
OntologyParser ontologyParser;
        ontologyParser = new OntologyParser(downloads.get("mpma").target, downloads.get("mpma").name, null, null);

        OntologyParser maParser = new OntologyParser(downloads.get("ma").target, downloads.get("ma").name, OntologyParserFactory.TOP_LEVEL_MA_TERMS, null);

        Set<String> referencedClasses = ontologyParser.getReferencedClasses("MP:0001926",
                OntologyParserFactory.VIA_PROPERTIES, "MA");
        if (referencedClasses != null && referencedClasses.size() > 0) {
            for (String id : referencedClasses) {
                OntologyTermDTO maTerm = maParser.getOntologyTerm(id);

                System.out.println("MA term "+id+" is "+maTerm+" for MP term MP:0001926");
                Assert.assertFalse(maTerm == null);
            }
        }
    }


//@Ignore
    @Test
    public void findSpecificMpTermMP_0020422() throws Exception {

        List<OntologyTermDTO> termList = ontologyParsers.get("mp").getTerms();
        Map<String, OntologyTermDTO> terms =
                termList.stream()
                        .filter(term -> term.getAccessionId().equals("MP:0020422"))
                        .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));

        Assert.assertTrue(terms.containsKey("MP:0020422"));

    }

    @Ignore
    @Test
    public void testRootTermAndTopTermsInOntologyParserMap() throws Exception {
OntologyParser ontologyParser;
        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
        List<OntologyTermDTO> termList = ontologyParser.getTerms();
        Map<String, OntologyTermDTO> terms =
                termList.stream()
                        .filter(term -> term.getAccessionId().equals("MP:0000001") || OntologyParserFactory.TOP_LEVEL_MP_TERMS.contains(term.getAccessionId()))
                        .collect(Collectors.toMap(OntologyTermDTO::getAccessionId, ontologyTermDTO -> ontologyTermDTO));

        Assert.assertTrue(terms.containsKey("MP:0000001") );
        Assert.assertTrue(terms.containsKey("MP:0010771"));
        Assert.assertFalse(terms.containsKey("MP:0010734571"));
    }

@Ignore
    @Test
    public void testTermsInSlim() throws Exception{
OntologyParser ontologyParser;
        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
        Set<String> wantedIds = new HashSet<>();
        wantedIds.add("MP:0008901");
        wantedIds.add("MP:0005395"); // "other phenotype" -  obsolete and should not be in the sim
        Set<String> termsInSlim = ontologyParser.getTermsInSlim(wantedIds, null);
        Assert.assertTrue(termsInSlim.size() == 7);
        Assert.assertTrue(!termsInSlim.contains("MP:0005395"));
    }

@Ignore
    @Test
    public void testParentInfo() throws Exception{
OntologyParser ontologyParser;
        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        Assert.assertTrue(term.getParentIds().contains("MP:0000003"));
        Assert.assertTrue(term.getParentIds().size() == 1);
        Assert.assertTrue(term.getParentNames().size() == 1);

    }

@Ignore
    @Test
    public void testChildInfo() throws Exception{
OntologyParser ontologyParser;
        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        Assert.assertTrue(term.getChildIds().contains("MP:0010024"));
        logger.debug("term.getChildIds().size() " + term.getChildIds().size() + term.getChildIds());
        Assert.assertTrue(term.getChildIds().size() == 4); // 4 child terms in the ontology without reasoning
        Assert.assertTrue(term.getChildNames().size() == 4);

        term =ontologyParser.getOntologyTerm("MP:0000003");
        logger.debug("term.getChildIds().size() " + term.getChildIds().size() + term.getChildIds());
        Assert.assertTrue(term.getChildIds().size() >= 12); // 11 child terms in the ontology without reasoning
        Assert.assertTrue(term.getChildNames().size() >= 12);

    }

@Ignore
    @Test
    public void testTopLevels() throws Exception{
OntologyParser ontologyParser;
        Set<String> topLevels = new HashSet<>(OntologyParserFactory.TOP_LEVEL_MP_TERMS);

        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, topLevels, null);

        // 1 term top level
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        Assert.assertTrue(term.getTopLevelIds().contains("MP:0005375"));
        Assert.assertTrue(term.getTopLevelIds().size() == 1);
        Assert.assertTrue(term.getTopLevelNames().size() == 1);

        Assert.assertTrue(ontologyParser.getOntologyTerm("MP:0005385") != null);

        // multiple top levels
        term = ontologyParser.getOntologyTerm("MP:0000017"); // big ears
        Assert.assertTrue(term.getTopLevelIds().contains("MP:0005382"));
        Assert.assertTrue(term.getTopLevelIds().contains("MP:0005378"));
        Assert.assertTrue(term.getTopLevelIds().contains("MP:0005377"));
        Assert.assertTrue(term.getTopLevelIds().size() == 3);
        Assert.assertTrue(term.getTopLevelNames().size() == 3);

        // term is top level itself
        term = ontologyParser.getOntologyTerm("MP:0005378");
        Assert.assertTrue(term.getTopLevelIds() == null || term.getTopLevelIds().size() == 0);
    }

@Ignore
    @Test
    public void testMpMaMapping() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {

        Set<OWLObjectPropertyImpl> viaProperties = new HashSet<>();
        viaProperties.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000052")));
        viaProperties.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/BFO_0000070")));
        viaProperties.add(new OWLObjectPropertyImpl(IRI.create("http://purl.obolibrary.org/obo/mp/mp-logical-definitions#inheres_in_part_of")));

        OntologyParser mpMaParser = new OntologyParser(Paths.get(owlpath)+ "/mp-ext-merged.owl", null, null, null);
        // Should have only MA_0000009 = adipose tissue; MP:0000003 = abnormal adipose tissue morphology
        Set<String> ma = mpMaParser.getReferencedClasses("MP:0000003", viaProperties, "MA");
        Assert.assertTrue(ma.size() == 1);
        Assert.assertTrue(ma.contains("MA:0000009"));

        Set<String> maBrain = mpMaParser.getReferencedClasses("MP:0002152", viaProperties, "MA");
        Assert.assertTrue(maBrain.contains("MA:0000168"));

    }

@Ignore
    @Test
    public void testPrefixCheck() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {

        Set<String> wantedIds = new HashSet<>();
        wantedIds.add("HP:0001892");
        OntologyParser hpParser = new OntologyParser(downloads.get("hp").target, "HP", null, wantedIds);
        Assert.assertTrue(!hpParser.getTermsInSlim().contains("UPHENO:0001002"));

    }

@Ignore
    @Test
    public void testTopLevelsForHp() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
        Set<String> wantedIds = new HashSet<>();
        wantedIds.add("HP:0001892");
        wantedIds.add("HP:0001477");
        wantedIds.add("HP:0000164");
        wantedIds.add("HP:0006202"); // child of HP:0001495
        OntologyParser hpParser = new OntologyParser(downloads.get("hp").target, "HP", OntologyParserFactory.TOP_LEVEL_HP_TERMS, wantedIds);
        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001892").getTopLevelIds().size() > 0);
        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001495").getTopLevelIds().size() > 0);
        Assert.assertTrue(hpParser.getOntologyTerm("HP:0000164").getTopLevelIds().size() > 0);
        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001477").getTopLevelIds().size() > 0);
        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001477").getTopLevelIds().contains("HP:0000478"));
    }
}