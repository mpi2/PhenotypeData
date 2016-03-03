package org.mousephenotype.cda.solr.service;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Test;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;


//@ContextConfiguration( locations={ "classpath:test-Observations.xml" })
public class HistopathServiceTest {
	

	
	
	@Test
	public void getTableDataTest(){
		//gene_accession_id:"MGI:2449119"
		//HistoPath_1481
		//looks like we don't pick up the associated pato term in the indexer e.g.
		//<ontologyParameter parameterID="IMPC_HIS_119_001" sequenceID="1">
        //<term>PATO:0001566:diffuse</term>
		//</ontologyParameter>
		
		HttpSolrServer solr=new HttpSolrServer("http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/");
		ObservationService observationService= new ObservationService(solr);
		HistopathService histopathService=new HistopathService(observationService);
		String geneAccession="MGI:2449119";
		try {
			histopathService.getTableData(geneAccession);
			
			
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}