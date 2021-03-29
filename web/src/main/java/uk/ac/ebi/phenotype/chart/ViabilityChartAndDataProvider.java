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
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.service.dto.ParameterDTO;
import org.mousephenotype.cda.solr.web.dto.EmbryoViability_DTO;
import org.mousephenotype.cda.solr.web.dto.ViabilityDTO;
import org.mousephenotype.cda.solr.web.dto.ViabilityDTOVersion2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ViabilityChartAndDataProvider {


	public ViabilityDTO doViabilityData(ViabilityDTO viabilityDTO, String parameterStableId) {

		viabilityDTO.setParameterStableId(parameterStableId);

		//we need 3 sets of data for the 3 graphs
		Map<String, ObservationDTO> paramStableIdToObservation = viabilityDTO.getParamStableIdToObservation();
		List<ObservationDTO> totals=new ArrayList<>();
		totals.add(paramStableIdToObservation.get(viabilityDTO.getTotalPupsWt()));
		totals.add(paramStableIdToObservation.get(viabilityDTO.getTotalPupsHom()));
		totals.add(paramStableIdToObservation.get(viabilityDTO.getTotalPupsHet()));

		List<ObservationDTO> male=new ArrayList<>();
		male.add(paramStableIdToObservation.get(viabilityDTO.getTotalMaleWt()));
		male.add(paramStableIdToObservation.get(viabilityDTO.getTotalMaleHom()));
		male.add(paramStableIdToObservation.get(viabilityDTO.getTotalMaleHet()));
		if(viabilityDTO instanceof ViabilityDTOVersion2){
			male.add(paramStableIdToObservation.get(((ViabilityDTOVersion2) viabilityDTO).getTotalMaleHem()));
			totals.add(paramStableIdToObservation.get(((ViabilityDTOVersion2) viabilityDTO).getTotalMaleHem()));
		}

		List<ObservationDTO> female=new ArrayList<>();


		female.add(paramStableIdToObservation.get(viabilityDTO.getTotalFemaleWt()));
		female.add(paramStableIdToObservation.get(viabilityDTO.getTotalFemaleHom()));
		female.add(paramStableIdToObservation.get(viabilityDTO.getTotalFemaleHet()));
		if(viabilityDTO instanceof ViabilityDTOVersion2){
			female.add(paramStableIdToObservation.get(((ViabilityDTOVersion2) viabilityDTO).getTotalFemaleAnz()));
		}

		Map<String, Integer> totalLabelToNumber = new LinkedHashMap<>();
		for (ObservationDTO ob : totals) {
			if (Math.round(ob.getDataPoint()) > 0) {
				totalLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
			}
		}
		String totalChart = PieChartCreator.getPieChart(totalLabelToNumber, "totalChart-"+parameterStableId, "Total Counts (Male and Female)", "", ChartColors.getZygosityColorMap());
		viabilityDTO.setTotalChart(totalChart);

		Map<String, Integer> maleLabelToNumber = new LinkedHashMap<>();
		for(ObservationDTO ob:male){
			if(Math.round(ob.getDataPoint())>0){
			maleLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
			}
		}
		String maleChart = PieChartCreator.getPieChart(maleLabelToNumber, "maleChart-"+parameterStableId, "Male Counts", "", ChartColors.getZygosityColorMap());
		viabilityDTO.setMaleChart(maleChart);

		Map<String, Integer> femaleLabelToNumber = new LinkedHashMap<>();
		for(ObservationDTO ob:female){
			if(Math.round(ob.getDataPoint())>0){
			femaleLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
			}
		}
		String femaleChart = PieChartCreator.getPieChart(femaleLabelToNumber, "femaleChart-"+parameterStableId, "Female Counts", "", ChartColors.getZygosityColorMap());
		viabilityDTO.setFemaleChart(femaleChart);
		return viabilityDTO;
	}


	public EmbryoViability_DTO doEmbryo_ViabilityData(ParameterDTO parameter, EmbryoViability_DTO embryoViability_DTO) {
		//we need 3 sets of data for the 3 graphs
		Map<String, ObservationDTO> paramStableIdToObservation = embryoViability_DTO.getParamStableIdToObservation();

		List<ObservationDTO> total=new ArrayList<>();
		total.add(paramStableIdToObservation.get(embryoViability_DTO.parameters.totalEmbryosWt));
		total.add(paramStableIdToObservation.get(embryoViability_DTO.parameters.totalEmbryosHet));
		total.add(paramStableIdToObservation.get(embryoViability_DTO.parameters.totalEmbryosHom));
		System.out.println(embryoViability_DTO.parameters.totalEmbryosWt);


		List<ObservationDTO> dead=new ArrayList<>();
		dead.add(paramStableIdToObservation.get(embryoViability_DTO.parameters.totalDeadEmbryosWt));
		dead.add(paramStableIdToObservation.get(embryoViability_DTO.parameters.totalDeadEmbryosHet));
		dead.add(paramStableIdToObservation.get(embryoViability_DTO.parameters.totalDeadEmbryosHom));


		List<ObservationDTO> live=new ArrayList<>();
		live.add(paramStableIdToObservation.get(embryoViability_DTO.parameters.totalLiveEmbryosWt));
		live.add(paramStableIdToObservation.get(embryoViability_DTO.parameters.totalLiveEmbryosHet));
		live.add(paramStableIdToObservation.get(embryoViability_DTO.parameters.totalLiveEmbryosHom));


		Map<String, Integer> totalLabelToNumber = new LinkedHashMap<>();
		for(ObservationDTO ob:total){
			if(ob != null){
				if(Math.round(ob.getDataPoint())>0){
					totalLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
				}
			}
		}
		if(totalLabelToNumber.size() > 0){
			String totalChart = PieChartCreator.getPieChart(totalLabelToNumber, "totalChart", "Total Counts (Dead and Live)", "", ChartColors.getZygosityColorMap());
			embryoViability_DTO.setTotalChart(totalChart);
		}

		Map<String, Integer> deadLabelToNumber = new LinkedHashMap<>();
		for(ObservationDTO ob:dead){
			if(ob != null){
				if(Math.round(ob.getDataPoint())>0){
					deadLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
				}
			}
		}
		if(deadLabelToNumber.size() > 0){
			String deadChart = PieChartCreator.getPieChart(deadLabelToNumber, "deadChart", "Dead Counts", "", ChartColors.getZygosityColorMap());
			embryoViability_DTO.setDeadChart(deadChart);
		}

		Map<String, Integer> liveLabelToNumber = new LinkedHashMap<>();
		for(ObservationDTO ob:live){
			if(ob != null){
				if(Math.round(ob.getDataPoint())>0){
					liveLabelToNumber.put(WordUtils.capitalize(ob.getParameterName()), Math.round(ob.getDataPoint()));
				}
			}
		}
		if (liveLabelToNumber.size() > 0){
			String liveChart = PieChartCreator.getPieChart(liveLabelToNumber, "liveChart", "Live Counts", "", ChartColors.getZygosityColorMap());
			embryoViability_DTO.setLiveChart(liveChart);
		}

		return embryoViability_DTO;

	}

	
}
