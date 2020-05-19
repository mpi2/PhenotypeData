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

import org.mousephenotype.cda.dto.AggregateCountXY;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.*;
import java.util.Map.Entry;

public class AnalyticsChartProvider {

	static Map<String, String> trendsSeriesTypes;
	static Map<String, String> trendsSeriesNames;
	static Map<String, String> trendsSeriesUnits;

	static {

		trendsSeriesTypes = new HashMap<>();
		trendsSeriesTypes.put("phenotyped_genes", "column");
		trendsSeriesTypes.put("phenotyped_lines", "column");
		trendsSeriesTypes.put("statistically_significant_calls", "spline");

		trendsSeriesTypes.put("unidimensional_datapoints_QC_passed", "spline");
		trendsSeriesTypes.put("unidimensional_datapoints_QC_failed", "spline");
		trendsSeriesTypes.put("unidimensional_datapoints_issues", "spline");
		trendsSeriesTypes.put("time_series_datapoints_QC_passed", "spline");
		trendsSeriesTypes.put("time_series_datapoints_QC_failed", "spline");
		trendsSeriesTypes.put("time_series_datapoints_issues", "spline");
		trendsSeriesTypes.put("text_datapoints_QC_passed", "spline");
		trendsSeriesTypes.put("text_datapoints_QC_failed", "spline");
		trendsSeriesTypes.put("text_datapoints_issues", "spline");
		trendsSeriesTypes.put("categorical_datapoints_QC_passed", "spline");
		trendsSeriesTypes.put("categorical_datapoints_QC_failed", "spline");
		trendsSeriesTypes.put("categorical_datapoints_issues", "spline");
		trendsSeriesTypes.put("image_record_datapoints_QC_passed", "spline");
		trendsSeriesTypes.put("image_record_datapoints_QC_failed", "spline");
		trendsSeriesTypes.put("image_record_datapoints_issues", "spline");

		trendsSeriesNames = new HashMap<>();
		trendsSeriesNames.put("phenotyped_genes", "Phenotyped genes");
		trendsSeriesNames.put("phenotyped_lines", "Phenotyped lines");
		trendsSeriesNames.put("statistically_significant_calls", "MP calls");

		trendsSeriesNames.put("unidimensional_datapoints_QC_passed", "Unidimensional (QC passed)");
		trendsSeriesNames.put("unidimensional_datapoints_QC_failed", "Unidimensional (QC failed)");
		trendsSeriesNames.put("unidimensional_datapoints_issues", "Unidimensional (issues)");
		trendsSeriesNames.put("time_series_datapoints_QC_passed", "Time series (QC passed)");
		trendsSeriesNames.put("time_series_datapoints_QC_failed", "Time series (QC failed)");
		trendsSeriesNames.put("time_series_datapoints_issues", "Time series (issues)");
		trendsSeriesNames.put("text_datapoints_QC_passed", "Text (QC passed)");
		trendsSeriesNames.put("text_datapoints_QC_failed", "Text (QC failed)");
		trendsSeriesNames.put("text_datapoints_issues", "Text (issues)");
		trendsSeriesNames.put("categorical_datapoints_QC_passed", "Categorical (QC passed)");
		trendsSeriesNames.put("categorical_datapoints_QC_failed", "Categorical (QC failed)");
		trendsSeriesNames.put("categorical_datapoints_issues", "Categorical (issues)");
		trendsSeriesNames.put("image_record_datapoints_QC_passed", "Image record (QC passed)");
		trendsSeriesNames.put("image_record_datapoints_QC_failed", "Image record (QC failed)");
		trendsSeriesNames.put("image_record_datapoints_issues", "Image record (issues)");

		trendsSeriesUnits = new HashMap<>();
		trendsSeriesUnits.put("phenotyped_genes", "genes");
		trendsSeriesUnits.put("phenotyped_lines", "lines");
		trendsSeriesUnits.put("statistically_significant_calls", "calls");
	}

	private String createHistoryTrendChart(
			JSONArray series,
			JSONArray categories,
			String title,
			String subTitle,
			String yAxis1Legend,
			String yAxis2Legend,
			boolean yAxisCombined, String containerId, String checkAllId, String uncheckAllId) {

		return "$(function () { var chart_" + containerId + "; \n"+
		"$(document).ready(function() { chart_"	+ containerId + " = new Highcharts.Chart({ \n" +
		"        chart: {\n"+
		"               zoomType: 'xy',\n"+
		"				renderTo: '" + containerId + "'"+
		"            },\n"+
		"            title: {\n"+
		"                text: '"+title+"'\n"+
		"            },\n"+
		"            subtitle: {\n"+
		"                text: '"+subTitle+"'\n"+
		"            },\n"+
		"            xAxis: [{\n"+
		"                categories: "+ categories.toString() +",\n"+
		"                   }],\n"+
					"            yAxis: [{ // Primary yAxis\n"+
					"                labels: {\n"+
					"                    format: '{value}',\n"+
					"                    style: {\n"+
					"                        color: Highcharts.getOptions().colors[1]\n"+
					"                    }\n"+
					"                },\n"+
					"                title: {\n"+
					"                    text: '"+yAxis1Legend+"',\n"+
					"                    style: {\n"+
					"                        color: Highcharts.getOptions().colors[1]\n"+
					"                    }\n"+
					"                }\n"+
					"            }, \n"+
					((!yAxisCombined) ? "" :
					"            { // Secondary yAxis\n"+
					"                title: {\n"+
					"                    text: '"+yAxis2Legend+"',\n"+
					"                    style: {\n"+
					"                        color: Highcharts.getOptions().colors[0]\n"+
					"                    }\n"+
					"                },\n"+
					"                labels: {\n"+
					"                    format: '{value}',\n"+
					"                    style: {\n"+
					"                        color: Highcharts.getOptions().colors[0]\n"+
					"                    }\n"+
					"                },\n"+
					"                opposite: true\n"+
					"            }\n") +
					"            ],\n"+
					"      credits: { \n"+
					"         enabled: false \n"+
					"      }, \n"+
					"            tooltip: {\n"+
					"                shared: true\n"+
					"            },\n"+
					"            series:" + series.toString() +"\n"+
					"        });\n"+
					"    });\n"
					+ ChartUtils.getSelectAllButtonJs("chart_"+ containerId, checkAllId, uncheckAllId)
					+ "});";
	}


	public String createLineProceduresOverviewChart(JSONArray series, JSONArray categories, String title, String subTitle, String yAxisLegend, String yAxisUnit, 
			String containerId, Boolean stacked, String checkAllId, String uncheckAllId) {

		return "$(function () {\n var chart_" + containerId + ";" +
		"	Highcharts.setOptions({"+
		"	    colors: " + ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaOpaque) + "});" +
		"   chart_" + containerId + " = new Highcharts.Chart({ " +
		"        chart: {\n"+
		"            type: 'column',\n"+
		"            height: 800,\n"+
		"			 renderTo:" + containerId +
		"        },\n"+
		"        title: {\n"+
		"            text: '"+title+"'\n"+
		"       },\n"+
		"        subtitle: {\n"+
		"            text: \""+subTitle+"\"\n"+
		"       },\n"+
		"        xAxis: {\n"+
		"            categories: "+ categories.toString() +",\n"+
		"            labels: { \n"+
		"               rotation: -90, \n"+
		"               align: 'right', \n"+
		"               style: { \n"+
		"                  fontSize: '11px', \n"+
		"                  fontFamily: '\"Roboto\", sans-serif' \n"+
		"               } \n"+
		"            }, \n"+
		"            showLastLabel: true \n"+
		"        },\n"+
		"        yAxis: {\n"+
		"            min: 0,\n"+
		"            title: {\n"+
		"                text: '"+ yAxisLegend +"'\n"+
		"            }\n"+
		"        },\n"+
		"      credits: {\n"+
		"         enabled: false\n"+
		"      },\n"+
		"        tooltip: {\n"+
		"            headerFormat: '<span style=\"font-size:10px\">{point.key}</span><table>',\n"+
		"            pointFormat: '<tr><td style=\"color:{series.color};padding:0\">{series.name}: </td>' +\n"+
		"                '<td style=\"padding:0\"><b>{point.y:.0f} "+yAxisUnit+"</b></td></tr>',\n"+
		"            footerFormat: '</table>',\n"+
		"            shared: true,\n"+
		"            useHTML: true\n"+
		"        },\n"+
		"        plotOptions: {\n"+
		"            column: {\n" + ((stacked) ? "            	 stacking: 'normal',\n" : "")+
		"                pointPadding: 0.2,\n"+
		"                borderWidth: 0\n"+
		"            }\n"+
		"        },\n"+
		"        series:" + series.toString() +"\n"+
		"    });\n"+
		ChartUtils.getSelectAllButtonJs("chart_" + containerId, checkAllId, uncheckAllId) +
		"});\n";

	}

	public String getSlicedPieChart(Map<String, Long> slicedOut, Map<String, Long> notSliced, String title, String containerId){

			List<String> colors=java.util.Arrays.asList( "'rgba(9, 120, 161,1)'", "'rgba(255, 201, 67, 1)'", "'rgba(239, 123, 11, 1)'","'rgba(119, 119, 119, 1)'", 
					 "'rgba(36, 139, 75, 1)'", "'rgba(238, 238, 180, 1)'", "'rgba(191, 75, 50, 1)'", "'rgba(191, 151, 50, 1)'", "'rgba(239, 123, 11, 1)'" ,
					"'rgba(247, 157, 70, 1)'", "'rgba(247, 181, 117, 1)'",  "'rgba(191, 75, 50, 1)'", "'rgba(151, 51, 51, 1)'");
			JSONArray data = new JSONArray();
			try {
				for ( Entry<String, Long> entry : slicedOut.entrySet()){
					JSONObject obj = new JSONObject();
					obj.put("name", entry.getKey());
					obj.put("y", entry.getValue());
					obj.put("sliced", true);
					obj.put("selected", true);
					data.put(obj);
				}
				for ( Entry<String, Long> entry : notSliced.entrySet()){
					JSONObject obj = new JSONObject();
					obj.put("name", entry.getKey());
					obj.put("y", entry.getValue());
					data.put(obj);
				}

				return "$(function () { $('#" + containerId + "').highcharts({ "
						 + " chart: { plotBackgroundColor: null, plotShadow: false}, "
						 + " colors:" + colors + ", "
						 + " title: {  text: '" + title + "' }, "
						 + " credits: { enabled: false }, "
						 + " tooltip: {  pointFormat: '<b>{point.percentage:.2f}%</b>'},"
						 + " plotOptions: { "
						 	+ "pie: { "
						 		+ "size: 200, "
						 		+ "showInLegend: true, "
						 		+ "allowPointSelect: true, "
						 		+ "cursor: 'pointer', "
						 		+ "dataLabels: { enabled: true, format: '{point.percentage:.2f} %', "
						 		+ "style: { color: '#666', width:'60px' }  }  },"
						 	+ "series: {  dataLabels: {  enabled: true, format: '{point.percentage:.2f}%'} }"
						 + " },"
					+ " series: [{  type: 'pie',   name: '',  "
						+ "data: " + data + "  }]"
				+" }); });";
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
	}

	public String generateAggregateCountByProcedureChart(
			List<AggregateCountXY> data,
			String title,
			String subTitle,
			String yAxisLegend,
			String yAxisUnit,
			String containerId, String checkAll, String uncheckAll
			) {

		JSONArray                           series         =new JSONArray();
		JSONArray                           categories     = new JSONArray();
		List<String>                        categoriesList = new ArrayList<>();
		Map<String, List<AggregateCountXY>> centerMap      = new HashMap<>();
		try {
			// List categories first
			// List centers
			for (AggregateCountXY bean: data) {
				if (!categoriesList.contains(bean.getxValue())) {
					categoriesList.add(bean.getxValue());
					categories.put(bean.getxValue());
				}
				List<AggregateCountXY> beans;
				if (!centerMap.containsKey(bean.getyValue())) {
					beans = new ArrayList<>();
					centerMap.put(bean.getyValue(), beans);
				} else {
					beans = centerMap.get(bean.getyValue());
				}
				beans.add(bean);
			}
			
			// build by center specific list
			for (String center: centerMap.keySet()) {
				List<AggregateCountXY> beans               = centerMap.get(center);
				JSONObject             containerJsonObject =new JSONObject();
				JSONArray              dataArray           =new JSONArray();
				// previous_countLines checks if a procedure has more than one id
				int previous_countLines = 0;
				// so always the same order for categories
				for (String procedure: categoriesList) {
					int countLines = 0;
					// Retrieve procedure (not the fastest way)
					for (AggregateCountXY bean: beans) {
						if (bean.getxValue().equals(procedure)) {
							// countLines = bean.getAggregateCount() + previous_countLines;
							if (previous_countLines != 0) {
								countLines = bean.getAggregateCount() + previous_countLines;
							} else {
								countLines = bean.getAggregateCount();
							} 
						}
						previous_countLines = countLines;
					}
					dataArray.put(countLines);
				}
				containerJsonObject.put("data", dataArray);
				containerJsonObject.put("name", center);
				series.put(containerJsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return this.createLineProceduresOverviewChart(series, categories, title, subTitle, yAxisLegend, yAxisUnit, containerId, true, checkAll, uncheckAll);
	}

	/**
	 * Generate a graph with trends information...
	 */
	public String generateHistoryTrendsChart(
			Map<String, List<AggregateCountXY>> trendsMap,
			List<String> releases,
			String title,
			String subtitle,
			String yAxis1Legend,
			String yAxis2Legend,
			boolean yAxisCombined,
			String containerId,
			String checkAllId,
			String uncheckAllId) {

		JSONArray series = new JSONArray();
		JSONArray categories = new JSONArray();
		try {

			// generate categories (release by release asc)
			for (String release : releases) {
				categories.put(release);
			}

			List<String> keys = new ArrayList<>(trendsMap.keySet());
			Collections.sort(keys);

			// We use all keys provided
			for (String trendProperty : keys) {
				// new series
				List<AggregateCountXY> beans = trendsMap.get(trendProperty);
				JSONObject containerJsonObject = new JSONObject();
				JSONArray dataArray = new JSONArray();
				// this is not performant but we need to plot the missing values
				for (String release : releases) {
					boolean found = false;
					for (AggregateCountXY bean : beans) {
						if (bean.getyValue().equals(release)) {
							dataArray.put(bean.getAggregateCount());
							found = true;
							break;
						}
					}
					if (!found) {
						dataArray.put(0); // default vaule
					}
				}
				containerJsonObject.put("data", dataArray);
				containerJsonObject.put("type", trendsSeriesTypes.get(trendProperty));
				String name = trendsSeriesNames.getOrDefault(trendProperty, trendProperty);
				containerJsonObject.put("name", name);

				if (trendProperty.equals("statistically_significant_calls")) {
					containerJsonObject.put("yAxis", 1);
				}

				JSONObject tooltip = new JSONObject();
				tooltip.put("pointFormat", "<span style=\"color:{series.color}\">\u25CF</span> {series.name}: <b>{point.y}</b><br/>");
				tooltip.put("valueSuffix", " " + (trendsSeriesUnits.getOrDefault(trendProperty, "")));
				containerJsonObject.put("tooltip", tooltip);

				series.put(containerJsonObject);
				// this is hardcoded for the moment
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return createHistoryTrendChart(series, categories, title, subtitle, yAxis1Legend, yAxis2Legend, yAxisCombined, containerId, checkAllId, uncheckAllId);

	}

}
