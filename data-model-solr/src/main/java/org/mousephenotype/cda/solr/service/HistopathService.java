package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.hibernate.sql.ordering.antlr.CollationSpecification;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	public List<HistopathPageTableRow> getTableData(List<ObservationDTO> allObservations) throws SolrServerException {
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
				HistopathPageTableRow row = new HistopathPageTableRow();
				row.setAnatomyName(anatomyName);
				row.setSampleId(sampleId);
				Set<String> parameterNames = new TreeSet<>();

				for (ObservationDTO obs : sampleToObservations.get(sampleId)) {
					// a row is a unique sampleId and anatomy combination
					if (obs.getSequenceId() != null) {
						// System.out.println("sequenceId="+obs.getSequenceId());
						row.setSequenceId(obs.getSequenceId());
					} else {
						System.out.println("sequence_id is null");
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
									System.out.println("subtermId=" + obs.getSubTermId() + "subtermname="
											+ obs.getSubTermName().get(i));

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

							System.out.println("image omero id=" + image.get(ImageDTO.OMERO_ID));
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
			System.out.println("no delimeter found with =" + paramName);
		}
		return anatomyString;
	}

	public List<ObservationDTO> getObservationsForHistopathForGene(String acc) throws SolrServerException {
		List<ObservationDTO> observations = observationService.getObservationsByProcedureNameAndGene("Histopathology",
				acc, ObservationDTO.PARAMETER_NAME, ObservationDTO.PARAMETER_STABLE_ID, ObservationDTO.OBSERVATION_TYPE,
				ObservationDTO.CATEGORY, ObservationDTO.VALUE, ObservationDTO.DOWNLOAD_FILE_PATH,
				ObservationDTO.PARAMETER_ASSOCIATION_SEQUENCE_ID, ObservationDTO.SEQUENCE_ID);
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
		Map<String, HistopathPageTableRow> anatomyToRowMap=new HashMap<>();
		for(HistopathPageTableRow row: histopathRows){
			String anatomy=row.getAnatomyName();
			if(!anatomyToRowMap.containsKey(anatomy)){
				anatomyToRowMap.put(anatomy, row);
			}
			HistopathPageTableRow anatomyRow=anatomyToRowMap.get(anatomy);
			anatomyRow.getSignificance().addAll(row.getSignificance());
			anatomyRow.getSeverity().addAll(row.getSeverity());
		}
		for(String anatomy: anatomyToRowMap.keySet()){
			collapsedRows.add(anatomyToRowMap.get(anatomy));
		}
		return collapsedRows;
	}

}
