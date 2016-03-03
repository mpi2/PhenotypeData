package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
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

	public List<ObservationDTO> getTableData(String geneAccession) throws SolrServerException {

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

			extSampleIdToObservations.get(externalSampeId).add(obs);

		}

		for (String sampleId : extSampleIdToObservations.keySet()) {
			System.out.println(sampleId);
			//probably need to split these into embryo and adult - no we dont have any histopath for embryo
			
			for (String observationType : observationTypesForGene) {
				for (ObservationDTO obs : extSampleIdToObservations.get(sampleId)) {
					if (observationType.equals(obs.getObservationType())) {
						System.out.println(sampleId+" "+ obs.getParameterName()+" "+obs.getParameterStableId()+" "+obs.getObservationType()+" categoryt=" +obs.getCategory()+ " text="+obs.getTextValue());

					}
				}
			}

		}
		return observations;

	}

}
