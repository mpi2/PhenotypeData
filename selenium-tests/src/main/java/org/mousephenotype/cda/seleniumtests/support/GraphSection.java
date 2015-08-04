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
import org.mousephenotype.cda.utilities.CommonUtils;
import org.mousephenotype.cda.web.ChartType;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to represent a single
 * graph page section containing one graph. A GraphSection contains the
 * following:
 * <ul>
 * <li>title (Required. Begins with 'Allele -')</li>
 * <li>heading (Required. E.g. components such as Background, Phenotyping
 *     Center, Pipeline, etc. Not all components are required)</li>
 * <li>summary (Optional. e.g. categorical graph sections have a table with id
 *     'catTable'. Unidimensional graph sections have a table with id
 *     'continuousTable')</li>
 * </ul>
 */
@Component
public abstract class GraphSection {
    protected WebElement chartElement;
    protected ChartType chartType = null;
    protected WebDriver driver;
    protected String graphUrl;
    protected MoreStatisticsLink moreStatisticsLink;
    protected WebDriverWait wait;

    @Autowired
    protected GraphCatTable catTable;

    @Autowired
    protected CommonUtils commonUtils;

    @Autowired
    protected GraphContinuousTable continuousTable;

    @Autowired
    protected DownloadSection downloadSection;

    @Autowired
    protected GraphGlobalTestTable globalTestTable;

    @Autowired
    GraphCatTable graphCatTable;

    @Autowired
    protected GraphHeading heading;

    @Autowired
    protected PhenotypePipelineDAO phenotypePipelineDAO;

    @Autowired
    protected TestUtils testUtils;

    @Autowired
    SeleniumWrapper wrapper;

    public GraphSection() {

    }
    
    public PageStatus validate() throws TestException {
        PageStatus status = new PageStatus();
        
        // Verify title contains 'Allele'.
        if ( ! getHeading().title.startsWith("Allele -")) {
            status.addError("ERROR: expected title to start with 'Allele -'. Title is '" + getHeading().title + "'. URL: " + graphUrl);
        }
        
        return status;
    }
    
    
    // SETTERS AND GETTERS

    
    public WebElement getChartElement() {
        return chartElement;
    }

    public String getGraphUrl() {
        return graphUrl;
    }

    public GraphCatTable getCatTable() {
        return catTable;
    }

    public GraphContinuousTable getContinuousTable() {
        return continuousTable;
    }

    public DownloadSection getDownloadSection() {
        return downloadSection;
    }

    public void setDownloadSection(DownloadSection downloadSection) {
        this.downloadSection = downloadSection;
    }

    public GraphGlobalTestTable getGlobalTestTable() {
        return globalTestTable;
    }

    public GraphHeading getHeading() {
        return heading;
    }

    public MoreStatisticsLink getMoreStatisticsLink() {
        return moreStatisticsLink;
    }
    
    
    // PRIVATE METHODS

    
    /**
     * Given a chart element, returns the ChartType.
     * 
     * @return the ChartType
     * @param chartElement The chart <code>WebElement</code>.
     * 
     * @throws TestException
     */
    public static ChartType getChartType(WebElement chartElement) throws TestException {
        ChartType chartTypeLocal = null;
        String graphUrlTag = chartElement.getAttribute("graphurl");
        String[] parts = graphUrlTag.split(Pattern.quote("&"));
        String chartTypeValue = "";
        
        for (String part : parts) {
            if (part.startsWith("chart_type")) {
                chartTypeValue = part.replace("chart_type=", "");
                chartTypeLocal = ChartType.valueOf(chartTypeValue);
                break;
            }
        }
        
        if (chartTypeLocal == null) {
            throw new TestException("GraphSection.getChartType: Invalid chart type '" + chartTypeValue + "'.");
        }
        
        return chartTypeLocal;
    }

    /**
     * Load the section data.
     *
     * @param graphUrl the graph url
     * @param chartElement the chart <code>WebElement</code>
     * @param timeoutInSeconds the wait timeout
     *
     * @throws TestException
     */
    public void load(String graphUrl, WebElement chartElement, long timeoutInSeconds) throws TestException {
        this.driver = wrapper.getDriver();
        this.wait = new WebDriverWait(driver, timeoutInSeconds);
        this.graphUrl = graphUrl;
        this.chartElement = chartElement;
        this.chartType = getChartType(chartElement);

        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='section']/div[@class='inner']//div[@class='highcharts-container']")));
            
            List<WebElement> elements = chartElement.findElements(By.xpath(".//table[starts-with(@id, 'catTable')]"));
            if ( ! elements.isEmpty()) {
                graphCatTable.load(elements.get(0));
            }
            
            elements = chartElement.findElements(By.xpath("./table[starts-with(@id, 'continuousTable')]"));
            if ( ! elements.isEmpty()) {
                this.continuousTable = continuousTable.load(elements.get(0));
            }
            
            // Scrape this graph's data off the page.
            this.heading = new GraphHeading(wait, chartElement, graphUrl, chartType);
            
            elements = chartElement.findElements(By.xpath("./p/a/i[starts-with(@id, 'toggle_table_buttondivChart_')]"));
            if ( ! elements.isEmpty()) {
                moreStatisticsLink = new MoreStatisticsLink(chartElement);
            }
            
            elements = chartElement.findElements(By.xpath("./table[starts-with(@id, 'globalTest')]"));
            if ( ! elements.isEmpty()) {
                this.globalTestTable = new GraphGlobalTestTable(graphUrl, elements.get(0));
            }
            
        } catch (NoSuchElementException | TimeoutException te ) {
            System.out.println("Expected graph page url but found none. Graph URL:\n\t" + graphUrl);
            throw te;
        } catch (Exception e) {
            String message = "EXCEPTION processing page: " + e.getLocalizedMessage() + ". Graph URL:\n\t" + graphUrl;
            System.out.println(message);
            throw new TestException(message, e);
        }
    }
    
    /**
     * This class encapsulates the code and data to represent the
     * 'More statistics' WebElement link.
     */
    public class MoreStatisticsLink {
        private final WebElement chartElement;
        private final String moreStatisticsIXpath   = ".//p/a/i[starts-with(@id, 'toggle_table_buttondivChart_')]"; // xpath to this section's 'more statistics' link.
                                                                                                                    // xpath to this section's 'more statistics' link contents.
        private final String moreStatisticsDivXpath = ".//div[starts-with(@id, 'toggle_tabledivChart_')] | .//div[starts-with(@id, 'toggle_timetabledivChart_')]";
    
        public MoreStatisticsLink(WebElement chartElement) {
            this.chartElement = chartElement;
        }
        
        public PageStatus validate() {
            PageStatus status = new PageStatus();
            List<WebElement> moreStatisticsList = chartElement.findElements(By.xpath(moreStatisticsIXpath));
            if (moreStatisticsList.isEmpty()) {
                status.addError("ERROR: Expected 'More statistics' link but wasn't found. URL: " + graphUrl);
            } else {
                WebElement moreStatisticsIElement = moreStatisticsList.get(0);
                WebElement moreStatisticsDivElement = chartElement.findElement(By.xpath(moreStatisticsDivXpath));
                String style = moreStatisticsDivElement.getAttribute("style");
                if ( ! style.equals("display: none;"))
                    status.addError("ERROR: Expected 'More statistics' drop-down to start collapsed.");

                moreStatisticsIElement.click();
                wait.until(ExpectedConditions.visibilityOf(moreStatisticsDivElement));
                style = moreStatisticsDivElement.getAttribute("style");
                if ( ! style.contains("display: block;"))
                    status.addError("ERROR: Expected 'More statistics' drop-down to be expanded.");

                moreStatisticsIElement.click();
                
                // Sometimes the following 'wait' doesn't wait long enough. Wrap it in a loop.
                for (int i = 0; i < 10; i++) {
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(moreStatisticsDivXpath)));
                    style = moreStatisticsDivElement.getAttribute("style");
                    if (style.contains("display: none;"))
                        break;
                    else
                        commonUtils.sleep(50);
                }
                if ( ! style.contains("display: none;"))
                    status.addError("ERROR: Expected 'More statistics' drop-down to be collapsed.");
            }
            
            return status;
        }
    }
}