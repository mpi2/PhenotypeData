package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.solr.service.dto.ProductDTO;
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
	
	public List<Allele2DTO> getAllele2DTOs(String geneAcc) throws SolrServerException{
		
		String q = "type:allele AND mgi_accession_id:\"" + geneAcc+ "\"";//&start=0&rows=100&hl=true&wt=json";
		SolrQuery query=new SolrQuery();
		query.setQuery(q);
		query.setRows(Integer.MAX_VALUE);
		System.out.println("query for products="+query);
		QueryResponse response = productCore.query(query);
		System.out.println("number found of allele2 docs="+response.getResults().getNumFound());
		List<Allele2DTO> allele2DTOs = response.getBeans(Allele2DTO.class);
		System.out.println("number of products is "+allele2DTOs.size());
		
        return allele2DTOs;

        
	}
	
public List<ProductDTO> getProducts(String geneAcc) throws SolrServerException{
		
		String q = "mgi_accession_id:\"" + geneAcc+ "\"";//&start=0&rows=100&hl=true&wt=json";
		SolrQuery query=new SolrQuery();
		query.setQuery(q);
		query.setRows(Integer.MAX_VALUE);
		System.out.println("query for allele2="+query);
		QueryResponse response = productCore.query(query);
		System.out.println("number found of allele2 docs="+response.getResults().getNumFound());
		List<ProductDTO> productDTOs = response.getBeans(ProductDTO.class);
		System.out.println("number of allele2DTOs is "+productDTOs.size());
		
        return productDTOs;

        
	}

}
