package org.mousephenotype.cda.indexers;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.indexers.utils.PhisService;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

//import org.springframework.web.client.RestTemplate;
@RunWith(SpringJUnit4ClassRunner.class)

public class PhisServiceTest {

	
	@Test
    public void testPhisRestGetter() {
		//url from brain histopath
		//http://ves-ebi-d2.ebi.ac.uk:8140/mi/phis/v1.0.3/images/select?q=host_name:WTSI&rows=200
		PhisService phisService=new PhisService();
		try {
			Map<String, Set<String>> primaryGenesProcedures = new HashMap<>();
			List<ImageDTO> imageDtos = phisService.getPhenoImageShareImageDTOs(primaryGenesProcedures);
			assertTrue(imageDtos.size()>162);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
        //RestTemplate restTemplate = new RestTemplate();
       // PhisRestWrapper contentWrapper = restTemplate.getForObject("http://www.phenoimageshare.org/data/v1.0.3/rest/getImages?hostName=WTSI&resNo=200", PhisRestWrapper.class);
        //System.out.println("pimage="+contentWrapper);
    }
}
