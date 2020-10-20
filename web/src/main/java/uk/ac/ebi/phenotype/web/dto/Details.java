package uk.ac.ebi.phenotype.web.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Details {

	@JsonProperty("response_type")
	private String responseType;
	
	@JsonProperty("raw_data_summary_statistics")
	private RawSummaryStatistics rawDataSummaryStatistics;
	

	private List<String> originalSex;
	
	
	private List<String> originalBiologicalSampleGroup;
	
	
	private List<String> originalResponse;//values
	
	
	private List<String> originalDateOfExperiment;
	
	
	private List<Float> originalBodyWeight;
	
	@JsonProperty("points")
	private List<Point> points=new ArrayList<>();
	
	
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
	//dont print the sex on its own in the rest service as we want only the object representing each point.
	@JsonIgnore
	public List<String> getOriginalSex() {
		return originalSex;
	}
	
	@JsonProperty("original_sex")
	public void setOriginalSex(List<String> originalSex) {
		this.originalSex = originalSex;
	}
	
	@JsonIgnore
	public List<String> getOriginalBiologicalSampleGroup() {
		return originalBiologicalSampleGroup;
	}
	
	@JsonProperty("original_biological_sample_group")
	public void setOriginalBiologicalSampleGroup(List<String> originalBiologicalSampleGroup) {
		this.originalBiologicalSampleGroup = originalBiologicalSampleGroup;
	}
	
	@JsonIgnore
	public List<String> getOriginalResponse() {
		return originalResponse;
	}
	
	@JsonIgnore
	public List<String> getOriginalDateOfExperiment() {
		return originalDateOfExperiment;
	}
	@JsonProperty("original_date_of_experiment")
	public void setOriginalDateOfExperiment(List<String> originalDateOfExperiment) {
		this.originalDateOfExperiment = originalDateOfExperiment;
	}
	
	@JsonIgnore
	public List<Float> getOriginalBodyWeight() {
		return originalBodyWeight;
	}
	
	@JsonProperty("original_body_weight")
	public void setOriginalBodyWeight(List<Float> originalBodyWeight) {
		this.originalBodyWeight = originalBodyWeight;
	}
	
	@JsonProperty("original_response")
	public void setOriginalResponse(List<String> originalResponse) {
		this.originalResponse = originalResponse;
	}
	
	
	public List<Point> getPoints() {
//		List<Point> points=new ArrayList<>();
//		
//		for(int i=0; i<originalResponse.size(); i++) {
//			points.add(new Point(originalResponse.get(i),originalSex.get(i), originalBiologicalSampleGroup.get(i), originalBodyWeight.get(i) ));
//		}
		return this.points;
	}
	
	public List<Point> setPoints() {
		
		for(int i=0; i<originalResponse.size(); i++) {
			points.add(new Point(originalResponse.get(i),originalSex.get(i), originalBiologicalSampleGroup.get(i), originalBodyWeight.get(i) , originalDateOfExperiment.get(i)));
		}
		System.out.println("set point size="+this.points.size());
		return points;
	}
	
	@Override
	public String toString() {
		return "Details [responseType=" + responseType + ", rawDataSummaryStatistics=" + rawDataSummaryStatistics
				+ ", originalSex=" + originalSex + ", originalBiologicalSampleGroup=" + originalBiologicalSampleGroup
				+ ", originalResponse=" + originalResponse + ", originalDateOfExperiment=" + originalDateOfExperiment
				+ ", originalBodyWeight=" + originalBodyWeight + "]";
	}
	
	
	
	
	
	
}
