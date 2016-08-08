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

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.EmapDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class EmapService extends BasicService{


	@Autowired
	@Qualifier("emapCore")
    private HttpSolrClient solr;

	public EmapService() {
	}

	/**
	 * Return an EMAP term
	 *
	 * @return single EMAP term from the emap core.
	 * @throws SolrServerException, IOException
	 */
	public EmapDTO getEmapTerm(String id) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery()
			.setQuery(EmapDTO.EMAP_ID + ":\"" + id + "\"")
			.setRows(1);

		QueryResponse rsp = solr.query(solrQuery);
		List<EmapDTO> emaps = rsp.getBeans(EmapDTO.class);

		if (rsp.getResults().getNumFound() > 0) {
			return emaps.get(0);
		}

		return null;
	}

    /**
     * Return all EMAP terms from the emap core.
     *
     * @return all EMAPs from the emap core.
     * @throws SolrServerException, IOException
     */
    public List<EmapDTO> getAllEmapTerms() throws SolrServerException, IOException {

    	System.out.println("SOLR: " + solr.getBaseURL());
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(EmapDTO.EMAP_ID + ":*");
       // solrQuery.setFields(MpDTO.MA_ID);
        solrQuery.setRows(1000000);
        QueryResponse rsp;
        rsp = solr.query(solrQuery);
        List<EmapDTO> emaps = rsp.getBeans(EmapDTO.class);

        return emaps;
    }

    public Set<BasicBean> getAllTopLevelEmapsAsBasicBeans() throws SolrServerException, IOException{

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.addFacetField("top_level_emap_term_id");
		solrQuery.setRows(0);
		QueryResponse rsp = solr.query(solrQuery);
		System.out.println("solr query in basicbean="+solrQuery);

		HashSet<BasicBean> allTopLevelEmaps = new LinkedHashSet<BasicBean>();
		for (FacetField ff:rsp.getFacetFields()){
			for(Count count: ff.getValues()){
				String emapArray[]=count.getName().split("___");
				BasicBean bean=new BasicBean();
				bean.setName(emapArray[0]);
				bean.setId(emapArray[1]);
				allTopLevelEmaps.add(bean);
			}

		}
		return allTopLevelEmaps;
	}

    public ArrayList<String> getChildrenFor(String emapId) throws SolrServerException, IOException{

    	SolrQuery solrQuery = new SolrQuery();
    	solrQuery.setQuery(EmapDTO.EMAP_ID + ":\"" + emapId + "\"");
    	solrQuery.setFields(EmapDTO.CHILD_EMAP_ID);
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();

//		System.out.println("Solr URL to getChildrenFor: " + solr.getBaseURL() + "/select?" + solrQuery);
		ArrayList<String> children = new ArrayList<String>();

        for (SolrDocument doc : res) {
        	if (doc.containsKey(EmapDTO.CHILD_EMAP_ID)){
        		for (Object child: doc.getFieldValues(EmapDTO.CHILD_EMAP_ID)){
        			children.add((String)child);
        		}
        	}
        }
        return children;
    }

    
	
}
