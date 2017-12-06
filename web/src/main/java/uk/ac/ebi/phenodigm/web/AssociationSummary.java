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
package uk.ac.ebi.phenodigm.web;

/**
 * Base class for Disease and GeneAssociationSummaries. 
 * This needs to be public otherwise there are issues with javax.el.ELException
 * being thrown.
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 * 
 * @deprecated pdsimplify
 */
public class AssociationSummary {
    
    private boolean associatedInHuman;
    private boolean hasLiteratureEvidence;
    private boolean inLocus;
    private String locus;
    private double bestModScore;
    private double bestHtpcScore;

    public AssociationSummary() {
    }

    public AssociationSummary(boolean associatedInHuman, boolean hasLiteratureEvidence, boolean inLocus, double bestMgiScore, double bestImpcScore) {
        this.associatedInHuman = associatedInHuman;
        this.hasLiteratureEvidence = hasLiteratureEvidence;
        this.inLocus = inLocus;
        this.bestModScore = bestMgiScore;
        this.bestHtpcScore = bestImpcScore;
    }  
    
    public boolean isAssociatedInHuman() {
        return associatedInHuman;
    }

    public void setAssociatedInHuman(boolean associatedInHuman) {
        this.associatedInHuman = associatedInHuman;
    }

    public boolean isHasLiteratureEvidence() {
        return hasLiteratureEvidence;
    }

    public void setHasLiteratureEvidence(boolean hasLiteratureEvidence) {
        this.hasLiteratureEvidence = hasLiteratureEvidence;
    }

    public boolean isInLocus() {
        return inLocus;
    }

    public void setInLocus(boolean inLocus) {
        this.inLocus = inLocus;
    }

    public String getLocus() {
        return locus;
    }

    public void setLocus(String locus) {
        this.locus = locus;
    }

    public double getBestModScore() {
        return bestModScore;
    }

    public void setBestModScore(double bestModScore) {
        this.bestModScore = bestModScore;
    }

    public double getBestHtpcScore() {
        return bestHtpcScore;
    }

    public void setBestHtpcScore(double bestHtpcScore) {
        this.bestHtpcScore = bestHtpcScore;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.associatedInHuman ? 1 : 0);
        hash = 59 * hash + (this.hasLiteratureEvidence ? 1 : 0);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.bestModScore) ^ (Double.doubleToLongBits(this.bestModScore) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.bestHtpcScore) ^ (Double.doubleToLongBits(this.bestHtpcScore) >>> 32));
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
        final AssociationSummary other = (AssociationSummary) obj;
        if (this.associatedInHuman != other.associatedInHuman) {
            return false;
        }
        if (this.hasLiteratureEvidence != other.hasLiteratureEvidence) {
            return false;
        }
        if (this.inLocus != other.inLocus) {
            return false;
        }
        if (Double.doubleToLongBits(this.bestModScore) != Double.doubleToLongBits(other.bestModScore)) {
            return false;
        }
        return true;
    }

    
    @Override
    public String toString() {
        return "AssociationSummary{" + "associatedInHuman=" + associatedInHuman + ", hasLiteratureEvidence=" + hasLiteratureEvidence + ", inLocus=" + inLocus + ", locus=" + locus + ", bestMgiScore=" + bestModScore + ", bestImpcScore=" + bestHtpcScore + '}';
    }   
}
