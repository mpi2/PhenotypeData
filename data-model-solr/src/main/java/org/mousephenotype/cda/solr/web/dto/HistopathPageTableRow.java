package org.mousephenotype.cda.solr.web.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	
	
	private Set<String> parameterNames;
	@Override
	public String toString() {
		return "HistopathPageTableRow [sampleId=" + sampleId + ", parameterNames=" + parameterNames
				+ ", parameterStableId=" + parameterStableId + ", observationType=" + observationType  + ", textValue=" + textValue + ", subOntologyBeans=" + subOntologyBeans + ", categoryList="
				+ categoryList + ", textParameters=" + textParameters + "]";
	}
	public HistopathPageTableRow(String sampleId, Set<String> parameterNames, String parameterStableId,
			String observationType, List<ParameterValueBean> category, String textValue, Map<String, List<OntologyBean>> subOntologyBeans) {
		super();
		this.sampleId = sampleId;
		this.parameterNames = parameterNames;
		this.parameterStableId = parameterStableId;
		this.observationType = observationType;
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
	
	public List<ParameterValueBean> getCategoryList() {
		return categoryList;
	}

	private String parameterStableId;
	private String observationType;
	private String textValue;
	private Map<String, List<OntologyBean>> subOntologyBeans=new HashMap<>();


	private List<ParameterValueBean> categoryList=new ArrayList<>();

	//reusing categoryBean here as 
	private List<ParameterValueBean> textParameters=new ArrayList<>();
	
	public List<ParameterValueBean> getTextParameters() {
		return textParameters;
	}
	public void setTextParameters(List<ParameterValueBean> textParameters) {
		this.textParameters = textParameters;
	}
	public void addCategoricalParam(ImpressBaseDTO parameter, String category) {
		this.categoryList.add(new ParameterValueBean(parameter, category));
		
	}
	public void addOntologicalParam(ImpressBaseDTO parameter,OntologyBean subOntologyBean) {
		if(!this.subOntologyBeans.containsKey(parameter)){
			this.subOntologyBeans.put(parameter.getName(), new ArrayList<OntologyBean>());
		}
		this.subOntologyBeans.get(parameter.getName()).add(subOntologyBean);
		
	}
	public void addTextParam(ImpressBaseDTO parameter, String textValue) {
		this.textParameters.add(new ParameterValueBean(parameter, textValue));
		
	}
	
	public  class ParameterValueBean{
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
	

}
