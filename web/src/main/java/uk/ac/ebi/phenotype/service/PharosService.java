package uk.ac.ebi.phenotype.service;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.client.RestTemplate;

/**
 * Created by ilinca on 02/02/2017.
 */
@EnableRetry
public class PharosService {

    private static final String PHAROS_URL = "http://juniper.health.unm.edu/";
    private RestTemplate restTemplate;

    public PharosService(){

        HttpClient client = HttpClientBuilder.create().setMaxConnTotal(5).build();
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));

    }

    @Retryable
    public PharosDTO getPharosInfo(String humanGeneSymbol){

        //http://juniper.health.unm.edu/tcrd/api/target?q=%20{%22genesymb%22:%22GPER1%22}
        String url = PHAROS_URL + "tcrd/api/target?q={gene}";
        String gene = "{\"genesymb\":\"" + humanGeneSymbol + "\"}";
        PharosDTO[] res = restTemplate.getForObject(url, PharosDTO[].class, gene);

        if(res != null){

            if (res.length > 1){
                System.out.println("More than 1 PHAROS result for this gene!! " + url );
            }

            PharosDTO pharos = res[0];
            pharos.setPageLink("https://pharos.nih.gov/idg/targets/" + humanGeneSymbol);

            return pharos;

        } else {
            return null;
        }
    }




}
