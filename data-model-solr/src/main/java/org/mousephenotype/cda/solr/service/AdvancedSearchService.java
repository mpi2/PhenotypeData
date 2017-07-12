package org.mousephenotype.cda.solr.service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AdvancedSearchService {
	
//	@Autowired
	@Qualifier("postqcService")
	PostQcService genotypePhenotypeService;
	
	public AdvancedSearchService(PostQcService genotypePhenotypeService){
		this.genotypePhenotypeService=genotypePhenotypeService;
	}
	
	//get the number of genes associated with phenotypes - the same way as we do for the phenotypes pages?
	 public List<String> getGenesForPhenotype(String phenotypeId) throws IOException, URISyntaxException, SolrServerException{
		List<String> geneSymbols=genotypePhenotypeService.getGenesForMpId(phenotypeId);
		return geneSymbols;
	 }
	 
	 public List<String> getGenesForPhenotypeAndPhenotype(String phenotypeId, String phenotypeId2) throws IOException, URISyntaxException, SolrServerException{
		 List<String> geneSymbols=genotypePhenotypeService.getGenesForMpId(phenotypeId);
		 System.out.println(geneSymbols.size());
		 List<String> geneSymbols2=genotypePhenotypeService.getGenesForMpId(phenotypeId2);
		 @SuppressWarnings("unchecked")
		List<String> list=(List<String>) CollectionUtils.intersection(geneSymbols, geneSymbols2);
		 System.out.println(geneSymbols2.size());
		return list;
	 }
	 
	 public List<String> getGenesForPhenotypeORPhenotype(String phenotypeId, String phenotypeId2) throws IOException, URISyntaxException, SolrServerException{
		 List<String> geneSymbols=genotypePhenotypeService.getGenesForMpId(phenotypeId);
		 System.out.println(geneSymbols.size());
		 List<String> geneSymbols2=genotypePhenotypeService.getGenesForMpId(phenotypeId2);
		 System.out.println(geneSymbols2.size());
		 @SuppressWarnings("unchecked")
		List<String> list=(List<String>) CollectionUtils.union(geneSymbols, geneSymbols2);
		 
		return list;
	 }
}
