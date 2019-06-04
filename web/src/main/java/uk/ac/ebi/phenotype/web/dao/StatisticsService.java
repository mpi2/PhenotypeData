package uk.ac.ebi.phenotype.web.dao;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;

import org.mousephenotype.cda.solr.service.dto.ExperimentDTO;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import uk.ac.ebi.phenotype.stats.model.Statistics;

//import temp.Statistics;



/**
 * Service should be able to connect to a direct file Dao or a rest Dao
 * @author jwarren
 *
 */
public class StatisticsService {
	
	StatsClient statsClient;
	
	//need to import the jar somehow or have the stats repo as a module in the PA???
	//@Inject
	public StatisticsService(String statsUrl) {
		this.statsClient=new StatsClient(statsUrl);
	}
	
	
	public ResponseEntity<List<Statistics>> getUniqueStatsResult(String geneAccession, String alleleAccession, String parameterStableId,
			 String pipelineStableId,  String zygosity,  String phenotypingCenter,  String metaDataGroup){
		System.out.println("statsClient url="+statsClient.getStatsUrl());
		ResponseEntity<List<Statistics>> stats=statsClient.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId,
		 pipelineStableId,  zygosity,  phenotypingCenter,  metaDataGroup);
		return stats;
	
		}
	

	
	public ExperimentDTO getSpecificExperimentDTOFromRest(String parameterStableId, String pipelineStableId, String geneAccession, List<String> genderList, List<String> zyList, String phenotypingCenter, String strain, String metaDataGroup, String alleleAccession, String ebiMappedSolrUrl)
	{
		String zygosity=null;
		ExperimentDTO exp=null;
	
		ResponseEntity<List<Statistics>> response = this.getUniqueStatsResult(geneAccession, alleleAccession, parameterStableId, pipelineStableId, zyList.get(0), phenotypingCenter, metaDataGroup);
		//if(response.getStatusCode()==HttpStatus.OK) {
			List<Statistics> stats = response.getBody();
			//System.out.println("stats size="+stats.size());
			if(stats.size()>0) {
				exp = StatisticsServiceUtilities.convertToExperiment(parameterStableId, stats.get(0));
			}
				
		//}
		
		
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

	
	public ResponseEntity<PagedResources<Statistics>> getStatsDataForGeneAccesssion(String geneAccession) {
		return statsClient.getStatsDataForGeneAccession(geneAccession);
		
	}
	

	public ResponseEntity<PagedResources<Statistics>> getStatsDataForGeneSymbol(String geneSybmol) {
		return statsClient.getStatsDataForGeneSymbol(geneSybmol);
		
	}

	
	
}
