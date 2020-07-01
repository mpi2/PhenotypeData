package org.mousephenotype.cda.loads.statistics.load.impc;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StatisticalResultLoaderTestConfig.class)
@Sql(scripts = {"/sql/h2/cda/schema.sql", "/sql/h2/impress/impressSchema.sql", "/sql/h2/statistical_results.sql"})
@Transactional
public class StatisticalResultLoaderTest {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private DataSource cdaDataSource;

    @Autowired
    MpTermService mpTermService;

    @Autowired
    CdaSqlUtils cdaSqlUtils;

    @Before
    public void before() throws SQLException {

        System.out.println("************************************** SELECT * FROM phenotype_parameter_type **************************************");
        String statsQuery = "SELECT * FROM phenotype_parameter_type";
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(statsQuery)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {
                System.out.println("DATA: " + resultSet.getString("parameter_stable_id") + ", " + resultSet.getString("observation_type"));
            }
        }
    }

@Ignore
    @Test
    public void testParseStatsResult() throws Exception {

        StatisticalResultLoader statisticalResultLoader = new StatisticalResultLoader(cdaDataSource, mpTermService);

        ClassPathResource file = new ClassPathResource("data/IMPC--IMPC--UC_Davis--UCD_001--IMPC_HEM--MGI2683688--013.tsv-test-with-weight.result");
        String[] loadArgs = new String[]{
                "--location",
                file.getFile().getAbsolutePath()
        };

        statisticalResultLoader.parameterTypeMap.put("IMPC_HEM_001_001", ObservationType.unidimensional);
        statisticalResultLoader.parameterTypeMap.put("IMPC_HEM_002_001", ObservationType.unidimensional);
        statisticalResultLoader.parameterTypeMap.put("IMPC_EYE_092_001", ObservationType.categorical);

        statisticalResultLoader.run(loadArgs);

        // Check that the model has a gene, allele and strain

        String statsQuery = "SELECT * FROM stats_unidimensional_results";
        Integer resultCount = 0;
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(statsQuery)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                if (resultSet.getString("Status").equals("Success")) {
                    Boolean hasMpTerm = resultSet.getString("mp_acc") != null ||
                            resultSet.getString("male_mp_acc") != null ||
                            resultSet.getString("female_mp_acc") != null;
                    Assert.assertTrue(hasMpTerm);
                }
                resultCount++;
                Integer bioModelId = resultSet.getInt("experimental_id");
                Assert.assertNotNull(bioModelId);
                Assert.assertTrue(bioModelId > 0);

                printResultSet(resultSet);
                System.out.println("");
            }
        }

        Assert.assertEquals(12, resultCount.intValue());

        statsQuery = "SELECT * FROM stats_categorical_results";
        resultCount = 0;
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(statsQuery)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                if (resultSet.getString("Status").equals("Success")) {
                    Boolean hasMpTerm = resultSet.getString("mp_acc") != null ||
                            resultSet.getString("male_mp_acc") != null ||
                            resultSet.getString("female_mp_acc") != null;
                    Assert.assertTrue(hasMpTerm);
                }
                resultCount++;
                Integer bioModelId = resultSet.getInt("experimental_id");
                Assert.assertNotNull(bioModelId);
                Assert.assertTrue(bioModelId > 0);

                printResultSet(resultSet);
                System.out.println("");

                if (resultSet.getString("metadata_group").equals("TEST_EYE_MALE_ONLY_SIGNIFICANT_R")) {
                    Assert.assertTrue(resultSet.getString("male_mp_acc") != null ||
                            resultSet.getString("female_mp_acc") == null);
                }
            }
        }

        Assert.assertEquals(13, resultCount.intValue());
    }


    @Test
    public void testParseStatsResultForAkt2() throws Exception {

        StatisticalResultLoader statisticalResultLoader = new StatisticalResultLoader(cdaDataSource, mpTermService);

        ClassPathResource file = new ClassPathResource("data/EuroPhenome--EUMODIC--WTSI--ESLIM_002--ESLIM_016--MGI3050593--000.tsv-with-weight.result");
        String[] loadArgs = new String[]{
                "--location",
                file.getFile().getAbsolutePath()
        };

        statisticalResultLoader.parameterTypeMap.put("ESLIM_016_001_001", ObservationType.unidimensional);
        statisticalResultLoader.parameterTypeMap.put("ESLIM_016_001_002", ObservationType.unidimensional);
        statisticalResultLoader.parameterTypeMap.put("ESLIM_016_001_003", ObservationType.unidimensional);
        statisticalResultLoader.parameterTypeMap.put("ESLIM_016_001_004", ObservationType.unidimensional);
        statisticalResultLoader.parameterTypeMap.put("ESLIM_016_001_005", ObservationType.unidimensional);
        statisticalResultLoader.parameterTypeMap.put("ESLIM_016_001_006", ObservationType.unidimensional);
        statisticalResultLoader.parameterTypeMap.put("ESLIM_016_001_007", ObservationType.unidimensional);
        statisticalResultLoader.parameterTypeMap.put("ESLIM_016_001_008", ObservationType.unidimensional);

        statisticalResultLoader.run(loadArgs);

        // Check that the model has a gene, allele and strain

        String statsQuery = "SELECT * FROM stats_unidimensional_results ";
        Integer resultCount = 0;
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(statsQuery)) {
            ResultSet resultSet = p.executeQuery();

            while (resultSet.next()) {

                if (resultSet.getString("Status").equals("Success")) {
                    Boolean hasMpTerm = resultSet.getString("mp_acc") != null ||
                            resultSet.getString("male_mp_acc") != null ||
                            resultSet.getString("female_mp_acc") != null;
                    if( ! hasMpTerm) {

                        printResultSet(resultSet);
                        System.out.println("");

                    }
                    Assert.assertTrue(hasMpTerm);
                }
                resultCount++;
                Integer bioModelId = resultSet.getInt("experimental_id");
                Assert.assertNotNull(bioModelId);
                Assert.assertTrue(bioModelId > 0);

                printResultSet(resultSet);
                System.out.println("");
            }
        }

        Assert.assertEquals(16, resultCount.intValue());
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