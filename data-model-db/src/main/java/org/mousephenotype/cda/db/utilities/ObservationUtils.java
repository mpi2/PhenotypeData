/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.db.utilities;

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ObservationUtils {

    protected static final Logger logger = LoggerFactory.getLogger(ObservationUtils.class);


    public static Observation createObservation(
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

    public static Observation createSimpleObservation(
            ObservationType observationType,
            String simpleValue,
            Parameter parameter,
            BiologicalSample sample,
            Datasource datasource,
            Experiment experiment, String parameterStatus) {
        return createObservation(observationType, simpleValue, null, null, parameter, sample, datasource, experiment, parameterStatus);
    }

    public static Observation createTimeSeriesObservationWithOriginalDate(
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

    private static Map<String, String> getParameterStatusAndMessage(String parameterStatus) {

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
}