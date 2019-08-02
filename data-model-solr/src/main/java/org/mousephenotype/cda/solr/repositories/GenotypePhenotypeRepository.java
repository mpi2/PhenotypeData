package org.mousephenotype.cda.solr.repositories;

import org.mousephenotype.cda.solr.service.dto.GenotypePhenotypeDTO;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

/**
 * Support accessing the solr index using spring data
 */
public interface GenotypePhenotypeRepository extends SolrCrudRepository<GenotypePhenotypeDTO, String> {

	List<GenotypePhenotypeDTO> findByParameterStableId(String stableId);
	List<GenotypePhenotypeDTO> findByProcedureStableId(String stableId);
}