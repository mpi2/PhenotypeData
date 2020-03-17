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

/**
 * Class to act as Map value DTO for impress data
 * @author tudose
 */
public class ImpressBaseDTO {

	private Long   id;
	private Long   stableKey;
	private String stableId;
	private String name;


	public ImpressBaseDTO(){
		
	}
	
	public ImpressBaseDTO(Long id, Long stableKey, String stableId, String name){
	
		this.id = id;
		this.stableId = stableId;
		this.stableKey = stableKey;
		this.name = name;
	
	}
	
	public Long getId() {

		return id;
	}


	public void setId(Long id) {

		this.id = id;
	}

	/**
	 * This is the procedure stable key
	 * @return
	 */
	public Long getStableKey() {

		return stableKey;
	}


	public void setStableKey(Long stableKey) {

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
		Comparator<ImpressBaseDTO> comp = Comparator.comparing(ImpressBaseDTO::getName);
		return comp;
	}  
	
	/**
	 * @author tudose
	 * @since 2015/08/04
	 * @return Sort by stable id.
	 */
	public static Comparator<ImpressBaseDTO> getComparatorByStableId()	{   
		Comparator<ImpressBaseDTO> comp = Comparator.comparing(ImpressBaseDTO::getStableId);
		return comp;
	}  
	

	/**
	 * @author tudose
	 * @return sort by name but IMPC objects always first.
	 */
	public static Comparator<ImpressBaseDTO> getComparatorByNameImpcFirst()	{   
		Comparator<ImpressBaseDTO> comp = new Comparator<ImpressBaseDTO>(){
	    @Override
	    public int compare(ImpressBaseDTO param1, ImpressBaseDTO param2)
	    {
	    	if (isImpc(param1.getStableId()) && !isImpc(param2.getStableId())){
				return -1;
			}
			if (isImpc(param2.getStableId()) && !isImpc(param1.getStableId())){
				return 1;
			}
			return param1.getName().compareTo(param2.getName());
	    }
		private boolean isImpc(String param){
			return param.startsWith("IMPC");
		}
		
		};
		return comp;
	}  
	
	/**
	 * @author tudose
	 * @since 2015/09/23
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((stableId == null) ? 0 : stableId.hashCode());
		result = prime * result + ((stableKey == null) ? 0 : stableKey.hashCode());
		return result;
	}

}
