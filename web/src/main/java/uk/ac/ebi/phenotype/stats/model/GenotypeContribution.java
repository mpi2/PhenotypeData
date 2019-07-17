package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenotypeContribution {

//	"genotype_contribution": {
//    "overal": 1.24889109502114e-12,
//    "sex_fvko_p_val": 3.66832947622071e-13,
//    "sexual_dimorphism_detected": "Sex specific results are always reported"
//  },
	@JsonProperty("overal")
	private Double overal;
	public Double getOveral() {
		return overal;
	}
	public void setOveral(Double overal) {
		this.overal = overal;
	}
	public Double getSexFvkoPVal() {
		return sexFvkoPVal;
	}
	public void setSexFvkoPVal(Double sexFvkoPVal) {
		this.sexFvkoPVal = sexFvkoPVal;
	}
	public String getSexualDimorphismDetected() {
		return sexualDimorphismDetected;
	}
	public void setSexualDimorphismDetected(String sexualDimorphismDetected) {
		this.sexualDimorphismDetected = sexualDimorphismDetected;
	}
	@JsonProperty("sex_fvko_p_val")
	private Double sexFvkoPVal;
	@JsonProperty("sexual_dimorphism_detected")
	private String sexualDimorphismDetected;
	
}
