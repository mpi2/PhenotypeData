package org.mousephenotype.cda.loads.statistics.load;


import java.io.Serializable;
import java.util.List;

public class LightweightCategoricalResult extends LightweightResult implements Serializable {

    private static final long serialVersionUID = 3192619384147534299L;


    // ======================================
    // Meta information about the calculation
    String statisticalMethod = "Fisher's exact test";
    private List<String> categories;
	private String categoryA;
	private String categoryB;
    private Double pValue;
    private Double effectSize;
    private Double malePValue;
    private Double maleEffectSize;
    private Double femalePValue;
    private Double femaleEffectSize;
    private String classificationTag;



    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

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

    @Override
    public String getStatisticalMethod() {
        return statisticalMethod;
    }

    @Override
    public void setStatisticalMethod(String statisticalMethod) {
        this.statisticalMethod = statisticalMethod;
    }

    public Double getMalePValue() {
        return malePValue;
    }

    public void setMalePValue(Double malePValue) {
        this.malePValue = malePValue;
    }

    public Double getMaleEffectSize() {
        return maleEffectSize;
    }

    public void setMaleEffectSize(Double maleEffectSize) {
        this.maleEffectSize = maleEffectSize;
    }

    public Double getFemalePValue() {
        return femalePValue;
    }

    public void setFemalePValue(Double femalePValue) {
        this.femalePValue = femalePValue;
    }

    public Double getFemaleEffectSize() {
        return femaleEffectSize;
    }

    public void setFemaleEffectSize(Double femaleEffectSize) {
        this.femaleEffectSize = femaleEffectSize;
    }

    public String getClassificationTag() {
        return classificationTag;
    }

    public void setClassificationTag(String classificationTag) {
        this.classificationTag = classificationTag;
    }

    @Override
    public String toString() {
        return "LightweightCategoricalResult{" +
                "classificationTag='" + classificationTag + '\'' +
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
