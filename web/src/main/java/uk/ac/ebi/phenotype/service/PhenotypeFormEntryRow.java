package uk.ac.ebi.phenotype.service;

public class PhenotypeFormEntryRow {

	private String phenotypeTerm;
	private Double pValueCutoffLow=new Double(0);//default set here.
	public Double getpValueCutoffLow() {
		return pValueCutoffLow;
	}

	public void setpValueCutoffLow(Double pValueCutoffLow) {
		this.pValueCutoffLow = pValueCutoffLow;
	}
	private Double pValueCutoffHigh=new Double(0.0001);//default set here.
	
	public PhenotypeFormEntryRow(String mpTerm){
		this.phenotypeTerm=mpTerm;
	}
	
	public PhenotypeFormEntryRow(String mpTerm, Double pValueCutoff){
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
	
}
