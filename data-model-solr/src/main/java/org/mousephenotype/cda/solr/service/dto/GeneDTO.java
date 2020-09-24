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

import static org.mousephenotype.cda.solr.service.dto.AlleleDTO.CHR_END;
import static org.mousephenotype.cda.solr.service.dto.AlleleDTO.CHR_START;

public class GeneDTO {

	public static final String DATA_TYPE = "dataType";
	public static final String MGI_ACCESSION_ID = "mgi_accession_id";

	public static final String MARKER_SYMBOL = "marker_symbol";
	public static final String MARKER_SYMBOL_LOWERCASE = "marker_symbol_lowercase";

	public static final String HUMAN_GENE_SYMBOL = "human_gene_symbol";
	public static final String HUMAN_GENE_SYMBOL_LOWERCASE = "human_gene_symbol_lowercase";
	public static final String HUMAN_SYMBOL_SYNONYM = "human_symbol_synonym";
	public static final String HUMAN_SYMBOL_SYNONYM_LOWERCASE = "human_symbol_synonym_lowercase";

	public static final String MARKER_NAME = "marker_name";
	public static final String MARKER_SYNONYM = "marker_synonym";
	public static final String MARKER_SYNONYM_LOWERCASE = "marker_synonym_lowercase";
	public static final String MARKER_TYPE = "marker_type";
	public static final String ENSEMBL_GENE_ID = "ensembl_gene_id";
	public static final String IMITS_PHENOTYPE_STARTED = "imits_phenotype_started";
	public static final String IMITS_PHENOTYPE_COMPLETE = "imits_phenotype_complete";
	public static final String IMITS_PHENOTYPE_STATUS = "imits_phenotype_status";
	public static final String STATUS = "status";
	public static final String LATEST_ES_CELL_STATUS = "latest_es_cell_status";
	public static final String LATEST_MOUSE_STATUS = "latest_mouse_status";
	public static final String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
	public static final String LATEST_PROJECT_STATUS = "latest_project_status";
	public static final String LATEST_PRODUCTION_CENTRE = "latest_production_centre";
	public static final String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";
	public static final String DISEASE_HUMAN_PHENOTYPES = "disease_human_phenotypes";
	public static final String HAS_QC = "hasQc";
	public static final String LEGACY_PHENOTYPE_STATUS = "legacy_phenotype_status";
	public static final String ALLELE_NAME = "allele_name";
	public static final String ALLELE_ACCESSION_ID = "allele_accession_id";
	public static final String IMITS_ES_CELL_STATUS = "imits_es_cell_status";
	public static final String ES_CELL_STATUS = "es_cell_status";
	public static final String IMITS_MOUSE_STATUS = "imits_mouse_status";
	public static final String MOUSE_STATUS = "mouse_status";
	public static final String PHENOTYPE_STATUS = "phenotype_status";
	public static final String PRODUCTION_CENTRE = "production_centre";
	public static final String PHENOTYPING_CENTRE = "phenotyping_centre";
	public static final String P_VALUE = "p_value";

	public static final String MP_ID = "mp_id";
	public static final String MP_TERM = "mp_term";
	public static final String MP_TERM_DEFINITION = "mp_term_definition";
	public static final String MP_TERM_SYNONYM = "mp_term_synonym";

	public static final String MA_ID = "ma_id";
	public static final String MA_TERM = "ma_term";
	public static final String MA_TERM_SYNONYM = "ma_term_synonym";
	public static final String MA_TERM_DEFINITION = "ma_term_definition";

	public static final String HP_ID = "hp_id";
	public static final String HP_TERM = "hp_term";


	public static final String CHILD_MP_ID = "child_mp_id";
	public static final String CHILD_MP_TERM = "child_mp_term";
	public static final String CHILD_MP_TERM_SYNONYM = "child_mp_term_synonym";

	public static final String TOP_LEVEL_MP_ID = "top_level_mp_id";
	public static final String TOP_LEVEL_MP_TERM = "top_level_mp_term";
	public static final String TOP_LEVEL_MP_DEFINITION = "top_level_mp_definition";
	public static final String TOP_LEVEL_MP_TERM_SYNONYM = "top_level_mp_term_synonym";

	public static final String INTERMEDIATE_MP_ID = "intermediate_mp_id";
	public static final String INTERMEDIATE_MP_TERM = "intermediate_mp_term";
	public static final String INTERMEDIATE_MP_TERM_SYNONYM = "intermediate_mp_term_synonym";

	public static final String INFERRED_MA_ID = "inferred_ma_id";
	public static final String INFERRED_MA_TERM = "inferred_ma_term";
	public static final String INFERRED_MA_TERM_SYNONYM = "inferred_ma_term_synonym";

	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_ID = "inferred_selected_top_level_ma_id";
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM = "inferred_selected_top_level_ma_term";
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = "inferred_selected_top_level_ma_term_synonym";
	public static final String INFERRED_CHILD_MA_ID = "inferred_child_ma_id";
	public static final String INFERRED_CHILD_MA_TERM = "inferred_child_ma_term";
	public static final String INFERRED_CHILD_MA_TERM_SYNONYM = "inferred_child_ma_term_synonym";

	public static final String TYPE = "type";
	public static final String DISEASE_ID = "disease_id";
	public static final String DISEASE_SOURCE = "disease_source";
	public static final String DISEASE_TERM = "disease_term";
	public static final String DISEASE_ALTS = "disease_alts";
	public static final String DISEASE_CLASSES = "disease_classes";
	public static final String HUMAN_CURATED = "human_curated";
	public static final String MOUSE_CURATED = "mouse_curated";
	public static final String MGI_PREDICTED = "mgi_predicted";
	public static final String IMPC_PREDICTED = "impc_predicted";
	public static final String MGI_PREDICTED_IN_LOCUS = "mgi_predicted_in_locus";
	public static final String IMPC_PREDICTED_IN_LOCUS = "impc_predicted_in_locus";
	public static final String PIPELINE_NAME = "pipeline_name";
	public static final String PIPELINE_STABLE_ID = "pipeline_stable_id";
	public static final String PROCEDURE_NAME = "procedure_name";
	public static final String PROCEDURE_STABLE_ID = "procedure_stable_id";
	public static final String PARAMETER_NAME = "parameter_name";
	public static final String PARAMETER_STABLE_ID = "parameter_stable_id";
	public static final String PROC_PARAM_NAME = "proc_param_name";
	public static final String PROC_PARAM_STABLE_ID = "proc_param_stable_id";
	public static final String EXPNAME = "expName";
	public static final String SUBTYPE = "subtype";
	public static final String ANNOTATED_HIGHER_LEVEL_MP_TERM_NAME = "annotatedHigherLevelMpTermName";
	public static final String TEXT = "text";
	public static final String AUTO_SUGGEST = "auto_suggest";
	public static final String SELECTED_TOP_LEVEL_MA_TERM = "selected_top_level_ma_term";
	public static final String MGI_PREDICTED_KNOWN_GENE="mgi_predicted_known_gene";
	public static final String IMPC_NOVEL_PREDICTED_IN_LOCUS="impc_novel_predicted_in_locus";

	// go term stuff
	public static final String GO_TERM_ID = "go_term_id";
	public static final String GO_TERM_NAME = "go_term_name";
	public static final String GO_TERM_DEF = "go_term_def";
	public static final String GO_TERM_EVID = "go_term_evid";
	public static final String GO_TERM_DOMAIN = "go_term_domain";
	public static final String GO_COUNT = "go_count";
	public static final String GO_UNIPROT = "go_uniprot";
	public static final String EVID_CODE_RANK = "evidCodeRank";


	public static final String PFAMA_JSON = "pfama_json";
	public static final String SCDB_ID = "scdb_id"; // structural classification db id
	public static final String SCDB_LINK = "scdb_link";
	public static final String CLAN_ID = "clan_id";
	public static final String CLAN_ACC = "clan_acc";
	public static final String CLAN_DESC = "clan_desc";
	public static final String PFAMA_ID = "pfama_id";
	public static final String PFAMA_ACC = "pfama_acc";
	public static final String PFAMA_GO_ID = "pfama_go_id";
	public static final String PFAMA_GO_TERM = "pfama_go_term";
	public static final String PFAMA_GO_CAT = "pfama_go_cat";
	public static final String EMBRYO_DATA_AVAILABLE = "embryo_data_available";
	public static final String EMBRYO_ANALYSIS_URL="embryo_analysis_view_url";
	public static final String EMBRYO_ANALYSIS_NAME="embryo_analysis_view_name";

	public static final String DMDD_IMAGE_DATA_AVAILABLE="dmdd_image_data_available";
	public static final String DMDD_LETHAL_DATA_AVAILABLE="dmdd_lethal_data_available";
	public static final String SEQ_REGION_ID = "seq_region_id";
	public static final String SEQ_REGION_START = "seq_region_start";
	public static final String SEQ_REGION_END = "seq_region_end";
	public static final String XREF = "xref";
	public static final String XREF_ACC = "xref_acc";
	private static final String VEGA_IDS = "vega_id";
	private static final String NCBI_IDS = "ncbi_id";
	private static final String CCDS_IDS = "ccds_id";
	public static final String EMBRYO_MODALITIES = "embryo_modalities";

	public static final String CHR_NAME = "chr_name";
	public static final String CHR_start = "chr_start";
	public static final String CHR_end = "chr_end";
	public static final String IS_IDG_GENE ="is_idg_gene";
	public static final String IS_UMASS_GENE ="is_umass_gene";

	public static final String DATASETS_RAW_DATA ="datasets_raw_data";

	
	@Field(EMBRYO_ANALYSIS_URL)
	private String embryoAnalysisUrl;

	@Field(EMBRYO_ANALYSIS_NAME)
	private String embryoAnalysisName;
	
	public String getEmbryoAnalysisUrl() {
		return embryoAnalysisUrl;
	}

	public void setEmbryoAnalysisUrl(String embryoAnalysisUrl) {
		this.embryoAnalysisUrl = embryoAnalysisUrl;
	}

	public String getEmbryoAnalysisName() {
		return embryoAnalysisName;
	}

	public void setEmbryoAnalysisName(String embryoAnalysisName) {
		this.embryoAnalysisName = embryoAnalysisName;
	}

	@Field(IS_IDG_GENE)
	private Boolean isIdgGene;
	public Boolean getIsIdgGene() {
		return isIdgGene;
	}
	public void setIsIdgGene(Boolean isIdgGene) {
		this.isIdgGene = isIdgGene;
	}

	@Field(IS_UMASS_GENE)
	private Boolean isUmassGene;
	public Boolean getIsUmassGene() {
		return isUmassGene;
	}

	public void setIsUmassGene(Boolean umassGene) {
		isUmassGene = umassGene;
	}

	@Field(DATASETS_RAW_DATA)
	private String datasetsRawData;
	public String getDatasetsRawData() { return datasetsRawData; }
	public void setDatasetsRawData(String datasetsRawData) { this.datasetsRawData = datasetsRawData; }

	@Field(VEGA_IDS)
	private List<String> vegaIds;

	@Field(NCBI_IDS)
	private List<String> ncbiIds;

	@Field(CCDS_IDS)
	private List<String> ccdsIds;

	public List<String> getVegaIds() {
		return vegaIds;
	}

	public void setVegaIds(List<String> vegaIds) {
		this.vegaIds = vegaIds;
	}

	public List<String> getNcbiIds() {
		return ncbiIds;
	}

	public void setNcbiIds(List<String> ncbiIds) {
		this.ncbiIds = ncbiIds;
	}

	public List<String> getCcdsIds() {
		return ccdsIds;
	}

	public void setCcdsIds(List<String> ccdsIds) {
		this.ccdsIds = ccdsIds;
	}



	@Field(SEQ_REGION_ID)
	private String seqRegionId;

	public String getSeqRegionId() {
		return seqRegionId;
	}

	public void setSeqRegionId(String seqRegionId) {
		this.seqRegionId = seqRegionId;
	}

	public int getSeqRegionStart() {
		return seqRegionStart;
	}

	public void setSeqRegionStart(int seqRegionStart) {
		this.seqRegionStart = seqRegionStart;
	}

	public int getSeqRegionEnd() {
		return seqRegionEnd;
	}

	public void setSeqRegionEnd(int seqRegionEnd) {
		this.seqRegionEnd = seqRegionEnd;
	}

	public List<String> getXrefs() {
		return xrefs;
	}

	public void setXrefs(List<String> xrefs) {
		this.xrefs = xrefs;
	}

	@Field(SEQ_REGION_START)
	private int seqRegionStart;

	@Field(SEQ_REGION_END)
	private int seqRegionEnd;

	@Field(CHR_NAME)
	private String chrName;

	@Field(CHR_START)
	private Integer chrStart;

	@Field(CHR_END)
	private Integer chrEnd;

	@Field(XREF)
	List<String> xrefs;

	// <!-- gene level fields -->
	@Field(IMPC_NOVEL_PREDICTED_IN_LOCUS)
	private List<Boolean>impcNovelPredictedInLocus;


	@Field(SangerImageDTO.SELECTED_TOP_LEVEL_MA_TERM_ID)
	private List<String> selectedTopLevelMaId;

	@Field(SangerImageDTO.SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	private List<String>selectedTopLevelMaTermSynonym;

	public String getChrName() {
		return chrName;
	}

	public void setChrName(String chrName) {
		this.chrName = chrName;
	}

	public Integer getChrStart() {
		return chrStart;
	}

	public void setChrStart(Integer chrStart) {
		this.chrStart = chrStart;
	}

	public Integer getChrEnd() {
		return chrEnd;
	}

	public void setChrEnd(Integer chrEnd) {
		this.chrEnd = chrEnd;
	}

	public List<String> getSelectedTopLevelMaTermSynonym() {

		return selectedTopLevelMaTermSynonym;
	}

	public void setSelectedTopLevelMaTermSynonym(List<String> selectedTopLevelMaTermSynonym) {

		this.selectedTopLevelMaTermSynonym = selectedTopLevelMaTermSynonym;
	}

	public List<String> getSelectedTopLevelMaId() {

		return selectedTopLevelMaId;
	}

	public void setSelectedTopLevelMaId(List<String> selectedTopLevelMaId) {

		this.selectedTopLevelMaId = selectedTopLevelMaId;
	}

	public List<Boolean> getImpcNovelPredictedInLocus() {

		return impcNovelPredictedInLocus;
	}

	public void setImpcNovelPredictedInLocus(List<Boolean> impcNovelPredictedInLocus) {

		this.impcNovelPredictedInLocus = impcNovelPredictedInLocus;
	}

	@Field(MGI_PREDICTED_KNOWN_GENE)
	private List<Boolean>mgiPredictedKnownGene;

	public List<Boolean> getMgiPredictedKnownGene() {

		return mgiPredictedKnownGene;
	}

	public void setMgiPredictedKnonwGene(List<Boolean> mgiPredictedKnonwGene) {

		this.mgiPredictedKnownGene = mgiPredictedKnonwGene;
	}


	@Field(DATA_TYPE)
	String dataType;

	@Field(MGI_ACCESSION_ID)
	String mgiAccessionId;

	@Field(MARKER_SYMBOL)
	String markerSymbol;
	@Field(MARKER_SYMBOL_LOWERCASE)
	String markerSymbolLowercase;

	@Field(HUMAN_GENE_SYMBOL)
	List<String> humanGeneSymbol;
	@Field(HUMAN_GENE_SYMBOL_LOWERCASE)
	List<String> humanGeneSymbolLowercase;
	@Field(HUMAN_SYMBOL_SYNONYM)
	List<String> humanSymbolSynonym;
	@Field(HUMAN_SYMBOL_SYNONYM_LOWERCASE)
	List<String> humanSymbolSynonymLowercase;

	@Field(MARKER_NAME)
	String markerName;

	@Field(MARKER_SYNONYM)
	List<String> markerSynonym;
	@Field(MARKER_SYNONYM_LOWERCASE)
	List<String> markerSynonymLowercase;

	@Field(MARKER_TYPE)
	String markerType;

	@Field(ENSEMBL_GENE_ID)
	List<String> ensemblGeneIds;

	@Field(IMITS_PHENOTYPE_STARTED)
	String imitsPhenotypeStarted;

	@Field(IMITS_PHENOTYPE_COMPLETE)
	String imitsPhenotypeComplete;

	@Field(IMITS_PHENOTYPE_STATUS)
	String imitsPhenotypeStatus;

	@Field(STATUS)
	String status;

	@Field(LATEST_ES_CELL_STATUS)
	String latestEsCellStatus;

	@Field(LATEST_MOUSE_STATUS)
	String latestMouseStatus;

	@Field(LATEST_PHENOTYPE_STATUS)
	String latestPhenotypeStatus;

	@Field(LATEST_PROJECT_STATUS)
	String latestProjectStatus;

	@Field(LATEST_PRODUCTION_CENTRE)
	List<String> latestProductionCentre;

	@Field(LATEST_PHENOTYPING_CENTRE)
	List<String> latestPhenotypingCentre;

	@Field(DISEASE_HUMAN_PHENOTYPES)
	List<String> diseaseHumanPhenotypes;

	// <!-- gene has QC: ie, a record in experiment core -->

	@Field(HAS_QC)
	Integer hasQc;

	@Field(LEGACY_PHENOTYPE_STATUS)
	Integer legacy_phenotype_status;

	// <!-- allele level fields of a gene -->

	@Field(ALLELE_NAME)
	List<String> alleleName;

	@Field(ALLELE_ACCESSION_ID)
	private List<String> alleleAccessionIds = new ArrayList<>();

	@Field(IMITS_ES_CELL_STATUS)
	String imitsEsCellStatus;

	@Field(ES_CELL_STATUS)
	List<String> esCellStatus;

	@Field(IMITS_MOUSE_STATUS)
	String imitsMouseStatus;

	@Field(MOUSE_STATUS)
	List<String> mouseStatus;

	@Field(PHENOTYPE_STATUS)
	List<String> phenotypeStatus;

	@Field(PRODUCTION_CENTRE)
	List<String> productionCentre;

	@Field(PHENOTYPING_CENTRE)
	List<String> phenotypingCentre;

	// <!-- annotated and inferred mp term -->

	@Field(P_VALUE)
	List<Float> p_value;

	@Field(MP_ID)
	List<String> mpId;

	@Field(MP_TERM)
	List<String> mpTerm;

	@Field(MP_TERM_SYNONYM)
	List<String> mpTermSynonym;

	@Field(MP_TERM_DEFINITION)
	List<String> mpTermDefinition;

	@Field(MA_ID)
	List<String> maId;

	@Field(MA_TERM)
	List<String> maTerm;

	@Field(MA_TERM_SYNONYM)
	List<String> maTermSynonym;

	@Field(MA_TERM_DEFINITION)
	List<String> maTermDefinition;

	@Field(HP_ID)
	List<String> hpId;

	@Field(HP_TERM)
	List<String> hpTerm;


	@Field(CHILD_MP_ID)
	List<String> childMpId;

	@Field(CHILD_MP_TERM)
	List<String> childMpTerm;

	@Field(CHILD_MP_TERM_SYNONYM)
	List<String> childMpTermSynonym;

	@Field(TOP_LEVEL_MP_ID)
	List<String> topLevelMpId;

	@Field(TOP_LEVEL_MP_TERM)
	List<String> topLevelMpTerm;

	@Field(TOP_LEVEL_MP_TERM_SYNONYM)
	List<String> topLevelMpTermSynonym;

	@Field(TOP_LEVEL_MP_DEFINITION)
	List<String> topLevelMpDefinition;

	@Field(INTERMEDIATE_MP_ID)
	List<String> intermediateMpId;

	@Field(INTERMEDIATE_MP_TERM)
	List<String> intermediateMpTerm;

	@Field(INTERMEDIATE_MP_TERM_SYNONYM)
	List<String> intermediateMpTermSynonym;

	// <!-- annotated and inferred ma term -->

	@Field(INFERRED_MA_ID)
	List<String> inferredMaId;

	@Field(INFERRED_MA_TERM)
	List<String> inferredMaTerm;

	@Field(INFERRED_MA_TERM_SYNONYM)
	List<String> inferredMaTermSynonym;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_ID)
	List<String> inferredSelectedTopLevelMaId;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_TERM)
	List<String> inferredSelectedTopLevelMaTerm;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	List<String> inferredSelectedTopLevelMaTermSynonym;

	@Field(INFERRED_CHILD_MA_ID)
	List<String> inferredChildMaId;

	@Field(INFERRED_CHILD_MA_TERM)
	List<String> inferredChildMaTerm;

	@Field(INFERRED_CHILD_MA_TERM_SYNONYM)
	List<String> inferredChildMaTermSynonym;

	// <!--disease fields -->

	@Field(TYPE)
	List<String> type;

	@Field(DISEASE_ID)
	List<String> diseaseId;

	@Field(DISEASE_SOURCE)
	List<String> diseaseSource;

	@Field(DISEASE_TERM)
	List<String> diseaseTerm;

	@Field(DISEASE_ALTS)
	List<String> diseaseAlts;

	@Field(DISEASE_CLASSES)
	List<String> diseaseClasses;

	@Field(HUMAN_CURATED)
	List<Boolean> humanCurated;

	@Field(MOUSE_CURATED)
	List<Boolean> mouseCurated;

	@Field(MGI_PREDICTED)
	List<Boolean> mgiPredicted;

	@Field(IMPC_PREDICTED)
	List<Boolean> impcPredicted;

	@Field(MGI_PREDICTED_IN_LOCUS)
	List<Boolean> mgiPredictedInLocus;

	@Field(IMPC_PREDICTED_IN_LOCUS)
	List<Boolean> impcPredictedInLocus;

	// <!-- pipeline stuff -->

	@Field(PIPELINE_NAME)
	List<String> pipelineName;

	@Field(PIPELINE_STABLE_ID)
	List<String> pipelineStableId;

	@Field(PROCEDURE_NAME)
	List<String> procedureName;

	@Field(PROCEDURE_STABLE_ID)
	List<String> procedureStableId;

	@Field(PARAMETER_NAME)
	List<String> parameterName;

	@Field(PARAMETER_STABLE_ID)
	List<String> parameterStableId;

	@Field(PROC_PARAM_NAME)
	List<String> procParamName;

	@Field(PROC_PARAM_STABLE_ID)
	List<String> procParamStableId;

	// <!-- images annotated to a gene/mp/ma/procedure -->

	@Field(EXPNAME)
	List<String> expName;

	@Field(SUBTYPE)
	List<String> subtype;

	@Field(ANNOTATED_HIGHER_LEVEL_MP_TERM_NAME)
	List<String> annotatedHigherLevelMpTermName;

	// <!-- for copyfield -->
	@Field(TEXT)
	List<String> text;

	@Field(AUTO_SUGGEST)
	List<String> autoSuggest;

	@Field(SELECTED_TOP_LEVEL_MA_TERM)
	List<String> selectedTopLevelMaTerm;

	@Field(GO_TERM_ID)
	private List<String> goTermIds = new ArrayList<>();

	@Field(GO_TERM_NAME)
	private List<String> goTermNames = new ArrayList<>();

	@Field(GO_TERM_DEF)
	private List<String> goTermDefs = new ArrayList<>();

	@Field(GO_TERM_EVID)
	private List<String> goTermEvids = new ArrayList<>();

	@Field(GO_TERM_DOMAIN)
	private List<String> goTermDomains = new ArrayList<>();

	@Field(GO_COUNT)
	private Integer goCount;

	@Field(GO_UNIPROT)
	private List<String> go_uniprot = new ArrayList<>();

	@Field(EVID_CODE_RANK)
	private Integer evidCodeRank;

	@Field(PFAMA_JSON)
	private List<String> pfama_jsons = new ArrayList<>();

	@Field(SCDB_ID)
	private List<String> scdb_ids = new ArrayList<>();

	@Field(SCDB_LINK)
	private List<String> scdb_links = new ArrayList<>();

	@Field(CLAN_ID)
	private List<String> clan_ids = new ArrayList<>();

	@Field(CLAN_ACC)
	private List<String> clan_accs = new ArrayList<>();

	@Field(CLAN_DESC)
	private List<String> clan_descs = new ArrayList<>();

	@Field(PFAMA_ID)
	private List<String> pfama_ids = new ArrayList<>();

	@Field(PFAMA_ACC)
	private List<String> pfama_accs = new ArrayList<>();

	@Field(PFAMA_GO_ID)
	private List<String> pfama_go_ids = new ArrayList<>();

	@Field(PFAMA_GO_TERM)
	private List<String> pfama_go_terms = new ArrayList<>();

	@Field(PFAMA_GO_CAT)
	private List<String> pfama_go_cats = new ArrayList<>();

	@Field(EMBRYO_DATA_AVAILABLE)
	private boolean isEmbryoDataAvailable;
	
	@Field(DMDD_IMAGE_DATA_AVAILABLE)
	private boolean isDmddImageDataAvailable;
	
	@Field(DMDD_LETHAL_DATA_AVAILABLE)
	private boolean isDmddLethalDataAvailable;
	
	public boolean isDmddLethalDataAvailable() {
		return isDmddLethalDataAvailable;
	}

	public void setDmddLethalDataAvailable(boolean isDmddLethalDataAvailable) {
		this.isDmddLethalDataAvailable = isDmddLethalDataAvailable;
	}

	public boolean isDmddImageDataAvailable() {
		return isDmddImageDataAvailable;
	}

	public void setDmddImageDataAvailable(boolean isDmddImageDataAvailable) {
		this.isDmddImageDataAvailable = isDmddImageDataAvailable;
	}

	@Field(EMBRYO_MODALITIES)
	private List<String> embryoModalities;


	public List<String> getEmbryoModalities() {
		return embryoModalities;
	}

	public void setEmbryoModalities(List<String> embryoModalities) {
		this.embryoModalities = embryoModalities;
	}

	public boolean isEmbryoDataAvailable() {
		return this.isEmbryoDataAvailable;
	}

	public void setEmbryoDataAvailable(boolean isEmbryoDataAvailable) {
		this.isEmbryoDataAvailable = isEmbryoDataAvailable;
	}

	public List<String> getTopLevelMpTermSynonym() {

		return topLevelMpTermSynonym;
	}


	public void setTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {

		this.topLevelMpTermSynonym = topLevelMpTermSynonym;
	}


	public List<String> getChildMpTermSynonym() {

		return childMpTermSynonym;
	}


	public void setChildMpTermSynonym(List<String> childMpTermSynonym) {

		this.childMpTermSynonym = childMpTermSynonym;
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


	public String getDataType() {

		return dataType;
	}


	public void setDataType(String dataType) {

		this.dataType = dataType;
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

	public String getMarkerSymbolLowercase() {

		return markerSymbolLowercase;
	}


	public void setMarkerSymbolLowercase(String markerSymbolLowercase) {

		this.markerSymbolLowercase = markerSymbolLowercase;
	}


	public List<String> getHumanGeneSymbol() {

		return humanGeneSymbol;
	}


	public void setHumanGeneSymbol(List<String> humanGeneSymbol) {

		this.humanGeneSymbol = humanGeneSymbol;
	}


	public List<String> getHumanGeneSymbolLowercase() {
		return humanGeneSymbolLowercase;
	}

	public void setHumanGeneSymbolLowercase(List<String> humanGeneSymbolLowercase) {
		this.humanGeneSymbolLowercase = humanGeneSymbolLowercase;
	}

	public List<String> getHumanSymbolSynonym() {
		return humanSymbolSynonym;
	}

	public void setHumanSymbolSynonym(List<String> humanSymbolSynonym) {
		this.humanSymbolSynonym = humanSymbolSynonym;
	}

	public List<String> getHumanSymbolSynonymLowercase() {
		return humanSymbolSynonymLowercase;
	}

	public void setHumanSymbolSynonymLowercase(List<String> humanSymbolSynonymLowercase) {
		this.humanSymbolSynonymLowercase = humanSymbolSynonymLowercase;
	}

	public List<String> getMarkerSynonymLowercase() {
		return markerSynonymLowercase;
	}

	public void setMarkerSynonymLowercase(List<String> markerSynonymLowercase) {
		this.markerSynonymLowercase = markerSynonymLowercase;
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


	public String getMarkerType() {

		return markerType;
	}


	public void setMarkerType(String markerType) {

		this.markerType = markerType;
	}


	public List<String> getEnsemblGeneIds() {
		return ensemblGeneIds;
	}


	public void setEnsemblGeneIds(List<String> ensemblGeneIds) {
		this.ensemblGeneIds = ensemblGeneIds;
	}


	public String getImitsPhenotypeStarted() {

		return imitsPhenotypeStarted;
	}


	public void setImitsPhenotypeStarted(String imitsPhenotypeStarted) {

		this.imitsPhenotypeStarted = imitsPhenotypeStarted;
	}


	public String getImitsPhenotypeComplete() {

		return imitsPhenotypeComplete;
	}


	public void setImitsPhenotypeComplete(String imitsPhenotypeComplete) {

		this.imitsPhenotypeComplete = imitsPhenotypeComplete;
	}


	public String getImitsPhenotypeStatus() {

		return imitsPhenotypeStatus;
	}


	public void setImitsPhenotypeStatus(String imitsPhenotypeStatus) {

		this.imitsPhenotypeStatus = imitsPhenotypeStatus;
	}


	public String getStatus() {

		return status;
	}


	public void setStatus(String status) {

		this.status = status;
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


	public String getLatestPhenotypeStatus() {

		return latestPhenotypeStatus;
	}


	public void setLatestPhenotypeStatus(String latestPhenotypeStatus) {

		this.latestPhenotypeStatus = latestPhenotypeStatus;
	}


	public String getLatestProjectStatus() {

		return latestProjectStatus;
	}


	public void setLatestProjectStatus(String latestProjectStatus) {

		this.latestProjectStatus = latestProjectStatus;
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


	public List<String> getDiseaseHumanPhenotypes() {

		return diseaseHumanPhenotypes;
	}


	public void setDiseaseHumanPhenotypes(List<String> diseaseHumanPhenotypes) {

		this.diseaseHumanPhenotypes = diseaseHumanPhenotypes;
	}


	public Integer getHasQc() {

		return hasQc;
	}


	public void setHasQc(Integer hasQc) {

		this.hasQc = hasQc;
	}


	public List<String> getAlleleName() {

		return alleleName;
	}


	public void setAlleleName(List<String> alleleName) {

		this.alleleName = alleleName;
	}


	/**
	 * @return the alleleAccessionIds
	 */
	public List<String> getAlleleAccessionIds() {
		return alleleAccessionIds;
	}

	/**
	 * @param alleleAccessionIds
	 *            the alleleAccessionIds to set
	 */
	public void setAlleleAccessionIds(List<String> alleleAccessionIds) {
		this.alleleAccessionIds = alleleAccessionIds;
	}


	public String getImitsEsCellStatus() {

		return imitsEsCellStatus;
	}


	public void setImitsEsCellStatus(String imitsEsCellStatus) {

		this.imitsEsCellStatus = imitsEsCellStatus;
	}


	public List<String> getEsCellStatus() {

		return esCellStatus;
	}


	public void setEsCellStatus(List<String> esCellStatus) {

		this.esCellStatus = esCellStatus;
	}


	public String getImitsMouseStatus() {

		return imitsMouseStatus;
	}


	public void setImitsMouseStatus(String imitsMouseStatus) {

		this.imitsMouseStatus = imitsMouseStatus;
	}


	public List<String> getMouseStatus() {

		return mouseStatus;
	}


	public void setMouseStatus(List<String> mouseStatus) {

		this.mouseStatus = mouseStatus;
	}


	public List<String> getPhenotypeStatus() {

		return phenotypeStatus;
	}


	public void setPhenotypeStatus(List<String> phenotypeStatus) {

		this.phenotypeStatus = phenotypeStatus;
	}


	public List<String> getProductionCentre() {

		return productionCentre;
	}


	public void setProductionCentre(List<String> productionCentre) {

		this.productionCentre = productionCentre;
	}


	public List<String> getPhenotypingCentre() {

		return phenotypingCentre;
	}


	public void setPhenotypingCentre(List<String> phenotypingCentre) {

		this.phenotypingCentre = phenotypingCentre;
	}


	public List<Float> getP_value() {

		return p_value;
	}


	public void setP_value(List<Float> p_value) {

		this.p_value = p_value;
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


	public List<String> getMpTermDefinition() {

		return mpTermDefinition;
	}


	public void setMpTermDefinition(List<String> mpTermDefinition) {

		this.mpTermDefinition = mpTermDefinition;
	}


	public List<String> getInferredSelectedTopLevelMaTermSynonym() {

		return inferredSelectedTopLevelMaTermSynonym;
	}


	public void setInferredSelectedTopLevelMaTermSynonym(List<String> inferredSelectedTopLevelMaTermSynonym) {

		this.inferredSelectedTopLevelMaTermSynonym = inferredSelectedTopLevelMaTermSynonym;
	}


	public List<String> getMaId() {

		return maId;
	}


	public void setMaId(List<String> maId) {

		this.maId = maId;
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


	public List<String> getMaTermDefinition() {

		return maTermDefinition;
	}


	public void setMaTermDefinition(List<String> maTermDefinition) {

		this.maTermDefinition = maTermDefinition;
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


	public void setTopLevelMpTerm(List<String> topLevelMpTerm) {

		this.topLevelMpTerm = topLevelMpTerm;
	}


	public List<String> getTopLevelMpDefinition() {

		return topLevelMpDefinition;
	}


	public void setTopLevelMpDefinition(List<String> topLevelMpDefinition) {

		this.topLevelMpDefinition = topLevelMpDefinition;
	}


	public List<String> getInferredMaId() {

		return inferredMaId;
	}


	public void setInferredMaId(List<String> inferredMaId) {

		this.inferredMaId = inferredMaId;
	}


	public List<String> getInferredMaTerm() {

		return inferredMaTerm;
	}


	public void setInferredMaTerm(List<String> inferredMaTerm) {

		this.inferredMaTerm = inferredMaTerm;
	}


	public List<String> getInferredMaTermSynonym() {

		return inferredMaTermSynonym;
	}


	public void setInferredMaTermSynonym(List<String> inferredMaTermSynonym) {

		this.inferredMaTermSynonym = inferredMaTermSynonym;
	}


	public List<String> getInferredSelectedTopLevelMaId() {

		return inferredSelectedTopLevelMaId;
	}


	public void setInferredSelectedTopLevelMaId(List<String> inferredSelectedTopLevelMaId) {

		this.inferredSelectedTopLevelMaId = inferredSelectedTopLevelMaId;
	}


	public List<String> getInferredSelectedTopLevelMaTerm() {

		return inferredSelectedTopLevelMaTerm;
	}


	public void setInferredSelectedTopLevelMaTerm(List<String> inferredSelectedTopLevelMaTerm) {

		this.inferredSelectedTopLevelMaTerm = inferredSelectedTopLevelMaTerm;
	}


	public List<String> getInferredChildMaId() {

		return inferredChildMaId;
	}


	public void setInferredChildMaId(List<String> inferredChildMaId) {

		this.inferredChildMaId = inferredChildMaId;
	}


	public List<String> getInferredChildMaTerm() {

		return inferredChildMaTerm;
	}


	public void setInferredChildMaTerm(List<String> inferredChildMaTerm) {

		this.inferredChildMaTerm = inferredChildMaTerm;
	}


	public List<String> getInferredChildMaTermSynonym() {

		return inferredChildMaTermSynonym;
	}


	public void setInferredChildMaTermSynonym(List<String> inferredChildMaTermSynonym) {

		this.inferredChildMaTermSynonym = inferredChildMaTermSynonym;
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


	public List<Boolean> getMgiPredictedInLocus() {

		return mgiPredictedInLocus;
	}


	public void setMgiPredictedInLocus(List<Boolean> mgiPredictedInLocus) {

		this.mgiPredictedInLocus = mgiPredictedInLocus;
	}


	public List<Boolean> getImpcPredictedInLocus() {

		return impcPredictedInLocus;
	}


	public void setImpcPredictedInLocus(List<Boolean> impcPredictedInLocus) {

		this.impcPredictedInLocus = impcPredictedInLocus;
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


	public List<String> getProcParamName() {

		return procParamName;
	}


	public void setProcParamName(List<String> procParamName) {

		this.procParamName = procParamName;
	}


	public List<String> getProcParamStableId() {

		return procParamStableId;
	}


	public void setProcParamStableId(List<String> procParamStableId) {

		this.procParamStableId = procParamStableId;
	}


	public List<String> getExpName() {

		return expName;
	}


	public void setExpName(List<String> expName) {

		this.expName = expName;
	}


	public List<String> getSubtype() {

		return subtype;
	}


	public void setSubtype(List<String> subtype) {

		this.subtype = subtype;
	}


	public List<String> getAnnotatedHigherLevelMpTermName() {

		return annotatedHigherLevelMpTermName;
	}


	public void setAnnotatedHigherLevelMpTermName(List<String> annotatedHigherLevelMpTermName) {

		this.annotatedHigherLevelMpTermName = annotatedHigherLevelMpTermName;
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


	public List<String> getSelectedTopLevelMaTerm() {

		return selectedTopLevelMaTerm;
	}


	public void setSelectedTopLevelMaTerm(List<String> selectedTopLevelMaTerm) {

		this.selectedTopLevelMaTerm = selectedTopLevelMaTerm;
	}


	public Integer getLegacy_phenotype_status() {

		return legacy_phenotype_status;
	}


	public void setLegacy_phenotype_status(Integer legacy_phenotype_status) {

		this.legacy_phenotype_status = legacy_phenotype_status;
	}


	public List<String> getChildMpTerm() {

		return childMpTerm;
	}


	public void setChildMpTerm(List<String> childMpTerm) {

		this.childMpTerm = childMpTerm;
	}


	public List<String> getChildMpId() {

		return childMpId;
	}


	public void setChildMpId(List<String> childMpId) {

		this.childMpId = childMpId;
	}


	/**
	 * @return the goTermIds
	 */
	public List<String> getGoTermIds() {
		return goTermIds;
	}

	/**
	 * @param goTermIds
	 *            the goTermIds to set
	 */
	public void setGoTermIds(List<String> goTermIds) {
		this.goTermIds = goTermIds;
	}

	/**
	 * @return the goTermNames
	 */
	public List<String> getGoTermNames() {
		return goTermNames;
	}

	/**
	 * @param goTermNames
	 *            the goTermNames to set
	 */
	public void setGoTermNames(List<String> goTermNames) {
		this.goTermNames = goTermNames;
	}

	/**
	 * @return the goTermDefs
	 */
	public List<String> getGoTermDefs() {
		return goTermDefs;
	}

	/**
	 * @param goTermDefs
	 *            the goTermDefs to set
	 */
	public void setGoTermDefs(List<String> goTermDefs) {
		this.goTermDefs = goTermDefs;
	}


	/**
	 * @return the goTermEvids
	 */
	public List<String> getGoTermEvids() {
		return goTermEvids;
	}

	/**
	 * @param goTermEvids
	 *            the goTermEvids to set
	 */
	public void setGoTermEvids(List<String> goTermEvids) {
		this.goTermEvids = goTermEvids;
	}

	/**
	 * @return the goTermDomains
	 */
	public List<String> getGoTermDomains() {
		return goTermDomains;
	}

	/**
	 * @param goTermDomains
	 *            the goTermDomains to set
	 */
	public void setGoTermDomains(List<String> goTermDomains) {
		this.goTermDomains = goTermDomains;
	}

	/**
	 * @return the goCount
	 */
	public Integer getGoCount() {
		return goCount;
	}

	/**
	 * @param goCount
	 *            the goCount to set
	 */
	public void setGoCount(Integer goCount) {
		this.goCount = goCount;
	}

	/**
	 * @return the go_uniprot
	 */
	public List<String> getGoUniprot() {
		return go_uniprot;
	}

	/**
	 * @param go_uniprot
	 *            the go_uniprot to set
	 */
	public void setGoUniprot(List<String> go_uniprot) {
		this.go_uniprot = go_uniprot;
	}

	/**
	 * @return the evidCodeRank
	 */
	public Integer getEvidCodeRank() {
		return evidCodeRank;
	}

	/**
	 * @param evidCodeRank
	 *            the evidCodeRank to set
	 */
	public void setEvidCodeRank(Integer evidCodeRank) {
		this.evidCodeRank = evidCodeRank;
	}


	/**
	 * @return the scdb_ids
	 */
	public List<String> getScdbIds() {
		return scdb_ids;
	}

	/**
	 * @param scdb_ids
	 *            the scdb_ids to set
	 */
	public void setScdbIds(List<String> scdb_ids) {
		this.scdb_ids = scdb_ids;
	}

	/**
	 * @return the scdb_links
	 */
	public List<String> getScdbLinks() {
		return scdb_links;
	}

	/**
	 * @param scdb_links
	 *            the scdb_links to set
	 */
	public void setScdbLinks(List<String> scdb_links) {
		this.scdb_links = scdb_links;
	}

	/**
	 * @return the clan_ids
	 */
	public List<String> getClanIds() {
		return clan_ids;
	}

	/**
	 * @param clan_ids
	 *            the clan_ids to set
	 */
	public void setClanIds(List<String> clan_ids) {
		this.clan_ids = clan_ids;
	}

	/**
	 * @return the clan_accs
	 */
	public List<String> getClanAccs() {
		return clan_accs;
	}

	/**
	 * @param clan_accs
	 *            the clan_accs to set
	 */
	public void setClanAccs(List<String> clan_accs) {
		this.clan_accs = clan_accs;
	}

	/**
	 * @return the clan_descs
	 */
	public List<String> getClanDescs() {
		return clan_descs;
	}

	/**
	 * @param clan_descs
	 *            the clan_descs to set
	 */
	public void setClanDescs(List<String> clan_descs) {
		this.clan_descs = clan_descs;
	}

	/**
	 * @return the pfama_ids
	 */
	public List<String> getPfamaIds() {
		return pfama_ids;
	}

	/**
	 * @param pfama_ids
	 *            the pfama_ids to set
	 */
	public void setPfamaIds(List<String> pfama_ids) {
		this.pfama_ids = pfama_ids;
	}

	/**
	 * @return the pfama_accs
	 */
	public List<String> getPfamaAccs() {
		return pfama_accs;
	}

	/**
	 * @param pfama_accs
	 *            the pfama_accs to set
	 */
	public void setPfamaAccs(List<String> pfama_accs) {
		this.pfama_accs = pfama_accs;
	}

	/**
	 * @return the pfama_go_ids
	 */
	public List<String> getPfamaGoIds() {
		return pfama_go_ids;
	}

	/**
	 * @param pfama_go_ids
	 *            the pfama_go_ids to set
	 */
	public void setPfamaGoIds(List<String> pfama_go_ids) {
		this.pfama_go_ids = pfama_go_ids;
	}

	/**
	 * @return the pfama_go_terms
	 */
	public List<String> getPfamaGoTerms() {
		return pfama_go_terms;
	}

	/**
	 * @param pfama_go_terms
	 *            the pfama_go_terms to set
	 */
	public void setPfamaGoTerms(List<String> pfama_go_terms) {
		this.pfama_go_terms = pfama_go_terms;
	}

	/**
	 * @return the pfama_go_cats
	 */
	public List<String> getPfamaGoCats() {
		return pfama_go_cats;
	}

	/**
	 * @param pfama_go_cats
	 *            the pfama_go_cats to set
	 */
	public void setPfamaGoCats(List<String> pfama_go_cats) {
		this.pfama_go_cats = pfama_go_cats;
	}

	/**
	 * @return the pfama_jsons to get
	 */
	public List<String> getPfamaJsons() {
		return pfama_jsons;
	}

	/**
	 * @param pfama_jsons
	 *            the pfama_jsons to set
	 */
	public void setPfamaJsons(List<String> pfama_jsons) {
		this.pfama_jsons = pfama_jsons;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GeneDTO geneDTO = (GeneDTO) o;

		if (seqRegionStart != geneDTO.seqRegionStart) return false;
		if (seqRegionEnd != geneDTO.seqRegionEnd) return false;
		if (isEmbryoDataAvailable != geneDTO.isEmbryoDataAvailable) return false;
		if (isDmddImageDataAvailable != geneDTO.isDmddImageDataAvailable) return false;
		if (isDmddLethalDataAvailable != geneDTO.isDmddLethalDataAvailable) return false;
		if (embryoAnalysisUrl != null ? !embryoAnalysisUrl.equals(geneDTO.embryoAnalysisUrl) : geneDTO.embryoAnalysisUrl != null)
			return false;
		if (embryoAnalysisName != null ? !embryoAnalysisName.equals(geneDTO.embryoAnalysisName) : geneDTO.embryoAnalysisName != null)
			return false;
		if (isIdgGene != null ? !isIdgGene.equals(geneDTO.isIdgGene) : geneDTO.isIdgGene != null) return false;
		if (vegaIds != null ? !vegaIds.equals(geneDTO.vegaIds) : geneDTO.vegaIds != null) return false;
		if (ncbiIds != null ? !ncbiIds.equals(geneDTO.ncbiIds) : geneDTO.ncbiIds != null) return false;
		if (ccdsIds != null ? !ccdsIds.equals(geneDTO.ccdsIds) : geneDTO.ccdsIds != null) return false;
		if (seqRegionId != null ? !seqRegionId.equals(geneDTO.seqRegionId) : geneDTO.seqRegionId != null) return false;
		if (chrName != null ? !chrName.equals(geneDTO.chrName) : geneDTO.chrName != null) return false;
		if (chrStart != null ? !chrStart.equals(geneDTO.chrStart) : geneDTO.chrStart != null) return false;
		if (chrEnd != null ? !chrEnd.equals(geneDTO.chrEnd) : geneDTO.chrEnd != null) return false;
		if (xrefs != null ? !xrefs.equals(geneDTO.xrefs) : geneDTO.xrefs != null) return false;
		if (impcNovelPredictedInLocus != null ? !impcNovelPredictedInLocus.equals(geneDTO.impcNovelPredictedInLocus) : geneDTO.impcNovelPredictedInLocus != null)
			return false;
		if (selectedTopLevelMaId != null ? !selectedTopLevelMaId.equals(geneDTO.selectedTopLevelMaId) : geneDTO.selectedTopLevelMaId != null)
			return false;
		if (selectedTopLevelMaTermSynonym != null ? !selectedTopLevelMaTermSynonym.equals(geneDTO.selectedTopLevelMaTermSynonym) : geneDTO.selectedTopLevelMaTermSynonym != null)
			return false;
		if (mgiPredictedKnownGene != null ? !mgiPredictedKnownGene.equals(geneDTO.mgiPredictedKnownGene) : geneDTO.mgiPredictedKnownGene != null)
			return false;
		if (dataType != null ? !dataType.equals(geneDTO.dataType) : geneDTO.dataType != null) return false;
		if (mgiAccessionId != null ? !mgiAccessionId.equals(geneDTO.mgiAccessionId) : geneDTO.mgiAccessionId != null)
			return false;
		if (markerSymbol != null ? !markerSymbol.equals(geneDTO.markerSymbol) : geneDTO.markerSymbol != null)
			return false;
		if (markerSymbolLowercase != null ? !markerSymbolLowercase.equals(geneDTO.markerSymbolLowercase) : geneDTO.markerSymbolLowercase != null)
			return false;
		if (humanGeneSymbol != null ? !humanGeneSymbol.equals(geneDTO.humanGeneSymbol) : geneDTO.humanGeneSymbol != null)
			return false;
		if (humanGeneSymbolLowercase != null ? !humanGeneSymbolLowercase.equals(geneDTO.humanGeneSymbolLowercase) : geneDTO.humanGeneSymbolLowercase != null)
			return false;
		if (humanSymbolSynonym != null ? !humanSymbolSynonym.equals(geneDTO.humanSymbolSynonym) : geneDTO.humanSymbolSynonym != null)
			return false;
		if (humanSymbolSynonymLowercase != null ? !humanSymbolSynonymLowercase.equals(geneDTO.humanSymbolSynonymLowercase) : geneDTO.humanSymbolSynonymLowercase != null)
			return false;
		if (markerName != null ? !markerName.equals(geneDTO.markerName) : geneDTO.markerName != null) return false;
		if (markerSynonym != null ? !markerSynonym.equals(geneDTO.markerSynonym) : geneDTO.markerSynonym != null)
			return false;
		if (markerSynonymLowercase != null ? !markerSynonymLowercase.equals(geneDTO.markerSynonymLowercase) : geneDTO.markerSynonymLowercase != null)
			return false;
		if (markerType != null ? !markerType.equals(geneDTO.markerType) : geneDTO.markerType != null) return false;
		if (ensemblGeneIds != null ? !ensemblGeneIds.equals(geneDTO.ensemblGeneIds) : geneDTO.ensemblGeneIds != null)
			return false;
		if (imitsPhenotypeStarted != null ? !imitsPhenotypeStarted.equals(geneDTO.imitsPhenotypeStarted) : geneDTO.imitsPhenotypeStarted != null)
			return false;
		if (imitsPhenotypeComplete != null ? !imitsPhenotypeComplete.equals(geneDTO.imitsPhenotypeComplete) : geneDTO.imitsPhenotypeComplete != null)
			return false;
		if (imitsPhenotypeStatus != null ? !imitsPhenotypeStatus.equals(geneDTO.imitsPhenotypeStatus) : geneDTO.imitsPhenotypeStatus != null)
			return false;
		if (status != null ? !status.equals(geneDTO.status) : geneDTO.status != null) return false;
		if (latestEsCellStatus != null ? !latestEsCellStatus.equals(geneDTO.latestEsCellStatus) : geneDTO.latestEsCellStatus != null)
			return false;
		if (latestMouseStatus != null ? !latestMouseStatus.equals(geneDTO.latestMouseStatus) : geneDTO.latestMouseStatus != null)
			return false;
		if (latestPhenotypeStatus != null ? !latestPhenotypeStatus.equals(geneDTO.latestPhenotypeStatus) : geneDTO.latestPhenotypeStatus != null)
			return false;
		if (latestProjectStatus != null ? !latestProjectStatus.equals(geneDTO.latestProjectStatus) : geneDTO.latestProjectStatus != null)
			return false;
		if (latestProductionCentre != null ? !latestProductionCentre.equals(geneDTO.latestProductionCentre) : geneDTO.latestProductionCentre != null)
			return false;
		if (latestPhenotypingCentre != null ? !latestPhenotypingCentre.equals(geneDTO.latestPhenotypingCentre) : geneDTO.latestPhenotypingCentre != null)
			return false;
		if (diseaseHumanPhenotypes != null ? !diseaseHumanPhenotypes.equals(geneDTO.diseaseHumanPhenotypes) : geneDTO.diseaseHumanPhenotypes != null)
			return false;
		if (hasQc != null ? !hasQc.equals(geneDTO.hasQc) : geneDTO.hasQc != null) return false;
		if (legacy_phenotype_status != null ? !legacy_phenotype_status.equals(geneDTO.legacy_phenotype_status) : geneDTO.legacy_phenotype_status != null)
			return false;
		if (alleleName != null ? !alleleName.equals(geneDTO.alleleName) : geneDTO.alleleName != null) return false;
		if (alleleAccessionIds != null ? !alleleAccessionIds.equals(geneDTO.alleleAccessionIds) : geneDTO.alleleAccessionIds != null)
			return false;
		if (imitsEsCellStatus != null ? !imitsEsCellStatus.equals(geneDTO.imitsEsCellStatus) : geneDTO.imitsEsCellStatus != null)
			return false;
		if (esCellStatus != null ? !esCellStatus.equals(geneDTO.esCellStatus) : geneDTO.esCellStatus != null)
			return false;
		if (imitsMouseStatus != null ? !imitsMouseStatus.equals(geneDTO.imitsMouseStatus) : geneDTO.imitsMouseStatus != null)
			return false;
		if (mouseStatus != null ? !mouseStatus.equals(geneDTO.mouseStatus) : geneDTO.mouseStatus != null) return false;
		if (phenotypeStatus != null ? !phenotypeStatus.equals(geneDTO.phenotypeStatus) : geneDTO.phenotypeStatus != null)
			return false;
		if (productionCentre != null ? !productionCentre.equals(geneDTO.productionCentre) : geneDTO.productionCentre != null)
			return false;
		if (phenotypingCentre != null ? !phenotypingCentre.equals(geneDTO.phenotypingCentre) : geneDTO.phenotypingCentre != null)
			return false;
		if (p_value != null ? !p_value.equals(geneDTO.p_value) : geneDTO.p_value != null) return false;
		if (mpId != null ? !mpId.equals(geneDTO.mpId) : geneDTO.mpId != null) return false;
		if (mpTerm != null ? !mpTerm.equals(geneDTO.mpTerm) : geneDTO.mpTerm != null) return false;
		if (mpTermSynonym != null ? !mpTermSynonym.equals(geneDTO.mpTermSynonym) : geneDTO.mpTermSynonym != null)
			return false;
		if (mpTermDefinition != null ? !mpTermDefinition.equals(geneDTO.mpTermDefinition) : geneDTO.mpTermDefinition != null)
			return false;
		if (maId != null ? !maId.equals(geneDTO.maId) : geneDTO.maId != null) return false;
		if (maTerm != null ? !maTerm.equals(geneDTO.maTerm) : geneDTO.maTerm != null) return false;
		if (maTermSynonym != null ? !maTermSynonym.equals(geneDTO.maTermSynonym) : geneDTO.maTermSynonym != null)
			return false;
		if (maTermDefinition != null ? !maTermDefinition.equals(geneDTO.maTermDefinition) : geneDTO.maTermDefinition != null)
			return false;
		if (hpId != null ? !hpId.equals(geneDTO.hpId) : geneDTO.hpId != null) return false;
		if (hpTerm != null ? !hpTerm.equals(geneDTO.hpTerm) : geneDTO.hpTerm != null) return false;
		if (childMpId != null ? !childMpId.equals(geneDTO.childMpId) : geneDTO.childMpId != null) return false;
		if (childMpTerm != null ? !childMpTerm.equals(geneDTO.childMpTerm) : geneDTO.childMpTerm != null) return false;
		if (childMpTermSynonym != null ? !childMpTermSynonym.equals(geneDTO.childMpTermSynonym) : geneDTO.childMpTermSynonym != null)
			return false;
		if (topLevelMpId != null ? !topLevelMpId.equals(geneDTO.topLevelMpId) : geneDTO.topLevelMpId != null)
			return false;
		if (topLevelMpTerm != null ? !topLevelMpTerm.equals(geneDTO.topLevelMpTerm) : geneDTO.topLevelMpTerm != null)
			return false;
		if (topLevelMpTermSynonym != null ? !topLevelMpTermSynonym.equals(geneDTO.topLevelMpTermSynonym) : geneDTO.topLevelMpTermSynonym != null)
			return false;
		if (topLevelMpDefinition != null ? !topLevelMpDefinition.equals(geneDTO.topLevelMpDefinition) : geneDTO.topLevelMpDefinition != null)
			return false;
		if (intermediateMpId != null ? !intermediateMpId.equals(geneDTO.intermediateMpId) : geneDTO.intermediateMpId != null)
			return false;
		if (intermediateMpTerm != null ? !intermediateMpTerm.equals(geneDTO.intermediateMpTerm) : geneDTO.intermediateMpTerm != null)
			return false;
		if (intermediateMpTermSynonym != null ? !intermediateMpTermSynonym.equals(geneDTO.intermediateMpTermSynonym) : geneDTO.intermediateMpTermSynonym != null)
			return false;
		if (inferredMaId != null ? !inferredMaId.equals(geneDTO.inferredMaId) : geneDTO.inferredMaId != null)
			return false;
		if (inferredMaTerm != null ? !inferredMaTerm.equals(geneDTO.inferredMaTerm) : geneDTO.inferredMaTerm != null)
			return false;
		if (inferredMaTermSynonym != null ? !inferredMaTermSynonym.equals(geneDTO.inferredMaTermSynonym) : geneDTO.inferredMaTermSynonym != null)
			return false;
		if (inferredSelectedTopLevelMaId != null ? !inferredSelectedTopLevelMaId.equals(geneDTO.inferredSelectedTopLevelMaId) : geneDTO.inferredSelectedTopLevelMaId != null)
			return false;
		if (inferredSelectedTopLevelMaTerm != null ? !inferredSelectedTopLevelMaTerm.equals(geneDTO.inferredSelectedTopLevelMaTerm) : geneDTO.inferredSelectedTopLevelMaTerm != null)
			return false;
		if (inferredSelectedTopLevelMaTermSynonym != null ? !inferredSelectedTopLevelMaTermSynonym.equals(geneDTO.inferredSelectedTopLevelMaTermSynonym) : geneDTO.inferredSelectedTopLevelMaTermSynonym != null)
			return false;
		if (inferredChildMaId != null ? !inferredChildMaId.equals(geneDTO.inferredChildMaId) : geneDTO.inferredChildMaId != null)
			return false;
		if (inferredChildMaTerm != null ? !inferredChildMaTerm.equals(geneDTO.inferredChildMaTerm) : geneDTO.inferredChildMaTerm != null)
			return false;
		if (inferredChildMaTermSynonym != null ? !inferredChildMaTermSynonym.equals(geneDTO.inferredChildMaTermSynonym) : geneDTO.inferredChildMaTermSynonym != null)
			return false;
		if (type != null ? !type.equals(geneDTO.type) : geneDTO.type != null) return false;
		if (diseaseId != null ? !diseaseId.equals(geneDTO.diseaseId) : geneDTO.diseaseId != null) return false;
		if (diseaseSource != null ? !diseaseSource.equals(geneDTO.diseaseSource) : geneDTO.diseaseSource != null)
			return false;
		if (diseaseTerm != null ? !diseaseTerm.equals(geneDTO.diseaseTerm) : geneDTO.diseaseTerm != null) return false;
		if (diseaseAlts != null ? !diseaseAlts.equals(geneDTO.diseaseAlts) : geneDTO.diseaseAlts != null) return false;
		if (diseaseClasses != null ? !diseaseClasses.equals(geneDTO.diseaseClasses) : geneDTO.diseaseClasses != null)
			return false;
		if (humanCurated != null ? !humanCurated.equals(geneDTO.humanCurated) : geneDTO.humanCurated != null)
			return false;
		if (mouseCurated != null ? !mouseCurated.equals(geneDTO.mouseCurated) : geneDTO.mouseCurated != null)
			return false;
		if (mgiPredicted != null ? !mgiPredicted.equals(geneDTO.mgiPredicted) : geneDTO.mgiPredicted != null)
			return false;
		if (impcPredicted != null ? !impcPredicted.equals(geneDTO.impcPredicted) : geneDTO.impcPredicted != null)
			return false;
		if (mgiPredictedInLocus != null ? !mgiPredictedInLocus.equals(geneDTO.mgiPredictedInLocus) : geneDTO.mgiPredictedInLocus != null)
			return false;
		if (impcPredictedInLocus != null ? !impcPredictedInLocus.equals(geneDTO.impcPredictedInLocus) : geneDTO.impcPredictedInLocus != null)
			return false;
		if (pipelineName != null ? !pipelineName.equals(geneDTO.pipelineName) : geneDTO.pipelineName != null)
			return false;
		if (pipelineStableId != null ? !pipelineStableId.equals(geneDTO.pipelineStableId) : geneDTO.pipelineStableId != null)
			return false;
		if (procedureName != null ? !procedureName.equals(geneDTO.procedureName) : geneDTO.procedureName != null)
			return false;
		if (procedureStableId != null ? !procedureStableId.equals(geneDTO.procedureStableId) : geneDTO.procedureStableId != null)
			return false;
		if (parameterName != null ? !parameterName.equals(geneDTO.parameterName) : geneDTO.parameterName != null)
			return false;
		if (parameterStableId != null ? !parameterStableId.equals(geneDTO.parameterStableId) : geneDTO.parameterStableId != null)
			return false;
		if (procParamName != null ? !procParamName.equals(geneDTO.procParamName) : geneDTO.procParamName != null)
			return false;
		if (procParamStableId != null ? !procParamStableId.equals(geneDTO.procParamStableId) : geneDTO.procParamStableId != null)
			return false;
		if (expName != null ? !expName.equals(geneDTO.expName) : geneDTO.expName != null) return false;
		if (subtype != null ? !subtype.equals(geneDTO.subtype) : geneDTO.subtype != null) return false;
		if (annotatedHigherLevelMpTermName != null ? !annotatedHigherLevelMpTermName.equals(geneDTO.annotatedHigherLevelMpTermName) : geneDTO.annotatedHigherLevelMpTermName != null)
			return false;
		if (text != null ? !text.equals(geneDTO.text) : geneDTO.text != null) return false;
		if (autoSuggest != null ? !autoSuggest.equals(geneDTO.autoSuggest) : geneDTO.autoSuggest != null) return false;
		if (selectedTopLevelMaTerm != null ? !selectedTopLevelMaTerm.equals(geneDTO.selectedTopLevelMaTerm) : geneDTO.selectedTopLevelMaTerm != null)
			return false;
		if (goTermIds != null ? !goTermIds.equals(geneDTO.goTermIds) : geneDTO.goTermIds != null) return false;
		if (goTermNames != null ? !goTermNames.equals(geneDTO.goTermNames) : geneDTO.goTermNames != null) return false;
		if (goTermDefs != null ? !goTermDefs.equals(geneDTO.goTermDefs) : geneDTO.goTermDefs != null) return false;
		if (goTermEvids != null ? !goTermEvids.equals(geneDTO.goTermEvids) : geneDTO.goTermEvids != null) return false;
		if (goTermDomains != null ? !goTermDomains.equals(geneDTO.goTermDomains) : geneDTO.goTermDomains != null)
			return false;
		if (goCount != null ? !goCount.equals(geneDTO.goCount) : geneDTO.goCount != null) return false;
		if (go_uniprot != null ? !go_uniprot.equals(geneDTO.go_uniprot) : geneDTO.go_uniprot != null) return false;
		if (evidCodeRank != null ? !evidCodeRank.equals(geneDTO.evidCodeRank) : geneDTO.evidCodeRank != null)
			return false;
		if (pfama_jsons != null ? !pfama_jsons.equals(geneDTO.pfama_jsons) : geneDTO.pfama_jsons != null) return false;
		if (scdb_ids != null ? !scdb_ids.equals(geneDTO.scdb_ids) : geneDTO.scdb_ids != null) return false;
		if (scdb_links != null ? !scdb_links.equals(geneDTO.scdb_links) : geneDTO.scdb_links != null) return false;
		if (clan_ids != null ? !clan_ids.equals(geneDTO.clan_ids) : geneDTO.clan_ids != null) return false;
		if (clan_accs != null ? !clan_accs.equals(geneDTO.clan_accs) : geneDTO.clan_accs != null) return false;
		if (clan_descs != null ? !clan_descs.equals(geneDTO.clan_descs) : geneDTO.clan_descs != null) return false;
		if (pfama_ids != null ? !pfama_ids.equals(geneDTO.pfama_ids) : geneDTO.pfama_ids != null) return false;
		if (pfama_accs != null ? !pfama_accs.equals(geneDTO.pfama_accs) : geneDTO.pfama_accs != null) return false;
		if (pfama_go_ids != null ? !pfama_go_ids.equals(geneDTO.pfama_go_ids) : geneDTO.pfama_go_ids != null)
			return false;
		if (pfama_go_terms != null ? !pfama_go_terms.equals(geneDTO.pfama_go_terms) : geneDTO.pfama_go_terms != null)
			return false;
		if (pfama_go_cats != null ? !pfama_go_cats.equals(geneDTO.pfama_go_cats) : geneDTO.pfama_go_cats != null)
			return false;
		return embryoModalities != null ? embryoModalities.equals(geneDTO.embryoModalities) : geneDTO.embryoModalities == null;
	}

	@Override
	public int hashCode() {
		int result = embryoAnalysisUrl != null ? embryoAnalysisUrl.hashCode() : 0;
		result = 31 * result + (embryoAnalysisName != null ? embryoAnalysisName.hashCode() : 0);
		result = 31 * result + (isIdgGene != null ? isIdgGene.hashCode() : 0);
		result = 31 * result + (vegaIds != null ? vegaIds.hashCode() : 0);
		result = 31 * result + (ncbiIds != null ? ncbiIds.hashCode() : 0);
		result = 31 * result + (ccdsIds != null ? ccdsIds.hashCode() : 0);
		result = 31 * result + (seqRegionId != null ? seqRegionId.hashCode() : 0);
		result = 31 * result + seqRegionStart;
		result = 31 * result + seqRegionEnd;
		result = 31 * result + (chrName != null ? chrName.hashCode() : 0);
		result = 31 * result + (chrStart != null ? chrStart.hashCode() : 0);
		result = 31 * result + (chrEnd != null ? chrEnd.hashCode() : 0);
		result = 31 * result + (xrefs != null ? xrefs.hashCode() : 0);
		result = 31 * result + (impcNovelPredictedInLocus != null ? impcNovelPredictedInLocus.hashCode() : 0);
		result = 31 * result + (selectedTopLevelMaId != null ? selectedTopLevelMaId.hashCode() : 0);
		result = 31 * result + (selectedTopLevelMaTermSynonym != null ? selectedTopLevelMaTermSynonym.hashCode() : 0);
		result = 31 * result + (mgiPredictedKnownGene != null ? mgiPredictedKnownGene.hashCode() : 0);
		result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
		result = 31 * result + (mgiAccessionId != null ? mgiAccessionId.hashCode() : 0);
		result = 31 * result + (markerSymbol != null ? markerSymbol.hashCode() : 0);
		result = 31 * result + (markerSymbolLowercase != null ? markerSymbolLowercase.hashCode() : 0);
		result = 31 * result + (humanGeneSymbol != null ? humanGeneSymbol.hashCode() : 0);
		result = 31 * result + (humanGeneSymbolLowercase != null ? humanGeneSymbolLowercase.hashCode() : 0);
		result = 31 * result + (humanSymbolSynonym != null ? humanSymbolSynonym.hashCode() : 0);
		result = 31 * result + (humanSymbolSynonymLowercase != null ? humanSymbolSynonymLowercase.hashCode() : 0);
		result = 31 * result + (markerName != null ? markerName.hashCode() : 0);
		result = 31 * result + (markerSynonym != null ? markerSynonym.hashCode() : 0);
		result = 31 * result + (markerSynonymLowercase != null ? markerSynonymLowercase.hashCode() : 0);
		result = 31 * result + (markerType != null ? markerType.hashCode() : 0);
		result = 31 * result + (ensemblGeneIds != null ? ensemblGeneIds.hashCode() : 0);
		result = 31 * result + (imitsPhenotypeStarted != null ? imitsPhenotypeStarted.hashCode() : 0);
		result = 31 * result + (imitsPhenotypeComplete != null ? imitsPhenotypeComplete.hashCode() : 0);
		result = 31 * result + (imitsPhenotypeStatus != null ? imitsPhenotypeStatus.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (latestEsCellStatus != null ? latestEsCellStatus.hashCode() : 0);
		result = 31 * result + (latestMouseStatus != null ? latestMouseStatus.hashCode() : 0);
		result = 31 * result + (latestPhenotypeStatus != null ? latestPhenotypeStatus.hashCode() : 0);
		result = 31 * result + (latestProjectStatus != null ? latestProjectStatus.hashCode() : 0);
		result = 31 * result + (latestProductionCentre != null ? latestProductionCentre.hashCode() : 0);
		result = 31 * result + (latestPhenotypingCentre != null ? latestPhenotypingCentre.hashCode() : 0);
		result = 31 * result + (diseaseHumanPhenotypes != null ? diseaseHumanPhenotypes.hashCode() : 0);
		result = 31 * result + (hasQc != null ? hasQc.hashCode() : 0);
		result = 31 * result + (legacy_phenotype_status != null ? legacy_phenotype_status.hashCode() : 0);
		result = 31 * result + (alleleName != null ? alleleName.hashCode() : 0);
		result = 31 * result + (alleleAccessionIds != null ? alleleAccessionIds.hashCode() : 0);
		result = 31 * result + (imitsEsCellStatus != null ? imitsEsCellStatus.hashCode() : 0);
		result = 31 * result + (esCellStatus != null ? esCellStatus.hashCode() : 0);
		result = 31 * result + (imitsMouseStatus != null ? imitsMouseStatus.hashCode() : 0);
		result = 31 * result + (mouseStatus != null ? mouseStatus.hashCode() : 0);
		result = 31 * result + (phenotypeStatus != null ? phenotypeStatus.hashCode() : 0);
		result = 31 * result + (productionCentre != null ? productionCentre.hashCode() : 0);
		result = 31 * result + (phenotypingCentre != null ? phenotypingCentre.hashCode() : 0);
		result = 31 * result + (p_value != null ? p_value.hashCode() : 0);
		result = 31 * result + (mpId != null ? mpId.hashCode() : 0);
		result = 31 * result + (mpTerm != null ? mpTerm.hashCode() : 0);
		result = 31 * result + (mpTermSynonym != null ? mpTermSynonym.hashCode() : 0);
		result = 31 * result + (mpTermDefinition != null ? mpTermDefinition.hashCode() : 0);
		result = 31 * result + (maId != null ? maId.hashCode() : 0);
		result = 31 * result + (maTerm != null ? maTerm.hashCode() : 0);
		result = 31 * result + (maTermSynonym != null ? maTermSynonym.hashCode() : 0);
		result = 31 * result + (maTermDefinition != null ? maTermDefinition.hashCode() : 0);
		result = 31 * result + (hpId != null ? hpId.hashCode() : 0);
		result = 31 * result + (hpTerm != null ? hpTerm.hashCode() : 0);
		result = 31 * result + (childMpId != null ? childMpId.hashCode() : 0);
		result = 31 * result + (childMpTerm != null ? childMpTerm.hashCode() : 0);
		result = 31 * result + (childMpTermSynonym != null ? childMpTermSynonym.hashCode() : 0);
		result = 31 * result + (topLevelMpId != null ? topLevelMpId.hashCode() : 0);
		result = 31 * result + (topLevelMpTerm != null ? topLevelMpTerm.hashCode() : 0);
		result = 31 * result + (topLevelMpTermSynonym != null ? topLevelMpTermSynonym.hashCode() : 0);
		result = 31 * result + (topLevelMpDefinition != null ? topLevelMpDefinition.hashCode() : 0);
		result = 31 * result + (intermediateMpId != null ? intermediateMpId.hashCode() : 0);
		result = 31 * result + (intermediateMpTerm != null ? intermediateMpTerm.hashCode() : 0);
		result = 31 * result + (intermediateMpTermSynonym != null ? intermediateMpTermSynonym.hashCode() : 0);
		result = 31 * result + (inferredMaId != null ? inferredMaId.hashCode() : 0);
		result = 31 * result + (inferredMaTerm != null ? inferredMaTerm.hashCode() : 0);
		result = 31 * result + (inferredMaTermSynonym != null ? inferredMaTermSynonym.hashCode() : 0);
		result = 31 * result + (inferredSelectedTopLevelMaId != null ? inferredSelectedTopLevelMaId.hashCode() : 0);
		result = 31 * result + (inferredSelectedTopLevelMaTerm != null ? inferredSelectedTopLevelMaTerm.hashCode() : 0);
		result = 31 * result + (inferredSelectedTopLevelMaTermSynonym != null ? inferredSelectedTopLevelMaTermSynonym.hashCode() : 0);
		result = 31 * result + (inferredChildMaId != null ? inferredChildMaId.hashCode() : 0);
		result = 31 * result + (inferredChildMaTerm != null ? inferredChildMaTerm.hashCode() : 0);
		result = 31 * result + (inferredChildMaTermSynonym != null ? inferredChildMaTermSynonym.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (diseaseId != null ? diseaseId.hashCode() : 0);
		result = 31 * result + (diseaseSource != null ? diseaseSource.hashCode() : 0);
		result = 31 * result + (diseaseTerm != null ? diseaseTerm.hashCode() : 0);
		result = 31 * result + (diseaseAlts != null ? diseaseAlts.hashCode() : 0);
		result = 31 * result + (diseaseClasses != null ? diseaseClasses.hashCode() : 0);
		result = 31 * result + (humanCurated != null ? humanCurated.hashCode() : 0);
		result = 31 * result + (mouseCurated != null ? mouseCurated.hashCode() : 0);
		result = 31 * result + (mgiPredicted != null ? mgiPredicted.hashCode() : 0);
		result = 31 * result + (impcPredicted != null ? impcPredicted.hashCode() : 0);
		result = 31 * result + (mgiPredictedInLocus != null ? mgiPredictedInLocus.hashCode() : 0);
		result = 31 * result + (impcPredictedInLocus != null ? impcPredictedInLocus.hashCode() : 0);
		result = 31 * result + (pipelineName != null ? pipelineName.hashCode() : 0);
		result = 31 * result + (pipelineStableId != null ? pipelineStableId.hashCode() : 0);
		result = 31 * result + (procedureName != null ? procedureName.hashCode() : 0);
		result = 31 * result + (procedureStableId != null ? procedureStableId.hashCode() : 0);
		result = 31 * result + (parameterName != null ? parameterName.hashCode() : 0);
		result = 31 * result + (parameterStableId != null ? parameterStableId.hashCode() : 0);
		result = 31 * result + (procParamName != null ? procParamName.hashCode() : 0);
		result = 31 * result + (procParamStableId != null ? procParamStableId.hashCode() : 0);
		result = 31 * result + (expName != null ? expName.hashCode() : 0);
		result = 31 * result + (subtype != null ? subtype.hashCode() : 0);
		result = 31 * result + (annotatedHigherLevelMpTermName != null ? annotatedHigherLevelMpTermName.hashCode() : 0);
		result = 31 * result + (text != null ? text.hashCode() : 0);
		result = 31 * result + (autoSuggest != null ? autoSuggest.hashCode() : 0);
		result = 31 * result + (selectedTopLevelMaTerm != null ? selectedTopLevelMaTerm.hashCode() : 0);
		result = 31 * result + (goTermIds != null ? goTermIds.hashCode() : 0);
		result = 31 * result + (goTermNames != null ? goTermNames.hashCode() : 0);
		result = 31 * result + (goTermDefs != null ? goTermDefs.hashCode() : 0);
		result = 31 * result + (goTermEvids != null ? goTermEvids.hashCode() : 0);
		result = 31 * result + (goTermDomains != null ? goTermDomains.hashCode() : 0);
		result = 31 * result + (goCount != null ? goCount.hashCode() : 0);
		result = 31 * result + (go_uniprot != null ? go_uniprot.hashCode() : 0);
		result = 31 * result + (evidCodeRank != null ? evidCodeRank.hashCode() : 0);
		result = 31 * result + (pfama_jsons != null ? pfama_jsons.hashCode() : 0);
		result = 31 * result + (scdb_ids != null ? scdb_ids.hashCode() : 0);
		result = 31 * result + (scdb_links != null ? scdb_links.hashCode() : 0);
		result = 31 * result + (clan_ids != null ? clan_ids.hashCode() : 0);
		result = 31 * result + (clan_accs != null ? clan_accs.hashCode() : 0);
		result = 31 * result + (clan_descs != null ? clan_descs.hashCode() : 0);
		result = 31 * result + (pfama_ids != null ? pfama_ids.hashCode() : 0);
		result = 31 * result + (pfama_accs != null ? pfama_accs.hashCode() : 0);
		result = 31 * result + (pfama_go_ids != null ? pfama_go_ids.hashCode() : 0);
		result = 31 * result + (pfama_go_terms != null ? pfama_go_terms.hashCode() : 0);
		result = 31 * result + (pfama_go_cats != null ? pfama_go_cats.hashCode() : 0);
		result = 31 * result + (isEmbryoDataAvailable ? 1 : 0);
		result = 31 * result + (isDmddImageDataAvailable ? 1 : 0);
		result = 31 * result + (isDmddLethalDataAvailable ? 1 : 0);
		result = 31 * result + (embryoModalities != null ? embryoModalities.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "GeneDTO{" +
				"embryoAnalysisUrl='" + embryoAnalysisUrl + '\'' +
				", embryoAnalysisName='" + embryoAnalysisName + '\'' +
				", isIdgGene=" + isIdgGene +
				", vegaIds=" + vegaIds +
				", ncbiIds=" + ncbiIds +
				", ccdsIds=" + ccdsIds +
				", seqRegionId='" + seqRegionId + '\'' +
				", seqRegionStart=" + seqRegionStart +
				", seqRegionEnd=" + seqRegionEnd +
				", chrName='" + chrName + '\'' +
				", chrStart=" + chrStart +
				", chrEnd=" + chrEnd +
				", xrefs=" + xrefs +
				", impcNovelPredictedInLocus=" + impcNovelPredictedInLocus +
				", selectedTopLevelMaId=" + selectedTopLevelMaId +
				", selectedTopLevelMaTermSynonym=" + selectedTopLevelMaTermSynonym +
				", mgiPredictedKnownGene=" + mgiPredictedKnownGene +
				", dataType='" + dataType + '\'' +
				", mgiAccessionId='" + mgiAccessionId + '\'' +
				", markerSymbol='" + markerSymbol + '\'' +
				", markerSymbolLowercase='" + markerSymbolLowercase + '\'' +
				", humanGeneSymbol=" + humanGeneSymbol +
				", humanGeneSymbolLowercase=" + humanGeneSymbolLowercase +
				", humanSymbolSynonym=" + humanSymbolSynonym +
				", humanSymbolSynonymLowercase=" + humanSymbolSynonymLowercase +
				", markerName='" + markerName + '\'' +
				", markerSynonym=" + markerSynonym +
				", markerSynonymLowercase=" + markerSynonymLowercase +
				", markerType='" + markerType + '\'' +
				", ensemblGeneIds=" + ensemblGeneIds +
				", imitsPhenotypeStarted='" + imitsPhenotypeStarted + '\'' +
				", imitsPhenotypeComplete='" + imitsPhenotypeComplete + '\'' +
				", imitsPhenotypeStatus='" + imitsPhenotypeStatus + '\'' +
				", status='" + status + '\'' +
				", latestEsCellStatus='" + latestEsCellStatus + '\'' +
				", latestMouseStatus='" + latestMouseStatus + '\'' +
				", latestPhenotypeStatus='" + latestPhenotypeStatus + '\'' +
				", latestProjectStatus='" + latestProjectStatus + '\'' +
				", latestProductionCentre=" + latestProductionCentre +
				", latestPhenotypingCentre=" + latestPhenotypingCentre +
				", diseaseHumanPhenotypes=" + diseaseHumanPhenotypes +
				", hasQc=" + hasQc +
				", legacy_phenotype_status=" + legacy_phenotype_status +
				", alleleName=" + alleleName +
				", alleleAccessionIds=" + alleleAccessionIds +
				", imitsEsCellStatus='" + imitsEsCellStatus + '\'' +
				", esCellStatus=" + esCellStatus +
				", imitsMouseStatus='" + imitsMouseStatus + '\'' +
				", mouseStatus=" + mouseStatus +
				", phenotypeStatus=" + phenotypeStatus +
				", productionCentre=" + productionCentre +
				", phenotypingCentre=" + phenotypingCentre +
				", p_value=" + p_value +
				", mpId=" + mpId +
				", mpTerm=" + mpTerm +
				", mpTermSynonym=" + mpTermSynonym +
				", mpTermDefinition=" + mpTermDefinition +
				", maId=" + maId +
				", maTerm=" + maTerm +
				", maTermSynonym=" + maTermSynonym +
				", maTermDefinition=" + maTermDefinition +
				", hpId=" + hpId +
				", hpTerm=" + hpTerm +
				", childMpId=" + childMpId +
				", childMpTerm=" + childMpTerm +
				", childMpTermSynonym=" + childMpTermSynonym +
				", topLevelMpId=" + topLevelMpId +
				", topLevelMpTerm=" + topLevelMpTerm +
				", topLevelMpTermSynonym=" + topLevelMpTermSynonym +
				", topLevelMpDefinition=" + topLevelMpDefinition +
				", intermediateMpId=" + intermediateMpId +
				", intermediateMpTerm=" + intermediateMpTerm +
				", intermediateMpTermSynonym=" + intermediateMpTermSynonym +
				", inferredMaId=" + inferredMaId +
				", inferredMaTerm=" + inferredMaTerm +
				", inferredMaTermSynonym=" + inferredMaTermSynonym +
				", inferredSelectedTopLevelMaId=" + inferredSelectedTopLevelMaId +
				", inferredSelectedTopLevelMaTerm=" + inferredSelectedTopLevelMaTerm +
				", inferredSelectedTopLevelMaTermSynonym=" + inferredSelectedTopLevelMaTermSynonym +
				", inferredChildMaId=" + inferredChildMaId +
				", inferredChildMaTerm=" + inferredChildMaTerm +
				", inferredChildMaTermSynonym=" + inferredChildMaTermSynonym +
				", type=" + type +
				", diseaseId=" + diseaseId +
				", diseaseSource=" + diseaseSource +
				", diseaseTerm=" + diseaseTerm +
				", diseaseAlts=" + diseaseAlts +
				", diseaseClasses=" + diseaseClasses +
				", humanCurated=" + humanCurated +
				", mouseCurated=" + mouseCurated +
				", mgiPredicted=" + mgiPredicted +
				", impcPredicted=" + impcPredicted +
				", mgiPredictedInLocus=" + mgiPredictedInLocus +
				", impcPredictedInLocus=" + impcPredictedInLocus +
				", pipelineName=" + pipelineName +
				", pipelineStableId=" + pipelineStableId +
				", procedureName=" + procedureName +
				", procedureStableId=" + procedureStableId +
				", parameterName=" + parameterName +
				", parameterStableId=" + parameterStableId +
				", procParamName=" + procParamName +
				", procParamStableId=" + procParamStableId +
				", expName=" + expName +
				", subtype=" + subtype +
				", annotatedHigherLevelMpTermName=" + annotatedHigherLevelMpTermName +
				", text=" + text +
				", autoSuggest=" + autoSuggest +
				", selectedTopLevelMaTerm=" + selectedTopLevelMaTerm +
				", goTermIds=" + goTermIds +
				", goTermNames=" + goTermNames +
				", goTermDefs=" + goTermDefs +
				", goTermEvids=" + goTermEvids +
				", goTermDomains=" + goTermDomains +
				", goCount=" + goCount +
				", go_uniprot=" + go_uniprot +
				", evidCodeRank=" + evidCodeRank +
				", pfama_jsons=" + pfama_jsons +
				", scdb_ids=" + scdb_ids +
				", scdb_links=" + scdb_links +
				", clan_ids=" + clan_ids +
				", clan_accs=" + clan_accs +
				", clan_descs=" + clan_descs +
				", pfama_ids=" + pfama_ids +
				", pfama_accs=" + pfama_accs +
				", pfama_go_ids=" + pfama_go_ids +
				", pfama_go_terms=" + pfama_go_terms +
				", pfama_go_cats=" + pfama_go_cats +
				", isEmbryoDataAvailable=" + isEmbryoDataAvailable +
				", isDmddImageDataAvailable=" + isDmddImageDataAvailable +
				", isDmddLethalDataAvailable=" + isDmddLethalDataAvailable +
				", embryoModalities=" + embryoModalities +
				'}';
	}



}
