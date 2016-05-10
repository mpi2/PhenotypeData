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

import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.utilities.UrlUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author mrelac
 *
 * This class encapsulates the code and data necessary for access to the gene
 * page's "genes" HTML table.
 */
public class GeneTable {

    private CommonUtils commonUtils = new CommonUtils();
    private GridMap data;       // Contains postQc rows only.
    protected WebDriver driver;
    protected Paginator paginator;
    private List<List<String>> postQcList;
    private List<List<String>> preAndPostQcList;
    private List<List<String>> preQcList;
    protected String target;
    protected TestUtils testUtils = new TestUtils();
    protected UrlUtils urlUtils = new UrlUtils();
    protected WebDriverWait wait;

    // These are used to parse the page. Keep them private. Callers should be using the public definitions below this group.
    private static final int COL_INDEX_GENES_PAGE_PHENOTYPE                  =  0;
    private static final int COL_INDEX_GENES_PAGE_ALLELE                     =  1;
    private static final int COL_INDEX_GENES_PAGE_ZYGOSITY                   =  2;
    private static final int COL_INDEX_GENES_PAGE_SEX                        =  3;
    private static final int COL_INDEX_GENES_PAGE_LIFE_STAGE                 =  4;
    private static final int COL_INDEX_GENES_PAGE_PROCEDURE_PARAMETER        =  5;
    private static final int COL_INDEX_GENES_PAGE_PHENOTYPING_CENTER_SOURCE  =  6;
    private static final int COL_INDEX_GENES_PAGE_P_VALUE                    =  7;
    private static final int COL_INDEX_GENES_PAGE_GRAPH_LINK                 =  8;

    // These are used to parameterise the page after the compound columns have been split out (for comparison against the download files).
    public static final int COL_INDEX_GENES_PHENOTYPE                  =   0;
    public static final int COL_INDEX_GENES_ALLELE                     =   1;
    public static final int COL_INDEX_GENES_ZYGOSITY                   =   2;
    public static final int COL_INDEX_GENES_SEX                        =   3;
    public static final int COL_INDEX_GENES_LIFE_STAGE                 =   4;
    public static final int COL_INDEX_GENES_PROCEDURE                  =   5;
    public static final int COL_INDEX_GENES_PARAMETER                  =   6;
    public static final int COL_INDEX_GENES_PHENOTYPING_CENTER         =   7;
    public static final int COL_INDEX_GENES_SOURCE                     =   8;
    public static final int COL_INDEX_GENES_P_VALUE                    =   9;
    public static final int COL_INDEX_GENES_GRAPH_LINK                 =  10;

    public static final String COL_GENES_PHENOTYPE                  = "Phenotype";
    public static final String COL_GENES_ALLELE                     = "Allele";
    public static final String COL_GENES_ZYGOSITY                   = "Zygosity";
    public static final String COL_GENES_SEX                        = "Sex";
    public static final String COL_GENES__LIFE_STAGE                = "Life Stage";
    public static final String COL_GENES_PROCEDURE_PARAMETER        = "Procedure | Parameter";
    public static final String COL_GENES_PHENOTYPING_CENTER_SOURCE  = "Phenotyping Center | Source";
    public static final String COL_GENES_P_VALUE                    = "P Value";
    public static final String COL_GENES_GRAPH                      = "Graph";

    public static final List<Integer> expandColumnList = new ArrayList<>(
        Arrays.asList(new Integer[] { COL_INDEX_GENES_PAGE_PROCEDURE_PARAMETER, COL_INDEX_GENES_PAGE_PHENOTYPING_CENTER_SOURCE }));

    public static final String NO_SUPPORTING_DATA                   = "No supporting data supplied.";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public GeneTable(WebDriver driver, WebDriverWait wait, String target) {
        this.driver = driver;
        this.wait = wait;
        this.target = target;
        this.data = null;
        this.paginator = new Paginator("genes_paginate", driver);
    }

    /**
     * Query to see if HTML table with id 'genes' exists and is not empty.
     * @return true if genes table exists and is not empty; false otherwise
     */
    public boolean genesTableIsNotEmpty() {
        List<WebElement> elements = driver.findElements(By.xpath("//table[@id='genes']/tbody/tr"));
        return ( ! elements.isEmpty());
    }

    /**
     * @return a <code>GridMap</code> containing the data and column access
     * variables that were loaded by the last call to <code>load()</code>.
     */
    public GridMap getData() {
        return data;
    }

    /**
     * Pulls all rows of data and column access variables from the gene page's
     * 'genes' HTML table.
     *
     * @return <code>numRows</code> rows of data and column access variables
     * from the gene page's 'genes' HTML table.
     */
    public GridMap load() {
        return load(null);
    }

    /**
     * Pulls <code>numRows</code> rows of postQc data and column access
     * variables from the gene page's 'genes' HTML table.
     *
     * @param numRows the number of postQc phenotype table rows to return,
     * including the heading row. To specify all postQc rows, set
     * <code>numRows</code> to null.
     * @return <code>numRows</code> rows of data and column access variables
     * from the gene page's 'genes' HTML table.
     */
    public GridMap load(Integer numRows) {
        if (numRows == null)
            numRows = computeTableRowCount();

        String[][] dataArray;
        preQcList = new ArrayList();
        postQcList = new ArrayList();
        preAndPostQcList = new ArrayList();
        String value;

        // Wait for page.
        WebElement genesTable = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#genes")));

        // Grab the headings.
        List<WebElement> headings = genesTable.findElements(By.cssSelector("thead tr th"));
        numRows = Math.min(computeTableRowCount(), numRows);                    // Take the lesser of: actual row count in HTML table (including heading), or requested numRows.
        int numCols = headings.size();

        dataArray = new String[numRows][numCols];                               // Allocate space for the data.
        int sourceColIndex = 0;
        for (WebElement heading : headings) {                                   // Copy the heading values.
            dataArray[0][sourceColIndex] = heading.getText();
            sourceColIndex++;
        }
        preQcList.add(Arrays.asList(dataArray[0]));
        postQcList.add(Arrays.asList(dataArray[0]));
        preAndPostQcList.add(Arrays.asList(dataArray[0]));
        // Loop through all of the tr objects for this page, gathering the data.
        int sourceRowIndex = 1;

        for (WebElement row : genesTable.findElements(By.xpath("//table[@id='genes']/tbody/tr"))) {
            List<WebElement> cells = row.findElements(By.cssSelector("td"));
            boolean isPreQcLink = false;
            sourceColIndex = 0;
            boolean skipLink = false;
            for (WebElement cell : cells) {
                value = "";
                if (sourceColIndex == COL_INDEX_GENES_PAGE_PHENOTYPE) {
                    List<WebElement> elements = cell.findElements(By.cssSelector("a"));
                    if ( ! elements.isEmpty()) {
                        value = elements.get(0).getText();
                    } else {
                        value = cell.getText();
                    }
                } else if (sourceColIndex == COL_INDEX_GENES_PAGE_ALLELE) {
                    String rawAllele = cell.getText();
                    List<WebElement> supList = cell.findElements(By.cssSelector("sup"));
                    if (supList.isEmpty()) {
                        value = rawAllele;
                    } else {
                        String sup = supList.get(0).getText();
                        AlleleParser ap = new AlleleParser(rawAllele, sup);
                        value = ap.toString();
                    }
                } else if (sourceColIndex == COL_INDEX_GENES_PAGE_SEX) {              // Translate the male/female symbol into a string: 'male', 'female', or 'both'.
                    List<WebElement> sex = cell.findElements(By.xpath("img[@alt='Male' or @alt='Female']"));
                    if ( ! sex.isEmpty()) {
                        if (sex.size() == 2) {
                            value = "both";
                        } else {
                            value = sex.get(0).getAttribute("alt").toLowerCase();
                        }
                    }
                } else if (sourceColIndex == COL_INDEX_GENES_PAGE_GRAPH_LINK) {                    // Extract the graph url from the <a> anchor and decode it.
                    // NOTE: Graph links are disabled if there is no supporting data.
                    List<WebElement> graphLinks = cell.findElements(By.cssSelector("a"));
                    value = "";
                    if ( ! graphLinks.isEmpty()) {
                        value = graphLinks.get(0).getAttribute("href");
                    } else {
                        graphLinks = cell.findElements(By.cssSelector("i"));
                        if ( ! graphLinks.isEmpty()) {
                            value = graphLinks.get(0).getAttribute("oldtitle");
                            if (value.contains(NO_SUPPORTING_DATA)) {
                                skipLink = true;
                            }
                        }
                    }

                    isPreQcLink = testUtils.isPreQcLink(value);
                } else {
                    value = cell.getText();
                }

                dataArray[sourceRowIndex][sourceColIndex] = value;
                sourceColIndex++;
            }

            // If the graph link is a postQc link, increment the index and return when we have the number of requested rows.
            if (isPreQcLink) {
                preQcList.add(Arrays.asList(dataArray[sourceRowIndex]));        // Add the row to the preQc list.
            } else {
                if ( ! skipLink) {
                    postQcList.add(Arrays.asList(dataArray[sourceRowIndex]));       // Add the row to the preQc list.
                    if (postQcList.size() >= numRows) {                             // Return when we have the number of requested rows.
                        break;
                    }
                }
            }
            preAndPostQcList.add(Arrays.asList(dataArray[sourceRowIndex]));     // Add the row to the preQc- and postQc-list.
            sourceRowIndex++;
        }

        preQcList = commonUtils.expandCompoundColumns(preQcList, expandColumnList, "|");
        preQcList = commonUtils.expandSexColumn(preQcList, COL_INDEX_GENES_SEX);
        postQcList = commonUtils.expandCompoundColumns(postQcList, expandColumnList, "|");
        postQcList = commonUtils.expandSexColumn(postQcList, COL_INDEX_GENES_SEX);
        preAndPostQcList = commonUtils.expandCompoundColumns(preAndPostQcList, expandColumnList, "|");
        preAndPostQcList = commonUtils.expandSexColumn(preAndPostQcList, COL_INDEX_GENES_SEX);
        data = new GridMap(postQcList, target);
        return data;
    }

    public Paginator getPaginator() {
        return paginator;
    }

    public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }

    public List<List<String>> getPreQcList() {
        return preQcList;
    }

    public List<List<String>> getPostQcList() {
        return postQcList;
    }

    public List<List<String>> getPreAndPostQcList() {
        return preAndPostQcList;
    }

    /**
     *
     * @return the number of rows in the "genes" table. Always include 1 extra for the heading.
     */
    private int computeTableRowCount() {
        // Wait for page.
        List<WebElement> elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@id='genes']/tbody/tr")));
        return elements.size() + 1;
    }

}
