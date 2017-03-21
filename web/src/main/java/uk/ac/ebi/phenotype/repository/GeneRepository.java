package uk.ac.ebi.phenotype.repository;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
//public interface GeneRepository extends CrudRepository<Gene, Long> {
 public interface GeneRepository extends GraphRepository<Gene> {

    Gene findByMgiAccessionId(String mgiAccessionId);
    Gene findByMarkerSymbol(String markerSymbol);

//
//    Gene findByMarkerSymbolAndDiseaseName(String symbol, String ensemblGeneId);
//
//    @Query("CYPHER asdfasdf afes adg ?1  ?2 ")
//    Gene findByMarkerSymbolAndDiseaseName(String symbol, String ensemblGeneId);


}
