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
	private String alleleType;
	
	private List<GeneTargetDetail> geneTargetDetails;
	
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

	public String getAlleleType() {
		return alleleType;
	}

	public void setAlleleType(String alleleType) {
		this.alleleType = alleleType;
	}

	public List<GeneTargetDetail> getGeneTargetDetails() {
		return geneTargetDetails;
	}

	public void setGeneTargetDetails(List<GeneTargetDetail> geneTargetDetails) {
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
