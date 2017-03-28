package uk.ac.ebi.phenotype.repository;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Hp {

    @GraphId
    Long id;

    private String hpId;
    private String hpTerm;
    private String hpDefinition;

    @Relationship(type="HP_SYNONYM", direction=Relationship.OUTGOING)
    private Set<OntoSynonym> hpSynonyms;

    @Relationship(type="MOUSE", direction=Relationship.OUTGOING)
    private Set<Mp> mousePhenotypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHpId() {
        return hpId;
    }

    public void setHpId(String hpId) {
        this.hpId = hpId;
    }

    public String getHpTerm() {
        return hpTerm;
    }

    public void setHpTerm(String hpTerm) {
        this.hpTerm = hpTerm;
    }

    public String getHpDefinition() {
        return hpDefinition;
    }

    public void setHpDefinition(String hpDefinition) {
        this.hpDefinition = hpDefinition;
    }

    public Set<OntoSynonym> getHpSynonyms() {
        return hpSynonyms;
    }

    public void setHpSynonyms(Set<OntoSynonym> hpSynonyms) {
        this.hpSynonyms = hpSynonyms;
    }

    public Set<Mp> getMousePhenotypes() {
        return mousePhenotypes;
    }

    public void setMousePhenotypes(Set<Mp> mousePhenotypes) {
        this.mousePhenotypes = mousePhenotypes;
    }

    @Override
    public String toString() {
        return "Hp{" +
                "id=" + id +
                ", hpId='" + hpId + '\'' +
                ", hpTerm='" + hpTerm + '\'' +
                ", hpDefinition='" + hpDefinition + '\'' +
                ", hpSynonyms=" + hpSynonyms +
                ", mousePhenotypes=" + mousePhenotypes +
                '}';
    }
}

