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

package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.service.dto.BasicBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfigSolr.class})
public class MpServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private final int EXPECTED_MP_BEAN_COUNT = 27;

	@Autowired
	MpService mpService;


	@Test
	public void testGetAllTopLevelPhenotypesAsBasicBeans(){
		try {
			Set<BasicBean> basicMpBeans=mpService.getAllTopLevelPhenotypesAsBasicBeans();
			assertTrue("Expected at least " + EXPECTED_MP_BEAN_COUNT + " mp beans but found "
				+ basicMpBeans.size(), basicMpBeans.size() >= EXPECTED_MP_BEAN_COUNT);

		} catch (SolrServerException | IOException e) {

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