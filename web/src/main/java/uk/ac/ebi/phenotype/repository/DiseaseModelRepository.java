package uk.ac.ebi.phenotype.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
public interface DiseaseModelRepository extends Neo4jRepository<DiseaseModel, Long> {

    DiseaseModel findByDiseaseId(String diseaseId);
    DiseaseModel findByDiseaseTerm(String diseaseTerm);

    List<DiseaseModel> findByAlleleGeneMarkerSymbol(String markerSymbol);

}
