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

import org.mousephenotype.cda.selenium.exception.TestException;
import org.mousephenotype.cda.solr.web.dto.GraphTestDTO;
import org.mousephenotype.cda.utilities.RunStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 *
 * @author mrelac
 *
 * This class encapsulates the code and data necessary to validate a preqc
 * graph. Since this is not an EBI graph, there is no need to extend from
 * GraphValidator.
 */
public class GraphValidatorPreqc {

    protected final TestUtils testUtils = new TestUtils();

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getCanonicalName());

    public RunStatus validate(WebDriver driver, GenePage genePage, GraphTestDTO geneGraph) throws TestException {
        RunStatus status = new RunStatus();
        String message;

        List<String> urls = genePage.getGraphUrls(GenePage.GraphUrlType.PREQC);
        for (String url : urls) {

            // If the graph page doesn't load, log it.
            driver.get(url);
            // Make sure there is a div.viz-tools.
            List<WebElement> elements = driver.findElements(By.xpath("//div[@class='viz-tools' or @class='phenodcc-heatmap']"));
            if (elements.isEmpty()) {
                message = "ERROR: " + "\n\tpreQc graph[ " + geneGraph.getProcedureParameterName()
                        + "] failed to load.  procedureName: " + geneGraph.getProcedureName()
                        + ". parameterName: " + geneGraph.getParameterName() + ". URL:\n"
                        + url + ". Gene page: " + genePage.getTarget() + "\n[FAILED]";
                status.addError(message);
            }
        }

        return status;
    }
}
