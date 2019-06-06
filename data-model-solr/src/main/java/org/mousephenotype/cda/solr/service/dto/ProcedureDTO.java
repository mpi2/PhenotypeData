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
/**
 * @author tudose
 */

package org.mousephenotype.cda.solr.service.dto;
import java.util.ArrayList;
import java.util.List;


/**
 * @author tudose
 * @since 2015/07/28
 */
public class ProcedureDTO extends ImpressBaseDTO{

	private boolean            required;
	private String             procNameId;
	private String             observationType;
	private String             description;
	private String             level;
	private String             stage;
	private String             stageLabel;
	private int                scheduleKey;
	private List<ParameterDTO> parameters;
	
	public ProcedureDTO(Integer id, Integer stableKey, String stableId, String name){
		
		super(id, stableKey, stableId, name);
	}
	
	public ProcedureDTO() {
		super ();
		this.parameters = new ArrayList<>();
	}
	
	
	public void addParameter(ParameterDTO parameter){
		if (this.parameters == null){
			this.parameters = new ArrayList<>();
		}
		parameters.add(parameter);
	}


	public List<ParameterDTO> getParameters() {
	
		return parameters;
	}
	
	public void setParameters(List<ParameterDTO> parameters) {
	
		this.parameters = parameters;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public String getProcNameId() {
		return procNameId;
	}

	public void setProcNameId(String procNameId) {
		this.procNameId = procNameId;
	}

	public String getObservationType() {
		return observationType;
	}

	public void setObservationType(String observationType) {
		this.observationType = observationType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getScheduleKey() {
		return scheduleKey;
	}

	public void setScheduleKey(int scheduleKey) {
		this.scheduleKey = scheduleKey;
	}
}