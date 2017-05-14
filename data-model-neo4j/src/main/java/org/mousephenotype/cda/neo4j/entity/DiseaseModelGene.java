package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.*;

/**
 * Created by ckchen on 14/03/2017.
 */
@RelationshipEntity(type = "DG")
public class DiseaseModelGene {

    Long id;

    @StartNode
    private DiseaseModel dideaseModel;
    @EndNode
    private Gene gene;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiseaseModel getDideaseModel() {
        return dideaseModel;
    }

    public void setDideaseModel(DiseaseModel dideaseModel) {
        this.dideaseModel = dideaseModel;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    @Override
    public String toString() {
        return "DiseaseModelGene{" +
                "id=" + id +
                ", dideaseModel=" + dideaseModel +
                ", gene=" + gene +
                '}';
    }
}