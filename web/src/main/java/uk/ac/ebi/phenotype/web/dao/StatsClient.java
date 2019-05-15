package uk.ac.ebi.phenotype.web.dao;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import uk.ac.ebi.phenotype.web.dao.Statistics;



@Component
public class StatsClient {
	
	

	//@Value("${statistics_url:http://localhost:8080/statisticses}")
    public String statisticsUrl="http://localhost:8080/statisticses";//"http://localhost:8080/statisticses"; //"http://localhost:8080/statisticses?page={page}&size={size}";
    private  String URL=statisticsUrl+"?page={page}&size={size}";
    
    private  String GENEURL = statisticsUrl+"/search/findByGeneSymbol?geneSymbol={geneSymbol}";
    
    private  String GENE_ACCESSION_URL = statisticsUrl+"/search/findByGeneAccession?geneAccession={geneAccession}";
    
    //private static final String SINGLE_STATS_URL = "http://localhost:8080/statisticses/search/findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup?geneAccession={geneAccession}&alleleAccession={alleleAccession}&parameterStableId={parameterStableId}&pipelineStableId={pipelineStableId}&zygosity={zygosity}&phenotypingCenter={phenotypingCenter}&metaDataGroup={metaDataGroup}";
    private  String SINGLE_STATS_URL = statisticsUrl+"/search/findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup?geneAccession=\"{geneAccession}\"&alleleAccession=\"{alleleAccession}\"&parameterStableId={parameterStableId}&pipelineStableId={pipelineStableId}&zygosity={zygosity}&phenotypingCenter={phenotypingCenter}&metaDataGroup={metaDataGroup}";
    
    private  String HALF_STATS_URL = statisticsUrl+"/search/findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup?geneAccession={geneAccession}&alleleAccession={alleleAccession}&parameterStableId={parameterStableId}&pipelineStableId={pipelineStableId}&zygosity={zygosity}&phenotypingCenter={phenotypingCenter}&metaDataGroup={metaDataGroup}";
    
    @Autowired
    RestTemplate template;//=new RestTemplate();
    
    
    public StatsClient(RestTemplate restTemplate) {
    	this.template=restTemplate;
    }
    
    public ResponseEntity<PagedResources<Statistics>> getUniqueStatsResult(String geneAccession, String alleleAccession, String parameterStableId,
   		 String pipelineStableId,  String zygosity,  String phenotypingCenter,  String metaDataGroup){
    	//http://localhost:8080/stats/search/findByGeneAccessionAndAlleleAccessionAndParameterStableIdAndPipelineStableIdAndZygosityAndPhenotypingCenterAndMetaDataGroup?geneAccession=MGI:2443170&alleleAccession=MGI:2159965&parameterStableId=IMPC_HEM_038_001&pipelineStableId=IMPC_001&zygosity=homozygote&phenotypingCenter=MARC&metaDataGroup=08aa37a898ab923b9ffdbd01c0077040
    	ResponseEntity<PagedResources<Statistics>> statsResponse=null;
		try {
			Map<String, String> params = new HashMap<>();
			    params.put("geneAccession", geneAccession);
			    params.put("alleleAccession", alleleAccession);
			    params.put("parameterStableId", parameterStableId);
			    params.put("pipelineStableId", pipelineStableId);
			    params.put("zygosity", zygosity);
			    params.put("phenotypingCenter", phenotypingCenter);
			    params.put("metaDataGroup", metaDataGroup);
			statsResponse = template
			        .exchange(SINGLE_STATS_URL, HttpMethod.GET, null,
			                new ParameterizedTypeReference<PagedResources<Statistics>>() {
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


   

    public ResponseEntity<PagedResources<Statistics>> getStats(int offset, int limit) {
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
}
