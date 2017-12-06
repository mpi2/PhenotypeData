package uk.ac.ebi.phenodigm.dao.advancedsearch;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/** 
 * @deprecated pdsimplify
 */
public class AdvancedSearchSolrDao {
	
//	@Autowired
	@Qualifier("postqcService")
	PostQcService genotypePhenotypeService;
	
	public AdvancedSearchSolrDao(PostQcService genotypePhenotypeService){
		this.genotypePhenotypeService=genotypePhenotypeService;
	}
	
	//get the number of genes associated with phenotypes - the same way as we do for the phenotypes pages?
	 public List<String> getGenesForPhenotype(String phenotypeId) throws IOException, URISyntaxException, SolrServerException{
		List<String> geneSymbols=genotypePhenotypeService.getGenesForMpId(phenotypeId);
		return geneSymbols;
	 }
	 
	 public List<String> getGenesForPhenotypeAndPhenotype(String phenotypeId, String phenotypeId2) throws IOException, URISyntaxException, SolrServerException{
		 List<String> geneSymbols=genotypePhenotypeService.getGenesForMpId(phenotypeId);
		 List<String> geneSymbols2=genotypePhenotypeService.getGenesForMpId(phenotypeId2);
		 List<String> list=(List<String>) CollectionUtils.intersection(geneSymbols, geneSymbols2);
		return list;
	 }
}
