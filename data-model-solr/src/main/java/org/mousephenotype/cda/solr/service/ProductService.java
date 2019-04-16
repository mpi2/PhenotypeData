package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;

@Service
public class ProductService implements WebStatus{

	private SolrClient productCore;


	@Inject
	public ProductService(SolrClient productCore) {
		this.productCore = productCore;
	}

	public ProductService() {

	}


	@Override
	public long getWebStatus() throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(productCore) + "/select?" + query);

		QueryResponse response = productCore.query(query);
		return response.getResults().getNumFound();
	}
	
	@Override
	public String getServiceName() {

		return "Product Service";
	}
}