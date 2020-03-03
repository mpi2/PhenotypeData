/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/
package org.mousephenotype.cda.solr.service.dto;

import java.util.Comparator;
import java.util.List;

/**
 * @sicne 2015/09/22
 * @author tudose
 * Class to be used for gene or allele objects, instead of the DAO one. 
 *
 */
public class MarkerBean {
	
	String accessionId;
	String symbol;
	String name;
	List<String> synonyms;
	String superScript;

	public MarkerBean () {}

	public MarkerBean(String accessionId, String symbol) {
		this.accessionId = accessionId;
		this.symbol = symbol;
	}

	public String getSuperScript() {
		return superScript;
	}
	public void setSuperScript(String superScript) {
		this.superScript = superScript;
	}
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessionId == null) ? 0 : accessionId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
		result = prime * result + ((synonyms == null) ? 0 : synonyms.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarkerBean other = (MarkerBean) obj;
		if (accessionId == null) {
			if (other.accessionId != null)
				return false;
		} else if (!accessionId.equals(other.accessionId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (symbol == null) {
			if (other.symbol != null)
				return false;
		} else if (!symbol.equals(other.symbol))
			return false;
		if (synonyms == null) {
			if (other.synonyms != null)
				return false;
		} else if (!synonyms.equals(other.synonyms))
			return false;
		return true;
	}
	
	public static Comparator<MarkerBean> getComparatorBySymbol(){
		Comparator<MarkerBean> comp = new Comparator<MarkerBean>(){
		    @Override
		    public int compare(MarkerBean a, MarkerBean b)
		    {
		    	return a.getSymbol().compareTo(b.getSymbol());
		    }
		};
		return comp;
	}
}
