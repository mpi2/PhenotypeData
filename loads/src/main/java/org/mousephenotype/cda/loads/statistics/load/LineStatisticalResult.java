package org.mousephenotype.cda.loads.statistics.load;

public class LineStatisticalResult {


    // From the filename
    private String center;
    private String pipeline;
    private String procedure;
    private String strain;
    private String dataSource;
    private String project;

    private String weightAvailable;
    private String metadataGroup;
    private String zygosity;
    private String colonyId;
    private String status;
    private String code;
    private Integer countControlMale;
    private Integer countControlFemale;
    private Integer countMutantMale;
    private Integer countMutantFemale;

    private Double maleControlMean;
    private Double femaleControlMean;
    private Double maleMutantMean;
    private Double femaleMutantMean;

    private String controlSelection;
    private String workflow;

    private String statisticalMethod;
    private String dependentVariable;

    private Boolean batchIncluded;
    private Boolean residualVariancesHomogeneity;
    private Double genotypeContribution;
    private Double genotypeEstimate;
    private Double genotypeStandardError;
    private Double genotypePVal;
    private String genotypePercentageChange;
    private Double sexEstimate;
    private Double sexStandardError;
    private Double sexPVal;
    private Double weightEstimate;
    private Double weightStandardError;
    private Double weightPVal;
    private String group1Genotype;
    private Double group1ResidualsNormalityTest;
    private String group2Genotype;
    private Double group2ResidualsNormalityTest;
    private Double blupsTest;
    private Double rotatedResidualsNormalityTest;
    private Double interceptEstimate;
    private Double interceptStandardError;
    private Boolean interactionIncluded;
    private Double interactionPVal;
    private Double sexFvKOEstimate;
    private Double sexFvKOStandardError;
    private Double sexFvKOPVal;
    private Double sexMvKOEstimate;
    private Double sexMvKOStandardError;
    private Double sexMvKOPVal;

    private String classificationTag;
    private String additionalInformation;

    public String getDataSource() {
        return dataSource;
    }

    public String getProject() {
        return project;
    }

    public String getWeightAvailable() {
        return weightAvailable;
    }

    public void setWeightAvailable(String weightAvailable) {
        this.weightAvailable = weightAvailable;
    }

    public String getDataSourceName() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getProjectName() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public String getControlSelection() {
        return controlSelection;
    }

    public void setControlSelection(String controlSelection) {
        this.controlSelection = controlSelection;
    }

    public Double getMaleControlMean() {
        return maleControlMean;
    }

    public void setMaleControlMean(Double maleControlMean) {
        this.maleControlMean = maleControlMean;
    }

    public Double getFemaleControlMean() {
        return femaleControlMean;
    }

    public void setFemaleControlMean(Double femaleControlMean) {
        this.femaleControlMean = femaleControlMean;
    }

    public Double getMaleMutantMean() {
        return maleMutantMean;
    }

    public void setMaleMutantMean(Double maleMutantMean) {
        this.maleMutantMean = maleMutantMean;
    }

    public Double getFemaleMutantMean() {
        return femaleMutantMean;
    }

    public void setFemaleMutantMean(Double femaleMutantMean) {
        this.femaleMutantMean = femaleMutantMean;
    }

    public String getMetadataGroup() {
        return metadataGroup;
    }

    public void setMetadataGroup(String metadataGroup) {
        this.metadataGroup = metadataGroup;
    }

    public String getZygosity() {
        return zygosity;
    }

    public void setZygosity(String zygosity) {
        this.zygosity = zygosity;
    }

    public String getColonyId() {
        return colonyId;
    }

    public void setColonyId(String colonyId) {
        this.colonyId = colonyId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getCountControlMale() {
        return countControlMale;
    }

    public void setCountControlMale(Integer countControlMale) {
        this.countControlMale = countControlMale;
    }

    public Integer getCountControlFemale() {
        return countControlFemale;
    }

    public void setCountControlFemale(Integer countControlFemale) {
        this.countControlFemale = countControlFemale;
    }

    public Integer getCountMutantMale() {
        return countMutantMale;
    }

    public void setCountMutantMale(Integer countMutantMale) {
        this.countMutantMale = countMutantMale;
    }

    public Integer getCountMutantFemale() {
        return countMutantFemale;
    }

    public void setCountMutantFemale(Integer countMutantFemale) {
        this.countMutantFemale = countMutantFemale;
    }

    public String getStatisticalMethod() {
        return statisticalMethod;
    }

    public void setStatisticalMethod(String statisticalMethod) {
        this.statisticalMethod = statisticalMethod;
    }

    public String getDependentVariable() {
        return dependentVariable;
    }

    public void setDependentVariable(String dependentVariable) {
        this.dependentVariable = dependentVariable;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getStrain() {
        return strain;
    }

    public void setStrain(String strain) {
        this.strain = strain;
    }

    public Boolean getBatchIncluded() {
        return batchIncluded;
    }

    public void setBatchIncluded(Boolean batchIncluded) {
        this.batchIncluded = batchIncluded;
    }

    public Boolean getResidualVariancesHomogeneity() {
        return residualVariancesHomogeneity;
    }

    public void setResidualVariancesHomogeneity(Boolean residualVariancesHomogeneity) {
        this.residualVariancesHomogeneity = residualVariancesHomogeneity;
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

    public Double getGenotypePVal() {
        return genotypePVal;
    }

    public void setGenotypePVal(Double genotypePVal) {
        this.genotypePVal = genotypePVal;
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

    public Double getSexPVal() {
        return sexPVal;
    }

    public void setSexPVal(Double sexPVal) {
        this.sexPVal = sexPVal;
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

    public Double getWeightPVal() {
        return weightPVal;
    }

    public void setWeightPVal(Double weightPVal) {
        this.weightPVal = weightPVal;
    }

    public String getGroup1Genotype() {
        return group1Genotype;
    }

    public void setGroup1Genotype(String group1Genotype) {
        this.group1Genotype = group1Genotype;
    }

    public Double getGroup1ResidualsNormalityTest() {
        return group1ResidualsNormalityTest;
    }

    public void setGroup1ResidualsNormalityTest(Double group1ResidualsNormalityTest) {
        this.group1ResidualsNormalityTest = group1ResidualsNormalityTest;
    }

    public String getGroup2Genotype() {
        return group2Genotype;
    }

    public void setGroup2Genotype(String group2Genotype) {
        this.group2Genotype = group2Genotype;
    }

    public Double getGroup2ResidualsNormalityTest() {
        return group2ResidualsNormalityTest;
    }

    public void setGroup2ResidualsNormalityTest(Double group2ResidualsNormalityTest) {
        this.group2ResidualsNormalityTest = group2ResidualsNormalityTest;
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

    public Double getSexFvKOEstimate() {
        return sexFvKOEstimate;
    }

    public void setSexFvKOEstimate(Double sexFvKOEstimate) {
        this.sexFvKOEstimate = sexFvKOEstimate;
    }

    public Double getSexFvKOStandardError() {
        return sexFvKOStandardError;
    }

    public void setSexFvKOStandardError(Double sexFvKOStandardError) {
        this.sexFvKOStandardError = sexFvKOStandardError;
    }

    public Double getSexFvKOPVal() {
        return sexFvKOPVal;
    }

    public void setSexFvKOPVal(Double sexFvKOPVal) {
        this.sexFvKOPVal = sexFvKOPVal;
    }

    public Double getSexMvKOEstimate() {
        return sexMvKOEstimate;
    }

    public void setSexMvKOEstimate(Double sexMvKOEstimate) {
        this.sexMvKOEstimate = sexMvKOEstimate;
    }

    public Double getSexMvKOStandardError() {
        return sexMvKOStandardError;
    }

    public void setSexMvKOStandardError(Double sexMvKOStandardError) {
        this.sexMvKOStandardError = sexMvKOStandardError;
    }

    public Double getSexMvKOPVal() {
        return sexMvKOPVal;
    }

    public void setSexMvKOPVal(Double sexMvKOPVal) {
        this.sexMvKOPVal = sexMvKOPVal;
    }

    public String getClassificationTag() {
        return classificationTag;
    }

    public void setClassificationTag(String classificationTag) {
        this.classificationTag = classificationTag;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String toString() {
        return "LineStatisticalResult{" +
                "center='" + center + '\'' +
                ", pipeline='" + pipeline + '\'' +
                ", procedure='" + procedure + '\'' +
                ", strain='" + strain + '\'' +
                ", dataSource='" + dataSource + '\'' +
                ", project='" + project + '\'' +
                ", weightAvailable='" + weightAvailable + '\'' +
                ", metadataGroup='" + metadataGroup + '\'' +
                ", zygosity='" + zygosity + '\'' +
                ", colonyId='" + colonyId + '\'' +
                ", status='" + status + '\'' +
                ", code='" + code + '\'' +
                ", countControlMale=" + countControlMale +
                ", countControlFemale=" + countControlFemale +
                ", countMutantMale=" + countMutantMale +
                ", countMutantFemale=" + countMutantFemale +
                ", maleControlMean=" + maleControlMean +
                ", femaleControlMean=" + femaleControlMean +
                ", maleMutantMean=" + maleMutantMean +
                ", femaleMutantMean=" + femaleMutantMean +
                ", controlSelection='" + controlSelection + '\'' +
                ", workflow='" + workflow + '\'' +
                ", statisticalMethod='" + statisticalMethod + '\'' +
                ", dependentVariable='" + dependentVariable + '\'' +
                ", batchIncluded=" + batchIncluded +
                ", residualVariancesHomogeneity=" + residualVariancesHomogeneity +
                ", genotypeContribution=" + genotypeContribution +
                ", genotypeEstimate=" + genotypeEstimate +
                ", genotypeStandardError=" + genotypeStandardError +
                ", genotypePVal=" + genotypePVal +
                ", genotypePercentageChange='" + genotypePercentageChange + '\'' +
                ", sexEstimate=" + sexEstimate +
                ", sexStandardError=" + sexStandardError +
                ", sexPVal=" + sexPVal +
                ", weightEstimate=" + weightEstimate +
                ", weightStandardError=" + weightStandardError +
                ", weightPVal=" + weightPVal +
                ", group1Genotype='" + group1Genotype + '\'' +
                ", group1ResidualsNormalityTest=" + group1ResidualsNormalityTest +
                ", group2Genotype='" + group2Genotype + '\'' +
                ", group2ResidualsNormalityTest=" + group2ResidualsNormalityTest +
                ", blupsTest=" + blupsTest +
                ", rotatedResidualsNormalityTest=" + rotatedResidualsNormalityTest +
                ", interceptEstimate=" + interceptEstimate +
                ", interceptStandardError=" + interceptStandardError +
                ", interactionIncluded=" + interactionIncluded +
                ", interactionPVal=" + interactionPVal +
                ", sexFvKOEstimate=" + sexFvKOEstimate +
                ", sexFvKOStandardError=" + sexFvKOStandardError +
                ", sexFvKOPVal=" + sexFvKOPVal +
                ", sexMvKOEstimate=" + sexMvKOEstimate +
                ", sexMvKOStandardError=" + sexMvKOStandardError +
                ", sexMvKOPVal=" + sexMvKOPVal +
                ", classificationTag='" + classificationTag + '\'' +
                ", additionalInformation='" + additionalInformation + '\'' +
                '}';
    }
}

