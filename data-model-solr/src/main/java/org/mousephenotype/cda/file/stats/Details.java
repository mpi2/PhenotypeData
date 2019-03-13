package org.mousephenotype.cda.file.stats;

import java.util.List;



import com.fasterxml.jackson.annotation.JsonProperty;

public class Details {

	@JsonProperty("response_type")
	private String responseType;
	
	@JsonProperty("raw_data_summary_statistics")
	private RawSummaryStatistics rawDataSummaryStatistics;
	
	@JsonProperty("original_sex")
	private List<String> originalSex;
	
	@JsonProperty("original_biological_sample_group")
	private List<String> originalBiologicalSampleGroup;
	
	@JsonProperty("original_response")
	private List<String> originalResponse;//values
	
	@JsonProperty("original_date_of_experiment")
	private List<String> originalDateOfExperiment;
	
	@JsonProperty("original_body_weight")
	private List<Float> originalBodyWeight;
	
	
	public String getResponseType() {
		return responseType;
	}
	public void setResponseType(String responseType) {
		this.responseType = responseType;
	}
	public RawSummaryStatistics getRawDataSummaryStatistics() {
		return rawDataSummaryStatistics;
	}
	public void setRawDataSummaryStatistics(RawSummaryStatistics rawDataSummaryStatistics) {
		this.rawDataSummaryStatistics = rawDataSummaryStatistics;
	}
	public List<String> getOriginalSex() {
		return originalSex;
	}
	public void setOriginalSex(List<String> originalSex) {
		this.originalSex = originalSex;
	}
	public List<String> getOriginalBiologicalSampleGroup() {
		return originalBiologicalSampleGroup;
	}
	public void setOriginalBiologicalSampleGroup(List<String> originalBiologicalSampleGroup) {
		this.originalBiologicalSampleGroup = originalBiologicalSampleGroup;
	}
	public List<String> getOriginalResponse() {
		return originalResponse;
	}
	public List<String> getOriginalDateOfExperiment() {
		return originalDateOfExperiment;
	}
	public void setOriginalDateOfExperiment(List<String> originalDateOfExperiment) {
		this.originalDateOfExperiment = originalDateOfExperiment;
	}
	public List<Float> getOriginalBodyWeight() {
		return originalBodyWeight;
	}
	public void setOriginalBodyWeight(List<Float> originalBodyWeight) {
		this.originalBodyWeight = originalBodyWeight;
	}
	public void setOriginalResponse(List<String> originalResponse) {
		this.originalResponse = originalResponse;
	}
	@Override
	public String toString() {
		return "Details [responseType=" + responseType + ", rawDataSummaryStatistics=" + rawDataSummaryStatistics
				+ ", originalSex=" + originalSex + ", originalBiologicalSampleGroup=" + originalBiologicalSampleGroup
				+ ", originalResponse=" + originalResponse + ", originalDateOfExperiment=" + originalDateOfExperiment
				+ ", originalBodyWeight=" + originalBodyWeight + "]";
	}
	
	
	
	
}
