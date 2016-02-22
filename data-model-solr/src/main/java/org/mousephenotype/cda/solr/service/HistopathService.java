package org.mousephenotype.cda.solr.service;

import java.util.List;

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

	public void getTableData(String geneAccession) throws SolrServerException{
		
		List<ObservationDTO> observations = observationService.getObservationsByProcedureNameAndGene("Histopath", geneAccession);
		for(ObservationDTO obs: observations){
			System.out.println(obs.getExternalSampleId());
		}
		
	}
	
}
