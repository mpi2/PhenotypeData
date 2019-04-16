package org.mousephenotype.cda.solr.service;


import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfigSolr.class})
public class HistopathServiceTest {

    private final Logger logger = LoggerFactory.getLogger(PhenodigmService.class);


    @Autowired
    GrossPathService grossService;

	@Autowired
    HttpSolrClient experimentCore;


    @Test
    public void getTableDataTest() throws IOException, SolrServerException {
        //gene_accession_id:"MGI:2449119"
        //HistoPath_1481
        //looks like we don't pick up the associated pato term in the indexer e.g.
        //<ontologyParameter parameterID="IMPC_HIS_119_001" sequenceID="1">
        //<term>PATO:0001566:diffuse</term>
        //</ontologyParameter>

        ObservationService observationService = new ObservationService(experimentCore);
        HistopathService   histopathService   = new HistopathService(observationService);


        //String geneAccession = "MGI:2449119";
        String               geneAccession   ="MGI:1891341";
        List<ObservationDTO> allObservations = histopathService.getObservationsForHistopathForGene(geneAccession);

        for (ObservationDTO obs : allObservations) {
//            System.out.println(obs);
        }

        Map<String, List<ObservationDTO>> uniqueSampleSequeneAndAnatomyName = histopathService
				.getUniqueInfo(allObservations);

//        List<HistopathPageTableRow> filteredObservations = histopathService.getTableData(uniqueSampleSequeneAndAnatomyName);
//        for (HistopathPageTableRow row : filteredObservations) {
//            //System.out.println("row="+row);
//        }

        logger.warn("Finish me or delete me.");
    }
}