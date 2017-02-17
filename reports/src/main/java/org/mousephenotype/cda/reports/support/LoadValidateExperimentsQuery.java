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
import org.mousephenotype.cda.utilities.RunStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * * This class validates the specified experimentIds against two databases.
 * <p>
 * Created by mrelac on 01/02/2017.
 */
public class LoadValidateExperimentsQuery {

    private Logger   logger   = LoggerFactory.getLogger(this.getClass());
    private SqlUtils sqlUtils = new SqlUtils();

    private NamedParameterJdbcTemplate jdbc1;
    private NamedParameterJdbcTemplate jdbc2;
    private List<String>               experimentIds;
    private MpCSVWriter                csvWriter;


    /**
     * This class implements an experiment validation that validates that every field in the list of {@code experimentId}s
     * matches in type and value in jdbc1 and jdbc2.
     * <p>
     * Any mismatches are written to the cdaWriter, jdbc1 line detail followed by jdbc2 line detail (on the same row
     * of the spreadsheet). The experimentIds of any experiments that have no differences are written to the cdaWriter
     * at the bottom of the spreadsheet. In summary, the cdaWriter contains the following after invocation:
     * <ul>
     * <li>{@code headings} the column headings</li>
     * <li>{@code data} element 0: a {@link List&lt;String&gt;} of the data cells from db1</li>
     * <li>{@code data} element 1: a {@link List&lt;String&gt;} of the data cells from db2</li>
     * <li>A 0-relative list of the columns that differ</li>
     * </ul>
     *
     * @param jdbc1         the first {@link NamedParameterJdbcTemplate} instance
     * @param jdbc2         the second {@link NamedParameterJdbcTemplate} instance
     * @param csvWriter     the spreadsheet instance
     * @param experimentIds a List&lt;String&gt; containing the experimentIds to be validated
     * @throws Exception
     */
    public LoadValidateExperimentsQuery(NamedParameterJdbcTemplate jdbc1, NamedParameterJdbcTemplate jdbc2, MpCSVWriter csvWriter, List<String> experimentIds) {
        this.jdbc1 = jdbc1;
        this.jdbc2 = jdbc2;
        this.experimentIds = experimentIds;
        this.csvWriter = csvWriter;
    }

    /**
     * Execute the query for each experimentId. For each experiment row that is not an exact match in both databases,
     * write each row on a single line, highlighting each cell difference using color. If an experiment row is an exact
     * match, save the center and experimentId for subsequent output to the worksheet, after all mismatches, to capture
     * the center/experimentIds that were compared and were equal.
     *
     * @return a {@link RunStatus} with a list of errors
     * @throws ReportException
     */
    public RunStatus execute2() throws ReportException {

        RunStatus status = new RunStatus();

        try {

            status = queryDetail();

        } catch (Exception e) {

            throw new ReportException(e);
        }

        return status;
    }
    /**
     * Execute the queries. If there are any results in the query run against the previous database that do not exist
     * in the current database, an error is written to the returned {@link RunStatus}.
     * @return  a {@link RunStatus} with a list of errors
     * @throws ReportException
     */
    public RunStatus execute() throws ReportException {

        RunStatus status = new RunStatus();

//        for (LoadsQuery loadsQuery : loadsQueryList) {
//
//            try {
//
//                logger.info("Query {}:\n{}", loadsQuery.getName(), loadsQuery.getQuery());
//                List<String[]> missing = sqlUtils.queryDiff(jdbcPrevious, jdbcCurrent, loadsQuery.getQuery());
//                if ( ! missing.isEmpty()) {
//
//                    logger.warn("{} ROWS MISSING", missing.size());
//                    String[] summary = new String[]{Integer.toString(missing.size()) + " " + loadsQuery.getName() + ":"};
//                    if (status.hasErrors())
//                        csvWriter.writeNext(AbstractReport.EMPTY_ROW);
//                    csvWriter.writeNext(summary);
//                    csvWriter.writeAll(missing);
//                    status.addError("missing rows");
//                } else {
//                    System.out.println("SUCCESS");
//                }
//
//                System.out.println();
//
//
//            } catch (Exception e) {
//
//                throw new ReportException(e);
//            }
//        }

        return status;
    }

    private final String query =
            "-- cdaExperimentWithDetail\n" +
                    "\n" +
                    "-- explain\n" +
                    "SELECT\n" +
                    "  edb.name                              AS e_short_name,\n" +
                    "  e.external_id                         AS e_external_id,\n" +
                    "  e.sequence_id                         AS e_sequence_id,\n" +
                    "  e.date_of_experiment                  AS e_date_of_experiment,            -- timestamp\n" +
                    "  eorg.name                             AS e_phenotypingCenter,\n" +
                    "  pr.name                               AS e_project,\n" +
                    "  e.pipeline_stable_id                  AS e_pipeline_stable_id,\n" +
                    "  e.procedure_stable_id                 AS e_procedure_stable_id,\n" +
                    "  e.colony_id                           AS e_colony_id,\n" +
                    "  e.metadata_combined                   AS e_metadata_combined,\n" +
                    "  e.metadata_group                      AS e_metadata_group,\n" +
                    "  e.procedure_status                    AS e_procedure_status,\n" +
                    "  e.procedure_status_message            AS e_procedure_status_message,\n" +
                    "  \n" +
                    "  'OBSERVATION',\n" +
                    "  obdb.name                             AS ob_short_name,\n" +
                    "  ob.parameter_stable_id                AS ob_parameter_stable_id,\n" +
                    "  ob.sequence_id                        AS ob_sequence_id,\n" +
                    "  ob.population_id                      AS ob_population_id,                -- int\n" +
                    "  ob.observation_type                   AS ob_observation_type,\n" +
                    "  ob.missing                            AS ob_missing,                      -- int\n" +
                    "  ob.parameter_status                   AS ob_parameter_status,\n" +
                    "  ob.parameter_status_message           AS ob_parameter_status_message,\n" +
                    "  \n" +
                    "  'BIOLOGICAL_MODEL',\n" +
                    "  -- bmdb.name                             AS bm_short_name,\n" +
                    "  bm.allelic_composition                AS bm_allelic_composition,\n" +
                    "  bm.genetic_background                 AS bm_genetic_background,\n" +
                    "  \n" +
                    "  'BIOLOGICAL_SAMPLE',\n" +
                    "  bs.external_id                        AS bs_external_id,\n" +
                    "  bsdb.name                             AS bs_short_name,\n" +
                    "  bs.sample_type_acc                    AS bs_sample_type_acc,\n" +
                    "  bsstdb.name                           AS bs_sample_type_short_name,\n" +
                    "  bs.sample_group                       AS bs_sample_group,\n" +
                    "  bsorg.name                            AS bs_organisation,\n" +
                    "  bspc.name                             AS bs_production_center,\n" +
                    "  \n" +
                    "  'CATEGORICAL',\n" +
                    "  cob.category                          AS cob_category,\n" +
                    "  \n" +
                    "  'DATETIME',\n" +
                    "  dob.datetime_point                    AS dob_datetime_point,                -- datetime\n" +
                    "  \n" +
                    "  'IMAGE_RECORD',\n" +
                    "  irob.full_resolution_file_path        AS irob_full_resolution_file_path,\n" +
                    "  irob.download_file_path               AS irob_download_file_path,\n" +
                    "  irob.image_link                       AS irob_image_link,\n" +
                    "  irorg.name                            AS irob_organisation,\n" +
                    "  irob.increment_value                  AS irob_increment_value,\n" +
                    "  irob.file_type                        AS irob_file_type,  \n" +
                    "  \n" +
                    "  'ONTOLOGY',\n" +
                    "  oob.parameter_id                      AS oob_parameter_id,\n" +
                    "  oob.sequence_id                       AS oob_sequence_id,                   -- int\n" +
                    "  \n" +
                    "  'TEXT',\n" +
                    "  tob.text                              AS tob_text,\n" +
                    "  \n" +
                    "  'TIME_SERIES',\n" +
                    "  tsob.data_point                       AS tsob_data_point,                   -- float\n" +
                    "  tsob.time_point                       AS tsob_time_point,                   -- timestamp\n" +
                    "  tsob.discrete_point                   AS tsob_discrete_point,               -- float\n" +
                    "  \n" +
                    "  'UNIDIMENSIONAL',\n" +
                    "  uob.data_point                        AS uob_data_point,                    -- float\n" +
                    "\n" +
                    "  'OBSERVATION PROCEDURE_META_DATA',\n" +
                    "    (    SELECT GROUP_CONCAT(DISTINCT CONCAT(pp.name, ' = ', pmdin.value) ORDER BY pmdin.parameter_id ASC SEPARATOR '::')\n" +
                    "        FROM procedure_meta_data pmdin\n" +
                    "        JOIN phenotype_parameter pp ON pp.stable_id = pmdin.parameter_id\n" +
                    "        WHERE e.id=pmdin.experiment_id\n" +
                    "          AND pmdin.observation_id    = ob.id\n" +
                    "          AND pp.name NOT LIKE '%Experimenter%')\n" +
                    "                                        AS ob_metadata_combined,\n" +
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
                    "                                        AS ob_metadata_group,\n" +
                    "    'PARAMETER_ASSOCIATION',\n" +
                    "    pa.parameter_id                     AS pa_parameter_id,\n" +
                    "    pa.sequence_id                      AS pa_sequence_id,\n" +
                    "    pa.dim_id                           AS pa_dim_id,\n" +
                    "    pa.parameter_association_value      AS pa_parameter_association_value\n" +
                    "\n" +
                    "FROM             experiment                     e\n" +
                    "JOIN             external_db                    edb     ON edb      .id                 = e     .db_id\n" +
                    "JOIN             organisation                   eorg    ON eorg     .id                 = e     .organisation_id\n" +
                    "JOIN             project                        pr      ON pr       .id                 = e     .project_id\n" +
                    "JOIN             phenotype_pipeline             pi      ON pi       .id                 = e     .pipeline_id\n" +
                    "JOIN             experiment_observation         eob     ON eob      .experiment_id      = e     .id\n" +
                    "                    \n" +
                    "JOIN             observation                    ob      ON ob       .id                 = eob   .observation_id\n" +
                    "JOIN             external_db                    obdb    ON obdb     .id                 = ob    .db_id\n" +
                    "                \n" +
                    "JOIN             biological_sample              bs      ON bs       .id                 = ob    .biological_sample_id\n" +
                    "JOIN             external_db                    bsdb    ON bsdb     .id                 = bs    .db_id\n" +
                    "JOIN             external_db                    bsstdb  ON bsstdb   .id                 = bs    .sample_type_db_id\n" +
                    "JOIN             organisation                   bsorg   ON bsorg    .id                 = bs    .organisation_id\n" +
                    "JOIN             organisation                   bspc    ON bspc     .id                 = bs    .production_center_id\n" +
                    "\n" +
                    "LEFT OUTER JOIN biological_model                bm      ON bm       .id                 = e     .biological_model_id\n" +
                    "LEFT OUTER JOIN categorical_observation         cob     ON cob      .id                 = ob    .id\n" +
                    "LEFT OUTER JOIN datetime_observation            dob     ON dob      .id                 = ob    .id\n" +
                    "LEFT OUTER JOIN image_record_observation        irob    ON irob     .id                 = ob    .id\n" +
                    "LEFT OUTER JOIN organisation                    irorg   ON irorg    .id                 = irob  .organisation_id\n" +
                    "LEFT OUTER JOIN ontology_observation            oob     ON oob      .id                 = ob    .id\n" +
                    "LEFT OUTER JOIN text_observation                tob     ON tob      .id                 = ob    .id\n" +
                    "LEFT OUTER JOIN time_series_observation         tsob    ON tsob     .id                 = ob    .id\n" +
                    "LEFT OUTER JOIN unidimensional_observation      uob     ON uob      .id                 = ob    .id\n" +
                    "LEFT OUTER JOIN parameter_association           pa      ON pa       .observation_id     = ob    .id\n" +
                    "\n" +
                    "LEFT OUTER JOIN procedure_meta_data             pmdob   ON pmdob    .experiment_id      = e.id AND pmdob.observation_id = ob.id\n" +
                    "  \n" +
                    "WHERE e.external_id = :experimentId\n" +
                    "ORDER BY e_short_name,e_external_id,e_sequence_id,e_procedure_stable_id,e_procedure_status,e_procedure_status_message,ob_observation_type,\n" +
                    "ob_parameter_stable_id,ob_parameter_status,cob_category,dob_datetime_point,irob_increment_value,irob_full_resolution_file_path,oob_parameter_id,\n" +
                    "oob_sequence_id,tob.text,tsob_discrete_point,tsob_data_point,uob_data_point,ob_metadata_combined,ob_metadata_group,\n" +
                    "pa_parameter_id,pa_sequence_id,pa_parameter_association_value\n";


    public RunStatus queryDetail() {

        RunStatus           status       = new RunStatus();
        Map<String, Object> parameterMap = new HashMap<>();

        for (String experimentId : experimentIds) {

            parameterMap.put("experimentId", experimentId);
            List<ExperimentDetail> list1 = jdbc1.query(query, parameterMap, new ExperimentDetailRowMapper());
            List<ExperimentDetail> list2 = jdbc2.query(query, parameterMap, new ExperimentDetailRowMapper());
            for (int i = 0; i < Math.min(list1.size(), list2.size()); i++) {
                ExperimentDetail detail1 = list1.get(i);
                ExperimentDetail detail2 = list2.get(i);


            }

        }

        return status;
    }


    public class ExperimentDetail {
        private String e_short_name;
        private String e_external_id;
        private String e_sequence_id;
        private Date   e_date_of_experiment;
        private String e_phenotypingCenter;
        private String e_project;
        private String e_pipeline_stable_id;
        private String e_procedure_stable_id;
        private String e_colony_id;
        private String e_metadata_combined;
        private String e_metadata_group;
        private String e_procedure_status;
        private String e_procedure_status_message;

        private String  ob_short_name;
        private String  ob_parameter_stable_id;
        private String  ob_sequence_id;
        private Integer ob_population_id;
        private String  ob_observation_type;
        private Integer ob_missing;
        private String  ob_parameter_status;
        private String  ob_parameter_status_message;

        private String bm_allelic_composition;
        private String bm_genetic_background;

        private String bs_external_id;
        private String bs_short_name;
        private String bs_sample_type_acc;
        private String bs_sample_type_short_name;
        private String bs_sample_group;
        private String bs_organisation;
        private String bs_production_center;

        private String cob_category;

        private Date dob_datetime_point;

        private String irob_full_resolution_file_path;
        private String irob_download_file_path;
        private String irob_image_link;
        private String irob_organisation;
        private String irob_increment_value;
        private String irob_file_type;

        private String  oob_parameter_id;
        private Integer oob_sequence_id;

        private String tob_text;

        private Float uob_data_point;

        private Float tsob_data_point;
        private Date  tsob_time_point;
        private Float tsob_discrete_point;

        private String ob_metadata_combined;
        private String ob_metadata_group;

        private String  pa_parameter_id;
        private Integer pa_sequence_id;
        private String  pa_dim_id;
        private String  pa_parameter_association_value;

        public ExperimentDetail() {
        }

        public String getE_short_name() {
            return e_short_name;
        }

        public void setE_short_name(String e_short_name) {
            this.e_short_name = e_short_name;
        }

        public String getE_external_id() {
            return e_external_id;
        }

        public void setE_external_id(String e_external_id) {
            this.e_external_id = e_external_id;
        }

        public String getE_sequence_id() {
            return e_sequence_id;
        }

        public void setE_sequence_id(String e_sequence_id) {
            this.e_sequence_id = e_sequence_id;
        }

        public Date getE_date_of_experiment() {
            return e_date_of_experiment;
        }

        public void setE_date_of_experiment(Date e_date_of_experiment) {
            this.e_date_of_experiment = e_date_of_experiment;
        }

        public String getE_phenotypingCenter() {
            return e_phenotypingCenter;
        }

        public void setE_phenotypingCenter(String e_phenotypingCenter) {
            this.e_phenotypingCenter = e_phenotypingCenter;
        }

        public String getE_project() {
            return e_project;
        }

        public void setE_project(String e_project) {
            this.e_project = e_project;
        }

        public String getE_pipeline_stable_id() {
            return e_pipeline_stable_id;
        }

        public void setE_pipeline_stable_id(String e_pipeline_stable_id) {
            this.e_pipeline_stable_id = e_pipeline_stable_id;
        }

        public String getE_procedure_stable_id() {
            return e_procedure_stable_id;
        }

        public void setE_procedure_stable_id(String e_procedure_stable_id) {
            this.e_procedure_stable_id = e_procedure_stable_id;
        }

        public String getE_colony_id() {
            return e_colony_id;
        }

        public void setE_colony_id(String e_colony_id) {
            this.e_colony_id = e_colony_id;
        }

        public String getE_metadata_combined() {
            return e_metadata_combined;
        }

        public void setE_metadata_combined(String e_metadata_combined) {
            this.e_metadata_combined = e_metadata_combined;
        }

        public String getE_metadata_group() {
            return e_metadata_group;
        }

        public void setE_metadata_group(String e_metadata_group) {
            this.e_metadata_group = e_metadata_group;
        }

        public String getE_procedure_status() {
            return e_procedure_status;
        }

        public void setE_procedure_status(String e_procedure_status) {
            this.e_procedure_status = e_procedure_status;
        }

        public String getE_procedure_status_message() {
            return e_procedure_status_message;
        }

        public void setE_procedure_status_message(String e_procedure_status_message) {
            this.e_procedure_status_message = e_procedure_status_message;
        }

        public String getOb_short_name() {
            return ob_short_name;
        }

        public void setOb_short_name(String ob_short_name) {
            this.ob_short_name = ob_short_name;
        }

        public String getOb_parameter_stable_id() {
            return ob_parameter_stable_id;
        }

        public void setOb_parameter_stable_id(String ob_parameter_stable_id) {
            this.ob_parameter_stable_id = ob_parameter_stable_id;
        }

        public String getOb_sequence_id() {
            return ob_sequence_id;
        }

        public void setOb_sequence_id(String ob_sequence_id) {
            this.ob_sequence_id = ob_sequence_id;
        }

        public Integer getOb_population_id() {
            return ob_population_id;
        }

        public void setOb_population_id(Integer ob_population_id) {
            this.ob_population_id = ob_population_id;
        }

        public String getOb_observation_type() {
            return ob_observation_type;
        }

        public void setOb_observation_type(String ob_observation_type) {
            this.ob_observation_type = ob_observation_type;
        }

        public Integer getOb_missing() {
            return ob_missing;
        }

        public void setOb_missing(Integer ob_missing) {
            this.ob_missing = ob_missing;
        }

        public String getOb_parameter_status() {
            return ob_parameter_status;
        }

        public void setOb_parameter_status(String ob_parameter_status) {
            this.ob_parameter_status = ob_parameter_status;
        }

        public String getOb_parameter_status_message() {
            return ob_parameter_status_message;
        }

        public void setOb_parameter_status_message(String ob_parameter_status_message) {
            this.ob_parameter_status_message = ob_parameter_status_message;
        }

        public String getBm_allelic_composition() {
            return bm_allelic_composition;
        }

        public void setBm_allelic_composition(String bm_allelic_composition) {
            this.bm_allelic_composition = bm_allelic_composition;
        }

        public String getBm_genetic_background() {
            return bm_genetic_background;
        }

        public void setBm_genetic_background(String bm_genetic_background) {
            this.bm_genetic_background = bm_genetic_background;
        }

        public String getBs_external_id() {
            return bs_external_id;
        }

        public void setBs_external_id(String bs_external_id) {
            this.bs_external_id = bs_external_id;
        }

        public String getBs_short_name() {
            return bs_short_name;
        }

        public void setBs_short_name(String bs_short_name) {
            this.bs_short_name = bs_short_name;
        }

        public String getBs_sample_type_acc() {
            return bs_sample_type_acc;
        }

        public void setBs_sample_type_acc(String bs_sample_type_acc) {
            this.bs_sample_type_acc = bs_sample_type_acc;
        }

        public String getBs_sample_type_short_name() {
            return bs_sample_type_short_name;
        }

        public void setBs_sample_type_short_name(String bs_sample_type_short_name) {
            this.bs_sample_type_short_name = bs_sample_type_short_name;
        }

        public String getBs_sample_group() {
            return bs_sample_group;
        }

        public void setBs_sample_group(String bs_sample_group) {
            this.bs_sample_group = bs_sample_group;
        }

        public String getBs_organisation() {
            return bs_organisation;
        }

        public void setBs_organisation(String bs_organisation) {
            this.bs_organisation = bs_organisation;
        }

        public String getBs_production_center() {
            return bs_production_center;
        }

        public void setBs_production_center(String bs_production_center) {
            this.bs_production_center = bs_production_center;
        }

        public String getCob_category() {
            return cob_category;
        }

        public void setCob_category(String cob_category) {
            this.cob_category = cob_category;
        }

        public Date getDob_datetime_point() {
            return dob_datetime_point;
        }

        public void setDob_datetime_point(Date dob_datetime_point) {
            this.dob_datetime_point = dob_datetime_point;
        }

        public String getIrob_full_resolution_file_path() {
            return irob_full_resolution_file_path;
        }

        public void setIrob_full_resolution_file_path(String irob_full_resolution_file_path) {
            this.irob_full_resolution_file_path = irob_full_resolution_file_path;
        }

        public String getIrob_download_file_path() {
            return irob_download_file_path;
        }

        public void setIrob_download_file_path(String irob_download_file_path) {
            this.irob_download_file_path = irob_download_file_path;
        }

        public String getIrob_image_link() {
            return irob_image_link;
        }

        public void setIrob_image_link(String irob_image_link) {
            this.irob_image_link = irob_image_link;
        }

        public String getIrob_organisation() {
            return irob_organisation;
        }

        public void setIrob_organisation(String irob_organisation) {
            this.irob_organisation = irob_organisation;
        }

        public String getIrob_increment_value() {
            return irob_increment_value;
        }

        public void setIrob_increment_value(String irob_increment_value) {
            this.irob_increment_value = irob_increment_value;
        }

        public String getIrob_file_type() {
            return irob_file_type;
        }

        public void setIrob_file_type(String irob_file_type) {
            this.irob_file_type = irob_file_type;
        }

        public String getOob_parameter_id() {
            return oob_parameter_id;
        }

        public void setOob_parameter_id(String oob_parameter_id) {
            this.oob_parameter_id = oob_parameter_id;
        }

        public Integer getOob_sequence_id() {
            return oob_sequence_id;
        }

        public void setOob_sequence_id(Integer oob_sequence_id) {
            this.oob_sequence_id = oob_sequence_id;
        }

        public String getTob_text() {
            return tob_text;
        }

        public void setTob_text(String tob_text) {
            this.tob_text = tob_text;
        }

        public Float getUob_data_point() {
            return uob_data_point;
        }

        public void setUob_data_point(Float uob_data_point) {
            this.uob_data_point = uob_data_point;
        }

        public Float getTsob_data_point() {
            return tsob_data_point;
        }

        public void setTsob_data_point(Float tsob_data_point) {
            this.tsob_data_point = tsob_data_point;
        }

        public Date getTsob_time_point() {
            return tsob_time_point;
        }

        public void setTsob_time_point(Date tsob_time_point) {
            this.tsob_time_point = tsob_time_point;
        }

        public Float getTsob_discrete_point() {
            return tsob_discrete_point;
        }

        public void setTsob_discrete_point(Float tsob_discrete_point) {
            this.tsob_discrete_point = tsob_discrete_point;
        }

        public String getOb_metadata_combined() {
            return ob_metadata_combined;
        }

        public void setOb_metadata_combined(String ob_metadata_combined) {
            this.ob_metadata_combined = ob_metadata_combined;
        }

        public String getOb_metadata_group() {
            return ob_metadata_group;
        }

        public void setOb_metadata_group(String ob_metadata_group) {
            this.ob_metadata_group = ob_metadata_group;
        }

        public String getPa_parameter_id() {
            return pa_parameter_id;
        }

        public void setPa_parameter_id(String pa_parameter_id) {
            this.pa_parameter_id = pa_parameter_id;
        }

        public Integer getPa_sequence_id() {
            return pa_sequence_id;
        }

        public void setPa_sequence_id(Integer pa_sequence_id) {
            this.pa_sequence_id = pa_sequence_id;
        }

        public String getPa_dim_id() {
            return pa_dim_id;
        }

        public void setPa_dim_id(String pa_dim_id) {
            this.pa_dim_id = pa_dim_id;
        }

        public String getPa_parameter_association_value() {
            return pa_parameter_association_value;
        }

        public void setPa_parameter_association_value(String pa_parameter_association_value) {
            this.pa_parameter_association_value = pa_parameter_association_value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ExperimentDetail that = (ExperimentDetail) o;

            if (!e_short_name.equals(that.e_short_name)) return false;
            if (!e_external_id.equals(that.e_external_id)) return false;
            if (e_sequence_id != null ? !e_sequence_id.equals(that.e_sequence_id) : that.e_sequence_id != null)
                return false;
            if (!e_date_of_experiment.equals(that.e_date_of_experiment)) return false;
            if (!e_phenotypingCenter.equals(that.e_phenotypingCenter)) return false;
            if (!e_project.equals(that.e_project)) return false;
            if (!e_pipeline_stable_id.equals(that.e_pipeline_stable_id)) return false;
            if (!e_procedure_stable_id.equals(that.e_procedure_stable_id)) return false;
            if (e_colony_id != null ? !e_colony_id.equals(that.e_colony_id) : that.e_colony_id != null) return false;
            if (!e_metadata_combined.equals(that.e_metadata_combined)) return false;
            if (!e_metadata_group.equals(that.e_metadata_group)) return false;
            if (e_procedure_status != null ? !e_procedure_status.equals(that.e_procedure_status) : that.e_procedure_status != null)
                return false;
            if (e_procedure_status_message != null ? !e_procedure_status_message.equals(that.e_procedure_status_message) : that.e_procedure_status_message != null)
                return false;
            if (!ob_short_name.equals(that.ob_short_name)) return false;
            if (!ob_parameter_stable_id.equals(that.ob_parameter_stable_id)) return false;
            if (ob_sequence_id != null ? !ob_sequence_id.equals(that.ob_sequence_id) : that.ob_sequence_id != null)
                return false;
            if (ob_population_id != null ? !ob_population_id.equals(that.ob_population_id) : that.ob_population_id != null)
                return false;
            if (!ob_observation_type.equals(that.ob_observation_type)) return false;
            if (!ob_missing.equals(that.ob_missing)) return false;
            if (ob_parameter_status != null ? !ob_parameter_status.equals(that.ob_parameter_status) : that.ob_parameter_status != null)
                return false;
            if (ob_parameter_status_message != null ? !ob_parameter_status_message.equals(that.ob_parameter_status_message) : that.ob_parameter_status_message != null)
                return false;
            if (!bm_allelic_composition.equals(that.bm_allelic_composition)) return false;
            if (!bm_genetic_background.equals(that.bm_genetic_background)) return false;
            if (!bs_external_id.equals(that.bs_external_id)) return false;
            if (!bs_short_name.equals(that.bs_short_name)) return false;
            if (bs_sample_type_acc != null ? !bs_sample_type_acc.equals(that.bs_sample_type_acc) : that.bs_sample_type_acc != null)
                return false;
            if (bs_sample_type_short_name != null ? !bs_sample_type_short_name.equals(that.bs_sample_type_short_name) : that.bs_sample_type_short_name != null)
                return false;
            if (!bs_sample_group.equals(that.bs_sample_group)) return false;
            if (!bs_organisation.equals(that.bs_organisation)) return false;
            if (bs_production_center != null ? !bs_production_center.equals(that.bs_production_center) : that.bs_production_center != null)
                return false;
            if (cob_category != null ? !cob_category.equals(that.cob_category) : that.cob_category != null)
                return false;
            if (dob_datetime_point != null ? !dob_datetime_point.equals(that.dob_datetime_point) : that.dob_datetime_point != null)
                return false;
            if (irob_full_resolution_file_path != null ? !irob_full_resolution_file_path.equals(that.irob_full_resolution_file_path) : that.irob_full_resolution_file_path != null)
                return false;
            if (irob_download_file_path != null ? !irob_download_file_path.equals(that.irob_download_file_path) : that.irob_download_file_path != null)
                return false;
            if (irob_image_link != null ? !irob_image_link.equals(that.irob_image_link) : that.irob_image_link != null)
                return false;
            if (irob_organisation != null ? !irob_organisation.equals(that.irob_organisation) : that.irob_organisation != null)
                return false;
            if (irob_increment_value != null ? !irob_increment_value.equals(that.irob_increment_value) : that.irob_increment_value != null)
                return false;
            if (irob_file_type != null ? !irob_file_type.equals(that.irob_file_type) : that.irob_file_type != null)
                return false;
            if (oob_parameter_id != null ? !oob_parameter_id.equals(that.oob_parameter_id) : that.oob_parameter_id != null)
                return false;
            if (oob_sequence_id != null ? !oob_sequence_id.equals(that.oob_sequence_id) : that.oob_sequence_id != null)
                return false;
            if (tob_text != null ? !tob_text.equals(that.tob_text) : that.tob_text != null) return false;
            if (uob_data_point != null ? !uob_data_point.equals(that.uob_data_point) : that.uob_data_point != null)
                return false;
            if (tsob_data_point != null ? !tsob_data_point.equals(that.tsob_data_point) : that.tsob_data_point != null)
                return false;
            if (tsob_time_point != null ? !tsob_time_point.equals(that.tsob_time_point) : that.tsob_time_point != null)
                return false;
            if (tsob_discrete_point != null ? !tsob_discrete_point.equals(that.tsob_discrete_point) : that.tsob_discrete_point != null)
                return false;
            if (ob_metadata_combined != null ? !ob_metadata_combined.equals(that.ob_metadata_combined) : that.ob_metadata_combined != null)
                return false;
            if (ob_metadata_group != null ? !ob_metadata_group.equals(that.ob_metadata_group) : that.ob_metadata_group != null)
                return false;
            if (pa_parameter_id != null ? !pa_parameter_id.equals(that.pa_parameter_id) : that.pa_parameter_id != null)
                return false;
            if (pa_sequence_id != null ? !pa_sequence_id.equals(that.pa_sequence_id) : that.pa_sequence_id != null)
                return false;
            if (pa_dim_id != null ? !pa_dim_id.equals(that.pa_dim_id) : that.pa_dim_id != null) return false;
            return pa_parameter_association_value != null ? pa_parameter_association_value.equals(that.pa_parameter_association_value) : that.pa_parameter_association_value == null;
        }

        @Override
        public int hashCode() {
            int result = e_short_name.hashCode();
            result = 31 * result + e_external_id.hashCode();
            result = 31 * result + (e_sequence_id != null ? e_sequence_id.hashCode() : 0);
            result = 31 * result + e_date_of_experiment.hashCode();
            result = 31 * result + e_phenotypingCenter.hashCode();
            result = 31 * result + e_project.hashCode();
            result = 31 * result + e_pipeline_stable_id.hashCode();
            result = 31 * result + e_procedure_stable_id.hashCode();
            result = 31 * result + (e_colony_id != null ? e_colony_id.hashCode() : 0);
            result = 31 * result + e_metadata_combined.hashCode();
            result = 31 * result + e_metadata_group.hashCode();
            result = 31 * result + (e_procedure_status != null ? e_procedure_status.hashCode() : 0);
            result = 31 * result + (e_procedure_status_message != null ? e_procedure_status_message.hashCode() : 0);
            result = 31 * result + ob_short_name.hashCode();
            result = 31 * result + ob_parameter_stable_id.hashCode();
            result = 31 * result + (ob_sequence_id != null ? ob_sequence_id.hashCode() : 0);
            result = 31 * result + (ob_population_id != null ? ob_population_id.hashCode() : 0);
            result = 31 * result + ob_observation_type.hashCode();
            result = 31 * result + ob_missing.hashCode();
            result = 31 * result + (ob_parameter_status != null ? ob_parameter_status.hashCode() : 0);
            result = 31 * result + (ob_parameter_status_message != null ? ob_parameter_status_message.hashCode() : 0);
            result = 31 * result + bm_allelic_composition.hashCode();
            result = 31 * result + bm_genetic_background.hashCode();
            result = 31 * result + bs_external_id.hashCode();
            result = 31 * result + bs_short_name.hashCode();
            result = 31 * result + (bs_sample_type_acc != null ? bs_sample_type_acc.hashCode() : 0);
            result = 31 * result + (bs_sample_type_short_name != null ? bs_sample_type_short_name.hashCode() : 0);
            result = 31 * result + bs_sample_group.hashCode();
            result = 31 * result + bs_organisation.hashCode();
            result = 31 * result + (bs_production_center != null ? bs_production_center.hashCode() : 0);
            result = 31 * result + (cob_category != null ? cob_category.hashCode() : 0);
            result = 31 * result + (dob_datetime_point != null ? dob_datetime_point.hashCode() : 0);
            result = 31 * result + (irob_full_resolution_file_path != null ? irob_full_resolution_file_path.hashCode() : 0);
            result = 31 * result + (irob_download_file_path != null ? irob_download_file_path.hashCode() : 0);
            result = 31 * result + (irob_image_link != null ? irob_image_link.hashCode() : 0);
            result = 31 * result + (irob_organisation != null ? irob_organisation.hashCode() : 0);
            result = 31 * result + (irob_increment_value != null ? irob_increment_value.hashCode() : 0);
            result = 31 * result + (irob_file_type != null ? irob_file_type.hashCode() : 0);
            result = 31 * result + (oob_parameter_id != null ? oob_parameter_id.hashCode() : 0);
            result = 31 * result + (oob_sequence_id != null ? oob_sequence_id.hashCode() : 0);
            result = 31 * result + (tob_text != null ? tob_text.hashCode() : 0);
            result = 31 * result + (uob_data_point != null ? uob_data_point.hashCode() : 0);
            result = 31 * result + (tsob_data_point != null ? tsob_data_point.hashCode() : 0);
            result = 31 * result + (tsob_time_point != null ? tsob_time_point.hashCode() : 0);
            result = 31 * result + (tsob_discrete_point != null ? tsob_discrete_point.hashCode() : 0);
            result = 31 * result + (ob_metadata_combined != null ? ob_metadata_combined.hashCode() : 0);
            result = 31 * result + (ob_metadata_group != null ? ob_metadata_group.hashCode() : 0);
            result = 31 * result + (pa_parameter_id != null ? pa_parameter_id.hashCode() : 0);
            result = 31 * result + (pa_sequence_id != null ? pa_sequence_id.hashCode() : 0);
            result = 31 * result + (pa_dim_id != null ? pa_dim_id.hashCode() : 0);
            result = 31 * result + (pa_parameter_association_value != null ? pa_parameter_association_value.hashCode() : 0);
            return result;
        }
    }

    public class ExperimentDetailRowMapper implements RowMapper<ExperimentDetail> {

        /**
         * Implementations must implement this method to map each row of data
         * in the ResultSet. This method should not call {@code next()} on
         * the ResultSet; it is only supposed to map values of the current row.
         *
         * @param rs     the ResultSet to map (pre-initialized for the current row)
         * @param rowNum the number of the current row
         * @return the result object for the current row
         * @throws SQLException if a SQLException is encountered getting
         *                      column values (that is, there's no need to catch SQLException)
         */
        @Override
        public ExperimentDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
            ExperimentDetail experiment = new ExperimentDetail();

            experiment.setE_short_name(rs.getString("e_short_name"));
            experiment.setE_external_id(rs.getString("e_external_id"));
            experiment.setE_sequence_id(rs.getString("e_sequence_id"));
            experiment.setE_date_of_experiment(rs.getDate("e_date_of_experiment"));
            experiment.setE_phenotypingCenter(rs.getString("e_phenotypingCenter"));
            experiment.setE_project(rs.getString("e_project"));
            experiment.setE_pipeline_stable_id(rs.getString("e_pipeline_stable_id"));
            experiment.setE_procedure_stable_id(rs.getString("e_procedure_stable_id"));
            experiment.setE_colony_id(rs.getString("e_colony_id"));
            experiment.setE_metadata_combined(rs.getString("e_metadata_combined"));
            experiment.setE_metadata_group(rs.getString("e_metadata_group"));
            experiment.setE_procedure_status(rs.getString("e_procedure_status"));
            experiment.setE_procedure_status_message(rs.getString("e_procedure_status_message"));

            experiment.setOb_short_name(rs.getString("ob_short_name"));
            experiment.setOb_parameter_stable_id(rs.getString("ob_parameter_stable_id"));
            experiment.setOb_sequence_id(rs.getString("ob_sequence_id"));
            experiment.setOb_population_id(rs.getInt("ob_population_id"));
            experiment.setOb_observation_type(rs.getString("ob_observation_type"));
            experiment.setOb_missing(rs.getInt("ob_missing"));
            experiment.setOb_parameter_status(rs.getString("ob_parameter_status"));
            experiment.setOb_parameter_status_message(rs.getString("ob_parameter_status_message"));

            experiment.setBm_allelic_composition(rs.getString("bm_allelic_composition"));
            experiment.setBm_genetic_background(rs.getString("bm_genetic_background"));

            experiment.setBs_external_id(rs.getString("bs_external_id"));
            experiment.setBs_short_name(rs.getString("bs_short_name"));
            experiment.setBs_sample_type_acc(rs.getString("bs_sample_type_acc"));
            experiment.setBs_sample_type_short_name(rs.getString("bs_sample_type_short_name"));
            experiment.setBs_sample_group(rs.getString("bs_sample_group"));
            experiment.setBs_organisation(rs.getString("bs_organisation"));
            experiment.setBs_production_center(rs.getString("bs_production_center"));

            experiment.setCob_category(rs.getString("cob_category"));

            experiment.setDob_datetime_point(rs.getDate("dob_datetime_point"));

            experiment.setIrob_full_resolution_file_path(rs.getString("irob_full_resolution_file_path"));
            experiment.setIrob_download_file_path(rs.getString("irob_download_file_path"));
            experiment.setIrob_image_link(rs.getString("irob_image_link"));
            experiment.setIrob_organisation(rs.getString("irob_organisation"));
            experiment.setIrob_increment_value(rs.getString("irob_increment_value"));
            experiment.setIrob_file_type(rs.getString("irob_file_type"));

            experiment.setOob_parameter_id(rs.getString("oob_parameter_id"));
            experiment.setOob_sequence_id(rs.getInt("oob_sequence_id"));

            experiment.setTob_text(rs.getString("tob_text"));

            experiment.setTsob_data_point(rs.getFloat("tsob_data_point"));
            experiment.setTsob_time_point(rs.getTimestamp("tsob_time_point"));
            experiment.setTsob_discrete_point(rs.getFloat("tsob_discrete_point"));

            experiment.setUob_data_point(rs.getFloat("uob_data_point"));

            experiment.setOb_metadata_combined(rs.getString("ob_metadata_combined"));
            experiment.setOb_metadata_group(rs.getString("ob_metadata_group"));

            experiment.setPa_parameter_id(rs.getString("pa_parameter_id"));
            experiment.setPa_sequence_id(rs.getInt("pa_sequence_id"));
            experiment.setPa_dim_id(rs.getString("pa_dim_id"));
            experiment.setPa_parameter_association_value(rs.getString("pa_parameter_association_value"));

            return experiment;
        }
    }

    public class DiffDetail {
        private List<String>       headings              = new ArrayList<>();
        private List<List<String>> data                  = new ArrayList<>();
        private List<Integer>      mismatchColumnNumbers = new ArrayList<>();

        public DiffDetail() {

        }

        public DiffDetail(List<String> headings, List<List<String>> data, List<Integer> mismatchColumnNumbers) {
            this.headings = headings;
            this.data = data;
            this.mismatchColumnNumbers = mismatchColumnNumbers;
        }

        public List<String> getHeadings() {
            return headings;
        }

        public void setHeadings(List<String> headings) {
            this.headings = headings;
        }

        public List<List<String>> getData() {
            return data;
        }

        public void setData(List<List<String>> data) {
            this.data = data;
        }

        public List<Integer> getMismatchColumnNumbers() {
            return mismatchColumnNumbers;
        }

        public void setMismatchColumnNumbers(List<Integer> mismatchColumnNumbers) {
            this.mismatchColumnNumbers = mismatchColumnNumbers;
        }
    }

}