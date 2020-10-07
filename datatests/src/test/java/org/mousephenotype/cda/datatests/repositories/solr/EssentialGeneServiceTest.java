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

package org.mousephenotype.cda.datatests.repositories.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.EssentialGeneService;
import org.mousephenotype.cda.solr.service.dto.EssentialGeneDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class EssentialGeneServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private EssentialGeneService essentialGeneService;


	@Test
	public void testGetGeneById() throws SolrServerException, IOException {
		String mgiId = "MGI:1098687";//Aak1

		List<EssentialGeneDTO> essentialGeneDTOS = essentialGeneService.getGeneListByMgiId(mgiId);
		for(EssentialGeneDTO geneDTO: essentialGeneDTOS) {
			logger.info("Gene symbol is: " + geneDTO.getMarkerSymbol());
			logger.info("mgiAccessionId: " + geneDTO.getMgiAccession());
			logger.info("idgChr: " + geneDTO.getIdgChr());
			logger.info("idgFamily: " + geneDTO.getIdgFamily());
			logger.info("idgIDL: " + geneDTO.getIdgIdl());
			logger.info("idgSymbol: " + geneDTO.getIdgSymbol());
			logger.info("uniprot acc: " + geneDTO.getIdgUniprotAcc());
			logger.info("===================");
		}

		EssentialGeneDTO gene = essentialGeneService.getGeneByMgiId(mgiId);
		logger.info("single Gene symbol is: " + gene.getMarkerSymbol());
		logger.info("mgiAccessionId: " + gene.getMgiAccession());
		logger.info("idgChr: " + gene.getIdgChr());
		logger.info("idgFamily: " + gene.getIdgFamily());
		logger.info("idgIDL: " + gene.getIdgIdl());
		logger.info("idgSymbol: " + gene.getIdgSymbol());
		logger.info("uniprot acc: " + gene.getIdgUniprotAcc());
		logger.info("===================");
		assertTrue("Expected gene but was null", gene != null);
	}

	@Test
	public void testGetAllIdgGeneList() throws IOException, SolrServerException {
		List<EssentialGeneDTO> idgGeneDTOS = essentialGeneService.getAllIdgGeneList();
		logger.info("idgGenes size="+idgGeneDTOS.size());

//		for(EssentialGeneDTO geneDTO: idgGeneDTOS) {
//			logger.info("Gene symbol is: " + geneDTO.getMarkerSymbol());
//			logger.info("mgiAccessionId: " + geneDTO.getMgiAccession());
//			logger.info("idgChr: " + geneDTO.getIdgChr());
//			logger.info("idgFamily: " + geneDTO.getIdgFamily());
//			logger.info("idgIDL: " + geneDTO.getIdgIdl());
//			logger.info("idgSymbol: " + geneDTO.getIdgSymbol());
//			logger.info("uniprot acc: " + geneDTO.getIdgUniprotAcc());
//			logger.info("===================");
//		}
	}
}
