package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/test.properties")
public class ImpressServiceTest {

	private final Logger logger = LoggerFactory.getLogger(ObservationServiceTest.class);

	// Sring Configuration class
	// Only wire up the observation service for this test suite
	@Configuration
	@ComponentScan(
		basePackages = {"org.mousephenotype.cda"},
		useDefaultFilters = false,
		includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ImpressService.class})
		})
	static class ContextConfiguration {

		@NotNull
		@Value("${solr.host}")
		private String solrBaseUrl;

		@Bean(name = "pipelineCore")
		HttpSolrServer getPipelineCore() {
			return new HttpSolrServer(solrBaseUrl + "/pipeline");
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}

	@Autowired
	ImpressService impressService;


	@Test
	public void getMpsForProcedures() throws Exception {

		Map<String, Set<String>> mps = impressService.getMpsForProcedures();

		assertTrue(mps.size() > 100);

	}

}
