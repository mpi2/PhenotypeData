package uk.ac.ebi.phenotype.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mousephenotype.cda.file.stats.Stats;
import org.mousephenotype.cda.file.stats.StatsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


/**
 * Service should be able to connect to a direct file Dao or a rest Dao
 * @author jwarren
 *
 */

@Service
public class StatsService {
	@Autowired
	StatsClient statsClient;
	
	public StatsService(StatsClient statsClient) {
		this.statsClient=statsClient;
	}
	
	public List<Stats> getTestStatsData() {
		
		Stream<Stats> stats = statsClient.getStats(0, 2);
//		RestTemplate restTemplate = new RestTemplate();
//		ResponseEntity<List<Stats>> response = restTemplate.exchange(
//		  "http://localhost:8080/stats",
//		  HttpMethod.GET,
//		  null,
//		  new ParameterizedTypeReference<List<Stats>>(){});
//		System.out.println("response body="+response.getBody());
//		List<Stats> statsList = response.getBody();
		
//		RestTemplate restTemplate = new RestTemplate();
//        StatsList statsList=null;
//		try {
//			statsList = restTemplate.getForObject("http://localhost:8080/stats", StatsList.class);
//		} catch (RestClientException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//        System.out.println("stats parameter="+statsList);//.getStats().get(0).getParameterStableId()
//		
        
        
        List<Stats> statsList = stats.collect(Collectors.toList());
        
		return statsList;
		
	}

}
