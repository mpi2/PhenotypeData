package uk.ac.ebi.phenotype.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
public interface MouseModelRepository extends Neo4jRepository<MouseModel, Long> {

    MouseModel findByModelId(int modelId);


}
