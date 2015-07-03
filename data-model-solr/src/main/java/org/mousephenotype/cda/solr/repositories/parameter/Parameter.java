package org.mousephenotype.cda.solr.repositories.parameter;

import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument(solrCoreName = "pipeline")
public class Parameter implements ParameterDefinition {

	public static final String STABLE_ID_FIELD_NAME="parameter_stable_id";
	public static final String ID_ID_ID = "ididid";
	public static final String PIPELINE_ID = ObservationDTO.PIPELINE_ID;
	public static final String PIPELINE_STABLE_ID = ObservationDTO.PIPELINE_STABLE_ID;
	public static final String PIPELINE_STABLE_KEY = "pipeline_stable_key";
	public static final String PIPELINE_NAME = ObservationDTO.PIPELINE_NAME;

	public static final String PROCEDURE_ID = ObservationDTO.PROCEDURE_ID;
	public static final String PROCEDURE_STABLE_ID = ObservationDTO.PROCEDURE_STABLE_ID;
	public static final String PROCEDURE_STABLE_KEY = "procedure_stable_key";
	public static final String PROCEDURE_NAME = ObservationDTO.PROCEDURE_NAME;
	public static final String PROCEDURE_NAME_ID = "proc_name_id";

	public static final String PROCEDURE_PARAMETER_STABLE_ID = "proc_param_stable_id";
	public static final String PROCEDURE_PARAMETER_NAME = "proc_param_name";

	public static final String PARAMETER_ID = ObservationDTO.PARAMETER_ID;
	public static final String PARAMETER_STABLE_ID = ObservationDTO.PARAMETER_STABLE_ID;
	public static final String PARAMETER_STABLE_KEY = "parameter_stable_key";
	public static final String PARAMETER_NAME = ObservationDTO.PARAMETER_NAME;

	public static final String MAPPED_PROCEDURE_NAME = "mapped_procedure_name";

	public static final String PIPE_PROC_SID = "pipe_proc_sid";

	public static final String MGI_ACCESSION_ID = GeneDTO.MGI_ACCESSION_ID;
	public static final String MARKER_TYPE = GeneDTO.MARKER_TYPE;
	public static final String MARKER_SYMBOL = GeneDTO.MARKER_SYMBOL;
	public static final String MARKER_SYNONYM = GeneDTO.MARKER_SYNONYM;
	public static final String MARKER_NAME = GeneDTO.MARKER_NAME;
	public static final String HUMAN_GENE_SYMBOL = GeneDTO.HUMAN_GENE_SYMBOL;
	public static final String STATUS = GeneDTO.STATUS;
	public static final String IMITS_PHENOTYPE_STARTED = GeneDTO.IMITS_PHENOTYPE_STARTED;
	public static final String IMITS_PHENOTYPE_COMPLETE = GeneDTO.IMITS_PHENOTYPE_COMPLETE;
	public static final String IMITS_PHENOTYPE_STATUS = GeneDTO.IMITS_PHENOTYPE_STATUS;
	public static final String LATEST_PRODUCTION_CENTRE = GeneDTO.LATEST_PRODUCTION_CENTRE;
	public static final String LATEST_PHENOTYPING_CENTRE = GeneDTO.LATEST_PHENOTYPING_CENTRE;
	public static final String LATEST_PHENOTYPE_STATUS = GeneDTO.LATEST_PHENOTYPE_STATUS;
	public static final String LEGACY_PHENOTYPE_STATUS = GeneDTO.LEGACY_PHENOTYPE_STATUS;
	public static final String ALLELE_NAME = GeneDTO.ALLELE_NAME;

	public static final String MP_ID = MpDTO.MP_ID;
	public static final String MP_TERM = MpDTO.MP_TERM;
	public static final String MP_TERM_SYNONYM = MpDTO.MP_TERM_SYNONYM;
	public static final String ONTOLOGY_SUBSET = MpDTO.ONTOLOGY_SUBSET;
	public static final String TOP_LEVEL_MP_ID = MpDTO.TOP_LEVEL_MP_ID;
	public static final String TOP_LEVEL_MP_TERM = MpDTO.TOP_LEVEL_MP_TERM;
	public static final String TOP_LEVEL_MP_TERM_SYNONYM = MpDTO.TOP_LEVEL_MP_TERM_SYNONYM;
	public static final String INTERMEDIATE_MP_ID = MpDTO.INTERMEDIATE_MP_ID;
	public static final String INTERMEDIATE_MP_TERM = MpDTO.INTERMEDIATE_MP_TERM;
	public static final String INTERMEDIATE_MP_TERM_SYNONYM = MpDTO.INTERMEDIATE_MP_TERM_SYNONYM;
	public static final String CHILD_MP_ID = MpDTO.CHILD_MP_ID;
	public static final String CHILD_MP_TERM = MpDTO.CHILD_MP_TERM;
	public static final String CHILD_MP_TERM_SYNONYM = MpDTO.CHILD_MP_TERM_SYNONYM;
	public static final String HP_ID = MpDTO.HP_ID;
	public static final String HP_TERM = MpDTO.HP_TERM;
	public static final String INFERRED_MA_ID = MpDTO.INFERRED_MA_ID;
	public static final String INFERRED_MA_TERM_SYNONYM = MpDTO.INFERRED_MA_TERM_SYNONYM;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_ID = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_ID;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_TERM;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM;
	public static final String INFERRED_CHILD_MA_ID = MpDTO.INFERRED_CHILD_MA_ID;
	public static final String INFERRED_CHILD_MA_TERM = MpDTO.INFERRED_CHILD_MA_TERM;
	public static final String INFERRED_CHILD_MA_TERM_SYNONYM = MpDTO.INFERRED_CHILD_MA_TERM_SYNONYM;
	public static final String ABNORMAL_MA_ID = "abnormal_ma_id";
	public static final String ABNORMAL_MA_NAME = "abnormal_ma_name";
	
	@Id
	@Indexed(PARAMETER_ID)
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Indexed(STABLE_ID_FIELD_NAME)
	String stableId;

	public String getStableId() {
		return stableId;
	}

	public void setStableId(String stableId) {
		this.stableId = stableId;
	}

	
	@Indexed
	private String ididid;

	public String getIdIdId() {
		return ididid;
	}

	public void setIdIdId(String ididid) {
		this.ididid = ididid;
	}
	
	
}
