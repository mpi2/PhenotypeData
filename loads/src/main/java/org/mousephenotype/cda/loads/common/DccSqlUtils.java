/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.loads.common;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.loads.exceptions.DataLoadException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.common.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.*;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.specimen.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mrelac on 02/03/2016.
 */
public class DccSqlUtils {

    private       CommonUtils          commonUtils = new CommonUtils();
    private final Logger               logger      = LoggerFactory.getLogger(this.getClass());
    private NamedParameterJdbcTemplate npJdbcTemplate;
    private       LoadUtils            loadUtils;

    // Cache for toe PK key from the DCC database
    private Map<Map<String, String>, Long> centerPkMap = new HashMap<>();



    @Inject
    public DccSqlUtils(NamedParameterJdbcTemplate npJdbcTemplate) {
        Assert.notNull(npJdbcTemplate, "Named parameter npJdbcTemplate cannot be null");
        this.npJdbcTemplate = npJdbcTemplate;
        loadUtils = new LoadUtils();
    }


    // These colonies are EuroPhenome colonies that have duplicated work on a cell line.  Disambiguation
   	// is by including a trailing ID (the rightmost numerical string, separated by underscore).  Most
   	// of the EuroPhenome colonies have the trailing ID stripped for translation, but these need to
   	// be uniquely identified.
   	//
   	// Records have been added into iMits (2016-01-25) to handle these cases, so do not strip the trailing ID
   	// from these legacy colony IDs.
   	List<String> DO_NOT_TRUNCATE = Arrays.asList(
   		"EPD0013_1_G11_10613", "EPD0019_1_A05_10494", "EPD0023_1_F07_10481", "EPD0033_3_F04_10955",
   		"EPD0037_1_E07_10471", "EPD0037_3_G01_10533", "EPD0039_1_B01_10470", "EPD0057_2_C02_10474",
   		"EPD0065_2_E04_10968", "EPD0089_4_F11_10538", "EPD0100_4_A06_10472", "EPD0135_1_A05_10967",
   		"EPD0156_1_B01_10970", "EPD0242_4_B03_10958", "EPD0011_3_B08_10331",
   		"EPD0017_3_E02_71",    "EPD0019_1_A05_10574", "EPD0023_1_F07_10553", "EPD0033_3_F04_10514",
   		"EPD0037_1_B03_10395", "EPD0037_1_E07_10554", "EPD0038_2_B10_10557", "EPD0057_1_H01_10556",
   		"EPD0057_2_C02_10549", "EPD0065_2_E04_10515", "EPD0065_5_A04_10523", "EPD0089_4_F11_320",
   		"EPD0100_4_A06_10380", "EPD0135_1_A05_10581", "EPD0145_4_B09_10343", "EPD0156_1_B01_10099",
   		"EPD0242_4_B03_10521", "EPD0023_1_F07_10594", "EPD0037_1_B03_10632", "EPD0037_1_E07_10595",
   		"EPD0038_2_B10_10026", "EPD0046_2_F02_216",   "EPD0057_1_H01_134",   "EPD0057_2_C02_10630",
   		"EPD0065_2_E04_10028", "EPD0065_5_A04_141",   "EPD0089_4_F11_10578", "EPD0100_4_A06_10519",
   		"EPD0135_1_A05_10631", "EPD0156_1_B01_10517", "EPD0242_4_B03_10579", "EPD0011_3_B08_28",
   		"EPD0013_1_G11_10560", "EPD0017_3_E02_10220", "EPD0019_1_A05_73",    "EPD0033_3_F04_10232",
   		"EPD0037_1_B03_10234", "EPD0037_3_G01_10649", "EPD0039_1_B01_157",   "EPD0046_2_F02_10658",
   		"EPD0135_1_A05_10562", "EPD0145_4_B09_10826", "EPD0242_4_B03_10233",
        "Dll1_C3H_113",        "Dll1_C3H_10333",      "EPD0059_2_C08_10660");

    /**
     * Translation is required for some project names supplied in the data files
     * Record the translations in the PROJECT_NAME_TRANSLATIONS map
     */
    Map<String, String> PROJECT_NAME_TRANSLATIONS = Collections.unmodifiableMap(Stream.of(
            new AbstractMap.SimpleEntry<>("Eumodic", "EUMODIC"))
            .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));

    /**
     * Most of the EuroPhenome colony ids provided by the DCC have a number appended to them that must be removed in order to match
     * the colony id as known by imits. There are a few cases that require the colony id to be left untouched, as they
     * are needed to disambiguate between colonies with duplicated work on a cell line.
     *
     * This method corrects the colony id according to the rules above: If the colony id has an underscore AND IT IS
     * NOT IN THE 'DO_NOT_TRUNCATE' LIST, truncate the colony id by removing the trailing underscore and all the
     * characters after it.
     *
     * If the colony id does not contain an underscore, or the colony id is in the DO_NOT_TRUNCATE list, simply return
     * it.
     *
     * NOTE: Some colony ids have leading and/or trailing spaces. Always trim them before returning them.
     *
     * @param colonyId
     *
     * @return the colony id, corrected if necessary
     */
    public String correctEurophenomeColonyId(String colonyId) {
        String retVal = colonyId;

        if ( ! DO_NOT_TRUNCATE.contains(colonyId)) {
            int idx = colonyId.lastIndexOf("_");
            if (idx > 0) {
                retVal = colonyId.substring(0, idx);
            }
        }

        return retVal.trim();
    }


    public String dumpSpecimen(long centerPk, long specimenPk) {
        String retVal = "";

        String query =
                "SELECT\n"
                        + "  cs.pk AS cs_pk\n"
                        + ", c.pk AS c_pk\n"
                        + ", s.pk AS s_pk\n"
                        + ", s.statuscode_pk AS s_statuscode_pk\n"
                        + ", c.centerId\n"
                        + ", c.pipeline\n"
                        + ", c.project\n"
                        + ", s.colonyId\n"
                        + ", s.gender\n"
                        + ", s.isBaseline\n"
                        + ", s.litterId\n"
                        + ", s.phenotypingCenter\n"
                        + ", s.pipeline\n"
                        + ", s.productionCenter\n"
                        + ", s.specimenId\n"
                        + ", s.strainId\n"
                        + ", s.zygosity\n"
                        + ", sc.dateOfStatuscode\n"
                        + ", sc.value\n"
                        + ", m.DOB\n"
                        + ", e.stage\n"
                        + ", e.stageUnit\n"
                        + "FROM center c\n"
                        + "JOIN center_specimen cs ON cs.center_pk = c.pk\n"
                        + "JOIN specimen s ON cs.specimen_pk = s.pk\n"
                        + "LEFT OUTER JOIN mouse m ON m.specimen_pk = cs.specimen_pk\n"
                        + "LEFT OUTER JOIN embryo e ON e.specimen_pk = cs.specimen_pk\n"
                        + "LEFT OUTER JOIN statuscode sc ON sc.pk = s.statuscode_pk\n"
                        + "WHERE c.pk = :centerPk AND s.pk = :specimenPk;";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("centerPk", centerPk);
        parameterMap.put("specimenPk", specimenPk);
        SqlRowSet rs = npJdbcTemplate.queryForRowSet(query, parameterMap);

        if (rs.next()) {
            retVal += "{"
                    + "cs.pk=" + rs.getLong("cs_pk")
                    + ",c.pk=" + rs.getLong("c_pk")
                    + ",s.pk=" + rs.getLong("s_pk")
                    + ",s.statuscode_pk=" + (rs.getLong("s_statuscode_pk") == 0 ? "<null>" : rs.getLong("s_statuscode_pk"))
                    + ",centerId=" + rs.getString("c.centerId")
                    + ",pipeline=" + rs.getString("c.pipeline")
                    + ",project=" + rs.getString("c.project")
                    + ",colonyId=" + rs.getString("s.colonyId")
                    + ",gender=" + rs.getString("s.gender")
                    + ",isBaseline=" + rs.getInt("s.isBaseline")
                    + ",litterId=" + rs.getString("s.litterId")
                    + ",phenotypingCenter=" + rs.getString("s.phenotypingCenter")
                    + ",productionCenter=" + (rs.getString("s.productionCenter") == null ? "<null>" : rs.getString("s.productionCenter"))
                    + ",specimenId=" + rs.getString("s.specimenId")
                    + ",strainId=" + rs.getString("s.strainId")
                    + ",zygosity=" + rs.getString("s.zygosity");
            if (rs.getLong("s_statuscode_pk") != 0) {
                retVal += ",sc.dateOfStatuscode=" + (rs.getDate("sc.dateOfStatuscode") == null ? "<null>" : rs.getDate("sc.dateOfStatuscode"))
                        + ",sc.value=" + rs.getString("sc.value");
            }
            retVal += (rs.getDate("m.DOB") == null ? " (EMBRYO)" : " (MOUSE)");
        }

        return retVal;
    }

    @Deprecated
    public void truncateExperimentTables(Connection connection) throws SQLException {
        String query;
        PreparedStatement ps;

        String[] tables = new String[] {
                  "experiment"
                , "experiment_statuscode"
                , "experiment_specimen"
                , "housing"
                , "line"
                , "procedure_"
                , "center_procedure"
                , "line_statuscode"
                , "simpleParameter"
                , "ontologyParameter"
                , "seriesParameter"
                , "mediaParameter"
                , "ontologyParameterTerm"
                , "seriesParameterValue"
                , "mediaParameter_parameterAssociation"
                , "mediaParameter_procedureMetadata"
                , "parameterAssociation"
                , "procedureMetadata"
                , "dimension"
                , "mediaSampleParameter"
                , "mediaSample"
                , "mediaSection"
                , "mediaFile"
                , "mediaFile_parameterAssociation"
                , "mediaFile_procedureMetadata"
                , "seriesMediaParameter"
                , "procedure_procedureMetadata"
                , "seriesMediaParameterValue"
                , "seriesMediaParameterValue_parameterAssociation"
                , "seriesMediaParameterValue_procedureMetadata"
        };

        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
        ps.execute();
        for (String tableName : tables) {
            query = "TRUNCATE " + tableName + ";";

            try {
                ps = connection.prepareStatement(query);
                ps.execute();
            } catch (SQLException e) {
                logger.error("Unable to truncate table " + tableName);
                throw e;
            }
        }
        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1");
        ps.execute();
    }

    @Deprecated
    public void truncateSpecimenTables(Connection connection) throws SQLException {
        String query;
        PreparedStatement ps;

        String[] tables = new String[] {
                  "center"
                , "center_specimen"
                , "embryo"
                , "genotype"
                , "mouse"
                , "parentalStrain"
                , "relatedSpecimen"
                , "specimen"
                , "statuscode"
        };

        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
        ps.execute();
        for (String tableName : tables) {
            query = "TRUNCATE " + tableName + ";";

            try {
                ps = connection.prepareStatement(query);
                ps.execute();
            } catch (SQLException e) {
                logger.error("Unable to truncate table " + tableName);
                throw e;
            }
        }
        ps = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1");
        ps.execute();
    }


    // GETS


    /**
     * Returns the primary key for the given centerId, pipeline, and project, if found; 0 otherwise
     * NOTE: This method caches the center for faster returns.
     *
     * @param centerId   The center id
     * @param pipeline   The pipeline
     * @param project    The Project
     *
     * @return The center primary key if found; 0 otherwise.
     */
    public long getCenterPk(String centerId, String pipeline, String project) {
        long centerPk = 0L;
        String query = "SELECT pk FROM center WHERE centerId = :centerId AND pipeline = :pipeline AND project = :project";

        Map<String, String> parameterMap = new HashMap<>();

        parameterMap.put("centerId", centerId);
        parameterMap.put("pipeline", pipeline);
        parameterMap.put("project", project);

        if (centerPkMap.containsKey(parameterMap)) {
            return centerPkMap.get(parameterMap);
        }

        try {
            centerPk = loadUtils.queryForPk(npJdbcTemplate, query, parameterMap);
           if (centerPk > 0) {
               centerPkMap.put(parameterMap, centerPk);
           }
        } catch (Exception e) {

        }

        return centerPk;
    }

    public long getCenter_specimenPk(long centerPk, long specimenPk) {
        long center_specimenPk = 0L;
        String query = "SELECT pk FROM center_specimen WHERE center_pk = :centerPk AND specimen_pk = :specimenPk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("centerPk", centerPk);
        parameterMap.put("specimenPk", specimenPk);

        try {
            center_specimenPk = loadUtils.queryForPk(npJdbcTemplate, query, parameterMap);
        } catch (Exception e) {

        }

        return center_specimenPk;
    }

    /**
     *
     * @param centerPk
     * @return the centerId for the given center primary key
     */
    public String getCenterId(long centerPk) {
        String centerId;
        String query = "SELECT centerId FROM center WHERE pk = :centerPk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("centerPk", centerPk);

        centerId = npJdbcTemplate.queryForObject(query, parameterMap, String.class);

        return centerId;
    }

    /**
     *
     * @return a list of all centerIds
     */
    public List<String> getCenterIds() {
        String query = "SELECT DISTINCT centerId FROM center ORDER BY centerId";

         List<String> centerIds = npJdbcTemplate.queryForList(query, new HashMap<>(), String.class);

        return centerIds;
    }

    public String getDbName() {
        String query = "SELECT DATABASE()";

        String dbname = npJdbcTemplate.queryForObject(query, new HashMap<>(), String.class);

        return dbname;
    }

    /**
     * Returns a {@link List} of {@link Dimension} instances associated with parameter association primary key. If
     * there are no associations, an empty list is returned.
     *
     * @param parameterAssociationPk the parameter association primary key
     * @return
     */
    public List<Dimension> getDimensions(long parameterAssociationPk) {
        List<Dimension> dimensions = new ArrayList<>();

        String query = "SELECT * FROM dimension WHERE parameterAssociation_pk = :parameterAssociationPk";
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("parameterAssociationPk", parameterAssociationPk);
        dimensions = npJdbcTemplate.query(query, parameterMap, new DimensionRowMapper());

        return dimensions;
    }

    /**
     * Returns the line primary key matching {@code colonyId} and {@code center_procedurePk}, if found; 0 otherwise.
     *
     * @param colonyId The {@link StatusCode} value to search for
     * @param center_procedurePk The center_procedure primary key value to search for
     * @return The line primary key matching {@code colonyId} and {@code center_procedurePk}, if found; 0 otherwise.
     */
    public long getLinePk(String colonyId, long center_procedurePk) {
        long pk = 0L;

        if (colonyId == null)
            return 0L;

        final String query = "SELECT pk FROM line WHERE colonyId = :colonyId AND center_procedure_pk = :center_procedurePk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("colonyId", colonyId);
        parameterMap.put("center_procedurePk", center_procedurePk);
        pk = loadUtils.queryForPk(npJdbcTemplate, query, parameterMap);

        return pk;
    }

    /**
     * Returns the matching line_statuscode primary key, if any; 0 otherwise.
     *
     * @param line_pk The line primary key value to search for
     * @param statuscode_pk The statuscode primary key value to search for
     *
     * @return The matching line_statuscode primary key, if any; 0 otherwise.
     */
    public long getLineStatuscodePk(long line_pk, long statuscode_pk) {
        long pk = 0L;

        final String query = "SELECT pk FROM line_statuscode WHERE line_pk = :line_pk AND statuscode_pk = :statuscode_pk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("line_pk", line_pk);
        parameterMap.put("statuscode_pk", statuscode_pk);
        pk = loadUtils.queryForPk(npJdbcTemplate, query, parameterMap);

        return pk;
    }

    /**
     * Returns the <code>ParameterAssociation</code> matching <code>parameterId</code> and <code>sequenceId</code>, and
     * the {@link Dimension} collection, if found; null otherwise.
     *
     * @param parameterId The parameterId value to search for
     * @param sequenceId An optional sequence Id to search for. May be null.
     * @return The <code>ParameterAssociation</code> matching <code>parameterId</code> and, if not null,
     * <code>sequenceId</code>, if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     * <i>NOTE: The <code>Dimension</code> collection, if any, is returned as well.</i>
     */
    public ParameterAssociation getParameterAssociation(String parameterId, Integer sequenceId) {
        ParameterAssociation parameterAssociation = null;

        if (parameterId == null)
            return parameterAssociation;

        String query;
        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("parameterId", parameterId);
        if (sequenceId == null) {
            query = "SELECT * FROM parameterAssociation WHERE parameterId = :parameterId";
        } else {
            query = "SELECT * FROM parameterAssociation WHERE parameterId = :parameterId AND sequenceId = :sequenceId";
            parameterMap.put("sequenceId", sequenceId);
        }

        List<ParameterAssociation> parameterAssociations =
                npJdbcTemplate.query(query, parameterMap, new ParameterAssociationRowMapper());

        if ( ! parameterAssociations.isEmpty()) {
            parameterAssociation = parameterAssociations.get(0);
            if (parameterAssociation.getDim() != null) {
                parameterAssociation.setDim(getDimensions(parameterAssociation.getHjid()));
            }
        }

        for (ParameterAssociation pa : parameterAssociations) {
            pa.setDim(getDimensions(pa.getHjid()));
        }

        return parameterAssociation;
    }


    private class MediaParameterEx {
        private long procedure_pk;
        private MediaParameter mediaParameter;

        public long getProcedure_pk() {
            return procedure_pk;
        }

        public void setProcedure_pk(long procedure_pk) {
            this.procedure_pk = procedure_pk;
        }

        public MediaParameter getMediaParameter() {
            return mediaParameter;
        }

        public void setMediaParameter(MediaParameter mediaParameter) {
            this.mediaParameter = mediaParameter;
        }
    }
    private class MediaParameterExRowMapper implements RowMapper<MediaParameterEx> {

        @Override
        public MediaParameterEx mapRow(ResultSet rs, int rowNum) throws SQLException {
            MediaParameterEx mediaParameterEx = new MediaParameterEx();

            mediaParameterEx.setProcedure_pk(rs.getLong("procedure_pk"));
            mediaParameterEx.setMediaParameter(new MediaParameterRowMapper().mapRow(rs, rowNum));

            return mediaParameterEx;
        }
    }

    public synchronized Map<Long, List<MediaParameter>> getMediaParameters() {
        Map<Long, List<MediaParameter>> retVal = new HashMap<>();

        final String query =
                "SELECT * FROM mediaParameter";

        Map<String, Object> parameterMap = new HashMap<>();

        List<MediaParameterEx> list = npJdbcTemplate.query(query, parameterMap, new MediaParameterExRowMapper());
        for (MediaParameterEx mpEx : list) {
            List<MediaParameter> mpList = retVal.get(mpEx.getProcedure_pk());
            if (mpList == null) {
                mpList = new ArrayList<>();
                retVal.put(mpEx.getProcedure_pk(), mpList);
            }
            mpList.add(mpEx.getMediaParameter());
        }

        return retVal;
    }

    private class MediaSampleParameterEx {
        private long procedure_pk;
        private MediaSampleParameter mediaSampleParameter;

        public long getProcedure_pk() {
            return procedure_pk;
        }

        public void setProcedure_pk(long procedure_pk) {
            this.procedure_pk = procedure_pk;
        }

        public MediaSampleParameter getMediaSampleParameter() {
            return mediaSampleParameter;
        }

        public void setMediaSampleParameter(MediaSampleParameter mediaSampleParameter) {
            this.mediaSampleParameter = mediaSampleParameter;
        }
    }
    private class MediaSampleParameterExRowMapper implements RowMapper<MediaSampleParameterEx> {

        @Override
        public MediaSampleParameterEx mapRow(ResultSet rs, int rowNum) throws SQLException {
            MediaSampleParameterEx mediaSampleParameterEx = new MediaSampleParameterEx();

            mediaSampleParameterEx.setProcedure_pk(rs.getLong("procedure_pk"));
            mediaSampleParameterEx.setMediaSampleParameter(new MediaSampleParameterRowMapper().mapRow(rs, rowNum));

            return mediaSampleParameterEx;
        }
    }

    public synchronized Map<Long, List<MediaSampleParameter>> getMediaSampleParameters() {
        Map<Long, List<MediaSampleParameter>> retVal = new HashMap<>();

        final String query =
                "SELECT * FROM mediaSampleParameter";

        Map<String, Object> parameterMap = new HashMap<>();

        List<MediaSampleParameterEx> list = npJdbcTemplate.query(query, parameterMap, new MediaSampleParameterExRowMapper());
        for (MediaSampleParameterEx mpEx : list) {
            List<MediaSampleParameter> mpList = retVal.get(mpEx.getProcedure_pk());
            if (mpList == null) {
                mpList = new ArrayList<>();
                retVal.put(mpEx.getProcedure_pk(), mpList);
            }
            mpList.add(mpEx.getMediaSampleParameter());
        }

        return retVal;
    }

    private class OntologyParameterEx {
        private long procedure_pk;
        private OntologyParameter ontologyParameter;

        public long getProcedure_pk() {
            return procedure_pk;
        }

        public void setProcedure_pk(long procedure_pk) {
            this.procedure_pk = procedure_pk;
        }

        public OntologyParameter getOntologyParameter() {
            return ontologyParameter;
        }

        public void setOntologyParameter(OntologyParameter ontologyParameter) {
            this.ontologyParameter = ontologyParameter;
        }
    }
    private class OntologyParameterExRowMapper implements RowMapper<OntologyParameterEx> {

        @Override
        public OntologyParameterEx mapRow(ResultSet rs, int rowNum) throws SQLException {
            OntologyParameterEx ontologyParameterEx = new OntologyParameterEx();

            ontologyParameterEx.setProcedure_pk(rs.getLong("procedure_pk"));
            ontologyParameterEx.setOntologyParameter(new OntologyParameterRowMapper().mapRow(rs, rowNum));

            return ontologyParameterEx;
        }
    }

    // The key is the OntologyParameter_pk
    public Map<Long, List<String>> getOntologyParameterTerms() {
        Map<Long, List<String>> retVal = new HashMap<>();

        final String termQuery =
                "SELECT ontologyParameter_pk, term FROM ontologyParameterTerm";

        Map<String, Object> parameterMap = new HashMap<>();

        List<Map<String, Object>> ontologyTerms = npJdbcTemplate.queryForList(termQuery, parameterMap);

        for (Map<String, Object> ontologyTerm : ontologyTerms) {
            String pkString = ontologyTerm.get("ontologyParameter_pk").toString();
            Long pk = (pkString == null ? null : Long.valueOf(pkString));
            String term = (String)ontologyTerm.get("term");

            if ( ! retVal.containsKey(pk)) {
                retVal.put(pk, new ArrayList<>());
            }

            retVal.get(pk).add(term);

        }

        return retVal;
    }

    public synchronized Map<Long, List<OntologyParameter>> getOntologyParameters() {
        Map<Long, List<OntologyParameter>> retVal = new HashMap<>();

        final String query = "SELECT * FROM ontologyParameter";

        Map<Long, List<String>> ontologyParametersMap = getOntologyParameterTerms();
        Map<String, Object> parameterMap = new HashMap<>();

        List<OntologyParameterEx> list = npJdbcTemplate.query(query, parameterMap, new OntologyParameterExRowMapper());
        for (OntologyParameterEx mpEx : list) {
            List<OntologyParameter> mpList = retVal.get(mpEx.getProcedure_pk());
            if (mpList == null) {
                mpList = new ArrayList<>();
                retVal.put(mpEx.getProcedure_pk(), mpList);
            }

            OntologyParameter op = mpEx.getOntologyParameter();

            List<String> ontologyTerms = ontologyParametersMap.get(op.getHjid());

            Pattern pattern = Pattern.compile("(.*?:\\d+).*");

            ontologyTerms = ontologyTerms
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(StringUtils::isNotEmpty)
                    .map(x -> {
                        Matcher matcher = pattern.matcher(x);
                        matcher.find();
                        return matcher.group(1);
                    })
                    .collect(Collectors.toList());
            op.setTerm(ontologyTerms);

            mpList.add(op);
        }

        return retVal;
    }

    private class SeriesMediaParameterEx {
        private long procedure_pk;
        private SeriesMediaParameter seriesMediaParameter;

        public long getProcedure_pk() {
            return procedure_pk;
        }

        public void setProcedure_pk(long procedure_pk) {
            this.procedure_pk = procedure_pk;
        }

        public SeriesMediaParameter getSeriesMediaParameter() {
            return seriesMediaParameter;
        }

        public void setSeriesMediaParameter(SeriesMediaParameter seriesMediaParameter) {
            this.seriesMediaParameter = seriesMediaParameter;
        }
    }
    private class SeriesMediaParameterExRowMapper implements RowMapper<SeriesMediaParameterEx> {

        @Override
        public SeriesMediaParameterEx mapRow(ResultSet rs, int rowNum) throws SQLException {
            SeriesMediaParameterEx seriesMediaParameterEx = new SeriesMediaParameterEx();

            seriesMediaParameterEx.setProcedure_pk(rs.getLong("procedure_pk"));
            seriesMediaParameterEx.setSeriesMediaParameter(new SeriesMediaParameterRowMapper().mapRow(rs, rowNum));

            return seriesMediaParameterEx;
        }
    }

    public synchronized Map<Long, List<SeriesMediaParameter>> getSeriesMediaParameters() {
        Map<Long, List<SeriesMediaParameter>> retVal = new HashMap<>();

        final String query =
                "SELECT * FROM seriesMediaParameter";

        Map<String, Object> parameterMap = new HashMap<>();

        List<SeriesMediaParameterEx> list = npJdbcTemplate.query(query, parameterMap, new SeriesMediaParameterExRowMapper());
        for (SeriesMediaParameterEx mpEx : list) {
            List<SeriesMediaParameter> mpList = retVal.get(mpEx.getProcedure_pk());
            if (mpList == null) {
                mpList = new ArrayList<>();
                retVal.put(mpEx.getProcedure_pk(), mpList);
            }
            mpList.add(mpEx.getSeriesMediaParameter());
        }

        return retVal;
    }

    public List<SeriesMediaParameterValue> getSeriesMediaParameterValues(long seriesMediaParameter_pk) {

        final String query =
                "SELECT\n" +
                        "  pk,\n" +
                        "  fileType,\n" +
                        "  incrementValue,\n" +
                        "  URI, link, seriesMediaParameter_pk\n" +
                        "FROM seriesMediaParameterValue\n" +
                        "WHERE seriesMediaParameter_pk = :seriesMediaParameter_pk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("seriesMediaParameter_pk", seriesMediaParameter_pk);

        List<SeriesMediaParameterValue> list = npJdbcTemplate.query(query, parameterMap, new SeriesMediaParameterValueRowMapper());

        return list;
    }

    /**
     * Series Media Parameter Value associations
     */
    public List<ParameterAssociation> getSeriesMediaParameterValueParameterAssociations(long seriesMediaParameterValuepk) {
        String query = "SELECT pa.* " +
                "FROM seriesMediaParameterValue_parameterAssociation smpvpa " +
                "JOIN parameterAssociation pa ON pa.pk = smpvpa.parameterAssociation_pk " +
                "where smpvpa.seriesMediaParameterValue_pk = :pk " ;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pk", seriesMediaParameterValuepk);

        return npJdbcTemplate.query(query, parameterMap, new ParameterAssociationRowMapper());
    }


    public List<ProcedureMetadata> getSeriesMediaParameterValueProcedureMetadataAssociations(long seriesMediaParameterValuepk) {
        String query = "SELECT pm.* " +
                "FROM seriesMediaParameterValue_procedureMetadata smpvpm " +
                "JOIN procedureMetadata pm ON pm.pk = smpvpm.procedureMetadata_pk " +
                "WHERE smpvpm.seriesMediaParameterValue_pk = :pk " ;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pk", seriesMediaParameterValuepk);

        return npJdbcTemplate.query(query, parameterMap, new ProcedureMetadataRowMapper());
    }


    /**
     * Media file associations
     */
    public List<ParameterAssociation> getMediaFileParameterAssociations(long mediaFilepk) {
        String query = "SELECT pa.* " +
                "FROM mediaParameter_parameterAssociation mppa " +
                "JOIN parameterAssociation pa ON pa.pk = mppa.parameterAssociation_pk " +
                "where mppa.mediaFile_pk = :pk " ;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pk", mediaFilepk);

        return npJdbcTemplate.query(query, parameterMap, new ParameterAssociationRowMapper());
    }


    public List<ProcedureMetadata> getMediaFileProcedureMetadataAssociations(long mediaFilepk) {
        String query = "SELECT pm.* " +
                "FROM mediaFile_procedureMetadata mfpm " +
                "JOIN procedureMetadata pm ON pm.pk = mfpm.procedureMetadata_pk " +
                "WHERE mfpm.mediaFile_pk = :pk " ;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pk", mediaFilepk);

        return npJdbcTemplate.query(query, parameterMap, new ProcedureMetadataRowMapper());
    }


    /**
     * Media Parameter associations
     */
    public List<ParameterAssociation> getMediaParameterParameterAssociations(long mediaParameterpk) {
        String query = "SELECT pa.* " +
                "FROM mediaParameter_parameterAssociation mppa " +
                "JOIN parameterAssociation pa ON pa.pk = mppa.parameterAssociation_pk " +
                "where mppa.mediaParameter_pk = :pk " ;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pk", mediaParameterpk);

        return npJdbcTemplate.query(query, parameterMap, new ParameterAssociationRowMapper());
    }


    public List<ProcedureMetadata> getMediaParameterProcedureMetadataAssociations(long mediaParameterpk) {
        String query = "SELECT pm.* " +
                "FROM mediaParameter_procedureMetadata mppm " +
                "JOIN procedureMetadata pm ON pm.pk = mppm.procedureMetadata_pk " +
                "WHERE mppm.mediaParameter_pk = :pk " ;

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("pk", mediaParameterpk);

        return npJdbcTemplate.query(query, parameterMap, new ProcedureMetadataRowMapper());
    }



    private class SeriesParameterEx {
        private long procedure_pk;
        private SeriesParameter seriesParameter;

        public long getProcedure_pk() {
            return procedure_pk;
        }

        public void setProcedure_pk(long procedure_pk) {
            this.procedure_pk = procedure_pk;
        }

        public SeriesParameter getSeriesParameter() {
            return seriesParameter;
        }

        public void setSeriesParameter(SeriesParameter seriesParameter) {
            this.seriesParameter = seriesParameter;
        }
    }
    private class SeriesParameterExRowMapper implements RowMapper<SeriesParameterEx> {

        @Override
        public SeriesParameterEx mapRow(ResultSet rs, int rowNum) throws SQLException {
            SeriesParameterEx seriesParameterEx = new SeriesParameterEx();

            seriesParameterEx.setProcedure_pk(rs.getLong("procedure_pk"));
            seriesParameterEx.setSeriesParameter(new SeriesParameterRowMapper().mapRow(rs, rowNum));

            return seriesParameterEx;
        }
    }

    public synchronized Map<Long, List<SeriesParameter>> getSeriesParameters() {
        Map<Long, List<SeriesParameter>> retVal = new HashMap<>();

        final String query =
                "SELECT * FROM seriesParameter";

        Map<String, Object> parameterMap = new HashMap<>();

        List<SeriesParameterEx> list = npJdbcTemplate.query(query, parameterMap, new SeriesParameterExRowMapper());
        for (SeriesParameterEx mpEx : list) {
            List<SeriesParameter> mpList = retVal.get(mpEx.getProcedure_pk());
            if (mpList == null) {
                mpList = new ArrayList<>();
                retVal.put(mpEx.getProcedure_pk(), mpList);
            }
            mpList.add(mpEx.getSeriesParameter());
        }

        return retVal;
    }

    public List<SeriesParameterValue> getSeriesParameterValues(long seriesParameter_pk) {

        final String query =
                "SELECT\n" +
                        "  pk,\n" +
                        "  incrementStatus,\n" +
                        "  incrementValue,\n" +
                        "  value\n" +
                        "FROM seriesParameterValue\n" +
                        "WHERE seriesParameter_pk = :seriesParameter_pk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("seriesParameter_pk", seriesParameter_pk);

        List<SeriesParameterValue> list = npJdbcTemplate.query(query, parameterMap, new SeriesParameterValueRowMapper());

        return list;
    }

    public List<SimpleParameter> getSimpleParameters(long procedure_pk) {
        Map<Long, List<SimpleParameter>> retVal = new HashMap<>();

        final String query =
                "SELECT * FROM simpleParameter WHERE procedure_pk = :procedure_pk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("procedure_pk", procedure_pk);

        List<SimpleParameter> list = npJdbcTemplate.query(query, parameterMap, new SimpleParameterRowMapper());

        return list;
    }

    /**
     * Returns the {@code ProcedureMetadata} matching {@code parameterId} and {@code sequenceId} if found; null
     * otherwise.
     *
     * @param parameterId The parameterId value to search for
     * @param sequenceId An optional sequence Id to search for. May be null.
     * @return The <code>ProcedureMetadata</code> matching <code>parameterId</code> and, if not null, <code>sequenceId</code>,
     * if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     */
    public ProcedureMetadata getProcedureMetadata(String parameterId, BigInteger sequenceId) {
        ProcedureMetadata procedureMetadata = null;

        if (parameterId == null)
            return procedureMetadata;

        String query;
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("parameterId", parameterId);
        if (sequenceId == null) {
            query = "SELECT * FROM procedureMetadata WHERE parameterId = :parameterId";
        } else {
            query = "SELECT * FROM procedureMetadata WHERE parameterId = :parameterId AND sequenceId = :sequenceId";
            parameterMap.put("sequenceId", sequenceId);
        }

        List<ProcedureMetadata> procedureMetadataList =
                npJdbcTemplate.query(query, parameterMap, new ProcedureMetadataRowMapper());

        if ( ! procedureMetadataList.isEmpty()) {
            procedureMetadata = procedureMetadataList.get(0);
        }

        return procedureMetadata;
    }

    public Map<String, Procedure> getProcedures() {
        Map<String, Procedure> results = new HashMap<>();
        final String query = "SELECT * FROM procedure";

        List<Procedure> list = npJdbcTemplate.query(query, new HashMap<>(), new ProcedureRowMapper());
        for (Procedure procedure: list) {
            results.put(procedure.getProcedureID(), procedure);
        }

        return results;
    }

    // This is a container to hold the procedure_pk matching the associated ProcedureMetadata.
    public class ProcedureMetadataEx {
        private long              procedure_pk;
        private ProcedureMetadata procedureMetadata;

        public long getProcedure_pk() {
            return procedure_pk;
        }

        public void setProcedure_pk(long procedure_pk) {
            this.procedure_pk = procedure_pk;
        }

        public ProcedureMetadata getProcedureMetadata() {
            return procedureMetadata;
        }

        public void setProcedureMetadata(ProcedureMetadata procedureMetadata) {
            this.procedureMetadata = procedureMetadata;
        }
    }
    public class ProcedureMetadataExRowMapper implements RowMapper<ProcedureMetadataEx> {

        @Override
        public ProcedureMetadataEx mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProcedureMetadataEx procedureMetadataEx = new ProcedureMetadataEx();

            procedureMetadataEx.setProcedure_pk(rs.getLong("procedure_pk"));
            procedureMetadataEx.setProcedureMetadata(new ProcedureMetadataRowMapper().mapRow(rs, rowNum));

            return procedureMetadataEx;
        }
    }
    public Map<Long, List<ProcedureMetadata>> getProcedureMetadata() {
        Map<Long, List<ProcedureMetadata>> retVal = new HashMap<>();

        final String query =
                "SELECT\n" +
                "  ppm.procedure_pk,\n" +
                "  pm.*\n" +
                "FROM procedureMetadata pm\n" +
                "JOIN procedure_procedureMetadata ppm on ppm.procedureMetadata_pk = pm.pk";

        Map<String, Object> parameterMap = new HashMap<>();

        List<ProcedureMetadataEx> list = npJdbcTemplate.query(query, parameterMap, new ProcedureMetadataExRowMapper());
        for (ProcedureMetadataEx pmEx : list) {
            List<ProcedureMetadata> pmdList = retVal.get(pmEx.getProcedure_pk());
            if (pmdList == null) {
                pmdList = new ArrayList<>();
                retVal.put(pmEx.getProcedure_pk(), pmdList);
            }
            pmdList.add(pmEx.getProcedureMetadata());
        }

        return retVal;
    }

    public List<ProcedureMetadata> getProcedureMetadata(long dccProcedurePk) {

        final String query =
                "SELECT\n" +
                "  ppm.procedure_pk,\n" +
                "  pm.*\n" +
                "FROM procedureMetadata pm\n" +
                "JOIN procedure_procedureMetadata ppm on ppm.procedureMetadata_pk = pm.pk\n" +
                "WHERE ppm.procedure_pk = :dccProcedurePk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("dccProcedurePk", dccProcedurePk);

        List<ProcedureMetadata> list = npJdbcTemplate.query(query, parameterMap, new ProcedureMetadataRowMapper());

        return list;
    }

    /**
     * Returns all specimens from the specimen table.
     *
     * @return The {@link List} of {@link Specimen} instances
     */
    public List<SpecimenExtended> getSpecimens() {
        // The Specimen class is abstract. Instances are of either Mouse or Embryo. Select info for both. The RowMapper
        // will return a Specimen of type Mouse or Embryo with all mouse or embryo table columns filled out.
        final String query =
                "SELECT\n" +
                "  s.*\n" +
                ", CASE WHEN m.DOB IS NULL THEN 0 ELSE 1 END AS is_mouse\n" +
                ", e.stage\n" +
                ", e.stageUnit\n" +
                ", m.DOB\n" +
                "FROM specimen s\n" +
                "LEFT OUTER JOIN mouse m ON m.specimen_pk = s.pk\n" +
                "LEFT OUTER JOIN embryo e on e.specimen_pk = s.pk";

        Map<String, Object> parameterMap = new HashMap<>();

        List<SpecimenExtended> specimens = npJdbcTemplate.query(query, parameterMap, new SpecimenExtendedRowMapper());

        return specimens;
    }

    /**
     * Returns the status code matching {@code value}, if found; null otherwise.
     *
     * @param value The {@link StatusCode} value to search for
     * @return The {@link StatusCode} matching {@code value}, if found; null otherwise.
     * <p/>
     * <i>NOTE: If found, the primary key value is returned in Hjid.</i>
     */
    public StatusCode getStatuscode(String value) {
        StatusCode statuscode = null;

        if (value == null)
            return statuscode;

        final String query = "SELECT * FROM statuscode WHERE value = :value";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("value", value);

        List<StatusCode> statuscodes = npJdbcTemplate.query(query, parameterMap, new StatusCodeRowMapper());
        if ( ! statuscodes.isEmpty()) {
            statuscode = statuscodes.get(0);
        }

        return statuscode;
    }


    // INSERTS


    /**
     * Inserts the given {@code }centerId}, {@code }pipeline}, and {@code }project} into the center table. Duplicates
     * are ignored. The center primary key is returned.
     *
     * @param centerId   The center id
     * @param pipeline   The pipeline
     * @param project    The Project
     *
     * @return the primary key matching the newly inserted values
     */
    public long insertCenter(String centerId, String pipeline, String project) {
        long pk;
        String insert = "INSERT INTO center (centerId, pipeline, project) VALUES (:centerId, :pipeline, :project)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("centerId", centerId);
            parameterMap.put("pipeline", pipeline);
            parameterMap.put("project", project);

            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            npJdbcTemplate.update(insert, parameterSource, keyholder);
            pk = keyholder.getKey().longValue();

        } catch (DuplicateKeyException e) {
            pk = getCenterPk(centerId, pipeline, project);
        }

        return pk;
    }

    /**
     * Inserts the given {@code centerPk} and {@code specimenPk}, into the center_specimen table. Duplicates are ignored.
     * The center_specimen primary key is returned.
     *
     * @param centerPk   The center primary key
     * @param specimenPk The specimen primary key
     * @return the the center_specimen primary key
     */
    public long insertCenter_specimen(long centerPk, long specimenPk) {
        long pk;
        String insert = "INSERT INTO center_specimen (center_pk, specimen_pk) VALUES (:centerPk, :specimenPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("centerPk", centerPk);
            parameterMap.put("specimenPk", specimenPk);

            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            npJdbcTemplate.update(insert, parameterSource, keyholder);
            pk = keyholder.getKey().longValue();

        } catch (DuplicateKeyException e) {
            pk = getCenter_specimenPk(centerPk, specimenPk);
        }

        return pk;
    }

    /**
     * Inserts the given {@link Embryo} into the embryo table. Duplicates are ignored.
     *
     * @param embryo the embryo to be inserted
     *
     * @return count of records inserted
     */
    public int insertEmbryo(Embryo embryo, long specimenPk) {
        int count = 0;
        String insert = "INSERT INTO embryo (stage, stageUnit, specimen_pk) VALUES (:stage, :stageUnit, :specimenPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("stage", embryo.getStage());
            parameterMap.put("stageUnit", (embryo.getStageUnit() == null ? null : embryo.getStageUnit().value()));
            parameterMap.put("specimenPk", specimenPk);

            count = npJdbcTemplate.update(insert, parameterMap);

        } catch (DuplicateKeyException e) {

        }

        return count;
    }

    /**
     * Inserts the given {@link Genotype} into the genotype table. Duplicates are ignored.
     *
     * @param genotype the genotype to be inserted
     *
     * @return count of records inserted
     */
    public int insertGenotype(Genotype genotype, long specimenPk) {
        int count = 0;
        String insert = "INSERT INTO genotype (geneSymbol, mgiAlleleId, mgiGeneId, fatherZygosity, motherZygosity, specimen_pk) "
                     + " VALUES (:geneSymbol, :mgiAlleleId, :mgiGeneId, :fatherZygosity, :motherZygosity, :specimenPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("geneSymbol", genotype.getGeneSymbol());
            parameterMap.put("mgiAlleleId", genotype.getMGIAlleleId());
            parameterMap.put("mgiGeneId", genotype.getMGIGeneId());
            parameterMap.put("fatherZygosity", (genotype.getFatherZygosity() == null ? null : genotype.getFatherZygosity().value()));
            parameterMap.put("motherZygosity", (genotype.getMotherZygosity() == null ? null : genotype.getMotherZygosity().value()));
            parameterMap.put("specimenPk", specimenPk);

            count = npJdbcTemplate.update(insert, parameterMap);

        } catch (DuplicateKeyException e) {

        }

        return count;
    }

    /**
     * Inserts the given {@link Housing} into the {@code housing} table. Duplicates are ignored.
     *
     * @param housingList;   The list of {@link Housing} instances to be inserted
     * @param center_procedurePk   The center_procedure primary key
     */
    public void insertHousing(List<Housing> housingList, long center_procedurePk) {

        String insert = "INSERT INTO housing (fromLims, lastUpdated, center_procedure_pk) VALUES (:fromLims, :lastUpdated, :center_procedurePk)";

        for (Housing housing : housingList) {
            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("fromLims", (housing.isFromLIMS() ? 1 : 0));
                parameterMap.put("lastUpdated", (housing.getLastUpdated() == null ? null : new Date(housing.getLastUpdated().getTime().getTime())));
                parameterMap.put("center_procedurePk", center_procedurePk);

                npJdbcTemplate.update(insert, parameterMap);

            } catch (DuplicateKeyException e) {

            }
        }
    }

    /**
     * Inserts the given {@link Line} into the {@code line} table. Duplicates are ignored.
     *
     * @param line;   The {@link Line} instance to be inserted
     * @param datasourceShortName the data source short name (e.g. EuroPhenome, IMPC, 3I, etc.)
     * @param center_procedurePk   The center_procedure primary key
     *
     * @return returns the line primary key
     */
    public long insertLine(Line line, String datasourceShortName, long center_procedurePk) {
        long pk = 0L;
        String insert = "INSERT INTO line (colonyId, datasourceShortName, center_procedure_pk) VALUES (:colonyId, :datasourceShortName, :center_procedurePk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("colonyId", line.getColonyID());
            parameterMap.put("datasourceShortName", datasourceShortName);
            parameterMap.put("center_procedurePk", center_procedurePk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                pk = keyholder.getKey().longValue();
            }

        } catch (DuplicateKeyException e) {
            pk = getLinePk(line.getColonyID(), center_procedurePk);
        }

        return pk;
    }

    /**
     * Inserts the given {@code linePk} and {@code statuscodePk} into the line_statuscode table. Duplicates are ignored.
     *
     * @param linePk;   The line primary key
     * @param statuscodePk   The statuscode primary key
     *
     * @return returns the line_statuscode primary key
     */
    public long insertLine_statuscode(long linePk, long statuscodePk) {
        long pk = 0L;
        String insert = "INSERT INTO line_statuscode (line_pk, statuscode_pk) VALUES (:linePk, :statuscodePk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("linePk", linePk);
            parameterMap.put("statuscodePk", statuscodePk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                pk = keyholder.getKey().longValue();
            }

        } catch (DuplicateKeyException e) {
            pk = getLineStatuscodePk(linePk, statuscodePk);
        }

        return pk;
    }

    /**
     * Inserts the given {@link Mouse} into the mouse table. Duplicates are ignored.
     *
     * @param mouse the mouse to be inserted
     *
     * @return count of records inserted
     */
    public int insertMouse(Mouse mouse, long specimenPk) {
        int count = 0;
        String insert = "INSERT INTO mouse (DOB, specimen_pk) VALUES (:DOB, :specimenPk)";

        java.sql.Date DOB = null;
        try {
            DOB = new java.sql.Date(mouse.getDOB().getTime().getTime());
        } catch (Exception e) { }

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("DOB", DOB);
            parameterMap.put("specimenPk", specimenPk);

           count = npJdbcTemplate.update(insert, parameterMap);

        } catch (DuplicateKeyException e) {

        }

        return count;
    }

    /**
     * Inserts the given {@link MediaFile} into the mediaFile table. If the insert fails, an error is logged
     * and 0 is returned.
     *
     * @param mediaFile the mediaFile to be inserted
     * @param mediaSectionPk the mediaSection primary key
     *
     * @return the primary key if the insert was successful; 0 otherwise
     */
    public long insertMediaFile(MediaFile mediaFile, long mediaSectionPk) {
        String insert = "INSERT INTO mediaFile (localId, fileType, URI, link, mediaSection_pk) " +
                        "VALUES (:localId, :fileType, :URI, :link, :mediaSectionPk)";

        Map<String, Object> parameterMap = new HashMap<>();
        try {

            parameterMap.put("localId", mediaFile.getLocalId());
            parameterMap.put("fileType", mediaFile.getFileType());
            parameterMap.put("URI", mediaFile.getURI());
            parameterMap.put("link", mediaFile.getLink());
            parameterMap.put("mediaSectionPk", mediaSectionPk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (Exception e) {
            logger.error("INSERT to mediaFile failed for fileType {}, localId {}, link {}, URI {}, mediaSectionPk {}. Reason:\n\t{}",
                         mediaFile.getFileType(), mediaFile.getLocalId(), mediaFile.getLink(), mediaFile.getURI(), mediaSectionPk, e.getLocalizedMessage());
        }

        return 0;
    }

    /**
     * Inserts the given {@code mediaFilePk} and {@code parameterAssociationPk} into the mediaFile_parameterAssociation
     * table. Duplicates are ignored.
     *
     * @param mediaFilePk the mediaFile primary key to be inserted
     * @param parameterAssociationPk the parameterAssociation primary key to be inserted
     */
    public void insertMediaFile_parameterAssociation(long mediaFilePk, long parameterAssociationPk) {

        String insert = "INSERT INTO mediaFile_parameterAssociation(mediaFile_pk, parameterAssociation_pk) " +
                        "VALUES (:mediaFilePk, :parameterAssociationPk)";

        Map<String, Object> parameterMap = new HashMap<>();
        try {
            parameterMap.put("mediaFilePk", mediaFilePk);
            parameterMap.put("parameterAssociationPk", parameterAssociationPk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            npJdbcTemplate.update(insert, parameterSource, keyholder);

        } catch (DuplicateKeyException e) {

        }
    }

    /**
     * Inserts the given {@code mediaFilePk} and {@code procedureMetadataPk} into the mediaFile_procedureMetadata
     * table. Duplicates are ignored.
     *
     * @param mediaFilePk the mediaFile primary key to be inserted
     * @param procedureMetadataPk the procedureMetadata primary key to be inserted
     *
     * @return the mediaFile_procedureMetadata primary key
     */
    public long insertMediaFile_procedureMetadata(long mediaFilePk, long procedureMetadataPk) {
        long pk = 0L;
        String insert = "INSERT INTO mediaFile_procedureMetadata(mediaFile_pk, procedureMetadata_pk) " +
                        "VALUES (:mediaFilePk, :procedureMetadataPk)";

        Map<String, Object> parameterMap = new HashMap<>();
        try {
            parameterMap.put("mediaFilePk", mediaFilePk);
            parameterMap.put("procedureMetadataPk", procedureMetadataPk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                pk = keyholder.getKey().longValue();
            }

        } catch (DuplicateKeyException e) {
            String query = "SELECT pk FROM mediaFile_procedureMetadata WHERE mediaFile_pk = :mediaFilePk AND procedureMetadata_pk = :procedureMetadataPk";
            pk = npJdbcTemplate.queryForObject(query, parameterMap, Long.class);
        }

        return pk;
    }

    /**
     * Inserts the given {@link MediaParameter} into the mediaParameter table. If the insert fails, an error is logged
     * and 0 is returned.
     *
     * @param mediaParameter the mediaParameter to be inserted
     *
     * @return the primary key if the insert was successful; 0 otherwise
     */
    public long insertMediaParameter(MediaParameter mediaParameter, long procedurePk) {
        String insert = "INSERT INTO mediaParameter (parameterId, parameterStatus, filetype, URI, procedure_pk) " +
                        "VALUES (:parameterId, :parameterStatus, :filetype, :URI, :procedurePk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("parameterId", mediaParameter.getParameterID());
            parameterMap.put("parameterStatus", mediaParameter.getParameterStatus());
            parameterMap.put("filetype", mediaParameter.getFileType());
            parameterMap.put("URI", mediaParameter.getURI());
            parameterMap.put("procedurePk", procedurePk);

            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (Exception e) {
            logger.error("INSERT to mediaParameter failed for arameterId {}, procedurePk {}. Reason:\n\t{}",
                         mediaParameter.getParameterID(), procedurePk, e.getLocalizedMessage());
        }

        return 0;
    }

    /**
     * Inserts the given {@code mediaParameterPk} and {@code parameterAssociationPk} into the
     * mediaParameter_parameterAssociation table. If the insert fails, an error is logged.
     *
     * @param mediaParameterPk;   The media parameter primary key
     * @param parameterAssociationPk   The parameterAssociation primary key
     */
    public void insertMediaParameter_parameterAssociation(long mediaParameterPk, long parameterAssociationPk) {

        String insert = "INSERT INTO mediaParameter_parameterAssociation(mediaParameter_pk, parameterAssociation_pk) " +
                        "VALUES (:mediaParameterPk, :parameterAssociationPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("mediaParameterPk", mediaParameterPk);
            parameterMap.put("parameterAssociationPk", parameterAssociationPk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count == 0) {
                logger.error("INSERT to mediaParameter_parameterAssociation failed for mediaParameterPk {}, parameterAssociationPk {}.",
                             mediaParameterPk, parameterAssociationPk);
            }

        } catch (Exception e) {
            logger.error("INSERT to mediaParameter_parameterAssociation failed for mediaParameterPk {}, parameterAssociationPk {}. Reason:\n\t{}",
                         mediaParameterPk, parameterAssociationPk, e.getLocalizedMessage());
        }
    }

    /**
     * Inserts the given {@code mediaParameterPk} and {@code procedureMetadataPk} into the
     * mediaParameter_procedureMetadata table. If the insert fails, an error is logged
     *
     * @param mediaParameterPk    The media parameter primary key
     * @param procedureMetadataPk The procedureMetadata primary key
     */
    public void insertMediaParameter_procedureMetadata(long mediaParameterPk, long procedureMetadataPk) {

        String insert = "INSERT INTO mediaParameter_procedureMetadata(mediaParameter_pk, procedureMetadata_pk) " +
                        "VALUES (:mediaParameterPk, :procedureMetadataPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("mediaParameterPk", mediaParameterPk);
            parameterMap.put("procedureMetadataPk", procedureMetadataPk);

            npJdbcTemplate.update(insert, parameterMap);

        } catch (Exception e) {
            logger.error("INSERT to mediaParameter_procedureMetadata failed for mediaParameterPk {}, procedureMetadataPk {}. Reason:\n\t{}",
                         mediaParameterPk, procedureMetadataPk, e.getLocalizedMessage());
        }
    }

    /**
     * Inserts the given {@link MediaSample} into the mediaSample table. If insert fails, an error is logged and
     * 0 is returned.
     *
     * @param mediaSample the mediaSample to be inserted
     *
     * @return the primary key if the insert was successful; 0 otherwise
     */
    public long insertMediaSample(MediaSample mediaSample, long mediaSampleParameterPk) {
        String insert = "INSERT INTO mediaSample (localId, mediaSampleParameter_pk) " +
                        "VALUES (:localId, :mediaSampleParameterPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("localId", mediaSample.getLocalId());
            parameterMap.put("mediaSampleParameterPk", mediaSampleParameterPk);

            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (Exception e) {
            logger.error("INSERT to mediaSample failed for localId {}, mediaSampleParameterPk {}. Reason:\n\t{}",
                         mediaSample.getLocalId(), mediaSampleParameterPk, e.getLocalizedMessage());
        }

        return 0;
    }

    /**
     * Inserts the given {@link MediaSampleParameter} into the mediaSampleParameter table.  If the insert fails, an
     * error is logged and 0 is returned.
     *
     * @param mediaSampleParameter the mediaSampleParameter to be inserted
     *
     * @return the mediaSampleParameter, with primary key loaded
     */
    public long insertMediaSampleParameter(MediaSampleParameter mediaSampleParameter, long procedurePk) {
        String insert = "INSERT INTO mediaSampleParameter (parameterId, parameterStatus, procedure_pk) " +
                        "VALUES (:parameterId, :parameterStatus, :procedurePk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("parameterId", mediaSampleParameter.getParameterID());
            parameterMap.put("parameterStatus", mediaSampleParameter.getParameterStatus());
            parameterMap.put("procedurePk", procedurePk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (Exception e) {
            logger.error("INSERT to mediaSampleParameter failed for parameterId {}, procedurePk {}. Reason:\n\t{}",
                         mediaSampleParameter.getParameterID(), procedurePk, e.getLocalizedMessage());
        }

        return 0;
    }

    /**
     * Inserts the given {@link MediaSection} into the mediaSection table. If the insert fails, an error is logged
     * and 0 is returned.
     *
     * @param mediaSection the mediaSection to be inserted
     * @param mediaSamplePk the mediaSample primary key
     *
     * @return the primary key if the insert was successful; 0 otherwise
     */
    public long insertMediaSection(MediaSection mediaSection, long mediaSamplePk) {
        String insert = "INSERT INTO mediaSection (localId, mediaSample_pk) " +
                        "VALUES (:localId, :mediaSamplePk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("localId", mediaSection.getLocalId());
            parameterMap.put("mediaSamplePk", mediaSamplePk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (Exception e) {
            logger.error("INSERT to mediaSection failed for localId {}, mediaSamplePk {}. Reason:\n\t{}",
                         mediaSection.getLocalId(), mediaSamplePk, e.getLocalizedMessage());
        }

        return 0;
    }

    /**
     * Inserts the given {@link OntologyParameter} into the ontologyParameter table.  If the insert fails, an error is
     * logged and 0 is returned.
     *
     * @param ontologyParameter the ontology parameter to be inserted
     * @param procedurePk the procedure primary key
     *
     * @return the primary key if the insert was successful; 0 otherwise
     */
    public long insertOntologyParameter(OntologyParameter ontologyParameter, long procedurePk) {
        String insert = "INSERT INTO ontologyParameter (parameterId, parameterStatus, sequenceId, procedure_pk) "
                      + "VALUES (:parameterId, :parameterStatus, :sequenceId, :procedurePk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("parameterId", ontologyParameter.getParameterID());
            parameterMap.put("parameterStatus", ontologyParameter.getParameterStatus());
            parameterMap.put("sequenceId", ontologyParameter.getSequenceID());
            parameterMap.put("procedurePk", procedurePk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (Exception e) {
            logger.error("INSERT to ontologyParameter failed for ontologyParameterId {}, procedurePk {}. Reason:\n\t{}",
                         ontologyParameter.getParameterID(), procedurePk, e.getLocalizedMessage());
        }

        return 0;
    }

    /**
     * Inserts the given {@code ontologyParameterTerm} into the ontologyParameterTerm table. If the insert fails, an
     * error is logged.
     *
     * @param ontologyParameterTerm the ontology parameter term to be inserted
     * @param ontologyParameterPk the ontology parameter primary key
     * @param ontologyParameter the ontology parameter instance (for logging purposes only)
     * @param filename the data filename currently being loaded
     *
     *
     * @return the ontologyParameterTerm primary key
     */
    public long insertOntologyParameterTerm(String ontologyParameterTerm, long ontologyParameterPk, OntologyParameter ontologyParameter, String filename) {
        String insert = "INSERT INTO ontologyParameterTerm(term, ontologyParameter_pk) "
                      + "VALUES (:term, :ontologyParameterPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("term", ontologyParameterTerm);
            parameterMap.put("ontologyParameterPk", ontologyParameterPk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (DuplicateKeyException dke) {
            logger.debug("IGNORED DUPLICATE INSERT to ontologyParameterTerm for ontologyParameterTerm {}, ontologyParameterPk {}, parameterId {}, filename {}",
                         ontologyParameterTerm, ontologyParameterPk, ontologyParameter.getParameterID(), filename);
        }

        return 0;
    }

    /**
     * Inserts the given {link ParentalStrain} into the parentalStrain table. Duplicates are ignored.
     *
     * @param parentalStrain the parentalStrain to be inserted
     * @param specimenPk the specimen primary key
     *
     * @return the parentalStrain primary key if the insert was successful; 0 otherwise
     */
    public long insertParentalStrain(ParentalStrain parentalStrain, long specimenPk) {
        long pk;
        String insert = "INSERT INTO parentalStrain (percentage, mgiStrainId, gender, level, specimen_pk) "
                      + "VALUES (:percentage, :mgiStrainId, :gender, :level, :specimenPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("percentage", parentalStrain.getPercentage());
            parameterMap.put("mgiStrainId", parentalStrain.getMGIStrainID());
            parameterMap.put("gender", (parentalStrain.getGender() == null ? null : parentalStrain.getGender().value()));
            parameterMap.put("level", parentalStrain.getLevel());
            parameterMap.put("specimenPk", specimenPk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (DuplicateKeyException e) {

        }

        return 0;
    }

    /**
     * Inserts the given {@code procedureId} into the procedure_ table. If the insert fails, an error is logged and
     * 0 is returned.
     *
     * @param procedureId the procedure id to be inserted
     *
     * @return the procedure primary key if the insert was successful; 0 otherwise
     */
    public long insertProcedure(String procedureId) {
        long pk;

        String insert = "INSERT INTO procedure_ (procedureId) VALUES (:procedureId)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("procedureId", procedureId);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("INSERT to procedure_ failed for procedureId {}. Reason:\n\t{}",
                         procedureId, e.getLocalizedMessage());
        }

        return 0;
    }

    /**
     * Inserts the given {@code procedurePk} and {@code procedureMetadataPk} into the procedure_procedureMetadata
     * table. If the insert fails, an error is logged.
     *
     * @param procedurePk the procedure primary key to be inserted
     * @param procedureMetadataPk the procedureMetadata primary key to be inserted
     */
    public void insertProcedure_procedureMetadata(long procedurePk, long procedureMetadataPk) {

        String insert = "INSERT INTO procedure_procedureMetadata(procedure_pk, procedureMetadata_pk) VALUES (:procedurePk, :procedureMetadataPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("procedurePk", procedurePk);
            parameterMap.put("procedureMetadataPk", procedureMetadataPk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count == 0) {
                logger.error("INSERT to procedure_procedureMetadata failed for procedurePk {}, procedureMetadataPk {}.",
                             procedurePk, procedureMetadataPk);
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("INSERT to procedure_procedureMetadata failed for procedurePk {}, procedureMetadataPk {}. Reason:\n\t{}",
                         procedurePk, procedureMetadataPk, e.getLocalizedMessage());
        }
    }

    /**
     * Inserts the given {@link RelatedSpecimen} into the relatedSpecimen table. Duplicates are ignored.
     *
     * @param centerId the center id
     * @param specimenPk the specimen id primary key
     * @param specimenId the specimen id
     * @param relationship the relationship of the related specimen id to the specimen id
     * @param relatedSpecimenId the related specimen id
     *
     * @return the number of rows inserted
     */
    public int insertRelatedSpecimen(String centerId, long specimenPk, String specimenId, String relationship, String relatedSpecimenId) {
        int count;
        String insert = "INSERT INTO relatedSpecimen (centerId, specimenIdPk, specimenId, relationship, relatedSpecimenId) "
                      + "VALUES (:centerId, :specimenIdPk, :specimenId, :relationship, :relatedSpecimenId)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("centerId", centerId);
            parameterMap.put("specimenIdPk", specimenPk);
            parameterMap.put("specimenId", specimenId);
            parameterMap.put("relationship", relationship);
            parameterMap.put("relatedSpecimenId", relatedSpecimenId);

            count = npJdbcTemplate.update(insert, parameterMap);


        } catch (DuplicateKeyException e) {
            count = 0;
        }

        return count;
    }

    /**
     * Inserts the given {@link SeriesMediaParameter} into the seriesMediaParameter table. If the insert fails, an error
     * is logged and 0 is returned.
     *
     * @param seriesMediaParameter the seriesMediaParameter to be inserted
     * @param procedurePk the procedure primary key
     *
     * @return the primary key if the insert was successful; 0 otherwise
     */
    public long insertSeriesMediaParameter(SeriesMediaParameter seriesMediaParameter, long procedurePk) {
        String insert = "INSERT INTO seriesMediaParameter (parameterId, parameterStatus, procedure_pk) "
                      + "VALUES (:parameterId, :parameterStatus, :procedurePk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("parameterId", seriesMediaParameter.getParameterID());
            parameterMap.put("parameterStatus", seriesMediaParameter.getParameterStatus());
            parameterMap.put("procedurePk", procedurePk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (Exception e) {
            logger.error("INSERT to seriesMediaParameter failed for seriesMediaParameter {}, procedurePk {}. Reason:\n\t{}",
                         seriesMediaParameter.getParameterID(), procedurePk, e.getLocalizedMessage());
        }

        return 0;
    }
    
    /**
     * Inserts the given {@link SeriesMediaParameterValue} into the seriesMediaParameterValue table. If the insert fails,
     * an error is logged and 0 is returned.
     *
     * @param seriesMediaParameterValue the seriesMediaParameterValue to be inserted
     * @param seriesMediaParameterPk the seriesMediaParameter primary key
     *
     * @return the primary key if the insert was successful; 0 otherwise
     */
    public long insertSeriesMediaParameterValue(SeriesMediaParameterValue seriesMediaParameterValue, long seriesMediaParameterPk) {
        String insert = "INSERT INTO seriesMediaParameterValue (fileType, incrementValue, URI, link, seriesMediaParameter_pk) " +
                        "VALUES (:fileType, :incrementValue, :URI, :link, :seriesMediaParameterPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("fileType", seriesMediaParameterValue.getFileType());
            parameterMap.put("incrementValue", seriesMediaParameterValue.getIncrementValue());
            parameterMap.put("URI", seriesMediaParameterValue.getURI());
            parameterMap.put("link", seriesMediaParameterValue.getLink());
            parameterMap.put("seriesMediaParameterPk", seriesMediaParameterPk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (DuplicateKeyException e) {
            logger.error("INSERT to seriesMediaParameterValue failed for link {}, URI {}, seriesMediaParameterPk {}. Reason:\n\t{}",
                         seriesMediaParameterValue.getLink(), seriesMediaParameterValue.getURI(), seriesMediaParameterPk, e.getLocalizedMessage());
        }

        return 0;
    }

    /**
     * Inserts the given {@code seriesMediaParameterValuePk} and {@code parameterAssociationPk} into the
     * seriesMediaParameterValue_parameterAssociation table.  If the insert fails, an error is logged.
     *
     * @param seriesMediaParameterValuePk The seriesMediaParameterValue primary key
     * @param parameterAssociationPk      The parameterAssociation primary key
     */
    public void insertSeriesMediaParameterValue_parameterAssociation(long seriesMediaParameterValuePk, long parameterAssociationPk) {

        String insert = "INSERT INTO seriesMediaParameterValue_parameterAssociation(seriesMediaParameterValue_pk, parameterAssociation_pk) " +
                        "VALUES (:seriesMediaParameterValuePk, :parameterAssociationPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("seriesMediaParameterValuePk", seriesMediaParameterValuePk);
            parameterMap.put("parameterAssociationPk", parameterAssociationPk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count == 0) {
                logger.error("INSERT to seriesMediaParameterValue_parameterAssociation failed for seriesMediaParameterValuePk {}, parameterAssociationPk {}.",
                        seriesMediaParameterValuePk, parameterAssociationPk);
            }

        } catch (Exception e) {
            logger.error("INSERT to seriesMediaParameterValue_parameterAssociation failed for seriesMediaParameterValuePk {}, parameterAssociationPk {}. Reason:\n\t{}",
                         seriesMediaParameterValuePk, parameterAssociationPk, e.getLocalizedMessage());
        }
    }

    /**
     * Inserts the given {@code seriesMediaParameterValuePk} and {@code procedureMetadataPk} into the
     * seriesMediaParameterValue_procedureMetadata table. If the insert fails, an error is logged.
     *
     * @param seriesMediaParameterValuePk The seriesMediaParameterValue primary key
     * @param procedureMetadataPk         The procedureMetadata primary key
     */
    public void insertSeriesMediaParameterValue_procedureMetadata(long seriesMediaParameterValuePk, long procedureMetadataPk) {

        String insert = "INSERT INTO seriesMediaParameterValue_procedureMetadata(seriesMediaParameterValue_pk, procedureMetadata_pk) " +
                        "VALUES (:seriesMediaParameterValuePk, :procedureMetadataPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("seriesMediaParameterValuePk", seriesMediaParameterValuePk);
            parameterMap.put("procedureMetadataPk", procedureMetadataPk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count == 0) {
                logger.error("INSERT to seriesMediaParameterValue_procedureMetadata failed for seriesMediaParameterValuePk {}, procedureMetadataPk {}.",
                             seriesMediaParameterValuePk, procedureMetadataPk);
            }

        } catch (Exception e) {
            logger.error("INSERT to seriesMediaParameterValue_procedureMetadata failed for mediaseriesMediaParameterValuePkParameterPk {}, procedureMetadataPk {}. Reason:\n\t{}",
                         seriesMediaParameterValuePk, procedureMetadataPk, e.getLocalizedMessage());
        }
    }

    /**
     * Inserts the given {@link SeriesParameter} into the seriesParameter table. If the insert fails, an error is logged
     * and 0 is returned.
     *
     * @param seriesParameter the series parameter to be inserted
     * @param procedurePk the procedure primary key
     *
     * @return the primary key if the insert was successful; 0 otherwise
     */
    public long insertSeriesParameter(SeriesParameter seriesParameter, long procedurePk) {
        String insert = "INSERT INTO seriesParameter (parameterId, parameterStatus, sequenceId, procedure_pk) "
                      + "VALUES (:parameterId, :parameterStatus, :sequenceId, :procedurePk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("parameterId", seriesParameter.getParameterID());
            parameterMap.put("parameterStatus", seriesParameter.getParameterStatus());
            parameterMap.put("sequenceId", seriesParameter.getSequenceID());
            parameterMap.put("procedurePk", procedurePk);

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                return pk;
            }

        } catch (Exception e) {
            logger.error("INSERT to seriesParameter failed for parameterId {}, procedurePk {}. Reason:\n\t{}",
                         seriesParameter.getParameterID(), procedurePk, e.getLocalizedMessage());
        }

        return 0;
    }

    /**
     * Inserts the given {@link SeriesParameterValue} into the seriesParameterValue table. If the insert fails, an error
     * is logged.
     *
     * @param seriesParameterValue the series parameter value to be inserted
     * @param seriesParameterPk the series parameter primary key
     *
     */
    public void insertSeriesParameterValue(SeriesParameterValue seriesParameterValue, long seriesParameterPk) {
        String insert = "INSERT INTO seriesParameterValue(value, incrementValue, incrementStatus, seriesParameter_pk) "
                      + "VALUES (:value, :incrementValue, :incrementStatus, :seriesParameterPk)";

        try {
            Map<String, Object> parameterMap = new HashMap<>();

            parameterMap.put("value", seriesParameterValue.getValue());
            parameterMap.put("incrementValue", seriesParameterValue.getIncrementValue());
            parameterMap.put("incrementStatus", seriesParameterValue.getIncrementStatus());
            parameterMap.put("seriesParameterPk", seriesParameterPk);

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count == 0) {
                logger.error("INSERT to seriesParameterValue failed for value {}, seriesParameterPk {}.",
                             seriesParameterValue.getValue(), seriesParameterPk);
            }

        } catch (Exception e) {
            logger.error("INSERT to seriesParameterValue failed for value {}, seriesParameterPk {}. Reason:\n\t{}",
                         seriesParameterValue.getValue(), seriesParameterPk, e.getLocalizedMessage());
        }
    }

    /**
     * Inserts the given {@link Specimen} into the specimen table. Duplicates are ignored. ColonyIds are trimmed first.
     *
     * @param specimen the specimen to be inserted
     * @param datasourceShortName the data source short name (e.g. EuroPhenome, IMPC, 3I, etc.)
     *
     * @return the specimen, with primary key loaded
     */
    public Specimen insertSpecimen(Specimen specimen, String datasourceShortName) throws DataLoadException {
        final String insert = "INSERT INTO specimen (" +
                    "colonyId, datasourceShortName, gender, isBaseline, litterId, phenotypingCenter, pipeline, productionCenter, project, specimenId, strainId, zygosity, statuscode_pk) VALUES " +
                    "(:colonyId, :datasourceShortName, :gender, :isBaseline, :litterId, :phenotypingCenter, :pipeline, :productionCenter, :project, :specimenId, :strainId, :zygosity, :statuscodePk);";

        Map<String, Object> parameterMap = new HashMap();

        try {
            parameterMap.put("colonyId", (specimen.getColonyID() == null ? null : specimen.getColonyID().trim()));
            parameterMap.put("datasourceShortName", datasourceShortName);
            parameterMap.put("gender", (specimen.getGender() == null ? null : specimen.getGender().value()));
            parameterMap.put("isBaseline", specimen.isIsBaseline() ? 1 : 0);
            parameterMap.put("litterId", specimen.getLitterId());
            parameterMap.put("phenotypingCenter", (specimen.getPhenotypingCentre() == null ? null : specimen.getPhenotypingCentre().value()));
            parameterMap.put("pipeline", specimen.getPipeline());
            parameterMap.put("productionCenter", (specimen.getProductionCentre() == null ? null : specimen.getProductionCentre().value()));
            parameterMap.put("project", specimen.getProject());
            parameterMap.put("specimenId", specimen.getSpecimenID());
            parameterMap.put("strainId", specimen.getStrainID());
            parameterMap.put("zygosity", specimen.getZygosity() == null ? null : specimen.getZygosity().value());
            parameterMap.put("statuscodePk", specimen.getStatusCode());

            KeyHolder keyholder = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
            if (count > 0) {
                long pk = keyholder.getKey().longValue();
                specimen.setHjid(pk);
            }
        } catch (DuplicateKeyException e ) {
            String query = "SELECT pk FROM specimen WHERE phenotypingCenter = :phenotypingCenter AND specimenId = :specimenId";
            Long id = npJdbcTemplate.queryForObject(query, parameterMap, Long.class);
            specimen.setHjid(id);
        }
        
        return specimen;
    }

    /**
     * Inserts the given {@link SimpleParameter} into the simpleParameter table. If the insert fails, an error is logged.
     *
     * @param simpleParameter the simpleParameter to be inserted
     * @param procedurePk the procedure primary key
     */
    public void insertSimpleParameter(SimpleParameter simpleParameter, long procedurePk) throws DataLoadException {
        final String insert = "INSERT INTO simpleParameter (parameterId, parameterStatus, procedure_pk, sequenceId, unit, value) "
                            + "VALUES (:parameterId, :parameterStatus, :procedurePk, :sequenceId, :unit, :value)";

        Map<String, Object> parameterMap = new HashMap();

        try {
            parameterMap.put("parameterId", simpleParameter.getParameterID());
            parameterMap.put("parameterStatus", simpleParameter.getParameterStatus());
            parameterMap.put("procedurePk", procedurePk);
            parameterMap.put("sequenceId", simpleParameter.getSequenceID());
            parameterMap.put("unit", simpleParameter.getUnit());
            parameterMap.put("value", simpleParameter.getValue());

            int count = npJdbcTemplate.update(insert, parameterMap);
            if (count == 0) {
                logger.error("INSERT to simpleParameter failed for parameterId {}, procedurePk {}.",
                             simpleParameter.getParameterID(), procedurePk);
            }

        } catch (Exception e) {
            logger.error("INSERT to simpleParameter failed for parameterId {}, procedurePk {}. Reason:\n\t{}",
                         simpleParameter.getParameterID(), procedurePk, e.getLocalizedMessage());
        }
    }


    // SELECT OR INSERTS


    /**
     * Returns the center_procedure primary key matching the given centerPk and procedurePk. The data is first inserted
     * into the center_procedure table if it does not yet exist.
     *
     * @param centerPk The center primary key
     * @param procedurePk The procedure primary key
     *
     * @return the center_procedure primary key matching the given centerPk and procedurePk. The data is first inserted
     *         into the center_procedure table if it does not yet exist.
     */
    public long selectOrInsertCenter_procedure(long centerPk, long procedurePk) {
        Map<String, Object> parameterMap = new HashMap<>();
        Long pk;
        final String query = "SELECT pk FROM center_procedure WHERE center_pk = :centerPk and procedure_pk = :procedurePk";

        parameterMap.put("centerPk", centerPk);
        parameterMap.put("procedurePk", procedurePk);
        pk = loadUtils.queryForPk(npJdbcTemplate, query, parameterMap);
        if ((pk == null) || (pk == 0)) {
            String insert = "INSERT INTO center_procedure (center_pk, procedure_pk) VALUES (:centerPk, :procedurePk)";
            try {
                int count = npJdbcTemplate.update(insert, parameterMap);
                if (count > 0) {
                    pk = loadUtils.queryForPk(npJdbcTemplate, query, parameterMap);
                }

            } catch (DuplicateKeyException e) {

            }
        }

        return pk;
    }

    /**
     * Inserts the {@link Experiment} into the experiment table and returns the primary key of the newly inserted row.
     *
     * @param experiment The experiment instance to be fetched/inserted
     * @param datasourceShortName the data source short name (e.g. EuroPhenome, IMPC, 3I, etc.)
     * @param center_procedurePk The center_procedure primary key
     *
     * @return the {@link Experiment} primary key
     */
    public long insertExperiment(Experiment experiment, String datasourceShortName, long center_procedurePk) throws DataLoadException {
        long pk = 0L;

        String insert =
                "INSERT INTO experiment (dateOfExperiment, experimentId, sequenceId, datasourceShortName, center_procedure_pk) " +
                "VALUES (:dateOfExperiment, :experimentId, :sequenceId, :datasourceShortName, :center_procedurePk)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("dateOfExperiment", experiment.getDateOfExperiment());
        parameterMap.put("experimentId", experiment.getExperimentID());
        parameterMap.put("sequenceId", experiment.getSequenceID());
        parameterMap.put("datasourceShortName", datasourceShortName);
        parameterMap.put("center_procedurePk", center_procedurePk);

        KeyHolder          keyholder       = new GeneratedKeyHolder();
        SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);

        int count = npJdbcTemplate.update(insert, parameterSource, keyholder);
        if (count > 0) {
            pk = keyholder.getKey().longValue();
        } else {
            throw new DataLoadException("INSERT experiment::datasourceShortName::center_procedure_pk failed. " +
                                                experiment.getExperimentID() + "::" + datasourceShortName + "::" +
                                                center_procedurePk);
        }

        return pk;
    }

    /**
     * Inserts the specified {@code experimentPk} and @@code specimenPk into the experiment_specimen table. Duplicate
     * rows are ignored.
     *
     * @param experimentPk The experiment primary key
     * @param specimenPk The specimen primary key
     */
    public void insertExperiment_specimen(long experimentPk, long specimenPk) {

        final String insert = "INSERT INTO experiment_specimen (experiment_pk, specimen_pk) VALUES (:experimentPk, :specimenPk)";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("experimentPk", experimentPk);
        parameterMap.put("specimenPk", specimenPk);
        try {
            npJdbcTemplate.update(insert, parameterMap);

        } catch (DuplicateKeyException e) {

        }
    }

    /**
     * Inserts the given {@codeexperimentPk} and {@code statuscodePk} into the experiment_statuscode table. Duplicates
     * are ignored.
     *
     * @param experimentPk The experiment primary key
     * @param statuscodePk The statuscode primary key
     *
     * @return the experiment_statuscode primary key matching the given experimentPk and statuscodePk. The data is first
     *         inserted into the experiment_statuscode table if it does not yet exist.
     */
    public void insertExperiment_statuscode(long experimentPk, long statuscodePk) {
        Map<String, Object> parameterMap = new HashMap<>();

        parameterMap.put("experimentPk", experimentPk);
        parameterMap.put("statuscodePk", statuscodePk);
        String insert = "INSERT INTO experiment_statuscode (experiment_pk, statuscode_pk) VALUES (:experimentPk, :statuscodePk)";
        try {
            npJdbcTemplate.update(insert, parameterMap);

        } catch (DuplicateKeyException e) {

        }
    }

    /**
     * Returns experiment primary key if found; 0 otherwise
     * @param experimentId
     * @param center_procedurePk
     * @return experiment primary key if found; 0 otherwise
     */
    public long getExperimentPkByExperimentId(String experimentId, long center_procedurePk) {
        long pk = 0L;
        final String query = "SELECT pk FROM experiment WHERE experimentId = :experimentId AND center_procedure_pk = :center_procedurePk";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("experimentId", experimentId);
        parameterMap.put("center_procedurePk", center_procedurePk);

        try {
            pk = npJdbcTemplate.queryForObject(query, parameterMap, Long.class);
        } catch (Exception e) {

        }

        return pk;
    }

    /**
     * Queries line level experiments for an experiment matching {@code procedureId}, {@code colonyId} and {@code centerId}.
     *
     * @param procedureId the dcc procedure id
     * @param colonyId the colony id
     * @param centerId the dcc center id (name)
     * @return the experiment, if found; null if not found. If more than 1 row is found, an error is logged and null
     *          is returned.
     */
    public DccExperimentDTO getExperimentLineLevel(String procedureId, String colonyId, String centerId) {

        final String query =
                "SELECT\n" +
                        "  l.datasourceShortName,\n" +
                        "  CONCAT(p.procedureId, '-', l.colonyId) AS experimentId,\n" +
                        "  null,\n" +
                        "  null,\n" +
                        "  c.centerId AS phenotypingCenter,\n" +
                        "  NULL       AS productionCenter,\n" +
                        "  c.pipeline,\n" +
                        "  c.project,\n" +
                        "  p.procedureId,\n" +
                        "  p.pk       AS dcc_procedure_pk,\n" +
                        "  l.colonyId,\n" +
                        "  NULL       AS specimenId,\n" +
                        "  NULL       AS gender,\n" +
                        "  sc.value   AS rawProcedureStatus,\n" +
                        "  1 AS isLineLevel\n" +
                        "FROM line l\n" +
                        "JOIN center_procedure            cp  ON cp .pk            = l  .center_procedure_pk\n" +
                        "JOIN center                      c   ON c  .pk            = cp .center_pk\n" +
                        "JOIN procedure_                  p   ON p  .pk            = cp .procedure_pk\n" +
                        "LEFT OUTER JOIN line_statuscode  lsc ON lsc.line_pk       = l  .pk\n" +
                        "LEFT OUTER JOIN statuscode       sc  ON sc .pk            = lsc.statuscode_pk\n" +
                        "WHERE p.procedureId = :procedureId AND l.colonyId = :colonyId AND c.centerId = :centerId";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("procedureId", procedureId);
        parameterMap.put("colonyId", colonyId);
        parameterMap.put("centerId", centerId);

        List<DccExperimentDTO> experiments = npJdbcTemplate.query(query, parameterMap, new DccExperimentRowMapper());

        if (experiments.isEmpty()) {
            return null;
        } else if (experiments.size() > 1) {
            logger.error("Expected exactly 1 row for line-level query. Found {} rows. Query:\n\t{}", experiments.size(), query);
            return null;
        }

        return experiments.get(0);
    }

    /**
     * Queries specimen experiments for an experiment matching {@code experimentId} and {@code centerId}.
     *
     * @param experimentId the dcc experiment id
     * @param centerId the dcc center id (name)
     * @return the experiment, if found; null if not found. If more than 1 row is found, an error is logged and null
     *          is returned.
     */
    public DccExperimentDTO getExperimentSpecimen(String experimentId, String centerId) {

        final String query =
                "SELECT\n" +
                        "  s.datasourceShortName,\n" +
                        "  e.experimentId,\n" +
                        "  e.sequenceId,\n" +
                        "  e.dateOfExperiment,\n" +
                        "  c.centerId   AS phenotypingCenter,\n" +
                        "  s.productionCenter,\n" +
                        "  c.pipeline,\n" +
                        "  c.project,\n" +
                        "  p.procedureId,\n" +
                        "  p.pk         AS dcc_procedure_pk,\n" +
                        "  s.colonyId   AS colonyId,\n" +
                        "  s.specimenId AS specimenId,\n" +
                        "  s.gender     AS gender,\n" +
                        "  sc.value     AS rawProcedureStatus,\n" +
                        "  0            AS isLineLevel\n" +
                        "FROM experiment e\n" +
                        "JOIN center_procedure                 cp  ON cp .pk            = e  .center_procedure_pk\n" +
                        "JOIN center                           c   ON c  .pk            = cp .center_pk\n" +
                        "JOIN procedure_                       p   ON p  .pk            = cp .procedure_pk\n" +
                        "JOIN experiment_specimen              es  ON es .pk            = e  .pk\n" +
                        "JOIN specimen                         s   ON s  .pk            = es .specimen_pk\n" +
                        "LEFT OUTER JOIN experiment_statuscode esc ON esc.experiment_pk = e  .pk\n" +
                        "LEFT OUTER JOIN statuscode            sc  ON sc .pk            = esc.statuscode_pk\n" +
                        "WHERE e.experimentId = :experimentId AND c.centerId = :centerId";

        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("experimentId", experimentId);
        parameterMap.put("centerId", centerId);

        List<DccExperimentDTO> experiments = npJdbcTemplate.query(query, parameterMap, new DccExperimentRowMapper());

        if (experiments.isEmpty()) {
            return null;
        } else if (experiments.size() > 1) {
            logger.error("Expected exactly 1 row for specimen query. Found {} rows. Query:\n\t{}", experiments.size(), query);
            return null;
        }

        return experiments.get(0);
    }


    /**
     * @return all line- and procedure-level experiments
     */
    public List<DccExperimentDTO> getExperiments() {

        final String query =
                "-- dccExperimentDTO_load.sql\n" +
                        "\n" +
                        "SELECT\n" +
                        "  e.datasourceShortName,\n" +
                        "  e.experimentId,\n" +
                        "  e.sequenceId,\n" +
                        "  e.dateOfExperiment,\n" +
                        "  c.centerId   AS phenotypingCenter,\n" +
                        "  CASE WHEN s.productionCenter IS NULL THEN c.centerId\n" +
                        "       ELSE s.productionCenter\n" +
                        "  END AS productionCenter,\n" +
                        "  c.pipeline,\n" +
                        "  c.project,\n" +
                        "  p.procedureId,\n" +
                        "  p.pk         AS dcc_procedure_pk,\n" +
                        "  s.colonyId   AS colonyId,\n" +
                        "  s.specimenId AS specimenId,\n" +
                        "  s.gender     AS gender,\n" +
                        "  s.strainId   AS specimenStrainId,\n" +
                        "  s.zygosity   AS zygosity,\n" +
                        "  sc.value     AS rawProcedureStatus,\n" +
                        "  0            AS isLineLevel,\n" +
                        "  s.isBaseline AS isControl\n" +
                        "FROM experiment e\n" +
                        "JOIN center_procedure                 cp  ON cp .pk            = e  .center_procedure_pk\n" +
                        "JOIN center                           c   ON c  .pk            = cp .center_pk\n" +
                        "JOIN procedure_                       p   ON p  .pk            = cp .procedure_pk\n" +
                        "JOIN experiment_specimen              es  ON es .experiment_pk = e  .pk\n" +
                        "LEFT OUTER JOIN specimen              s   ON s  .pk            = es .specimen_pk\n" +
                        "LEFT OUTER JOIN experiment_statuscode esc ON esc.experiment_pk = e  .pk\n" +
                        "LEFT OUTER JOIN statuscode            sc  ON sc .pk            = esc.statuscode_pk\n" +
                        "UNION ALL\n" +
                        "SELECT\n" +
                        "  l.datasourceShortName,\n" +
                        "  CONCAT(p.procedureId, '-', l.colonyId) AS experimentId,\n" +
                        "  NULL,\n" +
                        "  NULL,\n" +
                        "  c.centerId AS phenotypingCenter,\n" +
                        "  NULL       AS productionCenter,\n" +
                        "  c.pipeline,\n" +
                        "  c.project,\n" +
                        "  p.procedureId,\n" +
                        "  p.pk       AS dcc_procedure_pk,\n" +
                        "  l.colonyId,\n" +
                        "  NULL       AS specimenId,\n" +
                        "  NULL       AS gender,\n" +
                        "  NULL       AS specimenStrainId,\n" +
                        "  NULL       AS zygosity,\n" +
                        "  sc.value   AS rawProcedureStatus,\n" +
                        "  1          AS isLineLevel,\n" +
                        "  0          AS isControl\n" +
                        "FROM line l\n" +
                        "JOIN center_procedure            cp  ON cp .pk            = l  .center_procedure_pk\n" +
                        "JOIN center                      c   ON c  .pk            = cp .center_pk\n" +
                        "JOIN procedure_                  p   ON p  .pk            = cp .procedure_pk\n" +
                        "LEFT OUTER JOIN line_statuscode  lsc ON lsc.line_pk       = l  .pk\n" +
                        "LEFT OUTER JOIN statuscode       sc  ON sc .pk            = lsc.statuscode_pk";

        List<DccExperimentDTO> experiments = npJdbcTemplate.query(query, new HashMap<>(), new DccExperimentRowMapper());

        return (experiments.isEmpty() ? new ArrayList<>() : experiments);
    }

    /**
     * Inserts the given
     * Given a parameterId value, attempts to fetch the matching <code>ParameterAssociation</code> instance. If there is
     * none, the parameterId and sequenceId are first inserted. The <code>ParameterAssociation</code> instance is then
     * returned.
     *
     * <i>NOTE: if <code>parameterId</code> is null, a null <code>ParameterAssociation</code> is returned.</i>
     * <i>NOTE: Any <code>Dimension</code></i> instances in the parameterAssociation are added to the dimension table
     *          and returned in the returned <code>ParameterAssociation</code> instance.
     *
     * @param parameterAssociation a valid <code>ParameterAssociation</code> instance
     *
     * @return The <code>ParameterAssociation</code> instance matching <code>parameterId</code> (and <code>sequenceId</code>,
     * if specified), inserted first if necessary.
     */

    /**
     * Inserts the given {@code parameterAssociation} (and any associated {@link Dimension} instances) and returns the
     * primary key of the row just inserted. If either insert fails, an error is logged and a 0 key is returned.
     *
     * @param parameterAssociation the {@link ParameterAssociation} instance to insert.
     *
     * @return The primary key of the row just inserted.
     */
    public long insertParameterAssociation(ParameterAssociation parameterAssociation) {
        long parameterAssociationPk;
        String parameterId = parameterAssociation.getParameterID();
        Integer sequenceId = (parameterAssociation.getSequenceID() == null ? null : parameterAssociation.getSequenceID().intValue());
        String link = parameterAssociation.getLink();
        final String insertPa = "INSERT INTO parameterAssociation (parameterId, sequenceId, link) VALUES (:parameterId, :sequenceId, :link)";
        final String insertDimension = "INSERT INTO dimension (id, origin, unit, value, parameterAssociation_pk) "
                                     + "VALUES(:id, :origin, :unit, :value, :parameterAssociationPk)";

            try {
                Map<String, Object> parameterMap = new HashMap<>();
                parameterMap.put("parameterId", parameterId);
                parameterMap.put("sequenceId", sequenceId);
                parameterMap.put("link", link);

                KeyHolder keyholder = new GeneratedKeyHolder();
                SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
                npJdbcTemplate.update(insertPa, parameterSource, keyholder);

                npJdbcTemplate.update(insertPa, parameterMap);
                int count = npJdbcTemplate.update(insertPa, parameterSource, keyholder);
                if (count > 0) {
                    parameterAssociationPk = keyholder.getKey().longValue();
                } else {
                    logger.error("INSERT parameterAssociation FAILED for parameterId {}, sequenceId {}",
                                 parameterId, sequenceId);
                    return 0;
                }

                if (parameterAssociation.getDim() != null) {
                    parameterMap.clear();
                    for (Dimension dimension : parameterAssociation.getDim()) {
                        parameterMap.put("id", dimension.getId());
                        parameterMap.put("origin", dimension.getOrigin());
                        parameterMap.put("unit", dimension.getUnit());
                        parameterMap.put("value", dimension.getValue());
                        parameterMap.put("parameterAssociationPk", parameterAssociationPk);

                        npJdbcTemplate.update(insertDimension, parameterMap);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("INSERT of retVal(" + parameterId + ", " + sequenceId + ") FAILED: " + e.getLocalizedMessage());
            }

        return parameterAssociationPk;
    }

    /**
     * Inserts the given {@code procedureMetadata} and returns the primary key of the row just inserted.
     *
     * @param procedureMetadata the {@link ProcedureMetadata} instance to insert.
     *
     * @return The primary key of the row just inserted.
     */
    public long insertProcedureMetadata(ProcedureMetadata procedureMetadata) {

        String insert = "INSERT INTO procedureMetadata (parameterId, parameterStatus, sequenceId, value)\n" +
                " VALUES (:parameterId, :parameterStatus, :sequenceId, :value)";
        try {
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("parameterId", procedureMetadata.getParameterID());
            parameterMap.put("parameterStatus", procedureMetadata.getParameterStatus());
            parameterMap.put("sequenceId", procedureMetadata.getSequenceID());
            parameterMap.put("value", procedureMetadata.getValue());

            KeyHolder          keyholder       = new GeneratedKeyHolder();
            SqlParameterSource parameterSource = new MapSqlParameterSource(parameterMap);
            npJdbcTemplate.update(insert, parameterSource, keyholder);
            long pk = keyholder.getKey().longValue();
            return pk;

        } catch (Exception e) {
            e.printStackTrace();
            String s = String.format("INSERT INTO procedureMetadata (parameterId, parameterStatus, sequenceId, value) VALUES (%s, %s, %s, %s)", procedureMetadata.getParameterID(), procedureMetadata.getParameterStatus(), procedureMetadata.getSequenceID(), procedureMetadata.getValue());
            throw new RuntimeException(s + " FAILED: " + e.getLocalizedMessage());
        }
    }

    /**
     * Given a non-null statuscode value, attempts to insert the statuscode, ignoring any duplicates. Returns the
     * statuscode fetched from the database. A null {@code value} returns a null {@link StatusCode}.
     *
     * @param value The status code value
     * @param dateOfStatuscode statuscode date (may be null)  (Not used in SELECT)
     *
     * @return The {@link StatusCode} instance matching {@code value}
     */
    public StatusCode selectOrInsertStatuscode(String value, Calendar dateOfStatuscode) {
        if (value == null)
            return null;

        StatusCode statuscode = getStatuscode(value);

        if (statuscode == null) {
            String insert = "INSERT INTO statuscode (value, dateOfStatuscode) VALUES (:value, :dateOfStatuscode)\n";

            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("value", value);
            parameterMap.put("dateOfStatuscode", dateOfStatuscode);

            npJdbcTemplate.update(insert, parameterMap);
            statuscode = getStatuscode(value);
        }

        return statuscode;
    }

    /**
     * Given a statuscode instance, attempts to fetch the matching object. If there is none, the value and (nullable)
     * dateOfStatuscode are first inserted. The <code>StatusCode</code> instance is then returned.
     *
     * @param statuscode The <code>StatusCode</code> instance
     *
     * @return The <code>StatusCode</code> instance matching <code>value</code>, inserted first if necessary.
     */
    public StatusCode selectOrInsertStatuscode(StatusCode statuscode) {
        if (statuscode == null)
            return null;

        return selectOrInsertStatuscode(statuscode.getValue(), statuscode.getDate());
    }


    // ROWMAPPERS


    public class DimensionRowMapper implements RowMapper<Dimension> {

        @Override
        public Dimension mapRow(ResultSet rs, int rowNum) throws SQLException {
            Dimension dimension = new Dimension();

            dimension.setHjid(rs.getLong("pk"));
            dimension.setId(rs.getString("id"));
            dimension.setOrigin(rs.getString("origin"));
            dimension.setUnit(rs.getString("unit"));
            BigDecimal value = rs.getBigDecimal("value");
            dimension.setValue( (rs.wasNull() ? null : value));

            return dimension;
        }
    }

    public class ExperimentRowMapper implements RowMapper<Experiment> {

        @Override
        public Experiment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Experiment experiment = new Experiment();

            experiment.setHjid(rs.getLong("pk"));
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(rs.getDate("dateOfExperiment"));
            experiment.setDateOfExperiment(gc);
            experiment.setExperimentID(rs.getString("experimentId"));
            experiment.setSequenceID(rs.getString("sequenceId"));

            return experiment;
        }
    }

    public class ParameterAssociationRowMapper implements RowMapper<ParameterAssociation> {

        @Override
        public ParameterAssociation mapRow(ResultSet rs, int rowNum) throws SQLException {
            ParameterAssociation parameterAssociation = new ParameterAssociation();

            parameterAssociation.setHjid(rs.getLong("pk"));
            parameterAssociation.setParameterID(rs.getString("parameterId"));
            parameterAssociation.setSequenceID(BigInteger.valueOf(rs.getLong("sequenceId")));
            parameterAssociation.setLink(rs.getString("link"));

            return parameterAssociation;
        }
    }

    public class ProcedureMetadataRowMapper implements RowMapper<ProcedureMetadata> {

        @Override
        public ProcedureMetadata mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProcedureMetadata procedureMetadata = new ProcedureMetadata();

            procedureMetadata.setHjid(rs.getLong("pk"));
            procedureMetadata.setParameterID(rs.getString("parameterId"));
            procedureMetadata.setParameterStatus(rs.getString("parameterStatus"));
            BigInteger sequenceId = (rs.wasNull() ? null : BigInteger.valueOf(rs.getLong("sequenceId")));
            procedureMetadata.setSequenceID(sequenceId);
            procedureMetadata.setValue(rs.getString("value"));

            return procedureMetadata;
        }
    }

    public class ProcedureRowMapper implements RowMapper<Procedure> {

        @Override
        public Procedure mapRow(ResultSet rs, int rowNum) throws SQLException {
            Procedure procedure = new Procedure();

            procedure.setHjid(rs.getLong("pk"));
            procedure.setProcedureID(rs.getString("procedureId"));

            return procedure;
        }
    }

    /**
     * {@link Specimen} is an abstract class from which {@link Mouse} and {@link Embryo} are derived. This RowMapper
     * creates either a {@link Mouse} or a {@link Embryo}, returning it as the generic {@link Specimen}.
     */
    public class SpecimenExtendedRowMapper implements RowMapper<SpecimenExtended> {

        @Override
        public SpecimenExtended mapRow(ResultSet rs, int rowNum) throws SQLException {
            SpecimenExtended specimenExtended = new SpecimenExtended();
            Specimen specimen;

            // Create the correct Specimen type and load the Mouse- or Embryo-specific data.
            boolean isMouse = (rs.getInt("is_mouse") == 1 ? true : false);
            if (isMouse) {
                Mouse mouse = new Mouse();
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(rs.getDate("DOB"));
                mouse.setDOB(gc);
                specimen = mouse;
            } else {
                Embryo embryo = new Embryo();
                embryo.setStage(rs.getString("stage"));
                embryo.setStageUnit(StageUnit.fromValue(rs.getString("stageUnit")));
                specimen = embryo;
            }

            // Load the remaining common Specimen data.
            specimen.setHjid(rs.getLong("pk"));
            String colonyId = rs.getString("colonyId");
            if ( ! rs.wasNull()) {
                specimen.setColonyID(colonyId);
            }
            specimen.setGender(Gender.fromValue(rs.getString("gender")));
            specimen.setIsBaseline((rs.getInt("isBaseline") == 1 ? true : false));
            specimen.setLitterId(rs.getString("litterId"));
            specimen.setPhenotypingCentre(CentreILARcode.fromValue(rs.getString("phenotypingCenter")));
            specimen.setPipeline(rs.getString("pipeline"));
            String productionCenter = rs.getString("productionCenter");
            if ( ! rs.wasNull()) {
                specimen.setProductionCentre(CentreILARcode.fromValue(productionCenter));
            }

            // Translate unconventional project names to IMPC project names:
            // Specifically required to remap "Eumodic" to "EUMODIC" for case sensitive lookups
            // by the ConcurrentHashMapAllowNulls class
            // and hence the cdaSqlUtils.getCdaProject_idsByDccProject() method
            specimen.setProject(PROJECT_NAME_TRANSLATIONS.getOrDefault(rs.getString("project"), rs.getString("project")));


            specimen.setSpecimenID(rs.getString("specimenId"));
            String strainId = rs.getString("strainId");
            if ( ! rs.wasNull()) {
                specimen.setStrainID(strainId);
            }
            specimen.setZygosity(Zygosity.fromValue(rs.getString("zygosity")));
            StatusCode statusCode = new StatusCode();
            statusCode.setHjid(rs.getLong("statuscode_pk"));
            specimen.setStatusCode(statusCode);

            specimenExtended.setSpecimen(specimen);
            specimenExtended.setDatasourceShortName(rs.getString("datasourceShortName"));

            return specimenExtended;

        }
    }

    public class StatusCodeRowMapper implements RowMapper<StatusCode> {

        @Override
        public StatusCode mapRow(ResultSet rs, int rowNum) throws SQLException {
            StatusCode statuscode = new StatusCode();

            long pk = rs.getLong("pk");
            if (pk > 0) {
                statuscode.setHjid(pk);
            }

            statuscode.setValue(rs.getString("value"));
            Date dateOfStatuscode = rs.getDate("dateOfStatuscode");
            if ( ! rs.wasNull()) {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(dateOfStatuscode);
                statuscode.setDate(gc);
            }

            return statuscode;
        }
    }
}