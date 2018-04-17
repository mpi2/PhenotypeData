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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.utilities.SqlUtils;
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
import java.util.*;

/**
 ** This report queries a single database for rows matching a given sql 'LIKE' pattern in the field
 * image_record_observation.download_file_path that contain null values in the same row's full_resolution_file_path field.
 *
 * Created by mrelac on 16/03/2018.
 */
@ComponentScan("org.mousephenotype.cda.loads.reports")
public class ValidateImageRecordObservationReport extends AbstractReport implements CommandLineRunner {


    private Logger                       logger   = LoggerFactory.getLogger(this.getClass());

    private int          maxRecordsToLog  = 100;

    private NamedParameterJdbcTemplate jdbcCda;
    private SqlUtils                   sqlUtils;


    public static final String MAX_RECORDS_TO_LOG_ARG = "maxrecordstolog";
    final List<String> BLANK_ROW = Arrays.asList(new String[] { "" });

    @Inject
    public ValidateImageRecordObservationReport(NamedParameterJdbcTemplate jdbcCda, SqlUtils sqlUtils) {
        this.jdbcCda = jdbcCda;
        this.sqlUtils = sqlUtils;
    }

    /****************************
     * DATABASES: e.g. cda, komp2_base
     ****************************
     */

    @Override
    protected void initialise(String[] args) throws ReportException {
        String message;

        super.initialise(args);

        // Possible properties for this report: --like --maxrecordstolog
        OptionParser parser = new OptionParser();

        // parameter to indicate the maximum number of records to log
        parser.accepts(MAX_RECORDS_TO_LOG_ARG).withRequiredArg().ofType(Integer.class);

        parser.allowsUnrecognizedOptions();         // Ignore options already processed at a higher level.

        OptionSet options = parser.parse(args);

        if (options.hasArgument(MAX_RECORDS_TO_LOG_ARG)) {
            Integer count = commonUtils.tryParseInt(options.valueOf(MAX_RECORDS_TO_LOG_ARG));
            if (count == null) {
                message = "Invalid maxRecordsToLog {}";
                logger.error(message);
                throw new ReportException(message);
            }

            this.maxRecordsToLog = count;
        }
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ValidateImageRecordObservationReport.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ValidateImageRecordObservationReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();
        final String[] countsQuery = {
                "SELECT count(*) FROM image_record_observation WHERE (download_file_path like '%mousephenotype%' AND full_resolution_file_path IS NULL);",
                "SELECT count(*) FROM image_record_observation WHERE (download_file_path like 'file://%' AND full_resolution_file_path IS NULL);"
        };
        final String[] rowDifferencesQuery = {
                "SELECT download_file_path, full_resolution_file_path FROM image_record_observation WHERE (download_file_path like '%mousephenotype%' AND full_resolution_file_path IS NULL);",
                "SELECT download_file_path, full_resolution_file_path FROM image_record_observation WHERE (download_file_path like 'file://%' AND full_resolution_file_path IS NULL);"
        };

        for (int i = 0; i < countsQuery.length; i++) {
            int result = jdbcCda.queryForObject(countsQuery[i], new HashMap<>(), Integer.class);
            List<String> row = new ArrayList<>();
            row.add("Result of query '" + countsQuery[i] + "': " + result);
            csvWriter.writeRow(row);

            if (result > 0) {
                csvWriter.writeRow(BLANK_ROW);
                int limit = (maxRecordsToLog <= 0 ? result : maxRecordsToLog);
                csvWriter.writeRows(readRows(rowDifferencesQuery[i], limit));
            }

            csvWriter.writeRow(BLANK_ROW);
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }

    // Returns up to maxRecordsToLog rows of data.
    private List<List<String>> readRows(String query, int maxRecordsToLog) {
        List<List<String>> results = new ArrayList<>();
        List<String> row = new ArrayList<>();
        row.add("download_file_path");
        row.add("full_resolution_file_path");
        results.add(row);

         List<Map<String, Object>> data = jdbcCda.queryForList(query, new HashMap<>());
         for (Map<String, Object> rowData : data) {
             if (maxRecordsToLog <= 0) {
                 break;
             }
             row = new ArrayList<>();
             Object o = rowData.get("download_file_path");
             row.add(o == null ? "" : o.toString());
             o = rowData.get("full_resolution_file_path");
             row.add(o == null ? "" : o.toString());
             results.add(row);
             maxRecordsToLog--;
         }

        return results;
    }
}