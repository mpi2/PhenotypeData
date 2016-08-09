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

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.AlleleDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class AlleleService implements WebStatus{

	private static final Logger logger = LoggerFactory.getLogger(AlleleService.class);

	@Autowired @Qualifier("alleleCore")
	private HttpSolrClient solr;

	public AlleleService() {}

	/**
	 *
	 * @param geneIds the input set of gene ids
	 * @param statusField the status field
	 * @return Number of genes (from the provided list) in each status of interest.
	 */
	public HashMap<String, Long> getStatusCount(Set<String> geneIds, String statusField) {

		HashMap<String, Long> res = new HashMap<>();
		SolrQuery solrQuery = new SolrQuery();
		QueryResponse solrResponse;

		if (geneIds != null){
			String geneQuery = AlleleDTO.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneIds, " OR ").replace(":", "\\:") + ")";
			solrQuery.setQuery(geneQuery);
		}else {
			solrQuery.setQuery("*:*");
		}
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		try {
			solrQuery.addFacetField(statusField);
			solrResponse = solr.query(solrQuery);
			for (Count c : solrResponse.getFacetField(statusField).getValues()) {
				res.put(c.getName(), c.getCount());
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}

		return res;
	}

	public TreeMap<String, Long> getStatusCountByPhenotypingCenter(String center, String statusField) {

		TreeMap<String, Long> res = new TreeMap<>(new PhenotypingStatusComparator());
		SolrQuery solrQuery = new SolrQuery();
		QueryResponse solrResponse;

		if (center != null){
			String geneQuery = AlleleDTO.PHENOTYPING_CENTRE + ":\"" + center + "\"";
			solrQuery.setQuery(geneQuery);
		}else {
			solrQuery.setQuery("*:*");
		}

		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		try {
			solrQuery.addFacetField(statusField);
			solrResponse = solr.query(solrQuery);
			for (Count c : solrResponse.getFacetField(statusField).getValues()) {
				// We don't want to show everything
				if (!(c.getName().equalsIgnoreCase("Cre Excision Started") || c.getName().equals(""))){
					res.put(c.getName(), c.getCount());
				}
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public TreeMap<String, Long> getStatusCountByProductionCenter(String center, String statusField) {

		TreeMap<String, Long> res = new TreeMap<>(new ProductionStatusComparator());
		SolrQuery solrQuery = new SolrQuery();
		QueryResponse solrResponse;

		if (center != null){
			String geneQuery = AlleleDTO.LATEST_PRODUCTION_CENTRE + ":\"" + center + "\"";
			solrQuery.setQuery(geneQuery);
		}else {
			solrQuery.setQuery("*:*");
		}

		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		try {
			solrQuery.addFacetField(statusField);
			solrResponse = solr.query(solrQuery);
			for (Count c : solrResponse.getFacetField(statusField).getValues()) {
				// We don't want to show everything
				if (!c.getName().equals("")){
					res.put(c.getName(), c.getCount());
				}
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return res;
	}

	public Set<String> getFacets(String field){
		SolrQuery solrQuery = new SolrQuery();
		QueryResponse solrResponse;
		Set<String> res = new HashSet<>();
		solrQuery.setQuery("*:*");
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		solrQuery.addFacetField(field);

		logger.info(this.getClass().getEnclosingMethod() + "   " + solr.getBaseURL() + "/select?" + solrQuery);

		try {
			solrResponse = solr.query(solrQuery);
			for (Count c : solrResponse.getFacetField(field).getValues()) {
				res.add(c.getName());
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		return res;
	}


	public class PhenotypingStatusComparator implements Comparator<String> {

		Map<String, Integer> order = new HashMap<>(); //<string, desiredPosition>


		@Override
		public int compare(String o1, String o2) {
			order.put("Phenotype Attempt Registered", 1);
			order.put("Phenotyping Started", 2);
			order.put("Phenotyping Complete", 3);

			if (order.containsKey(o1) && order.containsKey(o2)){
				return order.get(o1).compareTo(order.get(o2));
			}

			return 0;
		}

	}

	public class ProductionStatusComparator  implements Comparator<String> {
		Map<String, Integer> order = new HashMap<>(); //<string, desiredPosition>

		@Override
		public int compare(String o1, String o2) {
			order.put("Micro-injection in progress", 1);
			order.put("Chimeras obtained", 2);
			order.put("Genotype confirmed", 3);
			order.put("Cre Excision Started", 4);
			order.put("Cre Excision Complete", 5);

			if (order.containsKey(o1) && order.containsKey(o2)){
				return order.get(o1).compareTo(order.get(o2));
			}

			return 0;
		}
	}

	@Override
	public long getWebStatus() throws SolrServerException, IOException {
		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + solr.getBaseURL() + "/select?" + query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}
	@Override
	public String getServiceName(){
		return "Allele Service";
	}


}
