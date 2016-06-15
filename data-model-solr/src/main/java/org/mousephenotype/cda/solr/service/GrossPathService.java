package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.GrossPathPageTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GrossPathService {

	@Autowired
	ObservationService observationService;

	@Autowired
	ImageService imageService;

	Map<String, List<ObservationDTO>> extSampleIdToObservations;

	private String delimeter = " - ";

	private Map<String, SolrDocument> downloadToImgMap;

	public GrossPathService(ObservationService observationService) {
		super();
		this.observationService = observationService;
	}

	public GrossPathService() {

	}

	public List<GrossPathPageTableRow> getTableData(List<ObservationDTO> allObservations) throws SolrServerException {
		List<GrossPathPageTableRow> rows = new ArrayList<>();
		downloadToImgMap = new HashMap<String, SolrDocument>();
		System.out.println("observations for GrossPath size with normal and abnormal=" + allObservations.size());

		//List<ObservationDTO> filteredObservations = screenOutObservationsThatAreNormal(allObservations);
		Set<String> anatomyNames = this.getAnatomyNamesFromObservations(allObservations);// We
																								// want
																								// each
																								// row
																								// to
																								// represent
																								// and
																								// antomy
																								// set
																								// i.e
																								// related
																								// to
																								// Brain

		Map<String, List<ObservationDTO>> sampleToObservations = this.getSampleToObservationMap(allObservations);
		for (String sampleId : sampleToObservations.keySet()) {
			TreeSet<String> abnormalAnatomyMapPerSampleId = new TreeSet<>();
			ArrayList<String> textValuesForSampleId = new ArrayList<String>();

			for (String anatomyName : anatomyNames) {

				//System.out.println("anatomyName=" + anatomyName);
				GrossPathPageTableRow row = new GrossPathPageTableRow();
				row.setAnatomyName(anatomyName);
				row.setSampleId(sampleId);
				Set<String> parameterNames = new TreeSet<>();

				for (ObservationDTO obs : sampleToObservations.get(sampleId)) {
					// a row is a unique sampleId and anatomy combination
					if (this.getAnatomyStringFromObservation(obs) != null
							&& this.getAnatomyStringFromObservation(obs).equals(anatomyName)) {

						ImpressBaseDTO parameter = new ImpressBaseDTO(null, null, obs.getParameterStableId(),
								obs.getParameterName());
						parameterNames.add(obs.getParameterName());
						
						if (obs.getObservationType().equalsIgnoreCase("ontological")) {

							if (obs.getSubTermName() != null) {
								for (int i = 0; i < obs.getSubTermId().size(); i++) {
									if(!obs.getSubTermName().get(i).equals("no abnormal phenotype detected")){
									
									abnormalAnatomyMapPerSampleId.add(anatomyName);
									}

									OntologyBean subOntologyBean = new OntologyBean(obs.getSubTermId().get(i),
											obs.getSubTermName().get(i), obs.getSubTermDescription().get(i));// ,
									row.addOntologicalParam(parameter, subOntologyBean);
								}
							}else{
								System.out.println("subterms are null for ontological data="+obs);
							}
						}
						
						

					} else {
						// should be image here
						if (obs.getObservationType().equals("image_record")) {
							SolrDocument image = null;
							if (downloadToImgMap.containsKey(obs.getDownloadFilePath())) {
								image = downloadToImgMap.get(obs.getDownloadFilePath());
							} else {
								image = imageService.getImageByDownloadFilePath(obs.getDownloadFilePath());
								downloadToImgMap.put(obs.getDownloadFilePath(), image);
							}

							//System.out.println("image omero id=" + image.get(ImageDTO.OMERO_ID));
							parameterNames.add(obs.getParameterName());
							
								row.addImage(image);
							

						}
						
					}
					if (obs.getObservationType().equalsIgnoreCase("text")) {
						//System.out.println("Text parameter found:" +obs.getTextValue()+" sampleId="+obs.getExternalSampleId());
						textValuesForSampleId.add(obs.getTextValue());
					}

				}

				if (parameterNames.size() != 0) {
					row.setParameterNames(parameterNames);
					rows.add(row);
				}
				
				
				

			}
			if (!abnormalAnatomyMapPerSampleId.isEmpty()) {// only bother
															// checking if we
															// have abnormal
															// phenotypes for
															// this sampleId
				for (GrossPathPageTableRow row : rows) {
					if (row.getSampleId().equals(sampleId)) {// filter by sample
																// id as well
						// System.out.println("checking rows with
						// size="+rows.size()+" abnormal phenotypes with
						// size="+abnormalAnatomyMapPerSampleId.size());
						if (abnormalAnatomyMapPerSampleId.contains(row.getAnatomyName())) {
							for (String text : textValuesForSampleId) {
								// System.out.println("Text="+text);
								String[] words = text.split(" ");
								for (String word : words) {
									// System.out.println("word="+word);
									for (String anatomyWord : row.getAnatomyName().split(" ")) {
										if (anatomyWord.equalsIgnoreCase(word)) {
											// System.out.println("Text matches
											// row!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
											row.setTextValue(text);
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return rows;

	}

	public Map<String, List<ObservationDTO>> getSampleToObservationMap(List<ObservationDTO> observations) {
		Map<String, List<ObservationDTO>> map = new HashMap<>();
		for (ObservationDTO obs : observations) {
			String sampleId = obs.getExternalSampleId();
			if (!map.containsKey(sampleId)) {
				map.put(sampleId, new ArrayList<ObservationDTO>());
			}
			map.get(sampleId).add(obs);
		}
		return map;
	}

	public Set<String> getAnatomyNamesFromObservations(List<ObservationDTO> observations) {
		
		Set<String> anatomyNames = new TreeSet<>();
		for (ObservationDTO obs : observations) {
			if(obs.getObservationType().equals("ontological")){//only set anatomy if ontological as simple is bodyweight or text or image
			String anatomyString = getAnatomyStringFromObservation(obs);
			if (anatomyString != null) {
				anatomyNames.add(anatomyString);
			}
			}
		}
		return anatomyNames;
	}

	private String getAnatomyStringFromObservation(ObservationDTO obs) {
		String anatomyString = null;
		String paramName = obs.getParameterName();
		
		//for Gross path we don't have anatomy names with description etc so lets just trim.
			anatomyString = paramName.trim();
			// System.out.println("anatomyString=" + anatomyString);
		return anatomyString;
	}

	public List<ObservationDTO> getObservationsForGrossPathForGene(String acc) throws SolrServerException {
		List<ObservationDTO> observations = observationService.getObservationsByProcedureNameAndGene("Gross Pathology and Tissue Collection",
				acc, ObservationDTO.PARAMETER_NAME, ObservationDTO.PARAMETER_STABLE_ID, ObservationDTO.OBSERVATION_TYPE,
				ObservationDTO.CATEGORY, ObservationDTO.VALUE, ObservationDTO.DOWNLOAD_FILE_PATH,
				ObservationDTO.PARAMETER_ASSOCIATION_SEQUENCE_ID, ObservationDTO.SEQUENCE_ID);
		return observations;
	}

	public Map<String, List<ObservationDTO>> getObservations() {
		return this.extSampleIdToObservations;

	}

}
