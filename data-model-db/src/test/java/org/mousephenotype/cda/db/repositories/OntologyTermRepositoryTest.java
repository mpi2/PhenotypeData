package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.DatasourceEntityId;
import org.mousephenotype.cda.db.pojo.OntologyTerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class OntologyTermRepositoryTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    OntologyTermRepository ontologyTermRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/schema.sql",
                "sql/h2/repositories/OntologyTermRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void findById() {

        DatasourceEntityId datasourceEntityId = new DatasourceEntityId("IMPC_PARAMSC_012", 22L);
        OntologyTerm actual = ontologyTermRepository.findById(datasourceEntityId).get();

        assertEquals(getExpected(), actual);
    }

    @Test
    public void getByAccAndShortName() {
        OntologyTerm actual = ontologyTermRepository.getByAccAndShortName("IMPC_PARAMSC_012", "IMPC");
        assertNotNull(actual);
        assertEquals(getExpected(), actual);
    }

    @Test
    public void getByNameAndShortName() {
        OntologyTerm actual = ontologyTermRepository.getByTermNameAndShortName("E9.5", "IMPC");
        assertNotNull(actual);
        assertEquals(getExpectedLifeStageTerm(), actual);
    }


    private OntologyTerm getExpected() {

        OntologyTerm ontologyTerm = new OntologyTerm();

        ontologyTerm.setId(new DatasourceEntityId("IMPC_PARAMSC_012", 22));
        ontologyTerm.setName("Software failure");
        ontologyTerm.setDescription("LIMS says no");
        ontologyTerm.setIsObsolete(false);
        ontologyTerm.setReplacementAcc(null);

        return ontologyTerm;
    }


    private OntologyTerm getExpectedLifeStageTerm() {

        OntologyTerm ontologyTerm = new OntologyTerm();

        ontologyTerm.setId(new DatasourceEntityId("IMPCLS:0001", 22));
        ontologyTerm.setName("E9.5");
        ontologyTerm.setDescription("Embryonic day 9.5");
        ontologyTerm.setIsObsolete(false);
        ontologyTerm.setReplacementAcc(null);

        return ontologyTerm;
    }
}