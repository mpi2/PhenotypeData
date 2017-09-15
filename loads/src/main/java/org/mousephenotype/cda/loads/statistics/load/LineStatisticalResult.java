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
    private String batchIncluded;
    private String residualVariancesHomogeneity;
    private String genotypeContribution;
    private String genotypeEstimate;
    private String genotypeStandardError;
    private String genotypePVal;
    private String genotypePercentageChange;
    private String sexEstimate;
    private String sexStandardError;
    private String sexPVal;
    private String weightEstimate;
    private String weightStandardError;
    private String weightPVal;
    private String group1Genotype;
    private String group1ResidualsNormalityTest;
    private String group2Genotype;
    private String group2ResidualsNormalityTest;
    private String blupsTest;
    private String rotatedResidualsNormalityTest;
    private String interceptEstimate;
    private String interceptStandardError;
    private String interactionIncluded;
    private String interactionPVal;
    private String sexFvKOEstimate;
    private String sexFvKOStandardError;
    private String sexFvKOPVal;
    private String sexMvKOEstimate;
    private String sexMvKOStandardError;
    private String sexMvKOPVal;
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

    public String getBatchIncluded() {
        return batchIncluded;
    }

    public void setBatchIncluded(String batchIncluded) {
        this.batchIncluded = batchIncluded;
    }

    public String getResidualVariancesHomogeneity() {
        return residualVariancesHomogeneity;
    }

    public void setResidualVariancesHomogeneity(String residualVariancesHomogeneity) {
        this.residualVariancesHomogeneity = residualVariancesHomogeneity;
    }

    public String getGenotypeContribution() {
        return genotypeContribution;
    }

    public void setGenotypeContribution(String genotypeContribution) {
        this.genotypeContribution = genotypeContribution;
    }

    public String getGenotypeEstimate() {
        return genotypeEstimate;
    }

    public void setGenotypeEstimate(String genotypeEstimate) {
        this.genotypeEstimate = genotypeEstimate;
    }

    public String getGenotypeStandardError() {
        return genotypeStandardError;
    }

    public void setGenotypeStandardError(String genotypeStandardError) {
        this.genotypeStandardError = genotypeStandardError;
    }

    public String getGenotypePVal() {
        return genotypePVal;
    }

    public void setGenotypePVal(String genotypePVal) {
        this.genotypePVal = genotypePVal;
    }

    public String getGenotypePercentageChange() {
        return genotypePercentageChange;
    }

    public void setGenotypePercentageChange(String genotypePercentageChange) {
        this.genotypePercentageChange = genotypePercentageChange;
    }

    public String getSexEstimate() {
        return sexEstimate;
    }

    public void setSexEstimate(String sexEstimate) {
        this.sexEstimate = sexEstimate;
    }

    public String getSexStandardError() {
        return sexStandardError;
    }

    public void setSexStandardError(String sexStandardError) {
        this.sexStandardError = sexStandardError;
    }

    public String getSexPVal() {
        return sexPVal;
    }

    public void setSexPVal(String sexPVal) {
        this.sexPVal = sexPVal;
    }

    public String getWeightEstimate() {
        return weightEstimate;
    }

    public void setWeightEstimate(String weightEstimate) {
        this.weightEstimate = weightEstimate;
    }

    public String getWeightStandardError() {
        return weightStandardError;
    }

    public void setWeightStandardError(String weightStandardError) {
        this.weightStandardError = weightStandardError;
    }

    public String getWeightPVal() {
        return weightPVal;
    }

    public void setWeightPVal(String weightPVal) {
        this.weightPVal = weightPVal;
    }

    public String getGroup1Genotype() {
        return group1Genotype;
    }

    public void setGroup1Genotype(String group1Genotype) {
        this.group1Genotype = group1Genotype;
    }

    public String getGroup1ResidualsNormalityTest() {
        return group1ResidualsNormalityTest;
    }

    public void setGroup1ResidualsNormalityTest(String group1ResidualsNormalityTest) {
        this.group1ResidualsNormalityTest = group1ResidualsNormalityTest;
    }

    public String getGroup2Genotype() {
        return group2Genotype;
    }

    public void setGroup2Genotype(String group2Genotype) {
        this.group2Genotype = group2Genotype;
    }

    public String getGroup2ResidualsNormalityTest() {
        return group2ResidualsNormalityTest;
    }

    public void setGroup2ResidualsNormalityTest(String group2ResidualsNormalityTest) {
        this.group2ResidualsNormalityTest = group2ResidualsNormalityTest;
    }

    public String getBlupsTest() {
        return blupsTest;
    }

    public void setBlupsTest(String blupsTest) {
        this.blupsTest = blupsTest;
    }

    public String getRotatedResidualsNormalityTest() {
        return rotatedResidualsNormalityTest;
    }

    public void setRotatedResidualsNormalityTest(String rotatedResidualsNormalityTest) {
        this.rotatedResidualsNormalityTest = rotatedResidualsNormalityTest;
    }

    public String getInterceptEstimate() {
        return interceptEstimate;
    }

    public void setInterceptEstimate(String interceptEstimate) {
        this.interceptEstimate = interceptEstimate;
    }

    public String getInterceptStandardError() {
        return interceptStandardError;
    }

    public void setInterceptStandardError(String interceptStandardError) {
        this.interceptStandardError = interceptStandardError;
    }

    public String getInteractionIncluded() {
        return interactionIncluded;
    }

    public void setInteractionIncluded(String interactionIncluded) {
        this.interactionIncluded = interactionIncluded;
    }

    public String getInteractionPVal() {
        return interactionPVal;
    }

    public void setInteractionPVal(String interactionPVal) {
        this.interactionPVal = interactionPVal;
    }

    public String getSexFvKOEstimate() {
        return sexFvKOEstimate;
    }

    public void setSexFvKOEstimate(String sexFvKOEstimate) {
        this.sexFvKOEstimate = sexFvKOEstimate;
    }

    public String getSexFvKOStandardError() {
        return sexFvKOStandardError;
    }

    public void setSexFvKOStandardError(String sexFvKOStandardError) {
        this.sexFvKOStandardError = sexFvKOStandardError;
    }

    public String getSexFvKOPVal() {
        return sexFvKOPVal;
    }

    public void setSexFvKOPVal(String sexFvKOPVal) {
        this.sexFvKOPVal = sexFvKOPVal;
    }

    public String getSexMvKOEstimate() {
        return sexMvKOEstimate;
    }

    public void setSexMvKOEstimate(String sexMvKOEstimate) {
        this.sexMvKOEstimate = sexMvKOEstimate;
    }

    public String getSexMvKOStandardError() {
        return sexMvKOStandardError;
    }

    public void setSexMvKOStandardError(String sexMvKOStandardError) {
        this.sexMvKOStandardError = sexMvKOStandardError;
    }

    public String getSexMvKOPVal() {
        return sexMvKOPVal;
    }

    public void setSexMvKOPVal(String sexMvKOPVal) {
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

    public LineStatisticalResult() {
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
                ", batchIncluded='" + batchIncluded + '\'' +
                ", residualVariancesHomogeneity='" + residualVariancesHomogeneity + '\'' +
                ", genotypeContribution='" + genotypeContribution + '\'' +
                ", genotypeEstimate='" + genotypeEstimate + '\'' +
                ", genotypeStandardError='" + genotypeStandardError + '\'' +
                ", genotypePVal='" + genotypePVal + '\'' +
                ", genotypePercentageChange='" + genotypePercentageChange + '\'' +
                ", sexEstimate='" + sexEstimate + '\'' +
                ", sexStandardError='" + sexStandardError + '\'' +
                ", sexPVal='" + sexPVal + '\'' +
                ", weightEstimate='" + weightEstimate + '\'' +
                ", weightStandardError='" + weightStandardError + '\'' +
                ", weightPVal='" + weightPVal + '\'' +
                ", group1Genotype='" + group1Genotype + '\'' +
                ", group1ResidualsNormalityTest='" + group1ResidualsNormalityTest + '\'' +
                ", group2Genotype='" + group2Genotype + '\'' +
                ", group2ResidualsNormalityTest='" + group2ResidualsNormalityTest + '\'' +
                ", blupsTest='" + blupsTest + '\'' +
                ", rotatedResidualsNormalityTest='" + rotatedResidualsNormalityTest + '\'' +
                ", interceptEstimate='" + interceptEstimate + '\'' +
                ", interceptStandardError='" + interceptStandardError + '\'' +
                ", interactionIncluded='" + interactionIncluded + '\'' +
                ", interactionPVal='" + interactionPVal + '\'' +
                ", sexFvKOEstimate='" + sexFvKOEstimate + '\'' +
                ", sexFvKOStandardError='" + sexFvKOStandardError + '\'' +
                ", sexFvKOPVal='" + sexFvKOPVal + '\'' +
                ", sexMvKOEstimate='" + sexMvKOEstimate + '\'' +
                ", sexMvKOStandardError='" + sexMvKOStandardError + '\'' +
                ", sexMvKOPVal='" + sexMvKOPVal + '\'' +
                ", classificationTag='" + classificationTag + '\'' +
                ", additionalInformation='" + additionalInformation + '\'' +
                '}';
    }

    public LineStatisticalResult(String center, String pipeline, String procedure, String strain, String dataSource, String project, String weightAvailable, String metadataGroup, String zygosity, String colonyId, String status, String code, Integer countControlMale, Integer countControlFemale, Integer countMutantMale, Integer countMutantFemale, Double maleControlMean, Double femaleControlMean, Double maleMutantMean, Double femaleMutantMean, String controlSelection, String workflow, String statisticalMethod, String dependentVariable, String batchIncluded, String residualVariancesHomogeneity, String genotypeContribution, String genotypeEstimate, String genotypeStandardError, String genotypePVal, String genotypePercentageChange, String sexEstimate, String sexStandardError, String sexPVal, String weightEstimate, String weightStandardError, String weightPVal, String group1Genotype, String group1ResidualsNormalityTest, String group2Genotype, String group2ResidualsNormalityTest, String blupsTest, String rotatedResidualsNormalityTest, String interceptEstimate, String interceptStandardError, String interactionIncluded, String interactionPVal, String sexFvKOEstimate, String sexFvKOStandardError, String sexFvKOPVal, String sexMvKOEstimate, String sexMvKOStandardError, String sexMvKOPVal, String classificationTag, String additionalInformation) {
        this.center = center;
        this.pipeline = pipeline;
        this.procedure = procedure;
        this.strain = strain;
        this.dataSource = dataSource;
        this.project = project;
        this.weightAvailable = weightAvailable;
        this.metadataGroup = metadataGroup;
        this.zygosity = zygosity;
        this.colonyId = colonyId;
        this.status = status;
        this.code = code;
        this.countControlMale = countControlMale;
        this.countControlFemale = countControlFemale;
        this.countMutantMale = countMutantMale;
        this.countMutantFemale = countMutantFemale;
        this.maleControlMean = maleControlMean;
        this.femaleControlMean = femaleControlMean;
        this.maleMutantMean = maleMutantMean;
        this.femaleMutantMean = femaleMutantMean;
        this.controlSelection = controlSelection;
        this.workflow = workflow;
        this.statisticalMethod = statisticalMethod;
        this.dependentVariable = dependentVariable;
        this.batchIncluded = batchIncluded;
        this.residualVariancesHomogeneity = residualVariancesHomogeneity;
        this.genotypeContribution = genotypeContribution;
        this.genotypeEstimate = genotypeEstimate;
        this.genotypeStandardError = genotypeStandardError;
        this.genotypePVal = genotypePVal;
        this.genotypePercentageChange = genotypePercentageChange;
        this.sexEstimate = sexEstimate;
        this.sexStandardError = sexStandardError;
        this.sexPVal = sexPVal;
        this.weightEstimate = weightEstimate;
        this.weightStandardError = weightStandardError;
        this.weightPVal = weightPVal;
        this.group1Genotype = group1Genotype;
        this.group1ResidualsNormalityTest = group1ResidualsNormalityTest;
        this.group2Genotype = group2Genotype;
        this.group2ResidualsNormalityTest = group2ResidualsNormalityTest;
        this.blupsTest = blupsTest;
        this.rotatedResidualsNormalityTest = rotatedResidualsNormalityTest;
        this.interceptEstimate = interceptEstimate;
        this.interceptStandardError = interceptStandardError;
        this.interactionIncluded = interactionIncluded;
        this.interactionPVal = interactionPVal;
        this.sexFvKOEstimate = sexFvKOEstimate;
        this.sexFvKOStandardError = sexFvKOStandardError;
        this.sexFvKOPVal = sexFvKOPVal;
        this.sexMvKOEstimate = sexMvKOEstimate;
        this.sexMvKOStandardError = sexMvKOStandardError;
        this.sexMvKOPVal = sexMvKOPVal;
        this.classificationTag = classificationTag;
        this.additionalInformation = additionalInformation;
    }
}

