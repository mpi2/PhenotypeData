package org.mousephenotype.cda.solr.repositories.parameter;

import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

interface ParameterRepository extends SolrCrudRepository<Parameter, String> {

	List<Parameter> findByStableId(String stableId);

}
