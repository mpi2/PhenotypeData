package org.mousephenotype.cda.solr.service;


import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

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

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public JSONObject toJson() throws JSONException {
		return new JSONObject().put("name", name).put("id", id);
	}
}