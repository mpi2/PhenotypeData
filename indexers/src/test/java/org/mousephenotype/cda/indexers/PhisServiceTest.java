package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.indexers.utils.PhisService;
import org.mousephenotype.cda.solr.service.ImpressService;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {IndexersTestConfig.class})
public class PhisServiceTest {

	@Autowired
	ImpressService impressService;

	@Test
    public void testPhisRestGetter() {
		//url from brain histopath http://ves-ebi-d2.ebi.ac.uk:8140/mi/phis/v1.0.3/images/select?q=host_name:WTSI&rows=200
		PhisService phisService=new PhisService();
		try {
			Map<String, Set<String>> primaryGenesProcedures = new HashMap<>();
			List<ImageDTO> imageDtos = phisService.getPhenoImageShareImageDTOs(primaryGenesProcedures, impressService);
			assertTrue(imageDtos.size() > 162);
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
		}
    }
}
