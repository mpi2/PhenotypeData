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

public class OntologyTermAnomaly {
    private int                       id;
    private String                    dbName;
    private String                    tableName;
    private String                    ontologyAccColumnName;
    private String                    originalAcc;
    private String                    replacementAcc;
    private OntologyTermAnomalyReason reason;
    private Date                      last_modified;

    public OntologyTermAnomaly() {

    }

    public OntologyTermAnomaly(String originalAcc, String replacementAcc, OntologyTermAnomalyReason reason) {
        this.originalAcc = originalAcc;
        this.replacementAcc = replacementAcc;
        this.reason = reason;
    }

    public OntologyTermAnomaly(String dbName, String tableName, String ontologyAccColumnName, String originalAcc, String replacementAcc, OntologyTermAnomalyReason reason) {
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

    public OntologyTermAnomalyReason getReason() {
        return reason;
    }

    public void setReason(OntologyTermAnomalyReason reason) {
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
        String result;

        switch (reason) {
            case NOT_FOUND_HAS_ALTERNATE_ID:
                result = "Term " + originalAcc + " is missing but is an alternate id for " + replacementAcc + ".";
                break;

            case NOT_FOUND_HAS_MULTIPLE_ALTERNATE_IDS:
                result = "Term " + originalAcc + " is missing and has multiple alternate ids.";
                break;

            case NOT_FOUND_INVALID_ALTERNATE_ID:
                result = "Term " + originalAcc + " is missing and has invalid alternate id " + replacementAcc + ".";
                break;

            case NOT_FOUND_NO_OTHER_ID:
                result = "Term " + originalAcc + " is missing and there is no replacement/alternative term.";
                break;

            case OBSOLETE_HAS_CONSIDER_ID:
                result = "Term " + originalAcc + " is obsolete and was replaced by consider id " + replacementAcc + ".";
                break;

            case OBSOLETE_INVALID_CONSIDER_ID:
                result = "Term " + originalAcc + " is obsolete and has invalid consider id " + replacementAcc + ".";
                break;

            case OBSOLETE_HAS_MULTIPLE_CONSIDER_IDS:
                result = "Term " + originalAcc + " is obsolete and has multiple consider ids.";
                break;

            case OBSOLETE_HAS_REPLACEMENT:
                result = "Term " + originalAcc + " is obsolete and was replaced by replacement id " + replacementAcc + ".";
                break;

            case OBSOLETE_HAS_INVALID_REPLACEMENT:
                result = "Term " + originalAcc + " has invalid replacement term " + replacementAcc + ".";
                break;

            case OBSOLETE_NO_OTHER_ID:
                result = "Term " + originalAcc + " is obsolete and has no replacement/consider id term.";
                break;

            default:
                result = "ERROR: Undefined reason '" + reason.toString() + ".";
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OntologyTermAnomaly that = (OntologyTermAnomaly) o;

        if (originalAcc != null ? !originalAcc.equals(that.originalAcc) : that.originalAcc != null) return false;
        if (replacementAcc != null ? !replacementAcc.equals(that.replacementAcc) : that.replacementAcc != null)
            return false;
        return reason == that.reason;

    }

    @Override
    public int hashCode() {
        int result = originalAcc != null ? originalAcc.hashCode() : 0;
        result = 31 * result + (replacementAcc != null ? replacementAcc.hashCode() : 0);
        result = 31 * result + (reason != null ? reason.hashCode() : 0);
        return result;
    }
}