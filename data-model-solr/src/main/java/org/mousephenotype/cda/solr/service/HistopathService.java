package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HistopathService {

	@Autowired
	ObservationService observationService;

	public HistopathService(ObservationService observationService) {
		super();
		this.observationService = observationService;
	}
	
	public HistopathService(){
		
	}

	public Map<String, List<ObservationDTO>> getTableData(String geneAccession) throws SolrServerException {
		List<HistopathPageTableRow> rows=new ArrayList<>();
		List<ObservationDTO> observations = observationService.getObservationsByProcedureNameAndGene("Histopathology",
				geneAccession);
		Map<String, List<ObservationDTO>> extSampleIdToObservations = new HashMap<>();
		Set<String> observationTypesForGene = new HashSet<>();
		for (ObservationDTO obs : observations) {
			String externalSampeId = obs.getExternalSampleId();
			if (!extSampleIdToObservations.containsKey(externalSampeId)) {
				extSampleIdToObservations.put(externalSampeId, new ArrayList<ObservationDTO>());
			}
			if (!observationTypesForGene.contains(obs.getObservationType())) {
				observationTypesForGene.add(obs.getObservationType());
			}
			
			boolean addObservation=true;
			if(obs.getObservationType().equalsIgnoreCase("categorical")){
				if(obs.getCategory().equalsIgnoreCase("0")){
					addObservation=false;
					System.out.println("setting obs to false");
					
				}
				
			}
			if(obs.getObservationType().equalsIgnoreCase("ontological")){
				for(String name:obs.getSubTermName()){
					if(name.equalsIgnoreCase("normal"))
					addObservation=false;
					System.out.println("setting obs to false");
					
				}
				
			}

			if(addObservation){
				extSampleIdToObservations.get(externalSampeId).add(obs);
			}

		}

		for (String sampleId : extSampleIdToObservations.keySet()) {
			System.out.println(sampleId);
			//probably need to split these into embryo and adult - no we dont have any histopath for embryo
			List<ObservationDTO> observationsForSample = extSampleIdToObservations.get(sampleId);
			for (String observationType : observationTypesForGene) {
				for (ObservationDTO obs : extSampleIdToObservations.get(sampleId)) {
					if (observationType.equals(obs.getObservationType())) {
						System.out.println(sampleId+" "+ obs.getParameterName()+" "+obs.getParameterStableId()+" "+obs.getObservationType()+" categoryt=" +obs.getCategory()+ " text="+obs.getTextValue()+"ontologyTermValue=");
						
//						ImpressBaseDTO procedure  = new ImpressBaseDTO(null, null, obs.getProcedureStableId(), obs.getProcedureName());
//				    	ImpressBaseDTO parameter = new ImpressBaseDTO(null, null, obs.getParameterStableId(), obs.getParameterName());
//				    	ImpressBaseDTO pipeline = new ImpressBaseDTO(null, null, obs.getPipelineStableId(), obs.getPipelineName());
//				    	ZygosityType zygosity = obs.getZygosity() != null ? ZygosityType.valueOf(obs.getZygosity()) : ZygosityType.not_applicable;
						
						//HistopathPageTableRow row=new HistopathPageTableRow();

					}
				}
			}

		}
		return extSampleIdToObservations;

	}

}
