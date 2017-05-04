package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.TestConfigSolr;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.HistopathPageTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfigSolr.class})
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
public class HistopathServiceTest {

	@Autowired
    GrossPathService grossService;
    @Test
    public void getTableDataTest() throws IOException, SolrServerException {
        //gene_accession_id:"MGI:2449119"
        //HistoPath_1481
        //looks like we don't pick up the associated pato term in the indexer e.g.
        //<ontologyParameter parameterID="IMPC_HIS_119_001" sequenceID="1">
        //<term>PATO:0001566:diffuse</term>
        //</ontologyParameter>

        HttpSolrClient solr = new HttpSolrClient("http://ves-ebi-d0.ebi.ac.uk:8090/mi/impc/dev/solr/experiment/");
        ObservationService observationService = new ObservationService(solr);
        HistopathService histopathService = new HistopathService(observationService);


        //String geneAccession = "MGI:2449119";
        String geneAccession="MGI:1891341";
        List<ObservationDTO> allObservations = histopathService.getObservationsForHistopathForGene(geneAccession);

        for (ObservationDTO obs : allObservations) {
            System.out.println(obs);
        }
        
        Map<String, List<ObservationDTO>> uniqueSampleSequeneAndAnatomyName = histopathService
				.getUniqueInfo(allObservations);

        List<HistopathPageTableRow> filteredObservations = histopathService.getTableData(uniqueSampleSequeneAndAnatomyName);
        for (HistopathPageTableRow row : filteredObservations) {
            //System.out.println("row="+row);
        }

    }


}
