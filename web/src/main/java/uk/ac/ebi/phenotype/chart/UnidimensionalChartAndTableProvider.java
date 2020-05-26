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
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.json.JSONArray;
import org.json.JSONException;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.imits.StatusConstants;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UnidimensionalChartAndTableProvider {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	private final ImpressService impressService;

	@Inject
	public UnidimensionalChartAndTableProvider(
			@NotNull ImpressService impressService
	) {
		this.impressService = impressService;
	}

	/**
	 * return one unidimensional data set per experiment - one experiment should
	 * have one or two graphs corresponding to sex and a stats result for one
	 * table at the bottom
	 *
	 */
	public UnidimensionalDataSet doUnidimensionalData(ExperimentDTO experiment, String chartId, ParameterDTO parameter, String yAxisTitle)
			throws JSONException {
		

		long startTime = System.currentTimeMillis();
		System.out.println("start time="+System.currentTimeMillis());

		ChartData chartAndTable;
		List<UnidimensionalDataSet> unidimensionalDataSets = new ArrayList<>();

		// get control data
		List<StatisticalResultDTO> allUnidimensionalResults = new ArrayList<>();
		UnidimensionalDataSet unidimensionalDataSet = new UnidimensionalDataSet();
		unidimensionalDataSet.setExperiment(experiment);
		unidimensionalDataSet.setOrganisation(experiment.getOrganisation());
		unidimensionalDataSet.setExperimentId(experiment.getExperimentId());

		// category e.g normal, abnormal
		Map<SexType, List<List<Float>>> genderAndRawDataMap = new HashMap<>();
		List<ChartsSeriesElement> chartsSeriesElementsList = new ArrayList<>();
		for (SexType sexType : experiment.getSexes()) {
			List<List<Float>> rawData = new ArrayList<>();
			List<Float> dataFloats = new ArrayList<>();
			for (ObservationDTO control : experiment.getControls(sexType)) {

				Float dataPoint = control.getDataPoint();
				dataFloats.add(dataPoint);
			}

			ChartsSeriesElement tempElement = new ChartsSeriesElement();
			tempElement.setSexType(sexType);
			tempElement.setZygosityType(null);
			tempElement.setOriginalData(dataFloats);
			chartsSeriesElementsList.add(tempElement);

			for (ZygosityType zType : experiment.getZygosities()) {

				List<Float> mutantCounts = new ArrayList<>();
				Set<ObservationDTO> expObservationsSet;
				expObservationsSet = experiment.getMutants(sexType, zType);

				for (ObservationDTO expDto : expObservationsSet) {
					Float dataPoint = expDto.getDataPoint();
					mutantCounts.add(dataPoint);
				}
				ChartsSeriesElement tempElementExp = new ChartsSeriesElement();
				tempElementExp.setSexType(sexType);
				tempElementExp.setZygosityType(zType);
				tempElementExp.setOriginalData(mutantCounts);
				chartsSeriesElementsList.add(tempElementExp);
			}

			genderAndRawDataMap.put(sexType, rawData);
		}

		List<UnidimensionalStatsObject> unidimensionalStatsObject = createUnidimensionalStatsObjects(experiment, parameter);
		List<UnidimensionalStatsObject> unidimensionalStatsObjects = new ArrayList<>(unidimensionalStatsObject);
		Map <String, Float> boxMinMax = ChartUtils.getMinMaxXAxis(chartsSeriesElementsList, experiment);
		chartAndTable = processChartData(chartId, boxMinMax.get("min"), boxMinMax.get("max"), parameter, experiment, yAxisTitle, chartsSeriesElementsList);
		String title = "<span data-parameterStableId=\"" + parameter.getStableId() + "\">" + parameter.getName() + "</span>";
		ProcedureDTO proc = impressService.getProcedureByStableId(experiment.getProcedureStableId()) ;
		String procedureDescription = "";
		if (proc != null) {
			procedureDescription = String.format("<a href=\"%s\">%s</a>", impressService.getProcedureUrlByKey(((Long)proc.getStableKey()).toString()),  "Procedure: "+ proc.getName());
		}
		if (parameter.getStableKey() != null) {
			title = String.format("<a href=\"%s\">%s</a>", impressService.getParameterUrlByProcedureAndParameterKey(proc.getStableKey(),parameter.getStableKey()),  "Parameter: "+ parameter.getName());
		}

		unidimensionalDataSet.setChartData(chartAndTable);
		unidimensionalDataSet.setAllUnidimensionalResults(allUnidimensionalResults);
		unidimensionalDataSet.setStatsObjects(unidimensionalStatsObjects);
		unidimensionalDataSets.add(unidimensionalDataSet);
		unidimensionalDataSet.setMin(boxMinMax.get("min"));
		unidimensionalDataSet.setMax(boxMinMax.get("max"));
		unidimensionalDataSet.setTitle(title);
		unidimensionalDataSet.setSubtitle(procedureDescription);
		System.out.println("end time="+System.currentTimeMillis());
		return unidimensionalDataSet;
	}


	public List<UnidimensionalStatsObject> createUnidimensionalStatsObjects(ExperimentDTO experiment, ParameterDTO parameter) {

		return produceUnidimensionalStatsData(experiment);
	}


	private ChartData processChartData(String chartId, Float yMin, Float yMax, ParameterDTO parameter, ExperimentDTO experiment, String yAxisTitle, List<ChartsSeriesElement> chartsSeriesElementsList) throws JSONException {

		String chartString = createContinuousBoxPlotChartsString(chartId, yMin, yMax, parameter, yAxisTitle, chartsSeriesElementsList, experiment);
		ChartData cNTable = new ChartData();
		cNTable.setChart(chartString);
		return cNTable;
	}


	private String createContinuousBoxPlotChartsString(String experimentNumber, Float yMin, Float yMax,ParameterDTO parameter, String yAxisTitle,
		List<ChartsSeriesElement> chartsSeriesElementsList, ExperimentDTO experiment) throws JSONException {

		JSONArray categories    = new JSONArray();
		String    boxPlotObject = "";
		String    seriesData    = "";
		int       decimalPlaces = ChartUtils.getDecimalPlaces(experiment);
		int       column        = 0;

		List<String> boxplotData = new ArrayList<>();

		for (ChartsSeriesElement chartsSeriesElement : chartsSeriesElementsList) {
			// fist get the raw data for each column (only one column per data
			// set at the moment as we will create both the scatter and boxplots
			// here
			String categoryString = WordUtils.capitalize(chartsSeriesElement.getSexType().toString()) + " " + WordUtils.capitalize(chartsSeriesElement.getControlOrZygosityString());
			categories.put(categoryString);
			List<Float> listOfFloats = chartsSeriesElement.getOriginalData();

			PercentileComputation pc = new PercentileComputation(listOfFloats);

			List<Float> wt1 = new ArrayList<>();
			if (listOfFloats.size() > 0) {
				double Q1 = ChartUtils.getDecimalAdjustedFloat(pc.getLowerQuartile(), decimalPlaces);
				double Q3 = ChartUtils.getDecimalAdjustedFloat(pc.getUpperQuartile(), decimalPlaces);
				double IQR = Q3 - Q1;

				Float minIQR = ChartUtils.getDecimalAdjustedFloat((float) (Q1 - (1.5 * IQR)), decimalPlaces);

				// Find the actual minimum data value not lower than the minimum whisker
				Float minValue = listOfFloats.stream().sorted().filter(x -> x > minIQR).findFirst().orElse(minIQR);

				wt1.add(minValue);// minimum
				Float q1 = (float) Q1;
				wt1.add(q1);// lower quartile

				Float decFloat = ChartUtils.getDecimalAdjustedFloat(pc.getMedian(), decimalPlaces);
				wt1.add(decFloat);// median
				Float q3 = (float) Q3;
				wt1.add(q3);// upper quartile

				Float maxIQR = ChartUtils.getDecimalAdjustedFloat((float) (Q3 + (1.5 * IQR)), decimalPlaces);

				// Find the actual minimum data value not lower than the minimum whisker
				Float maxValue = listOfFloats.stream().sorted((v1, v2) -> Float.compare(v2, v1)).filter(x -> x < maxIQR).findFirst().orElse(minIQR);

				wt1.add(maxValue);// maximum.
				chartsSeriesElement.setBoxPlotArray(new JSONArray(wt1.toArray()));

				// Calculate outliers as those data values that are outside of the whiskers
				List<List<Number>> outliers = new ArrayList<>();
				for (Float value : listOfFloats.stream().filter(x -> (x < minIQR || x > maxIQR)).distinct().collect(Collectors.toList())) {
					outliers.add(Arrays.asList(column, value));
				}
				chartsSeriesElement.setBoxPlotOutliersArray(new JSONArray(outliers));
			}

			JSONArray boxPlot2DDataOutliers = chartsSeriesElement.getBoxPlotOutliersArray();
			if (boxPlot2DDataOutliers == null) {
				System.err.println("error no boxplot outlier data for this chartSeriesElemen=" + chartsSeriesElement.getName());
				boxPlot2DDataOutliers = new JSONArray();
			}

			String outliersString =  boxPlot2DDataOutliers.toString();

			String color = ChartColors.getMutantColor(ChartColors.alphaOpaque);
			if (chartsSeriesElement.getControlOrZygosityString().equals("WT")) {
				color = ChartColors.getWTColor(ChartColors.alphaTranslucid70);
			}

			boxplotData.add(String.format("{x:%s, low:%s, q1:%s, median:%s, q3:%s, high:%s, name:\"%s\", color:%s}",
			column,
			wt1.get(0),
			wt1.get(1),
			wt1.get(2),
			wt1.get(3),
			wt1.get(4),
			categoryString,
			color));


			boxPlotObject = "{ color: " + color
					+ ", type: 'scatter', name: 'Outliers', data:" + outliersString
					+ ", tooltip: { headerFormat: '<em>Genotype outlier {point.key}</em><br/>' } "
					+ ", marker: { symbol: 'circle', fillColor: 'rgba(0,0,0,0)', radius: 2, lineWidth: 1, lineColor: "+color+" } }";

			seriesData += boxPlotObject + ",";
			column++;
		}

		seriesData += "{name: 'Observations', type: 'boxplot', data: [";
		seriesData += String.join(",", boxplotData);
		seriesData += "]} ,";

		String yAxisLabel = parameter.getName() + " (" + yAxisTitle + ")";
		if (yAxisTitle.trim().isEmpty()) {
			yAxisLabel = parameter.getName();
		}

		List<String> colors = ChartColors.getFemaleMaleColorsRgba(ChartColors.alphaOpaque);
		String chartString = " chart = new Highcharts.Chart({ " + " colors:" + colors
			+ ", chart: { type: 'boxplot', renderTo: 'chart" + experimentNumber + "', style: { fontFamily: '\"Roboto\", sans-serif' }},  "
			+ " tooltip: { formatter: function () { if(typeof this.point.high === 'undefined')"
			+ "{ return '<b>Observation</b><br/>' + this.point.y; } "
			+ "else { return '<b>Genotype: ' + this.key + '</b>"
			+ "<br/>Maximum(data value) <= (UQ + 1.5 * IQR): ' + this.point.options.high + '"
			+ "<br/>Upper Quartile: ' + this.point.options.q3 + '"
			+ "<br/>Median: ' + this.point.options.median + '"
			+ "<br/>Lower Quartile: ' + this.point.options.q1 +'"
			+ "<br/>Minimum(data value) >= (LQ - 1.5 * IQR): ' + this.point.low"
			+ "; } } }    ,"
			+ " title: {  text: 'Boxplot of the data <a href=\"/help/quick-guide-to-the-website/chart-page/\" target=\"_blank\"><i class=\"fa fa-question-circle\" style=\"color: #ce6211;\"></i></a>', useHTML:true } , "
			+ " credits: { enabled: false },  "
			+ " legend: { enabled: false }, "
			+ " xAxis: { categories:  " + categories + ","
			+ " labels: { "
			+ "           rotation: -45, "
			+ "           align: 'right', "
			+ "           style: { "
			+ "              fontSize: '15px',"
			+ "              fontFamily: '\"Roboto\", sans-serif'"
			+ "         } "
			+ "     }, "
			+ " }, \n"
			+ " plotOptions: { series: { groupPadding: 0.35, pointPadding: -0.5 }, scatter: { marker: { radius: 3 } } },"
			+ " yAxis: { max: " + yMax + ",  min: " + yMin + ", labels: { }, title: { text: '" +yAxisLabel + "' }, tickAmount: 5 }, "
			+ "\n series: [" + seriesData + "] }); });";

		return chartString;
	}


	public ChartData getStatusColumnChart(Map<String , Long> values, String title, String divId, List<String> colors){
		
		String data = "[";
		// custom order & selection from Terry
		if (divId.equalsIgnoreCase("genotypeStatusChart")){
			// custom statuses to show + custom order
			data += "['" + StatusConstants.IMITS_MOUSE_STATUS_MICRO_INJECTION_IN_PROGRESS + "', " +  values.get(StatusConstants.IMITS_MOUSE_STATUS_MICRO_INJECTION_IN_PROGRESS) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_STATUS_CHIMERA_OBTAINED + "', " +  values.get(StatusConstants.IMITS_MOUSE_STATUS_CHIMERA_OBTAINED) + "], ";
			
			Long genotypeConfirmed = values.get(StatusConstants.IMITS_MOUSE_STATUS_GENOTYPE_CONFIRMED) /**+ values.get(StatusConstants.IMITS_MOUSE_STATUS_CRE_EXCISION_STARTED) + values.get(StatusConstants.IMITS_MOUSE_STATUS_CRE_EXCISION_COMPLETE)**/;
			data += "['" + StatusConstants.IMITS_MOUSE_STATUS_GENOTYPE_CONFIRMED + "', " +  genotypeConfirmed + "], ";
			
			// data += "['" + StatusConstants.IMITS_MOUSE_STATUS_CRE_EXCISION_STARTED + "', " +  values.get(StatusConstants.IMITS_MOUSE_STATUS_CRE_EXCISION_STARTED) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_STATUS_CRE_EXCISION_COMPLETE + "', " +  values.get(StatusConstants.IMITS_MOUSE_STATUS_CRE_EXCISION_COMPLETE) + "], ";
		}
		
		else if (divId.equalsIgnoreCase("phenotypeStatusChart")){
			// custom statuses to show + custom order
			data += "['" + StatusConstants.IMITS_MOUSE_PHENOTYPING_ATTEMPT_REGISTERED + "', " +  values.get(StatusConstants.IMITS_MOUSE_PHENOTYPING_ATTEMPT_REGISTERED) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_PHENOTYPING_STARTED + "', " +  values.get(StatusConstants.IMITS_MOUSE_PHENOTYPING_STARTED) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_PHENOTYPING_COMPLETE + "', " +  values.get(StatusConstants.IMITS_MOUSE_PHENOTYPING_COMPLETE) + "], ";
		}
		
		else if(divId.equalsIgnoreCase("idgChart")){
			
			data += "['" + StatusConstants.IMPC_ES_CELL_STATUS_PRODUCTION_DONE + "', " +  values.get(StatusConstants.IMPC_ES_CELL_STATUS_PRODUCTION_DONE) + "], ";
			data += "['" + StatusConstants.IMPC_MOUSE_STATUS_PRODUCTION_DONE + "', " +  values.get(StatusConstants.IMPC_MOUSE_STATUS_PRODUCTION_DONE) + "], ";
			data += "['" + StatusConstants.IMITS_MOUSE_PHENOTYPING_COMPLETE + "', " +  values.get(StatusConstants.IMITS_MOUSE_PHENOTYPING_COMPLETE) + "], ";
		}
		
		else {
			for (String key: values.keySet()){
				data += "['" + key + "', " + values.get(key) + "], ";
			}
		}
		data += "]";
		if(colors==null || colors.isEmpty()){
			colors=ChartColors.getHighDifferenceColorsRgba(50d);
		}
		String javascript = "$(function () { $('#" + divId + "').highcharts({" +
			" colors:"+colors+
        	", chart: {type: 'column' }," +
        	" title: {text: '" + title + "'}," +
        	" credits: { enabled: false },  " +
        	" xAxis: { type: 'category', labels: { rotation: -45, style: {fontSize: '11px', fontFamily: '\"Roboto\", sans-serif'} } }," +
        	" yAxis: { min: 0, title: { text: 'Number of genes' } }," +
        	" legend: { enabled: false }," +
        	" tooltip: { pointFormat: '<b>{point.y}</b>' }," +
        	" series: [{ name: 'Population',  data: " + data + "," +
            " dataLabels: { enabled: true, style: { fontSize: '13px', fontFamily: '\"Roboto\", sans-serif' } } }]" +
			" }); });";
		ChartData chartAndTable = new ChartData();
		chartAndTable.setChart(javascript);
		chartAndTable.setId("statusChart");
		return chartAndTable;
	}



	private List<UnidimensionalStatsObject> produceUnidimensionalStatsData(ExperimentDTO experiment) {

		List<StatisticalResultDTO> results = experiment.getResults();
		logger.debug("result=" + results);
		List<UnidimensionalStatsObject> statsObjects = new ArrayList<>();

		for (SexType sexType : experiment.getSexes()) {

			// Set up the controls data
			UnidimensionalStatsObject wtStatsObject = new UnidimensionalStatsObject();
			Set<ObservationDTO> controls = experiment.getControls(sexType);

			wtStatsObject = generateStats(experiment, wtStatsObject, controls, null, sexType);
			statsObjects.add(wtStatsObject);

			// set up the mutant stats data
			for (ZygosityType zType : experiment.getZygosities()) {

				UnidimensionalStatsObject tempStatsObject = new UnidimensionalStatsObject();
				Set<ObservationDTO> mutants = experiment.getMutants(sexType, zType);
				tempStatsObject = generateStats(experiment, tempStatsObject, mutants, zType, sexType);

				for (StatisticalResultDTO result : results) {
					if (ZygosityType.valueOf(result.getZygosity()).equals(zType)) {
						tempStatsObject.setResult(result);
					}
				}

				statsObjects.add(tempStatsObject);
			}
		}
		return statsObjects;
	}


	private static UnidimensionalStatsObject generateStats(ExperimentDTO experiment, UnidimensionalStatsObject tempStatsObject, Set<ObservationDTO> mutants, ZygosityType zygosity, SexType sexType) {

		tempStatsObject.setSampleSize(mutants.size());
		// do the stats to get mean and SD
		// Get a DescriptiveStatistics instance
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// Add the data
		for (ObservationDTO mutantObservationDTO : mutants) {
			stats.addValue(mutantObservationDTO.getDataPoint());
		}
		if (mutants.size() > 0) {
			int decimalPlaces = ChartUtils.getDecimalPlaces(experiment);
			Float mean = ChartUtils.getDecimalAdjustedFloat((float) stats.getMean(), decimalPlaces);
			Float sd = ChartUtils.getDecimalAdjustedFloat((float) stats.getStandardDeviation(), decimalPlaces);
			tempStatsObject.setMean(mean);
			tempStatsObject.setSd(sd);
			if (zygosity != null) {
				tempStatsObject.setZygosity(zygosity);
			}

			// Set Control vs Not control based on passed in zygosity
			tempStatsObject.setLine(zygosity == null ? "Control" : "Not control");

			if (sexType != null) {
				tempStatsObject.setSexType(sexType);
			}
		}
		// end of stats creation for table
		return tempStatsObject;
	}
}