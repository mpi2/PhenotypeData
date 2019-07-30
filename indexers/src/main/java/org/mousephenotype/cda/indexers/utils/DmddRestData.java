package org.mousephenotype.cda.indexers.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DmddRestData {
	
	List<DmddDataUnit> imaged;
	List<DmddDataUnit> earlyLethal;
	public List<DmddDataUnit> getImaged() {
		return imaged;
	}
	public void setImaged(List<DmddDataUnit> imaged) {
		this.imaged = imaged;
	}
	public List<DmddDataUnit> getEarlyLethal() {
		return earlyLethal;
	}
	public void setEarlyLethal(List<DmddDataUnit> earlyLethal) {
		this.earlyLethal = earlyLethal;
	}
	

}
