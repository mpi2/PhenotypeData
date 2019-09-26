package uk.ac.ebi.phenotype.web.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.ac.ebi.phenotype.service.RestConfiguration;
import uk.ac.ebi.phenotype.stats.model.Statistics;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@ComponentScan
public class StatsClient {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Value("${statistics_url}")
	private String statisticsUrl;

//	private  String statisticsUrl;//"http://localhost:8080/
    private String repositoryPath="stats";//added to statsiticsUrl to get the repository
    private String apiPath="api/stats?";//added to statistics url to reach the controller for our specific API
    //private static final String SINGLE_STATS_URL = "http://localhost:8080/statisticses/search/findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup?geneAccession={geneAccession}&alleleAccession={alleleAccession}&parameterStableId={parameterStableId}&pipelineStableId={pipelineStableId}&zygosity={zygosity}&phenotypingCenter={phenotypingCenter}&metaDataGroup={metaDataGroup}"; 
    
    RestTemplate template;
    
	@Inject
    public StatsClient() {
    	RestConfiguration restConfiguration=new RestConfiguration();
		RestTemplateBuilder builder=new RestTemplateBuilder();
		template = restConfiguration.restTemplate(builder);
    }
    
    public ResponseEntity<List<Statistics>> getUniqueStatsResult(String geneAccession, String alleleAccession, String parameterStableId,
   		 String pipelineStableId,  String zygosity,  String phenotypingCenter,  String metaDataGroup){
    	//http://localhost:8080/stats/search/findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup?geneAccession=MGI:2443170&alleleAccession=MGI:2159965&parameterStableId=IMPC_HEM_038_001&pipelineStableId=IMPC_001&zygosity=homozygote&phenotypingCenter=MARC&metaDataGroup=08aa37a898ab923b9ffdbd01c0077040
    ResponseEntity<List<Statistics>> statsResponse=null;
    logger.debug("SINGLE_STATS_URL="+statisticsUrl+apiPath+"accession="+geneAccession+"&parameter_stable_id="+parameterStableId+"&pipeline_stable_id="+pipelineStableId+"&zygosity="+zygosity+"&phenotyping_center="+phenotypingCenter+"&metadata_group="+metaDataGroup);
    	//String SINGLE_STATS_URL = statisticsUrl+"api/singleStatistic?accession={geneAccession}&allele_accession_id={alleleAccession}&parameter_stable_id={parameterStableId}&pipeline_stable_id={pipelineStableId}&zygosity={zygosity}&phenotyping_center={phenotypingCenter}&metadata_group={metaDataGroup}";
    String SINGLE_STATS_URL = statisticsUrl+"api/stats?accession={geneAccession}&allele_accession_id={alleleAccession}&parameter_stable_id={parameterStableId}&pipeline_stable_id={pipelineStableId}&zygosity={zygosity}&phenotyping_center={phenotypingCenter}&metadata_group={metaDataGroup}";
    
    	System.out.println("SINGLE_STATS_URL="+SINGLE_STATS_URL);
		try {
			Map<String, String> params = new HashMap<>();
			    params.put("geneAccession", geneAccession);
			    params.put("alleleAccession", alleleAccession);//ignore allele accession until in stats file and service
			    params.put("parameterStableId", parameterStableId);
			    params.put("pipelineStableId", pipelineStableId);
			    params.put("zygosity", zygosity);
			    params.put("phenotypingCenter", phenotypingCenter);
			    params.put("metaDataGroup", metaDataGroup);
			statsResponse = template
			        .exchange(SINGLE_STATS_URL, HttpMethod.GET, null,
			                new ParameterizedTypeReference<List<Statistics>>() {
			                }, params);
			System.out.println("singleStatsResponse="+statsResponse);
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return statsResponse;
	
    }
    
    public ResponseEntity<PagedResources<Statistics>> findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosity(String geneAccession, String alleleAccession, String parameterStableId,
      		 String pipelineStableId, String zygosity){
       	//http://localhost:8080/stats/search/findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup?geneAccession=MGI:2443170&alleleAccession=MGI:2159965&parameterStableId=IMPC_HEM_038_001&pipelineStableId=IMPC_001&zygosity=homozygote&phenotypingCenter=MARC&metaDataGroup=08aa37a898ab923b9ffdbd01c0077040
    	String HALF_STATS_URL = statisticsUrl+repositoryPath+"/search/findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup?geneAccession={geneAccession}&alleleAccession={alleleAccession}&parameterStableId={parameterStableId}&pipelineStableId={pipelineStableId}&zygosity={zygosity}&phenotypingCenter={phenotypingCenter}&metaDataGroup={metaDataGroup}";
    	   
    	ResponseEntity<PagedResources<Statistics>> statsResponse=null;
   		try {
   			Map<String, String> params = new HashMap<>();
   			    params.put("geneAccession", geneAccession);
   			    params.put("alleleAccession", alleleAccession);
  			    params.put("parameterStableId", parameterStableId);
  			    params.put("pipelineStableId", pipelineStableId);
   			    params.put("zygosity", zygosity);
   			 
   			statsResponse = template
   			        .exchange(HALF_STATS_URL, HttpMethod.GET, null,
   			                new ParameterizedTypeReference<PagedResources<Statistics>>() {
   			                }, params);
   			System.out.println("singleStatsResponse="+statsResponse);
   		} catch (RestClientException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
           return statsResponse;
   	
       }
    
    public ResponseEntity<PagedResources<Statistics>> findByGeneAccessionAndAlleleAccessionAndParameterStableId(String geneAccession, String alleleAccession, String parameterStableId){
     		// String pipelineStableId, String zygosity){
      	//http://localhost:8080/stats/search/findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup?geneAccession=MGI:2443170&alleleAccession=MGI:2159965&parameterStableId=IMPC_HEM_038_001&pipelineStableId=IMPC_001&zygosity=homozygote&phenotypingCenter=MARC&metaDataGroup=08aa37a898ab923b9ffdbd01c0077040
   	String HALF_STATS_URL = statisticsUrl+repositoryPath+"/search/findByGeneAccessionAndAlleleAccessionAndParameterStableId?geneAccession={geneAccession}&alleleAccession={alleleAccession}&parameterStableId={parameterStableId}";//&pipelineStableId={pipelineStableId}&zygosity={zygosity}&phenotypingCenter={phenotypingCenter}&metaDataGroup={metaDataGroup}";";
   			//+ "AndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup?geneAccession={geneAccession}&alleleAccession={alleleAccession}&parameterStableId={parameterStableId}&pipelineStableId={pipelineStableId}&zygosity={zygosity}&phenotypingCenter={phenotypingCenter}&metaDataGroup={metaDataGroup}";
   	   
   	ResponseEntity<PagedResources<Statistics>> statsResponse=null;
  		try {
  			Map<String, String> params = new HashMap<>();
  			    params.put("geneAccession", geneAccession);
  			    params.put("alleleAccession", alleleAccession);
 			    params.put("parameterStableId", parameterStableId);
 			    //params.put("pipelineStableId", pipelineStableId);
  			    //params.put("zygosity", zygosity);
  			 
  			statsResponse = template
  			        .exchange(HALF_STATS_URL, HttpMethod.GET, null,
  			                new ParameterizedTypeReference<PagedResources<Statistics>>() {
  			                }, params);
  			System.out.println("singleStatsResponse="+statsResponse);
  		} catch (RestClientException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
          return statsResponse;
      }

    public ResponseEntity<PagedResources<Statistics>> getStats(int offset, int limit) {
    	
    	String URL=statisticsUrl+repositoryPath+"?page={page}&size={size}";
        Map<String, Integer> params = new HashMap<>();
        params.put("page", offset / limit);
        params.put("size", limit);

        // Using external class
//        final ResponseEntity<StatsResources> statsResponse = template
//                .getForEntity(URL, StatsResources.class, params);
//System.out.println("statsResponse="+statsResponse);
        // Using instantiated ParametrizedTypeReference Resources 
//        final ResponseEntity<Resources<Stats>> statsResponse2 = template
//                .exchange(URL, HttpMethod.GET, null,
//                        new ParameterizedTypeReference<Resources<Stats>>() {
//                        }, params);
//        System.out.println("statsResponse2="+statsResponse2);
        // Using instantiated ParametrizedTypeReference Resources
        final ResponseEntity<PagedResources<Statistics>> statsResponse3 = template
                .exchange(URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<PagedResources<Statistics>>() {
                        }, params);
        System.out.println("statsResponse3="+statsResponse3);
        return statsResponse3;

        // Using provided PagedResources type class, note the required {}
        // This is used for return
//        final ResponseEntity<PagedResources<Resource<Stats>>> statsResponse4 =
//                template
//                .exchange(URL, HttpMethod.GET, null,
//                        new TypeReferences.PagedResourcesType<Resource<Stats>>(){},
//                        params);
//
//        return statsResponse4.getBody().getContent().stream()
//                .map(Resource::getContent);
    }

    
    public ResponseEntity<PagedResources<Statistics>> getStatsDataForGeneAccession(String geneAccession) {
    	String GENE_ACCESSION_URL = statisticsUrl+repositoryPath+"/search/findByGeneAccession?geneAccession={geneAccession}";
		 ResponseEntity<PagedResources<Statistics>> statsResponse=null;
		try {
			Map<String, String> params = new HashMap<>();
			    params.put("geneAccession", geneAccession);
			statsResponse = template
			        .exchange(GENE_ACCESSION_URL, HttpMethod.GET, null,
			                new ParameterizedTypeReference<PagedResources<Statistics>>() {
			                }, params);
			System.out.println("statsResponse3="+statsResponse);
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       return statsResponse;
	}
    


	public ResponseEntity<PagedResources<Statistics>> getStatsDataForGeneSymbol(String geneSymbol) {
		String GENEURL =statisticsUrl+repositoryPath+"/search/findByGeneSymbol?geneSymbol={geneSymbol}";
		 ResponseEntity<PagedResources<Statistics>> statsResponse=null;
		try {
			Map<String, String> params = new HashMap<>();
			    params.put("geneSymbol", geneSymbol);
			statsResponse = template
			        .exchange(GENEURL, HttpMethod.GET, null,
			                new ParameterizedTypeReference<PagedResources<Statistics>>() {
			                }, params);
			System.out.println("statsResponse3="+statsResponse);
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return statsResponse;
	}

	public String getStatsUrl() {
		return this.statisticsUrl;
	}
}