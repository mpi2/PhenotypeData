package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Overal {

//	 "overall": {
//    "p_val": 0.170358323088087,
//    "n": 51,
//    "unique_n": 51,
//    "sd": 2.04220110034182,
//    "test": "Shapiro"
//  }
	@JsonProperty("p_val")
	private Double pVal;
	@JsonProperty("n")
	private Integer n;
	@JsonProperty("unique_n")
	private  Integer uniqueN;
	private Double sd;
	private String test;
	public Double getpVal() {
		return pVal;
	}
	public void setpVal(Double pVal) {
		this.pVal = pVal;
	}
	public Integer getN() {
		return n;
	}
	public void setN(Integer n) {
		this.n = n;
	}
	public Integer getUniqueN() {
		return uniqueN;
	}
	public void setUniqueN(Integer uniqueN) {
		this.uniqueN = uniqueN;
	}
	public Double getSd() {
		return sd;
	}
	public void setSd(Double sd) {
		this.sd = sd;
	}
	public String getTest() {
		return test;
	}
	public void setTest(String test) {
		this.test = test;
	}
}
