package org.mousephenotype.cda.solr.service.dto;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class Allele2DTO {
	public static final String MARKER_SYMBOL="marker_symbol";
	@Field(MARKER_SYMBOL)
	private String markerSymbol;
	
	public static final String MGI_ACCESSION_ID="mgi_accession_id";
	@Field(MGI_ACCESSION_ID)
	private String mgiAccessionId;
	
	public static final String ALLELE_MGI_ACCESSION_ID="allele_mgi_accession_id";
	@Field(ALLELE_MGI_ACCESSION_ID)
	private String alleleMgiAccessionId;
	
	
	public static final String ALLELE_NAME="allele_name";
	@Field(ALLELE_NAME)
	private String alleleName;
	
	public static final String ALLELE_TYPE="allele_type";
	@Field(ALLELE_TYPE)
	private String alleleType;
	
	public static final String ALLELE_DESCRIPTION="allele_description";
	@Field(ALLELE_DESCRIPTION)
	private String alleleDescription;
	
	public static final String GENBANK_FILE="genbank_file";
	@Field(GENBANK_FILE)
	private String genbankFile;
	
	public static final String ALLELE_IMAGE="allele_image";
	@Field(ALLELE_IMAGE)
	private String alleleImage;
	
	public static final String ALLELE_SIMPLE_IMAGE="allele_simple_image";
	@Field(ALLELE_SIMPLE_IMAGE)
	private String alleleSimpleImage;
	
	public static final String DESIGN_ID="design_id";
	@Field(DESIGN_ID)
	private String designId;
	
	public static final String CASSETTE="cassette";
	@Field(CASSETTE)
	private String cassette;
	
	public static final String MOUSE_STATUS="mouse_status";
	@Field(MOUSE_STATUS)
	private String mouseStatus;
	
	public static final String ES_CELL_STATUS="es_cell_status";
	@Field(ES_CELL_STATUS)
	private String esCellStatus;
	
	public static final String PRODUCTION_CENTRE="production_centre";
	@Field(PRODUCTION_CENTRE)
	private String productionCentre;
	
	public static final String TYPE="type";
	@Field(TYPE)
	private String type;
	
	public static final String IKMC_PROJECT="ikmc_project";//8856
	@Field(IKMC_PROJECT)
	private List<String> ikmcProject;
	
	public static final String PIPELINE="pipeline";
	@Field(PIPELINE)
	private List<String> pipeline;
	
	public static final String LINKS="links";
	@Field(LINKS)
	private List<String> links;
	
	public static final String TARGETING_VECTOR_AVAILABLE="targeting_vector_available";
	@Field(TARGETING_VECTOR_AVAILABLE)
	private Boolean targetingVectorAvailable;
	
	public static final String ES_CELL_AVAILABLE="es_cell_available";
	@Field(ES_CELL_AVAILABLE)
	private Boolean esCellAvailable;
	
	
	public static final String MOUSE_AVAILABLE="mouse_available";
	@Field(MOUSE_AVAILABLE)
	private Boolean mouseAvailable;
	
	
	public static final String VECTOR_ALLELE_IMAGE="vector_allele_image";
	@Field(VECTOR_ALLELE_IMAGE)
	private String vectorAlleleImage;
	
	
	public static final String VECTOR_GENBANK_LINK="vector_genbank_file";
	@Field(VECTOR_GENBANK_LINK)
	private String vectorGenbankLink;
	
	public String getVectorGenbankLink() {
		return vectorGenbankLink;
	}
	public void setVectorGenbankLink(String vectorGenbankLink) {
		this.vectorGenbankLink = vectorGenbankLink;
	}
	public String getVectorAlleleImage() {
		return vectorAlleleImage;
	}
	public void setVectorAlleleImage(String vectorAlleleImage) {
		this.vectorAlleleImage = vectorAlleleImage;
	}
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
	public String getAlleleMgiAccessionId() {
		return alleleMgiAccessionId;
	}
	public void setAlleleMgiAccessionId(String alleleMgiAccessionId) {
		this.alleleMgiAccessionId = alleleMgiAccessionId;
	}
	public String getAlleleName() {
		return alleleName;
	}
	public void setAlleleName(String alleleName) {
		this.alleleName = alleleName;
	}
	public String getAlleleType() {
		return alleleType;
	}
	public void setAlleleType(String alleleType) {
		this.alleleType = alleleType;
	}
	public String getAlleleDescription() {
		return alleleDescription;
	}
	public void setAlleleDescription(String alleleDescription) {
		this.alleleDescription = alleleDescription;
	}
	public String getGenbankFile() {
		return genbankFile;
	}
	public void setGenbankFile(String genbankFile) {
		this.genbankFile = genbankFile;
	}
	public String getAlleleImage() {
		return alleleImage;
	}
	public void setAlleleImage(String alleleImage) {
		this.alleleImage = alleleImage;
	}
	public String getAlleleSimpleImage() {
		return alleleSimpleImage;
	}
	public void setAlleleSimpleImage(String alleleSimpleImage) {
		this.alleleSimpleImage = alleleSimpleImage;
	}
	public String getDesignId() {
		return designId;
	}
	public void setDesignId(String designId) {
		this.designId = designId;
	}
	public String getCassette() {
		return cassette;
	}
	public void setCassette(String cassette) {
		this.cassette = cassette;
	}
	public String getMouseStatus() {
		return mouseStatus;
	}
	public void setMouseStatus(String mouseStatus) {
		this.mouseStatus = mouseStatus;
	}
	public String getEsCellStatus() {
		return esCellStatus;
	}
	public void setEsCellStatus(String esCellStatus) {
		this.esCellStatus = esCellStatus;
	}
	public String getProductionCentre() {
		return productionCentre;
	}
	public void setProductionCentre(String productionCentre) {
		this.productionCentre = productionCentre;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<String> getIkmcProject() {
		return ikmcProject;
	}
	public void setIkmcProject(List<String> ikmcProject) {
		this.ikmcProject = ikmcProject;
	}
	public List<String> getPipeline() {
		return pipeline;
	}
	public void setPipeline(List<String> pipeline) {
		this.pipeline = pipeline;
	}
	public List<String> getLinks() {
		return links;
	}
	public void setLinks(List<String> links) {
		this.links = links;
	}
	
	
	
}
