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

package org.mousephenotype.cda.reports;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.reports.support.ReportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.beans.Introspector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 ** This report produces a file of cda_base database differences between a previous version and the current one.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class ExtractValidateCdabaseReport extends AbstractReport {

    private Logger   logger   = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils();

    @Autowired
    @NotNull
    @Qualifier("jdbcCdaPrevious")
    private JdbcTemplate jdbcPrevious;

    @Autowired
    @NotNull
    @Qualifier("jdbcCdaCurrent")
    private JdbcTemplate jdbcCurrent;

    /********************
     * DATABASE: cda_base
     ********************
    */
    private class CdaQuery {
        public final String name;
        public final String query;

        public CdaQuery(String name, String query) {
            this.name = name;
            this.query = query;
        }
    }

    private CdaQuery[] queries = new CdaQuery[] {
            new CdaQuery("ALLELE COUNT", "SELECT count(*) FROM allele")
    };


    public ExtractValidateCdabaseReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ExtractValidateCdaReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        try {
            String db1Name = jdbcPrevious.getDataSource().getConnection().getCatalog();
            String db2Name = jdbcCurrent.getDataSource().getConnection().getCatalog();
            logger.info("VALIDATION STARTED AGAINST DATABASES {} AND {}", db1Name, db2Name);
        } catch (Exception e) { }

        List<String[]> results = new ArrayList<>();

        int badQueryCount = 0;
        for (int i = 0; i < queries.length; i++) {
            CdaQuery cdaQuery = queries[i];

            try {

                logger.info("Query {}:\n{}", cdaQuery.name, cdaQuery.query);
                List<String[]> missing = (sqlUtils.queryDiff(jdbcPrevious, jdbcCurrent, cdaQuery.query));
                if ( ! missing.isEmpty()) {

                    logger.warn("{} ROWS MISSING", missing.size());
                    String[] summary = new String[]{Integer.toString(missing.size()) + " " + cdaQuery.name + ":"};
                    if (badQueryCount > 0)
                        csvWriter.writeNext(EMPTY_ROW);
                    csvWriter.writeNext(summary);
                    csvWriter.writeAll(missing);
                    badQueryCount++;
                } else {
                    logger.info("SUCCESS");
                }


            } catch (Exception e) {

                throw new ReportException(e);
            }
        }

        csvWriter.writeAll(results);

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}