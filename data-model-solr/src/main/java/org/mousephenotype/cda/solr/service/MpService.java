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
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.HpDTO;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.solr.web.dto.SimpleOntoTerm;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class MpService extends BasicService implements WebStatus{


	@Autowired
	@Qualifier("mpCore")
    private HttpSolrClient solr;

	public MpService() {
	}


	public List<MpDTO> getAllMpWithMaMapping()
	throws SolrServerException, IOException {

		SolrQuery q = new SolrQuery();
		q.setRows(Integer.MAX_VALUE);
		q.setQuery(MpDTO.INFERRED_MA_TERM_ID + ":*");

		q.addField(MpDTO.MP_ID);
		q.addField(MpDTO.INFERRED_MA_TERM_ID);

		return solr.query(q).getBeans(MpDTO.class);
	}

	/**
	 * Return the phenotype
	 *
	 * @return all genes from the gene core.
	 * @throws SolrServerException, IOException
	 */
	public MpDTO getPhenotype(String id) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery()
				.setQuery(MpDTO.MP_ID + ":\"" + id + "\" OR " + MpDTO.ALT_MP_ID + ":\"" + id + "\"") // this will find current mp id if alt mp id is used
				.setRows(1);

		System.out.println(solrQuery.toString());
		QueryResponse rsp = solr.query(solrQuery);
		List<MpDTO> mps = rsp.getBeans(MpDTO.class);

		if (rsp.getResults().getNumFound() > 0) {
			return mps.get(0);
		}

		return null;
	}

	/**
	 *
	 * @param ids MP ids to query for (only query on MP_ID field.
	 * @return List<MpDTO> corresponding to the passed ids.
	 * @throws SolrServerException, IOException
	 */
	public List<MpDTO> getPhenotypes(List<String> ids)
	throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery()
				.setQuery(MpDTO.MP_ID + ":\"" + StringUtils.join(ids, "\" OR " + MpDTO.MP_ID + ":\"") + "\"");

		QueryResponse rsp = solr.query(solrQuery);
		return rsp.getBeans(MpDTO.class);

	}


	/**
	 * @author ilinca
	 * @since 2016/03/22
	 * @param id
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public List<OntologyBean> getParents(String id) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery()
				.setQuery(MpDTO.MP_ID + ":\"" + id + "\"")
				.setRows(1);

		QueryResponse rsp = solr.query(solrQuery);
		List<MpDTO> mps = rsp.getBeans(MpDTO.class);
		List<OntologyBean> parents = new ArrayList<>();

		if (mps.size() > 1){
			throw new Error("More documents in MP core for the same MP id: " + id);
		}

		if ((mps.get(0).getParentMpId() == null || mps.get(0).getParentMpId().size() == 0)){
			if (mps.get(0).getTopLevelMpId() != null && mps.get(0).getTopLevelMpId().size() > 0){ // first level below top level
				for (int i = 0; i < mps.get(0).getTopLevelMpId().size(); i++){
					parents.add(new OntologyBean(mps.get(0).getTopLevelMpId().get(i),
							shortenLabel(mps.get(0).getTopLevelMpTerm().get(i))));
				}
			}
			return parents;
		}

		if (mps.get(0).getParentMpId().size() != mps.get(0).getParentMpTerm().size()){
			throw new Error("Length of parent id list and parent term list does not match for MP id: " + id);
		}

		for (int i = 0; i < mps.get(0).getParentMpId().size(); i++){
			parents.add(new OntologyBean(mps.get(0).getParentMpId().get(i),
					shortenLabel(mps.get(0).getParentMpTerm().get(i))));
		}

		return parents;
	}


	/**
	 * @author ilinca
	 * @since 2016/04/05
	 * @param mpTermId
	 * @return JSON in jstree format for ontology browser
	 * @throws SolrServerException, IOException
	 */
	public String getSearchTermJson(String mpTermId)
	throws SolrServerException, IOException{

		SolrQuery solrQuery = new SolrQuery()
				.setQuery(MpDTO.MP_ID + ":\"" + mpTermId + "\"")
				.setRows(1);
		solrQuery.addField(MpDTO.SEARCH_TERM_JSON);

		QueryResponse rsp = solr.query(solrQuery);
		List<MpDTO> mps = rsp.getBeans(MpDTO.class);

		return (mps != null) ? mps.get(0).getSearchTermJson() : "";
	}


	/**
	 * @author ilinca
	 * @since 2016/04/05
	 * @param mpTermId
	 * @return JSON in jstree format for ontology browser
	 * @throws SolrServerException, IOException
	 */
	public String getChildrenJson(String nodeId, String termId)
	throws SolrServerException, IOException{

		SolrQuery solrQuery = new SolrQuery()
				.setQuery(MpDTO.MP_NODE_ID + ":" + nodeId)
				.setRows(1);
		solrQuery.addField(MpDTO.CHILDREN_JSON);

		QueryResponse rsp = solr.query(solrQuery);
		List<MpDTO> mps = rsp.getBeans(MpDTO.class);

		return (mps != null) ? mps.get(0).getChildrenJson() : "";
	}

	/**
	 * @author ilinca
	 * @since 2016/03/22
	 * @param id
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public List<OntologyBean> getChildren(String id) throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery()
				.setQuery(MpDTO.MP_ID + ":\"" + id + "\"")
				.setRows(1);

		QueryResponse rsp = solr.query(solrQuery);
		List<MpDTO> mps = rsp.getBeans(MpDTO.class);
		List<OntologyBean> children = new ArrayList<>();

		if (mps.size() > 1){
			throw new Error("More documents in MP core for the same MP id: " + id);
		}

		if (mps.get(0).getChildMpTerm() == null || mps.get(0).getChildMpTerm().size() == 0){
			return children;
		}

		if (mps.get(0).getChildMpTerm().size() != mps.get(0).getChildMpId().size()){
			throw new Error("Length of children id list and children term list does not match for MP id: " + id);
		}

		for (int i = 0; i < mps.get(0).getChildMpId().size(); i++){
			children.add(new OntologyBean(mps.get(0).getChildMpId().get(i),
					shortenLabel(mps.get(0).getChildMpTerm().get(i))));
		}

		return children;
	}


    /**
     * Return all phenotypes from the mp core.
     *
     * @return all genes from the gene core.
     * @throws SolrServerException, IOException
     */
    public Set<String> getAllPhenotypes() throws SolrServerException, IOException {

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

    public Set<BasicBean> getAllTopLevelPhenotypesAsBasicBeans() throws SolrServerException, IOException{

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.addFacetField("top_level_mp_term_id");
		solrQuery.setRows(0);
		QueryResponse rsp = solr.query(solrQuery);
		System.out.println("solr query in basicbean="+solrQuery);

		HashSet<BasicBean> allTopLevelPhenotypes = new LinkedHashSet<BasicBean>();
		for (FacetField ff:rsp.getFacetFields()){
			for(Count count: ff.getValues()){
				String mpArray[]=count.getName().split("__");
				BasicBean bean=new BasicBean();
				bean.setName(mpArray[0]);
				bean.setId(mpArray[1]);
				allTopLevelPhenotypes.add(bean);
			}

		}
		return allTopLevelPhenotypes;
	}

    public ArrayList<String> getChildrenFor(String mpId) throws SolrServerException, IOException{

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(MpDTO.MP_ID + ":\"" + mpId + "\"");
		solrQuery.setFields(MpDTO.CHILD_MP_ID);
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
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
		List<String> hpIds = getListFromJson(doc.getJSONArray(HpDTO.HP_ID));
		List<String> hpTerms = getListFromJson(doc.getJSONArray(HpDTO.HP_TERM));
		Map<String, SimpleOntoTerm> computationalHPTerms = new HashMap<>();

		for ( int i=0; i< hpIds.size(); i++  ){
			SimpleOntoTerm term = new SimpleOntoTerm();
			term.setTermId(hpIds.get(i));
			term.setTermName(hpTerms.get(i));

			computationalHPTerms.put(hpIds.get(i), term);
		}

		return new HashSet<SimpleOntoTerm>(computationalHPTerms.values());

	}

	@Override
	public long getWebStatus()
	throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();
		query.setQuery("*:*").setRows(0);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}
	@Override
	public String getServiceName(){
		return "MP Service";
	}
}
