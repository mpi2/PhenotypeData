package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.enumerations.Expression;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.enumerations.ZygosityType;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
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
	public void testGetImagePropertiesThatHaveMp() throws IOException, SolrServerException {

		String                   acc          = "MGI:1913955";
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
	public void testGetImagesForGeneByParameter() throws IOException, SolrServerException {
		//expression test first
		String        acc                        = "MGI:1336993";
		String        parameterStableId          = "IMPC_ELZ_064_001";
		String        anatomyId                  = "EMAPA:16105";
		String        parameterAsscociationValue = "ambiguous";
		QueryResponse response                   = null;
		response = imageService.getImages(acc, parameterStableId, "experimental", 100000, null, null, null, anatomyId, parameterAsscociationValue, null, null);
		int    expectedSize = 12;
		int    actualSize   = response.getResults().size();
		String message      = "Expected at least " + expectedSize + " images but found " + actualSize;
		assertTrue(message, expectedSize >= actualSize);

	}

	@Test
	public void testGetPhenotypeAssociatedImages() throws IOException, SolrServerException {

		String      acc          = "MGI:1891341";       //should be 8 parameters for this gene at least.
		int         rows         = 1;
		List<Group> response     = imageService.getPhenotypeAssociatedImages(acc, null, null, true, rows);
		int         expectedSize = 8;
		int         actualSize   = (response != null ? response.size() : 0);
		String      message      = "Expected at least " + expectedSize + " but found " + actualSize;
		assertTrue(message, expectedSize >= actualSize);

	}

	@Test
	public void testGetComparisonViewerMethodsWithNulls() {
		String         acc                       = null;
		int            numberOfControlsPerSex    = 10;
		String         anatomyId                 = null;
		String         parameterStableId         = null;
		SexType        sex                       = null;
		ImageDTO       imgDoc                    = null;
		List<ImageDTO> controlImages             = null;
		String         organ                     = null;
		Expression     expression                = null;
		String         parameterAssociationValue = null;
		String         zygosity                  = null;
		String         colonyId                  = null;
		String         mpId                      = null;

		try {
			controlImages = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sex, parameterStableId, anatomyId, null);
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//no information has been given so we would expect null or an empty array - empty array safest
		assertTrue(controlImages.size() == 0);


		List<ImageDTO> mutantImages = null;
		try {
			mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex, organ);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(mutantImages.size() > 0);//currently we are returning everything mutatn related if no filters
	}
	
	@Test
	public void testGetComparisonViewerMethodsWithExpression(){
		String acc="MGI:109331";
		int numberOfControlsPerSex=10;
		String anatomyId="MA:0000327";
		String parameterStableId=null;
		SexType sex=null;
		ImageDTO imgDoc=null;
		List<ImageDTO> controlImages=null;
		String organ=null;
		Expression expression=Expression.EXPRESSION;
		String parameterAssociationValue=expression.getDisplayName();
		String zygosity=null;
		String colonyId=null;
		String mpId=null;
		
		try {
			controlImages = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sex, parameterStableId, anatomyId, expression.getDisplayName());
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//no information has been given so we would expect null or an empty array - empty array safest
		int expectedSize = 3;
		int actualSize = controlImages.size();
		String message = "Expected at least " + expectedSize + " control images but found " + actualSize;
		assertTrue(message, actualSize >= expectedSize);		//there are no images with expression for this "respiratory system" and Nxn
		
		
		List<ImageDTO> mutantImages=null;
		try {
			mutantImages=imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex, organ);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(mutantImages.size()>0);//we have mutant images for nxn 29 currently
	}


	@Test
	public void testGetComparisonViewerMethodsWithAmbiguous() {
		String         acc                    = "MGI:109331";
		int            numberOfControlsPerSex = 10;
		String         anatomyId              = "MA:0000327";
		String         parameterStableId      = "IMPC_ALZ_076_001";
		SexType        sex                    = null;
		ImageDTO       imgDoc                 = null;
		List<ImageDTO> controlImages          = null;
		String         organ                  = null;
		//Expression expression=Expression.AMBIGUOUS;
		String parameterAssociationValue = Expression.AMBIGUOUS.getDisplayName();
		String zygosity                  = null;
		String colonyId                  = null;
		String mpId                      = null;

		try {
			controlImages = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sex, parameterStableId, anatomyId, parameterAssociationValue);
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//no information has been given so we would expect null or an empty array - empty array safest
		assertTrue(controlImages.size() > 0);//there are currently 25 images with ambiguous expression for this "respiratory system" and Nxn
		assertTrue(controlImages.size() >= 2);

		List<ImageDTO> mutantImages = null;
		try {
			mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex, organ);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int    expectedSize = 2;
		int    actualSize   = mutantImages.size();
		String message      = "Expected at least " + expectedSize + " mutant images but found " + actualSize;
		assertTrue(message, actualSize >= expectedSize);        //we have 2 mutant images for nxn that match this test currently with ambiguous expression in mutants
	}

	@Test
	public void testGetComparisonViewerMethodsWithNoExpression() {
		String         acc                       = "MGI:109331";
		int            numberOfControlsPerSex    = 10;
		String         anatomyId                 = "MA:0000327";
		String         parameterStableId         = "IMPC_ALZ_076_001";
		SexType        sex                       = null;
		ImageDTO       imgDoc                    = null;
		List<ImageDTO> controlImages             = null;
		String         organ                     = null;
		Expression     expression                = Expression.NO_EXPRESSION;
		String         parameterAssociationValue = Expression.NO_EXPRESSION.getDisplayName();//Expression.AMBIGUOUS.getDisplayName();
		String         zygosity                  = null;
		String         colonyId                  = null;
		String         mpId                      = null;

		try {
			controlImages = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sex, parameterStableId, anatomyId, expression.getDisplayName());
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//no information has been given so we would expect null or an empty array - empty array safest
		logger.info("control images size=" + controlImages.size());
		for (ImageDTO image : controlImages) {
			for (String value : image.getParameterAssociationValue()) {
				logger.info("value=" + value);
			}
		}
		assertTrue(controlImages.size() >= 10);//there are currently 25 images with ambiguous expression for this "respiratory system" and Nxn
		//assertTrue(controlImages.size()>=2);

		List<ImageDTO> mutantImages = null;
		try {
			mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex, organ);
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int    expectedSize = 0;
		int    actualSize   = mutantImages.size();
		String message      = "Expected " + expectedSize + " images but found " + actualSize;

		assertTrue(message, expectedSize >= actualSize);
	}
}