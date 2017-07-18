package uk.ac.ebi.phenotype.service;

public class PhenotypeFormEntryRow {

	private String phenotypeTerm;
	private Double pValueCutoff=new Double(0.0001);//default set here.
	
	
	public Double getpValueCutoff() {
		return pValueCutoff;
	}
	public void setpValueCutoff(Double pValueCutoff) {
		this.pValueCutoff = pValueCutoff;
	}
	public String getPhenotypeTerm() {
		return phenotypeTerm;
	}
	public void setPhenotypeTerm(String phenotypeTerm) {
		this.phenotypeTerm = phenotypeTerm;
	}
	
}
