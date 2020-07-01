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

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.ProcedureMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.util.*;


/**
 * Created by mrelac on 09/12/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SeriesParameterObservationUtilsTestConfig.class)
public class SeriesParameterObservationUtilsTest extends TestCase {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private SeriesParameterObservationUtils utils;

    @PostConstruct
   	public void initialise() {
		utils = new SeriesParameterObservationUtils();
   	}

   	@Test
   	public void testTimeStringParsing() {
   		List<String> variousTimeValues = Arrays.asList(
				"2012-12-12T12:12:00Z",     "2012-12-12T12:12:00-00:00", "2012-12-12T12:12:00-0000", "2012-12-12T12:12:00+00:00",
				"2012-12-12T12:12:00+0000", "2012-12-12T12:12:00Z",      "2012-12-12 12:12:00Z",     "2012-12-12T12:12:00",
				"2012-12-12 12:12:00",      "2012-12-12 12:12");

   		// The hardcoded format in time series observation is
   		// SimpleDateFormat pStatMap = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   		String finalDateString = "2012-12-12 12:12:00";

   		// All date times in the list should parse to this date
   		Calendar mydate = Calendar.getInstance();
   		mydate.set(Calendar.YEAR, 2012);
   		mydate.set(Calendar.MONTH, Calendar.DECEMBER);
   		mydate.set(Calendar.DAY_OF_MONTH, 12);
   		mydate.set(Calendar.HOUR_OF_DAY, 12);
   		mydate.set(Calendar.MINUTE, 12);
   		mydate.set(Calendar.SECOND, 00);
   		mydate.set(Calendar.MILLISECOND, 0);
   		mydate.set(Calendar.ZONE_OFFSET, 0);
   		Date constantDate = mydate.getTime();

   		System.out.println("Constant date is " + constantDate.toString());

   		for (String value : variousTimeValues) {

   			logger.debug("Testing date format: " + value);

   			String testDateString = utils.getParsedIncrementValue(value);
   			assertTrue(testDateString.equals(finalDateString));
   		}

   		for (String value : variousTimeValues) {

   			String testDateString = utils.getParsedIncrementValue(value);
   			assertTrue(testDateString.equals(finalDateString));
   		}


   		//Test specific parse datetime "2015-10-01T09:37:21-1000"
   		String testDateString = utils.getParsedIncrementValue("2015-10-01T09:37:21-1000");
   		assertTrue(testDateString.equals("2015-10-01 20:37:21"));


   		List<String> passThroughValues = Arrays.asList("-4.16", "100.0", "07:00pm", "2015-12-12");

   		for (String value : passThroughValues) {
   			System.out.println("Testing pass through value: " + value);
   			String testValue = utils.getParsedIncrementValue(value);
   			assertTrue(testValue.equals(value));
   		}
   	}

   	@Test
   	public void testGetDiscretePointIMPC_CAL() {

   		Calendar dateOfExperimentCal = Calendar.getInstance();
		dateOfExperimentCal.set(Calendar.YEAR, 2014);
		dateOfExperimentCal.set(Calendar.MONTH, Calendar.JANUARY);
		dateOfExperimentCal.set(Calendar.DAY_OF_MONTH, 28);
		dateOfExperimentCal.set(Calendar.HOUR_OF_DAY, 00);
		dateOfExperimentCal.set(Calendar.MINUTE, 00);
		dateOfExperimentCal.set(Calendar.SECOND, 00);
		dateOfExperimentCal.set(Calendar.MILLISECOND, 0);
		dateOfExperimentCal.set(Calendar.ZONE_OFFSET, 0);
		Date dateOfExperiment = dateOfExperimentCal.getTime();

		List<ProcedureMetadata> procedureMetadataList = new ArrayList<>();
   		ProcedureMetadata procedureMetadata = new ProcedureMetadata();
   		procedureMetadata.setParameterID("IMPC_CAL_010_003");
   		procedureMetadata.setValue("2014-01-28T17:00:00+0000");
		procedureMetadataList.add(procedureMetadata);

   		DccExperimentDTO dccExperiment = new DccExperimentDTO();
		dccExperiment.setDatasourceShortName("IMPC");
   		dccExperiment.setDateOfExperiment(dateOfExperiment);
   		dccExperiment.setProcedureId("IMPC_CAL_003_001");
		dccExperiment.setPhenotypingCenter("GMC");
		dccExperiment.setExperimentId("testGetDiscretePointIMPC_CAL");

   		// Test the increment value for time point in the next day for GMC
   		String incrementValue = "2014-01-28T12:15:00+0000";
		Float discreteTimePoint = utils.convertTimepoint(incrementValue, dccExperiment, procedureMetadataList);


   		System.out.println("Increment value " + incrementValue);
   		System.out.println("Discrete time point " + discreteTimePoint);

		dccExperiment.setProcedureId("IMPC_CAL_010_003");
		dccExperiment.setPhenotypingCenter("TCP");

		// Test the increment value for time point in the next day for TCP
		incrementValue = "2016-04-04T19:00:00Z";
        discreteTimePoint = utils.convertTimepoint(incrementValue, dccExperiment, procedureMetadataList);
   	}

   	@Test
   	public void testGetLightsOut() throws ParseException {

   		// Date of experiment
   		Calendar dateOfExperimentCal = Calendar.getInstance();
   		dateOfExperimentCal.set(2016, 03, 04); // Month is 0 based, so 3 == April
		Date dateOfExperiment = dateOfExperimentCal.getTime();

   		// Set up the metadata
		List<ProcedureMetadata> procedureMetadataList = new ArrayList<>();
   		ProcedureMetadata procedureMetadata = new ProcedureMetadata();
   		procedureMetadata.setParameterID("IMPC_CAL_010_003");
   		procedureMetadata.setValue("2016-04-04T19:00:00Z");
		procedureMetadataList.add(procedureMetadata);

		DccExperimentDTO dccExperiment = new DccExperimentDTO();
		dccExperiment.setDatasourceShortName("IMPC");
		dccExperiment.setDateOfExperiment(dateOfExperiment);
		dccExperiment.setProcedureId("IMPC_CAL_003_001");
		dccExperiment.setPhenotypingCenter("TCP");
		dccExperiment.setExperimentId("testGetLightsOut");

        Long lightsOut = utils.getLightsOut(dccExperiment, procedureMetadataList);
      		System.out.println("Should be 1459796400000 which is GMT Mon, 04 Apr 2016 19:00:00 GMT : " + lightsOut);
      		assertTrue(lightsOut == 1459796400000L);

        dccExperiment.setPhenotypingCenter("HMGU");
        lightsOut = utils.getLightsOut(dccExperiment, procedureMetadataList);
      		System.out.println("Should be 1459796400000 which is GMT Mon, 04 Apr 2016 19:00:00 GMT : " + lightsOut);
      		assertTrue(lightsOut == 1459796400000L);

		float v = utils.convertTimepoint("2016-04-04T13:42:46+00:00", dccExperiment, procedureMetadataList);
		System.out.println("2016-04-04T13:42:46+00:00 Should be between -6 and -5 : " + v);
		assertTrue(v > -6.0f && v < -5.0f);

		v = utils.convertTimepoint("2016-04-04T19:34:46+00:00", dccExperiment, procedureMetadataList);
   		System.out.println("2016-04-04T19:34:46+00:00 Should be between 0 and 1 : " + v);
   		assertTrue(v > 0.0f && v < 1.0f);


//        dccExperiment.setDatasourceShortName("EuroPhenome");
//        dccExperiment.setPhenotypingCenter("WTSI");
//        lightsOut = utils.getLightsOut(dccExperiment, procedureMetadataList);
//        System.out.println("Should be 1459796400000 which is GMT Mon, 04 Apr 2016 19:00:00 GMT : " + lightsOut);
//        assertTrue(lightsOut == 1459796400000L);
//
//        dccExperiment.setDatasourceShortName("EuroPhenome");
//        dccExperiment.setPhenotypingCenter("HMGU");
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
//        dccExperiment.setDateOfExperiment(format.parse("2016-04-04 00:00"));
//
//        lightsOut = utils.getLightsOut(dccExperiment, procedureMetadataList);
//        System.out.println("Should be 1459796400000 which is GMT Mon, 04 Apr 2016 19:00:00 GMT : " + lightsOut);
//        assertTrue(lightsOut == 1459796400000L);
//
//
//        v = utils.convertTimepoint("2016-04-04T13:42:46+00:00", dccExperiment, procedureMetadataList);
//        System.out.println("2016-04-04T13:42:46+00:00 Should be between -6 and -5 : " + v);
//        assertTrue(v > -6.0f && v < -5.0f);
//
//        v = utils.convertTimepoint("2016-04-04T19:34:46+00:00", dccExperiment, procedureMetadataList);
//        System.out.println("2016-04-04T19:34:46+00:00 Should be between 0 and 1 : " + v);
//        assertTrue(v > 0.0f && v < 1.0f);
    }
}