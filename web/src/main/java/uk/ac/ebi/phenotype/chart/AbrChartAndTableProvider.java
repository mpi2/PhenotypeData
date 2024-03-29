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

import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.common.Constants;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.ExperimentService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.service.dto.ProcedureDTO;
import org.mousephenotype.cda.solr.service.exception.SpecificExperimentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AbrChartAndTableProvider {

	/*
	 * IMPC_ABR_002_001 -- click
	 * IMPC_ABR_004_001 -- 6
	 * IMPC_ABR_006_001 -- 12
	 * IMPC_ABR_008_001	-- 18
	 * IMPC_ABR_010_001 -- 24
	 * IMPC_ABR_012_001 -- 30
	 *
	 */
	@Autowired
	ExperimentService es;

	@Autowired
	ImpressService impressService;

	public ChartData getAbrChartAndData(ExperimentDTO experiment, ParameterDTO parameter, String chartId, String solrURL) throws IOException, SolrServerException {

		ChartData chartData = new ChartData();

		chartData.setExperiment(experiment);

		chartData.setParameter(parameter);

		chartData.setChart(getChart(
				experiment.getPipelineStableId(),
				experiment.getProcedureStableId(),
				experiment.getMarkerAccession(),
				experiment.getSexes().stream().map(SexType::getName).collect(Collectors.toList()),
				experiment.getZygosities().stream().map(ZygosityType::getName).collect(Collectors.toList()),
				experiment.getOrganisation(),
				experiment.getStrain(),
				experiment.getMetadataGroup(),
				experiment.getAlleleAccession(),
				chartId,
				solrURL
				));

		return chartData;
	}

	public String getChart(
			String pipelineStableId,
			String procedureStableId,
			String acc,
			List<String> genderList,
			List<String> zyList,
			String phenotypingCenter,
			String strain,
			String metadataGroup,
			String alleleAccession,
			String chartId,
			String ebiMappedSolrUrl
	) throws SolrServerException, IOException {

    	Map<String, ArrayList<UnidimensionalStatsObject>> data = new HashMap<>(); // <control/experim, ArrayList<dataToPlot>>
    	data.put(ChartUtils.getLabel(null, SexType.female), new ArrayList<>() );
    	data.put(ChartUtils.getLabel(null,  SexType.male), new ArrayList<>() );
    	for (String zygosity: zyList){
        	data.put(ChartUtils.getLabel(ZygosityType.valueOf(zygosity), SexType.male), new ArrayList<>() );
        	data.put(ChartUtils.getLabel(ZygosityType.valueOf(zygosity), SexType.female), new ArrayList<>() );
    	}

		UnidimensionalStatsObject emptyObj = new UnidimensionalStatsObject();
		emptyObj.setMean(null);
		emptyObj.setSd(null);

		Set<ZygosityType> zygosities = null;
    	String procedureUrl = null;
    	String unit = impressService.getParameterByStableId(Constants.ABR_PARAMETERS.get(1)).getUnitX();

//    	for (String parameterStableId : Constants.ABR_PARAMETERS){
//
//    		ParameterDTO parameter = impressService.getParameterByStableId(parameterStableId);
//    		try {
//    			ExperimentDTO experiment = es.getSpecificExperimentDTO(parameterStableId, pipelineStableId, acc, genderList, zyList, phenotypingCenter, strain, metadataGroup, alleleAccession, ebiMappedSolrUrl);
//
//    			if (experiment != null){
//			    	zygosities = experiment.getZygosities();
//			    	Set<SexType> sexes = experiment.getSexes();
//					if (procedureUrl == null){
//						ProcedureDTO proc = impressService.getProcedureByStableId(experiment.getProcedureStableId()) ;
//						if (proc != null) {
//							procedureUrl = String.format("<a href=\"%s\">%s</a>", impressService.getProcedureUrlByKey(((Long)proc.getStableKey()).toString()), proc.getName());
//						}
//					}
//					for (SexType sex : sexes){
//						UnidimensionalStatsObject tempMeans = getMeans( sex, null, experiment);
//						String tempLabel = ChartUtils.getLabel(null, sex);
//						ArrayList<UnidimensionalStatsObject> tempData = data.get(tempLabel);
//						tempData.add(tempMeans);
//						for (ZygosityType z : zygosities){
//							data.get(ChartUtils.getLabel(z, sex)).add(getMeans(sex, z, experiment));
//						}
//					}
//				}
//				else {
//					emptyObj.setLabel(parameter.getName());
//			    	data.get(ChartUtils.getLabel(null,  SexType.female)).add(emptyObj);
//			    	data.get(ChartUtils.getLabel(null,  SexType.male)).add(emptyObj);
//					for (String z : zyList){
//						data.get(ChartUtils.getLabel(ZygosityType.valueOf(z),  SexType.male)).add(emptyObj);
//						data.get(ChartUtils.getLabel(ZygosityType.valueOf(z),  SexType.female)).add(emptyObj);
//					}
//				}
//    		} catch (SolrServerException | IOException | SpecificExperimentException e) {
//				e.printStackTrace();
//			}
//    	}

		for (String parameterStableId : Constants.ABR_PARAMETERS){
			ExperimentDTO lastExperiment = null;
			for (String zygosity : zyList){
				ParameterDTO parameter = impressService.getParameterByStableId(parameterStableId);
				ExperimentDTO experiment = es.getSpecificExperimentDTO(pipelineStableId, procedureStableId, parameterStableId,  alleleAccession, phenotypingCenter, zygosity, strain, metadataGroup);
				lastExperiment = experiment;
				zygosities = experiment.getZygosities();
    			if (experiment.getExperimentId() != null){
			    	Set<SexType> sexes = experiment.getSexes();
			    	sexes = sexes == null || (sexes.size() == 1 && sexes.contains(SexType.not_considered)) ? Stream.of(SexType.male, SexType.female).collect(Collectors.toSet()) : sexes;
					if (procedureUrl == null){
						ProcedureDTO proc = impressService.getProcedureByStableId(experiment.getProcedureStableId()) ;
						if (proc != null) {
							procedureUrl = String.format("<a href=\"%s\">%s</a>", impressService.getProcedureUrlByKey(((Long)proc.getStableKey()).toString()), proc.getName());
						}
					}
					for (SexType sex : sexes){
						UnidimensionalStatsObject tempMeans = getMeans( sex, ZygosityType.getByDisplayName(zygosity), experiment);
						String tempLabel = ChartUtils.getLabel(ZygosityType.getByDisplayName(zygosity), sex);
						ArrayList<UnidimensionalStatsObject> tempData = data.get(tempLabel);
						tempData.add(tempMeans);
					}
				}
				else {
					emptyObj.setLabel(parameter.getName());
			    	data.get(ChartUtils.getLabel(null,  SexType.female)).add(emptyObj);
			    	data.get(ChartUtils.getLabel(null,  SexType.male)).add(emptyObj);
					data.get(ChartUtils.getLabel(ZygosityType.getByDisplayName(zygosity),  SexType.female)).add(emptyObj);
					data.get(ChartUtils.getLabel(ZygosityType.getByDisplayName(zygosity),  SexType.male)).add(emptyObj);
				}
			}
			if(lastExperiment.getExperimentId() != null) {
				Set<SexType> sexes = lastExperiment.getSexes();
				sexes = sexes == null || (sexes.size() == 1 && sexes.contains(SexType.not_considered)) ? Stream.of(SexType.male, SexType.female).collect(Collectors.toSet()) : sexes;
				for (SexType sex : sexes){
					UnidimensionalStatsObject tempMeans = getMeans( sex, null, lastExperiment);
					String tempLabel = ChartUtils.getLabel(null, sex);
					ArrayList<UnidimensionalStatsObject> tempData = data.get(tempLabel);
					tempData.add(tempMeans);
				}
			}
		}

		return getCustomChart(data, procedureUrl, unit, zygosities, chartId);
	}


	public String getCustomChart(Map<String, ArrayList<UnidimensionalStatsObject>> data, String procedureLink, String unit, Set<ZygosityType> zygosities, String chartId) throws SolrServerException, IOException{

		JSONArray categories = new JSONArray();
		String title = "Evoked ABR Threshold (6, 12, 18, 24, 30 kHz)";

		Map<String, JSONArray> standardDeviation = new LinkedMap();
		Map<String, JSONArray> lines = new LinkedMap();

		Integer decimalNumber = 2;
		List<String> colors = ChartColors.getHighDifferenceColorsRgba(ChartColors.alphaTranslucid50);
		String empty = null;
		JSONArray emptyObj = new JSONArray();
		emptyObj.put("");
		emptyObj.put(empty);
		emptyObj.put(empty);

		List<SexType> sexes = new ArrayList<>();
		sexes.add(SexType.male);
		sexes.add(SexType.female);

		for (String abrId: Constants.ABR_PARAMETERS){
			categories.put(impressService.getParameterByStableId(abrId).getName());
			try {
				categories.put(1, ""); // empty category with null data so that the points won't be connected
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {

			for (SexType sex : sexes){

				boolean first = true;
				String label = ChartUtils.getLabel(null, sex);

				for (UnidimensionalStatsObject c : data.get(label)){

					JSONArray obj = new JSONArray();
					obj.put(c.getLabel());
					obj.put(c.getMean());

					JSONArray sdobj = new JSONArray();
					sdobj.put(c.getLabel());

					if (c.getMean() != null){
						sdobj.put(c.getMean() - c.getSd());
						sdobj.put(c.getMean() + c.getSd());
					}else {
						sdobj.put(empty);
						sdobj.put(empty);
					}

					if(first) {
						standardDeviation.put(label, new JSONArray());
						lines.put(label, new JSONArray());
						first = false;
						lines.get(label).put(obj);
						lines.get(label).put(emptyObj);
						standardDeviation.get(label).put(sdobj);
						standardDeviation.get(label).put(emptyObj);
					} else {
						lines.get(label).put(obj);
						standardDeviation.get(label).put(sdobj);
					}
				}

				for (ZygosityType zyg : zygosities){

					first = true;
					label = ChartUtils.getLabel(zyg, sex);

					for (UnidimensionalStatsObject mutant : data.get(label)){
						JSONArray obj = new JSONArray();
						obj.put(mutant.getLabel());
						obj.put(mutant.getMean());

						JSONArray sdobj = new JSONArray();
						sdobj.put(mutant.getLabel());

						if (mutant.getMean() != null){
							sdobj.put(mutant.getMean() - mutant.getSd());
							sdobj.put(mutant.getMean() + mutant.getSd());
						}else {
							sdobj.put(empty);
							sdobj.put(empty);
						}
						if(first) {
							first = false;
							standardDeviation.put(label, new JSONArray());
							lines.put(label, new JSONArray());
							// add empty datapoint too to keep the click separated
							lines.get(label).put(obj);
							lines.get(label).put(emptyObj);
							standardDeviation.get(label).put(sdobj);
							standardDeviation.get(label).put(emptyObj);
						} else {
							lines.get(label).put(obj);
							standardDeviation.get(label).put(sdobj);
						}

					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String chart =
			"$(function () {"+
				"$('#" + chartId + "').highcharts({"+
				"  title: { text: '" + title + "' },"+
				"  subtitle: {  useHTML: true,  text: '" + procedureLink + "'}, " +
				"  plotOptions: { column: {\"clip\": false} }, " +
				"  xAxis: {   categories: "  + categories + "},"+
				"  yAxis: {   title: {    text: '" + unit + "'  }, min:0, max:120, tickInterval: 20 },"+
				"  tooltip: {valueSuffix: ' " + unit + "', shared:true },"+
					"style: { fontFamily: '\"Roboto\", sans-serif;'\n  }," +
				"  legend: { },"+
				"  credits: { enabled: false },  " +
				"  series: [ ";

				for (SexType sex: sexes){
					for (ZygosityType zyg: zygosities){
						String label = ChartUtils.getLabel(zyg, sex);
						if(lines.get(label)!=null){
						chart += "   { name: '"+ label + "'," +
						"    data: " + lines.get(label).toString() + "," +
						"    zIndex: 1," +
						"    color: " + colors.get(1) + "," +
						"    tooltip: { pointFormat: '<span style=\"font-weight: bold; color: {series.color}\">{series.name}</span>: <b>{point.y:." + decimalNumber + "f}</b>' }," +
						"  }, {"+
						"    name: '" + label + " SD'," +
						"    data: " + standardDeviation.get(label).toString() + "," +
						"    type: 'errorbar',"+
						"    linkedTo: ':previous',"+
						"    color: " + colors.get(1) +","+
						"    tooltip: { pointFormat: ' (SD: {point.low:." + decimalNumber + "f} - {point.high:." + decimalNumber + "f} )<br/>', shared:true }" +
						"  },";
						}
					}

					String label = ChartUtils.getLabel(null, sex);
					if(lines.get(label)!=null){
					chart += "{" +
					"    name: '" + label + "',"+
					"    data: " + lines.get(label).toString() + "," +
					"    zIndex: 1,"+
					"    color: "+ colors.get(0) +", " +
					"    tooltip: { pointFormat: '<span style=\"font-weight: bold; color: {series.color}\">{series.name}</span>: <b>{point.y:." + decimalNumber + "f}</b>' }" +
					"  }, {" +
					"    name: '" + label + " SD',"+
					"    data: " + standardDeviation.get(label).toString() + "," +
					"    type: 'errorbar',"+
					"    linkedTo: ':previous',"+
					"    color: "+ colors.get(0) +","+
					"    tooltip: { pointFormat: ' (SD: {point.low:." + decimalNumber + "f} - {point.high:." + decimalNumber + "f}) <br/>' }" +
					"  },";
					}
				}
				chart += " ]" +
				"});" +
			"});" ;

		return chart;
		}

	public UnidimensionalStatsObject getMeans(SexType sex, ZygosityType zyg, ExperimentDTO exp){

		DescriptiveStatistics     stats      = new DescriptiveStatistics();
		UnidimensionalStatsObject res        = new UnidimensionalStatsObject();
		Set<ObservationDTO>       dataPoints = null;

		if (zyg == null){
			dataPoints = exp.getControls(sex);
		} else{
			dataPoints = exp.getMutants(sex, zyg);
			res.setAllele(exp.getAlleleAccession());
			res.setLine("Not control");
			res.setGeneticBackground(exp.getStrain());
		}

		if (dataPoints != null){
			for (ObservationDTO obs : dataPoints){
				stats.addValue(obs.getDataPoint());;
			}
			int decimalPlaces = ChartUtils.getDecimalPlaces(exp);
			res.setMean(!Double.isNaN(stats.getMean()) ? ChartUtils.getDecimalAdjustedFloat(new Float(stats.getMean()), decimalPlaces) : null);
			res.setSampleSize(dataPoints.size());
			res.setSd(!Double.isNaN(stats.getStandardDeviation()) ? ChartUtils.getDecimalAdjustedFloat(new Float(stats.getStandardDeviation()), decimalPlaces) : null);
		}
		return res;
	}
}