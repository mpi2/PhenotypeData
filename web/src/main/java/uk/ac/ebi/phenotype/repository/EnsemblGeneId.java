package uk.ac.ebi.phenotype.repository;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class EnsemblGeneId {

    @GraphId
    Long id;

    private String ensemblGeneId;

    @Relationship(type="OF_GENE", direction=Relationship.OUTGOING)
    private Gene gene;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnsemblGeneId() {
        return ensemblGeneId;
    }

    public void setEnsemblGeneId(String ensemblGeneId) {
        this.ensemblGeneId = ensemblGeneId;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }


    @Override
    public String toString() {
        return "EnsemblGeneId{" +
                "id=" + id +
                ", ensemblGeneId='" + ensemblGeneId + '\'' +
                ", gene=" + gene +
                '}';
    }
}
