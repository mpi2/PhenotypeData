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
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
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
import java.util.ArrayList;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
public class MpServiceTest {

	// Spring Configuration class
	// Only wire up the mp service for this test suite
	@Configuration
	@ComponentScan(
		basePackages = {"org.mousephenotype.cda.solr.service"},
		useDefaultFilters = false,
		includeFilters = { @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {MpService.class})
		})
	static class ContextConfiguration {

		@NotNull
		@Value("${solr.host}")
		private String solrBaseUrl;

		@Bean(name = "mpCore")
		HttpSolrClient getSolrCore() {
			return new HttpSolrClient(solrBaseUrl + "/mp");
		}

		@Bean
		public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

	}

	@Autowired
	MpService mpService;

	@Test
	public void testGetAllTopLevelPhenotypesAsBasicBeans(){
		try {
			Set<BasicBean> basicMpBeans=mpService.getAllTopLevelPhenotypesAsBasicBeans();
			for(BasicBean bean: basicMpBeans){
				System.out.println("MP name in test="+bean.getName()+" mp id in test="+bean.getId());
			}
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Test
	public void testGetChildren(){
		ArrayList<String> children;
		try {
			children = mpService.getChildrenFor("MP:0002461");
			assertTrue(children.size() > 0);
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
