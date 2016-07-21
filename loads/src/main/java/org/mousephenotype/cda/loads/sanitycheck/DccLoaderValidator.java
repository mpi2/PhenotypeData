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
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

/**
 * Created by mrelac on 19/07/16.
 *
 * This class is intended to be a command-line callable java main program that validates a pair of dcc data loaded databases.
 *
 * Usage:   --profile=shanti -DskipTests
 */
@Component
public class DccLoaderValidator {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String[] queries = new String[] {
            "SELECT\n" +
            "  c.centerId\n" +
            ", c.pipeline\n" +
            ", c.project\n" +
            ", p.procedureId\n" +
            "FROM center_procedure cp\n" +
            "JOIN center c ON c.pk = cp.center_pk\n" +
            "JOIN procedure_ p ON p.pk = cp.procedure_pk",

            "SELECT\n" +
            "  c.centerId\n" +
            ", c.pipeline\n" +
            ", c.project\n" +
            ", s.specimenId\n" +
            "FROM center_specimen cs\n" +
            "JOIN center c ON c.pk = cs.center_pk\n" +
            "JOIN specimen s ON s.pk = cs.specimen_pk",

            "SELECT * FROM specimen",

            "SELECT * FROM genotype",

            "SELECT\n" +
            "  e.experimentId\n" +
            ", c.centerId\n" +
            ", c.pipeline\n" +
            ", c.project\n" +
            ", p.procedureId\n" +
            "FROM experiment e\n" +
            "JOIN center_procedure cp ON cp.pk = e.center_procedure_pk\n" +
            "JOIN center c ON c.pk = cp.center_pk\n" +
            "JOIN procedure_ p ON p.pk = cp.procedure_pk"
        };

    @Autowired
    @Qualifier("jdbctemplate1")
    private JdbcTemplate jdbctemplate1;

    @Autowired
    @Qualifier("jdbctemplate2")
    private JdbcTemplate jdbctemplate2;


    @Bean
    public int run() throws DccLoaderException {
        String db1Name = "unknown";
        String db2Name = "unknown";

        try {
            db1Name = jdbctemplate1.getDataSource().getConnection().getCatalog();
            db2Name = jdbctemplate2.getDataSource().getConnection().getCatalog();

        } catch (SQLException e) {
        }

        logger.info("VALIDATION STARTED AGAINST DATABASES {} AND {}", db1Name, db2Name);

        for (String query : queries) {

            Set<List<String>> missing = new HashSet<>();

            logger.info("Query: \n{}\n", query);

            Set<List<String>> results1 = new HashSet<>();
            SqlRowSet rs1 = jdbctemplate1.queryForRowSet(query);
            while (rs1.next()) {
                results1.add(getData(rs1));
            }

            Set<List<String>> results2 = new HashSet<>();
            SqlRowSet rs2 = jdbctemplate2.queryForRowSet(query);
            while (rs2.next()) {
                results2.add(getData(rs2));
            }


            results1.add(Arrays.asList(new String[] { "Bad", "Bad", "Bad", "Bad" } ));
            results1.add(Arrays.asList(new String[] { "Bad1", "Bad", "Bad", "Bad" } ));
            results1.add(Arrays.asList(new String[] { "Bad2", "Bad", "Bad", "Bad" } ));
            results1.add(Arrays.asList(new String[] { "Bad3", "Bad", "Bad", "Bad" } ));
            results1.add(Arrays.asList(new String[] { "Bad4", "Bad", "Bad", "Bad" } ));


            // Log the rows found in results1 but not found in results2.
            results1.removeAll(results2);

            if ( ! results1.isEmpty()) {
                final int DISPLAY_WIDTH = 15;
                logger.warn("WARNING: DROPPED {} ROWS:", results1.size());
                String[] columnNames = rs1.getMetaData().getColumnNames();
                logger.warn("\t" + formatString(columnNames, DISPLAY_WIDTH));
                Iterator<List<String>> it = results1.iterator();
                while (it.hasNext()) {
                    logger.warn("\t" + formatString(it.next().toArray(new String[0]), DISPLAY_WIDTH));
                }
            } else {
                logger.info("PASSED");
            }
            logger.info(" ");
        }

        logger.info("VALIDATION COMPLETE.");

        return 0;
    }

    private String formatString(String[] row, int cellWidth) {
        String formattedString = "";

        for (int i = 0; i < row.length; i++) {
            String cell = row[i];
            if (i > 0)
                formattedString += "\t";
            formattedString += String.format("%" + cellWidth + "." + cellWidth + "s", cell);
        }

        return formattedString;
    }

    /**
     * Given an {@link SqlRowSet}, extracts each column of data, converting to type {@link String} as necessary,
     * returning the row's cells in a {@link List<String>}
     *
     * @param rs the sql result containng a row of data
     *
     * @return the row's cells in a {@link List<String>}
     *
     * @throws DccLoaderException
     */
    private List<String> getData(SqlRowSet rs) throws DccLoaderException {
        List<String> newRow = new ArrayList<>();

        SqlRowSetMetaData md = rs.getMetaData();

        // Start index at 1, as column indexes are 1-relative.
        for (int i = 1; i <= md.getColumnCount(); i++) {
            int sqlType = md.getColumnType(i);
            switch (sqlType) {
                case Types.VARCHAR:
                    newRow.add(rs.getString(i));
                    break;

                case Types.INTEGER:
                case Types.TINYINT:
                    newRow.add(Integer.toString(rs.getInt(i)));
                    break;

                case Types.BIT:
                    newRow.add(rs.getBoolean(i) ? "1" : "0");
                    break;

                default:
                    System.out.println("SQLTYPE: " + sqlType);
                    throw new DccLoaderException("No rule to handle sql type '" + md.getColumnTypeName(i) + "' (" + sqlType + ").");
            }
        }

        return newRow;
    }
}