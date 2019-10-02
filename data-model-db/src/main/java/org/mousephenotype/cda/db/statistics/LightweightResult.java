package org.mousephenotype.cda.db.statistics;

import org.mousephenotype.cda.enumerations.BatchClassification;
import org.mousephenotype.cda.enumerations.ControlStrategy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class LightweightResult {

    protected Integer femaleMutantCount;
    protected Integer maleMutantCount;
    protected Integer femaleControlCount;
    protected Integer maleControlCount;
    protected Long    dataSourceId;
    protected Long    projectId;
    protected Long    organisationId;
    protected String  organisationName;

    protected Long   pipelineId;
    protected String pipelineStableId;
    protected Long   procedureId;
    protected String procedureGroup;
    protected Long   parameterId;
    protected String parameterStableId;
    protected String alleleAccessionId;

    protected String parameterName;
    protected String procedureName;
    protected String markerAcc;
    protected String markerSymbol;
    protected String backgroundStrainName;

    protected String colonyId;
    protected String dependentVariable;

    protected String              sex;
    protected String              zygosity;
    protected String              strain;
    protected Long                controlId;
    protected Long                experimentalId;
    protected String              mpAcc;
    protected String              mpTermName;
    protected BatchClassification workflow;
    protected Boolean             weightAvailable;
    protected String              additionalInformation;


    protected String          statisticalMethod;
    protected String          metadataGroup = "";
    protected String          status;
    protected ControlStrategy controlSelectionMethod;
    protected String          rawOutput;
    protected Long            calculationTimeNanos;

    private StatisticalResult statisticalResult;

    public PreparedStatement getSaveResultStatement(Connection connection) throws SQLException {
        if (statisticalResult!=null) {
            return statisticalResult.getSaveResultStatement(connection, this);
        } else {
            return null;
        }
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public String getMpTermName() {
        return mpTermName;
    }

    public void setMpTermName(String mpTermName) {
        this.mpTermName = mpTermName;
    }

    public StatisticalResult getStatisticalResult() {
        return statisticalResult;
    }

    public void setStatisticalResult(StatisticalResult statisticalResult) {
        this.statisticalResult = statisticalResult;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getZygosity() {
        return zygosity;
    }

    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    public String getStrain() {
        return strain;
    }

    public void setStrain(String strain) {
        this.strain = strain;
    }

    public Long getControlId() {
        return controlId;
    }

    public void setControlId(Long controlId) {
        this.controlId = controlId;
    }

    public Long getExperimentalId() {
        return experimentalId;
    }

    public void setExperimentalId(Long experimentalId) {
        this.experimentalId = experimentalId;
    }

    public String getMpAcc() {
        return mpAcc;
    }

    public void setMpAcc(String mpAcc) {
        this.mpAcc = mpAcc;
    }

    public BatchClassification getWorkflow() {
        return workflow;
    }

    public void setWorkflow(BatchClassification workflow) {
        this.workflow = workflow;
    }

    public Boolean getWeightAvailable() {
        return weightAvailable;
    }

    public void setWeightAvailable(Boolean weightAvailable) {
        this.weightAvailable = weightAvailable;
    }

    public Integer getFemaleMutantCount() {
        return femaleMutantCount;
    }

    public void setFemaleMutantCount(Integer femaleMutantCount) {
        this.femaleMutantCount = femaleMutantCount;
    }

    public Integer getMaleMutantCount() {
        return maleMutantCount;
    }

    public void setMaleMutantCount(Integer maleMutantCount) {
        this.maleMutantCount = maleMutantCount;
    }

    public Integer getFemaleControlCount() {
        return femaleControlCount;
    }

    public void setFemaleControlCount(Integer femaleControlCount) {
        this.femaleControlCount = femaleControlCount;
    }

    public Integer getMaleControlCount() {
        return maleControlCount;
    }

    public void setMaleControlCount(Integer maleControlCount) {
        this.maleControlCount = maleControlCount;
    }

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public Long getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Long pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineStableId() {
        return pipelineStableId;
    }

    public void setPipelineStableId(String pipelineStableId) {
        this.pipelineStableId = pipelineStableId;
    }

    public Long getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(Long procedureId) {
        this.procedureId = procedureId;
    }

    public String getProcedureGroup() {
        return procedureGroup;
    }

    public void setProcedureGroup(String procedureGroup) {
        this.procedureGroup = procedureGroup;
    }

    public Long getParameterId() {
        return parameterId;
    }

    public void setParameterId(Long parameterId) {
        this.parameterId = parameterId;
    }

    public String getParameterStableId() {
        return parameterStableId;
    }

    public void setParameterStableId(String parameterStableId) {
        this.parameterStableId = parameterStableId;
    }

    public String getAlleleAccessionId() {
        return alleleAccessionId;
    }

    public void setAlleleAccessionId(String alleleAccessionId) {
        this.alleleAccessionId = alleleAccessionId;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getMarkerAcc() {
        return markerAcc;
    }

    public void setMarkerAcc(String markerAcc) {
        this.markerAcc = markerAcc;
    }

    public String getMarkerSymbol() {
        return markerSymbol;
    }

    public void setMarkerSymbol(String markerSymbol) {
        this.markerSymbol = markerSymbol;
    }

    public String getBackgroundStrainName() {
        return backgroundStrainName;
    }

    public void setBackgroundStrainName(String backgroundStrainName) {
        this.backgroundStrainName = backgroundStrainName;
    }

    public String getColonyId() {
        return colonyId;
    }

    public void setColonyId(String colonyId) {
        this.colonyId = colonyId;
    }

    public String getDependentVariable() {
        return dependentVariable;
    }

    public void setDependentVariable(String dependentVariable) {
        this.dependentVariable = dependentVariable;
    }

    public String getStatisticalMethod() {
        return statisticalMethod;
    }

    public void setStatisticalMethod(String statisticalMethod) {
        this.statisticalMethod = statisticalMethod;
    }

    public String getMetadataGroup() {
        return metadataGroup;
    }

    public void setMetadataGroup(String metadataGroup) {
        this.metadataGroup = metadataGroup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ControlStrategy getControlSelectionMethod() {
        return controlSelectionMethod;
    }

    public void setControlSelectionMethod(ControlStrategy controlSelectionMethod) {
        this.controlSelectionMethod = controlSelectionMethod;
    }

    public String getRawOutput() {
        return rawOutput;
    }

    public void setRawOutput(String rawOutput) {
        this.rawOutput = rawOutput;
    }

    public Long getCalculationTimeNanos() {
        return calculationTimeNanos;
    }

    public void setCalculationTimeNanos(Long calculationTimeNanos) {
        this.calculationTimeNanos = calculationTimeNanos;
    }
}
