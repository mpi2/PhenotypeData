package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckchen on 18/07/2017.
 */
public class AdvancedSearchDiseaseForm {

    // inputs and checkboxes
    private List<String> diseaseAssociationBy = new ArrayList<>();
    private Integer phenodigmLowerScore;
    private Integer phenodigmUpperScore;
    private String diseaseName;

    //customed output columns

    private Boolean showDiseaseTerm;
    private Boolean showDiseaseId;
    private Boolean showDiseaseClasses;
    private Boolean showDiseaseToModelScore;

    private Boolean showPredictedByImpc;
    private Boolean showPredictedByMgi;

    public List<String> getDiseaseAssociationBy() {
        return diseaseAssociationBy;
    }

    public void setDiseaseAssociationBy(List<String> diseaseAssociationBy) {
        this.diseaseAssociationBy = diseaseAssociationBy;
    }

    public Integer getPhenodigmLowerScore() {
        return phenodigmLowerScore;
    }

    public void setPhenodigmLowerScore(Integer phenodigmLowerScore) {
        this.phenodigmLowerScore = phenodigmLowerScore;
    }

    public Integer getPhenodigmUpperScore() {
        return phenodigmUpperScore;
    }

    public void setPhenodigmUpperScore(Integer phenodigmUpperScore) {
        this.phenodigmUpperScore = phenodigmUpperScore;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public Boolean getShowDiseaseTerm() {
        return showDiseaseTerm;
    }

    public void setShowDiseaseTerm(Boolean showDiseaseTerm) {
        this.showDiseaseTerm = showDiseaseTerm;
    }

    public Boolean getShowDiseaseId() {
        return showDiseaseId;
    }

    public void setShowDiseaseId(Boolean showDiseaseId) {
        this.showDiseaseId = showDiseaseId;
    }

    public Boolean getShowDiseaseClasses() {
        return showDiseaseClasses;
    }

    public void setShowDiseaseClasses(Boolean showDiseaseClasses) {
        this.showDiseaseClasses = showDiseaseClasses;
    }

    public Boolean getShowDiseaseToModelScore() {
        return showDiseaseToModelScore;
    }

    public void setShowDiseaseToModelScore(Boolean showDiseaseToModelScore) {
        this.showDiseaseToModelScore = showDiseaseToModelScore;
    }

    public Boolean getShowPredictedByImpc() {
        return showPredictedByImpc;
    }

    public void setShowPredictedByImpc(Boolean showPredictedByImpc) {
        this.showPredictedByImpc = showPredictedByImpc;
    }

    public Boolean getShowPredictedByMgi() {
        return showPredictedByMgi;
    }

    public void setShowPredictedByMgi(Boolean showPredictedByMgi) {
        this.showPredictedByMgi = showPredictedByMgi;
    }

    @Override
    public String toString() {
        return "AdvancedSearchDiseaseForm{" +
                "diseaseAssociationBy=" + diseaseAssociationBy +
                ", phenodigmLowerScore=" + phenodigmLowerScore +
                ", phenodigmUpperScore=" + phenodigmUpperScore +
                ", diseaseName='" + diseaseName + '\'' +
                ", showDiseaseTerm=" + showDiseaseTerm +
                ", showDiseaseId=" + showDiseaseId +
                ", showDiseaseClasses=" + showDiseaseClasses +
                ", showDiseaseToModelScore=" + showDiseaseToModelScore +
                ", showPredictedByImpc=" + showPredictedByImpc +
                ", showPredictedByMgi=" + showPredictedByMgi +
                '}';
    }
}
