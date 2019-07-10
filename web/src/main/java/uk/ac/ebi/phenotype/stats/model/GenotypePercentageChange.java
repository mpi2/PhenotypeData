package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenotypePercentageChange {
	//old "genotype_percentage_change":{"control_genotype":-0.130333287397218,"experimental_genotype":1.40759950388997}
	//"genotype_percentage_change":{"sexfemale_genotypeexperimental":1.30037066864167,"sexmale_genotypeexperimental":5.07831051934796}
	
//	private Double control_genotype;
//	private Double experimental_genotype;
	@JsonProperty("sexfemale_genotypeexperimental")
	private Double sexFemaleGenotypeExperimental;
	
	@JsonProperty("sexmale_genotypeexperimental")
	private Double sexGenotypeExperimental;

	public Double getSexFemaleGenotypeExperimental() {
		return sexFemaleGenotypeExperimental;
	}

	public void setSexFemaleGenotypeExperimental(Double sexFemaleGenotypeExperimental) {
		this.sexFemaleGenotypeExperimental = sexFemaleGenotypeExperimental;
	}

	public Double getSexGenotypeExperimental() {
		return sexGenotypeExperimental;
	}

	public void setSexGenotypeExperimental(Double sexGenotypeExperimental) {
		this.sexGenotypeExperimental = sexGenotypeExperimental;
	}
}
