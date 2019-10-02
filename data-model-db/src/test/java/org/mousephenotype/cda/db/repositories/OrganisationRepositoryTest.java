package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.Organisation;
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
public class OrganisationRepositoryTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    OrganisationRepository organisationRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/schema.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void getByName() {

        Organisation expected = createOrganisation();

        Organisation actual = organisationRepository.getByName("EBI");

        assertNotNull(actual);

        assertEquals(expected, actual);
    }

    private Organisation createOrganisation() {
        Organisation expected = new Organisation();
        expected.setId(5L);
        expected.setName("EBI");
        expected.setFullname("European Bioinformatics Institute");
        expected.setCountry("UK");
        return expected;
    }
}