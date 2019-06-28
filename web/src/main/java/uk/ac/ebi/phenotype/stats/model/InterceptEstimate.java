package uk.ac.ebi.phenotype.stats.model;

public class InterceptEstimate {
//	 "intercept_estimate": {
//    "value": -1.48897104775161,
//    "level": 0.95
//  },
	
	private Double value;
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	private Double level;
	public Double getLevel() {
		return level;
	}
	public void setLevel(Double level) {
		this.level = level;
	}
}
