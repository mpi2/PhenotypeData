package org.mousephenotype.cda.loads.statistics.load.impc;

import org.mousephenotype.cda.db.statistics.LightweightResult;
import org.mousephenotype.cda.db.statistics.StatisticalResult;
import org.mousephenotype.cda.db.utilities.SqlUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatisticalResultFailed implements StatisticalResult, Serializable {

	protected static final long serialVersionUID = 4716400683205025616L;

	public final String insertStatement = "INSERT INTO stats_unidimensional_results(control_id, experimental_id, experimental_zygosity, colony_id, external_db_id, project_id, organisation_id, parameter_id, procedure_id, pipeline_id, dependent_variable, control_selection_strategy, mp_acc, male_mp_acc, female_mp_acc, mp_db_id, male_controls, male_mutants, female_controls, female_mutants, female_control_mean, male_control_mean, female_experimental_mean, male_experimental_mean, metadata_group, statistical_method, workflow, weight_available, status, batch_significance, variance_significance, null_test_significance, genotype_parameter_estimate, genotype_stderr_estimate, genotype_effect_pvalue, genotype_percentage_change, gender_parameter_estimate, gender_stderr_estimate, gender_effect_pvalue, weight_parameter_estimate, weight_stderr_estimate, weight_effect_pvalue, gp1_genotype, gp1_residuals_normality_test, gp2_genotype, gp2_residuals_normality_test, blups_test, rotated_residuals_normality_test, intercept_estimate, intercept_stderr_estimate, interaction_significance, interaction_effect_pvalue, gender_female_ko_estimate, gender_female_ko_stderr_estimate, gender_female_ko_pvalue, gender_male_ko_estimate, gender_male_ko_stderr_estimate, gender_male_ko_pvalue, classification_tag, additional_information, raw_output) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?, ?, ?)";

	protected String statisticalMethod;
	private Boolean batchSignificance;
	private Boolean varianceSignificance;
	private Double nullTestSignificance;
	private Double genotypeParameterEstimate;
	private Double genotypeStandardErrorEstimate;
	private Double genotypeEffectPValue;
    private String genotypePercentageChange;
	private Double genderParameterEstimate;
	private Double genderStandardErrorEstimate;
	private Double genderEffectPValue;
	private Double weightParameterEstimate;
	private Double weightStandardErrorEstimate;
	private Double weightEffectPValue;
	private String gp1Genotype;
	private Double gp1ResidualsNormalityTest;
	private String gp2Genotype;
	private Double gp2ResidualsNormalityTest;
	private Double blupsTest;
	private Double rotatedResidualsNormalityTest;
	private Double interceptEstimate;
	private Double interceptEstimateStandardError;
	private Boolean interactionSignificance;
	private Double interactionEffectPValue;
	private Double genderFemaleKoEstimate;
	private Double genderFemaleKoStandardErrorEstimate;
	private Double genderFemaleKoPValue;
	private Double genderMaleKoEstimate;
	private Double genderMaleKoStandardErrorEstimate;
	private Double genderMaleKoPValue;
	private String classificationTag;

	/**
	 * Prepare the statement to insert this result into the database
	 *
	 * @param connection connection object to use to save the result
	 * @param result the base result object to populate the common parameters
	 * @throws SQLException
	 */
	public PreparedStatement getSaveResultStatement(Connection connection, LightweightResult result) throws SQLException {

		PreparedStatement s = connection.prepareStatement(insertStatement);
		int i = 1;

		SqlUtils.setSqlParameter(s, result.getControlId(), i++);
		SqlUtils.setSqlParameter(s, result.getExperimentalId(), i++);
		SqlUtils.setSqlParameter(s, result.getZygosity(), i++);
		SqlUtils.setSqlParameter(s, result.getColonyId(), i++);

		SqlUtils.setSqlParameter(s, result.getDataSourceId(), i++);
		SqlUtils.setSqlParameter(s, result.getProjectId(), i++);
		SqlUtils.setSqlParameter(s, result.getOrganisationId(), i++);
		SqlUtils.setSqlParameter(s, result.getParameterId(), i++);
		SqlUtils.setSqlParameter(s, result.getProcedureId(), i++);
		SqlUtils.setSqlParameter(s, result.getPipelineId(), i++);

		SqlUtils.setSqlParameter(s, result.getDependentVariable(), i++);
		SqlUtils.setSqlParameter(s, result.getControlSelectionMethod().name(), i++);

		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, 5, i++);

		SqlUtils.setSqlParameter(s, result.getMaleControlCount(), i++);
		SqlUtils.setSqlParameter(s, result.getMaleMutantCount(), i++);
		SqlUtils.setSqlParameter(s, result.getFemaleControlCount(), i++);
		SqlUtils.setSqlParameter(s, result.getFemaleMutantCount(), i++);

		SqlUtils.setSqlParameter(s, (Double)null, i++);
		SqlUtils.setSqlParameter(s, (Double)null, i++);
		SqlUtils.setSqlParameter(s, (Double)null, i++);
		SqlUtils.setSqlParameter(s, (Double)null, i++);

		SqlUtils.setSqlParameter(s, result.getMetadataGroup(), i++);
		SqlUtils.setSqlParameter(s, this.getStatisticalMethod(), i++);
		SqlUtils.setSqlParameter(s, (result.getWorkflow()!=null) ? result.getWorkflow().toString() : null, i++);
		SqlUtils.setSqlParameter(s, result.getWeightAvailable().toString(), i++);
		SqlUtils.setSqlParameter(s, result.getStatus(), i++);

		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);
		SqlUtils.setSqlParameter(s, (String)null, i++);

		return s;
	}

	// Generated methods


	public String getStatisticalMethod() {
		return statisticalMethod;
	}

	public void setStatisticalMethod(String statisticalMethod) {
		this.statisticalMethod = statisticalMethod;
	}

	public String getInsertStatement() {
		return insertStatement;
	}

	public Boolean getBatchSignificance() {
		return batchSignificance;
	}

	public void setBatchSignificance(Boolean batchSignificance) {
		this.batchSignificance = batchSignificance;
	}

	public Boolean getVarianceSignificance() {
		return varianceSignificance;
	}

	public void setVarianceSignificance(Boolean varianceSignificance) {
		this.varianceSignificance = varianceSignificance;
	}

	public Double getNullTestSignificance() {
		return nullTestSignificance;
	}

	public void setNullTestSignificance(Double nullTestSignificance) {
		this.nullTestSignificance = nullTestSignificance;
	}

	public Double getGenotypeParameterEstimate() {
		return genotypeParameterEstimate;
	}

	public void setGenotypeParameterEstimate(Double genotypeParameterEstimate) {
		this.genotypeParameterEstimate = genotypeParameterEstimate;
	}

	public Double getGenotypeStandardErrorEstimate() {
		return genotypeStandardErrorEstimate;
	}

	public void setGenotypeStandardErrorEstimate(Double genotypeStandardErrorEstimate) {
		this.genotypeStandardErrorEstimate = genotypeStandardErrorEstimate;
	}

	public Double getGenotypeEffectPValue() {
		return genotypeEffectPValue;
	}

	public void setGenotypeEffectPValue(Double genotypeEffectPValue) {
		this.genotypeEffectPValue = genotypeEffectPValue;
	}

	public String getGenotypePercentageChange() {
		return genotypePercentageChange;
	}

	public void setGenotypePercentageChange(String genotypePercentageChange) {
		this.genotypePercentageChange = genotypePercentageChange;
	}

	public Double getGenderParameterEstimate() {
		return genderParameterEstimate;
	}

	public void setGenderParameterEstimate(Double genderParameterEstimate) {
		this.genderParameterEstimate = genderParameterEstimate;
	}

	public Double getGenderStandardErrorEstimate() {
		return genderStandardErrorEstimate;
	}

	public void setGenderStandardErrorEstimate(Double genderStandardErrorEstimate) {
		this.genderStandardErrorEstimate = genderStandardErrorEstimate;
	}

	public Double getGenderEffectPValue() {
		return genderEffectPValue;
	}

	public void setGenderEffectPValue(Double genderEffectPValue) {
		this.genderEffectPValue = genderEffectPValue;
	}

	public Double getWeightParameterEstimate() {
		return weightParameterEstimate;
	}

	public void setWeightParameterEstimate(Double weightParameterEstimate) {
		this.weightParameterEstimate = weightParameterEstimate;
	}

	public Double getWeightStandardErrorEstimate() {
		return weightStandardErrorEstimate;
	}

	public void setWeightStandardErrorEstimate(Double weightStandardErrorEstimate) {
		this.weightStandardErrorEstimate = weightStandardErrorEstimate;
	}

	public Double getWeightEffectPValue() {
		return weightEffectPValue;
	}

	public void setWeightEffectPValue(Double weightEffectPValue) {
		this.weightEffectPValue = weightEffectPValue;
	}

	public String getGp1Genotype() {
		return gp1Genotype;
	}

	public void setGp1Genotype(String gp1Genotype) {
		this.gp1Genotype = gp1Genotype;
	}

	public Double getGp1ResidualsNormalityTest() {
		return gp1ResidualsNormalityTest;
	}

	public void setGp1ResidualsNormalityTest(Double gp1ResidualsNormalityTest) {
		this.gp1ResidualsNormalityTest = gp1ResidualsNormalityTest;
	}

	public String getGp2Genotype() {
		return gp2Genotype;
	}

	public void setGp2Genotype(String gp2Genotype) {
		this.gp2Genotype = gp2Genotype;
	}

	public Double getGp2ResidualsNormalityTest() {
		return gp2ResidualsNormalityTest;
	}

	public void setGp2ResidualsNormalityTest(Double gp2ResidualsNormalityTest) {
		this.gp2ResidualsNormalityTest = gp2ResidualsNormalityTest;
	}

	public Double getBlupsTest() {
		return blupsTest;
	}

	public void setBlupsTest(Double blupsTest) {
		this.blupsTest = blupsTest;
	}

	public Double getRotatedResidualsNormalityTest() {
		return rotatedResidualsNormalityTest;
	}

	public void setRotatedResidualsNormalityTest(Double rotatedResidualsNormalityTest) {
		this.rotatedResidualsNormalityTest = rotatedResidualsNormalityTest;
	}

	public Double getInterceptEstimate() {
		return interceptEstimate;
	}

	public void setInterceptEstimate(Double interceptEstimate) {
		this.interceptEstimate = interceptEstimate;
	}

	public Double getInterceptEstimateStandardError() {
		return interceptEstimateStandardError;
	}

	public void setInterceptEstimateStandardError(Double interceptEstimateStandardError) {
		this.interceptEstimateStandardError = interceptEstimateStandardError;
	}

	public Boolean getInteractionSignificance() {
		return interactionSignificance;
	}

	public void setInteractionSignificance(Boolean interactionSignificance) {
		this.interactionSignificance = interactionSignificance;
	}

	public Double getInteractionEffectPValue() {
		return interactionEffectPValue;
	}

	public void setInteractionEffectPValue(Double interactionEffectPValue) {
		this.interactionEffectPValue = interactionEffectPValue;
	}

	public Double getGenderFemaleKoEstimate() {
		return genderFemaleKoEstimate;
	}

	public void setGenderFemaleKoEstimate(Double genderFemaleKoEstimate) {
		this.genderFemaleKoEstimate = genderFemaleKoEstimate;
	}

	public Double getGenderFemaleKoStandardErrorEstimate() {
		return genderFemaleKoStandardErrorEstimate;
	}

	public void setGenderFemaleKoStandardErrorEstimate(Double genderFemaleKoStandardErrorEstimate) {
		this.genderFemaleKoStandardErrorEstimate = genderFemaleKoStandardErrorEstimate;
	}

	public Double getGenderFemaleKoPValue() {
		return genderFemaleKoPValue;
	}

	public void setGenderFemaleKoPValue(Double genderFemaleKoPValue) {
		this.genderFemaleKoPValue = genderFemaleKoPValue;
	}

	public Double getGenderMaleKoEstimate() {
		return genderMaleKoEstimate;
	}

	public void setGenderMaleKoEstimate(Double genderMaleKoEstimate) {
		this.genderMaleKoEstimate = genderMaleKoEstimate;
	}

	public Double getGenderMaleKoStandardErrorEstimate() {
		return genderMaleKoStandardErrorEstimate;
	}

	public void setGenderMaleKoStandardErrorEstimate(Double genderMaleKoStandardErrorEstimate) {
		this.genderMaleKoStandardErrorEstimate = genderMaleKoStandardErrorEstimate;
	}

	public Double getGenderMaleKoPValue() {
		return genderMaleKoPValue;
	}

	public void setGenderMaleKoPValue(Double genderMaleKoPValue) {
		this.genderMaleKoPValue = genderMaleKoPValue;
	}

	public String getClassificationTag() {
		return classificationTag;
	}

	public void setClassificationTag(String classificationTag) {
		this.classificationTag = classificationTag;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		StatisticalResultFailed that = (StatisticalResultFailed) o;

		if (insertStatement != null ? !insertStatement.equals(that.insertStatement) : that.insertStatement != null)
			return false;
		if (batchSignificance != null ? !batchSignificance.equals(that.batchSignificance) : that.batchSignificance != null)
			return false;
		if (varianceSignificance != null ? !varianceSignificance.equals(that.varianceSignificance) : that.varianceSignificance != null)
			return false;
		if (nullTestSignificance != null ? !nullTestSignificance.equals(that.nullTestSignificance) : that.nullTestSignificance != null)
			return false;
		if (genotypeParameterEstimate != null ? !genotypeParameterEstimate.equals(that.genotypeParameterEstimate) : that.genotypeParameterEstimate != null)
			return false;
		if (genotypeStandardErrorEstimate != null ? !genotypeStandardErrorEstimate.equals(that.genotypeStandardErrorEstimate) : that.genotypeStandardErrorEstimate != null)
			return false;
		if (genotypeEffectPValue != null ? !genotypeEffectPValue.equals(that.genotypeEffectPValue) : that.genotypeEffectPValue != null)
			return false;
		if (genotypePercentageChange != null ? !genotypePercentageChange.equals(that.genotypePercentageChange) : that.genotypePercentageChange != null)
			return false;
		if (genderParameterEstimate != null ? !genderParameterEstimate.equals(that.genderParameterEstimate) : that.genderParameterEstimate != null)
			return false;
		if (genderStandardErrorEstimate != null ? !genderStandardErrorEstimate.equals(that.genderStandardErrorEstimate) : that.genderStandardErrorEstimate != null)
			return false;
		if (genderEffectPValue != null ? !genderEffectPValue.equals(that.genderEffectPValue) : that.genderEffectPValue != null)
			return false;
		if (weightParameterEstimate != null ? !weightParameterEstimate.equals(that.weightParameterEstimate) : that.weightParameterEstimate != null)
			return false;
		if (weightStandardErrorEstimate != null ? !weightStandardErrorEstimate.equals(that.weightStandardErrorEstimate) : that.weightStandardErrorEstimate != null)
			return false;
		if (weightEffectPValue != null ? !weightEffectPValue.equals(that.weightEffectPValue) : that.weightEffectPValue != null)
			return false;
		if (gp1Genotype != null ? !gp1Genotype.equals(that.gp1Genotype) : that.gp1Genotype != null) return false;
		if (gp1ResidualsNormalityTest != null ? !gp1ResidualsNormalityTest.equals(that.gp1ResidualsNormalityTest) : that.gp1ResidualsNormalityTest != null)
			return false;
		if (gp2Genotype != null ? !gp2Genotype.equals(that.gp2Genotype) : that.gp2Genotype != null) return false;
		if (gp2ResidualsNormalityTest != null ? !gp2ResidualsNormalityTest.equals(that.gp2ResidualsNormalityTest) : that.gp2ResidualsNormalityTest != null)
			return false;
		if (blupsTest != null ? !blupsTest.equals(that.blupsTest) : that.blupsTest != null) return false;
		if (rotatedResidualsNormalityTest != null ? !rotatedResidualsNormalityTest.equals(that.rotatedResidualsNormalityTest) : that.rotatedResidualsNormalityTest != null)
			return false;
		if (interceptEstimate != null ? !interceptEstimate.equals(that.interceptEstimate) : that.interceptEstimate != null)
			return false;
		if (interceptEstimateStandardError != null ? !interceptEstimateStandardError.equals(that.interceptEstimateStandardError) : that.interceptEstimateStandardError != null)
			return false;
		if (interactionSignificance != null ? !interactionSignificance.equals(that.interactionSignificance) : that.interactionSignificance != null)
			return false;
		if (interactionEffectPValue != null ? !interactionEffectPValue.equals(that.interactionEffectPValue) : that.interactionEffectPValue != null)
			return false;
		if (genderFemaleKoEstimate != null ? !genderFemaleKoEstimate.equals(that.genderFemaleKoEstimate) : that.genderFemaleKoEstimate != null)
			return false;
		if (genderFemaleKoStandardErrorEstimate != null ? !genderFemaleKoStandardErrorEstimate.equals(that.genderFemaleKoStandardErrorEstimate) : that.genderFemaleKoStandardErrorEstimate != null)
			return false;
		if (genderFemaleKoPValue != null ? !genderFemaleKoPValue.equals(that.genderFemaleKoPValue) : that.genderFemaleKoPValue != null)
			return false;
		if (genderMaleKoEstimate != null ? !genderMaleKoEstimate.equals(that.genderMaleKoEstimate) : that.genderMaleKoEstimate != null)
			return false;
		if (genderMaleKoStandardErrorEstimate != null ? !genderMaleKoStandardErrorEstimate.equals(that.genderMaleKoStandardErrorEstimate) : that.genderMaleKoStandardErrorEstimate != null)
			return false;
		if (genderMaleKoPValue != null ? !genderMaleKoPValue.equals(that.genderMaleKoPValue) : that.genderMaleKoPValue != null)
			return false;
		return !(classificationTag != null ? !classificationTag.equals(that.classificationTag) : that.classificationTag != null);

	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (insertStatement != null ? insertStatement.hashCode() : 0);
		result = 31 * result + (batchSignificance != null ? batchSignificance.hashCode() : 0);
		result = 31 * result + (varianceSignificance != null ? varianceSignificance.hashCode() : 0);
		result = 31 * result + (nullTestSignificance != null ? nullTestSignificance.hashCode() : 0);
		result = 31 * result + (genotypeParameterEstimate != null ? genotypeParameterEstimate.hashCode() : 0);
		result = 31 * result + (genotypeStandardErrorEstimate != null ? genotypeStandardErrorEstimate.hashCode() : 0);
		result = 31 * result + (genotypeEffectPValue != null ? genotypeEffectPValue.hashCode() : 0);
		result = 31 * result + (genotypePercentageChange != null ? genotypePercentageChange.hashCode() : 0);
		result = 31 * result + (genderParameterEstimate != null ? genderParameterEstimate.hashCode() : 0);
		result = 31 * result + (genderStandardErrorEstimate != null ? genderStandardErrorEstimate.hashCode() : 0);
		result = 31 * result + (genderEffectPValue != null ? genderEffectPValue.hashCode() : 0);
		result = 31 * result + (weightParameterEstimate != null ? weightParameterEstimate.hashCode() : 0);
		result = 31 * result + (weightStandardErrorEstimate != null ? weightStandardErrorEstimate.hashCode() : 0);
		result = 31 * result + (weightEffectPValue != null ? weightEffectPValue.hashCode() : 0);
		result = 31 * result + (gp1Genotype != null ? gp1Genotype.hashCode() : 0);
		result = 31 * result + (gp1ResidualsNormalityTest != null ? gp1ResidualsNormalityTest.hashCode() : 0);
		result = 31 * result + (gp2Genotype != null ? gp2Genotype.hashCode() : 0);
		result = 31 * result + (gp2ResidualsNormalityTest != null ? gp2ResidualsNormalityTest.hashCode() : 0);
		result = 31 * result + (blupsTest != null ? blupsTest.hashCode() : 0);
		result = 31 * result + (rotatedResidualsNormalityTest != null ? rotatedResidualsNormalityTest.hashCode() : 0);
		result = 31 * result + (interceptEstimate != null ? interceptEstimate.hashCode() : 0);
		result = 31 * result + (interceptEstimateStandardError != null ? interceptEstimateStandardError.hashCode() : 0);
		result = 31 * result + (interactionSignificance != null ? interactionSignificance.hashCode() : 0);
		result = 31 * result + (interactionEffectPValue != null ? interactionEffectPValue.hashCode() : 0);
		result = 31 * result + (genderFemaleKoEstimate != null ? genderFemaleKoEstimate.hashCode() : 0);
		result = 31 * result + (genderFemaleKoStandardErrorEstimate != null ? genderFemaleKoStandardErrorEstimate.hashCode() : 0);
		result = 31 * result + (genderFemaleKoPValue != null ? genderFemaleKoPValue.hashCode() : 0);
		result = 31 * result + (genderMaleKoEstimate != null ? genderMaleKoEstimate.hashCode() : 0);
		result = 31 * result + (genderMaleKoStandardErrorEstimate != null ? genderMaleKoStandardErrorEstimate.hashCode() : 0);
		result = 31 * result + (genderMaleKoPValue != null ? genderMaleKoPValue.hashCode() : 0);
		result = 31 * result + (classificationTag != null ? classificationTag.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "StatisticalResultMixedModel{" +
			"statisticalMethod='" + statisticalMethod + '\'' +
			", batchSignificance=" + batchSignificance +
			", varianceSignificance=" + varianceSignificance +
			", nullTestSignificance=" + nullTestSignificance +
			", genotypeParameterEstimate=" + genotypeParameterEstimate +
			", genotypeStandardErrorEstimate=" + genotypeStandardErrorEstimate +
			", genotypeEffectPValue=" + genotypeEffectPValue +
			", genotypePercentageChange='" + genotypePercentageChange + '\'' +
			", genderParameterEstimate=" + genderParameterEstimate +
			", genderStandardErrorEstimate=" + genderStandardErrorEstimate +
			", genderEffectPValue=" + genderEffectPValue +
			", weightParameterEstimate=" + weightParameterEstimate +
			", weightStandardErrorEstimate=" + weightStandardErrorEstimate +
			", weightEffectPValue=" + weightEffectPValue +
			", gp1Genotype='" + gp1Genotype + '\'' +
			", gp1ResidualsNormalityTest=" + gp1ResidualsNormalityTest +
			", gp2Genotype='" + gp2Genotype + '\'' +
			", gp2ResidualsNormalityTest=" + gp2ResidualsNormalityTest +
			", blupsTest=" + blupsTest +
			", rotatedResidualsNormalityTest=" + rotatedResidualsNormalityTest +
			", interceptEstimate=" + interceptEstimate +
			", interceptEstimateStandardError=" + interceptEstimateStandardError +
			", interactionSignificance=" + interactionSignificance +
			", interactionEffectPValue=" + interactionEffectPValue +
			", genderFemaleKoEstimate=" + genderFemaleKoEstimate +
			", genderFemaleKoStandardErrorEstimate=" + genderFemaleKoStandardErrorEstimate +
			", genderFemaleKoPValue=" + genderFemaleKoPValue +
			", genderMaleKoEstimate=" + genderMaleKoEstimate +
			", genderMaleKoStandardErrorEstimate=" + genderMaleKoStandardErrorEstimate +
			", genderMaleKoPValue=" + genderMaleKoPValue +
			", classificationTag='" + classificationTag + '\'' +
			'}';
	}
}
