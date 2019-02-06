package org.mousephenotype.cda.solr.service;

import java.io.IOException;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.GeneResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Search for genes in the gene core and return minimal information needed for search results
 * @author jwarren
 *
 */
		
@Service
public class SearchGeneService {

	@Autowired
	@Qualifier("geneCore")
	private SolrClient solr;

    @NotNull
    @Value("${base_url}")
    private String baseUrl;
    
    /**
	 * Return all genes from the gene core.
	 *
	 * @return all genes from the gene core.
	 * @throws SolrServerException, IOException
	 */
	public QueryResponse searchGenes(String keywords) throws SolrServerException, IOException {

//		SolrQuery solrQuery = new SolrQuery();
//		solrQuery.setQuery("*:*");
//		solrQuery.setRows(Integer.MAX_VALUE);
		final SolrQuery query = new SolrQuery("*:*");
		//query.addField("id");
		//query.addField("name");

		final QueryResponse response = solr.query( query);
		return response;
		
	}
    
    
}
