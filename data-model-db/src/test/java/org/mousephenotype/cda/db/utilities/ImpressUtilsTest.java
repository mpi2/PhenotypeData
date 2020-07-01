/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This test class is intended to run healthchecks against the observation table.
 */

package org.mousephenotype.cda.db.utilities;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.repositories.ParameterRepository;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertSame;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UtilitiesTestConfig.class})
public class ImpressUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ApplicationContext context;

    @Autowired
    DataSource komp2DataSource;

    @Autowired
    ImpressUtils impressUtils;

    @Autowired
    ParameterRepository parameterRepository;

    @Autowired
    NamedParameterJdbcTemplate jdbc1;

    @Autowired
    NamedParameterJdbcTemplate jdbc2;

    // Test identical results. No difference is expected.

    @Test
    public void testQueryDiffNoDiffsIdentical() throws Exception{
        SqlUtils sqlUtils = new SqlUtils();

        logger.debug("Testing testQueryDiffNoDiffsIdentical");

        List<String[]> actualResults = sqlUtils.queryDiff(jdbc1, jdbc1, "SELECT message FROM test");

        assert(actualResults.isEmpty());
    }


    // Test more results in jdbc2 than jdbc1. No difference is expected.
    @Test
    public void testQueryDiffNoDiffsMoreResultsInJdbc2() throws Exception{
        SqlUtils sqlUtils = new SqlUtils();

        List<String[]> expectedResults = new ArrayList<>();

        logger.debug("Testing testQueryDiffNoDiffsMoreResultsInJdbc2");

        List<String[]> actualResults = sqlUtils.queryDiff(jdbc1, jdbc2, "SELECT message FROM test");

        assert(actualResults.isEmpty());
    }

    // Test fewer results in jdbc2 than jdbc1. The extra rows in jdbc1 should be returned.
    @Test
    public void testQueryDiffTwoDiffs() throws Exception{
        SqlUtils sqlUtils = new SqlUtils();

        List<String[]> expectedResults = new ArrayList<>();
        expectedResults.add(new String[] { "MESSAGE" } );
        expectedResults.add(new String[] { "dcc line 6" } );
        expectedResults.add(new String[] { "dcc line 7" } );

        logger.debug("Testing testQueryDiffTwoDiffs");

        List<String[]> actualResults = sqlUtils.queryDiff(jdbc2, jdbc1, "SELECT message FROM test");

        assert(actualResults.size() == 3);
        for (int i = 0; i < expectedResults.size(); i++) {
            Assert.assertArrayEquals(expectedResults.get(i), actualResults.get(i));
        }
    }

    @Test
    public void testParameterCorrectType() throws SQLException {
        List<String> resources = Arrays.asList("classpath:sql/h2/schema.sql",
                "classpath:sql/h2/impressSchema.sql",
                "classpath:sql/h2/utilities/test-data.sql");

        for (String resource : resources) {
            Resource r = context.getResource(resource);
            ScriptUtils.executeSqlScript(komp2DataSource.getConnection(), r);
        }

        // Tabulation (text series) parameter
        Parameter p = parameterRepository.getFirstByStableId("TCP_VFR_001_001");
        assertSame(ImpressUtils.checkType(p, "oxoox"), ObservationType.text_series);

        // Time series parameter
        p = parameterRepository.getFirstByStableId("ESLIM_003_001_006");
        assertSame(ImpressUtils.checkType(p, "1.5"), ObservationType.time_series);


    }
}