package uk.ac.ebi.phenotype.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ckchen on 17/03/2017.
 */
@Repository
 public interface EnsemblGeneIdRepository extends GraphRepository<EnsemblGeneId> {

    EnsemblGeneId findByEnsemblGeneId(String ensemblGeneId);


}
