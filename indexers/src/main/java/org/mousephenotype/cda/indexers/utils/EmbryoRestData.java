package org.mousephenotype.cda.indexers.utils;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

//import lombok.Data;

//@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmbryoRestData {
	
	@Override
	public String toString() {
		return "EmbryoRestData [strains=" + strains + "]";
	}

	List<EmbryoStrain> strains;

	public void setStrains(List<EmbryoStrain> strains) {
		this.strains=strains;
		
	}

	public List<EmbryoStrain> getStrains() {
		return this.strains;
	}
	
	

}
