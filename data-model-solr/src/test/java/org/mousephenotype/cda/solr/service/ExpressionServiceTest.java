package org.mousephenotype.cda.solr.service;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;

import java.util.List;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
public class ExpressionServiceTest {


	@Autowired
	private ExpressionService expressionService;
	
	@Autowired
	ExperimentService experimentService;
//
//	@Autowired
//	ImpressService impressService;

	
	// Sring Configuration class
		// Only wire up the observation service for this test suite
		@Configuration
		@ComponentScan(
			basePackages = {"org.mousephenotype.cda"},
			useDefaultFilters = false,
			includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ExpressionService.class})
			})
		static class ContextConfiguration {

			@NotNull
			@Value("${solr.host}")
			private String solrBaseUrl;

			@Bean(name = "impcImagesCore")
			HttpSolrServer getImpcImagesCore() {
				return new HttpSolrServer(solrBaseUrl + "/impc_images");
			}
			@Bean(name = "experimentCore")
			HttpSolrServer getExperimentCore() {
				return new HttpSolrServer(solrBaseUrl + "/experiment");
			}
			@Bean(name = "pipelineCore")
			HttpSolrServer getPipelineCore() {
				return new HttpSolrServer(solrBaseUrl + "/pipeline");
			}
			
			@Bean(name= "anatomyCore")
			HttpSolrServer getAnatomyCore(){
				return new HttpSolrServer(solrBaseUrl+"/anatomy");
			}
//			@Autowired
//			ImpressService impressService;
//
//			@Autowired
//			private AnatomyService anatomyService;

			@Bean
			public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
				return new PropertySourcesPlaceholderConfigurer();
			}

		}

		//@Test
		public void getLacDataForAnatomogram(){
		
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
	
//	@Test
//	public void getDataForAnatomyPage(){
//		expressionService.getExpressionForGenesOnAnatomyPage();
//	}


}
