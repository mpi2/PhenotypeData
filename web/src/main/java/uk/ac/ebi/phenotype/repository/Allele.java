package uk.ac.ebi.phenotype.repository;

import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Allele {

    @GraphId
    Long id;

    private String alleleMgiAccessionId;
    private String alleleSymbol;
    private String alleleDescription;
    private String mutationType;
    private String esCellStatus;
    private String mouseStatus;
    private String phenotypeStatus;

    @Relationship(type="OF_GENE", direction=Relationship.OUTGOING)
    private Gene gene;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAlleleMgiAccessionId() {
        return alleleMgiAccessionId;
    }

    public void setAlleleMgiAccessionId(String alleleMgiAccessionId) {
        this.alleleMgiAccessionId = alleleMgiAccessionId;
    }

    public String getAlleleSymbol() {
        return alleleSymbol;
    }

    public void setAlleleSymbol(String alleleSymbol) {
        this.alleleSymbol = alleleSymbol;
    }

    public String getAlleleDescription() {
        return alleleDescription;
    }

    public void setAlleleDescription(String alleleDescription) {
        this.alleleDescription = alleleDescription;
    }

    public String getMutationType() {
        return mutationType;
    }

    public void setMutationType(String mutationType) {
        this.mutationType = mutationType;
    }

    public String getEsCellStatus() {
        return esCellStatus;
    }

    public void setEsCellStatus(String esCellStatus) {
        this.esCellStatus = esCellStatus;
    }

    public String getMouseStatus() {
        return mouseStatus;
    }

    public void setMouseStatus(String mouseStatus) {
        this.mouseStatus = mouseStatus;
    }

    public String getPhenotypeStatus() {
        return phenotypeStatus;
    }

    public void setPhenotypeStatus(String phenotypeStatus) {
        this.phenotypeStatus = phenotypeStatus;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    @Override
    public String toString() {
        return "Allele{" +
                "id=" + id +
                ", alleleMgiAccessionId='" + alleleMgiAccessionId + '\'' +
                ", alleleSymbol='" + alleleSymbol + '\'' +
                ", alleleDescription='" + alleleDescription + '\'' +
                ", mutationType='" + mutationType + '\'' +
                ", esCellStatus='" + esCellStatus + '\'' +
                ", mouseStatus='" + mouseStatus + '\'' +
                ", phenotypeStatus='" + phenotypeStatus + '\'' +
                ", gene=" + gene +
                '}';
    }
}
