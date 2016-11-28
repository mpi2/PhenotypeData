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

import org.mousephenotype.cda.db.pojo.*;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ProcedureMetadata;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameter;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.SeriesParameterValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by mrelac on 25/11/2016.
 */
public class SeriesParameterObservationUtils {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Observation createSeriesParameterObservation(DccExperimentDTO dccExperimentDTO, SeriesParameter seriesParameter,
														SeriesParameterValue seriesParameterValue, List<ProcedureMetadata> procedureMetadataList,
														CdaSqlUtils cdaSqlUtils) {

        String parameterStableId = seriesParameter.getParameterID();

        // Get the parameter data type.
        String          incrementValue  = seriesParameterValue.getIncrementValue();
        String          simpleValue     = seriesParameterValue.getValue();
        ObservationType observationType = cdaSqlUtils.computeObservationType(parameterStableId, simpleValue);
        String[]        units           = cdaSqlUtils.computeParameterUnits(parameterStableId);

        // Get parameterStatus.
        String parameterStatus = seriesParameter.getParameterStatus();
        if (parameterStatus == null && seriesParameterValue.getIncrementStatus() != null) {
            parameterStatus = seriesParameterValue.getIncrementStatus();
        }

        // Build the Observation

        Observation observation = null;
        if (incrementValue.contains("-") && (incrementValue.contains(" ") || incrementValue.contains("T"))) {
            String discreteTimepoint = Float.toString(convertTimepoint(incrementValue, dccExperimentDTO, procedureMetadataList));

            // Need to parse value into correct format before sending to the observation creator
            String parsedIncrementValue = getParsedIncrementValue(incrementValue);
// fixme fixme fixme
//            observation = createTimeSeriesObservationWithOriginalDate(observationType, simpleValue, discreteTimepoint, parsedIncrementValue, units[0], parameter, specimen, datasource, currentExperiment, parameterStatus);
        } else {
// fixme fixme fixme
//            observation = observationDAO.createObservation(observationType, simpleValue, incrementValue, units[0], parameter, specimen, datasource, currentExperiment, parameterStatus);
        }
		if (dccExperimentDTO.getRawProcedureStatus()!= null) {
            observation.setMissingFlag(true);
        }

        return observation;
    }


    /**
   	 * Convert a time unit from absolute date to "time since lights out" as required
   	 * by the SOP.
   	 * <p/>
   	 * input comes in as a full time stamp string like 2009-01-22 14:05:00
   	 *
	 * @param input             the absolute date/time to convert
	 * @param dccExperimentDTO the dcc experiment instance
   	 * @param procedureMetadataList the procedure metadata list
   	 * @return float representing the converted time between -5 hours and +23 hours
   	 */
	private float convertTimepoint(String input, DccExperimentDTO dccExperimentDTO, List<ProcedureMetadata> procedureMetadataList) {
   		float retFloat=0.0f;

   		// If there is a +0000 at the end of the timestamp, we need to put it
   		// into +00:00 expected format
   		String[] parts = input.split("\\+");
   		if (parts.length>1) {
   			if ( ! parts[1].contains(":")) {
   				String newTimezone = parts[1].substring(0,2) + ":" + parts[1].substring(2, 4);
   				input = parts[0] + "+" + newTimezone;
   			}
   		}

   		try {
   			switch (dccExperimentDTO.getDatasourceShortName()) {
   				case "IMPC":

   					// Calculate the date difference between the measured value and lights out cycle
                       Date d = parseIncementValue(input);
                       Long measuredAt = d.getTime();
					Long startOfDay = dccExperimentDTO.getDateOfExperiment().getTime();
					Long lightsOut = dccExperimentDTO.getProcedureId().contains("IMPC_CAL") ? getLightsOut(dccExperimentDTO, procedureMetadataList) : null;

   					Long normalizedMillis = measuredAt - startOfDay;
   					if(lightsOut != null) {
   						normalizedMillis = measuredAt - lightsOut;
   					}

   					retFloat = normalizedMillis / 1000.0f / 60.0f / 60.0f;

   					break;

   				case "EuroPhenome":
   					retFloat = convertEurophenomeTimepoint(dccExperimentDTO, input);
   					break;
   			}
   		} catch (Exception e) {
   			logger.warn("Unable to convert IMPC timepoint for exp {} proc {}", dccExperimentDTO.getExperimentId(), dccExperimentDTO.getProcedureId(), e);
   		}
   		return retFloat;
   	}


    /**
   	 * Method to parse various increment value date time formats
   	 * Supported formats:
   	 *   2012-12-12T12:12:12+00:00
   	 *   2012-12-12T12:12:12+0000
   	 *   2012-12-12T12:12:12Z
   	 *   2012-12-12 12:12:12Z
   	 *   2012-12-12T12:12:12
   	 *   2012-12-12 12:12:12
   	 *
   	 *   Unsuccessful parse returns null
   	 * @param value date, or null if parse unsuccessful
   	 * @return
   	 */
   	public Date parseIncementValue(String value) {
   		Date                   d                = null;
   		List<SimpleDateFormat> supportedFormats = new ArrayList<>();
   		supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));
   		supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXX"));
   		supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"));
   		supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
   		supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ssZ"));
   		supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
   		supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss'Z'"));
   		supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
   		supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss"));
   		supportedFormats.add(new SimpleDateFormat("yyyy-MM-dd' 'HH:mm"));

   		for (SimpleDateFormat format : supportedFormats) {
   			try {
   				logger.debug("Testing format: {}", format.toPattern());
   				d = format.parse(value);
   			} catch (ParseException e) {
   				// Not this format, try the next one
   				continue;
   			}
   			// If the parse is successful, stop processing the rest
   			logger.debug("Parsed datestring {} using format {}: {}", value, format.toPattern(), d.toString());

   			break;
   		}

   		return d;
   	}



    /**
   	 * Gets the lights out cycle start time of the passed in experiment
   	 * the lights out cycle for IMPC Calorimetry is one of three procedureMetadata
   	 * IMPC_CAL_003_001,2,3 with three different date/time formats
   	 * See:
   	 *   https://www.mousephenotype.org/impress/parameters/86/7
   	 *   https://www.mousephenotype.org/impress/parameters/153/7
   	 *   https://www.mousephenotype.org/impress/parameters/240/7
   	 */
   	public Long getLightsOut(DccExperimentDTO dccExperimentDTO, List<ProcedureMetadata> procedureMetadataList) {

   		Long lightsOut = null;

   		Map<String, SimpleDateFormat> knownPatterns = new HashMap<>();
   		knownPatterns.put("IMPC_CAL_010_001", new SimpleDateFormat("HH:mm' 'a"));
   		knownPatterns.put("IMPC_CAL_010_002", new SimpleDateFormat("HH:mm:ss"));
   		knownPatterns.put("IMPC_CAL_010_003", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));

   		final List<String> lightsOutParameters =  new ArrayList<>(knownPatterns.keySet());

   		try {
   			for (ProcedureMetadata metadata : procedureMetadataList) {

   				// Find the lights out metadata parameter
   				if (lightsOutParameters.contains(metadata.getParameterID())) {

   					// This is the paramter "Time of dark cycle start"

   					SimpleDateFormat format = knownPatterns.get(metadata.getParameterID());
   					String value = metadata.getValue();

                       if (metadata.getParameterID().equals("IMPC_CAL_010_003")) {
                           Date d = parseIncementValue(value);
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
   	 * Get the decimal number representing the (fractional) number of hours before/after lights out
   	 * Lights out for Europhenome is defined as
   	 *   7PM (19:00:00) for MRC, WTSI, ICS,
   	 *   6PM (18:00:00) for HMGU
   	 *
   	 * Calculate the appropriate lights out time, then date diff the timepoint to determine the relative
   	 * time interval, then convert that to decimal hours
   	 *
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


    /**
   	 * Parse a date string
   	 * @param value the date time value to try to parse
   	 * @return string representing the date time
   	 */
   	public String getParsedIncrementValue(String value) {
   		String parsedValue = value;

   		Date d = parseIncementValue(value);
   		if (d!=null) {
   			// Process the date into the expected format
   			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
   			parsedValue = format.format(d);
   		}

   		return parsedValue;
   	}




    public Observation createTimeSeriesObservationWithOriginalDate(ObservationType observationType, String firstDimensionValue, String secondDimensionValue, String actualTimepoint, String secondDimensionUnit, Parameter parameter, BiologicalSample sample, Datasource datasource, org.mousephenotype.cda.db.pojo.Experiment experiment, String parameterStatus) {
        TimeSeriesObservation obs = new TimeSeriesObservation();
        if(firstDimensionValue != null && !firstDimensionValue.equals("null") && !firstDimensionValue.equals("")) {
            obs.setDataPoint(Float.parseFloat(firstDimensionValue));
        } else {
            obs.setMissingFlag(true);
        }

        Date actualTimePoint = experiment != null?experiment.getDateOfExperiment():null;
        if(actualTimepoint.contains("-")) {
            SimpleDateFormat pStatMap = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                actualTimePoint = pStatMap.parse(actualTimepoint);
            } catch (ParseException var15) {
                actualTimePoint = experiment != null?experiment.getDateOfExperiment():null;
            }
        }

        obs.setTimePoint(secondDimensionValue, actualTimePoint, secondDimensionUnit);
        obs.setDatasource(datasource);
        obs.setExperiment(experiment);
        obs.setParameter(parameter);
        obs.setSample(sample);
        obs.setType(observationType);
        obs.setParameterStableId(parameter.getStableId());
        if(parameterStatus != null) {
            Map pStatMap1 = getParameterStatusAndMessage(parameterStatus);
            obs.setParameterStatus((String)pStatMap1.get("status"));
            obs.setParameterStatusMessage((String)pStatMap1.get("message"));
            obs.setMissingFlag(true);
        }

        return obs;
    }


    public static Map<String, String> getParameterStatusAndMessage(String parameterStatus) {
        HashMap pStatusMap = new HashMap();
        pStatusMap.put("message", (Object)null);
        pStatusMap.put("status", parameterStatus);
        if(parameterStatus != null) {
            String code;
            String message;
            if(parameterStatus.contains(":")) {
                message = parameterStatus.substring(parameterStatus.indexOf(":") + 1, parameterStatus.length()).trim();
                pStatusMap.put("message", message);
                code = parameterStatus.substring(0, parameterStatus.indexOf(":"));
                pStatusMap.put("status", code);
            } else if(parameterStatus.contains("?")) {
                message = parameterStatus.substring(parameterStatus.indexOf("?") + 1, parameterStatus.length()).trim();
                pStatusMap.put("message", message);
                code = parameterStatus.substring(0, parameterStatus.indexOf("?"));
                pStatusMap.put("status", code);
            }

            if(((String)pStatusMap.get("status")).length() > 50) {
                pStatusMap.put("status", ((String)pStatusMap.get("status")).substring(0, 45) + "...");
            }
        }

        return pStatusMap;
    }





}
