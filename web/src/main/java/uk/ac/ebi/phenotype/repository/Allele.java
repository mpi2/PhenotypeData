package uk.ac.ebi.phenotype.repository;

import org.mousephenotype.cda.solr.service.dto.Allele2DTO;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class Allele {

    @GraphId
    Long id;

    private String alleleMgiAccessionId;
    private String alleleName;
    private String alleleDescription;
    private String alleleType;
    private String mutationType;
    private String esCellAvailable;
    private String esCellStatus;
    private String mouseAvailable;
    private String mouseStatus;
    private String phenotypeStatus;
    private Set<String> getPhenotypingCentres;

    @Relationship(type="OF_GENE", direction=Relationship.OUTGOING)
    private Gene gene;





}
