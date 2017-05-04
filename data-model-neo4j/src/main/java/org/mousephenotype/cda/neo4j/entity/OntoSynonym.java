package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class OntoSynonym {

    @GraphId
    Long id;

    private String ontoSynonym;

    @Relationship(type="MOUSE_PHENOTYPE", direction=Relationship.OUTGOING)
    private Mp mousePhenotype;

    @Relationship(type="HUMAN_PHENOTYPE", direction=Relationship.OUTGOING)
    private Hp humanPhenotype;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOntoSynonym() {
        return ontoSynonym;
    }

    public void setOntoSynonym(String ontoSynonym) {
        this.ontoSynonym = ontoSynonym;
    }

    public Mp getMousePhenotype() {
        return mousePhenotype;
    }

    public void setMousePhenotype(Mp mousePhenotype) {
        this.mousePhenotype = mousePhenotype;
    }

    public Hp getHumanPhenotype() {
        return humanPhenotype;
    }

    public void setHumanPhenotype(Hp humanPhenotype) {
        this.humanPhenotype = humanPhenotype;
    }


    @Override
    public String toString() {
        return "OntoSynonym{" +
                "id=" + id +
                ", ontoSynonym='" + ontoSynonym + '\'' +
                ", mousePhenotype=" + mousePhenotype +
                ", humanPhenotype=" + humanPhenotype +
                '}';
    }
}

