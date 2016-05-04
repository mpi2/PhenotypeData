/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ebi.phenodigm.model;

/**
 * A PhenoDigm-specific gene concept. Here a Gene is really a pair of ortholog genes,
 * one of which is a human gene the other an ortholog in a model organism, and 
 * some status information.
 * Given that the source of the phenotype data in PhenoDigm is the model 
 * organism database it is important to acknowledge that there might not be a human
 * ortholog. In other words - be prepared for the human GeneIdentifier to be null.
 * 
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class Gene implements Comparable<Gene>{
    
    private final GeneIdentifier orthologGeneId;
    private final GeneIdentifier humanGeneId;

    /**
     * 
     * @param orthologGeneId
     * @param humanGeneId 
     */
    public Gene(GeneIdentifier orthologGeneId, GeneIdentifier humanGeneId) {
        this.orthologGeneId = orthologGeneId;
        this.humanGeneId = humanGeneId;
    }

    public GeneIdentifier getOrthologGeneId() {
        return orthologGeneId;
    }

    public GeneIdentifier getHumanGeneId() {
        return humanGeneId;
    }

    /**
     * Will return a comparison based on the natural ordering of the ortholog 
     * GeneIdentifier - by gene symbol.
     * 
     * For example Fgfr1 will be ordered before Fgfr2.
     * 
     * @param other
     * @return
     */
    @Override
    public int compareTo(Gene other) {
        return this.orthologGeneId.compareTo(other.orthologGeneId);
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.orthologGeneId != null ? this.orthologGeneId.hashCode() : 0);
        hash = 29 * hash + (this.humanGeneId != null ? this.humanGeneId.hashCode() : 0);
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
        final Gene other = (Gene) obj;
        if (this.orthologGeneId != other.orthologGeneId && (this.orthologGeneId == null || !this.orthologGeneId.equals(other.orthologGeneId))) {
            return false;
        }
        if (this.humanGeneId != other.humanGeneId && (this.humanGeneId == null || !this.humanGeneId.equals(other.humanGeneId))) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("Gene{ %s - %s }", orthologGeneId, humanGeneId);
    }  
    
}
