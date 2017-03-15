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

    private String humanGeneSymbl;

    @Relationship(type="OF_GENE", direction=Relationship.OUTGOING)
    private Gene gene;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHumanGeneSymbl() {
        return humanGeneSymbl;
    }

    public void setHumanGeneSymbl(String humanGeneSymbl) {
        this.humanGeneSymbl = humanGeneSymbl;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }
}

