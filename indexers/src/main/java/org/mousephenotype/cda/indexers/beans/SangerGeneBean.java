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
package org.mousephenotype.cda.indexers.beans;

import org.apache.solr.client.solrj.beans.Field;

import java.util.List;

/**
 * @author Matt Pearce
 */
public class SangerGeneBean {

	public static final String MGI_ACCESSION_ID = "mgi_accession_id";
	public static final String MGI_ALLELE_ACCESSION_ID = "allele_accession_id";
	public static final String FEATURE_TYPE = "feature_type";
	public static final String MARKER_SYMBOL = "marker_symbol";
	public static final String MARKER_SYMBOL_LOWERCASE = "marker_symbol_lowercase";
	public static final String LATEST_ES_CELL_STATUS = "latest_es_cell_status";
	public static final String LATEST_MOUSE_STATUS = "latest_mouse_status";
	public static final String LATEST_PROJECT_STATUS = "latest_project_status";
	public static final String LATEST_PHENOTYPE_STARTED = "latest_phenotype_started";
	public static final String LATEST_PHENOTYPE_COMPLETE = "latest_phenotype_complete";
	public static final String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
	public static final String LATEST_PRODUCTION_CENTRE = "latest_production_centre";
	public static final String LATEST_PHENOTYPING_CENTRE = "latest_phenotyping_centre";
	public static final String CHR_NAME = "feature_chromosome";
	public static final String CHR_START = "feature_coord_start";
	public static final String CHR_END = "feature_coord_end";
	public static final String CHR_STRAND = "feature_strand";

	@Field(MGI_ACCESSION_ID)
	private String mgiAccessionId;

	@Field(MGI_ALLELE_ACCESSION_ID)
	private List<String> mgiAlleleAccessionIds;

	@Field(FEATURE_TYPE)
	private String featureType;

	@Field(MARKER_SYMBOL)
	private String markerSymbol;

	@Field(MARKER_SYMBOL_LOWERCASE)
	private String markerSymbolLowercase;
	
	@Field(LATEST_ES_CELL_STATUS)
	private String latestEsCellStatus;

	@Field(LATEST_MOUSE_STATUS)
	private String latestMouseStatus;

	@Field(LATEST_PROJECT_STATUS)
	private String latestProjectStatus;

	@Field(LATEST_PHENOTYPE_STARTED)
	private String latestPhenotypeStarted;

	@Field(LATEST_PHENOTYPE_COMPLETE)
	private String latestPhenotypeComplete;

	@Field(LATEST_PHENOTYPE_STATUS)
	private String latestPhenotypeStatus;

	@Field(LATEST_PRODUCTION_CENTRE)
	private List<String> latestProductionCentre;

	@Field(LATEST_PHENOTYPING_CENTRE)
	private List<String> latestPhenotypingCentre;

	@Field(CHR_NAME)
	private String chrName;

	@Field(CHR_START)
	private String chrStart;

	@Field(CHR_END)
	private String chrEnd;

	@Field(CHR_STRAND)
	private String chrStrand;


	/**
	 * @return the mgiAccessionId
	 */
	public String getMgiAccessionId() {
		return mgiAccessionId;
	}

	/**
	 * @param mgiAccessionId
	 *            the mgiAccessionId to set
	 */
	public void setMgiAccessionId(String mgiAccessionId) {
		this.mgiAccessionId = mgiAccessionId;
	}
	
	/**
	 * @return the mgiAlleleAccessionIds
	 */
	public List<String> getMgiAlleleAccessionIds() {
		return mgiAlleleAccessionIds;
	}

	/**
	 * @param mgiAlleleAccessionIds
	 *            the mgiAlleleAccessionId to set
	 */
	public void setMgiAlleleAccessionIds(List<String> mgiAlleleAccessionIds) {
		this.mgiAlleleAccessionIds = mgiAlleleAccessionIds;
	}

	
	/**
	 * @return the featureType
	 */
	public String getFeatureType() {
		return featureType;
	}

	/**
	 * @param featureType
	 *            the featureType to set
	 */
	public void setFeatureType(String featureType) {
		this.featureType = featureType;
	}

	/**
	 * @return the markerSymbol
	 */
	public String getMarkerSymbol() {
		return markerSymbol;
	}

	/**
	 * @param markerSymbol
	 *            the markerSymbol to set
	 */
	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}

	/**
	 * @return the markerSymbolLowercase
	 */
	public String getMarkerSymbolLowercase() {
		return markerSymbolLowercase;
	}

	/**
	 * @param markerSymbolLowercase
	 *            the markerSymbolLowercase to set
	 */
	public void setMarkerSymbolLowercase(String markerSymbolLowercase) {
		this.markerSymbolLowercase = markerSymbolLowercase;
	}

	
	
	/**
	 * @return the latestEsCellStatus
	 */
	public String getLatestEsCellStatus() {
		return latestEsCellStatus;
	}

	/**
	 * @param latestEsCellStatus
	 *            the latestEsCellStatus to set
	 */
	public void setLatestEsCellStatus(String latestEsCellStatus) {
		this.latestEsCellStatus = latestEsCellStatus;
	}

	/**
	 * @return the latestMouseStatus
	 */
	public String getLatestMouseStatus() {
		return latestMouseStatus;
	}

	/**
	 * @param latestMouseStatus
	 *            the latestMouseStatus to set
	 */
	public void setLatestMouseStatus(String latestMouseStatus) {
		this.latestMouseStatus = latestMouseStatus;
	}

	/**
	 * @return the latestProjectStatus
	 */
	public String getLatestProjectStatus() {
		return latestProjectStatus;
	}

	/**
	 * @param latestProjectStatus
	 *            the latestProjectStatus to set
	 */
	public void setLatestProjectStatus(String latestProjectStatus) {
		this.latestProjectStatus = latestProjectStatus;
	}

	/**
	 * @return the latestPhenotypeStarted
	 */
	public String getLatestPhenotypeStarted() {
		return latestPhenotypeStarted;
	}

	/**
	 * @param latestPhenotypeStarted
	 *            the latestPhenotypeStarted to set
	 */
	public void setLatestPhenotypeStarted(String latestPhenotypeStarted) {
		this.latestPhenotypeStarted = latestPhenotypeStarted;
	}

	/**
	 * @return the latestPhenotypeComplete
	 */
	public String getLatestPhenotypeComplete() {
		return latestPhenotypeComplete;
	}

	/**
	 * @param latestPhenotypeComplete
	 *            the latestPhenotypeComplete to set
	 */
	public void setLatestPhenotypeComplete(String latestPhenotypeComplete) {
		this.latestPhenotypeComplete = latestPhenotypeComplete;
	}

	/**
	 * @return the latestPhenotypeStatus
	 */
	public String getLatestPhenotypeStatus() {
		return latestPhenotypeStatus;
	}

	/**
	 * @param latestPhenotypeStatus
	 *            the latestPhenotypeStatus to set
	 */
	public void setLatestPhenotypeStatus(String latestPhenotypeStatus) {
		this.latestPhenotypeStatus = latestPhenotypeStatus;
	}

	/**
	 * @return the latestProductionCentre
	 */
	public List<String> getLatestProductionCentre() {
		return latestProductionCentre;
	}

	/**
	 * @param latestProductionCentre
	 *            the latestProductionCentre to set
	 */
	public void setLatestProductionCentre(List<String> latestProductionCentre) {
		this.latestProductionCentre = latestProductionCentre;
	}

	/**
	 * @return the latestPhenotypingCentre
	 */
	public List<String> getLatestPhenotypingCentre() {
		return latestPhenotypingCentre;
	}

	/**
	 * @param latestPhenotypingCentre
	 *            the latestPhenotypingCentre to set
	 */
	public void setLatestPhenotypingCentre(List<String> latestPhenotypingCentre) {
		this.latestPhenotypingCentre = latestPhenotypingCentre;
	}


	/**
	 * @return the chr name
	 */
	public String getChrName() {
		return chrName;
	}

	/**
	 * set the chr name
	 */
	public void setChrName(String chrName) {
		this.chrName = chrName;
	}


	/**
	 * @return the chr start
	 */
	public String getChrStart() {
		return chrStart;
	}

	/**
	 * set the chr start
	 */
	public void setChrStart(String chrStart) {
		this.chrStart = chrStart;
	}

	/**
	 * @return the chr end
	 */
	public String getChrEnd() {
		return chrEnd;
	}

	/**
	 * set the chr end
	 */
	public void setChrEnd(String chrEnd) {
		this.chrEnd = chrEnd;
	}

	/**
	 * @return the chr strand
	 */
	public String getChrStrand() {
		return chrStrand;
	}

	/**
	 * set the chr strand
	 */
	public void setChrStrand(String chrStrand) {
		this.chrStrand = chrStrand;
	}

	@Override
	public String toString() {
		return "SangerGeneBean{" +
				"mgiAccessionId='" + mgiAccessionId + '\'' +
				", mgiAlleleAccessionIds=" + mgiAlleleAccessionIds +
				", featureType='" + featureType + '\'' +
				", markerSymbol='" + markerSymbol + '\'' +
				", markerSymbolLowercase='" + markerSymbolLowercase + '\'' +
				", latestEsCellStatus='" + latestEsCellStatus + '\'' +
				", latestMouseStatus='" + latestMouseStatus + '\'' +
				", latestProjectStatus='" + latestProjectStatus + '\'' +
				", latestPhenotypeStarted='" + latestPhenotypeStarted + '\'' +
				", latestPhenotypeComplete='" + latestPhenotypeComplete + '\'' +
				", latestPhenotypeStatus='" + latestPhenotypeStatus + '\'' +
				", latestProductionCentre=" + latestProductionCentre +
				", latestPhenotypingCentre=" + latestPhenotypingCentre +
				", chrName='" + chrName + '\'' +
				", chrStart=" + chrStart +
				", chrEnd=" + chrEnd +
				", chrStrand='" + chrStrand + '\'' +
				'}';
	}
}
