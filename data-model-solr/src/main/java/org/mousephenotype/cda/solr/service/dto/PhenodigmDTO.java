/*
 * Copyright 2017 QMUL - Queen Mary University of London
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mousephenotype.cda.solr.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

/**
 * DTO to facilitate communication with the phenodigm2 solr core
 */
public class PhenodigmDTO {

    // generic fields
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String TEXT = "text";
    // fields for diseases
    public static final String DISEASE_ID = "disease_id";
    public static final String DISEASE_SOURCE = "disease_source";
    public static final String DISEASE_TERM = "disease_term";
    public static final String DISEASE_ALTS = "disease_alts";
    public static final String DISEASE_CLASSES = "disease_classes";
    public static final String DISEASE_PHENOTYPES = "disease_phenotypes";
    // fields for genes (mouse and human)
    public static final String GENE_ID = "gene_id";
    public static final String GENE_SYMBOL = "gene_symbol";
    public static final String GENE_LOCUS = "gene_locus";
    public static final String GENE_SYMBOLS_WITHDRAWN = "gene_symbols_withdrawn";
    public static final String HGNC_GENE_SYMBOL = "hgnc_gene_symbol";
    public static final String HGNC_GENE_ID = "hgnc_gene_id";
    public static final String HGNC_GENE_LOCUS = "hgnc_gene_locus";
    public static final String HGNC_GENE_SYMBOLS_WITHDRAWN = "hgnc_gene_symbols_withdrawn";
    // in some docs, "gene" can be encoded as marker
    public static final String MARKER_ID = "marker_id";
    public static final String MARKER_SYMBOL = "marker_symbol";
    public static final String MARKER_SYMBOLS_WITHDRAWN = "marker_symbols_withdrawn";
    public static final String MARKER_LOCUS = "marker_locus";
    public static final String MARKER_NUM_MODELS = "marker_num_models";
    // fields describing models
    public static final String MODEL_ID = "model_id";
    public static final String MODEL_SOURCE = "model_source";
    public static final String MODEL_GENETIC_BACKGROUND = "model_genetic_background";
    public static final String MODEL_DESCRIPTION = "model_description";
    public static final String MODEL_PHENOTYPES = "model_phenotypes";
    public static final String DISEASE_MODEL_AVG_RAW = "disease_model_avg_raw";
    public static final String DISEASE_MODEL_AVG_NORM = "disease_model_avg_norm";
    public static final String DISEASE_MODEL_MAX_RAW = "disease_model_max_raw";
    public static final String DISEASE_MODEL_MAX_NORM = "disease_model_max_norm";
    public static final String DISEASE_MATCHED_PHENOTYPES = "disease_matched_phenotypes";
    public static final String MODEL_MATCHED_PHENOTYPES = "model_matched_phenotypes";
    // descriptive/adjective fields
    public static final String IN_LOCUS = "in_locus";
    public static final String ASSOCIATION_CURATED = "association_curated";
    public static final String ASSOCIATION_ORTHOLOG = "association_ortholog";

    // type:ontology_ontology
    public static final String MP_ID = "mp_id";
    public static final String MP_TERM = "mp_term";
    public static final String HP_ID = "hp_id";
    public static final String HP_TERM = "hp_term";

    /**
     * Mappings that allow extraction of data from solr docs
     */
    // generic fields
    @Field(ID)
    private String id;
    @Field(TYPE)
    private String type;
    @Field(TEXT)
    private String text;
    // fields for diseases
    @Field(DISEASE_ID)
    private String diseaseId;
    @Field(DISEASE_SOURCE)
    private String diseaseSource;
    @Field(DISEASE_TERM)
    private String diseaseTerm;
    @Field(DISEASE_ALTS)
    private List<String> diseaseAlts;
    @Field(DISEASE_CLASSES)
    private List<String> diseaseClasses;
    @Field(DISEASE_PHENOTYPES)
    private List<String> diseasePhenotypes;
    // fields for genes
    @Field(GENE_ID)
    private String geneId;
    @Field(GENE_SYMBOL)
    private String geneSymbol;
    @Field(GENE_LOCUS)
    private String geneLocus;
    @Field(GENE_SYMBOLS_WITHDRAWN)
    private List<String> geneSymbolsWithdrawn;
    @Field(HGNC_GENE_ID)
    private String hgncGeneId;
    @Field(HGNC_GENE_SYMBOL)
    private String hgncGeneSymbol;
    @Field(HGNC_GENE_LOCUS)
    private String hgncGeneLocus;
    @Field(HGNC_GENE_SYMBOLS_WITHDRAWN)
    private List<String> hgncGeneSymbolsWithdrawn;
    @Field(MARKER_ID)
    private String markerId;
    @Field(MARKER_SYMBOL)
    private String markerSymbol;
    @Field(MARKER_SYMBOLS_WITHDRAWN)
    private List<String> markerSymbolsWithdrawn;
    @Field(MARKER_LOCUS)
    private String markerLocus;
    @Field(MARKER_NUM_MODELS)
    private int markerNumModels;
    // fields for models
    @Field(MODEL_ID)
    private String modelId;
    @Field(MODEL_SOURCE)
    private String modelSource;
    @Field(MODEL_GENETIC_BACKGROUND)
    private String modelGeneticBackground;
    @Field(MODEL_DESCRIPTION)
    private String modelDescription;
    @Field(MODEL_PHENOTYPES)
    private List<String> modelPhenotypes;
    @Field(DISEASE_MODEL_AVG_RAW)
    private double diseaseModelAvgRaw;
    @Field(DISEASE_MODEL_AVG_NORM)
    private double diseaseModelAvgNorm;
    @Field(DISEASE_MODEL_MAX_RAW)
    private double diseaseModelMaxRaw;
    @Field(DISEASE_MODEL_MAX_NORM)
    private double diseaseModelMaxNorm;
    @Field(DISEASE_MATCHED_PHENOTYPES)
    private List<String> diseaseMatchedPhenotypes;
    @Field(MODEL_MATCHED_PHENOTYPES)
    private List<String> modelMatchedPhenotypes;
    // fields for adjectives
    @Field(ASSOCIATION_CURATED)
    private Boolean associationCurated;
    @Field(ASSOCIATION_ORTHOLOG)
    private Boolean associationOrtholog;
    @Field(IN_LOCUS)
    private Boolean inLocus;
    @Field(MP_ID)
    private String mpId;
    @Field(MP_TERM)
    private String mpTerm;
    @Field(HP_ID)
    private String hpId;
    @Field(HP_TERM)
    private String hpTerm;

    /*
     * Automatically generated getters and setters
     *
     */
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
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

    public List<String> getDiseaseClasses() {
        return diseaseClasses;
    }

    public void setDiseaseClasses(List<String> diseaseClasses) {
        this.diseaseClasses = diseaseClasses;
    }

    public List<String> getDiseasePhenotypes() {
        return diseasePhenotypes;
    }

    public void setDiseasePhenotypes(List<String> diseasePhenotypes) {
        this.diseasePhenotypes = diseasePhenotypes;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public String getGeneLocus() {
        return geneLocus;
    }

    public void setGeneLocus(String geneLocus) {
        this.geneLocus = geneLocus;
    }

    public List<String> getGeneSymbolsWithdrawn() {
        return geneSymbolsWithdrawn;
    }

    public void setGeneSymbolsWithdrawn(List<String> geneSymbolsWithdrawn) {
        this.geneSymbolsWithdrawn = geneSymbolsWithdrawn;
    }

    public String getHgncGeneId() {
        return hgncGeneId;
    }

    public void setHgncGeneId(String hgncGeneId) {
        this.hgncGeneId = hgncGeneId;
    }

    public String getHgncGeneSymbol() {
        return hgncGeneSymbol;
    }

    public void setHgncGeneSymbol(String hgncGeneSymbol) {
        this.hgncGeneSymbol = hgncGeneSymbol;
    }

    public String getHgncGeneLocus() {
        return hgncGeneLocus;
    }

    public void setHgncGeneLocus(String hgncGeneLocus) {
        this.hgncGeneLocus = hgncGeneLocus;
    }

    public List<String> getHgncGeneSymbolsWithdrawn() {
        return hgncGeneSymbolsWithdrawn;
    }

    public void setHgncGeneSymbolsWithdrawn(List<String> hgncGeneSymbolsWithdrawn) {
        this.hgncGeneSymbolsWithdrawn = hgncGeneSymbolsWithdrawn;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public String getMarkerSymbol() {
        return markerSymbol;
    }

    public void setMarkerSymbol(String markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public String getMarkerLocus() {
        return markerLocus;
    }

    public void setMarkerLocus(String markerLocus) {
        this.markerLocus = markerLocus;
    }

    public int getMarkerNumModels() {
        return markerNumModels;
    }

    public void setMarkerNumModels(int markerNumModels) {
        this.markerNumModels = markerNumModels;
    }

    public List<String> getMarkerSymbolsWithdrawn() {
        return markerSymbolsWithdrawn;
    }

    public void setMarkerSymbolsWithdrawn(List<String> markerSymbolsWithdrawn) {
        this.markerSymbolsWithdrawn = markerSymbolsWithdrawn;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelSource() {
        return modelSource;
    }

    public void setModelSource(String modelSource) {
        this.modelSource = modelSource;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public List<String> getModelPhenotypes() {
        return modelPhenotypes;
    }

    public void setModelPhenotypes(List<String> modelPhenotypes) {
        this.modelPhenotypes = modelPhenotypes;
    }

    public String getModelGeneticBackground() {
        return modelGeneticBackground;
    }

    public void setModelGeneticBackground(String modelGeneticBackground) {
        this.modelGeneticBackground = modelGeneticBackground;
    }

    public double getDiseaseModelAvgRaw() {
        return diseaseModelAvgRaw;
    }

    public void setDiseaseModelAvgRaw(double diseaseModelAvgRaw) {
        this.diseaseModelAvgRaw = diseaseModelAvgRaw;
    }

    public double getDiseaseModelAvgNorm() {
        return diseaseModelAvgNorm;
    }

    public void setDiseaseModelAvgNorm(double diseaseModelAvgNorm) {
        this.diseaseModelAvgNorm = diseaseModelAvgNorm;
    }

    public double getDiseaseModelMaxRaw() {
        return diseaseModelMaxRaw;
    }

    public void setDiseaseModelMaxRaw(double diseaseModelMaxRaw) {
        this.diseaseModelMaxRaw = diseaseModelMaxRaw;
    }

    public double getDiseaseModelMaxNorm() {
        return diseaseModelMaxNorm;
    }

    public void setDiseaseModelMaxNorm(double diseaseModelMaxNorm) {
        this.diseaseModelMaxNorm = diseaseModelMaxNorm;
    }

    public List<String> getDiseaseMatchedPhenotypes() {
        return diseaseMatchedPhenotypes;
    }

    public void setDiseaseMatchedPhenotypes(List<String> diseaseMatchedPhenotypes) {
        this.diseaseMatchedPhenotypes = diseaseMatchedPhenotypes;
    }

    public List<String> getModelMatchedPhenotypes() {
        return modelMatchedPhenotypes;
    }

    public void setModelMatchedPhenotypes(List<String> modelMatchedPhenotypes) {
        this.modelMatchedPhenotypes = modelMatchedPhenotypes;
    }

    public Boolean getAssociationCurated() {
        return associationCurated;
    }

    public void setAssociationCurated(Boolean associationCurated) {
        this.associationCurated = associationCurated;
    }

    public Boolean getAssociationOrtholog() {
        return associationOrtholog;
    }

    public void setAssociationOrtholog(Boolean associationOrtholog) {
        this.associationOrtholog = associationOrtholog;
    }

    public Boolean getInLocus() {
        return inLocus;
    }

    public void setInLocus(Boolean inLocus) {
        this.inLocus = inLocus;
    }

    public String getMpId() {
        return mpId;
    }

    public void setMpId(String mpId) {
        this.mpId = mpId;
    }

    public String getMpTerm() {
        return mpTerm;
    }

    public void setMpTerm(String mpTerm) {
        this.mpTerm = mpTerm;
    }

    public String getHpId() {
        return hpId;
    }

    public void setHpId(String hpId) {
        this.hpId = hpId;
    }

    public String getHpTerm() {
        return hpTerm;
    }

    public void setHpTerm(String hpTerm) {
        this.hpTerm = hpTerm;
    }

    @Override
    public String toString() {
        return "PhenodigmDTO{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", text='" + text + '\'' +
                ", diseaseId='" + diseaseId + '\'' +
                ", diseaseSource='" + diseaseSource + '\'' +
                ", diseaseTerm='" + diseaseTerm + '\'' +
                ", diseaseAlts=" + diseaseAlts +
                ", diseaseClasses=" + diseaseClasses +
                ", diseasePhenotypes=" + diseasePhenotypes +
                ", geneId='" + geneId + '\'' +
                ", geneSymbol='" + geneSymbol + '\'' +
                ", geneLocus='" + geneLocus + '\'' +
                ", geneSymbolsWithdrawn=" + geneSymbolsWithdrawn +
                ", hgncGeneId='" + hgncGeneId + '\'' +
                ", hgncGeneSymbol='" + hgncGeneSymbol + '\'' +
                ", hgncGeneLocus='" + hgncGeneLocus + '\'' +
                ", hgncGeneSymbolsWithdrawn=" + hgncGeneSymbolsWithdrawn +
                ", markerId='" + markerId + '\'' +
                ", markerSymbol='" + markerSymbol + '\'' +
                ", markerSymbolsWithdrawn=" + markerSymbolsWithdrawn +
                ", markerLocus='" + markerLocus + '\'' +
                ", markerNumModels=" + markerNumModels +
                ", modelId='" + modelId + '\'' +
                ", modelSource='" + modelSource + '\'' +
                ", modelGeneticBackground='" + modelGeneticBackground + '\'' +
                ", modelDescription='" + modelDescription + '\'' +
                ", modelPhenotypes=" + modelPhenotypes +
                ", diseaseModelAvgRaw=" + diseaseModelAvgRaw +
                ", diseaseModelAvgNorm=" + diseaseModelAvgNorm +
                ", diseaseModelMaxRaw=" + diseaseModelMaxRaw +
                ", diseaseModelMaxNorm=" + diseaseModelMaxNorm +
                ", associationCurated=" + associationCurated +
                ", associationOrtholog=" + associationOrtholog +
                ", inLocus=" + inLocus +
                ", mpId='" + mpId + '\'' +
                ", mpTerm='" + mpTerm + '\'' +
                ", hpId='" + hpId + '\'' +
                ", hpTerm='" + hpTerm + '\'' +
                '}';
    }
}
