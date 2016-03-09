package org.mousephenotype.cda.solr.web.dto;

import java.util.List;

import org.mousephenotype.cda.solr.service.OntologyBean;

public class HistopathPageTableRow {

	private String sampleId;
	
	
	//System.out.println(sampleId+" "+ obs.getParameterName()+" "+obs.getParameterStableId()+" "+obs.getObservationType()+" categoryt=" +obs.getCategory()+ " text="+obs.getTextValue()+"ontologyTermValue="+obs.getTermValue());
	
	
	
	private String parameterName;
	public HistopathPageTableRow(String sampleId, String parameterName, String parameterStableId,
			String observationType, String category, String textValue, List<OntologyBean> subOntologyBeans) {
		super();
		this.sampleId = sampleId;
		this.parameterName = parameterName;
		this.parameterStableId = parameterStableId;
		this.observationType = observationType;
		this.category = category;
		this.textValue = textValue;
		this.subOntologyBeans = subOntologyBeans;
	}
	public String getSampleId() {
		return sampleId;
	}
	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
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
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getTextValue() {
		return textValue;
	}
	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}
	public List<OntologyBean> getSubOntologyBeans() {
		return subOntologyBeans;
	}
	public void setSubOntologyBeans(List<OntologyBean> subOntologyBeans) {
		this.subOntologyBeans = subOntologyBeans;
	}
	private String parameterStableId;
	private String observationType;
	private String category;
	private String textValue;
	private List<OntologyBean> subOntologyBeans;
	
	

}
