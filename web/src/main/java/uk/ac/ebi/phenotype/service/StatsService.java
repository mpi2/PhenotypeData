package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.file.stats.Point;
import org.mousephenotype.cda.file.stats.Stats;
import org.mousephenotype.cda.file.stats.StatsRepository;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
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
	
	//need to import the jar somehow or have the stats repo as a module in the PA???
	@Autowired
	StatsRepository repo;
	
	public StatsService(StatsClient statsClient) {
		this.statsClient=statsClient;
	}
	
	public ResponseEntity<PagedResources<Stats>> getUniqueStatsResult(String geneAccession, String alleleAccession, String parameterStableId,
			 String pipelineStableId,  String zygosity,  String phenotypingCenter,  String metaDataGroup){
		
		ResponseEntity<PagedResources<Stats>> stats=statsClient.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId,
		 pipelineStableId,  zygosity,  phenotypingCenter,  metaDataGroup);
		return stats;
	
		}
	

	
	public ExperimentDTO getSpecificExperimentDTOFromRest(String parameterStableId, String pipelineStableId, String geneAccession, List<String> genderList, List<String> zyList, String phenotypingCenter, String strain, String metaDataGroup, String alleleAccession, String ebiMappedSolrUrl)
	{
		String zygosity=null;
	
//		if(zyList.isEmpty()||zyList==null) {
//			zygosity=null;
//		}else {
//			
//		}
		ResponseEntity<PagedResources<Stats>> response = this.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId, pipelineStableId, "homozygote", phenotypingCenter, metaDataGroup);
		Collection<Stats> stats = response.getBody().getContent();
		assert(stats.size()==1);
		ExperimentDTO exp = convertToExperiment(parameterStableId, stats);
		
		System.out.println("experiment from file="+exp);
		return exp;
}
	
	
	public ExperimentDTO getSpecificExperimentDTOFromRepository(String parameterStableId, String pipelineStableId, String geneAccession, List<String> genderList, List<String> zyList, String phenotypingCenter, String strain, String metaDataGroup, String alleleAccession, String ebiMappedSolrUrl)
	{
		String zygosity=null;
	
//		if(zyList.isEmpty()||zyList==null) {
//			zygosity=null;
//		}else {
//			
//		}
		List<Stats> stats = repo.findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup(geneAccession, alleleAccession, parameterStableId, pipelineStableId, "homozygote", phenotypingCenter, metaDataGroup);
		assert(stats.size()>0);
		ExperimentDTO exp = convertToExperiment(parameterStableId, stats);
		
		System.out.println("experiment from file="+exp);
		return exp;
}

	private ExperimentDTO convertToExperiment(String parameterStableId, Collection<Stats> stats) {
		Stats stat = stats.iterator().next();
		ExperimentDTO exp=new ExperimentDTO();
		exp.setAlleleAccession(stat.getAlleleAccession());
		exp.setMetadataGroup(stat.getMetaDataGroup());
		exp.setParameterStableId(parameterStableId);
		exp.setAlleleSymobl(stat.getAllele());
		String zygosity = stat.getZygosity();//only one zygosity per stats object which we can then set for all observations
		//loop over points and then asssing to observation types ??
		
		List<Point> allPoints = stat.getResult().getDetails().getPoints();
		Set<ObservationDTO> controls=new HashSet<>();
		for(Point point: allPoints) {
			String sampleType=point.getSampleType();
					String sex=	point.getSex();
					String value = point.getValue();
					Float bw=point.getBodyWeight();
					point.setSex(sex);
					point.setSampleType(sampleType);
					point.setValue(value);
					point.setBodyWeight(bw);
					
//					 if (ZygosityType.valueOf(point.getZygosity()).equals(ZygosityType.heterozygote)) {
//			                experiment.getHeterozygoteMutants().add(observation);
//			            } else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.homozygote)) {
//			                experiment.getHomozygoteMutants().add(observation);
//			            } else if (ZygosityType.valueOf(observation.getZygosity()).equals(ZygosityType.hemizygote)) {
//			                experiment.getHemizygoteMutants().add(observation);
//			            }
					
		}
		
		exp.setControls(controls);
		return exp;
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
