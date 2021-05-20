package org.mousephenotype.cda.datatests.repositories.solr;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.mousephenotype.cda.solr.web.dto.ExperimentsDataTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class ObservationServiceTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
    ObservationService observationService;


	@Test
	public void getObservationByProcedureNameAndGene(){

		String procedureName="Histopathology";
		String geneAccession="MGI:1891341";
		try {
			List<ObservationDTO> result = observationService.getObservationsByProcedureNameAndGene(procedureName, geneAccession);
			assert(result.size()>5);

		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getAllPhenotypesFromObservationsByGeneAccession(){

		//gene is Cib2
		String geneAccession="MGI:1929293";
		try {
			//map is parameterStableId to Experiment Row??
			Set<ExperimentsDataTableRow> rows = observationService.getAllPhenotypesFromObservationsByGeneAccession(geneAccession);

//				for(ExperimentsDataTableRow row:rows) {
//					System.out.println(row.getParameter().getStableId()+" "+row.getZygosity()+" female m "+row.getFemaleMutantCount()+" male m "+row.getMaleMutantCount());
//				}

			assertTrue(rows.size()>4);

		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void getGrossPathObservationByProcedureNameAndGene(){

		String procedureName="Gross Pathology and Tissue Collection";
		String geneAccession="MGI:2449119";
		try {
			List<ObservationDTO> result = observationService.getObservationsByProcedureNameAndGene(procedureName, geneAccession);
			assertTrue(result.size()>0);

		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetDistinctPipelineAlleleCenterListByGeneAccession() throws SolrServerException, IOException {

		// Arsk
		String acc = "MGI:1924291";
		List<Map<String, String>> dataMapList = observationService.getDistinctPipelineAlleleCenterListByGeneAccession(acc);

		logger.debug("datamaplist: " + dataMapList);
	}

	@Test
	public void testGetStatisticsForParameterFromCenter() throws SolrServerException, IOException {

		String parameter = "IMPC_CBC_010_001";
		List<FieldStatsInfo> centerParameterStatsList = observationService.getStatisticsForParameterFromCenter(parameter, null);
		assertTrue(centerParameterStatsList.size()>3);
	}
}
