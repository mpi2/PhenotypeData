/*******************************************************************************
 * Copyright © 2015 EMBL - European Bioinformatics Institute
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

import java.util.Date;

/**
 * Created by mrelac on 04/10/16.
 */

@Deprecated
public class OntologyTermAnomaly {
    private int    id;
    private String dbName;
    private String tableName;
    private String ontologyAccColumnName;
    private String originalAcc;
    private String replacementAcc;
    private String reason;
    private Date   last_modified;

    public OntologyTermAnomaly() {

    }

    public OntologyTermAnomaly(String dbName, String tableName, String ontologyAccColumnName, String originalAcc, String replacementAcc, String reason) {
        this.dbName = dbName;
        this.tableName = tableName;
        this.ontologyAccColumnName = ontologyAccColumnName;
        this.originalAcc = originalAcc;
        this.replacementAcc = replacementAcc;
        this.reason = reason;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOntologyAccColumnName() {
        return ontologyAccColumnName;
    }

    public void setOntologyAccColumnName(String ontologyAccColumnName) {
        this.ontologyAccColumnName = ontologyAccColumnName;
    }

    public String getOriginalAcc() {
        return originalAcc;
    }

    public void setOriginalAcc(String originalAcc) {
        this.originalAcc = originalAcc;
    }

    public String getReplacementAcc() {
        return replacementAcc;
    }

    public void setReplacementAcc(String replacementAcc) {
        this.replacementAcc = replacementAcc;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getLast_modified() {
        return last_modified;
    }

    public void setLast_modified(Date last_modified) {
        this.last_modified = last_modified;
    }

    @Override
    public String toString() {
        return "OntologyTermAnomaly{" +
                "reason='" + reason + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OntologyTermAnomaly that = (OntologyTermAnomaly) o;

        if (dbName != null ? !dbName.equals(that.dbName) : that.dbName != null) return false;
        if (tableName != null ? !tableName.equals(that.tableName) : that.tableName != null) return false;
        if (ontologyAccColumnName != null ? !ontologyAccColumnName.equals(that.ontologyAccColumnName) : that.ontologyAccColumnName != null)
            return false;
        if (originalAcc != null ? !originalAcc.equals(that.originalAcc) : that.originalAcc != null) return false;
        if (replacementAcc != null ? !replacementAcc.equals(that.replacementAcc) : that.replacementAcc != null)
            return false;
        return reason != null ? reason.equals(that.reason) : that.reason == null;

    }

    @Override
    public int hashCode() {
        int result = dbName != null ? dbName.hashCode() : 0;
        result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
        result = 31 * result + (ontologyAccColumnName != null ? ontologyAccColumnName.hashCode() : 0);
        result = 31 * result + (originalAcc != null ? originalAcc.hashCode() : 0);
        result = 31 * result + (replacementAcc != null ? replacementAcc.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        return result;
    }
}