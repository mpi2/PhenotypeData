package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow.ParameterValueBean;
import org.mousephenotype.cda.solr.web.dto.HistopathSumPageTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class HistopathService {

	@Autowired
	ObservationService observationService;

	@Autowired
	ImageService imageService;

	Map<String, List<ObservationDTO>> extSampleIdToObservations;

	private String delimeter = " - ";

	private Map<String, SolrDocument> downloadToImgMap;

	public HistopathService(ObservationService observationService) {
		super();
		this.observationService = observationService;
	}

	public HistopathService() {

	}

	public List<HistopathPageTableRow> getTableData(List<ObservationDTO> allObservations) throws SolrServerException, IOException {
		List<HistopathPageTableRow> rows = new ArrayList<>();
		downloadToImgMap = new HashMap<String, SolrDocument>();
		System.out.println("observations for histopath size with normal and abnormal=" + allObservations.size());

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

			// just for images here as no anatomy currently

			for (String anatomyName : anatomyNames) {

				System.out.println("anatomyName=" + anatomyName);
	
				//System.out.println("sample id ="+sampleId);
				Set<String> parameterNames = new TreeSet<>();
				
				Map<Integer, List<ObservationDTO>> uniqueSequenceIdsToObservations=this.getSequenceIds(sampleToObservations.get(sampleId));
				for(Integer sequenceId: uniqueSequenceIdsToObservations.keySet()){
					HistopathPageTableRow row = new HistopathPageTableRow();// a row is a unique sampleId and anatomy and sequence id combination
					row.setAnatomyName(anatomyName);
					row.setSampleId(sampleId);
					//System.out.println("uniqueSequenceId="+sequenceId);
				for (ObservationDTO obs : uniqueSequenceIdsToObservations.get(sequenceId)) {
					
					String sequenceString="";
					if (obs.getSequenceId() != null) {
						//System.out.println("sequenceId in observation="+obs.getSequenceId());
						row.setSequenceId(obs.getSequenceId());
						sequenceString=Integer.toString(obs.getSequenceId());
					} else {
						//System.out.println("sequence_id is null");
					}

					if (this.getAnatomyStringFromObservation(obs) != null
							&& this.getAnatomyStringFromObservation(obs).equals(anatomyName)) {

						ImpressBaseDTO parameter = new ImpressBaseDTO(null, null, obs.getParameterStableId(),
								obs.getParameterName());
						parameterNames.add(obs.getParameterName());

						if (obs.getObservationType().equalsIgnoreCase("categorical")) {
							row.addCategoricalParam(parameter, obs.getCategory());
							if (parameter.getName().contains("Significance")) {
								row.addSignficiance(parameter, obs.getCategory());
							}
							if (parameter.getName().contains("Severity")) {
								row.addSeveirty(parameter, obs.getCategory());
							}
						}
						if (obs.getObservationType().equalsIgnoreCase("ontological")) {

							if (obs.getSubTermName() != null) {
								for (int i = 0; i < obs.getSubTermId().size(); i++) {
									//System.out.println("subtermId=" + obs.getSubTermId() + "subtermname="
										//	+ obs.getSubTermName().get(i));

									OntologyBean subOntologyBean = new OntologyBean(obs.getSubTermId().get(i),
											obs.getSubTermName().get(i), obs.getSubTermDescription().get(i));// ,
									// obs.getSubTermDescription().get(i));
									row.addOntologicalParam(parameter, subOntologyBean);
									if (parameter.getName().contains("MPATH process term")) {
										row.addMpathProcessParam(parameter, subOntologyBean);
									}
									if (parameter.getName().contains("MPATH diagnostic term")) {
										row.addMpathDiagnosticParam(parameter, subOntologyBean);
									}
									if (parameter.getName().contains("PATO")) {
										row.addPatoParam(parameter, subOntologyBean);
									}
								}
							}else{
								System.out.println("subterms are null for ontological data="+obs);
							}
						}
						if (obs.getObservationType().equalsIgnoreCase("text")) {
							row.addTextParam(parameter, obs.getTextValue());
							if (obs.getParameterName().contains("Free text")) {
								row.addFreeTextParam(parameter, obs.getTextValue());
							}
							if (obs.getParameterName().contains("Description")) {
								row.addDescriptionTextParam(parameter, obs.getTextValue());
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
							//if (image.get(ImageDTO.INCREMENT_VALUE) == row.getSequenceId()) {
								row.addImage(image);
							//} else {
							//	System.out.println("numbers don't match seqid" + row.getSequenceId() + " inc="
							//			+ image.get(ImageDTO.INCREMENT_VALUE));
							//}

							// if(obs.getParameterAssociationSequenceId()!=null){
							// System.out.println("parameterAssociationSequenceId="+obs.getParameterAssociationSequenceId());
							// }

						}
					}

				}
				if (parameterNames.size() != 0) {
					row.setParameterNames(parameterNames);
					//System.out.println("adding row=" + row);
					rows.add(row);
				}
			}//end of sequenceId loop

			
				
			}
		}
		return rows;

	}

	

	private Map<Integer, List<ObservationDTO>> getSequenceIds(List<ObservationDTO> list) {
		Map<Integer, List<ObservationDTO>>seqIdToObservations=new HashMap<>();
		for(ObservationDTO ob: list){
			if(seqIdToObservations.containsKey(ob.getSequenceId())){
				//if(ob.getSequenceId()==0)System.out.println("sequenceid == 0 need to change the way we handle nulls on sequenceId");
				seqIdToObservations.get(ob.getSequenceId()).add(ob);
			}else{
				//haven't seen a 0 sequenceId so assigning nulls to zero??
				List<ObservationDTO> obsForSeqId=new ArrayList<ObservationDTO>();
				obsForSeqId.add(ob);
				seqIdToObservations.put(ob.getSequenceId(), obsForSeqId);
			}
			
		}
		return seqIdToObservations;
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
			String anatomyString = getAnatomyStringFromObservation(obs);
			if (anatomyString != null) {
				anatomyNames.add(anatomyString);
			}
		}
		return anatomyNames;
	}

	private String getAnatomyStringFromObservation(ObservationDTO obs) {
		String anatomyString = null;
		String paramName = obs.getParameterName();
		if (paramName.contains(delimeter)) {
			anatomyString = paramName.substring(0, paramName.indexOf(delimeter));
			// System.out.println("anatomyString=" + anatomyString);
		} else {
			//System.out.println("no delimeter found with =" + paramName);
		}
		return anatomyString;
	}

	public List<ObservationDTO> getObservationsForHistopathForGene(String acc) throws SolrServerException, IOException {
		List<ObservationDTO> observations = observationService.getObservationsByProcedureNameAndGene("Histopathology",
				acc, ObservationDTO.PARAMETER_NAME, ObservationDTO.PARAMETER_STABLE_ID, ObservationDTO.OBSERVATION_TYPE,
				ObservationDTO.CATEGORY, ObservationDTO.VALUE, ObservationDTO.DOWNLOAD_FILE_PATH,
				ObservationDTO.PARAMETER_ASSOCIATION_SEQUENCE_ID, ObservationDTO.SEQUENCE_ID);
		return observations;
	}

	public List<ObservationDTO> screenOutObservationsThatAreNormal(List<ObservationDTO> observations) {

		List<ObservationDTO> filteredObservations = new ArrayList<>();
		for (ObservationDTO obs : observations) {
	
			boolean addObservation = true;
			if (obs.getObservationType().equalsIgnoreCase("categorical")) {
				if (obs.getCategory().equalsIgnoreCase("0")) {
					addObservation = false;
					// System.out.println("setting obs to false");

				}

			}
			if (obs.getObservationType().equalsIgnoreCase("ontological")) {
				if (obs.getSubTermName() != null) {
					for (String name : obs.getSubTermName()) {
						if (name.equalsIgnoreCase("normal"))
							addObservation = false;
						// System.out.println("setting obs to false");

					}
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

	public List<HistopathPageTableRow> collapseHistopathTableRows(List<HistopathPageTableRow> histopathRows) {
		List<HistopathPageTableRow> collapsedRows=new ArrayList<HistopathPageTableRow>();
		Map<String, HistopathSumPageTableRow> anatomyToRowMap=new HashMap<>();
		for(HistopathPageTableRow row: histopathRows){
			String anatomy=row.getAnatomyName();
			if(!anatomyToRowMap.containsKey(anatomy)){
				anatomyToRowMap.put(anatomy, new HistopathSumPageTableRow());
			}
			HistopathSumPageTableRow anatomyRow=anatomyToRowMap.get(anatomy);
			anatomyRow.setAnatomyName(anatomy);
			//anatomyRow.getSignificance().addAll(row.getSignificance());
			//anatomyRow.getSeverity().addAll(row.getSeverity());
			boolean significant=false;
			boolean images=false;
			for(ParameterValueBean sign:row.getSignificance()){
				String text=sign.getTextValue();
				//System.out.println("text="+text+"|");
				if(text.equals("Significant")){
					//System.out.println("significant!!!!!!!!!!!!");
					anatomyRow.setSignificantCount(anatomyRow.getSignificantCount()+1);
					significant=true;
					//if significant then set the text and parameters of the row that is significant to the row we are collapsing so we display the most appropriate info
					//anatomyRow.setDescriptionTextParameters(anatomyRow.getDescriptionTextParameters().addAll(row.get));
					//anatomyRow.setFreeTextParameters(row.get);
					
				}else{//assume non significant if not significant
					anatomyRow.setNonSignificantCount(anatomyRow.getNonSignificantCount()+1);
				}
				if(anatomyRow.getImageList().size()>0){
					images=true;
				}
			}
			if(significant){
				//if significant lets copy the main attributes so that we have a summary for that significant hit.
				anatomyRow.setSampleId(row.getSampleId());
				anatomyRow.setMpathProcessOntologyBeans(row.getMpathProcessOntologyBeans());
				anatomyRow.setMpathDiagnosticOntologyBeans(row.getMpathDiagnosticOntologyBeans());
				anatomyRow.setDescriptionTextParameters(row.getDescriptionTextParameters());
				anatomyRow.setFreeTextParameters(row.getFreeTextParameters());
				anatomyRow.setPatoOntologyBeans(row.getPatoOntologyBeans());
				if(!row.getImageList().isEmpty()){
					anatomyRow.setHasImages(true);
				}
				
				
			}
			
			
			
			//anatomyRow.setSignificanceCount(anatomyRow.getSignificanceCount()+ row.getSignificance().size());
			//anatomyRow.getSeverity().addAll(row.getSeverity());
		}
		for(String anatomy: anatomyToRowMap.keySet()){
			if(anatomyToRowMap.get(anatomy).getSignificantCount()>0){
			collapsedRows.add(anatomyToRowMap.get(anatomy));
			}
		}
		return collapsedRows;
	}

}
