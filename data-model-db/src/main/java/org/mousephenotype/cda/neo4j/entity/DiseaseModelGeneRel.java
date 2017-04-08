package org.mousephenotype.cda.neo4j.entity;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

/**
 * Created by ckchen on 07/04/2017.
 */

@RelationshipEntity(type="GENE")
public class DiseaseModelGeneRel {
    @GraphId
    private Long relationshipId;

    @StartNode
    private DiseaseModel diseaseModel;

    @EndNode
    private Gene gene;
}
