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

package org.mousephenotype.cda.loads.reports;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.loads.reports.support.LoadValidateCountsQuery;
import org.mousephenotype.cda.loads.reports.support.LoadValidateMissingQuery;
import org.mousephenotype.cda.loads.reports.support.LoadsQuery;
import org.mousephenotype.cda.loads.reports.support.LoadsQueryDelta;
import org.mousephenotype.cda.reports.AbstractReport;
import org.mousephenotype.cda.reports.support.ReportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.inject.Inject;
import java.beans.Introspector;
import java.io.IOException;
import java.util.List;

/**
 ** This report produces a file of dcc database differences between a previous version and the current one.
 *
 * Created by mrelac on 24/07/2015.
 */
@ComponentScan("org.mousephenotype.cda.loads.reports")
public class ValidateExtractDccReport extends AbstractReport implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private LoadValidateMissingQuery loadValidateMissingQuery;
    private LoadValidateCountsQuery  loadValidateCountsQuery;

    private NamedParameterJdbcTemplate jdbcDccPrevious;
    private NamedParameterJdbcTemplate jdbcDccCurrent;
    private SqlUtils                   sqlUtils;

    @Inject
    public ValidateExtractDccReport(NamedParameterJdbcTemplate jdbcDccPrevious, NamedParameterJdbcTemplate jdbcDccCurrent, SqlUtils sqlUtils) {
        this.jdbcDccPrevious = jdbcDccPrevious;
        this.jdbcDccCurrent = jdbcDccCurrent;
        this.sqlUtils = sqlUtils;
    }

    /**********************
     * DATABASES: DCC, 3I
     **********************
    */

    private final String PARAMETER_COUNT_QUERY =
            "SELECT DISTINCT COUNT(s1) AS PARM_COUNT FROM (\n" +
            "    SELECT DISTINCT SUBSTRING(parameterId, 1, 8) AS s1\n" +
            "    FROM simpleParameter\n" +
            "    WHERE parameterId like 'IMPC_%'\n" +
            ") AS o1";

    private final double      DELTA        = 0.8;
    private LoadsQueryDelta[] deltaQueries = new LoadsQueryDelta[] {
            new LoadsQueryDelta("specimen COUNTS", DELTA, "SELECT count(*) FROM specimen"),
            new LoadsQueryDelta("embryo COUNTS", DELTA, "SELECT count(*) FROM embryo"),
            new LoadsQueryDelta("mouse COUNTS", DELTA, "SELECT count(*) FROM mouse"),
            new LoadsQueryDelta("experiment COUNTS", DELTA, "SELECT count(*) FROM experiment"),
            new LoadsQueryDelta("procedure_ COUNTS", DELTA, "SELECT count(*) FROM procedure_"),
            new LoadsQueryDelta("parameter_ COUNTS", 1.0, PARAMETER_COUNT_QUERY)
    };

    private LoadsQuery[] queries = new LoadsQuery[] {
            new LoadsQuery("MISSING PROCEDURES", "SELECT\n" +
                                                 "  c.centerId\n" +
                                                 ", c.project\n" +
                                                 ", c.pipeline\n" +
                                                 ", p.procedureId\n" +
                                                 "FROM center_procedure cp\n" +
                                                 "JOIN center           c ON c.pk = cp.center_pk\n" +
                                                 "JOIN procedure_       p ON p.pk = cp.procedure_pk\n")

          , new LoadsQuery("MISSING COLONIES",   "SELECT DISTINCT\n" +
                                                 "  c.centerId\n" +
                                                 ", c.project\n" +
                                                 ", c.pipeline\n" +
                                                 ", s.colonyId\n" +
                                                 "FROM experiment                      e\n" +
                                                 "JOIN experiment_specimen             es     ON es .experiment_pk = e. pk\n" +
                                                 "JOIN specimen                        s      ON s.  pk =            es.specimen_pk\n" +
                                                 "JOIN center_procedure                cp     ON cp. pk =            e. center_procedure_pk\n" +
                                                 "JOIN center                          c      ON c.  pk =            cp.center_pk\n" +
                                                 "JOIN procedure_                      p      ON p.  pk =            cp.procedure_pk\n" +
                                                 "LEFT OUTER JOIN mediaParameter       mp     ON mp. procedure_pk =  p. pk\n" +
                                                 "LEFT OUTER JOIN mediaSampleParameter msp    ON msp.procedure_pk =  p. pk\n" +
                                                 "LEFT OUTER JOIN simpleParameter      sp     ON sp. procedure_pk =  p. pk\n" +
                                                 "LEFT OUTER JOIN seriesParameter      ser    ON ser.procedure_pk =  p. pk\n" +
                                                 "LEFT OUTER JOIN seriesMediaParameter smp    ON smp.procedure_pk =  p. pk\n" +
                                                 "LEFT OUTER JOIN ontologyParameter    op     ON op. procedure_pk =  p. pk\n" +
                                                 "WHERE colonyId IS NOT NULL AND colonyId != ''\n")

            , new LoadsQuery("MISSING SPECIMENS",  "SELECT\n" +
                                                   "  c.centerId\n" +
                                                   ", c.project\n" +
                                                   ", c.pipeline\n" +
                                                   ", s.specimenId\n" +
                                                   "FROM center_specimen cs\n" +
                                                   "JOIN center          c ON c.pk = cs.center_pk\n" +
                                                   "JOIN specimen        s ON s.pk = cs.specimen_pk\n")
    };

    @Override
    protected void initialise(String[] args) throws ReportException {
        super.initialise(args);
        loadValidateCountsQuery = new LoadValidateCountsQuery(jdbcDccPrevious, jdbcDccCurrent, csvWriter);
        loadValidateCountsQuery.addQueries(deltaQueries);
        loadValidateMissingQuery = new LoadValidateMissingQuery(jdbcDccPrevious, jdbcDccCurrent, csvWriter);
        loadValidateMissingQuery.addQueries(queries);
    }


    public ValidateExtractDccReport() {
        super();
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ValidateExtractDccReport.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ValidateExtractDccReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        try {
            String db1Info    = sqlUtils.getDbInfoString(jdbcDccPrevious);
            String db2Info    = sqlUtils.getDbInfoString(jdbcDccCurrent);

            logger.info("VALIDATION STARTED AGAINST DATABASES {} AND {}", db1Info, db2Info);

        } catch (Exception e) { }

        loadValidateCountsQuery.execute();
        loadValidateMissingQuery.execute();

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}