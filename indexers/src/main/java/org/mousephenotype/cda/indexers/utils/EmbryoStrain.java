/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.indexers.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbryoStrain {

	private String       analysisViewUrl;
	private String       centre;
	private String       colonyId;
	private String       mgiGeneAccessionId;
	private List<String> modalities;
	private List<Long>   parameterStableKeys;
	private List<Long>   procedureStableKeys;
	private String       url;

	public String getAnalysisViewUrl() {
		return analysisViewUrl;
	}

	public void setAnalysisViewUrl(String analysisViewUrl) {
		this.analysisViewUrl = analysisViewUrl;
	}

	public String getCentre() {
		return centre;
	}

	public void setCentre(String centre) {
		this.centre = centre;
	}

	public String getColonyId() {
		return colonyId;
	}

	public void setColonyId(String colonyId) {
		this.colonyId = colonyId;
	}

	public String getMgiGeneAccessionId() {
		return mgiGeneAccessionId;
	}

	public void setMgiGeneAccessionId(String mgiGeneAccessionId) {
		this.mgiGeneAccessionId = mgiGeneAccessionId;
	}

	public List<String> getModalities() {
		return modalities;
	}

	public void setModalities(List<String> modalities) {
		this.modalities = modalities;
	}

	public List<Long> getParameterStableKeys() {
		return parameterStableKeys;
	}

	public void setParameterStableKeys(List<Long> parameterStableKeys) {
		this.parameterStableKeys = parameterStableKeys;
	}

	public List<Long> getProcedureStableKeys() {
		return procedureStableKeys;
	}

	public void setProcedureStableKeys(List<Long> procedureStableKeys) {
		this.procedureStableKeys = procedureStableKeys;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "EmbryoStrain{" +
				"analysisViewUrl='" + analysisViewUrl + '\'' +
				", centre='" + centre + '\'' +
				", colonyId='" + colonyId + '\'' +
				", mgiGeneAccessionId='" + mgiGeneAccessionId + '\'' +
				", modalities=" + modalities +
				", parameterStableKeys=" + parameterStableKeys +
				", procedureStableKeys=" + procedureStableKeys +
				", url='" + url + '\'' +
				'}';
	}
}