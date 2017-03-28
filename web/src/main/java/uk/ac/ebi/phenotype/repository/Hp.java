package uk.ac.ebi.phenotype.repository;

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


    @Relationship(type="GENE", direction=Relationship.OUTGOING)
    private Set<Gene> genes;

    @Relationship(type="DISEASE", direction=Relationship.OUTGOING)
    private Set<Disease> diseases;





}
