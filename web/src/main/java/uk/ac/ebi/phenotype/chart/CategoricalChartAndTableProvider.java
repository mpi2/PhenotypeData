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

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.db.pojo.BiologicalModel;
import org.mousephenotype.cda.db.pojo.CategoricalResult;
import org.mousephenotype.cda.db.pojo.Parameter;
import org.mousephenotype.cda.db.pojo.Procedure;
import org.mousephenotype.cda.db.pojo.StatisticalResult;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.CategoricalDataObject;
import org.mousephenotype.cda.solr.web.dto.CategoricalSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class CategoricalChartAndTableProvider {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@Autowired
	PhenotypePipelineDAO ppDAO;

	@Autowired
	ImpressService impressService;


	/**
	 * return a list of categorical result and chart objects - one for each
	 * ExperimentDTO
	 *
	 * @param experiment
	 * @param parameter
	 * @param acc
	 * @param gender
	 * @param parameterId
	 * @param charts
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public CategoricalResultAndCharts doCategoricalData(ExperimentDTO experiment, Parameter parameter,
	String acc, String numberString, BiologicalModel expBiologicalModel)
	throws SQLException, IOException, URISyntaxException {

		logger.debug("running categorical data");
		List<String> categories = parameter.getCategoriesUserInterfaceFreindly();		
		CategoricalResultAndCharts categoricalResultAndCharts = new CategoricalResultAndCharts();
		categoricalResultAndCharts.setExperiment(experiment);
		List<? extends StatisticalResult> statsResults = (List<? extends StatisticalResult>) experiment.getResults();
		CategoricalChartDataObject chartData = new CategoricalChartDataObject();
		
		// make a chart object one for each sex
		for (SexType sexType : experiment.getSexes()) {
			categoricalResultAndCharts.setStatsResults(statsResults);
			CategoricalSet controlSet = new CategoricalSet();
			controlSet.setName(WordUtils.capitalize(sexType.name()) + " Control");
			controlSet.setSexType(sexType);
			
			for (String category : categories) {
				if (category.equals("imageOnly")){
					continue;
				}
				CategoricalDataObject controlCatData = new CategoricalDataObject();
				controlCatData.setName(WordUtils.capitalize(sexType.name()) + " Control");
				controlCatData.setCategory(ppDAO.getCategoryDescription(parameter.getId(), category));
				long controlCount = 0;
				
				for (ObservationDTO control : experiment.getControls()) {
					// get the attributes of this data point
					SexType docSexType = SexType.valueOf(control.getSex());
					String categoString = control.getCategory();
					if (categoString.equals(category) && docSexType.equals(sexType)) {
						controlCount++;
					}
				}

				controlCatData.setCount(controlCount);
				logger.debug("control=" + sexType.name() + " count=" + controlCount + " category=" + ppDAO.getCategoryDescription(parameter.getId(), category));
				controlSet.add(controlCatData);
			}
			chartData.add(controlSet);

			// now do experimental i.e. zygosities
			for (ZygosityType zType : experiment.getZygosities()) {
				CategoricalSet zTypeSet = new CategoricalSet();
				// hold the data for each bar on graph hom, normal, abnormal
				zTypeSet.setName(WordUtils.capitalize(sexType.name()) + " " + WordUtils.capitalize(zType.name()));
				for (String category : categories) {
					if (category.equals("imageOnly")){
						continue;
					}
					Long mutantCount = new Long(0);
					// loop over all the experimental docs and get
					// all that apply to current loop parameters
					Set<ObservationDTO> expObservationsSet = Collections.emptySet();
					expObservationsSet = experiment.getMutants(sexType, zType);

					for (ObservationDTO expDto : expObservationsSet) {

						// get the attributes of this data point
						SexType docSexType = SexType.valueOf(expDto
						.getSex());
						String categoString = expDto.getCategory();
						// get docs that match the criteria and add
						// 1 for each that does
						if (categoString.equals(category) && docSexType.equals(sexType)) {
							mutantCount++;
						}
					}

					CategoricalDataObject expCatData = new CategoricalDataObject();
					expCatData.setName(zType.name());
					expCatData.setCategory(ppDAO.getCategoryDescription(parameter.getId(), category));
					expCatData.setCount(mutantCount);
					CategoricalResult tempStatsResult = null;
					for (StatisticalResult result : statsResults) {
						// System.out.println("result.getZygosityType()!="+result.getZygosityType()+"  && result.getSexType()="+result.getSexType());
						if (result.getZygosityType() != null && result.getSexType() != null) {
							if (result.getZygosityType().equals(zType) && result.getSexType().equals(sexType)) {
								expCatData.setResult((CategoricalResult) result);
								result.setSexType(sexType);
								result.setZygosityType(zType);
								tempStatsResult = (CategoricalResult) result;
								// result.setControlBiologicalModel(controlBiologicalModel);
							}
						}
					}

					// //TODO get multiple p values when necessary
					// System.err.println("ERROR WE NEED to change the code to handle multiple p values and max effect!!!!!!!!");
					if (tempStatsResult != null) {
						expCatData.setpValue(tempStatsResult.getpValue());
						if (tempStatsResult.getEffectSize() != null) {
							expCatData.setMaxEffect(tempStatsResult.getEffectSize());
						}
					}
					// logger.warn("pValue="+pValue+" maxEffect="+maxEffect);
					// }
					zTypeSet.add(expCatData);

				}
				chartData.add(zTypeSet);
			}
			categoricalResultAndCharts.setOrganisation(experiment.getOrganisation());
			// add it here before check so we can see the organisation even if no graph data

		}// end of gender

		String chartNew = this.createCategoricalChartFromObjects(numberString, chartData, parameter, experiment);
		chartData.setChart(chartNew);
		categoricalResultAndCharts.add(chartData);
		categoricalResultAndCharts.setStatsResults(experiment.getResults());
		
		return categoricalResultAndCharts;
		
	}


	public List<ChartData> doCategoricalDataOverview(CategoricalSet controlSet, CategoricalSet mutantSet, Model model, Parameter parameter,
		String procedureName)
	throws SQLException {

		ChartData chartData = new ChartData();
		List<ChartData> categoricalResultAndCharts = new ArrayList<ChartData>();
		String chartNew = this.createCategoricalChartOverview(controlSet, mutantSet, model, parameter, procedureName, chartData);

		/* 2015/08/20 Ilinca : Commented out if for empty control sets as the FER an VIA never have control data.  */
		chartData.setChart(chartNew);
		categoricalResultAndCharts.add(chartData);

		return categoricalResultAndCharts;
	}


	private String createCategoricalChartOverview(CategoricalSet controlSet,
	CategoricalSet mutantSet,
	Model model,
	Parameter parameter, String procedureName,
	ChartData chartData)
	throws SQLException {

		// to not 0 index as using loop count in jsp
		JSONArray seriesArray = new JSONArray();
		JSONArray xAxisCategoriesArray = new JSONArray();
		String title = parameter.getName();
		String subtitle = procedureName ; // parameter.getStableId();

		// get a list of unique categories
		HashMap<String, List<Long>> categories = new LinkedHashMap<String, List<Long>>();
		// keep the order so we have normal first!
		if (controlSet != null && controlSet.getCount() > 0){
			for (CategoricalDataObject catObject : controlSet.getCatObjects()) {
				String category = catObject.getCategory();
				categories.put(category, new ArrayList<Long>());
			}
			xAxisCategoriesArray.put(controlSet.getName());
		}
		
		if (mutantSet != null && mutantSet.getCount() > 0){
			for (CategoricalDataObject catObject : mutantSet.getCatObjects()) {
				String category = catObject.getCategory();
				if (!categories.containsKey(category) && !category.equalsIgnoreCase("no data")) {
					categories.put(category, new ArrayList<Long>());
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

			if (mutantSet.getCategoryByLabel(categoryLabel) != null) {
				categories.get(categoryLabel).add(mutantSet.getCategoryByLabel(categoryLabel).getCount());
			}
			else categories.get(categoryLabel).add((long) 0);
		}

		try {
			Iterator<Entry<String, List<Long>>> it = categories.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, List<Long>> pairs = (Map.Entry<String, List<Long>>) it.next();
				List<Long> data = (List<Long>) pairs.getValue();
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
		+ "chart: { renderTo: '" + chartId + "', type: 'column' }, "
		+ "title: { text: '<span data-parameterStableId=\"" + parameter.getStableId() + "\">" + WordUtils.capitalize(title) + "</span>', useHTML:true }, "
		+ "subtitle: { text:'" + subtitle + "'}, credits: { enabled: false }, "
		+ "xAxis: { categories: " + xAxisCategoriesArray + "}, "
		+ "yAxis: { min: 0, title: { text: 'Percent Occurrence' } ,  "
		+ "labels: {       formatter: function() { return this.value +'%';   }  }  },  "
		+ "plotOptions: { column: { stacking: 'percent' } }, "
		+ "series: " + seriesArray + " });  });";

		chartData.setChart(javascript);
		chartData.setId(chartId);
		return javascript;

	}


	private String createCategoricalChartFromObjects(String chartId,
	CategoricalChartDataObject chartData, Parameter parameter,
	ExperimentDTO experiment)
	throws SQLException {

		JSONArray seriesArray = new JSONArray();
		JSONArray xAxisCategoriesArray = new JSONArray();
		String title = parameter.getName();
		List<CategoricalSet> catSets = chartData.getCategoricalSets();
		// get a list of unique categories
		HashMap<String, List<Long>> categories = new LinkedHashMap<String, List<Long>>();
		// keep the order so we have normal first!
		CategoricalSet catSet1 = catSets.get(0);
		// assume each cat set has the same number of categories
		for (CategoricalDataObject catObject : catSet1.getCatObjects()) {
			String category = catObject.getCategory();
			categories.put(category, new ArrayList<Long>());
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
			Iterator<Entry<String, List<Long>>> it = categories.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, List<Long>> pairs = (Map.Entry<String, List<Long>>) it.next();
				List<Long> data = (List<Long>) pairs.getValue();
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

		// replace space in MRC Harwell with underscore so valid javascript
		// variable
		// String chartId = bm.getId() + sex.name()+organisation.replace(" ",
		// "_")+"_"+metadataGroup;

		Procedure proc = ppDAO.getProcedureByStableId(experiment.getProcedureStableId()) ;
		String procedureDescription = "";
		if (proc != null) {
			procedureDescription = String.format("<a href=\"%s\">%s</a>", impressService.getProcedureUrlByKey(((Integer)proc.getStableKey()).toString()), proc.getName());
		}


		//impressService.getAnchorForProcedure(experiment.getProcedureName(), experiment.getProcedureStableId());

		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaOpaque);
		String toolTipFunction = "	{ formatter: function() {         return \''+  this.series.name +': '+ this.y +' ('+ (this.y*100/this.total).toFixed(1) +'%)';   }    }";
		String javascript = "$(function () {  var chart_"
			+ chartId
			+ "; $(document).ready(function() { chart_"
			+ chartId
			+ " = new Highcharts.Chart({ "
			+ "tooltip : "+ toolTipFunction
			+ ", colors:" + colors
			+ ", chart: { renderTo: 'chart"	+ chartId + "', type: 'column' }, "
			+ "title: {  text: '<span data-parameterStableId=\"" + parameter.getStableId() + "\">" + WordUtils.capitalize(title) + "</span>', useHTML:true  }, "
			+ "credits: { enabled: false }, "
			+ "subtitle: {  useHTML: true,  text: '" + procedureDescription + "', x: -20 }, "
			+ "xAxis: { categories: "+ xAxisCategoriesArray+ "}, "
			+ "yAxis: { min: 0, title: { text: 'Percent Occurrence' } ,  labels: {  formatter: function() { return this.value +'%';   }  }},  "
			+ "plotOptions: { column: { stacking: 'percent' } }, "
			+ "series: "+ seriesArray + " });  });  "
			+ "$('#checkAll').click(function(){ "
			+ " for(i=0; i < chart_" + chartId + ".series.length; i++) {"
			+ " if(chart_" + chartId + ".series[i].visible == false){ "
            + " chart_" + chartId + ".series[i].show(); "
            + "}}" 
            + "}); "
            +"$('#uncheckAll').click(function(){ "
			+ " for(i=0; i < chart_" + chartId + ".series.length; i++) { "
			+ " if(chart_" + chartId + ".series[i].visible == true){ "
            + " chart_" + chartId + ".series[i].hide(); "
            + "}}" 
            + "}); "
			+ "});";
		chartData.setChart(javascript);
		chartData.setChartIdentifier(chartId);

		return javascript;
	}

}
