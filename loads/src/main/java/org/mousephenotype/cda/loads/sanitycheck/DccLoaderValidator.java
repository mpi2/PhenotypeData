/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.loads.sanitycheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

/**
 * Created by mrelac on 19/07/16.
 *
 * This class is intended to be a command-line callable java main program that validates a pair of dcc data loaded databases.
 *
 * Usage:
 *
 *
 *
 *
 */
@Component
public class DccLoaderValidator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("jdbctemplate1")
    private JdbcTemplate jdbctemplate1;

    @Autowired
    @Qualifier("jdbctemplate2")
    private JdbcTemplate jdbctemplate2;


    @Bean
    public int run() {


        logger.info("Validation start.");

        SqlRowSet rs1 = jdbctemplate1.queryForRowSet("SELECT * FROM specimen");

        SqlRowSet rs2 = jdbctemplate2.queryForRowSet("SELECT * FROM specimen");

//        System.out.println(rs1.getString("specimenId"));

        logger.info("Validation complete.");

        return 0;
    }
}