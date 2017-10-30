package org.mousephenotype.cda.solr.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

/**
 * DTO to facilitate dommunication with the phenodigm solr core
 * @author jmason
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
	public static final String MARKER_ACCESSION = "gene_id";
	public static final String MARKER_SYMBOL = "gene_symbol";
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
	public static final String MP_MATCHED_IDS = "mp_matched_ids";
	public static final String MP_MATCHED_TERMS = "mp_matched_terms";
	public static final String TOP_LEVEL_MP_MATCHED_IDS = "top_level_mp_matched_ids";
	public static final String TOP_LEVEL_MP_MATCHED_TERMS = "top_level_mp_matched_terms";
	public static final String INTERMEDIATE_MP_MATCHED_IDS = "intermediate_mp_matched_ids";
	public static final String INTERMEDIATE_MP_MATCHED_TERMS = "intermediate_mp_matched_terms";
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

	@Field(MP_MATCHED_IDS)
	private List<String> mpMatchedIds;

	@Field(MP_MATCHED_TERMS)
	private List<String> mpMatchedTerms;

	@Field(TOP_LEVEL_MP_MATCHED_IDS)
	private List<String> topLevelMpMatchedIds;

	@Field(TOP_LEVEL_MP_MATCHED_TERMS)
	private List<String> topLevelMpMatchedTerms;

	@Field(INTERMEDIATE_MP_MATCHED_IDS)
	private List<String> intermediateMpMatchedIds;

	@Field(INTERMEDIATE_MP_MATCHED_TERMS)
	private List<String> intermediateMpMatchedTerms;

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

	public List<String> getMpMatchedIds() {
		return mpMatchedIds;
	}

	public void setMpMatchedIds(List<String> mpMatchedIds) {
		this.mpMatchedIds = mpMatchedIds;
	}

	public List<String> getMpMatchedTerms() {
		return mpMatchedTerms;
	}

	public void setMpMatchedTerms(List<String> mpMatchedTerms) {
		this.mpMatchedTerms = mpMatchedTerms;
	}

	public List<String> getTopLevelMpMatchedIds() {
		return topLevelMpMatchedIds;
	}

	public void setTopLevelMpMatchedIds(List<String> topLevelMpMatchedIds) {
		this.topLevelMpMatchedIds = topLevelMpMatchedIds;
	}

	public List<String> getTopLevelMpMatchedTerms() {
		return topLevelMpMatchedTerms;
	}

	public void setTopLevelMpMatchedTerms(List<String> topLevelMpMatchedTerms) {
		this.topLevelMpMatchedTerms = topLevelMpMatchedTerms;
	}

	public List<String> getIntermediateMpMatchedIds() {
		return intermediateMpMatchedIds;
	}

	public void setIntermediateMpMatchedIds(List<String> intermediateMpMatchedIds) {
		this.intermediateMpMatchedIds = intermediateMpMatchedIds;
	}

	public List<String> getIntermediateMpMatchedTerms() {
		return intermediateMpMatchedTerms;
	}

	public void setIntermediateMpMatchedTerms(List<String> intermediateMpMatchedTerms) {
		this.intermediateMpMatchedTerms = intermediateMpMatchedTerms;
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

		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (type != null ? !type.equals(that.type) : that.type != null) return false;
		if (diseaseID != null ? !diseaseID.equals(that.diseaseID) : that.diseaseID != null) return false;
		if (diseaseSource != null ? !diseaseSource.equals(that.diseaseSource) : that.diseaseSource != null)
			return false;
		if (diseaseTerm != null ? !diseaseTerm.equals(that.diseaseTerm) : that.diseaseTerm != null) return false;
		if (diseaseAlts != null ? !diseaseAlts.equals(that.diseaseAlts) : that.diseaseAlts != null) return false;
		if (diseaseLocus != null ? !diseaseLocus.equals(that.diseaseLocus) : that.diseaseLocus != null) return false;
		if (diseaseClasses != null ? !diseaseClasses.equals(that.diseaseClasses) : that.diseaseClasses != null)
			return false;
		if (markerAccession != null ? !markerAccession.equals(that.markerAccession) : that.markerAccession != null)
			return false;
		if (markerSymbol != null ? !markerSymbol.equals(that.markerSymbol) : that.markerSymbol != null) return false;
		if (hgncGeneSymbol != null ? !hgncGeneSymbol.equals(that.hgncGeneSymbol) : that.hgncGeneSymbol != null)
			return false;
		if (hgncGeneID != null ? !hgncGeneID.equals(that.hgncGeneID) : that.hgncGeneID != null) return false;
		if (hgncGeneLocus != null ? !hgncGeneLocus.equals(that.hgncGeneLocus) : that.hgncGeneLocus != null)
			return false;
		if (modelID != null ? !modelID.equals(that.modelID) : that.modelID != null) return false;
		if (source != null ? !source.equals(that.source) : that.source != null) return false;
		if (allelicComposition != null ? !allelicComposition.equals(that.allelicComposition) : that.allelicComposition != null)
			return false;
		if (geneticBackground != null ? !geneticBackground.equals(that.geneticBackground) : that.geneticBackground != null)
			return false;
		if (alleleIds != null ? !alleleIds.equals(that.alleleIds) : that.alleleIds != null) return false;
		if (homHet != null ? !homHet.equals(that.homHet) : that.homHet != null) return false;
		if (humanCurated != null ? !humanCurated.equals(that.humanCurated) : that.humanCurated != null) return false;
		if (mouseCurated != null ? !mouseCurated.equals(that.mouseCurated) : that.mouseCurated != null) return false;
		if (mgiPredicted != null ? !mgiPredicted.equals(that.mgiPredicted) : that.mgiPredicted != null) return false;
		if (impcPredicted != null ? !impcPredicted.equals(that.impcPredicted) : that.impcPredicted != null)
			return false;
		if (mgiPredictedKnownGene != null ? !mgiPredictedKnownGene.equals(that.mgiPredictedKnownGene) : that.mgiPredictedKnownGene != null)
			return false;
		if (impcPredictedKnownGene != null ? !impcPredictedKnownGene.equals(that.impcPredictedKnownGene) : that.impcPredictedKnownGene != null)
			return false;
		if (mgiNovelPredictedInLocus != null ? !mgiNovelPredictedInLocus.equals(that.mgiNovelPredictedInLocus) : that.mgiNovelPredictedInLocus != null)
			return false;
		if (impcNovelPredictedInLocus != null ? !impcNovelPredictedInLocus.equals(that.impcNovelPredictedInLocus) : that.impcNovelPredictedInLocus != null)
			return false;
		if (modModel != null ? !modModel.equals(that.modModel) : that.modModel != null) return false;
		if (htpcModel != null ? !htpcModel.equals(that.htpcModel) : that.htpcModel != null) return false;
		if (htpcPhenotype != null ? !htpcPhenotype.equals(that.htpcPhenotype) : that.htpcPhenotype != null)
			return false;
		if (maxMgiD2mScore != null ? !maxMgiD2mScore.equals(that.maxMgiD2mScore) : that.maxMgiD2mScore != null)
			return false;
		if (maxMgiM2dScore != null ? !maxMgiM2dScore.equals(that.maxMgiM2dScore) : that.maxMgiM2dScore != null)
			return false;
		if (maxImpcD2mScore != null ? !maxImpcD2mScore.equals(that.maxImpcD2mScore) : that.maxImpcD2mScore != null)
			return false;
		if (maxImpcM2dScore != null ? !maxImpcM2dScore.equals(that.maxImpcM2dScore) : that.maxImpcM2dScore != null)
			return false;
		if (rawModScore != null ? !rawModScore.equals(that.rawModScore) : that.rawModScore != null) return false;
		if (rawHtpcScore != null ? !rawHtpcScore.equals(that.rawHtpcScore) : that.rawHtpcScore != null) return false;
		if (inLocus != null ? !inLocus.equals(that.inLocus) : that.inLocus != null) return false;
		if (litModel != null ? !litModel.equals(that.litModel) : that.litModel != null) return false;
		if (diseaseToModelScore != null ? !diseaseToModelScore.equals(that.diseaseToModelScore) : that.diseaseToModelScore != null)
			return false;
		if (modelToDiseaseScore != null ? !modelToDiseaseScore.equals(that.modelToDiseaseScore) : that.modelToDiseaseScore != null)
			return false;
		if (rawScore != null ? !rawScore.equals(that.rawScore) : that.rawScore != null) return false;
		if (hpMatchedTerms != null ? !hpMatchedTerms.equals(that.hpMatchedTerms) : that.hpMatchedTerms != null)
			return false;
		if (mpMatchedIds != null ? !mpMatchedIds.equals(that.mpMatchedIds) : that.mpMatchedIds != null) return false;
		if (mpMatchedTerms != null ? !mpMatchedTerms.equals(that.mpMatchedTerms) : that.mpMatchedTerms != null)
			return false;
		if (topLevelMpMatchedIds != null ? !topLevelMpMatchedIds.equals(that.topLevelMpMatchedIds) : that.topLevelMpMatchedIds != null)
			return false;
		if (topLevelMpMatchedTerms != null ? !topLevelMpMatchedTerms.equals(that.topLevelMpMatchedTerms) : that.topLevelMpMatchedTerms != null)
			return false;
		if (intermediateMpMatchedIds != null ? !intermediateMpMatchedIds.equals(that.intermediateMpMatchedIds) : that.intermediateMpMatchedIds != null)
			return false;
		if (intermediateMpMatchedTerms != null ? !intermediateMpMatchedTerms.equals(that.intermediateMpMatchedTerms) : that.intermediateMpMatchedTerms != null)
			return false;
		if (mpID != null ? !mpID.equals(that.mpID) : that.mpID != null) return false;
		if (mpTerm != null ? !mpTerm.equals(that.mpTerm) : that.mpTerm != null) return false;
		if (hpID != null ? !hpID.equals(that.hpID) : that.hpID != null) return false;
		if (hpTerm != null ? !hpTerm.equals(that.hpTerm) : that.hpTerm != null) return false;
		if (hpSynonym != null ? !hpSynonym.equals(that.hpSynonym) : that.hpSynonym != null) return false;
		if (phenotypes != null ? !phenotypes.equals(that.phenotypes) : that.phenotypes != null) return false;
		return text != null ? text.equals(that.text) : that.text == null;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (diseaseID != null ? diseaseID.hashCode() : 0);
		result = 31 * result + (diseaseSource != null ? diseaseSource.hashCode() : 0);
		result = 31 * result + (diseaseTerm != null ? diseaseTerm.hashCode() : 0);
		result = 31 * result + (diseaseAlts != null ? diseaseAlts.hashCode() : 0);
		result = 31 * result + (diseaseLocus != null ? diseaseLocus.hashCode() : 0);
		result = 31 * result + (diseaseClasses != null ? diseaseClasses.hashCode() : 0);
		result = 31 * result + (markerAccession != null ? markerAccession.hashCode() : 0);
		result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
		result = 31 * result + (hgncGeneSymbol != null ? hgncGeneSymbol.hashCode() : 0);
		result = 31 * result + (hgncGeneID != null ? hgncGeneID.hashCode() : 0);
		result = 31 * result + (hgncGeneLocus != null ? hgncGeneLocus.hashCode() : 0);
		result = 31 * result + (modelID != null ? modelID.hashCode() : 0);
		result = 31 * result + (source != null ? source.hashCode() : 0);
		result = 31 * result + (allelicComposition != null ? allelicComposition.hashCode() : 0);
		result = 31 * result + (geneticBackground != null ? geneticBackground.hashCode() : 0);
		result = 31 * result + (alleleIds != null ? alleleIds.hashCode() : 0);
		result = 31 * result + (homHet != null ? homHet.hashCode() : 0);
		result = 31 * result + (humanCurated != null ? humanCurated.hashCode() : 0);
		result = 31 * result + (mouseCurated != null ? mouseCurated.hashCode() : 0);
		result = 31 * result + (mgiPredicted != null ? mgiPredicted.hashCode() : 0);
		result = 31 * result + (impcPredicted != null ? impcPredicted.hashCode() : 0);
		result = 31 * result + (mgiPredictedKnownGene != null ? mgiPredictedKnownGene.hashCode() : 0);
		result = 31 * result + (impcPredictedKnownGene != null ? impcPredictedKnownGene.hashCode() : 0);
		result = 31 * result + (mgiNovelPredictedInLocus != null ? mgiNovelPredictedInLocus.hashCode() : 0);
		result = 31 * result + (impcNovelPredictedInLocus != null ? impcNovelPredictedInLocus.hashCode() : 0);
		result = 31 * result + (modModel != null ? modModel.hashCode() : 0);
		result = 31 * result + (htpcModel != null ? htpcModel.hashCode() : 0);
		result = 31 * result + (htpcPhenotype != null ? htpcPhenotype.hashCode() : 0);
		result = 31 * result + (maxMgiD2mScore != null ? maxMgiD2mScore.hashCode() : 0);
		result = 31 * result + (maxMgiM2dScore != null ? maxMgiM2dScore.hashCode() : 0);
		result = 31 * result + (maxImpcD2mScore != null ? maxImpcD2mScore.hashCode() : 0);
		result = 31 * result + (maxImpcM2dScore != null ? maxImpcM2dScore.hashCode() : 0);
		result = 31 * result + (rawModScore != null ? rawModScore.hashCode() : 0);
		result = 31 * result + (rawHtpcScore != null ? rawHtpcScore.hashCode() : 0);
		result = 31 * result + (inLocus != null ? inLocus.hashCode() : 0);
		result = 31 * result + (litModel != null ? litModel.hashCode() : 0);
		result = 31 * result + (diseaseToModelScore != null ? diseaseToModelScore.hashCode() : 0);
		result = 31 * result + (modelToDiseaseScore != null ? modelToDiseaseScore.hashCode() : 0);
		result = 31 * result + (rawScore != null ? rawScore.hashCode() : 0);
		result = 31 * result + (hpMatchedTerms != null ? hpMatchedTerms.hashCode() : 0);
		result = 31 * result + (mpMatchedIds != null ? mpMatchedIds.hashCode() : 0);
		result = 31 * result + (mpMatchedTerms != null ? mpMatchedTerms.hashCode() : 0);
		result = 31 * result + (topLevelMpMatchedIds != null ? topLevelMpMatchedIds.hashCode() : 0);
		result = 31 * result + (topLevelMpMatchedTerms != null ? topLevelMpMatchedTerms.hashCode() : 0);
		result = 31 * result + (intermediateMpMatchedIds != null ? intermediateMpMatchedIds.hashCode() : 0);
		result = 31 * result + (intermediateMpMatchedTerms != null ? intermediateMpMatchedTerms.hashCode() : 0);
		result = 31 * result + (mpID != null ? mpID.hashCode() : 0);
		result = 31 * result + (mpTerm != null ? mpTerm.hashCode() : 0);
		result = 31 * result + (hpID != null ? hpID.hashCode() : 0);
		result = 31 * result + (hpTerm != null ? hpTerm.hashCode() : 0);
		result = 31 * result + (hpSynonym != null ? hpSynonym.hashCode() : 0);
		result = 31 * result + (phenotypes != null ? phenotypes.hashCode() : 0);
		result = 31 * result + (text != null ? text.hashCode() : 0);
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
				", mpMatchedIds=" + mpMatchedIds +
				", mpMatchedTerms=" + mpMatchedTerms +
				", topLevelMpMatchedIds=" + topLevelMpMatchedIds +
				", topLevelMpMatchedTerms=" + topLevelMpMatchedTerms +
				", intermediateMpMatchedIds=" + intermediateMpMatchedIds +
				", intermediateMpMatchedTerms=" + intermediateMpMatchedTerms +
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
