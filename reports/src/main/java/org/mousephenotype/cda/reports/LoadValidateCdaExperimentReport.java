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

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.reports.support.LoadValidateExperimentsQuery;
import org.mousephenotype.cda.reports.support.ReportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.beans.Introspector;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 ** This report produces a file of cda/komp2 database differences between the supplied experimentIds.
 *
 * Created by mrelac on 20/02/2017.
 */
@Component
public class LoadValidateCdaExperimentReport extends AbstractReport {

    private Logger                       logger   = LoggerFactory.getLogger(this.getClass());
    private LoadValidateExperimentsQuery loadValidateExperimentsQuery;
    private SqlUtils                     sqlUtils = new SqlUtils();

    private int         count            = 100;
    private Set<String> experimentIdList = new HashSet<>();
    private Set<String> ignoreList       = new HashSet<>();

    @Autowired
    @NotNull
    @Qualifier("jdbcCda1")
    private NamedParameterJdbcTemplate jdbc1;

    @Autowired
    @NotNull
    @Qualifier("jdbcCda2")
    private NamedParameterJdbcTemplate jdbc2;


    public static final String COUNT_ARG = "count";
    public static final String IGNORE_ARG = "ignore";
    public static final String EXPERIMENT_ARG = "experiment";

    /****************************
     * DATABASES: e.g. cda, komp2_base
     ****************************
    */

    @Override
    protected void initialise(String[] args) throws ReportException {
        String message;

        super.initialise(args);


        PropertySource ps = new SimpleCommandLinePropertySource(args);

        // Possible properties for this report: --count, --ignore
        OptionParser parser = new OptionParser();

        // parameter to indicate the count of experiment ids to generate and validate
        parser.accepts("count").withRequiredArg().ofType(Integer.class);

        // parameter to indicate the column alias(es) to ignore
        parser.accepts("ignore").withRequiredArg().ofType(String.class);

        // parameter to indicate any experimentIds to test
        parser.accepts("experiment").withRequiredArg().ofType(String.class);

        parser.allowsUnrecognizedOptions();         // Ignore options already processed at a higher level.

        OptionSet options = parser.parse(args);

        if (options.hasArgument("count")) {
            Integer count = commonUtils.tryParseInt(options.valueOf("count"));
            if (count == null) {
                message = "Invalid count {}";
                logger.error(message);
                throw new ReportException(message);
            }
        }

        if (options.hasArgument("ignore")) {
            ignoreList.addAll((List<String>) options.valuesOf("ignore"));
        }

        if (options.hasArgument("experiment")) {
            experimentIdList.addAll((List<String>) options.valuesOf("experiment"));
        }

        loadValidateExperimentsQuery = new LoadValidateExperimentsQuery(jdbc1, jdbc2, csvWriter, experimentIdList, count, ignoreList);
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("LoadValidateCdaExperimentReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        try {
            String db1Name = sqlUtils.getDatabaseName(jdbc1);
            String db2Name = sqlUtils.getDatabaseName(jdbc2);
            logger.info("VALIDATION STARTED AGAINST DATABASES {} AND {}", db1Name, db2Name);

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