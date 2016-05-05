/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.phenodigm.model;

import java.util.List;

/**
 * Defines a mouse model for a genetic disease.
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class MouseModel implements Comparable<MouseModel> {
    
    private String mgiGeneId;
    
    private Integer mgiModelId;
    //source is the centre who did the phenotyping of the model.
    //MGI, MGP, IMPC, EUROPHENOME
    private String source;
    private String geneticBackground;

    private String allelicComposition;
    private String alleleIds;
    //link out to MGI - this is fiddly, so it's stored here for ease of reference
    private String allelicCompositionLink;
    
    private List<PhenotypeTerm> phenotypeTerms;

    public MouseModel() {
    }

    public String getMgiGeneId() {
        return mgiGeneId;
    }

    public void setMgiGeneId(String mgiGeneId) {
        this.mgiGeneId = mgiGeneId;
    }

    public Integer getMgiModelId() {
        return mgiModelId;
    }

    public void setMgiModelId(Integer mgiModelId) {
        this.mgiModelId = mgiModelId;
    }

    public String getAllelicComposition() {
        return allelicComposition;
    }

    public void setAllelicComposition(String allelicComposition) {
        this.allelicComposition = allelicComposition;
    }

    public String getGeneticBackground() {
        return geneticBackground;
    }

    public void setGeneticBackground(String geneticBackground) {
        this.geneticBackground = geneticBackground;
    }

    public String getAlleleIds() {
        return alleleIds;
    }

    public void setAlleleIds(String alleleIds) {
        this.alleleIds = alleleIds;
    }

    public String getAllelicCompositionLink() {
        return allelicCompositionLink;
    }

    public void setAllelicCompositionLink(String alleleicCompositionLink) {
        this.allelicCompositionLink = alleleicCompositionLink;
    }
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
        hash = 29 * hash + (this.mgiGeneId != null ? this.mgiGeneId.hashCode() : 0);
        hash = 29 * hash + (this.mgiModelId != null ? this.mgiModelId.hashCode() : 0);
        hash = 29 * hash + (this.allelicComposition != null ? this.allelicComposition.hashCode() : 0);
        hash = 29 * hash + (this.geneticBackground != null ? this.geneticBackground.hashCode() : 0);
        hash = 29 * hash + (this.alleleIds != null ? this.alleleIds.hashCode() : 0);
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
        final MouseModel other = (MouseModel) obj;
        if ((this.mgiGeneId == null) ? (other.mgiGeneId != null) : !this.mgiGeneId.equals(other.mgiGeneId)) {
            return false;
        }
        if ((this.mgiModelId == null) ? (other.mgiModelId != null) : !this.mgiModelId.equals(other.mgiModelId)) {
            return false;
        }
        if ((this.allelicComposition == null) ? (other.allelicComposition != null) : !this.allelicComposition.equals(other.allelicComposition)) {
            return false;
        }
        if ((this.geneticBackground == null) ? (other.geneticBackground != null) : !this.geneticBackground.equals(other.geneticBackground)) {
            return false;
        }
        if ((this.alleleIds == null) ? (other.alleleIds != null) : !this.alleleIds.equals(other.alleleIds)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(MouseModel other) {
        return this.mgiModelId.compareTo(other.mgiModelId);
    }
    
    @Override
    public String toString() {
        return String.format("MouseModel{%s_%s (%s) %s %s %s}", mgiGeneId, mgiModelId, source, allelicComposition, geneticBackground, alleleIds);
    }

}
