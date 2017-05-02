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

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.solr.service.ExperimentService;
import org.mousephenotype.cda.solr.service.StatisticalResultService;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.web.ChartType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class GraphUtils {

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	ExperimentService experimentService;
	StatisticalResultService srService;

	public GraphUtils(ExperimentService experimentService, StatisticalResultService srService) {

		this.experimentService = experimentService;
		this.srService = srService;

	}


	public Set<String> getGraphUrls(String acc,
	ParameterDTO parameter, List<String> pipelineStableIds, List<String> zyList, List<String> phenotypingCentersList,
	List<String> strainsParams, List<String> metaDataGroup, ChartType chartType, List<String> alleleAccession)
			throws SolrServerException, IOException, URISyntaxException {

		// each url should be unique and so we use a set
		Set<String> urls = new LinkedHashSet<String>();
		String seperator = "&";
		String parameterStableId = parameter.getStableId();
		String accessionAndParam = "accession=" + acc;

		if (chartType != null) {
			if(chartType==ChartType.PIE){
				urls.add("chart_type=PIE&parameter_stable_id=IMPC_VIA_001_001");
				return urls;
			}
		} else {
			// default chart type
			chartType = getDefaultChartType(parameter);
		}

		if ( ! ChartUtils.getPlotParameter(parameter.getStableId()).equalsIgnoreCase(parameter.getStableId())) {
            parameterStableId = ChartUtils.getPlotParameter(parameter.getStableId());
            chartType = ChartUtils.getPlotType(parameterStableId);
            if (chartType.equals(ChartType.TIME_SERIES_LINE)){
				metaDataGroup = null; // Dderived serie parameters don't have the same metadata so we have to ignore it for series
            }
        }
		accessionAndParam += seperator + "parameter_stable_id=" + parameterStableId;
		accessionAndParam += seperator + "chart_type=" + chartType + seperator;
		if(parameter.getStableId().equals("IMPC_BWT_008_001")){//if bodywieght we don't have stats results so can't use the srService to pivot and have to use exmperiment service instead
			urls.addAll(experimentService.getChartPivots( accessionAndParam, acc, parameter, pipelineStableIds, zyList, phenotypingCentersList,
					 strainsParams, metaDataGroup, alleleAccession));
		}else{
				urls.addAll(srService.getChartPivots( accessionAndParam, acc, parameter, pipelineStableIds, zyList, phenotypingCentersList,
				 strainsParams, metaDataGroup, alleleAccession));
		}
		
		for(String url:urls){
			System.out.println("url="+url);
		}

		return urls;
	}


	public static ChartType getDefaultChartType(ParameterDTO parameter){

		if (Constants.ABR_PARAMETERS.contains(parameter.getStableId())){

			return ChartType.UNIDIMENSIONAL_ABR_PLOT;

		}else if(parameter.getStableId().equals("IMPC_VIA_001_001")){
			return ChartType.PIE;

		}else{

	        ObservationType observationTypeForParam = parameter.getObservationType();
	        switch (observationTypeForParam) {

                case unidimensional:
                   return ChartType.UNIDIMENSIONAL_BOX_PLOT;

                case categorical:
                	return ChartType.CATEGORICAL_STACKED_COLUMN;

                case time_series:
                	return ChartType.TIME_SERIES_LINE;
	        }
		}
		return null;
	}

}
