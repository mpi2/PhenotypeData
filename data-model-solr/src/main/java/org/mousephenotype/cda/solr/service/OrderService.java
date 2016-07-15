package org.mousephenotype.cda.solr.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.mousephenotype.cda.solr.service.dto.ProductDTO;
import org.mousephenotype.cda.solr.web.dto.LinkDetails;
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
	
	
   
	
	public List<OrderTableRow> getOrderTableRows(String acc) throws SolrServerException {
		List<OrderTableRow> orderTableRows=new ArrayList<>();
		
		List<Allele2DTO> allele2DTOs = this.getAllele2DTOs(acc);
		Map<String,List<ProductDTO>> alleleNameToProductsMap=this.getProducts(acc);
		//
		for(Allele2DTO allele: allele2DTOs){
			OrderTableRow row=new OrderTableRow();
			String alleleName=allele.getAlleleName();
			row.setAlleleName(alleleName);
			row.setAlleleDescription(allele.getAlleleDescription());
			List<LinkDetails> targetLinks=new ArrayList<>();
			
			
			//row.setNoProductInfo(allele.get);
			List<ProductDTO> productsForAllele = alleleNameToProductsMap.get(alleleName);
			System.out.println("alleName="+alleleName);
			LinkDetails vectorTargetMap=null;
			for(ProductDTO prod:productsForAllele){
				//ProductDTO prod=productsForAllele.get(0);
				vectorTargetMap=new LinkDetails();
				vectorTargetMap.setLabel("Target vector map");
				List<String> colonSeperatedMap=prod.getOtherLinks();
				if(colonSeperatedMap!=null){
					for (String link : colonSeperatedMap) {
						if (link.startsWith("design_link")) {
							vectorTargetMap.setLink(link.replace("design_link:", ""));
						}
					}
				}
				
				
			}
			for(ProductDTO prod: productsForAllele){
				System.out.println("prod= "+prod);
			}
			LinkDetails geneTargetMap=new LinkDetails();
			geneTargetMap.setLabel("Targeted gene map");
			geneTargetMap.setLink(allele.getAlleleImage());
			targetLinks.add(geneTargetMap);
			if(vectorTargetMap!=null){
			targetLinks.add(vectorTargetMap);
			}
			
			row.setGeneTargetDetails(targetLinks);
			orderTableRows.add(row);
			
		}
		
		
		
		OrderTableRow row=new OrderTableRow();
		row.setAlleleName("Cpsf3tm1b(EUCOMM)Wtsi");
		row.setStrainOfOrigin("C57BL/6N");
		row.setAlleleDescription("Reporter-tagged deletion allele (post-Cre)");
		LinkDetails detail=new LinkDetails();
		detail.setLabel("Target Vector Map");
		detail.setLink("https://www.i-dcc.org/imits/targ_rep/alleles/4973/allele-image-cre?simple=true.jpg.jpg");
		List<LinkDetails> geneTargetDetails=new ArrayList<>();
		geneTargetDetails.add(detail);
		row.setGeneTargetDetails(geneTargetDetails);
		orderTableRows.add(row);
		return orderTableRows;
	}
	
	protected List<Allele2DTO> getAllele2DTOs(String geneAcc) throws SolrServerException{
		
		String q = "mgi_accession_id:\"" + geneAcc+ "\"";//&start=0&rows=100&hl=true&wt=json";
		SolrQuery query=new SolrQuery();
		query.setQuery(q);
		query.addFilterQuery("type:Allele");
		query.setRows(Integer.MAX_VALUE);
		System.out.println("query for alleles="+query);
		QueryResponse response = allele2Core.query(query);
		System.out.println("number found of allele2 docs="+response.getResults().getNumFound());
		List<Allele2DTO> allele2DTOs = response.getBeans(Allele2DTO.class);
		System.out.println("number of alleles is "+allele2DTOs.size());
		
        return allele2DTOs;

        
	}
	
	protected Map<String,List<ProductDTO>> getProducts(String geneAcc) throws SolrServerException{
		Map<String,List<ProductDTO>> alleleNameToProductsMap=new HashMap<>();
		String q = "mgi_accession_id:\"" + geneAcc+ "\"";//&start=0&rows=100&hl=true&wt=json";
		SolrQuery query=new SolrQuery();
		query.setQuery(q);
		query.setRows(Integer.MAX_VALUE);
		System.out.println("query for products="+query);
		QueryResponse response = productCore.query(query);
		System.out.println("number found of products docs="+response.getResults().getNumFound());
		List<ProductDTO> productDTOs = response.getBeans(ProductDTO.class);
		System.out.println("number of productDTOs is "+productDTOs.size());
		for(ProductDTO prod: productDTOs){
			if(!alleleNameToProductsMap.containsKey(prod.getAlleleName())){
				alleleNameToProductsMap.put(prod.getAlleleName(), new ArrayList<>());
			}
			alleleNameToProductsMap.get(prod.getAlleleName()).add(prod);
		}
		
        return alleleNameToProductsMap;

        
	}

}
