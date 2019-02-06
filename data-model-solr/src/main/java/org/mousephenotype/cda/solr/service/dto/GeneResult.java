package org.mousephenotype.cda.solr.service.dto;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

/**
 * class to contain minimal information on gene
 * @author jwarren
 *
 */
public class GeneResult {
	public static final String MARKER_SYMBOL = "marker_symbol";
	
	@Field(MARKER_SYMBOL)
	String markerSymbol;
	
	public static final String MARKER_NAME = "marker_name";
	
	@Field(MARKER_NAME)
	String markerName;
	
	public static final String HUMAN_GENE_SYMBOL = "human_gene_symbol";
	
	@Field(HUMAN_GENE_SYMBOL)
	List<String> humanGeneSymbol;
	
	public static final String MARKER_SYNONYM = "marker_synonym";
	
	@Field(MARKER_SYNONYM)
	List<String> markerSynonym;
	
	public static final String LATEST_ES_CELL_STATUS = "latest_es_cell_status";
	
	@Field(LATEST_ES_CELL_STATUS)
	String latestEsCellStatus;
	
	public static final String LATEST_MOUSE_STATUS = "latest_mouse_status";
	
	@Field(LATEST_MOUSE_STATUS)
	String latestMouseStatus;

	public static final String LATEST_PHENOTYPE_STATUS = "latest_phenotype_status";
	
	@Field(LATEST_PHENOTYPE_STATUS)
	String latestPhenotypeStatus;

	public String getMarkerSymbol() {
		return markerSymbol;
	}

	public void setMarkerSymbol(String markerSymbol) {
		this.markerSymbol = markerSymbol;
	}

	public String getMarkerName() {
		return markerName;
	}

	public void setMarkerName(String markerName) {
		this.markerName = markerName;
	}

	public List<String> getHumanGeneSymbol() {
		return humanGeneSymbol;
	}

	public void setHumanGeneSymbol(List<String> humanGeneSymbol) {
		this.humanGeneSymbol = humanGeneSymbol;
	}


	public void setMarkerSynonym(List<String> markerSynonym) {
		this.markerSynonym = markerSynonym;
	}

	public List<String> getMarkerSynonym() {
		return this.markerSynonym;
	}
	
	public String getLatestEsCellStatus() {
		return latestEsCellStatus;
	}

	public void setLatestEsCellStatus(String latestEsCellStatus) {
		this.latestEsCellStatus = latestEsCellStatus;
	}

	public String getLatestMouseStatus() {
		return latestMouseStatus;
	}

	public void setLatestMouseStatus(String latestMouseStatus) {
		this.latestMouseStatus = latestMouseStatus;
	}

	public String getLatestPhenotypeStatus() {
		return latestPhenotypeStatus;
	}

	public void setLatestPhenotypeStatus(String latestPhenotypeStatus) {
		this.latestPhenotypeStatus = latestPhenotypeStatus;
	}

	
	
	
}
