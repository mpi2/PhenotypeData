package uk.ac.ebi.phenotype.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckchen on 18/07/2017.
 */
public class AdvancedSearchDiseaseForm {

    // inputs and checkboxes
    private Boolean isHumanCurated;
    private Integer phenodigmLowerScore = 0; // default
    private Integer phenodigmUpperScore = 100; // default
    private String humanDiseaseTerm;

    //customed output columns

    private Boolean showDiseaseTerm;
    private Boolean showDiseaseId;
    private Boolean showDiseaseClasses;
    private Boolean showDiseaseToModelScore;

    private Boolean showImpcPredicted;
    private Boolean showMgiPredicted;

    private Boolean hasOutputColumn;

    public Boolean getHumanCurated() {
        return isHumanCurated;
    }

    public void setHumanCurated(Boolean humanCurated) {
        isHumanCurated = humanCurated;
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

    public String getHumanDiseaseTerm() {
        return humanDiseaseTerm;
    }

    public void setHumanDiseaseTerm(String humanDiseaseTerm) {
        this.humanDiseaseTerm = humanDiseaseTerm;
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

    public Boolean getShowImpcPredicted() {
        return showImpcPredicted;
    }

    public void setShowImpcPredicted(Boolean showImpcPredicted) {
        this.showImpcPredicted = showImpcPredicted;
    }

    public Boolean getShowMgiPredicted() {
        return showMgiPredicted;
    }

    public void setShowMgiPredicted(Boolean showMgiPredicted) {
        this.showMgiPredicted = showMgiPredicted;
    }

    public Boolean getHasOutputColumn() {
        return hasOutputColumn;
    }

    public void setHasOutputColumn(Boolean hasOutputColumn) {
        this.hasOutputColumn = hasOutputColumn;
    }

    @Override
    public String toString() {
        return "AdvancedSearchDiseaseForm{" +
                "isHumanCurated=" + isHumanCurated +
                ", phenodigmLowerScore=" + phenodigmLowerScore +
                ", phenodigmUpperScore=" + phenodigmUpperScore +
                ", humanDiseaseTerm='" + humanDiseaseTerm + '\'' +
                ", showDiseaseTerm=" + showDiseaseTerm +
                ", showDiseaseId=" + showDiseaseId +
                ", showDiseaseClasses=" + showDiseaseClasses +
                ", showDiseaseToModelScore=" + showDiseaseToModelScore +
                ", showImpcPredicted=" + showImpcPredicted +
                ", showMgiPredicted=" + showMgiPredicted +
                ", hasOutputColumn=" + hasOutputColumn +
                '}';
    }
}

