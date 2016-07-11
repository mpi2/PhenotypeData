package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class EucommCreProductService implements WebStatus{

	@Autowired @Qualifier("eucommCreProductsCore")
	private HttpSolrServer solr;

	@Override
	public long getWebStatus() throws SolrServerException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + solr.getBaseURL() + "/select?" + query);

		QueryResponse response = solr.query(query);
		return response.getResults().getNumFound();
	}

	@Override
	public String getServiceName(){
		return "Eucomm cre product service";
	}

}
