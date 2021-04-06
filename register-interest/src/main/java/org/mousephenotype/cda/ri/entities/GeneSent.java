/*******************************************************************************
 *  Copyright © 2017 EMBL - European Bioinformatics Institute
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License. You may obtain a copy of the License at
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 *  either express or implied. See the License for the specific
 *  language governing permissions and limitations under the
 *  License.
 ******************************************************************************/

package org.mousephenotype.cda.ri.entities;

import org.mousephenotype.cda.ri.pojo.SummaryDetail;

import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 * This entity class maps to the gene_sent table. Since its content is directly related to the
 * SummaryDetail DTO, a translator is provided.
 */

public class GeneSent {
    private int    pk;
    private String address;
    private String geneAccessionId;
    private String symbol;
    private String assignmentStatus;
    private String conditionalAlleleProductionStatus;
    private String crisprAlleleProductionStatus;
    private String nullAlleleProductionStatus;
    private int    phenotypingDataAvailable;
    private Date   createdAt;
    private Date   SentAt;
    private Date   updatedAt;

    public GeneSent() { }

    /**
     *
     * @return a new SummaryDetail instance from this GeneSent instance
     */
    public SummaryDetail toSummaryDetail() {
        SummaryDetail sd = new SummaryDetail();
        sd.setGeneAccessionId(this.geneAccessionId);
        sd.setSymbol(this.symbol);
        sd.setAssignmentStatus(this.assignmentStatus);
        sd.setConditionalAlleleProductionStatus(this.conditionalAlleleProductionStatus);
        sd.setCrisprAlleleProductionStatus(this.crisprAlleleProductionStatus);
        sd.setNullAlleleProductionStatus(this.nullAlleleProductionStatus);
        sd.setPhenotypingDataAvailable(this.phenotypingDataAvailable == 1);
        return sd;
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public static String generateKey(String emailAddress, String geneAccessionId) {
        return emailAddress + "_" + geneAccessionId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGeneAccessionId() {
        return geneAccessionId;
    }

    public void setGeneAccessionId(String geneAccessionId) {
        this.geneAccessionId = geneAccessionId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public String getConditionalAlleleProductionStatus() {
        return conditionalAlleleProductionStatus;
    }

    public void setConditionalAlleleProductionStatus(String conditionalAlleleProductionStatus) {
        this.conditionalAlleleProductionStatus = conditionalAlleleProductionStatus;
    }

    public String getCrisprAlleleProductionStatus() {
        return crisprAlleleProductionStatus;
    }

    public void setCrisprAlleleProductionStatus(String crisprAlleleProductionStatus) {
        this.crisprAlleleProductionStatus = crisprAlleleProductionStatus;
    }

    public String getNullAlleleProductionStatus() {
        return nullAlleleProductionStatus;
    }

    public void setNullAlleleProductionStatus(String nullAlleleProductionStatus) {
        this.nullAlleleProductionStatus = nullAlleleProductionStatus;
    }

    public int getPhenotypingDataAvailable() {
        return phenotypingDataAvailable;
    }

    public void setPhenotypingDataAvailable(int phenotypingDataAvailable) {
        this.phenotypingDataAvailable = phenotypingDataAvailable;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getSentAt() {
        return SentAt;
    }

    public void setSentAt(Date sentAt) {
        SentAt = sentAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "GeneSent{" +
                "pk=" + pk +
                ", address='" + address + '\'' +
                ", geneAccessionId='" + geneAccessionId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", assignmentStatus='" + assignmentStatus + '\'' +
                ", conditionalAlleleProductionStatus='" + conditionalAlleleProductionStatus + '\'' +
                ", crisprAlleleProductionStatus='" + crisprAlleleProductionStatus + '\'' +
                ", nullAlleleProductionStatus='" + nullAlleleProductionStatus + '\'' +
                ", phenotypingDataAvailable='" + phenotypingDataAvailable + '\'' +
                ", SentAt=" + SentAt +
                '}';
    }
}