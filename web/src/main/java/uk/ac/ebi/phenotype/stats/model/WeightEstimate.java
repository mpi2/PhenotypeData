package uk.ac.ebi.phenotype.stats.model;

public class WeightEstimate {
//"weight_estimate":{"value":0.224236854374259,"level":0.95},
	private Double value;
	private Double level;
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public Double getLevel() {
		return level;
	}
	public void setLevel(Double level) {
		this.level = level;
	}
}
