package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.Parameter;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class ParameterRepositoryTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    private ParameterRepository parameterRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/schema.sql",
                "sql/h2/impressSchema.sql",
                "sql/h2/repositories/ParameterRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }


    @Test
    public void findById() throws Exception {

        Parameter actual = parameterRepository.findById(8L).get();
        assertNotNull(actual);

        compareFields(getExpected(), actual);
    }


    @Test
    public void getByStableId() throws Exception {

        Parameter actual = parameterRepository.getFirstByStableId("IMPC_BWT_008_001");
        assertNotNull(actual);

        compareFields(getExpected(), actual);
    }

    @Test
    public void testGetByParameterAndProcedure() throws Exception {

        Parameter actual = parameterRepository.getByStableIdAndProcedureAndPipeline("IMPC_PAT_049_002", "IMPC_PAT_002", "UCD_001");
        assertNotNull(actual.getProcedures());
        assertNotNull(actual.getProcedures().stream().findFirst().map(Procedure::getPipelines).orElse(null));

        Parameter bad = parameterRepository.getByStableIdAndProcedureAndPipeline("IMPC_PAT_049_002", "IMPC_PAT_002", "IMPC_001");
        assertNull(bad);

    }


    private void compareFields(Parameter expected, Parameter actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getStableId(), actual.getStableId());
        assertEquals(expected.getDatasource(), actual.getDatasource());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getMajorVersion(), actual.getMajorVersion());
        assertEquals(expected.getMinorVersion(), actual.getMinorVersion());
        assertEquals(expected.getUnit(), actual.getUnit());
        assertEquals(expected.getDatatype(), actual.getDatatype());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.getFormula(), actual.getFormula());
        assertEquals(expected.isRequiredFlag(), actual.isRequiredFlag());
        assertEquals(expected.isMetaDataFlag(), actual.isMetaDataFlag());
        assertEquals(expected.isImportantFlag(), actual.isImportantFlag());
        assertEquals(expected.getDerivedFlag(), actual.getDerivedFlag());
        assertEquals(expected.isAnnotateFlag(), actual.isAnnotateFlag());
        assertEquals(expected.isIncrementFlag(), actual.isIncrementFlag());
        assertEquals(expected.isOptionsFlag(), actual.isOptionsFlag());
        assertEquals(expected.getSequence(), actual.getSequence());
        assertEquals(expected.isMediaFlag(), actual.isMediaFlag());
        assertEquals(expected.getDataAnalysisNotes(), actual.getDataAnalysisNotes());
        assertEquals(expected.getStableKey(), actual.getStableKey());
    }

    private Parameter getExpected() throws Exception {
        Parameter parameter = new Parameter();
        parameter.setId(8L);
        parameter.setStableId("IMPC_BWT_008_001");
        parameter.setDatasource(getDatasource());
        parameter.setName("Body weight curve");
        parameter.setDescription("Body weight curve DESCRIPTION");
        parameter.setMajorVersion(1);
        parameter.setMinorVersion(1);
        parameter.setUnit("g");
        parameter.setDatatype("FLOAT");
        parameter.setType("seriesMediaParameter");
        parameter.setFormula("IMPC_GRS_003_001 IMPC_CAL_001_001 IMPC_DXA_001_001 IMPC_HWT_007_001 IMPC_PAT_049_001 IMPC_BWT_001_001 IMPC_ABR_001_001 IMPC_CHL_001_001 TCP_CHL_001_001 HMGU_ROT_004_001 PLOT_ALL_PARAMETERS_AS_TIMESERIES");
        parameter.setRequiredFlag(false);
        parameter.setMetaDataFlag(false);
        parameter.setImportantFlag(false);
        parameter.setDerivedFlag(true);
        parameter.setAnnotateFlag(false);
        parameter.setIncrementFlag(false);
        parameter.setOptionsFlag(false);
        parameter.setSequence(0);
        parameter.setMediaFlag(false);
        parameter.setDataAnalysisNotes("");
        parameter.setStableKey(4276L);

        return parameter;
    }

    private Date getDate(String format, String date) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(date);
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
}