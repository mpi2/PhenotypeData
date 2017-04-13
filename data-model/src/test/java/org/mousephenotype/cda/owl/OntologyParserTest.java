package org.mousephenotype.cda.owl;

/**
 * Created by ilinca on 10/08/2016.
 */

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.utilities.UrlUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class OntologyParserTest {

    public static boolean               downloadFiles = false;
    private       Map<String, Download> downloads     = new HashMap<>();  // key = map name. value = download info.
    public        boolean               doDownload    = true;
    private final Logger                logger        = LoggerFactory.getLogger(this.getClass());
    private       OntologyParser        ontologyParser;


    @NotNull
    @Value("${owlpath}")
    protected String owlpath;

    @Before
    public void setUp() throws Exception {

        downloads.put("efo", new Download("EFO", "http://www.ebi.ac.uk/efo/efo.owl", owlpath + "/efo.owl"));
        downloads.put("mphp", new Download("MP", "http://build-artifacts.berkeleybop.org/build-mp-hp-view/latest/mp-hp-view.owl", owlpath + "/mp-hp.owl"));
        downloads.put("mp", new Download("MP", "http://purl.obolibrary.org/obo/mp.owl", owlpath + "/mp.owl"));
        downloads.put("hp", new Download("HP", "http://purl.obolibrary.org/obo/hp.owl", owlpath + "/hp.owl"));

        if ( ! downloadFiles) {
            downloadFiles();
            downloadFiles = true;
        }
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

    private void downloadFiles() {

        try {
            Files.createDirectories(Paths.get(owlpath));
        } catch (IOException e) {
            System.err.println("Create owlpath directory '" + owlpath + "' failed. Reason: " + e.getLocalizedMessage());
        }

        if (doDownload) {
            for (Download download : downloads.values()) {
                // Download the owl files.

                FileOutputStream    fos;
                ReadableByteChannel rbc;
                final DateFormat    DATE_FORMAT    = new SimpleDateFormat("yyyyMMddHHmmss");
                String              outputAppender = DATE_FORMAT.format(new Date());
                String              target;
                String              targetTemp;
                URL                 url;

                target = download.target;
                targetTemp = target + "." + outputAppender;
                try {
                    url = new URL(UrlUtils.getRedirectedUrl(download.url));
                    if (download.url.equals(url.toString())) {
                        System.out.println("DOWNLOADING " + url.toString() + " to " + download.target);
                    } else {
                        System.out.println("DOWNLOADING " + download.url + " (remapped to " + url.toString() + ") to " + download.target);
                    }
                    rbc = Channels.newChannel(url.openStream());
                    fos = new FileOutputStream(targetTemp);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    Files.move(Paths.get(targetTemp), Paths.get(target), StandardCopyOption.REPLACE_EXISTING);

                } catch (IOException e) {
                    logger.error(download.url + " -> " + target + " download failed. Reason: " + e.getLocalizedMessage());
                }
            }
        }
    }


    @Ignore
    @Test
    public void testOwlOntologyDownloads() throws Exception {
        String message;
        List<Exception> exception = new ArrayList();
        File owlpathFile = new File(owlpath);
        File[] owlFiles = owlpathFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".owl");
            }
        });

        String prefix;
        for (File file : owlFiles) {
            prefix = file.getName().replace(".owl", "").toUpperCase();
            try {
                ontologyParser = new OntologyParser(file.getPath(), prefix, null, null);
            } catch (Exception e) {
                message = "[FAIL - " + prefix + "] Exception in " + file.getPath() + "(" + prefix + "): " + e.getLocalizedMessage();
                exception.add(e);
                System.out.println(message + "\n");
                continue;
            }
            List<OntologyTermDTO> terms = ontologyParser.getTerms();
            if (terms.size() > 700) {
                System.out.println("[PASS - " + prefix + "] - " + file.getPath() + ". Size: " + terms.size());
            } else {
                System.out.println("[FAIL - " + prefix + "] - " + file.getPath() + ". Size: " + terms.size());
            }
            System.out.println();
        }

        if ( ! exception.isEmpty()) {
            throw exception.get(0);            // Just throw the first one.
        }
    }


    // Because it had that IRI used twice, once with ObjectProperty and once with AnnotationProperty RO_0002200
    @Test
    public void testEFO()  throws Exception {

        ontologyParser = new OntologyParser(downloads.get("efo").target, downloads.get("efo").name, null, null);
        List<OntologyTermDTO> terms = ontologyParser.getTerms();
        Assert.assertFalse("Expected at least one term.", terms.isEmpty());

    }


    @Test
    public void testNarrowSynonyms() throws Exception {

        System.out.println("target: " + downloads.get("mphp").target);
        System.out.println("name:   " + downloads.get("mphp").name);
        ontologyParser = new OntologyParser(downloads.get("mphp").target, downloads.get("mphp").name, null, null);
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0006325");

        Set<String> narrowSynonyms = ontologyParser.getNarrowSynonyms(term, 1);

        Assert.assertFalse("Narrow synonyms list is empty!", narrowSynonyms.isEmpty());
        Assert.assertTrue("Narrow synonyms list does not contain a label!", narrowSynonyms.contains("conductive hearing impairment"));
        Assert.assertTrue("Narrow synonyms list does not contain an exact synonym!", narrowSynonyms.contains("complete hearing loss"));

        // Test both HP and MP terms are considered.
        // Abnormal glucose homeostasis MP:0002078 is equivalent to HP:0011014
        term = ontologyParser.getOntologyTerm("MP:0002078");

        Assert.assertTrue("HP synonym not found, was looking for Abnormal C-peptide level ." , ontologyParser.getNarrowSynonyms(term,2).contains("Abnormal C-peptide level"));

    }


    @Test
    public void testEquivalent() throws Exception {

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


    @Test
    public void testReplacementOptions() throws Exception {

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

    @Test
    public void testTermsInSlim() throws Exception{

        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
        Set<String> wantedIds = new HashSet<>();
        wantedIds.add("MP:0008901");
        wantedIds.add("MP:0005395"); // "other phenotype" -  obsolete and should not be in the sim
        Set<String> termsInSlim = ontologyParser.getTermsInSlim(wantedIds, null);
        Assert.assertTrue(termsInSlim.size() == 7);
        Assert.assertTrue(!termsInSlim.contains("MP:0005395"));
    }

    @Test
    public void testParentInfo() throws Exception{

        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        Assert.assertTrue(term.getParentIds().contains("MP:0000003"));
        Assert.assertTrue(term.getParentIds().size() == 1);
        Assert.assertTrue(term.getParentNames().size() == 1);

    }

    @Test
    public void testChildInfo() throws Exception{

        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, null, null);
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        Assert.assertTrue(term.getChildIds().contains("MP:0010024"));
        System.out.println("term.getChildIds().size() " + term.getChildIds().size() + term.getChildIds());
        Assert.assertTrue(term.getChildIds().size() == 4); // 4 child terms in the ontology without reasoning
        Assert.assertTrue(term.getChildNames().size() == 4);

        term =ontologyParser.getOntologyTerm("MP:0000003");
        System.out.println("term.getChildIds().size() " + term.getChildIds().size() + term.getChildIds());
        Assert.assertTrue(term.getChildIds().size() == 11); // 11 child terms in the ontology without reasoning
        Assert.assertTrue(term.getChildNames().size() == 11);

    }

    @Test
    public void testTopLevels() throws Exception{

        Set<String> topLevels = new HashSet<>(Arrays.asList("MP:0010768", "MP:0002873", "MP:0001186", "MP:0003631",
                "MP:0003012", "MP:0005367",  "MP:0005369", "MP:0005370", "MP:0005371", "MP:0005377", "MP:0005378", "MP:0005375", "MP:0005376",
                "MP:0005379", "MP:0005380",  "MP:0005381", "MP:0005384", "MP:0005385", "MP:0005382", "MP:0005388", "MP:0005389", "MP:0005386",
                "MP:0005387", "MP:0005391",  "MP:0005390", "MP:0005394", "MP:0005397"));

        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name, topLevels, null);

        // 1 term top level
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0005452");  // abnormal adipose tissue amount
        Assert.assertTrue(term.getTopLevelIds().contains("MP:0005375"));
        Assert.assertTrue(term.getTopLevelIds().size() == 1);
        Assert.assertTrue(term.getTopLevelNames().size() == 1);

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

    @Test
    public void testPrefixCheck() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {

        Set<String> wantedIds = new HashSet<>();
        wantedIds.add("HP:0001892");
        OntologyParser hpParser = new OntologyParser(downloads.get("hp").target, "HP", null, wantedIds);
        Assert.assertTrue(!hpParser.getTermsInSlim().contains("UPHENO:0001002"));

    }

    @Test
    public void testTopLevelsForHp() throws OWLOntologyCreationException, OWLOntologyStorageException, IOException {
        Set<String> wantedIds = new HashSet<>();
        wantedIds.add("HP:0001892");
        wantedIds.add("HP:0001477");
        wantedIds.add("HP:0000164");
        wantedIds.add("HP:0006202"); // child of HP:0001495
        OntologyParser hpParser = new OntologyParser(downloads.get("hp").target, "HP", OntologyParserFactory.TOP_LEVEL_HP_TERMS, wantedIds);
        System.out.println("1  - " + hpParser.getOntologyTerm("HP:0001892").getTopLevelIds());
        System.out.println("2  - " + hpParser.getOntologyTerm("HP:0001495").getTopLevelIds());
        System.out.println("3  - " + hpParser.getOntologyTerm("HP:0000164").getTopLevelIds());
        System.out.println("4  - " + hpParser.getOntologyTerm("HP:0001477").getTopLevelIds());
        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001892").getTopLevelIds().size() > 0);
        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001495").getTopLevelIds().size() > 0);
        Assert.assertTrue(hpParser.getOntologyTerm("HP:0000164").getTopLevelIds().size() > 0);
        Assert.assertTrue(hpParser.getOntologyTerm("HP:0001477").getTopLevelIds().size() > 0);
    }

}