package org.mousephenotype.cda.solr.service;

import java.util.List;
import java.util.Map;

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
			List<AnatomogramDataBean> beans = expressionService.getAnatomogramDataBeans(parameterCounts);
			for(AnatomogramDataBean bean:beans){
				System.out.println("AnatomogramDataBean"+bean);
			}
			
			
		
			Map<String, Long> anatomogramDataBeans = expressionService.getLacSelectedTopLevelMaCountsForAnatomogram(beans);
			for( String topMa:anatomogramDataBeans.keySet()){
				System.out.println("topMa="+topMa+" total count "+anatomogramDataBeans.get(topMa));
			}
			
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
