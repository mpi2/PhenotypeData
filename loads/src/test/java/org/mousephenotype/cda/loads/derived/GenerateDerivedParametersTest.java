package org.mousephenotype.cda.loads.derived;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.*;
import org.mousephenotype.cda.db.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.*;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = GenerateDerivedParametersTestConfig.class)
@Sql(scripts = {"/sql/h2/cda/schema.sql", "/sql/h2/impress/impressSchema.sql", "/sql/h2/GenerateDerivedParameterTestData.sql"})
@Rollback
public class GenerateDerivedParametersTest {

    private BiologicalModelRepository biologicalModelRepository;
    private DatasourceRepository      datasourceRepository;
    private ExperimentRepository      experimentRepository;
    private GenerateDerivedParameters generateDerivedParameters;
    private DataSource                komp2DataSource;
    private LiveSampleRepository      liveSampleRepository;
    private ObservationRepository     observationRepository;
    private OrganisationRepository    organisationRepository;
    private ParameterRepository       parameterRepository;
    private PipelineRepository        pipelineRepository;
    private ProcedureRepository       procedureRepository;
    private ProjectRepository         projectRepository;


    @Inject
    public GenerateDerivedParametersTest(
            @NotNull BiologicalModelRepository biologicalModelRepository,
            @NotNull DatasourceRepository      datasourceRepository,
            @NotNull ExperimentRepository      experimentRepository,
            @NotNull GenerateDerivedParameters generateDerivedParameters,
            @NotNull DataSource                komp2DataSource,
            @NotNull LiveSampleRepository      liveSampleRepository,
            @NotNull ObservationRepository     observationRepository,
            @NotNull OrganisationRepository    organisationRepository,
            @NotNull ParameterRepository       parameterRepository,
            @NotNull PipelineRepository        pipelineRepository,
            @NotNull ProcedureRepository       procedureRepository,
            @NotNull ProjectRepository         projectRepository)
    {
        this.biologicalModelRepository = biologicalModelRepository;
        this.datasourceRepository = datasourceRepository;
        this.experimentRepository = experimentRepository;
        this.generateDerivedParameters = generateDerivedParameters;
        this.komp2DataSource = komp2DataSource;
        this.liveSampleRepository = liveSampleRepository;
        this.observationRepository = observationRepository;
        this.parameterRepository = parameterRepository;
        this.procedureRepository = procedureRepository;
        this.organisationRepository = organisationRepository;
        this.pipelineRepository = pipelineRepository;
        this.projectRepository = projectRepository;
    }



    @Before
    public void setup() {
        generateDerivedParameters = new GenerateDerivedParameters(
                biologicalModelRepository,
                datasourceRepository,
                experimentRepository,
                komp2DataSource,
                liveSampleRepository,
                observationRepository,
                organisationRepository,
                parameterRepository,
                pipelineRepository,
                procedureRepository,
                projectRepository);
        generateDerivedParameters.loadAllDatasourcesById();
        generateDerivedParameters.loadAllOrganisationsById();
        generateDerivedParameters.loadAllPipelinesByStableId();
        generateDerivedParameters.loadAllProjectsById();
    }

    @Test
    public void IMPC_ACS_037_001Test() throws SQLException {

        generateDerivedParameters.IMPC_ACS_037_001();

        String statsQuery = "SELECT * FROM observation o INNER JOIN unidimensional_observation uo ON uo.id=o.id WHERE parameter_stable_id = 'IMPC_ACS_037_001' ";
        Integer resultCount = 0;
        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement p = connection.prepareStatement(statsQuery)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                resultCount++;
                Integer bioSampleId = resultSet.getInt("biological_sample_id");
                if (bioSampleId == 1) {
                    // This is the 4 parameter version, so the result should be
                    // 100 * ((S - ((PP1_S + PP2_S + PP3_S + PP4_S) / 4)) / S)
                    Float expectedValue = 100.0f * ((6.0f - ((7.0f + 8 + 9 + 10) / 4)) / 6);
                    assert expectedValue == resultSet.getFloat("data_point");
                } else  if (bioSampleId == 2) {
                    // This is the 3 parameter version, so the result should be
                    // 100 * ((S - ((PP1_S + PP2_S + PP3_S) / 3)) / S)
                    Float expectedValue = 100.0f * ((60.f - ((7.0f + 8 + 9 ) / 3)) / 6);
                    assert expectedValue == resultSet.getFloat("data_point");
                }

                printResultSet(resultSet);
                System.out.println("");

            }
        }

        assert resultCount == 2;
    }


    private void printResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData rsmd = resultSet.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            if (i > 1 && i<rsmd.getColumnCount()) System.out.print(",  ");
            String columnValue = resultSet.getString(i);
            System.out.print(rsmd.getColumnName(i) + ": " + columnValue );
        }
    }
}