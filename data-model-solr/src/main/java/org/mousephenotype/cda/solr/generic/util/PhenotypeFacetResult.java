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
package org.mousephenotype.cda.solr.generic.util;

import org.mousephenotype.cda.solr.web.dto.PhenotypeCallSummaryDTO;
import org.mousephenotype.cda.utilities.RunStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhenotypeFacetResult {

	private List<PhenotypeCallSummaryDTO>     phenotypeCallSummaries = new ArrayList<PhenotypeCallSummaryDTO>();
	private Map<String, Map<String, Integer>> facetResults           = new HashMap<String, Map<String, Integer>>();
	private List<String>                      errorCodes             = new ArrayList<>();
	private RunStatus                         status                 = new RunStatus();
		
	public List<PhenotypeCallSummaryDTO> getPhenotypeCallSummaries() {
		return phenotypeCallSummaries;
	}
	
	public void setPhenotypeCallSummaries(	List<PhenotypeCallSummaryDTO> phenotypeCallSummaries) {
		this.phenotypeCallSummaries = phenotypeCallSummaries;
	}
	
	public Map<String, Map<String, Integer>> getFacetResults() {
		return facetResults;
	}
	
	public void setFacetResults(Map<String, Map<String, Integer>> facetResults) {
		this.facetResults = facetResults;
	}

	public List<String> getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(List<String> errorCodes) {
		this.errorCodes = errorCodes;
	}

	public void addErrorCode(String code){
		errorCodes.add(code);
	}

	public RunStatus getStatus() {
		return status;
	}

	public void setStatus(RunStatus status) {
		this.status = status;
	}
}