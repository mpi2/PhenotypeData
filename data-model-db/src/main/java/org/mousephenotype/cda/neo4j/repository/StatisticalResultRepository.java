package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.Pipeline;
import org.mousephenotype.cda.neo4j.entity.StatisticalResult;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
 public interface StatisticalResultRepository extends GraphRepository<StatisticalResult> {

//    StatisticalResult findByPipelineStableId(String StatisticalResult);



}
