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
		ExperimentDTO exp = convertToExperiment(parameterStableId, stats);
		
		System.out.println("experiment from file="+exp);
		return exp;
}

	public ExperimentDTO convertToExperiment(String parameterStableId, Collection<Statistics> stats) {
		Statistics stat = stats.iterator().next();
		ExperimentDTO exp = new ExperimentDTO();
		exp.setAlleleAccession(stat.getAlleleAccession());
		exp.setMetadataGroup(stat.getMetaDataGroup());
		exp.setParameterStableId(parameterStableId);
		exp.setProcedureStableId(stat.getProcedureStableId());
		exp.setAlleleSymobl(stat.getAllele());
		// question will this code suffice - probably not - need to set for both stats
		// files in one experiment DTO?
		String zygosity = stat.getZygosity();// only one zygosity per stats object which we can then set for all
												// observations
		// loop over points and then asssing to observation types ??
		ZygosityType zyg = ZygosityType.valueOf(stat.getZygosity());
		Set<ZygosityType> zygs = new HashSet();
		zygs.add(zyg);
		exp.setZygosities(zygs);

		List<Point> allPoints = stat.getResult().getDetails().getPoints();
		Set<ObservationDTO> controls = new HashSet<>();
		Set<ObservationDTO> mutants = new HashSet<>();

		exp.setControls(new HashSet<ObservationDTO>());
		exp.setHomozygoteMutants(new HashSet<ObservationDTO>());
		exp.setHeterozygoteMutants(new HashSet<ObservationDTO>());
		exp.setHemizygoteMutants(new HashSet<ObservationDTO>());
		Set<SexType> sexes = new HashSet<>();

		for (Point point : allPoints) {
			ObservationDTO obs = new ObservationDTO();
			String sampleType = point.getSampleType();
			obs.setObservationType(sampleType);
			String sex = point.getSex();
			obs.setSex(sex);
			String value = point.getValue();
			obs.setDataPoint(Float.valueOf(value));

			Float bw = point.getBodyWeight();
			obs.setWeight(bw);
			obs.setSex(sex);

			sexes.add(SexType.valueOf(sex));// question are all stats files for both??? if so setting it here???
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			if (point.getDateOfExperiment() != null) {
				try {
					obs.setDateOfExperiment(dateFormat.parse(point.getDateOfExperiment()));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (sampleType.equals("control")) {
				exp.getControls().add(obs);
			} else if (sampleType.equals("experimental")) {
				obs.setZygosity(zygosity);
				if (zyg.equals(ZygosityType.heterozygote)) {
					exp.getHeterozygoteMutants().add(obs);
				} else if (ZygosityType.valueOf(obs.getZygosity()).equals(ZygosityType.homozygote)) {
					exp.getHomozygoteMutants().add(obs);
				} else if (ZygosityType.valueOf(obs.getZygosity()).equals(ZygosityType.hemizygote)) {
					exp.getHemizygoteMutants().add(obs);
				}

			}

		}
		exp.setSexes(sexes);

		List<? extends StatisticalResult> results = new ArrayList<>();
		StatisticalResult result = new StatisticalResult();
		// result.set
		exp.setResults(results);
		// question - hard coding this here until hamed puts in a field of
		// observation_type in the stats file in next version...
		exp.setObservationType(ObservationType.unidimensional);
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
