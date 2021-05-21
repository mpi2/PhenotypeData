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
import uk.ac.ebi.phenotype.chart.ColorCodingPalette;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ColorCodingPaletteTest {

	List<Double> generateRamdomPvalues(double min, double max, int capacity) {
		List<Double> l = new ArrayList<Double>();
		for (int i=0; i<capacity; i++) {
			l.add(Math.random());
		}
		return l;
	}

	@Test
	public void testPaletteOne() {

		ColorCodingPalette ccp = new ColorCodingPalette();
		for (int i=0; i<10000; i++) {
			List<Double> pValues = generateRamdomPvalues(0,0, i+1);
			double scale = 0;
			double minimalPValue = 0.005;
			for (int maxColorIndex = 3; maxColorIndex <= 9; maxColorIndex++) {
				ccp.generateColors(pValues, maxColorIndex, scale, minimalPValue);
//				System.out.println(maxColorIndex + " " + ccp.getPalette().size() + " " + ccp.getPalette().get(1).length);
				assertTrue(ccp.getPalette().size() == 3 && ccp.getPalette().get(1).length == maxColorIndex);
//				System.out.println(pValues.size() + " " + ccp.getColors().length);
				assertTrue(ccp.getColors().length > 0 && ccp.getColors().length == pValues.size());
			}
		}
	}
}