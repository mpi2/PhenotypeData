package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.PhenodigmDTO;
import org.mousephenotype.cda.web.WebStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PhenodigmService implements WebStatus{

	private final Logger logger = LoggerFactory.getLogger(PhenodigmService.class);

	@Autowired
	@Qualifier("phenodigmCore")
	private SolrClient solr;

	/**
	 * Get the number of unique disease associations from the phenodigm core
	 *
	 * @return a count of the number of disease associations or 0 if there was an error and a log entry will be made
	 */
	public Integer getDiseaseAssociationCount() {

		Integer nGroups = null;

		try {
			SolrQuery q = new SolrQuery();

			q.setQuery("*:*")
					.setRows(0)
					.addFilterQuery(PhenodigmDTO.TYPE + ":disease_model_association")

					.set("group", true)
					.set("group.ngroups", true)
					.set("group.field", PhenodigmDTO.MODEL_ID);

			nGroups = solr.query(q).getGroupResponse().getValues().get(0).getNGroups();
		} catch (IOException | SolrServerException | HttpSolrClient.RemoteSolrException e) {
			logger.error("\n\nERROR getDiseaseAssociationCount: Could not get disease association count\n\n", e);
		}

		return nGroups;

	}

	@Override
	public long getWebStatus() throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(solr) + "/select?" + query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}
	
	@Override
	public String getServiceName(){
		return "Phenodigm Service";
	}

}
