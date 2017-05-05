package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class HumanGeneSymbol {

    @GraphId
    Long id;

    private String humanGeneSymbol;

    // one human symbol can be associated with multiple mouse symbols
    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Set<Gene> genes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHumanGeneSymbol() {
        return humanGeneSymbol;
    }

    public void setHumanGeneSymbol(String humanGeneSymbol) {
        this.humanGeneSymbol = humanGeneSymbol;
    }

    public Set<Gene> getGenes() {
        return genes;
    }

    public void setGenes(Set<Gene> genes) {
        this.genes = genes;
    }

    @Override
    public String toString() {
        return "HumanGeneSymbol{" +
                "id=" + id +
                ", humanGeneSymbol='" + humanGeneSymbol + '\'' +
                ", genes=" + genes +
                '}';
    }
}

