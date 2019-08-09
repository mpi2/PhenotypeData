package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.Allele;
import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class AlleleRepositoryTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource h2DataSource;

    @Autowired
    private AlleleRepository alleleRepository;

    @Before
    public void setUp() throws Exception {

        // Load data.
            Resource r = context.getResource("sql/h2/repositories/AlleleRepositoryTest-data.sql");
            ScriptUtils.executeSqlScript(h2DataSource.getConnection(), r);
    }

    @Test
    public void getById_Accession() {

        Allele expectedAllele = new Allele();
        expectedAllele.setId(new DatasourceEntityId("MGI:5013777", 3L));
        expectedAllele.setBiotype(new OntologyTerm("CV:000000101", 3L));
        expectedAllele.setGene(null);
        expectedAllele.setName("targeted mutation 1a, Helmholtz Zentrum Muenchen GmbH");
        expectedAllele.setSymbol("0610009B22Rik<tm1a(EUCOMM)Hmgu>");
        expectedAllele.setSynonyms(new ArrayList<>());

        Allele allele = alleleRepository.getById_Accession("MGI:5013777");
        assertEquals(expectedAllele, allele);
    }
}