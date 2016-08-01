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

	private List<LinkDetails> geneTargetDetails;
	private List<LinkDetails> orderTargetVectorDetails;
	

	public List<LinkDetails> getOrderTargetVectorDetails() {
		return orderTargetVectorDetails;
	}

	public void setOrderTargetVectorDetails(List<LinkDetails> orderTargetVectorDetails) {
		this.orderTargetVectorDetails = orderTargetVectorDetails;
	}

	private List<LinkDetails> orderEsCellDetails;
	
	public List<LinkDetails> getOrderEsCellDetails() {
		return orderEsCellDetails;
	}

	public void setOrderEsCelltDetails(List<LinkDetails> orderEsCellDetails) {
		this.orderEsCellDetails = orderEsCellDetails;
	}

	private List<LinkDetails> orderMouseDetails;
	
	public List<LinkDetails> getOrderMouseDetails() {
		return orderMouseDetails;
	}

	public void setOrderMouseDetails(List<LinkDetails> orderMouseDetails) {
		this.orderMouseDetails = orderMouseDetails;
	}

	
	//when no product add some info
	private String noProductInfo;

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

	

	public List<LinkDetails> getGeneTargetDetails() {
		return geneTargetDetails;
	}

	public void setGeneTargetDetails(List<LinkDetails> geneTargetDetails) {
		this.geneTargetDetails = geneTargetDetails;
	}

	public String getNoProductInfo() {
		return noProductInfo;
	}

	public void setNoProductInfo(String noProductInfo) {
		this.noProductInfo = noProductInfo;
	}

}
