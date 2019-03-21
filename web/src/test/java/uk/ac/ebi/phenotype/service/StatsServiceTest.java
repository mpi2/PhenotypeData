package uk.ac.ebi.phenotype.service;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.mousephenotype.cda.config.TestConfigIndexers;
import org.mousephenotype.cda.file.stats.Stats;
import org.mousephenotype.cda.file.stats.StatsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringJUnit4ClassRunner.class)
//@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
//@SpringBootTest(classes = {TestConfigIndexers.class})
public class StatsServiceTest {

	
	
	private StatsService statsService;
	
	RestTemplate restTemplate;

	@Before
	public void setUp() throws Exception {
		
		RestConfiguration restConfiguration=new RestConfiguration();
		RestTemplateBuilder builder=new RestTemplateBuilder();
		restTemplate = restConfiguration.restTemplate(builder);
		StatsClient client=new StatsClient(restTemplate);
		statsService=new StatsService(client);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTestStatsData() {
		List<Stats> statsList=null;
		try {
			statsList = statsService.getTestStatsData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(statsList);
		//assert(statsList.getStats().size()>0);
	}

}
