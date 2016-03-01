package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnatomogramDataBean {

	String parameterId;
	String patameterName;
	List<String> uberonIds;
	
	long count;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getParameterId() {
		return parameterId;
	}
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	public String getPatameterName() {
		return patameterName;
	}
	public void setPatameterName(String patameterName) {
		this.patameterName = patameterName;
	}
	public List<String> getUberonIds() {
		return uberonIds;
	}
	public void setUberonIds(List<String> uberonIds) {
		this.uberonIds = uberonIds;
	}
	
	public String getMaId() {
		return maId;
	}
	public void setMaId(String maId) {
		this.maId = maId;
	}
	
	public List<String> getTopLevelMaIds() {
		return topLevelMaIds;
	}
	public void setTopLevelMaIds(List<String> topLevelMaIds) {
		this.topLevelMaIds = topLevelMaIds;
	}
	String maId;
	List<String> topLevelMaIds=new ArrayList<>();
	List<String> topLevelMaNames=new ArrayList<>();
	
	private String maTerm;
	
	public String getMaTerm() {
		return maTerm;
	}
	public void setMaTerm(String maTerm) {
		this.maTerm = maTerm;
	}
	public List<String> getTopLevelMaNames() {
		return topLevelMaNames;
	}
	public void setTopLevelMaNames(List<String> topLevelMaNames) {
		this.topLevelMaNames = topLevelMaNames;
	}
	
	@Override
	public String toString() {
		return "AnatomogramDataBean [parameterId=" + parameterId + ", patameterName=" + patameterName + ", uberonIds="
				+ uberonIds + ", maId=" + maId + ", topLevelMaIds=" + topLevelMaIds + ", topLevelMaNames="
				+ topLevelMaNames + ", count=" + count + ", maTerm=" + maTerm + "]";
	}
	public void addTopLevelMaIds(List<String> selectedTopLevelMas) {
		this.topLevelMaIds.addAll(selectedTopLevelMas);
		
	}
	public void addTopLevelMaNames(List<String> selectedTopLevelMaTerms) {
		this.topLevelMaNames.addAll(selectedTopLevelMaTerms);
		
	}
	
	
}
