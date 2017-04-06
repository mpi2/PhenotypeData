package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.Mp;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
public interface MpRepository extends Neo4jRepository<Mp, Long> {

    Mp findByMpId(String mpId);
    Mp findByMpTerm (String mpTerm);




}
