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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

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
    public RunStatus execute() throws ReportException {

        int                 centerColumnIndex = -1;
        String[]            emptyColumn      = new String[] { "" };
        List<String>        emptyExperiment  = new ArrayList<>();
        List<String>        noDifferences    = new ArrayList<>();
        Map<String, Object> parameterMap     = new HashMap<>();
        boolean             populateHeadings = true;
        RunStatus           status           = new RunStatus();

        try {

            for (String experimentId : experimentIds) {

                parameterMap.put("experimentId", experimentId);
                ExperimentDetail detail = queryDetail(jdbc1, jdbc2, query, parameterMap, populateHeadings);
                if (populateHeadings) {
                    populateHeadings = false;
                    writeDbNames(detail);
                    centerColumnIndex = computeCenterColumnIndex(detail);
                    csvWriter.writeRow(detail.getHeadings());                                                           // Write the jdbc1 headings.
                    csvWriter.writeNext(emptyColumn);                                                                   // Write an empty column.
                    csvWriter.writeRow(detail.getHeadings());                                                           // Write the jdbc1 headings.
                    for (int i = 0; i < detail.getHeadings().size(); i++) {                                             // Populate the empty experiment list
                        emptyExperiment.add("");
                    }
                }

                if (detail.getDifferences().isEmpty()) {
                    noDifferences.add(detail.getRow1().get(centerColumnIndex) + "::" + experimentId);
                } else {
                    List<List<String>> list1 = detail.getRow1();
                    List<List<String>> list2 = detail.getRow2();

                    for (int i = 0; i < Math.max(list1.size(), list2.size()); i++) {
                        if (i < list1.size()) {
                            csvWriter.writeRow(list1.get(i));
                        } else {
                            csvWriter.writeRow(emptyExperiment);
                        }
                        csvWriter.writeNext(emptyColumn);
                        if (i < list2.size()) {
                            csvWriter.writeRow(list2.get(i));
                        } else {
                            csvWriter.writeRow(emptyExperiment);
                        }
                    }
                }
            }

            csvWriter.writeNext(emptyColumn);                                                                           // Write an empty row before the experiments with no differences.

            List<String> row = new ArrayList<>();
            for (int i = 0; i < noDifferences.size(); i++) {
                row.add(noDifferences.get(i));
                if (i % 1000 == 0) {
                    csvWriter.writeRow(row);
                    row.clear();
                }
            }
            if ( ! row.isEmpty()) {
                csvWriter.writeRow(row);
            }

        } catch (Exception e) {

            throw new ReportException(e);
        }

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


    /**
     * Given two {@link NamedParameterJdbcTemplate} instances, a query, and a {@code parameterMap}, executes the query
     * against {@code jdbc1}, then against {@code jdbc2}, returning an {@link ExperimentDetail} instance with all
     * mismatches. If there are no mismatches, {@link ExperimentDetail}.differences is empty. The remainder of the
     * {@link ExperimentDetail} collections are filled out as appropriate.
     *
     * @param jdbc1            the first {@link NamedParameterJdbcTemplate} instance
     * @param jdbc2            the second {@link NamedParameterJdbcTemplate} instance
     * @param query            the query to execute against both {@link NamedParameterJdbcTemplate} instances
     * @param parameterMap     initialised parameter map
     * @param populateHeadings if true, the headings are returned; if false, the headings are not returned.
     * @return the list difference of the results found in jdbc1 but not found in jdbc2. If there are no differences,
     * an empty list is returned.
     * @throws Exception
     */
    private ExperimentDetail queryDetail(NamedParameterJdbcTemplate jdbc1, NamedParameterJdbcTemplate jdbc2, String query, Map<String, Object> parameterMap, boolean populateHeadings) throws Exception {
        ExperimentDetail results = new ExperimentDetail();

        List<List<String>> results1 = new ArrayList<>();
        SqlRowSet          rs1      = jdbc1.queryForRowSet(query, parameterMap);
        while (rs1.next()) {
            results1.add(sqlUtils.getData(rs1));
        }

        List<List<String>> results2 = new ArrayList<>();
        SqlRowSet          rs2      = jdbc2.queryForRowSet(query, parameterMap);
        while (rs2.next()) {
            results2.add(sqlUtils.getData(rs2));
        }

        if (populateHeadings) {
            List<String> columnNames = Arrays.asList(rs1.getMetaData().getColumnNames());
            results.setHeadings(columnNames);
        }

        results.setRow1(results1);
        results.setRow2(results2);
        for (int rowIndex = 0; rowIndex < Math.min(results1.size(), results2.size()); rowIndex++) {           // Compute differences.
            List<String> row1 = results1.get(rowIndex);
            List<String> row2 = results2.get(rowIndex);

            List<Integer> rowDifferences = new ArrayList<>();
            results.getDifferences().add(rowDifferences);

            for (int colIndex = 0; colIndex < row1.size(); colIndex++) {
                String cell1 = row1.get(colIndex);
                String cell2 = row2.get(colIndex);
                if (!cell1.equals(cell2)) {
                    rowDifferences.add(colIndex);
                }
            }
        }

        return results;
    }

    private int computeCenterColumnIndex(ExperimentDetail detail) {
        for (int i = 0; i < detail.getHeadings().size(); i++) {
            if (detail.getHeadings().get(i).equals("e_organisation")) {
                return i;
            }
        }

        return -1;
    }

    private void writeDbNames(ExperimentDetail detail) {
        String[] cells = new String[detail.getHeadings().size()];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = "";
        }

        String dbname1 = jdbc1.queryForObject("SELECT DBNAME()", new HashMap<String, Object>(), String.class);
        String dbname2 = jdbc2.queryForObject("SELECT DBNAME()", new HashMap<String, Object>(), String.class);
        cells[0] = dbname1;
        csvWriter.writeNext(cells);
        csvWriter.writeNext(new String[] {""});     // Blank column
        cells[0] = dbname2;
        csvWriter.writeNext(cells);
    }
}