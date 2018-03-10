package org.mousephenotype.cda.loads.statistics.load.threei;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.statistics.load.MpTermService;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                "sql/h2/cda/threei.sql"
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

        threeIStatisticalResultLoader.run(loadArgs);


        // Check that the model has a gene, allele and strain

        String statsQuery = "SELECT * FROM stats_unidimensional_results limit 10 ";
        Integer modelCount = 0;
        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement(statsQuery)) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                modelCount++;
            }

        }

        Assert.assertEquals(2, modelCount.intValue());

    }
}
