package org.mousephenotype.cda.solr.service.dto;

import java.util.List;

public class MarkerBean {
	
	String accessionId;
	String symbol;
	String name;
	List<String> synonyms;
	public String getAccessionId() {
		return accessionId;
	}
	public void setAccessionId(String accessionId) {
		this.accessionId = accessionId;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(List<String> synonyms) {
		this.synonyms = synonyms;
	}
	
}
