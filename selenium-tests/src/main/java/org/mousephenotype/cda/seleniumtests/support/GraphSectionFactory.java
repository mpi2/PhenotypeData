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
import org.mousephenotype.cda.web.ChartType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author mrelac
 */
@Component
public class GraphSectionFactory {

    protected WebDriver driver;
    protected WebDriverWait wait;

    @Autowired
    GraphSectionABR graphSectionABR;

    @Autowired
    GraphSectionCategorical graphSectionCategorical;

    @Autowired
    GraphSectionPie graphSectionPie;

    @Autowired
    GraphSectionTimeSeries graphSectionTimeSeries;

    @Autowired
    GraphSectionUnidimensional graphSectionUnidimensional;

    @Autowired
    SeleniumWrapper wrapper;

    public GraphSectionFactory() {

    }

    /**
     * Creates a new <code>GraphPage</code> instance of the type specified
     * by <code>chartType</code>.
     *
     * @param graphUrl the graph url
     * @param chartElement The ABR <code>WebElement</code>
     * @param timeoutInSeconds the wait timeout
     * 
     * @return the graph section appropriate to the chart type as defined by the chart element.
     * 
     * @throws TestException a new <code>GraphPage</code> instance of the type specified
     * by <code>chartType</code>.
     */
    public GraphSection createGraphSection(String graphUrl, WebElement chartElement, long timeoutInSeconds) throws TestException {
        this.driver = wrapper.getDriver();
        this.wait = new WebDriverWait(driver, timeoutInSeconds);
        ChartType chartType = GraphSection.getChartType(chartElement);
        GraphSection graphSection;

        switch (chartType) {
            case CATEGORICAL_STACKED_COLUMN:
                graphSection = graphSectionCategorical;
                break;
                
            case PIE:
                graphSection = graphSectionPie;
                break;
                
            case TIME_SERIES_LINE:
            case TIME_SERIES_LINE_BODYWEIGHT:
                graphSection = graphSectionTimeSeries;
                break;
                
            case UNIDIMENSIONAL_ABR_PLOT:
                graphSection = graphSectionABR;
                break;
                
            case UNIDIMENSIONAL_BOX_PLOT:
            case UNIDIMENSIONAL_SCATTER_PLOT:
                graphSection = graphSectionUnidimensional;
                break;
                
            default:
                throw new TestException("Unknown chart type " + chartType);
        }

        graphSection.load(graphUrl, chartElement, timeoutInSeconds);
        return graphSection;
    }
}
