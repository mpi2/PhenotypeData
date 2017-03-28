package uk.ac.ebi.phenotype.repository;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class HumanGeneSymbol {

    @GraphId
    Long id;

    private String humanGeneSymbol;

    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Gene gene;

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

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    @Override
    public String toString() {
        return "HumanGeneSymbol{" +
                "id=" + id +
                ", humanGeneSymbol='" + humanGeneSymbol + '\'' +
                ", gene=" + gene +
                '}';
    }
}

