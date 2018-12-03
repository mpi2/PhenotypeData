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

    private LoadValidateCountsQuery    loadValidateCountsQuery;
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



    private final String excludedProcedures =
            " WHERE pr.stable_id NOT IN (\n" +
                    "'bcmla_abr_001',\n" +
                    "'bcmla_ecg_001',\n" +
                    "'hmgula_ins_001',\n" +
                    "'hrwlip_csd_001',\n" +
                    "'hrwlla_csd_001',\n" +
                    "'icsla_abr_001',\n" +
                    "'icsla_ecg_001',\n" +
                    "'tcpip_csd_001',\n" +
                    "'tcpla_csd_001',\n" +
                    "'tcpla_ecg_001',\n" +
                    "'tcpla_eye_001',\n" +
                    "'ucdip_csd_001',\n" +
                    "'ucdla_csd_001',\n" +
                    "'ucdla_ecg_001',\n" +
                    "'ucdla_eye_001'\n" +
                    ")";

    private final String excludedParameters =
            " AND (pa.stable_id NOT IN ('bcmla_eye_093_001'))";

    private final String pipeline =
        "SELECT pi.stable_id, edb.Name AS datasource_name,pi.name\n" +
        "FROM phenotype_pipeline pi\n" +
        "JOIN external_db edb ON edb.id = pi.db_id";

    private final String procedure =
        "SELECT pi.stable_id, pr.stable_id, edb.Name AS datasource_name\n" +
                "FROM phenotype_procedure pr\n" +
                "JOIN external_db edb ON edb.id = pr.db_id\n" +
                "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id\n" +
                "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String procedureLevel =
            "SELECT pi.stable_id, pr.stable_id, edb.Name AS datasource_name, pr.level\n" +
                    "FROM phenotype_procedure pr\n" +
                    "JOIN external_db edb ON edb.id = pr.db_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String procedureStage =
            "SELECT pi.stable_id, pr.stable_id, edb.Name AS datasource_name, pr.stage\n" +
                    "FROM phenotype_procedure pr\n" +
                    "JOIN external_db edb ON edb.id = pr.db_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String procedureStageLabel =
            "SELECT pi.stable_id, pr.stable_id, edb.Name AS datasource_name, pr.stage_label\n" +
                    "FROM phenotype_procedure pr\n" +
                    "JOIN external_db edb ON edb.id = pr.db_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameter =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameterUnit =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name, pa.unit\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameterDatatype =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name, pa.datatype\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameterType =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name, pa.parameter_type\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameterFormula =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name, pa.formula\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameterRequired =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name, pa.required\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameterMetadata =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name, pa.metadata\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameterDerived =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name, pa.derived\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id";

    private final String parameterOptions =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name,\n" +
                    "  pao.name, pao.normal\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id\n" +
                    "JOIN phenotype_parameter_lnk_option palo ON palo.parameter_id = pa.id\n" +
                    "JOIN phenotype_parameter_option pao ON pao.id = palo.option_id";

    private final String parameterIncrements =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name,\n" +
                    "  pai.increment_value, pai.increment_datatype, pai.increment_unit,pai.increment_minimum\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id\n" +
                    "JOIN phenotype_parameter_lnk_increment pali ON pali.parameter_id = pa.id\n" +
                    "JOIN phenotype_parameter_increment pai ON pai.id = pali.increment_id";

    private final String parameterAnnotations =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name,\n" +
                    "  paoa.event_type, paoa.ontology_acc,paoa.ontology_db_id, paoa.sex\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id\n" +
                    "JOIN phenotype_parameter_lnk_ontology_annotation paola ON paola.parameter_id = pa.id\n" +
                    "JOIN phenotype_parameter_ontology_annotation paoa ON paoa.id = paola.annotation_id";

    private final String parameterAnnotationsOptions =
            "SELECT pi.stable_id, pr.stable_id, pa.stable_id, edb.Name AS datasource_name,\n" +
                    "  pao.name AS optionName, pao.normal AS optionNormal\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edb ON edb.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id\n" +
                    "JOIN phenotype_parameter_lnk_ontology_annotation paola ON paola.parameter_id = pa.id\n" +
                    "JOIN phenotype_parameter_ontology_annotation paoa ON paoa.id = paola.annotation_id\n" +
                    "JOIN phenotype_parameter_option pao ON pao.id = paoa.option_id";

    private final String parameterAnnotationsOntologyTerms =
            "SELECT DISTINCT pi.stable_id, pr.stable_id, pa.stable_id, edbIm.Name AS impressDatasourceName,\n" +
                    "  ot.acc, edbOn.name AS ontologyDatasourceName\n" +
                    "FROM phenotype_parameter pa\n" +
                    "JOIN external_db edbIm ON edbIm.id = pa.db_id\n" +
                    "JOIN phenotype_procedure_parameter prpa ON prpa.parameter_id = pa.id\n" +
                    "JOIN phenotype_procedure pr ON pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline_procedure pipr ON pipr.procedure_id = pr.id and pr.id = prpa.procedure_id\n" +
                    "JOIN phenotype_pipeline pi ON pi.id = pipr.pipeline_id\n" +
                    "JOIN phenotype_parameter_lnk_ontology_annotation paola ON paola.parameter_id = pa.id\n" +
                    "JOIN phenotype_parameter_ontology_annotation paoa ON paoa.id = paola.annotation_id\n" +
                    "JOIN ontology_term ot ON ot.acc = paoa.ontology_acc AND ot.db_id = paoa.ontology_db_id\n" +
                    "JOIN external_db edbOn ON edbOn.id = paoa.ontology_db_id";

    private List<ValidationQuery> contentQueries = Arrays.asList(new ValidationQuery[] {

//            new ValidationQuery("procedureStageLabel", procedureStageLabel),                      // Omit this test. The komp2 values for stage label don't match impress v1!
//            new ValidationQuery("parameterDatatype", parameterDatatype),                          // Omit this test. Spot-checking revealed V1 has blank datatype, V2 has non-blank datatype.
//            new ValidationQuery("parameterFormula", parameterFormula),                            // Omit this test, as the new impress V2 formulas are similar to, but slightly different from the impress V1 formulas.

            new ValidationQuery("pipeline", pipeline),
            new ValidationQuery("procedure", procedure),
            new ValidationQuery("procedureLevel", procedureLevel),
            new ValidationQuery("procedureStage", procedureStage),
            new ValidationQuery("parameter", parameter),
            new ValidationQuery("parameterUnit", parameterUnit),
            new ValidationQuery("parameterType", parameterType),
            new ValidationQuery("parameterRequired", parameterRequired),
            new ValidationQuery("parameterMetadata", parameterMetadata),
            new ValidationQuery("parameterDerived", parameterDerived),
            new ValidationQuery("parameterOptions", parameterOptions),
            new ValidationQuery("parameterIncrements", parameterIncrements),
            new ValidationQuery("parameterAnnotations", parameterAnnotations),
            new ValidationQuery("parameterAnnotationsOptions", parameterAnnotationsOptions),
            new ValidationQuery("parameterAnnotationsOntologyTerms", parameterAnnotationsOntologyTerms),
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

    private final int ivNumErrorsToShow = 200;

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

        if (parser.checkContent()) {

            System.out.println(" ");

            for (ValidationQuery query : contentQueries) {

                List<String[]> diffs;

                try {
                    String q = query.query;
                    if ((query.name.startsWith("procedure")) || (query.name.startsWith("parameter"))) {
                        q += excludedProcedures;
                    }
                    if ((query.name.startsWith("parameter"))) {
                        q += excludedParameters;
                    }
                    diffs = sqlUtils.queryDiff(jdbcCdabasePrevious, jdbcCdabaseCurrent, q, useLenient);
                    int numErrorsToShow = Math.min(ivNumErrorsToShow, diffs.size());
                    if (diffs.size() == 0) {
                        logger.info("SUCCESS:\t{}: {}", query.name, ImpressUtils.newlineToSpace(query.query));
                    } else {
                        // Subtract 1 from numErrorsToShow and diffs.size() to account for heading row.
                        logger.warn("FAIL:\t{}: Displaying first {} diff rows of {}. query: {}", query.name, numErrorsToShow - 1, diffs.size() - 1, ImpressUtils.newlineToSpace(query.query));
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