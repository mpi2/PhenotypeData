package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.*;
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
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class ExperimentRepositoryTest {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    ExperimentRepository experimentRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "sql/h2/schema.sql",
                "sql/h2/impressSchema.sql",
                "sql/h2/repositories/ExperimentRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void save() throws Exception {

        Experiment expected = createExperiment(419040);
        experimentRepository.save(expected);
        Experiment actual = experimentRepository.findById(419040L).get();
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getDatasource(), actual.getDatasource());
        assertEquals(expected.getExternalId(), actual.getExternalId());
        assertEquals(expected.getSequenceId(), actual.getSequenceId());
        assertEquals(expected.getDateOfExperiment(), actual.getDateOfExperiment());
        assertEquals(expected.getOrganisation().getId(), actual.getOrganisation().getId());
        assertEquals(expected.getProject().getId(), actual.getProject().getId());
        assertEquals(expected.getPipeline().getId(), actual.getPipeline().getId());
        assertEquals(expected.getPipelineStableId(), actual.getPipelineStableId());
        assertEquals(expected.getProcedure().getId(), actual.getProcedure().getId());
        assertEquals(expected.getProcedureStableId(), actual.getProcedureStableId());
        assertEquals(expected.getModel().getId(), actual.getModel().getId());
        assertEquals(expected.getColonyId(), actual.getColonyId());
        assertEquals(expected.getMetadataCombined(), actual.getMetadataCombined());
        assertEquals(expected.getMetadataGroup(), actual.getMetadataGroup());
        assertEquals(expected.getProcedureStatus(), actual.getProcedureStatus());
        assertEquals(expected.getProcedureStatusMessage(), actual.getProcedureStatusMessage());

    }

    private Experiment createExperiment(Integer id) throws Exception {

        Experiment experiment = new Experiment();
        experiment.setId(id.longValue());
        experiment.setDatasource(getDatasource(22));
        experiment.setExternalId("4429264");
        experiment.setSequenceId("4429264");
        experiment.setDateOfExperiment(getDate("yyyy-MM-dd", "2017-05-25"));
        experiment.setOrganisation(getOrganisation(7));
        experiment.setProject(getProject(16));
        experiment.setPipeline(getPipeline(2));
        experiment.setPipelineStableId("HRWL_001");
        experiment.setProcedure(getProcedure(1));
        experiment.setProcedureStableId("IMPC_BWT_001");
        experiment.setModel(getBiologicalModel(39787));
        experiment.setColonyId(null);
        experiment.setMetadataCombined("");
        experiment.setMetadataGroup("d41d8cd98f00b204e9800998ecf8427e");
        experiment.setProcedureStatus("IMPC_PSC_015");
        experiment.setProcedureStatusMessage("Weight record deleted for unknown reason");

        return experiment;
    }

    private Date getDate(String format, String date) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(date);
    }

    private BiologicalModel getBiologicalModel(Integer id) {
        BiologicalModel biologicalModel = new BiologicalModel();
        biologicalModel.setId(id.longValue());

        return biologicalModel;
    }

    private Datasource getDatasource(Integer id) throws Exception {
        Datasource datasource = new Datasource();

        datasource.setId(id.longValue());
        datasource.setName("International Mouse Phenotyping Consortium");
        datasource.setShortName("IMPC");
        datasource.setVersion("2010-11-15");
        datasource.setReleaseDate(getDate("yyyy-MM-dd", "2010-11-15"));

        return datasource;
    }

    private Organisation getOrganisation(Integer id) {
        Organisation organisation = new Organisation();
        organisation.setId(id.longValue());

        return organisation;
    }

    private Pipeline getPipeline(Integer id) throws ParseException {
        Pipeline pipeline = new Pipeline();
        pipeline.setId(id.longValue());
        return pipeline;
    }

    private Procedure getProcedure(Integer id) {
        Procedure procedure = new Procedure();
        procedure.setId(id.longValue());

        return procedure;
    }

    private Project getProject(Integer id) {
        Project project = new Project();
        project.setId(id.longValue());

        return project;
    }
}