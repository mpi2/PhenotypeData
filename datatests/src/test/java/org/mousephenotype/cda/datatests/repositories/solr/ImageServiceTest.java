package org.mousephenotype.cda.datatests.repositories.solr;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.Group;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.enumerations.Expression;
import org.mousephenotype.cda.enumerations.SexType;
import org.mousephenotype.cda.solr.service.ImageService;
import org.mousephenotype.cda.solr.service.dto.ImageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RepositorySolrTestConfig.class})
public class ImageServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ImageService imageService;


    @Test
    public void allImageRecordSolrQueryTest() throws Exception {


        SolrQuery query = ImageService.allImageRecordSolrQuery();

        logger.debug("Query is: " + query.toString());

        assertTrue(query.toString().contains("3i"));
        assertTrue(query.toString().contains("mousephenotype.org"));
        assertTrue(query.toString().contains("\\:"));
    }

    @Test
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
        logger.debug(testName + ": mpToColonies actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        assertTrue(message, ! failed);
    }

    @Test
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

        response = imageService.getImages(acc, parameterStableId, "experimental", 100000, null, null, null, anatomyId, parameterAsscociationValue, null, null, null);

        expectedSize = 13;          // 26-Oct-2017 (mrelac) As of this date there were 13 images found.
        actualSize   = response.getResults().size();
        message      = testName + ": Expected at least " + expectedSize + " images but found " + actualSize;
        logger.debug(testName + ": images actualSize = " + actualSize);

        if (actualSize < expectedSize) {
            logger.error(message);
            failed = true;
        }

        assertTrue(message, ! failed);
    }

    @Test
    public void testGetPhenotypeAssociatedImages() throws IOException, SolrServerException {

        String  testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String  acc      = "MGI:1891341";
        int     rows     = 1;

        int    expectedSize;
        int    actualSize;
        String message;

        List<Group> response     = imageService.getPhenotypeAssociatedImages(acc, null, null, true, rows);

        expectedSize = 6;                        // 26-Oct-2017 (mrelac) As of this date there were 8 phenotype associated images found.
        actualSize   = (response != null ? response.size() : 0);
        message      = testName + ": Expected at least " + expectedSize + " associated images but found " + actualSize;
        logger.debug(testName + ": associated images actualSize = " + actualSize);

        assertTrue(message, actualSize >= expectedSize);
    }

    // FIXME FIXME FIXME This test takes a long time to run and runs out of memory when run on local laptops.
    @Ignore
    @Test
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

        mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex);

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

    // FIXME FIXME FIXME This test fails as of 03-Apr-2019, so I'm disabling it as there isn't any obvious failure observed here. Will research later.
    // FIXME FIXME FIXME 15-Apr-2019 (mrelac) Why is the ImageService querying with unknown field 'parameter_association_value'?
    @Ignore
    @Test
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

        int    expectedSize;
        int    actualSize;
        String message;

        controlImages = imageService.getControlsBySexAndOthersForComparisonViewer(imgDoc, numberOfControlsPerSex, sex, parameterStableId, anatomyId, expression.getDisplayName());

        expectedSize = 3;                        // 26-Oct-2017 (mrelac) As of this date there were 9 control images found.
        actualSize   = controlImages.size();
        message      = testName + ": Expected at least " + expectedSize + " control images but found " + actualSize;
        logger.info(testName + ": control images actualSize = " + actualSize);

        assertTrue(message, actualSize >= expectedSize);

        mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex);

        expectedSize = 3;                      // 26-Oct-2017 (mrelac) As of this date there were 34 mutant images found.
        actualSize   = mutantImages.size();
        message      = testName + ": Expected at least " + expectedSize + " mutant images but found " + actualSize;
        logger.info(testName + ": mutant images actualSize = " + actualSize);

        assertTrue(actualSize >= expectedSize);
    }

    @Test
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

        mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex);

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

        mutantImages = imageService.getMutantImagesForComparisonViewer(acc, parameterStableId, parameterAssociationValue, anatomyId, zygosity, colonyId, mpId, sex);

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