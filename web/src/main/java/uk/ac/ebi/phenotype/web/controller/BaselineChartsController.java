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
package uk.ac.ebi.phenotype.web.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import uk.ac.ebi.phenotype.chart.ChartColors;
import uk.ac.ebi.phenotype.chart.ChartUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Controller
public class BaselineChartsController {

	@Autowired
	ObservationService os;
	
	@Autowired
	ImpressService impressService;

	public BaselineChartsController(){
	}

	@RequestMapping(value="/baselineCharts/{phenotype_id}", method=RequestMethod.GET)
	public String getGraph(
		@PathVariable String phenotype_id,
		@RequestParam(required = true, value = "parameter_id") String parameterStableId,
		Model model) throws SolrServerException, IOException {

		List<FieldStatsInfo> baselinesForParameter = os.getStatisticsForParameterFromCenter(parameterStableId, null);
		String baseLineChart=this.generateBaselineChartBoxStyle(parameterStableId,baselinesForParameter);
		model.addAttribute("baselineChart", baseLineChart);
		return "baselineChart";
	}
	
	private String generateBaselineChartBoxStyle(String parameterStableId, List<FieldStatsInfo> baselinesForParameter) throws SolrServerException, IOException {
		ParameterDTO parameter=impressService.getParameterByStableId(parameterStableId);
		System.out.println("procedure names="+parameter.getProcedureNames());
		
		String yAxisTitle=parameter.getUnitX();
		List<String> xAxisLabels=new ArrayList<>();
		List<List<String>> boxColumns=new ArrayList<>();
		//get the number of decimal places to display
		int decimalPlaces = getDecimalPlacesToDisplay(baselinesForParameter);
		System.out.println("decimalPlaces="+decimalPlaces);
		
		
		Double yMin=new Double(0);
		Double yMax=new Double(0);
		
		for(FieldStatsInfo baseLine:baselinesForParameter){
			List<String> boxColumn=new ArrayList<String>();
			xAxisLabels.add("'"+baseLine.getName()+"'");
			if((Double)baseLine.getMin()<yMin){
				yMin=(Double)baseLine.getMin();
			}
			if((Double)baseLine.getMax()>yMax){
				yMax=(Double)baseLine.getMax();
			}
			boxColumn.add(Double.toString(ChartUtils.getDecimalAdjustedDouble((Double)baseLine.getMin(), decimalPlaces)));
			double lower = (double)baseLine.getMean()- baseLine.getStddev();
			boxColumn.add(Double.toString(ChartUtils.getDecimalAdjustedDouble((Double)lower, decimalPlaces)));
			boxColumn.add(Double.toString(ChartUtils.getDecimalAdjustedDouble((Double)baseLine.getMean(), decimalPlaces)));
			double upper = (double)baseLine.getMean()+ baseLine.getStddev();
			boxColumn.add(Double.toString(ChartUtils.getDecimalAdjustedDouble((Double)upper, decimalPlaces)));
			boxColumn.add(Double.toString(ChartUtils.getDecimalAdjustedDouble((Double)baseLine.getMax(), decimalPlaces)));
			boxColumns.add(boxColumn);
		}
		
		
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaOpaque);
		
		 String seriesData="{"
			 		+ " name: 'Observations',"
			 		+ " data:"
			 		+ boxColumns
			 		+ ","
			 		+ " tooltip: {"
			 		+ " headerFormat: '<em>Experiment No {point.key}</em><br/>'"
			 		+ " }"
			 		+ " }";
			 
			 
				String chartString = "$('#baseline-chart-div').highcharts({" + " colors:" + colors
					+ ", chart: { type: 'boxplot'},  "
					+ " tooltip: { formatter: function () { if(typeof this.point.high === 'undefined')"
					+ "{ return '<b>Observation</b><br/>' + this.point.y; } "
					+ "else { return '<b>Center: ' + this.key + '</b>"
					+ "<br/>Max: ' + this.point.options.high + '"
					+ "<br/>Mean + SD: ' + this.point.options.q3 + '"
					+ "<br/>Mean: ' + this.point.options.median + '"
					+ "<br/>Mean - SD: ' + this.point.options.q1 +'"
					+ "<br/>Min: ' + this.point.options.low"
					+ "; } } }    ,"
					+ " title: {  text: '"+parameter.getName()+" WT Variation By Center', useHTML:true } ,  subtitle: {  text: '"+parameter.getProcedureNames().get(0)+"' }, "
					+ " credits: { enabled: false },  "
					+ " legend: { enabled: false }, "
					+ " xAxis: { categories:  " + xAxisLabels + ","
					+ " labels: { "
					+ "           rotation: -45, "
					+ "           align: 'right', "
					+ "           style: { "
					+ "              fontSize: '15px',"
					+ "              fontFamily: '\"Roboto\", sans-serif'"
					+ "         } "
					+ "     }, "
					+ " }, \n"
					+ " plotOptions: { series: { pointWidth:20, groupPadding: 0.25, pointPadding: -0.5 } },"
					+ " yAxis: {  " + "max: " + yMax + ",  min: " + yMin + "," + " labels: { },title: { text: '" + yAxisTitle + "' }, tickAmount: 5 }, "
					+ "\n series: [" + seriesData + "] });";

				return chartString;
				
	
	}

	private int getDecimalPlacesToDisplay(List<FieldStatsInfo> baselinesForParameter) {
		List<String> minAndMaxStrings=new ArrayList<>();
		for(FieldStatsInfo baseLine:baselinesForParameter){
			String min=Double.toString((Double)baseLine.getMin());
			String max=Double.toString((Double)baseLine.getMax());
			minAndMaxStrings.add(min);
			minAndMaxStrings.add(max);
		}
		int decimalPlaces=ChartUtils.getDecimalPlacesFromStrings(minAndMaxStrings);
		return decimalPlaces;
	}

	private String generateBaselineChartBarStyle(String parameterStableId, List<FieldStatsInfo> baselinesForParameter) throws SolrServerException, IOException {
		ParameterDTO parameter=impressService.getParameterByStableId(parameterStableId);
		System.out.println("procedure names="+parameter.getProcedureNames());
		
		String yAxisTitle=parameter.getUnitX();
		List<String> xAxisLabels=new ArrayList();
		
		List<String> means=new ArrayList();
		for(FieldStatsInfo baseLine:baselinesForParameter){
			xAxisLabels.add("'"+baseLine.getName()+"'");
			means.add(baseLine.getMean().toString());
			System.out.println(baseLine.getMin()+ " " +baseLine.getMax()+" "+baseLine.getMean());
		}
		//[-9.7, 9.4],
		int decimalPlaces=ChartUtils.getDecimalPlacesFromStrings(means);
		System.out.println("decimalPlaces="+decimalPlaces);
		List<Float> meanFloats = getDecimalAdjustedValues(means, decimalPlaces);
		
		
		List<String> minAndMax=new ArrayList<>();
		for(FieldStatsInfo baseLine:baselinesForParameter){
		minAndMax.add("["+ChartUtils.getDecimalAdjustedFloat(new Float(baseLine.getMin().toString()), decimalPlaces)+","+new Float(baseLine.getMax().toString())+"]");
		}
		String minAndMaxData=StringUtils.join(minAndMax, ",");
		System.out.println("minAndMaxData="+minAndMaxData);
		System.out.println("means="+means);
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaOpaque);
		 String chartString="$('#baseline-chart-div').highcharts({"  + " colors:" + colors
		        + ", "+

		        " chart: {  " 
				        + " type: 'columnrange',  inverted: false },  title: { text: '"+parameter.getName()+" WT Variation By Center' }, subtitle: {  text: '"+parameter.getProcedureNames().get(0)+"' },"
				        + " plotOptions: {   series: {  states: { hover: { enabled: false  }   }  } },"
				        + "  xAxis: {"
				        + " categories: "+xAxisLabels 
				        +"},"
				        + "  yAxis: {"
				        			+ " title: { text: '"+yAxisTitle+"' }"
				        		+ "  },"
				        		+ "  tooltip: {"
				        		+ "  valueSuffix: '"+yAxisTitle+"' },"
				        		+ "  plotOptions: {"
				        		+ " columnrange: { "
				        				+ "dataLabels: {"
				        				+ "  enabled: false,   formatter: function () { return this.y + '"+yAxisTitle+"'; }"
				        				+ "   }"
				        		+ "  }"
				        		+ "}, legend: { enabled: false  }, tooltip: { enabled: false },"
				        				+ " series: "
				        				+ "[ {  name: '"+parameter.getName()+yAxisTitle+"',  data: [  "+minAndMaxData
				        				+ " ] },"
				        		+ " {  type: 'scatter', name: 'Observations', data: "+meanFloats+", marker: { radius: 4 } }]"
				        		+ "  });";
		
		 System.out.println("chartString="+chartString);
	return chartString;
	
	}

	private List<Float> getDecimalAdjustedValues(List<String> means, int decimalPlaces) {
		List<Float> meanFloats=new ArrayList<>();
		for(String mean:means){
		meanFloats.add(ChartUtils.getDecimalAdjustedFloat(new Float(mean), decimalPlaces));
		}
		return meanFloats;
	}

	

}
