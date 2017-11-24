package org.mousephenotype.cda.solr.service;

import net.sf.json.JSONObject;

public class OntologyBean{


	String name;
	String id;
	String description;
	
	
	@Override
	public String toString() {
		return name + "["+id+"]";
	}
	public OntologyBean(String id, String name){
		this.id=id;
		this.name=name;
	}
	
	public OntologyBean(String id, String name, String description) {
		this(id, name);
		this.description=description;
	}

	public OntologyBean() {

	}



	public String getId() {
		return id;
	}
	public void setId(String maId) {
		this.id = maId;
	}
	public String getName() {
		return name;
	}
	public void setName(String maName) {
		this.name = maName;
	}
	
	//used for Text next to PATO terms in the xml
//	String termTextValue;
//	public String getTermTextValue() {
//		return termTextValue;
//	}
//	public void setTermTextValue(String termTextValue) {
//		this.termTextValue = termTextValue;
//	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public JSONObject toJson(){
		return new JSONObject().element("name", name).element("id", id);
	}
}