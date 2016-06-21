package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;

import java.util.List;
import java.util.Map;


//@ContextConfiguration( locations={ "classpath:test-Observations.xml" })
public class ExpressionServiceTest {


	//TODO: Fix this test case


//	@Test
	public void getLacDataForAnatomogram(){
		String solrServer="http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/";
		//String solrServer="http://ves-hx-d1.ebi.ac.uk:8080/mi/impc/beta/solr/";
		String experimentCore=(solrServer+"experiment/");
		String imagesCore=(solrServer+"impc_images/");
		String pipelineCore=(solrServer+"pipeline/");
		String anatomyCore=(solrServer+"anatomy/");

		ExpressionService expressionService= new ExpressionService(experimentCore, imagesCore , pipelineCore, anatomyCore );
		expressionService.initialiseAbnormalOntologyMaps();
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
