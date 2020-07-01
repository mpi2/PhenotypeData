package org.mousephenotype.cda.loads.batch;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.loads.integration.data.config.BatchLoaderTestConfig;
import org.mousephenotype.cda.loads.integration.data.config.BatchPhenotypedColonyLoaderTestConfig;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {BatchPhenotypedColonyLoaderTestConfig.class, BatchLoaderTestConfig.class})
public class PhenotypedColonyLoaderFunctionalTest {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private DataSource cdaDataSource;


    @Before
    public void before() throws SQLException {

        // Reload databases.
        String[] cdaSchemas = new String[] {
                "sql/h2/cda/schema.sql",
                "sql/h2/impress/impressSchema.sql"
        };

        for (String schema : cdaSchemas) {
            logger.info("cda schema: " + schema);
            Resource r = context.getResource(schema);
            ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), r);
        }

        // Ensure the spring batch tables exist in the database
        org.springframework.core.io.Resource r = new ClassPathResource("org/springframework/batch/core/schema-h2.sql");
        ResourceDatabasePopulator p = new ResourceDatabasePopulator(r);
        p.execute(cdaDataSource);

        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p1 = connection.prepareStatement("DELETE FROM phenotyped_colony")) {
            p1.execute();
        }


    }


    @Test
    public void testing() throws Exception {

        final BatchStatus status = jobLauncherTestUtils.launchJob().getStatus();
        Assert.assertEquals("COMPLETED", status.toString());


        Integer colonyCount = 0;

        try (Connection connection = cdaDataSource.getConnection(); PreparedStatement p = connection.prepareStatement("SELECT count(*) AS cnt FROM phenotyped_colony")) {
            ResultSet resultSet = p.executeQuery();
            while (resultSet.next()) {
                colonyCount = resultSet.getInt("cnt");
            }
        }

        final int EXPECTED_COLONY_COUNT = 9;
        assertTrue( "Expected " + EXPECTED_COLONY_COUNT + " colonies. Found " + colonyCount, colonyCount == EXPECTED_COLONY_COUNT);
    }
}