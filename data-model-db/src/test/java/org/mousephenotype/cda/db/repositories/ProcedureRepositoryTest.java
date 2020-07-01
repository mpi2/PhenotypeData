package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class ProcedureRepositoryTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    private ProcedureRepository procedureRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/schema.sql",
                "sql/h2/impressSchema.sql",
                "sql/h2/repositories/ProcedureRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }


    @Test
    public void getByStableKey() throws Exception {

        Procedure expectedProcedure = getExpected();

        Procedure procedure = procedureRepository.getByStableKey(173L);

        compareFields(expectedProcedure, procedure);
    }


    private void compareFields(Procedure expectedProcedure, Procedure procedure) {
        assertEquals(expectedProcedure.getDatasource(), procedure.getDatasource());
        assertEquals(expectedProcedure.getId(), procedure.getId());
        assertEquals(expectedProcedure.getLevel(), procedure.getLevel());
        assertEquals(expectedProcedure.isMandatory(), procedure.isMandatory());
        assertEquals(expectedProcedure.getScheduleKey(), procedure.getScheduleKey());
        assertEquals(expectedProcedure.getStage(), procedure.getStage());
        assertEquals(expectedProcedure.getStageLabel(), procedure.getStageLabel());
        assertEquals(expectedProcedure.getStableId(), procedure.getStableId());
        assertEquals(expectedProcedure.getStableKey(), procedure.getStableKey());
        assertEquals(expectedProcedure.getName(), procedure.getName());
        assertEquals(expectedProcedure.getMajorVersion(), procedure.getMajorVersion());
        assertEquals(expectedProcedure.getMinorVersion(), procedure.getMinorVersion());
    }

    private Procedure getExpected() throws Exception {
        Procedure expectedProcedure = new Procedure();
        expectedProcedure.setId(2L);
        expectedProcedure.setLevel("housing");
        expectedProcedure.setMandatory(false);
        expectedProcedure.setParameterCollection(null);
        expectedProcedure.setMetaDataSet(null);
        expectedProcedure.setParameters(null);
        expectedProcedure.setPipelines(null);
        expectedProcedure.setScheduleKey(3L);
        expectedProcedure.setStage("Adult");
        expectedProcedure.setStageLabel("Unrestricted");
        expectedProcedure.setStableId("IMPC_HOU_001");
        expectedProcedure.setStableKey(173L);
        expectedProcedure.setName("Housing and Husbandry");
        expectedProcedure.setDescription("");
        expectedProcedure.setMajorVersion(1);
        expectedProcedure.setMinorVersion(0);
        expectedProcedure.setDatasource(getDatasource());
        return expectedProcedure;
    }

    @Test
    public void getByStableId() throws Exception {

        Procedure expected = getExpected();
        Procedure actual = procedureRepository.getByStableIdAndPipeline("IMPC_HOU_001", "IMPC_001");

        compareFields(expected, actual);
    }

    private Datasource getDatasource() throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.S");

        Datasource datasource = new Datasource();
        datasource.setId(6L);
        datasource.setName("IMPReSS");
        datasource.setShortName(datasource.getName());
        datasource.setVersion("unknown");
        datasource.setReleaseDate(format.parse("2012-01-26 00:00:00.0"));
        return datasource;
    }
}