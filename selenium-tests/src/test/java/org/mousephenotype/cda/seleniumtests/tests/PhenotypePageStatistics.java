/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 *
 * This selenium test walks through all phenotype pages, compiling a list of phenotype pages with:
 * <ul>
 * <li>only phenotype table (and no graphs)</li>
 * <li>only images (and no phenotype table)</li>
 * <li>both a phenotype table and one or more images</li>
 * <li>no phenotype table and no images</li>
 * </ul>
 */

package org.mousephenotype.cda.seleniumtests.tests;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.seleniumtests.support.TestUtils;
import org.mousephenotype.cda.solr.service.MpService;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mrelac
 *
 * Selenium test for phenotype page statistics.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class PhenotypePageStatistics {

    private CommonUtils commonUtils = new CommonUtils();
    protected TestUtils testUtils = new TestUtils();
    private WebDriverWait wait;

    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final int TIMEOUT_IN_SECONDS = 120;         // Increased timeout from 4 to 120 secs as some of the graphs take a long time to load.
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;

    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int threadWaitInMilliseconds = THREAD_WAIT_IN_MILLISECONDS;

    private final String NO_PHENOTYPE_ASSOCIATIONS = "Phenotype associations to genes and alleles will be available once data has completed quality control.";
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${baseUrl}")
    protected String baseUrl;

    @Autowired
    WebDriver driver;

    @Autowired
    Environment env;

    @Autowired
    protected MpService mpService;

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

    /**
     * Walks the phenotype core collecting the count of: [phenotype] table only,
     * image(s) only, both, and none.
     *
     * @throws SolrServerException
     */
    @Test
    public void testCollectTableAndImageStatistics() throws SolrServerException, IOException {
        RunStatus masterStatus = new RunStatus();
        String testName = "testCollectTableAndImageStatistics";
        List<String> phenotypeIds = new ArrayList(mpService.getAllPhenotypes());
        String target = "";

        List<String> phenotypeTableOnly = new ArrayList();
        List<String> imagesOnly = new ArrayList();
        List<String> both = new ArrayList();
        String message;
        Date start = new Date();

        int pagesWithPhenotypeTableCount = 0;
        int pagesWithImageCount = 0;
        int pagesWithBoth = 0;
        List<String> urlsWithNeitherPhenotypeTableNorImage = new ArrayList();

        int targetCount = testUtils.getTargetCount(env, testName, phenotypeIds, 10);
        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, phenotypeIds.size());

        // Loop through first targetCount phenotype MGI links, testing each one for valid page load.
        int i = 0;
        for (String phenotypeId : phenotypeIds) {
            RunStatus status = new RunStatus();
            if (i >= targetCount) {
                break;
            }
            i++;

            boolean found = false;

            target = baseUrl + "/phenotypes/" + phenotypeId;
            logger.debug("phenotype[" + i + "] URL: " + target);

            try {
                driver.get(target);
                (new WebDriverWait(driver, timeoutInSeconds)).until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("h1#top")));
                found = true;
                boolean hasPhenotypeTable = false;
                boolean hasImage = false;

                // Are there any phenotype associations?
                List<WebElement> elementList = driver.findElements(By.cssSelector("div.alert"));

                hasPhenotypeTable = ! testUtils.contains(elementList, NO_PHENOTYPE_ASSOCIATIONS);

                // Are there any images?
                elementList = driver.findElements(By.cssSelector("h2#section"));
                if (testUtils.contains(elementList, "Images")) {
                    List<WebElement> imagesAccordion = driver.findElements(By.cssSelector("div.accordion-body ul li"));
                    if (imagesAccordion.isEmpty()) {
                        status.addError("ERROR: Found Image tag but there were no image links");
                    } else {
                        hasImage = true;
                    }
                }

                if (hasPhenotypeTable && hasImage) {
                    pagesWithBoth++;
                    both.add(driver.getCurrentUrl());
                } else if (hasPhenotypeTable) {
                    pagesWithPhenotypeTableCount++;
                    phenotypeTableOnly.add(driver.getCurrentUrl());
                } else if (hasImage) {
                    pagesWithImageCount++;
                    imagesOnly.add(driver.getCurrentUrl());
                } else {
                    urlsWithNeitherPhenotypeTableNorImage.add(driver.getCurrentUrl());
                }
            } catch (Exception e) {
                e.printStackTrace();
                status.addError("EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage());
            }

            if (found) {
                status.successCount++;
            } else {
                status.addError("h1 with id 'top' not found.");
            }

            masterStatus.add(status);
        }

        System.out.println("\nPhenotype pages with tables but no images: " + pagesWithPhenotypeTableCount);
        for (String s : phenotypeTableOnly) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("Phenotype pages with images but no tables: " + pagesWithImageCount);
        for (String s : imagesOnly) {
            System.out.println(s);
        }
        System.out.println();
        System.out.println("Phenotype pages with both tables and images: " + pagesWithBoth);
        for (String s : both) {
            System.out.println(s);
        }
        System.out.println();

        if ( ! urlsWithNeitherPhenotypeTableNorImage.isEmpty()) {
            System.out.println("WARNING: The following " + urlsWithNeitherPhenotypeTableNorImage.size() + " results had neither phenotype table nor images:");
            System.out.println("WARNING: Phenotype pages with neither phenotype table nor images: " + urlsWithNeitherPhenotypeTableNorImage.size());
            for (String s : urlsWithNeitherPhenotypeTableNorImage) {
                System.out.println("\t" + s);
            }
        }

        testUtils.printEpilogue(testName, start, masterStatus, targetCount, phenotypeIds.size());
    }
}