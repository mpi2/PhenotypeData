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
 * This file is intended to contain web tests for graphs - e.g. if there is an
 * IMPC link to a graph (either from a gene page or a phenotype page), there
 * should indeed be a graph present when the link is clicked.
 */

package org.mousephenotype.cda.seleniumtests.tests;

import edu.emory.mathcs.backport.java.util.Arrays;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.enumerations.ObservationType;
import org.mousephenotype.cda.seleniumtests.exception.TestException;
import org.mousephenotype.cda.seleniumtests.support.*;
import org.mousephenotype.cda.solr.service.*;
import org.mousephenotype.cda.solr.web.dto.GraphTestDTO;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.web.ChartType;
import org.mousephenotype.cda.web.TimeSeriesParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertTrue;

/**
 *
 * @author mrelac
 *
 * Selenium test for graph page query coverage ensuring each page works as expected.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/applicationTest.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class GraphPageTest {

    private CommonUtils commonUtils = new CommonUtils();
    private WebDriver driver;
    protected TestUtils testUtils = new TestUtils();
    private WebDriverWait wait;

    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final int TIMEOUT_IN_SECONDS = 120;         // Increased timeout from 4 to 120 secs as some of the graphs take a long time to load.
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;

    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int thread_wait_in_ms = THREAD_WAIT_IN_MILLISECONDS;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Environment env;

    @Autowired
    protected GeneService geneService;

    @Autowired
    @Qualifier("postqcService")
    protected PostQcService genotypePhenotypeService;

    @Autowired
    protected MpService mpService;

    @Autowired
    ObservationService observationService;

    @Autowired
    private PhenotypePipelineDAO phenotypePipelineDAO;

    @Autowired
    PostQcService postQcService;

    @Autowired
    PreQcService preQcService;

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
            thread_wait_in_ms = commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));

        testUtils.printTestEnvironment(driver, wrapper.getSeleniumUrl());
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


    // PRIVATE METHODS


    private void graphEngine(String testName, List<String> graphUrls) throws TestException {
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus statuses = new PageStatus();
        int successCount = 0;

        int targetCount = graphUrls.size();
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " graph pages.");

        int i = 1;
        for (String graphUrl : graphUrls) {
            PageStatus status = new PageStatus();

            // Skip gene pages without graphs.
            if (graphUrls.isEmpty())
                continue;

            try {
                GraphPage graphPage = new GraphPage(driver, wait, phenotypePipelineDAO, graphUrl, baseUrl);
                status.add(graphPage.validate());
                if ( ! status.hasErrors()) {
                    successCount++;
                }
                statuses.add(status);

            } catch (TestException e) {
                statuses.addError(e.getLocalizedMessage());
            }

            if (i++ >= targetCount) {
                break;
            }
        }

        testUtils.printEpilogue(testName, start, statuses, successCount, targetCount, graphUrls.size());
        System.out.println();
    }

    private void testEngine(String testName, List<GraphTestDTO> geneGraphs, ChartType chartType) throws TestException {
        String target;
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus statuses = new PageStatus();
        int successCount = 0;

        int targetCount = testUtils.getTargetCount(env, testName, geneGraphs, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " graph pages.");

        int i = 1;
        for (GraphTestDTO geneGraph : geneGraphs) {
            target = baseUrl + "/genes/" + geneGraph.getMgiAccessionId();

            GenePage genePage = new GenePage(driver, wait, target, geneGraph.getMgiAccessionId(), phenotypePipelineDAO, baseUrl);
            genePage.selectGenesLength(100);
            List<String> graphUrls = genePage.getGraphUrls(geneGraph.getProcedureName(), geneGraph.getParameterName());

            // Skip gene pages without graphs.
            if (graphUrls.isEmpty())
                continue;
            try {
                GraphPage graphPage = new GraphPage(driver, wait, phenotypePipelineDAO, graphUrls.get(0), baseUrl);
                PageStatus status = graphPage.validate();
                if ( ! status.hasErrors()) {
                    successCount++;
                }
                statuses.add(status);

            } catch (Exception e) {
                statuses.addError(e.getLocalizedMessage());
            }

            if (i++ >= targetCount) {
                break;
            }
        }

        testUtils.printEpilogue(testName, start, statuses, successCount, targetCount, geneGraphs.size());
        System.out.println();
    }


    // TESTS


    // Tests known graph URLs that have historically been broken or are interesting cases, such as 2 graphs per page.
    //
    // NOTE: This test is configured to run on either BETA or DEV. If the profile is neither, then the test is skipped.
    @Test
//@Ignore
    public void testKnownGraphs() throws TestException {
        String profile = env.getProperty("profile");
        if ((profile != null) && (profile.equals("beta") || (profile.equals("dev")))) {
            String port = (profile.equals("dev") ? "8080" : "18080");
            String testName = "testKnownGraphs";
            List<String> graphUrls = Arrays.asList(new String[]{
                      "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:3588194&allele_accession=NULL-3a8c98b85&zygosity=homozygote&parameter_stable_id=IMPC_ABR_010_001&pipeline_stable_id=IMPC_001&phenotyping_center=BCM"               // ABR
                    , "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:2149209&allele_accession=MGI:5548754&zygosity=homozygote&parameter_stable_id=IMPC_ABR_004_001&pipeline_stable_id=UCD_001&phenotyping_center=UC Davis"              // ABR
                    , "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:2146574&allele_accession=MGI:4419159&zygosity=homozygote&parameter_stable_id=IMPC_ABR_008_001&pipeline_stable_id=MGP_001&phenotyping_center=WTSI"                  // ABR
                    , "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:1860086&allele_accession=MGI:4363171&zygosity=homozygote&parameter_stable_id=ESLIM_022_001_001&pipeline_stable_id=ESLIM_001&phenotyping_center=WTSI"               // Time Series
                    , "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:1929878&allele_accession=MGI:5548713&zygosity=homozygote&parameter_stable_id=IMPC_XRY_028_001&pipeline_stable_id=HRWL_001&phenotyping_center=MRC Harwell"          // Unidimensional
                    , "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:1920093&zygosity=homozygote&allele_accession=MGI:5548625&parameter_stable_id=IMPC_CSD_033_001&pipeline_stable_id=HRWL_001&phenotyping_center=MRC%20Harwell"        // Categorical
                    , "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:1100883&allele_accession=MGI:2668337&zygosity=heterozygote&parameter_stable_id=ESLIM_001_001_087&pipeline_stable_id=ESLIM_001&phenotyping_center=MRC%20Harwell"    // Categorical
                    , "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:98216&allele_accession=EUROALL:15&zygosity=homozygote&parameter_stable_id=ESLIM_021_001_005&pipeline_stable_id=ESLIM_001&phenotyping_center=ICS"                   // Unidimensional
                    , "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:1270128&allele_accession_id=MGI:4434551&zygosity=homozygote&parameter_stable_id=ESLIM_015_001_014&pipeline_stable_id=ESLIM_002&phenotyping_center=HMGU"            // Unidimensional
                    , "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:1923455&allele_accession_id=EUROALL:3&zygosity=homozygote&parameter_stable_id=ESLIM_015_001_001&pipeline_stable_id=ESLIM_002&phenotyping_center=ICS"
                    , "http://ves-ebi-d0:" + port + "/mi/impc/" + profile + "/phenotype-archive/charts?accession=MGI:96816&allele_accession_id=MGI:5605843&zygosity=heterozygote&parameter_stable_id=IMPC_CSD_024_001&pipeline_stable_id=UCD_001&phenotyping_center=UC Davis"
            });

            graphEngine(testName, graphUrls);
        } else {
            logger.info("ERROR: testKnownGraphs() skipped. It runs only against profiles 'beta' and 'dev', and profile was " + profile);
        }
    }

    @Test
//@Ignore
    public void testPreQcGraphs() throws TestException {
        String testName = "testPreQcGraphs";
        List<GraphTestDTO> geneGraphs = getGeneGraphs(ChartType.PREQC, 100);
        assertTrue("Expected at least one gene graph.", geneGraphs.size() > 0);
        String target;
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus statuses = new PageStatus();
        int successCount = 0;

        int targetCount = testUtils.getTargetCount(env, testName, geneGraphs, 10);
        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + targetCount + " graphs.");

        for (int i = 0; i < targetCount; i++) {
            GraphTestDTO geneGraph = geneGraphs.get(i);
            target = baseUrl + "/genes/" + geneGraph.getMgiAccessionId();
            GenePage genePage = new GenePage(driver, wait, target, geneGraph.getMgiAccessionId(), phenotypePipelineDAO, baseUrl);
            genePage.selectGenesLength(100);
            GraphValidatorPreqc validator = new GraphValidatorPreqc();
            PageStatus status = validator.validate(driver, genePage, geneGraph);
            if ( ! status.hasErrors())
                successCount++;
            statuses.add(status);
        }

        testUtils.printEpilogue(testName, start, statuses, successCount, targetCount, geneGraphs.size());
        System.out.println();
    }

    @Test
//@Ignore
    public void testCategoricalGraphs() throws TestException {
        String testName = "testCategoricalGraphs";

        List<GraphTestDTO> geneGraphs = getGeneGraphs(ChartType.CATEGORICAL_STACKED_COLUMN, 100);
        assertTrue("Expected at least one gene graph.", geneGraphs.size() > 0);
        testEngine(testName, geneGraphs, ChartType.CATEGORICAL_STACKED_COLUMN);
    }

    @Test
//@Ignore
    public void testUnidimensionalGraphs() throws TestException {
        String testName = "testUnidimensionalGraphs";

        List<GraphTestDTO> geneGraphs = getGeneGraphs(ChartType.UNIDIMENSIONAL_BOX_PLOT, 100);
        assertTrue("Expected at least one gene graph.", geneGraphs.size() > 0);
        testEngine(testName, geneGraphs, ChartType.UNIDIMENSIONAL_BOX_PLOT);
    }

    @Test
//@Ignore
    public void testABRGraphs() throws TestException {
        String testName = "testABRGraphs";

        List<GraphTestDTO> geneGraphs = getGeneGraphs(ChartType.UNIDIMENSIONAL_ABR_PLOT, 100);
        assertTrue("Expected at least one gene graph.", geneGraphs.size() > 0);
        testEngine(testName, geneGraphs, ChartType.UNIDIMENSIONAL_ABR_PLOT);
    }

    @Test
//@Ignore
    public void testPieGraphs() throws TestException {
        String testName = "testPieGraphs";

        List<GraphTestDTO> geneGraphs = getGeneGraphs(ChartType.PIE, 100);
        assertTrue("Expected at least one gene graph.", geneGraphs.size() > 0);
        testEngine(testName, geneGraphs, ChartType.PIE);
    }

    @Test
//@Ignore
    public void testTimeSeriesGraphs() throws TestException {
        String testName = "testTimeSeriesGraphs";

        List<GraphTestDTO> geneGraphs = getGeneGraphs(ChartType.TIME_SERIES_LINE_BODYWEIGHT, 100);
        assertTrue("Expected at least one gene graph.", geneGraphs.size() > 0);
        testEngine(testName, geneGraphs, ChartType.TIME_SERIES_LINE);
    }


    // PRIVATE METHODS


    /**
     * Returns <em>count</em> <code>GraphTestDTO</code> instances matching genes
     * with graph links of type <code>chartType</code>.
     *
     * @param chartType the desired chart type
     * @param count the desired number of instances to be returned. If -1,
     * MAX_INT instances will be returned.
     *
     * @return <em>count</em> <code>GraphTestDTO</code> instances matching genes
     * with graph links of type <code>chartType</code>.
     *
     * @throws TestException
     */
    private List<GraphTestDTO> getGeneGraphs(ChartType chartType, int count) throws TestException {
        List<GraphTestDTO> geneGraphs = new ArrayList();

        if (count == -1)
            count = Integer.MAX_VALUE;

        switch (chartType) {
            case CATEGORICAL_STACKED_COLUMN:
                try {
                    List<String> parameterStableIds = observationService.getParameterStableIdsByObservationType(ObservationType.categorical, count);
                    geneGraphs = postQcService.getGeneAccessionIdsByParameterStableId(parameterStableIds, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new TestException("TestUtils.getGeneGraphs() CATEGORICAL_STACKED_COLUMN EXCEPTION: " + e.getLocalizedMessage());
                }
                break;

            case PIE:
                try {
                    List<String> parameterStableIds = java.util.Arrays.asList(new String[]{"*_VIA_*"});
                    geneGraphs = postQcService.getGeneAccessionIdsByParameterStableId(parameterStableIds, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new TestException("TestUtils.getGeneGraphs() PIE EXCEPTION: " + e.getLocalizedMessage());
                }
                break;

            case UNIDIMENSIONAL_ABR_PLOT:
                try {
                    List<String> parameterStableIds = java.util.Arrays.asList(new String[]{"*_ABR_*"});
                    geneGraphs = postQcService.getGeneAccessionIdsByParameterStableId(parameterStableIds, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new TestException("TestUtils.getGeneGraphs() UNIDIMENSIONAL_ABR_PLOT EXCEPTION: " + e.getLocalizedMessage());
                }
                break;

            case UNIDIMENSIONAL_BOX_PLOT:
            case UNIDIMENSIONAL_SCATTER_PLOT:
                try {
                    List<String> parameterStableIds = observationService.getParameterStableIdsByObservationType(ObservationType.unidimensional, count);
                    geneGraphs = postQcService.getGeneAccessionIdsByParameterStableId(parameterStableIds, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new TestException("TestUtils.getGeneGraphs() UNIDIMENSIONAL_XXX EXCEPTION: " + e.getLocalizedMessage());
                }
                break;

            case PREQC:
                try {
                    geneGraphs = preQcService.getGeneAccessionIds(count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new TestException("TestUtils.getGeneGraphs() PREQC EXCEPTION: " + e.getLocalizedMessage());
                }
                break;

            case TIME_SERIES_LINE:
            case TIME_SERIES_LINE_BODYWEIGHT:
                try {
                    List<String> parameterStableIds = new ArrayList();
                    parameterStableIds.addAll(TimeSeriesParameters.ESLIM_701);
                    parameterStableIds.addAll(TimeSeriesParameters.ESLIM_702);
                    parameterStableIds.addAll(TimeSeriesParameters.IMPC_BWT);
                    geneGraphs = postQcService.getGeneAccessionIdsByParameterStableId(parameterStableIds, count);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new TestException("TestUtils.getGeneGraphs() TIME_SERIES_XXX EXCEPTION: " + e.getLocalizedMessage());
                }
                break;

        }

        return geneGraphs;
    }
}