/*******************************************************************************
 * Copyright © 2017 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.integration.data;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.loads.common.CdaSqlUtils;
import org.mousephenotype.cda.loads.integration.data.config.TestConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.Map;

/**
 * This is an end-to-end integration data test class that uses an in-memory database to populate a small dcc, cda_base,
 * and cda set of databases.
 * The specimen and experiment tested here was missing from dcc_6_0 and dcc_6_1 but both were present in the live komp2 database.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ProjectMapperThreeITest {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired @NotNull private CdaSqlUtils cdaSqlUtils;
    @Autowired @NotNull private DataSource cdaDataSource;
    @Autowired @NotNull private ApplicationContext context;


    @Test
    public void testCaseInsensitiveProjectLookup() throws SQLException {


        // Reload databases.
        String[] cdaSchemas = new String[]{
                "sql/h2/cda/schema.sql",
                "sql/h2/impress/impressSchema.sql"
        };

        for (String schema : cdaSchemas) {
            logger.info("cda schema: " + schema);
            Resource r = context.getResource(schema);
            ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), r);
        }

        final Map<String, Integer> m = cdaSqlUtils.getCdaProject_idsByDccProject();

        Assert.assertTrue(m.get("Eumodic") == 1);
        Assert.assertTrue(m.get("EUMODIC") == 1);
        Assert.assertTrue(m.get("3i") == 27);
        Assert.assertTrue(m.get("IMPC") == 4);

        logger.info("Getting Eumodic, returned {} (Should be 1)", m.get("Eumodic"));
    }

    @Test
    public void testCaseSensitiveProjectLookup() throws SQLException {


        // Reload databases.
        String[] cdaSchemas = new String[]{
                "sql/h2/cda/schema.sql",
                "sql/h2/impress/impressSchema.sql"
        };

        for (String schema : cdaSchemas) {
            logger.info("cda schema: " + schema);
            Resource r = context.getResource(schema);
            ScriptUtils.executeSqlScript(cdaDataSource.getConnection(), r);
        }

        final Map<String, Integer> m = cdaSqlUtils.getCdaOrganisation_idsByDccCenterId();

        logger.info("Getting Bcm, returned {} (Should be 25)", m.get("Bcm"));
        logger.info("Getting BCM, returned {} (Should be null)", m.get("BCM"));

        Assert.assertTrue(m.get("Bcm") == 25);
        Assert.assertTrue(m.get("BCM") == null);
    }
}