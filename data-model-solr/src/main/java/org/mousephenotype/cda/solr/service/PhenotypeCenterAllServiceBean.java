/*******************************************************************************
 * Copyright © 2019 EMBL - European Bioinformatics Institute
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

package org.mousephenotype.cda.solr.service;

/**
 * This is the DTO used with the PhenotypeCenterService. It holds the service components.
 */
public class PhenotypeCenterAllServiceBean {

    public String center;
    public String colonyId;
    public String zygosity;
    public String geneAccessionId;
    public String geneSymbol;
    public String alleleAccessionId;
    public String alleleSymbol;
    public String procedureStableId;
    public String procedureName;
    public String parameterStableId;
    public String parameterName;
    public String topLevelMpTermId;
    public String topLevelMpTermName;
    public String mpTermId;
    public String mpTermName;
    public String lifeStageName;
    public String status;


    public PhenotypeCenterAllServiceBean() {

    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getColonyId() {
        return colonyId;
    }

    public void setColonyId(String colonyId) {
        this.colonyId = colonyId;
    }

    public String getZygosity() {
        return zygosity;
    }

    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    public String getGeneAccessionId() {
        return geneAccessionId;
    }

    public void setGeneAccessionId(String geneAccessionId) {
        this.geneAccessionId = geneAccessionId;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public void setGeneSymbol(String geneSymbol) {
        this.geneSymbol = geneSymbol;
    }

    public String getAlleleAccessionId() {
        return alleleAccessionId;
    }

    public void setAlleleAccessionId(String alleleAccessionId) {
        this.alleleAccessionId = alleleAccessionId;
    }

    public String getAlleleSymbol() {
        return alleleSymbol;
    }

    public void setAlleleSymbol(String alleleSymbol) {
        this.alleleSymbol = alleleSymbol;
    }

    public String getProcedureStableId() {
        return procedureStableId;
    }

    public void setProcedureStableId(String procedureStableId) {
        this.procedureStableId = procedureStableId;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getParameterStableId() {
        return parameterStableId;
    }

    public void setParameterStableId(String parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getTopLevelMpTermId() {
        return topLevelMpTermId;
    }

    public void setTopLevelMpTermId(String topLevelMpTermId) {
        this.topLevelMpTermId = topLevelMpTermId;
    }

    public String getTopLevelMpTermName() {
        return topLevelMpTermName;
    }

    public void setTopLevelMpTermName(String topLevelMpTermName) {
        this.topLevelMpTermName = topLevelMpTermName;
    }

    public String getMpTermId() {
        return mpTermId;
    }

    public void setMpTermId(String mpTermId) {
        this.mpTermId = mpTermId;
    }

    public String getMpTermName() {
        return mpTermName;
    }

    public void setMpTermName(String mpTermName) {
        this.mpTermName = mpTermName;
    }

    public String getLifeStageName() {
        return lifeStageName;
    }

    public void setLifeStageName(String lifeStageName) {
        this.lifeStageName = lifeStageName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}