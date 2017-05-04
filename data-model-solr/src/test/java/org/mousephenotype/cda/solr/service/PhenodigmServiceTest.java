package org.mousephenotype.cda.solr.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringRunner.class)
@TestExecutionListeners( { DependencyInjectionTestExecutionListener.class })
@ContextConfiguration(classes={TestConfigSolr.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/application.properties"})
public class PhenodigmServiceTest {

    @Autowired
    PhenodigmService phenodigmService;


    @Test
    public void getDiseaseAssociationCount() throws Exception {

        final Integer diseaseAssociationCount = phenodigmService.getDiseaseAssociationCount();

        System.out.println("Disease count: " + diseaseAssociationCount);
        assert (diseaseAssociationCount > 0);

    }

}