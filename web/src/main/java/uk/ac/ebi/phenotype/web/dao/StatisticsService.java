package uk.ac.ebi.phenotype.web.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.mousephenotype.cda.db.pojo.StatisticalResult;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.mousephenotype.cda.solr.service.dto.ObservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.web.dao.Point;
import uk.ac.ebi.phenotype.web.dao.Statistics;
import uk.ac.ebi.phenotype.web.dao.StatisticsRepository;


/**
 * Service should be able to connect to a direct file Dao or a rest Dao
 * @author jwarren
 *
 */

@Service
public class StatisticsService {
//	@Autowired
//	StatsClient statsClient;
	
	//need to import the jar somehow or have the stats repo as a module in the PA???
//	@Autowired
	private final StatisticsRepository statsRepository;
	
	@Autowired
	public StatisticsService(StatisticsRepository statsRepository) {
		this.statsRepository=statsRepository;
	}
	
	public List<Statistics> findAll(){
		System.out.println("statsRepository="+statsRepository);
		return statsRepository.findByGeneSymbol("Arel1");
	}
	
	
//	public ResponseEntity<PagedResources<Stats>> getUniqueStatsResult(String geneAccession, String alleleAccession, String parameterStableId,
//			 String pipelineStableId,  String zygosity,  String phenotypingCenter,  String metaDataGroup){
//		
//		ResponseEntity<PagedResources<Stats>> stats=statsClient.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId,
//		 pipelineStableId,  zygosity,  phenotypingCenter,  metaDataGroup);
//		return stats;
//	
//		}
	

	
//	public ExperimentDTO getSpecificExperimentDTOFromRest(String parameterStableId, String pipelineStableId, String geneAccession, List<String> genderList, List<String> zyList, String phenotypingCenter, String strain, String metaDataGroup, String alleleAccession, String ebiMappedSolrUrl)
//	{
//		String zygosity=null;
//	
////		if(zyList.isEmpty()||zyList==null) {
////			zygosity=null;
////		}else {
////			
////		}
//		ResponseEntity<PagedResources<Stats>> response = this.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId, pipelineStableId, "homozygote", phenotypingCenter, metaDataGroup);
//		Collection<Stats> stats = response.getBody().getContent();
//		assert(stats.size()==1);
//		ExperimentDTO exp = convertToExperiment(parameterStableId, stats);
//		
//		System.out.println("experiment from file="+exp);
//		return exp;
//}
	
	
	public ExperimentDTO getSpecificExperimentDTOFromRepository(String parameterStableId, String pipelineStableId, String geneAccession, List<String> genderList, List<String> zyList, String phenotypingCenter, String strain, String metaDataGroup, String alleleAccession, String ebiMappedSolrUrl)
	{
		String zygosity=null;
	
//		if(zyList.isEmpty()||zyList==null) {
//			zygosity=null;
//		}else {
//			
//		}
		List<Statistics> stats = statsRepository.findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup(geneAccession, alleleAccession, parameterStableId, pipelineStableId, "homozygote", phenotypingCenter, metaDataGroup);
		assert(stats.size()>0);
		ExperimentDTO exp = StatisticsServiceUtilities.convertToExperiment(parameterStableId, stats);
		
		System.out.println("experiment from file="+exp);
		return exp;
}

	
	
	 /**
     * @return the dateOfExperiment
     */
    public Date getDateOfExperiment(Date dateOfExperiment) {
	    //        return dateOfExperiment;
    	if(dateOfExperiment==null){
    		return null;
    	}
	    ZonedDateTime zdt = ZonedDateTime.ofInstant(dateOfExperiment.toInstant(), ZoneId.of("UTC"));
	    if(TimeZone.getDefault().inDaylightTime(dateOfExperiment)) {
		    zdt = dateOfExperiment.toInstant().atZone(ZoneId.of(TimeZone.getDefault().getID()));
	    }
	    return Date.from(zdt.toLocalDateTime().toInstant(ZoneOffset.ofHours(0)));
    }

	
	/**
	 * just get the stats in order returned from the data source (findall in sping data)
	 * @param offset
	 * @param limit
	 * @return
	 */
//	public ResponseEntity<PagedResources<Stats>> getStatsData(int offset, int limit) {
//		
//		ResponseEntity<PagedResources<Stats>> stats = statsClient.getStats(offset, limit);
//		
//		return stats;
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
		
//	}
	
//	public ResponseEntity<PagedResources<Stats>> getStatsDataForGeneAccesssion(String geneAccession) {
//		return statsClient.getStatsDataForGeneAccession(geneAccession);
//		
//	}
	

//	public ResponseEntity<PagedResources<Stats>> getStatsDataForGeneSymbol(String geneSybmol) {
//		return statsClient.getStatsDataForGeneSymbol(geneSybmol);
//		
//	}

	
	
}
