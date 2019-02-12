package org.mousephenotype.cda.solr.service;

import java.io.IOException;

import javax.validation.constraints.NotNull;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.MpDTO;
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
public class SearchPhenotypeService {

	@Autowired
	@Qualifier("mpCore")
	private SolrClient solr;

    @NotNull
    @Value("${base_url}")
    private String baseUrl;
    
    /**
	 * Return all genes from the gene core.
     * @param rows 
     * @param start 
	 *
	 * @return all genes from the gene core.
	 * @throws SolrServerException, IOException
	 */
	public QueryResponse searchPhenotypes(String keywords, Integer start, Integer rows) throws SolrServerException, IOException {
		//current query used by BZ is just taken from old one which has DisMax and boost in the URL (boosts could be in solr config as defaults??)
//https://wwwdev.ebi.ac.uk/mi/impc/dev/solr/mp/select?facet.field=top_level_mp_term_inclusive&fl=mp_id,mp_term,mixSynQf,mp_definition&fq=+*:*&rows=10&bq=mp_term:("abnormal")^1000+mp_term_synonym:("abnormal")^500+mp_definition:("abnormal")^100&q="abnormal"&facet.limit=-1&defType=edismax&qf=mixSynQf&wt=json&facet=on&facet.sort=index&indent=true&start=0
		final SolrQuery query = new SolrQuery("\""+keywords+"\"");
		query.add("defType", "edismax");
		query.setFields(MpDTO.MP_ID, MpDTO.MP_TERM, MpDTO.MP_TERM_SYNONYM, MpDTO.MP_DEFINITION);
		//boost looks like this bq=mp_term:("abnormal")^1000+mp_term_synonym:("abnormal")^500+mp_definition:("abnormal")^100&
		query.add("bq", "mp_term:(\""+keywords+"\")^1000");
		query.add("bq", "mp_term_synonym:(\""+keywords+"\")^500");
		query.add("bq", "mp_definition:(\""+keywords+"\")^100");
		query.add("qf","mixSynQf");
		
		query.setStart(start);
		query.setRows(rows);
		
		//query.setRows(Integer.MAX_VALUE);
System.out.println("phenotype search query="+query);
		final QueryResponse response = solr.query( query);
		return response;
		
	}
    
    
}
