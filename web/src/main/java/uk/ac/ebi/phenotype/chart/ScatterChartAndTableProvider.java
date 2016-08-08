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

import org.apache.commons.lang.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mousephenotype.cda.db.pojo.DiscreteTimePoint;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

@Service
public class ScatterChartAndTableProvider {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@Autowired
	UnidimensionalChartAndTableProvider unidimensionalChartAndTableProvider;

	@Autowired
	ImpressService impressService;

	public String createScatter(ExperimentDTO experiment, Float min, Float max, String experimentNumber, ParameterDTO parameter, JSONArray series) {

		String chartString="	$(function () { "
			+ "  chart71maleWTSI = new Highcharts.Chart({ "
			+ "     chart: {"
			+ "renderTo: 'scatter"
			+ experimentNumber + "',"
			+ "         type: 'scatter',"
			+ "         zoomType: 'xy'"

			+ "     },"
			+ "   title: {  text: 'Scatterplot of the data' },"
			+ "     xAxis: {"
			+ "       type: 'datetime',"
			+ "       labels: { "
			+ "         rotation: -45, "
			+ "         align: 'right', "
			+ "         style: { "
			+ "           fontSize: '13px', "
			+ "           fontFamily: 'Verdana, sans-serif' "
			+ "         }, "
			+ "         formatter: function() { return Highcharts.dateFormat('%b %Y', this.value); } "
			+ "       },"
			+ "       showLastLabel: true "
			+ "     }, "
			+ "     yAxis: { "
			+ "       tickAmount: 5,"
			+ (max != null ? "       max: " + max + ", " : "")
			+ (min != null ? "       min: " + min + ", " : "")
			+ "       title: { "
			+ "         text: '" + parameter.getUnitY() + "' "
			+ "       } "
			+ "     }, "
			+ "     credits: { "
			+ "       enabled: false "
			+ "     }, "
			+ "      plotOptions: { "
			+ "        scatter: { "
			+ "            marker: { "
			+ "                radius: 5, "
			+ "                states: { "
			+ "                hover: { "
			+ "                    enabled: true, "
			+ "                    lineColor: 'rgb(100,100,100)' "
			+ "               } "
			+ "           } "
			+ "       }, "
			+ "       states: { "
			+ "           hover: { "
			+ "               marker: { "
			+ "                   enabled: false "
			+ "               } "
			+ "           } "
			+ "        } "
			+ "     } "
			+ "   }, "
			+ "     tooltip: { "
			+ "          formatter: function () { "
			+ "              return '<b>' + this.series.name + '</b><br/>' + Highcharts.dateFormat('%e %b %Y', this.x) + ': ' + this.y + ' " + parameter.getUnitX() + " '; "
			+ "          } "
			+ "      }, "
			+ "     series: " +
			series.toString()
			+ "    }); console.log('HERE');"
			+ "	}); ";

		return chartString;
	}


	public ScatterChartAndData doScatterData(ExperimentDTO experiment, Float yMin, Float yMax, ParameterDTO parameter, String experimentNumber)
	throws IOException,	URISyntaxException {

		JSONArray series=new JSONArray();
		// maybe need to put these into method that can be called as repeating
		// this - so needs refactoring though there are minor differences?
		Map<String, List<DiscreteTimePoint>> lines = new HashMap<String, List<DiscreteTimePoint>>();

		for (SexType sex : experiment.getSexes()) {

			List<DiscreteTimePoint> controlDataPoints = new ArrayList<>();
			JSONObject controlJsonObject=new JSONObject();
			JSONArray dataArray=new JSONArray();

			try {
				controlJsonObject.put("name", WordUtils.capitalize(sex.name())+" "+"WT");
				JSONObject markerObject=ChartColors.getMarkerJSONObject(sex, null);
				controlJsonObject.put("marker", markerObject);
			} catch (JSONException e) {
			e.printStackTrace();
			}

			for (ObservationDTO control : experiment.getControls(sex)) {

				String docGender = control.getSex();

				if (SexType.valueOf(docGender).equals(sex)) {
					Float dataPoint = control.getDataPoint();
					logger.debug("data value=" + dataPoint);
							addScatterPoint(dataArray, control, dataPoint);
				}

			}

			try {
				controlJsonObject.put("data", dataArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			series.put(controlJsonObject);

			TimeSeriesStats stats = new TimeSeriesStats();
			List<DiscreteTimePoint> controlMeans = stats.getMeanDataPoints(controlDataPoints);
			lines.put(WordUtils.capitalize(sex.name())+" WT", controlMeans);
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
					String docGender = expDto.getSex();
					if (SexType.valueOf(docGender).equals(sex)) {
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

		ScatterChartAndData scatterChartAndData = new ScatterChartAndData();
		String chartString = createScatter(experiment, yMin, yMax, experimentNumber, parameter, series);
		scatterChartAndData.setChart(chartString);

		List<UnidimensionalStatsObject> unidimensionalStatsObjects=null;
		if(experiment.getObservationType().equals(ObservationType.unidimensional)) {
			unidimensionalStatsObjects = unidimensionalChartAndTableProvider.createUnidimensionalStatsObjects(experiment, parameter);
			scatterChartAndData.setUnidimensionalStatsObjects(unidimensionalStatsObjects);
		}
		return scatterChartAndData;
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
