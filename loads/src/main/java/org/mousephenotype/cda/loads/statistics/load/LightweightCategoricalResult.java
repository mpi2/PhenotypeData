package org.mousephenotype.cda.loads.statistics.load;


import org.mousephenotype.cda.enumerations.BatchClassification;
import org.mousephenotype.cda.enumerations.ControlStrategy;

import java.io.Serializable;
import java.util.List;

public class LightweightCategoricalResult implements Serializable {

    private static final long serialVersionUID = 3192619384147534299L;


    // ======================================
    // Meta information about the calculation
    private String statisticalMethod = "Fisher's exact test";
    private String metadataGroup = "";
    private String status;
    private ControlStrategy controlSelectionMethod;
    private String rawOutput;
    private Long calculationTimeNanos;
    private Integer femaleMutantCount;
    private Integer maleMutantCount;
    private Integer femaleControlCount;
    private Integer maleControlCount;
    private Integer dataSourceId;
    private Integer projectId;
    private Integer organisationId;
    private String organisationName;

    private Integer pipelineId;
    private String pipelineStableId;
    private Integer procedureId;
    private String procedureGroup;
    private Integer parameterId;
    private String parameterStableId;
    private String alleleAccessionId;

	protected String parameterName;
	protected String procedureName;
	protected String markerAcc;
	protected String markerSymbol;
	protected String backgroundStrainName;

	private String colonyId;
    private String dependentVariable;
    private List<String> categories;
	private String categoryA;
	private String categoryB;
    private String sex;
    private String zygosity;
    private String strain;
    private Integer controlId;
    private Integer experimentalId;
    private Double pValue;
    private Double effectSize;
    private String mpAcc;
    private BatchClassification workflow;
    private Boolean weightAvailable;


	public String getCategoryA() {
		return categoryA;
	}

	public void setCategoryA(String categoryA) {
		this.categoryA = categoryA;
	}

	public String getCategoryB() {
		return categoryB;
	}

	public void setCategoryB(String categoryB) {
		this.categoryB = categoryB;
	}

	public Boolean getWeightAvailable() {

        return weightAvailable;
    }


    public void setWeightAvailable(Boolean weightAvailable) {

        this.weightAvailable = weightAvailable;
    }


    public BatchClassification getWorkflow() {

        return workflow;
    }


    public void setWorkflow(BatchClassification workflow) {

        this.workflow = workflow;
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

	public static long getSerialVersionUID() {
        return serialVersionUID;
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
    // ======================================

    public void setMetadataGroup(String metadataGroup) {
        this.metadataGroup = metadataGroup;
    }

    public String getStatisticalmethod() {
        return statisticalMethod;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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


    public Integer getOrganisationId() { return organisationId; }

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

    public Integer getParameterId() {
        return parameterId;
    }

    public void setParameterId(Integer parameterId) {
        this.parameterId = parameterId;
    }


    public String getPipelineStableId() {

        return pipelineStableId;
    }


    public void setPipelineStableId(String pipelineStableId) {

        this.pipelineStableId = pipelineStableId;
    }


    public String getProcedureGroup() {

        return procedureGroup;
    }


    public void setProcedureGroup(String procedureGroup) {

        this.procedureGroup = procedureGroup;
    }


    public String getParameterStableId() {

        return parameterStableId;
    }


    public void setParameterStableId(String parameterStableId) {

        this.parameterStableId = parameterStableId;
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

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
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

    public Double getpValue() {
        return pValue;
    }

    public void setpValue(Double pValue) {
        this.pValue = pValue;
    }

    public Double getEffectSize() {
        return effectSize;
    }

    public void setEffectSize(Double effectSize) {
        this.effectSize = effectSize;
    }

    public String getMpAcc() {
        return mpAcc;
    }

    public void setMpAcc(String mpAcc) {
        this.mpAcc = mpAcc;
    }


    public String getAlleleAccessionId() {

        return alleleAccessionId;
    }


    public void setAlleleAccessionId(String alleleAccessionId) {

        this.alleleAccessionId = alleleAccessionId;
    }


    public Integer getProcedureId() {

        return procedureId;
	}


	public void setProcedureId(Integer procedureId) {
		this.procedureId = procedureId;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LightweightCategoricalResult)) return false;

		LightweightCategoricalResult that = (LightweightCategoricalResult) o;

		if (calculationTimeNanos != null ? !calculationTimeNanos.equals(that.calculationTimeNanos) : that.calculationTimeNanos != null)
			return false;
		if (categories != null ? !categories.equals(that.categories) : that.categories != null) return false;
		if (colonyId != null ? !colonyId.equals(that.colonyId) : that.colonyId != null) return false;
		if (controlId != null ? !controlId.equals(that.controlId) : that.controlId != null) return false;
		if (controlSelectionMethod != that.controlSelectionMethod) return false;
		if (dataSourceId != null ? !dataSourceId.equals(that.dataSourceId) : that.dataSourceId != null) return false;
		if (dependentVariable != null ? !dependentVariable.equals(that.dependentVariable) : that.dependentVariable != null)
			return false;
		if (effectSize != null ? !effectSize.equals(that.effectSize) : that.effectSize != null) return false;
		if (experimentalId != null ? !experimentalId.equals(that.experimentalId) : that.experimentalId != null)
			return false;
		if (femaleControlCount != null ? !femaleControlCount.equals(that.femaleControlCount) : that.femaleControlCount != null)
			return false;
		if (femaleMutantCount != null ? !femaleMutantCount.equals(that.femaleMutantCount) : that.femaleMutantCount != null)
			return false;
		if (maleControlCount != null ? !maleControlCount.equals(that.maleControlCount) : that.maleControlCount != null)
			return false;
		if (maleMutantCount != null ? !maleMutantCount.equals(that.maleMutantCount) : that.maleMutantCount != null)
			return false;
		if (metadataGroup != null ? !metadataGroup.equals(that.metadataGroup) : that.metadataGroup != null)
			return false;
		if (mpAcc != null ? !mpAcc.equals(that.mpAcc) : that.mpAcc != null) return false;
		if (organisationId != null ? !organisationId.equals(that.organisationId) : that.organisationId != null)
			return false;
		if (pValue != null ? !pValue.equals(that.pValue) : that.pValue != null) return false;
		if (parameterId != null ? !parameterId.equals(that.parameterId) : that.parameterId != null) return false;
		if (pipelineId != null ? !pipelineId.equals(that.pipelineId) : that.pipelineId != null) return false;
		if (procedureId != null ? !procedureId.equals(that.procedureId) : that.procedureId != null) return false;
		if (projectId != null ? !projectId.equals(that.projectId) : that.projectId != null) return false;
		if (rawOutput != null ? !rawOutput.equals(that.rawOutput) : that.rawOutput != null) return false;
		if (sex != null ? !sex.equals(that.sex) : that.sex != null) return false;
		if (status != null ? !status.equals(that.status) : that.status != null) return false;
		if (strain != null ? !strain.equals(that.strain) : that.strain != null) return false;
        return !(zygosity != null ? !zygosity.equals(that.zygosity) : that.zygosity != null);

    }


	@Override
	public int hashCode() {
		int result = metadataGroup != null ? metadataGroup.hashCode() : 0;
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (controlSelectionMethod != null ? controlSelectionMethod.hashCode() : 0);
		result = 31 * result + (rawOutput != null ? rawOutput.hashCode() : 0);
		result = 31 * result + (calculationTimeNanos != null ? calculationTimeNanos.hashCode() : 0);
		result = 31 * result + (femaleMutantCount != null ? femaleMutantCount.hashCode() : 0);
		result = 31 * result + (maleMutantCount != null ? maleMutantCount.hashCode() : 0);
		result = 31 * result + (femaleControlCount != null ? femaleControlCount.hashCode() : 0);
		result = 31 * result + (maleControlCount != null ? maleControlCount.hashCode() : 0);
		result = 31 * result + (dataSourceId != null ? dataSourceId.hashCode() : 0);
		result = 31 * result + (projectId != null ? projectId.hashCode() : 0);
		result = 31 * result + (organisationId != null ? organisationId.hashCode() : 0);
		result = 31 * result + (pipelineId != null ? pipelineId.hashCode() : 0);
		result = 31 * result + (procedureId != null ? procedureId.hashCode() : 0);
		result = 31 * result + (parameterId != null ? parameterId.hashCode() : 0);
		result = 31 * result + (colonyId != null ? colonyId.hashCode() : 0);
		result = 31 * result + (dependentVariable != null ? dependentVariable.hashCode() : 0);
		result = 31 * result + (categories != null ? categories.hashCode() : 0);
		result = 31 * result + (sex != null ? sex.hashCode() : 0);
		result = 31 * result + (zygosity != null ? zygosity.hashCode() : 0);
		result = 31 * result + (strain != null ? strain.hashCode() : 0);
		result = 31 * result + (controlId != null ? controlId.hashCode() : 0);
		result = 31 * result + (experimentalId != null ? experimentalId.hashCode() : 0);
		result = 31 * result + (pValue != null ? pValue.hashCode() : 0);
		result = 31 * result + (effectSize != null ? effectSize.hashCode() : 0);
		result = 31 * result + (mpAcc != null ? mpAcc.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "LightweightCategoricalResult{" +
			"statisticalMethod='" + statisticalMethod + '\'' +
			", metadataGroup='" + metadataGroup + '\'' +
			", status='" + status + '\'' +
			", controlSelectionMethod=" + controlSelectionMethod +
			", rawOutput='" + rawOutput + '\'' +
			", calculationTimeNanos=" + calculationTimeNanos +
			", femaleMutantCount=" + femaleMutantCount +
			", maleMutantCount=" + maleMutantCount +
			", femaleControlCount=" + femaleControlCount +
			", maleControlCount=" + maleControlCount +
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
			", parameterName='" + parameterName + '\'' +
			", procedureName='" + procedureName + '\'' +
			", markerAcc='" + markerAcc + '\'' +
			", markerSymbol='" + markerSymbol + '\'' +
			", backgroundStrainName='" + backgroundStrainName + '\'' +
			", colonyId='" + colonyId + '\'' +
			", dependentVariable='" + dependentVariable + '\'' +
			", categories=" + categories +
			", categoryA='" + categoryA + '\'' +
			", categoryB='" + categoryB + '\'' +
			", sex='" + sex + '\'' +
			", zygosity='" + zygosity + '\'' +
			", strain='" + strain + '\'' +
			", controlId=" + controlId +
			", experimentalId=" + experimentalId +
			", pValue=" + pValue +
			", effectSize=" + effectSize +
			", mpAcc='" + mpAcc + '\'' +
			", workflow=" + workflow +
			", weightAvailable=" + weightAvailable +
			'}';
	}
}
