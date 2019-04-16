package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow.ParameterValueBean;
import org.mousephenotype.cda.solr.web.dto.HistopathSumPageTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

@Service
public class HistopathService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private       ObservationService observationService;
	private       String             delimeter      = " - ";
	public static String             histoDelimeter = "||";


	@Inject
	public HistopathService(ObservationService observationService) {
		this.observationService = observationService;
	}

	public HistopathService() {

	}

    public List<HistopathPageTableRow> getTableData(Map<String, List<ObservationDTO>> uniqueSampleSequeneAndAnatomyName, Map<String, String> sampleIds) throws SolrServerException, IOException {
        List<HistopathPageTableRow> rows = new ArrayList<>();

        for (String key : uniqueSampleSequeneAndAnatomyName.keySet()) {

            // just for images here as no anatomy currently

            List<ObservationDTO> observations   = uniqueSampleSequeneAndAnatomyName.get(key);
            Set<String>          parameterNames = new TreeSet<>();

            HistopathPageTableRow row = new HistopathPageTableRow();// a row is a unique sampleId and anatomy and sequence id combination
            row.setAnatomyName(this.getAnatomyStringFromObservation(observations.get(0)));//anatomy should be the same from any in this dataset
            row.setSampleId(sampleIds.get(observations.get(0).getExternalSampleId()));
            row.setSex(SexType.valueOf(observations.get(0).getSex()));

            for (ObservationDTO obs : observations) {

                String zyg = ZygosityType.valueOf(obs.getZygosity()).getShortName();
                row.setZygosity(zyg);
                if (obs.getAgeInWeeks() != null) {
                    row.setAgeInWeeks(obs.getAgeInWeeks());
                }

                //System.out.println("sequenceId in observation="+obs.getSequenceId());
                row.setSequenceId(obs.getSequenceId());


                ImpressBaseDTO parameter = new ImpressBaseDTO(null, null, obs.getParameterStableId(),
                                                              obs.getParameterName());

                parameterNames.add(obs.getParameterName());

                if (obs.getObservationType().equalsIgnoreCase("categorical")) {
                    row.addCategoricalParam(parameter, obs.getCategory());
                    if (parameter.getName().contains("Significance")) {
//								System.out.println(parameter+" "+ obs.getCategory());
                        row.addSignficiance(parameter, obs.getCategory());
                        row.setSignficant();

                    }
                    if (parameter.getName().contains("Severity")) {
                        row.addSeverity(parameter, obs.getCategory());
                    }
                }
                if (obs.getObservationType().equalsIgnoreCase("ontological")) {

                    if (obs.getSubTermName() != null) {
                        for (int i = 0; i < obs.getSubTermId().size(); i++) {
                            //System.out.println("subtermId=" + obs.getSubTermId() + "subtermname="
                            //+ obs.getSubTermName().get(i));

                            OntologyBean subOntologyBean = new OntologyBean(obs.getSubTermId().get(i),
                                                                            obs.getSubTermName().get(i), obs.getSubTermDescription().get(i));// ,
                            //obs.getSubTermDescription().get(i));
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
                    } else {
                        logger.info("subterms are null for ontological data=" + obs);
                    }
                }
                if (obs.getObservationType().equalsIgnoreCase("text")) {
                    row.addTextParam(parameter, obs.getTextValue());
                    if (obs.getParameterName().contains("Free text")) {
                        row.addFreeTextParam(parameter, obs.getTextValue());
                    }
                    if (obs.getParameterName().contains("Description")) {
                        //System.out.println("setting description="+obs.getTextValue());
                        row.addDescriptionTextParam(parameter, obs.getTextValue());
                    }
                }

            }

            rows.add(row);
            row.setParameterNames(parameterNames);
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

	//get observations that have the same anatomy name, sampleid, sequence_id
	public Map<String, List<ObservationDTO>> getUniqueInfo(List<ObservationDTO> allObservations) {
		Map<String, List<ObservationDTO>> uniqueDataSets=new HashMap<>();
		
		for(ObservationDTO obs: allObservations){
			String key=this.getAnatomyStringFromObservation(obs)+histoDelimeter+obs.getExternalSampleId()+histoDelimeter+obs.getSequenceId();
			if(uniqueDataSets.containsKey(key)){
				uniqueDataSets.get(key).add(obs);
			}else{
				ArrayList<ObservationDTO> newList = new ArrayList<ObservationDTO>();
				newList.add(obs);
				uniqueDataSets.put(key, newList );
			}
		}
		logger.info("unique datasets for histopath size = "+uniqueDataSets.size());
		return uniqueDataSets;
	}
}