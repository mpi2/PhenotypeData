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
package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.PivotField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.util.NamedList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.JSONArray;



public class BasicService {

	private static final Logger LOG = LoggerFactory.getLogger(BasicService.class);
    
	
	protected static final int MAX_NB_DOCS = 1000000;
	
    /**
     * Recursive method to fill a map with multiple combination of pivot fields.
     * Each pivot level can have multiple children. Hence, each level should
     * pass back to the caller a list of all possible combination
     * 
     * @param pivotLevel
     * @param map
     */
    protected List<Map<String, String>> getLeveledFacetPivotValue(PivotField pivotLevel, PivotField parentPivot, boolean keepCount) {

        List<Map<String, String>> results = new ArrayList<Map<String, String>>();

        List<PivotField> pivotResult = pivotLevel.getPivot();
        if (pivotResult != null) {
            for (int i = 0; i < pivotResult.size(); i++) {
                List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotResult.get(i), pivotLevel, keepCount);
                
                // add the parent pivot
                if (parentPivot != null) {
                    for (Map<String, String> map : lmap) {
                        map.put(parentPivot.getField(), parentPivot.getValue().toString());
                        if (keepCount)
                        	map.put(parentPivot.getField() + "_count", new Integer(parentPivot.getCount()).toString());
                    }
                }
                results.addAll(lmap);
            }
        } else {
            Map<String, String> map = new HashMap<String, String>();
            map.put(pivotLevel.getField(), pivotLevel.getValue().toString());
            if (keepCount)
            	map.put(pivotLevel.getField() + "_count", new Integer(pivotLevel.getCount()).toString());
            // add the parent pivot
            if (parentPivot != null) {
                map.put(parentPivot.getField(), parentPivot.getValue().toString());
                if (keepCount)
                	map.put(parentPivot.getField() + "_count", new Integer(parentPivot.getCount()).toString());
            }
            results.add(map);
        }
        //
        return results;
    }

	
    /**
     * @author ilinca
     * @param label
     * @return shortened labels for parent-child views on anatomy/phenotype pages, i.e. "abn." instead of "abnormal"
     */
	protected String shortenLabel(String label){

		String res = label;
		res = res.replaceAll("abnormal ", "abn. ");
		res = res.replaceAll("phenotype ", "phen. ");
		res = res.replaceAll("decreased ", "dec. ");
		res = res.replaceAll("increased ", "inc. ");
		res = res.replaceAll("abnormality ", "abn. ");
		res = res.replaceAll("abnormal$", "abn.");
		res = res.replaceAll("decreased$", "dec.");
		res = res.replaceAll("increased$", "inc.");
		res = res.replaceAll("phenotype$", "phen.");
		return res;
	}
    
    /**
     * Unwrap results from a facet pivot solr query and return the flattened
     * list of maps of results
     * 
     * @param response
     *            list of maps
     * @return
     */
    protected List<Map<String, String>> getFacetPivotResults(QueryResponse response, boolean keepCount) {
    	
        List<Map<String, String>> results = new LinkedList<Map<String, String>>();
        NamedList<List<PivotField>> facetPivot = response.getFacetPivot();

        if (facetPivot != null && facetPivot.size() > 0) {
            for (int i = 0; i < facetPivot.size(); i++) {

                String name = facetPivot.getName(i); // in this case only one of
                                                     // them
                LOG.debug("facetPivot name" + name);
                List<PivotField> pivotResult = facetPivot.get(name);

                // iterate on results
                for (int j = 0; j < pivotResult.size(); j++) {

                    // create a HashMap to store a new triplet of data

                    PivotField pivotLevel = pivotResult.get(j);
                    List<Map<String, String>> lmap = getLeveledFacetPivotValue(pivotLevel, null, keepCount);
                    results.addAll(lmap);
                }
            }
        }

        return results;
    }
       
    /**
     * Get results for 2 level pivot faceting only!
     * @param response
     * @param pivot 
     * @return Returns values for one facetPivot at a time. <pivot_value, <facet1, facet2 ... >>
     */
    protected Map<String, List<String>> getFacetPivotResults(QueryResponse response, String pivot) {
    	
    	Map<String, List<String>>res = new HashMap<String, List<String>>();
        List<PivotField> facetPivot = response.getFacetPivot().get(pivot);

        for( PivotField p : facetPivot){
			List<String> secondLevelFacets = new ArrayList<>();
			for (PivotField pf : p.getPivot()){
				secondLevelFacets.add(pf.getValue().toString());
			}
			res.put(p.getValue().toString(), new ArrayList<String>(secondLevelFacets));
		}

        return res;
    }
    
    /**
     * Java structure for simple facets in Solr. 
     *
     * @param response
     * @return HashMap with facets + counts: <facet_field, <field_value, count>>
     */
    protected HashMap<String, HashMap<String, Long>> getFacets(QueryResponse response){
    	HashMap<String, HashMap<String, Long>> res = new HashMap<>();
    	for (FacetField facet: response.getFacetFields()){
    		HashMap<String, Long> facetMap = new HashMap<>();
    		for (Count values : facet.getValues()){
    			facetMap.put(values.getName(), values.getCount());
    		}
    		res.put(facet.getName(), facetMap);
    	}    	
    	return res;
    }
    
    
    
	/**
	 * @author tudose
	 * @since 2015/08/03
	 * @param jsonArray
	 * @return List representation of the JSONArray using toString on individual objects
	 */
	public List<String> getListFromJson(JSONArray jsonArray){
		
		List<String> list = new ArrayList<>();
		if (jsonArray != null){
			for (Object obj : jsonArray){
				list.add(obj.toString());
			}
		}
		
		return list;
	}
	
	/**
	 * @author tudose
	 * @since 2015/08/03
	 * @param collection
	 * @return List representation of the collection using toString on individual objects.
	 */
	public List<String> getListFromCollection(Collection<Object> collection){
		
		List<String> list = new ArrayList<>();
		if (collection != null){
			for (Object obj : collection){
				if (obj != null){
                    list.add(obj.toString());
				}
			}
		}
		
		return list;
	}
    
   }
