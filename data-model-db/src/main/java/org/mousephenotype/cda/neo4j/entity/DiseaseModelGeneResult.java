package org.mousephenotype.cda.neo4j.entity;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Set;

/**
 * Created by ckchen on 07/04/2017.
 */

@QueryResult
public class DiseaseModelGeneResult {

    Set<DiseaseModel> diseaseModel;
    Gene gene;
    Set<Allele> allele;
    Set<MouseModel> mouseModel;

    public Set<DiseaseModel> getDiseaseModel() {
        return diseaseModel;
    }

    public void setDiseaseModel(Set<DiseaseModel> diseaseModel) {
        this.diseaseModel = diseaseModel;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public Set<Allele> getAllele() {
        return allele;
    }

    public void setAllele(Set<Allele> allele) {
        this.allele = allele;
    }

    public Set<MouseModel> getMouseModel() {
        return mouseModel;
    }

    public void setMouseModel(Set<MouseModel> mouseModel) {
        this.mouseModel = mouseModel;
    }

    @Override
    public String toString() {
        return "DiseaseModelGeneResult{" +
                "diseaseModel=" + diseaseModel +
                ", gene=" + gene +
                ", allele=" + allele +
                ", mouseModel=" + mouseModel +
                '}';
    }
}
