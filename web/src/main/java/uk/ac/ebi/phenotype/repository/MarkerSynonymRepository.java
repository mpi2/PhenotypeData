package uk.ac.ebi.phenotype.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
 public interface MarkerSynonymRepository extends GraphRepository<MarkerSynonym> {

    MarkerSynonym findByMarkerSynonym(String markerSynonym);

}
