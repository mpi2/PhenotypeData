package org.mousephenotype.cda.indexers.utils;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//import lombok.Data;

//@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class  EmbryoStrain{
	@Override
	public String toString() {
		return "EmbryoStrain [centre=" + centre + ", mgi=" + mgi + ", url=" + url + ", colonyId=" + colonyId
				+ ", procedureStableKeys=" + procedureStableKeys + ", parameterStableKeys=" + parameterStableKeys
				+ ", modalities=" + modalities + "]";
	}
	
	String analysisViewUrl;
	public String getAnalysisViewUrl() {
		return analysisViewUrl;
	}
	public void setAnalysisViewUrl(String analysisViewUrl) {
		this.analysisViewUrl = analysisViewUrl;
	}

	String centre;
	public String getCentre() {
		return centre;
	}
	public void setCentre(String centre) {
		this.centre = centre;
	}
	public String getMgi() {
		return mgi;
	}
	public void setMgi(String mgi) {
		this.mgi = mgi;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setProcedureStableKeys(List<String> procedureStableKeys) {
		this.procedureStableKeys = procedureStableKeys;
	}
	public List<String> getProcedureStableKeys() {
		return procedureStableKeys;
	}
	public void setParameterStableKeys(List<String> parameterStableKeys) {
		this.parameterStableKeys = parameterStableKeys;
	}
	public List<String> getParameterStableKeys() {
		return parameterStableKeys;
	}
	String mgi;
	String url;
	String colonyId;
	List<String> procedureStableKeys;
	List<String> parameterStableKeys;
	List<String> modalities;
	
	
	public String getColonyId() {
		return colonyId;
	}
	public void setColonyId(String colonyId) {
		this.colonyId = colonyId;
	}
	public void setModalities(List<String> modalities) {
		this.modalities=modalities;
		
	}
	
	public List<String> getModalities() {
		return modalities;
	}
	
	
}