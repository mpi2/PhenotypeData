package org.mousephenotype.cda.solr.service;

import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.junit.Test;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;


//@ContextConfiguration( locations={ "classpath:test-Observations.xml" })
public class ExpressionServiceTest {
	

	
	
	@Test
	public void getLacDataForAnatomogram(){
		String experimentCore=("http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/");
		String imagesCore=("http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/impc_images/");
		String pipelineCore=("http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/pipeline/");
		String maCore=("http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/ma/");
		
		ExpressionService expressionService= new ExpressionService(experimentCore, imagesCore , pipelineCore, maCore );
		expressionService.initialiseAbnormalMaMap();
		String geneAccession="MGI:1922730";
		try {
			List<Count> parameterCounts = expressionService.getLaczCategoricalParametersForGene(geneAccession);
			expressionService.getLacDataForAnatomogram(parameterCounts);
			
			
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
