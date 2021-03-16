/*******************************************************************************
 * Copyright © 2018 EMBL - European Bioinformatics Institute
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 ******************************************************************************/

package org.mousephenotype.cda.ri.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/*
 * This class encapsulates the code and data necessary to represent the results of a register interest GET for a single
 * contact from the register interest web service. The data item contains all of the detail fields used to manage the
 * contact's genes of registered interest in the summary.jsp.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Summary {

    protected String emailAddress;
    protected List<Gene> genes = new ArrayList<>();


    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public void setGenes(List<Gene> genes) {
        this.genes = genes;
    }

    @Override
    public String toString() {
        return "Summary{" +
                "emailAddress='" + emailAddress + '\'' +
                ", genes=" + genes +
                '}';
    }
}