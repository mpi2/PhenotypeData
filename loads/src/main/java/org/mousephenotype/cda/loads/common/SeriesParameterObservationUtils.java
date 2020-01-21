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

import org.mousephenotype.cda.utilities.DateUtils;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ProcedureMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mrelac on 25/11/2016 from AdminTools code.
 * <p/>
 * Discussion:
 * This class encapsulates code required to process time series data received from centers.
 * Sometimes centers send us data with a proper date and time stamp; other times they send just the time stamp. In the
 * latter case, we must discover the date component by adding the given timestamp (which is relative to lights-out time)
 * to the experiment date, being careful to handle late evening and early morning experiments where the experiment date
 * may be the day before lights-out.
 */
public class SeriesParameterObservationUtils {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * Convert a time unit from absolute date to "time since lights out" as required
     * by the SOP.
     * <p/>
     * input comes in as a full time stamp string like 2009-01-22 14:05:00
     *
     * @param input                 the absolute date/time to convert
     * @param dccExperimentDTO      the dcc experiment instance. The following components are required:
     *                              <ul>
     *                              <li>datasourceShortName</li>
     *                              <li>dateOfExperiment</li>
     *                              <li>procedureId</li>
     *                              <li>phenotypingCenter</li>
     *                              <li>experimentId (for use in logging messages only)</li>
     *                              </ul>
     * @param procedureMetadataList the procedure metadata list
     * @return float representing the converted time between -5 hours and +23 hours
     */
    public Float convertTimepoint(String input, DccExperimentDTO dccExperimentDTO, List<ProcedureMetadata> procedureMetadataList) {
        Float retFloat = null;

        // If there is a +0000 at the end of the timestamp, we need to put it
        // into +00:00 expected format
        String[] parts = input.split("\\+");
        if (parts.length > 1) {
            if (!parts[1].contains(":")) {
                String newTimezone = parts[1].substring(0, 2) + ":" + parts[1].substring(2, 4);
                input = parts[0] + "+" + newTimezone;
            }
        }

        try {
            switch (dccExperimentDTO.getDatasourceShortName()) {
                case "IMPC":

                    // Calculate the date difference between the measured value and lights out cycle
                    Date d = DateUtils.parseIncrementValue(logger, input);
                    Long measuredAt = d.getTime();
                    Long startOfDay = dccExperimentDTO.getDateOfExperiment().getTime();
                    Long lightsOut = dccExperimentDTO.getProcedureId().contains("IMPC_CAL") ? getLightsOut(dccExperimentDTO, procedureMetadataList) : null;

                    Long normalizedMillis = measuredAt - startOfDay;
                    if (lightsOut != null) {
                        normalizedMillis = measuredAt - lightsOut;
                    }

                    retFloat = normalizedMillis / 1000.0f / 60.0f / 60.0f;

                    break;

                case "EuroPhenome":
                    retFloat = convertEurophenomeTimepoint(dccExperimentDTO, input);
                    break;
            }
        } catch (Exception e) {
            String message = "Unable to convert IMPC timepoint date "
                    + "'"    + input + "'"
                    + " for phenotypingCenter::experiment::procedure "
                    + "'"    + dccExperimentDTO.getPhenotypingCenter()
                    + "'::'" + dccExperimentDTO.getExperimentId()
                    + "'::'" + dccExperimentDTO.getProcedureId();

            logger.warn(message, e);
        }

        return retFloat;
    }

    /**
     * Gets the lights out cycle start time of the passed in experiment
     * the lights out cycle for IMPC Calorimetry is one of three procedureMetadata
     * IMPC_CAL_003_001,2,3 with three different date/time formats
     * See:
     * https://www.mousephenotype.org/impress/parameters/86/7
     * https://www.mousephenotype.org/impress/parameters/153/7
     * https://www.mousephenotype.org/impress/parameters/240/7
     *
     * @param dccExperimentDTO      the dcc experiment instance. The following components are required:
     *                              <ul>
     *                              <li>dateOfExperiment</li>
     *                              <li>procedureId  (for use in logging messages only)</li>
     *                              <li>experimentId (for use in logging messages only)</li>
     *                              </ul>
     * @param procedureMetadataList the procedure metadata list
     * @return lightsOut as long
     */
    public Long getLightsOut(DccExperimentDTO dccExperimentDTO, List<ProcedureMetadata> procedureMetadataList) {

        Long lightsOut = null;

        Map<String, SimpleDateFormat> knownPatterns = new HashMap<>();
        knownPatterns.put("IMPC_CAL_010_001", new SimpleDateFormat("HH:mm' 'a"));
        knownPatterns.put("IMPC_CAL_010_002", new SimpleDateFormat("HH:mm:ss"));
        knownPatterns.put("IMPC_CAL_010_003", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));

        final List<String> lightsOutParameters = new ArrayList<>(knownPatterns.keySet());

        try {
            for (ProcedureMetadata metadata : procedureMetadataList) {

                // Find the lights out metadata parameter
                if (lightsOutParameters.contains(metadata.getParameterID())) {

                    // This is the paramter "Time of dark cycle start"

                    SimpleDateFormat format = knownPatterns.get(metadata.getParameterID());
                    String           value  = metadata.getValue();

                    if (metadata.getParameterID().equals("IMPC_CAL_010_003")) {
                        Date d = DateUtils.parseIncrementValue(logger, value);
                        lightsOut = d.getTime();
                    } else {
                        try {
                            lightsOut = format.parse(value).getTime();
                        } catch (ParseException pe) {
                            logger.warn("Unknown pattern parsing date string {}", value);
                            return null;
                        }
                    }

                    if ( ! metadata.getParameterID().equals("IMPC_CAL_010_003")) {
                        // Need to add the date of experiment to the time
                        Long dateOfExperiment = dccExperimentDTO.getDateOfExperiment().getTime();
                        lightsOut += dateOfExperiment;
                    }

                    break;
                }

            }
        } catch (Exception e) {
            logger.debug("Unable to determine lights out cycle start for experiment {}, procedure {}",
                         dccExperimentDTO.getExperimentId(), dccExperimentDTO.getProcedureId());
        }

        // Calculate the lights out metadata procedure
        return lightsOut;
    }

    /**
     * Parse a date string
     *
     * @param value the date time value to try to parse
     * @return string representing the date time
     */
    public String getParsedIncrementValue(String value) {
        String parsedValue = value;

        Date d = DateUtils.parseIncrementValue(logger, value);
        if (d != null) {
            // Process the date into the expected format
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            parsedValue = format.format(d);
        }

        return parsedValue;
    }

    /**
     * Get the decimal number representing the (fractional) number of hours before/after lights out
     * Lights out for Europhenome is defined as
     * 7PM (19:00:00) for MRC, WTSI, ICS,
     * 6PM (18:00:00) for HMGU
     * <p/>
     * Calculate the appropriate lights out time, then date diff the timepoint to determine the relative
     * time interval, then convert that to decimal hours
     */
    public float convertEurophenomeTimepoint(DccExperimentDTO dccExperimentDTO, String input) throws ParseException {

        Long lightsOut = dccExperimentDTO.getDateOfExperiment().getTime();
        switch (dccExperimentDTO.getPhenotypingCenter()) {
            case "HMGU"://1: // HMGU
                // Lights out for EUMODIC HMGU  is 18:00
                lightsOut += 18 * 60 * 60 * 1000;
                break;
            case "MRC"://2: // MRC
            case "WTSI"://3: // WTSI
            case "ICS"://4: // ICS
                // Lights out for EUMODIC MRC, WTSI, ICS  is 19:00
                lightsOut += 19 * 60 * 60 * 1000;
                break;
            default: // CMHD never sent calorimetry, so this should never happen
                break;
        }

        SimpleDateFormat format     = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
        Long             measuredAt = format.parse(input).getTime();

        return (measuredAt - lightsOut) / 1000.0f / 60.0f / 60.0f;
    }
}