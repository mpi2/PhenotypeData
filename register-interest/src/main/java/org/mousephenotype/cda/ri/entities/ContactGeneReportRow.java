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

import java.util.Date;

/**
 * Created by mrelac on 13/03/2018.
 */
public class ContactGeneReportRow {
    private String contactEmail;
    private Date contactCreatedAt;
    private String markerSymbol;
    private String mgiAccessionId;
    private Date geneInterestCreatedDate;

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Date getContactCreatedAt() {
        return contactCreatedAt;
    }

    public void setContactCreatedAt(Date contactCreatedAt) {
        this.contactCreatedAt = contactCreatedAt;
    }

    public String getMarkerSymbol() {
        return markerSymbol;
    }

    public void setMarkerSymbol(String markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public String getMgiAccessionId() {
        return mgiAccessionId;
    }

    public void setMgiAccessionId(String mgiAccessionId) {
        this.mgiAccessionId = mgiAccessionId;
    }

    public Date getGeneInterestCreatedDate() {
        return geneInterestCreatedDate;
    }

    public void setGeneInterestCreatedDate(Date geneInterestCreatedDate) {
        this.geneInterestCreatedDate = geneInterestCreatedDate;
    }

    @Override
    public String toString() {
        return "ContactGeneReport{" +
                "contactEmail='" + contactEmail + '\'' +
                ", contactCreatedAt=" + contactCreatedAt +
                ", markerSymbol='" + markerSymbol + '\'' +
                ", mgiAccessionId='" + mgiAccessionId + '\'' +
                ", geneInterestCreatedDate=" + geneInterestCreatedDate +
                '}';
    }
}