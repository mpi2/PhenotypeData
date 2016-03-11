package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
	
	Map<String,List<ObservationDTO>>extSampleIdToObservations;

	public HistopathService(ObservationService observationService) {
		super();
		this.observationService = observationService;
	}
	
	public HistopathService(){
		
	}

	public List<HistopathPageTableRow> getTableData(List<ObservationDTO> allObservations) throws SolrServerException {
		List<HistopathPageTableRow> rows=new ArrayList<>();
		
		System.out.println("observations for histopath size with normal and abnormal="+allObservations.size());
	
		Map<String, List<ObservationDTO>> extSampleIdToObservations = screenOutObservationsThatAreNormal(allObservations);
		
		for (String sampleId : extSampleIdToObservations.keySet()) {
			System.out.println(sampleId);
			Set<String> parameterNames=new TreeSet<>();
			//probably need to split these into embryo and adult - no we dont have any histopath for embryo
			List<ObservationDTO> observationsForSample = extSampleIdToObservations.get(sampleId);
			HistopathPageTableRow row=new HistopathPageTableRow();
				for (ObservationDTO obs : observationsForSample) {
						row.setSampleId(sampleId);
						System.out.println(sampleId+" "+ obs.getParameterName()+" "+obs.getParameterStableId()+" "+obs.getObservationType()+" categoryt=" +obs.getCategory()+ " text="+obs.getTextValue()+"ontologyTermValue=");	
						parameterNames.add(obs.getParameterName());
						ImpressBaseDTO parameter = new ImpressBaseDTO(null, null, obs.getParameterStableId(), obs.getParameterName());
						
						if(obs.getObservationType().equalsIgnoreCase("categorical")){
						row.addCategoricalParam(parameter, obs.getCategory());
						}
						if(obs.getObservationType().equalsIgnoreCase("ontological")){
						
							for(int i=0;i<obs.getSubTermId().size();i++){
								System.out.println("subtermId="+obs.getSubTermId()+"subtermname="+obs.getSubTermName().get(i));
							
							OntologyBean subOntologyBean=new OntologyBean(obs.getSubTermId().get(i), obs.getSubTermName().get(i));//, obs.getSubTermDescription().get(i));
							row.addOntologicalParam(parameter,subOntologyBean);
							}
						}
						if(obs.getObservationType().equalsIgnoreCase("text")){
							row.addTextParam(parameter, obs.getTextValue());
						}
						

					
				}
				row.setParameterNames(parameterNames);
				rows.add(row);
				
			

		}
		this.extSampleIdToObservations=extSampleIdToObservations;//for debug purposes we can display the data coming back in the web page
		return rows;

	}
	
	public List<ObservationDTO> getObservationsForHistopathForGene(String acc) throws SolrServerException{
		List<ObservationDTO> observations = observationService.getObservationsByProcedureNameAndGene("Histopathology",
				acc);
		return observations;
	}
	

	public  Map<String, List<ObservationDTO>> screenOutObservationsThatAreNormal(List<ObservationDTO> observations) {
		
		Map<String, List<ObservationDTO>> extSampleIdToObservations = new HashMap<>();
		for (ObservationDTO obs : observations) {
			String externalSampeId = obs.getExternalSampleId();
			if (!extSampleIdToObservations.containsKey(externalSampeId)) {
				extSampleIdToObservations.put(externalSampeId, new ArrayList<ObservationDTO>());
			}
			
			boolean addObservation=true;
			if(obs.getObservationType().equalsIgnoreCase("categorical")){
				if(obs.getCategory().equalsIgnoreCase("0")){
					addObservation=false;
					//System.out.println("setting obs to false");
					
				}
				
			}
			if(obs.getObservationType().equalsIgnoreCase("ontological")){
				for(String name:obs.getSubTermName()){
					if(name.equalsIgnoreCase("normal"))
					addObservation=false;
					//System.out.println("setting obs to false");
					
				}
				
			}

			if(addObservation){
				extSampleIdToObservations.get(externalSampeId).add(obs);
			}

		}
		return extSampleIdToObservations;
	}

	public Map<String, List<ObservationDTO>> getObservations() {
		return this.extSampleIdToObservations;
		
	}

}
