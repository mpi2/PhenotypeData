package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.web.dto.GeneTargetDetail;
import org.mousephenotype.cda.solr.web.dto.OrderTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

	
	@Autowired
	@Qualifier("allele2Core")
	private HttpSolrServer allele2Core;
	
	@Autowired
	@Qualifier("eucommCreProductsCore")
	private HttpSolrServer eucommProduct;
	
	@Autowired
	@Qualifier("eucommToolsProductCore")
	private HttpSolrServer productCore;
	
	
   
	
	public List<OrderTableRow> getOrderTableRows(String acc) {
		List<OrderTableRow> orderTableRows=new ArrayList<>();
		
		OrderTableRow row=new OrderTableRow();
		row.setAlleleName("Cpsf3tm1b(EUCOMM)Wtsi");
		row.setStrainOfOrigin("C57BL/6N");
		row.setAlleleType("Reporter-tagged deletion allele (post-Cre)");
		GeneTargetDetail detail=new GeneTargetDetail();
		detail.setLabel("Target Vector Map");
		detail.setLink("https://www.i-dcc.org/imits/targ_rep/alleles/4973/allele-image-cre?simple=true.jpg.jpg");
		List<GeneTargetDetail> geneTargetDetails=new ArrayList<>();
		geneTargetDetails.add(detail);
		row.setGeneTargetDetails(geneTargetDetails);
		orderTableRows.add(row);
		return orderTableRows;
	}
	
	public void getAlleleDocs(String geneAcc) throws SolrServerException{
		
		String q = "type:allele AND mgi_accession_id:\"" + geneAcc+ "\"";//&start=0&rows=100&hl=true&wt=json";
		SolrQuery query=new SolrQuery();
		query.setQuery(q);
		query.setRows(Integer.MAX_VALUE);
		System.out.println("query for allele2="+query);
		QueryResponse response = allele2Core.query(query);
		System.out.println("size of response="+response.getResults().size());
        

        
	}

}
