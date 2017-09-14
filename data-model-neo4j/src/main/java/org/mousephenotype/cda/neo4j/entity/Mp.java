package org.mousephenotype.cda.neo4j.entity;

/**
 * Created by ckchen on 28/03/2017.
 */

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.Set;

@NodeEntity
public class Mp {

    @GraphId
    Long id;

    private String mpId;
    private String mpTerm;
    private String mpDefinition;
    private Boolean topLevelStatus;
    private List<String> topLevelMpIds;
    private List<String> mpSynonyms;

    private List<String> topLevelMpTerms;
    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Set<Gene> genes;

    @Relationship(type="HUMAN", direction=Relationship.OUTGOING)
    private Set<Hp> humanPhenotypes;

//    @Relationship(type="MP_SYNONYM", direction=Relationship.OUTGOING)
//    private Set<OntoSynonym> ontoSynonyms;

    @Relationship(type="MP_NARROW_SYNONYM", direction=Relationship.OUTGOING)
    private Set<OntoSynonym> mpNarrowSynonyms;

    @Relationship(type="PARENT", direction=Relationship.OUTGOING)
    private Set<Mp> mpParentIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMpId() {
        return mpId;
    }

    public void setMpId(String mpId) {
        this.mpId = mpId;
    }

    public String getMpTerm() {
        return mpTerm;
    }

    public void setMpTerm(String mpTerm) {
        this.mpTerm = mpTerm;
    }

    public String getMpDefinition() {
        return mpDefinition;
    }

    public void setMpDefinition(String mpDefinition) {
        this.mpDefinition = mpDefinition;
    }

    public Boolean getTopLevelStatus() {
        return topLevelStatus;
    }

    public void setTopLevelStatus(Boolean topLevelStatus) {
        this.topLevelStatus = topLevelStatus;
    }

    public Set<Gene> getGenes() {
        return genes;
    }

    public void setGenes(Set<Gene> genes) {
        this.genes = genes;
    }

    public Set<Hp> getHumanPhenotypes() {
        return humanPhenotypes;
    }

    public void setHumanPhenotypes(Set<Hp> humanPhenotypes) {
        this.humanPhenotypes = humanPhenotypes;
    }

//    public Set<OntoSynonym> getOntoSynonyms() {
//        return ontoSynonyms;
//    }
//
//    public void setOntoSynonyms(Set<OntoSynonym> ontoSynonyms) {
//        this.ontoSynonyms = ontoSynonyms;
//    }

    public Set<OntoSynonym> getMpNarrowSynonyms() {
        return mpNarrowSynonyms;
    }

    public void setMpNarrowSynonyms(Set<OntoSynonym> mpNarrowSynonyms) {
        this.mpNarrowSynonyms = mpNarrowSynonyms;
    }

    public Set<Mp> getMpParentIds() {
        return mpParentIds;
    }

    public void setMpParentIds(Set<Mp> mpParentIds) {
        this.mpParentIds = mpParentIds;
    }

    public List<String> getTopLevelMpIds() {
        return topLevelMpIds;
    }

    public void setTopLevelMpIds(List<String> topLevelMpIds) {
        this.topLevelMpIds = topLevelMpIds;
    }

    public List<String> getTopLevelMpTerms() {
        return topLevelMpTerms;
    }

    public void setTopLevelMpTerms(List<String> topLevelMpTerms) {
        this.topLevelMpTerms = topLevelMpTerms;
    }

    public List<String> getMpSynonyms() {
        return mpSynonyms;
    }

    public void setMpSynonyms(List<String> mpSynonyms) {
        this.mpSynonyms = mpSynonyms;
    }

    @Override
    public String toString() {
        return "Mp{" +
                "id=" + id +
                ", mpId='" + mpId + '\'' +
                ", mpTerm='" + mpTerm + '\'' +
                ", mpDefinition='" + mpDefinition + '\'' +
                ", topLevelStatus=" + topLevelStatus +
                ", topLevelMpIds=" + topLevelMpIds +
                ", mpSynonyms=" + mpSynonyms +
                ", topLevelMpTerms=" + topLevelMpTerms +
                ", genes=" + genes +
                ", humanPhenotypes=" + humanPhenotypes +
                ", mpNarrowSynonyms=" + mpNarrowSynonyms +
                ", mpParentIds=" + mpParentIds +
                '}';
    }
}


