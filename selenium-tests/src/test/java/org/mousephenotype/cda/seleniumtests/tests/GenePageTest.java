/*******************************************************************************
 *  Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.seleniumtests.tests;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.seleniumtests.support.GenePage;
import org.mousephenotype.cda.seleniumtests.support.PageStatus;
import org.mousephenotype.cda.seleniumtests.support.SeleniumWrapper;
import org.mousephenotype.cda.seleniumtests.support.TestUtils;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.thoughtworks.selenium.SeleneseTestBase.fail;
import static com.thoughtworks.selenium.SeleneseTestNgHelper.assertEquals;

/**
 *
 * @author mrelac
 *
 * Selenium test for gene page query coverage ensuring each page works as expected.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/application.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class GenePageTest {

    private CommonUtils commonUtils = new CommonUtils();
    private WebDriver driver;
    protected TestUtils testUtils = new TestUtils();
    private WebDriverWait wait;

    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final int TIMEOUT_IN_SECONDS = 120;         // Increased timeout from 4 to 120 secs as some of the graphs take a long time to load.
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;

    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int threadWaitInMilliseconds = THREAD_WAIT_IN_MILLISECONDS;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Environment env;

    @Autowired
    protected GeneService geneService;

    @Autowired
    protected PhenotypePipelineDAO phenotypePipelineDAO;

    @Autowired
    protected SeleniumWrapper wrapper;

    @NotNull
    @Value("${baseUrl}")
    protected String baseUrl;


    @PostConstruct
    public void initialise() throws Exception {
        driver = wrapper.getDriver();
    }

    @Before
    public void setup() {
        if (commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeoutInSeconds = commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            threadWaitInMilliseconds = commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));

        testUtils.printTestEnvironment(driver, wrapper.getSeleniumUrl());
        wait = new WebDriverWait(driver, timeoutInSeconds);

        driver.navigate().refresh();
        commonUtils.sleep(threadWaitInMilliseconds);
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }


    // PRIVATE METHODS


    private void geneIdsTestEngine(String testName, List<String> geneIds) throws SolrServerException {
        PageStatus status = new PageStatus();
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);

geneIds = testUtils.removeKnownBadGeneIds(geneIds);

        String target = "";
        List<String> successList = new ArrayList();
        String message;
        Date start = new Date();

        int targetCount = testUtils.getTargetCount(env, testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");

        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;

            target = baseUrl + "/genes/" + geneId;
            logger.debug("gene[" + i + "] URL: " + target);

            try {
                GenePage genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);
                boolean phenotypesTableRequired = false;
                genePage.validate(phenotypesTableRequired);
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                status.addError(message);
                commonUtils.sleep(threadWaitInMilliseconds);
                continue;
            }  catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                status.addError(message);
                commonUtils.sleep(threadWaitInMilliseconds);
                continue;
            }

            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);

            commonUtils.sleep(threadWaitInMilliseconds);
        }

        testUtils.printEpilogue(testName, start, status, successList.size(), targetCount, geneIds.size());
    }

    private void tick(String phenoStatus, String prodCentre, String phenoCentre) {
        // If no parameters were specified, set target to the default search page.
        String target = baseUrl + "/search";
        String fields = "";
        if ( ! ((phenoStatus == null) && (prodCentre == null) && (phenoCentre == null))) {
            target += "#fq=";
            if (phenoStatus != null) {
                switch(phenoStatus) {
                    case "Complete":
                        fields += "(latest_phenotype_status:\"Phenotyping Complete\")";
                        break;

                    case "Started":
                        fields += "(latest_phenotype_status:\"Phenotyping Started\")";
                        break;

                    case "Attempt Registered":
                        fields += "(latest_phenotype_status:\"Phenotype Attempt Registered\")";
                        break;

                    default:
                        throw new RuntimeException("tick(): unknown phenotyping status '" + phenoStatus + "'.");
                }
            }

            if (prodCentre != null) {
                if ( ! fields.isEmpty()) {
                    fields += " AND ";
                    fields += "(latest_production_centre:\"" + prodCentre + "\")";
                }
            }

            if (phenoCentre != null) {
                if ( ! fields.isEmpty()) {
                    fields += " AND ";
                    fields += "(latest_phenotyping_centre:\"" + phenoCentre + "\")";
                }
            }

            target += fields + "&facet=gene";
        }

        driver.get(target);

        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        String xpathSelector = "//span[@id=\"resultCount\"]/a";
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathSelector)));
    }

    /**
     * Given that the current page is the gene search page, returns the number
     * sandwiched between the 'Found' and 'genes' terms; e.g., given the string
     * 'Found 5 genes', returns the number 5. Returns 0 if there is no number
     * or no such formatted string.
     * @return gene count if found; 0 otherwise
     */
    private int getGeneCount() {
        WebElement element = driver.findElement(By.xpath("//div[@id=\"resultMsg\"]/span[@id=\"resultCount\"]/a"));

        String s = element.getText().replace(" genes", "");
        Integer i = commonUtils.tryParseInt(s);
        return (i == null ? 0 : i);
    }


    // TESTS


    /**
     * Finds all MGI_ACCESSION_IDs in the genotype-phenotype
     * core that do not start with 'MGI'.
     *
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     *
     * @throws SolrServerException
     */
    @Test
//@Ignore
    public void testForBadGeneIds() throws Exception {
        PageStatus status = new PageStatus();
        String testName = "testForBadGeneIds";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        List<String> geneIds = new ArrayList(geneService.getAllNonConformingGenes());
        String target = "";
        List<String> successList = new ArrayList();
        String message;
        Date start = new Date();

        int targetCount = testUtils.getTargetCount(env, testName, geneIds, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of " + geneIds.size() + " records.");

        // Loop through all non-conforming genes, testing each one for valid page load (they will likely fail).
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;

            target = baseUrl + "/genes/" + geneId;
            logger.debug("gene[" + i + "] URL: " + target);

            try {
                driver.get(target);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
            } catch (NoSuchElementException | TimeoutException te) {
// 25-Mar-2015 (mrelac) These CGI genes are now back in the game.
//                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
//                errorList.add(message);
            } catch (Exception e) {
                status.addError("EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage());
                continue;
            }

            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            successList.add(message);
            Thread.sleep(threadWaitInMilliseconds);
        }

        testUtils.printEpilogue(testName, start, status, successList.size(), targetCount, geneIds.size());
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure there is a page for each.
     *
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     *
     * @throws SolrServerException
     */
    @Test
//@Ignore
    public void testPageForGeneIds() throws SolrServerException {
        String testName = "testPageForGeneIds";
        List<String> geneIds = new ArrayList(geneService.getAllGenes());

        geneIdsTestEngine(testName, geneIds);
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the gene core with
     * phenotype status 'started' and phenotype centre 'WTSI' and tests to
     * make sure there is a page for each.
     *
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     *
     * @throws SolrServerException
     */
    @Test
//@Ignore
    public void testPageForGenesByLatestPhenotypeStatusStartedAndPhenotypeCentreWTSI() throws SolrServerException {
        String testName = "testPageForGenesByLatestPhenotypeStatusStartedAndPhenotypeCentreWTSI";
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndPhenotypeCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.CENTRE_WTSI));

geneIds = testUtils.removeKnownBadGeneIds(geneIds);

        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Started", null, GeneService.GeneFieldValue.CENTRE_WTSI);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());

        geneIdsTestEngine(testName, geneIds);
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the gene core with
     * phenotype status 'started' and production centre 'WTSI' and tests to
     * make sure there is a page for each.
     *
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     *
     * @throws SolrServerException
     */
    @Test
//@Ignore
    public void testPageForGenesByLatestPhenotypeStatusStartedAndProductionCentreWTSI() throws SolrServerException {
        String testName = "testPageForGenesByLatestPhenotypeStatusStartedAndProductionCentreWTSI";
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.CENTRE_WTSI));

geneIds = testUtils.removeKnownBadGeneIds(geneIds);

        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Started", GeneService.GeneFieldValue.CENTRE_WTSI, null);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());

        geneIdsTestEngine(testName, geneIds);
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the gene core with
     * phenotype status 'complete' and phenotype centre 'WTSI' and tests to
     * make sure there is a page for each.
     *
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     *
     * @throws SolrServerException
     */
    @Test
//@Ignore
    public void testPageForGenesByLatestPhenotypeStatusCompleteAndPhenotypeCentreWTSI() throws SolrServerException {
        String testName = "testPageForGenesByLatestPhenotypeStatusCompleteAndPhenotypeCentreWTSI";
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndPhenotypeCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_COMPLETE, GeneService.GeneFieldValue.CENTRE_WTSI));

geneIds = testUtils.removeKnownBadGeneIds(geneIds);

        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Complete", null, GeneService.GeneFieldValue.CENTRE_WTSI);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());

        geneIdsTestEngine(testName, geneIds);
    }

    /**
     * Fetches all gene IDs (MARKER_ACCESSION_ID) from the gene core with
     * phenotype status 'complete' and production centre 'WTSI' and tests to
     * make sure there is a page for each.
     *
     * <p><em>Limit the number of test iterations by adding an entry to
     * testIterations.properties with this test's name as the lvalue and the
     * number of iterations as the rvalue. -1 means run all iterations.</em></p>
     *
     * @throws SolrServerException [
     */
    @Test
//@Ignore
    public void testPageForGenesByLatestPhenotypeStatusCompleteAndProductionCentreWTSI() throws SolrServerException {
        String testName = "testPageForGenesByLatestPhenotypeStatusCompleteAndProductionCentreWTSI";
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_COMPLETE, GeneService.GeneFieldValue.CENTRE_WTSI));

geneIds = testUtils.removeKnownBadGeneIds(geneIds);

        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Complete", GeneService.GeneFieldValue.CENTRE_WTSI, null);
        int geneCount = getGeneCount();
        assertEquals(geneCount, geneIds.size());

        geneIdsTestEngine(testName, geneIds);
    }

    /**
     * Tests that a sensible page is returned for an invalid gene id.
     *
     * @throws SolrServerException
     */
    @Test
//@Ignore
    public void testInvalidGeneId() throws SolrServerException {
        PageStatus status = new PageStatus();
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        String testName = "testInvalidGeneId";
        String target = "";
        int targetCount = 1;
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        String message;
        Date start = new Date();
        String geneId = "junkBadGene";
        final String EXPECTED_ERROR_MESSAGE = "Oops! junkBadGene is not a valid MGI gene identifier.";

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of 1 records.");

        boolean found = false;
        target = baseUrl + "/genes/" + geneId;
        System.out.println("URL: " + target);

        try {
            driver.get(target);
            List<WebElement> geneLinks = (new WebDriverWait(driver, timeoutInSeconds))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.node h1")));

            if (geneLinks == null) {
                message = "Expected error page for MP_TERM_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            }

            for (WebElement element : geneLinks) {
                if (element.getText().compareTo(EXPECTED_ERROR_MESSAGE) == 0) {
                    found = true;
                    break;
                }
            }

            if ( ! found) {
                message = "Expected error page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                errorList.add(message);
            }
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            status.addError(message);
            exceptionList.add(message);
        }

        message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
        successList.add(message);
        testUtils.printEpilogue(testName, start, status, successList.size(), targetCount, 1);
    }

    // Test for the minimum number of blue and orange icons. Match orange icons with phenotype summary strings
    // appearing on the left side of the gene page.
    @Test
//@Ignore
    public void testAkt2() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        String testName = "testAkt2";
        int targetCount = 1;
        List<String> errorList = new ArrayList();
        List<String> successList = new ArrayList();
        List<String> exceptionList = new ArrayList();
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        int sectionErrorCount;
        int numOccurrences;

        PageStatus status;
        String message;
        Date start = new Date();

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of 1 records.");

        String geneId = "MGI:104874";
        String target = baseUrl + "/genes/" + geneId;
        System.out.println("URL: " + target);
        GenePage genePage;

        try {
            genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);
        } catch (Exception e) {
            message = "ERROR: Failed to load gene page URL: " + target;
            System.out.println(message);
            fail(message);
            return;
        }

        // Title
        String title = genePage.getTitle();
        if (title.contains("Akt2")) {
            System.out.println("Title: [PASSED]\n");
        } else {
            message = "Title: [FAILED]: Expected title to contain 'Akt2' but it was not found. Title: '" + title + "'";
            errorList.add(message);
            System.out.println(message + "\n");
        }

        // Section Titles: count and values (e.g. 'Gene: Akt2', 'Phenotype associations for Akt2', 'Pre-QC phenotype heatmap', etc.)
        // ... count
        sectionErrorCount = 0;
        String[] sectionTitlesArray = {
                "Gene: Akt2",
                "Phenotype associations for Akt2",
                "Phenotype Associated Images",
                "Expression",
                "Disease Models associated by gene orthology",
                "Potential Disease Models predicted by phenotypic similarity",
                "Order Mouse and ES Cells",};
        List<String> expectedSectionTitles = Arrays.asList(sectionTitlesArray);
        List<String> actualSectionTitles = genePage.getSectionTitles();
        if (actualSectionTitles.size() != sectionTitlesArray.length) {
            sectionErrorCount++;
            message = "Section Titles (count): [FAILED]. Expected " + sectionTitlesArray.length + " section titles but found " + actualSectionTitles.size() + ".";
            errorList.add(message);
            System.out.println(message + "\n");
        } else {
            System.out.println("Section Titles (count): [PASSED]\n");
        }
        // ... values
        status = new PageStatus();
        for (String expectedSectionTitle : expectedSectionTitles) {
            if ( ! actualSectionTitles.contains(expectedSectionTitle)) {
                message = "Section Titles (values): [FAILED]. Mismatch: Expected section named '" + expectedSectionTitle + "' but wasn't found.";
                status.addError(message);
                sectionErrorCount++;
            }
        }
        for (String actualSectionTitle : actualSectionTitles) {
            if ( ! expectedSectionTitles.contains(actualSectionTitle)) {
                message = "Section Titles (values): [FAILED]. Mismatch: Found section named '" + actualSectionTitle + "' but wasn't expected.";
                status.addError(message);
                sectionErrorCount++;
            } else {
                numOccurrences = testUtils.count(actualSectionTitles, actualSectionTitle);
                if (numOccurrences > 1) {
                    message = "Section Titles (values): [FAILED]. " + numOccurrences + " occurrences of '" + actualSectionTitle + "' were found.";
                    status.addError(message);
                    sectionErrorCount++;
                }
            }
        }
        if (sectionErrorCount == 0) {
            System.out.println("Section Titles (values): [PASSED]\n");
        } else {
            // Dump out all titles.
            for (int i = 0; i < actualSectionTitles.size(); i++) {
                String sectionTitle = actualSectionTitles.get(i);
                System.out.println("\t[" + i + "]: " + sectionTitle);
            }

            // Dump out the missing ones.
            System.out.println(status.toStringErrorMessages());

            // Add missing ones to error list.
            errorList.addAll(status.getErrorMessages());
        }

        // Buttons: count and labels
        // ... count
        sectionErrorCount = 0;
        String[] buttonLabelsArray = {
                "Login to register interest",
                "Order",
                "All Adult Data",
                "KOMP",
                "EUMMCR",};
        List<String> expectedButtonLabels = Arrays.asList(buttonLabelsArray);
        List<String> actualButtonLabels = genePage.getButtonLabels();
        if (actualButtonLabels.size() != buttonLabelsArray.length) {
            sectionErrorCount++;
            message = "Buttons (count): [FAILED]. Expected " + buttonLabelsArray.length + " buttons but found " + actualButtonLabels.size() + ".";
            errorList.add(message);
            System.out.println(message + "\n");
        } else {
            System.out.println("Buttons (count): [PASSED]\n");
        }
        // ... values
        status = new PageStatus();
        for (String expectedSectionTitle : expectedButtonLabels) {
            if ( ! actualButtonLabels.contains(expectedSectionTitle)) {
                message = "Buttons (values): [FAILED]. Mismatch: Expected button named '" + expectedSectionTitle + "' but wasn't found.";
                status.addError(message);
                sectionErrorCount++;
            }
        }
        for (String actualButtonLabel : actualButtonLabels) {
            if ( ! expectedButtonLabels.contains(actualButtonLabel)) {
                message = "Buttons (values): [FAILED]. Mismatch: Found button named '" + actualButtonLabel + "' but wasn't expected.";
                status.addError(message);
                sectionErrorCount++;
            } else {
                numOccurrences = TestUtils.count(actualButtonLabels, actualButtonLabel);
                if (numOccurrences > 1) {
                    message = "Buttons (values): [FAILED]. " + numOccurrences + " occurrences of '" + actualButtonLabel + "' were found.";
                    status.addError(message);
                    sectionErrorCount++;
                }
            }
        }
        if (sectionErrorCount == 0) {
            System.out.println("Buttons (values): [PASSED]\n");
        } else {
            // Dump out all buttons.
            for (int i = 0; i < actualButtonLabels.size(); i++) {
                String sectionTitle = actualButtonLabels.get(i);
                System.out.println("\t[" + i + "]: " + sectionTitle);
            }

            // Dump out the missing ones.
            System.out.println(status.toStringErrorMessages());

            // Add missing ones to error list.
            errorList.addAll(status.getErrorMessages());
        }

        // Enabled Abnormalities: count and strings. As of 15-September-2015, there should be at least:
        //   8 - tested but not significant (blue)
        //   5 - significant (orange)
        // ... count
        sectionErrorCount = 0;
        numOccurrences = 0;
        status = new PageStatus();
        final List<String> expectedSignificantList = Arrays.asList(
                new String[] {
                        "growth/size/body region phenotype"
                      , "homeostasis/metabolism phenotype or adipose tissue phenotype"
                      , "behavior/neurological phenotype or nervous system phenotype"
                      , "skeleton phenotype"
                      , "immune system phenotype or hematopoietic system phenotype"
                });
        final List<String> expectedNotSignificantList = Arrays.asList(
                new String[] {
                        "reproductive system phenotype"
                      , "cardiovascular system phenotype"
                      , "digestive/alimentary phenotype or liver/biliary system phenotype"
                      , "renal/urinary system phenotype"
                      , "limbs/digits/tail phenotype"
                      , "integument phenotype or pigmentation phenotype"
                      , "craniofacial phenotype"
                      , "vision/eye phenotype"
                });

        // Validate that there are at least 5 expectedSignificant icons.
        List<String> actualSignificantList = genePage.getSignificantAbnormalities();
        if (actualSignificantList.size() < expectedSignificantList.size()) {
            sectionErrorCount++;
            message = "Significant Abnormalities (count): [FAILED]. Expected " + expectedSignificantList.size() + " strings but found " + actualSignificantList.size() + ".";
            status.addError(message);
            System.out.println(message + "\n");
        } else {
            System.out.println("Significant Abnormalities (count): [PASSED]\n");
        }

        // Validate that the sum of expectedSignificant and expectedNotSignificant are at least the sum of the expected list sizes.
        List<String> actualNotSignificantList = genePage.getNotSignificantAbnormalities();
        if (actualSignificantList.size() + actualNotSignificantList.size() < expectedSignificantList.size() + expectedNotSignificantList.size()) {
            sectionErrorCount++;
            message = "Sum of Significant and Non-Significant Abnormalities (count): [FAILED]. Expected "
                     + expectedSignificantList.size() + expectedNotSignificantList.size()
                     + " strings but found "
                     + actualSignificantList.size() + actualNotSignificantList.size() + ".";
            status.addError(message);
            System.out.println(message + "\n");
        } else {
            System.out.println("Not Significant Abnormalities (count): [PASSED]\n");
        }

        // Validate the actual Significant abnormality values against the expected ones.
        for (String actualSignificant : actualSignificantList) {
            if ( ! expectedSignificantList.contains(actualSignificant)) {
                message = "Significant Abnormalities (values): [FAILED]. Mismatch: Expected significant abnormality named '" + actualSignificant + "' but wasn't found.";
                status.addError(message);
                sectionErrorCount++;
            }
        }

        // Validate that the actual NotSignificant abnormality values are in either the expectedSignificantList or
        // the expectedNonSignificantList.
        List<String> both = new ArrayList<>();
        both.addAll(expectedSignificantList);
        both.addAll(expectedNotSignificantList);
        for (String actualNotSignificant : actualNotSignificantList) {
            if ( ! both.contains(actualNotSignificant)) {
                message = "Not Significant Abnormalities (values): [FAILED]. Mismatch: Couldn't find actual Not Significant abnormality named '" + actualNotSignificant + "'";
                status.addError(message);
                sectionErrorCount++;
            }
        }

        if (sectionErrorCount == 0) {
            System.out.println("Significant and Not Significant Abnormalities (values): [PASSED]\n");
        } else {
            // Dump out all Significant abnormalities.
            for (int i = 0; i < actualSignificantList.size(); i++) {
                if (i == 0)
                    System.out.println("Actual Significant List:");
                String actualAbnormality = actualSignificantList.get(i);
                System.out.println("\t[" + i + "]: " + actualAbnormality);
            }

            // Dump out all Not Significant abnormalities.
            for (int i = 0; i < actualNotSignificantList.size(); i++) {
                if (i == 0)
                    System.out.println("Actual Not Significant List:");
                String actualAbnormality = actualNotSignificantList.get(i);
                System.out.println("\t[" + i + "]: " + actualAbnormality);
            }

            // Add missing titles to error list.
            errorList.addAll(status.getErrorMessages());
        }

        // Phenotype Associated Images and Expression sections: count. Since the data can
        // change over time, don't compare individual strings; just look for at least a count of 12.
        // ... count
        sectionErrorCount = 0;
        numOccurrences = 0;

        final int expectedAssociatedImageSize = 12;
        List<String> actualAssociatedImageSections = genePage.getAssociatedImageSections();
        if (actualAssociatedImageSections.size() < expectedAssociatedImageSize) {
            sectionErrorCount++;
            message = "Associated Image Sections (count): [FAILED]. Expected at least 12 strings but found " + actualAssociatedImageSections.size() + ".";
            errorList.add(message);
            System.out.println(message + "\n");
        } else {
            System.out.println("Associate Image Sections (count): [PASSED]\n");
        }

        if (sectionErrorCount == 0) {
            System.out.println("Associated Image Sections (values): [PASSED]\n");
        } else {
            // Dump out all associated image sections.
            for (int i = 0; i < actualAssociatedImageSections.size(); i++) {
                String actualAssociatedImageSection = actualAssociatedImageSections.get(i);
                System.out.println("\t[" + i + "]: " + actualAssociatedImageSection);
            }

            // Dump out the missing/duplicated ones.
            System.out.println(status.toStringErrorMessages());
        }

        //test that the order mouse and es cells content from viveks team exists on the page
        WebElement orderAlleleDiv = driver.findElement(By.id("allele2"));//this div is in the ebi jsp which should be populated but without the ajax call success will be empty.
        // This used to be called id="allele". That id still exists but is empty and causes the test to fail here. Now they use id="allele2".
        String text = orderAlleleDiv.getText();
        if (text.length() < 100) {
            message = "Order Mouse content: [FAILED]. less than 100 characters: \n\t'" + text + "'";
            errorList.add(message);
            sectionErrorCount++;
        } else {
            System.out.println("Order Mouse content: [PASSED]\n");
        }

        if ((errorList.isEmpty() && (exceptionList.isEmpty()))) {
            successList.add("Akt2 test: [PASSED]");
        }

        testUtils.printEpilogue(testName, start, status, successList.size(), targetCount, 1);
    }
    
    @Test
//@Ignore
      public void testImageThumbnails() throws Exception {
          DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
          String testName = "testImageThumbnails";
          int targetCount = 1;
          List<String> errorList = new ArrayList();
          List<String> successList = new ArrayList();
          List<String> exceptionList = new ArrayList();
          WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
          int sectionErrorCount;
          int numOccurrences;

          PageStatus status;
          String message;
          Date start = new Date();

          System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of 1 records.");

          String geneId = "MGI:1935151";
          String target = baseUrl + "/genes/" + geneId;
          System.out.println("URL: " + target);
          GenePage genePage;

          try {
              genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);
          } catch (Exception e) {
              message = "ERROR: Failed to load gene page URL: " + target;
              System.out.println(message);
              fail(message);
              return;
          }

          // Title
          String title = genePage.getTitle();
          if (title.contains("Klf7")) {
              System.out.println("Title: [PASSED]\n");
          } else {
              message = "Title: [FAILED]: Expected title to contain 'Klf7' but it was not found. Title: '" + title + "'";
              errorList.add(message);
              System.out.println(message + "\n");
          }

          status = new PageStatus();

          // Phenotype Associated Images and Expression sections: count. Since the data can
          // change over time, don't compare individual strings; just look for at least a count of 12.
          // ... count
          sectionErrorCount = 0;
          numOccurrences = 0;

          final int expectedAssociatedImageSize = 12;
          List<String> actualAssociatedImageSections = genePage.getAssociatedImpcImageSections();
          if (actualAssociatedImageSections.size() < expectedAssociatedImageSize) {
              sectionErrorCount++;
              message = "IMPC Phenotype Associated Images (count): [FAILED]. Expected at least 4 strings but found " + actualAssociatedImageSections.size() + ".";
              errorList.add(message);
              status.addError(message);
              System.out.println(message + "\n");
          } else {
              System.out.println("Associate Image Sections (count): [PASSED]\n");
          }

          if (sectionErrorCount == 0) {
              System.out.println("Associated Image Sections (values): [PASSED]\n");
          } else {
              // Dump out all associated image sections.
              for (int i = 0; i < actualAssociatedImageSections.size(); i++) {
                  String actualAssociatedImageSection = actualAssociatedImageSections.get(i);
                  System.out.println("\t[" + i + "]: " + actualAssociatedImageSection);
              }

              // Dump out the missing/duplicated ones.
              System.out.println(status.toStringErrorMessages());
          }

          //test that the order mouse and es cells content from viveks team exists on the page
          WebElement orderAlleleDiv = driver.findElement(By.id("allele2"));//this div is in the ebi jsp which should be populated but without the ajax call success will be empty.
          // This used to be called id="allele". That id still exists but is empty and causes the test to fail here. Now they use id="allele2".
          String text = orderAlleleDiv.getText();
          if (text.length() < 100) {
              message = "Order Mouse content: [FAILED]. less than 100 characters: \n\t'" + text + "'";
              errorList.add(message);
              sectionErrorCount++;
          } else {
              System.out.println("Order Mouse content: [PASSED]\n");
          }

          if ((errorList.isEmpty() && (exceptionList.isEmpty()))) {
              successList.add("testImageThumbnails: [PASSED]");
          }

          testUtils.printEpilogue(testName, start, status, successList.size(), targetCount, 1);
      }

    // Tests gene page with more than one Production Status [blue] order button.
    @Test
//@Ignore
    public void testOrderButtons() throws SolrServerException {
        String testName = "testOrderButtons";
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        int targetCount = 1;
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);

        PageStatus status = new PageStatus();
        String message;
        Date start = new Date();

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " of a total of 1 records.");

        String geneId = "MGI:1353431";
        String target = baseUrl + "/genes/" + geneId;
        System.out.println("URL: " + target);
        GenePage genePage;

        try {
            genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);
        } catch (Exception e) {
            message = "ERROR: Failed to load gene page URL: " + target;
            System.out.println(message);
            fail(message);
            return;
        }

        List<WebElement> buttonElements = genePage.getProductionStatusOrderButtons();

        if ( buttonElements.size() != 3) {
            status.addError("This test expects three order buttons. Number of buttons found: " + buttonElements.size());
        } else {
            for (WebElement buttonElement : buttonElements) {
                buttonElement.click();

                // Verify that we're in the order section.
                boolean expectedUrlEnding = driver.getCurrentUrl().endsWith("#order2");
                if ( ! expectedUrlEnding) {
                    status.addError("Expected url to end in '#order2'. URL: " + driver.getCurrentUrl());
                }

                driver.navigate().back();
            }
        }

        testUtils.printEpilogue(testName, start, status, 1, 1, 1);
    }
}