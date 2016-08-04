package org.mousephenotype.cda.solr.web.dto;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Represent the data in the order table on the gene page and possibly the new order tab on search
 * @author jwarren
 *
 */
public class OrderTableRow {
	private String alleleName;
	private String strainOfOrigin;
	private String alleleDescription;
	private Boolean targetingVectorAvailable;
	private Boolean esCellAvailable;
	private String markerSymbol;
	
	public String getMarkerSymbol() {
			return markerSymbol;
		}
		public void setMarkerSymbol(String markerSymbol) {
			this.markerSymbol = markerSymbol;
		}
	
	private Boolean mouseAvailable;
	
	public Boolean getMouseAvailable() {
		return mouseAvailable;
	}
	public void setMouseAvailable(Boolean mouseAvailable) {
		this.mouseAvailable = mouseAvailable;
	}

	public Boolean getEsCellAvailable() {
		return esCellAvailable;
	}
	public void setEsCellAvailable(Boolean esCellAvailable) {
		this.esCellAvailable = esCellAvailable;
	}
	
	public Boolean getTargetingVectorAvailable() {
		return targetingVectorAvailable;
	}
	public void setTargetingVectorAvailable(Boolean targetingVectorAvailable) {
		this.targetingVectorAvailable = targetingVectorAvailable;
	}
	
	public String getAlleleDescription() {
		return alleleDescription;
	}

	public void setAlleleDescription(String alleleDescription) {
		this.alleleDescription = alleleDescription;
	}

	
	//when no product add some info
	private String noProductInfo;
	private String type;
	private String geneMapLink;
	public String getGeneMapLink() {
		return geneMapLink;
	}
	public void setGeneMapLink(String geneMapLink) {
		this.geneMapLink = geneMapLink;
	}
	public String getVectorMapLink() {
		return vectorMapLink;
	}
	public void setVectorMapLink(String vectorMapLink) {
		this.vectorMapLink = vectorMapLink;
	}
	public String getGeneGenbankLink() {
		return geneGenbankLink;
	}
	public void setGeneGenbankLink(String geneGenbankLink) {
		this.geneGenbankLink = geneGenbankLink;
	}
	public String getVectorGenbankLink() {
		return vectorGenbankLink;
	}
	public void setVectorGenbankLink(String vectorGenbankLink) {
		this.vectorGenbankLink = vectorGenbankLink;
	}

	private String vectorMapLink;
	private String geneGenbankLink;
	private String vectorGenbankLink;


	
	
	public String getType() {
		return type;
	}
	public String getAlleleName() {
		return alleleName;
	}

	public void setAlleleName(String alleleName) {
		this.alleleName = alleleName;
	}

	public String getStrainOfOrigin() {
		return strainOfOrigin;
	}

	public void setStrainOfOrigin(String strainOfOrigin) {
		this.strainOfOrigin = strainOfOrigin;
	}


	public String getNoProductInfo() {
		return noProductInfo;
	}

	public void setNoProductInfo(String noProductInfo) {
		this.noProductInfo = noProductInfo;
	}
	public void setType(String type) {
		this.type=type;
		
	}
	@Override
	public String toString() {
		return "OrderTableRow [geneMapLink=" + geneMapLink + ", vectorMapLink=" + vectorMapLink + ", geneGenbankLink="
				+ geneGenbankLink + ", vectorGenbankLink=" + vectorGenbankLink + "]";
	}
	

}
