package org.mousephenotype.cda.owl;

/**
 * Created by ilinca on 10/08/2016.
 */

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
        downloads.put("mp", new Download("MP", "ftp://ftp.informatics.jax.org/pub/reports/mp.owl", owlpath + "/mp.owl"));

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
                System.out.println("DOWNLOADING " + download.url + " to " + download.target);

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
                    url = new URL(download.url);
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
                ontologyParser = new OntologyParser(file.getPath(), prefix);
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

        if (!exception.isEmpty()) {
            throw exception.get(0);            // Just throw the first one.
        }
    }

    // Because it had that IRI used twice, once with ObjectProperty and once with AnnotationProperty RO_0002200
    @Test
    public void testEFO()  throws Exception {

        ontologyParser = new OntologyParser(downloads.get("efo").target, downloads.get("efo").name);
        List<OntologyTermDTO> terms = ontologyParser.getTerms();
        Assert.assertFalse("Expected at least one term.", terms.isEmpty());
    }

    @Test
    public void testNarrowSynonyms() throws Exception {

        System.out.println("target: " + downloads.get("mphp").target);
        System.out.println("name:   " + downloads.get("mphp").name);
        ontologyParser = new OntologyParser(downloads.get("mphp").target, downloads.get("mphp").name);
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0006325");

        Set<String> narrowSynonyms = ontologyParser.getNarrowSynonyms(term, 1);

        Assert.assertFalse("Narrow synonyms list is empty!", narrowSynonyms.isEmpty());
        Assert.assertTrue("Narrow synonyms list does not contain a label!", narrowSynonyms.contains("conductive hearing impairment"));
        Assert.assertTrue("Narrow synonyms list does not contain an exact synonym!", narrowSynonyms.contains("complete hearing loss"));
    }

    @Test
    public void testEquivalent() throws Exception {

        ontologyParser = new OntologyParser(downloads.get("mphp").target, downloads.get("mphp").name);
        List<OntologyTermDTO> terms = ontologyParser.getTerms();
        Assert.assertFalse("Term list is empty!", terms.isEmpty());

        OntologyTermDTO mp0000572 = ontologyParser.getOntologyTerm("MP:0000572");
        Assert.assertNotNull("Could not find MP:0000572 in mp-hp.owl", mp0000572);

        Assert.assertFalse("Could not find equivalent class for MP:0000572 in mp-hp.owl. Equivalent class should be HP:0005922.", mp0000572.getEquivalentClasses().isEmpty());
        Set<OntologyTermDTO> termSet = mp0000572.getEquivalentClasses();
        List<OntologyTermDTO> eqTerms =
                termSet.stream()
                .filter(term -> term.getAccessonId().equals("HP:0005922"))
                .collect(Collectors.toList());
        Assert.assertFalse("Expected equivalent class HP:0005922 but list is empty.", eqTerms.isEmpty());
        Assert.assertTrue("Expected equivalent class HP:0005922. Not found.", eqTerms.get(0).getAccessonId().equals("HP:0005922"));
    }

    @Test
    public void testDeprecated() throws Exception {

        ontologyParser = new OntologyParser(downloads.get("mp").target, downloads.get("mp").name);

        List<OntologyTermDTO> termList = ontologyParser.getTerms();
        Map<String, OntologyTermDTO> terms =
                termList.stream()
                .filter(term -> term.getAccessonId().equals("MP:0006374") || term.getAccessonId().equals("MP:0002977"))
                .collect(Collectors.toMap(OntologyTermDTO::getAccessonId, ontologyTermDTO -> ontologyTermDTO));

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
}