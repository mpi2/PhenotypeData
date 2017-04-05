package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.GrossPathPageTableRow;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
public class GrosspathServiceTest {


   @Autowired
    private GrossPathService  grossPathService;
//doesn't work currently maybe with new spring version this stuff will be simpler?
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
		List<GrossPathPageTableRow> grossPathRows = grossPathService.getSummaryTableData(allObservations, images, abnormalObservations);
        for (GrossPathPageTableRow row : grossPathRows) {
            //System.out.println("row="+row);
        }

    }


}
