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
package org.mousephenotype.cda.solr.web.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since 2015/07/14
 * @author tudose
 *
 */
public class AllelePageDTO {

	String geneAccession;
	String geneSymbol;
	List<String> alleleSymbols;
	List<String> escapedAlleleSymbols;
	List<String> phenotypingCenters;
	List<String> pipelineNames;
	List<String> mpTerms;
	Map<String, List<String>> parametersByProcedure = new HashMap<>();


	public Map<String, List<String>> getParametersByProcedure() {
		return parametersByProcedure;
	}

	public void setParametersByProcedure(Map<String, List<String>> parametersByProcedure) {
		this.parametersByProcedure = parametersByProcedure;
	}

	public void addParametersByProcedure(String key, List<String> parametersByProcedure) {
		if (this.parametersByProcedure == null){ this.parametersByProcedure = new HashMap<>();}
		this.parametersByProcedure.put(key, parametersByProcedure);
	}

	public String getGeneAccession() {
		return geneAccession;
	}

	public void setGeneAccession(String geneAccession) {
		this.geneAccession = geneAccession;
	}

	public String getGeneSymbol() {
		return geneSymbol;
	}

	public void setGeneSymbol(String geneSymbol) {
		this.geneSymbol = geneSymbol;
	}
	
	public List<String> getMpTerms() {
		return mpTerms;
	}

	public void setMpTerms(List<String> mpTerms) {
		this.mpTerms = mpTerms;
	}

	public void addMpTerms(String mpTerm) {
		mpTerms = mpTerms == null ? new ArrayList<>() : mpTerms;
		mpTerms.add(mpTerm);
	}
	
	public List<String> getAlleleSymbols() {
		return alleleSymbols;
	}

	public void setAlleleSymbols(List<String> alleleSymbols) {
		this.alleleSymbols = alleleSymbols;
	}

	public void addAlleleSymbol(String alleleSymbol) {
		alleleSymbols = alleleSymbols == null ? new ArrayList<>() : alleleSymbols;
		alleleSymbols.add(alleleSymbol);

		escapedAlleleSymbols = escapedAlleleSymbols == null ? new ArrayList<>() : escapedAlleleSymbols;
		escapedAlleleSymbols.add(alleleSymbol.replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
	}

	public List<String> getPhenotypingCenters() {
		return phenotypingCenters;
	}

	public void setPhenotypingCenters(List<String> phenotypingCenters) {
		this.phenotypingCenters = phenotypingCenters;
	}

	public void addPhenotypingCenter(String phenotypingCenter) {
		phenotypingCenters = phenotypingCenters == null ? new ArrayList<>() : phenotypingCenters;
		phenotypingCenters.add(phenotypingCenter);
	}

	public List<String> getPipelineNames() {
		return pipelineNames;
	}

	public void setPipelineNames(List<String> pipelineNames) {
		this.pipelineNames = pipelineNames;
	}

	public void addPipelineName(String pipelineName) {
		pipelineNames = pipelineNames == null ? new ArrayList<>() : pipelineNames;
		pipelineNames.add(pipelineName);
	}


	public List<String> getEscapedAlleleSymbols() {
		return escapedAlleleSymbols;
	}

	public void setEscapedAlleleSymbols(List<String> escapedAlleleSymbols) {
		this.escapedAlleleSymbols = escapedAlleleSymbols;
	}

	@Override
	public String toString() {
		return "AllelePageDTO [geneAccession=" + geneAccession + ", geneSymbol=" + geneSymbol + ", alleleSymbols="
				+ alleleSymbols + ", phenotypingCenters=" + phenotypingCenters + ", pipelineNames=" + pipelineNames
				+ "]";
	}

}
