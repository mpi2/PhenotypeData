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
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;


public class UnidimensionalStatsObject {

	private String line = "Control";// if not set we assume control
	private ZygosityType zygosity = ZygosityType.homozygote;
	private SexType sexType = SexType.male;
	private Float mean = (float) 0;
	private Float sd = (float) 0;
	private Integer sampleSize = 0;
	private String label = "Not set"; // label to display on x Axis. Need this at least for ABR
	private String mpTermId;
	private String mpTermName;
	private String allele = "allele not found";
	private String geneticBackground = "genetic background not found";
	private StatisticalResultDTO result;
	private String status;


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMpTermId() {
		return mpTermId;
	}

	public void setMpTermId(String mpTermId) {
		this.mpTermId = mpTermId;
	}

	public String getMpTermName() {
		return mpTermName;
	}

	public void setMpTermName(String mpTermName) {
		this.mpTermName = mpTermName;
	}

	public Integer getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(Integer sampleSize) {
		this.sampleSize = sampleSize;
	}

	public StatisticalResultDTO getResult() {
		return result;
	}

	public void setResult(StatisticalResultDTO result) {
		this.result = result;
	}

	public String getLabel() {

		return label;
	}

	public void setLabel(String label) {

		this.label = label;
	}

	public String getGeneticBackground() {
		return geneticBackground;
	}

	public void setGeneticBackground(String geneticBackground) {
		this.geneticBackground = geneticBackground;
	}

	public String getAllele() {
		return allele;
	}

	public void setAllele(String allele) {
		this.allele = allele;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public ZygosityType getZygosity() {
		return zygosity;
	}

	public void setZygosity(ZygosityType zygosity) {
		this.zygosity = zygosity;
	}

	public SexType getSexType() {
		return sexType;
	}

	public void setSexType(SexType sexType) {
		this.sexType = sexType;
	}

	public Float getMean() {
		return mean;
	}

	public void setMean(Float mean) {
		this.mean = mean;
	}

	public Float getSd() {
		return sd;
	}

	public void setSd(Float sd) {
		this.sd = sd;
	}
	

	@Override
	public String toString() {
		return "UnidimensionalStatsObject [line=" + line + ", zygosity="
				+ zygosity + ", sexType=" + sexType + ", mean=" + mean
				+ ", sd=" + sd + ", sampleSize=" + sampleSize
				 + ", allele=" + allele
				+ ", geneticBackground=" + geneticBackground + " result="+result+"]";
	}

}
