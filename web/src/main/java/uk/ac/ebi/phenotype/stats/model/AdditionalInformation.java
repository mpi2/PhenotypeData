package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdditionalInformation {
	
	@JsonProperty("gender_included_in_analysis")
	private String genderIncludedInAnalysis;//: "Both sexes included",
	@JsonProperty("multibatch_in_analysis")
	private String multiBatchInAnalysis;//: "Data contains multi batches",
	
	
	@JsonProperty("summary_statistics")
	private SummaryStatistics summaryStatistics;//: {


}
