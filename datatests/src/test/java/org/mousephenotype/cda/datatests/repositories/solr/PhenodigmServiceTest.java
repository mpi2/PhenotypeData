package org.mousephenotype.cda.datatests.repositories.solr;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.PhenodigmService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class PhenodigmServiceTest {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PhenodigmService phenodigmService;


    @Test
    public void getDiseaseAssociationCount() throws Exception {

        final Integer diseaseAssociationCount = phenodigmService.getDiseaseAssociationCount();

        assertTrue ("Expected at least one disease association. Actual count: " + diseaseAssociationCount, diseaseAssociationCount > 0);
    }
}