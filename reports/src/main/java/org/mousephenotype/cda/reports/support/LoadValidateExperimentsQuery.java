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

package org.mousephenotype.cda.reports.support;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.reports.AbstractReport;
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 ** This class validates the experiments in database one against the experiments in database two using the queries and
 * code in this class.
 *
 * Created by mrelac on 01/02/2017.
 */
public class LoadValidateExperimentsQuery {

    private Logger   logger   = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils();

    private List<LoadsQuery> loadsQueryList = new ArrayList<>();
    private JdbcTemplate     jdbcPrevious;
    private JdbcTemplate     jdbcCurrent;
    private MpCSVWriter      csvWriter;

    public LoadValidateExperimentsQuery(JdbcTemplate jdbcPrevious, JdbcTemplate jdbcCurrent, MpCSVWriter  csvWriter) {
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
                        csvWriter.writeNext(AbstractReport.EMPTY_ROW);
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

    private final String queryDetail = 
    "-- series parameter with metadata\n" +
            "SELECT\n" +
            "  edb.name                                 AS e_short_name,\n" +
            "  e.external_id                            AS e_external_id,\n" +
            "  e.sequence_id                            AS e_sequence_id,\n" +
            "  e.date_of_experiment,\n" +
            "  eorg.name                                AS e_organisation,\n" +
            "  pr.name                                  AS project_id,\n" +
            "  pi.name                                  AS pipeline_id,\n" +
            "  e.pipeline_stable_id,\n" +
            "  proc.name                                AS procedure_id,\n" +
            "  e.biological_model_id,\n" +
            "  e.colony_id,\n" +
            "  e.metadata_combined,\n" +
            "  e.metadata_group,\n" +
            "  e.procedure_status                       AS e_procedure_status,\n" +
            "  e.procedure_status_message               AS e_procedure_status_message,\n" +

            "  'OBSERVATION',\n" +
            "  obdb.name                                AS ob_short_name,\n" +
            "  ob.parameter_stable_id,\n" +
            "  ob.sequence_id                           AS ob_sequence_id,\n" +
            "  ob.population_id,\n" +
            "  ob.observation_type,\n" +
            "  ob.missing,\n" +
            "  ob.parameter_status                      AS obParameterStatus,\n" +
            "  ob.parameter_status_message              AS obParameterStatusMessage,\n" +

            "  'BIOLOGICAL_SAMPLE',\n" +
            "  bs.external_id                           AS bs_external_id,\n" +
            "  bsdb.name                                AS bs_short_name,\n" +
            "  bs.sample_type_acc                       AS bs_sample_type_acc,\n" +
            "  bsstdb.name                              AS bs_sample_type_short_name,\n" +
            "  bs.sample_group,\n" +
            "  bsorg.name                               AS bs_organisation,\n" +
            "  bspc.name                                AS bs_production_center,\n" +

            "  'CATEGORICAL',\n" +
            "  cob.category                             AS cob_category,\n" +
            "  'DATETIME',\n" +

            "  dob.datetime_point                       AS dob_datetime_point,\n" +

            "  'IMAGE_RECORD',\n" +
            "  irob.full_resolution_file_path           AS irob_full_resolution_file_path,\n" +
            "  irob.download_file_path                  AS irob_download_file_path,\n" +
            "  irob.image_link                          AS irob_image_link,\n" +
            "  irorg.name                               AS irob_organisation,\n" +
            "  irob.increment_value                     AS irob_increment_value,\n" +
            "  irob.file_type                           AS irob_file_type,  \n" +

            "  'ONTOLOGY',\n" +
            "  oob.parameter_id                         AS oob_parameter_id,\n" +
            "  oob.sequence_id                          AS oob_sequence_id,\n" +

            "  'TEXT',\n" +
            "  tob.text                                 AS tob_text,\n" +
            "  'TIME_SERIES',\n" +

            "  tsob.data_point                          AS tsob_data_point,\n" +
            "  tsob.time_point                          AS tsob_time_point,\n" +
            "  tsob.discrete_point                      AS tsob_discrete_point,\n" +

            "  'UNIDIMENSIONAL',\n" +
            "  uob.data_point                           AS uob_data_point,\n" +
            "  'OBSERVATION PROCEDURE_META_DATA',\n" +
            "    (    SELECT GROUP_CONCAT(DISTINCT CONCAT(pp.name, ' = ', pmdin.value) ORDER BY pmdin.parameter_id ASC SEPARATOR '::')\n" +
            "        FROM procedure_meta_data pmdin\n" +
            "        JOIN phenotype_parameter pp ON pp.stable_id = pmdin.parameter_id\n" +
            "        WHERE e.id=pmdin.experiment_id\n" +
            "          AND pmdin.observation_id    = ob.id\n" +
            "          AND pp.name NOT LIKE '%Experimenter%')\n" +
            "                                           AS ob_metadata_combined,\n" +
            "    (    SELECT IF\n" +
            "            (    ob_metadata_combined IS NULL, NULL, \n" +
            "                MD5(\n" +
            "                    GROUP_CONCAT(DISTINCT CONCAT(pp.name, ' = ', pmdin.value) ORDER BY pmdin.parameter_id ASC SEPARATOR '::')\n" +
            "                   )\n" +
            "            )\n" +
            "        FROM procedure_meta_data pmdin\n" +
            "        JOIN phenotype_parameter pp ON pp.stable_id = pmdin.parameter_id\n" +
            "        WHERE pp.data_analysis = 1\n" +
            "          AND pmdin.observation_id    = ob.id\n" +
            "          AND pmdin.experiment_id = e.id)\n" +
            "                                           AS ob_metadata_group\n" +
            "FROM             experiment                    e\n" +
            "JOIN             external_db                   edb     ON edb        .id             = e.db_id\n" +
            "JOIN             organisation                  eorg    ON eorg        .id             = e.organisation_id\n" +
            "JOIN             project                       pr         ON pr        .id             = e.project_id\n" +
            "JOIN             phenotype_pipeline            pi         ON pi        .id             = e.pipeline_id\n" +
            "JOIN             phenotype_procedure           proc     ON proc     .id             = e.procedure_id\n" +
            "JOIN             experiment_observation        eob       ON eob       .experiment_id     = e.id\n" +

            "JOIN             observation                   ob      ON ob       .id             = eob.observation_id\n" +
            "JOIN             external_db                   obdb    ON obdb     .id             = ob.db_id\n" +

            "JOIN             biological_sample             bs      ON bs       .id             = ob.biological_sample_id\n" +
            "JOIN             external_db                   bsdb    ON bsdb     .id             = bs.db_id\n" +
            "JOIN             external_db                   bsstdb  ON bsstdb    .id             = bs.sample_type_db_id\n" +
            "JOIN             organisation                  bsorg      ON bsorg       .id             = bs.organisation_id\n" +
            "JOIN             organisation                  bspc    ON bspc      .id                = bs.production_center_id\n" +

            "LEFT OUTER JOIN categorical_observation        cob     ON cob        .id             = ob.id\n" +
            "LEFT OUTER JOIN datetime_observation           dob     ON dob        .id             = ob.id\n" +
            "LEFT OUTER JOIN image_record_observation       irob     ON irob        .id             = ob.id\n" +
            "LEFT OUTER JOIN organisation                   irorg      ON irorg       .id             = irob.organisation_id\n" +
            "LEFT OUTER JOIN ontology_observation           oob      ON oob        .id             = ob.id\n" +
            "LEFT OUTER JOIN text_observation               tob     ON tob        .id             = ob.id\n" +
            "LEFT OUTER JOIN time_series_observation        tsob    ON tsob        .id                = ob.id\n" +
            "LEFT OUTER JOIN unidimensional_observation     uob     ON uob        .id             = ob.id\n" +
            "LEFT OUTER JOIN procedure_meta_data            pmdob    ON pmdob    .experiment_id  = e.id AND pmdob.observation_id    = ob.id\n" +
            "WHERE e.external_id = :external_id";
}