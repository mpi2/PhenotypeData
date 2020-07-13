package org.mousephenotype.cda.solr.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class HtgtDataAccess {
RestTemplate restTemplate=new RestTemplate();
    private String dataUrl="https://www.gentar.org/htgt/console";//data url for graphQl endpoint set up by RW

    DesignsResponse getDesigns(int designId){
        //String query = "\n    query Topology($duration: Duration!) {\n      getGlobalTopology(duration: $duration) {\n        nodes {\n          id\n          name\n          type\n          isReal\n        }\n        calls {\n          id\n          source\n          target\n          callType\n          detectPoint\n        }\n      }\n    }\n  ";
        String query="query MyQuery {\n"+
                "  oligos (where: {design_id: {_eq: "+designId+"}}){\n"+
                "    id, strand, chr, design_id, assembly, feature_type, oligo_sequence, oligo_start, oligo_stop\n"+
                "  }\n"+
                "}";
        GraphQLQuery topologyQuery = new GraphQLQuery();
// Use a singletonMap to retain the object name
//topologyQuery.setVariables(Collections.singletonMap("duration", duration));
        System.out.println("query="+query);
        topologyQuery.setQuery(query);
        ResponseEntity<DesignsResponse> designsResponse = restTemplate.postForEntity("https://www.gentar.org/htgt/v1/graphql", topologyQuery, DesignsResponse.class);
        System.out.println(designsResponse);
        return designsResponse.getBody();
    }


public static void main(String [] args){
HtgtDataAccess access=new HtgtDataAccess();
DesignsResponse design=access.getDesigns(1);
System.out.println("Get oligo number 1="+design.getData().getOligos().get(0));


}



}
