package uk.ac.ebi.phenotype.repository;

import org.mousephenotype.cda.solr.service.dto.PhenodigmDTO;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.List;
import java.util.Set;

/**
 * Created by ckchen on 14/03/2017.
 */
@NodeEntity
public class DiseaseModelAssociation {

    @GraphId
    Long id;

    private String diseaseId;
    private List<String> diseaseClasses;
    private String diseaseTerm;
    private List<String> diseaseAlts;
    private String markerAccession;
    private Boolean impcPredicted;
    private Boolean mgiPredicted;
    private Double diseaseToModelScore;
    private Double modelToDiseaseScore;


//            n.createRelationshipTo(gnode, RelationshipType.withName("OF_GENE"));
//            gnode.createRelationshipTo(n, RelationshipType.withName("HAS_DISEASE"));
//
//                n.createRelationshipTo(pnode, RelationshipType.withName("HAS_HP"));
//                pnode.createRelationshipTo(n, RelationshipType.withName("HAS_DISEASE"));
//                Node pnode = phenotypeIdNode.get(mpId);
//                n.createRelationshipTo(pnode, RelationshipType.withName("HAS_PHENOTYPE"));
//                pnode.createRelationshipTo(n, RelationshipType.withName("HAS_DISEASE"));
//            n.createRelationshipTo(modelIdNode.get(doc.getModelID()), RelationshipType.withName("HAS_MOUSE_MODEL"));

    @Relationship(type="OF_GENE", direction=Relationship.OUTGOING)
    private Set<Gene> genes;



}

