package org.mousephenotype.cda.solr.web.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.OntologyBean;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;

/**
 * A table row which is one per samepleId which should have all ontology info and text info and images info for the row
 * @author jwarren
 *
 */
public class GrossPathPageTableRow {

	private String sampleId;
	private String anatomyName;
	private String center;//phenotyping center
	
	
	//System.out.println(sampleId+" "+ obs.getParameterName()+" "+obs.getParameterStableId()+" "+obs.getObservationType()+" categoryt=" +obs.getCategory()+ " text="+obs.getTextValue()+"ontologyTermValue="+obs.getTermValue());
	
	
	
	public String getCenter() {
		return center;
	}
	public void setCenter(String center) {
		this.center = center;
	}
	public String getAnatomyName() {
		return anatomyName;
	}
	public void setAnatomyName(String anatomyName) {
		this.anatomyName = anatomyName;
	}

	private Set<String> parameterNames;
	
	@Override
	public String toString() {
		return "GrossPathPageTableRow [sampleId=" + sampleId + ", anatomyName=" + anatomyName + ", parameterNames="
				+ parameterNames + ", parameterStableId=" + parameterStableId + ", observationType=" + observationType
				+ ", textValue=" + textValue + ", subOntologyBeans=" + subOntologyBeans + ", patoOntologyBeans="
				+ patoOntologyBeans + ", descriptionTextParameters=" + descriptionTextParameters
				+ ", freeTextParameters=" + freeTextParameters + ", textParameters=" + textParameters
				+ ", significance=" + significance + ", severity=" + severity + ", imageList=" + imageList
				+ ", sequenceId=" + sequenceId + ", zygosity=" + zygosity + ", numberOfAbnormalObservations="
				+ numberOfAbnormalObservations + ", numberOfNormalObservations=" + numberOfNormalObservations
				+ ", mpId=" + mpId +",  center="+ center+"]";
	}
	
	public GrossPathPageTableRow(String sampleId, Set<String> parameterNames, String parameterStableId,
			String observationType, List<ParameterValueBean> category, String textValue, Map<String, List<OntologyBean>> subOntologyBeans) {
		super();
		this.sampleId = sampleId;
		this.parameterNames = parameterNames;
		this.parameterStableId = parameterStableId;
		this.observationType = observationType;
		this.textValue = textValue;
		this.subOntologyBeans = subOntologyBeans;
	}
	public GrossPathPageTableRow() {
		// TODO Auto-generated constructor stub
	}
	public String getSampleId() {
		return sampleId;
	}
	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}
	public Set<String> getParameterNames() {
		return parameterNames;
	}
	
	public String getParameterStableId() {
		return parameterStableId;
	}
	public void setParameterStableId(String parameterStableId) {
		this.parameterStableId = parameterStableId;
	}
	public String getObservationType() {
		return observationType;
	}
	public void setObservationType(String observationType) {
		this.observationType = observationType;
	}
	
	
	public String getTextValue() {
		return textValue;
	}
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	public Map<String, List<OntologyBean>> getSubOntologyBeans() {
		return subOntologyBeans;
	}

	private String parameterStableId;
	private String observationType;
	private String textValue;
	private Map<String, List<OntologyBean>> subOntologyBeans=new HashMap<>();
	public void addOntologicalParam(ImpressBaseDTO parameter,OntologyBean subOntologyBean) {
		if(!this.subOntologyBeans.containsKey(parameter.getName())){
			this.subOntologyBeans.put(parameter.getName(), new ArrayList<OntologyBean>());
		}
		this.subOntologyBeans.get(parameter.getName()).add(subOntologyBean);
		
	}
	
	
	public Map<String, List<OntologyBean>> getPatoOntologyBeans() {
		return patoOntologyBeans;
	}
	public void setPatoOntologyBeans(Map<String, List<OntologyBean>> patoOntologyBeans) {
		this.patoOntologyBeans = patoOntologyBeans;
	}

	private Map<String, List<OntologyBean>> patoOntologyBeans=new HashMap<>();
	
	public void addPatoParam(ImpressBaseDTO parameter,OntologyBean subOntologyBean) {
		if(!this.patoOntologyBeans.containsKey(parameter.getName())){
			this.patoOntologyBeans.put(parameter.getName(), new ArrayList<OntologyBean>());
		}
		this.patoOntologyBeans.get(parameter.getName()).add(subOntologyBean);
		
	}

	private List<ParameterValueBean> descriptionTextParameters=new ArrayList<>();
	
	public List<ParameterValueBean> getDescriptionTextParameters() {
		return descriptionTextParameters;
	}
	public void setDescriptionTextParameters(List<ParameterValueBean> descriptionTextParameters) {
		this.descriptionTextParameters = descriptionTextParameters;
	}
	
	public void addDescriptionTextParam(ImpressBaseDTO parameter, String textValue) {
		this.descriptionTextParameters.add(new ParameterValueBean(parameter, textValue));
		
	}
	
	private List<ParameterValueBean> freeTextParameters=new ArrayList<>();
	
	public List<ParameterValueBean> getFreeTextParameters() {
		return freeTextParameters;
	}
	public void setFreeTextParameters(List<ParameterValueBean> freeTextParameters) {
		this.freeTextParameters = freeTextParameters;
	}
	
	public void addFreeTextParam(ImpressBaseDTO parameter, String textValue) {
		this.freeTextParameters.add(new ParameterValueBean(parameter, textValue));
		
	}
	
	
	
	private List<ParameterValueBean> textParameters=new ArrayList<>();
	
	public List<ParameterValueBean> getTextParameters() {
		return textParameters;
	}
	public void setTextParameters(List<ParameterValueBean> textParameters) {
		this.textParameters = textParameters;
	}
	
	
	private List<ParameterValueBean> significance=new ArrayList<>();
	
	public List<ParameterValueBean> getSignificance() {
		return significance;
	}
	
	public void addSignficiance(ImpressBaseDTO parameter, String category) {
		this.significance.add(new ParameterValueBean(parameter, category));
		
	}
	
	private List<ParameterValueBean> severity=new ArrayList<>();
	
	public List<ParameterValueBean> getSeverity() {
		return severity;
	}
	
	public void addSeveirty(ImpressBaseDTO parameter, String category) {
		this.severity.add(new ParameterValueBean(parameter, category));
		
	}
	
	
	
	public void addTextParam(ImpressBaseDTO parameter, String textValue) {
		this.textParameters.add(new ParameterValueBean(parameter, textValue));
		
	}
	
	public  class ParameterValueBean{
		@Override
		public String toString() {
			return "ParameterValueBean [parameter=" + parameter + ", textValue=" + textValue + "]";
		}

		ImpressBaseDTO parameter;
		public ImpressBaseDTO getParameter() {
			return parameter;
		}

		public void setParameter(ImpressBaseDTO parameter) {
			this.parameter = parameter;
		}

		public String getTextValue() {
			return textValue;
		}

		public void setTextValue(String textValue) {
			this.textValue = textValue;
		}

		String textValue;
		
		public ParameterValueBean(ImpressBaseDTO parameter, String textValue){
			this.parameter=parameter;
			this.textValue=textValue;
		}
		
		
	}

	public void setParameterNames(Set<String> parameterNames) {
		this.parameterNames=parameterNames;
		
	}
	
	private List<SolrDocument> imageList=new ArrayList<>();
	private Integer sequenceId;
	private String zygosity;
	private int numberOfAbnormalObservations=0;
	
	public int getNumberOfAbnormalObservations() {
		return numberOfAbnormalObservations;
	}
	public int getNumberOfNormalObservations() {
		return numberOfNormalObservations;
	}

	private int numberOfNormalObservations=0;
	private String mpId;
	
	public String getMpId() {
		return mpId;
	}
	public Integer getSequenceId() {
		return sequenceId;
	}
	public List<SolrDocument> getImageList() {
		return imageList;
	}
	
	public void addImage(SolrDocument obs) {
			this.imageList.add(obs);	
	}
	public void setSequenceId(Integer sequenceId) {
		this.sequenceId=sequenceId;
	}
	public void addImages(List<SolrDocument> list) {
		this.imageList=list;
	}
	public void setZygosity(String zygosity) {
		this.zygosity=zygosity;
		
	}
	public String getZygosity() {
		return zygosity;
	}
	public void setNumberOfAbnormalObservations(int numberOfAbnormalObservations) {
		this.numberOfAbnormalObservations=numberOfAbnormalObservations;
	}
	
	
public void setNumberOfNormalObservations(int normalObservations) {
		
		this.numberOfNormalObservations=normalObservations;
	}

/**
 * used for gross path table
 * @param string
 */
public void setMpId(String mpId) {
	this.mpId=mpId;
	
}
	
	
	

}
