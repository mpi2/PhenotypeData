package uk.ac.ebi.phenotype.stats.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class Result {
	
	@JsonProperty("vectoroutput")
	private  VectorOutput vectoroutput;
	
	public VectorOutput getVectoroutput() {
		return vectoroutput;
	}

	public void setVectoroutput(VectorOutput vectoroutput) {
		this.vectoroutput = vectoroutput;
	}

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
