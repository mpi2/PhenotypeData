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
    private String diseaseTerm;
    private String diseaseLocus;
    private String hgncGeneId;
    private String hgncGeneSymbol;
    //private List<String> diseaseClasses;
    //private List<String> diseaseAlts;
    private Boolean inLocus;
    private Boolean impcPredicted;
    private Boolean mgiPredicted;
    private Boolean humanCurated;
    private Boolean mouseCurated;
    private Boolean mgiPredictedKnownGene;
    private Boolean impcPredictedKnownGene;
    private Boolean mgiNovelPredictedInLocus;
    private Boolean impcNovelPredictedInLocus;

    // <!--model organism database (MGI) scores-->
    private Double maxMgiD2mScore;
    private Double maxMgiM2dScore;

    // <!--IMPC scores-->
    private Double maxImpcD2mScore;
    private Double maxImpcM2dScore;

    // <!--raw scores-->
    private Double rawModScore;
    private Double rawHtpcScore;

    private Double diseaseToModelScore;
    private Double modelToDiseaseScore;


    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Gene gene;

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

    public String getDiseaseLocus() {
        return diseaseLocus;
    }

    public void setDiseaseLocus(String diseaseLocus) {
        this.diseaseLocus = diseaseLocus;
    }

    public String getHgncGeneId() {
        return hgncGeneId;
    }

    public void setHgncGeneId(String hgncGeneId) {
        this.hgncGeneId = hgncGeneId;
    }

    public String getHgncGeneSymbol() {
        return hgncGeneSymbol;
    }

    public void setHgncGeneSymbol(String hgncGeneSymbol) {
        this.hgncGeneSymbol = hgncGeneSymbol;
    }

    public Boolean getInLocus() {
        return inLocus;
    }

    public void setInLocus(Boolean inLocus) {
        this.inLocus = inLocus;
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

    public Boolean getHumanCurated() {
        return humanCurated;
    }

    public void setHumanCurated(Boolean humanCurated) {
        this.humanCurated = humanCurated;
    }

    public Boolean getMouseCurated() {
        return mouseCurated;
    }

    public void setMouseCurated(Boolean mouseCurated) {
        this.mouseCurated = mouseCurated;
    }

    public Boolean getMgiPredictedKnownGene() {
        return mgiPredictedKnownGene;
    }

    public void setMgiPredictedKnownGene(Boolean mgiPredictedKnownGene) {
        this.mgiPredictedKnownGene = mgiPredictedKnownGene;
    }

    public Boolean getImpcPredictedKnownGene() {
        return impcPredictedKnownGene;
    }

    public void setImpcPredictedKnownGene(Boolean impcPredictedKnownGene) {
        this.impcPredictedKnownGene = impcPredictedKnownGene;
    }

    public Boolean getMgiNovelPredictedInLocus() {
        return mgiNovelPredictedInLocus;
    }

    public void setMgiNovelPredictedInLocus(Boolean mgiNovelPredictedInLocus) {
        this.mgiNovelPredictedInLocus = mgiNovelPredictedInLocus;
    }

    public Boolean getImpcNovelPredictedInLocus() {
        return impcNovelPredictedInLocus;
    }

    public void setImpcNovelPredictedInLocus(Boolean impcNovelPredictedInLocus) {
        this.impcNovelPredictedInLocus = impcNovelPredictedInLocus;
    }

    public Double getMaxMgiD2mScore() {
        return maxMgiD2mScore;
    }

    public void setMaxMgiD2mScore(Double maxMgiD2mScore) {
        this.maxMgiD2mScore = maxMgiD2mScore;
    }

    public Double getMaxMgiM2dScore() {
        return maxMgiM2dScore;
    }

    public void setMaxMgiM2dScore(Double maxMgiM2dScore) {
        this.maxMgiM2dScore = maxMgiM2dScore;
    }

    public Double getMaxImpcD2mScore() {
        return maxImpcD2mScore;
    }

    public void setMaxImpcD2mScore(Double maxImpcD2mScore) {
        this.maxImpcD2mScore = maxImpcD2mScore;
    }

    public Double getMaxImpcM2dScore() {
        return maxImpcM2dScore;
    }

    public void setMaxImpcM2dScore(Double maxImpcM2dScore) {
        this.maxImpcM2dScore = maxImpcM2dScore;
    }

    public Double getRawModScore() {
        return rawModScore;
    }

    public void setRawModScore(Double rawModScore) {
        this.rawModScore = rawModScore;
    }

    public Double getRawHtpcScore() {
        return rawHtpcScore;
    }

    public void setRawHtpcScore(Double rawHtpcScore) {
        this.rawHtpcScore = rawHtpcScore;
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

    @Override
    public String toString() {
        return "Disease{" +
                "id=" + id +
                ", diseaseId='" + diseaseId + '\'' +
                ", diseaseTerm='" + diseaseTerm + '\'' +
                ", diseaseLocus='" + diseaseLocus + '\'' +
                ", hgncGeneId='" + hgncGeneId + '\'' +
                ", hgncGeneSymbol='" + hgncGeneSymbol + '\'' +
                ", inLocus=" + inLocus +
                ", impcPredicted=" + impcPredicted +
                ", mgiPredicted=" + mgiPredicted +
                ", humanCurated=" + humanCurated +
                ", mouseCurated=" + mouseCurated +
                ", mgiPredictedKnownGene=" + mgiPredictedKnownGene +
                ", impcPredictedKnownGene=" + impcPredictedKnownGene +
                ", mgiNovelPredictedInLocus=" + mgiNovelPredictedInLocus +
                ", impcNovelPredictedInLocus=" + impcNovelPredictedInLocus +
                ", maxMgiD2mScore=" + maxMgiD2mScore +
                ", maxMgiM2dScore=" + maxMgiM2dScore +
                ", maxImpcD2mScore=" + maxImpcD2mScore +
                ", maxImpcM2dScore=" + maxImpcM2dScore +
                ", rawModScore=" + rawModScore +
                ", rawHtpcScore=" + rawHtpcScore +
                ", diseaseToModelScore=" + diseaseToModelScore +
                ", modelToDiseaseScore=" + modelToDiseaseScore +
                ", gene=" + gene +
                ", mousePhenotypes=" + mousePhenotypes +
                ", humanPhenotypes=" + humanPhenotypes +
                ", mouseModel=" + mouseModel +
                '}';
    }
}

