package org.mousephenotype.cda.solr.service;

public class OntologyBean{

	@Override
	public String toString() {
		return "OntologyBean [id=" + id + ", name=" + name + "]";
	}
	public OntologyBean(String id, String name){
		this.id=id;
		this.name=name;
	}

	public OntologyBean() {

	}

	String id;
	public String getId() {
		return id;
	}
	public void setId(String maId) {
		this.id = maId;
	}
	String name;
	public String getName() {
		return name;
	}
	public void setName(String maName) {
		this.name = maName;
	}
	
	//used for Text next to PATO terms in the xml
	String termTextValue;
	public String getTermTextValue() {
		return termTextValue;
	}
	public void setTermTextValue(String termTextValue) {
		this.termTextValue = termTextValue;
	}

}