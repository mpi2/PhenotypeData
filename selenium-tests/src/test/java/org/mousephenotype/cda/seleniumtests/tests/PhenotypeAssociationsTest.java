/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mousephenotype.cda.seleniumtests.tests;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.seleniumtests.support.GenePage;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.cda.seleniumtests.support.Paginator;
import org.mousephenotype.cda.seleniumtests.support.TestUtils;
import org.mousephenotype.cda.solr.service.PostQcService;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mrelac
 *
 * Selenium test for phenotype association coverage ensuring each page works as expected.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/test.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class PhenotypeAssociationsTest {

    private CommonUtils commonUtils = new CommonUtils();
    private List<String> successList = new ArrayList<>();
    protected TestUtils testUtils = new TestUtils();
    private WebDriverWait wait;

    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final int TIMEOUT_IN_SECONDS = 120;         // Increased timeout from 4 to 120 secs as some of the graphs take a long time to load.
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;

    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int threadWaitInMilliseconds = THREAD_WAIT_IN_MILLISECONDS;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${baseUrl}")
    protected String baseUrl;

    @Autowired
    WebDriver driver;

    @Autowired
    Environment env;

    @Autowired
	@Qualifier("postqcService")
    protected PostQcService genotypePhenotypeService;

    @Autowired
    protected PhenotypePipelineDAO phenotypePipelineDAO;

    @Value("${seleniumUrl}")
    protected String seleniumUrl;


    @Before
    public void setup() {
        if (commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeoutInSeconds = commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            threadWaitInMilliseconds = commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));

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


    private RunStatus processRow(WebDriverWait wait, String geneId, int index) {
        RunStatus status = new RunStatus();
        String message;
        String target = baseUrl + "/genes/" + geneId;
        System.out.println("gene[" + index + "] URL: " + target);

        int sumOfPhenotypeCounts = 0;
        int expectedMinimumResultCount = -1;
        try {
            GenePage genePage = new GenePage(driver, wait, target, geneId, phenotypePipelineDAO, baseUrl);
            genePage.selectGenesLength(100);
            // Make sure this page has phenotype associations.
            List<WebElement> phenotypeAssociationElements = driver.findElements(By.cssSelector("div.inner ul li a.filterTrigger"));
            if ((phenotypeAssociationElements == null) || (phenotypeAssociationElements.isEmpty())) {
                status.addError("ERROR: Expected phenotype association but none was found");
                return status;         // This gene page has no phenotype associations.
            }

            // Get the expected result count.
            int expectedResultsCount = genePage.getResultsCount();

            // Loop through all pages, summing the male and female icons. They should match the result counts exactly.
            int actualResultsCount = driver.findElements(By.xpath("//img[@alt = 'Female' or @alt = 'Male']")).size();
            Paginator paginator = genePage.getGeneTable().getPaginator();
            while (paginator.hasNext()) {
                paginator.clickNext();
                actualResultsCount += driver.findElements(By.xpath("//img[@alt = 'Female' or @alt = 'Male']")).size();
            };

            if (expectedResultsCount != actualResultsCount) {
                status.addError("ERROR: Expected minimum result count of " + expectedMinimumResultCount + " but actual sum of phenotype counts was " + sumOfPhenotypeCounts + " for " + driver.getCurrentUrl());
            }
        } catch (NoSuchElementException | TimeoutException te) {
            message = "Expected phenotype associations for MGI_ACCESSION_ID " + geneId + "(page " + target + ") but found none.";
            te.printStackTrace();
            status.addError(message);
        }  catch (Exception e) {
            message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            e.printStackTrace();
            status.addError(message);
        }

        return status;
    }


    // TESTS


    /**
     * Fetches a random set of gene IDs (MARKER_ACCESSION_ID) from the genotype-phenotype
     * core and tests to make sure:
     * <ul><li>this page has phenotype associations</li>
     * <li>the expected result count at the top of the page is equal to the sum of the male and female icons for all pages</li></ul>
     *
     * @throws SolrServerException
     */





    // 12-Nov-2015: THIS TEST HANGS IN FIREFOX ON WINDOWS AND MAC WITH MGI:1336993.






    @Test
@Ignore
    public void testTotalsCount() throws SolrServerException {
        RunStatus status = new RunStatus();
        String testName = "testTotalsCount";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date start = new Date();
        List<String> geneIds = new ArrayList(genotypePhenotypeService.getAllGenesWithPhenotypeAssociations());

geneIds = testUtils.removeKnownBadGeneIds(geneIds);

        int targetCount = testUtils.getTargetCount(env, testName, geneIds, 10);

        geneIds.set(0, "MGI:1336993");         // Always test this mgi id, as it spans two 100-element pages.
        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, geneIds.size());

        // Loop through all genes, testing each one for valid page load.
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        int i = 0;
        for (String geneId : geneIds) {
if (i == 1) geneId = "MGI:1306779";
            if (i >= targetCount) {
                break;
            }

            RunStatus localStatus = processRow(wait, geneId, i);
            if ( ! localStatus.hasErrors()) {
                successList.add("Success");
            }
            status.add(localStatus);

            i++;

            commonUtils.sleep(threadWaitInMilliseconds);
        }

        testUtils.printEpilogue(testName, start, status, successList.size(), targetCount, geneIds.size());
    }
}