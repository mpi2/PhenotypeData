package org.mousephenotype.cda.loads.statistics.load.threei;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.db.statistics.MpTermService;
import org.mousephenotype.cda.loads.statistics.load.StatisticalResultLoaderConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan(basePackages = "org.mousephenotype.cda.loads.statistics.load",
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {StatisticalResultLoaderConfig.class})}
)
@ContextConfiguration(classes = TestConfigThreeI.class)
public class ThreeILoadTest {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource cdaDataSource;

    @Autowired
    MpTermService mpTermService;

    @Autowired
    CdaSqlUtils cdaSqlUtils;

    @Autowired
    @Qualifier("threeIFile")
    Resource threeIFile;

    @Before
    public void before() throws SQLException {

        // Reload databases.
        String[] cdaSchemas = new String[] {
                "sql/h2/cda/schema.sql",
                "sql/h2/impress/impressSchema.sql",
                "sql/h2/threei.sql"
        };

        for (String schema : cdaSchemas) {
            logger.info("cda schema: " + schema);
            Resource r = context.getResource(schema);
            ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), r);
        }
    }


    @Test
    public void testParseThreeIStatsResult() throws Exception {

        ThreeIStatisticalResultLoader threeIStatisticalResultLoader = new ThreeIStatisticalResultLoader(cdaDataSource, mpTermService, cdaSqlUtils, threeIFile);

        String[] loadArgs = new String[]{
        };

        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_ANA_001_001", ObservationType.unidimensional);
        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_ANA_002_001", ObservationType.unidimensional);

        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_IMM_047_001", ObservationType.unidimensional);
        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_IMM_117_001", ObservationType.unidimensional);

        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_MLN_010_001", ObservationType.unidimensional);
        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_MLN_026_001", ObservationType.unidimensional);
        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_MLN_058_001", ObservationType.unidimensional);
        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_MLN_071_001", ObservationType.unidimensional);
        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_MLN_102_001", ObservationType.unidimensional);
        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_MLN_111_001", ObservationType.unidimensional);
        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_MLN_115_001", ObservationType.unidimensional);

        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_PBI_009_001", ObservationType.unidimensional);
        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_PBI_015_001", ObservationType.unidimensional);

        threeIStatisticalResultLoader.parameterTypeMap.put("MGP_EEI_002_001", ObservationType.unidimensional);

        threeIStatisticalResultLoader.run(loadArgs);


        // Check that the model has a gene, allele and strain

        String statsQuery = "SELECT * FROM stats_unidimensional_results ";
        Integer resultCount = 0;
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(statsQuery)) {
            ResultSet resultSet = p.executeQuery();
            ResultSetMetaData rsmd = resultSet.getMetaData();

            while (resultSet.next()) {
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

        //
        // PBI data is not loaded by the 3I loader
        //
        Assert.assertEquals(13, resultCount.intValue());

    }
}
