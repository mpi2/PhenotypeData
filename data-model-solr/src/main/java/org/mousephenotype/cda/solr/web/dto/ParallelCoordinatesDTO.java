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

package org.mousephenotype.cda.solr.web.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.mousephenotype.cda.db.pojo.Parameter;

import edu.emory.mathcs.backport.java.util.Collections;
import net.sf.json.JSONObject;


public class ParallelCoordinatesDTO {

	public static final String DEFAULT = "default value";
	public static final String GROUP_WT = "WT";
	public static final String GROUP_MUTANT = "Mutant";
	

	String geneSymbol;
	String geneAccession;
	HashMap<String, MeanBean> means;
	ArrayList<Parameter> allColumns;
	String group;
	
	public ParallelCoordinatesDTO(String geneSymbol, String geneAccession, String group, ArrayList<Parameter> allColumns){
		this.geneAccession = geneAccession;
		this.geneSymbol = geneSymbol;
		this.group = group;
		means = new HashMap<>();
		this.allColumns = allColumns;
		for (Parameter column: allColumns){
			means.put(column.getName(), new MeanBean( null, column.getStableId(), column.getName(), column.getStableKey(), null));
		}
	}
	
	public void addMean( String unit, String parameterStableId,
	String parameterName, Integer parameterStableKey, Double mean){
		means.put(parameterName, new MeanBean( unit, parameterStableId, parameterName, parameterStableKey, mean));
	}
	
	public String toString(boolean onlyComplete){
		String res = "";
		
		if (onlyComplete && isComplete() || !onlyComplete){			
			res += "\"name\": \"" + geneSymbol + "\",";
			res += "\"group\": \"" + group + "\",";
			int i = 0; 
			
			
			if (this.means.values().size() > 0){
				
				ArrayList <MeanBean> values = new ArrayList<MeanBean>(this.means.values());
				Collections.sort(values, this.means.values().iterator().next().getComparatorByParameterName());				
				
				for (MeanBean mean : values){
					res += "\"" + mean.parameterName + "\": ";
					res += mean.mean;
					i++;
					if (i < this.means.size()){
						res +=", ";
					}
				}
			}
		}
		return res;
	}
	
	public boolean isComplete(){
		
		boolean complete = true;
		for (MeanBean row: means.values()){
			if (row.mean == null){
				complete = false;
				System.out.println(this.geneSymbol + " not complete");
				break;
			}
		}
		return complete;
	}	
	
	public JSONObject getJson(){
		JSONObject obj = new JSONObject();
		obj.accumulate("name", this.geneSymbol);
		obj.accumulate("group", "default gene group");
		for (MeanBean mean: this.means.values()){
			obj.accumulate(mean.parameterName, mean.mean);
		}
		return obj;
	}
	
	public HashMap<String, MeanBean> getMeans(){
		return means;
	}
	
	public class MeanBean{
		
		String unit;
		String parameterStableId;
		String parameterName;
		Integer parameterStableKey;
		Double mean;
		
		public MeanBean(String unit, String parameterStableId,
		String parameterName, Integer parameterStableKey, Double mean){
			this.unit = unit;
			this.parameterName = parameterName;
			this.parameterStableId = parameterStableId;
			this.parameterStableKey = parameterStableKey;
			this.mean = mean;
		}
		public Double getMean(){
			return mean;
		}
		
		public Comparator<MeanBean> getComparatorByParameterName()
		{   
			Comparator<MeanBean> comp = new Comparator<MeanBean>(){
		    @Override
		    public int compare(MeanBean s1, MeanBean s2)
		    {
		        return s1.parameterName.compareTo(s2.parameterName);
		    }        
			};
			return comp;
		}  
		
		public String getUnit() {
			return unit;
		}
		public void setUnit(String unit) {
			this.unit = unit;
		}
		public String getParameterStableId() {
			return parameterStableId;
		}
		public void setParameterStableId(String parameterStableId) {
			this.parameterStableId = parameterStableId;
		}
		public String getParameterName() {
			return parameterName;
		}
		public void setParameterName(String parameterName) {
			this.parameterName = parameterName;
		}
		public Integer getParameterStableKey() {
			return parameterStableKey;
		}
		public void setParameterStableKey(Integer parameterStableKey) {
			this.parameterStableKey = parameterStableKey;
		}
		public void setMean(Double mean) {
			this.mean = mean;
		}
	}
}
