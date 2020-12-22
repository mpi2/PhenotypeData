package org.mousephenotype.cda.solr.web.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.common.SolrDocument;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.OntologyBean;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.mousephenotype.cda.solr.service.dto.ImpressBaseDTO;

/**
 * A table row which is one per samepleId which should have all ontology info and text info and images info for the row
 * @author jwarren
 *
 */
public class HistopathPageTableRow{

	private String sampleId;
	private String anatomyName;
	private String anatomyId;
	public String getAnatomyId() {
		return anatomyId;
	}
	public void setAnatomyId(String anatomyId) {
		this.anatomyId = anatomyId;
	}

	private String zygosity;
	private long ageInDays;
	private long ageInWeeks;
	private SexType sex;
	
	
	
	public SexType getSex() {
		return sex;
	}
	public void setSex(SexType sex) {
		this.sex = sex;
	}
	public long getAgeInWeeks() {
		return ageInWeeks;
	}
	public void setAgeInWeeks(long ageInWeeks) {
		this.ageInWeeks = ageInWeeks;
	}
	public String getZygosity() {
		return zygosity;
	}
	public void setZygosity(String zygosity) {
		this.zygosity = zygosity;
	}
	public long getAgeInDays() {
		return ageInDays;
	}
	public void setAgeInDays(long ageInDays) {
		this.ageInDays = ageInDays;
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
		return "HistopathPageTableRow [sampleId=" + sampleId + ", anatomyName=" + anatomyName + ", parameterNames="
				+ parameterNames + ", parameterStableId=" + parameterStableId + ", observationType=" + observationType
				+ ", textValue=" + textValue + ", subOntologyBeans=" + subOntologyBeans + ", mpathProcessOntologyBeans="
				+ mpathProcessOntologyBeans + ", mpathDiagnosticOntologyBeans=" + mpathDiagnosticOntologyBeans
				+ ", patoOntologyBeans=" + patoOntologyBeans + ", descriptionTextParameters="
				+ descriptionTextParameters + ", freeTextParameters=" + freeTextParameters + ", textParameters="
				+ textParameters + ", significance=" + significance + ", severity=" + severity + ", categoryList="
				+ categoryList + ", imageList=" + imageList + "]";
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
	
	
	private Map<String, List<OntologyBean>> mpathProcessOntologyBeans=new HashMap<>();
	
	public void addMpathProcessParam(ImpressBaseDTO parameter,OntologyBean subOntologyBean) {
		if(!this.mpathProcessOntologyBeans.containsKey(parameter.getName())){
			this.mpathProcessOntologyBeans.put(parameter.getName(), new ArrayList<OntologyBean>());
		}
		this.mpathProcessOntologyBeans.get(parameter.getName()).add(subOntologyBean);
		
	}
	
private Map<String, List<OntologyBean>> mpathDiagnosticOntologyBeans=new HashMap<>();
	
	public Map<String, List<OntologyBean>> getMpathDiagnosticOntologyBeans() {
	return mpathDiagnosticOntologyBeans;
}
public void setMpathDiagnosticOntologyBeans(Map<String, List<OntologyBean>> mpathDiagnosticOntologyBeans) {
	this.mpathDiagnosticOntologyBeans = mpathDiagnosticOntologyBeans;
}
	public void addMpathDiagnosticParam(ImpressBaseDTO parameter,OntologyBean subOntologyBean) {
		if(!this.mpathDiagnosticOntologyBeans.containsKey(parameter.getName())){
			this.mpathDiagnosticOntologyBeans.put(parameter.getName(), new ArrayList<OntologyBean>());
		}
		this.mpathDiagnosticOntologyBeans.get(parameter.getName()).add(subOntologyBean);
		
	}
	
	public Map<String, List<OntologyBean>> getMpathProcessOntologyBeans() {
		return mpathProcessOntologyBeans;
	}
	public void setMpathProcessOntologyBeans(Map<String, List<OntologyBean>> mpathOntologyBeans) {
		this.mpathProcessOntologyBeans = mpathOntologyBeans;
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
	
	public void addSignficance(ImpressBaseDTO parameter, String category) {
		this.significance.add(new ParameterValueBean(parameter, category));
		
	}
	
	private List<ParameterValueBean> severity=new ArrayList<>();
	
	public List<ParameterValueBean> getSeverity() {
		return severity;
	}
	
	public void addSeverity(ImpressBaseDTO parameter, String category) {
		this.severity.add(new ParameterValueBean(parameter, category));
		
	}
	
	private List<ParameterValueBean> categoryList=new ArrayList<>();
	
	public List<ParameterValueBean> getCategoryList() {
		return categoryList;
	}
	
	public void addCategoricalParam(ImpressBaseDTO parameter, String category) {
		this.categoryList.add(new ParameterValueBean(parameter, category));
		
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
			this.textValue=textValue.trim();
		}
		
		
	}

	public void setParameterNames(Set<String> parameterNames) {
		this.parameterNames=parameterNames;
		
	}
	
	private List<SolrDocument> imageList=new ArrayList<>();
	private Integer sequenceId;

	public Integer getSequenceId() {
		return sequenceId;
	}
	public List<SolrDocument> getImageList() {
		return imageList;
	}
	
	public void addImage(SolrDocument image) {
			this.imageList.add(image);
			
		
		
	}
	public void setSequenceId(Integer sequenceId) {
		this.sequenceId=sequenceId;
	}

	
	
	
	

}
