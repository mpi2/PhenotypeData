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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.cda.seleniumtests.support.TestUtils;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * @author Gautier Koscielny
 * @author mrelac
 *
 * Selenium test for graph query coverage ensuring each graph display works as expected.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class ChartsPageTest {

    CommonUtils commonUtils = new CommonUtils();
    protected TestUtils testUtils = new TestUtils();
    protected WebDriverWait wait;

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

    @Value("${seleniumUrl}")
    protected String seleniumUrl;


    @Before
    public void setUp() throws Exception {
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


    @Test
    public void testExampleCategorical() throws Exception {
        String testName = "testExampleCategorical";
        RunStatus status = new RunStatus();
        Date start = new Date();
        int targetCount = 1;

        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, 1);

        String mgiGeneAcc = "MGI:2444584";
        String impressParameter = "ESLIM_001_001_004";
        String zygosity = "homozygote";
        String geneSymbol = "Mysm1";
        String target = baseUrl + "/charts?accession=" + mgiGeneAcc + "&parameter_stable_id=" + impressParameter + "&zygosity=" + zygosity;
        logger.info("Target: " + target);
        driver.get(target);
        String title  = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("title"))).getText();
        if ( ! title.contains(geneSymbol)) {
            status.addError("ERROR: Expected title to contain '" + geneSymbol + "' but was '" + title + "'.  URL: " + target + "'");
        } else {
            status.successCount++;

        }

        testUtils.printEpilogue(testName, start, status, targetCount, targetCount);
    }

    @Test
    public void testExampleCategorical2() throws Exception {
        String testName = "testExampleCategorical2";
        RunStatus status = new RunStatus();

        Date start = new Date();
        int targetCount = 1;

        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, 1);

        String mgiGeneAcc = "MGI:98373";
        String impressParameter = "M-G-P_014_001_001";
        String zygosity = "homozygote";
        String geneSymbol = "Sparc";
        String target = baseUrl + "/charts?accession=" + mgiGeneAcc + "&parameter_stable_id=" + impressParameter + "&zygosity=" + zygosity;
        logger.info("Target: " + target);
        driver.get(target);
        String title  = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("title"))).getText();
        if ( ! title.contains(geneSymbol)) {
            status.addError("ERROR: Expected title to contain '" + geneSymbol + "' but was '" + title + "'.  URL: " + target + "'");
        } else {
            status.successCount++;
        }

        testUtils.printEpilogue(testName, start, status, targetCount, targetCount);
        System.out.println();
    }

}
