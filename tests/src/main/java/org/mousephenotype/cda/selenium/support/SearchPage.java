/*******************************************************************************
 * Copyright 2015 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 *******************************************************************************/

package org.mousephenotype.cda.selenium.support;

import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.selenium.exception.TestException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.DataReaderTsv;
import org.mousephenotype.cda.utilities.DataReaderXls;
import org.mousephenotype.cda.web.DownloadType;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

/**
 *
 * @author mrelac
 *
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page.
 */
public class SearchPage {

    private final String               baseUrl;
    protected final CommonUtils        commonUtils = new CommonUtils();
    private final WebDriver            driver;
    private final PhenotypePipelineDAO phenotypePipelineDAO;
    private Random                     random = new Random();
    private String                     target;
    protected final TestUtils          testUtils = new TestUtils();
    private final int                  timeoutInSeconds;
    private final WebDriverWait        wait;

    private SearchGeneTable      geneTable;
    private SearchPhenotypeTable phenotypeTable;
    private SearchDiseaseTable   diseaseTable;
    private SearchAnatomyTable   anatomyTable;
    private SearchImpcImageTable impcImageTable;
    private SearchImageTable     imageTable;
    private Map<SearchFacetTable.TableComponent, By> map;

    // core names.
    public static final String GENE_CORE_NAME        = "gene";
    public static final String PHENOTYPE_CORE_NAME   = "mp";
    public static final String PHENODIGM_CORE_NAME   = "phenodigm";
    public static final String ANATOMY_CORE_NAME     = "anatomy";
    public static final String IMPC_IMAGES_CORE_NAME = "impc_images";
    public static final String ALLELE2_CORE_NAME     = "allele2";

    // tab ids (core name with 'T" appended)
    public static final String GENE_TAB_ID        = "geneT";
    public static final String PHENOTYPE_TAB_ID   = "mpT";
    public static final String DISEASE_TAB_ID     = "diseaseT";
    public static final String ANATOMY_TAB_ID     = "anatomyT";
    public static final String IMPC_IMAGES_TAB_ID = "impc_imagesT";
    public static final String PRODUCT_TAB_ID     = "allele2T";

    // Tab names as shown on search page.
    public static final String GENE_TAB_NAME        = "Genes";
    public static final String PHENOTYPE_TAB_NAME   = "Phenotypes";
    public static final String DISEASE_TAB_NAME     = "Diseases";
    public static final String ANATOMY_TAB_NAME     = "Anatomy";
    public static final String IMPC_IMAGES_TAB_NAME = "IMPC Images";
    public static final String PRODUCT_TAB_NAME     = "Products";

    // solr qf filter values.
    public static final String GENE_QF_VALUE        = "geneQf";
    public static final String PHENOTYPE_QF_VALUE   = "mixSynQf";
    public static final String DISEASE_QF_VALUE     = "diseaseQf";
    public static final String ANATOMY_QF_VALUE     = "anatomyQf";
    public static final String IMPC_IMAGES_QF_VALUE = "imgQf";
    public static final String PRODUCT_QF_VALUE     = "auto_suggest";

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    // Facets.
//    public enum Facet {
//        GENES(GENE_FACET, GENE_TAB_NAME),
//        PHENOTYPES(PHENOTYPE_FACET, PHENOTYPE_TAB_NAME),
//        DISEASES(DISEASE_FACET, DISEASE_TAB_NAME),
//        ANATOMY(ANATOMY_FACET, ANATOMY_TAB_NAME),
//        IMPC_IMAGES(IMPC_IMAGES_FACET, IMPC_IMAGES_TAB_NAME),
//        PRODUCTS(PRODUCTS_FACET, PRODUCTS_TAB_NAME);
//
//        private String facetName;
//        private String tabName;
//
//        Facet(String facetName, String tabName) {
//            this.facetName = facetName;
//            this.tabName = tabName;
//        }
//        public String getCoreName() { return facetName; }
//        public String getFacetName() {
//            return facetName;
//        }
//        public String getTabId() { return facetName + "T"; }
//        public String getTabName() { return tabName; }
//    }

    private Map<String, Facet> facetsByTabId;       // key = tabId on page


    private void initialiseFacets() {
        facetsByTabId = new HashMap<>();
        facetsByTabId.put(GENE_TAB_ID, new Facet(driver, GENE_CORE_NAME, GENE_TAB_NAME, GENE_TAB_ID));
        facetsByTabId.put(PHENOTYPE_TAB_ID, new Facet(driver, PHENOTYPE_CORE_NAME, PHENOTYPE_TAB_NAME, PHENOTYPE_TAB_ID));
        facetsByTabId.put(DISEASE_TAB_ID, new Facet(driver, PHENODIGM_CORE_NAME, DISEASE_TAB_NAME, DISEASE_TAB_ID));
        facetsByTabId.put(ANATOMY_TAB_ID, new Facet(driver, ANATOMY_CORE_NAME, ANATOMY_TAB_NAME, ANATOMY_TAB_ID));
        facetsByTabId.put(IMPC_IMAGES_TAB_ID, new Facet(driver, IMPC_IMAGES_CORE_NAME, IMPC_IMAGES_TAB_NAME, IMPC_IMAGES_TAB_ID));
        facetsByTabId.put(PRODUCT_TAB_ID, new Facet(driver, ALLELE2_CORE_NAME, PRODUCT_TAB_NAME, PRODUCT_TAB_ID));
    }


    public Map<String, Facet> getFacetsByTabId() {
        return facetsByTabId;
    }

    public FacetFilter getFacetFilter() {
        return new FacetFilter();
    }

    // Used by FacetFilter, Subfacet, and FacetRow internal classes to mark the active li element.
    private final String liXpathBase = "//div[contains(@class,'activeFilter')]//ul/li";

    public class FacetFilter {
        private WebElement facetLiElement;      // Do not access directly. Use getFacetLiElement() to refresh after a post.

        public FacetFilter() {

        }

        public int getCount() {
            String sCount = getFacetLiElement().findElement(By.xpath(liXpathBase + "/span[@class='fcount']")).getText();

            return commonUtils.tryParseInt(sCount);
        }

        public String getName() {
            String name = getFacetLiElement().findElement(By.xpath("./span[@class='flabel']")).getText();

            return name;
        }

        public boolean isOpen() {
            String attr = getFacetLiElement().getAttribute("class");

            return (attr.contains("open"));
        }

        public void close() {
            if (isOpen()) {
                getFacetLiElement().findElement(By.xpath("./span[@class='flabel']")).click();
            }
        }

        public void open() {
            if ( ! isOpen()) {
                getFacetLiElement().findElement(By.xpath("./span[@class='flabel']")).click();
            }
        }

        public int getNumFacetRows() {
            int retVal = 0;

            if (getNumSubfacetRows() == 0) {
                List<WebElement> facetRowElements = getFacetLiElement().findElements(By.xpath("./ul/li[contains(@class, 'fcat')]"));
                retVal = facetRowElements.size();
            }

            return retVal;
        }

        public int getNumSubfacetRows() {
            List<WebElement> facetRowElements = getFacetLiElement().findElements(By.xpath("./ul/li[contains(@class, 'fcatsection')]"));

            return facetRowElements.size();
        }

        public Subfacet getSubfacet(String subfacetText) {
            Subfacet subfacet = null;

            List<WebElement> fcatSectionElements = getFacetLiElement().findElements(By.xpath(liXpathBase + "/ul/li[contains(@class, 'fcatsection')]"));
            for (WebElement fcatSectionElement : fcatSectionElements) {
                String rowText = fcatSectionElement.getText();
                if (rowText.startsWith(subfacetText)) {
                    subfacet = new Subfacet(subfacetText);
                    break;
                }
            }

            return subfacet;
        }

        public FacetRow getFacetRow(String facetRowText) {
            FacetRow facetRow = null;

            List<WebElement> fcatElements = getFacetLiElement().findElements(By.xpath("./ul/li[contains(@class, 'fcat')]"));
            for (WebElement fcatElement : fcatElements) {
                String rowText = fcatElement.getText();
                if (rowText.startsWith(facetRowText)) {
                    facetRow = new FacetRow(facetRowText);
                    break;
                }
            }

            return facetRow;
        }

        private WebElement getFacetLiElement() {

            // Test for stale element.
            try {
                if (facetLiElement.isEnabled()) {
                    // Nothing to do if the element is not stale.
                }
            } catch (StaleElementReferenceException | NullPointerException e) {
                facetLiElement = driver.findElement(By.xpath(liXpathBase));
            }

            return facetLiElement;
        }
    }




    // cssClass = fcatsection
    public class Subfacet {
        private String subfacetText;                // The subfacet facetRowText (e.g. "IMPC Phenotyping Status")
        private WebElement subfacetLiElement;       // Do not access directly. Use getSubfacetLiElement() to refresh after a post.

        public Subfacet(String subfacetText) {
            this.subfacetText = subfacetText;
        }

        public String getName() {
            String name = getSubfacetLiElement().findElement(By.xpath("./span[@class='flabel']")).getText();

            return name;
        }

        public boolean isOpen() {
            String attr = getSubfacetLiElement().getAttribute("class");

            return (attr.contains("open"));
        }

        public void close() {
            if (isOpen()) {
                getSubfacetLiElement().findElement(By.xpath("./span[@class='flabel']")).click();
            }
        }

        public void open() {
            if ( ! isOpen()) {
                getSubfacetLiElement().findElement(By.xpath("./span[@class='flabel']")).click();
            }
        }

        public int getNumFacetRows() {
            List<WebElement> facetRowElements = getSubfacetLiElement().findElements(By.xpath("./ul/li[contains(@class, 'fcat')]"));

            return facetRowElements.size();
        }

        public FacetRow getFacetRow(String facetRowText) {
            FacetRow facetRow = null;

            List<WebElement> fcatElements = getSubfacetLiElement().findElements(By.xpath("./ul/li[contains(@class, 'fcat')]"));
            for (WebElement fcatElement : fcatElements) {
                String rowText = fcatElement.getText();
                if (rowText.startsWith(facetRowText)) {
                    facetRow = new FacetRow(facetRowText);
                    break;
                }
            }

            return facetRow;
        }

        private WebElement getSubfacetLiElement() {

            // Test for stale element.
            try {
                if (subfacetLiElement.isEnabled()) {
                    // Nothing to do if the element is not stale.
                }
            } catch (StaleElementReferenceException | NullPointerException e) {
                List<WebElement> fcatsectionElements = driver.findElements(By.xpath(liXpathBase + "/ul/li[contains(@class, 'fcatsection')]"));
                for (WebElement fcatsectionElement : fcatsectionElements) {
                    String rowText = fcatsectionElement.getText();
                    if (rowText.startsWith(subfacetText)) {
                        subfacetLiElement = fcatsectionElement;
                        break;
                    }
                }
            }

            return subfacetLiElement;
        }
    }




    // cssClass = fcat
    public class FacetRow {
        private String facetRowText;                // The facet row text (e.g. "Approved")
        private WebElement facetRowLiElement;       // Do not access directly. Use getFacetRowLiElement() to refresh after a post.

        public FacetRow(String facetRowText) {
            this.facetRowText = facetRowText;
        }

        public int getCount() {
            String sCount = getFacetRowLiElement().findElement(By.xpath("./span[@class='fcount']")).getText();

            return commonUtils.tryParseInt(sCount);
        }

        public String getName() {
            String name = getFacetRowLiElement().findElement(By.xpath("./span[contains(@class, 'flabel')]")).getText();

            return name;
        }

        public boolean isChecked() {
            String sIsChecked = getFacetRowLiElement().findElement(By.xpath("./input")).getAttribute("checked");

            return ((sIsChecked != null) && (sIsChecked.equals("true")));
//            return getFacetRowLiElement().findElement(By.xpath("./input")).getAttribute("checked").equals("true");
//            List<WebElement> elements = getFacetRowLiElement().findElements(By.xpath("./span[contains(@class, 'filterCheck')]"));

//            return elements.size() > 0;
        }

        public boolean isEnabled() {
            return getFacetRowLiElement().findElement(By.xpath("./input")).isEnabled();
        }

        public void check() {
            if ((isEnabled()) && ( ! isChecked())) {
                getFacetRowLiElement().findElement(By.xpath("./span[contains(@class,'flabel')]")).click();
            }
        }

        public void uncheck() {
            if ((isEnabled()) && (isChecked())) {
                getFacetRowLiElement().findElement(By.xpath("./span[contains(@class,'flabel')]")).click();
            }
        }

        private WebElement getFacetRowLiElement() {

            // Test for stale element.
            try {
                if (facetRowLiElement.isEnabled()) {
                    // Nothing to do if the element is not stale.
                }
            } catch (StaleElementReferenceException | NullPointerException e) {
                List<WebElement> fcatElements = driver.findElements(By.xpath(liXpathBase + "/ul/li[ not (contains(@class, 'fcatsection')) and contains(@class, 'fcat')]"));
                for (WebElement fcatElement : fcatElements) {
                    String rowText = fcatElement.getText();
                    if (rowText.startsWith(facetRowText)) {
                        facetRowLiElement = fcatElement;
                        break;
                    }
                }

            }

            return facetRowLiElement;
        }
    }

    // Page directives (i.e. pagination buttons)
    public enum PageDirective {
        PREVIOUS,
        FIRST_NUMBERED,
        SECOND_NUMBERED,
        THIRD_NUMBERED,
        FOURTH_NUMBERED,
        FIFTH_NUMBERED,
        ELLIPSIS,
        LAST,
        NEXT
    }

    /**
     * Creates a new <code>SearchPage</code> instance. No web page is loaded.
     * @param driver Web driver
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     * @param phenotypePipelineDAO
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param map a map of HTML table-related definitions, keyed by <code>
     * TableComponent</code>.
     */
    public SearchPage(WebDriver driver, int timeoutInSeconds, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl, Map<SearchFacetTable.TableComponent, By> map) throws TestException {
        this(driver, timeoutInSeconds, null, phenotypePipelineDAO, baseUrl, map);
        this.target = driver.getCurrentUrl();
        this.map = map;
    }

    /**
     * Creates a new <code>SearchPage</code> instance attempting to load the
     * search web page at <code>target</code>.
     * @param driver Web driver
     * @param timeoutInSeconds The <code>WebDriver</code> timeout, in seconds
     * @param target target search URL
     * @param phenotypePipelineDAO
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param map a map of HTML table-related definitions, keyed by <code>
     * TableComponent</code>.
     *
     * @throws TestException If the target cannot be set
     */
    public SearchPage(WebDriver driver, int timeoutInSeconds, String target, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl, Map<SearchFacetTable.TableComponent, By> map) throws TestException {
        this.driver = driver;
        this.timeoutInSeconds = timeoutInSeconds;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.baseUrl = baseUrl;
        this.map = map;
        wait = new WebDriverWait(driver, timeoutInSeconds);

        if ((target != null) && ( ! target.isEmpty())) {
            try {
                driver.get(target);
            } catch (Exception e) {
                throw new TestException("EXCEPTION: " + e.getLocalizedMessage() + "\ntarget: '" + target + "'");
            }
            this.target = target;
        }

        // Initialise facets map.
        initialiseFacets();
    }

//    /**
//     * Return a <code>HashMap&lt;Facet, FacetFilter&gt;</code> containing the
//     * facet and related filter subfacetText. The HashMap is empty if there are no filters.
//     *
//     * @return A <code>HashMap&lt;Facet, FacetFilter&gt;</code> containing the
//     * facet and related filter subfacetText. The HashMap is empty if there are no filters.
//     */
//    public HashMap<Facet, FacetFilter> getFacetFilter() {
//        HashMap<Facet, FacetFilter> results = new HashMap();
//
//        // If there are no filters, div.ffilter's style property will be 'display: none;'.
//        WebElement ffilterElement = driver.findElement(By.xpath("//div[@class='ffilter']"));
//        if (hasFilters()) {
//            List<WebElement> liElements = ffilterElement.findElements(By.cssSelector("ul#facetFilter > li"));
//            for (WebElement liElement : liElements) {
//                FacetFilter facetFilter;
//                String facetName = liElement.findElement(By.cssSelector("span")).getText();
//                switch (facetName) {
//                    case "Gene":
//                        facetFilter = new FacetFilter(Facet.GENES);
//                        break;
//
//                    case "Phenotype":
//                        facetFilter = new FacetFilter(Facet.PHENOTYPES);
//                        break;
//
//                    case "Disease":
//                        facetFilter = new FacetFilter(Facet.DISEASES);
//                        break;
//
//                    case "Anatomy":
//                        facetFilter = new FacetFilter(Facet.ANATOMY);
//                        break;
//
//                    case "IMPC Images":
//                        facetFilter = new FacetFilter(Facet.IMPC_IMAGES);
//                        break;
//
//                    case "Images":
//                        facetFilter = new FacetFilter(Facet.IMAGES);
//                        break;
//
//                    default:
//                        continue;
//                }
//
//                List<WebElement> ulElements = liElement.findElements(By.cssSelector("ul"));
//                for (WebElement ulElement : ulElements) {
//                    facetFilter.subfacetTexts.add(ulElement.findElement(By.cssSelector("li a")).getText());
//                }
//
//                results.put(facetFilter.facet, facetFilter);
//            }
//        }
//
//        return results;
//    }

//    public void clearFilters() {
//        if (hasFilters()) {
//            driver.findElement(By.xpath("//span[@id='rmFilters']")).click();
//        }
//    }

//    /**
//     * Returns true if there are any filters; false otherwise.
//     * @return true if there are any filters; false otherwise.
//     */
//    public boolean hasFilters() {
//        // If there are no filters, div.filter's style property will be 'display: none;'.
//        WebElement ffilterElement = driver.findElement(By.xpath("//div[@class='ffilter']"));
//        return ! (ffilterElement.getAttribute("style").contains("display: none"));
//    }

    public void clickDownloadButton(DownloadType downloadType) {
        String className = "";

        switch (downloadType) {
            case TSV: className = "tsv_grid"; break;

            case XLS: className = "xls_grid"; break;
        }

        driver.findElement(By.xpath("//button[contains(@class, '" + className + "')]")).click();
    }

//    /**
//     * Clicks the facet and returns the result count. This has the side effect of
//     * waiting for the page to finish loading.
//     *
//     * @param facet desired facet to click
//     * @return the [total] results count
//     */
//    public int clickFacet(Facet facet) throws TestException {
//        return clickFacetById(getFacetId(facet));
//    }

    /**
     * Clicks the facet and returns the result count. This has the side effect of
     * waiting for the page to finish loading.
     *
     * @param facetId HTML 'li' id of desired facet to click
     * @return the [total] results count
     */
    public int clickFacetById(String facetId) throws TestException {
        // Clicking the li element opens the facet but does not close it. Click on the subfacetText in the span instead.
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@id='" + facetId + "']//span[@class='flabel']")));
        testUtils.scrollToTop(driver, element, -50);           // Scroll element into view.
        element.click();

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[contains(@class, 'dataTable')]")));            // Wait for facet to load.
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@class, 'dataTables_paginate')]")));    // Wait for page buttons to load.
            setFacetTable();

        } catch (Exception e) {
            System.out.println("SearchPage.clickFacetById: Exception: " + e.getLocalizedMessage() + "\nURL: " + driver.getCurrentUrl());
            e.printStackTrace();
            throw new TestException(e);
        }

        return getTabResultCountFooter();
    }

    /**
     * Selects a valid random page number within the range of enabled page
     * buttons, then clicks selected page button. Disabled buttons and the
     * ellipsis are not clickable and are re-mapped to a clickable button.
     * Calling this method has the side effect of waiting for the page to finish
     * loading.
     *
     * @throws TestException if no such button exists
     * @return the <code>PageDirective</code> of the clicked button
     */
    public PageDirective clickPageButton() throws TestException {
        PageDirective pageDirective = null;
        try {
            int max = getNumPageButtons();
            int randomPageNumber = random.nextInt(max);
            WebElement element = getButton(randomPageNumber);

            if (element.getAttribute("class").contains("disabled")) {
                if (randomPageNumber == 0) {
                    logger.debug("Changing randomPageNumber from 0 to 1.");
                    randomPageNumber++;
                } else {
                    logger.debug("Changing randomPageNumber from " + randomPageNumber + " to " + (randomPageNumber - 1) + ".");
                    randomPageNumber--;
                }
            } else if (element.getText().contains("...")) {
                logger.debug("Changing randomPageNumber from " + randomPageNumber + " to " + (randomPageNumber - 1) + ".");
                randomPageNumber--;
            }

            pageDirective = getPageDirective(randomPageNumber);

            logger.debug("SearchPage.clickPageButton(): max = " + max + ". randomPageNumber = " + randomPageNumber + ". Clicking " + pageDirective + " button.");

        } catch (Exception e) {
            throw new TestException("EXCEPTION in SearchPage.clickPageButton: " + e.getLocalizedMessage(), e);
        }

        setFacetTable();

        getTabResultCountFooter();        // Insure page has finished loading.

        return pageDirective;
    }

    /**
     * Clicks the given page button. This has the side effect of waiting for
     * the page to finish loading. <b>Note:</b> The pageButton may be disabled
     * or it may be the ellipsis, in which case the form simply won't change.
     * @param pageButton the page button to click
     * @throws TestException if no such button exists
     */
    public void clickPageButton(PageDirective pageButton) throws TestException {
        List<WebElement> ulElements = driver.findElements(By.xpath("//div[contains(@class, 'dataTables_paginate')]/ul/li"));
        WebElement pageButtonElement = null;
        try {
            switch (pageButton) {
                case PREVIOUS:          pageButtonElement = ulElements.get(0);      break;

                case FIRST_NUMBERED:    pageButtonElement = ulElements.get(1);      break;

                case SECOND_NUMBERED:   pageButtonElement = ulElements.get(2);      break;

                case THIRD_NUMBERED:    pageButtonElement = ulElements.get(3);      break;

                case FOURTH_NUMBERED:   pageButtonElement = ulElements.get(4);      break;

                case FIFTH_NUMBERED:    pageButtonElement = ulElements.get(5);      break;

                case ELLIPSIS:                                          break;

                case LAST:              pageButtonElement = ulElements.get(7);      break;

                case NEXT:
                    // See javadoc for getPageDirective() below for mapping of 'Next' button.
                    switch (getNumPageButtons()) {
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                            pageButtonElement = ulElements.get(getNumPageButtons() - 1);    break;
                        case 9:
                            pageButtonElement = ulElements.get(8);                          break;
                    }
                    break;
            }

            // Scroll the page buttons into view before clicking it.
            testUtils.scrollToTop(driver, pageButtonElement, -50);
            pageButtonElement.click();

            // After a new page has been selected, we must update the old, stale image/impc_image table to fetch the new page's data.
            if (hasImageTable())
                getImageTable().updateImageTableAfterChange();
            if (hasImpcImageTable())
                getImpcImageTable().updateImageTableAfterChange();
            setFacetTable();
            getTabResultCountFooter();                                                    // Allow page to finish loading.

        } catch (Exception e) {
            logger.error("SearchPage.clickPageButton exception: " + e.getLocalizedMessage() + "\nURL: " + driver.getCurrentUrl());
            throw e;
        }
    }

    /**
     * Click the specified facet tab
     *
     * @param facet the facet tab to click
     *
     * @return the facet's count
     */
    public int clickTab(Facet facet) {

        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@id='" + facet.getTabId() + "']/a")));
        element.click();

        // Wait until "Showing x to y of z entries" at footer.
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='dTable_info']")));

        return getTabResultCountFooter(facet);
    }

    /**
     * @return Returns the anatomy table [maGrid], if there is one; or an
     * empty one if there is not
     */
    public SearchAnatomyTable getAnatomyTable() throws TestException {
        if (hasAnatomyTable()) {
            if (anatomyTable == null) {
                anatomyTable = new SearchAnatomyTable(driver, timeoutInSeconds);
            }
        }

        return anatomyTable;
    }

    /**
     * Fetches the autosuggest components matching <code>searchString</code>
     *
     * @param searchString The search string
     *
     * @return the autosuggest components matching <code>searchString</code>
     *
     * @throws TestException
     */
    public List<String> getAutosuggest(String searchString) throws TestException {
        List<String> results = new ArrayList();

        if ((searchString == null) || (searchString.trim().isEmpty()))
            return results;

        submitSearch(searchString);
        List<WebElement> autosuggestBlockList;
        commonUtils.sleep(2000);              // Sleep for 2 seconds to let autosuggest complete.
        autosuggestBlockList = driver.findElements(By.cssSelector("ul#ui-id-1"));

        if ( ! autosuggestBlockList.isEmpty()) {
            List<WebElement> autosuggestElements = autosuggestBlockList.get(0).findElements(By.cssSelector("li"));
            for (WebElement autosuggestElement : autosuggestElements) {
                results.add(autosuggestElement.getText());
            }
        }

        return results;
    }

    /**
     *
     * @return The base url
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * Returns the <code>WebElement</code> button matching the given buttonIndex.
     * @param buttonIndex 0-relative button index
     * @return The <code>WebElement</code> matching <code>buttonIndex</code>
     * @throws IndexOutOfBoundsException if <code>buttonIndex</code> lies outside
     * the bounds of the range of page buttons
     */
    public WebElement getButton(int buttonIndex) throws IndexOutOfBoundsException {
        List<WebElement> ulElements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class, 'dataTables_paginate')]/ul/li")));
        if (buttonIndex < ulElements.size()) {
            return ulElements.get(buttonIndex);
        } else {
            throw new IndexOutOfBoundsException("SearchPage.getPage(int pageIndex): pageIndex: " + buttonIndex + ". # elements: " + ulElements.size());
        }
    }

    /**
     * @return Returns the disease table [diseaseGrid], if there is one; or an
     * empty one if there is not
     */
    public SearchDiseaseTable getDiseaseTable() throws TestException {
        if (hasDiseaseTable()) {
            if (diseaseTable == null) {
                diseaseTable = new SearchDiseaseTable(driver, timeoutInSeconds);
            }
        }

        return diseaseTable;
    }

//    /**
//     *
//     * @param facet desired facet
//     *
//     */
//    public Integer getFacetCount(Facet facet) {
//
//        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@id='" + getFacetId(facet) + "']/span[contains(@class, 'fcount')]")));
//
//        return commonUtils.tryParseInt(element.getText());
//    }

//    public Integer getFacetCount(String coreName) {
//        return getFacetCount(getFacetByCoreName(coreName));
//    }
//
//    /**
//     * Returns the <code>Facet</code> matching <code>coreName</code>.
//     * @param coreName The core name, as a string
//     * @return the <code>Facet</code> matching <code>coreName</code>
//     * @throws RuntimeException if <code>coreName</code> doesn't map to a facet.
//     */
//    public Facet getFacetByCoreName(String coreName) throws RuntimeException {
//        switch (coreName) {
//            case "gene":
//                return Facet.GENES;
//
//            case "mp":
//                return Facet.PHENOTYPES;
//
//            case "disease":
//                return Facet.DISEASES;
//
//            case "ma":
//                return Facet.ANATOMY;
//
//            case "impc_images":
//                return Facet.IMPC_IMAGES;
//        }
//
//        throw new RuntimeException("No matching facet for coreName'" + coreName + "'.");
//    }
//
//    /**
//     * Returns the <code>Facet</code> matching <code>tabId</code>, or null if unknown tabId.
//     * @param tabId The HTML tab id, as a string
//     * @return the <code>Facet</code> matching <code>tabId</code>, or null if unknown tabId.
//     */
//    public Facet getFacetByTabId(String tabId) {
//        switch (tabId) {
//            case "geneT":
//                return Facet.GENES;
//
//            case "mpT":
//                return Facet.PHENOTYPES;
//
//            case "diseaseT":
//                return Facet.DISEASES;
//
//            case "maT":
//                return Facet.ANATOMY;
//
//            case "impc_imagesT":
//                return Facet.IMPC_IMAGES;
//
//            default:
//                System.out.println("No matching facet for tabName'" + tabId + "'.");
//                return null;
//        }
//    }
//
//    /**
//     * Given a <code>Facet</code> instance, returns the HTML id of the li element.
//     * @param facet
//     * @return
//     */
//    public String getFacetId(Facet facet) {
//        String id = "";
//
//        switch (facet) {
//            case GENES:
//                id = "gene";
//                break;
//
//            case PHENOTYPES:
//                id = "mp";
//                break;
//
//            case DISEASES:
//                id = "disease";
//                break;
//
//            case ANATOMY:
//                id = "ma";
//                break;
//
//            case IMPC_IMAGES:
//                id = "impc_images";
//                break;
//        }
//
//        return id;
//    }
//
//    /**
//     * Returns an array of facet names.
//     *
//     * @param facet the facet whose names are to be returned
//     *
//     * @return an array of facet names
//     *
//     * @throws TestException
//     */
//    public String[] getFacetNames(Facet facet) throws TestException {
//        ArrayList<String> names = new ArrayList();
//        String xpath = "";
//
//        switch (facet) {
//            case GENES:
//                throw new TestException("Not implemented yet.");
//
//            case PHENOTYPES:
//                xpath = "//*[@id='mp']//li";
//                break;
//
//            case DISEASES:
//                throw new TestException("Not implemented yet.");
//
//            case ANATOMY:
//                xpath = "//*[@id='ma']//li";
//                break;
//// FIXME FIXME FIXME
//            case IMPC_IMAGES:
//                throw new TestException("Not implemented yet.");
//        }
//
//        List<WebElement> elements = driver.findElements(By.xpath(xpath));
//        if ( ! elements.isEmpty()) {
//            for (WebElement element: elements) {
//                names.add(element.getText());
//            }
//        }
//
//        return names.toArray(new String[0]);
//    }

    /**
     * @return Returns the GENES table [geneGrid], if there is one; or an
    empty one if there is not
     */
    public SearchGeneTable getGeneTable() throws TestException {
        if (hasGeneTable()) {
            if (geneTable == null) {
                geneTable = new SearchGeneTable(driver, timeoutInSeconds);
            }
        }

        return geneTable;
    }

    /**
     * @return Returns the impc_images table [impc_imagesGrid], if there is one;
     * or an empty one if there is not
     */
    public SearchImpcImageTable getImpcImageTable() throws TestException {
        if (hasImpcImageTable()) {
            if (impcImageTable == null) {
                impcImageTable = new SearchImpcImageTable(driver, timeoutInSeconds);
            }
        }

        return impcImageTable;
    }

    /**
     * @return Returns the images table [imagesGrid], if there is one; or an
     * empty one if there is not
     */
    public SearchImageTable getImageTable() throws TestException {
        if (hasImageTable()) {
            if (imageTable == null) {
                imageTable = new SearchImageTable(driver, timeoutInSeconds);
            }
        }

        return imageTable;
    }

    /**
     *
     * @return The number of pagination buttons displayed. e.g.:
     * <ul><li>3 for 'previous', '1', 'next'</li>
     * <li>3 for 'previous', '1', '2','next'</li>
     * <li>4 for 'previous', '1', '2', '3', 'next'</li>
     * <li>9 for 'previous', '1', '2', '3', '4', '5', '...', '4852', 'next'</li></ul>
     */
    public int getNumPageButtons() {
        return driver.findElements(By.xpath("//div[contains(@class, 'dataTables_paginate')]/ul/li")).size();
    }

    /**
     * Return the matching <code>PageDirective</code>
     *
     * @param buttonIndex 0-relative button index
     * @return the matching <code>PageDirective</code>
     * @throws IndexOutOfBoundsException if <code>buttonIndex</code> lies outside
     * the bounds of the range of page buttons
     *
     * Depending on the number of results, the button array can look like any
     * of the following:
     * <ul>
     * <li>'previous'  1  'next'                         (e.g. search for akt2)</li>
     * <li>'previous'  1  2  'next'                      (e.g. search for head)</li>
     * <li>'previous'  1  2  3  'next'                   (e.g. search for tail)</li>
     * <li>'previous'  1  2  3  4  'next'                (e.g. search for leg, click on Diseases facet)</li>
     * <li>'previous'  1  2  3  4  5  'next'             (e.g. search for bladder)</li>
     * <li>'previous'  1  2  3  4  5  ...  4356  'next'  (e.g. no search criteria)</li>
     * </ul>
     */
    public PageDirective getPageDirective(int buttonIndex) throws IndexOutOfBoundsException {
        switch (getNumPageButtons()) {
            case 3:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.NEXT;
                }
            case 4:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.SECOND_NUMBERED;
                    case 3:     return PageDirective.NEXT;
                }

            case 5:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.SECOND_NUMBERED;
                    case 3:     return PageDirective.THIRD_NUMBERED;
                    case 4:     return PageDirective.NEXT;
                }

            case 6:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.SECOND_NUMBERED;
                    case 3:     return PageDirective.THIRD_NUMBERED;
                    case 4:     return PageDirective.FOURTH_NUMBERED;
                    case 5:     return PageDirective.NEXT;
                }

            case 7:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.SECOND_NUMBERED;
                    case 3:     return PageDirective.THIRD_NUMBERED;
                    case 4:     return PageDirective.FOURTH_NUMBERED;
                    case 5:     return PageDirective.FIFTH_NUMBERED;
                    case 6:     return PageDirective.NEXT;
                }

            case 9:
                switch (buttonIndex) {
                    case 0:     return PageDirective.PREVIOUS;
                    case 1:     return PageDirective.FIRST_NUMBERED;
                    case 2:     return PageDirective.SECOND_NUMBERED;
                    case 3:     return PageDirective.THIRD_NUMBERED;
                    case 4:     return PageDirective.FOURTH_NUMBERED;
                    case 5:     return PageDirective.FIFTH_NUMBERED;
                    case 6:     return PageDirective.ELLIPSIS;
                    case 7:     return PageDirective.LAST;
                    case 8:     return PageDirective.NEXT;
                }
        }

        throw new IndexOutOfBoundsException("SearchPage.getPageDirective: buttonIndex = " + buttonIndex + ". # buttons: " + getNumPageButtons());
    }

    /**
     * @return Returns the phenotype table [mpGrid], if there is one; or an
     * empty one if there is not
     */
    public SearchPhenotypeTable getPhenotypeTable() throws TestException {
        if (hasPhenotypeTable()) {
            if (phenotypeTable == null) {
                phenotypeTable = new SearchPhenotypeTable(driver, timeoutInSeconds);
            }
        }

        return phenotypeTable;
    }

    /**
     * Returns the tab count in the footer at the bottom of the currently selected facet tab (HTML id 'dTable')
     *
     * @return the tab count in the footer at the bottom of the currently selected facet tab (HTML id 'dTable')
     *
     * @throws TestException
     */
    public int getTabResultCountFooter() throws TestException {
        return getTabResultCountFooter(getSelectedTab());
    }

    /**
     * Returns the tab count in the footer at the bottom of the specified facet tab (HTML id 'dTable')
     *
     * @param facet the facet tab for which the count is desired
     *
     * @return the tab count in the footer at the bottom of the specified facet tab (HTML id 'dTable')
     */
    public int getTabResultCountFooter(Facet facet) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated((By.xpath("//div[@id='dTable_info']"))));

        String[] showingParts = element.getText().split(" ");       // Typical string: "Showing 1 to 10 of 23432 entries"

        return commonUtils.tryParseInt(showingParts[5].trim());
    }

    /**
     * Returns the tab count in the header at the top of the specified facet tab (HTML id 'dTable')
     *
     * @param facet the facet tab for which the count is desired
     *
     * @return the tab count in the header at the top of the specified facet tab (HTML id 'dTable')
     * <i>NOTE: Only the images and impce_images tabs have counts in the header.</i>
     */
    public int getTabResultCountHeader(Facet facet) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated((By.xpath("//span[@id='resultCount']/a"))));

        String sResultCount = element.getText();       // Typical string: "Showing 1 to 10 of 23432 entries"

        return commonUtils.tryParseInt(sResultCount.trim());
    }

    /**
     * Returns the selected tab as a <code>Facet</code>
     *
     * @return the selected tab as a <code>Facet</code>
     *
     * @throws TestException
     */
    public Facet getSelectedTab(){
        String tabId = getSelectedTabElement().getAttribute("id");

        return facetsByTabId.get(tabId);
    }

    /**
     * Returns the selected tab <code>WebElement</code>
     *
     * @return the selected tab <code>WebElement</code>
     */
    public WebElement getSelectedTabElement() {
        String xpathTabName = "//div[@id='tabs']//li[@class='currDataType']";

         return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpathTabName)));
    }

    /**
     *
     * @return A <code>Showing</code> instance with the interesting inteter
     * parts of the <i>Showing</i> page results string.
     */
    public Showing getShowing() {
        return new Showing();
    }

//    /**
//     *
//     * @return The timeout, in seconds
//     */
//    public long getTimeoutInSeconds() {
//        return timeoutInSeconds;
//    }
//
//    public WindowState getToolboxState() {
//        String style = driver.findElement(By.xpath("//div[@id='toolBox']")).getAttribute("style");
//        return (style.contains("block;") ? WindowState.OPEN : WindowState.CLOSED);
//    }

    /**
     *
     * @return true if this search page has a maGrid HTML table; false
     * otherwise
     */
    public boolean hasAnatomyTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='maGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @return true if this search page has a diseaseGrid HTML table; false
     * otherwise
     */
    public boolean hasDiseaseTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='diseaseGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @return true if this search page has a geneGrid HTML table; false
     * otherwise
     */
    public boolean hasGeneTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='geneGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @return true if this search page has a impc_imagesGrid HTML table; false
     * otherwise
     */
    public boolean hasImpcImageTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='impc_imagesGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @return true if this search page has a imagesGrid HTML table; false
     * otherwise
     */
    public boolean hasImageTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='imagesGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *
     * @return true if this search page has a mpGrid HTML table; false
     * otherwise
     */
    public boolean hasPhenotypeTable() {
        try {
            List<WebElement> elements = driver.findElements(By.xpath("//table[@id='mpGrid']"));
            return (elements.size() > 0);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sets the facet table based on the current-showing facet.
     */
    public void setFacetTable() throws TestException {
        if (hasAnatomyTable()) {
            anatomyTable = new SearchAnatomyTable(driver, timeoutInSeconds);
        } else if (hasDiseaseTable()) {
            diseaseTable = new SearchDiseaseTable(driver, timeoutInSeconds);
        } else if (hasGeneTable()) {
            geneTable = new SearchGeneTable(driver, timeoutInSeconds);
        } else if (hasImageTable()) {
            imageTable = new SearchImageTable(driver, timeoutInSeconds);
        } else if (hasImpcImageTable()) {
            impcImageTable = new SearchImpcImageTable(driver, timeoutInSeconds);
        } else if (hasPhenotypeTable()) {
            phenotypeTable = new SearchPhenotypeTable(driver, timeoutInSeconds);
        }
    }

    public void setImageFacetView(SearchFacetTable.ImagesView desiredView) throws TestException {
        getImageTable().setCurrentView(desiredView);
    }

    /**
     * Submits the string in <code>searchString</code> to the server. If <code>
     * searchString</code> is terminated with a newline, the page will submit
     * the request and wait for the page to finish loading.
     *
     * @param searchString The keystrokes to be sent to the server
     * @return the result count. (<i>don't know if result count is returned if <code>
     * searchString</code> is not terminated by a newline</i>)
     *
     * @throws TestException
     */
    public int submitSearch(String searchString) throws TestException {
        WebElement weInput = driver.findElement(By.cssSelector("input#s"));
        weInput.clear();
        testUtils.seleniumSendKeysHack(weInput, searchString);

        try {
            WebElement resultMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'mpi2-search')]")));

            if (resultMsg.getText().contains("returned no entry"))
                return 0;
            else
                return getTabResultCountFooter();

        } catch (Exception e) {

            return 0;
        }
    }

    /**
     * Compares each facet's grid (on the right-hand side of the search page)
     * with each of the download data streams (page/all and tsv/xls). Any
     * errors are returned in the <code>RunStatus</code> instance.

//     * @param facet facet
//     * @return page status instance
//     */
//    public RunStatus validateDownload(Facet facet) {
//        RunStatus status = new RunStatus();
//
//        DownloadType[] downloadTypes = {
//              DownloadType.TSV
//            , DownloadType.XLS
//        };
//
//        String[][] downloadData = new String[0][0];
//        // Validate the download types for this facet.
//        for (DownloadType downloadType : downloadTypes) {
//            SearchFacetTable table = null;
//            try {
//                downloadData = getDownload(downloadType, baseUrl);                  // Get the data for this download type.
//                table = getFacetTable(facet);                                       // Get the facet table.
//            } catch (Exception e) {
//                String message = "Error getting download data for " + downloadType + " from URL: " + baseUrl;
//                status.addError(message);
//                System.out.println(message);
//            }
//
//            if (table != null) {
//                status.add(table.validateDownload(downloadData, downloadType)); // Validate it.
//            }
//        }
//
//        return status;
//    }

    /**
     * Returns the production status order button elements (e.g. 'ES Cells',
     * 'Mice tm1.1', 'Mice tm1', etc.)
     *
     * @param geneTrElement a valid gene element, derived from the selected tr
     * row under the geneGrid table (a tr element pointing to the desired gene
     * row)
     *
     * @return the production status order button elements (e.g. 'ES Cells',
     * 'Mice tm1.1', 'Mice tm1', etc.)
     */
    public List<WebElement> getProductionStatusOrderButtons(WebElement geneTrElement) {
        List<WebElement> retVal = new ArrayList();
        WebDriverWait wait = new WebDriverWait(driver, timeoutInSeconds);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@oldtitle]")));

        try {
            List<WebElement> elements = geneTrElement.findElements(By.xpath(".//*[@oldtitle]"));

            for (WebElement element : elements) {
                if (element.getTagName().equals("a"))
                    retVal.add(element);
            }
        } catch (Exception e) { }

        return retVal;
    }

//    /**
//     * Return the number of entries currently showing in the 'entries' drop-down
//     * box.
//     *
//     * @return the number of entries currently showing in the 'entries'
//     * drop-down box.
//     */
//    public int getNumEntries() throws TestException {
//        int numEntries;
//        if (hasAnatomyTable()) {
//            numEntries = getAnatomyTable().getNumEntries();
//            logger.info("AnatomyTable has " + numEntries + " entries.");
//            return getAnatomyTable().getNumEntries();
//        } else if (hasDiseaseTable()) {
//            numEntries = getDiseaseTable().getNumEntries();
//            logger.info("DiseaseTable has " + numEntries + " entries.");
//            return getDiseaseTable().getNumEntries();
//        } else if (hasGeneTable()) {
//            numEntries = getGeneTable().getNumEntries();
//            logger.info("GeneTable has " + numEntries + " entries.");
//            return getGeneTable().getNumEntries();
//        } else if (hasImageTable()) {
//            numEntries = getImageTable().getNumEntries();
//            logger.info("ImageTable has " + numEntries + " entries.");
//            return getImageTable().getNumEntries();
//        } else if (hasImpcImageTable()) {
//            numEntries = getImpcImageTable().getNumEntries();
//            logger.info("ImpcImageTable has " + numEntries + " entries.");
//            return getImpcImageTable().getNumEntries();
//        } else if (hasPhenotypeTable()) {
//            numEntries = getPhenotypeTable().getNumEntries();
//            logger.info("PhenotypeTable has " + numEntries + " entries.");
//            return getPhenotypeTable().getNumEntries();
//        } else {
//            throw new RuntimeException("No facet table found.");
//        }
//    }
//    /**
//     * Set the number of entries in the 'entries' drop-down box.
//     *
//     * @param entriesSelect The new value for the number of entries to show.
//     */
//    public void setNumEntries(SearchFacetTable.EntriesSelect entriesSelect) throws TestException {
//        // Currently (14-Aug-2015), the search page doesn't have a widget for setting the number of entries. Code is left in should this functionality be reimplemented.
//        if (1 == 1) return;
//
//
//
//        if (hasAnatomyTable()) {
//            logger.info("Setting AnatomyTable entries to " + entriesSelect.getValue() + ".");
//            getAnatomyTable().setNumEntries(entriesSelect);
//            anatomyTable = getAnatomyTable();
//        } else if (hasDiseaseTable()) {
//            logger.info("Setting DiseaseTable entries to " + entriesSelect.getValue() + ".");
//            getDiseaseTable().setNumEntries(entriesSelect);
//            diseaseTable = getDiseaseTable();
//        } else if (hasGeneTable()) {
//            logger.info("Setting GeneTable entries to " + entriesSelect.getValue() + ".");
//            getGeneTable().setNumEntries(entriesSelect);
//            geneTable = getGeneTable();
//        } else if (hasImageTable()) {
//            logger.info("Setting ImageTable entries to " + entriesSelect.getValue() + ".");
//            getImageTable().setNumEntries(entriesSelect);
//            imageTable = getImageTable();
//        } else if (hasImpcImageTable()) {
//            logger.info("Setting ImpcImageTable entries to " + entriesSelect.getValue() + ".");
//            getImpcImageTable().setNumEntries(entriesSelect);
//            impcImageTable = getImpcImageTable();
//        } else if (hasPhenotypeTable()) {
//            logger.info("Setting PhenotypeTable entries to " + entriesSelect.getValue() + ".");
//            getPhenotypeTable().setNumEntries(entriesSelect);
//            phenotypeTable = getPhenotypeTable();
//        }
//    }


    // PUBLIC CLASSES


    public class Showing {
        public final int first;
        public final int last;
        public final int total;
        public final String text;
        private final WebElement element;

        public Showing(){
            wait.until(ExpectedConditions.elementToBeClickable(getButton(1)));
            element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(@id, 'Grid_info')]")));
            text = element.getText();
            String[] showing = text.split(" ");
            first = commonUtils.tryParseInt(showing[1]);
            last = commonUtils.tryParseInt(showing[3]);
            total = commonUtils.tryParseInt(showing[5]);
        }

        @Override
        public String toString() {
            return text;
        }
    }


    // PRIVATE METHODS


    /**
     * Get the full data store matching the download type
     *
     * @param downloadType The download button type (e.g. page/all, tsv/xls)
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @return the full TSV data store
     * @throws TestException
     */
    private String[][] getDownload(DownloadType downloadType, String baseUrl) throws TestException {
        String[][] data = new String[0][0];

        try {

            URL downloadUrl = new URL(getDownloadUrl(downloadType));

            switch (downloadType) {
                case TSV:
                    // Get the download stream and statistics for the TSV stream.
                    DataReaderTsv dataReaderTsv = new DataReaderTsv(downloadUrl);
                    data = dataReaderTsv.getData();
                    break;

                case XLS:
                    // Get the download stream and statistics for the XLS stream.
                    DataReaderXls dataReaderXls = new DataReaderXls(downloadUrl);
                    data = dataReaderXls.getData();
                    break;
            }
        } catch (NoSuchElementException | TimeoutException te) {
            throw new RuntimeException("SearchPage.getDownload: Expected page for target: " + target + ".");
        } catch (IllegalArgumentException iae) {
            // This is thrown when the GENE download stream is large (e.g. 48k rows) on an unfiltered gene list (ALL_XLS).
            logger.error("EXCEPTION: SearchPage.getDownload(): " + iae.getLocalizedMessage());
        } catch (Exception e) {
            String message = "EXCEPTION: SearchPage.getDownload: processing target URL " + target + ": " + e.getLocalizedMessage();
            logger.error(message);
            e.printStackTrace();
            throw new TestException(e);
        }

        return data;
    }

    /**
     * Return the download url base based on download type
     * @param downloadType The download button type (e.g. page/all, tsv/xls)
     * @return the download url base embedded in the <i>downloadType</i> button.
     */
    private String getDownloadUrl(DownloadType downloadType) {


        final String xpath = "//a[@id='" + downloadType.getName() + "A']";

        WebElement downloadLinkElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));

        String attr = downloadLinkElement.getAttribute("href");

        return attr;
    }

//    /**
//     * Given a facet, returns the matching generic <code>SearchFacetTable</code>.
//     * @param facet facet
//     * @return The matching generic <code>SearchFacetTable</code>.
//     */
//    private SearchFacetTable getFacetTable(Facet facet) throws TestException {
//        switch (facet) {
//            case ANATOMY:       return getAnatomyTable();
//            case DISEASES:      return getDiseaseTable();
//            case GENES:         return getGeneTable();
//            case IMPC_IMAGES:   return getImpcImageTable();
//            case PHENOTYPES:    return getPhenotypeTable();
//        }
//
//        throw new RuntimeException("SearchPage.getFacetTable(): Invalid facet " + facet + ".");
//    }

}
