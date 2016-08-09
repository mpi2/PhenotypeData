/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This test class is intended to run healthchecks against the observation table.
 */

package uk.ac.ebi.phenotype.api;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
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
import java.io.IOException;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class GeneServiceTest {

	// Spring Configuration class
	// Only wire up the observation service for this test suite
	@Configuration
	@ComponentScan(
		basePackages = {"org.mousephenotype.cda.solr.service"},
		useDefaultFilters = false,
		includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {GeneService.class})
		})
	static class ContextConfiguration {

		@NotNull
		@Value("${baseUrl}")
		private String baseUrl;

		@NotNull
		@Value("${solr.host}")
		private String solrBaseUrl;

		@Bean(name = "geneCore")
		HttpSolrClient getSolrCore() {
			return new HttpSolrClient(solrBaseUrl + "/gene");
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}

	@Autowired
	GeneService geneService;

	@Test
	public void testGetGeneById() throws SolrServerException, IOException {
		String mgiId = "MGI:1929293";
		GeneDTO gene = geneService.getGeneById(mgiId);
		assertTrue(gene!=null);
		System.out.println("Gene symbol is: " + gene.getMarkerSymbol());
		System.out.println("Didn't retreive human gene symbol. Proof: " + gene.getHumanGeneSymbol());
	}
}
