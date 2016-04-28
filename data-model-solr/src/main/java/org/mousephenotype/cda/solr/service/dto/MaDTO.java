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
    public static final String MA_NODE_ID = "ma_node_id";
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
    
    //OntologyBrowser
	public static final String SEARCH_TERM_JSON = MpDTO.SEARCH_TERM_JSON;
	public static final String CHILDREN_JSON = MpDTO.CHILDREN_JSON;
	public static final String SCROLL_NODE = MpDTO.SCROLL_NODE;


    @Field(DATA_TYPE)
    private String dataType;

    @Field(MA_ID)
    private String maId;

	@Field(SEARCH_TERM_JSON)
	private String searchTermJson;

	@Field(CHILDREN_JSON)
	private String childrenJson;
	
	@Field(SCROLL_NODE)
	private String scrollNode;
    
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

    @Field(MA_NODE_ID)
    private List<Integer> maNodeId;

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

    public List<Integer> getMaNodeId() {
        return maNodeId;
    }

    public void setMaNodeId(List<Integer> maNodeId) {
        this.maNodeId = maNodeId;
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

    public String getSearchTermJson() {
		return searchTermJson;
	}

	public void setSearchTermJson(String searchTermJson) {
		this.searchTermJson = searchTermJson;
	}

	public String getChildrenJson() {
		return childrenJson;
	}

	public void setChildrenJson(String childrenJson) {
		this.childrenJson = childrenJson;
	}

	public String getScrollNode() {
		return scrollNode;
	}

	public void setScrollNode(String scrollNode) {
		this.scrollNode = scrollNode;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MaDTO maDTO = (MaDTO) o;

        if (!dataType.equals(maDTO.dataType)) return false;
        if (!maId.equals(maDTO.maId)) return false;
        if (!searchTermJson.equals(maDTO.searchTermJson)) return false;
        if (!childrenJson.equals(maDTO.childrenJson)) return false;
        if (!scrollNode.equals(maDTO.scrollNode)) return false;
        if (!uberonIds.equals(maDTO.uberonIds)) return false;
        if (!all_ae_mapped_uberonIds.equals(maDTO.all_ae_mapped_uberonIds)) return false;
        if (!efoIds.equals(maDTO.efoIds)) return false;
        if (!all_ae_mapped_efoIds.equals(maDTO.all_ae_mapped_efoIds)) return false;
        if (!maTerm.equals(maDTO.maTerm)) return false;
        if (!maTermSynonym.equals(maDTO.maTermSynonym)) return false;
        if (!maNodeId.equals(maDTO.maNodeId)) return false;
        if (!altMaIds.equals(maDTO.altMaIds)) return false;
        if (!ontologySubset.equals(maDTO.ontologySubset)) return false;
        if (!parentMaId.equals(maDTO.parentMaId)) return false;
        if (!parentMaTerm.equals(maDTO.parentMaTerm)) return false;
        if (!parentMaTermSynonym.equals(maDTO.parentMaTermSynonym)) return false;
        if (!childMaId.equals(maDTO.childMaId)) return false;
        if (!childMaTerm.equals(maDTO.childMaTerm)) return false;
        if (!childMaTermSynonym.equals(maDTO.childMaTermSynonym)) return false;
        if (!childMaIdTerm.equals(maDTO.childMaIdTerm)) return false;
        if (!topLevelMaId.equals(maDTO.topLevelMaId)) return false;
        if (!topLevelMaTerm.equals(maDTO.topLevelMaTerm)) return false;
        if (!selectedTopLevelMaId.equals(maDTO.selectedTopLevelMaId)) return false;
        if (!selectedTopLevelMaTerm.equals(maDTO.selectedTopLevelMaTerm)) return false;
        if (!selectedTopLevelMaTermSynonym.equals(maDTO.selectedTopLevelMaTermSynonym)) return false;
        if (!hpId.equals(maDTO.hpId)) return false;
        if (!hpTerm.equals(maDTO.hpTerm)) return false;
        if (!goId.equals(maDTO.goId)) return false;
        if (!pValue.equals(maDTO.pValue)) return false;
        if (!mgiAccessionId.equals(maDTO.mgiAccessionId)) return false;
        if (!markerSymbol.equals(maDTO.markerSymbol)) return false;
        if (!markerName.equals(maDTO.markerName)) return false;
        if (!markerSynonym.equals(maDTO.markerSynonym)) return false;
        if (!markerType.equals(maDTO.markerType)) return false;
        if (!humanGeneSymbol.equals(maDTO.humanGeneSymbol)) return false;
        if (!status.equals(maDTO.status)) return false;
        if (!imitsPhenotypeStarted.equals(maDTO.imitsPhenotypeStarted)) return false;
        if (!imitsPhenotypeComplete.equals(maDTO.imitsPhenotypeComplete)) return false;
        if (!imitsPhenotypeStatus.equals(maDTO.imitsPhenotypeStatus)) return false;
        if (!latestProductionCentre.equals(maDTO.latestProductionCentre)) return false;
        if (!latestPhenotypingCentre.equals(maDTO.latestPhenotypingCentre)) return false;
        if (!latestPhenotypeStatus.equals(maDTO.latestPhenotypeStatus)) return false;
        if (!legacyPhenotypeStatus.equals(maDTO.legacyPhenotypeStatus)) return false;
        if (!alleleName.equals(maDTO.alleleName)) return false;
        if (!type.equals(maDTO.type)) return false;
        if (!diseaseId.equals(maDTO.diseaseId)) return false;
        if (!diseaseSource.equals(maDTO.diseaseSource)) return false;
        if (!diseaseTerm.equals(maDTO.diseaseTerm)) return false;
        if (!diseaseAlts.equals(maDTO.diseaseAlts)) return false;
        if (!diseaseClasses.equals(maDTO.diseaseClasses)) return false;
        if (!diseaseHumanPhenotypes.equals(maDTO.diseaseHumanPhenotypes)) return false;
        if (!humanCurated.equals(maDTO.humanCurated)) return false;
        if (!mouseCurated.equals(maDTO.mouseCurated)) return false;
        if (!mgiPredicted.equals(maDTO.mgiPredicted)) return false;
        if (!impcPredicted.equals(maDTO.impcPredicted)) return false;
        if (!mgiPredictedKnownGene.equals(maDTO.mgiPredictedKnownGene)) return false;
        if (!impcPredictedKnownGene.equals(maDTO.impcPredictedKnownGene)) return false;
        if (!mgiNovelPredictedInLocus.equals(maDTO.mgiNovelPredictedInLocus)) return false;
        if (!impcNovelPredictedInLocus.equals(maDTO.impcNovelPredictedInLocus)) return false;
        if (!annotationTermId.equals(maDTO.annotationTermId)) return false;
        if (!annotationTermName.equals(maDTO.annotationTermName)) return false;
        if (!name.equals(maDTO.name)) return false;
        if (!accession.equals(maDTO.accession)) return false;
        if (!expName.equals(maDTO.expName)) return false;
        if (!largeThumbnailFilePath.equals(maDTO.largeThumbnailFilePath)) return false;
        if (!smallThumbnailFilePath.equals(maDTO.smallThumbnailFilePath)) return false;
        if (!inferredMaTermId.equals(maDTO.inferredMaTermId)) return false;
        if (!inferredMaTermName.equals(maDTO.inferredMaTermName)) return false;
        if (!annotatedHigherLevelMaTermId.equals(maDTO.annotatedHigherLevelMaTermId)) return false;
        if (!annotatedHigherLevelMaTermName.equals(maDTO.annotatedHigherLevelMaTermName)) return false;
        if (!annotatedHigherLevelMpTermId.equals(maDTO.annotatedHigherLevelMpTermId)) return false;
        if (!annotatedHigherLevelMpTermName.equals(maDTO.annotatedHigherLevelMpTermName)) return false;
        if (!inferredHigherLevelMaTermId.equals(maDTO.inferredHigherLevelMaTermId)) return false;
        if (!inferredHigherLevelMaTermName.equals(maDTO.inferredHigherLevelMaTermName)) return false;
        if (!annotatedOrInferredHigherLevelMaTermName.equals(maDTO.annotatedOrInferredHigherLevelMaTermName))
            return false;
        if (!annotatedOrInferredHigherLevelMaTermId.equals(maDTO.annotatedOrInferredHigherLevelMaTermId)) return false;
        if (!symbol.equals(maDTO.symbol)) return false;
        if (!sangerSymbol.equals(maDTO.sangerSymbol)) return false;
        if (!geneName.equals(maDTO.geneName)) return false;
        if (!subtype.equals(maDTO.subtype)) return false;
        if (!geneSynonyms.equals(maDTO.geneSynonyms)) return false;
        if (!maTermId.equals(maDTO.maTermId)) return false;
        if (!maTermName.equals(maDTO.maTermName)) return false;
        if (!mpTermId.equals(maDTO.mpTermId)) return false;
        if (!mpTermName.equals(maDTO.mpTermName)) return false;
        if (!expNameExp.equals(maDTO.expNameExp)) return false;
        if (!symbolGene.equals(maDTO.symbolGene)) return false;
        if (!topLevel.equals(maDTO.topLevel)) return false;
        if (!alleleSymbol.equals(maDTO.alleleSymbol)) return false;
        if (!alleleId.equals(maDTO.alleleId)) return false;
        if (!strainName.equals(maDTO.strainName)) return false;
        if (!strainId.equals(maDTO.strainId)) return false;
        if (!geneticBackground.equals(maDTO.geneticBackground)) return false;
        if (!pipelineName.equals(maDTO.pipelineName)) return false;
        if (!pipelineStableId.equals(maDTO.pipelineStableId)) return false;
        if (!pipelineStableKey.equals(maDTO.pipelineStableKey)) return false;
        if (!procedureName.equals(maDTO.procedureName)) return false;
        if (!procedureStableId.equals(maDTO.procedureStableId)) return false;
        if (!procedureStableKey.equals(maDTO.procedureStableKey)) return false;
        if (!parameterName.equals(maDTO.parameterName)) return false;
        if (!parameterStableId.equals(maDTO.parameterStableId)) return false;
        if (!parameterStableKey.equals(maDTO.parameterStableKey)) return false;
        if (!mpId.equals(maDTO.mpId)) return false;
        if (!mpTerm.equals(maDTO.mpTerm)) return false;
        if (!mpTermSynonym.equals(maDTO.mpTermSynonym)) return false;
        if (!topLevelMpId.equals(maDTO.topLevelMpId)) return false;
        if (!topLevelMpTerm.equals(maDTO.topLevelMpTerm)) return false;
        if (!topLevelMpTermSynonym.equals(maDTO.topLevelMpTermSynonym)) return false;
        if (!intermediateMaId.equals(maDTO.intermediateMaId)) return false;
        if (!intermediateMaTerm.equals(maDTO.intermediateMaTerm)) return false;
        if (!intermediateMaTermSynonym.equals(maDTO.intermediateMaTermSynonym)) return false;
        if (!childMpId.equals(maDTO.childMpId)) return false;
        if (!childMpTerm.equals(maDTO.childMpTerm)) return false;
        if (!childMpTermSynonym.equals(maDTO.childMpTermSynonym)) return false;
        if (!text.equals(maDTO.text)) return false;
        if (!autoSuggest.equals(maDTO.autoSuggest)) return false;
        if (!geneQf.equals(maDTO.geneQf)) return false;
        if (!mpQf.equals(maDTO.mpQf)) return false;
        if (!diseaseQf.equals(maDTO.diseaseQf)) return false;
        return maQf.equals(maDTO.maQf);

    }

    @Override
    public int hashCode() {
        int result = dataType.hashCode();
        result = 31 * result + maId.hashCode();
        result = 31 * result + searchTermJson.hashCode();
        result = 31 * result + childrenJson.hashCode();
        result = 31 * result + scrollNode.hashCode();
        result = 31 * result + uberonIds.hashCode();
        result = 31 * result + all_ae_mapped_uberonIds.hashCode();
        result = 31 * result + efoIds.hashCode();
        result = 31 * result + all_ae_mapped_efoIds.hashCode();
        result = 31 * result + maTerm.hashCode();
        result = 31 * result + maTermSynonym.hashCode();
        result = 31 * result + maNodeId.hashCode();
        result = 31 * result + altMaIds.hashCode();
        result = 31 * result + ontologySubset.hashCode();
        result = 31 * result + parentMaId.hashCode();
        result = 31 * result + parentMaTerm.hashCode();
        result = 31 * result + parentMaTermSynonym.hashCode();
        result = 31 * result + childMaId.hashCode();
        result = 31 * result + childMaTerm.hashCode();
        result = 31 * result + childMaTermSynonym.hashCode();
        result = 31 * result + childMaIdTerm.hashCode();
        result = 31 * result + topLevelMaId.hashCode();
        result = 31 * result + topLevelMaTerm.hashCode();
        result = 31 * result + selectedTopLevelMaId.hashCode();
        result = 31 * result + selectedTopLevelMaTerm.hashCode();
        result = 31 * result + selectedTopLevelMaTermSynonym.hashCode();
        result = 31 * result + hpId.hashCode();
        result = 31 * result + hpTerm.hashCode();
        result = 31 * result + goId.hashCode();
        result = 31 * result + pValue.hashCode();
        result = 31 * result + mgiAccessionId.hashCode();
        result = 31 * result + markerSymbol.hashCode();
        result = 31 * result + markerName.hashCode();
        result = 31 * result + markerSynonym.hashCode();
        result = 31 * result + markerType.hashCode();
        result = 31 * result + humanGeneSymbol.hashCode();
        result = 31 * result + status.hashCode();
        result = 31 * result + imitsPhenotypeStarted.hashCode();
        result = 31 * result + imitsPhenotypeComplete.hashCode();
        result = 31 * result + imitsPhenotypeStatus.hashCode();
        result = 31 * result + latestProductionCentre.hashCode();
        result = 31 * result + latestPhenotypingCentre.hashCode();
        result = 31 * result + latestPhenotypeStatus.hashCode();
        result = 31 * result + legacyPhenotypeStatus.hashCode();
        result = 31 * result + alleleName.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + diseaseId.hashCode();
        result = 31 * result + diseaseSource.hashCode();
        result = 31 * result + diseaseTerm.hashCode();
        result = 31 * result + diseaseAlts.hashCode();
        result = 31 * result + diseaseClasses.hashCode();
        result = 31 * result + diseaseHumanPhenotypes.hashCode();
        result = 31 * result + humanCurated.hashCode();
        result = 31 * result + mouseCurated.hashCode();
        result = 31 * result + mgiPredicted.hashCode();
        result = 31 * result + impcPredicted.hashCode();
        result = 31 * result + mgiPredictedKnownGene.hashCode();
        result = 31 * result + impcPredictedKnownGene.hashCode();
        result = 31 * result + mgiNovelPredictedInLocus.hashCode();
        result = 31 * result + impcNovelPredictedInLocus.hashCode();
        result = 31 * result + annotationTermId.hashCode();
        result = 31 * result + annotationTermName.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + accession.hashCode();
        result = 31 * result + expName.hashCode();
        result = 31 * result + largeThumbnailFilePath.hashCode();
        result = 31 * result + smallThumbnailFilePath.hashCode();
        result = 31 * result + inferredMaTermId.hashCode();
        result = 31 * result + inferredMaTermName.hashCode();
        result = 31 * result + annotatedHigherLevelMaTermId.hashCode();
        result = 31 * result + annotatedHigherLevelMaTermName.hashCode();
        result = 31 * result + annotatedHigherLevelMpTermId.hashCode();
        result = 31 * result + annotatedHigherLevelMpTermName.hashCode();
        result = 31 * result + inferredHigherLevelMaTermId.hashCode();
        result = 31 * result + inferredHigherLevelMaTermName.hashCode();
        result = 31 * result + annotatedOrInferredHigherLevelMaTermName.hashCode();
        result = 31 * result + annotatedOrInferredHigherLevelMaTermId.hashCode();
        result = 31 * result + symbol.hashCode();
        result = 31 * result + sangerSymbol.hashCode();
        result = 31 * result + geneName.hashCode();
        result = 31 * result + subtype.hashCode();
        result = 31 * result + geneSynonyms.hashCode();
        result = 31 * result + maTermId.hashCode();
        result = 31 * result + maTermName.hashCode();
        result = 31 * result + mpTermId.hashCode();
        result = 31 * result + mpTermName.hashCode();
        result = 31 * result + expNameExp.hashCode();
        result = 31 * result + symbolGene.hashCode();
        result = 31 * result + topLevel.hashCode();
        result = 31 * result + alleleSymbol.hashCode();
        result = 31 * result + alleleId.hashCode();
        result = 31 * result + strainName.hashCode();
        result = 31 * result + strainId.hashCode();
        result = 31 * result + geneticBackground.hashCode();
        result = 31 * result + pipelineName.hashCode();
        result = 31 * result + pipelineStableId.hashCode();
        result = 31 * result + pipelineStableKey.hashCode();
        result = 31 * result + procedureName.hashCode();
        result = 31 * result + procedureStableId.hashCode();
        result = 31 * result + procedureStableKey.hashCode();
        result = 31 * result + parameterName.hashCode();
        result = 31 * result + parameterStableId.hashCode();
        result = 31 * result + parameterStableKey.hashCode();
        result = 31 * result + mpId.hashCode();
        result = 31 * result + mpTerm.hashCode();
        result = 31 * result + mpTermSynonym.hashCode();
        result = 31 * result + topLevelMpId.hashCode();
        result = 31 * result + topLevelMpTerm.hashCode();
        result = 31 * result + topLevelMpTermSynonym.hashCode();
        result = 31 * result + intermediateMaId.hashCode();
        result = 31 * result + intermediateMaTerm.hashCode();
        result = 31 * result + intermediateMaTermSynonym.hashCode();
        result = 31 * result + childMpId.hashCode();
        result = 31 * result + childMpTerm.hashCode();
        result = 31 * result + childMpTermSynonym.hashCode();
        result = 31 * result + text.hashCode();
        result = 31 * result + autoSuggest.hashCode();
        result = 31 * result + geneQf.hashCode();
        result = 31 * result + mpQf.hashCode();
        result = 31 * result + diseaseQf.hashCode();
        result = 31 * result + maQf.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "MaDTO{" +
                "dataType='" + dataType + '\'' +
                ", maId='" + maId + '\'' +
                ", searchTermJson='" + searchTermJson + '\'' +
                ", childrenJson='" + childrenJson + '\'' +
                ", scrollNode='" + scrollNode + '\'' +
                ", uberonIds=" + uberonIds +
                ", all_ae_mapped_uberonIds=" + all_ae_mapped_uberonIds +
                ", efoIds=" + efoIds +
                ", all_ae_mapped_efoIds=" + all_ae_mapped_efoIds +
                ", maTerm='" + maTerm + '\'' +
                ", maTermSynonym=" + maTermSynonym +
                ", maNodeId=" + maNodeId +
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
