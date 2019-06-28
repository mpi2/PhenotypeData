package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SexFvkoEstimate {
	//"sex_fvko_estimate":{"value":183.180394014633,"confidence":{"sexfemale_genotypeexperimental_lower":-377.573331851511,"sexfemale_genotypeexperimental_upper":743.934119880777},"level":0.95}
private Double value;
@JsonProperty("confidence")
private SexConfidence sexConfidence;
public Double getValue() {
	return value;
}
public void setValue(Double value) {
	this.value = value;
}
public SexConfidence getSexConfidence() {
	return sexConfidence;
}
public void setSexConfidence(SexConfidence sexConfidence) {
	this.sexConfidence = sexConfidence;
}


}
