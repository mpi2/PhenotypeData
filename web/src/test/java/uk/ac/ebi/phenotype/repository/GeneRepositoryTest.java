package uk.ac.ebi.phenotype.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Created by ckchen on 14/03/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(classes = {Neo4jTestConfig.class})
//@TestPropertySource(locations = {"classpath:ogm.properties"})
@EnableNeo4jRepositories(basePackages = "uk.ac.ebi.phenotype.repository")
@Transactional
public class GeneRepositoryTest {

    private final static Logger log = LoggerFactory.getLogger(GeneRepositoryTest.class);

    public static final String geneSymbol = "Akt2";

    @Autowired
    GeneRepository geneRepository;

    @Test
    public void testGeneRepository() {
        geneRepository.deleteAll();

        Gene gene = geneRepository.findByMarkerSymbol(geneSymbol);
        if (gene == null) {
            log.debug("Gene {} not found. Creating", geneSymbol);
            gene = new Gene();
            gene.setMarkerSymbol(geneSymbol);
            geneRepository.save(gene);
        }

        Assert.notNull(geneRepository.findByMarkerSymbol(geneSymbol));
    }

}