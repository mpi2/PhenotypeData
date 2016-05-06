/*
 * Copyright © 2011-2013 EMBL - European Bioinformatics Institute
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

import java.util.List;

/**
 * Disease bean representing a genetic disease.
 * 
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class Disease implements Comparable<Disease>{
    
    private DiseaseIdentifier diseaseIdentifier;
    private String term;
    private List<String> alternativeTerms;
    private String locus;
    private List<String> classes;
    private List<PhenotypeTerm> phenotypeTerms;
    
    public Disease() {
    }
    
    /**
     * Convenience constructor - will create a new Disease with a new DiseaseIdentifier
     * being made from the provided diseaseId.
     * 
     * @param diseaseId 
     */
    public Disease(String diseaseId) {
        this.diseaseIdentifier = new DiseaseIdentifier(diseaseId);
    }
    
    public Disease(DiseaseIdentifier diseaseIdentifier) {
        this.diseaseIdentifier = diseaseIdentifier;
    }
    
    public String getDiseaseId() {
        return diseaseIdentifier.getCompoundIdentifier();
    }

    public DiseaseIdentifier getDiseaseIdentifier() {
        return diseaseIdentifier;
    }
    
    public void setDiseaseIdentifier(DiseaseIdentifier diseaseIdentifier) {
        this.diseaseIdentifier = diseaseIdentifier;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<String> getAlternativeTerms() {
        return alternativeTerms;
    }

    public void setAlternativeTerms(List<String> alternativeTerms) {
        this.alternativeTerms = alternativeTerms;
    }

    public String getLocus() {
        return locus;
    }

    public void setLocus(String locus) {
        this.locus = locus;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public List<PhenotypeTerm> getPhenotypeTerms() {
        return phenotypeTerms;
    }

    public void setPhenotypeTerms(List<PhenotypeTerm> phenotypeTerms) {
        this.phenotypeTerms = phenotypeTerms;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.diseaseIdentifier != null ? this.diseaseIdentifier.hashCode() : 0);
        hash = 53 * hash + (this.term != null ? this.term.hashCode() : 0);
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
        final Disease other = (Disease) obj;
        if (this.diseaseIdentifier != other.diseaseIdentifier && (this.diseaseIdentifier == null || !this.diseaseIdentifier.equals(other.diseaseIdentifier))) {
            return false;
        }
        if ((this.term == null) ? (other.term != null) : !this.term.equals(other.term)) {
            return false;
        }
        return true;
    }

    
    @Override
    public int compareTo(Disease t) {
        return this.diseaseIdentifier.compareTo(t.diseaseIdentifier);
    }
    
    @Override
    public String toString() {
        return "Disease{" + diseaseIdentifier + " - " + term + ", alternativeTerms=" + alternativeTerms + ", locus=" + locus + ", classes=" + classes + "}";
    }    
    
}
