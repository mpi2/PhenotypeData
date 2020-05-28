package org.mousephenotype.cda.db.repositories;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.AnalyticsSignificantCallsProcedures;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertArrayEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RepositoriesTestConfig.class})
public class AnalyticsSignificantCallsProceduresRepositoryTest {
    @Autowired
    private ApplicationContext context;

    @Autowired
    private DataSource komp2DataSource;

    @Autowired
    AnalyticsSignificantCallsProceduresRepository analyticsSignificantCallsProceduresRepository;

    @Before
    public void setUp() throws Exception {

        List<String> resources = Arrays.asList(
                "h2/repository/AnalyticsSignificantCallsProceduresRepositoryTest-data.sql"
        );

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }
    }

    @Test
    public void findAll() {

        List<AnalyticsSignificantCallsProcedures> expected = Arrays.asList(
                new AnalyticsSignificantCallsProcedures(1L,  5L,   "BCM", "BCM_CSD_001", "Combined SHIRPA and Dysmorphology"),
                new AnalyticsSignificantCallsProcedures(4L,  2L,   "BCM", "IMPC_CAL_003", "Indirect Calorimetry"),
                new AnalyticsSignificantCallsProcedures(23L, 223L, "HMGU", "IMPC_CBC_002", "Clinical Chemistry"),
                new AnalyticsSignificantCallsProcedures(71L, 274L, "JAX", "IMPC_HEM_002", "Hematology"),
                new AnalyticsSignificantCallsProcedures(74L, 143L, "JAX", "IMPC_IPG_001", "Intraperitoneal glucose tolerance test (IPGTT)")
        );
        List<AnalyticsSignificantCallsProcedures> actual =
                StreamSupport
                .stream(analyticsSignificantCallsProceduresRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        assertArrayEquals(expected.toArray(), actual.toArray());
    }
}