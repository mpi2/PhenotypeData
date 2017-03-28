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
    private String hpDefinition;

    @Relationship(type="MOUSE", direction=Relationship.OUTGOING)
    private Set<Mp> mousePhenotypes;

    @Relationship(type="HP_SYNONYM", direction=Relationship.OUTGOING)
    private Set<OntoSynonym> hpSynonyms;


}

