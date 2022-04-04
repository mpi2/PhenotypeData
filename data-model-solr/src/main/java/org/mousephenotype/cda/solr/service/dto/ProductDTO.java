package org.mousephenotype.cda.solr.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

public class ProductDTO {

    public static final String PRODUCT_INDEX = "product_index";
    public static final String PRODUCT_ID = "product_id";
    public static final String ALLELE_ID = "allele_id";
    public static final String MARKER_SYMBOL = "marker_symbol";
    public static final String MGI_ACCESSION_ID = "mgi_accession_id";
    public static final String ALLELE_TYPE = "allele_type";
    public static final String ALLELE_NAME = "allele_name";
    public static final String ALLELE_DESCRIPTION = "allele_description";
    public static final String ALLELE_HAS_ISSUES = "allele_has_issues";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String PRODUCTION_CENTRE = "production_centre";
    public static final String PRODUCTION_COMPLETED = "production_completed";
    public static final String STATUS = "status";
    public static final String OTHER_LINKS = "other_links";
    public static final String ORDER_LINKS = "order_links";
    public static final String ORDER_NAMES = "order_names";
    public static final String TISSUE_ENQUIRY_TYPES = "tissue_enquiry_types";
    public static final String TISSUE_ENQUIRY_LINKS = "tissue_enquiry_links";
    public static final String TISSUE_DISTRIBUTION_CENTRES = "tissue_distribution_centres";
    public static final String QC_DATA = "qc_data";
    public static final String GENETIC_INFO = "genetic_info";

    public static final String PRODUCTION_PIPELINE = "production_pipeline";
    public static final String STATUS_DATE = "status_date";
    public static final String PRODUCTION_INFO = "production_info";

    public static final String ASSOCIATED_PRODUCT_COLONY_NAME = "associated_product_colony_name";
    public static final String ASSOCIATED_PRODUCT_ES_CELL_NAME = "associated_product_es_cell_name";
    public static final String ASSOCIATED_PRODUCT_VECTOR_NAME = "associated_product_vector_name";
    public static final String ASSOCIATED_PRODUCT_COLONY_NAMES = "associated_products_colony_names";
    public static final String ASSOCIATED_PRODUCTS_ES_CELL_NAMES = "associated_products_es_cell_names";

    public static final String CONTRACT_NAMES = "contact_names";
    public static final String CONTACT_LINKS = "contact_links";
    public static final String IKMC_PROJECT_ID = "ikmc_project_id";
    public static final String CASSETTE = "cassette";
    public static final String DESIGN_ID = "design_id";
    public static final String LOA_ASSAYS = "loa_assays";
    public static final String ALLELE_SYMBOL = "allele_symbol";
    public static final String AUTO_SUGGEST = "auto_suggest";
    public static final String ALLELE_DESIGN_PROJECT="allele_design_project";

    @Field(PRODUCT_INDEX)
    private String productIndex;

    @Field(ALLELE_DESIGN_PROJECT)
    private String alleleDesignProject;

    public String getAlleleDesignProject() {
		return alleleDesignProject;
	}

	public void setAlleleDesignProject(String alleleDesignProject) {
		this.alleleDesignProject = alleleDesignProject;
	}

	@Field(IKMC_PROJECT_ID)
    private String ikmcProjectId;

    @Field(CASSETTE)
    private String cassette;

    @Field(DESIGN_ID)
    private String designId;

    @Field(LOA_ASSAYS)
    private List<String> laoAssays;

    @Field(AUTO_SUGGEST)
    private List<String> autoSuggest;

    @Field(ASSOCIATED_PRODUCT_ES_CELL_NAME)
    private String associatedProductEsCellName;
    @Field(ASSOCIATED_PRODUCT_VECTOR_NAME)
    private String associatedProductVectorName;
    @Field(ASSOCIATED_PRODUCT_COLONY_NAMES)
    private List<String> associatedProductColonyNames;
    @Field(ASSOCIATED_PRODUCTS_ES_CELL_NAMES)
    private List<String> associatedProductEsCellNames;
    @Field(CONTRACT_NAMES)
    private List<String> contractNames;
    @Field(CONTACT_LINKS)
    private List<String> contactLinks;

    @Field(STATUS_DATE)
    private String statusDate;

    @Field(PRODUCTION_INFO)
    private List<String> productionInfo;

    @Field(ASSOCIATED_PRODUCT_COLONY_NAME)
    private String associatedProductColonyName;

    @Field(GENETIC_INFO)
    private List<String> geneticInfo;

    @Field(PRODUCTION_PIPELINE)
    private String productionPipeline;

    @Field(PRODUCTION_COMPLETED)
    private Boolean productionCompleted;

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

    @Field(ALLELE_DESCRIPTION)
    private String alleleDescription;

    @Field(ALLELE_HAS_ISSUES)
    private List<Boolean> alleleHasIssues;

    @Field(TYPE)
    private String type;

    @Field(NAME)
    private String name;

    @Field(PRODUCTION_CENTRE)
    private String productionCentre;

    @Field(STATUS)
    private String status;

    @Field(OTHER_LINKS)
    private List<String> otherLinks;//image link to the vector map

    @Field(ORDER_LINKS)
    private List<String> orderLinks;//image link to the vector map

    public List<String> getTissueEnquiryTypes() {
        return tissueEnquiryTypes;
    }

    public void setTissueEnquiryTypes(List<String> tissueEnquiryTypes) {
        this.tissueEnquiryTypes = tissueEnquiryTypes;
    }

    public Boolean getProductionCompleted() {
        return productionCompleted;
    }

    public void setProductionCompleted(Boolean productionCompleted) {
        this.productionCompleted = productionCompleted;
    }

    @Field(TISSUE_ENQUIRY_TYPES)
    private List<String> tissueEnquiryTypes;

    public List<String> getTissueDistributionCentres() {
        return tissueDistributionCentres;
    }

    public void setTissueDistributionCentres(List<String> tissueDistributionCentres) {
        this.tissueDistributionCentres = tissueDistributionCentres;
    }

    @Field(TISSUE_DISTRIBUTION_CENTRES)
    private List<String> tissueDistributionCentres;

    public List<String> getTissueEnquiryLinks() {
        return tissueEnquiryLinks;
    }

    public void setTissueEnquiryLinks(List<String> tissueEnquiryLinks) {
        this.tissueEnquiryLinks = tissueEnquiryLinks;
    }

    @Field(TISSUE_ENQUIRY_LINKS)
    private List<String> tissueEnquiryLinks;

    @Field(ORDER_NAMES)
    private List<String> orderNames;

    @Field(QC_DATA)
    private List<String> qcData;


    public String getProductIndex() {
        return productIndex;
    }

    public void setProductIndex(String productIndex) {
        this.productIndex = productIndex;
    }

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

    public String getAlleleDescription() {
        return alleleDescription;
    }

    public void setAlleleDescription(String alleleDescription) {
        this.alleleDescription = alleleDescription;
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

    public static String getTYPE() {
        return TYPE;
    }

    public String getIkmcProjectId() {
        return ikmcProjectId;
    }

    public void setIkmcProjectId(String ikmcProjectId) {
        this.ikmcProjectId = ikmcProjectId;
    }

    public String getCassette() {
        return cassette;
    }

    public void setCassette(String cassette) {
        this.cassette = cassette;
    }

    public String getDesignId() {
        return designId;
    }

    public void setDesignId(String designId) {
        this.designId = designId;
    }

    public List<String> getLaoAssays() {
        return laoAssays;
    }

    public void setLaoAssays(List<String> laoAssays) {
        this.laoAssays = laoAssays;
    }

    public List<String> getAutoSuggest() {
        return autoSuggest;
    }

    public void setAutoSuggest(List<String> autoSuggest) {
        this.autoSuggest = autoSuggest;
    }

    public String getAssociatedProductEsCellName() {
        return associatedProductEsCellName;
    }

    public void setAssociatedProductEsCellName(String associatedProductEsCellName) {
        this.associatedProductEsCellName = associatedProductEsCellName;
    }

    public String getAssociatedProductVectorName() {
        return associatedProductVectorName;
    }

    public void setAssociatedProductVectorName(String associatedProductVectorName) {
        this.associatedProductVectorName = associatedProductVectorName;
    }

    public List<String> getAssociatedProductColonyNames() {
        return associatedProductColonyNames;
    }

    public void setAssociatedProductColonyNames(List<String> associatedProductColonyNames) {
        this.associatedProductColonyNames = associatedProductColonyNames;
    }

    public List<String> getAssociatedProductEsCellNames() {
        return associatedProductEsCellNames;
    }

    public void setAssociatedProductEsCellNames(List<String> associatedProductEsCellNames) {
        this.associatedProductEsCellNames = associatedProductEsCellNames;
    }

    public List<String> getContractNames() {
        return contractNames;
    }

    public void setContractNames(List<String> contractNames) {
        this.contractNames = contractNames;
    }

    public List<String> getContactLinks() {
        return contactLinks;
    }

    public void setContactLinks(List<String> contactLinks) {
        this.contactLinks = contactLinks;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public List<String> getProductionInfo() {
        return productionInfo;
    }

    public void setProductionInfo(List<String> productionInfo) {
        this.productionInfo = productionInfo;
    }

    public String getAssociatedProductColonyName() {
        return associatedProductColonyName;
    }

    public void setAssociatedProductColonyName(String associatedProductColonyName) {
        this.associatedProductColonyName = associatedProductColonyName;
    }

    public List<String> getGeneticInfo() {
        return geneticInfo;
    }

    public void setGeneticInfo(List<String> geneticInfo) {
        this.geneticInfo = geneticInfo;
    }

    public String getProductionPipeline() {
        return productionPipeline;
    }

    public void setProductionPipeline(String productionPipeline) {
        this.productionPipeline = productionPipeline;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "ikmcProjectId='" + ikmcProjectId + '\'' +
                ", productIndex='" + productIndex + '\'' +
                ", cassette='" + cassette + '\'' +
                ", designId='" + designId + '\'' +
                ", laoAssays=" + laoAssays +
                ", autoSuggest=" + autoSuggest +
                ", associatedProductEsCellName='" + associatedProductEsCellName + '\'' +
                ", associatedProductVectorName='" + associatedProductVectorName + '\'' +
                ", associatedProductColonyNames=" + associatedProductColonyNames +
                ", associatedProductEsCellNames=" + associatedProductEsCellNames +
                ", contractNames=" + contractNames +
                ", contactLinks=" + contactLinks +
                ", statusDate='" + statusDate + '\'' +
                ", productionInfo=" + productionInfo +
                ", associatedProductColonyName='" + associatedProductColonyName + '\'' +
                ", geneticInfo=" + geneticInfo +
                ", productionPipeline='" + productionPipeline + '\'' +
                ", productionCompleted=" + productionCompleted +
                ", productId='" + productId + '\'' +
                ", alleleId='" + alleleId + '\'' +
                ", markerSymbol='" + markerSymbol + '\'' +
                ", mgiAccessionId='" + mgiAccessionId + '\'' +
                ", alleleType='" + alleleType + '\'' +
                ", alleleName='" + alleleName + '\'' +
                ", alleleHasIssues=" + alleleHasIssues +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", productionCentre='" + productionCentre + '\'' +
                ", status='" + status + '\'' +
                ", otherLinks=" + otherLinks +
                ", orderLinks=" + orderLinks +
                ", orderNames=" + orderNames +
                ", qcData=" + qcData +
                '}';
    }
}
