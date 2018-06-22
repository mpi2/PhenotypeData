package org.mousephenotype.cda.loads.statistics.load;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.statistics.load.threei.TestConfigThreeI;
import org.mousephenotype.cda.loads.statistics.load.threei.ThreeIStatisticalResultLoader;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = "org.mousephenotype.cda.loads.statistics.load",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ThreeIStatisticalResultLoader.class})}
)
@ContextConfiguration(classes = TestConfigThreeI.class)
public class StatisticalResultLoaderTest {


    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource cdaDataSource;

    @Autowired
    MpTermService mpTermService;

    @Autowired
    CdaSqlUtils cdaSqlUtils;

    @Before
    public void before() throws SQLException {

        // Reload databases.
        String[] cdaSchemas = new String[] {
                "sql/h2/cda/schema.sql",
                "sql/h2/impress/impressSchema.sql",
                "sql/h2/statistical_results.sql"
        };

        for (String schema : cdaSchemas) {
            logger.info("cda schema: " + schema);
            Resource r = context.getResource(schema);
            ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), r);
        }
    }


    @Test
    public void testParseThreeIStatsResult() throws Exception {

        StatisticalResultLoader statisticalResultLoader = new StatisticalResultLoader(cdaDataSource, mpTermService);

        ClassPathResource file = new ClassPathResource("data/IMPC--IMPC--UC_Davis--UCD_001--IMPC_HEM--MGI2683688--013.tsv-test-with-weight.result");
        String[] loadArgs = new String[]{
                "--location",
                file.getFile().getAbsolutePath()
        };

        statisticalResultLoader.parameterTypeMap.put("IMPC_HEM_001_001", ObservationType.unidimensional);
        statisticalResultLoader.parameterTypeMap.put("IMPC_HEM_002_001", ObservationType.unidimensional);

        statisticalResultLoader.run(loadArgs);

        // Check that the model has a gene, allele and strain

        String statsQuery = "SELECT * FROM stats_unidimensional_results ";
        Integer resultCount = 0;
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(statsQuery)) {
            ResultSet resultSet = p.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

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

                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (i > 1 && i<rsmd.getColumnCount()) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(rsmd.getColumnName(i) + ": " + columnValue );
                }
                System.out.println("");

            }

        }

        Assert.assertEquals(12, resultCount.intValue());

    }


}