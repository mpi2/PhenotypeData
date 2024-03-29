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
package uk.ac.ebi.phenotype.util;

import uk.ac.ebi.phenotype.chart.ChartColors;

import java.util.List;

public class PhenotypeGeneSummaryDTO {


	private String pieChartCode;
	private boolean display;

	private String malePercentage;
	private String femalePercentage;
	private String totalPercentage;

	private int maleGenesTested;
	private int femaleGenesTested;
	private int totalGenesTested;

	private int maleGenesAssociated;
	private int femaleGenesAssociated;
	private int totalGenesAssociated;

	private int femaleOnlyNumber; // with phenotypeL
	private int maleOnlyNumber;
	private int bothNumber;


	public String getMalePercentage() {
		return malePercentage;
	}
	public void setMalePercentage(float malePercentage) {
		this.malePercentage = String.format("%.2f", malePercentage);
	}
	public String getFemalePercentage() {
		return femalePercentage;
	}
	public void setFemalePercentage(float femalePercentage) {
		this.femalePercentage = String.format("%.2f", femalePercentage);
	}
	public String getTotalPercentage() {
		return totalPercentage;
	}
	public void setTotalPercentage(float totalPercentage) {
		this.totalPercentage = String.format("%.2f", totalPercentage);
	}
	public int getMaleGenesTested() {
		return maleGenesTested;
	}
	public void setMaleGenesTested(int maleGenesTestes) {
		this.maleGenesTested = maleGenesTestes;
	}
	public int getFemaleGenesTested() {
		return femaleGenesTested;
	}
	public void setFemaleGenesTested(int femaleGenesTestes) {
		this.femaleGenesTested = femaleGenesTestes;
	}
	public int getTotalGenesTested() {
		return totalGenesTested;
	}
	public void setTotalGenesTested(int totalGenesTestes) {
		this.totalGenesTested = totalGenesTestes;
	}
	public int getMaleGenesAssociated() {
		return maleGenesAssociated;
	}
	public void setMaleGenesAssociated(int maleGenesAssociated) {
		this.maleGenesAssociated = maleGenesAssociated;
	}
	public int getFemaleGenesAssociated() {
		return femaleGenesAssociated;
	}
	public void setFemaleGenesAssociated(int femaleGenesAssociated) {
		this.femaleGenesAssociated = femaleGenesAssociated;
	}
	public int getTotalGenesAssociated() {
		return totalGenesAssociated;
	}
	public void setTotalGenesAssociated(int totalGenesAssociated) {
		this.totalGenesAssociated = totalGenesAssociated;
	}
	public boolean getDisplay() {
		return display;
	}
	public void setDisplay(boolean display) {
		this.display = display;
	}	
	public String getPieChartCode() {
		if (pieChartCode != null){
			return pieChartCode;
		} else {			
			return  getPiechart(getMaleOnlyNumber(), getFemaleOnlyNumber(), getBothNumber(), getTotalGenesTested());
		}
	}
	public void fillPieChartCode(String noPhenotypeLabel) {
		this.pieChartCode = getPiechart(getMaleOnlyNumber(), getFemaleOnlyNumber(), getBothNumber(), getTotalGenesTested());
	}
	public int getFemaleOnlyNumber() {
		return femaleOnlyNumber;
	}
	public void setFemaleOnlyNumber(int femaleOnlyNumber) {
		this.femaleOnlyNumber = femaleOnlyNumber;
	}
	public int getMaleOnlyNumber() {
		return maleOnlyNumber;
	}
	public void setMaleOnlyNumber(int maleOnlyNumber) {
		this.maleOnlyNumber = maleOnlyNumber;
	}
	public int getBothNumber() {
		return bothNumber;
	}
	public void setBothNumber(int bothNumber) {
		this.bothNumber = bothNumber;
	}

	protected String getPiechart(int maleOnly, int femaleOnly, int both, int total){
	
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaOpaque);

		
		String chart = "$(function () { $('#pieChart').highcharts({ "
				 + " chart: { plotBackgroundColor: null, plotShadow: false}, "	
				 + " colors:"+colors+", "
				 + " title: {  text: '' }, "
				 + " credits: { enabled: false }, "
				 + " tooltip: {  pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'},"
				 + " plotOptions: { "
				 	+ "pie: { "
				 		+ "size: 200, "
				 		+ "allowPointSelect: true, "
				 		+ "cursor: 'pointer', "
				 		+ "dataLabels: { enabled: true, format: '<b>{point.name}</b>: {point.percentage:.2f} %', "
				 		+ "style: { color: '#666', width:'60px' }  }  },"
				 	+ "series: {  dataLabels: {  enabled: true, format: '{point.name}: {point.percentage:.2f}%'} }"
				 + " },"
			+ " series: [{  type: 'pie',   name: '',  "
				+ "data: [ { name: 'Female only', y: " + femaleOnly + ", sliced: true, selected: true }, "
					+ "{ name: 'Male only', y: " + maleOnly + ", sliced: true, selected: true }, "
					+ "{ name: 'Both sexes', y: " + both + ", sliced: true, selected: true }, "
					+ "[' Phenotype not present', " + (total- maleOnly - femaleOnly - both) + " ] ]  }]"
		+" }); });";
		
		return chart;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "PhenotypeGeneSummaryDTO [display=" + display + ", malePercentage=" + malePercentage + ", femalePercentage=" + femalePercentage + ", totalPercentage=" + totalPercentage + ", maleGenesTested=" + maleGenesTested + ", femaleGenesTested=" + femaleGenesTested + ", totalGenesTested=" + totalGenesTested + ", maleGenesAssociated=" + maleGenesAssociated + ", femaleGenesAssociated=" + femaleGenesAssociated + ", totalGenesAssociated=" + totalGenesAssociated + ", femaleOnlyNumber=" + femaleOnlyNumber + ", maleOnlyNumber=" + maleOnlyNumber + ", bothNumber=" + bothNumber + "]";
	}
	
	
}
