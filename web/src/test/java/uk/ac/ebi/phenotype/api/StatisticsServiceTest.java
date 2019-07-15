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
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
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

import uk.ac.ebi.phenotype.web.dao.StatisticsService;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class StatisticsServiceTest {

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
		@Value("${base_url}")
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

	String statisticsUrl="http://ves-ebi-d1.ebi.ac.uk:8091/";
	StatisticsService statsService=new StatisticsService(statisticsUrl);

	@Test
	public void testGetSpecificExperimentFromRest() throws SolrServerException, IOException {
		//charts?accession=MGI:1915747&parameter_stable_id=IMPC_HEM_038_001
		//https://dev.mousephenotype.org/data/chart?accession=MGI:1915747&parameter_stable_id=IMPC_HEM_038_001&chart_type=UNIDIMENSIONAL_BOX_PLOT&pipeline_stable_id=IMPC_001&zygosity=homozygote&phenotyping_center=MARC&strain_accession_id=MGI:2159965&allele_accession_id=MGI:5605782&metadata_group=08aa37a898ab923b9ffdbd01c0077040&&experimentNumber=chart1
		
		String parameter_stable_id="IMPC_HEM_038_001";
		String pipeline_stable_id="IMPC_001";
		String zygosity="homozygote";
		String phenotyping_center="MARC";
		String strain_accession_id="MGI:2159965";
		String allele_accession_id="MGI:5605782";
		String metadata_group="08aa37a898ab923b9ffdbd01c0077040";
		String experimentNumber="chart1";
		String geneAccession = "MGI:1915747";
		List<String> genderList=null;
		
		List<String> zyList=new ArrayList<>();
		zyList.add(zygosity);
		ExperimentDTO experiment = statsService.getSpecificExperimentDTOFromRest(parameter_stable_id, pipeline_stable_id, geneAccession, genderList, zyList, phenotyping_center, strain_accession_id, metadata_group, allele_accession_id);
		
		System.out.println("Gene symbol is: " + experiment.getGeneMarker());
		assertTrue(experiment.getGeneMarker()!=null);
		
	}
}
