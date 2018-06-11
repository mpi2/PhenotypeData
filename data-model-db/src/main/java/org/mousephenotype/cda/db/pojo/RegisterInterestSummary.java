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

package org.mousephenotype.cda.db.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * This class encapsulates the code and data necessary to represent a single Register Interest data item (row) fetched
 * from the register interest web service. The data item contains all of the detail fields used to manage a user's
 * genes of registered interest in the registerInterestSummary.jsp.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterInterestSummary {

    private String geneSymbol;
    private String mgiAccessionId;
    private String assignmentStatus;
    private String nullAlleleProductionStatus;
    private String conditionalAlleleProductionStatus;
    private boolean phenotypingDataAvailable;

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public String getMgiAccessionId() {
        return mgiAccessionId;
    }

    public void setMgiAccessionId(String mgiAccessionId) {
        this.mgiAccessionId = mgiAccessionId;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public String getNullAlleleProductionStatus() {
        return nullAlleleProductionStatus;
    }

    public void setNullAlleleProductionStatus(String nullAlleleProductionStatus) {
        this.nullAlleleProductionStatus = nullAlleleProductionStatus;
    }

    public String getConditionalAlleleProductionStatus() {
        return conditionalAlleleProductionStatus;
    }

    public void setConditionalAlleleProductionStatus(String conditionalAlleleProductionStatus) {
        this.conditionalAlleleProductionStatus = conditionalAlleleProductionStatus;
    }

    public boolean isPhenotypingDataAvailable() {
        return phenotypingDataAvailable;
    }

    public void setPhenotypingDataAvailable(boolean phenotypingDataAvailable) {
        this.phenotypingDataAvailable = phenotypingDataAvailable;
    }

    @Override
    public String toString() {
        return "RegisterInterestSummary{" +
                "geneSymbol='" + geneSymbol + '\'' +
                ", mgiAccessionId='" + mgiAccessionId + '\'' +
                ", assignmentStatus='" + assignmentStatus + '\'' +
                ", nullAlleleProductionStatus='" + nullAlleleProductionStatus + '\'' +
                ", conditionalAlleleProductionStatus='" + conditionalAlleleProductionStatus + '\'' +
                ", phenotypingDataAvailable=" + phenotypingDataAvailable +
                '}';
    }
}
