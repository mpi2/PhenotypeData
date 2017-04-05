package uk.ac.ebi.phenotype.repository;

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

    @Query("MATCH (dm:DiseaseModel)--(a:Allele)--(g:Gene) WHERE g.markerSymbol = {markerSymbol} return dm")
    List<DiseaseModel> findByAlleleGeneMarkerSymbol(@Param("markerSymbol") String markerSymbol);

}
