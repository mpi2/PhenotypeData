package org.mousephenotype.cda.solr.service;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Test;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;


//@ContextConfiguration( locations={ "classpath:test-Observations.xml" })
public class ObservationServiceTest {
	

	
	
	@Test
	public void getObservationByProcedureNameAndGene(){
		HttpSolrServer solr=new HttpSolrServer("http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/");
		ObservationService observationService= new ObservationService(solr);
		String procedureName="Histopathology";
		String geneAccession="MGI:2449119";
		try {
			List<ObservationDTO> result = observationService.getObservationsByProcedureNameAndGene(procedureName, geneAccession);
			for(ObservationDTO obs:result){
				System.out.println("observations="+obs);
			}
			
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
