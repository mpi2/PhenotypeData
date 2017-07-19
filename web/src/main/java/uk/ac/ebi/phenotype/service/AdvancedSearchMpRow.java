package uk.ac.ebi.phenotype.service;

public class AdvancedSearchMpRow {

	private String phenotypeTerm;
	private Double lowerPvalue;
	private Double upperPvalue;

	public AdvancedSearchMpRow(String mpTerm) {
		this.phenotypeTerm = mpTerm;
	}

	public AdvancedSearchMpRow(String mpTerm, Double lowerPvalue, Double upperPvalue) {
		this.phenotypeTerm = mpTerm;
		this.lowerPvalue = lowerPvalue;
		this.upperPvalue = upperPvalue;
	}

	public String getPhenotypeTerm() {
		return phenotypeTerm;
	}

	public void setPhenotypeTerm(String phenotypeTerm) {
		this.phenotypeTerm = phenotypeTerm;
	}

	public Double getLowerPvalue() {
		return lowerPvalue;
	}

	public void setLowerPvalue(Double lowerPvalue) {
		this.lowerPvalue = lowerPvalue;
	}

	public Double getUpperPvalue() {
		return upperPvalue;
	}

	public void setUpperPvalue(Double upperPvalue) {
		this.upperPvalue = upperPvalue;
	}

	@Override
	public String toString() {
		return "AdvancedSearchMpRow{" +
				"phenotypeTerm='" + phenotypeTerm + '\'' +
				", lowerPvalue=" + lowerPvalue +
				", upperPvalue=" + upperPvalue +
				'}';
	}
}
