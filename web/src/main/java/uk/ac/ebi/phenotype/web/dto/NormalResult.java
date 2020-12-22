package uk.ac.ebi.phenotype.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NormalResult {

	
	private String method;
	public String getDependentVariable() {
		return dependentVariable;
	}
	public void setDependentVariable(String dependentVariable) {
		this.dependentVariable = dependentVariable;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	@JsonProperty("dependent_variable")
	private String dependentVariable;
	//question??? cant find "Batch effect significant" in stats file so where should we get this from??
	//http://localhost:8090/phenotype-archive/charts?accession=MGI:1915747&parameter_stable_id=IMPC_HEM_038_001
	
}
