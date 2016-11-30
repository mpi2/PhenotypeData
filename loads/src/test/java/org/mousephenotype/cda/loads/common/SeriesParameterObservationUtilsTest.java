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
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

/**
 * Created by mrelac on 30/11/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class SeriesParameterObservationUtilsTest extends TestCase {

	private SeriesParameterObservationUtils utils;

    @PostConstruct
   	public void initialise() {
		utils = new SeriesParameterObservationUtils();
//   		loader.datasourceName = "IMPC";
   	}

//   	@Test
//   	public void testLoadPipelineByStableId() throws Exception {
//   		String pipeline = "ESLIM_001";
//
//   		loader.loadPipelineByStableId(pipeline);
//   		Map<String, Procedure> procs = loader.getCdaProcedures();
//   		assertTrue(procs.containsKey("ESLIM_019_001"));
//
//   		pipeline = "ESLIM_002";
//   		loader.resetCdaProcedures();
//   		loader.loadPipelineByStableId(pipeline);
//   		procs = loader.getCdaProcedures();
//   		assertTrue(procs.containsKey("ESLIM_019_001"));
//
//   		pipeline = "IMPC_001";
//   		loader.resetCdaProcedures();
//   		loader.loadPipelineByStableId(pipeline);
//   		procs = loader.getCdaProcedures();
//   		assertFalse(procs.containsKey("ESLIM_019_001"));
//
//   		pipeline = "ESLIM_001";
//   		loader.resetCdaProcedures();
//   		loader.loadPipelineByStableId(pipeline);
//   		procs = loader.getCdaProcedures();
//   		assertFalse(procs.containsKey("IMPC_BWT_001"));
//
//   	}

//   	@Test
//   	public void testGetProcedureGroup() {
//
//   		Map<String, String> groups = new HashMap<>();
//
//   		groups.put("IMPC_ELZ_001", "IMPC_ELZ");
//   		groups.put("IMPC_GEO_003", "IMPC_GEO");
//   		groups.put("IMPC_GEO_002", "IMPC_GEO");
//   		groups.put("IMPC_ABR_001", "IMPC_ABR");
//   		groups.put("ESLIM_008_001", "ESLIM_008");
//
//   		for (String key : groups.keySet()) {
//   			System.out.println("Checking key: " + key + ", Should be group: " + groups.get(key));
//
//   			assertTrue(groups.get(key).equals(loader.getProcedureGroup(key)));
//   		}
//
//   	}

//   	@Test
//   	public void testGetDiscretePoint() {
//
//   		loader.datasourceName = "IMPC";
//
//   		Calendar dateOfExperiment = Calendar.getInstance();
//   		dateOfExperiment.set(Calendar.YEAR, 2015);
//   		dateOfExperiment.set(Calendar.MONTH, Calendar.JUNE);
//   		dateOfExperiment.set(Calendar.DAY_OF_MONTH, 23);
//   		dateOfExperiment.set(Calendar.HOUR_OF_DAY, 00);
//   		dateOfExperiment.set(Calendar.MINUTE, 00);
//   		dateOfExperiment.set(Calendar.SECOND, 00);
//   		dateOfExperiment.set(Calendar.MILLISECOND, 0);
//   		dateOfExperiment.set(Calendar.ZONE_OFFSET, 0);
//   		/*
//           "data_point": 60.47,
//           "time_point": "2015-06-23 14:15:03.0",
//           TODO: FIX THIS "discrete_point": -4.74917,
//   		 */
//
//   		ProcedureMetadata p = new ProcedureMetadata();
//   		p.setParameterID("IMPC_CAL_010_002");
//   		p.setValue("20:00:00");
//
//   		List<ProcedureMetadata> procedureMetaData = new ArrayList<>();
//   		procedureMetaData.add(p);
//
//   		org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure procedure = new org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure();
//   		procedure.setProcedureMetadata(procedureMetaData);
//
//   		Experiment experiment = new Experiment();
//   		experiment.setDateOfExperiment(dateOfExperiment);
//   		experiment.setProcedure(procedure);
//
//   		// Test the increment value for time point in the next day
//   		String incrementValue = "2015-06-23 14:15:03";
//   		String phenotypeingCenter = "NING";
//   		String procedureName = "IMPC_CAL_004_001";
//
//   		Float discreteTimePoint = loader.convertTimepoint(incrementValue, phenotypeingCenter, experiment, procedureName);
//   		System.out.println("Increment value " + incrementValue);
//   		System.out.println("Discrete time point " + discreteTimePoint);
//
//   		assertTrue(discreteTimePoint > -5.0f && discreteTimePoint < -4.0f);
//   	}


//   	@Test
//   	public void testGetDiscretePointAgain() {
//
//   		loader.datasourceName = "IMPC";
//
//   		Calendar dateOfExperiment = Calendar.getInstance();
//   		dateOfExperiment.set(Calendar.YEAR, 2014);
//   		dateOfExperiment.set(Calendar.MONTH, Calendar.JANUARY);
//   		dateOfExperiment.set(Calendar.DAY_OF_MONTH, 28);
//   		dateOfExperiment.set(Calendar.HOUR_OF_DAY, 00);
//   		dateOfExperiment.set(Calendar.MINUTE, 00);
//   		dateOfExperiment.set(Calendar.SECOND, 00);
//   		dateOfExperiment.set(Calendar.MILLISECOND, 0);
//   		dateOfExperiment.set(Calendar.ZONE_OFFSET, 0);
//
//   		ProcedureMetadata p = new ProcedureMetadata();
//   		p.setParameterID("IMPC_CAL_010_003");
//   		p.setValue("2014-01-28T17:00:00+0000");
//
//   		List<ProcedureMetadata> procedureMetaData = new ArrayList<>();
//   		procedureMetaData.add(p);
//
//   		org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure procedure = new org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure();
//   		procedure.setProcedureMetadata(procedureMetaData);
//
//   		Experiment experiment = new Experiment();
//   		experiment.setDateOfExperiment(dateOfExperiment);
//   		experiment.setProcedure(procedure);
//
//   		// Test the increment value for time point in the next day
//   		String incrementValue = "2014-01-28T12:15:00+0000";
//   		String phenotypeingCenter = "GMC";
//   		String procedureName = "IMPC_CAL_003_001";
//
//   		Float discreteTimePoint = loader.convertTimepoint(incrementValue, phenotypeingCenter, experiment, procedureName);
//   		System.out.println("Increment value " + incrementValue);
//   		System.out.println("Discrete time point " + discreteTimePoint);
//
//   		// Test the increment value for time point in the next day
//   		incrementValue = "2016-04-04T19:00:00Z";
//   		phenotypeingCenter = "TCP";
//   		procedureName = "IMPC_CAL_010_003";
//
//   		discreteTimePoint = loader.convertTimepoint(incrementValue, phenotypeingCenter, experiment, procedureName);
//   		System.out.println("Increment value " + incrementValue);
//   		System.out.println("Discrete time point " + discreteTimePoint);
//
//   	}

//   	@Test
//   	public void testTimeStringParsing() throws ParseException {
//   		List<String> variousTimeValues = Arrays.asList("2012-12-12T12:12:00Z", "2012-12-12T12:12:00-00:00", "2012-12-12T12:12:00-0000", "2012-12-12T12:12:00+00:00", "2012-12-12T12:12:00+0000", "2012-12-12T12:12:00Z", "2012-12-12 12:12:00Z", "2012-12-12T12:12:00", "2012-12-12 12:12:00", "2012-12-12 12:12");
//
//   		// The hardcoded format in time series observation is
//   		// SimpleDateFormat pStatMap = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//   		String finalDateString = "2012-12-12 12:12:00";
//
//   		// All date times in the list should parse to this date
//   		Calendar mydate = Calendar.getInstance();
//   		mydate.set(Calendar.YEAR, 2012);
//   		mydate.set(Calendar.MONTH, Calendar.DECEMBER);
//   		mydate.set(Calendar.DAY_OF_MONTH, 12);
//   		mydate.set(Calendar.HOUR_OF_DAY, 12);
//   		mydate.set(Calendar.MINUTE, 12);
//   		mydate.set(Calendar.SECOND, 00);
//   		mydate.set(Calendar.MILLISECOND, 0);
//   		mydate.set(Calendar.ZONE_OFFSET, 0);
//   		Date constantDate = mydate.getTime();
//
//   		System.out.println("Constant date is " + constantDate.toString());
//
//   		for (String value : variousTimeValues) {
//
//   			System.out.println("Testing date format: " + value);
//   			Date testDate = loader.parseIncementValue(value);
//   			assertTrue(testDate.compareTo(constantDate) == 0);
//
//   			String testDateString = loader.getParsedIncrementValue(value);
//   			assertTrue(testDateString.equals(finalDateString));
//   		}
//
//   		for (String value : variousTimeValues) {
//
//   			String testDateString = loader.getParsedIncrementValue(value);
//   			assertTrue(testDateString.equals(finalDateString));
//   		}
//
//
//   		//Test specific parse datetime "2015-10-01T09:37:21-1000"
//   		String testDateString = loader.getParsedIncrementValue("2015-10-01T09:37:21-1000");
//   		assertTrue(testDateString.equals("2015-10-01 20:37:21"));
//
//
//   		List<String> passThroughValues = Arrays.asList("-4.16", "100.0", "07:00pm", "2015-12-12");
//
//   		for (String value : passThroughValues) {
//   			System.out.println("Testing pass through value: " + value);
//   			String testValue = loader.getParsedIncrementValue(value);
//   			assertTrue(testValue.equals(value));
//   		}
//
//   	}


//   	@Test
//   	public void testGetLightsOut() {
//
//   		// Date of experiment
//   		Calendar dateOfExperiment = Calendar.getInstance();
//   		dateOfExperiment.set(2016, 03, 04); // Month is 0 based, so 3 == April
//
//   		// Set up the metadata
//   		ProcedureMetadata meta = new ProcedureMetadata();
//   		meta.setParameterID("IMPC_CAL_010_003");
//   		meta.setValue("2016-04-04T19:00:00Z");
//   		List<ProcedureMetadata> listMetadata = new ArrayList<>();
//   		listMetadata.add(meta);
//
//   		org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure procedure = new org.mousephenotype.dcc.exportlibrary.datastructure.core.procedure.Procedure();
//   		procedure.setProcedureMetadata(listMetadata);
//
//
//   		Experiment experiment = new Experiment();
//   		experiment.setDateOfExperiment(dateOfExperiment);
//   		experiment.setProcedure(procedure);
//
//
//   		Long lightsOut = loader.getLightsOut(experiment);
//   		System.out.println("Should be 1459796400000 which is GMT Mon, 04 Apr 2016 19:00:00 GMT : " + lightsOut);
//   		assertTrue(lightsOut == 1459796400000L);
//
//   		float v = loader.convertTimepoint("2016-04-04T13:42:46+00:00", "TCP", experiment, "IMPC_CAL_003");
//   		System.out.println("2016-04-04T13:42:46+00:00 Should be between -6 and -5 : " + v);
//   		assertTrue(v > -6.0f && v < -5.0f);
//
//   		v = loader.convertTimepoint("2016-04-04T19:34:46+00:00", "TCP", experiment, "IMPC_CAL_003");
//   		System.out.println("2016-04-04T19:34:46+00:00 Should be between 0 and 1 : " + v);
//   		assertTrue(v > 0.0f && v < 1.0f);
//
//   	}
}
