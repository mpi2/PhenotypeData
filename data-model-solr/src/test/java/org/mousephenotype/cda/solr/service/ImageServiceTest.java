package org.mousephenotype.cda.solr.service;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.enumerations.Expression;
import org.mousephenotype.cda.enumerations.SexType;
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
//@Ignore
    public void testGetImagePropertiesThatHaveMp() throws IOException, SolrServerException {

        String  testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String  acc      = "MGI:1913955";
        int     expectedSize;
        int     actualSize;
        String  message;
        boolean failed   = false;

        Map<String, Set<String>> mpToColonies = imageService.getImagePropertiesThatHaveMp(acc);

        expectedSize = 18;              // 26-Oct-2017 (mrelac) As of this date there were 18 colonies found.
        actualSize = mpToColonies.size();
        message = testName + ": Expected at least " + expectedSize + " mpToColonies but found " + actualSize;
        logger.info(testName + ": mpToColonies actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        assertTrue(message, ! failed);
    }

    @Test
//@Ignore
    public void testGetImagesForGeneByParameter() throws IOException, SolrServerException {

        String        testName                   = Thread.currentThread().getStackTrace()[1].getMethodName();
        String        acc                        = "MGI:1336993";
        String        parameterStableId          = "IMPC_ELZ_064_001";
        String        anatomyId                  = "EMAPA:16105";
        String        parameterAsscociationValue = "ambiguous";
        QueryResponse response;
        boolean       failed                     = false;

        int    expectedSize;
        int    actualSize;
        String message;

        response = imageService.getImages(acc, parameterStableId, "experimental", 100000, null, null, null, anatomyId, parameterAsscociationValue, null, null);

        expectedSize = 13;          // 26-Oct-2017 (mrelac) As of this date there were 13 images found.
        actualSize   = response.getResults().size();
        message      = testName + ": Expected at least " + expectedSize + " images but found " + actualSize;
        logger.info(testName + ": images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        assertTrue(message, ! failed);
    }

    @Test
//@Ignore
    public void testGetPhenotypeAssociatedImages() throws IOException, SolrServerException {

        String  testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String  acc      = "MGI:1891341";
        int     rows     = 1;
        boolean failed   = false;

        int    expectedSize;
        int    actualSize;
        String message;

        List<Group> response     = imageService.getPhenotypeAssociatedImages(acc, null, null, true, rows);

        expectedSize = 8;                        // 26-Oct-2017 (mrelac) As of this date there were 8 phenotype associated images found.
        actualSize   = (response != null ? response.size() : 0);
        message      = testName + ": Expected at least " + expectedSize + " associated images but found " + actualSize;
        logger.info(testName + ": associated images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        assertTrue(message, ! failed);
    }

    @Test
//@Ignore
    public void testGetComparisonViewerMethodsWithNulls() throws IOException, SolrServerException {

        String         testName                  = Thread.currentThread().getStackTrace()[1].getMethodName();
        String         acc                       = null;
        int            numberOfControlsPerSex    = 10;
        String         anatomyId                 = null;
        String         parameterStableId         = null;
        SexType        sex                       = null;
        ImageDTO       imgDoc                    = null;
        List<ImageDTO> controlImages;
        List<ImageDTO> mutantImages;
        String         organ                     = null;
        String         parameterAssociationValue = null;
        String         zygosity                  = null;
        String         colonyId                  = null;
        String         mpId                      = null;
        boolean        failed                    = false;

        int    expectedSize;
        int    actualSize;
        String message;

        controlImages = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sex, parameterStableId, anatomyId, null);

        expectedSize = 0;                        // 26-Oct-2017 (mrelac) As of this date there were no control images found.
        actualSize   = controlImages.size();
        message      = testName + ": Expected at least " + expectedSize + " control images but found " + actualSize;
        logger.info(testName + ": control images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex, organ);

        expectedSize = 207009;                  // 26-Oct-2017 (mrelac) As of this date there were 207009 mutant images found.
        actualSize   = mutantImages.size();
        message      = testName + ": Expected at least " + expectedSize + " mutant images but found " + actualSize;
        logger.info(testName + ": mutant images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        assertTrue(message, ! failed);
    }

    @Test
//@Ignore
    public void testGetComparisonViewerMethodsWithExpression() throws IOException, SolrServerException {

        String         testName                  = Thread.currentThread().getStackTrace()[1].getMethodName();
        String         acc                       = "MGI:109331";
        int            numberOfControlsPerSex    = 10;
        String         anatomyId                 = "MA:0000327";
        String         parameterStableId         = null;
        SexType        sex                       = null;
        ImageDTO       imgDoc                    = null;
        List<ImageDTO> controlImages;
        List<ImageDTO> mutantImages;
        String         organ                     = null;
        Expression     expression                = Expression.EXPRESSION;
        String         parameterAssociationValue = expression.getDisplayName();
        String         zygosity                  = null;
        String         colonyId                  = null;
        String         mpId                      = null;
        boolean        failed                    = false;

        int    expectedSize;
        int    actualSize;
        String message;

        controlImages = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sex, parameterStableId, anatomyId, expression.getDisplayName());

        expectedSize = 9;                        // 26-Oct-2017 (mrelac) As of this date there were 9 control images found.
        actualSize   = controlImages.size();
        message      = testName + ": Expected at least " + expectedSize + " control images but found " + actualSize;
        logger.info(testName + ": control images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex, organ);

        expectedSize = 34;                      // 26-Oct-2017 (mrelac) As of this date there were 34 mutant images found.
        actualSize   = mutantImages.size();
        message      = testName + ": Expected at least " + expectedSize + " mutant images but found " + actualSize;
        logger.info(testName + ": mutant images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        assertTrue("Test failed", ! failed);
    }

    @Test
//@Ignore
    public void testGetComparisonViewerMethodsWithAmbiguous() throws IOException, SolrServerException {
        String         testName               = Thread.currentThread().getStackTrace()[1].getMethodName();
        String         acc                    = "MGI:109331";
        int            numberOfControlsPerSex = 10;
        String         anatomyId              = "MA:0000327";
        String         parameterStableId      = "IMPC_ALZ_076_001";
        SexType        sex                    = null;
        ImageDTO       imgDoc                 = null;
        List<ImageDTO> controlImages;
        List<ImageDTO> mutantImages;
        String         organ                  = null;
        boolean        failed                 = false;

        String parameterAssociationValue = Expression.AMBIGUOUS.getDisplayName();
        String zygosity                  = null;
        String colonyId                  = null;
        String mpId                      = null;

        int    expectedSize;
        int    actualSize;
        String message;

        controlImages = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sex, parameterStableId, anatomyId, parameterAssociationValue);

        expectedSize = 10;                        // 26-Oct-2017 (mrelac) As of this date there were 10 control images found.
        actualSize   = controlImages.size();
        message      = testName + " : Expected at least " + expectedSize + " control images but found " + actualSize;

        logger.info(testName + ": control images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex, organ);

        expectedSize = 2;                       // 26-Oct-2017 (mrelac) As of this date there were 2 mutant images found.
        actualSize   = mutantImages.size();
        message      = testName + ": Expected at least " + expectedSize + " mutant images but found " + actualSize;
        logger.info(testName + ": mutant images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        assertTrue("Test failed", ! failed);
    }

    @Test
//@Ignore
    public void testGetComparisonViewerMethodsWithNoExpression() throws IOException, SolrServerException {

        String         testName                  = Thread.currentThread().getStackTrace()[1].getMethodName();
        String         acc                       = "MGI:109331";
        int            numberOfControlsPerSex    = 10;
        String         anatomyId                 = "MA:0000327";
        String         parameterStableId         = "IMPC_ALZ_076_001";
        SexType        sex                       = null;
        ImageDTO       imgDoc                    = null;
        List<ImageDTO> controlImages;
        List<ImageDTO> mutantImages;
        String         organ                     = null;
        Expression     expression                = Expression.NO_EXPRESSION;
        String         parameterAssociationValue = Expression.NO_EXPRESSION.getDisplayName();//Expression.AMBIGUOUS.getDisplayName();
        String         zygosity                  = null;
        String         colonyId                  = null;
        String         mpId                      = null;
        boolean        failed                    = false;

        int    expectedSize;
        int    actualSize;
        String message;

        controlImages = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sex, parameterStableId, anatomyId, expression.getDisplayName());

        expectedSize = 10;                        // 26-Oct-2017 (mrelac) As of this date there were 10 control images found.
        actualSize   = controlImages.size();
        message      = testName + ": Expected at least " + expectedSize + " control images but found " + actualSize;
        logger.info(testName + ": control images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex, organ);

        expectedSize = 0;                       // 26-Oct-2017 (mrelac) As of this date there were no mutant images found.
        actualSize   = mutantImages.size();
        message      = testName + ": Expected at least " + expectedSize + " mutant images but found " + actualSize;
        logger.info(testName + ": mutant images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        assertTrue("Test failed", ! failed);
    }
}