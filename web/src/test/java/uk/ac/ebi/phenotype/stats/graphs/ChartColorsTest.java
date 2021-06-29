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

package uk.ac.ebi.phenotype.stats.graphs;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.phenotype.chart.ChartColors;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ChartColorsTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void test() {

		List<String> colorStrings=ChartColors.getFemaleMaleColorsRgba(0.7);
		logger.debug(StringUtils.join(colorStrings, ", "));
		assertTrue(colorStrings.size()>3);
	}
}