/*******************************************************************************
 * Copyright © 2013 - 2015 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.selenium;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.selenium.config.TestConfig;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.selenium.support.GenePage;
import org.mousephenotype.cda.selenium.support.TestUtils;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

@RunWith(SpringRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@SpringBootTest(classes = TestConfig.class)
public class GenePageTest {

    private CommonUtils   commonUtils = new CommonUtils();
    private WebDriver     driver;
    private TestUtils     testUtils   = new TestUtils();
    private WebDriverWait wait;

    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final int    TIMEOUT_IN_SECONDS = 120;
    private final int    THREAD_WAIT_IN_MILLISECONDS = 20;

    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int threadWaitInMilliseconds = THREAD_WAIT_IN_MILLISECONDS;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    private DesiredCapabilities desiredCapabilities;

    @Autowired
    private Environment env;

    @Autowired
    private GeneService geneService;

    @Autowired
    private PhenotypePipelineDAO phenotypePipelineDAO;

    @NotNull
    @Value("${base_url}")
    private String baseUrl;

    @Value("${seleniumUrl}")
    private String seleniumUrl;


    @Before
    public void setup() throws MalformedURLException {
        driver = new RemoteWebDriver(new URL(seleniumUrl), desiredCapabilities);
        if (commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeoutInSeconds = commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            threadWaitInMilliseconds = commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));

        wait = new WebDriverWait(driver, timeoutInSeconds);
    }

    @After
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }


    // PRIVATE METHODS

    
    private void geneIdsTestEngine(String testName, List<String> geneIds) throws SolrServerException {
        RunStatus masterStatus = new RunStatus();
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        String target = "";
        String message;
        Date start = new Date();

        int targetCount = testUtils.getTargetCount(env, testName, geneIds, 10);
        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, geneIds.size());

        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;

            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);

            try {
                GenePage genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);
                boolean phenotypesTableRequired = false;
                RunStatus status = genePage.validate(phenotypesTableRequired);
                if (status.hasErrors()) {
                    System.out.println(status.toStringErrorMessages());
                }
                masterStatus.add(status);
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                te.printStackTrace();
                masterStatus.addError(message);
                commonUtils.sleep(threadWaitInMilliseconds);
                continue;
            } catch (Exception e) {
                message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
                e.printStackTrace();
                masterStatus.addError(message);
                commonUtils.sleep(threadWaitInMilliseconds);
                continue;
            }

            if ( ! masterStatus.hasErrors()) {
                masterStatus.successCount++;
            }

            commonUtils.sleep(threadWaitInMilliseconds);
        }

        testUtils.printEpilogue(testName, start, masterStatus, targetCount, geneIds.size());
    }

    private void tick(String phenoStatus, String prodCentre, String phenoCentre) {
        // If no parameters were specified, set target to the default search page.
        String target = baseUrl + "/search/gene?kw=*";
        String fields = "";
        if (!((phenoStatus == null) && (prodCentre == null) && (phenoCentre == null))) {
            target += "&fq=";
            if (phenoStatus != null) {
                switch (phenoStatus) {
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
                if (!fields.isEmpty()) {
                    fields += " AND ";
                    fields += "(latest_production_centre:\"" + prodCentre + "\")";
                }
            }

            if (phenoCentre != null) {
                if (!fields.isEmpty()) {
                    fields += " AND ";
                    fields += "(latest_phenotyping_centre:\"" + phenoCentre + "\")";
                }
            }

            target += fields;
        }

        driver.get(target);
    }

    /**
     * Given that the current page is the gene search page, returns the number
     * sandwiched between the 'Found' and 'genes' terms; e.g., given the string
     * 'Found 5 genes', returns the number 5. Returns 0 if there is no number
     * or no such formatted string.
     * @return gene count if found; 0 otherwise
     */
    private int getGeneCount() {
        WebElement filterCountElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//li[@id='gene']/span[contains(@class, 'fcount')]")));
        String filterCount = filterCountElement.getText();
        Integer i = commonUtils.tryParseInt(filterCount);

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
        RunStatus status = new RunStatus();
        String testName = "testForBadGeneIds";
        List<String> geneIds = new ArrayList(geneService.getAllNonConformingGenes());
        String target = "";
        String message;
        Date start = new Date();

        int targetCount = testUtils.getTargetCount(env, testName, geneIds, 10);
        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, geneIds.size());

        // Loop through all non-conforming genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        for (String geneId : geneIds) {
            if (i >= targetCount) {
                break;
            }
            i++;

            target = baseUrl + "/genes/" + geneId;
            System.out.println("gene[" + i + "] URL: " + target);

            try {
                driver.get(target);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
            } catch (NoSuchElementException | TimeoutException te) {
                message = "Expected page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                te.printStackTrace();
                status.addError(message);
            } catch (Exception e) {
                status.addError("EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage());
                e.printStackTrace();
                continue;
            }

            message = "SUCCESS: MGI_ACCESSION_ID " + geneId + ". URL: " + target;
            Thread.sleep(threadWaitInMilliseconds);
        }

        if (( ! geneIds.isEmpty()) && ( ! status.hasErrors()))
            status.successCount++;

        testUtils.printEpilogue(testName, start, status, targetCount, geneIds.size());
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
    public void testPageForGeneIds() throws SolrServerException, IOException {
        String testName = "testPageForGeneIds";
        List<String> geneIds = new ArrayList<>(geneService.getAllGenes());

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
    public void testPageForGenesByLatestPhenotypeStatusStartedAndPhenotypeCentreWTSI() throws SolrServerException, IOException {
        String testName = "testPageForGenesByLatestPhenotypeStatusStartedAndPhenotypeCentreWTSI";
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndPhenotypeCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.CENTRE_WTSI));

        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Started", null, GeneService.GeneFieldValue.CENTRE_WTSI);
        int actualGeneCount = getGeneCount();
        int expectedGeneCount = geneIds.size();
        assertEquals(actualGeneCount, expectedGeneCount);

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
    public void testPageForGenesByLatestPhenotypeStatusStartedAndProductionCentreWTSI() throws SolrServerException, IOException {
        String testName = "testPageForGenesByLatestPhenotypeStatusStartedAndProductionCentreWTSI";
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_STARTED, GeneService.GeneFieldValue.CENTRE_WTSI));

        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Started", GeneService.GeneFieldValue.CENTRE_WTSI, null);
        int actualGeneCount = getGeneCount();
        int expectedGeneCount = geneIds.size();
        assertEquals(actualGeneCount, expectedGeneCount);

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
    public void testPageForGenesByLatestPhenotypeStatusCompleteAndPhenotypeCentreWTSI() throws SolrServerException, IOException {
        String testName = "testPageForGenesByLatestPhenotypeStatusCompleteAndPhenotypeCentreWTSI";
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndPhenotypeCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_COMPLETE, GeneService.GeneFieldValue.CENTRE_WTSI));

        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Complete", null, GeneService.GeneFieldValue.CENTRE_WTSI);
        int actualGeneCount = getGeneCount();
        int expectedGeneCount = geneIds.size();
        assertEquals(actualGeneCount, expectedGeneCount);

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
    public void testPageForGenesByLatestPhenotypeStatusCompleteAndProductionCentreWTSI() throws SolrServerException, IOException {
        String testName = "testPageForGenesByLatestPhenotypeStatusCompleteAndProductionCentreWTSI";
        List<String> geneIds = new ArrayList(geneService.getGenesByLatestPhenotypeStatusAndProductionCentre(GeneService.GeneFieldValue.PHENOTYPE_STATUS_COMPLETE, GeneService.GeneFieldValue.CENTRE_WTSI));

        // Check that the count of fetched genes looks correct by ticking the appropriate boxes for this test and comparing
        // the result against the number of gene rows.
        tick("Complete", GeneService.GeneFieldValue.CENTRE_WTSI, null);
        int actualGeneCount = getGeneCount();
        int expectedGeneCount = geneIds.size();
        assertEquals(actualGeneCount, expectedGeneCount);

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
        RunStatus status = new RunStatus();
        String testName = "testInvalidGeneId";
        String target = "";
        int targetCount = 1;
        String message;
        Date start = new Date();
        String geneId = "junkBadGene";
        final String EXPECTED_ERROR_MESSAGE = "Oops! The marker junkBadGene is not currently part of the IMPC project.";

        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, 1);

        boolean found = false;
        target = baseUrl + "/genes/" + geneId;
        System.out.println("URL: " + target);

        try {
            driver.get(target);
            List<WebElement> geneLinks = (new WebDriverWait(driver, timeoutInSeconds))
                    .until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.node h1")));

            if (geneLinks == null) {
                message = "ERROR: Expected error page for MP_TERM_ID " + geneId + "(" + target + ") but found none.";
                status.addError(message);
            }

            for (WebElement element : geneLinks) {
                if (element.getText().compareTo(EXPECTED_ERROR_MESSAGE) == 0) {
                    found = true;
                    break;
                }
            }

            if ( ! found) {
                message = "ERROR: Expected error page for MGI_ACCESSION_ID " + geneId + "(" + target + ") but found none.";
                status.addError(message);
            }
        } catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            e.printStackTrace();
            status.addError(message);
        }

        if ( ! status.hasErrors())
            status.successCount++;

        testUtils.printEpilogue(testName, start, status, targetCount, 1);
    }

    // Test for the minimum number of blue and orange icons. Match orange icons with phenotype summary strings
    // appearing on the left side of the gene page.
    @Test
//@Ignore
    public void testAkt2() throws Exception {
        String testName = "testAkt2";
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        int numOccurrences;

        RunStatus masterStatus = new RunStatus();
        String message;
        Date start = new Date();

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        String geneId = "MGI:104874";
        String target = baseUrl + "/genes/" + geneId;
        logger.info("URL: " + target);
        GenePage genePage;

        try {
            genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);
        } catch (Exception e) {
            message = "ERROR: Failed to load gene page URL: " + target;
            System.out.println(message);
            masterStatus.addError(message);
            testUtils.printEpilogue(testName, start, masterStatus, 1, 1);
            return;
        }

        // Title
        String title = genePage.getTitle();
        if (title.contains("Akt2")) {
            System.out.println("PASSED [Title]");
        } else {
            message = "FAILED [Title]: Expected title to contain 'Akt2' but it was not found. Title: '" + title + "'";
            masterStatus.addError(message);
            System.out.println(message);
        }

        // Section Titles: count and values (e.g. 'Gene: Akt2', 'Phenotype associations for Akt2', 'Pre-QC phenotype heatmap', etc.)
        // ... count
        String[] sectionTitlesArray = {
                "Gene: Akt2",
                "Phenotype associations for Akt2",
                "Expression",
                "Associated Images",
                "Disease Models",
                "Order Mouse and ES Cells",};
        List<String> expectedSectionTitles = Arrays.asList(sectionTitlesArray);
        List<String> actualSectionTitles = genePage.getSectionTitles();
        if (actualSectionTitles.size() != sectionTitlesArray.length) {
            message = "FAILED [Section Titles (count)]: Expected " + sectionTitlesArray.length + " section titles but found " + actualSectionTitles.size() + ":";
            masterStatus.addError(message);
            System.out.println(message);
        } else {
            System.out.println("PASSED [Section Titles (count)]");
        }
        // ... values
        RunStatus status = new RunStatus();
        for (String expectedSectionTitle : expectedSectionTitles) {
            if ( ! actualSectionTitles.contains(expectedSectionTitle)) {
                message = "\tERROR: Mismatch: Expected section named '" + expectedSectionTitle + "' but wasn't found.";
                status.addError(message);
            }
        }
        for (String actualSectionTitle : actualSectionTitles) {
            if ( ! expectedSectionTitles.contains(actualSectionTitle)) {
                message = "\tERROR: Mismatch: Found section named '" + actualSectionTitle + "' but wasn't expected.";
                status.addError(message);
            } else {
                numOccurrences = TestUtils.count(actualSectionTitles, actualSectionTitle);
                if (numOccurrences > 1) {
                    message = "\tERROR: " + numOccurrences + " occurrences of '" + actualSectionTitle + "' were found.";
                    status.addError(message);
                }
            }
        }
        if (status.hasErrors()) {
            // Dump out all titles.
            for (int i = 0; i < actualSectionTitles.size(); i++) {
                String sectionTitle = actualSectionTitles.get(i);
                System.out.println("\t[" + i + "]: " + sectionTitle);
            }

            // Dump out the missing titles.
            System.out.println(status.toStringErrorMessages());
            System.out.println("FAILED [Section Titles (values)]");
            masterStatus.add(status);
        } else {
            System.out.println("PASSED [Section Titles (values)]");
        }

        // Buttons: count and labels
        // ... count
        String[] buttonLabelsArray = {
                "Login to register interest",
                "Order",
                "Akt2 Measurements",
                "",
                "",
                ""
                };
        List<String> expectedButtonLabels = Arrays.asList(buttonLabelsArray);
        List<String> actualButtonLabels = genePage.getButtonLabels();
        if (actualButtonLabels.size() < buttonLabelsArray.length) {
            message = "FAILED [Buttons (count)]. Expected " + buttonLabelsArray.length + " buttons but found " + actualButtonLabels.size() + ".";
            masterStatus.addError(message);
            System.out.println(message);
        } else {
            System.out.println("PASSED [Buttons (count)]");
        }
        // ... values
        status = new RunStatus();
        for (String expectedSectionTitle : expectedButtonLabels) {
            if ( ! actualButtonLabels.contains(expectedSectionTitle)) {
                message = "\tERROR: Mismatch: Expected button named '" + expectedSectionTitle + "' but wasn't found.";
                status.addError(message);
            }
        }

        if (status.hasErrors()) {
            // Dump out all buttons.
            for (int i = 0; i < actualButtonLabels.size(); i++) {
                String sectionTitle = actualButtonLabels.get(i);
                System.out.println("\t[" + i + "]: " + sectionTitle);
            }

            // Dump out the missing buttons.
            System.out.println(status.toStringErrorMessages());
            System.out.println("FAILED [Buttons (values)]");
            masterStatus.add(status);
        } else {
            System.out.println("PASSED [Buttons (values)]");
        }

        // Significant Abnormalities: count and values. As of 15-September-2015, there should be at least:
        //   9 - tested but not significant (blue)
        //   5 - significant (orange)
        // ... count
        numOccurrences = 0;
        final List<String> expectedSignificantList = Arrays.asList(
                new String[]{
                        "Akt2 growth/size/body region phenotype measurements"
                        , "Akt2 homeostasis/metabolism phenotype or adipose tissue phenotype measurements"
                        , "Akt2 behavior/neurological phenotype or nervous system phenotype measurements"
                        , "Akt2 skeleton phenotype measurements"
                        , "Akt2 immune system phenotype or hematopoietic system phenotype measurements"
                });
        final List<String> expectedNotSignificantList = Arrays.asList(
                new String[]{
                        "Akt2 reproductive system phenotype measurements"
                        , "Akt2 cardiovascular system phenotype measurements"
                        , "Akt2 digestive/alimentary phenotype or liver/biliary system phenotype measurements"
                        , "Akt2 renal/urinary system phenotype measurements"
                        , "Akt2 limbs/digits/tail phenotype measurements"
                        , "Akt2 integument phenotype or pigmentation phenotype measurements"
                        , "Akt2 craniofacial phenotype measurements"
                        , "Akt2 endocrine/exocrine gland phenotype measurements"
                        , "Akt2 vision/eye phenotype measurements"
                });

        // Significant Abnormalities: count
        List<String> actualSignificantList = genePage.getSignificantAbnormalities();
        if (actualSignificantList.size() < expectedSignificantList.size()) {
            message = "FAILED [Significant Abnormalities (count)]. Expected " + expectedSignificantList.size() + " strings but found " + actualSignificantList.size() + ".";
            masterStatus.addError(message);
            System.out.println(message);
        } else {
            System.out.println("PASSED [Significant Abnormalities (count)]");
        }

        // Sum of Significant and Non-Significant Abnormalities (count): validate that the sum of expectedSignificant and expectedNotSignificant are at least the sum of the expected list sizes.
        List<String> actualNotSignificantList = genePage.getNotSignificantAbnormalities();
        if (actualSignificantList.size() + actualNotSignificantList.size() < expectedSignificantList.size() + expectedNotSignificantList.size()) {
            message = "FAILED [Sum of Significant and Non-Significant Abnormalities (count)]. Expected "
                    + expectedSignificantList.size() + expectedNotSignificantList.size()
                    + " strings but found "
                    + Integer.sum(actualSignificantList.size(), actualNotSignificantList.size()) + ".";
            masterStatus.addError(message);
            System.out.println(message);
        } else {
            System.out.println("PASSED [Sum of Significant and Non-Significant Abnormalities (count)]");
        }

        // Significant Abnormalities (values)
        boolean hasErrors = false;
        for (String actualSignificant : actualSignificantList) {
            if (!expectedSignificantList.contains(actualSignificant)) {
                message = "\tERROR: Mismatch: Expected significant abnormality named '" + actualSignificant + "' but wasn't found.";
                masterStatus.addError(message);
                hasErrors = true;
            }
        }
        if (hasErrors) {
            System.out.println("FAILED [Significant Abnormalities (values)]");
        } else {
            System.out.println("PASSED [Significant Abnormalities (values)]");
        }

        // Not Significant Abnormalities (values): Validate that the actual NotSignificant abnormality values are in either the expectedSignificantList or
        // the expectedNonSignificantList.
        List<String> both = new ArrayList<>();
        both.addAll(expectedSignificantList);
        both.addAll(expectedNotSignificantList);
        hasErrors = false;
        for (String actualNotSignificant : actualNotSignificantList) {
            if (!both.contains(actualNotSignificant)) {
                message = "\tERROR: Mismatch: Couldn't find actual Not Significant abnormality named '" + actualNotSignificant + "'";
                masterStatus.addError(message);
                hasErrors = true;
            }
        }
        if (hasErrors) {
            System.out.println("FAILED [Not Significant Abnormalities (values)]");
        } else {
            System.out.println("PASSED [Not Significant Abnormalities (values)]");
        }

        // Phenotype Associated Images and Expression sections: count. Since the data can
        // change over time, don't compare individual strings; just look for at least a count of 12.
        // ... count
        status = new RunStatus();
        final int expectedAssociatedImageSize = 12;
        List<String> actualAssociatedImageSections = genePage.getAssociatedImageSections();
        if (actualAssociatedImageSections.size() < expectedAssociatedImageSize) {
            message = "FAILED [Associated Image Sections (count)]. Expected at least 12 strings but found " + actualAssociatedImageSections.size() + ":";
            status.addError(message);
            System.out.println(message);
        } else {
            System.out.println("PASSED [Associated Image Sections (count)]");
        }

        if (status.hasErrors()) {
            // Dump out all associated image sections.
            for (int i = 0; i < actualAssociatedImageSections.size(); i++) {
                String actualAssociatedImageSection = actualAssociatedImageSections.get(i);
                System.out.println("\t[" + i + "]: " + actualAssociatedImageSection);
            }

            // Dump out the missing/duplicated ones.
            System.out.println(masterStatus.toStringErrorMessages());
            System.out.println("FAILED [Associated Image Sections (values)]");
            masterStatus.add(status);
        } else {
            System.out.println("PASSED [Associated Image Sections (values)]");
        }

        //test that the order mouse and es cells content from viveks team exists on the page
        WebElement orderAlleleDiv = driver.findElement(By.id("order2"));//this div is in the ebi jsp which should be populated but without the ajax call success will be empty.
        // This used to be called id="allele". That id still exists but is empty and causes the test to fail here. Now they use id="allele2".
        String text = orderAlleleDiv.getText();
        if (text.length() < 100) {
            message = "FAILED [Order Mouse content]. less than 100 characters: \n\t'" + text + "'";
            masterStatus.addError(message);
            System.out.println(message);
        } else {
            System.out.println("PASSED [Order Mouse content]");
        }

        if ( ! masterStatus.hasErrors()) {
            masterStatus.successCount++;
        }

        testUtils.printEpilogue(testName, start, masterStatus, 1, 1);
    }

    // 06-Apr-2016 On DEV, the Arsk gene has no All Adult Data button, even though there are statistical results for it.
    // Test to make sure it has an All Adult Data button.
    @Test
//@Ignore
    public void testAllAdultPhenotypes() {
        String testName = "testAllAdultPhenotypes";
        RunStatus status = new RunStatus();
        String message;
        Date start = new Date();

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        String geneId = "MGI:1924291";
        String target = baseUrl + "/genes/" + geneId;
        logger.info("URL: " + target);
        GenePage genePage;

        try {
            genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);

            List<WebElement> elements = driver.findElements(By.xpath("//a[@id='allAdultDataBtn']"));
            if (elements.isEmpty()) {
                status.addError("ERROR: Arsk has no 'All Adult Data' button.");
            } else {
                if ( ! elements.get(0).getAttribute("href").contains("/experiments?geneAccession=MGI:1924291")) {
                    status.addError("ERROR: MGI:109331 has no 'All Adult Data' button (2).");
                }
            }

        } catch (Exception e) {
            status.addError("ERROR: Failed to load gene page URL: " + target + ". Reason: '" + e.getLocalizedMessage() + "'.");
        }

        if ( ! status.hasErrors())
            status.successCount++;

        testUtils.printEpilogue(testName, start, status, 1, 1);
    }

    @Test
//@Ignore
    public void testImageThumbnails() throws Exception {
        String testName = "testImageThumbnails";
        int targetCount = 1;
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);

        RunStatus status = new RunStatus();
        RunStatus masterStatus = new RunStatus();
        String message;
        Date start = new Date();

        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, 1);

        String geneId = "MGI:1935151";
        String target = baseUrl + "/genes/" + geneId;
        System.out.println("URL: " + target);
        GenePage genePage = null;

        try {
            genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);
        } catch (Exception e) {
            message = "ERROR: Failed to load gene page URL: " + target;
            status.addError(message);
            testUtils.printEpilogue(testName, start, status, targetCount, 1);
            return;
        }

        // Title
        String title = genePage.getTitle();
        if (title.contains("Klf7")) {
            System.out.println("Title: [PASSED]");
        } else {
            message = "Title: [FAILED]: Expected title to contain 'Klf7' but it was not found. Title: '" + title + "'";
            status.addError(message);
        }
        masterStatus.add(status);
        status = new RunStatus();

        // Phenotype Associated Images : count. Since the data can change over time, compare only the first four.
        List<String> expectedPhenotypeAssociatedImageSections = Arrays.asList(
            new String[] {
                "M-Mode Images",
                "XRay Images Dorso Ventral",
                "XRay Images Lateral Orientation",
                "Click-evoked + 6 To 30kHz Tone Waveforms (pdf Format)"
        });

        List<String> actualPhenotypeAssociatedImageSections = genePage.getAssociatedImpcImageUrls();
        if (actualPhenotypeAssociatedImageSections.size() < expectedPhenotypeAssociatedImageSections.size()) {
            message = "IMPC Phenotype Associated Images (count): [FAILED]. Expected at least " + expectedPhenotypeAssociatedImageSections.size() + " strings but found " + actualPhenotypeAssociatedImageSections.size() + ".";
            status.addError(message);
        } else {
            System.out.println("IMPC Phenotype Associated Images (count): [PASSED]");
        }

        masterStatus.add(status);
        status = new RunStatus();

        // Test for at least the four expected strings.
        for (String expectedPhenotypeAssociatedImageSection : expectedPhenotypeAssociatedImageSections) {
            boolean subsetFound = false;
            for (String actualPhenotypeAssociatedImageSection : actualPhenotypeAssociatedImageSections) {
                if (actualPhenotypeAssociatedImageSection.trim().toLowerCase().contains(expectedPhenotypeAssociatedImageSection.trim().toLowerCase())) {
                    subsetFound = true;
                    break;
                }
            }

            if ( ! subsetFound) {
                message = "IMPC Phenotype Associated Images (values): [FAILED]. Expected:\n"
                        + testUtils.buildIndexedList(expectedPhenotypeAssociatedImageSections)
                        + "\nActual:\n"
                        + testUtils.buildIndexedList(actualPhenotypeAssociatedImageSections);
                status.addError(message);
                break;
            }
        }

        if ( ! status.hasErrors()) {
            System.out.println("IMPC Phenotype Associated Images (values): [PASSED]");
        }

        masterStatus.add(status);
        status = new RunStatus();

        //test that the order mouse and es cells content from viveks team exists on the page
        WebElement orderAlleleDiv = driver.findElement(By.id("order2"));//this div is in the ebi jsp which should be populated but without the ajax call success will be empty.
        // This used to be called id="allele". That id still exists but is empty and causes the test to fail here. Now they use id="allele2".
        String text = orderAlleleDiv.getText();
        if (text.length() < 100) {
            message = "Order Mouse content: [FAILED]. less than 100 characters: \n\t'" + text + "'";
            status.addError(message);
        } else {
            System.out.println("Order Mouse content: [PASSED]");
        }

        masterStatus.add(status);

        if ( ! status.hasErrors())
            status.successCount++;

        testUtils.printEpilogue(testName, start, status, targetCount, 1);
    }

    // Tests gene page with more than one Production Status [blue] order button.
    @Test
//@Ignore
    public void testOrderButtons() throws SolrServerException {
        String testName = "testOrderButtons";
        int targetCount = 1;
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);

        RunStatus status = new RunStatus();
        String message;
        Date start = new Date();

        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, 1);

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
        if (buttonElements.size() < 3) {
            status.addError("This test expects at least three production status order buttons. Number of buttons found: " + buttonElements.size());
        } else {
            for (WebElement buttonElement : buttonElements) {
                buttonElement.click();

                // Verify that we're in the order section.
                boolean expectedUrlEnding = driver.getCurrentUrl().endsWith("#order2");
                if (!expectedUrlEnding) {
                    status.addError("Expected url to end in '#order2'. URL: " + driver.getCurrentUrl());
                }

                driver.navigate().back();
            }
        }

        buttonElements = genePage.getphenotypingStatusOrderButtons();
        if (buttonElements.size() < 1) {
            status.addError("This test expects at least one phenotyping status order button. Number of buttons found: " + buttonElements.size());
        }

        if ( ! status.hasErrors())
            status.successCount++;

        testUtils.printEpilogue(testName, start, status, 1, 1);
    }
}