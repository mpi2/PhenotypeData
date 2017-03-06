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

import org.apache.commons.lang.StringUtils;
import org.mousephenotype.cda.selenium.exception.TestException;
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
 * components of a search page 'geneGrid' HTML table for genes.
 */
@Deprecated
public class SearchGeneTable extends SearchFacetTable {

    protected GridMap pageData;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final int COL_INDEX_GENE_SYMBOL           = 0;
    public static final int COL_INDEX_HUMAN_ORTHOLOG        = 1;
    public static final int COL_INDEX_GENE_ID               = 2;
    public static final int COL_INDEX_GENE_NAME             = 3;
    public static final int COL_INDEX_GENE_SYNONYMS         = 4;
    public static final int COL_INDEX_PRODUCTION_STATUS     = 5;
    public static final int COL_INDEX_PHENOTYPE_STATUS      = 6;
    public static final int COL_INDEX_PHENOTYPE_STATUS_LINK = 7;
    public static final int COL_INDEX_LAST = COL_INDEX_PHENOTYPE_STATUS_LINK;        // Should always point to the last (highest-numbered) index.
    
    private final List<GeneRow> bodyRows = new ArrayList();
    
    private final static Map<TableComponent, By> map = new HashMap();
    static {
        map.put(TableComponent.BY_TABLE, By.xpath("//table[@id='geneGrid']"));
        map.put(TableComponent.BY_TABLE_TR, By.xpath("//table[@id='geneGrid']/tbody/tr"));
        map.put(TableComponent.BY_SELECT_GRID_LENGTH, By.xpath("//select[@name='geneGrid_length']"));
    }

    /**
     * Creates a new <code>SearchGeneTable</code> instance.
     *
     * @param driver A valid <code>WebDriver</code> instance
     * @param timeoutInSeconds timeout
     */
    public SearchGeneTable(WebDriver driver, int timeoutInSeconds) throws TestException {
        super(driver, timeoutInSeconds, map);

        pageData = load();
    }

    /**
     * Validates download data against this <code>SearchGeneTable</code>
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
              COL_INDEX_GENE_SYMBOL
            , COL_INDEX_HUMAN_ORTHOLOG
            , COL_INDEX_GENE_ID
            , COL_INDEX_GENE_NAME
            , COL_INDEX_GENE_SYNONYMS
//            , COL_INDEX_PRODUCTION_STATUS                             Not all production statuses can be scraped off the page, so there's nothing accurate to compare to.
            , COL_INDEX_PHENOTYPE_STATUS
            , COL_INDEX_PHENOTYPE_STATUS_LINK
        };
        final Integer[] downloadColumns = {
              DownloadSearchMapGenes.COL_INDEX_GENE_SYMBOL
            , DownloadSearchMapGenes.COL_INDEX_HUMAN_ORTHOLOG
            , DownloadSearchMapGenes.COL_INDEX_GENE_ID
            , DownloadSearchMapGenes.COL_INDEX_GENE_NAME
            , DownloadSearchMapGenes.COL_INDEX_GENE_SYNONYM
//            , DownloadSearchMapGenes.COL_INDEX_PRODUCTION_STATUS      Not all production statuses can be scraped off the page, so there's nothing accurate to compare to.
            , DownloadSearchMapGenes.COL_INDEX_PHENOTYPE_STATUS
            , DownloadSearchMapGenes.COL_INDEX_PHENOTYPE_STATUS_LINK
        };
        
        return validateDownloadInternal(pageData, pageColumns, downloadDataArray, downloadColumns, driver.getCurrentUrl());
    }


    // PRIVATE METHODS


    /**
     * Pulls all rows of data and column access variables from the search page's 'geneGrid' HTML table.
     *
     * @return <code>numRows</code> rows of data and column access variables from the search page's phenotype HTML table.
     */
    private GridMap load() {
        return load(null);
    }

    /**
     * Pulls <code>numRows</code> rows of search page gene facet data and column access variables from the search page's
     * 'geneGrid' HTML table.
     *
     * @param numRows the number of <code>GridMap</code> table rows to return, including the heading row. To specify all
     *                rows, set <code>numRows</code> to null.
     *
     * @return <code>numRows</code> rows of search page gene facet data and column access variables from the search
     * page's phenotype HTML table.
     */
    private GridMap load(Integer numRows) {
        if (numRows == null)
            numRows = computeTableRowCount();

        String[][] pageArray;

        // Wait for page.
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("table#geneGrid")));
        int numCols = COL_INDEX_LAST + 1;

        pageArray = new String[numRows][numCols];                               // Allocate space for the data.
        for (int i = 0; i < numCols; i++) {
            pageArray[0][i] = "Column_" + i;                                    // Set the headings.
        }

        // Save the body values.
        List<WebElement> bodyRowElementsList = table.findElements(By.cssSelector("tbody tr"));
        if ( ! bodyRowElementsList.isEmpty()) {
            int sourceRowIndex = 1;

            for (WebElement bodyRowElements : bodyRowElementsList) {
                GeneRow geneRow = new GeneRow();
                List<WebElement> bodyRowElementList= bodyRowElements.findElements(By.cssSelector("td"));
                WebElement titleDivElement = bodyRowElementList.get(0).findElement(By.cssSelector("div.geneCol div.title a"));
                String href = titleDivElement.getAttribute("href");
                WebElement geneColElement = bodyRowElementList.get(0).findElement(By.cssSelector("div.geneCol"));

                // Look for phenotype status.
                List<WebElement> phenotypeStatusElements = bodyRowElementList.get(2).findElements(By.xpath("./a[contains(@class, 'phenotypingStatus')]"));
                List<String> phenotypeStatus = new ArrayList();
                List<String> phenotypeStatusLink = new ArrayList();
                for (WebElement phenotypeStatusElement : phenotypeStatusElements) {
                    phenotypeStatus.add(phenotypeStatusElement.getText().trim());
                    phenotypeStatusLink.add(phenotypeStatusElement.getAttribute("href"));
                }

                GeneDetails geneDetails = new GeneDetails(geneColElement);
                geneRow.geneSymbol = titleDivElement.findElement(By.cssSelector("span.gSymbol")).getText().trim();      // geneSymbol
                geneRow.humanOrthologs = geneDetails.humanOrthologs;                                                    // humanOrtholog list
                geneRow.geneId = href.substring(href.lastIndexOf("/") + 1).trim();                                      // geneId
                geneRow.geneName = geneDetails.name;                                                                    // geneName
                geneRow.synonyms = geneDetails.synonyms;                                                                // synonym list
                geneRow.phenotypeStatus = phenotypeStatus;                                                              // phenotypeStatus
                geneRow.phenotypeStatusLink = phenotypeStatusLink;                                                      // phenotypeStatusLink

                pageArray[sourceRowIndex][COL_INDEX_GENE_SYMBOL] = geneRow.geneSymbol;
                pageArray[sourceRowIndex][COL_INDEX_HUMAN_ORTHOLOG] = StringUtils.join(geneRow.humanOrthologs, "|");
                pageArray[sourceRowIndex][COL_INDEX_GENE_ID] = geneRow.geneId;
                pageArray[sourceRowIndex][COL_INDEX_GENE_NAME] = geneRow.geneName;
                pageArray[sourceRowIndex][COL_INDEX_GENE_SYNONYMS] = StringUtils.join(geneRow.synonyms, "|");
                pageArray[sourceRowIndex][COL_INDEX_PHENOTYPE_STATUS] = StringUtils.join(geneRow.phenotypeStatus, "|");
                pageArray[sourceRowIndex][COL_INDEX_PHENOTYPE_STATUS_LINK] = StringUtils.join(geneRow.phenotypeStatusLink, "|");

                sourceRowIndex++;
                bodyRows.add(geneRow);
            }
        }

        return new GridMap(pageArray, target);
    }


    // PRIVATE CLASSES
    
    
    /**
     * This class encapsulates the code and data necessary to parse and extract the
     * search page gene details found when you hover the mouse over the gene symbol.
     * There may be any of: [gene] name, human ortholog, and 0 or more synonyms.
     */
    private class GeneDetails {
        private String name = "";
        private List<String> humanOrthologs = new ArrayList<>();
        private List<String> synonyms = new ArrayList<>();
        
        public GeneDetails(WebElement geneColElement) {        
            
            // In order to see the contents of the span, we need to first bring
            // the gene symbol into view, then hover over it.
            Actions builder = new Actions(driver);

            try {
                testUtils.scrollToTop(driver, geneColElement, -50);             // Scroll gene symbol into view.
                Actions hoverOverGene = builder.moveToElement(geneColElement);
                hoverOverGene.perform();                                        // Hover over the gene symbol.
                
                List<WebElement> humanOrthologElements = geneColElement.findElements(By.cssSelector("div.subinfo ul.ortholog li"));
                if ( ! humanOrthologElements.isEmpty()) {
                    for (WebElement humanOrthologElement : humanOrthologElements) {
                        humanOrthologs.add(humanOrthologElement.getText());
                    }
                } else {
                    String[] rawHumanOrthologStrings = geneColElement.findElement(By.cssSelector("div.subinfo")).getText().split("\n");
                    for (String humanOrthologString : rawHumanOrthologStrings) {
                        String[] humanOrthologParts = humanOrthologString.split(":");
                        if (humanOrthologParts[0].trim().equals("human ortholog")) {
                            humanOrthologs.add(humanOrthologParts[1].trim());
                            break;
                        }
                    }
                }

                String subinfoDivText = geneColElement.getText();
                String[] subinfoDivLines = subinfoDivText.split("\n");
                for (String subinfoDivLine : subinfoDivLines) {
                    String[] textParts = subinfoDivLine.split(":");
                    switch (textParts[0].trim().toLowerCase()) {
                        case "name":
                            this.name = textParts[1].trim();                    // geneName
                            break;

                        case "synonym":
                            // This handles a single synonym only. Multiple synonyms pass through this path but textParts has only 1 element.
                            if (textParts.length > 1) {
                                synonyms.add(textParts[1].trim());              // single synonym
                            }
                            
                        default:
                            break;
                    }
                 }

                // Look for multiple synonyms.
                List<WebElement> synonymElements = geneColElement.findElements(By.cssSelector("ul.synonym li"));
                for (WebElement synonymElement : synonymElements) {
                    synonyms.add(synonymElement.getText().trim());
                }

            } catch (Exception e) {
                logger.error("EXCEPTION: SearchGeneTable.GeneDetails.GeneDetails() while waiting to hover. Error message: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
    
    private class GeneRow {
        private String geneId = "";
        private String geneSymbol = "";
        private List<String> humanOrthologs = new ArrayList();
        private String geneName = "";
        private List<String> synonyms = new ArrayList();
        private List<PhenotypeArchiveStatus> productionStatus = new ArrayList();
        private List<String> phenotypeStatus;
        private List<String> phenotypeStatusLink = new ArrayList<>();
        
        @Override
        public String toString() {
            return "geneId: '" + geneId
                 + "'  geneSymbol: '" + geneSymbol
                 + "'  humanOrtholog: '" + toStringHumanOrthologs()
                 + "'  geneName: '" + geneName
                 + "'  synonyms: '" + toStringSynonyms()
                 + "'  productionStatus: '" + toStringProductionStatus()
                 + "'  phenotypeStatus: " + (phenotypeStatus == null ? "<null>" : "[" + StringUtils.join(phenotypeStatus, ",") + "]")
                 + "'  phenotypeStatusLink: '" + phenotypeStatusLink + "'";
        }
        
        public String toStringHumanOrthologs() {
            String retVal = "";
            
            for (int i = 0; i < humanOrthologs.size(); i++) {
                if (i > 0)
                    retVal += ", ";
                retVal += humanOrthologs.get(i);
            }
            
            return retVal;
        }
        
        public String toStringSynonyms() {
            String retVal = "";
            
            for (int i = 0; i < synonyms.size(); i++) {
                if (i > 0)
                    retVal += ", ";
                retVal += synonyms.get(i);
            }
            
            return retVal;
        }
        
        public String toStringProductionStatus() {
            String retVal = "";
            
            for (int i = 0; i < productionStatus.size(); i++) {
                if (i > 0)
                    retVal += ", ";
                retVal += productionStatus.get(i).mpName + "[" + productionStatus.get(i).mpClass + "]";
            }
            
            return retVal;
        }
    }
    
    /**
     * This enum is meant to emulate the css classes used for production and
     * phenotype statuses. They are lowercase to exactly reflect the css class
     * names.
     */
    public enum PhenotypeArchiveStatusClass {
        done,
        inprogress,
        none,
        qc
    }
    
    /**
     * This class is meant to encapsulate the code and data necessary to represent
     * mouse phenotype status and corresponding css class (which determines button/
     * label display color).
     */
    private class PhenotypeArchiveStatus {
        private String mpName;
        private PhenotypeArchiveStatusClass mpClass;
        private String mpHoverText;
        
        /**
         * This constructor takes a <code>WebElement</code> instance pointing to
         * the status <b>a</b> anchor element, if there is one, parsing it and
         * returning a properly initialized object containing the status name
         * and css class.
         *
         * @param anchorElement The phenotype status <b>a</b> element.
         * <p>
         * NOTE: If this <b>a</b> element has no status, mpName and mpClass will be
         * initialized to null.
         */
        public PhenotypeArchiveStatus(WebElement anchorElement) {
            mpName = null;
            mpClass = null;
            mpHoverText = null;
            
            
            try {
                if (anchorElement != null) {
                    mpName = anchorElement.findElement(By.cssSelector("span")).getText();
                    mpClass = PhenotypeArchiveStatusClass.valueOf(anchorElement.getAttribute("class").split(" ")[1]);
                    mpHoverText = anchorElement.getAttribute("oldtitle");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}