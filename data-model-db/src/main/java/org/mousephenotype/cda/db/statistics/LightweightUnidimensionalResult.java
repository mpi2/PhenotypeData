package org.mousephenotype.cda.db.statistics;


import java.io.Serializable;

/**
 * Base class for light weight statistical result
 */
public class LightweightUnidimensionalResult extends LightweightResult implements Serializable {

	private static final long serialVersionUID = 3192619382747534299L;

	// ======================================
	// Meta information about the calculation

	protected Double femaleControlMean;
	protected Double maleControlMean;
	protected Double femaleExperimentalMean;
	protected Double maleExperimentalMean;

	protected String maleMpAcc;
	protected String femaleMpAcc;
	protected String maleMpTermName;
	protected String femaleMpTermName;



		// Genreated methods

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

	public String getMaleMpAcc() {
		return maleMpAcc;
	}

	public void setMaleMpAcc(String maleMpAcc) {
		this.maleMpAcc = maleMpAcc;
	}

	public String getFemaleMpAcc() {
		return femaleMpAcc;
	}

	public void setFemaleMpAcc(String femaleMpAcc) {
		this.femaleMpAcc = femaleMpAcc;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LightweightUnidimensionalResult that = (LightweightUnidimensionalResult) o;

		if (femaleControlMean != null ? !femaleControlMean.equals(that.femaleControlMean) : that.femaleControlMean != null)
			return false;
		if (maleControlMean != null ? !maleControlMean.equals(that.maleControlMean) : that.maleControlMean != null)
			return false;
		if (femaleExperimentalMean != null ? !femaleExperimentalMean.equals(that.femaleExperimentalMean) : that.femaleExperimentalMean != null)
			return false;
		if (maleExperimentalMean != null ? !maleExperimentalMean.equals(that.maleExperimentalMean) : that.maleExperimentalMean != null)
			return false;
		if (maleMpAcc != null ? !maleMpAcc.equals(that.maleMpAcc) : that.maleMpAcc != null) return false;
		if (femaleMpAcc != null ? !femaleMpAcc.equals(that.femaleMpAcc) : that.femaleMpAcc != null) return false;
		if (maleMpTermName != null ? !maleMpTermName.equals(that.maleMpTermName) : that.maleMpTermName != null)
			return false;
		return femaleMpTermName != null ? femaleMpTermName.equals(that.femaleMpTermName) : that.femaleMpTermName == null;
	}

	@Override
	public int hashCode() {
		int result = femaleControlMean != null ? femaleControlMean.hashCode() : 0;
		result = 31 * result + (maleControlMean != null ? maleControlMean.hashCode() : 0);
		result = 31 * result + (femaleExperimentalMean != null ? femaleExperimentalMean.hashCode() : 0);
		result = 31 * result + (maleExperimentalMean != null ? maleExperimentalMean.hashCode() : 0);
		result = 31 * result + (maleMpAcc != null ? maleMpAcc.hashCode() : 0);
		result = 31 * result + (femaleMpAcc != null ? femaleMpAcc.hashCode() : 0);
		result = 31 * result + (maleMpTermName != null ? maleMpTermName.hashCode() : 0);
		result = 31 * result + (femaleMpTermName != null ? femaleMpTermName.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "LightweightUnidimensionalResult{" +
				"femaleControlMean=" + femaleControlMean +
				", maleControlMean=" + maleControlMean +
				", femaleExperimentalMean=" + femaleExperimentalMean +
				", maleExperimentalMean=" + maleExperimentalMean +
				", maleMpAcc='" + maleMpAcc + '\'' +
				", femaleMpAcc='" + femaleMpAcc + '\'' +
				", maleMpTermName='" + maleMpTermName + '\'' +
				", femaleMpTermName='" + femaleMpTermName + '\'' +
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
				", sex='" + sex + '\'' +
				", zygosity='" + zygosity + '\'' +
				", strain='" + strain + '\'' +
				", controlId=" + controlId +
				", experimentalId=" + experimentalId +
				", mpAcc='" + mpAcc + '\'' +
				", mpTermName='" + mpTermName + '\'' +
				", workflow=" + workflow +
				", weightAvailable=" + weightAvailable +
				", statisticalMethod='" + statisticalMethod + '\'' +
				", metadataGroup='" + metadataGroup + '\'' +
				", status='" + status + '\'' +
				", controlSelectionMethod=" + controlSelectionMethod +
				", rawOutput='" + rawOutput + '\'' +
				", calculationTimeNanos=" + calculationTimeNanos +
				'}';
	}
}
