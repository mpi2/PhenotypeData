/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package org.mousephenotype.cda.solr.service.dto;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;
import java.util.Objects;


/**
 * Created by mrelac on 19/11/2014.
 */
public class MaDTO {
    public static final String DATA_TYPE = "dataType";
    public static final String MA_ID = "ma_id";
    public static final String MA_TERM = "ma_term";
    public static final String ALT_MA_ID = "alt_ma_id";

    public static final String UBERON_ID = "uberon_id";
    public static final String ALL_AE_MAPPED_UBERON_ID = "all_ae_mapped_uberon_id";
    public static final String EFO_ID = "efo_id";
    public static final String ALL_AE_MAPPED_EFO_ID = "all_ae_mapped_efo_id";

    public static final String MA_TERM_SYNONYM = "ma_term_synonym";
    public static final String ONTOLOGY_SUBSET = "ontology_subset";

    public static final String CHILD_MA_ID = "child_ma_id";
    public static final String CHILD_MA_TERM = "child_ma_term";
    public static final String CHILD_MA_TERM_SYNONYM = "child_ma_term_synonym";
    public static final String CHILD_MA_ID_TERM = "child_ma_idTerm";


    public static final String PARENT_MA_ID = "parent_ma_id";
    public static final String PARENT_MA_TERM = "parent_ma_term";
    public static final String PARENT_MA_TERM_SYNONYM = "parent_ma_term_synonym";

    public static final String INTERMEDIATE_MA_ID = "intermediate_ma_id";
    public static final String INTERMEDIATE_MA_TERM = "intermediate_ma_term";
    public static final String INTERMEDIATE_MA_TERM_SYNONYM = "intermediate_ma_term_synonym";

    public static final String TOP_LEVEL_MA_ID = "top_level_ma_id";
    public static final String TOP_LEVEL_MA_TERM = "top_level_ma_term";
//    public static final String TOP_LEVEL_MA_TERM_SYNONYM = "top_level_ma_term_synonym";

    public static final String SELECTED_TOP_LEVEL_MA_ID = "selected_top_level_ma_id";
    public static final String SELECTED_TOP_LEVEL_MA_TERM = "selected_top_level_ma_term";
    public static final String SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = "selected_top_level_ma_term_synonym";

    // mp to hp mapping
    public static final String HP_ID = "hp_id";
    public static final String HP_TERM = "hp_term";
    public static final String GO_ID = "go_id";

    // gene core stuff
    public static final String P_VALUE = "p_value";
    public static final String MGI_ACCESSION_ID = "mgi_accession_id";
    public static final String MARKER_SYMBOL = "marker_symbol";
    public static final String MARKER_NAME = "marker_name";
    public static final String MARKER_SYNONYM = "marker_synonym";
    public static final String MARKER_TYPE = "marker_type";
    public static final String HUMAN_GENE_SYMBOL = "human_gene_symbol";

    // latest mouse status
    public static final String STATUS = "status";

    // phenotyping status
    public static final String IMITS_PHENOTYPE_STARTED = "imits_phenotype_started";
    public static final String IMITS_PHENOTYPE_COMPLETE = "imits_phenotype_complete";
    public static final String IMITS_PHENOTYPE_STATUS = "imits_phenotype_status";

    // centers
    public static final String LATEST_PRODUCTION_CENTRE = "latest_production_centre";
    public static final String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";
    public static final String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
    public static final String LEGACY_PHENOTYPE_STATUS = "legacy_phenotype_status";

    // allele level fields of a gene
    public static final String ALLELE_NAME = "allele_name";

    // disease core stuff
    public static final String TYPE = "type";
    public static final String DISEASE_ID = "disease_id";
    public static final String DISEASE_SOURCE = "disease_source";
    public static final String DISEASE_TERM = "disease_term";
    public static final String DISEASE_ALTS = "disease_alts";
    public static final String DISEASE_CLASSES = "disease_classes";
    public static final String DISEASE_HUMAN_PHENOTYPES = "disease_human_phenotypes";
    public static final String HUMAN_CURATED = "human_curated";
    public static final String MOUSE_CURATED = "mouse_curated";
    public static final String MGI_PREDICTED = "mgi_predicted";
    public static final String IMPC_PREDICTED = "impc_predicted";
    public static final String MGI_PREDICTED_KNOWN_GENE = "mgi_predicted_known_gene";
    public static final String IMPC_PREDICTED_KNOWN_GENE = "impc_predicted_known_gene";
    public static final String MGI_NOVEL_PREDICTED_IN_LOCUS = "mgi_novel_predicted_in_locus";
    public static final String IMPC_NOVEL_PREDICTED_IN_LOCUS = "impc_novel_predicted_in_locus";

    // images core stuff
    public static final String ANNOTATION_TERM_ID = "annotationTermId";
    public static final String ANNOTATION_TERM_NAME = "annotationTermName";
    public static final String NAME = "name";
    public static final String ACCESSION = "accession";
    public static final String EXP_NAME = "expName";

    public static final String LARGE_THUMBNAIL_FILE_PATH = "largeThumbnailFilePath";
    public static final String SMALL_THUMBNAIL_FILE_PATH = "smallThumbnailFilePath";

    public static final String INFERRED_MA_TERM_ID = "inferredMaTermId";
    public static final String INFERRED_MA_TERM_NAME = "inferredMaTermName";
    public static final String ANNOTATED_HIGHER_LEVEL_MA_TERM_ID = "annotatedHigherLevelMaTermId";
    public static final String ANNOTATED_HIGHER_LEVEL_MA_TERM_NAME = "annotatedHigherLevelMaTermName";
    public static final String ANNOTATED_HIGHER_LEVEL_MP_TERM_ID = "annotatedHigherLevelMpTermId";
    public static final String ANNOTATED_HIGHER_LEVEL_MP_TERM_NAME = "annotatedHigherLevelMpTermName";
    public static final String INFERRED_HIGHER_LEVEL_MA_TERM_ID = "inferredHigherLevelMaTermId";
    public static final String INFERRED_HIGHER_LEVEL_MA_TERM_NAME = "inferredHigherLevelMaTermName";

    public static final String ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_NAME = "annotated_or_inferred_higherLevelMaTermName";
    public static final String ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_ID = "annotated_or_inferred_higherLevelMaTermId";

    public static final String SYMBOL = "symbol";
    public static final String SANGER_SYMBOL = "sangerSymbol";
    public static final String GENE_NAME = "geneName";
    public static final String SUBTYPE = "subtype";
    public static final String GENE_SYNONYMS = "geneSynonyms";

    public static final String MA_TERM_ID = "maTermId";
    public static final String MA_TERM_NAME = "maTermName";
    public static final String MP_TERM_ID = "mpTermId";
    public static final String MP_TERM_NAME = "mpTermName";
    public static final String EXP_NAME_EXP = "expName_exp";
    public static final String SYMBOL_GENE = "symbol_gene";
    public static final String TOP_LEVEL = "topLevel";

    public static final String ALLELE_SYMBOL = "allele_symbol";
    public static final String ALLELE_ID = "allele_id";

    public static final String STRAIN_NAME = "strain_name";
    public static final String STRAIN_ID = "strain_id";
    public static final String GENETIC_BACKGROUND = "genetic_background";

    public static final String PIPELINE_NAME = "pipeline_name";
    public static final String PIPELINE_STABLE_ID = "pipeline_stable_id";
    public static final String PIPELINE_STABLE_KEY = "pipeline_stable_key";


    public static final String PROCEDURE_NAME = "procedure_name";
    public static final String PROCEDURE_STABLE_ID = "procedure_stable_id";
    public static final String PROCEDURE_STABLE_KEY = "procedure_stable_key";

    public static final String PARAMETER_NAME = "parameter_name";
    public static final String PARAMETER_STABLE_ID = "parameter_stable_id";
    public static final String PARAMETER_STABLE_KEY = "parameter_stable_key";

    public static final String MP_ID = "mp_id";
    public static final String MP_TERM = "mp_term";
    public static final String MP_TERM_SYNONYM = "mp_term_synonym";

    public static final String TOP_LEVEL_MP_ID = "top_level_mp_id";
    public static final String TOP_LEVEL_MP_TERM = "top_level_mp_term";
    public static final String TOP_LEVEL_MP_TERM_SYNONYM = "top_level_mp_term_synonym";

    public static final String INTERMEDIATE_MP_ID = "intermediate_mp_id";
    public static final String INTERMEDIATE_MP_TERM = "intermediate_mp_term";
    public static final String INTERMEDIATE_MP_TERM_SYNONYM = "intermediate_mp_term_synonym";

    public static final String CHILD_MP_ID = "child_mp_id";
    public static final String CHILD_MP_TERM = "child_mp_term";
    public static final String CHILD_MP_TERM_SYNONYM = "child_mp_term_synonym";

    // catchall field, containing all other searchable text fields
    public static final String TEXT = "text";
    public static final String AUTO_SUGGEST = "auto_suggest";

    // bucket list qf
    public static final String GENE_QF = "geneQf";
    public static final String MP_QF = "mpQf";
    public static final String DISEASE_QF = "diseaseQf";
    public static final String MA_QF = "maQf";


    @Field(DATA_TYPE)
    private String dataType;

    @Field(MA_ID)
    private String maId;
    
    @Field(UBERON_ID)
    private List<String> uberonIds;

    @Field(ALL_AE_MAPPED_UBERON_ID)
    private List<String> all_ae_mapped_uberonIds;

    @Field(EFO_ID)
    private List<String> efoIds;

    @Field(ALL_AE_MAPPED_EFO_ID)
    private List<String> all_ae_mapped_efoIds;

    @Field(MA_TERM)
    private String maTerm;

    @Field(MA_TERM_SYNONYM)
    private List<String> maTermSynonym;

    @Field(ALT_MA_ID)
    private List<String> altMaIds;

    @Field(ONTOLOGY_SUBSET)
    private List<String> ontologySubset;

    @Field(PARENT_MA_ID)
    private List<String> parentMaId;

    @Field(PARENT_MA_TERM)
    private List<String> parentMaTerm;

    @Field(PARENT_MA_TERM_SYNONYM)
    private List<String> parentMaTermSynonym;
    
    @Field(CHILD_MA_ID)
    private List<String> childMaId;

    @Field(CHILD_MA_TERM)
    private List<String> childMaTerm;

    @Field(CHILD_MA_TERM_SYNONYM)
    private List<String> childMaTermSynonym;

    @Field(CHILD_MA_ID_TERM)
    private List<String> childMaIdTerm;


    @Field(TOP_LEVEL_MA_ID)
    private List<String> topLevelMaId;

    @Field(TOP_LEVEL_MA_TERM)
    private List<String> topLevelMaTerm;

//    @Field(TOP_LEVEL_MA_TERM_SYNONYM)
//    private List<String> topLevelMaTermSynonym;

    @Field(SELECTED_TOP_LEVEL_MA_ID)
    private List<String> selectedTopLevelMaId;

    @Field(SELECTED_TOP_LEVEL_MA_TERM)
    private List<String> selectedTopLevelMaTerm;

    @Field(SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
    private List<String> selectedTopLevelMaTermSynonym;

    // mp to hp mapping
    @Field(HP_ID)
    private List<String> hpId;

    @Field(HP_TERM)
    private List<String> hpTerm;

    @Field(GO_ID)
    private List<String> goId;

    // gene core stuff
    @Field(P_VALUE)
    private List<Float> pValue;

    @Field(MGI_ACCESSION_ID)
    private List<String> mgiAccessionId;

    @Field(MARKER_SYMBOL)
    private List<String> markerSymbol;

    @Field(MARKER_NAME)
    private List<String> markerName;

    @Field(MARKER_SYNONYM)
    private List<String> markerSynonym;

    @Field(MARKER_TYPE)
    private List<String> markerType;

    @Field(HUMAN_GENE_SYMBOL)
    private List<String> humanGeneSymbol;


    // latest mouse status
    @Field(STATUS)
    private List<String> status;


    // phenotyping status
    @Field(IMITS_PHENOTYPE_STARTED)
    private List<String> imitsPhenotypeStarted;

    @Field(IMITS_PHENOTYPE_COMPLETE)
    private List<String> imitsPhenotypeComplete;

    @Field(IMITS_PHENOTYPE_STATUS)
    private List<String> imitsPhenotypeStatus;


    // centers
    @Field(LATEST_PRODUCTION_CENTRE)
    private List<String> latestProductionCentre;

    @Field(LATEST_PHENOTYPING_CENTRE)
    private List<String> latestPhenotypingCentre;

    @Field(LATEST_PHENOTYPE_STATUS)
    private List<String> latestPhenotypeStatus;

    @Field(LEGACY_PHENOTYPE_STATUS)
    private List<Integer> legacyPhenotypeStatus;


    // allele level fields of a gene
    @Field(ALLELE_NAME)
    private List<String> alleleName;


    // disease core stuff
    @Field(TYPE)
    private List<String> type;

    @Field(DISEASE_ID)
    private List<String> diseaseId;

    @Field(DISEASE_SOURCE)
    private List<String> diseaseSource;

    @Field(DISEASE_TERM)
    private List<String> diseaseTerm;

    @Field(DISEASE_ALTS)
    private List<String> diseaseAlts;

    @Field(DISEASE_CLASSES)
    private List<String> diseaseClasses;

    @Field(DISEASE_HUMAN_PHENOTYPES)
    private List<String> diseaseHumanPhenotypes;

    @Field(HUMAN_CURATED)
    private List<Boolean> humanCurated;

    @Field(MOUSE_CURATED)
    private List<Boolean> mouseCurated;

    @Field(MGI_PREDICTED)
    private List<Boolean> mgiPredicted;

    @Field(IMPC_PREDICTED)
    private List<Boolean> impcPredicted;

    @Field(MGI_PREDICTED_KNOWN_GENE)
    private List<Boolean> mgiPredictedKnownGene;

    @Field(IMPC_PREDICTED_KNOWN_GENE)
    private List<Boolean> impcPredictedKnownGene;

    @Field(MGI_NOVEL_PREDICTED_IN_LOCUS)
    private List<Boolean> mgiNovelPredictedInLocus;

    @Field(IMPC_NOVEL_PREDICTED_IN_LOCUS)
    private List<Boolean> impcNovelPredictedInLocus;


    // images core stuff
    @Field(ANNOTATION_TERM_ID)
    private List<String> annotationTermId;

    @Field(ANNOTATION_TERM_NAME)
    private List<String> annotationTermName;

    @Field(NAME)
    private List<String> name;

    @Field(ACCESSION)
    private List<String> accession;

    @Field(EXP_NAME)
    private List<String> expName;

    @Field(LARGE_THUMBNAIL_FILE_PATH)
    private List<String> largeThumbnailFilePath;

    @Field(SMALL_THUMBNAIL_FILE_PATH)
    private List<String> smallThumbnailFilePath;

    @Field(INFERRED_MA_TERM_ID)
    private List<String> inferredMaTermId;

    @Field(INFERRED_MA_TERM_NAME)
    private List<String> inferredMaTermName;

    @Field(ANNOTATED_HIGHER_LEVEL_MA_TERM_ID)
    private List<String> annotatedHigherLevelMaTermId;

    @Field(ANNOTATED_HIGHER_LEVEL_MA_TERM_NAME)
    private List<String> annotatedHigherLevelMaTermName;

    @Field(ANNOTATED_HIGHER_LEVEL_MP_TERM_ID)
    private List<String> annotatedHigherLevelMpTermId;

    @Field(ANNOTATED_HIGHER_LEVEL_MP_TERM_NAME)
    private List<String> annotatedHigherLevelMpTermName;

    @Field(INFERRED_HIGHER_LEVEL_MA_TERM_ID)
    private List<String> inferredHigherLevelMaTermId;

    @Field(INFERRED_HIGHER_LEVEL_MA_TERM_NAME)
    private List<String> inferredHigherLevelMaTermName;

    @Field(ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_NAME)
    private List<String> annotatedOrInferredHigherLevelMaTermName;

    @Field(ANNOTATED_OR_INFERRED_HIGHER_LEVEL_MA_TERM_ID)
    private List<String> annotatedOrInferredHigherLevelMaTermId;

    @Field(SYMBOL)
    private List<String> symbol;

    @Field(SANGER_SYMBOL)
    private List<String> sangerSymbol;

    @Field(GENE_NAME)
    private List<String> geneName;

    @Field(SUBTYPE)
    private List<String> subtype;

    @Field(GENE_SYNONYMS)
    private List<String> geneSynonyms;

    @Field(MA_TERM_ID)
    private List<String> maTermId;

    @Field(MA_TERM_NAME)
    private List<String> maTermName;

    @Field(MP_TERM_ID)
    private List<String> mpTermId;

    @Field(MP_TERM_NAME)
    private List<String> mpTermName;

    @Field(EXP_NAME_EXP)
    private List<String> expNameExp;

    @Field(SYMBOL_GENE)
    private List<String> symbolGene;

    @Field(TOP_LEVEL)
    private List<String> topLevel;

    @Field(ALLELE_SYMBOL)
    private List<String> alleleSymbol;

    @Field(ALLELE_ID)
    private List<String> alleleId;

    @Field(STRAIN_NAME)
    private List<String> strainName;

    @Field(STRAIN_ID)
    private List<String> strainId;

    @Field(GENETIC_BACKGROUND)
    String geneticBackground;

    @Field(PIPELINE_NAME)
    private List<String> pipelineName;

    @Field(PIPELINE_STABLE_ID)
    private List<String> pipelineStableId;

    @Field(PIPELINE_STABLE_KEY)
    private List<String> pipelineStableKey;

    @Field(PROCEDURE_NAME)
    private List<String> procedureName;

    @Field(PROCEDURE_STABLE_ID)
    private List<String> procedureStableId;

    @Field(PROCEDURE_STABLE_KEY)
    private List<String> procedureStableKey;

    @Field(PARAMETER_NAME)
    private List<String> parameterName;

    @Field(PARAMETER_STABLE_ID)
    private List<String> parameterStableId;

    @Field(PARAMETER_STABLE_KEY)
    private List<String> parameterStableKey;

    @Field(MP_ID)
    private List<String> mpId;

    @Field(MP_TERM)
    private List<String> mpTerm;

    @Field(MP_TERM_SYNONYM)
    private List<String> mpTermSynonym;

    @Field(TOP_LEVEL_MP_ID)
    private List<String> topLevelMpId;

    @Field(TOP_LEVEL_MP_TERM)
    private List<String> topLevelMpTerm;

    @Field(TOP_LEVEL_MP_TERM_SYNONYM)
    private List<String> topLevelMpTermSynonym;


    @Field(INTERMEDIATE_MA_ID)
    private List<String> intermediateMaId;

    @Field(INTERMEDIATE_MA_TERM)
    private List<String> intermediateMaTerm;

    @Field(INTERMEDIATE_MA_TERM_SYNONYM)
    private List<String> intermediateMaTermSynonym;


    @Field(CHILD_MP_ID)
    private List<String> childMpId;

    @Field(CHILD_MP_TERM)
    private List<String> childMpTerm;

    @Field(CHILD_MP_TERM_SYNONYM)
    private List<String> childMpTermSynonym;

    // catchall field, containing all other searchable text fields
    @Field(TEXT)
    private List<String> text;

    @Field(AUTO_SUGGEST)
    private List<String> autoSuggest;

    // bucket list qf
    @Field(GENE_QF)
    private List<String> geneQf;

    @Field(MP_QF)
    private List<String> mpQf;

    @Field(DISEASE_QF)
    private List<String> diseaseQf;

    @Field(MA_QF)
    private List<String> maQf;
        
        
    // SETTERS AND GETTERS

    public String getDataType() {
        return dataType;
    }

    public List<String> getParentMaId() {
		return parentMaId;
	}

	public void setParentMaId(List<String> parentMaId) {
		this.parentMaId = parentMaId;
	}

	public List<String> getParentMaTerm() {
		return parentMaTerm;
	}

	public void setParentMaTerm(List<String> parentMaTerm) {
		this.parentMaTerm = parentMaTerm;
	}

	public List<String> getParentMaTermSynonym() {
		return parentMaTermSynonym;
	}

	public void setParentMaTermSynonym(List<String> parentMaTermSynonym) {
		this.parentMaTermSynonym = parentMaTermSynonym;
	}

	public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getMaId() {
        return maId;
    }

    public void setMaId(String maId) {
        this.maId = maId;
    }

    public List<String> getAltMaIds() {
        return altMaIds;
    }

    public void setAltMaIds(List<String> altMaIds) {
        this.altMaIds = altMaIds;
    }

    public List<String> getUberonIds() {
        return uberonIds;
    }

    public void setUberonIds(List<String> uberonIds) {
        this.uberonIds = uberonIds;
    }

    public List<String> getAllAeMappedUberonIds() { return all_ae_mapped_uberonIds; }

    public void setAllAeMappedUberonIds(List<String> all_ae_mapped_uberonIds) {
        this.all_ae_mapped_uberonIds = all_ae_mapped_uberonIds;
    }

    public List<String> getEfoIds() {
        return efoIds;
    }

    public void setEfoIds(List<String> efoIds) {
        this.efoIds = efoIds;
    }

    public List<String> getAllAeMappedEfoIds() {
        return all_ae_mapped_efoIds;
    }

    public void setAllAeMappedEfoIds(List<String> all_ae_mapped_efoIds) {
        this.all_ae_mapped_efoIds = all_ae_mapped_efoIds;
    }
    
    public String getMaTerm() {
        return maTerm;
    }

    public void setMaTerm(String maTerm) {
        this.maTerm = maTerm;
    }

    public List<String> getMaTermSynonym() {
        return maTermSynonym;
    }

    public void setMaTermSynonym(List<String> maTermSynonym) {
        this.maTermSynonym = maTermSynonym;
    }

    public List<String> getOntologySubset() {
        return ontologySubset;
    }

    public void setOntologySubset(List<String> ontologySubset) {
        this.ontologySubset = ontologySubset;
    }

    public List<String> getChildMaId() {
        return childMaId;
    }

    public void setChildMaId(List<String> childMaId) {
        this.childMaId = childMaId;
    }

    public List<String> getChildMaTerm() {
        return childMaTerm;
    }

    public void setChildMaTerm(List<String> childMaTerm) {
        this.childMaTerm = childMaTerm;
    }

    public List<String> getChildMaTermSynonym() {
        return childMaTermSynonym;
    }

    public void setChildMaTermSynonym(List<String> childMaTermSynonym) {
        this.childMaTermSynonym = childMaTermSynonym;
    }

    public List<String> getChildMaIdTerm() {
        return childMaIdTerm;
    }

    public void setChildMaIdTerm(List<String> childMaIdTerm) {
        this.childMaIdTerm = childMaIdTerm;
    }

    public List<String> getTopLevelMaId() {
        return topLevelMaId;
    }

    public void setTopLevelMaId(List<String> topLevelMaId) {
        this.topLevelMaId = topLevelMaId;
    }
        
    public List<String> getTopLevelMaTerm() {
        return topLevelMaTerm;
    }

    public void setTopLevelMaTerm(List<String> topLevelMaTerm) {
        this.topLevelMaTerm = topLevelMaTerm;
    }

//    public List<String> getTopLevelMaTermSynonym() {
//        return topLevelMaTermSynonym;
//    }
//
//    public void setTopLevelMaTermSynonym(List<String> topLevelMaTermSynonym) {
//        this.topLevelMaTermSynonym = topLevelMaTermSynonym;
//    }

    public List<String> getSelectedTopLevelMaId() {
        return selectedTopLevelMaId;
    }

    public void setSelectedTopLevelMaId(List<String> selectedTopLevelMaId) {
        this.selectedTopLevelMaId = selectedTopLevelMaId;
    }

    public List<String> getSelectedTopLevelMaTerm() {
        return selectedTopLevelMaTerm;
    }

    public void setSelectedTopLevelMaTerm(List<String> selectedTopLevelMaTerm) {
        this.selectedTopLevelMaTerm = selectedTopLevelMaTerm;
    }

    public List<String> getSelectedTopLevelMaTermSynonym() {
        return selectedTopLevelMaTermSynonym;
    }

    public void setSelectedTopLevelMaTermSynonym(List<String> selectedTopLevelMaTermSynonym) {
        this.selectedTopLevelMaTermSynonym = selectedTopLevelMaTermSynonym;
    }

    public List<String> getHpId() {
        return hpId;
    }

    public void setHpId(List<String> hpId) {
        this.hpId = hpId;
    }

    public List<String> getHpTerm() {
        return hpTerm;
    }

    public void setHpTerm(List<String> hpTerm) {
        this.hpTerm = hpTerm;
    }

    public List<String> getGoId() {
        return goId;
    }

    public void setGoId(List<String> goId) {
        this.goId = goId;
    }

    public List<Float> getpValue() {
        return pValue;
    }

    public void setpValue(List<Float> pValue) {
        this.pValue = pValue;
    }

    public List<String> getMgiAccessionId() {
        return mgiAccessionId;
    }

    public void setMgiAccessionId(List<String> mgiAccessionId) {
        this.mgiAccessionId = mgiAccessionId;
    }

    public List<String> getMarkerSymbol() {
        return markerSymbol;
    }

    public void setMarkerSymbol(List<String> markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public List<String> getMarkerName() {
        return markerName;
    }

    public void setMarkerName(List<String> markerName) {
        this.markerName = markerName;
    }

    public List<String> getMarkerSynonym() {
        return markerSynonym;
    }

    public void setMarkerSynonym(List<String> markerSynonym) {
        this.markerSynonym = markerSynonym;
    }

    public List<String> getMarkerType() {
        return markerType;
    }

    public void setMarkerType(List<String> markerType) {
        this.markerType = markerType;
    }

    public List<String> getHumanGeneSymbol() {
        return humanGeneSymbol;
    }

    public void setHumanGeneSymbol(List<String> humanGeneSymbol) {
        this.humanGeneSymbol = humanGeneSymbol;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public List<String> getImitsPhenotypeStarted() {
        return imitsPhenotypeStarted;
    }

    public void setImitsPhenotypeStarted(List<String> imitsPhenotypeStarted) {
        this.imitsPhenotypeStarted = imitsPhenotypeStarted;
    }

    public List<String> getImitsPhenotypeComplete() {
        return imitsPhenotypeComplete;
    }

    public void setImitsPhenotypeComplete(List<String> imitsPhenotypeComplete) {
        this.imitsPhenotypeComplete = imitsPhenotypeComplete;
    }

    public List<String> getImitsPhenotypeStatus() {
        return imitsPhenotypeStatus;
    }

    public void setImitsPhenotypeStatus(List<String> imitsPhenotypeStatus) {
        this.imitsPhenotypeStatus = imitsPhenotypeStatus;
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

    public List<String> getLatestPhenotypeStatus() {
        return latestPhenotypeStatus;
    }

    public void setLatestPhenotypeStatus(List<String> latestPhenotypeStatus) {
        this.latestPhenotypeStatus = latestPhenotypeStatus;
    }

    public List<Integer> getLegacyPhenotypeStatus() {
        return legacyPhenotypeStatus;
    }

    public void setLegacyPhenotypeStatus(List<Integer> legacyPhenotypeStatus) {
        this.legacyPhenotypeStatus = legacyPhenotypeStatus;
    }

    public List<String> getAlleleName() {
        return alleleName;
    }

    public void setAlleleName(List<String> alleleName) {
        this.alleleName = alleleName;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public List<String> getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(List<String> diseaseId) {
        this.diseaseId = diseaseId;
    }

    public List<String> getDiseaseSource() {
        return diseaseSource;
    }

    public void setDiseaseSource(List<String> diseaseSource) {
        this.diseaseSource = diseaseSource;
    }

    public List<String> getDiseaseTerm() {
        return diseaseTerm;
    }

    public void setDiseaseTerm(List<String> diseaseTerm) {
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

    public List<String> getDiseaseHumanPhenotypes() {
        return diseaseHumanPhenotypes;
    }

    public void setDiseaseHumanPhenotypes(List<String> diseaseHumanPhenotypes) {
        this.diseaseHumanPhenotypes = diseaseHumanPhenotypes;
    }

    public List<Boolean> getHumanCurated() {
        return humanCurated;
    }

    public void setHumanCurated(List<Boolean> humanCurated) {
        this.humanCurated = humanCurated;
    }

    public List<Boolean> getMouseCurated() {
        return mouseCurated;
    }

    public void setMouseCurated(List<Boolean> mouseCurated) {
        this.mouseCurated = mouseCurated;
    }

    public List<Boolean> getMgiPredicted() {
        return mgiPredicted;
    }

    public void setMgiPredicted(List<Boolean> mgiPredicted) {
        this.mgiPredicted = mgiPredicted;
    }

    public List<Boolean> getImpcPredicted() {
        return impcPredicted;
    }

    public void setImpcPredicted(List<Boolean> impcPredicted) {
        this.impcPredicted = impcPredicted;
    }

    public List<Boolean> getMgiPredictedKnownGene() {
        return mgiPredictedKnownGene;
    }

    public void setMgiPredictedKnownGene(List<Boolean> mgiPredictedKnownGene) {
        this.mgiPredictedKnownGene = mgiPredictedKnownGene;
    }

    public List<Boolean> getImpcPredictedKnownGene() {
        return impcPredictedKnownGene;
    }

    public void setImpcPredictedKnownGene(List<Boolean> impcPredictedKnownGene) {
        this.impcPredictedKnownGene = impcPredictedKnownGene;
    }

    public List<Boolean> getMgiNovelPredictedInLocus() {
        return mgiNovelPredictedInLocus;
    }

    public void setMgiNovelPredictedInLocus(List<Boolean> mgiNovelPredictedInLocus) {
        this.mgiNovelPredictedInLocus = mgiNovelPredictedInLocus;
    }

    public List<Boolean> getImpcNovelPredictedInLocus() {
        return impcNovelPredictedInLocus;
    }

    public void setImpcNovelPredictedInLocus(List<Boolean> impcNovelPredictedInLocus) {
        this.impcNovelPredictedInLocus = impcNovelPredictedInLocus;
    }

    public List<String> getAnnotationTermId() {
        return annotationTermId;
    }

    public void setAnnotationTermId(List<String> annotationTermId) {
        this.annotationTermId = annotationTermId;
    }

    public List<String> getAnnotationTermName() {
        return annotationTermName;
    }

    public void setAnnotationTermName(List<String> annotationTermName) {
        this.annotationTermName = annotationTermName;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getAccession() {
        return accession;
    }

    public void setAccession(List<String> accession) {
        this.accession = accession;
    }

    public List<String> getExpName() {
        return expName;
    }

    public void setExpName(List<String> expName) {
        this.expName = expName;
    }

    public List<String> getLargeThumbnailFilePath() {
        return largeThumbnailFilePath;
    }

    public void setLargeThumbnailFilePath(List<String> largeThumbnailFilePath) {
        this.largeThumbnailFilePath = largeThumbnailFilePath;
    }

    public List<String> getSmallThumbnailFilePath() {
        return smallThumbnailFilePath;
    }

    public void setSmallThumbnailFilePath(List<String> smallThumbnailFilePath) {
        this.smallThumbnailFilePath = smallThumbnailFilePath;
    }

    public List<String> getInferredMaTermId() {
        return inferredMaTermId;
    }

    public void setInferredMaTermId(List<String> inferredMaTermId) {
        this.inferredMaTermId = inferredMaTermId;
    }

    public List<String> getInferredMaTermName() {
        return inferredMaTermName;
    }

    public void setInferredMaTermName(List<String> inferredMaTermName) {
        this.inferredMaTermName = inferredMaTermName;
    }

    public List<String> getAnnotatedHigherLevelMaTermId() {
        return annotatedHigherLevelMaTermId;
    }

    public void setAnnotatedHigherLevelMaTermId(List<String> annotatedHigherLevelMaTermId) {
        this.annotatedHigherLevelMaTermId = annotatedHigherLevelMaTermId;
    }

    public List<String> getAnnotatedHigherLevelMaTermName() {
        return annotatedHigherLevelMaTermName;
    }

    public void setAnnotatedHigherLevelMaTermName(List<String> annotatedHigherLevelMaTermName) {
        this.annotatedHigherLevelMaTermName = annotatedHigherLevelMaTermName;
    }

    public List<String> getAnnotatedHigherLevelMpTermId() {
        return annotatedHigherLevelMpTermId;
    }

    public void setAnnotatedHigherLevelMpTermId(List<String> annotatedHigherLevelMpTermId) {
        this.annotatedHigherLevelMpTermId = annotatedHigherLevelMpTermId;
    }

    public List<String> getAnnotatedHigherLevelMpTermName() {
        return annotatedHigherLevelMpTermName;
    }

    public void setAnnotatedHigherLevelMpTermName(List<String> annotatedHigherLevelMpTermName) {
        this.annotatedHigherLevelMpTermName = annotatedHigherLevelMpTermName;
    }

    public List<String> getInferredHigherLevelMaTermId() {
        return inferredHigherLevelMaTermId;
    }

    public void setInferredHigherLevelMaTermId(List<String> inferredHigherLevelMaTermId) {
        this.inferredHigherLevelMaTermId = inferredHigherLevelMaTermId;
    }

    public List<String> getInferredHigherLevelMaTermName() {
        return inferredHigherLevelMaTermName;
    }

    public void setInferredHigherLevelMaTermName(List<String> inferredHigherLevelMaTermName) {
        this.inferredHigherLevelMaTermName = inferredHigherLevelMaTermName;
    }

    public List<String> getAnnotatedOrInferredHigherLevelMaTermName() {
        return annotatedOrInferredHigherLevelMaTermName;
    }

    public void setAnnotatedOrInferredHigherLevelMaTermName(List<String> annotatedOrInferredHigherLevelMaTermName) {
        this.annotatedOrInferredHigherLevelMaTermName = annotatedOrInferredHigherLevelMaTermName;
    }

    public List<String> getAnnotatedOrInferredHigherLevelMaTermId() {
        return annotatedOrInferredHigherLevelMaTermId;
    }

    public void setAnnotatedOrInferredHigherLevelMaTermId(List<String> annotatedOrInferredHigherLevelMaTermId) {
        this.annotatedOrInferredHigherLevelMaTermId = annotatedOrInferredHigherLevelMaTermId;
    }

    public List<String> getSymbol() {
        return symbol;
    }

    public void setSymbol(List<String> symbol) {
        this.symbol = symbol;
    }

    public List<String> getSangerSymbol() {
        return sangerSymbol;
    }

    public void setSangerSymbol(List<String> sangerSymbol) {
        this.sangerSymbol = sangerSymbol;
    }

    public List<String> getGeneName() {
        return geneName;
    }

    public void setGeneName(List<String> geneName) {
        this.geneName = geneName;
    }

    public List<String> getSubtype() {
        return subtype;
    }

    public void setSubtype(List<String> subtype) {
        this.subtype = subtype;
    }

    public List<String> getGeneSynonyms() {
        return geneSynonyms;
    }

    public void setGeneSynonyms(List<String> geneSynonyms) {
        this.geneSynonyms = geneSynonyms;
    }

    public List<String> getMaTermId() {
        return maTermId;
    }

    public void setMaTermId(List<String> maTermId) {
        this.maTermId = maTermId;
    }

    public List<String> getMaTermName() {
        return maTermName;
    }

    public void setMaTermName(List<String> maTermName) {
        this.maTermName = maTermName;
    }

    public List<String> getMpTermId() {
        return mpTermId;
    }

    public void setMpTermId(List<String> mpTermId) {
        this.mpTermId = mpTermId;
    }

    public List<String> getMpTermName() {
        return mpTermName;
    }

    public void setMpTermName(List<String> mpTermName) {
        this.mpTermName = mpTermName;
    }

    public List<String> getExpNameExp() {
        return expNameExp;
    }

    public void setExpNameExp(List<String> expNameExp) {
        this.expNameExp = expNameExp;
    }

    public List<String> getSymbolGene() {
        return symbolGene;
    }

    public void setSymbolGene(List<String> symbolGene) {
        this.symbolGene = symbolGene;
    }

    public List<String> getTopLevel() {
        return topLevel;
    }

    public void setTopLevel(List<String> topLevel) {
        this.topLevel = topLevel;
    }

    public List<String> getAlleleSymbol() {
        return alleleSymbol;
    }

    public void setAlleleSymbol(List<String> alleleSymbol) {
        this.alleleSymbol = alleleSymbol;
    }

    public List<String> getAlleleId() {
        return alleleId;
    }

    public void setAlleleId(List<String> alleleId) {
        this.alleleId = alleleId;
    }

    public List<String> getStrainName() {
        return strainName;
    }

    public void setStrainName(List<String> strainName) {
        this.strainName = strainName;
    }

    public List<String> getStrainId() {
        return strainId;
    }

    public void setStrainId(List<String> strainId) {
        this.strainId = strainId;
    }

    public String getGeneticBackground() {
        return geneticBackground;
    }

    public void setGeneticBackground(String geneticBackground) {
        this.geneticBackground = geneticBackground;
    }

    public List<String> getPipelineName() {
        return pipelineName;
    }

    public void setPipelineName(List<String> pipelineName) {
        this.pipelineName = pipelineName;
    }

    public List<String> getPipelineStableId() {
        return pipelineStableId;
    }

    public void setPipelineStableId(List<String> pipelineStableId) {
        this.pipelineStableId = pipelineStableId;
    }

    public List<String> getPipelineStableKey() {
        return pipelineStableKey;
    }

    public void setPipelineStableKey(List<String> pipelineStableKey) {
        this.pipelineStableKey = pipelineStableKey;
    }

    public List<String> getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(List<String> procedureName) {
        this.procedureName = procedureName;
    }

    public List<String> getProcedureStableId() {
        return procedureStableId;
    }

    public void setProcedureStableId(List<String> procedureStableId) {
        this.procedureStableId = procedureStableId;
    }

    public List<String> getProcedureStableKey() {
        return procedureStableKey;
    }

    public void setProcedureStableKey(List<String> procedureStableKey) {
        this.procedureStableKey = procedureStableKey;
    }

    public List<String> getParameterName() {
        return parameterName;
    }

    public void setParameterName(List<String> parameterName) {
        this.parameterName = parameterName;
    }

    public List<String> getParameterStableId() {
        return parameterStableId;
    }

    public void setParameterStableId(List<String> parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

    public List<String> getParameterStableKey() {
        return parameterStableKey;
    }

    public void setParameterStableKey(List<String> parameterStableKey) {
        this.parameterStableKey = parameterStableKey;
    }

    public List<String> getMpId() {
        return mpId;
    }

    public void setMpId(List<String> mpId) {
        this.mpId = mpId;
    }

    public List<String> getMpTerm() {
        return mpTerm;
    }

    public void setMpTerm(List<String> mpTerm) {
        this.mpTerm = mpTerm;
    }

    public List<String> getMpTermSynonym() {
        return mpTermSynonym;
    }

    public void setMpTermSynonym(List<String> mpTermSynonym) {
        this.mpTermSynonym = mpTermSynonym;
    }

    public List<String> getTopLevelMpId() {
        return topLevelMpId;
    }

    public void setTopLevelMpId(List<String> topLevelMpId) {
        this.topLevelMpId = topLevelMpId;
    }

    public List<String> getTopLevelMpTerm() {
        return topLevelMpTerm;
    }

    public void setTopLevelMpTerm(List<String> topLevelMpTerm) {
        this.topLevelMpTerm = topLevelMpTerm;
    }

    public List<String> getTopLevelMpTermSynonym() {
        return topLevelMpTermSynonym;
    }

    public void setTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {
        this.topLevelMpTermSynonym = topLevelMpTermSynonym;
    }

    public List<String> getIntermediateMaId() {
        return intermediateMaId;
    }

    public void setIntermediateMaId(List<String> intermediateMaId) {
        this.intermediateMaId = intermediateMaId;
    }

    public List<String> getIntermediateMaTerm() {
        return intermediateMaTerm;
    }

    public void setIntermediateMaTerm(List<String> intermediateMaTerm) {
        this.intermediateMaTerm = intermediateMaTerm;
    }

    public List<String> getIntermediateMaTermSynonym() {
        return intermediateMaTermSynonym;
    }

    public void setIntermediateMaTermSynonym(List<String> intermediateMaTermSynonym) {
        this.intermediateMaTermSynonym = intermediateMaTermSynonym;
    }

    public List<String> getChildMpId() {
        return childMpId;
    }

    public void setChildMpId(List<String> childMpId) {
        this.childMpId = childMpId;
    }

    public List<String> getChildMpTerm() {
        return childMpTerm;
    }

    public void setChildMpTerm(List<String> childMpTerm) {
        this.childMpTerm = childMpTerm;
    }

    public List<String> getChildMpTermSynonym() {
        return childMpTermSynonym;
    }

    public void setChildMpTermSynonym(List<String> childMpTermSynonym) {
        this.childMpTermSynonym = childMpTermSynonym;
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    public List<String> getAutoSuggest() {
        return autoSuggest;
    }

    public void setAutoSuggest(List<String> autoSuggest) {
        this.autoSuggest = autoSuggest;
    }

    public List<String> getGeneQf() {
        return geneQf;
    }

    public void setGeneQf(List<String> geneQf) {
        this.geneQf = geneQf;
    }

    public List<String> getMpQf() {
        return mpQf;
    }

    public void setMpQf(List<String> mpQf) {
        this.mpQf = mpQf;
    }

    public List<String> getDiseaseQf() {
        return diseaseQf;
    }

    public void setDiseaseQf(List<String> diseaseQf) {
        this.diseaseQf = diseaseQf;
    }

    public List<String> getMaQf() {
        return maQf;
    }

    public void setMaQf(List<String> maQf) {
        this.maQf = maQf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MaDTO maDTO = (MaDTO) o;

        if (dataType != null ? !dataType.equals(maDTO.dataType) : maDTO.dataType != null) return false;
        if (maId != null ? !maId.equals(maDTO.maId) : maDTO.maId != null) return false;
        if (uberonIds != null ? !uberonIds.equals(maDTO.uberonIds) : maDTO.uberonIds != null) return false;
        if (all_ae_mapped_uberonIds != null ? !all_ae_mapped_uberonIds.equals(maDTO.all_ae_mapped_uberonIds) : maDTO.all_ae_mapped_uberonIds != null)
            return false;
        if (efoIds != null ? !efoIds.equals(maDTO.efoIds) : maDTO.efoIds != null) return false;
        if (all_ae_mapped_efoIds != null ? !all_ae_mapped_efoIds.equals(maDTO.all_ae_mapped_efoIds) : maDTO.all_ae_mapped_efoIds != null)
            return false;
        if (maTerm != null ? !maTerm.equals(maDTO.maTerm) : maDTO.maTerm != null) return false;
        if (maTermSynonym != null ? !maTermSynonym.equals(maDTO.maTermSynonym) : maDTO.maTermSynonym != null)
            return false;
        if (altMaIds != null ? !altMaIds.equals(maDTO.altMaIds) : maDTO.altMaIds != null) return false;
        if (ontologySubset != null ? !ontologySubset.equals(maDTO.ontologySubset) : maDTO.ontologySubset != null)
            return false;
        if (parentMaId != null ? !parentMaId.equals(maDTO.parentMaId) : maDTO.parentMaId != null) return false;
        if (parentMaTerm != null ? !parentMaTerm.equals(maDTO.parentMaTerm) : maDTO.parentMaTerm != null) return false;
        if (parentMaTermSynonym != null ? !parentMaTermSynonym.equals(maDTO.parentMaTermSynonym) : maDTO.parentMaTermSynonym != null)
            return false;
        if (childMaId != null ? !childMaId.equals(maDTO.childMaId) : maDTO.childMaId != null) return false;
        if (childMaTerm != null ? !childMaTerm.equals(maDTO.childMaTerm) : maDTO.childMaTerm != null) return false;
        if (childMaTermSynonym != null ? !childMaTermSynonym.equals(maDTO.childMaTermSynonym) : maDTO.childMaTermSynonym != null)
            return false;
        if (childMaIdTerm != null ? !childMaIdTerm.equals(maDTO.childMaIdTerm) : maDTO.childMaIdTerm != null)
            return false;
        if (topLevelMaId != null ? !topLevelMaId.equals(maDTO.topLevelMaId) : maDTO.topLevelMaId != null) return false;
        if (topLevelMaTerm != null ? !topLevelMaTerm.equals(maDTO.topLevelMaTerm) : maDTO.topLevelMaTerm != null)
            return false;
        if (selectedTopLevelMaId != null ? !selectedTopLevelMaId.equals(maDTO.selectedTopLevelMaId) : maDTO.selectedTopLevelMaId != null)
            return false;
        if (selectedTopLevelMaTerm != null ? !selectedTopLevelMaTerm.equals(maDTO.selectedTopLevelMaTerm) : maDTO.selectedTopLevelMaTerm != null)
            return false;
        if (selectedTopLevelMaTermSynonym != null ? !selectedTopLevelMaTermSynonym.equals(maDTO.selectedTopLevelMaTermSynonym) : maDTO.selectedTopLevelMaTermSynonym != null)
            return false;
        if (hpId != null ? !hpId.equals(maDTO.hpId) : maDTO.hpId != null) return false;
        if (hpTerm != null ? !hpTerm.equals(maDTO.hpTerm) : maDTO.hpTerm != null) return false;
        if (goId != null ? !goId.equals(maDTO.goId) : maDTO.goId != null) return false;
        if (pValue != null ? !pValue.equals(maDTO.pValue) : maDTO.pValue != null) return false;
        if (mgiAccessionId != null ? !mgiAccessionId.equals(maDTO.mgiAccessionId) : maDTO.mgiAccessionId != null)
            return false;
        if (markerSymbol != null ? !markerSymbol.equals(maDTO.markerSymbol) : maDTO.markerSymbol != null) return false;
        if (markerName != null ? !markerName.equals(maDTO.markerName) : maDTO.markerName != null) return false;
        if (markerSynonym != null ? !markerSynonym.equals(maDTO.markerSynonym) : maDTO.markerSynonym != null)
            return false;
        if (markerType != null ? !markerType.equals(maDTO.markerType) : maDTO.markerType != null) return false;
        if (humanGeneSymbol != null ? !humanGeneSymbol.equals(maDTO.humanGeneSymbol) : maDTO.humanGeneSymbol != null)
            return false;
        if (status != null ? !status.equals(maDTO.status) : maDTO.status != null) return false;
        if (imitsPhenotypeStarted != null ? !imitsPhenotypeStarted.equals(maDTO.imitsPhenotypeStarted) : maDTO.imitsPhenotypeStarted != null)
            return false;
        if (imitsPhenotypeComplete != null ? !imitsPhenotypeComplete.equals(maDTO.imitsPhenotypeComplete) : maDTO.imitsPhenotypeComplete != null)
            return false;
        if (imitsPhenotypeStatus != null ? !imitsPhenotypeStatus.equals(maDTO.imitsPhenotypeStatus) : maDTO.imitsPhenotypeStatus != null)
            return false;
        if (latestProductionCentre != null ? !latestProductionCentre.equals(maDTO.latestProductionCentre) : maDTO.latestProductionCentre != null)
            return false;
        if (latestPhenotypingCentre != null ? !latestPhenotypingCentre.equals(maDTO.latestPhenotypingCentre) : maDTO.latestPhenotypingCentre != null)
            return false;
        if (latestPhenotypeStatus != null ? !latestPhenotypeStatus.equals(maDTO.latestPhenotypeStatus) : maDTO.latestPhenotypeStatus != null)
            return false;
        if (legacyPhenotypeStatus != null ? !legacyPhenotypeStatus.equals(maDTO.legacyPhenotypeStatus) : maDTO.legacyPhenotypeStatus != null)
            return false;
        if (alleleName != null ? !alleleName.equals(maDTO.alleleName) : maDTO.alleleName != null) return false;
        if (type != null ? !type.equals(maDTO.type) : maDTO.type != null) return false;
        if (diseaseId != null ? !diseaseId.equals(maDTO.diseaseId) : maDTO.diseaseId != null) return false;
        if (diseaseSource != null ? !diseaseSource.equals(maDTO.diseaseSource) : maDTO.diseaseSource != null)
            return false;
        if (diseaseTerm != null ? !diseaseTerm.equals(maDTO.diseaseTerm) : maDTO.diseaseTerm != null) return false;
        if (diseaseAlts != null ? !diseaseAlts.equals(maDTO.diseaseAlts) : maDTO.diseaseAlts != null) return false;
        if (diseaseClasses != null ? !diseaseClasses.equals(maDTO.diseaseClasses) : maDTO.diseaseClasses != null)
            return false;
        if (diseaseHumanPhenotypes != null ? !diseaseHumanPhenotypes.equals(maDTO.diseaseHumanPhenotypes) : maDTO.diseaseHumanPhenotypes != null)
            return false;
        if (humanCurated != null ? !humanCurated.equals(maDTO.humanCurated) : maDTO.humanCurated != null) return false;
        if (mouseCurated != null ? !mouseCurated.equals(maDTO.mouseCurated) : maDTO.mouseCurated != null) return false;
        if (mgiPredicted != null ? !mgiPredicted.equals(maDTO.mgiPredicted) : maDTO.mgiPredicted != null) return false;
        if (impcPredicted != null ? !impcPredicted.equals(maDTO.impcPredicted) : maDTO.impcPredicted != null)
            return false;
        if (mgiPredictedKnownGene != null ? !mgiPredictedKnownGene.equals(maDTO.mgiPredictedKnownGene) : maDTO.mgiPredictedKnownGene != null)
            return false;
        if (impcPredictedKnownGene != null ? !impcPredictedKnownGene.equals(maDTO.impcPredictedKnownGene) : maDTO.impcPredictedKnownGene != null)
            return false;
        if (mgiNovelPredictedInLocus != null ? !mgiNovelPredictedInLocus.equals(maDTO.mgiNovelPredictedInLocus) : maDTO.mgiNovelPredictedInLocus != null)
            return false;
        if (impcNovelPredictedInLocus != null ? !impcNovelPredictedInLocus.equals(maDTO.impcNovelPredictedInLocus) : maDTO.impcNovelPredictedInLocus != null)
            return false;
        if (annotationTermId != null ? !annotationTermId.equals(maDTO.annotationTermId) : maDTO.annotationTermId != null)
            return false;
        if (annotationTermName != null ? !annotationTermName.equals(maDTO.annotationTermName) : maDTO.annotationTermName != null)
            return false;
        if (name != null ? !name.equals(maDTO.name) : maDTO.name != null) return false;
        if (accession != null ? !accession.equals(maDTO.accession) : maDTO.accession != null) return false;
        if (expName != null ? !expName.equals(maDTO.expName) : maDTO.expName != null) return false;
        if (largeThumbnailFilePath != null ? !largeThumbnailFilePath.equals(maDTO.largeThumbnailFilePath) : maDTO.largeThumbnailFilePath != null)
            return false;
        if (smallThumbnailFilePath != null ? !smallThumbnailFilePath.equals(maDTO.smallThumbnailFilePath) : maDTO.smallThumbnailFilePath != null)
            return false;
        if (inferredMaTermId != null ? !inferredMaTermId.equals(maDTO.inferredMaTermId) : maDTO.inferredMaTermId != null)
            return false;
        if (inferredMaTermName != null ? !inferredMaTermName.equals(maDTO.inferredMaTermName) : maDTO.inferredMaTermName != null)
            return false;
        if (annotatedHigherLevelMaTermId != null ? !annotatedHigherLevelMaTermId.equals(maDTO.annotatedHigherLevelMaTermId) : maDTO.annotatedHigherLevelMaTermId != null)
            return false;
        if (annotatedHigherLevelMaTermName != null ? !annotatedHigherLevelMaTermName.equals(maDTO.annotatedHigherLevelMaTermName) : maDTO.annotatedHigherLevelMaTermName != null)
            return false;
        if (annotatedHigherLevelMpTermId != null ? !annotatedHigherLevelMpTermId.equals(maDTO.annotatedHigherLevelMpTermId) : maDTO.annotatedHigherLevelMpTermId != null)
            return false;
        if (annotatedHigherLevelMpTermName != null ? !annotatedHigherLevelMpTermName.equals(maDTO.annotatedHigherLevelMpTermName) : maDTO.annotatedHigherLevelMpTermName != null)
            return false;
        if (inferredHigherLevelMaTermId != null ? !inferredHigherLevelMaTermId.equals(maDTO.inferredHigherLevelMaTermId) : maDTO.inferredHigherLevelMaTermId != null)
            return false;
        if (inferredHigherLevelMaTermName != null ? !inferredHigherLevelMaTermName.equals(maDTO.inferredHigherLevelMaTermName) : maDTO.inferredHigherLevelMaTermName != null)
            return false;
        if (annotatedOrInferredHigherLevelMaTermName != null ? !annotatedOrInferredHigherLevelMaTermName.equals(maDTO.annotatedOrInferredHigherLevelMaTermName) : maDTO.annotatedOrInferredHigherLevelMaTermName != null)
            return false;
        if (annotatedOrInferredHigherLevelMaTermId != null ? !annotatedOrInferredHigherLevelMaTermId.equals(maDTO.annotatedOrInferredHigherLevelMaTermId) : maDTO.annotatedOrInferredHigherLevelMaTermId != null)
            return false;
        if (symbol != null ? !symbol.equals(maDTO.symbol) : maDTO.symbol != null) return false;
        if (sangerSymbol != null ? !sangerSymbol.equals(maDTO.sangerSymbol) : maDTO.sangerSymbol != null) return false;
        if (geneName != null ? !geneName.equals(maDTO.geneName) : maDTO.geneName != null) return false;
        if (subtype != null ? !subtype.equals(maDTO.subtype) : maDTO.subtype != null) return false;
        if (geneSynonyms != null ? !geneSynonyms.equals(maDTO.geneSynonyms) : maDTO.geneSynonyms != null) return false;
        if (maTermId != null ? !maTermId.equals(maDTO.maTermId) : maDTO.maTermId != null) return false;
        if (maTermName != null ? !maTermName.equals(maDTO.maTermName) : maDTO.maTermName != null) return false;
        if (mpTermId != null ? !mpTermId.equals(maDTO.mpTermId) : maDTO.mpTermId != null) return false;
        if (mpTermName != null ? !mpTermName.equals(maDTO.mpTermName) : maDTO.mpTermName != null) return false;
        if (expNameExp != null ? !expNameExp.equals(maDTO.expNameExp) : maDTO.expNameExp != null) return false;
        if (symbolGene != null ? !symbolGene.equals(maDTO.symbolGene) : maDTO.symbolGene != null) return false;
        if (topLevel != null ? !topLevel.equals(maDTO.topLevel) : maDTO.topLevel != null) return false;
        if (alleleSymbol != null ? !alleleSymbol.equals(maDTO.alleleSymbol) : maDTO.alleleSymbol != null) return false;
        if (alleleId != null ? !alleleId.equals(maDTO.alleleId) : maDTO.alleleId != null) return false;
        if (strainName != null ? !strainName.equals(maDTO.strainName) : maDTO.strainName != null) return false;
        if (strainId != null ? !strainId.equals(maDTO.strainId) : maDTO.strainId != null) return false;
        if (geneticBackground != null ? !geneticBackground.equals(maDTO.geneticBackground) : maDTO.geneticBackground != null)
            return false;
        if (pipelineName != null ? !pipelineName.equals(maDTO.pipelineName) : maDTO.pipelineName != null) return false;
        if (pipelineStableId != null ? !pipelineStableId.equals(maDTO.pipelineStableId) : maDTO.pipelineStableId != null)
            return false;
        if (pipelineStableKey != null ? !pipelineStableKey.equals(maDTO.pipelineStableKey) : maDTO.pipelineStableKey != null)
            return false;
        if (procedureName != null ? !procedureName.equals(maDTO.procedureName) : maDTO.procedureName != null)
            return false;
        if (procedureStableId != null ? !procedureStableId.equals(maDTO.procedureStableId) : maDTO.procedureStableId != null)
            return false;
        if (procedureStableKey != null ? !procedureStableKey.equals(maDTO.procedureStableKey) : maDTO.procedureStableKey != null)
            return false;
        if (parameterName != null ? !parameterName.equals(maDTO.parameterName) : maDTO.parameterName != null)
            return false;
        if (parameterStableId != null ? !parameterStableId.equals(maDTO.parameterStableId) : maDTO.parameterStableId != null)
            return false;
        if (parameterStableKey != null ? !parameterStableKey.equals(maDTO.parameterStableKey) : maDTO.parameterStableKey != null)
            return false;
        if (mpId != null ? !mpId.equals(maDTO.mpId) : maDTO.mpId != null) return false;
        if (mpTerm != null ? !mpTerm.equals(maDTO.mpTerm) : maDTO.mpTerm != null) return false;
        if (mpTermSynonym != null ? !mpTermSynonym.equals(maDTO.mpTermSynonym) : maDTO.mpTermSynonym != null)
            return false;
        if (topLevelMpId != null ? !topLevelMpId.equals(maDTO.topLevelMpId) : maDTO.topLevelMpId != null) return false;
        if (topLevelMpTerm != null ? !topLevelMpTerm.equals(maDTO.topLevelMpTerm) : maDTO.topLevelMpTerm != null)
            return false;
        if (topLevelMpTermSynonym != null ? !topLevelMpTermSynonym.equals(maDTO.topLevelMpTermSynonym) : maDTO.topLevelMpTermSynonym != null)
            return false;
        if (intermediateMaId != null ? !intermediateMaId.equals(maDTO.intermediateMaId) : maDTO.intermediateMaId != null)
            return false;
        if (intermediateMaTerm != null ? !intermediateMaTerm.equals(maDTO.intermediateMaTerm) : maDTO.intermediateMaTerm != null)
            return false;
        if (intermediateMaTermSynonym != null ? !intermediateMaTermSynonym.equals(maDTO.intermediateMaTermSynonym) : maDTO.intermediateMaTermSynonym != null)
            return false;
        if (childMpId != null ? !childMpId.equals(maDTO.childMpId) : maDTO.childMpId != null) return false;
        if (childMpTerm != null ? !childMpTerm.equals(maDTO.childMpTerm) : maDTO.childMpTerm != null) return false;
        if (childMpTermSynonym != null ? !childMpTermSynonym.equals(maDTO.childMpTermSynonym) : maDTO.childMpTermSynonym != null)
            return false;
        if (text != null ? !text.equals(maDTO.text) : maDTO.text != null) return false;
        if (autoSuggest != null ? !autoSuggest.equals(maDTO.autoSuggest) : maDTO.autoSuggest != null) return false;
        if (geneQf != null ? !geneQf.equals(maDTO.geneQf) : maDTO.geneQf != null) return false;
        if (mpQf != null ? !mpQf.equals(maDTO.mpQf) : maDTO.mpQf != null) return false;
        if (diseaseQf != null ? !diseaseQf.equals(maDTO.diseaseQf) : maDTO.diseaseQf != null) return false;
        return !(maQf != null ? !maQf.equals(maDTO.maQf) : maDTO.maQf != null);

    }

    @Override
    public int hashCode() {
        int result = dataType != null ? dataType.hashCode() : 0;
        result = 31 * result + (maId != null ? maId.hashCode() : 0);
        result = 31 * result + (uberonIds != null ? uberonIds.hashCode() : 0);
        result = 31 * result + (all_ae_mapped_uberonIds != null ? all_ae_mapped_uberonIds.hashCode() : 0);
        result = 31 * result + (efoIds != null ? efoIds.hashCode() : 0);
        result = 31 * result + (all_ae_mapped_efoIds != null ? all_ae_mapped_efoIds.hashCode() : 0);
        result = 31 * result + (maTerm != null ? maTerm.hashCode() : 0);
        result = 31 * result + (maTermSynonym != null ? maTermSynonym.hashCode() : 0);
        result = 31 * result + (altMaIds != null ? altMaIds.hashCode() : 0);
        result = 31 * result + (ontologySubset != null ? ontologySubset.hashCode() : 0);
        result = 31 * result + (parentMaId != null ? parentMaId.hashCode() : 0);
        result = 31 * result + (parentMaTerm != null ? parentMaTerm.hashCode() : 0);
        result = 31 * result + (parentMaTermSynonym != null ? parentMaTermSynonym.hashCode() : 0);
        result = 31 * result + (childMaId != null ? childMaId.hashCode() : 0);
        result = 31 * result + (childMaTerm != null ? childMaTerm.hashCode() : 0);
        result = 31 * result + (childMaTermSynonym != null ? childMaTermSynonym.hashCode() : 0);
        result = 31 * result + (childMaIdTerm != null ? childMaIdTerm.hashCode() : 0);
        result = 31 * result + (topLevelMaId != null ? topLevelMaId.hashCode() : 0);
        result = 31 * result + (topLevelMaTerm != null ? topLevelMaTerm.hashCode() : 0);
        result = 31 * result + (selectedTopLevelMaId != null ? selectedTopLevelMaId.hashCode() : 0);
        result = 31 * result + (selectedTopLevelMaTerm != null ? selectedTopLevelMaTerm.hashCode() : 0);
        result = 31 * result + (selectedTopLevelMaTermSynonym != null ? selectedTopLevelMaTermSynonym.hashCode() : 0);
        result = 31 * result + (hpId != null ? hpId.hashCode() : 0);
        result = 31 * result + (hpTerm != null ? hpTerm.hashCode() : 0);
        result = 31 * result + (goId != null ? goId.hashCode() : 0);
        result = 31 * result + (pValue != null ? pValue.hashCode() : 0);
        result = 31 * result + (mgiAccessionId != null ? mgiAccessionId.hashCode() : 0);
        result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
        result = 31 * result + (markerName != null ? markerName.hashCode() : 0);
        result = 31 * result + (markerSynonym != null ? markerSynonym.hashCode() : 0);
        result = 31 * result + (markerType != null ? markerType.hashCode() : 0);
        result = 31 * result + (humanGeneSymbol != null ? humanGeneSymbol.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (imitsPhenotypeStarted != null ? imitsPhenotypeStarted.hashCode() : 0);
        result = 31 * result + (imitsPhenotypeComplete != null ? imitsPhenotypeComplete.hashCode() : 0);
        result = 31 * result + (imitsPhenotypeStatus != null ? imitsPhenotypeStatus.hashCode() : 0);
        result = 31 * result + (latestProductionCentre != null ? latestProductionCentre.hashCode() : 0);
        result = 31 * result + (latestPhenotypingCentre != null ? latestPhenotypingCentre.hashCode() : 0);
        result = 31 * result + (latestPhenotypeStatus != null ? latestPhenotypeStatus.hashCode() : 0);
        result = 31 * result + (legacyPhenotypeStatus != null ? legacyPhenotypeStatus.hashCode() : 0);
        result = 31 * result + (alleleName != null ? alleleName.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (diseaseId != null ? diseaseId.hashCode() : 0);
        result = 31 * result + (diseaseSource != null ? diseaseSource.hashCode() : 0);
        result = 31 * result + (diseaseTerm != null ? diseaseTerm.hashCode() : 0);
        result = 31 * result + (diseaseAlts != null ? diseaseAlts.hashCode() : 0);
        result = 31 * result + (diseaseClasses != null ? diseaseClasses.hashCode() : 0);
        result = 31 * result + (diseaseHumanPhenotypes != null ? diseaseHumanPhenotypes.hashCode() : 0);
        result = 31 * result + (humanCurated != null ? humanCurated.hashCode() : 0);
        result = 31 * result + (mouseCurated != null ? mouseCurated.hashCode() : 0);
        result = 31 * result + (mgiPredicted != null ? mgiPredicted.hashCode() : 0);
        result = 31 * result + (impcPredicted != null ? impcPredicted.hashCode() : 0);
        result = 31 * result + (mgiPredictedKnownGene != null ? mgiPredictedKnownGene.hashCode() : 0);
        result = 31 * result + (impcPredictedKnownGene != null ? impcPredictedKnownGene.hashCode() : 0);
        result = 31 * result + (mgiNovelPredictedInLocus != null ? mgiNovelPredictedInLocus.hashCode() : 0);
        result = 31 * result + (impcNovelPredictedInLocus != null ? impcNovelPredictedInLocus.hashCode() : 0);
        result = 31 * result + (annotationTermId != null ? annotationTermId.hashCode() : 0);
        result = 31 * result + (annotationTermName != null ? annotationTermName.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (accession != null ? accession.hashCode() : 0);
        result = 31 * result + (expName != null ? expName.hashCode() : 0);
        result = 31 * result + (largeThumbnailFilePath != null ? largeThumbnailFilePath.hashCode() : 0);
        result = 31 * result + (smallThumbnailFilePath != null ? smallThumbnailFilePath.hashCode() : 0);
        result = 31 * result + (inferredMaTermId != null ? inferredMaTermId.hashCode() : 0);
        result = 31 * result + (inferredMaTermName != null ? inferredMaTermName.hashCode() : 0);
        result = 31 * result + (annotatedHigherLevelMaTermId != null ? annotatedHigherLevelMaTermId.hashCode() : 0);
        result = 31 * result + (annotatedHigherLevelMaTermName != null ? annotatedHigherLevelMaTermName.hashCode() : 0);
        result = 31 * result + (annotatedHigherLevelMpTermId != null ? annotatedHigherLevelMpTermId.hashCode() : 0);
        result = 31 * result + (annotatedHigherLevelMpTermName != null ? annotatedHigherLevelMpTermName.hashCode() : 0);
        result = 31 * result + (inferredHigherLevelMaTermId != null ? inferredHigherLevelMaTermId.hashCode() : 0);
        result = 31 * result + (inferredHigherLevelMaTermName != null ? inferredHigherLevelMaTermName.hashCode() : 0);
        result = 31 * result + (annotatedOrInferredHigherLevelMaTermName != null ? annotatedOrInferredHigherLevelMaTermName.hashCode() : 0);
        result = 31 * result + (annotatedOrInferredHigherLevelMaTermId != null ? annotatedOrInferredHigherLevelMaTermId.hashCode() : 0);
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (sangerSymbol != null ? sangerSymbol.hashCode() : 0);
        result = 31 * result + (geneName != null ? geneName.hashCode() : 0);
        result = 31 * result + (subtype != null ? subtype.hashCode() : 0);
        result = 31 * result + (geneSynonyms != null ? geneSynonyms.hashCode() : 0);
        result = 31 * result + (maTermId != null ? maTermId.hashCode() : 0);
        result = 31 * result + (maTermName != null ? maTermName.hashCode() : 0);
        result = 31 * result + (mpTermId != null ? mpTermId.hashCode() : 0);
        result = 31 * result + (mpTermName != null ? mpTermName.hashCode() : 0);
        result = 31 * result + (expNameExp != null ? expNameExp.hashCode() : 0);
        result = 31 * result + (symbolGene != null ? symbolGene.hashCode() : 0);
        result = 31 * result + (topLevel != null ? topLevel.hashCode() : 0);
        result = 31 * result + (alleleSymbol != null ? alleleSymbol.hashCode() : 0);
        result = 31 * result + (alleleId != null ? alleleId.hashCode() : 0);
        result = 31 * result + (strainName != null ? strainName.hashCode() : 0);
        result = 31 * result + (strainId != null ? strainId.hashCode() : 0);
        result = 31 * result + (geneticBackground != null ? geneticBackground.hashCode() : 0);
        result = 31 * result + (pipelineName != null ? pipelineName.hashCode() : 0);
        result = 31 * result + (pipelineStableId != null ? pipelineStableId.hashCode() : 0);
        result = 31 * result + (pipelineStableKey != null ? pipelineStableKey.hashCode() : 0);
        result = 31 * result + (procedureName != null ? procedureName.hashCode() : 0);
        result = 31 * result + (procedureStableId != null ? procedureStableId.hashCode() : 0);
        result = 31 * result + (procedureStableKey != null ? procedureStableKey.hashCode() : 0);
        result = 31 * result + (parameterName != null ? parameterName.hashCode() : 0);
        result = 31 * result + (parameterStableId != null ? parameterStableId.hashCode() : 0);
        result = 31 * result + (parameterStableKey != null ? parameterStableKey.hashCode() : 0);
        result = 31 * result + (mpId != null ? mpId.hashCode() : 0);
        result = 31 * result + (mpTerm != null ? mpTerm.hashCode() : 0);
        result = 31 * result + (mpTermSynonym != null ? mpTermSynonym.hashCode() : 0);
        result = 31 * result + (topLevelMpId != null ? topLevelMpId.hashCode() : 0);
        result = 31 * result + (topLevelMpTerm != null ? topLevelMpTerm.hashCode() : 0);
        result = 31 * result + (topLevelMpTermSynonym != null ? topLevelMpTermSynonym.hashCode() : 0);
        result = 31 * result + (intermediateMaId != null ? intermediateMaId.hashCode() : 0);
        result = 31 * result + (intermediateMaTerm != null ? intermediateMaTerm.hashCode() : 0);
        result = 31 * result + (intermediateMaTermSynonym != null ? intermediateMaTermSynonym.hashCode() : 0);
        result = 31 * result + (childMpId != null ? childMpId.hashCode() : 0);
        result = 31 * result + (childMpTerm != null ? childMpTerm.hashCode() : 0);
        result = 31 * result + (childMpTermSynonym != null ? childMpTermSynonym.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (autoSuggest != null ? autoSuggest.hashCode() : 0);
        result = 31 * result + (geneQf != null ? geneQf.hashCode() : 0);
        result = 31 * result + (mpQf != null ? mpQf.hashCode() : 0);
        result = 31 * result + (diseaseQf != null ? diseaseQf.hashCode() : 0);
        result = 31 * result + (maQf != null ? maQf.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MaDTO{" +
                "dataType='" + dataType + '\'' +
                ", maId='" + maId + '\'' +
                ", uberonIds=" + uberonIds +
                ", all_ae_mapped_uberonIds=" + all_ae_mapped_uberonIds +
                ", efoIds=" + efoIds +
                ", all_ae_mapped_efoIds=" + all_ae_mapped_efoIds +
                ", maTerm='" + maTerm + '\'' +
                ", maTermSynonym=" + maTermSynonym +
                ", altMaIds=" + altMaIds +
                ", ontologySubset=" + ontologySubset +
                ", parentMaId=" + parentMaId +
                ", parentMaTerm=" + parentMaTerm +
                ", parentMaTermSynonym=" + parentMaTermSynonym +
                ", childMaId=" + childMaId +
                ", childMaTerm=" + childMaTerm +
                ", childMaTermSynonym=" + childMaTermSynonym +
                ", childMaIdTerm=" + childMaIdTerm +
                ", topLevelMaId=" + topLevelMaId +
                ", topLevelMaTerm=" + topLevelMaTerm +
                ", selectedTopLevelMaId=" + selectedTopLevelMaId +
                ", selectedTopLevelMaTerm=" + selectedTopLevelMaTerm +
                ", selectedTopLevelMaTermSynonym=" + selectedTopLevelMaTermSynonym +
                ", hpId=" + hpId +
                ", hpTerm=" + hpTerm +
                ", goId=" + goId +
                ", pValue=" + pValue +
                ", mgiAccessionId=" + mgiAccessionId +
                ", markerSymbol=" + markerSymbol +
                ", markerName=" + markerName +
                ", markerSynonym=" + markerSynonym +
                ", markerType=" + markerType +
                ", humanGeneSymbol=" + humanGeneSymbol +
                ", status=" + status +
                ", imitsPhenotypeStarted=" + imitsPhenotypeStarted +
                ", imitsPhenotypeComplete=" + imitsPhenotypeComplete +
                ", imitsPhenotypeStatus=" + imitsPhenotypeStatus +
                ", latestProductionCentre=" + latestProductionCentre +
                ", latestPhenotypingCentre=" + latestPhenotypingCentre +
                ", latestPhenotypeStatus=" + latestPhenotypeStatus +
                ", legacyPhenotypeStatus=" + legacyPhenotypeStatus +
                ", alleleName=" + alleleName +
                ", type=" + type +
                ", diseaseId=" + diseaseId +
                ", diseaseSource=" + diseaseSource +
                ", diseaseTerm=" + diseaseTerm +
                ", diseaseAlts=" + diseaseAlts +
                ", diseaseClasses=" + diseaseClasses +
                ", diseaseHumanPhenotypes=" + diseaseHumanPhenotypes +
                ", humanCurated=" + humanCurated +
                ", mouseCurated=" + mouseCurated +
                ", mgiPredicted=" + mgiPredicted +
                ", impcPredicted=" + impcPredicted +
                ", mgiPredictedKnownGene=" + mgiPredictedKnownGene +
                ", impcPredictedKnownGene=" + impcPredictedKnownGene +
                ", mgiNovelPredictedInLocus=" + mgiNovelPredictedInLocus +
                ", impcNovelPredictedInLocus=" + impcNovelPredictedInLocus +
                ", annotationTermId=" + annotationTermId +
                ", annotationTermName=" + annotationTermName +
                ", name=" + name +
                ", accession=" + accession +
                ", expName=" + expName +
                ", largeThumbnailFilePath=" + largeThumbnailFilePath +
                ", smallThumbnailFilePath=" + smallThumbnailFilePath +
                ", inferredMaTermId=" + inferredMaTermId +
                ", inferredMaTermName=" + inferredMaTermName +
                ", annotatedHigherLevelMaTermId=" + annotatedHigherLevelMaTermId +
                ", annotatedHigherLevelMaTermName=" + annotatedHigherLevelMaTermName +
                ", annotatedHigherLevelMpTermId=" + annotatedHigherLevelMpTermId +
                ", annotatedHigherLevelMpTermName=" + annotatedHigherLevelMpTermName +
                ", inferredHigherLevelMaTermId=" + inferredHigherLevelMaTermId +
                ", inferredHigherLevelMaTermName=" + inferredHigherLevelMaTermName +
                ", annotatedOrInferredHigherLevelMaTermName=" + annotatedOrInferredHigherLevelMaTermName +
                ", annotatedOrInferredHigherLevelMaTermId=" + annotatedOrInferredHigherLevelMaTermId +
                ", symbol=" + symbol +
                ", sangerSymbol=" + sangerSymbol +
                ", geneName=" + geneName +
                ", subtype=" + subtype +
                ", geneSynonyms=" + geneSynonyms +
                ", maTermId=" + maTermId +
                ", maTermName=" + maTermName +
                ", mpTermId=" + mpTermId +
                ", mpTermName=" + mpTermName +
                ", expNameExp=" + expNameExp +
                ", symbolGene=" + symbolGene +
                ", topLevel=" + topLevel +
                ", alleleSymbol=" + alleleSymbol +
                ", alleleId=" + alleleId +
                ", strainName=" + strainName +
                ", strainId=" + strainId +
                ", geneticBackground='" + geneticBackground + '\'' +
                ", pipelineName=" + pipelineName +
                ", pipelineStableId=" + pipelineStableId +
                ", pipelineStableKey=" + pipelineStableKey +
                ", procedureName=" + procedureName +
                ", procedureStableId=" + procedureStableId +
                ", procedureStableKey=" + procedureStableKey +
                ", parameterName=" + parameterName +
                ", parameterStableId=" + parameterStableId +
                ", parameterStableKey=" + parameterStableKey +
                ", mpId=" + mpId +
                ", mpTerm=" + mpTerm +
                ", mpTermSynonym=" + mpTermSynonym +
                ", topLevelMpId=" + topLevelMpId +
                ", topLevelMpTerm=" + topLevelMpTerm +
                ", topLevelMpTermSynonym=" + topLevelMpTermSynonym +
                ", intermediateMaId=" + intermediateMaId +
                ", intermediateMaTerm=" + intermediateMaTerm +
                ", intermediateMaTermSynonym=" + intermediateMaTermSynonym +
                ", childMpId=" + childMpId +
                ", childMpTerm=" + childMpTerm +
                ", childMpTermSynonym=" + childMpTermSynonym +
                ", text=" + text +
                ", autoSuggest=" + autoSuggest +
                ", geneQf=" + geneQf +
                ", mpQf=" + mpQf +
                ", diseaseQf=" + diseaseQf +
                ", maQf=" + maQf +
                '}';
    }


}
