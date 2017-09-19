package org.mousephenotype.cda.loads.statistics.load;

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
    protected Integer dataSourceId;
    protected Integer projectId;
    protected Integer organisationId;
    protected String organisationName;

    protected Integer pipelineId;
    protected String pipelineStableId;
    protected Integer procedureId;
    protected String procedureGroup;
    protected Integer parameterId;
    protected String parameterStableId;
    protected String alleleAccessionId;

    protected String parameterName;
    protected String procedureName;
    protected String markerAcc;
    protected String markerSymbol;
    protected String backgroundStrainName;

    protected String colonyId;
    protected String dependentVariable;

    protected String sex;
    protected String zygosity;
    protected String strain;
    protected Integer controlId;
    protected Integer experimentalId;
    protected String mpAcc;
    protected String mpTermName;
    protected BatchClassification workflow;
    protected Boolean weightAvailable;
    protected String additionalInformation;


    protected String statisticalMethod;
    protected String metadataGroup = "";
    protected String status;
    protected ControlStrategy controlSelectionMethod;
    protected String rawOutput;
    protected Long calculationTimeNanos;

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

    public Integer getControlId() {
        return controlId;
    }

    public void setControlId(Integer controlId) {
        this.controlId = controlId;
    }

    public Integer getExperimentalId() {
        return experimentalId;
    }

    public void setExperimentalId(Integer experimentalId) {
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

    public Integer getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Integer dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Integer organisationId) {
        this.organisationId = organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public Integer getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(Integer pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getPipelineStableId() {
        return pipelineStableId;
    }

    public void setPipelineStableId(String pipelineStableId) {
        this.pipelineStableId = pipelineStableId;
    }

    public Integer getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(Integer procedureId) {
        this.procedureId = procedureId;
    }

    public String getProcedureGroup() {
        return procedureGroup;
    }

    public void setProcedureGroup(String procedureGroup) {
        this.procedureGroup = procedureGroup;
    }

    public Integer getParameterId() {
        return parameterId;
    }

    public void setParameterId(Integer parameterId) {
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
