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
 * This is a DTO class intended to capture the important fields from the DCC experiment table in preparation for
 * insertiion into the cda experiment table.
 *
 * Created by mrelac on 11/11/2016.
 */
public class DccExperimentDTO {
    private String  datasourceShortName;
    private String  experimentId;
    private String  sequenceId;
    private Date    dateOfExperiment;
    private String  centerId;
    private String  pipeline;
    private String  project;
    private String  procedureId;
    private String  colonyId;
    private String  specimenId;
    private String  sex;
    private String  rawProcedureStatus;
    private long    dcc_procedure_pk;
    private boolean isLineLevel;

    public String getDatasourceShortName() {
        return datasourceShortName;
    }

    public void setDatasourceShortName(String datasourceShortName) {
        this.datasourceShortName = datasourceShortName;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getSequenceId() {
        return sequenceId;
    }

    public void setSequenceId(String sequenceId) {
        this.sequenceId = sequenceId;
    }

    public Date getDateOfExperiment() {
        return dateOfExperiment;
    }

    public void setDateOfExperiment(Date dateOfExperiment) {
        this.dateOfExperiment = dateOfExperiment;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    public String getColonyId() {
        return colonyId;
    }

    public void setColonyId(String colonyId) {
        this.colonyId = colonyId;
    }

    public String getSpecimenId() {
        return specimenId;
    }

    public void setSpecimenId(String specimenId) {
        this.specimenId = specimenId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getRawProcedureStatus() {
        return rawProcedureStatus;
    }

    public void setRawProcedureStatus(String rawProcedureStatus) {
        this.rawProcedureStatus = rawProcedureStatus;
    }

    public long getDcc_procedure_pk() {
        return dcc_procedure_pk;
    }

    public void setDcc_procedure_pk(long dcc_procedure_pk) {
        this.dcc_procedure_pk = dcc_procedure_pk;
    }

    public boolean isLineLevel() {
        return isLineLevel;
    }

    public void setLineLevel(boolean lineLevel) {
        isLineLevel = lineLevel;
    }

    @Override
    public String toString() {
        return "DccExperimentDTO{" +
                "datasourceShortName='" + datasourceShortName + '\'' +
                ", experimentId='" + experimentId + '\'' +
                ", sequenceId='" + sequenceId + '\'' +
                ", dateOfExperiment=" + dateOfExperiment +
                ", centerId='" + centerId + '\'' +
                ", pipeline='" + pipeline + '\'' +
                ", project='" + project + '\'' +
                ", procedureId='" + procedureId + '\'' +
                ", colonyId='" + colonyId + '\'' +
                ", specimenId='" + specimenId + '\'' +
                ", sex='" + sex + '\'' +
                ", rawProcedureStatus='" + rawProcedureStatus + '\'' +
                ", dcc_procedure_pk=" + dcc_procedure_pk +
                ", isLineLevel=" + isLineLevel +
                '}';
    }
}
