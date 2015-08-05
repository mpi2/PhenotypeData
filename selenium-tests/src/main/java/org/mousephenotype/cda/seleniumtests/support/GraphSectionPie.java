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
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

/**
 *
 * @author mrelac
 * 
 * This class encapsulates the code and data necessary to validate a Pie graph 
 * section.
 */
@Component
public class GraphSectionPie extends GraphSection {

    public GraphSectionPie() {

    }

    /**
     * Loads the <code>GraphSectionPie</code> instance
     *
     * @param graphUrl the graph url
     * @param chartElement <code>WebElement</code> pointing to the HTML
     *                     div.chart element of the pie chart section.
     * @param timeoutInSeconds the wait timeout
     *
     * @throws TestException
     */
    public void load(String graphUrl, WebElement chartElement, long timeoutInSeconds) throws TestException {
        super.load(graphUrl, chartElement, timeoutInSeconds);
    }
    
    @Override
    public PageStatus validate() throws TestException {
        PageStatus status = super.validate();                                   // Validate common components.
        
        return status;
    }
}