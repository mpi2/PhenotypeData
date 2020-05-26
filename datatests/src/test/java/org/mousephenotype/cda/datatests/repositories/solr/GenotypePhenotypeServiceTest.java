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

package org.mousephenotype.cda.datatests.repositories.solr;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.GenotypePhenotypeService;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.CountTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertTrue;


/**
 * @author ilinca
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class GenotypePhenotypeServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GenotypePhenotypeService genotypePhenotypeService;

    @Autowired
    private GeneService geneService;

    @Autowired
    private ObservationService observationService;

    @Autowired
    private MpService mpService;
    

    @Test
    public void testGetAssociationsCount() {
        List<String> resources = new ArrayList<>();
        resources.add("IMPC");
        try {
            TreeSet<CountTableRow> result2 = genotypePhenotypeService.getAssociationsCount("MP:0005377", resources);

            int expectedCount      = 100;        // 2017-10-24 (mrelac) After much statistical refactoring to the hearing associations, today's actual count was 106.
            int actualAssociations = result2.iterator().next().getCount();
            assertTrue("Expected at least " + expectedCount + " associations but found " + actualAssociations, expectedCount <= actualAssociations);

        } catch (IOException | SolrServerException e) {

            e.printStackTrace();
        }
    }


    @Test
    public void testAllGPGenesInGeneCore()
            throws SolrServerException, IOException {
        logger.debug("Test if all genes in genotype-phenotype core are indexed in the gene core.");

        Set<String> gpGenes = genotypePhenotypeService.getAllGenesWithPhenotypeAssociations();

        Set<String> gGenes      = geneService.getAllGenes();
        Set<String> knownToMiss = new HashSet<>();  // Ignore these genes because they only have legacy phenotype data.
        knownToMiss.add("MGI:3688249"); // For bug MPII-1493, Ostes
//        knownToMiss.add("MGI:1861674"); // For bug https://www.ebi.ac.uk/panda/jira/browse/MPII-1783? Nespas

        Collection res = CollectionUtils.subtract(gpGenes, gGenes);
        res = CollectionUtils.subtract(res, knownToMiss);

        if (!res.isEmpty()) {
            logger.warn("The following genes are in in the genotype-phenotype core but not in the gene core: " + res);
        }
    }

    @Test
    public void testAllExperimentGenesInGeneCore()
            throws SolrServerException, IOException {

        logger.debug("Test if all genes in experiment core are indexed in the gene core.");

        Set<String> experimentGenes = observationService.getAllGeneIdsByResource(null, true);

        Set<String> gGenes      = geneService.getAllGenes();
        Set<String> knownToMiss = new HashSet<>();  // Ignore these genes because they only have legacy phenotype data.
        knownToMiss.add("MGI:3688249");

        Collection res = CollectionUtils.subtract(experimentGenes, gGenes);
        res = CollectionUtils.subtract(res, knownToMiss);

        if (!res.isEmpty()) {
            logger.warn("The following genes are in in the experiment core but not in the gene core:  " + res);
        }
    }

    @Test
    public void testAllGPPhenotypeInMP() throws SolrServerException, IOException {
        logger.debug("Test if all phenotypes in genotype-phenotype core are indexed in the mp core.");

        Set<String> gpPhen = genotypePhenotypeService.getAllPhenotypesWithGeneAssociations();

        Set<String> mpPhen = mpService.getAllPhenotypes();

        Collection res = CollectionUtils.subtract(gpPhen, mpPhen);

        if (res.size() > 0) {

            // The term MP:0005395 is encoded in IMPRESS for a legacy parameter:
            //   https://www.mousephenotype.org/impress/parameterontologies/2/1
            // Since it's a legacy pipeline, it will likely never be updated,
            // so the test skips checking this term.
            if (res.size() == 1 && !res.toArray()[0].equals("MP:0005395")) {
                logger.warn("The following phenotypes are in in the genotype-phenotype core but not in the MP core: " + res);
            }
        }
    }

    @Test
    public void testGetGenesForMpId() {
        String       phenotypeId = "MP:0002078";
        List<String> geneSymbols = null;
        try {

            geneSymbols = genotypePhenotypeService.getGenesForMpId(phenotypeId);

        } catch (IOException | SolrServerException e) {

            e.printStackTrace();
        }

        //currently 4056 genes in the gp core are associated to abnormal glucose Homeostasis
        logger.info("geneSymbols size for phenotype is " + geneSymbols.size());
        assertTrue(geneSymbols.size() > 405);
    }
}