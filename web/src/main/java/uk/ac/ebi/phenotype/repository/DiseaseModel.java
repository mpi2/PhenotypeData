package uk.ac.ebi.phenotype.repository;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class DiseaseModel {

    @GraphId
    Long id;

    private String diseaseId;
    private String diseaseTerm;
    private String diseaseClasses; // comma separated string
    //private List<String> diseaseAlts;
    private Boolean impcPredicted;
    private Boolean mgiPredicted;
    private Double rawScore;
    private Double diseaseToModelScore;
    private Double modelToDiseaseScore;

    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Gene gene;

    @Relationship(type="ALLELE", direction=Relationship.OUTGOING)
    private Allele allele;

    @Relationship(type="MOUSE_PHENOTYPE", direction=Relationship.OUTGOING)
    private Set<Mp> mousePhenotypes;

    @Relationship(type="HUMAN_PHENOTYPE", direction=Relationship.OUTGOING)
    private Set<Hp> humanPhenotypes;

    @Relationship(type="MOUSE_MODEL", direction=Relationship.OUTGOING)
    private MouseModel mouseModel;

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

    public String getDiseaseClasses() {
        return diseaseClasses;
    }

    public void setDiseaseClasses(String diseaseClasses) {
        this.diseaseClasses = diseaseClasses;
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

    public Double getRawScore() {
        return rawScore;
    }

    public void setRawScore(Double rawScore) {
        this.rawScore = rawScore;
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

    public MouseModel getMouseModel() {
        return mouseModel;
    }

    public void setMouseModel(MouseModel mouseModel) {
        this.mouseModel = mouseModel;
    }

    public Allele getAllele() {
        return allele;
    }

    public void setAllele(Allele allele) {
        this.allele = allele;
    }

    @Override
    public String toString() {
        return "DiseaseModel{" +
                "id=" + id +
                ", diseaseId='" + diseaseId + '\'' +
                ", diseaseTerm='" + diseaseTerm + '\'' +
                ", diseaseClasses='" + diseaseClasses + '\'' +
                ", impcPredicted=" + impcPredicted +
                ", mgiPredicted=" + mgiPredicted +
                ", rawScore=" + rawScore +
                ", diseaseToModelScore=" + diseaseToModelScore +
                ", modelToDiseaseScore=" + modelToDiseaseScore +
                ", gene=" + ((gene!=null && gene.getMarkerSymbol()!=null)?gene.getMarkerSymbol():"null") +
                ", allele=" + ((allele!=null && allele.getAlleleSymbol()!=null)?allele.getAlleleSymbol():"null") +
                ", mousePhenotypes=" + mousePhenotypes +
                ", humanPhenotypes=" + humanPhenotypes +
                ", mouseModel=" + mouseModel +
                '}';
    }
}

