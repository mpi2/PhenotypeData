package org.mousephenotype.cda.datatests.repositories.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.GrossPathService;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.GrossPathPageTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class GrosspathServiceTest {

    private final Logger logger         = LoggerFactory.getLogger(this.getClass());
    private final int    EXPECTED_COUNT = 1;

   @Autowired
    private GrossPathService grossPathService;

	@Test
    public void getTableDataTest() throws IOException, SolrServerException {

        String acc="MGI:1891341";
        List<ObservationDTO> allObservations = grossPathService.getObservationsForGrossPathForGene(acc);
		int sampleSize = grossPathService.getSampleToObservationMap(allObservations).size();
		logger.info("sample size="+sampleSize);
		List<SolrDocument> images = grossPathService.getGrossPathImagesForGene(acc);
		List<ObservationDTO> abnormalObservations = grossPathService.getAbnormalObservations(allObservations);
		List<GrossPathPageTableRow> grossPathRows = grossPathService.getSummaryTableData(allObservations, images, abnormalObservations, true, "IMPC_PAT_029_002");
        for (GrossPathPageTableRow row : grossPathRows) {
            logger.info("row="+row);
        }
        assertTrue("Expected " + EXPECTED_COUNT + " rows but got " + grossPathRows.size(), grossPathRows.size() >= EXPECTED_COUNT);
    }
}