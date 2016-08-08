package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
public class ImageServiceTest {


    @Autowired
    private ImageService imageService;

    // Sring Configuration class
    // Only wire up the observation service for this test suite
    @Configuration
    @ComponentScan(
            basePackages = {"org.mousephenotype.cda"},
            useDefaultFilters = false,
            includeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {ImageService.class})
            })
    static class ContextConfiguration {

        @NotNull
        @Value("${solr.host}")
        private String solrBaseUrl;

        @Bean(name = "impcImagesCore")
        HttpSolrClient getExperimentCore() {
            return new HttpSolrClient(solrBaseUrl + "/impc_images");
        }

        @Bean
        public static PropertySourcesPlaceholderConfigurer propertyConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }

    }


    @Test
    public void testgetImagePropertiesThatHaveMp() throws IOException, SolrServerException {

        String acc = "MGI:1913955";
        Map<String, Set<String>> mpToColonies = imageService.getImagePropertiesThatHaveMp(acc);
        assertTrue(mpToColonies.size() > 0);
        for (String mp : mpToColonies.keySet()) {
            System.out.println(mp);
            for (String colony : mpToColonies.get(mp)) {
                System.out.println("colony=" + colony);
            }
        }

    }

    @Test
    public void testgetImagesForGeneByParameter() throws IOException, SolrServerException {
        //expression test first
        String acc = "MGI:1336993";
        String parameterStableId = "IMPC_ELZ_064_001";
        String anatomyId = "EMAPA:16105";
        String parameterAsscociationValue = "ambiguous";
        QueryResponse response = null;
        response = imageService.getImagesForGeneByParameter(acc, parameterStableId, "experimental", 100000, null, null, null, anatomyId, parameterAsscociationValue, null, null);

        long resultsSize = response.getResults().size();
        System.out.println("resultsSize=" + resultsSize);
        assertTrue(resultsSize > 12);

    }

}
