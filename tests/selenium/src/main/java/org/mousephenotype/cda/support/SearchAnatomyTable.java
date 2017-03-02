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

import org.apache.commons.lang3.StringUtils;
import org.mousephenotype.cda.exception.TestException;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.cda.web.DownloadType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'maGrid' HTML table for anatomy.
 */
@Deprecated
public class SearchAnatomyTable extends SearchFacetTable {

    private final List<AnatomyRow> bodyRows = new ArrayList();
    private GridMap pageData;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Map<TableComponent, By> map = new HashMap();
    public static final int COL_INDEX_ANATOMY_TERM     = 0;
    public static final int COL_INDEX_ANATOMY_ID       = 1;
    public static final int COL_INDEX_ANATOMY_ID_LINK  = 2;
    public static final int COL_INDEX_ANATOMY_SYNONYMS = 3;
    public static final int COL_INDEX_LAST = COL_INDEX_ANATOMY_SYNONYMS;        // Should always point to the last (highest-numbered) index.

    static {
        map.put(TableComponent.BY_TABLE, By.xpath("//table[@id='maGrid']"));
        map.put(TableComponent.BY_TABLE_TR, By.xpath("//table[@id='maGrid']/tbody/tr"));
        map.put(TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='maGrid_length']"));
    }

    /**
     * Creates a new <code>SearchAnatomyTable</code> instance.
     *
     * @param driver A valid <code>WebDriver</code> instance
     * @param timeoutInSeconds timeout
     */
    public SearchAnatomyTable(WebDriver driver, int timeoutInSeconds) throws TestException {
        super(driver, timeoutInSeconds, map);

        pageData = load();
    }

    /**
     * Validates download data against this <code>SearchAnatomyTable</code>
     * instance.
     * 
     * @param downloadDataArray The download data used for comparison
     * @param downloadType Supported download type - e.g. TSV, XLS
     *
     * @return validation status
     */
    @Override
    public RunStatus validateDownload(String[][] downloadDataArray, DownloadType downloadType) {
        final Integer[] pageColumns = {
                COL_INDEX_ANATOMY_TERM
              , COL_INDEX_ANATOMY_ID
              , COL_INDEX_ANATOMY_ID_LINK
              , COL_INDEX_ANATOMY_SYNONYMS
        };
        final Integer[] downloadColumns = {
              DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM
            , DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_ID
            , DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_ID_LINK
            , DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_SYNONYMS
        };
        
        return validateDownloadInternal(pageData, pageColumns, downloadDataArray, downloadColumns, driver.getCurrentUrl());
    }
    
    
    // PRIVATE METHODS


    /**
     * Pulls all rows of data and column access variables from the search page's 'maGrid' HTML table.
     *
     * @return <code>numRows</code> rows of data and column access variables from the search page's phenotype HTML table.
     */
    private GridMap load() {
        return load(null);
    }

    /**
     * Pulls <code>numRows</code> rows of search page gene facet data and column access variables from the search page's
     * 'maGrid' HTML table.
     *
     * @param numRows the number of <code>GridMap</code> table rows to return, including the heading row. To specify all
     *                rows, set <code>numRows</code> to null.
     *
     * @return <code>numRows</code> rows of search page gene facet data and column access variables from the search
     * page's 'diseaseGrid' HTML table.
     */
    private GridMap load(Integer numRows) {
        if (numRows == null)
            numRows = computeTableRowCount();

        String[][] pageArray;

        // Wait for page.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#maGrid")));
        int numCols = COL_INDEX_LAST + 1;

        pageArray = new String[numRows][numCols];                               // Allocate space for the data.
        for (int i = 0; i < numCols; i++) {
            pageArray[0][i] = "Column_" + i;                                    // Set the headings.
        }

        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int sourceRowIndex = 1;

            pageArray[sourceRowIndex][COL_INDEX_ANATOMY_TERM] = "";                                  // Insure there is always a non-null value.
            pageArray[sourceRowIndex][COL_INDEX_ANATOMY_ID] = "";                                    // Insure there is always a non-null value.
            pageArray[sourceRowIndex][COL_INDEX_ANATOMY_ID_LINK] = "";                               // Insure there is always a non-null value.
            pageArray[sourceRowIndex][COL_INDEX_ANATOMY_SYNONYMS] = "";                              // Insure there is always a non-null value.

            for (WebElement bodyRowElements : bodyRowElementsList) {
                AnatomyRow anatomyRow = new AnatomyRow();
                List<WebElement> bodyRowElementList = bodyRowElements.findElements(By.cssSelector("td"));
                WebElement anatomyColElement = bodyRowElementList.get(0);
                WebElement anatomyColAnchorElement = bodyRowElementList.get(0).findElement(By.cssSelector("a"));

                // In order to see the contents of the span, we need to first bring the anatomy term into view, then
                // hover over it.
                Actions builder = new Actions(driver);
                try {
                    testUtils.scrollToTop(driver, anatomyColElement, -50);                  // Scroll anatomy term into view.
                    Actions hoverOverTerm = builder.moveToElement(anatomyColElement);
                    hoverOverTerm.perform();

                    anatomyRow.anatomyIdLink = anatomyColAnchorElement.getAttribute("href");                            // anatomyIdLink
                    int pos = anatomyRow.anatomyIdLink.lastIndexOf("/");
                    anatomyRow.anatomyTerm = anatomyColAnchorElement.getText();                                         // anatomyTerm
                    anatomyRow.anatomyId = anatomyRow.anatomyIdLink.substring(pos + 1);                                 // anatomyId

                    List<WebElement> subinfoElement = bodyRowElementList.get(0).findElements(By.cssSelector("div.subinfo"));
                    if ( ! subinfoElement.isEmpty()) {
                        String[] parts = subinfoElement.get(0).getText().split(":");
                        switch (parts[0].trim().toLowerCase()) {
                            case "synonym":
                                // This handles a single synonym only. Multiple synonyms pass through this path but parts[1] has a newline in that case.
                                if ((parts.length > 1) && ( ! parts[1].contains("\n"))) {
                                    anatomyRow.synonyms.add(parts[1].trim());                                           // single synonym
                                }

                            default:
                                break;
                        }
                    }

                    List<WebElement> synonymElements = bodyRowElementList.get(0).findElements(By.cssSelector("ul.synonym li")); // Look for multiple synonyms.
                    for (WebElement synonymElement : synonymElements) {
                        anatomyRow.synonyms.add(synonymElement.getText().trim());
                    }

                } catch (Exception e) {
                    logger.error("EXCEPTION: SearchAnatomyTable.load() while waiting to hover. Error message: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }

                pageArray[sourceRowIndex][COL_INDEX_ANATOMY_TERM] = anatomyRow.anatomyTerm;
                pageArray[sourceRowIndex][COL_INDEX_ANATOMY_ID] = anatomyRow.anatomyId;
                pageArray[sourceRowIndex][COL_INDEX_ANATOMY_ID_LINK] = anatomyRow.anatomyIdLink;
                pageArray[sourceRowIndex][COL_INDEX_ANATOMY_SYNONYMS] = StringUtils.join(anatomyRow.synonyms, "|");

                sourceRowIndex++;
                bodyRows.add(anatomyRow);
            }
        }

        return new GridMap(pageArray, target);
    }


    // PRIVATE CLASSES
    
    
    private class AnatomyRow {
        private String anatomyId = "";
        private String anatomyIdLink = "";
        private String anatomyTerm = "";
        private List<String> synonyms = new ArrayList();
    
        // NOTE: We don't need these (as sorting the arrays does not solve the problem). However, I'm leaving these in
        //       because they are a good example of how to sort String[][] objects.
        // o1 and o2 are of type String[].
        private class PageComparator implements Comparator<String[]> {

            @Override
            public int compare(String[] o1, String[] o2) {
                if ((o1 == null) && (o2 == null))
                    return 0;
                if (o1 == null)
                    return -1;
                if (o2 == null)
                    return 1;

                // We're only interested in the COL_INDEX_ANATOMY_TERM column.
                String op1 = ((String[])o1)[COL_INDEX_ANATOMY_TERM];
                String op2 = ((String[])o2)[COL_INDEX_ANATOMY_TERM];

                if ((op1 == null) && (op2 == null))
                    return 0;
                if (op1 == null)
                    return -1;
                if (op2 == null)
                    return 1;

                return op1.compareTo(op2);
            }
        }

        // o1 and o2 are of type String[].
        private class DownloadComparator implements Comparator<String[]> {

            @Override
            public int compare(String[] o1, String[] o2) {
                if ((o1 == null) && (o2 == null))
                    return 0;
                if (o1 == null)
                    return -1;
                if (o2 == null)
                    return 1;

                // We're only interested in the DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM column.
                String op1 = ((String[])o1)[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM];
                String op2 = ((String[])o2)[DownloadSearchMapAnatomy.COL_INDEX_ANATOMY_TERM];

                if ((op1 == null) && (op2 == null))
                    return 0;
                if (op1 == null)
                    return -1;
                if (op2 == null)
                    return 1;

                return op1.compareTo(op2);
            }
        }
    }

}