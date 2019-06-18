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
package org.mousephenotype.cda.selenium;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.selenium.config.TestConfig;
import org.mousephenotype.cda.selenium.exception.TestException;
import org.mousephenotype.cda.selenium.support.Facet;
import org.mousephenotype.cda.selenium.support.SearchFacetTable;
import org.mousephenotype.cda.selenium.support.SearchPage;
import org.mousephenotype.cda.selenium.support.TestUtils;
import org.mousephenotype.cda.solr.generic.util.JSONRestUtil;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.cda.utilities.UrlUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.util.*;

import static org.junit.Assert.fail;

/**
 *
 * @author mrelac
 *
 * Selenium test for search coverage ensuring each page works as expected.
 */


// FIXME FIXME FIXME
@Ignore



@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class SearchPageTest {

    private CommonUtils   commonUtils = new CommonUtils();
    private WebDriver     driver;
    private TestUtils     testUtils   = new TestUtils();
    private UrlUtils      urlUtils    = new UrlUtils();
    private WebDriverWait wait;

    private final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final int TIMEOUT_IN_SECONDS = 120;
    private final int THREAD_WAIT_IN_MILLISECONDS = 20;

    private int timeoutInSeconds = TIMEOUT_IN_SECONDS;
    private int threadWaitInMilliseconds = THREAD_WAIT_IN_MILLISECONDS;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HashMap<String, String> params    = new HashMap();
    private final List<String>            paramList = new ArrayList();
    private final List<String>            cores     = new ArrayList();
    private Connection                    komp2Connection;

    private final Map<SearchFacetTable.TableComponent, By> imageMap = new HashMap();
    private final Map<SearchFacetTable.TableComponent, By> impcImageMap = new HashMap();


    @Value("${paBaseUrl}")
    private String paBaseUrl;

    @Value("${seleniumUrl}")
    private String seleniumUrl;

    @Value("${internal_solr_url}")
    private String solrUrl;


    @NotNull @Autowired
    private DesiredCapabilities desiredCapabilities;

    @NotNull @Autowired
    private DataSource komp2DataSource;

    @NotNull @Autowired
    private PhenotypePipelineDAO phenotypePipelineDAO;



    @PostConstruct
    public void initialise() throws TestException {

//        try {
//            komp2Connection = komp2DataSource.getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//
//        imageMap.put(SearchFacetTable.TableComponent.BY_TABLE, By.xpath("//table[@id='imagesGrid']"));
//        imageMap.put(SearchFacetTable.TableComponent.BY_TABLE_TR, By.xpath("//table[@id='imagesGrid']/tbody/tr"));
//        imageMap.put(SearchFacetTable.TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='imagesGrid_length']"));
//
//        impcImageMap.put(SearchFacetTable.TableComponent.BY_TABLE, By.xpath("//table[@id='impc_imagesGrid']"));
//        impcImageMap.put(SearchFacetTable.TableComponent.BY_TABLE_TR, By.xpath("//table[@id='impc_imagesGrid']/tbody/tr"));
//        impcImageMap.put(SearchFacetTable.TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='impc_imagesGrid_length']"));
    }

    @Before
    public void setup() throws MalformedURLException {
        driver = new RemoteWebDriver(new URL(seleniumUrl), desiredCapabilities);
        if (commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS")) != null)
            timeoutInSeconds = commonUtils.tryParseInt(System.getProperty("TIMEOUT_IN_SECONDS"));
        if (commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS")) != null)
            threadWaitInMilliseconds = commonUtils.tryParseInt(System.getProperty("THREAD_WAIT_IN_MILLISECONDS"));

        wait = new WebDriverWait(driver, timeoutInSeconds);

        params.put("gene","fq=*:*&qf=geneQf");
        params.put("mp", "fq=*:*&qf=mixSynQf");
        params.put("phenodigm", "q=*:*&fq=type:disease_search");
        params.put("anatomy", "fq=*:*&qf=anatomyQf");
        params.put("impc_images", "fq=*:*&qf=imgQf");
        params.put("allele2", "fq=type:Allele&qf=auto_suggest");

        String commonParam = "defType=edismax&wt=json&rows=0&q=*:*";
        final String geneParams        = "/gene/select?"        + commonParam + "&" + params.get("gene");
        final String mpParams          = "/mp/select?"          + commonParam + "&" + params.get("mp");
        final String diseaseParams     = "/phenodigm/select?"   + commonParam + "&" + params.get("phenodigm");
        final String anatomyParams     = "/anatomy/select?"     + commonParam + "&" + params.get("anatomy");
        final String impc_imagesParams = "/impc_images/select?" + commonParam + "&" + params.get("impc_images");
        final String productParams     = "/allele2/select?"     + commonParam + "&" + params.get("allele2");

        paramList.add(geneParams);
        paramList.add(mpParams);
        paramList.add(diseaseParams);
        paramList.add(anatomyParams);
        paramList.add(impc_imagesParams);
        paramList.add(productParams);

        cores.add("gene");
        cores.add("mp");
        cores.add("phenodigm");
        cores.add("anatomy");
        cores.add("impc_images");
        cores.add("allele2");
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
//        Date start = new Date();
//        RunStatus masterStatus = new RunStatus();
//        int totalNonzeroCount = 0;
//        String message = "";
//
//        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);
//
//        if (searchString == null)
//            searchString = "";
//
//        try {
//            // Apply searchPhrase. Click on this facet. Click on a random page. Click on each download type: Compare page values with download stream values.
//            String target = paBaseUrl + "/search";
//
//            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, map);
//            if ( ! searchString.isEmpty()) {
//                searchPage.submitSearch(searchString + "\n");
//            }
//
//            for (Facet facet : facets) {
//
//                // Select the correct tab.
//                int facetCount = searchPage.clickTab(facet);
//
//                message = "";
//                if (facetCount == 0) {
//                    System.out.println("\tSKIPPING [" + facet + "] as it has no rows.");
//                    continue;
//                } else {
//                    searchPage.setNumEntries(SearchFacetTable.EntriesSelect._25);
//                    searchPage.clickPageButton();
//
//                    RunStatus status = searchPage.validateDownload(facet);
//                    String statusString = "\t" + (status.hasErrors() ? "FAILED" : "PASSED") + " [" + facet + "]";
//                    System.out.println(statusString + message);
//
//                    masterStatus.add(status);
//                }
//            }
//            totalNonzeroCount++;
//        } catch (Exception e) {
//            System.out.println("FAILED [ " + message + "]\n[" + e.getLocalizedMessage() + "]");
//            e.printStackTrace();
//            masterStatus.addError(message);
//        }
//
//        if ( ! masterStatus.hasErrors())
//            masterStatus.successCount++;
//
//        testUtils.printEpilogue(testName, start, masterStatus, totalNonzeroCount, paramList.size());
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

//    /**
//     * Invokes the facet count engine with the specified, [already escaped if necessary] search term group.
//     *
//     * @param searchTermGroup the desired search term group (may be null)
//     *
//     * @return page status
//     */
//    private RunStatus facetCountEngine(SearchPage searchPage, SearchTermGroup searchTermGroup) throws TestException {
//        RunStatus status = new RunStatus();
//        String message;
//
//        // Get each core's solr counts.
//        Map<String, Integer> solrCoreCountMap = getSolrCoreCounts(searchTermGroup);
//        if (solrCoreCountMap == null) {
//            message = "FAIL: Unable to get facet count from Solr.";
//            masterStatus.addError(message);
//            logger.error(message);
//            return masterStatus;
//        }
//
//        // Compare the search page facet count with the solr count.
//        for ()
//
//
//
//        for (Facet facet : searchPage.getFacetsByTabId().values()) {
//            if ( facet.getCount() != solrCoreCountMap.get(facet.getCoreName())) {
//                message = "FAIL: Facet count mismatch. Page: " + facet.getCount() + ". core: " + solrCoreCountMap.get(facet.getCoreName());
//                masterStatus.addError(message);
//                logger.error(message);
//            }
//        }
//
//
//
//        // Get the search page's version of the counts.
//        Map<String, Integer> pageCountMap = getPageCounts(searchPage, searchTermGroup);
//        if (pageCountMap == null) {
//            message = "FAIL: Unable to get facet count from page.";
//            masterStatus.addError(message);
//            logger.error(message);
//            return masterStatus;
//        }





//       /**
//        * Get each facet's result count from the page. It is found in more than one place depending on the facet.
//        *
//        * For all facets, the result count is on the page, in the right-hand part of the filter.
//        * For all facets BUT images and impc_images, the same result count is in the footer of the 'dTable' HTML table
//        * containing the results.
//        * For the images and impc_images cores (all views), the same result count is just above the 'dTable' HTML table
//        * containing the results.
//        * For the images and impc_images cores in image view ONLY, the same result count is in the footer of the 'dTable'
//        * HTML table, just like the other cores.
//        */
//        for (Facet facet : facets) {
//            int filterCount;
//            int headerCount;
//            int footerCount;
//            int solrCount = solrCoreCountMap.get(facet.getCoreName());
//
//            String target = paBaseUrl + "/search/" + facet.getCoreName() + "?kw=" + (searchTermGroup == null ? "*" : searchTermGroup.solrTarget);
//
//            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
//
//            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@id='" + facet.getFacetName() + "']//span[@class='fcount']")));
//            filterCount = commonUtils.tryParseInt(element.getText().trim());
//
//            switch (facet) {
//                case ANATOMY:
//                case DISEASES:
//                case GENES:
//                case PHENOTYPES:
//                    footerCount = searchPage.getTabResultCountFooter();
//                    // Compare the result counts.
//                    if ((solrCount != filterCount) && (solrCount != footerCount)) {
//                        masterStatus.addError("Search term facet count MISMATCH for " + facet.getFacetName() + " facet: solrCount: " + solrCount + ". filterCount: " + filterCount + ". footerCount: " + footerCount);
//                    }
//                    break;
//
////                case IMAGES:
////                case IMPC_IMAGES:
////                    headerCount = searchPage.getTabResultCountHeader(facet);
////
////                    // Compare the result counts for default Annotation view.
////                    if ((solrCount != filterCount) && (solrCount != headerCount)) {
////                        masterStatus.addError("Search term facet count MISMATCH for " + facet.getFacetName() + " facet: solrCount: " + solrCount + ". filterCount: " + filterCount + ". headerCount: " + headerCount);
////                    }
////                    searchPage.setImageFacetView(SearchFacetTable.ImagesView.IMAGE_VIEW);
////                    footerCount = searchPage.getTabResultCountFooter(facet);
////                    wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@id='" + facet.getFacetName() + "']//span[@class='fcount']")));
////                    filterCount = commonUtils.tryParseInt(element.getText().trim());
////
////                    // Compare the results for the Image view.
////                    if ((solrCount != filterCount) && (solrCount != footerCount)) {
////                        masterStatus.addError("Search term facet count MISMATCH for " + facet.getFacetName() + " facet: solrCount: " + solrCount + ". filterCount: " + filterCount + ". footerCount: " + footerCount);
////                    }
////                    break;
//            }
//
//
//
//            SearchPage.FacetFilter facetFilter = searchPage.getFacetFilter();
//            String name = facetFilter.getName();
//            int count = facetFilter.getCount();
//            facetFilter.open();
//            facetFilter.close();
//            name = facetFilter.getName();
//            count = facetFilter.getCount();
////            facetFilter.getFacetFilterSubfacets();
//            System.out.println();
//       }
//
//        return masterStatus;
//    }

    /**
     * Validate Filter on left side of search screen by scraping the values and comparing against expected values.
     *
     * @param facetFilter A valid <code>FacetFilter</code> instance
     * @param expectedMinFacetCount The expected minimum facet count (the number to the right of the facet name)
     * @param expectedMinNumFacetRows The expected number of facet rows for this facet
     * @param subfacetText The subfacet text you want to match. Set to null if this filter has no subfacets.
     * @param expectedMinNumSubfacetRows The expected number of subfacet rows for this facet. Ignored if subfacetText is null
     * @param facetRowText The facet text you want to match
     * @param expectedMinFacetRowCount The expected facet row count (the number to the right of the facet row)
     *
     * @return status
     */
    private RunStatus validateFilter(SearchPage.FacetFilter facetFilter, int expectedMinFacetCount, int expectedMinNumFacetRows,
                                     String subfacetText, int expectedMinNumSubfacetRows,
                                     String facetRowText, int expectedMinFacetRowCount) {
        RunStatus status = new RunStatus();
        String message;

        // Check for the minimum number of facet rows.
        if (facetFilter.getNumFacetRows() < expectedMinNumFacetRows) {
            message = "Expected at least " + expectedMinNumFacetRows + " facetFilterFacetRows but found " + facetFilter.getNumFacetRows();
            status.addError(message);
        }

        // Check the min facet count.
        if (facetFilter.getCount() < expectedMinFacetCount) {
            message = "Expected minimum facet count  " + expectedMinFacetCount + " but found " + facetFilter.getCount();
            status.addError(message);
        }

        SearchPage.FacetRow facetRow;
        if (subfacetText != null) {
            // Check for the minimum number of subfacet rows.
            if (facetFilter.getNumSubfacetRows() < expectedMinNumSubfacetRows) {
                message = "Expected at least " + expectedMinNumSubfacetRows + " subfacet rows but found " + facetFilter.getNumSubfacetRows();
                status.addError(message);
            }
            // Make sure subfacet is open.
            facetFilter.getSubfacet(subfacetText).open();
            facetRow = facetFilter.getSubfacet(subfacetText).getFacetRow(facetRowText);
        } else {
            facetRow = facetFilter.getFacetRow(facetRowText);
        }

        // Check the selected facet row.
        if (facetRow == null) {
            message = "Subfacet " + subfacetText + ": Expected to find '" + facetRowText + "' but it was not found.";
            status.addError(message);
        } else {
            if (facetRow.getCount() < expectedMinFacetRowCount) {
                message = "Subfacet " + subfacetText + ": Expected at least " + expectedMinFacetRowCount + " genes for '" + facetRowText + "' but found " + facetRow.getCount();
                status.addError(message);
            }

            if ( ! facetRow.isEnabled()) {
                message = "Subfacet " + subfacetText + ": Expected checkbox '" + facetRowText + "' to be enabled but it was not.";
                status.addError(message);
            }

            if (facetRow.isChecked()) {
                message = "Subfacet " + subfacetText + ": Expected checkbox '" + facetRowText + "' to be unchecked but it was checked.";
                status.addError(message);
            }

            facetRow.check();
            if ( ! facetRow.isChecked()) {
                message = "Subfacet " + subfacetText + ": Expected checkbox '" + facetRowText + "' to be checked but it was unchecked.";
                status.addError(message);
            }

            facetRow.uncheck();
            if (facetFilter.getNumSubfacetRows() > 0) {
                facetFilter.getSubfacet(subfacetText).open();   // For facets with subfacets, the 'uncheck()' above closes the subfacet. Open it back up first.
            }
            if (facetRow.isChecked()) {
                message = "Subfacet " + subfacetText + ": Expected checkbox '" + facetRowText + "' to be unchecked but it was checked (2).";
                status.addError(message);
            }
        }

        return status;
    }


//    /**
//     * Queries each of the six search solr cores for the number of occurrences
//     * of <code>searchPhrase</code> (which may be null), returning a
//     * <code>Map</code> keyed by core name containing the occurrence count for
//     * each core.
//     *
//     * @param searchTermGroup The search phrase to use when querying the cores. If
//     * null, the count is unfiltered.
//     * @return the <code>searchPhrase</code> occurrence count
//     */
//    private Set<Facet> getPageCounts(SearchPage searchPage, SearchTermGroup searchTermGroup) throws TestException {
//        Set<Facet> pageCounts = new HashSet<>();
//        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul[@class='tabLabel']")));
//        String result = element.getText().trim();
//        String[] labels = StringUtils.split(result, "\n");
//        for (String label : labels) {
//            String[] labelParts = StringUtils.split(label, " (");
//            Integer i = commonUtils.tryParseInt(labelParts[1].replace(")", ""));
//            pageCounts.put(labelParts[0], i);
//        }
//
//        return pageCounts;
//    }

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
    private Map<String, SolrData> getSolrCoreCounts(SearchTermGroup searchTermGroup) {
        Map<String, SolrData> solrCoreCountMap = new HashMap();

        for (int i = 0; i < paramList.size(); i++) {
            String solrQueryString = paramList.get(i);
            try {
                if (searchTermGroup != null) {
                    solrQueryString = solrQueryString.replace("&q=*:*", "&q=\"" + searchTermGroup.solrTarget + "\"");
                }

                String fqSolrQueryString = solrUrl + solrQueryString;
//System.out.println(fqSolrQueryString);

                JSONObject geneResults = JSONRestUtil.getResults(fqSolrQueryString);
                int facetCountFromSolr = geneResults.getJSONObject("response").getInt("numFound");
                String facet = cores.get(i);
                solrCoreCountMap.put(facet, new SolrData(facetCountFromSolr, fqSolrQueryString));
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

    public class SolrData {
        public final int count;
        public final String url;

        public SolrData() {
            this.count = -1;
            this.url = "";
        }

        public SolrData(int count, String url) {
            this.count = count;
            this.url = url;
        }
    }








    private void specialStrQueryTest(String testName, String searchTerm) throws TestException {
        Date start = new Date();
        RunStatus status = new RunStatus();
        String message;

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        // Build and load the page.
        String target = paBaseUrl + "/search?q=" + searchTerm;
        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
        searchPage.submitSearch(searchTerm + "\n");

        // Get each core's solr counts.
        Map<String, SolrData> solrCoreCountMap = getSolrCoreCounts(new SearchTermGroup(searchTerm, searchTerm));
        if (solrCoreCountMap == null) {
            message = "FAIL: Unable to get facet count from Solr.";
            status.addError(message);
            logger.error(message);
        }

        // Compare the search page facet count with the solr count.
        for (Facet facet : searchPage.getFacetsByTabId().values()) {
            SolrData solrData = solrCoreCountMap.get(facet.getCoreName());
            if (facet.getCount() != solrData.count) {
                message = "FAIL: " + facet.getTabName() + " Facet count mismatch. Term: '" + searchTerm + "'. Page: " + facet.getCount() + ". core: " + solrData.count +
                        "\n\tPAGE URL: " + target + "\n\tSOLR URL: " + solrData.url;
                status.addError(message);
                logger.error(message);
            }
        }

        if ( ! status.hasErrors()) {
            status.successCount++;
        }

        testUtils.printEpilogue(testName, start, status, 1, 1);
    }


    // TESTS


    @Test
//@Ignore
//FIXED
    public void testAutosuggestForSpecificKnownGenes() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus masterStatus = new RunStatus();

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        String[] geneSymbols = {
              "Klk4"
        };

        String message;

        for (String geneSymbol : geneSymbols) {
            RunStatus status = new RunStatus();
            String target = paBaseUrl + "/search";

            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
            List<String> autoSuggestions = searchPage.getAutosuggest(geneSymbol);

            boolean found = false;
            for (String row : autoSuggestions) {
                if (row.equalsIgnoreCase(geneSymbol)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                status.successCount++;
            } else {
                message = "\tFAILED [Expected to find gene id '" + geneSymbol + "' in the autosuggest list but it was not found].";
                status.addError(message);
                for (String row : autoSuggestions) {
                    message += "\n" + row;
                }
                status.addError(message);
            }

            masterStatus.add(status);
        }

        testUtils.printEpilogue(testName, start, masterStatus, geneSymbols.length, geneSymbols.length);
    }

    @Test
//@Ignore
//FIXED
    public void testAutosuggestMinCharacters() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus status = new RunStatus();

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        String geneSymbol = "mas";

        // NOTE: Results don't seem to be ordered, so it's possible the gene is beyond the first 10 shown.
        String message;

        String target = paBaseUrl + "/search";

        SearchPage   searchPage      = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
        List<String> autoSuggestions = searchPage.getAutosuggest(geneSymbol);

        int numTerms = autoSuggestions.size();
        if (numTerms > 0) {
            status.successCount++;
        } else {
            status.addError("Entered " + geneSymbol + " into search box. Expected matches but found none.");
        }

        testUtils.printEpilogue(testName, start, status, 1, 1);
    }

    @Test
@Ignore
// DOWNLOAD
    public void testBoneDownload() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String searchString = "bone";

        downloadTestEngine(testName, searchString, imageMap);
    }

    @Test
@Ignore
// DOWNLOAD
    public void testBrachydactyly() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String searchString = "brachydactyly";

        downloadTestEngine(testName, searchString, imageMap);
    }

    @Test
@Ignore
// DOWNLOAD
    public void testDefaultDownload() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String searchString = null;

        downloadTestEngine(testName, searchString, imageMap);
    }

    @Test
//@Ignore
//FIXED
    public void testFacetCountsNoSearchTermGroup() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus status = new RunStatus();
        String message;

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        String target = paBaseUrl + "/search/?kw=*";
        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);

        // Get each core's solr counts.
        Map<String, SolrData> solrCoreCountMap = getSolrCoreCounts(null);
        if (solrCoreCountMap == null) {
            message = "FAIL: Unable to get facet count from Solr.";
            status.addError(message);
            logger.error(message);
        }

        // Compare the search page facet count with the solr count.
        for (Facet facet : searchPage.getFacetsByTabId().values()) {
            SolrData solrData = solrCoreCountMap.get(facet.getCoreName());
            if (facet.getCount() != solrData.count) {
                message = "FAIL: " + facet.getTabName() + " Facet count mismatch. Term: none. Page: " + facet.getCount() + ". core: " + solrData.count +
                        "\n\tPAGE URL: " + target + "\n\tSOLR URL: " + solrData.url;
                status.addError(message);
                logger.error(message);
            }
        }

        testUtils.printEpilogue(testName, start, status, cores.size(), cores.size());
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
//        , new SearchTermGroup("€",          "\\%E2%82%AC")      // €    %E2%82%AC
//        , new SearchTermGroup("£",          "\\%C2%A3")         // £    %C2%A3
        , new SearchTermGroup("\\%23",      "\\%23")            // #    %23
        , new SearchTermGroup("$",          "$")                // $    %24
        , new SearchTermGroup("\\%25",      "\\%25")            // %    %25
        , new SearchTermGroup("^",          "^")                // ^    %5E
        , new SearchTermGroup("\\%26",      "\\%26")            // &    %26
        , new SearchTermGroup("\\*",        "\\%2A")            // *    %2A
        , new SearchTermGroup("\\%28",      "\\%28")            // (    %28
        , new SearchTermGroup(")",          ")")                // )    %29
        , new SearchTermGroup("\\-",        "\\-")              // -    %2D (hyphen)
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
@Ignore
// FIXME AFTER WE DEFINE AND DOCUMENT WILDCARD BEHAVIOUR. CURRENTLY MANY TESTS FAIL THAT USE ASTERISKS BECAUSE OF MISMATCH BETWEEN PAGE AND SOLR COUNTS. I DON'T KNOW
// FIXME WHAT THE EXPECTED BEHAVOIUR SHOULD BE UNTIL WE DISCUSS AND DOCUMENT IT.
    public void testFacetCountsSpecialCharacters() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus masterStatus = new RunStatus();
        String message;

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

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
int i = 0;
        for (SearchTermGroup searchTermGroup : searchTermGroupWildcard) {
//if (i++ == 5) { testUtils.printEpilogue(testName, start, masterStatus, searchTermGroupWildcard.length, searchTermGroupWildcard.length);return;}
            RunStatus status = new RunStatus();

            // Build and load the page.
            String target = paBaseUrl + "/search?q=" + searchTermGroup.pageTarget;
            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
            searchPage.submitSearch(searchTermGroup.pageTarget + "\n");

            // Get each core's solr counts.
            Map<String, SolrData> solrCoreCountMap = getSolrCoreCounts(searchTermGroup);
            if (solrCoreCountMap == null) {
                message = "FAIL: Unable to get facet count from Solr.";
                status.addError(message);
                logger.error(message);
            }

            // Compare the search page facet count with the solr count.
            for (Facet facet : searchPage.getFacetsByTabId().values()) {
                SolrData solrData = solrCoreCountMap.get(facet.getCoreName());
                if (facet.getCount() != solrData.count) {
                    message = "FAIL: " + facet.getTabName() + " Facet count mismatch. Term: '" + searchTermGroup.pageTarget + "'. Page: " + facet.getCount() + ". core: " + solrData.count +
                            "\n\tPAGE URL: " + target + "\n\tSOLR URL: " + solrData.url;
                    status.addError(message);
                    logger.error(message);
                }
            }

            if ( ! status.hasErrors())
                status.successCount++;

            masterStatus.add(status);
        }

        testUtils.printEpilogue(testName, start, masterStatus, searchTermGroupWildcard.length, searchTermGroupWildcard.length);
    }

//    @Test
//@Ignore
//    public void testFilter() throws TestException {
//        String testName = "testFilter";
//        Date start = new Date();
//        RunStatus masterStatus = new RunStatus();
//        String message;
//
//        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);
//
//        for (Facet facet : facets) {
//            RunStatus status = new RunStatus();
//
//            String target = paBaseUrl + "/search/" + facet.getCoreName() + "?kw=*";
//            System.out.println("Testing facet " + facet.getTabName() + "   " + target);
//            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
//
//            SearchPage.FacetFilter facetFilter = searchPage.getFacetFilter();
//            String name = facetFilter.getName();
//            if ( ! name.equals(facet.getTabName())) {
//                message = "Expected tab name '" + facet.getTabName() + "' but actual name was '" + facetFilter.getName() + "'.";
//                status.addError(message);
//            }
//
//            facetFilter.open();
//            if ( ! facetFilter.isOpen())  {
//                message = "Expected facet name '" + facet.getFacetName() + "' to be opened but it was not.";
//                status.addError(message);
//            }
//
//            facetFilter.close();
//            if (facetFilter.isOpen())  {
//                message = "Expected facet name '" + facet.getFacetName() + "' to be closed but it was not.";
//                status.addError(message);
//            }
//            name = facetFilter.getName();
//            if ( ! name.equals(facet.getTabName())) {
//                message = "Closed subfacet: Expected tab name '" + facet.getTabName() + "' but actual name was '" + facetFilter.getName() + "'.";
//                status.addError(message);
//            }
//
//            // Custom test for each facet for min subfacets/rows/names, etc.
//            facetFilter.open();             // Make sure facet is opened.
//            switch (facet) {
//                case ANATOMY:
//                    status = validateFilter(facetFilter, 400, 15, null, 0, "urinary system", 8);                        // No subfacets.
//                    break;
//
//                case DISEASES:
//                    status = validateFilter(facetFilter, 7300, 0, "Classifications", 4,"respiratory", 77);              // Has subfacets.
//                    break;
//
//                case GENES:
//                    status = validateFilter(facetFilter, 23400, 0, "IMPC Mouse Phenotype Center", 6, "RIKEN BRC", 41);  // Has subfacets.
//                    break;
//
//                case IMPC_IMAGES:
//                    status = validateFilter(facetFilter, 187000, 0, "Anatomy", 2, "digestive system", 5400);            // Has subfacets.
//                    break;
//
//                case PHENOTYPES:
//                    status = validateFilter(facetFilter, 1400, 25, null, 0, "immune system", 405);                      // No subfacets.
//                    break;
//            }
//
//            if (status.hasErrors()) {
//                System.out.println(status.toStringErrorMessages());
//            } else {
//                status.successCount++;
//            }
//
//            masterStatus.add(status);
//        }
//
//        testUtils.printEpilogue(testName, start, masterStatus, facets.length, facets.length);
//    }

    // Test that when Wnt1 is selected, it is at the top of the autosuggest list.
    @Test
//@Ignore
//FIXED
    public void testHox() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus status = new RunStatus();

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        String searchString = "Hox";
        String target = paBaseUrl + "/search";

        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
        List<String> rows = searchPage.getAutosuggest(searchString);
        String expectedStartsWith = "Hox";

        if (rows.size() < 10) {
            status.addError("Expected at least 10 autosuggest rows. Found " + rows.size() + " rows.");
        } else {
            for (int i = 0; i < rows.size(); i++) {
                String row = rows.get(i);
                if (row.trim().isEmpty())
                    continue;
                if ( ! row.startsWith(expectedStartsWith)) {

                    status.addError("Row[" + i + "]: Expected autosuggest row to begin with 'Hox'. Row value = " + row);
                    break;
                }
            }
        }

        if ( ! status.hasErrors()) {
            status.successCount++;
        }

        testUtils.printEpilogue(testName, start, status, 1, 1);
    }

    // Test that when Wnt1 is selected, it is at the top of the autosuggest list.
    @Test
//@Ignore
//FIXED
    public void testHoxStar() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus status = new RunStatus();

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        String searchString = "Hox*";
        String target = paBaseUrl + "/search";

        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
        List<String> rows = searchPage.getAutosuggest(searchString);
        String expectedStartsWith = "Hox";

        if (rows.size() < 6) {
            status.addError("Expected at least 6 autosuggest rows. Found " + rows.size() + " rows.");
        } else {
            for (int i = 0; i < rows.size(); i++) {
                String row = rows.get(i);
                if (row.isEmpty())
                    continue;
                if ( ! row.startsWith(expectedStartsWith)) {
                    status.addError("Row[" + i + "]: Expected autosuggest row to begin with 'Hox'. Row value = " + row);
                    break;
                }
            }
        }

        if ( ! status.hasErrors()) {
            status.successCount++;
        }

        testUtils.printEpilogue(testName, start, status, 1, 1);
    }

    // This test doesn't use the download test engine as it requires an extra
    // click to switch to the Image facet's 'Image' view. It also tests only the IMPC_IMAGES facet rather than all facets.
    @Test
@Ignore
    public void testImpcImageFacetImageView() throws TestException {
//        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        String searchString = "";
//        Date start = new Date();
//        RunStatus status = new RunStatus();
//        Facet facet;
//        String message = "";
//
//        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);
//        String target = "";
//
//        try {
//            target = paBaseUrl + "/search";
//            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, impcImageMap);
//
//            facet = Facet.IMPC_IMAGES;
//
//            // Select the correct tab.
//            int facetCount = searchPage.clickTab(facet);
//
//            searchPage.clickFacet(facet);
//            searchPage.getImpcImageTable().setCurrentView(SearchFacetTable.ImagesView.IMAGE_VIEW);
//
//            message = "[facet " + facet + "], image view. Search string: '" + searchString + "'. URL: " + target;
//
//            status.add(searchPage.validateDownload(facet));
//
//        } catch (TestException e) {
//            message = "FAILED [" + message + "]. URL: " + target + ". localMessage: " + e.getLocalizedMessage();
//            System.out.println(message);
//            e.printStackTrace();
//            status.addError(message);
//        }
//
//        if ( ! status.hasErrors()) {
//            status.successCount++;
//        }
//
//        testUtils.printEpilogue(testName, start, status, 1, 1);
    }

    @Test
@Ignore
    public void testLeadingWildcard() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        specialStrQueryTest(testName, "*rik");
    }

    @Test
@Ignore
    public void testLegDownload() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
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
//    @Test
//@Ignore
//    public void testMaTermNamesMatchFacetNames() throws TestException {
//    String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        Date start = new Date();
//        int expectedCount;
//        RunStatus status = new RunStatus();
//
//        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);
//
//        String target = paBaseUrl + "/search";
//
//        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, impcImageMap);
//
//        // Select the correct tab.
//        searchPage.clickTab(Facet.ANATOMY);
//
//        String[] pageMaFacetTermNames = searchPage.getFacetNames(Facet.ANATOMY);
//        expectedCount = pageMaFacetTermNames.length;
//        for (int i = 0; i < pageMaFacetTermNames.length; i++) {
//            // Remove the newline and facet count.
//            int nl = pageMaFacetTermNames[i].lastIndexOf("\n");
//            pageMaFacetTermNames[i] = pageMaFacetTermNames[i].substring(0, nl);
//        }
//
//        ArrayList<String> dbMaFacetTermNames = new ArrayList();
//
//        String query =
//              "SELECT hla.TERM_ID, ti.name\n"
//            + "FROM komp2.higher_level_annotation hla\n"
//            + "JOIN ontodb_komp2.ma_term_infos ti ON ti.term_id = hla.TERM_ID\n"
//            + "ORDER BY ti.name\n";
//        PreparedStatement ps = null;
//        try {
//            ps = komp2Connection.prepareStatement(query);
//            ResultSet resultSet = ps.executeQuery();
//            while (resultSet.next()) {
//                dbMaFacetTermNames.add(resultSet.getString("name"));
//            }
//            ps.close();
//        } catch (SQLException sqle) {
//            if ( ps != null) {
//                try { ps.close(); } catch (SQLException sqle2) { }
//            }
//            throw new TestException(testName + ": SQL Exception", sqle);
//        }
//
//        Set<String> pageSet = new HashSet(Arrays.asList(pageMaFacetTermNames));
//        Set<String> dbSet = new HashSet(dbMaFacetTermNames);
//
//        if (pageSet.size() != dbSet.size()) {
//            status.addError("facet name count mismatch: "
//                    + dumpLists(Arrays.asList(pageMaFacetTermNames), dbMaFacetTermNames));
//        }
//
//        for (int i = 0; i < pageSet.size(); i++) {
//            if (pageMaFacetTermNames[i].equals(dbMaFacetTermNames.get(i))) {
//                status.successCount++;
//            } else {
//                status.addError("facet name mismatch: "
//                  + dumpLists(Arrays.asList(pageMaFacetTermNames), dbMaFacetTermNames));
//            }
//        }
//
//        testUtils.printEpilogue(testName, start, status, expectedCount, expectedCount);
//    }

    /**
     * Test for Jira bug MPII-806: from the search page, searching for the characters
     * "fasting glu" should autosuggest 'fasting' and 'glucose'. Click on 'fasting glucose'
     * and verify that the correct phenotype page appears.
     * @throws TestException
     */
    @Test
@Ignore
    public void testMPII_806() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus status = new RunStatus();

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

         String queryStr = paBaseUrl + "/search";
         driver.get(queryStr);
         String characters = "fasting glu";
         driver.findElement(By.cssSelector("input#s")).sendKeys(characters);

         // Wait for dropdown list to appear with candidates.
        String xpathSelector = "//ul[@id='ui-id-1']/li[@class='ui-menu-item']/a";
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathSelector)));
        if (( ! element.getText().contains("fasting")) && ( ! element.getText().contains("glucose"))){
            status.addError("ERROR: Expected the terms 'fasting' and 'glucose' but found '" + element.getText() + "'");
        } else {
            element.click();
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@id='resultCount']")));
            String resultCountString = element.getText().toLowerCase();
            if (( ! resultCountString.contains("gene")) &&
                ( ! resultCountString.contains("phenotype")) &&
                ( ! resultCountString.contains("phenodigm"))) {
                status.addError("ERROR: Expected result text to contain 'gene' or 'phenotype' or 'phenodigm'. Actual text was '" + resultCountString + "'");
            }
        }

        if ( ! status.hasErrors())
            status.successCount++;

        testUtils.printEpilogue(testName, start, status, 1, 1);
    }

    // Tests search page with more than one Production Status [blue] order button.
    // We'll use MGI:1353431 (gene Pcks1n), which has 2 Production Status buttons.
    @Test
@Ignore
    public void testOrderButtons() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus status = new RunStatus();

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        int buttonElementsSize = 0;
        String message;
        String target = "";

        try {
            target = urlUtils.urlEncode(paBaseUrl + "/search?q=MGI\\:1353431#fq=*:*&facet=gene");
            System.out.println("target: " + target);
            SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, impcImageMap);

            // Use the first gene div element in the search results.
            List<WebElement> geneElements = driver.findElements(By.xpath("//*[@id='geneGrid']/tbody/tr[1]"));
            if (geneElements.isEmpty()) {
                fail("Can't find first 'geneGrid' div element in gene search results list. URL: " + target);
            }

            buttonElementsSize = searchPage.getProductionStatusOrderButtons(geneElements.get(0)).size();

            if (buttonElementsSize < 2) {
                status.addError("This test expects at least two production status order buttons. Number of buttons found: " + buttonElementsSize + ". URL: " + target);
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
                    }

                    driver.navigate().back();
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(path)));
                }
            }

        } catch (TestException e) {
            message = "ERROR: Failed to load search page URL: " + target;
            status.addError(message);
        }

        if ( ! status.hasErrors())
            status.successCount++;

        testUtils.printEpilogue(testName, start, status, 1, 1);
    }

//    @Test
//@Ignore
//    public void testPagination() throws TestException {
//String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        Date start = new Date();
//        RunStatus masterStatus = new RunStatus();
//
//        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);
//
//        String target;
//        String message = "";
//        final String showing_1 = "Showing 1 to ";
//        final String showing_11 = "Showing 11 to ";
//        String expectedShowingPhrase = "";
//        String actualShowing = "";
//
//        for (String core : cores ) {
//            RunStatus status = new RunStatus();
//            target = paBaseUrl + "/search#" + params.get(core) + "&facet=" + core;
//
//            try {
//                SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
//                searchPage.clickFacetById(core);
//                Facet facet = searchPage.getFacetByCoreName(core);
//
//                // Select the correct tab.
//                searchPage.clickTab(facet);
//
//                // Upon entry, the 'showing' string should start with 'Showing 1 to 10 of".
//                expectedShowingPhrase = showing_1;
//                actualShowing = searchPage.getShowing().toString();
//                if ( ! actualShowing.contains(expectedShowingPhrase)) {
//                    message = "Expected '" + expectedShowingPhrase + "' but found '" + actualShowing + "'. URL: " + target;
//                    status.addError(message);
//                }
//
//                if (searchPage.getNumPageButtons() > 3) {                       // Previous, page, Next
//                    // Wait for facet page to load, then click the page '2' link. The 'showing' string should start with 'Showing 11 to 20 of".
//                    searchPage.clickPageButton(SearchPage.PageDirective.SECOND_NUMBERED);
//                    expectedShowingPhrase = showing_11;
//
//                    actualShowing = searchPage.getShowing().toString();
//                    if ( ! actualShowing.contains(expectedShowingPhrase)) {
//                        message = "Expected '" + expectedShowingPhrase + "' but found '" + actualShowing + "'. URL: " + target;
//                        status.addError(message);
//                    }
//                }
//
//                String statusString = "\t" + (status.hasErrors() ? "FAILED" : "PASSED") + " [" + facet + "]";
//                System.out.println(statusString + message);
//
//                if ( ! status.hasErrors())
//                    status.successCount++;
//
//                masterStatus.add(status);
//
//            } catch (TestException e) {
//                message = "Expected '" + expectedShowingPhrase + "' but found '" + actualShowing + "'. URL: " + target + "/nmessage: " + e.getLocalizedMessage();
//                masterStatus.addError(message);
//                e.printStackTrace();
//            }
//        }
//
//        testUtils.printEpilogue(testName, start, masterStatus, cores.size(), cores.size());
//    }

    // FIXME FIXME FIXME This test fails as of 03-Apr-2019, so I'm disabling it as there isn't any obvious failure observed here. Will research later.
    @Test
@Ignore
    public void testPhrase() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        specialStrQueryTest(testName, "grip strength");
    }

    // FIXME FIXME FIXME This test fails as of 03-Apr-2019, so I'm disabling it as there isn't any obvious failure observed here. Will research later.
    @Test
@Ignore
    public void testPhraseInQuotes() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        specialStrQueryTest(testName, "zinc finger protein");
    }

    // Verify that random genes appear in the autosuggest list.
    @Test
@Ignore
    public void testQueryingRandomGeneSymbols() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus masterStatus = new RunStatus();
        int nbRows = 20;

        testUtils.logTestStartup(logger, this.getClass(), testName, nbRows, nbRows);

        Random rn = new Random();
        int startIndex = rn.nextInt(60000 - 0 + 1) + 1;

        String target = paBaseUrl + "/search#fq=*:*&facet=gene";
        System.out.println("URL: " + target);
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
                    SearchPage searchPage = new SearchPage(driver, timeoutInSeconds * 2, target, phenotypePipelineDAO, paBaseUrl, imageMap);
                    searchPage.submitSearch(geneSymbol1);
                    WebDriverWait localWait = new WebDriverWait(driver, 10);        // Wait up to 10 seconds.
                    localWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//ul[contains(@class, 'ui-autocomplete')]")));
                    List<WebElement> elems = localWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("ul#ui-id-1 li.ui-menu-item a span b.sugTerm")));
                    String geneSymbol2 = null;
                    String autosuggestCandidates = "";
                    for (WebElement elem : elems) {
                        String autosuggestGene = elem.getText();
                        if ( ! autosuggestCandidates.isEmpty())
                            autosuggestCandidates += ",";
                        autosuggestCandidates += autosuggestGene;
                        if (autosuggestGene.equals(geneSymbol1)) {
                            geneSymbol2 = elem.getText();
                            break;
                        }
                    }

                    if (geneSymbol1.trim().toLowerCase().equals(geneSymbol2.trim().toLowerCase())) {
                        System.out.println("\tPASSED [" + geneSymbol1 + "]");
                    } else {
                        message = "\tFAILED [" + geneSymbol1 + "]: Expected to find gene id '" + geneSymbol1 + "' in the autosuggest list but it was not found. URL: " + target;
                        System.out.println(message);
                        System.out.println("\tWas searching for '" + geneSymbol1 + "' in [" + autosuggestCandidates + "]");

                        masterStatus.addError(message);
                    }
                } catch (Exception e) {
                    masterStatus.addError("ERROR testing gene symbol " + geneSymbol1 + ": " + e.getLocalizedMessage());
                }
            }
        }

        if ( ! masterStatus.hasErrors())
            masterStatus.successCount++;

        testUtils.printEpilogue(testName, start, masterStatus, nbRows, nbRows);
    }

    @Test
@Ignore
    public void testRandomMgiIds() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus masterStatus = new RunStatus();
        int nbRows = 20;

        testUtils.logTestStartup(logger, this.getClass(), testName, nbRows, nbRows);

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
                RunStatus status = new RunStatus();
                count = i + 1;
                String mgiId = docs.getJSONObject(i).getString("mgi_accession_id");
                String symbol = docs.getJSONObject(i).getString("marker_symbol");
                String target = paBaseUrl + "/search?q=" + mgiId;
                String message = "[" + String.format("% 2d", i) + ": " + String.format("%-10s", mgiId) + "]. URL: " + target;

                driver.get(target);

                new WebDriverWait(driver, 25).until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.geneCol")));
                try {
                    driver.findElement(By.cssSelector("div.geneCol a").linkText(symbol));
                    if ( ! status.hasErrors())
                        status.successCount++;

                    String statusString = "\t" + (status.hasErrors() ? "FAILED " : "PASSED " );
                    System.out.println(statusString + message);
                    masterStatus.add(status);

                } catch (Exception e) {
                    System.out.println("FAILED [ " + message + "]\n[" + e.getLocalizedMessage() + "]");
                    e.printStackTrace();
                    masterStatus.addError(message);
                }
            }
        }

        testUtils.printEpilogue(testName, start, masterStatus, nbRows, nbRows);
    }

    @Test
@Ignore
    public void testTickingFacetFilters() throws TestException {
//        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
//        Date start = new Date();
//        RunStatus masterStatus = new RunStatus();
//        String message = "";
//        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);
//
//        String target = paBaseUrl + "/search";
//        System.out.println("target Page URL: " + target);
//        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
//
//        // For each core:
//        //   Click the first subfacet.
//        //   Check that it is selected.
//        //   Check that there is a filter matching the selected facet above the Genes facet.
//        //   Click the first subfacet again to unselect it.
//        //   Check that it is unselected.
//        //   Check that there is no filter matching the just-unselected facet above the Genes facet.
//        for (String core :  cores) {
//            String subfacetCheckboxCssSelector = "li#" + core + " li.fcat input[type='checkbox']";
//            String subfacetTextCssSelector = "li#" + core + " li.fcat span.flabel";
//            Facet facet = searchPage.getFacetByCoreName(core);
//            searchPage.openFacet(facet);                                        // Open facet if it is not alreay opened.
//            WebElement firstSubfacetElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(subfacetCheckboxCssSelector)));
//            testUtils.scrollToTop(driver, firstSubfacetElement, -50);           // Scroll first subfacet element into view.
//            firstSubfacetElement.click();                                       // Select the first subfacet.
//
//            searchPage.openFacet(facet);                                        // Re-open the facet as, by design, it closed after the click() above.
//            RunStatus status = new RunStatus();
//            if ( ! firstSubfacetElement.isSelected()) {                         // Verify that the subfacet is selected.
//                message = "Failed to tick input filter. Expected first subfacet filter to be checked, but it wasn't. URL = " + driver.getCurrentUrl();
//                status.addError(message);
//            } else {
//
//                // Check that there is a filter matching the selected facet above the Genes facet.
//                String facetText = driver.findElement(By.cssSelector(subfacetTextCssSelector)).getText();
//                HashMap<Facet, SearchPage.FacetFilter> facetFilterHash = searchPage.getFacetFilter();
//                List<String> facetFilterText = facetFilterHash.get(facet).subfacetTexts;
//                boolean found = false;
//                for (String facetFilter : facetFilterText) {
//                    if (facetFilter.contains(facetText)) {
//                        found = true;
//                        break;
//                    }
//                }
//                if ( ! found) {
//                    message = "Expected subfacet filter for '" + facetText + "' but it was not found. URL: " + driver.getCurrentUrl();
//                    status.addError(message);
//                }
//
//                searchPage.openFacet(facet);                                        // Open facet if it is not alreay opened.
//                firstSubfacetElement.click();                                       // Deselect the first subfacet.
//
//                searchPage.openFacet(facet);                                        // Re-open the facet as, by design, it closed after the click() above.
//
//                // The page becomes stale after the click() above, so we must re-fetch the WebElement objects.
//                firstSubfacetElement = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(subfacetCheckboxCssSelector)));
//
//                if (firstSubfacetElement.isSelected()) {                            // Verify that the subfacet is no longer selected.
//                    message = "Expected subfacet checkbox to be unchecked. URL: " + driver.getCurrentUrl();
//                    status.addError(message);
//                }
//
//                // Check that there are no filters.
//                if (searchPage.hasFilters()) {
//                    message = "Expected filters to be cleared. URL: " + driver.getCurrentUrl();
//                    status.addError(message);
//                }
//
//                searchPage.clearFilters();
//            }
//
//            String statusString = "\t" + (status.hasErrors() ? "FAILED" : "PASSED") + " [" + facet + "]";
//            System.out.println(statusString + message);
//
//            masterStatus.add(status);
//
//            searchPage.closeFacet(facet);                                           // Close the facet.
//        }
//
//        if ( ! masterStatus.hasErrors())
//            masterStatus.successCount++;
//
//        testUtils.printEpilogue(testName, start, masterStatus, 1, 1);
    }

    @Test
@Ignore
    public void testTrailingWildcard() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        specialStrQueryTest(testName, "hox*");
    }

    @Test
@Ignore
    public void testTwist1() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String searchString = "twist1";

        downloadTestEngine(testName, searchString, imageMap);
    }

    // Test that when Wnt1 is selected, it is at the top of the autosuggest list.
    @Test
//@Ignore
//FIXED
    public void testWnt1IsAtTop() throws TestException {
        String testName = Thread.currentThread().getStackTrace()[1].getMethodName();
        Date start = new Date();
        RunStatus status = new RunStatus();

        testUtils.logTestStartup(logger, this.getClass(), testName, 1, 1);

        String searchString = "Wnt1";
        String target = paBaseUrl + "/search";

        SearchPage searchPage = new SearchPage(driver, timeoutInSeconds, target, phenotypePipelineDAO, paBaseUrl, imageMap);
        List<String> rows = searchPage.getAutosuggest(searchString);
        String expectedResult = "Wnt1";

        if (rows.isEmpty()) {
            status.addError("Expected at least 1 row. No rows found.");
        } else {
            String row = rows.get(0);
            if ( ! row.startsWith(expectedResult)) {
                status.addError("Expected Wnt1 to be at top of autosuggest row. Top value = " + row);
            }
        }

        if ( ! status.hasErrors())
            status.successCount++;

        testUtils.printEpilogue(testName, start, status, 1, 1);
    }
}