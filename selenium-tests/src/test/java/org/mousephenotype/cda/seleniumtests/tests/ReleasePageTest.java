/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 /**
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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
 * This file is intended to contain web tests for graphs - e.g. if there is an
 * IMPC link to a graph (either from a gene page or a phenotype page), there
 * should indeed be a graph present when the link is clicked.
 */

package org.mousephenotype.cda.seleniumtests.tests;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.cda.seleniumtests.support.TestUtils;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 *
 * @author mrelac
 *
 * Selenium test for release page tests.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile:dev}/test.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class ReleasePageTest {

    private CommonUtils commonUtils = new CommonUtils();
    protected TestUtils testUtils = new TestUtils();
    private WebDriverWait wait;

    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final int TIMEOUT_IN_SECONDS = 120;         // Increased timeout from 4 to 120 secs as some of the graphs take a long time to load.
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;

    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @NotNull
    @Value("${baseUrl}")
    protected String baseUrl;

    @Autowired
    WebDriver driver;

    @Autowired
    Environment env;

    @Value("${seleniumUrl}")
    protected String seleniumUrl;


    @Before
    public void setup() {
        if (commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeoutInSeconds = commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            thread_wait_in_ms = commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));

        wait = new WebDriverWait(driver, timeoutInSeconds);

        driver.navigate().refresh();
        commonUtils.sleep(thread_wait_in_ms);
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


    @Test
    public void testJSONVersion() {
        String testName = "testJSONVersion";
        Date start = new Date();
        RunStatus status = new RunStatus();

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        String target = baseUrl + "/release.json";
        driver.get(target);

        String bodyText = driver.findElement(By.xpath("//body")).getText();
        if ( ! bodyText.contains("data_release_version")) {
            status.addError("ERROR: Expected json data_release_version tag. URL: " + target);
        }

        if ( ! status.hasErrors())
            status.successCount++;

        testUtils.printEpilogue(testName, start, status, 1, 1);
    }
}