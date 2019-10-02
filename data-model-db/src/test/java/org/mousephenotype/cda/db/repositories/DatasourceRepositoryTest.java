package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.Datasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class DatasourceRepositoryTest {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    DatasourceRepository datasourceRepository;

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
    public void getByShortName() throws Exception {
        Datasource datasource = datasourceRepository.getByShortName("MP");
        assertTrue(datasource.getId() == 5L);
        assertTrue(datasource.getName().equals("Mammalian Phenotype"));
        assertTrue(datasource.getShortName().equals("MP"));
        assertTrue(datasource.getVersion().equals("JAX"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(datasource.getReleaseDate().getTime(), formatter.parse("2012-01-09").getTime());
    }

    @Test
    public void getById() throws Exception {
        Datasource datasource = datasourceRepository.findById(6L).get();
        assertTrue(datasource.getId() == 6L);
        assertTrue(datasource.getName().equals("IMPReSS"));
        assertTrue(datasource.getShortName().equals("IMPReSS"));
        assertTrue(datasource.getVersion().equals("unknown"));

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals(datasource.getReleaseDate().getTime(), formatter.parse("2012-01-26").getTime());
    }
}