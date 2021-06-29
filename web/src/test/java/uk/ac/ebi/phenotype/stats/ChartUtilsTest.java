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
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import uk.ac.ebi.phenotype.chart.ChartUtils;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ChartUtilsTest {

	@Test
	public void testGetDecimalPlaces() {
		ExperimentDTO experiment=new ExperimentDTO();
		Set<ObservationDTO> controls=new HashSet<>();
		ObservationDTO ob1=new ObservationDTO();
		ob1.setDataPoint(new Float(1));
		controls.add(ob1);
		experiment.setControls(controls);
		int numberOfDecimalPlaces=ChartUtils.getDecimalPlaces(experiment);
		assertTrue(numberOfDecimalPlaces==1);
		ObservationDTO ob2=new ObservationDTO();
		ob2.setDataPoint(new Float(100.0003));
		controls.add(ob2);

		int numberOfDecimalPlaces2=ChartUtils.getDecimalPlaces(experiment);
		assertTrue(numberOfDecimalPlaces2==4);
	}

	@Test
	public void testGetDecimalAdjustedFloat() {
		int numberOfDecimals=2;
		Float n1=new Float(10.7777);
		Float result=ChartUtils.getDecimalAdjustedFloat(n1, numberOfDecimals);
		System.out.println("adjuted float="+result);
		assertTrue(new Float(10.78).equals(result));

	}
}