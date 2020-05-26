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

import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold all data, charts and tables pertaining to a Categorical Result Stats set so for male and female data combined
 * @author jwarren
 *
 */
public class CategoricalResultAndCharts {

	List<CategoricalChartDataObject> maleAndFemale=new ArrayList<CategoricalChartDataObject>();
	ExperimentDTO experiment;
	private List<StatisticalResultDTO> statsResults;
	private String organisation="placeHolder";
	
	private Double combinedPValue;//male and female controls and experimental data combined to get an overall p value

	public Double getCombinedPValue() {
		return combinedPValue;
	}

	public void setCombinedPValue(Double combinedPValue) {
		this.combinedPValue = combinedPValue;
	}

	public List<StatisticalResultDTO> getStatsResults() {
		return statsResults;
	}

	public void setStatsResults(List<StatisticalResultDTO> list) {
		this.statsResults = list;
	}

	public List<CategoricalChartDataObject> getMaleAndFemale() {
		return maleAndFemale;
	}

	public void setMaleAndFemale(List<CategoricalChartDataObject> maleAndFemale) {
		this.maleAndFemale = maleAndFemale;
	}

	public void add(CategoricalChartDataObject chartData) {
		this.maleAndFemale.add(chartData);

	}

	public String getOrganisation() {
		 return organisation;
	}

	public void setOrganisation(String organisation) {
		this.organisation=organisation;

	}

	/**
	 * @return the experiment
	 */
	public ExperimentDTO getExperiment() {
		return experiment;
	}

	/**
	 * @param experiment the experiment to set
	 */
	public void setExperiment(ExperimentDTO experiment) {
		this.experiment = experiment;
	}
	
	

}
