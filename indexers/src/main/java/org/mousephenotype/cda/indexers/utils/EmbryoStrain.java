package org.mousephenotype.cda.indexers.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//import lombok.Data;

//@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class  EmbryoStrain{
	@Override
	public String toString() {
		return "EmbryoStrain [centre=" + centre + ", mgi=" + mgi + ", url=" + url + ", name=" + name + "]";
	}
	String centre;
	public String getCentre() {
		return centre;
	}
	public void setCentre(String centre) {
		this.centre = centre;
	}
	public String getMgi() {
		return mgi;
	}
	public void setMgi(String mgi) {
		this.mgi = mgi;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	String mgi;
	String url;
	String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}