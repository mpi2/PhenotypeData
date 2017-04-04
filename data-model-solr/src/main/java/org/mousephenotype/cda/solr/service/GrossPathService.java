package org.mousephenotype.cda.solr.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.GrossPathPageTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class GrossPathService {

	@Autowired
	ObservationService observationService;

	@Autowired
	ImageService imageService;

	Map<String, List<ObservationDTO>> extSampleIdToObservations;

	private String delimeter = " - ";


	public GrossPathService(ObservationService observationService, ImageService imageService) {
		super();
		this.observationService = observationService;
		this.imageService=imageService;
	}

	public GrossPathService() {

	}
	
	public List<GrossPathPageTableRow> getSummaryTableData(List<ObservationDTO> allObservations, List<SolrDocument> images, List<ObservationDTO> abnormaObservations) throws SolrServerException, IOException {
		List<GrossPathPageTableRow> rows = new ArrayList<>();
		System.out.println("observations for GrossPath size with abnormal=" + allObservations.size());
		Map<String, List<ObservationDTO>> anatomyToObservationMap = this.getAnatomyNamesToObservationsMap(abnormaObservations);//only look at abnormal anatomies now as summary view
			ArrayList<String> textValuesForSampleId = new ArrayList<String>();
			for (String anatomyName : anatomyToObservationMap.keySet()) {				
				int abnormalObservations=0;
				//for summary we want the rows split on a key of anatomy||zygosity||normal/abnormal
				Map<String, List<ObservationDTO>> keysForRow = this.generateKeyMapForAnatomy(anatomyToObservationMap.get(anatomyName));
				for (String key : keysForRow.keySet()) {	
					System.out.println("key for row is "+key);
					GrossPathPageTableRow row = new GrossPathPageTableRow();
					row.setAnatomyName(anatomyName);
						for(ObservationDTO obs: keysForRow.get(key)){
						
						
						//row.setSampleId(obs.getExternalSampleId());				
						Set<String> parameterNames = new TreeSet<>();
							row.setZygosity(obs.getZygosity().substring(0, 3).toUpperCase());
							ImpressBaseDTO parameter = new ImpressBaseDTO(null, null, obs.getParameterStableId(),
									obs.getParameterName());
							parameterNames.add(obs.getParameterName());
							
							if (obs.getObservationType().equalsIgnoreCase("ontological")) {
	
								if (obs.getSubTermName() != null) {
									for (int i = 0; i < obs.getSubTermId().size(); i++) {
										if(!obs.getSubTermName().get(i).equals("no abnormal phenotype detected")){
										
											abnormalObservations++;
										}
										if(obs.getSubTermId().get(i).contains("MP:")){//for the moment lets keep things simple and restrict to MP only as PATO doesn't seem useful as is.
										OntologyBean subOntologyBean = new OntologyBean(obs.getSubTermId().get(i),
												obs.getSubTermName().get(i), obs.getSubTermDescription().get(i));// ,
										row.addOntologicalParam(parameter, subOntologyBean);
										}
									}
								}else{
									System.out.println("subterms are null for ontological data="+obs);
								}
							}
							if (obs.getObservationType().equalsIgnoreCase("text")) {
								//System.out.println("Text parameter found:" +obs.getTextValue()+" sampleId="+obs.getExternalSampleId());
								row.setTextValue(obs.getTextValue());
							}
	
						if (parameterNames.size() != 0) {
//							row.setParameterNames(parameterNames);
//							//row.setNumberOfAbnormalPhenotypes();
//							rows.add(row);
						}
					}
						//row.setParameterNames(parameterNames);
						//row.setNumberOfAbnormalPhenotypes();
						rows.add(row);
				}
			}
//			if (!abnormalAnatomyMapPerSampleId.isEmpty()) {// only bother
//															// checking if we
//															// have abnormal
//															// phenotypes for
//															// this sampleId
//				for (GrossPathPageTableRow row : rows) {
//					if (row.getSampleId().equals(sampleId)) {// filter by sample
//																// id as well
//						// System.out.println("checking rows with
//						// size="+rows.size()+" abnormal phenotypes with
//						// size="+abnormalAnatomyMapPerSampleId.size());
//						if (abnormalAnatomyMapPerSampleId.contains(row.getAnatomyName())) {
//							for (String text : textValuesForSampleId) {
//								// System.out.println("Text="+text);
//								String[] words = text.split(" ");
//								for (String word : words) {
//									// System.out.println("word="+word);
//									for (String anatomyWord : row.getAnatomyName().split(" ")) {
//										if (anatomyWord.equalsIgnoreCase(word)) {
//											// System.out.println("Text matches
//											// row!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//											row.setTextValue(text);
//											
//										}
//									}
//								}
//							}
//							
//							if(sampleToImages.containsKey(sampleId)){
//								row.addImages(sampleToImages.get(sampleId));
//							}
//						}
//					}
//				}
//			}
		//}

		return rows;

	}

	private Map<String, List<ObservationDTO>> generateKeyMapForAnatomy(List<ObservationDTO> list) {
		//for summary we want the rows split on a key of anatomy||zygosity||normal/abnormal
		Map<String, List<ObservationDTO> >keyMap=new HashMap<>();
		for(ObservationDTO obs:list){
			String key=this.getAnatomyStringFromObservation(obs)+"||"+obs.getZygosity()+"||"+this.getNormalOrAbnormalStringFromObservation(obs);
			if(keyMap.containsKey(key)){
				keyMap.get(key).add(obs);
			}else{
				List<ObservationDTO> tempList=new ArrayList<>();
				tempList.add(obs);
				keyMap.put(key, tempList);
			}
		}
		return keyMap;
	}

	public List<GrossPathPageTableRow> getTableData(List<ObservationDTO> allObservations, List<SolrDocument> images) throws SolrServerException, IOException {
		List<GrossPathPageTableRow> rows = new ArrayList<>();
		
		System.out.println("observations for GrossPath size with abnormal=" + allObservations.size());

		//List<ObservationDTO> filteredObservations = screenOutObservationsThatAreNormal(allObservations);
		Map<String, List<ObservationDTO>> anatomyNames = this.getAnatomyNamesToObservationsMap(allObservations);// We
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
		Map<String, List<SolrDocument>> sampleToImages = this.getSampleToImagesMap(images);
		
		
		for (String sampleId : sampleToObservations.keySet()) {
			TreeSet<String> abnormalAnatomyMapPerSampleId = new TreeSet<>();
			ArrayList<String> textValuesForSampleId = new ArrayList<String>();

			for (String anatomyName : anatomyNames.keySet()) {
				
				GrossPathPageTableRow row = new GrossPathPageTableRow();
				row.setAnatomyName(anatomyName);
				row.setSampleId(sampleId);
				
				Set<String> parameterNames = new TreeSet<>();

				for (ObservationDTO obs : sampleToObservations.get(sampleId)) {
					// a row is a unique sampleId and anatomy combination
					if (this.getAnatomyStringFromObservation(obs) != null
							&& this.getAnatomyStringFromObservation(obs).equals(anatomyName)) {
						row.setZygosity(obs.getZygosity().substring(0, 3).toUpperCase());

						ImpressBaseDTO parameter = new ImpressBaseDTO(null, null, obs.getParameterStableId(),
								obs.getParameterName());
						parameterNames.add(obs.getParameterName());
						
						if (obs.getObservationType().equalsIgnoreCase("ontological")) {

							if (obs.getSubTermName() != null) {
								for (int i = 0; i < obs.getSubTermId().size(); i++) {
									if(!obs.getSubTermName().get(i).equals("no abnormal phenotype detected")){
									
									abnormalAnatomyMapPerSampleId.add(anatomyName);
									}
									if(obs.getSubTermId().get(i).contains("MP:")){//for the moment lets keep things simple and restrict to MP only as PATO doesn't seem useful as is.
									OntologyBean subOntologyBean = new OntologyBean(obs.getSubTermId().get(i),
											obs.getSubTermName().get(i), obs.getSubTermDescription().get(i));// ,
									row.addOntologicalParam(parameter, subOntologyBean);
									}
								}
							}else{
								System.out.println("subterms are null for ontological data="+obs);
							}
						}
						
						

					} else {
						// should be image here
						
						
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
							
							if(sampleToImages.containsKey(sampleId)){
								row.addImages(sampleToImages.get(sampleId));
							}
						}
					}
				}
			}
		}

		return rows;

	}

	
	private String getNormalOrAbnormalStringFromObservation(ObservationDTO obs){
		String normalOrAbnormal="";
		int abnormalObservations=0;
		if (obs.getSubTermName() != null) {
			for (int i = 0; i < obs.getSubTermId().size(); i++) {
				if(!obs.getSubTermName().get(i).equals("no abnormal phenotype detected")){
				
					abnormalObservations++;
				}
				if(obs.getSubTermId().get(i).contains("MP:")){//for the moment lets keep things simple and restrict to MP only as PATO doesn't seem useful as is.
				OntologyBean subOntologyBean = new OntologyBean(obs.getSubTermId().get(i),
						obs.getSubTermName().get(i), obs.getSubTermDescription().get(i));// ,
				//row.addOntologicalParam(parameter, subOntologyBean);
				normalOrAbnormal+=obs.getSubTermName();
				}
			}
		}
		System.out.println("abnormalObservations size in getString="+abnormalObservations);
		System.out.println("normal or abnormal="+normalOrAbnormal);
		if(abnormalObservations>0){
			return "Abnormal";
		}
		return "Normal";
	}
	private Map<String, List<SolrDocument>> getSampleToImagesMap(List<SolrDocument> images) {
		Map<String, List<SolrDocument>> sampleToImagesMap=new HashMap<>();
		
		for(SolrDocument image:images){
			if(sampleToImagesMap.containsKey(image.get(ObservationDTO.EXTERNAL_SAMPLE_ID))){
				sampleToImagesMap.get(image.get(ObservationDTO.EXTERNAL_SAMPLE_ID)).add(image);
			}else{
				List<SolrDocument> tmpImageList=new ArrayList<>();
				tmpImageList.add(image);
				sampleToImagesMap.put((String)image.get(ObservationDTO.EXTERNAL_SAMPLE_ID), tmpImageList);
			}
		}
//		System.out.println("sampleToImagesMap size="+sampleToImagesMap.size());
		return sampleToImagesMap;
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
	

	public Map<String, List<ObservationDTO>> getAnatomyNamesToObservationsMap(List<ObservationDTO> observations) {
		
		Map<String, List<ObservationDTO>> anatomyToObservationsMap = new HashMap<>();
		for (ObservationDTO obs : observations) {
			if(obs.getObservationType().equals("ontological")){//only set anatomy if ontological as simple is bodyweight or text or image
			String anatomyString = obs.getParameterName().trim();
				if (anatomyString != null) {
					if(anatomyToObservationsMap.containsKey(anatomyString)){
						anatomyToObservationsMap.get(anatomyString).add(obs);
						
					}else{
						List<ObservationDTO> tempList=new ArrayList<>();
						tempList.add(obs);
						anatomyToObservationsMap.put(anatomyString, tempList);
					}
				}
			}
		}
		return anatomyToObservationsMap;
	}

	private String getAnatomyStringFromObservation(ObservationDTO obs) {
		String anatomyString = null;
		String paramName = obs.getParameterName();
		
		//for Gross path we don't have anatomy names with description etc so lets just trim.
			anatomyString = paramName.trim();
			// System.out.println("anatomyString=" + anatomyString);
		return anatomyString;
	}

	public List<ObservationDTO> getObservationsForGrossPathForGene(String acc) throws SolrServerException, IOException {
		List<ObservationDTO> observations = observationService.getObservationsByProcedureNameAndGene("Gross Pathology and Tissue Collection",
				acc, ObservationDTO.PARAMETER_NAME, ObservationDTO.PARAMETER_STABLE_ID, ObservationDTO.OBSERVATION_TYPE,
				ObservationDTO.CATEGORY, ObservationDTO.VALUE, ObservationDTO.DOWNLOAD_FILE_PATH,
				ObservationDTO.PARAMETER_ASSOCIATION_SEQUENCE_ID, ObservationDTO.SEQUENCE_ID);
		return observations;
	}

	public Map<String, List<ObservationDTO>> getObservations() {
		return this.extSampleIdToObservations;

	}
	
	public SolrDocumentList getGrossPathImagesForGene(String accession) throws SolrServerException, IOException{
		return imageService.getImagesForGrossPathForGene(accession);
			
	}

	public List<ObservationDTO> getAbnormalObservations(List<ObservationDTO> allObservations) {
		List<ObservationDTO> abnormalObservations=new ArrayList<>();
		for(ObservationDTO obs: allObservations){
			if(obs.getSubTermId()!=null && !obs.getSubTermId().isEmpty()){
				if(!obs.getSubTermId().get(0).equals("MP:0002169")){
					abnormalObservations.add(obs);
				}
			}
		}
		return abnormalObservations;
	}

}
