package uk.ac.ebi.phenotype.repository;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class MouseModel {

    @GraphId
    Long id;

    private int modelId;
    private String allelicComposition;
    private String geneticBackground;
    private String homHet;

    @Relationship(type="ALLELE", direction=Relationship.OUTGOING)
    private Set<Allele> alleles;

    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Gene gene;

    @Relationship(type="MOUSE_PHENOTYPE", direction=Relationship.OUTGOING)
    private Set<Mp> mousePhenotypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
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

    public String getHomHet() {
        return homHet;
    }

    public void setHomHet(String homHet) {
        this.homHet = homHet;
    }

    public Set<Allele> getAlleles() {
        return alleles;
    }

    public void setAlleles(Set<Allele> alleles) {
        this.alleles = alleles;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public Set<Mp> getMousePhenotypes() {
        return mousePhenotypes;
    }

    public void setMousePhenotypes(Set<Mp> mousePhenotypes) {
        this.mousePhenotypes = mousePhenotypes;
    }

    @Override
    public String toString() {
        return "MouseModel{" +
                "id=" + id +
                ", modelId=" + modelId +
                ", allelicComposition='" + allelicComposition + '\'' +
                ", geneticBackground='" + geneticBackground + '\'' +
                ", homHet='" + homHet + '\'' +
                ", alleles=" + alleles +
                ", gene=" + gene +
                ", mousePhenotypes=" + mousePhenotypes +
                '}';
    }
}

