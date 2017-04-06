package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.Gene;
import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
 public interface GeneRepository extends Neo4jRepository<Gene, Long> {

    Gene findByMgiAccessionId(String mgiAccessionId);
    Gene findByMarkerSymbol(String markerSymbol);

    // diseaseModels know about MouseModel and Allele
    @Depth(2)
    @Query("match (g:Gene) where g.markerSymbol={markerSymbol} with g, "
//        "[(g)<-[:GENE]-(mm:MouseModel) | mm] as mouseModel, "
        + "[(g)<-[:GENE]-(mm:MouseModel)<-[:MOUSE_MODEL]-(dm:DiseaseModel) | dm] as diseaseModel "
//        + "[(g)<-[:GENE]-(mm:MouseModel)<-[:MOUSE_MODEL]-(dm:DiseaseModel)-[:ALLELE]->(a:Allele) | a] as allele "
        + "return g, diseaseModel")
    List<Object> findDiseaseModelByMarkerSymbol(@Param( "markerSymbol" ) String markerSymbol);

//
//    Gene findByMarkerSymbolAndDiseaseName(String symbol, String ensemblGeneId);
//
//    @Query("CYPHER asdfasdf afes adg ?1  ?2 ")
//    Gene findByMarkerSymbolAndDiseaseName(String symbol, String ensemblGeneId);


}
