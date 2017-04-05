package org.mousephenotype.cda.indexers.utils;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//import lombok.Data;

//@Data
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
