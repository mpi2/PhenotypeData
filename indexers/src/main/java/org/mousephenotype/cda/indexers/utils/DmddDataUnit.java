package org.mousephenotype.cda.indexers.utils;

public class DmddDataUnit {
	String geneAccession;
	String url;
	public DmddDataUnit(String geneAccession, String url) {
		this.geneAccession=geneAccession;
		this.url=url;
	}
	public String getGeneAccession() {
		return geneAccession;
	}
	public void setGeneAccession(String geneAccession) {
		this.geneAccession = geneAccession;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
