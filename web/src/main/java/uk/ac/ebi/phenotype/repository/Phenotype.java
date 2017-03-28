package uk.ac.ebi.phenotype.repository;

import org.mousephenotype.cda.solr.service.dto.MpDTO;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Phenotype {

    @GraphId
    Long id;

    private String mpId;
    private String mpTerm;
    private String mpDefinition;
    private List<String> mpTermSynonym;

    private List<String> topLevelMpId;
    private List<String> topLevelMpTerm;
    private List<String> topLevelMpDefinition;
    private List<String> topLevelMpTermSynonym;

    private List<String> intermediateMpId;
    private List<String> intermediateMpTerm;
    private List<String> intermediateMpDefinition;
    private List<String> intermediateMpTermSynonym;

    private List<String> parentMpId;
    private List<String> parentMpTerm;
    private List<String> parentMpDefinition;
    private List<String> parentMpTermSynonym;

    private List<String> childMpId;
    private List<String> childMpTerm;
    private List<String> childMpTermSynonym;
    private List<String> childMpDefinition;


    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Set<Gene> genes;

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

    public List<String> getMpTermSynonym() {
        return mpTermSynonym;
    }

    public void setMpTermSynonym(List<String> mpTermSynonym) {
        this.mpTermSynonym = mpTermSynonym;
    }

    public List<String> getTopLevelMpId() {
        return topLevelMpId;
    }

    public void setTopLevelMpId(List<String> topLevelMpId) {
        this.topLevelMpId = topLevelMpId;
    }

    public List<String> getTopLevelMpTerm() {
        return topLevelMpTerm;
    }

    public void setTopLevelMpTerm(List<String> topLevelMpTerm) {
        this.topLevelMpTerm = topLevelMpTerm;
    }

    public List<String> getTopLevelMpDefinition() {
        return topLevelMpDefinition;
    }

    public void setTopLevelMpDefinition(List<String> topLevelMpDefinition) {
        this.topLevelMpDefinition = topLevelMpDefinition;
    }

    public List<String> getTopLevelMpTermSynonym() {
        return topLevelMpTermSynonym;
    }

    public void setTopLevelMpTermSynonym(List<String> topLevelMpTermSynonym) {
        this.topLevelMpTermSynonym = topLevelMpTermSynonym;
    }

    public List<String> getIntermediateMpId() {
        return intermediateMpId;
    }

    public void setIntermediateMpId(List<String> intermediateMpId) {
        this.intermediateMpId = intermediateMpId;
    }

    public List<String> getIntermediateMpTerm() {
        return intermediateMpTerm;
    }

    public void setIntermediateMpTerm(List<String> intermediateMpTerm) {
        this.intermediateMpTerm = intermediateMpTerm;
    }

    public List<String> getIntermediateMpDefinition() {
        return intermediateMpDefinition;
    }

    public void setIntermediateMpDefinition(List<String> intermediateMpDefinition) {
        this.intermediateMpDefinition = intermediateMpDefinition;
    }

    public List<String> getIntermediateMpTermSynonym() {
        return intermediateMpTermSynonym;
    }

    public void setIntermediateMpTermSynonym(List<String> intermediateMpTermSynonym) {
        this.intermediateMpTermSynonym = intermediateMpTermSynonym;
    }

    public List<String> getParentMpId() {
        return parentMpId;
    }

    public void setParentMpId(List<String> parentMpId) {
        this.parentMpId = parentMpId;
    }

    public List<String> getParentMpTerm() {
        return parentMpTerm;
    }

    public void setParentMpTerm(List<String> parentMpTerm) {
        this.parentMpTerm = parentMpTerm;
    }

    public List<String> getParentMpDefinition() {
        return parentMpDefinition;
    }

    public void setParentMpDefinition(List<String> parentMpDefinition) {
        this.parentMpDefinition = parentMpDefinition;
    }

    public List<String> getParentMpTermSynonym() {
        return parentMpTermSynonym;
    }

    public void setParentMpTermSynonym(List<String> parentMpTermSynonym) {
        this.parentMpTermSynonym = parentMpTermSynonym;
    }

    public List<String> getChildMpId() {
        return childMpId;
    }

    public void setChildMpId(List<String> childMpId) {
        this.childMpId = childMpId;
    }

    public List<String> getChildMpTerm() {
        return childMpTerm;
    }

    public void setChildMpTerm(List<String> childMpTerm) {
        this.childMpTerm = childMpTerm;
    }

    public List<String> getChildMpTermSynonym() {
        return childMpTermSynonym;
    }

    public void setChildMpTermSynonym(List<String> childMpTermSynonym) {
        this.childMpTermSynonym = childMpTermSynonym;
    }

    public List<String> getChildMpDefinition() {
        return childMpDefinition;
    }

    public void setChildMpDefinition(List<String> childMpDefinition) {
        this.childMpDefinition = childMpDefinition;
    }

    public Set<Gene> getGenes() {
        return genes;
    }

    public void setGenes(Set<Gene> genes) {
        this.genes = genes;
    }
}

