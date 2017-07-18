package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.List;

public class PhenotypeFormObject {
	
	private Logical logical1;
	private Logical logical2;
	private List<PhenotypeFormEntryRow> phenotypeFormRows=new ArrayList<>();

	public Logical getLogical1() {
		return logical1;
	}
	public void setLogical1(Logical logical1) {
		this.logical1 = logical1;
	}
	public Logical getLogical2() {
		return logical2;
	}
	public void setLogical2(Logical logical2) {
		this.logical2 = logical2;
	}
	public List<PhenotypeFormEntryRow> getPhenotypeFormRows() {
		return phenotypeFormRows;
	}
	public void setPhenotypeFormRows(List<PhenotypeFormEntryRow> phenotypeFormRows) {
		this.phenotypeFormRows = phenotypeFormRows;
	}
	
}
