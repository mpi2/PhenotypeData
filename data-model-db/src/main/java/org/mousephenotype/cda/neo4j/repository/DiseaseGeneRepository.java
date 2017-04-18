package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.DiseaseGene;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
public interface DiseaseGeneRepository extends Neo4jRepository<DiseaseGene, Long> {

    DiseaseGene findByDiseaseId(String diseaseId);
    DiseaseGene findByDiseaseTerm(String diseaseTerm);

}
