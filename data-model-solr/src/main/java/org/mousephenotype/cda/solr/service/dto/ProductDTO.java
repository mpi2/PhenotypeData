package org.mousephenotype.cda.solr.service.dto;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class ProductDTO {

    public static final String PRODUCT_ID = "product_id";
    public static final String ALLELE_ID = "allele_id";
    public static final String MARKER_SYMBOL = "marker_symbol";
    public static final String MGI_ACCESSION_ID = "mgi_accession_id";
    public static final String ALLELE_TYPE = "allele_type";
    public static final String ALLELE_NAME = "allele_name";
    public static final String ALLELE_HAS_ISSUES = "allele_has_issues";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String PRODUCT_CENTRE = "production_centre";
    public static final String PRODUCT_COMPLETED = "production_completed";
    public static final String STATUS = "status";
    public static final String OTHER_LINKS = "other_links";
    public static final String ORDER_LINKS = "order_links";
    public static final String ORDER_NAMES = "order_names";
    public static final String QC_DATA = "qc_data";

    @Field(PRODUCT_ID)
    private String productId;

    @Field(ALLELE_ID)
    private String alleleId;

    @Field(MARKER_SYMBOL)
    private String markerSymbol;

    @Field(MGI_ACCESSION_ID)
    private String mgiAccessionId;

    @Field(ALLELE_TYPE)
    private String alleleType;

    @Field(ALLELE_NAME)
    private String alleleName;

    @Field(ALLELE_HAS_ISSUES)
    private List<Boolean> alleleHasIssues;

    @Field(TYPE)
    private String type;

    @Field(NAME)
    private String name;

    @Field(PRODUCT_CENTRE)
    private String productionCentre;

    @Field(PRODUCT_COMPLETED)
    private boolean productionCompleted;

    @Field(STATUS)
    private String status;

    @Field(OTHER_LINKS)
    private List<String> otherLinks;//image link to the vector map

    @Field(ORDER_LINKS)
    private List<String> orderLinks;//image link to the vector map

    @Field(ORDER_NAMES)
    private List<String> orderNames;

    @Field(QC_DATA)
    private List<String> qcData;

    public List<String> getQcData() {
        return qcData;
    }

    public void setQcData(List<String> qcData) {
        this.qcData = qcData;
    }

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

    public List<Boolean> getAlleleHasIssues() {
        return alleleHasIssues;
    }

    public void setAlleleHasIssues(List<Boolean> alleleHasIssues) {
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

    public List<String> getOrderNames() {
        return this.orderNames;

    }

    @Override
    public String toString() {
        return "ProductDTO [productId=" + productId + ", alleleId=" + alleleId + ", markerSymbol=" + markerSymbol
                + ", mgiAccessionId=" + mgiAccessionId + ", alleleType=" + alleleType + ", alleleName=" + alleleName
                + ", alleleHasIssues=" + alleleHasIssues + ", type=" + type + ", name=" + name + ", productionCentre="
                + productionCentre + ", productionCompleted=" + productionCompleted + ", status=" + status
                + ", otherLinks=" + otherLinks + ", orderLinks=" + orderLinks + ", orderNames=" + orderNames + "]";
    }


}
