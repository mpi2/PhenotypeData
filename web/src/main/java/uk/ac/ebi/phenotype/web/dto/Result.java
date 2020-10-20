package uk.ac.ebi.phenotype.web.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;

public class Result {
	@JsonIgnore
	private  JsonNode vectoroutput;
	
//	@JsonIgnore
//	private  VectorOutput vectoroutput;
	
	private  Details details;

	public Details getDetails() {
		return details;
	}

	public void setDetails(Details details) {
		this.details = details;
	}

	@Override
	public String toString() {
		return "Result [vectoroutput=" + vectoroutput + ", details=" + details + "]";
	}
	
	

}
