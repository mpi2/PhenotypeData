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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

public class ImpressDTO {

	private static final String ID_ID_ID = "ididid"; // unique key 
	
	public static final String PIPELINE_ID = ObservationDTO.PIPELINE_ID;
	public static final String PIPELINE_STABLE_ID = ObservationDTO.PIPELINE_STABLE_ID;
	public static final String PIPELINE_STABLE_KEY = "pipeline_stable_key";
	public static final String PIPELINE_NAME = ObservationDTO.PIPELINE_NAME;

	public static final String PROCEDURE_ID = ObservationDTO.PROCEDURE_ID;
	public static final String PROCEDURE_STABLE_ID = ObservationDTO.PROCEDURE_STABLE_ID;
	public static final String PROCEDURE_STABLE_KEY = "procedure_stable_key";
	public static final String PROCEDURE_NAME = ObservationDTO.PROCEDURE_NAME;

	public static final String PARAMETER_ID = ObservationDTO.PARAMETER_ID;
	public static final String PARAMETER_STABLE_ID = ObservationDTO.PARAMETER_STABLE_ID;
	public static final String PARAMETER_STABLE_KEY = "parameter_stable_key";
	public static final String PARAMETER_NAME = ObservationDTO.PARAMETER_NAME;
	
	public static final String REQUIRED = "required";
	public static final String MP_TERMS = "mp_terms";
	public static final String DESCRIPTION = "description";
	public static final String UNIT = "unit";
	public static final String INCREMENT = "increment";
	public static final String METADATA = "metadata";
	public static final String HAS_OPTIONS = "has_options";
	public static final String CATEGORIES = "categories";
	public static final String DERIVED = "derived";
	public static final String MEDIA = "media";
	public static final String OBSERVATION_TYPE = ObservationDTO.OBSERVATION_TYPE;


	public static final String MP_ID = MpDTO.MP_ID;
	public static final String MP_TERM = MpDTO.MP_TERM;
	public static final String MP_TERM_SYNONYM = MpDTO.MP_TERM_SYNONYM;
	public static final String TOP_LEVEL_MP_ID = MpDTO.TOP_LEVEL_MP_ID;
	public static final String TOP_LEVEL_MP_TERM = MpDTO.TOP_LEVEL_MP_TERM;
	public static final String TOP_LEVEL_MP_TERM_SYNONYM = MpDTO.TOP_LEVEL_MP_TERM_SYNONYM;
	public static final String INTERMEDIATE_MP_ID = MpDTO.INTERMEDIATE_MP_ID;
	public static final String INTERMEDIATE_MP_TERM = MpDTO.INTERMEDIATE_MP_TERM;
	public static final String INTERMEDIATE_MP_TERM_SYNONYM = MpDTO.INTERMEDIATE_MP_TERM_SYNONYM;

	public static final String MA_ID = "ma_id";
	public static final String MA_TERM = "ma_term";
	public static final String INFERRED_MA_ID = MpDTO.INFERRED_MA_ID;
	public static final String INFERRED_MA_TERM_SYNONYM = MpDTO.INFERRED_MA_TERM_SYNONYM;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_ID = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_ID;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_TERM;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM;

	@Field(CATEGORIES)
	private List<String> catgories;

	@Field(UNIT)
	private String unit;

	@Field(INCREMENT)
	private boolean increment;

	@Field(METADATA)
	private boolean metadata;

	@Field(HAS_OPTIONS)
	private boolean hasOptions;

	@Field(DERIVED)
	private boolean derived;

	@Field(MEDIA)
	private boolean media;		
	
	@Field(REQUIRED)
	private boolean required;

	@Field(DESCRIPTION)
	private String description;

	@Field(MP_TERMS)
	private List<String> mpTerms;

	@Field(OBSERVATION_TYPE)
	private String observationType;
	
	@Field(PARAMETER_ID)
	private int parameterId;

	@Field(PARAMETER_STABLE_ID)
	private String parameterStableId;

	@Field(PARAMETER_NAME)
	private String parameterName;
	
	@Field(PARAMETER_STABLE_KEY)
	private int parameterStableKey;
	

	@Field(PROCEDURE_ID)
	private Integer procedureId;

	@Field(PROCEDURE_STABLE_ID)
	private String procedureStableId;

	@Field(PROCEDURE_NAME)
	private String procedureName;
	
	@Field(PROCEDURE_STABLE_KEY)
	private Integer procedureStableKey;
	
	@Field(PIPELINE_ID)
	private int pipelineId;

	@Field(PIPELINE_STABLE_ID)
	private String pipelineStableId;

	@Field(PIPELINE_STABLE_KEY)
	private Integer pipelineStableKey;

	@Field(PIPELINE_NAME)
	private String pipelineName;

	@Field(ID_ID_ID)
	private String ididid;

	//
	// MP fields
	//

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

	@Field(INFERRED_MA_ID)
	private List<String> inferredMaId;

	@Field(INFERRED_MA_TERM_SYNONYM)
	private List<String> inferredMaTermSynonym;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_ID)
	private List<String> selectedTopLevelMaId;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_TERM)
	private List<String> inferredSelectedTopLevelMaTerm;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_TERM_SYNONYM)
	private List<String> inferredSelectedToLevelMaTermSynonym;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_ID)
	private List<String> inferredSelectedTopLevelMaId;
	
	@Field(MA_ID)
	private String maId;
	
	@Field(MA_TERM)
	private String maTerm;


	
	public boolean isRequired() {
		return required;
	}


	public void setRequired(boolean required) {
		this.required = required;
	}


	public List<String> getCategories() {
		return catgories;
	}


	public void setCategories(List<String> catgories) {
		this.catgories = catgories;
	}


	public List<String> getMpTerms() {
		return mpTerms;
	}


	public void setMpTerms(List<String> mpTerms) {
		this.mpTerms = mpTerms;
	}


	public String getObservationType() {
		return observationType;
	}


	public void setObservationType(String observationType) {
		this.observationType = observationType;
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


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
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

	public List<String> getInferredMaId() {

		return inferredMaId;
	}


	public void setInferredMaId(List<String> inferredMaId) {

		this.inferredMaId = inferredMaId;
	}

	public List<String> getInferredMaTermSynonym() {

		return inferredMaTermSynonym;
	}


	public void setInferredMaTermSynonym(List<String> inferredMaTermSynonym) {

		this.inferredMaTermSynonym = inferredMaTermSynonym;
	}


	public List<String> getSelectedTopLevelMaId() {

		return selectedTopLevelMaId;
	}


	public void setSelectedTopLevelMaId(List<String> selectedTopLevelMaId) {

		this.selectedTopLevelMaId = selectedTopLevelMaId;
	}


	public List<String> getInferredSelectedTopLevelMaTerm() {

		return inferredSelectedTopLevelMaTerm;
	}


	public void setInferredSelectedTopLevelMaTerm(List<String> inferredSelectedTopLevelMaTerm) {

		this.inferredSelectedTopLevelMaTerm = inferredSelectedTopLevelMaTerm;
	}


	public List<String> getInferredSelectedToLevelMaTermSynonym() {

		return inferredSelectedToLevelMaTermSynonym;
	}


	public void setInferredSelectedToLevelMaTermSynonym(List<String> inferredSelectedToLevelMaTermSynonym) {

		this.inferredSelectedToLevelMaTermSynonym = inferredSelectedToLevelMaTermSynonym;
	}

	public String getIdidid() {

		return ididid;
	}


	public void setIdidid(String ididid) {

		this.ididid = ididid;
	}

	
	public int getParameterStableKey() {

		return parameterStableKey;
	}

	
	public int getParameterId() {

		return parameterId;
	}


	public void setParameterId(int parameterId) {

		this.parameterId = parameterId;
	}


	public String getParameterStableId() {

		return parameterStableId;
	}


	public void setParameterStableId(String parameterStableId) {

		this.parameterStableId = parameterStableId;
	}


	public String getParameterName() {

		return parameterName;
	}


	public void setParameterName(String parameterName) {

		this.parameterName = parameterName;
	}

	public int getPipelineId() {

		return pipelineId;
	}


	public void setPipelineId(int pipelineId) {

		this.pipelineId = pipelineId;
	}

	public void setParameterStableKey(int paramStableKey) {

		this.parameterStableKey = paramStableKey;

	}


	public void setIdIdId(String ididid) {

		this.ididid = ididid;

	}

	public void addMpId(String mpTermId) {

		if (this.mpId == null) {
			this.mpId = new ArrayList<>();
		}
		this.mpId.add(mpTermId);

	}


	public void addMpTerm(String mpTerm) {

		if (this.mpTerm == null) {
			this.mpTerm = new ArrayList<>();
		}
		this.mpTerm.add(mpTerm);

	}


	public void addMpTermSynonym(List<String> mpTermSynonym) {

		if (this.mpTermSynonym == null) {
			this.mpTermSynonym = new ArrayList<>();
		}
		this.mpTermSynonym.addAll(mpTermSynonym);
		this.mpTermSynonym = new ArrayList<>(new HashSet<>(this.mpTermSynonym));
	}

	public void addTopLevelMpId(List<String> topLevelMpTermId) {
		if (this.topLevelMpId == null) {
			this.topLevelMpId = new ArrayList<>();
		}
		this.topLevelMpId.addAll(topLevelMpTermId);
		this.topLevelMpId = new ArrayList<>(new HashSet<>(this.topLevelMpId));
	}


	public void addTopLevelMpTerm(List<String> topLevelMpTerm) {
		if (this.topLevelMpTerm == null) {
			this.topLevelMpTerm = new ArrayList<>();
		}
		this.topLevelMpTerm.addAll(topLevelMpTerm);
		this.topLevelMpTerm = new ArrayList<>(new HashSet<>(this.topLevelMpTerm));
	}


	public void addTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {
		if (this.topLevelMpTermSynonym == null) {
			this.topLevelMpTermSynonym = new ArrayList<>();
		}
		this.topLevelMpTermSynonym.addAll(topLevelMpTermSynonym);
		this.topLevelMpTermSynonym = new ArrayList<>(new HashSet<>(this.topLevelMpTermSynonym));
	}


	public void addIntermediateMpId(List<String> intermediateMpId) {
		if (this.intermediateMpId == null) {
			this.intermediateMpId = new ArrayList<>();
		}
		this.intermediateMpId.addAll(intermediateMpId);
		this.intermediateMpId = new ArrayList<>(new HashSet<>(this.intermediateMpId));
	}


	public void addIntermediateMpTerm(List<String> intermediateMpTerm) {
		if (this.intermediateMpTerm == null) {
			this.intermediateMpTerm = new ArrayList<>();
		}
		this.intermediateMpTerm.addAll(intermediateMpTerm);
		this.intermediateMpTerm = new ArrayList<>(new HashSet<>(this.intermediateMpTerm));
	}


	public void addIntermediateMpTermSynonym(List<String> intermediateMpTermSynonym) {

		if (this.intermediateMpTermSynonym == null) {
			this.intermediateMpTermSynonym = new ArrayList<>();
		}
		this.intermediateMpTermSynonym.addAll(intermediateMpTermSynonym);
		this.intermediateMpTermSynonym = new ArrayList<>(new HashSet<>(this.intermediateMpTermSynonym));
	}


	public void addInferredSelectedTopLevelMaId(List<String> inferredSelectedTopLevelMaId) {

		if (this.inferredSelectedTopLevelMaId == null) {
			this.inferredSelectedTopLevelMaId = new ArrayList<>();
		}
		this.inferredSelectedTopLevelMaId.addAll(inferredSelectedTopLevelMaId);
		
		
	}


	public void addInferredSelectedTopLevelMaTerm(List<String> inferredSelectedTopLevelMaTerm) {

		if (this.inferredSelectedTopLevelMaTerm == null) {
			this.inferredSelectedTopLevelMaTerm = new ArrayList<>();
		}
		this.inferredSelectedTopLevelMaTerm.addAll(inferredSelectedTopLevelMaTerm);
		
	}


	public void addInferredSelectedToLevelMaTermSynonym(List<String> inferredSelectedTopLevelMaTermSynonym) {

		if (this.inferredSelectedToLevelMaTermSynonym== null) {
			this.inferredSelectedToLevelMaTermSynonym = new ArrayList<>();
		}
		this.inferredSelectedToLevelMaTermSynonym.addAll(inferredSelectedTopLevelMaTermSynonym);
		
	}


	public Integer getProcedureId() {
		return procedureId;
	}


	public void setProcedureId(Integer procedureId) {
		this.procedureId = procedureId;
	}


	public String getProcedureStableId() {
		return procedureStableId;
	}


	public void setProcedureStableId(String procedureStableId) {
		this.procedureStableId = procedureStableId;
	}


	public String getProcedureName() {
		return procedureName;
	}


	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}


	public Integer getProcedureStableKey() {
		return procedureStableKey;
	}


	public void setProcedureStableKey(Integer procedureStableKey) {
		this.procedureStableKey = procedureStableKey;
	}


	public String getPipelineStableId() {
		return pipelineStableId;
	}


	public void setPipelineStableId(String pipelineStableId) {
		this.pipelineStableId = pipelineStableId;
	}


	public Integer getPipelineStableKey() {
		return pipelineStableKey;
	}


	public void setPipelineStableKey(Integer pipelineStableKey) {
		this.pipelineStableKey = pipelineStableKey;
	}


	public String getPipelineName() {
		return pipelineName;
	}


	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}


	public List<String> getInferredSelectedTopLevelMaId() {
		return inferredSelectedTopLevelMaId;
	}


	public void setInferredSelectedTopLevelMaId(List<String> inferredSelectedTopLevelMaId) {
		this.inferredSelectedTopLevelMaId = inferredSelectedTopLevelMaId;
	}


	public String getMaTermId() {
		return maId;
	}


	public void setMaId(String maId) {
		this.maId = maId;
	}


	public String getMaName() {
		return maTerm;
	}


	public void setMaName(String maName) {
		this.maTerm = maName;
	}


	public String getUnit() {
		return unit;
	}


	public void setUnit(String unit) {
		this.unit = unit;
	}


	public boolean isIncrement() {
		return increment;
	}


	public void setIncrement(boolean increment) {
		this.increment = increment;
	}


	public boolean isMetadata() {
		return metadata;
	}


	public void setMetadata(boolean metadata) {
		this.metadata = metadata;
	}


	public boolean getHasOptions() {
		return hasOptions;
	}


	public void setHasOptions(boolean hasOptions) {
		this.hasOptions = hasOptions;
	}


	public boolean isDerived() {
		return derived;
	}


	public void setDerived(boolean derived) {
		this.derived = derived;
	}


	public boolean isMedia() {
		return media;
	}


	public void setMedia(boolean media) {
		this.media = media;
	}


	public String getMaTerm() {
		return maTerm;
	}


	public void setMaTerm(String maTerm) {
		this.maTerm = maTerm;
	}


	public String getMaId() {
		return maId;
	}


	@Override
	public String toString() {
		return "PipelineDTO [unit=" + unit + ", increment=" + increment + ", metadata=" + metadata + ", hasOptions="
				+ hasOptions + ", derived=" + derived + ", media=" + media + ", required=" + required + ", description="
				+ description + ", mpTerms=" + mpTerms + ", observationType=" + observationType + ", parameterId="
				+ parameterId + ", parameterStableId=" + parameterStableId + ", parameterName=" + parameterName
				+ ", parameterStableKey=" + parameterStableKey + ", procedureId=" + procedureId + ", procedureStableId="
				+ procedureStableId + ", procedureName=" + procedureName + ", procedureStableKey=" + procedureStableKey
				+ ", pipelineId=" + pipelineId + ", pipelineStableId=" + pipelineStableId + ", pipelineStableKey="
				+ pipelineStableKey + ", pipelineName=" + pipelineName + ", ididid=" + ididid + ", mpId=" + mpId
				+ ", mpTerm=" + mpTerm + ", mpTermSynonym=" + mpTermSynonym + ", topLevelMpId=" + topLevelMpId
				+ ", topLevelMpTerm=" + topLevelMpTerm + ", topLevelMpTermSynonym=" + topLevelMpTermSynonym
				+ ", intermediateMpId=" + intermediateMpId + ", intermediateMpTerm=" + intermediateMpTerm
				+ ", intermediateMpTermSynonym=" + intermediateMpTermSynonym + ", inferredMaId=" + inferredMaId
				+ ", inferredMaTermSynonym=" + inferredMaTermSynonym + ", selectedTopLevelMaId=" + selectedTopLevelMaId
				+ ", inferredSelectedTopLevelMaTerm=" + inferredSelectedTopLevelMaTerm
				+ ", inferredSelectedToLevelMaTermSynonym=" + inferredSelectedToLevelMaTermSynonym
				+ ", inferredSelectedTopLevelMaId=" + inferredSelectedTopLevelMaId + ", maId=" + maId + ", maTerm="
				+ maTerm + "]";
	}


}
