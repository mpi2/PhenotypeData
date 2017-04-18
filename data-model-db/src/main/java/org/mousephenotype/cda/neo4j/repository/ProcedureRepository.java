package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.Pipeline;
import org.mousephenotype.cda.neo4j.entity.Procedure;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
 public interface ProcedureRepository extends GraphRepository<Procedure> {

    Procedure findByProcedureStableId(String procedureStableId);



}
