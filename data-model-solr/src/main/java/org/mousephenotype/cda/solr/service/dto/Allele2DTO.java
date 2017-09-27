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
	public static final String MARKER_SYNONYM = "marker_synonym";
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
	public static final String ALLELE_DESIGN_PROJECT="allele_design_project";
	public static final String ALLELE_SYMBOL_SEARCH_VARIANTS = "allele_symbol_search_variants";
	
	@Field(ALLELE_DESIGN_PROJECT)
	private String alleleDesignProject;
	@Field(MUTATION_TYPE)
	private String mutationType;
	@Field(ALLELE_CATEGORY)
	private String alleleCategory;
	@Field(ALLELE_FEATURES)
	private List<String> alleleFeatures;
	@Field(WITHOUT_ALLELE_FEATURES)
	private List<String> withoutAlleleFeatures;
	@Field(FEATURE_COORD_END)
	private Integer featureCoordEnd;
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
	private Integer featureCoordStart;
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

	@Field(ALLELE_SYMBOL_SEARCH_VARIANTS)
	private List<String> alleleSymbolSearchVariants;

	@Field(LATEST_PRODUCTION_CENTRE)
	private List<String> latestProductionCentre;

	@Field(LATEST_PHENOTYPING_CENTRE)
	private List<String> latestPhenotypingCentre;

	@Field(LATEST_PHENOTYPE_STATUS)
	private String latestPhenotypeStatus;

	@Field(LATEST_PROJECT_STATUS_LEGACY)
	private String latestProjectStatusLegacy;
	@Field(LATEST_PROJECT_STATUS)
	private String latestProjectStatus;

	@Field(PRODUCTION_CENTRES)
	private List<String> productionCentres;
	@Field(MARKER_TYPE)
	private String markerType;
	@Field(MGI_ACCESSION_ID)
	private String mgiAccessionId;
	@Field(MARKER_SYMBOL)
	private String markerSymbol;
	@Field(MARKER_NAME)
	private String markerName;

	@Field(MARKER_SYNONYM)
	private List<String> markerSynonym;

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

	@Field(ALLELE_SYMBOL)
	private String alleleSymbol;

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

	public String getAlleleDesignProject() {
		return alleleDesignProject;
	}

	public void setAlleleDesignProject(String alleleDesignProject) {
		this.alleleDesignProject = alleleDesignProject;
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

	public List<String> getAlleleSymbolSearchVariants() {
		return alleleSymbolSearchVariants;
	}

	public void setAlleleSymbolSearchVariants(List<String> alleleSymbolSearchVariants) {
		this.alleleSymbolSearchVariants = alleleSymbolSearchVariants;
	}

	public Integer getFeatureCoordEnd() {
		return featureCoordEnd;
	}

	public void setFeatureCoordEnd(Integer featureCoordEnd) {
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

	public Integer getFeatureCoordStart() {
		return featureCoordStart;
	}

	public void setFeatureCoordStart(Integer featureCoordStart) {
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

	public List<String> getLatestProductionCentre() {
		return latestProductionCentre;
	}

	public void setLatestProductionCentre(List<String> latestProductionCentre) {
		this.latestProductionCentre = latestProductionCentre;
	}

	public List<String> getLatestPhenotypingCentre() {
		return latestPhenotypingCentre;
	}

	public void setLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {
		this.latestPhenotypingCentre = latestPhenotypingCentre;
	}

	public String getLatestPhenotypeStatus() {
		return latestPhenotypeStatus;
	}

	public void setLatestPhenotypeStatus(String latestPhenotypeStatus) {
		this.latestPhenotypeStatus = latestPhenotypeStatus;
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

	public String getMgiAccessionId() {
		return mgiAccessionId;
	}

	public void setMgiAccessionId(String mgiAccessionId) {
		this.mgiAccessionId = mgiAccessionId;
	}

	public String getMarkerSymbol() {
		return markerSymbol;
	}

	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}

	public String getMarkerName() {
		return markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	public List<String> getMarkerSynonym() {
		return markerSynonym;
	}

	public void setMarkerSynonym(List<String> markerSynonym) {
		this.markerSynonym = markerSynonym;
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

	public String getAlleleSymbol() {
		return alleleSymbol;
	}

	public void setAlleleSymbol(String alleleSymbol) {
		this.alleleSymbol = alleleSymbol;
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

	public Boolean getTargetingVectorAvailable() {
		return targetingVectorAvailable;
	}

	public void setTargetingVectorAvailable(Boolean targetingVectorAvailable) {
		this.targetingVectorAvailable = targetingVectorAvailable;
	}

	public Boolean getEsCellAvailable() {
		return esCellAvailable;
	}

	public void setEsCellAvailable(Boolean esCellAvailable) {
		this.esCellAvailable = esCellAvailable;
	}

	public Boolean getMouseAvailable() {
		return mouseAvailable;
	}

	public void setMouseAvailable(Boolean mouseAvailable) {
		this.mouseAvailable = mouseAvailable;
	}

	public String getVectorAlleleImage() {
		return vectorAlleleImage;
	}

	public void setVectorAlleleImage(String vectorAlleleImage) {
		this.vectorAlleleImage = vectorAlleleImage;
	}

	public String getVectorGenbankLink() {
		return vectorGenbankLink;
	}

	public void setVectorGenbankLink(String vectorGenbankLink) {
		this.vectorGenbankLink = vectorGenbankLink;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Allele2DTO that = (Allele2DTO) o;

		if (alleleDesignProject != null ? !alleleDesignProject.equals(that.alleleDesignProject) : that.alleleDesignProject != null)
			return false;
		if (mutationType != null ? !mutationType.equals(that.mutationType) : that.mutationType != null) return false;
		if (alleleCategory != null ? !alleleCategory.equals(that.alleleCategory) : that.alleleCategory != null)
			return false;
		if (alleleFeatures != null ? !alleleFeatures.equals(that.alleleFeatures) : that.alleleFeatures != null)
			return false;
		if (withoutAlleleFeatures != null ? !withoutAlleleFeatures.equals(that.withoutAlleleFeatures) : that.withoutAlleleFeatures != null)
			return false;
		if (featureCoordEnd != null ? !featureCoordEnd.equals(that.featureCoordEnd) : that.featureCoordEnd != null)
			return false;
		if (phenotypeStatus != null ? !phenotypeStatus.equals(that.phenotypeStatus) : that.phenotypeStatus != null)
			return false;
		if (latestEsCellStatus != null ? !latestEsCellStatus.equals(that.latestEsCellStatus) : that.latestEsCellStatus != null)
			return false;
		if (latestMouseStatus != null ? !latestMouseStatus.equals(that.latestMouseStatus) : that.latestMouseStatus != null)
			return false;
		if (featureChromosome != null ? !featureChromosome.equals(that.featureChromosome) : that.featureChromosome != null)
			return false;
		if (featureStrand != null ? !featureStrand.equals(that.featureStrand) : that.featureStrand != null)
			return false;
		if (featureCoordStart != null ? !featureCoordStart.equals(that.featureCoordStart) : that.featureCoordStart != null)
			return false;
		if (autoSuggest != null ? !autoSuggest.equals(that.autoSuggest) : that.autoSuggest != null) return false;
		if (synonym != null ? !synonym.equals(that.synonym) : that.synonym != null) return false;
		if (featureType != null ? !featureType.equals(that.featureType) : that.featureType != null) return false;
		if (sequenceMapLinks != null ? !sequenceMapLinks.equals(that.sequenceMapLinks) : that.sequenceMapLinks != null)
			return false;
		if (geneModelIds != null ? !geneModelIds.equals(that.geneModelIds) : that.geneModelIds != null) return false;
		if (latestPhenotypeStarted != null ? !latestPhenotypeStarted.equals(that.latestPhenotypeStarted) : that.latestPhenotypeStarted != null)
			return false;
		if (latestPhenotypeComplete != null ? !latestPhenotypeComplete.equals(that.latestPhenotypeComplete) : that.latestPhenotypeComplete != null)
			return false;
		if (geneticMapLinks != null ? !geneticMapLinks.equals(that.geneticMapLinks) : that.geneticMapLinks != null)
			return false;
		if (phenotypingCentres != null ? !phenotypingCentres.equals(that.phenotypingCentres) : that.phenotypingCentres != null)
			return false;
		if (alleleSymbolSearchVariants != null ? !alleleSymbolSearchVariants.equals(that.alleleSymbolSearchVariants) : that.alleleSymbolSearchVariants != null)
			return false;
		if (latestProductionCentre != null ? !latestProductionCentre.equals(that.latestProductionCentre) : that.latestProductionCentre != null)
			return false;
		if (latestPhenotypingCentre != null ? !latestPhenotypingCentre.equals(that.latestPhenotypingCentre) : that.latestPhenotypingCentre != null)
			return false;
		if (latestPhenotypeStatus != null ? !latestPhenotypeStatus.equals(that.latestPhenotypeStatus) : that.latestPhenotypeStatus != null)
			return false;
		if (latestProjectStatusLegacy != null ? !latestProjectStatusLegacy.equals(that.latestProjectStatusLegacy) : that.latestProjectStatusLegacy != null)
			return false;
		if (latestProjectStatus != null ? !latestProjectStatus.equals(that.latestProjectStatus) : that.latestProjectStatus != null)
			return false;
		if (productionCentres != null ? !productionCentres.equals(that.productionCentres) : that.productionCentres != null)
			return false;
		if (markerType != null ? !markerType.equals(that.markerType) : that.markerType != null) return false;
		if (mgiAccessionId != null ? !mgiAccessionId.equals(that.mgiAccessionId) : that.mgiAccessionId != null)
			return false;
		if (markerSymbol != null ? !markerSymbol.equals(that.markerSymbol) : that.markerSymbol != null) return false;
		if (markerName != null ? !markerName.equals(that.markerName) : that.markerName != null) return false;
		if (markerSynonym != null ? !markerSynonym.equals(that.markerSynonym) : that.markerSynonym != null)
			return false;
		if (alleleMgiAccessionId != null ? !alleleMgiAccessionId.equals(that.alleleMgiAccessionId) : that.alleleMgiAccessionId != null)
			return false;
		if (alleleName != null ? !alleleName.equals(that.alleleName) : that.alleleName != null) return false;
		if (alleleType != null ? !alleleType.equals(that.alleleType) : that.alleleType != null) return false;
		if (alleleDescription != null ? !alleleDescription.equals(that.alleleDescription) : that.alleleDescription != null)
			return false;
		if (genbankFile != null ? !genbankFile.equals(that.genbankFile) : that.genbankFile != null) return false;
		if (alleleImage != null ? !alleleImage.equals(that.alleleImage) : that.alleleImage != null) return false;
		if (alleleSymbol != null ? !alleleSymbol.equals(that.alleleSymbol) : that.alleleSymbol != null) return false;
		if (alleleSimpleImage != null ? !alleleSimpleImage.equals(that.alleleSimpleImage) : that.alleleSimpleImage != null)
			return false;
		if (designId != null ? !designId.equals(that.designId) : that.designId != null) return false;
		if (cassette != null ? !cassette.equals(that.cassette) : that.cassette != null) return false;
		if (mouseStatus != null ? !mouseStatus.equals(that.mouseStatus) : that.mouseStatus != null) return false;
		if (esCellStatus != null ? !esCellStatus.equals(that.esCellStatus) : that.esCellStatus != null) return false;
		if (type != null ? !type.equals(that.type) : that.type != null) return false;
		if (ikmcProject != null ? !ikmcProject.equals(that.ikmcProject) : that.ikmcProject != null) return false;
		if (pipeline != null ? !pipeline.equals(that.pipeline) : that.pipeline != null) return false;
		if (links != null ? !links.equals(that.links) : that.links != null) return false;
		if (targetingVectorAvailable != null ? !targetingVectorAvailable.equals(that.targetingVectorAvailable) : that.targetingVectorAvailable != null)
			return false;
		if (esCellAvailable != null ? !esCellAvailable.equals(that.esCellAvailable) : that.esCellAvailable != null)
			return false;
		if (mouseAvailable != null ? !mouseAvailable.equals(that.mouseAvailable) : that.mouseAvailable != null)
			return false;
		if (vectorAlleleImage != null ? !vectorAlleleImage.equals(that.vectorAlleleImage) : that.vectorAlleleImage != null)
			return false;
		return vectorGenbankLink != null ? vectorGenbankLink.equals(that.vectorGenbankLink) : that.vectorGenbankLink == null;
	}

	@Override
	public int hashCode() {
		int result = alleleDesignProject != null ? alleleDesignProject.hashCode() : 0;
		result = 31 * result + (mutationType != null ? mutationType.hashCode() : 0);
		result = 31 * result + (alleleCategory != null ? alleleCategory.hashCode() : 0);
		result = 31 * result + (alleleFeatures != null ? alleleFeatures.hashCode() : 0);
		result = 31 * result + (withoutAlleleFeatures != null ? withoutAlleleFeatures.hashCode() : 0);
		result = 31 * result + (featureCoordEnd != null ? featureCoordEnd.hashCode() : 0);
		result = 31 * result + (phenotypeStatus != null ? phenotypeStatus.hashCode() : 0);
		result = 31 * result + (latestEsCellStatus != null ? latestEsCellStatus.hashCode() : 0);
		result = 31 * result + (latestMouseStatus != null ? latestMouseStatus.hashCode() : 0);
		result = 31 * result + (featureChromosome != null ? featureChromosome.hashCode() : 0);
		result = 31 * result + (featureStrand != null ? featureStrand.hashCode() : 0);
		result = 31 * result + (featureCoordStart != null ? featureCoordStart.hashCode() : 0);
		result = 31 * result + (autoSuggest != null ? autoSuggest.hashCode() : 0);
		result = 31 * result + (synonym != null ? synonym.hashCode() : 0);
		result = 31 * result + (featureType != null ? featureType.hashCode() : 0);
		result = 31 * result + (sequenceMapLinks != null ? sequenceMapLinks.hashCode() : 0);
		result = 31 * result + (geneModelIds != null ? geneModelIds.hashCode() : 0);
		result = 31 * result + (latestPhenotypeStarted != null ? latestPhenotypeStarted.hashCode() : 0);
		result = 31 * result + (latestPhenotypeComplete != null ? latestPhenotypeComplete.hashCode() : 0);
		result = 31 * result + (geneticMapLinks != null ? geneticMapLinks.hashCode() : 0);
		result = 31 * result + (phenotypingCentres != null ? phenotypingCentres.hashCode() : 0);
		result = 31 * result + (alleleSymbolSearchVariants != null ? alleleSymbolSearchVariants.hashCode() : 0);
		result = 31 * result + (latestProductionCentre != null ? latestProductionCentre.hashCode() : 0);
		result = 31 * result + (latestPhenotypingCentre != null ? latestPhenotypingCentre.hashCode() : 0);
		result = 31 * result + (latestPhenotypeStatus != null ? latestPhenotypeStatus.hashCode() : 0);
		result = 31 * result + (latestProjectStatusLegacy != null ? latestProjectStatusLegacy.hashCode() : 0);
		result = 31 * result + (latestProjectStatus != null ? latestProjectStatus.hashCode() : 0);
		result = 31 * result + (productionCentres != null ? productionCentres.hashCode() : 0);
		result = 31 * result + (markerType != null ? markerType.hashCode() : 0);
		result = 31 * result + (mgiAccessionId != null ? mgiAccessionId.hashCode() : 0);
		result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
		result = 31 * result + (markerName != null ? markerName.hashCode() : 0);
		result = 31 * result + (markerSynonym != null ? markerSynonym.hashCode() : 0);
		result = 31 * result + (alleleMgiAccessionId != null ? alleleMgiAccessionId.hashCode() : 0);
		result = 31 * result + (alleleName != null ? alleleName.hashCode() : 0);
		result = 31 * result + (alleleType != null ? alleleType.hashCode() : 0);
		result = 31 * result + (alleleDescription != null ? alleleDescription.hashCode() : 0);
		result = 31 * result + (genbankFile != null ? genbankFile.hashCode() : 0);
		result = 31 * result + (alleleImage != null ? alleleImage.hashCode() : 0);
		result = 31 * result + (alleleSymbol != null ? alleleSymbol.hashCode() : 0);
		result = 31 * result + (alleleSimpleImage != null ? alleleSimpleImage.hashCode() : 0);
		result = 31 * result + (designId != null ? designId.hashCode() : 0);
		result = 31 * result + (cassette != null ? cassette.hashCode() : 0);
		result = 31 * result + (mouseStatus != null ? mouseStatus.hashCode() : 0);
		result = 31 * result + (esCellStatus != null ? esCellStatus.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (ikmcProject != null ? ikmcProject.hashCode() : 0);
		result = 31 * result + (pipeline != null ? pipeline.hashCode() : 0);
		result = 31 * result + (links != null ? links.hashCode() : 0);
		result = 31 * result + (targetingVectorAvailable != null ? targetingVectorAvailable.hashCode() : 0);
		result = 31 * result + (esCellAvailable != null ? esCellAvailable.hashCode() : 0);
		result = 31 * result + (mouseAvailable != null ? mouseAvailable.hashCode() : 0);
		result = 31 * result + (vectorAlleleImage != null ? vectorAlleleImage.hashCode() : 0);
		result = 31 * result + (vectorGenbankLink != null ? vectorGenbankLink.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Allele2DTO{" +
				"alleleDesignProject='" + alleleDesignProject + '\'' +
				", mutationType='" + mutationType + '\'' +
				", alleleCategory='" + alleleCategory + '\'' +
				", alleleFeatures=" + alleleFeatures +
				", withoutAlleleFeatures=" + withoutAlleleFeatures +
				", featureCoordEnd=" + featureCoordEnd +
				", phenotypeStatus='" + phenotypeStatus + '\'' +
				", latestEsCellStatus='" + latestEsCellStatus + '\'' +
				", latestMouseStatus='" + latestMouseStatus + '\'' +
				", featureChromosome='" + featureChromosome + '\'' +
				", featureStrand='" + featureStrand + '\'' +
				", featureCoordStart=" + featureCoordStart +
				", autoSuggest=" + autoSuggest +
				", synonym=" + synonym +
				", featureType='" + featureType + '\'' +
				", sequenceMapLinks=" + sequenceMapLinks +
				", geneModelIds=" + geneModelIds +
				", latestPhenotypeStarted='" + latestPhenotypeStarted + '\'' +
				", latestPhenotypeComplete='" + latestPhenotypeComplete + '\'' +
				", geneticMapLinks=" + geneticMapLinks +
				", phenotypingCentres=" + phenotypingCentres +
				", alleleSymbolSearchVariants=" + alleleSymbolSearchVariants +
				", latestProductionCentre=" + latestProductionCentre +
				", latestPhenotypingCentre=" + latestPhenotypingCentre +
				", latestPhenotypeStatus='" + latestPhenotypeStatus + '\'' +
				", latestProjectStatusLegacy='" + latestProjectStatusLegacy + '\'' +
				", latestProjectStatus='" + latestProjectStatus + '\'' +
				", productionCentres=" + productionCentres +
				", markerType='" + markerType + '\'' +
				", mgiAccessionId='" + mgiAccessionId + '\'' +
				", markerSymbol='" + markerSymbol + '\'' +
				", markerName='" + markerName + '\'' +
				", markerSynonym=" + markerSynonym +
				", alleleMgiAccessionId='" + alleleMgiAccessionId + '\'' +
				", alleleName='" + alleleName + '\'' +
				", alleleType='" + alleleType + '\'' +
				", alleleDescription='" + alleleDescription + '\'' +
				", genbankFile='" + genbankFile + '\'' +
				", alleleImage='" + alleleImage + '\'' +
				", alleleSymbol='" + alleleSymbol + '\'' +
				", alleleSimpleImage='" + alleleSimpleImage + '\'' +
				", designId='" + designId + '\'' +
				", cassette='" + cassette + '\'' +
				", mouseStatus='" + mouseStatus + '\'' +
				", esCellStatus='" + esCellStatus + '\'' +
				", type='" + type + '\'' +
				", ikmcProject=" + ikmcProject +
				", pipeline=" + pipeline +
				", links=" + links +
				", targetingVectorAvailable=" + targetingVectorAvailable +
				", esCellAvailable=" + esCellAvailable +
				", mouseAvailable=" + mouseAvailable +
				", vectorAlleleImage='" + vectorAlleleImage + '\'' +
				", vectorGenbankLink='" + vectorGenbankLink + '\'' +
				'}';
	}
}
