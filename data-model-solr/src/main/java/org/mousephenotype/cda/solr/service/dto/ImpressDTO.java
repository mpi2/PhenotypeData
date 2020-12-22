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

import java.util.*;

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
	public static final String DESCRIPTION = "description";
	public static final String UNITX = "unit_x";
	public static final String UNITY = "unit_y";
	public static final String INCREMENT = "increment";
	public static final String METADATA = "metadata";
	public static final String HAS_OPTIONS = "has_options";
	public static final String CATEGORIES = "categories";
	public static final String DERIVED = "derived";
	public static final String MEDIA = "media";
	public static final String ANNOTATE = "annotate";
	public static final String OBSERVATION_TYPE = ObservationDTO.OBSERVATION_TYPE;
	public static final String DATA_TYPE = ObservationDTO.DATA_TYPE;

	public static final String LEVEL = "level";
	public static final String STAGE = "stage";
	public static final String STAGE_LABEL = "stage_label";
	public static final String SCHEDULE_KEY = "schedule_key";


	public static final String MP_ID = MpDTO.MP_ID; // All possible MP terms
	public static final String MP_TERM = MpDTO.MP_TERM;
	public static final String MP_TERM_SYNONYM = MpDTO.MP_TERM_SYNONYM;
	public static final String TOP_LEVEL_MP_ID = MpDTO.TOP_LEVEL_MP_ID;
	public static final String TOP_LEVEL_MP_TERM = MpDTO.TOP_LEVEL_MP_TERM;
	public static final String TOP_LEVEL_MP_TERM_SYNONYM = MpDTO.TOP_LEVEL_MP_TERM_SYNONYM;
	public static final String INTERMEDIATE_MP_ID = MpDTO.INTERMEDIATE_MP_ID;
	public static final String INTERMEDIATE_MP_TERM = MpDTO.INTERMEDIATE_MP_TERM;
	public static final String INTERMEDIATE_MP_TERM_SYNONYM = MpDTO.INTERMEDIATE_MP_TERM_SYNONYM;
	public static final String ABNORMAL_MP_ID = "abnormal_mp_id";
	public static final String INCREASED_MP_ID = "increased_mp_id";
	public static final String DECREASED_MP_ID = "decreased_mp_id";
	public static final String ABNORMAL_MP_TERM = "abnormal_mp_term";
	public static final String INCREASED_MP_TERM = "increased_mp_term";
	public static final String DECREASED_MP_TERM = "decreased_mp_term";

	public static final String MOUSE_ANATOMY_ID = "mouse_anatomy_id";
	public static final String MOUSE_ANATOMY_TERM = "mouse_anatomy_term";
	public static final String INFERRED_MA_ID = MpDTO.INFERRED_MA_ID;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_ID = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_ID;
	public static final String INFERRED_SELECTED_TOP_LEVEL_MA_TERM = MpDTO.INFERRED_SELECTED_TOP_LEVEL_MA_TERM;

	public static final String EMBRYO_ANATOMY_ID = "embryo_anatomy_id";
	public static final String EMBRYO_ANATOMY_TERM = "embryo_anatomy_term";

	@Field(INCREASED_MP_ID)
	List<String> increasedMpId;

	@Field(ABNORMAL_MP_ID)
	List<String> abnormalMpId;

	@Field(DECREASED_MP_ID)
	List<String> decreasedMpId;


	@Field(INCREASED_MP_TERM)
	List<String> increasedMpTerm;

	@Field(ABNORMAL_MP_TERM)
	List<String> abnormalMpTerm;

	@Field(DECREASED_MP_TERM)
	List<String> decreasedMpTerm;

	@Field(CATEGORIES)
	private List<String> catgories;

	@Field(UNITX)
	private String unitX;

	@Field(UNITY)
	private String unitY;

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

	@Field(ANNOTATE)
	private boolean annotate;

	@Field(REQUIRED)
	private boolean required;

	@Field(DESCRIPTION)
	private String description;

	@Field(OBSERVATION_TYPE)
	private String observationType;

	@Field(DATA_TYPE)
	private String dataType;

	@Field(LEVEL)
	private String level;

	@Field(STAGE)
	private String stage;

	@Field(STAGE_LABEL)
	private String stageLabel;

	@Field(SCHEDULE_KEY)
	private Long scheduleKey;

	@Field(PARAMETER_ID)
	private Long parameterId;

	@Field(PARAMETER_STABLE_ID)
	private String parameterStableId;

	@Field(PARAMETER_NAME)
	private String parameterName;

	@Field(PARAMETER_STABLE_KEY)
	private Long parameterStableKey;

	@Field(PROCEDURE_ID)
	private Long procedureId;

	@Field(PROCEDURE_STABLE_ID)
	private String procedureStableId;

	@Field(PROCEDURE_NAME)
	private String procedureName;

	@Field(PROCEDURE_STABLE_KEY)
	private Long procedureStableKey;

	@Field(PIPELINE_ID)
	private Long pipelineId;

	@Field(PIPELINE_STABLE_ID)
	private String pipelineStableId;

	@Field(PIPELINE_STABLE_KEY)
	private Long pipelineStableKey;

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

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_ID)
	private List<String> selectedTopLevelMaId;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_TERM)
	private List<String> inferredSelectedTopLevelMaTerm;

	@Field(INFERRED_SELECTED_TOP_LEVEL_MA_ID)
	private List<String> inferredSelectedTopLevelMaId;

	@Field(MOUSE_ANATOMY_ID)
	private String maId;

	@Field(MOUSE_ANATOMY_TERM)
	private String maTerm;

	@Field(EMBRYO_ANATOMY_ID)
	private String emapId;

	@Field(EMBRYO_ANATOMY_TERM)
	private String emapTerm;

	public boolean isHasOptions() {
		return hasOptions;
	}

	public String getEmapId() {
		return emapId;
	}

	public void setEmapId(String emapId) {
		this.emapId = emapId;
	}


	public String getEmapTerm() {
		return emapTerm;
	}


	public List<String> getIncreasedMpTerm() {
		return increasedMpTerm;
	}


	public void setIncreasedMpTerm(List<String> increasedMpTerm) {
		this.increasedMpTerm = increasedMpTerm;
	}
	public void addIncreasedMpTerm(String mpTerm){
		if (this.increasedMpTerm == null){
			this.increasedMpTerm = new ArrayList<>();
		}
		increasedMpTerm.add(mpTerm);
	}

	public List<String> getAbnormalMpTerm() {
		return abnormalMpTerm;
	}


	public void setAbnormalMpTerm(List<String> abnormalMpTerm) {
		this.abnormalMpTerm = abnormalMpTerm;
	}
	public void addAbnormalMpTerm(String mpTerm){
		if (this.abnormalMpTerm == null){
			this.abnormalMpTerm = new ArrayList<>();
		}
		abnormalMpTerm.add(mpTerm);
	}

	public List<String> getDecreasedMpTerm() {
		return decreasedMpTerm;
	}


	public void setDecreasedMpTerm(List<String> decreasedMpTerm) {
		this.decreasedMpTerm = decreasedMpTerm;
	}

	public void addDecreasedMpTerm(String mpTerm){
		if (this.decreasedMpTerm == null){
			this.decreasedMpTerm = new ArrayList<>();
		}
		decreasedMpTerm.add(mpTerm);
	}


	public void setEmapTerm(String emapTerm) {
		this.emapTerm = emapTerm;
	}






	public List<String> getIncreasedMpId() {
		return increasedMpId;
	}


	public void setIncreasedMpId(List<String> increasedMpId) {
		this.increasedMpId = increasedMpId;
	}
	public void addIncreasedMpId(String mpTerm){
		if (this.increasedMpId == null){
			this.increasedMpId = new ArrayList<>();
		}
		increasedMpId.add(mpTerm);
	}

	public List<String> getAbnormalMpId() {
		return abnormalMpId;
	}


	public void setAbnormalMpId(List<String> abnormalMpId) {
		this.abnormalMpId = abnormalMpId;
	}
	public void addAbnormalMpId(String mpTerm){
		if (this.abnormalMpId == null){
			this.abnormalMpId = new ArrayList<>();
		}
		abnormalMpId.add(mpTerm);
	}

	public List<String> getDecreasedMpId() {
		return decreasedMpId;
	}


	public void setDecreasedMpId(List<String> decreasedMpId) {
		this.decreasedMpId = decreasedMpId;
	}
	public void addDecreasedMpId(String mpTerm){
		if (this.decreasedMpId == null){
			this.decreasedMpId = new ArrayList<>();
		}
		decreasedMpId.add(mpTerm);
	}

	public List<String> getCatgories() {
		return catgories;
	}

	public void setCatgories(List<String> catgories) {
		this.catgories = catgories;
	}

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

	public String getObservationType() {
		return observationType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setObservationType(String observationType) {
		this.observationType = observationType;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public Long getScheduleKey() {
		return scheduleKey;
	}

	public void setScheduleKey(Long scheduleKey) {
		this.scheduleKey = scheduleKey;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ImpressDTO that = (ImpressDTO) o;
		return increment == that.increment &&
				metadata == that.metadata &&
				hasOptions == that.hasOptions &&
				derived == that.derived &&
				media == that.media &&
				annotate == that.annotate &&
				required == that.required &&
				parameterId == that.parameterId &&
				parameterStableKey == that.parameterStableKey &&
				procedureStableKey == that.procedureStableKey &&
				pipelineId == that.pipelineId &&
				pipelineStableKey == that.pipelineStableKey &&
				Objects.equals(increasedMpId, that.increasedMpId) &&
				Objects.equals(abnormalMpId, that.abnormalMpId) &&
				Objects.equals(decreasedMpId, that.decreasedMpId) &&
				Objects.equals(increasedMpTerm, that.increasedMpTerm) &&
				Objects.equals(abnormalMpTerm, that.abnormalMpTerm) &&
				Objects.equals(decreasedMpTerm, that.decreasedMpTerm) &&
				Objects.equals(catgories, that.catgories) &&
				Objects.equals(unitX, that.unitX) &&
				Objects.equals(unitY, that.unitY) &&
				Objects.equals(description, that.description) &&
				Objects.equals(observationType, that.observationType) &&
				Objects.equals(dataType, that.dataType) &&
				Objects.equals(level, that.level) &&
				Objects.equals(stage, that.stage) &&
				Objects.equals(stageLabel, that.stageLabel) &&
				Objects.equals(scheduleKey, that.scheduleKey) &&
				Objects.equals(parameterStableId, that.parameterStableId) &&
				Objects.equals(parameterName, that.parameterName) &&
				Objects.equals(procedureId, that.procedureId) &&
				Objects.equals(procedureStableId, that.procedureStableId) &&
				Objects.equals(procedureName, that.procedureName) &&
				Objects.equals(pipelineStableId, that.pipelineStableId) &&
				Objects.equals(pipelineName, that.pipelineName) &&
				Objects.equals(ididid, that.ididid) &&
				Objects.equals(mpId, that.mpId) &&
				Objects.equals(mpTerm, that.mpTerm) &&
				Objects.equals(mpTermSynonym, that.mpTermSynonym) &&
				Objects.equals(topLevelMpId, that.topLevelMpId) &&
				Objects.equals(topLevelMpTerm, that.topLevelMpTerm) &&
				Objects.equals(topLevelMpTermSynonym, that.topLevelMpTermSynonym) &&
				Objects.equals(intermediateMpId, that.intermediateMpId) &&
				Objects.equals(intermediateMpTerm, that.intermediateMpTerm) &&
				Objects.equals(intermediateMpTermSynonym, that.intermediateMpTermSynonym) &&
				Objects.equals(inferredMaId, that.inferredMaId) &&
				Objects.equals(selectedTopLevelMaId, that.selectedTopLevelMaId) &&
				Objects.equals(inferredSelectedTopLevelMaTerm, that.inferredSelectedTopLevelMaTerm) &&
				Objects.equals(inferredSelectedTopLevelMaId, that.inferredSelectedTopLevelMaId) &&
				Objects.equals(maId, that.maId) &&
				Objects.equals(maTerm, that.maTerm) &&
				Objects.equals(emapId, that.emapId) &&
				Objects.equals(emapTerm, that.emapTerm);
	}

	@Override
	public int hashCode() {

		return Objects.hash(increasedMpId, abnormalMpId, decreasedMpId, increasedMpTerm, abnormalMpTerm, decreasedMpTerm, catgories, unitX, unitY, increment, metadata, hasOptions, derived, media, annotate, required, description, observationType, level, stage, stageLabel, scheduleKey, parameterId, parameterStableId, parameterName, parameterStableKey, procedureId, procedureStableId, procedureName, procedureStableKey, pipelineId, pipelineStableId, pipelineStableKey, pipelineName, ididid, mpId, mpTerm, mpTermSynonym, topLevelMpId, topLevelMpTerm, topLevelMpTermSynonym, intermediateMpId, intermediateMpTerm, intermediateMpTermSynonym, inferredMaId, selectedTopLevelMaId, inferredSelectedTopLevelMaTerm, inferredSelectedTopLevelMaId, maId, maTerm, emapId, emapTerm);
	}

	public String getStage() {

		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getStageLabel() {
		return stageLabel;
	}

	public void setStageLabel(String stageLabel) {
		this.stageLabel = stageLabel;
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


	public String getIdidid() {

		return ididid;
	}


	public void setIdidid(String ididid) {

		this.ididid = ididid;
	}


	public Long getParameterStableKey() {

		return parameterStableKey;
	}


	public Long getParameterId() {

		return parameterId;
	}


	public void setParameterId(Long parameterId) {

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

	public Long getPipelineId() {

		return pipelineId;
	}


	public void setPipelineId(Long pipelineId) {

		this.pipelineId = pipelineId;
	}

	public void setParameterStableKey(Long paramStableKey) {

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

	public void addTopLevelMpId(Collection<String> topLevelMpTermId) {
		if (this.topLevelMpId == null) {
			this.topLevelMpId = new ArrayList<>();
		}
		this.topLevelMpId.addAll(topLevelMpTermId);
		this.topLevelMpId = new ArrayList<>(new HashSet<>(this.topLevelMpId));
	}


	public void addTopLevelMpTerm(Collection<String> topLevelMpTerm) {
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


	public void addIntermediateMpId(Collection<String> intermediateMpId) {
		if (this.intermediateMpId == null) {
			this.intermediateMpId = new ArrayList<>();
		}
		this.intermediateMpId.addAll(intermediateMpId);
		this.intermediateMpId = new ArrayList<>(new HashSet<>(this.intermediateMpId));
	}


	public void addIntermediateMpTerm(Collection<String> intermediateMpTerm) {
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


	public Long getProcedureId() {
		return procedureId;
	}


	public void setProcedureId(Long procedureId) {
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


	public Long getProcedureStableKey() {
		return procedureStableKey;
	}


	public void setProcedureStableKey(Long procedureStableKey) {
		this.procedureStableKey = procedureStableKey;
	}


	public String getPipelineStableId() {
		return pipelineStableId;
	}


	public void setPipelineStableId(String pipelineStableId) {
		this.pipelineStableId = pipelineStableId;
	}


	public Long getPipelineStableKey() {
		return pipelineStableKey;
	}


	public void setPipelineStableKey(Long pipelineStableKey) {
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

	public String getUnitX() {
		return unitX;
	}

	public void setUnitX(String unitX) {
		this.unitX = unitX;
	}

	public String getUnitY() {
		return unitY;
	}

	public void setUnitY(String unitY) {
		this.unitY = unitY;
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

	public boolean isAnnotate() {
		return annotate;
	}

	public void setAnnotate(boolean annotate) {
		this.annotate = annotate;
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
		return "ImpressDTO{" +
				"catgories=" + catgories +
				", unitX='" + unitX + '\'' +
				", unitY='" + unitY + '\'' +
				", increment=" + increment +
				", metadata=" + metadata +
				", hasOptions=" + hasOptions +
				", derived=" + derived +
				", level=" + level +
				", scheduleKey=" + scheduleKey +
				", media=" + media +
				", annotate=" + annotate +
				", required=" + required +
				", description='" + description + '\'' +
				", observationType='" + observationType + '\'' +
				", parameterId=" + parameterId +
				", parameterStableId='" + parameterStableId + '\'' +
				", parameterName='" + parameterName + '\'' +
				", parameterStableKey=" + parameterStableKey +
				", procedureId=" + procedureId +
				", procedureStableId='" + procedureStableId + '\'' +
				", procedureName='" + procedureName + '\'' +
				", procedureStableKey=" + procedureStableKey +
				", pipelineId=" + pipelineId +
				", pipelineStableId='" + pipelineStableId + '\'' +
				", pipelineStableKey=" + pipelineStableKey +
				", pipelineName='" + pipelineName + '\'' +
				", ididid='" + ididid + '\'' +
				", mpId=" + mpId +
				", mpTerm=" + mpTerm +
				", mpTermSynonym=" + mpTermSynonym +
				", topLevelMpId=" + topLevelMpId +
				", topLevelMpTerm=" + topLevelMpTerm +
				", topLevelMpTermSynonym=" + topLevelMpTermSynonym +
				", intermediateMpId=" + intermediateMpId +
				", intermediateMpTerm=" + intermediateMpTerm +
				", intermediateMpTermSynonym=" + intermediateMpTermSynonym +
				", inferredMaId=" + inferredMaId +
				", selectedTopLevelMaId=" + selectedTopLevelMaId +
				", inferredSelectedTopLevelMaTerm=" + inferredSelectedTopLevelMaTerm +
				", inferredSelectedTopLevelMaId=" + inferredSelectedTopLevelMaId +
				", maId='" + maId + '\'' +
				", maTerm='" + maTerm + '\'' +
				", emapId='" + emapId + '\'' +
				", emapTerm='" + emapTerm + '\'' +
				'}';
	}

	public ImpressDTO(){

	}

	/**
	 * @author tudose
	 * @return sort by name but IMPC objects always first.
	 */
	public static Comparator<ImpressDTO> getComparatorByProcedureNameImpcFirst()	{
		Comparator<ImpressDTO> comp = new Comparator<ImpressDTO>(){
	    @Override
	    public int compare(ImpressDTO param1, ImpressDTO param2)
	    {
	    	if (isImpc(param1.getProcedureStableId()) && !isImpc(param2.getProcedureStableId())){
				return -1;
			}
			if (isImpc(param2.getProcedureStableId()) && !isImpc(param1.getProcedureStableId())){
				return 1;
			}
			return param1.getProcedureName().compareTo(param2.getProcedureName());
	    }
		private boolean isImpc(String param){
			return param.startsWith("IMPC");
		}

		};
		return comp;
	}

	public static Comparator<ImpressDTO> getComparatorByProcedureName()	{
		Comparator<ImpressDTO> comp = new Comparator<ImpressDTO>(){
			@Override
			public int compare(ImpressDTO param1, ImpressDTO param2)
			{
				String s1 = param1.getProcedureName() + "_" + param1.getPipelineName();
				String s2 = param2.getProcedureName() + "_" + param2.getPipelineName();
				return s1.compareTo(s2);
			}
			private boolean isImpc(String param){
				return param.startsWith("IMPC");
			}

		};
		return comp;
	}
}
