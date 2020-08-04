/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this targetFile except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.reports.support;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 ** This class offers an implementation of a report framework that compares integer value of a single table, run against
 * two database versions with matching schemas. If the ratio of the value from the current database divided by the
 * value from the previous database is less than the delta, the values are written to the  provided {@code csvWriter}
 * and an error is written to the returned {@link RunStatus}.
 *
 * @return a {@link RunStatus} indicating the status of the query execution(s).
 *
 * Created by mrelac on 24/07/2015.
 */
public class LoadValidateCountsQuery {

    private Logger   logger   = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils();

    private List<LoadsQueryDelta> loadsQueryList = new ArrayList<>();
    private NamedParameterJdbcTemplate jdbcPrevious;
    private NamedParameterJdbcTemplate jdbcCurrent;
    private MpCSVWriter                csvWriter;

    public LoadValidateCountsQuery(NamedParameterJdbcTemplate jdbcPrevious, NamedParameterJdbcTemplate jdbcCurrent, MpCSVWriter  csvWriter) {
        this.jdbcPrevious = jdbcPrevious;
        this.jdbcCurrent = jdbcCurrent;
        this.csvWriter = csvWriter;
    }



    /**
     * Add a query to the list of queries to be executed.
     * @param loadsQueryDelta
     */
    public void addQuery(LoadsQueryDelta loadsQueryDelta) {
        this.loadsQueryList.add(loadsQueryDelta);
    }

    /**
     * Add a query to the list of queries to be executed.
     * @param name
     * @param query
     */
    public void addQuery(String name, Double delta, String query) {
        this.loadsQueryList.add(new LoadsQueryDelta(name, delta, query));
    }

    public void addQueries(LoadsQueryDelta[] loadsQueriesDelta) {
        this.loadsQueryList = new ArrayList(Arrays.asList(loadsQueriesDelta));
    }

    /**
     * Execute the queries. Add the query, the previousValue, the CurrentValue, the ratio, the delta, and whether or not
     * the ratio is below the delta.
     * @return  a {@link List<String[]>} with the query and its results
     * @throws ReportException
     */
    public void execute() throws ReportException {

        String message;
        final String FORMAT_STRING = "%-7.7s\t%-70.70s\t%-15.15s\t%-15.15s\t%-10.10s\t%-10.10s\t%-15.15s";

        for (int i = 0; i < loadsQueryList.size(); i++) {
            LoadsQueryDelta loadsQuery = loadsQueryList.get(i);

            try {
                List<String[]> results = sqlUtils.queryForLongValue(jdbcPrevious, jdbcCurrent, loadsQuery.getDelta(), loadsQuery.getQuery());
                if (i == 0) {
                    message = String.format(FORMAT_STRING, results.get(0));
                    logger.info(message);
                    csvWriter.writeNext(results.get(0));
                }

                message = String.format(FORMAT_STRING, results.get(1));
                boolean belowThreshold = (results.get(1)[0].equals("FAIL") ? true : false);
                if (belowThreshold)
                    logger.warn(message);
                else
                    logger.info(message);
                csvWriter.writeNext(results.get(1));

            } catch (Exception e) {

                logger.error("Exception executing query '" + loadsQuery.getQuery() + ": ", e);

            }
        }
    }
}