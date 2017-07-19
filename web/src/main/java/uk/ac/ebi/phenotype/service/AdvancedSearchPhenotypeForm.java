package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.List;

public class AdvancedSearchPhenotypeForm {

	// inputs and checkboxes
	private Logical logical1;
	private Logical logical2;
	private List<AdvancedSearchMpRow> phenotypeRows =new ArrayList<>();
	private Boolean excludeNestedPhenotype;
	private String impressParameterName;
	private Boolean significantPvaluesOnly;

	//customed output columns
	private Boolean showMpTerm;
	private Boolean showMpTermSynonym;
	private Boolean showMpId;
	private Boolean showMpDefinition;

	private Boolean showTopLevelMpTerm;
	private Boolean showTopLevelMpId;

	private Boolean showParameterName;
	private Boolean showPvalue;


	public void addPhenotypeRows(AdvancedSearchMpRow row) {
		this.phenotypeRows.add(row);
		if(this.phenotypeRows.size()>3){
			System.err.println("Phenotype form rows exceeds 3 - max is currently set to 3");
		}
	}

	public List<AdvancedSearchMpRow> getPhenotypeRows() {
		return phenotypeRows;
	}

	public void setPhenotypeRows(List<AdvancedSearchMpRow> phenotypeRows) {
		this.phenotypeRows = phenotypeRows;
	}

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

	public Boolean getExcludeNestedPhenotype() {
		return excludeNestedPhenotype;
	}

	public void setExcludeNestedPhenotype(Boolean excludeNestedPhenotype) {
		this.excludeNestedPhenotype = excludeNestedPhenotype;
	}

	public String getImpressParameterName() {
		return impressParameterName;
	}

	public void setImpressParameterName(String impressParameterName) {
		this.impressParameterName = impressParameterName;
	}

	public Boolean getSignificantPvaluesOnly() {
		return significantPvaluesOnly;
	}

	public void setSignificantPvaluesOnly(Boolean significantPvaluesOnly) {
		this.significantPvaluesOnly = significantPvaluesOnly;
	}

	public Boolean getShowMpTerm() {
		return showMpTerm;
	}

	public void setShowMpTerm(Boolean showMpTerm) {
		this.showMpTerm = showMpTerm;
	}

	public Boolean getShowMpTermSynonym() {
		return showMpTermSynonym;
	}

	public void setShowMpTermSynonym(Boolean showMpTermSynonym) {
		this.showMpTermSynonym = showMpTermSynonym;
	}

	public Boolean getShowMpId() {
		return showMpId;
	}

	public void setShowMpId(Boolean showMpId) {
		this.showMpId = showMpId;
	}

	public Boolean getShowMpDefinition() {
		return showMpDefinition;
	}

	public void setShowMpDefinition(Boolean showMpDefinition) {
		this.showMpDefinition = showMpDefinition;
	}

	public Boolean getShowTopLevelMpTerm() {
		return showTopLevelMpTerm;
	}

	public void setShowTopLevelMpTerm(Boolean showTopLevelMpTerm) {
		this.showTopLevelMpTerm = showTopLevelMpTerm;
	}

	public Boolean getShowTopLevelMpId() {
		return showTopLevelMpId;
	}

	public void setShowTopLevelMpId(Boolean showTopLevelMpId) {
		this.showTopLevelMpId = showTopLevelMpId;
	}

	public Boolean getShowParameterName() {
		return showParameterName;
	}

	public void setShowParameterName(Boolean showParameterName) {
		this.showParameterName = showParameterName;
	}

	public Boolean getShowPvalue() {
		return showPvalue;
	}

	public void setShowPvalue(Boolean showPvalue) {
		this.showPvalue = showPvalue;
	}

	@Override
	public String toString() {
		return "AdvancedSearchPhenotypeForm{" +
				"logical1=" + logical1 +
				", logical2=" + logical2 +
				", phenotypeRows=" + phenotypeRows +
				", excludeNestedPhenotype=" + excludeNestedPhenotype +
				", impressParameterName='" + impressParameterName + '\'' +
				", significantPvaluesOnly=" + significantPvaluesOnly +
				", showMpTerm=" + showMpTerm +
				", showMpTermSynonym=" + showMpTermSynonym +
				", showMpId=" + showMpId +
				", showMpDefinition=" + showMpDefinition +
				", showTopLevelMpTerm=" + showTopLevelMpTerm +
				", showTopLevelMpId=" + showTopLevelMpId +
				", showParameterName=" + showParameterName +
				", showPvalue=" + showPvalue +
				'}';
	}

	//	public Logical getLogical1() {
//		return logical1;
//	}
//	public void setLogical1(Logical logical1) {
//		this.logical1 = logical1;
//	}
//
//	public Logical getLogical2() {
//		return logical2;
//	}
//	public void setLogical2(Logical logical2) {
//		this.logical2 = logical2;
//	}
//
//	public List<AdvancedSearchMpRow> getPhenotypeRows() {
//		return phenotypeRows;
//	}
//	public void setPhenotypeRows(List<AdvancedSearchMpRow> phenotypeRows) {
//		this.phenotypeRows = phenotypeRows;
//	}
//	public void addPhenotypeRows(AdvancedSearchMpRow row) {
//		this.phenotypeRows.add(row);
//		if(this.phenotypeRows.size()>3){
//			System.err.println("Phenotype form rows exceeds 3 - max is currently set to 3");
//		}
//	}
//
//	public Boolean getExcludeNestedPhenotype() {
//		return excludeNestedPhenotype;
//	}
//
//	public void setExcludeNestedPhenotype(Boolean excludeNestedPhenotype) {
//		this.excludeNestedPhenotype = excludeNestedPhenotype;
//	}
//
//	public String getParameterName() {
//		return parameterName;
//	}
//
//	public void setParameterName(String parameterName) {
//		this.parameterName = parameterName;
//	}
//
//	public Boolean getSignificantPvaluesOnly() {
//		return significantPvaluesOnly;
//	}
//
//	public void onlySignificantPvalue(Boolean significantPvaluesOnly) {
//		this.significantPvaluesOnly = significantPvaluesOnly;
//	}
//
//	public Boolean getShowMpTerm() {
//		return showMpTerm;
//	}
//
//	public void setShowMpTerm(Boolean showMpTerm) {
//		this.showMpTerm = showMpTerm;
//	}
//
//	public Boolean getShowMpTermSynonym() {
//		return showMpTermSynonym;
//	}
//
//	public void setShowMpTermSynonym(Boolean showMpTermSynonym) {
//		this.showMpTermSynonym = showMpTermSynonym;
//	}
//
//	public Boolean getShowMpId() {
//		return showMpId;
//	}
//
//	public void setShowMpId(Boolean showMpId) {
//		this.showMpId = showMpId;
//	}
//
//	public Boolean getShowMpDefinition() {
//		return showMpDefinition;
//	}
//
//	public void setShowMpDefinition(Boolean showMpDefinition) {
//		this.showMpDefinition = showMpDefinition;
//	}
//
//	public Boolean getShowTopLevelMpTerm() {
//		return showTopLevelMpTerm;
//	}
//
//	public void setShowTopLevelMpTerm(Boolean showTopLevelMpTerm) {
//		this.showTopLevelMpTerm = showTopLevelMpTerm;
//	}
//
//	public Boolean getShowTopLevelMpId() {
//		return showTopLevelMpId;
//	}
//
//	public void setShowTopLevelMpId(Boolean showTopLevelMpId) {
//		this.showTopLevelMpId = showTopLevelMpId;
//	}
//
//	public Boolean getShowParameterName() {
//		return showParameterName;
//	}
//
//	public void setShowParameterName(Boolean showParameterName) {
//		this.showParameterName = showParameterName;
//	}
//
//	public Boolean getShowPvalue() {
//		return showPvalue;
//	}
//
//	public void setShowPvalue(Boolean showPvalue) {
//		this.showPvalue = showPvalue;
//	}
//
//	@Override
//	public String toString() {
//		return "AdvancedSearchPhenotypeForm{" +
//				"logical1=" + logical1 +
//				", logical2=" + logical2 +
//				", phenotypeRows=" + phenotypeRows +
//				", excludeNestedPhenotype=" + excludeNestedPhenotype +
//				", parameterName='" + parameterName + '\'' +
//				", significantPvaluesOnly=" + significantPvaluesOnly +
//				", showMpTerm=" + showMpTerm +
//				", showMpTermSynonym=" + showMpTermSynonym +
//				", showMpId=" + showMpId +
//				", showMpDefinition=" + showMpDefinition +
//				", showTopLevelMpTerm=" + showTopLevelMpTerm +
//				", showTopLevelMpId=" + showTopLevelMpId +
//				", showParameterName=" + showParameterName +
//				", showPvalue=" + showPvalue +
//				'}';
//	}
}
