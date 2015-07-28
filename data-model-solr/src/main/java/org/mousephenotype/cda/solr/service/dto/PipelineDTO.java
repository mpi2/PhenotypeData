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
import java.util.List;

/**
 * @since 2015/07/28
 * @author tudose
 *
 */
public class PipelineDTO  extends ImpressBaseDTO {
	
	private List<ProcedureDTO> procedures;
	
	
	public PipelineDTO(){
		super();
	}

	public List<ProcedureDTO> getProcedures() {
		return procedures;
	}

	public void setProcedures(List<ProcedureDTO> procedures) {
		this.procedures = procedures;
	}
	
	public void addProcedure(ProcedureDTO procedure) {
		if (this.procedures == null){
			procedures = new ArrayList<>();
		}
		this.procedures.add(procedure);
	}
	
}

