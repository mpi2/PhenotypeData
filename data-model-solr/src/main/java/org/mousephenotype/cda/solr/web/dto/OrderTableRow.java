package org.mousephenotype.cda.solr.web.dto;

import java.util.List;

/**
 * Represent the data in the order table on the gene page and possibly the new order tab on search
 * @author jwarren
 *
 */
public class OrderTableRow {
	private String alleleName;
	private String strainOfOrigin;
	private String alleleDescription;
	
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

	private String orderTargetVectorUrl;
	private String orderEsCellUrl;
	private String orderMouseUrl;
	
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

	public String getOrderTargetVectorUrl() {
		return orderTargetVectorUrl;
	}

	public void setOrderTargetVectorUrl(String orderTargetVectorUrl) {
		this.orderTargetVectorUrl = orderTargetVectorUrl;
	}

	public String getOrderEsCellUrl() {
		return orderEsCellUrl;
	}

	public void setOrderEsCellUrl(String orderEsCellUrl) {
		this.orderEsCellUrl = orderEsCellUrl;
	}

	public String getOrderMouseUrl() {
		return orderMouseUrl;
	}

	public void setOrderMouseUrl(String orderMouseUrl) {
		this.orderMouseUrl = orderMouseUrl;
	}

	public String getNoProductInfo() {
		return noProductInfo;
	}

	public void setNoProductInfo(String noProductInfo) {
		this.noProductInfo = noProductInfo;
	}

}
