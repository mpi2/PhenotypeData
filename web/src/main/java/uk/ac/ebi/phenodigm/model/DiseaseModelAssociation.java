/*
 * Copyright Â© 2011-2013 EMBL - European Bioinformatics Institute
 * and Genome Research Limited
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License.  
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.phenodigm.model;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;


/**
 * Contains the information relating a mouse model to a disease.
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class DiseaseModelAssociation implements Comparable<DiseaseModelAssociation> {
    
    private DiseaseIdentifier diseaseIdentifier;
    //need to add gene identifier here? 
    private MouseModel mouseModel;
    private double modelToDiseaseScore;
    private double diseaseToModelScore;
    private List<PhenotypeMatch> phenotypeMatches;

    private boolean hasLiteratureEvidence;
    
    public DiseaseModelAssociation() {
        hasLiteratureEvidence = false;
    }

    public DiseaseIdentifier getDiseaseIdentifier() {
        return diseaseIdentifier;
    }

    public void setDiseaseIdentifier(DiseaseIdentifier diseaseIdentifier) {
        this.diseaseIdentifier = diseaseIdentifier;
    }

    public MouseModel getMouseModel() {
        return mouseModel;
    }

    public void setMouseModel(MouseModel mouseModel) {
        this.mouseModel = mouseModel;
    }

    public double getModelToDiseaseScore() {
        return modelToDiseaseScore;
    }

    public void setModelToDiseaseScore(double modelToDiseaseScore) {
        this.modelToDiseaseScore = modelToDiseaseScore;
    }

    public double getDiseaseToModelScore() {
        return diseaseToModelScore;
    }

    public void setDiseaseToModelScore(double diseaseToModelScore) {
        this.diseaseToModelScore = diseaseToModelScore;
    }

    public List<PhenotypeMatch> getPhenotypeMatches() {
        return phenotypeMatches;
    }

    public void setPhenotypeMatches(List<PhenotypeMatch> phenotypeMatches) {
        this.phenotypeMatches = phenotypeMatches;
    }

    public boolean hasLiteratureEvidence() {
        return hasLiteratureEvidence;
    }

    public void setHasLiteratureEvidence(boolean hasLiteratureEvidence) {
        this.hasLiteratureEvidence = hasLiteratureEvidence;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.diseaseIdentifier);
        hash = 97 * hash + Objects.hashCode(this.mouseModel);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.modelToDiseaseScore) ^ (Double.doubleToLongBits(this.modelToDiseaseScore) >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.diseaseToModelScore) ^ (Double.doubleToLongBits(this.diseaseToModelScore) >>> 32));
        hash = 97 * hash + (this.hasLiteratureEvidence ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DiseaseModelAssociation other = (DiseaseModelAssociation) obj;
        if (!Objects.equals(this.diseaseIdentifier, other.diseaseIdentifier)) {
            return false;
        }
        if (!Objects.equals(this.mouseModel, other.mouseModel)) {
            return false;
        }
        if (Double.doubleToLongBits(this.modelToDiseaseScore) != Double.doubleToLongBits(other.modelToDiseaseScore)) {
            return false;
        }
        if (Double.doubleToLongBits(this.diseaseToModelScore) != Double.doubleToLongBits(other.diseaseToModelScore)) {
            return false;
        }
        if (this.hasLiteratureEvidence != other.hasLiteratureEvidence) {
            return false;
        }
        return true;
    }
        
    @Override
    public String toString() {
        return String.format("DiseaseAssociation{%s %s_%s hasLiteratureEvidence: %s Scores: [m2d=%s, d2m=%s] PhenotypeMatches: %s MouseModelPhenotypes: %s}", diseaseIdentifier, mouseModel.getMgiGeneId(), mouseModel.getMgiModelId(), hasLiteratureEvidence, modelToDiseaseScore, diseaseToModelScore, phenotypeMatches, mouseModel.getPhenotypeTerms());
    }

    public static Comparator<DiseaseModelAssociation> DiseaseToGeneScoreComparator = new Comparator<DiseaseModelAssociation>() {

        private static final int BEFORE = -1;
        private static final int EQUAL = 0;
        private static final int AFTER = 1;
        
        @Override
        public int compare(DiseaseModelAssociation o1, DiseaseModelAssociation o2) {
            if (o1.diseaseToModelScore > o2.diseaseToModelScore) {
                return BEFORE;
            }
            if (o1.diseaseToModelScore < o2.diseaseToModelScore) {
                return AFTER;
            }

            return EQUAL;
        }
        
    };
    
    public static Comparator<DiseaseModelAssociation> GeneToDiseaseScoreComparator  = new Comparator<DiseaseModelAssociation>() {

        private static final int BEFORE = -1;
        private static final int EQUAL = 0;
        private static final int AFTER = 1;
        
        @Override
        public int compare(DiseaseModelAssociation o1, DiseaseModelAssociation o2) {
            if (o1.modelToDiseaseScore > o2.modelToDiseaseScore) {
                return BEFORE;
            }
            if (o1.modelToDiseaseScore < o2.modelToDiseaseScore) {
                return AFTER;
            }

            return EQUAL;
        }
        
    };
    
    //need to specify comparator for different sorting criteria 
    @Override
    public int compareTo(DiseaseModelAssociation that) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
    
        if (this == that) {
            return EQUAL;
        }
        if (this.diseaseToModelScore == that.diseaseToModelScore) {
            if (! this.diseaseIdentifier.equals(that.diseaseIdentifier)) {
                return this.diseaseIdentifier.compareTo(that.diseaseIdentifier);                
            } else {
                return this.mouseModel.getMgiModelId().compareTo(that.mouseModel.getMgiModelId());
            }
        }
        if (this.diseaseToModelScore > that.diseaseToModelScore) {
            return BEFORE;
        }
        if (this.diseaseToModelScore < that.diseaseToModelScore) {
            return AFTER;
        }
        
        return AFTER;
    }
    
    
}
