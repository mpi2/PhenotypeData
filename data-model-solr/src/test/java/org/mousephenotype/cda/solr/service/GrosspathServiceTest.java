package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.GrossPathPageTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {TestConfigSolr.class})
public class GrosspathServiceTest {

    private final Logger logger = LoggerFactory.getLogger(PhenodigmService.class);

   @Autowired
    private GrossPathService  grossPathService;


	@Test
    public void getTableDataTest() throws IOException, SolrServerException {

        //String geneAccession = "MGI:2449119";
        String acc="MGI:1891341";
        List<ObservationDTO> allObservations = grossPathService.getObservationsForGrossPathForGene(acc);
		int sampleSize=grossPathService.getSampleToObservationMap(allObservations).size();
		System.out.println("sample size="+sampleSize);
		List<SolrDocument> images = grossPathService.getGrossPathImagesForGene(acc);
		List<ObservationDTO> abnormalObservations = grossPathService.getAbnormalObservations(allObservations);
		//grossPathService.processForAbnormalAnatomies(allObservations, abnormalObservations);
		List<GrossPathPageTableRow> grossPathRows = grossPathService.getSummaryTableData(allObservations, images, abnormalObservations, true, "IMPC_PAT_029_002");
        for (GrossPathPageTableRow row : grossPathRows) {
            System.out.println("row="+row);
        }

        logger.warn("Finish me or delete me.");
    }
}