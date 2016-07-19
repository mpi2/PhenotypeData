package org.mousephenotype.cda.solr.service.dto;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class ProductDTO {

	public static final String PRODUCT_ID = "product_id";
	@Field(PRODUCT_ID)
	private String productId;
	public static final String ALLELE_ID = "allele_id";
	@Field(ALLELE_ID)
	private String alleleId;

	public static final String MARKER_SYMBOL = "marker_symbol";
	@Field(MARKER_SYMBOL)
	private String markerSymbol;
	public static final String MGI_ACCESSION_ID = "mgi_accession_id";
	@Field(MGI_ACCESSION_ID)
	private String mgiAccessionId;
	public static final String ALLELE_TYPE = "allele_type";
	@Field(ALLELE_TYPE)
	private String alleleType;
	public static final String ALLELE_NAME = "allele_name";
	@Field(ALLELE_NAME)
	private String alleleName;
	public static final String ALLELE_HAS_ISSUES = "allele_has_issues";// :																	// false,
	@Field(ALLELE_HAS_ISSUES)
	private boolean alleleHasIssues;
	public static final String TYPE = "type";
	@Field(TYPE)
	private String type;
	public static final String NAME = "name";
	@Field(NAME)
	private String name;
	public static final String PRODUCT_CENTRE = "production_centre";
	@Field(PRODUCT_CENTRE)
	private String productionCentre;
	public static final String PRODUCT_COMPLETED = "production_completed";// :																		// true,
	@Field(PRODUCT_COMPLETED)
	private boolean productionCompleted;
	public static final String STATUS = "status";
	@Field(STATUS)
	private String status;
	
	public static final String OTHER_LINKS="other_links";
	@Field(OTHER_LINKS)
	private List<String> otherLinks;//image link to the vector map
	
	public static final String ORDER_LINKS="other_links";
	@Field(ORDER_LINKS)
	private List<String> orderLinks;//image link to the vector map
	
	public static final String ORDER_NAMES="other_names";
	@Field(ORDER_NAMES)
	private List<String> orderNames;
	
	public void setOrderNames(List<String> orderNames) {
		this.orderNames = orderNames;
	}
	public List<String> getOrderLinks() {
		return orderLinks;
	}
	public void setOrderLinks(List<String> orderLinks) {
		this.orderLinks = orderLinks;
	}
	public List<String> getOtherLinks() {
		return otherLinks;
	}
	public void setOtherLinks(List<String> otherLinks) {
		this.otherLinks = otherLinks;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getAlleleId() {
		return alleleId;
	}
	public void setAlleleId(String alleleId) {
		this.alleleId = alleleId;
	}
	public String getMarkerSymbol() {
		return markerSymbol;
	}
	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}
	public String getMgiAccessionId() {
		return mgiAccessionId;
	}
	public void setMgiAccessionId(String mgiAccessionId) {
		this.mgiAccessionId = mgiAccessionId;
	}
	public String getAlleleType() {
		return alleleType;
	}
	public void setAlleleType(String alleleType) {
		this.alleleType = alleleType;
	}
	public String getAlleleName() {
		return alleleName;
	}
	public void setAlleleName(String alleleName) {
		this.alleleName = alleleName;
	}
	public boolean isAlleleHasIssues() {
		return alleleHasIssues;
	}
	public void setAlleleHasIssues(boolean alleleHasIssues) {
		this.alleleHasIssues = alleleHasIssues;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProductionCentre() {
		return productionCentre;
	}
	public void setProductionCentre(String productionCentre) {
		this.productionCentre = productionCentre;
	}
	public boolean isProductionCompleted() {
		return productionCompleted;
	}
	public void setProductionCompleted(boolean productionCompleted) {
		this.productionCompleted = productionCompleted;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "ProductDTO [productId=" + productId + ", alleleId=" + alleleId + ", markerSymbol=" + markerSymbol
				+ ", mgiAccessionId=" + mgiAccessionId + ", alleleType=" + alleleType + ", alleleName=" + alleleName
				+ ", alleleHasIssues=" + alleleHasIssues + ", type=" + type + ", name=" + name + ", productionCentre="
				+ productionCentre + ", productionCompleted=" + productionCompleted + ", status=" + status + "]";
	}
	public List<String> getOrderNames() {
		return this.orderNames;
		
	}
			
}
