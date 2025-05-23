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

import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ImageServiceUtil {
	
	 static final Comparator<SolrDocument> PARAMETER_NAME_ORDER = 
             new Comparator<SolrDocument>() {
		 public int compare(SolrDocument o1, SolrDocument o2) {
								List<String>paramAssName=(List<String>) o1.get(ImageDTO.PARAMETER_ASSOCIATION_NAME);
								List<String>paramAssName2=(List<String>) o2.get(ImageDTO.PARAMETER_ASSOCIATION_NAME);
								//only compare the first association most only have one
				if(paramAssName!=null && paramAssName2!=null){
								return paramAssName.get(0).compareTo(paramAssName2.get(0));
				}else if(paramAssName==null && paramAssName2!=null){
					return -1;
				}else if(paramAssName!=null && paramAssName2==null){
					return 1;
				}else if(paramAssName==null && paramAssName2==null){
					return 0;
				}
				return 0;//should never get this far???
			}
	 };
	 
	 static final Comparator<Count> COUNT_COMPARE_APHABETICALLY = 
             new Comparator<Count>() {
		 public int compare(Count o1, Count o2) {
								String paramAssName=o1.getName();
								String paramAssName2=o2.getName();
				if(paramAssName!=null && paramAssName2!=null){
								return paramAssName.compareTo(paramAssName2);
				}
				System.out.println("returning 0");
				return 0;//should never get this far???
			}
	 };
	
	/**
	 * Within the images we get for top level ma terms we want them sorted by the organ the expression is in. so group by alphabetical e.g. all testis go together
	 * @param expFacetToDocs
	 */
	public static Map<String, SolrDocumentList> sortDocsByExpressionAlphabetically(Map<String, SolrDocumentList> expFacetToDocs) {
		for(String key: expFacetToDocs.keySet()){
			SolrDocumentList docs = expFacetToDocs.get(key);
			Collections.sort(docs, PARAMETER_NAME_ORDER);	
		}
		return expFacetToDocs;
	}
	
	public static List<Count> sortHigherLevelTermCountsAlphabetically(List<Count> higherLevelMaTerms) {
		
			Collections.sort(higherLevelMaTerms, COUNT_COMPARE_APHABETICALLY);	
		
		return higherLevelMaTerms;
	}

}
