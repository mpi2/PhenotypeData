package org.mousephenotype.cda.loads.derived;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.*;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = GenerateDerivedParametersTestConfig.class)
@Sql(scripts = {"/sql/h2/cda/schema.sql", "/sql/h2/impress/impressSchema.sql", "/sql/h2/GenerateDerivedParameterTestData.sql"})
@Rollback
public class GenerateDerivedParametersTest {

    @Autowired private BiologicalModelRepository biologicalModelRepository;
    @Autowired private DatasourceRepository      datasourceRepository;
    @Autowired private ExperimentRepository      experimentRepository;
    @Autowired private DataSource                komp2DataSource;
    @Autowired private LiveSampleRepository      liveSampleRepository;
    @Autowired private ObservationRepository     observationRepository;
    @Autowired private OrganisationRepository    organisationRepository;
    @Autowired private ParameterRepository       parameterRepository;
    @Autowired private PipelineRepository        pipelineRepository;
    @Autowired private ProcedureRepository       procedureRepository;
    @Autowired private ProjectRepository         projectRepository;

    private GenerateDerivedParameters generateDerivedParameters;

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