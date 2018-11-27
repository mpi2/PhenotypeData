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
import org.mousephenotype.cda.loads.create.extract.cdabase.support.ImpressUtils;
import org.mousephenotype.cda.loads.reports.support.LoadValidateCountsQuery;
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
import java.util.Arrays;
import java.util.List;

/**
 ** This report produces a file of cdabase database differences between a previous version and the current one.
 *
 * Created by mrelac on 24/07/2015.
 */
@ComponentScan("org.mousephenotype.cda.loads.reports")
public class ValidateExtractImpressReport extends AbstractReport implements CommandLineRunner {

    private Logger   logger   = LoggerFactory.getLogger(this.getClass());
    private LoadValidateCountsQuery loadValidateCountsQuery;

    private NamedParameterJdbcTemplate jdbcCdabasePrevious;
    private NamedParameterJdbcTemplate jdbcCdabaseCurrent;
    private SqlUtils                   sqlUtils;

    @Inject
    public ValidateExtractImpressReport(NamedParameterJdbcTemplate jdbcCdabasePrevious, NamedParameterJdbcTemplate jdbcCdabaseCurrent, SqlUtils sqlUtils) {
        this.jdbcCdabasePrevious = jdbcCdabasePrevious;
        this.jdbcCdabaseCurrent = jdbcCdabaseCurrent;
        this.sqlUtils = sqlUtils;
    }


    /**********************
     * DATABASE: impress
     **********************
    */
    private final double      DELTA        = 1.0;
    private LoadsQueryDelta[] countQueries = new LoadsQueryDelta[] {
            new LoadsQueryDelta("phenotype_parameter COUNTS", DELTA, "SELECT count(*) FROM phenotype_parameter"),
            new LoadsQueryDelta("phenotype_parameter_eq_annotation COUNTS", DELTA, "SELECT count(*) FROM phenotype_parameter_eq_annotation"),
            new LoadsQueryDelta("phenotype_parameter_lnk_eq_annotation COUNTS", DELTA, "SELECT count(*) FROM phenotype_parameter_lnk_eq_annotation"),
            new LoadsQueryDelta("phenotype_parameter_lnk_increment COUNTS", DELTA, "SELECT count(*) FROM phenotype_parameter_lnk_increment"),
            new LoadsQueryDelta("phenotype_parameter_lnk_ontology_annotation COUNTS", DELTA, "SELECT count(*) FROM phenotype_parameter_lnk_ontology_annotation"),
            new LoadsQueryDelta("phenotype_parameter_lnk_option COUNTS", DELTA, "SELECT count(*) FROM phenotype_parameter_lnk_option"),
            new LoadsQueryDelta("phenotype_parameter_ontology_annotation COUNTS", DELTA, "SELECT count(*) FROM phenotype_parameter_ontology_annotation"),
            new LoadsQueryDelta("phenotype_parameter_option COUNTS", DELTA, "SELECT count(*) FROM phenotype_parameter_option"),
            new LoadsQueryDelta("phenotype_pipeline COUNTS", DELTA, "SELECT count(*) FROM phenotype_pipeline"),
            new LoadsQueryDelta("phenotype_pipeline_procedure COUNTS", DELTA, "SELECT count(*) FROM phenotype_pipeline_procedure"),
            new LoadsQueryDelta("phenotype_procedure COUNTS", DELTA, "SELECT count(*) FROM phenotype_procedure"),
            new LoadsQueryDelta("phenotype_procedure_meta_data COUNTS", DELTA, "SELECT count(*) FROM phenotype_procedure_meta_data"),
            new LoadsQueryDelta("phenotype_procedure_parameter COUNTS", DELTA, "SELECT count(*) FROM phenotype_procedure_parameter")
    };

    private final String pipelineQuery =
        "SELECT pi.stable_id, edb.Name AS datasource_name,pi.name, pi.major_version, pi.minor_version\n" +
        "FROM phenotype_pipeline pi\n" +
        "JOIN external_db edb ON edb.id = pi.db_id";

    private final String procedureQueryWithStage =
        "SELECT pi.stable_id, pr.stable_id, edb.Name AS datasource_name, pr.name, pr.major_version, pr.minor_version, pr.level, pr.stage, pr.stage_label\n" +
                "FROM phenotype_procedure pr\n" +
                "JOIN external_db edb ON edb.id = pr.db_id\n" +
                "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id\n" +
                "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String procedureQueryNoStage =
            "SELECT pi.stable_id, pr.stable_id, edb.Name AS datasource_name, pr.name, pr.major_version, pr.minor_version, pr.level, pr.stage_label\n" +
                    "FROM phenotype_procedure pr\n" +
                    "JOIN external_db edb ON edb.id = pr.db_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String procedureQueryNoStageNoMinor =
            "SELECT pi.stable_id, pr.stable_id, edb.Name AS datasource_name, pr.name, pr.major_version, pr.level, pr.stage_label\n" +
                    "FROM phenotype_procedure pr\n" +
                    "JOIN external_db edb ON edb.id = pr.db_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameterQuery =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name, pa.name, pa.major_version, pa.minor_version,\n" +
                    "  pa.unit, pa.datatype, pa.parameter_type, pa.formula,\n" +
                    "  pa.increment, pa.options, pa.sequence, pa.media,\n" +
                    "  pa.data_analysis, pa.data_analysis_notes\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameterQueryNoNotes =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name, pa.name, pa.major_version, pa.minor_version,\n" +
                    "  pa.unit, pa.datatype, pa.parameter_type, pa.formula,\n" +
                    "  pa.increment, pa.options, pa.sequence, pa.media,\n" +
                    "  pa.data_analysis\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";


    private List<ValidationQuery> contentQueries = Arrays.asList(new ValidationQuery[] {
            new ValidationQuery("pipeline  ", pipelineQuery),
            new ValidationQuery("procedureWithStage", procedureQueryWithStage),
            new ValidationQuery("ProcedureNoStage", procedureQueryNoStage),
            new ValidationQuery("ProcedureNoStageNoMinor", procedureQueryNoStageNoMinor),
            new ValidationQuery("parameter", parameterQuery),
            new ValidationQuery("parameterNoNotes", parameterQueryNoNotes)
    });

    @Override
    protected void initialise(String[] args) throws ReportException {
        super.initialise(args);
        loadValidateCountsQuery = new LoadValidateCountsQuery(jdbcCdabasePrevious, jdbcCdabaseCurrent, csvWriter);
        loadValidateCountsQuery.addQueries(countQueries);
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ValidateExtractImpressReport.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    private final int ivNumErrorsToShow = 6;

    @Override
    public void run(String[] args) throws Exception {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("Parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        try {
            String db1Info    = sqlUtils.getDbInfoString(jdbcCdabasePrevious);
            String db2Info    = sqlUtils.getDbInfoString(jdbcCdabaseCurrent);

            logger.info("VALIDATION STARTED AGAINST DATABASES {} (Previous) AND {} (Current)", db1Info, db2Info);

        } catch (Exception e) { }

        loadValidateCountsQuery.execute();

        System.out.println(" ");

        for (ValidationQuery query : contentQueries) {

            List<String[]> diffs;

            try {
                diffs = sqlUtils.queryDiff(jdbcCdabasePrevious, jdbcCdabaseCurrent, query.query);
                int numErrorsToShow = Math.min(ivNumErrorsToShow, diffs.size());
                if (diffs.size() == 0) {
                    logger.info("SUCCESS:\t{}: {}", query.name, ImpressUtils.newlineToSpace(query.query));
                } else {
                    logger.warn("FAIL:\t{}: Displaying first {} diff rows of {}. query: {}", query.name, numErrorsToShow, diffs.size(), ImpressUtils.newlineToSpace(query.query));
                }
                for (int i = 0; i < numErrorsToShow; i++) {
                    String[]      diff = diffs.get(i);
                    StringBuilder sb   = new StringBuilder();
                    for (int j = 0; j < diff.length; j++) {
                        if (j > 0) {
                            sb.append("\t");
                        }
                        sb.append(diff[j]);
                    }

                    logger.warn("\t{}", sb);
                }
                System.out.println(" ");

            } catch (Exception e) {

                e.printStackTrace();
                logger.error("ERROR: {}", e.getLocalizedMessage());
            }
        }

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }

    public class ValidationQuery {

        public final String name;
        public final String query;

        public ValidationQuery(String name, String query) {
            this.name = name;
            this.query = query;
        }

    }
}