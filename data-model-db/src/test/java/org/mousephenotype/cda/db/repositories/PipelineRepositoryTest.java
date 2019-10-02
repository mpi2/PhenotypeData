package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class PipelineRepositoryTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    private PipelineRepository pipelineRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/schema.sql",
                "sql/h2/impressSchema.sql",
                "sql/h2/repositories/PipelineRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }


    @Test
    public void getByStableId() throws Exception {

        Pipeline actual = pipelineRepository.getByStableId("ESLIM_003");
        assertEquals(getExpected(), actual);
    }


    private Datasource getDatasource() throws Exception {
        Datasource datasource = new Datasource();

        datasource.setId(6L);
        datasource.setName("IMPReSS");
        datasource.setShortName("IMPReSS");
        datasource.setVersion("unknown");
        datasource.setReleaseDate(getDate("yyyy-MM-dd", "2012-01-26"));

        return datasource;
    }

    private Date getDate(String format, String date) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(date);
    }

    private Pipeline getExpected() throws Exception {
        Pipeline expected = new Pipeline();
        expected.setId(34L);
        expected.setStableId("ESLIM_003");
        expected.setDatasource(getDatasource());
        expected.setName("EUMODIC Pipeline 3");
        expected.setDescription("EUMODIC Pipeline 3");
        expected.setMajorVersion(1);
        expected.setMinorVersion(0);
        expected.setStableKey(6L);

        return expected;
    }
}