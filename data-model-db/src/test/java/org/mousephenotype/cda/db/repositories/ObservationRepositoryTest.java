package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.BiologicalSample;
import org.mousephenotype.cda.db.pojo.Datasource;
import org.mousephenotype.cda.db.pojo.Observation;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
@Transactional
public class ObservationRepositoryTest {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    ExperimentRepository experimentRepository;

    @Autowired
    ObservationRepository observationRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/schema.sql",
                "sql/h2/impressSchema.sql",
                "sql/h2/repositories/ObservationRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void save() throws Exception {

        Observation expected = createObservation();

        assertEquals(0L, observationRepository.count());

        Observation actual = observationRepository.save(expected);
        assertNotNull(actual);

        assertEquals(1L, observationRepository.count());

        assertNotNull(actual.getId());
        assertEquals(expected.getDatasource(), actual.getDatasource());

        assertEquals(expected.getExperiment().getId(), actual.getExperiment().getId());
        assertEquals(expected.getParameter().getId(), actual.getParameter().getId());
        assertEquals(expected.getParameterStatus(), actual.getParameterStatus());
        assertEquals(expected.getParameterStatusMessage(), actual.getParameterStatusMessage());
        assertEquals(expected.getParameterStableId(), actual.getParameterStableId());
        assertEquals(expected.getPopulationId(), actual.getPopulationId());
        assertEquals(expected.getSample(), actual.getSample());
        assertEquals(expected.getType(), actual.getType());
        assertEquals(expected.isMissingFlag(), actual.isMissingFlag());
    }

    private Observation createObservation() throws Exception {

        Observation observation = new Observation();

        observation.setDatasource(getDatasource());
        observation.setExperiment(experimentRepository.findById(1L).get());
        observation.setParameter(getParameter(8305));
        observation.setParameterStatus("ESLIM_PARAMSC_005");
        observation.setParameterStatusMessage("missing data unknown reason");
        observation.setParameterStableId("");
        observation.setPopulationId(0);
        observation.setSample(getSample(40));
        observation.setType(ObservationType.categorical);
        observation.setMissingFlag(true);

        return observation;
    }

    private Datasource getDatasource() {
        Datasource datasource = new Datasource();

        datasource.setId(12L);

        return datasource;
    }

    private Parameter getParameter(Integer id) {
        Parameter parameter = new Parameter();
        parameter.setId(id.longValue());

        return parameter;
    }

    private BiologicalSample getSample(Integer id) {
        BiologicalSample biologicalSample = new BiologicalSample();
        biologicalSample.setId(id.longValue());

        return biologicalSample;
    }
}