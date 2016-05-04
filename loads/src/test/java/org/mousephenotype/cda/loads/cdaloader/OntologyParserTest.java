package org.mousephenotype.cda.loads.cdaloader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigLoaders.class} )
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile}/test.properties"})
public class OntologyParserTest {

    private OntologyParser ontologyParser;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${owlpath}")
    protected String owlpath;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testOwlOntologyDownloads() throws Exception {
        String message;
        List<Exception> exceptions = new ArrayList();
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
                exceptions.add(e);
                System.out.println(message + "\n");
                continue;
            }
            List<OntologyTerm> terms = ontologyParser.getTerms();
            if (terms.size() > 700) {
                System.out.println("[PASS - " + prefix + "] - " + file.getPath() + ". Size: " + terms.size());
            } else {
                System.out.println("[FAIL - " + prefix + "] - " + file.getPath() + ". Size: " + terms.size());
            }
            System.out.println();
        }

        if (! exceptions.isEmpty()) {
            throw exceptions.get(0);            // Just throw the first one.
        }
    }
    
    @Test 
    public void testDeprecated() 
    throws OWLOntologyCreationException{
    	
        ontologyParser = new OntologyParser(owlpath + "/mp.owl", "MP:");
        //	 --> replaced by MP:0008996
        List<OntologyTerm> terms = ontologyParser.getTerms();
        for (OntologyTerm term : terms){
        	if (term.getId().getAccession().equals("MP:0006374")){
        		//AssertTrue(term.isDeprecated());
        		// 
        	}
        }
    	
    }
    
}