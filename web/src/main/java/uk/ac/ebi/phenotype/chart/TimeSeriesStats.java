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
package uk.ac.ebi.phenotype.chart;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.mousephenotype.cda.dto.DiscreteTimePoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;


public class TimeSeriesStats {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	public List<DiscreteTimePoint> getMeanDataPoints(
			List<DiscreteTimePoint> timeSeriesDataForLine) {

		List<DiscreteTimePoint> means = new ArrayList<>();
		// Add the data from the array
		SortedMap<Float, List<Float>> timeMap = new TreeMap<Float, List<Float>>();
		for (DiscreteTimePoint timePoint : timeSeriesDataForLine) {
			if (timeMap.containsKey(timePoint.getDiscreteTime())) {
				timeMap.get(timePoint.getDiscreteTime()).add(
						timePoint.getData());
			} else {
				List<Float> dataPointsFloats = new ArrayList<Float>();
				dataPointsFloats.add(timePoint.getData());
				timeMap.put(timePoint.getDiscreteTime(), dataPointsFloats);
			}

		}
		log.debug("time map size=" + timeMap.keySet().size());

		for (Float time : timeMap.keySet()) {
			DescriptiveStatistics stats = new DescriptiveStatistics();
			log.debug("time=" + time + " number of points="
					+ timeMap.get(time).size());
			for (Float data : timeMap.get(time)) {
				stats.addValue(data);
			}

			// Compute some statistics
			double mean = stats.getMean();
			double std = stats.getStandardDeviation();
			DiscreteTimePoint meanDataTimePoint = new DiscreteTimePoint(time,
					(float) mean, (float) stats.getStandardDeviation(), timeMap.get(time).size());
			List<Float> errorPair = new ArrayList<>();

			Float lower = (float) (mean - std);
			Float higher = (float) (mean + std);
			errorPair.add(lower);
			errorPair.add(higher);
			log.debug("stddev=" + std + " lower=" + lower + " higher=" + higher);
			meanDataTimePoint.setErrorPair(errorPair);
			// meanDataTimePoint.s
			means.add(meanDataTimePoint);

		}

		return means;
	}
}