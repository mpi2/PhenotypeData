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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ImageDTO extends ObservationDTO {


    public final static String ID = ObservationDTO.ID;
    public final static String DATASOURCE_ID = ObservationDTO.DATASOURCE_ID;
    public final static String DATASOURCE_NAME = ObservationDTO.DATASOURCE_NAME;
    public final static String PROJECT_ID = ObservationDTO.PROJECT_ID;
    public final static String PROJECT_NAME = ObservationDTO.PROJECT_NAME;
    public final static String PHENOTYPING_CENTER = ObservationDTO.PHENOTYPING_CENTER;
    public final static String PHENOTYPING_CENTER_ID = ObservationDTO.PHENOTYPING_CENTER_ID;
    public final static String GENE_ACCESSION_ID = ObservationDTO.GENE_ACCESSION_ID;
    public final static String GENE_SYMBOL = ObservationDTO.GENE_SYMBOL;
    public final static String ALLELE_ACCESSION_ID = ObservationDTO.ALLELE_ACCESSION_ID;
    public final static String ALLELE_SYMBOL = ObservationDTO.ALLELE_SYMBOL;
    public final static String ZYGOSITY = ObservationDTO.ZYGOSITY;
    public final static String SEX = ObservationDTO.SEX;
    public final static String BIOLOGICAL_MODEL_ID = ObservationDTO.BIOLOGICAL_MODEL_ID;
    public final static String BIOLOGICAL_SAMPLE_ID = ObservationDTO.BIOLOGICAL_SAMPLE_ID;
    public final static String BIOLOGICAL_SAMPLE_GROUP = ObservationDTO.BIOLOGICAL_SAMPLE_GROUP;
    public final static String STRAIN_ACCESSION_ID = ObservationDTO.STRAIN_ACCESSION_ID;
    public final static String STRAIN_NAME = ObservationDTO.STRAIN_NAME;
    public final static String GENETIC_BACKGROUND = ObservationDTO.GENETIC_BACKGROUND;
    public final static String PIPELINE_NAME = ObservationDTO.PIPELINE_NAME;
    public final static String PIPELINE_ID = ObservationDTO.PIPELINE_ID;
    public final static String PIPELINE_STABLE_ID = ObservationDTO.PIPELINE_STABLE_ID;
    public final static String PROCEDURE_ID = ObservationDTO.PROCEDURE_ID;
    public final static String PROCEDURE_NAME = ObservationDTO.PROCEDURE_NAME;
    public final static String PROCEDURE_STABLE_ID = ObservationDTO.PROCEDURE_STABLE_ID;
    public final static String PROCEDURE_GROUP = ObservationDTO.PROCEDURE_GROUP;
    public final static String PARAMETER_ID = ObservationDTO.PARAMETER_ID;
    public final static String PARAMETER_NAME = ObservationDTO.PARAMETER_NAME;
    public final static String PARAMETER_STABLE_ID = ObservationDTO.PARAMETER_STABLE_ID;
    public final static String EXPERIMENT_ID = ObservationDTO.EXPERIMENT_ID;
    public final static String EXPERIMENT_SOURCE_ID = ObservationDTO.EXPERIMENT_SOURCE_ID;
    public final static String OBSERVATION_TYPE = ObservationDTO.OBSERVATION_TYPE;
    public final static String COLONY_ID = ObservationDTO.COLONY_ID;
    public final static String DATE_OF_BIRTH = ObservationDTO.DATE_OF_BIRTH;
    public final static String DATE_OF_EXPERIMENT = ObservationDTO.DATE_OF_EXPERIMENT;
    public final static String POPULATION_ID = ObservationDTO.POPULATION_ID;
    public final static String EXTERNAL_SAMPLE_ID = ObservationDTO.EXTERNAL_SAMPLE_ID;
    public final static String DATA_POINT = ObservationDTO.DATA_POINT;
    public final static String ORDER_INDEX = ObservationDTO.ORDER_INDEX;
    public final static String DIMENSION = ObservationDTO.DIMENSION;
    public final static String TIME_POINT = ObservationDTO.TIME_POINT;
    public final static String DISCRETE_POINT = ObservationDTO.DISCRETE_POINT;
    public final static String CATEGORY = ObservationDTO.CATEGORY;
    public final static String VALUE = ObservationDTO.VALUE;
    public final static String METADATA = ObservationDTO.METADATA;
    public final static String METADATA_GROUP = ObservationDTO.METADATA_GROUP;
    public final static String DOWNLOAD_FILE_PATH = ObservationDTO.DOWNLOAD_FILE_PATH;
    public final static String FILE_TYPE = ObservationDTO.FILE_TYPE;
    public final static String PARAMETER_ASSOCIATION_STABLE_ID = ObservationDTO.PARAMETER_ASSOCIATION_STABLE_ID;
    public final static String PARAMETER_ASSOCIATION_SEQUENCE_ID = ObservationDTO.PARAMETER_ASSOCIATION_SEQUENCE_ID;
    public final static String PARAMETER_ASSOCIATION_DIM_ID = ObservationDTO.PARAMETER_ASSOCIATION_DIM_ID;
    public final static String PARAMETER_ASSOCIATION_NAME = ObservationDTO.PARAMETER_ASSOCIATION_NAME;
    public final static String PARAMETER_ASSOCIATION_VALUE = ObservationDTO.PARAMETER_ASSOCIATION_VALUE;
    public final static String WEIGHT_PARAMETER_STABLE_ID = ObservationDTO.WEIGHT_PARAMETER_STABLE_ID;
    public final static String WEIGHT_DATE = ObservationDTO.WEIGHT_DATE;
    public final static String WEIGHT_DAYS_OLD = ObservationDTO.WEIGHT_DAYS_OLD;
    public final static String WEIGHT = ObservationDTO.WEIGHT;
    public static final String AGE_IN_DAYS = ObservationDTO.AGE_IN_DAYS;

    public static final String FULL_RESOLUTION_FILE_PATH = "full_resolution_file_path";

    public static final String OMERO_ID = "omero_id";
    public static final String DOWNLOAD_URL = "download_url";
    public static final String JPEG_URL = "jpeg_url";
    public static final String IMAGE_LINK = "image_link";

    public static final String EFO_ID = "efo_id";
    public static final String UBERON_ID = "uberon_id";

    public static final String STAGE = "stage";

    public static final String SYMBOL_GENE = "symbol_gene";
    public static final String SYMBOL = "symbol";
    public static final String SUBTYPE = "subtype";
    public static final String STATUS = "status";
    public static final String IMITS_PHENOTYPE_STARTED = SangerImageDTO.IMITS_PHENOTYPE_STARTED;
    public static final String IMITS_PHENOTYPE_COMPLETE = SangerImageDTO.IMITS_PHENOTYPE_COMPLETE;
    public static final String IMITS_PHENOTYPE_STATUS = SangerImageDTO.IMITS_PHENOTYPE_STATUS;
    public static final String LEGACY_PHENOTYPE_STATUS = AlleleDTO.LEGACY_PHENOTYPE_STATUS;
    public static final String LATEST_PRODUCTION_CENTRE = SangerImageDTO.LATEST_PRODUCTION_CENTRE;
    public static final String LATEST_PHENOTYPING_CENTRE = SangerImageDTO.LATEST_PHENOTYPING_CENTRE;
    public static final String ALLELE_NAME = SangerImageDTO.ALLELE_NAME;
    public static final String MARKER_SYMBOL = SangerImageDTO.MARKER_SYMBOL;
    public static final String MARKER_NAME = SangerImageDTO.MARKER_NAME;
    public static final String MARKER_SYNONYM = SangerImageDTO.MARKER_SYNONYM;
    public static final String MARKER_TYPE = SangerImageDTO.MARKER_TYPE;
    public static final String HUMAN_GENE_SYMBOL = SangerImageDTO.HUMAN_GENE_SYMBOL;
    public static final String LATEST_PHENOTYPE_STATUS = AlleleDTO.LATEST_PHENOTYPE_STATUS;

    public static final String MP_ID = MpDTO.MP_ID;
    public static final String MP_TERM = MpDTO.MP_TERM;
    public static final String MP_TERM_SYNONYM = MpDTO.MP_TERM_SYNONYM;
    public static final String MP_NARROW_SYNONYM = MpDTO.MP_NARROW_SYNONYM;

    public static final String INTERMEDIATE_MP_ID = MpDTO.INTERMEDIATE_MP_ID;
    public static final String INTERMEDIATE_MP_TERM = MpDTO.INTERMEDIATE_MP_TERM;

    public static final String TOP_LEVEL_MP_TERM = MpDTO.TOP_LEVEL_MP_TERM;
    public static final String TOP_LEVEL_MP_ID = MpDTO.TOP_LEVEL_MP_ID;

    public static final String MP_ID_TERM = "mp_id_term";

    public static final String INCREMENT_VALUE = "increment_value";

    //	used for lexical match on search page
    public static final String MARKER_SYNONYM_SYMBOL_GENE = "marker_synonym_symbol_gene";
    public static final String PARAMETER_ASSOCIATION_NAME_PROCEDURE_NAME = "parameter_association_name_procedure_name";
    public static final String SELECTED_TOP_LEVEL_ANATOMY_ID_ANATOMY_ID_TERM = "selected_top_level_anatomy_id_anatomy_id_term";
    public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM_ANATOMY_ID_TERM = "selected_top_level_anatomy_term_anatomy_id_term";
    public static final String SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM_ANATOMY_ID_TERM = "selected_top_level_anatomy_term_synonym_anatomy_id_term";
    public static final String INTERMEDIATE_ANATOMY_ID_ANATOMY_ID_TERM = "intermediate_anatomy_id_anatomy_id_term";
    public static final String INTERMEDIATE_ANATOMY_TERM_ANATOMY_ID_TERM = "intermediate_anatomy_term_anatomy_id_term";
    public static final String INTERMEDIATE_ANATOMY_TERM_SYNONYM_ANATOMY_ID_TERM = "intermediate_anatomy_term_synonym_anatomy_id_term";
    public static final String ANATOMY_TERM_SYNONYM_ANATOMY_ID_TERM = "anatomy_term_synonym_anatomy_id_term";
    public static final String THUMBNAIL_URL = "thumbnail_url";

    @Field(PARAMETER_ASSOCIATION_VALUE)
    private List<String> parameterAssociationValues;

    @Field(MP_ID)
    private List<String> mpId;

    @Field(MP_TERM)
    private List<String> mpTerm;

    @Field(INTERMEDIATE_MP_ID)
    private List<String> intermediateMpId;

    @Field(INTERMEDIATE_MP_TERM)
    private List<String> intermediateMpTerm;

    @Field(TOP_LEVEL_MP_TERM)
    private List<String> topLevelMpTerm;

    @Field(TOP_LEVEL_MP_ID)
    private List<String> topLevelMpId;

    @Field(MP_ID_TERM)
    private List<String> mpIdTerm;

    @Field(MP_TERM_SYNONYM)
    private List<String> mpTermSynonym;

    @Field(MP_NARROW_SYNONYM)
    private Set<String> mpNarrowSynonym;

    @Field(FULL_RESOLUTION_FILE_PATH)
    private String fullResolutionFilePath;

    @Field(OMERO_ID)
    private int omeroId;

    @Field(DOWNLOAD_URL)
    private String downloadUrl;

    @Field(STAGE)
    private String stage;

    @Field(IMAGE_LINK)
    private String imageLink;

    @Field(JPEG_URL)
    private String jpegUrl;

    @Field(EFO_ID)
    private List<String> efoId;

    @Field(UBERON_ID)
    private List<String> uberonId;

    //for search and annotation view
    @Field(SYMBOL_GENE)
    private String symbolGene;

    @Field(STATUS)
    private List<String> status;

    @Field(IMITS_PHENOTYPE_STARTED)
    private List<String> imitsPhenotypeStarted;

    @Field(IMITS_PHENOTYPE_COMPLETE)
    private List<String> imitsPhenotypeComplete;

    @Field(IMITS_PHENOTYPE_STATUS)
    private List<String> imitsPhenotypeStatus;

    @Field(LEGACY_PHENOTYPE_STATUS)
    private Integer legacyPhenotypeStatus;

    @Field(LATEST_PRODUCTION_CENTRE)
    private List<String> latestProductionCentre;

    @Field(LATEST_PHENOTYPING_CENTRE)
    private List<String> latestPhenotypingCentre;

    @Field(ALLELE_NAME)
    private List<String> alleleName;

    @Field(MARKER_SYMBOL)
    private List<String> markerSymbol;

    @Field(MARKER_NAME)
    private List<String> markerName;

    @Field(MARKER_SYNONYM)
    private List<String> markerSynonym;

    @Field(MARKER_TYPE)
    private String markerType;

    @Field(HUMAN_GENE_SYMBOL)
    private List<String> humanGeneSymbol;

    @Field(SYMBOL)
    private String symbol;

    @Field(SUBTYPE)
    private String subtype;

    @Field(INCREMENT_VALUE)
    private Integer increment;

    @Field(LATEST_PHENOTYPE_STATUS)
    private List<String> latestPhenotypeStatus;

    // used for lexical match on search page
    @Field(MARKER_SYNONYM_SYMBOL_GENE)
    private List<String> markerSynonymSymbolGene;

    @Field(PARAMETER_ASSOCIATION_NAME_PROCEDURE_NAME)
    private List<String> parameterAssociationNameProcedureName;

    @Field(SELECTED_TOP_LEVEL_ANATOMY_ID_ANATOMY_ID_TERM)
    private List<String> selectedTopLevelAnatomyIdAnatomyIdTerm;

    @Field(SELECTED_TOP_LEVEL_ANATOMY_TERM_ANATOMY_ID_TERM)
    private List<String> selectedTopLevelAnatomyTermAnatomyIdTerm;

    @Field(SELECTED_TOP_LEVEL_ANATOMY_TERM_SYNONYM_ANATOMY_ID_TERM)
    private List<String> selectedTopLevelAnatomyTermSynonymAnatomyIdTerm;

    @Field(INTERMEDIATE_ANATOMY_ID_ANATOMY_ID_TERM)
    private List<String> intermediateAnatomyIdAnatomyIdTerm;

    @Field(INTERMEDIATE_ANATOMY_TERM_ANATOMY_ID_TERM)
    private List<String> intermediateAnatomyTermAnatomyIdTerm;

    @Field(INTERMEDIATE_ANATOMY_TERM_SYNONYM_ANATOMY_ID_TERM)
    private List<String> intermediateAnatomyTermSynonymAnatomyIdTerm;

    @Field(ANATOMY_TERM_SYNONYM_ANATOMY_ID_TERM)
    private List<String> anatomyTermSynonymAnatomyIdTerm;


    public List<String> getParameterAssociationValues() {
        return parameterAssociationValues;
    }

    public void setParameterAssociationValues(List<String> parameterAssociationValues) {
        this.parameterAssociationValues = parameterAssociationValues;
    }

    public List<String> getIntermediateMpId() {
        return intermediateMpId;
    }

    public void setIntermediateMpId(List<String> intermediateMpId) {
        this.intermediateMpId = intermediateMpId;
    }

    public void addIntermediateMpId(String intermediateMpId) {
        if (this.intermediateMpId == null) {
            this.intermediateMpId = new ArrayList<>();
        }
        this.intermediateMpId.add(intermediateMpId);
    }

    public List<String> getIntermediateMpTerm() {
        return intermediateMpTerm;
    }

    public void setIntermediateMpTerm(List<String> intermediateMpTerm) {
        this.intermediateMpTerm = intermediateMpTerm;
    }

    public void addIntermediateMpTerm(String intermediateMpTerm) {
        if (this.intermediateMpTerm == null) {
            this.intermediateMpTerm = new ArrayList<>();
        }
        this.intermediateMpTerm.add(intermediateMpTerm);
    }

    public List<String> getTopLevelMpTerm() {
        return topLevelMpTerm;
    }

    public void setTopLevelMpTerm(List<String> topLevelMpTerm) {
        this.topLevelMpTerm = topLevelMpTerm;
    }

    public void addTopLevelMpTerm(String topLevelMpTerm, Boolean uniqueOnly) {
        this.topLevelMpTerm = add(this.topLevelMpTerm, topLevelMpTerm, uniqueOnly);
    }

    public List<String> getTopLevelMpId() {
        return topLevelMpId;
    }

    public void setTopLevelMpId(List<String> topLevelMpId) {
        this.topLevelMpId = topLevelMpId;
    }

    public void addTopLevelMpId(String topLevelMpId, Boolean uniqueOnly) {
        this.topLevelMpId = add(this.topLevelMpId, topLevelMpId, uniqueOnly);
    }

    @Field(THUMBNAIL_URL)
    private String thumbnailUrl;

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }


    public List<String> getMarkerSynonymSymbolGene() {
        return markerSynonymSymbolGene;
    }

    public void setMarkerSynonymSymbolGene(List<String> markerSynonymSymbolGene) {
        this.markerSynonymSymbolGene = markerSynonymSymbolGene;
    }

    public List<String> getParameterAssociationNameProcedureName() {
        return parameterAssociationNameProcedureName;
    }

    public void setParameterAssociationNameProcedureName(List<String> parameterAssociationNameProcedureName) {
        this.parameterAssociationNameProcedureName = parameterAssociationNameProcedureName;
    }

    public List<String> getSelectedTopLevelAnatomyIdAnatomyIdTerm() {
        return selectedTopLevelAnatomyIdAnatomyIdTerm;
    }

    public void setSelectedTopLevelAnatomyIdAnatomyIdTerm(List<String> selectedTopLevelAnatomyIdAnatomyIdTerm) {
        this.selectedTopLevelAnatomyIdAnatomyIdTerm = selectedTopLevelAnatomyIdAnatomyIdTerm;
    }

    public void addSelectedTopLevelAnatomyIdAnatomyIdTerm(String selectedTopLevelAnatomyIdAnatomyIdTerm) {

        this.selectedTopLevelAnatomyIdAnatomyIdTerm = add(this.selectedTopLevelAnatomyIdAnatomyIdTerm, selectedTopLevelAnatomyIdAnatomyIdTerm);

    }

    public List<String> getSelectedTopLevelAnatomyTermAnatomyIdTerm() {
        return selectedTopLevelAnatomyTermAnatomyIdTerm;
    }

    public void setSelectedTopLevelAnatomyTermAnatomyIdTerm(List<String> selectedTopLevelAnatomyTermAnatomyIdTerm) {
        this.selectedTopLevelAnatomyTermAnatomyIdTerm = selectedTopLevelAnatomyTermAnatomyIdTerm;
    }

    public void addSelectedTopLevelAnatomyTermAnatomyIdTerm(String selectedTopLevelAnatomyTermAnatomyIdTerm) {
        this.selectedTopLevelAnatomyTermAnatomyIdTerm = add(this.selectedTopLevelAnatomyTermAnatomyIdTerm, selectedTopLevelAnatomyTermAnatomyIdTerm);
    }

    public List<String> getSelectedTopLevelAnatomyTermSynonymAnatomyIdTerm() {
        return selectedTopLevelAnatomyTermSynonymAnatomyIdTerm;
    }

    public void setSelectedTopLevelAnatomyTermSynonymAnatomyIdTerm(List<String> selectedTopLevelAnatomyTermSynonymAnatomyIdTerm) {
        this.selectedTopLevelAnatomyTermSynonymAnatomyIdTerm = selectedTopLevelAnatomyTermSynonymAnatomyIdTerm;
    }

    public void addSelectedTopLevelAnatomyTermSynonymAnatomyIdTerm(List<String> synonyms, String postfix) {
        for (String synonym : synonyms) {
            this.selectedTopLevelAnatomyTermSynonymAnatomyIdTerm = add(this.selectedTopLevelAnatomyTermSynonymAnatomyIdTerm, synonym + postfix);
        }
    }

    public List<String> getIntermediateAnatomyIdAnatomyIdTerm() {
        return intermediateAnatomyIdAnatomyIdTerm;
    }

    public void setIntermediateAnatomyIdAnatomyIdTerm(List<String> intermediateAnatomyIdAnatomyIdTerm) {
        this.intermediateAnatomyIdAnatomyIdTerm = intermediateAnatomyIdAnatomyIdTerm;
    }

    public void addIntermediateAnatomyIdAnatomyIdTerm(String intermediateAnatomyIdAnatomyIdTerm) {
        this.intermediateAnatomyIdAnatomyIdTerm = add(this.intermediateAnatomyIdAnatomyIdTerm, intermediateAnatomyIdAnatomyIdTerm);
    }

    public List<String> getIntermediateAnatomyTermAnatomyIdTerm() {
        return intermediateAnatomyTermAnatomyIdTerm;
    }

    public void setIntermediateAnatomyTermAnatomyIdTerm(List<String> intermediateAnatomyTermAnatomyIdTerm) {
        this.intermediateAnatomyTermAnatomyIdTerm = intermediateAnatomyTermAnatomyIdTerm;
    }

    public void addIntermediateAnatomyTermAnatomyIdTerm(String intermediateAnatomyTermAnatomyIdTerm) {
        if (this.intermediateAnatomyTermAnatomyIdTerm == null) {
            this.intermediateAnatomyTermAnatomyIdTerm = new ArrayList<>();
        }
        this.intermediateAnatomyTermAnatomyIdTerm.add(intermediateAnatomyTermAnatomyIdTerm);
    }

    public List<String> getIntermediateAnatomyTermSynonymAnatomyIdTerm() {
        return intermediateAnatomyTermSynonymAnatomyIdTerm;
    }

    public void setIntermediateAnatomyTermSynonymAnatomyIdTerm(List<String> intermediateAnatomyTermSynonymAnatomyIdTerm) {
        this.intermediateAnatomyTermSynonymAnatomyIdTerm = intermediateAnatomyTermSynonymAnatomyIdTerm;
    }

    public void addIntermediateAnatomyTermSynonymAnatomyIdTerm(List<String> synonyms, String postfix) {
        for (String synonym : synonyms) {
            this.intermediateAnatomyTermSynonymAnatomyIdTerm = add(this.intermediateAnatomyTermSynonymAnatomyIdTerm, synonym + postfix);
        }
    }

    public void addAnatomyId(String anatomyId) {
        if (this.anatomyId == null) {
            this.anatomyId = new ArrayList<>();
        }
        this.anatomyId.add(anatomyId);
    }

    public void addAnatomyTerm(String anatomyTerm) {
        if (this.anatomyTerm == null) {
            this.anatomyTerm = new ArrayList<>();
        }
        this.anatomyTerm.add(anatomyTerm);
    }

    public List<String> getAnatomyTermSynonymAnatomyIdTerm() {
        return anatomyTermSynonymAnatomyIdTerm;
    }

    public void setAnatomyTermSynonymAnatomyIdTerm(List<String> anatomyTermSynonymAnatomyIdTerm) {
        this.anatomyTermSynonymAnatomyIdTerm = anatomyTermSynonymAnatomyIdTerm;
    }

    public void addAnatomyTermSynonymAnatomyIdTerm(List<String> synonyms, String postfix) {

        for (String syn : synonyms) {
            this.anatomyTermSynonymAnatomyIdTerm = add(this.anatomyTermSynonymAnatomyIdTerm, syn + postfix);
        }
    }

    public List<String> getMpIdTerm() {
        return mpIdTerm;
    }

    public void setMpIdTerm(List<String> mpIdTerm) {
        this.mpIdTerm = mpIdTerm;
    }

    public void addMpId(String mpId, Boolean uniqueOnly) {
        this.mpId = add(this.mpId, mpId, true);
    }

    public void addMpIdTerm(String mpIdTerm, boolean uniqueOnly) {
        this.mpIdTerm = add(this.mpIdTerm, mpIdTerm, true);
    }

    public List<String> getMpId() {
        return mpId;
    }

    public List<String> getMpTerm() {
        return mpTerm;
    }

    public void setMpTerm(List<String> mpTerm) {
        this.mpTerm = mpTerm;
    }

    public void addMpTerm(String mpTerm, Boolean uniqueOnly) {
        this.mpTerm = add(this.mpTerm, mpTerm, uniqueOnly);
    }

    public List<String> getMpTermSynonym() {
        return mpTermSynonym;
    }

    public void addMpTermSynonym(List<String> mpTermSynonym, Boolean uniqueOnly) {
        this.mpTermSynonym = add(this.mpTermSynonym, mpTermSynonym, uniqueOnly);
    }

    public void setMpTermSynonym(List<String> mpTermSynonym) {
        this.mpTermSynonym = mpTermSynonym;
    }

    public void setMpId(List<String> mpId) {
        this.mpId = mpId;
    }

    public Set<String> getMpNarrowSynonym() {
        return mpNarrowSynonym;
    }

    public void setMpNarrowSynonym(Set<String> mpNarrowSynonym) {
        this.mpNarrowSynonym = mpNarrowSynonym;
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

    public List<String> getLatestPhenotypingCentre() {
        return latestPhenotypingCentre;
    }

    public List<String> getAlleleName() {
        return alleleName;
    }

    public List<String> getMarkerSynonym() {
        return markerSynonym;
    }

    public void setMarkerSynonym(List<String> markerSynonym) {
        this.markerSynonym = markerSynonym;
    }

    public String getMarkerType() {
        return markerType;
    }

    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }

    public List<String> getHumanGeneSymbol() {
        return humanGeneSymbol;
    }

    public void setHumanGeneSymbol(List<String> humanGeneSymbol) {
        this.humanGeneSymbol = humanGeneSymbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setIncrement(Integer increment) {
        this.increment = increment;
    }

    public List<String> getLatestPhenotypeStatus() {
        return latestPhenotypeStatus;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getSubtype() {

        return subtype;
    }

    public List<String> getMarkerName() {

        return markerName;
    }

    public void setMarkerName(List<String> markerName) {

        this.markerName = markerName;
    }

    public List<String> getMarkerSymbol() {

        return markerSymbol;
    }

    public void setMarkerSymbol(List<String> markerSymbol) {

        this.markerSymbol = markerSymbol;
    }

    public void setSubtype(String subtype) {

        this.subtype = subtype;
    }

    public String getSymbolGene() {
        if ((this.getGeneSymbol() != null) && (this.getGeneAccession() != null)) {
            this.symbolGene = this.getGeneSymbol() + "_" + this.getGeneAccession();
        }
        return this.symbolGene;
    }


    public List<String> getEfoId() {

        return efoId;
    }

    public void setEfoId(List<String> efoId) {

        this.efoId = efoId;
    }

    public void addEfoId(String id) {
        if (this.efoId == null) {
            this.efoId = new ArrayList<>();
        }
        this.efoId.add(id);
    }

    public List<String> getUberonId() {

        return uberonId;
    }

    public void setUberonId(List<String> uberonId) {

        this.uberonId = uberonId;
    }

    public void addUberonId(String id) {
        if (this.uberonId == null) {
            this.uberonId = new ArrayList<>();
        }
        this.uberonId.add(id);
    }

    public String getDownloadUrl() {

        return downloadUrl;
    }

    public int getOmeroId() {

        return omeroId;
    }

    public void setOmeroId(int omeroId) {

        this.omeroId = omeroId;
    }

    public String getFullResolutionFilePath() {

        return fullResolutionFilePath;
    }

    public void setFullResolutionFilePath(String fullResolutionFilePath) {

        this.fullResolutionFilePath = fullResolutionFilePath;
    }

    public ImageDTO() {
        super();
    }

    public void setDownloadUrl(String downloadUrl) {

        this.downloadUrl = downloadUrl;
    }

    public void setJpegUrl(String jpegUrl) {

        this.jpegUrl = jpegUrl;
    }

    public String getJpegUrl() {

        return jpegUrl;
    }

    public void addStatus(String status1) {

        if (this.status == null) {
            status = new ArrayList<>();
        }
        status.add(status1);
    }

    public void addImitsPhenotypeStarted(String imitsPhenotypeStarted1) {

        if (this.imitsPhenotypeStarted == null) {
            this.imitsPhenotypeStarted = new ArrayList<>();
        }
        this.imitsPhenotypeStarted.add(imitsPhenotypeStarted1);
    }

    public void addImitsPhenotypeComplete(String imitsPhenotypeComplete1) {

        if (this.imitsPhenotypeComplete == null) {
            this.imitsPhenotypeComplete = new ArrayList<>();
        }
        this.imitsPhenotypeComplete.add(imitsPhenotypeComplete1);
    }

    public void addImitsPhenotypeStatus(String imitsPhenotypeStatus1) {

        if (this.imitsPhenotypeStatus == null) {
            this.imitsPhenotypeStatus = new ArrayList<>();
        }
        this.imitsPhenotypeStatus.add(imitsPhenotypeStatus1);
    }

    public void setLegacyPhenotypeStatus(Integer legacyPhenotypeStatus) {

        this.legacyPhenotypeStatus = legacyPhenotypeStatus;
    }

    public Integer getLegacyPhenotypeStatus() {

        return legacyPhenotypeStatus;
    }

    public void setLatestProductionCentre(List<String> latestProductionCentre) {

        this.latestProductionCentre = latestProductionCentre;
    }

    public void setLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {

        this.latestPhenotypingCentre = latestPhenotypingCentre;
    }

    public void setAlleleName(List<String> alleleName) {

        this.alleleName = alleleName;
    }

    public void addMarkerName(String markerName) {
        if (this.markerName == null) {
            this.markerName = new ArrayList<>();
        }
        this.markerName.add(markerName);
    }

    public void addMarkerSynonym(List<String> markerSynonym) {
        if (this.markerSynonym == null) {
            this.markerSynonym = new ArrayList<>();
        }
        this.markerSynonym.addAll(markerSynonym);
    }

    public void addMarkerType(String markerType) {

        this.markerType = markerType;
    }

    public void addHumanGeneSymbol(List<String> humanGeneSymbol) {

        if (this.humanGeneSymbol == null) {
            this.humanGeneSymbol = new ArrayList<>();
        }
        this.humanGeneSymbol.addAll(humanGeneSymbol);
    }

    public void addSymbol(String markerSymbol) {

        this.symbol = markerSymbol;
    }

    public void setLatestPhenotypeStatus(List<String> latestPhenotypeStatus) {

        this.latestPhenotypeStatus = latestPhenotypeStatus;
    }

    public void addLatestPhenotypeStatus(String latestPhenotypeStatus) {
        if (this.latestPhenotypeStatus == null) {
            this.latestPhenotypeStatus = new ArrayList<String>();
        }
        this.latestPhenotypeStatus.add(latestPhenotypeStatus);
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbolGene(String symbolGene) {
        this.symbolGene = symbolGene;

    }

    public String getExpression(String maId) {

        int pos = maId.indexOf(maId);
        return getParameterAssociationValue().get(pos);

    }

    public void setMpTermId(ArrayList<String> mpTermIds) {
        this.mpId = mpTermIds;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageDTO imageDTO = (ImageDTO) o;

        if (omeroId != imageDTO.omeroId) return false;
        if (mpId != null ? !mpId.equals(imageDTO.mpId) : imageDTO.mpId != null) return false;
        if (mpTerm != null ? !mpTerm.equals(imageDTO.mpTerm) : imageDTO.mpTerm != null) return false;
        if (mpIdTerm != null ? !mpIdTerm.equals(imageDTO.mpIdTerm) : imageDTO.mpIdTerm != null) return false;
        if (mpTermSynonym != null ? !mpTermSynonym.equals(imageDTO.mpTermSynonym) : imageDTO.mpTermSynonym != null)
            return false;
        if (mpNarrowSynonym != null ? !mpNarrowSynonym.equals(imageDTO.mpNarrowSynonym) : imageDTO.mpNarrowSynonym != null)
            return false;
        if (fullResolutionFilePath != null ? !fullResolutionFilePath.equals(imageDTO.fullResolutionFilePath) : imageDTO.fullResolutionFilePath != null)
            return false;
        if (downloadUrl != null ? !downloadUrl.equals(imageDTO.downloadUrl) : imageDTO.downloadUrl != null)
            return false;
        if (imageLink != null ? !imageLink.equals(imageDTO.imageLink) : imageDTO.imageLink != null) return false;
        if (jpegUrl != null ? !jpegUrl.equals(imageDTO.jpegUrl) : imageDTO.jpegUrl != null) return false;
        if (efoId != null ? !efoId.equals(imageDTO.efoId) : imageDTO.efoId != null) return false;
        if (uberonId != null ? !uberonId.equals(imageDTO.uberonId) : imageDTO.uberonId != null) return false;
        if (symbolGene != null ? !symbolGene.equals(imageDTO.symbolGene) : imageDTO.symbolGene != null) return false;
        if (status != null ? !status.equals(imageDTO.status) : imageDTO.status != null) return false;
        if (imitsPhenotypeStarted != null ? !imitsPhenotypeStarted.equals(imageDTO.imitsPhenotypeStarted) : imageDTO.imitsPhenotypeStarted != null)
            return false;
        if (imitsPhenotypeComplete != null ? !imitsPhenotypeComplete.equals(imageDTO.imitsPhenotypeComplete) : imageDTO.imitsPhenotypeComplete != null)
            return false;
        if (imitsPhenotypeStatus != null ? !imitsPhenotypeStatus.equals(imageDTO.imitsPhenotypeStatus) : imageDTO.imitsPhenotypeStatus != null)
            return false;
        if (legacyPhenotypeStatus != null ? !legacyPhenotypeStatus.equals(imageDTO.legacyPhenotypeStatus) : imageDTO.legacyPhenotypeStatus != null)
            return false;
        if (latestProductionCentre != null ? !latestProductionCentre.equals(imageDTO.latestProductionCentre) : imageDTO.latestProductionCentre != null)
            return false;
        if (latestPhenotypingCentre != null ? !latestPhenotypingCentre.equals(imageDTO.latestPhenotypingCentre) : imageDTO.latestPhenotypingCentre != null)
            return false;
        if (alleleName != null ? !alleleName.equals(imageDTO.alleleName) : imageDTO.alleleName != null) return false;
        if (markerSymbol != null ? !markerSymbol.equals(imageDTO.markerSymbol) : imageDTO.markerSymbol != null)
            return false;
        if (markerName != null ? !markerName.equals(imageDTO.markerName) : imageDTO.markerName != null) return false;
        if (markerSynonym != null ? !markerSynonym.equals(imageDTO.markerSynonym) : imageDTO.markerSynonym != null)
            return false;
        if (markerType != null ? !markerType.equals(imageDTO.markerType) : imageDTO.markerType != null) return false;
        if (humanGeneSymbol != null ? !humanGeneSymbol.equals(imageDTO.humanGeneSymbol) : imageDTO.humanGeneSymbol != null)
            return false;
        if (symbol != null ? !symbol.equals(imageDTO.symbol) : imageDTO.symbol != null) return false;
        if (subtype != null ? !subtype.equals(imageDTO.subtype) : imageDTO.subtype != null) return false;
        if (increment != null ? !increment.equals(imageDTO.increment) : imageDTO.increment != null) return false;
        if (latestPhenotypeStatus != null ? !latestPhenotypeStatus.equals(imageDTO.latestPhenotypeStatus) : imageDTO.latestPhenotypeStatus != null)
            return false;
        if (markerSynonymSymbolGene != null ? !markerSynonymSymbolGene.equals(imageDTO.markerSynonymSymbolGene) : imageDTO.markerSynonymSymbolGene != null)
            return false;
        if (parameterAssociationNameProcedureName != null ? !parameterAssociationNameProcedureName.equals(imageDTO.parameterAssociationNameProcedureName) : imageDTO.parameterAssociationNameProcedureName != null)
            return false;
        if (selectedTopLevelAnatomyIdAnatomyIdTerm != null ? !selectedTopLevelAnatomyIdAnatomyIdTerm.equals(imageDTO.selectedTopLevelAnatomyIdAnatomyIdTerm) : imageDTO.selectedTopLevelAnatomyIdAnatomyIdTerm != null)
            return false;
        if (selectedTopLevelAnatomyTermAnatomyIdTerm != null ? !selectedTopLevelAnatomyTermAnatomyIdTerm.equals(imageDTO.selectedTopLevelAnatomyTermAnatomyIdTerm) : imageDTO.selectedTopLevelAnatomyTermAnatomyIdTerm != null)
            return false;
        if (selectedTopLevelAnatomyTermSynonymAnatomyIdTerm != null ? !selectedTopLevelAnatomyTermSynonymAnatomyIdTerm.equals(imageDTO.selectedTopLevelAnatomyTermSynonymAnatomyIdTerm) : imageDTO.selectedTopLevelAnatomyTermSynonymAnatomyIdTerm != null)
            return false;
        if (intermediateAnatomyIdAnatomyIdTerm != null ? !intermediateAnatomyIdAnatomyIdTerm.equals(imageDTO.intermediateAnatomyIdAnatomyIdTerm) : imageDTO.intermediateAnatomyIdAnatomyIdTerm != null)
            return false;
        if (intermediateAnatomyTermAnatomyIdTerm != null ? !intermediateAnatomyTermAnatomyIdTerm.equals(imageDTO.intermediateAnatomyTermAnatomyIdTerm) : imageDTO.intermediateAnatomyTermAnatomyIdTerm != null)
            return false;
        if (intermediateAnatomyTermSynonymAnatomyIdTerm != null ? !intermediateAnatomyTermSynonymAnatomyIdTerm.equals(imageDTO.intermediateAnatomyTermSynonymAnatomyIdTerm) : imageDTO.intermediateAnatomyTermSynonymAnatomyIdTerm != null)
            return false;
        if (anatomyIdTerm != null ? !anatomyIdTerm.equals(imageDTO.anatomyIdTerm) : imageDTO.anatomyIdTerm != null)
            return false;
        return anatomyTermSynonymAnatomyIdTerm != null ? anatomyTermSynonymAnatomyIdTerm.equals(imageDTO.anatomyTermSynonymAnatomyIdTerm) : imageDTO.anatomyTermSynonymAnatomyIdTerm == null;

    }

    @Override
    public int hashCode() {
        int result = mpId != null ? mpId.hashCode() : 0;
        result = 31 * result + (mpTerm != null ? mpTerm.hashCode() : 0);
        result = 31 * result + (mpIdTerm != null ? mpIdTerm.hashCode() : 0);
        result = 31 * result + (mpTermSynonym != null ? mpTermSynonym.hashCode() : 0);
        result = 31 * result + (mpNarrowSynonym != null ? mpNarrowSynonym.hashCode() : 0);
        result = 31 * result + (fullResolutionFilePath != null ? fullResolutionFilePath.hashCode() : 0);
        result = 31 * result + omeroId;
        result = 31 * result + (downloadUrl != null ? downloadUrl.hashCode() : 0);
        result = 31 * result + (imageLink != null ? imageLink.hashCode() : 0);
        result = 31 * result + (jpegUrl != null ? jpegUrl.hashCode() : 0);
        result = 31 * result + (efoId != null ? efoId.hashCode() : 0);
        result = 31 * result + (uberonId != null ? uberonId.hashCode() : 0);
        result = 31 * result + (symbolGene != null ? symbolGene.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (imitsPhenotypeStarted != null ? imitsPhenotypeStarted.hashCode() : 0);
        result = 31 * result + (imitsPhenotypeComplete != null ? imitsPhenotypeComplete.hashCode() : 0);
        result = 31 * result + (imitsPhenotypeStatus != null ? imitsPhenotypeStatus.hashCode() : 0);
        result = 31 * result + (legacyPhenotypeStatus != null ? legacyPhenotypeStatus.hashCode() : 0);
        result = 31 * result + (latestProductionCentre != null ? latestProductionCentre.hashCode() : 0);
        result = 31 * result + (latestPhenotypingCentre != null ? latestPhenotypingCentre.hashCode() : 0);
        result = 31 * result + (alleleName != null ? alleleName.hashCode() : 0);
        result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
        result = 31 * result + (markerName != null ? markerName.hashCode() : 0);
        result = 31 * result + (markerSynonym != null ? markerSynonym.hashCode() : 0);
        result = 31 * result + (markerType != null ? markerType.hashCode() : 0);
        result = 31 * result + (humanGeneSymbol != null ? humanGeneSymbol.hashCode() : 0);
        result = 31 * result + (symbol != null ? symbol.hashCode() : 0);
        result = 31 * result + (subtype != null ? subtype.hashCode() : 0);
        result = 31 * result + (increment != null ? increment.hashCode() : 0);
        result = 31 * result + (latestPhenotypeStatus != null ? latestPhenotypeStatus.hashCode() : 0);
        result = 31 * result + (markerSynonymSymbolGene != null ? markerSynonymSymbolGene.hashCode() : 0);
        result = 31 * result + (parameterAssociationNameProcedureName != null ? parameterAssociationNameProcedureName.hashCode() : 0);
        result = 31 * result + (selectedTopLevelAnatomyIdAnatomyIdTerm != null ? selectedTopLevelAnatomyIdAnatomyIdTerm.hashCode() : 0);
        result = 31 * result + (selectedTopLevelAnatomyTermAnatomyIdTerm != null ? selectedTopLevelAnatomyTermAnatomyIdTerm.hashCode() : 0);
        result = 31 * result + (selectedTopLevelAnatomyTermSynonymAnatomyIdTerm != null ? selectedTopLevelAnatomyTermSynonymAnatomyIdTerm.hashCode() : 0);
        result = 31 * result + (intermediateAnatomyIdAnatomyIdTerm != null ? intermediateAnatomyIdAnatomyIdTerm.hashCode() : 0);
        result = 31 * result + (intermediateAnatomyTermAnatomyIdTerm != null ? intermediateAnatomyTermAnatomyIdTerm.hashCode() : 0);
        result = 31 * result + (intermediateAnatomyTermSynonymAnatomyIdTerm != null ? intermediateAnatomyTermSynonymAnatomyIdTerm.hashCode() : 0);
        result = 31 * result + (anatomyIdTerm != null ? anatomyIdTerm.hashCode() : 0);
        result = 31 * result + (anatomyTermSynonymAnatomyIdTerm != null ? anatomyTermSynonymAnatomyIdTerm.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ImageDTO{" +
                "mpId=" + mpId +
                ", mpTerm=" + mpTerm +
                ", mpIdTerm=" + mpIdTerm +
                ", mpTermSynonym=" + mpTermSynonym +
                ", mpNarrowSynonym=" + mpNarrowSynonym +
                ", fullResolutionFilePath='" + fullResolutionFilePath + '\'' +
                ", omeroId=" + omeroId +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", jpegUrl='" + jpegUrl + '\'' +
                ", efoId=" + efoId +
                ", uberonId=" + uberonId +
                ", symbolGene='" + symbolGene + '\'' +
                ", status=" + status +
                ", imitsPhenotypeStarted=" + imitsPhenotypeStarted +
                ", imitsPhenotypeComplete=" + imitsPhenotypeComplete +
                ", imitsPhenotypeStatus=" + imitsPhenotypeStatus +
                ", legacyPhenotypeStatus=" + legacyPhenotypeStatus +
                ", latestProductionCentre=" + latestProductionCentre +
                ", latestPhenotypingCentre=" + latestPhenotypingCentre +
                ", alleleName=" + alleleName +
                ", markerSymbol=" + markerSymbol +
                ", markerName=" + markerName +
                ", markerSynonym=" + markerSynonym +
                ", markerType='" + markerType + '\'' +
                ", humanGeneSymbol=" + humanGeneSymbol +
                ", symbol='" + symbol + '\'' +
                ", subtype='" + subtype + '\'' +
                ", increment=" + increment +
                ", latestPhenotypeStatus=" + latestPhenotypeStatus +
                ", markerSynonymSymbolGene=" + markerSynonymSymbolGene +
                ", parameterAssociationNameProcedureName=" + parameterAssociationNameProcedureName +
                ", selectedTopLevelAnatomyIdAnatomyIdTerm=" + selectedTopLevelAnatomyIdAnatomyIdTerm +
                ", selectedTopLevelAnatomyTermAnatomyIdTerm=" + selectedTopLevelAnatomyTermAnatomyIdTerm +
                ", selectedTopLevelAnatomyTermSynonymAnatomyIdTerm=" + selectedTopLevelAnatomyTermSynonymAnatomyIdTerm +
                ", intermediateAnatomyIdAnatomyIdTerm=" + intermediateAnatomyIdAnatomyIdTerm +
                ", intermediateAnatomyTermAnatomyIdTerm=" + intermediateAnatomyTermAnatomyIdTerm +
                ", intermediateAnatomyTermSynonymAnatomyIdTerm=" + intermediateAnatomyTermSynonymAnatomyIdTerm +
                ", anatomyIdTerm=" + anatomyIdTerm +
                ", anatomyTermSynonymAnatomyIdTerm=" + anatomyTermSynonymAnatomyIdTerm +
                '}';
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;

    }

    private List<String> add(List<String> to, String what, Boolean uniqueOnly) {
        if (to == null) {
            to = new ArrayList<>();
        }
        if (!uniqueOnly || !to.contains(what)) {
            to.add(what);
        }
        return to;
    }

    private List<String> add(List<String> to, String what) {
        if (to == null) {
            to = new ArrayList<>();
        }
        to.add(what);
        return to;
    }

    private List<String> add(List<String> to, List<String> what, Boolean uniqueOnly) {
        if (to == null) {
            to = new ArrayList<>();
        }
        if (uniqueOnly) {
            addUnique(to, what);
        } else {
            to.addAll(what);
        }
        return to;
    }

    private List<String> addUnique(List<String> toList, List<String> fromList) {

        for (String o : fromList) {
            if (!toList.contains(o)) {
                toList.add(o);
            }
        }
        return toList;
    }


}
