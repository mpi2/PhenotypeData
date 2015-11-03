/*
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
 */
package org.mousephenotype.cda.seleniumtests.tests;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.seleniumtests.exception.TestException;
import org.mousephenotype.cda.seleniumtests.support.*;
import org.mousephenotype.cda.seleniumtests.support.SearchPage.Facet;
import org.mousephenotype.cda.solr.generic.util.JSONRestUtil;
import org.mousephenotype.cda.solr.service.GeneService;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.fail;

/**
 *
 * @author mrelac
 *
 * Selenium test for search coverage ensuring each page works as expected.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("file:${user.home}/configfiles/${profile}/application.properties")
@SpringApplicationConfiguration(classes = TestConfig.class)
public class SearchPageTest {

    private CommonUtils commonUtils = new CommonUtils();
    protected TestUtils testUtils = new TestUtils();
    private WebDriverWait wait;

    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final int TIMEOUT_IN_SECONDS = 120;         // Increased timeout from 4 to 120 secs as some of the graphs take a long time to load.
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;

    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int threadWaitInMilliseconds = THREAD_WAIT_IN_MILLISECONDS;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HashMap<String, String> params = new HashMap();
    private final List<String> paramList = new ArrayList();
    private final List<String> cores = new ArrayList();
    private final List<String> errorList = new ArrayList();
    private final List<String> successList = new ArrayList();
    private final static List<String> sumErrorList = new ArrayList();
    private final static List<String> sumSuccessList = new ArrayList();
    private static String startTime;
    protected Connection komp2Connection;

    private static final Map<SearchFacetTable.TableComponent, By> imageMap = new HashMap();
    static {
        imageMap.put(SearchFacetTable.TableComponent.BY_TABLE, By.xpath("//table[@id='imagesGrid']"));
        imageMap.put(SearchFacetTable.TableComponent.BY_TABLE_TR, By.xpath("//table[@id='imagesGrid']/tbody/tr"));
        imageMap.put(SearchFacetTable.TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='imagesGrid_length']"));
    }

    private static final Map<SearchFacetTable.TableComponent, By> impcImageMap = new HashMap();
    static {
        impcImageMap.put(SearchFacetTable.TableComponent.BY_TABLE, By.xpath("//table[@id='impc_imagesGrid']"));
        impcImageMap.put(SearchFacetTable.TableComponent.BY_TABLE_TR, By.xpath("//table[@id='impc_imagesGrid']/tbody/tr"));
        impcImageMap.put(SearchFacetTable.TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='impc_imagesGrid_length']"));
    }

    @NotNull
    @Value("${baseUrl}")
    protected String baseUrl;

    @Autowired
    WebDriver driver;

    @Autowired
    protected GeneService geneService;

    @NotNull
    @Value("${internalSolrUrl}")
    protected String solrUrl;

    @Autowired
    @Qualifier("komp2DataSource")
    DataSource komp2DataSource;

    @Autowired
    protected PhenotypePipelineDAO phenotypePipelineDAO;

    @Value("${seleniumUrl}")
    protected String seleniumUrl;


    @PostConstruct
    public void initialise() throws TestException {

        try {
            komp2Connection = komp2DataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Before
    public void setup() {
        if (commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeoutInSeconds = commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            threadWaitInMilliseconds = commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));

        testUtils.printTestEnvironment(driver, seleniumUrl);
        wait = new WebDriverWait(driver, timeoutInSeconds);

        driver.navigate().refresh();
        commonUtils.sleep(threadWaitInMilliseconds);

        params.put("gene","fq=*:*");
        params.put("mp", "fq=top_level_mp_term:*");
        params.put("disease", "fq=*:*");
        params.put("ma", "fq=selected_top_level_ma_term:*");
        params.put("impc_images", "fq=*:*");
        params.put("images", "fq=annotationTermId:M* OR expName:* OR symbol:*");

        String commonParam = "qf=auto_suggest&defType=edismax&wt=json&rows=0&q=*:*";
        final String geneParams        = "/gene/select?"        + commonParam + "&" + params.get("gene");
        final String mpParams          = "/mp/select?"          + commonParam + "&" + params.get("mp");
        final String diseaseParams     = "/disease/select?"     + commonParam + "&" + params.get("disease");
        final String maParams          = "/ma/select?"          + commonParam + "&" + params.get("ma");
        final String impc_imagesParams = "/impc_images/select?" + commonParam + "&" + params.get("impc_images");
        final String imagesParams      = "/images/select?"      + commonParam + "&" + params.get("images");

        paramList.add(geneParams);
        paramList.add(mpParams);
        paramList.add(diseaseParams);
        paramList.add(maParams);
        paramList.add(impc_imagesParams);
        paramList.add(imagesParams);

        cores.add("gene");
        cores.add("mp");
        cores.add("disease");
        cores.add("ma");
        cores.add("impc_images");
        cores.add("images");
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


    /**
     * Executes download verification. <code>searchPhrase</code> is used to
     * specify the search characters to send to the server. It may be null or empty.
     * @param testName the test name
     * @param searchString the search characters to be sent to the server. May
     *        be null or empty. If not empty, must be terminated by a trailing
     *        forward slash.
     * @param map the image map to use (impc_image or image)
     * @return status
     */
    private void downloadTestEngine(String testName, String searchString, Map<SearchFacetTable.TableComponent, By> map) throws TestException {
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();
        int totalNonzeroCount = 0;

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process 1 page.");

        int successCount = 0;

        if (searchString == null)
            searchString = "";

        try {
            // Apply searchPhrase. Click on this facet. Click on a random page. Click on each download type: Compare page values with download stream values.
            String target = baseUrl + "/search";
            logger.info("target: " + target);

            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, map);
            if (! searchString.isEmpty()) {
                searchPage.submitSearch(searchString + "\n");
            }

            Facet[] facets = {
                  Facet.ANATOMY
                , Facet.DISEASES
                , Facet.GENES
                , Facet.IMAGES
                , Facet.IMPC_IMAGES
                , Facet.PHENOTYPES
            };

            for (Facet facet : facets) {
                Integer facetCount = searchPage.getFacetCount(facet);
                if (facetCount == null) {
                    String message = "ERROR: the facet count for facet '" + facet + "' was null, which means the search page had no facet count.";
                    logger.error(message);
                    status.addError(message);
                } else if (facetCount == 0) {
                    logger.info("Skipping facet " + facet + " as it has no rows.");
                } else {
                    searchPage.clickFacet(facet);
                    searchPage.setNumEntries(SearchFacetTable.EntriesSelect._25);
                    searchPage.clickPageButton();

                    logger.debug("\nTesting " + facet + " facet. Search string: '" + searchString + "'. URL: " + driver.getCurrentUrl());
                    PageStatus localStatus = searchPage.validateDownload(facet);
                    if ( ! localStatus.hasErrors()) {
                        successCount++;
                    }

                    status.add(localStatus);
                    totalNonzeroCount++;
                }
            }
        } catch (Exception e) {
            String message = "EXCEPTION: SearchPageTest." + testName + "(): Message: " + e.getLocalizedMessage();
            logger.error(message);
            e.printStackTrace();
            status.addError(message);
        }

        testUtils.printEpilogue(testName, start, status, successCount, totalNonzeroCount, paramList.size());
    }

    // Dump the page and db lists.
    private String dumpLists(List<String> pageList, List<String> dbList) {
        StringBuilder sb = new StringBuilder();

        Collections.sort(pageList);
        Collections.sort(dbList);

        sb.append(String.format("%30s %30s", "Page Value (" + pageList.size() + ")", "DB Value(" + dbList.size() + ")\n"));
        sb.append(String.format("%30s %30s", "------------------------------", "------------------------------\n"));
        for (int i = 0; i < Math.max(pageList.size(), dbList.size()); i++) {
            String pageValue = (i < pageList.size() ? pageList.get(i) : "");
            String dbValue = (i < dbList.size() ? dbList.get(i) : "");
            sb.append(String.format("%30s %30s\n", pageValue, dbValue));
        }

        return sb.toString();
    }

    /**
     * Invokes the facet count engine with no search term.
     * @param target the page target URL
     * @param map image map
     * @return page status
     */
    private PageStatus facetCountEngine(String target, Map<SearchFacetTable.TableComponent, By> map) throws TestException {
        return facetCountEngine(target, null, map);
    }

    /**
     * Invokes the facet count engine with the specified, [already escaped if necessary] search term.
     * @param target the page target URL
     * @param searchTermGroup the desired search term group
     * @map image map
     * @return page status
     */
    private PageStatus facetCountEngine(String target, SearchTermGroup searchTermGroup, Map<SearchFacetTable.TableComponent, By> map) throws TestException {
        PageStatus status = new PageStatus();
        String message;

        // Get the solarUrlCounts.
        Map solrCoreCountMap = getSolrCoreCounts(searchTermGroup);
        if (solrCoreCountMap == null) {
            message = "FAIL: Unable to get facet count from Solr.";
            status.addError(message);
            logger.error(message);
            return status;
        }

        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, map);

        // Verify that the core counts returned by solr match the facet counts on the page and the page.
        String[] facets = { "gene", "mp", "disease", "ma", "impc_images", "images" };
        for (String facet : facets) {
            Integer facetCountFromPage = searchPage.getFacetCount(facet);
            int facetCountFromSolr = (int)solrCoreCountMap.get(facet);

            if (facetCountFromPage == null) {
                message = "ERROR: the page's facet count is null. URL: " + target;
                logger.error(message);
                status.addError(message);
                break;                      // The page is broken for all facets. No need to dump out 6 messages.
            } else {
                // 26-Mar-2015 (mrelac) Skip the gene core count compare. The rules are fuzzy.
                if ( ! facet.equals("gene") && (facetCountFromPage != facetCountFromSolr)) {
                    message = "FAIL: facet count from facet '" + facet + "': " + facetCountFromSolr + ". facetCountFromPage: " + facetCountFromPage + ". URL: " + target;
                    status.addError(message);
                    logger.error(message);
                }
            }
        }

        return status;
    }

    /**
     * Queries each of the six search solr cores for the number of occurrences
     * of <code>searchPhrase</code> (which may be null), returning a
     * <code>Map</code> keyed by core name containing the occurrence count for
     * each core.
     *
     * @param searchTermGroup The search phrase to use when querying the cores. If
     * null, the count is unfiltered.
     * @return the <code>searchPhrase</code> occurrence count
     */
    private Map<String, Integer> getSolrCoreCounts(SearchTermGroup searchTermGroup) {
        Map<String, Integer> solrCoreCountMap = new HashMap();

        for (int i = 0; i < paramList.size(); i++) {
            String solrQueryString = paramList.get(i);
            try {
                if (searchTermGroup != null) {
                    solrQueryString = solrQueryString.replace("&q=*:*", "&q=" + searchTermGroup.solrTarget);
                }

                String fqSolrQueryString = solrUrl + solrQueryString;

                JSONObject geneResults = JSONRestUtil.getResults(fqSolrQueryString);
                int facetCountFromSolr = geneResults.getJSONObject("response").getInt("numFound");
                String facet = cores.get(i);
                solrCoreCountMap.put(facet, facetCountFromSolr);
            } catch (TimeoutException te) {
                logger.error("ERROR: SearchPageTest.getSolrCoreCounts() timeout!");
                return null;
            }
            catch(Exception e){
                logger.error("ERROR: SearchPageTest.getSolrCoreCounts(): " + e.getLocalizedMessage());
                return null;
            }
        }

        return solrCoreCountMap;
    }

    private void specialStrQueryTest(String testName, String qry) throws TestException {
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process 1 page.");

        int successCount = 0;
        String target = baseUrl + "/search?q=" + qry;
        driver.get(target);

        new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.id("geneGrid_info")));
        String foundMsg = driver.findElement(By.cssSelector("span#resultCount a")).getText();
        if ( foundMsg.isEmpty() ) {
            status.addError("ERROR: query string '" + qry + "' was not found. URL: " + target);
        }
        else {
            successCount++;
        }

        testUtils.printEpilogue(testName, start, status, successCount, 1, 1);
    }


    // TESTS


    @Test
//@Ignore
    public void testAutosuggestForSpecificKnownGenes() throws TestException {
        String testName = "testAutosuggestForSpecificKnownGenes";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();

        System.out.println(dateFormat.format(start) + ": " + testName + " started.");

        String[] geneSymbols = {
              "Klk4"
            , "Del(7Gabrb3-Ube3a)1Yhj"
        };

        System.out.println("TESTING autosuggest for specific gene symbols. NOTE: Results don't seem to be ordered, so it's possible the gene is beyond the first 10 shown.");
        String message;

        for (String geneSymbol : geneSymbols) {
            String target = baseUrl + "/search";

            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, imageMap);
            System.out.println("Testing symbol " + geneSymbol + ":\t. URL: " + driver.getCurrentUrl());

            List<SearchPage.AutosuggestRow> autoSuggestions = searchPage.getAutosuggest(geneSymbol);

            boolean found = false;
            for (SearchPage.AutosuggestRow row : autoSuggestions) {
                if (row.value.equalsIgnoreCase(geneSymbol)) {
                    found = true;
                    break;
                }
            }

            int successCount = 0;
            if (found) {
                successCount++;
            } else {
                message = "[FAILED]: Expected to find gene id '" + geneSymbol + "' in the autosuggest list but it was not found.";
                for (SearchPage.AutosuggestRow row : autoSuggestions) {
                    message += "\n" + row.toString();
                }
                status.addError(message);
            }
        }

        testUtils.printEpilogue(testName, start, status, successList.size(), geneSymbols.length, geneSymbols.length);
    }

    @Test
//@Ignore
    // test that there is a dropdown when at least 3 letters with match are entered into the input box
    public void testAutosuggestMinCharacters() throws TestException {
        String testName = "testAutosuggestMinCharacters";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process 1 page.");

        String queryStr = baseUrl + "/search";

        driver.get(queryStr);
        String testKw = "mas";
        driver.findElement(By.cssSelector("input#s")).sendKeys(testKw);
        commonUtils.sleep(2000);            // wait until the dropdown list pops up

        int successCount = 0;
         int numTerms = driver.findElements(By.cssSelector("ul.ui-autocomplete li")).size();
         if ( numTerms > 0) {
            successCount++;
         }
         else {
            status.addError("Entered " + testKw + " into search box. Expected matches but found none.");
         }

        testUtils.printEpilogue(testName, start, status, successCount, 1, 1);
    }

    @Test
//@Ignore
    public void testBoneDownload() throws TestException {
        String testName = "testBoneDownload";
        String searchString = "bone";

        downloadTestEngine(testName, searchString, imageMap);
    }

    @Test
//@Ignore
    public void testBrachydactyly() throws TestException {
        String testName = "testBrachydactyly";
        String searchString = "brachydactyly";

        downloadTestEngine(testName, searchString, imageMap);
    }

    @Test
//@Ignore
    public void testDefaultDownload() throws TestException {
        String testName = "testDefaultDownload";
        String searchString = null;

        downloadTestEngine(testName, searchString, imageMap);
    }

    @Test
//@Ignore
    public void testFacetCountsNoSearchTerm() throws TestException {
        String testName = "testFacetCountsNoSearchTerm";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();
        int successCount = 0;

        System.out.println(dateFormat.format(start) + ": " + testName + " started.");

        Map<String, Integer> solrCoreCountMap = getSolrCoreCounts(null);

        // Compare solr core count to page result count.
        // 26-Mar-2015 (mrelac) Skip the gene core count compare. The rules are fuzzy.
        String[] localCores = { "mp", "disease", "ma", "impc_images", "images" };
        for (String core : localCores) {
            String target = baseUrl + "/search#" + params.get(core) + "&facet=" + core;
            PageStatus localStatus = facetCountEngine(target, imageMap);
            if ( ! localStatus.hasErrors()) {
                SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, imageMap);
                int facetCountFromSolr = solrCoreCountMap.get(core);
                int resultCount = searchPage.getResultCount();

                if (facetCountFromSolr != resultCount) {
                    status.addError("Search term facet count MISMATCH: facet count from page: " + resultCount + ". facet count from solr core (" + core + ") = " + facetCountFromSolr + ".");
                } else {
                    successCount++;
                }
            }
        }

        testUtils.printEpilogue(testName, start, status, successCount, localCores.length, localCores.length);
    }

    private class SearchTermGroup {
        private final String pageTarget;
        private final String solrTarget;

        public SearchTermGroup(String pageTarget, String solrTarget) {
            this.pageTarget = pageTarget;
            this.solrTarget = solrTarget;
        }
    }

    // Here's a good site to use for decoding: http://meyerweb.com/eric/tools/dencoder/
    SearchTermGroup[] staticSearchTermGroups = {
                           // Page target   Solr targetr
          new SearchTermGroup("leprot",     "leprot")           // leprot
        , new SearchTermGroup("!",          "!")                // !    %21
        , new SearchTermGroup("@",          "@")                // @    %40
        , new SearchTermGroup("€",          "\\%E2%82%AC")      // €    %E2%82%AC
        , new SearchTermGroup("£",          "\\%C2%A3")         // £    %C2%A3
        , new SearchTermGroup("\\%23",      "\\%23")            // #    %23
        , new SearchTermGroup("$",          "$")                // $    %24
        , new SearchTermGroup("\\%25",      "\\%25")            // %    %25
        , new SearchTermGroup("^",          "^")                // ^    %5E
        , new SearchTermGroup("\\%26",      "\\%26")            // &    %26
        , new SearchTermGroup("\\*",        "\\%2A")            // *    %2A
        , new SearchTermGroup("\\%28",      "\\%28")            // (    %28
        , new SearchTermGroup(")",          ")")                // )    %29
        , new SearchTermGroup("-",          "-")                // -    %2D (hyphen)
        , new SearchTermGroup("_",          "_")                // _    %5F (underscore)
        , new SearchTermGroup("\\=",        "\\=")              // =    %3D
        , new SearchTermGroup("\\%2B",      "\\%2B")            // +    %2B
        , new SearchTermGroup("\\[",        "\\[")              // [    %5B
        , new SearchTermGroup("\\]",        "\\]")              // [    %5D
        , new SearchTermGroup("{",          "\\%7B")            // {    %7B
        , new SearchTermGroup("}",          "\\%7D")            // }    %7D
        , new SearchTermGroup("\\:",        "\\:")              // :    %3A
        , new SearchTermGroup(";",          ";")                // ;    %3B
        , new SearchTermGroup("'",          "'")                // '    %27 (single quote)
        , new SearchTermGroup("\\\"",       "\\\"")             // "    %22 (double quote)
        , new SearchTermGroup("|",          "|")                // |    %7C
        , new SearchTermGroup(",",          ",")                // ,    %2C (comma)
        , new SearchTermGroup(".",          ".")                // .    %2E (period)
        , new SearchTermGroup("<",          "<")                // <    %3C
        , new SearchTermGroup(">",          ">")                // >    %3E
        , new SearchTermGroup("\\%2F",      "\\%2F")            // /    %2F
        , new SearchTermGroup("\\?",        "\\?")              // ?    %3F (question mark)
        , new SearchTermGroup("`",          "`")                // `    %60 (backtick)
        , new SearchTermGroup("\\~",        "\\~")              // ~    %7E
        , new SearchTermGroup("é",          "\\%C3%A9")         // é    %C3%A9
        , new SearchTermGroup("å",          "\\%C3%A5")         // å    %C3%A5
        , new SearchTermGroup("ç",          "\\%C3%A7")         // ç    %C3%A7
        , new SearchTermGroup("ß",          "\\%C3%9F")         // ß    %C3%9F
        , new SearchTermGroup("č",          "\\%C4%8D")         // č    %C4%8D
        , new SearchTermGroup("ü",          "\\%C3%BC")         // ü    %C3%BC
        , new SearchTermGroup("ö",          "\\%C3%B6")         // ö    %C3%B6
    };

    @Test
//@Ignore
    public void testFacetCountsSpecialCharacters() throws TestException {
        String testName = "testFacetCountsSpecialCharacters";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();
        int successCount = 0;

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process 1 page.");

        // Create an array of SearchTermGroup from searchTermGroups that expands the original list by 4,
        // prepending '*', appending '*', and prepending AND appending '*' to the original searchTermGroups values.
        // Example:  "leprot", "leprot" becomes:
        //      "leprot",   "leprot"
        //      "*leprot",  "*leprot"
        //      "leprot*",  "leprot*"
        //      "*leprot*", "*leprot*"
        List<SearchTermGroup> searchTermGroupListWildcard = new ArrayList();
        for (SearchTermGroup staticSearchTermGroup : staticSearchTermGroups) {
            searchTermGroupListWildcard.add(new SearchTermGroup(staticSearchTermGroup.pageTarget, staticSearchTermGroup.solrTarget));
            searchTermGroupListWildcard.add(new SearchTermGroup("*" + staticSearchTermGroup.pageTarget, "*" + staticSearchTermGroup.solrTarget));
            searchTermGroupListWildcard.add(new SearchTermGroup(staticSearchTermGroup.pageTarget + "*", staticSearchTermGroup.solrTarget + "*"));
            searchTermGroupListWildcard.add(new SearchTermGroup("*" + staticSearchTermGroup.pageTarget + "*", "*" + staticSearchTermGroup.solrTarget + "*"));
        }
        SearchTermGroup[] searchTermGroupWildcard = searchTermGroupListWildcard.toArray(new SearchTermGroup[0]);

        for (SearchTermGroup searchTermGroup : searchTermGroupWildcard) {
            // logging/debugging statements:
//            Map solrCoreCountMap = getSolrCoreCounts(searchTermGroup);
//            Set<Map.Entry<String, Integer>> entrySet = solrCoreCountMap.entrySet();
//            for (Map.Entry<String, Integer> entry : entrySet) {
//                log.info("Core: " + entry.getKey() + ". Count: " + entry.getValue());
//            }

            // Build the solarUrlCounts.
            String target = baseUrl + "/search?q=" + searchTermGroup.pageTarget;

            PageStatus localStatus = facetCountEngine(target, searchTermGroup, imageMap);
            if ( ! localStatus.hasErrors()) {
                successCount++;
            }
            status.add(localStatus);
        }

        testUtils.printEpilogue(testName, start, status, successCount, searchTermGroupWildcard.length, searchTermGroupWildcard.length);
    }

    // Test that when Wnt1 is selected, it is at the top of the autosuggest list.
    @Test
//@Ignore
    public void testHox() throws TestException {
        String testName = "testHox";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process 1 page.");

        String searchString = "Hox";
        String target = baseUrl + "/search";

        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, imageMap);
        System.out.println("\nTesting Gene facet. Search string: '" + searchString + "'. URL: " + driver.getCurrentUrl());
        List<SearchPage.AutosuggestRow> rows = searchPage.getAutosuggest(searchString);
        String expectedStartsWith = "Hox";

        if (rows.size() != 10) {
            status.addError("Expected 10 autosuggest rows. Found " + rows.size() + " rows.");
        } else {
            for (int i = 0; i < rows.size(); i++) {
                SearchPage.AutosuggestRow row = rows.get(i);
                if ( ! row.value.startsWith(expectedStartsWith)) {
                    status.addError("Row[" + i + "]: Expected autosuggest row to begin with 'Hox'. Row value = " + row.value);
                    break;
                }
            }
        }

        int successCount = (status.hasErrors() ? 0 : 1);
        testUtils.printEpilogue(testName, start, status, successCount, 1, 1);
    }

    // Test that when Wnt1 is selected, it is at the top of the autosuggest list.
    @Test
//@Ignore
    public void testHoxStar() throws TestException {
        String testName = "testHoxStar";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process 1 page.");

        String searchString = "Hox*";
        String target = baseUrl + "/search";

        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, imageMap);
        System.out.println("\nTesting Gene facet. Search string: '" + searchString + "'. URL: " + driver.getCurrentUrl());
        List<SearchPage.AutosuggestRow> rows = searchPage.getAutosuggest(searchString);
        String expectedStartsWith = "Hox";

        if (rows.size() != 10) {
            status.addError("Expected 10 autosuggest rows. Found " + rows.size() + " rows.");
        } else {
            for (int i = 0; i < rows.size(); i++) {
                SearchPage.AutosuggestRow row = rows.get(i);
                if (!row.value.startsWith(expectedStartsWith)) {
                    status.addError("Row[" + i + "]: Expected autosuggest row to begin with 'Hox'. Row value = " + row.value);
                    break;
                }
            }
        }

        int successCount = (status.hasErrors() ? 0 : 1);
        testUtils.printEpilogue(testName, start, status, successCount, 1, 1);
    }

//    // This test doesn't use the download test engine as it requires an extra
//    // click to switch to the Image facet's 'Image' view.
    @Test
//@Ignore
    public void testImageFacetImageView() throws TestException {
        String testName = "testImageFacetImageView";
        String searchString = "";
        Date start = new Date();
        PageStatus status = new PageStatus();
        Facet facet;

        System.out.println("\n\n----- " + testName + " -----");

        try {
            String target = baseUrl + "/search";
            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, imageMap);
            facet = Facet.IMAGES;
            searchPage.clickFacet(facet);
            searchPage.getImageTable().setCurrentView(SearchFacetTable.ImagesView.IMAGE_VIEW);
            searchPage.clickPageButton();

            System.out.println("Testing " + facet + " facet. Search string: '" + searchString + "'. URL: " + driver.getCurrentUrl());
            status.add(searchPage.validateDownload(facet));

        } catch (TestException e) {
            String message = "EXCEPTION: SearchPageTest." + testName + "(): Message: " + e.getLocalizedMessage();
            status.addError(message);
        } finally {
            if ( ! status.hasErrors()) {
                successList.add(testName + ": SUCCESS.");
            }

            testUtils.printEpilogue(testName, start, status, successList.size(), 1, 1);
        }
    }

    // This test was spawned from testImageFacetImageView() when it came across
    // a 500 response from the server when the last page was selected.
    @Test
//@Ignore
    public void testImageFacetImageViewLastPage() throws TestException {
        String testName = "testImageFacetImageViewLastPage";
        String searchString = "";
        Date start = new Date();
        PageStatus status = new PageStatus();

        System.out.println("\n\n----- " + testName + " -----");

        try {
            String target = baseUrl + "/search";
            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, imageMap);
            Facet facet = Facet.IMAGES;
            searchPage.clickFacet(facet);
            searchPage.getImageTable().setCurrentView(SearchFacetTable.ImagesView.IMAGE_VIEW);
            searchPage.clickPageButton(SearchPage.PageDirective.LAST);
            System.out.println("Testing " + facet + " facet. Search string: '" + searchString + "'. URL: " + driver.getCurrentUrl());
            status.add(searchPage.validateDownload(facet));
        } catch (TestException e) {
            String message = "EXCEPTION: SearchPageTest." + testName + "(): Message: " + e.getLocalizedMessage();
            System.out.println(message);
            e.printStackTrace();
            status.addError(message);
        } finally {
            if (status.hasErrors()) {
                errorList.add(status.toStringErrorMessages());
            } else {
                successList.add(testName + ": SUCCESS.");
            }

            testUtils.printEpilogue(testName, start, status, successList.size(), 1, 1);
        }
    }

    // This test doesn't use the download test engine as it requires an extra
    // click to switch to the Image facet's 'Image' view. It also tests only the IMPC_IMAGES facet rather than all facets.
    @Test
//@Ignore
    public void testImpcImageFacetImageView() throws TestException {
        String testName = "testImpcImageFacetImageView";
        String searchString = "";
        Date start = new Date();
        PageStatus status = new PageStatus();
        Facet facet;

        System.out.println("\n\n----- " + testName + " -----");

        try {
            String target = baseUrl + "/search";
            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, impcImageMap);

            facet = Facet.IMPC_IMAGES;
            searchPage.clickFacet(facet);
            searchPage.getImpcImageTable().setCurrentView(SearchFacetTable.ImagesView.IMAGE_VIEW);

            System.out.println("Testing " + facet + " facet. Search string: '" + searchString + "'. URL: " + driver.getCurrentUrl());

            status.add(searchPage.validateDownload(facet));

        } catch (TestException e) {
            String message = "EXCEPTION: SearchPageTest." + testName + "(): Message: " + e.getLocalizedMessage();
            System.out.println(message);
            e.printStackTrace();
            status.addError(message);
        } finally {
            if (status.hasErrors()) {
                errorList.add(status.toStringErrorMessages());
            } else {
                successList.add(testName + ": SUCCESS.");
            }

            testUtils.printEpilogue(testName, start, status, successList.size(), 1, 1);
        }
    }

    @Test
//@Ignore
    public void testLeadingWildcard() throws TestException {
        specialStrQueryTest("testLeadingWildcard", "*rik");
    }

    @Test
//@Ignore
    public void testLegDownload() throws TestException {
        String testName = "testLegDownload";
        String searchString = "leg";

        downloadTestEngine(testName, searchString, imageMap);
    }

    /**
     * Test ma facet names match Terry's higher_level_annotation table picks.
     *
     * Expected result: ma facet names match higher_level_annotation.TERM_NAME values.
     *
     * @throws TestException
     */
    @Test
//@Ignore
    public void testMaTermNamesMatchFacetNames() throws TestException {
        String testName = "testMaTermNamesMatchFacetNames";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        int expectedCount;
        int successCount = 0;
        PageStatus status = new PageStatus();

        System.out.println(dateFormat.format(start) + ": " + testName + " started.");

        String target = baseUrl + "/search";

        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, impcImageMap);
        String[] pageMaFacetTermNames = searchPage.getFacetNames(Facet.ANATOMY);
        expectedCount = pageMaFacetTermNames.length;
        for (int i = 0; i < pageMaFacetTermNames.length; i++) {
            // Remove the newline and facet count.
            int nl = pageMaFacetTermNames[i].lastIndexOf("\n");
            pageMaFacetTermNames[i] = pageMaFacetTermNames[i].substring(0, nl);
        }

        ArrayList<String> dbMaFacetTermNames = new ArrayList();

        String query =
              "SELECT hla.TERM_ID, ti.name\n"
            + "FROM komp2.higher_level_annotation hla\n"
            + "JOIN ontodb_komp2.ma_term_infos ti ON ti.term_id = hla.TERM_ID\n"
            + "ORDER BY ti.name\n";
        PreparedStatement ps = null;
        try {
            ps = komp2Connection.prepareStatement(query);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                dbMaFacetTermNames.add(resultSet.getString("name"));
            }
            ps.close();
        } catch (SQLException sqle) {
            if ( ps != null) {
                try { ps.close(); } catch (SQLException sqle2) { }
            }
            throw new TestException(testName + ": SQL Exception", sqle);
        }

        Set<String> pageSet = new HashSet(Arrays.asList(pageMaFacetTermNames));
        Set<String> dbSet = new HashSet(dbMaFacetTermNames);

        if (pageSet.size() != dbSet.size()) {
            status.addError("facet name count mismatch: "
                    + dumpLists(Arrays.asList(pageMaFacetTermNames), dbMaFacetTermNames));
        }

        for (int i = 0; i < pageSet.size(); i++) {
            if (pageMaFacetTermNames[i].equals(dbMaFacetTermNames.get(i))) {
                successCount++;
            } else {
                status.addError("facet name mismatch: "
                  + dumpLists(Arrays.asList(pageMaFacetTermNames), dbMaFacetTermNames));
            }
        }

        testUtils.printEpilogue(testName, start, status, successCount, expectedCount, expectedCount);
    }

    /**
     * Test for Jira bug MPII-806: from the search page, searching for the characters
     * "fasting glu" should autosuggest 'fasting glucose'. Click on 'fasting glucose'
     * and verify that the correct phenotype page appears.
     * @throws TestException
     */
    @Test
//@Ignore
    public void testMPII_806() throws TestException {
        String testName = "testMPII_806";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();

        System.out.println(dateFormat.format(start) + ": " + testName + " started.");

         String queryStr = baseUrl + "/search";
         driver.get(queryStr);
         String characters = "fasting glu";
         driver.findElement(By.cssSelector("input#s")).sendKeys(characters);

         // Wait for dropdown list to appear with 'blood glucose'.
        String xpathSelector = "//ul[@id='ui-id-1']/li[@class='ui-menu-item']/a";
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathSelector)));
        if (( ! element.getText().contains("fasting")) && ( ! element.getText().contains("glucose"))){
            status.addError("ERROR: Expected the terms 'fasting' and 'glucose' but found '" + element.getText() + "'");
        } else {
            element.click();
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='resultMsg']")));
            if (element.getText().contains("Found") == false) {
                status.addError("ERROR: Expected 'Found xxx genes' message. Text = '" + element.getText() + "'");
            }
        }

        int successCount = (status.hasErrors() ? 0 : 1);
        testUtils.printEpilogue(testName, start, status, successCount, 1, 1);
    }

    // Tests search page with more than one Production Status [blue] order button.
    // We'll use MGI:1353431 (gene Pcks1n), which has 2 Production Status buttons.
    @Test
//@Ignore
    public void testOrderButtons() throws TestException {
        String testName = "testOrderButtons";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();

        System.out.println(dateFormat.format(start) + ": " + testName + " started.");

        int successCount = 0;
        int buttonElementsSize = 0;
        String message;
        String target = "";

        System.out.println(dateFormat.format(start) + ": " + testName + " started.");

        try {
            target = baseUrl + "/search?q=MGI%3A1353431#fq=*:*&facet=gene";
            logger.info("target: " + target);
            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, impcImageMap);

            // Use the first gene div element in the search results.
            List<WebElement> geneElements = driver.findElements(By.xpath("//*[@id='geneGrid']/tbody/tr[1]"));
            if (geneElements.isEmpty()) {
                fail("Can't find first 'geneGrid' div element in gene search results list.");
            }

            buttonElementsSize = searchPage.getProductionStatusOrderButtons(geneElements.get(0)).size();

            if (buttonElementsSize != 2) {
                status.addError("This test expects two order buttons. Number of buttons found: " + buttonElementsSize);
            } else {
                for (int i = 0; i < buttonElementsSize; i++) {
                    String path = "//*[@id='geneGrid']/tbody/tr[1]";
                    geneElements = driver.findElements(By.xpath(path));
                    WebElement geneElement = geneElements.get(0);
                    List<WebElement> buttonElements = searchPage.getProductionStatusOrderButtons(geneElement);
                    WebElement buttonElement = buttonElements.get(i);
                    buttonElement.click();

                    // Verify that we're in the order section.
                    boolean expectedUrlEnding = driver.getCurrentUrl().endsWith("#order2");
                    if ( ! expectedUrlEnding) {
                        status.addError("Expected url to end in '#order2'. URL: " + driver.getCurrentUrl());
                    } else {
                        successCount++;
                    }

                    driver.navigate().back();
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(path)));
                }
            }

        } catch (TestException e) {
            message = "ERROR: Failed to load search page URL: " + target;
            status.addError(message);
        }

        testUtils.printEpilogue(testName, start, status, successCount, buttonElementsSize, buttonElementsSize);
    }

    @Test
//@Ignore
    public void testPagination() throws TestException {
        String testName = "testPagination";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();
        int successCount = 0;

        System.out.println(dateFormat.format(start) + ": " + testName + " started.");

        String target;
        String message;
        final String showing_1 = "Showing 1 to ";
        final String showing_11 = "Showing 11 to ";
        String expectedShowingPhrase = "";
        String actualShowing = "";

        for (String core : cores ){
            target = baseUrl + "/search#" + params.get(core) + "&facet=" + core;
            logger.info("Testing URL: " + target);
            try {
                SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, imageMap);
                searchPage.clickFacetById(core);

                // Upon entry, the 'showing' string should start with 'Showing 1 to 10 of".
                expectedShowingPhrase = showing_1;
                actualShowing = searchPage.getShowing().toString();
                if ( ! actualShowing.contains(expectedShowingPhrase)) {
                    message = "ERROR: Expected '" + expectedShowingPhrase + "' but found '" + actualShowing + "'.";
                    status.addError(message);
                }

                if (searchPage.getNumPageButtons() > 3) {                       // Previous, page, Next
                    // Wait for facet page to load, then click the page '2' link. The 'showing' string should start with 'Showing 11 to 20 of".
                    searchPage.clickPageButton(SearchPage.PageDirective.SECOND_NUMBERED);
                    expectedShowingPhrase = showing_11;
                    actualShowing = searchPage.getShowing().toString();
                    if ( ! actualShowing.contains(expectedShowingPhrase)) {
                        message = "ERROR: Expected '" + expectedShowingPhrase + "' but found '" + actualShowing + "'.";
                        status.addError(message);
                    }
                }

                if (errorList.isEmpty())
                    successCount++;
            } catch (TestException e) {
                message = "EXCEPTION: Expected '" + expectedShowingPhrase + "' but found '" + actualShowing + "'. message: " + e.getLocalizedMessage();
                status.addError(message);
                e.printStackTrace();
            }
        }

        testUtils.printEpilogue(testName, start, status, successCount, cores.size(), cores.size());
    }

    @Test
//@Ignore
    public void testPhrase() throws TestException {
        specialStrQueryTest("testPhrase", "grip strength");
    }

    @Test
//@Ignore
    public void testPhraseInQuotes() throws TestException {
        specialStrQueryTest("testPhraseInQuotes", "\"zinc finger protein\"");
    }

    // Verify that random genes appear in the autosuggest list.
    @Test
//@Ignore
    public void testQueryingRandomGeneSymbols() throws TestException {
        String testName = "testQueryingRandomGeneSymbols";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();
        int nbRows = 20;
        int successCount = 0;

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + nbRows + " gene symbols.");

        Random rn = new Random();
        int startIndex = rn.nextInt(60000 - 0 + 1) + 1;

        String target = baseUrl + "/search#fq=*:*&facet=gene";
        logger.info("URL: " + target);
        String queryString = solrUrl + "/gene/select?q=*:*&start=" + startIndex + "&rows=" + nbRows + "&fl=marker_symbol&wt=json&indent=true";

        JSONObject geneResults;
        try {
            geneResults = JSONRestUtil.getResults(queryString);
        } catch (Exception e) {
            throw new TestException("Error getting geneResults", e);
        }
        JSONArray docs = JSONRestUtil.getDocArray(geneResults);
        String message;

        if (docs != null) {
            int size = docs.size();
            for (int i = 0; i < size; i++) {
                String geneSymbol1 = "";
                try {
                    geneSymbol1 = docs.getJSONObject(i).getString("marker_symbol");

                    logger.info("Testing gene symbol " + geneSymbol1);
                    SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, imageMap);
                    searchPage.submitSearch(geneSymbol1);
                    WebDriverWait localWait = new WebDriverWait(driver, 10);        // Wait up to 10 seconds.
                    localWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[contains(@class, 'ui-autocomplete')]")));

                    List<WebElement> elems = driver.findElements(By.cssSelector("ul#ui-id-1 li.ui-menu-item a span b.sugTerm"));
                    String geneSymbol2 = null;
                    for (WebElement elem : elems) {
                        String autosuggestGene = elem.getText();
                        if (autosuggestGene.equals(geneSymbol1)) {
                            geneSymbol2 = elem.getText();
                            break;
                        }
                    }

                    if (geneSymbol1.equals(geneSymbol2)) {
                        logger.debug("[" + i + "] (OK): '" + geneSymbol1 + "'");
                        successCount++;
                    } else {
                        message = "ERROR[" + i + "]: Expected to find gene id '" + geneSymbol1 + "' in the autosuggest list but it was not found.";
                        logger.error(message);
                        status.addError(message);
                    }
                } catch (Exception e) {
                    logger.error("ERROR testing gene symbol " + geneSymbol1 + ": " + e.getLocalizedMessage());
                }
            }
        }

        testUtils.printEpilogue(testName, start, status, successCount, nbRows, nbRows);
    }

    @Test
//@Ignore
    public void testRandomMgiIds() throws TestException {
        String testName = "testRandomMgiIds";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();
        int nbRows = 20;
        int successCount = 0;

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process " + nbRows + " gene symbols.");

        String newQueryString = "/gene/select?q=mgi_accession_id:*&fq=-marker_symbol:CGI_* AND -marker_symbol:Gm*&fl=mgi_accession_id,marker_symbol&wt=json";
        Random rn = new Random();
        int startIndex = rn.nextInt(40000 - 0 + 1) + 1;
        newQueryString+="&start="+startIndex+"&rows="+nbRows;

        JSONObject geneResults;
        try {
            geneResults = JSONRestUtil.getResults(solrUrl + newQueryString);
        } catch (Exception e) {
            throw new TestException(testName + ": Error getting gene results", e);
        }
        JSONArray docs = JSONRestUtil.getDocArray(geneResults);

        if (docs != null) {
            int size = docs.size();
            int count;
            for (int i = 0; i < size; i++) {

                count = i + 1;
                String mgiId = docs.getJSONObject(i).getString("mgi_accession_id");
                String symbol = docs.getJSONObject(i).getString("marker_symbol");
                logger.info("Testing MGI ID " + String.format("%3d", count) + ": " + String.format("%-10s", mgiId));

                driver.get(baseUrl + "/search?q=" + mgiId);

                new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.geneCol")));
                WebElement geneLink = null;
                try {
                    driver.findElement(By.cssSelector("div.geneCol a").linkText(symbol));
                    successCount++;
                } catch (Exception e) {
                    status.addError("ERROR: Expected to find gene symbol '" + symbol + "' but it was not found.");
                }
            }
        }

        testUtils.printEpilogue(testName, start, status, successCount, nbRows, nbRows);
    }

    @Test
//@Ignore
    public void testTickingFacetFilters() throws TestException {
        String testName = "testTickingFacetFilters";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();
        int successCount = 0;

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process 1 page.");

        String message;
        successList.clear();
        errorList.clear();
        String target = baseUrl + "/search";
        logger.debug("target Page URL: " + target);
        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, imageMap);

        // For each core:
        //   Click the first subfacet.
        //   Check that it is selected.
        //   Check that there is a filter matching the selected facet above the Genes facet.
        //   Click the first subfacet again to unselect it.
        //   Check that it is unselected.
        //   Check that there is no filter matching the just-unselected facet above the Genes facet.
        for (String core :  cores) {
            String subfacetCheckboxCssSelector = "li#" + core + " li.fcat input[type='checkbox']";
            String subfacetTextCssSelector = "li#" + core + " li.fcat span.flabel";
            int iterationErrorCount = 0;
            Facet facet = searchPage.getFacetByCoreName(core);
            searchPage.openFacet(facet);                                        // Open facet if it is not alreay opened.

            WebElement firstSubfacetElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(subfacetCheckboxCssSelector)));
            firstSubfacetElement.click();                                       // Select the first subfacet.

            searchPage.openFacet(facet);                                        // Re-open the facet as, by design, it closed after the click() above.
            PageStatus coreStatus = new PageStatus();
            if (!firstSubfacetElement.isSelected()) {                         // Verify that the subfacet is selected.
                coreStatus.addError("Failed to check input filter for " + facet + " facet.");
            } else {

                // Check that there is a filter matching the selected facet above the Genes facet.
                String facetText = driver.findElement(By.cssSelector(subfacetTextCssSelector)).getText();
                HashMap<Facet, SearchPage.FacetFilter> facetFilterHash = searchPage.getFacetFilter();
                List<String> facetFilterText = facetFilterHash.get(facet).subfacetTexts;
                boolean found = false;
                for (String facetFilter : facetFilterText) {
                    if (facetFilter.contains(facetText)) {
                        found = true;
                        break;
                    }
                }
                if ( ! found) {
                    coreStatus.addError("ERROR: Expeted subfacet '" + facetText + "' in facet " + facet);
                }

                searchPage.openFacet(facet);                                        // Open facet if it is not alreay opened.
                firstSubfacetElement.click();                                       // Deselect the first subfacet.

                searchPage.openFacet(facet);                                        // Re-open the facet as, by design, it closed after the click() above.

                // The page becomes stale after the click() above, so we must re-fetch the WebElement objects.
                firstSubfacetElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(subfacetCheckboxCssSelector)));

                if (firstSubfacetElement.isSelected()) {                            // Verify that the subfacet is no longer selected.
                    coreStatus.addError("Failed to uncheck input filter for " + facet + " facet.");
                }

                // Check that there are no filters.
                if (searchPage.hasFilters()) {
                    coreStatus.addError("ERROR: Expected filters to be cleared, but there were filters in place for facet " + facet);
                }

                if (iterationErrorCount == 0) {
                    logger.info("   " + core + " OK");
                    successList.add(core);
                }

                searchPage.clearFilters();
            }

            if (coreStatus.hasErrors()) {
                status.add(coreStatus);
            } else {
                successCount++;
            }
        }

        testUtils.printEpilogue(testName, start, status, successCount, cores.size(), cores.size());
    }

    @Test
//@Ignore
    public void testTrailingWildcard() throws TestException {
        specialStrQueryTest("testTrailingWildcard", "hox*");
    }

    @Test
//@Ignore
    public void testTwist1() throws TestException {
        String testName = "testTwist1";
        String searchString = "twist1";

        downloadTestEngine(testName, searchString, imageMap);
    }

    // Test that when Wnt1 is selected, it is at the top of the autosuggest list.
    @Test
//@Ignore
    public void testWnt1IsAtTop() throws TestException {
        String testName = "testWnt1IsAtTop";
        Date start = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        PageStatus status = new PageStatus();

        System.out.println(dateFormat.format(start) + ": " + testName + " started. Expecting to process 1 page.");

        String searchString = "Wnt1";
        String target = baseUrl + "/search";

        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, baseUrl, imageMap);
        System.out.println("\nTesting Gene facet. Search string: '" + searchString + "'. URL: " + driver.getCurrentUrl());
        List<SearchPage.AutosuggestRow> rows = searchPage.getAutosuggest(searchString);
        String expectedResult = "wnt1";

        if (rows.isEmpty()) {
            status.addError("Expected at least 1 row. No rows found.");
        } else {
            SearchPage.AutosuggestRow row = rows.get(0);
            if ( ! row.value.equalsIgnoreCase(expectedResult)) {
                status.addError("Expected Wnt1 to be at top of autosuggest row. Top value = " + row.value);
            }
        }

        int successCount = (status.hasErrors() ? 0 : 1);
        testUtils.printEpilogue(testName, start, status, successCount, 1, 1);
    }
}