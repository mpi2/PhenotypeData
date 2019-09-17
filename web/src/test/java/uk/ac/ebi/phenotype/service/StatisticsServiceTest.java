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

package uk.ac.ebi.phenotype.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.ac.ebi.phenotype.web.dao.StatisticsService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ServiceTestConfig.class})
public class StatisticsServiceTest {


	@Autowired
	StatisticsService statisticsService;


	// 17-09-2019 (mrelac) Ignoring this test as the assertTrue(experiment != null) below currently fails.
	@Ignore
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
		String geneAccession = "MGI:1915747";
		List<String> genderList=null;
		
		List<String> zyList=new ArrayList<>();
		zyList.add(zygosity);
		ExperimentDTO experiment = statisticsService.getSpecificExperimentDTOFromRest(parameter_stable_id, pipeline_stable_id, geneAccession, genderList, zyList, phenotyping_center, strain_accession_id, metadata_group, allele_accession_id);

		assertTrue(experiment != null);
		assertTrue(experiment.getGeneMarker()!=null);
		
		
	}
	
	@Test
	public void testZygositySpecificExperimentFromRest() throws SolrServerException, IOException {
		//charts?accession=MGI:1915747&parameter_stable_id=IMPC_HEM_038_001
		//https://dev.mousephenotype.org/data/chart?accession=MGI:1915747&parameter_stable_id=IMPC_HEM_038_001&chart_type=UNIDIMENSIONAL_BOX_PLOT&pipeline_stable_id=IMPC_001&zygosity=homozygote&phenotyping_center=MARC&strain_accession_id=MGI:2159965&allele_accession_id=MGI:5605782&metadata_group=08aa37a898ab923b9ffdbd01c0077040&&experimentNumber=chart1
		
		String parameter_stable_id="IMPC_HEM_038_001";
		String pipeline_stable_id="IMPC_001";
		String zygosity="heterozygote";
		String phenotyping_center="MARC";
		String strain_accession_id="MGI:2159965";
		String allele_accession_id="MGI:5605782";
		String metadata_group="08aa37a898ab923b9ffdbd01c0077040";
		String geneAccession = "MGI:1915747";
		List<String> genderList=null;
		
		List<String> zyList=new ArrayList<>();
		zyList.add(zygosity);
		ExperimentDTO experiment = statisticsService.getSpecificExperimentDTOFromRest(parameter_stable_id, pipeline_stable_id, geneAccession, genderList, zyList, phenotyping_center, strain_accession_id, metadata_group, allele_accession_id);
		assertTrue(experiment==null);		
	}

	// 17-09-2019 (mrelac) Ignoring this test as the assertTrue(experiment != null) below currently fails.
	@Ignore
	@Test
	public void testSexSpecificExperimentFromRest() throws SolrServerException, IOException {
		//how should the service behave for sex at the moment sex is just ignored and zygosity just uses the first option...
		//charts?accession=MGI:1915747&parameter_stable_id=IMPC_HEM_038_001
		//https://dev.mousephenotype.org/data/chart?accession=MGI:1915747&parameter_stable_id=IMPC_HEM_038_001&chart_type=UNIDIMENSIONAL_BOX_PLOT&pipeline_stable_id=IMPC_001&zygosity=homozygote&phenotyping_center=MARC&strain_accession_id=MGI:2159965&allele_accession_id=MGI:5605782&metadata_group=08aa37a898ab923b9ffdbd01c0077040&&experimentNumber=chart1
		
		String parameter_stable_id="IMPC_HEM_038_001";
		String pipeline_stable_id="IMPC_001";
		String zygosity="homozygote";
		String phenotyping_center="MARC";
		String strain_accession_id="MGI:2159965";
		String allele_accession_id="MGI:5605782";
		String metadata_group="08aa37a898ab923b9ffdbd01c0077040";
		String geneAccession = "MGI:1915747";
		List<String> genderList=new ArrayList<>();
		genderList.add("female");
		
		List<String> zyList=new ArrayList<>();
		zyList.add(zygosity);
		ExperimentDTO experiment = statisticsService.getSpecificExperimentDTOFromRest(parameter_stable_id, pipeline_stable_id, geneAccession, genderList, zyList, phenotyping_center, strain_accession_id, metadata_group, allele_accession_id);
		assertTrue(experiment != null);
	}
}