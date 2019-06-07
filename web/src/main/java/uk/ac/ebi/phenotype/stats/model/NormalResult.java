package uk.ac.ebi.phenotype.stats.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class NormalResult {

	@JsonProperty("batch_included")
	private Boolean batchIncluded;// true,
	@JsonProperty("batch_p_val")
	private Double batchPValue;// 3.38268269546894e-06,
	@JsonProperty("residual_variances_homogeneity")
	private Boolean residualVariancesHomogeneity;// : false,
	@JsonProperty("residual_variances_homogeneity_p_val")
	private Double residualVariancesHomogeneityPVal;// : 0.0216828136119118,
	@JsonProperty("genotype_contribution")
	private Double genotypeContribution;// : 0.618217417150485,
	@JsonProperty("genotype_estimate")
	private Double genotypeEstimate;// : -0.0142470730062138,
	@JsonProperty("genotype_standard_error")
	private Double genotypeStandardError;// : 0.0292248626248843,
	@JsonProperty("genotype_p_val")
	private Double genotypePValue;// : 0.626780054354097,

	@JsonProperty("genotype_percentage_change")
	private String genotypePercentageChange;// : "Female: -32.4272586778729%, Male: -32.4272586778729%",

	@JsonProperty("sex_estimate")
	private Double sexEstimate;// : {},
	@JsonProperty("sex_standard_error")
	private Double sexStandardError;
	@JsonProperty("sex_p_val")
	private Double sexPValue;
	@JsonProperty("weight_estimate")
	private Double weightEstimate;
	@JsonProperty("weight_standard_error")
	private Double weightStandardError;
	@JsonProperty("weight_p_val")
	private Double weightPValue;
	@JsonProperty("gp1_genotype")
	private String gp1Genotype;// : "control",
	@JsonProperty("gp1_residuals_normality_test")
	private Double gp1ResidualsNormalTest;// : 7.37e-10,
	@JsonProperty("gp2_genotype")
	private String gp2Genotype;// : "experimental")
	@JsonProperty("gp2_residuals_normality_test")
	private Double gp2ResidualsNormalityTest;// : 4.85000879067445e-05,
	@JsonProperty("blups_test")
	private Double blurpsTest;// : 8.99274577563096e-10,
	@JsonProperty("rotated_residuals_normality_test")
	private Double rotatedResidualsNormalTest;// : 7.37e-10,
	@JsonProperty("intercept_estimate")
	private Double interceptEstimate;// : 0.0442470730062141,
	@JsonProperty("intercept_standard_error")
	private Double interceptStandardError;// : 0.0036142897908856,
	@JsonProperty("interaction_included")
	private Boolean interactionIncluded;// : false,
	public Boolean getBatchIncluded() {
		return batchIncluded;
	}

	public void setBatchIncluded(Boolean batchIncluded) {
		this.batchIncluded = batchIncluded;
	}

	public Double getBatchPValue() {
		return batchPValue;
	}

	public void setBatchPValue(Double batchPValue) {
		this.batchPValue = batchPValue;
	}

	public Boolean getResidualVariancesHomogeneity() {
		return residualVariancesHomogeneity;
	}

	public void setResidualVariancesHomogeneity(Boolean residualVariancesHomogeneity) {
		this.residualVariancesHomogeneity = residualVariancesHomogeneity;
	}

	public Double getResidualVariancesHomogeneityPVal() {
		return residualVariancesHomogeneityPVal;
	}

	public void setResidualVariancesHomogeneityPVal(Double residualVariancesHomogeneityPVal) {
		this.residualVariancesHomogeneityPVal = residualVariancesHomogeneityPVal;
	}

	public Double getGenotypeContribution() {
		return genotypeContribution;
	}

	public void setGenotypeContribution(Double genotypeContribution) {
		this.genotypeContribution = genotypeContribution;
	}

	public Double getGenotypeEstimate() {
		return genotypeEstimate;
	}

	public void setGenotypeEstimate(Double genotypeEstimate) {
		this.genotypeEstimate = genotypeEstimate;
	}

	public Double getGenotypeStandardError() {
		return genotypeStandardError;
	}

	public void setGenotypeStandardError(Double genotypeStandardError) {
		this.genotypeStandardError = genotypeStandardError;
	}

	public Double getGenotypePValue() {
		return genotypePValue;
	}

	public void setGenotypePValue(Double genotypePValue) {
		this.genotypePValue = genotypePValue;
	}

	public String getGenotypePercentageChange() {
		return genotypePercentageChange;
	}

	public void setGenotypePercentageChange(String genotypePercentageChange) {
		this.genotypePercentageChange = genotypePercentageChange;
	}

	public Double getSexEstimate() {
		return sexEstimate;
	}

	public void setSexEstimate(Double sexEstimate) {
		this.sexEstimate = sexEstimate;
	}

	public Double getSexStandardError() {
		return sexStandardError;
	}

	public void setSexStandardError(Double sexStandardError) {
		this.sexStandardError = sexStandardError;
	}

	public Double getSexPValue() {
		return sexPValue;
	}

	public void setSexPValue(Double sexPValue) {
		this.sexPValue = sexPValue;
	}

	public Double getWeightEstimate() {
		return weightEstimate;
	}

	public void setWeightEstimate(Double weightEstimate) {
		this.weightEstimate = weightEstimate;
	}

	public Double getWeightStandardError() {
		return weightStandardError;
	}

	public void setWeightStandardError(Double weightStandardError) {
		this.weightStandardError = weightStandardError;
	}

	public Double getWeightPValue() {
		return weightPValue;
	}

	public void setWeightPValue(Double weightPValue) {
		this.weightPValue = weightPValue;
	}

	public String getGp1Genotype() {
		return gp1Genotype;
	}

	public void setGp1Genotype(String gp1Genotype) {
		this.gp1Genotype = gp1Genotype;
	}

	public Double getGp1ResidualsNormalTest() {
		return gp1ResidualsNormalTest;
	}

	public void setGp1ResidualsNormalTest(Double gp1ResidualsNormalTest) {
		this.gp1ResidualsNormalTest = gp1ResidualsNormalTest;
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

	public Double getBlurpsTest() {
		return blurpsTest;
	}

	public void setBlurpsTest(Double blurpsTest) {
		this.blurpsTest = blurpsTest;
	}

	public Double getRotatedResidualsNormalTest() {
		return rotatedResidualsNormalTest;
	}

	public void setRotatedResidualsNormalTest(Double rotatedResidualsNormalTest) {
		this.rotatedResidualsNormalTest = rotatedResidualsNormalTest;
	}

	public Double getInterceptEstimate() {
		return interceptEstimate;
	}

	public void setInterceptEstimate(Double interceptEstimate) {
		this.interceptEstimate = interceptEstimate;
	}

	public Double getInterceptStandardError() {
		return interceptStandardError;
	}

	public void setInterceptStandardError(Double interceptStandardError) {
		this.interceptStandardError = interceptStandardError;
	}

	public Boolean getInteractionIncluded() {
		return interactionIncluded;
	}

	public void setInteractionIncluded(Boolean interactionIncluded) {
		this.interactionIncluded = interactionIncluded;
	}

	public Double getInteractionPVal() {
		return interactionPVal;
	}

	public void setInteractionPVal(Double interactionPVal) {
		this.interactionPVal = interactionPVal;
	}

	public Double getSexFvkoEstimate() {
		return sexFvkoEstimate;
	}

	public void setSexFvkoEstimate(Double sexFvkoEstimate) {
		this.sexFvkoEstimate = sexFvkoEstimate;
	}

	public Double getSexFvkoStandardError() {
		return sexFvkoStandardError;
	}

	public void setSexFvkoStandardError(Double sexFvkoStandardError) {
		this.sexFvkoStandardError = sexFvkoStandardError;
	}

	public Double getSexFvkoPVal() {
		return sexFvkoPVal;
	}

	public void setSexFvkoPVal(Double sexFvkoPVal) {
		this.sexFvkoPVal = sexFvkoPVal;
	}

	public Double getSexMykoEstimate() {
		return sexMykoEstimate;
	}

	public void setSexMykoEstimate(Double sexMykoEstimate) {
		this.sexMykoEstimate = sexMykoEstimate;
	}

	public Double getSexMvkoStandardError() {
		return sexMvkoStandardError;
	}

	public void setSexMvkoStandardError(Double sexMvkoStandardError) {
		this.sexMvkoStandardError = sexMvkoStandardError;
	}

	public Double getSexMvkoPVal() {
		return sexMvkoPVal;
	}

	public void setSexMvkoPVal(Double sexMvkoPVal) {
		this.sexMvkoPVal = sexMvkoPVal;
	}

	public String getClassificationTag() {
		return classificationTag;
	}

	public void setClassificationTag(String classificationTag) {
		this.classificationTag = classificationTag;
	}

	public String getTransformation() {
		return transformation;
	}

	public void setTransformation(String transformation) {
		this.transformation = transformation;
	}

	public AdditionalInformation getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(AdditionalInformation additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	public String getGenederIndcludedInAnalysis() {
		return genederIndcludedInAnalysis;
	}

	public void setGenederIndcludedInAnalysis(String genederIndcludedInAnalysis) {
		this.genederIndcludedInAnalysis = genederIndcludedInAnalysis;
	}

	public String getMultiBatchInAnalysis() {
		return multiBatchInAnalysis;
	}

	public void setMultiBatchInAnalysis(String multiBatchInAnalysis) {
		this.multiBatchInAnalysis = multiBatchInAnalysis;
	}

	@JsonProperty("interaction_p_val")
	private Double interactionPVal;// : 0.623132324183186,
	@JsonProperty("sex_fvko_estimate")
	private Double sexFvkoEstimate;// : {},
	@JsonProperty("sex_fvko_standard_error")
	private Double sexFvkoStandardError;// : {},
	@JsonProperty("sex_fvko_p_val")
	private Double sexFvkoPVal;// : {},
	@JsonProperty("sex_mvko_estimate")
	private Double sexMykoEstimate;// : {},
	@JsonProperty("sex_mvko_standard_error")
	private Double sexMvkoStandardError;// : {},
	@JsonProperty("sex_mvko_p_val")
	private Double sexMvkoPVal;// : {},
	@JsonProperty("classification_tag")
	private String classificationTag;// : "If phenotype is significant - both sexes equally",
	@JsonProperty("transformation")
	private String transformation;// : "lambda=NA, scaleShift=NA, transformed=FALSE, code=0",
	
	@JsonProperty("additional_information")
	private AdditionalInformation additionalInformation;// : {
	@JsonProperty("gender_included_in_analysis")
	private String genederIndcludedInAnalysis;// : "Both sexes included",
	@JsonProperty("multibatch_in_analysis")
	private String multiBatchInAnalysis;// : "Data contains multi batches",

	private String method;

	public String getDependentVariable() {
		return dependentVariable;
	}

	public void setDependentVariable(String dependentVariable) {
		this.dependentVariable = dependentVariable;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@JsonProperty("dependent_variable")
	private String dependentVariable;
	// question??? cant find "Batch effect significant" in stats file so where
	// should we get this from??
	// http://localhost:8090/phenotype-archive/charts?accession=MGI:1915747&parameter_stable_id=IMPC_HEM_038_001

}
