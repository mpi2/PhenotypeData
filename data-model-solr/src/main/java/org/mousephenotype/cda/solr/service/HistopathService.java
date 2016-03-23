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

	Map<String, List<ObservationDTO>> extSampleIdToObservations;

	private String delimeter = " - ";

	public HistopathService(ObservationService observationService) {
		super();
		this.observationService = observationService;
	}

	public HistopathService() {

	}

	public List<HistopathPageTableRow> getTableData(List<ObservationDTO> allObservations) throws SolrServerException {
		List<HistopathPageTableRow> rows = new ArrayList<>();
		
		System.out.println("observations for histopath size with normal and abnormal=" + allObservations.size());

		List<ObservationDTO> filteredObservations = screenOutObservationsThatAreNormal(allObservations);
		Set<String> anatomyNames=this.getAnatomyNamesFromObservations(filteredObservations);//We want each row to represent and antomy set i.e related to Brain
		
		Map<String, List<ObservationDTO>> sampleToObservations=this.getSampleToObservationMap(filteredObservations);
		for (String sampleId : sampleToObservations.keySet()) {
			for (String anatomyName : anatomyNames) {
				
				System.out.println("anatomyName=" + anatomyName);
				HistopathPageTableRow row = new HistopathPageTableRow();
				row.setAnatomyName(anatomyName);
				row.setSampleId(sampleId);
				Set<String> parameterNames = new TreeSet<>();

				
				for (ObservationDTO obs : sampleToObservations.get(sampleId)) {
					// a row is a unique sampleId and anatomy combination
					if (this.getAnatomyStringFromObservation(obs) != null
							&& this.getAnatomyStringFromObservation(obs).equals(anatomyName) ) {
						
						
						ImpressBaseDTO parameter = new ImpressBaseDTO(null, null, obs.getParameterStableId(),
								obs.getParameterName());
						parameterNames.add(obs.getParameterName());

						if (obs.getObservationType().equalsIgnoreCase("categorical")) {
							row.addCategoricalParam(parameter, obs.getCategory());
							if(parameter.getName().contains("Significance")){
								row.addSignficiance(parameter, obs.getCategory());
							}
							if(parameter.getName().contains("Severity")){
								row.addSeveirty(parameter, obs.getCategory());
							}
						}
						if (obs.getObservationType().equalsIgnoreCase("ontological")) {

							for (int i = 0; i < obs.getSubTermId().size(); i++) {
								System.out.println("subtermId=" + obs.getSubTermId() + "subtermname="
										+ obs.getSubTermName().get(i));

								OntologyBean subOntologyBean = new OntologyBean(obs.getSubTermId().get(i),
										obs.getSubTermName().get(i) , obs.getSubTermDescription().get(i));// ,
																		// obs.getSubTermDescription().get(i));
								row.addOntologicalParam(parameter, subOntologyBean);
								if(parameter.getName().contains("MPATH process term")){
									row.addMpathProcessParam(parameter, subOntologyBean);
								}
								if(parameter.getName().contains("MPATH diagnostic term")){
									row.addMpathDiagnosticParam(parameter, subOntologyBean);
								}
								if(parameter.getName().contains("PATO")){
									row.addPatoParam(parameter, subOntologyBean);
								}
							}
						}
						if (obs.getObservationType().equalsIgnoreCase("text")) {
							row.addTextParam(parameter, obs.getTextValue());
							if(obs.getParameterName().contains("Free text")){
								row.addFreeTextParam(parameter, obs.getTextValue());
							}
							if(obs.getParameterName().contains("Description")){
								row.addDescriptionTextParam(parameter, obs.getTextValue());
							}
						}

						
						
					}
					
					
				}
				
				
				if(parameterNames.size()!=0){
					row.setParameterNames(parameterNames);
					System.out.println("adding row="+row);
					rows.add(row);
				}
				
				
			}
		}

		return rows;

	}

	public  Map<String, List<ObservationDTO>> getSampleToObservationMap(List<ObservationDTO> observations) {
		Map<String, List<ObservationDTO>> map=new HashMap<>();
		for(ObservationDTO obs: observations){
			String sampleId=obs.getExternalSampleId();
			if(!map.containsKey(sampleId)){
				map.put(sampleId, new ArrayList<ObservationDTO>());
			}
			map.get(sampleId).add(obs);
		}
		return map;
	}

	public Set<String> getAnatomyNamesFromObservations(List<ObservationDTO> observations) {
		Set<String> anatomyNames = new TreeSet<>();
		for (ObservationDTO obs : observations) {
			String anatomyString = getAnatomyStringFromObservation(obs);
			if(anatomyString!=null){
				anatomyNames.add(anatomyString);
			}
		}
		return anatomyNames;
	}

	private String getAnatomyStringFromObservation(ObservationDTO obs) {
		String anatomyString=null;
		String paramName = obs.getParameterName();
		if (paramName.contains(delimeter)) {
			anatomyString = paramName.substring(0, paramName.indexOf(delimeter));
			//System.out.println("anatomyString=" + anatomyString);
		} else {
			System.out.println("no delimeter found with =" + paramName);
		}
		return anatomyString;
	}

	public List<ObservationDTO> getObservationsForHistopathForGene(String acc) throws SolrServerException {
		List<ObservationDTO> observations = observationService.getObservationsByProcedureNameAndGene("Histopathology",
				acc, ObservationDTO.PARAMETER_NAME, ObservationDTO.PARAMETER_STABLE_ID, ObservationDTO.OBSERVATION_TYPE, ObservationDTO.CATEGORY, ObservationDTO.VALUE );
		return observations;
	}

	public List<ObservationDTO> screenOutObservationsThatAreNormal(List<ObservationDTO> observations) {

		List<ObservationDTO> filteredObservations = new ArrayList<>();
		for (ObservationDTO obs : observations) {
			String externalSampeId = obs.getExternalSampleId();

			boolean addObservation = true;
			if (obs.getObservationType().equalsIgnoreCase("categorical")) {
				if (obs.getCategory().equalsIgnoreCase("0")) {
					addObservation = false;
					// System.out.println("setting obs to false");

				}

			}
			if (obs.getObservationType().equalsIgnoreCase("ontological")) {
				for (String name : obs.getSubTermName()) {
					if (name.equalsIgnoreCase("normal"))
						addObservation = false;
					// System.out.println("setting obs to false");

				}

			}

			if (addObservation) {
				filteredObservations.add(obs);
			}

		}
		return filteredObservations;
	}

	public Map<String, List<ObservationDTO>> getObservations() {
		return this.extSampleIdToObservations;

	}

}
