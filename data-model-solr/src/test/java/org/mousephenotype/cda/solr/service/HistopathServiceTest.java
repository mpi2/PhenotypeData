package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;

import java.util.List;


//@ContextConfiguration( locations={ "classpath:test-Observations.xml" })
public class HistopathServiceTest {



	//TODO: Fix this test case

//	@Test
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
			List<ObservationDTO> allObservations = histopathService.getObservationsForHistopathForGene(geneAccession);

			for(ObservationDTO obs: allObservations){
				System.out.println(obs);
			}

			List<HistopathPageTableRow> filteredObservations = histopathService.getTableData(allObservations);
			 for(HistopathPageTableRow row: filteredObservations){
				 //System.out.println("row="+row);
			 }

		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
