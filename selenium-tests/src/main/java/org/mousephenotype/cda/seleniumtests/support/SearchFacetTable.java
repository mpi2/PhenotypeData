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

import org.mousephenotype.cda.seleniumtests.exception.TestException;
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.cda.utilities.UrlUtils;
import org.mousephenotype.cda.web.DownloadType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * @author mrelac
 *
 * This class encapsulates the code and data necessary to represent the abstract
 * parent of the search page facet tables. It contains code and data common to
 * all such search page facet tables. Subclasses need only implement <code>
 * validateDownload(String[][] data)</code>.
 */
@Deprecated
public abstract class SearchFacetTable {

    private Map<TableComponent, By> byMap;
    protected WebDriver driver;
    protected final CommonUtils commonUtils = new CommonUtils();
    private boolean hasTable;
    protected String[] pageHeading;
    protected WebElement table;
    protected String target;
    protected final TestUtils testUtils = new TestUtils();
    protected long timeoutInSeconds;
    protected final UrlUtils urlUtils = new UrlUtils();
    protected WebDriverWait wait;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    public static final String NO_INFO_AVAILABLE    = "No information available";
    public static final String NO_ES_CELLS_PRODUCED = "No ES Cell produced";

    public static final String SHOW_ANNOTATION_VIEW = "Show Annotation View";
    public static final String SHOW_IMAGE_VIEW      = "Show Image View";
    public enum ImagesView {
        ANNOTATION_VIEW,
        IMAGE_VIEW
    }

    // byHash String keys:
    public enum TableComponent {
        BY_TABLE("byTable")
        , BY_TABLE_TR("byTableTr")
        , BY_SELECT_GRID_LENGTH("bySelectGridLength");

        private final String mapKey;

        private TableComponent(final String mapKey) {
          this.mapKey = mapKey;
        }

        @Override
        public String toString() {
          return mapKey;
        }
    };


    /**
     * Creates a new instance (called by descendent classes).
     *
     * @param driver A valid <code>WebDriver</code> instance
     * @param timeoutInSeconds timeout
     * @param byMap a map of HTML table-related definitions, keyed by <code> TableComponent</code>.
     *
     * @throws TestException
     */
    public SearchFacetTable (WebDriver driver, long timeoutInSeconds, Map<TableComponent, By> byMap) throws TestException {
        this.driver = driver;
        this.target = driver.getCurrentUrl();
        this.timeoutInSeconds = timeoutInSeconds;
        this.wait = new WebDriverWait(driver, timeoutInSeconds);
        this.byMap = byMap;
        try {
            setTable(driver.findElement(byMap.get(TableComponent.BY_TABLE)));
        } catch (Exception e) {
            logger.error("URL: " + target);
            throw e;
        }
    }

    public enum EntriesSelect {
        _10(10),
        _25(25),
        _50(50),
        _100(100);

        private final int value;

        private EntriesSelect(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     *
     * @return the number of rows in the "xxGrid" table. Always include 1
     * extra for the heading.
     */
    public int computeTableRowCount() {
        // Wait for page.
        List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(byMap.get(TableComponent.BY_TABLE_TR)));
        return elements.size() + 1;
    }

//    /**
//     * Return the number of entries currently showing in the 'entries' drop-down
//     * box.
//     *
//     * @return the number of entries currently showing in the 'entries'
//     * drop-down box.
//     */
//    public int getNumEntries() {
//        Select select = new Select(driver.findElement(byMap.get(TableComponent.BY_SELECT_GRID_LENGTH)));
//        try {
//            return commonUtils.tryParseInt(select.getFirstSelectedOption().getText());
//        } catch (NullPointerException npe) {
//            return 0;
//        }
//    }
//
//    /**
//     *
//     * @return true if this page has a searchFaceTable; false otherwise
//     */
//    public boolean hasTable() {
//        return hasTable;
//    }
//
//    /**
//     *
//     * @return The searchFacetTable <code>WebElement</code>
//     */
//    public WebElement getTable() {
//        return table;
//    }

    /**
     * Set the  <code>WebElement</code> search facet table
     * @param table WebElement New instance to set to
     */
    public final void setTable(WebElement table) {
//        hasTable = false;
//        this.table = table;
//        try {
//            table = driver.findElement(byMap.get(TableComponent.BY_TABLE));
//        } catch (Exception e) {
//            pageHeading = null;
//            this.table = null;
//            return;
//        }
//        hasTable = true;
//
//        // Save the pageHeading values.
//        List<WebElement> headingElementList = table.findElements(By.cssSelector("thead tr th"));
//        pageHeading = new String[headingElementList.size()];
//        if ( ! headingElementList.isEmpty()) {
//            for (int colIndex = 0; colIndex < headingElementList.size(); colIndex++) {
//                WebElement headingElement = headingElementList.get(colIndex);
//                pageHeading[colIndex] = headingElement.getText();
//            }
//        }
    }

    /**
     * Validates download data against this search table instance.
     *
     * @param data The download data used for comparison
     * @param downloadType Supported download type - e.g. TSV, XLS
     * @return validation status
     */
    public abstract RunStatus validateDownload(String[][] data, DownloadType downloadType);


    // PROTECTED METHODS


    /**
     * Validates download data against this search table instance.
     *
     * @param pageData The page data used for comparison
     * @param pageColumns The page columns used in the comparison
     * @param downloadDataArray The download data used for comparison
     * @param downloadColumns The download columns used in the comparison
     * @param downloadUrl The download stream URL
     *
     * @return validation status
     */
    protected RunStatus validateDownloadInternal(GridMap pageData, Integer[] pageColumns, String[][] downloadDataArray, Integer[] downloadColumns, String downloadUrl) {
        RunStatus status = new RunStatus();
        List<List<String>> downloadDataList = new ArrayList();
        for (String[] row : downloadDataArray) {
            List rowList = Arrays.asList(row);
            downloadDataList.add(rowList);
        }

        GridMap downloadData = new GridMap(downloadDataList, target);
        // Replace any occurrence of more than one space with exactly one space; otherwise, a comparison may fail simply because of an extra space.
        String[][] downloadDataRows = downloadData.getData();
        for (String[] row : downloadDataRows) {
            for (int i = 1; i < row.length; i++) {
                if (row[i] != null) {
                    row[i] = row[i].replaceAll("  ", " ");
                }
            }
        }
        downloadData = new GridMap(downloadDataRows, pageData.getTarget());

        // Do a set difference between the rows on the first displayed page
        // and the rows in the download file. The difference should be empty.
        int errorCount = 0;

        // Create a pair of sets: one from the page, the other from the download.
        GridMap patchedPageData = new GridMap(testUtils.patchEmptyFields(pageData.getBody()), pageData.getTarget());
        Set pageSet = testUtils.createSet(patchedPageData, pageColumns);
        Set downloadSet = testUtils.createSet(downloadData, downloadColumns);
        Set difference = testUtils.cloneStringSet(pageSet);
        difference.removeAll(downloadSet);
        if ( ! difference.isEmpty()) {
            String message = "SearchFacetTable.validateDownloadInternal(): Page/Download data mismatch. \nURL: " + downloadUrl;
            Iterator it = difference.iterator();
            int i = 0;
            while (it.hasNext()) {
                String value = (String)it.next();
                logger.error("[" + i + "]:\t page data:\n" + value);
                logger.error("[" + i + "]:\tdownload data:\n" + testUtils.closestMatch(downloadSet, value) + "\n");
                i++;
                errorCount++;
            }

            status.addError(message);
        }

        return status;
    }
}