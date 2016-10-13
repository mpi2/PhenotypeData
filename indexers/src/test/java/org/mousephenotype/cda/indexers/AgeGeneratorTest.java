package org.mousephenotype.cda.indexers;

import static org.junit.Assert.assertTrue;


import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.solr.service.GrossPathService;
import org.mousephenotype.cda.solr.service.ObservationService;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

//import org.springframework.boot.test.context.web.*;
import org.springframework.boot.test.web.client.*;

@RunWith(SpringRunner.class)
//@SpringBootTest(WebEnvironment.RANDOM_PORT, classes=TestConfigIndexers.class)
//@TestPropertySource(locations = {"file:${user.home}/configfiles/${profile:dev}/test.properties"})
public class AgeGeneratorTest {
	
	// "date_of_experiment": "2016-03-02T00:00:00Z",
    //"date_of_birth": "2015-11-12T00:00:00Z",	
	
	@Test
	public void testGetEmbryoAge(){
		List<ObservationDTO> results=null;
		
			ObservationService obsService=new ObservationService();
			
		
		
		assertTrue(results.size()>0);
	}
	
}
