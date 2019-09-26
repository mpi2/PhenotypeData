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
public class AutoSuggestService implements WebStatus {

	private SolrClient autosuggestCore;

	@Inject
	public AutoSuggestService(SolrClient autosuggestCore) {
		this.autosuggestCore = autosuggestCore;
	}

	public AutoSuggestService() {

	}


	public long getWebStatus() throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(genotypePhenotypeCore) + "/select?" + query);

		QueryResponse response = autosuggestCore.query(query);
		return response.getResults().getNumFound();
	}
	
	public String getServiceName(){
		return "Autosuggest service";
	}
}