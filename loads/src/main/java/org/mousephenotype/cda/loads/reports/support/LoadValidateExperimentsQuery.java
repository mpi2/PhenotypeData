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

package org.mousephenotype.cda.loads.reports.support;

import org.mousephenotype.cda.db.utilities.SqlUtils;
import org.mousephenotype.cda.reports.support.ReportException;
import org.mousephenotype.cda.utilities.CommonUtils;
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

    private Logger      logger      = LoggerFactory.getLogger(this.getClass());
    private CommonUtils commonUtils = new CommonUtils();
    private SqlUtils    sqlUtils    = new SqlUtils();

    private MpCSVWriter                csvWriter;
    private List<String>               columnAliases                = new ArrayList<>();
    private Map<String, Integer>       columnIndexesByName          = new HashMap<>();
    private Map<Integer, String>       columnNamesByColumnIndex     = new HashMap<>();
    private int                        count;
    private String                     dbname1;
    private String                     dbname2;
    List<String>                       emptyExperiment              = new ArrayList<>();

    private List<String> experimentIds;
    private boolean      includeDerived;
    private Set<String>  skipColumns;
    private Set<String>  skipParameters;
    private Set<Integer> skipColumnIndexSet = new HashSet<>();

    private NamedParameterJdbcTemplate jdbc1;
    private NamedParameterJdbcTemplate jdbc2;


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
     * @param jdbc1          the first {@link NamedParameterJdbcTemplate} instance
     * @param jdbc2          the second {@link NamedParameterJdbcTemplate} instance
     * @param csvWriter      the spreadsheet instance
     * @param experimentIds  an optional List&lt;String&gt; containing experimentIds to be validated
     * @param count          an optional count of the number of experiment ids to randomly generate and validate. This
     *                       count is above and beyond any experiments in {@code experimentIds}. The default is 100.
     * @param skipColumns     an optional collection of column aliases to exclude from validation
     * @param skipParameters  an optional collection of parameter stable ids to exclude from validation
     * @param includeDerived  an optional flag to indicate that derived experiments should be included in the random selection
     */
    public LoadValidateExperimentsQuery(NamedParameterJdbcTemplate jdbc1, NamedParameterJdbcTemplate jdbc2, MpCSVWriter csvWriter, List<String> experimentIds,
                                        int count, Set<String> skipColumns, Set<String> skipParameters, boolean includeDerived) {
        this.jdbc1 = jdbc1;
        this.jdbc2 = jdbc2;
        this.csvWriter = csvWriter;
        this.experimentIds = experimentIds;
        this.count = count;
        this.includeDerived = includeDerived;
        this.skipColumns = skipColumns;
        this.skipParameters = skipParameters;
    }


    /**
     * Execute the baseQuery for each experimentId. For each experiment row that is not an exact match in both databases,
     * write each row on a single line, highlighting each cell difference using color. If an experiment row is an exact
     * match, save the center and experimentId for subsequent output to the worksheet, after all mismatches, to capture
     * the center/experimentIds that were compared and were equal.
     *
     * @return a {@link RunStatus} with a list of errors
     * @throws ReportException
     */
    public RunStatus execute() throws ReportException {

        Map<String, Object> parameterMap      = new HashMap<>();
        RunStatus           status            = new RunStatus();

        try {
            initialise();

            // Validate experiments.
            for (String experimentId : experimentIds) {

                parameterMap.put("experimentId", experimentId);
                StringBuilder query = new StringBuilder(baseQuery).append(whereClause);
                if ( ! skipParameters.isEmpty()) {
                    String parameterList = commonUtils.wrapInQuotes(skipParameters, '\'');
                    query.append(baseOmitParameterClause.replace("PARAMETER_REPLACE_TEMPLATE", parameterList));
                }
                query.append(orderByClause);
                List<ExperimentDetail> detailList = queryDetail(jdbc1, jdbc2, query.toString(), parameterMap);

                for (ExperimentDetail detail : detailList) {
                    if ( ! detail.getColIndexDifference().isEmpty()) {
                        writeDifferences(detail);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ReportException(e);
        }

        return status;
    }


    private final String baseQuery =
            "-- cdaExperimentWithDetail\n" +
                    "\n" +
                    "-- explain\n" +
                    "SELECT\n" +
                    "  edb.short_name                        AS e_short_name,\n" +
                    "  e.external_id                         AS e_experiment_id,\n" +
                    "  e.sequence_id                         AS e_sequence_id,\n" +
                    "  e.date_of_experiment                  AS e_date_of_experiment,            -- timestamp\n" +
                    "  eorg.name                             AS e_phenotyping_center,\n" +
                    "  pr.name                               AS e_project,\n" +
                    "  e.pipeline_stable_id                  AS e_pipeline_stable_id,\n" +
                    "  e.procedure_stable_id                 AS e_procedure_stable_id,\n" +
                    "  e.colony_id                           AS e_colony_id,\n" +
                    "  ( SELECT GROUP_CONCAT(DISTINCT CONCAT(pp.name, ' = ', pmdin.value) ORDER BY pmdin.parameter_id ASC SEPARATOR '::')\n" +
                    "    FROM procedure_meta_data pmdin\n" +
                    "    JOIN phenotype_parameter pp ON pp.stable_id=pmdin.parameter_id\n" +
                    "      WHERE e.id=pmdin.experiment_id AND pp.name NOT LIKE '%Experimenter%') AS e_metadata_combined,\n" +
                    "  ( SELECT IF(GROUP_CONCAT(DISTINCT pmdin.value) IS NULL, '', MD5(GROUP_CONCAT(DISTINCT CONCAT(pp.name, ' = ', pmdin.value) ORDER BY pmdin.parameter_id ASC SEPARATOR '::')))\n" +
                    "    FROM procedure_meta_data pmdin\n" +
                    "    JOIN phenotype_parameter pp ON pp.stable_id=pmdin.parameter_id\n" +
                    "    WHERE pp.important=1 AND pmdin.experiment_id=e.id) AS e_metadata_group,\n" +
                    "  e.procedure_status                    AS e_procedure_status,\n" +
                    "  e.procedure_status_message            AS e_procedure_status_message,\n" +
                    "  \n" +
                    "  'OBSERVATION',\n" +
                    "  ob.id                                 AS observation_id,\n" +
                    "  obdb.short_name                       AS ob_short_name,\n" +
                    "  ob.parameter_stable_id                AS ob_parameter_stable_id,\n" +
                    "  ob.sequence_id                        AS ob_sequence_id,\n" +
                    "  ob.population_id                      AS ob_population_id,                -- int\n" +
                    "  ob.observation_type                   AS ob_observation_type,\n" +
                    "  ob.missing                            AS ob_missing,                      -- int\n" +
                    "  ob.parameter_status                   AS ob_parameter_status,\n" +
                    "  ob.parameter_status_message           AS ob_parameter_status_message,\n" +
                    "  \n" +
                    "  'BIOLOGICAL_MODEL',\n" +
                    "  -- bmdb.short_name                             AS bm_short_name,\n" +
                    "  bm.allelic_composition                AS bm_allelic_composition,\n" +
                    "  bm.genetic_background                 AS bm_genetic_background,\n" +
                    "  \n" +
                    "  'LIVE_SAMPLE',\n" +
                    "  ls.colony_id                          AS ls_colony_id,\n" +
                    "  \n" +
                    "  'BIOLOGICAL_SAMPLE',\n" +
                    "  bs.external_id                        AS bs_external_id,\n" +
                    "  bsdb.short_name                       AS bs_short_name,\n" +
                    "  bs.sample_type_acc                    AS bs_sample_type_acc,\n" +
                    "  bsstdb.short_name                     AS bs_sample_type_short_name,\n" +
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
                    "        WHERE pp.important = 1\n" +
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
                    "JOIN             live_sample                    ls      ON ls       .id                 = ob    .biological_sample_id\n" +
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
                    "LEFT OUTER JOIN procedure_meta_data             pmdob   ON pmdob    .experiment_id      = e.id AND pmdob.observation_id = ob.id\n";

    private final String whereClause = "WHERE e.external_id = :experimentId\n";

    private final String baseOmitParameterClause = "AND ob.parameter_stable_id NOT IN (PARAMETER_REPLACE_TEMPLATE)\n";

    private final String orderByClause =
            "ORDER BY e_short_name,e_phenotyping_center,e_experiment_id,e_sequence_id,e_procedure_stable_id,e_procedure_status,e_procedure_status_message,ob_observation_type,\n" +
            "ob_parameter_stable_id,ob.missing,ob_parameter_status,bs_external_id,cob_category,dob_datetime_point,irob_increment_value,irob_full_resolution_file_path,oob_parameter_id,\n" +
            "oob_sequence_id,tob.text,tsob_discrete_point,tsob_data_point,uob_data_point,ob_metadata_combined,ob_metadata_group,\n" +
            "pa_parameter_id,pa_sequence_id,pa_parameter_association_value\n";


    // PRIVATE METHODS


    private void initialise() {

        dbname1 = sqlUtils.getDatabaseName(jdbc1);
        dbname2 = sqlUtils.getDatabaseName(jdbc2);

        // Get column headings and populate column lookup maps.
        SqlRowSet rs1 = jdbc1.queryForRowSet(baseQuery + "LIMIT 0\n", new HashMap<>());
        for (int i = 0; i < rs1.getMetaData().getColumnCount(); i++) {
            columnAliases.add(rs1.getMetaData().getColumnLabel(i + 1));                                              // column label indexes are 1-relative.
        }
        loadColumnMaps(columnAliases);

        // Write spreadsheet heading
        writeDbNames();
        writeHeadings();

        for (int i = 0; i < columnAliases.size(); i++) {                                                                // Populate emptyExperiment.
            emptyExperiment.add("");
        }

        // populate the ignoreColumnIndex set.
        skipColumnIndexSet.add(columnIndexesByName.get("observation_id"));                                            // Always ignore the observation_id (primary key).
        skipColumnIndexSet.add(columnIndexesByName.get("ls_colony_id"));                                            // Always ignore the ls_colony_id (min.
        for (String ignoreAlias : skipColumns) {
            Integer colIndex = columnIndexesByName.get(ignoreAlias);
            if (colIndex != null) {
                skipColumnIndexSet.add(colIndex);
            }
        }

        // If count > 0, select a randomised list of experimentIds.
        List<String> allExperimentIds = new ArrayList<>();
        if (count > 0) {
            StringBuilder query = new StringBuilder("SELECT external_id FROM experiment\n");
            if ( ! includeDerived) {
                query.append("WHERE external_id NOT LIKE 'derived%'");
            }
            SqlRowSet rs = jdbc1.queryForRowSet(query.toString(), new HashMap<>());
            while (rs.next()) {
                allExperimentIds.add(rs.getString("external_id"));
            }

            Collections.shuffle(allExperimentIds);                                                                      // Randomise all experimentIds.

            // Remove duplicates and truncate the list as directed by 'count'.
            allExperimentIds.removeAll(experimentIds);                                                                // Remove any experimentIds already accounted for in experimentIds (to avoid duplicates).
            allExperimentIds = allExperimentIds.subList(0, count);
            experimentIds.addAll(allExperimentIds);
        }
    }

    private void loadColumnMaps(List<String> columnNames) {
        for (int i = 0; i < columnNames.size(); i++) {
            columnIndexesByName.put(columnNames.get(i), i);
            columnNamesByColumnIndex.put(i, columnNames.get(i));
        }
    }

    /**
     * Given two {@link NamedParameterJdbcTemplate} instances, a baseQuery, and a {@code parameterMap}, executes the baseQuery
     * against {@code jdbc1}, then against {@code jdbc2}, returning an {@link ExperimentDetail} instance with all
     * mismatches. If there are no mismatches, {@link ExperimentDetail}.differences is empty. The remainder of the
     * {@link ExperimentDetail} collections are filled out as appropriate.
     *
     * @param jdbc1            the first {@link NamedParameterJdbcTemplate} instance
     * @param jdbc2            the second {@link NamedParameterJdbcTemplate} instance
     * @param query            the baseQuery to execute against both {@link NamedParameterJdbcTemplate} instances
     * @param parameterMap     initialised parameter map
     * @return the list difference of the results found in jdbc1 but not found in jdbc2. If there are no differences,
     * an empty list is returned.
     * @throws Exception
     */
    private List<ExperimentDetail> queryDetail(NamedParameterJdbcTemplate jdbc1, NamedParameterJdbcTemplate jdbc2, String query, Map<String, Object> parameterMap) throws Exception {
        List<ExperimentDetail> results  = new ArrayList<>();
        List<List<String>>     results1 = new ArrayList<>();
        List<List<String>>     results2 = new ArrayList<>();
        SqlRowSet              rs1      = jdbc1.queryForRowSet(query, parameterMap);
        SqlRowSet              rs2      = jdbc2.queryForRowSet(query, parameterMap);

        while (rs1.next()) {
            results1.add(sqlUtils.getData(rs1));
        }

        while (rs2.next()) {
            results2.add(sqlUtils.getData(rs2));
        }

        for (int rowIndex = 0; rowIndex < Math.max(results1.size(), results2.size()); rowIndex++) {                     // Capture differences.

            ExperimentDetail detail = new ExperimentDetail();
            List<Integer> detailRowDifferences = new ArrayList<>();
            List<String> detailRow1 = (rowIndex < results1.size()  ? results1.get(rowIndex) : emptyExperiment);
            List<String> detailRow2 = (rowIndex < results2.size() ? results2.get(rowIndex) : emptyExperiment);
            detail.setRow1(detailRow1);
            detail.setRow2(detailRow2);
            detail.setColIndexDifference(detailRowDifferences);

            for (int colIndex = 0; colIndex < detailRow1.size(); colIndex++) {

                // Skip columns requested to be ignored
                if (skipColumnIndexSet.contains(colIndex)) {
                    continue;
                }

                String cell1 = detailRow1.get(colIndex);
                String cell2 = detailRow2.get(colIndex);
                if (cell1 == null) {
                    if (cell2 != null) {
                        detailRowDifferences.add(colIndex);
                    }
                } else if (cell2 == null) {
                    if (cell1 != null) {
                        detailRowDifferences.add(colIndex);
                    }
                } else if ( ! cell1.equals(cell2)) {
                    detailRowDifferences.add(colIndex);
                }
            }

            if ( ! detailRowDifferences.isEmpty()) {
                results.add(detail);
            }

        }

        return results;
    }

    private void writeDbNames() {
        csvWriter.writeNext(new String[] { "Experiment differences between " + dbname1 + " and " + dbname2 });
    }

    private void writeDifferences(ExperimentDetail detail) {
        List<String> row = new ArrayList<>();
        int phenotypingCenterColumIndex = columnIndexesByName.get("e_phenotyping_center");
        int experimentIdColumnIndex = columnIndexesByName.get("e_experiment_id");
        int observationIdColumnIndex = columnIndexesByName.get("observation_id");
        int colonyIdColumnIndex = columnIndexesByName.get("ls_colony_id");

        row.add(detail.getRow1().get(phenotypingCenterColumIndex));
        row.add(detail.getRow1().get(experimentIdColumnIndex));
        row.add(detail.getRow1().get(observationIdColumnIndex) + "::" + detail.getRow2().get(observationIdColumnIndex));
        row.add(detail.getRow1().get(colonyIdColumnIndex));

        for (int i = 0; i < detail.getColIndexDifference().size(); i++) {
            String columnName = columnNamesByColumnIndex.get(detail.getColIndexDifference().get(i));
            String db1Value = detail.getRow1().get(detail.getColIndexDifference().get(i));
            String db2Value = detail.getRow2().get(detail.getColIndexDifference().get(i));
            row.add(columnName + "::" + db1Value + "::" + db2Value);
        }

        csvWriter.writeRow(row);
    }

    private void writeHeadings() {
        List<String> row = new ArrayList<>();
        row.add("e_phenotyping_center");
        row.add("e_experiment_id");
        row.add("observation_id");
        row.add("ls_colony_id");

        csvWriter.writeRow(row);
    }
}