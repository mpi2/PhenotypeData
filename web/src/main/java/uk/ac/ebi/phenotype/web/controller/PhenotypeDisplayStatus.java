package uk.ac.ebi.phenotype.web.controller;

/**
 * Classs to encapsulate the decision of how we should display the phenothype data depending on the gene status and other information we have
 * @author jwarren
 *
 */
public class PhenotypeDisplayStatus {

	boolean displayHeatmap=false;
	boolean postQcTopLevelMPTermsAvailable=false;
	boolean postQcDataAvailable=false;
	boolean eitherPostQcOrPreQcDataIsAvailable=false;
	
	public boolean isDisplayHeatmap() {
		return displayHeatmap;
	}

	public boolean isPostQcTopLevelMPTermsAvailable() {
		return postQcTopLevelMPTermsAvailable;
	}

	public boolean isPostQcDataAvailable() {
		return postQcDataAvailable;
	}

	
	public PhenotypeDisplayStatus(){
		
	}

	public void setDisplayHeatmap(boolean b) {
		this.displayHeatmap=b;
		
	}

	public void setPostQcTopLevelMPTermsAvailable(boolean b) {
		this.postQcTopLevelMPTermsAvailable=b;
		
	}

	public void setPostQcDataAvailable(boolean b) {
		this.postQcDataAvailable=b;
		
	}
	
	public void setEitherPostQcOrPreQcDataIsAvailable(boolean b) {
		this.eitherPostQcOrPreQcDataIsAvailable=b;
		
	}

	public boolean isEitherPostQcOrPreQcDataIsAvailable() {
		return eitherPostQcOrPreQcDataIsAvailable;
	}

	@Override
	public String toString() {
		return "PhenotypeDisplayStatus [displayHeatmap=" + displayHeatmap + ", postQcTopLevelMPTermsAvailable="
				+ postQcTopLevelMPTermsAvailable + ", postQcDataAvailable=" + postQcDataAvailable
				+ ", eitherPostQcOrPreQcDataIsAvailable=" + eitherPostQcOrPreQcDataIsAvailable + "]";
	}

	
	
	
}
