package uk.ac.ebi.phenotype.repository;

/**
 * Created by ckchen on 28/03/2017.
 */

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

@NodeEntity
public class Mp {

    @GraphId
    Long id;

    private String mpId;
    private String mpTerm;
    private String mpDefinition;
    private Boolean topLevelStatus;

    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Set<Gene> genes;

    @Relationship(type="HUMAN", direction=Relationship.OUTGOING)
    private Set<Hp> humanPhenotypes;

    @Relationship(type="MP_SYNONYM", direction=Relationship.OUTGOING)
    private Set<OntoSynonym> ontoSynonyms;

    @Relationship(type="MP_NARROW_SYNONYM", direction=Relationship.OUTGOING)
    private Set<OntoSynonym> mpNarrowSynonyms;

    @Relationship(type="MP_TOP_LEVEL_ID", direction=Relationship.OUTGOING)
    private Set<Mp> mpTopLevelIds;

    @Relationship(type="MP_INTERMEDIATE_ID", direction=Relationship.OUTGOING)
    private Set<Mp> mpIntermediateIds;

    @Relationship(type="MP_PARENT_ID", direction=Relationship.OUTGOING)
    private Set<Mp> mpParentIds;

    @Relationship(type="MP_CHILD_ID", direction=Relationship.OUTGOING)
    private Set<Mp> mpChildIds;

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

    public Set<OntoSynonym> getOntoSynonyms() {
        return ontoSynonyms;
    }

    public void setOntoSynonyms(Set<OntoSynonym> ontoSynonyms) {
        this.ontoSynonyms = ontoSynonyms;
    }

    public Set<Mp> getMpTopLevelIds() {
        return mpTopLevelIds;
    }

    public void setMpTopLevelIds(Set<Mp> mpTopLevelIds) {
        this.mpTopLevelIds = mpTopLevelIds;
    }

    public Set<Mp> getMpIntermediateIds() {
        return mpIntermediateIds;
    }

    public void setMpIntermediateIds(Set<Mp> mpIntermediateIds) {
        this.mpIntermediateIds = mpIntermediateIds;
    }

    public Set<Mp> getMpParentIds() {
        return mpParentIds;
    }

    public void setMpParentIds(Set<Mp> mpParentIds) {
        this.mpParentIds = mpParentIds;
    }

    public Set<Mp> getMpChildIds() {
        return mpChildIds;
    }

    public void setMpChildIds(Set<Mp> mpChildIds) {
        this.mpChildIds = mpChildIds;
    }

    public Set<OntoSynonym> getMpNarrowSynonyms() {
        return mpNarrowSynonyms;
    }

    public void setMpNarrowSynonyms(Set<OntoSynonym> mpNarrowSynonyms) {
        this.mpNarrowSynonyms = mpNarrowSynonyms;
    }

    @Override
    public String toString() {
        return "Mp{" +
                "id=" + id +
                ", mpId='" + mpId + '\'' +
                ", mpTerm='" + mpTerm + '\'' +
                ", mpDefinition='" + mpDefinition + '\'' +
                ", topLevelStatus=" + topLevelStatus +
                ", genes=" + genes +
                ", humanPhenotypes=" + humanPhenotypes +
                ", ontoSynonyms=" + ontoSynonyms +
                ", mpNarrowSynonyms=" + mpNarrowSynonyms +
                ", mpTopLevelIds=" + mpTopLevelIds +
                ", mpIntermediateIds=" + mpIntermediateIds +
                ", mpParentIds=" + mpParentIds +
                ", mpChildIds=" + mpChildIds +
                '}';
    }
}


