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
 *     'catTable'. Unidimensional graph sections have a table with class
 *     'continuousTable')</li>
 * </ul>
 */
public abstract class GraphSection {
//
//    protected final WebElement chartElement;
//    protected final CommonUtils commonUtils = new CommonUtils();
//    protected DownloadSection downloadSection;
//    protected final WebDriver driver;
//    protected final String graphUrl;
//    protected final WebDriverWait wait;
//
//    private GraphCatTable catTable = null;
//    private GraphContinuousTable continuousTable;
//    private GraphGlobalTestTable globalTestTable;
//    private GraphHeading heading;
//    private MoreStatisticsLink moreStatisticsLink;
//    private ChartType chartType = null;
//    private boolean statisticsFailed = false;
//
//
//    @Autowired
//    DatasourceRepository datasourceRepository;
//
//    @Autowired
//    OntologyTermRepository ontologyTermRepository;
//
//    @Autowired
//    ParameterRepository parameterRepository;
//
//
//    /**
//     * Creates a new <code>GraphSection</code> instance
//     *
//     * @param driver <code>WebDriver</code> instance
//     * @param wait <code>WebDriverWait</code> instance
//     * @param graphUrl the graph url
//     * @param chartElement <code>WebElement</code> pointing to the HTML
//     *                     div.chart element
//     */
//    public GraphSection(WebDriver driver, WebDriverWait wait, String graphUrl, WebElement chartElement) throws TestException {
//        this.driver = driver;
//        this.wait = wait;
//        this.graphUrl = graphUrl;
//        this.chartElement = chartElement;
//        this.chartType = getChartType(chartElement);
//
//        load();
//    }
//
//    public RunStatus validate() throws TestException {
//        RunStatus status = new RunStatus();
//
//        // Verify title contains 'Allele'.
//        if ( ! getHeading().title.startsWith("Allele -")) {
//            status.addError("ERROR: expected title to start with 'Allele -'. Title is '" + getHeading().title + "'. URL: " + graphUrl);
//        }
//
//        return status;
//    }
//
//
//    // SETTERS AND GETTERS
//
//
//    public WebElement getChartElement() {
//        return chartElement;
//    }
//
//    public String getGraphUrl() {
//        return graphUrl;
//    }
//
//    public GraphCatTable getCatTable() {
//        return catTable;
//    }
//
//    public GraphContinuousTable getContinuousTable() {
//        return continuousTable;
//    }
//
//    public DownloadSection getDownloadSection() {
//        return downloadSection;
//    }
//
//    public void setDownloadSection(DownloadSection downloadSection) {
//        this.downloadSection = downloadSection;
//    }
//
//    public GraphGlobalTestTable getGlobalTestTable() {
//        return globalTestTable;
//    }
//
//    public GraphHeading getHeading() {
//        return heading;
//    }
//
//    public MoreStatisticsLink getMoreStatisticsLink() {
//        return moreStatisticsLink;
//    }
//
//    public boolean statisticsFailed() {
//        return this.statisticsFailed;
//    }
//
//
//    // PRIVATE METHODS
//
//
//    /**
//     * Given a chart element, returns the ChartType.
//     *
//     * @return the ChartType
//     * @param chartElement The chart <code>WebElement</code>.
//     *
//     * @throws TestException
//     */
//    public static ChartType getChartType(WebElement chartElement) throws TestException {
//        ChartType chartTypeLocal = null;
//        String graphUrlTag = chartElement.getAttribute("graphurl");
//        String[] parts = graphUrlTag.split(Pattern.quote("&"));
//        String chartTypeValue = "";
//
//        for (String part : parts) {
//            if (part.startsWith("chart_type")) {
//                chartTypeValue = part.replace("chart_type=", "");
//                chartTypeLocal = ChartType.valueOf(chartTypeValue);
//                break;
//            }
//        }
//
//        if (chartTypeLocal == null) {
//            throw new TestException("GraphSection.getChartType: Invalid chart type '" + chartTypeValue + "'.");
//        }
//
//        return chartTypeLocal;
//    }
//
//    /**
//     * Load the section data.
//     */
//    private void load() throws TestException {
//
//        try {
//            String chartXpath = "//div[@class='section']/div[@class='inner']/div[@class='chart']";
//            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(chartXpath)));
//
//            // Unidimensional box graphs sometimes aren't yet loaded when we get to this point. If this is one, wait for it to load.
//            ChartType chartType = getChartType(chartElement);
//            if (chartType == ChartType.UNIDIMENSIONAL_BOX_PLOT) {
//                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[contains(@class, 'continuousTable')]")));
//            }
//
//            List<WebElement> elements = chartElement.findElements(By.xpath(".//table[starts-with(@id, 'catTable')]"));
//            if ( ! elements.isEmpty()) {
//                this.catTable = new GraphCatTable(elements.get(0));
//            }
//
//            for (int i = 0; i < 5; i++) {
//                elements = chartElement.findElements(By.xpath("./table[starts-with(@class, 'continuousTable')]"));
//                if ( ! elements.isEmpty()) {
//                    this.continuousTable = new GraphContinuousTable(elements.get(0));
//                    break;
//                } else {
//                    if ((elements.isEmpty()) && (chartType == ChartType.UNIDIMENSIONAL_BOX_PLOT)) {
//                        commonUtils.sleep(5000);
//                    }
//                }
//            }
//
//            // Scrape this graph's data off the page.
//            this.heading = new GraphHeading(chartElement, chartType, datasourceRepository, graphUrl, ontologyTermRepository, parameterRepository, wait);
//
//            elements = chartElement.findElements(By.xpath("./p/a/i[starts-with(@id, 'toggle_table_buttondivChart_')]"));
//            if ( ! elements.isEmpty()) {
//                moreStatisticsLink = new MoreStatisticsLink(chartElement);
//            }
//
//            elements = chartElement.findElements(By.xpath("./table[starts-with(@class, 'globalTest')]"));
//            if ( ! elements.isEmpty()) {
//                this.globalTestTable = new GraphGlobalTestTable(graphUrl, elements.get(0));
//            }
//
//            elements = driver.findElements(By.xpath("//div[@class='alert']"));
//            if ( ! elements.isEmpty()) {
//                if (elements.get(0).getText().contains("Statistics Failed")) {
//                    statisticsFailed = true;
//                }
//            }
//
//        } catch (NoSuchElementException | TimeoutException te ) {
//            System.out.println("Expected graph page url but found none. Graph URL:\n\t" + graphUrl);
//            throw te;
//        } catch (Exception e) {
//            String message = "EXCEPTION processing page: " + e.getLocalizedMessage() + ". Graph URL:\n\t" + graphUrl;
//            System.out.println(message);
//            throw new TestException(message, e);
//        }
//    }
//
//    /**
//     * This class encapsulates the code and data to represent the
//     * 'More statistics' WebElement link.
//     */
//    public class MoreStatisticsLink {
//        private final WebElement chartElement;
//        private final String moreStatisticsIXpath   = ".//p/a/i[starts-with(@id, 'more_statistics')]"; // xpath to this section's 'more statistics' link.
//        // xpath to this section's 'more statistics' link contents.
//        private final String moreStatisticsDivXpath = ".//div[starts-with(@id, 'toggle_tabledivChart_')] | .//div[starts-with(@id, 'toggle_timetabledivChart_')]";
//
//        public MoreStatisticsLink(WebElement chartElement) {
//            this.chartElement = chartElement;
//        }
//
//        public RunStatus validate() {
//            RunStatus status = new RunStatus();
//            List<WebElement> moreStatisticsList = chartElement.findElements(By.xpath(moreStatisticsIXpath));
//            if (moreStatisticsList.isEmpty()) {
//                status.addError("ERROR: Expected 'More statistics' link but wasn't found. URL: " + graphUrl);
//            } else {
//                WebElement moreStatisticsIElement = moreStatisticsList.get(0);
//                WebElement moreStatisticsDivElement = chartElement.findElement(By.xpath(moreStatisticsDivXpath));
//                String style = moreStatisticsDivElement.getAttribute("style");
//                if ( ! style.equals("display: none;"))
//                    status.addError("ERROR: Expected 'More statistics' drop-down to start collapsed.");
//
//                moreStatisticsIElement.click();
//                wait.until(ExpectedConditions.visibilityOf(moreStatisticsDivElement));
//                style = moreStatisticsDivElement.getAttribute("style");
//                if ( ! style.contains("display: block;"))
//                    status.addError("ERROR: Expected 'More statistics' drop-down to be expanded.");
//
//                moreStatisticsIElement.click();
//
//                // Sometimes the following 'wait' doesn't wait long enough. Wrap it in a loop.
//                for (int i = 0; i < 10; i++) {
//                    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(moreStatisticsDivXpath)));
//                    style = moreStatisticsDivElement.getAttribute("style");
//                    if (style.contains("display: none;"))
//                        break;
//                    else
//                        commonUtils.sleep(500);
//                }
//                if ( ! style.contains("display: none;"))
//                    status.addError("ERROR: Expected 'More statistics' drop-down to be collapsed.");
//            }
//
//            return status;
//        }
//    }
}