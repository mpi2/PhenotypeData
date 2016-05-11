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

	public static final String FULL_RESOLUTION_FILE_PATH="full_resolution_file_path";

	public static final String OMERO_ID = "omero_id";
	public static final String DOWNLOAD_URL = "download_url";
	public static final String JPEG_URL = "jpeg_url";
	public static final String IMAGE_LINK = "image_link";
	
	public static final String EFO_ID = "efo_id";
	public static final String UBERON_ID = "uberon_id";
	
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
	
	public static final String MA_ID = "ma_id";
	public static final String MA_TERM = "ma_term";
	public static final String MA_ID_TERM = "ma_id_term";
	public static final String MA_TERM_SYNONYM = "ma_term_synonym";
	public static final String SELECTED_TOP_LEVEL_MA_ID = "selected_top_level_ma_id";
	public static final String SELECTED_TOP_LEVEL_MA_TERM = "selected_top_level_ma_term";
	public static final String SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = "selected_top_level_ma_term_synonym";
	
	public static final String INTERMEDIATE_LEVEL_MA_TERM_ID = "intermediate_ma_term_id";
	public static final String INTERMEDIATE_LEVEL_MA_TERM = "intermediate_ma_term";
	public static final String INTERMEDIATE_LEVEL_MA_TERM_SYNONYM = "intermediate_ma_term_synonym";
	
	public static final String EMAP_ID = "emap_id";
	public static final String EMAP_TERM = "emap_term";
	public static final String EMAP_ID_TERM = "emap_id_term";
	public static final String EMAP_TERM_SYNONYM = "emap_term_synonym";
	public static final String SELECTED_TOP_LEVEL_EMAP_ID = "selected_top_level_emap_id";
	public static final String SELECTED_TOP_LEVEL_EMAP_TERM = "selected_top_level_emap_term";
	public static final String SELECTED_TOP_LEVEL_EMAP_TERM_SYNONYM = "selected_top_level_emap_term_synonym";
	
	public static final String INTERMEDIATE_LEVEL_EMAP_TERM_ID = "intermediate_emap_term_id";
	public static final String INTERMEDIATE_LEVEL_EMAP_TERM = "intermediate_emap_term";
	public static final String INTERMEDIATE_LEVEL_EMAP_TERM_SYNONYM = "intermediate_emap_term_synonym";
	public static final String INCREMENT_VALUE="increment_value";

	@Field(MA_ID)
	private List<String> maTermId;
	@Field(MA_TERM)
	private List<String> maTerm;

	@Field(MA_ID_TERM)
	private List<String> maIdTerm;

	@Field(MA_TERM_SYNONYM)
	private List<String> maTermSynonym;

	@Field(SELECTED_TOP_LEVEL_MA_ID)
	private List<String> topLevelMaIds;

	@Field(SELECTED_TOP_LEVEL_MA_TERM)
	private List<String> topLeveMaTerm;

	@Field(SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	private List<String> topLevelMaTermSynonym;
	
	@Field(INTERMEDIATE_LEVEL_MA_TERM_ID)
	private ArrayList<String> intermediateLevelMaId;

	@Field(INTERMEDIATE_LEVEL_MA_TERM)
	private ArrayList<String> intermediateLevelMaTerm;
	
	@Field(INTERMEDIATE_LEVEL_MA_TERM_SYNONYM)
	private ArrayList<String> intermediateLevelMaTermSynonym;
	
	@Field(MpDTO.MP_ID)
	private ArrayList<String> mpTermIds;
	
	@Field(MpDTO.MP_TERM)
	private List<String> mpTerm;

	@Field(MpDTO.MP_ID)
	private List<String> mpIdTerm;

	@Field(MpDTO.MP_TERM_SYNONYM)
	private List<String> mpTermSynonym;
	
	public ArrayList<String> getMpTermIds() {
		return mpTermIds;
	}

	public void setMpTermIds(ArrayList<String> mpTermIds) {
		this.mpTermIds = mpTermIds;
	}

	public List<String> getMpTerm() {
		return mpTerm;
	}

	public void setMpTerm(List<String> mpTerm) {
		this.mpTerm = mpTerm;
	}

	public List<String> getMpIdTerm() {
		return mpIdTerm;
	}

	public void setMpIdTerm(List<String> mpIdTerm) {
		this.mpIdTerm = mpIdTerm;
	}

	public List<String> getMpTermSynonym() {
		return mpTermSynonym;
	}

	public void setMpTermSynonym(List<String> mpTermSynonym) {
		this.mpTermSynonym = mpTermSynonym;
	}
	
	
	@Field(EMAP_ID)
	private List<String> emapTermId;
	@Field(EMAP_TERM)
	private List<String> emapTerm;

	@Field(EMAP_ID_TERM)
	private List<String> emapIdTerm;

	@Field(EMAP_TERM_SYNONYM)
	private List<String> emapTermSynonym;

	@Field(SELECTED_TOP_LEVEL_EMAP_ID)
	private List<String> topLevelEmapIds;

	@Field(SELECTED_TOP_LEVEL_EMAP_TERM)
	private List<String> topLeveEmapTerm;

	@Field(SELECTED_TOP_LEVEL_EMAP_TERM_SYNONYM)
	private List<String> topLevelEmapTermSynonym;
	
	@Field(INTERMEDIATE_LEVEL_EMAP_TERM_ID)
	private ArrayList<String> intermediateLevelEmapId;

	@Field(INTERMEDIATE_LEVEL_EMAP_TERM)
	private ArrayList<String> intermediateLevelEmapTerm;
	
	@Field(INTERMEDIATE_LEVEL_EMAP_TERM_SYNONYM)
	private ArrayList<String> intermediateLevelEmapTermSynonym;
	
	@Field(FULL_RESOLUTION_FILE_PATH)
	private String fullResolutionFilePath;

	@Field(OMERO_ID)
	private int omeroId;

	@Field(DOWNLOAD_URL)
	private String downloadUrl;

	@Field(IMAGE_LINK)
	private String imageLink;

	@Field(JPEG_URL)
	private String jpegUrl;

	@Field(EFO_ID)
	private List<String> efoId;
	
	@Field(UBERON_ID)
	private List<String> uberonId;
	
	

	@Field(SYMBOL_GENE)
	private String symbolGene;//for search and annotation view

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

	

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	@Field(LATEST_PHENOTYPE_STATUS)
	private List<String> latestPhenotypeStatus;


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
		if((this.getGeneSymbol()!=null)&&(this.getGeneAccession()!=null)){
			this.symbolGene=this.getGeneSymbol()+"_"+this.getGeneAccession();
			}
		return this.symbolGene;
	}

	public List<String> getMaTermId() {

		return maTermId;
	}

	public void setMaTermId(List<String> maTermId) {

		this.maTermId = maTermId;
	}

	public List<String> getEfoId() {

		return efoId;
	}

	public void setEfoId(List<String> efoId) {

		this.efoId = efoId;
	}
	
	public void addEfoId(String id) {
		if(this.efoId==null){
			this.efoId=new ArrayList<>();
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
		if(this.uberonId==null){
			this.uberonId=new ArrayList<>();
		}
		this.uberonId.add(id);
	}
	
	public List<String> getMaIdTerm() {

		return maIdTerm;
	}

	public void setMaIdTerm(List<String> maIdTerms) {

		this.maIdTerm = maIdTerms;
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

	public ImageDTO(){
		super();
	}

	public void setDownloadUrl(String downloadUrl) {

		this.downloadUrl=downloadUrl;
	}

	public void setJpegUrl(String jpegUrl) {

		this.jpegUrl=jpegUrl;
	}

	public String getJpegUrl() {

		return jpegUrl;
	}

	public void addStatus(String status1) {

		if(this.status==null){
			status=new ArrayList<String>();
		}
		status.add(status1);
	}

	public void addImitsPhenotypeStarted(String imitsPhenotypeStarted1) {

		if(this.imitsPhenotypeStarted==null){
			this.imitsPhenotypeStarted=new ArrayList<String>();
		}
		this.imitsPhenotypeStarted.add(imitsPhenotypeStarted1);
	}

	public void addImitsPhenotypeComplete(String imitsPhenotypeComplete1) {

		if(this.imitsPhenotypeComplete==null){
			this.imitsPhenotypeComplete=new ArrayList<String>();
		}
		this.imitsPhenotypeComplete.add(imitsPhenotypeComplete1);
	}

	public void addImitsPhenotypeStatus(String imitsPhenotypeStatus1) {

		if(this.imitsPhenotypeStatus==null){
			this.imitsPhenotypeStatus=new ArrayList<String>();
		}
		this.imitsPhenotypeStatus.add(imitsPhenotypeStatus1);
	}

	public void setLegacyPhenotypeStatus(Integer legacyPhenotypeStatus) {

		this.legacyPhenotypeStatus=legacyPhenotypeStatus;
	}

	public Integer getLegacyPhenotypeStatus() {

		return legacyPhenotypeStatus;
	}

	public void setLatestProductionCentre(List<String> latestProductionCentre) {

		this.latestProductionCentre=latestProductionCentre;
	}

	public void setLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {

		this.latestPhenotypingCentre=latestPhenotypingCentre;
	}

	public void setAlleleName(List<String> alleleName) {

		this.alleleName=alleleName;
	}

	public void addMarkerName(String markerName) {
		if(this.markerName==null){
			this.markerName=new ArrayList<>();
		}
		this.markerName.add(markerName);
	}

	public void addMarkerSynonym(List<String> markerSynonym) {
		if(this.markerSynonym==null){
			this.markerSynonym=new ArrayList<>();
		}
		this.markerSynonym.addAll(markerSynonym);
	}

	public void addMarkerType(String markerType) {

		this.markerType=markerType;
	}

	public void addHumanGeneSymbol(List<String> humanGeneSymbol) {

		if(this.humanGeneSymbol==null){
			this.humanGeneSymbol=new ArrayList<String>();
		}
		this.humanGeneSymbol.addAll(humanGeneSymbol);
	}

	public void addSymbol(String markerName) {

		this.symbol=markerName;
	}

	public void setLatestPhenotypeStatus(List<String> latestPhenotypeStatus) {

		this.latestPhenotypeStatus=latestPhenotypeStatus;
	}

	public void addLatestPhenotypeStatus(String latestPhenotypeStatus) {
		if(this.latestPhenotypeStatus==null){
			this.latestPhenotypeStatus=new ArrayList<String>();
		}
		this.latestPhenotypeStatus.add(latestPhenotypeStatus);
	}

	public String getSymbol() {
		// TODO Auto-generated method stub
		return symbol;
	}

	public void setSymbolGene(String symbolGene) {
		this.symbolGene=symbolGene;

	}

	public List<String> getMaTerm() {
		return maTerm;
	}

	public void setMaTerm(List<String> maTerm) {
		this.maTerm = maTerm;
	}

	public List<String> getMaTermSynonym() {
		return maTermSynonym;
	}

	public void setMaTermSynonym(List<String> maTermSynonym) {
		this.maTermSynonym = maTermSynonym;
	}

	public void setTopLevelMaId(ArrayList<String> topLevelMaIds) {
		this.topLevelMaIds=topLevelMaIds;
	}

	public void setTopLevelMaTerm(ArrayList<String> topLevelMaTerm) {
		this.topLeveMaTerm=topLevelMaTerm;
	}

	public void setTopLevelMaTermSynonym(ArrayList<String> topLevelMaTermSynonym) {
		this.topLevelMaTermSynonym=topLevelMaTermSynonym;
	}

	public List<String> getTopLevelMaIds() {
		return topLevelMaIds;
	}

	public List<String> getTopLeveMaTerm() {
		return topLeveMaTerm;
	}

	public List<String> getTopLevelMaTermSynonym() {
		return topLevelMaTermSynonym;
	}

	public String getExpression(String maId){

		int pos = maTermId.indexOf(maId);
		return getParameterAssociationValue().get(pos);

	}

	public void setIntermediateLevelMaId(ArrayList<String> intermediateLevelMaId) {
		this.intermediateLevelMaId=intermediateLevelMaId;
	}

	public void setIntermediateLevelMaTerm(ArrayList<String> intermediateLevelMaTerm) {
		this.intermediateLevelMaTerm=intermediateLevelMaTerm;
	}

	public void setIntermediateLevelMaTermSynonym(
			ArrayList<String> intermediateLevelMaTermSynonym) {
		this.intermediateLevelMaTermSynonym=intermediateLevelMaTermSynonym;
	}

	public static String getIntermediateLevelMaTermId() {
		return INTERMEDIATE_LEVEL_MA_TERM_ID;
	}


	public static String getIntermediateLevelMaTerm() {
		return INTERMEDIATE_LEVEL_MA_TERM;
	}


	public static String getIntermediateLevelMaTermSynonym() {
		return INTERMEDIATE_LEVEL_MA_TERM_SYNONYM;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alleleName == null) ? 0 : alleleName.hashCode());
		result = prime * result + ((downloadUrl == null) ? 0 : downloadUrl.hashCode());
		result = prime * result + ((efoId == null) ? 0 : efoId.hashCode());
		result = prime * result + ((fullResolutionFilePath == null) ? 0 : fullResolutionFilePath.hashCode());
		result = prime * result + ((humanGeneSymbol == null) ? 0 : humanGeneSymbol.hashCode());
		result = prime * result + ((imageLink == null) ? 0 : imageLink.hashCode());
		result = prime * result + ((imitsPhenotypeComplete == null) ? 0 : imitsPhenotypeComplete.hashCode());
		result = prime * result + ((imitsPhenotypeStarted == null) ? 0 : imitsPhenotypeStarted.hashCode());
		result = prime * result + ((imitsPhenotypeStatus == null) ? 0 : imitsPhenotypeStatus.hashCode());
		result = prime * result + ((intermediateLevelMaId == null) ? 0 : intermediateLevelMaId.hashCode());
		result = prime * result + ((intermediateLevelMaTerm == null) ? 0 : intermediateLevelMaTerm.hashCode());
		result = prime * result
				+ ((intermediateLevelMaTermSynonym == null) ? 0 : intermediateLevelMaTermSynonym.hashCode());
		result = prime * result + ((jpegUrl == null) ? 0 : jpegUrl.hashCode());
		result = prime * result + ((latestPhenotypeStatus == null) ? 0 : latestPhenotypeStatus.hashCode());
		result = prime * result + ((latestPhenotypingCentre == null) ? 0 : latestPhenotypingCentre.hashCode());
		result = prime * result + ((latestProductionCentre == null) ? 0 : latestProductionCentre.hashCode());
		result = prime * result + ((legacyPhenotypeStatus == null) ? 0 : legacyPhenotypeStatus.hashCode());
		result = prime * result + ((maIdTerm == null) ? 0 : maIdTerm.hashCode());
		result = prime * result + ((maTerm == null) ? 0 : maTerm.hashCode());
		result = prime * result + ((maTermId == null) ? 0 : maTermId.hashCode());
		result = prime * result + ((maTermSynonym == null) ? 0 : maTermSynonym.hashCode());
		result = prime * result + ((markerName == null) ? 0 : markerName.hashCode());
		result = prime * result + ((markerSymbol == null) ? 0 : markerSymbol.hashCode());
		result = prime * result + ((markerSynonym == null) ? 0 : markerSynonym.hashCode());
		result = prime * result + ((markerType == null) ? 0 : markerType.hashCode());
		result = prime * result + omeroId;
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((subtype == null) ? 0 : subtype.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((symbolGene == null) ? 0 : symbolGene.hashCode());
		result = prime * result + ((topLeveMaTerm == null) ? 0 : topLeveMaTerm.hashCode());
		result = prime * result + ((topLevelMaIds == null) ? 0 : topLevelMaIds.hashCode());
		result = prime * result + ((topLevelMaTermSynonym == null) ? 0 : topLevelMaTermSynonym.hashCode());
		result = prime * result + ((uberonId == null) ? 0 : uberonId.hashCode());
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
		ImageDTO other = (ImageDTO) obj;
		if (alleleName == null) {
			if (other.alleleName != null)
				return false;
		} else if (!alleleName.equals(other.alleleName))
			return false;
		if (downloadUrl == null) {
			if (other.downloadUrl != null)
				return false;
		} else if (!downloadUrl.equals(other.downloadUrl))
			return false;
		if (efoId == null) {
			if (other.efoId != null)
				return false;
		} else if (!efoId.equals(other.efoId))
			return false;
		if (fullResolutionFilePath == null) {
			if (other.fullResolutionFilePath != null)
				return false;
		} else if (!fullResolutionFilePath.equals(other.fullResolutionFilePath))
			return false;
		if (humanGeneSymbol == null) {
			if (other.humanGeneSymbol != null)
				return false;
		} else if (!humanGeneSymbol.equals(other.humanGeneSymbol))
			return false;
		if (imageLink == null) {
			if (other.imageLink != null)
				return false;
		} else if (!imageLink.equals(other.imageLink))
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
		if (intermediateLevelMaId == null) {
			if (other.intermediateLevelMaId != null)
				return false;
		} else if (!intermediateLevelMaId.equals(other.intermediateLevelMaId))
			return false;
		if (intermediateLevelMaTerm == null) {
			if (other.intermediateLevelMaTerm != null)
				return false;
		} else if (!intermediateLevelMaTerm.equals(other.intermediateLevelMaTerm))
			return false;
		if (intermediateLevelMaTermSynonym == null) {
			if (other.intermediateLevelMaTermSynonym != null)
				return false;
		} else if (!intermediateLevelMaTermSynonym.equals(other.intermediateLevelMaTermSynonym))
			return false;
		if (jpegUrl == null) {
			if (other.jpegUrl != null)
				return false;
		} else if (!jpegUrl.equals(other.jpegUrl))
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
		if (maIdTerm == null) {
			if (other.maIdTerm != null)
				return false;
		} else if (!maIdTerm.equals(other.maIdTerm))
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
		if (omeroId != other.omeroId)
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
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
		if (topLeveMaTerm == null) {
			if (other.topLeveMaTerm != null)
				return false;
		} else if (!topLeveMaTerm.equals(other.topLeveMaTerm))
			return false;
		if (topLevelMaIds == null) {
			if (other.topLevelMaIds != null)
				return false;
		} else if (!topLevelMaIds.equals(other.topLevelMaIds))
			return false;
		if (topLevelMaTermSynonym == null) {
			if (other.topLevelMaTermSynonym != null)
				return false;
		} else if (!topLevelMaTermSynonym.equals(other.topLevelMaTermSynonym))
			return false;
		if (uberonId == null) {
			if (other.uberonId != null)
				return false;
		} else if (!uberonId.equals(other.uberonId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ImageDTO [fullResolutionFilePath=" + fullResolutionFilePath + ", omeroId=" + omeroId + ", downloadUrl="
				+ downloadUrl + ", imageLink=" + imageLink + ", jpegUrl=" + jpegUrl + ", maTermId=" + maTermId
				+ ", efoId=" + efoId + ", uberonId=" + uberonId + ", maTerm=" + maTerm + ", maIdTerm=" + maIdTerm
				+ ", maTermSynonym=" + maTermSynonym + ", topLevelMaIds=" + topLevelMaIds + ", topLeveMaTerm="
				+ topLeveMaTerm + ", topLevelMaTermSynonym=" + topLevelMaTermSynonym + ", symbolGene=" + symbolGene
				+ ", status=" + status + ", imitsPhenotypeStarted=" + imitsPhenotypeStarted
				+ ", imitsPhenotypeComplete=" + imitsPhenotypeComplete + ", imitsPhenotypeStatus="
				+ imitsPhenotypeStatus + ", legacyPhenotypeStatus=" + legacyPhenotypeStatus
				+ ", latestProductionCentre=" + latestProductionCentre + ", latestPhenotypingCentre="
				+ latestPhenotypingCentre + ", alleleName=" + alleleName + ", markerSymbol=" + markerSymbol
				+ ", markerName=" + markerName + ", markerSynonym=" + markerSynonym + ", markerType=" + markerType
				+ ", humanGeneSymbol=" + humanGeneSymbol + ", symbol=" + symbol + ", subtype=" + subtype
				+ ", latestPhenotypeStatus=" + latestPhenotypeStatus + ", intermediateLevelMaId="
				+ intermediateLevelMaId + ", intermediateLevelMaTerm=" + intermediateLevelMaTerm
				+ ", intermediateLevelMaTermSynonym=" + intermediateLevelMaTermSynonym + "]";
	}

	public List<String> getEmapTermId() {
		return emapTermId;
	}

	public void setEmapTermId(List<String> emapTermId) {
		this.emapTermId = emapTermId;
	}

	public List<String> getEmapTerm() {
		return emapTerm;
	}

	public void setEmapTerm(List<String> emapTerm) {
		this.emapTerm = emapTerm;
	}

	public List<String> getEmapIdTerm() {
		return emapIdTerm;
	}

	public void setEmapIdTerm(List<String> emapIdTerm) {
		this.emapIdTerm = emapIdTerm;
	}

	public List<String> getEmapTermSynonym() {
		return emapTermSynonym;
	}

	public void setEmapTermSynonym(List<String> emapTermSynonym) {
		this.emapTermSynonym = emapTermSynonym;
	}

	public List<String> getTopLevelEmapIds() {
		return topLevelEmapIds;
	}

	public void setTopLevelEmapIds(List<String> topLevelEmapIds) {
		this.topLevelEmapIds = topLevelEmapIds;
	}

	public List<String> getTopLeveEmapTerm() {
		return topLeveEmapTerm;
	}

	public void setTopLeveEmapTerm(List<String> topLeveEmapTerm) {
		this.topLeveEmapTerm = topLeveEmapTerm;
	}

	public List<String> getTopLevelEmapTermSynonym() {
		return topLevelEmapTermSynonym;
	}

	public void setTopLevelEmapTermSynonym(List<String> topLevelEmapTermSynonym) {
		this.topLevelEmapTermSynonym = topLevelEmapTermSynonym;
	}

	public ArrayList<String> getIntermediateLevelEmapId() {
		return intermediateLevelEmapId;
	}

	public void setIntermediateLevelEmapId(ArrayList<String> intermediateLevelEmapId) {
		this.intermediateLevelEmapId = intermediateLevelEmapId;
	}

	public ArrayList<String> getIntermediateLevelEmapTerm() {
		return intermediateLevelEmapTerm;
	}

	public void setIntermediateLevelEmapTerm(ArrayList<String> intermediateLevelEmapTerm) {
		this.intermediateLevelEmapTerm = intermediateLevelEmapTerm;
	}

	public ArrayList<String> getIntermediateLevelEmapTermSynonym() {
		return intermediateLevelEmapTermSynonym;
	}

	public void setIntermediateLevelEmapTermSynonym(ArrayList<String> intermediateLevelEmapTermSynonym) {
		this.intermediateLevelEmapTermSynonym = intermediateLevelEmapTermSynonym;
	}

	public void setMpTermId(ArrayList<String> mpTermIds) {
		this.mpTermIds=mpTermIds;
		
	}


	
	
	
}
