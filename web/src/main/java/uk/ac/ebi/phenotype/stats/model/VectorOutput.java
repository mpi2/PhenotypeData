package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VectorOutput {

	
	@JsonProperty("normal_result")
	private NormalResult normalResult;

	public NormalResult getNormalResult() {
		return normalResult;
	}

	public void setNormalResult(NormalResult normalResult) {
		this.normalResult = normalResult;
	}
	
}
