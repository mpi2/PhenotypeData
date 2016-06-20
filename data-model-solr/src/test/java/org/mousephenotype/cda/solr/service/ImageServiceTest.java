package org.mousephenotype.cda.solr.service;

import javax.validation.constraints.NotNull;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/test.properties")
public class ImageServiceTest {
	
	
	@Autowired
	private ImageService imageService;
	// Sring Configuration class
		// Only wire up the observation service for this test suite
		@Configuration
		@ComponentScan(
			basePackages = {"org.mousephenotype.cda"},
			useDefaultFilters = false,
			includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ImageService.class})
			})
		static class ContextConfiguration {

			@NotNull
			@Value("${solr.host}")
			private String solrBaseUrl;

			@Bean(name = "impcImagesCore")
			HttpSolrServer getExperimentCore() {
				return new HttpSolrServer(solrBaseUrl + "/impc_images");
			}

			@Bean
			public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
				return new PropertySourcesPlaceholderConfigurer();
			}

		}
	
	
	@Test
	public void testgetImagePropertiesThatHaveMp(){
		
		String acc="MGI:1913955";
		try {
			imageService.getImagePropertiesThatHaveMp(acc);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
