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
 * This report produces a file of what procedures, specimens, and colonies exist
 * in a prior dcc database that no longer exist in a more recent version of the database.
 *
 * Created by mrelac on 24/07/2015.
 */
@Component
public class ImportDccMissingReport extends AbstractReport {

    private Logger   logger   = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils();

    @Autowired
    @NotNull
    @Qualifier("jdbctemplate1")
    private JdbcTemplate jdbc1;

    @Autowired
    @NotNull
    @Qualifier("jdbctemplate2")
    private JdbcTemplate jdbc2;

    /**********************
     * DATABASES: DCC, 3I
     **********************
    */
    private final String[] queries = new String[] {
            "SELECT\n" +
            "  c.centerId\n" +
            ", c.pipeline\n" +
            ", c.project\n" +
            ", p.procedureId\n" +
            "FROM center_procedure cp\n" +
            "JOIN center c ON c.pk = cp.center_pk\n" +
            "JOIN procedure_ p ON p.pk = cp.procedure_pk"

          , "SELECT\n" +
            "  c.centerId\n" +
            ", c.pipeline\n" +
            ", c.project\n" +
            ", s.specimenId\n" +
            "FROM center_specimen cs\n" +
            "JOIN center c ON c.pk = cs.center_pk\n" +
            "JOIN specimen s ON s.pk = cs.specimen_pk"

//          , "SELECT\n" +
//            "  e.experimentId\n" +
//            ", c.centerId\n" +
//            ", c.pipeline\n" +
//            ", c.project\n" +
//            ", p.procedureId\n" +
//            "FROM experiment e\n" +
//            "JOIN center_procedure cp ON cp.pk = e.center_procedure_pk\n" +
//            "JOIN center c ON c.pk = cp.center_pk\n" +
//            "JOIN procedure_ p ON p.pk = cp.procedure_pk"
        };



    public ImportDccMissingReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ImportDccMissingReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        List<String[]> results = new ArrayList<>();

        for (int i = 0; i < queries.length; i++) {
            String query = queries[i];

            try {
                List<String[]> missing = (sqlUtils.queryDiff(jdbc1, jdbc2, query));
                if (i > 0)
                    missing.add(EMPTY_ROW);
                csvWriter.writeAll(missing);

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