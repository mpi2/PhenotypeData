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
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ScatterChartAndTableProvider {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	private final UnidimensionalChartAndTableProvider unidimensionalChartAndTableProvider;

	public ScatterChartAndTableProvider(
			UnidimensionalChartAndTableProvider unidimensionalChartAndTableProvider
	) {
		this.unidimensionalChartAndTableProvider = unidimensionalChartAndTableProvider;
	}

	public String createScatter(Float min, Float max, String experimentNumber, ParameterDTO parameter, JSONArray series, Boolean windowSeries, String alleleSymbol) {

		String yTitle="";

		//if incremental parameter we should have a y unit - if not we use the xunit - most scatters were displaying null so this is a fix JW
		if(parameter.getUnitY()!=null && !parameter.getUnitY().equals("")){
			yTitle=parameter.getUnitY();
		}else if(parameter.getUnitX()!=null && !parameter.getUnitX().equals("")){
			yTitle=parameter.getUnitX();
		}

		String yAxisLabel = parameter.getName() + " (" + yTitle + ")";
		if (yTitle.trim().isEmpty()) {
			yAxisLabel = parameter.getName();
		}

		String yAxisMin = (min != null ? "min: " + min + ", " : "");
		String yAxisMax = (max != null ? "max: " + max + ", " : "");
		String yAxis = "yAxis: { tickAmount: 5,"
				+ yAxisMin + yAxisMax
				+ " title: { text: '" + yAxisLabel + "' } }, ";

/*		if(windowSeries) {
			String primaryAxis = String.format("{tickAmount: 5, %s %s title: {text: '%s'}}", yAxisMin, yAxisMax, yAxisLabel);
			String secondaryAxis = "{tickAmount: 5, min: 0, max: 1, title: {text: 'Soft window statistical weight'}, opposite: true}";
			yAxis = String.format("yAxis: [%s, %s],", primaryAxis, secondaryAxis);
		}*/

		// A better title for this plot
		String allele = alleleSymbol
			.replaceAll("<", "��").replaceAll(">", "##")
			.replaceAll("��", "<sup>").replaceAll("##", "</sup>");
		String chartTitle = String.format("%s<br>%s scatter plot", allele, parameter.getName());


		return "$(function () {\n"
			+ " new Highcharts.Chart({ "
			+ " chart: {renderTo: 'scatter" + experimentNumber + "',"
			+ "         zoomType: 'xy'"
			+ "     },"
			+ "   title: {  useHTML:true, text: '"+chartTitle+" <a href=\"/help/quick-guide-to-the-website/chart-page/\" target=\"_blank\"><i class=\"fa fa-question-circle\" style=\"color: #ce6211;\"></i></a>' },"
			+ "     xAxis: {"
			+ "       type: 'datetime',"
			+ "       labels: { "
			+ "         rotation: -45, "
			+ "         align: 'right', "
			+ "         style: { "
			+ "           fontSize: '13px', "
		    + "            fontFamily: '\"Roboto\", sans-serif;'\n"
			+ "         }, "
			+ "         formatter: function() { return Highcharts.dateFormat('%b %Y', this.value); } "
			+ "       },"
			+ "       showLastLabel: true "
			+ "     }, "
			+ yAxis
			+ "\n     credits: { "
			+ "\n       enabled: false "
			+ "\n     }, "
			+ "\n     tooltip: { "
			+ "\n          formatter: function () { "
			+ "\n              if (this.series.name === 'Soft window statistical weight') { return '<b>' + this.series.name + '</b><br/>' + Highcharts.dateFormat('%e %b %Y', this.x) + ': ' + this.y ; }"
			+ "\n              return '<b>' + this.series.name + '</b><br/>' + Highcharts.dateFormat('%e %b %Y', this.x) + ': ' + this.y + ' " + parameter.getUnitX() + " '; "
			+ "\n          } "
			+ "\n      }, "
			+ "\n     series: "
			+ series.toString()
			+ "    });"
			+ "	}); ";
	}


	public ScatterChartAndData doScatterData(ExperimentDTO experiment, Float yMin, Float yMax, ParameterDTO parameter, String experimentNumber) {

		Boolean windowSeries = Boolean.FALSE;

		JSONArray series=new JSONArray();
		// maybe need to put these into method that can be called as repeating
		// this - so needs refactoring though there are minor differences?

		for (SexType sex : experiment.getSexes()) {

			JSONObject controlJsonObject = new JSONObject();
			JSONArray dataArray = new JSONArray();

			try {
				controlJsonObject.put("name", WordUtils.capitalize(sex.name()) + " " + "WT");
				JSONObject markerObject = ChartColors.getMarkerJSONObject(sex, null);
				controlJsonObject.put("marker", markerObject);
				controlJsonObject.put("type", "scatter");
			} catch (JSONException e) {
				e.printStackTrace();
			}

			for (ObservationDTO control : experiment.getControls(sex)) {

				if ( ! windowSeries && control.getWindowWeight() != null) {
					windowSeries = Boolean.TRUE;
				}

				if (SexType.valueOf(control.getSex()).equals(sex)) {
					Float dataPoint = control.getDataPoint();
					addScatterPoint(dataArray, control, dataPoint);
				}

			}

			try {
				controlJsonObject.put("data", dataArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			series.put(controlJsonObject);
			logger.debug("finished putting control to data points");
		}

		for (SexType sex : experiment.getSexes()) {
			JSONObject expZyg = new JSONObject();
			JSONArray expDataArray = new JSONArray();

			for (ZygosityType zType : experiment.getZygosities()) {

				try {
					expZyg.put("name", WordUtils.capitalize(sex.name()) + " " + WordUtils.capitalize(zType.getShortName()));
					JSONObject markerObject = ChartColors.getMarkerJSONObject(sex, zType);
					expZyg.put("marker", markerObject);
					expZyg.put("type", "scatter");

				} catch (JSONException e1) {
					e1.printStackTrace();
				}

				Set<ObservationDTO> expObservationsSet = Collections.emptySet();

				if (zType.equals(ZygosityType.heterozygote)) {
					expObservationsSet = experiment.getHeterozygoteMutants();
				}
                if (zType.equals(ZygosityType.hemizygote)) {
					expObservationsSet = experiment.getHemizygoteMutants();
				}
				if (zType.equals(ZygosityType.homozygote)) {
					expObservationsSet = experiment.getHomozygoteMutants();
				}

				for (ObservationDTO expDto : expObservationsSet) {

					if ( ! windowSeries && expDto.getWindowWeight() != null) {
						windowSeries = Boolean.TRUE;
					}

					if (SexType.valueOf(expDto.getSex()).equals(sex)) {
						Float dataPoint = expDto.getDataPoint();
						addScatterPoint(expDataArray, expDto, dataPoint);
					}
				}

				try {
					expZyg.put("data", expDataArray);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				series.put(expZyg);
			}
		}



		try {
			if (windowSeries) {

				JSONObject windowsSeriesName = new JSONObject();
				windowsSeriesName.put("name", "Soft window statistical weight");
				windowsSeriesName.put("type", "spline");
				//windowsSeriesName.put("yAxis", 1);
				windowsSeriesName.put("dashStyle", "shortdot");
				windowsSeriesName.put("lineWidth", 4);
				windowsSeriesName.put("color", "#000000");
				windowsSeriesName.put("clip", false);

				Map<Long, Double> windowData = new HashMap<>();
				for (ObservationDTO dto : Stream.of(experiment.getMutants(), experiment.getControls()).flatMap(Collection::stream).collect(Collectors.toSet())) {

					// Some entries in the statpacket don't have weights
					// Since all data points on the same day must have the same weight, we can skip
					// those that don't have a weight value and hope that the date will get filled in
					// by one of the other data points on that day
					if (dto.getWindowWeight() != null) {
						windowData.put(dto.getDateOfExperiment().getTime(), (yMax - yMin)  * dto.getWindowWeight() + yMin);
					}
				}

				JSONArray windowDataJson = new JSONArray(windowData.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(x -> {
					JSONArray a = new JSONArray();
					a.put(x.getKey());
					a.put(x.getValue());
					return a;
				}).collect(Collectors.toList()));
				windowsSeriesName.put("data", windowDataJson);

				series.put(windowsSeriesName);
			}
		} catch (JSONException e) {
			logger.warn("JSON error creating soft windowing JSON object ", e);
		}


		ScatterChartAndData scatterChartAndData = new ScatterChartAndData();
		String chartString = createScatter(yMin, yMax, experimentNumber, parameter, series, windowSeries, experiment.getAlleleSymbol());

		//JSONStringer "helpfully" escapes forward slashes.  Need to unwrap these
		chartString = chartString.replaceAll("\\\\/", "/");
		scatterChartAndData.setChart(chartString);

		List<UnidimensionalStatsObject> unidimensionalStatsObjects;
		if(experiment.getObservationType().equals(ObservationType.unidimensional)) {
			unidimensionalStatsObjects = unidimensionalChartAndTableProvider.createUnidimensionalStatsObjects(experiment);
			scatterChartAndData.setUnidimensionalStatsObjects(unidimensionalStatsObjects);
		}
		return scatterChartAndData;
	}

	public static String getScatterChart(String divId, JSONArray data, String yTitle, String xTitle, String xTooltipPrefix, String yTooltipPrefix, String seriesTitle){
		String seriesLabel="";
		if(seriesTitle!=null){
			seriesLabel="name: 'Gene',";
		}

		return " chart = new Highcharts.Chart({ " + " colors:" + ChartColors.getFemaleMaleColorsRgba(ChartColors.alphaOpaque) + ", " +
		" chart: { type: 'scatter',	zoomType: 'xy',	renderTo: '" + divId + "'}, " +
		" title: { text: ''}, " +
		" subtitle: {	text: ''	}," +
		" credits: { enabled: false }," +
		" xAxis: { title: { enabled: true,	text: '" + xTitle + "' } }," +
		" yAxis: { title: {	text: '" + yTitle + "'		}	}, " +
		" plotOptions: { " +
				"series:{ turboThreshold:5000}, " +
				"scatter: {	point: { events: { click: function(point) { var url=\"/data/genes/\" + this.markerAcc" + ";  window.open(url); } } }, " +
				"marker: {radius: 5}, " +
				"tooltip: {headerFormat: '', pointFormat: '<b>{point.markerSymbol}</b><br>" + xTooltipPrefix +" {point.x:,.0f}<br/>" + yTooltipPrefix + "{point.y:,.0f}', hideDelay:5} }	}," +
		" series: [{	"+seriesLabel+"  data: "+ data + "}]});";

	}


	private void addScatterPoint(JSONArray dataArray, ObservationDTO control, Float dataPoint) {

		JSONArray timeAndValue = new JSONArray();
		Date date = control.getDateOfExperiment();
		//Date.UTC(1970,  9, 27)
		long dateString = date.getTime();
		timeAndValue.put(dateString);
		timeAndValue.put(dataPoint);
		dataArray.put(timeAndValue);
	}

}
