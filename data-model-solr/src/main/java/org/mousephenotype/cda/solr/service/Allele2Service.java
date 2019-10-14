package org.mousephenotype.cda.solr.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.SolrUtils;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;


@Service
public class Allele2Service implements WebStatus{
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private SolrClient allele2Core;


	@Inject
	public Allele2Service(@NotNull @Qualifier("allele2Core") SolrClient allele2Core) {
		this.allele2Core = allele2Core;
	}

	public Allele2Service() {

	}


	@Override
	public long getWebStatus() throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		QueryResponse response = allele2Core.query(query);
		return response.getResults().getNumFound();
	}

	public List<Allele2DTO> getAllDocuments(String type, String... fields) throws IOException, SolrServerException {

		SolrQuery query = new SolrQuery().setQuery("*:*");
		if (type != null){
			query.setFilterQueries(Allele2DTO.TYPE + ":" + type);
		}
		if (fields != null){
			query.setFields(fields);

		}
		query.setRows(Integer.MAX_VALUE);

		return allele2Core.query(query).getBeans(Allele2DTO.class);

	}

	@Override
	public String getServiceName(){
		return "Allele2 Product Service";
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
			String geneQuery = Allele2DTO.MGI_ACCESSION_ID + ":(" + StringUtils.join(geneIds, " OR ").replace(":", "\\:") + ")";
			solrQuery.setQuery(geneQuery);
		}else {
			solrQuery.setQuery("*:*");
		}
		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		try {
			solrQuery.addFacetField(statusField);
			solrResponse = allele2Core.query(solrQuery);
			for (Count c : solrResponse.getFacetField(statusField).getValues()) {
				res.put(c.getName(), c.getCount());
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

		logger.info(this.getClass().getEnclosingMethod() + "   " + SolrUtils.getBaseURL(allele2Core) + "/select?" + solrQuery);

		try {
			solrResponse = allele2Core.query(solrQuery);
			for (Count c : solrResponse.getFacetField(field).getValues()) {
				res.add(c.getName());
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
			String geneQuery = Allele2DTO.LATEST_PHENOTYPING_CENTRE + ":\"" + center + "\"";
			solrQuery.setQuery(geneQuery);
		}else {
			solrQuery.setQuery("*:*");
		}

		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		try {
			solrQuery.addFacetField(statusField);
			solrResponse = allele2Core.query(solrQuery);
			for (Count c : solrResponse.getFacetField(statusField).getValues()) {
				// We don't want to show everything
				if ( !(c.getName().equalsIgnoreCase("Cre Excision Started") || c.getName().equalsIgnoreCase("Phenotype Production Aborted") || c.getName().equalsIgnoreCase("Rederivation Complete") || c.getName().equals("")) ) {
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
			String geneQuery = Allele2DTO.LATEST_PRODUCTION_CENTRE + ":\"" + center + "\"";
			solrQuery.setQuery(geneQuery);
		}else {
			solrQuery.setQuery("*:*");
		}

		solrQuery.setRows(1);
		solrQuery.setFacet(true);
		solrQuery.setFacetLimit(-1);
		try {
			solrQuery.addFacetField(statusField);
			solrResponse = allele2Core.query(solrQuery);
			for (Count c : solrResponse.getFacetField(statusField).getValues()) {
				// We don't want to show everything
				if (!(c.getName().equalsIgnoreCase("Founder obtained") || c.getName().equals(""))){
					res.put(c.getName(), c.getCount());
				}
			}
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
		
		return res;
	}
}