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

import org.mousephenotype.cda.exception.TestException;
import org.mousephenotype.cda.utilities.RunStatus;
import org.mousephenotype.cda.web.DownloadType;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent the important
 * components of a search page 'diseaseGrid' HTML table for diseases.
 */
@Deprecated
public class SearchDiseaseTable extends SearchFacetTable {

    private final List<DiseaseRow> bodyRows = new ArrayList();
    private static final Map<TableComponent, By> map = new HashMap();
    protected GridMap pageData;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public static final int COL_INDEX_DISEASE_ID         = 0;
    public static final int COL_INDEX_DISEASE_ID_LINK    = 1;
    public static final int COL_INDEX_DISEASE_NAME       = 2;
    public static final int COL_INDEX_SOURCE             = 3;
    public static final int COL_INDEX_CURATED_HUMAN_OMIM = 4;
    public static final int COL_INDEX_CURATED_MOUSE_MGI  = 5;
    public static final int COL_INDEX_CANDIDATE_IMPC     = 6;
    public static final int COL_INDEX_CANDIDATE_MGI      = 7;
    public static final int COL_INDEX_LAST = COL_INDEX_CANDIDATE_MGI;           // Should always point to the last (highest-numbered) index.

    static {
        map.put(TableComponent.BY_TABLE, By.xpath("//table[@id='diseaseGrid']"));
        map.put(TableComponent.BY_TABLE_TR, By.xpath("//table[@id='diseaseGrid']/tbody/tr"));
        map.put(TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='diseaseGrid_length']"));
    }

    /**
     * Creates a new <code>SearchDiseaseTable</code> instance.
     *
     * @param driver A valid <code>WebDriver</code> instance
     * @param timeoutInSeconds timeout
     */
    public SearchDiseaseTable(WebDriver driver, int timeoutInSeconds) throws TestException {
        super(driver, timeoutInSeconds, map);

        pageData = load();
    }

    /**
     * Validates download data against this <code>SearchDiseaseTable</code>
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
              COL_INDEX_DISEASE_ID
            , COL_INDEX_DISEASE_ID_LINK
            , COL_INDEX_DISEASE_NAME
            , COL_INDEX_SOURCE
            , COL_INDEX_CURATED_HUMAN_OMIM
            , COL_INDEX_CURATED_MOUSE_MGI
            , COL_INDEX_CANDIDATE_IMPC
            , COL_INDEX_CANDIDATE_MGI
        };
        final Integer[] downloadColumns = {
              DownloadSearchMapDiseases.COL_INDEX_DISEASE_ID
            , DownloadSearchMapDiseases.COL_INDEX_DISEASE_ID_LINK
            , DownloadSearchMapDiseases.COL_INDEX_DISEASE_NAME
            , DownloadSearchMapDiseases.COL_INDEX_SOURCE
            , DownloadSearchMapDiseases.COL_INDEX_CURATED_HUMAN_OMIM
            , DownloadSearchMapDiseases.COL_INDEX_CURATED_MOUSE_MGI
            , DownloadSearchMapDiseases.COL_INDEX_CANDIDATE_IMPC
            , DownloadSearchMapDiseases.COL_INDEX_CANDIDATE_MGI
        };
        
        return validateDownloadInternal(pageData, pageColumns, downloadDataArray, downloadColumns, driver.getCurrentUrl());
    }


    // PRIVATE METHODS

    /**
     * Pulls all rows of data and column access variables from the search page's 'diseaseGrid' HTML table.
     *
     * @return <code>numRows</code> rows of data and column access variables from the search page's phenotype HTML table.
     */
    private GridMap load() {
        return load(null);
    }

    /**
     * Pulls <code>numRows</code> rows of search page gene facet data and column access variables from the search page's
     * 'diseaseGrid' HTML table.
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
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#diseaseGrid")));
        int numCols = COL_INDEX_LAST + 1;

        pageArray = new String[numRows][numCols];                               // Allocate space for the data.
        for (int i = 0; i < numCols; i++) {
            pageArray[0][i] = "Column_" + i;                                    // Set the headings.
        }

        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int sourceRowIndex = 1;

            // Insure there is always a non-null value.
            pageArray[sourceRowIndex][COL_INDEX_DISEASE_ID] = "";
            pageArray[sourceRowIndex][COL_INDEX_DISEASE_ID_LINK] = "";
            pageArray[sourceRowIndex][COL_INDEX_DISEASE_NAME] = "";
            pageArray[sourceRowIndex][COL_INDEX_SOURCE] = "";
            pageArray[sourceRowIndex][COL_INDEX_CURATED_HUMAN_OMIM] = "";
            pageArray[sourceRowIndex][COL_INDEX_CURATED_MOUSE_MGI] = "";
            pageArray[sourceRowIndex][COL_INDEX_CANDIDATE_IMPC] = "";
            pageArray[sourceRowIndex][COL_INDEX_CANDIDATE_MGI] = "";

            for (WebElement bodyRowElements : bodyRowElementsList) {
                DiseaseRow diseaseRow = new DiseaseRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                WebElement diseaseColElement = bodyRowElementList.get(0);
                WebElement diseaseColAnchorElement = bodyRowElementList.get(0).findElement(By.cssSelector("a"));

                // In order to see the contents of the span, we need to first bring the anatomy term into view, then
                // hover over it.
                Actions builder = new Actions(driver);
                try {
                    testUtils.scrollToTop(driver, diseaseColElement, -50);                  // Scroll disease term into view.
                    Actions hoverOverTerm = builder.moveToElement(diseaseColElement);
                    hoverOverTerm.perform();

                    diseaseRow.diseaseIdLink = diseaseColAnchorElement.getAttribute("href");
                    int pos = diseaseRow.diseaseIdLink.lastIndexOf("/");
                    diseaseRow.diseaseId = diseaseRow.diseaseIdLink.substring(pos + 1);
                    diseaseRow.diseaseName = diseaseColAnchorElement.getText();
                    diseaseRow.source = bodyRowElementList.get(1).getText();

                    diseaseRow.curatedHumanOmim = bodyRowElements.findElements(By.cssSelector("span.curatedHuman")).isEmpty() ? false : true;
                    diseaseRow.curatedMouseMgi = bodyRowElements.findElements(By.cssSelector("span.curatedMice")).isEmpty() ? false : true;
                    diseaseRow.candidateImpc = bodyRowElements.findElements(By.cssSelector("span.candidateImpc")).isEmpty() ? false : true;
                    diseaseRow.candidateMgi = bodyRowElements.findElements(By.cssSelector("span.candidateMgi")).isEmpty() ? false : true;

                } catch (Exception e) {
                    logger.error("EXCEPTION: SearchAnatomyTable.load() while waiting to hover. Error message: " + e.getLocalizedMessage());
                    e.printStackTrace();
                }

                pageArray[sourceRowIndex][COL_INDEX_DISEASE_ID]         = diseaseRow.diseaseId;
                pageArray[sourceRowIndex][COL_INDEX_DISEASE_ID_LINK]    = diseaseRow.diseaseIdLink;
                pageArray[sourceRowIndex][COL_INDEX_DISEASE_NAME]       = diseaseRow.diseaseName;
                pageArray[sourceRowIndex][COL_INDEX_SOURCE]             = diseaseRow.source;
                pageArray[sourceRowIndex][COL_INDEX_CURATED_HUMAN_OMIM] = (diseaseRow.curatedHumanOmim ? "true" : "false");
                pageArray[sourceRowIndex][COL_INDEX_CURATED_MOUSE_MGI]  = (diseaseRow.curatedMouseMgi  ? "true" : "false");
                pageArray[sourceRowIndex][COL_INDEX_CANDIDATE_IMPC]     = (diseaseRow.candidateImpc    ? "true" : "false");
                pageArray[sourceRowIndex][COL_INDEX_CANDIDATE_MGI]      = (diseaseRow.candidateMgi     ? "true" : "false");

                sourceRowIndex++;
                bodyRows.add(diseaseRow);
            }
        }

        return new GridMap(pageArray, target);
    }


    // PRIVATE CLASSES
    
    
    private class DiseaseRow {
        private String diseaseId = "";
        private String diseaseIdLink = "";
        private String diseaseName = "";
        private String source = "";
        private boolean curatedHumanOmim = false;
        private boolean curatedMouseMgi = false;
        private boolean candidateImpc = false;
        private boolean candidateMgi = false;
    }

}