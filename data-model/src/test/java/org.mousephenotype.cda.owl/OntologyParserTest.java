package org.mousephenotype.cda.owl;

/**
 * Created by ilinca on 10/08/2016.
 */

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
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class OntologyParserTest {

    private OntologyParser ontologyParser;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${owlpath}")
    protected String owlpath;

    @Before
    public void setUp() throws Exception {

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

    @Test
    public void testEFO() // Because it had that IRI used twice, once with ObjectProperty and once with AnnotationProperty RO_0002200
            throws Exception {
        ontologyParser = new OntologyParser(owlpath + "/efo.owl", "EFO");
        List<OntologyTermDTO> terms = ontologyParser.getTerms();
        if (terms.isEmpty())
            throw new Exception("testDeprecated: term list is empty!");

    }

    @Test
    public void testNarrowSynonyms()
    throws Exception {

        ontologyParser = new OntologyParser(owlpath + "/mp-hp.owl", "MP");
        OntologyTermDTO term = ontologyParser.getOntologyTerm("MP:0006325");
        Set<String> narrowSynonyms = ontologyParser.getNarrowSynonyms(term, 1);
        if (narrowSynonyms.isEmpty()){
            throw new Exception("Narrow synonyms list is empty!");
        }
        if (!narrowSynonyms.contains("conductive hearing impairment")){
            throw new Exception("Narrow synonyms list does not contain a label!");
        }
        if (!narrowSynonyms.contains("complete hearing loss")){
            throw new Exception("Narrow synonyms list does not contain an exact synonym!");
        }


    }

    @Test
    public void testEquivalent()
    throws Exception {

        ontologyParser = new OntologyParser(owlpath + "/mp-hp.owl", "MP");
        List<OntologyTermDTO> terms = ontologyParser.getTerms();
        if (terms.isEmpty()) {
            throw new Exception("Term list is empty!");
        }
        OntologyTermDTO mp0000572 = ontologyParser.getOntologyTerm("MP:0000572");
        if ( mp0000572 == null ){
            throw new Exception("Could not find MP:0000572 in mp-hp.owl");
        }
        if (mp0000572.getEquivalentClasses().isEmpty()){
            throw new Exception("Could not find equivalent class for MP:0000572 in mp-hp.owl. Equivalent class should be HP:0005922. ");
        }
        if (!mp0000572.getEquivalentClasses().isEmpty()){
            boolean foundHp = false;
            for (OntologyTermDTO cls : mp0000572.getEquivalentClasses()){
                if (cls.getAccessonId().equals("HP:0005922")){
                    foundHp = true;
                }
            }
            if (!foundHp) {
                throw new Exception("Could not find equivalent class for MP:0000572 in mp-hp.owl. Equivalent class should be HP:0005922. ");
            }
        }
    }



    @Test
    public void testDeprecated()
            throws Exception {

        // FIXME FIXME FIXME TEST ALL COMPONENT PIECES!!!


        List<Exception> exception = new ArrayList();
        ontologyParser = new OntologyParser(owlpath + "/mp.owl", "MP");
        List<OntologyTermDTO> terms = ontologyParser.getTerms();
        if (terms.isEmpty())
            throw new Exception("testDeprecated: term list is empty!");

        boolean found0006374 = false;
        boolean found0002977 = false;
        for (OntologyTermDTO term : terms) {

            /*
             * Test for term MP:0006374 with replacement IDs
             */
            if (term.getAccessonId().equals("MP:0006374")) {
                found0006374 = true;
                if (!term.isObsolete()) {
                    String message = "[FAIL] Exception in testDeprecated (" + term.getAccessonId() + " is not marked as deprecated)";
                    exception.add(new Exception(message));
                }
                if (term.getReplacementAccessionIds() == null || !term.getReplacementAccessionIds().equals("MP:0008996")) {
                    String message = "[FAIL] Exception in testDeprecated (" + term.getAccessonId() + " does not have the correct replacement term)";
                    exception.add(new Exception(message));
                }
            }

            /*
             * Test for term MP:0002977 with consider IDs
             */
            if (term.getAccessonId().equals("MP:0002977")) {
                found0002977 = true;
                if (term.getConsiderId().size() == 0) {
                    String message = "[FAIL] Exception in testDeprecated (" + term.getAccessonId() + " does not have any consider terms )";
                    exception.add(new Exception(message));
                } else {
                    Set<String> considerIds = term.getConsiderId();
                    if (!considerIds.contains("MP:0010241") || !considerIds.contains("MP:0010464")) {
                        String message = "[FAIL] Exception in testDeprecated (" + term.getAccessonId() + " does not contain the consider terms expected )";
                        exception.add(new Exception(message));
                    }
                }
            }

            // Short circuit if both terms have been seen
            if (found0002977 && found0006374) {
                break;
            }
        }

        if (!found0006374) {
            String message = "[FAIL] Expected to find class MP:0006374 but it was not found.";
            exception.add(new Exception(message));
        }

        if (!found0002977) {
            String message = "[FAIL] Expected to find class MP:0002977 but it was not found.";
            exception.add(new Exception(message));
        }

        if (!exception.isEmpty()) {
            throw exception.get(0);            // Just throw the first one because Mike does so
        }

    }

}
