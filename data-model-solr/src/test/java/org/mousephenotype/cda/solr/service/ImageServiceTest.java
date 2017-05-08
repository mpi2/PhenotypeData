package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
public class ImageServiceTest {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

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
            logger.debug(mp);
            for (String colony : mpToColonies.get(mp)) {
                logger.debug("colony=" + colony);
            }
        }

	}
	
	@Test
	public void testgetImagesForGeneByParameter() throws IOException, SolrServerException{
		//expression test first
		String acc="MGI:1336993";
		String parameterStableId="IMPC_ELZ_064_001";
		String anatomyId="EMAPA:16105";
		String parameterAsscociationValue="ambiguous";
		QueryResponse response =null;
			response = imageService.getImagesForGeneByParameter(acc, parameterStableId, "experimental", 100000, null, null, null, anatomyId, parameterAsscociationValue, null, null);
			long resultsSize=response.getResults().size();
			logger.debug("resultsSize="+resultsSize);
			assertTrue("Expected at least 12 results. Actual count: " + resultsSize, resultsSize > 12);

	}

	@Test
	public void testGetPhenotypeAssociatedImages() throws IOException, SolrServerException{

        final int expectedSize = 8;
		String acc="MGI:1891341";       //should be 8 parameters for this gene at least.
        int rows = 1;
        List<Group> response = imageService.getPhenotypeAssociatedImages(acc, null, null, true, rows);
        int size = (response != null ? response.size() : 0);

        assertTrue("Expected at least " + expectedSize + " parameters. Found " + size, size >= 8);

	}
}