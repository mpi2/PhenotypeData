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
import org.mousephenotype.cda.solr.service.dto.AnatomyDTO;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class AnatomyService extends BasicService implements WebStatus {

	@Autowired
	@Qualifier("anatomyCore")
	private HttpSolrClient solr;

	public AnatomyService() {
	}

	public AnatomyService(String anatomyServiceUrl) {
		this.solr = new HttpSolrClient(anatomyServiceUrl);
	}

	/**
	 * Return an MA term
	 *
	 * @return single anatomy term from the anatomy core.
	 * @throws SolrServerException, IOException
	 */
	public AnatomyDTO getTerm(String id) throws SolrServerException, IOException  {

		SolrQuery solrQuery = new SolrQuery().setQuery(AnatomyDTO.ANATOMY_ID + ":\"" + id + "\"").setRows(1);

		QueryResponse rsp = solr.query(solrQuery);
		List<AnatomyDTO> anas = rsp.getBeans(AnatomyDTO.class);

		if (rsp.getResults().getNumFound() > 0) {
			return anas.get(0);
		}

		return null;
	}

	/**
	 * Return all anatomy terms from the anatomy core.
	 *
	 * @return all anas from the anatomy core.
	 * @throws SolrServerException, IOException
	 */
	public List<AnatomyDTO> getAllMaTerms() throws SolrServerException, IOException  {

		//System.out.println("SOLR: " + solr.getBaseURL());
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(AnatomyDTO.ANATOMY_ID + ":*");
		solrQuery.setRows(1000000);
		QueryResponse rsp;
		rsp = solr.query(solrQuery);
		List<AnatomyDTO> anas = rsp.getBeans(AnatomyDTO.class);

		return anas;
	}


	/**
	 * @author ilinca
	 * @since 2016/05/03
	 * @param id
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public List<OntologyBean> getParents(String id) 
	throws SolrServerException, IOException  {

		SolrQuery solrQuery = new SolrQuery()
				.setQuery(AnatomyDTO.ANATOMY_ID + ":\"" + id + "\"")
				.setRows(1);

		QueryResponse rsp = solr.query(solrQuery);
		List<AnatomyDTO> mps = rsp.getBeans(AnatomyDTO.class);
		List<OntologyBean> parents = new ArrayList<>();

		if (mps.size() > 1){
			throw new Error("More documents in anatomy core for the same anatomy id: " + id);
		}

		if ((mps.get(0).getParentAnatomyId() == null || mps.get(0).getParentAnatomyId().size() == 0)){
			if (mps.get(0).getTopLevelMpId() != null && mps.get(0).getTopLevelMpId().size() > 0){ // first level below top level
				for (int i = 0; i < mps.get(0).getTopLevelMpId().size(); i++){
					parents.add(new OntologyBean(mps.get(0).getTopLevelMpId().get(i), mps.get(0).getTopLevelMpTerm().get(i)));
				}
			}
			return parents;
		}

		if (mps.get(0).getParentAnatomyId().size() != mps.get(0).getParentAnatomyTerm().size()){
			throw new Error("Length of parent id list and parent term list does not match for anatomy id: " + id);
		}

		for (int i = 0; i < mps.get(0).getParentAnatomyId().size(); i++){
			parents.add(new OntologyBean(mps.get(0).getParentAnatomyId().get(i),	mps.get(0).getParentAnatomyTerm().get(i)));
		}

		return parents;
	}



	/**
	 * @author ilinca
	 * @since 2016/05/03
	 * @param id
	 * @return
	 * @throws SolrServerException, IOException
	 */
	public List<OntologyBean> getChildren(String id) 
	throws SolrServerException, IOException  {

		SolrQuery solrQuery = new SolrQuery()
				.setQuery(AnatomyDTO.ANATOMY_ID + ":\"" + id + "\"")
				.setRows(1);

		QueryResponse rsp = solr.query(solrQuery);
		List<AnatomyDTO> mps = rsp.getBeans(AnatomyDTO.class);
		List<OntologyBean> children = new ArrayList<>();

		if (mps.size() > 1){
			throw new Error("More documents in anatomy core for the same anatomy id: " + id);
		}

		if (mps.get(0).getChildAnatomyId() == null || mps.get(0).getChildAnatomyId().size() == 0){
			return children;
		}

		if (mps.get(0).getChildAnatomyTerm().size() != mps.get(0).getChildAnatomyId().size()){
			throw new Error("Length of children id list and children term list does not match for anatomy id: " + id);
		}

		for (int i = 0; i < mps.get(0).getChildAnatomyId().size(); i++){
			children.add(new OntologyBean(mps.get(0).getChildAnatomyId().get(i), mps.get(0).getChildAnatomyTerm().get(i)));
		}

		return children;
	}
	
	
	public Set<BasicBean> getAllTopLevelPhenotypesAsBasicBeans() throws SolrServerException, IOException  {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.addFacetField("top_level_mp_term_id");
		solrQuery.setRows(0);
		QueryResponse rsp = solr.query(solrQuery);
		System.out.println("solr query in basicbean=" + solrQuery);

		HashSet<BasicBean> allTopLevelPhenotypes = new LinkedHashSet<BasicBean>();
		for (FacetField ff : rsp.getFacetFields()) {
			for (Count count : ff.getValues()) {
				String mpArray[] = count.getName().split("___");
				BasicBean bean = new BasicBean();
				bean.setName(mpArray[0]);
				bean.setId(mpArray[1]);
				allTopLevelPhenotypes.add(bean);
			}

		}
		return allTopLevelPhenotypes;
	}

	public ArrayList<String> getChildrenFor(String mpId) throws SolrServerException, IOException  {

		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(MpDTO.MP_ID + ":\"" + mpId + "\"");
		solrQuery.setFields(MpDTO.CHILD_MP_ID);
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();
		ArrayList<String> children = new ArrayList<String>();

		for (SolrDocument doc : res) {
			if (doc.containsKey(MpDTO.CHILD_MP_ID)) {
				for (Object child : doc.getFieldValues(MpDTO.CHILD_MP_ID)) {
					children.add((String) child);
				}
			}
		}
		return children;
	}

	public AnatomogramDataBean getUberonIdAndTopLevelMaTerm(AnatomogramDataBean bean) throws SolrServerException, IOException  {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(AnatomyDTO.ANATOMY_ID + ":\"" + bean.getMaId() + "\"");
		solrQuery.setFields(AnatomyDTO.UBERON_ID, AnatomyDTO.ALL_AE_MAPPED_UBERON_ID, AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_ID, AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM);
		QueryResponse rsp = solr.query(solrQuery);
		SolrDocumentList res = rsp.getResults();

		ArrayList<String> uberonIds = new ArrayList<String>();
		Set<String> mappedEfoIds = new HashSet<>();
		Set<String> mappedUberonIds = new HashSet<>();

		if (res.getNumFound() > 1) {
			System.err.println("Warning - more than 1 anatomy term found where we only expect one doc!");
		}

		for (SolrDocument doc : res) {
			if (doc.containsKey(AnatomyDTO.UBERON_ID)) {
				for (Object child : doc.getFieldValues(AnatomyDTO.UBERON_ID)) {
					mappedUberonIds.add((String) child);
				}
				bean.setMappedUberonIdsForAnatomogram(new ArrayList(mappedUberonIds));
			}
			if (doc.containsKey(AnatomyDTO.EFO_ID)) {
				for (Object child : doc.getFieldValues(AnatomyDTO.EFO_ID)) {
					mappedEfoIds.add((String) child);
				}
				bean.setMappedUberonIdsForAnatomogram(new ArrayList(mappedEfoIds));
			}

			if (doc.containsKey(AnatomyDTO.ALL_AE_MAPPED_UBERON_ID)) {
				for (Object child : doc.getFieldValues(AnatomyDTO.ALL_AE_MAPPED_UBERON_ID)) {
					uberonIds.add((String) child);
				}
				bean.setUberonIds( uberonIds);
			}

			if (doc.containsKey(AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_ID)) {
				List<String> selectedTopLevelAnas = (List<String>) doc.get(AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_ID);
				bean.addTopLevelMaIds(selectedTopLevelAnas);
			}
			if (doc.containsKey(AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM)) {
				List<String> selectedTopLevelMaTerms = (List<String>) doc.get(AnatomyDTO.SELECTED_TOP_LEVEL_ANATOMY_TERM);
				bean.addTopLevelMaNames(selectedTopLevelMaTerms);
			}

			if (doc.containsKey(AnatomyDTO.ALL_AE_MAPPED_EFO_ID)) {
				List<String> efoIds = (List<String>) doc.get(AnatomyDTO.ALL_AE_MAPPED_EFO_ID);
				bean.addEfoIds(efoIds);
			}


		}
		return bean;
	}

	@Override
	public long getWebStatus() throws SolrServerException, IOException  {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		// System.out.println("SOLR URL WAS " + solr.getBaseURL() + "/select?" +
		// query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}

	@Override
	public String getServiceName() {
		return "Anatomy Service";
	}


	public String getSearchTermJson(String anatomyTermId)
			throws SolrServerException, IOException {

		SolrQuery solrQuery = new SolrQuery()
				.setQuery(AnatomyDTO.ANATOMY_ID + ":\"" + anatomyTermId + "\"")
				.setRows(1);
		solrQuery.addField(AnatomyDTO.SEARCH_TERM_JSON);

		QueryResponse rsp = solr.query(solrQuery);

		List<AnatomyDTO> mas = rsp.getBeans(AnatomyDTO.class);

		return (mas != null) ? mas.get(0).getSearchTermJson() : "";
	}

	public String getChildrenJson(String nodeId, String termId)
			throws SolrServerException, IOException {

		// Node_id is unique in ontodb for each ontology.
		// But we are mixing ontologies here so need to use a combination of node_id and anotomy_id prefix for uniqueness
		String qStr = null;
		if ( termId.startsWith("MA:") ){
			qStr = "MA*";
		}
		else if ( termId.startsWith("EMAPA:") ){
			qStr = "EMAPA*";
		}
		SolrQuery solrQuery = new SolrQuery()
				.setQuery(AnatomyDTO.ANATOMY_NODE_ID + ":" + nodeId + " AND " + AnatomyDTO.ANATOMY_ID + ":" + qStr)
				.setRows(1);
		solrQuery.addField(AnatomyDTO.CHILDREN_JSON);
		QueryResponse rsp = solr.query(solrQuery);
		List<AnatomyDTO> mas = rsp.getBeans(AnatomyDTO.class);

		return (mas != null) ? mas.get(0).getChildrenJson() : "";
	}

}
