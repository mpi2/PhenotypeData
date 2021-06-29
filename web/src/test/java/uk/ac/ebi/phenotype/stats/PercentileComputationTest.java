/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This test class is intended to run healthchecks against the observation table.
 */

package uk.ac.ebi.phenotype.stats;

import org.junit.jupiter.api.Test;
import uk.ac.ebi.phenotype.chart.ChartUtils;
import uk.ac.ebi.phenotype.chart.PercentileComputation;

import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class PercentileComputationTest {

	@Test
	public void testOddNumber(){

		ArrayList<Float> testarray = new ArrayList<>();
		testarray.add((float)6);
		testarray.add((float)7);
		testarray.add((float) 15);
		testarray.add((float)36);
		testarray.add((float)39);
		testarray.add((float)40);
		testarray.add((float)41);
		testarray.add((float)42);
		testarray.add((float)43);
		testarray.add((float)47);
		testarray.add((float)49);
		PercentileComputation pc = new PercentileComputation(testarray);
		assertTrue(pc.getLowerQuartile() == 25.5);
		assertTrue(pc.getUpperQuartile() == 42.5);
	}

	@Test
	public void testEvenNumber(){
		ArrayList<Float> testarray = new ArrayList<>();
		testarray.add((float)7);
		testarray.add((float) 15);
		testarray.add((float)36);
		testarray.add((float)39);
		testarray.add((float)40);
		testarray.add((float)41);
		PercentileComputation pc = new PercentileComputation(testarray);
		assertTrue(pc.getLowerQuartile() == 15.0);
		assertTrue(pc.getUpperQuartile() == 40.0);
	}

	@Test
	public void testNumbersFromHugh(){
		ArrayList<Float> testarray = new ArrayList<>();
		testarray.add((float) 0.3441);
		testarray.add((float) 0.3675);
		testarray.add((float) 0.4842);
		testarray.add((float) 0.3074);
		testarray.add((float) 0.489);
		testarray.add((float) 0.3188);
		testarray.add((float) 0.385);
		PercentileComputation pc = new PercentileComputation(testarray);
		assertTrue((float)ChartUtils.getDecimalAdjustedFloat(pc.getLowerQuartile(),4) == (float)0.3314);
		assertTrue((float)ChartUtils.getDecimalAdjustedFloat(pc.getUpperQuartile(),4) == (float)0.4346);
		assertTrue((float)ChartUtils.getDecimalAdjustedFloat(pc.getMedian(),4) == (float)0.3675);
	}
}
