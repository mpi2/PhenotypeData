package uk.ac.ebi.phenotype.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
 public interface GeneRepository extends Neo4jRepository<Gene, Long> {

    Gene findByMgiAccessionId(String mgiAccessionId);
    Gene findByMarkerSymbol(String markerSymbol);
    Gene findByMarkerSynonym(String markerSynonym);

//
//    Gene findByMarkerSymbolAndDiseaseName(String symbol, String ensemblGeneId);
//
//    @Query("CYPHER asdfasdf afes adg ?1  ?2 ")
//    Gene findByMarkerSymbolAndDiseaseName(String symbol, String ensemblGeneId);


}
