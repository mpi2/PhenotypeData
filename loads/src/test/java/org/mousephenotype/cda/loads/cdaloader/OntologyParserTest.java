package org.mousephenotype.cda.loads.cdaloader;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.mousephenotype.cda.loads.cdaloader.OntologyParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {TestConfigLoaders.class} )
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile}/test.properties"})
public class OntologyParserTest {

    private OntologyParser ontologyParser;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setUp() throws Exception {
    	
    	String path = "/Users/ilinca/Documents/ontologies/mp.owl";
    	String prefix = "MA";
    	ontologyParser = new OntologyParser(path, prefix);
    }

    @Test
    public void testgetTerms() {

    	List<OntologyTerm> terms = ontologyParser.getTerms();
    	Assert.assertTrue(terms.size() > 1000);

    }

}
