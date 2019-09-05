package uk.ac.ebi.phenotype.web.dao;

import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.ac.ebi.phenotype.stats.model.Statistics;

import javax.inject.Inject;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Service should be able to connect to a direct file Dao or a rest Dao
 * @author jwarren
 *
 */

@Service
public class StatisticsService {
	
	private StatsClient statsClient;


	@Inject
	public StatisticsService( StatsClient statsClient) {
		this.statsClient = statsClient;
	}
	
	
	public ResponseEntity<List<Statistics>> getUniqueStatsResult(String geneAccession, String alleleAccession, String parameterStableId,
			 String pipelineStableId,  String zygosity,  String phenotypingCenter,  String metaDataGroup){
		System.out.println("statsClient url="+statsClient.getStatsUrl());
		ResponseEntity<List<Statistics>> stats=statsClient.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId,
		 pipelineStableId,  zygosity,  phenotypingCenter,  metaDataGroup);
		return stats;
	
		}
	

	
	public ExperimentDTO getSpecificExperimentDTOFromRest(String parameterStableId, String pipelineStableId, String geneAccession, List<String> genderList, List<String> zyList, String phenotypingCenter, String strain, String metaDataGroup, String alleleAccession)
	{
		String zygosity=null;
		ExperimentDTO exp=null;
	
		ResponseEntity<List<Statistics>> response = this.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId, pipelineStableId, zyList.get(0), phenotypingCenter, metaDataGroup);
		//if(response.getStatusCode()==HttpStatus.OK) {
			List<Statistics> stats = response.getBody();
			System.out.println("stats size="+stats.size());
			if(stats.size()>0) {
				exp = StatisticsServiceUtilities.convertToExperiment(parameterStableId, stats.get(0));
			}
			if(stats.size()>1) {
				System.err.println("more than one stats result returned from Stats Service");
			}
				
		//}
		
		
		//System.out.println("experiment from file="+exp);
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

	
//	public ResponseEntity<PagedResources<Statistics>> getStatsDataForGeneAccesssion(String geneAccession) {
//		return statsClient.getStatsDataForGeneAccession(geneAccession);
//		
//	}
//	
//
//	public ResponseEntity<PagedResources<Statistics>> getStatsDataForGeneSymbol(String geneSybmol) {
//		return statsClient.getStatsDataForGeneSymbol(geneSybmol);
//		
//	}

	
	
}
