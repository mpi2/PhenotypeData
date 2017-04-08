package org.mousephenotype.cda.neo4j.repository;

import org.mousephenotype.cda.neo4j.entity.DiseaseModel;
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
public interface DiseaseModelRepository extends Neo4jRepository<DiseaseModel, Long> {

    @Depth(2)
    DiseaseModel findByDiseaseId(String diseaseId);
    DiseaseModel findByDiseaseTerm(String diseaseTerm);

    @Depth(3)
    List<DiseaseModel> findByAlleleAlleleSymbol(String alleleSymbol);

//    @Depth(2)
//    List<DiseaseModel> fiindByGeneMarkerSymbol(String markerSymbol);

    @Query("MATCH (dm:DiseaseModel)--(a:Allele)--(g:Gene) WHERE g.markerSymbol = {markerSymbol} return dm")
    List<DiseaseModel> findByAllele_Gene_MarkerSymbol(@Param("markerSymbol") String markerSymbol);

//    @Query("MATCH (d:DiseaseModel)-[r:GENE]->(g:Gene) WHERE g.markerSymbol={markerSymbol} RETURN d, g")
//    List<DiseaseModelGeneResult> findDiseasModelsByMarkerSymbol(@Param("markerSymbol") String markerSymbol);

}
