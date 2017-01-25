package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.web.WebStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class Allele2Service implements WebStatus{
	
	@Autowired @Qualifier("allele2Core")
	private SolrClient solr;

	@Override
	public long getWebStatus() throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		query.setQuery("*:*").setRows(0);

		//System.out.println("SOLR URL WAS " + SolrUtils.getBaseURL(solr) + "/select?" + query);

		QueryResponse response = solr.query(query);
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

		return solr.query(query).getBeans(Allele2DTO.class);

	}

	@Override
	public String getServiceName(){
		return "Allele2 Product Service";
	}

}
