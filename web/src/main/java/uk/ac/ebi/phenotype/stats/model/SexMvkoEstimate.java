package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SexMvkoEstimate {
	//"sex_mvko_estimate":{"value":398.638030445719,"confidence":{"sexmale_genotypeexperimental_lower":-197.821141223371,"sexmale_genotypeexperimental_upper":995.09720211481},"level":0.95}
private Double value;
@JsonProperty("confidence")
private SexMukoConfidence sexMukoConfidence;
	
}
