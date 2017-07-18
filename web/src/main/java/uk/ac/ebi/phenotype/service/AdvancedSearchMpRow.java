package uk.ac.ebi.phenotype.service;

public class AdvancedSearchMpRow {

	private String phenotypeTerm;
	private Double pValueCutoffLow=new Double(0);//default set here.
	public Double getpValueCutoffLow() {
		return pValueCutoffLow;
	}

	public void setpValueCutoffLow(Double pValueCutoffLow) {
		this.pValueCutoffLow = pValueCutoffLow;
	}
	private Double pValueCutoffHigh=new Double(0.0001);//default set here.
	
	public AdvancedSearchMpRow(String mpTerm){
		this.phenotypeTerm=mpTerm;
	}
	
	public AdvancedSearchMpRow(String mpTerm, Double pValueCutoff){
		this.phenotypeTerm=mpTerm;
		this.pValueCutoffHigh=pValueCutoff;
	}
	public Double getpValueCutoffHigh() {
		return pValueCutoffHigh;
	}
	public void setpValueCutoffHigh(Double pValueCutoff) {
		this.pValueCutoffHigh = pValueCutoff;
	}
	public String getPhenotypeTerm() {
		return phenotypeTerm;
	}
	public void setPhenotypeTerm(String phenotypeTerm) {
		this.phenotypeTerm = phenotypeTerm;
	}

	@Override
	public String toString() {
		return "AdvancedSearchMpRow{" +
				"phenotypeTerm='" + phenotypeTerm + '\'' +
				", pValueCutoffLow=" + pValueCutoffLow +
				", pValueCutoffHigh=" + pValueCutoffHigh +
				'}';
	}
}
