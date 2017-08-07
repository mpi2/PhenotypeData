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
 ** This report produces a file of cda_base database differences between a previous version and the current one.
 *
 * Created by mrelac on 24/07/2015.
 */
@ComponentScan("org.mousephenotype.cda.loads.reports")
public class ExtractValidateCdabaseReport extends AbstractReport implements CommandLineRunner {

    private Logger   logger   = LoggerFactory.getLogger(this.getClass());
    private LoadValidateCountsQuery loadValidateCountsQuery;

    private NamedParameterJdbcTemplate jdbcCdabasePrevious;
    private NamedParameterJdbcTemplate               jdbcCdabaseCurrent;
    private SqlUtils                   sqlUtils;

    @Inject
    public ExtractValidateCdabaseReport(NamedParameterJdbcTemplate jdbcCdabasePrevious, NamedParameterJdbcTemplate jdbcCdabaseCurrent, SqlUtils sqlUtils) {
        this.jdbcCdabasePrevious = jdbcCdabasePrevious;
        this.jdbcCdabaseCurrent = jdbcCdabaseCurrent;
        this.sqlUtils = sqlUtils;
    }


    /********************
     * DATABASE: cda_base
     ********************
    */
    private final double      DELTA   = 0.8;
    private LoadsQueryDelta[] queries = new LoadsQueryDelta[] {
            new LoadsQueryDelta("allele COUNTS", DELTA, "SELECT count(*) FROM allele"),
            new LoadsQueryDelta("biological_model COUNTS", DELTA, "SELECT count(*) FROM biological_model"),
            new LoadsQueryDelta("biological_model_allele COUNTS", DELTA, "SELECT count(*) FROM biological_model_allele"),
            new LoadsQueryDelta("biological_model_genomic_feature COUNTS", DELTA, "SELECT count(*) FROM biological_model_genomic_feature"),
            new LoadsQueryDelta("biological_model_phenotype COUNTS", DELTA, "SELECT count(*) FROM biological_model_phenotype"),
            new LoadsQueryDelta("external_db COUNTS", DELTA, "SELECT count(*) FROM external_db"),
            new LoadsQueryDelta("genomic_feature COUNTS", DELTA, "SELECT count(*) FROM genomic_feature"),
            new LoadsQueryDelta("ontology_term COUNTS", DELTA, "SELECT count(*) FROM ontology_term"),
            new LoadsQueryDelta("organisation COUNTS", DELTA, "SELECT count(*) FROM organisation"),
            new LoadsQueryDelta("project COUNTS", DELTA, "SELECT count(*) FROM project"),
            new LoadsQueryDelta("strain COUNTS", DELTA, "SELECT count(*) FROM strain")
    };

    @Override
    protected void initialise(String[] args) throws ReportException {
        super.initialise(args);
        loadValidateCountsQuery = new LoadValidateCountsQuery(jdbcCdabasePrevious, jdbcCdabaseCurrent, csvWriter);
        loadValidateCountsQuery.addQueries(queries);
    }

    @Override
    public String getDefaultFilename() {
        return Introspector.decapitalize(ClassUtils.getShortClassName(this.getClass()));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(ExtractValidateCdabaseReport.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setLogStartupInfo(false);
        app.setWebEnvironment(false);
        app.run(args);
    }

    public void run(String[] args) throws ReportException {

        List<String> errors = parser.validate(parser.parse(args));
        if ( ! errors.isEmpty()) {
            logger.error("ExtractValidateDccReport parser validation error: " + StringUtils.join(errors, "\n"));
            return;
        }
        initialise(args);

        long start = System.currentTimeMillis();

        try {
            String db1Info    = sqlUtils.getDbInfoString(jdbcCdabasePrevious);
            String db2Info    = sqlUtils.getDbInfoString(jdbcCdabaseCurrent);

            logger.info("VALIDATION STARTED AGAINST DATABASES {} AND {}", db1Info, db2Info);

        } catch (Exception e) { }

        loadValidateCountsQuery.execute();

        try {
            csvWriter.close();
        } catch (IOException e) {
            throw new ReportException("Exception closing csvWriter: " + e.getLocalizedMessage());
        }

        log.info(String.format("Finished. [%s]", commonUtils.msToHms(System.currentTimeMillis() - start)));
    }
}