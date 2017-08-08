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
import org.mousephenotype.cda.loads.reports.support.LoadValidateExperimentsQuery;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 ** This report produces a file of cda/komp2 database differences between the supplied experimentIds.
 *
 * Created by mrelac on 20/02/2017.
 */
@ComponentScan("org.mousephenotype.cda.loads.reports")
public class ValidateLoadCdaExperimentReport extends AbstractReport implements CommandLineRunner {


    private Logger                       logger   = LoggerFactory.getLogger(this.getClass());
    private LoadValidateExperimentsQuery loadValidateExperimentsQuery;

    private int          count            = 100;
    private boolean      includeDerived   = false;
    private List<String> experimentIdList = new ArrayList<>();
    private Set<String>  skipColumns      = new HashSet<>();
    private Set<String>  skipParameters   = new HashSet<>();

    private NamedParameterJdbcTemplate jdbcCdaPrevious;
    private NamedParameterJdbcTemplate jdbcCdaCurrent;
    private SqlUtils                   sqlUtils;


    public static final String COUNT_ARG           = "count";
    public static final String SKIP_COLUMN_ARG     = "skipcolumn";
    public static final String SKIP_PARAMETER_ARG  = "skipparameter";
    public static final String EXPERIMENT_ARG      = "experiment";
    public static final String INCLUDE_DERIVED_ARG = "includederived";

    @Inject
    public ValidateLoadCdaExperimentReport(NamedParameterJdbcTemplate jdbcCdaPrevious, NamedParameterJdbcTemplate jdbcCdaCurrent, SqlUtils sqlUtils) {
        this.jdbcCdaPrevious = jdbcCdaPrevious;
        this.jdbcCdaCurrent = jdbcCdaCurrent;
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

        // Possible properties for this report: --count, --skipcolumn, --skipparameter
        OptionParser parser = new OptionParser();

        // parameter to indicate the count of experiment ids to generate and validate
        parser.accepts(COUNT_ARG).withRequiredArg().ofType(Integer.class);

        // parameter to indicate the column alias(es) to ignore
        parser.accepts(SKIP_COLUMN_ARG).withRequiredArg().ofType(String.class);

        // parameter to indicate the parameter stable id(s) to ignore
        parser.accepts(SKIP_PARAMETER_ARG).withRequiredArg().ofType(String.class);

        // parameter to indicate any experimentIds to test
        parser.accepts(EXPERIMENT_ARG).withRequiredArg().ofType(String.class);

        // parameter to indicate inclusion of derived experiments
        parser.accepts(INCLUDE_DERIVED_ARG);

        parser.allowsUnrecognizedOptions();         // Ignore options already processed at a higher level.

        OptionSet options = parser.parse(args);

        if (options.hasArgument(COUNT_ARG)) {
            Integer count = commonUtils.tryParseInt(options.valueOf(COUNT_ARG));
            if (count == null) {
                message = "Invalid count {}";
                logger.error(message);
                throw new ReportException(message);
            }

            this.count = count;
        }

        if (options.hasArgument(SKIP_COLUMN_ARG)) {
            skipColumns.addAll((List<String>) options.valuesOf(SKIP_COLUMN_ARG));
        }

        if (options.hasArgument(SKIP_PARAMETER_ARG)) {
            skipParameters.addAll((List<String>) options.valuesOf(SKIP_PARAMETER_ARG));
        }

        if (options.hasArgument(EXPERIMENT_ARG)) {
            experimentIdList.addAll((List<String>) options.valuesOf(EXPERIMENT_ARG));
        }

        if (options.has(INCLUDE_DERIVED_ARG)) {
            includeDerived = true;
        }

        if ( ! skipColumns.isEmpty())
            logger.info("skipColumns: [{}]", StringUtils.join(skipColumns, ", "));

        if ( ! skipParameters.isEmpty())
            logger.info("skipParameters: [{}]", StringUtils.join(skipParameters, ", "));

        logger.info("{} derived experiments", includeDerived ? "Including " : "Omitting ");

        loadValidateExperimentsQuery = new LoadValidateExperimentsQuery(jdbcCdaPrevious, jdbcCdaCurrent, csvWriter, experimentIdList, count, skipColumns, skipParameters, includeDerived);
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ValidateLoadCdaExperimentReport.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    @Override
    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ValidateLoadCdaExperimentReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        try {
            String db1Info    = sqlUtils.getDbInfoString(jdbcCdaPrevious);
            String db2Info    = sqlUtils.getDbInfoString(jdbcCdaCurrent);

            logger.info("VALIDATION STARTED AGAINST DATABASES {} AND {}", db1Info, db2Info);

        } catch (Exception e) { }

        loadValidateExperimentsQuery.execute();

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}