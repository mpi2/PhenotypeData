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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This abstract class encapsulates the common code and data necessary to
 * validate a graph section. Subclasses handle validation for specific graph
 * types.
 * 
 * @author mrelac
 */
@Component
public abstract class GraphValidator {

    @Autowired
    protected GraphSection graphSection;

    public static final String IMPC_PIPELINE = "IMPC Pipeline";

    public GraphValidator() {
        
    }

    public GraphSection getGraphSection() {
        return graphSection;
    }

    public void setGraphSection(GraphSection graphSection) {
        this.graphSection = graphSection;
    }
    
    
    public PageStatus validate() throws TestException {
        return graphSection.getHeading().validate();
    }
}
