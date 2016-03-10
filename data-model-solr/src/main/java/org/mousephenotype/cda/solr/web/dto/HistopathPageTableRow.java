package org.mousephenotype.cda.solr.web.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mousephenotype.cda.solr.service.OntologyBean;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;

/**
 * A table row which is one per samepleId which should have all ontology info and text info and images info for the row
 * @author jwarren
 *
 */
public class HistopathPageTableRow {

	private String sampleId;
	
	
	//System.out.println(sampleId+" "+ obs.getParameterName()+" "+obs.getParameterStableId()+" "+obs.getObservationType()+" categoryt=" +obs.getCategory()+ " text="+obs.getTextValue()+"ontologyTermValue="+obs.getTermValue());
	
	
	
	private String parameterName;
	@Override
	public String toString() {
		return "HistopathPageTableRow [sampleId=" + sampleId + ", parameterName=" + parameterName
				+ ", parameterStableId=" + parameterStableId + ", observationType=" + observationType + ", category="
				+ category + ", textValue=" + textValue + ", subOntologyBeans=" + subOntologyBeans + ", categoryList="
				+ categoryList + ", textParameters=" + textParameters + "]";
	}
	public HistopathPageTableRow(String sampleId, String parameterName, String parameterStableId,
			String observationType, String category, String textValue, Map<String, List<OntologyBean>> subOntologyBeans) {
		super();
		this.sampleId = sampleId;
		this.parameterName = parameterName;
		this.parameterStableId = parameterStableId;
		this.observationType = observationType;
		this.category = category;
		this.textValue = textValue;
		this.subOntologyBeans = subOntologyBeans;
	}
	public HistopathPageTableRow() {
		// TODO Auto-generated constructor stub
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
	public Map<String, List<OntologyBean>> getSubOntologyBeans() {
		return subOntologyBeans;
	}
	
	private String parameterStableId;
	private String observationType;
	private String category;
	private String textValue;
	private Map<String, List<OntologyBean>> subOntologyBeans=new HashMap<>();


	private List<CategoryBean> categoryList=new ArrayList<>();

	//reusing categoryBean here as 
	private List<CategoryBean> textParameters=new ArrayList<>();
	
	public List<CategoryBean> getTextParameters() {
		return textParameters;
	}
	public void setTextParameters(List<CategoryBean> textParameters) {
		this.textParameters = textParameters;
	}
	public void addCategoricalParam(ImpressBaseDTO parameter, String category) {
		this.categoryList.add(new CategoryBean(parameter, category));
		
	}
	public void addOntologicalParam(ImpressBaseDTO parameter,OntologyBean subOntologyBean) {
		if(!this.subOntologyBeans.containsKey(parameter)){
			this.subOntologyBeans.put(parameter.getName(), new ArrayList<OntologyBean>());
		}
		this.subOntologyBeans.get(parameter.getName()).add(subOntologyBean);
		
	}
	public void addTextParam(ImpressBaseDTO parameter, String textValue) {
		this.textParameters.add(new CategoryBean(parameter, textValue));
		
	}
	
	public  class CategoryBean{
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
		
		public CategoryBean(ImpressBaseDTO parameter, String textValue){
			this.parameter=parameter;
			this.textValue=textValue;
		}
		
		
	}
	

}
