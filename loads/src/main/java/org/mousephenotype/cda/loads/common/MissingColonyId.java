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

package org.mousephenotype.cda.loads.common;

/**
 * This is a DTO class intended to capture the important fields from the cda missing_colony_ids table.
 *
 * Created by mrelac on 22/11/2017.
 */
public class MissingColonyId {
    private int    id;
    private String colonyId;
    private int    logLevel;
    private String reason;

    public MissingColonyId() {

    }

    public MissingColonyId(String colonyId, int logLevel, String reason) {
        this.colonyId = colonyId;
        this.logLevel = logLevel;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getColonyId() {
        return colonyId;
    }

    public void setColonyId(String colonyId) {
        this.colonyId = colonyId;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "MissingColonyId{" +
                "id=" + id +
                ", colonyId='" + colonyId + '\'' +
                ", logLevel=" + logLevel +
                ", reason='" + reason + '\'' +
                '}';
    }
}
