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

import org.mousephenotype.cda.solr.web.dto.CategoricalSet;

import java.util.ArrayList;
import java.util.List;


public class CategoricalChartDataObject {
	List<CategoricalSet> categoricalSets;
	private String chart="";
	private String chartIdentifier="";

	public String getChartIdentifier() {
		return chartIdentifier;
	}

	public void setChartIdentifier(String chartIdentifier) {
		this.chartIdentifier = chartIdentifier;
	}

	public String getChart() {
		return chart;
	}

	public CategoricalChartDataObject() {
		this.categoricalSets = new ArrayList<CategoricalSet>();
	}

	public void add(CategoricalSet categoricalSet) {
		categoricalSets.add(categoricalSet);

	}

	public String toString(){
		String dataString="";
		for(CategoricalSet data: this.categoricalSets){
			dataString+=data.toString()+"\n";
		}
		 return dataString;

	}

	public List<CategoricalSet> getCategoricalSets() {
	return this.categoricalSets;
	}

	public void setChart(String javascript) {
		this.chart=javascript;

	}


}
