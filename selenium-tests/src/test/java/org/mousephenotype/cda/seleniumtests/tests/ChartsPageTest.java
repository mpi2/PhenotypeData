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
import org.mousephenotype.cda.seleniumtests.support.PageStatus;
import org.mousephenotype.cda.seleniumtests.support.TestUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author Gautier Koscielny
 * @author mrelac
 *
 * Selenium test for graph query coverage ensuring each graph display works as expected.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/application.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class ChartsPageTest {

    protected TestUtils testUtils = new TestUtils();

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
        PageStatus status = new PageStatus();
        List<String> successList = new ArrayList<>();
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        int targetCount = 1;

        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, 1);

        String mgiGeneAcc = "MGI:2444584";
        String impressParameter = "ESLIM_001_001_004";
        String zygosity = "homozygote";
        String geneSymbol = "Mysm1";
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        String target = baseUrl + "/charts?accession=" + mgiGeneAcc + "&parameter_stable_id=" + impressParameter + "&zygosity=" + zygosity;
        logger.info("Target: " + target);
        driver.get(target);
        String title = driver.findElement(By.className("title")).getText();
        if ( ! title.contains(geneSymbol)) {
            status.addError("ERROR: Expected title to contain '" + geneSymbol + "' but was '" + title + "'.  URL: " + target + "'");
        } else {
            successList.add("OK");

        }

        testUtils.printEpilogue(testName, start, status, successList.size(), targetCount, targetCount);
    }

    @Test
    public void testExampleCategorical2() throws Exception {
        String testName = "testExampleCategorical2";
        PageStatus status = new PageStatus();

        List<String> successList = new ArrayList<>();
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(TestUtils.DATE_FORMAT);
        int targetCount = 1;

        testUtils.logTestStartup(logger, this.getClass(), testName, targetCount, 1);

        String mgiGeneAcc = "MGI:98373";
        String impressParameter = "M-G-P_014_001_001";
        String zygosity = "homozygote";
        String geneSymbol = "Sparc";
        driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
        String tempUrl = baseUrl + "/charts?accession=" + mgiGeneAcc + "&parameter_stable_id=" + impressParameter + "&zygosity=" + zygosity;
        driver.get(tempUrl);
        String title = driver.findElement(By.className("title")).getText();
        if ( ! title.contains(geneSymbol)) {
            status.addError("ERROR: Expected title to contain '" + geneSymbol + "' but was '" + title + "'.  URL: " + tempUrl + "'");
        } else {
            successList.add("OK");
        }

        testUtils.printEpilogue(testName, start, status, successList.size(), targetCount, targetCount);
        System.out.println();
    }

}
