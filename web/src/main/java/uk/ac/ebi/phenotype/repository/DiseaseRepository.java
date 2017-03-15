package uk.ac.ebi.phenotype.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by ckchen on 14/03/2017.
 */
@Repository
public interface DiseaseRepository extends CrudRepository<DiseaseModelAssociation, Long> {

    Gene findByDiseaseId(String diseaseId);
    Gene findByDiseaseTerm(String diseaseTerm);

}
