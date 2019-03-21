package uk.ac.ebi.phenotype.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.mousephenotype.cda.file.stats.Stats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StatsClient {

    private static final String URL = "http://localhost:8080/stats?page={page}&size={size}";
    
    @Autowired
    RestTemplate template;//=new RestTemplate();
    
    
    public StatsClient(RestTemplate restTemplate) {
    	this.template=restTemplate;
    }

   

    public Stream<Stats> getStats(int offset, int limit) {
        Map<String, Integer> params = new HashMap<>();
        params.put("page", offset / limit);
        params.put("size", limit);

        // Using external class
        final ResponseEntity<StatsResources> statsResponse = template
                .getForEntity(URL, StatsResources.class, params);
System.out.println("statsResponse="+statsResponse);
        // Using instantiated ParametrizedTypeReference Resources 
        final ResponseEntity<Resources<Stats>> statsResponse2 = template
                .exchange(URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<Resources<Stats>>() {
                        }, params);
        System.out.println("statsResponse2="+statsResponse2);
        // Using instantiated ParametrizedTypeReference Resources
        final ResponseEntity<PagedResources<Stats>> statsResponse3 = template
                .exchange(URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<PagedResources<Stats>>() {
                        }, params);
        System.out.println("statsResponse3="+statsResponse3);
        // Does not work for some reason, ends up with empty Resources inside Resources
        // final ResponseEntity<Resources<Resource<Student>>> studentResponse = template
        //         .exchange(URL, HttpMethod.GET, null,
        //                 new TypeReferences.ResourcesType<Resource<Student>>() {
        //                 }, params);

        // Using provided PagedResources type class, note the required {}
        // This is used for return
        final ResponseEntity<PagedResources<Resource<Stats>>> statsResponse4 =
                template
                .exchange(URL, HttpMethod.GET, null,
                        new TypeReferences.PagedResourcesType<Resource<Stats>>(){},
                        params);

        return statsResponse4.getBody().getContent().stream()
                .map(Resource::getContent);
    }
}
