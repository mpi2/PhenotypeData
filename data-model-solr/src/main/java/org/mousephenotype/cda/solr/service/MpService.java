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

import net.sf.json.JSONObject;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.HpDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.web.dto.SimpleOntoTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MpService extends BasicService{

	private final Logger log = LoggerFactory.getLogger(this.getClass().getCanonicalName());

	@Autowired
	@Qualifier("mpCore")
    private HttpSolrServer solr;

	@Autowired
	@Qualifier("phenotypePipelineDAOImpl")
	private PhenotypePipelineDAO pipelineDao;

	public MpService() {
	}



	/**
	 * Return the phenotype
	 *
	 * @return all genes from the gene core.
	 * @throws SolrServerException
	 */
	public MpDTO getPhenotypes(String id) throws SolrServerException {

		SolrQuery solrQuery = new SolrQuery()
			.setQuery(MpDTO.MP_ID + ":\"" + id + "\"")
			.setRows(1);

		QueryResponse rsp = solr.query(solrQuery);
		List<MpDTO> mps = rsp.getBeans(MpDTO.class);

		if (rsp.getResults().getNumFound() > 0) {
			return mps.get(0);
		}

		return null;
	}

    /**
     * Return all phenotypes from the mp core.
     *
     * @return all genes from the gene core.
     * @throws SolrServerException
     */
    public Set<String> getAllPhenotypes() throws SolrServerException {

        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQuery(MpDTO.MP_ID + ":*");
        solrQuery.setFields(MpDTO.MP_ID);
        solrQuery.setRows(1000000);
        QueryResponse rsp;
        rsp = solr.query(solrQuery);
        List<MpDTO> mps = rsp.getBeans(MpDTO.class);
        Set<String> allPhenotypes = new HashSet<String>();

        for (MpDTO mp : mps) {
            allPhenotypes.add(mp.getMpId());
        }

        return allPhenotypes;
    }

    public Set<BasicBean> getAllTopLevelPhenotypesAsBasicBeans() throws SolrServerException{

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.addFacetField("top_level_mp_term_id");
		solrQuery.setRows(0);
		QueryResponse rsp = solr.query(solrQuery);
		System.out.println("solr query in basicbean="+solrQuery);

		HashSet<BasicBean> allTopLevelPhenotypes = new LinkedHashSet<BasicBean>();
		for (FacetField ff:rsp.getFacetFields()){
			for(Count count: ff.getValues()){
				String mpArray[]=count.getName().split("___");
				BasicBean bean=new BasicBean();
				bean.setName(mpArray[0]);
				bean.setId(mpArray[1]);
				allTopLevelPhenotypes.add(bean);
			}

		}
		return allTopLevelPhenotypes;
	}

    public ArrayList<String> getChildrenFor(String mpId) throws SolrServerException{

    	SolrQuery solrQuery = new SolrQuery();
    	solrQuery.setQuery(MpDTO.MP_ID + ":\"" + mpId + "\"");
    	solrQuery.setFields(MpDTO.CHILD_MP_ID);
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();

//		System.out.println("Solr URL to getChildrenFor: " + solr.getBaseURL() + "/select?" + solrQuery);
		ArrayList<String> children = new ArrayList<String>();

        for (SolrDocument doc : res) {
        	if (doc.containsKey(MpDTO.CHILD_MP_ID)){
        		for (Object child: doc.getFieldValues(MpDTO.CHILD_MP_ID)){
        			children.add((String)child);
        		}
        	}
        }
        return children;
    }

    // get computationally mapped HP terms of MP from Solr json doc of an MP
    public Set<SimpleOntoTerm> getComputationalHPTerms(JSONObject doc){
    	// this mapping is computational
    	List<String> hpIds = getList(doc.getJSONArray(HpDTO.HP_ID));
    	List<String> hpTerms = getList(doc.getJSONArray(HpDTO.HP_TERM));

    	Set<SimpleOntoTerm> computationalHPTerms = new HashSet<SimpleOntoTerm>();

    	for ( int i=0; i< hpIds.size(); i++  ){
    		SimpleOntoTerm term = new SimpleOntoTerm();
    		term.setTermId(hpIds.get(i));
    		term.setTermName(hpTerms.get(i));
    		computationalHPTerms.add(term);
		}

    	return computationalHPTerms;
    }

	/**
	 *
	 * @param mpTermId
	 * @return List of all parameters that may lead to associations to the MP
	 * term or any of it's children (based on the slim only)
	 */
	public HashSet<String> getParameterStableIdsByPhenotypeAndChildren(String mpTermId) {
		HashSet<String> res = new HashSet<>();
		ArrayList<String> mpIds;
		try {
			mpIds = getChildrenFor(mpTermId);
			res.addAll(pipelineDao.getParameterStableIdsByPhenotypeTerm(mpTermId));
			for (String mp : mpIds) {
				res.addAll(pipelineDao.getParameterStableIdsByPhenotypeTerm(mp));
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		return res;
	}
}
