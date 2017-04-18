package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.HumanGeneSymbol;
import org.mousephenotype.cda.neo4j.entity.Pipeline;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
 public interface PipelineRepository extends GraphRepository<Pipeline> {

    Pipeline findByPipelineStableId(String pipelineStableId);



}
