package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class MarkerSynonym {

    @GraphId
    Long id;

    private String markerSynonym;

    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Gene gene;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarkerSynonym() {
        return markerSynonym;
    }

    public void setMarkerSynonym(String markerSynonym) {
        this.markerSynonym = markerSynonym;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    @Override
    public String toString() {
        return "MarkerSynonym{" +
                "id=" + id +
                ", markerSynonym='" + markerSynonym + '\'' +
                ", gene=" + gene +
                '}';
    }
}

