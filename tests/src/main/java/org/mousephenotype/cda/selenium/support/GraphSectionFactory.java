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
 */
public class GraphSectionFactory {

//    protected TestUtils testUtils = new TestUtils();
//
//    /**
//     * Creates a new <code>GraphPage</code> instance of the type specified
//     * by <code>chartType</code>.
//     *
//     * @param driver <code>WebDriver</code> instance
//     * @param wait <code>WebDriverWait</code> instance
//     * @param graphUrl the graph url
//     * @param chartElement The ABR <code>WebElement</code>
//     *
//     * @return
//     *
//     * @throws TestException a new <code>GraphPage</code> instance of the type specified
//     * by <code>chartType</code>.
//     */
//    public static GraphSection createGraphSection(WebDriver driver, WebDriverWait wait, String graphUrl, WebElement chartElement) throws TestException {
//        ChartType chartType = GraphSection.getChartType(chartElement);
//        switch (chartType) {
//            case CATEGORICAL_STACKED_COLUMN:
//                return new GraphSectionCategorical(driver, wait, graphUrl, chartElement);
//
//            case PIE:
//                return new GraphSectionPie(driver, wait, graphUrl, chartElement);
//
//            case TIME_SERIES_LINE:
//            case TIME_SERIES_LINE_BODYWEIGHT:
//                return new GraphSectionTimeSeries(driver, wait, graphUrl, chartElement);
//
//            case UNIDIMENSIONAL_ABR_PLOT:
//                return new GraphSectionABR(driver, wait, graphUrl, chartElement);
//
//            case UNIDIMENSIONAL_BOX_PLOT:
//            case UNIDIMENSIONAL_SCATTER_PLOT:
//                return new GraphSectionUnidimensional(driver, wait, graphUrl, chartElement);
//
//            default:
//                throw new TestException("Unknown chart type " + chartType);
//        }
//    }
}