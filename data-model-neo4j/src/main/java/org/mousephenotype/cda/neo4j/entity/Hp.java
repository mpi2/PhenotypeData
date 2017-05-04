package org.mousephenotype.cda.neo4j.entity;

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
    private Boolean topLevelStatus;

    @Relationship(type="HP_SYNONYM", direction=Relationship.OUTGOING)
    private Set<OntoSynonym> ontoSynonyms;

    @Relationship(type="MOUSE", direction=Relationship.OUTGOING)
    private Set<Mp> mousePhenotypes;

    @Relationship(type="PARENT", direction=Relationship.OUTGOING)
    private Set<Hp> hpParentIds;

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

    public Boolean getTopLevelStatus() {
        return topLevelStatus;
    }

    public void setTopLevelStatus(Boolean topLevelStatus) {
        this.topLevelStatus = topLevelStatus;
    }

    public Set<OntoSynonym> getOntoSynonyms() {
        return ontoSynonyms;
    }

    public void setOntoSynonyms(Set<OntoSynonym> ontoSynonyms) {
        this.ontoSynonyms = ontoSynonyms;
    }

    public Set<Mp> getMousePhenotypes() {
        return mousePhenotypes;
    }

    public void setMousePhenotypes(Set<Mp> mousePhenotypes) {
        this.mousePhenotypes = mousePhenotypes;
    }

    public Set<Hp> getHpParentIds() {
        return hpParentIds;
    }

    public void setHpParentIds(Set<Hp> hpParentIds) {
        this.hpParentIds = hpParentIds;
    }

    @Override
    public String toString() {
        return "Hp{" +
                "id=" + id +
                ", hpId='" + hpId + '\'' +
                ", hpTerm='" + hpTerm + '\'' +
                ", hpDefinition='" + hpDefinition + '\'' +
                ", topLevelStatus=" + topLevelStatus +
                ", ontoSynonyms=" + ontoSynonyms +
                ", mousePhenotypes=" + mousePhenotypes +
                ", hpParentIds=" + hpParentIds +
                '}';
    }
}

