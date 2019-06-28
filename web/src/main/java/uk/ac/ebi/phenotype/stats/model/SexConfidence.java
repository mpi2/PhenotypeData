package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SexConfidence {
	//"confidence":{"sexfemale_genotypeexperimental_lower":-377.573331851511,"sexfemale_genotypeexperimental_upper":743.934119880777},"level":0.95}
	
	@JsonProperty("sexfemale_genotypeexperimental_lower")
	private Double sexFemaleGenotypeExperimentalLower;
	@JsonProperty("sexfemale_genotypeexperimental_upper")
	private Double sexFemaleGenotypeExperimentalUpper;
	
	private Double level;

	public Double getSexFemaleGenotypeExperimentalLower() {
		return sexFemaleGenotypeExperimentalLower;
	}

	public void setSexFemaleGenotypeExperimentalLower(Double sexFemaleGenotypeExperimentalLower) {
		this.sexFemaleGenotypeExperimentalLower = sexFemaleGenotypeExperimentalLower;
	}

	public Double getSexFemaleGenotypeExperimentalUpper() {
		return sexFemaleGenotypeExperimentalUpper;
	}

	public void setSexFemaleGenotypeExperimentalUpper(Double sexFemaleGenotypeExperimentalUpper) {
		this.sexFemaleGenotypeExperimentalUpper = sexFemaleGenotypeExperimentalUpper;
	}

	public Double getLevel() {
		return level;
	}

	public void setLevel(Double level) {
		this.level = level;
	}

}
