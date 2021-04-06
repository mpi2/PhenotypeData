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

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;

import java.util.ArrayList;
import java.util.List;

import static org.mousephenotype.cda.solr.service.dto.AlleleDTO.CHR_END;
import static org.mousephenotype.cda.solr.service.dto.AlleleDTO.CHR_START;

@Data
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
	public static final String DISEASE_HUMAN_PHENOTYPES = "disease_human_phenotypes";
	public static final String HAS_QC = "hasQc";
	public static final String ALLELE_NAME = "allele_name";
	public static final String ALLELE_ACCESSION_ID = "allele_accession_id";
	public static final String ES_CELL_PRODUCTION_STATUS = "es_cell_production_status";
	public static final String MOUSE_PRODUCTION_STATUS = "mouse_production_status";
	public static final String PHENOTYPE_STATUS = "phenotype_status";
	public static final String ASSIGNMENT_STATUS = "assignment_status";
	public static final String PRODUCTION_CENTRE = "production_centre";
	public static final String PHENOTYPING_CENTRE = "phenotyping_centre";
	public static final String PROJECT_STATUS = "project_status";
	public static final String PHENOTYPING_DATA_AVAILABLE = "phenotyping_data_available";
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
	public static final String SIGNIFICANT_TOP_LEVEL_MP_TERMS = "significant_top_level_mp_terms";
	public static final String NOT_SIGNIFICANT_TOP_LEVEL_MP_TERMS = "not_significant_top_level_mp_terms";
	private static final String VEGA_IDS = "vega_id";
	private static final String NCBI_IDS = "ncbi_id";
	private static final String CCDS_IDS = "ccds_id";
	public static final String EMBRYO_MODALITIES = "embryo_modalities";

	public static final String CHR_NAME = "chr_name";
	public static final String CHR_start = "chr_start";
	public static final String CHR_end = "chr_end";
	public static final String IS_UMASS_GENE ="is_umass_gene";

	public static final String DATASETS_RAW_DATA ="datasets_raw_data";

	public static final String CONDITIONAL_ALLELE_PRODUCTION_STATUS = "conditional_allele_production_status";
	public static final String CRISPR_ALLELE_PRODUCTION_STATUS = "crispr_allele_production_status";
	public static final String NULL_ALLELE_PRODUCTION_STATUS = "null_allele_production_status";

	
	@Field(EMBRYO_ANALYSIS_URL)
	private String embryoAnalysisUrl;

	@Field(EMBRYO_ANALYSIS_NAME)
	private String embryoAnalysisName;

	@Field(IS_UMASS_GENE)
	private Boolean isUmassGene;

	@Field(DATASETS_RAW_DATA)
	private String datasetsRawData;

	@Field(VEGA_IDS)
	private List<String> vegaIds;

	@Field(NCBI_IDS)
	private List<String> ncbiIds;

	@Field(CCDS_IDS)
	private List<String> ccdsIds;


	@Field(SEQ_REGION_ID)
	private String seqRegionId;

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

	@Field(DISEASE_HUMAN_PHENOTYPES)
	List<String> diseaseHumanPhenotypes;

	// <!-- gene has QC: ie, a record in experiment core -->

	@Field(HAS_QC)
	Integer hasQc;

	// <!-- allele level fields of a gene -->

	@Field(ALLELE_NAME)
	List<String> alleleName;

	@Field(ALLELE_ACCESSION_ID)
	private List<String> alleleAccessionIds = new ArrayList<>();

	@Field(ES_CELL_PRODUCTION_STATUS)
	String esCellProductionStatus;

	@Field(MOUSE_PRODUCTION_STATUS)
	String mouseProductionStatus;

	@Field(PHENOTYPE_STATUS)
	String phenotypeStatus;

	@Field(PRODUCTION_CENTRE)
	List<String> productionCentre;

	@Field(PHENOTYPING_CENTRE)
	List<String> phenotypingCentre;


	@Field(ASSIGNMENT_STATUS)
	String assignmentStatus;

	// <!-- annotated and inferred mp term -->

	@Field(P_VALUE)
	List<Float> p_value;

	@Field(SIGNIFICANT_TOP_LEVEL_MP_TERMS)
	List<String> significantTopLevelMpTerms;

	@Field(NOT_SIGNIFICANT_TOP_LEVEL_MP_TERMS)
	List<String> notSignificantTopLevelMpTerms;

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

	@Field(EMBRYO_MODALITIES)
	private List<String> embryoModalities;

	@Field(CONDITIONAL_ALLELE_PRODUCTION_STATUS)
	private String conditionalAlleleProductionStatus;

	@Field(CRISPR_ALLELE_PRODUCTION_STATUS)
	private String crisprAlleleProductionStatus;

	@Field(NULL_ALLELE_PRODUCTION_STATUS)
	private String nullAlleleProductionStatus;

	@Field(PHENOTYPING_DATA_AVAILABLE)
	private boolean phenotypingDataAvailable;

}
