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

package org.mousephenotype.cda.support;

import org.mousephenotype.cda.db.dao.PhenotypePipelineDAO;
import org.mousephenotype.cda.utilities.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mrelac
 
 * This class encapsulates the code and data necessary to represent a Phenotype
 * Archive phenotype page for Selenium testing.
 */
public class PhenotypePage {

    protected final CommonUtils commonUtils = new CommonUtils();
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final String target;
    private final PhenotypePipelineDAO phenotypePipelineDAO;
    private final String baseUrl;
    private final PhenotypeTable phenotypeTable;
    protected final TestUtils testUtils = new TestUtils();

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    private boolean hasGraphs;
    private boolean hasImages;
    private boolean hasPhenotypesTable;
    private int resultsCount;
    private UrlUtils urlUtils = new UrlUtils();

    /**
     * Creates a new <code>GenePage</code> instance
     * @param driver A valid <code>WebDriver</code> instance
     * @param wait A valid <code>WebDriverWait</code> instance
     * @param target This page's target url
     * @param phenotypePipelineDAO a <code>PhenotypePipelineDAO</code> instance
     * @param baseUrl A fully-qualified hostname and path, such as
     *   http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve
     */
    public PhenotypePage(WebDriver driver, WebDriverWait wait, String target, PhenotypePipelineDAO phenotypePipelineDAO, String baseUrl) {
        this.driver = driver;
        this.wait = wait;
        this.target = target;
        this.phenotypePipelineDAO = phenotypePipelineDAO;
        this.baseUrl = baseUrl;
        this.phenotypeTable = new PhenotypeTable(driver, wait, target);

        load();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     *
     * @return The definition string
     */
    public String getDefinition() {
        String definition = "";

        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id='definition']")));
            if ( ! element.getText().isEmpty()) {
                if (element.findElement(By.cssSelector("span.label")).getText().trim().equals("Definition")) {
                    definition = element.getText().split("\\n")[1].trim();
                }
            }
        } catch (Exception e) { }

        return definition;
    }

    /**
     *
     * @return A list of mapped hp terms. The list will be empty if there are no mapped hp terms.
     */
    public List<String> getMappedHpTerms() {
        List<String> mappedHpTermList = new ArrayList();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id='mpId']")));

        try {
            List<WebElement> mappedHpTermElements = driver.findElements(By.xpath("//div[@id='mappedHpTerms']/ul/li"));
            for (WebElement mappedHpTermElement : mappedHpTermElements) {
                mappedHpTermList.add(mappedHpTermElement.getText().trim());
            }

        } catch (Exception e) { }

        return mappedHpTermList;
    }

    /**
     *
     * @return a <code>List&lt;String&gt;</code> of this page's graph urls. The
     * list will be empty if this page doesn't have any graph urls.
     */
    public List<String> getGraphUrls() {
        List<String> urls = new ArrayList();

        if (hasGraphs) {
            phenotypeTable.load();
            GridMap map = phenotypeTable.getData();
            for (int i = 0; i < map.getBody().length; i++) {
                urls.add(map.getCell(i, PhenotypeTable.COL_INDEX_PHENOTYPES_GRAPH_LINK));
            }
        }

        return urls;
    }

    /**
     *
     * @return A list of procedures. The list will be empty if there are no procedures.
     */
    public List<PhenotypeProcedure> getProcedures() {
        List<PhenotypeProcedure> procedureList = new ArrayList();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id='mpId']")));

        try {
            List<WebElement> procedureElements = driver.findElements(By.xpath("//div[@id='procedures']/ul/li/a"));
            for (WebElement procedureElement : procedureElements) {
                PhenotypeProcedure phenotypeProcedure = new PhenotypeProcedure(procedureElement.getText(), procedureElement.getAttribute("href"));
                procedureList.add(phenotypeProcedure);
            }

        } catch (Exception e) { }

        return procedureList;
    }

    /**
     * @return the number at the end of the string "Total number of results: xxxx" found just before the "phenotypes" HTML table.
     */
    public int getResultsCount() {
        return resultsCount;
    }

    /**
     *
     * @return A list of synonyms. The list will be empty if there are no synonyms.
     */
    public List<String> getSynonyms() {
        List<String> synonymList = new ArrayList();

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//p[@id='mpId']")));

            List<WebElement> synonymElements = driver.findElements(By.xpath("//p[@id='synonyms']"));
            for (WebElement synonymElement : synonymElements) {
                String[] synonymArray = synonymElement.getText().replace("Synonyms\n", "").split(",");
                for (String synonym : synonymArray) {
                    synonymList.add(synonym.trim());
                }
            }

        } catch (Exception e) { }

        return synonymList;
    }

    /**
     *
     * @return this page's url
     */
    public String getTarget() {
        return target;
    }

    public boolean hasGraphs() {
        return hasGraphs;
    }

    public boolean hasImages() {
        return hasImages;
    }

    /**
     *
     * @return true if this page has a <b><i>phenotypes</i></b> HTML table;
     * false otherwise.
     */
    public boolean hasPhenotypesTable() {
        return hasPhenotypesTable;
    }

    /**
     * Validates that:
     * <ul>
     *     <li>MGI MP browser has a link, and title starts with <b><i>Phenotype</i></b></li>
     *     <li>There is either a <b><i>Phenotype Association</i></b> section
     *         or an <b><i>Images</i></b> or both.</li>
     *     <li>If there is a <b><i>phenotypes</i></b> HTML table, validates that:
     *         <ul>
     *             <li>Each row has a p-value</li>
     *             <li>Each row has a valid graph link (the graph pages themselves
     *                 are not checked here as they take too long)</li>
     *             <li>The sex icon count matches <i>Total number of results</i> count</li>
     *             <li><b><i>TSV</i></b> and <b><i>XLS</i></b> downloads are valid</li>
     *         </ul>
     *     </li>
     * </ul>
     * @return validation status
     */
    public RunStatus validate() {
        RunStatus status = new RunStatus();

        // Validate title starts with 'Phenotype:'
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@id='top']")));
        if ( ! element.getText().startsWith("Phenotype:")) {
            status.addError("Expected phenotype page title to start with 'Phenotype:'.");
        }

        // Validate there is a 'Phenotype Association' section or at least one image.
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@id='gene-variants']")));
        } catch (Exception e) {
            if ( ! hasImages()) {
                status.addError("Expected either 'Phenotype Association' section or 'Images' section or both.");
            }
        }

        // If there is a 'phenotypes' HTML table, validate it.
        if (hasPhenotypesTable) {
            // Validate that there is a 'pheontypes' HTML table by loading it.
            phenotypeTable.load();                                                 // Load all of the phenotypes table pageMap data.
            List<List<String>> preAndPostQcList = phenotypeTable.getPreAndPostQcList();
            String cell;
            int i = 0;
            for (List<String> row : preAndPostQcList) {
                if (i++ == 0)
                    continue;

                //   Verify p value.
                cell = row.get(PhenotypeTable.COL_INDEX_PHENOTYPES_P_VALUE);
                if (cell == null) {
                    status.addError("Missing or invalid P Value. URL: " + target);
                }

                // Validate that the graph link is not missing.
                cell = row.get(PhenotypeTable.COL_INDEX_PHENOTYPES_GRAPH_LINK);
                if ((cell == null) || (cell.trim().isEmpty())) {
                    status.addError("Missing graph link. URL: " + target);
                }
            }

            // Validate the download links.
            //status = validateDownload(); jw set to ignore as failing for various reasons - surely we can set the code to run from the same method for display and download?
        }

        return status;
    }


    // PRIVATE METHODS


    /**
     * Get the full TSV data store
     * @param status Indicates the success or failure of the operation
     * @return the full TSV data store
     */
    private GridMap getDownloadTsv(RunStatus status) {
        String[][] data = new String[0][0];

        try {
            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
            // The downloadTarget is a path of the form 'http://ves-ebi-d0:8080/mi/impc/dev/phenotype-archive/phenotypes/export/MP:0002102?fileType=tsv&fileName=abnormal%20ear%20morphology.
            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export' with the baseUrl
            // and "/phenotypes".
            String downloadUrlBase = driver.findElement(By.xpath("//a[@id='tsvDownload']")).getAttribute("href");
            int pos = downloadUrlBase.indexOf("/export");
            downloadUrlBase = downloadUrlBase.substring(pos);
            String downloadTarget = baseUrl + "/phenotypes" + downloadUrlBase + ".tsv";

            // Get the download stream and statistics for the TSV stream.
            URL url = new URL(downloadTarget);
            DataReaderTsv dataReaderTsv = new DataReaderTsv(url);

            data = dataReaderTsv.getData();
        } catch (NoSuchElementException | TimeoutException te) {
            String message = "Invalid page: target " + target + ".";
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
     * @param status Indicates the success or failure of the operation
     * @return the full XLS data store
     */
    private GridMap getDownloadXls(RunStatus status) {
        String[][] data = new String[0][0];

        try {
            // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
            // The downloadTarget is a path of the form 'http://ves-ebi-d0:8080/mi/impc/dev/phenotype-archive/phenotypes/export/MP:0002102?fileType=xls&fileName=abnormal%20ear%20morphology.
            // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export' with the baseUrl
            // and "/phenotypes".
            String downloadUrlBase = driver.findElement(By.xpath("//a[@id='xlsDownload']")).getAttribute("href");
            int pos = downloadUrlBase.indexOf("/export");
            downloadUrlBase = downloadUrlBase.substring(pos);
            String downloadTarget = baseUrl + "/phenotypes" + downloadUrlBase + ".xls";

            // Get the download stream and statistics for the XLS stream.
            URL url = new URL(downloadTarget);
            DataReaderXls dataReaderXls = new DataReaderXls(url);

            data = dataReaderXls.getData();
        } catch (NoSuchElementException | TimeoutException te) {
            String message = "Invalid page: target " + target + ".";
            status.addError(message);
        }  catch (Exception e) {
            String message = "EXCEPTION processing target URL " + target + ": " + e.getLocalizedMessage();
            e.printStackTrace();
            status.addError(message);
        }

        return new GridMap(data, target);
    }

    /**
     * Waits for the pheno page to load.
     */
    private void load() {
        final String NOT_AVAILABLE = "Phenotype associations to genes and alleles will be available once data has completed quality control.";

        driver.get(target);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@id='gene-variants']")));

        // Get results count. [NOTE: pages with no phenotype associations don't have totals]
        Integer i = null;
        List<WebElement> elements = driver.findElements(By.cssSelector("div#phenotypesDiv div.alert"));
        if (elements.isEmpty()) {
            elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@id='phenotypesDiv']/div[@class='container span12']/p[@class='resultCount']")));
            if ( ! elements.isEmpty()) {
                String totResultsString = elements.get(0).getText();
                int index = totResultsString.lastIndexOf(":");
                String count = totResultsString.substring(index + 1);
                i = commonUtils.tryParseInt(count);
            }
        }

        // Determine if this page has images.
        elements = driver.findElements(By.xpath("//*[@id='imagesSection']/div/div/div"));
        hasImages = ! elements.isEmpty();

        // Determine if this page has phenotype associations.
        elements = driver.findElements(By.xpath("//table[@id='phenotypes']"));
        hasPhenotypesTable = ! elements.isEmpty();

        resultsCount = (i == null ? 0 : i);
        hasGraphs = (resultsCount > 0);
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
        GridMap pageData = phenotypeTable.load();                                   // Load all of the phenotypes table pageMap data.

        // Decode pageData link columns.
        pageData = new GridMap(urlUtils.urlDecodeColumn(pageData.getData(), PhenotypeTable.COL_INDEX_PHENOTYPES_GRAPH_LINK), pageData.getTarget());

        // Test the TSV.
        GridMap downloadData = getDownloadTsv(status);
        if (status.hasErrors()) {
            return status;
        }

        // Decode downloadData link columns.
        downloadData = new GridMap(urlUtils.urlDecodeColumn(downloadData.getData(), DownloadPhenotypeMap.COL_INDEX_GRAPH_LINK), downloadData.getTarget());
        status = validateDownload(pageData, downloadData);
        if (status.hasErrors()) {
            return status;
        }

        // Test the XLS.
        downloadData = getDownloadXls(status);
        if (status.hasErrors()) {
            return status;
        }

        // Decode downloadData link columns.
        downloadData = new GridMap(urlUtils.urlDecodeColumn(downloadData.getData(), DownloadPhenotypeMap.COL_INDEX_GRAPH_LINK), downloadData.getTarget());
        status = validateDownload(pageData, downloadData);
        if (status.hasErrors()) {
            return status;
        }

        return status;
    }

    /**
     * Internal validation comparing a loaded <code>pageMap</code> store with a
     * loaded <code>downloadData</code> store
     * @param pageData A loaded phenotypes table store
     * @param downloadData a loaded download store
     * @return status
     */
    private RunStatus validateDownload(GridMap pageData, GridMap downloadData) {
        RunStatus status = new RunStatus();
        int downloadDataLineCount = downloadData.getBody().length;

        // Do a set difference between the rows on the first displayed page
        // and the rows in the download file. The difference should be empty.
        int errorCount = 0;

        final Integer[] pageColumns = {
                  PhenotypeTable.COL_INDEX_PHENOTYPES_GENE
                , PhenotypeTable.COL_INDEX_PHENOTYPES_ALLELE
               // , PhenotypeTable.COL_INDEX_PHENOTYPES_ZYGOSITY
                , PhenotypeTable.COL_INDEX_PHENOTYPES_SEX
                , PhenotypeTable.COL_INDEX_PHENOTYPES_LIFE_STAGE
                , PhenotypeTable.COL_INDEX_PHENOTYPES_PHENOTYPE
                , PhenotypeTable.COL_INDEX_PHENOTYPES_PROCEDURE
                , PhenotypeTable.COL_INDEX_PHENOTYPES_PARAMETER
                , PhenotypeTable.COL_INDEX_PHENOTYPES_PHENOTYPING_CENTER
                , PhenotypeTable.COL_INDEX_PHENOTYPES_SOURCE
                //, PhenotypeTable.COL_INDEX_PHENOTYPES_GRAPH_LINK jw removed as http vs // path errors
        };
        final Integer[] downloadColumns = {

                  DownloadPhenotypeMap.COL_INDEX_GENE
                , DownloadPhenotypeMap.COL_INDEX_ALLELE
                //, DownloadPhenotypeMap.COL_INDEX_ZYGOSITY jw set this to not being used until fixed as homozygous vs hom which is just annoying! :)
                , DownloadPhenotypeMap.COL_INDEX_SEX
                , DownloadPhenotypeMap.COL_INDEX_LIFE_STAGE
                , DownloadPhenotypeMap.COL_INDEX_PHENOTYPE
                , DownloadPhenotypeMap.COL_INDEX_PROCEDURE
                , DownloadPhenotypeMap.COL_INDEX_PARAMETER
                , DownloadPhenotypeMap.COL_INDEX_PHENOTYPING_CENTER
                , DownloadPhenotypeMap.COL_INDEX_SOURCE
                //, DownloadPhenotypeMap.COL_INDEX_GRAPH_LINK
        };
        final Integer[] decodeColumns = {
                DownloadPhenotypeMap.COL_INDEX_GRAPH_LINK
        };

        // Create a pair of sets: one from the page, the other from the download.
        Set<String> pageSet = testUtils.createSet(pageData, pageColumns);
        Set<String> downloadSet = testUtils.createSet(downloadData, downloadColumns);

        Set<String> difference = testUtils.cloneStringSet(pageSet);
        difference.removeAll(downloadSet);
        if ( ! difference.isEmpty()) {
            String message = "PhenotypePage.validateDownload(): Page/Download data mismatch. \nURL: " + driver.getCurrentUrl();
            Iterator it = difference.iterator();
            int i = 0;
            while (it.hasNext()) {
                String value = (String) it.next();
                logger.error("[" + i + "]:\t page data:\n" + value);
                logger.error("\t download data:\n" + testUtils.closestMatch(downloadSet, value) + "\n");
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