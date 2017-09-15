package org.mousephenotype.cda.loads.statistics.load;


import org.mousephenotype.cda.enumerations.BatchClassification;
import org.mousephenotype.cda.enumerations.ControlStrategy;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Base class for light weight statistical result
 */
public class LightweightUnidimensionalResult implements Serializable {

	private static final long serialVersionUID = 3192619382747534299L;

	// ======================================
	// Meta information about the calculation
	protected String metadataGroup = "";
	protected String status;
	protected ControlStrategy controlSelectionMethod;
	protected String rawOutput;
	protected Long calculationTimeNanos;
	protected Integer femaleMutantCount;
	protected Integer maleMutantCount;
	protected Integer femaleControlCount;
	protected Integer maleControlCount;
	protected Double femaleControlMean;
	protected Double maleControlMean;
	protected Double femaleExperimentalMean;
	protected Double maleExperimentalMean;
	protected Integer controlId;
	protected Integer experimentalId;
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
	protected String colonyId;
	protected String dependentVariable;
	protected String statisticalMethod;
	protected String zygosity;
	protected String strain;
	protected String backgroundStrainName;

	protected String mpAcc;
	protected String maleMpAcc;
	protected String femaleMpAcc;
	protected String mpTermName;
	protected String maleMpTermName;
	protected String femaleMpTermName;

	protected String id;
	protected String experimentalZygosity;
	protected BatchClassification workflow;
	protected Boolean weightAvailable;
	protected String sex;
	private String additionalInformation;

	private StatisticalResult statisticalResult;


	public PreparedStatement getSaveResultStatement(Connection connection) throws SQLException {
		if (statisticalResult!=null) {
			return statisticalResult.getSaveResultStatement(connection, this);
		} else {
			return null;
		}
	}



		// Genreated methods


	public String getStatisticalMethod() {
		return statisticalMethod;
	}

	public void setStatisticalMethod(String statisticalMethod) {
		this.statisticalMethod = statisticalMethod;
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

	public StatisticalResult getStatisticalResult() {
		return statisticalResult;
	}

	public void setStatisticalResult(StatisticalResult statisticalResult) {
		this.statisticalResult = statisticalResult;
	}

	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
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

	public Double getFemaleControlMean() {
		return femaleControlMean;
	}

	public void setFemaleControlMean(Double femaleControlMean) {
		this.femaleControlMean = femaleControlMean;
	}

	public Double getMaleControlMean() {
		return maleControlMean;
	}

	public void setMaleControlMean(Double maleControlMean) {
		this.maleControlMean = maleControlMean;
	}

	public Double getFemaleExperimentalMean() {
		return femaleExperimentalMean;
	}

	public void setFemaleExperimentalMean(Double femaleExperimentalMean) {
		this.femaleExperimentalMean = femaleExperimentalMean;
	}

	public Double getMaleExperimentalMean() {
		return maleExperimentalMean;
	}

	public void setMaleExperimentalMean(Double maleExperimentalMean) {
		this.maleExperimentalMean = maleExperimentalMean;
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

	public String getMpAcc() {
		return mpAcc;
	}

	public void setMpAcc(String mpAcc) {
		this.mpAcc = mpAcc;
	}

	public String getMaleMpAcc() {
		return maleMpAcc;
	}

	public void setMaleMpAcc(String maleMpAcc) {
		this.maleMpAcc = maleMpAcc;
	}

	public String getFemaleMpAcc() {
		return femaleMpAcc;
	}

	public String getMpTermName() {
		return mpTermName;
	}

	public void setMpTermName(String mpTermName) {
		this.mpTermName = mpTermName;
	}

	public String getMaleMpTermName() {
		return maleMpTermName;
	}

	public void setMaleMpTermName(String maleMpTermName) {
		this.maleMpTermName = maleMpTermName;
	}

	public String getFemaleMpTermName() {
		return femaleMpTermName;
	}

	public void setFemaleMpTermName(String femaleMpTermName) {
		this.femaleMpTermName = femaleMpTermName;
	}

	public void setFemaleMpAcc(String femaleMpAcc) {
		this.femaleMpAcc = femaleMpAcc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExperimentalZygosity() {
		return experimentalZygosity;
	}

	public void setExperimentalZygosity(String experimentalZygosity) {
		this.experimentalZygosity = experimentalZygosity;
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

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LightweightUnidimensionalResult that = (LightweightUnidimensionalResult) o;

		if (getMetadataGroup() != null ? !getMetadataGroup().equals(that.getMetadataGroup()) : that.getMetadataGroup() != null)
			return false;
		if (getStatus() != null ? !getStatus().equals(that.getStatus()) : that.getStatus() != null) return false;
		if (getControlSelectionMethod() != that.getControlSelectionMethod()) return false;
		if (getRawOutput() != null ? !getRawOutput().equals(that.getRawOutput()) : that.getRawOutput() != null)
			return false;
		if (getFemaleMutantCount() != null ? !getFemaleMutantCount().equals(that.getFemaleMutantCount()) : that.getFemaleMutantCount() != null)
			return false;
		if (getMaleMutantCount() != null ? !getMaleMutantCount().equals(that.getMaleMutantCount()) : that.getMaleMutantCount() != null)
			return false;
		if (getFemaleControlCount() != null ? !getFemaleControlCount().equals(that.getFemaleControlCount()) : that.getFemaleControlCount() != null)
			return false;
		if (getMaleControlCount() != null ? !getMaleControlCount().equals(that.getMaleControlCount()) : that.getMaleControlCount() != null)
			return false;
		if (getFemaleControlMean() != null ? !getFemaleControlMean().equals(that.getFemaleControlMean()) : that.getFemaleControlMean() != null)
			return false;
		if (getMaleControlMean() != null ? !getMaleControlMean().equals(that.getMaleControlMean()) : that.getMaleControlMean() != null)
			return false;
		if (getFemaleExperimentalMean() != null ? !getFemaleExperimentalMean().equals(that.getFemaleExperimentalMean()) : that.getFemaleExperimentalMean() != null)
			return false;
		if (getMaleExperimentalMean() != null ? !getMaleExperimentalMean().equals(that.getMaleExperimentalMean()) : that.getMaleExperimentalMean() != null)
			return false;
		if (getControlId() != null ? !getControlId().equals(that.getControlId()) : that.getControlId() != null)
			return false;
		if (getExperimentalId() != null ? !getExperimentalId().equals(that.getExperimentalId()) : that.getExperimentalId() != null)
			return false;
		if (getDataSourceId() != null ? !getDataSourceId().equals(that.getDataSourceId()) : that.getDataSourceId() != null)
			return false;
		if (getProjectId() != null ? !getProjectId().equals(that.getProjectId()) : that.getProjectId() != null)
			return false;
		if (getOrganisationId() != null ? !getOrganisationId().equals(that.getOrganisationId()) : that.getOrganisationId() != null)
			return false;
		if (getOrganisationName() != null ? !getOrganisationName().equals(that.getOrganisationName()) : that.getOrganisationName() != null)
			return false;
		if (getPipelineId() != null ? !getPipelineId().equals(that.getPipelineId()) : that.getPipelineId() != null)
			return false;
		if (getPipelineStableId() != null ? !getPipelineStableId().equals(that.getPipelineStableId()) : that.getPipelineStableId() != null)
			return false;
		if (getProcedureId() != null ? !getProcedureId().equals(that.getProcedureId()) : that.getProcedureId() != null)
			return false;
		if (getProcedureGroup() != null ? !getProcedureGroup().equals(that.getProcedureGroup()) : that.getProcedureGroup() != null)
			return false;
		if (getParameterId() != null ? !getParameterId().equals(that.getParameterId()) : that.getParameterId() != null)
			return false;
		if (getParameterStableId() != null ? !getParameterStableId().equals(that.getParameterStableId()) : that.getParameterStableId() != null)
			return false;
		if (getAlleleAccessionId() != null ? !getAlleleAccessionId().equals(that.getAlleleAccessionId()) : that.getAlleleAccessionId() != null)
			return false;
		if (getMarkerAcc() != null ? !getMarkerAcc().equals(that.getMarkerAcc()) : that.getMarkerAcc() != null)
			return false;
		if (getMarkerSymbol() != null ? !getMarkerSymbol().equals(that.getMarkerSymbol()) : that.getMarkerSymbol() != null)
			return false;
		if (getColonyId() != null ? !getColonyId().equals(that.getColonyId()) : that.getColonyId() != null)
			return false;
		if (getDependentVariable() != null ? !getDependentVariable().equals(that.getDependentVariable()) : that.getDependentVariable() != null)
			return false;
		if (getZygosity() != null ? !getZygosity().equals(that.getZygosity()) : that.getZygosity() != null)
			return false;
		if (getStrain() != null ? !getStrain().equals(that.getStrain()) : that.getStrain() != null) return false;
		if (getBackgroundStrainName() != null ? !getBackgroundStrainName().equals(that.getBackgroundStrainName()) : that.getBackgroundStrainName() != null)
			return false;
		if (getMpAcc() != null ? !getMpAcc().equals(that.getMpAcc()) : that.getMpAcc() != null) return false;
		if (getMaleMpAcc() != null ? !getMaleMpAcc().equals(that.getMaleMpAcc()) : that.getMaleMpAcc() != null)
			return false;
		if (getFemaleMpAcc() != null ? !getFemaleMpAcc().equals(that.getFemaleMpAcc()) : that.getFemaleMpAcc() != null)
			return false;
		if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
		if (getExperimentalZygosity() != null ? !getExperimentalZygosity().equals(that.getExperimentalZygosity()) : that.getExperimentalZygosity() != null)
			return false;
		if (getWorkflow() != that.getWorkflow()) return false;
		if (getWeightAvailable() != null ? !getWeightAvailable().equals(that.getWeightAvailable()) : that.getWeightAvailable() != null)
			return false;
		if (getSex() != null ? !getSex().equals(that.getSex()) : that.getSex() != null) return false;
		if (getAdditionalInformation() != null ? !getAdditionalInformation().equals(that.getAdditionalInformation()) : that.getAdditionalInformation() != null)
			return false;
		return getStatisticalResult() != null ? getStatisticalResult().equals(that.getStatisticalResult()) : that.getStatisticalResult() == null;

	}

	@Override
	public int hashCode() {
		int result = getMetadataGroup() != null ? getMetadataGroup().hashCode() : 0;
		result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
		result = 31 * result + (getControlSelectionMethod() != null ? getControlSelectionMethod().hashCode() : 0);
		result = 31 * result + (getRawOutput() != null ? getRawOutput().hashCode() : 0);
		result = 31 * result + (getFemaleMutantCount() != null ? getFemaleMutantCount().hashCode() : 0);
		result = 31 * result + (getMaleMutantCount() != null ? getMaleMutantCount().hashCode() : 0);
		result = 31 * result + (getFemaleControlCount() != null ? getFemaleControlCount().hashCode() : 0);
		result = 31 * result + (getMaleControlCount() != null ? getMaleControlCount().hashCode() : 0);
		result = 31 * result + (getFemaleControlMean() != null ? getFemaleControlMean().hashCode() : 0);
		result = 31 * result + (getMaleControlMean() != null ? getMaleControlMean().hashCode() : 0);
		result = 31 * result + (getFemaleExperimentalMean() != null ? getFemaleExperimentalMean().hashCode() : 0);
		result = 31 * result + (getMaleExperimentalMean() != null ? getMaleExperimentalMean().hashCode() : 0);
		result = 31 * result + (getControlId() != null ? getControlId().hashCode() : 0);
		result = 31 * result + (getExperimentalId() != null ? getExperimentalId().hashCode() : 0);
		result = 31 * result + (getDataSourceId() != null ? getDataSourceId().hashCode() : 0);
		result = 31 * result + (getProjectId() != null ? getProjectId().hashCode() : 0);
		result = 31 * result + (getOrganisationId() != null ? getOrganisationId().hashCode() : 0);
		result = 31 * result + (getOrganisationName() != null ? getOrganisationName().hashCode() : 0);
		result = 31 * result + (getPipelineId() != null ? getPipelineId().hashCode() : 0);
		result = 31 * result + (getPipelineStableId() != null ? getPipelineStableId().hashCode() : 0);
		result = 31 * result + (getProcedureId() != null ? getProcedureId().hashCode() : 0);
		result = 31 * result + (getProcedureGroup() != null ? getProcedureGroup().hashCode() : 0);
		result = 31 * result + (getParameterId() != null ? getParameterId().hashCode() : 0);
		result = 31 * result + (getParameterStableId() != null ? getParameterStableId().hashCode() : 0);
		result = 31 * result + (getAlleleAccessionId() != null ? getAlleleAccessionId().hashCode() : 0);
		result = 31 * result + (getMarkerAcc() != null ? getMarkerAcc().hashCode() : 0);
		result = 31 * result + (getMarkerSymbol() != null ? getMarkerSymbol().hashCode() : 0);
		result = 31 * result + (getColonyId() != null ? getColonyId().hashCode() : 0);
		result = 31 * result + (getDependentVariable() != null ? getDependentVariable().hashCode() : 0);
		result = 31 * result + (getZygosity() != null ? getZygosity().hashCode() : 0);
		result = 31 * result + (getStrain() != null ? getStrain().hashCode() : 0);
		result = 31 * result + (getBackgroundStrainName() != null ? getBackgroundStrainName().hashCode() : 0);
		result = 31 * result + (getMpAcc() != null ? getMpAcc().hashCode() : 0);
		result = 31 * result + (getMaleMpAcc() != null ? getMaleMpAcc().hashCode() : 0);
		result = 31 * result + (getFemaleMpAcc() != null ? getFemaleMpAcc().hashCode() : 0);
		result = 31 * result + (getId() != null ? getId().hashCode() : 0);
		result = 31 * result + (getExperimentalZygosity() != null ? getExperimentalZygosity().hashCode() : 0);
		result = 31 * result + (getWorkflow() != null ? getWorkflow().hashCode() : 0);
		result = 31 * result + (getWeightAvailable() != null ? getWeightAvailable().hashCode() : 0);
		result = 31 * result + (getSex() != null ? getSex().hashCode() : 0);
		result = 31 * result + (getAdditionalInformation() != null ? getAdditionalInformation().hashCode() : 0);
		result = 31 * result + (getStatisticalResult() != null ? getStatisticalResult().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "LightweightUnidimensionalResult{" +
			"metadataGroup='" + metadataGroup + '\'' +
			", status='" + status + '\'' +
			", controlSelectionMethod=" + controlSelectionMethod +
			", rawOutput='" + rawOutput + '\'' +
			", femaleMutantCount=" + femaleMutantCount +
			", maleMutantCount=" + maleMutantCount +
			", femaleControlCount=" + femaleControlCount +
			", maleControlCount=" + maleControlCount +
			", femaleControlMean=" + femaleControlMean +
			", maleControlMean=" + maleControlMean +
			", femaleExperimentalMean=" + femaleExperimentalMean +
			", maleExperimentalMean=" + maleExperimentalMean +
			", controlId=" + controlId +
			", experimentalId=" + experimentalId +
			", dataSourceId=" + dataSourceId +
			", projectId=" + projectId +
			", organisationId=" + organisationId +
			", organisationName='" + organisationName + '\'' +
			", pipelineId=" + pipelineId +
			", pipelineStableId='" + pipelineStableId + '\'' +
			", procedureId=" + procedureId +
			", procedureGroup='" + procedureGroup + '\'' +
			", parameterId=" + parameterId +
			", parameterStableId='" + parameterStableId + '\'' +
			", alleleAccessionId='" + alleleAccessionId + '\'' +
			", markerAcc='" + markerAcc + '\'' +
			", markerSymbol='" + markerSymbol + '\'' +
			", colonyId='" + colonyId + '\'' +
			", dependentVariable='" + dependentVariable + '\'' +
			", zygosity='" + zygosity + '\'' +
			", strain='" + strain + '\'' +
			", backgroundStrainName='" + backgroundStrainName + '\'' +
			", mpAcc='" + mpAcc + '\'' +
			", maleMpAcc='" + maleMpAcc + '\'' +
			", femaleMpAcc='" + femaleMpAcc + '\'' +
			", id='" + id + '\'' +
			", experimentalZygosity='" + experimentalZygosity + '\'' +
			", workflow=" + workflow +
			", weightAvailable=" + weightAvailable +
			", sex='" + sex + '\'' +
			", additionalInformation='" + additionalInformation + '\'' +
			", statisticalResult=" + statisticalResult +
			'}';
	}
}
