/*
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
 */

package org.mousephenotype.cda.solr.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.service.dto.CountTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.fail;

/**
 * @author ilinca
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestConfigSolr.class})
@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class GenotypePhenotypeServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
	@Qualifier("postqcService")
    private PostQcService gpService;

    @Autowired
    private GeneService gService;


    @Autowired
    private ObservationService oService;

    @Autowired
    private MpService mpService;
    
    
    
//    @Test
//    public void testGetBiologicalSystemPleiotropyDownloadQuery(){
//    	try {
//			gpService.getBiologicalSystemPleiotropyDownloadQuery(null, false, null, "Cardiovascular");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SolrServerException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }
    
    @Test
    public void testgetAssociationsCount(){
    	System.out.println("running test");
    	List<String> resources = new ArrayList<>();
        resources.add("IMPC");
    	try {
//			TreeSet<CountTableRow> result = gpService.getAssociationsCount("MP:0000269", resources);
//			for(CountTableRow row : result){
//				System.out.println(row.getMpId()+" "+ row.getCategory()+" "+row.getCount());
//			}
			
			TreeSet<CountTableRow> result2 = gpService.getAssociationsCount("MP:0005377", resources);
			for(CountTableRow row : result2){
				System.out.println(row.getMpId()+" "+ row.getCategory()+" "+row.getCount());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    

    @Test
    public void testAllGPGenesInGeneCore()
    throws SolrServerException, IOException {
    	logger.debug("Test if all genes in genotype-phenotype core are indexed in the gene core.");

        Set<String> gpGenes = gpService.getAllGenesWithPhenotypeAssociations();

        Set<String> gGenes = gService.getAllGenes();
        Set<String> knownToMiss = new HashSet<>();  // Ignore these genes because they only have legacy phenotype data.
        knownToMiss.add("MGI:3688249"); // For bug MPII-1493, Ostes
//        knownToMiss.add("MGI:1861674"); // For bug https://www.ebi.ac.uk/panda/jira/browse/MPII-1783? Nespas
        // Don't need this any more as Peter put them in iMits and they should be in now.

        Collection res = CollectionUtils.subtract(gpGenes, gGenes);
        res = CollectionUtils.subtract(res, knownToMiss);

        if (res.size() > 0) {
            logger.warn("The following genes are in in the genotype-phenotype core but not in the gene core: " + res);
        	fail("The following genes are in in the genotype-phenotype core but not in the gene core: " + res);
        }
    }

    @Test
    public void testAllExperimentGenesInGeneCore()
    throws SolrServerException, IOException {

        logger.debug("Test if all genes in experiment core are indexed in the gene core.");

        Set<String> experimentGenes = oService.getAllGeneIdsByResource(null, true);

        Set<String> gGenes = gService.getAllGenes();
        Set<String> knownToMiss = new HashSet<>();  // Ignore these genes because they only have legacy phenotype data.
        knownToMiss.add("MGI:3688249");

        Collection res = CollectionUtils.subtract(experimentGenes, gGenes);
        res = CollectionUtils.subtract(res, knownToMiss);

        if (res.size() > 0) {
            logger.warn("The following genes are in in the genotype-phenotype core but not in the gene core: " + res);
        	fail("The following genes are in in the genotype-phenotype core but not in the gene core: " + res);
        }
    }

    @Test
    public void testAllGPPhenotypeInMP() throws SolrServerException, IOException {
        logger.debug("Test if all phenotypes in genotype-phenotype core are indexed in the mp core.");

        Set<String> gpPhen = gpService.getAllPhenotypesWithGeneAssociations();

        Set<String> mpPhen = mpService.getAllPhenotypes();

        Collection res = CollectionUtils.subtract(gpPhen, mpPhen);

        if (res.size() > 0){
        	fail("The following phenotypes are in in the genotype-phenotype core but not in the MP core: " + res);
        }
    }
}