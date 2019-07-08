/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package org.mousephenotype.cda.db.dao;

/**
 * experimental observation manager implementation
 *
 * @author Gautier Koscielny (EMBL-EBI) <koscieln@ebi.ac.uk>
 * @since May 2012
 */

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
@Transactional
public class ObservationDAOImpl extends HibernateDAOImpl implements ObservationDAO {

	public ObservationDAOImpl() {
	}

	/**
	 * Creates a new Hibernate project data access manager.
	 * @param sessionFactory the Hibernate session factory
	 */
	public ObservationDAOImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Transactional(readOnly = false)
	public void saveExperiment(Experiment experiment) {
		getCurrentSession().saveOrUpdate(experiment);

	}

	@Transactional(readOnly = false)
	public void saveObservation(Observation observation) {
		getCurrentSession().saveOrUpdate(observation);

	}

	@Transactional(readOnly = false)
	public Observation createSimpleObservation(
			ObservationType observationType,
			String simpleValue,
			Parameter parameter,
			BiologicalSample sample,
			Datasource datasource,
			Experiment experiment, String parameterStatus) {
		return createObservation(observationType, simpleValue, null, null, parameter, sample, datasource, experiment, parameterStatus);
	}

	@Transactional(readOnly = false)
	public Observation createObservation(
			ObservationType observationType,
			String firstDimensionValue,
			String secondDimensionValue,
			String secondDimensionUnit,
			Parameter parameter,
			BiologicalSample sample,
			Datasource datasource,
			Experiment experiment, String parameterStatus) {

		Observation obs = null;
		if (observationType == ObservationType.time_series) {

			TimeSeriesObservation seriesObservation = new TimeSeriesObservation();

			if (firstDimensionValue == null || firstDimensionValue.equals("null") || firstDimensionValue.equals("")) {
				seriesObservation.setMissingFlag(true);
			} else {
				seriesObservation.setDataPoint(Float.parseFloat(firstDimensionValue));
			}

			Date dateOfExperiment = (experiment != null) ? experiment.getDateOfExperiment() : null;
			seriesObservation.setTimePoint(secondDimensionValue, dateOfExperiment, secondDimensionUnit);
			seriesObservation.setDatasource(datasource);
			seriesObservation.setExperiment(experiment);
			seriesObservation.setParameter(parameter);
			seriesObservation.setSample(sample);
			seriesObservation.setType(observationType);
			seriesObservation.setMissingFlag(false);

			obs = seriesObservation;

		} else if (observationType == ObservationType.metadata) {

			logger.debug("Metadata: " + firstDimensionValue);
			MetaDataObservation metaDataObservation = new MetaDataObservation();
			metaDataObservation.setValue(firstDimensionValue);
			metaDataObservation.setDatasource(datasource);
			metaDataObservation.setExperiment(experiment);
			metaDataObservation.setParameter(parameter);
			metaDataObservation.setSample(sample);
			metaDataObservation.setType(observationType);
			// we do our best for missing flag
			if (firstDimensionValue.equals("null")) {
				metaDataObservation.setMissingFlag(true);
			}

			obs = metaDataObservation;

		} else if (observationType == ObservationType.categorical) {

			 /* Categorical information */

			logger.debug("Categorical: " + firstDimensionValue);
			CategoricalObservation categoricalObservation = new CategoricalObservation();
			categoricalObservation.setCategory(firstDimensionValue);
			categoricalObservation.setDatasource(datasource);
			categoricalObservation.setExperiment(experiment);
			categoricalObservation.setParameter(parameter);
			categoricalObservation.setSample(sample);
			categoricalObservation.setType(observationType);
			// we do our best for missing flag
			if (firstDimensionValue.equals("null")) {
				categoricalObservation.setMissingFlag(true);
			}

			obs = categoricalObservation;

		} else if (observationType == ObservationType.unidimensional) {

			 /* Unidimensional information */

			logger.debug("Unidimensional :" + firstDimensionValue);
			UnidimensionalObservation unidimensionalObservation = new UnidimensionalObservation();

			// parse the floating point value
			try {
				unidimensionalObservation.setDataPoint(Float.parseFloat(firstDimensionValue));
			} catch (NumberFormatException ex) {
				// can be "null" can be "/" and many others!
				logger.debug(ex.getMessage());
				unidimensionalObservation.setMissingFlag(true);
			}

			unidimensionalObservation.setDatasource(datasource);
			unidimensionalObservation.setExperiment(experiment);
			unidimensionalObservation.setParameter(parameter);
			unidimensionalObservation.setSample(sample);
			unidimensionalObservation.setType(observationType);

			obs = unidimensionalObservation;
		} else if (observationType == ObservationType.datetime) {

			 /* Unidimensional information */

			logger.debug("Datetime string:" + firstDimensionValue);
			DatetimeObservation datetimeObservation = new DatetimeObservation();

			// Use JAXB to parse the datetime because java SimpleDateFormat cannot parse ISO8601 dates correctly
			if (javax.xml.bind.DatatypeConverter.parseDateTime(firstDimensionValue).getTime() == null) {
				datetimeObservation.setMissingFlag(true);
			} else {
				datetimeObservation.setDatetimePoint(javax.xml.bind.DatatypeConverter.parseDateTime(firstDimensionValue).getTime());
			}

			datetimeObservation.setDatasource(datasource);
			datetimeObservation.setExperiment(experiment);
			datetimeObservation.setParameter(parameter);
			datetimeObservation.setSample(sample);
			datetimeObservation.setType(observationType);

			obs = datetimeObservation;
		} else if (observationType == ObservationType.text) {

			 /* Text information */

			logger.debug("Text :" + firstDimensionValue);
			TextObservation textObservation = new TextObservation();

			textObservation.setText(firstDimensionValue);
			textObservation.setDatasource(datasource);
			textObservation.setExperiment(experiment);
			textObservation.setParameter(parameter);
			textObservation.setSample(sample);
			textObservation.setType(observationType);

			obs = textObservation;
		}

        obs.setParameterStableId(parameter.getStableId());

        // Add the status code to the observation if there is one
        if(parameterStatus!=null) {

            Map<String, String> pStatMap = getParameterStatusAndMessage(parameterStatus);

            obs.setParameterStatus(pStatMap.get("status"));
            obs.setParameterStatusMessage(pStatMap.get("message"));
            obs.setMissingFlag(true);

        }

		return obs;

	}

	@Transactional(readOnly = false)
	public Observation createTimeSeriesObservationWithOriginalDate(
			ObservationType observationType,
			String firstDimensionValue,
			String secondDimensionValue,
			String actualTimepoint,
			String secondDimensionUnit,
			Parameter parameter,
			BiologicalSample sample,
			Datasource datasource,
			Experiment experiment, String parameterStatus) {

		//logger.debug("Series :" + secondDimensionValue + "\t" + firstDimensionValue);

		TimeSeriesObservation obs = new TimeSeriesObservation();

		if (firstDimensionValue == null || firstDimensionValue.equals("null") || firstDimensionValue.equals("")) {
			obs.setMissingFlag(true);
		} else {
			obs.setDataPoint(Float.parseFloat(firstDimensionValue));
		}

		Date actualTimePoint = (experiment != null) ? experiment.getDateOfExperiment() : null;

		// If the center supplied an actual date time,
		// use that as the time_point
		if (actualTimepoint.contains("-")) {
			DateFormat inputDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				actualTimePoint = inputDateFormatter.parse(actualTimepoint);
			} catch (ParseException e) {
				actualTimePoint = (experiment != null) ? experiment.getDateOfExperiment() : null;
			}
		}

		obs.setTimePoint(secondDimensionValue, actualTimePoint, secondDimensionUnit);
		obs.setDatasource(datasource);
		obs.setExperiment(experiment);
		obs.setParameter(parameter);
		obs.setSample(sample);
		obs.setType(observationType);
		obs.setParameterStableId(parameter.getStableId());

		// Add the status code to the observation if there is one
        if(parameterStatus!=null) {

            Map<String, String> pStatMap = getParameterStatusAndMessage(parameterStatus);

            obs.setParameterStatus(pStatMap.get("status"));
            obs.setParameterStatusMessage(pStatMap.get("message"));
            obs.setMissingFlag(true);

        }

		return obs;
	}

	public static Map<String, String> getParameterStatusAndMessage(String parameterStatus) {

	    Map<String, String> pStatusMap = new HashMap<>();
        pStatusMap.put("message", null);
        pStatusMap.put("status", parameterStatus);

       // Add the status code to the observation if there is one
        if(parameterStatus != null) {

            String code = parameterStatus;

            if(code.contains(":")) {

                String message = code.substring(code.indexOf(":")+1, code.length()).trim();
                pStatusMap.put("message", message);

                code = code.substring(0, code.indexOf(":"));
                pStatusMap.put("status", code);

            } else if (code.contains("?")) {
	            String message = code.substring(code.indexOf("?")+1, code.length()).trim();
	            pStatusMap.put("message", message);

	            code = code.substring(0, code.indexOf("?"));
	            pStatusMap.put("status", code);

            }

	        // Truncate the status code if it's too long
	        if (pStatusMap.get("status").length()>50) {
		        pStatusMap.put("status", pStatusMap.get("status").substring(0,45)+"...");
	        }

        }

        return pStatusMap;
	}


    /**
     * Fetch count of records NOT missing but with not null/empty parameter_status or parameter_status_message.
     * @return count, interesting fields
     * @throws SQLException
     */
    @Override
    @Transactional(readOnly = true)
    public List<String[]> getNotMissingNotEmpty() throws SQLException {
        List<String[]> data = new ArrayList();
        Object o;

        String query = "SELECT\n"
                + "  o.missing\n"
                + ", COUNT(*) AS notMissingCount\n"
                + ", e.organisation_id\n"
                + ", o.observation_type\n"
                + ", o.parameter_status\n"
                + ", o.parameter_status_message\n"
                + "FROM observation o\n"
                + "JOIN experiment_observation eo ON eo.observation_id = o.id\n"
                + "JOIN experiment e ON e.id = eo.experiment_id\n"
                + "WHERE (missing = 0)  AND (((TRIM(IFNULL(parameter_status, ''))) != '')\n"
                + "   OR (TRIM(IFNULL(parameter_status_message, '')) != ''))\n"
                + "GROUP BY observation_type, e.organisation_id, o.observation_type, o.parameter_status, o.parameter_status_message\n"
                + "ORDER BY e.organisation_id limit 1000000\n";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String[] row = new String[6];
                row[0] = resultSet.getString("missing");
                row[1] = Long.toString(resultSet.getLong("notMissingCount"));
                row[2] = resultSet.getString("organisation_id");
                row[3] = resultSet.getString("observation_type");
                o = resultSet.getObject("parameter_status");
                row[4] = (String)(o == null ? "<null>" : o);
                o = resultSet.getObject("parameter_status_message");
                row[5] = (String)(o == null ? "<null>" : o);
                data.add(row);
            }
        }

        return data;
    }

    /**
     * Fetch count of records missing that have a null/empty parameter_status.
     * @return count, interesting fields
     * @throws SQLException
     */
    @Override
    @Transactional(readOnly = true)
    public List<String[]> getMissingEmpty() throws SQLException {
        List<String[]> data = new ArrayList();
        Object o;

        String query = "SELECT\n"
                + "  o.missing\n"
                + ", COUNT(*) AS missingCount\n"
                + ", e.organisation_id\n"
                + ", o.observation_type\n"
                + ", o.parameter_status\n"
                + ", o.parameter_status_message\n"
                + "FROM observation o\n"
                + "JOIN experiment_observation eo ON eo.observation_id = o.id\n"
                + "JOIN experiment e ON e.id = eo.experiment_id\n"
                + "WHERE (missing = 1) AND (TRIM(IFNULL(o.parameter_status, '')) = '')\n"
                + "GROUP BY o.observation_type, e.organisation_id, o.parameter_status, o.parameter_status_message\n"
                + "ORDER BY e.organisation_id limit 1000000\n";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String[] row = new String[6];
                row[0] = resultSet.getString("missing");
                row[1] = Long.toString(resultSet.getLong("missingCount"));
                row[2] = resultSet.getString("organisation_id");
                row[3] = resultSet.getString("observation_type");
                o = resultSet.getObject("parameter_status");
                row[4] = (String)(o == null ? "<null>" : o);
                o = resultSet.getObject("parameter_status_message");
                row[5] = (String)(o == null ? "<null>" : o);
                data.add(row);
            }
        }

        return data;
    }

    /**
     * Fetch list of observation.parameter_status that is not in IMPC ontology_term.acc.
     * @return list of missing ontology_term.acc used by observation.parameter_status
     * @throws SQLException
     */
    @Override
    @Transactional(readOnly = true)
    public List<String[]> getMissingOntologyTerms() throws SQLException {
        List<String[]> data = new ArrayList();
        Object o;

        String query = "SELECT DISTINCT\n"
                + "  o.parameter_status\n"
                + ", ot.acc\n"
                + ", e.organisation_id\n"
                + ", o.observation_type\n"
                + "FROM observation o\n"
                + "LEFT OUTER JOIN ontology_term ot ON ot.acc = o.parameter_status AND ot.db_id = 22\n"
                + "JOIN experiment_observation eo ON eo.observation_id = o.id\n"
                + "JOIN experiment e ON e.id = eo.experiment_id\n"
                + "WHERE (TRIM(IFNULL(parameter_status, '')) != '')\n"
                + "  AND ot.acc IS NULL\n"
                + "ORDER BY o.parameter_status, e.organisation_id, o.observation_type\n";

        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String[] row = new String[4];
                row[0] = resultSet.getString("parameter_status");
                o = resultSet.getObject("acc");
                row[1] = (String)(o == null ? "<null>" : o);
                row[2] = resultSet.getString("organisation_id");
                row[3] = resultSet.getString("observation_type");
                data.add(row);
            }
        }

        return data;
    }
}