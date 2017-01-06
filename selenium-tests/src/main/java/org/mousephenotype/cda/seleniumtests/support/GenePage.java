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

package org.mousephenotype.cda.seleniumtests.support;

import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.seleniumtests.exception.TestException;
import org.mousephenotype.cda.utilities.*;
import org.mousephenotype.cda.web.DownloadType;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;

/**
 *
 * @author mrelac
 
 * This class encapsulates the code and data necessary to represent a Phenotype
 * Archive gene page for Selenium testing.
 */
public class GenePage {

    private final String baseUrl;
    protected final CommonUtils commonUtils = new CommonUtils();
    private final WebDriver driver;
    private final String geneId;
    private final GeneTable geneTable;
    private final PhenotypePipelineDAO phenotypePipelineDAO;
    private final String target;
    protected final TestUtils testUtils = new TestUtils();
    protected final UrlUtils urlUtils = new UrlUtils();
    private final WebDriverWait wait;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    private boolean hasDiseaseModels;
    private boolean hasImages;
    private boolean hasImpcImages;
    private boolean hasGraphs;
    private boolean hasGenesTable;
    private ResultsCount resultsCount = new ResultsCount();

    public class ResultsCount {
        private int females = 0;
        private int males = 0;

        public int getFemales() {
            return females;
        }

        public void setFemales(Integer females) {
            this.females = (females == null ? 0 : females);
        }

        public int getMales() {
            return males;
        }

        public void setMales(Integer males) {
            this.males = (males == null ? 0 : males);
        }

        public int getTotals() {
            return males + females;
        }
    }

    /**
     * Creates a new <code>GenePage</code> instance
     * @param driver A valid <code>WebDriver</code> instance
     * @param wait A valid <code>WebDriverWait</code> instance
     * @param target This page's target url
     * @param geneId This page's gene id
     * @param phenotypePipelineDAO a <code>PhenotypePipelineDAO</code> instance
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @throws TestException
     */
    public GenePage(WebDriver driver, WebDriverWait wait, String target, String geneId, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl) throws TestException {
        this.driver = driver;
        this.wait = wait;
        this.target = target;
        this.geneId = geneId;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.baseUrl = baseUrl;
        this.geneTable = new GeneTable(driver, wait, target);

        load();
    }

    /**
     *
     * @return A list of top level MP terms.
     */
    public List<String> getAssociatedImageSections() {
        List<String> associatedImageSections = new ArrayList();
        List<WebElement> associatedImageSectionElements = driver.findElements(By.className("accordion-heading"));
        for (WebElement associatedImageSectionElement : associatedImageSectionElements) {
            associatedImageSections.add(associatedImageSectionElement.getText());
        }

        return associatedImageSections;
    }

    /**
     *
     * @return A list of Impc Image urls.
     */
    public List<String> getAssociatedImpcImageUrls() {
        List<String> associatedImageUrls = new ArrayList();
        List<WebElement> associatedImageUrlElements = driver.findElements(By.xpath("//div[@id='grid']/ul//img"));
        for (WebElement associatedImageUrlElement : associatedImageUrlElements) {
            associatedImageUrls.add(associatedImageUrlElement.getText());
        }

        return associatedImageUrls;
    }

    /**
     *
     * @return All significant abnormality strings
     */
    public List<String> getSignificantAbnormalities() {
        List<String> abnormalityStrings = new ArrayList();

        List<WebElement> significantAbnormalityElements = driver.findElements(By.xpath("//div[@class='abnormalities']//div[contains(@class, 'sprite_orange') and not (contains(@class, 'sprite_NA'))]"));

        for (WebElement significantAbnormalityElement : significantAbnormalityElements) {
            String abnormality = significantAbnormalityElement.getAttribute("oldtitle");
            if ((abnormality != null) && ( ! abnormality.isEmpty()))
                abnormalityStrings.add(abnormality);
        }

        return abnormalityStrings;
    }

    /**
     *
     * @return All NotSignificant abnormality strings
     */
    public List<String> getNotSignificantAbnormalities() {
        List<String> abnormalityStrings = new ArrayList();

        List<WebElement> notSignificantAbnormalityElements = driver.findElements(By.xpath("//div[@class='abnormalities']//div[contains(@class, 'sprite_blue') and not (contains(@class, 'sprite_NA'))]"));

        for (WebElement notSignificantAbnormalityElement : notSignificantAbnormalityElements) {
            String abnormality = notSignificantAbnormalityElement.getAttribute("oldtitle");
            if ((abnormality != null) && ( ! abnormality.isEmpty()))
                abnormalityStrings.add(abnormality);
        }

        return abnormalityStrings;
    }

    /**
     *
     * @return the base url
     */
    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     *
     * @return all button labels in a <code>List</code>.
     */
    public List<String> getButtonLabels() {
        List<String> buttonLabels = new ArrayList();
        List<WebElement> buttons = driver.findElements(By.xpath("//*[contains(@class,'btn')]"));
        for (WebElement button : buttons) {
            buttonLabels.add(button.getText());
        }

        return buttonLabels;
    }

    /**
     *
     * @return the gene ID
     */
    public String getGeneId() {
        return geneId;
    }

    /**
     *
     * @return a <code>List&lt;String&gt;</code> of this page's graph urls. The
     * list will be empty if this page doesn't have any graph urls.
     */
    public List<String> getGraphUrls() {
        List<String> urls = new ArrayList();

        if (hasGraphs) {
            if (geneTable.genesTableIsNotEmpty()) {
                geneTable.load();
                GridMap map = geneTable.getData();
                for (int i = 0; i < map.getBody().length; i++) {
                    urls.add(map.getCell(i, GeneTable.COL_INDEX_GENES_GRAPH_LINK));
                }
            }
        }

        return urls;
    }

    /**
     * Return a list of this page's graph urls matching the given graph url type.
     *
     * @param graphUrlType the graph url type desired: preqc, postqc, or both
     *
     * @return a list of this page's graph urls matching the given graph url type.
     */
    public List<String> getGraphUrls(GraphUrlType graphUrlType) {
        List<String> urls = new ArrayList();
        List<List<String>> graphUrlList;

        if (hasGraphs()) {
            if (geneTable.genesTableIsNotEmpty()) {
                geneTable.load();

                switch (graphUrlType) {
                    case POSTQC:        graphUrlList = geneTable.getPostQcList();
                    break;

                    case PREQC:         graphUrlList = geneTable.getPreQcList();
                    break;

                    default:            graphUrlList = geneTable.getPreAndPostQcList();
                }

                for (List<String> row : graphUrlList) {
                    urls.add(row.get(GeneTable.COL_INDEX_GENES_GRAPH_LINK));
                }
            }
        }
        return urls;
    }
    public enum GraphUrlType {
          PREQC
        , POSTQC
        , PREQC_AND_POSTQC
    }

    /**
     * Returns the phenotyping status order button elements (e.g. 'phenotype data available',
     * 'Mice', etc.)
     *
     * @return the phenotyping status order button elements (e.g. 'phenotype data available',
     * 'Mice', etc.)
     */
    public List<WebElement> getphenotypingStatusOrderButtons() {
        return driver.findElements(By.xpath("//a[contains(@class, 'phenotypingStatus')]"));
    }

    /**
     * Returns the production status order button elements (e.g. 'ES Cells',
     * 'Mice', etc.)
     *
     * @return the production status order button elements (e.g. 'ES Cells',
     * 'Mice', etc.)
     */
    public List<WebElement> getProductionStatusOrderButtons() {
        return driver.findElements(By.xpath("//a[contains(@class, 'productionStatus')]"));
    }

    /**
     * @return the number at the end of the gene page string 'Total number of results: xxxx'
     */
    public int getResultsCount() {
        return resultsCount.getTotals();
    }

    /**
     *
     * @return all section titles in a <code>List</code>.
     */
    public List<String> getSectionTitles() {
        List<String> sectionTitles = new ArrayList();
        List<WebElement> sections = driver.findElements(By.cssSelector(".title"));

        for (WebElement sectionElement : sections) {
            String text = sectionElement.getText().trim();
            if ((text != null) && ( ! text.isEmpty()))
                sectionTitles.add(sectionElement.getText());
        }

        return sectionTitles;
    }

    /**
     *
     * @return The target URL
     */
    public String getTarget() {
        return target;
    }

    /**
     *
     * @return the title ('Gene: Akt2')
     */
    public String getTitle() {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("top")));
        return element.getText();
    }

    /**
     *
     * @return A list of top level MP terms.
     */
    public List<String> getTopLevelMPs() {
        List<String> topLevelMPs = new ArrayList();
        Select selectTopLevel = new Select(driver.findElement(By.id("top_level_mp_term_name")));
        for (WebElement option : selectTopLevel.getOptions()) {
            topLevelMPs.add(option.getAttribute("value"));
        }

        return topLevelMPs;
    }

    /**
     *
     * @return true if this page has graphs; false otherwise.
     */
    public boolean hasGraphs() {
        return hasGraphs;
    }

    /**
     *
     * @return true if this page has images; false otherwise.
     */
    public boolean hasImages() {
        return hasImages;
    }

    /**
     *
     * @return true if this page has images; false otherwise.
     */
    public boolean hasImpcImages() {
        return hasImpcImages;
    }

    /**
     *
     * @return true if this page has a <b><i>genes</i></b> HTML table;
     * false otherwise.
     */
    public boolean hasGenesTable() {
        return hasGenesTable;
    }

    /**
     * Returns true if this is an 'Oops...' page; false otherwise.
     *
     * @return true if this is an 'Oops...' page; false otherwise.
     */
    public boolean isOopsPage() {
        List<WebElement> elements = driver.findElements(By.cssSelector("#main > div.region.region-content > div > div > div > h1"));
        if ( ! elements.isEmpty()) {
            for (WebElement element : elements) {
                if (element.getText().startsWith("Oops!")) {
                    return true;
                }
            }
        }

        return false;
    }
    /**
     * Validates that:
     * <ul>
     *     <li>There is a <b><i>Phenotype Association</i></b> section.</li>
     *     <li>Gene page title starts with <b><i>Gene:</i></b></li>
     *     <li>If there is a <b><i>genes</i></b> HTML table, validates that:
     *         <ul>
     *             <li>Each row has a p-value</li>
     *             <li>Each row has a valid graph link (the graph pages themselves
     *                 are not checked here as they take too long)</li>
     *             <li>The sex icon count matches <i>Total number of results</i> count</li>
     *             <li><b><i>TSV</i></b> and <b><i>XLS</i></b> downloads are valid</li>
     *         </ul>
     *     </li>
     *     <li>A <b><i>genes</i></b> HTML table is present if <code>
     *         genesTableRequired</code> is <code>true</code></li>
     *     <li>There are 3 buttons:</li>
     *     <ul>
     *         <li><b><i>Login to register interest</i></b></li>
     *         <li><b><i>Order</i></b></li>
     *         <li><b><i>KOMP</i></b></li>
     *     </ul>
     * </ul>
     * @param genesTableRequired If set to true, there must be a phenotype
     * HTML table or an error is logged. If false, no error is logged if there
     * is no phenotype HTML table.
     * @return validation status
     */
    public RunStatus validate(boolean genesTableRequired) {
        RunStatus status = new RunStatus();

        // Validate title starts with 'Gene:'
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@id='top']")));
        if ( ! element.getText().startsWith("Gene:")) {
            status.addError("Expected gene page title to start with 'Gene:'.");
        }

        // Validate there is a 'Phenotype Association' section.
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@id='section-associations']")));
        } catch (Exception e) {
            status.addError("Expected 'Phenotype Association' section.");
        }

        // If there is a 'genes' HTML table, validate it.
        if (hasGenesTable) {
            geneTable.load();                                                   // Load all of the genes table pageMap data. Use preAndPostQcList.
            List<List<String>> preAndPostQcList = geneTable.getPreAndPostQcList();
            String cell;
            int i = 0;
            for (List<String> row : preAndPostQcList) {
                if (i++ == 0)
                    continue;

                //   Verify p value.
                cell = row.get(GeneTable.COL_INDEX_GENES_P_VALUE);
                if (cell == null) {
                    status.addError("Missing or invalid P Value. URL: " + target);
                }

                cell = row.get(GeneTable.COL_INDEX_GENES_GRAPH_LINK);
                if ((cell == null) || (cell.trim().isEmpty())) {
                    status.addError("Missing graph link. URL: " + target);
                }
            }

            // Validate the download links.
            status = validateDownload();
        } else {
            if (genesTableRequired) {
                status.addError("Expected genes HTML table but found none.");
            }
        }

        // Buttons - these are the only buttons that are guaranteed to exist.
        List<String> expectedButtons = Arrays.asList( new String[] { "Login to register interest" } );
        List<String> actualButtons = new ArrayList<>();

        List<WebElement> actualButtonElements = driver.findElements(By.xpath("//a[contains(@class, 'btn')]"));
        for (WebElement webElement : actualButtonElements) {
            actualButtons.add(webElement.getText());
        }
        // ... count
        if (actualButtons.size() < expectedButtons.size()) {
            status.addError("Expected at least " + expectedButtons.size() + " buttons but found " + actualButtons.size());
        }
        // ... Button text
        for (String expectedButton : expectedButtons) {
            if ( ! actualButtons.contains(expectedButton)) {
                status.addError("Expected button with title '" + expectedButton + "' but none was found.");
            }
        }

        return status;
    }

    public int getGenesLength() {
        Select select = new Select(driver.findElement(By.xpath("//select[@name='genes_length']")));
        return commonUtils.tryParseInt(select.getFirstSelectedOption());
    }

    public GeneTable getGeneTable() {
        return geneTable;
    }

    public void selectGenesLength(Integer resultCount) {
        Select select = new Select(driver.findElement(By.xpath("//select[@name='genes_length']")));
        select.selectByValue(resultCount.toString());
    }


    // PRIVATE METHODS


    /**
     * Waits for the gene page to load.
     */
    private void load() throws TestException {
        try {
            driver.get(target);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[@id='summaryMgiId']/following-sibling::a")));
            if (isOopsPage()) {
                throw new TestException("GenePage: Found 'Oops...' page. URL: " + target);
            }
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("span#enu")));
        } catch (Exception e) {
            throw new TestException("GenePage: failed to load url. Reason: " + e.getLocalizedMessage() + "\nURL: " + target);
        }

        List<WebElement> elements;


        // Check for phenotype associations. If any, get the results count.
        try {
            elements = driver.findElements(By.xpath("//table[@id='genes']"));
            hasGenesTable = ! elements.isEmpty();
            if (hasGenesTable) {
                elements = driver.findElements(By.xpath("//*[@id='phenotypesDiv']//p[@class='resultCount']"));
                String totResultsString = elements.get(0).getText();
                int index = totResultsString.lastIndexOf(":");
                String[] counts = totResultsString.substring(index + 1).split(",");
                if ((counts != null) && (counts.length > 0)) {
                    for (String count : counts) {
                        if (count.contains("female")) {
                            resultsCount.setFemales(commonUtils.extractIntFromParens(count));
                        } else if (count.contains("male")) {
                            resultsCount.setMales(commonUtils.extractIntFromParens(count));
                        }
                    }
                }
            }
            hasGraphs = (resultsCount.getTotals() > 0);
        } catch (Exception e) {
            throw new TestException("GenePage.load(): page appears to have a 'genes' HTML table but it was not found.");
        }

        // Check for expression.

        // Check for phenotype associated images.
        elements = driver.findElements(By.xpath("//*[@id='section-images']/following-sibling::div[1]//h5"));
        if ( ! elements.isEmpty()) {
            String text = elements.get(0).getText().toLowerCase();
            hasImages = text.contains("legacy");
            hasImpcImages = text.contains("associated images");
        }

        // Check for disease models.
        elements = driver.findElements(By.xpath("//*[@id='predicted_diseases_table']"));
        if ( ! elements.isEmpty()) {
            hasDiseaseModels = ! elements.isEmpty();
        }
    }

    /**
     * Get the full TSV data store
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param status Indicates the success or failure of the operation
     * @return the full TSV data store
     */
    private GridMap getDownloadTsv(String baseUrl, RunStatus status) {
        String[][] data = new String[0][0];

        try {
            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
            // The downloadTarget is a path of the form '/mi/impc/dev/phenotype-archive/genes/export/MGI:2158015?fileType=tsv&fileName=Rln3'.
            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export' with the baseUrl
            // and "/genes".
            String downloadUrlBase = driver.findElement(By.xpath("//a[@id='tsvDownload']")).getAttribute("href");
            int pos = downloadUrlBase.indexOf("/export");
            downloadUrlBase = downloadUrlBase.substring(pos);
            String downloadTarget = baseUrl + "/genes" + downloadUrlBase + ".tsv";

            // Get the download stream and statistics for the TSV stream.
            URL url = new URL(downloadTarget);
            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);

            data = dataReaderTsv.getData();
        } catch (NoSuchElementException | TimeoutException te) {
            String message = "Expected page for ID " + geneId + "(" + target + ") but found none.";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            e.printStackTrace();
            status.addError(message);
        }

        return new GridMap(data, target);
    }

    /**
     * Get the full XLS data store
     * @param baseUrl A fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     * @param status Indicates the success or failure of the operation
     * @return the full XLS data store
     */
    private GridMap getDownloadXls(String baseUrl, RunStatus status) {
        String[][] data = new String[0][0];

        try {
            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
            // The downloadTarget is a path of the form '/mi/impc/dev/phenotype-archive/genes/export/MGI:2158015?fileType=xls&fileName=Rln3'.
            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export' with the baseUrl
            // and "/genes".
            String downloadUrlBase = driver.findElement(By.xpath("//a[@id='xlsDownload']")).getAttribute("href");
            int pos = downloadUrlBase.indexOf("/export");
            downloadUrlBase = downloadUrlBase.substring(pos);
            String downloadTarget = baseUrl + "/genes" + downloadUrlBase + "xls";

            // Get the download stream and statistics for the XLS stream.
            URL url = new URL(downloadTarget);
            DataReaderXls dataReaderXls = new DataReaderXls(url);

            data = dataReaderXls.getData();
        } catch (NoSuchElementException | TimeoutException te) {
            String message = "Expected page for ID " + geneId + "(" + target + ") but found none.";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            e.printStackTrace();
            status.addError(message);
        }

        return new GridMap(data, target);
    }

    /**
     * Validate the page data against the download data. This is a difficult task,
     * as there is more detail data in the download file that simply doesn't exist
     * on the page. Metadata splits complicate matters. And, on the page, there is
     * a single row for male and female; in the download, there are always two such
     * rows - one for each sex. Then there is the newly added pagination that
     * does not serve up all of the page data in a single gulp any more.
     *
     * So validation will be simple:
     * <li>Check that the number of rows in the download file is at least as
     *     many rows as the number of [non-preqc] sex icons shown on the first page.</li>
     * <li>Do a set difference between the rows on the first displayed page
     *     and the rows in the download file. The difference should be empty.</li></ul>
     * Any errors are returned in the <code>RunStatus</code> instance.
     *
     * @return page status instance
     */
    private RunStatus validateDownload() {
        RunStatus status = new RunStatus();
        GridMap pageMap = geneTable.load();                                        // Load all of the genes table pageMap data.

        // Test the TSV.
        //jw set to ignore so tests pass - these fail if any largish number of calls is made to these - we need to refactore the code that generates the tables and the files  or use solr to give a tab delim file of the data....
        GridMap downloadData = getDownloadTsv(baseUrl, status);
        if (status.hasErrors()) {
            return status;
        }

        status = validateDownload(pageMap, downloadData, DownloadType.TSV);
        if (status.hasErrors()) {
            return status;
        }

        // Test the XLS.
        downloadData = getDownloadXls(baseUrl, status);
        if (status.hasErrors()) {
            return status;
        }

        status = validateDownload(pageMap, downloadData, DownloadType.XLS);
        if (status.hasErrors()) {
            return status;
        }

        return status;
    }

    /**
     * Internal validation comparing a loaded <code>pageMap</code> store with a
     * loaded <code>downloadData</code> store
     * @param pageData A loaded genes table store
     * @param downloadData a loaded download store
     * @return status
     */
    private RunStatus validateDownload(GridMap pageData, GridMap downloadData, DownloadType downloadType) {
        RunStatus status = new RunStatus();
        int downloadDataLineCount = downloadData.getBody().length;

        // Encode the page and download graph links for accurate comparison.
        pageData = new GridMap(urlUtils.urlEncodeColumn(pageData.getData(), GeneTable.COL_INDEX_GENES_GRAPH_LINK), pageData.getTarget());
        downloadData = new GridMap(urlUtils.urlEncodeColumn(downloadData.getData(), DownloadGeneMap.COL_INDEX_GRAPH_LINK), downloadData.getTarget());

        // The page zygosity string is the first three characters of the zygosity: het, hom, hem, etc. Truncate the download zygosity string to match to avoid set difference failure.
        downloadData = testUtils.convertZygosityToShortName(downloadData, DownloadGeneMap.COL_INDEX_ZYGOSITY);

        // Do a set difference between the rows on the first displayed page
        // and the rows in the download file. The difference should be empty.
        int errorCount = 0;

        final Integer[] pageColumns = {
                  GeneTable.COL_INDEX_GENES_PHENOTYPE
                , GeneTable.COL_INDEX_GENES_ALLELE
                , GeneTable.COL_INDEX_GENES_ZYGOSITY
                , GeneTable.COL_INDEX_GENES_SEX
                , GeneTable.COL_INDEX_GENES_LIFE_STAGE
//                , GeneTable.COL_INDEX_GENES_GRAPH_LINK
        };
        final Integer[] downloadColumns = {
                  DownloadGeneMap.COL_INDEX_PHENOTYPE
                , DownloadGeneMap.COL_INDEX_ALLELE
                , DownloadGeneMap.COL_INDEX_ZYGOSITY
                , DownloadGeneMap.COL_INDEX_SEX
                , DownloadGeneMap.COL_INDEX_LIFE_STAGE
//                , DownloadGeneMap.COL_INDEX_GRAPH_LINK    // Can't check these anymore because the page collapses center, procedure, parameter, and pipeline whereas, the download does not.
        };
        final Integer[] decodeColumns = {
                DownloadGeneMap.COL_INDEX_GRAPH_LINK
        };

        // Create a pair of sets: one from the page, the other from the download.
        Set<String> pageSet = testUtils.createSet(pageData, pageColumns);
        Set<String> downloadSet = testUtils.createSet(downloadData, downloadColumns);


        Set difference = testUtils.cloneStringSet(pageSet);
        difference.removeAll(downloadSet);
        if ( ! difference.isEmpty()) {
            String message = "GenePage.validateDownload(): Page/Download data mismatch. \nURL: " + driver.getCurrentUrl();
            Iterator it = difference.iterator();
            int i = 0;
            while (it.hasNext()) {
                String value = (String) it.next();
                logger.error("[" + i + "]:\t page data:\n\t" + value);
                logger.error("\t download data:\n\t" + testUtils.closestMatch(downloadSet, value) + "\n");
                i++;
                errorCount++;
            }
        }

        if (errorCount > 0) {
            status.addError("Mismatch.");
        }

        return status;
    }
}
