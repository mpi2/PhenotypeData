package org.mousephenotype.cda.solr.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

/**
 * Created by jmason on 02/06/2016.
 */
public class PhenodigmDTO {

	public static final String ID = "id";
	public static final String TYPE = "type";
	public static final String DISEASE_ID = "disease_id";
	public static final String DISEASE_SOURCE = "disease_source";
	public static final String DISEASE_TERM = "disease_term";
	public static final String DISEASE_ALTS = "disease_alts";
	public static final String DISEASE_LOCUS = "disease_locus";
	public static final String DISEASE_CLASSES = "disease_classes";
	public static final String MARKER_ACCESSION = "marker_accession";
	public static final String MARKER_SYMBOL = "marker_symbol";
	public static final String HGNC_GENE_SYMBOL = "hgnc_gene_symbol";
	public static final String HGNC_GENE_ID = "hgnc_gene_id";
	public static final String HGNC_GENE_LOCUS = "hgnc_gene_locus";
	public static final String MODEL_ID = "model_id";
	public static final String SOURCE = "source";
	public static final String ALLELIC_COMPOSITION = "allelic_composition";
	public static final String GENETIC_BACKGROUND = "genetic_background";
	public static final String ALLELE_IDS = "allele_ids";
	public static final String HOM_HET = "hom_het";
	public static final String HUMAN_CURATED = "human_curated";
	public static final String MOUSE_CURATED = "mouse_curated";
	public static final String MGI_PREDICTED = "mgi_predicted";
	public static final String IMPC_PREDICTED = "impc_predicted";
	public static final String MGI_PREDICTED_KNOWN_GENE = "mgi_predicted_known_gene";
	public static final String IMPC_PREDICTED_KNOWN_GENE = "impc_predicted_known_gene";
	public static final String MGI_NOVEL_PREDICTED_IN_LOCUS = "mgi_novel_predicted_in_locus";
	public static final String IMPC_NOVEL_PREDICTED_IN_LOCUS = "impc_novel_predicted_in_locus";
	public static final String MOD_MODEL = "mod_model";
	public static final String HTPC_MODEL = "htpc_model";
	public static final String HTPC_PHENOTYPE = "htpc_phenotype";
	public static final String MAX_MGI_D2M_SCORE = "max_mgi_d2m_score";
	public static final String MAX_MGI_M2D_SCORE = "max_mgi_m2d_score";
	public static final String MAX_IMPC_D2M_SCORE = "max_impc_d2m_score";
	public static final String MAX_IMPC_M2D_SCORE = "max_impc_m2d_score";
	public static final String RAW_MOD_SCORE = "raw_mod_score";
	public static final String RAW_HTPC_SCORE = "raw_htpc_score";
	public static final String IN_LOCUS = "in_locus";
	public static final String LIT_MODEL = "lit_model";
	public static final String DISEASE_TO_MODEL_SCORE = "disease_to_model_score";
	public static final String MODEL_TO_DISEASE_SCORE = "model_to_disease_score";
	public static final String RAW_SCORE = "raw_score";
	public static final String HP_MATCHED_TERMS = "hp_matched_terms";
	public static final String MP_MATCHED_TERMS = "mp_matched_terms";
	public static final String MP_ID = "mp_id";
	public static final String MP_TERM = "mp_term";
	public static final String HP_ID = "hp_id";
	public static final String HP_TERM = "hp_term";
	public static final String HP_SYNONYM = "hp_synonym";
	public static final String PHENOTYPES = "phenotypes";
	public static final String TEXT = "text";

	@Field(ID)
	private String id;

	@Field(TYPE)
	private String type;

	@Field(DISEASE_ID)
	private String diseaseID;

	@Field(DISEASE_SOURCE)
	private String diseaseSource;

	@Field(DISEASE_TERM)
	private String diseaseTerm;

	@Field(DISEASE_ALTS)
	private List<String> diseaseAlts;

	@Field(DISEASE_LOCUS)
	private String diseaseLocus;

	@Field(DISEASE_CLASSES)
	private List<String> diseaseClasses;

	@Field(MARKER_ACCESSION)
	private String markerAccession;

	@Field(MARKER_SYMBOL)
	private String markerSymbol;

	@Field(HGNC_GENE_SYMBOL)
	private String hgncGeneSymbol;

	@Field(HGNC_GENE_ID)
	private String hgncGeneID;

	@Field(HGNC_GENE_LOCUS)
	private String hgncGeneLocus;

	@Field(MODEL_ID)
	private Integer modelID;

	@Field(SOURCE)
	private String source;

	@Field(ALLELIC_COMPOSITION)
	private String allelicComposition;

	@Field(GENETIC_BACKGROUND)
	private String geneticBackground;

	@Field(ALLELE_IDS)
	private String alleleIds;

	@Field(HOM_HET)
	private String homHet;

	@Field(HUMAN_CURATED)
	private Boolean humanCurated;

	@Field(MOUSE_CURATED)
	private Boolean mouseCurated;

	@Field(MGI_PREDICTED)
	private Boolean mgiPredicted;

	@Field(IMPC_PREDICTED)
	private Boolean impcPredicted;

	@Field(MGI_PREDICTED_KNOWN_GENE)
	private Boolean mgiPredictedKnownGene;

	@Field(IMPC_PREDICTED_KNOWN_GENE)
	private Boolean impcPredictedKnownGene;

	@Field(MGI_NOVEL_PREDICTED_IN_LOCUS)
	private Boolean mgiNovelPredictedInLocus;

	@Field(IMPC_NOVEL_PREDICTED_IN_LOCUS)
	private Boolean impcNovelPredictedInLocus;

	@Field(MOD_MODEL)
	private Boolean modModel;

	@Field(HTPC_MODEL)
	private Boolean htpcModel;

	@Field(HTPC_PHENOTYPE)
	private Boolean htpcPhenotype;

	@Field(MAX_MGI_D2M_SCORE)
	private Double maxMgiD2mScore;

	@Field(MAX_MGI_M2D_SCORE)
	private Double maxMgiM2dScore;

	@Field(MAX_IMPC_D2M_SCORE)
	private Double maxImpcD2mScore;

	@Field(MAX_IMPC_M2D_SCORE)
	private Double maxImpcM2dScore;

	@Field(RAW_MOD_SCORE)
	private Double rawModScore;

	@Field(RAW_HTPC_SCORE)
	private Double rawHtpcScore;

	@Field(IN_LOCUS)
	private Boolean inLocus;

	@Field(LIT_MODEL)
	private Boolean litModel;

	@Field(DISEASE_TO_MODEL_SCORE)
	private Double diseaseToModelScore;

	@Field(MODEL_TO_DISEASE_SCORE)
	private Double modelToDiseaseScore;

	@Field(RAW_SCORE)
	private Double rawScore;

	@Field(HP_MATCHED_TERMS)
	private List<String> hpMatchedTerms;

	@Field(MP_MATCHED_TERMS)
	private List<String> mpMatchedTerms;

	@Field(MP_ID)
	private String mpID;

	@Field(MP_TERM)
	private String mpTerm;

	@Field(HP_ID)
	private String hpID;

	@Field(HP_TERM)
	private String hpTerm;

	@Field(HP_SYNONYM)
	private List<String> hpSynonym;

	@Field(PHENOTYPES)
	private List<String> phenotypes;

	@Field(TEXT)
	private List<String> text;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDiseaseID() {
		return diseaseID;
	}

	public void setDiseaseID(String diseaseID) {
		this.diseaseID = diseaseID;
	}

	public String getDiseaseSource() {
		return diseaseSource;
	}

	public void setDiseaseSource(String diseaseSource) {
		this.diseaseSource = diseaseSource;
	}

	public String getDiseaseTerm() {
		return diseaseTerm;
	}

	public void setDiseaseTerm(String diseaseTerm) {
		this.diseaseTerm = diseaseTerm;
	}

	public List<String> getDiseaseAlts() {
		return diseaseAlts;
	}

	public void setDiseaseAlts(List<String> diseaseAlts) {
		this.diseaseAlts = diseaseAlts;
	}

	public String getDiseaseLocus() {
		return diseaseLocus;
	}

	public void setDiseaseLocus(String diseaseLocus) {
		this.diseaseLocus = diseaseLocus;
	}

	public List<String> getDiseaseClasses() {
		return diseaseClasses;
	}

	public void setDiseaseClasses(List<String> diseaseClasses) {
		this.diseaseClasses = diseaseClasses;
	}

	public String getMarkerAccession() {
		return markerAccession;
	}

	public void setMarkerAccession(String markerAccession) {
		this.markerAccession = markerAccession;
	}

	public String getMarkerSymbol() {
		return markerSymbol;
	}

	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}

	public String getHgncGeneSymbol() {
		return hgncGeneSymbol;
	}

	public void setHgncGeneSymbol(String hgncGeneSymbol) {
		this.hgncGeneSymbol = hgncGeneSymbol;
	}

	public String getHgncGeneID() {
		return hgncGeneID;
	}

	public void setHgncGeneID(String hgncGeneID) {
		this.hgncGeneID = hgncGeneID;
	}

	public String getHgncGeneLocus() {
		return hgncGeneLocus;
	}

	public void setHgncGeneLocus(String hgncGeneLocus) {
		this.hgncGeneLocus = hgncGeneLocus;
	}

	public Integer getModelID() {
		return modelID;
	}

	public void setModelID(Integer modelID) {
		this.modelID = modelID;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAllelicComposition() {
		return allelicComposition;
	}

	public void setAllelicComposition(String allelicComposition) {
		this.allelicComposition = allelicComposition;
	}

	public String getGeneticBackground() {
		return geneticBackground;
	}

	public void setGeneticBackground(String geneticBackground) {
		this.geneticBackground = geneticBackground;
	}

	public String getAlleleIds() {
		return alleleIds;
	}

	public void setAlleleIds(String alleleIds) {
		this.alleleIds = alleleIds;
	}

	public String getHomHet() {
		return homHet;
	}

	public void setHomHet(String homHet) {
		this.homHet = homHet;
	}

	public Boolean getHumanCurated() {
		return humanCurated;
	}

	public void setHumanCurated(Boolean humanCurated) {
		this.humanCurated = humanCurated;
	}

	public Boolean getMouseCurated() {
		return mouseCurated;
	}

	public void setMouseCurated(Boolean mouseCurated) {
		this.mouseCurated = mouseCurated;
	}

	public Boolean getMgiPredicted() {
		return mgiPredicted;
	}

	public void setMgiPredicted(Boolean mgiPredicted) {
		this.mgiPredicted = mgiPredicted;
	}

	public Boolean getImpcPredicted() {
		return impcPredicted;
	}

	public void setImpcPredicted(Boolean impcPredicted) {
		this.impcPredicted = impcPredicted;
	}

	public Boolean getMgiPredictedKnownGene() {
		return mgiPredictedKnownGene;
	}

	public void setMgiPredictedKnownGene(Boolean mgiPredictedKnownGene) {
		this.mgiPredictedKnownGene = mgiPredictedKnownGene;
	}

	public Boolean getImpcPredictedKnownGene() {
		return impcPredictedKnownGene;
	}

	public void setImpcPredictedKnownGene(Boolean impcPredictedKnownGene) {
		this.impcPredictedKnownGene = impcPredictedKnownGene;
	}

	public Boolean getMgiNovelPredictedInLocus() {
		return mgiNovelPredictedInLocus;
	}

	public void setMgiNovelPredictedInLocus(Boolean mgiNovelPredictedInLocus) {
		this.mgiNovelPredictedInLocus = mgiNovelPredictedInLocus;
	}

	public Boolean getImpcNovelPredictedInLocus() {
		return impcNovelPredictedInLocus;
	}

	public void setImpcNovelPredictedInLocus(Boolean impcNovelPredictedInLocus) {
		this.impcNovelPredictedInLocus = impcNovelPredictedInLocus;
	}

	public Boolean getModModel() {
		return modModel;
	}

	public void setModModel(Boolean modModel) {
		this.modModel = modModel;
	}

	public Boolean getHtpcModel() {
		return htpcModel;
	}

	public void setHtpcModel(Boolean htpcModel) {
		this.htpcModel = htpcModel;
	}

	public Boolean getHtpcPhenotype() {
		return htpcPhenotype;
	}

	public void setHtpcPhenotype(Boolean htpcPhenotype) {
		this.htpcPhenotype = htpcPhenotype;
	}

	public Double getMaxMgiD2mScore() {
		return maxMgiD2mScore;
	}

	public void setMaxMgiD2mScore(Double maxMgiD2mScore) {
		this.maxMgiD2mScore = maxMgiD2mScore;
	}

	public Double getMaxMgiM2dScore() {
		return maxMgiM2dScore;
	}

	public void setMaxMgiM2dScore(Double maxMgiM2dScore) {
		this.maxMgiM2dScore = maxMgiM2dScore;
	}

	public Double getMaxImpcD2mScore() {
		return maxImpcD2mScore;
	}

	public void setMaxImpcD2mScore(Double maxImpcD2mScore) {
		this.maxImpcD2mScore = maxImpcD2mScore;
	}

	public Double getMaxImpcM2dScore() {
		return maxImpcM2dScore;
	}

	public void setMaxImpcM2dScore(Double maxImpcM2dScore) {
		this.maxImpcM2dScore = maxImpcM2dScore;
	}

	public Double getRawModScore() {
		return rawModScore;
	}

	public void setRawModScore(Double rawModScore) {
		this.rawModScore = rawModScore;
	}

	public Double getRawHtpcScore() {
		return rawHtpcScore;
	}

	public void setRawHtpcScore(Double rawHtpcScore) {
		this.rawHtpcScore = rawHtpcScore;
	}

	public Boolean getInLocus() {
		return inLocus;
	}

	public void setInLocus(Boolean inLocus) {
		this.inLocus = inLocus;
	}

	public Boolean getLitModel() {
		return litModel;
	}

	public void setLitModel(Boolean litModel) {
		this.litModel = litModel;
	}

	public Double getDiseaseToModelScore() {
		return diseaseToModelScore;
	}

	public void setDiseaseToModelScore(Double diseaseToModelScore) {
		this.diseaseToModelScore = diseaseToModelScore;
	}

	public Double getModelToDiseaseScore() {
		return modelToDiseaseScore;
	}

	public void setModelToDiseaseScore(Double modelToDiseaseScore) {
		this.modelToDiseaseScore = modelToDiseaseScore;
	}

	public Double getRawScore() {
		return rawScore;
	}

	public void setRawScore(Double rawScore) {
		this.rawScore = rawScore;
	}

	public List<String> getHpMatchedTerms() {
		return hpMatchedTerms;
	}

	public void setHpMatchedTerms(List<String> hpMatchedTerms) {
		this.hpMatchedTerms = hpMatchedTerms;
	}

	public List<String> getMpMatchedTerms() {
		return mpMatchedTerms;
	}

	public void setMpMatchedTerms(List<String> mpMatchedTerms) {
		this.mpMatchedTerms = mpMatchedTerms;
	}

	public String getMpID() {
		return mpID;
	}

	public void setMpID(String mpID) {
		this.mpID = mpID;
	}

	public String getMpTerm() {
		return mpTerm;
	}

	public void setMpTerm(String mpTerm) {
		this.mpTerm = mpTerm;
	}

	public String getHpID() {
		return hpID;
	}

	public void setHpID(String hpID) {
		this.hpID = hpID;
	}

	public String getHpTerm() {
		return hpTerm;
	}

	public void setHpTerm(String hpTerm) {
		this.hpTerm = hpTerm;
	}

	public List<String> getHpSynonym() {
		return hpSynonym;
	}

	public void setHpSynonym(List<String> hpSynonym) {
		this.hpSynonym = hpSynonym;
	}

	public List<String> getPhenotypes() {
		return phenotypes;
	}

	public void setPhenotypes(List<String> phenotypes) {
		this.phenotypes = phenotypes;
	}

	public List<String> getText() {
		return text;
	}

	public void setText(List<String> text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		PhenodigmDTO that = (PhenodigmDTO) o;

		if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
		if (getType() != null ? !getType().equals(that.getType()) : that.getType() != null) return false;
		if (getDiseaseID() != null ? !getDiseaseID().equals(that.getDiseaseID()) : that.getDiseaseID() != null)
			return false;
		if (getDiseaseSource() != null ? !getDiseaseSource().equals(that.getDiseaseSource()) : that.getDiseaseSource() != null)
			return false;
		if (getDiseaseTerm() != null ? !getDiseaseTerm().equals(that.getDiseaseTerm()) : that.getDiseaseTerm() != null)
			return false;
		if (getDiseaseAlts() != null ? !getDiseaseAlts().equals(that.getDiseaseAlts()) : that.getDiseaseAlts() != null)
			return false;
		if (getDiseaseLocus() != null ? !getDiseaseLocus().equals(that.getDiseaseLocus()) : that.getDiseaseLocus() != null)
			return false;
		if (getDiseaseClasses() != null ? !getDiseaseClasses().equals(that.getDiseaseClasses()) : that.getDiseaseClasses() != null)
			return false;
		if (getMarkerAccession() != null ? !getMarkerAccession().equals(that.getMarkerAccession()) : that.getMarkerAccession() != null)
			return false;
		if (getMarkerSymbol() != null ? !getMarkerSymbol().equals(that.getMarkerSymbol()) : that.getMarkerSymbol() != null)
			return false;
		if (getHgncGeneSymbol() != null ? !getHgncGeneSymbol().equals(that.getHgncGeneSymbol()) : that.getHgncGeneSymbol() != null)
			return false;
		if (getHgncGeneID() != null ? !getHgncGeneID().equals(that.getHgncGeneID()) : that.getHgncGeneID() != null) return false;
		if (getHgncGeneLocus() != null ? !getHgncGeneLocus().equals(that.getHgncGeneLocus()) : that.getHgncGeneLocus() != null)
			return false;
		if (getModelID() != null ? !getModelID().equals(that.getModelID()) : that.getModelID() != null) return false;
		if (getSource() != null ? !getSource().equals(that.getSource()) : that.getSource() != null) return false;
		if (getAllelicComposition() != null ? !getAllelicComposition().equals(that.getAllelicComposition()) : that.getAllelicComposition() != null)
			return false;
		if (getGeneticBackground() != null ? !getGeneticBackground().equals(that.getGeneticBackground()) : that.getGeneticBackground() != null)
			return false;
		if (getAlleleIds() != null ? !getAlleleIds().equals(that.getAlleleIds()) : that.getAlleleIds() != null)
			return false;
		if (getHomHet() != null ? !getHomHet().equals(that.getHomHet()) : that.getHomHet() != null) return false;
		if (getHumanCurated() != null ? !getHumanCurated().equals(that.getHumanCurated()) : that.getHumanCurated() != null)
			return false;
		if (getMouseCurated() != null ? !getMouseCurated().equals(that.getMouseCurated()) : that.getMouseCurated() != null)
			return false;
		if (getMgiPredicted() != null ? !getMgiPredicted().equals(that.getMgiPredicted()) : that.getMgiPredicted() != null)
			return false;
		if (getImpcPredicted() != null ? !getImpcPredicted().equals(that.getImpcPredicted()) : that.getImpcPredicted() != null)
			return false;
		if (getMgiPredictedKnownGene() != null ? !getMgiPredictedKnownGene().equals(that.getMgiPredictedKnownGene()) : that.getMgiPredictedKnownGene() != null)
			return false;
		if (getImpcPredictedKnownGene() != null ? !getImpcPredictedKnownGene().equals(that.getImpcPredictedKnownGene()) : that.getImpcPredictedKnownGene() != null)
			return false;
		if (getMgiNovelPredictedInLocus() != null ? !getMgiNovelPredictedInLocus().equals(that.getMgiNovelPredictedInLocus()) : that.getMgiNovelPredictedInLocus() != null)
			return false;
		if (getImpcNovelPredictedInLocus() != null ? !getImpcNovelPredictedInLocus().equals(that.getImpcNovelPredictedInLocus()) : that.getImpcNovelPredictedInLocus() != null)
			return false;
		if (getModModel() != null ? !getModModel().equals(that.getModModel()) : that.getModModel() != null)
			return false;
		if (getHtpcModel() != null ? !getHtpcModel().equals(that.getHtpcModel()) : that.getHtpcModel() != null)
			return false;
		if (getHtpcPhenotype() != null ? !getHtpcPhenotype().equals(that.getHtpcPhenotype()) : that.getHtpcPhenotype() != null)
			return false;
		if (getMaxMgiD2mScore() != null ? !getMaxMgiD2mScore().equals(that.getMaxMgiD2mScore()) : that.getMaxMgiD2mScore() != null)
			return false;
		if (getMaxMgiM2dScore() != null ? !getMaxMgiM2dScore().equals(that.getMaxMgiM2dScore()) : that.getMaxMgiM2dScore() != null)
			return false;
		if (getMaxImpcD2mScore() != null ? !getMaxImpcD2mScore().equals(that.getMaxImpcD2mScore()) : that.getMaxImpcD2mScore() != null)
			return false;
		if (getMaxImpcM2dScore() != null ? !getMaxImpcM2dScore().equals(that.getMaxImpcM2dScore()) : that.getMaxImpcM2dScore() != null)
			return false;
		if (getRawModScore() != null ? !getRawModScore().equals(that.getRawModScore()) : that.getRawModScore() != null)
			return false;
		if (getRawHtpcScore() != null ? !getRawHtpcScore().equals(that.getRawHtpcScore()) : that.getRawHtpcScore() != null)
			return false;
		if (getInLocus() != null ? !getInLocus().equals(that.getInLocus()) : that.getInLocus() != null) return false;
		if (getLitModel() != null ? !getLitModel().equals(that.getLitModel()) : that.getLitModel() != null)
			return false;
		if (getDiseaseToModelScore() != null ? !getDiseaseToModelScore().equals(that.getDiseaseToModelScore()) : that.getDiseaseToModelScore() != null)
			return false;
		if (getModelToDiseaseScore() != null ? !getModelToDiseaseScore().equals(that.getModelToDiseaseScore()) : that.getModelToDiseaseScore() != null)
			return false;
		if (getRawScore() != null ? !getRawScore().equals(that.getRawScore()) : that.getRawScore() != null)
			return false;
		if (getHpMatchedTerms() != null ? !getHpMatchedTerms().equals(that.getHpMatchedTerms()) : that.getHpMatchedTerms() != null)
			return false;
		if (getMpMatchedTerms() != null ? !getMpMatchedTerms().equals(that.getMpMatchedTerms()) : that.getMpMatchedTerms() != null)
			return false;
		if (getMpID() != null ? !getMpID().equals(that.getMpID()) : that.getMpID() != null) return false;
		if (getMpTerm() != null ? !getMpTerm().equals(that.getMpTerm()) : that.getMpTerm() != null) return false;
		if (getHpID() != null ? !getHpID().equals(that.getHpID()) : that.getHpID() != null) return false;
		if (getHpTerm() != null ? !getHpTerm().equals(that.getHpTerm()) : that.getHpTerm() != null) return false;
		if (getHpSynonym() != null ? !getHpSynonym().equals(that.getHpSynonym()) : that.getHpSynonym() != null)
			return false;
		if (getPhenotypes() != null ? !getPhenotypes().equals(that.getPhenotypes()) : that.getPhenotypes() != null)
			return false;
		return getText() != null ? getText().equals(that.getText()) : that.getText() == null;

	}

	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getType() != null ? getType().hashCode() : 0);
		result = 31 * result + (getDiseaseID() != null ? getDiseaseID().hashCode() : 0);
		result = 31 * result + (getDiseaseSource() != null ? getDiseaseSource().hashCode() : 0);
		result = 31 * result + (getDiseaseTerm() != null ? getDiseaseTerm().hashCode() : 0);
		result = 31 * result + (getDiseaseAlts() != null ? getDiseaseAlts().hashCode() : 0);
		result = 31 * result + (getDiseaseLocus() != null ? getDiseaseLocus().hashCode() : 0);
		result = 31 * result + (getDiseaseClasses() != null ? getDiseaseClasses().hashCode() : 0);
		result = 31 * result + (getMarkerAccession() != null ? getMarkerAccession().hashCode() : 0);
		result = 31 * result + (getMarkerSymbol() != null ? getMarkerSymbol().hashCode() : 0);
		result = 31 * result + (getHgncGeneSymbol() != null ? getHgncGeneSymbol().hashCode() : 0);
		result = 31 * result + (getHgncGeneID() != null ? getHgncGeneID().hashCode() : 0);
		result = 31 * result + (getHgncGeneLocus() != null ? getHgncGeneLocus().hashCode() : 0);
		result = 31 * result + (getModelID() != null ? getModelID().hashCode() : 0);
		result = 31 * result + (getSource() != null ? getSource().hashCode() : 0);
		result = 31 * result + (getAllelicComposition() != null ? getAllelicComposition().hashCode() : 0);
		result = 31 * result + (getGeneticBackground() != null ? getGeneticBackground().hashCode() : 0);
		result = 31 * result + (getAlleleIds() != null ? getAlleleIds().hashCode() : 0);
		result = 31 * result + (getHomHet() != null ? getHomHet().hashCode() : 0);
		result = 31 * result + (getHumanCurated() != null ? getHumanCurated().hashCode() : 0);
		result = 31 * result + (getMouseCurated() != null ? getMouseCurated().hashCode() : 0);
		result = 31 * result + (getMgiPredicted() != null ? getMgiPredicted().hashCode() : 0);
		result = 31 * result + (getImpcPredicted() != null ? getImpcPredicted().hashCode() : 0);
		result = 31 * result + (getMgiPredictedKnownGene() != null ? getMgiPredictedKnownGene().hashCode() : 0);
		result = 31 * result + (getImpcPredictedKnownGene() != null ? getImpcPredictedKnownGene().hashCode() : 0);
		result = 31 * result + (getMgiNovelPredictedInLocus() != null ? getMgiNovelPredictedInLocus().hashCode() : 0);
		result = 31 * result + (getImpcNovelPredictedInLocus() != null ? getImpcNovelPredictedInLocus().hashCode() : 0);
		result = 31 * result + (getModModel() != null ? getModModel().hashCode() : 0);
		result = 31 * result + (getHtpcModel() != null ? getHtpcModel().hashCode() : 0);
		result = 31 * result + (getHtpcPhenotype() != null ? getHtpcPhenotype().hashCode() : 0);
		result = 31 * result + (getMaxMgiD2mScore() != null ? getMaxMgiD2mScore().hashCode() : 0);
		result = 31 * result + (getMaxMgiM2dScore() != null ? getMaxMgiM2dScore().hashCode() : 0);
		result = 31 * result + (getMaxImpcD2mScore() != null ? getMaxImpcD2mScore().hashCode() : 0);
		result = 31 * result + (getMaxImpcM2dScore() != null ? getMaxImpcM2dScore().hashCode() : 0);
		result = 31 * result + (getRawModScore() != null ? getRawModScore().hashCode() : 0);
		result = 31 * result + (getRawHtpcScore() != null ? getRawHtpcScore().hashCode() : 0);
		result = 31 * result + (getInLocus() != null ? getInLocus().hashCode() : 0);
		result = 31 * result + (getLitModel() != null ? getLitModel().hashCode() : 0);
		result = 31 * result + (getDiseaseToModelScore() != null ? getDiseaseToModelScore().hashCode() : 0);
		result = 31 * result + (getModelToDiseaseScore() != null ? getModelToDiseaseScore().hashCode() : 0);
		result = 31 * result + (getRawScore() != null ? getRawScore().hashCode() : 0);
		result = 31 * result + (getHpMatchedTerms() != null ? getHpMatchedTerms().hashCode() : 0);
		result = 31 * result + (getMpMatchedTerms() != null ? getMpMatchedTerms().hashCode() : 0);
		result = 31 * result + (getMpID() != null ? getMpID().hashCode() : 0);
		result = 31 * result + (getMpTerm() != null ? getMpTerm().hashCode() : 0);
		result = 31 * result + (getHpID() != null ? getHpID().hashCode() : 0);
		result = 31 * result + (getHpTerm() != null ? getHpTerm().hashCode() : 0);
		result = 31 * result + (getHpSynonym() != null ? getHpSynonym().hashCode() : 0);
		result = 31 * result + (getPhenotypes() != null ? getPhenotypes().hashCode() : 0);
		result = 31 * result + (getText() != null ? getText().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "PhenodigmDTO{" +
			"id='" + id + '\'' +
			", type='" + type + '\'' +
			", diseaseID='" + diseaseID + '\'' +
			", diseaseSource='" + diseaseSource + '\'' +
			", diseaseTerm='" + diseaseTerm + '\'' +
			", diseaseAlts=" + diseaseAlts +
			", diseaseLocus='" + diseaseLocus + '\'' +
			", diseaseClasses=" + diseaseClasses +
			", markerAccession='" + markerAccession + '\'' +
			", markerSymbol='" + markerSymbol + '\'' +
			", hgncGeneSymbol='" + hgncGeneSymbol + '\'' +
			", hgncGeneID='" + hgncGeneID + '\'' +
			", hgncGeneLocus='" + hgncGeneLocus + '\'' +
			", modelID=" + modelID +
			", source='" + source + '\'' +
			", allelicComposition='" + allelicComposition + '\'' +
			", geneticBackground='" + geneticBackground + '\'' +
			", alleleIds='" + alleleIds + '\'' +
			", homHet='" + homHet + '\'' +
			", humanCurated=" + humanCurated +
			", mouseCurated=" + mouseCurated +
			", mgiPredicted=" + mgiPredicted +
			", impcPredicted=" + impcPredicted +
			", mgiPredictedKnownGene=" + mgiPredictedKnownGene +
			", impcPredictedKnownGene=" + impcPredictedKnownGene +
			", mgiNovelPredictedInLocus=" + mgiNovelPredictedInLocus +
			", impcNovelPredictedInLocus=" + impcNovelPredictedInLocus +
			", modModel=" + modModel +
			", htpcModel=" + htpcModel +
			", htpcPhenotype=" + htpcPhenotype +
			", maxMgiD2mScore=" + maxMgiD2mScore +
			", maxMgiM2dScore=" + maxMgiM2dScore +
			", maxImpcD2mScore=" + maxImpcD2mScore +
			", maxImpcM2dScore=" + maxImpcM2dScore +
			", rawModScore=" + rawModScore +
			", rawHtpcScore=" + rawHtpcScore +
			", inLocus=" + inLocus +
			", litModel=" + litModel +
			", diseaseToModelScore=" + diseaseToModelScore +
			", modelToDiseaseScore=" + modelToDiseaseScore +
			", rawScore=" + rawScore +
			", hpMatchedTerms=" + hpMatchedTerms +
			", mpMatchedTerms=" + mpMatchedTerms +
			", mpID='" + mpID + '\'' +
			", mpTerm='" + mpTerm + '\'' +
			", hpID='" + hpID + '\'' +
			", hpTerm='" + hpTerm + '\'' +
			", hpSynonym=" + hpSynonym +
			", phenotypes=" + phenotypes +
			", text=" + text +
			'}';
	}
}
