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

import org.mousephenotype.cda.solr.web.dto.ParallelCoordinatesDTO.MeanBean;

/**
 * Class to act as Map value DTO for impress data
 * @author tudose
 */
public class ImpressBaseDTO {
	
	private Integer id;
	private Integer stableKey;
	private String stableId;
	private String name;


	public ImpressBaseDTO(){
		
	}
	
	public ImpressBaseDTO(Integer id, Integer stableKey, String stableId, String name){
	
		this.id = id;
		this.stableId = stableId;
		this.stableKey = stableKey;
		this.name = name;
	
	}
	
	public Integer getId() {

		return id;
	}


	public void setId(Integer id) {

		this.id = id;
	}


	public Integer getStableKey() {

		return stableKey;
	}


	public void setStableKey(Integer stableKey) {

		this.stableKey = stableKey;
	}


	public String getStableId() {

		return stableId;
	}


	public void setStableId(String stableId) {

		this.stableId = stableId;
	}


	public String getName() {

		return name;
	}


	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String toString() {
		return "ImpressBaseDTO [id=" + id + ", stableKey=" + stableKey + ", stableId=" + stableId + ", name=" + name
				+ "]";
	}
	
	/**
	 * @author tudose
	 * @since 2015/08/04
	 * @return
	 */
	public static Comparator<ImpressBaseDTO> getComparatorByName()
	{   
		Comparator<ImpressBaseDTO> comp = new Comparator<ImpressBaseDTO>(){
	    @Override
	    public int compare(ImpressBaseDTO a, ImpressBaseDTO b)
	    {
	        return a.getName().compareTo(b.getName());
	    }        
		};
		return comp;
	}  
	
	/**
	 * @author tudose
	 * @since 2015/08/04
	 * @return
	 */
	public static Comparator<ImpressBaseDTO> getComparatorByStableId()
	{   
		Comparator<ImpressBaseDTO> comp = new Comparator<ImpressBaseDTO>(){
	    @Override
	    public int compare(ImpressBaseDTO a, ImpressBaseDTO b)
	    {
	        return a.getStableId().compareTo(b.getStableId());
	    }        
		};
		return comp;
	}  
	
	
}
