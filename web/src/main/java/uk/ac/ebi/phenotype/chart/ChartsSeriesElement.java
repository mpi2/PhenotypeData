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

import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


//to hold a highchart section of data with properties together so we can inject it into highcharts via Java JSON objects
//name: 'Observation',
//color: 'rgba(30, 151, 50,0.7)',
//type: 'scatter',
//data: [
//[2, 3.26],
//[2, 2.7],
//[2, 2.66],
//[3, 2.35],
//[3, 2.73],
//[3, 2.55],
//[3, 2.92]
//]
public class ChartsSeriesElement {

	private String       name;
	private String       colorString;
	private String       chartTypeString;
	private JSONArray    boxPlotArray;
	private JSONArray    boxPlotOutliersArray;
	private SexType      sexType;
	private ZygosityType zygosityType;

	//to hold original data before being processsed to chart objects
	List<Float> originalData = new ArrayList<>();

	// to record the column of this data if say for boxplots we need this to determine how many other [] arrays to add before us
	int column;

	public List<Float> getOriginalData() {
		return originalData;
	}

	public void setOriginalData(List<Float> originalData) {
		this.originalData = originalData;
	}

	public JSONArray getBoxPlotOutliersArray() {
		return boxPlotOutliersArray;
	}

	public void setBoxPlotOutliersArray(JSONArray boxPlotOutliersArray) {
		this.boxPlotOutliersArray = boxPlotOutliersArray;
	}

	public ZygosityType getZygosityType() {
		return zygosityType;
	}

	public void setZygosityType(ZygosityType zygosityType) {
		this.zygosityType = zygosityType;
	}

	String getControlOrZygosityString() {
		if (zygosityType == null) {
			return "WT";
		} else {
			return zygosityType.getShortName();
		}

	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public SexType getSexType() {
		return sexType;
	}

	public void setSexType(SexType sexType) {
		this.sexType = sexType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColorString() {
		return colorString;
	}

	public void setColorString(String colorString) {
		this.colorString = colorString;
	}

	public String getChartTypeString() {
		return chartTypeString;
	}

	public void setChartTypeString(String chartTypeString) {
		this.chartTypeString = chartTypeString;
	}

	public JSONArray getBoxPlotArray() {
		return boxPlotArray;
	}

	public void setBoxPlotArray(JSONArray dataArray) {
		this.boxPlotArray = dataArray;
	}

	@Override
	public String toString() {
		return "ChartsSeriesElement [originalData=" + originalData
				+ ", column=" + column + ", sexType=" + sexType
				+ ", controlOrZygosity=" + zygosityType + ", name=" + name
				+ ", colorString=" + colorString + ", chartTypeString="
				+ chartTypeString + ", dataArray=" + boxPlotArray + "]\n";
	}

}
