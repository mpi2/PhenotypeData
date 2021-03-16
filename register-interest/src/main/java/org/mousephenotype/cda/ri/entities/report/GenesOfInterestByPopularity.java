/*******************************************************************************
 * Copyright © 2017 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.ri.entities.report;

import java.util.Date;

public class GenesOfInterestByPopularity {
    private String mgiAccessionId;
    private String symbol;
    private String assignedTo;
    private String assignmentStatus;
    private Date   assignmentStatusDate;
    private int    subscriberCount;
    private String subscribers;

    public String getMgiAccessionId() {
        return mgiAccessionId;
    }

    public void setMgiAccessionId(String mgiAccessionId) {
        this.mgiAccessionId = mgiAccessionId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public Date getAssignmentStatusDate() {
        return assignmentStatusDate;
    }

    public void setAssignmentStatusDate(Date assignmentStatusDate) {
        this.assignmentStatusDate = assignmentStatusDate;
    }

    public int getSubscriberCount() {
        return subscriberCount;
    }

    public void setSubscriberCount(int subscriberCount) {
        this.subscriberCount = subscriberCount;
    }

    public String getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(String subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String toString() {
        return "GenesOfInterestByPopularity{" +
                "mgiAccessionId='" + mgiAccessionId + '\'' +
                ", symbol='" + symbol + '\'' +
                ", assignedTo='" + assignedTo + '\'' +
                ", assignmentStatus='" + assignmentStatus + '\'' +
                ", assignmentStatusDate=" + assignmentStatusDate +
                ", subscriberCount=" + subscriberCount +
                ", subscribers='" + subscribers + '\'' +
                '}';
    }
}