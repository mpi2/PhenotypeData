package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SexMukoConfidence {
	//"confidence":{"sexmale_genotypeexperimental_lower":-197.821141223371,"sexmale_genotypeexperimental_upper":995.09720211481},"level":0.95}
	private Double level;
	
	@JsonProperty("sexmale_genotypeexperimental_lower")
	private Double sexMaleGenotypeExperimentalLower;
	
	@JsonProperty("sexmale_genotypeexperimental_upper")
	private Double sexMaleGenotypeExperimentalUpper;

	public Double getLevel() {
		return level;
	}

	public void setLevel(Double level) {
		this.level = level;
	}

	public Double getSexMaleGenotypeExperimentalLower() {
		return sexMaleGenotypeExperimentalLower;
	}

	public void setSexMaleGenotypeExperimentalLower(Double sexMaleGenotypeExperimentalLower) {
		this.sexMaleGenotypeExperimentalLower = sexMaleGenotypeExperimentalLower;
	}

	public Double getSexMaleGenotypeExperimentalUpper() {
		return sexMaleGenotypeExperimentalUpper;
	}

	public void setSexMaleGenotypeExperimentalUpper(Double sexMaleGenotypeExperimentalUpper) {
		this.sexMaleGenotypeExperimentalUpper = sexMaleGenotypeExperimentalUpper;
	}
	
	
}
