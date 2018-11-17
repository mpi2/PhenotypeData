package org.mousephenotype.cda.db;

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.constants.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
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

@Component
public class WeightMap {

    private final Logger logger = LoggerFactory.getLogger(WeightMap.class);

    private  static final String ipgttWeightParameter = "IMPC_IPG_001_001";

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    private Map<Integer, List<BodyWeight>> weightMap = new HashMap<>();
    private Map<Integer, BodyWeight> ipgttWeightMap = new HashMap<>();
    private DataSource komp2DataSource;

    public WeightMap() {
    }

    @Inject
    public WeightMap(@Named("komp2DataSource") DataSource komp2DataSource) {
        this.komp2DataSource = komp2DataSource;
    }

    public static boolean isWeightParameter(String stableId) {
        return Constants.weightParameters.contains(stableId) || isIpgttWeightParameter(stableId);
    }

    public static boolean isIpgttWeightParameter(String stableId) {
        return ipgttWeightParameter.equalsIgnoreCase(stableId);
    }

    @PostConstruct
    public void initialize() throws SQLException {
        logger.debug("  populating weight map");
        populateWeightMap();
        logger.debug("  map size: " + weightMap.size());

        logger.debug("  populating ipgt map");
        populateIpgttWeightMap();
        logger.debug("  map size: " + ipgttWeightMap.size());
    }

    public Map<Integer, List<BodyWeight>> get() {
        return Collections.unmodifiableMap(weightMap);
    }

    public List<BodyWeight> get(Integer specimenId) {
        return weightMap.get(specimenId);
    }

    public Integer size() {
        return weightMap.size();
    }

    /**
     * Compare all weight dates to select the nearest to the date of experiment
     *
     * @param specimenID
     *            the specimen
     * @param dateOfExperiment
     *            the date
     * @return the nearest weight bean to the date of the experiment
     */
    public BodyWeight getNearestWeight(Integer specimenID, ZonedDateTime dateOfExperiment) {

        BodyWeight nearest = null;

        if (dateOfExperiment != null && weightMap.containsKey(specimenID)) {

            for (BodyWeight candidate : weightMap.get(specimenID)) {

                if (nearest == null) {
                    nearest = candidate;
                    continue;
                }

                if (Math.abs(
                        dateOfExperiment.toInstant().toEpochMilli() - candidate.date.toInstant().toEpochMilli()) < Math
                        .abs(dateOfExperiment.toInstant().toEpochMilli()
                                - nearest.date.toInstant().toEpochMilli())) {
                    nearest = candidate;
                }
            }
        }

        // Do not return weight that is > 4 days away from the experiment
        // since the weight of the specimen become less and less relevant
        // (Heuristic from Natasha Karp @ WTSI)
        // 4 days = 345,600,000 ms
        if (nearest != null && Math
                .abs(dateOfExperiment.toInstant().toEpochMilli() - nearest.date.toInstant().toEpochMilli()) > 3.456E8) {
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
    public BodyWeight getNearestIpgttWeight(Integer specimenID) {

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

        String query = "SELECT o.biological_sample_id, data_point AS weight, parameter_stable_id,  date_of_experiment, datediff(date_of_experiment, ls.date_of_birth) as days_old, e.organisation_id "
                + "FROM observation o " + "  INNER JOIN unidimensional_observation uo ON uo.id = o.id  "
                + "  INNER JOIN live_sample ls ON ls.id=o.biological_sample_id  "
                + "  INNER JOIN experiment_observation eo ON o.id = eo.observation_id  "
                + "  INNER JOIN experiment e ON e.id = eo.experiment_id  " + "WHERE parameter_stable_id IN ("
                + StringUtils.join(Constants.weightParameters, ",") + ") AND data_point > 0"
                + "  ORDER BY biological_sample_id, date_of_experiment ASC ";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                BodyWeight b = new BodyWeight();
                try {
                    b.date = ZonedDateTime.parse(resultSet.getString("date_of_experiment"),
                            DateTimeFormatter.ofPattern(DATETIME_FORMAT).withZone(ZoneId.of("UTC")));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    b.date = null;
                    logger.debug("  No date of experiment set for sample id {} parameter {}",
                            resultSet.getString("biological_sample_id"), resultSet.getString("parameter_stable_id"));
                }

                b.weight = resultSet.getFloat("weight");
                b.parameterStableId = resultSet.getString("parameter_stable_id");
                b.daysOld = resultSet.getInt("days_old");

                final Integer specimenId = resultSet.getInt("biological_sample_id");

                if (!weightMap.containsKey(specimenId)) {
                    weightMap.put(specimenId, new ArrayList<>());
                }

                weightMap.get(specimenId).add(b);
                count += 1;
            }
        }

        logger.info(" Added {} specimen weight data map entries", count, weightMap.size());
    }

    /**
     * Return map of specimen ID => weight for
     *
     * @throws SQLException When a database error occurrs
     */
    private void populateIpgttWeightMap() throws SQLException {

        String query = "SELECT o.biological_sample_id, data_point AS weight, parameter_stable_id, date_of_experiment, DATEDIFF(date_of_experiment, ls.date_of_birth) AS days_old "
                + "FROM observation o " + "  INNER JOIN unidimensional_observation uo ON uo.id = o.id "
                + "  INNER JOIN live_sample ls ON ls.id=o.biological_sample_id "
                + "  INNER JOIN experiment_observation eo ON o.id = eo.observation_id "
                + "  INNER JOIN experiment e ON e.id = eo.experiment_id " + "WHERE parameter_stable_id = '"
                + ipgttWeightParameter + "' ";

        try (Connection connection = komp2DataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {

                BodyWeight b = new BodyWeight();
                try {
                    b.date = ZonedDateTime.parse(resultSet.getString("date_of_experiment"),
                            DateTimeFormatter.ofPattern(DATETIME_FORMAT).withZone(ZoneId.of("UTC")));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    b.date = null;
                    logger.debug("  No date of experiment set for sample id {} parameter {}",
                            resultSet.getString("biological_sample_id"), resultSet.getString("parameter_stable_id"));

                }
                b.weight = resultSet.getFloat("weight");
                b.parameterStableId = resultSet.getString("parameter_stable_id");
                b.daysOld = resultSet.getInt("days_old");

                final Integer specimenId = resultSet.getInt("biological_sample_id");
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
    }

}
