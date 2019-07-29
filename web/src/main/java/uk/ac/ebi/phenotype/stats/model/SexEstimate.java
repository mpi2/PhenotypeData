package uk.ac.ebi.phenotype.stats.model;

public class SexEstimate {
	//"sex_estimate":{"value":-0.455444113587709,"level":0.95}
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
