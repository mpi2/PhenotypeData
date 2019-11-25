/**
 * Copyright © 2014 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.mousephenotype.cda.selenium;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.selenium.config.TestConfig;
import org.mousephenotype.cda.selenium.support.GenePage;
import org.mousephenotype.cda.selenium.support.TestUtils;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.solr.service.dto.GeneDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author mrelac
 *
 * Selenium test for impc images coverage ensuring each page works as expected.
 */


// FIXME FIXME FIXME
@Ignore



@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class
ImpcImagesTest {

    private CommonUtils   commonUtils = new CommonUtils();
    private WebDriver     driver;
    private TestUtils     testUtils   = new TestUtils();
    private WebDriverWait wait;

    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final int TIMEOUT_IN_SECONDS = 120;
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;

    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int threadWaitInMilliseconds = THREAD_WAIT_IN_MILLISECONDS;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    @Value("${paBaseUrl}")
    private String paBaseUrl;

    @Value("${seleniumUrl}")
    private String seleniumUrl;


    @NotNull @Autowired
    private DesiredCapabilities desiredCapabilities;

    @NotNull @Autowired
    private GeneService geneService;


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
        String target = "";
        Date start = new Date();

        testUtils.logTestStartup(logger, this.getClass(), testName, geneIds.size(), geneIds.size());

        // Loop through all genes, testing each one for valid page load.
        int i = 0;
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        for (String geneId : geneIds) {
            RunStatus status = new RunStatus();
            target = paBaseUrl + "/genes/" + geneId;

            try {
                driver.get(target);

                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));

                GenePage genePage = new GenePage(driver, wait, target, geneId, paBaseUrl);

                if (genePage.hasImpcImages()) {
                    List<String> parameters = genePage.getAssociatedImpcImageUrls();
                    if (parameters.isEmpty()) {
                        status.addError("Parameter list is empty!");
                    }
                } else {
                    status.addError("No IMPC Images found for " + target);
                }

            } catch (Exception e) {
                e.printStackTrace();
                status.addError(e.getLocalizedMessage());
            }

            if (status.hasErrors()) {
                System.out.println("[" + i + "] [FAIL]. URL: " + target + ". REASON: " + StringUtils.join(status.getErrorMessages(), ", "));
            } else {
                status.successCount++;
                System.out.println("[" + i + "] [PASS]. URL: " + target);
            }

            i ++;
            masterStatus.add(status);
        }

        testUtils.printEpilogue(testName, start, masterStatus, geneIds.size(), geneIds.size());
    }


    // TESTS

// FIXME FIXME FIXME
    @Test
@Ignore
    public void testImpcImagesOnGenePage() throws Exception {

        String testName = "testImpcImagesOnGenePage";
        ArrayList<String> genes = new ArrayList<>();
		// genes.add("Akt2"); should fail on Akt2 as no impc_images

        genes.add("Ccdc120");
        genes.add("Cenpo");
        genes.add("Cwc27");
        genes.add("Eya4");
        genes.add("Htr1b");
        genes.add("Lrp1");
        genes.add("Osm");
        genes.add("Ppp2r2b");
        genes.add("Prkab1");
        genes.add("Rhbdl1");
        genes.add("Rxfp2");
        genes.add("Snrnp200");
        genes.add("Tpgs2");
        genes.add("Wee1");

        genes.add("Abcb11");
        genes.add("Baz1a");
        genes.add("C3");
        genes.add("Ddx41");
        genes.add("Dnajb7");
        genes.add("Idh1");
        genes.add("Ovgp1");
        genes.add("Palb2");
        genes.add("Pipox");
        genes.add("Pkd2l2");
        genes.add("Plekhm1");
        genes.add("Stk16");
        genes.add("Vps13d");
        List<String> geneIds = new ArrayList<>();

        for (String gene : genes) {
            GeneDTO geneDto = geneService.getGeneByGeneSymbolWithLimitedFields(gene);
            logger.debug("geneDto=" + geneDto.getMgiAccessionId());
            geneIds.add(geneDto.getMgiAccessionId());
        }
        geneIdsTestEngine(testName, geneIds);

    }
}
