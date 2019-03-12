package org.mousephenotype.cda.solr.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Details {

	@JsonProperty("response_type")
	String responseType;
	
	@JsonProperty("raw_data_summary_statistics")
	RawSummaryStatistics rawDataSummaryStatistics;
	
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
}
