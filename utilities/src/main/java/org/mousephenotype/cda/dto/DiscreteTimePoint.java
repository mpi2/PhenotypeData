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
package org.mousephenotype.cda.dto;

import java.util.ArrayList;
import java.util.List;


public class DiscreteTimePoint implements Comparable {

	private int count;// store the number of data points that contributed to
						// this point if the main data is a mean
	private Float discreteTime;
	private Float data;
	private Float stdDeviation;
	private List<Float> errorPair;// if this is the mean of a set we can store
									// the error bars data and other data such
									// as Standard Deviation etc?

	public int getCount() {

		return count;
	}


	public void setCount(int count) {

		this.count = count;
	}


	public List<Float> getErrorPair() {

		return this.errorPair;
	}


	public void setErrorPair(List<Float> errorPair) {

		this.errorPair = errorPair;
	}


	public DiscreteTimePoint(Float discreteTime, Float discreteDataPoint) {

		this.discreteTime = discreteTime;
		this.data = discreteDataPoint;
	}


	public DiscreteTimePoint(Float discreteTime, Float discreteDataPoint, Float standardDeviation) {

		this(discreteTime, discreteDataPoint);
		this.stdDeviation = standardDeviation;
		errorPair = new ArrayList<Float>();
		errorPair.add(this.data - this.stdDeviation);
		errorPair.add(this.data + this.stdDeviation);
	}


	public DiscreteTimePoint(Float discreteTime, Float discreteDataPoint, Float standardDeviation, Integer count) {

		this(discreteTime, discreteDataPoint, standardDeviation);
		this.count = count;
	}


	public Float getDiscreteTime() {

		return discreteTime;
	}


	public void setDiscreteTime(Float discretePoint) {

		this.discreteTime = discretePoint;
	}


	public Float getData() {

		return data;
	}


	public void setData(Float data) {

		this.data = data;
	}


	public Float getStdDeviation() {

		return stdDeviation;
	}


	public String toString() {

		// String myString =
		// DateFormat.getDateInstance(DateFormat.FULL).format(date);
		String stdDev = "No std dev specified";
		if (stdDeviation != null) stdDev = stdDeviation.toString();
		return "[" + discreteTime + " ," + data + stdDev + "]";

	}


	@Override
	public int compareTo(Object arg0) {

		DiscreteTimePoint p = (DiscreteTimePoint) arg0;
		if (p.getDiscreteTime() > this.getDiscreteTime()) {
			return -1;
		} else {
			return 1;
		}

	}

}
