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

import org.mousephenotype.cda.common.Constants;
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
 ** This class offers an implementation of a report framework that compares two database versions with matching schemas
 * with the given queries. If there are any results in the queries run against the previous database that do not exist
 * in the current database, each query's missing rows are written to the provided {@code csvWriter} and an error is
 * written to the returned {@link RunStatus}.
 *
 * @return a {@link RunStatus} indicating the status of the query execution(s).
 *
 * Created by mrelac on 24/07/2015.
 */
public class LoadValidateMissingQuery {

    private Logger   logger   = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils();

    private List<LoadsQuery> loadsQueryList = new ArrayList<>();
    private NamedParameterJdbcTemplate jdbcPrevious;
    private NamedParameterJdbcTemplate jdbcCurrent;
    private MpCSVWriter                csvWriter;

    public LoadValidateMissingQuery(NamedParameterJdbcTemplate jdbcPrevious, NamedParameterJdbcTemplate jdbcCurrent, MpCSVWriter  csvWriter) {
        this.jdbcPrevious = jdbcPrevious;
        this.jdbcCurrent = jdbcCurrent;
        this.csvWriter = csvWriter;
    }

    /**
     * Add a query to the list of queries to be executed.
     * @param loadsQuery
     */
    public void addQuery(LoadsQuery loadsQuery) {
        this.loadsQueryList.add(loadsQuery);
    }

    /**
     * Add a query to the list of queries to be executed.
     * @param name
     * @param query
     */
    public void addQuery(String name, String query) {
        this.loadsQueryList.add(new LoadsQuery(name, query));
    }

    public void addQueries(LoadsQuery[] loadsQueries) {
        this.loadsQueryList = new ArrayList(Arrays.asList(loadsQueries));
    }

    /**
     * Execute the queries. If there are any results in the query run against the previous database that do not exist
     * in the current database, an error is written to the returned {@link RunStatus}.
     * @return  a {@link RunStatus} with a list of errors
     * @throws ReportException
     */
    public RunStatus execute() throws ReportException {

        RunStatus status = new RunStatus();

        for (LoadsQuery loadsQuery : loadsQueryList) {

            try {

                logger.info("Query {}:\n{}", loadsQuery.getName(), loadsQuery.getQuery());
                List<String[]> missing = sqlUtils.queryDiff(jdbcPrevious, jdbcCurrent, loadsQuery.getQuery());
                if ( ! missing.isEmpty()) {

                    logger.warn("{} ROWS MISSING", missing.size());
                    String[] summary = new String[]{Integer.toString(missing.size()) + " " + loadsQuery.getName() + ":"};
                    if (status.hasErrors())
                        csvWriter.writeNext(Constants.EMPTY_ROW);
                    csvWriter.writeNext(summary);
                    csvWriter.writeAll(missing);
                    status.addError("missing rows");
                } else {
                    System.out.println("SUCCESS");
                }

                System.out.println();


            } catch (Exception e) {

                throw new ReportException(e);
            }
        }

        return status;
    }
}