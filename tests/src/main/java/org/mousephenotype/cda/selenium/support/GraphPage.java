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
 * @author mrelac
 *         <p>
 *         This abstract class encapsulates the code and data necessary to represent the important,
 *         common components of a graph page, such as: allele, background, phenotyping center,
 *         pipeline name, metadata group, procedure name and parameter name, and
 *         parameterStableId.
 *         <p>
 *         Currently there are no collections or links shared by all
 *         graphs; for those, consult subclasses such as
 *         GraphPageCategorical and GraphPageUnidimensional.
 */
public class GraphPage {
//
//    protected final String baseUrl;
//    protected final CommonUtils commonUtils = new CommonUtils();
//    protected final WebDriver driver;
//    protected final List<GraphSection> graphSections = new ArrayList();
//    protected final String graphUrl;
//    protected final TestUtils testUtils = new TestUtils();
//    protected final WebDriverWait wait;
//
//    /**
//     * Creates a new <code>GraphPage</code> instance
//     *
//     * @param driver               <code>WebDriver</code> instance
//     * @param wait                 <code>WebDriverWait</code> instance
//     * @param graphUrl             url of graph page to load
//     * @param baseUrl              the base url pointing to the downloads
//     * @throws TestException
//     */
//    public GraphPage(WebDriver driver, WebDriverWait wait, String graphUrl, String baseUrl) throws TestException {
//        this.driver = driver;
//        this.wait = wait;
//        this.graphUrl = graphUrl;
//        this.baseUrl = baseUrl;
//
//        driver.get(graphUrl);
//        load();
//    }
//
//    public RunStatus validate() throws TestException {
//        RunStatus status = new RunStatus();
//
//        for (GraphSection graphSection : graphSections) {
//            status.add(graphSection.validate());
//        }
//
//        return status;
//    }
//
//
//    // GETTERS AND SETTERS
//
//
//    public List<GraphSection> getGraphSections() {
//        return graphSections;
//    }
//
//
//    // PRIVATE METHODS
//
//
//    private boolean hasDownloadLinks() {
//        List<WebElement> elements = driver.findElements(By.xpath("//div[@id='exportIconsDivGlobal']"));
//        return (!elements.isEmpty());
//    }
//
//    /**
//     * Load the page and its section and tsv/xls download data.
//     */
//    private void load() throws TestException {
//        String message;
//        List<WebElement> chartElements;
//        List<WebElement> tableElements;
//
//        // The wait is dependent on the chart icon type: postQcTable, postQcGraph, preqc
//        String postQcXpathTable = "//div[@class='inner']/div[@id='histopath_wrapper']";
//        String postQcXpathChart = "//div[@class='chart']";
//        String preqcXpathOk = "//div[@class='viz-tools']";
//        String preqcXpathHung = "//div[@id='loading-app']";
//        List<WebElement> postQcTableElementList = driver.findElements(By.xpath(postQcXpathTable));
//        List<WebElement> postQcGraphElementList = driver.findElements(By.xpath(postQcXpathChart));
//        List<WebElement> preQcOkElementList = driver.findElements(By.xpath(preqcXpathOk));
//        List<WebElement> preQcHungElementList = driver.findElements(By.xpath(preqcXpathHung));
//
//        chartElements = new ArrayList<>();
//        tableElements = new ArrayList<>();
//        if ( ! postQcTableElementList.isEmpty()) {
//            System.out.println("WAITING FOR postQcTable");
//            tableElements.addAll(wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(postQcXpathTable))));
//        }
//        if ( ! postQcGraphElementList.isEmpty()) {
//            System.out.println("WAITING FOR postQcGraph");
//            chartElements.addAll(wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(postQcXpathChart))));
//        }
//        if ( ! preQcOkElementList.isEmpty()) {
//            System.out.println("WAITING FOR preQcOk");
//            chartElements.addAll(wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(preqcXpathOk))));
//        }
//
//        // If the page is a chart, load the GraphSections.
//        for (WebElement chartElement : chartElements) {
//            GraphSection graphSection = GraphSectionFactory.createGraphSection(driver, wait, graphUrl, chartElement);
//            graphSections.add(graphSection);
//        }
//
//        // If the page has download links, parse each download section into its
//        // own DownloadSection. Each such section contains a map with two keys:
//        // "tsv" and "xls". Each value points to its [tsv or xls] data.
//        if (hasDownloadLinks()) {
//            List<DownloadSection> downloadSections;
//            try {
//                downloadSections = loadAllDownloadData();
//            } catch (TestException te) {
//                throw te;
//            } catch (Exception e) {
//                message = "Exception: " + e.getLocalizedMessage() + "\nURL: " + graphUrl;
//                System.out.println(message);
//                throw new TestException(message, e);
//            }
//
//            // For each GraphSection, compare the heading's pageKey with the set
//            // of keys for each download section until found. If found, bind
//            // that download section to the graph section; otherwise, throw an
//            // exception indicating the expected key wasn't found.
//            for (GraphSection graphSection : graphSections) {
//                GraphHeading heading = graphSection.getHeading();
//                graphSection.setDownloadSection(null);
//                String pageKey = heading.getMutantKey();
//
//                List<Set<String>> downloadKeysSet = new ArrayList();
//                for (DownloadSection downloadSection : downloadSections) {
//                    Set<String> downloadKeys = downloadSection.getKeys(heading.chartType, DownloadType.XLS);
//                    if (downloadKeys.contains(pageKey)) {
//                        graphSection.setDownloadSection(downloadSection);
//                        break;
//                    } else {
//                        downloadKeysSet.add(downloadKeys);
//                    }
//                }
//                if (graphSection.getDownloadSection() == null) {
//                    String setContents = "";
//                    for (Set<String> downloadKeys : downloadKeysSet) {
//                        if (!setContents.isEmpty())
//                            setContents += "\n\n";
//                        setContents += testUtils.dumpSet(downloadKeys);
//                    }
//                    message = "ERROR: target " + graphUrl + "\nExpected page mutantKey '" + pageKey
//                            + "' but was not found. Set:\n" + setContents;
//                    System.out.println(message);
//                    throw new TestException(message);
//                }
//            }
//        }
//    }
//
//    /**
//     * Parse the tsv and xls download files. When successfully completed, the
//     * map will contain two key/value pairs: one keyed "tsv" and one keyed
//     * "xls". Each value contains a list of download data by section, where a
//     * section is identified as starting with a column heading.
//     *
//     * @return two key/value pairs: one keyed "tsv" and one keyed "xls". Each
//     * value contains a list of download data by section, where a section is
//     * identified as starting with a column heading.
//     * @throws Exception
//     */
//    public List<DownloadSection> loadAllDownloadData() throws Exception {
//        List<DownloadSection> retVal = new ArrayList();
//
//        // Extract the TSV data.
//        // Typically baseUrl is a fully-qualified hostname and path, such as http://ves-ebi-d0:8080/mi/impc/dev/phenotype-arcihve.
//        // getDownloadTargetUrlBase() typically returns a path of the form '/mi/impc/dev/phenotype-archive/export?xxxxxxx...'.
//        // To create the correct url for the stream, replace everything in downloadTargetUrlBase before '/export?' with the baseUrl.
//        String downloadTargetUrlBase = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='exportIconsDivGlobal']"))).getAttribute("data-exporturl");
//        String downloadTargetTsv = testUtils.patchUrl(baseUrl, downloadTargetUrlBase + "tsv", "/export?");
//
//        // Get the download stream data.
//        List<List<List<String>>> downloadBlockTsv = new ArrayList();
//        List<List<List<String>>> downloadBlockXls = new ArrayList();
//
//        URL url;
//        try {
//            url = new URL(downloadTargetTsv);
//
//        } catch (IOException e) {
//            throw new TestException("EXCEPTION creating url '" + downloadTargetTsv + "': ", e);
//        }
//
//        try (DataReaderTsv dataReaderTsv = new DataReaderTsv(url)) {
//            String[][] allGraphData = dataReaderTsv.getData();
//
//            if (allGraphData.length > 0) {
//                downloadBlockTsv = parseDownloadStream(allGraphData);
//            }
//
//        } catch (IOException e) {
//            throw new TestException("Error parsing TSV", e);
//        }
//        // Extract the XLS data.
//        String downloadTargetXls = testUtils.patchUrl(baseUrl, downloadTargetUrlBase + "xls", "/export?");
//
//        try {
//            url = new URL(downloadTargetXls);
//
//        } catch (IOException e) {
//            throw new TestException("EXCEPTION creating url '" + downloadTargetTsv + "': ", e);
//        }
//
//        try (DataReaderXls dataReaderXls = new DataReaderXls(url)) {
//            String[][] allGraphData = dataReaderXls.getData();
//
//            if (allGraphData.length > 0) {
//                downloadBlockXls = parseDownloadStream(allGraphData);
//            }
//
//        } catch (IOException e) {
//            throw new TestException("Error parsing XLS", e);
//        }
//
//        for (int i = 0; i < downloadBlockTsv.size(); i++) {
//            Map<DownloadType, List<List<String>>> downloadDataMap = new HashMap();
//
//            downloadDataMap.put(DownloadType.TSV, downloadBlockTsv.get(i));
//            downloadDataMap.put(DownloadType.XLS, downloadBlockXls.get(i));
//            DownloadSection downloadSection = new DownloadSection(downloadDataMap);
//            retVal.add(downloadSection);
//        }
//
//        return retVal;
//    }
//
//    /**
//     * Given the full download data set, this method parses it, separating it
//     * into a separate dataset for each graph. Each graph's dataset is preceeded
//     * by row of column headings.
//     * <p>
//     * Dependency: This code depends on the first column of the first row
//     * matching the string 'pipeline name'.
//     *
//     * @param allGraphData the full download data set
//     * @return a list of download data set chunks, one for every graph
//     */
//    private List<List<List<String>>> parseDownloadStream(String[][] allGraphData) {
//        List<List<List<String>>> retVal = new ArrayList<>();
//        List<List<String>> dataBlock = new ArrayList<>();
//
//        for (String[] row : allGraphData) {
//            if (isHeading(row)) {
//                if (!dataBlock.isEmpty()) {
//                    retVal.add(dataBlock);
//                    dataBlock = new ArrayList<>();
//                }
//            }
//
//            if (!isBlank(row)) {                                              // Skip blank lines.
//                dataBlock.add(Arrays.asList(row));
//            }
//        }
//
//        if (!dataBlock.isEmpty()) {
//            retVal.add(dataBlock);
//        }
//
//        return retVal;
//    }
//
//    /**
//     * Returns true if the line described by <code>row</code> is a heading;
//     * false otherwise.
//     *
//     * @param row the line to be queried for a heading
//     * @return true if the line described by <code>row</code> is a heading;
//     * false otherwise.
//     */
//    public static boolean isHeading(String[] row) {
//        return (row[0].equals("pipeline name"));
//    }
//
//    /**
//     * Returns true if the line described by <code>row</code> is a heading;
//     * false otherwise.
//     *
//     * @param row the line to be queried for a heading
//     * @return true if the line described by <code>row</code> is a heading;
//     * false otherwise.
//     */
//    public static boolean isHeading(List<String> row) {
//        return isHeading(row.toArray(new String[0]));
//    }
//
//    public static boolean isBlank(String[] row) {
//        return ((row == null) || (row[0].isEmpty()));
//    }
}