package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

//@JsonSerialize(typing = SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
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
