package uk.ac.ebi.phenotype.service;

import java.util.Collection;
import java.util.List;

import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.file.stats.Stats;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


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
	
	public ResponseEntity<PagedResources<Stats>> getUniqueStatsResult(String geneAccession, String alleleAccession, String parameterStableId,
			 String pipelineStableId,  String zygosity,  String phenotypingCenter,  String metaDataGroup){
		
		ResponseEntity<PagedResources<Stats>> stats=statsClient.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId,
		 pipelineStableId,  zygosity,  phenotypingCenter,  metaDataGroup);
		return stats;
	
		}
	

	
	public ExperimentDTO getSpecificExperimentDTO(String parameterStableId, String pipelineStableId, String geneAccession, List<String> genderList, List<String> zyList, String phenotypingCenter, String strain, String metaDataGroup, String alleleAccession, String ebiMappedSolrUrl)
	{
		String zygosity=null;
		ExperimentDTO experimentDTO=new ExperimentDTO();
//		if(zyList.isEmpty()||zyList==null) {
//			zygosity=null;
//		}else {
//			
//		}
		ResponseEntity<PagedResources<Stats>> response = this.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId, pipelineStableId, "homozygote", phenotypingCenter, metaDataGroup);
		Collection<Stats> stats = response.getBody().getContent();
		assert(stats.size()==1);
		Stats first = stats.iterator().next();
		experimentDTO.setAlleleAccession(first.getAlleleAccession());
		experimentDTO.setMetadataGroup(first.getMetaDataGroup());
		experimentDTO.setParameterStableId(parameterStableId);
		experimentDTO.setAlleleSymobl(first.getAllele());
		System.out.println("experiment from file="+experimentDTO);
		return experimentDTO;
}
	
	/**
	 * just get the stats in order returned from the data source (findall in sping data)
	 * @param offset
	 * @param limit
	 * @return
	 */
	public ResponseEntity<PagedResources<Stats>> getStatsData(int offset, int limit) {
		
		ResponseEntity<PagedResources<Stats>> stats = statsClient.getStats(offset, limit);
		
		return stats;
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
		
	}
	
	public ResponseEntity<PagedResources<Stats>> getStatsDataForGeneAccesssion(String geneAccession) {
		return statsClient.getStatsDataForGeneAccession(geneAccession);
		
	}
	

	public ResponseEntity<PagedResources<Stats>> getStatsDataForGeneSymbol(String geneSybmol) {
		return statsClient.getStatsDataForGeneSymbol(geneSybmol);
		
	}

	
	
}
