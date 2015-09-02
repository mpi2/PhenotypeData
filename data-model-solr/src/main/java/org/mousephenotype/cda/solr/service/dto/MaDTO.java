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
    public static final String UBERON_ID = "uberon_id";
    public static final String EFO_ID = "efo_id";
    public static final String MA_TERM_SYNONYM = "ma_term_synonym";
    public static final String ONTOLOGY_SUBSET = "ontology_subset";

    public static final String CHILD_MA_ID = "child_ma_id";
    public static final String CHILD_MA_TERM = "child_ma_term";
    public static final String CHILD_MA_TERM_SYNONYM = "child_ma_term_synonym";
    public static final String CHILD_MA_ID_TERM = "child_ma_idTerm";

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
    
    @Field(EFO_ID)
    private List<String> efoIds;

    @Field(MA_TERM)
    private String maTerm;

    @Field(MA_TERM_SYNONYM)
    private List<String> maTermSynonym;

    @Field(ONTOLOGY_SUBSET)
    private List<String> ontologySubset;

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


    @Field(INTERMEDIATE_MP_ID)
    private List<String> intermediateMpId;

    @Field(INTERMEDIATE_MP_TERM)
    private List<String> intermediateMpTerm;

    @Field(INTERMEDIATE_MP_TERM_SYNONYM)
    private List<String> intermediateMpTermSynonym;

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

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getMaId() {
        return maId;
    }

    public void setMaId(String maId) {
        this.maId = maId;
    }

    public List<String> getUberonIds() {
        return uberonIds;
    }

    public void setUberonIds(List<String> uberonIds) {
        this.uberonIds = uberonIds;
    }
    
    public List<String> getEfoIds() {
        return uberonIds;
    }

    public void setEfoIds(List<String> efoIds) {
        this.efoIds = efoIds;
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

    public List<String> getIntermediateMpId() {
        return intermediateMpId;
    }

    public void setIntermediateMpId(List<String> intermediateMpId) {
        this.intermediateMpId = intermediateMpId;
    }

    public List<String> getIntermediateMpTerm() {
        return intermediateMpTerm;
    }

    public void setIntermediateMpTerm(List<String> intermediateMpTerm) {
        this.intermediateMpTerm = intermediateMpTerm;
    }

    public List<String> getIntermediateMpTermSynonym() {
        return intermediateMpTermSynonym;
    }

    public void setIntermediateMpTermSynonym(List<String> intermediateMpTermSynonym) {
        this.intermediateMpTermSynonym = intermediateMpTermSynonym;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accession == null) ? 0 : accession.hashCode());
		result = prime * result + ((alleleId == null) ? 0 : alleleId.hashCode());
		result = prime * result + ((alleleName == null) ? 0 : alleleName.hashCode());
		result = prime * result + ((alleleSymbol == null) ? 0 : alleleSymbol.hashCode());
		result = prime * result
				+ ((annotatedHigherLevelMaTermId == null) ? 0 : annotatedHigherLevelMaTermId.hashCode());
		result = prime * result
				+ ((annotatedHigherLevelMaTermName == null) ? 0 : annotatedHigherLevelMaTermName.hashCode());
		result = prime * result
				+ ((annotatedHigherLevelMpTermId == null) ? 0 : annotatedHigherLevelMpTermId.hashCode());
		result = prime * result
				+ ((annotatedHigherLevelMpTermName == null) ? 0 : annotatedHigherLevelMpTermName.hashCode());
		result = prime * result + ((annotatedOrInferredHigherLevelMaTermId == null) ? 0
				: annotatedOrInferredHigherLevelMaTermId.hashCode());
		result = prime * result + ((annotatedOrInferredHigherLevelMaTermName == null) ? 0
				: annotatedOrInferredHigherLevelMaTermName.hashCode());
		result = prime * result + ((annotationTermId == null) ? 0 : annotationTermId.hashCode());
		result = prime * result + ((annotationTermName == null) ? 0 : annotationTermName.hashCode());
		result = prime * result + ((autoSuggest == null) ? 0 : autoSuggest.hashCode());
		result = prime * result + ((childMaId == null) ? 0 : childMaId.hashCode());
		result = prime * result + ((childMaIdTerm == null) ? 0 : childMaIdTerm.hashCode());
		result = prime * result + ((childMaTerm == null) ? 0 : childMaTerm.hashCode());
		result = prime * result + ((childMaTermSynonym == null) ? 0 : childMaTermSynonym.hashCode());
		result = prime * result + ((childMpId == null) ? 0 : childMpId.hashCode());
		result = prime * result + ((childMpTerm == null) ? 0 : childMpTerm.hashCode());
		result = prime * result + ((childMpTermSynonym == null) ? 0 : childMpTermSynonym.hashCode());
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((diseaseAlts == null) ? 0 : diseaseAlts.hashCode());
		result = prime * result + ((diseaseClasses == null) ? 0 : diseaseClasses.hashCode());
		result = prime * result + ((diseaseHumanPhenotypes == null) ? 0 : diseaseHumanPhenotypes.hashCode());
		result = prime * result + ((diseaseId == null) ? 0 : diseaseId.hashCode());
		result = prime * result + ((diseaseQf == null) ? 0 : diseaseQf.hashCode());
		result = prime * result + ((diseaseSource == null) ? 0 : diseaseSource.hashCode());
		result = prime * result + ((diseaseTerm == null) ? 0 : diseaseTerm.hashCode());
		result = prime * result + ((efoIds == null) ? 0 : efoIds.hashCode());
		result = prime * result + ((expName == null) ? 0 : expName.hashCode());
		result = prime * result + ((expNameExp == null) ? 0 : expNameExp.hashCode());
		result = prime * result + ((geneName == null) ? 0 : geneName.hashCode());
		result = prime * result + ((geneQf == null) ? 0 : geneQf.hashCode());
		result = prime * result + ((geneSynonyms == null) ? 0 : geneSynonyms.hashCode());
		result = prime * result + ((geneticBackground == null) ? 0 : geneticBackground.hashCode());
		result = prime * result + ((goId == null) ? 0 : goId.hashCode());
		result = prime * result + ((hpId == null) ? 0 : hpId.hashCode());
		result = prime * result + ((hpTerm == null) ? 0 : hpTerm.hashCode());
		result = prime * result + ((humanCurated == null) ? 0 : humanCurated.hashCode());
		result = prime * result + ((humanGeneSymbol == null) ? 0 : humanGeneSymbol.hashCode());
		result = prime * result + ((imitsPhenotypeComplete == null) ? 0 : imitsPhenotypeComplete.hashCode());
		result = prime * result + ((imitsPhenotypeStarted == null) ? 0 : imitsPhenotypeStarted.hashCode());
		result = prime * result + ((imitsPhenotypeStatus == null) ? 0 : imitsPhenotypeStatus.hashCode());
		result = prime * result + ((impcNovelPredictedInLocus == null) ? 0 : impcNovelPredictedInLocus.hashCode());
		result = prime * result + ((impcPredicted == null) ? 0 : impcPredicted.hashCode());
		result = prime * result + ((impcPredictedKnownGene == null) ? 0 : impcPredictedKnownGene.hashCode());
		result = prime * result + ((inferredHigherLevelMaTermId == null) ? 0 : inferredHigherLevelMaTermId.hashCode());
		result = prime * result
				+ ((inferredHigherLevelMaTermName == null) ? 0 : inferredHigherLevelMaTermName.hashCode());
		result = prime * result + ((inferredMaTermId == null) ? 0 : inferredMaTermId.hashCode());
		result = prime * result + ((inferredMaTermName == null) ? 0 : inferredMaTermName.hashCode());
		result = prime * result + ((intermediateMpId == null) ? 0 : intermediateMpId.hashCode());
		result = prime * result + ((intermediateMpTerm == null) ? 0 : intermediateMpTerm.hashCode());
		result = prime * result + ((intermediateMpTermSynonym == null) ? 0 : intermediateMpTermSynonym.hashCode());
		result = prime * result + ((largeThumbnailFilePath == null) ? 0 : largeThumbnailFilePath.hashCode());
		result = prime * result + ((latestPhenotypeStatus == null) ? 0 : latestPhenotypeStatus.hashCode());
		result = prime * result + ((latestPhenotypingCentre == null) ? 0 : latestPhenotypingCentre.hashCode());
		result = prime * result + ((latestProductionCentre == null) ? 0 : latestProductionCentre.hashCode());
		result = prime * result + ((legacyPhenotypeStatus == null) ? 0 : legacyPhenotypeStatus.hashCode());
		result = prime * result + ((maId == null) ? 0 : maId.hashCode());
		result = prime * result + ((maQf == null) ? 0 : maQf.hashCode());
		result = prime * result + ((maTerm == null) ? 0 : maTerm.hashCode());
		result = prime * result + ((maTermId == null) ? 0 : maTermId.hashCode());
		result = prime * result + ((maTermName == null) ? 0 : maTermName.hashCode());
		result = prime * result + ((maTermSynonym == null) ? 0 : maTermSynonym.hashCode());
		result = prime * result + ((markerName == null) ? 0 : markerName.hashCode());
		result = prime * result + ((markerSymbol == null) ? 0 : markerSymbol.hashCode());
		result = prime * result + ((markerSynonym == null) ? 0 : markerSynonym.hashCode());
		result = prime * result + ((markerType == null) ? 0 : markerType.hashCode());
		result = prime * result + ((mgiAccessionId == null) ? 0 : mgiAccessionId.hashCode());
		result = prime * result + ((mgiNovelPredictedInLocus == null) ? 0 : mgiNovelPredictedInLocus.hashCode());
		result = prime * result + ((mgiPredicted == null) ? 0 : mgiPredicted.hashCode());
		result = prime * result + ((mgiPredictedKnownGene == null) ? 0 : mgiPredictedKnownGene.hashCode());
		result = prime * result + ((mouseCurated == null) ? 0 : mouseCurated.hashCode());
		result = prime * result + ((mpId == null) ? 0 : mpId.hashCode());
		result = prime * result + ((mpQf == null) ? 0 : mpQf.hashCode());
		result = prime * result + ((mpTerm == null) ? 0 : mpTerm.hashCode());
		result = prime * result + ((mpTermId == null) ? 0 : mpTermId.hashCode());
		result = prime * result + ((mpTermName == null) ? 0 : mpTermName.hashCode());
		result = prime * result + ((mpTermSynonym == null) ? 0 : mpTermSynonym.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ontologySubset == null) ? 0 : ontologySubset.hashCode());
		result = prime * result + ((pValue == null) ? 0 : pValue.hashCode());
		result = prime * result + ((parameterName == null) ? 0 : parameterName.hashCode());
		result = prime * result + ((parameterStableId == null) ? 0 : parameterStableId.hashCode());
		result = prime * result + ((parameterStableKey == null) ? 0 : parameterStableKey.hashCode());
		result = prime * result + ((pipelineName == null) ? 0 : pipelineName.hashCode());
		result = prime * result + ((pipelineStableId == null) ? 0 : pipelineStableId.hashCode());
		result = prime * result + ((pipelineStableKey == null) ? 0 : pipelineStableKey.hashCode());
		result = prime * result + ((procedureName == null) ? 0 : procedureName.hashCode());
		result = prime * result + ((procedureStableId == null) ? 0 : procedureStableId.hashCode());
		result = prime * result + ((procedureStableKey == null) ? 0 : procedureStableKey.hashCode());
		result = prime * result + ((sangerSymbol == null) ? 0 : sangerSymbol.hashCode());
		result = prime * result + ((selectedTopLevelMaId == null) ? 0 : selectedTopLevelMaId.hashCode());
		result = prime * result + ((selectedTopLevelMaTerm == null) ? 0 : selectedTopLevelMaTerm.hashCode());
		result = prime * result
				+ ((selectedTopLevelMaTermSynonym == null) ? 0 : selectedTopLevelMaTermSynonym.hashCode());
		result = prime * result + ((smallThumbnailFilePath == null) ? 0 : smallThumbnailFilePath.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((strainId == null) ? 0 : strainId.hashCode());
		result = prime * result + ((strainName == null) ? 0 : strainName.hashCode());
		result = prime * result + ((subtype == null) ? 0 : subtype.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((symbolGene == null) ? 0 : symbolGene.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		result = prime * result + ((topLevel == null) ? 0 : topLevel.hashCode());
		result = prime * result + ((topLevelMaId == null) ? 0 : topLevelMaId.hashCode());
		result = prime * result + ((topLevelMaTerm == null) ? 0 : topLevelMaTerm.hashCode());
		result = prime * result + ((topLevelMpId == null) ? 0 : topLevelMpId.hashCode());
		result = prime * result + ((topLevelMpTerm == null) ? 0 : topLevelMpTerm.hashCode());
		result = prime * result + ((topLevelMpTermSynonym == null) ? 0 : topLevelMpTermSynonym.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((uberonIds == null) ? 0 : uberonIds.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaDTO other = (MaDTO) obj;
		if (accession == null) {
			if (other.accession != null)
				return false;
		} else if (!accession.equals(other.accession))
			return false;
		if (alleleId == null) {
			if (other.alleleId != null)
				return false;
		} else if (!alleleId.equals(other.alleleId))
			return false;
		if (alleleName == null) {
			if (other.alleleName != null)
				return false;
		} else if (!alleleName.equals(other.alleleName))
			return false;
		if (alleleSymbol == null) {
			if (other.alleleSymbol != null)
				return false;
		} else if (!alleleSymbol.equals(other.alleleSymbol))
			return false;
		if (annotatedHigherLevelMaTermId == null) {
			if (other.annotatedHigherLevelMaTermId != null)
				return false;
		} else if (!annotatedHigherLevelMaTermId.equals(other.annotatedHigherLevelMaTermId))
			return false;
		if (annotatedHigherLevelMaTermName == null) {
			if (other.annotatedHigherLevelMaTermName != null)
				return false;
		} else if (!annotatedHigherLevelMaTermName.equals(other.annotatedHigherLevelMaTermName))
			return false;
		if (annotatedHigherLevelMpTermId == null) {
			if (other.annotatedHigherLevelMpTermId != null)
				return false;
		} else if (!annotatedHigherLevelMpTermId.equals(other.annotatedHigherLevelMpTermId))
			return false;
		if (annotatedHigherLevelMpTermName == null) {
			if (other.annotatedHigherLevelMpTermName != null)
				return false;
		} else if (!annotatedHigherLevelMpTermName.equals(other.annotatedHigherLevelMpTermName))
			return false;
		if (annotatedOrInferredHigherLevelMaTermId == null) {
			if (other.annotatedOrInferredHigherLevelMaTermId != null)
				return false;
		} else if (!annotatedOrInferredHigherLevelMaTermId.equals(other.annotatedOrInferredHigherLevelMaTermId))
			return false;
		if (annotatedOrInferredHigherLevelMaTermName == null) {
			if (other.annotatedOrInferredHigherLevelMaTermName != null)
				return false;
		} else if (!annotatedOrInferredHigherLevelMaTermName.equals(other.annotatedOrInferredHigherLevelMaTermName))
			return false;
		if (annotationTermId == null) {
			if (other.annotationTermId != null)
				return false;
		} else if (!annotationTermId.equals(other.annotationTermId))
			return false;
		if (annotationTermName == null) {
			if (other.annotationTermName != null)
				return false;
		} else if (!annotationTermName.equals(other.annotationTermName))
			return false;
		if (autoSuggest == null) {
			if (other.autoSuggest != null)
				return false;
		} else if (!autoSuggest.equals(other.autoSuggest))
			return false;
		if (childMaId == null) {
			if (other.childMaId != null)
				return false;
		} else if (!childMaId.equals(other.childMaId))
			return false;
		if (childMaIdTerm == null) {
			if (other.childMaIdTerm != null)
				return false;
		} else if (!childMaIdTerm.equals(other.childMaIdTerm))
			return false;
		if (childMaTerm == null) {
			if (other.childMaTerm != null)
				return false;
		} else if (!childMaTerm.equals(other.childMaTerm))
			return false;
		if (childMaTermSynonym == null) {
			if (other.childMaTermSynonym != null)
				return false;
		} else if (!childMaTermSynonym.equals(other.childMaTermSynonym))
			return false;
		if (childMpId == null) {
			if (other.childMpId != null)
				return false;
		} else if (!childMpId.equals(other.childMpId))
			return false;
		if (childMpTerm == null) {
			if (other.childMpTerm != null)
				return false;
		} else if (!childMpTerm.equals(other.childMpTerm))
			return false;
		if (childMpTermSynonym == null) {
			if (other.childMpTermSynonym != null)
				return false;
		} else if (!childMpTermSynonym.equals(other.childMpTermSynonym))
			return false;
		if (dataType == null) {
			if (other.dataType != null)
				return false;
		} else if (!dataType.equals(other.dataType))
			return false;
		if (diseaseAlts == null) {
			if (other.diseaseAlts != null)
				return false;
		} else if (!diseaseAlts.equals(other.diseaseAlts))
			return false;
		if (diseaseClasses == null) {
			if (other.diseaseClasses != null)
				return false;
		} else if (!diseaseClasses.equals(other.diseaseClasses))
			return false;
		if (diseaseHumanPhenotypes == null) {
			if (other.diseaseHumanPhenotypes != null)
				return false;
		} else if (!diseaseHumanPhenotypes.equals(other.diseaseHumanPhenotypes))
			return false;
		if (diseaseId == null) {
			if (other.diseaseId != null)
				return false;
		} else if (!diseaseId.equals(other.diseaseId))
			return false;
		if (diseaseQf == null) {
			if (other.diseaseQf != null)
				return false;
		} else if (!diseaseQf.equals(other.diseaseQf))
			return false;
		if (diseaseSource == null) {
			if (other.diseaseSource != null)
				return false;
		} else if (!diseaseSource.equals(other.diseaseSource))
			return false;
		if (diseaseTerm == null) {
			if (other.diseaseTerm != null)
				return false;
		} else if (!diseaseTerm.equals(other.diseaseTerm))
			return false;
		if (efoIds == null) {
			if (other.efoIds != null)
				return false;
		} else if (!efoIds.equals(other.efoIds))
			return false;
		if (expName == null) {
			if (other.expName != null)
				return false;
		} else if (!expName.equals(other.expName))
			return false;
		if (expNameExp == null) {
			if (other.expNameExp != null)
				return false;
		} else if (!expNameExp.equals(other.expNameExp))
			return false;
		if (geneName == null) {
			if (other.geneName != null)
				return false;
		} else if (!geneName.equals(other.geneName))
			return false;
		if (geneQf == null) {
			if (other.geneQf != null)
				return false;
		} else if (!geneQf.equals(other.geneQf))
			return false;
		if (geneSynonyms == null) {
			if (other.geneSynonyms != null)
				return false;
		} else if (!geneSynonyms.equals(other.geneSynonyms))
			return false;
		if (geneticBackground == null) {
			if (other.geneticBackground != null)
				return false;
		} else if (!geneticBackground.equals(other.geneticBackground))
			return false;
		if (goId == null) {
			if (other.goId != null)
				return false;
		} else if (!goId.equals(other.goId))
			return false;
		if (hpId == null) {
			if (other.hpId != null)
				return false;
		} else if (!hpId.equals(other.hpId))
			return false;
		if (hpTerm == null) {
			if (other.hpTerm != null)
				return false;
		} else if (!hpTerm.equals(other.hpTerm))
			return false;
		if (humanCurated == null) {
			if (other.humanCurated != null)
				return false;
		} else if (!humanCurated.equals(other.humanCurated))
			return false;
		if (humanGeneSymbol == null) {
			if (other.humanGeneSymbol != null)
				return false;
		} else if (!humanGeneSymbol.equals(other.humanGeneSymbol))
			return false;
		if (imitsPhenotypeComplete == null) {
			if (other.imitsPhenotypeComplete != null)
				return false;
		} else if (!imitsPhenotypeComplete.equals(other.imitsPhenotypeComplete))
			return false;
		if (imitsPhenotypeStarted == null) {
			if (other.imitsPhenotypeStarted != null)
				return false;
		} else if (!imitsPhenotypeStarted.equals(other.imitsPhenotypeStarted))
			return false;
		if (imitsPhenotypeStatus == null) {
			if (other.imitsPhenotypeStatus != null)
				return false;
		} else if (!imitsPhenotypeStatus.equals(other.imitsPhenotypeStatus))
			return false;
		if (impcNovelPredictedInLocus == null) {
			if (other.impcNovelPredictedInLocus != null)
				return false;
		} else if (!impcNovelPredictedInLocus.equals(other.impcNovelPredictedInLocus))
			return false;
		if (impcPredicted == null) {
			if (other.impcPredicted != null)
				return false;
		} else if (!impcPredicted.equals(other.impcPredicted))
			return false;
		if (impcPredictedKnownGene == null) {
			if (other.impcPredictedKnownGene != null)
				return false;
		} else if (!impcPredictedKnownGene.equals(other.impcPredictedKnownGene))
			return false;
		if (inferredHigherLevelMaTermId == null) {
			if (other.inferredHigherLevelMaTermId != null)
				return false;
		} else if (!inferredHigherLevelMaTermId.equals(other.inferredHigherLevelMaTermId))
			return false;
		if (inferredHigherLevelMaTermName == null) {
			if (other.inferredHigherLevelMaTermName != null)
				return false;
		} else if (!inferredHigherLevelMaTermName.equals(other.inferredHigherLevelMaTermName))
			return false;
		if (inferredMaTermId == null) {
			if (other.inferredMaTermId != null)
				return false;
		} else if (!inferredMaTermId.equals(other.inferredMaTermId))
			return false;
		if (inferredMaTermName == null) {
			if (other.inferredMaTermName != null)
				return false;
		} else if (!inferredMaTermName.equals(other.inferredMaTermName))
			return false;
		if (intermediateMpId == null) {
			if (other.intermediateMpId != null)
				return false;
		} else if (!intermediateMpId.equals(other.intermediateMpId))
			return false;
		if (intermediateMpTerm == null) {
			if (other.intermediateMpTerm != null)
				return false;
		} else if (!intermediateMpTerm.equals(other.intermediateMpTerm))
			return false;
		if (intermediateMpTermSynonym == null) {
			if (other.intermediateMpTermSynonym != null)
				return false;
		} else if (!intermediateMpTermSynonym.equals(other.intermediateMpTermSynonym))
			return false;
		if (largeThumbnailFilePath == null) {
			if (other.largeThumbnailFilePath != null)
				return false;
		} else if (!largeThumbnailFilePath.equals(other.largeThumbnailFilePath))
			return false;
		if (latestPhenotypeStatus == null) {
			if (other.latestPhenotypeStatus != null)
				return false;
		} else if (!latestPhenotypeStatus.equals(other.latestPhenotypeStatus))
			return false;
		if (latestPhenotypingCentre == null) {
			if (other.latestPhenotypingCentre != null)
				return false;
		} else if (!latestPhenotypingCentre.equals(other.latestPhenotypingCentre))
			return false;
		if (latestProductionCentre == null) {
			if (other.latestProductionCentre != null)
				return false;
		} else if (!latestProductionCentre.equals(other.latestProductionCentre))
			return false;
		if (legacyPhenotypeStatus == null) {
			if (other.legacyPhenotypeStatus != null)
				return false;
		} else if (!legacyPhenotypeStatus.equals(other.legacyPhenotypeStatus))
			return false;
		if (maId == null) {
			if (other.maId != null)
				return false;
		} else if (!maId.equals(other.maId))
			return false;
		if (maQf == null) {
			if (other.maQf != null)
				return false;
		} else if (!maQf.equals(other.maQf))
			return false;
		if (maTerm == null) {
			if (other.maTerm != null)
				return false;
		} else if (!maTerm.equals(other.maTerm))
			return false;
		if (maTermId == null) {
			if (other.maTermId != null)
				return false;
		} else if (!maTermId.equals(other.maTermId))
			return false;
		if (maTermName == null) {
			if (other.maTermName != null)
				return false;
		} else if (!maTermName.equals(other.maTermName))
			return false;
		if (maTermSynonym == null) {
			if (other.maTermSynonym != null)
				return false;
		} else if (!maTermSynonym.equals(other.maTermSynonym))
			return false;
		if (markerName == null) {
			if (other.markerName != null)
				return false;
		} else if (!markerName.equals(other.markerName))
			return false;
		if (markerSymbol == null) {
			if (other.markerSymbol != null)
				return false;
		} else if (!markerSymbol.equals(other.markerSymbol))
			return false;
		if (markerSynonym == null) {
			if (other.markerSynonym != null)
				return false;
		} else if (!markerSynonym.equals(other.markerSynonym))
			return false;
		if (markerType == null) {
			if (other.markerType != null)
				return false;
		} else if (!markerType.equals(other.markerType))
			return false;
		if (mgiAccessionId == null) {
			if (other.mgiAccessionId != null)
				return false;
		} else if (!mgiAccessionId.equals(other.mgiAccessionId))
			return false;
		if (mgiNovelPredictedInLocus == null) {
			if (other.mgiNovelPredictedInLocus != null)
				return false;
		} else if (!mgiNovelPredictedInLocus.equals(other.mgiNovelPredictedInLocus))
			return false;
		if (mgiPredicted == null) {
			if (other.mgiPredicted != null)
				return false;
		} else if (!mgiPredicted.equals(other.mgiPredicted))
			return false;
		if (mgiPredictedKnownGene == null) {
			if (other.mgiPredictedKnownGene != null)
				return false;
		} else if (!mgiPredictedKnownGene.equals(other.mgiPredictedKnownGene))
			return false;
		if (mouseCurated == null) {
			if (other.mouseCurated != null)
				return false;
		} else if (!mouseCurated.equals(other.mouseCurated))
			return false;
		if (mpId == null) {
			if (other.mpId != null)
				return false;
		} else if (!mpId.equals(other.mpId))
			return false;
		if (mpQf == null) {
			if (other.mpQf != null)
				return false;
		} else if (!mpQf.equals(other.mpQf))
			return false;
		if (mpTerm == null) {
			if (other.mpTerm != null)
				return false;
		} else if (!mpTerm.equals(other.mpTerm))
			return false;
		if (mpTermId == null) {
			if (other.mpTermId != null)
				return false;
		} else if (!mpTermId.equals(other.mpTermId))
			return false;
		if (mpTermName == null) {
			if (other.mpTermName != null)
				return false;
		} else if (!mpTermName.equals(other.mpTermName))
			return false;
		if (mpTermSynonym == null) {
			if (other.mpTermSynonym != null)
				return false;
		} else if (!mpTermSynonym.equals(other.mpTermSynonym))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (ontologySubset == null) {
			if (other.ontologySubset != null)
				return false;
		} else if (!ontologySubset.equals(other.ontologySubset))
			return false;
		if (pValue == null) {
			if (other.pValue != null)
				return false;
		} else if (!pValue.equals(other.pValue))
			return false;
		if (parameterName == null) {
			if (other.parameterName != null)
				return false;
		} else if (!parameterName.equals(other.parameterName))
			return false;
		if (parameterStableId == null) {
			if (other.parameterStableId != null)
				return false;
		} else if (!parameterStableId.equals(other.parameterStableId))
			return false;
		if (parameterStableKey == null) {
			if (other.parameterStableKey != null)
				return false;
		} else if (!parameterStableKey.equals(other.parameterStableKey))
			return false;
		if (pipelineName == null) {
			if (other.pipelineName != null)
				return false;
		} else if (!pipelineName.equals(other.pipelineName))
			return false;
		if (pipelineStableId == null) {
			if (other.pipelineStableId != null)
				return false;
		} else if (!pipelineStableId.equals(other.pipelineStableId))
			return false;
		if (pipelineStableKey == null) {
			if (other.pipelineStableKey != null)
				return false;
		} else if (!pipelineStableKey.equals(other.pipelineStableKey))
			return false;
		if (procedureName == null) {
			if (other.procedureName != null)
				return false;
		} else if (!procedureName.equals(other.procedureName))
			return false;
		if (procedureStableId == null) {
			if (other.procedureStableId != null)
				return false;
		} else if (!procedureStableId.equals(other.procedureStableId))
			return false;
		if (procedureStableKey == null) {
			if (other.procedureStableKey != null)
				return false;
		} else if (!procedureStableKey.equals(other.procedureStableKey))
			return false;
		if (sangerSymbol == null) {
			if (other.sangerSymbol != null)
				return false;
		} else if (!sangerSymbol.equals(other.sangerSymbol))
			return false;
		if (selectedTopLevelMaId == null) {
			if (other.selectedTopLevelMaId != null)
				return false;
		} else if (!selectedTopLevelMaId.equals(other.selectedTopLevelMaId))
			return false;
		if (selectedTopLevelMaTerm == null) {
			if (other.selectedTopLevelMaTerm != null)
				return false;
		} else if (!selectedTopLevelMaTerm.equals(other.selectedTopLevelMaTerm))
			return false;
		if (selectedTopLevelMaTermSynonym == null) {
			if (other.selectedTopLevelMaTermSynonym != null)
				return false;
		} else if (!selectedTopLevelMaTermSynonym.equals(other.selectedTopLevelMaTermSynonym))
			return false;
		if (smallThumbnailFilePath == null) {
			if (other.smallThumbnailFilePath != null)
				return false;
		} else if (!smallThumbnailFilePath.equals(other.smallThumbnailFilePath))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (strainId == null) {
			if (other.strainId != null)
				return false;
		} else if (!strainId.equals(other.strainId))
			return false;
		if (strainName == null) {
			if (other.strainName != null)
				return false;
		} else if (!strainName.equals(other.strainName))
			return false;
		if (subtype == null) {
			if (other.subtype != null)
				return false;
		} else if (!subtype.equals(other.subtype))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (symbolGene == null) {
			if (other.symbolGene != null)
				return false;
		} else if (!symbolGene.equals(other.symbolGene))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		if (topLevel == null) {
			if (other.topLevel != null)
				return false;
		} else if (!topLevel.equals(other.topLevel))
			return false;
		if (topLevelMaId == null) {
			if (other.topLevelMaId != null)
				return false;
		} else if (!topLevelMaId.equals(other.topLevelMaId))
			return false;
		if (topLevelMaTerm == null) {
			if (other.topLevelMaTerm != null)
				return false;
		} else if (!topLevelMaTerm.equals(other.topLevelMaTerm))
			return false;
		if (topLevelMpId == null) {
			if (other.topLevelMpId != null)
				return false;
		} else if (!topLevelMpId.equals(other.topLevelMpId))
			return false;
		if (topLevelMpTerm == null) {
			if (other.topLevelMpTerm != null)
				return false;
		} else if (!topLevelMpTerm.equals(other.topLevelMpTerm))
			return false;
		if (topLevelMpTermSynonym == null) {
			if (other.topLevelMpTermSynonym != null)
				return false;
		} else if (!topLevelMpTermSynonym.equals(other.topLevelMpTermSynonym))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (uberonIds == null) {
			if (other.uberonIds != null)
				return false;
		} else if (!uberonIds.equals(other.uberonIds))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MaDTO [dataType=" + dataType + ", maId=" + maId + ", uberonIds=" + uberonIds + ", efoIds=" + efoIds
				+ ", maTerm=" + maTerm + ", maTermSynonym=" + maTermSynonym + ", ontologySubset=" + ontologySubset
				+ ", childMaId=" + childMaId + ", childMaTerm=" + childMaTerm + ", childMaTermSynonym="
				+ childMaTermSynonym + ", childMaIdTerm=" + childMaIdTerm + ", topLevelMaId=" + topLevelMaId
				+ ", topLevelMaTerm=" + topLevelMaTerm + ", selectedTopLevelMaId=" + selectedTopLevelMaId
				+ ", selectedTopLevelMaTerm=" + selectedTopLevelMaTerm + ", selectedTopLevelMaTermSynonym="
				+ selectedTopLevelMaTermSynonym + ", hpId=" + hpId + ", hpTerm=" + hpTerm + ", goId=" + goId
				+ ", pValue=" + pValue + ", mgiAccessionId=" + mgiAccessionId + ", markerSymbol=" + markerSymbol
				+ ", markerName=" + markerName + ", markerSynonym=" + markerSynonym + ", markerType=" + markerType
				+ ", humanGeneSymbol=" + humanGeneSymbol + ", status=" + status + ", imitsPhenotypeStarted="
				+ imitsPhenotypeStarted + ", imitsPhenotypeComplete=" + imitsPhenotypeComplete
				+ ", imitsPhenotypeStatus=" + imitsPhenotypeStatus + ", latestProductionCentre="
				+ latestProductionCentre + ", latestPhenotypingCentre=" + latestPhenotypingCentre
				+ ", latestPhenotypeStatus=" + latestPhenotypeStatus + ", legacyPhenotypeStatus="
				+ legacyPhenotypeStatus + ", alleleName=" + alleleName + ", type=" + type + ", diseaseId=" + diseaseId
				+ ", diseaseSource=" + diseaseSource + ", diseaseTerm=" + diseaseTerm + ", diseaseAlts=" + diseaseAlts
				+ ", diseaseClasses=" + diseaseClasses + ", diseaseHumanPhenotypes=" + diseaseHumanPhenotypes
				+ ", humanCurated=" + humanCurated + ", mouseCurated=" + mouseCurated + ", mgiPredicted=" + mgiPredicted
				+ ", impcPredicted=" + impcPredicted + ", mgiPredictedKnownGene=" + mgiPredictedKnownGene
				+ ", impcPredictedKnownGene=" + impcPredictedKnownGene + ", mgiNovelPredictedInLocus="
				+ mgiNovelPredictedInLocus + ", impcNovelPredictedInLocus=" + impcNovelPredictedInLocus
				+ ", annotationTermId=" + annotationTermId + ", annotationTermName=" + annotationTermName + ", name="
				+ name + ", accession=" + accession + ", expName=" + expName + ", largeThumbnailFilePath="
				+ largeThumbnailFilePath + ", smallThumbnailFilePath=" + smallThumbnailFilePath + ", inferredMaTermId="
				+ inferredMaTermId + ", inferredMaTermName=" + inferredMaTermName + ", annotatedHigherLevelMaTermId="
				+ annotatedHigherLevelMaTermId + ", annotatedHigherLevelMaTermName=" + annotatedHigherLevelMaTermName
				+ ", annotatedHigherLevelMpTermId=" + annotatedHigherLevelMpTermId + ", annotatedHigherLevelMpTermName="
				+ annotatedHigherLevelMpTermName + ", inferredHigherLevelMaTermId=" + inferredHigherLevelMaTermId
				+ ", inferredHigherLevelMaTermName=" + inferredHigherLevelMaTermName
				+ ", annotatedOrInferredHigherLevelMaTermName=" + annotatedOrInferredHigherLevelMaTermName
				+ ", annotatedOrInferredHigherLevelMaTermId=" + annotatedOrInferredHigherLevelMaTermId + ", symbol="
				+ symbol + ", sangerSymbol=" + sangerSymbol + ", geneName=" + geneName + ", subtype=" + subtype
				+ ", geneSynonyms=" + geneSynonyms + ", maTermId=" + maTermId + ", maTermName=" + maTermName
				+ ", mpTermId=" + mpTermId + ", mpTermName=" + mpTermName + ", expNameExp=" + expNameExp
				+ ", symbolGene=" + symbolGene + ", topLevel=" + topLevel + ", alleleSymbol=" + alleleSymbol
				+ ", alleleId=" + alleleId + ", strainName=" + strainName + ", strainId=" + strainId
				+ ", geneticBackground=" + geneticBackground + ", pipelineName=" + pipelineName + ", pipelineStableId="
				+ pipelineStableId + ", pipelineStableKey=" + pipelineStableKey + ", procedureName=" + procedureName
				+ ", procedureStableId=" + procedureStableId + ", procedureStableKey=" + procedureStableKey
				+ ", parameterName=" + parameterName + ", parameterStableId=" + parameterStableId
				+ ", parameterStableKey=" + parameterStableKey + ", mpId=" + mpId + ", mpTerm=" + mpTerm
				+ ", mpTermSynonym=" + mpTermSynonym + ", topLevelMpId=" + topLevelMpId + ", topLevelMpTerm="
				+ topLevelMpTerm + ", topLevelMpTermSynonym=" + topLevelMpTermSynonym + ", intermediateMpId="
				+ intermediateMpId + ", intermediateMpTerm=" + intermediateMpTerm + ", intermediateMpTermSynonym="
				+ intermediateMpTermSynonym + ", childMpId=" + childMpId + ", childMpTerm=" + childMpTerm
				+ ", childMpTermSynonym=" + childMpTermSynonym + ", text=" + text + ", autoSuggest=" + autoSuggest
				+ ", geneQf=" + geneQf + ", mpQf=" + mpQf + ", diseaseQf=" + diseaseQf + ", maQf=" + maQf + "]";
	}
  
	
}
