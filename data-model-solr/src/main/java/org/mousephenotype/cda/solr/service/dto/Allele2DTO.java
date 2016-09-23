package org.mousephenotype.cda.solr.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

public class Allele2DTO {

	public static final String MGI_ACCESSION_ID="mgi_accession_id";
	public static final String MARKER_SYMBOL="marker_symbol";
	public static final String ALLELE_NAME="allele_name";
	public static final String ALLELE_TYPE="allele_type";
	public static final String ALLELE_DESCRIPTION="allele_description";
	public static final String GENBANK_FILE="genbank_file";
	public static final String ALLELE_IMAGE="allele_image";
	public static final String ALLELE_SIMPLE_IMAGE="allele_simple_image";
	public static final String DESIGN_ID="design_id";
	public static final String CASSETTE="cassette";
	public static final String MOUSE_STATUS="mouse_status";
	public static final String ES_CELL_STATUS="es_cell_status";
	public static final String PRODUCTION_CENTRE="production_centre";
	public static final String TYPE="type";
	public static final String IKMC_PROJECT="ikmc_project";//8856
	public static final String PIPELINE="pipeline";
	public static final String LINKS="links";
	public static final String TARGETING_VECTOR_AVAILABLE="targeting_vector_available";
	public static final String VECTOR_ALLELE_IMAGE="vector_allele_image";
	public static final String ES_CELL_AVAILABLE="es_cell_available";
	public static final String MOUSE_AVAILABLE="mouse_available";
	public static final String VECTOR_GENBANK_LINK="vector_genbank_file";
	public static final String MARKER_TYPE = "marker_type";
	public static final String ALLELE_MGI_ACCESSION_ID = "allele_mgi_accession_id";
	public static final String PHENOTYPING_CENTRE = "phenotyping_centre";
	public static final String PRODUCTION_CENTRES = "production_centres";
	public static final String PHENOTYPING_CENTRES = "phenotyping_centres";
	public static final String LATEST_PROJECT_STATUS_LEGACY = "latest_project_status_legacy";
	public static final String LATEST_PROJECT_STATUS = "latest_project_status";
	public static final String LATEST_PRODUCTION_CENTRE = "latest_production_centre";
	public static final String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";
	public static final String LATEST_PHENOTYPE_STARTED = "latest_phenotype_started";
	public static final String LATEST_PHENOTYPE_COMPLETE = "latest_phenotype_complete";
	public static final String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
	public static final String GENETIC_MAP_LINKS = "genetic_map_links";
	public static final String SEQUENCE_MAP_LINKS = "sequence_map_links";
	public static final String GENE_MODEL_IDS = "gene_model_ids";
	public static final String NOTE = "notes";
	public static final String ALLELE_SYMBOL = "allele_symbol";
	public static final String AUTO_SUGGEST = "auto_suggest";
	public static final String MARKER_NAME = "marker_name";
	public static final String SYNONYM = "synonym";
	public static final String FEATURE_TYPE = "feature_type";
	public static final String FEATURE_CHROMOSOME = "feature_chromosome";
	public static final String FEATURE_STRAND = "feature_strand";
	public static final String FEAURE_COORD_START = "feature_coord_start";
	public static final String FEATURE_COORD_END = "feature_coord_end";
	public static final String PHENOTYPE_STATUS = "phenotype_status";
	public static final String LATEST_ES_CELL_STATUS = "latest_es_cell_status";
	public static final String LATEST_MOUSE_STATUS = "latest_mouse_status";
	public static final String MUTATION_TYPE = "mutation_type";
	public static final String ALLELE_CATEGORY = "allele_category";
	public static final String ALLELE_FEATURES = "allele_features";
	public static final String WITHOUT_ALLELE_FEATURES = "without_allele_features";



	@Field(MUTATION_TYPE)
	private String mutationType;
	@Field(ALLELE_CATEGORY)
	private String alleleCategory;
	@Field(ALLELE_FEATURES)
	private List<String> alleleFeatures;
	@Field(WITHOUT_ALLELE_FEATURES)
	private List<String> withoutAlleleFeatures;
	@Field(FEATURE_COORD_END)
	private Long featureCoordEnd;
	@Field(PHENOTYPE_STATUS)
	private String phenotypeStatus;
	@Field(LATEST_ES_CELL_STATUS)
	private String latestEsCellStatus;
	@Field(LATEST_MOUSE_STATUS)
	private String latestMouseStatus;
	@Field(FEATURE_CHROMOSOME)
	private String featureChromosome;
	@Field(FEATURE_STRAND)
	private String featureStrand;
	@Field(FEAURE_COORD_START)
	private Long featureCoordStart;
	@Field(AUTO_SUGGEST)
	private List<String> autoSuggest;
	@Field(SYNONYM)
	private List<String> synonym;
	@Field(FEATURE_TYPE)
	private String featureType;
	@Field(SEQUENCE_MAP_LINKS)
	private List<String> sequenceMapLinks;
	@Field(GENE_MODEL_IDS)
	private List<String> geneModelIds;

	@Field(LATEST_PHENOTYPE_STARTED)
	private String latestPhenotypeStarted;

	@Field(LATEST_PHENOTYPE_COMPLETE)
	private String latestPhenotypeComplete;

	@Field(GENETIC_MAP_LINKS)
	private List<String> geneticMapLinks;

	@Field(PHENOTYPING_CENTRES)
	private List<String> phenotypingCentres;

	@Field(LATEST_PROJECT_STATUS_LEGACY)
	private String latestProjectStatusLegacy;

	@Field(LATEST_PROJECT_STATUS)
	private String latestProjectStatus;

	@Field(PHENOTYPING_CENTRE)
	private String phenotypingCentre;

	@Field(PRODUCTION_CENTRES)
	private List<String> productionCentres;

	@Field(MARKER_TYPE)
	private String markerType;

	@Field(MGI_ACCESSION_ID)
	private String mgiAccessionId;

	@Field(MARKER_SYMBOL)
	private String markerSymbol;

	@Field(ALLELE_MGI_ACCESSION_ID)
	private String alleleMgiAccessionId;

	@Field(ALLELE_NAME)
	private String alleleName;

	@Field(ALLELE_TYPE)
	private String alleleType;

	@Field(ALLELE_DESCRIPTION)
	private String alleleDescription;

	@Field(GENBANK_FILE)
	private String genbankFile;

	@Field(ALLELE_IMAGE)
	private String alleleImage;

	@Field(ALLELE_SIMPLE_IMAGE)
	private String alleleSimpleImage;

	@Field(DESIGN_ID)
	private String designId;

	@Field(CASSETTE)
	private String cassette;

	@Field(MOUSE_STATUS)
	private String mouseStatus;

	@Field(ES_CELL_STATUS)
	private String esCellStatus;

	@Field(PRODUCTION_CENTRE)
	private String productionCentre;

	@Field(TYPE)
	private String type;

	@Field(IKMC_PROJECT)
	private List<String> ikmcProject;

	@Field(PIPELINE)
	private List<String> pipeline;

	@Field(LINKS)
	private List<String> links;

	@Field(TARGETING_VECTOR_AVAILABLE)
	private Boolean targetingVectorAvailable;

	@Field(ES_CELL_AVAILABLE)
	private Boolean esCellAvailable;
	

	@Field(MOUSE_AVAILABLE)
	private Boolean mouseAvailable;
	

	@Field(VECTOR_ALLELE_IMAGE)
	private String vectorAlleleImage;
	

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

	public String getMutationType() {
		return mutationType;
	}

	public void setMutationType(String mutationType) {
		this.mutationType = mutationType;
	}

	public String getAlleleCategory() {
		return alleleCategory;
	}

	public void setAlleleCategory(String alleleCategory) {
		this.alleleCategory = alleleCategory;
	}

	public List<String> getAlleleFeatures() {
		return alleleFeatures;
	}

	public void setAlleleFeatures(List<String> alleleFeatures) {
		this.alleleFeatures = alleleFeatures;
	}

	public List<String> getWithoutAlleleFeatures() {
		return withoutAlleleFeatures;
	}

	public void setWithoutAlleleFeatures(List<String> withoutAlleleFeatures) {
		this.withoutAlleleFeatures = withoutAlleleFeatures;
	}

	public Long getFeatureCoordEnd() {
		return featureCoordEnd;
	}

	public void setFeatureCoordEnd(Long featureCoordEnd) {
		this.featureCoordEnd = featureCoordEnd;
	}

	public String getPhenotypeStatus() {
		return phenotypeStatus;
	}

	public void setPhenotypeStatus(String phenotypeStatus) {
		this.phenotypeStatus = phenotypeStatus;
	}

	public String getLatestEsCellStatus() {
		return latestEsCellStatus;
	}

	public void setLatestEsCellStatus(String latestEsCellStatus) {
		this.latestEsCellStatus = latestEsCellStatus;
	}

	public String getLatestMouseStatus() {
		return latestMouseStatus;
	}

	public void setLatestMouseStatus(String latestMouseStatus) {
		this.latestMouseStatus = latestMouseStatus;
	}

	public String getFeatureChromosome() {
		return featureChromosome;
	}

	public void setFeatureChromosome(String featureChromosome) {
		this.featureChromosome = featureChromosome;
	}

	public String getFeatureStrand() {
		return featureStrand;
	}

	public void setFeatureStrand(String featureStrand) {
		this.featureStrand = featureStrand;
	}

	public Long getFeatureCoordStart() {
		return featureCoordStart;
	}

	public void setFeatureCoordStart(Long featureCoordStart) {
		this.featureCoordStart = featureCoordStart;
	}

	public List<String> getAutoSuggest() {
		return autoSuggest;
	}

	public void setAutoSuggest(List<String> autoSuggest) {
		this.autoSuggest = autoSuggest;
	}

	public List<String> getSynonym() {
		return synonym;
	}

	public void setSynonym(List<String> synonym) {
		this.synonym = synonym;
	}

	public String getFeatureType() {
		return featureType;
	}

	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}

	public List<String> getSequenceMapLinks() {
		return sequenceMapLinks;
	}

	public void setSequenceMapLinks(List<String> sequenceMapLinks) {
		this.sequenceMapLinks = sequenceMapLinks;
	}

	public List<String> getGeneModelIds() {
		return geneModelIds;
	}

	public void setGeneModelIds(List<String> geneModelIds) {
		this.geneModelIds = geneModelIds;
	}

	public String getLatestPhenotypeStarted() {
		return latestPhenotypeStarted;
	}

	public void setLatestPhenotypeStarted(String latestPhenotypeStarted) {
		this.latestPhenotypeStarted = latestPhenotypeStarted;
	}

	public String getLatestPhenotypeComplete() {
		return latestPhenotypeComplete;
	}

	public void setLatestPhenotypeComplete(String latestPhenotypeComplete) {
		this.latestPhenotypeComplete = latestPhenotypeComplete;
	}

	public List<String> getGeneticMapLinks() {
		return geneticMapLinks;
	}

	public void setGeneticMapLinks(List<String> geneticMapLinks) {
		this.geneticMapLinks = geneticMapLinks;
	}

	public List<String> getPhenotypingCentres() {
		return phenotypingCentres;
	}

	public void setPhenotypingCentres(List<String> phenotypingCentres) {
		this.phenotypingCentres = phenotypingCentres;
	}

	public String getLatestProjectStatusLegacy() {
		return latestProjectStatusLegacy;
	}

	public void setLatestProjectStatusLegacy(String latestProjectStatusLegacy) {
		this.latestProjectStatusLegacy = latestProjectStatusLegacy;
	}

	public String getLatestProjectStatus() {
		return latestProjectStatus;
	}

	public void setLatestProjectStatus(String latestProjectStatus) {
		this.latestProjectStatus = latestProjectStatus;
	}

	public String getPhenotypingCentre() {
		return phenotypingCentre;
	}

	public void setPhenotypingCentre(String phenotypingCentre) {
		this.phenotypingCentre = phenotypingCentre;
	}

	public List<String> getProductionCentres() {
		return productionCentres;
	}

	public void setProductionCentres(List<String> productionCentres) {
		this.productionCentres = productionCentres;
	}

	public String getMarkerType() {
		return markerType;
	}

	public void setMarkerType(String markerType) {
		this.markerType = markerType;
	}
}
