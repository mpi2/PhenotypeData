package org.mousephenotype.cda.db;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * WeightMap is a body weight specimen map used primarily in the association of body weights to parameters in the
 * ObservationIndexer. There is also an integration test in the loads module that also requires access to this class
 * and belongs in the loads module.
 *
 * As of 12/12/2018, loading this class takes upwards of 8 minutes, and there is no requirement that it be managed by
 * Spring.
 *
 * Manually instantiate this class as required.
 *
 * NOTE: Do not make this a spring component because it will slow everything that component-scans it.
 */
public class WeightMap {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String ipgttWeightParameter = "IMPC_IPG_001_001";

    private Map<Long, List<BodyWeight>> weightMap      = new HashMap<>();
    private Map<Long, BodyWeight>       ipgttWeightMap = new HashMap<>();
    private DataSource                  komp2DataSource;

    public WeightMap(@Named("komp2DataSource") DataSource komp2DataSource) throws SQLException {
        this.komp2DataSource = komp2DataSource;
        initialize();
    }

    public static boolean isWeightParameter(String stableId) {
        return Constants.weightParameters.contains(stableId) || isIpgttWeightParameter(stableId);
    }

    public static boolean isIpgttWeightParameter(String stableId) {
        return ipgttWeightParameter.equalsIgnoreCase(stableId);
    }

    public void initialize() throws SQLException {
        logger.debug("  populating weight map");
        populateWeightMap();
        logger.debug("  map size: " + weightMap.size());

        logger.debug("  populating ipgt map");
        populateIpgttWeightMap();
        logger.debug("  map size: " + ipgttWeightMap.size());
    }

    public Map<Long, List<BodyWeight>> get() {
        return Collections.unmodifiableMap(weightMap);
    }

    public List<BodyWeight> get(Long specimenId) {
        return weightMap.get(specimenId);
    }

    public Integer size() {
        return weightMap.size();
    }


    public BodyWeight getNearestWeight(Long specimenID, ZonedDateTime dateOfExperiment) {
        return getNearestWeight(specimenID, null, dateOfExperiment);
    }

    /**
     * Compare all weight dates to select the nearest to the date of experiment
     *
     * This method follows a set of rules as follows:
     *  1) Closest weight in time to the date of experiment
     *
     *  NOTE: If there are multiple points in time equidistant from the date of exeperiment
     *  2) Weight value from the same procedure
     *  3) The IMPC bodyweight procedure IMPC_BWT
     *  4) The weight with the largest value (favoring future weights over past weights)
     *
     *
     * @param specimenID       the specimen
     * @param stableId         the stable ID of the parameter with which to associate the weight
     * @param dateOfExperiment the date the experiment was conducted
     * @return the nearest weight bean to the date of the experiment
     */
    public BodyWeight getNearestWeight(Long specimenID, String stableId, ZonedDateTime dateOfExperiment) {

        BodyWeight nearest = null;
        String procedureGroup = null;

        if (stableId!= null) {
            procedureGroup = stableId.substring(0, stableId.indexOf("_", 1));
        }

        if (dateOfExperiment != null && weightMap.containsKey(specimenID)) {

            for (BodyWeight candidate : weightMap.get(specimenID)) {

                if (nearest == null) {
                    nearest = candidate;
                    continue;
                }

                final long candidateDiff = Math.abs(dateOfExperiment.toInstant().toEpochMilli() - candidate.date.toInstant().toEpochMilli());
                final long nearestDiff = Math.abs(dateOfExperiment.toInstant().toEpochMilli() - nearest.date.toInstant().toEpochMilli());

                // If the date of the candidate weight is closer in time to the date of experiment, use that
                if (candidateDiff < nearestDiff) {
                    nearest = candidate;
                }
                // If the weights are the same distance away in time to the date of experiment...
                else if (candidateDiff == nearestDiff){

                    // This is now a selection based on rules
                    //   1) Prefer weight measurements from the same procedure
                    //   2) Second preference are data from the IMPC_BWT procedure
                    //   3) Third preference is the larger weight value (selective pressure towards weight dates in the future)

                    // NOTE: If there are multiple weight measurements from the same procedure that are equidistant
                    // the largest value will be preferred

                    if (procedureGroup!= null && candidate.getParameterStableId().contains(procedureGroup)) {
                        if (candidate.getWeight() > nearest.getWeight()) {
                            nearest = candidate;
                        }
                    } else if (candidate.getParameterStableId().contains("_BWT")) {
                        if (candidate.getWeight() > nearest.getWeight()) {
                            nearest = candidate;
                        }
                    } else if (candidate.getWeight() > nearest.getWeight()) {
                        nearest = candidate;
                    }

                }
            }
        }

        // Do not return weight that is > 4 days away from the experiment
        // since the weight of the specimen become less and less relevant
        // (Heuristic from Natasha Karp @ WTSI)
        // 4 days = 345,600,000 ms
        if (nearest != null &&
                Math.abs(dateOfExperiment.toInstant().toEpochMilli() - nearest.date.toInstant().toEpochMilli()) > 3.456E8) {
            nearest = null;
        }
        return nearest;
    }


    /**
     * Select the nearest weight by date of experiment
     *
     * @param specimenID the specimen
     * @return the nearest weight to the date of the experiment
     */
    public BodyWeight getNearestIpgttWeight(Long specimenID) {

        BodyWeight nearest = null;

        if (ipgttWeightMap.containsKey(specimenID)) {
            nearest = ipgttWeightMap.get(specimenID);
        }

        return nearest;
    }

    /**
     * Return map of specimen ID => List of all weights ordered by date ASC
     *
     * @throws SQLException When a database error occurs
     */
    private void populateWeightMap() throws SQLException {

        int count = 0;

        String query = "SELECT o.biological_sample_id, data_point AS weight, parameter_stable_id,  date_of_experiment, DATEDIFF(date_of_experiment, ls.date_of_birth) as days_old, e.organisation_id "
                + "FROM observation o " + "  INNER JOIN unidimensional_observation uo ON uo.id = o.id  "
                + "  INNER JOIN live_sample ls ON ls.id=o.biological_sample_id  "
                + "  INNER JOIN experiment_observation eo ON o.id = eo.observation_id  "
                + "  INNER JOIN experiment e ON e.id = eo.experiment_id  " + "WHERE parameter_stable_id IN ("
                + StringUtils.join(Constants.weightParameters, ",") + ") AND data_point > 0"
                + "  ORDER BY biological_sample_id, date_of_experiment ASC ";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            logger.info("populating weight map. This takes upwards of 8 minutes.");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                BodyWeight b = new BodyWeight();
                try {
                    b.date = ZonedDateTime.parse(resultSet.getString("date_of_experiment"),
                            DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_OPTIONAL_MILLISECONDS).withZone(ZoneId.of("UTC")));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    b.date = null;
                    logger.debug("  No date of experiment set for sample id {} parameter {}",
                                 resultSet.getString("biological_sample_id"), resultSet.getString("parameter_stable_id"));
                }

                b.weight = resultSet.getFloat("weight");
                b.parameterStableId = resultSet.getString("parameter_stable_id");
                b.daysOld = resultSet.getInt("days_old");

                final Long specimenId = resultSet.getLong("biological_sample_id");

                if ( ! weightMap.containsKey(specimenId)) {
                    weightMap.put(specimenId, new ArrayList<>());
                }

                weightMap.get(specimenId).add(b);
                count += 1;
            }
        }

        logger.info(" Added {} specimen weight data map entries (weight map size: {})", count, weightMap.size());
    }

    /**
     * Return map of specimen ID => weight for
     *
     * @throws SQLException When a database error occurrs
     */
    private void populateIpgttWeightMap() throws SQLException {

        String query = "SELECT o.biological_sample_id, data_point AS weight, parameter_stable_id, date_of_experiment, DATEDIFF(date_of_experiment, ls.date_of_birth) AS days_old "
                + "FROM observation o "
                + "  INNER JOIN unidimensional_observation uo ON uo.id = o.id "
                + "  INNER JOIN live_sample ls ON ls.id=o.biological_sample_id "
                + "  INNER JOIN experiment_observation eo ON o.id = eo.observation_id "
                + "  INNER JOIN experiment e ON e.id = eo.experiment_id "
                + "WHERE parameter_stable_id = '" + ipgttWeightParameter + "' ";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                BodyWeight b = new BodyWeight();
                try {
                    b.date = ZonedDateTime.parse(resultSet.getString("date_of_experiment"),
                            DateTimeFormatter.ofPattern(Constants.DATETIME_FORMAT_OPTIONAL_MILLISECONDS).withZone(ZoneId.of("UTC")));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    b.date = null;
                    logger.debug("  No date of experiment set for sample id {} parameter {}",
                            resultSet.getString("biological_sample_id"), resultSet.getString("parameter_stable_id"));

                }
                b.weight = resultSet.getFloat("weight");
                b.parameterStableId = resultSet.getString("parameter_stable_id");
                b.daysOld = resultSet.getInt("days_old");

                final Long specimenId = resultSet.getLong("biological_sample_id");
                ipgttWeightMap.put(specimenId, b);
            }
        }

    }

    /**
     * Internal class to act as Map value DTO for weight data
     */
    public class BodyWeight {
        private String parameterStableId;
        private ZonedDateTime date;
        private Float weight;
        private Integer daysOld;

        public String getParameterStableId() {
            return parameterStableId;
        }

        public void setParameterStableId(String parameterStableId) {
            this.parameterStableId = parameterStableId;
        }

        public ZonedDateTime getDate() {
            return date;
        }

        public void setDate(ZonedDateTime date) {
            this.date = date;
        }

        public Float getWeight() {
            return weight;
        }

        public void setWeight(Float weight) {
            this.weight = weight;
        }

        public Integer getDaysOld() {
            return daysOld;
        }

        public void setDaysOld(Integer daysOld) {
            this.daysOld = daysOld;
        }

        @Override
        public String toString() {
            return "WeightBean{" + "parameterStableId='" + parameterStableId + '\'' + ", date=" + date + ", weight="
                    + weight + ", daysOld=" + daysOld + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BodyWeight that = (BodyWeight) o;
            return Objects.equals(parameterStableId, that.parameterStableId) &&
                    Objects.equals(date, that.date) &&
                    Objects.equals(weight, that.weight) &&
                    Objects.equals(daysOld, that.daysOld);
        }

        @Override
        public int hashCode() {

            return Objects.hash(parameterStableId, date, weight, daysOld);
        }
    }
}