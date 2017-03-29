package uk.ac.ebi.phenotype.repository;

import org.mousephenotype.cda.solr.service.dto.PhenodigmDTO;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Disease {

    @GraphId
    Long id;

    private String diseaseId;
//    private List<String> diseaseClasses;
    private String diseaseTerm;
//    private List<String> diseaseAlts;
    private Boolean impcPredicted;
    private Boolean mgiPredicted;
    private Double diseaseToModelScore;
    private Double modelToDiseaseScore;

    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Gene gene;

    @Relationship(type="MOUSE_PHENOTYPE", direction=Relationship.OUTGOING)
    private Set<Mp> mousePhenotypes;

    @Relationship(type="HUMAN_PHENOTYPE", direction=Relationship.OUTGOING)
    private Set<Hp> humanPhenotypes;

//    @Relationship(type="MOUSE_MODEL", direction=Relationship.OUTGOING)
//    private Set<MouseModel> mouseModels;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(String diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getDiseaseTerm() {
        return diseaseTerm;
    }

    public void setDiseaseTerm(String diseaseTerm) {
        this.diseaseTerm = diseaseTerm;
    }

    public Boolean getImpcPredicted() {
        return impcPredicted;
    }

    public void setImpcPredicted(Boolean impcPredicted) {
        this.impcPredicted = impcPredicted;
    }

    public Boolean getMgiPredicted() {
        return mgiPredicted;
    }

    public void setMgiPredicted(Boolean mgiPredicted) {
        this.mgiPredicted = mgiPredicted;
    }

    public Double getDiseaseToModelScore() {
        return diseaseToModelScore;
    }

    public void setDiseaseToModelScore(Double diseaseToModelScore) {
        this.diseaseToModelScore = diseaseToModelScore;
    }

    public Double getModelToDiseaseScore() {
        return modelToDiseaseScore;
    }

    public void setModelToDiseaseScore(Double modelToDiseaseScore) {
        this.modelToDiseaseScore = modelToDiseaseScore;
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

    public Set<Hp> getHumanPhenotypes() {
        return humanPhenotypes;
    }

    public void setHumanPhenotypes(Set<Hp> humanPhenotypes) {
        this.humanPhenotypes = humanPhenotypes;
    }

    @Override
    public String toString() {
        return "Disease{" +
                "id=" + id +
                ", diseaseId='" + diseaseId + '\'' +
                ", diseaseTerm='" + diseaseTerm + '\'' +
                ", impcPredicted=" + impcPredicted +
                ", mgiPredicted=" + mgiPredicted +
                ", diseaseToModelScore=" + diseaseToModelScore +
                ", modelToDiseaseScore=" + modelToDiseaseScore +
                ", gene=" + gene +
                ", mousePhenotypes=" + mousePhenotypes +
                ", humanPhenotypes=" + humanPhenotypes +
                '}';
    }
}

