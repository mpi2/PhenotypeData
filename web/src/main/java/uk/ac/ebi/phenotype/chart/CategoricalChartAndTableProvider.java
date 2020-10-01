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

import org.apache.commons.lang3.text.WordUtils;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.StatisticalResultDTO;
import org.mousephenotype.cda.solr.web.dto.CategoricalDataObject;
import org.mousephenotype.cda.solr.web.dto.CategoricalSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

@Service
public class CategoricalChartAndTableProvider {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	private final ImpressService impressService;

	@Inject
	public CategoricalChartAndTableProvider(ImpressService impressService) {
		this.impressService = impressService;
	}


	/**
	 * return a list of categorical result and chart objects - one for each
	 * ExperimentDTO
	 */
	public CategoricalResultAndCharts doCategoricalData(ExperimentDTO experiment, ParameterDTO parameter,
														String numberString)
	throws SQLException {

		logger.debug("running categorical data");
		
			List<String> categories = parameter.getCategories();
			//System.out.println("categories===="+categories);
			
			//for the IMPC_EYE_092_001 derived parameter hack the categories to match the SR core????
			if(parameter.getStableId().equals("IMPC_EYE_092_001")){
				categories=new ArrayList<>();
				categories.add("normal");
				categories.add("abnormal");
			}
			
		CategoricalResultAndCharts categoricalResultAndCharts = new CategoricalResultAndCharts();
		categoricalResultAndCharts.setExperiment(experiment);
		List<StatisticalResultDTO> statsResults = experiment.getResults();
		CategoricalChartDataObject chartData = new CategoricalChartDataObject();

		// make a chart object one for each sex
		for (SexType sexType : experiment.getSexes()) {
			categoricalResultAndCharts.setStatsResults(statsResults);
			CategoricalSet controlSet = new CategoricalSet();
			controlSet.setName(WordUtils.capitalize(sexType.name()) + " Control");
			if (sexType.equals(SexType.not_considered)) {
				controlSet.setName(WordUtils.capitalize("Control"));
			}
			controlSet.setSexType(sexType);

			for (String category : categories) {
				if (category.equals("imageOnly")){
					continue;
				}
				CategoricalDataObject controlCatData = new CategoricalDataObject();
				controlCatData.setName(WordUtils.capitalize(sexType.name()) + " Control");
				if (sexType.equals(SexType.not_considered)) {
					controlSet.setName(WordUtils.capitalize("Control"));
				}
				controlCatData.setCategory(category);
				long controlCount = 0;

				// Embryo parameters do not generally have controls
				if (experiment.getControls() != null) {
					for (ObservationDTO control : experiment.getControls()) {
						// get the attributes of this data point
						SexType docSexType = SexType.valueOf(control.getSex());
						String categoString = control.getCategory();
						if (categoString.equals(category) && (docSexType.equals(sexType) || sexType.equals(SexType.not_considered))) {
							controlCount++;
						}
					}
				} else {
					controlCount = 0;
				}
				controlCatData.setCount(controlCount);
				controlSet.add(controlCatData);
			}
			chartData.add(controlSet);

			// now do experimental i.e. zygosities
			for (ZygosityType zType : experiment.getZygosities()) {
				CategoricalSet zTypeSet = new CategoricalSet();
				// hold the data for each bar on graph hom, normal, abnormal
				zTypeSet.setName(WordUtils.capitalize(sexType.name()) + " " + WordUtils.capitalize(zType.name()));
				if (sexType.equals(SexType.not_considered)) {
					zTypeSet.setName(WordUtils.capitalize(WordUtils.capitalize(zType.name()) + " Mutant"));
				}
				for (String category : categories) {
					if (category.equals("imageOnly")){
						continue;
					}
					Long mutantCount = 0L;
					// loop over all the experimental docs and get
					// all that apply to current loop parameters
					Set<ObservationDTO> expObservationsSet;
					expObservationsSet = experiment.getMutants(sexType, zType);

					for (ObservationDTO expDto : expObservationsSet) {

						// get the attributes of this data point
						SexType docSexType = SexType.valueOf(expDto.getSex());
						String categoString = expDto.getCategory();
						//System.out.println("mutant category string="+categoString);
						// get docs that match the criteria and add
						// 1 for each that does
						if (categoString.equals(category) && (docSexType.equals(sexType) || sexType.equals(SexType.not_considered))) {
							mutantCount++;
						}
					}

					CategoricalDataObject expCatData = new CategoricalDataObject();
					expCatData.setName(zType.name());
					expCatData.setCategory(category);
					expCatData.setCount(mutantCount);
					StatisticalResultDTO tempStatsResult = null;
					for (StatisticalResultDTO result : statsResults) {
						if(result.getSex() != null && (SexType.valueOf(result.getSex()).equals(SexType.both) || SexType.valueOf(result.getSex()).equals(SexType.not_considered))){
							categoricalResultAndCharts.setCombinedPValue(result.getPValue());
						}
						categoricalResultAndCharts.setFemalePValue(result.getFemaleKoEffectPValue());
						categoricalResultAndCharts.setMalePValue(result.getMaleKoEffectPValue());
						if (result.getZygosity() != null && result.getSex() != null) {
							if (ZygosityType.valueOf(result.getZygosity()).equals(zType) && SexType.valueOf(result.getSex()).equals(sexType)) {
								expCatData.setResult(result);
								result.setSex(sexType.getName());
								result.setZygosity(zType.getName());
								tempStatsResult = result;
							}
						}
					}

					// //TODO get multiple p values when necessary
					// System.err.println("ERROR WE NEED to change the code to handle multiple p values and max effect!!!!!!!!");
					if (tempStatsResult != null && tempStatsResult.getPValue() != null) {
						expCatData.setpValue(tempStatsResult.getPValue());
						if (tempStatsResult.getEffectSize() != null) {
							expCatData.setMaxEffect(tempStatsResult.getEffectSize());
						}
					}

					zTypeSet.add(expCatData);

				}
				chartData.add(zTypeSet);
			}
			categoricalResultAndCharts.setOrganisation(experiment.getOrganisation());
			// add it here before check so we can see the organisation even if no graph data

		}// end of gender

		String chartNew = this.createCategoricalChartFromObjects(numberString, chartData);
		chartData.setChart(chartNew);
		categoricalResultAndCharts.add(chartData);
		categoricalResultAndCharts.setStatsResults(experiment.getResults());
		
		return categoricalResultAndCharts;
		
	}


	public List<ChartData> doCategoricalDataOverview(CategoricalSet controlSet, CategoricalSet mutantSet) {

		ChartData chartData = new ChartData();
		createCategoricalChartOverview(controlSet, mutantSet, chartData);

		List<ChartData> categoricalResultAndCharts = new ArrayList<>();
		categoricalResultAndCharts.add(chartData);

		return categoricalResultAndCharts;
	}


	private void createCategoricalChartOverview(CategoricalSet controlSet,
	CategoricalSet mutantSet,
	ChartData chartData) {

		// to not 0 index as using loop count in jsp
		JSONArray seriesArray          = new JSONArray();
		JSONArray xAxisCategoriesArray = new JSONArray();

		// get a list of unique categories
		HashMap<String, List<Long>> categories = new LinkedHashMap<>();
		// keep the order so we have normal first!
		if (controlSet != null && controlSet.getCount() > 0){
			for (CategoricalDataObject catObject : controlSet.getCatObjects()) {
				String category = catObject.getCategory();
				categories.put(category, new ArrayList<>());
			}
			xAxisCategoriesArray.put(controlSet.getName());
		}

		if (mutantSet != null && mutantSet.getCount() > 0){
			for (CategoricalDataObject catObject : mutantSet.getCatObjects()) {
				String category = catObject.getCategory();
				if (!categories.containsKey(category) && !category.equalsIgnoreCase("no data")) {
					categories.put(category, new ArrayList<>());
				}
			}
			xAxisCategoriesArray.put(mutantSet.getName());
		}

		for (String categoryLabel : categories.keySet()) {
			if (controlSet != null && controlSet.getCount() > 0){
				if (controlSet.getCategoryByLabel(categoryLabel) != null) {
					categories.get(categoryLabel).add(controlSet.getCategoryByLabel(categoryLabel).getCount());
				}
				else categories.get(categoryLabel).add((long) 0);
			}

			assert mutantSet != null;
			if (mutantSet.getCategoryByLabel(categoryLabel) != null) {
				categories.get(categoryLabel).add(mutantSet.getCategoryByLabel(categoryLabel).getCount());
			}
			else categories.get(categoryLabel).add((long) 0);
		}

		try {
			for (Entry<String, List<Long>> pairs : categories.entrySet()) {
				List<Long> data = pairs.getValue();
				JSONObject dataset1 = new JSONObject();// e.g. normal
				dataset1.put("name", pairs.getKey());
				JSONArray dataset = new JSONArray();
				for (Long singleValue : data) {
					dataset.put(singleValue);
				}
				dataset1.put("data", dataset);

				seriesArray.put(dataset1);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		String chartId = "single-chart-div";
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaOpaque);
		String javascript = "$(document).ready(function() { chart = new Highcharts.Chart({ "
		+ "colors:" + colors + ", "
		+ "tooltip: {  pointFormat: '{series.name}: <b>{point.y}</b>'},"
		+ "chart: { renderTo: '" + chartId + "', type: 'column', style: { fontFamily: '\"Roboto\", sans-serif' }}, "
		+ "title: { text: '', useHTML:true }, "
		+ "subtitle: { text:''}, credits: { enabled: false }, "
		+ "xAxis: { categories: " + xAxisCategoriesArray + "}, "
		+ "yAxis: { min: 0, title: { text: 'Percent Occurrence' } ,  "
		+ "labels: {       formatter: function() { return this.value +'%';   }  }  },  "
		+ "plotOptions: { column: { stacking: 'percent' } }, "
		+ "series: " + seriesArray + " });  });";

		chartData.setChart(javascript);
		chartData.setId(chartId);

	}


	private String createCategoricalChartFromObjects(String chartId,
													 CategoricalChartDataObject chartData) {

		JSONArray seriesArray = new JSONArray();
		JSONArray xAxisCategoriesArray = new JSONArray();

		List<CategoricalSet> catSets = chartData.getCategoricalSets();
		// get a list of unique categories
		HashMap<String, List<Long>> categories = new LinkedHashMap<>();
		// keep the order so we have normal first!
		CategoricalSet catSet1 = catSets.get(0);
		// assume each cat set has the same number of categories
		for (CategoricalDataObject catObject : catSet1.getCatObjects()) {
			String category = catObject.getCategory();
			categories.put(category, new ArrayList<>());
		}
		
		// loop through control, then hom, then het etc
		for (CategoricalSet catSet : catSets) {
			xAxisCategoriesArray.put(catSet.getName());
			for (CategoricalDataObject catObject : catSet.getCatObjects()) {
				List<Long> catData = categories.get(catObject.getCategory());
				catData.add(catObject.getCount());
			}
		}

		try {
			for (Entry<String, List<Long>> pairs : categories.entrySet()) {
				List<Long> data = pairs.getValue();
				JSONObject dataset1 = new JSONObject();// e.g. normal
				dataset1.put("name", pairs.getKey());
				JSONArray dataset = new JSONArray();

				for (Long singleValue : data) {
					dataset.put(singleValue);
				}
				dataset1.put("data", dataset);
				seriesArray.put(dataset1);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}


		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaOpaque);
		String toolTipFunction = "	{ formatter: function() {         return \''+  this.series.name +': '+ this.y +' ('+ (this.y*100/this.total).toFixed(1) +'%)';   }    }";
		String javascript = "$(function () {  var chart_"
			+ chartId
			+ "; $(document).ready(function() { chart_"
			+ chartId
			+ " = new Highcharts.Chart({ "
			+ "tooltip : "+ toolTipFunction
			+ ", colors:" + colors
			+ ", chart: { renderTo: 'chart"	+ chartId + "', type: 'column', style: { fontFamily: '\"Roboto\", sans-serif' } }, "
			+ "title: {  text: ' ', useHTML:true  }, "
			+ "credits: { enabled: false }, "
			+ "subtitle: { text: '', x: -20 }, "
			+ "xAxis: { categories: "+ xAxisCategoriesArray+ "}, "
			+ "yAxis: { min: 0, title: { text: 'Percent Occurrence' } ,  labels: {  formatter: function() { return this.value +'%';   }  }},  "
			+ "plotOptions: { column: { stacking: 'percent' } }, "
			+ "series: "+ seriesArray + " });  });  "
			+ ChartUtils.getSelectAllButtonJs("chart_"+ chartId, "checkAll", "uncheckAll")
			+ "});";
		chartData.setChart(javascript);
		chartData.setChartIdentifier(chartId);

		return javascript;
	}

}
